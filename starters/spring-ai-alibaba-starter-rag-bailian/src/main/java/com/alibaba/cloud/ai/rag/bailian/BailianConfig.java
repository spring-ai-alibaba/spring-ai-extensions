/*
 * Copyright 2024-2026 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.cloud.ai.rag.bailian;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.jspecify.annotations.Nullable;

/**
 * Configuration for Alibaba Cloud Bailian Knowledge Base.
 *
 * <p>
 * This class contains all the necessary configuration parameters to connect to and
 * interact with Alibaba Cloud Bailian Knowledge Base service, including connection
 * settings and retrieval parameters.
 *
 * <p>
 * Example usage: <pre>{@code
 * BailianConfig config = BailianConfig.builder()
 *     .accessKeyId(System.getenv("ALIBABA_CLOUD_ACCESS_KEY_ID"))
 *     .accessKeySecret(System.getenv("ALIBABA_CLOUD_ACCESS_KEY_SECRET"))
 *     .workspaceId("llm-xxx")
 *     .indexId("mymxbdxxxx")
 *     // Optional: configure retrieval parameters
 *     .denseSimilarityTopK(50)
 *     .sparseSimilarityTopK(50)
 *     .enableReranking(true)
 *     .rerankConfig(RerankConfig.builder()
 *         .modelName("gte-rerank-hybrid")
 *         .build())
 *     .enableRewrite(true)
 *     .rewriteConfig(RewriteConfig.builder()
 *         .modelName("conv-rewrite-qwen-1.8b")
 *         .build())
 *     .build();
 * }</pre>
 */
public class BailianConfig {

	private static final String DEFAULT_ENDPOINT = "bailian.cn-beijing.aliyuncs.com";

	// Connection configuration
	private final String accessKeyId;

	private final String accessKeySecret;

	private final String workspaceId;

	private final @Nullable String indexId;

	private final String endpoint;

	// Retrieval configuration (knowledge base defaults)
	private final @Nullable Integer denseSimilarityTopK;

	private final @Nullable Integer sparseSimilarityTopK;

	private final @Nullable Boolean enableReranking;

	private final @Nullable RerankConfig rerankConfig;

	private final @Nullable Boolean enableRewrite;

	private final @Nullable RewriteConfig rewriteConfig;

	private final @Nullable List<Map<String, String>> searchFilters;

	private final @Nullable Boolean saveRetrieverHistory;

	private BailianConfig(Builder builder) {
		if (builder.accessKeyId == null || builder.accessKeyId.trim().isEmpty()) {
			throw new IllegalArgumentException("AccessKeyId cannot be null or empty");
		}
		if (builder.accessKeySecret == null || builder.accessKeySecret.trim().isEmpty()) {
			throw new IllegalArgumentException("AccessKeySecret cannot be null or empty");
		}
		if (builder.workspaceId == null || builder.workspaceId.trim().isEmpty()) {
			throw new IllegalArgumentException("WorkspaceId cannot be null or empty");
		}

		// Connection configuration
		this.accessKeyId = Objects.requireNonNull(builder.accessKeyId, "AccessKeyId cannot be null or empty");
		this.accessKeySecret = Objects.requireNonNull(builder.accessKeySecret,
				"AccessKeySecret cannot be null or empty");
		this.workspaceId = Objects.requireNonNull(builder.workspaceId, "WorkspaceId cannot be null or empty");
		this.indexId = builder.indexId;
		this.endpoint = builder.endpoint != null ? builder.endpoint : DEFAULT_ENDPOINT;

		// Retrieval configuration
		this.denseSimilarityTopK = builder.denseSimilarityTopK;
		this.sparseSimilarityTopK = builder.sparseSimilarityTopK;
		this.enableReranking = builder.enableReranking;
		this.rerankConfig = builder.rerankConfig;
		this.enableRewrite = builder.enableRewrite;
		this.rewriteConfig = builder.rewriteConfig;
		this.searchFilters = builder.searchFilters != null ? new ArrayList<>(builder.searchFilters) : null;
		this.saveRetrieverHistory = builder.saveRetrieverHistory;

		// Validate denseSimilarityTopK + sparseSimilarityTopK <= 200
		if (denseSimilarityTopK != null && sparseSimilarityTopK != null) {
			if (denseSimilarityTopK + sparseSimilarityTopK > 200) {
				throw new IllegalArgumentException("denseSimilarityTopK + sparseSimilarityTopK must be <= 200");
			}
		}
	}

	/**
	 * Creates a new builder for BailianConfig.
	 * @return a new Builder instance
	 */
	public static Builder builder() {
		return new Builder();
	}

