/*
 * Copyright 2026-2027 the original author or authors.
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

package com.alibaba.cloud.ai.dashscope.embedding.multimodal;

import org.junit.jupiter.api.Test;
import org.springframework.ai.embedding.EmbeddingOptions;

import static org.assertj.core.api.Assertions.assertThat;

class DashScopeMultimodalEmbeddingOptionsTests {

	@Test
	void testBuilderAndGetters() {
		DashScopeMultimodalEmbeddingOptions options = DashScopeMultimodalEmbeddingOptions.builder()
			.model("multimodal-embedding-v1")
			.dimensions(1024)
			.outputType("dense")
			.fps(0.5f)
			.instruct("Describe semantic meaning.")
			.build();

		assertThat(options.getModel()).isEqualTo("multimodal-embedding-v1");
		assertThat(options.getDimensions()).isEqualTo(1024);
		assertThat(options.getOutputType()).isEqualTo("dense");
		assertThat(options.getFps()).isEqualTo(0.5f);
		assertThat(options.getInstruct()).isEqualTo("Describe semantic meaning.");
	}

	@Test
	void testDefaultValues() {
		DashScopeMultimodalEmbeddingOptions options = DashScopeMultimodalEmbeddingOptions.builder().build();
		assertThat(options.getModel()).isNull();
		assertThat(options.getDimensions()).isNull();
		assertThat(options.getOutputType()).isNull();
		assertThat(options.getFps()).isNull();
		assertThat(options.getInstruct()).isNull();
		assertThat(options).isInstanceOf(EmbeddingOptions.class);
	}

	@Test
	void testFromBuilder() {
		DashScopeMultimodalEmbeddingOptions original = DashScopeMultimodalEmbeddingOptions.builder()
			.model("m1")
			.dimensions(512)
			.outputType("dense")
			.fps(1.0f)
			.instruct("inst")
			.build();

		DashScopeMultimodalEmbeddingOptions copy = DashScopeMultimodalEmbeddingOptions.builder()
			.from(original)
			.build();

		assertThat(copy).usingRecursiveComparison().isEqualTo(original);
		assertThat(copy).isNotSameAs(original);
	}

	@Test
	void testMergeWithEmbeddingOptions() {
		DashScopeMultimodalEmbeddingOptions defaultOpts = DashScopeMultimodalEmbeddingOptions.builder()
			.model("default-model")
			.dimensions(1024)
			.outputType("dense")
			.fps(1.0f)
			.instruct("default-instruct")
			.build();

		DashScopeMultimodalEmbeddingOptions runtimeOpts = DashScopeMultimodalEmbeddingOptions.builder()
			.model("runtime-model")
			.outputType("sparse")
			.build();

		DashScopeMultimodalEmbeddingOptions merged = DashScopeMultimodalEmbeddingOptions.builder()
			.from(defaultOpts)
			.merge(runtimeOpts)
			.build();

		assertThat(merged.getModel()).isEqualTo("runtime-model");
		assertThat(merged.getDimensions()).isEqualTo(1024);
		assertThat(merged.getOutputType()).isEqualTo("sparse");
		assertThat(merged.getFps()).isEqualTo(1.0f);
		assertThat(merged.getInstruct()).isEqualTo("default-instruct");
	}

	@Test
	void testMergeWithNull() {
		DashScopeMultimodalEmbeddingOptions defaultOpts = DashScopeMultimodalEmbeddingOptions.builder()
			.model("default-model")
			.dimensions(1024)
			.build();

		DashScopeMultimodalEmbeddingOptions merged = DashScopeMultimodalEmbeddingOptions.builder()
			.from(defaultOpts)
			.merge(null)
			.build();

		assertThat(merged.getModel()).isEqualTo("default-model");
		assertThat(merged.getDimensions()).isEqualTo(1024);
	}

	@Test
	void testEqualsAndHashCode() {
		DashScopeMultimodalEmbeddingOptions options1 = DashScopeMultimodalEmbeddingOptions.builder()
			.model("m1")
			.dimensions(512)
			.build();
		DashScopeMultimodalEmbeddingOptions options2 = DashScopeMultimodalEmbeddingOptions.builder()
			.model("m1")
			.dimensions(512)
			.build();

		assertThat(options1).isEqualTo(options2);
		assertThat(options1.hashCode()).isEqualTo(options2.hashCode());
	}

}
