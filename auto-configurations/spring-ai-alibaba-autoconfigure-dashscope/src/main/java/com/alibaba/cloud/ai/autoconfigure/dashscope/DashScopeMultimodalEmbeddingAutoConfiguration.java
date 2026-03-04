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
package com.alibaba.cloud.ai.autoconfigure.dashscope;

import com.alibaba.cloud.ai.dashscope.api.DashScopeMultimodalEmbeddingApi;
import com.alibaba.cloud.ai.dashscope.embedding.multimodal.DashScopeMultimodalEmbeddingModel;
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
import org.springframework.boot.autoconfigure.web.client.RestClientAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClient;

import static com.alibaba.cloud.ai.autoconfigure.dashscope.DashScopeConnectionUtils.resolveConnectionProperties;

/**
 * Spring AI Alibaba DashScope Multimodal Embedding Auto Configuration.
 *
 * @author buvidk
 */
@AutoConfiguration(after = { RestClientAutoConfiguration.class, SpringAiRetryAutoConfiguration.class })
@ConditionalOnDashScopeEnabled
@ConditionalOnClass(DashScopeMultimodalEmbeddingApi.class)
@ConditionalOnProperty(name = SpringAIModelProperties.MULTI_MODAL_EMBEDDING_MODEL, havingValue = SpringAIAlibabaModels.DASHSCOPE,
		matchIfMissing = true)
@EnableConfigurationProperties({ DashScopeConnectionProperties.class, DashScopeMultimodalEmbeddingProperties.class })
public class DashScopeMultimodalEmbeddingAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public DashScopeMultimodalEmbeddingModel dashScopeMultimodalEmbeddingModel(
			DashScopeConnectionProperties commonProperties,
			DashScopeMultimodalEmbeddingProperties embeddingProperties,
			ObjectProvider<RestClient.Builder> restClientBuilderProvider,
			ObjectProvider<RetryTemplate> retryTemplate,
			ObjectProvider<ResponseErrorHandler> responseErrorHandler,
			ObjectProvider<ObservationRegistry> observationRegistry,
			ObjectProvider<EmbeddingModelObservationConvention> observationConvention) {

		ResolvedConnectionProperties resolved = resolveConnectionProperties(commonProperties, embeddingProperties, "multimodal");

		var dashScopeApi = DashScopeMultimodalEmbeddingApi.builder()
				.apiKey(resolved.apiKey())
				.baseUrl(resolved.baseUrl())
				.headers(resolved.headers())
				.multimodalPath(embeddingProperties.getMultimodalPath())
				.restClientBuilder(restClientBuilderProvider.getIfAvailable(RestClient::builder))
				.responseErrorHandler(responseErrorHandler.getIfAvailable(() -> RetryUtils.DEFAULT_RESPONSE_ERROR_HANDLER))
				.build();

		var embeddingModel = DashScopeMultimodalEmbeddingModel.builder()
				.dashScopeMultimodalEmbeddingApi(dashScopeApi)
				.defaultOptions(embeddingProperties.getOptions())
				.retryTemplate(retryTemplate.getIfUnique(() -> RetryUtils.DEFAULT_RETRY_TEMPLATE))
				.observationRegistry(observationRegistry.getIfUnique(() -> ObservationRegistry.NOOP))
				.build();

		observationConvention.ifAvailable(embeddingModel::setObservationConvention);

		return embeddingModel;
	}
}
