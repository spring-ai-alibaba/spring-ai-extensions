package com.alibaba.cloud.ai.rag.retrieval.search;

/**
 * Enumeration of retriever types.
 *
 * @author benym
 */
public enum RetrieverType {

    /**
     * BM25 Retriever Type
     */
    BM25,

    /**
     * KNN Retriever Type
     */
    KNN,

    /**
     * Hybrid Retriever Type (BM25 + KNN)
     */
    HYBRID
}
