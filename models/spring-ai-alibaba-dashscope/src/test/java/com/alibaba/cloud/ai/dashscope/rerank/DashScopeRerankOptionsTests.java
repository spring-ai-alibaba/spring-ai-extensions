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
package com.alibaba.cloud.ai.dashscope.rerank;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * Test cases for DashScopeRerankOptions. Tests cover default values, builder pattern, and
 * property modifications.
 *
 * @author yuluo
 * @author <a href="mailto:yuluo08290126@gmail.com">yuluo</a>
 * @author brianxiadong
 * @since 1.0.0-M5.1
 */
class DashScopeRerankOptionsTests {

	// Test constants
	private static final String TEST_MODEL = "test-rerank-model";

	private static final Integer TEST_TOP_N = 5;

	private static final Boolean TEST_RETURN_DOCUMENTS = true;

	/**
	 * Test that an empty builder produces null fields. All fields default to null so that
	 * ModelOptionsUtils.mergeOption() correctly falls through to the configured defaults
	 * rather than having constructor values silently override them.
	 */
	@Test
	void testDefaultValues() {
		DashScopeRerankOptions options = DashScopeRerankOptions.builder().build();

        assertThat(options.getModel()).isEqualTo("gte-rerank");
		assertThat(options.getTopN()).isEqualTo(3);
		assertThat(options.getReturnDocuments()).isEqualTo(false);
	}

	/**
	 * Test builder pattern with all properties set. Verifies that all properties can be
	 * set using the builder pattern and are correctly assigned to the created instance.
	 */
	@Test
	void testBuilderPattern() {
		DashScopeRerankOptions options = DashScopeRerankOptions.builder()
			.model(TEST_MODEL)
			.topN(TEST_TOP_N)
			.returnDocuments(TEST_RETURN_DOCUMENTS)
			.build();

		assertThat(options.getModel()).isEqualTo(TEST_MODEL);
		assertThat(options.getTopN()).isEqualTo(TEST_TOP_N);
		assertThat(options.getReturnDocuments()).isEqualTo(TEST_RETURN_DOCUMENTS);
	}

	/**
	 * Test setters and getters. Verifies that all properties can be modified after
	 * instance creation using setter methods and retrieved using getter methods.
	 */
	@Test
	void testMutate() {
		DashScopeRerankOptions original = DashScopeRerankOptions.builder()
			.model(TEST_MODEL)
			.topN(TEST_TOP_N)
			.returnDocuments(TEST_RETURN_DOCUMENTS)
			.build();

		DashScopeRerankOptions modified = DashScopeRerankOptions.builder()
            .from(original)
            .model("modified-model")
            .build();

		assertThat(modified).isNotSameAs(original);
		assertThat(modified.getModel()).isEqualTo("modified-model");
		assertThat(modified.getTopN()).isEqualTo(TEST_TOP_N);
		assertThat(modified.getReturnDocuments()).isEqualTo(TEST_RETURN_DOCUMENTS);
		assertThat(original.getModel()).isEqualTo(TEST_MODEL);
	}

	@Test
	void testFromOptions() {
		DashScopeRerankOptions original = DashScopeRerankOptions.builder()
			.model(TEST_MODEL)
			.topN(TEST_TOP_N)
			.build();

		DashScopeRerankOptions copy = DashScopeRerankOptions.builder().from(original).build();

		assertThat(copy).isNotSameAs(original);
		assertThat(copy.getModel()).isEqualTo(TEST_MODEL);
		assertThat(copy.getTopN()).isEqualTo(TEST_TOP_N);
	}

	/**
	 * Test builder with partial values set. Unset fields remain null so they do not
	 * accidentally override configured defaults during options merging.
	 */
	@Test
	void testBuilderWithPartialValues() {
		DashScopeRerankOptions options = DashScopeRerankOptions.builder()
			.model(TEST_MODEL)
			.topN(TEST_TOP_N)
			.build();

		assertThat(options.getModel()).isEqualTo(TEST_MODEL);
		assertThat(options.getTopN()).isEqualTo(TEST_TOP_N);
        assertThat(options.getReturnDocuments()).isEqualTo(false);
	}

	@Test
	void testDeprecatedWithMethods() {
		DashScopeRerankOptions options = DashScopeRerankOptions.builder()
			.model(TEST_MODEL)
			.topN(TEST_TOP_N)
			.returnDocuments(TEST_RETURN_DOCUMENTS)
			.build();

		assertThat(options.getModel()).isEqualTo(TEST_MODEL);
		assertThat(options.getTopN()).isEqualTo(TEST_TOP_N);
		assertThat(options.getReturnDocuments()).isEqualTo(TEST_RETURN_DOCUMENTS);
	}

}
