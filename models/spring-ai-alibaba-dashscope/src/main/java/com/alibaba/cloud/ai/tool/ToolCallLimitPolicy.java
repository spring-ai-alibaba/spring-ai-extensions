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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jspecify.annotations.Nullable;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.AssistantMessage.ToolCall;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.ToolResponseMessage;
import org.springframework.ai.chat.messages.ToolResponseMessage.ToolResponse;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.DefaultToolCallingManager;
import org.springframework.ai.model.tool.ToolCallLimitBehavior;
import org.springframework.ai.model.tool.ToolCallLimitExceededException;
import org.springframework.ai.model.tool.ToolExecutionResult;
import org.springframework.util.Assert;

/** Per-turn limits for the DashScope and ARMS tool calling managers. */
public record ToolCallLimitPolicy(int maxCallsPerTool, Map<String, Integer> toolOverrides, Set<String> excludedTools,
        int maxTotalCalls, ToolCallLimitBehavior onLimitExceeded) {

    private static final int UNLIMITED = -1;

    static final ToolCallLimitPolicy DEFAULT = new ToolCallLimitPolicy(
            DefaultToolCallingManager.DEFAULT_MAX_CALLS_PER_TOOL, Map.of(), Set.of(),
            DefaultToolCallingManager.DEFAULT_MAX_TOTAL_TOOL_CALLS, ToolCallLimitBehavior.THROW);

    public ToolCallLimitPolicy(@Nullable Integer maxCallsPerTool, Map<String, Integer> toolOverrides,
            Set<String> excludedTools, @Nullable Integer maxTotalCalls, ToolCallLimitBehavior onLimitExceeded) {
        this(maxCallsPerTool == null ? DefaultToolCallingManager.DEFAULT_MAX_CALLS_PER_TOOL : maxCallsPerTool,
                toolOverrides, excludedTools,
                maxTotalCalls == null ? DefaultToolCallingManager.DEFAULT_MAX_TOTAL_TOOL_CALLS : maxTotalCalls,
                onLimitExceeded);
    }

    public ToolCallLimitPolicy {
        validateLimit(maxCallsPerTool);
        validateLimit(maxTotalCalls);
        toolOverrides = Map.copyOf(toolOverrides);
        toolOverrides.forEach((name, limit) -> {
            Assert.hasText(name, "Tool name must not be empty");
            validateLimit(limit);
        });
        excludedTools = Set.copyOf(excludedTools);
        Assert.notNull(onLimitExceeded, "onLimitExceeded must not be null");
    }

    private static void validateLimit(int limit) {
        Assert.isTrue(limit == UNLIMITED || limit > 0, "Tool call limit must be positive or -1");
    }

    Turn startTurn(List<Message> instructions) {
        return new Turn(instructions);
    }

    final class Turn {
        private final Map<String, Integer> counts = new HashMap<>();
        private int total;

        private Turn(List<Message> instructions) {
            for (int i = instructions.size() - 1; i >= 0; i--) {
                Message message = instructions.get(i);
                if (message instanceof UserMessage) {
                    break;
                }
                if (message instanceof ToolResponseMessage toolResponse) {
                    for (ToolResponse response : toolResponse.getResponses()) {
                        this.counts.merge(response.name(), 1, Integer::sum);
                        this.total++;
                    }
                }
            }
        }

        @Nullable Breach check(ToolCall call) {
            this.total++;
            String name = call.name();
            int count = this.counts.merge(name, 1, Integer::sum);
            int limit = toolOverrides.getOrDefault(name, maxCallsPerTool);
            if (!excludedTools.contains(name) && limit != UNLIMITED && count > limit) {
                return new Breach(name, limit, new ToolResponse(call.id(), name,
                        "Tool call limit (%d) exceeded for tool '%s'. No further calls to this tool are allowed in this turn."
                                .formatted(limit, name)));
            }
            if (maxTotalCalls != UNLIMITED && this.total > maxTotalCalls) {
                return new Breach(null, maxTotalCalls, new ToolResponse(call.id(), name,
                        "Total tool call limit (%d) exceeded for this turn. No further tool calls are allowed."
                                .formatted(maxTotalCalls)));
            }
            return null;
        }
    }

    record Breach(@Nullable String toolName, int limit, ToolResponse response) {
        ToolCallLimitExceededException exception(Prompt prompt, AssistantMessage assistantMessage,
                List<ToolResponse> responses, boolean returnDirect) {
            List<Message> history = new ArrayList<>(prompt.getInstructions());
            history.add(assistantMessage);
            history.add(ToolResponseMessage.builder().responses(responses).build());
            ToolExecutionResult partial = ToolExecutionResult.builder()
                    .conversationHistory(history).returnDirect(returnDirect).build();
            return new ToolCallLimitExceededException(this.toolName, this.limit, partial);
        }
    }
}
