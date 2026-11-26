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

package com.alibaba.cloud.ai.autoconfigure.dashscope.sdk;

import java.util.Map;

import com.alibaba.cloud.ai.dashscope.sdk.chat.DashScopeSdkChatOptions;
import org.jspecify.annotations.Nullable;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.DeprecatedConfigurationProperty;

/**
 * DashScope SDK chat model properties.
 */
@ConfigurationProperties(DashScopeSdkChatProperties.CONFIG_PREFIX)
public class DashScopeSdkChatProperties extends DashScopeSdkParentProperties {

    public static final String CONFIG_PREFIX = "spring.ai.dashscope.sdk.chat";

    public static final String DEFAULT_DEPLOYMENT_NAME = "qwen-plus";

    private boolean enabled = true;

    private @Nullable String model = DEFAULT_DEPLOYMENT_NAME;

    private @Nullable Boolean stream;

    private @Nullable Double temperature;

    private @Nullable Integer seed;

    private @Nullable Double topP;

    private @Nullable Integer topK;

    private @Nullable Object stop;

    private @Nullable Boolean enableSearch;

    private @Nullable Integer maxTokens;

    private @Nullable Boolean incrementalOutput;

    private @Nullable Double repetitionPenalty;

    private @Nullable Object toolChoice;

    private @Nullable Map<String, String> httpHeaders;

    private @Nullable String baseUrl;

    private @Nullable Boolean multiModel;

    private @Nullable Boolean enableThinking;

    private @Nullable Boolean preserveThinking;

    private @Nullable Boolean clearThinking;

    private @Nullable String thinkingDisplay;

    private @Nullable Integer thinkingBudget;

    private @Nullable Integer maxCompletionTokens;

    private @Nullable String reasoningEffort;

    private @Nullable Boolean toolStream;

    private @Nullable Map<String, Object> extraBody;

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public @Nullable String getModel() {
        return this.model;
    }

    public void setModel(@Nullable String model) {
        this.model = model;
    }

    public @Nullable Boolean getStream() {
        return this.stream;
    }

    public void setStream(@Nullable Boolean stream) {
        this.stream = stream;
    }

    public @Nullable Double getTemperature() {
        return this.temperature;
    }

    public void setTemperature(@Nullable Double temperature) {
        this.temperature = temperature;
    }

    public @Nullable Integer getSeed() {
        return this.seed;
    }

    public void setSeed(@Nullable Integer seed) {
        this.seed = seed;
    }

    public @Nullable Double getTopP() {
        return this.topP;
    }

    public void setTopP(@Nullable Double topP) {
        this.topP = topP;
    }

    public @Nullable Integer getTopK() {
        return this.topK;
    }

    public void setTopK(@Nullable Integer topK) {
        this.topK = topK;
    }

    public @Nullable Object getStop() {
        return this.stop;
    }

    public void setStop(@Nullable Object stop) {
        this.stop = stop;
    }

    public @Nullable Boolean getEnableSearch() {
        return this.enableSearch;
    }

    public void setEnableSearch(@Nullable Boolean enableSearch) {
        this.enableSearch = enableSearch;
    }

    public @Nullable Integer getMaxTokens() {
        return this.maxTokens;
    }

    public void setMaxTokens(@Nullable Integer maxTokens) {
        this.maxTokens = maxTokens;
    }

    public @Nullable Boolean getIncrementalOutput() {
        return this.incrementalOutput;
    }

    public void setIncrementalOutput(@Nullable Boolean incrementalOutput) {
        this.incrementalOutput = incrementalOutput;
    }

    public @Nullable Double getRepetitionPenalty() {
        return this.repetitionPenalty;
    }

    public void setRepetitionPenalty(@Nullable Double repetitionPenalty) {
        this.repetitionPenalty = repetitionPenalty;
    }

    public @Nullable Object getToolChoice() {
        return this.toolChoice;
    }

    public void setToolChoice(@Nullable Object toolChoice) {
        this.toolChoice = toolChoice;
    }

    public @Nullable Map<String, String> getHttpHeaders() {
        return this.httpHeaders;
    }

    public void setHttpHeaders(@Nullable Map<String, String> httpHeaders) {
        this.httpHeaders = httpHeaders;
    }