	/**
	 * Gets the Alibaba Cloud Access Key ID.
	 * @return the access key ID
	 */
	public String getAccessKeyId() {
		return accessKeyId;
	}

	/**
	 * Gets the Alibaba Cloud Access Key Secret.
	 * @return the access key secret
	 */
	public String getAccessKeySecret() {
		return accessKeySecret;
	}

	/**
	 * Gets the Bailian workspace ID.
	 * @return the workspace ID
	 */
	public String getWorkspaceId() {
		return workspaceId;
	}

	/**
	 * Gets the knowledge base index ID.
	 * @return the index ID, or null if not set
	 */
	public @Nullable String getIndexId() {
		return indexId;
	}

	/**
	 * Gets the Bailian API endpoint.
	 * @return the API endpoint
	 */
	public String getEndpoint() {
		return endpoint;
	}

	/**
	 * Gets the dense similarity top K value.
	 * @return the dense similarity top K (0-100), or null if not set
	 */
	public @Nullable Integer getDenseSimilarityTopK() {
		return denseSimilarityTopK;
	}

	/**
	 * Gets the sparse similarity top K value.
	 * @return the sparse similarity top K (0-100), or null if not set
	 */
	public @Nullable Integer getSparseSimilarityTopK() {
		return sparseSimilarityTopK;
	}

	/**
	 * Checks if reranking is enabled.
	 * @return true if reranking is enabled, null if not configured
	 */
	public @Nullable Boolean getEnableReranking() {
		return enableReranking;
	}

	/**
	 * Gets the rerank configuration.
	 * @return the rerank config, or null if not set
	 */
	public @Nullable RerankConfig getRerankConfig() {
		return rerankConfig;
	}

	/**
	 * Checks if query rewrite is enabled.
	 * @return true if rewrite is enabled, null if not configured
	 */
	public @Nullable Boolean getEnableRewrite() {
		return enableRewrite;
	}

	/**
	 * Gets the rewrite configuration.
	 * @return the rewrite config, or null if not set
	 */
	public @Nullable RewriteConfig getRewriteConfig() {
		return rewriteConfig;
	}

	/**
	 * Gets the search filters.
	 * @return the search filters list, or null if not set
	 */
	public @Nullable List<Map<String, String>> getSearchFilters() {
		return searchFilters;
	}

	/**
	 * Checks if retriever history should be saved.
	 * @return true if history should be saved, null if not configured
	 */
	public @Nullable Boolean getSaveRetrieverHistory() {
		return saveRetrieverHistory;
	}

	/**
	 * Builder for BailianConfig.
	 */
	public static class Builder {

		// Connection configuration
		private @Nullable String accessKeyId;

		private @Nullable String accessKeySecret;

		private @Nullable String workspaceId;

		private @Nullable String indexId;

		private @Nullable String endpoint;

		// Retrieval configuration
		private @Nullable Integer denseSimilarityTopK;

		private @Nullable Integer sparseSimilarityTopK;

		private @Nullable Boolean enableReranking;

		private @Nullable RerankConfig rerankConfig;

		private @Nullable Boolean enableRewrite;

		private @Nullable RewriteConfig rewriteConfig;

		private @Nullable List<Map<String, String>> searchFilters;

		private @Nullable Boolean saveRetrieverHistory;

		private Builder() {
		}

		/**
		 * Sets the Alibaba Cloud Access Key ID.
		 * @param accessKeyId the access key ID
		 * @return this builder for method chaining
		 */
		public Builder accessKeyId(@Nullable String accessKeyId) {
			this.accessKeyId = accessKeyId;
			return this;
		}

		/**
		 * Sets the Alibaba Cloud Access Key Secret.
		 * @param accessKeySecret the access key secret
		 * @return this builder for method chaining
		 */
		public Builder accessKeySecret(@Nullable String accessKeySecret) {
			this.accessKeySecret = accessKeySecret;
			return this;
		}

		/**
		 * Sets the Bailian workspace ID.
		 * @param workspaceId the workspace ID
		 * @return this builder for method chaining
		 */
		public Builder workspaceId(@Nullable String workspaceId) {
			this.workspaceId = workspaceId;
			return this;
		}

		/**
		 * Sets the knowledge base index ID.
		 *
		 * <p>
		 * This is optional for configuration, but required when calling retrieve().
		 * @param indexId the index ID
		 * @return this builder for method chaining
		 */
		public Builder indexId(@Nullable String indexId) {
			this.indexId = indexId;
			return this;
		}

