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

package com.alibaba.cloud.ai.autoconfigure.dashscope;

import java.util.LinkedHashMap;
import java.util.Map;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.alibaba.cloud.ai.dashscope.audio.tts.DashScopeAudioSpeechOptions;
import com.alibaba.cloud.ai.dashscope.audio.transcription.DashScopeAudioTranscriptionOptions;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageOptions;
import com.alibaba.cloud.ai.dashscope.embedding.text.DashScopeEmbeddingOptions;
import com.alibaba.cloud.ai.dashscope.embedding.multimodal.DashScopeMultimodalEmbeddingOptions;
import com.alibaba.cloud.ai.dashscope.rerank.DashScopeRerankOptions;
import com.alibaba.cloud.ai.dashscope.agent.DashScopeAgentOptions;
import com.alibaba.cloud.ai.dashscope.video.DashScopeVideoOptions;
import org.junit.jupiter.api.Test;

import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

class DashScopePropertiesTests {

	// ========== Chat ==========

	@Test
	void chatPropertiesBindFlatOptions() {
		DashScopeChatProperties properties = bind(DashScopeChatProperties.CONFIG_PREFIX,
				DashScopeChatProperties.class,
				"spring.ai.dashscope.chat.model", "qwen-test",
				"spring.ai.dashscope.chat.temperature", "0.7",
				"spring.ai.dashscope.chat.enable-search", "true",
				"spring.ai.dashscope.chat.top-p", "0.9",
				"spring.ai.dashscope.chat.top-k", "50",
				"spring.ai.dashscope.chat.seed", "42",
				"spring.ai.dashscope.chat.max-completion-tokens", "1024",
				"spring.ai.dashscope.chat.enable-thinking", "true",
				"spring.ai.dashscope.chat.incremental-output", "true",
				"spring.ai.dashscope.chat.repetition-penalty", "1.1",
				"spring.ai.dashscope.chat.presence-penalty", "0.5",
				"spring.ai.dashscope.chat.parallel-tool-calls", "true",
				"spring.ai.dashscope.chat.multi-model", "true");

		DashScopeChatOptions options = properties.toOptions();
		assertThat(options).isNotNull();
		assertThat(options.getModel()).isEqualTo("qwen-test");
		assertThat(options.getTemperature()).isEqualTo(0.7d);
		assertThat(options.getEnableSearch()).isTrue();
		assertThat(options.getTopP()).isEqualTo(0.9d);
		assertThat(options.getTopK()).isEqualTo(50);
		assertThat(options.getSeed()).isEqualTo(42);
		assertThat(options.getMaxCompletionTokens()).isEqualTo(1024);
		assertThat(options.getEnableThinking()).isTrue();
		assertThat(options.getIncrementalOutput()).isTrue();
		assertThat(options.getRepetitionPenalty()).isEqualTo(1.1d);
		assertThat(options.getPresencePenalty()).isEqualTo(0.5d);
		assertThat(options.getParallelToolCalls()).isTrue();
		assertThat(options.getMultiModel()).isTrue();
	}

	@Test
	void chatPropertiesDefaultValues() {
		DashScopeChatProperties properties = bind(DashScopeChatProperties.CONFIG_PREFIX,
				DashScopeChatProperties.class);

		DashScopeChatOptions options = properties.toOptions();
		assertThat(options).isNotNull();
		assertThat(options.getModel()).isEqualTo(DashScopeChatProperties.DEFAULT_CHAT_MODEL);
		assertThat(properties.isEnabled()).isTrue();
		assertThat(properties.getCompletionsPath()).isNotNull();
	}

	@Test
	void chatPropertiesStillBindLegacyOptions() {
		DashScopeChatProperties properties = bind(DashScopeChatProperties.CONFIG_PREFIX,
				DashScopeChatProperties.class,
				"spring.ai.dashscope.chat.options.model", "legacy-qwen",
				"spring.ai.dashscope.chat.options.temperature", "0.7",
				"spring.ai.dashscope.chat.options.enable-search", "true",
				"spring.ai.dashscope.chat.options.top-p", "0.9",
				"spring.ai.dashscope.chat.options.top-k", "50",
				"spring.ai.dashscope.chat.options.seed", "42",
				"spring.ai.dashscope.chat.options.max-completion-tokens", "1024",
				"spring.ai.dashscope.chat.options.enable-thinking", "true",
				"spring.ai.dashscope.chat.options.incremental-output", "true",
				"spring.ai.dashscope.chat.options.repetition-penalty", "1.1",
				"spring.ai.dashscope.chat.options.presence-penalty", "0.5",
				"spring.ai.dashscope.chat.options.parallel-tool-calls", "true",
				"spring.ai.dashscope.chat.options.multi-model", "true");

		DashScopeChatOptions options = properties.toOptions();
		assertThat(options).isNotNull();
		assertThat(options.getModel()).isEqualTo("legacy-qwen");
		assertThat(options.getTemperature()).isEqualTo(0.7d);
		assertThat(options.getEnableSearch()).isTrue();
		assertThat(options.getTopP()).isEqualTo(0.9d);
		assertThat(options.getTopK()).isEqualTo(50);
		assertThat(options.getSeed()).isEqualTo(42);
		assertThat(options.getMaxCompletionTokens()).isEqualTo(1024);
		assertThat(options.getEnableThinking()).isTrue();
		assertThat(options.getIncrementalOutput()).isTrue();
		assertThat(options.getRepetitionPenalty()).isEqualTo(1.1d);
		assertThat(options.getPresencePenalty()).isEqualTo(0.5d);
		assertThat(options.getParallelToolCalls()).isTrue();
		assertThat(options.getMultiModel()).isTrue();
	}