    public @Nullable Boolean getToolStream() {
        return this.toolStream;
    }

    public void setToolStream(@Nullable Boolean toolStream) {
        this.toolStream = toolStream;
    }

    public @Nullable String getReasoningEffort() {
        return this.reasoningEffort;
    }

    public void setReasoningEffort(@Nullable String reasoningEffort) {
        this.reasoningEffort = reasoningEffort;
    }

    public @Nullable Integer getThinkingBudget() {
        return this.thinkingBudget;
    }

    public void setThinkingBudget(@Nullable Integer thinkingBudget) {
        this.thinkingBudget = thinkingBudget;
    }

    public @Nullable String getThinkingDisplay() {
        return this.thinkingDisplay;
    }

    public void setThinkingDisplay(@Nullable String thinkingDisplay) {
        this.thinkingDisplay = thinkingDisplay;
    }

    public @Nullable Boolean getClearThinking() {
        return this.clearThinking;
    }

    public void setClearThinking(@Nullable Boolean clearThinking) {
        this.clearThinking = clearThinking;
    }

    public @Nullable Boolean getPreserveThinking() {
        return this.preserveThinking;
    }

    public void setPreserveThinking(@Nullable Boolean preserveThinking) {
        this.preserveThinking = preserveThinking;
    }

    public @Nullable Boolean getEnableThinking() {
        return this.enableThinking;
    }

    public void setEnableThinking(@Nullable Boolean enableThinking) {
        this.enableThinking = enableThinking;
    }

    public @Nullable Boolean getMultiModel() {
        return this.multiModel;
    }

    public void setMultiModel(@Nullable Boolean multiModel) {
        this.multiModel = multiModel;
    }

    public @Nullable String getBaseUrl() {
        return this.baseUrl;
    }

