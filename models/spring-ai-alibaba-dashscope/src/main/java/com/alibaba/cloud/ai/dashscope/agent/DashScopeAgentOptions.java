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
import org.jspecify.annotations.Nullable;
import tools.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.DefaultChatOptionsBuilder;

/**
 * @author yuluo
 * @author <a href="mailto:yuluo08290126@gmail.com">yuluo</a>
 * @since 1.0.0-M2
 */
public class DashScopeAgentOptions implements ChatOptions {

    @JsonProperty("app_id")
    private final @Nullable String appId;

    @JsonProperty("session_id")
    private final @Nullable String sessionId;

    @JsonProperty("memory_id")
    private final @Nullable String memoryId;

    @JsonProperty("model_id")
    private final @Nullable String modelId;

    @JsonProperty("incremental_output")
    private final @Nullable Boolean incrementalOutput;

    @JsonProperty("has_thoughts")
    private final @Nullable Boolean hasThoughts;

    @JsonProperty("enable_thinking")
    private final @Nullable Boolean enableThinking;

    @JsonProperty("images")
    private final @Nullable List<String> images;

    @JsonProperty("file_list")
    private final @Nullable List<String> files;

    @JsonProperty("biz_params")
    private final @Nullable JsonNode bizParams;

    @JsonProperty("rag_options")
    private final @Nullable DashScopeAgentRagOptions ragOptions;

    @JsonProperty("flow_stream_mode")
    private final @Nullable DashScopeAgentFlowStreamMode flowStreamMode;

    protected DashScopeAgentOptions(
            @Nullable String appId,
            @Nullable String sessionId,
            @Nullable String memoryId,
            @Nullable String modelId,
            @Nullable Boolean incrementalOutput,
            @Nullable Boolean hasThoughts,
            @Nullable Boolean enableThinking,
            @Nullable List<String> images,
            @Nullable List<String> files,
            @Nullable JsonNode bizParams,
            @Nullable DashScopeAgentRagOptions ragOptions,
            @Nullable DashScopeAgentFlowStreamMode flowStreamMode) {
        this.appId = appId;
        this.sessionId = sessionId;
        this.memoryId = memoryId;
        this.modelId = modelId;
        this.incrementalOutput = incrementalOutput;
        this.hasThoughts = hasThoughts;
        this.enableThinking = enableThinking;
        this.images = images != null ? new ArrayList<>(images) : null;
        this.files = files != null ? new ArrayList<>(files) : null;
        this.bizParams = bizParams != null ? bizParams.deepCopy() : null;
        this.ragOptions = ragOptions;
        this.flowStreamMode = flowStreamMode;
    }

    @Override
    public @Nullable String getModel() {
        return this.modelId;
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
        return this.appId;
    }

    public @Nullable String getSessionId() {
        return this.sessionId;
    }

    public @Nullable String getMemoryId() {
        return this.memoryId;
    }

    public @Nullable String getModelId() {
        return this.modelId;
    }

    public @Nullable Boolean getIncrementalOutput() {
        return this.incrementalOutput;
    }

    public @Nullable Boolean getHasThoughts() {
        return this.hasThoughts;
    }

    public @Nullable Boolean getEnableThinking() {
        return this.enableThinking;
    }

    public @Nullable List<String> getImages() {
        return this.images;
    }

    public @Nullable List<String> getFiles() {
        return this.files;
    }

    public @Nullable JsonNode getBizParams() {
        return this.bizParams;
    }

    public @Nullable DashScopeAgentRagOptions getRagOptions() {
        return this.ragOptions;
    }

    public @Nullable DashScopeAgentFlowStreamMode getFlowStreamMode() {
        return this.flowStreamMode;
    }

    @Override
    public ChatOptions copy() {
        return mutate().build();
    }

    @Override
    public Builder mutate() {
        return builder().appId(this.appId)
                .sessionId(this.sessionId)
                .memoryId(this.memoryId)
                .modelId(this.modelId)
                .incrementalOutput(this.incrementalOutput)
                .hasThoughts(this.hasThoughts)
                .enableThinking(this.enableThinking)
                .images(this.images)
                .files(this.files)
                .bizParams(this.bizParams)
                .ragOptions(this.ragOptions)
                .flowStreamMode(this.flowStreamMode);
    }

    public static DashScopeAgentOptions fromOptions(DashScopeAgentOptions options) {
        return options.mutate().build();
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DashScopeAgentOptions that = (DashScopeAgentOptions) o;
        return Objects.equals(this.appId, that.appId) && Objects.equals(this.sessionId, that.sessionId)
                && Objects.equals(this.memoryId, that.memoryId) && Objects.equals(this.modelId, that.modelId)
                && Objects.equals(this.incrementalOutput, that.incrementalOutput)
                && Objects.equals(this.hasThoughts, that.hasThoughts)
                && Objects.equals(this.enableThinking, that.enableThinking) && Objects.equals(this.images, that.images)
                && Objects.equals(this.files, that.files) && Objects.equals(this.bizParams, that.bizParams)
                && Objects.equals(this.ragOptions, that.ragOptions)
                && Objects.equals(this.flowStreamMode, that.flowStreamMode);
    }

