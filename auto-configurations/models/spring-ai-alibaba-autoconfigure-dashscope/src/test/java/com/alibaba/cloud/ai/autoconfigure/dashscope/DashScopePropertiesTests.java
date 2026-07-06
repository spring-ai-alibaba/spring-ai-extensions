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

import org.junit.jupiter.api.Test;

import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

class DashScopePropertiesTests {

	@Test
	void chatPropertiesBindFlatOptions() {
		DashScopeChatProperties properties = bind(DashScopeChatProperties.CONFIG_PREFIX,
				DashScopeChatProperties.class,
				"spring.ai.dashscope.chat.model", "qwen-test",
				"spring.ai.dashscope.chat.temperature", "0.7",
				"spring.ai.dashscope.chat.enable-search", "true");

		assertThat(properties.toOptions().getModel()).isEqualTo("qwen-test");
		assertThat(properties.toOptions().getTemperature()).isEqualTo(0.7d);
		assertThat(properties.toOptions().getEnableSearch()).isTrue();
	}

	@Test
	void chatPropertiesStillBindLegacyOptions() {
		DashScopeChatProperties properties = bind(DashScopeChatProperties.CONFIG_PREFIX,
				DashScopeChatProperties.class,
				"spring.ai.dashscope.chat.options.model", "legacy-qwen");

		assertThat(properties.toOptions().getModel()).isEqualTo("legacy-qwen");
	}

	@Test
	void audioSpeechPropertiesBindFlatOptions() {
		DashScopeAudioSpeechProperties properties = bind(DashScopeAudioSpeechProperties.CONFIG_PREFIX,
				DashScopeAudioSpeechProperties.class,
				"spring.ai.dashscope.audio.speech.model", "cosyvoice-test",
				"spring.ai.dashscope.audio.speech.voice", "longxiaochun",
				"spring.ai.dashscope.audio.speech.sample-rate", "16000");

		assertThat(properties.toOptions().getModel()).isEqualTo("cosyvoice-test");
		assertThat(properties.toOptions().getVoice()).isEqualTo("longxiaochun");
		assertThat(properties.toOptions().getSampleRate()).isEqualTo(16000);
	}

	@Test
	void audioSpeechPropertiesStillBindLegacyOptions() {
		DashScopeAudioSpeechProperties properties = bind(DashScopeAudioSpeechProperties.CONFIG_PREFIX,
				DashScopeAudioSpeechProperties.class,
				"spring.ai.dashscope.audio.speech.options.voice", "legacy-voice");

		assertThat(properties.toOptions().getVoice()).isEqualTo("legacy-voice");
	}

	@Test
	void audioTranscriptionPropertiesBindFlatOptions() {
		DashScopeAudioTranscriptionProperties properties = bind(DashScopeAudioTranscriptionProperties.CONFIG_PREFIX,
				DashScopeAudioTranscriptionProperties.class,
				"spring.ai.dashscope.audio.transcription.model", "paraformer-test",
				"spring.ai.dashscope.audio.transcription.translation-enabled", "true",
				"spring.ai.dashscope.audio.transcription.channel-id[0]", "1");

		assertThat(properties.toOptions().getModel()).isEqualTo("paraformer-test");
		assertThat(properties.toOptions().getTranslationEnabled()).isTrue();
		assertThat(properties.toOptions().getChannelId()).containsExactly(1);
	}

	@Test
	void audioTranscriptionPropertiesStillBindLegacyOptions() {
		DashScopeAudioTranscriptionProperties properties = bind(DashScopeAudioTranscriptionProperties.CONFIG_PREFIX,
				DashScopeAudioTranscriptionProperties.class,
				"spring.ai.dashscope.audio.transcription.options.source-language", "en");

		assertThat(properties.toOptions().getSourceLanguage()).isEqualTo("en");
	}

	@Test
	void embeddingPropertiesBindFlatOptions() {
		DashScopeEmbeddingProperties properties = bind(DashScopeEmbeddingProperties.CONFIG_PREFIX,
				DashScopeEmbeddingProperties.class,
				"spring.ai.dashscope.embedding.model", "embedding-test",
				"spring.ai.dashscope.embedding.dimensions", "256",
				"spring.ai.dashscope.embedding.embeddings-path", "/compatible-mode/v1/embeddings");

		assertThat(properties.toOptions().getModel()).isEqualTo("embedding-test");
		assertThat(properties.toOptions().getDimensions()).isEqualTo(256);
		assertThat(properties.getEmbeddingsPath()).isEqualTo("/compatible-mode/v1/embeddings");
		assertThat(properties.toOptions().getEmbeddingsPath()).isEqualTo("/compatible-mode/v1/embeddings");
	}

	@Test
	void embeddingPropertiesStillBindLegacyOptions() {
		DashScopeEmbeddingProperties properties = bind(DashScopeEmbeddingProperties.CONFIG_PREFIX,
				DashScopeEmbeddingProperties.class,
				"spring.ai.dashscope.embedding.options.dimensions", "128");

		assertThat(properties.toOptions().getDimensions()).isEqualTo(128);
	}

