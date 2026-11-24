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

import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.Map;

import com.alibaba.cloud.ai.mcp.common.transport.builder.ModernMcpToolTransport;
import com.alibaba.cloud.ai.mcp.discovery.client.transport.streamable.StreamWebFluxDistributedAsyncMcpClient;
import com.alibaba.cloud.ai.mcp.discovery.client.transport.streamable.StreamWebFluxDistributedSyncMcpClient;
import com.alibaba.cloud.ai.mcp.gateway.core.McpGatewayMultiServerProperties;
import com.alibaba.cloud.ai.mcp.gateway.core.McpGatewayMultiServerProperties.ServerConfig;
import com.alibaba.cloud.ai.mcp.gateway.core.McpGatewayMultiServerProperties.TransportType;
import com.alibaba.cloud.ai.mcp.gateway.core.McpGatewayProperties;
import com.alibaba.cloud.ai.mcp.gateway.core.utils.SpringBeanUtils;
import com.alibaba.cloud.ai.mcp.nacos.service.NacosMcpOperationService;
import com.alibaba.cloud.ai.mcp.nacos.service.model.NacosMcpServerEndpoint;
import com.alibaba.cloud.ai.mcp.router.service.McpProxyService;
import com.alibaba.cloud.ai.mcp.utils.NacosMcpProtocol;
import com.alibaba.nacos.api.ai.model.mcp.McpEndpointInfo;
import com.alibaba.nacos.api.ai.model.mcp.McpServerDetailInfo;
import com.alibaba.nacos.api.ai.model.mcp.McpServerRemoteServiceConfig;
import com.alibaba.nacos.api.ai.model.mcp.McpServiceRef;
import com.alibaba.nacos.api.ai.model.mcp.McpTool;
import com.alibaba.nacos.api.ai.model.mcp.McpToolMeta;
import com.alibaba.nacos.api.ai.model.mcp.McpToolSpecification;
import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.transport.HttpClientStreamableHttpTransport;
import io.modelcontextprotocol.json.jackson3.JacksonMcpJsonMapper;
import io.modelcontextprotocol.modern.McpSchema.Implementation;
import io.modelcontextprotocol.modern.server.feature.ToolsFeature;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.spec.McpClientTransport;
import io.modelcontextprotocol.spec.McpSchema;
import org.junit.jupiter.api.Test;
import org.springframework.ai.mcp.McpToolUtils;
import org.springframework.ai.mcp.client.common.autoconfigure.configurer.McpAsyncClientConfigurer;
import org.springframework.ai.mcp.client.common.autoconfigure.configurer.McpSyncClientConfigurer;
import org.springframework.ai.mcp.client.common.autoconfigure.properties.McpClientCommonProperties;
import org.springframework.ai.mcp.modern.InMemoryAsyncToolRepository;
import org.springframework.ai.mcp.modern.McpModernToolUtils;
import org.springframework.ai.mcp.server.webflux.transport.WebFluxStreamableServerTransportProvider;
import org.springframework.ai.mcp.server.webflux.transport.modern.WebFluxMcpTransport;
import org.springframework.ai.tool.function.FunctionToolCallback;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import reactor.netty.DisposableServer;
import reactor.netty.http.server.HttpServer;
import tools.jackson.databind.json.JsonMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ModernMcpGatewayTests {

	private static final Duration TIMEOUT = Duration.ofSeconds(10);

	private static final String MODERN_PATH = "/modern/mcp";

	private static final String LEGACY_PATH = "/legacy/mcp";

	private static final String TOOL_NAME = "echo_tools_message";

	private final JsonMapper jsonMapper = JsonMapper.shared();

	private final JacksonMcpJsonMapper mcpMapper = new JacksonMcpJsonMapper(this.jsonMapper);

	@Test
	void syncAndAsyncClientsCanListAndCallBothProtocolsThroughDiscoveryRouterAndGateway() throws Exception {
		var registry = mock(NacosMcpOperationService.class);
		var beanUtils = SpringBeanUtils.getInstance();
		var previousContext = beanUtils.getApplicationContext();
		try (var context = new GenericApplicationContext()) {
			context.registerBean(NacosMcpOperationService.class, () -> registry);
			context.registerBean(WebClient.Builder.class, WebClient::builder);
			context.registerBean(McpGatewayProperties.class);
			context.registerBean(JsonMapper.class, () -> this.jsonMapper);
			context.registerBean(McpSyncClientConfigurer.class, () -> new McpSyncClientConfigurer(List.of()));
			context.registerBean(McpAsyncClientConfigurer.class, () -> new McpAsyncClientConfigurer(List.of()));
			context.registerBean(McpClientCommonProperties.class, () -> {
				var properties = new McpClientCommonProperties();
				properties.setInitialized(true);
				properties.setRequestTimeout(TIMEOUT);
				return properties;
			});
			context.refresh();
			beanUtils.setApplicationContext(context);
			var legacyTransport = WebFluxStreamableServerTransportProvider.builder().messageEndpoint(LEGACY_PATH)
				.jsonMapper(this.mcpMapper).build();
			var legacyServer = McpServer.async(legacyTransport).serverInfo("legacy", "1")
				.capabilities(McpSchema.ServerCapabilities.builder().tools(false).build())
				.tools(List.of(McpToolUtils.toAsyncToolSpecification(echo("legacy")))).build();
			var repository = InMemoryAsyncToolRepository.builder().build();
			repository.add(McpModernToolUtils.toAsyncTool(echo("modern")));
			var modernServer = io.modelcontextprotocol.modern.server.McpServer.builder()
				.serverInfo(Implementation.builder("modern", "1").build()).jsonMapper(this.mcpMapper)
				.feature(ToolsFeature.ofAsync(repository)).build();
			var modernTransport = WebFluxMcpTransport.builder(modernServer).endpoint(MODERN_PATH)
				.jsonMapper(this.mcpMapper).build();
			var backend = serve(modernTransport.getRouterFunction().andOther(legacyTransport.getRouterFunction()));
			try {
				registerBackend(registry, "legacy", "legacy-service", LEGACY_PATH, backend.port());
				registerBackend(registry, "modern", NacosMcpProtocol.modernServiceName("modern-service"),
						MODERN_PATH, backend.port());
				for (String protocol : List.of("legacy", "modern")) {
					assertDiscoveredClients(registry, context, protocol);
					assertRouterCalls(registry, protocol);
				}

				var properties = new McpGatewayMultiServerProperties();
				properties.setServers(List.of(gatewayEntry("modern", TransportType.MODERN, MODERN_PATH),
						gatewayEntry("legacy", TransportType.STREAMABLE, LEGACY_PATH)));
				var manager = new WebFluxMultiServerMcpGatewayManager(properties, registry, this.jsonMapper);
				var gateway = serve(manager.getCombinedRouterFunction());
				try {
					for (String protocol : List.of("legacy", "modern")) {
						assertSyncCalls(gateway, protocol);
						assertAsyncCalls(gateway, protocol);
					}
				}
				finally {
					gateway.disposeNow();
					manager.destroy();
				}
			}
			finally {
				backend.disposeNow();
				modernTransport.closeGracefully();
				modernServer.closeGracefully();
				legacyServer.closeGracefully().block(TIMEOUT);
			}
		}
		finally {
			beanUtils.setApplicationContext(previousContext);
		}
	}

	private static void assertDiscoveredClients(NacosMcpOperationService registry, GenericApplicationContext context,
			String name) {
		var sync = new StreamWebFluxDistributedSyncMcpClient(name, "1.0.0", registry, context, false);
		var async = new StreamWebFluxDistributedAsyncMcpClient(name, "1.0.0", registry, context, false);
		try {
			sync.init();
			async.init();
			assertThat(sync.listTools().tools()).extracting(McpSchema.Tool::name).containsExactly(TOOL_NAME);
			assertEcho(sync.callTool(new McpSchema.CallToolRequest(TOOL_NAME, Map.of("message", "discovery"))),
					name + ":discovery");
			assertThat(async.listTools().block(TIMEOUT).tools()).extracting(McpSchema.Tool::name).containsExactly(TOOL_NAME);
			assertEcho(async.callTool(new McpSchema.CallToolRequest(TOOL_NAME, Map.of("message", "discovery")))
				.block(TIMEOUT), name + ":discovery");
		}
		finally {
			sync.closeGracefully();
			async.closeGracefully().block(TIMEOUT);
		}
	}

	private static void assertRouterCalls(NacosMcpOperationService registry, String name) {
		var router = new McpProxyService(registry);
		try {
			assertThat(router.establishConnection(name)).isTrue();
			assertThat(router.getClient(name).listTools().tools()).extracting(McpSchema.Tool::name).containsExactly(TOOL_NAME);
			assertThat(router.callTool(name, TOOL_NAME, Map.of("message", "router"))).contains(name + ":router");
		}
		finally {
			router.closeAllConnections();
		}
	}

	private void assertSyncCalls(DisposableServer gateway, String protocol) {
		try (var client = McpClient.sync(transport(gateway, protocol)).requestTimeout(TIMEOUT).build()) {
			client.initialize();
			assertThat(client.listTools().tools()).extracting(McpSchema.Tool::name)
				.containsExactlyInAnyOrder("legacy_tools_" + TOOL_NAME, "modern_tools_" + TOOL_NAME);
			for (String backend : List.of("legacy", "modern")) {
				assertEcho(client.callTool(request(backend, "sync")), backend + ":sync");
			}
		}
	}

	private void assertAsyncCalls(DisposableServer gateway, String protocol) {
		var client = McpClient.async(transport(gateway, protocol)).requestTimeout(TIMEOUT).build();
		try {
			client.initialize().block(TIMEOUT);
			assertThat(client.listTools().block(TIMEOUT).tools()).extracting(McpSchema.Tool::name)
				.containsExactlyInAnyOrder("legacy_tools_" + TOOL_NAME, "modern_tools_" + TOOL_NAME);
			for (String backend : List.of("legacy", "modern")) {
				assertEcho(client.callTool(request(backend, "async")).block(TIMEOUT), backend + ":async");
			}
		}
		finally {
			client.closeGracefully().block(TIMEOUT);
		}
	}

	private McpClientTransport transport(DisposableServer gateway, String protocol) {
		String baseUrl = "http://127.0.0.1:" + gateway.port();
		if ("modern".equals(protocol)) {
			return new ModernMcpToolTransport(URI.create(baseUrl + MODERN_PATH), "modern-gateway", this.mcpMapper,
					TIMEOUT);
		}
		return HttpClientStreamableHttpTransport.builder(baseUrl).endpoint(LEGACY_PATH).jsonMapper(this.mcpMapper)
			.build();
	}

	private static McpSchema.CallToolRequest request(String backend, String input) {
		return new McpSchema.CallToolRequest(backend + "_tools_" + TOOL_NAME, Map.of("message", input));
	}

	private static void assertEcho(McpSchema.CallToolResult result, String expected) {
		assertThat(result.isError()).isNotEqualTo(true);
		assertThat(result.content()).singleElement().isInstanceOfSatisfying(McpSchema.TextContent.class,
				content -> assertThat(content.text()).contains(expected));
	}

	private static FunctionToolCallback<EchoInput, String> echo(String backend) {
		return FunctionToolCallback.<EchoInput, String>builder(TOOL_NAME, input -> backend + ":" + input.message())
			.inputType(EchoInput.class).description("Echoes a message").build();
	}

	private static ServerConfig gatewayEntry(String name, TransportType transport, String path) {
		var config = new ServerConfig();
		config.setName(name);
		config.setMcpEndpoint(path);
		config.setTransport(transport);
		config.setServiceNames(List.of("legacy", "modern"));
		return config;
	}

	private static void registerBackend(NacosMcpOperationService registry, String name, String serviceName,
			String path, int port) throws Exception {
		var ref = new McpServiceRef();
		ref.setServiceName(serviceName);
		ref.setTransportProtocol("http");
		var remote = new McpServerRemoteServiceConfig();
		remote.setExportPath(path);
		remote.setServiceRef(ref);
		var tool = new McpTool();
		tool.setName(TOOL_NAME);
		tool.setDescription("Echoes a message");
		tool.setInputSchema(Map.of("type", "object", "properties", Map.of("message", Map.of("type", "string")),
				"required", List.of("message"), "additionalProperties", false));
		var metadata = new McpToolMeta();
		metadata.setEnabled(true);
		var tools = new McpToolSpecification();
		tools.setTools(List.of(tool));
		tools.setToolsMeta(Map.of(TOOL_NAME, metadata));
		var server = new McpServerDetailInfo();
		server.setName(name);
		server.setProtocol("mcp-streamable");
		server.setRemoteServerConfig(remote);
		server.setToolSpec(tools);
		when(registry.getServerDetail(name)).thenReturn(server);
		var endpoint = new McpEndpointInfo();
		endpoint.setAddress("127.0.0.1");
		endpoint.setPort(port);
		when(registry.selectEndpoint(ref)).thenReturn(endpoint);
		when(registry.getServerEndpoint(name, "1.0.0")).thenReturn(
				new NacosMcpServerEndpoint(List.of(endpoint), path, NacosMcpProtocol.resolve(server), "1.0.0"));
	}

	private static DisposableServer serve(RouterFunction<?> routes) {
		return HttpServer.create().host("127.0.0.1").port(0)
			.handle(new ReactorHttpHandlerAdapter(RouterFunctions.toHttpHandler(routes))).bindNow(TIMEOUT);
	}

	record EchoInput(String message) {
	}

}
