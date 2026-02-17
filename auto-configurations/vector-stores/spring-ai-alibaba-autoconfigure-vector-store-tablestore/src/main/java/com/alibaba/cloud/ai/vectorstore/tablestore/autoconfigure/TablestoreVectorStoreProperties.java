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
package com.alibaba.cloud.ai.vectorstore.tablestore.autoconfigure;

import com.alicloud.openservices.tablestore.model.search.FieldSchema;
import com.alicloud.openservices.tablestore.model.search.FieldType;
import org.jspecify.annotations.Nullable;
import org.springframework.ai.vectorstore.properties.CommonVectorStoreProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Arrays;
import java.util.List;

@ConfigurationProperties(prefix = TablestoreVectorStoreProperties.CONFIG_PREFIX)
public class TablestoreVectorStoreProperties extends CommonVectorStoreProperties {
	public static final String CONFIG_PREFIX = "spring.ai.vectorstore.tablestore";

	private @Nullable String endpoint;

	private @Nullable String instanceName;

	private @Nullable String accessKeyId;

	private @Nullable String accessKeySecret;

	private String tableName = "spring_ai_multi_tenant_knowledge_store";

	private String textField = "text_1";

	private String embeddingField = "embedding_1";

	private Integer embeddingDimension = 1536;

	private Boolean enableMultitenant = true;

	List<FieldSchema> extraMetaDataIndexSchema = Arrays.asList(new FieldSchema("country", FieldType.KEYWORD),
			new FieldSchema("year", FieldType.LONG), new FieldSchema("meta_example_string", FieldType.KEYWORD),
			new FieldSchema("meta_example_text", FieldType.TEXT).setAnalyzer(FieldSchema.Analyzer.MaxWord),
			new FieldSchema("meta_example_long", FieldType.LONG),
			new FieldSchema("meta_example_double", FieldType.DOUBLE),
			new FieldSchema("meta_example_boolean", FieldType.BOOLEAN));

	public @Nullable String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public @Nullable String getInstanceName() {
		return instanceName;
	}

	public void setInstanceName(String instanceName) {
		this.instanceName = instanceName;
	}

	public @Nullable String getAccessKeyId() {
		return accessKeyId;
	}

	public void setAccessKeyId(String accessKeyId) {
		this.accessKeyId = accessKeyId;
	}

	public @Nullable String getAccessKeySecret() {
		return accessKeySecret;
	}

	public void setAccessKeySecret(String accessKeySecret) {
		this.accessKeySecret = accessKeySecret;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getTextField() {
		return textField;
	}

	public void setTextField(String textField) {
		this.textField = textField;
	}

	public String getEmbeddingField() {
		return embeddingField;
	}

	public void setEmbeddingField(String embeddingField) {
		this.embeddingField = embeddingField;
	}

	public Integer getEmbeddingDimension() {
		return embeddingDimension;
	}

	public void setEmbeddingDimension(Integer embeddingDimension) {
		this.embeddingDimension = embeddingDimension;
	}

	public Boolean isEnableMultitenant() {
		return enableMultitenant;
	}

	public void setEnableMultitenant(Boolean enableMultitenant) {
		this.enableMultitenant = enableMultitenant;
	}

	public List<FieldSchema> getExtraMetaDataIndexSchema() {
		return extraMetaDataIndexSchema;
	}

	public void setExtraMetaDataIndexSchema(List<FieldSchema> extraMetaDataIndexSchema) {
		this.extraMetaDataIndexSchema = extraMetaDataIndexSchema;
	}
}
