# Spring AI Alibaba Rag 模块

[English](./README.md)

## 简介

基于Spring AI Rag框架，集成阿里云DashScope AI服务，提供开箱即用的RAG（Retrieval-Augmented Generation）能力

## 功能特性
- **Hybrid Search**：结合BM25和KNN向量搜索, 支持Reciprocal Rank Fusion (RRF) 排序
- **Hypothetical Document Embedding (HyDE)** : 通过生成假设性文档来增强检索效果
- 多种主流RAG模式 advisor支持
- 内置基于DashScope平台的重排序后置检索处理
- 内置前置检索转化器

## 快速开始

### 1. 添加依赖
在Spring Boot项目的`pom.xml`中添加以下依赖：

```xml
<dependency>
    <groupId>com.alibaba.cloud.ai</groupId>
    <artifactId>spring-ai-alibaba-rag</artifactId>
    <version>${最新版本}</version>
</dependency>
```

### 2. 通过HybridElasticsearchRetriever使用基于ElasticSearch的Hybrid Search

#### 基本配置

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

#### 完全配置

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

#### 检索过滤

使用filterExpression表达式进行检索过滤

```java
@RestController
@RequestMapping("/test")
public class TestController {

    @Resource
    private HybridElasticsearchRetriever hybridRetriever;

    @GetMapping("/retrieval")
    public List<Document> retrieval() {
        // (metadata.category == "技术文档" or metadata.category == "HybridSearch") and metadata.word = "什么是hybridSearch"
        // expression会自动添加metadata
        FilterExpressionBuilder builder = new FilterExpressionBuilder();
        Filter.Expression expression = builder.or(
                builder.eq("category", "技术文档"),
                builder.eq("category", "HybridSearch")
        ).build();
        Map<String, Object> context = new HashMap<>();
        context.put(HybridElasticsearchRetriever.FILTER_EXPRESSION, expression);
        context.put(HybridElasticsearchRetriever.BM25_FILED, "metadata.word");
        Query query = Query.builder()
                .text("什么是hybridSearch")
                .context(context)
                .build();
        return hybridRetriever.retrieve(query);
    }
}
```

使用Es Query进行检索过滤

```java
@RestController
@RequestMapping("/test")
public class TestController {

    @Resource
    private HybridElasticsearchRetriever hybridRetriever;

    @GetMapping("/retrieval")
    public List<Document> retrieval() {
        Query query = new Query("什么是hybridSearch");
        // (metadata.category == "技术文档" or metadata.category == "其他") and metadata.word = "什么是hybridSearch"
        co.elastic.clients.elasticsearch._types.query_dsl.Query filterQuery = QueryBuilders.bool(b -> b
                .should(QueryBuilders.term(t -> t.field("metadata.category.keyword").value("技术文档")))
                .should(QueryBuilders.term(t -> t.field("metadata.category.keyword").value("其他")))
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

### 3. 通过HyDeRetriever使用基于HyDE的RAG检索

#### 基本配置

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

#### 完全配置

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

#### 检索过滤

使用filterExpression表达式进行检索过滤

```java
@RestController
@RequestMapping("/test")
public class TestController {

    @Resource
    private HyDeRetriever hyDeRetriever;

    @GetMapping("/retrieval")
    public List<Document> retrieval() {
        // (metadata.category == "技术文档" or metadata.category == "HybridSearch") 
        FilterExpressionBuilder builder = new FilterExpressionBuilder();
        Filter.Expression expression = builder.or(
                builder.eq("category", "技术文档"),
                builder.eq("category", "HybridSearch")
        ).build();
        Map<String, Object> context = new HashMap<>();
        context.put(HyDeRetriever.FILTER_EXPRESSION, expression);
        Query query = Query.builder()
                .text("什么是hybridSearch")
                .context(context)
                .build();
        return hyDeRetriever.retrieve(query);
    }
}
```

## 使用模块化RAG组件

### 1. 检索前处理

- HyDeTransformer: 转化当前查询query生成假设性文档答案

HyDeTransformer实现了org.springframework.ai.rag.preretrieval.query.transformation.QueryTransformer，可作为RAG检索前处理器使用

### 2. 检索后处理

- DashScopeRerankPostProcessor: 基于DashScope平台的重排序后置检索处理器

DashScopeRerankPostProcessor实现了org.springframework.ai.rag.postretrieval.document.DocumentPostProcessor，可作为RAG检索后处理器使用

### 3. RAG模式的Advisors

- HybridSearchAdvisor: 基于Hybrid Search的RAG Advisor
```
┌─────────────────┐    ┌──────────────────┐    ┌─────────────────┐
│   用户提问       │    │   问题转化器      │    │    问题改写      │
│                 │───▶│（问题翻译，HyDE增强等）│───▶│（多角度改写）│
└─────────────────┘    └──────────────────┘    └─────────────────┘
                                                              │
                                                              ├─ 问题1 ─┐
                                                              ├─ 问题2 ─┤
                                                              ├─ ...  ─┤
                                                              └─ 问题N ─┘
                                                                      │
                                                                      ▼
┌─────────────────┐    ┌──────────────────┐    ┌─────────────────┐
│     LLM         │    │   检索后处理      │    │    混合搜索      │
│ 检索结果作为上下文│◀──│（文档合并，模型重排序等）│◀──│（KNN+BM25+RRF）│
└─────────────────┘    └──────────────────┘    └─────────────────┘
```
- MultiQueryRetrieverAdvisor: 基于多查询改写的RAG Advisor

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   用户提问       │───▶│    问题改写      │───▶│   检索器检索     │
│                 │    │ （多角度改写）    │    │                 │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                                │                      │
                                ▼                      ▼
                       ┌─────────────────┐    ┌─────────────────┐
                       │   问题角度1      │    │   检索结果1      │
                       └─────────────────┘    └─────────────────┘
                                │                      │
                       ┌─────────────────┐    ┌─────────────────┐
                       │      ...        │    │      ...        │
                       └─────────────────┘    └─────────────────┘
                                │                      │
                       ┌─────────────────┐    ┌─────────────────┐
                       │   问题角度N      │    │   检索结果N      │
                       └─────────────────┘    └─────────────────┘
                                └──────────┬───────────┘
                                           ▼
                                  ┌─────────────────┐
                                  │      LLM        │
                                  │（多角度检索结果   │
                                  │ 作为上下文联合问题）│
                                  └─────────────────┘
```