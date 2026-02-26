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
package com.alibaba.cloud.ai.dashscope.api.tts;

import com.alibaba.cloud.ai.dashscope.audio.DashScopeWebSocketClient;
import com.alibaba.cloud.ai.dashscope.audio.DashScopeWebSocketClient.EventType;
import com.alibaba.cloud.ai.dashscope.audio.WebSocketRequest;
import com.alibaba.cloud.ai.dashscope.audio.tts.DashScopeAudioSpeechOptions;
import com.alibaba.cloud.ai.dashscope.common.DashScopeAudioApiConstants;
import com.alibaba.cloud.ai.dashscope.protocol.DashScopeWebSocketClientOptions;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.springframework.ai.model.ApiKey;
import org.springframework.ai.util.JacksonUtils;
import reactor.core.publisher.Flux;

import java.nio.ByteBuffer;
import java.util.UUID;

/**
 * DashScope WebSocket TTS API for Sambert and CosyVoice models.
 * Uses run-task / continue-task / finish-task protocol.
 *
 * @author spring-ai-alibaba
 */
public class DashScopeWebSocketTTSApi {

	private final String websocketUrl;
	private final ApiKey apiKey;
	private final String workSpaceId;
	private final DashScopeWebSocketClientOptions clientOptions;
	private final ObjectMapper objectMapper;

	public DashScopeWebSocketTTSApi(String websocketUrl, ApiKey apiKey, String workSpaceId,
			DashScopeWebSocketClientOptions options) {
		this.websocketUrl = websocketUrl;
		this.apiKey = apiKey;
		this.workSpaceId = workSpaceId;
		this.clientOptions = options != null ? options : DashScopeWebSocketClientOptions.builder()
				.apiKey(apiKey.getValue())
				.workSpaceId(workSpaceId)
				.url(websocketUrl)
				.build();
		this.objectMapper = JsonMapper.builder()
				.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
				.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
				.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
				.serializationInclusion(JsonInclude.Include.NON_NULL)
				.addModules(JacksonUtils.instantiateAvailableModules())
				.build();
	}

	/**
	 * Create a new WebSocket client for each call to ensure thread-safety
	 * and avoid state leaking between concurrent calls.
	 */
	private DashScopeWebSocketClient newWebSocketClient() {
		return new DashScopeWebSocketClient(this.clientOptions);
	}

