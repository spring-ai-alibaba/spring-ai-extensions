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
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.model.ApiKey;
import org.springframework.ai.model.NoopApiKey;
import org.springframework.ai.util.JacksonUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.json.JsonMapper;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
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
	private final @Nullable String workSpaceId;
	private final RestClient restClient;

	private final RestClient downloadClient = RestClient.create();
	private final WebClient webClient;
	private final JsonMapper jsonMapper;

	public DashScopeQwenTTSApi(String baseUrl, ApiKey apiKey, @Nullable String workSpaceId,
			@Nullable HttpHeaders headers, RestClient.Builder restClientBuilder,
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

		this.jsonMapper = JsonMapper.builder()
				.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
				.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
				.disable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS)
                .changeDefaultPropertyInclusion(incl -> incl.withValueInclusion(JsonInclude.Include.NON_NULL))
                .changeDefaultPropertyInclusion(incl -> incl.withContentInclusion(JsonInclude.Include.NON_NULL))
				.addModules(JacksonUtils.instantiateAvailableModules())
				.build();
	}

	public DashScopeAudioTTSResponse call(String text, DashScopeAudioSpeechOptions options) {
		String model = Objects.requireNonNull(options.getModel(), "TTS model is required");
		if (DashScopeAudioApiConstants.isQwenAudioTts(model)) {
			DashScopeAudioTTSResponse response = this.restClient.post()
				.uri(DashScopeAudioApiConstants.QWEN_AUDIO_TTS).body(audioRequest(model, text, options))
				.retrieve().body(DashScopeAudioTTSResponse.class);
			return withAudio(response);
		}
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
			return Objects.requireNonNull(response.getBody(), "DashScope Qwen TTS response body must not be null");
		}
		log.error("Failed to call Qwen TTS API: " + response.getStatusCode());
		throw new RuntimeException("Failed to call Qwen TTS API: " + response.getStatusCode());
	}

	public Flux<DashScopeAudioTTSResponse> stream(String text, DashScopeAudioSpeechOptions options) {
		String model = Objects.requireNonNull(options.getModel(), "TTS model is required");
		if (DashScopeAudioApiConstants.isQwenAudioTts(model)) {
			return this.webClient.post().uri(DashScopeAudioApiConstants.QWEN_AUDIO_TTS)
				.accept(MediaType.TEXT_EVENT_STREAM).header(HEADER_SSE, ENABLED)
				.bodyValue(audioRequest(model, text, options)).retrieve().bodyToFlux(String.class)
				.takeWhile(frame -> !"[DONE]".equals(frame))
				.map(frame -> {
					var payload = this.jsonMapper.readTree(frame);
					if (payload.hasNonNull("code")) {
						throw new IllegalStateException("DashScope audio synthesis failed: "
								+ payload.path("code").asString() + " (request_id=" + payload.path("request_id").asString() + ")");
					}
					return this.jsonMapper.treeToValue(payload, DashScopeAudioTTSResponse.class);
				})
				.filter(frame -> frame.getResult().getOutput().length > 0);
		}
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
				.map(content -> jsonMapper.readValue(content, DashScopeAudioTTSResponse.class));
	}

	private static Map<String, Object> audioRequest(String model, String text, DashScopeAudioSpeechOptions options) {
		Map<String, Object> input = new LinkedHashMap<>();
		input.put("text", text);
		input.put("voice", options.getVoice());
		input.put("format", options.getFormat());
		input.put("sample_rate", options.getSampleRate());
		input.put("volume", options.getVolume());
		input.put("rate", options.getRate());
		input.put("pitch", options.getPitch());
		input.put("instruction", options.getInstruction());
		input.put("enable_ssml", "SSML".equalsIgnoreCase(options.getTextType()) ? Boolean.TRUE : options.getEnableSsml());
		if ("opus".equalsIgnoreCase(options.getFormat())) {
			input.put("bit_rate", options.getBitRate());
		}
		input.put("enable_aigc_tag", options.getEnableAigcTag());
		input.put("aigc_propagator", options.getAigcPropagator());
		input.put("aigc_propagate_id", options.getAigcPropagateId());
		input.values().removeIf(Objects::isNull);
		return Map.of("model", model, "input", input);
	}

	private DashScopeAudioTTSResponse withAudio(@Nullable DashScopeAudioTTSResponse response) {
		if (response == null || response.getOutput() == null || response.getOutput().audio() == null) {
			throw new IllegalStateException("DashScope audio synthesis did not return audio");
		}
		if (response.getResult().getOutput().length > 0) {
			return response;
		}
		String url = response.getOutput().audio().url();
		Assert.hasText(url, "DashScope audio synthesis did not return a download URL");
		byte[] bytes = this.downloadClient.get().uri(URI.create(url)).retrieve().body(byte[].class);
		Assert.state(bytes != null && bytes.length > 0, "DashScope returned an empty audio file");
		return new DashScopeAudioTTSResponse(response, bytes);
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {

		private @Nullable String baseUrl;
		private @Nullable ApiKey apiKey;
		private @Nullable String workSpaceId;
		private @Nullable HttpHeaders headers;
		private RestClient.@Nullable Builder restClientBuilder;
		private WebClient.@Nullable Builder webClientBuilder;
		private @Nullable ResponseErrorHandler responseErrorHandler;

		public Builder baseUrl(String baseUrl) {
			this.baseUrl = baseUrl;
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

		public DashScopeQwenTTSApi build() {
			Assert.hasText(baseUrl, "baseUrl cannot be null or empty");
			Assert.notNull(apiKey, "apiKey must be set");
			Assert.notNull(restClientBuilder, "restClientBuilder cannot be null");
			Assert.notNull(webClientBuilder, "webClientBuilder cannot be null");
			Assert.notNull(responseErrorHandler, "responseErrorHandler cannot be null");
			String baseUrl = Objects.requireNonNull(this.baseUrl, "baseUrl cannot be null");
			ApiKey apiKey = Objects.requireNonNull(this.apiKey, "apiKey must be set");
			RestClient.Builder restClientBuilder = Objects.requireNonNull(this.restClientBuilder,
					"restClientBuilder cannot be null");
			WebClient.Builder webClientBuilder = Objects.requireNonNull(this.webClientBuilder,
					"webClientBuilder cannot be null");
			ResponseErrorHandler responseErrorHandler = Objects.requireNonNull(this.responseErrorHandler,
					"responseErrorHandler cannot be null");
			return new DashScopeQwenTTSApi(baseUrl, apiKey, this.workSpaceId, this.headers, restClientBuilder,
					webClientBuilder, responseErrorHandler);
		}
	}
}
