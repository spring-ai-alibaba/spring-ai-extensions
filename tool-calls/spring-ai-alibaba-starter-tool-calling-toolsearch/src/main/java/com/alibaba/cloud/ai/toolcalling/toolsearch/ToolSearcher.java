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

import java.util.List;

/**
 * 工具搜索器接口
 */
public interface ToolSearcher {

	/**
	 * 索引工具列表
	 * @param tools 要索引的工具列表
	 */
	void indexTools(List<ToolCallback> tools);

	/**
	 * 搜索工具
	 * @param query 搜索查询
	 * @param maxResults 最大返回结果数
	 * @return 匹配的工具列表
	 */
	List<ToolCallback> search(String query, int maxResults);

	/**
	 * 获取工具的 JSON Schema
	 * @param tool 工具回调
	 * @return JSON Schema 字符串
	 */
	String getToolSchema(ToolCallback tool);

}
