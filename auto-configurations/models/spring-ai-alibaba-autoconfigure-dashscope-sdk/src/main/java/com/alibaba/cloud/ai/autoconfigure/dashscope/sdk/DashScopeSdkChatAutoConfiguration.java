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

import com.alibaba.cloud.ai.dashscope.sdk.chat.DashScopeSdkChatModel;
import com.alibaba.dashscope.aigc.generation.Generation;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.ai.chat.observation.ChatModelObservationConvention;
import org.springframework.ai.model.SpringAIModelProperties;
import org.springframework.ai.model.tool.DefaultToolExecutionEligibilityPredicate;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.model.tool.ToolExecutionEligibilityPredicate;
import org.springframework.ai.model.tool.autoconfigure.ToolCallingAutoConfiguration;
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
 * Auto configuration for DashScope SDK chat model.
 */
@AutoConfiguration(after = { SpringAiRetryAutoConfiguration.class, ToolCallingAutoConfiguration.class })
@ConditionalOnDashScopeSdkEnabled
@ConditionalOnClass(Generation.class)
@ConditionalOnProperty(name = SpringAIModelProperties.CHAT_MODEL, havingValue = "dashscope-sdk", matchIfMissing = true)
@EnableConfigurationProperties({ DashScopeSdkConnectionProperties.class, DashScopeSdkChatProperties.class })
public class DashScopeSdkChatAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	@ConditionalOnProperty(prefix = DashScopeSdkChatProperties.CONFIG_PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
	public DashScopeSdkChatModel dashScopeSdkChatModel(ObjectProvider<RetryTemplate> retryTemplate,
			ToolCallingManager toolCallingManager,
			DashScopeSdkConnectionProperties commonProperties,
			DashScopeSdkChatProperties chatProperties,
			ObjectProvider<ObservationRegistry> observationRegistry,
			ObjectProvider<ChatModelObservationConvention> observationConvention,
			ObjectProvider<ToolExecutionEligibilityPredicate> toolExecutionEligibilityPredicate) {

		ResolvedConnectionProperties resolved = resolveConnectionProperties(commonProperties, chatProperties);

		DashScopeSdkChatModel model = DashScopeSdkChatModel.builder()
			.apiKey(resolved.apiKey())
			.workspaceId(resolved.workspaceId())
			.connectionHeaders(resolved.headers())
			.defaultOptions(chatProperties.getOptions())
			.retryTemplate(retryTemplate.getIfUnique(() -> RetryUtils.DEFAULT_RETRY_TEMPLATE))
			.toolCallingManager(toolCallingManager)
			.observationRegistry(observationRegistry.getIfUnique(() -> ObservationRegistry.NOOP))
			.toolExecutionEligibilityPredicate(toolExecutionEligibilityPredicate
				.getIfUnique(DefaultToolExecutionEligibilityPredicate::new))
			.build();

		observationConvention.ifAvailable(model::setObservationConvention);
		return model;
	}

}
