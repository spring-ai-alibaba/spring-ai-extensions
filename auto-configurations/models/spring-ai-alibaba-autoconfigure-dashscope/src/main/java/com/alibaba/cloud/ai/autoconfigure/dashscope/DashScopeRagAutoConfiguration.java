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

import java.net.URI;
import java.util.Objects;

import com.alibaba.cloud.ai.dashscope.rag.DashScopeRagApi;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;

@AutoConfiguration
@ConditionalOnDashScopeEnabled
@ConditionalOnProperty(prefix = "spring.ai.dashscope.rag", name = "enabled", havingValue = "true")
@EnableConfigurationProperties(DashScopeConnectionProperties.class)
public class DashScopeRagAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	DashScopeRagApi dashScopeRagApi(DashScopeConnectionProperties properties,
			@Value("${spring.ai.dashscope.rag.base-url:}") String baseUrl,
			ObjectProvider<RestClient.Builder> restClient, ObjectProvider<WebClient.Builder> webClient) {
		String apiKey = properties.getApiKey();
		Assert.hasText(apiKey, "spring.ai.dashscope.api-key is required for RAG");
		URI gateway = StringUtils.hasText(baseUrl) ? URI.create(baseUrl)
				: DashScopeRagApi.workspaceUrl(Objects.requireNonNull(properties.getWorkspaceId(),
						"spring.ai.dashscope.workspace-id is required for RAG"));
		return new DashScopeRagApi(gateway, Objects.requireNonNull(apiKey),
				restClient.getIfAvailable(RestClient::builder), webClient.getIfAvailable(WebClient::builder),
				RestClient.create());
	}

}
