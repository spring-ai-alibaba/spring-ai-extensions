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

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import com.alibaba.cloud.ai.mcp.nacos.NacosMcpProperties;
import com.alibaba.cloud.ai.mcp.nacos.service.NacosMcpOperationService;
import com.alibaba.cloud.ai.mcp.utils.NacosMcpProtocol;
import com.alibaba.nacos.api.ai.constant.AiConstants;
import com.alibaba.nacos.api.ai.model.mcp.McpEndpointSpec;
import com.alibaba.nacos.api.ai.model.mcp.McpServerBasicInfo;
import com.alibaba.nacos.api.ai.model.mcp.McpServerRemoteServiceConfig;
import com.alibaba.nacos.api.ai.model.mcp.McpToolSpecification;
import com.alibaba.nacos.api.ai.model.mcp.registry.ServerVersionDetail;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.api.ai.model.mcp.McpServerDetailInfo;
import com.alibaba.nacos.api.utils.NetUtils;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.mcp.server.common.autoconfigure.properties.McpServerModernProperties;
import org.springframework.ai.mcp.server.common.autoconfigure.properties.McpServerProperties;
import org.springframework.boot.web.server.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public final class NacosModernMcpRegister implements ApplicationListener<WebServerInitializedEvent> {

	private static final Logger logger = LoggerFactory.getLogger(NacosModernMcpRegister.class);

	private final NacosMcpOperationService service;

	private final NacosMcpProperties nacosProperties;

	private final NacosMcpRegisterProperties registerProperties;

	private final McpServerProperties server;

	private final McpServerModernProperties modern;

	private final Supplier<McpToolSpecification> tools;

	public NacosModernMcpRegister(NacosMcpOperationService service, NacosMcpProperties nacosProperties,
			NacosMcpRegisterProperties registerProperties, McpServerProperties server,
			McpServerModernProperties modern, Supplier<McpToolSpecification> tools) {
		this.service = service;
		this.nacosProperties = nacosProperties;
		this.registerProperties = registerProperties;
		this.server = server;
		this.modern = modern;
		this.tools = tools;
		Assert.hasText(server.getVersion(), "MCP server version must be configured");
	}

	@Override
	public void onApplicationEvent(WebServerInitializedEvent event) {
		if ("management".equals(event.getApplicationContext().getServerNamespace())) {
			return;
		}
		try {
			register(event.getWebServer().getPort());
		}
		catch (NacosException ex) {
			logger.error("Failed to register Modern MCP server {}", this.server.getName(), ex);
		}
	}

	public void register(int webServerPort) throws NacosException {
		String serviceName = NacosMcpProtocol.modernServiceName(hasTextOr(this.registerProperties.getServiceName(),
				this.server.getName() + "::" + this.server.getVersion()));
		String group = hasTextOr(this.registerProperties.getServiceGroup(), "DEFAULT_GROUP");
		McpServerDetailInfo existing = findVersion();
		if (existing == null) {
			existing = createOrPublishVersion(serviceName, group);
		}
		if (existing != null) {
			if (!NacosMcpProtocol.isModern(NacosMcpProtocol.resolve(existing))) {
				throw new IllegalStateException("Existing Nacos MCP service " + this.server.getName()
						+ " uses a legacy protocol; configure a distinct name or version for Modern MCP");
			}
			var remote = existing.getRemoteServerConfig();
			if (remote == null || remote.getServiceRef() == null) {
				throw new IllegalStateException("Nacos MCP service requires a Naming service reference: "
						+ this.server.getName());
			}
			var ref = remote.getServiceRef();
			serviceName = ref.getServiceName();
			group = hasTextOr(ref.getGroupName(), group);
		}

		if (this.registerProperties.isServiceRegister()) {
			var instance = new Instance();
			instance.setIp(hasTextOr(this.registerProperties.getHost(),
					hasTextOr(this.nacosProperties.getIp(), NetUtils.localIp())));
			int configuredPort = this.registerProperties.getPort();
			instance.setPort(configuredPort > 0 && configuredPort <= 65535 ? configuredPort : webServerPort);
			instance.setEphemeral(this.registerProperties.isServiceEphemeral());
			this.service.registerService(serviceName, group, instance);
		}
	}

	private @Nullable McpServerDetailInfo findVersion() throws NacosException {
		try {
			return this.service.getServerDetail(this.server.getName(), this.server.getVersion());
		}
		catch (NacosException ex) {
			if (ex.getErrCode() == NacosException.NOT_FOUND) {
				return null;
			}
			throw ex;
		}
	}

	private @Nullable McpServerDetailInfo createOrPublishVersion(String serviceName, String group) throws NacosException {
		var version = new ServerVersionDetail();
		version.setVersion(this.server.getVersion());
		var serverInfo = new McpServerBasicInfo();
		serverInfo.setName(this.server.getName());
		serverInfo.setDescription(hasTextOr(this.server.getInstructions(), this.server.getName()));
		serverInfo.setVersionDetail(version);
		serverInfo.setProtocol(AiConstants.Mcp.MCP_PROTOCOL_STREAMABLE);
		serverInfo.setFrontProtocol(AiConstants.Mcp.MCP_PROTOCOL_STREAMABLE);

		var remote = new McpServerRemoteServiceConfig();
		remote.setExportPath(hasTextOr(this.registerProperties.getSseExportContextPath(), "")
				+ this.modern.getMcpEndpoint());
		serverInfo.setRemoteServerConfig(remote);
		var endpoint = new McpEndpointSpec();
		endpoint.setType(AiConstants.Mcp.MCP_ENDPOINT_TYPE_REF);
		endpoint.setData(new HashMap<>(Map.of("serviceName", serviceName, "groupName", group)));
		McpToolSpecification toolSpec = this.tools.get();
		try {
			this.service.createMcpServer(this.server.getName(), serverInfo, toolSpec, endpoint);
		}
		catch (NacosException ex) {
			if (ex.getErrCode() != NacosException.CONFLICT) {
				throw ex;
			}
			McpServerDetailInfo concurrentVersion = findVersion();
			if (concurrentVersion != null) {
				return concurrentVersion;
			}
			if (!this.service.addMcpServerVersion(this.server.getName(), serverInfo, toolSpec, endpoint)) {
				throw new IllegalStateException("Failed to publish Nacos MCP version " + this.server.getVersion());
			}
		}
		return null;
	}

	private static String hasTextOr(@Nullable String value, String fallback) {
		return StringUtils.hasText(value) ? value : fallback;
	}

}