	// ========== Audio Speech ==========

	@Test
	void audioSpeechPropertiesBindFlatOptions() {
		DashScopeAudioSpeechProperties properties = bind(DashScopeAudioSpeechProperties.CONFIG_PREFIX,
				DashScopeAudioSpeechProperties.class,
				"spring.ai.dashscope.audio.speech.model", "cosyvoice-test",
				"spring.ai.dashscope.audio.speech.voice", "longxiaochun",
				"spring.ai.dashscope.audio.speech.sample-rate", "16000",
				"spring.ai.dashscope.audio.speech.format", "mp3",
				"spring.ai.dashscope.audio.speech.volume", "80",
				"spring.ai.dashscope.audio.speech.rate", "1.2",
				"spring.ai.dashscope.audio.speech.pitch", "1.0",
				"spring.ai.dashscope.audio.speech.speed", "1.5",
				"spring.ai.dashscope.audio.speech.seed", "123",
				"spring.ai.dashscope.audio.speech.enable-ssml", "true",
				"spring.ai.dashscope.audio.speech.bit-rate", "128",
				"spring.ai.dashscope.audio.speech.enable-aigc-tag", "true",
				"spring.ai.dashscope.audio.speech.language-type", "zh");

		DashScopeAudioSpeechOptions options = properties.toOptions();
		assertThat(options).isNotNull();
		assertThat(options.getModel()).isEqualTo("cosyvoice-test");
		assertThat(options.getVoice()).isEqualTo("longxiaochun");
		assertThat(options.getSampleRate()).isEqualTo(16000);
		assertThat(options.getFormat()).isEqualTo("mp3");
		assertThat(options.getVolume()).isEqualTo(80);
		assertThat(options.getRate()).isEqualTo(1.2f);
		assertThat(options.getPitch()).isEqualTo(1.0f);
		assertThat(options.getSpeed()).isEqualTo(1.5d);
		assertThat(options.getSeed()).isEqualTo(123);
		assertThat(options.getEnableSsml()).isTrue();
		assertThat(options.getBitRate()).isEqualTo(128);
		assertThat(options.getEnableAigcTag()).isTrue();
		assertThat(options.getLanguageType()).isEqualTo("zh");
	}

	@Test
	void audioSpeechPropertiesStillBindLegacyOptions() {
		DashScopeAudioSpeechProperties properties = bind(DashScopeAudioSpeechProperties.CONFIG_PREFIX,
				DashScopeAudioSpeechProperties.class,
				"spring.ai.dashscope.audio.speech.options.model", "legacy-cosyvoice",
				"spring.ai.dashscope.audio.speech.options.voice", "legacy-voice",
				"spring.ai.dashscope.audio.speech.options.sample-rate", "16000",
				"spring.ai.dashscope.audio.speech.options.format", "mp3",
				"spring.ai.dashscope.audio.speech.options.volume", "80",
				"spring.ai.dashscope.audio.speech.options.rate", "1.2",
				"spring.ai.dashscope.audio.speech.options.pitch", "1.0",
				"spring.ai.dashscope.audio.speech.options.speed", "1.5",
				"spring.ai.dashscope.audio.speech.options.seed", "123",
				"spring.ai.dashscope.audio.speech.options.enable-ssml", "true",
				"spring.ai.dashscope.audio.speech.options.bit-rate", "128",
				"spring.ai.dashscope.audio.speech.options.enable-aigc-tag", "true",
				"spring.ai.dashscope.audio.speech.options.language-type", "zh");

		DashScopeAudioSpeechOptions options = properties.toOptions();
		assertThat(options).isNotNull();
		assertThat(options.getModel()).isEqualTo("legacy-cosyvoice");
		assertThat(options.getVoice()).isEqualTo("legacy-voice");
		assertThat(options.getSampleRate()).isEqualTo(16000);
		assertThat(options.getFormat()).isEqualTo("mp3");
		assertThat(options.getVolume()).isEqualTo(80);
		assertThat(options.getRate()).isEqualTo(1.2f);
		assertThat(options.getPitch()).isEqualTo(1.0f);
		assertThat(options.getSpeed()).isEqualTo(1.5d);
		assertThat(options.getSeed()).isEqualTo(123);
		assertThat(options.getEnableSsml()).isTrue();
		assertThat(options.getBitRate()).isEqualTo(128);
		assertThat(options.getEnableAigcTag()).isTrue();
		assertThat(options.getLanguageType()).isEqualTo("zh");
	}

	// ========== Audio Transcription ==========

