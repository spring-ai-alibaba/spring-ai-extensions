# Alibaba Extensions for Spring AI

This project builds upon Spring AI, providing extended implementations of core concepts such as `ChatModel`, `ImageModel`, `AudioModel`, `MCP`, `DocumentParser`, `ChatMemory`, `ToolCallback`, `VectorStore`, etc. It helps developers quickly integrate with Alibaba Cloud Bailian model services, vector database services, chat memory components, tool calling, and other features.

Based on these components, developers can use Spring AI [ChatClient](https://java2ai.com/docs/1.0.0.2/tutorials/basics/chat-client/), or [Spring AI Alibaba Agent Framework](https://github.com/alibaba/spring-ai-alibaba) to quickly build their own AI agent applications. Please choose according to your specific use case.

## Get Started

### Prerequsites

1. Requires JDK 17+.
2. If there are any `spring-ai` dependency issue, please lean how to configure the `spring-milestones` Maven repository on [FAQ page](https://java2ai.com/docs/1.0.0.2/faq).

### Use `ChatClient` to Develop a Chatbot

#### Add Dependencies
To quickly get started with Spring AI Alibaba, add 'spring-ai-alibaba-starter-dashscope' dependency to your java project.

```xml
<dependencyManagement>
  <dependencies>
    <dependency>
      <groupId>com.alibaba.cloud.ai</groupId>
      <artifactId>spring-ai-extensions-bom</artifactId>
      <version>1.1.0.0-SNAPSHOT</version>
      <type>pom</type>
      <scope>import</scope>
    </dependency>
  </dependencies>
</dependencyManagement>

<dependencies>
  <dependency>
    <groupId>com.alibaba.cloud.ai</groupId>
    <artifactId>spring-ai-alibaba-starter-dashscope</artifactId>
  </dependency>
</dependencies>
```

#### Declare ChatClient
Decare a `ChatClient` instance that would have `DashScopeChatModel` automatically injected.

```java
@RestController
@RequestMapping("/helloworld")
public class HelloworldController {
  private static final String DEFAULT_PROMPT = "你是一个博学的智能聊天助手，请根据用户提问回答！";
  private final ChatClient dashScopeChatClient;

  public HelloworldController(ChatClient.Builder chatClientBuilder) {
    this.dashScopeChatClient = chatClientBuilder
        .defaultSystem(DEFAULT_PROMPT)
        .defaultAdvisors(
            new SimpleLoggerAdvisor()
        )
        .defaultOptions(
            DashScopeChatOptions.builder()
                .withTopP(0.7)
                .build()
        )
        .build();
  }

  @GetMapping("/simple/chat")
  public String simpleChat(@RequestParam(value = "query") String query) {
    return dashScopeChatClient.prompt(query).call().content();
  }
}
```

Please check [Quick Start](https://java2ai.com/docs/1.0.0.2/get-started/chatbot) on our official website to learn more details.

### Use Agent Framework to Develop an Agent

// TBD

## Playground and Example

The community has developed a [Playground](https://github.com/springaialibaba/spring-ai-alibaba-examples/tree/main/spring-ai-alibaba-playground) agent that includes a complete front-end UI and back-end implementation. The Playground back-end is developed using Spring AI Alibaba and gives users a quick overview of all core framework capabilities such as chatbot, multi-round conversations, image generation, multi-modality, tool calling, MCP, and RAG.

<p align="center">
    <img src="./docs/imgs/playground.png" alt="PlayGround" style="max-width: 949px; height: 537px; border-radius: 15px; box-shadow: 0 4px 15px rgba(0, 0, 0, 0.3);" />
</p>

You can [deploy the Playground example locally](https://github.com/springaialibaba/spring-ai-alibaba-examples) and access the experience through your browser, or copy the source code and tweak it to your own business needs to build your own set of AI apps more quickly.
For more examples, please refer to our official example repository: [https://github.com/springaialibaba/spring-ai-alibaba-examples](https://github.com/springaialibaba/spring-ai-alibaba-examples)

## Available Extensions

* Model
* MCP
* ToolCallback
* VectorStore
* ChatMemory
* RAG
* DocumentParser & DocumentReader
* Prompt Management
* Observation

### Models

Spring AI Alibaba provides comprehensive model implementations through DashScope (Alibaba Cloud's AI model service platform):

#### DashScopeChatModel

The DashScope Chat Model provides access to Bailian(百炼) -- Alibaba Cloud's large language model service， which supports Qwen series, Deepseek series models.

DashScopeChatModel supports:
- Multi-turn conversations
- Function calling / Tool use
- Streaming responses
- Structured output

#### DashScopeImageModel

Image generation capabilities powered by DashScope, supporting text-to-image generation with various styles and parameters.

#### DashScopeEmbeddingModel

Text embedding model for converting text into vector representations, essential for RAG (Retrieval Augmented Generation) applications and semantic search.

#### DashScopeAudioSpeechModel

Text-to-speech synthesis model that converts text into natural-sounding audio with support for multiple voices and languages.

#### DashScopeAudioTranscriptionModel

Speech-to-text transcription model that converts audio into text with high accuracy.

### MCP (Model Context Protocol)

MCP provides a standardized protocol for managing and routing AI model contexts. This extension includes:

- **MCP Common**: Core abstractions and utilities for Model Context Protocol
- **MCP Registry**: Service registry for discovering and managing MCP services
- **MCP Router**: Intelligent routing capabilities for distributing requests across multiple model contexts

**Available Starters:**
- `spring-ai-alibaba-starter-mcp-registry`
- `spring-ai-alibaba-starter-mcp-router`

### ToolCallback

Extensive collection of pre-built tool integrations that enable AI models to interact with external services and APIs. The framework includes 40+ ready-to-use tools:

**Search & Information:**
- Baidu Search, Brave Search, DuckDuckGo, Metaso Search, Tavily Search, SerpAPI
- Wikipedia, Google Scholar, OpenAlex
- Aliyun AI Search

**Translation Services:**
- Alibaba Translate, Baidu Translate, Google Translate, Microsoft Translate, Youdao Translate

**Map & Location:**
- Amap (高德地图), Baidu Map, Tencent Map, OpenTripMap, TripAdvisor

**News & Media:**
- Sina News, Toutiao News

**Collaboration Tools:**
- DingTalk, Lark Suite (飞书)
- GitHub Toolkit, GitLab
- Yuque (语雀), Notion

**Web Scraping:**
- Firecrawl, Jina Crawler

**Data & Storage:**
- Memcached, Minio
- MongoDB, MySQL, Elasticsearch, SQLite

**Academic & Research:**
- Arxiv, Google Scholar, OpenAlex, Semantic Scholar

**Finance & Data:**
- Tushare (financial data)
- World Bank Data

**Utilities:**
- Time, Weather, Kuaidi100 (logistics tracking)
- JSON Processor, Regex
- Sensitive Filter

**Trend Analysis:**
- Google Trends

**Specialized:**
- Ollama Search Model
- Bilibili (video platform)

Each tool comes with auto-configuration support and can be easily enabled via properties configuration.

### VectorStore

Vector database integrations for building RAG applications and semantic search capabilities:

- **AnalyticDB Store**: Alibaba Cloud AnalyticDB vector storage
- **OceanBase Store**: OceanBase distributed database with vector support
- **OpenSearch Store**: Alibaba Cloud OpenSearch vector search
- **TableStore Store**: Alibaba Cloud TableStore for vector data
- **Tair Store**: Alibaba Cloud Tair (Redis-compatible) vector storage

All vector stores provide consistent APIs for:
- Embedding storage and retrieval
- Similarity search
- Metadata filtering
- Batch operations

### ChatMemory

Multiple storage backends for managing conversation history and long-term memory:

**Short-term Memory:**
- **Redis**: High-performance in-memory storage
- **Memcached**: Distributed memory caching
- **JDBC**: Relational database storage
- **MongoDB**: Document-based storage
- **Elasticsearch**: Full-text search enabled memory
- **TableStore**: Alibaba Cloud TableStore

**Long-term Memory:**
- **Mem0**: Advanced long-term memory with intelligent summarization and retrieval

Available starters:
- `spring-ai-alibaba-starter-memory` (short-term memory)
- `spring-ai-alibaba-starter-memory-long` (long-term memory)
- Individual storage backend starters (e.g., `spring-ai-alibaba-starter-memory-redis`)

### RAG

Popular RAG architecture and a variety of reusable components:

- **Hybrid Search**: Hybrid retriever using BM25 and KNN search with Reciprocal Rank Fusion (RRF). Now Support Elasticsearch.
- **[HyDE Search](https://arxiv.org/abs/2212.10496)**: Hypothetical Document Embeddings RAG, using hypothetical document embeddings to improve retrieval recall and accuracy

Available starters:
```xml
<dependency>
    <groupId>com.alibaba.cloud.ai</groupId>
    <artifactId>spring-ai-alibaba-starter-rag</artifactId>
</dependency>
```

### Prompt

Dynamic prompt management and versioning capabilities:

- **Nacos Prompt**: Store and manage prompts in Nacos configuration center with support for:
  - Dynamic prompt updates without code changes
  - Version control
  - Environment-specific prompts
  - Multi-tenancy support

**Starter:**
```xml
<dependency>
    <groupId>com.alibaba.cloud.ai</groupId>
    <artifactId>spring-ai-alibaba-starter-nacos-prompt</artifactId>
</dependency>
```

### DocumentParser

Comprehensive document parsing capabilities supporting various formats:

- **Apache PDFBox**: PDF document parsing
- **BibTeX**: Bibliography file parsing
- **BSHtml**: HTML content parsing with BeautifulSoup-like capabilities
- **Directory**: Batch directory parsing
- **Markdown**: Markdown document parsing
- **Multi-modality**: Multi-modal document parsing (text, images, etc.)
- **PDF Tables**: Advanced PDF table extraction
- **Tika**: Apache Tika integration for 1000+ file formats
- **YAML**: YAML configuration file parsing

### DocumentReader

Specialized document readers for various data sources and platforms:

**Archive & Storage:**
- Archive files (ZIP, TAR, etc.)
- Tencent COS (Cloud Object Storage)

**Academic & Research:**
- Arxiv papers
- HuggingFace filesystem

**Collaboration Platforms:**
- Notion
- Yuque (语雀)
- LarkSuite (飞书)
- Obsidian
- OneNote
- GitBook

**Code Repositories:**
- GitHub
- GitLab
- GPT Repository Loader format

**Media:**
- Bilibili transcripts
- YouTube transcripts

**Databases:**
- Elasticsearch
- MongoDB
- MySQL
- SQLite

**Communication:**
- Email (IMAP, POP3)
- Mbox format

**AI Data:**
- ChatGPT conversation data
- POI (Point of Interest) data

Each reader can extract and structure content from its respective source, making it ready for RAG pipelines and AI processing.

### Observation

ARMS (Application Real-Time Monitoring Service) integration for comprehensive AI application observability:

- Request/response tracing
- Performance metrics
- Token usage tracking
- Error monitoring
- Cost analysis

**Starter:**
```xml
<dependency>
    <groupId>com.alibaba.cloud.ai</groupId>
    <artifactId>spring-ai-alibaba-starter-arms-observation</artifactId>
</dependency>
```

## Contributing

We welcome contributions! Please see our contributing guidelines and follow the development standards outlined in each module's README.

## License

This project is licensed under the Apache License 2.0 - see the LICENSE file for details.

## Community & Support

- Spring AI Alibaba Agent Framework: [https://github.com/alibaba/spring-ai-alibaba](https://github.com/alibaba/spring-ai-alibaba)
- Documentation: [https://java2ai.com](https://java2ai.com)
- Examples: [Spring AI Alibaba Examples](https://github.com/springaialibaba/spring-ai-alibaba-examples)
