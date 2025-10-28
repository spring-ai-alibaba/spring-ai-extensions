# Spring AI Alibaba Rag Module

[中文版本](./README-zh.md)

## Introduction

Based on the Spring AI Rag framework and integrated with Alibaba Cloud DashScope AI service, it provides out-of-the-box RAG (Retrieval-Augmented Generation) capabilities

## Features

- **Hybrid Search** : Combining BM25 and KNN vector search, it supports Reciprocal Rank Fusion (RRF) sorting

- **Hypothetical Document Embedding (HyDE)** : Enhances retrieval performance by generating hypothetical documents

- Multiple mainstream RAG modes are supported by advisor

- Built-in rerank post-retrieval processing based on the DashScope platform

- Built-in pre-retrieval converter

## Quick Start

### 1. Add Dependency
Add the following dependencies in the 'pom.xml' of the Spring Boot project:

```xml
<dependency>
    <groupId>com.alibaba.cloud.ai</groupId>
    <artifactId>spring-ai-alibaba-rag</artifactId>
    <version>${latest-version}</version>
</dependency>
```

### 2. Through HybridElasticsearchRetriever use ElasticSearch based Hybrid Search

#### basic configuration

```java
@Configuration
public class Config {

    @Bean
    public HybridElasticsearchRetriever hybridElasticsearchRetriever(ElasticsearchVectorStoreProperties vectorStoreProperties,
                                                                     ElasticsearchClient elasticsearchClient,
                                                                     EmbeddingModel embeddingModel) {
        ElasticsearchVectorStoreOptions elasticsearchVectorStoreOptions = new ElasticsearchVectorStoreOptions();
        elasticsearchVectorStoreOptions.setIndexName(vectorStoreProperties.getIndexName());
        elasticsearchVectorStoreOptions.setDimensions(vectorStoreProperties.getDimensions());
        elasticsearchVectorStoreOptions.setSimilarity(vectorStoreProperties.getSimilarity());
        elasticsearchVectorStoreOptions.setEmbeddingFieldName(vectorStoreProperties.getEmbeddingFieldName());
        return HybridElasticsearchRetriever.builder()
                .vectorStoreOptions(elasticsearchVectorStoreOptions)
                .elasticsearchClient(elasticsearchClient)
                .embeddingModel(embeddingModel)
                .build();
    }
}
```

#### Full configuration

```java
@Configuration
public class Config {

    @Bean
    public HybridElasticsearchRetriever hybridElasticsearchRetriever(ElasticsearchVectorStoreProperties vectorStoreProperties,
                                                                     ElasticsearchClient elasticsearchClient,
                                                                     EmbeddingModel embeddingModel) {
        ElasticsearchVectorStoreOptions elasticsearchVectorStoreOptions = new ElasticsearchVectorStoreOptions();
        elasticsearchVectorStoreOptions.setIndexName(vectorStoreProperties.getIndexName());
        elasticsearchVectorStoreOptions.setDimensions(vectorStoreProperties.getDimensions());
        elasticsearchVectorStoreOptions.setSimilarity(vectorStoreProperties.getSimilarity());
        elasticsearchVectorStoreOptions.setEmbeddingFieldName(vectorStoreProperties.getEmbeddingFieldName());
        return HybridElasticsearchRetriever.builder()
                .vectorStoreOptions(elasticsearchVectorStoreOptions)
                .elasticsearchClient(elasticsearchClient)
                .embeddingModel(embeddingModel)
                .similarityThreshold(0.8)
                .neighborsNum(50)
                .candidateNum(100)
                .topK(50)
                .rankWindowSize(50)
                .rankConstant(60)
                .bm25Bias(1.0f)
                .knnBias(1.0f)
                .retrieverType(RetrieverType.HYBRID)
                .useRrf(true)
                .build();
    }
}
```

#### Retrieval filter

Use the filterExpression expression for retrieval and filtering

```java
@RestController
@RequestMapping("/test")
public class TestController {

    @Resource
    private HybridElasticsearchRetriever hybridRetriever;

    @GetMapping("/retrieval")
    public List<Document> retrieval() {
        Query query = new Query("What is hybridSearch");
        // (metadata.category == "Technical report" or metadata.category == "HybridSearch") and metadata.word = "What is hybridSearch"
        FilterExpressionBuilder builder = new FilterExpressionBuilder();
        Filter.Expression expression = builder.or(
                builder.eq("category", "Technical report"),
                builder.eq("category", "HybridSearch")
        ).build();
        query.context().put(HybridElasticsearchRetriever.FILTER_EXPRESSION, expression);
        query.context().put(HybridElasticsearchRetriever.BM25_FILED, "metadata.word");
        return hybridRetriever.retrieve(query);
    }
}
```

Search and filter using Es Query

```java
@RestController
@RequestMapping("/test")
public class TestController {

    @Resource
    private HybridElasticsearchRetriever hybridRetriever;

    @GetMapping("/retrieval")
    public List<Document> retrieval() {
        Query query = new Query("What is hybridSearch");
        // (metadata.category == "Technical report" or metadata.category == "Other") and metadata.word = "What is hybridSearch"
        co.elastic.clients.elasticsearch._types.query_dsl.Query filterQuery = QueryBuilders.bool(b -> b
                .should(QueryBuilders.term(t -> t.field("metadata.category.keyword").value("Technical report")))
                .should(QueryBuilders.term(t -> t.field("metadata.category.keyword").value("Other")))
        ).bool()._toQuery();
        // for bm25 field
        co.elastic.clients.elasticsearch._types.query_dsl.Query textQuery =
                QueryBuilders.match(m -> m
                        .field("metadata.word")
                        .query("什么是hybridSearch")
                );
        return hybridRetriever.retrieve(query, filterQuery, textQuery);
    }
}
```

