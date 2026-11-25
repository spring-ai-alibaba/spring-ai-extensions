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
package com.alibaba.cloud.ai.dashscope.chat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.IntUnaryOperator;

import org.jspecify.annotations.Nullable;
import org.springframework.ai.chat.messages.AssistantMessage.ToolCall;
import org.springframework.ai.chat.messages.part.MessagePart;
import org.springframework.ai.chat.messages.part.StreamingParts;
import org.springframework.ai.chat.messages.part.ToolCallPart;
import org.springframework.util.StringUtils;

/** Collects argument deltas for a single response choice. */
public final class DashScopeToolCallStream {

	private final Map<Integer, PendingCall> pending = new TreeMap<>();

	private final IntUnaryOperator blockIndex;

	public DashScopeToolCallStream() {
		this(index -> index + 2);
	}

	public DashScopeToolCallStream(IntUnaryOperator blockIndex) {
		this.blockIndex = blockIndex;
	}

	public List<MessagePart> accept(List<Delta> calls, boolean complete, boolean incremental) {
		var parts = new ArrayList<MessagePart>();
		for (Delta call : calls) {
			var pendingCall = this.pending.computeIfAbsent(call.index(), key -> new PendingCall(this.blockIndex.applyAsInt(key)));
			String delta = pendingCall.update(call, incremental);
			if (incremental && !complete && !delta.isEmpty()) {
				parts.add(StreamingParts.partial(ToolCallPart.of(pendingCall.toToolCall(delta)), pendingCall.index));
			}
		}
		if (complete) {
			this.pending.forEach((index, call) -> {
				if (StringUtils.hasText(call.name)) {
					parts.add(StreamingParts.complete(ToolCallPart.of(call.toToolCall(call.arguments.toString())),
							call.index));
				}
			});
			this.pending.clear();
		}
		return parts;
	}

	public record Delta(int index, @Nullable String id, @Nullable String name, @Nullable String arguments) {
	}

	private static final class PendingCall {

		private final int index;

		PendingCall(int index) {
			this.index = index;
		}

		private String id = "";

		private String name = "";

		private final StringBuilder arguments = new StringBuilder();

		String update(Delta call, boolean incremental) {
			if (StringUtils.hasText(call.id())) {
				this.id = call.id();
			}
			if (StringUtils.hasText(call.name())) {
				this.name = call.name();
			}
			String delta = call.arguments();
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
