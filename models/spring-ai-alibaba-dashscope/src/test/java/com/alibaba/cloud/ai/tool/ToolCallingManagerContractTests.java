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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.AssistantMessage.ToolCall;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.ToolResponseMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.messages.part.ReasoningPart;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.ToolCallConfirmationPolicy;
import org.springframework.ai.model.tool.ToolCallConfirmationRequiredException;
import org.springframework.ai.model.tool.ToolCallDecision;
import org.springframework.ai.model.tool.ToolCallDecisions;
import org.springframework.ai.model.tool.ToolCallLimitBehavior;
import org.springframework.ai.model.tool.ToolCallLimitExceededException;
import org.springframework.ai.model.tool.ToolCallingChatOptions;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.definition.DefaultToolDefinition;
import org.springframework.ai.tool.metadata.ToolMetadata;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ToolCallingManagerContractTests {

	private final ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 2, 0, TimeUnit.SECONDS,
			new LinkedBlockingQueue<>());

	@AfterEach
	void closeExecutor() {
		this.executor.shutdownNow();
	}

	@ParameterizedTest
	@ValueSource(booleans = { false, true })
	void pausesTheWholeBatchAndResumesInReplyOrder(boolean async) {
		ToolCallback ordinary = tool("ordinary", false);
		ToolCallback approval = tool("approval", true);
		ToolCallingManager manager = manager(async, ToolCallExecutionOptions.defaults());
		ChatResponse reply = reply("ordinary", "approval");
		AssistantMessage assistant = reply.getResult().getOutput();
		Prompt prompt = prompt(List.of(new UserMessage("go"), assistant), ordinary, approval);

		assertThatExceptionOfType(ToolCallConfirmationRequiredException.class)
			.isThrownBy(() -> manager.executeToolCalls(prompt, reply))
			.satisfies(ex -> assertThat(ex.getToolCallIds()).containsExactly("call-1"));
		verify(ordinary, never()).call(any(), any());
		verify(approval, never()).call(any(), any());

		assertThatIllegalArgumentException().isThrownBy(() -> manager.executeToolCalls(prompt, reply,
				ToolCallDecisions.of(ToolCallDecision.allow("call-0"))));
		verify(ordinary, never()).call(any(), any());

		var result = manager.executeToolCalls(prompt, reply,
				ToolCallDecisions.of(ToolCallDecision.allow("call-0"), ToolCallDecision.deny("call-1", "declined")));
		assertThat(result.conversationHistory()).hasSize(3);
		assertThat(result.conversationHistory().get(1)).isEqualTo(assistant);
		assertThat(assistant.getReasoning()).containsExactly(ReasoningPart.of("plan"));
		var output = (ToolResponseMessage) result.conversationHistory().get(2);
		assertThat(output.getResponses()).extracting(ToolResponseMessage.ToolResponse::responseData)
			.containsExactly("ordinary result", "declined");
		assertThat(result.returnDirect()).isFalse();
		verify(ordinary).call(eq("{}"), any());
		verify(approval, never()).call(any(), any());
	}

	@ParameterizedTest
	@ValueSource(booleans = { false, true })
	void checksLimitsAcrossRoundsAndStartsFreshForANewUserTurn(boolean async) {
		ToolCallback tool = tool("lookup", false);
		var options = new ToolCallExecutionOptions(ToolCallConfirmationPolicy.fromToolMetadata(), false, 1,
				Map.of(), Set.of(), 3, ToolCallLimitBehavior.THROW);
		ToolCallingManager manager = manager(async, options);
		Prompt request = prompt(List.of(new UserMessage("first")), tool);
		var first = manager.executeToolCalls(request, reply("lookup"));
		Prompt sameTurn = prompt(first.conversationHistory(), tool);
		assertThatExceptionOfType(ToolCallLimitExceededException.class)
			.isThrownBy(() -> manager.executeToolCalls(sameTurn, reply("lookup")))
			.satisfies(ex -> {
				assertThat(ex.getLimit()).isEqualTo(1);
				assertThat(ex.getToolName()).isEqualTo("lookup");
			});
		verify(tool).call(eq("{}"), any());

		var nextTurn = new ArrayList<>(first.conversationHistory());
		nextTurn.add(new UserMessage("second"));
		assertThat(manager.executeToolCalls(prompt(nextTurn, tool), reply("lookup")).conversationHistory())
			.hasSize(6);
	}

	@ParameterizedTest
	@ValueSource(booleans = { false, true })
	void enforcesTotalLimitWithoutLosingResponseOrder(boolean async) {
		ToolCallback first = tool("first", false);
		ToolCallback second = tool("second", false);
		var options = new ToolCallExecutionOptions(ToolCallConfirmationPolicy.none(), false, -1,
				Map.of(), Set.of("second"), 1, ToolCallLimitBehavior.RETURN_ERROR_RESPONSE);
		var result = manager(async, options).executeToolCalls(prompt(List.of(new UserMessage("go")), first, second),
				reply("first", "second"));
		var output = (ToolResponseMessage) result.conversationHistory().get(2);
		assertThat(output.getResponses()).extracting(ToolResponseMessage.ToolResponse::id)
			.containsExactly("call-0", "call-1");
		assertThat(output.getResponses().get(1).responseData()).contains("Total tool call limit");
		assertThat(result.returnDirect()).isFalse();
		verify(second, never()).call(any(), any());
	}

	@ParameterizedTest
	@ValueSource(booleans = { false, true })
	void rejectsToolsOutsideTheRequestWhenResumingEvenWithFallbackEnabled(boolean async) {
		ToolCallback fallback = tool("fallback", false);
		var on = new ToolCallExecutionOptions(ToolCallConfirmationPolicy.none(), true, -1, Map.of(),
				Set.of(), -1, ToolCallLimitBehavior.THROW);
		Prompt request = prompt(List.of(new UserMessage("go")));
		assertThatIllegalStateException().isThrownBy(() ->
				manager(async, ToolCallExecutionOptions.defaults()).executeToolCalls(request, reply("fallback")));
		ToolCallingManager manager = async
				? DashScopeAsyncToolCallingManager.builder().taskExecutor(this.executor).executionOptions(on)
					.toolCallbackResolver(name -> fallback).build()
				: ObservableToolCallingManager.builder().executionOptions(on)
					.toolCallbackResolver(name -> fallback).build();
		assertThatIllegalArgumentException().isThrownBy(() -> manager.executeToolCalls(request, reply("fallback"),
				ToolCallDecisions.of(ToolCallDecision.allow("call-0"))));
		verify(fallback, never()).call(any(), any());
	}

	private ToolCallingManager manager(boolean async, ToolCallExecutionOptions options) {
		return async
				? DashScopeAsyncToolCallingManager.builder().taskExecutor(this.executor).executionOptions(options).build()
				: ObservableToolCallingManager.builder().executionOptions(options).build();
	}

	private static Prompt prompt(List<Message> messages, ToolCallback... tools) {
		return new Prompt(messages, ToolCallingChatOptions.builder().toolCallbacks(List.of(tools)).build());
	}

	private static ChatResponse reply(String... tools) {
		List<ToolCall> calls = IntStream.range(0, tools.length)
			.mapToObj(index -> new ToolCall("call-" + index, "function", tools[index], "{}")).toList();
		return new ChatResponse(List.of(new Generation(AssistantMessage.builder().reasoning(ReasoningPart.of("plan"))
			.toolCalls(calls).build())));
	}

	private static ToolCallback tool(String name, boolean confirmation) {
		ToolCallback callback = mock(ToolCallback.class);
		when(callback.getToolDefinition()).thenReturn(DefaultToolDefinition.builder().name(name).description(name)
			.inputSchema("{\"type\":\"object\"}").build());
		when(callback.getToolMetadata()).thenReturn(ToolMetadata.builder().requiresConfirmation(confirmation).build());
		when(callback.call(any(), any())).thenReturn(name + " result");
		return callback;
	}

}
