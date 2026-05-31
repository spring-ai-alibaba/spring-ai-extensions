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

package com.alibaba.cloud.ai.dashscope.sdk.audio.tts;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

class DashScopeSdkAudioSpeechOptionsTests {

	@Test
	void testBuilderAndCopy() {
		DashScopeSdkAudioSpeechOptions options = DashScopeSdkAudioSpeechOptions.builder()
			.model("sambert-zhichu-v1")
			.voice("longyuan")
			.format("mp3")
			.speed(1.1)
			.textType("plain_text")
			.sampleRate(16000)
			.volume(50)
			.rate(1.0f)
			.pitch(0.0f)
			.wordTimestampEnabled(true)
			.phonemeTimestampEnabled(false)
			.httpHeaders(Map.of("x-test", "v"))
			.build();

		DashScopeSdkAudioSpeechOptions copy = options.copy();

		assertThat(copy).usingRecursiveComparison().isEqualTo(options);
		assertThat(copy).isNotSameAs(options);
	}

	@Test
	void testFromOptions() {
		DashScopeSdkAudioSpeechOptions original = DashScopeSdkAudioSpeechOptions.builder()
			.model("sambert-zhichu-v1")
			.voice("longyuan")
			.format("mp3")
			.speed(1.1)
			.textType("plain_text")
			.sampleRate(16000)
			.volume(50)
			.rate(1.0f)
			.pitch(0.0f)
			.wordTimestampEnabled(true)
			.phonemeTimestampEnabled(false)
			.httpHeaders(Map.of("x-source", "s1"))
			.build();

		DashScopeSdkAudioSpeechOptions target = DashScopeSdkAudioSpeechOptions.fromOptions(original);

		assertThat(target.getModel()).isEqualTo(original.getModel());
		assertThat(target.getVoice()).isEqualTo(original.getVoice());
		assertThat(target.getFormat()).isEqualTo(original.getFormat());
		assertThat(target.getSpeed()).isEqualTo(original.getSpeed());
		assertThat(target.getTextType()).isEqualTo(original.getTextType());
		assertThat(target.getSampleRate()).isEqualTo(original.getSampleRate());
		assertThat(target.getVolume()).isEqualTo(original.getVolume());
		assertThat(target.getRate()).isEqualTo(original.getRate());
		assertThat(target.getPitch()).isEqualTo(original.getPitch());
		assertThat(target.getWordTimestampEnabled()).isEqualTo(original.getWordTimestampEnabled());
		assertThat(target.getPhonemeTimestampEnabled()).isEqualTo(original.getPhonemeTimestampEnabled());
		assertThat(target.getHttpHeaders()).containsOnly(entry("x-source", "s1"));
	}

	@Test
	void testDefaultValues() {
		DashScopeSdkAudioSpeechOptions options = DashScopeSdkAudioSpeechOptions.builder().build();

		assertThat(options.getModel()).isNull();
		assertThat(options.getVoice()).isNull();
		assertThat(options.getFormat()).isNull();
		assertThat(options.getSpeed()).isNull();
		assertThat(options.getTextType()).isNull();
		assertThat(options.getSampleRate()).isNull();
		assertThat(options.getVolume()).isNull();
		assertThat(options.getRate()).isNull();
		assertThat(options.getPitch()).isNull();
		assertThat(options.getWordTimestampEnabled()).isNull();
		assertThat(options.getPhonemeTimestampEnabled()).isNull();
		assertThat(options.getHttpHeaders()).isNotNull().isEmpty();
	}

	@Test
	void testFromOptionsCreatesIndependentHttpHeaders() {
		DashScopeSdkAudioSpeechOptions original = DashScopeSdkAudioSpeechOptions.builder()
			.httpHeaders(Map.of("x-source", "s1"))
			.build();
		DashScopeSdkAudioSpeechOptions copy = original.copy();

		original.getHttpHeaders().put("x-source-2", "s2");
		copy.getHttpHeaders().put("x-copy", "c1");

		assertThat(original.getHttpHeaders()).containsOnly(entry("x-source", "s1"), entry("x-source-2", "s2"));
		assertThat(copy.getHttpHeaders()).containsOnly(entry("x-source", "s1"), entry("x-copy", "c1"));
	}

	@Test
	void testFromOptionsHandlesNullHttpHeaders() {
		DashScopeSdkAudioSpeechOptions original = DashScopeSdkAudioSpeechOptions.builder()
			.httpHeaders(null)
			.build();

		DashScopeSdkAudioSpeechOptions copy = DashScopeSdkAudioSpeechOptions.fromOptions(original);

		assertThat(copy.getHttpHeaders()).isNotNull().isEmpty();
	}

}
