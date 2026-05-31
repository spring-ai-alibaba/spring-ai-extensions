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
package com.alibaba.cloud.ai.dashscope.audio.tts;

import com.alibaba.cloud.ai.dashscope.api.DashScopeAudioSpeechApi;
import com.alibaba.cloud.ai.dashscope.api.tts.DashScopeQwenTTSRealtimeApi;
import com.alibaba.cloud.ai.dashscope.common.DashScopeAudioApiConstants;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.audio.tts.Speech;
import org.springframework.ai.audio.tts.TextToSpeechModel;
import org.springframework.ai.audio.tts.TextToSpeechOptions;
import org.springframework.ai.audio.tts.TextToSpeechPrompt;
import org.springframework.ai.audio.tts.TextToSpeechResponse;
import org.springframework.ai.retry.RetryUtils;
import org.springframework.core.retry.RetryTemplate;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * DashScope TTS model facade. Supports all four TTS modes:
 * <ul>
 *   <li>Qwen-TTS (REST/SSE) — call / stream</li>
 *   <li>Sambert (WebSocket half-duplex) — stream</li>
 *   <li>CosyVoice (WebSocket duplex) — stream / stream with streaming input</li>
 *   <li>Qwen TTS Realtime (WebSocket append/commit) — stream / stream with streaming input</li>
 * </ul>
 * <p>
 * Implements both Spring AI's {@link TextToSpeechModel} (complete text input) and
 * {@link StreamingInputTextToSpeechModel} (streaming text input for CosyVoice / Qwen TTS Realtime).
 *
 * @author kevinlin09, xuguan, yingzi
 */
public class DashScopeAudioSpeechModel implements TextToSpeechModel, StreamingInputTextToSpeechModel {

	private static final Logger logger = LoggerFactory.getLogger(DashScopeAudioSpeechModel.class);

	private final DashScopeAudioSpeechApi audioSpeechApi;

	private final DashScopeAudioSpeechOptions defaultOptions;

	private final RetryTemplate retryTemplate;

	public DashScopeAudioSpeechModel(DashScopeAudioSpeechApi audioSpeechApi) {
		this(audioSpeechApi, DashScopeAudioSpeechOptions.builder()
			.build());
	}

	public DashScopeAudioSpeechModel(DashScopeAudioSpeechApi audioSpeechApi, DashScopeAudioSpeechOptions defaultOptions) {
		this(audioSpeechApi, defaultOptions, RetryUtils.DEFAULT_RETRY_TEMPLATE);
	}

	public DashScopeAudioSpeechModel(DashScopeAudioSpeechApi audioSpeechApi, DashScopeAudioSpeechOptions defaultOptions,
		RetryTemplate retryTemplate) {
		this.audioSpeechApi = audioSpeechApi;
		this.defaultOptions = defaultOptions;
		this.retryTemplate = retryTemplate;
	}

	// ========================= TextToSpeechModel =========================

    @Override
	public TextToSpeechResponse call(TextToSpeechPrompt prompt) {
        DashScopeAudioSpeechOptions options = DashScopeAudioSpeechOptions.builder()
                .from(this.defaultOptions)
                .merge(prompt.getOptions())
                .build();
        String model = options.getModel();
        Assert.notNull(model, "audio speech model must not be null");
        if (DashScopeAudioApiConstants.isQwenTTSModel(model)) {
            return this.audioSpeechApi.callQwenTTS(prompt.getInstructions().getText(), options);
        }
        if (DashScopeAudioApiConstants.isWebsocketByTTSModelName(model)
                || DashScopeAudioApiConstants.isQwenTTSRealtimeModel(model)) {
            throw new IllegalArgumentException("Model " + model
                    + " does not support synchronous call; use stream() instead.");
        }
        throw new IllegalArgumentException("Model " + model + " is not supported.");
	}

