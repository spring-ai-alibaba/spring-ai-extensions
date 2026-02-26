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
package com.alibaba.cloud.ai.dashscope.audio.transcription;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Objects;

import com.alibaba.cloud.ai.dashscope.api.DashScopeAudioTranscriptionApi;
import com.alibaba.cloud.ai.dashscope.audio.transcription.DashScopeTranscriptionResponse.DashScopeAudioTranscription;
import com.alibaba.cloud.ai.dashscope.common.DashScopeAudioApiConstants;
import com.alibaba.cloud.ai.dashscope.metadata.audio.DashScopeAudioTranscriptionResponseMetadata.Sentence;
import com.alibaba.cloud.ai.dashscope.metadata.audio.DashScopeAudioTranscriptionResponseMetadata.Translation;
import com.alibaba.cloud.ai.dashscope.metadata.audio.DashScopeAudioTranscriptionResponseMetadata.Usage;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.audio.transcription.AudioTranscriptionOptions;
import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.audio.transcription.AudioTranscriptionResponse;
import org.springframework.ai.model.ModelOptionsUtils;
import org.springframework.ai.retry.RetryUtils;
import org.springframework.ai.util.JacksonUtils;
import org.springframework.retry.support.RetryTemplate;
import reactor.core.publisher.Flux;

/**
 * Audio transcription: Input audio, output text.
 *
 * @author xuguan, yingzi
 */
public class DashScopeAudioTranscriptionModel implements AudioTranscriptionModel {

	private static final Logger logger = LoggerFactory.getLogger(DashScopeAudioTranscriptionModel.class);

	private final DashScopeAudioTranscriptionApi audioTranscriptionApi;

	private final DashScopeAudioTranscriptionOptions defaultOptions;

	private final RetryTemplate retryTemplate;

    private final ObjectMapper mapper;

	public DashScopeAudioTranscriptionModel(DashScopeAudioTranscriptionApi api,
			DashScopeAudioTranscriptionOptions defaultOptions) {

		this(api, defaultOptions, RetryUtils.DEFAULT_RETRY_TEMPLATE);
	}

	public DashScopeAudioTranscriptionModel(DashScopeAudioTranscriptionApi api,
			DashScopeAudioTranscriptionOptions defaultOptions, RetryTemplate retryTemplate) {

		this.audioTranscriptionApi = Objects.requireNonNull(api, "api must not be null");
		this.defaultOptions = Objects.requireNonNull(defaultOptions, "options must not be null");
		this.retryTemplate = Objects.requireNonNull(retryTemplate, "retryTemplate must not be null");
        this.mapper = JsonMapper.builder()
                // Deserialization configuration
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                // Serialization configuration
                .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .serializationInclusion(JsonInclude.Include.NON_NULL)
                // Register standard Jackson modules (Jdk8, JavaTime, ParameterNames, Kotlin)
                .addModules(JacksonUtils.instantiateAvailableModules())
                .build();
	}

	@Override
	public AudioTranscriptionResponse call(AudioTranscriptionPrompt prompt) {
        DashScopeAudioTranscriptionOptions options = this.mergeOptions(prompt);
        if (DashScopeAudioApiConstants.isLiveTranslate(options.getModel())) {
            // prompt 类型强转判断
            if (!(prompt instanceof DashScopeAudioTranscriptionPrompt)) {
                throw new IllegalArgumentException("Prompt type is not DashScopeAudioTranscriptionPrompt.");
            }
            return audioTranscriptionApi.callLiveTranslate(
                    (DashScopeAudioTranscriptionPrompt) prompt,
                    options);
        }

        // 录音文件识别Paraformer、Fun-ASR
        if (DashScopeAudioApiConstants.isAsr(options.getModel())) {
            // prompt 类型强转判断
            if (!(prompt instanceof DashScopeAudioTranscriptionPrompt)) {
                throw new IllegalArgumentException("Prompt type is not DashScopeAudioTranscriptionPrompt.");
            }
            return audioTranscriptionApi.callAsr((DashScopeAudioTranscriptionPrompt) prompt, options);
        }

        // 录音文件识别Qwen-ASR
        if (DashScopeAudioApiConstants.isQwenAsr(options.getModel())) {
            // prompt 类型强转判断
            if (!(prompt instanceof DashScopeAudioTranscriptionPrompt)) {
                throw new IllegalArgumentException("Prompt type is not DashScopeAudioTranscriptionPrompt.");
            }
            return audioTranscriptionApi.callQwenAsr((DashScopeAudioTranscriptionPrompt) prompt, options);
        }

        throw new IllegalArgumentException("Model " + options.getModel() + " is not supported call method.");
	}

