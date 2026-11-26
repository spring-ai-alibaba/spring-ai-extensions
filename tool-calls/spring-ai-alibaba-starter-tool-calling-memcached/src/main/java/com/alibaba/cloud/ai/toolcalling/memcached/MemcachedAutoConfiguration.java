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
package com.alibaba.cloud.ai.toolcalling.memcached;

import net.spy.memcached.MemcachedClient;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.function.FunctionToolCallback;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.function.Function;

/**
 * auth: dahua
 */
@Configuration
@ConditionalOnClass(MemcachedClient.class)
@ConditionalOnProperty(prefix = MemcachedConstants.CONFIG_PREFIX, name = "enabled", havingValue = "true",
		matchIfMissing = true)
@EnableConfigurationProperties(MemcachedProperties.class)
public class MemcachedAutoConfiguration {

	private final MemcachedProperties memcachedProperties;

	public MemcachedAutoConfiguration(MemcachedProperties memcachedProperties) {
		this.memcachedProperties = memcachedProperties;
	}

	@Bean
	@ConditionalOnMissingBean
	public MemcachedClient memcachedClient() throws IOException {
		return new MemcachedClient(new InetSocketAddress(memcachedProperties.getIp(), memcachedProperties.getPort()));
	}

	@Bean(name = MemcachedConstants.TOOL_NAME)
	@ConditionalOnMissingBean
	public MemcachedService memcachedService(MemcachedClient memcachedClient) {
		return new MemcachedService(memcachedClient);
	}

	@Bean(name = "memcachedSetterToolCallback")
	@ConditionalOnMissingBean(name = "memcachedSetterToolCallback")
	public ToolCallback memcachedSetterToolCallback(MemcachedService memcachedService) {
		return FunctionToolCallback.builder(MemcachedConstants.TOOL_NAME_SET, memcachedService.setter())
			.description("set data to memcached api")
			.inputType(MemcachedService.MemcachedServiceSetter.Request.class)
			.build();
	}

	@Bean(name = "memcachedGetterToolCallback")
	@ConditionalOnMissingBean(name = "memcachedGetterToolCallback")
	public ToolCallback memcachedGetterToolCallback(MemcachedService memcachedService) {
		@SuppressWarnings({ "rawtypes", "unchecked" })
		Function<MemcachedService.MemcachedServiceGetter.Request, Object> getter = (Function) memcachedService
			.getter();
		return FunctionToolCallback.builder(MemcachedConstants.TOOL_NAME_GET, getter)
			.description("get data from memcached api")
			.inputType(MemcachedService.MemcachedServiceGetter.Request.class)
			.build();
	}

	@Bean(name = "memcachedDeleterToolCallback")
	@ConditionalOnMissingBean(name = "memcachedDeleterToolCallback")
	public ToolCallback memcachedDeleterToolCallback(MemcachedService memcachedService) {
		return FunctionToolCallback.builder(MemcachedConstants.TOOL_NAME_DELETE, memcachedService.deleter())
			.description("delete data from memcached api")
			.inputType(MemcachedService.MemcachedServiceDeleter.Request.class)
			.build();
	}

	@Bean(name = "memcachedReplacerToolCallback")
	@ConditionalOnMissingBean(name = "memcachedReplacerToolCallback")
	public ToolCallback memcachedReplacerToolCallback(MemcachedService memcachedService) {
		return FunctionToolCallback.builder(MemcachedConstants.TOOL_NAME_REPLACE, memcachedService.replacer())
			.description("replace data to memcached api")
			.inputType(MemcachedService.MemcachedServiceReplacer.Request.class)
			.build();
	}

	@Bean(name = "memcachedAppenderToolCallback")
	@ConditionalOnMissingBean(name = "memcachedAppenderToolCallback")
	public ToolCallback memcachedAppenderToolCallback(MemcachedService memcachedService) {
		return FunctionToolCallback.builder(MemcachedConstants.TOOL_NAME_APPEND, memcachedService.appender())
			.description("append data to memcached api")
			.inputType(MemcachedService.MemcachedServiceAppender.Request.class)
			.build();
	}

}
