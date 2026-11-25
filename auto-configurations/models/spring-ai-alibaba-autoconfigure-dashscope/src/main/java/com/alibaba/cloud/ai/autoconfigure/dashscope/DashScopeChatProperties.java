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
import java.util.Map;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatApiSpec.ChatCompletionRequest.Parameters.ResponseFormat;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatApiSpec.ChatCompletionRequest.Parameters.SearchOptions;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatApiSpec.ChatCompletionRequest.Parameters.Skill;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatApiSpec.ChatCompletionRequest.Parameters.Tool;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.alibaba.cloud.ai.dashscope.common.DashScopeChatApiConstants;
import com.alibaba.cloud.ai.dashscope.spec.DashScopeModel.ChatModel;
import org.jspecify.annotations.Nullable;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.DeprecatedConfigurationProperty;

/**
 * DashScope chat auto-configuration properties.
 *
 * @author yuluo
 * @author <a href="mailto:yuluo08290126@gmail.com">yuluo</a>
 * @author xuguan
 * @since 2023.0.1.0
 */
@ConfigurationProperties(DashScopeChatProperties.CONFIG_PREFIX)
public class DashScopeChatProperties extends DashScopeParentProperties {

    /**
     * Spring AI Alibaba configuration prefix.
     */
    public static final String CONFIG_PREFIX = "spring.ai.dashscope.chat";

    /**
     * Default DashScope Chat model.
     */
    public static final String DEFAULT_CHAT_MODEL = ChatModel.QWEN_PLUS.getValue();

    /**
     * Enable DashScope ai chat client.
     */
    private boolean enabled = true;

    /**
     * DashScope Chat completions path.
     */
    private String completionsPath = DashScopeChatApiConstants.TEXT_GENERATION;

    private @Nullable String model = DEFAULT_CHAT_MODEL;

    private @Nullable Boolean stream;

    private @Nullable Double temperature;

    private @Nullable Double topP;

    private @Nullable Integer topK;

    private @Nullable Boolean enableThinking;

    private @Nullable Boolean preserveThinking;

    private @Nullable Integer thinkingBudget;

    private @Nullable String reasoningEffort;

    private @Nullable Boolean toolStream;

    private @Nullable Boolean enableCodeInterpreter;

    private @Nullable Double repetitionPenalty;

    private @Nullable Double presencePenalty;

    private @Nullable Boolean vlHighResolutionImages;

    private @Nullable Boolean vlEnableImageHwOutput;

    private @Nullable Integer maxCompletionTokens;

    private @Nullable Integer seed;

    private @Nullable Boolean incrementalOutput;

    private @Nullable ResponseFormat responseFormat;

    private @Nullable String resultFormat;

    private @Nullable Boolean logprobs;

    private @Nullable Integer topLogprobs;

    private @Nullable Integer n;

    private @Nullable Object stop;

    private @Nullable List<Tool> tools;

    private @Nullable Object toolChoice;

    private @Nullable Boolean parallelToolCalls;

    private @Nullable Boolean enableSearch;

    private @Nullable SearchOptions searchOptions;

    private @Nullable String dataInspection;

    private @Nullable List<Skill> skill;

    private @Nullable Boolean clearThinking;

    private @Nullable String thinkingDisplay;

    private @Nullable Map<String, Object> extraBody;

    private @Nullable Map<String, String> httpHeaders;

    private @Nullable Boolean multiModel;

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

    public @Nullable Boolean getEnableThinking() {
        return this.enableThinking;
    }

    public void setEnableThinking(@Nullable Boolean enableThinking) {
        this.enableThinking = enableThinking;
    }

    public @Nullable Boolean getPreserveThinking() {
        return this.preserveThinking;
    }

    public void setPreserveThinking(@Nullable Boolean preserveThinking) {
        this.preserveThinking = preserveThinking;
    }

    public @Nullable Integer getThinkingBudget() {
        return this.thinkingBudget;
    }

    public void setThinkingBudget(@Nullable Integer thinkingBudget) {
        this.thinkingBudget = thinkingBudget;
    }

    public @Nullable String getReasoningEffort() {
        return this.reasoningEffort;
    }

    public void setReasoningEffort(@Nullable String reasoningEffort) {
        this.reasoningEffort = reasoningEffort;
    }

