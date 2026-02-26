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

import com.alibaba.cloud.ai.dashscope.api.ApiUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * WebSocket client for Qwen TTS Realtime protocol (append/commit).
 * <p>
 * Server events handled (per
 * <a href="https://help.aliyun.com/zh/model-studio/qwen-tts-realtime-server-events">服务端事件</a>):
 * <ul>
 *   <li>error - Server/client error, error object has code and message</li>
 *   <li>session.created - Session ready, can send append/commit/finish</li>
 *   <li>session.updated - Response to session.update</li>
 *   <li>input_text_buffer.committed - Response to commit</li>
 *   <li>input_text_buffer.cleared - Response to clear</li>
 *   <li>response.created - Response created for commit</li>
 *   <li>response.output_item.added - New output item</li>
 *   <li>response.content_part.added - New content part</li>
 *   <li>response.audio.delta - Audio chunk (base64 in "delta" field)</li>
 *   <li>response.content_part.done - Content part done</li>
 *   <li>response.output_item.done - Output item done</li>
 *   <li>response.audio.done - Audio generation done</li>
 *   <li>response.done - Response synthesis complete</li>
 *   <li>session.finished - Session done, server will close connection</li>
 * </ul>
 * Audio is delivered in JSON via response.audio.delta, not binary frames.
 *
 * @author spring-ai-alibaba
 */
public class QwenTTSRealtimeWebSocketClient extends WebSocketListener {

	private static final Logger log = LoggerFactory.getLogger(QwenTTSRealtimeWebSocketClient.class);

	private final String url;
	private final String apiKey;
	private final String workSpaceId;
	private final Map<String, String> customHeaders;
	private final ObjectMapper objectMapper;

	private WebSocket webSocket;
	private FluxSink<ByteBuffer> binarySink;
	private volatile boolean sessionCreated = false;
	private final AtomicBoolean completed = new AtomicBoolean(false);

	public QwenTTSRealtimeWebSocketClient(String url, String apiKey, String workSpaceId,
			Map<String, String> customHeaders, ObjectMapper objectMapper) {
		this.url = url;
		this.apiKey = apiKey;
		this.workSpaceId = workSpaceId;
		this.customHeaders = customHeaders != null ? customHeaders : Collections.emptyMap();
		this.objectMapper = objectMapper;
	}

	/**
	 * Stream TTS for the given text. Protocol: connect -> append -> commit -> finish -> receive
	 * audio deltas (in JSON response.audio.delta as base64).
	 */
	public Flux<ByteBuffer> stream(String text) {
		return Flux.<ByteBuffer>create(sink -> {
			this.binarySink = sink;
			connect();
			waitForSessionCreated();
			appendText(text);
			commit();
			finish();
		});
	}

	/**
	 * Stream TTS for streaming text input. Protocol: connect -> for each text append -> commit ->
	 * finish -> receive audio deltas.
	 */
	public Flux<ByteBuffer> stream(Flux<String> textStream) {
		return Flux.<ByteBuffer>create(sink -> {
			this.binarySink = sink;
			connect();
			waitForSessionCreated();
			textStream.doOnNext(this::appendText)
					.doOnComplete(() -> {
						commit();
						finish();
					})
					.doOnError(err -> safeCompleteError(err))
					.subscribe();
		});
	}

	private void connect() {
		Map<String, String> headers = ApiUtils.getMapContentHeaders(apiKey, false, workSpaceId,
				customHeaders);
		Request request = new Request.Builder().url(url).headers(Headers.of(headers)).build();
		OkHttpClient client = new OkHttpClient.Builder()
				.connectTimeout(30, TimeUnit.SECONDS)
				.readTimeout(300, TimeUnit.SECONDS)
				.writeTimeout(60, TimeUnit.SECONDS)
				.build();
		webSocket = client.newWebSocket(request, this);
	}

