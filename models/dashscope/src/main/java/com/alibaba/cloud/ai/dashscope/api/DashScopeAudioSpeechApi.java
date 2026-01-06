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
package com.alibaba.cloud.ai.dashscope.api;

import com.alibaba.cloud.ai.dashscope.protocol.DashScopeWebSocketClient;
import com.alibaba.cloud.ai.dashscope.protocol.DashScopeWebSocketClientOptions;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.springframework.ai.util.JacksonUtils;
import reactor.core.publisher.Flux;

import java.nio.ByteBuffer;
import java.util.List;

import static com.alibaba.cloud.ai.dashscope.common.DashScopeApiConstants.DEFAULT_WEBSOCKET_URL;

/**
 * @author xuguan
 */
public class DashScopeAudioSpeechApi {

	private final DashScopeWebSocketClient webSocketClient;

	private final ObjectMapper objectMapper;

	public DashScopeAudioSpeechApi(String apiKey) {
		this(apiKey, null);
	}

	public DashScopeAudioSpeechApi(String apiKey, String workSpaceId) {
		this(apiKey, workSpaceId, DEFAULT_WEBSOCKET_URL);
	}

	public DashScopeAudioSpeechApi(String apiKey, String workSpaceId, String websocketUrl) {
		this.webSocketClient = new DashScopeWebSocketClient(DashScopeWebSocketClientOptions.builder()
			.apiKey(apiKey)
			.workSpaceId(workSpaceId)
			.url(websocketUrl)
			.build());

		this.objectMapper =
			JsonMapper.builder()
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

	public Flux<ByteBuffer> streamBinaryOut(Request request) {
		try {
			String message = this.objectMapper.writeValueAsString(request);
			return this.webSocketClient.streamBinaryOut(message);
		}
		catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	// @formatter:off
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public record Request(
			@JsonProperty("header") RequestHeader header,
			@JsonProperty("payload") RequestPayload payload) {
		public record RequestHeader(
			@JsonProperty("action") DashScopeWebSocketClient.EventType action,
			@JsonProperty("task_id") String taskId,
			@JsonProperty("streaming") String streaming
		) {}
		@JsonInclude(JsonInclude.Include.NON_NULL)
		public record RequestPayload(
			@JsonProperty("model") String model,
			@JsonProperty("task_group") String taskGroup,
			@JsonProperty("task") String task,
			@JsonProperty("function") String function,
			@JsonProperty("input") RequestPayloadInput input,
			@JsonProperty("parameters") RequestPayloadParameters parameters) {
			@JsonInclude(JsonInclude.Include.NON_NULL)
			public record RequestPayloadInput(
				@JsonProperty("text") String text
			) {}
			@JsonInclude(JsonInclude.Include.NON_NULL)
			public record RequestPayloadParameters(
				@JsonProperty("volume") Integer volume,
				@JsonProperty("text_type") RequestTextType textType,
				@JsonProperty("voice") String voice,
				@JsonProperty("sample_rate") Integer sampleRate,
				@JsonProperty("rate") Double rate,
				@JsonProperty("format") ResponseFormat format,
				@JsonProperty("pitch") Double pitch,
				@JsonProperty("enable_ssml") Boolean enableSsml,
				@JsonProperty("bit_rate") Integer bitRate,
				@JsonProperty("seed") Integer seed,
				@JsonProperty("language_hints") List<String> languageHints,
				@JsonProperty("instruction") String instruction,
				@JsonProperty("phoneme_timestamp_enabled") Boolean phonemeTimestampEnabled,
				@JsonProperty("word_timestamp_enabled") Boolean wordTimestampEnabled
			) {}
		}
	}
	// @formatter:on

	// @formatter:off
    public static class Response {
        ByteBuffer audio;

        public ByteBuffer getAudio() {
            return audio;
        }
    }
    // @formatter:on

	public enum RequestTextType {

		// @formatter:off
		@JsonProperty("PlainText") PLAIN_TEXT("PlainText"),
		@JsonProperty("SSML") SSML("SSML");
		// @formatter:on

		private final String value;

		RequestTextType(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}

	}

	public enum ResponseFormat {

		// @formatter:off
		@JsonProperty("pcm") PCM("pcm"),
		@JsonProperty("wav") WAV("wav"),
		@JsonProperty("mp3") MP3("mp3");
		// @formatter:on

		public final String formatType;

		ResponseFormat(String value) {
			this.formatType = value;
		}

		public String getValue() {
			return this.formatType;
		}

	}

}
