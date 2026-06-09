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
package com.alibaba.cloud.ai.dashscope.metadata;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatApiSpec.TokenUsage;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.util.Assert;

/**
 * {@link Usage} implementation for {@literal DashScopeAI}.
 *
 * @author Ken
 */
public class DashScopeAiUsage implements Usage {

	private final Object usage;

	private final Integer inputTokens;

	private final Integer outputTokens;

	private final Integer totalTokens;

	protected DashScopeAiUsage(TokenUsage usage) {
		Assert.notNull(usage, "DashScope Usage must not be null");
		this.usage = usage;
		this.inputTokens = usage.inputTokens();
		this.outputTokens = usage.outputTokens();
		this.totalTokens = usage.totalTokens();
	}

	public static DashScopeAiUsage from(TokenUsage usage) {
		return new DashScopeAiUsage(usage);
	}

	protected Object getUsage() {
		return this.usage;
	}

	@Override
	public Integer getPromptTokens() {
		return this.inputTokens;
	}

	@Override
	public Integer getCompletionTokens() {
		return this.outputTokens;
	}

	@Override
	public Integer getTotalTokens() {
		if (this.totalTokens != null) {
			return this.totalTokens;
		}
		Integer promptTokens = getPromptTokens();
		Integer completionTokens = getCompletionTokens();
		if (promptTokens == null || completionTokens == null) {
			return 0;
		}
		return promptTokens + completionTokens;
	}

	/**
	 * Returns the DashScope native {@link TokenUsage} object containing all original fields.
	 */
	@Override
	public Object getNativeUsage() {
		return this.usage;
	}


	@Override
	public String toString() {
		return getUsage().toString();
	}

}