	private void waitForSessionCreated() {
		int maxWait = 10;
		for (int i = 0; i < maxWait && !sessionCreated; i++) {
			try {
				Thread.sleep(500);
			}
			catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				throw new RuntimeException("Interrupted while waiting for session", e);
			}
		}
		if (!sessionCreated) {
			throw new RuntimeException("Timeout waiting for session.created");
		}
	}

	private void appendText(String text) {
		sendMessage(Map.of(QwenTTSRealtimeConstants.PROTOCOL_EVENT_ID, generateEventId(),
				QwenTTSRealtimeConstants.PROTOCOL_TYPE,
				QwenTTSRealtimeConstants.PROTOCOL_EVENT_TYPE_APPEND_TEXT,
				QwenTTSRealtimeConstants.PROTOCOL_TEXT, text));
	}

	private void commit() {
		sendMessage(Map.of(QwenTTSRealtimeConstants.PROTOCOL_EVENT_ID, generateEventId(),
				QwenTTSRealtimeConstants.PROTOCOL_TYPE,
				QwenTTSRealtimeConstants.PROTOCOL_EVENT_TYPE_COMMIT));
	}

	private void finish() {
		sendMessage(Map.of(QwenTTSRealtimeConstants.PROTOCOL_EVENT_ID, generateEventId(),
				QwenTTSRealtimeConstants.PROTOCOL_TYPE,
				QwenTTSRealtimeConstants.PROTOCOL_EVENT_SESSION_FINISH));
	}

	private void sendMessage(Map<String, Object> message) {
		try {
			String json = objectMapper.writeValueAsString(message);
			if (webSocket != null) {
				webSocket.send(json);
			}
		}
		catch (Exception e) {
			throw new RuntimeException("Failed to send message", e);
		}
	}

	private static String generateEventId() {
		return "event_" + UUID.randomUUID().toString().replace("-", "");
	}

	private void safeComplete() {
		if (completed.compareAndSet(false, true) && binarySink != null && !binarySink.isCancelled()) {
			binarySink.complete();
		}
	}

	private void safeCompleteError(Throwable t) {
		if (completed.compareAndSet(false, true) && binarySink != null && !binarySink.isCancelled()) {
			binarySink.error(t);
		}
	}

	private void emitAudioFromDelta(String base64Delta) {
		if (base64Delta == null || base64Delta.isEmpty()) {
			return;
		}
		try {
			byte[] decoded = Base64.getDecoder().decode(base64Delta);
			if (decoded != null && decoded.length > 0 && binarySink != null && !binarySink.isCancelled()) {
				binarySink.next(ByteBuffer.wrap(decoded));
			}
		}
		catch (IllegalArgumentException e) {
			log.warn("Failed to decode base64 audio delta: {}", e.getMessage());
		}
	}

	@Override
	public void onOpen(WebSocket webSocket, Response response) {
		log.debug("WebSocket opened");
	}

	@Override
	public void onMessage(WebSocket webSocket, String text) {
		try {
			JsonNode node = objectMapper.readTree(text);
			if (!node.has(QwenTTSRealtimeConstants.PROTOCOL_TYPE)) {
				log.debug("Received message without type");
				return;
			}

			String type = node.get(QwenTTSRealtimeConstants.PROTOCOL_TYPE).asText();
			switch (type) {
				case QwenTTSRealtimeConstants.PROTOCOL_RESPONSE_TYPE_ERROR:
					handleError(node);
					break;

				case QwenTTSRealtimeConstants.PROTOCOL_RESPONSE_TYPE_SESSION_CREATED:
					sessionCreated = true;
					if (log.isDebugEnabled() && node.has(QwenTTSRealtimeConstants.PROTOCOL_SESSION)) {
						JsonNode session = node.get(QwenTTSRealtimeConstants.PROTOCOL_SESSION);
						if (session.has(QwenTTSRealtimeConstants.PROTOCOL_SESSION_ID)) {
							log.debug("Session created: {}",
									session.get(QwenTTSRealtimeConstants.PROTOCOL_SESSION_ID).asText());
						}
					}
					break;

				case QwenTTSRealtimeConstants.PROTOCOL_RESPONSE_TYPE_SESSION_UPDATED:
					log.debug("Session updated");
					break;

				case QwenTTSRealtimeConstants.PROTOCOL_RESPONSE_TYPE_INPUT_BUFFER_COMMITTED:
					log.debug("Input text buffer committed");
					break;

				case QwenTTSRealtimeConstants.PROTOCOL_RESPONSE_TYPE_INPUT_BUFFER_CLEARED:
					log.debug("Input text buffer cleared");
					break;

				case QwenTTSRealtimeConstants.PROTOCOL_RESPONSE_TYPE_RESPONSE_CREATED:
					log.debug("Response created");
					break;

				case QwenTTSRealtimeConstants.PROTOCOL_RESPONSE_TYPE_OUTPUT_ITEM_ADDED:
					log.debug("Response output item added");
					break;

				case QwenTTSRealtimeConstants.PROTOCOL_RESPONSE_TYPE_CONTENT_PART_ADDED:
					log.debug("Response content part added");
					break;

				case QwenTTSRealtimeConstants.PROTOCOL_RESPONSE_TYPE_AUDIO_DELTA:
					if (node.has(QwenTTSRealtimeConstants.PROTOCOL_DELTA)) {
						String delta = node.get(QwenTTSRealtimeConstants.PROTOCOL_DELTA).asText();
						emitAudioFromDelta(delta);
					}
					break;

				case QwenTTSRealtimeConstants.PROTOCOL_RESPONSE_TYPE_CONTENT_PART_DONE:
					log.debug("Response content part done");
					break;

				case QwenTTSRealtimeConstants.PROTOCOL_RESPONSE_TYPE_OUTPUT_ITEM_DONE:
					log.debug("Response output item done");
					break;

				case QwenTTSRealtimeConstants.PROTOCOL_RESPONSE_TYPE_AUDIO_DONE:
					log.debug("Response audio done");
					break;

				case QwenTTSRealtimeConstants.PROTOCOL_RESPONSE_TYPE_RESPONSE_DONE:
					log.debug("Response done");
					break;

				case QwenTTSRealtimeConstants.PROTOCOL_RESPONSE_TYPE_SESSION_FINISHED:
					log.debug("Session finished");
					safeComplete();
					break;

				default:
					log.debug("Received event: {}", type);
			}
		}
		catch (Exception e) {
			log.warn("Failed to parse message: {}", e.getMessage());
			safeCompleteError(e);
		}
	}

	/**
	 * Parse error event. Structure: { "type": "error", "error": { "code": "...", "message": "..." } }
	 */
	private void handleError(JsonNode node) {
		String code = "UNKNOWN";
		String message = "No error message";
		if (node.has(QwenTTSRealtimeConstants.PROTOCOL_ERROR)) {
			JsonNode err = node.get(QwenTTSRealtimeConstants.PROTOCOL_ERROR);
			if (err.isObject()) {
				if (err.has(QwenTTSRealtimeConstants.PROTOCOL_ERROR_CODE)) {
					code = err.get(QwenTTSRealtimeConstants.PROTOCOL_ERROR_CODE).asText();
				}
				if (err.has(QwenTTSRealtimeConstants.PROTOCOL_ERROR_MESSAGE)) {
					message = err.get(QwenTTSRealtimeConstants.PROTOCOL_ERROR_MESSAGE).asText();
				}
			}
			else {
				message = err.asText();
			}
		}
		log.error("Server error: code={}, message={}", code, message);
		safeCompleteError(new RuntimeException("Qwen TTS Realtime error: code=" + code + ", message=" + message));
	}

	@Override
	public void onMessage(WebSocket webSocket, ByteString bytes) {
		// Audio is typically in JSON (response.audio.delta), but support binary as fallback
		if (binarySink != null && !binarySink.isCancelled() && bytes != null && bytes.size() > 0) {
			binarySink.next(bytes.asByteBuffer());
		}
	}

	@Override
	public void onClosing(WebSocket webSocket, int code, String reason) {
		log.debug("WebSocket closing: code={}, reason={}", code, reason);
	}

	@Override
	public void onClosed(WebSocket webSocket, int code, String reason) {
		log.debug("WebSocket closed: code={}, reason={}", code, reason);
		// Complete sink if not already (e.g. connection closed before session.finished)
		safeComplete();
	}

	@Override
	public void onFailure(WebSocket webSocket, Throwable t, Response response) {
		log.error("WebSocket failed: {}", t.getMessage());
		safeCompleteError(t);
	}

	public void close() {
		if (webSocket != null) {
			webSocket.close(1000, "bye");
		}
	}

}
