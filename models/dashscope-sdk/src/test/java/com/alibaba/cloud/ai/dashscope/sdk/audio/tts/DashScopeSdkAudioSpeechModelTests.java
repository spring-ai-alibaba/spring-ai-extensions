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

import static org.assertj.core.api.Assertions.assertThat;

class DashScopeSdkAudioSpeechModelTests {

	@Test
	void testCallAndStream() {
		DashScopeSdkAudioSpeechModel model = DashScopeSdkAudioSpeechModel.builder()
			.speechClient(new FakeSpeechClient())
			.defaultOptions(DashScopeSdkAudioSpeechOptions.builder().model("sambert-zhichu-v1").build())
			.apiKey("test-key")
			.build();

		TextToSpeechResponse callResponse = model.call(new TextToSpeechPrompt("hello"));
		assertThat(callResponse.getResult().getOutput()).containsExactly(1, 2, 3);

		StepVerifier.create(model.stream(new TextToSpeechPrompt("hello")))
			.assertNext(response -> assertThat(response.getResult().getOutput()).containsExactly(4, 5))
			.verifyComplete();
	}

	private static final class FakeSpeechClient implements DashScopeSdkSpeechSynthesisClient {

		@Override
		public ByteBuffer call(SpeechSynthesisParam request) {
			return ByteBuffer.wrap(new byte[] { 1, 2, 3 });
		}

		@Override
		public Flowable<SpeechSynthesisResult> streamCall(SpeechSynthesisParam request) {
			SpeechSynthesisResult result = new SpeechSynthesisResult();
			result.setRequestId("req-1");
			result.setAudioFrame(ByteBuffer.wrap(new byte[] { 4, 5 }));
			result.setUsage(SpeechSynthesisUsage.builder().characters(2).build());
			return Flowable.fromIterable(List.of(result));
		}

	}

}
