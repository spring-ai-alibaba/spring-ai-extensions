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
package com.alibaba.cloud.ai.dashscope.agent;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import org.springframework.ai.chat.prompt.ChatOptions;

/**
 * @author yuluo
 * @author <a href="mailto:yuluo08290126@gmail.com">yuluo</a>
 * @since 1.0.0-M2
 */
public class DashScopeAgentOptions implements ChatOptions {

  @JsonProperty("app_id")
  private String appId;

  @JsonProperty("session_id")
  private String sessionId;

  @JsonProperty("memory_id")
  private String memoryId;

  @JsonProperty("incremental_output")
  private Boolean incrementalOutput;

  @JsonProperty("has_thoughts")
  private Boolean hasThoughts;

  @JsonProperty("images")
  private List<String> images;

  @JsonProperty("biz_params")
  private JsonNode bizParams;

  @JsonProperty("rag_options")
  private DashScopeAgentRagOptions ragOptions;

  @JsonProperty("flow_stream_mode")
  private DashScopeAgentFlowStreamMode flowStreamMode;

  @Override
  public String getModel() {
    return null;
  }

  @Override
  public Double getFrequencyPenalty() {
    return null;
  }

  @Override
  public Integer getMaxTokens() {
    return null;
  }

  @Override
  public Double getPresencePenalty() {
    return null;
  }

  @Override
  public List<String> getStopSequences() {
    return null;
  }

  @Override
  public Double getTemperature() {
    return 0d;
  }

  @Override
  public Double getTopP() {
    return 0d;
  }

  @Override
  public Integer getTopK() {
    return 0;
  }

  public String getAppId() {
    return appId;
  }

  public void setAppId(String appId) {
    this.appId = appId;
  }

  public String getSessionId() {
    return sessionId;
  }

  public void setSessionId(String sessionId) {
    this.sessionId = sessionId;
  }

  public String getMemoryId() {
    return memoryId;
  }

  public void setMemoryId(String memoryId) {
    this.memoryId = memoryId;
  }

  public Boolean getIncrementalOutput() {
    return incrementalOutput;
  }

  public void setIncrementalOutput(Boolean incrementalOutput) {
    this.incrementalOutput = incrementalOutput;
  }

  public Boolean getHasThoughts() {
    return hasThoughts;
  }

  public void setHasThoughts(Boolean hasThoughts) {
    this.hasThoughts = hasThoughts;
  }

  public List<String> getImages() {
    return images;
  }

  public void setImages(List<String> images) {
    this.images = images;
  }

  public JsonNode getBizParams() {
    return bizParams;
  }

  public void setBizParams(JsonNode bizParams) {
    this.bizParams = bizParams;
  }

  public DashScopeAgentRagOptions getRagOptions() {
    return ragOptions;
  }

  public void setRagOptions(DashScopeAgentRagOptions ragOptions) {
    this.ragOptions = ragOptions;
  }

  public DashScopeAgentFlowStreamMode getFlowStreamMode() {
    return flowStreamMode;
  }

  public void setFlowStreamMode(DashScopeAgentFlowStreamMode flowStreamMode) {
    this.flowStreamMode = flowStreamMode;
  }

  @Override
  public ChatOptions copy() {
    return DashScopeAgentOptions.fromOptions(this);
  }

  public static DashScopeAgentOptions fromOptions(DashScopeAgentOptions options) {
    return DashScopeAgentOptions.builder()
        .appId(options.getAppId())
        .sessionId(options.getSessionId())
        .memoryId(options.getMemoryId())
        .incrementalOutput(options.getIncrementalOutput())
        .hasThoughts(options.getHasThoughts())
        .bizParams(options.getBizParams())
        .build();
  }

  public static DashScopeAgentOptions.Builder builder() {

    return new DashScopeAgentOptions.Builder();
  }

  public static class Builder {

    protected DashScopeAgentOptions options;

    public Builder() {
      this.options = new DashScopeAgentOptions();
    }

    public Builder(DashScopeAgentOptions options) {
      this.options = options;
    }

    public Builder appId(String appId) {
      this.options.setAppId(appId);
      return this;
    }

    @Deprecated
    public Builder withAppId(String appId) {
      return appId(appId);
    }

    public Builder sessionId(String sessionId) {
      this.options.sessionId = sessionId;
      return this;
    }

    @Deprecated
    public Builder withSessionId(String sessionId) {
      return sessionId(sessionId);
    }

    public Builder memoryId(String memoryId) {
      this.options.memoryId = memoryId;
      return this;
    }

    @Deprecated
    public Builder withMemoryId(String memoryId) {
      return memoryId(memoryId);
    }

    public Builder incrementalOutput(Boolean incrementalOutput) {
      this.options.incrementalOutput = incrementalOutput;
      return this;
    }

    @Deprecated
    public Builder withIncrementalOutput(Boolean incrementalOutput) {
      return incrementalOutput(incrementalOutput);
    }

    public Builder hasThoughts(Boolean hasThoughts) {
      this.options.hasThoughts = hasThoughts;
      return this;
    }

    @Deprecated
    public Builder withHasThoughts(Boolean hasThoughts) {
      return hasThoughts(hasThoughts);
    }

    public Builder images(List<String> images) {
      this.options.images = images;
      return this;
    }

    @Deprecated
    public Builder withImages(List<String> images) {
      return images(images);
    }

    public Builder bizParams(JsonNode bizParams) {
      this.options.bizParams = bizParams;
      return this;
    }

    @Deprecated
    public Builder withBizParams(JsonNode bizParams) {
      return bizParams(bizParams);
    }

    public Builder ragOptions(DashScopeAgentRagOptions ragOptions) {
      this.options.ragOptions = ragOptions;
      return this;
    }

    @Deprecated
    public Builder withRagOptions(DashScopeAgentRagOptions ragOptions) {
      return ragOptions(ragOptions);
    }

    public Builder flowStreamMode(DashScopeAgentFlowStreamMode flowStreamMode) {
      this.options.flowStreamMode = flowStreamMode;
      return this;
    }

    @Deprecated
    public Builder withFlowStreamMode(DashScopeAgentFlowStreamMode flowStreamMode) {
      return flowStreamMode(flowStreamMode);
    }

    public DashScopeAgentOptions build() {
      return this.options;
    }
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("DashScopeAgentOptions{");
    sb.append("appId='").append(appId).append('\'');
    sb.append(", sessionId='").append(sessionId).append('\'');
    sb.append(", memoryId='").append(memoryId).append('\'');
    sb.append(", incrementalOutput=").append(incrementalOutput);
    sb.append(", hasThoughts=").append(hasThoughts);
    sb.append(", images=").append(images);
    sb.append(", bizParams=").append(bizParams);
    sb.append(", ragOptions=").append(ragOptions);
    sb.append(", flowStreamMode=").append(flowStreamMode);
    sb.append('}');
    return sb.toString();
  }
}
