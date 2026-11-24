/*
 * Copyright 2023-2026 the original author or authors.
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

package com.alibaba.cloud.ai.rag;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.alibaba.cloud.ai.rag.preretrieval.transformation.HyDeTransformer;
import com.alibaba.cloud.ai.rag.retrieval.search.HyDeRetriever;
import com.alibaba.cloud.ai.rag.retrieval.search.HybridElasticsearchRetriever;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.rag.Query;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.elasticsearch.ElasticsearchVectorStoreOptions;
import org.springframework.ai.vectorstore.filter.Filter;
import tools.jackson.databind.json.JsonMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RagSpringAiCompatibilityTests {

    private static final long REVISION = 2_147_483_648L;

    private static final String FILTER = "revision == 2147483648";

    @Test
    void hydeUsesGeneratedPassageAndPreservesLongFilter() {
        ChatModel model = mock(ChatModel.class);
        when(model.getOptions()).thenReturn(ChatOptions.builder().build());
        when(model.call(any(Prompt.class)))
            .thenReturn(new ChatResponse(List.of(new Generation(new AssistantMessage("Generated passage")))));
        VectorStore vectorStore = mock(VectorStore.class);
        var requested = ArgumentCaptor.forClass(org.springframework.ai.vectorstore.SearchRequest.class);
        var retriever = HyDeRetriever.builder()
            .hyDeTransformer(HyDeTransformer.builder().chatClientBuilder(ChatClient.builder(model)).build())
            .vectorStore(vectorStore).build();

        retriever.retrieve(Query.builder().text("What changed?")
            .context(Map.of(HyDeRetriever.FILTER_EXPRESSION, FILTER)).build());

        verify(vectorStore).similaritySearch(requested.capture());
        var search = requested.getValue();
        assertThat(search.getQuery()).isEqualTo("Generated passage");
        assertThat(search.getFilterExpression().right()).isEqualTo(new Filter.Value(REVISION));
    }

    @Test
    @SuppressWarnings("unchecked")
    void hybridSearchCarriesLongFilterAndReadsBackDocument() throws IOException {
        var client = mock(ElasticsearchClient.class);
        SearchResponse<Document> response = mock(SearchResponse.class, RETURNS_DEEP_STUBS);
        var source = JsonMapper.shared().readValue(
            """
            {"id":"versioned","text":"Release history","metadata":{"revision":2147483648}}
            """, Document.class);
        when(response.hits().hits())
            .thenReturn(List.of(Hit.of(hit -> hit.index("documents").id("versioned").source(source).score(0.9))));
        when(client.search(any(SearchRequest.class), eq(Document.class))).thenReturn(response);
        var embedding = mock(EmbeddingModel.class);
        when(embedding.embed("Release history")).thenReturn(new float[] { 0.25f, 0.75f });
        var options = new ElasticsearchVectorStoreOptions();
        options.setIndexName("documents");
        options.setEmbeddingFieldName("embedding");
        var retriever = HybridElasticsearchRetriever.builder()
            .elasticsearchClient(client).embeddingModel(embedding).vectorStoreOptions(options).build();

        var documents = retriever.retrieve(Query.builder().text("Release history")
            .context(Map.of(HybridElasticsearchRetriever.FILTER_EXPRESSION, FILTER)).build());

        var requested = ArgumentCaptor.forClass(SearchRequest.class);
        verify(client).search(requested.capture(), eq(Document.class));
        assertThat(requested.getValue().knn()).isNotEmpty();
        assertThat(requested.getValue().query()).isNotNull();
        assertThat(requested.getValue().toString()).contains("metadata.revision", Long.toString(REVISION));
        assertThat(documents).singleElement().satisfies(document -> {
            assertThat(document.getId()).isEqualTo("versioned");
            assertThat(document.getText()).isEqualTo("Release history");
            assertThat(document.getMetadata()).containsEntry("revision", REVISION);
            assertThat(document.getScore()).isNotNull();
        });
    }

}
