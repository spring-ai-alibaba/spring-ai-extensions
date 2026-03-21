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

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import io.reactivex.Flowable;

/**
 * Default {@link DashScopeSdkGenerationClient} backed by DashScope Java SDK.
 */
public class DefaultDashScopeSdkGenerationClient implements DashScopeSdkGenerationClient {

	private final Generation generation;

	public DefaultDashScopeSdkGenerationClient() {
		this(new Generation());
	}

	public DefaultDashScopeSdkGenerationClient(Generation generation) {
		this.generation = generation;
	}

	@Override
	public GenerationResult call(GenerationParam generationParam) throws Exception {
		return this.generation.call(generationParam);
	}

	@Override
	public Flowable<GenerationResult> stream(GenerationParam generationParam) throws Exception {
		return this.generation.streamCall(generationParam);
	}

}
