package com.alibaba.cloud.ai.rag.retrieval.search;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentMetadata;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.model.EmbeddingUtils;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
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
 */
public class HybridElasticsearchRetriever implements DocumentRetriever {

    private static final String FILTER_EXPRESSION = "spring_ai_alibaba_rag_filter_expression";

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
    private final Float similarityThreshold;

    /**
     * Number of neighbors for Knn search
     */
    private final int neighborsNum;

    /**
     * Number of candidates for Knn search
     */
    private final int candidateNum;

    /**
     * Maximum number of documents to return in search results
     */
    private final int topK;

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

    /**
     * Whether to use Reciprocal Rank Fusion (RRF) scoring
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
                                        EmbeddingModel embeddingModel,
                                        Float similarityThreshold, int neighborsNum, int candidateNum, int topK,
                                        int rrfK, float bm25Bias, float knnBias, boolean useHybrid, boolean useRrf,
                                        FilterExpressionConverter filterExpressionConverter,
                                        Supplier<Filter.Expression> filterExpression) {
        this.vectorStoreOptions = vectorStoreOptions;
        this.elasticsearchClient = elasticsearchClient;
        this.embeddingModel = embeddingModel;
        this.similarityThreshold = similarityThreshold;
        this.neighborsNum = neighborsNum;
        this.candidateNum = candidateNum;
        this.topK = topK;
        this.rrfK = rrfK;
        this.bm25Bias = bm25Bias;
        this.knnBias = knnBias;
        this.useHybrid = useHybrid;
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

    /**
     * Execute a hybrid search using BM25 and KNN search with Reciprocal Rank Fusion.
     *
     * @param query The query to search for
     * @return A list of documents matching the query
     */
    private List<Document> search(Query query) throws IOException {
        // 1. Compute the filter expression to use for the request
        var requestFilterExpression = computeRequestFilterExpression(query);
        float[] vector = embeddingModel.embed(query.text());
        // 2. Build search request
        SearchResponse<Document> response = elasticsearchClient.search(
                sr -> buildSearchRequest(sr, vector, requestFilterExpression, query.text()),
                Document.class
        );
        // 3. Convert search response to documents
        return response.hits().hits().stream().map(this::toDocument).collect(Collectors.toList());
    }

    private SearchRequest.Builder buildSearchRequest(SearchRequest.Builder sr, float[] vector, Filter.Expression filterExpression, String queryText) {
        // 1. Knn search
        SearchRequest.Builder builder = sr.index(vectorStoreOptions.getIndexName())
                .knn(k -> k.queryVector(EmbeddingUtils.toList(vector))
                        .similarity(similarityThreshold)
                        .k(neighborsNum)
                        .field(vectorStoreOptions.getEmbeddingFieldName())
                        .numCandidates(candidateNum)
                        .filter(fl -> fl
                                .queryString(qs -> qs.query(getElasticsearchQueryString(filterExpression))))
                        .boost(knnBias))
                .size(topK);

        // 2. Bm25 search
        if (useHybrid) {
            builder.query(q -> q.match(m -> m.field("content")
                    .query(escape(queryText))
                    .boost(bm25Bias)));
        }
        // 3. RRF
        if (useRrf) {
            builder.rank(r -> r.rrf(rrf -> rrf.rankConstant((long) rrfK)
                    .rankWindowSize((long) topK)));
        }
        return builder;
    }

    private static String escape(String text) {
        return text.replace("\"", "\\\"");
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

        private ElasticsearchClient elasticsearchClient;

        private EmbeddingModel embeddingModel;

        private String indexName;

        private int windowSize;

        private int rrfK;

        private float bm25Bias;

        private float knnBias;

        private boolean useHybrid;

        private FilterExpressionConverter filterExpressionConverter = new ElasticsearchAiSearchFilterExpressionConverter();

        private Supplier<Filter.Expression> filterExpression;

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

        public Builder filterExpressionConverter(FilterExpressionConverter converter) {
            Assert.notNull(converter, "filterExpressionConverter must not be null");
            this.filterExpressionConverter = converter;
            return this;
        }

        public Builder filterExpression(Supplier<Filter.Expression> filterExpression) {
            this.filterExpression = filterExpression;
            return this;
        }

        public HybridElasticsearchRetriever build() {
            return null;
        }
    }
}
