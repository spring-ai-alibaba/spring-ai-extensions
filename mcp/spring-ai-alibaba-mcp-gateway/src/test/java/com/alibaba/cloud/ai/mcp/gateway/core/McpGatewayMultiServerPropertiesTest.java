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

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link McpGatewayMultiServerProperties}.
 */
class McpGatewayMultiServerPropertiesTest {

	@Test
	void defaultValuesAreCorrect() {
		McpGatewayMultiServerProperties props = new McpGatewayMultiServerProperties();

		assertFalse(props.isEnabled(), "multi-server should be disabled by default");
		assertNotNull(props.getServers(), "servers list should not be null");
		assertTrue(props.getServers().isEmpty(), "servers list should be empty by default");
	}

	@Test
	void configPrefixIsCorrect() {
		assertEquals("spring.ai.alibaba.mcp.gateway.multi-server", McpGatewayMultiServerProperties.CONFIG_PREFIX);
	}

	@Test
	void serverConfigDefaultValues() {
		McpGatewayMultiServerProperties.ServerConfig cfg = new McpGatewayMultiServerProperties.ServerConfig();

		assertEquals(McpGatewayMultiServerProperties.TransportType.SSE, cfg.getTransport(),
				"default transport should be SSE");
		assertEquals("/sse", cfg.getSseEndpoint());
		assertEquals("/mcp/message", cfg.getMessageEndpoint());
		assertEquals("/mcp", cfg.getMcpEndpoint());
		assertEquals("1.0.0", cfg.getServerVersion());
		assertNotNull(cfg.getServiceNames());
		assertTrue(cfg.getServiceNames().isEmpty());
	}

	@Test
	void serverNameFallsBackToNameWhenNotSet() {
		McpGatewayMultiServerProperties.ServerConfig cfg = new McpGatewayMultiServerProperties.ServerConfig();
		cfg.setName("my-server");

		assertEquals("my-server", cfg.getServerName(),
				"serverName should fall back to name when serverName is not set");
	}

	@Test
	void serverNameUsesExplicitValueWhenSet() {
		McpGatewayMultiServerProperties.ServerConfig cfg = new McpGatewayMultiServerProperties.ServerConfig();
		cfg.setName("my-server");
		cfg.setServerName("explicit-server-name");

		assertEquals("explicit-server-name", cfg.getServerName());
	}

	@Test
	void transportTypeEnumHasBothValues() {
		McpGatewayMultiServerProperties.TransportType[] types = McpGatewayMultiServerProperties.TransportType.values();
		assertEquals(2, types.length);
		assertEquals(McpGatewayMultiServerProperties.TransportType.SSE,
				McpGatewayMultiServerProperties.TransportType.valueOf("SSE"));
		assertEquals(McpGatewayMultiServerProperties.TransportType.STREAMABLE,
				McpGatewayMultiServerProperties.TransportType.valueOf("STREAMABLE"));
	}

	@Test
	void propertiesCanBeBuiltProgrammatically() {
		McpGatewayMultiServerProperties props = new McpGatewayMultiServerProperties();
		props.setEnabled(true);

		McpGatewayMultiServerProperties.ServerConfig server1 = new McpGatewayMultiServerProperties.ServerConfig();
		server1.setName("server1");
		server1.setTransport(McpGatewayMultiServerProperties.TransportType.SSE);
		server1.setSseEndpoint("/mcp/server1/sse");
		server1.setMessageEndpoint("/mcp/server1/message");
		server1.setServiceNames(List.of("nacos-service-a"));

		McpGatewayMultiServerProperties.ServerConfig server2 = new McpGatewayMultiServerProperties.ServerConfig();
		server2.setName("server2");
		server2.setTransport(McpGatewayMultiServerProperties.TransportType.STREAMABLE);
		server2.setMcpEndpoint("/mcp/server2/mcp");
		server2.setServiceNames(List.of("nacos-service-b", "nacos-service-c"));

		props.setServers(List.of(server1, server2));

		assertTrue(props.isEnabled());
		assertEquals(2, props.getServers().size());

		McpGatewayMultiServerProperties.ServerConfig s1 = props.getServers().get(0);
		assertEquals("server1", s1.getName());
		assertEquals(McpGatewayMultiServerProperties.TransportType.SSE, s1.getTransport());
		assertEquals("/mcp/server1/sse", s1.getSseEndpoint());
		assertEquals(1, s1.getServiceNames().size());
		assertEquals("nacos-service-a", s1.getServiceNames().get(0));

		McpGatewayMultiServerProperties.ServerConfig s2 = props.getServers().get(1);
		assertEquals("server2", s2.getName());
		assertEquals(McpGatewayMultiServerProperties.TransportType.STREAMABLE, s2.getTransport());
		assertEquals("/mcp/server2/mcp", s2.getMcpEndpoint());
		assertEquals(2, s2.getServiceNames().size());
	}

	@Test
	void createNacosPropertiesHelperCreatesCorrectProperties() {
		// Test the helper method via a concrete anonymous subclass to access protected method
		MultiServerMcpGatewayManager manager = new MultiServerMcpGatewayManager() {
		};

		var nacosProps = manager.createNacosProperties(List.of("svc-a", "svc-b"));

		assertNotNull(nacosProps);
		assertEquals(List.of("svc-a", "svc-b"), nacosProps.getServiceNames());
	}

	@Test
	void createNacosPropertiesWithEmptyList() {
		MultiServerMcpGatewayManager manager = new MultiServerMcpGatewayManager() {
		};

		var nacosProps = manager.createNacosProperties(List.of());

		assertNotNull(nacosProps);
		assertTrue(nacosProps.getServiceNames().isEmpty());
	}

	@Test
	void destroyWithNoWatchersDoesNotThrow() {
		MultiServerMcpGatewayManager manager = new MultiServerMcpGatewayManager() {
		};

		assertDoesNotThrow(manager::destroy);
	}

}
