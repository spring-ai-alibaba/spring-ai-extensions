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

package com.alibaba.cloud.ai.dashscope.sdk.metadata;

import com.alibaba.dashscope.aigc.generation.GenerationUsage;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.util.Assert;

/**
 * {@link Usage} implementation for DashScope SDK native usage.
 */
public class DashScopeSdkUsage implements Usage {

	private final GenerationUsage usage;

	protected DashScopeSdkUsage(GenerationUsage usage) {
		Assert.notNull(usage, "DashScope SDK usage must not be null");
		this.usage = usage;
	}

	public static DashScopeSdkUsage from(GenerationUsage usage) {
		return new DashScopeSdkUsage(usage);
	}

	@Override
	public Integer getPromptTokens() {
		return this.usage.getInputTokens();
	}

	@Override
	public Integer getCompletionTokens() {
		return this.usage.getOutputTokens();
	}

	@Override
	public Integer getTotalTokens() {
		Integer totalTokens = this.usage.getTotalTokens();
		if (totalTokens != null) {
			return totalTokens;
		}
		Integer promptTokens = getPromptTokens() == null ? 0 : getPromptTokens();
		Integer completionTokens = getCompletionTokens() == null ? 0 : getCompletionTokens();
		return promptTokens + completionTokens;
	}

	@Override
	public Object getNativeUsage() {
		return this.usage;
	}

	@Override
	public String toString() {
		return this.usage.toString();
	}

}
