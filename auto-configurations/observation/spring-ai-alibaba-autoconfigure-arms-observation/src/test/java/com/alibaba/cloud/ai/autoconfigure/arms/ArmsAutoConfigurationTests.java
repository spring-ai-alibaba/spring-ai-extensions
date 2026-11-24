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
package com.alibaba.cloud.ai.autoconfigure.arms;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import com.alibaba.cloud.ai.autoconfigure.dashscope.DashScopeAsyncToolCallingManagerAutoConfiguration;
import com.alibaba.cloud.ai.autoconfigure.dashscope.DashScopeChatAutoConfiguration;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeToolCallingAdvisor;
import com.alibaba.cloud.ai.tool.DashScopeAsyncToolCallingManager;
import com.alibaba.cloud.ai.tool.ObservableToolCallingManager;
import com.alibaba.cloud.ai.tool.observation.ArmsToolCallingObservationContext;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationHandler;
import io.micrometer.observation.ObservationRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.ToolCallingAdvisor;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.ToolResponseMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.chat.client.autoconfigure.ChatClientAutoConfiguration;
import org.springframework.ai.model.tool.ToolCallLimitExceededException;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.model.tool.ToolCallingChatOptions;
import org.springframework.ai.model.tool.autoconfigure.ToolCallingAutoConfiguration;
import org.springframework.ai.tool.function.FunctionToolCallback;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ArmsAutoConfigurationTests {

    @Test
    void usesUpstreamToolLimitProperties() {
        new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(ArmsAutoConfiguration.class,
                        ToolCallingAutoConfiguration.class))
                .withPropertyValues("spring.ai.alibaba.arms.enabled=true", "spring.ai.alibaba.arms.tool.enabled=true",
                        "spring.ai.tools.limits.max-calls-per-tool-default=1")
                .run(context -> {
                    assertThat(context).hasSingleBean(ToolCallingManager.class);
                    ToolCallingManager manager = context.getBean(ToolCallingManager.class);
                    assertThat(manager).isInstanceOf(ObservableToolCallingManager.class);
                    ToolResponseMessage previous = ToolResponseMessage.builder()
                            .responses(List.of(new ToolResponseMessage.ToolResponse("1", "weather", "sunny"))).build();
                    Prompt prompt = new Prompt(List.of(new UserMessage("weather"), previous));
                    ChatResponse response = new ChatResponse(List.of(new Generation(AssistantMessage.builder()
                            .content("").toolCalls(List.of(new AssistantMessage.ToolCall("2", "function", "weather", "{}")))
                            .build())));

                    assertThatThrownBy(() -> manager.executeToolCalls(prompt, response))
                            .isInstanceOfSatisfying(ToolCallLimitExceededException.class,
                                    error -> assertThat(error.getLimit()).isEqualTo(1));
                });
    }
    @Test
    void bothToolConfigurationsUseOneAsyncManagerAndKeepArmsObservation() {
        var registry = ObservationRegistry.create();
        var observed = new AtomicReference<ArmsToolCallingObservationContext>();
        registry.observationConfig().observationHandler(new ObservationHandler<ArmsToolCallingObservationContext>() {
            @Override
            public boolean supportsContext(Observation.Context context) {
                return context instanceof ArmsToolCallingObservationContext;
            }

            @Override
            public void onStop(ArmsToolCallingObservationContext context) {
                observed.set(context);
            }
        });

        new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(ArmsAutoConfiguration.class,
                        DashScopeAsyncToolCallingManagerAutoConfiguration.class, DashScopeChatAutoConfiguration.class,
                        ToolCallingAutoConfiguration.class, ChatClientAutoConfiguration.class))
                .withBean(ObservationRegistry.class, () -> registry)
                .withPropertyValues("spring.ai.alibaba.arms.enabled=true", "spring.ai.alibaba.arms.tool.enabled=true",
                        "spring.ai.alibaba.tool.async.enabled=true", "spring.ai.tools.limits.max-calls-per-tool-default=1",
                        "spring.ai.dashscope.api-key=test")
                .run(context -> {
                    assertThat(context).hasSingleBean(ToolCallingManager.class);
                    ToolCallingManager manager = context.getBean(ToolCallingManager.class);
                    assertThat(manager).isInstanceOf(DashScopeAsyncToolCallingManager.class);
                    assertThat(context).hasSingleBean(ChatClient.Builder.class);
                    assertThat(context.getBean(ToolCallingAdvisor.Builder.class).copy().build())
                            .isInstanceOf(DashScopeToolCallingAdvisor.class);

                    var callingThread = Thread.currentThread();
                    var callbackThread = new AtomicReference<Thread>();
                    var callback = FunctionToolCallback.builder("weather", (WeatherRequest request) -> {
                        callbackThread.set(Thread.currentThread());
                        return "sunny";
                    }).description("Look up weather").inputType(WeatherRequest.class).build();
                    var prompt = new Prompt("weather", ToolCallingChatOptions.builder().toolCallbacks(callback).build());
                    var response = new ChatResponse(List.of(new Generation(AssistantMessage.builder().content("")
                            .toolCalls(List.of(new AssistantMessage.ToolCall("1", "function", "weather",
                                    "{\"city\":\"Hangzhou\"}"))).build())));

                    var parent = Observation.start("chat", registry);
                    try (Observation.Scope scope = parent.openScope()) {
                        var result = manager.executeToolCalls(prompt, response);
                        assertThat(result.conversationHistory()).hasSize(3);
                        assertThat(callbackThread.get()).isNotNull().isNotSameAs(callingThread);
                        assertThat(observed.get()).isNotNull();
                        assertThat(observed.get().getToolResult()).contains("sunny");
                        assertThat(observed.get().getParentObservation()).isSameAs(parent);

                        var nextPrompt = new Prompt(result.conversationHistory(), prompt.getOptions());
                        assertThatThrownBy(() -> manager.executeToolCalls(nextPrompt, response))
                                .isInstanceOf(ToolCallLimitExceededException.class);
                    }
                    finally {
                        parent.stop();
                    }
                });
    }

    record WeatherRequest(String city) {
    }

}
