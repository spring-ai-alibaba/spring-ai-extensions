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

import java.util.Map;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

import com.alibaba.cloud.ai.mcp.discovery.client.transport.DistributedSyncMcpClient;
import io.modelcontextprotocol.spec.McpSchema;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.mcp.McpToolUtils;
import org.springframework.ai.model.ModelOptionsUtils;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

/**
 * @author yingzi
 * @since 2025/10/25
 */
public class DistributedSyncMcpToolCallback implements ToolCallback {

    private final DistributedSyncMcpClient distributedSyncMcpClient;

    private final McpSchema.Tool tool;

    private final BiPredicate<String, Object> contextFilter;
    private final BiPredicate<String, Object> defaultContextFilter = (key, value) -> key != null && key.startsWith("_meta");

    public DistributedSyncMcpToolCallback(
            DistributedSyncMcpClient distributedSyncMcpClient,
            McpSchema.Tool tool,
            BiPredicate<String, Object> contextFilter) {
        Assert.notNull(distributedSyncMcpClient, "distributedSyncClient must not be null");
        Assert.notNull(tool, "tool must not be null");
        this.distributedSyncMcpClient = distributedSyncMcpClient;
        this.tool = tool;
        this.contextFilter = contextFilter != null ? contextFilter : defaultContextFilter;
    }

    @Override
    public ToolDefinition getToolDefinition() {
        return ToolDefinition.builder()
                .name(McpToolUtils.prefixedToolName(this.distributedSyncMcpClient.getServerName(), this.tool.name()))
                .description(this.tool.description())
                .inputSchema(ModelOptionsUtils.toJsonString(this.tool.inputSchema()))
                .build();
    }

    @Override
    public String call(String toolInput, ToolContext toolContext) {
        Map<String, Object> arguments = ModelOptionsUtils.jsonToMap(toolInput);
        Map<String, Object> meta = Map.of();
        if (toolContext != null && !CollectionUtils.isEmpty(toolContext.getContext())) {
            meta = toolContext.getContext()
                    .entrySet()
                    .stream()
                    .filter(entry -> entry.getValue() != null
                            && this.contextFilter.test(entry.getKey(), entry.getValue()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        }
        McpSchema.CallToolResult response = this.distributedSyncMcpClient.callTool(new McpSchema.CallToolRequest(this.tool.name(), arguments, meta));
        if (response.isError() != null && response.isError()) {
            throw new IllegalStateException("Error calling tool: " + response.content());
        } else {
            return ModelOptionsUtils.toJsonString(response.content());
        }
    }

    @Override
    public String call(String toolInput) {
        Map<String, Object> arguments = ModelOptionsUtils.jsonToMap(toolInput);
        McpSchema.CallToolResult response = this.distributedSyncMcpClient.callTool(new McpSchema.CallToolRequest(this.tool.name(), arguments));
        if (response.isError() != null && response.isError()) {
            throw new IllegalStateException("Error calling tool: " + response.content());
        } else {
            return ModelOptionsUtils.toJsonString(response.content());
        }
    }
}
