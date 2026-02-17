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
package com.alibaba.cloud.ai.vectorstore.opensearch.autoconfigure;


import org.jspecify.annotations.Nullable;
import org.springframework.ai.vectorstore.properties.CommonVectorStoreProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 北极星
 */

@ConfigurationProperties(prefix = OpenSearchVectorStoreProperties.DEFAULT_ALIBABA_OPENSEARCH_CONFIG_PREFIX)
public class OpenSearchVectorStoreProperties extends CommonVectorStoreProperties {

	protected static final String DEFAULT_ALIBABA_OPENSEARCH_CONFIG_PREFIX = "spring.ai.vectorstore.opensearch";

	private @Nullable Boolean enabled;

	private @Nullable String instanceId;

	private @Nullable String endpoint;

	private @Nullable String accessUserName;

	private @Nullable String accessPassWord;

	private @Nullable String mappingJson;

	private @Nullable String tableName;

	private @Nullable String primaryKeyField;

	private @Nullable String index;

	private @Nullable String similarityFunction;

	private List<String> outputFields = List.of();

	private @Nullable Integer dimensions;


	public @Nullable Boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public @Nullable String getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}

	public @Nullable String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public @Nullable String getAccessUserName() {
		return accessUserName;
	}

	public void setAccessUserName(String accessUserName) {
		this.accessUserName = accessUserName;
	}

	public @Nullable String getAccessPassWord() {
		return accessPassWord;
	}

	public void setAccessPassWord(String accessPassWord) {
		this.accessPassWord = accessPassWord;
	}

	public @Nullable String getMappingJson() {
		return mappingJson;
	}

	public void setMappingJson(String mappingJson) {
		this.mappingJson = mappingJson;
	}

	public @Nullable String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public @Nullable String getPrimaryKeyField() {
		return primaryKeyField;
	}

	public void setPrimaryKeyField(String primaryKeyField) {
		this.primaryKeyField = primaryKeyField;
	}

	public @Nullable String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	public @Nullable String getSimilarityFunction() {
		return similarityFunction;
	}

	public void setSimilarityFunction(String similarityFunction) {
		this.similarityFunction = similarityFunction;
	}

	public List<String> getOutputFields() {
		return outputFields;
	}

	public void setOutputFields(List<String> outputFields) {
		this.outputFields = outputFields;
	}

	public @Nullable Integer getDimensions() {
		return dimensions;
	}

	public void setDimensions(int dimensions) {
		this.dimensions = dimensions;
	}

	public Map<String, @Nullable Object> toOpenSearchClientParams() {
		Map<String, @Nullable Object> params = new HashMap<>();
		params.put("instanceId", this.getInstanceId());
		params.put("endpoint", this.getEndpoint());
		params.put("accessUserName", this.getAccessUserName());
		params.put("accessPassWord", this.getAccessPassWord());
		return params;
	}
}
