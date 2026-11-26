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
import java.util.List;
import java.util.Map;

import com.alibaba.cloud.ai.dashscope.sdk.chat.DashScopeSdkChatOptions;
import com.alibaba.cloud.ai.dashscope.sdk.embedding.DashScopeSdkEmbeddingOptions;
import com.alibaba.cloud.ai.dashscope.sdk.image.DashScopeSdkImageOptions;
import com.alibaba.cloud.ai.dashscope.sdk.audio.tts.DashScopeSdkAudioSpeechOptions;
import com.alibaba.cloud.ai.dashscope.sdk.audio.transcription.DashScopeSdkAudioTranscriptionOptions;
import org.junit.jupiter.api.Test;

import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

class DashScopeSdkPropertiesTests {

	// ========== Chat ==========

	@Test
	void chatPropertiesBindFlatOptions() {
		DashScopeSdkChatProperties properties = bind(DashScopeSdkChatProperties.CONFIG_PREFIX,
				DashScopeSdkChatProperties.class,
				"spring.ai.dashscope.sdk.chat.model", "qwen-sdk-test",
				"spring.ai.dashscope.sdk.chat.temperature", "0.6",
				"spring.ai.dashscope.sdk.chat.enable-search", "true",
				"spring.ai.dashscope.sdk.chat.stream", "true",
				"spring.ai.dashscope.sdk.chat.seed", "42",
				"spring.ai.dashscope.sdk.chat.top-p", "0.9",
				"spring.ai.dashscope.sdk.chat.top-k", "50",
				"spring.ai.dashscope.sdk.chat.max-tokens", "2048",
				"spring.ai.dashscope.sdk.chat.incremental-output", "true",
				"spring.ai.dashscope.sdk.chat.repetition-penalty", "1.1");

		DashScopeSdkChatOptions options = properties.toOptions();
		assertThat(options).isNotNull();
		assertThat(options.getModel()).isEqualTo("qwen-sdk-test");
		assertThat(options.getTemperature()).isEqualTo(0.6d);
		assertThat(options.getEnableSearch()).isTrue();
		assertThat(options.getStream()).isTrue();
		assertThat(options.getSeed()).isEqualTo(42);
		assertThat(options.getTopP()).isEqualTo(0.9d);
		assertThat(options.getTopK()).isEqualTo(50);
		assertThat(options.getMaxTokens()).isEqualTo(2048);
		assertThat(options.getIncrementalOutput()).isTrue();
		assertThat(options.getRepetitionPenalty()).isEqualTo(1.1d);
	}

	@Test
	void chatPropertiesDefaultValues() {
		DashScopeSdkChatProperties properties = bind(DashScopeSdkChatProperties.CONFIG_PREFIX,
				DashScopeSdkChatProperties.class);

		DashScopeSdkChatOptions options = properties.toOptions();
		assertThat(options).isNotNull();
		assertThat(options.getModel()).isEqualTo(DashScopeSdkChatProperties.DEFAULT_DEPLOYMENT_NAME);
		assertThat(properties.isEnabled()).isTrue();
	}

	@Test
	void chatPropertiesStillBindLegacyOptions() {
		DashScopeSdkChatProperties properties = bind(DashScopeSdkChatProperties.CONFIG_PREFIX,
				DashScopeSdkChatProperties.class,
				"spring.ai.dashscope.sdk.chat.options.model", "legacy-sdk-qwen",
				"spring.ai.dashscope.sdk.chat.options.temperature", "0.6",
				"spring.ai.dashscope.sdk.chat.options.enable-search", "true",
				"spring.ai.dashscope.sdk.chat.options.stream", "true",
				"spring.ai.dashscope.sdk.chat.options.seed", "42",
				"spring.ai.dashscope.sdk.chat.options.top-p", "0.9",
				"spring.ai.dashscope.sdk.chat.options.top-k", "50",
				"spring.ai.dashscope.sdk.chat.options.max-tokens", "2048",
				"spring.ai.dashscope.sdk.chat.options.incremental-output", "true",
				"spring.ai.dashscope.sdk.chat.options.repetition-penalty", "1.1");

		DashScopeSdkChatOptions options = properties.toOptions();
		assertThat(options).isNotNull();
		assertThat(options.getModel()).isEqualTo("legacy-sdk-qwen");
		assertThat(options.getTemperature()).isEqualTo(0.6d);
		assertThat(options.getEnableSearch()).isTrue();
		assertThat(options.getStream()).isTrue();
		assertThat(options.getSeed()).isEqualTo(42);
		assertThat(options.getTopP()).isEqualTo(0.9d);
		assertThat(options.getTopK()).isEqualTo(50);
		assertThat(options.getMaxTokens()).isEqualTo(2048);
		assertThat(options.getIncrementalOutput()).isTrue();
		assertThat(options.getRepetitionPenalty()).isEqualTo(1.1d);
	}

