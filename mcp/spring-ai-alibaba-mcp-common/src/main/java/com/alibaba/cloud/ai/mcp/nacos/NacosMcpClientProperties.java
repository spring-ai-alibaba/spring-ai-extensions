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

package com.alibaba.cloud.ai.mcp.nacos;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yingzi
 * @since 2025/10/28
 */
@ConfigurationProperties(NacosMcpClientProperties.CONFIG_PREFIX)
public class NacosMcpClientProperties {

    public static final String CONFIG_PREFIX = "spring.ai.alibaba.mcp.nacos.client";

    private final Map<String, NacosConfig> configs = new HashMap<>();

	private boolean lazyInit = false;

    public Map<String, NacosConfig> getConfigs() {
        return configs;
    }

	public boolean isLazyInit() {
		return lazyInit;
	}

	public void setLazyInit(boolean lazyInit) {
		this.lazyInit = lazyInit;
	}

    public record NacosConfig(String namespace, String serverAddr, String username, String password, String accessKey, String secretKey,
                                     String endpoint) {
    }

}
