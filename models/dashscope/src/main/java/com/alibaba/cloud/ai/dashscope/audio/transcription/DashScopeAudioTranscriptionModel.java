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
        return audioTranscriptionApi.createWebSocketTask(binaryData, options).map(
                response -> {
                    try {
                        logger.debug("Raw WebSocket response: {}", response);
                        JsonNode jsonNode = mapper.readTree(response).get("payload").get("output");
                        if (DashScopeAudioApiConstants.QWEN3_LONG_SHORT_TRANSLATE_LIST.contains(options.getModel())) {
                            JsonNode translationsNode = jsonNode.get("translations");
                            JsonNode transcriptionNode = jsonNode.get("transcription");
                            logger.debug("translationsNode: {}", translationsNode);
                            logger.debug("transcriptionNode: {}", transcriptionNode);

                            List<Translation> translations = mapper.convertValue(translationsNode, new TypeReference<>() {});
                            DashScopeAudioTranscription transcription = mapper.convertValue(transcriptionNode, new TypeReference<>() {});
                            return new DashScopeTranscriptionResponse(translations, transcription);
                        } else if (DashScopeAudioApiConstants.PARAFORMER_FUNAS_LIST.contains(options.getModel())) {
                            JsonNode sentenceNode = jsonNode.get("sentence");
                            JsonNode usageNode = jsonNode.get("usage");

                            logger.debug("sentenceNode: {}", sentenceNode);
                            logger.debug("usageNode: {}", usageNode);

                            Sentence sentence = mapper.convertValue(sentenceNode, new TypeReference<>() {});
                            Usage usage = mapper.convertValue(usageNode, new TypeReference<>() {});
                            return new DashScopeTranscriptionResponse(sentence, usage);
                        } else {
                            throw new IllegalArgumentException("Model " + options.getModel() + " is not supported stream method.");
                        }

                    } catch (JsonProcessingException e) {
                        logger.error("Failed to parse WebSocket response: {}", response, e);
                        throw new RuntimeException(e);
                    }
                }
        );
    }

	private DashScopeAudioTranscriptionOptions mergeOptions(AudioTranscriptionPrompt prompt) {
        DashScopeAudioTranscriptionOptions options = DashScopeAudioTranscriptionOptions.builder().build();
        DashScopeAudioTranscriptionOptions runtimeOptions = ModelOptionsUtils.copyToTarget(prompt.getOptions(), AudioTranscriptionOptions.class, DashScopeAudioTranscriptionOptions.class);

        options = ModelOptionsUtils.merge(runtimeOptions, options, DashScopeAudioTranscriptionOptions.class);

        return ModelOptionsUtils.merge(options, this.defaultOptions, DashScopeAudioTranscriptionOptions.class);
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
