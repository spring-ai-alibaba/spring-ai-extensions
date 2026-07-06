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
import org.springframework.ai.tool.ToolCallback;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.DeprecatedConfigurationProperty;

/**
 * @author yuluo
 * @author <a href="mailto:yuluo08290126@gmail.com">yuluo</a>
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
	private DashScopeChatOptions options = DashScopeChatOptions.builder()
		.model(DEFAULT_CHAT_MODEL)
		.build();
	private final Options legacyOptions = new Options();

	public DashScopeChatOptions toOptions() {
		if (this.options == null) {
			this.options = DashScopeChatOptions.builder().model(DEFAULT_CHAT_MODEL).build();
		}
		return this.options;
	}

	@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX)
	@Deprecated(since = "2.0.0", forRemoval = true)
	public Options getOptions() {
		return this.legacyOptions;
	}

	public void setOptions(Options options) {
		// Deprecated options are applied by the nested Options setters.
	}

	private void updateOptions(java.util.function.Consumer<DashScopeChatOptions.Builder> customizer) {
		DashScopeChatOptions.Builder builder = toOptions().mutate();
		customizer.accept(builder);
		this.options = builder.build();
	}

	public @Nullable String getModel() {
		return toOptions().getModel();
	}

	public void setModel(String model) {
		updateOptions(builder -> builder.model(model));
	}

	public @Nullable Integer getMaxTokens() {
		return toOptions().getMaxTokens();
	}

	public void setMaxTokens(Integer maxTokens) {
		updateOptions(builder -> builder.maxTokens(maxTokens));
	}

	public @Nullable Integer getMaxCompletionTokens() {
		return toOptions().getMaxCompletionTokens();
	}

	public void setMaxCompletionTokens(Integer maxCompletionTokens) {
		updateOptions(builder -> builder.maxCompletionTokens(maxCompletionTokens));
	}

	public @Nullable Boolean getStream() {
		return toOptions().getStream();
	}

	public void setStream(Boolean stream) {
		updateOptions(builder -> builder.stream(stream));
	}

	public @Nullable Double getTemperature() {
		return toOptions().getTemperature();
	}

	public void setTemperature(Double temperature) {
		updateOptions(builder -> builder.temperature(temperature));
	}

	public @Nullable SearchOptions getSearchOptions() {
		return toOptions().getSearchOptions();
	}

	public void setSearchOptions(SearchOptions searchOptions) {
		updateOptions(builder -> builder.searchOptions(searchOptions));
	}

	public @Nullable Boolean getParallelToolCalls() {
		return toOptions().getParallelToolCalls();
	}

	public void setParallelToolCalls(Boolean parallelToolCalls) {
		updateOptions(builder -> builder.parallelToolCalls(parallelToolCalls));
	}

	public @Nullable Map<String, String> getHttpHeaders() {
		return toOptions().getHttpHeaders();
	}

	public void setHttpHeaders(Map<String, String> httpHeaders) {
		updateOptions(builder -> builder.httpHeaders(httpHeaders));
	}

	public @Nullable Double getTopP() {
		return toOptions().getTopP();
	}

	public void setTopP(Double topP) {
		updateOptions(builder -> builder.topP(topP));
	}

	public @Nullable Integer getTopK() {
		return toOptions().getTopK();
	}

	public void setTopK(Integer topK) {
		updateOptions(builder -> builder.topK(topK));
	}

	public @Nullable Object getStop() {
		return toOptions().getStop();
	}

	public void setStop(Object stop) {
		updateOptions(builder -> builder.stop(stop));
	}

	public @Nullable ResponseFormat getResponseFormat() {
		return toOptions().getResponseFormat();
	}

	public void setResponseFormat(ResponseFormat responseFormat) {
		updateOptions(builder -> builder.responseFormat(responseFormat));
	}

	public @Nullable String getResultFormat() {
		return toOptions().getResultFormat();
	}

	public void setResultFormat(String resultFormat) {
		updateOptions(builder -> builder.resultFormat(resultFormat));
	}

	public @Nullable Boolean getLogprobs() {
		return toOptions().getLogprobs();
	}

	public void setLogprobs(Boolean logprobs) {
		updateOptions(builder -> builder.logprobs(logprobs));
	}

	public @Nullable Integer getTopLogProbs() {
		return toOptions().getTopLogProbs();
	}

	public void setTopLogProbs(Integer topLogProbs) {
		updateOptions(builder -> builder.topLogprobs(topLogProbs));
	}

	public @Nullable Integer getN() {
		return toOptions().getN();
	}

	public void setN(Integer n) {
		updateOptions(builder -> builder.n(n));
	}

	public @Nullable Integer getThinkingBudget() {
		return toOptions().getThinkingBudget();
	}

	public void setThinkingBudget(Integer thinkingBudget) {
		updateOptions(builder -> builder.thinkingBudget(thinkingBudget));
	}

	public @Nullable Boolean getEnableCodeInterpreter() {
		return toOptions().getEnableCodeInterpreter();
	}

	public void setEnableCodeInterpreter(Boolean enableCodeInterpreter) {
		updateOptions(builder -> builder.enableCodeInterpreter(enableCodeInterpreter));
	}

	public @Nullable Boolean getEnableSearch() {
		return toOptions().getEnableSearch();
	}

	public void setEnableSearch(Boolean enableSearch) {
		updateOptions(builder -> builder.enableSearch(enableSearch));
	}

	public @Nullable Double getRepetitionPenalty() {
		return toOptions().getRepetitionPenalty();
	}

	public void setRepetitionPenalty(Double repetitionPenalty) {
		updateOptions(builder -> builder.repetitionPenalty(repetitionPenalty));
	}

	public @Nullable Double getPresencePenalty() {
		return toOptions().getPresencePenalty();
	}

	public void setPresencePenalty(Double presencePenalty) {
		updateOptions(builder -> builder.presencePenalty(presencePenalty));
	}

	public @Nullable Boolean getPreserveThinking() {
		return toOptions().getPreserveThinking();
	}

	public void setPreserveThinking(Boolean preserveThinking) {
		updateOptions(builder -> builder.preserveThinking(preserveThinking));
	}

	public @Nullable String getReasoningEffort() {
		return toOptions().getReasoningEffort();
	}

	public void setReasoningEffort(String reasoningEffort) {
		updateOptions(builder -> builder.reasoningEffort(reasoningEffort));
	}

	public @Nullable Boolean getToolStream() {
		return toOptions().getToolStream();
	}

	public void setToolStream(Boolean toolStream) {
		updateOptions(builder -> builder.toolStream(toolStream));
	}

	public @Nullable List<Tool> getTools() {
		return toOptions().getTools();
	}

	public void setTools(List<Tool> tools) {
		updateOptions(builder -> builder.tools(tools));
	}

	public @Nullable Object getToolChoice() {
		return toOptions().getToolChoice();
	}

	public void setToolChoice(Object toolChoice) {
		updateOptions(builder -> builder.toolChoice(toolChoice));
	}

	public @Nullable Integer getSeed() {
		return toOptions().getSeed();
	}

	public void setSeed(Integer seed) {
		updateOptions(builder -> builder.seed(seed));
	}

	public @Nullable List<ToolCallback> getToolCallbacks() {
		return toOptions().getToolCallbacks();
	}

	public void setToolCallbacks(List<ToolCallback> toolCallbacks) {
		updateOptions(builder -> builder.toolCallbacks(toolCallbacks));
	}

	public @Nullable Map<String, Object> getToolContext() {
		return toOptions().getToolContext();
	}

	public void setToolContext(Map<String, Object> toolContext) {
		updateOptions(builder -> builder.toolContext(toolContext));
	}

	public @Nullable Boolean getIncrementalOutput() {
		return toOptions().getIncrementalOutput();
	}

	public void setIncrementalOutput(Boolean incrementalOutput) {
		updateOptions(builder -> builder.incrementalOutput(incrementalOutput));
	}

	public @Nullable Boolean getVlHighResolutionImages() {
		return toOptions().getVlHighResolutionImages();
	}

	public void setVlHighResolutionImages(Boolean vlHighResolutionImages) {
		updateOptions(builder -> builder.vlHighResolutionImages(vlHighResolutionImages));
	}

	public @Nullable Boolean getEnableThinking() {
		return toOptions().getEnableThinking();
	}

	public void setEnableThinking(Boolean enableThinking) {
		updateOptions(builder -> builder.enableThinking(enableThinking));
	}

	public @Nullable Boolean getMultiModel() {
		return toOptions().getMultiModel();
	}

	public void setMultiModel(Boolean multiModel) {
		updateOptions(builder -> builder.multiModel(multiModel));
	}

	public @Nullable Map<String, Object> getExtraBody() {
		return toOptions().getExtraBody();
	}

	public void setExtraBody(Map<String, Object> extraBody) {
		updateOptions(builder -> builder.extraBody(extraBody));
	}

	public @Nullable String getDataInspection() {
		return toOptions().getDataInspection();
	}

	public void setDataInspection(String dataInspection) {
		updateOptions(builder -> builder.dataInspection(dataInspection));
	}

	public @Nullable List<Skill> getSkill() {
		return toOptions().getSkill();
	}

	public void setSkill(List<Skill> skill) {
		updateOptions(builder -> builder.skill(skill));
	}

	public String getCompletionsPath() {
		return completionsPath;
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
	public class Options {

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".model")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable String getModel() {
			return DashScopeChatProperties.this.getModel();
		}

		public void setModel(String model) {
			DashScopeChatProperties.this.setModel(model);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".max-tokens")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Integer getMaxTokens() {
			return DashScopeChatProperties.this.getMaxTokens();
		}

		public void setMaxTokens(Integer maxTokens) {
			DashScopeChatProperties.this.setMaxTokens(maxTokens);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".max-completion-tokens")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Integer getMaxCompletionTokens() {
			return DashScopeChatProperties.this.getMaxCompletionTokens();
		}

		public void setMaxCompletionTokens(Integer maxCompletionTokens) {
			DashScopeChatProperties.this.setMaxCompletionTokens(maxCompletionTokens);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".stream")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Boolean getStream() {
			return DashScopeChatProperties.this.getStream();
		}

		public void setStream(Boolean stream) {
			DashScopeChatProperties.this.setStream(stream);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".temperature")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Double getTemperature() {
			return DashScopeChatProperties.this.getTemperature();
		}

		public void setTemperature(Double temperature) {
			DashScopeChatProperties.this.setTemperature(temperature);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".search-options")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable SearchOptions getSearchOptions() {
			return DashScopeChatProperties.this.getSearchOptions();
		}

		public void setSearchOptions(SearchOptions searchOptions) {
			DashScopeChatProperties.this.setSearchOptions(searchOptions);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".parallel-tool-calls")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Boolean getParallelToolCalls() {
			return DashScopeChatProperties.this.getParallelToolCalls();
		}

		public void setParallelToolCalls(Boolean parallelToolCalls) {
			DashScopeChatProperties.this.setParallelToolCalls(parallelToolCalls);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".http-headers")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Map<String, String> getHttpHeaders() {
			return DashScopeChatProperties.this.getHttpHeaders();
		}

		public void setHttpHeaders(Map<String, String> httpHeaders) {
			DashScopeChatProperties.this.setHttpHeaders(httpHeaders);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".top-p")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Double getTopP() {
			return DashScopeChatProperties.this.getTopP();
		}

		public void setTopP(Double topP) {
			DashScopeChatProperties.this.setTopP(topP);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".top-k")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Integer getTopK() {
			return DashScopeChatProperties.this.getTopK();
		}

		public void setTopK(Integer topK) {
			DashScopeChatProperties.this.setTopK(topK);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".stop")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Object getStop() {
			return DashScopeChatProperties.this.getStop();
		}

		public void setStop(Object stop) {
			DashScopeChatProperties.this.setStop(stop);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".response-format")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable ResponseFormat getResponseFormat() {
			return DashScopeChatProperties.this.getResponseFormat();
		}

		public void setResponseFormat(ResponseFormat responseFormat) {
			DashScopeChatProperties.this.setResponseFormat(responseFormat);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".result-format")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable String getResultFormat() {
			return DashScopeChatProperties.this.getResultFormat();
		}

		public void setResultFormat(String resultFormat) {
			DashScopeChatProperties.this.setResultFormat(resultFormat);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".logprobs")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Boolean getLogprobs() {
			return DashScopeChatProperties.this.getLogprobs();
		}

		public void setLogprobs(Boolean logprobs) {
			DashScopeChatProperties.this.setLogprobs(logprobs);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".top-log-probs")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Integer getTopLogProbs() {
			return DashScopeChatProperties.this.getTopLogProbs();
		}

		public void setTopLogProbs(Integer topLogProbs) {
			DashScopeChatProperties.this.setTopLogProbs(topLogProbs);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".n")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Integer getN() {
			return DashScopeChatProperties.this.getN();
		}

		public void setN(Integer n) {
			DashScopeChatProperties.this.setN(n);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".thinking-budget")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Integer getThinkingBudget() {
			return DashScopeChatProperties.this.getThinkingBudget();
		}

		public void setThinkingBudget(Integer thinkingBudget) {
			DashScopeChatProperties.this.setThinkingBudget(thinkingBudget);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".enable-code-interpreter")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Boolean getEnableCodeInterpreter() {
			return DashScopeChatProperties.this.getEnableCodeInterpreter();
		}

		public void setEnableCodeInterpreter(Boolean enableCodeInterpreter) {
			DashScopeChatProperties.this.setEnableCodeInterpreter(enableCodeInterpreter);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".enable-search")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Boolean getEnableSearch() {
			return DashScopeChatProperties.this.getEnableSearch();
		}

		public void setEnableSearch(Boolean enableSearch) {
			DashScopeChatProperties.this.setEnableSearch(enableSearch);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".repetition-penalty")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Double getRepetitionPenalty() {
			return DashScopeChatProperties.this.getRepetitionPenalty();
		}

		public void setRepetitionPenalty(Double repetitionPenalty) {
			DashScopeChatProperties.this.setRepetitionPenalty(repetitionPenalty);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".presence-penalty")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Double getPresencePenalty() {
			return DashScopeChatProperties.this.getPresencePenalty();
		}

		public void setPresencePenalty(Double presencePenalty) {
			DashScopeChatProperties.this.setPresencePenalty(presencePenalty);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".preserve-thinking")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Boolean getPreserveThinking() {
			return DashScopeChatProperties.this.getPreserveThinking();
		}

		public void setPreserveThinking(Boolean preserveThinking) {
			DashScopeChatProperties.this.setPreserveThinking(preserveThinking);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".reasoning-effort")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable String getReasoningEffort() {
			return DashScopeChatProperties.this.getReasoningEffort();
		}

		public void setReasoningEffort(String reasoningEffort) {
			DashScopeChatProperties.this.setReasoningEffort(reasoningEffort);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".tool-stream")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Boolean getToolStream() {
			return DashScopeChatProperties.this.getToolStream();
		}

		public void setToolStream(Boolean toolStream) {
			DashScopeChatProperties.this.setToolStream(toolStream);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".tools")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable List<Tool> getTools() {
			return DashScopeChatProperties.this.getTools();
		}

		public void setTools(List<Tool> tools) {
			DashScopeChatProperties.this.setTools(tools);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".tool-choice")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Object getToolChoice() {
			return DashScopeChatProperties.this.getToolChoice();
		}

		public void setToolChoice(Object toolChoice) {
			DashScopeChatProperties.this.setToolChoice(toolChoice);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".seed")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Integer getSeed() {
			return DashScopeChatProperties.this.getSeed();
		}

		public void setSeed(Integer seed) {
			DashScopeChatProperties.this.setSeed(seed);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".tool-callbacks")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable List<ToolCallback> getToolCallbacks() {
			return DashScopeChatProperties.this.getToolCallbacks();
		}

		public void setToolCallbacks(List<ToolCallback> toolCallbacks) {
			DashScopeChatProperties.this.setToolCallbacks(toolCallbacks);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".tool-context")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Map<String, Object> getToolContext() {
			return DashScopeChatProperties.this.getToolContext();
		}

		public void setToolContext(Map<String, Object> toolContext) {
			DashScopeChatProperties.this.setToolContext(toolContext);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".incremental-output")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Boolean getIncrementalOutput() {
			return DashScopeChatProperties.this.getIncrementalOutput();
		}

		public void setIncrementalOutput(Boolean incrementalOutput) {
			DashScopeChatProperties.this.setIncrementalOutput(incrementalOutput);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".vl-high-resolution-images")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Boolean getVlHighResolutionImages() {
			return DashScopeChatProperties.this.getVlHighResolutionImages();
		}

		public void setVlHighResolutionImages(Boolean vlHighResolutionImages) {
			DashScopeChatProperties.this.setVlHighResolutionImages(vlHighResolutionImages);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".enable-thinking")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Boolean getEnableThinking() {
			return DashScopeChatProperties.this.getEnableThinking();
		}

		public void setEnableThinking(Boolean enableThinking) {
			DashScopeChatProperties.this.setEnableThinking(enableThinking);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".multi-model")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Boolean getMultiModel() {
			return DashScopeChatProperties.this.getMultiModel();
		}

		public void setMultiModel(Boolean multiModel) {
			DashScopeChatProperties.this.setMultiModel(multiModel);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".extra-body")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Map<String, Object> getExtraBody() {
			return DashScopeChatProperties.this.getExtraBody();
		}

		public void setExtraBody(Map<String, Object> extraBody) {
			DashScopeChatProperties.this.setExtraBody(extraBody);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".data-inspection")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable String getDataInspection() {
			return DashScopeChatProperties.this.getDataInspection();
		}

		public void setDataInspection(String dataInspection) {
			DashScopeChatProperties.this.setDataInspection(dataInspection);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".skill")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable List<Skill> getSkill() {
			return DashScopeChatProperties.this.getSkill();
		}

		public void setSkill(List<Skill> skill) {
			DashScopeChatProperties.this.setSkill(skill);
		}

	}


}
