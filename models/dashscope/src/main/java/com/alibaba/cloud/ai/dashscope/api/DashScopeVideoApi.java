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

import com.alibaba.cloud.ai.dashscope.common.DashScopeApiConstants;
import com.alibaba.cloud.ai.dashscope.spec.DashScopeApiSpec;
import com.alibaba.cloud.ai.dashscope.spec.DashScopeModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.model.ApiKey;
import org.springframework.ai.model.NoopApiKey;
import org.springframework.ai.model.SimpleApiKey;
import org.springframework.ai.retry.RetryUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClient;

import java.util.function.Consumer;

import static com.alibaba.cloud.ai.dashscope.common.DashScopeApiConstants.ENABLED;
import static com.alibaba.cloud.ai.dashscope.common.DashScopeApiConstants.HEADER_ASYNC;

/**
 * DashScope Video Generation API client.
 *
 * @author windWheel
 * @author yuluo
 * @since 1.0.0.3
 */

public class DashScopeVideoApi {

	private static final Logger logger = LoggerFactory.getLogger(DashScopeVideoApi.class);

	public static final String DEFAULT_VIDEO_MODEL = DashScopeModel.VideoModel.WANX2_1_T2V_TURBO.getValue();

	private final String baseUrl;

	private final ApiKey apiKey;

	private final RestClient restClient;

	private final ResponseErrorHandler responseErrorHandler;

	public Builder mutate() {
		return new Builder(this);
	}

	public static Builder builder() {
		return new Builder();
	}

	public DashScopeVideoApi(String baseUrl, ApiKey apiKey, RestClient.Builder restClientBuilder,
			ResponseErrorHandler responseErrorHandler) {

		this.baseUrl = baseUrl;
		this.apiKey = apiKey;
		this.responseErrorHandler = responseErrorHandler;

		// Check API Key in headers.
		Consumer<HttpHeaders> finalHeaders = h -> {
			if (!(apiKey instanceof NoopApiKey)) {
				h.setBearerAuth(apiKey.getValue());
			}
			h.setContentType(MediaType.APPLICATION_JSON);
		};

		this.restClient = restClientBuilder.clone()
			.baseUrl(baseUrl)
			.defaultHeaders(finalHeaders)
			.defaultStatusHandler(responseErrorHandler)
			.build();
	}

	/**
	 * Submit video generation task.
	 */
	public ResponseEntity<DashScopeApiSpec.VideoGenerationResponse> submitVideoGenTask(DashScopeApiSpec.VideoGenerationRequest request) {

		logger.debug("Submitting video generation task with options: {}", request);

		String uri;

		// Use unused uri paths based on the head and tail frames
		if (request.getInput().getFirstFrameUrl() != null || request.getInput().getLastFrameUrl() != null) {
			uri = DashScopeApiConstants.IMAGE2VIDEO_RESTFUL_URL;
		}
		else {
			uri = DashScopeApiConstants.VIDEO_GENERATION_RESTFUL_URL;
		}

		return this.restClient.post()
			.uri(uri)
			.body(request)
			.header(HEADER_ASYNC, ENABLED)
			.retrieve()
			.toEntity(DashScopeApiSpec.VideoGenerationResponse.class);
	}

	/**
	 * Query video generation task status.
	 */
	public ResponseEntity<DashScopeApiSpec.VideoGenerationResponse> queryVideoGenTask(String taskId) {
		return this.restClient.get()
			.uri(DashScopeApiConstants.QUERY_TASK_RESTFUL_URL, taskId)
			.retrieve()
			.toEntity(DashScopeApiSpec.VideoGenerationResponse.class);
	}

	String getBaseUrl() {
		return this.baseUrl;
	}

	ApiKey getApiKey() {
		return this.apiKey;
	}

	RestClient getRestClient() {
		return this.restClient;
	}

	ResponseErrorHandler getResponseErrorHandler() {
		return this.responseErrorHandler;
	}

	public static class Builder {

		public Builder() {
		}

		// Copy constructor for mutate()
		public Builder(DashScopeVideoApi api) {
			this.baseUrl = api.getBaseUrl();
			this.apiKey = api.getApiKey();
			this.restClientBuilder = api.restClient != null ? api.restClient.mutate() : RestClient.builder();
			this.responseErrorHandler = api.getResponseErrorHandler();
		}

		private String baseUrl = DashScopeApiConstants.DEFAULT_BASE_URL;

		private ApiKey apiKey;

		private RestClient.Builder restClientBuilder = RestClient.builder();

		private ResponseErrorHandler responseErrorHandler = RetryUtils.DEFAULT_RESPONSE_ERROR_HANDLER;

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

		public DashScopeVideoApi build() {

			Assert.notNull(apiKey, "API key cannot be null");

			return new DashScopeVideoApi(this.baseUrl, this.apiKey, this.restClientBuilder, this.responseErrorHandler);
		}

	}

}