	@Test
	void audioTranscriptionPropertiesBindFlatOptions() {
		DashScopeAudioTranscriptionProperties properties = bind(DashScopeAudioTranscriptionProperties.CONFIG_PREFIX,
				DashScopeAudioTranscriptionProperties.class,
				"spring.ai.dashscope.audio.transcription.model", "paraformer-test",
				"spring.ai.dashscope.audio.transcription.translation-enabled", "true",
				"spring.ai.dashscope.audio.transcription.channel-id[0]", "1",
				"spring.ai.dashscope.audio.transcription.sample-rate", "16000",
				"spring.ai.dashscope.audio.transcription.format", "wav",
				"spring.ai.dashscope.audio.transcription.source-language", "zh",
				"spring.ai.dashscope.audio.transcription.transcription-enabled", "true",
				"spring.ai.dashscope.audio.transcription.max-end-silence", "800",
				"spring.ai.dashscope.audio.transcription.stream", "true",
				"spring.ai.dashscope.audio.transcription.max-tokens", "2048",
				"spring.ai.dashscope.audio.transcription.seed", "42",
				"spring.ai.dashscope.audio.transcription.temperature", "0.3",
				"spring.ai.dashscope.audio.transcription.top-p", "0.8",
				"spring.ai.dashscope.audio.transcription.top-k", "30",
				"spring.ai.dashscope.audio.transcription.disfluency-removal-enabled", "true",
				"spring.ai.dashscope.audio.transcription.diarization-enabled", "true",
				"spring.ai.dashscope.audio.transcription.speaker-count", "2");

		DashScopeAudioTranscriptionOptions options = properties.toOptions();
		assertThat(options).isNotNull();
		assertThat(options.getModel()).isEqualTo("paraformer-test");
		assertThat(options.getTranslationEnabled()).isTrue();
		assertThat(options.getChannelId()).containsExactly(1);
		assertThat(options.getSampleRate()).isEqualTo(16000);
		assertThat(options.getFormat()).isEqualTo("wav");
		assertThat(options.getSourceLanguage()).isEqualTo("zh");
		assertThat(options.getTranscriptionEnabled()).isTrue();
		assertThat(options.getMaxEndSilence()).isEqualTo(800);
		assertThat(options.getStream()).isTrue();
		assertThat(options.getMaxTokens()).isEqualTo(2048);
		assertThat(options.getSeed()).isEqualTo(42);
		assertThat(options.getTemperature()).isEqualTo(0.3f);
		assertThat(options.getTopP()).isEqualTo(0.8f);
		assertThat(options.getTopK()).isEqualTo(30);
		assertThat(options.getDisfluencyRemovalEnabled()).isTrue();
		assertThat(options.getDiarizationEnabled()).isTrue();
		assertThat(options.getSpeakerCount()).isEqualTo(2);
	}

	@Test
	void audioTranscriptionPropertiesStillBindLegacyOptions() {
		DashScopeAudioTranscriptionProperties properties = bind(DashScopeAudioTranscriptionProperties.CONFIG_PREFIX,
				DashScopeAudioTranscriptionProperties.class,
				"spring.ai.dashscope.audio.transcription.options.model", "legacy-paraformer",
				"spring.ai.dashscope.audio.transcription.options.translation-enabled", "true",
				"spring.ai.dashscope.audio.transcription.options.sample-rate", "16000",
				"spring.ai.dashscope.audio.transcription.options.format", "wav",
				"spring.ai.dashscope.audio.transcription.options.source-language", "zh",
				"spring.ai.dashscope.audio.transcription.options.transcription-enabled", "true",
				"spring.ai.dashscope.audio.transcription.options.max-end-silence", "800",
				"spring.ai.dashscope.audio.transcription.options.stream", "true",
				"spring.ai.dashscope.audio.transcription.options.max-tokens", "2048",
				"spring.ai.dashscope.audio.transcription.options.seed", "42",
				"spring.ai.dashscope.audio.transcription.options.temperature", "0.3",
				"spring.ai.dashscope.audio.transcription.options.top-p", "0.8",
				"spring.ai.dashscope.audio.transcription.options.top-k", "30",
				"spring.ai.dashscope.audio.transcription.options.disfluency-removal-enabled", "true",
				"spring.ai.dashscope.audio.transcription.options.diarization-enabled", "true",
				"spring.ai.dashscope.audio.transcription.options.speaker-count", "2");

		DashScopeAudioTranscriptionOptions options = properties.toOptions();
		assertThat(options).isNotNull();
		assertThat(options.getModel()).isEqualTo("legacy-paraformer");
		assertThat(options.getTranslationEnabled()).isTrue();
		assertThat(options.getSampleRate()).isEqualTo(16000);
		assertThat(options.getFormat()).isEqualTo("wav");
		assertThat(options.getSourceLanguage()).isEqualTo("zh");
		assertThat(options.getTranscriptionEnabled()).isTrue();
		assertThat(options.getMaxEndSilence()).isEqualTo(800);
		assertThat(options.getStream()).isTrue();
		assertThat(options.getMaxTokens()).isEqualTo(2048);
		assertThat(options.getSeed()).isEqualTo(42);
		assertThat(options.getTemperature()).isEqualTo(0.3f);
		assertThat(options.getTopP()).isEqualTo(0.8f);
		assertThat(options.getTopK()).isEqualTo(30);
		assertThat(options.getDisfluencyRemovalEnabled()).isTrue();
		assertThat(options.getDiarizationEnabled()).isTrue();
		assertThat(options.getSpeakerCount()).isEqualTo(2);
	}