    // @formatter:off
	@Override
	public int hashCode() {
		return Objects.hash(this.appId, this.sessionId, this.memoryId, this.modelId, this.incrementalOutput,
				this.hasThoughts, this.enableThinking, this.images, this.files, this.bizParams, this.ragOptions,
				this.flowStreamMode);
	}
    // @formatter:on

    @Override
    public String toString() {
        return "DashScopeAgentOptions{" + "appId='" + this.appId + '\'' + ", sessionId='" + this.sessionId + '\''
                + ", memoryId='" + this.memoryId + '\'' + ", modelId='" + this.modelId + '\'' + ", incrementalOutput="
                + this.incrementalOutput + ", hasThoughts=" + this.hasThoughts + ", enableThinking="
                + this.enableThinking + ", images=" + this.images + ", files=" + this.files + ", bizParams="
                + this.bizParams + ", ragOptions=" + this.ragOptions + ", flowStreamMode=" + this.flowStreamMode + '}';
    }

    public static class Builder extends AbstractBuilder<Builder> {

    }

    protected abstract static class AbstractBuilder<B extends AbstractBuilder<B>>
            extends DefaultChatOptionsBuilder<B> {

        protected @Nullable String appId;

        protected @Nullable String sessionId;

        protected @Nullable String memoryId;

        protected @Nullable String modelId;

        protected @Nullable Boolean incrementalOutput;

        protected @Nullable Boolean hasThoughts;

        protected @Nullable Boolean enableThinking;

        protected @Nullable List<String> images;

        protected @Nullable List<String> files;

        protected @Nullable JsonNode bizParams;

        protected @Nullable DashScopeAgentRagOptions ragOptions;

        protected @Nullable DashScopeAgentFlowStreamMode flowStreamMode;

        @Override
        public B clone() {
            B copy = super.clone();
            copy.images = this.images != null ? new ArrayList<>(this.images) : null;
            copy.files = this.files != null ? new ArrayList<>(this.files) : null;
            return copy;
        }

        @Override
        public B model(@Nullable String model) {
            this.modelId = model;
            return self();
        }

        public B appId(@Nullable String appId) {
            this.appId = appId;
            return self();
        }

        public B sessionId(@Nullable String sessionId) {
            this.sessionId = sessionId;
            return self();
        }

        public B memoryId(@Nullable String memoryId) {
            this.memoryId = memoryId;
            return self();
        }

        public B modelId(@Nullable String modelId) {
            this.modelId = modelId;
            return self();
        }

        public B incrementalOutput(@Nullable Boolean incrementalOutput) {
            this.incrementalOutput = incrementalOutput;
            return self();
        }

        public B hasThoughts(@Nullable Boolean hasThoughts) {
            this.hasThoughts = hasThoughts;
            return self();
        }

        public B enableThinking(@Nullable Boolean enableThinking) {
            this.enableThinking = enableThinking;
            return self();
        }

        public B images(@Nullable List<String> images) {
            this.images = images;
            return self();
        }

        public B files(@Nullable List<String> files) {
            this.files = files;
            return self();
        }

        public B bizParams(@Nullable JsonNode bizParams) {
            this.bizParams = bizParams;
            return self();
        }

        public B ragOptions(@Nullable DashScopeAgentRagOptions ragOptions) {
            this.ragOptions = ragOptions;
            return self();
        }

        public B flowStreamMode(@Nullable DashScopeAgentFlowStreamMode flowStreamMode) {
            this.flowStreamMode = flowStreamMode;
            return self();
        }

        @Override
        public B combineWith(ChatOptions.Builder<?> other) {
            super.combineWith(other);
            if (other instanceof AbstractBuilder<?> that) {
                if (that.appId != null) {
                    this.appId = that.appId;
                }
                if (that.sessionId != null) {
                    this.sessionId = that.sessionId;
                }
                if (that.memoryId != null) {
                    this.memoryId = that.memoryId;
                }
                if (that.modelId != null) {
                    this.modelId = that.modelId;
                }
                if (that.incrementalOutput != null) {
                    this.incrementalOutput = that.incrementalOutput;
                }
                if (that.hasThoughts != null) {
                    this.hasThoughts = that.hasThoughts;
                }
                if (that.enableThinking != null) {
                    this.enableThinking = that.enableThinking;
                }
                if (that.images != null) {
                    this.images = that.images;
                }
                if (that.files != null) {
                    this.files = that.files;
                }
                if (that.bizParams != null) {
                    this.bizParams = that.bizParams;
                }
                if (that.ragOptions != null) {
                    this.ragOptions = that.ragOptions;
                }
                if (that.flowStreamMode != null) {
                    this.flowStreamMode = that.flowStreamMode;
                }
            }
            return self();
        }

        // @formatter:off
		@Override
		public DashScopeAgentOptions build() {
			return new DashScopeAgentOptions(this.appId, this.sessionId, this.memoryId, this.modelId,
					this.incrementalOutput, this.hasThoughts, this.enableThinking, this.images, this.files,
					this.bizParams, this.ragOptions, this.flowStreamMode);
		}
        // @formatter:on

    }

}