	@Override
	public Flux<TextToSpeechResponse> stream(TextToSpeechPrompt prompt) {
        // Merge request options with default options
        DashScopeAudioSpeechOptions options = DashScopeAudioSpeechOptions.builder()
                .from(this.defaultOptions)
                .merge(prompt.getOptions())
                .build();
        String model = options.getModel();
        Assert.notNull(model, "audio speech model must not be null");
        if (DashScopeAudioApiConstants.isQwenTTSModel(model)) {
            return this.audioSpeechApi.streamQwenTTS(prompt.getInstructions().getText(), options)
                    .map(response -> (TextToSpeechResponse) response);
        }

        if (DashScopeAudioApiConstants.isWebsocketByTTSModelName(model)
                || DashScopeAudioApiConstants.isQwenTTSRealtimeModel(model)) {
            return this.audioSpeechApi.createWebSocketTask(prompt.getInstructions().getText(), options)
                    .map(byteBuffer -> {
                        byte[] data = new byte[byteBuffer.remaining()];
                        byteBuffer.get(data);
                        return new TextToSpeechResponse(List.of(new Speech(data)));
                    });
        }

        throw new IllegalArgumentException("Model " + model + " is not supported.");
	}

	// =================== StreamingInputTextToSpeechModel ===================

	@Override
	public Flux<TextToSpeechResponse> stream(Flux<String> textStream, TextToSpeechOptions options) {
        // Merge request options with default options
		DashScopeAudioSpeechOptions dashScopeOptions = DashScopeAudioSpeechOptions.builder()
                .from(this.defaultOptions)
                .merge(options)
                .build();
		String model = dashScopeOptions.getModel();
		Assert.notNull(model, "audio speech model must not be null");

		if (DashScopeAudioApiConstants.isQwenTTSRealtimeModel(model)) {
			DashScopeQwenTTSRealtimeApi realtimeApi = audioSpeechApi.getQwenTTSRealtimeApi();
			return realtimeApi.stream(textStream, dashScopeOptions)
					.map(byteBuffer -> toTextToSpeechResponse(byteBuffer));
		}

		if (DashScopeAudioApiConstants.isCosyVoiceModel(model)) {
			return audioSpeechApi.createWebSocketStreamingTask(textStream, dashScopeOptions)
					.map(byteBuffer -> toTextToSpeechResponse(byteBuffer));
		}

		throw new IllegalArgumentException(
					"Model " + model + " does not support streaming input. "
							+ "Only CosyVoice and Qwen TTS Realtime models support streaming text input.");
	}

	// ========================= Internal helpers =========================

	private static TextToSpeechResponse toTextToSpeechResponse(ByteBuffer byteBuffer) {
		byte[] data = new byte[byteBuffer.remaining()];
		byteBuffer.get(data);
		return new TextToSpeechResponse(List.of(new Speech(data)));
	}

    /**
     * Returns a builder pre-populated with the current configuration for mutation.
     */
    public Builder mutate() {
        return new Builder(this);
    }

    @Override
    public DashScopeAudioSpeechModel clone() {
        return this.mutate().build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

	        private @Nullable DashScopeAudioSpeechApi audioSpeechApi;

        private DashScopeAudioSpeechOptions defaultOptions = DashScopeAudioSpeechOptions.builder().build();

        private RetryTemplate retryTemplate = RetryUtils.DEFAULT_RETRY_TEMPLATE;

        private Builder() {
        }

        private Builder(DashScopeAudioSpeechModel audioSpeechModel) {
            this.audioSpeechApi = audioSpeechModel.audioSpeechApi;
            this.defaultOptions = audioSpeechModel.defaultOptions;
            this.retryTemplate = audioSpeechModel.retryTemplate;
        }

        public Builder audioSpeechApi(DashScopeAudioSpeechApi audioSpeechApi) {
            this.audioSpeechApi = audioSpeechApi;
            return this;
        }

        public Builder defaultOptions(DashScopeAudioSpeechOptions defaultOptions) {
            this.defaultOptions = defaultOptions;
            return this;
        }

        public Builder retryTemplate(RetryTemplate retryTemplate) {
            this.retryTemplate = retryTemplate;
            return this;
        }

        public DashScopeAudioSpeechModel build() {
            DashScopeAudioSpeechApi audioSpeechApi = this.audioSpeechApi;
            Assert.notNull(audioSpeechApi, "audioSpeechApi must not be null");
            return new DashScopeAudioSpeechModel(audioSpeechApi, this.defaultOptions, this.retryTemplate);
        }
    }

}
