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

package com.alibaba.cloud.ai.dashscope.sdk.image;

import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesis;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesisParam;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesisResult;

/**
 * Default {@link DashScopeSdkImageSynthesisClient} backed by DashScope Java SDK.
 */
public class DefaultDashScopeSdkImageSynthesisClient implements DashScopeSdkImageSynthesisClient {

	private final ImageSynthesis imageSynthesis;

	public DefaultDashScopeSdkImageSynthesisClient() {
		this(new ImageSynthesis());
	}

	public DefaultDashScopeSdkImageSynthesisClient(ImageSynthesis imageSynthesis) {
		this.imageSynthesis = imageSynthesis;
	}

	@Override
	public ImageSynthesisResult call(ImageSynthesisParam request) throws Exception {
		return this.imageSynthesis.call(request);
	}

	@Override
	public ImageSynthesisResult asyncCall(ImageSynthesisParam request) throws Exception {
		return this.imageSynthesis.asyncCall(request);
	}

	@Override
	public ImageSynthesisResult wait(ImageSynthesisResult request, String intervalMs) throws Exception {
		return this.imageSynthesis.wait(request, intervalMs);
	}

}
