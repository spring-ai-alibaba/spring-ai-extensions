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
package com.alibaba.cloud.ai.vectorstore.tablestore;

import java.util.List;
import java.util.Map;

import com.alicloud.openservices.tablestore.model.search.vector.VectorMetricType;
import com.aliyun.openservices.tablestore.agent.knowledge.KnowledgeStoreImpl;
import com.aliyun.openservices.tablestore.agent.model.Document;
import com.aliyun.openservices.tablestore.agent.model.DocumentHit;
import com.aliyun.openservices.tablestore.agent.model.Response;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.EmbeddedDocument;
import org.springframework.ai.vectorstore.SearchRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class TablestorePrecomputedVectorTests {

	@Test
	void preservesTenantMetadataAcrossRepeatedUpsertsAndSearchesByVector() {
		var client = mock(KnowledgeStoreImpl.class);
		var model = mock(EmbeddingModel.class);
		when(client.getEmbeddingDimension()).thenReturn(2);
		when(client.getEmbeddingMetricType()).thenReturn(VectorMetricType.values()[0]);
		when(client.enableMultiTenant()).thenReturn(true);
		var store = TablestoreVectorStore.builder(client, model).build();
		float[] vector = { 0.25f, 0.75f };
		var document = new org.springframework.ai.document.Document("test-id", "text",
				Map.of(Document.DOCUMENT_TENANT_ID, "tenant-a", "kind", "test"));
		var embedded = new EmbeddedDocument(document, vector);

		assertThatIllegalArgumentException().isThrownBy(
				() -> store.upsert(List.of(embedded, new EmbeddedDocument(document, new float[] { 1 }))));
		assertThatIllegalArgumentException().isThrownBy(
				() -> store.similaritySearch(new float[] { 1 }, SearchRequest.builder().build()));

		store.upsert(List.of(embedded));
		store.upsert(List.of(embedded));
		var row = ArgumentCaptor.forClass(Document.class);
		verify(client, times(2)).putDocument(row.capture());
		assertThat(row.getValue().getDocumentId()).isEqualTo("test-id");
		assertThat(row.getValue().getEmbedding()).containsExactly(vector);
		assertThat(row.getValue().getTenantId()).isEqualTo("tenant-a");
		assertThat(document.getMetadata()).containsEntry(Document.DOCUMENT_TENANT_ID, "tenant-a");

		when(client.vectorSearch(any(), eq(2), eq(0.5f), anySet(), any(), any(), anyMap()))
			.thenAnswer(invocation -> {
				assertThat(invocation.<float[]>getArgument(0)).containsExactly(vector);
				return new Response<>(List.of(new DocumentHit(row.getValue(), 0.9)), null);
			});
		assertThat(store.similaritySearch(vector, SearchRequest.builder().topK(2).similarityThreshold(0.5).build()))
			.singleElement().satisfies(result -> assertThat(result.getId()).isEqualTo("test-id"));
		verifyNoInteractions(model);
	}

}
