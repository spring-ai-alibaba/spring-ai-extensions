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

package com.alibaba.cloud.ai.dashscope.sdk.audio.transcription;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class DashScopeSdkAudioTranscriptionOptionsTests {

	@Test
	void testBuilderAndCopy() {
		DashScopeSdkAudioTranscriptionOptions options = DashScopeSdkAudioTranscriptionOptions.builder()
			.model("paraformer-v2")
			.fileUrls(List.of("https://example.com/a.wav"))
			.phraseId("p1")
			.channelId(List.of(0))
			.diarizationEnabled(true)
			.speakerCount(2)
			.disfluencyRemovalEnabled(true)
			.timestampAlignmentEnabled(true)
			.specialWordFilter("*")
			.audioEventDetectionEnabled(false)
			.httpHeaders(Map.of("x-test", "v"))
			.build();

		DashScopeSdkAudioTranscriptionOptions copy = DashScopeSdkAudioTranscriptionOptions.fromOptions(options);
		assertThat(copy).usingRecursiveComparison().isEqualTo(options);
		assertThat(copy).isNotSameAs(options);
	}

}
