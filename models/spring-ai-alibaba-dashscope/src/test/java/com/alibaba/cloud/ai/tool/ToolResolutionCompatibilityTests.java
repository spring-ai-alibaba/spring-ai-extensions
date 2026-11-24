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
package com.alibaba.cloud.ai.tool;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationHandler;
import io.micrometer.observation.ObservationRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.ToolResponseMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.ToolCallLimitBehavior;
import org.springframework.ai.model.tool.ToolCallLimitExceededException;
import org.springframework.ai.model.tool.ToolCallingChatOptions;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.ai.tool.resolution.ToolCallbackResolver;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ToolResolutionCompatibilityTests {

    private final ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1, 1, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>());

    @AfterEach
    void shutdownExecutor() {
        this.executor.shutdownNow();
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void toolsNotAttachedToRequestAreRejectedByDefault(boolean async) {
        ToolCallbackResolver resolver = mock(ToolCallbackResolver.class);
        ToolCallingManager manager = manager(async, false, resolver);
        assertThatThrownBy(() -> manager.executeToolCalls(new Prompt("hello"), response())).isInstanceOf(RuntimeException.class);
        verify(resolver, never()).resolve("weather");
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void explicitlyEnabledFallbackResolvesTools(boolean async) {
        ToolCallbackResolver resolver = mock(ToolCallbackResolver.class);
        ToolCallback tool = tool();
        when(resolver.resolve("weather")).thenReturn(tool);
        manager(async, true, resolver).executeToolCalls(new Prompt("hello"), response());
        verify(resolver).resolve("weather");
        verify(tool).call(eq("{}"), any(ToolContext.class));
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void attachedToolsExecuteWithoutFallbackAndNormalizeMissingArguments(boolean async) {
        ToolCallbackResolver resolver = mock(ToolCallbackResolver.class);
        ToolCallback tool = tool();
        Prompt prompt = new Prompt("hello", ToolCallingChatOptions.builder().toolCallbacks(tool).build());
        assertThatCode(() -> manager(async, false, resolver).executeToolCalls(prompt, response())).doesNotThrowAnyException();
        verify(resolver, never()).resolve("weather");
        verify(tool).call(eq("{}"), any(ToolContext.class));
    }

    @Test
    void blockingToolObservationRetainsParentObservation() {
        ObservationRegistry registry = ObservationRegistry.create();
        registry.observationConfig().observationHandler(new ObservationHandler<Observation.Context>() {
            @Override
            public boolean supportsContext(Observation.Context context) {
                return true;
            }
        });
        ToolCallback callback = tool();
        Observation parent = Observation.start("parent", registry);
        when(callback.call(eq("{}"), any(ToolContext.class))).thenAnswer(invocation -> {
            assertThat(registry.getCurrentObservation()).isNotNull();
            assertThat(registry.getCurrentObservation().getContext().getParentObservation()).isSameAs(parent);
            return "sunny";
        });
        Prompt prompt = new Prompt("hello", ToolCallingChatOptions.builder().toolCallbacks(callback).build());
        try (Observation.Scope scope = parent.openScope()) {
            ObservableToolCallingManager.builder().observationRegistry(registry).build()
                    .executeToolCalls(prompt, response());
        }
        finally {
            parent.stop();
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void perToolLimitPreservesCompletedCallsAndResetsAtNextUserMessage(boolean async) {
        var policy = new ToolCallLimitPolicy(2, Map.of(), Set.of(), -1, ToolCallLimitBehavior.THROW);
        ToolCallingManager manager = async ? DashScopeAsyncToolCallingManager.builder().taskExecutor(this.executor)
                .limitPolicy(policy).build() : ObservableToolCallingManager.builder().limitPolicy(policy).build();
        ToolCallback tool = tool();
        ToolResponseMessage earlierResponse = ToolResponseMessage.builder()
                .responses(List.of(new ToolResponseMessage.ToolResponse("earlier", "weather", "sunny"))).build();
        Prompt prompt = new Prompt(List.of(new UserMessage("previous turn"), earlierResponse,
                new UserMessage("current turn"), earlierResponse),
                ToolCallingChatOptions.builder().toolCallbacks(tool).build());
        ChatResponse batch = new ChatResponse(List.of(new Generation(AssistantMessage.builder().content("")
                .toolCalls(List.of(new AssistantMessage.ToolCall("1", "function", "weather", null),
                        new AssistantMessage.ToolCall("2", "function", "weather", null),
                        new AssistantMessage.ToolCall("3", "function", "weather", null))).build())));

        assertThatThrownBy(() -> manager.executeToolCalls(prompt, batch))
                .isInstanceOfSatisfying(ToolCallLimitExceededException.class, ex -> {
                    assertThat(ex.getToolName()).isEqualTo("weather");
                    assertThat(ex.getLimit()).isEqualTo(2);
                    assertThat(ex.buildGeneration().getMetadata().getFinishReason())
                            .isEqualTo(ToolCallLimitExceededException.FINISH_REASON);
                    var partial = (ToolResponseMessage) ex.getPartialToolExecutionResult().conversationHistory()
                            .get(prompt.getInstructions().size() + 1);
                    assertThat(partial.getResponses()).extracting(ToolResponseMessage.ToolResponse::id)
                            .containsExactly("1", "2");
                });
        verify(tool).call(eq("{}"), any(ToolContext.class));
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void totalLimitCountsExcludedToolsAndReturnsAnErrorResponse(boolean async) {
        var policy = new ToolCallLimitPolicy(1, Map.of(), Set.of("weather"), 2,
                ToolCallLimitBehavior.RETURN_ERROR_RESPONSE);
        ToolCallingManager manager = async ? DashScopeAsyncToolCallingManager.builder().taskExecutor(this.executor)
                .limitPolicy(policy).build() : ObservableToolCallingManager.builder().limitPolicy(policy).build();
        ToolCallback tool = tool();
        Prompt prompt = new Prompt("hello", ToolCallingChatOptions.builder().toolCallbacks(tool).build());
        ChatResponse batch = new ChatResponse(List.of(new Generation(AssistantMessage.builder().content("")
                .toolCalls(List.of(new AssistantMessage.ToolCall("1", "function", "weather", null),
                        new AssistantMessage.ToolCall("2", "function", "weather", null),
                        new AssistantMessage.ToolCall("3", "function", "weather", null))).build())));

        var result = manager.executeToolCalls(prompt, batch);
        var responses = (ToolResponseMessage) result.conversationHistory().get(2);
        assertThat(responses.getResponses()).extracting(ToolResponseMessage.ToolResponse::responseData)
                .satisfiesExactly(first -> assertThat(first).isEqualTo("sunny"),
                        second -> assertThat(second).isEqualTo("sunny"),
                        third -> assertThat(third).contains("Total tool call limit (2)"));
        verify(tool, times(2)).call(eq("{}"), any(ToolContext.class));
    }

    private ToolCallingManager manager(boolean async, boolean fallback, ToolCallbackResolver resolver) {
        return async ? DashScopeAsyncToolCallingManager.builder().taskExecutor(this.executor)
                .toolCallbackResolver(resolver).resolutionFallbackEnabled(fallback).build()
                : ObservableToolCallingManager.builder().toolCallbackResolver(resolver)
                        .resolutionFallbackEnabled(fallback).build();
    }

    private ToolCallback tool() {
        ToolCallback callback = mock(ToolCallback.class);
        when(callback.getToolDefinition()).thenReturn(ToolDefinition.builder().name("weather")
                .description("Weather").inputSchema("{\"type\":\"object\"}").build());
        when(callback.getToolMetadata()).thenCallRealMethod();
        when(callback.call(eq("{}"), any(ToolContext.class))).thenReturn("sunny");
        return callback;
    }

    private ChatResponse response() {
        return new ChatResponse(List.of(new Generation(AssistantMessage.builder().content("")
                .toolCalls(List.of(new AssistantMessage.ToolCall("1", "function", "weather", null))).build())));
    }
}
