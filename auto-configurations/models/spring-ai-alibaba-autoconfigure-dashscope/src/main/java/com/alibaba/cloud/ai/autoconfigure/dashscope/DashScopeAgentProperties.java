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
import com.fasterxml.jackson.databind.JsonNode;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.DeprecatedConfigurationProperty;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import static com.alibaba.cloud.ai.dashscope.common.DashScopeApiConstants.APPS_COMPLETION_RESTFUL_URL;

/**
 * @author yuluo
 * @author <a href="mailto:yuluo08290126@gmail.com">yuluo</a>
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

    @NestedConfigurationProperty
    private DashScopeAgentOptions options = DashScopeAgentOptions.builder().build();

    public DashScopeAgentOptions toOptions() {
        return this.options;
    }

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

    @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX)
    @Deprecated(since = "2.0.0", forRemoval = true)
    public DashScopeAgentOptions getOptions() {
        return this.options;
    }

    public void setOptions(DashScopeAgentOptions options) {
        this.options = options;
    }

    public String getAppId() {
        return this.options.getAppId();
    }

    public void setAppId(String appId) {
        this.options.setAppId(appId);
    }

    public String getSessionId() {
        return this.options.getSessionId();
    }

    public void setSessionId(String sessionId) {
        this.options.setSessionId(sessionId);
    }

    public String getMemoryId() {
        return this.options.getMemoryId();
    }

    public void setMemoryId(String memoryId) {
        this.options.setMemoryId(memoryId);
    }

    public String getModelId() {
        return this.options.getModelId();
    }

    public void setModelId(String modelId) {
        this.options.setModelId(modelId);
    }

    public Boolean getIncrementalOutput() {
        return this.options.getIncrementalOutput();
    }

    public void setIncrementalOutput(Boolean incrementalOutput) {
        this.options.setIncrementalOutput(incrementalOutput);
    }

    public Boolean getHasThoughts() {
        return this.options.getHasThoughts();
    }

    public void setHasThoughts(Boolean hasThoughts) {
        this.options.setHasThoughts(hasThoughts);
    }

    public Boolean getEnableThinking() {
        return this.options.getEnableThinking();
    }

    public void setEnableThinking(Boolean enableThinking) {
        this.options.setEnableThinking(enableThinking);
    }

    public List<String> getImages() {
        return this.options.getImages();
    }

    public void setImages(List<String> images) {
        this.options.setImages(images);
    }

    public List<String> getFiles() {
        return this.options.getFiles();
    }

    public void setFiles(List<String> files) {
        this.options.setFiles(files);
    }

    public JsonNode getBizParams() {
        return this.options.getBizParams();
    }

    public void setBizParams(JsonNode bizParams) {
        this.options.setBizParams(bizParams);
    }

    public DashScopeAgentRagOptions getRagOptions() {
        return this.options.getRagOptions();
    }

    public void setRagOptions(DashScopeAgentRagOptions ragOptions) {
        this.options.setRagOptions(ragOptions);
    }

    public DashScopeAgentFlowStreamMode getFlowStreamMode() {
        return this.options.getFlowStreamMode();
    }

    public void setFlowStreamMode(DashScopeAgentFlowStreamMode flowStreamMode) {
        this.options.setFlowStreamMode(flowStreamMode);
    }

}
