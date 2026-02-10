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
package com.alibaba.cloud.ai.dashscope.rag;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

/**
 * @author nuocheng.lxm
 * @since 2024/8/6 11:04
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DashScopeDocumentRetrieverOptions {

  private @JsonProperty("index_name") String indexName;

  private @JsonProperty("dense_similarity_top_k") int denseSimilarityTopK = 100;

  private @JsonProperty("sparse_similarity_top_k") int sparseSimilarityTopK = 100;

  private @JsonProperty("enable_rewrite") boolean enableRewrite = false;

  private @JsonProperty("model_name") String rewriteModelName = "conv-rewrite-qwen-1.8b";

  private @JsonProperty("enable_reranking") boolean enableReranking = true;

  private @JsonProperty("model_name") String rerankModelName = "gte-rerank-hybrid";

  private @JsonProperty("rerank_min_score") float rerankMinScore = 0.01f;

  private @JsonProperty("rerank_top_n") int rerankTopN = 5;

  private @JsonProperty("search_filters") List<Map<String, Object>> searchFilters;

  public static DashScopeDocumentRetrieverOptions.Builder builder() {
    return new DashScopeDocumentRetrieverOptions.Builder();
  }

  public String getIndexName() {
    return indexName;
  }

  public void setIndexName(String indexName) {
    this.indexName = indexName;
  }

  public int getDenseSimilarityTopK() {
    return denseSimilarityTopK;
  }

  public void setDenseSimilarityTopK(int denseSimilarityTopK) {
    this.denseSimilarityTopK = denseSimilarityTopK;
  }

  public int getSparseSimilarityTopK() {
    return sparseSimilarityTopK;
  }

  public void setSparseSimilarityTopK(int sparseSimilarityTopK) {
    this.sparseSimilarityTopK = sparseSimilarityTopK;
  }

  public boolean isEnableRewrite() {
    return enableRewrite;
  }

  public void setEnableRewrite(boolean enableRewrite) {
    this.enableRewrite = enableRewrite;
  }

  public String getRewriteModelName() {
    return rewriteModelName;
  }

  public void setRewriteModelName(String rewriteModelName) {
    this.rewriteModelName = rewriteModelName;
  }

  public boolean isEnableReranking() {
    return enableReranking;
  }

  public void setEnableReranking(boolean enableReranking) {
    this.enableReranking = enableReranking;
  }

  public String getRerankModelName() {
    return rerankModelName;
  }

  public void setRerankModelName(String rerankModelName) {
    this.rerankModelName = rerankModelName;
  }

  public float getRerankMinScore() {
    return rerankMinScore;
  }

  public void setRerankMinScore(float rerankMinScore) {
    this.rerankMinScore = rerankMinScore;
  }

  public int getRerankTopN() {
    return rerankTopN;
  }

  public void setRerankTopN(int rerankTopN) {
    this.rerankTopN = rerankTopN;
  }

  public void setSearchFilters(List<Map<String, Object>> searchFilters) {
    this.searchFilters = searchFilters;
  }

  public List<Map<String, Object>> getSearchFilters() {
    return searchFilters;
  }

  public static class Builder {

    protected DashScopeDocumentRetrieverOptions options;

    public Builder() {
      this.options = new DashScopeDocumentRetrieverOptions();
    }

    public Builder indexName(String indexName) {
      this.options.setIndexName(indexName);
      return this;
    }

    @Deprecated
    public Builder withIndexName(String indexName) {
      return indexName(indexName);
    }

    public Builder denseSimilarityTopK(Integer denseSimilarityTopK) {
      this.options.setDenseSimilarityTopK(denseSimilarityTopK);
      return this;
    }

    @Deprecated
    public Builder withDenseSimilarityTopK(Integer denseSimilarityTopK) {
      return denseSimilarityTopK(denseSimilarityTopK);
    }

    public Builder sparseSimilarityTopK(int sparseSimilarityTopK) {
      this.options.setSparseSimilarityTopK(sparseSimilarityTopK);
      return this;
    }

    @Deprecated
    public Builder withSparseSimilarityTopK(int sparseSimilarityTopK) {
      return sparseSimilarityTopK(sparseSimilarityTopK);
    }

    public Builder enableRewrite(boolean enableRewrite) {
      this.options.setEnableRewrite(enableRewrite);
      return this;
    }

    @Deprecated
    public Builder withEnableRewrite(boolean enableRewrite) {
      return enableRewrite(enableRewrite);
    }

    public Builder rewriteModelName(String rewriteModelName) {
      this.options.setRewriteModelName(rewriteModelName);
      return this;
    }

    @Deprecated
    public Builder withRewriteModelName(String rewriteModelName) {
      return rewriteModelName(rewriteModelName);
    }

    public Builder enableReranking(boolean enableReranking) {
      this.options.setEnableReranking(enableReranking);
      return this;
    }

    @Deprecated
    public Builder withEnableReranking(boolean enableReranking) {
      this.options.setEnableReranking(enableReranking);
      return this;
    }

    public Builder rerankModelName(String textType) {
      this.options.setRerankModelName(textType);
      return this;
    }

    @Deprecated
    public Builder withRerankModelName(String textType) {
      return rerankModelName(textType);
    }

    public Builder rerankMinScore(float rerankMinScore) {
      this.options.setRerankMinScore(rerankMinScore);
      return this;
    }

    @Deprecated
    public Builder withRerankMinScore(float rerankMinScore) {
      return rerankMinScore(rerankMinScore);
    }

    public Builder rerankTopN(int rerankTopN) {
      this.options.setRerankTopN(rerankTopN);
      return this;
    }

    @Deprecated
    public Builder withRerankTopN(int rerankTopN) {
      return rerankTopN(rerankTopN);
    }

    public Builder searchFilters(List<Map<String, Object>> searchFilters) {
      this.options.setSearchFilters(searchFilters);
      return this;
    }

    @Deprecated
    public Builder withSearchFilters(List<Map<String, Object>> searchFilters) {
      return searchFilters(searchFilters);
    }

    public DashScopeDocumentRetrieverOptions build() {
      return this.options;
    }
  }
}
