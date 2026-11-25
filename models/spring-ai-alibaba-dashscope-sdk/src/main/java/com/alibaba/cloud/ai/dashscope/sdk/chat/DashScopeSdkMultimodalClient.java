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

package com.alibaba.cloud.ai.dashscope.sdk.chat;

import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationParam;
import com.alibaba.dashscope.api.SynchronizeHalfDuplexApi;
import com.alibaba.dashscope.common.DashScopeResult;
import com.alibaba.dashscope.protocol.ApiServiceOption;
import com.alibaba.dashscope.protocol.StreamingMode;
import io.reactivex.Flowable;
import org.jspecify.annotations.Nullable;

/** Uses the SDK's multimodal transport while retaining provider-specific message fields. */
final class DashScopeSdkMultimodalClient {

	private final SynchronizeHalfDuplexApi<MultiModalConversationParam> sync;

	private final SynchronizeHalfDuplexApi<MultiModalConversationParam> streaming;

	DashScopeSdkMultimodalClient(@Nullable String baseUrl) {
		this.sync = new SynchronizeHalfDuplexApi<>(options(baseUrl, false));
		this.streaming = new SynchronizeHalfDuplexApi<>(options(baseUrl, true));
	}

	DashScopeResult call(MultiModalConversationParam request) throws Exception {
		return this.sync.call(request);
	}

	Flowable<DashScopeResult> stream(MultiModalConversationParam request) throws Exception {
		return this.streaming.streamCall(request);
	}

	private static ApiServiceOption options(@Nullable String baseUrl, boolean stream) {
		// The SDK's typed MultiModalConversationResult drops encrypted_content.
		return ApiServiceOption.builder()
			.baseHttpUrl(baseUrl)
			.taskGroup("aigc")
			.task("multimodal-generation")
			.function("generation")
			.streamingMode(stream ? StreamingMode.OUT : StreamingMode.NONE)
			.isSSE(stream)
			.build();
	}

}
