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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

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

	@Test
	void testFromOptionsReturnsNullForNullInput() {
		assertThat(DashScopeSdkAudioTranscriptionOptions.fromOptions(null)).isNull();
	}

	@Test
	void testFromOptionsCreatesIndependentCollections() {
		List<String> fileUrls = new ArrayList<>(List.of("https://example.com/a.wav"));
		List<Integer> channelIds = new ArrayList<>(List.of(0));
		Map<String, String> headers = new HashMap<>();
		headers.put("x-source", "s1");

		DashScopeSdkAudioTranscriptionOptions original = DashScopeSdkAudioTranscriptionOptions.builder()
			.fileUrls(fileUrls)
			.channelId(channelIds)
			.httpHeaders(headers)
			.build();
		DashScopeSdkAudioTranscriptionOptions copy = DashScopeSdkAudioTranscriptionOptions.fromOptions(original);

		fileUrls.add("https://example.com/b.wav");
		channelIds.add(1);
		headers.put("x-source-2", "s2");
		copy.getFileUrls().add("https://example.com/c.wav");
		copy.getChannelId().add(2);
		copy.getHttpHeaders().put("x-copy", "c1");

		assertThat(original.getFileUrls()).containsExactly("https://example.com/a.wav", "https://example.com/b.wav");
		assertThat(copy.getFileUrls()).containsExactly("https://example.com/a.wav", "https://example.com/c.wav");
		assertThat(original.getChannelId()).containsExactly(0, 1);
		assertThat(copy.getChannelId()).containsExactly(0, 2);
		assertThat(original.getHttpHeaders()).containsOnly(entry("x-source", "s1"), entry("x-source-2", "s2"));
		assertThat(copy.getHttpHeaders()).containsOnly(entry("x-source", "s1"), entry("x-copy", "c1"));
	}

	@Test
	void testFromOptionsHandlesNullCollections() {
		DashScopeSdkAudioTranscriptionOptions original = new DashScopeSdkAudioTranscriptionOptions();
		original.setFileUrls(null);
		original.setChannelId(null);
		original.setHttpHeaders(null);

		DashScopeSdkAudioTranscriptionOptions copy = DashScopeSdkAudioTranscriptionOptions.fromOptions(original);

		assertThat(copy.getFileUrls()).isNull();
		assertThat(copy.getChannelId()).isNull();
		assertThat(copy.getHttpHeaders()).isNotNull().isEmpty();
	}

}
