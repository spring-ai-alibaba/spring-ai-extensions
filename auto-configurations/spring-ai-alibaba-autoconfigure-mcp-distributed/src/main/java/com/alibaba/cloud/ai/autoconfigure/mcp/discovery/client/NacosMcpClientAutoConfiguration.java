/*
 * Copyright 2024-2025 the original author or authors.
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

package com.alibaba.cloud.ai.autoconfigure.mcp.discovery.client;

import com.alibaba.cloud.ai.mcp.discovery.client.transport.DistributedAsyncMcpClient;
import com.alibaba.cloud.ai.mcp.discovery.client.transport.DistributedSyncMcpClient;
import com.alibaba.cloud.ai.mcp.discovery.client.transport.sse.SseWebFluxDistributedAsyncMcpClient;
import com.alibaba.cloud.ai.mcp.discovery.client.transport.sse.SseWebFluxDistributedSyncMcpClient;
import com.alibaba.cloud.ai.mcp.nacos.service.NacosMcpOperationService;
import com.alibaba.cloud.ai.mcp.nacos.sse.NacosMcpSseClientProperties;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yingzi
 * @since 2025/10/25
 */
@AutoConfiguration(after = { NacosMcpAutoConfiguration.class })
@EnableConfigurationProperties({ NacosMcpSseClientProperties.class })
@ConditionalOnProperty(prefix = "spring.ai.alibaba.mcp.nacos.client", name = { "enabled" }, havingValue = "true",
        matchIfMissing = false)
public class NacosMcpClientAutoConfiguration {

    @Bean
    @ConditionalOnProperty(prefix = "spring.ai.mcp.client", name = { "type" }, havingValue = "SYNC",
            matchIfMissing = true)
    public List<DistributedSyncMcpClient> sseWebFluxDistributedSyncClients(
            ObjectProvider<NacosMcpOperationService> nacosMcpOperationServiceProvider,
            NacosMcpSseClientProperties nacosMcpSseClientProperties, ApplicationContext applicationContext) {
        NacosMcpOperationService nacosMcpOperationService = nacosMcpOperationServiceProvider.getObject();

        List<DistributedSyncMcpClient> clients = new ArrayList<>();

        for (NacosMcpSseClientProperties.NacosSseParameters nacosSseParameters : nacosMcpSseClientProperties.getConnections().values()) {
            SseWebFluxDistributedSyncMcpClient client = SseWebFluxDistributedSyncMcpClient.builder()
                    .serverName(nacosSseParameters.serviceName())
                    .version(nacosSseParameters.version())
                    .nacosMcpOperationService(nacosMcpOperationService)
                    .applicationContext(applicationContext)
                    .build();
            client.init();
            client.subscribe();

            clients.add(client);
        }
        return clients;
    }

    @Bean
    @ConditionalOnProperty(prefix = "spring.ai.mcp.client", name = { "type" }, havingValue = "ASYNC",
            matchIfMissing = true)
    public List<DistributedAsyncMcpClient> sseWebFluxDistributedAsyncClients(
            ObjectProvider<NacosMcpOperationService> nacosMcpOperationServiceProvider,
            NacosMcpSseClientProperties nacosMcpSseClientProperties, ApplicationContext applicationContext) {
        NacosMcpOperationService nacosMcpOperationService = nacosMcpOperationServiceProvider.getObject();

        List<DistributedAsyncMcpClient> clients = new ArrayList<>();

        for (NacosMcpSseClientProperties.NacosSseParameters nacosSseParameters : nacosMcpSseClientProperties.getConnections().values()) {
            SseWebFluxDistributedAsyncMcpClient client = SseWebFluxDistributedAsyncMcpClient.builder()
                    .serverName(nacosSseParameters.serviceName())
                    .version(nacosSseParameters.version())
                    .nacosMcpOperationService(nacosMcpOperationService)
                    .applicationContext(applicationContext)
                    .build();
            client.init();
            client.subscribe();

            clients.add(client);
        }
        return clients;
    }
}