	// ========== Embedding ==========

	@Test
	void embeddingPropertiesBindFlatOptions() {
		DashScopeEmbeddingProperties properties = bind(DashScopeEmbeddingProperties.CONFIG_PREFIX,
				DashScopeEmbeddingProperties.class,
				"spring.ai.dashscope.embedding.model", "text-embedding-v2",
				"spring.ai.dashscope.embedding.text-type", "document",
				"spring.ai.dashscope.embedding.dimensions", "1536",
				"spring.ai.dashscope.embedding.output-type", "dense&sparse",
				"spring.ai.dashscope.embedding.embeddings-path", "/custom/embedding/path");

		DashScopeEmbeddingOptions options = properties.toOptions();
		assertThat(options).isNotNull();
		assertThat(options.getModel()).isEqualTo("text-embedding-v2");
		assertThat(options.getTextType()).isEqualTo("document");
		assertThat(options.getDimensions()).isEqualTo(1536);
		assertThat(options.getOutputType()).isEqualTo("dense&sparse");
		assertThat(options.getEmbeddingsPath()).isEqualTo("/custom/embedding/path");
	}

	@Test
	void embeddingPropertiesDefaultValues() {
		DashScopeEmbeddingProperties properties = bind(DashScopeEmbeddingProperties.CONFIG_PREFIX,
				DashScopeEmbeddingProperties.class);

		DashScopeEmbeddingOptions options = properties.toOptions();
		assertThat(options).isNotNull();
		assertThat(options.getModel()).isEqualTo(DashScopeEmbeddingProperties.DEFAULT_EMBEDDING_MODEL);
		assertThat(properties.isEnabled()).isTrue();
		assertThat(properties.getEmbeddingsPath()).isNotNull();
		assertThat(properties.getMetadataMode()).isNotNull();
	}

	@Test
	void embeddingPropertiesStillBindLegacyOptions() {
		DashScopeEmbeddingProperties properties = bind(DashScopeEmbeddingProperties.CONFIG_PREFIX,
				DashScopeEmbeddingProperties.class,
				"spring.ai.dashscope.embedding.options.model", "legacy-text-embedding",
				"spring.ai.dashscope.embedding.options.text-type", "query",
				"spring.ai.dashscope.embedding.options.dimensions", "1024",
				"spring.ai.dashscope.embedding.options.output-type", "dense");

		DashScopeEmbeddingOptions options = properties.toOptions();
		assertThat(options).isNotNull();
		assertThat(options.getModel()).isEqualTo("legacy-text-embedding");
		assertThat(options.getTextType()).isEqualTo("query");
		assertThat(options.getDimensions()).isEqualTo(1024);
		assertThat(options.getOutputType()).isEqualTo("dense");
	}

	@Test
	void embeddingPropertiesDirectGetterSetter() {
		DashScopeEmbeddingProperties properties = new DashScopeEmbeddingProperties();
		properties.setModel("text-embedding-direct");
		properties.setTextType("document");
		properties.setDimensions(2048);
		properties.setOutputType("sparse");
		properties.setEmbeddingsPath("/direct/path");

		assertThat(properties.getModel()).isEqualTo("text-embedding-direct");
		assertThat(properties.getTextType()).isEqualTo("document");
		assertThat(properties.getDimensions()).isEqualTo(2048);
		assertThat(properties.getOutputType()).isEqualTo("sparse");
		assertThat(properties.getEmbeddingsPath()).isEqualTo("/direct/path");

		DashScopeEmbeddingOptions options = properties.toOptions();
		assertThat(options).isNotNull();
		assertThat(options.getModel()).isEqualTo("text-embedding-direct");
		assertThat(options.getTextType()).isEqualTo("document");
		assertThat(options.getDimensions()).isEqualTo(2048);
		assertThat(options.getOutputType()).isEqualTo("sparse");
		assertThat(options.getEmbeddingsPath()).isEqualTo("/direct/path");
	}

	// ========== Image ==========

	@Test
	void imagePropertiesBindFlatOptions() {
		DashScopeImageProperties properties = bind(DashScopeImageProperties.CONFIG_PREFIX,
				DashScopeImageProperties.class,
				"spring.ai.dashscope.image.model", "wanx-test",
				"spring.ai.dashscope.image.n", "2",
				"spring.ai.dashscope.image.size", "1024*1024",
				"spring.ai.dashscope.image.width", "1024",
				"spring.ai.dashscope.image.height", "1024",
				"spring.ai.dashscope.image.style", "photography",
				"spring.ai.dashscope.image.seed", "12345",
				"spring.ai.dashscope.image.negative-prompt", "blurry",
				"spring.ai.dashscope.image.prompt-extend", "true",
				"spring.ai.dashscope.image.watermark", "false",
				"spring.ai.dashscope.image.response-format", "url",
				"spring.ai.dashscope.image.aspect-ratio", "16:9",
				"spring.ai.dashscope.image.resolution", "2K");

		DashScopeImageOptions options = properties.toOptions();
		assertThat(options).isNotNull();
		assertThat(options.getModel()).isEqualTo("wanx-test");
		assertThat(options.getN()).isEqualTo(2);
		assertThat(options.getSize()).isEqualTo("1024*1024");
		assertThat(options.getWidth()).isEqualTo(1024);
		assertThat(options.getHeight()).isEqualTo(1024);
		assertThat(options.getStyle()).isEqualTo("photography");
		assertThat(options.getSeed()).isEqualTo(12345);
		assertThat(options.getNegativePrompt()).isEqualTo("blurry");
		assertThat(options.getPromptExtend()).isTrue();
		assertThat(options.getWatermark()).isFalse();
		assertThat(options.getResponseFormat()).isEqualTo("url");
		assertThat(options.getAspectRatio()).isEqualTo("16:9");
		assertThat(options.getResolution()).isEqualTo("2K");
	}

