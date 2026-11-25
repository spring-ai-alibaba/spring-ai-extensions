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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Stream;

import com.alibaba.cloud.ai.mcp.nacos.NacosMcpProperties;
import com.alibaba.cloud.ai.mcp.nacos.service.NacosMcpOperationService;
import com.alibaba.cloud.ai.mcp.register.NacosMcpRegisterProperties;
import com.alibaba.cloud.ai.mcp.register.NacosModernMcpRegister;
import com.alibaba.nacos.api.ai.model.mcp.McpTool;
import com.alibaba.nacos.api.ai.model.mcp.McpToolMeta;
import com.alibaba.nacos.api.ai.model.mcp.McpToolSpecification;
import com.alibaba.nacos.api.exception.NacosException;
import io.modelcontextprotocol.modern.server.McpServer;
import io.modelcontextprotocol.modern.server.feature.McpAsyncTool;
import io.modelcontextprotocol.modern.server.feature.McpSyncTool;
import org.springframework.ai.mcp.modern.McpSchemaConverter;
import org.springframework.ai.mcp.server.common.autoconfigure.McpServerModernAutoConfiguration;
import org.springframework.ai.mcp.server.common.autoconfigure.properties.McpServerModernProperties;
import org.springframework.ai.mcp.server.common.autoconfigure.properties.McpServerProperties;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration(after = { McpServerModernAutoConfiguration.class, NacosMcpRegisterAutoConfiguration.class })
@ConditionalOnWebApplication
@ConditionalOnBean(McpServer.class)
@ConditionalOnProperty(prefix = NacosMcpRegisterProperties.CONFIG_PREFIX, name = "enabled", havingValue = "true")
@ConditionalOnProperty(prefix = McpServerProperties.CONFIG_PREFIX, name = "protocol", havingValue = "MODERN")
@ConditionalOnProperty(prefix = McpServerProperties.CONFIG_PREFIX, name = "stdio", havingValue = "false",
		matchIfMissing = true)
@EnableConfigurationProperties({ NacosMcpRegisterProperties.class, NacosMcpProperties.class,
		McpServerProperties.class, McpServerModernProperties.class })
public class NacosModernMcpRegisterAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	NacosMcpOperationService modernNacosMcpOperationService(NacosMcpProperties properties) throws NacosException {
		return new NacosMcpOperationService(properties.getNacosProperties());
	}

	@Bean
	NacosModernMcpRegister nacosModernMcpRegister(NacosMcpOperationService service, NacosMcpProperties properties,
			NacosMcpRegisterProperties register, McpServerProperties server, McpServerModernProperties modern,
			ObjectProvider<List<McpSyncTool>> syncTools, ObjectProvider<List<McpAsyncTool>> asyncTools) {
		return new NacosModernMcpRegister(service, properties, register, server, modern, () -> {
			var spec = new McpToolSpecification();
			spec.setTools(Stream.concat(syncTools.orderedStream().flatMap(List::stream).map(McpSyncTool::definition),
					asyncTools.orderedStream().flatMap(List::stream).map(McpAsyncTool::definition))
				.map(tool -> McpSchemaConverter.convert(tool, McpTool.class)).toList());
			var metadata = new LinkedHashMap<String, McpToolMeta>();
			for (McpTool tool : spec.getTools()) {
				var toolMeta = new McpToolMeta();
				toolMeta.setEnabled(true);
				metadata.put(tool.getName(), toolMeta);
			}
			spec.setToolsMeta(metadata);
			return spec;
		});
	}

}
