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
import com.alibaba.cloud.ai.dashscope.audio.tts.DashScopeTTSApiSpec.DashScopeAudioTTSRequest;
import com.alibaba.cloud.ai.dashscope.audio.tts.DashScopeTTSApiSpec.DashScopeAudioTTSResponse;
import com.alibaba.cloud.ai.dashscope.common.DashScopeAudioApiConstants;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.ai.util.JacksonUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;
import java.util.function.Predicate;

import static com.alibaba.cloud.ai.dashscope.common.DashScopeApiConstants.ENABLED;
import static com.alibaba.cloud.ai.dashscope.common.DashScopeApiConstants.HEADER_SSE;

/**
 * DashScope Qwen-TTS REST API client.
 * Supports call (sync) and stream (SSE) for Qwen-TTS models.
 *
 * @author spring-ai-alibaba
 */
public class DashScopeQwenTTSApi {

	private static final Logger log = LoggerFactory.getLogger(DashScopeQwenTTSApi.class);

	private final String baseUrl;
	private final ApiKey apiKey;
	private final String workSpaceId;
	private final RestClient restClient;
	private final WebClient webClient;
	private final ObjectMapper objectMapper;

	public DashScopeQwenTTSApi(String baseUrl, ApiKey apiKey, String workSpaceId,
			MultiValueMap<String, String> headers, RestClient.Builder restClientBuilder,
			WebClient.Builder webClientBuilder, ResponseErrorHandler responseErrorHandler) {
		this.baseUrl = baseUrl;
		this.apiKey = apiKey;
		this.workSpaceId = workSpaceId;

		Consumer<HttpHeaders> authHeaders = h -> {
			if (headers != null) {
				h.addAll(headers);
			}
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

		this.objectMapper = JsonMapper.builder()
				.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
				.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
				.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
				.serializationInclusion(JsonInclude.Include.NON_NULL)
				.addModules(JacksonUtils.instantiateAvailableModules())
				.build();
	}

	public DashScopeAudioTTSResponse call(String text, DashScopeAudioSpeechOptions options) {
		DashScopeAudioTTSRequest request = DashScopeAudioTTSRequest.builder()
				.model(options.getModel())
				.text(text)
				.voice(options.getVoice())
				.languageType(options.getLanguageType())
				.stream(false)
				.instructions(options.getInstruction())
				.optimizeInstructions(options.getOptimizeInstructions())
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

	public Flux<DashScopeAudioTTSResponse> stream(String text, DashScopeAudioSpeechOptions options) {
		DashScopeAudioTTSRequest request = DashScopeAudioTTSRequest.builder()
				.model(options.getModel())
				.text(text)
				.voice(options.getVoice())
				.languageType(options.getLanguageType())
				.stream(true)
				.instructions(options.getInstruction())
				.optimizeInstructions(options.getOptimizeInstructions())
				.build();

		Predicate<String> SSE_DONE_PREDICATE = "[DONE]"::equals;

		return webClient.post()
				.uri(DashScopeAudioApiConstants.MULTIMODAL_GENERATION)
				.headers(headers -> headers.add(HEADER_SSE, ENABLED))
				.body(Mono.just(request), DashScopeAudioTTSRequest.class)
				.retrieve()
				.bodyToFlux(String.class)
				.takeUntil(SSE_DONE_PREDICATE)
				.filter(SSE_DONE_PREDICATE.negate())
				.map(content -> {
					try {
						return objectMapper.readValue(content, DashScopeAudioTTSResponse.class);
					}
					catch (JsonProcessingException e) {
						throw new RuntimeException("Failed to parse TTS response: " + content, e);
					}
				});
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {

		private String baseUrl;
		private ApiKey apiKey;
		private String workSpaceId;
		private org.springframework.util.MultiValueMap<String, String> headers;
		private RestClient.Builder restClientBuilder;
		private WebClient.Builder webClientBuilder;
		private ResponseErrorHandler responseErrorHandler;

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

		public Builder headers(org.springframework.util.MultiValueMap<String, String> headers) {
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

		public DashScopeQwenTTSApi build() {
			Assert.hasText(baseUrl, "baseUrl cannot be null or empty");
			Assert.notNull(apiKey, "apiKey must be set");
			Assert.notNull(restClientBuilder, "restClientBuilder cannot be null");
			Assert.notNull(webClientBuilder, "webClientBuilder cannot be null");
			Assert.notNull(responseErrorHandler, "responseErrorHandler cannot be null");
			return new DashScopeQwenTTSApi(baseUrl, apiKey, workSpaceId, headers, restClientBuilder,
					webClientBuilder, responseErrorHandler);
		}
	}
}