	@Test
	void imagePropertiesDefaultValues() {
		DashScopeImageProperties properties = bind(DashScopeImageProperties.CONFIG_PREFIX,
				DashScopeImageProperties.class);

		DashScopeImageOptions options = properties.toOptions();
		assertThat(options).isNotNull();
		assertThat(options.getModel()).isNotNull();
		assertThat(options.getN()).isEqualTo(1);
		assertThat(properties.isEnabled()).isTrue();
	}

	@Test
	void imagePropertiesSizeFromWidthHeight() {
		DashScopeImageProperties properties = bind(DashScopeImageProperties.CONFIG_PREFIX,
				DashScopeImageProperties.class,
				"spring.ai.dashscope.image.width", "720",
				"spring.ai.dashscope.image.height", "1280");

		DashScopeImageOptions options = properties.toOptions();
		assertThat(options).isNotNull();
		assertThat(options.getSize()).isEqualTo("720*1280");
	}

	@Test
	void imagePropertiesStillBindLegacyOptions() {
		DashScopeImageProperties properties = bind(DashScopeImageProperties.CONFIG_PREFIX,
				DashScopeImageProperties.class,
				"spring.ai.dashscope.image.options.model", "legacy-wanx",
				"spring.ai.dashscope.image.options.n", "2",
				"spring.ai.dashscope.image.options.size", "1024*1024",
				"spring.ai.dashscope.image.options.width", "1024",
				"spring.ai.dashscope.image.options.height", "1024",
				"spring.ai.dashscope.image.options.style", "photography",
				"spring.ai.dashscope.image.options.seed", "12345",
				"spring.ai.dashscope.image.options.negative-prompt", "legacy-blurry",
				"spring.ai.dashscope.image.options.prompt-extend", "true",
				"spring.ai.dashscope.image.options.watermark", "false",
				"spring.ai.dashscope.image.options.response-format", "url",
				"spring.ai.dashscope.image.options.aspect-ratio", "16:9",
				"spring.ai.dashscope.image.options.resolution", "2K");

		DashScopeImageOptions options = properties.toOptions();
		assertThat(options).isNotNull();
		assertThat(options.getModel()).isEqualTo("legacy-wanx");
		assertThat(options.getN()).isEqualTo(2);
		assertThat(options.getSize()).isEqualTo("1024*1024");
		assertThat(options.getWidth()).isEqualTo(1024);
		assertThat(options.getHeight()).isEqualTo(1024);
		assertThat(options.getStyle()).isEqualTo("photography");
		assertThat(options.getSeed()).isEqualTo(12345);
		assertThat(options.getNegativePrompt()).isEqualTo("legacy-blurry");
		assertThat(options.getPromptExtend()).isTrue();
		assertThat(options.getWatermark()).isFalse();
		assertThat(options.getResponseFormat()).isEqualTo("url");
		assertThat(options.getAspectRatio()).isEqualTo("16:9");
		assertThat(options.getResolution()).isEqualTo("2K");
	}

	// ========== Multimodal Embedding ==========

	@Test
	void multimodalEmbeddingPropertiesBindFlatOptions() {
		DashScopeMultimodalEmbeddingProperties properties = bind(DashScopeMultimodalEmbeddingProperties.CONFIG_PREFIX,
				DashScopeMultimodalEmbeddingProperties.class,
				"spring.ai.dashscope.embedding.multimodal.model", "vision-embedding-test",
				"spring.ai.dashscope.embedding.multimodal.dimensions", "512",
				"spring.ai.dashscope.embedding.multimodal.output-type", "dense",
				"spring.ai.dashscope.embedding.multimodal.fps", "12.5",
				"spring.ai.dashscope.embedding.multimodal.instruct", "test-instruct");

		DashScopeMultimodalEmbeddingOptions options = properties.toOptions();
		assertThat(options).isNotNull();
		assertThat(options.getModel()).isEqualTo("vision-embedding-test");
		assertThat(options.getDimensions()).isEqualTo(512);
		assertThat(options.getOutputType()).isEqualTo("dense");
		assertThat(options.getFps()).isEqualTo(12.5f);
		assertThat(options.getInstruct()).isEqualTo("test-instruct");
	}

