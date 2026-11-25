# Spring AI Alibaba Autoconfigure RAG Elasticsearch Module

[中文版本](./README-zh.md)

## Introduction

Spring AI Alibaba Autoconfigure RAG Elasticsearch module is specifically designed to simplify the configuration and integration of RAG (Retrieval-Augmented Generation) functionality based on Elasticsearch. This module provides a set of automated configuration options, enabling developers to easily integrate RAG functionality into Spring Boot applications, thereby enhancing the information retrieval and generation capabilities of AI systems.

## Quick Start

### 1. add Dependency

Add the following dependencies in the `pom.xml` file of the Spring Boot project:

```xml
<dependency>
    <groupId>com.alibaba.cloud.ai</groupId>
    <artifactId>spring-ai-alibaba-autoconfigure-rag-elasticsearch</artifactId>
    <version>${latest-version}</version>
</dependency>
```

### 2. rag configuration

Add RAG related configurations in the `application.properties` or `application.yml` file:

#### Basic Configuration

```yaml
spring:
  # spring elasticsearch configuration
  elasticsearch:
    uris: http://localhost:9200
    username: test
    password: test  
  ai:
    # dashscope configuration
    dashscope:
      api-key: ${DASHSCOPE_API_KEY}
      chat:
        options:
          model: qwen-plus-2025-04-28
      embedding:
        options:
          model: text-embedding-v1
    # vector store configuration
    vectorstore:
      elasticsearch:
        initialize-schema: true
        index-name: rag_index_name
        similarity: cosine
        dimensions: 1536
    # rag configuration
    alibaba:
      rag:
        elasticsearch:
          enabled: true
```

#### Full Configuration
```yaml
spring:
  # spring elasticsearch configuration
  elasticsearch:
    uris: http://localhost:9200
    username: test
    password: test
  ai:
    # dashscope configuration
    dashscope:
      api-key: ${DASHSCOPE_API_KEY}
      chat:
        options:
          model: qwen-plus-2025-04-28
      embedding:
        options:
          model: text-embedding-v1
    # vector store configuration
    vectorstore:
      elasticsearch:
        initialize-schema: true
        index-name: rag_index_name
        similarity: cosine
        dimensions: 1536
    # rag configuration
    alibaba:
      rag:
        elasticsearch:
          # Default value: true
          enabled: true
          # Default value: false
          use-rrf: false
          # knn recall parameter configuration
          recall:
            # Similarity threshold, default value 0.0, accepts all
            similarity-threshold: 0.8
            # The number of neighbors, default value 50
            neighbors-num: 50
            # The number of candidates, default value 100
            candidate-num: 100
          # rrf parameter configuration
          rrf:
            # k value, default value 60
            rank-constant: 60
            # window size, default value 50
            rank-window-size: 50
          # support bm25, knn , hybrid, default value hybrid
          retriever-type: hybrid
          # return topK documents, default value 50
          top-k: 50
          # hybrid mode knn weight, default value 1
          knn-bias: 1
          # hybrid mode bm25 weight, default value 1
          bm25-bias: 1
```

### 3. Use the RAG component

In your Spring Boot application, directly use the automatically configured RAG component

```java
import com.alibaba.cloud.ai.rag.preretrieval.transformation.HyDeTransformer;
import com.alibaba.cloud.ai.rag.retrieval.search.HyDeRetriever;
import jakarta.annotation.Resource;

@RestController
@RequestMapping("/test")
public class TestController {

    @Resource
    private HybridElasticsearchRetriever hybridRetriever;

    @Resource
    private HyDeRetriever hyDeRetriever;

    @Resource
    private HyDeTransformer hyDeTransformer;

    @GetMapping("/retrieval/hybrid")
    public List<Document> retrievalHybrid() {
        Query query = Query.builder()
                .text("What is hybridSearch")
                .build();
        return hybridRetriever.retrieve(query);
    }

    @GetMapping("/retrieval/hyde")
    public List<Document> retrievalHyde() {
        Query query = Query.builder()
                .text("What is hybridSearch")
                .build();
        return hyDeRetriever.retrieve(query);
    }

    @GetMapping("/transform/hyde")
    public Query transformHyde() {
        Query query = Query.builder()
                .text("What is hybridSearch")
                .build();
        return hyDeTransformer.transform(query);
    }
}
```