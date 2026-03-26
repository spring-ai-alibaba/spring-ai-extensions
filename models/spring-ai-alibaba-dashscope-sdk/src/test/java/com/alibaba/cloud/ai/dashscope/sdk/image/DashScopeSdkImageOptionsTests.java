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

package com.alibaba.cloud.ai.dashscope.sdk.image;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

class DashScopeSdkImageOptionsTests {

	@Test
	void testBuilderAndCopy() {
		DashScopeSdkImageOptions options = DashScopeSdkImageOptions.builder()
			.model("wanx-v1")
			.n(1)
			.width(1024)
			.height(1024)
			.size("1024*1024")
			.style("photography")
			.responseFormat("url")
			.seed(1)
			.negativePrompt("blur")
			.refImage("https://example.com/ref.png")
			.pollIntervalMs(2000)
			.async(false)
			.httpHeaders(Map.of("x-test", "v"))
			.extraBody(Map.of("watermark", false))
			.build();

		DashScopeSdkImageOptions copy = DashScopeSdkImageOptions.fromOptions(options);

		assertThat(copy).usingRecursiveComparison().isEqualTo(options);
		assertThat(copy).isNotSameAs(options);
		assertThat(copy.getSize()).isEqualTo("1024*1024");
	}

	@Test
	void testDefaultValues() {
		DashScopeSdkImageOptions options = DashScopeSdkImageOptions.builder().build();

		assertThat(options.getPollIntervalMs()).isEqualTo(1000);
		assertThat(options.getAsync()).isTrue();
		assertThat(options.getHttpHeaders()).isNotNull().isEmpty();
		assertThat(options.getExtraBody()).isNull();
		assertThat(options.getSize()).isNull();
	}

	@Test
	void testGetSizePrefersExplicitSize() {
		DashScopeSdkImageOptions options = DashScopeSdkImageOptions.builder()
			.width(1024)
			.height(1024)
			.size("768*768")
			.build();

		assertThat(options.getSize()).isEqualTo("768*768");
	}

	@Test
	void testGetSizeFromDimensionsWhenSizeMissing() {
		DashScopeSdkImageOptions options = DashScopeSdkImageOptions.builder().width(1024).height(768).build();

		assertThat(options.getSize()).isEqualTo("1024*768");
	}

	@Test
	void testGetSizeIsNullWhenOnlyOneDimensionProvided() {
		DashScopeSdkImageOptions options = DashScopeSdkImageOptions.builder().width(1024).build();

		assertThat(options.getSize()).isNull();
	}

	@Test
	void testFromOptionsReturnsNullForNullInput() {
		assertThat(DashScopeSdkImageOptions.fromOptions(null)).isNull();
	}

	@Test
	void testFromOptionsCreatesIndependentMaps() {
		Map<String, String> headers = new HashMap<>();
		headers.put("x-test", "v1");
		Map<String, Object> extraBody = new HashMap<>();
		extraBody.put("watermark", false);

		DashScopeSdkImageOptions original = DashScopeSdkImageOptions.builder()
			.httpHeaders(headers)
			.extraBody(extraBody)
			.build();
		DashScopeSdkImageOptions copy = DashScopeSdkImageOptions.fromOptions(original);

		headers.put("x-test-2", "v2");
		extraBody.put("seed", 42);
		copy.getHttpHeaders().put("x-copy", "c1");
		copy.getExtraBody().put("style", "anime");

		assertThat(copy.getHttpHeaders()).containsOnly(entry("x-test", "v1"), entry("x-copy", "c1"));
		assertThat(original.getHttpHeaders()).containsOnly(entry("x-test", "v1"), entry("x-test-2", "v2"));
		assertThat(copy.getExtraBody()).containsOnly(entry("watermark", false), entry("style", "anime"));
		assertThat(original.getExtraBody()).containsOnly(entry("watermark", false), entry("seed", 42));
	}

	@Test
	void testFromOptionsHandlesNullMaps() {
		DashScopeSdkImageOptions original = new DashScopeSdkImageOptions();
		original.setHttpHeaders(null);
		original.setExtraBody(null);

		DashScopeSdkImageOptions copy = DashScopeSdkImageOptions.fromOptions(original);

		assertThat(copy.getHttpHeaders()).isNotNull().isEmpty();
		assertThat(copy.getExtraBody()).isNull();
	}

}
