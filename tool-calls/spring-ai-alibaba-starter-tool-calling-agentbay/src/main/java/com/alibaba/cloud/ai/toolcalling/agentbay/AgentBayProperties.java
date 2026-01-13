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

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for AgentBay Tool.
 *
 * @author Spring AI Alibaba
 */
@ConfigurationProperties(prefix = "spring.ai.alibaba.agentbay.tool")
public class AgentBayProperties {

	/**
	 * Whether to enable AgentBay tools.
	 */
	private boolean enabled = true;

	/**
	 * AgentBay API Key. Can also be set via AGENTBAY_API_KEY environment variable.
	 */
	private String apiKey;

	/**
	 * AgentBay region ID.
	 */
	private String regionId = "cn-shanghai";

	/**
	 * AgentBay endpoint.
	 */
	private String endpoint = "wuyingai.cn-shanghai.aliyuncs.com";

	/**
	 * Default image ID for code execution sessions.
	 */
	private String defaultImageId = "linux_latest";

	/**
	 * Request timeout in milliseconds.
	 */
	private int timeoutMs = 60000;

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public String getRegionId() {
		return regionId;
	}

	public void setRegionId(String regionId) {
		this.regionId = regionId;
	}

	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public String getDefaultImageId() {
		return defaultImageId;
	}

	public void setDefaultImageId(String defaultImageId) {
		this.defaultImageId = defaultImageId;
	}

	public int getTimeoutMs() {
		return timeoutMs;
	}

	public void setTimeoutMs(int timeoutMs) {
		this.timeoutMs = timeoutMs;
	}

}

