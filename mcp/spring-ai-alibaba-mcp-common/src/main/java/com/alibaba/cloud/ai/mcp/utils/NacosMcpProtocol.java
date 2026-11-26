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
package com.alibaba.cloud.ai.mcp.utils;

import com.alibaba.nacos.api.ai.constant.AiConstants;
import com.alibaba.nacos.api.ai.model.mcp.McpServerBasicInfo;
import com.alibaba.nacos.api.ai.model.mcp.McpServiceRef;
import org.jspecify.annotations.Nullable;

/**
 * Decodes Modern MCP for Alibaba clients while keeping the protocol accepted by Nacos
 * for HTTP endpoint registration.
 */
public final class NacosMcpProtocol {

	public static final String MODERN = "mcp-modern";

	private static final String MODERN_SUFFIX = "::mcp-2026-07-28";

	private NacosMcpProtocol() {
	}

	public static String modernServiceName(String serviceName) {
		return serviceName.endsWith(MODERN_SUFFIX) ? serviceName : serviceName + MODERN_SUFFIX;
	}

	public static String resolve(McpServerBasicInfo server) {
		var remote = server.getRemoteServerConfig();
		return resolve(server.getProtocol(), remote == null ? null : remote.getServiceRef());
	}

	public static String resolve(String protocol, @Nullable McpServiceRef serviceRef) {
		if (AiConstants.Mcp.MCP_PROTOCOL_STREAMABLE.equalsIgnoreCase(protocol) && serviceRef != null
				&& serviceRef.getServiceName() != null && serviceRef.getServiceName().endsWith(MODERN_SUFFIX)) {
			return MODERN;
		}
		return protocol;
	}

	public static boolean isModern(@Nullable String protocol) {
		return MODERN.equalsIgnoreCase(protocol);
	}

	public static boolean isStreamable(@Nullable String protocol) {
		return isModern(protocol) || AiConstants.Mcp.MCP_PROTOCOL_STREAMABLE.equalsIgnoreCase(protocol);
	}

}
