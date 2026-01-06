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
package com.alibaba.cloud.ai.dashscope.audio;

import com.alibaba.cloud.ai.dashscope.api.DashScopeAudioTranscriptionApi;
import com.alibaba.cloud.ai.dashscope.audio.transcription.AudioTranscriptionModel;
import com.alibaba.cloud.ai.dashscope.common.DashScopeApiConstants;
import com.alibaba.cloud.ai.dashscope.common.DashScopeException;
import com.alibaba.cloud.ai.dashscope.protocol.DashScopeWebSocketClient;
import com.alibaba.cloud.ai.dashscope.spec.DashScopeModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.audio.transcription.AudioTranscription;
import org.springframework.ai.audio.transcription.AudioTranscriptionOptions;
import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.audio.transcription.AudioTranscriptionResponse;
import org.springframework.ai.audio.transcription.AudioTranscriptionResponseMetadata;
import org.springframework.ai.model.ModelOptionsUtils;
import org.springframework.ai.retry.RetryUtils;
import org.springframework.ai.retry.TransientAiException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Audio transcription: Input audio, output text.
 *
 * @author xuguan
 */
public class DashScopeAudioTranscriptionModel implements AudioTranscriptionModel {

	private static final Logger logger = LoggerFactory.getLogger(DashScopeAudioTranscriptionModel.class);

	private final DashScopeAudioTranscriptionApi audioTranscriptionApi;

	private final DashScopeAudioTranscriptionOptions defaultOptions;

	private final RetryTemplate retryTemplate;

	public DashScopeAudioTranscriptionModel(DashScopeAudioTranscriptionApi api,
			DashScopeAudioTranscriptionOptions defaultOptions) {

		this(api, defaultOptions, RetryUtils.DEFAULT_RETRY_TEMPLATE);
	}

	public DashScopeAudioTranscriptionModel(DashScopeAudioTranscriptionApi api,
			DashScopeAudioTranscriptionOptions defaultOptions, RetryTemplate retryTemplate) {

		this.audioTranscriptionApi = Objects.requireNonNull(api, "api must not be null");
		this.defaultOptions = Objects.requireNonNull(defaultOptions, "options must not be null");
		this.retryTemplate = Objects.requireNonNull(retryTemplate, "retryTemplate must not be null");
	}

	@Override
	public AudioTranscriptionResponse call(AudioTranscriptionPrompt prompt) {
		DashScopeAudioTranscriptionApi.Request request = this.createRequest(prompt);

		ResponseEntity<DashScopeAudioTranscriptionApi.Response> submitResponse = this.audioTranscriptionApi.submitTask(request);

		String taskId = Optional.ofNullable(submitResponse)
			.map(ResponseEntity::getBody)
			.map(DashScopeAudioTranscriptionApi.Response::output)
			.map(DashScopeAudioTranscriptionApi.Response.Output::taskId)
			.orElse(null);

		if (taskId == null) {
			logger.warn("No taskId returned for request: {}", request);
			AudioTranscriptionResponseMetadata metadata = new AudioTranscriptionResponseMetadata();
			metadata.put("taskStatus", "NO_TASK_ID");
			return new AudioTranscriptionResponse(new AudioTranscription(null), metadata);
		}

		AudioTranscriptionResponse response = this.retryTemplate
			.execute(ctx -> {
				DashScopeAudioTranscriptionApi.Response taskResultResponse = this.audioTranscriptionApi.queryTaskResult(taskId)
					.getBody();

				DashScopeAudioTranscriptionApi.TaskStatus taskStatus = Optional.ofNullable(taskResultResponse)
					.map(DashScopeAudioTranscriptionApi.Response::output)
					.map(DashScopeAudioTranscriptionApi.Response.Output::taskStatus)
					.orElse(null);

				if (taskStatus == null) {
					logger.warn("No taskStatus returned for request: {}", request);
					AudioTranscriptionResponseMetadata metadata = new AudioTranscriptionResponseMetadata();
					metadata.put("taskStatus", "NO_TASK_STATUS");
					return new AudioTranscriptionResponse(new AudioTranscription(null), metadata);
				}

				switch (taskStatus) {
					case FAILED, CANCELED, UNKNOWN -> {
						logger.error("task failed");
						return this.toResponse(taskResultResponse);
					}
					case SUCCEEDED -> {
						logger.info("task succeeded");
						return this.toResponse(taskResultResponse);
					}
					default -> throw new TransientAiException("Audio generation still pending");
				}
			});

		return response;
	}

