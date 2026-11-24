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
package com.alibaba.cloud.ai.mcp.register;

import java.util.List;

import com.alibaba.cloud.ai.mcp.nacos.NacosMcpProperties;
import com.alibaba.cloud.ai.mcp.nacos.service.NacosMcpOperationService;
import com.alibaba.cloud.ai.mcp.utils.NacosMcpProtocol;
import com.alibaba.nacos.api.ai.model.mcp.McpEndpointInfo;
import com.alibaba.nacos.api.ai.model.mcp.McpEndpointSpec;
import com.alibaba.nacos.api.ai.model.mcp.McpServerBasicInfo;
import com.alibaba.nacos.api.ai.model.mcp.McpServerDetailInfo;
import com.alibaba.nacos.api.ai.model.mcp.McpServiceRef;
import com.alibaba.nacos.api.ai.model.mcp.McpToolSpecification;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.pojo.Instance;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.ai.mcp.server.common.autoconfigure.properties.McpServerModernProperties;
import org.springframework.ai.mcp.server.common.autoconfigure.properties.McpServerProperties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

class NacosModernMcpRegisterTests {

	@Test
	void usesAStreamableRegistryEntryAndDiscoversTheModernTransport() throws Exception {
		var operation = mock(NacosMcpOperationService.class, CALLS_REAL_METHODS);
		var serverSpec = ArgumentCaptor.forClass(McpServerBasicInfo.class);
		var endpointSpec = ArgumentCaptor.forClass(McpEndpointSpec.class);
		var instance = ArgumentCaptor.forClass(Instance.class);
		var tools = new McpToolSpecification();
		String serviceName = NacosMcpProtocol.modernServiceName("echo::2.1.0");
		doThrow(new NacosException(NacosException.NOT_FOUND, "missing")).when(operation)
			.getServerDetail("echo", "2.1.0");
		doAnswer(invocation -> {
			invocation.<McpEndpointSpec>getArgument(3).getData().put("namespaceId", "public");
			return "id";
		}).when(operation).createMcpServer(eq("echo"), serverSpec.capture(), eq(tools),
				endpointSpec.capture());
		doNothing().when(operation).registerService(eq(serviceName), eq("DEFAULT_GROUP"), instance.capture());

		registrar(operation, tools).register(8101);

		var spec = serverSpec.getValue();
		assertThat(spec.getProtocol()).isEqualTo("mcp-streamable");
		assertThat(spec.getVersionDetail().getVersion()).isEqualTo("2.1.0");
		assertThat(spec.getRemoteServerConfig().getExportPath()).isEqualTo("/app/modern");
		assertThat(endpointSpec.getValue().getData()).containsEntry("serviceName", serviceName);
		assertThat(instance.getValue().getIp()).isEqualTo("127.0.0.1");
		assertThat(instance.getValue().getPort()).isEqualTo(8101);

		var serviceRef = new McpServiceRef();
		serviceRef.setServiceName(serviceName);
		spec.getRemoteServerConfig().setServiceRef(serviceRef);
		var detail = new McpServerDetailInfo();
		detail.setName("echo");
		detail.setProtocol(spec.getProtocol());
		detail.setVersionDetail(spec.getVersionDetail());
		detail.setRemoteServerConfig(spec.getRemoteServerConfig());
		var endpoint = new McpEndpointInfo();
		endpoint.setAddress("127.0.0.1");
		endpoint.setPort(8101);
		detail.setBackendEndpoints(List.of(endpoint));
		doReturn(detail).when(operation).getServerDetail("echo", "2.1.0");
		assertThat(operation.getServerEndpoint("echo", "2.1.0").getProtocol()).isEqualTo(NacosMcpProtocol.MODERN);
		serviceRef.setServiceName("legacy-echo");
		assertThat(operation.getServerEndpoint("echo", "2.1.0").getProtocol()).isEqualTo("mcp-streamable");
	}

	@Test
	void preventsReplacingAnExistingLegacyVersion() throws Exception {
		var operation = mock(NacosMcpOperationService.class);
		var legacy = new McpServerDetailInfo();
		legacy.setProtocol("mcp-sse");
		doReturn(legacy).when(operation).getServerDetail("echo", "2.1.0");
		var registrar = registrar(operation, new McpToolSpecification());

		assertThatIllegalStateException().isThrownBy(() -> registrar.register(8101)).withMessageContaining("legacy");
		verify(operation).getServerDetail("echo", "2.1.0");
		verifyNoMoreInteractions(operation);
	}

	@Test
	void publishesAnAdditionalVersionWhenTheNameAlreadyExists() throws Exception {
		var operation = mock(NacosMcpOperationService.class);
		var tools = new McpToolSpecification();
		doThrow(new NacosException(NacosException.CONFLICT, "name exists")).when(operation)
			.createMcpServer(eq("echo"), any(), eq(tools), any());
		doReturn(true).when(operation).addMcpServerVersion(eq("echo"), any(), eq(tools), any());

		registrar(operation, tools).register(8101);

		var spec = ArgumentCaptor.forClass(McpServerBasicInfo.class);
		verify(operation).addMcpServerVersion(eq("echo"), spec.capture(), eq(tools), any());
		assertThat(spec.getValue().getVersionDetail().getVersion()).isEqualTo("2.1.0");
		verify(operation).registerService(eq(NacosMcpProtocol.modernServiceName("echo::2.1.0")),
				eq("DEFAULT_GROUP"), any());
	}

	private static NacosModernMcpRegister registrar(NacosMcpOperationService operation, McpToolSpecification tools) {
		var server = new McpServerProperties();
		server.setName("echo");
		server.setVersion("2.1.0");
		var modern = new McpServerModernProperties();
		modern.setMcpEndpoint("/modern");
		var register = new NacosMcpRegisterProperties();
		register.setSseExportContextPath("/app");
		register.setHost("127.0.0.1");
		register.setServiceRegister(true);
		return new NacosModernMcpRegister(operation, new NacosMcpProperties(), register, server, modern, () -> tools);
	}

}
