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
package com.alibaba.cloud.ai.mcp.gateway.nacos.provider;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import com.alibaba.cloud.ai.mcp.gateway.core.McpGatewayToolDefinition;
import com.alibaba.cloud.ai.mcp.gateway.core.McpGatewayToolManager;
import com.alibaba.cloud.ai.mcp.gateway.nacos.callback.NacosMcpGatewayToolCallback;
import org.springframework.ai.mcp.modern.InMemoryAsyncToolRepository;
import org.springframework.ai.mcp.modern.InMemorySyncToolRepository;
import org.springframework.ai.mcp.modern.McpModernToolUtils;
import org.springframework.ai.tool.ToolCallback;

public final class NacosModernMcpGatewayToolsProvider implements McpGatewayToolManager, AutoCloseable {

	private final Map<String, NacosMcpGatewayToolCallback> toolCallbacks = new HashMap<>();

	private final Consumer<ToolCallback> add;

	private final Consumer<String> remove;

	public NacosModernMcpGatewayToolsProvider(InMemorySyncToolRepository repository) {
		this.add = callback -> repository.add(McpModernToolUtils.toSyncTool(callback));
		this.remove = repository::remove;
	}

	public NacosModernMcpGatewayToolsProvider(InMemoryAsyncToolRepository repository) {
		this.add = callback -> repository.add(McpModernToolUtils.toAsyncTool(callback));
		this.remove = repository::remove;
	}

	@Override
	public synchronized void addTool(McpGatewayToolDefinition toolDefinition) {
		removeTool(toolDefinition.name());
		var callback = new NacosMcpGatewayToolCallback(toolDefinition);
		this.add.accept(callback);
		this.toolCallbacks.put(toolDefinition.name(), callback);
	}

	@Override
	public synchronized void removeTool(String toolName) {
		this.remove.accept(toolName);
		var callback = this.toolCallbacks.remove(toolName);
		if (callback != null) {
			callback.close();
		}
	}

	@Override
	public synchronized void close() {
		this.toolCallbacks.values().forEach(NacosMcpGatewayToolCallback::close);
		this.toolCallbacks.clear();
	}

}