	@Override
	public Flux<AudioTranscriptionResponse> stream(AudioTranscriptionPrompt prompt) {
		String taskId = UUID.randomUUID().toString();
		DashScopeAudioTranscriptionApi.RealtimeRequest runTaskRequest = this.createRealtimeRequest(prompt, taskId,
			DashScopeWebSocketClient.EventType.RUN_TASK);

		logger.info("send run-task, taskId={}", taskId);
		this.audioTranscriptionApi.realtimeSendTask(runTaskRequest);

		Resource resource = prompt.getInstructions();

		Flux<ByteBuffer> audio = DataBufferUtils.read(resource, new DefaultDataBufferFactory(), 16384)
			.map(dataBuffer -> {
				try {
					byte[] bytes = new byte[dataBuffer.readableByteCount()];
					dataBuffer.read(bytes);
					return ByteBuffer.wrap(bytes);
				} finally {
					DataBufferUtils.release(dataBuffer);
				}
			})
			.delayElements(Duration.ofMillis(100), Schedulers.boundedElastic())
			.doOnComplete(() -> {
				DashScopeAudioTranscriptionApi.RealtimeRequest finishTaskRequest = this.createRealtimeRequest(prompt,
					taskId, DashScopeWebSocketClient.EventType.FINISH_TASK);

				logger.info("send finish-task, taskId={}", taskId);
				this.audioTranscriptionApi.realtimeSendTask(finishTaskRequest);
			});

		return this.audioTranscriptionApi.realtimeStream(audio).map(this::toResponse);
	}

	private DashScopeAudioTranscriptionApi.Request createRequest(AudioTranscriptionPrompt prompt) {
		DashScopeAudioTranscriptionOptions options = this.mergeOptions(prompt);

		List<String> fileUrls = List.of();
		try {
			if (prompt.getInstructions() != null) {
				fileUrls = List.of(prompt.getInstructions().getURL().toString());
			}
		} catch (IOException e) {
			throw new DashScopeException("failed to get file urls", e);
		}

		String model = options.getModel();
		String vocabularyId = options.getVocabularyId();
		List<DashScopeAudioTranscriptionApi.Request.Resource> resources = null;
		if (DashScopeModel.AudioModel.PARAFORMER_V1.getValue().equals(model) ||
			DashScopeModel.AudioModel.PARAFORMER_8K_V1.getValue().equals(model) ||
			DashScopeModel.AudioModel.PARAFORMER_MTL_V1.getValue().equals(model)) {
			vocabularyId = null;
			String resourceId = options.getResourceId();
			if (StringUtils.hasText(resourceId)) {
				resources = List.of(new DashScopeAudioTranscriptionApi.Request.Resource(resourceId,
					"asr_phrase"));
			}
		}

		return new DashScopeAudioTranscriptionApi.Request(model,
			new DashScopeAudioTranscriptionApi.Request.Input(fileUrls),
			resources,
			new DashScopeAudioTranscriptionApi.Request.Parameters(vocabularyId,
				options.getChannelId(), options.getDisfluencyRemovalEnabled(),
				options.getTimestampAlignmentEnabled(), options.getSpecialWordFilter(),
				options.getLanguageHints(), options.getDiarizationEnabled(),
				options.getSpeakerCount()));
	}

	private DashScopeAudioTranscriptionApi.RealtimeRequest createRealtimeRequest(AudioTranscriptionPrompt prompt,
			String taskId, DashScopeWebSocketClient.EventType action) {
		DashScopeAudioTranscriptionOptions options = this.mergeOptions(prompt);

		String model = options.getModel();
		String vocabularyId = options.getVocabularyId();
		List<DashScopeAudioTranscriptionApi.RealtimeRequest.Payload.Resource> resources = null;
		if (DashScopeModel.AudioModel.PARAFORMER_REALTIME_V1.getValue().equals(model) ||
			DashScopeModel.AudioModel.PARAFORMER_REALTIME_8K_V1.getValue().equals(model)) {
			vocabularyId = null;
			String resourceId = options.getResourceId();
			if (StringUtils.hasText(resourceId)) {
				resources = List.of(new DashScopeAudioTranscriptionApi.RealtimeRequest.Payload.Resource(resourceId,
					"asr_phrase"));
			}
		}

		return new DashScopeAudioTranscriptionApi.RealtimeRequest(
			new DashScopeAudioTranscriptionApi.RealtimeRequest.Header(action, taskId, "duplex"),
			new DashScopeAudioTranscriptionApi.RealtimeRequest.Payload(model, "audio",
				"asr", "recognition",
				new DashScopeAudioTranscriptionApi.RealtimeRequest.Payload.Input(),
				new DashScopeAudioTranscriptionApi.RealtimeRequest.Payload.Parameters(options.getFormat(),
					options.getSampleRate(), vocabularyId, options.getDisfluencyRemovalEnabled(),
					options.getLanguageHints(), options.getSemanticPunctuationEnabled(), options.getMaxSentenceSilence(),
					options.getMultiThresholdModeEnabled(), options.getPunctuationPredictionEnabled(),
					options.getHeartbeat(), options.getInverseTextNormalizationEnabled(), options.getSourceLanguage(),
					options.getTranscriptionEnabled(), options.getTranslationEnabled(), options.getTranslationTargetLanguages(),
					options.getMaxEndSilence()), resources));
	}

