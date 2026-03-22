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

package com.alibaba.cloud.ai.dashscope.sdk.chat;

import org.junit.jupiter.api.Test;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.definition.DefaultToolDefinition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.entry;

class DashScopeSdkChatOptionsTests {

	private static final String TEST_MODEL = "qwen-plus";

	private static final Map<String, Object> TEST_EXTRA_BODY = Map.of("customKey", "customValue");

	@Test
	void testBuilderAndGetters() {
		DashScopeSdkChatOptions options = DashScopeSdkChatOptions.builder()
			.model(TEST_MODEL)
			.temperature(0.7)
			.topP(0.8)
			.topK(50)
			.seed(42)
			.repetitionPenalty(1.1)
			.stream(true)
			.enableSearch(true)
			.incrementalOutput(true)
			.maxTokens(1024)
			.extraBody(TEST_EXTRA_BODY)
			.build();

		assertThat(options.getModel()).isEqualTo(TEST_MODEL);
		assertThat(options.getTemperature()).isEqualTo(0.7);
		assertThat(options.getTopP()).isEqualTo(0.8);
		assertThat(options.getTopK()).isEqualTo(50);
		assertThat(options.getSeed()).isEqualTo(42);
		assertThat(options.getRepetitionPenalty()).isEqualTo(1.1);
		assertThat(options.getStream()).isTrue();
		assertThat(options.getEnableSearch()).isTrue();
		assertThat(options.getIncrementalOutput()).isTrue();
		assertThat(options.getMaxTokens()).isEqualTo(1024);
		assertThat(options.getExtraBody()).isEqualTo(TEST_EXTRA_BODY);
	}

	@Test
	void testToolCallbacks() {
		ToolCallback callback1 = new SimpleToolCallback("tool1");
		ToolCallback callback2 = new SimpleToolCallback("tool2");
		List<ToolCallback> callbacks = Arrays.asList(callback1, callback2);
		Set<String> toolNames = new HashSet<>(Arrays.asList("test1", "test2"));

		DashScopeSdkChatOptions options = DashScopeSdkChatOptions.builder()
			.toolCallbacks(callbacks)
			.toolNames(toolNames)
			.build();

		assertThat(options.getToolCallbacks()).containsExactlyElementsOf(callbacks);
		assertThat(options.getToolNames()).containsExactlyInAnyOrderElementsOf(toolNames);
	}

	@Test
	void testCopy() {
		DashScopeSdkChatOptions original = DashScopeSdkChatOptions.builder()
			.model(TEST_MODEL)
			.temperature(0.9)
			.topP(0.7)
			.extraBody(TEST_EXTRA_BODY)
			.build();

		DashScopeSdkChatOptions copy = (DashScopeSdkChatOptions) original.copy();

		assertThat(copy).usingRecursiveComparison().isEqualTo(original);
		assertThat(copy).isNotSameAs(original);
	}

	@Test
	void testStopSequencesMapping() {
		DashScopeSdkChatOptions options = DashScopeSdkChatOptions.builder().stop(List.of("A", "B")).build();

		assertThat(options.getStopSequences()).containsExactly("A", "B");
	}

	@Test
	void testDefaultValues() {
		DashScopeSdkChatOptions options = DashScopeSdkChatOptions.builder().build();

		assertThat(options.getEnableSearch()).isFalse();
		assertThat(options.getIncrementalOutput()).isTrue();
		assertThat(options.getStop()).isNull();
		assertThat(options.getStopSequences()).isNull();
		assertThat(options.getHttpHeaders()).isNotNull().isEmpty();
		assertThat(options.getToolCallbacks()).isNotNull().isEmpty();
		assertThat(options.getToolNames()).isNotNull().isEmpty();
		assertThat(options.getToolContext()).isNotNull().isEmpty();
		assertThat(options.getFrequencyPenalty()).isNull();
		assertThat(options.getPresencePenalty()).isNull();
	}

	@Test
	void testStopSequencesReturnsNullWhenNoStringStops() {
		DashScopeSdkChatOptions options = DashScopeSdkChatOptions.builder().stop(List.of(1, 2)).build();

		assertThat(options.getStopSequences()).isNull();
	}

	@Test
	void testFromOptionsReturnsNullForNullInput() {
		assertThat(DashScopeSdkChatOptions.fromOptions(null)).isNull();
	}