	/**
	 * Create WebSocket TTS task with a single text input.
	 * For CosyVoice: run-task (no text) -> continue-task (text) -> finish-task.
	 * For Sambert: run-task (with text) only.
	 */
	public Flux<ByteBuffer> stream(String text, DashScopeAudioSpeechOptions options) {
		boolean isCosyVoiceModel = DashScopeAudioApiConstants.COSY_VOICE_MODEL_LIST.contains(options.getModel());

		String taskId = UUID.randomUUID().toString();
		String streamingMode = isCosyVoiceModel ? "duplex" : "output";

		WebSocketRequest runTaskRequest = buildRunTaskRequest(taskId, streamingMode, options,
				isCosyVoiceModel ? null : text);
		WebSocketRequest finishTaskRequest = buildFinishTaskRequest(taskId, streamingMode);

		try {
			String runTaskMessage = objectMapper.writeValueAsString(runTaskRequest);
			String finishTaskMessage = objectMapper.writeValueAsString(finishTaskRequest);
			DashScopeWebSocketClient client = newWebSocketClient();

			if (isCosyVoiceModel) {
				WebSocketRequest continueTaskRequest = buildContinueTaskRequest(taskId, streamingMode, text);
				String continueTaskMessage = objectMapper.writeValueAsString(continueTaskRequest);
				return client.command(runTaskMessage, continueTaskMessage, finishTaskMessage);
			}
			else {
				return client.command(runTaskMessage);
			}
		}
		catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Create WebSocket TTS task with streaming text input (CosyVoice only).
	 * Protocol: run-task (no text) → task-started → N * continue-task (text chunk) → finish-task.
	 * Each element in textStream is sent as a separate continue-task message.
	 *
	 * @param textStream the streaming text input
	 * @param options TTS options
	 * @return the binary audio data flux
	 */
	public Flux<ByteBuffer> streamWithStreamingInput(Flux<String> textStream, DashScopeAudioSpeechOptions options) {
		if (!DashScopeAudioApiConstants.COSY_VOICE_MODEL_LIST.contains(options.getModel())) {
			throw new IllegalArgumentException(
					"Streaming text input is only supported for CosyVoice models, got: " + options.getModel());
		}

		String taskId = UUID.randomUUID().toString();
		String streamingMode = "duplex";

		WebSocketRequest runTaskRequest = buildRunTaskRequest(taskId, streamingMode, options, null);
		// Template for continue-task (text will be injected dynamically per chunk)
		WebSocketRequest continueTaskTemplate = buildContinueTaskRequest(taskId, streamingMode, null);
		WebSocketRequest finishTaskRequest = buildFinishTaskRequest(taskId, streamingMode);

		try {
			String runTaskMessage = objectMapper.writeValueAsString(runTaskRequest);
			String continueTaskTemplateMessage = objectMapper.writeValueAsString(continueTaskTemplate);
			String finishTaskMessage = objectMapper.writeValueAsString(finishTaskRequest);
			DashScopeWebSocketClient client = newWebSocketClient();

			return client.commandStreaming(runTaskMessage, continueTaskTemplateMessage,
					finishTaskMessage, textStream);
		}
		catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	private WebSocketRequest buildRunTaskRequest(String taskId, String streamingMode,
			DashScopeAudioSpeechOptions options, String text) {
		return WebSocketRequest.builder()
				.header(WebSocketRequest.RequestHeader.builder()
						.action(EventType.RUN_TASK)
						.taskId(taskId)
						.streaming(streamingMode)
						.build())
				.payload(WebSocketRequest.RequestPayload.builder()
						.model(options.getModel())
						.taskGroup("audio")
						.task("tts")
						.function("SpeechSynthesizer")
						.input(WebSocketRequest.RequestPayloadInput.builder()
								.text(text)
								.build())
						.parameters(WebSocketRequest.RequestPayloadParameters.speechOptionsConvertReq(options))
						.build())
				.build();
	}

	private WebSocketRequest buildContinueTaskRequest(String taskId, String streamingMode, String text) {
		return WebSocketRequest.builder()
				.header(WebSocketRequest.RequestHeader.builder()
						.action(EventType.CONTINUE_TASK)
						.taskId(taskId)
						.streaming(streamingMode)
						.build())
				.payload(WebSocketRequest.RequestPayload.builder()
						.input(WebSocketRequest.RequestPayloadInput.builder().text(text).build())
						.build())
				.build();
	}

	private WebSocketRequest buildFinishTaskRequest(String taskId, String streamingMode) {
		return WebSocketRequest.builder()
				.header(WebSocketRequest.RequestHeader.builder()
						.action(EventType.FINISH_TASK)
						.taskId(taskId)
						.streaming(streamingMode)
						.build())
				.payload(WebSocketRequest.RequestPayload.builder()
						.input(WebSocketRequest.RequestPayloadInput.builder().build())
						.build())
				.build();
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {

		private String websocketUrl = DashScopeAudioApiConstants.DEFAULT_WEBSOCKET_URL;
		private ApiKey apiKey;
		private String workSpaceId;
		private DashScopeWebSocketClientOptions options;

		public Builder websocketUrl(String websocketUrl) {
			this.websocketUrl = websocketUrl;
			return this;
		}

		public Builder apiKey(ApiKey apiKey) {
			this.apiKey = apiKey;
			return this;
		}

		public Builder workSpaceId(String workSpaceId) {
			this.workSpaceId = workSpaceId;
			return this;
		}

		public Builder options(DashScopeWebSocketClientOptions options) {
			this.options = options;
			return this;
		}

		public DashScopeWebSocketTTSApi build() {
			if (options == null && apiKey != null) {
				options = DashScopeWebSocketClientOptions.builder()
						.apiKey(apiKey.getValue())
						.workSpaceId(workSpaceId)
						.url(websocketUrl)
						.build();
			}
			return new DashScopeWebSocketTTSApi(websocketUrl, apiKey, workSpaceId, options);
		}
	}
}
