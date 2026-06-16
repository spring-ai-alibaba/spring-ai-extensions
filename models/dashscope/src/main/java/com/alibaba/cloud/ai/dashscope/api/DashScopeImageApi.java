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

import com.alibaba.cloud.ai.dashscope.common.DashScopeImageApiConstants;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageApiSpec;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageApiSpec.InvokeMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.model.ApiKey;
import org.springframework.ai.model.SimpleApiKey;
import org.springframework.ai.retry.RetryUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClient;

import static com.alibaba.cloud.ai.dashscope.common.DashScopeApiConstants.DEFAULT_BASE_URL;
import static com.alibaba.cloud.ai.dashscope.common.DashScopeApiConstants.ENABLED;
import static com.alibaba.cloud.ai.dashscope.common.DashScopeApiConstants.HEADER_ASYNC;
import static com.alibaba.cloud.ai.dashscope.spec.DashScopeModel.ImageModel.QWEN_IMAGE;

/**
 * DashScope image generation API client.
 *
 * @author nuocheng.lxm
 * @author yuluo-yx
 * @author Soryu
 * @author xuguan
 */

public class DashScopeImageApi {

    private static final Logger logger = LoggerFactory.getLogger(DashScopeImageApi.class);

	private final String baseUrl;

	private final ApiKey apiKey;

    private final @Nullable String imagesPath;

    private final @Nullable String queryTaskPath;

	public static final String DEFAULT_IMAGE_MODEL = QWEN_IMAGE.getValue();

	private final RestClient restClient;

	private final ResponseErrorHandler responseErrorHandler;

    @Override
    public DashScopeImageApi clone() {
        return mutate().build();
    }

	/**
	 * Returns a builder pre-populated with the current configuration for mutation.
	 */
	public Builder mutate() {
		return new Builder(this);
	}

	public static Builder builder() {
		return new Builder();
	}

	// format: off
	public DashScopeImageApi(String baseUrl, ApiKey apiKey, @Nullable String imagesPath, @Nullable String queryTaskPath, String workSpaceId,
                             RestClient.Builder restClientBuilder, ResponseErrorHandler responseErrorHandler) {

		this.baseUrl = baseUrl;
		this.apiKey = apiKey;
        this.imagesPath = imagesPath;
        this.queryTaskPath = queryTaskPath;
        this.responseErrorHandler = responseErrorHandler;

		Assert.notNull(apiKey, "ApiKey must not be null");
		Assert.notNull(baseUrl, "Base URL must not be null");
		Assert.notNull(restClientBuilder, "RestClientBuilder must not be null");

		this.restClient = restClientBuilder.clone()
            .baseUrl(baseUrl)
				.defaultHeaders(ApiUtils.getJsonContentHeaders(apiKey.getValue(), workSpaceId))
				.defaultStatusHandler(responseErrorHandler)
				.build();
	}

	public ResponseEntity<DashScopeImageApiSpec.ImageResponse> submitImageGenTask(DashScopeImageApiSpec.ImageRequest request, InvokeMode invokeMode) {
		final String model = request.model();

        String imagePath = this.resolveImagePath(model, invokeMode);
        Assert.hasText(imagePath, "Image path must not be empty");

        var requestBuilder = this.restClient.post()
			.uri(imagePath)
			.body(request);

		if (DashScopeImageApiConstants.isAsync(model, invokeMode)) {
			requestBuilder.header(HEADER_ASYNC, ENABLED);
		}

		return requestBuilder
			.retrieve()
			.toEntity(DashScopeImageApiSpec.ImageResponse.class);
	}

    public ResponseEntity<DashScopeImageApiSpec.ImageResponse> getImageGenTaskResult(String model, String taskId) {
        String queryTaskPath = this.resolveQueryTaskPath(model);
        Assert.hasText(queryTaskPath, "Query task path must not be empty");

        return this.restClient.get()
                .uri(queryTaskPath, taskId)
                .retrieve()
                .toEntity(DashScopeImageApiSpec.ImageResponse.class);
    }

	/**
	 * Resolves API path and request body type from model name.
	 * Mapping per Aliyun Bailian: text-to-image, image-to-image, multimodal, image-generation docs.
     * Use the user's explicitly set path if available.
	 */
	private @Nullable String resolveImagePath(String model, InvokeMode invokeMode) {
        return this.imagesPath != null
                ? this.imagesPath
                : DashScopeImageApiConstants.getImagePath(model, invokeMode);
    }

    /**
	 * Resolves query task API path from model name.
     * Use the user's explicitly set path if available.
	 */
	private @Nullable String resolveQueryTaskPath(String model) {
        return this.queryTaskPath != null
                ? this.queryTaskPath
                : DashScopeImageApiConstants.getQueryTaskUrl(model);
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

        private String baseUrl = DEFAULT_BASE_URL;

        private ApiKey apiKey;

        private @Nullable String imagesPath;

        private @Nullable String queryTaskPath;

        private String workSpaceId;

        private RestClient.Builder restClientBuilder = RestClient.builder();

        private ResponseErrorHandler responseErrorHandler = RetryUtils.DEFAULT_RESPONSE_ERROR_HANDLER;

		public Builder() {
		}

		// Copy constructor for mutate()
		public Builder(DashScopeImageApi api) {
			this.baseUrl = api.getBaseUrl();
			this.apiKey = api.getApiKey();
            this.imagesPath = api.imagesPath;
            this.queryTaskPath = api.queryTaskPath;
			this.restClientBuilder = api.restClient != null ? api.restClient.mutate() : RestClient.builder();
			this.responseErrorHandler = api.getResponseErrorHandler();
		}

		public DashScopeImageApi.Builder baseUrl(String baseUrl) {

			Assert.notNull(baseUrl, "Base URL cannot be null");
			this.baseUrl = baseUrl;
			return this;
		}

		public DashScopeImageApi.Builder workSpaceId(String workSpaceId) {
			// Workspace ID is optional, but if provided, it must not be null.
			if (StringUtils.hasText(workSpaceId)) {
				Assert.notNull(workSpaceId, "Workspace ID cannot be null");
			}
			this.workSpaceId = workSpaceId;
			return this;
		}

		public DashScopeImageApi.Builder apiKey(String simpleApiKey) {
			Assert.notNull(simpleApiKey, "Simple api key cannot be null");
			this.apiKey = new SimpleApiKey(simpleApiKey);
			return this;
		}

        public DashScopeImageApi.Builder imagesPath(@Nullable String imagesPath) {
			this.imagesPath = imagesPath;
			return this;
		}

        public DashScopeImageApi.Builder queryTaskPath(@Nullable String queryTaskPath) {
			this.queryTaskPath = queryTaskPath;
			return this;
		}

		public DashScopeImageApi.Builder restClientBuilder(RestClient.Builder restClientBuilder) {
			Assert.notNull(restClientBuilder, "Rest client builder cannot be null");
			this.restClientBuilder = restClientBuilder;
			return this;
		}

		public DashScopeImageApi.Builder responseErrorHandler(ResponseErrorHandler responseErrorHandler) {
			Assert.notNull(responseErrorHandler, "Response error handler cannot be null");
			this.responseErrorHandler = responseErrorHandler;
			return this;
		}

		public DashScopeImageApi build() {

			Assert.notNull(apiKey, "API key cannot be null");

			return new DashScopeImageApi(this.baseUrl, this.apiKey, this.imagesPath, this.queryTaskPath,
                    this.workSpaceId, this.restClientBuilder, this.responseErrorHandler);
		}

	}

}
