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

import com.alibaba.cloud.ai.mcp.gateway.nacos.properties.NacosMcpGatewayProperties;
import com.alibaba.cloud.ai.mcp.gateway.nacos.watcher.NacosMcpGatewayToolsWatcher;
import org.springframework.beans.factory.DisposableBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base class for multi-server MCP Gateway managers. Manages the lifecycle of
 * per-entry {@link NacosMcpGatewayToolsWatcher} instances and provides utility methods
 * shared across WebMVC and WebFlux implementations.
 *
 * <p>
 * Concrete subclasses are responsible for creating the appropriate transport provider
 * ({@code WebMvcSseServerTransportProvider}, {@code WebMvcStreamableServerTransportProvider},
 * {@code WebFluxSseServerTransportProvider}, or
 * {@code WebFluxStreamableServerTransportProvider}) and the corresponding MCP server
 * instance ({@code McpSyncServer} or {@code McpAsyncServer}).
 *
 * @author vera-qwang
 */
public abstract class MultiServerMcpGatewayManager implements DisposableBean {

	protected final List<NacosMcpGatewayToolsWatcher> watchers = new ArrayList<>();

	/**
	 * Creates a per-entry {@link NacosMcpGatewayProperties} instance containing only the
	 * service names configured for that specific server entry. This ensures tool
	 * isolation between different virtual server endpoints.
	 * @param serviceNames the Nacos service names for this server entry
	 * @return a configured {@link NacosMcpGatewayProperties} instance
	 */
	protected NacosMcpGatewayProperties createNacosProperties(List<String> serviceNames) {
		NacosMcpGatewayProperties props = new NacosMcpGatewayProperties();
		props.setServiceNames(serviceNames);
		return props;
	}

	/**
	 * Stops all registered Nacos watchers and releases their scheduled thread pools.
	 */
	@Override
	public void destroy() {
		watchers.forEach(NacosMcpGatewayToolsWatcher::stop);
	}

}
