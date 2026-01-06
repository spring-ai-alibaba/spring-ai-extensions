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

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentMetadata;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.model.EmbeddingUtils;
import org.springframework.ai.rag.Query;
import org.springframework.ai.vectorstore.elasticsearch.ElasticsearchAiSearchFilterExpressionConverter;
import org.springframework.ai.vectorstore.elasticsearch.ElasticsearchVectorStoreOptions;
import org.springframework.ai.vectorstore.elasticsearch.SimilarityFunction;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionConverter;
import org.springframework.ai.vectorstore.filter.FilterExpressionTextParser;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Hybrid Elasticsearch retriever using BM25 and KNN search with Reciprocal Rank Fusion.
 *
 * @author hupei
 * @author ViliamSun
 * @author benym
 * @since 1.1.0.0-SNAPSHOT
 */
public class HybridElasticsearchRetriever implements HybridDocumentRetriever {

    private static final Logger logger = LoggerFactory.getLogger(HybridElasticsearchRetriever.class);

    /**
     * BM25 field key in the query context
     */
    public static final String BM25_FILED = "spring_ai_alibaba_rag_bm25_field";

    /**
     * Filter expression key in the query context
     */
    public static final String FILTER_EXPRESSION = "spring_ai_alibaba_rag_filter_expression";

    /**
     * Similarity threshold that accepts all search scores. A threshold value of 0.0 means
     * any similarity is accepted or disable the similarity threshold filtering. A
     * threshold value of 1.0 means an exact match is required.
     */
    public static final double SIMILARITY_THRESHOLD_ACCEPT_ALL = 0.0;

    /**
     * Default number of neighbors for Knn search
     */
    private static final int DEFAULT_NEIGHBORS_NUM = 50;

    /**
     * Default number of candidates for Knn search
     */
    private static final int DEFAULT_CANDIDATE_NUM = 100;

    /**
     * Default top K documents to retrieve
     */
    private static final int DEFAULT_TOP_K = 50;

    /**
     * Default rank constant for Reciprocal Rank Fusion
     */
    private static final int DEFAULT_RANK_CONSTANT = 60;

    /**
     * Default boost factor applied to BM25 text search scores
     */
    private static final float DEFAULT_BM25_BIAS = 1.0f;

    /**
     * Default boost factor applied to KNN vector search scores
     */
    private static final float DEFAULT_KNN_BIAS = 1.0f;

    /**
     * Default whether to use Reciprocal Rank Fusion (RRF) scoring
     */
    private static final boolean DEFAULT_USE_RRF = false;

    /**
     * Options for configuring the Elasticsearch vector store
     */
    private final ElasticsearchVectorStoreOptions vectorStoreOptions;

    /**
     * Elasticsearch REST client for executing search requests
     */
    private final ElasticsearchClient elasticsearchClient;

    /**
     * Model used for generating embeddings from text queries
     */
    private final EmbeddingModel embeddingModel;

    /**
     * Similarity threshold
     */
    private final double similarityThreshold;

    /**
     * Number of neighbors for Knn search
     */
    private final int neighborsNum;

    /**
     * Number of candidates for Knn search
     */
    private final int candidateNum;

    /**
     * top K documents to retrieve
     */
    private final int topK;

    /**
     * Rank window size for Reciprocal Rank Fusion
     * This value determines the size of the individual result sets per query.
     * A higher value will improve result relevance at the cost of performance.
     * The final ranked result set is pruned down to the search request’s size.
     * rank_window_size must be greater than or equal to topK and greater than or equal to 1.
     * Defaults to the topK parameter.
     */
    private final int rankWindowSize;

    /**
     * This value determines how much influence documents in individual result sets per query have over the final ranked result set.
     * A higher value indicates that lower ranked documents have more influence.
     */
    private final int rankConstant;

    /**
     * Boost factor applied to BM25 text search scores
     */
    private final float bm25Bias;

    /**
     * Boost factor applied to KNN vector search scores
     */
    private final float knnBias;

    /**
     * Retriever type
     */
    private final RetrieverType retrieverType;

    /**
     * Whether to use Reciprocal Rank Fusion (RRF) scoring
     * This feature requires a commercial license for elasticsearch
     */
    private final boolean useRrf;

    /**
     * Filter expression converter
     */
    private final FilterExpressionConverter filterExpressionConverter;