	// ========== Embedding ==========

	@Test
	void embeddingPropertiesBindFlatOptions() {
		DashScopeSdkEmbeddingProperties properties = bind(DashScopeSdkEmbeddingProperties.CONFIG_PREFIX,
				DashScopeSdkEmbeddingProperties.class,
				"spring.ai.dashscope.sdk.embedding.model", "embedding-sdk-test",
				"spring.ai.dashscope.sdk.embedding.dimensions", "512",
				"spring.ai.dashscope.sdk.embedding.text-type", "document");

		DashScopeSdkEmbeddingOptions options = properties.toOptions();
		assertThat(options).isNotNull();
		assertThat(options.getModel()).isEqualTo("embedding-sdk-test");
		assertThat(options.getDimensions()).isEqualTo(512);
		assertThat(options.getTextType()).isEqualTo("document");
	}

	@Test
	void embeddingPropertiesDefaultValues() {
		DashScopeSdkEmbeddingProperties properties = bind(DashScopeSdkEmbeddingProperties.CONFIG_PREFIX,
				DashScopeSdkEmbeddingProperties.class);

		DashScopeSdkEmbeddingOptions options = properties.toOptions();
		assertThat(options).isNotNull();
		assertThat(options.getModel()).isEqualTo("text-embedding-v2");
		assertThat(properties.isEnabled()).isTrue();
		assertThat(properties.getMetadataMode()).isNotNull();
	}

	@Test
	void embeddingPropertiesStillBindLegacyOptions() {
		DashScopeSdkEmbeddingProperties properties = bind(DashScopeSdkEmbeddingProperties.CONFIG_PREFIX,
				DashScopeSdkEmbeddingProperties.class,
				"spring.ai.dashscope.sdk.embedding.options.model", "legacy-embedding",
				"spring.ai.dashscope.sdk.embedding.options.dimensions", "256",
				"spring.ai.dashscope.sdk.embedding.options.text-type", "query");

		DashScopeSdkEmbeddingOptions options = properties.toOptions();
		assertThat(options).isNotNull();
		assertThat(options.getModel()).isEqualTo("legacy-embedding");
		assertThat(options.getDimensions()).isEqualTo(256);
		assertThat(options.getTextType()).isEqualTo("query");
	}

	// ========== Image ==========

	@Test
	void imagePropertiesBindFlatOptions() {
		DashScopeSdkImageProperties properties = bind(DashScopeSdkImageProperties.CONFIG_PREFIX,
				DashScopeSdkImageProperties.class,
				"spring.ai.dashscope.sdk.image.model", "wanx-sdk-test",
				"spring.ai.dashscope.sdk.image.n", "3",
				"spring.ai.dashscope.sdk.image.size", "1024*1024",
				"spring.ai.dashscope.sdk.image.width", "1024",
				"spring.ai.dashscope.sdk.image.height", "1024",
				"spring.ai.dashscope.sdk.image.style", "photography",
				"spring.ai.dashscope.sdk.image.seed", "12345",
				"spring.ai.dashscope.sdk.image.negative-prompt", "blurry",
				"spring.ai.dashscope.sdk.image.response-format", "url",
				"spring.ai.dashscope.sdk.image.async", "true");

		DashScopeSdkImageOptions options = properties.toOptions();
		assertThat(options).isNotNull();
		assertThat(options.getModel()).isEqualTo("wanx-sdk-test");
		assertThat(options.getN()).isEqualTo(3);
		assertThat(options.getSize()).isEqualTo("1024*1024");
		assertThat(options.getWidth()).isEqualTo(1024);
		assertThat(options.getHeight()).isEqualTo(1024);
		assertThat(options.getStyle()).isEqualTo("photography");
		assertThat(options.getSeed()).isEqualTo(12345);
		assertThat(options.getNegativePrompt()).isEqualTo("blurry");
		assertThat(options.getResponseFormat()).isEqualTo("url");
		assertThat(options.getAsync()).isTrue();
	}

