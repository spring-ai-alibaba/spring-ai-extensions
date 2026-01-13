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

import com.alibaba.cloud.ai.mcp.gateway.core.McpGatewayToolCallbackProvider;
import com.alibaba.cloud.ai.mcp.gateway.core.McpGatewayToolsInitializer;
import com.alibaba.cloud.ai.mcp.gateway.core.utils.SpringBeanUtils;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.spec.McpServerTransportProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.mcp.server.common.autoconfigure.McpServerAutoConfiguration;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.lang.NonNull;

/**
 * Autoconfiguration for MCP Server Bean initialization compatible with v0.11.0. This
 * configuration provides a compatibility layer for Spring AI's MCP Server by creating
 * McpServer beans that work with MCP Java SDK v0.11.0.
 *
 * @author aias00
 */
@AutoConfiguration(after = { McpServerAutoConfiguration.class })
@ConditionalOnClass({ McpServer.class, McpServerTransportProvider.class })
@ConditionalOnProperty(name = "spring.ai.alibaba.mcp.gateway.enabled", havingValue = "true", matchIfMissing = false)
public class McpGatewayServerAutoConfiguration implements ApplicationContextAware {

	private static final Logger log = LoggerFactory.getLogger(McpGatewayServerAutoConfiguration.class);

	@Override
	public void setApplicationContext(@NonNull final ApplicationContext applicationContext) throws BeansException {
		SpringBeanUtils.getInstance().setApplicationContext(applicationContext);
	}

	@Bean
	public ToolCallbackProvider callbackProvider(final McpGatewayToolsInitializer mcpGatewayToolsInitializer) {
		return McpGatewayToolCallbackProvider.builder()
			.toolCallbacks(mcpGatewayToolsInitializer.initializeTools())
			.build();
	}

}
