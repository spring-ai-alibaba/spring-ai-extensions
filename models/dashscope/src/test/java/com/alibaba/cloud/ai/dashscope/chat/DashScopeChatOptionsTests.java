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
package com.alibaba.cloud.ai.dashscope.chat;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.alibaba.cloud.ai.dashscope.api.DashScopeResponseFormat;
import com.alibaba.cloud.ai.dashscope.spec.DashScopeApiSpec;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.ai.tool.ToolCallback;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test cases for DashScopeChatOptions
 *
 * @author yuluo
 * @author <a href="mailto:yuluo08290126@gmail.com">yuluo</a>
 * @author brianxiadong
 * @since 1.0.0-M5.1
 */
class DashScopeChatOptionsTests {

    private static final String TEST_MODEL = "qwen-turbo";

    private static final Double TEST_TEMPERATURE = 0.7;

    private static final Double TEST_TOP_P = 0.8;

    private static final Integer TEST_TOP_K = 50;

    private static final Integer TEST_SEED = 42;

    private static final Double TEST_REPETITION_PENALTY = 1.1;

    private static final Integer TEST_THINKING_BUDGET = 1000;

    private static final Map<String, Object> TEST_EXTRA_BODY = Map.of("customKey", "customValue");

    @Test
    void testBuilderAndGetters() {
        // Test building DashScopeChatOptions using builder pattern and verify getters
        DashScopeChatOptions options = DashScopeChatOptions.builder()
                .model(TEST_MODEL)
                .temperature(TEST_TEMPERATURE)
                .topP(TEST_TOP_P)
                .topK(TEST_TOP_K)
                .seed(TEST_SEED)
                .repetitionPenalty(TEST_REPETITION_PENALTY)
                .stream(true)
                .enableSearch(true)
                .incrementalOutput(true)
                .vlHighResolutionImages(true)
                .enableThinking(true)
                .thinkingBudget(TEST_THINKING_BUDGET)
                .multiModel(true)
                .extraBody(TEST_EXTRA_BODY)
                .build();

        // Verify all fields are set correctly
        assertThat(options.getModel()).isEqualTo(TEST_MODEL);
        assertThat(options.getTemperature()).isEqualTo(TEST_TEMPERATURE);
        assertThat(options.getTopP()).isEqualTo(TEST_TOP_P);
        assertThat(options.getTopK()).isEqualTo(TEST_TOP_K);
        assertThat(options.getSeed()).isEqualTo(TEST_SEED);
        assertThat(options.getRepetitionPenalty()).isEqualTo(TEST_REPETITION_PENALTY);
        assertThat(options.getStream()).isTrue();
        assertThat(options.getEnableSearch()).isTrue();
        assertThat(options.getIncrementalOutput()).isTrue();
        assertThat(options.getVlHighResolutionImages()).isTrue();
        assertThat(options.getEnableThinking()).isTrue();
        assertThat(options.getThinkingBudget()).isEqualTo(TEST_THINKING_BUDGET);
        assertThat(options.getMultiModel()).isTrue();
        assertThat(options.getExtraBody()).isEqualTo(TEST_EXTRA_BODY);
    }

    @Test
    void testSettersAndGetters() {
        // Test setters and getters
        DashScopeChatOptions options = new DashScopeChatOptions();

        options.setModel(TEST_MODEL);
        options.setTemperature(TEST_TEMPERATURE);
        options.setTopP(TEST_TOP_P);
        options.setTopK(TEST_TOP_K);
        options.setSeed(TEST_SEED);
        options.setRepetitionPenalty(TEST_REPETITION_PENALTY);
        options.setStream(true);
        options.setEnableSearch(true);
        options.setIncrementalOutput(true);
        options.setVlHighResolutionImages(true);
        options.setEnableThinking(true);
        options.setThinkingBudget(TEST_THINKING_BUDGET);
        options.setMultiModel(true);
        options.setExtraBody(TEST_EXTRA_BODY);

        // Verify all fields are set correctly
        assertThat(options.getModel()).isEqualTo(TEST_MODEL);
        assertThat(options.getTemperature()).isEqualTo(TEST_TEMPERATURE);
        assertThat(options.getTopP()).isEqualTo(TEST_TOP_P);
        assertThat(options.getTopK()).isEqualTo(TEST_TOP_K);
        assertThat(options.getSeed()).isEqualTo(TEST_SEED);
        assertThat(options.getRepetitionPenalty()).isEqualTo(TEST_REPETITION_PENALTY);
        assertThat(options.getStream()).isTrue();
        assertThat(options.getEnableSearch()).isTrue();
        assertThat(options.getIncrementalOutput()).isTrue();
        assertThat(options.getVlHighResolutionImages()).isTrue();
        assertThat(options.getEnableThinking()).isTrue();
        assertThat(options.getThinkingBudget()).isEqualTo(TEST_THINKING_BUDGET);
        assertThat(options.getMultiModel()).isTrue();
        assertThat(options.getExtraBody()).isEqualTo(TEST_EXTRA_BODY);
    }

