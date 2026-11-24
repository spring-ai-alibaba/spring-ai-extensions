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
package com.alibaba.cloud.ai.vectorstore.opensearch;

import java.util.List;
import java.util.Map;

import com.aliyun.ha3engine.vector.Client;
import com.aliyun.ha3engine.vector.models.PushDocumentsRequest;
import com.aliyun.ha3engine.vector.models.PushDocumentsResponse;
import com.aliyun.ha3engine.vector.models.QueryRequest;
import com.aliyun.ha3engine.vector.models.SearchResponse;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.EmbeddedDocument;
import org.springframework.ai.vectorstore.SearchRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class OpenSearchPrecomputedVectorTests {

	@Test
	void sendsVectorsToTheNativeQueryAndUpsertApis() throws Exception {
		var client = mock(Client.class);
		var model = mock(EmbeddingModel.class);
		var options = new OpenSearchVectorStoreOptions();
		options.setTableName("table");
		options.setIndex("vector_index");
		options.setPrimaryKeyField("key");
		options.setVectorField("embedding");
		options.setDimensions(2);
		options.setSimilarityFunction("l2");
		var store = OpenSearchVectorStore.builder(new OpenSearchApi("test-instance", client), model)
			.options(options).build();
		float[] vector = { 0.25f, 0.75f };
		var document = new Document("test-id", "text", Map.of("kind", "test"));
		var embedded = new EmbeddedDocument(document, vector);

		assertThatIllegalArgumentException().isThrownBy(
				() -> store.upsert(List.of(embedded, new EmbeddedDocument(new Document("other"), new float[] { 1 }))));
		verifyNoInteractions(client);
		when(client.pushDocuments(anyString(), anyString(), any()))
			.thenReturn(new PushDocumentsResponse().setBody("{\"code\":200}"));
		store.upsert(List.of(embedded));
		var upsert = ArgumentCaptor.forClass(PushDocumentsRequest.class);
		verify(client).pushDocuments(eq("test-instance_table"), eq("key"), upsert.capture());
		assertThat(upsert.getValue().getBody()).containsExactly(Map.of("cmd", "add", "fields",
				Map.of("key", "test-id", "content", "text", "metadata", "{\"kind\":\"test\"}",
						"embedding", List.of(0.25f, 0.75f))));

		when(client.query(any())).thenReturn(new SearchResponse().setBody("""
				{"totalCount": 1, "result": [{"id": "test-id", "score": 0, "fields": {"content": "text"}}]}
				"""));
		assertThat(store.similaritySearch(vector, SearchRequest.builder().query("ignored").topK(2).build()))
			.singleElement().satisfies(result -> {
				assertThat(result.getId()).isEqualTo("test-id");
				assertThat(result.getScore()).isEqualTo(1.0);
			});
		var query = ArgumentCaptor.forClass(QueryRequest.class);
		verify(client).query(query.capture());
		assertThat(query.getValue().getVector()).containsExactly(0.25f, 0.75f);
		assertThat(query.getValue().getTopK()).isEqualTo(2);
		assertThat(query.getValue().getIndexName()).isEqualTo("vector_index");
		assertThat(query.getValue().getContent()).isNull();
		verify(client, never()).inferenceQuery(any());
		verifyNoInteractions(model);
	}

}
