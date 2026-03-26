/*
 * Copyright 2026-2027 the original author or authors.
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

import com.alibaba.cloud.ai.dashscope.spec.DashScopeApiSpec;
import com.alibaba.cloud.ai.dashscope.spec.DashScopeModel;
import org.springframework.ai.model.ApiKey;
import org.springframework.ai.model.NoopApiKey;
import org.springframework.ai.model.SimpleApiKey;
import org.springframework.ai.retry.RetryUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClient;

import java.util.function.Consumer;

import static com.alibaba.cloud.ai.dashscope.common.DashScopeApiConstants.DEFAULT_BASE_URL;
import static com.alibaba.cloud.ai.dashscope.common.DashScopeApiConstants.MULTIMODAL_EMBEDDING_RESTFUL_URL;

/**
 * @author buvidk
 */
public class DashScopeMultimodalEmbeddingApi {

	public static final String DEFAULT_MULTIMODAL_EMBEDDING_MODEL = DashScopeModel.MultimodalEmbeddingModel.TONGYI_EMBEDDING_VISION_PLUS.getValue();

	private final String multimodalPath;

	private final RestClient restClient;

	/**
	 * Client to communicate with DashScope Multimodal Embedding API.
	 *
	 * @param baseUrl              api base URL.
	 * @param apiKey               API key.
	 * @param multimodalPath       request path.
	 * @param headers              additional HTTP headers.
	 * @param restClientBuilder    RestClient builder.
	 * @param responseErrorHandler Response error handler.
	 */
	public DashScopeMultimodalEmbeddingApi(
			String baseUrl,
			ApiKey apiKey,
			String multimodalPath,
			MultiValueMap<String, String> headers,
			RestClient.Builder restClientBuilder,
			ResponseErrorHandler responseErrorHandler
	) {
		this.multimodalPath = multimodalPath;

		Consumer<HttpHeaders> finalHeaders = h -> {
			h.addAll(headers);
			if (!(apiKey instanceof NoopApiKey)) {
				h.setBearerAuth(apiKey.getValue());
			}
			h.add("Content-Type", "application/json");
		};

		this.restClient = restClientBuilder.clone()
				.baseUrl(baseUrl)
				.defaultHeaders(finalHeaders)
				.defaultStatusHandler(responseErrorHandler)
				.build();
	}

	public static Builder builder() {
		return new Builder();
	}

	public ResponseEntity<DashScopeApiSpec.MultimodalEmbeddingResponse> embedding(DashScopeApiSpec.MultimodalEmbeddingRequest embeddingRequest) {
		Assert.notNull(embeddingRequest, "The request body can not be null.");
		Assert.notNull(embeddingRequest.input(), "The input can not be null.");
		Assert.isTrue(!CollectionUtils.isEmpty(embeddingRequest.input().contents()), "The input contents can not be empty.");

		return this.restClient.post()
				.uri(this.multimodalPath)
				.body(embeddingRequest)
				.retrieve()
				.toEntity(DashScopeApiSpec.MultimodalEmbeddingResponse.class);
	}

	public static class Builder {

		private String baseUrl = DEFAULT_BASE_URL;

		private ApiKey apiKey;

		private String multimodalPath = MULTIMODAL_EMBEDDING_RESTFUL_URL;

		private final MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();

		private RestClient.Builder restClientBuilder = RestClient.builder();

		private ResponseErrorHandler responseErrorHandler = RetryUtils.DEFAULT_RESPONSE_ERROR_HANDLER;

		public Builder() {
		}

		public Builder baseUrl(String baseUrl) {
			Assert.notNull(baseUrl, "Base URL cannot be null");
			this.baseUrl = baseUrl;
			return this;
		}

		public Builder apiKey(String simpleApiKey) {
			Assert.notNull(simpleApiKey, "Simple api key cannot be null");
			this.apiKey = new SimpleApiKey(simpleApiKey);
			return this;
		}

		public Builder apiKey(ApiKey apiKey) {
			Assert.notNull(apiKey, "Api key cannot be null");
			this.apiKey = apiKey;
			return this;
		}

		public Builder multimodalPath(String multimodalPath) {
			Assert.notNull(multimodalPath, "Multimodal path cannot be null");
			this.multimodalPath = multimodalPath;
			return this;
		}

		public Builder headers(MultiValueMap<String, String> headers) {
			if (headers != null) {
				this.headers.addAll(headers);
			}
			return this;
		}

		public Builder restClientBuilder(RestClient.Builder restClientBuilder) {
			Assert.notNull(restClientBuilder, "Rest client builder cannot be null");
			this.restClientBuilder = restClientBuilder;
			return this;
		}

		public Builder responseErrorHandler(ResponseErrorHandler responseErrorHandler) {
			Assert.notNull(responseErrorHandler, "Response error handler cannot be null");
			this.responseErrorHandler = responseErrorHandler;
			return this;
		}

		public DashScopeMultimodalEmbeddingApi build() {
			return new DashScopeMultimodalEmbeddingApi(baseUrl, apiKey, multimodalPath, headers, restClientBuilder, responseErrorHandler);
		}

	}
}
