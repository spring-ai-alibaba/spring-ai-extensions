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

import com.alibaba.cloud.ai.toolcalling.common.CommonToolCallConstants;

/**
 * Tool Search 工具常量定义
 */
public class ToolSearchConstants {

	/**
	 * 配置前缀
	 */
	public static final String CONFIG_PREFIX = CommonToolCallConstants.TOOL_CALLING_CONFIG_PREFIX + ".toolsearch";

	/**
	 * 工具名称
	 */
	public static final String TOOL_NAME = "tool_search";

	/**
	 * 默认最大搜索结果数
	 */
	public static final int DEFAULT_MAX_RESULTS = 5;

	/**
	 * 默认最大递归深度
	 */
	public static final int DEFAULT_MAX_RECURSION_DEPTH = 3;

	/**
	 * 默认字段权重 - 名称
	 */
	public static final float DEFAULT_NAME_BOOST = 3.0f;

	/**
	 * 默认字段权重 - 描述
	 */
	public static final float DEFAULT_DESCRIPTION_BOOST = 2.0f;

	/**
	 * 默认字段权重 - 参数
	 */
	public static final float DEFAULT_PARAMETERS_BOOST = 1.0f;

}
