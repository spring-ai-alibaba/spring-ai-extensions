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
	void testFromOptions() {
		DashScopeSdkAudioTranscriptionOptions original = DashScopeSdkAudioTranscriptionOptions.builder()
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
			.httpHeaders(Map.of("x-source", "s1"))
			.build();

		DashScopeSdkAudioTranscriptionOptions target = DashScopeSdkAudioTranscriptionOptions.fromOptions(original);

		assertThat(target.getModel()).isEqualTo(original.getModel());
		assertThat(target.getFileUrls()).isEqualTo(original.getFileUrls());
		assertThat(target.getPhraseId()).isEqualTo(original.getPhraseId());
		assertThat(target.getChannelId()).isEqualTo(original.getChannelId());
		assertThat(target.getDiarizationEnabled()).isEqualTo(original.getDiarizationEnabled());
		assertThat(target.getSpeakerCount()).isEqualTo(original.getSpeakerCount());
		assertThat(target.getDisfluencyRemovalEnabled()).isEqualTo(original.getDisfluencyRemovalEnabled());
		assertThat(target.getTimestampAlignmentEnabled()).isEqualTo(original.getTimestampAlignmentEnabled());
		assertThat(target.getSpecialWordFilter()).isEqualTo(original.getSpecialWordFilter());
		assertThat(target.getAudioEventDetectionEnabled()).isEqualTo(original.getAudioEventDetectionEnabled());
		assertThat(target.getHttpHeaders()).containsOnly(entry("x-source", "s1"));
	}

	@Test
	void testFromOptionsCreatesIndependentCollections() {
		DashScopeSdkAudioTranscriptionOptions original = DashScopeSdkAudioTranscriptionOptions.builder()
			.fileUrls(List.of("https://example.com/a.wav"))
			.channelId(List.of(0))
			.httpHeaders(Map.of("x-source", "s1"))
			.build();
		DashScopeSdkAudioTranscriptionOptions copy = DashScopeSdkAudioTranscriptionOptions.fromOptions(original);

		original.getFileUrls().add("https://example.com/b.wav");
		original.getChannelId().add(1);
		original.getHttpHeaders().put("x-source-2", "s2");
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
		DashScopeSdkAudioTranscriptionOptions original = DashScopeSdkAudioTranscriptionOptions.builder()
			.httpHeaders(null)
			.build();

		DashScopeSdkAudioTranscriptionOptions copy = DashScopeSdkAudioTranscriptionOptions.fromOptions(original);

		assertThat(copy.getFileUrls()).isNull();
		assertThat(copy.getChannelId()).isNull();
		assertThat(copy.getHttpHeaders()).isNotNull().isEmpty();
	}

}
