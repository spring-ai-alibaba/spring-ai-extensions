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

package com.alibaba.cloud.ai.dashscope.sdk.chat;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.model.tool.ToolCallingChatOptions;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Options for DashScope SDK chat model.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DashScopeSdkChatOptions implements ToolCallingChatOptions {

	@JsonProperty("model")
	private String model;

	@JsonIgnore
	private Boolean stream;

	@JsonProperty("temperature")
	private Double temperature;

	@JsonProperty("seed")
	private Integer seed;

	@JsonProperty("top_p")
	private Double topP;

	@JsonProperty("top_k")
	private Integer topK;

	@JsonProperty("stop")
	private List<Object> stop;

	@JsonProperty("enable_search")
	private Boolean enableSearch = false;

	@JsonProperty("max_tokens")
	private Integer maxTokens;

	@JsonProperty("incremental_output")
	private Boolean incrementalOutput = true;

	@JsonProperty("repetition_penalty")
	private Double repetitionPenalty;

	@JsonProperty("tool_choice")
	private Object toolChoice;

	@JsonIgnore
	private Map<String, String> httpHeaders = new HashMap<>();

	@JsonIgnore
	private List<ToolCallback> toolCallbacks = new ArrayList<>();

	@JsonIgnore
	private Set<String> toolNames = new HashSet<>();

	@JsonIgnore
	private Boolean internalToolExecutionEnabled;

	@JsonIgnore
	private Map<String, Object> toolContext = new HashMap<>();

	@JsonProperty("extra_body")
	private Map<String, Object> extraBody;

	public static DashScopeSdkChatOptionsBuilder builder() {
		return new DashScopeSdkChatOptionsBuilder();
	}

	public static DashScopeSdkChatOptions fromOptions(DashScopeSdkChatOptions options) {
		if (options == null) {
			return null;
		}
		DashScopeSdkChatOptions copy = new DashScopeSdkChatOptions();
		copy.setModel(options.getModel());
		copy.setStream(options.getStream());
		copy.setTemperature(options.getTemperature());
		copy.setSeed(options.getSeed());
		copy.setTopP(options.getTopP());
		copy.setTopK(options.getTopK());
		copy.setStop(options.getStop() == null ? null : new ArrayList<>(options.getStop()));
		copy.setEnableSearch(options.getEnableSearch());
		copy.setMaxTokens(options.getMaxTokens());
		copy.setIncrementalOutput(options.getIncrementalOutput());
		copy.setRepetitionPenalty(options.getRepetitionPenalty());
		copy.setToolChoice(options.getToolChoice());
		copy.setHttpHeaders(options.getHttpHeaders() == null ? new HashMap<>() : new HashMap<>(options.getHttpHeaders()));
		copy.setToolCallbacks(
				options.getToolCallbacks() == null ? new ArrayList<>() : new ArrayList<>(options.getToolCallbacks()));
		copy.setToolNames(options.getToolNames() == null ? new HashSet<>() : new HashSet<>(options.getToolNames()));
		copy.setInternalToolExecutionEnabled(options.getInternalToolExecutionEnabled());
		copy.setToolContext(options.getToolContext() == null ? new HashMap<>() : new HashMap<>(options.getToolContext()));
		copy.setExtraBody(options.getExtraBody() == null ? null : new HashMap<>(options.getExtraBody()));
		return copy;
	}

	@Override
	public String getModel() {
		return this.model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public Boolean getStream() {
		return this.stream;
	}

	public void setStream(Boolean stream) {
		this.stream = stream;
	}

	@Override
	public Double getTemperature() {
		return this.temperature;
	}

	public void setTemperature(Double temperature) {
		this.temperature = temperature;
	}

	public Integer getSeed() {
		return this.seed;
	}

	public void setSeed(Integer seed) {
		this.seed = seed;
	}

	@Override
	public Double getTopP() {
		return this.topP;
	}

	public void setTopP(Double topP) {
		this.topP = topP;
	}

	@Override
	public Integer getTopK() {
		return this.topK;
	}

	public void setTopK(Integer topK) {
		this.topK = topK;
	}

	public List<Object> getStop() {
		return this.stop;
	}

	public void setStop(List<Object> stop) {
		this.stop = stop;
	}

	public Boolean getEnableSearch() {
		return this.enableSearch;
	}

	public void setEnableSearch(Boolean enableSearch) {
		this.enableSearch = enableSearch;
	}

	@Override
	public Integer getMaxTokens() {
		return this.maxTokens;
	}

	public void setMaxTokens(Integer maxTokens) {
		this.maxTokens = maxTokens;
	}

	public Boolean getIncrementalOutput() {
		return this.incrementalOutput;
	}

	public void setIncrementalOutput(Boolean incrementalOutput) {
		this.incrementalOutput = incrementalOutput;
	}

	public Double getRepetitionPenalty() {
		return this.repetitionPenalty;
	}

	public void setRepetitionPenalty(Double repetitionPenalty) {
		this.repetitionPenalty = repetitionPenalty;
	}

	public Object getToolChoice() {
		return this.toolChoice;
	}

	public void setToolChoice(Object toolChoice) {
		this.toolChoice = toolChoice;
	}

	public Map<String, String> getHttpHeaders() {
		return this.httpHeaders;
	}

	public void setHttpHeaders(Map<String, String> httpHeaders) {
		this.httpHeaders = httpHeaders;
	}

	public Map<String, Object> getExtraBody() {
		return this.extraBody;
	}

	public void setExtraBody(Map<String, Object> extraBody) {
		this.extraBody = extraBody;
	}

	@Override
	public Double getFrequencyPenalty() {
		return null;
	}

	@Override
	public Double getPresencePenalty() {
		return null;
	}

	@Override
	public List<String> getStopSequences() {
		if (this.stop == null) {
			return null;
		}
		List<String> stopStrings = this.stop.stream().filter(String.class::isInstance).map(String.class::cast).toList();
		return stopStrings.isEmpty() ? null : stopStrings;
	}

	@Override
	public ChatOptions copy() {
		return DashScopeSdkChatOptions.fromOptions(this);
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
	public Boolean getInternalToolExecutionEnabled() {
		return this.internalToolExecutionEnabled;
	}

	@Override
	@JsonIgnore
	public void setInternalToolExecutionEnabled(Boolean internalToolExecutionEnabled) {
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

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		DashScopeSdkChatOptions that = (DashScopeSdkChatOptions) o;
		return Objects.equals(this.model, that.model) && Objects.equals(this.stream, that.stream)
				&& Objects.equals(this.temperature, that.temperature) && Objects.equals(this.seed, that.seed)
				&& Objects.equals(this.topP, that.topP) && Objects.equals(this.topK, that.topK)
				&& Objects.equals(this.stop, that.stop) && Objects.equals(this.enableSearch, that.enableSearch)
				&& Objects.equals(this.maxTokens, that.maxTokens)
				&& Objects.equals(this.incrementalOutput, that.incrementalOutput)
				&& Objects.equals(this.repetitionPenalty, that.repetitionPenalty)
				&& Objects.equals(this.toolChoice, that.toolChoice)
				&& Objects.equals(this.httpHeaders, that.httpHeaders)
				&& Objects.equals(this.toolCallbacks, that.toolCallbacks)
				&& Objects.equals(this.toolNames, that.toolNames)
				&& Objects.equals(this.internalToolExecutionEnabled, that.internalToolExecutionEnabled)
				&& Objects.equals(this.toolContext, that.toolContext)
				&& Objects.equals(this.extraBody, that.extraBody);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.model, this.stream, this.temperature, this.seed, this.topP, this.topK, this.stop,
				this.enableSearch, this.maxTokens, this.incrementalOutput, this.repetitionPenalty, this.toolChoice,
				this.httpHeaders, this.toolCallbacks, this.toolNames, this.internalToolExecutionEnabled, this.toolContext,
				this.extraBody);
	}

	@Override
	public String toString() {
		return "DashScopeSdkChatOptions{" + "model='" + this.model + '\'' + ", stream=" + this.stream
				+ ", temperature=" + this.temperature + ", seed=" + this.seed + ", topP=" + this.topP + ", topK="
				+ this.topK + ", stop=" + this.stop + ", enableSearch=" + this.enableSearch + ", maxTokens="
				+ this.maxTokens + ", incrementalOutput=" + this.incrementalOutput + ", repetitionPenalty="
				+ this.repetitionPenalty + ", toolChoice=" + this.toolChoice + ", httpHeaders=" + this.httpHeaders
				+ ", toolNames=" + this.toolNames + ", extraBody=" + this.extraBody + '}';
	}

	public static class DashScopeSdkChatOptionsBuilder {

		private final DashScopeSdkChatOptions options;

		public DashScopeSdkChatOptionsBuilder() {
			this.options = new DashScopeSdkChatOptions();
		}

		public DashScopeSdkChatOptionsBuilder model(String model) {
			this.options.model = model;
			return this;
		}

		public DashScopeSdkChatOptionsBuilder stream(Boolean stream) {
			this.options.stream = stream;
			return this;
		}

		public DashScopeSdkChatOptionsBuilder temperature(Double temperature) {
			this.options.temperature = temperature;
			return this;
		}

		public DashScopeSdkChatOptionsBuilder seed(Integer seed) {
			this.options.seed = seed;
			return this;
		}

		public DashScopeSdkChatOptionsBuilder topP(Double topP) {
			this.options.topP = topP;
			return this;
		}

		public DashScopeSdkChatOptionsBuilder topK(Integer topK) {
			this.options.topK = topK;
			return this;
		}

		public DashScopeSdkChatOptionsBuilder stop(List<Object> stop) {
			this.options.stop = stop;
			return this;
		}

		public DashScopeSdkChatOptionsBuilder enableSearch(Boolean enableSearch) {
			this.options.enableSearch = enableSearch;
			return this;
		}

		public DashScopeSdkChatOptionsBuilder maxTokens(Integer maxTokens) {
			this.options.maxTokens = maxTokens;
			return this;
		}

		public DashScopeSdkChatOptionsBuilder incrementalOutput(Boolean incrementalOutput) {
			this.options.incrementalOutput = incrementalOutput;
			return this;
		}

		public DashScopeSdkChatOptionsBuilder repetitionPenalty(Double repetitionPenalty) {
			this.options.repetitionPenalty = repetitionPenalty;
			return this;
		}

		public DashScopeSdkChatOptionsBuilder toolChoice(Object toolChoice) {
			this.options.toolChoice = toolChoice;
			return this;
		}

		public DashScopeSdkChatOptionsBuilder httpHeaders(Map<String, String> httpHeaders) {
			this.options.httpHeaders = httpHeaders;
			return this;
		}

		public DashScopeSdkChatOptionsBuilder toolCallbacks(List<ToolCallback> toolCallbacks) {
			this.options.setToolCallbacks(toolCallbacks);
			return this;
		}

		public DashScopeSdkChatOptionsBuilder toolNames(Set<String> toolNames) {
			this.options.setToolNames(toolNames);
			return this;
		}

		public DashScopeSdkChatOptionsBuilder internalToolExecutionEnabled(Boolean internalToolExecutionEnabled) {
			this.options.internalToolExecutionEnabled = internalToolExecutionEnabled;
			return this;
		}

		public DashScopeSdkChatOptionsBuilder toolContext(Map<String, Object> toolContext) {
			this.options.toolContext = toolContext;
			return this;
		}

		public DashScopeSdkChatOptionsBuilder extraBody(Map<String, Object> extraBody) {
			this.options.extraBody = extraBody;
			return this;
		}

		public DashScopeSdkChatOptions build() {
			return this.options;
		}

	}

}
