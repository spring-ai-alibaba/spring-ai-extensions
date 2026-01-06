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

package com.alibaba.cloud.ai.mcp.discovery.client.transport;

import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.spec.McpSchema;

import java.util.Map;

/**
 * @author yingzi
 * @since 2025/10/25
 */

public interface DistributedSyncMcpClient {

    String getServerName();

    McpSchema.CallToolResult callTool(McpSchema.CallToolRequest callToolRequest);

    McpSchema.ListToolsResult listTools();

    McpSyncClient getMcpSyncClient();

    Map<String, McpSyncClient> init();

    void subscribe();
}
