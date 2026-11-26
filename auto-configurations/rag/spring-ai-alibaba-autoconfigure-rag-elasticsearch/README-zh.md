# Spring AI Alibaba Autoconfigure RAG Elasticsearch 模块

[English](./README.md)

## 简介

Spring AI Alibaba Autoconfigure RAG Elasticsearch模块，专门用于简化基于Elasticsearch的RAG（Retrieval-Augmented Generation）功能的配置和集成。该模块提供了一套自动化配置选项，使开发者能够轻松地将RAG功能集成到Spring Boot应用中，从而提升AI系统的信息检索和生成能力。

## 快速开始

### 1. 添加依赖
在Spring Boot项目的`pom.xml`文件中添加以下依赖：

```xml
<dependency>
    <groupId>com.alibaba.cloud.ai</groupId>
    <artifactId>spring-ai-alibaba-autoconfigure-rag-elasticsearch</artifactId>
    <version>${最新版本}</version>
</dependency>
```

### 2. 配置rag参数
在`application.properties`或`application.yml`文件中添加RAG相关配置：

#### 基本配置

```yaml
spring:
  # spring elasticsearch配置
  elasticsearch:
    uris: http://localhost:9200
    username: test
    password: test  
  ai:
    # dashscope配置
    dashscope:
      api-key: ${DASHSCOPE_API_KEY}
      chat:
        options:
          model: qwen-plus-2025-04-28
      embedding:
        options:
          model: text-embedding-v1
    # vector store配置
    vectorstore:
      elasticsearch:
        initialize-schema: true
        index-name: rag_index_name
        similarity: cosine
        dimensions: 1536
    # rag配置
    alibaba:
      rag:
        elasticsearch:
          enabled: true
```

#### 完全配置
```yaml
spring:
  # spring elasticsearch配置
  elasticsearch:
    uris: http://localhost:9200
    username: test
    password: test
  ai:
    # dashscope配置
    dashscope:
      api-key: ${DASHSCOPE_API_KEY}
      chat:
        options:
          model: qwen-plus-2025-04-28
      embedding:
        options:
          model: text-embedding-v1
    # vector store配置
    vectorstore:
      elasticsearch:
        initialize-schema: true
        index-name: rag_index_name
        similarity: cosine
        dimensions: 1536
    # rag配置
    alibaba:
      rag:
        elasticsearch:
          # 默认值true
          enabled: true
          # 默认值false
          use-rrf: false
          # knn召回参数配置
          recall:
            # 相似度阈值，默认值0.0接受所有
            similarity-threshold: 0.8
            # 邻居数量，默认值50
            neighbors-num: 50
            # 候选集数量，默认值100
            candidate-num: 100
          # rrf重排参数配置
          rrf:
            # k值，默认值60
            rank-constant: 60
            # 窗口大小，默认值50
            rank-window-size: 50
          # 支持bm25, knn , hybrid，默认值hybrid
          retriever-type: hybrid
          # 返回topK个文档，默认值50
          top-k: 50
          # hybrid模式下knn权重，默认值1
          knn-bias: 1
          # hybrid模式下bm25权重，默认值1
          bm25-bias: 1
```

### 3. 使用自动注入的RAG组件

在你的Spring Boot应用中，直接使用自动配置的RAG组件：

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
                .text("什么是hybridSearch")
                .build();
        return hybridRetriever.retrieve(query);
    }

    @GetMapping("/retrieval/hyde")
    public List<Document> retrievalHyde() {
        Query query = Query.builder()
                .text("什么是hybridSearch")
                .build();
        return hyDeRetriever.retrieve(query);
    }

    @GetMapping("/transform/hyde")
    public Query transformHyde() {
        Query query = Query.builder()
                .text("什么是hybridSearch")
                .build();
        return hyDeTransformer.transform(query);
    }
}
```