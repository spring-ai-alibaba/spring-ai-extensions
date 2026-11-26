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

import com.alibaba.cloud.ai.dashscope.api.tts.DashScopeQwenTTSApi;
import com.alibaba.cloud.ai.dashscope.api.tts.DashScopeQwenTTSRealtimeApi;
import com.alibaba.cloud.ai.dashscope.api.tts.DashScopeWebSocketTTSApi;
import com.alibaba.cloud.ai.dashscope.audio.tts.DashScopeAudioSpeechOptions;
import com.alibaba.cloud.ai.dashscope.audio.tts.DashScopeTTSApiSpec.DashScopeAudioTTSResponse;
import com.alibaba.cloud.ai.dashscope.audio.tts.DashScopeTtsStrategyRegistry;
import com.alibaba.cloud.ai.dashscope.common.DashScopeApiConstants;
import com.alibaba.cloud.ai.dashscope.common.DashScopeAudioApiConstants;
import com.alibaba.cloud.ai.dashscope.protocol.DashScopeWebSocketClientOptions;
import org.jspecify.annotations.Nullable;
import org.springframework.ai.model.ApiKey;
import org.springframework.ai.model.NoopApiKey;
import org.springframework.ai.retry.RetryUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.util.Assert;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.nio.ByteBuffer;
import java.util.Objects;

/**
 * Facade for DashScope TTS APIs. Delegates to Qwen-TTS, WebSocket (Sambert/CosyVoice), and Qwen TTS
 * Realtime based on model name.
 *
 * @author xuguan
 */
public class DashScopeAudioSpeechApi {

	private final String baseUrl;
	private final String websocketUrl;
	private final ApiKey apiKey;
	private final @Nullable String workSpaceId;
	private final HttpHeaders headers;
	private final DashScopeTtsStrategyRegistry strategyRegistry;
	private final DashScopeQwenTTSApi qwenTTSApi;
	private final DashScopeWebSocketTTSApi webSocketTTSApi;
	private final DashScopeQwenTTSRealtimeApi qwenTTSRealtimeApi;

	public DashScopeAudioSpeechApi(String baseUrl, String websocketUrl, ApiKey apiKey,
			@Nullable String workSpaceId, HttpHeaders headers,
			RestClient.Builder restClientBuilder, WebClient.Builder webClientBuilder,
			ResponseErrorHandler responseErrorHandler) {
		this.baseUrl = baseUrl;
		this.websocketUrl = websocketUrl;
		this.apiKey = apiKey;
		this.workSpaceId = workSpaceId;
		this.headers = headers;

		ApiKey effectiveApiKey = apiKey instanceof NoopApiKey
				? apiKey
				: apiKey;

		this.qwenTTSApi = DashScopeQwenTTSApi.builder()
				.baseUrl(baseUrl)
				.apiKey(effectiveApiKey)
				.workSpaceId(workSpaceId)
				.headers(headers)
				.restClientBuilder(restClientBuilder)
				.webClientBuilder(webClientBuilder)
				.responseErrorHandler(responseErrorHandler)
				.build();

		this.webSocketTTSApi = DashScopeWebSocketTTSApi.builder()
				.websocketUrl(websocketUrl)
				.apiKey(effectiveApiKey)
				.workSpaceId(workSpaceId)
				.options(DashScopeWebSocketClientOptions.builder()
						.apiKey(apiKey.getValue())
						.workSpaceId(workSpaceId)
						.url(websocketUrl)
						.build())
				.build();

		this.qwenTTSRealtimeApi = DashScopeQwenTTSRealtimeApi.builder()
				.baseUrl(DashScopeAudioApiConstants.QWEN_TTS_REALTIME_WEBSOCKET_URL)
				.apiKey(effectiveApiKey)
				.workSpaceId(workSpaceId)
				.build();

		this.strategyRegistry = new DashScopeTtsStrategyRegistry(qwenTTSApi, this.webSocketTTSApi,
				qwenTTSRealtimeApi);
	}

	public DashScopeTtsStrategyRegistry getStrategyRegistry() {
		return strategyRegistry;
	}

	public com.alibaba.cloud.ai.dashscope.api.tts.DashScopeQwenTTSRealtimeApi getQwenTTSRealtimeApi() {
		return qwenTTSRealtimeApi;
	}

	public DashScopeAudioTTSResponse callQwenTTS(String text, DashScopeAudioSpeechOptions options) {
		return (DashScopeAudioTTSResponse) qwenTTSApi.call(text, options);
	}

