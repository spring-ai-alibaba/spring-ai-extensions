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

import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.definition.DefaultToolDefinition;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
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

		DashScopeSdkChatOptions options = DashScopeSdkChatOptions.builder()
			.toolCallbacks(callbacks)
			.build();

		assertThat(options.getToolCallbacks()).containsExactlyElementsOf(callbacks);
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
	void testGenericStopSequencesMapToSdkStop() {
		DashScopeSdkChatOptions options = DashScopeSdkChatOptions.builder()
			.combineWith(ChatOptions.builder().stopSequences(List.of("END", "STOP")))
			.build();

		assertThat(options.getStop()).asInstanceOf(InstanceOfAssertFactories.LIST).containsExactly("END", "STOP");
		assertThat(options.getStopSequences()).containsExactly("END", "STOP");
	}

	@Test
	void testDefaultValues() {
		DashScopeSdkChatOptions options = DashScopeSdkChatOptions.builder().build();

		assertThat(options.getEnableSearch()).isNull();
		assertThat(options.getIncrementalOutput()).isNull();
		assertThat(options.getStop()).isNull();
		assertThat(options.getStopSequences()).isNull();
		assertThat(options.getHttpHeaders()).isNull();
		assertThat(options.getToolCallbacks()).isNull();
		assertThat(options.getToolContext()).isNull();
		assertThat(options.getFrequencyPenalty()).isNull();
		assertThat(options.getPresencePenalty()).isNull();
	}

	@Test
	void testStopSequencesReturnsNullWhenNoStringStops() {
		DashScopeSdkChatOptions options = DashScopeSdkChatOptions.builder().stop(List.of(1, 2)).build();

		assertThat(options.getStopSequences()).isNull();
	}

	@Test
	void testMutate() {
		DashScopeSdkChatOptions original = DashScopeSdkChatOptions.builder()
			.model(TEST_MODEL)
			.temperature(0.9)
			.topP(0.7)
			.topK(50)
			.seed(42)
			.repetitionPenalty(1.1)
			.stream(true)
			.enableSearch(true)
			.incrementalOutput(true)
			.maxTokens(1024)
			.stop(List.of("A"))
			.httpHeaders(Map.of("x-source", "s1"))
			.toolContext(Map.of("k1", "v1"))
			.extraBody(TEST_EXTRA_BODY)
			.build();

		DashScopeSdkChatOptions target = original.mutate().build();

		assertThat(target.getModel()).isEqualTo(original.getModel());
		assertThat(target.getTemperature()).isEqualTo(original.getTemperature());
		assertThat(target.getTopP()).isEqualTo(original.getTopP());
		assertThat(target.getTopK()).isEqualTo(original.getTopK());
		assertThat(target.getSeed()).isEqualTo(original.getSeed());
		assertThat(target.getRepetitionPenalty()).isEqualTo(original.getRepetitionPenalty());
		assertThat(target.getStream()).isEqualTo(original.getStream());
		assertThat(target.getEnableSearch()).isEqualTo(original.getEnableSearch());
		assertThat(target.getIncrementalOutput()).isEqualTo(original.getIncrementalOutput());
		assertThat(target.getMaxTokens()).isEqualTo(original.getMaxTokens());
		assertThat(target.getStop()).asInstanceOf(InstanceOfAssertFactories.LIST).containsExactly("A");
		assertThat(target.getHttpHeaders()).containsOnly(entry("x-source", "s1"));
		assertThat(target.getToolContext()).containsOnly(entry("k1", "v1"));
		assertThat(target.getExtraBody()).isEqualTo(TEST_EXTRA_BODY);
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
