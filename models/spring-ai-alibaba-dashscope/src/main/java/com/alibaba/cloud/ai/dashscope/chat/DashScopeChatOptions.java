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
import org.springframework.ai.model.tool.DefaultToolCallingChatOptions;
import org.springframework.ai.model.tool.ToolCallingChatOptions;
import org.springframework.ai.tool.ToolCallback;

/**
 * Options for the DashScope Chat API.
 *
 * @author nottyjay
 * @author guanxu
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DashScopeChatOptions implements ToolCallingChatOptions {

    /**
     * ID of the model to use.
     */
    @JsonProperty("model")
    private final @Nullable String model;

    /**
     * Options for streaming response. Included in the API only if streaming-mode completion is requested.
     */
    @JsonIgnore
    private final @Nullable Boolean stream;

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
    @JsonProperty("temperature")
    private final @Nullable Double temperature;

    /**
     * Random seed for generation, controlled by the user to affect reproducibility.
     * Seed supports unsigned 64‑bit integers. When a seed is provided, the model
     * will attempt to generate identical or similar results, though exact
     * reproducibility is not guaranteed.
     */
    @JsonProperty("seed")
    private final @Nullable Integer seed;

    /**
     * Nucleus (top-p) sampling threshold during generation. For example, with
     * top_p = 0.8, only tokens whose cumulative probability mass reaches at least
     * 0.8 are retained as candidates for sampling. Range: (0, 1.0), default: 0.8.
     * Higher values increase randomness; lower values increase determinism.
     * Note: do not set >= 1.0.
     */
    @JsonProperty("top_p")
    private final @Nullable Double topP;

    /**
     * Size of the sampling candidate pool (top-k). For example, top_k = 50 means
     * only the 50 highest-scoring tokens are considered for random sampling.
     * Larger values increase randomness; smaller values increase determinism.
     * Note: if top_k is null or > 100, top-k is disabled and only top-p applies.
     * Default is null (i.e., disabled).
     */
    @JsonProperty("top_k")
    private final @Nullable Integer topK;

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
    @JsonProperty("stop")
    private final @Nullable List<Object> stop;

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
    @JsonProperty("enable_search")
    private final @Nullable Boolean enableSearch;

    /**
     * Models can specify the format of the returned content. Valid values: {"type": "text"} or {"type": "json_object"}
     * {@link DashScopeResponseFormat}
     */
    @JsonProperty("response_format")
    private final @Nullable DashScopeResponseFormat responseFormat;

    /**
     * The maximum number of tokens to generate in the chat completion.
     * The total length of input tokens and generated tokens is limited by the model's context length.
     */
    @JsonProperty("max_tokens")
    private final @Nullable Integer maxTokens;

    /**
     * Controls whether to enable incremental output in streaming output mode, that is,
     * whether the subsequent output content includes the previously output content.
     * When set to true, the incremental output mode will be enabled, and the subsequent output
     * will not include the previously output content. You need to concatenate the overall output yourself.
     * When set to false, the subsequent output will include the previously output content.
     */
    @JsonProperty("incremental_output")
    private final Boolean incrementalOutput;

    /**
     * Used to control the repetition degree during model generation. Increasing the repetition_penalty
     * can reduce the repetition degree of the model generation. A value of 1.0 means no penalty. The default value is 1.1.
     */
    @JsonProperty("repetition_penalty")
    private final @Nullable Double repetitionPenalty;

    /**
     * A list of optional tools that the model can call. Currently, only functions are supported.
     * Even if multiple functions are input, the model will only select one of them to generate results.
     * The model can generate function call parameters based on the content of the tools parameter.
     */
    @JsonProperty("tools")
    private final @Nullable List<DashScopeApiSpec.FunctionTool> tools;

    /**
     * Strategies for networked search. Takes effect only if the enable_search is true.
     */
    @JsonProperty("search_options")
    private final DashScopeApiSpec.@Nullable SearchOptions searchOptions;

    /**
     * Whether to enable parallel tool calling.
     */
    @JsonProperty("parallel_tool_calls")
    private final @Nullable Boolean parallelToolCalls;

    /**
     * Optional HTTP headers to be added to the chat completion request.
     */
    @JsonIgnore
    private final Map<String, String> httpHeaders;

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
    private final @Nullable Object toolChoice;

    /**
     * this is to change token limitation to 16384 for vl model, only support for vl models
     * including qwen-vl-max, qwen-vl-max-0809, qwen-vl-plus-0809.
     */
    @JsonProperty("vl_high_resolution_images")
    private final @Nullable Boolean vlHighResolutionImages;

    /**
     * Whether to enable the thinking process of the model.
     */
    @JsonProperty("enable_thinking")
    private final @Nullable Boolean enableThinking;

    /**
     * The maximum length of the thinking process. Takes effect when enable_thinking is true,
     * and is suitable for Qwen3 full system model.
     */
    @JsonProperty("thinking_budget")
    private final @Nullable Integer thinkingBudget;

    /**
     * Whether to enable the code interpreter function.
     */
    @JsonProperty("enable_code_interpreter")
    private final @Nullable Boolean enableCodeInterpreter;

    /**
     * Collection of {@link ToolCallback}s to be used for tool calling in the chat completion requests.
     */
    @JsonIgnore
    private final List<ToolCallback> toolCallbacks;

    /**
     * Collection of tool names to be resolved at runtime and used for tool calling in the chat completion request.
     */
    @JsonIgnore
    private final Set<String> toolNames;

    /**
     * Whether to enable the tool execution lifecycle internally in ChatModel.
     */
    @JsonIgnore
    private final @Nullable Boolean internalToolExecutionEnabled;

    /**
     * Whether to use the DashScope multimodal generation endpoint.
     */
    @JsonProperty("multi_model")
    private final @Nullable Boolean multiModel;

    /**
     * Whether to enable the vision language model to output image height and width.
     */
    private final Boolean vlEnableImageHwOutput;

    /**
     * The tone color and format of the output audio are only applicable to the 'Qwen-Omni' model,
     * and the modalities parameter must be ["text","audio"].
     */
    @JsonProperty("audio")
    private final @Nullable Object audio;

    /**
     * The configuration item for streaming output that takes effect only when `stream` is true.
     */
    @JsonProperty("stream_options")
    private final @Nullable Object streamOptions;

    /**
     * The configuration item for 'Qwen-ASR' model.
     */
    @JsonProperty("asr_options")
    private final @Nullable Object asrOptions;

    /**
     * The maximum number of tokens to use for the input.
     */
    @JsonProperty("max_input_tokens")
    private final @Nullable Integer maxInputTokens;

    /**
     * The modalities of the output data, only support for 'Qwen-Omni' model.
     * <ul>
     *     <li>["text"](default): output text</li>
     *     <li>["text","audio"]: output text and audio</li>
     * </ul>
     */
    @JsonProperty("modalities")
    private final @Nullable List<String> modalities;

    /**
     * The configuration item for 'Qwen-OCR' model.
     */
    @JsonProperty("ocr_options")
    private final DashScopeApiSpec.@Nullable OCROption ocrOptions;

    /**
     * Specifies the number of candidate Tokens that return the maximum probability of the model at each generation step.
     * Value range: [0,5], Takes effect only if `logprobs` is true.
     */
    @JsonProperty("top_logprobs")
    private final @Nullable Integer topLogProbs;

    /**
     * Whether to return the logarithmic probability of the output Token, optional values:
     * true: return, false: not return.
     * The content generated during the thinking phase (reasoning_content) does not return a logarithmic probability.
     */
    @JsonProperty("logprobs")
    private final @Nullable Boolean logprobs;

    /**
     * The configuration item for 'Qwen-MT' model.
     */
    @JsonProperty("translation_options")
    private final DashScopeApiSpec.@Nullable TranslationOptions translationOptions;

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
    @JsonProperty("output_format")
    private final @Nullable String outputFormat;

    /**
     * The configured tool context.
     */
    @JsonIgnore
    private final Map<String, Object> toolContext;

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
    @JsonProperty("extra_body")
    private final @Nullable Map<String, Object> extraBody;

    protected DashScopeChatOptions(
            @Nullable String model,
            @Nullable Boolean stream,
            @Nullable Double temperature,
            @Nullable Integer seed,
            @Nullable Double topP,
            @Nullable Integer topK,
            @Nullable List<Object> stop,
            @Nullable Boolean enableSearch,
            @Nullable DashScopeResponseFormat responseFormat,
            @Nullable Integer maxTokens,
            @Nullable Boolean incrementalOutput,
            @Nullable Double repetitionPenalty,
            @Nullable List<DashScopeApiSpec.FunctionTool> tools,
            DashScopeApiSpec.@Nullable SearchOptions searchOptions,
            @Nullable Boolean parallelToolCalls,
            @Nullable Map<String, String> httpHeaders,
            @Nullable Object toolChoice,
            @Nullable Boolean vlHighResolutionImages,
            @Nullable Boolean enableThinking,
            @Nullable Integer thinkingBudget,
            @Nullable Boolean enableCodeInterpreter,
            @Nullable List<ToolCallback> toolCallbacks,
            @Nullable Set<String> toolNames,
            @Nullable Boolean internalToolExecutionEnabled,
            @Nullable Boolean multiModel,
            @Nullable Boolean vlEnableImageHwOutput,
            @Nullable Object audio,
            @Nullable Object streamOptions,
            @Nullable Object asrOptions,
            @Nullable Integer maxInputTokens,
            @Nullable List<String> modalities,
            DashScopeApiSpec.@Nullable OCROption ocrOptions,
            @Nullable Integer topLogProbs,
            @Nullable Boolean logprobs,
            DashScopeApiSpec.@Nullable TranslationOptions translationOptions,
            @Nullable String outputFormat,
            @Nullable Map<String, Object> toolContext,
            @Nullable Map<String, Object> extraBody) {
        this.model = model;
        this.stream = stream;
        this.temperature = temperature;
        this.seed = seed;
        this.topP = topP;
        this.topK = topK;
        this.stop = stop != null ? new ArrayList<>(stop) : null;
        this.enableSearch = enableSearch;
        this.responseFormat = responseFormat;
        this.maxTokens = maxTokens;
        this.incrementalOutput = incrementalOutput != null ? incrementalOutput : Boolean.TRUE;
        this.repetitionPenalty = repetitionPenalty;
        this.tools = tools != null ? new ArrayList<>(tools) : null;
        this.searchOptions = searchOptions;
        this.parallelToolCalls = parallelToolCalls;
        this.httpHeaders = httpHeaders != null ? new HashMap<>(httpHeaders) : new HashMap<>();
        this.toolChoice = toolChoice;
        this.vlHighResolutionImages = vlHighResolutionImages;
        this.enableThinking = enableThinking;
        this.thinkingBudget = thinkingBudget;
        this.enableCodeInterpreter = enableCodeInterpreter;
        this.toolCallbacks = toolCallbacks != null ? new ArrayList<>(toolCallbacks) : new ArrayList<>();
        this.toolNames = toolNames != null ? new HashSet<>(toolNames) : new HashSet<>();
        this.internalToolExecutionEnabled = internalToolExecutionEnabled;
        this.multiModel = multiModel;
        this.vlEnableImageHwOutput = vlEnableImageHwOutput != null ? vlEnableImageHwOutput : false;
        this.audio = audio;
        this.streamOptions = streamOptions;
        this.asrOptions = asrOptions;
        this.maxInputTokens = maxInputTokens;
        this.modalities = modalities != null ? new ArrayList<>(modalities) : null;
        this.ocrOptions = ocrOptions;
        this.topLogProbs = topLogProbs;
        this.logprobs = logprobs;
        this.translationOptions = translationOptions;
        this.outputFormat = outputFormat;
        this.toolContext = toolContext != null ? new HashMap<>(toolContext) : new HashMap<>();
        this.extraBody = extraBody != null ? new HashMap<>(extraBody) : null;
    }

    @Override
    public @Nullable String getModel() {
        return this.model;
    }

    @Override
    public @Nullable Double getFrequencyPenalty() {
        return null;
    }

    @Override
    public @Nullable Integer getMaxTokens() {
        return this.maxTokens;
    }

    @Override
    public @Nullable Double getPresencePenalty() {
        return null;
    }

    @Override
    public @Nullable List<String> getStopSequences() {
        return null;
    }

    public @Nullable Boolean getStream() {
        return this.stream;
    }

    @Override
    public @Nullable Double getTemperature() {
        return this.temperature;
    }

    public @Nullable Integer getSeed() {
        return this.seed;
    }

    @Override
    public @Nullable Double getTopP() {
        return this.topP;
    }

    @Override
    public @Nullable Integer getTopK() {
        return this.topK;
    }

    public @Nullable List<Object> getStop() {
        return this.stop;
    }

    public @Nullable Boolean getEnableSearch() {
        return this.enableSearch;
    }

    public @Nullable DashScopeResponseFormat getResponseFormat() {
        return this.responseFormat;
    }

    public Boolean getIncrementalOutput() {
        return this.incrementalOutput;
    }

    public @Nullable Double getRepetitionPenalty() {
        return this.repetitionPenalty;
    }

    public @Nullable List<DashScopeApiSpec.FunctionTool> getTools() {
        return this.tools;
    }

    public DashScopeApiSpec.@Nullable SearchOptions getSearchOptions() {
        return this.searchOptions;
    }

    public @Nullable Boolean getParallelToolCalls() {
        return this.parallelToolCalls;
    }

    public Map<String, String> getHttpHeaders() {
        return this.httpHeaders;
    }

    public @Nullable Object getToolChoice() {
        return this.toolChoice;
    }

    public @Nullable Boolean getVlHighResolutionImages() {
        return this.vlHighResolutionImages;
    }

    public @Nullable Boolean getEnableThinking() {
        return this.enableThinking;
    }

    public @Nullable Integer getThinkingBudget() {
        return this.thinkingBudget;
    }

    public @Nullable Boolean getEnableCodeInterpreter() {
        return this.enableCodeInterpreter;
    }

    @Override
    @JsonIgnore
    public List<ToolCallback> getToolCallbacks() {
        return this.toolCallbacks;
    }

    @Override
    @JsonIgnore
    public Set<String> getToolNames() {
        return this.toolNames;
    }

    @Override
    @JsonIgnore
    public @Nullable Boolean getInternalToolExecutionEnabled() {
        return this.internalToolExecutionEnabled;
    }

    public @Nullable Boolean getMultiModel() {
        return this.multiModel;
    }

    public Boolean getVlEnableImageHwOutput() {
        return this.vlEnableImageHwOutput;
    }

    public @Nullable Object getAudio() {
        return this.audio;
    }

    public @Nullable Object getStreamOptions() {
        return this.streamOptions;
    }

    public @Nullable Object getAsrOptions() {
        return this.asrOptions;
    }

    public @Nullable Integer getMaxInputTokens() {
        return this.maxInputTokens;
    }

    public @Nullable List<String> getModalities() {
        return this.modalities;
    }

    public DashScopeApiSpec.@Nullable OCROption getOcrOptions() {
        return this.ocrOptions;
    }

    public @Nullable Integer getTopLogProbs() {
        return this.topLogProbs;
    }

    public @Nullable Boolean getLogprobs() {
        return this.logprobs;
    }

    public DashScopeApiSpec.@Nullable TranslationOptions getTranslationOptions() {
        return this.translationOptions;
    }

    public @Nullable String getOutputFormat() {
        return this.outputFormat;
    }

    @Override
    public Map<String, Object> getToolContext() {
        return this.toolContext;
    }

    public @Nullable Map<String, Object> getExtraBody() {
        return this.extraBody;
    }

    @Override
    public DashScopeChatOptions copy() {
        return mutate().build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static DashScopeChatOptions fromOptions(DashScopeChatOptions fromOptions) {
        return fromOptions.mutate().build();
    }

    @Override
    public Builder mutate() {
        return builder().model(this.model)
                .stream(this.stream)
                .temperature(this.temperature)
                .seed(this.seed)
                .topP(this.topP)
                .topK(this.topK)
                .stop(this.stop)
                .enableSearch(this.enableSearch)
                .responseFormat(this.responseFormat)
                .maxTokens(this.maxTokens)
                .incrementalOutput(this.incrementalOutput)
                .repetitionPenalty(this.repetitionPenalty)
                .tools(this.tools)
                .searchOptions(this.searchOptions)
                .parallelToolCalls(this.parallelToolCalls)
                .httpHeaders(this.httpHeaders)
                .toolChoice(this.toolChoice)
                .vlHighResolutionImages(this.vlHighResolutionImages)
                .enableThinking(this.enableThinking)
                .thinkingBudget(this.thinkingBudget)
                .enableCodeInterpreter(this.enableCodeInterpreter)
                .toolCallbacks(this.toolCallbacks)
                .toolNames(this.toolNames)
                .internalToolExecutionEnabled(this.internalToolExecutionEnabled)
                .multiModel(this.multiModel)
                .vlEnableImageHwOutput(this.vlEnableImageHwOutput)
                .audio(this.audio)
                .streamOptions(this.streamOptions)
                .asrOptions(this.asrOptions)
                .maxInputTokens(this.maxInputTokens)
                .modalities(this.modalities)
                .ocrOptions(this.ocrOptions)
                .topLogProbs(this.topLogProbs)
                .logprobs(this.logprobs)
                .translationOptions(this.translationOptions)
                .outputFormat(this.outputFormat)
                .toolContext(this.toolContext)
                .extraBody(this.extraBody);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        DashScopeChatOptions that = (DashScopeChatOptions) o;
        return Objects.equals(this.model, that.model) && Objects.equals(this.stream, that.stream)
                && Objects.equals(this.temperature, that.temperature) && Objects.equals(this.seed, that.seed)
                && Objects.equals(this.topP, that.topP) && Objects.equals(this.topK, that.topK)
                && Objects.equals(this.stop, that.stop) && Objects.equals(this.enableSearch, that.enableSearch)
                && Objects.equals(this.responseFormat, that.responseFormat)
                && Objects.equals(this.maxTokens, that.maxTokens)
                && Objects.equals(this.incrementalOutput, that.incrementalOutput)
                && Objects.equals(this.repetitionPenalty, that.repetitionPenalty)
                && Objects.equals(this.tools, that.tools) && Objects.equals(this.toolChoice, that.toolChoice)
                && Objects.equals(this.searchOptions, that.searchOptions)
                && Objects.equals(this.parallelToolCalls, that.parallelToolCalls)
                && Objects.equals(this.httpHeaders, that.httpHeaders)
                && Objects.equals(this.vlHighResolutionImages, that.vlHighResolutionImages)
                && Objects.equals(this.enableThinking, that.enableThinking)
                && Objects.equals(this.thinkingBudget, that.thinkingBudget)
                && Objects.equals(this.enableCodeInterpreter, that.enableCodeInterpreter)
                && Objects.equals(this.toolCallbacks, that.toolCallbacks)
                && Objects.equals(this.toolNames, that.toolNames)
                && Objects.equals(this.internalToolExecutionEnabled, that.internalToolExecutionEnabled)
                && Objects.equals(this.multiModel, that.multiModel)
                && Objects.equals(this.vlEnableImageHwOutput, that.vlEnableImageHwOutput)
                && Objects.equals(this.audio, that.audio) && Objects.equals(this.streamOptions, that.streamOptions)
                && Objects.equals(this.asrOptions, that.asrOptions)
                && Objects.equals(this.maxInputTokens, that.maxInputTokens)
                && Objects.equals(this.modalities, that.modalities) && Objects.equals(this.ocrOptions, that.ocrOptions)
                && Objects.equals(this.topLogProbs, that.topLogProbs) && Objects.equals(this.logprobs, that.logprobs)
                && Objects.equals(this.translationOptions, that.translationOptions)
                && Objects.equals(this.outputFormat, that.outputFormat)
                && Objects.equals(this.toolContext, that.toolContext) && Objects.equals(this.extraBody, that.extraBody);
    }

    // @formatter:off
    @Override
    public int hashCode() {
        return Objects.hash(this.model, this.stream, this.temperature, this.seed, this.topP, this.topK, this.stop,
                this.enableSearch, this.responseFormat, this.maxTokens, this.incrementalOutput, this.repetitionPenalty,
                this.tools, this.toolChoice, this.searchOptions, this.parallelToolCalls, this.httpHeaders,
                this.vlHighResolutionImages, this.enableThinking, this.thinkingBudget, this.enableCodeInterpreter,
                this.toolCallbacks, this.toolNames, this.internalToolExecutionEnabled, this.multiModel,
                this.vlEnableImageHwOutput, this.audio, this.streamOptions, this.asrOptions, this.maxInputTokens,
                this.modalities, this.ocrOptions, this.topLogProbs, this.logprobs, this.translationOptions,
                this.outputFormat, this.toolContext, this.extraBody);
    }
    // @formatter:on

    @Override
    public String toString() {
        return "DashScopeChatOptions{" + "model='" + this.model + '\'' + ", stream=" + this.stream + ", temperature="
                + this.temperature + ", seed=" + this.seed + ", topP=" + this.topP + ", topK=" + this.topK + ", stop="
                + this.stop + ", enableSearch=" + this.enableSearch + ", responseFormat=" + this.responseFormat
                + ", maxTokens=" + this.maxTokens + ", incrementalOutput=" + this.incrementalOutput
                + ", repetitionPenalty=" + this.repetitionPenalty + ", tools=" + this.tools + ", toolChoice="
                + this.toolChoice + ", searchOptions=" + this.searchOptions + ", parallelToolCalls="
                + this.parallelToolCalls + ", vlHighResolutionImages=" + this.vlHighResolutionImages
                + ", enableThinking=" + this.enableThinking + ", thinkingBudget=" + this.thinkingBudget
                + ", enableCodeInterpreter=" + this.enableCodeInterpreter + ", multiModel=" + this.multiModel
                + ", vlEnableImageHwOutput=" + this.vlEnableImageHwOutput + ", audio=" + this.audio + ", streamOptions="
                + this.streamOptions + ", asrOptions=" + this.asrOptions + ", maxInputTokens=" + this.maxInputTokens
                + ", modalities=" + this.modalities + ", ocrOptions=" + this.ocrOptions + ", topLogProbs="
                + this.topLogProbs + ", logprobs=" + this.logprobs + ", translationOptions=" + this.translationOptions
                + ", outputFormat=" + this.outputFormat + ", extraBody=" + this.extraBody + ", httpHeaders="
                + this.httpHeaders + ", toolCallbacks=" + this.toolCallbacks + ", toolNames=" + this.toolNames
                + ", internalToolExecutionEnabled=" + this.internalToolExecutionEnabled + ", toolContext="
                + this.toolContext + '}';
    }

    public static class Builder extends AbstractBuilder<Builder> {

    }

    protected abstract static class AbstractBuilder<B extends AbstractBuilder<B>>
            extends DefaultToolCallingChatOptions.Builder<B> {

        protected @Nullable Boolean stream;

        protected @Nullable Integer seed;

        protected @Nullable List<Object> stop;

        protected @Nullable Boolean enableSearch;

        protected @Nullable DashScopeResponseFormat responseFormat;

        protected @Nullable Boolean incrementalOutput;

        protected @Nullable Double repetitionPenalty;

        protected @Nullable List<DashScopeApiSpec.FunctionTool> tools;

        protected DashScopeApiSpec.@Nullable SearchOptions searchOptions;

        protected @Nullable Boolean parallelToolCalls;

        protected Map<String, String> httpHeaders = new HashMap<>();

        protected @Nullable Object toolChoice;

        protected @Nullable Boolean vlHighResolutionImages;

        protected @Nullable Boolean enableThinking;

        protected @Nullable Integer thinkingBudget;

        protected @Nullable Boolean enableCodeInterpreter;

        protected @Nullable Boolean multiModel;

        protected @Nullable Boolean vlEnableImageHwOutput;

        protected @Nullable Object audio;

        protected @Nullable Object streamOptions;

        protected @Nullable Object asrOptions;

        protected @Nullable Integer maxInputTokens;

        protected @Nullable List<String> modalities;

        protected DashScopeApiSpec.@Nullable OCROption ocrOptions;

        protected @Nullable Integer topLogProbs;

        protected @Nullable Boolean logprobs;

        protected DashScopeApiSpec.@Nullable TranslationOptions translationOptions;

        protected @Nullable String outputFormat;

        protected @Nullable Map<String, Object> extraBody;

        @Override
        public B clone() {
            B copy = super.clone();
            copy.stop = this.stop != null ? new ArrayList<>(this.stop) : null;
            copy.tools = this.tools != null ? new ArrayList<>(this.tools) : null;
            if (this.httpHeaders != null && !this.httpHeaders.isEmpty()) {
                copy.httpHeaders = new HashMap<>(this.httpHeaders);
            }
            copy.modalities = this.modalities != null ? new ArrayList<>(this.modalities) : null;
            copy.extraBody = this.extraBody != null ? new HashMap<>(this.extraBody) : null;
            return copy;
        }

        public B stream(@Nullable Boolean stream) {
            this.stream = stream;
            return self();
        }

        public B seed(@Nullable Integer seed) {
            this.seed = seed;
            return self();
        }

        public B stop(@Nullable List<Object> stop) {
            this.stop = stop;
            return self();
        }

        public B enableSearch(@Nullable Boolean enableSearch) {
            this.enableSearch = enableSearch;
            return self();
        }

        public B responseFormat(@Nullable DashScopeResponseFormat responseFormat) {
            this.responseFormat = responseFormat;
            return self();
        }

        public B incrementalOutput(@Nullable Boolean incrementalOutput) {
            this.incrementalOutput = incrementalOutput;
            return self();
        }

        public B repetitionPenalty(@Nullable Double repetitionPenalty) {
            this.repetitionPenalty = repetitionPenalty;
            return self();
        }

        public B tools(@Nullable List<DashScopeApiSpec.FunctionTool> tools) {
            this.tools = tools;
            return self();
        }

        public B searchOptions(DashScopeApiSpec.@Nullable SearchOptions searchOptions) {
            this.searchOptions = searchOptions;
            return self();
        }

        public B parallelToolCalls(@Nullable Boolean parallelToolCalls) {
            this.parallelToolCalls = parallelToolCalls;
            return self();
        }

        public B httpHeaders(@Nullable Map<String, String> httpHeaders) {
            this.httpHeaders = httpHeaders != null ? new HashMap<>(httpHeaders) : new HashMap<>();
            return self();
        }

        public B toolChoice(@Nullable Object toolChoice) {
            this.toolChoice = toolChoice;
            return self();
        }

        public B vlHighResolutionImages(@Nullable Boolean vlHighResolutionImages) {
            this.vlHighResolutionImages = vlHighResolutionImages;
            return self();
        }

        public B enableThinking(@Nullable Boolean enableThinking) {
            if (enableThinking != null) {
                this.enableThinking = enableThinking;
            }
            return self();
        }

        public B thinkingBudget(@Nullable Integer thinkingBudget) {
            this.thinkingBudget = thinkingBudget;
            return self();
        }

        public B enableCodeInterpreter(@Nullable Boolean enableCodeInterpreter) {
            this.enableCodeInterpreter = enableCodeInterpreter;
            return self();
        }

        public B multiModel(@Nullable Boolean multiModel) {
            this.multiModel = multiModel;
            return self();
        }

        public B vlEnableImageHwOutput(@Nullable Boolean vlEnableImageHwOutput) {
            this.vlEnableImageHwOutput = vlEnableImageHwOutput;
            return self();
        }

        public B audio(@Nullable Object audio) {
            this.audio = audio;
            return self();
        }

        public B streamOptions(@Nullable Object streamOptions) {
            this.streamOptions = streamOptions;
            return self();
        }

        public B asrOptions(@Nullable Object asrOptions) {
            this.asrOptions = asrOptions;
            return self();
        }

        public B maxInputTokens(@Nullable Integer maxInputTokens) {
            this.maxInputTokens = maxInputTokens;
            return self();
        }

        public B modalities(@Nullable List<String> modalities) {
            this.modalities = modalities;
            return self();
        }

        public B ocrOptions(DashScopeApiSpec.@Nullable OCROption ocrOptions) {
            this.ocrOptions = ocrOptions;
            return self();
        }

        public B topLogProbs(@Nullable Integer topLogProbs) {
            this.topLogProbs = topLogProbs;
            return self();
        }

        public B logprobs(@Nullable Boolean logprobs) {
            this.logprobs = logprobs;
            return self();
        }

        public B translationOptions(DashScopeApiSpec.@Nullable TranslationOptions translationOptions) {
            this.translationOptions = translationOptions;
            return self();
        }

        public B outputFormat(@Nullable String outputFormat) {
            this.outputFormat = outputFormat;
            return self();
        }

        public B extraBody(@Nullable Map<String, Object> extraBody) {
            this.extraBody = extraBody;
            return self();
        }

        @Override
        public B toolCallbacks(@Nullable List<ToolCallback> toolCallbacks) {
            this.toolCallbacks = toolCallbacks;
            return self();
        }

        @Override
        public B toolCallbacks(ToolCallback... toolCallbacks) {
            if (this.toolCallbacks == null) {
                this.toolCallbacks = new ArrayList<>();
            }
            this.toolCallbacks.addAll(List.of(toolCallbacks));
            return self();
        }

        @Override
        public B toolNames(@Nullable Set<String> toolNames) {
            this.toolNames = toolNames;
            return self();
        }

        @Override
        public B toolNames(String... toolNames) {
            if (this.toolNames == null) {
                this.toolNames = new HashSet<>();
            }
            this.toolNames.addAll(Set.of(toolNames));
            return self();
        }

        @Override
        public B internalToolExecutionEnabled(@Nullable Boolean internalToolExecutionEnabled) {
            this.internalToolExecutionEnabled = internalToolExecutionEnabled;
            return self();
        }

        @Override
        public B toolContext(@Nullable Map<String, Object> context) {
            if (context != null) {
                if (this.toolContext == null) {
                    this.toolContext = new HashMap<>();
                }
                this.toolContext.putAll(context);
            } else {
                this.toolContext = null;
            }
            return self();
        }

        @Override
        public B toolContext(String key, Object value) {
            if (this.toolContext == null) {
                this.toolContext = new HashMap<>();
            }
            this.toolContext.put(key, value);
            return self();
        }

        @Override
        public B combineWith(ChatOptions.Builder<?> other) {
            super.combineWith(other);
            if (other instanceof AbstractBuilder<?> that) {
                if (that.stream != null) {
                    this.stream = that.stream;
                }
                if (that.seed != null) {
                    this.seed = that.seed;
                }
                if (that.stop != null) {
                    this.stop = new ArrayList<>(that.stop);
                }
                if (that.enableSearch != null) {
                    this.enableSearch = that.enableSearch;
                }
                if (that.responseFormat != null) {
                    this.responseFormat = that.responseFormat;
                }
                if (that.incrementalOutput != null) {
                    this.incrementalOutput = that.incrementalOutput;
                }
                if (that.repetitionPenalty != null) {
                    this.repetitionPenalty = that.repetitionPenalty;
                }
                if (that.tools != null) {
                    this.tools = that.tools;
                }
                if (that.searchOptions != null) {
                    this.searchOptions = that.searchOptions;
                }
                if (that.parallelToolCalls != null) {
                    this.parallelToolCalls = that.parallelToolCalls;
                }
                if (that.httpHeaders != null && !that.httpHeaders.isEmpty()) {
                    this.httpHeaders = that.httpHeaders;
                }
                if (that.toolChoice != null) {
                    this.toolChoice = that.toolChoice;
                }
                if (that.vlHighResolutionImages != null) {
                    this.vlHighResolutionImages = that.vlHighResolutionImages;
                }
                if (that.enableThinking != null) {
                    this.enableThinking = that.enableThinking;
                }
                if (that.thinkingBudget != null) {
                    this.thinkingBudget = that.thinkingBudget;
                }
                if (that.enableCodeInterpreter != null) {
                    this.enableCodeInterpreter = that.enableCodeInterpreter;
                }
                if (that.multiModel != null) {
                    this.multiModel = that.multiModel;
                }
                if (that.vlEnableImageHwOutput != null) {
                    this.vlEnableImageHwOutput = that.vlEnableImageHwOutput;
                }
                if (that.audio != null) {
                    this.audio = that.audio;
                }
                if (that.streamOptions != null) {
                    this.streamOptions = that.streamOptions;
                }
                if (that.asrOptions != null) {
                    this.asrOptions = that.asrOptions;
                }
                if (that.maxInputTokens != null) {
                    this.maxInputTokens = that.maxInputTokens;
                }
                if (that.modalities != null) {
                    this.modalities = that.modalities;
                }
                if (that.ocrOptions != null) {
                    this.ocrOptions = that.ocrOptions;
                }
                if (that.topLogProbs != null) {
                    this.topLogProbs = that.topLogProbs;
                }
                if (that.logprobs != null) {
                    this.logprobs = that.logprobs;
                }
                if (that.translationOptions != null) {
                    this.translationOptions = that.translationOptions;
                }
                if (that.outputFormat != null) {
                    this.outputFormat = that.outputFormat;
                }
                if (that.extraBody != null) {
                    this.extraBody = that.extraBody;
                }
            }
            return self();
        }

        // @formatter:off
        @Override
        public DashScopeChatOptions build() {
            return new DashScopeChatOptions(this.model, this.stream, this.temperature, this.seed, this.topP, this.topK,
                    this.stop, this.enableSearch, this.responseFormat, this.maxTokens, this.incrementalOutput,
                    this.repetitionPenalty, this.tools, this.searchOptions, this.parallelToolCalls, this.httpHeaders,
                    this.toolChoice, this.vlHighResolutionImages, this.enableThinking, this.thinkingBudget,
                    this.enableCodeInterpreter, this.toolCallbacks, this.toolNames, this.internalToolExecutionEnabled,
                    this.multiModel, this.vlEnableImageHwOutput, this.audio, this.streamOptions, this.asrOptions,
                    this.maxInputTokens, this.modalities, this.ocrOptions, this.topLogProbs, this.logprobs,
                    this.translationOptions, this.outputFormat, this.toolContext, this.extraBody);
        }
        // @formatter:on

    }

}
