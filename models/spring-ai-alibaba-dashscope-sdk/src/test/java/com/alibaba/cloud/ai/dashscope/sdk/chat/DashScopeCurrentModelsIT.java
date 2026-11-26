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

package com.alibaba.cloud.ai.dashscope.sdk.chat;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatReasoning;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.ai.chat.messages.ToolResponseMessage;
import org.springframework.ai.chat.messages.ToolResponseMessage.ToolResponse;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.messages.part.ToolCallPart;
import org.springframework.ai.chat.metadata.FinishReasonCategory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.MessageAggregator;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.tool.function.FunctionToolCallback;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import tools.jackson.databind.json.JsonMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

@EnabledIfEnvironmentVariable(named = "AI_DASHSCOPE_API_KEY", matches = ".+")
class DashScopeCurrentModelsIT {

	private static final String TOOL = "lookup_demo";

	private static final String VALUE = "SAA_ORCHID_739";

	@ParameterizedTest
	@CsvSource({ "qwen4-max, HTTP", "qwen4-max, SDK", "glm-5.2, HTTP", "glm-5.2, SDK",
			"glm-5.3, HTTP", "glm-5.3, SDK", "kimi-k3.1-pro, HTTP", "kimi-k3.1-pro, SDK",
			"kimi-k3, HTTP", "kimi-k3, SDK", "qwen3.8-flash, HTTP", "qwen3.8-flash, SDK" })
	void streamsNestedToolArgumentsAndReplaysTheAssistantInTheFollowUp(String model, String transport) {
		var tool = FunctionToolCallback.builder(TOOL, (LookupRequest request) -> VALUE)
			.description("Looks up a value for the nested query").inputType(LookupRequest.class).build();
		var first = new UserMessage("Call lookup_demo with query name demo and count 7. "
				+ "After using the tool, repeat only the value returned by the tool.");
		var chat = chatModel(model, transport, tool);
		var combined = new AtomicReference<ChatResponse>();
		var frames = withoutResourceExhaustion(() -> new MessageAggregator()
			.aggregate(chat.stream(new Prompt(first)), combined::set)
			.onErrorMap(WebClientResponseException.class, error -> {
				String body = error.getResponseBodyAsString();
				String payload = body.lines().filter(line -> line.startsWith("data:"))
					.map(line -> line.substring("data:".length()).strip()).findFirst().orElse(body);
				if (!payload.startsWith("{")) {
					return error;
				}
				var providerError = JsonMapper.shared().readTree(payload);
				return new IllegalStateException(error.getStatusCode() + " " + providerError.path("code").asString()
						+ " " + providerError.path("message").asString());
			}).collectList().block(Duration.ofSeconds(75)));
		assertThat(frames).isNotEmpty();
		var response = combined.get();
		assertThat(response.hasFinishReasonCategory(FinishReasonCategory.TOOL_CALL)).isTrue();
		var assistant = response.getResult().getOutput();
		assertThat(assistant.getToolCalls()).singleElement().satisfies(call -> {
			assertThat(call.name()).isEqualTo(TOOL);
			assertThat(call.arguments()).contains("query", "demo", "count", "7");
		});
		assertThat(assistant.getParts()).anyMatch(ToolCallPart.class::isInstance);
		if (model.startsWith("kimi-") || "glm-5.2".equals(model)) {
			assertThat(DashScopeChatReasoning.reasoningContent(assistant)).isNotBlank();
		}
		var call = assistant.getToolCalls().get(0);
		var result = ToolResponseMessage.builder()
			.responses(List.of(new ToolResponse(call.id(), call.name(), tool.call(call.arguments()))))
			.build();
		var answer = withoutResourceExhaustion(() -> chat.call(new Prompt(List.of(first, assistant, result))));
		assertThat(answer.getResult().getOutput().getText()).contains(VALUE);
		assertThat(answer.hasFinishReasonCategory(FinishReasonCategory.COMPLETED)).isTrue();
	}

	@ParameterizedTest
	@ValueSource(strings = { "HTTP", "SDK" })
	void replaysQwen4CiphertextAcrossTurns(String transport) {
		var chat = chatModel("qwen4-max", transport, null);
		var question = new UserMessage("请先推理，再计算 17×23。");
		var aggregate = new AtomicReference<ChatResponse>();
		new MessageAggregator().aggregate(chat.stream(new Prompt(question)), aggregate::set)
			.then().block(Duration.ofSeconds(75));
		var assistant = aggregate.get().getResult().getOutput();
		assertThat(DashScopeChatReasoning.encryptedContent(assistant)).isNotBlank();
		var response = chat.call(new Prompt(List.of(question, assistant, new UserMessage("上一个结果再加 9 是多少？"))));
		assertThat(response.getResult().getOutput().getText()).contains("400");
	}

	private static <T> T withoutResourceExhaustion(Supplier<T> work) {
		try {
			return work.get();
		}
		catch (RuntimeException ex) {
			for (Throwable cause = ex; cause != null; cause = cause.getCause()) {
				String message = cause.getMessage();
				assumeFalse(message != null && message.contains("Throttling.ResourceExhausted"),
						"Bailian returned Throttling.ResourceExhausted");
			}
			throw ex;
		}
	}

	private ChatModel chatModel(String model, String transport, @Nullable FunctionToolCallback<?, ?> callback) {
		String apiKey = System.getenv("AI_DASHSCOPE_API_KEY");
		boolean encrypted = "qwen4-max".equals(model);
		boolean streamToolArguments = model.startsWith("glm-") || model.startsWith("qwen");
		if ("HTTP".equals(transport)) {
			var options = DashScopeChatOptions.builder().model(model).maxCompletionTokens(1536).enableThinking(true);
			if (callback != null) {
				options.toolCallbacks(List.of(callback));
			}
			if (encrypted) {
				options.thinkingDisplay("summarized").preserveThinking(true).reasoningEffort("low");
			}
			if (model.startsWith("glm-")) {
				options.clearThinking(false).reasoningEffort("low");
			}
			if ("kimi-k3.1-pro".equals(model)) {
				options.reasoningEffort("low");
			}
			if (callback != null && streamToolArguments) {
				options.toolStream(true);
			}
			return DashScopeChatModel.builder().dashScopeApi(DashScopeApi.builder().apiKey(apiKey).build())
				.defaultOptions(options.build()).build();
		}
		var options = DashScopeSdkChatOptions.builder().model(model).maxCompletionTokens(1536).enableThinking(true);
		if (callback != null) {
			options.toolCallbacks(List.of(callback));
		}
		if (encrypted) {
			options.thinkingDisplay("summarized").preserveThinking(true).reasoningEffort("low");
		}
		if (model.startsWith("glm-")) {
			options.clearThinking(false).reasoningEffort("low");
		}
		if ("kimi-k3.1-pro".equals(model)) {
			options.reasoningEffort("low");
		}
		if (callback != null && streamToolArguments) {
			options.toolStream(true);
		}
		return DashScopeSdkChatModel.builder().apiKey(apiKey).defaultOptions(options.build()).build();
	}

	record LookupRequest(Query query) {
	}

	record Query(String name, int count) {
	}

}
