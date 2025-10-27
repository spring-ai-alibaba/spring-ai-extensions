package com.alibaba.cloud.ai.rag.advisor;

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
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Expand the original query into multiple queries for Retrieval
 *
 * @author benym
 */
public class MultiQueryRetrieverAdvisor implements BaseAdvisor {

    public static final String DOCUMENT_CONTEXT = "spring_ai_alibaba_rag_document_context";

    private final QueryExpander queryExpander;

    private final QueryAugmenter queryAugmenter;

    private final DocumentRetriever documentRetriever;

    private final int order;

    public MultiQueryRetrieverAdvisor(QueryExpander queryExpander, QueryAugmenter queryAugmenter,
                                      DocumentRetriever documentRetriever, Integer order) {
        Assert.notNull(documentRetriever, "documentRetriever cannot be null");
        this.queryExpander = queryExpander;
        this.queryAugmenter = queryAugmenter != null ? queryAugmenter : ContextualQueryAugmenter.builder().build();
        this.documentRetriever = documentRetriever;
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
        // 2. Expand query into one or multiple queries.
        List<Query> expandedQueries = this.queryExpander != null ? this.queryExpander.expand(originalQuery)
                : List.of(originalQuery);
        List<Document> allRetrievedDocuments = new ArrayList<>();
        for (Query query : expandedQueries) {
            List<Document> retrieveDocuments = documentRetriever.retrieve(query);
            allRetrievedDocuments.addAll(retrieveDocuments);
        }
        context.put(DOCUMENT_CONTEXT, allRetrievedDocuments);
        // 3. Augment user query with the document contextual data.
        Query augmentedQuery = this.queryAugmenter.augment(originalQuery, allRetrievedDocuments);
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


    public static final class Builder {

        private QueryExpander queryExpander;

        private QueryAugmenter queryAugmenter;

        private DocumentRetriever documentRetriever;

        private int order;

        private Builder() {
        }

        public static Builder builder() {
            return new Builder();
        }

        public Builder queryExpander(QueryExpander queryExpander) {
            this.queryExpander = queryExpander;
            return this;
        }

        public Builder queryAugmenter(QueryAugmenter queryAugmenter) {
            this.queryAugmenter = queryAugmenter;
            return this;
        }

        public Builder documentRetriever(DocumentRetriever documentRetriever) {
            Assert.notNull(documentRetriever, "documentRetriever cannot be null");
            this.documentRetriever = documentRetriever;
            return this;
        }

        public Builder order(int order) {
            this.order = order;
            return this;
        }

        public MultiQueryRetrieverAdvisor build() {
            return new MultiQueryRetrieverAdvisor(queryExpander, queryAugmenter, documentRetriever, order);
        }
    }
}
