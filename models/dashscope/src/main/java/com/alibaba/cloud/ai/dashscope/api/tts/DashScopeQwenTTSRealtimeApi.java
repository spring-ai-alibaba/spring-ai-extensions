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

import com.alibaba.cloud.ai.dashscope.audio.tts.DashScopeAudioSpeechOptions;
import com.alibaba.cloud.ai.dashscope.common.DashScopeAudioApiConstants;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.springframework.ai.model.ApiKey;
import org.springframework.ai.model.NoopApiKey;
import org.springframework.ai.util.JacksonUtils;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;

import java.nio.ByteBuffer;
import java.util.Collections;

/**
 * DashScope Qwen TTS Realtime API. Uses append/commit WebSocket protocol.
 *
 * @author spring-ai-alibaba
 */
public class DashScopeQwenTTSRealtimeApi {

	private final String baseUrl;
	private final ApiKey apiKey;
	private final String workSpaceId;
	private final ObjectMapper objectMapper;

	public DashScopeQwenTTSRealtimeApi(String baseUrl, ApiKey apiKey, String workSpaceId) {
		this.baseUrl = baseUrl;
		this.apiKey = apiKey;
		this.workSpaceId = workSpaceId;
		this.objectMapper = JsonMapper.builder()
				.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
				.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
				.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
				.serializationInclusion(JsonInclude.Include.NON_NULL)
				.addModules(JacksonUtils.instantiateAvailableModules())
				.build();
	}

	/**
	 * Stream TTS for single text. Protocol: connect -> append -> commit -> finish.
	 */
	public Flux<ByteBuffer> stream(String text, DashScopeAudioSpeechOptions options) {
		String url = buildUrl(options.getModel());
		QwenTTSRealtimeWebSocketClient client = new QwenTTSRealtimeWebSocketClient(url,
				apiKey instanceof NoopApiKey ? "" : apiKey.getValue(), workSpaceId,
				Collections.emptyMap(), objectMapper);
		return client.stream(text).doOnComplete(client::close).doOnError(e -> client.close());
	}

	/**
	 * Stream TTS for streaming text input.
	 */
	public Flux<ByteBuffer> stream(Flux<String> textStream, DashScopeAudioSpeechOptions options) {
		String url = buildUrl(options.getModel());
		QwenTTSRealtimeWebSocketClient client = new QwenTTSRealtimeWebSocketClient(url,
				apiKey instanceof NoopApiKey ? "" : apiKey.getValue(), workSpaceId,
				Collections.emptyMap(), objectMapper);
		return client.stream(textStream).doOnComplete(client::close).doOnError(e -> client.close());
	}

	private String buildUrl(String model) {
		String base = baseUrl != null ? baseUrl : DashScopeAudioApiConstants.QWEN_TTS_REALTIME_WEBSOCKET_URL;
		return base + "?model=" + model;
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {

		private String baseUrl = DashScopeAudioApiConstants.QWEN_TTS_REALTIME_WEBSOCKET_URL;
		private ApiKey apiKey;
		private String workSpaceId;

		public Builder baseUrl(String baseUrl) {
			this.baseUrl = baseUrl;
			return this;
		}

		public Builder apiKey(ApiKey apiKey) {
			this.apiKey = apiKey;
			return this;
		}

		public Builder workSpaceId(String workSpaceId) {
			this.workSpaceId = workSpaceId;
			return this;
		}

		public DashScopeQwenTTSRealtimeApi build() {
			Assert.notNull(apiKey, "apiKey must be set");
			return new DashScopeQwenTTSRealtimeApi(baseUrl, apiKey, workSpaceId);
		}
	}
}
