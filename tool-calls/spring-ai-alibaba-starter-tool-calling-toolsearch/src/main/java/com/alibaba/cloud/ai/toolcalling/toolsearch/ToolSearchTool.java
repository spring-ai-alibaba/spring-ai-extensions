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

import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.function.FunctionToolCallback;

/**
 * Tool search tool builder for creating tool search functionality.
 */
public class ToolSearchTool {

	public static final String DEFAULT_TOOL_NAME = "tool_search";

	public static final String DEFAULT_TOOL_DESCRIPTION = """
			Search for available tools by keyword or description.
			Use this when you need a tool but it's not currently available.

			Example queries:
			- "weather" - find weather-related tools
			- "database query" - find tools for querying databases
			- "file operations" - find file manipulation tools
			""";

	/**
	 * Create a builder for the tool search tool.
	 *
	 * @param toolSearcher the tool searcher
	 * @return builder instance
	 */
	public static Builder builder(ToolSearcher toolSearcher) {
		return new Builder(toolSearcher);
	}

	public static class Builder {

		private final ToolSearcher toolSearcher;

		private String name = DEFAULT_TOOL_NAME;

		private String description = DEFAULT_TOOL_DESCRIPTION;

		private int maxResults = ToolSearchConstants.DEFAULT_MAX_RESULTS;

		public Builder(ToolSearcher toolSearcher) {
			this.toolSearcher = toolSearcher;
		}

		public Builder withName(String name) {
			this.name = name;
			return this;
		}

		public Builder withDescription(String description) {
			this.description = description;
			return this;
		}

		public Builder withMaxResults(int maxResults) {
			this.maxResults = maxResults;
			return this;
		}

		/**
		 * Build the ToolCallback.
		 *
		 * @return ToolCallback instance
		 */
		public ToolCallback build() {
			ToolSearchService service = new ToolSearchService(toolSearcher, maxResults);
			return FunctionToolCallback.builder(name, service)
				.description(description)
				.inputType(ToolSearchService.Request.class)
				.build();
		}

	}

}