		/**
		 * Sets the Bailian API endpoint.
		 *
		 * <p>
		 * If not set, defaults to "bailian.cn-beijing.aliyuncs.com".
		 *
		 * <p>
		 * Available endpoints:
		 * <ul>
		 * <li>Public Cloud: bailian.cn-beijing.aliyuncs.com
		 * <li>Finance Cloud: bailian.cn-shanghai-finance-1.aliyuncs.com
		 * <li>VPC (Beijing): bailian-vpc.cn-beijing.aliyuncs.com
		 * <li>VPC (Shanghai Finance): bailian-vpc.cn-shanghai-finance-1.aliyuncs.com
		 * </ul>
		 * @param endpoint the API endpoint
		 * @return this builder for method chaining
		 */
		public Builder endpoint(@Nullable String endpoint) {
			this.endpoint = endpoint;
			return this;
		}

		/**
		 * Sets the dense similarity top K.
		 *
		 * <p>
		 * Vector retrieval top K. Range: [0-100], default: 100. Note: denseSimilarityTopK
		 * + sparseSimilarityTopK must be smaller than 200.
		 * @param denseSimilarityTopK the top K value
		 * @return this builder
		 */
		public Builder denseSimilarityTopK(@Nullable Integer denseSimilarityTopK) {
			if (denseSimilarityTopK != null && (denseSimilarityTopK < 0 || denseSimilarityTopK > 100)) {
				throw new IllegalArgumentException("denseSimilarityTopK must be between 0 and 100");
			}
			this.denseSimilarityTopK = denseSimilarityTopK;
			return this;
		}

		/**
		 * Sets the sparse similarity top K.
		 *
		 * <p>
		 * Keyword retrieval top K. Range: [0-100], default: 100. Note:
		 * denseSimilarityTopK + sparseSimilarityTopK must be smaller than 200.
		 * @param sparseSimilarityTopK the top K value
		 * @return this builder
		 */
		public Builder sparseSimilarityTopK(@Nullable Integer sparseSimilarityTopK) {
			if (sparseSimilarityTopK != null && (sparseSimilarityTopK < 0 || sparseSimilarityTopK > 100)) {
				throw new IllegalArgumentException("sparseSimilarityTopK must be between 0 and 100");
			}
			this.sparseSimilarityTopK = sparseSimilarityTopK;
			return this;
		}

		/**
		 * Sets whether to enable reranking.
		 *
		 * <p>
		 * Default: true (enabled)
		 * @param enableReranking true to enable reranking
		 * @return this builder
		 */
		public Builder enableReranking(@Nullable Boolean enableReranking) {
			this.enableReranking = enableReranking;
			return this;
		}

		/**
		 * Sets the rerank configuration.
		 * @param rerankConfig the rerank config
		 * @return this builder
		 */
		public Builder rerankConfig(@Nullable RerankConfig rerankConfig) {
			this.rerankConfig = rerankConfig;
			return this;
		}

		/**
		 * Sets whether to enable multi-turn conversation rewrite.
		 *
		 * <p>
		 * Default: false (disabled)
		 * @param enableRewrite true to enable rewrite
		 * @return this builder
		 */
		public Builder enableRewrite(@Nullable Boolean enableRewrite) {
			this.enableRewrite = enableRewrite;
			return this;
		}

		/**
		 * Sets the rewrite configuration.
		 * @param rewriteConfig the rewrite config
		 * @return this builder
		 */
		public Builder rewriteConfig(@Nullable RewriteConfig rewriteConfig) {
			this.rewriteConfig = rewriteConfig;
			return this;
		}

		/**
		 * Sets search filters for personalized retrieval.
		 *
		 * <p>
		 * Filters can be used to exclude irrelevant information based on tags and other
		 * metadata.
		 * @param searchFilters the search filters
		 * @return this builder
		 */
		public Builder searchFilters(@Nullable List<Map<String, String>> searchFilters) {
			this.searchFilters = searchFilters != null ? new ArrayList<>(searchFilters) : null;
			return this;
		}

		/**
		 * Sets whether to save retriever history.
		 *
		 * <p>
		 * Default: false
		 * @param saveRetrieverHistory true to save history
		 * @return this builder
		 */
		public Builder saveRetrieverHistory(@Nullable Boolean saveRetrieverHistory) {
			this.saveRetrieverHistory = saveRetrieverHistory;
			return this;
		}

		/**
		 * Builds a new BailianConfig instance.
		 * @return a new BailianConfig instance
		 * @throws IllegalArgumentException if required parameters are missing
		 */
		public BailianConfig build() {
			return new BailianConfig(this);
		}

	}

}
