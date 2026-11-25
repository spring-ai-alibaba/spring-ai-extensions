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

import java.nio.ByteBuffer;

import com.alibaba.cloud.ai.dashscope.api.DashScopeAudioTranscriptionApi;
import com.alibaba.cloud.ai.dashscope.spec.DashScopeModel.AudioModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.model.SimpleApiKey;
import reactor.core.publisher.Flux;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for model validation in streamRecognition, streamTranslation, streamTranslationChat.
 * These tests do not require DASHSCOPE_API_KEY.
 */
class DashScopeAudioTranscriptionModelValidationTest {

	private DashScopeAudioTranscriptionModel transcriptionModel;

	@BeforeEach
	void setUp() {
		DashScopeAudioTranscriptionApi api = DashScopeAudioTranscriptionApi.builder()
				.apiKey(new SimpleApiKey("dummy-key-for-validation-only"))
				.build();

		transcriptionModel = DashScopeAudioTranscriptionModel.builder()
				.audioTranscriptionApi(api)
				.defaultOptions(DashScopeAudioTranscriptionOptions.builder().build())
				.build();
	}

	@Test
	void streamRecognition_rejectsUnsupportedModel() {
		DashScopeAudioTranscriptionOptions options = DashScopeAudioTranscriptionOptions.builder()
				.model(AudioModel.GUMMY_CHAT_V1.getValue())
				.sampleRate(16000)
				.format("pcm")
				.build();

		Flux<ByteBuffer> audioStream = Flux.just(ByteBuffer.wrap(new byte[3200]));

		assertThatThrownBy(() -> transcriptionModel.streamRecognition(audioStream, options).blockLast())
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("streamRecognition only supports models")
				.hasMessageContaining("gummy-chat-v1");
	}

	@Test
	void streamTranslation_rejectsUnsupportedModel() {
		DashScopeAudioTranscriptionOptions options = DashScopeAudioTranscriptionOptions.builder()
				.model(AudioModel.PARAFORMER_REALTIME_V2.getValue())
				.sampleRate(16000)
				.format("pcm")
				.build();

		Flux<ByteBuffer> audioStream = Flux.just(ByteBuffer.wrap(new byte[3200]));

		assertThatThrownBy(() -> transcriptionModel.streamTranslation(audioStream, options).blockLast())
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("streamTranslation only supports models")
				.hasMessageContaining("paraformer-realtime-v2");
	}

	@Test
	void streamTranslationChat_rejectsUnsupportedModel() {
		DashScopeAudioTranscriptionOptions options = DashScopeAudioTranscriptionOptions.builder()
				.model(AudioModel.GUMMY_REALTIME_V1.getValue())
				.sampleRate(16000)
				.format("wav")
				.build();

		Flux<ByteBuffer> audioStream = Flux.just(ByteBuffer.wrap(new byte[3200]));

		assertThatThrownBy(() -> transcriptionModel.streamTranslationChat(audioStream, options).blockLast())
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("streamTranslationChat only supports models")
				.hasMessageContaining("gummy-realtime-v1");
	}
}
