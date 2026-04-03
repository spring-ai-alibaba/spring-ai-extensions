/*
 * Copyright 2024-2026 the original author or authors.
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
package com.alibaba.cloud.ai.memory.mem0.core;

import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yingzi
 * @since 2025/9/14
 */

public class Mem0Server {

	private @Nullable String version;

	private @Nullable VectorStore vectorStore;

	private @Nullable GraphStore graphStore;

	private @Nullable Llm llm;

	private @Nullable Embedder embedder;

	private @Nullable String historyDbPath;

	private @Nullable Project project;

	private @Nullable String customFactExtractionPrompt;

	private @Nullable String customUpdateMemoryPrompt;

	// 私有构造函数，防止直接实例化
	private Mem0Server() {
	}

	private Mem0Server(Mem0Server server) {
		this.version = server.version;
		this.vectorStore = server.vectorStore;
		this.graphStore = server.graphStore;
		this.llm = server.llm;
		this.embedder = server.embedder;
		this.historyDbPath = server.historyDbPath;
		this.project = server.project;
		this.customFactExtractionPrompt = server.customFactExtractionPrompt;
		this.customUpdateMemoryPrompt = server.customUpdateMemoryPrompt;
	}

	public static Builder builder() {
		return new Builder();
	}

	public @Nullable String getVersion() {
		return version;
	}

	public void setVersion(@Nullable String version) {
		this.version = version;
	}

	public @Nullable VectorStore getVectorStore() {
		return vectorStore;
	}

	public void setVectorStore(@Nullable VectorStore vectorStore) {
		this.vectorStore = vectorStore;
	}

	public @Nullable GraphStore getGraphStore() {
		return graphStore;
	}

	public void setGraphStore(@Nullable GraphStore graphStore) {
		this.graphStore = graphStore;
	}

	public @Nullable Llm getLlm() {
		return llm;
	}

	public void setLlm(@Nullable Llm llm) {
		this.llm = llm;
	}

	public @Nullable Embedder getEmbedder() {
		return embedder;
	}

	public void setEmbedder(@Nullable Embedder embedder) {
		this.embedder = embedder;
	}

	public @Nullable String getHistoryDbPath() {
		return historyDbPath;
	}

	public void setHistoryDbPath(@Nullable String historyDbPath) {
		this.historyDbPath = historyDbPath;
	}

	public @Nullable Project getProject() {
		return project;
	}

	public void setProject(@Nullable Project project) {
		this.project = project;
	}

	public @Nullable String getCustomFactExtractionPrompt() {
		return customFactExtractionPrompt;
	}

	public void setCustomFactExtractionPrompt(@Nullable String customFactExtractionPrompt) {
		this.customFactExtractionPrompt = customFactExtractionPrompt;
	}

	public @Nullable String getCustomUpdateMemoryPrompt() {
		return customUpdateMemoryPrompt;
	}

	public void setCustomUpdateMemoryPrompt(@Nullable String customUpdateMemoryPrompt) {
		this.customUpdateMemoryPrompt = customUpdateMemoryPrompt;
	}

	public static class Builder {

		private final Mem0Server server = new Mem0Server();

		private Builder() {
		}

		public Builder version(@Nullable String version) {
			server.version = version;
			return this;
		}

		public Builder vectorStore(@Nullable VectorStore vectorStore) {
			server.vectorStore = vectorStore;
			return this;
		}

		public Builder graphStore(@Nullable GraphStore graphStore) {
			server.graphStore = graphStore;
			return this;
		}

		public Builder llm(@Nullable Llm llm) {
			server.llm = llm;
			return this;
		}

		public Builder embedder(@Nullable Embedder embedder) {
			server.embedder = embedder;
			return this;
		}

		public Builder historyDbPath(@Nullable String historyDbPath) {
			server.historyDbPath = historyDbPath;
			return this;
		}

		public Builder project(@Nullable Project project) {
			server.project = project;
			return this;
		}

		public Builder customFactExtractionPrompt(@Nullable String customFactExtractionPrompt) {
			server.customFactExtractionPrompt = customFactExtractionPrompt;
			return this;
		}

		public Builder customUpdateMemoryPrompt(@Nullable String customUpdateMemoryPrompt) {
			server.customUpdateMemoryPrompt = customUpdateMemoryPrompt;
			return this;
		}

		public Mem0Server build() {
			return new Mem0Server(server);
		}

	}