    public @Nullable Boolean getToolStream() {
        return this.toolStream;
    }

    public void setToolStream(@Nullable Boolean toolStream) {
        this.toolStream = toolStream;
    }

    public @Nullable Boolean getEnableCodeInterpreter() {
        return this.enableCodeInterpreter;
    }

    public void setEnableCodeInterpreter(@Nullable Boolean enableCodeInterpreter) {
        this.enableCodeInterpreter = enableCodeInterpreter;
    }

    public @Nullable Double getRepetitionPenalty() {
        return this.repetitionPenalty;
    }

    public void setRepetitionPenalty(@Nullable Double repetitionPenalty) {
        this.repetitionPenalty = repetitionPenalty;
    }

    public @Nullable Double getPresencePenalty() {
        return this.presencePenalty;
    }

    public void setPresencePenalty(@Nullable Double presencePenalty) {
        this.presencePenalty = presencePenalty;
    }

    public @Nullable Boolean getVlHighResolutionImages() {
        return this.vlHighResolutionImages;
    }

    public void setVlHighResolutionImages(@Nullable Boolean vlHighResolutionImages) {
        this.vlHighResolutionImages = vlHighResolutionImages;
    }

    public @Nullable Boolean getVlEnableImageHwOutput() {
        return this.vlEnableImageHwOutput;
    }

    public void setVlEnableImageHwOutput(@Nullable Boolean vlEnableImageHwOutput) {
        this.vlEnableImageHwOutput = vlEnableImageHwOutput;
    }

    public @Nullable Integer getMaxTokens() {
        return this.maxCompletionTokens;
    }

    public void setMaxTokens(@Nullable Integer maxTokens) {
        this.maxCompletionTokens = maxTokens;
    }

    public @Nullable Integer getMaxCompletionTokens() {
        return this.maxCompletionTokens;
    }

    public void setMaxCompletionTokens(@Nullable Integer maxCompletionTokens) {
        this.maxCompletionTokens = maxCompletionTokens;
    }

    public @Nullable Integer getSeed() {
        return this.seed;
    }

    public void setSeed(@Nullable Integer seed) {
        this.seed = seed;
    }

    public @Nullable Boolean getIncrementalOutput() {
        return this.incrementalOutput;
    }

    public void setIncrementalOutput(@Nullable Boolean incrementalOutput) {
        this.incrementalOutput = incrementalOutput;
    }

    public @Nullable ResponseFormat getResponseFormat() {
        return this.responseFormat;
    }

    public void setResponseFormat(@Nullable ResponseFormat responseFormat) {
        this.responseFormat = responseFormat;
    }

    public @Nullable String getResultFormat() {
        return this.resultFormat;
    }

    public void setResultFormat(@Nullable String resultFormat) {
        this.resultFormat = resultFormat;
    }

    public @Nullable Boolean getLogprobs() {
        return this.logprobs;
    }

    public void setLogprobs(@Nullable Boolean logprobs) {
        this.logprobs = logprobs;
    }

    public @Nullable Integer getTopLogProbs() {
        return this.topLogprobs;
    }

    public void setTopLogProbs(@Nullable Integer topLogprobs) {
        this.topLogprobs = topLogprobs;
    }

    public @Nullable Integer getN() {
        return this.n;
    }

    public void setN(@Nullable Integer n) {
        this.n = n;
    }

    public @Nullable Object getStop() {
        return this.stop;
    }

    public void setStop(@Nullable Object stop) {
        this.stop = stop;
    }

    public @Nullable List<Tool> getTools() {
        return this.tools;
    }

    public void setTools(@Nullable List<Tool> tools) {
        this.tools = tools;
    }

    public @Nullable Object getToolChoice() {
        return this.toolChoice;
    }

    public void setToolChoice(@Nullable Object toolChoice) {
        this.toolChoice = toolChoice;
    }

    public @Nullable Boolean getParallelToolCalls() {
        return this.parallelToolCalls;
    }

    public void setParallelToolCalls(@Nullable Boolean parallelToolCalls) {
        this.parallelToolCalls = parallelToolCalls;
    }

    public @Nullable Boolean getEnableSearch() {
        return this.enableSearch;
    }

