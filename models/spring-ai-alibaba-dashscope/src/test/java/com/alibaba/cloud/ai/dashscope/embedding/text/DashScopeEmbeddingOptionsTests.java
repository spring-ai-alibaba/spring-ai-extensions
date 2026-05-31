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
package com.alibaba.cloud.ai.dashscope.embedding.text;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import org.junit.jupiter.api.Test;
import org.springframework.ai.embedding.EmbeddingOptions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test cases for DashScopeEmbeddingOptions. Tests cover builder pattern, getters,
 * fromOptions, validation, and JSON properties.
 *
 * @author yuluo
 * @author <a href="mailto:yuluo08290126@gmail.com">yuluo</a>
 * @author brianxiadong
 * @since 1.0.0-M5.1
 */
class DashScopeEmbeddingOptionsTests {

	private static final String TEST_MODEL = "text-embedding-v2";

	private static final String TEST_TEXT_TYPE = "document";

	private static final Integer TEST_DIMENSIONS = 1536;

	@Test
	void testBuilderAndGetters() {
		DashScopeEmbeddingOptions options = DashScopeEmbeddingOptions.builder()
			.model(TEST_MODEL)
			.textType(TEST_TEXT_TYPE)
			.dimensions(TEST_DIMENSIONS)
			.build();

		assertThat(options.getModel()).isEqualTo(TEST_MODEL);
		assertThat(options.getTextType()).isEqualTo(TEST_TEXT_TYPE);
		assertThat(options.getDimensions()).isEqualTo(TEST_DIMENSIONS);
	}

	@Test
	void testDefaultValues() {
		DashScopeEmbeddingOptions options = DashScopeEmbeddingOptions.builder().build();

		assertThat(options.getModel()).isNull();
		assertThat(options.getTextType()).isNull();
		assertThat(options.getDimensions()).isNull();
		assertThat(options.getOutputType()).isNull();
		assertThat(options.getEmbeddingsPath()).isNull();
	}

	@Test
	void testBuilderWithDefaultModel() {
		DashScopeEmbeddingOptions options = DashScopeEmbeddingOptions.builder()
			.model(DashScopeApi.DEFAULT_EMBEDDING_MODEL)
			.build();

		assertThat(options.getModel()).isEqualTo(DashScopeApi.DEFAULT_EMBEDDING_MODEL);
	}

	@Test
	void testBuilderWithDefaultTextType() {
		DashScopeEmbeddingOptions options = DashScopeEmbeddingOptions.builder()
			.textType(DashScopeApi.DEFAULT_EMBEDDING_TEXT_TYPE)
			.build();

		assertThat(options.getTextType()).isEqualTo(DashScopeApi.DEFAULT_EMBEDDING_TEXT_TYPE);
	}

	@Test
	void testImplementsEmbeddingOptions() {
		DashScopeEmbeddingOptions options = DashScopeEmbeddingOptions.builder().build();

		assertThat(options).isInstanceOf(EmbeddingOptions.class);
	}

	@Test
	void testEmbeddingsPath() {
		DashScopeEmbeddingOptions options = DashScopeEmbeddingOptions.builder()
			.model(TEST_MODEL)
			.textType(TEST_TEXT_TYPE)
			.dimensions(TEST_DIMENSIONS)
			.embeddingsPath("/tmp/embeddings")
			.build();

		assertThat(options.getModel()).isEqualTo(TEST_MODEL);
		assertThat(options.getTextType()).isEqualTo(TEST_TEXT_TYPE);
		assertThat(options.getDimensions()).isEqualTo(TEST_DIMENSIONS);
		assertThat(options.getEmbeddingsPath()).isEqualTo("/tmp/embeddings");
	}

	@Test
	void testOutputTypeValidationAcceptsValidValues() {
		DashScopeEmbeddingOptions dense = DashScopeEmbeddingOptions.builder()
			.outputType(DashScopeEmbeddingOptions.OUTPUT_TYPE_DENSE)
			.build();
		assertThat(dense.getOutputType()).isEqualTo("dense");

		DashScopeEmbeddingOptions sparse = DashScopeEmbeddingOptions.builder()
			.outputType(DashScopeEmbeddingOptions.OUTPUT_TYPE_SPARSE)
			.build();
		assertThat(sparse.getOutputType()).isEqualTo("sparse");

		DashScopeEmbeddingOptions both = DashScopeEmbeddingOptions.builder()
			.outputType(DashScopeEmbeddingOptions.OUTPUT_TYPE_DENSE_AND_SPARSE)
			.build();
		assertThat(both.getOutputType()).isEqualTo("dense&sparse");
	}

	@Test
	void testOutputTypeValidationRejectsInvalidValue() {
		assertThatThrownBy(() -> DashScopeEmbeddingOptions.builder().outputType("invalid"))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void testFromOptions() {
		DashScopeEmbeddingOptions original = DashScopeEmbeddingOptions.builder()
			.model(TEST_MODEL)
			.textType(TEST_TEXT_TYPE)
			.dimensions(TEST_DIMENSIONS)
			.outputType(DashScopeEmbeddingOptions.OUTPUT_TYPE_DENSE)
			.embeddingsPath("/tmp/embeddings")
			.build();

		DashScopeEmbeddingOptions copy = DashScopeEmbeddingOptions.fromOptions(original);

		assertThat(copy).usingRecursiveComparison().isEqualTo(original);
		assertThat(copy).isNotSameAs(original);
	}

	@Test
	void testEqualsAndHashCode() {
		DashScopeEmbeddingOptions options1 = DashScopeEmbeddingOptions.builder()
			.model(TEST_MODEL)
			.textType(TEST_TEXT_TYPE)
			.build();
		DashScopeEmbeddingOptions options2 = DashScopeEmbeddingOptions.builder()
			.model(TEST_MODEL)
			.textType(TEST_TEXT_TYPE)
			.build();

		assertThat(options1).isEqualTo(options2);
		assertThat(options1.hashCode()).isEqualTo(options2.hashCode());
	}

}
