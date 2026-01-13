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

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for Tool Search functionality.
 */
@ConfigurationProperties(prefix = ToolSearchConstants.CONFIG_PREFIX)
public class ToolSearchProperties {

	/**
	 * Whether to enable tool search functionality.
	 */
	private boolean enabled = true;

	/**
	 * Default maximum number of search results.
	 */
	private int maxResults = ToolSearchConstants.DEFAULT_MAX_RESULTS;

	/**
	 * Field weight configuration - name field.
	 */
	private float nameBoost = ToolSearchConstants.DEFAULT_NAME_BOOST;

	/**
	 * Field weight configuration - description field.
	 */
	private float descriptionBoost = ToolSearchConstants.DEFAULT_DESCRIPTION_BOOST;

	/**
	 * Field weight configuration - parameters field.
	 */
	private float parametersBoost = ToolSearchConstants.DEFAULT_PARAMETERS_BOOST;

	/**
	 * Whether to automatically index all available tools on application startup.
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
