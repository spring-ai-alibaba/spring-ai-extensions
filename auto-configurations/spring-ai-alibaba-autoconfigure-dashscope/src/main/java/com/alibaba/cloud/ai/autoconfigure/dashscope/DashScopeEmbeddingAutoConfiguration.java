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

package com.alibaba.cloud.ai.autoconfigure.dashscope;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.embedding.DashScopeEmbeddingModel;
import com.alibaba.cloud.ai.model.SpringAIAlibabaModels;
import io.micrometer.observation.ObservationRegistry;

import org.springframework.ai.embedding.observation.EmbeddingModelObservationConvention;
import org.springframework.ai.model.SpringAIModelProperties;
import org.springframework.ai.retry.RetryUtils;
import org.springframework.ai.retry.autoconfigure.SpringAiRetryAutoConfiguration;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.restclient.autoconfigure.RestClientAutoConfiguration;
import org.springframework.boot.webclient.autoconfigure.WebClientAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.retry.RetryTemplate;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;

import static com.alibaba.cloud.ai.autoconfigure.dashscope.DashScopeConnectionUtils.resolveConnectionProperties;

/**
 * Spring AI Alibaba DashScope Embedding Auto Configuration.
 *
 * @author yuluo
 * @author <a href="mailto:yuluo08290126@gmail.com">yuluo</a>
 */

@AutoConfiguration(after = { RestClientAutoConfiguration.class, WebClientAutoConfiguration.class,
		SpringAiRetryAutoConfiguration.class })
@ConditionalOnDashScopeEnabled
@ConditionalOnClass(DashScopeApi.class)
@ConditionalOnProperty(name = SpringAIModelProperties.EMBEDDING_MODEL, havingValue = SpringAIAlibabaModels.DASHSCOPE,
		matchIfMissing = true)
@EnableConfigurationProperties({ DashScopeConnectionProperties.class, DashScopeEmbeddingProperties.class })
public class DashScopeEmbeddingAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public DashScopeEmbeddingModel dashscopeEmbeddingModel(
            DashScopeConnectionProperties commonProperties,
			DashScopeEmbeddingProperties embeddingProperties,
			ObjectProvider<WebClient.Builder> webClientBuilderProvider,
			ObjectProvider<RestClient.Builder> restClientBuilderProvider,
            ObjectProvider<RetryTemplate> retryTemplate,
			ObjectProvider<ResponseErrorHandler> responseErrorHandler,
			ObjectProvider<ObservationRegistry> observationRegistry,
			ObjectProvider<EmbeddingModelObservationConvention> observationConvention) {

		var dashScopeApi = dashscopeEmbeddingApi(commonProperties, embeddingProperties,
				restClientBuilderProvider.getIfAvailable(RestClient::builder),
				webClientBuilderProvider.getIfAvailable(WebClient::builder),
				responseErrorHandler.getIfAvailable(() -> RetryUtils.DEFAULT_RESPONSE_ERROR_HANDLER));

		var embeddingModel = DashScopeEmbeddingModel.builder()
                .dashScopeApi(dashScopeApi)
                .metadataMode(embeddingProperties.getMetadataMode())
                .defaultOptions(embeddingProperties.getOptions())
                .retryTemplate(retryTemplate.getIfUnique(() -> RetryUtils.DEFAULT_RETRY_TEMPLATE))
                .observationRegistry(observationRegistry.getIfUnique(() -> ObservationRegistry.NOOP))
                .build();

		observationConvention.ifAvailable(embeddingModel::setObservationConvention);

		return embeddingModel;
	}

	private DashScopeApi dashscopeEmbeddingApi(DashScopeConnectionProperties commonProperties,
			DashScopeEmbeddingProperties embeddingProperties, RestClient.Builder restClientBuilder,
			WebClient.Builder webClientBuilder, ResponseErrorHandler responseErrorHandler) {
		ResolvedConnectionProperties resolved = resolveConnectionProperties(commonProperties, embeddingProperties,
				"embedding");

		return DashScopeApi.builder()
			.apiKey(resolved.apiKey())
            .workSpaceId(resolved.workspaceId())
			.headers(resolved.headers())
			.baseUrl(resolved.baseUrl())
			.embeddingsPath(embeddingProperties.getEmbeddingsPath())
			.webClientBuilder(webClientBuilder)
			.restClientBuilder(restClientBuilder)
			.responseErrorHandler(responseErrorHandler)
			.build();
	}

}
