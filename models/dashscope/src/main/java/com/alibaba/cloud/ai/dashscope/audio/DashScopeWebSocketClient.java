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
package com.alibaba.cloud.ai.dashscope.audio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import com.alibaba.cloud.ai.dashscope.api.ApiUtils;
import com.alibaba.cloud.ai.dashscope.protocol.DashScopeWebSocketClientOptions;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.ByteString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.util.JacksonUtils;
import org.springframework.util.ObjectUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

/**
 * @author kevinlin09, xuguan, yingzi
 */
public class DashScopeWebSocketClient extends WebSocketListener {

	private final Logger logger = LoggerFactory.getLogger(DashScopeWebSocketClient.class);

	private final DashScopeWebSocketClientOptions options;

	private final AtomicBoolean isOpen;

	private final ObjectMapper objectMapper;

	private WebSocket webSocketClient;

	FluxSink<ByteBuffer> binaryEmitter;

	FluxSink<String> textEmitter;

    private String continueTaskMessage;

    private String finishTaskMessage;

    private ByteBuffer binaryData;

	public DashScopeWebSocketClient(DashScopeWebSocketClientOptions options) {
		this.options = options;
		this.isOpen = new AtomicBoolean(false);
		this.objectMapper = JsonMapper.builder()
			// Deserialization configuration
			.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
			// Serialization configuration
			.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
			.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
			.serializationInclusion(JsonInclude.Include.NON_NULL)
			// Register standard Jackson modules (Jdk8, JavaTime, ParameterNames, Kotlin)
			.addModules(JacksonUtils.instantiateAvailableModules())
			.build();
	}

	/**
	 * Stream binary output using event-driven duplex flow for CosyVoice.
	 * This implements the official protocol specification:
	 * 1. Send run-task
	 * 2. Wait for task-started event
	 * 3. Send continue-task
	 * 4. Send finish-task
	 * 5. Wait for task-finished event
	 *
	 * @param runTaskMessage the run-task JSON message
	 * @param continueTaskMessage the continue-task JSON message
	 * @param finishTaskMessage the finish-task JSON message
	 * @return the binary data flux
	 */
	public Flux<ByteBuffer> command(String runTaskMessage, String continueTaskMessage,
			String finishTaskMessage) {
		// Prepare the messages to be sent
		this.continueTaskMessage = continueTaskMessage;
		this.finishTaskMessage = finishTaskMessage;

		return Flux.<ByteBuffer>create(emitter -> {
			this.binaryEmitter = emitter;

			// Send run-task first
			logger.info("Event-driven : Sending run-task message");
			sendText(runTaskMessage);

		}, FluxSink.OverflowStrategy.BUFFER);
	}

    public Flux<ByteBuffer> command(String runTaskMessage) {
        return Flux.<ByteBuffer>create(emitter -> {
            this.binaryEmitter = emitter;

            // Send run-task first
            logger.info("Event-driven : Sending run-task message");
            sendText(runTaskMessage);

        }, FluxSink.OverflowStrategy.BUFFER);
    }


    public Flux<String> command(String runTaskMessage, ByteBuffer binaryData, String finishTaskMessage) {
        this.binaryData = binaryData;
        this.finishTaskMessage = finishTaskMessage;

        return Flux.<String>create(emitter -> {
            this.textEmitter = emitter;

            // Send run-task first
            logger.info("Event-driven : Sending run-task message");
            sendText(runTaskMessage);
        }, FluxSink.OverflowStrategy.BUFFER);
    }

	public void sendText(String text) {
        if (!isOpen.get()) {
            establishWebSocketClient();
            try {
                TimeUnit.SECONDS.sleep(Constants.DEFAULT_READY_TIMEOUT);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // 恢复中断状态
                throw new RuntimeException("Interrupted while waiting for WebSocket connection", e);
            }
        }
        logger.info("send text: {}", text);
		boolean success = webSocketClient.send(text);

		if (!success) {
			logger.error("send text failed");
		}
	}

