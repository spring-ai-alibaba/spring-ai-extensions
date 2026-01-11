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

import java.util.List;

/**
 * Tool searcher interface for indexing and searching tools.
 */
public interface ToolSearcher {

	/**
	 * Index a list of tools.
	 * @param tools the list of tools to index
	 */
	void indexTools(List<ToolCallback> tools);

	/**
	 * Search for tools.
	 * @param query the search query
	 * @param maxResults maximum number of results to return
	 * @return list of matching tools
	 */
	List<ToolCallback> search(String query, int maxResults);

	/**
	 * Get the JSON Schema of a tool.
	 * @param tool the tool callback
	 * @return JSON Schema string
	 */
	String getToolSchema(ToolCallback tool);

}