	@Test
	void imagePropertiesDefaultValues() {
		DashScopeSdkImageProperties properties = bind(DashScopeSdkImageProperties.CONFIG_PREFIX,
				DashScopeSdkImageProperties.class);

		DashScopeSdkImageOptions options = properties.toOptions();
		assertThat(options).isNotNull();
		assertThat(options.getModel()).isEqualTo("wanx-v1");
		assertThat(options.getN()).isEqualTo(1);
		assertThat(properties.isEnabled()).isTrue();
	}

	@Test
	void imagePropertiesStillBindLegacyOptions() {
		DashScopeSdkImageProperties properties = bind(DashScopeSdkImageProperties.CONFIG_PREFIX,
				DashScopeSdkImageProperties.class,
				"spring.ai.dashscope.sdk.image.options.model", "legacy-wanx",
				"spring.ai.dashscope.sdk.image.options.n", "3",
				"spring.ai.dashscope.sdk.image.options.size", "1024*1024",
				"spring.ai.dashscope.sdk.image.options.width", "1024",
				"spring.ai.dashscope.sdk.image.options.height", "1024",
				"spring.ai.dashscope.sdk.image.options.style", "photography",
				"spring.ai.dashscope.sdk.image.options.seed", "12345",
				"spring.ai.dashscope.sdk.image.options.negative-prompt", "legacy-blurry",
				"spring.ai.dashscope.sdk.image.options.response-format", "url",
				"spring.ai.dashscope.sdk.image.options.async", "true");

		DashScopeSdkImageOptions options = properties.toOptions();
		assertThat(options).isNotNull();
		assertThat(options.getModel()).isEqualTo("legacy-wanx");
		assertThat(options.getN()).isEqualTo(3);
		assertThat(options.getSize()).isEqualTo("1024*1024");
		assertThat(options.getWidth()).isEqualTo(1024);
		assertThat(options.getHeight()).isEqualTo(1024);
		assertThat(options.getStyle()).isEqualTo("photography");
		assertThat(options.getSeed()).isEqualTo(12345);
		assertThat(options.getNegativePrompt()).isEqualTo("legacy-blurry");
		assertThat(options.getResponseFormat()).isEqualTo("url");
		assertThat(options.getAsync()).isTrue();
	}

	// ========== Audio Speech ==========

	@Test
	void audioSpeechPropertiesBindFlatOptions() {
		DashScopeSdkAudioSpeechProperties properties = bind(DashScopeSdkAudioSpeechProperties.CONFIG_PREFIX,
				DashScopeSdkAudioSpeechProperties.class,
				"spring.ai.dashscope.sdk.audio.speech.model", "sambert-test",
				"spring.ai.dashscope.sdk.audio.speech.voice", "zhichu",
				"spring.ai.dashscope.sdk.audio.speech.sample-rate", "16000",
				"spring.ai.dashscope.sdk.audio.speech.format", "mp3",
				"spring.ai.dashscope.sdk.audio.speech.speed", "1.2",
				"spring.ai.dashscope.sdk.audio.speech.volume", "80",
				"spring.ai.dashscope.sdk.audio.speech.rate", "1.0",
				"spring.ai.dashscope.sdk.audio.speech.pitch", "1.0",
				"spring.ai.dashscope.sdk.audio.speech.text-type", "document",
				"spring.ai.dashscope.sdk.audio.speech.word-timestamp-enabled", "true",
				"spring.ai.dashscope.sdk.audio.speech.phoneme-timestamp-enabled", "true");

		DashScopeSdkAudioSpeechOptions options = properties.toOptions();
		assertThat(options).isNotNull();
		assertThat(options.getModel()).isEqualTo("sambert-test");
		assertThat(options.getVoice()).isEqualTo("zhichu");
		assertThat(options.getSampleRate()).isEqualTo(16000);
		assertThat(options.getFormat()).isEqualTo("mp3");
		assertThat(options.getSpeed()).isEqualTo(1.2d);
		assertThat(options.getVolume()).isEqualTo(80);
		assertThat(options.getRate()).isEqualTo(1.0f);
		assertThat(options.getPitch()).isEqualTo(1.0f);
		assertThat(options.getTextType()).isEqualTo("document");
		assertThat(options.getWordTimestampEnabled()).isTrue();
		assertThat(options.getPhonemeTimestampEnabled()).isTrue();
	}

