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
package com.alibaba.cloud.ai.dashscope.metadata.audio;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.ai.audio.transcription.AudioTranscriptionResponseMetadata;
import org.springframework.ai.chat.metadata.EmptyUsage;

import static org.assertj.core.api.Assertions.assertThat;

class DashScopeAudioTranscriptionResponseMetadataTests {

	@Test
	void exposesDurationThroughSpringAiUsage() {
		var usage = new DashScopeAudioTranscriptionResponseMetadata.Usage(1500);
		var metadata = new DashScopeAudioTranscriptionResponseMetadata(null, usage);
		AudioTranscriptionResponseMetadata springMetadata = metadata;

		assertThat(springMetadata.getUsage().getNativeUsage()).isEqualTo(Map.of("duration", 1500));
		assertThat(springMetadata.getUsage().getTotalTokens()).isZero();
		assertThat(metadata.getDashScopeUsage()).isSameAs(usage);
	}

	@Test
	void keepsSpringAiDefaultWhenDurationIsAbsent() {
		var metadata = new DashScopeAudioTranscriptionResponseMetadata(List.of());

		assertThat(metadata.getUsage()).isInstanceOf(EmptyUsage.class);
		assertThat(metadata.getDashScopeUsage()).isNull();
	}

}
