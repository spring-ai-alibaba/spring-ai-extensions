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

import com.alibaba.cloud.ai.dashscope.agent.DashScopeAgentFlowStreamMode;
import com.alibaba.cloud.ai.dashscope.common.DashScopeException;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.ai.model.ApiKey;
import org.springframework.ai.model.SimpleApiKey;
import org.springframework.ai.retry.RetryUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import static com.alibaba.cloud.ai.dashscope.common.DashScopeApiConstants.APPS_COMPLETION_RESTFUL_URL;
import static com.alibaba.cloud.ai.dashscope.common.DashScopeApiConstants.DEFAULT_BASE_URL;

import java.util.List;
import java.util.Objects;

/**
 * @author linkesheng.lks
 * @author guanxu
 * @since 1.0.0-M2
 */
public class DashScopeAgentApi {

    private final String baseUrl;

    private final ApiKey apiKey;

    private final String workSpaceId;

    private final String agentPath;

    private final RestClient restClient;

    private final WebClient webClient;

    private final ResponseErrorHandler responseErrorHandler;

    public DashScopeAgentApi(
            String baseUrl,
            ApiKey apiKey,
            String workSpaceId,
            String agentPath,
            RestClient.Builder restClientBuilder,
            WebClient.Builder webClientBuilder,
            ResponseErrorHandler responseErrorHandler) {
        Assert.hasText(baseUrl, "Base URL cannot be null");
        Assert.notNull(apiKey, "API key cannot be null");
        Assert.hasText(agentPath, "Agent path cannot be null");

        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
        this.workSpaceId = workSpaceId;
        this.agentPath = agentPath;
        this.responseErrorHandler = responseErrorHandler;

        this.restClient = restClientBuilder.baseUrl(baseUrl)
                .defaultHeaders(ApiUtils.getJsonContentHeaders(apiKey.getValue(), workSpaceId))
                .defaultStatusHandler(responseErrorHandler)
                .build();

        this.webClient = webClientBuilder.baseUrl(baseUrl)
                .defaultHeaders(ApiUtils.getJsonContentHeaders(apiKey.getValue(), workSpaceId, true))
                .build();
    }

    public ResponseEntity<DashScopeAgentResponse> call(DashScopeAgentRequest request) {
        return restClient.post()
                .uri(this.agentPath, request.appId())
                .body(request)
                .retrieve()
                .toEntity(DashScopeAgentResponse.class);
    }

    public Flux<DashScopeAgentResponse> stream(DashScopeAgentRequest request) {
        return webClient.post()
                .uri(this.agentPath, request.appId())
                .body(Mono.just(request), DashScopeAgentRequest.class)
                .retrieve()
                .bodyToFlux(DashScopeAgentResponse.class)
                .filter(Objects::nonNull)
                .map(response -> {
                    if (response.code() != null && !response.code().isBlank()) {
                        throw new DashScopeException(String.format("[%s] %s (requestId: %s)", response.code(), response.message(), response.requestId()));
                    }
                    return response;
                })
                .subscribeOn(Schedulers.boundedElastic());
    }

