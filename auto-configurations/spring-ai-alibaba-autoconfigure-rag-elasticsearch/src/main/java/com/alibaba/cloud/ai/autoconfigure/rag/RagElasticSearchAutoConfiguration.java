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

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.alibaba.cloud.ai.rag.preretrieval.transformation.HyDeTransformer;
import com.alibaba.cloud.ai.rag.retrieval.search.HyDeRetriever;
import com.alibaba.cloud.ai.rag.retrieval.search.HybridElasticsearchRetriever;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.elasticsearch.ElasticsearchVectorStoreOptions;
import org.springframework.ai.vectorstore.elasticsearch.autoconfigure.ElasticsearchVectorStoreProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.util.Assert;

/**
 * Autoconfiguration class for integrating ElasticSearch as a retrieval backend in
 *
 * @author benym
 * @since 1.1.0.0-SNAPSHOT
 */
@AutoConfiguration
@EnableConfigurationProperties({RagElasticSearchProperties.class})
@ConditionalOnProperty(prefix = RagElasticSearchProperties.RAG_ES_PREFIX, name = "enabled", havingValue = "true")
public class RagElasticSearchAutoConfiguration {


    @Bean
    @ConditionalOnMissingBean(HybridElasticsearchRetriever.class)
    public HybridElasticsearchRetriever hybridElasticsearchRetriever(ElasticsearchVectorStoreProperties vectorStoreProperties,
                                                                     RagElasticSearchProperties ragElasticSearchProperties,
                                                                     ElasticsearchClient elasticsearchClient,
                                                                     EmbeddingModel embeddingModel) {
        Assert.notNull(vectorStoreProperties, "vectorStoreProperties must not be null");
        Assert.notNull(elasticsearchClient, "elasticsearchClient must not be null");
        Assert.notNull(embeddingModel, "embeddingModel must not be null");
        ElasticsearchVectorStoreOptions elasticsearchVectorStoreOptions = new ElasticsearchVectorStoreOptions();
        elasticsearchVectorStoreOptions.setIndexName(vectorStoreProperties.getIndexName());
        elasticsearchVectorStoreOptions.setDimensions(vectorStoreProperties.getDimensions());
        elasticsearchVectorStoreOptions.setSimilarity(vectorStoreProperties.getSimilarity());
        elasticsearchVectorStoreOptions.setEmbeddingFieldName(vectorStoreProperties.getEmbeddingFieldName());
        return HybridElasticsearchRetriever.builder()
                .vectorStoreOptions(elasticsearchVectorStoreOptions)
                .elasticsearchClient(elasticsearchClient)
                .embeddingModel(embeddingModel)
                .similarityThreshold(ragElasticSearchProperties.getRecall().getSimilarityThreshold())
                .neighborsNum(ragElasticSearchProperties.getRecall().getNeighborsNum())
                .candidateNum(ragElasticSearchProperties.getRecall().getCandidateNum())
                .topK(ragElasticSearchProperties.getTopK())
                .rankWindowSize(ragElasticSearchProperties.getRrf().getRankWindowSize())
                .rankConstant(ragElasticSearchProperties.getRrf().getRankConstant())
                .bm25Bias(ragElasticSearchProperties.getBm25Bias())
                .knnBias(ragElasticSearchProperties.getKnnBias())
                .retrieverType(ragElasticSearchProperties.getRetrieverType())
                .useRrf(ragElasticSearchProperties.isUseRrf())
                .build();
    }

    @Bean
    @ConditionalOnMissingBean(HyDeTransformer.class)
    public HyDeTransformer hyDeTransformer(ChatClient.Builder chatClientBuilder) {
        return HyDeTransformer.builder()
                .chatClientBuilder(chatClientBuilder)
                .build();
    }

    @Bean
    @ConditionalOnMissingBean(HyDeRetriever.class)
    public HyDeRetriever hyDeRetriever(HyDeTransformer hyDeTransformer, VectorStore vectorStore, RagElasticSearchProperties ragElasticSearchProperties) {
        return HyDeRetriever.builder()
                .hyDeTransformer(hyDeTransformer)
                .vectorStore(vectorStore)
                .similarityThreshold(ragElasticSearchProperties.getRecall().getSimilarityThreshold())
                .topK(ragElasticSearchProperties.getTopK())
                .build();
    }
}
