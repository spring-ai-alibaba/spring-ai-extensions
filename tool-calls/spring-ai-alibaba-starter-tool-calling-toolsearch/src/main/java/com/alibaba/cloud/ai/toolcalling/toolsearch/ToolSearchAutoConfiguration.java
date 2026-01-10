/*
 * Copyright 2024-2025 the original author or authors.
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
package com.alibaba.cloud.ai.toolcalling.toolsearch;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

/**
 * Tool Search 自动配置类
 */
@Configuration
@ConditionalOnClass(ToolSearchService.class)
@EnableConfigurationProperties(ToolSearchProperties.class)
@ConditionalOnProperty(prefix = ToolSearchConstants.CONFIG_PREFIX, name = "enabled", havingValue = "true",
		matchIfMissing = true)
public class ToolSearchAutoConfiguration {

	/**
	 * 创建默认的 Lucene 工具搜索器
	 */
	@Bean
	@ConditionalOnMissingBean(ToolSearcher.class)
	public ToolSearcher toolSearcher(ToolSearchProperties properties) {
		return LuceneToolSearcher.builder()
			.fieldBoost("name", properties.getNameBoost())
			.fieldBoost("description", properties.getDescriptionBoost())
			.fieldBoost("parameters", properties.getParametersBoost())
			.build();
	}

	/**
	 * 创建工具搜索服务
	 */
	@Bean(name = ToolSearchConstants.TOOL_NAME)
	@ConditionalOnMissingBean
	@Description("Search and discover available tools dynamically based on query keywords. "
			+ "使用关键词动态搜索和发现可用工具。")
	public ToolSearchService toolSearchService(ToolSearcher toolSearcher, ToolSearchProperties properties) {
		return new ToolSearchService(toolSearcher, properties.getMaxResults());
	}

	/**
	 * 创建工具自动索引器
	 * 在应用启动时自动发现并索引所有可用的 ToolCallback
	 */
	@Bean
	@ConditionalOnMissingBean
	@ConditionalOnProperty(prefix = ToolSearchConstants.CONFIG_PREFIX, name = "auto-index", havingValue = "true",
			matchIfMissing = true)
	public ToolIndexer toolIndexer(ToolSearcher toolSearcher, ToolSearchProperties properties) {
		return new ToolIndexer(toolSearcher, properties);
	}

}
