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

package com.alibaba.cloud.ai.autoconfigure.mcp.discovery.client;

import com.alibaba.cloud.ai.mcp.nacos.NacosMcpClientProperties;
import com.alibaba.cloud.ai.mcp.nacos.service.NacosMcpOperationService;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.utils.StringUtils;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

import static com.alibaba.cloud.ai.mcp.nacos.NacosMcpProperties.DEFAULT_ADDRESS;

/**
 * @author yingzi
 * @since 2025/6/4 19:16
 */
@AutoConfiguration
@EnableConfigurationProperties({ NacosMcpClientProperties.class })
public class NacosMcpAutoConfiguration {

	@Bean
	public Map<String, NacosMcpOperationService> nacosMcpOperationServiceMap(NacosMcpClientProperties nacosMcpClientProperties) {
		Map<String, NacosMcpOperationService> map = new HashMap<>();
		nacosMcpClientProperties.getConfigs().forEach((name, nacosSseParameters) -> {
			Properties properties = new Properties();
			properties.put(PropertyKeyConst.NAMESPACE, Objects.toString(nacosSseParameters.namespace(), ""));
			properties.put(PropertyKeyConst.SERVER_ADDR, Objects.toString(nacosSseParameters.serverAddr(), ""));
			properties.put(PropertyKeyConst.USERNAME, Objects.toString(nacosSseParameters.username(), ""));
			properties.put(PropertyKeyConst.PASSWORD, Objects.toString(nacosSseParameters.password(), ""));
			properties.put(PropertyKeyConst.ACCESS_KEY, Objects.toString(nacosSseParameters.accessKey(), ""));
			properties.put(PropertyKeyConst.SECRET_KEY, Objects.toString(nacosSseParameters.secretKey(), ""));
			String endpoint = Objects.toString(nacosSseParameters.endpoint(), "");
			if (endpoint.contains(":")) {
				int index = endpoint.indexOf(":");
				properties.put(PropertyKeyConst.ENDPOINT, endpoint.substring(0, index));
				properties.put(PropertyKeyConst.ENDPOINT_PORT, endpoint.substring(index + 1));
			}
			else {
				properties.put(PropertyKeyConst.ENDPOINT, endpoint);
			}

			if (StringUtils.isEmpty(nacosSseParameters.serverAddr()) && StringUtils.isEmpty(nacosSseParameters.endpoint())) {
				properties.put(PropertyKeyConst.SERVER_ADDR, DEFAULT_ADDRESS);
			}

			try {
				NacosMcpOperationService nacosMcpOperationService = new NacosMcpOperationService(properties);
				map.put(name, nacosMcpOperationService);
			} catch (NacosException e) {
				throw new RuntimeException(e);
			}
		});

		return map;
	}

}
