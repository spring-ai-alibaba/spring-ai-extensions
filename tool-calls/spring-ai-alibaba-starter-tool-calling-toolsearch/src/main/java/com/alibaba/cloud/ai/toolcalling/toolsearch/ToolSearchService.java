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

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.ToolCallback;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 工具搜索服务
 */
public class ToolSearchService implements Function<ToolSearchService.Request, ToolSearchService.Response> {

	private static final Logger log = LoggerFactory.getLogger(ToolSearchService.class);

	private final ToolSearcher toolSearcher;

	private final int maxResults;

	public ToolSearchService(ToolSearcher toolSearcher, int maxResults) {
		this.toolSearcher = toolSearcher;
		this.maxResults = maxResults;
	}

	@Override
	public Response apply(Request request) {
		// 输入验证
		if (request.query() == null || request.query().trim().isEmpty()) {
			log.warn("Empty query received");
			return new Response(List.of(), 0, "Query cannot be empty");
		}

		String query = request.query().trim();
		// 限制最大结果数，防止过大请求
		int limit = request.maxResults() > 0 ? Math.min(request.maxResults(), 100) : maxResults;

		try {
			log.debug("Searching tools with query: '{}', maxResults: {}", query, limit);

			List<ToolCallback> foundTools = toolSearcher.search(query, limit);

			List<ToolInfo> toolInfos = foundTools.stream().map(tool -> {
				String schema = toolSearcher.getToolSchema(tool);
				return new ToolInfo(tool.getToolDefinition().name(), tool.getToolDefinition().description(), schema);
			}).collect(Collectors.toList());

			log.info("Found {} tools for query: '{}'", toolInfos.size(), query);

			return new Response(toolInfos, toolInfos.size(), null);
		}
		catch (IllegalStateException e) {
			log.error("Tools not indexed", e);
			return new Response(List.of(), 0, "Tools not indexed. Please index tools first.");
		}
		catch (Exception e) {
			log.error("Error searching tools", e);
			return new Response(List.of(), 0, "Search failed: " + e.getMessage());
		}
	}

	/**
	 * 工具搜索请求
	 */
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonClassDescription("Tool search request")
	public record Request(
			@JsonProperty(required = true, value = "query") @JsonPropertyDescription("搜索查询关键词，用于查找相关工具 Search query keywords to find relevant tools") String query,

			@JsonProperty(required = false, value = "max_results") @JsonPropertyDescription("最大返回结果数，默认为5 Maximum number of results to return, default is 5") int maxResults) {
	}

	/**
	 * 工具搜索响应
	 */
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonClassDescription("Tool search response")
	public record Response(
			@JsonProperty(value = "tools") @JsonPropertyDescription("找到的工具列表 List of found tools") List<ToolInfo> tools,

			@JsonProperty(value = "total") @JsonPropertyDescription("找到的工具总数 Total number of tools found") int total,

			@JsonProperty(value = "error") @JsonPropertyDescription("错误信息（如果有） Error message if any") String error) {
	}

	/**
	 * 工具信息
	 */
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonClassDescription("Tool information")
	public record ToolInfo(
			@JsonProperty(value = "name") @JsonPropertyDescription("工具名称 Tool name") String name,

			@JsonProperty(value = "description") @JsonPropertyDescription("工具描述 Tool description") String description,

			@JsonProperty(value = "schema") @JsonPropertyDescription("工具JSON Schema Tool JSON schema") String schema) {
	}

}
