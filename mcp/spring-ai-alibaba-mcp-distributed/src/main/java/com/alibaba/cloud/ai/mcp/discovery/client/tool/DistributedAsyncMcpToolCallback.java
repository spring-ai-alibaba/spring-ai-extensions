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

import com.alibaba.cloud.ai.mcp.discovery.client.transport.DistributedAsyncMcpClient;
import io.modelcontextprotocol.spec.McpSchema;
import org.springframework.ai.mcp.McpToolUtils;
import org.springframework.ai.model.ModelOptionsUtils;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.util.Assert;

import java.util.Map;

/**
 * @author yingzi
 * @since 2025/10/25
 */

public class DistributedAsyncMcpToolCallback implements ToolCallback {

    private final DistributedAsyncMcpClient distributedAsyncMcpClient;

    private final McpSchema.Tool tool;

    public DistributedAsyncMcpToolCallback(DistributedAsyncMcpClient distributedAsyncMcpClient, McpSchema.Tool tool) {
        Assert.notNull(distributedAsyncMcpClient, "distributedSyncClient must not be null");
        Assert.notNull(tool, "tool must not be null");
        this.distributedAsyncMcpClient = distributedAsyncMcpClient;
        this.tool = tool;
    }

    @Override
    public ToolDefinition getToolDefinition() {
        return ToolDefinition.builder()
                .name(McpToolUtils.prefixedToolName(this.distributedAsyncMcpClient.getServerName(), this.tool.name()))
                .description(this.tool.description())
                .inputSchema(ModelOptionsUtils.toJsonString(this.tool.inputSchema()))
                .build();    }

    @Override
    public String call(String toolInput) {
        Map<String, Object> arguments = ModelOptionsUtils.jsonToMap(toolInput);
        return this.distributedAsyncMcpClient
                .callTool(new McpSchema.CallToolRequest(this.tool.name(), arguments))
                .map((response) -> {
                    if (response.isError() != null && response.isError()) {
                        throw new IllegalStateException("Error calling tool: " + String.valueOf(response.content()));
                    }
                    else {
                        return ModelOptionsUtils.toJsonString(response.content());
                    }
                })
                .block();
    }
}
