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
package com.alibaba.cloud.ai.autoconfigure.dashscope;

import java.util.List;

import com.alibaba.cloud.ai.tool.DashScopeAsyncToolCallingManager;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.ToolResponseMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.ToolCallLimitExceededException;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.model.tool.autoconfigure.ToolCallingAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DashScopeAsyncToolCallingManagerAutoConfigurationTests {

    @Test
    void usesUpstreamToolLimitProperties() {
        new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(DashScopeAsyncToolCallingManagerAutoConfiguration.class,
                        ToolCallingAutoConfiguration.class))
                .withPropertyValues("spring.ai.alibaba.tool.async.enabled=true",
                        "spring.ai.tools.limits.max-calls-per-tool-default=1")
                .run(context -> {
                    assertThat(context).hasSingleBean(ToolCallingManager.class);
                    ToolCallingManager manager = context.getBean(ToolCallingManager.class);
                    assertThat(manager).isInstanceOf(DashScopeAsyncToolCallingManager.class);
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
}
