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
package com.alibaba.cloud.ai.dashscope.api.tts;

/**
 * Qwen TTS Realtime WebSocket protocol constants.
 * <p>
 * Reference: <a href="https://help.aliyun.com/zh/model-studio/qwen-tts-realtime-client-events">客户端事件</a>,
 * <a href="https://help.aliyun.com/zh/model-studio/qwen-tts-realtime-server-events">服务端事件</a>
 *
 * @author spring-ai-alibaba
 */
public final class QwenTTSRealtimeConstants {

	private QwenTTSRealtimeConstants() {
	}

	public static final String PROTOCOL_EVENT_ID = "event_id";
	public static final String PROTOCOL_TYPE = "type";
	public static final String PROTOCOL_SESSION = "session";
	public static final String PROTOCOL_TEXT = "text";

	// ==================== Client events (send) ====================
	public static final String PROTOCOL_EVENT_TYPE_UPDATE_SESSION = "session.update";
	public static final String PROTOCOL_EVENT_TYPE_APPEND_TEXT = "input_text_buffer.append";
	public static final String PROTOCOL_EVENT_TYPE_COMMIT = "input_text_buffer.commit";
	public static final String PROTOCOL_EVENT_TYPE_CLEAR_TEXT = "input_text_buffer.clear";
	public static final String PROTOCOL_EVENT_TYPE_CANCEL_RESPONSE = "response.cancel";
	public static final String PROTOCOL_EVENT_SESSION_FINISH = "session.finish";

	// ==================== Server events (receive) ====================
	public static final String PROTOCOL_RESPONSE_TYPE_ERROR = "error";
	public static final String PROTOCOL_RESPONSE_TYPE_SESSION_CREATED = "session.created";
	public static final String PROTOCOL_RESPONSE_TYPE_SESSION_UPDATED = "session.updated";
	public static final String PROTOCOL_RESPONSE_TYPE_INPUT_BUFFER_COMMITTED = "input_text_buffer.committed";
	public static final String PROTOCOL_RESPONSE_TYPE_INPUT_BUFFER_CLEARED = "input_text_buffer.cleared";
	public static final String PROTOCOL_RESPONSE_TYPE_RESPONSE_CREATED = "response.created";
	public static final String PROTOCOL_RESPONSE_TYPE_OUTPUT_ITEM_ADDED = "response.output_item.added";
	public static final String PROTOCOL_RESPONSE_TYPE_CONTENT_PART_ADDED = "response.content_part.added";
	public static final String PROTOCOL_RESPONSE_TYPE_AUDIO_DELTA = "response.audio.delta";
	public static final String PROTOCOL_RESPONSE_TYPE_CONTENT_PART_DONE = "response.content_part.done";
	public static final String PROTOCOL_RESPONSE_TYPE_OUTPUT_ITEM_DONE = "response.output_item.done";
	public static final String PROTOCOL_RESPONSE_TYPE_AUDIO_DONE = "response.audio.done";
	public static final String PROTOCOL_RESPONSE_TYPE_RESPONSE_DONE = "response.done";
	public static final String PROTOCOL_RESPONSE_TYPE_SESSION_FINISHED = "session.finished";

	// ==================== Payload keys ====================
	public static final String PROTOCOL_DELTA = "delta";
	public static final String PROTOCOL_SESSION_ID = "id";
	public static final String PROTOCOL_RESPONSE = "response";
	public static final String PROTOCOL_RESPONSE_ID = "response_id";
	public static final String PROTOCOL_ERROR = "error";
	public static final String PROTOCOL_ERROR_CODE = "code";
	public static final String PROTOCOL_ERROR_MESSAGE = "message";
}
