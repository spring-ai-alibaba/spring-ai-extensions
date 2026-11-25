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

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.alibaba.cloud.ai.dashscope.common.DashScopeApiConstants;
import org.jspecify.annotations.Nullable;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.part.MessagePart;
import org.springframework.ai.chat.messages.part.OpaquePayload;
import org.springframework.ai.chat.messages.part.ReasoningPart;
import org.springframework.ai.chat.messages.part.StreamingParts;
import org.springframework.util.StringUtils;

/** Maps DashScope reasoning fields to Spring AI message parts and back. */
public final class DashScopeChatReasoning {

	private static final String ENCRYPTED_CONTENT = "encrypted_content";

	private DashScopeChatReasoning() {
	}

	public static @Nullable ReasoningPart fromResponse(@Nullable String reasoning, @Nullable String encrypted,
			boolean summarized) {
		if (!StringUtils.hasLength(reasoning) && !StringUtils.hasLength(encrypted)) {
			return null;
		}
		if (summarized || StringUtils.hasLength(encrypted)) {
			OpaquePayload payload = StringUtils.hasLength(encrypted)
					? new OpaquePayload(DashScopeApiConstants.PROVIDER_NAME, ENCRYPTED_CONTENT, encrypted) : null;
			return new ReasoningPart(null, reasoning, payload, Map.of());
		}
		return ReasoningPart.of(Objects.requireNonNull(reasoning));
	}

	public static String reasoningContent(AssistantMessage message) {
		if (message.getReasoning().isEmpty()) {
			return message.getMetadata().get("reasoningContent") instanceof String legacy ? legacy : "";
		}
		return message.getReasoning().stream()
			.filter(part -> part.payload() == null)
			.map(part -> Objects.requireNonNullElse(part.text(), ""))
			.collect(Collectors.joining());
	}

	public static @Nullable String encryptedContent(AssistantMessage message) {
		for (ReasoningPart part : message.getReasoning()) {
			OpaquePayload payload = part.payload();
			if (payload != null && part.replayableTo(DashScopeApiConstants.PROVIDER_NAME)
					&& ENCRYPTED_CONTENT.equals(payload.kind())) {
				return payload.data();
			}
		}
		return null;
	}

	/** Keeps an independently delivered payload when the stream contains full snapshots. */
	public static final class Stream {

		private @Nullable String encrypted;

		public @Nullable MessagePart accept(@Nullable String reasoning, @Nullable String encrypted,
				boolean incremental, boolean summarized) {
			if (StringUtils.hasLength(encrypted)) {
				this.encrypted = encrypted;
			}
			boolean snapshot = !incremental && StringUtils.hasLength(reasoning);
			ReasoningPart part = fromResponse(reasoning, snapshot ? this.encrypted : encrypted, summarized);
			if (part == null) {
				return null;
			}
			return snapshot ? StreamingParts.complete(part, 0) : StreamingParts.partial(part, 0);
		}

	}

}
