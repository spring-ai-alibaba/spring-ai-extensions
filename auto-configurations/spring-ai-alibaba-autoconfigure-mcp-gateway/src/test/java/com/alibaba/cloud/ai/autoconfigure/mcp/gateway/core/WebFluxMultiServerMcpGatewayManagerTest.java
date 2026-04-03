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
import com.alibaba.cloud.ai.mcp.nacos.service.NacosMcpOperationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link WebFluxMultiServerMcpGatewayManager}.
 */
@ExtendWith(MockitoExtension.class)
class WebFluxMultiServerMcpGatewayManagerTest {

	@Mock
	private NacosMcpOperationService nacosService;

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Test
	void managerWithEmptyServersReturnsNullRouterFunction() {
		McpGatewayMultiServerProperties props = new McpGatewayMultiServerProperties();
		props.setServers(Collections.emptyList());

		WebFluxMultiServerMcpGatewayManager manager = new WebFluxMultiServerMcpGatewayManager(props, nacosService,
				objectMapper);

		assertNull(manager.getCombinedRouterFunction());

		manager.destroy();
	}

	@Test
	void managerWithSseEntryCreatesReactiveRouterFunction() throws Exception {
		when(nacosService.getServerDetail(anyString())).thenReturn(null);

		McpGatewayMultiServerProperties props = buildPropsWithSseServer("/mcp/server1/sse", "/mcp/server1/message",
				"nacos-svc");

		WebFluxMultiServerMcpGatewayManager manager = new WebFluxMultiServerMcpGatewayManager(props, nacosService,
				objectMapper);

		assertNotNull(manager.getCombinedRouterFunction(), "SSE WebFlux transport should produce a RouterFunction");

		manager.destroy();
	}

	@Test
	void managerWithStreamableEntryCreatesReactiveRouterFunction() {
		McpGatewayMultiServerProperties props = buildPropsWithStreamableServer("/mcp/server2/mcp");

		WebFluxMultiServerMcpGatewayManager manager = new WebFluxMultiServerMcpGatewayManager(props, nacosService,
				objectMapper);

		assertNotNull(manager.getCombinedRouterFunction(),
				"STREAMABLE WebFlux transport should produce a RouterFunction");

		manager.destroy();
	}

	@Test
	void managerWithMultipleEntriesCombinesRouterFunctions() throws Exception {
		when(nacosService.getServerDetail(anyString())).thenReturn(null);

		McpGatewayMultiServerProperties props = new McpGatewayMultiServerProperties();

		McpGatewayMultiServerProperties.ServerConfig sse = serverConfig("s1",
				McpGatewayMultiServerProperties.TransportType.SSE, "/mcp/s1/sse", "/mcp/s1/message", null,
				List.of("svc-a"));
		McpGatewayMultiServerProperties.ServerConfig streamable = serverConfig("s2",
				McpGatewayMultiServerProperties.TransportType.STREAMABLE, null, null, "/mcp/s2/mcp", List.of("svc-b"));

		props.setServers(List.of(sse, streamable));

		WebFluxMultiServerMcpGatewayManager manager = new WebFluxMultiServerMcpGatewayManager(props, nacosService,
				objectMapper);

		assertNotNull(manager.getCombinedRouterFunction());

		manager.destroy();
	}

	@Test
	void destroyStopsAllWatchers() throws Exception {
		when(nacosService.getServerDetail(anyString())).thenReturn(null);

		McpGatewayMultiServerProperties props = buildPropsWithSseServer("/mcp/srv/sse", "/mcp/srv/message", "svc");

		WebFluxMultiServerMcpGatewayManager manager = new WebFluxMultiServerMcpGatewayManager(props, nacosService,
				objectMapper);

		assertDoesNotThrow(manager::destroy);
	}

	// ---- helpers ----

	private McpGatewayMultiServerProperties buildPropsWithSseServer(String sseEndpoint, String messageEndpoint,
			String... serviceNames) {
		McpGatewayMultiServerProperties props = new McpGatewayMultiServerProperties();
		McpGatewayMultiServerProperties.ServerConfig cfg = new McpGatewayMultiServerProperties.ServerConfig();
		cfg.setName("test-server");
		cfg.setTransport(McpGatewayMultiServerProperties.TransportType.SSE);
		cfg.setSseEndpoint(sseEndpoint);
		cfg.setMessageEndpoint(messageEndpoint);
		cfg.setServiceNames(serviceNames == null ? Collections.emptyList() : List.of(serviceNames));
		props.setServers(List.of(cfg));
		return props;
	}

	private McpGatewayMultiServerProperties buildPropsWithStreamableServer(String mcpEndpoint,
			String... serviceNames) {
		McpGatewayMultiServerProperties props = new McpGatewayMultiServerProperties();
		McpGatewayMultiServerProperties.ServerConfig cfg = new McpGatewayMultiServerProperties.ServerConfig();
		cfg.setName("test-server");
		cfg.setTransport(McpGatewayMultiServerProperties.TransportType.STREAMABLE);
		cfg.setMcpEndpoint(mcpEndpoint);
		cfg.setServiceNames(serviceNames == null ? Collections.emptyList() : List.of(serviceNames));
		props.setServers(List.of(cfg));
		return props;
	}

	private McpGatewayMultiServerProperties.ServerConfig serverConfig(String name,
			McpGatewayMultiServerProperties.TransportType transport, String sseEndpoint, String messageEndpoint,
			String mcpEndpoint, List<String> serviceNames) {
		McpGatewayMultiServerProperties.ServerConfig cfg = new McpGatewayMultiServerProperties.ServerConfig();
		cfg.setName(name);
		cfg.setTransport(transport);
		if (sseEndpoint != null) {
			cfg.setSseEndpoint(sseEndpoint);
		}
		if (messageEndpoint != null) {
			cfg.setMessageEndpoint(messageEndpoint);
		}
		if (mcpEndpoint != null) {
			cfg.setMcpEndpoint(mcpEndpoint);
		}
		cfg.setServiceNames(serviceNames != null ? serviceNames : Collections.emptyList());
		return cfg;
	}

}
