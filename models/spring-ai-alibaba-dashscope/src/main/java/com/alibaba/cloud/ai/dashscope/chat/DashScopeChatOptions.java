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

package com.alibaba.cloud.ai.dashscope.chat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.alibaba.cloud.ai.dashscope.api.DashScopeResponseFormat;
import com.alibaba.cloud.ai.dashscope.spec.DashScopeApiSpec;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jspecify.annotations.Nullable;

import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.model.ModelOptionsUtils;
import org.springframework.ai.model.tool.ToolCallingChatOptions;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.util.Assert;

/**
 * Options for the DashScope Chat API.
 *
 * @author nottyjay
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DashScopeChatOptions implements ToolCallingChatOptions {

    /**
     * ID of the model to use.
     */
    @JsonProperty("model")
    private @Nullable String model;

    /**
     * Options for streaming response. Included in the API only if streaming-mode completion is requested.
     */
    @JsonIgnore
    private @Nullable Boolean stream;

    /**
     * Used to control the degree of randomness and diversity.
     * Specifically, the temperature value smooths the probability distribution
     * of each candidate token during text generation. Higher temperature values
     * lower the peak of the distribution—allowing more low-probability tokens
     * to be selected and producing more diverse outputs—while lower temperature
     * values increase the peak—making high-probability tokens more likely and
     * resulting in more deterministic outputs.
     * Range: [0, 2), system default: 0.85. Setting to 0 is not recommended.
     */
    private @Nullable @JsonProperty("temperature") Double temperature;

    /**
     * Random seed for generation, controlled by the user to affect reproducibility.
     * Seed supports unsigned 64‑bit integers. When a seed is provided, the model
     * will attempt to generate identical or similar results, though exact
     * reproducibility is not guaranteed.
     */
    private @Nullable @JsonProperty("seed") Integer seed;

    /**
     * Nucleus (top-p) sampling threshold during generation. For example, with
     * top_p = 0.8, only tokens whose cumulative probability mass reaches at least
     * 0.8 are retained as candidates for sampling. Range: (0, 1.0), default: 0.8.
     * Higher values increase randomness; lower values increase determinism.
     * Note: do not set >= 1.0.
     */
    private @Nullable @JsonProperty("top_p") Double topP;

    /**
     * Size of the sampling candidate pool (top-k). For example, top_k = 50 means
     * only the 50 highest-scoring tokens are considered for random sampling.
     * Larger values increase randomness; smaller values increase determinism.
     * Note: if top_k is null or > 100, top-k is disabled and only top-p applies.
     * Default is null (i.e., disabled).
     */
    private @Nullable @JsonProperty("top_k") Integer topK;

    /**
     * <ul>
     *   <li>The stop parameter is used to precisely control the content generation process.
     *   It automatically stops the generation when the content is about to include the specified string or token_ids,
     *   and the generated content does not include the specified content.
     *       <p>For example, if stop is set to "Hello", the generation will stop when "Hello" is about to be generated;
     *       if stop is set to [37763, 367], the generation will stop when "Observation" is about to be generated.
     *   <li>The stop parameter supports passing in an array of strings or an array of token_ids in list mode,
     *   and it supports scenarios where multiple stop conditions are used.
     * </ul>
     *
     * <q>Note: In list mode, strings and token_ids cannot be mixed. The element types in list mode must be the same.</q>
     */
    private @Nullable @JsonProperty("stop") List<Object> stop;

    /**
     * The model has a built - in internet search service. This parameter controls whether the model refers to
     * and uses internet search results when generating text. The possible values are as follows:
     *
     * <ul>
     *   <li>true: Enable internet search. The model will use the search results as reference information during
     *   the text generation process. However, the model will "decide on its own" whether to use the
     *   internet search results based on its internal logic.
     *   <li>false (default): Disable internet search.
     * </ul>
     */
    private @JsonProperty("enable_search") Boolean enableSearch;

    /**
     * Models can specify the format of the returned content. Valid values: {"type": "text"} or {"type": "json_object"}
     * {@link DashScopeResponseFormat}
     */
    private @Nullable @JsonProperty("response_format") DashScopeResponseFormat responseFormat;

    /**
     * The maximum number of tokens to generate in the chat completion.
     * The total length of input tokens and generated tokens is limited by the model's context length.
     */
    private @Nullable @JsonProperty("max_tokens") Integer maxTokens;

    /**
     * Controls whether to enable incremental output in streaming output mode, that is,
     * whether the subsequent output content includes the previously output content.
     * When set to true, the incremental output mode will be enabled, and the subsequent output
     * will not include the previously output content. You need to concatenate the overall output yourself.
     * When set to false, the subsequent output will include the previously output content.
     */
    private @JsonProperty("incremental_output") Boolean incrementalOutput = true;

    /**
     * Used to control the repetition degree during model generation. Increasing the repetition_penalty
     * can reduce the repetition degree of the model generation. A value of 1.0 means no penalty. The default value is 1.1.
     */
    private @Nullable @JsonProperty("repetition_penalty") Double repetitionPenalty;

    /**
     * A list of optional tools that the model can call. Currently, only functions are supported.
     * Even if multiple functions are input, the model will only select one of them to generate results.
     * The model can generate function call parameters based on the content of the tools parameter.
     */
    private @Nullable @JsonProperty("tools") List<DashScopeApiSpec.FunctionTool> tools;

    /**
     * Strategies for networked search. Takes effect only if the enable_search is true.
     */
    private @JsonProperty("search_options") DashScopeApiSpec.@Nullable SearchOptions searchOptions;

    /**
     * Whether to enable parallel tool calling。
     */
    private @Nullable @JsonProperty("parallel_tool_calls") Boolean parallelToolCalls;

    /**
     * Optional HTTP headers to be added to the chat completion request.
     */
    @JsonIgnore
    private Map<String, String> httpHeaders = new HashMap<>();

    /**
     * When using the tools parameter, it is used to control the model to call a specified tool.
     * There are three possible values:
     * "none" indicates not to call any tool. When the tools parameter is empty, the default value is "none".
     * "auto" indicates that the model decides whether to call a tool, which may or may not happen.
     * When the tools parameter is not empty, the default value is "auto".
     * An object structure can specify the model to call a specific tool. For example,
     * tool_choice={"type": "function", "function": {"name": "user_function"}}.
     */
    @JsonProperty("tool_choice")
    private @Nullable Object toolChoice;

    /**
     * this is to change token limitation to 16384 for vl model, only support for vl models
     * including qwen-vl-max、qwen-vl-max-0809、qwen-vl-plus-0809.
     */
    private @Nullable @JsonProperty("vl_high_resolution_images") Boolean vlHighResolutionImages;

    /**
     * Whether to enable the thinking process of the model.
     */
    private @JsonProperty("enable_thinking") Boolean enableThinking;

    /**
     * The maximum length of the thinking process takes effect when enable_thinking is true,
     * and is suitable for Qwen3 full system model.
     */
    private @Nullable @JsonProperty("thinking_budget") Integer thinkingBudget;

    /**
     * Whether to enable the code interpreter function.
     */
    private @Nullable @JsonProperty("enable_code_interpreter") Boolean enableCodeInterpreter;

    /**
     * Collection of {@link ToolCallback}s to be used for tool calling in the chat completion requests.
     */
    @JsonIgnore
    private List<ToolCallback> toolCallbacks = new ArrayList<>();

    /**
     * Collection of tool names to be resolved at runtime and used for tool calling in the chat completion requests.
     */
    @JsonIgnore
    private Set<String> toolNames = new HashSet<>();

    /**
     * Whether to enable the tool execution lifecycle internally in ChatModel.
     */
    @JsonIgnore
    private @Nullable Boolean internalToolExecutionEnabled;

    /**
     * Whether to use the DashScope multimodal generation endpoint.
     */
    private @Nullable @JsonProperty("multi_model") Boolean multiModel;

    /**
     * Whether to enable the vision language model to output image height and width.
     */
    private Boolean vlEnableImageHwOutput = false;

    /**
     * The tone color and format of the output audio are only applicable to the 'Qwen-Omni' model,
     * and the modalities parameters must be ["text","audio"]
     */
    private @Nullable @JsonProperty("audio") Object audio;

    /**
     * The configuration item for streaming output that takes effect only when `stream` is true.
     */
    private @Nullable @JsonProperty("stream_options") Object streamOptions;

    /**
     * The configuration item for 'Qwen-ASR' model.
     */
    private @Nullable @JsonProperty("asr_options") Object asrOptions;

    /**
     * The maximum number of tokens to use for the input.
     */
    private @Nullable @JsonProperty("max_input_tokens") Integer maxInputTokens;

    /**
     * The modalities of the output data, only support for 'Qwen-Omni' model.
     * <ul>
     *     <li>["text"](default): output text</li>
     *     <li>["text","audio"]: output text and audio</li>
     * </ul>
     */
    private @Nullable @JsonProperty("modalities") List<String> modalities;

    /**
     * The configuration item for 'Qwen-OCR' model.
     */
    private @JsonProperty("ocr_options") DashScopeApiSpec.@Nullable OCROption ocrOptions;

    /**
     * Specifies the number of candidate Tokens that return the maximum probability of the model at each generation step.
     * Value range: [0,5], Takes effect only if `logprobs` is true.
     */
    private @Nullable @JsonProperty("top_logprobs") Integer topLogProbs;

    /**
     * Whether to return the logarithmic probability of the output Token, optional values:
     * true: return, false: not return.
     * The content generated during the thinking phase (reasoning_content) does not return a logarithmic probability.
     */
    private @Nullable @JsonProperty("logprobs") Boolean logprobs;

    /**
     * The configuration item for 'Qwen-MT' model.
     */
    private @JsonProperty("translation_options") DashScopeApiSpec.@Nullable TranslationOptions translationOptions;

    /**
     * Specify the format and level of detail of the output study report for 'Qwen-Deep-Research' model.
     * <ul>
     *     <li>model_detailed_report(default): Generate a well-structured and detailed in-depth research report with
     *     a length of approximately 6000 Tokens, suitable for scenarios requiring comprehensive in-depth analysis
     *     </li>
     *     <li>model_summary_report: Generate a summary research report with prominent core ideas and refined content,
     *     about 1500-2000 tokens in length, suitable for quick understanding of key information and conclusions
     *     </li>
     * </ul>
     */
    private @Nullable @JsonProperty("output_format") String outputFormat;

    /**
     * The configured tool context.
     */
    @JsonIgnore
    private Map<String, Object> toolContext = new HashMap<>();

    /**
     * Additional parameters to pass to DashScope-compatible servers. Accepts any key-value pairs
     * that will be included at the top level of the JSON request.
     * <p>
     * Example:
     * <pre>{@code
     * DashScopeChatOptions.builder()
     *     .extraBody(Map.of("top_k", 50, "repetition_penalty", 1.1))
     *     .build()
     * }</pre>
     */
    private @Nullable @JsonProperty("extra_body") Map<String, Object> extraBody;

    public DashScopeApiSpec.@Nullable TranslationOptions getTranslationOptions() {
        return translationOptions;
    }

    public void setTranslationOptions(DashScopeApiSpec.@Nullable TranslationOptions translationOptions) {
        this.translationOptions = translationOptions;
    }

    public @Nullable String getOutputFormat() {
        return outputFormat;
    }

    public void setOutputFormat(@Nullable String outputFormat) {
        this.outputFormat = outputFormat;
    }

    public @Nullable Integer getTopLogProbs() {
        return topLogProbs;
    }

    public void setTopLogProbs(@Nullable Integer topLogProbs) {
        this.topLogProbs = topLogProbs;
    }

    public @Nullable Boolean getLogprobs() {
        return logprobs;
    }

    public void setLogprobs(@Nullable Boolean logprobs) {
        this.logprobs = logprobs;
    }

    public DashScopeApiSpec.@Nullable OCROption getOcrOptions() {
        return ocrOptions;
    }

    public void setOcrOptions(DashScopeApiSpec.@Nullable OCROption ocrOptions) {
        this.ocrOptions = ocrOptions;
    }

    public Boolean getVlEnableImageHwOutput() {
        return vlEnableImageHwOutput;
    }

    public void setVlEnableImageHwOutput(Boolean vlEnableImageHwOutput) {
        this.vlEnableImageHwOutput = vlEnableImageHwOutput;
    }

    public @Nullable Object getAudio() {
        return audio;
    }

    public void setAudio(@Nullable Object audio) {
        this.audio = audio;
    }

    public @Nullable Object getStreamOptions() {
        return streamOptions;
    }

    public void setStreamOptions(@Nullable Object streamOptions) {
        this.streamOptions = streamOptions;
    }

    public @Nullable Object getAsrOptions() {
        return asrOptions;
    }

    public void setAsrOptions(@Nullable Object asrOptions) {
        this.asrOptions = asrOptions;
    }

    public @Nullable Integer getMaxInputTokens() {
        return maxInputTokens;
    }

    public void setMaxInputTokens(@Nullable Integer maxInputTokens) {
        this.maxInputTokens = maxInputTokens;
    }

    public @Nullable List<String> getModalities() {
        return modalities;
    }

    public void setModalities(@Nullable List<String> modalities) {
        this.modalities = modalities;
    }

    @Override
    public @Nullable String getModel() {
        return model;
    }

    @Override
    public @Nullable Double getFrequencyPenalty() {
        return null;
    }

    @Override
    public @Nullable Integer getMaxTokens() {
        return maxTokens;
    }

    public void setMaxTokens(@Nullable Integer maxTokens) {
        this.maxTokens = maxTokens;
    }

    @Override
    public @Nullable Double getPresencePenalty() {
        return null;
    }

    @Override
    public @Nullable List<String> getStopSequences() {
        return null;
    }

    public void setModel(@Nullable String model) {
        this.model = model;
    }

    public @Nullable Boolean getStream() {
        return stream;
    }

    public void setStream(@Nullable Boolean stream) {
        this.stream = stream;
    }

    @Override
    public @Nullable Double getTemperature() {
        return this.temperature;
    }

    public void setTemperature(@Nullable Double temperature) {
        this.temperature = temperature;
    }

    public void setSearchOptions(DashScopeApiSpec.@Nullable SearchOptions searchOptions) {
        this.searchOptions = searchOptions;
    }

    public DashScopeApiSpec.@Nullable SearchOptions getSearchOptions() {
        return searchOptions;
    }

    public @Nullable Boolean getParallelToolCalls() {
        return parallelToolCalls;
    }

    public void setParallelToolCalls(@Nullable Boolean parallelToolCalls) {
        this.parallelToolCalls = parallelToolCalls;
    }

    public void setHttpHeaders(Map<String, String> httpHeaders) {
        this.httpHeaders = httpHeaders;
    }

    public Map<String, String> getHttpHeaders() {
        return httpHeaders;
    }

    @Override
    public @Nullable Double getTopP() {
        return this.topP;
    }

    @Override
    public ChatOptions copy() {
        return DashScopeChatOptions.fromOptions(this);
    }

    public void setTopP(@Nullable Double topP) {
        this.topP = topP;
    }

    @Override
    public @Nullable Integer getTopK() {
        return this.topK;
    }

    public void setTopK(@Nullable Integer topK) {
        this.topK = topK;
    }

    public @Nullable List<Object> getStop() {
        return stop;
    }

    public void setStop(@Nullable List<Object> stop) {
        this.stop = stop;
    }

    public @Nullable DashScopeResponseFormat getResponseFormat() {

        return responseFormat;
    }

    public @Nullable Integer getThinkingBudget() {
        return thinkingBudget;
    }

    public void setThinkingBudget(@Nullable Integer thinkingBudget) {
        this.thinkingBudget = thinkingBudget;
    }

    public @Nullable Boolean getEnableCodeInterpreter() {
        return this.enableCodeInterpreter;
    }

    public void setEnableCodeInterpreter(@Nullable Boolean enableCodeInterpreter) {
        this.enableCodeInterpreter = enableCodeInterpreter;
    }

    public void setResponseFormat(@Nullable DashScopeResponseFormat responseFormat) {

        this.responseFormat = responseFormat;
    }

    public Boolean getEnableSearch() {
        return enableSearch;
    }

    public void setEnableSearch(Boolean enableSearch) {
        this.enableSearch = enableSearch;
    }

    public @Nullable Double getRepetitionPenalty() {
        return repetitionPenalty;
    }

    public void setRepetitionPenalty(@Nullable Double repetitionPenalty) {
        this.repetitionPenalty = repetitionPenalty;
    }

    public @Nullable List<DashScopeApiSpec.FunctionTool> getTools() {
        return tools;
    }

    public void setTools(@Nullable List<DashScopeApiSpec.FunctionTool> tools) {
        this.tools = tools;
    }

    public @Nullable Object getToolChoice() {
        return toolChoice;
    }

    public void setToolChoice(@Nullable Object toolChoice) {
        this.toolChoice = toolChoice;
    }

    public @Nullable Integer getSeed() {
        return seed;
    }

    public void setSeed(@Nullable Integer seed) {
        this.seed = seed;
    }

    @Override
    @JsonIgnore
    public List<ToolCallback> getToolCallbacks() {
        return this.toolCallbacks;
    }

    @Override
    @JsonIgnore
    public void setToolCallbacks(List<ToolCallback> toolCallbacks) {
        Assert.notNull(toolCallbacks, "toolCallbacks cannot be null");
        Assert.noNullElements(toolCallbacks, "toolCallbacks cannot contain null elements");
        this.toolCallbacks = toolCallbacks;
    }

    @Override
    @JsonIgnore
    public Set<String> getToolNames() {
        return this.toolNames;
    }

    @Override
    @JsonIgnore
    public void setToolNames(Set<String> toolNames) {
        Assert.notNull(toolNames, "toolNames cannot be null");
        Assert.noNullElements(toolNames, "toolNames cannot contain null elements");
        toolNames.forEach(tool -> Assert.hasText(tool, "toolNames cannot contain empty elements"));
        this.toolNames = toolNames;
    }

    @Override
    @JsonIgnore
    public @Nullable Boolean getInternalToolExecutionEnabled() {
        return this.internalToolExecutionEnabled;
    }

    @Override
    @JsonIgnore
    public void setInternalToolExecutionEnabled(@Nullable Boolean internalToolExecutionEnabled) {
        this.internalToolExecutionEnabled = internalToolExecutionEnabled;
    }

    @Override
    public Map<String, Object> getToolContext() {
        return this.toolContext;
    }

    @Override
    public void setToolContext(Map<String, Object> toolContext) {
        this.toolContext = toolContext;
    }

    public Boolean getIncrementalOutput() {
        return incrementalOutput;
    }

    public void setIncrementalOutput(Boolean incrementalOutput) {
        this.incrementalOutput = incrementalOutput;
    }

    public @Nullable Boolean getVlHighResolutionImages() {
        return vlHighResolutionImages;
    }

    public void setVlHighResolutionImages(@Nullable Boolean vlHighResolutionImages) {
        this.vlHighResolutionImages = vlHighResolutionImages;
    }

    public Boolean getEnableThinking() {
        return enableThinking;
    }

    public void setEnableThinking(Boolean enableThinking) {
        this.enableThinking = enableThinking;
    }

    public @Nullable Boolean getMultiModel() {
        return multiModel;
    }

    public void setMultiModel(@Nullable Boolean multiModel) {
        this.multiModel = multiModel;
    }

    public @Nullable Map<String, Object> getExtraBody() {
        return this.extraBody;
    }

    public void setExtraBody(@Nullable Map<String, Object> extraBody) {
        this.extraBody = extraBody;
    }

    public static DashScopeChatOptionsBuilder builder() {
        return new DashScopeChatOptionsBuilder();
    }

    public static class DashScopeChatOptionsBuilder {

        private final DashScopeChatOptions options;

        public DashScopeChatOptionsBuilder() {
            this.options = new DashScopeChatOptions();
        }

        public DashScopeChatOptionsBuilder model(@Nullable String model) {
            this.options.model = model;
            return this;
        }

        public DashScopeChatOptionsBuilder searchOptions(DashScopeApiSpec.@Nullable SearchOptions searchOptions) {
            this.options.searchOptions = searchOptions;
            return this;
        }

        public DashScopeChatOptionsBuilder parallelToolCalls(@Nullable Boolean parallelToolCalls) {
            this.options.parallelToolCalls = parallelToolCalls;
            return this;
        }

        public DashScopeChatOptionsBuilder httpHeaders(Map<String, String> httpHeaders) {
            this.options.httpHeaders = httpHeaders;
            return this;
        }

        public DashScopeChatOptionsBuilder maxToken(@Nullable Integer maxTokens) {
            this.options.maxTokens = maxTokens;
            return this;
        }

        public DashScopeChatOptionsBuilder temperature(@Nullable Double temperature) {
            this.options.temperature = temperature;
            return this;
        }

        public DashScopeChatOptionsBuilder topP(@Nullable Double topP) {
            this.options.topP = topP;
            return this;
        }

        public DashScopeChatOptionsBuilder topK(@Nullable Integer topK) {
            this.options.topK = topK;
            return this;
        }

        public DashScopeChatOptionsBuilder stop(@Nullable List<Object> stop) {
            this.options.stop = stop;
            return this;
        }

        public DashScopeChatOptionsBuilder responseFormat(@Nullable DashScopeResponseFormat responseFormat) {
            this.options.responseFormat = responseFormat;
            return this;
        }

        public DashScopeChatOptionsBuilder enableSearch(@Nullable Boolean enableSearch) {
            if (enableSearch != null) {
                this.options.enableSearch = enableSearch;
            }
            return this;
        }

        public DashScopeChatOptionsBuilder repetitionPenalty(@Nullable Double repetitionPenalty) {
            this.options.repetitionPenalty = repetitionPenalty;
            return this;
        }

        public DashScopeChatOptionsBuilder tools(@Nullable List<DashScopeApiSpec.FunctionTool> tools) {
            this.options.tools = tools;
            return this;
        }

        public DashScopeChatOptionsBuilder toolChoice(@Nullable Object toolChoice) {
            this.options.toolChoice = toolChoice;
            return this;
        }

        public DashScopeChatOptionsBuilder stream(@Nullable Boolean stream) {
            this.options.stream = stream;
            return this;
        }

        public DashScopeChatOptionsBuilder toolCallbacks(List<ToolCallback> toolCallbacks) {
            Assert.notNull(toolCallbacks, "toolCallbacks cannot be null");
            Assert.noNullElements(toolCallbacks, "toolCallbacks cannot contain null elements");
            this.options.toolCallbacks = toolCallbacks;
            return this;
        }

        public DashScopeChatOptionsBuilder toolNames(Set<String> toolNames) {
            Assert.notNull(toolNames, "toolNames cannot be null");
            Assert.noNullElements(toolNames, "toolNames cannot contain null elements");
            toolNames.forEach(tool -> Assert.hasText(tool, "toolNames cannot contain empty elements"));
            this.options.toolNames = toolNames;
            return this;
        }

        public DashScopeChatOptionsBuilder toolName(String toolName) {
            Assert.hasText(toolName, "Tool name must not be empty");
            this.options.toolNames.add(toolName);
            return this;
        }

        public DashScopeChatOptionsBuilder internalToolExecutionEnabled(
                @Nullable Boolean internalToolExecutionEnabled) {
            this.options.internalToolExecutionEnabled = internalToolExecutionEnabled;
            return this;
        }

        public DashScopeChatOptionsBuilder seed(@Nullable Integer seed) {
            this.options.seed = seed;
            return this;
        }

        public DashScopeChatOptionsBuilder incrementalOutput(@Nullable Boolean incrementalOutput) {
            if (incrementalOutput != null) {
                this.options.incrementalOutput = incrementalOutput;
            }
            return this;
        }

        public DashScopeChatOptionsBuilder toolContext(Map<String, Object> toolContext) {
            if (this.options.toolContext == null) {
                this.options.toolContext = toolContext;
            } else {
                this.options.toolContext.putAll(toolContext);
            }
            return this;
        }

        public DashScopeChatOptionsBuilder vlHighResolutionImages(@Nullable Boolean vlHighResolutionImages) {
            this.options.vlHighResolutionImages = vlHighResolutionImages;
            return this;
        }

        public DashScopeChatOptionsBuilder enableThinking(@Nullable Boolean enableThinking) {
            if (enableThinking != null) {
                this.options.enableThinking = enableThinking;
            }
            return this;
        }

        public DashScopeChatOptionsBuilder multiModel(@Nullable Boolean multiModel) {
            this.options.multiModel = multiModel;
            return this;
        }

        public DashScopeChatOptionsBuilder thinkingBudget(@Nullable Integer thinkingBudget) {
            this.options.thinkingBudget = thinkingBudget;
            return this;
        }

        public DashScopeChatOptionsBuilder enableCodeInterpreter(@Nullable Boolean enableCodeInterpreter) {
            this.options.enableCodeInterpreter = enableCodeInterpreter;
            return this;
        }

        public DashScopeChatOptionsBuilder modalities(@Nullable List<String> modalities) {
            this.options.modalities = modalities;
            return this;
        }

        public DashScopeChatOptionsBuilder audio(@Nullable Object audio) {
            this.options.audio = audio;
            return this;
        }

        public DashScopeChatOptionsBuilder streamOptions(@Nullable Object streamOptions) {
            this.options.streamOptions = streamOptions;
            return this;
        }

        public DashScopeChatOptionsBuilder extraBody(@Nullable Map<String, Object> extraBody) {
            this.options.extraBody = extraBody;
            return this;
        }

        public DashScopeChatOptions build() {
            return this.options;
        }
    }

    public static DashScopeChatOptions fromOptions(DashScopeChatOptions fromOptions) {

        return DashScopeChatOptions.builder()
                .model(fromOptions.model)
                .temperature(fromOptions.temperature)
                .maxToken(fromOptions.maxTokens)
                .topP(fromOptions.topP)
                .topK(fromOptions.topK)
                .seed(fromOptions.seed)
                .stop(fromOptions.stop)
                .responseFormat(fromOptions.responseFormat)
                .stream(fromOptions.stream)
                .enableSearch(fromOptions.enableSearch)
                .enableCodeInterpreter(fromOptions.enableCodeInterpreter)
                .incrementalOutput(fromOptions.incrementalOutput)
                .toolCallbacks(fromOptions.toolCallbacks)
                .toolNames(fromOptions.toolNames)
                .internalToolExecutionEnabled(fromOptions.internalToolExecutionEnabled)
                .repetitionPenalty(fromOptions.repetitionPenalty)
                .tools(fromOptions.tools)
                .toolContext(fromOptions.toolContext)
                .multiModel(fromOptions.multiModel)
                .vlHighResolutionImages(fromOptions.vlHighResolutionImages)
                .enableThinking(fromOptions.enableThinking)
                .thinkingBudget(fromOptions.thinkingBudget)
                .parallelToolCalls(fromOptions.parallelToolCalls)
                .searchOptions(fromOptions.searchOptions)
                .httpHeaders(fromOptions.httpHeaders)
                .modalities(fromOptions.modalities)
                .audio(fromOptions.audio)
                .streamOptions(fromOptions.streamOptions)
                .extraBody(fromOptions.extraBody)
                .build();
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
        DashScopeChatOptions that = (DashScopeChatOptions) o;

        return Objects.equals(this.model, that.model) && Objects.equals(this.stream, that.stream)
                && Objects.equals(this.temperature, that.temperature) && Objects.equals(this.seed, that.seed)
                && Objects.equals(this.topP, that.topP) && Objects.equals(this.topK, that.topK)
                && Objects.equals(this.stop, that.stop) && Objects.equals(this.enableSearch, that.enableSearch)
                && Objects.equals(this.enableCodeInterpreter, that.enableCodeInterpreter)
                && Objects.equals(this.responseFormat, that.responseFormat)
                && Objects.equals(this.incrementalOutput, that.incrementalOutput)
                && Objects.equals(this.repetitionPenalty, that.repetitionPenalty)
                && Objects.equals(this.tools, that.tools) && Objects.equals(this.toolChoice, that.toolChoice)
                && Objects.equals(this.vlHighResolutionImages, that.vlHighResolutionImages)
                && Objects.equals(this.enableThinking, that.enableThinking)
                && Objects.equals(this.thinkingBudget, that.thinkingBudget)
                && Objects.equals(this.toolCallbacks, that.toolCallbacks)
                && Objects.equals(this.toolNames, that.toolNames)
                && Objects.equals(this.internalToolExecutionEnabled, that.internalToolExecutionEnabled)
                && Objects.equals(this.multiModel, that.multiModel)
                && Objects.equals(this.searchOptions, that.searchOptions)
                && Objects.equals(this.parallelToolCalls, that.parallelToolCalls)
                && Objects.equals(this.httpHeaders, that.httpHeaders)
                && Objects.equals(this.toolContext, that.toolContext)
                && Objects.equals(this.modalities, that.modalities) && Objects.equals(this.audio, that.audio)
                && Objects.equals(this.streamOptions, that.streamOptions)
                && Objects.equals(this.extraBody, that.extraBody);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.model, this.stream, this.temperature, this.seed, this.topP, this.topK, this.stop,
                this.enableSearch, this.enableCodeInterpreter, this.responseFormat, this.incrementalOutput,
                this.repetitionPenalty, this.tools, this.toolChoice, this.vlHighResolutionImages,
                this.enableThinking, this.thinkingBudget, this.toolCallbacks, this.toolNames,
                this.internalToolExecutionEnabled, this.multiModel, this.searchOptions, this.parallelToolCalls,
                this.httpHeaders, this.toolContext, this.modalities, this.audio, this.streamOptions,
                this.extraBody);
    }

    @Override
    public String toString() {

        return "DashScopeChatOptions: " + ModelOptionsUtils.toJsonString(this);
    }

}