	public static class Project {

		private @Nullable String customCategories;

		private @Nullable String customInstructions;

		// 私有构造函数，防止直接实例化
		private Project() {
		}

		// 私有构造函数，用于复制现有实例
		private Project(Project project) {
			this.customCategories = project.customCategories;
			this.customInstructions = project.customInstructions;
		}

		public static Builder builder() {
			return new Builder();
		}

		public @Nullable String getCustomCategories() {
			return customCategories;
		}

		public void setCustomCategories(@Nullable String customCategories) {
			this.customCategories = customCategories;
		}

		public @Nullable String getCustomInstructions() {
			return customInstructions;
		}

		public void setCustomInstructions(@Nullable String customInstructions) {
			this.customInstructions = customInstructions;
		}

		public static class Builder {

			private final Project project = new Project();

			private Builder() {
			}

			public Builder customCategories(@Nullable String customCategories) {
				project.customCategories = customCategories;
				return this;
			}

			public Builder customInstructions(@Nullable String customInstructions) {
				project.customInstructions = customInstructions;
				return this;
			}

			public Project build() {
				return new Project(project);
			}

		}

	}

	public static class VectorStore {

		private @Nullable String provider;

		/**
		 * The following vector databases are supported. For specific configurations,
		 * please refer to the official documentation or Mem0 source code. "qdrant":
		 * "QdrantConfig", "chroma": "ChromaDbConfig", "pgvector": "PGVectorConfig",
		 * "pinecone": "PineconeConfig", "mongodb": "MongoDBConfig", "milvus":
		 * "MilvusDBConfig", "baidu": "BaiduDBConfig", "upstash_vector":
		 * "UpstashVectorConfig", "azure_ai_search": "AzureAISearchConfig", "redis":
		 * "RedisDBConfig", "elasticsearch": "ElasticsearchConfig",
		 * "vertex_ai_vector_search": "GoogleMatchingEngineConfig", "opensearch":
		 * "OpenSearchConfig", "supabase": "SupabaseConfig", "weaviate": "WeaviateConfig",
		 * "faiss": "FAISSConfig", "langchain": "LangchainConfig",
		 */
		private @Nullable Map<String, String> config;

		// 私有构造函数，防止直接实例化
		private VectorStore() {
		}

		// 私有构造函数，用于复制现有实例
		private VectorStore(VectorStore vectorStore) {
			this.provider = vectorStore.provider;
			this.config = vectorStore.config;
		}

		public static Builder builder() {
			return new Builder();
		}

		public @Nullable String getProvider() {
			return provider;
		}

		public void setProvider(@Nullable String provider) {
			this.provider = provider;
		}

		public Map<String, String> getConfig() {
			Map<String, String> result = new HashMap<>();
			if (config == null) {
				return result;
			}
			for (Map.Entry<String, String> entry : config.entrySet()) {
				String key = entry.getKey().replace("-", "_");
				result.put(key, entry.getValue());
			}
			return result;
		}

		public void setConfig(@Nullable Map<String, String> config) {
			this.config = config;
		}

		public static class Builder {

			private final VectorStore vectorStore = new VectorStore();

			private Builder() {
			}

			public Builder provider(@Nullable String provider) {
				vectorStore.provider = provider;
				return this;
			}

			public Builder config(@Nullable Map<String, String> config) {
				vectorStore.config = config;
				return this;
			}

			public VectorStore build() {
				return new VectorStore(vectorStore);
			}

		}

	}

	public static class GraphStore {

		private @Nullable String provider;

		private @Nullable GraphStoreConfig config;

		private @Nullable Llm llm;

		/*
		 * customPrompt: classpath:/prompts/system-message.st
		 */
		private @Nullable String customPrompt;

		// 私有构造函数，防止直接实例化
		private GraphStore() {
		}

		// 私有构造函数，用于复制现有实例
		private GraphStore(GraphStore graphStore) {
			this.provider = graphStore.provider;
			this.config = graphStore.config;
			this.llm = graphStore.llm;
			this.customPrompt = graphStore.customPrompt;
		}

		public static Builder builder() {
			return new Builder();
		}

		public @Nullable String getProvider() {
			return provider;
		}

		public void setProvider(@Nullable String provider) {
			this.provider = provider;
		}

		public @Nullable GraphStoreConfig getConfig() {
			return config;
		}

