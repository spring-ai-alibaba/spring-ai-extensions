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

import com.alibaba.cloud.ai.dashscope.audio.AudioCommonType.TextType;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DashScopeAudioSpeechOptionsTests {

	@Test
	void testDefaultValues() {
		DashScopeAudioSpeechOptions options = new DashScopeAudioSpeechOptions();

		assertThat(options.getModel()).isNull();
		assertThat(options.getTextType()).isEqualTo(TextType.PLAIN_TEXT.getValue());
		assertThat(options.getVoice()).isEqualTo("longanyang");
		assertThat(options.getFormat()).isNull();
		assertThat(options.getSampleRate()).isNull();
		assertThat(options.getSpeed()).isNull();
	}

	@Test
	void testBuilderAndGetters() {
		DashScopeAudioSpeechOptions options = DashScopeAudioSpeechOptions.builder()
			.model("sambert-zhichu-v1")
			.textType("ssml")
			.voice("longxiaochun")
			.format("mp3")
			.sampleRate(16000)
			.volume(50)
			.rate(1.0f)
			.pitch(0.2f)
			.enableSsml(true)
			.bitRate(192000)
			.speed(1.1)
			.seed(123)
			.wordTimestampEnabled(true)
			.phonemeTimestampEnabled(false)
			.languageHints(List.of("zh", "en"))
			.instruction("speak naturally")
			.optimizeInstructions(true)
			.enableAigcTag(true)
			.aigcPropagator("p1")
			.aigcPropagateId("pid-1")
			.languageType("zh")
			.build();

		assertThat(options.getModel()).isEqualTo("sambert-zhichu-v1");
		assertThat(options.getTextType()).isEqualTo("ssml");
		assertThat(options.getVoice()).isEqualTo("longxiaochun");
		assertThat(options.getFormat()).isEqualTo("mp3");
		assertThat(options.getSampleRate()).isEqualTo(16000);
		assertThat(options.getVolume()).isEqualTo(50);
		assertThat(options.getRate()).isEqualTo(1.0f);
		assertThat(options.getPitch()).isEqualTo(0.2f);
		assertThat(options.getEnableSsml()).isTrue();
		assertThat(options.getBitRate()).isEqualTo(192000);
		assertThat(options.getSpeed()).isEqualTo(1.1);
		assertThat(options.getSeed()).isEqualTo(123);
		assertThat(options.getWordTimestampEnabled()).isTrue();
		assertThat(options.getPhonemeTimestampEnabled()).isFalse();
		assertThat(options.getLanguageHints()).containsExactly("zh", "en");
		assertThat(options.getInstruction()).isEqualTo("speak naturally");
		assertThat(options.getOptimizeInstructions()).isTrue();
		assertThat(options.getEnableAigcTag()).isTrue();
		assertThat(options.getAigcPropagator()).isEqualTo("p1");
		assertThat(options.getAigcPropagateId()).isEqualTo("pid-1");
		assertThat(options.getLanguageType()).isEqualTo("zh");
	}

	@Test
	void testSetResponseFormatAlias() {
		DashScopeAudioSpeechOptions options = new DashScopeAudioSpeechOptions();
		options.setResponseFormat("wav");

		assertThat(options.getFormat()).isEqualTo("wav");
	}

	@Test
	void testCopyCreatesIndependentObject() {
		List<String> languageHints = new ArrayList<>(List.of("zh"));
		DashScopeAudioSpeechOptions original = DashScopeAudioSpeechOptions.builder()
			.model("sambert-zhichu-v1")
			.languageHints(languageHints)
			.build();

		DashScopeAudioSpeechOptions copy = original.copy();
		languageHints.add("en");
		copy.getLanguageHints().add("ja");
		copy.setVoice("longxiaoyun");

		assertThat(copy).isNotSameAs(original);
		assertThat(original.getLanguageHints()).containsExactly("zh", "en");
		assertThat(copy.getLanguageHints()).containsExactly("zh", "ja");
		assertThat(original.getVoice()).isEqualTo("longanyang");
		assertThat(copy.getVoice()).isEqualTo("longxiaoyun");
	}

}
