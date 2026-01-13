/*
 * Copyright 2025-2026 the original author or authors.
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
 * Auto-configuration class for Tool Search functionality.
 */
@Configuration
@ConditionalOnClass(ToolSearchService.class)
@EnableConfigurationProperties(ToolSearchProperties.class)
@ConditionalOnProperty(prefix = ToolSearchConstants.CONFIG_PREFIX, name = "enabled", havingValue = "true",
		matchIfMissing = true)
public class ToolSearchAutoConfiguration {

	/**
	 * Creates default Lucene tool searcher.
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
	 * Creates tool search service.
	 */
	@Bean(name = ToolSearchConstants.TOOL_NAME)
	@ConditionalOnMissingBean
	@Description("Search and discover available tools dynamically based on query keywords. "
			+ "Dynamically search and discover available tools using keywords.")
	public ToolSearchService toolSearchService(ToolSearcher toolSearcher, ToolSearchProperties properties) {
		return new ToolSearchService(toolSearcher, properties.getMaxResults());
	}

	/**
	 * Creates automatic tool indexer.
	 * Automatically discovers and indexes all available ToolCallback on application startup.
	 */
	@Bean
	@ConditionalOnMissingBean
	@ConditionalOnProperty(prefix = ToolSearchConstants.CONFIG_PREFIX, name = "auto-index", havingValue = "true",
			matchIfMissing = true)
	public ToolIndexer toolIndexer(ToolSearcher toolSearcher, ToolSearchProperties properties) {
		return new ToolIndexer(toolSearcher, properties);
	}

}