	public void sendBinary(ByteBuffer binary) {
		if (!isOpen.get()) {
			establishWebSocketClient();
		}

		if (binary == null) {
			logger.error("binary data is null");
			return;
		}

		boolean success = webSocketClient.send(ByteString.of(binary));

		if (!success) {
			logger.error("send binary failed");
		}
	}

	private void establishWebSocketClient() {
		HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
		logging.setLevel(HttpLoggingInterceptor.Level.valueOf(Constants.DEFAULT_HTTP_LOGGING_LEVEL));
		Dispatcher dispatcher = new Dispatcher();
		dispatcher.setMaxRequests(Constants.DEFAULT_MAXIMUM_ASYNC_REQUESTS);
		dispatcher.setMaxRequestsPerHost(Constants.DEFAULT_MAXIMUM_ASYNC_REQUESTS_PER_HOST);

		OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
		clientBuilder.connectTimeout(Constants.DEFAULT_CONNECT_TIMEOUT)
			.readTimeout(Constants.DEFAULT_READ_TIMEOUT)
			.writeTimeout(Constants.DEFAULT_WRITE_TIMEOUT)
			.addInterceptor(logging)
			.dispatcher(dispatcher)
			.protocols(Collections.singletonList(Protocol.HTTP_1_1))
			.connectionPool(new ConnectionPool(Constants.DEFAULT_CONNECTION_POOL_SIZE,
					Constants.DEFAULT_CONNECTION_IDLE_TIMEOUT.getSeconds(), TimeUnit.SECONDS));
		OkHttpClient httpClient = clientBuilder.build();

		try {
			this.webSocketClient = httpClient.newWebSocket(buildConnectionRequest(), this);
		}
		catch (Throwable ex) {
			logger.error("create websocket failed: msg={}", ex.getMessage());
		}
	}

	private Request buildConnectionRequest() {
		Builder bd = new Request.Builder();
		bd.headers(Headers.of(ApiUtils.getMapContentHeaders(options.getApiKey(), false,
			options.getWorkSpaceId(), null)));
		return bd.url(options.getUrl()).build();
	}

	private String getRequestBody(Response response) {
		String responseBody = "";
		if (response != null && response.body() != null) {
			try {
				responseBody = response.body().string();
			}
			catch (IOException ex) {
				logger.error("get response body failed: {}", ex.getMessage());
			}
		}
		return responseBody;
	}

	@Override
	public void onOpen(WebSocket webSocket, Response response) {
		logger.info("receive ws event onOpen: handle={}, body={}", webSocket, getRequestBody(response));
		isOpen.set(true);
	}

	@Override
	public void onClosed(WebSocket webSocket, int code, String reason) {
		logger.info("receive ws event onClosed: handle={}, code={}, reason={}", webSocket, code, reason);
		isOpen.set(false);
		emittersComplete("closed");
	}

	@Override
	public void onClosing(WebSocket webSocket, int code, String reason) {
		logger.info("receive ws event onClosing: handle={}, code={}, reason={}", webSocket.toString(), code, reason);
		emittersComplete("closing");
		webSocket.close(code, reason);
	}

	@Override
	public void onFailure(WebSocket webSocket, Throwable t, Response response) {
		String failureMessage = String.format("msg=%s, cause=%s, body=%s", t.getMessage(), t.getCause(),
				getRequestBody(response));
		logger.error("receive ws event onFailure: handle={}, {}", webSocket, failureMessage);
		isOpen.set(false);
		emittersError("failure", new Exception(failureMessage, t));
	}

