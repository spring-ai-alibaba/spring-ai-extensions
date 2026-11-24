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
package com.alibaba.cloud.ai.vectorstore.tair;

import java.util.List;
import java.util.Map;

import com.aliyun.tair.tairvector.factory.VectorBuilderFactory.Knn;
import com.aliyun.tair.tairvector.factory.VectorBuilderFactory.KnnItem;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.EmbeddedDocument;
import org.springframework.ai.vectorstore.SearchRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class TairPrecomputedVectorTests {

	@Test
	void usesProvidedVectorsAndAppliesThresholdToSimilarity() {
		var client = mock(TairVectorApi.class);
		var model = mock(EmbeddingModel.class);
		var options = new TairVectorStoreOptions();
		options.setDimensions(2);
		var store = TairVectorStore.builder(client, model).options(options).build();
		var document = new Document("test-id", "text", Map.of("kind", "test"));
		float[] vector = { 0.25f, 0.75f };
		var embedded = new EmbeddedDocument(document, vector);
		String index = options.getIndexName();

		assertThatIllegalArgumentException().isThrownBy(
				() -> store.upsert(List.of(embedded, new EmbeddedDocument(new Document("other"), new float[] { 1 }))));
		verifyNoInteractions(client);
		store.upsert(List.of(embedded));
		verify(client).tvshset(index, "test-id", "[0.25,0.75]",
				"id", "test-id", "content", "text", "metadata", "{\"kind\":\"test\"}");

		var results = new Knn<String>();
		results.add(new KnnItem<>("test-id", 0.0));
		results.add(new KnnItem<>("other", 4.0));
		when(client.tvsknnsearch(index, 2L, "[0.25,0.75]")).thenReturn(results);
		when(client.tvshmget(index, "test-id", "id", "content", "metadata"))
			.thenReturn(List.of("test-id", "text", "{\"kind\":\"test\"}"));

		assertThat(store.similaritySearch(vector, SearchRequest.builder().topK(2).similarityThreshold(0.5).build()))
			.singleElement().satisfies(result -> {
				assertThat(result.getId()).isEqualTo("test-id");
				assertThat(result.getScore()).isEqualTo(1.0);
			});
		verifyNoInteractions(model);
	}

}
