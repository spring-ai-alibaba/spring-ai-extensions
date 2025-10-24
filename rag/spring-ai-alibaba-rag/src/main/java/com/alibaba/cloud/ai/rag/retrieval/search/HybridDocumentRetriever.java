package com.alibaba.cloud.ai.rag.retrieval.search;

import org.springframework.ai.document.Document;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;

import java.util.List;

/**
 * Hybrid Document Retriever interface that extends the basic DocumentRetriever.
 *
 * @author benym
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
