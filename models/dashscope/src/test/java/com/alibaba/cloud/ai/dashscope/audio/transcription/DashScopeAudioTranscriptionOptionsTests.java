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

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DashScopeAudioTranscriptionOptionsTests {

	@Test
	void testDefaultValues() {
		DashScopeAudioTranscriptionOptions options = new DashScopeAudioTranscriptionOptions();

		assertThat(options.getModel()).isNull();
		assertThat(options.getSampleRate()).isEqualTo(16000);
		assertThat(options.getFormat()).isEqualTo("pcm");
		assertThat(options.getTopK()).isNull();
		assertThat(options.getAsrOptions()).isNull();
	}

	@Test
	void testBuilderAndGetters() {
		DashScopeAudioTranscriptionOptions.Audio audio = new DashScopeAudioTranscriptionOptions.Audio("longyang", "wav");
		DashScopeAudioTranscriptionOptions.StreamOptions streamOptions =
				new DashScopeAudioTranscriptionOptions.StreamOptions(true);
		DashScopeAudioTranscriptionOptions.TranslationOptions translationOptions =
				new DashScopeAudioTranscriptionOptions.TranslationOptions("zh", "en");
		DashScopeAudioTranscriptionOptions.AsrOptions asrOptions = new DashScopeAudioTranscriptionOptions.AsrOptions();
		asrOptions.setLanguage("zh");
		asrOptions.setEnableItn(true);
		DashScopeAudioTranscriptionOptions.Resource resource = new DashScopeAudioTranscriptionOptions.Resource();
		resource.setResourceId("res-1");
		resource.setResourceType("custom_words");

		DashScopeAudioTranscriptionOptions options = DashScopeAudioTranscriptionOptions.builder()
			.model("gummy-realtime-v1")
			.vocabularyId("vocab-1")
			.sampleRate(8000)
			.format("wav")
			.sourceLanguage("zh")
			.transcriptionEnabled(true)
			.translationEnabled(true)
			.translationTargetLanguages(List.of("en", "ja"))
			.maxEndSilence(1500)
			.modalities(List.of("text"))
			.audio(audio)
			.stream(true)
			.streamOptions(streamOptions)
			.maxTokens(256)
			.seed(11)
			.temperature(0.5f)
			.topP(0.8f)
			.presencePenalty(0.2f)
			.topK(20)
			.repetitionPenalty(1.1f)
			.translationOptions(translationOptions)
			.disfluencyRemovalEnabled(true)
			.languageHints(List.of("zh"))
			.semanticPunctuationEnabled(true)
			.maxSentenceSilence(800)
			.multiThresholdModeEnabled(true)
			.punctuationPredictionEnabled(true)
			.heartbeat(true)
			.inverseTextNormalizationEnabled(true)
			.resources(List.of(resource))
			.timestampAlignmentEnabled(true)
			.specialWordFilter("*")
			.diarizationEnabled(true)
			.speakerCount(2)
			.channelId(List.of(0, 1))
			.asrOptions(asrOptions)
			.build();

		assertThat(options.getModel()).isEqualTo("gummy-realtime-v1");
		assertThat(options.getVocabularyId()).isEqualTo("vocab-1");
		assertThat(options.getSampleRate()).isEqualTo(8000);
		assertThat(options.getFormat()).isEqualTo("wav");
		assertThat(options.getSourceLanguage()).isEqualTo("zh");
		assertThat(options.getTranscriptionEnabled()).isTrue();
		assertThat(options.getTranslationEnabled()).isTrue();
		assertThat(options.getTranslationTargetLanguages()).containsExactly("en", "ja");
		assertThat(options.getAudio().getVoice()).isEqualTo("longyang");
		assertThat(options.getAudio().getFormat()).isEqualTo("wav");
		assertThat(options.getStream()).isTrue();
		assertThat(options.getStreamOptions().getIncludeUsage()).isTrue();
		assertThat(options.getTranslationOptions().getSourceLang()).isEqualTo("zh");
		assertThat(options.getTranslationOptions().getTargetLang()).isEqualTo("en");
		assertThat(options.getResources()).hasSize(1);
		assertThat(options.getResources().get(0).getResourceId()).isEqualTo("res-1");
		assertThat(options.getAsrOptions().getLanguage()).isEqualTo("zh");
		assertThat(options.getAsrOptions().getEnableItn()).isTrue();
	}

	@Test
	void testNestedClassSetters() {
		DashScopeAudioTranscriptionOptions.Audio audio = new DashScopeAudioTranscriptionOptions.Audio();
		audio.setVoice("voice-1");
		audio.setFormat("mp3");
		assertThat(audio.getVoice()).isEqualTo("voice-1");
		assertThat(audio.getFormat()).isEqualTo("mp3");

		DashScopeAudioTranscriptionOptions.StreamOptions streamOptions =
				new DashScopeAudioTranscriptionOptions.StreamOptions();
		streamOptions.setIncludeUsage(false);
		assertThat(streamOptions.getIncludeUsage()).isFalse();

		DashScopeAudioTranscriptionOptions.TranslationOptions translationOptions =
				new DashScopeAudioTranscriptionOptions.TranslationOptions();
		translationOptions.setSourceLang("en");
		translationOptions.setTargetLang("zh");
		assertThat(translationOptions.getSourceLang()).isEqualTo("en");
		assertThat(translationOptions.getTargetLang()).isEqualTo("zh");
	}

}