    // @formatter:off
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public record DashScopeAgentRequest(
			@JsonProperty("app_id") String appId,
			@JsonProperty("input") DashScopeAgentRequestInput input,
			@JsonProperty("parameters") DashScopeAgentRequestParameters parameters) {
		@JsonInclude(JsonInclude.Include.NON_NULL)
		public record DashScopeAgentRequestInput(
				@JsonProperty("prompt") String prompt,
				@JsonProperty("messages") List<DashScopeAgentRequestMessage> messages,
				@JsonProperty("session_id") String sessionId,
				@JsonProperty("memory_id") String memoryId,
				@JsonProperty("image_list") List<String> images,
				@JsonProperty("file_list") List<String> files,
				@JsonProperty("biz_params") JsonNode bizParams) {
			@JsonInclude(JsonInclude.Include.NON_NULL)
			public record DashScopeAgentRequestMessage(
					@JsonProperty("role") String role,
					@JsonProperty("content") String content) {
			}
		}

		@JsonInclude(JsonInclude.Include.NON_NULL)
		public record DashScopeAgentRequestParameters(
				@JsonProperty("flow_stream_mode") DashScopeAgentFlowStreamMode flowStreamMode,
				@JsonProperty("has_thoughts") Boolean hasThoughts,
				@JsonProperty("enable_thinking") Boolean enableThinking,
				@JsonProperty("incremental_output") Boolean incrementalOutput,
                @JsonProperty("model_id") String modelId,
				@JsonProperty("rag_options") DashScopeAgentRequestRagOptions ragOptions
		) {
			@JsonInclude(JsonInclude.Include.NON_NULL)
			public record DashScopeAgentRequestRagOptions(
					@JsonProperty("pipeline_ids") List<String> pipelineIds,
					@JsonProperty("file_ids") List<String> fileIds,
					@JsonProperty("metadata_filter") JsonNode metadataFilter,
					@JsonProperty("tags") List<String> tags,
					@JsonProperty("structured_filter") JsonNode structuredFilter,
					@JsonProperty("session_file_ids") List<String> sessionFileIds) {
			}
		}
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public record DashScopeAgentResponse(
			@JsonProperty("status_code") Integer statusCode,
			@JsonProperty("request_id") String requestId,
			@JsonProperty("code") String code,
			@JsonProperty("message") String message,
			@JsonProperty("output") DashScopeAgentResponseOutput output,
			@JsonProperty("usage") DashScopeAgentResponseUsage usage) {
		@JsonInclude(JsonInclude.Include.NON_NULL)
		public record DashScopeAgentResponseOutput(
				@JsonProperty("text") String text,
				@JsonProperty("finish_reason") String finishReason,
				@JsonProperty("session_id") String sessionId,
				@JsonProperty("thoughts") List<DashScopeAgentResponseOutputThoughts> thoughts,
				@JsonProperty("doc_references") List<DashScopeAgentResponseOutputDocReference> docReferences) {
			@JsonInclude(JsonInclude.Include.NON_NULL)
			public record DashScopeAgentResponseOutputThoughts(
					@JsonProperty("thought") String thought,
					@JsonProperty("action_type") String actionType,
					@JsonProperty("action_name") String actionName,
					@JsonProperty("action") String action,
					@JsonProperty("action_input_stream") String actionInputStream,
					@JsonProperty("action_input") String actionInput,
					@JsonProperty("response") String response,
					@JsonProperty("observation") String observation,
					@JsonProperty("reasoning_content") String reasoningContent) {
			}

			@JsonInclude(JsonInclude.Include.NON_NULL)
			public record DashScopeAgentResponseOutputDocReference(
					@JsonProperty("index_id") String indexId,
					@JsonProperty("title") String title,
					@JsonProperty("doc_id") String docId,
					@JsonProperty("doc_name") String docName,
					@JsonProperty("text") String text,
					@JsonProperty("images") List<String> images,
					@JsonProperty("page_number") List<Integer> pageNumber) {
			}
		}

		@JsonInclude(JsonInclude.Include.NON_NULL)
		public record DashScopeAgentResponseUsage(
				@JsonProperty("models") List<DashScopeAgentResponseUsageModels> models) {
			@JsonInclude(JsonInclude.Include.NON_NULL)
			public record DashScopeAgentResponseUsageModels(
					@JsonProperty("model_id") String modelId,
					@JsonProperty("input_tokens") Integer inputTokens,
					@JsonProperty("output_tokens") Integer outputTokens) {
			}
		}
	}

    @Override
    public DashScopeAgentApi clone(){
        return mutate().build();
    }

    public Builder mutate() {
        return new Builder(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String baseUrl = DEFAULT_BASE_URL;

        private ApiKey apiKey;

        private String workSpaceId;

        private String agentPath = APPS_COMPLETION_RESTFUL_URL;

        private RestClient.Builder restClientBuilder = RestClient.builder();

        private WebClient.Builder webClientBuilder = WebClient.builder();

        private ResponseErrorHandler responseErrorHandler = RetryUtils.DEFAULT_RESPONSE_ERROR_HANDLER;

        public Builder() {
        }

        public Builder(DashScopeAgentApi api) {
            this.baseUrl = api.baseUrl;
            this.apiKey = api.apiKey;
            this.workSpaceId = api.workSpaceId;
            this.agentPath = api.agentPath;
            this.restClientBuilder = api.restClient != null ? api.restClient.mutate() : RestClient.builder();
            this.webClientBuilder = api.webClient != null ? api.webClient.mutate() : WebClient.builder();
            this.responseErrorHandler = api.responseErrorHandler;
        }

        public Builder baseUrl(String baseUrl) {
            Assert.hasText(baseUrl, "Base URL cannot be null");
            this.baseUrl = baseUrl;
            return this;
        }

        public Builder apiKey(String apiKey) {
            Assert.hasText(apiKey, "API key cannot be null");
            this.apiKey = new SimpleApiKey(apiKey);
            return this;
        }

        public Builder workSpaceId(String workSpaceId) {
            this.workSpaceId = workSpaceId;
            return this;
        }

        public Builder agentPath(String agentPath) {
            Assert.hasText(agentPath, "Agent path cannot be null");
            this.agentPath = agentPath;
            return this;
        }

        public Builder restClientBuilder(RestClient.Builder restClientBuilder) {
            Assert.notNull(restClientBuilder, "RestClient builder cannot be null");
            this.restClientBuilder = restClientBuilder;
            return this;
        }

        public Builder webClientBuilder(WebClient.Builder webClientBuilder) {
            Assert.notNull(webClientBuilder, "WebClient builder cannot be null");
            this.webClientBuilder = webClientBuilder;
            return this;
        }

        public Builder responseErrorHandler(ResponseErrorHandler responseErrorHandler) {
            Assert.notNull(responseErrorHandler, "ResponseErrorHandler cannot be null");
            this.responseErrorHandler = responseErrorHandler;
            return this;
        }

        public DashScopeAgentApi build() {
            return new DashScopeAgentApi(this.baseUrl, this.apiKey, this.workSpaceId, this.agentPath,
                    this.restClientBuilder, this.webClientBuilder, this.responseErrorHandler);
        }
    }
}
