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

package com.alibaba.cloud.ai.autoconfigure.rag;

import com.alibaba.cloud.ai.rag.retrieval.search.RetrieverType;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for RAG ElasticSearch integration.
 *
 * @author benym
 * @since 1.1.0.0-SNAPSHOT
 */
@ConfigurationProperties(prefix = RagElasticSearchProperties.RAG_ES_PREFIX)
public class RagElasticSearchProperties {

    public static final String RAG_ES_PREFIX = "spring.ai.alibaba.rag.elasticsearch";

    /**
     * Whether RAG ElasticSearch integration is enabled
     */
    private Boolean enabled = true;

    /**
     * Retrieval type: BM25, KNN, HYBRID
     */
    private RetrieverType retrieverType = RetrieverType.HYBRID;

    /**
     * Whether to use Reciprocal Rank Fusion (RRF) scoring
     * This feature requires a commercial license for elasticsearch
     */
    private boolean useRrf = false;

    /**
     * Configuration for recall
     */
    private Recall recall = new Recall();

    /**
     * Configuration for Reciprocal Rank Fusion (RRF)
     */
    private Rrf rrf = new Rrf();

    /**
     * Boost factor applied to BM25 text search scores
     */
    private float bm25Bias = 1.0f;

    /**
     * Boost factor applied to KNN vector search scores
     */
    private float knnBias = 1.0f;

    /**
     * top K documents to retrieve
     */
    private Integer topK = 50;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public RetrieverType getRetrieverType() {
        return retrieverType;
    }

    public void setRetrieverType(RetrieverType retrieverType) {
        this.retrieverType = retrieverType;
    }

    public boolean isUseRrf() {
        return useRrf;
    }

    public void setUseRrf(boolean useRrf) {
        this.useRrf = useRrf;
    }

    public Recall getRecall() {
        return recall;
    }

    public void setRecall(Recall recall) {
        this.recall = recall;
    }

    public Rrf getRrf() {
        return rrf;
    }

    public void setRrf(Rrf rrf) {
        this.rrf = rrf;
    }

    public float getBm25Bias() {
        return bm25Bias;
    }

    public void setBm25Bias(float bm25Bias) {
        this.bm25Bias = bm25Bias;
    }

    public float getKnnBias() {
        return knnBias;
    }

    public void setKnnBias(float knnBias) {
        this.knnBias = knnBias;
    }

    public Integer getTopK() {
        return topK;
    }

    public void setTopK(Integer topK) {
        this.topK = topK;
    }

    /**
     * Recall properties
     */
    public static class Recall {
        /**
         * Similarity threshold
         */
        private Double similarityThreshold = 0.0;

        /**
         * Number of neighbors for Knn search
         */
        private Integer neighborsNum = 50;

        /**
         * Number of candidates for Knn search
         */
        private Integer candidateNum = 100;

        public Double getSimilarityThreshold() {
            return similarityThreshold;
        }

        public void setSimilarityThreshold(Double similarityThreshold) {
            this.similarityThreshold = similarityThreshold;
        }

        public Integer getNeighborsNum() {
            return neighborsNum;
        }

        public void setNeighborsNum(Integer neighborsNum) {
            this.neighborsNum = neighborsNum;
        }

        public Integer getCandidateNum() {
            return candidateNum;
        }

        public void setCandidateNum(Integer candidateNum) {
            this.candidateNum = candidateNum;
        }
    }

    /**
     * Reciprocal Rank Fusion (RRF) properties
     */
    public static class Rrf {
        /**
         * This value determines how much influence documents in individual result sets per query have over the final ranked result set.
         * A higher value indicates that lower ranked documents have more influence.
         */
        private Integer rankConstant = 60;

        /**
         * Rank window size for Reciprocal Rank Fusion
         * This value determines the size of the individual result sets per query.
         * A higher value will improve result relevance at the cost of performance.
         * The final ranked result set is pruned down to the search request’s size.
         * rank_window_size must be greater than or equal to topK and greater than or equal to 1.
         * Defaults to the topK parameter.
         */
        private Integer rankWindowSize = 50;

        public Integer getRankConstant() {
            return rankConstant;
        }

        public void setRankConstant(Integer rankConstant) {
            this.rankConstant = rankConstant;
        }

        public Integer getRankWindowSize() {
            return rankWindowSize;
        }

        public void setRankWindowSize(Integer rankWindowSize) {
            this.rankWindowSize = rankWindowSize;
        }
    }
}
