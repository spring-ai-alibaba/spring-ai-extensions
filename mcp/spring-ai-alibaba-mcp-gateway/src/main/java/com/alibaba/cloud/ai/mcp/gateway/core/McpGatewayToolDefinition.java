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

package com.alibaba.cloud.ai.mcp.gateway.core;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.jspecify.annotations.Nullable;
import org.springframework.ai.tool.definition.ToolDefinition;

/**
 * MCP Gateway 工具定义抽象类
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class McpGatewayToolDefinition implements ToolDefinition {

	protected @Nullable String name;

	protected @Nullable String description;

	protected @Nullable String version;

	protected @Nullable String protocol;

	protected @Nullable Boolean enabled;

	protected @Nullable Object inputSchema;

	public McpGatewayToolDefinition() {
	}

	public McpGatewayToolDefinition(String name, String description, Object inputSchema, String version,
			String protocol, Boolean enabled) {
		this.name = name;
		this.description = description;
		this.inputSchema = inputSchema;
		this.version = version;
		this.protocol = protocol;
		this.enabled = enabled;
	}

	public @Nullable String getName() {
		return name;
	}

	public void setName(@Nullable String name) {
		this.name = name;
	}

	public @Nullable String getDescription() {
		return description;
	}

	public void setDescription(@Nullable String description) {
		this.description = description;
	}

	public @Nullable String getVersion() {
		return version;
	}

	public void setVersion(@Nullable String version) {
		this.version = version;
	}

	public @Nullable String getProtocol() {
		return protocol;
	}

	public void setProtocol(@Nullable String protocol) {
		this.protocol = protocol;
	}

	public @Nullable Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(@Nullable Boolean enabled) {
		this.enabled = enabled;
	}

	public @Nullable Object getInputSchema() {
		return inputSchema;
	}

	public void setInputSchema(@Nullable Object inputSchema) {
		this.inputSchema = inputSchema;
	}

}
