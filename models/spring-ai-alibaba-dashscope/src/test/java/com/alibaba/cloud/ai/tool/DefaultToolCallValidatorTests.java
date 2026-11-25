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
package com.alibaba.cloud.ai.tool;

import com.alibaba.cloud.ai.dashscope.spec.DashScopeApiSpec.ChatCompletionFinishReason;
import com.alibaba.cloud.ai.dashscope.spec.DashScopeApiSpec.ChatCompletionMessage.ChatCompletionFunction;
import com.alibaba.cloud.ai.dashscope.spec.DashScopeApiSpec.ChatCompletionMessage.ToolCall;
import com.alibaba.cloud.ai.tool.validator.DefaultToolCallValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link DefaultToolCallValidator}.
 *
 * Covers basic validation, LENGTH truncation handling, JSON edge cases,
 * and multiple tool call scenarios.
 *
 * @author gccszs
 * @since 2026/01/13
 */
class DefaultToolCallValidatorTests {

	private DefaultToolCallValidator validator;

	@BeforeEach
	void setUp() {
		validator = new DefaultToolCallValidator();
	}

	// Helper method to create a ToolCall
	private ToolCall createToolCall(String id, String name, String arguments) {
		ChatCompletionFunction function = new ChatCompletionFunction(name, arguments);
		return new ToolCall(id, "function", function, 0);
	}

	// Helper method to create a ToolCall with null function
	private ToolCall createToolCallWithNullFunction(String id) {
		return new ToolCall(id, "function", null, 0);
	}

	// Helper method to create a ToolCall with null function name
	private ToolCall createToolCallWithNullFunctionName(String id, String arguments) {
		ChatCompletionFunction function = new ChatCompletionFunction(null, arguments);
		return new ToolCall(id, "function", function, 0);
	}

	@Nested
	@DisplayName("Basic Validation Tests")
	class BasicValidationTests {

		@Test
		@DisplayName("Should return empty list when toolCalls is null")
		void shouldReturnEmptyListWhenToolCallsIsNull() {
			List<ToolCall> result = validator.validate(null, ChatCompletionFinishReason.STOP);
			assertThat(result).isEmpty();
		}

		@Test
		@DisplayName("Should return empty list when toolCalls is empty")
		void shouldReturnEmptyListWhenToolCallsIsEmpty() {
			List<ToolCall> result = validator.validate(List.of(), ChatCompletionFinishReason.STOP);
			assertThat(result).isEmpty();
		}

		@Test
		@DisplayName("Should filter out toolCall with null function")
		void shouldFilterOutToolCallWithNullFunction() {
			ToolCall nullFunctionToolCall = createToolCallWithNullFunction("tc-1");
			ToolCall validToolCall = createToolCall("tc-2", "validTool", "{}");

			List<ToolCall> result = validator.validate(
					List.of(nullFunctionToolCall, validToolCall),
					ChatCompletionFinishReason.STOP);

			assertThat(result).hasSize(1);
			assertThat(result.get(0).id()).isEqualTo("tc-2");
		}

		@Test
		@DisplayName("Should filter out toolCall with null function name")
		void shouldFilterOutToolCallWithNullFunctionName() {
			ToolCall nullNameToolCall = createToolCallWithNullFunctionName("tc-1", "{}");
			ToolCall validToolCall = createToolCall("tc-2", "validTool", "{}");

			List<ToolCall> result = validator.validate(
					List.of(nullNameToolCall, validToolCall),
					ChatCompletionFinishReason.STOP);

			assertThat(result).hasSize(1);
			assertThat(result.get(0).id()).isEqualTo("tc-2");
		}

		@Test
		@DisplayName("Should pass valid toolCall with normal finishReason")
		void shouldPassValidToolCallWithNormalFinishReason() {
			ToolCall validToolCall = createToolCall("tc-1", "myTool", "{\"key\": \"value\"}");

			List<ToolCall> result = validator.validate(
					List.of(validToolCall),
					ChatCompletionFinishReason.STOP);

			assertThat(result).hasSize(1);
			assertThat(result.get(0).function().name()).isEqualTo("myTool");
		}

		@Test
		@DisplayName("Should pass toolCall with TOOL_CALLS finishReason")
		void shouldPassToolCallWithToolCallsFinishReason() {
			ToolCall validToolCall = createToolCall("tc-1", "myTool", "{\"param\": 123}");

			List<ToolCall> result = validator.validate(
					List.of(validToolCall),
					ChatCompletionFinishReason.TOOL_CALLS);

			assertThat(result).hasSize(1);
		}
	}