		public void setConfig(@Nullable GraphStoreConfig config) {
			this.config = config;
		}

		public @Nullable Llm getLlm() {
			return llm;
		}

		public void setLlm(@Nullable Llm llm) {
			this.llm = llm;
		}

		public @Nullable String getCustomPrompt() {
			return customPrompt;
		}

		public void setCustomPrompt(@Nullable String customPrompt) {
			this.customPrompt = customPrompt;
		}

		public static class Builder {

			private final GraphStore graphStore = new GraphStore();

			private Builder() {
			}

			public Builder provider(@Nullable String provider) {
				graphStore.provider = provider;
				return this;
			}

			public Builder config(@Nullable GraphStoreConfig config) {
				graphStore.config = config;
				return this;
			}

			public Builder llm(@Nullable Llm llm) {
				graphStore.llm = llm;
				return this;
			}

			public Builder customPrompt(@Nullable String customPrompt) {
				graphStore.customPrompt = customPrompt;
				return this;
			}

			public GraphStore build() {
				return new GraphStore(graphStore);
			}

		}

		public static class GraphStoreConfig {

			private @Nullable String url;

			private @Nullable String username;

			private @Nullable String password;

			// Neo4j supports the following two items:
			private @Nullable String database;

			private @Nullable Boolean baseLabel;

			// 私有构造函数，防止直接实例化
			private GraphStoreConfig() {
			}

			// 私有构造函数，用于复制现有实例
			private GraphStoreConfig(GraphStoreConfig config) {
				this.url = config.url;
				this.username = config.username;
				this.password = config.password;
				this.database = config.database;
				this.baseLabel = config.baseLabel;
			}

			public static Builder builder() {
				return new Builder();
			}

			public @Nullable String getUrl() {
				return url;
			}

			public void setUrl(@Nullable String url) {
				this.url = url;
			}

			public @Nullable String getUsername() {
				return username;
			}

			public void setUsername(@Nullable String username) {
				this.username = username;
			}

			public @Nullable String getPassword() {
				return password;
			}

			public void setPassword(@Nullable String password) {
				this.password = password;
			}

			public @Nullable String getDatabase() {
				return database;
			}

			public void setDatabase(@Nullable String database) {
				this.database = database;
			}

			public @Nullable Boolean getBaseLabel() {
				return baseLabel;
			}

			public void setBaseLabel(@Nullable Boolean baseLabel) {
				this.baseLabel = baseLabel;
			}

			public static class Builder {

				private final GraphStoreConfig config = new GraphStoreConfig();

				private Builder() {
				}

				public Builder url(@Nullable String url) {
					config.url = url;
					return this;
				}

				public Builder username(@Nullable String username) {
					config.username = username;
					return this;
				}

				public Builder password(@Nullable String password) {
					config.password = password;
					return this;
				}

				public Builder database(@Nullable String database) {
					config.database = database;
					return this;
				}

				public Builder baseLabel(@Nullable Boolean baseLabel) {
					config.baseLabel = baseLabel;
					return this;
				}

				public GraphStoreConfig build() {
					return new GraphStoreConfig(config);
				}

			}

		}

	}

	public static class Llm {

		private @Nullable String provider;

		private @Nullable LlmConfig config;

		// 私有构造函数，防止直接实例化
		private Llm() {
		}

		// 私有构造函数，用于复制现有实例
		private Llm(Llm llm) {
			this.provider = llm.provider;
			this.config = llm.config;
		}

		public static Builder builder() {
			return new Builder();
		}

		public @Nullable String getProvider() {
			return provider;
		}

		public void setProvider(@Nullable String provider) {
			this.provider = provider;
		}

		public @Nullable LlmConfig getConfig() {
			return config;
		}

		public void setConfig(@Nullable LlmConfig config) {
			this.config = config;
		}

		public static class Builder {

			private Llm llm = new Llm();

			private Builder() {
			}

			public Builder provider(@Nullable String provider) {
				llm.provider = provider;
				return this;
			}

			public Builder config(@Nullable LlmConfig config) {
				llm.config = config;
				return this;
			}

			public Llm build() {
				return new Llm(llm);
			}

		}

		public static class LlmConfig {

			private @Nullable String apiKey;

			private double temperature;

			private @Nullable String model;

			private @Nullable String openaiBaseUrl;

			// 私有构造函数，防止直接实例化
			private LlmConfig() {
			}