	@Test
	void multimodalEmbeddingPropertiesStillBindLegacyOptions() {
		DashScopeMultimodalEmbeddingProperties properties = bind(DashScopeMultimodalEmbeddingProperties.CONFIG_PREFIX,
				DashScopeMultimodalEmbeddingProperties.class,
				"spring.ai.dashscope.embedding.multimodal.options.model", "legacy-vision-embedding",
				"spring.ai.dashscope.embedding.multimodal.options.dimensions", "512",
				"spring.ai.dashscope.embedding.multimodal.options.output-type", "dense",
				"spring.ai.dashscope.embedding.multimodal.options.fps", "12.5",
				"spring.ai.dashscope.embedding.multimodal.options.instruct", "legacy-instruct");

		DashScopeMultimodalEmbeddingOptions options = properties.toOptions();
		assertThat(options).isNotNull();
		assertThat(options.getModel()).isEqualTo("legacy-vision-embedding");
		assertThat(options.getDimensions()).isEqualTo(512);
		assertThat(options.getOutputType()).isEqualTo("dense");
		assertThat(options.getFps()).isEqualTo(12.5f);
		assertThat(options.getInstruct()).isEqualTo("legacy-instruct");
	}

	// ========== Rerank ==========

	@Test
	void rerankPropertiesBindFlatOptions() {
		DashScopeRerankProperties properties = bind(DashScopeRerankProperties.CONFIG_PREFIX,
				DashScopeRerankProperties.class,
				"spring.ai.dashscope.rerank.model", "rerank-test",
				"spring.ai.dashscope.rerank.top-n", "7",
				"spring.ai.dashscope.rerank.return-documents", "true");

		DashScopeRerankOptions options = properties.toOptions();
		assertThat(options).isNotNull();
		assertThat(options.getModel()).isEqualTo("rerank-test");
		assertThat(options.getTopN()).isEqualTo(7);
		assertThat(options.getReturnDocuments()).isTrue();
	}

	@Test
	void rerankPropertiesDefaultValues() {
		DashScopeRerankProperties properties = bind(DashScopeRerankProperties.CONFIG_PREFIX,
				DashScopeRerankProperties.class);

		DashScopeRerankOptions options = properties.toOptions();
		assertThat(options).isNotNull();
		assertThat(options.getModel()).isEqualTo(DashScopeRerankProperties.DEFAULT_RERANK_MODEL);
		assertThat(options.getTopN()).isEqualTo(3);
		assertThat(options.getReturnDocuments()).isFalse();
		assertThat(properties.getRerankPath()).isNotNull();
	}

	@Test
	void rerankPropertiesStillBindLegacyOptions() {
		DashScopeRerankProperties properties = bind(DashScopeRerankProperties.CONFIG_PREFIX,
				DashScopeRerankProperties.class,
				"spring.ai.dashscope.rerank.options.model", "legacy-rerank",
				"spring.ai.dashscope.rerank.options.top-n", "7",
				"spring.ai.dashscope.rerank.options.return-documents", "true");

		DashScopeRerankOptions options = properties.toOptions();
		assertThat(options).isNotNull();
		assertThat(options.getModel()).isEqualTo("legacy-rerank");
		assertThat(options.getTopN()).isEqualTo(7);
		assertThat(options.getReturnDocuments()).isTrue();
	}

	// ========== Agent ==========

	@Test
	void agentPropertiesBindFlatOptions() {
		DashScopeAgentProperties properties = bind(DashScopeAgentProperties.CONFIG_PREFIX,
				DashScopeAgentProperties.class,
				"spring.ai.dashscope.agent.app-id", "app-test",
				"spring.ai.dashscope.agent.model-id", "qwen-agent",
				"spring.ai.dashscope.agent.enable-thinking", "true",
				"spring.ai.dashscope.agent.session-id", "session-123",
				"spring.ai.dashscope.agent.memory-id", "memory-456",
				"spring.ai.dashscope.agent.incremental-output", "true",
				"spring.ai.dashscope.agent.has-thoughts", "true");

		DashScopeAgentOptions options = properties.toOptions();
		assertThat(options).isNotNull();
		assertThat(options.getAppId()).isEqualTo("app-test");
		assertThat(options.getModelId()).isEqualTo("qwen-agent");
		assertThat(options.getEnableThinking()).isTrue();
		assertThat(options.getSessionId()).isEqualTo("session-123");
		assertThat(options.getMemoryId()).isEqualTo("memory-456");
		assertThat(options.getIncrementalOutput()).isTrue();
		assertThat(options.getHasThoughts()).isTrue();
	}

	@Test
	void agentPropertiesDefaultValues() {
		DashScopeAgentProperties properties = bind(DashScopeAgentProperties.CONFIG_PREFIX,
				DashScopeAgentProperties.class);

		assertThat(properties.isEnabled()).isTrue();
		assertThat(properties.getAgentPath()).isNotNull();
	}

