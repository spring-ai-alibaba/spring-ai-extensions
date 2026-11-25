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
import com.alibaba.dashscope.audio.tts.SpeechSynthesizer;
import io.reactivex.Flowable;

import java.nio.ByteBuffer;

/**
 * Default {@link DashScopeSdkSpeechSynthesisClient} backed by DashScope Java SDK.
 */
public class DefaultDashScopeSdkSpeechSynthesisClient implements DashScopeSdkSpeechSynthesisClient {

	private final SpeechSynthesizer speechSynthesizer;

	public DefaultDashScopeSdkSpeechSynthesisClient() {
		this(new SpeechSynthesizer());
	}

	public DefaultDashScopeSdkSpeechSynthesisClient(SpeechSynthesizer speechSynthesizer) {
		this.speechSynthesizer = speechSynthesizer;
	}

	@Override
	public ByteBuffer call(SpeechSynthesisParam request) throws Exception {
		return this.speechSynthesizer.call(request);
	}

	@Override
	public Flowable<SpeechSynthesisResult> streamCall(SpeechSynthesisParam request) throws Exception {
		return this.speechSynthesizer.streamCall(request);
	}

}
