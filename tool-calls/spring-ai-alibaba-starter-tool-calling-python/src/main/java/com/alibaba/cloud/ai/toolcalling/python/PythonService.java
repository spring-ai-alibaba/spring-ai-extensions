/*
 * Copyright 2024-2026 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.cloud.ai.toolcalling.python;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Engine;
import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.function.FunctionToolCallback;

import java.util.function.BiFunction;

/**
 * Tool for executing Python code using GraalVM polyglot.
 *
 * This tool allows the agent to execute Python code snippets and get results.
 * It uses GraalVM's polyglot API to run Python code in a sandboxed environment.
 */
public class PythonService implements BiFunction<PythonService.Request, ToolContext, PythonService.Response> {

    private static final Logger log = LoggerFactory.getLogger(PythonService.class);

    private final PythonProperties properties;

    private final Engine engine;

    private final Context context;

    public PythonService(PythonProperties properties) {
        this.properties = properties;
        this.engine = Engine.newBuilder()
                .allowExperimentalOptions(properties.getEngine().isWarnInterpreterOnly())
                .build();
        this.context = Context.newBuilder("python")
                .engine(engine)
                .allowAllAccess(properties.getContext().isAllowAllAccess())
                .allowIO(properties.getContext().isAllowIO())
                .allowNativeAccess(properties.getContext().isAllowNativeAccess())
                .allowCreateProcess(properties.getContext().isAllowCreateProcess())
                .allowHostAccess(properties.getContext().isAllowHostAccess())
                .build();
    }

    /**
     * Constructor for backwards compatibility (used by static factory method).
     * Creates PythonService with default configuration.
     */
    public PythonService() {
        this(new PythonProperties());
    }

    @Override
    public Response apply(Request request, ToolContext toolContext) {
        if (request.code == null || request.code.trim().isEmpty()) {
            return new Response("Error: Python code cannot be empty");
        }
        try {
            log.debug("Executing Python code: {}", request.code);
            Value result = context.eval("python", request.code);
            String resultStr = convertResultToString(result);

            return new Response(resultStr);
        } catch (PolyglotException e) {
            log.error("Error executing Python code", e);
            return new Response("Error executing Python code: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error executing Python code", e);
            return new Response("Unexpected error: " + e.getMessage());
        }
    }

    /**
     * 将 Polyglot Value 转换为字符串
     */
    private String convertResultToString(Value result) {
        if (result.isNull()) {
            return "Execution completed with no return value";
        }

        if (result.isString()) {
            return result.asString();
        } else if (result.isNumber()) {
            return String.valueOf(result.as(Object.class));
        } else if (result.isBoolean()) {
            return String.valueOf(result.asBoolean());
        } else if (result.hasArrayElements()) {
            return convertArrayToString(result);
        } else {
            return result.toString();
        }
    }

    /**
     * 将数组/列表转换为字符串表示
     */
    private String convertArrayToString(Value result) {
        StringBuilder sb = new StringBuilder("[");
        long size = result.getArraySize();

        for (long i = 0; i < size; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            Value element = result.getArrayElement(i);
            sb.append(element.toString());
        }

        sb.append("]");
        return sb.toString();
    }

    /**
     * Create a ToolCallback for the Python tool.
     */
    public static ToolCallback createPythonToolCallback(String description) {
        return FunctionToolCallback.builder("python", new PythonService())
                .description(description)
                .inputType(Request.class)
                .build();
    }

    /**
     * Request structure for the Python tool.
     */
    @JsonClassDescription("Python Reuqest")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Request(
            @JsonProperty(required = true, value = "code")
            @JsonPropertyDescription("Code that can be executed by Python\n") String code) {

    }

    @JsonClassDescription("Python Result")
    public record Response(String result) {

    }
}