    /**
     * Supplier to allow for lazy evaluation of the filter expression,
     * which may depend on the execution content. For example, you may want to
     * filter dynamically based on the current user's identity or tenant ID.
     */
    private final Supplier<Filter.Expression> filterExpression;

    public HybridElasticsearchRetriever(ElasticsearchVectorStoreOptions vectorStoreOptions, ElasticsearchClient elasticsearchClient,
                                        EmbeddingModel embeddingModel, double similarityThreshold, int neighborsNum,
                                        int candidateNum, int topK, int rankWindowSize, int rankConstant, float bm25Bias, float knnBias,
                                        RetrieverType retrieverType, boolean useRrf,
                                        FilterExpressionConverter filterExpressionConverter,
                                        Supplier<Filter.Expression> filterExpression) {
        this.vectorStoreOptions = vectorStoreOptions;
        this.elasticsearchClient = elasticsearchClient;
        this.embeddingModel = embeddingModel;
        this.similarityThreshold = similarityThreshold > 0.0 ? similarityThreshold : SIMILARITY_THRESHOLD_ACCEPT_ALL;
        this.neighborsNum = neighborsNum > 0 ? neighborsNum : DEFAULT_NEIGHBORS_NUM;
        this.candidateNum = candidateNum > 0 ? candidateNum : DEFAULT_CANDIDATE_NUM;
        this.topK = topK > 0 ? topK : DEFAULT_TOP_K;
        this.rankWindowSize = rankWindowSize > 0 ? rankWindowSize : this.topK;
        Assert.isTrue(rankWindowSize >= this.topK, "rankWindowSize must be >= topK");
        this.rankConstant = rankConstant > 0 ? rankConstant : DEFAULT_RANK_CONSTANT;
        this.bm25Bias = bm25Bias != 0 ? bm25Bias : DEFAULT_BM25_BIAS;
        this.knnBias = knnBias != 0 ? knnBias : DEFAULT_KNN_BIAS;
        this.retrieverType = retrieverType != null ? retrieverType : RetrieverType.HYBRID;
        this.useRrf = useRrf;
        this.filterExpressionConverter = filterExpressionConverter != null ? filterExpressionConverter : new ElasticsearchAiSearchFilterExpressionConverter();
        this.filterExpression = filterExpression != null ? filterExpression : () -> null;
    }

    @Override
    public List<Document> retrieve(Query query) {
        Assert.notNull(query, "query cannot be null");
        try {
            return search(query);
        } catch (IOException e) {
            throw new RuntimeException("Failed to execute hybrid search", e);
        }
    }

    @Override
    public List<Document> retrieve(Query query,
                                   co.elastic.clients.elasticsearch._types.query_dsl.Query filterQuery,
                                   co.elastic.clients.elasticsearch._types.query_dsl.Query textQuery) {
        Assert.notNull(query, "query cannot be null");
        try {
            return search(query, filterQuery, textQuery);
        } catch (IOException e) {
            throw new RuntimeException("Failed to execute hybrid search", e);
        }
    }

    /**
     * Execute a hybrid search using BM25 and KNN search with Reciprocal Rank Fusion.
     *
     * @param query       The query to search for
     * @param filterQuery The filter query to apply
     * @param textQuery   The text query to apply
     * @return A list of documents matching the query
     */
    private List<Document> search(Query query,
                                  co.elastic.clients.elasticsearch._types.query_dsl.Query filterQuery,
                                  co.elastic.clients.elasticsearch._types.query_dsl.Query textQuery) throws IOException {
        float[] vector = embeddingModel.embed(query.text());
        // 1. Build search request
        SearchRequest.Builder builder = new SearchRequest.Builder();
        SearchResponse<Document> response = elasticsearchClient.search(
                buildSearchRequest(builder, vector, filterQuery, textQuery), Document.class
        );
        // 2. Convert search response to documents
        return response.hits().hits().stream().map(this::toDocument).collect(Collectors.toList());
    }