	public Flux<DashScopeAudioTTSResponse> streamQwenTTS(String text,
			DashScopeAudioSpeechOptions options) {
		return qwenTTSApi.stream(text, options).map(r -> (DashScopeAudioTTSResponse) r);
	}

	public Flux<ByteBuffer> createWebSocketTask(String text, DashScopeAudioSpeechOptions options) {
		String model = options.getModel() != null ? options.getModel() : DashScopeAudioSpeechOptions.DEFAULT_MODEL;
		if (DashScopeAudioApiConstants.isQwenTTSRealtimeModel(model)) {
			return qwenTTSRealtimeApi.stream(text, options);
		}
		if (DashScopeAudioApiConstants.isWebsocketByTTSModelName(model)) {
			return webSocketTTSApi.stream(text, options);
		}
		throw new IllegalArgumentException("Model " + model + " is not supported.");
	}

	/**
	 * Create a WebSocket TTS task with streaming text input (CosyVoice only).
	 * Each element in textStream is sent as a separate continue-task message,
	 * enabling true streaming text-to-speech synthesis.
	 */
	public Flux<ByteBuffer> createWebSocketStreamingTask(Flux<String> textStream,
			DashScopeAudioSpeechOptions options) {
		return webSocketTTSApi.streamWithStreamingInput(textStream, options);
	}

	public DashScopeWebSocketTTSApi getWebSocketTTSApi() {
		return webSocketTTSApi;
	}

    public Builder mutate() {
        return new Builder(this);
    }

    public String getBaseUrl() {
        return this.baseUrl;
    }

    public String getWebsocketUrl() {
        return this.websocketUrl;
    }

    public ApiKey getApiKey() {
        return this.apiKey;
    }

    public @Nullable String getWorkSpaceId() {
        return this.workSpaceId;
    }

    public HttpHeaders getHeaders() {
        return this.headers;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String baseUrl = DashScopeApiConstants.DEFAULT_BASE_URL;

        private String websocketUrl = DashScopeAudioApiConstants.DEFAULT_WEBSOCKET_URL;

        private @Nullable ApiKey apiKey;

        private @Nullable String workSpaceId;

        private HttpHeaders headers = new HttpHeaders();

        private RestClient.Builder restClientBuilder = RestClient.builder();

        private WebClient.Builder webClientBuilder = WebClient.builder();

        private ResponseErrorHandler responseErrorHandler = RetryUtils.DEFAULT_RESPONSE_ERROR_HANDLER;

        public Builder() {
        }

        public Builder(DashScopeAudioSpeechApi api) {
            this.baseUrl = api.getBaseUrl();
            this.websocketUrl = api.getWebsocketUrl();
            this.apiKey = api.getApiKey();
            this.workSpaceId = api.getWorkSpaceId();
            this.headers = api.getHeaders();
        }

        public Builder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public Builder websocketUrl(String websocketUrl) {
            this.websocketUrl = websocketUrl;
            return this;
        }

        public Builder apiKey(ApiKey apiKey) {
            this.apiKey = apiKey;
            return this;
        }

        public Builder workSpaceId(@Nullable String workSpaceId) {
            this.workSpaceId = workSpaceId;
            return this;
        }

        public Builder headers(HttpHeaders headers) {
            this.headers = headers;
            return this;
        }

        public Builder restClientBuilder(RestClient.Builder restClientBuilder) {
            this.restClientBuilder = restClientBuilder;
            return this;
        }

        public Builder webClientBuilder(WebClient.Builder webClientBuilder) {
            this.webClientBuilder = webClientBuilder;
            return this;
        }

        public Builder responseErrorHandler(ResponseErrorHandler responseErrorHandler) {
            this.responseErrorHandler = responseErrorHandler;
            return this;
        }

        public DashScopeAudioSpeechApi build() {
            Assert.hasText(this.baseUrl, "baseUrl cannot be null or empty");
            Assert.hasText(this.websocketUrl, "websocketUrl cannot be null or empty");
            Assert.notNull(this.headers, "headers cannot be null");
            Assert.notNull(this.restClientBuilder, "restClientBuilder cannot be null");
            Assert.notNull(this.webClientBuilder, "webClientBuilder cannot be null");
            Assert.notNull(this.responseErrorHandler, "responseErrorHandler cannot be null");
            ApiKey apiKey = Objects.requireNonNull(this.apiKey, "apiKey must be set");

            return new DashScopeAudioSpeechApi(
                    this.baseUrl,
                    this.websocketUrl,
                    apiKey,
                    this.workSpaceId,
                    this.headers,
                    this.restClientBuilder,
                    this.webClientBuilder,
                    this.responseErrorHandler);
        }
    }

}