	@Nested
	@DisplayName("LENGTH Truncation Tests")
	class LengthTruncationTests {

		@Test
		@DisplayName("Should filter out toolCall with incomplete JSON when finishReason is LENGTH")
		void shouldFilterOutToolCallWithIncompleteJsonWhenLengthTruncated() {
			// Simulate truncated JSON - missing closing brace
			ToolCall truncatedToolCall = createToolCall("tc-1", "truncatedTool", "{\"key\": \"val");

			List<ToolCall> result = validator.validate(
					List.of(truncatedToolCall),
					ChatCompletionFinishReason.LENGTH);

			assertThat(result).isEmpty();
		}

		@Test
		@DisplayName("Should pass toolCall with valid JSON even when finishReason is LENGTH")
		void shouldPassToolCallWithValidJsonWhenLengthTruncated() {
			ToolCall validToolCall = createToolCall("tc-1", "validTool", "{\"key\": \"value\"}");

			List<ToolCall> result = validator.validate(
					List.of(validToolCall),
					ChatCompletionFinishReason.LENGTH);

			assertThat(result).hasSize(1);
		}

		@Test
		@DisplayName("Should filter only last incomplete toolCall when LENGTH truncated")
		void shouldFilterOnlyIncompleteToolCallWhenLengthTruncated() {
			// First tool call - complete JSON
			ToolCall completeToolCall = createToolCall("tc-1", "completeTool", "{\"param\": 1}");
			// Second tool call - incomplete JSON (simulating truncation)
			ToolCall incompleteToolCall = createToolCall("tc-2", "incompleteTool", "{\"param\":");

			List<ToolCall> result = validator.validate(
					List.of(completeToolCall, incompleteToolCall),
					ChatCompletionFinishReason.LENGTH);

			assertThat(result).hasSize(1);
			assertThat(result.get(0).id()).isEqualTo("tc-1");
		}

		@Test
		@DisplayName("Should not check JSON validity when finishReason is not LENGTH")
		void shouldNotCheckJsonValidityWhenNotLengthTruncated() {
			// Even invalid JSON should pass when finishReason is not LENGTH
			ToolCall invalidJsonToolCall = createToolCall("tc-1", "myTool", "this is not json");

			List<ToolCall> result = validator.validate(
					List.of(invalidJsonToolCall),
					ChatCompletionFinishReason.STOP);

			assertThat(result).hasSize(1);
		}
	}

	@Nested
	@DisplayName("JSON Argument Edge Cases")
	class JsonArgumentEdgeCasesTests {

		@Test
		@DisplayName("Should accept null arguments as valid")
		void shouldAcceptNullArgumentsAsValid() {
			ToolCall nullArgsToolCall = createToolCall("tc-1", "noArgsTool", null);

			List<ToolCall> result = validator.validate(
					List.of(nullArgsToolCall),
					ChatCompletionFinishReason.LENGTH);

			assertThat(result).hasSize(1);
		}

		@Test
		@DisplayName("Should accept empty string arguments as valid")
		void shouldAcceptEmptyStringArgumentsAsValid() {
			ToolCall emptyArgsToolCall = createToolCall("tc-1", "noArgsTool", "");

			List<ToolCall> result = validator.validate(
					List.of(emptyArgsToolCall),
					ChatCompletionFinishReason.LENGTH);

			assertThat(result).hasSize(1);
		}

		@Test
		@DisplayName("Should accept blank string arguments as valid")
		void shouldAcceptBlankStringArgumentsAsValid() {
			ToolCall blankArgsToolCall = createToolCall("tc-1", "noArgsTool", "   ");

			List<ToolCall> result = validator.validate(
					List.of(blankArgsToolCall),
					ChatCompletionFinishReason.LENGTH);

			assertThat(result).hasSize(1);
		}

		@Test
		@DisplayName("Should accept empty JSON object as valid")
		void shouldAcceptEmptyJsonObjectAsValid() {
			ToolCall emptyObjectToolCall = createToolCall("tc-1", "myTool", "{}");

			List<ToolCall> result = validator.validate(
					List.of(emptyObjectToolCall),
					ChatCompletionFinishReason.LENGTH);

			assertThat(result).hasSize(1);
		}