	@Test
	void imagePropertiesBindFlatOptions() {
		DashScopeImageProperties properties = bind(DashScopeImageProperties.CONFIG_PREFIX,
				DashScopeImageProperties.class,
				"spring.ai.dashscope.image.model", "wanx-test",
				"spring.ai.dashscope.image.n", "2",
				"spring.ai.dashscope.image.size", "1024*1024");

		assertThat(properties.toOptions().getModel()).isEqualTo("wanx-test");
		assertThat(properties.toOptions().getN()).isEqualTo(2);
		assertThat(properties.toOptions().getSize()).isEqualTo("1024*1024");
	}

	@Test
	void imagePropertiesStillBindLegacyOptions() {
		DashScopeImageProperties properties = bind(DashScopeImageProperties.CONFIG_PREFIX,
				DashScopeImageProperties.class,
				"spring.ai.dashscope.image.options.negative-prompt", "legacy-negative");

		assertThat(properties.toOptions().getNegativePrompt()).isEqualTo("legacy-negative");
	}

	@Test
	void multimodalEmbeddingPropertiesBindFlatOptions() {
		DashScopeMultimodalEmbeddingProperties properties = bind(DashScopeMultimodalEmbeddingProperties.CONFIG_PREFIX,
				DashScopeMultimodalEmbeddingProperties.class,
				"spring.ai.dashscope.embedding.multimodal.model", "vision-embedding-test",
				"spring.ai.dashscope.embedding.multimodal.fps", "12.5");

		assertThat(properties.toOptions().getModel()).isEqualTo("vision-embedding-test");
		assertThat(properties.toOptions().getFps()).isEqualTo(12.5f);
	}

	@Test
	void multimodalEmbeddingPropertiesStillBindLegacyOptions() {
		DashScopeMultimodalEmbeddingProperties properties = bind(DashScopeMultimodalEmbeddingProperties.CONFIG_PREFIX,
				DashScopeMultimodalEmbeddingProperties.class,
				"spring.ai.dashscope.embedding.multimodal.options.output-type", "dense");

		assertThat(properties.toOptions().getOutputType()).isEqualTo("dense");
	}

	@Test
	void rerankPropertiesBindFlatOptions() {
		DashScopeRerankProperties properties = bind(DashScopeRerankProperties.CONFIG_PREFIX,
				DashScopeRerankProperties.class,
				"spring.ai.dashscope.rerank.model", "rerank-test",
				"spring.ai.dashscope.rerank.top-n", "7",
				"spring.ai.dashscope.rerank.return-documents", "true");

		assertThat(properties.toOptions().getModel()).isEqualTo("rerank-test");
		assertThat(properties.toOptions().getTopN()).isEqualTo(7);
		assertThat(properties.toOptions().getReturnDocuments()).isTrue();
	}

	@Test
	void rerankPropertiesStillBindLegacyOptions() {
		DashScopeRerankProperties properties = bind(DashScopeRerankProperties.CONFIG_PREFIX,
				DashScopeRerankProperties.class,
				"spring.ai.dashscope.rerank.options.top-n", "3");

		assertThat(properties.toOptions().getTopN()).isEqualTo(3);
	}

	@Test
	void agentPropertiesBindFlatOptions() {
		DashScopeAgentProperties properties = bind(DashScopeAgentProperties.CONFIG_PREFIX,
				DashScopeAgentProperties.class,
				"spring.ai.dashscope.agent.app-id", "app-test",
				"spring.ai.dashscope.agent.model-id", "qwen-agent",
				"spring.ai.dashscope.agent.enable-thinking", "true");

		assertThat(properties.toOptions().getAppId()).isEqualTo("app-test");
		assertThat(properties.toOptions().getModelId()).isEqualTo("qwen-agent");
		assertThat(properties.toOptions().getEnableThinking()).isTrue();
	}

	@Test
	void agentPropertiesStillBindLegacyOptions() {
		DashScopeAgentProperties properties = bind(DashScopeAgentProperties.CONFIG_PREFIX,
				DashScopeAgentProperties.class,
				"spring.ai.dashscope.agent.options.app-id", "legacy-app");

		assertThat(properties.toOptions().getAppId()).isEqualTo("legacy-app");
	}

	@Test
	void videoPropertiesBindFlatOptions() {
		DashScopeVideoProperties properties = bind(DashScopeVideoProperties.CONFIG_PREFIX,
				DashScopeVideoProperties.class,
				"spring.ai.dashscope.video.model", "wan-video-test");

		assertThat(properties.toOptions().getModel()).isEqualTo("wan-video-test");
	}

	@Test
	void videoPropertiesStillBindLegacyOptions() {
		DashScopeVideoProperties properties = bind(DashScopeVideoProperties.CONFIG_PREFIX,
				DashScopeVideoProperties.class,
				"spring.ai.dashscope.video.options.model", "legacy-video");

		assertThat(properties.toOptions().getModel()).isEqualTo("legacy-video");
	}

	private static <T> T bind(String prefix, Class<T> propertiesType, String... pairs) {
		Map<String, String> source = new LinkedHashMap<>();
		for (int i = 0; i < pairs.length; i += 2) {
			source.put(pairs[i], pairs[i + 1]);
		}
		return new Binder(new MapConfigurationPropertySource(source)).bind(prefix, propertiesType).get();
	}

}