    public void setBaseUrl(@Nullable String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public @Nullable Integer getMaxCompletionTokens() {
        return this.maxCompletionTokens;
    }

    public void setMaxCompletionTokens(@Nullable Integer maxCompletionTokens) {
        this.maxCompletionTokens = maxCompletionTokens;
    }

    public @Nullable Map<String, Object> getExtraBody() {
        return this.extraBody;
    }

    public void setExtraBody(@Nullable Map<String, Object> extraBody) {
        this.extraBody = extraBody;
    }

    public DashScopeSdkChatOptions toOptions() {
        var builder = DashScopeSdkChatOptions.builder()
                .model(this.model)
                .stream(this.stream)
                .temperature(this.temperature)
                .seed(this.seed)
                .topP(this.topP)
                .topK(this.topK)
                .stop(this.stop)
                .enableSearch(this.enableSearch)
                .maxTokens(this.maxTokens)
                .incrementalOutput(this.incrementalOutput)
                .repetitionPenalty(this.repetitionPenalty)
                .toolChoice(this.toolChoice)
                .httpHeaders(this.httpHeaders)
                .extraBody(this.extraBody);
        if (this.multiModel != null) {
            builder.multiModel(this.multiModel);
        }
        if (this.enableThinking != null) {
            builder.enableThinking(this.enableThinking);
        }
        if (this.preserveThinking != null) {
            builder.preserveThinking(this.preserveThinking);
        }
        if (this.clearThinking != null) {
            builder.clearThinking(this.clearThinking);
        }
        if (this.thinkingDisplay != null) {
            builder.thinkingDisplay(this.thinkingDisplay);
        }
        if (this.thinkingBudget != null) {
            builder.thinkingBudget(this.thinkingBudget);
        }
        if (this.reasoningEffort != null) {
            builder.reasoningEffort(this.reasoningEffort);
        }
        if (this.toolStream != null) {
            builder.toolStream(this.toolStream);
        }
        if (this.maxCompletionTokens != null) {
            builder.maxCompletionTokens(this.maxCompletionTokens);
        }
        return builder.build();
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

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".model")
        @Deprecated(since = "2.0.0", forRemoval = true)
        public @Nullable String getModel() {
            return DashScopeSdkChatProperties.this.getModel();
        }

        public void setModel(String model) {
            DashScopeSdkChatProperties.this.setModel(model);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".stream")
        @Deprecated(since = "2.0.0", forRemoval = true)
        public @Nullable Boolean getStream() {
            return DashScopeSdkChatProperties.this.getStream();
        }

        public void setStream(Boolean stream) {
            DashScopeSdkChatProperties.this.setStream(stream);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".temperature")
        @Deprecated(since = "2.0.0", forRemoval = true)
        public @Nullable Double getTemperature() {
            return DashScopeSdkChatProperties.this.getTemperature();
        }

        public void setTemperature(Double temperature) {
            DashScopeSdkChatProperties.this.setTemperature(temperature);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".seed")
        @Deprecated(since = "2.0.0", forRemoval = true)
        public @Nullable Integer getSeed() {
            return DashScopeSdkChatProperties.this.getSeed();
        }

        public void setSeed(Integer seed) {
            DashScopeSdkChatProperties.this.setSeed(seed);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".top-p")
        @Deprecated(since = "2.0.0", forRemoval = true)
        public @Nullable Double getTopP() {
            return DashScopeSdkChatProperties.this.getTopP();
        }

        public void setTopP(Double topP) {
            DashScopeSdkChatProperties.this.setTopP(topP);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".top-k")
        @Deprecated(since = "2.0.0", forRemoval = true)
        public @Nullable Integer getTopK() {
            return DashScopeSdkChatProperties.this.getTopK();
        }

        public void setTopK(Integer topK) {
            DashScopeSdkChatProperties.this.setTopK(topK);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".stop")
        @Deprecated(since = "2.0.0", forRemoval = true)
        public @Nullable Object getStop() {
            return DashScopeSdkChatProperties.this.getStop();
        }

        public void setStop(Object stop) {
            DashScopeSdkChatProperties.this.setStop(stop);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".enable-search")
        @Deprecated(since = "2.0.0", forRemoval = true)
        public @Nullable Boolean getEnableSearch() {
            return DashScopeSdkChatProperties.this.getEnableSearch();
        }

        public void setEnableSearch(Boolean enableSearch) {
            DashScopeSdkChatProperties.this.setEnableSearch(enableSearch);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".max-tokens")
        @Deprecated(since = "2.0.0", forRemoval = true)
        public @Nullable Integer getMaxTokens() {
            return DashScopeSdkChatProperties.this.getMaxTokens();
        }

        public void setMaxTokens(Integer maxTokens) {
            DashScopeSdkChatProperties.this.setMaxTokens(maxTokens);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".incremental-output")
        @Deprecated(since = "2.0.0", forRemoval = true)
        public @Nullable Boolean getIncrementalOutput() {
            return DashScopeSdkChatProperties.this.getIncrementalOutput();
        }

        public void setIncrementalOutput(Boolean incrementalOutput) {
            DashScopeSdkChatProperties.this.setIncrementalOutput(incrementalOutput);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".repetition-penalty")
        @Deprecated(since = "2.0.0", forRemoval = true)
        public @Nullable Double getRepetitionPenalty() {
            return DashScopeSdkChatProperties.this.getRepetitionPenalty();
        }

        public void setRepetitionPenalty(Double repetitionPenalty) {
            DashScopeSdkChatProperties.this.setRepetitionPenalty(repetitionPenalty);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".tool-choice")
        @Deprecated(since = "2.0.0", forRemoval = true)
        public @Nullable Object getToolChoice() {
            return DashScopeSdkChatProperties.this.getToolChoice();
        }

        public void setToolChoice(Object toolChoice) {
            DashScopeSdkChatProperties.this.setToolChoice(toolChoice);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".http-headers")
        @Deprecated(since = "2.0.0", forRemoval = true)
        public @Nullable Map<String, String> getHttpHeaders() {
            return DashScopeSdkChatProperties.this.getHttpHeaders();
        }

        public void setHttpHeaders(Map<String, String> httpHeaders) {
            DashScopeSdkChatProperties.this.setHttpHeaders(httpHeaders);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".extra-body")
        @Deprecated(since = "2.0.0", forRemoval = true)
        public @Nullable Map<String, Object> getExtraBody() {
            return DashScopeSdkChatProperties.this.getExtraBody();
        }

        public void setExtraBody(Map<String, Object> extraBody) {
            DashScopeSdkChatProperties.this.setExtraBody(extraBody);
        }

    }

}
