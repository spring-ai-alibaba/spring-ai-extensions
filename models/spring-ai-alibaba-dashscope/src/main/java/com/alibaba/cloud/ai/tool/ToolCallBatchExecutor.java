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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;

import org.jspecify.annotations.Nullable;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.AssistantMessage.ToolCall;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.ToolResponseMessage;
import org.springframework.ai.chat.messages.ToolResponseMessage.ToolResponse;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.ToolCallConfirmationRequiredException;
import org.springframework.ai.model.tool.ToolCallDecision;
import org.springframework.ai.model.tool.ToolCallDecisions;
import org.springframework.ai.model.tool.ToolCallLimitBehavior;
import org.springframework.ai.model.tool.ToolCallLimitExceededException;
import org.springframework.ai.model.tool.ToolCallingChatOptions;
import org.springframework.ai.model.tool.ToolExecutionResult;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.ai.tool.resolution.ToolCallbackResolver;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/** Plans a whole batch before any callback can run, then collects results in model order. */
final class ToolCallBatchExecutor {

	private final ToolCallbackResolver resolver;

	private final ToolCallExecutionOptions options;

	ToolCallBatchExecutor(ToolCallbackResolver resolver, ToolCallExecutionOptions options) {
		this.resolver = resolver;
		this.options = options;
	}

	static List<ToolDefinition> resolveToolDefinitions(ToolCallingChatOptions options) {
		Assert.notNull(options, "chatOptions cannot be null");
		return requestCallbacks(options).stream().map(ToolCallback::getToolDefinition).toList();
	}

	private static List<ToolCallback> requestCallbacks(ToolCallingChatOptions options) {
		List<ToolCallback> callbacks = options.getToolCallbacks();
		return callbacks != null ? callbacks : List.of();
	}

	ToolExecutionResult execute(Prompt prompt, ChatResponse response, @Nullable ToolCallDecisions decisions,
			BiFunction<Invocation, ToolContext, CompletableFuture<String>> invoke) {
		Assert.notNull(prompt, "prompt cannot be null");
		Assert.notNull(response, "chatResponse cannot be null");
		AssistantMessage reply = response.getResults().stream()
			.map(Generation::getOutput)
			.filter(AssistantMessage::hasToolCalls)
			.findFirst()
			.orElseThrow(() -> new IllegalStateException("No tool call requested by the chat model"));

		Map<String, ToolCallback> requestTools = new LinkedHashMap<>();
		Map<String, Object> context = new HashMap<>();
		if (prompt.getOptions() instanceof ToolCallingChatOptions toolOptions) {
			requestCallbacks(toolOptions).forEach(tool -> requestTools.putIfAbsent(tool.getToolDefinition().name(), tool));
			Map<String, Object> toolContext = toolOptions.getToolContext();
			if (toolContext != null) {
				context.putAll(toolContext);
			}
		}

		List<String> keys = ToolCallDecisions.toolCallKeys(reply);
		List<ToolCall> calls = reply.getToolCalls();
		List<ResolvedCall> resolved = resolveCalls(calls, keys, requestTools, decisions);
		List<Message> history = new ArrayList<>(prompt.getInstructions());
		if (!history.isEmpty() && history.get(history.size() - 1) instanceof AssistantMessage last
				&& last.getToolCalls().equals(calls)) {
			history.remove(history.size() - 1);
		}
		List<PlannedCall> plan = planCalls(resolved, history);
		history.add(reply);

		ToolContext toolContext = new ToolContext(context);
		List<CompletableFuture<ToolResponse>> responses = new ArrayList<>();
		boolean returnDirect = true;
		for (PlannedCall call : plan) {
			ToolCallback callback = call.callback();
			if (callback != null) {
				returnDirect &= callback.getToolMetadata().returnDirect();
				responses.add(invoke.apply(new Invocation(call.toolCall(), callback), toolContext)
					.thenApply(result -> toolResponse(call.toolCall(), Objects.requireNonNullElse(result, ""))));
			}
			else {
				returnDirect = false;
				responses.add(CompletableFuture.completedFuture(
						toolResponse(call.toolCall(), Objects.requireNonNull(call.error()))));
			}
		}

		CompletableFuture.allOf(responses.toArray(CompletableFuture<?>[]::new)).join();
		history.add(ToolResponseMessage.builder().responses(responses.stream().map(CompletableFuture::join).toList())
			.build());
		ToolExecutionResult result = ToolExecutionResult.builder().conversationHistory(history)
			.returnDirect(returnDirect).build();

		Breach breach = plan.get(plan.size() - 1).breach();
		if (breach != null && this.options.onLimitExceeded() == ToolCallLimitBehavior.THROW) {
			throw new ToolCallLimitExceededException(breach.toolName(), breach.limit(), result);
		}
		return result;
	}