	@Test
	void agentPropertiesStillBindLegacyOptions() {
		DashScopeAgentProperties properties = bind(DashScopeAgentProperties.CONFIG_PREFIX,
				DashScopeAgentProperties.class,
				"spring.ai.dashscope.agent.options.app-id", "legacy-app",
				"spring.ai.dashscope.agent.options.model-id", "legacy-qwen-agent",
				"spring.ai.dashscope.agent.options.enable-thinking", "true",
				"spring.ai.dashscope.agent.options.session-id", "legacy-session",
				"spring.ai.dashscope.agent.options.memory-id", "legacy-memory",
				"spring.ai.dashscope.agent.options.incremental-output", "true",
				"spring.ai.dashscope.agent.options.has-thoughts", "true");

		DashScopeAgentOptions options = properties.toOptions();
		assertThat(options).isNotNull();
		assertThat(options.getAppId()).isEqualTo("legacy-app");
		assertThat(options.getModelId()).isEqualTo("legacy-qwen-agent");
		assertThat(options.getEnableThinking()).isTrue();
		assertThat(options.getSessionId()).isEqualTo("legacy-session");
		assertThat(options.getMemoryId()).isEqualTo("legacy-memory");
		assertThat(options.getIncrementalOutput()).isTrue();
		assertThat(options.getHasThoughts()).isTrue();
	}

	// ========== Video ==========

	@Test
	void videoPropertiesBindFlatOptions() {
		DashScopeVideoProperties properties = bind(DashScopeVideoProperties.CONFIG_PREFIX,
				DashScopeVideoProperties.class,
				"spring.ai.dashscope.video.model", "wan-video-test",
				"spring.ai.dashscope.video.video-path", "/custom/video/path",
				"spring.ai.dashscope.video.query-task-path", "/custom/query/path");

		DashScopeVideoOptions options = properties.toOptions();
		assertThat(options).isNotNull();
		assertThat(options.getModel()).isEqualTo("wan-video-test");
		assertThat(properties.getVideoPath()).isEqualTo("/custom/video/path");
		assertThat(properties.getQueryTaskPath()).isEqualTo("/custom/query/path");
	}

	@Test
	void videoPropertiesDefaultValues() {
		DashScopeVideoProperties properties = bind(DashScopeVideoProperties.CONFIG_PREFIX,
				DashScopeVideoProperties.class);

		DashScopeVideoOptions options = properties.toOptions();
		assertThat(options).isNotNull();
		assertThat(options.getModel()).isEqualTo(DashScopeVideoOptions.DEFAULT_MODEL);
		assertThat(properties.getVideoPath()).isNotNull();
		assertThat(properties.getQueryTaskPath()).isNotNull();
	}

	@Test
	void videoPropertiesWithInputOptions() {
		DashScopeVideoOptions.InputOptions inputOptions = new DashScopeVideoOptions.InputOptions();
		inputOptions.setPrompt("test video prompt");
		inputOptions.setImgUrl("https://example.com/image.jpg");

		DashScopeVideoProperties properties = new DashScopeVideoProperties();
		properties.setModel("wan-video-test");
		properties.setInput(inputOptions);

		DashScopeVideoOptions options = properties.toOptions();
		assertThat(options).isNotNull();
		assertThat(options.getModel()).isEqualTo("wan-video-test");
		assertThat(options.getInput()).isNotNull();
		assertThat(options.getInput().getPrompt()).isEqualTo("test video prompt");
		assertThat(options.getInput().getImgUrl()).isEqualTo("https://example.com/image.jpg");
	}

	@Test
	void videoPropertiesWithParametersOptions() {
		DashScopeVideoOptions.ParametersOptions paramOptions = new DashScopeVideoOptions.ParametersOptions();
		paramOptions.setResolution("720P");
		paramOptions.setSize("1280*720");
		paramOptions.setDuration(5);
		paramOptions.setPromptExtend(true);

		DashScopeVideoProperties properties = new DashScopeVideoProperties();
		properties.setParameters(paramOptions);

		DashScopeVideoOptions options = properties.toOptions();
		assertThat(options).isNotNull();
		assertThat(options.getParameters()).isNotNull();
		assertThat(options.getParameters().getResolution()).isEqualTo("720P");
		assertThat(options.getParameters().getSize()).isEqualTo("1280*720");
		assertThat(options.getParameters().getDuration()).isEqualTo(5);
		assertThat(options.getParameters().getPromptExtend()).isTrue();
	}

	@Test
	void videoPropertiesStillBindLegacyOptions() {
		DashScopeVideoProperties properties = bind(DashScopeVideoProperties.CONFIG_PREFIX,
				DashScopeVideoProperties.class,
				"spring.ai.dashscope.video.options.model", "legacy-video");

		DashScopeVideoOptions options = properties.toOptions();
		assertThat(options).isNotNull();
		assertThat(options.getModel()).isEqualTo("legacy-video");
	}

	// ========== Direct Getter/Setter ==========

	@Test
	void chatPropertiesDirectGetterSetter() {
		DashScopeChatProperties properties = new DashScopeChatProperties();
		properties.setModel("qwen-direct");
		properties.setTemperature(0.5d);
		properties.setTopP(0.8d);
		properties.setTopK(40);
		properties.setEnableThinking(true);
		properties.setSeed(100);

		assertThat(properties.getModel()).isEqualTo("qwen-direct");
		assertThat(properties.getTemperature()).isEqualTo(0.5d);
		assertThat(properties.getTopP()).isEqualTo(0.8d);
		assertThat(properties.getTopK()).isEqualTo(40);
		assertThat(properties.getEnableThinking()).isTrue();
		assertThat(properties.getSeed()).isEqualTo(100);

		DashScopeChatOptions options = properties.toOptions();
		assertThat(options).isNotNull();
		assertThat(options.getModel()).isEqualTo("qwen-direct");
		assertThat(options.getTemperature()).isEqualTo(0.5d);
	}

