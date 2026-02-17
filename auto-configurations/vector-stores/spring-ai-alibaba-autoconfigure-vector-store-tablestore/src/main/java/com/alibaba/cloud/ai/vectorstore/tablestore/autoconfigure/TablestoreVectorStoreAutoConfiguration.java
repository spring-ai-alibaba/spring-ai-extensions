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


import com.alibaba.cloud.ai.vectorstore.tablestore.TablestoreVectorStore;
import com.alicloud.openservices.tablestore.SyncClient;
import com.aliyun.openservices.tablestore.agent.knowledge.KnowledgeStoreImpl;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnClass({ TablestoreVectorStore.class, EmbeddingModel.class, KnowledgeStoreImpl.class, SyncClient.class })
@EnableConfigurationProperties({ TablestoreVectorStoreProperties.class })
@ConditionalOnProperty(prefix = "spring.ai.vectorstore.tablestore", name = "enabled", havingValue = "true")
public class TablestoreVectorStoreAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public SyncClient tablestoreSyncClient(TablestoreVectorStoreProperties properties) {
		var endpoint = properties.getEndpoint();
		var accessKeyId = properties.getAccessKeyId();
		var accessKeySecret = properties.getAccessKeySecret();
		var instanceName = properties.getInstanceName();
		if (endpoint == null || accessKeyId == null || accessKeySecret == null || instanceName == null) {
			throw new IllegalArgumentException("Tablestore SyncClient configuration is incomplete. Please check the properties.");
		}
		return new SyncClient(endpoint, accessKeyId, accessKeySecret, instanceName);
	}

	@Bean
	@ConditionalOnMissingBean
	public KnowledgeStoreImpl knowledgeStore(SyncClient syncClient, TablestoreVectorStoreProperties properties) {
		return KnowledgeStoreImpl.builder().client(syncClient)
				.metadataSchema(properties.getExtraMetaDataIndexSchema())
				.textField(properties.getTextField())
				.embeddingField(properties.getEmbeddingField())
				.embeddingDimension(properties.getEmbeddingDimension())
				.enableMultiTenant(properties.isEnableMultitenant())
				.build();
	}

	@Bean
	@ConditionalOnMissingBean
	public TablestoreVectorStore tablestoreVectorStore(KnowledgeStoreImpl knowledgeStore, EmbeddingModel embeddingModel, TablestoreVectorStoreProperties properties) {
		return TablestoreVectorStore.builder(knowledgeStore, embeddingModel).initializeSchema(properties.isInitializeSchema()).build();
	}

}
