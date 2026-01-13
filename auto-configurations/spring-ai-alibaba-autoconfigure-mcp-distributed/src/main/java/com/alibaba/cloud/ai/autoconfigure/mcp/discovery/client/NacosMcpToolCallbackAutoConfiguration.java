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

import com.alibaba.cloud.ai.mcp.discovery.client.tool.DistributedAsyncMcpToolCallbackProvider;
import com.alibaba.cloud.ai.mcp.discovery.client.tool.DistributedSyncMcpToolCallbackProvider;
import com.alibaba.cloud.ai.mcp.discovery.client.transport.DistributedAsyncMcpClient;
import com.alibaba.cloud.ai.mcp.discovery.client.transport.DistributedSyncMcpClient;
import com.alibaba.cloud.ai.mcp.nacos.NacosMcpProperties;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.util.List;

/**
 * @author yingzi
 * @since 2025/10/25
 */
@AutoConfiguration(after = NacosMcpAutoConfiguration.class)
@EnableConfigurationProperties({ NacosMcpProperties.class })
public class NacosMcpToolCallbackAutoConfiguration {

    @Bean(name = "distributedSyncToolCallback")
    @ConditionalOnProperty(prefix = "spring.ai.mcp.client", name = { "type" }, havingValue = "SYNC",
            matchIfMissing = true)
    public ToolCallbackProvider distributedSyncToolCallback(ObjectProvider<List<DistributedSyncMcpClient>> distributedSyncMcpClients) {
        List<DistributedSyncMcpClient> mcpClients = distributedSyncMcpClients.stream()
                .flatMap(List::stream)
                .toList();
        return new DistributedSyncMcpToolCallbackProvider(mcpClients);
    }

    @Bean(name = "distributedAsyncToolCallback")
    @ConditionalOnProperty(prefix = "spring.ai.mcp.client", name = { "type" }, havingValue = "ASYNC",
    matchIfMissing = false)
    public ToolCallbackProvider distributedAsyncToolCallback(ObjectProvider<List<DistributedAsyncMcpClient>> distributedAsyncMcpClients) {
        List<DistributedAsyncMcpClient> mcpClients = distributedAsyncMcpClients.stream()
                .flatMap(List::stream)
                .toList();
        return new DistributedAsyncMcpToolCallbackProvider(mcpClients);
    }
}
