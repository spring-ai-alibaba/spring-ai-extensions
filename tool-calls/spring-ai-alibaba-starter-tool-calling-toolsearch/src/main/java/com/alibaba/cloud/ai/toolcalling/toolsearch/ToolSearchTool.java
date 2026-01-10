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

import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.function.FunctionToolCallback;

/**
 * 工具搜索工具构建器
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

			使用关键词或描述搜索可用工具。
			当你需要某个工具但当前不可用时使用此工具。

			示例查询：
			- "天气" - 查找天气相关工具
			- "数据库查询" - 查找数据库查询工具
			- "文件操作" - 查找文件操作工具
			""";

	/**
	 * 创建工具搜索工具的构建器
	 *
	 * @param toolSearcher 工具搜索器
	 * @return 构建器实例
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
		 * 构建 ToolCallback
		 *
		 * @return ToolCallback 实例
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
