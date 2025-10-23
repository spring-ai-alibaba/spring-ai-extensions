package com.alibaba.cloud.ai.rag.retrieval.search;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.alibaba.cloud.ai.rag.postretrieval.DashScopeRerankPostProcessor;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;

import java.util.List;

/**
 * Hybrid Elasticsearch retriever using BM25 and KNN search with Reciprocal Rank Fusion.
 *
 * @author hupei
 * @author ViliamSun
 * @author benym
 */
public class HybridElasticsearchRetriever implements DocumentRetriever {

    /**
     * Elasticsearch REST client for executing search requests
     */
    private final ElasticsearchClient elasticsearchClient;

    /**
     * Model used for generating embeddings from text queries
     */
    private final EmbeddingModel embeddingModel;

    /**
     * Name of the Elasticsearch index to search
     */
    private final String indexName;

    /**
     * Maximum number of documents to return in search results
     */
    private final int windowSize;

    /**
     * Constant k used in Reciprocal Rank Fusion scoring
     */
    private final int rrfK;

    /**
     * Boost factor applied to BM25 text search scores
     */
    private final float bm25Bias;

    /**
     * Boost factor applied to KNN vector search scores
     */
    private final float knnBias;

    /**
     * Whether to use hybrid search (BM25 + KNN)
     */
    private final boolean useHybrid;

    public HybridElasticsearchRetriever(ElasticsearchClient elasticsearchClient, EmbeddingModel embeddingModel, String indexName, int windowSize, int rrfK, float bm25Bias, float knnBias, boolean useHybrid) {
        this.elasticsearchClient = elasticsearchClient;
        this.embeddingModel = embeddingModel;
        this.indexName = indexName;
        this.windowSize = windowSize;
        this.rrfK = rrfK;
        this.bm25Bias = bm25Bias;
        this.knnBias = knnBias;
        this.useHybrid = useHybrid;
    }

    @Override
    public List<Document> retrieve(Query query) {
        return List.of();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private ElasticsearchClient elasticsearchClient;

        private EmbeddingModel embeddingModel;

        private String indexName;

        private int windowSize;

        private int rrfK;

        private float bm25Bias;

        private float knnBias;

        private boolean useHybrid;

        public Builder elasticsearchClient(ElasticsearchClient elasticsearchClient) {
            this.elasticsearchClient = elasticsearchClient;
            return this;
        }

        public Builder embeddingModel(EmbeddingModel embeddingModel) {
            this.embeddingModel = embeddingModel;
            return this;
        }

        public Builder indexName(String indexName) {
            this.indexName = indexName;
            return this;
        }

        public Builder windowSize(int windowSize) {
            this.windowSize = windowSize;
            return this;
        }

        public Builder rrfK(int rrfK) {
            this.rrfK = rrfK;
            return this;
        }

        public Builder bm25Bias(float bm25Bias) {
            this.bm25Bias = bm25Bias;
            return this;
        }

        public Builder knnBias(float knnBias) {
            this.knnBias = knnBias;
            return this;
        }

        public Builder useHybrid(boolean useHybrid) {
            this.useHybrid = useHybrid;
            return this;
        }

        public HybridElasticsearchRetriever build() {
            return new HybridElasticsearchRetriever(elasticsearchClient, embeddingModel, indexName, windowSize, rrfK, bm25Bias, knnBias, useHybrid);
        }
    }
}
