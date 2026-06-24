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

import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jspecify.annotations.NullUnmarked;
import org.jspecify.annotations.Nullable;

import org.springframework.ai.util.JsonHelper;

/**
 * DashScope Chat REST API specification.
 *
 * @author yingzi
 * @since 2026/6/2
 */
@NullUnmarked
public class DashScopeChatApiSpec {

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class ChatCompletionRequest {

		@JsonProperty("model")
		private String model;

		@JsonProperty("input")
		private Input input;

		@JsonProperty("parameters")
		private Parameters parameters;

		private ChatCompletionRequest() {
		}

		public String model() {
			return this.model;
		}

		public Input input() {
			return this.input;
		}

		public Parameters parameters() {
			return this.parameters;
		}

		public static Builder builder() {
			return new Builder();
		}

		public static final class Builder {

			private String model;

			private Input input;

			private Parameters parameters;

			private Builder() {
			}

			public Builder model(String model) {
				this.model = model;
				return this;
			}

			public Builder input(Input input) {
				this.input = input;
				return this;
			}

			public Builder parameters(Parameters parameters) {
				this.parameters = parameters;
				return this;
			}

			public ChatCompletionRequest build() {
				ChatCompletionRequest request = new ChatCompletionRequest();
				request.model = this.model;
				request.input = this.input;
				request.parameters = this.parameters;
				return request;
			}

		}

		@JsonInclude(JsonInclude.Include.NON_NULL)
		public static class Input {

			@JsonProperty("messages")
			private List<ChatCompletionMessage> messages;

			public Input() {
			}

			public Input(List<ChatCompletionMessage> messages) {
				this.messages = messages;
			}

			public List<ChatCompletionMessage> messages() {
				return this.messages;
			}

			@JsonInclude(JsonInclude.Include.NON_NULL)
			public static class ChatCompletionMessage {

				@JsonProperty("role")
				private Role role;

				@JsonProperty("content")
				private Object content;

				@JsonProperty("partial")
				private Boolean partial;

				@JsonProperty("tool_calls")
				private List<ToolCall> toolCalls;

				@JsonProperty("tool_call_id")
				private String toolCallId;

				private ChatCompletionMessage() {
				}

				public Role role() {
					return this.role;
				}

				public Object content() {
					return this.content;
				}

				public Object rawContent() {
					return this.content;
				}

				public Boolean partial() {
					return this.partial;
				}

				public List<ToolCall> toolCalls() {
					return this.toolCalls;
				}

				public String toolCallId() {
					return this.toolCallId;
				}

				public static Builder builder() {
					return new Builder();
				}

				public static final class Builder {

					private Role role;

					private Object content;

					private Boolean partial;

					private List<ToolCall> toolCalls;

					private String toolCallId;

					private Builder() {
					}

					public Builder role(Role role) {
						this.role = role;
						return this;
					}

					public Builder content(Object content) {
						this.content = content;
						return this;
					}

					public Builder partial(Boolean partial) {
						this.partial = partial;
						return this;
					}

					public Builder toolCalls(List<ToolCall> toolCalls) {
						this.toolCalls = toolCalls;
						return this;
					}

					public Builder toolCallId(String toolCallId) {
						this.toolCallId = toolCallId;
						return this;
					}

					public ChatCompletionMessage build() {
						ChatCompletionMessage message = new ChatCompletionMessage();
						message.role = this.role;
						message.content = this.content;
						message.partial = this.partial;
						message.toolCalls = this.toolCalls;
						message.toolCallId = this.toolCallId;
						return message;
					}

				}

			}

		}

		@JsonInclude(JsonInclude.Include.NON_NULL)
		public static class Parameters {

			@JsonProperty("temperature")
			private Float temperature;

			@JsonProperty("top_p")
			private Float topP;

			@JsonProperty("top_k")
			private Integer topK;

			@JsonProperty("enable_thinking")
			private Boolean enableThinking;

			@JsonProperty("preserve_thinking")
			private Boolean preserveThinking;

			@JsonProperty("thinking_budget")
			private Integer thinkingBudget;

			@JsonProperty("reasoning_effort")
			private String reasoningEffort;

			@JsonProperty("tool_stream")
			private Boolean toolStream;

			@JsonProperty("enable_code_interpreter")
			private Boolean enableCodeInterpreter;

			@JsonProperty("repetition_penalty")
			private Float repetitionPenalty;

			@JsonProperty("presence_penalty")
			private Float presencePenalty;

			@JsonProperty("vl_high_resolution_images")
			private Boolean vlHighResolutionImages;

			@JsonProperty("vl_enable_image_hw_output")
			private Boolean vlEnableImageHwOutput;

			@JsonProperty("max_completion_tokens")
			private Integer maxCompletionTokens;

			@JsonProperty("seed")
			private Integer seed;

			@JsonProperty("incremental_output")
			private Boolean incrementalOutput;

			@JsonProperty("response_format")
			private ResponseFormat responseFormat;

			@JsonProperty("result_format")
			private String resultFormat;

			@JsonProperty("logprobs")
			private Boolean logprobs;

			@JsonProperty("top_logprobs")
			private Integer topLogprobs;

			@JsonProperty("n")
			private Integer n;

			@JsonProperty("stop")
			private Object stop;

			@JsonProperty("tools")
			private List<Tool> tools;

			@JsonProperty("tool_choice")
			private Object toolChoice;

			@JsonProperty("parallel_tool_calls")
			private Boolean parallelToolCalls;

			@JsonProperty("enable_search")
			private Boolean enableSearch;

			@JsonProperty("search_options")
			private SearchOptions searchOptions;

			@JsonProperty("skill")
			private List<Skill> skill;

			private Map<String, Object> extraBody = Map.of();

			private Parameters() {
			}

			public Float temperature() {
				return this.temperature;
			}

			public Float topP() {
				return this.topP;
			}

			public Integer topK() {
				return this.topK;
			}

			public Boolean enableThinking() {
				return this.enableThinking;
			}

			public Boolean preserveThinking() {
				return this.preserveThinking;
			}

			public Integer thinkingBudget() {
				return this.thinkingBudget;
			}

			public String reasoningEffort() {
				return this.reasoningEffort;
			}

			public Boolean toolStream() {
				return this.toolStream;
			}

			public Boolean enableCodeInterpreter() {
				return this.enableCodeInterpreter;
			}

			public Float repetitionPenalty() {
				return this.repetitionPenalty;
			}

			public Float presencePenalty() {
				return this.presencePenalty;
			}

			public Boolean vlHighResolutionImages() {
				return this.vlHighResolutionImages;
			}

			public Boolean vlEnableImageHwOutput() {
				return this.vlEnableImageHwOutput;
			}

			public Integer maxCompletionTokens() {
				return this.maxCompletionTokens;
			}

			public Integer seed() {
				return this.seed;
			}

			public Boolean incrementalOutput() {
				return this.incrementalOutput;
			}

			public ResponseFormat responseFormat() {
				return this.responseFormat;
			}

			public String resultFormat() {
				return this.resultFormat;
			}

			public Boolean logprobs() {
				return this.logprobs;
			}

			public Integer topLogprobs() {
				return this.topLogprobs;
			}

			public Integer n() {
				return this.n;
			}

			public Object stop() {
				return this.stop;
			}

			public List<Tool> tools() {
				return this.tools;
			}

			public Object toolChoice() {
				return this.toolChoice;
			}

			public Boolean parallelToolCalls() {
				return this.parallelToolCalls;
			}

			public Boolean enableSearch() {
				return this.enableSearch;
			}

			public SearchOptions searchOptions() {
				return this.searchOptions;
			}

			public List<Skill> skill() {
				return this.skill;
			}

			@JsonAnyGetter
			public Map<String, Object> extraBody() {
				return this.extraBody;
			}

			public static Builder builder() {
				return new Builder();
			}

			public static final class Builder {

				private Float temperature;

				private Float topP;

				private Integer topK;

				private Boolean enableThinking;

				private Boolean preserveThinking;

				private Integer thinkingBudget;

				private String reasoningEffort;

				private Boolean toolStream;

				private Boolean enableCodeInterpreter;

				private Float repetitionPenalty;

				private Float presencePenalty;

				private Boolean vlHighResolutionImages;

				private Boolean vlEnableImageHwOutput;

				private Integer maxTokens;

				private Integer maxCompletionTokens;

				private Integer seed;

				private Boolean incrementalOutput;

				private ResponseFormat responseFormat;

				private String resultFormat;

				private Boolean logprobs;

				private Integer topLogprobs;

				private Integer n;

				private Object stop;

				private List<Tool> tools;

				private Object toolChoice;

				private Boolean parallelToolCalls;

				private Boolean enableSearch;

				private SearchOptions searchOptions;

				private List<Skill> skill;

				private @Nullable Map<String, Object> extraBody;

				private Builder() {
				}

				public Builder temperature(Float temperature) {
					this.temperature = temperature;
					return this;
				}

				public Builder topP(Float topP) {
					this.topP = topP;
					return this;
				}

				public Builder topK(Integer topK) {
					this.topK = topK;
					return this;
				}

				public Builder enableThinking(Boolean enableThinking) {
					this.enableThinking = enableThinking;
					return this;
				}

				public Builder preserveThinking(Boolean preserveThinking) {
					this.preserveThinking = preserveThinking;
					return this;
				}

				public Builder thinkingBudget(Integer thinkingBudget) {
					this.thinkingBudget = thinkingBudget;
					return this;
				}

				public Builder reasoningEffort(String reasoningEffort) {
					this.reasoningEffort = reasoningEffort;
					return this;
				}

				public Builder toolStream(Boolean toolStream) {
					this.toolStream = toolStream;
					return this;
				}

				public Builder enableCodeInterpreter(Boolean enableCodeInterpreter) {
					this.enableCodeInterpreter = enableCodeInterpreter;
					return this;
				}

				public Builder repetitionPenalty(Float repetitionPenalty) {
					this.repetitionPenalty = repetitionPenalty;
					return this;
				}

				public Builder presencePenalty(Float presencePenalty) {
					this.presencePenalty = presencePenalty;
					return this;
				}

				public Builder vlHighResolutionImages(Boolean vlHighResolutionImages) {
					this.vlHighResolutionImages = vlHighResolutionImages;
					return this;
				}

				public Builder vlEnableImageHwOutput(Boolean vlEnableImageHwOutput) {
					this.vlEnableImageHwOutput = vlEnableImageHwOutput;
					return this;
				}

				public Builder maxCompletionTokens(Integer maxCompletionTokens) {
					this.maxCompletionTokens = maxCompletionTokens;
					return this;
				}

				public Builder seed(Integer seed) {
					this.seed = seed;
					return this;
				}

				public Builder incrementalOutput(Boolean incrementalOutput) {
					this.incrementalOutput = incrementalOutput;
					return this;
				}

				public Builder responseFormat(ResponseFormat responseFormat) {
					this.responseFormat = responseFormat;
					return this;
				}

				public Builder resultFormat(String resultFormat) {
					this.resultFormat = resultFormat;
					return this;
				}

				public Builder logprobs(Boolean logprobs) {
					this.logprobs = logprobs;
					return this;
				}

				public Builder topLogprobs(Integer topLogprobs) {
					this.topLogprobs = topLogprobs;
					return this;
				}

				public Builder n(Integer n) {
					this.n = n;
					return this;
				}

				public Builder stop(Object stop) {
					this.stop = stop;
					return this;
				}

				public Builder tools(List<Tool> tools) {
					this.tools = tools;
					return this;
				}

				public Builder toolChoice(Object toolChoice) {
					this.toolChoice = toolChoice;
					return this;
				}

				public Builder parallelToolCalls(Boolean parallelToolCalls) {
					this.parallelToolCalls = parallelToolCalls;
					return this;
				}

				public Builder enableSearch(Boolean enableSearch) {
					this.enableSearch = enableSearch;
					return this;
				}

				public Builder searchOptions(SearchOptions searchOptions) {
					this.searchOptions = searchOptions;
					return this;
				}

				public Builder skill(List<Skill> skill) {
					this.skill = skill;
					return this;
				}

				public Builder extraBody(@Nullable Map<String, Object> extraBody) {
					this.extraBody = extraBody;
					return this;
				}

				public Parameters build() {
					Parameters parameters = new Parameters();
					parameters.temperature = this.temperature;
					parameters.topP = this.topP;
					parameters.topK = this.topK;
					parameters.enableThinking = this.enableThinking;
					parameters.preserveThinking = this.preserveThinking;
					parameters.thinkingBudget = this.thinkingBudget;
					parameters.reasoningEffort = this.reasoningEffort;
					parameters.toolStream = this.toolStream;
					parameters.enableCodeInterpreter = this.enableCodeInterpreter;
					parameters.repetitionPenalty = this.repetitionPenalty;
					parameters.presencePenalty = this.presencePenalty;
					parameters.vlHighResolutionImages = this.vlHighResolutionImages;
					parameters.vlEnableImageHwOutput = this.vlEnableImageHwOutput;
					parameters.maxCompletionTokens = this.maxCompletionTokens;
					parameters.seed = this.seed;
					parameters.incrementalOutput = this.incrementalOutput;
					parameters.responseFormat = this.responseFormat;
					parameters.resultFormat = this.resultFormat;
					parameters.logprobs = this.logprobs;
					parameters.topLogprobs = this.topLogprobs;
					parameters.n = this.n;
					parameters.stop = this.stop;
					parameters.tools = this.tools;
					parameters.toolChoice = this.toolChoice;
					parameters.parallelToolCalls = this.parallelToolCalls;
					parameters.enableSearch = this.enableSearch;
					parameters.searchOptions = this.searchOptions;
					parameters.skill = this.skill;
					parameters.extraBody = this.extraBody != null ? this.extraBody : Map.of();
					return parameters;
				}

			}

			@JsonInclude(JsonInclude.Include.NON_NULL)
			public static class ResponseFormat {

				@JsonProperty("type")
				private Type type = Type.TEXT;

				@JsonProperty("json_schema")
				private @Nullable JsonSchemaConfig jsonScheme;

				public ResponseFormat() {
				}

				public Type getType() {
					return this.type;
				}

				public void setType(Type type) {
					this.type = type;
				}

				public @Nullable JsonSchemaConfig getJsonScheme() {
					return this.jsonScheme;
				}

				public void setJsonScheme(@Nullable JsonSchemaConfig jsonScheme) {
					this.jsonScheme = jsonScheme;
				}

				public static ResponseFormat.Builder builder() {
					return new ResponseFormat.Builder();
				}

				@Override
				public String toString() {
					return new JsonHelper().toJson(this);
				}

				@Override
				public boolean equals(Object o) {
					if (this == o) {
						return true;
					}
					if (o == null || getClass() != o.getClass()) {
						return false;
					}
					ResponseFormat that = (ResponseFormat) o;
					return this.type == that.type && Objects.equals(this.jsonScheme, that.jsonScheme);
				}

				@Override
				public int hashCode() {
					return Objects.hash(this.type, this.jsonScheme);
				}

				public static class Builder {

					private Type type = Type.TEXT;

					private @Nullable JsonSchemaConfig jsonScheme;

					public Builder type(Type type) {
						this.type = type;
						return this;
					}

					public Builder jsonScheme(@Nullable JsonSchemaConfig jsonScheme) {
						this.jsonScheme = jsonScheme;
						return this;
					}

					public ResponseFormat build() {
						ResponseFormat responseFormat = new ResponseFormat();
						responseFormat.setType(this.type);
						responseFormat.setJsonScheme(this.jsonScheme);
						return responseFormat;
					}

				}

				public enum Type {

					@JsonProperty("text")
					TEXT,

					@JsonProperty("json_object")
					JSON_OBJECT,

					@JsonProperty("json_schema")
					JSON_SCHEMA

				}

				@JsonInclude(JsonInclude.Include.NON_NULL)
				public static class JsonSchemaConfig {

					@JsonProperty("name")
					private @Nullable String name;

					@JsonProperty("description")
					private @Nullable String description;

					@JsonProperty("schema")
					private @Nullable Object schema;

					@JsonProperty("strict")
					private Boolean strict = false;

					public @Nullable String getName() {
						return this.name;
					}

					public void setName(@Nullable String name) {
						this.name = name;
					}

					public @Nullable String getDescription() {
						return this.description;
					}

					public void setDescription(@Nullable String description) {
						this.description = description;
					}

					public @Nullable Object getSchema() {
						return this.schema;
					}

					public void setSchema(@Nullable Object schema) {
						this.schema = schema;
					}

					public Boolean getStrict() {
						return this.strict;
					}

					public void setStrict(Boolean strict) {
						this.strict = strict;
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
						JsonSchemaConfig that = (JsonSchemaConfig) o;
						return Objects.equals(this.name, that.name)
								&& Objects.equals(this.description, that.description)
								&& Objects.equals(this.schema, that.schema)
								&& Objects.equals(this.strict, that.strict);
					}

					@Override
					public int hashCode() {
						return Objects.hash(this.name, this.description, this.schema, this.strict);
					}

					public static class Builder {

						private @Nullable String name;

						private @Nullable String description;

						private @Nullable Object schema;

						private Boolean strict = false;

						public Builder name(@Nullable String name) {
							this.name = name;
							return this;
						}

						public Builder description(@Nullable String description) {
							this.description = description;
							return this;
						}

						public Builder schema(@Nullable Object schema) {
							this.schema = schema;
							return this;
						}

						public Builder strict(Boolean strict) {
							this.strict = strict;
							return this;
						}

						public JsonSchemaConfig build() {
							JsonSchemaConfig config = new JsonSchemaConfig();
							config.setName(this.name);
							config.setDescription(this.description);
							config.setSchema(this.schema);
							config.setStrict(this.strict);
							return config;
						}

					}

				}

			}

			@JsonInclude(JsonInclude.Include.NON_NULL)
			public record Tool(@JsonProperty("type") String type, @JsonProperty("function") Function function) {
			}

			@JsonInclude(JsonInclude.Include.NON_NULL)
			public record Function(@JsonProperty("name") String name, @JsonProperty("description") String description,
					@JsonProperty("parameters") Map<String, Object> parameters) {
			}

			@JsonInclude(JsonInclude.Include.NON_NULL)
			public record SearchOptions(@JsonProperty("enable_source") Boolean enableSource,
					@JsonProperty("enable_citation") Boolean enableCitation,
					@JsonProperty("citation_format") String citationFormat,
					@JsonProperty("search_strategy") String searchStrategy,
					@JsonProperty("enable_search_extension") Boolean enableSearchExtension,
					@JsonProperty("prepend_search_result") Boolean prependSearchResult) {

				public static Builder builder() {
					return new Builder();
				}

				public static final class Builder {

					private Boolean enableSource;

					private Boolean enableCitation;

					private String citationFormat;

					private String searchStrategy;

					private Boolean enableSearchExtension;

					private Boolean prependSearchResult;

					private Builder() {
					}

					public Builder enableSource(Boolean enableSource) {
						this.enableSource = enableSource;
						return this;
					}

					public Builder enableCitation(Boolean enableCitation) {
						this.enableCitation = enableCitation;
						return this;
					}

					public Builder citationFormat(String citationFormat) {
						this.citationFormat = citationFormat;
						return this;
					}

					public Builder searchStrategy(String searchStrategy) {
						this.searchStrategy = searchStrategy;
						return this;
					}

					public Builder enableSearchExtension(Boolean enableSearchExtension) {
						this.enableSearchExtension = enableSearchExtension;
						return this;
					}

					public Builder prependSearchResult(Boolean prependSearchResult) {
						this.prependSearchResult = prependSearchResult;
						return this;
					}

					public SearchOptions build() {
						return new SearchOptions(this.enableSource, this.enableCitation, this.citationFormat,
								this.searchStrategy, this.enableSearchExtension, this.prependSearchResult);
					}

				}

			}

			@JsonInclude(JsonInclude.Include.NON_NULL)
			public record Skill(@JsonProperty("type") String type, @JsonProperty("mode") String mode,
					@JsonProperty("template_id") String templateId) {
			}

		}

	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public record ChatCompletion(@JsonProperty("request_id") @Nullable String requestId,
			@JsonProperty("output") @Nullable ChatCompletionOutput output,
			@JsonProperty("usage") @Nullable TokenUsage usage) {
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public record ChatCompletionOutput(@JsonProperty("text") @Nullable String text,
			@JsonProperty("choices") List<Choice> choices, @JsonProperty("search_info") @Nullable SearchInfo searchInfo) {

		@JsonInclude(JsonInclude.Include.NON_NULL)
		public record Choice(@JsonProperty("finish_reason") @Nullable ChatCompletionFinishReason finishReason,
				@JsonProperty("message") @Nullable ChatCompletionMessage message,
				@JsonProperty("logprobs") @Nullable ChatCompletionLogprobs logprobs,
				@JsonProperty("index") @Nullable Integer index) {
		}

	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public record SearchInfo(@JsonProperty("search_results") List<SearchResult> searchResults,
			@JsonProperty("extra_tool_info") List<ExtraToolInfo> extraToolInfo) {
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public record ExtraToolInfo(@JsonProperty("result") String result, @JsonProperty("tool") String tool) {
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public record SearchResult(@JsonProperty("site_name") String siteName, @JsonProperty("icon") String icon,
			@JsonProperty("index") Integer index, @JsonProperty("title") String title, @JsonProperty("url") String url) {
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public record ChatCompletionLogprobs(@JsonProperty("content") List<TokenInfo> content) {
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public record TokenInfo(@JsonProperty("token") String token, @JsonProperty("bytes") byte[] bytes,
			@JsonProperty("logprob") Float logprob, @JsonProperty("top_logprobs") List<TopLogprobs> topLogprobs) {
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public record TopLogprobs(@JsonProperty("token") String token, @JsonProperty("bytes") byte[] bytes,
			@JsonProperty("logprob") Float logprob) {
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public record ChatCompletionAnnotations(@JsonProperty("type") String type, @JsonProperty("language") String language,
			@JsonProperty("emotion") String emotion) {
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public record ChatCompletionMessage(@JsonProperty("content") @Nullable Object rawContent,
			@JsonProperty("role") Role role, @JsonProperty("name") @Nullable String name,
			@JsonProperty("tool_call_id") @Nullable String toolCallId,
			@JsonProperty("tool_calls") @Nullable List<ToolCall> toolCalls,
			@JsonProperty("reasoning_content") @Nullable String reasoningContent,
			@JsonProperty("partial") @Nullable Boolean partial, @JsonProperty("phase") @Nullable String phase,
			@JsonProperty("annotations") @Nullable List<ChatCompletionAnnotations> annotations,
			@JsonProperty("status") @Nullable String status) {

		public ChatCompletionMessage(Object content, Role role) {
			this(content, role, null, null, null, null, null, null, null, null);
		}

		public String content() {
			if (this.rawContent == null) {
				return "";
			}
			if (this.rawContent instanceof String text) {
				return text;
			}
			if (this.rawContent instanceof List<?> list) {
				if (list.isEmpty()) {
					return "";
				}
				Object object = list.get(0);
				if (object instanceof Map<?, ?> map) {
					if (map.isEmpty()) {
						return "";
					}
					Object audio = map.get("audio");
					if (audio instanceof Map<?, ?> audioMap && audioMap.get("data") != null) {
						return String.format("<audio>%s</audio>", audioMap.get("data"));
					}
					Object text = map.get("text");
					return text != null ? text.toString() : "";
				}
			}
			throw new IllegalStateException("The content is not valid!");
		}

	}

	public enum ChatCompletionFinishReason {

		@JsonProperty("null")
		NULL,

		@JsonProperty("stop")
		STOP,

		@JsonProperty("length")
		LENGTH,

		@JsonProperty("tool_calls")
		TOOL_CALLS

	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public record TokenUsage(@JsonProperty("output_tokens") Integer outputTokens,
			@JsonProperty("input_tokens") Integer inputTokens, @JsonProperty("total_tokens") Integer totalTokens,
			@JsonProperty("image_tokens") Integer imageTokens, @JsonProperty("video_tokens") Integer videoTokens,
			@JsonProperty("audio_tokens") Integer audioTokens, @JsonProperty("seconds") Integer seconds,
			@JsonProperty("input_tokens_details") InputTokenDetailed inputTokensDetails,
			@JsonProperty("output_tokens_details") OutputTokenDetailed outputTokensDetails,
			@JsonProperty("prompt_tokens_details") PromptTokenDetailed promptTokenDetailed) {
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public record InputTokenDetailed(@JsonProperty("text_tokens") Integer text,
			@JsonProperty("image_tokens") Integer image, @JsonProperty("audio_tokens") Integer audio) {
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public record OutputTokenDetailed(@JsonProperty("text_tokens") Integer text,
			@JsonProperty("reasoning_tokens") Integer image) {
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public record PromptTokenDetailed(@JsonProperty("cached_tokens") Integer cachedTokens,
			@JsonProperty("cache_creation") CacheCreation cacheCreation,
			@JsonProperty("cache_creation_input_tokens") Integer cacheCreationInputTokens,
			@JsonProperty("cache_type") String cacheType) {
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public record CacheCreation(@JsonProperty("ephemeral_5m_input_tokens") Integer ephemeral5mInputTokens) {
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public record ChatCompletionChunk(@JsonProperty("request_id") @Nullable String requestId,
			@JsonProperty("output") @Nullable ChatCompletionOutput output,
			@JsonProperty("usage") @Nullable TokenUsage usage, @Nullable Object o) {
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public record MediaContent(@JsonIgnore String type, @JsonProperty("text") @Nullable String text,
			@JsonProperty("image") @Nullable String image, @JsonProperty("video") @Nullable List<String> video,
			@JsonProperty("audio") @Nullable String audio,
			@JsonProperty("cache_control") @Nullable Map<String, String> cacheControl) {

		public MediaContent(String text) {
			this("text", text, null, null, null, null);
		}

		public MediaContent(String text, @Nullable Map<String, String> cacheControl) {
			this("text", text, null, null, null, cacheControl);
		}

		public MediaContent(String type, @Nullable String text, @Nullable String image, @Nullable List<String> video) {
			this(type, text, image, video, null, null);
		}

		public MediaContent(String type, @Nullable String text, @Nullable String image, @Nullable List<String> video,
				@Nullable String audio) {
			this(type, text, image, video, audio, null);
		}

		public static Builder builder() {
			return new Builder();
		}

		public static final class Builder {

			private @Nullable String type;

			private @Nullable String text;

			private @Nullable String image;

			private @Nullable List<String> video;

			private @Nullable String audio;

			private @Nullable Map<String, String> cacheControl;

			private Builder() {
			}

			public Builder text(@Nullable String text) {
				this.type = "text";
				this.text = text;
				return this;
			}

			public Builder image(@Nullable String image) {
				this.type = "image";
				this.image = image;
				return this;
			}

			public Builder video(@Nullable List<String> video) {
				this.type = "video";
				this.video = video;
				return this;
			}

			public Builder audio(@Nullable String audio) {
				this.type = "audio";
				this.audio = audio;
				return this;
			}

			public Builder cacheControl(@Nullable Map<String, String> cacheControl) {
				this.cacheControl = cacheControl;
				return this;
			}

			public MediaContent build() {
				return new MediaContent(this.type, this.text, this.image, this.video, this.audio, this.cacheControl);
			}

		}

	}

	public enum Role {

		@JsonProperty("system")
		SYSTEM,

		@JsonProperty("user")
		USER,

		@JsonProperty("assistant")
		ASSISTANT,

		@JsonProperty("tool")
		TOOL

	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public record ToolCall(@JsonProperty("id") String id, @JsonProperty("type") String type,
			@JsonProperty("function") ChatCompletionFunction function, @JsonProperty("index") Integer index) {
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public record ChatCompletionFunction(@JsonProperty("name") String name,
			@JsonProperty("arguments") String arguments) {
	}

}
