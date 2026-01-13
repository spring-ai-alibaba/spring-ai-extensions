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

package com.alibaba.cloud.ai.mcp.discovery.client.tool;

import com.alibaba.cloud.ai.mcp.discovery.client.transport.DistributedSyncMcpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.spec.McpSchema;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.support.ToolUtils;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;

/**
 * @author yingzi
 * @since 2025/10/25
 */

public class DistributedSyncMcpToolCallbackProvider implements ToolCallbackProvider {

    private final List<DistributedSyncMcpClient> mcpClients;

    private final BiPredicate<McpSyncClient, McpSchema.Tool> toolFilter;

    public DistributedSyncMcpToolCallbackProvider(BiPredicate<McpSyncClient, McpSchema.Tool> toolFilter, List<DistributedSyncMcpClient> mcpClients) {
        Assert.notNull(mcpClients, "mcpClients cannot be null");
        Assert.notNull(toolFilter, "toolFilter cannot be null");
        this.mcpClients = mcpClients;
        this.toolFilter = toolFilter;
    }

    public DistributedSyncMcpToolCallbackProvider(List<DistributedSyncMcpClient> mcpClients) {
        this((mcpClient, tool) -> true, mcpClients);
    }

    @Override
    public ToolCallback[] getToolCallbacks() {
        ArrayList<Object> toolCallbacks = new ArrayList();

        this.mcpClients.stream().forEach(
                mcpClint -> {
                    toolCallbacks.addAll(mcpClint.listTools().tools().stream().filter((tool) -> {
                        return this.toolFilter.test(mcpClint.getMcpSyncClient(), tool);
                    }).map((tool) -> {
                        return new DistributedSyncMcpToolCallback(mcpClint, tool);
                    }).toList());                }
        );
        ToolCallback[] array = (ToolCallback[]) toolCallbacks.toArray(new ToolCallback[0]);
        this.validateToolCallbacks(array);
        return array;
    }

    private void validateToolCallbacks(ToolCallback[] toolCallbacks) {
        List<String> duplicateToolNames = ToolUtils.getDuplicateToolNames(toolCallbacks);
        if (!duplicateToolNames.isEmpty()) {
            throw new IllegalStateException(
                    "Multiple tools with the same name (%s)".formatted(String.join(", ", duplicateToolNames)));
        }
    }
}