	@Override
	public void onMessage(WebSocket webSocket, String text) {
		logger.debug("receive ws event onMessage(text): handle={}, text={}", webSocket, text);

		try {
            EventMessage message = this.objectMapper.readValue(text, EventMessage.class);
			switch (message.header().event()) {
				case TASK_STARTED:
					logger.info("task started: text={}", text);
                    if (!ObjectUtils.isEmpty(this.continueTaskMessage)) {
                        sendText(this.continueTaskMessage);
                    }
                    if (!ObjectUtils.isEmpty(this.binaryData)) {
                        sendBinary(this.binaryData);
                        sendText(this.finishTaskMessage);
                    }
					break;
                case RESULT_GENERATED:
                    logger.debug("result generated: text={}", text);
                    if (this.textEmitter != null) {
                        textEmitter.next(text);
                    }

                    break;
				case TASK_FINISHED:
					logger.info("task finished: text={}", text);
                    if (!ObjectUtils.isEmpty(this.finishTaskMessage)) {
                        sendText(this.finishTaskMessage);
                    }
					emittersComplete("finished");
					break;
				case TASK_FAILED:
                    String errorCode = message.header().code() != null ? message.header().code() : "UNKNOWN";
                    String errorMessage =
                            message.header().message() != null ? message.header().message() : "No error message provided";
                    String errorDetail = String.format("Task failed with error_code='%s', error_message='%s'", errorCode, errorMessage);
                    logger.error("task failed: text={}, error_code={}, error_message={}", text, errorCode, errorMessage);
                    // Reset duplex state on error
                    emittersError("task failed", new Exception(errorDetail));
					break;
			}
		}
		catch (Exception e) {
			logger.error("parse message failed: text={}, msg={}", text, e.getMessage());
		}
	}

	@Override
	public void onMessage(WebSocket webSocket, ByteString bytes) {
		logger.debug("receive ws event onMessage(bytes): handle={}, size={}", webSocket, bytes.size());
		ByteBuffer audioData = bytes.asByteBuffer();
        this.binaryEmitter.next(audioData);

	}

	private void emittersComplete(String event) {
		if (this.binaryEmitter != null && !this.binaryEmitter.isCancelled()) {
			logger.info("binary emitter handling: complete on {}", event);
			this.binaryEmitter.complete();
		}
		if (this.textEmitter != null && !this.textEmitter.isCancelled()) {
			logger.info("text emitter handling: complete on {}", event);
			this.textEmitter.complete();
			logger.info("done");
		}
	}

	private void emittersError(String event, Throwable t) {
		if (this.binaryEmitter != null && !this.binaryEmitter.isCancelled()) {
			logger.info("binary emitter handling: error on {}", event);
			this.binaryEmitter.error(t);
		}
		if (this.textEmitter != null && !this.textEmitter.isCancelled()) {
			logger.info("text emitter handling: error on {}", event);
			this.textEmitter.error(t);
		}
	}

	public static class Constants {

		private static final Duration DEFAULT_CONNECT_TIMEOUT = Duration.ofSeconds(120);

		private static final Duration DEFAULT_WRITE_TIMEOUT = Duration.ofSeconds(60);

		private static final Duration DEFAULT_READ_TIMEOUT = Duration.ofSeconds(300);

		private static final Duration DEFAULT_CONNECTION_IDLE_TIMEOUT = Duration.ofSeconds(300);

        private static final Integer DEFAULT_READY_TIMEOUT = 1;

		private static final Integer DEFAULT_CONNECTION_POOL_SIZE = 32;

		private static final Integer DEFAULT_MAXIMUM_ASYNC_REQUESTS = 32;

		private static final Integer DEFAULT_MAXIMUM_ASYNC_REQUESTS_PER_HOST = 32;

		private static final String DEFAULT_HTTP_LOGGING_LEVEL = "NONE";

	}

	// @formatter:off
	public enum EventType {

		// receive
		@JsonProperty("task-started")
		TASK_STARTED("task-started"),

		@JsonProperty("result-generated")
		RESULT_GENERATED("result-generated"),

		@JsonProperty("task-finished")
		TASK_FINISHED("task-finished"),

		@JsonProperty("task-failed")
		TASK_FAILED("task-failed"),

		// send
		@JsonProperty("run-task")
		RUN_TASK("run-task"),

		@JsonProperty("continue-task")
		CONTINUE_TASK("continue-task"),

		@JsonProperty("finish-task")
		FINISH_TASK("finish-task");

		private final String value;

		private EventType(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}
	}

}
