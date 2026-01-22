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

import com.alibaba.cloud.ai.dashscope.spec.DashScopeApiSpec.CacheCreation;
import com.alibaba.cloud.ai.dashscope.spec.DashScopeApiSpec.PromptTokenDetailed;
import com.alibaba.cloud.ai.dashscope.spec.DashScopeApiSpec.TokenUsage;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test cases for DashScopeAiUsage. Tests cover factory method, token calculations,
 * native usage and null handling.
 *
 * @author yuluo
 * @author <a href="mailto:yuluo08290126@gmail.com">yuluo</a>
 * @author brianxiadong
 * @since 1.0.0-M5.1
 */
class DashScopeAiUsageTests {

	@Test
	void testFromTokenUsage() {
		// Test factory method with valid TokenUsage
		TokenUsage tokenUsage = new TokenUsage(10, 10, 15, null, null, null, null, null, null, null);
		DashScopeAiUsage usage = DashScopeAiUsage.from(tokenUsage);

		// Verify token counts are converted correctly
		assertThat(usage.getPromptTokens()).isEqualTo(10L);
		assertThat(usage.getCompletionTokens()).isEqualTo(10);
		assertThat(usage.getTotalTokens()).isEqualTo(15L);
	}

	@Test
	void testFromNullTokenUsage() {
		// Test factory method with null TokenUsage
		assertThatThrownBy(() -> DashScopeAiUsage.from(null)).isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("DashScope Usage must not be null");
	}

	@Test
	void testCalculatedTotalTokens() {
		// Test total tokens calculation when totalTokens is null
		TokenUsage tokenUsage = new TokenUsage(10, 15, 15, null, null, null, null, null, null, null);
		DashScopeAiUsage usage = DashScopeAiUsage.from(tokenUsage);

		// Verify total tokens is calculated from prompt and generation tokens
		assertThat(usage.getTotalTokens()).isEqualTo(15L);
	}

	@Test
	void testZeroTokens() {
		// Test with all token counts set to zero
		TokenUsage tokenUsage = new TokenUsage(0, 0, 0, null, null, null, null, null, null, null);
		DashScopeAiUsage usage = DashScopeAiUsage.from(tokenUsage);

		// Verify all token counts are zero
		assertThat(usage.getPromptTokens()).isZero();
		assertThat(usage.getCompletionTokens()).isZero();
		assertThat(usage.getTotalTokens()).isZero();
	}

	@Test
	void testToString() {
		// Test toString method
		TokenUsage tokenUsage = new TokenUsage(10, 5, 15, null, null, null, null, null, null, null);
		DashScopeAiUsage usage = DashScopeAiUsage.from(tokenUsage);

		// Verify toString contains token usage information
		String toString = usage.toString();
		assertThat(toString).isEqualTo(tokenUsage.toString());
	}

	@Test
	void testGetNativeUsageReturnsTokenUsage() {
		// Test that getNativeUsage returns the original TokenUsage object
		TokenUsage tokenUsage = new TokenUsage(10, 5, 15, null, null, null, null, null, null, null);
		DashScopeAiUsage usage = DashScopeAiUsage.from(tokenUsage);

		// Verify getNativeUsage returns the same TokenUsage instance
		assertThat(usage.getNativeUsage()).isSameAs(tokenUsage);
	}

	@Test
	void testGetNativeUsageWithCacheDetails() {
		// Test that getNativeUsage returns TokenUsage with cache details accessible
		CacheCreation cacheCreation = new CacheCreation(1024);
		PromptTokenDetailed promptTokenDetailed = new PromptTokenDetailed(
				128,           // cachedTokens
				cacheCreation, // cacheCreation
				1024,          // cacheCreationInputTokens
				"ephemeral_5m" // cacheType
		);

		TokenUsage tokenUsage = new TokenUsage(
				10,    // outputTokens
				20,    // inputTokens
				30,    // totalTokens
				null,  // imageTokens
				null,  // videoTokens
				null,  // audioTokens
				null,  // seconds
				null,  // inputTokensDetails
				null,  // outputTokensDetails
				promptTokenDetailed
		);

		DashScopeAiUsage usage = DashScopeAiUsage.from(tokenUsage);

		// Verify getNativeUsage returns TokenUsage with cache fields
		assertThat(usage.getNativeUsage()).isSameAs(tokenUsage);

		// Verify cache details are accessible via native usage
		TokenUsage nativeUsage = (TokenUsage) usage.getNativeUsage();
		assertThat(nativeUsage.promptTokenDetailed()).isNotNull();
		assertThat(nativeUsage.promptTokenDetailed().cachedTokens()).isEqualTo(128);
		assertThat(nativeUsage.promptTokenDetailed().cacheType()).isEqualTo("ephemeral_5m");
		assertThat(nativeUsage.promptTokenDetailed().cacheCreationInputTokens()).isEqualTo(1024);
		assertThat(nativeUsage.promptTokenDetailed().cacheCreation().ephemeral_5m_input_tokens()).isEqualTo(1024);
	}

	@Test
	void testGetNativeUsageWithMediaTokens() {
		// Test that getNativeUsage returns TokenUsage with media tokens accessible
		TokenUsage tokenUsage = new TokenUsage(
				10,   // outputTokens
				20,   // inputTokens
				30,   // totalTokens
				100,  // imageTokens
				200,  // videoTokens
				50,   // audioTokens
				null, // seconds
				null, // inputTokensDetails
				null, // outputTokensDetails
				null  // promptTokenDetailed
		);

		DashScopeAiUsage usage = DashScopeAiUsage.from(tokenUsage);

		// Verify media tokens are accessible via native usage
		TokenUsage nativeUsage = (TokenUsage) usage.getNativeUsage();
		assertThat(nativeUsage.imageTokens()).isEqualTo(100);
		assertThat(nativeUsage.videoTokens()).isEqualTo(200);
		assertThat(nativeUsage.audioTokens()).isEqualTo(50);
	}

}
