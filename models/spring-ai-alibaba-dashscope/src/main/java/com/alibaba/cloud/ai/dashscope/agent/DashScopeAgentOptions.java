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
import tools.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.jspecify.annotations.Nullable;
import org.springframework.ai.chat.prompt.ChatOptions;

/**
 * @author yuluo
 * @author <a href="mailto:yuluo08290126@gmail.com">yuluo</a>
 * @since 1.0.0-M2
 */
public class DashScopeAgentOptions implements ChatOptions {

    @JsonProperty("app_id")
    private @Nullable String appId;

    @JsonProperty("session_id")
    private @Nullable String sessionId;

    @JsonProperty("memory_id")
    private @Nullable String memoryId;

    @JsonProperty("model_id")
    @Nullable String modelId;

    @JsonProperty("incremental_output")
    private @Nullable Boolean incrementalOutput;

    @JsonProperty("has_thoughts")
    private @Nullable Boolean hasThoughts;

    @JsonProperty("enable_thinking")
    @Nullable Boolean enableThinking;

    @JsonProperty("images")
    private @Nullable List<String> images;

    @JsonProperty("file_list")
    @Nullable List<String> files;

    @JsonProperty("biz_params")
    private @Nullable JsonNode bizParams;

    @JsonProperty("rag_options")
    private @Nullable DashScopeAgentRagOptions ragOptions;

    @JsonProperty("flow_stream_mode")
    private @Nullable DashScopeAgentFlowStreamMode flowStreamMode;

    @Override
    public @Nullable String getModel() {
        return modelId;
    }

    @Override
    public @Nullable Double getFrequencyPenalty() {
        return null;
    }

    @Override
    public @Nullable Integer getMaxTokens() {
        return null;
    }

    @Override
    public @Nullable Double getPresencePenalty() {
        return null;
    }

