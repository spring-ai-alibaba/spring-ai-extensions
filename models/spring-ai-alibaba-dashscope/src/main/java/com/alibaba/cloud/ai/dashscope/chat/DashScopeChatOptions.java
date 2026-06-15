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
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatApiSpec.ChatCompletionRequest.Parameters.ResponseFormat;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatApiSpec.ChatCompletionRequest.Parameters.SearchOptions;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatApiSpec.ChatCompletionRequest.Parameters.Skill;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatApiSpec.ChatCompletionRequest.Parameters.Tool;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
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

	private final @Nullable String model;

	@JsonIgnore
	private final @Nullable Boolean stream;

	private final @Nullable Double temperature;

	private final @Nullable Double topP;

	private final @Nullable Integer topK;

	private final @Nullable Boolean enableThinking;

	private final @Nullable Boolean preserveThinking;

	private final @Nullable Integer thinkingBudget;

	private final @Nullable String reasoningEffort;

	private final @Nullable Boolean toolStream;

	private final @Nullable Boolean enableCodeInterpreter;

	private final @Nullable Double repetitionPenalty;

	private final @Nullable Double presencePenalty;

	private final @Nullable Boolean vlHighResolutionImages;

	private final @Nullable Boolean vlEnableImageHwOutput;

	private final @Nullable Integer maxCompletionTokens;

	private final @Nullable Integer seed;

	private final Boolean incrementalOutput;

	private final @Nullable ResponseFormat responseFormat;

	private final @Nullable String resultFormat;

	private final @Nullable Boolean logprobs;

	private final @Nullable Integer topLogprobs;

	private final @Nullable Integer n;

	private final @Nullable Object stop;

	private final @Nullable List<Tool> tools;

	private final @Nullable Object toolChoice;

	private final @Nullable Boolean parallelToolCalls;

	private final @Nullable Boolean enableSearch;

	private final @Nullable SearchOptions searchOptions;

	private final @Nullable String dataInspection;

	private final @Nullable List<Skill> skill;

	@JsonIgnore
	private final @Nullable Map<String, String> httpHeaders;

	@JsonIgnore
	private final @Nullable List<ToolCallback> toolCallbacks;

	private final @Nullable Boolean multiModel;

	@JsonIgnore
	private final @Nullable Map<String, Object> toolContext;

	protected DashScopeChatOptions(@Nullable String model, @Nullable Boolean stream, @Nullable Double temperature,
			@Nullable Double topP, @Nullable Integer topK, @Nullable Boolean enableThinking,
			@Nullable Boolean preserveThinking, @Nullable Integer thinkingBudget, @Nullable String reasoningEffort,
			@Nullable Boolean toolStream, @Nullable Boolean enableCodeInterpreter, @Nullable Double repetitionPenalty,
			@Nullable Double presencePenalty, @Nullable Boolean vlHighResolutionImages,
			@Nullable Boolean vlEnableImageHwOutput, @Nullable Integer maxCompletionTokens, @Nullable Integer seed,
			@Nullable Boolean incrementalOutput,
			@Nullable ResponseFormat responseFormat, @Nullable String resultFormat, @Nullable Boolean logprobs,
			@Nullable Integer topLogprobs, @Nullable Integer n, @Nullable Object stop, @Nullable List<Tool> tools,
			@Nullable Object toolChoice, @Nullable Boolean parallelToolCalls, @Nullable Boolean enableSearch,
			@Nullable SearchOptions searchOptions, @Nullable String dataInspection, @Nullable List<Skill> skill,
			@Nullable Map<String, String> httpHeaders, @Nullable List<ToolCallback> toolCallbacks,
			@Nullable Boolean multiModel, @Nullable Map<String, Object> toolContext) {

		this.model = model;
		this.stream = stream;
		this.temperature = temperature;
		this.topP = topP;
		this.topK = topK;
		this.enableThinking = enableThinking;
		this.preserveThinking = preserveThinking;
		this.thinkingBudget = thinkingBudget;
		this.reasoningEffort = reasoningEffort;
		this.toolStream = toolStream;
		this.enableCodeInterpreter = enableCodeInterpreter;
		this.repetitionPenalty = repetitionPenalty;
		this.presencePenalty = presencePenalty;
		this.vlHighResolutionImages = vlHighResolutionImages;
		this.vlEnableImageHwOutput = vlEnableImageHwOutput;
		this.maxCompletionTokens = maxCompletionTokens;
		this.seed = seed;
		this.incrementalOutput = incrementalOutput != null ? incrementalOutput : Boolean.TRUE;
		this.responseFormat = responseFormat;
		this.resultFormat = resultFormat;
		this.logprobs = logprobs;
		this.topLogprobs = topLogprobs;
		this.n = n;
		this.stop = copyStop(stop);
		this.tools = tools != null ? List.copyOf(tools) : null;
		this.toolChoice = toolChoice;
		this.parallelToolCalls = parallelToolCalls;
		this.enableSearch = enableSearch;
		this.searchOptions = searchOptions;
		this.dataInspection = dataInspection;
		this.skill = skill != null ? List.copyOf(skill) : null;
		this.httpHeaders = httpHeaders != null ? Map.copyOf(httpHeaders) : null;
		this.toolCallbacks = toolCallbacks != null ? List.copyOf(toolCallbacks) : null;
		this.multiModel = multiModel;
		this.toolContext = toolContext != null ? Map.copyOf(toolContext) : null;
	}

	@SuppressWarnings("unchecked")
	private static @Nullable Object copyStop(@Nullable Object stop) {
		if (stop instanceof List<?> list) {
			return List.copyOf((List<Object>) list);
		}
		return stop;
	}

	@Override
	public @Nullable String getModel() {
		return this.model;
	}

	public @Nullable Boolean getStream() {
		return this.stream;
	}

	@Override
	public @Nullable Double getTemperature() {
		return this.temperature;
	}

	@Override
	public @Nullable Double getTopP() {
		return this.topP;
	}

	@Override
	public @Nullable Integer getTopK() {
		return this.topK;
	}

	public @Nullable Boolean getEnableThinking() {
		return this.enableThinking;
	}

	public @Nullable Boolean getPreserveThinking() {
		return this.preserveThinking;
	}

	public @Nullable Integer getThinkingBudget() {
		return this.thinkingBudget;
	}

	public @Nullable String getReasoningEffort() {
		return this.reasoningEffort;
	}

	public @Nullable Boolean getToolStream() {
		return this.toolStream;
	}

	public @Nullable Boolean getEnableCodeInterpreter() {
		return this.enableCodeInterpreter;
	}

	public @Nullable Double getRepetitionPenalty() {
		return this.repetitionPenalty;
	}

	@Override
	public @Nullable Double getPresencePenalty() {
		return this.presencePenalty;
	}

	public @Nullable Boolean getVlHighResolutionImages() {
		return this.vlHighResolutionImages;
	}

	public @Nullable Boolean getVlEnableImageHwOutput() {
		return this.vlEnableImageHwOutput;
	}

	@Override
	public @Nullable Integer getMaxTokens() {
		return this.maxCompletionTokens;
	}

	public @Nullable Integer getMaxCompletionTokens() {
		return this.maxCompletionTokens;
	}

	public @Nullable Integer getSeed() {
		return this.seed;
	}

	public Boolean getIncrementalOutput() {
		return this.incrementalOutput;
	}

	public @Nullable ResponseFormat getResponseFormat() {
		return this.responseFormat;
	}

	public @Nullable String getResultFormat() {
		return this.resultFormat;
	}

	public @Nullable Boolean getLogprobs() {
		return this.logprobs;
	}

	public @Nullable Integer getTopLogprobs() {
		return this.topLogprobs;
	}

	public @Nullable Integer getTopLogProbs() {
		return this.topLogprobs;
	}

	public @Nullable Integer getN() {
		return this.n;
	}

	public @Nullable Object getStop() {
		return this.stop;
	}

	@Override
	@SuppressWarnings("unchecked")
	public @Nullable List<String> getStopSequences() {
		if (this.stop instanceof List<?> list && list.stream().allMatch(String.class::isInstance)) {
			return (List<String>) list;
		}
		return null;
	}

	public @Nullable List<Tool> getTools() {
		return this.tools;
	}

	public @Nullable Object getToolChoice() {
		return this.toolChoice;
	}

	public @Nullable Boolean getParallelToolCalls() {
		return this.parallelToolCalls;
	}

	public @Nullable Boolean getEnableSearch() {
		return this.enableSearch;
	}

	public @Nullable SearchOptions getSearchOptions() {
		return this.searchOptions;
	}

	public @Nullable String getDataInspection() {
		return this.dataInspection;
	}

	public @Nullable List<Skill> getSkill() {
		return this.skill;
	}

	public @Nullable Map<String, String> getHttpHeaders() {
		return this.httpHeaders;
	}

	@Override
	@JsonIgnore
	public @Nullable List<ToolCallback> getToolCallbacks() {
		return this.toolCallbacks;
	}


	public @Nullable Boolean getMultiModel() {
		return this.multiModel;
	}

	@Override
	@JsonIgnore
	public @Nullable Map<String, Object> getToolContext() {
		return this.toolContext;
	}

	@Override
	public @Nullable Double getFrequencyPenalty() {
		return null;
	}

	public DashScopeChatOptions copy() {
		return mutate().build();
	}

	public static Builder builder() {
		return new Builder();
	}

	@Override
	public Builder mutate() {
		return builder().model(this.model)
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
			.httpHeaders(this.httpHeaders)
			.toolCallbacks(this.toolCallbacks)
			.multiModel(this.multiModel)
			.toolContext(this.toolContext);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		DashScopeChatOptions that = (DashScopeChatOptions) o;
		return Objects.equals(this.model, that.model) && Objects.equals(this.stream, that.stream)
				&& Objects.equals(this.temperature, that.temperature) && Objects.equals(this.topP, that.topP)
				&& Objects.equals(this.topK, that.topK) && Objects.equals(this.enableThinking, that.enableThinking)
				&& Objects.equals(this.preserveThinking, that.preserveThinking)
				&& Objects.equals(this.thinkingBudget, that.thinkingBudget)
				&& Objects.equals(this.reasoningEffort, that.reasoningEffort)
				&& Objects.equals(this.toolStream, that.toolStream)
				&& Objects.equals(this.enableCodeInterpreter, that.enableCodeInterpreter)
				&& Objects.equals(this.repetitionPenalty, that.repetitionPenalty)
				&& Objects.equals(this.presencePenalty, that.presencePenalty)
				&& Objects.equals(this.vlHighResolutionImages, that.vlHighResolutionImages)
				&& Objects.equals(this.vlEnableImageHwOutput, that.vlEnableImageHwOutput)
				&& Objects.equals(this.maxCompletionTokens, that.maxCompletionTokens)
				&& Objects.equals(this.seed, that.seed)
				&& Objects.equals(this.incrementalOutput, that.incrementalOutput)
				&& Objects.equals(this.responseFormat, that.responseFormat)
				&& Objects.equals(this.resultFormat, that.resultFormat) && Objects.equals(this.logprobs, that.logprobs)
				&& Objects.equals(this.topLogprobs, that.topLogprobs) && Objects.equals(this.n, that.n)
				&& Objects.equals(this.stop, that.stop) && Objects.equals(this.tools, that.tools)
				&& Objects.equals(this.toolChoice, that.toolChoice)
				&& Objects.equals(this.parallelToolCalls, that.parallelToolCalls)
				&& Objects.equals(this.enableSearch, that.enableSearch)
				&& Objects.equals(this.searchOptions, that.searchOptions)
				&& Objects.equals(this.dataInspection, that.dataInspection) && Objects.equals(this.skill, that.skill)
				&& Objects.equals(this.httpHeaders, that.httpHeaders)
				&& Objects.equals(this.toolCallbacks, that.toolCallbacks)
				&& Objects.equals(this.multiModel, that.multiModel)
				&& Objects.equals(this.toolContext, that.toolContext);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.model, this.stream, this.temperature, this.topP, this.topK, this.enableThinking,
				this.preserveThinking, this.thinkingBudget, this.reasoningEffort, this.toolStream,
				this.enableCodeInterpreter, this.repetitionPenalty, this.presencePenalty,
				this.vlHighResolutionImages, this.vlEnableImageHwOutput, this.maxCompletionTokens, this.seed,
				this.incrementalOutput, this.responseFormat, this.resultFormat, this.logprobs, this.topLogprobs,
				this.n, this.stop, this.tools, this.toolChoice, this.parallelToolCalls, this.enableSearch,
				this.searchOptions, this.dataInspection, this.skill, this.httpHeaders,
				this.toolCallbacks, this.multiModel, this.toolContext);
	}

	@Override
	public String toString() {
		return "DashScopeChatOptions{" + "model='" + this.model + '\'' + ", stream=" + this.stream
				+ ", temperature=" + this.temperature + ", topP=" + this.topP + ", topK=" + this.topK
				+ ", enableThinking=" + this.enableThinking + ", preserveThinking=" + this.preserveThinking
				+ ", thinkingBudget=" + this.thinkingBudget + ", reasoningEffort='" + this.reasoningEffort + '\''
				+ ", toolStream=" + this.toolStream + ", enableCodeInterpreter=" + this.enableCodeInterpreter
				+ ", repetitionPenalty=" + this.repetitionPenalty + ", presencePenalty=" + this.presencePenalty
				+ ", vlHighResolutionImages=" + this.vlHighResolutionImages + ", vlEnableImageHwOutput="
				+ this.vlEnableImageHwOutput + ", maxCompletionTokens=" + this.maxCompletionTokens + ", seed="
				+ this.seed + ", incrementalOutput="
				+ this.incrementalOutput + ", responseFormat=" + this.responseFormat + ", resultFormat='"
				+ this.resultFormat + '\'' + ", logprobs=" + this.logprobs + ", topLogprobs="
				+ this.topLogprobs + ", n=" + this.n + ", stop=" + this.stop + ", tools=" + this.tools
				+ ", toolChoice=" + this.toolChoice + ", parallelToolCalls=" + this.parallelToolCalls
				+ ", enableSearch=" + this.enableSearch + ", searchOptions=" + this.searchOptions
				+ ", dataInspection='" + this.dataInspection + '\'' + ", skill=" + this.skill
				+ ", httpHeaders=" + this.httpHeaders + ", toolCallbacks=" + this.toolCallbacks
				+ ", multiModel=" + this.multiModel + ", toolContext=" + this.toolContext + '}';
	}

	public static class Builder extends AbstractBuilder<Builder> {
	}

	protected abstract static class AbstractBuilder<B extends AbstractBuilder<B>>
			extends DefaultToolCallingChatOptions.Builder<B> {

		protected @Nullable Boolean stream;

		protected @Nullable Boolean enableThinking;

		protected @Nullable Boolean preserveThinking;

		protected @Nullable Integer thinkingBudget;

		protected @Nullable String reasoningEffort;

		protected @Nullable Boolean toolStream;

		protected @Nullable Boolean enableCodeInterpreter;

		protected @Nullable Double repetitionPenalty;

		protected @Nullable Boolean vlHighResolutionImages;

		protected @Nullable Boolean vlEnableImageHwOutput;

		protected @Nullable Integer maxCompletionTokens;

		protected @Nullable Integer seed;

		protected @Nullable Boolean incrementalOutput;

		protected @Nullable ResponseFormat responseFormat;

		protected @Nullable String resultFormat;

		protected @Nullable Boolean logprobs;

		protected @Nullable Integer topLogprobs;

		protected @Nullable Integer n;

		protected @Nullable Object stop;

		protected @Nullable List<Tool> tools;

		protected @Nullable Object toolChoice;

		protected @Nullable Boolean parallelToolCalls;

		protected @Nullable Boolean enableSearch;

		protected @Nullable SearchOptions searchOptions;

		protected @Nullable String dataInspection;

		protected @Nullable List<Skill> skill;

		protected @Nullable Map<String, String> httpHeaders;

		protected @Nullable Boolean multiModel;

		@Override
		public B clone() {
			B copy = super.clone();
			copy.stop = this.stop;
			copy.tools = this.tools;
			copy.skill = this.skill;
			copy.httpHeaders = this.httpHeaders;
			return copy;
		}

		public B stream(@Nullable Boolean stream) {
			this.stream = stream;
			return self();
		}

		public B enableThinking(@Nullable Boolean enableThinking) {
			this.enableThinking = enableThinking;
			return self();
		}

		public B preserveThinking(@Nullable Boolean preserveThinking) {
			this.preserveThinking = preserveThinking;
			return self();
		}

		public B thinkingBudget(@Nullable Integer thinkingBudget) {
			this.thinkingBudget = thinkingBudget;
			return self();
		}

		public B reasoningEffort(@Nullable String reasoningEffort) {
			this.reasoningEffort = reasoningEffort;
			return self();
		}

		public B toolStream(@Nullable Boolean toolStream) {
			this.toolStream = toolStream;
			return self();
		}

		public B enableCodeInterpreter(@Nullable Boolean enableCodeInterpreter) {
			this.enableCodeInterpreter = enableCodeInterpreter;
			return self();
		}

		public B repetitionPenalty(@Nullable Double repetitionPenalty) {
			this.repetitionPenalty = repetitionPenalty;
			return self();
		}

		public B vlHighResolutionImages(@Nullable Boolean vlHighResolutionImages) {
			this.vlHighResolutionImages = vlHighResolutionImages;
			return self();
		}

		public B vlEnableImageHwOutput(@Nullable Boolean vlEnableImageHwOutput) {
			this.vlEnableImageHwOutput = vlEnableImageHwOutput;
			return self();
		}

		@Override
		public B maxTokens(@Nullable Integer maxTokens) {
			this.maxCompletionTokens = maxTokens;
			return self();
		}

		public B maxCompletionTokens(@Nullable Integer maxCompletionTokens) {
			this.maxCompletionTokens = maxCompletionTokens;
			return self();
		}

		public B seed(@Nullable Integer seed) {
			this.seed = seed;
			return self();
		}

		public B incrementalOutput(@Nullable Boolean incrementalOutput) {
			this.incrementalOutput = incrementalOutput;
			return self();
		}

		public B responseFormat(@Nullable ResponseFormat responseFormat) {
			this.responseFormat = responseFormat;
			return self();
		}

		public B resultFormat(@Nullable String resultFormat) {
			this.resultFormat = resultFormat;
			return self();
		}

		public B logprobs(@Nullable Boolean logprobs) {
			this.logprobs = logprobs;
			return self();
		}

		public B topLogprobs(@Nullable Integer topLogprobs) {
			this.topLogprobs = topLogprobs;
			return self();
		}

		public B n(@Nullable Integer n) {
			this.n = n;
			return self();
		}

		public B stop(@Nullable Object stop) {
			this.stop = stop;
			return self();
		}

		public B tools(@Nullable List<Tool> tools) {
			this.tools = tools;
			return self();
		}

		public B toolChoice(@Nullable Object toolChoice) {
			this.toolChoice = toolChoice;
			return self();
		}

		public B parallelToolCalls(@Nullable Boolean parallelToolCalls) {
			this.parallelToolCalls = parallelToolCalls;
			return self();
		}

		public B enableSearch(@Nullable Boolean enableSearch) {
			this.enableSearch = enableSearch;
			return self();
		}

		public B searchOptions(@Nullable SearchOptions searchOptions) {
			this.searchOptions = searchOptions;
			return self();
		}

		public B dataInspection(@Nullable String dataInspection) {
			this.dataInspection = dataInspection;
			return self();
		}

		public B skill(@Nullable List<Skill> skill) {
			this.skill = skill;
			return self();
		}

		public B httpHeaders(@Nullable Map<String, String> httpHeaders) {
			this.httpHeaders = httpHeaders;
			return self();
		}

		public B multiModel(@Nullable Boolean multiModel) {
			this.multiModel = multiModel;
			return self();
		}

		@Override
		public B combineWith(ChatOptions.Builder<?> other) {
			super.combineWith(other);
			if (this.maxTokens != null) {
				this.maxCompletionTokens = this.maxTokens;
				this.maxTokens = null;
			}
			if (other instanceof AbstractBuilder<?> that) {
				if (that.stream != null) {
					this.stream = that.stream;
				}
				if (that.enableThinking != null) {
					this.enableThinking = that.enableThinking;
				}
				if (that.preserveThinking != null) {
					this.preserveThinking = that.preserveThinking;
				}
				if (that.thinkingBudget != null) {
					this.thinkingBudget = that.thinkingBudget;
				}
				if (that.reasoningEffort != null) {
					this.reasoningEffort = that.reasoningEffort;
				}
				if (that.toolStream != null) {
					this.toolStream = that.toolStream;
				}
				if (that.enableCodeInterpreter != null) {
					this.enableCodeInterpreter = that.enableCodeInterpreter;
				}
				if (that.repetitionPenalty != null) {
					this.repetitionPenalty = that.repetitionPenalty;
				}
				if (that.vlHighResolutionImages != null) {
					this.vlHighResolutionImages = that.vlHighResolutionImages;
				}
				if (that.vlEnableImageHwOutput != null) {
					this.vlEnableImageHwOutput = that.vlEnableImageHwOutput;
				}
				if (that.maxCompletionTokens != null) {
					this.maxCompletionTokens = that.maxCompletionTokens;
				}
				if (that.seed != null) {
					this.seed = that.seed;
				}
				if (that.incrementalOutput != null) {
					this.incrementalOutput = that.incrementalOutput;
				}
				if (that.responseFormat != null) {
					this.responseFormat = that.responseFormat;
				}
				if (that.resultFormat != null) {
					this.resultFormat = that.resultFormat;
				}
				if (that.logprobs != null) {
					this.logprobs = that.logprobs;
				}
				if (that.topLogprobs != null) {
					this.topLogprobs = that.topLogprobs;
				}
				if (that.n != null) {
					this.n = that.n;
				}
				if (that.stop != null) {
					this.stop = that.stop;
				}
				if (that.tools != null) {
                    if (this.tools == null) {
                        this.tools = new ArrayList<>(that.tools);
                    }
                    else {
                        List<Tool> merged = new ArrayList<>(this.tools);
                        merged.addAll(that.tools);
                        this.tools = merged;
                    }
				}
				if (that.toolChoice != null) {
					this.toolChoice = that.toolChoice;
				}
				if (that.parallelToolCalls != null) {
					this.parallelToolCalls = that.parallelToolCalls;
				}
				if (that.enableSearch != null) {
					this.enableSearch = that.enableSearch;
				}
				if (that.searchOptions != null) {
					this.searchOptions = that.searchOptions;
				}
				if (that.dataInspection != null) {
					this.dataInspection = that.dataInspection;
				}
				if (that.skill != null) {
                    if (this.skill == null) {
                        this.skill = new ArrayList<>(that.skill);
                    }
                    else {
                        List<Skill> merged = new ArrayList<>(this.skill);
                        merged.addAll(that.skill);
                        this.skill = merged;
                    }
				}
				if (that.httpHeaders != null) {
                    if (this.httpHeaders == null) {
                        this.httpHeaders = new HashMap<>(that.httpHeaders);
                    }
                    else {
                        Map<String, String> merged = new HashMap<>(this.httpHeaders);
                        merged.putAll(that.httpHeaders);
                        this.httpHeaders = merged;
                    }
				}
				if (that.multiModel != null) {
					this.multiModel = that.multiModel;
				}
			}
			return self();
		}

		@Override
		public DashScopeChatOptions build() {
			return new DashScopeChatOptions(this.model, this.stream, this.temperature, this.topP, this.topK,
					this.enableThinking, this.preserveThinking, this.thinkingBudget, this.reasoningEffort,
					this.toolStream, this.enableCodeInterpreter, this.repetitionPenalty, this.presencePenalty,
					this.vlHighResolutionImages, this.vlEnableImageHwOutput, this.maxCompletionTokens, this.seed,
					this.incrementalOutput, this.responseFormat, this.resultFormat, this.logprobs, this.topLogprobs,
					this.n, this.stop, this.tools, this.toolChoice, this.parallelToolCalls, this.enableSearch,
					this.searchOptions, this.dataInspection, this.skill, this.httpHeaders, this.toolCallbacks,
					this.multiModel, this.toolContext);
		}

	}

}