	@Test
	void audioSpeechPropertiesDefaultValues() {
		DashScopeSdkAudioSpeechProperties properties = bind(DashScopeSdkAudioSpeechProperties.CONFIG_PREFIX,
				DashScopeSdkAudioSpeechProperties.class);

		DashScopeSdkAudioSpeechOptions options = properties.toOptions();
		assertThat(options).isNotNull();
		assertThat(options.getModel()).isEqualTo("sambert-zhichu-v1");
		assertThat(properties.isEnabled()).isTrue();
	}

	@Test
	void audioSpeechPropertiesStillBindLegacyOptions() {
		DashScopeSdkAudioSpeechProperties properties = bind(DashScopeSdkAudioSpeechProperties.CONFIG_PREFIX,
				DashScopeSdkAudioSpeechProperties.class,
				"spring.ai.dashscope.sdk.audio.speech.options.model", "legacy-sambert",
				"spring.ai.dashscope.sdk.audio.speech.options.voice", "legacy-voice",
				"spring.ai.dashscope.sdk.audio.speech.options.sample-rate", "16000",
				"spring.ai.dashscope.sdk.audio.speech.options.format", "mp3",
				"spring.ai.dashscope.sdk.audio.speech.options.speed", "1.2",
				"spring.ai.dashscope.sdk.audio.speech.options.volume", "80",
				"spring.ai.dashscope.sdk.audio.speech.options.rate", "1.0",
				"spring.ai.dashscope.sdk.audio.speech.options.pitch", "1.0",
				"spring.ai.dashscope.sdk.audio.speech.options.text-type", "document",
				"spring.ai.dashscope.sdk.audio.speech.options.word-timestamp-enabled", "true",
				"spring.ai.dashscope.sdk.audio.speech.options.phoneme-timestamp-enabled", "true");

		DashScopeSdkAudioSpeechOptions options = properties.toOptions();
		assertThat(options).isNotNull();
		assertThat(options.getModel()).isEqualTo("legacy-sambert");
		assertThat(options.getVoice()).isEqualTo("legacy-voice");
		assertThat(options.getSampleRate()).isEqualTo(16000);
		assertThat(options.getFormat()).isEqualTo("mp3");
		assertThat(options.getSpeed()).isEqualTo(1.2d);
		assertThat(options.getVolume()).isEqualTo(80);
		assertThat(options.getRate()).isEqualTo(1.0f);
		assertThat(options.getPitch()).isEqualTo(1.0f);
		assertThat(options.getTextType()).isEqualTo("document");
		assertThat(options.getWordTimestampEnabled()).isTrue();
		assertThat(options.getPhonemeTimestampEnabled()).isTrue();
	}

	// ========== Audio Transcription ==========

	@Test
	void audioTranscriptionPropertiesBindFlatOptions() {
		DashScopeSdkAudioTranscriptionProperties properties = bind(
				DashScopeSdkAudioTranscriptionProperties.CONFIG_PREFIX,
				DashScopeSdkAudioTranscriptionProperties.class,
				"spring.ai.dashscope.sdk.audio.transcription.model", "paraformer-test",
				"spring.ai.dashscope.sdk.audio.transcription.file-urls[0]", "https://example.com/audio.wav",
				"spring.ai.dashscope.sdk.audio.transcription.phrase-id", "phrase-123",
				"spring.ai.dashscope.sdk.audio.transcription.channel-id[0]", "1",
				"spring.ai.dashscope.sdk.audio.transcription.diarization-enabled", "true",
				"spring.ai.dashscope.sdk.audio.transcription.speaker-count", "2",
				"spring.ai.dashscope.sdk.audio.transcription.disfluency-removal-enabled", "true",
				"spring.ai.dashscope.sdk.audio.transcription.timestamp-alignment-enabled", "true",
				"spring.ai.dashscope.sdk.audio.transcription.special-word-filter", "filter1",
				"spring.ai.dashscope.sdk.audio.transcription.audio-event-detection-enabled", "true");

		DashScopeSdkAudioTranscriptionOptions options = properties.toOptions();
		assertThat(options).isNotNull();
		assertThat(options.getModel()).isEqualTo("paraformer-test");
		assertThat(options.getFileUrls()).containsExactly("https://example.com/audio.wav");
		assertThat(options.getPhraseId()).isEqualTo("phrase-123");
		assertThat(options.getChannelId()).containsExactly(1);
		assertThat(options.getDiarizationEnabled()).isTrue();
		assertThat(options.getSpeakerCount()).isEqualTo(2);
		assertThat(options.getDisfluencyRemovalEnabled()).isTrue();
		assertThat(options.getTimestampAlignmentEnabled()).isTrue();
		assertThat(options.getSpecialWordFilter()).isEqualTo("filter1");
		assertThat(options.getAudioEventDetectionEnabled()).isTrue();
	}

