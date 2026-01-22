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

import com.alibaba.cloud.ai.dashscope.spec.DashScopeApiSpec;
import com.alibaba.cloud.ai.dashscope.spec.DashScopeApiSpec.DashScopeImageGenerationRequest;
import com.alibaba.cloud.ai.dashscope.spec.DashScopeApiSpec.DashScopeImageGenerationRequest.DashScopeImageGenerationRequestInput;
import com.alibaba.cloud.ai.dashscope.spec.DashScopeApiSpec.DashScopeImageGenerationRequest.DashScopeImageGenerationRequestInputMessage;
import com.alibaba.cloud.ai.dashscope.spec.DashScopeApiSpec.DashScopeImageGenerationRequest.DashScopeImageGenerationRequestInputMessageContent;
import com.alibaba.cloud.ai.dashscope.spec.DashScopeApiSpec.DashScopeImageRequest;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.model.ApiKey;
import org.springframework.ai.model.SimpleApiKey;
import org.springframework.ai.retry.RetryUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;

import static com.alibaba.cloud.ai.dashscope.common.DashScopeApiConstants.DEFAULT_BASE_URL;
import static com.alibaba.cloud.ai.dashscope.common.DashScopeApiConstants.ENABLED;
import static com.alibaba.cloud.ai.dashscope.common.DashScopeApiConstants.HEADER_ASYNC;
import static com.alibaba.cloud.ai.dashscope.common.DashScopeApiConstants.IMAGE2IMAGE_RESTFUL_URL;
import static com.alibaba.cloud.ai.dashscope.common.DashScopeApiConstants.IMAGE_GENERATION_RESTFUL_URL;
import static com.alibaba.cloud.ai.dashscope.common.DashScopeApiConstants.MULTIMODAL_GENERATION_RESTFUL_URL;
import static com.alibaba.cloud.ai.dashscope.common.DashScopeApiConstants.QUERY_TASK_RESTFUL_URL;
import static com.alibaba.cloud.ai.dashscope.common.DashScopeApiConstants.TEXT2IMAGE_RESTFUL_URL;
import static com.alibaba.cloud.ai.dashscope.spec.DashScopeModel.ImageModel.QWEN_IMAGE;
import static com.alibaba.cloud.ai.dashscope.spec.DashScopeModel.ImageModel.QWEN_MT_IMAGE;
import static com.alibaba.cloud.ai.dashscope.spec.DashScopeModel.ImageModel.WAN_2_6_IMAGE;

/**
 * @author nuocheng.lxm
 * @author yuluo-yx
 * @author Soryu
 */

public class DashScopeImageApi {

    private static final Logger logger = LoggerFactory.getLogger(DashScopeImageApi.class);

	private final String baseUrl;

	private final ApiKey apiKey;

    private final String imagesPath;

    private final String queryTaskPath;

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
	public DashScopeImageApi(String baseUrl, ApiKey apiKey, String imagesPath, String queryTaskPath, String workSpaceId,
                             RestClient.Builder restClientBuilder, ResponseErrorHandler responseErrorHandler) {

		this.baseUrl = baseUrl;
		this.apiKey = apiKey;
        this.imagesPath = imagesPath;
        this.queryTaskPath = queryTaskPath;
        this.responseErrorHandler = responseErrorHandler;

		Assert.notNull(apiKey, "ApiKey must not be null");
		Assert.notNull(baseUrl, "Base URL must not be null");
		Assert.notNull(restClientBuilder, "RestClientBuilder must not be null");

		this.restClient = restClientBuilder.baseUrl(baseUrl)
			.defaultHeaders(ApiUtils.getJsonContentHeaders(apiKey.getValue(), workSpaceId))
			.defaultStatusHandler(responseErrorHandler)
			.build();
	}

	public ResponseEntity<DashScopeApiSpec.DashScopeImageAsyncResponse> submitImageGenTask(DashScopeApiSpec.DashScopeImageRequest request) {

		String model = request.model();
        String imagesUri = this.imagesPath;
        Object requestBody = request;

        if (model.startsWith("qwen-image") || model.startsWith("z-image")) {
			imagesUri = MULTIMODAL_GENERATION_RESTFUL_URL;
        }else if(model.equals(WAN_2_6_IMAGE.getValue())) {
            imagesUri = IMAGE_GENERATION_RESTFUL_URL;
            requestBody = convertToImageGenerationRequest(request);
        } else if (model.equals(QWEN_MT_IMAGE.getValue()) || model.contains("edit")) {
			imagesUri = IMAGE2IMAGE_RESTFUL_URL;
        }

		return this.restClient.post()
			.uri(imagesUri)
			.header(HEADER_ASYNC, ENABLED)
			.body(requestBody)
			.retrieve()
			.toEntity(DashScopeApiSpec.DashScopeImageAsyncResponse.class);
	}

    private DashScopeImageGenerationRequest convertToImageGenerationRequest(DashScopeImageRequest request) {
        List<DashScopeImageGenerationRequestInputMessageContent> content = getDashScopeImageGenerationRequestInputMessageContents(request);
        List<DashScopeImageGenerationRequestInputMessage> imageGenerationRequestInputMessages = new ArrayList<>();
        imageGenerationRequestInputMessages.add(new DashScopeImageGenerationRequestInputMessage("user",content));
        return new DashScopeApiSpec.DashScopeImageGenerationRequest(
                request.model(),
                new DashScopeImageGenerationRequestInput(imageGenerationRequestInputMessages),
                new DashScopeApiSpec.DashScopeImageGenerationRequest.DashScopeImageGenerationRequestParameter(
                        request.parameters().negativePrompt(),
                        request.parameters().size(),
                        request.parameters().enableInterleave(),
                        request.parameters().n(),
                        request.parameters().maxImages(),
                        request.parameters().seed(),
                        request.parameters().promptExtend(),
                        request.parameters().watermark()
                ));
    }

    @NonNull
    private List<DashScopeImageGenerationRequestInputMessageContent> getDashScopeImageGenerationRequestInputMessageContents(
            DashScopeImageRequest request) {
        String prompt = request.input().prompt();
        String baseImageUrl = request.input().baseImageUrl();
        List<DashScopeImageGenerationRequestInputMessageContent> content = new ArrayList<>();

        if (prompt != null && !prompt.isEmpty()){
            DashScopeImageGenerationRequestInputMessageContent promptContent = new DashScopeImageGenerationRequestInputMessageContent(prompt,null);
            content.add(promptContent);
        }
        if (baseImageUrl != null && !baseImageUrl.isEmpty()){
            DashScopeImageGenerationRequestInputMessageContent imageContent = new DashScopeImageGenerationRequestInputMessageContent(
                    null, baseImageUrl);
            content.add(imageContent);
        }
        return content;
    }

	public ResponseEntity<DashScopeApiSpec.DashScopeImageAsyncResponse> getImageGenTaskResult(String taskId) {
		return this.restClient.get()
			.uri(this.queryTaskPath, taskId)
			.retrieve()
			.toEntity(DashScopeApiSpec.DashScopeImageAsyncResponse.class);
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

        private String imagesPath = TEXT2IMAGE_RESTFUL_URL;

        private String queryTaskPath = QUERY_TASK_RESTFUL_URL;

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

        public DashScopeImageApi.Builder imagesPath(String imagesPath) {
			Assert.notNull(imagesPath, "Images path cannot be null");
			this.imagesPath = imagesPath;
			return this;
		}

        public DashScopeImageApi.Builder queryTaskPath(String queryTaskPath) {
			Assert.notNull(queryTaskPath, "Query task path cannot be null");
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