    @Override
    public @Nullable List<String> getStopSequences() {
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

    public @Nullable String getAppId() {
        return appId;
    }

    public void setAppId(@Nullable String appId) {
        this.appId = appId;
    }

    public @Nullable String getSessionId() {
        return sessionId;
    }

    public void setSessionId(@Nullable String sessionId) {
        this.sessionId = sessionId;
    }

    public @Nullable String getMemoryId() {
        return memoryId;
    }

    public void setMemoryId(@Nullable String memoryId) {
        this.memoryId = memoryId;
    }

    public @Nullable String getModelId() {
        return modelId;
    }

    public void setModelId(@Nullable String modelId) {
        this.modelId = modelId;
    }

    public @Nullable Boolean getIncrementalOutput() {
        return incrementalOutput;
    }

    public void setIncrementalOutput(@Nullable Boolean incrementalOutput) {
        this.incrementalOutput = incrementalOutput;
    }

    public @Nullable Boolean getHasThoughts() {
        return hasThoughts;
    }

    public void setHasThoughts(@Nullable Boolean hasThoughts) {
        this.hasThoughts = hasThoughts;
    }

    public @Nullable Boolean getEnableThinking() {
        return enableThinking;
    }

    public void setEnableThinking(@Nullable Boolean enableThinking) {
        this.enableThinking = enableThinking;
    }

    public @Nullable List<String> getImages() {
        return images;
    }

    public void setImages(@Nullable List<String> images) {
        this.images = images;
    }

    public @Nullable List<String> getFiles() {
        return files;
    }

    public void setFiles(@Nullable List<String> files) {
        this.files = files;
    }

    public @Nullable JsonNode getBizParams() {
        return bizParams;
    }

    public void setBizParams(@Nullable JsonNode bizParams) {
        this.bizParams = bizParams;
    }

    public @Nullable DashScopeAgentRagOptions getRagOptions() {
        return ragOptions;
    }

    public void setRagOptions(@Nullable DashScopeAgentRagOptions ragOptions) {
        this.ragOptions = ragOptions;
    }

    public @Nullable DashScopeAgentFlowStreamMode getFlowStreamMode() {
        return flowStreamMode;
    }

    public void setFlowStreamMode(@Nullable DashScopeAgentFlowStreamMode flowStreamMode) {
        this.flowStreamMode = flowStreamMode;
    }

    @Override
    public ChatOptions copy() {
        return Objects.requireNonNull(DashScopeAgentOptions.fromOptions(this), "Copied options must not be null");
    }

    public static @Nullable DashScopeAgentOptions fromOptions(@Nullable DashScopeAgentOptions options) {
        if (options == null) {
            return null;
        }
        return DashScopeAgentOptions.builder()
                .appId(options.getAppId())
                .sessionId(options.getSessionId())
                .memoryId(options.getMemoryId())
                .modelId(options.getModelId())
                .incrementalOutput(options.getIncrementalOutput())
                .hasThoughts(options.getHasThoughts())
                .enableThinking(options.getEnableThinking())
                .images(options.getImages() == null ? null : new ArrayList<>(options.getImages()))
                .files(options.getFiles() == null ? null : new ArrayList<>(options.getFiles()))
                .bizParams(options.getBizParams() == null ? null : options.getBizParams().deepCopy())
                .ragOptions(copyRagOptions(options.getRagOptions()))
                .flowStreamMode(options.getFlowStreamMode())
                .build();
    }

    private static @Nullable DashScopeAgentRagOptions copyRagOptions(@Nullable DashScopeAgentRagOptions source) {
        if (source == null) {
            return null;
        }
        return DashScopeAgentRagOptions.builder()
                .pipelineIds(source.getPipelineIds() == null ? null : new ArrayList<>(source.getPipelineIds()))
                .fileIds(source.getFileIds() == null ? null : new ArrayList<>(source.getFileIds()))
                .tags(source.getTags() == null ? null : new ArrayList<>(source.getTags()))
                .metadataFilter(source.getMetadataFilter() == null ? null : source.getMetadataFilter().deepCopy())
                .structuredFilter(
                        source.getStructuredFilter() == null ? null : source.getStructuredFilter().deepCopy())
                .sessionFileIds(
                        source.getSessionFileIds() == null ? null : new ArrayList<>(source.getSessionFileIds()))
                .build();
    }

    public Builder mutate() {
        return new Builder(this);
    }

    public static Builder builder() {

        return new Builder();
    }

    public static class Builder {

        private DashScopeAgentOptions options = new DashScopeAgentOptions();

        private Builder() {
        }

        private Builder(DashScopeAgentOptions options) {
            this.options = options;
        }

        public Builder appId(@Nullable String appId) {
            this.options.setAppId(appId);
            return this;
        }

        public Builder sessionId(@Nullable String sessionId) {
            this.options.sessionId = sessionId;
            return this;
        }

        public Builder memoryId(@Nullable String memoryId) {
            this.options.memoryId = memoryId;
            return this;
        }

        public Builder incrementalOutput(@Nullable Boolean incrementalOutput) {
            this.options.incrementalOutput = incrementalOutput;
            return this;
        }

        public Builder hasThoughts(@Nullable Boolean hasThoughts) {
            this.options.hasThoughts = hasThoughts;
            return this;
        }

        public Builder images(@Nullable List<String> images) {
            this.options.images = images;
            return this;
        }

        public Builder bizParams(@Nullable JsonNode bizParams) {
            this.options.bizParams = bizParams;
            return this;
        }

        public Builder ragOptions(@Nullable DashScopeAgentRagOptions ragOptions) {
            this.options.ragOptions = ragOptions;
            return this;
        }

        public Builder flowStreamMode(@Nullable DashScopeAgentFlowStreamMode flowStreamMode) {
            this.options.flowStreamMode = flowStreamMode;
            return this;
        }

        public Builder enableThinking(@Nullable Boolean enableThinking) {
            this.options.enableThinking = enableThinking;
            return this;
        }

        public Builder modelId(@Nullable String modelId) {
            this.options.modelId = modelId;
            return this;
        }

        public Builder files(@Nullable List<String> files) {
            this.options.files = files;
            return this;
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
        sb.append(", modelId='").append(modelId).append('\'');
        sb.append(", incrementalOutput=").append(incrementalOutput);
        sb.append(", hasThoughts=").append(hasThoughts);
        sb.append(", enableThinking=").append(enableThinking);
        sb.append(", images=").append(images);
        sb.append(", files=").append(files);
        sb.append(", bizParams=").append(bizParams);
        sb.append(", ragOptions=").append(ragOptions);
        sb.append(", flowStreamMode=").append(flowStreamMode);
        sb.append('}');
        return sb.toString();
    }
}