	@Test
	void audioTranscriptionPropertiesDefaultValues() {
		DashScopeSdkAudioTranscriptionProperties properties = bind(
				DashScopeSdkAudioTranscriptionProperties.CONFIG_PREFIX,
				DashScopeSdkAudioTranscriptionProperties.class);

		DashScopeSdkAudioTranscriptionOptions options = properties.toOptions();
		assertThat(options).isNotNull();
		assertThat(options.getModel()).isEqualTo("paraformer-v2");
		assertThat(properties.isEnabled()).isTrue();
	}

	@Test
	void audioTranscriptionPropertiesStillBindLegacyOptions() {
		DashScopeSdkAudioTranscriptionProperties properties = bind(
				DashScopeSdkAudioTranscriptionProperties.CONFIG_PREFIX,
				DashScopeSdkAudioTranscriptionProperties.class,
				"spring.ai.dashscope.sdk.audio.transcription.options.model", "legacy-paraformer",
				"spring.ai.dashscope.sdk.audio.transcription.options.file-urls[0]", "https://example.com/legacy.wav",
				"spring.ai.dashscope.sdk.audio.transcription.options.phrase-id", "legacy-phrase",
				"spring.ai.dashscope.sdk.audio.transcription.options.channel-id[0]", "1",
				"spring.ai.dashscope.sdk.audio.transcription.options.diarization-enabled", "true",
				"spring.ai.dashscope.sdk.audio.transcription.options.speaker-count", "2",
				"spring.ai.dashscope.sdk.audio.transcription.options.disfluency-removal-enabled", "true",
				"spring.ai.dashscope.sdk.audio.transcription.options.timestamp-alignment-enabled", "true",
				"spring.ai.dashscope.sdk.audio.transcription.options.special-word-filter", "legacy-filter",
				"spring.ai.dashscope.sdk.audio.transcription.options.audio-event-detection-enabled", "true");

		DashScopeSdkAudioTranscriptionOptions options = properties.toOptions();
		assertThat(options).isNotNull();
		assertThat(options.getModel()).isEqualTo("legacy-paraformer");
		assertThat(options.getFileUrls()).containsExactly("https://example.com/legacy.wav");
		assertThat(options.getPhraseId()).isEqualTo("legacy-phrase");
		assertThat(options.getChannelId()).containsExactly(1);
		assertThat(options.getDiarizationEnabled()).isTrue();
		assertThat(options.getSpeakerCount()).isEqualTo(2);
		assertThat(options.getDisfluencyRemovalEnabled()).isTrue();
		assertThat(options.getTimestampAlignmentEnabled()).isTrue();
		assertThat(options.getSpecialWordFilter()).isEqualTo("legacy-filter");
		assertThat(options.getAudioEventDetectionEnabled()).isTrue();
	}

	// ========== Direct Getter/Setter ==========

	@Test
	void chatPropertiesDirectGetterSetter() {
		DashScopeSdkChatProperties properties = new DashScopeSdkChatProperties();
		properties.setModel("qwen-direct");
		properties.setTemperature(0.5d);
		properties.setStream(true);
		properties.setSeed(100);
		properties.setTopP(0.8d);
		properties.setTopK(40);

		assertThat(properties.getModel()).isEqualTo("qwen-direct");
		assertThat(properties.getTemperature()).isEqualTo(0.5d);
		assertThat(properties.getStream()).isTrue();
		assertThat(properties.getSeed()).isEqualTo(100);
		assertThat(properties.getTopP()).isEqualTo(0.8d);
		assertThat(properties.getTopK()).isEqualTo(40);

		DashScopeSdkChatOptions options = properties.toOptions();
		assertThat(options).isNotNull();
		assertThat(options.getModel()).isEqualTo("qwen-direct");
		assertThat(options.getTemperature()).isEqualTo(0.5d);
	}

