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

import com.alibaba.cloud.ai.autoconfigure.mcp.gateway.nacos.NacosMcpGatewayAutoConfiguration;
import com.alibaba.cloud.ai.mcp.gateway.core.McpGatewayMultiServerProperties;
import com.alibaba.cloud.ai.mcp.gateway.core.utils.SpringBeanUtils;
import com.alibaba.cloud.ai.mcp.nacos.service.NacosMcpOperationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.modelcontextprotocol.server.transport.WebMvcSseServerTransportProvider;
import org.springframework.lang.NonNull;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

/**
 * Auto-configuration for the multi-server MCP Gateway in a Spring WebMVC (servlet)
 * environment.
 *
 * <p>
 * Activated when ALL of the following conditions are met:
 * <ul>
 * <li>Running in a servlet web application ({@code @ConditionalOnWebApplication(SERVLET)})
 * <li>{@code WebMvcSseServerTransportProvider} is on the classpath (i.e.
 * {@code mcp-spring-webmvc} dependency is present)
 * <li>{@code spring.ai.alibaba.mcp.gateway.multi-server.enabled=true}</li>
 * </ul>
 *
 * <p>
 * When active, creates a {@link WebMvcMultiServerMcpGatewayManager} that initializes one
 * independent {@code McpSyncServer} per configured server entry, and exposes a combined
 * {@link RouterFunction} bean for Spring MVC routing.
 *
 * <p>
 * <b>Required configuration:</b>
 *
 * <pre>{@code
 * spring.ai.mcp.server.enabled: false        # prevent ghost McpSyncServer Bean
 * spring.ai.alibaba.mcp.gateway.enabled: false  # prevent single-server mode init
 * spring.ai.alibaba.mcp.gateway.multi-server.enabled: true
 * }</pre>
 *
 * @author vera-qwang
 */
@AutoConfiguration(after = { NacosMcpGatewayAutoConfiguration.class })
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnClass(WebMvcSseServerTransportProvider.class)
@ConditionalOnProperty(prefix = McpGatewayMultiServerProperties.CONFIG_PREFIX, name = "enabled",
		havingValue = "true")
@EnableConfigurationProperties(McpGatewayMultiServerProperties.class)
public class McpGatewayMultiServerWebMvcAutoConfiguration implements ApplicationContextAware {

	/**
	 * Initializes {@link SpringBeanUtils} with the Spring {@link ApplicationContext}.
	 * This is required by {@code NacosMcpGatewayToolCallback} which uses
	 * {@code SpringBeanUtils.getBean()} to resolve the shared {@code ObjectMapper}.
	 * When the default single-server gateway is disabled
	 * ({@code spring.ai.alibaba.mcp.gateway.enabled=false}),
	 * {@code McpGatewayServerAutoConfiguration} (which normally does this setup) is
	 * skipped, so we must initialize it here.
	 */
	@Override
	public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
		SpringBeanUtils.getInstance().setApplicationContext(applicationContext);
	}

	@Bean
	public WebMvcMultiServerMcpGatewayManager webMvcMultiServerMcpGatewayManager(
			McpGatewayMultiServerProperties properties, NacosMcpOperationService nacosService,
			ObjectMapper objectMapper) {
		return new WebMvcMultiServerMcpGatewayManager(properties, nacosService, objectMapper);
	}

	@Bean
	public RouterFunction<ServerResponse> multiServerWebMvcRouterFunction(
			WebMvcMultiServerMcpGatewayManager manager) {
		RouterFunction<ServerResponse> routerFunction = manager.getCombinedRouterFunction();
		if (routerFunction == null) {
			throw new IllegalStateException(
					"No multi-server entries were successfully initialized. "
							+ "Check spring.ai.alibaba.mcp.gateway.multi-server.servers configuration.");
		}
		return routerFunction;
	}

}
