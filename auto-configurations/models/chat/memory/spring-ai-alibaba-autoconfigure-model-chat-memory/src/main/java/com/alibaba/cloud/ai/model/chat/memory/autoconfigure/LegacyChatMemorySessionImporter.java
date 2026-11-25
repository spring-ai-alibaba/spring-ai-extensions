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
package com.alibaba.cloud.ai.model.chat.memory.autoconfigure;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.IntStream;

import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.session.CreateSessionRequest;
import org.springframework.ai.session.EventType;
import org.springframework.ai.session.SessionEvent;
import org.springframework.ai.session.SessionService;
import org.springframework.util.Assert;

/**
 * Explicitly imports one legacy conversation into an empty session. A repeat with the
 * same source and target skips an already imported prefix.
 */
public class LegacyChatMemorySessionImporter {

	private final SessionService sessionService;

	public LegacyChatMemorySessionImporter(SessionService sessionService) {
		this.sessionService = Objects.requireNonNull(sessionService);
	}

	/**
	 * Imports the messages returned by the source repository in their original order.
	 * @return number of imported messages, or zero when already imported or empty
	 */
	public int importConversation(ChatMemoryRepository source, String conversationId, CreateSessionRequest target) {
		Assert.hasText(conversationId, "conversationId must not be blank");
		String sessionId = target.id();
		Assert.hasText(sessionId, "An explicit target session id is required");
		List<Message> messages = List.copyOf(source.findByConversationId(conversationId));
		if (messages.isEmpty()) {
			return 0;
		}
		var events = IntStream.range(0, messages.size())
			.mapToObj(index -> SessionEvent.builder()
				.id(eventId(conversationId, sessionId, index))
				.sessionId(sessionId)
				.type(EventType.MESSAGE)
				.message(messages.get(index))
				.build())
			.toList();

		this.sessionService.findOrCreate(target);
		if (this.sessionService.getEventVersion(sessionId) == 0
				&& this.sessionService.appendEvents(sessionId, events, 0)) {
			return events.size();
		}
		List<SessionEvent> existing = this.sessionService.getEvents(sessionId);
		if (existing.size() >= events.size() && IntStream.range(0, events.size())
			.allMatch(index -> sameMessageEvent(existing.get(index), events.get(index)))) {
			return 0;
		}
		throw new IllegalStateException("Target session already has events that do not match the legacy conversation");
	}

	private static boolean sameMessageEvent(SessionEvent existing, SessionEvent imported) {
		return existing.getId().equals(imported.getId()) && Objects.equals(existing.getMessage(), imported.getMessage());
	}

	private static String eventId(String conversationId, String sessionId, int index) {
		String key = "spring-ai-alibaba:legacy-chat-memory:" + conversationId.length() + ":" + conversationId
				+ ":" + sessionId.length() + ":" + sessionId + ":" + index;
		return UUID.nameUUIDFromBytes(key.getBytes(StandardCharsets.UTF_8)).toString();
	}

}