### 3. Retrieve RAG based on HyDE through HyDeRetriever

#### basic configuration

```java
@Configuration
public class Config {

    @Bean
    @ConditionalOnMissingBean(HyDeTransformer.class)
    public HyDeTransformer hyDeTransformer(ChatClient.Builder chatClientBuilder) {
        return HyDeTransformer.builder()
                .chatClientBuilder(chatClientBuilder)
                .build();
    }

    @Bean
    @ConditionalOnMissingBean(HyDeRetriever.class)
    public HyDeRetriever hyDeRetriever(HyDeTransformer hyDeTransformer, VectorStore vectorStore) {
        return HyDeRetriever.builder()
                .hyDeTransformer(hyDeTransformer)
                .vectorStore(vectorStore)
                .build();
    }
}
```

#### Full configuration

```java
@Configuration
public class Config {

    @Bean
    @ConditionalOnMissingBean(HyDeTransformer.class)
    public HyDeTransformer hyDeTransformer(ChatClient.Builder chatClientBuilder) {
        return HyDeTransformer.builder()
                .chatClientBuilder(chatClientBuilder)
                .build();
    }

    @Bean
    @ConditionalOnMissingBean(HyDeRetriever.class)
    public HyDeRetriever hyDeRetriever(HyDeTransformer hyDeTransformer, VectorStore vectorStore) {
        return HyDeRetriever.builder()
                .hyDeTransformer(hyDeTransformer)
                .vectorStore(vectorStore)
                .similarityThreshold(0.8)
                .topK(50)
                .build();
    }
}
```

#### Retrieval filter

Use the filterExpression expression for retrieval and filtering

```java
@RestController
@RequestMapping("/test")
public class TestController {

    @Resource
    private HyDeRetriever hyDeRetriever;

    @GetMapping("/retrieval")
    public List<Document> retrieval() {
        Query query = new Query("What is hybridSearch");
        // (metadata.category == "Technical report" or metadata.category == "HybridSearch") 
        FilterExpressionBuilder builder = new FilterExpressionBuilder();
        Filter.Expression expression = builder.or(
                builder.eq("category", "Technical report"),
                builder.eq("category", "HybridSearch")
        ).build();
        query.context().put(HyDeRetriever.FILTER_EXPRESSION, expression);
        return hyDeRetriever.retrieve(query);
    }
}
```

## Using Modular RAG Components

### 1. Pre-retrieval Processing

- HyDeTransformer: Transforms the current query to generate hypothetical document answers

HyDeTransformer implements `org.springframework.ai.rag.preretrieval.query.transformation.QueryTransformer` and can be used as a RAG pre-retrieval processor

### 2. Post-retrieval Processing

- DashScopeRerankPostProcessor: A rerank post-retrieval processor based on the DashScope platform

DashScopeRerankPostProcessor implements `org.springframework.ai.rag.postretrieval.document.DocumentPostProcessor` and can be used as a RAG post-retrieval processor

### 3. RAG Mode Advisors

- HybridSearchAdvisor: A RAG advisor based on Hybrid Search
```
┌─────────────────┐    ┌──────────────────┐    ┌─────────────────┐
│   User Query    │    │  Query Transformer │    │  Query Rewriter │
│                 │───▶│(Query Translation, │───▶│(Multi-perspective│
│                 │    │   HyDE Enhancement)│    │    Rewriting)   │
└─────────────────┘    └──────────────────┘    └─────────────────┘
                                                              │
                                                              ├─ Query 1 ─┐
                                                              ├─ Query 2 ─┤
                                                              ├─ ...   ──┤
                                                              └─ Query N ─┘
                                                                      │
                                                                      ▼
┌─────────────────┐    ┌──────────────────┐    ┌─────────────────┐
│      LLM        │    │Retrieval Post-proc.│    │  Hybrid Search  │
│(Retrieval Results│◀──│(Doc Merging, Model │◀──│(KNN+BM25+RRF)   │
│   as Context)   │    │    Re-ranking, etc.)│    │                 │
└─────────────────┘    └──────────────────┘    └─────────────────┘
```
- MultiQueryRetrieverAdvisor: RAG Advisor based on multi-query rewriting

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   User Query    │───▶│ Query Rewriter  │───▶│  Retriever      │
│                 │    │(Multi-perspective│    │  Search         │
│                 │    │    Rewriting)   │    │                 │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                                │                      │
                                ▼                      ▼
                       ┌─────────────────┐    ┌─────────────────┐
                       │  Query Angle 1  │    │ Retrieval Result 1│
                       └─────────────────┘    └─────────────────┘
                                │                      │
                                ▼                      ▼
                       ┌─────────────────┐    ┌─────────────────┐
                       │       ...       │    │       ...       │
                       └─────────────────┐    └─────────────────┘
                                │                      │
                                ▼                      ▼
                       ┌─────────────────┐    ┌─────────────────┐
                       │  Query Angle N  │    │ Retrieval Result N│
                       └─────────────────┘    └─────────────────┘
                                └──────────┬───────────┘
                                           ▼
                                  ┌─────────────────┐
                                  │      LLM        │
                                  │(Multi-angle     │
                                  │ Retrieval Results│
                                  │  as Context)    │
                                  └─────────────────┘
```