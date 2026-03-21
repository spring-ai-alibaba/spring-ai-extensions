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

}
