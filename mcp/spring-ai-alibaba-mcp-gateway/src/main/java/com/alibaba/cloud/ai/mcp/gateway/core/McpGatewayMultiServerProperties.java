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

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * Configuration properties for multi-server MCP Gateway mode. When enabled, each entry
 * in {@code servers} creates an independent MCP Server instance with its own endpoint,
 * tool set, and Nacos service subscription.
 *
 * <p>
 * Example configuration:
 *
 * <pre>{@code
 * spring.ai.mcp.server.enabled: false
 * spring.ai.alibaba.mcp.gateway.enabled: false
 * spring.ai.alibaba.mcp.gateway.multi-server:
 *   enabled: true
 *   servers:
 *     - name: server1
 *       transport: SSE
 *       sse-endpoint: /mcp/server1/sse
 *       message-endpoint: /mcp/server1/message
 *       server-name: mcp-gateway-server1
 *       service-names:
 *         - nacos-mcp-server-sse
 *     - name: server2
 *       transport: STREAMABLE
 *       mcp-endpoint: /mcp/server2/mcp
 *       server-name: mcp-gateway-server2
 *       service-names:
 *         - nacos-mcp-server-streamable
 * }</pre>
 *
 * @author vera-qwang
 */
@ConfigurationProperties(prefix = McpGatewayMultiServerProperties.CONFIG_PREFIX)
public class McpGatewayMultiServerProperties {

	public static final String CONFIG_PREFIX = "spring.ai.alibaba.mcp.gateway.multi-server";

	private boolean enabled = false;

	private List<ServerConfig> servers = new ArrayList<>();

	/**
	 * MCP transport protocol type.
	 */
	public enum TransportType {

		/**
		 * Legacy SSE transport: GET /xxx/sse for SSE connection + POST /xxx/message for
		 * JSON-RPC messages.
		 */
		SSE,

		/**
		 * Streamable HTTP transport (MCP spec 2025-03-26): unified GET/POST/DELETE
		 * /xxx/mcp endpoint.
		 */
		STREAMABLE

	}

	/**
	 * Configuration for a single virtual MCP server endpoint.
	 */
	public static class ServerConfig {

		/** Logical name for this server entry. Used for logging. */
		private String name;

		/** Transport protocol. Defaults to SSE. */
		private TransportType transport = TransportType.SSE;

		/** [SSE mode] SSE connection endpoint path. Defaults to /sse. */
		private String sseEndpoint = "/sse";

		/** [SSE mode] JSON-RPC message endpoint path. Defaults to /mcp/message. */
		private String messageEndpoint = "/mcp/message";

		/** [STREAMABLE mode] Unified MCP endpoint path. Defaults to /mcp. */
		private String mcpEndpoint = "/mcp";

		/** MCP server info name, defaults to name field if not set. */
		private String serverName;

		/** MCP server info version. */
		private String serverVersion = "1.0.0";

		/** Nacos service names whose tools will be aggregated into this server. */
		private List<String> serviceNames = new ArrayList<>();

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public TransportType getTransport() {
			return transport;
		}

		public void setTransport(TransportType transport) {
			this.transport = transport;
		}

		public String getSseEndpoint() {
			return sseEndpoint;
		}

		public void setSseEndpoint(String sseEndpoint) {
			this.sseEndpoint = sseEndpoint;
		}

		public String getMessageEndpoint() {
			return messageEndpoint;
		}

		public void setMessageEndpoint(String messageEndpoint) {
			this.messageEndpoint = messageEndpoint;
		}

		public String getMcpEndpoint() {
			return mcpEndpoint;
		}

		public void setMcpEndpoint(String mcpEndpoint) {
			this.mcpEndpoint = mcpEndpoint;
		}

		public String getServerName() {
			return serverName != null ? serverName : name;
		}

		public void setServerName(String serverName) {
			this.serverName = serverName;
		}

		public String getServerVersion() {
			return serverVersion;
		}

		public void setServerVersion(String serverVersion) {
			this.serverVersion = serverVersion;
		}

		public List<String> getServiceNames() {
			return serviceNames;
		}

		public void setServiceNames(List<String> serviceNames) {
			this.serviceNames = serviceNames;
		}

	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public List<ServerConfig> getServers() {
		return servers;
	}

	public void setServers(List<ServerConfig> servers) {
		this.servers = servers;
	}

}
