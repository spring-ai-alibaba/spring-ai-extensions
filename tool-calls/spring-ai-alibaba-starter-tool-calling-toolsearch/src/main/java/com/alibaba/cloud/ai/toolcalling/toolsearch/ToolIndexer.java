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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Automatic tool indexer that indexes all available ToolCallback beans on application startup.
 */
public class ToolIndexer implements ApplicationListener<ContextRefreshedEvent> {

	private static final Logger log = LoggerFactory.getLogger(ToolIndexer.class);

	private final ToolSearcher toolSearcher;

	private final ToolSearchProperties properties;

	private volatile boolean indexed = false;

	public ToolIndexer(ToolSearcher toolSearcher, ToolSearchProperties properties) {
		this.toolSearcher = toolSearcher;
		this.properties = properties;
	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		// Avoid duplicate indexing
		if (indexed) {
			log.debug("Tools already indexed, skipping");
			return;
		}

		if (!properties.isEnabled()) {
			log.info("Tool search is disabled, skipping auto-indexing");
			return;
		}

		ApplicationContext context = event.getApplicationContext();

		try {
			// Collect all ToolCallback beans
			Map<String, ToolCallback> toolBeans = context.getBeansOfType(ToolCallback.class);

			if (toolBeans.isEmpty()) {
				log.warn("No ToolCallback beans found in application context");
				return;
			}

			List<ToolCallback> tools = new ArrayList<>(toolBeans.values());

			// Filter out tool_search itself to avoid circular reference
			tools.removeIf(tool -> ToolSearchConstants.TOOL_NAME.equals(tool.getToolDefinition().name()));

			if (tools.isEmpty()) {
				log.warn("No tools to index after filtering tool_search itself");
				return;
			}

			log.info("Auto-indexing {} tools...", tools.size());

			// Index all tools
			toolSearcher.indexTools(tools);

			indexed = true;

			log.info("Successfully auto-indexed {} tools", tools.size());

			// Print the list of indexed tools
			if (log.isDebugEnabled()) {
				tools.forEach(tool -> log.debug("  - {}: {}", tool.getToolDefinition().name(),
						tool.getToolDefinition().description()));
			}
		}
		catch (Exception e) {
			log.error("Failed to auto-index tools", e);
		}
	}

}