	@Test
	void imagePropertiesDirectGetterSetter() {
		DashScopeImageProperties properties = new DashScopeImageProperties();
		properties.setModel("wanx-direct");
		properties.setN(4);
		properties.setWidth(1024);
		properties.setHeight(1024);
		properties.setStyle("portrait");
		properties.setSeed(999);
		properties.setNegativePrompt("ugly");
		properties.setPromptExtend(true);
		properties.setWatermark(false);

		assertThat(properties.getModel()).isEqualTo("wanx-direct");
		assertThat(properties.getN()).isEqualTo(4);
		assertThat(properties.getWidth()).isEqualTo(1024);
		assertThat(properties.getHeight()).isEqualTo(1024);
		assertThat(properties.getStyle()).isEqualTo("portrait");
		assertThat(properties.getSeed()).isEqualTo(999);
		assertThat(properties.getNegativePrompt()).isEqualTo("ugly");
		assertThat(properties.getPromptExtend()).isTrue();
		assertThat(properties.getWatermark()).isFalse();

		DashScopeImageOptions options = properties.toOptions();
		assertThat(options).isNotNull();
		assertThat(options.getModel()).isEqualTo("wanx-direct");
		assertThat(options.getN()).isEqualTo(4);
	}

	@Test
	void rerankPropertiesDirectGetterSetter() {
		DashScopeRerankProperties properties = new DashScopeRerankProperties();
		properties.setModel("rerank-direct");
		properties.setTopN(10);
		properties.setReturnDocuments(true);

		assertThat(properties.getModel()).isEqualTo("rerank-direct");
		assertThat(properties.getTopN()).isEqualTo(10);
		assertThat(properties.getReturnDocuments()).isTrue();

		DashScopeRerankOptions options = properties.toOptions();
		assertThat(options).isNotNull();
		assertThat(options.getModel()).isEqualTo("rerank-direct");
		assertThat(options.getTopN()).isEqualTo(10);
		assertThat(options.getReturnDocuments()).isTrue();
	}

	@Test
	void agentPropertiesDirectGetterSetter() {
		DashScopeAgentProperties properties = new DashScopeAgentProperties();
		properties.setAppId("app-direct");
		properties.setSessionId("session-direct");
		properties.setMemoryId("memory-direct");
		properties.setModelId("qwen-agent-direct");
		properties.setEnableThinking(true);
		properties.setHasThoughts(true);
		properties.setIncrementalOutput(true);

		assertThat(properties.getAppId()).isEqualTo("app-direct");
		assertThat(properties.getSessionId()).isEqualTo("session-direct");
		assertThat(properties.getMemoryId()).isEqualTo("memory-direct");
		assertThat(properties.getModelId()).isEqualTo("qwen-agent-direct");
		assertThat(properties.getEnableThinking()).isTrue();
		assertThat(properties.getHasThoughts()).isTrue();
		assertThat(properties.getIncrementalOutput()).isTrue();

		DashScopeAgentOptions options = properties.toOptions();
		assertThat(options).isNotNull();
		assertThat(options.getAppId()).isEqualTo("app-direct");
	}

	@Test
	void audioSpeechPropertiesDirectGetterSetter() {
		DashScopeAudioSpeechProperties properties = new DashScopeAudioSpeechProperties();
		properties.setModel("cosyvoice-direct");
		properties.setVoice("direct-voice");
		properties.setSampleRate(22050);
		properties.setFormat("wav");
		properties.setVolume(90);
		properties.setRate(1.5f);
		properties.setPitch(1.2f);

		assertThat(properties.getModel()).isEqualTo("cosyvoice-direct");
		assertThat(properties.getVoice()).isEqualTo("direct-voice");
		assertThat(properties.getSampleRate()).isEqualTo(22050);
		assertThat(properties.getFormat()).isEqualTo("wav");
		assertThat(properties.getVolume()).isEqualTo(90);
		assertThat(properties.getRate()).isEqualTo(1.5f);
		assertThat(properties.getPitch()).isEqualTo(1.2f);

		DashScopeAudioSpeechOptions options = properties.toOptions();
		assertThat(options).isNotNull();
		assertThat(options.getModel()).isEqualTo("cosyvoice-direct");
	}

	@Test
	void multimodalEmbeddingPropertiesDirectGetterSetter() {
		DashScopeMultimodalEmbeddingProperties properties = new DashScopeMultimodalEmbeddingProperties();
		properties.setModel("vision-embedding-direct");
		properties.setDimensions(1024);
		properties.setOutputType("sparse");
		properties.setFps(24.0f);
		properties.setInstruct("direct-instruct");

		assertThat(properties.getModel()).isEqualTo("vision-embedding-direct");
		assertThat(properties.getDimensions()).isEqualTo(1024);
		assertThat(properties.getOutputType()).isEqualTo("sparse");
		assertThat(properties.getFps()).isEqualTo(24.0f);
		assertThat(properties.getInstruct()).isEqualTo("direct-instruct");

		DashScopeMultimodalEmbeddingOptions options = properties.toOptions();
		assertThat(options).isNotNull();
		assertThat(options.getModel()).isEqualTo("vision-embedding-direct");
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
