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
package com.alibaba.cloud.ai.dashscope.api;

import com.alibaba.cloud.ai.dashscope.spec.DashScopeApiSpec.ChatCompletionMessage;
import com.alibaba.cloud.ai.dashscope.spec.DashScopeApiSpec.ChatCompletionRequest;
import com.alibaba.cloud.ai.dashscope.spec.DashScopeApiSpec.ChatCompletionRequestInput;
import com.alibaba.cloud.ai.dashscope.spec.DashScopeApiSpec.ChatCompletionRequestParameter;
import com.alibaba.cloud.ai.dashscope.spec.DashScopeApiSpec.FunctionTool;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for the OpenAI-compatible mode of {@link DashScopeApi}.
 *
 * <p>Issue #4463: When connecting to private/local models that implement the OpenAI
 * Chat Completions API, the DashScope-native request format wraps {@code messages} inside
 * an {@code input} object, causing HTTP 400 errors ("you must provide a messages
 * parameter").
 *
 * <p>The fix adds an {@code openAiCompatible} flag to {@link DashScopeApi.Builder}.
 * When {@code true}, the request body is converted to a flat OpenAI-compatible format
 * with {@code messages} at the top level.
 */
class DashScopeApiOpenAiCompatibleTest {

	private static final ObjectMapper MAPPER = new ObjectMapper();

	// -----------------------------------------------------------------------
	// Helper: build a simple ChatCompletionRequest with one user message
	// -----------------------------------------------------------------------
	private static ChatCompletionRequest buildRequest() {
		List<ChatCompletionMessage> messages = List.of(
				new ChatCompletionMessage("Hello", ChatCompletionMessage.Role.USER));
		return new ChatCompletionRequest(
				"qwen-turbo",
				new ChatCompletionRequestInput(messages),
				null,
				false,
				false);
	}

	private static ChatCompletionRequest buildRequestWithParams() {
		List<ChatCompletionMessage> messages = List.of(
				new ChatCompletionMessage("Hello", ChatCompletionMessage.Role.USER));
		// Field order matches ChatCompletionRequestParameter record (33 fields):
		// resultFormat, seed, topP, topK, repetitionPenalty, presencePenalty,
		// temperature, stop, enableSearch, searchOptions, responseFormat,
		// incrementalOutput, tools, toolChoice, parallelToolCalls,
		// enableThinking, thinkingBudget, enableCodeInterpreter,
		// vlHighResolutionImages, vlEnableImageHwOutput, ocrOptions,
		// logprobs, topLogprobs, translationOptions,
		// stream, streamOptions, modalities, audio,
		// maxTokens, maxInputTokens, asrOptions, outputFormat, extraBody
		ChatCompletionRequestParameter params = new ChatCompletionRequestParameter(
				"message",  // resultFormat
				null,       // seed
				0.9,        // topP
				null,       // topK
				null,       // repetitionPenalty
				null,       // presencePenalty
				0.7,        // temperature
				null,       // stop
				null,       // enableSearch
				null,       // searchOptions
				null,       // responseFormat
				null,       // incrementalOutput
				List.of(new FunctionTool(new FunctionTool.Function("A tool", "my_tool", "{}"))), // tools
				"auto",     // toolChoice
				null,       // parallelToolCalls
				null,       // enableThinking
				null,       // thinkingBudget
				null,       // enableCodeInterpreter
				null,       // vlHighResolutionImages
				null,       // vlEnableImageHwOutput
				null,       // ocrOptions
				null,       // logprobs
				null,       // topLogprobs
				null,       // translationOptions
				null,       // stream
				null,       // streamOptions
				null,       // modalities
				null,       // audio
				1024,       // maxTokens
				null,       // maxInputTokens
				null,       // asrOptions
				null,       // outputFormat
				null        // extraBody
		);
		return new ChatCompletionRequest(
				"qwen-turbo",
				new ChatCompletionRequestInput(messages),
				params,
				false,
				false);
	}

	// -----------------------------------------------------------------------
	// BUG DEMONSTRATION: default (non-openAiCompatible) format nests messages
	// -----------------------------------------------------------------------

	/**
	 * Demonstrates the bug: the default DashScope serialization wraps {@code messages}
	 * inside {@code "input"}, NOT at the top level. This is what causes private/local
	 * OpenAI-compatible models to return HTTP 400 "missing messages parameter".
	 */
	@Test
	void defaultFormat_messagesAreNestedInsideInput() throws Exception {
		ChatCompletionRequest request = buildRequest();

		// Serialize as-is (how DashScopeApi currently sends the request)
		String json = MAPPER.writeValueAsString(request);
		JsonNode node = MAPPER.readTree(json);

		// BUG: messages are NOT at the top level – they are inside "input"
		assertThat(node.has("input")).isTrue();
		assertThat(node.get("input").has("messages")).isTrue();

		// An OpenAI-compatible endpoint expects top-level "messages" – which is ABSENT
		assertThat(node.has("messages"))
				.as("BUG: top-level 'messages' field is absent in default DashScope format")
				.isFalse();
	}

	// -----------------------------------------------------------------------
	// FIX VERIFICATION: openAiCompatible mode produces flat messages
	// -----------------------------------------------------------------------

	/**
	 * After the fix: when {@code openAiCompatible=true}, the converted body must have
	 * {@code messages} at the top level and must NOT contain {@code "input"}.
	 */
	@Test
	void openAiCompatibleMode_messagesAreAtTopLevel() {
		DashScopeApi api = DashScopeApi.builder()
				.apiKey("test-key")
				.openAiCompatible(true)
				.build();

		ChatCompletionRequest request = buildRequest();
		Map<String, Object> body = api.toOpenAiCompatibleBody(request);

		assertThat(body).containsKey("messages");
		assertThat(body).doesNotContainKey("input");
		assertThat(body).containsKey("model");
		assertThat(body).containsKey("stream");
	}

	/**
	 * The converted messages list must contain exactly the messages from the request.
	 */
	@Test
	void openAiCompatibleMode_messagesContentIsPreserved() {
		DashScopeApi api = DashScopeApi.builder()
				.apiKey("test-key")
				.openAiCompatible(true)
				.build();

		ChatCompletionRequest request = buildRequest();
		Map<String, Object> body = api.toOpenAiCompatibleBody(request);

		@SuppressWarnings("unchecked")
		List<ChatCompletionMessage> messages = (List<ChatCompletionMessage>) body.get("messages");
		assertThat(messages).hasSize(1);
		assertThat(messages.get(0).role()).isEqualTo(ChatCompletionMessage.Role.USER);
	}

	/**
	 * Parameters in {@code ChatCompletionRequestParameter} must be flattened to the
	 * top level (e.g., {@code temperature}, {@code tools}, {@code tool_choice}).
	 */
	@Test
	void openAiCompatibleMode_parametersAreFlattenedToTopLevel() {
		DashScopeApi api = DashScopeApi.builder()
				.apiKey("test-key")
				.openAiCompatible(true)
				.build();

		ChatCompletionRequest request = buildRequestWithParams();
		Map<String, Object> body = api.toOpenAiCompatibleBody(request);

		// No nested "parameters" object
		assertThat(body).doesNotContainKey("parameters");

		// Individual fields promoted to top level
		assertThat(body).containsKey("temperature");
		assertThat(body).containsKey("top_p");
		assertThat(body).containsKey("tools");
		assertThat(body).containsKey("tool_choice");
		assertThat(body).containsKey("max_tokens");
	}

	/**
	 * The {@code openAiCompatible} flag defaults to {@code false} and the body retains
	 * the DashScope-native format.
	 */
	@Test
	void defaultMode_flagIsFalseAndBodyIsNotConverted() {
		DashScopeApi api = DashScopeApi.builder()
				.apiKey("test-key")
				// openAiCompatible NOT set → defaults to false
				.build();

		assertThat(api.isOpenAiCompatible()).isFalse();
	}

	/**
	 * The {@code openAiCompatible} flag is preserved through {@code mutate()} / copy.
	 */
	@Test
	void mutate_preservesOpenAiCompatibleFlag() {
		DashScopeApi original = DashScopeApi.builder()
				.apiKey("test-key")
				.openAiCompatible(true)
				.build();

		DashScopeApi copy = original.mutate().build();
		assertThat(copy.isOpenAiCompatible()).isTrue();
	}

}
