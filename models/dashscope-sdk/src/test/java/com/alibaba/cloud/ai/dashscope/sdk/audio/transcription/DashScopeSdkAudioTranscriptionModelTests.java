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

import com.alibaba.dashscope.audio.asr.transcription.TranscriptionParam;
import com.alibaba.dashscope.audio.asr.transcription.TranscriptionQueryParam;
import com.alibaba.dashscope.audio.asr.transcription.TranscriptionResult;
import com.alibaba.dashscope.common.TaskStatus;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;
import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.audio.transcription.AudioTranscriptionResponse;
import org.springframework.core.io.ByteArrayResource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DashScopeSdkAudioTranscriptionModelTests {

	@Test
	void testTranscriptionCall() {
		DashScopeSdkAudioTranscriptionModel model = DashScopeSdkAudioTranscriptionModel.builder()
			.transcriptionClient(new FakeTranscriptionClient())
			.defaultOptions(DashScopeSdkAudioTranscriptionOptions.builder().model("paraformer-v2").build())
			.apiKey("test-key")
			.build();

		DashScopeSdkAudioTranscriptionOptions options = DashScopeSdkAudioTranscriptionOptions.builder()
			.fileUrls(List.of("https://example.com/audio.wav"))
			.build();
		AudioTranscriptionPrompt prompt = new AudioTranscriptionPrompt(new ByteArrayResource(new byte[0]), options);

		AudioTranscriptionResponse response = model.call(prompt);

		assertThat(response.getResult().getOutput()).isEqualTo("hello transcription");
		assertThat((String) response.getMetadata().get("taskStatus")).isEqualTo("SUCCEEDED");
	}

	private static final class FakeTranscriptionClient implements DashScopeSdkTranscriptionClient {

		@Override
		public TranscriptionResult asyncCall(TranscriptionParam request) {
			TranscriptionResult result = new TranscriptionResult();
			result.setTaskId("task-1");
			result.setTaskStatus(TaskStatus.RUNNING);
			return result;
		}

		@Override
		public TranscriptionResult wait(TranscriptionQueryParam request) {
			TranscriptionResult result = new TranscriptionResult();
			result.setTaskId("task-1");
			result.setTaskStatus(TaskStatus.SUCCEEDED);
			JsonObject output = new JsonObject();
			output.addProperty("text", "hello transcription");
			result.setOutput(output);
			return result;
		}

	}

}
