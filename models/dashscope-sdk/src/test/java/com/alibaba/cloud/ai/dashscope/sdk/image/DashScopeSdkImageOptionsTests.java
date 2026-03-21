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

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class DashScopeSdkImageOptionsTests {

	@Test
	void testBuilderAndCopy() {
		DashScopeSdkImageOptions options = DashScopeSdkImageOptions.builder()
			.model("wanx-v1")
			.n(1)
			.width(1024)
			.height(1024)
			.style("photography")
			.responseFormat("url")
			.seed(1)
			.httpHeaders(Map.of("x-test", "v"))
			.extraBody(Map.of("watermark", false))
			.build();

		DashScopeSdkImageOptions copy = DashScopeSdkImageOptions.fromOptions(options);

		assertThat(copy).usingRecursiveComparison().isEqualTo(options);
		assertThat(copy).isNotSameAs(options);
		assertThat(copy.getSize()).isEqualTo("1024*1024");
	}

}
