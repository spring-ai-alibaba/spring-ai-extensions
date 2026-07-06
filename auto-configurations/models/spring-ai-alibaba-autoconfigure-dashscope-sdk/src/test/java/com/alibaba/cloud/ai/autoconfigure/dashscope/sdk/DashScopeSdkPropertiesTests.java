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

package com.alibaba.cloud.ai.autoconfigure.dashscope.sdk;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

class DashScopeSdkPropertiesTests {

	@Test
	void chatPropertiesBindFlatOptions() {
		DashScopeSdkChatProperties properties = bind(DashScopeSdkChatProperties.CONFIG_PREFIX,
				DashScopeSdkChatProperties.class,
				"spring.ai.dashscope.sdk.chat.model", "qwen-sdk-test",
				"spring.ai.dashscope.sdk.chat.temperature", "0.6",
				"spring.ai.dashscope.sdk.chat.enable-search", "true");

		assertThat(properties.toOptions().getModel()).isEqualTo("qwen-sdk-test");
		assertThat(properties.toOptions().getTemperature()).isEqualTo(0.6d);
		assertThat(properties.toOptions().getEnableSearch()).isTrue();
	}

	@Test
	void chatPropertiesStillBindLegacyOptions() {
		DashScopeSdkChatProperties properties = bind(DashScopeSdkChatProperties.CONFIG_PREFIX,
				DashScopeSdkChatProperties.class,
				"spring.ai.dashscope.sdk.chat.options.model", "legacy-sdk-qwen");

		assertThat(properties.toOptions().getModel()).isEqualTo("legacy-sdk-qwen");
	}

	@Test
	void embeddingPropertiesBindFlatOptions() {
		DashScopeSdkEmbeddingProperties properties = bind(DashScopeSdkEmbeddingProperties.CONFIG_PREFIX,
				DashScopeSdkEmbeddingProperties.class,
				"spring.ai.dashscope.sdk.embedding.model", "embedding-sdk-test",
				"spring.ai.dashscope.sdk.embedding.dimensions", "512",
				"spring.ai.dashscope.sdk.embedding.text-type", "document");

		assertThat(properties.toOptions().getModel()).isEqualTo("embedding-sdk-test");
		assertThat(properties.toOptions().getDimensions()).isEqualTo(512);
		assertThat(properties.toOptions().getTextType()).isEqualTo("document");
	}

	@Test
	void embeddingPropertiesStillBindLegacyOptions() {
		DashScopeSdkEmbeddingProperties properties = bind(DashScopeSdkEmbeddingProperties.CONFIG_PREFIX,
				DashScopeSdkEmbeddingProperties.class,
				"spring.ai.dashscope.sdk.embedding.options.dimensions", "256");

		assertThat(properties.toOptions().getDimensions()).isEqualTo(256);
	}

	@Test
	void imagePropertiesBindFlatOptions() {
		DashScopeSdkImageProperties properties = bind(DashScopeSdkImageProperties.CONFIG_PREFIX,
				DashScopeSdkImageProperties.class,
				"spring.ai.dashscope.sdk.image.model", "wanx-sdk-test",
				"spring.ai.dashscope.sdk.image.n", "3",
				"spring.ai.dashscope.sdk.image.size", "1024*1024");

		assertThat(properties.toOptions().getModel()).isEqualTo("wanx-sdk-test");
		assertThat(properties.toOptions().getN()).isEqualTo(3);
		assertThat(properties.toOptions().getSize()).isEqualTo("1024*1024");
	}

	@Test
	void imagePropertiesStillBindLegacyOptions() {
		DashScopeSdkImageProperties properties = bind(DashScopeSdkImageProperties.CONFIG_PREFIX,
				DashScopeSdkImageProperties.class,
				"spring.ai.dashscope.sdk.image.options.negative-prompt", "legacy-negative");

		assertThat(properties.toOptions().getNegativePrompt()).isEqualTo("legacy-negative");
	}

	@Test
	void audioSpeechPropertiesBindFlatOptions() {
		DashScopeSdkAudioSpeechProperties properties = bind(DashScopeSdkAudioSpeechProperties.CONFIG_PREFIX,
				DashScopeSdkAudioSpeechProperties.class,
				"spring.ai.dashscope.sdk.audio.speech.model", "sambert-test",
				"spring.ai.dashscope.sdk.audio.speech.voice", "zhichu",
				"spring.ai.dashscope.sdk.audio.speech.sample-rate", "16000");

		assertThat(properties.toOptions().getModel()).isEqualTo("sambert-test");
		assertThat(properties.toOptions().getVoice()).isEqualTo("zhichu");
		assertThat(properties.toOptions().getSampleRate()).isEqualTo(16000);
	}

	@Test
	void audioSpeechPropertiesStillBindLegacyOptions() {
		DashScopeSdkAudioSpeechProperties properties = bind(DashScopeSdkAudioSpeechProperties.CONFIG_PREFIX,
				DashScopeSdkAudioSpeechProperties.class,
				"spring.ai.dashscope.sdk.audio.speech.options.voice", "legacy-voice");

		assertThat(properties.toOptions().getVoice()).isEqualTo("legacy-voice");
	}

	@Test
	void audioTranscriptionPropertiesBindFlatFileUrls() {
		DashScopeSdkAudioTranscriptionProperties properties = bind(
				DashScopeSdkAudioTranscriptionProperties.CONFIG_PREFIX,
				DashScopeSdkAudioTranscriptionProperties.class,
				"spring.ai.dashscope.sdk.audio.transcription.model", "paraformer-test",
				"spring.ai.dashscope.sdk.audio.transcription.file-urls[0]", "https://example.com/audio.wav");

		assertThat(properties.toOptions().getModel()).isEqualTo("paraformer-test");
		assertThat(properties.toOptions().getFileUrls()).containsExactly("https://example.com/audio.wav");
	}

	@Test
	void audioTranscriptionPropertiesStillBindLegacyOptions() {
		DashScopeSdkAudioTranscriptionProperties properties = bind(
				DashScopeSdkAudioTranscriptionProperties.CONFIG_PREFIX,
				DashScopeSdkAudioTranscriptionProperties.class,
				"spring.ai.dashscope.sdk.audio.transcription.options.file-urls[0]",
				"https://example.com/legacy.wav");

		assertThat(properties.toOptions().getFileUrls()).containsExactly("https://example.com/legacy.wav");
	}

	private static <T> T bind(String prefix, Class<T> propertiesType, String... pairs) {
		Map<String, String> source = new LinkedHashMap<>();
		for (int i = 0; i < pairs.length; i += 2) {
			source.put(pairs[i], pairs[i + 1]);
		}
		return new Binder(new MapConfigurationPropertySource(source)).bind(prefix, propertiesType).get();
	}

}
