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

import com.alibaba.dashscope.audio.asr.transcription.Transcription;
import com.alibaba.dashscope.audio.asr.transcription.TranscriptionParam;
import com.alibaba.dashscope.audio.asr.transcription.TranscriptionQueryParam;
import com.alibaba.dashscope.audio.asr.transcription.TranscriptionResult;

/**
 * Default {@link DashScopeSdkTranscriptionClient} backed by DashScope Java SDK.
 */
public class DefaultDashScopeSdkTranscriptionClient implements DashScopeSdkTranscriptionClient {

	private final Transcription transcription;

	public DefaultDashScopeSdkTranscriptionClient() {
		this(new Transcription());
	}

	public DefaultDashScopeSdkTranscriptionClient(Transcription transcription) {
		this.transcription = transcription;
	}

	@Override
	public TranscriptionResult asyncCall(TranscriptionParam request) {
		return this.transcription.asyncCall(request);
	}

	@Override
	public TranscriptionResult wait(TranscriptionQueryParam request) {
		return this.transcription.wait(request);
	}

}
