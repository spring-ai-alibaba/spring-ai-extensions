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

package com.alibaba.cloud.ai.autoconfigure.dashscope;

import java.util.List;

import com.alibaba.cloud.ai.dashscope.agent.DashScopeAgentFlowStreamMode;
import com.alibaba.cloud.ai.dashscope.agent.DashScopeAgentOptions;
import com.alibaba.cloud.ai.dashscope.agent.DashScopeAgentRagOptions;
import org.jspecify.annotations.Nullable;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.DeprecatedConfigurationProperty;
import tools.jackson.databind.JsonNode;

import static com.alibaba.cloud.ai.dashscope.common.DashScopeApiConstants.APPS_COMPLETION_RESTFUL_URL;

/**
 * DashScope agent auto-configuration properties.
 *
 * @author yuluo
 * @author <a href="mailto:yuluo08290126@gmail.com">yuluo</a>
 * @author xuguan
 */

@ConfigurationProperties(DashScopeAgentProperties.CONFIG_PREFIX)
public class DashScopeAgentProperties extends DashScopeParentProperties {

    /**
     * Spring AI Alibaba configuration prefix.
     */
    public static final String CONFIG_PREFIX = "spring.ai.dashscope.agent";

    /**
     * Enable DashScope ai agent client.
     */
    private boolean enabled = true;

    /**
     * DashScope ai agent path.
     */
    private String agentPath = APPS_COMPLETION_RESTFUL_URL;

    private @Nullable String appId;

    private @Nullable String sessionId;

    private @Nullable String memoryId;

    private @Nullable String modelId;

    private @Nullable Boolean incrementalOutput;

    private @Nullable Boolean hasThoughts;

    private @Nullable Boolean enableThinking;

    private @Nullable List<String> images;

    private @Nullable List<String> files;

    private @Nullable JsonNode bizParams;

    private @Nullable DashScopeAgentRagOptions ragOptions;

    private @Nullable DashScopeAgentFlowStreamMode flowStreamMode;

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getAgentPath() {
        return this.agentPath;
    }

    public void setAgentPath(String agentPath) {
        this.agentPath = agentPath;
    }

    public @Nullable String getAppId() {
        return this.appId;
    }

    public void setAppId(@Nullable String appId) {
        this.appId = appId;
    }

    public @Nullable String getSessionId() {
        return this.sessionId;
    }

    public void setSessionId(@Nullable String sessionId) {
        this.sessionId = sessionId;
    }

    public @Nullable String getMemoryId() {
        return this.memoryId;
    }

    public void setMemoryId(@Nullable String memoryId) {
        this.memoryId = memoryId;
    }

    public @Nullable String getModelId() {
        return this.modelId;
    }

    public void setModelId(@Nullable String modelId) {
        this.modelId = modelId;
    }

    public @Nullable Boolean getIncrementalOutput() {
        return this.incrementalOutput;
    }

    public void setIncrementalOutput(@Nullable Boolean incrementalOutput) {
        this.incrementalOutput = incrementalOutput;
    }

    public @Nullable Boolean getHasThoughts() {
        return this.hasThoughts;
    }

    public void setHasThoughts(@Nullable Boolean hasThoughts) {
        this.hasThoughts = hasThoughts;
    }

    public @Nullable Boolean getEnableThinking() {
        return this.enableThinking;
    }

    public void setEnableThinking(@Nullable Boolean enableThinking) {
        this.enableThinking = enableThinking;
    }

    public @Nullable List<String> getImages() {
        return this.images;
    }

    public void setImages(@Nullable List<String> images) {
        this.images = images;
    }

    public @Nullable List<String> getFiles() {
        return this.files;
    }

    public void setFiles(@Nullable List<String> files) {
        this.files = files;
    }

    public @Nullable JsonNode getBizParams() {
        return this.bizParams;
    }

    public void setBizParams(@Nullable JsonNode bizParams) {
        this.bizParams = bizParams;
    }

    public @Nullable DashScopeAgentRagOptions getRagOptions() {
        return this.ragOptions;
    }

    public void setRagOptions(@Nullable DashScopeAgentRagOptions ragOptions) {
        this.ragOptions = ragOptions;
    }

    public @Nullable DashScopeAgentFlowStreamMode getFlowStreamMode() {
        return this.flowStreamMode;
    }

    public void setFlowStreamMode(@Nullable DashScopeAgentFlowStreamMode flowStreamMode) {
        this.flowStreamMode = flowStreamMode;
    }

