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

import com.alibaba.cloud.ai.toolcalling.common.CommonToolCallConstants;

/**
 * Constants definition for Tool Search functionality.
 */
public class ToolSearchConstants {

	/**
	 * Configuration prefix.
	 */
	public static final String CONFIG_PREFIX = CommonToolCallConstants.TOOL_CALLING_CONFIG_PREFIX + ".toolsearch";

	/**
	 * Tool name.
	 */
	public static final String TOOL_NAME = "tool_search";

	/**
	 * Default maximum number of search results.
	 */
	public static final int DEFAULT_MAX_RESULTS = 5;

	/**
	 * Default maximum recursion depth.
	 */
	public static final int DEFAULT_MAX_RECURSION_DEPTH = 3;

	/**
	 * Default field weight - name.
	 */
	public static final float DEFAULT_NAME_BOOST = 3.0f;

	/**
	 * Default field weight - description.
	 */
	public static final float DEFAULT_DESCRIPTION_BOOST = 2.0f;

	/**
	 * Default field weight - parameters.
	 */
	public static final float DEFAULT_PARAMETERS_BOOST = 1.0f;

}
