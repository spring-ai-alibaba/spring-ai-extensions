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

import com.alibaba.cloud.ai.dashscope.audio.tts.DashScopeAudioSpeechOptions;
import com.alibaba.cloud.ai.dashscope.audio.DashScopeWebSocketClient.EventType;
import com.alibaba.cloud.ai.dashscope.audio.WebSocketRequest;
import com.alibaba.cloud.ai.dashscope.audio.DashScopeWebSocketClient;
import com.alibaba.cloud.ai.dashscope.audio.WebSocketRequest.RequestHeader;
import com.alibaba.cloud.ai.dashscope.audio.WebSocketRequest.RequestPayload;
import com.alibaba.cloud.ai.dashscope.audio.WebSocketRequest.RequestPayloadInput;
import com.alibaba.cloud.ai.dashscope.audio.tts.DashScopeTTSApiSpec.DashScopeAudioTTSRequest;
import com.alibaba.cloud.ai.dashscope.audio.tts.DashScopeTTSApiSpec.DashScopeAudioTTSResponse;
import com.alibaba.cloud.ai.dashscope.common.DashScopeApiConstants;
import com.alibaba.cloud.ai.dashscope.common.DashScopeAudioApiConstants;
import com.alibaba.cloud.ai.dashscope.protocol.DashScopeWebSocketClientOptions;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.model.ApiKey;
import org.springframework.ai.model.NoopApiKey;
import org.springframework.ai.retry.RetryUtils;
import org.springframework.ai.util.JacksonUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.ByteBuffer;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static com.alibaba.cloud.ai.dashscope.common.DashScopeApiConstants.ENABLED;
import static com.alibaba.cloud.ai.dashscope.common.DashScopeApiConstants.HEADER_SSE;

/**
 * @author xuguan
 */
public class DashScopeAudioSpeechApi {

    private static final Logger log = LoggerFactory.getLogger(DashScopeAudioSpeechApi.class);

    private final String baseUrl;

    private final String websocketUrl;

    private final ApiKey apiKey;

    private final String workSpaceId;

    private final MultiValueMap<String, String> headers;

    private final DashScopeWebSocketClient webSocketClient;

    private final RestClient restClient;

    private final WebClient webClient;

    private final ObjectMapper objectMapper;

