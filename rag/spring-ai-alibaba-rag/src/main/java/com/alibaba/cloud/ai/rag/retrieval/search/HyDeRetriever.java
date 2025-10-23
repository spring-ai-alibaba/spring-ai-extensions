package com.alibaba.cloud.ai.rag.retrieval.search;

import org.springframework.ai.document.Document;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;

import java.util.List;

/**
 *
 * @author benym
 */
public class HyDeRetriever implements DocumentRetriever {

    @Override
    public List<Document> retrieve(Query query) {
        return List.of();
    }
}