    /**
     * Builds the search request for the hybrid search.
     *
     * @param sr          SearchRequest.Builder
     * @param vector      query embedding vector
     * @param filterQuery filter query
     * @param textQuery   text query
     * @return SearchRequest
     */
    private SearchRequest buildSearchRequest(SearchRequest.Builder sr, float[] vector,
                                                     co.elastic.clients.elasticsearch._types.query_dsl.Query filterQuery,
                                                     co.elastic.clients.elasticsearch._types.query_dsl.Query textQuery) {
        // 1. Knn search
        SearchRequest.Builder builder = new SearchRequest.Builder();
        if (RetrieverType.KNN.equals(retrieverType) || RetrieverType.HYBRID.equals(retrieverType)) {
            builder = sr.index(vectorStoreOptions.getIndexName())
                    .knn(k -> k.queryVector(EmbeddingUtils.toList(vector))
                            .similarity(computeSimilarityThreshold())
                            .k(neighborsNum)
                            .field(vectorStoreOptions.getEmbeddingFieldName())
                            .numCandidates(candidateNum)
                            .filter(ensureQuery(filterQuery))
                            .boost(knnBias))
                    .size(topK);
        }
        // 2. Bm25 search
        if (RetrieverType.BM25.equals(retrieverType) || RetrieverType.HYBRID.equals(retrieverType)) {
            builder.query(q -> q.bool(b -> b
                    .filter(ensureQuery(filterQuery))
                    .must(ensureQuery(textQuery))
                    .boost(bm25Bias)))
                    .size(topK);
        }
        // 3. RRF
        if (useRrf) {
            builder.rank(r -> r.rrf(rrf -> rrf.rankConstant((long) rankConstant)
                    .rankWindowSize((long) rankWindowSize)));
        }
        SearchRequest searchRequest = builder.build();
        logger.debug("Elasticsearch Hybrid Search Request: {}", searchRequest.toString());
        return searchRequest;
    }

    /**
     * Execute a hybrid search using BM25 and KNN search with Reciprocal Rank Fusion.
     * use filter expression and bm25 filed from the query context
     *
     * @param query The query to search for
     * @return A list of documents matching the query
     */
    private List<Document> search(Query query) throws IOException {
        // 1. Compute the filter expression and bm25 filed to use for the request
        Filter.Expression requestFilterExpression = computeRequestFilterExpression(query);
        String bm25Field = computeBm25Field(query);
        float[] vector = embeddingModel.embed(query.text());
        // 2. Build search request
        SearchRequest.Builder builder = new SearchRequest.Builder();
        SearchResponse<Document> response = elasticsearchClient.search(
                buildSearchRequest(builder, vector, requestFilterExpression, query.text(), bm25Field), Document.class
        );
        // 3. Convert search response to documents
        return response.hits().hits().stream().map(this::toDocument).collect(Collectors.toList());
    }

    /**
     * Builds the search request for the hybrid search.
     * use filter expression and bm25 filed from the query context
     *
     * @param sr               SearchRequest.Builder
     * @param vector           query embedding vector
     * @param filterExpression filter expression
     * @param queryText        query text
     * @param bm25Field        bm25 field
     * @return SearchRequest.Builder
     */
    private SearchRequest buildSearchRequest(SearchRequest.Builder sr, float[] vector, Filter.Expression filterExpression,
                                                     String queryText, String bm25Field) {
        // 1. Knn search
        SearchRequest.Builder builder = new SearchRequest.Builder();
        if (RetrieverType.KNN.equals(retrieverType) || RetrieverType.HYBRID.equals(retrieverType)) {
            builder = sr.index(vectorStoreOptions.getIndexName())
                    .knn(k -> k.queryVector(EmbeddingUtils.toList(vector))
                            .similarity(computeSimilarityThreshold())
                            .k(neighborsNum)
                            .field(vectorStoreOptions.getEmbeddingFieldName())
                            .numCandidates(candidateNum)
                            .filter(fl -> fl
                                    .queryString(qs -> qs.query(getElasticsearchQueryString(filterExpression))))
                            .boost(knnBias))
                    .size(topK);
        }
        // 2. Bm25 search
        if (RetrieverType.BM25.equals(retrieverType) || RetrieverType.HYBRID.equals(retrieverType)) {
            builder.query(q -> q.bool(b -> b
                    .filter(fl -> fl
                            .queryString(qs -> qs.query(getElasticsearchQueryString(filterExpression))))
                    .must(m -> m.match(
                            mm -> mm.field(bm25Field)
                                    .query(escape(queryText)))
                    ).boost(bm25Bias)))
                    .size(topK);
        }
        // 3. RRF
        if (useRrf) {
            builder.rank(r -> r.rrf(rrf -> rrf.rankConstant((long) rankConstant)
                    .rankWindowSize((long) rankWindowSize)));
        }
        SearchRequest searchRequest = builder.build();
        logger.debug("Elasticsearch Hybrid Search Request: {}", searchRequest.toString());
        return searchRequest;
    }

