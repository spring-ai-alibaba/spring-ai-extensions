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

package com.alibaba.cloud.ai.mcp.gateway.nacos.callback;

import com.alibaba.cloud.ai.mcp.gateway.core.McpGatewayProperties;
import com.alibaba.cloud.ai.mcp.gateway.core.utils.SpringBeanUtils;
import com.alibaba.cloud.ai.mcp.gateway.nacos.definition.NacosMcpGatewayToolDefinition;
import com.alibaba.cloud.ai.mcp.nacos.service.NacosMcpOperationService;
import com.alibaba.nacos.api.ai.model.mcp.McpServerRemoteServiceConfig;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.web.reactive.function.client.WebClient;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for NacosMcpGatewayToolCallback response template processing
 * @author saladday
 */
class NacosMcpGatewayToolCallbackTest {

	private GenericApplicationContext applicationContext;

	private final ObjectMapper objectMapper = new ObjectMapper();

	@BeforeEach
	void setUp() {
		applicationContext = new GenericApplicationContext();
		applicationContext.registerBean(WebClient.Builder.class, WebClient::builder);
		applicationContext.registerBean(NacosMcpOperationService.class, () -> Mockito.mock(NacosMcpOperationService.class));
		applicationContext.refresh();
		SpringBeanUtils.getInstance().setApplicationContext(applicationContext);
	}

	@AfterEach
	void tearDown() {
		if (applicationContext != null) {
			applicationContext.close();
		}
		SpringBeanUtils.getInstance().setApplicationContext(null);
	}

	@Test
	void bodyTemplateWithRootPlaceholderReturnsRawResponse() throws Exception {
		NacosMcpGatewayToolDefinition definition = new NacosMcpGatewayToolDefinition();
		definition.setName("test-tool");
		definition.setDescription("test tool");
		definition.setProtocol("http");
		definition.setRemoteServerConfig(new McpServerRemoteServiceConfig());

		NacosMcpGatewayToolCallback callback = new NacosMcpGatewayToolCallback(definition);

		ObjectNode responseTemplate = objectMapper.createObjectNode();
		responseTemplate.put("body", "{{.}}");

		JsonNode templateNode = responseTemplate;

		Method processResponse = NacosMcpGatewayToolCallback.class
			.getDeclaredMethod("processResponse", String.class, JsonNode.class, Map.class);
		processResponse.setAccessible(true);

		String response = "20.5";
		String result = (String) processResponse.invoke(callback, response, templateNode, Collections.emptyMap());

		assertEquals(response, result);
	}

	@Test
	void getTimeoutDurationReadsConfiguredValue() throws Exception {
		McpGatewayProperties properties = new McpGatewayProperties();
		properties.setToolTimeout(Duration.ofSeconds(60));
		applicationContext.getBeanFactory().registerSingleton("mcpGatewayProperties", properties);

		Duration timeout = invokeGetTimeoutDuration();

		assertEquals(Duration.ofSeconds(60), timeout);
	}

	@Test
	void getTimeoutDurationFallsBackToDefaultWhenNoPropertiesBean() throws Exception {
		// setUp() registers no McpGatewayProperties bean, so the lookup fails and the
		// default 30s timeout is used.
		Duration timeout = invokeGetTimeoutDuration();

		assertEquals(Duration.ofSeconds(30), timeout);
	}

	private Duration invokeGetTimeoutDuration() throws Exception {
		NacosMcpGatewayToolDefinition definition = new NacosMcpGatewayToolDefinition();
		definition.setName("test-tool");
		definition.setDescription("test tool");
		definition.setProtocol("http");
		definition.setRemoteServerConfig(new McpServerRemoteServiceConfig());

		NacosMcpGatewayToolCallback callback = new NacosMcpGatewayToolCallback(definition);

		Method getTimeoutDuration = NacosMcpGatewayToolCallback.class.getDeclaredMethod("getTimeoutDuration");
		getTimeoutDuration.setAccessible(true);
		return (Duration) getTimeoutDuration.invoke(callback);
	}

}