    public DashScopeAudioSpeechApi(String baseUrl,
                                   String  websocketUrl,
                                   ApiKey apiKey,
                                   String workSpaceId,
                                   MultiValueMap<String, String> headers,
                                   RestClient.Builder restClientBuilder,
                                   WebClient.Builder webClientBuilder,
                                   ResponseErrorHandler responseErrorHandler) {
        this.baseUrl = baseUrl;
        this.websocketUrl = websocketUrl;
        this.apiKey = apiKey;
        this.workSpaceId = workSpaceId;
        this.headers = headers;

        Consumer<HttpHeaders> authHeaders = h -> {
            h.addAll(headers);
            h.setContentType(MediaType.APPLICATION_JSON);
            if (!(apiKey instanceof NoopApiKey)) {
                h.setBearerAuth(apiKey.getValue());
            }

        };

        this.restClient = restClientBuilder.clone()
                .baseUrl(baseUrl)
                .defaultHeaders(authHeaders)
                .defaultStatusHandler(responseErrorHandler)
                .build();

        this.webClient = webClientBuilder.clone()
                .baseUrl(baseUrl)
                .defaultHeaders(authHeaders)
                .build();

		this.webSocketClient = new DashScopeWebSocketClient(
                DashScopeWebSocketClientOptions.builder()
			        .apiKey(apiKey.getValue())
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

    public DashScopeAudioTTSResponse callQwenTTS(String text, DashScopeAudioSpeechOptions options) {
        DashScopeAudioTTSRequest request = DashScopeAudioTTSRequest.builder()
                .model(options.getModel())
                .text(text)
                .voice(options.getVoice())
                .languageType(options.getLanguageType())
                .build();

        ResponseEntity<DashScopeAudioTTSResponse> response = restClient.post()
                .uri(DashScopeAudioApiConstants.MULTIMODAL_GENERATION)
                .body(request)
                .retrieve()
                .toEntity(DashScopeAudioTTSResponse.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        }

        log.error("Failed to call Qwen TTS API: " + response.getStatusCode());
        throw new RuntimeException("Failed to call Qwen TTS API: " + response.getStatusCode());
    }

    public Flux<DashScopeAudioTTSResponse> streamQwenTTS(String text, DashScopeAudioSpeechOptions options) {
        DashScopeAudioTTSRequest request = DashScopeAudioTTSRequest.builder()
                .model(options.getModel())
                .text(text)
                .voice(options.getVoice())
                .languageType(options.getLanguageType())
                .build();

        // SSE 流结束标志
        Predicate<String> SSE_DONE_PREDICATE = "[DONE]"::equals;

        return this.webClient.post()
                .uri(DashScopeAudioApiConstants.MULTIMODAL_GENERATION)
                .headers(headers -> {
                    headers.add(HEADER_SSE, ENABLED);  // X-DashScope-SSE: enable
                })
                .body(Mono.just(request), DashScopeAudioTTSRequest.class)
                .retrieve()
                .bodyToFlux(String.class)  // 接收 SSE 流数据
                .takeUntil(SSE_DONE_PREDICATE)  // 遇到 [DONE] 停止
                .filter(SSE_DONE_PREDICATE.negate())  // 过滤掉 [DONE]
                .map(content -> {
                    try {
                        return this.objectMapper.readValue(content, DashScopeAudioTTSResponse.class);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException("Failed to parse TTS response: " + content, e);
                    }
                });
    }

    public Flux<ByteBuffer> createWebSocketTask(String text, DashScopeAudioSpeechOptions options) {
        boolean isCosyVoiceModel = DashScopeAudioApiConstants.COSY_VOICE_MODEL_LIST.contains(options.getModel());

        String taskId = UUID.randomUUID().toString();
        // run-task
        WebSocketRequest runTaskRequest = WebSocketRequest.builder()
                .header(WebSocketRequest.RequestHeader.builder()
                        .action(EventType.RUN_TASK)
                        .taskId(taskId)
                        .streaming(isCosyVoiceModel ? "duplex" : "output") // duplex对应cosy voice，output对应 sambert
                        .build())
                .payload(WebSocketRequest.RequestPayload.builder()
                        .model(options.getModel())
                        .taskGroup("audio")
                        .task("tts")
                        .function("SpeechSynthesizer")
                        .input(WebSocketRequest.RequestPayloadInput.builder()
                                .text(isCosyVoiceModel ? null : text) // cosy voice不需要text, sambert需要text
                                .build())
                        .parameters(WebSocketRequest.RequestPayloadParameters
                                .speechOptionsConvertReq(options))
                        .build())
                .build();
        // continue-task
        WebSocketRequest continueTaskRequest = WebSocketRequest.builder()
                .header(RequestHeader.builder()
                        .action(EventType.CONTINUE_TASK)
                        .taskId(taskId)
                        .streaming(isCosyVoiceModel ? "duplex" : "output") // duplex对应cosy voice，output对应 sambert
                        .build())
                .payload(RequestPayload.builder().
                        input(RequestPayloadInput.builder()
                            .text(text)
                            .build()
                ).build())
                .build();
        // finish-task
        WebSocketRequest finishTaskRequest = WebSocketRequest.builder()
                .header(RequestHeader.builder()
                        .action(EventType.FINISH_TASK)
                        .taskId(taskId)
                        .streaming(isCosyVoiceModel ? "duplex" : "output") // duplex对应cosy voice，output对应 sambert
                        .build())
                .payload(RequestPayload.builder()
                        .input(RequestPayloadInput.builder()
                                .build())
                        .build())
                .build();
        try {
            String runTaskMessage = this.objectMapper.writeValueAsString(runTaskRequest);
            String continueTaskMessage = this.objectMapper.writeValueAsString(continueTaskRequest);
            String finishTaskMessage = this.objectMapper.writeValueAsString(finishTaskRequest);
            if (isCosyVoiceModel) {
                return this.webSocketClient.command(runTaskMessage, continueTaskMessage,
                        finishTaskMessage);
            } else {
                return this.webSocketClient.command(runTaskMessage);
            }

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
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

    public String getWorkSpaceId() {
        return this.workSpaceId;
    }

    public MultiValueMap<String, String> getHeaders() {
        return this.headers;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String baseUrl = DashScopeApiConstants.DEFAULT_BASE_URL;

        private String websocketUrl = DashScopeAudioApiConstants.DEFAULT_WEBSOCKET_URL;

        private ApiKey apiKey;

        private String workSpaceId;

        private MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();

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

        public Builder workSpaceId(String workSpaceId) {
            this.workSpaceId = workSpaceId;
            return this;
        }

        public Builder headers(MultiValueMap<String, String> headers) {
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
            Assert.notNull(this.apiKey, "apiKey must be set");
            Assert.notNull(this.headers, "headers cannot be null");
            Assert.notNull(this.restClientBuilder, "restClientBuilder cannot be null");
            Assert.notNull(this.webClientBuilder, "webClientBuilder cannot be null");
            Assert.notNull(this.responseErrorHandler, "responseErrorHandler cannot be null");

            return new DashScopeAudioSpeechApi(
                    this.baseUrl,
                    this.websocketUrl,
                    this.apiKey,
                    this.workSpaceId,
                    this.headers,
                    this.restClientBuilder,
                    this.webClientBuilder,
                    this.responseErrorHandler);
        }
    }

}