    @Test
    void testToolCallbacks() {
        // Test function callbacks related methods
        ToolCallback callback1 = Mockito.mock(ToolCallback.class);
        ToolCallback callback2 = Mockito.mock(ToolCallback.class);

        List<ToolCallback> callbacks = Arrays.asList(callback1, callback2);
        Set<String> functions = new HashSet<>(Arrays.asList("test1", "test2"));

        DashScopeChatOptions options = DashScopeChatOptions.builder()
                .toolCallbacks(callbacks)
                .toolNames(functions)
                .build();

        assertThat(options.getToolCallbacks()).containsExactlyElementsOf(callbacks);
        assertThat(options.getToolNames()).containsExactlyInAnyOrderElementsOf(functions);
    }

    @Test
    void testToolsAndToolChoice() {
        // Test tools and tool choice related methods
        DashScopeApiSpec.FunctionTool.Function function = new DashScopeApiSpec.FunctionTool.Function("Test function", "test", "{}");
        DashScopeApiSpec.FunctionTool tool = new DashScopeApiSpec.FunctionTool(function);
        List<DashScopeApiSpec.FunctionTool> tools = Collections.singletonList(tool);
        Map<String, String> toolChoice = Map.of("type", "function", "name", "test");

        DashScopeChatOptions options = DashScopeChatOptions.builder().tools(tools).toolChoice(toolChoice).build();

        assertThat(options.getTools()).containsExactlyElementsOf(tools);
        assertThat(options.getToolChoice()).isEqualTo(toolChoice);
    }

    @Test
    void testResponseFormat() {
        // Test response format related methods
        DashScopeResponseFormat responseFormat = DashScopeResponseFormat.builder()
                .type(DashScopeResponseFormat.Type.JSON_OBJECT)
                .build();

        DashScopeChatOptions options = DashScopeChatOptions.builder().responseFormat(responseFormat).build();

        assertThat(options.getResponseFormat()).isEqualTo(responseFormat);
        assertThat(options.getResponseFormat().getType()).isEqualTo(DashScopeResponseFormat.Type.JSON_OBJECT);
    }

    @Test
    void testCopy() {
        // Test copy method
        DashScopeChatOptions original = DashScopeChatOptions.builder()
                .model(TEST_MODEL)
                .temperature(TEST_TEMPERATURE)
                .topP(TEST_TOP_P)
                .topK(TEST_TOP_K)
                .build();

        DashScopeChatOptions copy = (DashScopeChatOptions) original.copy();

        assertThat(copy).usingRecursiveComparison().isEqualTo(original);
        assertThat(copy).isNotSameAs(original);
    }

    @Test
    void testEqualsAndHashCode() {
        // Test equals and hashCode methods
        DashScopeChatOptions options1 = DashScopeChatOptions.builder()
                .model(TEST_MODEL)
                .temperature(TEST_TEMPERATURE)
                .extraBody(TEST_EXTRA_BODY)
                .build();

        DashScopeChatOptions options2 = DashScopeChatOptions.builder()
                .model(TEST_MODEL)
                .temperature(TEST_TEMPERATURE)
                .extraBody(TEST_EXTRA_BODY)
                .build();

        DashScopeChatOptions options3 = DashScopeChatOptions.builder()
                .model("different-model")
                .temperature(0.5)
                .extraBody(Map.of())
                .build();

        assertThat(options1).isEqualTo(options2);
        assertThat(options1.hashCode()).isEqualTo(options2.hashCode());
        assertThat(options1).isNotEqualTo(options3);
        assertThat(options1.hashCode()).isNotEqualTo(options3.hashCode());
    }

    @Test
    void testToString() {
        // Test toString method
        DashScopeChatOptions options = DashScopeChatOptions.builder()
                .model(TEST_MODEL)
                .temperature(TEST_TEMPERATURE)
                .build();

        String toString = options.toString();

        assertThat(toString).contains("DashScopeChatOptions")
                .contains(TEST_MODEL)
                .contains(TEST_TEMPERATURE.toString());
    }
}