    private static String escape(String text) {
        return text.replace("\"", "\\\"");
    }

    /**
     * Ensures that the provided query is not null.
     * If it is null, a match_all query is returned.
     *
     * @param query query
     * @return non-null query
     */
    private co.elastic.clients.elasticsearch._types.query_dsl.Query ensureQuery(
            co.elastic.clients.elasticsearch._types.query_dsl.Query query) {
        return query != null ? query : co.elastic.clients.elasticsearch._types.query_dsl.Query.of(q -> q.matchAll(m -> m));
    }

    /**
     * Computes the similarity threshold to use for the current request.
     * <p>
     * If the similarity function is {@link SimilarityFunction#l2_norm}, the threshold
     * is reverted to its original value.
     *
     * @return the similarity threshold to use for the request
     */
    private float computeSimilarityThreshold() {
        float finalThreshold = (float) similarityThreshold;
        // reverting l2_norm distance to its original value
        if (this.vectorStoreOptions.getSimilarity().equals(SimilarityFunction.l2_norm)) {
            finalThreshold = 1 - finalThreshold;
        }
        return finalThreshold;
    }

    /**
     * Computes the filter expression to use for the current request.
     * <p>
     * The filter expression can be provided in the query context using the
     * {@link #FILTER_EXPRESSION} key. This key accepts either a string representation of
     * a filter expression or a {@link Filter.Expression} object directly.
     * <p>
     * If no filter expression is provided in the context, the default filter expression
     * configured for this retriever is used.
     *
     * @param query the query containing potential context with filter expression
     * @return the filter expression to use for the request
     */
    private Filter.Expression computeRequestFilterExpression(Query query) {
        var contextFilterExpression = query.context().get(FILTER_EXPRESSION);
        if (contextFilterExpression != null) {
            if (contextFilterExpression instanceof Filter.Expression) {
                return (Filter.Expression) contextFilterExpression;
            } else if (StringUtils.hasText(contextFilterExpression.toString())) {
                return new FilterExpressionTextParser().parse(contextFilterExpression.toString());
            }
        }
        return this.filterExpression.get();
    }

    /**
     * Computes the BM25 field to use for the current request.
     * <p>
     * The BM25 field can be provided in the query context using the
     * {@link #BM25_FILED} key. If no BM25 field is provided in the context, an empty
     * string is returned.
     *
     * @param query the query containing potential context with BM25 field
     * @return the BM25 field to use for the request
     */
    private String computeBm25Field(Query query) {
        var bm25Filed = query.context().get(BM25_FILED);
        if (bm25Filed != null) {
            if (bm25Filed instanceof String && StringUtils.hasText((String) bm25Filed)) {
                return (String) bm25Filed;
            }
        }
        return "";
    }

    private String getElasticsearchQueryString(Filter.Expression filterExpression) {
        return Objects.isNull(filterExpression) ? "*"
                : this.filterExpressionConverter.convertExpression(filterExpression);

    }

    /**
     * Converts a hit from the Elasticsearch response to a Document.
     * <p>
     * This method converts the hit from the Elasticsearch response to a Document. It
     * extracts the source document from the hit and adds the score to the document if
     * necessary. The score is added as a metadata field with the key
     * {@link DocumentMetadata#DISTANCE}.
     *
     * @param hit the hit from the Elasticsearch response
     * @return the converted Document
     */
    private Document toDocument(Hit<Document> hit) {
        Document document = hit.source();
        Document.Builder documentBuilder = document != null ? document.mutate() : new Document.Builder();
        Double score = hit.score();
        if (useRrf && score != null) {
            documentBuilder.metadata(DocumentMetadata.DISTANCE.value(), score);
            documentBuilder.score(score);
        } else if (!useRrf && score != null) {
            documentBuilder.metadata(DocumentMetadata.DISTANCE.value(), 1 - normalizeSimilarityScore(hit.score()));
            documentBuilder.score(normalizeSimilarityScore(hit.score()));
        }
        return documentBuilder.build();
    }

