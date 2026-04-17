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

package com.alibaba.cloud.ai.autoconfigure.mcp.gateway.core;

import com.alibaba.cloud.ai.mcp.gateway.core.McpGatewayMultiServerProperties;
import com.alibaba.cloud.ai.mcp.gateway.core.MultiServerMcpGatewayManager;
import com.alibaba.cloud.ai.mcp.gateway.nacos.properties.NacosMcpGatewayProperties;
import com.alibaba.cloud.ai.mcp.gateway.nacos.provider.NacosMcpAsyncGatewayToolsProvider;
import com.alibaba.cloud.ai.mcp.gateway.nacos.tools.NacosMcpGatewayToolsInitializer;
import com.alibaba.cloud.ai.mcp.gateway.nacos.watcher.NacosMcpGatewayToolsWatcher;
import com.alibaba.cloud.ai.mcp.nacos.service.NacosMcpOperationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.modelcontextprotocol.json.jackson.JacksonMcpJsonMapper;
import io.modelcontextprotocol.server.McpAsyncServer;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.McpServerFeatures.AsyncToolSpecification;
import io.modelcontextprotocol.server.transport.WebFluxSseServerTransportProvider;
import io.modelcontextprotocol.server.transport.WebFluxStreamableServerTransportProvider;
import io.modelcontextprotocol.spec.McpSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.mcp.McpToolUtils;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.util.List;

/**
 * WebFlux (reactive) implementation of the multi-server MCP Gateway manager. Creates one
 * independent {@link McpAsyncServer} + {@link WebFluxSseServerTransportProvider} or
 * {@link WebFluxStreamableServerTransportProvider} per configured server entry, and
 * aggregates their {@link RouterFunction}s into a single combined route that can be
 * registered as a Spring WebFlux {@code RouterFunction} bean.
 *
 * <p>
 * Uses {@link McpAsyncServer} (instead of {@code McpSyncServer}) to avoid blocking Netty
 * event-loop threads in a reactive environment.
 *
 * <p>
 * Requires MCP Java SDK 0.14+ ({@code jsonMapper} / {@code JacksonMcpJsonMapper} on transport
 * builders).
 *
 * @author vera-qwang
 */
public class WebFluxMultiServerMcpGatewayManager extends MultiServerMcpGatewayManager {

	private static final Logger logger = LoggerFactory.getLogger(WebFluxMultiServerMcpGatewayManager.class);

	private RouterFunction<ServerResponse> combinedRouterFunction;

	public WebFluxMultiServerMcpGatewayManager(McpGatewayMultiServerProperties properties,
			NacosMcpOperationService nacosService, ObjectMapper objectMapper) {
		for (McpGatewayMultiServerProperties.ServerConfig cfg : properties.getServers()) {
			try {
				initServerEntry(cfg, nacosService, objectMapper);
			}
			catch (Exception e) {
				logger.error("Failed to initialize multi-server entry '{}', skipping.", cfg.getName(), e);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void initServerEntry(McpGatewayMultiServerProperties.ServerConfig cfg,
			NacosMcpOperationService nacosService, ObjectMapper objectMapper) {

		// 1. Create transport and resolve server builder by transport type (MCP SDK 0.14+).
		// WebFlux transports return RouterFunction<?> so we cast with suppressed warning.
		var mcpJsonMapper = new JacksonMcpJsonMapper(objectMapper);
		RouterFunction<ServerResponse> routerFunction;
		McpServer.AsyncSpecification<?> serverBuilder;

		if (cfg.getTransport() == McpGatewayMultiServerProperties.TransportType.STREAMABLE) {
			WebFluxStreamableServerTransportProvider transport = WebFluxStreamableServerTransportProvider.builder()
				.jsonMapper(mcpJsonMapper)
				.messageEndpoint(cfg.getMcpEndpoint())
				.build();
			serverBuilder = McpServer.async(transport);
			routerFunction = (RouterFunction<ServerResponse>) transport.getRouterFunction();
		}
		else {
			WebFluxSseServerTransportProvider transport = WebFluxSseServerTransportProvider.builder()
				.jsonMapper(mcpJsonMapper)
				.sseEndpoint(cfg.getSseEndpoint())
				.messageEndpoint(cfg.getMessageEndpoint())
				.build();
			serverBuilder = McpServer.async(transport);
			routerFunction = (RouterFunction<ServerResponse>) transport.getRouterFunction();
		}

		// 2. Load initial tools from Nacos
		NacosMcpGatewayProperties perEntryProps = createNacosProperties(cfg.getServiceNames());
		NacosMcpGatewayToolsInitializer initializer = new NacosMcpGatewayToolsInitializer(nacosService, perEntryProps);
		List<ToolCallback> initialCallbacks = initializer.initializeTools();
		List<AsyncToolSpecification> initialSpecs = initialCallbacks.stream()
			.map(McpToolUtils::toAsyncToolSpecification)
			.toList();

		// 3. Build McpAsyncServer with initial tools
		serverBuilder.serverInfo(cfg.getServerName(), cfg.getServerVersion());
		serverBuilder.capabilities(McpSchema.ServerCapabilities.builder().tools(true).build());
		if (!initialSpecs.isEmpty()) {
			serverBuilder.tools(initialSpecs);
		}
		McpAsyncServer server = serverBuilder.build();

		// 4. Create tool manager and watcher for dynamic Nacos updates (30s polling)
		NacosMcpAsyncGatewayToolsProvider toolManager = new NacosMcpAsyncGatewayToolsProvider(server);
		if (cfg.getServiceNames() != null && !cfg.getServiceNames().isEmpty()) {
			NacosMcpGatewayToolsWatcher watcher = new NacosMcpGatewayToolsWatcher(toolManager, nacosService,
					perEntryProps);
			watchers.add(watcher);
		}

		// 5. Accumulate router functions
		combinedRouterFunction = (combinedRouterFunction == null) ? routerFunction
				: combinedRouterFunction.and(routerFunction);

		logger.info("Initialized WebFlux multi-server entry '{}' [{}] with {} initial tool(s) at: {}",
				cfg.getName(), cfg.getTransport(), initialCallbacks.size(),
				cfg.getTransport() == McpGatewayMultiServerProperties.TransportType.SSE ? cfg.getSseEndpoint()
						: cfg.getMcpEndpoint());
	}

	/**
	 * Returns the combined {@link RouterFunction} for all configured server entries.
	 * @return combined router function, or {@code null} if no entries were initialized
	 */
	public RouterFunction<ServerResponse> getCombinedRouterFunction() {
		return combinedRouterFunction;
	}

}
