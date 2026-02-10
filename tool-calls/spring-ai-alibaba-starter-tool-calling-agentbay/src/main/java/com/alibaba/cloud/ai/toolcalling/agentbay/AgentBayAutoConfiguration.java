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
package com.alibaba.cloud.ai.toolcalling.agentbay;

import com.aliyun.agentbay.AgentBay;
import com.aliyun.agentbay.Config;
import com.aliyun.agentbay.exception.AgentBayException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.function.FunctionToolCallback;
import org.springframework.context.annotation.Bean;

/**
 * Auto-configuration for AgentBay Tool.
 *
 * This configuration automatically registers the AgentBay tools when AgentBay SDK
 * is available on the classpath.
 *
 * @author Spring AI Alibaba
 */
@AutoConfiguration
@ConditionalOnClass({ AgentBay.class })
@ConditionalOnProperty(prefix = "spring.ai.alibaba.agentbay.tool", name = "enabled", havingValue = "true",
		matchIfMissing = true)
@EnableConfigurationProperties(AgentBayProperties.class)
public class AgentBayAutoConfiguration {

	private static final Logger logger = LoggerFactory.getLogger(AgentBayAutoConfiguration.class);

	@Bean
	@ConditionalOnMissingBean
	public AgentBay agentBay(AgentBayProperties properties) throws AgentBayException {
		String apiKey = properties.getApiKey();
		if (apiKey == null || apiKey.trim().isEmpty()) {
			apiKey = System.getenv("AGENTBAY_API_KEY");
		}

		if (apiKey == null || apiKey.trim().isEmpty()) {
			throw new IllegalStateException(
					"AgentBay API Key is required. Please set it via spring.ai.alibaba.agentbay.tool.api-key property or AGENTBAY_API_KEY environment variable");
		}

		Config config = new Config();
		config.setRegionId(properties.getRegionId());
		config.setEndpoint(properties.getEndpoint());
		config.setTimeoutMs(properties.getTimeoutMs());

		logger.info("Initializing AgentBay client with region: {}, endpoint: {}", properties.getRegionId(),
				properties.getEndpoint());

		return new AgentBay(apiKey, config);
	}

	@Bean
	@ConditionalOnMissingBean
	public AgentBayToolService agentBayToolService(AgentBay agentBay, AgentBayProperties properties) {
		return new AgentBayToolService(agentBay, properties);
	}

	@Bean(name = "create_sandbox")
	public ToolCallback createSessionCallback(AgentBayToolService service) {
		return FunctionToolCallback.builder("create_sandbox", service.createSessionTool())
			.description("Create a new AgentBay sandbox and return its sandbox_id")
			.inputType(AgentBayToolService.CreateSessionRequest.class)
			.build();
	}

	@Bean(name = "kill_sandbox")
	public ToolCallback deleteSessionCallback(AgentBayToolService service) {
		return FunctionToolCallback.builder("kill_sandbox", service.deleteSessionTool())
			.description("Release sandbox resources when task is finished")
			.inputType(AgentBayToolService.DeleteSessionRequest.class)
			.build();
	}

	@Bean(name = "shell")
	public ToolCallback executeShellCallback(AgentBayToolService service) {
		return FunctionToolCallback.builder("shell", service.executeShellTool())
			.description(
					"Execute shell command in sandbox. The sandbox_id is the identifier for the tool execution environment. This sandbox_id comes from the create_sandbox tool. For multiple commands, use && or ; to chain them in a single line.")
			.inputType(AgentBayToolService.ExecuteShellRequest.class)
			.build();
	}

	@Bean(name = "get_link")
	public ToolCallback getLinkCallback(AgentBayToolService service) {
		return FunctionToolCallback.builder("get_link", service.getLinkTool())
			.description(
					"Get public HTTP link for sandbox port (30100-30199). Returns a URL that can be accessed from the internet.")
			.inputType(AgentBayToolService.GetLinkRequest.class)
			.build();
	}

	@Bean(name = "read_file")
	public ToolCallback readFileCallback(AgentBayToolService service) {
		return FunctionToolCallback.builder("read_file", service.readFileTool())
			.description("Read file content from sandbox filesystem. Returns the file content as string.")
			.inputType(AgentBayToolService.ReadFileRequest.class)
			.build();
	}

	@Bean(name = "write_file")
	public ToolCallback writeFileCallback(AgentBayToolService service) {
		return FunctionToolCallback.builder("write_file", service.writeFileTool())
			.description("Write content to a file in sandbox filesystem. Creates or overwrites the file.")
			.inputType(AgentBayToolService.WriteFileRequest.class)
			.build();
	}

	@Bean(name = "list_files")
	public ToolCallback listFilesCallback(AgentBayToolService service) {
		return FunctionToolCallback.builder("list_files", service.listFilesTool())
			.description("List files and directories in sandbox filesystem. Returns formatted directory listing.")
			.inputType(AgentBayToolService.ListFilesRequest.class)
			.build();
	}

}
