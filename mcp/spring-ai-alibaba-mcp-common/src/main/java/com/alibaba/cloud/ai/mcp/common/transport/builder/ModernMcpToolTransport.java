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
package com.alibaba.cloud.ai.mcp.common.transport.builder;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.Map;
import java.util.function.Function;

import com.alibaba.cloud.ai.mcp.utils.NacosMcpClientUtil;
import com.alibaba.nacos.api.ai.model.mcp.McpEndpointInfo;
import io.modelcontextprotocol.json.McpJsonMapper;
import io.modelcontextprotocol.json.TypeRef;
import io.modelcontextprotocol.modern.client.McpAsyncClient;
import io.modelcontextprotocol.modern.client.McpClient;
import io.modelcontextprotocol.modern.client.transport.HttpClientMcpTransport;
import io.modelcontextprotocol.spec.McpClientTransport;
import io.modelcontextprotocol.spec.McpSchema;
import io.modelcontextprotocol.spec.McpSchema.JSONRPCMessage;
import io.modelcontextprotocol.spec.McpSchema.JSONRPCRequest;
import io.modelcontextprotocol.spec.McpSchema.JSONRPCResponse;
import io.modelcontextprotocol.spec.McpSchema.JSONRPCResponse.JSONRPCError;
import org.jspecify.annotations.Nullable;
import org.springframework.ai.mcp.modern.McpSchemaConverter;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

/**
 * Adapts the Nacos tool client contract to a modern HTTP peer. Initialization stays
 * local to the existing client API; on the wire it uses server discovery.
 */
public final class ModernMcpToolTransport implements McpClientTransport {

	private final McpJsonMapper mapper;

	private final McpAsyncClient client;

	private final String serverName;

	private volatile @Nullable Function<Mono<JSONRPCMessage>, Mono<JSONRPCMessage>> handler;

	public ModernMcpToolTransport(McpEndpointInfo endpoint, @Nullable String exportPath, String serverName,
			McpJsonMapper mapper, Duration timeout) {
		this(endpointUri(endpoint, exportPath), serverName, mapper, timeout);
	}

	public ModernMcpToolTransport(URI endpoint, String serverName, McpJsonMapper mapper, Duration timeout) {
		this.mapper = mapper;
		this.serverName = serverName;
		var transport = HttpClientMcpTransport.builder(endpoint).jsonMapper(mapper).connectTimeout(timeout).build();
		this.client = McpClient.async(transport).jsonMapper(mapper).roundTimeout(timeout).callTimeout(timeout).build();
	}

	public static URI endpointUri(McpEndpointInfo endpoint, @Nullable String exportPath) {
		String path = StringUtils.hasText(exportPath) ? exportPath : endpoint.getPath();
		try {
			return new URI(NacosMcpClientUtil.checkProtocol(endpoint), null, endpoint.getAddress(), endpoint.getPort(),
					StringUtils.hasText(path) ? path : "/mcp", null, null);
		}
		catch (URISyntaxException ex) {
			throw new IllegalArgumentException("Invalid MCP endpoint", ex);
		}
	}

	@Override
	public Mono<Void> connect(Function<Mono<JSONRPCMessage>, Mono<JSONRPCMessage>> handler) {
		return Mono.fromRunnable(() -> this.handler = handler);
	}

	@Override
	public Mono<Void> sendMessage(JSONRPCMessage message) {
		return Mono.defer(() -> {
			var receiver = this.handler;
			if (receiver == null) {
				return Mono.error(new IllegalStateException("MCP client transport is disconnected"));
			}
			if (!(message instanceof JSONRPCRequest request)) {
				return Mono.empty();
			}
			return response(request)
				.map(result -> JSONRPCResponse.result(request.id(), result))
				.onErrorResume(UnsupportedOperationException.class, ex -> Mono.just(JSONRPCResponse.error(request.id(),
						new JSONRPCError(-32601, "MCP method is unavailable through the tools adapter"))))
				.onErrorResume(ex -> Mono.just(JSONRPCResponse.error(request.id(),
						new JSONRPCError(-32603, "Modern MCP request failed"))))
				.flatMap(reply -> receiver.apply(Mono.just(reply))).then();
		});
	}

	private Mono<?> response(JSONRPCRequest request) {
		return Mono.defer(() -> switch (request.method()) {
			case "initialize" -> initialize(request.params());
			case "ping" -> this.client.discover().thenReturn(Map.of());
			case "tools/list" -> listTools(request.params());
			case "tools/call" -> callTool(request.params());
			default -> Mono.error(new UnsupportedOperationException(request.method()));
		});
	}

	private Mono<McpSchema.InitializeResult> initialize(Object params) {
		var request = unmarshalFrom(params, new TypeRef<McpSchema.InitializeRequest>() { });
		return this.client.discover().map(discovery -> new McpSchema.InitializeResult(request.protocolVersion(),
				McpSchema.ServerCapabilities.builder().tools(false).build(),
				new McpSchema.Implementation(this.serverName, "2026-07-28"), discovery.instructions()));
	}

	private Mono<McpSchema.ListToolsResult> listTools(@Nullable Object params) {
		var pagination = params == null ? null : unmarshalFrom(params, new TypeRef<McpSchema.PaginatedRequest>() { });
		var tools = this.client.tools();
		if (pagination != null && pagination.cursor() != null) {
			tools.cursor(pagination.cursor());
		}
		return tools.list().map(result -> new McpSchema.ListToolsResult(result.tools().stream()
			.map(tool -> McpSchemaConverter.convert(tool, McpSchema.Tool.class)).toList(), result.nextCursor()));
	}

	private Mono<McpSchema.CallToolResult> callTool(Object params) {
		var request = unmarshalFrom(params, new TypeRef<McpSchema.CallToolRequest>() { });
		var call = this.client.tool(request.name()).arguments(request.arguments());
		if (request.meta() != null && !request.meta().isEmpty()) {
			call.meta(request.meta());
		}
		return call.call().map(result -> McpSchemaConverter.convert(result, McpSchema.CallToolResult.class));
	}

	@Override
	public <T> T unmarshalFrom(Object data, TypeRef<T> type) {
		return this.mapper.convertValue(data, type);
	}

	@Override
	public Mono<Void> closeGracefully() {
		return Mono.fromRunnable(() -> this.handler = null);
	}

}
