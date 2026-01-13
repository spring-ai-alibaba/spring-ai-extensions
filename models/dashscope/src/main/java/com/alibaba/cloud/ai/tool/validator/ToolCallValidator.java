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

import java.util.List;

/**
 * Strategy interface for validating tool calls.
 *
 * This interface allows different validation strategies to be applied
 * based on the context (sync vs streaming, different finish reasons, etc.).
 *
 * @author gccszs
 * @since 2026/01/13
 */
@FunctionalInterface
public interface ToolCallValidator {

	/**
	 * Validates and filters tool calls, removing invalid ones.
	 *
	 * @param toolCalls the list of tool calls to validate
	 * @param finishReason the finish reason from the LLM response
	 * @return a list of valid tool calls
	 */
	List<ToolCall> validate(List<ToolCall> toolCalls, ChatCompletionFinishReason finishReason);

}