	@Test
	void embeddingPropertiesDirectGetterSetter() {
		DashScopeSdkEmbeddingProperties properties = new DashScopeSdkEmbeddingProperties();
		properties.setModel("embedding-direct");
		properties.setDimensions(1024);
		properties.setTextType("query");

		assertThat(properties.getModel()).isEqualTo("embedding-direct");
		assertThat(properties.getDimensions()).isEqualTo(1024);
		assertThat(properties.getTextType()).isEqualTo("query");

		DashScopeSdkEmbeddingOptions options = properties.toOptions();
		assertThat(options).isNotNull();
		assertThat(options.getModel()).isEqualTo("embedding-direct");
		assertThat(options.getDimensions()).isEqualTo(1024);
	}

	@Test
	void imagePropertiesDirectGetterSetter() {
		DashScopeSdkImageProperties properties = new DashScopeSdkImageProperties();
		properties.setModel("wanx-direct");
		properties.setN(4);
		properties.setWidth(1024);
		properties.setHeight(1024);
		properties.setStyle("portrait");
		properties.setSeed(999);
		properties.setNegativePrompt("ugly");

		assertThat(properties.getModel()).isEqualTo("wanx-direct");
		assertThat(properties.getN()).isEqualTo(4);
		assertThat(properties.getWidth()).isEqualTo(1024);
		assertThat(properties.getHeight()).isEqualTo(1024);
		assertThat(properties.getStyle()).isEqualTo("portrait");
		assertThat(properties.getSeed()).isEqualTo(999);
		assertThat(properties.getNegativePrompt()).isEqualTo("ugly");

		DashScopeSdkImageOptions options = properties.toOptions();
		assertThat(options).isNotNull();
		assertThat(options.getModel()).isEqualTo("wanx-direct");
		assertThat(options.getN()).isEqualTo(4);
	}

	@Test
	void audioSpeechPropertiesDirectGetterSetter() {
		DashScopeSdkAudioSpeechProperties properties = new DashScopeSdkAudioSpeechProperties();
		properties.setModel("sambert-direct");
		properties.setVoice("direct-voice");
		properties.setSampleRate(22050);
		properties.setFormat("wav");
		properties.setSpeed(1.5d);
		properties.setVolume(90);

		assertThat(properties.getModel()).isEqualTo("sambert-direct");
		assertThat(properties.getVoice()).isEqualTo("direct-voice");
		assertThat(properties.getSampleRate()).isEqualTo(22050);
		assertThat(properties.getFormat()).isEqualTo("wav");
		assertThat(properties.getSpeed()).isEqualTo(1.5d);
		assertThat(properties.getVolume()).isEqualTo(90);

		DashScopeSdkAudioSpeechOptions options = properties.toOptions();
		assertThat(options).isNotNull();
		assertThat(options.getModel()).isEqualTo("sambert-direct");
		assertThat(options.getVoice()).isEqualTo("direct-voice");
	}

	@Test
	void audioTranscriptionPropertiesDirectGetterSetter() {
		DashScopeSdkAudioTranscriptionProperties properties = new DashScopeSdkAudioTranscriptionProperties();
		properties.setModel("paraformer-direct");
		properties.setFileUrls(List.of("https://example.com/direct.wav"));
		properties.setPhraseId("direct-phrase");
		properties.setDiarizationEnabled(true);
		properties.setSpeakerCount(3);

		assertThat(properties.getModel()).isEqualTo("paraformer-direct");
		assertThat(properties.getFileUrls()).containsExactly("https://example.com/direct.wav");
		assertThat(properties.getPhraseId()).isEqualTo("direct-phrase");
		assertThat(properties.getDiarizationEnabled()).isTrue();
		assertThat(properties.getSpeakerCount()).isEqualTo(3);

		DashScopeSdkAudioTranscriptionOptions options = properties.toOptions();
		assertThat(options).isNotNull();
		assertThat(options.getModel()).isEqualTo("paraformer-direct");
		assertThat(options.getFileUrls()).containsExactly("https://example.com/direct.wav");
	}

	// ========== Helper ==========

	private static <T> T bind(String prefix, Class<T> propertiesType, String... pairs) {
		Map<String, String> source = new LinkedHashMap<>();
		for (int i = 0; i < pairs.length; i += 2) {
			source.put(pairs[i], pairs[i + 1]);
		}
		return new Binder(new MapConfigurationPropertySource(source))
				.bindOrCreate(prefix, Bindable.of(propertiesType));
	}

}
