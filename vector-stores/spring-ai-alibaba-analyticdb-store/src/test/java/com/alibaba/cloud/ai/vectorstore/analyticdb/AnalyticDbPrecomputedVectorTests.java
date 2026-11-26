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
package com.alibaba.cloud.ai.vectorstore.analyticdb;

import java.util.List;
import java.util.Map;

import com.aliyun.gpdb20160503.Client;
import com.aliyun.gpdb20160503.models.DescribeCollectionResponse;
import com.aliyun.gpdb20160503.models.DescribeCollectionResponseBody;
import com.aliyun.gpdb20160503.models.InitVectorDatabaseResponse;
import com.aliyun.gpdb20160503.models.QueryCollectionDataRequest;
import com.aliyun.gpdb20160503.models.QueryCollectionDataResponse;
import com.aliyun.gpdb20160503.models.QueryCollectionDataResponseBody;
import com.aliyun.gpdb20160503.models.QueryCollectionDataResponseBody.QueryCollectionDataResponseBodyMatches;
import com.aliyun.gpdb20160503.models.QueryCollectionDataResponseBody.QueryCollectionDataResponseBodyMatchesMatch;
import com.aliyun.gpdb20160503.models.UpsertCollectionDataRequest;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.EmbeddedDocument;
import org.springframework.ai.vectorstore.SearchRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class AnalyticDbPrecomputedVectorTests {

	@Test
	void passesTheSuppliedVectorAndDocumentIdToTheNativeOperations() throws Exception {
		var client = mock(Client.class);
		var model = mock(EmbeddingModel.class);
		when(client.describeCollection(any())).thenReturn(new DescribeCollectionResponse()
			.setBody(new DescribeCollectionResponseBody().setDimension(2)));
		when(client.initVectorDatabase(any())).thenReturn(new InitVectorDatabaseResponse());
		var store = AnalyticDbVectorStore.builder("documents", new AnalyticDbConfig(), client, model)
			.initializeSchema(true).build();
		store.afterPropertiesSet();
		var document = new Document("6cc64114-6bea-4fa9-a290-bdd15c787c76", "text", Map.of("kind", "test"));
		float[] vector = { 0.25f, 0.75f };
		var embedded = new EmbeddedDocument(document, vector);

		assertThatIllegalArgumentException().isThrownBy(
				() -> store.upsert(List.of(embedded, new EmbeddedDocument(new Document("other"), new float[] { 1 }))));
		assertThatIllegalArgumentException().isThrownBy(
				() -> store.similaritySearch(new float[] { 1 }, SearchRequest.builder().build()));

		store.upsert(List.of(embedded));
		var upsert = ArgumentCaptor.forClass(UpsertCollectionDataRequest.class);
		verify(client).upsertCollectionData(upsert.capture());
		assertThat(upsert.getValue().getRows()).singleElement().satisfies(row -> {
			assertThat(row.getId()).isEqualTo(document.getId());
			assertThat(row.getVector()).containsExactly(0.25, 0.75);
		});

		var match = new QueryCollectionDataResponseBodyMatchesMatch().setId(document.getId()).setScore(1.0)
			.setMetadata(Map.of("content", "text", "metadata", "{\"kind\":\"test\"}"));
		when(client.queryCollectionData(any())).thenAnswer(invocation -> {
			var query = invocation.<QueryCollectionDataRequest>getArgument(0);
			assertThat(query.getVector()).containsExactly(0.25, 0.75);
			assertThat(query.getContent()).isNull();
			assertThat(query.getTopK()).isEqualTo(2L);
			return new QueryCollectionDataResponse().setBody(new QueryCollectionDataResponseBody()
				.setMatches(new QueryCollectionDataResponseBodyMatches().setMatch(List.of(match))));
		});
		assertThat(store.similaritySearch(vector, SearchRequest.builder().query("ignored").topK(2).build()))
			.extracting(Document::getId).containsExactly(document.getId());
		verifyNoInteractions(model);
	}

}
