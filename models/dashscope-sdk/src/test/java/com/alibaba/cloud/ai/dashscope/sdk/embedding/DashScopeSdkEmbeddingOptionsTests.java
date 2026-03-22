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

package com.alibaba.cloud.ai.dashscope.sdk.embedding;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

class DashScopeSdkEmbeddingOptionsTests {

	@Test
	void testBuilderAndCopy() {
		DashScopeSdkEmbeddingOptions options = DashScopeSdkEmbeddingOptions.builder()
			.model("text-embedding-v2")
			.textType("query")
			.dimensions(1024)
			.httpHeaders(Map.of("x-test", "v"))
			.build();

		DashScopeSdkEmbeddingOptions copy = DashScopeSdkEmbeddingOptions.fromOptions(options);

		assertThat(copy).usingRecursiveComparison().isEqualTo(options);
		assertThat(copy).isNotSameAs(options);
	}

	@Test
	void testDefaultValues() {
		DashScopeSdkEmbeddingOptions options = DashScopeSdkEmbeddingOptions.builder().build();

		assertThat(options.getModel()).isNull();
		assertThat(options.getTextType()).isNull();
		assertThat(options.getDimensions()).isNull();
		assertThat(options.getHttpHeaders()).isNotNull().isEmpty();
	}

	@Test
	void testFromOptionsReturnsNullForNullInput() {
		assertThat(DashScopeSdkEmbeddingOptions.fromOptions(null)).isNull();
	}

	@Test
	void testFromOptionsCreatesIndependentHttpHeaders() {
		Map<String, String> headers = new HashMap<>();
		headers.put("x-source", "s1");
		DashScopeSdkEmbeddingOptions original = DashScopeSdkEmbeddingOptions.builder().httpHeaders(headers).build();

		DashScopeSdkEmbeddingOptions copy = DashScopeSdkEmbeddingOptions.fromOptions(original);
		headers.put("x-source-2", "s2");
		copy.getHttpHeaders().put("x-copy", "c1");

		assertThat(original.getHttpHeaders()).containsOnly(entry("x-source", "s1"), entry("x-source-2", "s2"));
		assertThat(copy.getHttpHeaders()).containsOnly(entry("x-source", "s1"), entry("x-copy", "c1"));
	}

	@Test
	void testFromOptionsHandlesNullHttpHeaders() {
		DashScopeSdkEmbeddingOptions original = new DashScopeSdkEmbeddingOptions();
		original.setHttpHeaders(null);

		DashScopeSdkEmbeddingOptions copy = DashScopeSdkEmbeddingOptions.fromOptions(original);
		assertThat(copy.getHttpHeaders()).isNotNull().isEmpty();
	}

}
