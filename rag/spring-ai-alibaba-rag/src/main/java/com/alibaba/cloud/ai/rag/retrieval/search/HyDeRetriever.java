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

import com.alibaba.cloud.ai.rag.preretrieval.transformation.HyDeTransformer;
import org.springframework.ai.document.Document;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionTextParser;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.function.Supplier;

/**
 * Hypothetical Document Embeddings Retriever implementation.
 *
 * @author benym
 * @since 1.1.0.0-SNAPSHOT
 */
public class HyDeRetriever implements DocumentRetriever {

    public static final String FILTER_EXPRESSION = "vector_store_filter_expression";

    private final VectorStore vectorStore;

    private final Double similarityThreshold;

    private final Integer topK;

    // Supplier to allow for lazy evaluation of the filter expression,
    // which may depend on the execution content. For example, you may want to
    // filter dynamically based on the current user's identity or tenant ID.
    private final Supplier<Filter.Expression> filterExpression;

    private final HyDeTransformer hyDeTransformer;

    public HyDeRetriever(@Nullable HyDeTransformer hyDeTransformer, @Nullable VectorStore vectorStore, @Nullable Double similarityThreshold,
                         @Nullable Integer topK, Supplier<Filter.Expression> filterExpression) {
        Assert.notNull(hyDeTransformer, "hyDeTransformer must not be null");
        Assert.notNull(vectorStore, "vectorStore cannot be null");
        this.hyDeTransformer = hyDeTransformer;
        this.vectorStore = vectorStore;
        this.similarityThreshold = similarityThreshold != null ? similarityThreshold
                : SearchRequest.SIMILARITY_THRESHOLD_ACCEPT_ALL;
        this.topK = topK != null ? topK : SearchRequest.DEFAULT_TOP_K;
        this.filterExpression = filterExpression != null ? filterExpression : () -> null;
    }

    @Override
    public List<Document> retrieve(Query query) {
        Assert.notNull(query, "query must not be null");
        Query hyDeAnswer = hyDeTransformer.transform(query);
        var requestFilterExpression = computeRequestFilterExpression(query);
        var searchRequest = SearchRequest.builder()
                .query(hyDeAnswer.text())
                .filterExpression(requestFilterExpression)
                .similarityThreshold(this.similarityThreshold)
                .topK(this.topK)
                .build();
        return this.vectorStore.similaritySearch(searchRequest);
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
     * @param query the query containing potential context with filter expression
     * @return the filter expression to use for the request
     */
    private Filter.Expression computeRequestFilterExpression(Query query) {
        var contextFilterExpression = query.context().get(FILTER_EXPRESSION);
        if (contextFilterExpression != null) {
            if (contextFilterExpression instanceof Filter.Expression) {
                return (Filter.Expression) contextFilterExpression;
            }
            else if (StringUtils.hasText(contextFilterExpression.toString())) {
                return new FilterExpressionTextParser().parse(contextFilterExpression.toString());
            }
        }
        return this.filterExpression.get();
    }

    public static Builder builder() {
        return new Builder();
    }


    public static final class Builder {

        private VectorStore vectorStore;

        private Double similarityThreshold = SearchRequest.SIMILARITY_THRESHOLD_ACCEPT_ALL;

        private Integer topK = SearchRequest.DEFAULT_TOP_K;

        private Supplier<Filter.Expression> filterExpression;

        private HyDeTransformer hyDeTransformer;

        private Builder() {
        }

        public static Builder builder() {
            return new Builder();
        }

        public Builder vectorStore(VectorStore vectorStore) {
            Assert.notNull(vectorStore, "vectorStore must not be null");
            this.vectorStore = vectorStore;
            return this;
        }

        public Builder similarityThreshold(Double similarityThreshold) {
            Assert.isTrue(similarityThreshold >= 0, "similarityThreshold must not be negative");
            this.similarityThreshold = similarityThreshold;
            return this;
        }

        public Builder topK(Integer topK) {
            Assert.isTrue(topK >= 0, "topK must not be negative");
            this.topK = topK;
            return this;
        }

        public Builder filterExpression(Supplier<Filter.Expression> filterExpression) {
            this.filterExpression = filterExpression;
            return this;
        }

        public Builder hyDeTransformer(HyDeTransformer hyDeTransformer) {
            Assert.notNull(hyDeTransformer, "hyDeTransformer must not be null");
            this.hyDeTransformer = hyDeTransformer;
            return this;
        }

        public HyDeRetriever build() {
            return new HyDeRetriever(hyDeTransformer, vectorStore, similarityThreshold, topK, filterExpression);
        }
    }
}
