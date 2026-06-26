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

import com.alibaba.cloud.ai.dashscope.audio.transcription.DashScopeTranscriptionResponse.DashScopeAudioTranscription;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DashScopeTranscriptionResponseTests {

	private final ObjectMapper objectMapper = new ObjectMapper();

	// The DashScope response carries channel_id / content_duration_in_milliseconds /
	// sentences at the top level; these must be mapped into the metadata (see issue #260).
	@Test
	void testDeserializeMapsTopLevelFieldsIntoMetadata() throws Exception {
		String json = """
				{
				  "text": "hello world",
				  "channel_id": 0,
				  "content_duration_in_milliseconds": 1234,
				  "sentences": [
				    { "begin_time": 0, "end_time": 1000, "text": "hello world" }
				  ]
				}
				""";

		DashScopeAudioTranscription transcription = objectMapper.readValue(json, DashScopeAudioTranscription.class);

		assertThat(transcription.getText()).isEqualTo("hello world");
		assertThat(transcription.getMetadata()).isNotNull();
		assertThat(transcription.getMetadata().channelId()).isEqualTo(0);
		assertThat(transcription.getMetadata().contentDurationInMilliseconds()).isEqualTo(1234);
		assertThat(transcription.getMetadata().sentences()).hasSize(1);
		assertThat(transcription.getMetadata().sentences().get(0).text()).isEqualTo("hello world");
	}

	// The "transcript" alias of the text field must still be honored. A text-only response
	// (no channel_id / content_duration_in_milliseconds / sentences) must not create an empty
	// metadata object — getMetadata() stays null, matching the prior behavior.
	@Test
	void testDeserializeHonorsTranscriptAliasWithoutCreatingEmptyMetadata() throws Exception {
		String json = """
				{ "transcript": "aliased text" }
				""";

		DashScopeAudioTranscription transcription = objectMapper.readValue(json, DashScopeAudioTranscription.class);

		assertThat(transcription.getText()).isEqualTo("aliased text");
		assertThat(transcription.getMetadata()).isNull();
	}

	// The original single-argument constructor must remain available for backward
	// compatibility with clients compiled against the previous API.
	@Test
	void testSingleArgConstructorStillAvailable() {
		DashScopeAudioTranscription transcription = new DashScopeAudioTranscription("plain text");

		assertThat(transcription.getText()).isEqualTo("plain text");
		assertThat(transcription.getMetadata()).isNull();
	}

}
