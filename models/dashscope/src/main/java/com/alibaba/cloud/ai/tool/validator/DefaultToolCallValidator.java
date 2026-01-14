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
package com.alibaba.cloud.ai.tool.validator;

import com.alibaba.cloud.ai.dashscope.spec.DashScopeApiSpec.ChatCompletionFinishReason;
import com.alibaba.cloud.ai.dashscope.spec.DashScopeApiSpec.ChatCompletionMessage.ToolCall;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.util.json.JsonParser;

import java.util.List;

/**
 * Default implementation of {@link ToolCallValidator}.
 *
 * Filters out invalid tool calls with null function, null function name,
 * or incomplete JSON arguments when finishReason is LENGTH.
 *
 * @author gccszs
 * @since 2026/01/13
 */
public class DefaultToolCallValidator implements ToolCallValidator {

	private static final Logger logger = LoggerFactory.getLogger(DefaultToolCallValidator.class);


	@Override
	public List<ToolCall> validate(List<ToolCall> toolCalls, ChatCompletionFinishReason finishReason) {
		if (toolCalls == null || toolCalls.isEmpty()) {
			return List.of();
		}

		boolean isLengthTruncated = finishReason == ChatCompletionFinishReason.LENGTH;

		return toolCalls.stream()
			.filter(toolCall -> isValidToolCall(toolCall, isLengthTruncated))
			.toList();
	}

	/**
	 * Validates a single tool call.
	 *
	 * @param toolCall the tool call to validate
	 * @param isLengthTruncated whether the response was truncated due to max tokens
	 * @return true if the tool call is valid, false otherwise
	 */
	private boolean isValidToolCall(ToolCall toolCall, boolean isLengthTruncated) {
		// Basic validation: function must not be null
		if (toolCall.function() == null) {
			logger.warn("Filtering out toolCall with null function: {}", toolCall);
			return false;
		}

		// Basic validation: function name must not be null
		if (toolCall.function().name() == null) {
			logger.warn("Filtering out toolCall with null function name: {}", toolCall);
			return false;
		}

		// Additional validation when response was truncated due to LENGTH
		if (isLengthTruncated && !isValidJson(toolCall.function().arguments())) {
			logger.warn("Filtering out toolCall with incomplete arguments due to LENGTH truncation. " +
					"Tool: {}, Arguments: {}", toolCall.function().name(), toolCall.function().arguments());
			return false;
		}

		return true;
	}

	/**
	 * Checks if the given string is valid JSON.
	 *
	 * Empty or blank strings are considered valid (representing no arguments).
	 *
	 * @param json the string to check
	 * @return true if valid JSON or empty, false otherwise
	 */
	private boolean isValidJson(String json) {
		// Empty arguments are valid (for functions with no parameters)
		if (json == null || json.isBlank()) {
			return true;
		}

		try {
			JsonParser.getObjectMapper().readTree(json);
			return true;
		}
		catch (JsonProcessingException e) {
			logger.debug("Invalid JSON detected: {}", json);
			return false;
		}
	}

}