			// 私有构造函数，用于复制现有实例
			private LlmConfig(LlmConfig config) {
				this.apiKey = config.apiKey;
				this.temperature = config.temperature;
				this.model = config.model;
				this.openaiBaseUrl = config.openaiBaseUrl;
			}

			public static Builder builder() {
				return new Builder();
			}

			public @Nullable String getApiKey() {
				return apiKey;
			}

			public void setApiKey(@Nullable String apiKey) {
				this.apiKey = apiKey;
			}

			public double getTemperature() {
				return temperature;
			}

			public void setTemperature(double temperature) {
				this.temperature = temperature;
			}

			public @Nullable String getModel() {
				return model;
			}

			public void setModel(@Nullable String model) {
				this.model = model;
			}

			public @Nullable String getOpenaiBaseUrl() {
				return openaiBaseUrl;
			}

			public void setOpenaiBaseUrl(@Nullable String openaiBaseUrl) {
				this.openaiBaseUrl = openaiBaseUrl;
			}

			public static class Builder {

				private final LlmConfig config = new LlmConfig();

				private Builder() {
				}

				public Builder apiKey(@Nullable String apiKey) {
					config.apiKey = apiKey;
					return this;
				}

				public Builder temperature(double temperature) {
					config.temperature = temperature;
					return this;
				}

				public Builder model(@Nullable String model) {
					config.model = model;
					return this;
				}

				public Builder openaiBaseUrl(@Nullable String openaiBaseUrl) {
					config.openaiBaseUrl = openaiBaseUrl;
					return this;
				}

				public LlmConfig build() {
					return new LlmConfig(config);
				}

			}

		}

	}

	public static class Embedder {

		private @Nullable String provider;

		private @Nullable EmbedderConfig config;

		// 私有构造函数，防止直接实例化
		private Embedder() {
		}

		// 私有构造函数，用于复制现有实例
		private Embedder(Embedder embedder) {
			this.provider = embedder.provider;
			this.config = embedder.config;
		}

		public static Builder builder() {
			return new Builder();
		}

		public @Nullable String getProvider() {
			return provider;
		}

		public void setProvider(@Nullable String provider) {
			this.provider = provider;
		}

		public @Nullable EmbedderConfig getConfig() {
			return config;
		}

		public void setConfig(@Nullable EmbedderConfig config) {
			this.config = config;
		}

		public static class Builder {

			private final Embedder embedder = new Embedder();

			private Builder() {
			}

			public Builder provider(@Nullable String provider) {
				embedder.provider = provider;
				return this;
			}

			public Builder config(@Nullable EmbedderConfig config) {
				embedder.config = config;
				return this;
			}

			public Embedder build() {
				return new Embedder(embedder);
			}

		}

		public static class EmbedderConfig {

			private @Nullable String apiKey;

			private @Nullable String model;

			private @Nullable String openaiBaseUrl;

			// 私有构造函数，防止直接实例化
			private EmbedderConfig() {
			}

			// 私有构造函数，用于复制现有实例
			private EmbedderConfig(EmbedderConfig config) {
				this.apiKey = config.apiKey;
				this.model = config.model;
				this.openaiBaseUrl = config.openaiBaseUrl;
			}

			public static Builder builder() {
				return new Builder();
			}

			public @Nullable String getApiKey() {
				return apiKey;
			}

			public void setApiKey(@Nullable String apiKey) {
				this.apiKey = apiKey;
			}

			public @Nullable String getModel() {
				return model;
			}

			public void setModel(@Nullable String model) {
				this.model = model;
			}

			public @Nullable String getOpenaiBaseUrl() {
				return openaiBaseUrl;
			}

			public void setOpenaiBaseUrl(@Nullable String openaiBaseUrl) {
				this.openaiBaseUrl = openaiBaseUrl;
			}

			public static class Builder {

				private final EmbedderConfig config = new EmbedderConfig();

				private Builder() {
				}

				public Builder apiKey(@Nullable String apiKey) {
					config.apiKey = apiKey;
					return this;
				}

				public Builder model(@Nullable String model) {
					config.model = model;
					return this;
				}

				public Builder openaiBaseUrl(@Nullable String openaiBaseUrl) {
					config.openaiBaseUrl = openaiBaseUrl;
					return this;
				}

				public EmbedderConfig build() {
					return new EmbedderConfig(config);
				}

			}

		}

	}

}