	private DashScopeAudioTranscriptionOptions mergeOptions(AudioTranscriptionPrompt prompt) {
		DashScopeAudioTranscriptionOptions runtimeOptions = null;

		if (prompt.getOptions() != null) {
			runtimeOptions = ModelOptionsUtils.copyToTarget(prompt.getOptions(),
				AudioTranscriptionOptions.class, DashScopeAudioTranscriptionOptions.class);
		}

		return runtimeOptions == null ? this.defaultOptions
			: ModelOptionsUtils.merge(runtimeOptions, this.defaultOptions, DashScopeAudioTranscriptionOptions.class);
	}

	private AudioTranscriptionResponse toResponse(DashScopeAudioTranscriptionApi.Response apiResponse) {
		DashScopeAudioTranscriptionApi.Response.Output output = apiResponse.output();
		List<DashScopeAudioTranscriptionApi.Response.Output.Result> results = output.results();

		String text = null;
		if (results != null && !results.isEmpty()) {
			String transcriptionUrl = results.get(0).transcriptionUrl();
			DashScopeAudioTranscriptionApi.Outcome outcome = this.audioTranscriptionApi.getOutcome(transcriptionUrl);
			if (!outcome.transcripts().isEmpty()) {
				text = outcome.transcripts().get(0).text();
			}
		}

		AudioTranscription result = new AudioTranscription(text);

		AudioTranscriptionResponseMetadata responseMetadata = new AudioTranscriptionResponseMetadata();
		if (apiResponse.requestId() != null) {
			responseMetadata.put(DashScopeApiConstants.REQUEST_ID, apiResponse.requestId());
		}
		if (apiResponse.usage() != null) {
			responseMetadata.put(DashScopeApiConstants.USAGE, apiResponse.usage());
		}
		responseMetadata.put(DashScopeApiConstants.OUTPUT, output);

		return new AudioTranscriptionResponse(result, responseMetadata);
	}

	private AudioTranscriptionResponse toResponse(DashScopeAudioTranscriptionApi.RealtimeResponse realtimeResponse) {
		String taskId = realtimeResponse.header().taskId();
		DashScopeAudioTranscriptionApi.RealtimeResponse.Payload payload = realtimeResponse.payload();
		DashScopeAudioTranscriptionApi.RealtimeResponse.Payload.Output output = payload.output();

		String text = "";
		Boolean transcriptionSentenceEnd = Optional.of(output)
			.map(DashScopeAudioTranscriptionApi.RealtimeResponse.Payload.Output::transcription)
			.map(DashScopeAudioTranscriptionApi.RealtimeResponse.Payload.Output.Transcription::sentenceEnd)
			.orElse(Boolean.FALSE);
		if (transcriptionSentenceEnd) {
			text = output.transcription().text();
		} else {
			Boolean sentenceSentenceEnd = Optional.of(output)
				.map(DashScopeAudioTranscriptionApi.RealtimeResponse.Payload.Output::sentence)
				.map(DashScopeAudioTranscriptionApi.RealtimeResponse.Payload.Output.Sentence::sentenceEnd)
				.orElse(Boolean.FALSE);
			if (sentenceSentenceEnd) {
				text = output.sentence().text();
			}
		}

		AudioTranscription result = new AudioTranscription(text);

		AudioTranscriptionResponseMetadata responseMetadata = new AudioTranscriptionResponseMetadata();

		responseMetadata.put(DashScopeApiConstants.TASK_ID, taskId);
		responseMetadata.put(DashScopeApiConstants.OUTPUT, output);
		if (payload.usage() != null) {
			responseMetadata.put(DashScopeApiConstants.USAGE, payload.usage());
		}

		return new AudioTranscriptionResponse(result, responseMetadata);
	}

}