    public void setEnableSearch(@Nullable Boolean enableSearch) {
        this.enableSearch = enableSearch;
    }

    public @Nullable SearchOptions getSearchOptions() {
        return this.searchOptions;
    }

    public void setSearchOptions(@Nullable SearchOptions searchOptions) {
        this.searchOptions = searchOptions;
    }

    public @Nullable String getDataInspection() {
        return this.dataInspection;
    }

    public void setDataInspection(@Nullable String dataInspection) {
        this.dataInspection = dataInspection;
    }

    public @Nullable List<Skill> getSkill() {
        return this.skill;
    }

    public void setSkill(@Nullable List<Skill> skill) {
        this.skill = skill;
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

    public @Nullable Map<String, Object> getExtraBody() {
        return this.extraBody;
    }

    public void setExtraBody(@Nullable Map<String, Object> extraBody) {
        this.extraBody = extraBody;
    }

    public @Nullable Map<String, String> getHttpHeaders() {
        return this.httpHeaders;
    }

    public void setHttpHeaders(@Nullable Map<String, String> httpHeaders) {
        this.httpHeaders = httpHeaders;
    }

    public @Nullable Boolean getMultiModel() {
        return this.multiModel;
    }

    public void setMultiModel(@Nullable Boolean multiModel) {
        this.multiModel = multiModel;
    }

    public String getCompletionsPath() {
        return this.completionsPath;
    }

    public void setCompletionsPath(String completionsPath) {
        this.completionsPath = completionsPath;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public DashScopeChatOptions toOptions() {
        var builder = DashScopeChatOptions.builder()
                .model(this.model)
                .stream(this.stream)
                .temperature(this.temperature)
                .topP(this.topP)
                .topK(this.topK)
                .enableThinking(this.enableThinking)
                .preserveThinking(this.preserveThinking)
                .thinkingBudget(this.thinkingBudget)
                .reasoningEffort(this.reasoningEffort)
                .toolStream(this.toolStream)
                .enableCodeInterpreter(this.enableCodeInterpreter)
                .repetitionPenalty(this.repetitionPenalty)
                .presencePenalty(this.presencePenalty)
                .vlHighResolutionImages(this.vlHighResolutionImages)
                .vlEnableImageHwOutput(this.vlEnableImageHwOutput)
                .maxCompletionTokens(this.maxCompletionTokens)
                .seed(this.seed)
                .incrementalOutput(this.incrementalOutput)
                .responseFormat(this.responseFormat)
                .resultFormat(this.resultFormat)
                .logprobs(this.logprobs)
                .topLogprobs(this.topLogprobs)
                .n(this.n)
                .stop(this.stop)
                .tools(this.tools)
                .toolChoice(this.toolChoice)
                .parallelToolCalls(this.parallelToolCalls)
                .enableSearch(this.enableSearch)
                .searchOptions(this.searchOptions)
                .dataInspection(this.dataInspection)
                .skill(this.skill)
                .extraBody(this.extraBody)
                .httpHeaders(this.httpHeaders)
                .multiModel(this.multiModel);
        if (this.clearThinking != null) {
            builder.clearThinking(this.clearThinking);
        }
        if (this.thinkingDisplay != null) {
            builder.thinkingDisplay(this.thinkingDisplay);
        }
        return builder.build();
    }

    private Options options = new Options();

    @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX)
    @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
    public Options getOptions() {
        return this.options;
    }

    public void setOptions(Options options) {
        this.options = options;
    }

    public class Options {

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".model")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable String getModel() {
            return DashScopeChatProperties.this.getModel();
        }

        public void setModel(@Nullable String model) {
            DashScopeChatProperties.this.setModel(model);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".max-tokens")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable Integer getMaxTokens() {
            return DashScopeChatProperties.this.getMaxTokens();
        }

        public void setMaxTokens(@Nullable Integer maxTokens) {
            DashScopeChatProperties.this.setMaxTokens(maxTokens);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".max-completion-tokens")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable Integer getMaxCompletionTokens() {
            return DashScopeChatProperties.this.getMaxCompletionTokens();
        }

        public void setMaxCompletionTokens(@Nullable Integer maxCompletionTokens) {
            DashScopeChatProperties.this.setMaxCompletionTokens(maxCompletionTokens);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".stream")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable Boolean getStream() {
            return DashScopeChatProperties.this.getStream();
        }

        public void setStream(@Nullable Boolean stream) {
            DashScopeChatProperties.this.setStream(stream);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".temperature")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable Double getTemperature() {
            return DashScopeChatProperties.this.getTemperature();
        }

        public void setTemperature(@Nullable Double temperature) {
            DashScopeChatProperties.this.setTemperature(temperature);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".search-options")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable SearchOptions getSearchOptions() {
            return DashScopeChatProperties.this.getSearchOptions();
        }

        public void setSearchOptions(@Nullable SearchOptions searchOptions) {
            DashScopeChatProperties.this.setSearchOptions(searchOptions);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".parallel-tool-calls")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable Boolean getParallelToolCalls() {
            return DashScopeChatProperties.this.getParallelToolCalls();
        }

        public void setParallelToolCalls(@Nullable Boolean parallelToolCalls) {
            DashScopeChatProperties.this.setParallelToolCalls(parallelToolCalls);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".http-headers")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable Map<String, String> getHttpHeaders() {
            return DashScopeChatProperties.this.getHttpHeaders();
        }

        public void setHttpHeaders(@Nullable Map<String, String> httpHeaders) {
            DashScopeChatProperties.this.setHttpHeaders(httpHeaders);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".top-p")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable Double getTopP() {
            return DashScopeChatProperties.this.getTopP();
        }

        public void setTopP(@Nullable Double topP) {
            DashScopeChatProperties.this.setTopP(topP);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".top-k")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable Integer getTopK() {
            return DashScopeChatProperties.this.getTopK();
        }

        public void setTopK(@Nullable Integer topK) {
            DashScopeChatProperties.this.setTopK(topK);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".stop")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable Object getStop() {
            return DashScopeChatProperties.this.getStop();
        }

        public void setStop(@Nullable Object stop) {
            DashScopeChatProperties.this.setStop(stop);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".response-format")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable ResponseFormat getResponseFormat() {
            return DashScopeChatProperties.this.getResponseFormat();
        }

        public void setResponseFormat(@Nullable ResponseFormat responseFormat) {
            DashScopeChatProperties.this.setResponseFormat(responseFormat);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".result-format")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable String getResultFormat() {
            return DashScopeChatProperties.this.getResultFormat();
        }

        public void setResultFormat(@Nullable String resultFormat) {
            DashScopeChatProperties.this.setResultFormat(resultFormat);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".logprobs")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable Boolean getLogprobs() {
            return DashScopeChatProperties.this.getLogprobs();
        }

        public void setLogprobs(@Nullable Boolean logprobs) {
            DashScopeChatProperties.this.setLogprobs(logprobs);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".top-log-probs")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable Integer getTopLogProbs() {
            return DashScopeChatProperties.this.getTopLogProbs();
        }

        public void setTopLogProbs(@Nullable Integer topLogprobs) {
            DashScopeChatProperties.this.setTopLogProbs(topLogprobs);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".n")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable Integer getN() {
            return DashScopeChatProperties.this.getN();
        }

        public void setN(@Nullable Integer n) {
            DashScopeChatProperties.this.setN(n);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".thinking-budget")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable Integer getThinkingBudget() {
            return DashScopeChatProperties.this.getThinkingBudget();
        }

        public void setThinkingBudget(@Nullable Integer thinkingBudget) {
            DashScopeChatProperties.this.setThinkingBudget(thinkingBudget);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".enable-code-interpreter")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable Boolean getEnableCodeInterpreter() {
            return DashScopeChatProperties.this.getEnableCodeInterpreter();
        }

        public void setEnableCodeInterpreter(@Nullable Boolean enableCodeInterpreter) {
            DashScopeChatProperties.this.setEnableCodeInterpreter(enableCodeInterpreter);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".enable-search")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable Boolean getEnableSearch() {
            return DashScopeChatProperties.this.getEnableSearch();
        }

        public void setEnableSearch(@Nullable Boolean enableSearch) {
            DashScopeChatProperties.this.setEnableSearch(enableSearch);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".repetition-penalty")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable Double getRepetitionPenalty() {
            return DashScopeChatProperties.this.getRepetitionPenalty();
        }

        public void setRepetitionPenalty(@Nullable Double repetitionPenalty) {
            DashScopeChatProperties.this.setRepetitionPenalty(repetitionPenalty);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".presence-penalty")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable Double getPresencePenalty() {
            return DashScopeChatProperties.this.getPresencePenalty();
        }

        public void setPresencePenalty(@Nullable Double presencePenalty) {
            DashScopeChatProperties.this.setPresencePenalty(presencePenalty);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".preserve-thinking")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable Boolean getPreserveThinking() {
            return DashScopeChatProperties.this.getPreserveThinking();
        }

        public void setPreserveThinking(@Nullable Boolean preserveThinking) {
            DashScopeChatProperties.this.setPreserveThinking(preserveThinking);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".reasoning-effort")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable String getReasoningEffort() {
            return DashScopeChatProperties.this.getReasoningEffort();
        }

        public void setReasoningEffort(@Nullable String reasoningEffort) {
            DashScopeChatProperties.this.setReasoningEffort(reasoningEffort);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".tool-stream")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable Boolean getToolStream() {
            return DashScopeChatProperties.this.getToolStream();
        }

        public void setToolStream(@Nullable Boolean toolStream) {
            DashScopeChatProperties.this.setToolStream(toolStream);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".tools")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable List<Tool> getTools() {
            return DashScopeChatProperties.this.getTools();
        }

        public void setTools(@Nullable List<Tool> tools) {
            DashScopeChatProperties.this.setTools(tools);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".tool-choice")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable Object getToolChoice() {
            return DashScopeChatProperties.this.getToolChoice();
        }

        public void setToolChoice(@Nullable Object toolChoice) {
            DashScopeChatProperties.this.setToolChoice(toolChoice);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".seed")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable Integer getSeed() {
            return DashScopeChatProperties.this.getSeed();
        }

        public void setSeed(@Nullable Integer seed) {
            DashScopeChatProperties.this.setSeed(seed);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".incremental-output")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable Boolean getIncrementalOutput() {
            return DashScopeChatProperties.this.getIncrementalOutput();
        }

        public void setIncrementalOutput(@Nullable Boolean incrementalOutput) {
            DashScopeChatProperties.this.setIncrementalOutput(incrementalOutput);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".vl-high-resolution-images")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable Boolean getVlHighResolutionImages() {
            return DashScopeChatProperties.this.getVlHighResolutionImages();
        }

        public void setVlHighResolutionImages(@Nullable Boolean vlHighResolutionImages) {
            DashScopeChatProperties.this.setVlHighResolutionImages(vlHighResolutionImages);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".vl-enable-image-hw-output")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable Boolean getVlEnableImageHwOutput() {
            return DashScopeChatProperties.this.getVlEnableImageHwOutput();
        }

        public void setVlEnableImageHwOutput(@Nullable Boolean vlEnableImageHwOutput) {
            DashScopeChatProperties.this.setVlEnableImageHwOutput(vlEnableImageHwOutput);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".enable-thinking")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable Boolean getEnableThinking() {
            return DashScopeChatProperties.this.getEnableThinking();
        }

        public void setEnableThinking(@Nullable Boolean enableThinking) {
            DashScopeChatProperties.this.setEnableThinking(enableThinking);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".multi-model")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable Boolean getMultiModel() {
            return DashScopeChatProperties.this.getMultiModel();
        }

        public void setMultiModel(@Nullable Boolean multiModel) {
            DashScopeChatProperties.this.setMultiModel(multiModel);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".extra-body")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable Map<String, Object> getExtraBody() {
            return DashScopeChatProperties.this.getExtraBody();
        }

        public void setExtraBody(@Nullable Map<String, Object> extraBody) {
            DashScopeChatProperties.this.setExtraBody(extraBody);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".data-inspection")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable String getDataInspection() {
            return DashScopeChatProperties.this.getDataInspection();
        }

        public void setDataInspection(@Nullable String dataInspection) {
            DashScopeChatProperties.this.setDataInspection(dataInspection);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".skill")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable List<Skill> getSkill() {
            return DashScopeChatProperties.this.getSkill();
        }

        public void setSkill(@Nullable List<Skill> skill) {
            DashScopeChatProperties.this.setSkill(skill);
        }

    }

}
