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

package com.alibaba.cloud.ai.autoconfigure.mcp.discovery.client;

import com.alibaba.cloud.ai.mcp.discovery.client.transport.DistributedAsyncMcpClient;
import com.alibaba.cloud.ai.mcp.discovery.client.transport.DistributedSyncMcpClient;
import com.alibaba.cloud.ai.mcp.discovery.client.transport.sse.SseWebFluxDistributedAsyncMcpClient;
import com.alibaba.cloud.ai.mcp.discovery.client.transport.sse.SseWebFluxDistributedSyncMcpClient;
import com.alibaba.cloud.ai.mcp.nacos.NacosMcpClientProperties;
import com.alibaba.cloud.ai.mcp.nacos.service.NacosMcpOperationService;
import com.alibaba.cloud.ai.mcp.nacos.NacosMcpSseClientProperties;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author yingzi
 * @since 2025/10/28
 */
@AutoConfiguration(after = { NacosMcpAutoConfiguration.class })
@EnableConfigurationProperties({ NacosMcpSseClientProperties.class, NacosMcpClientProperties.class})
@ConditionalOnProperty(prefix = "spring.ai.alibaba.mcp.nacos.client", name = { "enabled" }, havingValue = "true",
        matchIfMissing = false)
public class NacosMcpSseClientAutoConfiguration {

    @Bean
    @ConditionalOnProperty(prefix = "spring.ai.mcp.client", name = { "type" }, havingValue = "SYNC",
            matchIfMissing = true)
    public List<DistributedSyncMcpClient> sseWebFluxDistributedSyncClients(
            ObjectProvider<Map<String, NacosMcpOperationService>> nacosMcpOperationServiceMapProvider,
            NacosMcpSseClientProperties nacosMcpSseClientProperties, ApplicationContext applicationContext,
            NacosMcpClientProperties nacosMcpClientProperties) {
        Map<String, NacosMcpOperationService> nacosMcpOperationServiceMap = nacosMcpOperationServiceMapProvider.getObject();
        List<DistributedSyncMcpClient> clients = new ArrayList<>();

        nacosMcpSseClientProperties.getConnections().forEach((name, nacosSseParameters) -> {
            SseWebFluxDistributedSyncMcpClient client = SseWebFluxDistributedSyncMcpClient.builder()
                    .serverName(nacosSseParameters.serviceName())
                    .version(nacosSseParameters.version())
                    .nacosMcpOperationService(nacosMcpOperationServiceMap.get(name))
                    .applicationContext(applicationContext)
                    .lazyInit(nacosMcpClientProperties.isLazyInit())
                    .build();
            client.init();
            client.subscribe();
            clients.add(client);
        });
        return clients;
    }

    @Bean
    @ConditionalOnProperty(prefix = "spring.ai.mcp.client", name = { "type" }, havingValue = "ASYNC",
            matchIfMissing = true)
    public List<DistributedAsyncMcpClient> sseWebFluxDistributedAsyncClients(
            ObjectProvider<Map<String, NacosMcpOperationService>> nacosMcpOperationServiceMapProvider,
            NacosMcpSseClientProperties nacosMcpSseClientProperties, ApplicationContext applicationContext,
            NacosMcpClientProperties nacosMcpClientProperties) {
        Map<String, NacosMcpOperationService> nacosMcpOperationServiceMap = nacosMcpOperationServiceMapProvider.getObject();
        List<DistributedAsyncMcpClient> clients = new ArrayList<>();

        nacosMcpSseClientProperties.getConnections().forEach((name, nacosSseParameters) -> {
            SseWebFluxDistributedAsyncMcpClient client = SseWebFluxDistributedAsyncMcpClient.builder()
                    .serverName(nacosSseParameters.serviceName())
                    .version(nacosSseParameters.version())
                    .nacosMcpOperationService(nacosMcpOperationServiceMap.get(name))
                    .applicationContext(applicationContext)
                    .lazyInit(nacosMcpClientProperties.isLazyInit())
                    .build();
            client.init();
            client.subscribe();
            clients.add(client);
        });
        return clients;
    }
}
