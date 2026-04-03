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

package com.alibaba.cloud.ai.mcp.gateway.nacos.definition;

import com.alibaba.cloud.ai.mcp.gateway.core.McpGatewayToolDefinition;
import com.alibaba.nacos.api.ai.model.mcp.McpServerRemoteServiceConfig;
import com.alibaba.nacos.api.ai.model.mcp.McpToolMeta;
import com.alibaba.nacos.common.utils.JacksonUtils;
import com.alibaba.nacos.common.utils.StringUtils;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.jspecify.annotations.Nullable;
import org.springframework.ai.tool.support.ToolUtils;
import org.springframework.util.Assert;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NacosMcpGatewayToolDefinition extends McpGatewayToolDefinition {

	private @Nullable McpServerRemoteServiceConfig remoteServerConfig;

	private @Nullable Object inputSchema;

	private @Nullable McpToolMeta toolMeta;

	public NacosMcpGatewayToolDefinition() {
	}

	public NacosMcpGatewayToolDefinition(final String name, final String description, final String inputSchema) {
		Assert.hasText(name, "name cannot be null or empty");
		Assert.hasText(description, "description cannot be null or empty");
		Assert.hasText(inputSchema, "inputSchema cannot be null or empty");
		this.name = name;
		this.description = description;
		this.inputSchema = inputSchema;
	}

	public NacosMcpGatewayToolDefinition(final String name, final String description, final Object inputSchema,
			final @Nullable String version, final String protocol, final McpServerRemoteServiceConfig remoteServerConfig,
			final @Nullable McpToolMeta toolMeta, final @Nullable Boolean enabled) {
		Assert.hasText(name, "name cannot be null or empty");
		Assert.hasText(description, "description cannot be null or empty");
		Assert.notNull(inputSchema, "inputSchema cannot be null or empty");
		this.name = name;
		this.description = description;
		this.inputSchema = inputSchema;
		this.version = version;
		this.protocol = protocol;
		this.remoteServerConfig = remoteServerConfig;
		this.toolMeta = toolMeta;
		this.enabled = enabled;
	}

	public static NacosMcpGatewayToolDefinition.Builder builder() {
		return new NacosMcpGatewayToolDefinition.Builder();
	}

	@Override
	public String name() {
		return Objects.requireNonNull(this.name, "Tool name must not be null");
	}

	@Override
	public String description() {
		return Objects.requireNonNull(this.description, "Tool description must not be null");
	}

	@Override
	public String inputSchema() {
		return JacksonUtils.toJson(Objects.requireNonNull(this.inputSchema, "Tool inputSchema must not be null"));
	}

	public @Nullable String getName() {
		return name;
	}

	public void setName(final @Nullable String name) {
		this.name = name;
	}

	public @Nullable String getDescription() {
		return description;
	}

	public void setDescription(final @Nullable String description) {
		this.description = description;
	}

	public @Nullable Object getInputSchema() {
		return inputSchema;
	}

	public void setInputSchema(final @Nullable Object inputSchema) {
		this.inputSchema = inputSchema;
	}

	public @Nullable String getVersion() {
		return version;
	}

	public void setVersion(final @Nullable String version) {
		this.version = version;
	}

	public @Nullable McpServerRemoteServiceConfig getRemoteServerConfig() {
		return remoteServerConfig;
	}

	public void setRemoteServerConfig(final @Nullable McpServerRemoteServiceConfig remoteServerConfig) {
		this.remoteServerConfig = remoteServerConfig;
	}

	public @Nullable Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(final @Nullable Boolean enabled) {
		this.enabled = enabled;
	}

	public @Nullable String getProtocol() {
		return protocol;
	}

	public void setProtocol(final @Nullable String protocol) {
		this.protocol = protocol;
	}

	public @Nullable McpToolMeta getToolMeta() {
		return toolMeta;
	}

	public void setToolMeta(final @Nullable McpToolMeta toolMeta) {
		this.toolMeta = toolMeta;
	}

	public static final class Builder {

		private @Nullable String name;

		private @Nullable String description;

		private @Nullable String version;

		private @Nullable String protocol;

		private @Nullable McpServerRemoteServiceConfig remoteServerConfig;

		private @Nullable Boolean enabled;

		private @Nullable Object inputSchema;

		private @Nullable McpToolMeta toolsMeta;

		private Builder() {
		}

		public NacosMcpGatewayToolDefinition.Builder name(final String name) {
			this.name = name;
			return this;
		}

		public NacosMcpGatewayToolDefinition.Builder description(final String description) {
			this.description = description;
			return this;
		}

		public NacosMcpGatewayToolDefinition.Builder version(final @Nullable String version) {
			this.version = version;
			return this;
		}

		public NacosMcpGatewayToolDefinition.Builder remoteServerConfig(
				final McpServerRemoteServiceConfig remoteServerConfig) {
			this.remoteServerConfig = remoteServerConfig;
			return this;
		}

		public NacosMcpGatewayToolDefinition.Builder enabled(final @Nullable Boolean enabled) {
			this.enabled = enabled;
			return this;
		}

		public NacosMcpGatewayToolDefinition.Builder protocol(final String protocol) {
			this.protocol = protocol;
			return this;
		}

		public NacosMcpGatewayToolDefinition.Builder inputSchema(final Object inputSchema) {
			this.inputSchema = inputSchema;
			return this;
		}

		public NacosMcpGatewayToolDefinition.Builder toolsMeta(final @Nullable McpToolMeta toolsMeta) {
			this.toolsMeta = toolsMeta;
			return this;
		}

		public NacosMcpGatewayToolDefinition build() {
			String name = Objects.requireNonNull(this.name, "name cannot be null");
			String description = this.description;
			if (!StringUtils.isNoneBlank(description)) {
				description = ToolUtils.getToolDescriptionFromName(name);
			}
			String resolvedDescription = Objects.requireNonNull(description, "description cannot be null");
			Object inputSchema = Objects.requireNonNull(this.inputSchema, "inputSchema cannot be null");
			String protocol = Objects.requireNonNull(this.protocol, "protocol cannot be null");
			McpServerRemoteServiceConfig remoteServerConfig = Objects.requireNonNull(this.remoteServerConfig,
					"remoteServerConfig cannot be null");

			return new NacosMcpGatewayToolDefinition(name, resolvedDescription, inputSchema, this.version, protocol,
					remoteServerConfig, this.toolsMeta, this.enabled);
		}

	}

}
