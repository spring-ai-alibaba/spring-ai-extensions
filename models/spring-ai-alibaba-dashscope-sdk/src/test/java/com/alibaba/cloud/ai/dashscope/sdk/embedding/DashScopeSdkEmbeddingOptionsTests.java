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
	void testFromOptions() {
		DashScopeSdkEmbeddingOptions original = DashScopeSdkEmbeddingOptions.builder()
			.model("text-embedding-v2")
			.textType("query")
			.dimensions(1024)
			.httpHeaders(Map.of("x-source", "s1"))
			.build();

		DashScopeSdkEmbeddingOptions target = DashScopeSdkEmbeddingOptions.fromOptions(original);

		assertThat(target.getModel()).isEqualTo(original.getModel());
		assertThat(target.getTextType()).isEqualTo(original.getTextType());
		assertThat(target.getDimensions()).isEqualTo(original.getDimensions());
		assertThat(target.getHttpHeaders()).containsOnly(entry("x-source", "s1"));
	}

	@Test
	void testFromOptionsCreatesIndependentHttpHeaders() {
		DashScopeSdkEmbeddingOptions original = DashScopeSdkEmbeddingOptions.builder()
			.httpHeaders(Map.of("x-source", "s1"))
			.build();

		DashScopeSdkEmbeddingOptions copy = DashScopeSdkEmbeddingOptions.fromOptions(original);

		original.getHttpHeaders().put("x-source-2", "s2");
		copy.getHttpHeaders().put("x-copy", "c1");

		assertThat(original.getHttpHeaders()).containsOnly(entry("x-source", "s1"), entry("x-source-2", "s2"));
		assertThat(copy.getHttpHeaders()).containsOnly(entry("x-source", "s1"), entry("x-copy", "c1"));
	}

	@Test
	void testFromOptionsHandlesEmptyHttpHeaders() {
		DashScopeSdkEmbeddingOptions original = DashScopeSdkEmbeddingOptions.builder().build();

		DashScopeSdkEmbeddingOptions copy = DashScopeSdkEmbeddingOptions.fromOptions(original);
		assertThat(copy.getHttpHeaders()).isNotNull().isEmpty();
	}


	@Test
	void testEqualsAndHashCode() {
		DashScopeSdkEmbeddingOptions options1 = DashScopeSdkEmbeddingOptions.builder()
			.model("text-embedding-v2")
			.textType("query")
			.build();
		DashScopeSdkEmbeddingOptions options2 = DashScopeSdkEmbeddingOptions.builder()
			.model("text-embedding-v2")
			.textType("query")
			.build();

		assertThat(options1).isEqualTo(options2);
		assertThat(options1.hashCode()).isEqualTo(options2.hashCode());
	}

}
