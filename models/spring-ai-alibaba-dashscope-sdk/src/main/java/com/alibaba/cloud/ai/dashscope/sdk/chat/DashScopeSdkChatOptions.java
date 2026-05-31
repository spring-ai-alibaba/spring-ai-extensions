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
import org.jspecify.annotations.Nullable;

import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.model.tool.DefaultToolCallingChatOptions;
import org.springframework.ai.model.tool.ToolCallingChatOptions;
import org.springframework.ai.tool.ToolCallback;

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
	private final @Nullable String model;

	@JsonIgnore
	private final @Nullable Boolean stream;

	@JsonProperty("temperature")
	private final @Nullable Double temperature;

	@JsonProperty("seed")
	private final @Nullable Integer seed;

	@JsonProperty("top_p")
	private final @Nullable Double topP;

	@JsonProperty("top_k")
	private final @Nullable Integer topK;

	@JsonProperty("stop")
	private final @Nullable List<Object> stop;

	@JsonProperty("enable_search")
	private final Boolean enableSearch;

	@JsonProperty("max_tokens")
	private final @Nullable Integer maxTokens;

	@JsonProperty("incremental_output")
	private final Boolean incrementalOutput;

	@JsonProperty("repetition_penalty")
	private final @Nullable Double repetitionPenalty;

	@JsonProperty("tool_choice")
	private final @Nullable Object toolChoice;

	@JsonIgnore
	private final Map<String, String> httpHeaders;

	@JsonIgnore
	private final List<ToolCallback> toolCallbacks;

	@JsonIgnore
	private final Set<String> toolNames;

	@JsonIgnore
	private final @Nullable Boolean internalToolExecutionEnabled;

	@JsonIgnore
	private final Map<String, Object> toolContext;

	@JsonProperty("extra_body")
	private final @Nullable Map<String, Object> extraBody;

	protected DashScopeSdkChatOptions(@Nullable String model, @Nullable Boolean stream, @Nullable Double temperature,
			@Nullable Integer seed, @Nullable Double topP, @Nullable Integer topK, @Nullable List<Object> stop,
			@Nullable Boolean enableSearch, @Nullable Integer maxTokens, @Nullable Boolean incrementalOutput,
			@Nullable Double repetitionPenalty, @Nullable Object toolChoice, @Nullable Map<String, String> httpHeaders,
			@Nullable List<ToolCallback> toolCallbacks, @Nullable Set<String> toolNames,
			@Nullable Boolean internalToolExecutionEnabled, @Nullable Map<String, Object> toolContext,
			@Nullable Map<String, Object> extraBody) {
		this.model = model;
		this.stream = stream;
		this.temperature = temperature;
		this.seed = seed;
		this.topP = topP;
		this.topK = topK;
		this.stop = stop != null ? new ArrayList<>(stop) : null;
		this.enableSearch = enableSearch != null ? enableSearch : false;
		this.maxTokens = maxTokens;
		this.incrementalOutput = incrementalOutput != null ? incrementalOutput : true;
		this.repetitionPenalty = repetitionPenalty;
		this.toolChoice = toolChoice;
		this.httpHeaders = httpHeaders != null ? new HashMap<>(httpHeaders) : new HashMap<>();
		this.toolCallbacks = toolCallbacks != null ? new ArrayList<>(toolCallbacks) : new ArrayList<>();
		this.toolNames = toolNames != null ? new HashSet<>(toolNames) : new HashSet<>();
		this.internalToolExecutionEnabled = internalToolExecutionEnabled;
		this.toolContext = toolContext != null ? new HashMap<>(toolContext) : new HashMap<>();
		this.extraBody = extraBody != null ? new HashMap<>(extraBody) : null;
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

	public Boolean getEnableSearch() {
		return this.enableSearch;
	}

	@Override
	public @Nullable Integer getMaxTokens() {
		return this.maxTokens;
	}

	public Boolean getIncrementalOutput() {
		return this.incrementalOutput;
	}

	public @Nullable Double getRepetitionPenalty() {
		return this.repetitionPenalty;
	}

	public @Nullable Object getToolChoice() {
		return this.toolChoice;
	}

	public Map<String, String> getHttpHeaders() {
		return this.httpHeaders;
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

	@Override
	public Map<String, Object> getToolContext() {
		return this.toolContext;
	}

	public @Nullable Map<String, Object> getExtraBody() {
		return this.extraBody;
	}

	@Override
	public @Nullable Double getFrequencyPenalty() {
		return null;
	}

	@Override
	public @Nullable Double getPresencePenalty() {
		return null;
	}

	@Override
	public @Nullable List<String> getStopSequences() {
		if (this.stop == null) {
			return null;
		}
		List<String> stopStrings = this.stop.stream().filter(String.class::isInstance).map(String.class::cast).toList();
		return stopStrings.isEmpty() ? null : stopStrings;
	}

	@Override
	public DashScopeSdkChatOptions copy() {
		return mutate().build();
	}

	@Override
	public Builder mutate() {
		return builder()
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
				.httpHeaders(new HashMap<>(this.httpHeaders))
				.toolCallbacks(new ArrayList<>(this.toolCallbacks))
				.toolNames(new HashSet<>(this.toolNames))
				.internalToolExecutionEnabled(this.internalToolExecutionEnabled)
				.toolContext(new HashMap<>(this.toolContext))
				.extraBody(this.extraBody);
	}

    public static DashScopeSdkChatOptions fromOptions(DashScopeSdkChatOptions options) {
        return options.mutate().build();
    }

	public static Builder builder() {
		return new Builder();
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
				&& Objects.equals(this.toolChoice, that.toolChoice) && Objects.equals(this.httpHeaders, that.httpHeaders)
				&& Objects.equals(this.toolCallbacks, that.toolCallbacks)
				&& Objects.equals(this.toolNames, that.toolNames)
				&& Objects.equals(this.internalToolExecutionEnabled, that.internalToolExecutionEnabled)
				&& Objects.equals(this.toolContext, that.toolContext) && Objects.equals(this.extraBody, that.extraBody);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.model, this.stream, this.temperature, this.seed, this.topP, this.topK, this.stop,
				this.enableSearch, this.maxTokens, this.incrementalOutput, this.repetitionPenalty, this.toolChoice,
				this.httpHeaders, this.toolCallbacks, this.toolNames, this.internalToolExecutionEnabled,
				this.toolContext, this.extraBody);
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

	public static class Builder extends AbstractBuilder<Builder> {

	}

	protected abstract static class AbstractBuilder<B extends AbstractBuilder<B>>
			extends DefaultToolCallingChatOptions.Builder<B> {

		protected @Nullable Boolean stream;

		protected @Nullable Integer seed;

		protected @Nullable List<Object> stop;

		protected @Nullable Boolean enableSearch;

		protected @Nullable Boolean incrementalOutput;

		protected @Nullable Double repetitionPenalty;

		protected @Nullable Object toolChoice;

		protected Map<String, String> httpHeaders = new HashMap<>();

		protected @Nullable Map<String, Object> extraBody;

		@Override
		public B clone() {
			B copy = super.clone();
			copy.stop = this.stop != null ? new ArrayList<>(this.stop) : null;
			if (this.httpHeaders != null && !this.httpHeaders.isEmpty()) {
				copy.httpHeaders = new HashMap<>(this.httpHeaders);
			}
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

		public B maxTokens(@Nullable Integer maxTokens) {
			this.maxTokens = maxTokens;
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

		public B toolChoice(@Nullable Object toolChoice) {
			this.toolChoice = toolChoice;
			return self();
		}

		public B httpHeaders(@Nullable Map<String, String> httpHeaders) {
			this.httpHeaders = httpHeaders != null ? new HashMap<>(httpHeaders) : new HashMap<>();
			return self();
		}

		public B extraBody(@Nullable Map<String, Object> extraBody) {
			this.extraBody = extraBody;
			return self();
		}

		@Override
		public B toolCallbacks(@Nullable List<ToolCallback> toolCallbacks) {
			this.toolCallbacks = toolCallbacks != null ? new ArrayList<>(toolCallbacks) : new ArrayList<>();
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
			this.toolNames = toolNames != null ? new HashSet<>(toolNames) : new HashSet<>();
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
			}
			else {
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
					this.stop = that.stop;
				}
				if (that.enableSearch != null) {
					this.enableSearch = that.enableSearch;
				}
				if (that.incrementalOutput != null) {
					this.incrementalOutput = that.incrementalOutput;
				}
				if (that.repetitionPenalty != null) {
					this.repetitionPenalty = that.repetitionPenalty;
				}
				if (that.toolChoice != null) {
					this.toolChoice = that.toolChoice;
				}
				if (that.httpHeaders != null && !that.httpHeaders.isEmpty()) {
					this.httpHeaders = new HashMap<>(that.httpHeaders);
				}
				if (that.extraBody != null) {
                    this.extraBody = that.extraBody;
				}
			}
			return self();
		}

		@Override
		public DashScopeSdkChatOptions build() {
			return new DashScopeSdkChatOptions(this.model, this.stream, this.temperature, this.seed, this.topP,
					this.topK, this.stop, this.enableSearch, this.maxTokens, this.incrementalOutput,
					this.repetitionPenalty, this.toolChoice, this.httpHeaders, this.toolCallbacks, this.toolNames,
					this.internalToolExecutionEnabled, this.toolContext, this.extraBody);
		}

	}

}
