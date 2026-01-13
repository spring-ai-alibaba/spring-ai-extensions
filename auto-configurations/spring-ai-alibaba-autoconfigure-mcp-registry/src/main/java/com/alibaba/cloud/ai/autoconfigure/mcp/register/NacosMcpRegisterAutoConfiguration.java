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

package com.alibaba.cloud.ai.autoconfigure.mcp.register;

import com.alibaba.cloud.ai.mcp.nacos.NacosMcpProperties;
import com.alibaba.cloud.ai.mcp.nacos.service.NacosMcpOperationService;
import com.alibaba.cloud.ai.mcp.register.NacosMcpRegister;
import com.alibaba.cloud.ai.mcp.register.NacosMcpRegisterProperties;
import com.alibaba.nacos.api.ai.constant.AiConstants;
import com.alibaba.nacos.api.exception.NacosException;
import io.modelcontextprotocol.server.McpAsyncServer;
import io.modelcontextprotocol.server.McpSyncServer;
import io.modelcontextprotocol.server.transport.StdioServerTransportProvider;
import io.modelcontextprotocol.spec.McpServerTransportProviderBase;
import org.springframework.ai.mcp.server.common.autoconfigure.McpServerAutoConfiguration;
import org.springframework.ai.mcp.server.common.autoconfigure.properties.McpServerProperties;
import org.springframework.ai.mcp.server.common.autoconfigure.properties.McpServerSseProperties;
import org.springframework.ai.mcp.server.common.autoconfigure.properties.McpServerStreamableHttpProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;

import java.util.Properties;

/**
 * @author Sunrisea
 */
@EnableConfigurationProperties({ NacosMcpRegisterProperties.class, NacosMcpProperties.class,
		McpServerProperties.class, McpServerSseProperties.class, McpServerStreamableHttpProperties.class})
@AutoConfiguration(after = McpServerAutoConfiguration.class)
@ConditionalOnProperty(prefix = NacosMcpRegisterProperties.CONFIG_PREFIX, name = "enabled", havingValue = "true",
		matchIfMissing = false)
@Conditional(McpServerAutoConfiguration.NonStatelessServerCondition.class)
public class NacosMcpRegisterAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean(NacosMcpOperationService.class)
	public NacosMcpOperationService nacosMcpOperationService(NacosMcpProperties nacosMcpProperties) {
		Properties nacosProperties = nacosMcpProperties.getNacosProperties();
		try {
			return new NacosMcpOperationService(nacosProperties);
		}
		catch (NacosException e) {
			throw new RuntimeException(e);
		}
	}

	@Bean
	@ConditionalOnProperty(prefix = McpServerProperties.CONFIG_PREFIX, name = "type", havingValue = "SYNC",
			matchIfMissing = true)
	public NacosMcpRegister nacosMcpRegisterSync(NacosMcpOperationService nacosMcpOperationService,
												 McpSyncServer mcpSyncServer, NacosMcpProperties nacosMcpProperties,
												 NacosMcpRegisterProperties nacosMcpRegistryProperties, McpServerProperties mcpServerProperties, McpServerSseProperties mcpServerSseProperties,
												 ApplicationContext applicationContext, McpServerTransportProviderBase mcpServerTransportBase) {
		McpAsyncServer mcpAsyncServer = mcpSyncServer.getAsyncServer();
		return getNacosMcpRegister(nacosMcpOperationService, mcpAsyncServer, nacosMcpProperties,
				nacosMcpRegistryProperties, mcpServerProperties, mcpServerSseProperties, applicationContext, mcpServerTransportBase);
	}

	@Bean
	@ConditionalOnProperty(prefix = McpServerProperties.CONFIG_PREFIX, name = "type", havingValue = "ASYNC")
	public NacosMcpRegister nacosMcpRegisterAsync(NacosMcpOperationService nacosMcpOperationService,
												  McpAsyncServer mcpAsyncServer, NacosMcpProperties nacosMcpProperties,
												  NacosMcpRegisterProperties nacosMcpRegistryProperties, McpServerProperties mcpServerProperties,McpServerSseProperties mcpServerSseProperties,
												  ApplicationContext applicationContext,McpServerTransportProviderBase mcpServerTransportBase) {
		return getNacosMcpRegister(nacosMcpOperationService, mcpAsyncServer, nacosMcpProperties,
				nacosMcpRegistryProperties, mcpServerProperties, mcpServerSseProperties, applicationContext,mcpServerTransportBase);
	}

	private NacosMcpRegister getNacosMcpRegister(NacosMcpOperationService nacosMcpOperationService,
												 McpAsyncServer mcpAsyncServer, NacosMcpProperties nacosMcpProperties,
												 NacosMcpRegisterProperties nacosMcpRegistryProperties, McpServerProperties mcpServerProperties, McpServerSseProperties mcpServerSseProperties,
												 ApplicationContext applicationContext,McpServerTransportProviderBase mcpServerTransportBase) {
		if (mcpServerTransportBase instanceof StdioServerTransportProvider) {
			return new NacosMcpRegister(nacosMcpOperationService, mcpAsyncServer, nacosMcpProperties,
					nacosMcpRegistryProperties, mcpServerProperties, mcpServerSseProperties, applicationContext,AiConstants.Mcp.MCP_PROTOCOL_STDIO);
		} else if (mcpServerProperties.getProtocol() == McpServerProperties.ServerProtocol.SSE){
			return new NacosMcpRegister(nacosMcpOperationService, mcpAsyncServer, nacosMcpProperties,
					nacosMcpRegistryProperties, mcpServerProperties, mcpServerSseProperties, applicationContext,AiConstants.Mcp.MCP_PROTOCOL_SSE);
		} else {
			return new NacosMcpRegister(nacosMcpOperationService, mcpAsyncServer, nacosMcpProperties,
					nacosMcpRegistryProperties, mcpServerProperties, mcpServerSseProperties, applicationContext,AiConstants.Mcp.MCP_PROTOCOL_STREAMABLE);
		}
	}

}