	private List<ResolvedCall> resolveCalls(List<ToolCall> calls, List<String> keys,
			Map<String, ToolCallback> requestTools, @Nullable ToolCallDecisions decisions) {
		if (decisions != null) {
			for (ToolCallDecision decision : decisions.decisions()) {
				Assert.isTrue(keys.contains(decision.toolCallId()),
						() -> "No tool call has the key '" + decision.toolCallId() + "'");
			}
		}
		List<ResolvedCall> resolved = new ArrayList<>(calls.size());
		List<String> awaiting = new ArrayList<>();
		for (int i = 0; i < calls.size(); i++) {
			ToolCall call = calls.get(i);
			String key = keys.get(i);
			ToolCallback callback = requestTools.get(call.name());
			if (decisions != null) {
				ToolCallDecision decision = decisions.decisionOn(key);
				Assert.notNull(decision, () -> "Every tool call needs a decision; missing: " + key);
				if (!decision.allowed()) {
					resolved.add(new ResolvedCall(call, null, decision.denyMessage()));
					continue;
				}
				Assert.notNull(callback, () -> "The prompt does not carry the tool '" + call.name() + "'");
			}
			else {
				if (callback == null && this.options.resolutionFallbackEnabled()) {
					callback = this.resolver.resolve(call.name());
				}
				if (this.options.confirmationPolicy().requiresConfirmation(call,
						callback != null ? callback.getToolMetadata() : null)) {
					awaiting.add(key);
				}
			}
			resolved.add(new ResolvedCall(call, callback, null));
		}
		if (!awaiting.isEmpty()) {
			throw new ToolCallConfirmationRequiredException(awaiting);
		}
		return resolved;
	}

	private List<PlannedCall> planCalls(List<ResolvedCall> calls, List<Message> history) {
		Map<String, Integer> counts = countCurrentTurn(history);
		int total = counts.values().stream().mapToInt(Integer::intValue).sum();
		List<PlannedCall> plan = new ArrayList<>(calls.size());
		for (ResolvedCall call : calls) {
			if (call.denial() != null) {
				plan.add(new PlannedCall(call.toolCall(), null, call.denial(), null));
				continue;
			}
			String name = call.toolCall().name();
			Breach breach = checkLimit(name, counts.merge(name, 1, Integer::sum), ++total);
			if (breach != null) {
				plan.add(new PlannedCall(call.toolCall(), null, breach.message(), breach));
				if (this.options.onLimitExceeded() == ToolCallLimitBehavior.THROW) {
					break;
				}
				continue;
			}
			ToolCallback callback = call.callback();
			if (callback == null) {
				throw new IllegalStateException("No ToolCallback found for tool name: " + name);
			}
			plan.add(new PlannedCall(call.toolCall(), callback, null, null));
		}
		return plan;
	}

	private static Map<String, Integer> countCurrentTurn(List<Message> history) {
		Map<String, Integer> counts = new HashMap<>();
		for (Message message : history) {
			if (message instanceof UserMessage) {
				counts.clear();
			}
			else if (message instanceof ToolResponseMessage responses) {
				for (ToolResponse response : responses.getResponses()) {
					counts.merge(response.name(), 1, Integer::sum);
				}
			}
		}
		return counts;
	}

	private @Nullable Breach checkLimit(String name, int count, int total) {
		int max = this.options.perToolLimits().getOrDefault(name, this.options.maxCallsPerTool());
		if (!this.options.excludedTools().contains(name) && max >= 0 && count > max) {
			return new Breach(name, max, "Tool call limit (%d) exceeded for tool '%s' in this turn.".formatted(max, name));
		}
		int maxTotal = this.options.maxTotalCalls();
		if (maxTotal >= 0 && total > maxTotal) {
			return new Breach(null, maxTotal, "Total tool call limit (%d) exceeded for this turn.".formatted(maxTotal));
		}
		return null;
	}

	private static ToolResponse toolResponse(ToolCall call, String result) {
		return new ToolResponse(call.id(), call.name(), result);
	}

	record Invocation(ToolCall call, ToolCallback callback) {

		String arguments() {
			return StringUtils.hasText(this.call.arguments()) ? this.call.arguments() : "{}";
		}

	}

	private record ResolvedCall(ToolCall toolCall, @Nullable ToolCallback callback, @Nullable String denial) {
	}

	private record PlannedCall(ToolCall toolCall, @Nullable ToolCallback callback, @Nullable String error,
			@Nullable Breach breach) {
	}

	private record Breach(@Nullable String toolName, int limit, String message) {
	}

}
