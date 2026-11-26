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

import java.util.List;

import com.alicloud.openservices.tablestore.SyncClient;
import com.alicloud.openservices.tablestore.model.search.DescribeSearchIndexRequest;
import com.alicloud.openservices.tablestore.model.search.DescribeSearchIndexResponse;
import com.alicloud.openservices.tablestore.model.search.FieldSchema;
import com.alicloud.openservices.tablestore.model.search.FieldType;
import com.alicloud.openservices.tablestore.model.search.IndexSchema;
import com.alicloud.openservices.tablestore.model.search.vector.VectorDataType;
import com.alicloud.openservices.tablestore.model.search.vector.VectorMetricType;
import com.alicloud.openservices.tablestore.model.search.vector.VectorOptions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class TablestoreVectorStoreAutoConfigurationTests {

	@Test
	void usesExistingIndexDimensionWhenUnconfigured() {
		var client = mock(SyncClient.class);
		var properties = new TablestoreVectorStoreProperties();
		var schema = new IndexSchema();
		var vector = new VectorOptions(VectorDataType.FLOAT_32, 384, VectorMetricType.COSINE);
		schema.setFieldSchemas(List.of(new FieldSchema(properties.getEmbeddingField(), FieldType.VECTOR)
			.setVectorOptions(vector)));
		var response = mock(DescribeSearchIndexResponse.class);
		when(response.getSchema()).thenReturn(schema);
		when(client.describeSearchIndex(any())).thenReturn(response);

		var store = new TablestoreVectorStoreAutoConfiguration().knowledgeStore(client, properties);

		assertThat(store.getEmbeddingDimension()).isEqualTo(384);
		var request = ArgumentCaptor.forClass(DescribeSearchIndexRequest.class);
		verify(client).describeSearchIndex(request.capture());
		assertThat(request.getValue().getTableName()).isEqualTo(store.getTableName());
		assertThat(request.getValue().getIndexName()).isEqualTo(store.getSearchIndexName());
	}

	@Test
	void explicitDimensionSkipsIndexMetadataLookup() {
		var client = mock(SyncClient.class);
		var properties = new TablestoreVectorStoreProperties();
		properties.setEmbeddingDimension(1024);

		var store = new TablestoreVectorStoreAutoConfiguration().knowledgeStore(client, properties);

		assertThat(store.getEmbeddingDimension()).isEqualTo(1024);
		verifyNoInteractions(client);
	}

}
