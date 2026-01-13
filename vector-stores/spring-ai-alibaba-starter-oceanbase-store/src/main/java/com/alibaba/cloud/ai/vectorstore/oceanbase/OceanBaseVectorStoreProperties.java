/*
 * Copyright 2024-2026 the original author or authors.
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
package com.alibaba.cloud.ai.vectorstore.oceanbase;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for OceanBase Vector Store.
 *
 * @author xxsc0529
 */
@ConfigurationProperties(prefix = OceanBaseVectorStoreProperties.CONFIG_PREFIX)
public class OceanBaseVectorStoreProperties {

	public static final String CONFIG_PREFIX = "spring.ai.vectorstore.oceanbase";

	private String url;

	private String username;

	private String password;

	private String tableName;

	private Integer defaultTopK = -1;

	private Double defaultSimilarityThreshold = -1.0;

	private Integer dimension;

	private String hybridSearchType;
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public Integer getDefaultTopK() {
		return defaultTopK;
	}

	public void setDefaultTopK(Integer defaultTopK) {
		this.defaultTopK = defaultTopK;
	}

	public Double getDefaultSimilarityThreshold() {
		return defaultSimilarityThreshold;
	}

	public void setDefaultSimilarityThreshold(Double defaultSimilarityThreshold) {
		this.defaultSimilarityThreshold = defaultSimilarityThreshold;
	}

	public Integer getDimension() {
		return dimension;
	}

	public void setDimension(Integer dimension) {
		this.dimension = dimension;
	}

	public String getHybridSearchType() {
		return hybridSearchType;
	}

	public void setHybridSearchType(String hybridSearchType) {
		this.hybridSearchType = hybridSearchType;
	}

}
