/*
 * Copyright 2023-2025 the original author or authors.
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

package com.alibaba.cloud.ai.rag.elasticsearch;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

/**
 * Integration tests for Elasticsearch RAG components.
 *
 * @author benym
 */
@SpringBootTest()
@Testcontainers
public class ElasticsearchRagTest {

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
        registry.add("spring.ai.vectorstore.elasticsearch.index-name", () -> "spring_ai_alibaba_rag_embeddings");
        registry.add("spring.ai.vectorstore.elasticsearch.similarity", () -> "cosine");
        registry.add("spring.ai.vectorstore.elasticsearch.dimensions", () -> 1536);
        // spring es properties
        String uris = "http://" + elasticsearchContainer.getHost() + ":" + elasticsearchContainer.getMappedPort(9200);
        registry.add("spring.elasticsearch.uris", () -> uris);
    }
}
