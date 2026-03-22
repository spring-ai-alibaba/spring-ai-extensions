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

import com.alibaba.dashscope.audio.tts.SpeechSynthesisParam;
import com.alibaba.dashscope.audio.tts.SpeechSynthesisResult;
import com.alibaba.dashscope.audio.tts.SpeechSynthesisUsage;
import io.reactivex.Flowable;
import org.junit.jupiter.api.Test;
import org.springframework.ai.audio.tts.TextToSpeechPrompt;
import org.springframework.ai.audio.tts.TextToSpeechResponse;
import reactor.test.StepVerifier;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DashScopeSdkAudioSpeechModelTests {

	@Test
	void testCallAndStream() {
		FakeSpeechClient client = new FakeSpeechClient();
		DashScopeSdkAudioSpeechModel model = DashScopeSdkAudioSpeechModel.builder()
			.speechClient(client)
			.defaultOptions(DashScopeSdkAudioSpeechOptions.builder().model("sambert-zhichu-v1").build())
			.apiKey("test-key")
			.build();

		TextToSpeechResponse callResponse = model.call(new TextToSpeechPrompt("hello"));
		assertThat(callResponse.getResult().getOutput()).containsExactly(1, 2, 3);
		assertThat((String) callResponse.getMetadata().get("model")).isEqualTo("sambert-zhichu-v1");

		StepVerifier.create(model.stream(new TextToSpeechPrompt("hello")))
			.assertNext(response -> assertThat(response.getResult().getOutput()).containsExactly(4, 5))
			.verifyComplete();
		assertThat(client.lastCallRequest).isNotNull();
		assertThat(client.lastStreamRequest).isNotNull();
	}

	@Test
	void testCallHandlesNullBuffer() {
		FakeSpeechClient client = new FakeSpeechClient();
		client.returnNullCallResult = true;
		DashScopeSdkAudioSpeechModel model = DashScopeSdkAudioSpeechModel.builder()
			.speechClient(client)
			.defaultOptions(DashScopeSdkAudioSpeechOptions.builder().model("sambert-zhichu-v1").build())
			.build();

		TextToSpeechResponse response = model.call(new TextToSpeechPrompt("hello"));
		assertThat(response.getResult().getOutput()).isEmpty();
	}

	@Test
	void testStreamFiltersEmptyAudioFrames() {
		FakeSpeechClient client = new FakeSpeechClient();
		SpeechSynthesisResult emptyResult = new SpeechSynthesisResult();
		emptyResult.setAudioFrame(null);
		SpeechSynthesisResult validResult = new SpeechSynthesisResult();
		validResult.setRequestId("req-valid");
		validResult.setAudioFrame(ByteBuffer.wrap(new byte[] { 9, 8 }));
		client.streamResults = List.of(emptyResult, validResult);

		DashScopeSdkAudioSpeechModel model = DashScopeSdkAudioSpeechModel.builder()
			.speechClient(client)
			.defaultOptions(DashScopeSdkAudioSpeechOptions.builder().model("sambert-zhichu-v1").build())
			.build();

		StepVerifier.create(model.stream(new TextToSpeechPrompt("hello")))
			.assertNext(response -> {
				assertThat(response.getResult().getOutput()).containsExactly(9, 8);
					assertThat((String) response.getMetadata().get("requestId")).isEqualTo("req-valid");
				})
			.verifyComplete();
	}

	@Test
	void testRequestMergesHeadersAndSetsStreamParameter() {
		FakeSpeechClient client = new FakeSpeechClient();
		DashScopeSdkAudioSpeechModel model = DashScopeSdkAudioSpeechModel.builder()
			.speechClient(client)
			.defaultOptions(DashScopeSdkAudioSpeechOptions.builder()
				.model("sambert-zhichu-v1")
				.httpHeaders(Map.of("x-default", "d", "x-override", "default"))
				.format("mp3")
				.textType("plain_text")
				.voice("longxiaochun")
				.speed(1.2)
				.build())
			.apiKey("api-key")
			.workspaceId("workspace-id")
			.connectionHeaders(Map.of("x-conn", "c", "x-override", "conn"))
			.build();

		model.stream(new TextToSpeechPrompt("hello", DashScopeSdkAudioSpeechOptions.builder()
			.httpHeaders(Map.of("x-runtime", "r", "x-override", "runtime"))
			.format("wav")
			.textType("ssml")
			.build())).blockLast();

		assertThat(client.lastStreamRequest.getHeaders()).containsEntry("x-conn", "c")
			.containsEntry("x-runtime", "r")
			.containsEntry("x-override", "runtime");
		assertThat(client.lastStreamRequest.getParameters()).containsEntry("stream", true)
			.containsEntry("voice", "longxiaochun")
			.containsEntry("speed", 1.2);
		assertThat(client.lastStreamRequest.getFormat().name()).isEqualTo("WAV");
		assertThat(client.lastStreamRequest.getTextType().name()).isEqualTo("SSML");
	}

	@Test
	void testCallFailureIsWrapped() {
		FakeSpeechClient client = new FakeSpeechClient();
		client.throwOnCall = new RuntimeException("call failed");
		DashScopeSdkAudioSpeechModel model = DashScopeSdkAudioSpeechModel.builder()
			.speechClient(client)
			.defaultOptions(DashScopeSdkAudioSpeechOptions.builder().model("sambert-zhichu-v1").build())
			.build();

		assertThatThrownBy(() -> model.call(new TextToSpeechPrompt("hello")))
			.isInstanceOf(com.alibaba.cloud.ai.dashscope.sdk.common.DashScopeSdkException.class)
			.hasMessageContaining("Failed to call DashScope SDK speech API")
			.hasCause(client.throwOnCall);
	}

	@Test
	void testStreamFailureIsWrapped() {
		FakeSpeechClient client = new FakeSpeechClient();
		client.throwOnStream = new RuntimeException("stream failed");
		DashScopeSdkAudioSpeechModel model = DashScopeSdkAudioSpeechModel.builder()
			.speechClient(client)
			.defaultOptions(DashScopeSdkAudioSpeechOptions.builder().model("sambert-zhichu-v1").build())
			.build();

		assertThatThrownBy(() -> model.stream(new TextToSpeechPrompt("hello")))
			.isInstanceOf(com.alibaba.cloud.ai.dashscope.sdk.common.DashScopeSdkException.class)
			.hasMessageContaining("Failed to stream DashScope SDK speech API")
			.hasCause(client.throwOnStream);
	}

	private static final class FakeSpeechClient implements DashScopeSdkSpeechSynthesisClient {

		private ByteBuffer callResult;

		private boolean returnNullCallResult;

		private List<SpeechSynthesisResult> streamResults;

		private RuntimeException throwOnCall;

		private RuntimeException throwOnStream;

		private SpeechSynthesisParam lastCallRequest;

		private SpeechSynthesisParam lastStreamRequest;

		@Override
		public ByteBuffer call(SpeechSynthesisParam request) {
			this.lastCallRequest = request;
			if (this.throwOnCall != null) {
				throw this.throwOnCall;
			}
			if (this.returnNullCallResult) {
				return null;
			}
			return this.callResult == null ? ByteBuffer.wrap(new byte[] { 1, 2, 3 }) : this.callResult;
		}

		@Override
		public Flowable<SpeechSynthesisResult> streamCall(SpeechSynthesisParam request) {
			this.lastStreamRequest = request;
			if (this.throwOnStream != null) {
				throw this.throwOnStream;
			}
			if (this.streamResults != null) {
				return Flowable.fromIterable(this.streamResults);
			}
			SpeechSynthesisResult result = new SpeechSynthesisResult();
			result.setRequestId("req-1");
			result.setAudioFrame(ByteBuffer.wrap(new byte[] { 4, 5 }));
			result.setUsage(SpeechSynthesisUsage.builder().characters(2).build());
			return Flowable.fromIterable(List.of(result));
		}

	}

}
