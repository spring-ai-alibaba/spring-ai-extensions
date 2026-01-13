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

package com.alibaba.cloud.ai.rag.hyde;

import com.alibaba.cloud.ai.autoconfigure.dashscope.DashScopeChatAutoConfiguration;
import com.alibaba.cloud.ai.autoconfigure.dashscope.DashScopeEmbeddingAutoConfiguration;
import com.alibaba.cloud.ai.rag.preretrieval.transformation.HyDeTransformer;
import com.alibaba.cloud.ai.rag.retrieval.search.HyDeRetriever;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.model.chat.client.autoconfigure.ChatClientAutoConfiguration;
import org.springframework.ai.rag.Query;
import org.springframework.ai.vectorstore.elasticsearch.ElasticsearchVectorStore;
import org.springframework.ai.vectorstore.elasticsearch.autoconfigure.ElasticsearchVectorStoreAutoConfiguration;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchClientAutoConfiguration;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchRestClientAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.*;

/**
 * Integration test for HyDe RAG.
 *
 * @author benym
 */
@ImportAutoConfiguration(classes = {
        ElasticsearchVectorStoreAutoConfiguration.class,
        ElasticsearchClientAutoConfiguration.class,
        DashScopeChatAutoConfiguration.class,
        ElasticsearchRestClientAutoConfiguration.class,
        DashScopeEmbeddingAutoConfiguration.class,
        ChatClientAutoConfiguration.class
})
@SpringBootTest(classes = HyDeRagTest.class)
@Testcontainers
public class HyDeRagTest {

    private static final Logger logger = LoggerFactory.getLogger(HyDeRagTest.class);

    private static final DockerImageName ELASTICSEARCH_IMAGE = DockerImageName
            .parse("docker.elastic.co/elasticsearch/elasticsearch:8.11.0");

    @Container
    private static final ElasticsearchContainer elasticsearchContainer = new ElasticsearchContainer(ELASTICSEARCH_IMAGE)
            .withEnv("discovery.type", "single-node")
            .withEnv("xpack.security.enabled", "false")
            .withEnv("ES_JAVA_OPTS", "-Xms512m -Xmx512m")
            .withStartupAttempts(3);

    /**
     * Dynamically configure Elasticsearch properties
     */
    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        // vector store properties
        registry.add("spring.ai.vectorstore.elasticsearch.initialize-schema", () -> true);
        registry.add("spring.ai.vectorstore.elasticsearch.index-name", () -> "spring_ai_alibaba_rag_index");
        registry.add("spring.ai.vectorstore.elasticsearch.similarity", () -> "cosine");
        registry.add("spring.ai.vectorstore.elasticsearch.dimensions", () -> 1536);
        // spring es properties
        String uris = "http://" + elasticsearchContainer.getHost() + ":" + elasticsearchContainer.getMappedPort(9200);
        registry.add("spring.elasticsearch.uris", () -> uris);
        // dashscope
        registry.add("spring.ai.dashscope.api-key", () -> System.getenv("AI_DASHSCOPE_API_KEY"));
        registry.add("spring.ai.dashscope.chat.options.model", () -> "qwen-plus-2025-07-28");
        registry.add("spring.ai.dashscope.embedding.options.model", () -> "text-embedding-v1");
        registry.add("spring.ai.dashscope.chat.options.incremental-output", () -> false);
    }

    @Resource
    private ElasticsearchVectorStore elasticsearchVectorStore;

    @Resource
    private ChatClient.Builder chatClientBuilder;

    @BeforeEach
    public void addDocuments() throws InterruptedException {
        List<Document> documents = new ArrayList<>();
        Map<String, Object> metadata1 = new HashMap<>();
        metadata1.put("category", "技术文档");
        metadata1.put("word", "什么是hybridSearch？它是一种混合检索技术");
        Document document1 = Document.builder()
                .id("1")
                .text("什么是hybridSearch")
                .metadata(metadata1)
                .build();
        Map<String, Object> metadata2 = new HashMap<>();
        metadata2.put("category", "HybridSearch");
        metadata2.put("word", "HybridSearch结合了向量搜索和传统文本检索的优势");
        Document document2 = Document.builder()
                .id("2")
                .text("HybridSearch的优势")
                .metadata(metadata2)
                .build();
        Map<String, Object> metadata3 = new HashMap<>();
        metadata3.put("category", "其他");
        metadata3.put("word", "无关文档内容");
        Document document3 = Document.builder()
                .id("3")
                .text("这是一个无关的文档")
                .metadata(metadata3)
                .build();
        documents.add(document1);
        documents.add(document2);
        documents.add(document3);
        elasticsearchVectorStore.doAdd(documents);
        logger.info("add documents success");
        // Give Elasticsearch some time to process the request
        Thread.sleep(1000);
    }

    @Test
    public void testHyDeRag() {
        HyDeTransformer hyDeTransformer = HyDeTransformer.builder()
                .chatClientBuilder(chatClientBuilder)
                .build();
        HyDeRetriever hyDeRetriever = HyDeRetriever.builder()
                .hyDeTransformer(hyDeTransformer)
                .vectorStore(elasticsearchVectorStore)
                .similarityThreshold(0.5)
                .topK(50)
                .build();
        Query query = new Query("什么是hybridSearch");
        List<Document> retrieveList = hyDeRetriever.retrieve(query);
        Assertions.assertNotNull(retrieveList);
        // sort by id
        retrieveList.sort(Comparator.comparing(doc -> Integer.valueOf(doc.getId())));
        Document document = retrieveList.get(0);
        Map<String, Object> meta = document.getMetadata();
        Assertions.assertTrue(meta.containsKey("word"));
        String expectedContent = "什么是hybridSearch？它是一种混合检索技术";
        Assertions.assertEquals(expectedContent, meta.get("word"));
        Document document1 = retrieveList.get(1);
        Map<String, Object> meta2 = document1.getMetadata();
        Assertions.assertTrue(meta2.containsKey("word"));
        String expectedContent2 = "HybridSearch结合了向量搜索和传统文本检索的优势";
        Assertions.assertEquals(expectedContent2, meta2.get("word"));
    }

    @Test
    public void testHyDeRagWithFilter() {
        HyDeTransformer hyDeTransformer = HyDeTransformer.builder()
                .chatClientBuilder(chatClientBuilder)
                .build();
        FilterExpressionBuilder builder = new FilterExpressionBuilder();
        Filter.Expression expression = builder
                .eq("category", "HybridSearch")
                .build();
        Map<String, Object> context = new HashMap<>();
        context.put(HyDeRetriever.FILTER_EXPRESSION, expression);
        Query query = Query.builder()
                .text("什么是hybridSearch")
                .context(context)
                .build();
        HyDeRetriever hyDeRetriever = HyDeRetriever.builder()
                .hyDeTransformer(hyDeTransformer)
                .vectorStore(elasticsearchVectorStore)
                .similarityThreshold(0.5)
                .topK(50)
                .build();
        List<Document> retrieveList = hyDeRetriever.retrieve(query);
        Assertions.assertNotNull(retrieveList);
        Document document = retrieveList.get(0);
        Map<String, Object> meta = document.getMetadata();
        Assertions.assertTrue(meta.containsKey("word"));
        String expectedContent2 = "HybridSearch结合了向量搜索和传统文本检索的优势";
        Assertions.assertEquals(expectedContent2, meta.get("word"));
    }
}
