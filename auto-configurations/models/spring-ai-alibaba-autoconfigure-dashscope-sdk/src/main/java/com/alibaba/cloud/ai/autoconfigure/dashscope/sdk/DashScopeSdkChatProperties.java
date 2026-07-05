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

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.alibaba.cloud.ai.dashscope.sdk.chat.DashScopeSdkChatOptions;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.DeprecatedConfigurationProperty;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * DashScope SDK chat model properties.
 */
@ConfigurationProperties(DashScopeSdkChatProperties.CONFIG_PREFIX)
public class DashScopeSdkChatProperties extends DashScopeSdkParentProperties {

	public static final String CONFIG_PREFIX = "spring.ai.dashscope.sdk.chat";

	public static final String DEFAULT_DEPLOYMENT_NAME = "qwen-plus";

	private boolean enabled = true;

	@NestedConfigurationProperty
	private DashScopeSdkChatOptions options = DashScopeSdkChatOptions.builder()
		.model(DEFAULT_DEPLOYMENT_NAME)
		.build();

	public DashScopeSdkChatOptions toOptions() {
		return this.options;
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX)
	@Deprecated(since = "2.0.0", forRemoval = true)
	public DashScopeSdkChatOptions getOptions() {
		return this.options;
	}

	public void setOptions(DashScopeSdkChatOptions options) {
		this.options = options;
	}

	public String getModel() {
		return this.options.getModel();
	}

	public void setModel(String model) {
		this.options.setModel(model);
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

	public Integer getSeed() {
		return this.options.getSeed();
	}

	public void setSeed(Integer seed) {
		this.options.setSeed(seed);
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

	public Boolean getEnableSearch() {
		return this.options.getEnableSearch();
	}

	public void setEnableSearch(Boolean enableSearch) {
		this.options.setEnableSearch(enableSearch);
	}

	public Integer getMaxTokens() {
		return this.options.getMaxTokens();
	}

	public void setMaxTokens(Integer maxTokens) {
		this.options.setMaxTokens(maxTokens);
	}

	public Boolean getIncrementalOutput() {
		return this.options.getIncrementalOutput();
	}

	public void setIncrementalOutput(Boolean incrementalOutput) {
		this.options.setIncrementalOutput(incrementalOutput);
	}

	public Double getRepetitionPenalty() {
		return this.options.getRepetitionPenalty();
	}

	public void setRepetitionPenalty(Double repetitionPenalty) {
		this.options.setRepetitionPenalty(repetitionPenalty);
	}

	public Object getToolChoice() {
		return this.options.getToolChoice();
	}

	public void setToolChoice(Object toolChoice) {
		this.options.setToolChoice(toolChoice);
	}

	public Map<String, String> getHttpHeaders() {
		return this.options.getHttpHeaders();
	}

	public void setHttpHeaders(Map<String, String> httpHeaders) {
		this.options.setHttpHeaders(httpHeaders);
	}

	public Map<String, Object> getExtraBody() {
		return this.options.getExtraBody();
	}

	public void setExtraBody(Map<String, Object> extraBody) {
		this.options.setExtraBody(extraBody);
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

}
