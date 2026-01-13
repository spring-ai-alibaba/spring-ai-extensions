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

package com.alibaba.cloud.ai.rag.retrieval.search;

import org.springframework.ai.document.Document;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;

import java.util.List;

/**
 * Hybrid Document Retriever interface that extends the basic DocumentRetriever.
 *
 * @author benym
 * @since 1.1.0.0-SNAPSHOT
 */
public interface HybridDocumentRetriever extends DocumentRetriever {


    /**
     * Retrieves relevant documents from an underlying data source based on the given query.
     *
     * @param query       The query to use for retrieving documents
     * @param filterQuery filterQuery will be applied to limit the search scope in knn and bm25
     * @param textQuery   textQuery will be used in bm25 search
     * @return A list of relevant documents
     */
    List<Document> retrieve(Query query,
                            co.elastic.clients.elasticsearch._types.query_dsl.Query filterQuery,
                            co.elastic.clients.elasticsearch._types.query_dsl.Query textQuery);
}
