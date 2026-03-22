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
package com.alibaba.cloud.ai.dashscope.protocol;

import com.alibaba.cloud.ai.dashscope.common.DashScopeAudioApiConstants;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DashScopeWebSocketClientOptionsTests {

	@Test
	void testDefaultValues() {
		DashScopeWebSocketClientOptions options = new DashScopeWebSocketClientOptions();

		assertThat(options.getUrl()).isEqualTo(DashScopeAudioApiConstants.DEFAULT_WEBSOCKET_URL);
		assertThat(options.getApiKey()).isNull();
		assertThat(options.getWorkSpaceId()).isNull();
	}

	@Test
	void testBuilderAndDeprecatedWithMethods() {
		DashScopeWebSocketClientOptions options = DashScopeWebSocketClientOptions.builder()
			.withUrl("wss://example.test/ws")
			.withApiKey("ak-test")
			.withWorkSpaceId("ws-1")
			.build();

		assertThat(options.getUrl()).isEqualTo("wss://example.test/ws");
		assertThat(options.getApiKey()).isEqualTo("ak-test");
		assertThat(options.getWorkSpaceId()).isEqualTo("ws-1");
	}

	@Test
	void testBuilderFromExistingOptions() {
		DashScopeWebSocketClientOptions original = new DashScopeWebSocketClientOptions();
		original.setUrl("wss://origin/ws");
		original.setApiKey("origin-ak");
		original.setWorkSpaceId("origin-ws");

		DashScopeWebSocketClientOptions updated = new DashScopeWebSocketClientOptions.Builder(original)
			.url("wss://updated/ws")
			.apiKey("updated-ak")
			.build();

		assertThat(updated).isSameAs(original);
		assertThat(updated.getUrl()).isEqualTo("wss://updated/ws");
		assertThat(updated.getApiKey()).isEqualTo("updated-ak");
		assertThat(updated.getWorkSpaceId()).isEqualTo("origin-ws");
	}

}