	@Override
	public Flux<AudioTranscriptionResponse> stream(AudioTranscriptionPrompt prompt) {
        DashScopeAudioTranscriptionOptions options = this.mergeOptions(prompt);
        if (DashScopeAudioApiConstants.isLiveTranslate(options.getModel())) {
            // prompt 类型强转判断
            if (!(prompt instanceof DashScopeAudioTranscriptionPrompt)) {
                throw new IllegalArgumentException("Prompt type is not DashScopeAudioTranscriptionPrompt.");
            }
            return audioTranscriptionApi.streamLiveTranslate(
                    (DashScopeAudioTranscriptionPrompt) prompt,
                    options);
        }
        // 录音文件识别Qwen-ASR
        if (DashScopeAudioApiConstants.isQwenAsr(options.getModel())) {
            // prompt 类型强转判断
            if (!(prompt instanceof DashScopeAudioTranscriptionPrompt)) {
                throw new IllegalArgumentException("Prompt type is not DashScopeAudioTranscriptionPrompt.");
            }
            return audioTranscriptionApi.streamQwenAsr((DashScopeAudioTranscriptionPrompt) prompt, options);
        }

        // 下面是websocket任务
        byte[] audioBytes = null;
        try {
            audioBytes = prompt.getInstructions().getInputStream().readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ByteBuffer binaryData = ByteBuffer.wrap(audioBytes);
        return audioTranscriptionApi.createWebSocketTask(binaryData, options)
                .map(response -> parseWebSocketResponse(response, options.getModel()));
    }

	// ==================== Bidirectional Streaming Methods ====================

	/**
	 * Bidirectional streaming recognition for Paraformer / Fun-ASR real-time models.
	 * <p>
	 * Audio is streamed incrementally (e.g., from a microphone) while recognition results
	 * are returned as they are produced. Returns rich {@link RecognitionResult} with full
	 * sentence, stash (intermediate), word timestamps, and VAD information.
	 *
	 * @param audioStream streaming audio input (PCM chunks, typically 16kHz mono)
	 * @param options transcription options (model, format, sample rate, etc.)
	 * @return stream of recognition results
	 * @throws IllegalArgumentException if the model is not a Paraformer/Fun-ASR realtime model
	 */
	public Flux<RecognitionResult> streamRecognition(Flux<ByteBuffer> audioStream,
			DashScopeAudioTranscriptionOptions options) {
		DashScopeAudioTranscriptionOptions mergedOptions = mergeOptions(options);
		if (!DashScopeAudioApiConstants.PARAFORMER_REALTIME_ONLY_LIST.contains(mergedOptions.getModel())) {
			throw new IllegalArgumentException(
					"streamRecognition only supports models: " + DashScopeAudioApiConstants.PARAFORMER_REALTIME_ONLY_LIST
							+ ", got: " + mergedOptions.getModel());
		}
		return audioTranscriptionApi.createWebSocketStreamingTask(audioStream, mergedOptions)
				.map(this::parseRecognitionResult);
	}

	/**
	 * Bidirectional streaming translation for Gummy long-speech (realtime) models.
	 * <p>
	 * Audio is streamed incrementally while both source-language transcription and
	 * target-language translations are returned. Suitable for continuous long-form audio
	 * such as meetings or lectures.
	 *
	 * @param audioStream streaming audio input (PCM chunks, typically 16kHz mono)
	 * @param options transcription options (model, source/target languages, etc.)
	 * @return stream of translation-recognition results
	 * @throws IllegalArgumentException if the model is not a Gummy realtime model
	 */
	public Flux<TranslationRecognitionResult> streamTranslation(Flux<ByteBuffer> audioStream,
			DashScopeAudioTranscriptionOptions options) {
		DashScopeAudioTranscriptionOptions mergedOptions = mergeOptions(options);
		if (!DashScopeAudioApiConstants.GUMMY_REALTIME_LIST.contains(mergedOptions.getModel())) {
			throw new IllegalArgumentException(
					"streamTranslation only supports models: " + DashScopeAudioApiConstants.GUMMY_REALTIME_LIST
							+ ", got: " + mergedOptions.getModel());
		}
		return audioTranscriptionApi.createWebSocketStreamingTask(audioStream, mergedOptions)
				.map(this::parseTranslationRecognitionResult);
	}

	/**
	 * Bidirectional streaming translation for Gummy short-speech (chat) models.
	 * <p>
	 * Processes a single sentence: after a sentence-end is detected, subsequent audio
	 * frames are ignored. Suitable for voice assistants and short utterance translation.
	 *
	 * @param audioStream streaming audio input (PCM chunks, typically 16kHz mono)
	 * @param options transcription options (model, source/target languages, etc.)
	 * @return stream of translation-recognition results (completes after sentence-end)
	 * @throws IllegalArgumentException if the model is not a Gummy chat model
	 */
	public Flux<TranslationRecognitionResult> streamTranslationChat(Flux<ByteBuffer> audioStream,
			DashScopeAudioTranscriptionOptions options) {
		DashScopeAudioTranscriptionOptions mergedOptions = mergeOptions(options);
		if (!DashScopeAudioApiConstants.GUMMY_CHAT_LIST.contains(mergedOptions.getModel())) {
			throw new IllegalArgumentException(
					"streamTranslationChat only supports models: " + DashScopeAudioApiConstants.GUMMY_CHAT_LIST
							+ ", got: " + mergedOptions.getModel());
		}
		// Chat mode: take results until sentence-end, then complete the stream
		return audioTranscriptionApi.createWebSocketStreamingTask(audioStream, mergedOptions)
				.map(this::parseTranslationRecognitionResult)
				.takeUntil(TranslationRecognitionResult::isSentenceEnd);
	}

	// ==================== Option Merging ====================

	private DashScopeAudioTranscriptionOptions mergeOptions(AudioTranscriptionPrompt prompt) {
        DashScopeAudioTranscriptionOptions options = DashScopeAudioTranscriptionOptions.builder().build();
        DashScopeAudioTranscriptionOptions runtimeOptions = ModelOptionsUtils.copyToTarget(prompt.getOptions(), AudioTranscriptionOptions.class, DashScopeAudioTranscriptionOptions.class);

        options = ModelOptionsUtils.merge(runtimeOptions, options, DashScopeAudioTranscriptionOptions.class);

        return ModelOptionsUtils.merge(options, this.defaultOptions, DashScopeAudioTranscriptionOptions.class);
	}

	private DashScopeAudioTranscriptionOptions mergeOptions(DashScopeAudioTranscriptionOptions options) {
		if (options == null) {
			return this.defaultOptions;
		}
		return ModelOptionsUtils.merge(options, this.defaultOptions, DashScopeAudioTranscriptionOptions.class);
	}

	// ==================== WebSocket Response Parsing ====================

	/**
	 * Parse WebSocket response for the existing {@code stream()} method (backward compatible).
	 */
	private AudioTranscriptionResponse parseWebSocketResponse(String response, String model) {
		try {
			logger.debug("Raw WebSocket response: {}", response);
			JsonNode jsonNode = mapper.readTree(response).get("payload").get("output");
			if (DashScopeAudioApiConstants.QWEN3_LONG_SHORT_TRANSLATE_LIST.contains(model)) {
				JsonNode translationsNode = jsonNode.get("translations");
				JsonNode transcriptionNode = jsonNode.get("transcription");
				List<Translation> translations = mapper.convertValue(translationsNode, new TypeReference<>() {});
				DashScopeAudioTranscription transcription = mapper.convertValue(transcriptionNode, new TypeReference<>() {});
				return new DashScopeTranscriptionResponse(translations, transcription);
			}
			if (DashScopeAudioApiConstants.PARAFORMER_FUNAS_LIST.contains(model)) {
				JsonNode sentenceNode = jsonNode.get("sentence");
				JsonNode usageNode = jsonNode.get("usage");
				Sentence sentence = mapper.convertValue(sentenceNode, new TypeReference<>() {});
				Usage usage = mapper.convertValue(usageNode, new TypeReference<>() {});
				return new DashScopeTranscriptionResponse(sentence, usage);
			}
			throw new IllegalArgumentException("Model " + model + " is not supported for WebSocket response parsing.");
		}
		catch (JsonProcessingException e) {
			logger.error("Failed to parse WebSocket response: {}", response, e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * Parse WebSocket response into rich {@link RecognitionResult} for Paraformer/Fun-ASR.
	 */
	private RecognitionResult parseRecognitionResult(String response) {
		try {
			logger.debug("Raw WebSocket response (recognition): {}", response);
			JsonNode outputNode = mapper.readTree(response).get("payload").get("output");
			return mapper.convertValue(outputNode, RecognitionResult.class);
		}
		catch (JsonProcessingException e) {
			logger.error("Failed to parse recognition result: {}", response, e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * Parse WebSocket response into rich {@link TranslationRecognitionResult} for Gummy.
	 */
	private TranslationRecognitionResult parseTranslationRecognitionResult(String response) {
		try {
			logger.debug("Raw WebSocket response (translation): {}", response);
			JsonNode outputNode = mapper.readTree(response).get("payload").get("output");
			return mapper.convertValue(outputNode, TranslationRecognitionResult.class);
		}
		catch (JsonProcessingException e) {
			logger.error("Failed to parse translation recognition result: {}", response, e);
			throw new RuntimeException(e);
		}
	}
    /**
     * Returns a builder pre-populated with the current configuration for mutation.
     */
    public Builder mutate() {
        return new Builder(this);
    }

    @Override
    public DashScopeAudioTranscriptionModel clone() {
        return this.mutate().build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private DashScopeAudioTranscriptionApi audioTranscriptionApi;

        private DashScopeAudioTranscriptionOptions defaultOptions = DashScopeAudioTranscriptionOptions.builder().build();;

        private RetryTemplate retryTemplate = RetryUtils.DEFAULT_RETRY_TEMPLATE;

        private Builder() {
        }

        private Builder(DashScopeAudioTranscriptionModel model) {
            this.audioTranscriptionApi = model.audioTranscriptionApi;
            this.defaultOptions = model.defaultOptions;
            this.retryTemplate = model.retryTemplate;
        }

        public Builder audioTranscriptionApi(DashScopeAudioTranscriptionApi audioTranscriptionApi) {
            this.audioTranscriptionApi = audioTranscriptionApi;
            return this;
        }

        public Builder defaultOptions(DashScopeAudioTranscriptionOptions defaultOptions) {
            this.defaultOptions = defaultOptions;
            return this;
        }

        public Builder retryTemplate(RetryTemplate retryTemplate) {
            this.retryTemplate = retryTemplate;
            return this;
        }

        public DashScopeAudioTranscriptionModel build() {
            return new DashScopeAudioTranscriptionModel(this.audioTranscriptionApi, this.defaultOptions, this.retryTemplate);
        }
    }

}
