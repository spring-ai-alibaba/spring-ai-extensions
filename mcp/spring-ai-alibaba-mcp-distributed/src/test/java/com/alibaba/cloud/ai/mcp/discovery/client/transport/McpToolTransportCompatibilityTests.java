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

package com.alibaba.cloud.ai.mcp.discovery.client.transport;

import java.time.Duration;
import java.util.List;

import com.alibaba.cloud.ai.mcp.common.transport.builder.WebFluxSseClientTransportBuilder;
import com.alibaba.cloud.ai.mcp.common.transport.builder.WebFluxStreamableClientTransportBuilder;
import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.json.jackson3.JacksonMcpJsonMapper;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.spec.McpSchema;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.ToolResponseMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.mcp.McpToolUtils;
import org.springframework.ai.mcp.SyncMcpToolCallback;
import org.springframework.ai.mcp.server.webflux.transport.WebFluxSseServerTransportProvider;
import org.springframework.ai.mcp.server.webflux.transport.WebFluxStreamableServerTransportProvider;
import org.springframework.ai.model.tool.ToolCallingChatOptions;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.tool.function.FunctionToolCallback;
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.RouterFunctions;
import reactor.netty.http.server.HttpServer;
import tools.jackson.databind.json.JsonMapper;

import static org.assertj.core.api.Assertions.assertThat;

class McpToolTransportCompatibilityTests {

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void discoversAndExecutesAnExplicitlyAttachedToolOverLoopback(boolean streamable) {
        var mapper = new JacksonMcpJsonMapper(JsonMapper.shared());
        var tool = FunctionToolCallback.builder("greet", (Greeting input) -> "Hello " + input.name())
            .description("Greet the caller").inputType(Greeting.class).build();
        var toolSpec = McpToolUtils.toSyncToolSpecification(tool);
        var serverInfo = McpSchema.Implementation.builder("test-server", "1.0.0").build();

        var streamTransport = streamable
            ? WebFluxStreamableServerTransportProvider.builder().jsonMapper(mapper).messageEndpoint("/mcp").build()
            : null;
        var sseTransport = streamable ? null : WebFluxSseServerTransportProvider.builder().jsonMapper(mapper)
            .sseEndpoint("/sse").messageEndpoint("/message").build();
        var server = streamTransport != null
            ? McpServer.sync(streamTransport).serverInfo(serverInfo).tools(toolSpec).build()
            : McpServer.sync(sseTransport).serverInfo(serverInfo).tools(toolSpec).build();
        var router = streamTransport != null ? streamTransport.getRouterFunction() : sseTransport.getRouterFunction();
        var http = HttpServer.create().host("127.0.0.1").port(0)
            .handle(new ReactorHttpHandlerAdapter(RouterFunctions.toHttpHandler(router)))
            .bindNow(Duration.ofSeconds(5));

        try {
            var web = WebClient.builder().baseUrl("http://127.0.0.1:" + http.port());
            var transport = streamable ? WebFluxStreamableClientTransportBuilder.build(web, mapper, "/mcp")
                : WebFluxSseClientTransportBuilder.build(web, mapper, "/sse");
			var client = McpClient.sync(transport).requestTimeout(Duration.ofSeconds(5)).build();
			try {
                client.initialize();
				var tools = client.listTools().tools();
				assertThat(tools).hasSize(1);
				var callback = new SyncMcpToolCallback(client, tools.get(0));
                var prompt = new Prompt("Greet", ToolCallingChatOptions.builder().toolCallbacks(callback).build());
                var call = new AssistantMessage.ToolCall("greet-1", "function", callback.getToolDefinition().name(),
                    "{\"name\":\"Spring AI\"}");
                var response = new ChatResponse(List.of(new Generation(AssistantMessage.builder()
                    .toolCalls(List.of(call)).build())));

                var result = ToolCallingManager.builder().build().executeToolCalls(prompt, response);

                var toolResponse = (ToolResponseMessage) result.conversationHistory().get(2);
                assertThat(toolResponse.getResponses()).singleElement()
                    .satisfies(reply -> assertThat(reply.responseData()).contains("Hello Spring AI"));
            }
			finally {
				client.closeGracefully();
			}
        }
        finally {
			try {
				server.closeGracefully();
			}
			finally {
				http.disposeNow(Duration.ofSeconds(5));
			}
        }
    }

    record Greeting(String name) {
    }

}
