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
package com.alibaba.cloud.ai.dashscope.api.asr;

import java.nio.ByteBuffer;
import java.util.UUID;

import com.alibaba.cloud.ai.dashscope.audio.DashScopeWebSocketClient;
import com.alibaba.cloud.ai.dashscope.audio.DashScopeWebSocketClient.EventType;
import com.alibaba.cloud.ai.dashscope.audio.WebSocketRequest;
import com.alibaba.cloud.ai.dashscope.audio.transcription.DashScopeAudioTranscriptionOptions;
import com.alibaba.cloud.ai.dashscope.protocol.DashScopeWebSocketClientOptions;
import tools.jackson.core.JacksonException;
import org.jspecify.annotations.Nullable;
import reactor.core.publisher.Flux;
import tools.jackson.databind.json.JsonMapper;

/**
 * DashScope WebSocket ASR API for Paraformer, Fun-ASR, and Gummy models.
 * Uses run-task / binary / finish-task protocol (same as dashscope-sdk Recognition and TranslationRecognizer).
 *
 * @author spring-ai-alibaba
 */
public class DashScopeWebSocketAsrApi {

	private final DashScopeWebSocketClientOptions clientOptions;
	private final JsonMapper jsonMapper;

	public DashScopeWebSocketAsrApi(@Nullable DashScopeWebSocketClientOptions options) {
		this.clientOptions = options != null ? options : DashScopeWebSocketClientOptions.builder().build();
		this.jsonMapper = JsonMapper.shared();
	}

	/**
	 * Create a new WebSocket client for each call to ensure thread-safety.
	 */
	private DashScopeWebSocketClient newWebSocketClient() {
		return new DashScopeWebSocketClient(this.clientOptions);
	}

	/**
	 * One-shot binary input: run-task → task-started → binary data → finish-task.
	 * Suitable for pre-recorded audio (e.g., from file or Resource).
	 */
	public Flux<String> stream(ByteBuffer binaryData, DashScopeAudioTranscriptionOptions options) {
		String taskId = UUID.randomUUID().toString();
		WebSocketRequest runTaskRequest = buildRunTaskRequest(taskId, options);
		WebSocketRequest finishTaskRequest = buildFinishTaskRequest(taskId);

		try {
			String runTaskMessage = jsonMapper.writeValueAsString(runTaskRequest);
			String finishTaskMessage = jsonMapper.writeValueAsString(finishTaskRequest);
			DashScopeWebSocketClient client = newWebSocketClient();
			return client.command(runTaskMessage, binaryData, finishTaskMessage);
		}
		catch (JacksonException e) {
			return Flux.error(e);
		}
	}

	/**
	 * Bidirectional streaming: run-task → task-started → N * sendBinary(chunk) → finish-task.
	 * Suitable for real-time microphone input.
	 */
	public Flux<String> stream(Flux<ByteBuffer> audioStream, DashScopeAudioTranscriptionOptions options) {
		String taskId = UUID.randomUUID().toString();
		WebSocketRequest runTaskRequest = buildRunTaskRequest(taskId, options);
		WebSocketRequest finishTaskRequest = buildFinishTaskRequest(taskId);

		try {
			String runTaskMessage = jsonMapper.writeValueAsString(runTaskRequest);
			String finishTaskMessage = jsonMapper.writeValueAsString(finishTaskRequest);
			DashScopeWebSocketClient client = newWebSocketClient();
			return client.commandStreaming(runTaskMessage, finishTaskMessage, audioStream);
		}
		catch (JacksonException e) {
			return Flux.error(e);
		}
	}

	private WebSocketRequest buildRunTaskRequest(String taskId, DashScopeAudioTranscriptionOptions options) {
		WebSocketRequest.RequestPayloadParameters parameters = WebSocketRequest.RequestPayloadParameters.builder()
				.sampleRate(options.getSampleRate())
				.format(options.getFormat())
				.vocabularyId(options.getVocabularyId())
				.sourceLanguage(options.getSourceLanguage())
				.transcriptionEnabled(options.getTranscriptionEnabled())
				.translationEnabled(options.getTranslationEnabled())
				.translationTargetLanguages(options.getTranslationTargetLanguages())
				.maxEndSilence(options.getMaxEndSilence())
				.multiThresholdModeEnabled(options.getMultiThresholdModeEnabled())
				.punctuationPredictionEnabled(options.getPunctuationPredictionEnabled())
				.heartbeat(options.getHeartbeat())
				.inverseTextNormalizationEnabled(options.getInverseTextNormalizationEnabled())
				.disfluencyRemovalEnabled(options.getDisfluencyRemovalEnabled())
				.languageHints(options.getLanguageHints())
				.build();

		WebSocketRequest.RequestHeader header = WebSocketRequest.RequestHeader.builder()
				.action(EventType.RUN_TASK)
				.taskId(taskId)
				.streaming("duplex")
				.build();
		WebSocketRequest.RequestPayload payload = WebSocketRequest.RequestPayload.builder()
				.model(options.getModel())
				.task("asr")
				.function("recognition")
				.taskGroup("audio")
				.input(WebSocketRequest.RequestPayloadInput.builder().build())
				.parameters(parameters)
				.resources(options.getResources())
				.build();
		return new WebSocketRequest(header, payload);
	}

	private WebSocketRequest buildFinishTaskRequest(String taskId) {
		WebSocketRequest.RequestHeader header = WebSocketRequest.RequestHeader.builder()
				.action(EventType.FINISH_TASK)
				.taskId(taskId)
				.streaming("duplex")
				.build();
		WebSocketRequest.RequestPayload payload = WebSocketRequest.RequestPayload.builder()
				.input(WebSocketRequest.RequestPayloadInput.builder().build())
				.build();
		return new WebSocketRequest(header, payload);
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {

		private @Nullable DashScopeWebSocketClientOptions options;

		public Builder options(@Nullable DashScopeWebSocketClientOptions options) {
			this.options = options;
			return this;
		}

		public DashScopeWebSocketAsrApi build() {
			return new DashScopeWebSocketAsrApi(options);
		}
	}
}