    public DashScopeAgentOptions toOptions() {
        return DashScopeAgentOptions.builder()
                .appId(this.appId)
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
                .flowStreamMode(this.flowStreamMode)
                .build();
    }

    private Options options = new Options();

    @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX)
    @Deprecated(since = "2.0.0", forRemoval = true)
    public Options getOptions() {
        return this.options;
    }

    public void setOptions(Options options) {
        this.options = options;
    }

    public class Options {

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".app-id")
        @Deprecated(since = "2.0.0", forRemoval = true)
        public @Nullable String getAppId() {
            return DashScopeAgentProperties.this.getAppId();
        }

        public void setAppId(@Nullable String appId) {
            DashScopeAgentProperties.this.setAppId(appId);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".session-id")
        @Deprecated(since = "2.0.0", forRemoval = true)
        public @Nullable String getSessionId() {
            return DashScopeAgentProperties.this.getSessionId();
        }

        public void setSessionId(@Nullable String sessionId) {
            DashScopeAgentProperties.this.setSessionId(sessionId);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".memory-id")
        @Deprecated(since = "2.0.0", forRemoval = true)
        public @Nullable String getMemoryId() {
            return DashScopeAgentProperties.this.getMemoryId();
        }

        public void setMemoryId(@Nullable String memoryId) {
            DashScopeAgentProperties.this.setMemoryId(memoryId);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".model-id")
        @Deprecated(since = "2.0.0", forRemoval = true)
        public @Nullable String getModelId() {
            return DashScopeAgentProperties.this.getModelId();
        }

        public void setModelId(@Nullable String modelId) {
            DashScopeAgentProperties.this.setModelId(modelId);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".incremental-output")
        @Deprecated(since = "2.0.0", forRemoval = true)
        public @Nullable Boolean getIncrementalOutput() {
            return DashScopeAgentProperties.this.getIncrementalOutput();
        }

        public void setIncrementalOutput(@Nullable Boolean incrementalOutput) {
            DashScopeAgentProperties.this.setIncrementalOutput(incrementalOutput);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".has-thoughts")
        @Deprecated(since = "2.0.0", forRemoval = true)
        public @Nullable Boolean getHasThoughts() {
            return DashScopeAgentProperties.this.getHasThoughts();
        }

        public void setHasThoughts(@Nullable Boolean hasThoughts) {
            DashScopeAgentProperties.this.setHasThoughts(hasThoughts);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".enable-thinking")
        @Deprecated(since = "2.0.0", forRemoval = true)
        public @Nullable Boolean getEnableThinking() {
            return DashScopeAgentProperties.this.getEnableThinking();
        }

        public void setEnableThinking(@Nullable Boolean enableThinking) {
            DashScopeAgentProperties.this.setEnableThinking(enableThinking);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".images")
        @Deprecated(since = "2.0.0", forRemoval = true)
        public @Nullable List<String> getImages() {
            return DashScopeAgentProperties.this.getImages();
        }

        public void setImages(@Nullable List<String> images) {
            DashScopeAgentProperties.this.setImages(images);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".files")
        @Deprecated(since = "2.0.0", forRemoval = true)
        public @Nullable List<String> getFiles() {
            return DashScopeAgentProperties.this.getFiles();
        }

        public void setFiles(@Nullable List<String> files) {
            DashScopeAgentProperties.this.setFiles(files);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".biz-params")
        @Deprecated(since = "2.0.0", forRemoval = true)
        public @Nullable JsonNode getBizParams() {
            return DashScopeAgentProperties.this.getBizParams();
        }

        public void setBizParams(@Nullable JsonNode bizParams) {
            DashScopeAgentProperties.this.setBizParams(bizParams);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".rag-options")
        @Deprecated(since = "2.0.0", forRemoval = true)
        public @Nullable DashScopeAgentRagOptions getRagOptions() {
            return DashScopeAgentProperties.this.getRagOptions();
        }

        public void setRagOptions(@Nullable DashScopeAgentRagOptions ragOptions) {
            DashScopeAgentProperties.this.setRagOptions(ragOptions);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".flow-stream-mode")
        @Deprecated(since = "2.0.0", forRemoval = true)
        public @Nullable DashScopeAgentFlowStreamMode getFlowStreamMode() {
            return DashScopeAgentProperties.this.getFlowStreamMode();
        }

        public void setFlowStreamMode(@Nullable DashScopeAgentFlowStreamMode flowStreamMode) {
            DashScopeAgentProperties.this.setFlowStreamMode(flowStreamMode);
        }

    }

}