	@Test
	void testFromOptionsCreatesIndependentCollections() {
		List<Object> stop = new ArrayList<>(List.of("A"));
		Map<String, String> headers = new HashMap<>();
		headers.put("x-source", "s1");
		Set<String> toolNames = new HashSet<>(Set.of("tool1"));
		Map<String, Object> toolContext = new HashMap<>();
		toolContext.put("k1", "v1");
		Map<String, Object> extraBody = new HashMap<>();
		extraBody.put("e1", "v1");
		List<ToolCallback> callbacks = new ArrayList<>(List.of(new SimpleToolCallback("toolA")));

		DashScopeSdkChatOptions original = DashScopeSdkChatOptions.builder()
			.stop(stop)
			.httpHeaders(headers)
			.toolNames(toolNames)
			.toolContext(toolContext)
			.toolCallbacks(callbacks)
			.extraBody(extraBody)
			.build();

		DashScopeSdkChatOptions copy = DashScopeSdkChatOptions.fromOptions(original);

		stop.add("B");
		headers.put("x-source-2", "s2");
		toolNames.add("tool2");
		toolContext.put("k2", "v2");
		extraBody.put("e2", "v2");
		callbacks.add(new SimpleToolCallback("toolB"));
		copy.getStop().add("C");
		copy.getHttpHeaders().put("x-copy", "c1");
		copy.getToolNames().add("tool-copy");
		copy.getToolContext().put("k-copy", "v-copy");
		copy.getExtraBody().put("e-copy", "v-copy");
		copy.getToolCallbacks().add(new SimpleToolCallback("toolC"));

		assertThat(original.getStop()).containsExactly("A", "B");
		assertThat(copy.getStop()).containsExactly("A", "C");
		assertThat(original.getHttpHeaders()).containsOnly(entry("x-source", "s1"), entry("x-source-2", "s2"));
		assertThat(copy.getHttpHeaders()).containsOnly(entry("x-source", "s1"), entry("x-copy", "c1"));
		assertThat(original.getToolNames()).containsExactlyInAnyOrder("tool1", "tool2");
		assertThat(copy.getToolNames()).containsExactlyInAnyOrder("tool1", "tool-copy");
		assertThat(original.getToolContext()).containsOnly(entry("k1", "v1"), entry("k2", "v2"));
		assertThat(copy.getToolContext()).containsOnly(entry("k1", "v1"), entry("k-copy", "v-copy"));
		assertThat(original.getExtraBody()).containsOnly(entry("e1", "v1"), entry("e2", "v2"));
		assertThat(copy.getExtraBody()).containsOnly(entry("e1", "v1"), entry("e-copy", "v-copy"));
		assertThat(original.getToolCallbacks()).hasSize(2);
		assertThat(copy.getToolCallbacks()).hasSize(2);
	}

	@Test
	void testToolCallbacksValidation() {
		DashScopeSdkChatOptions options = DashScopeSdkChatOptions.builder().build();

		assertThatThrownBy(() -> options.setToolCallbacks(null)).isInstanceOf(IllegalArgumentException.class);
		assertThatThrownBy(() -> options.setToolCallbacks(Arrays.asList(new SimpleToolCallback("t1"), null)))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void testToolNamesValidation() {
		DashScopeSdkChatOptions options = DashScopeSdkChatOptions.builder().build();

		assertThatThrownBy(() -> options.setToolNames(null)).isInstanceOf(IllegalArgumentException.class);
		assertThatThrownBy(() -> options.setToolNames(new HashSet<>(Arrays.asList("tool1", null))))
			.isInstanceOf(IllegalArgumentException.class);
		assertThatThrownBy(() -> options.setToolNames(Set.of("tool1", ""))).isInstanceOf(IllegalArgumentException.class);
	}

	private static final class SimpleToolCallback implements ToolCallback {

		private final String name;

		private SimpleToolCallback(String name) {
			this.name = name;
		}

		@Override
		public org.springframework.ai.tool.definition.ToolDefinition getToolDefinition() {
			return DefaultToolDefinition.builder()
				.name(this.name)
				.description("test tool")
				.inputSchema("{}")
				.build();
		}

		@Override
		public String call(String toolInput) {
			return "{}";
		}

	}

}
