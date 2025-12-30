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

import com.alibaba.cloud.ai.dashscope.agent.DashScopeAgent;
import com.alibaba.cloud.ai.dashscope.api.DashScopeAgentApi;

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
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;

import static com.alibaba.cloud.ai.autoconfigure.dashscope.DashScopeConnectionUtils.resolveConnectionProperties;

/**
 * Spring AI Alibaba DashScope Agent Auto Configuration.
 *
 * @author yuluo
 * @author <a href="mailto:yuluo08290126@gmail.com">yuluo</a>
 */

@AutoConfiguration(after = { RestClientAutoConfiguration.class, WebClientAutoConfiguration.class,
        SpringAiRetryAutoConfiguration.class })
@ConditionalOnDashScopeEnabled
@ConditionalOnClass(DashScopeAgentApi.class)
@ConditionalOnProperty(prefix = DashScopeAgentProperties.CONFIG_PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties({DashScopeConnectionProperties.class, DashScopeAgentProperties.class})
public class DashScopeAgentAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public DashScopeAgent dashScopeAgent(
            DashScopeConnectionProperties commonProperties,
            DashScopeAgentProperties agentProperties,
            ObjectProvider<RestClient.Builder> restClientBuilderProvider,
            ObjectProvider<WebClient.Builder> webClientBuilderProvider,
            ObjectProvider<ResponseErrorHandler> responseErrorHandler) {

        var resolved = resolveConnectionProperties(commonProperties, agentProperties, "agent");

        var dashScopeAgentApi = DashScopeAgentApi.builder()
                .baseUrl(resolved.baseUrl())
                .apiKey(resolved.apiKey())
                .workSpaceId(resolved.workspaceId())
                .agentPath(agentProperties.getAgentPath())
                .restClientBuilder(restClientBuilderProvider.getIfAvailable(RestClient::builder))
                .webClientBuilder(webClientBuilderProvider.getIfAvailable(WebClient::builder))
                .responseErrorHandler(responseErrorHandler.getIfAvailable(() -> RetryUtils.DEFAULT_RESPONSE_ERROR_HANDLER))
                .build();

        var dashScopeAgent = DashScopeAgent.builder()
                .dashScopeAgentApi(dashScopeAgentApi)
                .defaultOptions(agentProperties.getOptions())
                .build();

        return dashScopeAgent;
    }

}
