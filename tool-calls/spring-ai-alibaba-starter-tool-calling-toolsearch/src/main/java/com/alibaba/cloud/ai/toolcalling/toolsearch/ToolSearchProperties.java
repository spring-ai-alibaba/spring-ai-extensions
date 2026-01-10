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

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Tool Search 工具配置属性
 */
@ConfigurationProperties(prefix = ToolSearchConstants.CONFIG_PREFIX)
public class ToolSearchProperties {

	/**
	 * 是否启用工具搜索功能
	 */
	private boolean enabled = true;

	/**
	 * 默认最大搜索结果数
	 */
	private int maxResults = ToolSearchConstants.DEFAULT_MAX_RESULTS;

	/**
	 * 字段权重配置 - 名称字段
	 */
	private float nameBoost = ToolSearchConstants.DEFAULT_NAME_BOOST;

	/**
	 * 字段权重配置 - 描述字段
	 */
	private float descriptionBoost = ToolSearchConstants.DEFAULT_DESCRIPTION_BOOST;

	/**
	 * 字段权重配置 - 参数字段
	 */
	private float parametersBoost = ToolSearchConstants.DEFAULT_PARAMETERS_BOOST;

	/**
	 * 是否在应用启动时自动索引所有可用工具
	 */
	private boolean autoIndex = true;

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public int getMaxResults() {
		return maxResults;
	}

	public void setMaxResults(int maxResults) {
		this.maxResults = maxResults;
	}

	public float getNameBoost() {
		return nameBoost;
	}

	public void setNameBoost(float nameBoost) {
		this.nameBoost = nameBoost;
	}

	public float getDescriptionBoost() {
		return descriptionBoost;
	}

	public void setDescriptionBoost(float descriptionBoost) {
		this.descriptionBoost = descriptionBoost;
	}

	public float getParametersBoost() {
		return parametersBoost;
	}

	public void setParametersBoost(float parametersBoost) {
		this.parametersBoost = parametersBoost;
	}

	public boolean isAutoIndex() {
		return autoIndex;
	}

	public void setAutoIndex(boolean autoIndex) {
		this.autoIndex = autoIndex;
	}

}
