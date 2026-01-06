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

package com.alibaba.cloud.ai.rag.advisor;

import com.alibaba.cloud.ai.rag.postretrieval.DashScopeRerankPostProcessor;
import com.alibaba.cloud.ai.rag.preretrieval.transformation.HyDeTransformer;
import com.alibaba.cloud.ai.rag.retrieval.search.HybridDocumentRetriever;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.AdvisorChain;
import org.springframework.ai.chat.client.advisor.api.BaseAdvisor;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.document.Document;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;
import org.springframework.ai.rag.generation.augmentation.QueryAugmenter;
import org.springframework.ai.rag.postretrieval.document.DocumentPostProcessor;
import org.springframework.ai.rag.preretrieval.query.expansion.QueryExpander;
import org.springframework.ai.rag.preretrieval.query.transformation.QueryTransformer;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Hybrid Search Advisor
 * Adopt the architecture of hybrid search combined with the rerank model
 *
 * @author benym
 * @since 1.1.0.0-SNAPSHOT
 */
public class HybridSearchAdvisor implements BaseAdvisor {

    public static final String DOCUMENT_CONTEXT = "spring_ai_alibaba_rag_document_context";

    private final List<QueryTransformer> queryTransformers;

    private final QueryExpander queryExpander;

    private final HybridDocumentRetriever hybridDocumentRetriever;

    private final List<DocumentPostProcessor> documentPostProcessors;

    private final QueryAugmenter queryAugmenter;

    private final int order;

    private final HyDeTransformer hyDeTransformer;

    private final DashScopeRerankPostProcessor dashScopeRerankPostProcessor;

    public HybridSearchAdvisor(List<QueryTransformer> queryTransformers,
                               QueryExpander queryExpander,
                               @Nullable HybridDocumentRetriever hybridDocumentRetriever,
                               List<DocumentPostProcessor> documentPostProcessors, QueryAugmenter queryAugmenter,
                               HyDeTransformer hyDeTransformer, DashScopeRerankPostProcessor dashScopeRerankPostProcessor, Integer order) {
        Assert.notNull(hybridDocumentRetriever, "hybridDocumentRetriever must not be null");
        this.queryTransformers = queryTransformers;
        this.queryExpander = queryExpander;
        this.hybridDocumentRetriever = hybridDocumentRetriever;
        this.documentPostProcessors = documentPostProcessors;
        this.queryAugmenter = queryAugmenter != null ? queryAugmenter : ContextualQueryAugmenter.builder().build();
        this.hyDeTransformer = hyDeTransformer;
        this.dashScopeRerankPostProcessor = dashScopeRerankPostProcessor;
        this.order = order != null ? order : 0;
    }

    @Override
    public ChatClientRequest before(ChatClientRequest chatClientRequest, AdvisorChain advisorChain) {
        Map<String, Object> context = new HashMap<>(chatClientRequest.context());
        // 1. Create a query from the user text, parameters, and conversation history.
        Query originalQuery = Query.builder()
                .text(chatClientRequest.prompt().getUserMessage().getText())
                .history(chatClientRequest.prompt().getInstructions())
                .context(context)
                .build();
        // 2. Apply query transformers
        Query transformedQuery = originalQuery;
        if (!CollectionUtils.isEmpty(this.queryTransformers)) {
            if (hyDeTransformer != null) {
                this.queryTransformers.add(hyDeTransformer);
            }
            for (var queryTransformer : this.queryTransformers) {
                transformedQuery = queryTransformer.apply(transformedQuery);
            }
        }
        // 3. Expand query into one or multiple queries.
        List<Query> expandedQueries = this.queryExpander != null ? this.queryExpander.expand(transformedQuery)
                : List.of(transformedQuery);
        List<Document> allRetrievedDocuments = new ArrayList<>();
        for (Query query : expandedQueries) {
            List<Document> retrieveDocuments = hybridDocumentRetriever.retrieve(query);
            allRetrievedDocuments.addAll(retrieveDocuments);
        }
        // 4. Post-process the documents.
        List<Document> resultDocuments = new ArrayList<>();
        if (!CollectionUtils.isEmpty(documentPostProcessors)) {
            if (dashScopeRerankPostProcessor != null) {
                this.documentPostProcessors.add(dashScopeRerankPostProcessor);
            }
            for (var documentPostProcessor : this.documentPostProcessors) {
                resultDocuments = documentPostProcessor.process(originalQuery, allRetrievedDocuments);
            }
        }
        context.put(DOCUMENT_CONTEXT, resultDocuments);
        // 5. Augment user query with the document contextual data.
        Query augmentedQuery = this.queryAugmenter.augment(originalQuery, resultDocuments);
        return chatClientRequest.mutate()
                .prompt(chatClientRequest.prompt().augmentUserMessage(augmentedQuery.text()))
                .context(context)
                .build();
    }

    @Override
    public ChatClientResponse after(ChatClientResponse chatClientResponse, AdvisorChain advisorChain) {
        ChatResponse.Builder chatResponseBuilder;
        if (chatClientResponse.chatResponse() == null) {
            chatResponseBuilder = ChatResponse.builder();
        } else {
            chatResponseBuilder = ChatResponse.builder().from(chatClientResponse.chatResponse());
        }
        chatResponseBuilder.metadata(DOCUMENT_CONTEXT, chatClientResponse.context().get(DOCUMENT_CONTEXT));
        return ChatClientResponse.builder()
                .chatResponse(chatResponseBuilder.build())
                .context(chatClientResponse.context())
                .build();
    }

    @Override
    public int getOrder() {
        return this.order;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private List<QueryTransformer> queryTransformers = new ArrayList<>();

        private QueryExpander queryExpander;

        private HybridDocumentRetriever hybridDocumentRetriever;

        private List<DocumentPostProcessor> documentPostProcessors = new ArrayList<>();

        private QueryAugmenter queryAugmenter;

        private int order;

        private HyDeTransformer hyDeTransformer;

        private DashScopeRerankPostProcessor dashScopeRerankPostProcessor;

        private Builder() {
        }

        public Builder queryTransformers(List<QueryTransformer> queryTransformers) {
            this.queryTransformers = queryTransformers;
            return this;
        }

        public Builder queryExpander(QueryExpander queryExpander) {
            this.queryExpander = queryExpander;
            return this;
        }

        public Builder hybridDocumentRetriever(HybridDocumentRetriever hybridDocumentRetriever) {
            Assert.notNull(hybridDocumentRetriever, "hybridDocumentRetriever must not be null");
            this.hybridDocumentRetriever = hybridDocumentRetriever;
            return this;
        }

        public Builder documentPostProcessors(List<DocumentPostProcessor> documentPostProcessors) {
            this.documentPostProcessors = documentPostProcessors;
            return this;
        }

        public Builder queryAugmenter(QueryAugmenter queryAugmenter) {
            this.queryAugmenter = queryAugmenter;
            return this;
        }

        public Builder order(int order) {
            this.order = order;
            return this;
        }

        public Builder hyDeTransformer(HyDeTransformer hyDeTransformer) {
            this.hyDeTransformer = hyDeTransformer;
            return this;
        }

        public Builder dashScopeRerankPostProcessor(DashScopeRerankPostProcessor dashScopeRerankPostProcessor) {
            this.dashScopeRerankPostProcessor = dashScopeRerankPostProcessor;
            return this;
        }

        public HybridSearchAdvisor build() {
            return new HybridSearchAdvisor(queryTransformers, queryExpander, hybridDocumentRetriever,
                    documentPostProcessors, queryAugmenter, hyDeTransformer, dashScopeRerankPostProcessor, order);
        }
    }
}
