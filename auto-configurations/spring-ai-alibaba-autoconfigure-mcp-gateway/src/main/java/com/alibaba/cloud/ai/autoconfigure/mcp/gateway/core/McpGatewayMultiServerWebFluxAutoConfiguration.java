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
import io.modelcontextprotocol.server.transport.WebFluxSseServerTransportProvider;
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
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

/**
 * Auto-configuration for the multi-server MCP Gateway in a Spring WebFlux (reactive)
 * environment.
 *
 * <p>
 * Activated when ALL of the following conditions are met:
 * <ul>
 * <li>Running in a reactive web application ({@code @ConditionalOnWebApplication(REACTIVE)})
 * <li>{@code WebFluxSseServerTransportProvider} is on the classpath (i.e.
 * {@code mcp-spring-webflux} dependency is present — already provided transitively via
 * {@code spring-ai-alibaba-mcp-common})
 * <li>{@code spring.ai.alibaba.mcp.gateway.multi-server.enabled=true}</li>
 * </ul>
 *
 * <p>
 * When active, creates a {@link WebFluxMultiServerMcpGatewayManager} that initializes
 * one independent {@code McpAsyncServer} per configured server entry (avoiding blocking
 * calls on Netty event-loop threads), and exposes a combined reactive
 * {@link RouterFunction} bean for Spring WebFlux routing.
 *
 * <p>
 * <b>Required configuration:</b>
 *
 * <pre>{@code
 * spring.ai.mcp.server.enabled: false        # prevent ghost McpAsyncServer Bean
 * spring.ai.alibaba.mcp.gateway.enabled: false  # prevent single-server mode init
 * spring.ai.alibaba.mcp.gateway.multi-server.enabled: true
 * }</pre>
 *
 * @author vera-qwang
 */
@AutoConfiguration(after = { NacosMcpGatewayAutoConfiguration.class })
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
@ConditionalOnClass(WebFluxSseServerTransportProvider.class)
@ConditionalOnProperty(prefix = McpGatewayMultiServerProperties.CONFIG_PREFIX, name = "enabled",
		havingValue = "true")
@EnableConfigurationProperties(McpGatewayMultiServerProperties.class)
public class McpGatewayMultiServerWebFluxAutoConfiguration implements ApplicationContextAware {

	@Override
	public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
		SpringBeanUtils.getInstance().setApplicationContext(applicationContext);
	}

	@Bean
	public WebFluxMultiServerMcpGatewayManager webFluxMultiServerMcpGatewayManager(
			McpGatewayMultiServerProperties properties, NacosMcpOperationService nacosService,
			ObjectMapper objectMapper) {
		return new WebFluxMultiServerMcpGatewayManager(properties, nacosService, objectMapper);
	}

	@Bean
	public RouterFunction<ServerResponse> multiServerWebFluxRouterFunction(
			WebFluxMultiServerMcpGatewayManager manager) {
		RouterFunction<ServerResponse> routerFunction = manager.getCombinedRouterFunction();
		if (routerFunction == null) {
			throw new IllegalStateException(
					"No multi-server entries were successfully initialized. "
							+ "Check spring.ai.alibaba.mcp.gateway.multi-server.servers configuration.");
		}
		return routerFunction;
	}

}
