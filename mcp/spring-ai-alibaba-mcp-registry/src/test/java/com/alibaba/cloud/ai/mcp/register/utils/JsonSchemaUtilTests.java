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

package com.alibaba.cloud.ai.mcp.register.utils;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JsonSchemaUtilTests {

	@Test
	void shouldReturnFalseWhenArrayPrimitiveItemTypesDiffer() {
		String origin = "{\"type\":\"array\",\"items\":{\"type\":\"string\"}}";
		String target = "{\"type\":\"array\",\"items\":{\"type\":\"integer\"}}";

		assertThat(JsonSchemaUtil.compare(origin, target)).isFalse();
	}

	@Test
	void shouldReturnFalseWhenArrayItemsExistOnlyOnOneSide() {
		String origin = "{\"type\":\"array\"}";
		String target = "{\"type\":\"array\",\"items\":{\"type\":\"string\"}}";

		assertThat(JsonSchemaUtil.compare(origin, target)).isFalse();
	}

	@Test
	void shouldReturnFalseWhenTopLevelPrimitiveTypesDiffer() {
		String origin = "{\"type\":\"string\"}";
		String target = "{\"type\":\"integer\"}";

		assertThat(JsonSchemaUtil.compare(origin, target)).isFalse();
	}

	@Test
	void shouldReturnTrueWhenArraySchemasAreEquivalent() {
		String origin = "{\"type\":\"array\",\"items\":{\"type\":\"object\",\"properties\":{\"name\":{\"type\":\"string\"}},\"required\":[\"name\"]}}";
		String target = "{\"type\":\"array\",\"items\":{\"type\":\"object\",\"properties\":{\"name\":{\"type\":\"string\"}},\"required\":[\"name\"]}}";

		assertThat(JsonSchemaUtil.compare(origin, target)).isTrue();
	}

}
