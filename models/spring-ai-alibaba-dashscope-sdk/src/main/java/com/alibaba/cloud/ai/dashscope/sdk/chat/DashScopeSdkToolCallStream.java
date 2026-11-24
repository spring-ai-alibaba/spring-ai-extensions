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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.tools.ToolCallBase;
import com.alibaba.dashscope.tools.ToolCallFunction;
import org.jspecify.annotations.Nullable;
import org.springframework.ai.chat.messages.AssistantMessage.ToolCall;
import org.springframework.ai.chat.messages.part.MessagePart;
import org.springframework.ai.chat.messages.part.StreamingParts;
import org.springframework.ai.chat.messages.part.ToolCallPart;
import org.springframework.util.StringUtils;

/** Collects argument deltas for a single response choice. */
final class DashScopeSdkToolCallStream {

	private final Map<Integer, PendingCall> pending = new TreeMap<>();

	List<MessagePart> accept(@Nullable Message message, @Nullable String finishReason, boolean incremental) {
		var parts = new ArrayList<MessagePart>();
		List<ToolCallBase> calls = message != null ? message.getToolCalls() : null;
		boolean complete = "tool_calls".equalsIgnoreCase(finishReason);
		if (calls != null) {
			for (int position = 0; position < calls.size(); position++) {
				if (!(calls.get(position) instanceof ToolCallFunction call)) {
					continue;
				}
				int index = Objects.requireNonNullElse(call.getIndex(), position);
				var pendingCall = this.pending.computeIfAbsent(index, key -> new PendingCall());
				String delta = pendingCall.update(call, incremental);
				if (incremental && !complete && !delta.isEmpty()) {
					parts.add(StreamingParts.partial(ToolCallPart.of(pendingCall.toToolCall(delta)), index + 2));
				}
			}
		}
		if (complete) {
			this.pending.forEach((index, call) -> {
				if (StringUtils.hasText(call.name)) {
					parts.add(StreamingParts.complete(ToolCallPart.of(call.toToolCall(call.arguments.toString())),
							index + 2));
				}
			});
			this.pending.clear();
		}
		return parts;
	}

	private static final class PendingCall {

		private String id = "";

		private String name = "";

		private final StringBuilder arguments = new StringBuilder();

		String update(ToolCallFunction call, boolean incremental) {
			if (StringUtils.hasText(call.getId())) {
				this.id = call.getId();
			}
			var function = call.getFunction();
			if (function == null) {
				return "";
			}
			if (StringUtils.hasText(function.getName())) {
				this.name = function.getName();
			}
			String delta = function.getArguments();
			if (delta == null) {
				return "";
			}
			if (!incremental) {
				this.arguments.setLength(0);
			}
			this.arguments.append(delta);
			return delta;
		}

		ToolCall toToolCall(String arguments) {
			return new ToolCall(this.id, "function", this.name, arguments);
		}

	}

}
