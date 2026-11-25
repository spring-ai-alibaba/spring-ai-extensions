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
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.alibaba.cloud.ai.dashscope.common.DashScopeApiConstants;
import org.jspecify.annotations.Nullable;
import org.springframework.ai.chat.messages.part.MediaPart;
import org.springframework.ai.chat.messages.part.MessagePart;
import org.springframework.ai.chat.messages.part.OpaquePayload;
import org.springframework.ai.chat.messages.part.StreamingParts;
import org.springframework.ai.chat.messages.part.TextPart;
import org.springframework.ai.chat.messages.part.ToolResultPart;
import org.springframework.ai.chat.messages.part.UnknownPart;
import org.springframework.ai.content.Media;
import org.springframework.http.MediaTypeFactory;
import org.springframework.util.MimeType;
import org.springframework.util.StringUtils;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.node.ObjectNode;

/** Translates the content blocks shared by DashScope chat transports. */
public final class DashScopeMessageParts {

	private static final String PROVIDER = DashScopeApiConstants.PROVIDER_NAME;

	private static final String CONTENT_BLOCK = "content-block";

	private DashScopeMessageParts() {
	}

	public static List<MessagePart> decode(@Nullable Object content) {
		if (content == null) {
			return List.of();
		}
		if (content instanceof String text) {
			return text.isEmpty() ? List.of() : List.of(TextPart.of(text));
		}
		var blocks = JsonMapper.shared().valueToTree(content);
		List<MessagePart> result = new ArrayList<>();
		if (blocks.isArray()) {
			blocks.forEach(block -> result.add(decodeBlock(block)));
		}
		else {
			result.add(decodeBlock(blocks));
		}
		return result;
	}

	private static MessagePart decodeBlock(JsonNode block) {
		for (String kind : List.of("text", "image", "audio", "video")) {
			var value = block.path(kind);
			if (value.isString()) {
				String data = value.asString();
				OpaquePayload raw = block.size() > 1 ? new OpaquePayload(PROVIDER, CONTENT_BLOCK, block.toString()) : null;
				if ("text".equals(kind)) {
					return new TextPart(data, raw, Map.of());
				}
				return new MediaPart(Media.builder().mimeType(mimeType(kind, data)).data(data).build(), raw, Map.of());
			}
		}
		var audio = block.path("audio");
		if (audio.path("data").isString()) {
			String format = audio.path("format").asString("wav");
			String data = audio.path("data").asString();
			if (!data.startsWith("data:")) {
				data = "data:audio/" + format + ";base64," + data;
			}
			return MediaPart.of(Media.builder().mimeType(mimeType("audio", data)).data(data).build());
		}
		String kind = block.path("type").asString();
		return new UnknownPart(PROVIDER, StringUtils.hasText(kind) ? kind : CONTENT_BLOCK, block.toString(), null,
				Map.of());
	}

	private static MimeType mimeType(String kind, String data) {
		int separator = data.indexOf(';');
		if (data.startsWith("data:") && separator > 5) {
			return MimeType.valueOf(data.substring(5, separator));
		}
		String path = data.split("[?#]", 2)[0];
		return MediaTypeFactory.getMediaType(path).filter(type -> kind.equals(type.getType()))
			.map(MimeType.class::cast)
			.orElseGet(() -> new MimeType(kind, switch (kind) {
				case "image" -> "png";
				case "video" -> "mp4";
				default -> "wav";
			}));
	}

	public static boolean requiresBlocks(List<MessagePart> parts) {
		return parts.stream().anyMatch(part -> part instanceof MediaPart
				|| part instanceof UnknownPart unknown && PROVIDER.equals(unknown.provider())
				|| part instanceof TextPart text && isContentPayload(text.payload()));
	}

	/** The documented DashScope tool-message content is a string. */
	public static String toolResultText(ToolResultPart result) {
		if (result.content().stream().anyMatch(part -> !(part instanceof TextPart))) {
			throw new IllegalArgumentException("DashScope tool results support text content only");
		}
		String text = result.content().stream().map(TextPart.class::cast).map(TextPart::text)
			.collect(Collectors.joining());
		return result.isError() ? JsonMapper.shared().writeValueAsString(Map.of("is_error", true, "content", text)) : text;
	}

	public static @Nullable JsonNode replayBlock(MessagePart part) {
		if (part instanceof UnknownPart unknown && PROVIDER.equals(unknown.provider())) {
			return JsonMapper.shared().readTree(unknown.rawJson());
		}
		if (part instanceof TextPart text && contentPayload(text.payload()) instanceof ObjectNode raw) {
			return raw.put("text", text.text());
		}
		if (part instanceof MediaPart media && contentPayload(media.payload()) instanceof ObjectNode raw) {
			return raw.put(media.media().getMimeType().getType(), mediaData(media.media()));
		}
		return null;
	}

	private static @Nullable JsonNode contentPayload(@Nullable OpaquePayload payload) {
		return payload != null && isContentPayload(payload) ? JsonMapper.shared().readTree(payload.data()) : null;
	}

	private static boolean isContentPayload(@Nullable OpaquePayload payload) {
		return payload != null && PROVIDER.equals(payload.provider()) && CONTENT_BLOCK.equals(payload.kind());
	}

	public static String mediaData(Media media) {
		Object data = media.getData();
		if (data instanceof byte[] bytes) {
			return "data:" + media.getMimeType() + ";base64," + Base64.getEncoder().encodeToString(bytes);
		}
		if (data instanceof String value) {
			return value;
		}
		throw new IllegalArgumentException("DashScope media must contain a URL or bytes");
	}

	public static String toolCallId(ToolResultPart result) {
		String id = result.id();
		if (!StringUtils.hasText(id)) {
			throw new IllegalArgumentException("A DashScope tool result must have a tool call ID");
		}
		return Objects.requireNonNull(id);
	}

	/** Assigns ordered, collision-free indexes to content and tool calls in a choice. */
	public static final class Stream {

		private final Map<Integer, Block> blocks = new HashMap<>();

		private int nextIndex = 2;

		private @Nullable Block lastContent;

		public int nextIndex() {
			return this.nextIndex++;
		}

		public List<MessagePart> accept(@Nullable Object content, boolean incremental) {
			var decoded = decode(content);
			if (incremental && decoded.size() == 1 && decoded.get(0) instanceof TextPart text) {
				var previous = this.lastContent;
				int index = previous != null && previous.part() instanceof TextPart ? previous.index() : nextContentIndex();
				var block = new Block(index, text);
				this.lastContent = block;
				if (this.blocks.get(0) == null || this.blocks.get(0).index() == index) {
					this.blocks.put(0, block);
				}
				return List.of(StreamingParts.partial(text, index));
			}
			List<MessagePart> parts = new ArrayList<>(decoded.size());
			for (int position = 0; position < decoded.size(); position++) {
				var part = decoded.get(position);
				var previous = this.blocks.get(position);
				boolean appendBlock = incremental && previous != null
						&& (!(part instanceof TextPart) || !(previous.part() instanceof TextPart))
						&& !part.equals(previous.part());
				int index = previous == null || appendBlock ? nextContentIndex() : previous.index();
				var block = new Block(index, part);
				this.blocks.put(position, block);
				this.lastContent = block;
				parts.add(incremental && part instanceof TextPart
					? StreamingParts.partial(part, index) : StreamingParts.complete(part, index));
			}
			return parts;
		}

		private int nextContentIndex() {
			return this.lastContent == null ? 1 : nextIndex();
		}

		private record Block(int index, MessagePart part) {
		}

	}

}