		@Test
		@DisplayName("Should accept complex nested JSON as valid")
		void shouldAcceptComplexNestedJsonAsValid() {
			String complexJson = "{\"users\": [{\"name\": \"Alice\", \"age\": 30}, {\"name\": \"Bob\", \"age\": 25}], \"count\": 2}";
			ToolCall complexJsonToolCall = createToolCall("tc-1", "processUsers", complexJson);

			List<ToolCall> result = validator.validate(
					List.of(complexJsonToolCall),
					ChatCompletionFinishReason.LENGTH);

			assertThat(result).hasSize(1);
		}

		@Test
		@DisplayName("Should filter truncated JSON array")
		void shouldFilterTruncatedJsonArray() {
			// Truncated JSON array
			String truncatedArray = "[{\"name\": \"Alice\"}, {\"name\":";
			ToolCall truncatedToolCall = createToolCall("tc-1", "processUsers", truncatedArray);

			List<ToolCall> result = validator.validate(
					List.of(truncatedToolCall),
					ChatCompletionFinishReason.LENGTH);

			assertThat(result).isEmpty();
		}

		@Test
		@DisplayName("Should filter truncated string value")
		void shouldFilterTruncatedStringValue() {
			// String value truncated mid-way
			String truncatedString = "{\"message\": \"Hello, this is a very long message that gets trun";
			ToolCall truncatedToolCall = createToolCall("tc-1", "sendMessage", truncatedString);

			List<ToolCall> result = validator.validate(
					List.of(truncatedToolCall),
					ChatCompletionFinishReason.LENGTH);

			assertThat(result).isEmpty();
		}
	}

	@Nested
	@DisplayName("Null FinishReason Tests")
	class NullFinishReasonTests {

		@Test
		@DisplayName("Should handle null finishReason gracefully")
		void shouldHandleNullFinishReasonGracefully() {
			ToolCall validToolCall = createToolCall("tc-1", "myTool", "{\"key\": \"value\"}");

			List<ToolCall> result = validator.validate(
					List.of(validToolCall),
					null);

			assertThat(result).hasSize(1);
		}

		@Test
		@DisplayName("Should not check JSON validity when finishReason is null")
		void shouldNotCheckJsonValidityWhenFinishReasonIsNull() {
			// Invalid JSON should pass when finishReason is null
			ToolCall invalidJsonToolCall = createToolCall("tc-1", "myTool", "{invalid}");

			List<ToolCall> result = validator.validate(
					List.of(invalidJsonToolCall),
					null);

			assertThat(result).hasSize(1);
		}
	}

	@Nested
	@DisplayName("Multiple ToolCalls Validation Tests")
	class MultipleToolCallsTests {

		@Test
		@DisplayName("Should validate multiple toolCalls independently")
		void shouldValidateMultipleToolCallsIndependently() {
			ToolCall valid1 = createToolCall("tc-1", "tool1", "{\"a\": 1}");
			ToolCall invalid = createToolCallWithNullFunction("tc-2");
			ToolCall valid2 = createToolCall("tc-3", "tool3", "{\"b\": 2}");

			List<ToolCall> result = validator.validate(
					List.of(valid1, invalid, valid2),
					ChatCompletionFinishReason.STOP);

			assertThat(result).hasSize(2);
			assertThat(result).extracting(ToolCall::id).containsExactly("tc-1", "tc-3");
		}

		@Test
		@DisplayName("Should filter all invalid toolCalls")
		void shouldFilterAllInvalidToolCalls() {
			ToolCall nullFunction = createToolCallWithNullFunction("tc-1");
			ToolCall nullName = createToolCallWithNullFunctionName("tc-2", "{}");

			List<ToolCall> result = validator.validate(
					List.of(nullFunction, nullName),
					ChatCompletionFinishReason.STOP);

			assertThat(result).isEmpty();
		}

		@Test
		@DisplayName("Should preserve order of valid toolCalls")
		void shouldPreserveOrderOfValidToolCalls() {
			ToolCall first = createToolCall("tc-1", "firstTool", "{}");
			ToolCall second = createToolCall("tc-2", "secondTool", "{}");
			ToolCall third = createToolCall("tc-3", "thirdTool", "{}");

			List<ToolCall> result = validator.validate(
					List.of(first, second, third),
					ChatCompletionFinishReason.STOP);

			assertThat(result).hasSize(3);
			assertThat(result.get(0).id()).isEqualTo("tc-1");
			assertThat(result.get(1).id()).isEqualTo("tc-2");
			assertThat(result.get(2).id()).isEqualTo("tc-3");
		}
	}

}
