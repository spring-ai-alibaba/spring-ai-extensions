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

package com.alibaba.cloud.ai.autoconfigure.dashscope.sdk;

import com.alibaba.cloud.ai.dashscope.sdk.embedding.DashScopeSdkEmbeddingModel;
import com.alibaba.dashscope.embeddings.TextEmbedding;
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
import org.springframework.context.annotation.Bean;
import org.springframework.retry.support.RetryTemplate;

import static com.alibaba.cloud.ai.autoconfigure.dashscope.sdk.DashScopeSdkConnectionUtils.resolveConnectionProperties;

/**
 * Auto configuration for DashScope SDK embedding model.
 */
@AutoConfiguration(after = SpringAiRetryAutoConfiguration.class)
@ConditionalOnDashScopeSdkEnabled
@ConditionalOnClass(TextEmbedding.class)
@ConditionalOnProperty(name = SpringAIModelProperties.TEXT_EMBEDDING_MODEL, havingValue = "dashscope-sdk", matchIfMissing = true)
@EnableConfigurationProperties({ DashScopeSdkConnectionProperties.class, DashScopeSdkEmbeddingProperties.class })
public class DashScopeSdkEmbeddingAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	@ConditionalOnProperty(prefix = DashScopeSdkEmbeddingProperties.CONFIG_PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
	public DashScopeSdkEmbeddingModel dashScopeSdkEmbeddingModel(ObjectProvider<RetryTemplate> retryTemplate,
			DashScopeSdkConnectionProperties commonProperties,
			DashScopeSdkEmbeddingProperties embeddingProperties,
			ObjectProvider<ObservationRegistry> observationRegistry,
			ObjectProvider<EmbeddingModelObservationConvention> observationConvention) {

		ResolvedConnectionProperties resolved = resolveConnectionProperties(commonProperties, embeddingProperties);

		DashScopeSdkEmbeddingModel embeddingModel = DashScopeSdkEmbeddingModel.builder()
			.apiKey(resolved.apiKey())
			.workspaceId(resolved.workspaceId())
			.connectionHeaders(resolved.headers())
			.defaultOptions(embeddingProperties.getOptions())
			.metadataMode(embeddingProperties.getMetadataMode())
			.retryTemplate(retryTemplate.getIfUnique(() -> RetryUtils.DEFAULT_RETRY_TEMPLATE))
			.observationRegistry(observationRegistry.getIfUnique(() -> ObservationRegistry.NOOP))
			.build();

		observationConvention.ifAvailable(embeddingModel::setObservationConvention);
		return embeddingModel;
	}

}
