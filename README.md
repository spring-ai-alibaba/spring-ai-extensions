# Alibaba Extensions for Spring AI

This project builds upon Spring AI, providing extended implementations of core concepts such as `ChatModel`, `ImageModel`, `AudioModel`, `MCP`, `DocumentParser`, `ChatMemory`, `ToolCallback`, `VectorStore`, etc. It helps developers quickly integrate with Alibaba Cloud Bailian model services, vector database services, chat memory components, tool calling, and other features.

Based on these components, developers can use Spring AI [ChatClient](https://java2ai.com/docs/1.0.0.2/tutorials/basics/chat-client/), or [Spring AI Alibaba Agent Framework](https://github.com/alibaba/spring-ai-alibaba) to quickly build their own AI agent applications. Please choose according to your specific use case.

**English** | [📖 中文版](README-zh.md)

## Get Started

### Prerequsites

1. Requires JDK 17+.
2. If there are any `spring-ai` dependency issue, please lean how to configure the `spring-milestones` Maven repository on [FAQ page](https://java2ai.com/docs/1.0.0.2/faq).
3. Extensions `2.1.0.0` uses Spring AI `2.1.0` and Spring Boot `4.2.0`. To use a local build with the example below, run `mvn install -DskipTests`.

### Use `ChatClient` to Develop a Chatbot

#### Add Dependencies
To quickly get started with Spring AI Alibaba, add 'spring-ai-alibaba-starter-dashscope' dependency to your java project.

```xml
<dependencyManagement>
  <dependencies>
    <dependency>
      <groupId>com.alibaba.cloud.ai</groupId>
      <artifactId>spring-ai-alibaba-extensions-bom</artifactId>
      <version>2.1.0.0</version>
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

To use DashScope Java SDK based chat model implementation, use the SDK starter instead:

```xml
<dependency>
  <groupId>com.alibaba.cloud.ai</groupId>
  <artifactId>spring-ai-alibaba-starter-dashscope-sdk</artifactId>
</dependency>
```

And set `spring.ai.model.chat=dashscope-sdk`.

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
                .topP(0.7)
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

### DashScope chat connections

The DashScope starter manages a bounded pool for blocking and streaming chat. Configure the following properties under `spring.ai.dashscope.chat.http`.

| Property | Default |
| --- | --- |
| `max-connections`, `pending-acquire-max-count` | 64 each |
| `acquire-timeout`, `connect-timeout` | 2 s, 10 s |
| `max-idle-time`, `max-life-time`, `eviction-interval` | 30 s, 10 min, 15 s |
| `first-chunk-timeout`, `stream-read-timeout`, `request-timeout` | 60 s, 60 s, 5 min |

The streaming request timeout includes time in the connection queue and covers the full response. The blocking native client's read timeout is `spring.ai.dashscope.read-timeout` in seconds. The default chat client sends one POST per attempt. Programmatic native chat clients can explicitly supply a `RetryTemplate` for blocking calls.

For the DashScope OpenAI-compatible **Chat Completions** endpoint, add `org.springframework.ai:spring-ai-openai` and opt into the managed transport:

```java
var transport = new DashScopeHttpTransport(DashScopeHttpTransport.Settings.defaults());
var options = OpenAiChatOptions.builder().apiKey(apiKey)
        .baseUrl("https://dashscope.aliyuncs.com/compatible-mode/v1")
        .model("qwen-plus").build();
var model = DashScopeCompatibleChatModel.builder(options, transport).build();
```

Reuse the model and transport across calls and close the transport at application shutdown. This transport cancels the HTTP subscription and treats a missing `[DONE]` as an incomplete stream. A caller can supply its own `WebClient.Builder` for filters and observations.

### DashScope model routing and reasoning

The HTTP and SDK chat starters select the native generation endpoint for known models.
For example, `qwen4-max`, `qwen3.8-flash`, and the Bailian-hosted `kimi-k3.1-pro` use the
multimodal endpoint for text-only requests as well. `glm-5.3` uses the text endpoint.
Set `multi-model` explicitly to override the route for a custom deployment.

```yaml
spring:
  ai:
    dashscope:
      chat:
        model: qwen4-max
        enable-thinking: true
        reasoning-effort: low
        thinking-display: summarized
        preserve-thinking: true
```

Use the same chat options under `spring.ai.dashscope.sdk.chat` with the SDK starter.
For a workspace-specific endpoint, use `spring.ai.dashscope.base-url` with the HTTP
starter or `spring.ai.dashscope.sdk.chat.base-url` with the SDK starter. Include `/api/v1`
in the SDK base URL. For GLM, use `clear-thinking: false` to retain prior reasoning.
Qwen4-Max accepts either `reasoning-effort` or `thinking-budget` in a request.
Kimi-K3.1-Pro accepts only its default sampling values and does not accept
`thinking-budget`.

When `thinking-display: summarized` is enabled, keep the assistant message and its
parts in conversation history. The adapter replays the encrypted reasoning payload
with the next turn. For the provider-direct `kimi/kimi-k3` model, follow Bailian's
[OpenAI-compatible API instructions](https://help.aliyun.com/zh/model-studio/kimi-api-by-moonshot-ai).

### Bailian Decision Model

Add `org.springframework.ai:spring-ai-starter-model-systemone` from Spring AI 2.1.0 to use the Bailian Decision Model. Inject `DecisionModel` and submit yes/no, choice, or score questions with `DecisionPrompt`. This configuration uses a China (Beijing) workspace. For other regions, use the hostname for your workspace. [Bailian Decision Model API](https://help.aliyun.com/en/model-studio/decision-model-api)

```yaml
spring:
  ai:
    model:
      decision: systemone
    systemone:
      api-key: ${DASHSCOPE_API_KEY}
      base-url: https://${BAILIAN_WORKSPACE_ID}.cn-beijing.maas.aliyuncs.com/compatible-mode
      decision:
        model: decision-model-preview
        provider: dashscope
```

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

#### DashScopeSdkChatModel

DashScope SDK based chat model implementation. Configure with `spring-ai-alibaba-starter-dashscope-sdk` and `spring.ai.model.chat=dashscope-sdk`.

#### DashScopeSdkImageModel

DashScope SDK based image model implementation. Configure with `spring-ai-alibaba-starter-dashscope-sdk` and `spring.ai.model.image=dashscope-sdk`.

#### DashScopeSdkEmbeddingModel

DashScope SDK based embedding model implementation. Configure with `spring-ai-alibaba-starter-dashscope-sdk` and `spring.ai.model.embedding=dashscope-sdk`.

#### DashScopeSdkAudioSpeechModel

DashScope SDK based text-to-speech model implementation. Configure with `spring-ai-alibaba-starter-dashscope-sdk` and `spring.ai.model.audio.speech=dashscope-sdk`.

#### DashScopeSdkAudioTranscriptionModel

DashScope SDK based audio transcription model implementation. Configure with `spring-ai-alibaba-starter-dashscope-sdk` and `spring.ai.model.audio.transcription=dashscope-sdk`.

#### DashScopeImageModel

Image generation capabilities powered by DashScope, supporting text-to-image generation with various styles and parameters.

#### DashScopeEmbeddingModel

Text embedding model for converting text into vector representations, essential for RAG (Retrieval Augmented Generation) applications and semantic search.

#### DashScopeAudioSpeechModel

Text-to-speech synthesis model that converts text into natural-sounding audio with support for multiple voices and languages.

#### DashScopeAudioTranscriptionModel

Speech-to-text transcription model that converts audio into text with high accuracy.

#### Current DashScope image, video, and audio APIs

- Image generation and editing support `qwen-image-3.0-pro`, `qwen-image-3.0`, `qwen-image-2.1-pro`, and `qwen-image-2.1-turbo`. Pass reference images with `baseImageUrl` or `images`. For synchronous Qwen Image 3.0, use `responseFormat("b64_json")` and read `Image.getB64Json()`.
- Select `wan3.0-video` or a Wan 2.7 model for video generation. Set the frame with `resolution` and `ratio`, and provide reference media through `InputOptions.media`. The adapter polls the submitted task by ID.
- Speech synthesis supports Qwen Audio TTS 3.0/3.1 and CosyVoice 3.5. For CosyVoice 3.5, configure a voice cloned or designed for the selected model. Use `qwen-audio-3.1-asr-flash` for short audio, `qwen-audio-3.1-asr-flash-filetrans` with a public URL for long audio, or `qwen-audio-3.1-asr-flash-streaming` for real-time transcription.

### MCP (Model Context Protocol)

MCP provides a standardized protocol for managing and routing AI model contexts. This extension includes:

- **MCP Common**: Core abstractions and utilities for Model Context Protocol
- **MCP Gateway**: Unified MCP service gateway with multi-protocol and OAuth authentication support
- **MCP Registry**: Service registry for discovering and managing MCP services
- **MCP Router**: Intelligent routing capabilities for distributing requests across multiple model contexts

MCP Java SDK 2.2.0 supports both Modern MCP (2026-07-28) and legacy MCP tool discovery and calls through the Nacos registry, router, and gateway.

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

For new conversations on Spring AI 2.1, use Session Memory. With
`spring-ai-alibaba-starter-model-chat-memory`, set `spring.ai.alibaba.session.enabled=true`
and register `SessionAdvisors.builder(sessionService).build()` on the chat client. Pass
`SessionAdvisors.session(sessionId)` on every request. The starter provides an in-memory
session repository when there is no configured store. Add Spring AI's
`spring-ai-starter-model-session-jdbc` for a persistent JDBC store.

To migrate an existing conversation, call
`LegacyChatMemorySessionImporter.importConversation(repository, conversationId, target)`
with a `CreateSessionRequest` containing an explicit target session ID. The importer copies
the stored messages in order and can be retried with the same source and target. The
session can then continue with the new advisors. See the
[Spring AI migration guide](https://docs.spring.io/spring-ai/reference/2.1-SNAPSHOT/upgrading/session-memory.html).

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
- `spring-ai-alibaba-starter-model-chat-memory` (short-term memory)
- `spring-ai-alibaba-starter-model-chat-memory-long` (long-term memory)
- Individual storage backend starters (e.g., `spring-ai-alibaba-starter-model-chat-memory-repository-redis`)

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

#### Bailian workspace RAG API

Configure a workspace and an API key in the DashScope starter to inject `DashScopeRagApi`.

```yaml
spring:
  ai:
    dashscope:
      api-key: ${DASHSCOPE_API_KEY}
      workspace-id: ${BAILIAN_WORKSPACE_ID}
      rag:
        enabled: true
```

Use `DashScopeRagDocumentRetriever.forIndex(api, indexId, 5, List.of())` with an existing knowledge base or `forService(api, agentId, filters)` with a published retrieval service. Pass the retriever to `RetrievalAugmentationAdvisor` or `DashScopeDocumentRetrievalAdvisor`.

Register files with `upload`, pass the file IDs to `submitImport`, and retain the job ID for `importStatus` or `awaitImport`. Use the knowledge base's document IDs with `deleteDocuments` and the data center file ID with `deleteFile`. For hosted question answering, consume the SSE events from `chat` and send the full message history on each turn.

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