    // more info on score/distance calculation
    // https://www.elastic.co/guide/en/elasticsearch/reference/current/knn-search.html#knn-similarity-search
    private double normalizeSimilarityScore(double score) {
        if (this.vectorStoreOptions.getSimilarity() == SimilarityFunction.l2_norm) {
            // the returned value of l2_norm is the opposite of the other functions
            // (closest to zero means more accurate), so to make it consistent
            // with the other functions the reverse is returned applying a "1-"
            // to the standard transformation
            return (1 - (Math.sqrt((1 / score) - 1)));
        }
        // cosine and dot_product
        return (2 * score) - 1;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private ElasticsearchVectorStoreOptions vectorStoreOptions;

        private ElasticsearchClient elasticsearchClient;

        private EmbeddingModel embeddingModel;

        private double similarityThreshold = SIMILARITY_THRESHOLD_ACCEPT_ALL;

        private int neighborsNum = DEFAULT_NEIGHBORS_NUM;

        private int candidateNum = DEFAULT_CANDIDATE_NUM;

        private int topK = DEFAULT_TOP_K;

        private int rankWindowSize = DEFAULT_TOP_K;

        private int rankConstant = DEFAULT_RANK_CONSTANT;

        private float bm25Bias = DEFAULT_BM25_BIAS;

        private float knnBias = DEFAULT_KNN_BIAS;

        private RetrieverType retrieverType = RetrieverType.HYBRID;

        private boolean useRrf = DEFAULT_USE_RRF;

        private FilterExpressionConverter filterExpressionConverter = new ElasticsearchAiSearchFilterExpressionConverter();

        private Supplier<Filter.Expression> filterExpression;

        public Builder vectorStoreOptions(ElasticsearchVectorStoreOptions vectorStoreOptions) {
            Assert.notNull(vectorStoreOptions, "vectorStoreOptions must not be null");
            this.vectorStoreOptions = vectorStoreOptions;
            return this;
        }

        public Builder elasticsearchClient(ElasticsearchClient elasticsearchClient) {
            Assert.notNull(elasticsearchClient, "elasticsearchClient must not be null");
            this.elasticsearchClient = elasticsearchClient;
            return this;
        }

        public Builder embeddingModel(EmbeddingModel embeddingModel) {
            Assert.notNull(embeddingModel, "embeddingModel must not be null");
            this.embeddingModel = embeddingModel;
            return this;
        }

        public Builder similarityThreshold(Double similarityThreshold) {
            this.similarityThreshold = similarityThreshold;
            return this;
        }

        public Builder neighborsNum(int neighborsNum) {
            Assert.isTrue(neighborsNum > 0, "neighborsNum must be greater than 0");
            this.neighborsNum = neighborsNum;
            return this;
        }

        public Builder candidateNum(int candidateNum) {
            Assert.isTrue(candidateNum > 0, "candidateNum must be greater than 0");
            this.candidateNum = candidateNum;
            return this;
        }

        public Builder topK(int topK) {
            Assert.isTrue(topK > 0, "topK must be greater than 0");
            this.topK = topK;
            return this;
        }

        public Builder rankWindowSize(int rankWindowSize) {
            Assert.isTrue(rankWindowSize > 0, "rankWindowSize must be greater than 0");
            Assert.isTrue(rankWindowSize >= this.topK, "rankWindowSize must be >= topK");
            this.rankWindowSize = rankWindowSize;
            return this;
        }

        public Builder rankConstant(int rankConstant) {
            Assert.isTrue(rankConstant > 0, "rankConstant must be greater than 0");
            this.rankConstant = rankConstant;
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

        public Builder retrieverType(RetrieverType retrieverType) {
            this.retrieverType = retrieverType;
            return this;
        }

        public Builder useRrf(boolean useRrf) {
            this.useRrf = useRrf;
            return this;
        }

        public Builder filterExpressionConverter(FilterExpressionConverter filterExpressionConverter) {
            this.filterExpressionConverter = filterExpressionConverter;
            return this;
        }

        public Builder filterExpression(Supplier<Filter.Expression> filterExpression) {
            this.filterExpression = filterExpression;
            return this;
        }

        public HybridElasticsearchRetriever build() {
            return new HybridElasticsearchRetriever(vectorStoreOptions, elasticsearchClient, embeddingModel, similarityThreshold,
                    neighborsNum, candidateNum, topK, rankWindowSize, rankConstant, bm25Bias, knnBias, retrieverType, useRrf,
                    filterExpressionConverter, filterExpression);
        }
    }
}
