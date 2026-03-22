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
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DashScopeSdkUsageTests {

	@Test
	void testUsageFieldsAndNativeUsage() {
		GenerationUsage usage = GenerationUsage.builder().inputTokens(10).outputTokens(6).totalTokens(16).build();

		DashScopeSdkUsage sdkUsage = DashScopeSdkUsage.from(usage);

		assertThat(sdkUsage.getPromptTokens()).isEqualTo(10);
		assertThat(sdkUsage.getCompletionTokens()).isEqualTo(6);
		assertThat(sdkUsage.getTotalTokens()).isEqualTo(16);
		assertThat(sdkUsage.getNativeUsage()).isSameAs(usage);
		assertThat(sdkUsage.toString()).isEqualTo(usage.toString());
	}

	@Test
	void testTotalTokensFallsBackToPromptAndCompletion() {
		GenerationUsage usage = GenerationUsage.builder().inputTokens(3).outputTokens(2).build();

		DashScopeSdkUsage sdkUsage = DashScopeSdkUsage.from(usage);
		assertThat(sdkUsage.getTotalTokens()).isEqualTo(5);
	}

	@Test
	void testTotalTokensTreatsNullPromptOrCompletionAsZero() {
		GenerationUsage usage = GenerationUsage.builder().inputTokens(null).outputTokens(4).build();

		DashScopeSdkUsage sdkUsage = DashScopeSdkUsage.from(usage);
		assertThat(sdkUsage.getTotalTokens()).isEqualTo(4);
	}

	@Test
	void testFromRejectsNullUsage() {
		assertThatThrownBy(() -> DashScopeSdkUsage.from(null)).isInstanceOf(IllegalArgumentException.class);
	}

}
