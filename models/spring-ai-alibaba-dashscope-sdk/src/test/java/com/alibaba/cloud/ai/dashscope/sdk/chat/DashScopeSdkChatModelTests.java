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

import com.alibaba.dashscope.aigc.generation.GenerationOutput;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.aigc.generation.GenerationUsage;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.tools.ToolCallFunction;
import io.reactivex.Flowable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.messages.ToolResponseMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DashScopeSdkChatModelTests {

	private static final String TEST_MODEL = "qwen-plus";

	private static final String TEST_REQUEST_ID = "test-request-id";

	private FakeGenerationClient generationClient;

	private DashScopeSdkChatModel chatModel;

	@BeforeEach
	void setUp() {
		this.generationClient = new FakeGenerationClient();
		DashScopeSdkChatOptions defaultOptions = DashScopeSdkChatOptions.builder().model(TEST_MODEL).build();
		this.chatModel = DashScopeSdkChatModel.builder()
			.generationClient(this.generationClient)
			.defaultOptions(defaultOptions)
			.apiKey("test-key")
			.workspaceId("workspace-id")
			.connectionHeaders(Map.of("x-test", "v"))
			.toolExecutionEligibilityPredicate((options, response) -> false)
			.build();
	}

	@Test
	void testBasicChatCompletion() {
		this.generationClient.callResult = createResult("Hello from SDK", "stop", false);

		ChatResponse response = this.chatModel.call(new Prompt(List.of(new UserMessage("Hello"))));

		assertThat(response).isNotNull();
		assertThat(response.getResult().getOutput().getText()).isEqualTo("Hello from SDK");
		assertThat(response.getMetadata().getId()).isEqualTo(TEST_REQUEST_ID);
		assertThat(this.generationClient.lastCallParam).isNotNull();
		assertThat(this.generationClient.lastCallParam.getModel()).isEqualTo(TEST_MODEL);
	}

	@Test
	void testStreamChatCompletion() {
		GenerationResult chunk1 = createResult("I am ", null, false);
		GenerationResult chunk2 = createResult("streaming", "stop", false);
		this.generationClient.streamResult = Flowable.just(chunk1, chunk2);

		Flux<ChatResponse> responses = this.chatModel.stream(new Prompt(List.of(new UserMessage("Hi"))));

		StepVerifier.create(responses)
			.assertNext(response -> assertThat(response.getResult().getOutput().getText()).isEqualTo("I am "))
			.assertNext(response -> assertThat(response.getResult().getOutput().getText()).isEqualTo("streaming"))
			.verifyComplete();
	}

	@Test
	void testToolCallMapping() {
		this.generationClient.callResult = createResult("", "tool_calls", true);

		ChatResponse response = this.chatModel.call(new Prompt(List.of(new UserMessage("call tool"))));

		assertThat(response.getResult().getOutput().getToolCalls()).hasSize(1);
		assertThat(response.getResult().getOutput().getToolCalls().get(0).name()).isEqualTo("get_weather");
	}

	@Test
	void testCreateRequestContainsExtraBody() {
		DashScopeSdkChatOptions runtimeOptions = DashScopeSdkChatOptions.builder()
			.model(TEST_MODEL)
			.extraBody(Map.of("custom_field", "custom_value"))
			.build();

		Prompt requestPrompt = this.chatModel.buildRequestPrompt(new Prompt(List.of(new UserMessage("Hello")), runtimeOptions));
		GenerationParam generationParam = this.chatModel.createRequest(requestPrompt, false);

		assertThat(generationParam.getParameters()).containsEntry("custom_field", "custom_value");
	}

	@Test
	void testCreateRequestMergesHeadersAndExtraBodyWithOverride() {
		this.chatModel.setDashScopeSdkChatOptions(DashScopeSdkChatOptions.builder()
			.model(TEST_MODEL)
			.httpHeaders(Map.of("x-default", "d", "x-override", "default"))
			.extraBody(Map.of("defaultKey", "defaultValue", "overridden", "from-default"))
			.build());

		DashScopeSdkChatOptions runtimeOptions = DashScopeSdkChatOptions.builder()
			.model(TEST_MODEL)
			.httpHeaders(Map.of("x-runtime", "r", "x-override", "runtime"))
			.extraBody(Map.of("runtimeKey", "runtimeValue", "overridden", "from-runtime"))
			.build();

		GenerationParam generationParam = this.chatModel
			.createRequest(this.chatModel.buildRequestPrompt(new Prompt(List.of(new UserMessage("Hello")), runtimeOptions)),
					false);

		assertThat(generationParam.getHeaders()).containsEntry("x-test", "v")
			.containsEntry("x-runtime", "r")
			.containsEntry("x-override", "runtime");
		assertThat(generationParam.getParameters()).containsEntry("defaultKey", "defaultValue")
			.containsEntry("runtimeKey", "runtimeValue")
			.containsEntry("overridden", "from-runtime");
	}

	@Test
	void testStopMappingForNumbersAndNestedNumbers() {
		DashScopeSdkChatOptions numericStopOptions = DashScopeSdkChatOptions.builder()
			.model(TEST_MODEL)
			.stop(List.of(10, 20))
			.build();

		GenerationParam numericStopRequest = this.chatModel.createRequest(
				this.chatModel.buildRequestPrompt(new Prompt(List.of(new UserMessage("Hello")), numericStopOptions)),
				false);

		assertThat(numericStopRequest.getStopTokens()).contains(List.of(10, 20));

		DashScopeSdkChatOptions nestedStopOptions = DashScopeSdkChatOptions.builder()
			.model(TEST_MODEL)
			.stop(List.of(List.of(1, 2, 3)))
			.build();

		GenerationParam nestedStopRequest = this.chatModel.createRequest(
				this.chatModel.buildRequestPrompt(new Prompt(List.of(new UserMessage("Hello")), nestedStopOptions)),
				false);

		assertThat(nestedStopRequest.getStopTokens()).contains(List.of(1, 2, 3));
	}

	@Test
	void testToolResponseMessageIsMappedToSdkToolRole() {
		ToolResponseMessage toolResponseMessage = ToolResponseMessage.builder()
			.responses(List.of(new ToolResponseMessage.ToolResponse("call-1", "get_weather", "{\"city\":\"HZ\"}")))
			.metadata(Map.of())
			.build();

		GenerationParam request = this.chatModel.createRequest(
				this.chatModel.buildRequestPrompt(new Prompt(List.of(toolResponseMessage),
						DashScopeSdkChatOptions.builder().model(TEST_MODEL).build())),
				false);

		assertThat(request.getMessages()).hasSize(1);
		assertThat(request.getMessages().get(0).getRole()).isEqualTo("tool");
		assertThat(request.getMessages().get(0).getName()).isEqualTo("get_weather");
		assertThat(request.getMessages().get(0).getToolCallId()).isEqualTo("call-1");
		assertThat(request.getMessages().get(0).getContent()).isEqualTo("{\"city\":\"HZ\"}");
	}

	@Test
	void testCallWrapsClientException() {
		this.generationClient.throwOnCall = new RuntimeException("call failed");

		assertThatThrownBy(() -> this.chatModel.call(new Prompt(List.of(new UserMessage("Hello")))))
			.isInstanceOf(com.alibaba.cloud.ai.dashscope.sdk.common.DashScopeSdkException.class)
			.hasMessageContaining("Failed to call DashScope SDK generation API")
			.hasCause(this.generationClient.throwOnCall);
	}

	@Test
	void testStreamWrapsClientException() {
		this.generationClient.throwOnStream = new RuntimeException("stream failed");

		StepVerifier.create(this.chatModel.stream(new Prompt(List.of(new UserMessage("Hi")))))
			.expectErrorSatisfies(error -> {
				assertThat(error)
					.isInstanceOf(com.alibaba.cloud.ai.dashscope.sdk.common.DashScopeSdkException.class)
					.hasMessageContaining("Failed to stream DashScope SDK generation API")
					.hasCause(this.generationClient.throwOnStream);
			})
			.verify();
	}

	@Test
	void testTextOnlyOutputWithoutChoices() {
		GenerationResult result = instantiateGenerationResult();
		GenerationOutput output = new GenerationOutput();
		output.setText("text-only");
		output.setFinishReason("stop");
		result.setRequestId(TEST_REQUEST_ID);
		result.setOutput(output);
		result.setUsage(GenerationUsage.builder().inputTokens(1).outputTokens(1).build());
		this.generationClient.callResult = result;

		ChatResponse response = this.chatModel.call(new Prompt(List.of(new UserMessage("Hello"))));

		assertThat(response.getResults()).hasSize(1);
		assertThat(response.getResult().getOutput().getText()).isEqualTo("text-only");
	}

	@Test
	void testNullResultReturnsEmptyGenerations() {
		this.generationClient.callResult = null;

		ChatResponse response = this.chatModel.call(new Prompt(List.of(new UserMessage("Hello"))));

		assertThat(response.getResults()).isEmpty();
	}

	private GenerationResult createResult(String content, String finishReason, boolean withToolCall) {
		GenerationOutput output = new GenerationOutput();
		GenerationOutput.Choice choice = output.new Choice();
		choice.setFinishReason(finishReason);
		choice.setIndex(0);

		Message message = Message.builder().role("assistant").content(content).build();
		if (withToolCall) {
			ToolCallFunction toolCallFunction = new ToolCallFunction();
			toolCallFunction.setId("tool-call-id-1");
			toolCallFunction.setType("function");
			ToolCallFunction.CallFunction function = toolCallFunction.new CallFunction();
			function.setName("get_weather");
			function.setArguments("{\"city\":\"Hangzhou\"}");
			toolCallFunction.setFunction(function);
			message.setToolCalls(List.of(toolCallFunction));
		}

		choice.setMessage(message);
		output.setChoices(List.of(choice));
		output.setText(content);
		output.setFinishReason(finishReason);

		GenerationUsage usage = GenerationUsage.builder().inputTokens(10).outputTokens(5).totalTokens(15).build();

		GenerationResult result = instantiateGenerationResult();
		result.setRequestId(TEST_REQUEST_ID);
		result.setOutput(output);
		result.setUsage(usage);
		return result;
	}

	private GenerationResult instantiateGenerationResult() {
		try {
			Constructor<GenerationResult> constructor = GenerationResult.class.getDeclaredConstructor();
			constructor.setAccessible(true);
			return constructor.newInstance();
		}
		catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	private static final class FakeGenerationClient implements DashScopeSdkGenerationClient {

		private GenerationResult callResult;

		private Flowable<GenerationResult> streamResult;

		private GenerationParam lastCallParam;

		private RuntimeException throwOnCall;

		private RuntimeException throwOnStream;

		@Override
		public GenerationResult call(GenerationParam generationParam) {
			if (this.throwOnCall != null) {
				throw this.throwOnCall;
			}
			this.lastCallParam = generationParam;
			return this.callResult;
		}

		@Override
		public Flowable<GenerationResult> stream(GenerationParam generationParam) {
			if (this.throwOnStream != null) {
				throw this.throwOnStream;
			}
			return this.streamResult;
		}

	}

}
