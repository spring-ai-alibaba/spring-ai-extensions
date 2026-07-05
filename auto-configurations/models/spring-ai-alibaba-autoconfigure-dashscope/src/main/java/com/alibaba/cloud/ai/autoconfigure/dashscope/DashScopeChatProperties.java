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
import java.util.Set;

import com.alibaba.cloud.ai.dashscope.api.DashScopeResponseFormat;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.alibaba.cloud.ai.dashscope.common.DashScopeChatApiConstants;
import com.alibaba.cloud.ai.dashscope.spec.DashScopeApiSpec;
import com.alibaba.cloud.ai.dashscope.spec.DashScopeModel.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.DeprecatedConfigurationProperty;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

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

	@NestedConfigurationProperty
	private DashScopeChatOptions options = DashScopeChatOptions.builder()
		.model(DEFAULT_CHAT_MODEL)
		.build();

	public DashScopeChatOptions toOptions() {
		return this.options;
	}

	@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX)
	@Deprecated(since = "2.0.0", forRemoval = true)
	public DashScopeChatOptions getOptions() {
		return this.options;
	}

	public void setOptions(DashScopeChatOptions options) {
		this.options = options;
	}

	public DashScopeApiSpec.TranslationOptions getTranslationOptions() {
		return this.options.getTranslationOptions();
	}

	public void setTranslationOptions(DashScopeApiSpec.TranslationOptions translationOptions) {
		this.options.setTranslationOptions(translationOptions);
	}

	public String getOutputFormat() {
		return this.options.getOutputFormat();
	}

	public void setOutputFormat(String outputFormat) {
		this.options.setOutputFormat(outputFormat);
	}

	public Integer getTopLogProbs() {
		return this.options.getTopLogProbs();
	}

	public void setTopLogProbs(Integer topLogProbs) {
		this.options.setTopLogProbs(topLogProbs);
	}

	public Boolean getLogprobs() {
		return this.options.getLogprobs();
	}

	public void setLogprobs(Boolean logprobs) {
		this.options.setLogprobs(logprobs);
	}

	public DashScopeApiSpec.OCROption getOcrOptions() {
		return this.options.getOcrOptions();
	}

	public void setOcrOptions(DashScopeApiSpec.OCROption ocrOptions) {
		this.options.setOcrOptions(ocrOptions);
	}

	public Boolean getVlEnableImageHwOutput() {
		return this.options.getVlEnableImageHwOutput();
	}

	public void setVlEnableImageHwOutput(Boolean vlEnableImageHwOutput) {
		this.options.setVlEnableImageHwOutput(vlEnableImageHwOutput);
	}

	public Object getAudio() {
		return this.options.getAudio();
	}

	public void setAudio(Object audio) {
		this.options.setAudio(audio);
	}

	public Object getStreamOptions() {
		return this.options.getStreamOptions();
	}

	public void setStreamOptions(Object streamOptions) {
		this.options.setStreamOptions(streamOptions);
	}

	public Object getAsrOptions() {
		return this.options.getAsrOptions();
	}

	public void setAsrOptions(Object asrOptions) {
		this.options.setAsrOptions(asrOptions);
	}

	public Integer getMaxInputTokens() {
		return this.options.getMaxInputTokens();
	}

	public void setMaxInputTokens(Integer maxInputTokens) {
		this.options.setMaxInputTokens(maxInputTokens);
	}

	public List<String> getModalities() {
		return this.options.getModalities();
	}

	public void setModalities(List<String> modalities) {
		this.options.setModalities(modalities);
	}

	public String getModel() {
		return this.options.getModel();
	}

	public void setModel(String model) {
		this.options.setModel(model);
	}

	public Integer getMaxTokens() {
		return this.options.getMaxTokens();
	}

	public void setMaxTokens(Integer maxTokens) {
		this.options.setMaxTokens(maxTokens);
	}

	public Boolean getStream() {
		return this.options.getStream();
	}

	public void setStream(Boolean stream) {
		this.options.setStream(stream);
	}

	public Double getTemperature() {
		return this.options.getTemperature();
	}

	public void setTemperature(Double temperature) {
		this.options.setTemperature(temperature);
	}

	public DashScopeApiSpec.SearchOptions getSearchOptions() {
		return this.options.getSearchOptions();
	}

	public void setSearchOptions(DashScopeApiSpec.SearchOptions searchOptions) {
		this.options.setSearchOptions(searchOptions);
	}

	public Boolean getParallelToolCalls() {
		return this.options.getParallelToolCalls();
	}

	public void setParallelToolCalls(Boolean parallelToolCalls) {
		this.options.setParallelToolCalls(parallelToolCalls);
	}

	public Map<String, String> getHttpHeaders() {
		return this.options.getHttpHeaders();
	}

	public void setHttpHeaders(Map<String, String> httpHeaders) {
		this.options.setHttpHeaders(httpHeaders);
	}

	public Double getTopP() {
		return this.options.getTopP();
	}

	public void setTopP(Double topP) {
		this.options.setTopP(topP);
	}

	public Integer getTopK() {
		return this.options.getTopK();
	}

	public void setTopK(Integer topK) {
		this.options.setTopK(topK);
	}

	public List<Object> getStop() {
		return this.options.getStop();
	}

	public void setStop(List<Object> stop) {
		this.options.setStop(stop);
	}

	public DashScopeResponseFormat getResponseFormat() {
		return this.options.getResponseFormat();
	}

	public void setResponseFormat(DashScopeResponseFormat responseFormat) {
		this.options.setResponseFormat(responseFormat);
	}

	public Integer getThinkingBudget() {
		return this.options.getThinkingBudget();
	}

	public void setThinkingBudget(Integer thinkingBudget) {
		this.options.setThinkingBudget(thinkingBudget);
	}

	public Boolean getEnableCodeInterpreter() {
		return this.options.getEnableCodeInterpreter();
	}

	public void setEnableCodeInterpreter(Boolean enableCodeInterpreter) {
		this.options.setEnableCodeInterpreter(enableCodeInterpreter);
	}

	public Boolean getEnableSearch() {
		return this.options.getEnableSearch();
	}

	public void setEnableSearch(Boolean enableSearch) {
		this.options.setEnableSearch(enableSearch);
	}

	public Double getRepetitionPenalty() {
		return this.options.getRepetitionPenalty();
	}

	public void setRepetitionPenalty(Double repetitionPenalty) {
		this.options.setRepetitionPenalty(repetitionPenalty);
	}

	public List<DashScopeApiSpec.FunctionTool> getTools() {
		return this.options.getTools();
	}

	public void setTools(List<DashScopeApiSpec.FunctionTool> tools) {
		this.options.setTools(tools);
	}

	public Object getToolChoice() {
		return this.options.getToolChoice();
	}

	public void setToolChoice(Object toolChoice) {
		this.options.setToolChoice(toolChoice);
	}

	public Integer getSeed() {
		return this.options.getSeed();
	}

	public void setSeed(Integer seed) {
		this.options.setSeed(seed);
	}

	public List<ToolCallback> getToolCallbacks() {
		return this.options.getToolCallbacks();
	}

	public void setToolCallbacks(List<ToolCallback> toolCallbacks) {
		this.options.setToolCallbacks(toolCallbacks);
	}

	public Set<String> getToolNames() {
		return this.options.getToolNames();
	}

	public void setToolNames(Set<String> toolNames) {
		this.options.setToolNames(toolNames);
	}

	public Boolean getInternalToolExecutionEnabled() {
		return this.options.getInternalToolExecutionEnabled();
	}

	public void setInternalToolExecutionEnabled(Boolean internalToolExecutionEnabled) {
		this.options.setInternalToolExecutionEnabled(internalToolExecutionEnabled);
	}

	public Map<String, Object> getToolContext() {
		return this.options.getToolContext();
	}

	public void setToolContext(Map<String, Object> toolContext) {
		this.options.setToolContext(toolContext);
	}

	public Boolean getIncrementalOutput() {
		return this.options.getIncrementalOutput();
	}

	public void setIncrementalOutput(Boolean incrementalOutput) {
		this.options.setIncrementalOutput(incrementalOutput);
	}

	public Boolean getVlHighResolutionImages() {
		return this.options.getVlHighResolutionImages();
	}

	public void setVlHighResolutionImages(Boolean vlHighResolutionImages) {
		this.options.setVlHighResolutionImages(vlHighResolutionImages);
	}

	public Boolean getEnableThinking() {
		return this.options.getEnableThinking();
	}

	public void setEnableThinking(Boolean enableThinking) {
		this.options.setEnableThinking(enableThinking);
	}

	public Boolean getMultiModel() {
		return this.options.getMultiModel();
	}

	public void setMultiModel(Boolean multiModel) {
		this.options.setMultiModel(multiModel);
	}

	public Map<String, Object> getExtraBody() {
		return this.options.getExtraBody();
	}

	public void setExtraBody(Map<String, Object> extraBody) {
		this.options.setExtraBody(extraBody);
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

}
