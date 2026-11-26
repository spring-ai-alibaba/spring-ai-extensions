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
import com.alibaba.dashscope.audio.asr.transcription.TranscriptionTaskResult;
import com.alibaba.dashscope.common.TaskStatus;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;
import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.audio.transcription.AudioTranscriptionResponse;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.UrlResource;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DashScopeSdkAudioTranscriptionModelTests {

	@Test
	void testTranscriptionCall() {
		FakeTranscriptionClient client = new FakeTranscriptionClient();
		DashScopeSdkAudioTranscriptionModel model = DashScopeSdkAudioTranscriptionModel.builder()
			.transcriptionClient(client)
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
		assertThat(client.lastAsyncRequest.getModel()).isEqualTo("paraformer-v2");
		assertThat(client.asyncCallCount).isEqualTo(1);
		assertThat(client.waitCallCount).isEqualTo(1);
	}

	@Test
	void testUsesPromptUrlWhenOptionsDoNotProvideFileUrls() throws Exception {
		FakeTranscriptionClient client = new FakeTranscriptionClient();
		DashScopeSdkAudioTranscriptionModel model = DashScopeSdkAudioTranscriptionModel.builder()
			.transcriptionClient(client)
			.defaultOptions(DashScopeSdkAudioTranscriptionOptions.builder().model("paraformer-v2").build())
			.build();

		AudioTranscriptionPrompt prompt = new AudioTranscriptionPrompt(new UrlResource("https://example.com/a.wav"),
				DashScopeSdkAudioTranscriptionOptions.builder().build());
		model.call(prompt);

		assertThat(client.lastAsyncRequest.getFileUrls()).containsExactly("https://example.com/a.wav");
	}

	@Test
	void testThrowsWhenNoValidFileUrlExists() {
		DashScopeSdkAudioTranscriptionModel model = DashScopeSdkAudioTranscriptionModel.builder()
			.transcriptionClient(new FakeTranscriptionClient())
			.defaultOptions(DashScopeSdkAudioTranscriptionOptions.builder().model("paraformer-v2").build())
			.build();

		AudioTranscriptionPrompt prompt = new AudioTranscriptionPrompt(new ByteArrayResource(new byte[] { 1, 2, 3 }),
				DashScopeSdkAudioTranscriptionOptions.builder().build());

		assertThatThrownBy(() -> model.call(prompt)).isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("No valid file URLs found");
	}

	@Test
	void testSubmitWithoutTaskIdReturnsSubmitResponseDirectly() {
		FakeTranscriptionClient client = new FakeTranscriptionClient();
		TranscriptionResult submitResult = new TranscriptionResult();
		submitResult.setTaskStatus(TaskStatus.SUCCEEDED);
		JsonObject output = new JsonObject();
		output.addProperty("text", "submit-only text");
		submitResult.setOutput(output);
		client.asyncResult = submitResult;

		DashScopeSdkAudioTranscriptionModel model = DashScopeSdkAudioTranscriptionModel.builder()
			.transcriptionClient(client)
			.defaultOptions(DashScopeSdkAudioTranscriptionOptions.builder()
				.model("paraformer-v2")
				.fileUrls(List.of("https://example.com/a.wav"))
				.build())
			.build();

		AudioTranscriptionResponse response = model.call(
				new AudioTranscriptionPrompt(new ByteArrayResource(new byte[0]), DashScopeSdkAudioTranscriptionOptions.builder().build()));

		assertThat(response.getResult().getOutput()).isEqualTo("submit-only text");
		assertThat(client.asyncCallCount).isEqualTo(1);
		assertThat(client.waitCallCount).isZero();
	}

	@Test
	void testWrapsSubmitFailure() {
		FakeTranscriptionClient client = new FakeTranscriptionClient();
		client.throwOnAsync = new RuntimeException("submit failed");
		DashScopeSdkAudioTranscriptionModel model = DashScopeSdkAudioTranscriptionModel.builder()
			.transcriptionClient(client)
			.defaultOptions(DashScopeSdkAudioTranscriptionOptions.builder()
				.model("paraformer-v2")
				.fileUrls(List.of("https://example.com/a.wav"))
				.build())
			.build();

		AudioTranscriptionPrompt prompt = new AudioTranscriptionPrompt(new ByteArrayResource(new byte[0]),
				DashScopeSdkAudioTranscriptionOptions.builder().build());

		assertThatThrownBy(() -> model.call(prompt))
			.isInstanceOf(com.alibaba.cloud.ai.dashscope.sdk.common.DashScopeSdkException.class)
			.hasMessageContaining("Failed to submit DashScope SDK transcription request")
			.hasCause(client.throwOnAsync);
	}

	@Test
	void testWrapsWaitFailure() {
		FakeTranscriptionClient client = new FakeTranscriptionClient();
		client.throwOnWait = new RuntimeException("wait failed");
		DashScopeSdkAudioTranscriptionModel model = DashScopeSdkAudioTranscriptionModel.builder()
			.transcriptionClient(client)
			.defaultOptions(DashScopeSdkAudioTranscriptionOptions.builder()
				.model("paraformer-v2")
				.fileUrls(List.of("https://example.com/a.wav"))
				.build())
			.build();

		AudioTranscriptionPrompt prompt = new AudioTranscriptionPrompt(new ByteArrayResource(new byte[0]),
				DashScopeSdkAudioTranscriptionOptions.builder().build());

		assertThatThrownBy(() -> model.call(prompt))
			.isInstanceOf(com.alibaba.cloud.ai.dashscope.sdk.common.DashScopeSdkException.class)
			.hasMessageContaining("Failed to fetch DashScope SDK transcription result")
			.hasCause(client.throwOnWait);
	}

	@Test
	void testExtractsTextFromNestedOutputJsonAndMergesHeaders() {
		FakeTranscriptionClient client = new FakeTranscriptionClient();
		JsonObject output = new JsonObject();
		JsonObject sentence = new JsonObject();
		sentence.addProperty("text", "hello");
		JsonObject transcript = new JsonObject();
		transcript.addProperty("transcript", "world");
		output.add("sentence", sentence);
		output.add("item", transcript);

		TranscriptionTaskResult resultItem = new TranscriptionTaskResult();
		resultItem.setMessage("ignored");
		client.waitResult = new TranscriptionResult();
		client.waitResult.setTaskId("task-1");
		client.waitResult.setTaskStatus(TaskStatus.SUCCEEDED);
		client.waitResult.setOutput(output);
		client.waitResult.setResults(List.of(resultItem));
		client.waitResult.setUsage(new JsonObject());

		DashScopeSdkAudioTranscriptionModel model = DashScopeSdkAudioTranscriptionModel.builder()
			.transcriptionClient(client)
			.defaultOptions(DashScopeSdkAudioTranscriptionOptions.builder()
				.model("paraformer-v2")
				.fileUrls(List.of("https://example.com/a.wav"))
				.httpHeaders(Map.of("x-default", "d", "x-override", "default"))
				.build())
			.apiKey("api-key")
			.workspaceId("workspace-id")
			.connectionHeaders(Map.of("x-conn", "c", "x-override", "conn"))
			.build();

		AudioTranscriptionResponse response = model.call(
				new AudioTranscriptionPrompt(new ByteArrayResource(new byte[0]),
						DashScopeSdkAudioTranscriptionOptions.builder()
							.httpHeaders(Map.of("x-runtime", "r", "x-override", "runtime"))
							.build()));

		assertThat(response.getResult().getOutput()).isEqualTo("hello\nworld");
		assertThat((Object) response.getMetadata().get("resultCount")).isEqualTo(1);
		assertThat(response.getMetadata().containsKey("usage")).isTrue();
		assertThat(client.lastAsyncRequest.getHeaders()).containsEntry("x-conn", "c")
			.containsEntry("x-runtime", "r")
			.containsEntry("x-override", "runtime");
	}

	private static final class FakeTranscriptionClient implements DashScopeSdkTranscriptionClient {

		private TranscriptionResult asyncResult;

		private TranscriptionResult waitResult;

		private RuntimeException throwOnAsync;

		private RuntimeException throwOnWait;

		private int asyncCallCount;

		private int waitCallCount;

		private TranscriptionParam lastAsyncRequest;

		private TranscriptionQueryParam lastWaitRequest;

		@Override
		public TranscriptionResult asyncCall(TranscriptionParam request) {
			this.asyncCallCount++;
			this.lastAsyncRequest = request;
			if (this.throwOnAsync != null) {
				throw this.throwOnAsync;
			}
			if (this.asyncResult != null) {
				return this.asyncResult;
			}
			TranscriptionResult result = new TranscriptionResult();
			result.setTaskId("task-1");
			result.setTaskStatus(TaskStatus.RUNNING);
			return result;
		}

		@Override
		public TranscriptionResult wait(TranscriptionQueryParam request) {
			this.waitCallCount++;
			this.lastWaitRequest = request;
			if (this.throwOnWait != null) {
				throw this.throwOnWait;
			}
			if (this.waitResult != null) {
				return this.waitResult;
			}
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
