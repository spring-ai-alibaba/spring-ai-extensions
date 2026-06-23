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
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatApiSpec.ChatCompletion;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatApiSpec.ChatCompletionChunk;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatApiSpec.ChatCompletionFinishReason;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatApiSpec.ChatCompletionFunction;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatApiSpec.ChatCompletionMessage;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatApiSpec.ChatCompletionOutput;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatApiSpec.ChatCompletionOutput.Choice;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatApiSpec.ChatCompletionRequest;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatApiSpec.ChatCompletionRequest.Input;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatApiSpec.ChatCompletionRequest.Parameters;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatApiSpec.ChatCompletionRequest.Parameters.Function;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatApiSpec.ChatCompletionRequest.Parameters.Tool;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatApiSpec.MediaContent;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatApiSpec.Role;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatApiSpec.TokenUsage;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatApiSpec.ToolCall;
import com.alibaba.cloud.ai.dashscope.chat.observation.DashScopeChatModelObservationConvention;
import com.alibaba.cloud.ai.dashscope.common.DashScopeApiConstants;
import com.alibaba.cloud.ai.dashscope.common.DashScopeException;
import com.alibaba.cloud.ai.dashscope.metadata.DashScopeAiUsage;
import com.alibaba.cloud.ai.dashscope.spec.DashScopeModel;
import com.alibaba.cloud.ai.tool.validator.DefaultToolCallValidator;
import com.alibaba.cloud.ai.tool.validator.ToolCallValidator;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.contextpropagation.ObservationThreadLocalAccessor;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.ToolResponseMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.metadata.ChatGenerationMetadata;
import org.springframework.ai.chat.metadata.ChatResponseMetadata;
import org.springframework.ai.chat.metadata.EmptyUsage;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.model.MessageAggregator;
import org.springframework.ai.chat.observation.ChatModelObservationContext;
import org.springframework.ai.chat.observation.ChatModelObservationConvention;
import org.springframework.ai.chat.observation.ChatModelObservationDocumentation;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.retry.RetryUtils;
import org.springframework.ai.support.UsageCalculator;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.ai.util.JsonHelper;
import org.springframework.core.retry.RetryTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MimeType;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/**
 * {@link ChatModel} implementation for {@literal Alibaba DashScope} backed by
 * {@link Generation}.
 *
 * @author yuluo
 * @author yingzi
 */
public final class DashScopeChatModel implements ChatModel {

	private static final Logger logger = LoggerFactory.getLogger(DashScopeChatModel.class);

	public static final String DEFAULT_MODEL_NAME = DashScopeModel.ChatModel.QWEN_PLUS.getValue();

	private static final String DEFAULT_RESULT_FORMAT = "message";

	private static final ChatModelObservationConvention DEFAULT_OBSERVATION_CONVENTION = new DashScopeChatModelObservationConvention();

	private final DashScopeApi dashscopeApi;

	public final RetryTemplate retryTemplate;

	private final ObservationRegistry observationRegistry;

	private final ToolCallingManager toolCallingManager;

	private final ToolCallValidator toolCallingValidator;

	/**
	 * The default options used for the chat completion requests.
	 */
	private DashScopeChatOptions defaultOptions;

	/**
	 * Conventions to use for generating observations.
	 */
	private ChatModelObservationConvention observationConvention = DEFAULT_OBSERVATION_CONVENTION;

	public DashScopeChatModel(DashScopeApi dashScopeApi, DashScopeChatOptions defaultOptions,
			ToolCallingManager toolCallingManager, RetryTemplate retryTemplate, ObservationRegistry observationRegistry) {

		this(dashScopeApi, defaultOptions, toolCallingManager, retryTemplate, observationRegistry, new DefaultToolCallValidator());
	}

	public DashScopeChatModel(DashScopeApi dashScopeApi, DashScopeChatOptions defaultOptions,
			ToolCallingManager toolCallingManager, RetryTemplate retryTemplate, ObservationRegistry observationRegistry, ToolCallValidator toolCallingValidator) {

		Assert.notNull(dashScopeApi, "dashscopeApi cannot be null");
		Assert.notNull(defaultOptions, "defaultOptions cannot be null");
		Assert.notNull(toolCallingManager, "toolCallingManager cannot be null");
		Assert.notNull(retryTemplate, "retryTemplate cannot be null");
		Assert.notNull(observationRegistry, "observationRegistry cannot be null");
		Assert.notNull(toolCallingValidator, "toolCallingValidator cannot be null");

		this.dashscopeApi = dashScopeApi;
		this.defaultOptions = defaultOptions;
		this.toolCallingManager = toolCallingManager;
		this.retryTemplate = retryTemplate;
		this.observationRegistry = observationRegistry;
		this.toolCallingValidator = toolCallingValidator;
	}

	@Override
	public ChatResponse call(Prompt prompt) {
		Assert.notNull(prompt, "Prompt must not be null");
		Assert.isTrue(!CollectionUtils.isEmpty(prompt.getInstructions()), "Prompt messages must not be empty");
		Prompt requestPrompt = buildRequestPrompt(prompt);
		return internalCall(requestPrompt, null);
	}

	public ChatResponse internalCall(Prompt prompt, @Nullable ChatResponse previousChatResponse) {
		ChatCompletionRequest request = createRequest(prompt);

        ChatModelObservationContext observationContext = ChatModelObservationContext.builder()
			.prompt(prompt)
			.provider(DashScopeApiConstants.PROVIDER_NAME)
			.build();

		ChatResponse response = ChatModelObservationDocumentation.CHAT_MODEL_OPERATION
			.observation(this.observationConvention, DEFAULT_OBSERVATION_CONVENTION, () -> observationContext,
					this.observationRegistry)
			.observe(() -> {
				ResponseEntity<ChatCompletion> completionEntity = RetryUtils.execute(this.retryTemplate,
						() -> this.dashscopeApi.chatCompletionEntity(request, getAdditionalHttpHeaders(prompt), isMultiModel(prompt.getOptions())));

				ChatCompletion chatCompletion = completionEntity.getBody();
				ChatResponse chatResponse = toChatResponse(chatCompletion, previousChatResponse, request, null);
				observationContext.setResponse(chatResponse);
				return chatResponse;
				});

		return response;
	}

	@Override
	public ChatOptions getOptions() {
		return this.defaultOptions;
	}

	private boolean isMultiModel(@Nullable ChatOptions options) {
		return options instanceof DashScopeChatOptions
				&& Boolean.TRUE.equals(((DashScopeChatOptions) options).getMultiModel());
	}

	@Override
	public Flux<ChatResponse> stream(Prompt prompt) {
		Assert.notNull(prompt, "Prompt must not be null");
		Assert.isTrue(!CollectionUtils.isEmpty(prompt.getInstructions()), "Prompt messages must not be empty");
		Prompt requestPrompt = buildRequestPrompt(prompt);
		return this.internalStream(requestPrompt, null);
	}

	public Flux<ChatResponse> internalStream(Prompt prompt, @Nullable ChatResponse previousChatResponse) {

		return Flux.deferContextual(contextView -> {
			Prompt streamPrompt = withStreamDefaults(prompt);
			ChatCompletionRequest request = createRequest(streamPrompt);
			Flux<ChatCompletionChunk> completionChunks = RetryUtils.execute(this.retryTemplate,
					() -> this.dashscopeApi.chatCompletionStream(request, getAdditionalHttpHeaders(streamPrompt),
							isMultiModel(streamPrompt.getOptions())));

			ConcurrentHashMap<String, String> roleMap = new ConcurrentHashMap<>();

			ChatModelObservationContext observationContext = ChatModelObservationContext.builder()
				.prompt(streamPrompt)
				.provider(DashScopeApiConstants.PROVIDER_NAME)
				.build();

			Observation observation = ChatModelObservationDocumentation.CHAT_MODEL_OPERATION
				.observation(this.observationConvention, DEFAULT_OBSERVATION_CONVENTION, () -> observationContext,
						this.observationRegistry);

			observation.parentObservation(contextView.getOrDefault(ObservationThreadLocalAccessor.KEY, null)).start();

			Flux<ChatResponse> chatResponse = completionChunks.map(this::chunkToChatCompletion)
				.switchMap(chatCompletion -> Mono.just(chatCompletion)
					.map(chatCompletion2 -> toChatResponse(chatCompletion2, previousChatResponse, request, roleMap)));

			Flux<ChatResponse> flux = chatResponse.doOnError(observation::error)
				.doFinally(s -> observation.stop())
				.contextWrite(ctx -> ctx.put(ObservationThreadLocalAccessor.KEY, observation));

			return new MessageAggregator().aggregate(flux, observationContext::setResponse);
		});
	}

	private Prompt withStreamDefaults(Prompt prompt) {
		if (prompt.getOptions() instanceof DashScopeChatOptions options && options.getIncrementalOutput() == null) {
			return new Prompt(prompt.getInstructions(), options.mutate().incrementalOutput(true).build());
		}
		return prompt;
	}

	private static String finishReasonToMetadataValue(@Nullable ChatCompletionFinishReason finishReason) {
		if (finishReason == null || finishReason == ChatCompletionFinishReason.NULL) {
			return "";
		}
		return finishReason.name();
	}

	private ChatResponse toChatResponse(@Nullable ChatCompletion chatCompletion,
			@Nullable ChatResponse previousChatResponse, ChatCompletionRequest request,
			@Nullable ConcurrentHashMap<String, String> roleMap) {

		if (chatCompletion == null) {
			logger.warn("Null chat completion returned");
			return new ChatResponse(List.of());
		}

		TokenUsage usage = chatCompletion.usage();
		Usage currentChatResponseUsage = usage != null ? DashScopeAiUsage.from(usage) : new EmptyUsage();
		UsageCalculator.getCumulativeUsage(currentChatResponseUsage, previousChatResponse);

		ChatCompletionOutput output = chatCompletion.output();
		if (output == null) {
			logger.warn("No output returned");
			return new ChatResponse(List.of(), from(chatCompletion, currentChatResponseUsage));
		}

		List<Choice> choices = output.choices();
		if (choices == null || choices.isEmpty()) {
			logger.warn("No choices returned");
			return new ChatResponse(List.of(), from(chatCompletion, currentChatResponseUsage));
		}

		ConcurrentHashMap<String, String> finalRoleMap = roleMap == null ? new ConcurrentHashMap<>() : roleMap;

		List<Generation> generations = choices.stream().map(choice -> {
			ChatCompletionMessage choiceMessage = choice.message();
			String requestId = chatCompletion.requestId() != null ? chatCompletion.requestId() : "";

			if (choiceMessage != null && choiceMessage.role() != null && chatCompletion.requestId() != null) {
				finalRoleMap.putIfAbsent(chatCompletion.requestId(), choiceMessage.role().name());
			}

			Map<String, Object> metadata = Map.of("id", requestId, "role",
					requestId.isEmpty() ? "" : finalRoleMap.getOrDefault(requestId, ""), "finishReason",
					finishReasonToMetadataValue(choice.finishReason()), "reasoningContent",
					choiceMessage != null && StringUtils.hasText(choiceMessage.reasoningContent())
							? choiceMessage.reasoningContent() : "",
					"search_info", Objects.isNull(output.searchInfo()) ? "" : output.searchInfo());
			return buildGeneration(choice, metadata);
		}).toList();

		return new ChatResponse(generations, from(chatCompletion, currentChatResponseUsage));
	}

	public void setDashScopeChatOptions(DashScopeChatOptions options) {
		this.defaultOptions = options;
	}

	private Generation buildGeneration(Choice choice, Map<String, Object> metadata) {
		ChatCompletionMessage choiceMessage = choice.message();

		List<ToolCall> validatedToolCalls = List.of();
		if (choiceMessage != null && choice.finishReason() == ChatCompletionFinishReason.TOOL_CALLS
				&& !CollectionUtils.isEmpty(choiceMessage.toolCalls())) {
			validatedToolCalls = choiceMessage.toolCalls()
				.stream()
				.filter(toolCall -> toolCall != null && toolCall.function() != null
						&& StringUtils.hasText(toolCall.function().name()))
				.toList();
		}
		List<AssistantMessage.ToolCall> toolCalls = validatedToolCalls.stream()
			.map(toolCall -> new AssistantMessage.ToolCall(toolCall.id(), "function", toolCall.function().name(),
					toolCall.function().arguments()))
			.toList();

		String finishReason = finishReasonToMetadataValue(choice.finishReason());
		ChatGenerationMetadata generationMetadata = ChatGenerationMetadata.builder().finishReason(finishReason)
                .metadata(metadata)
                .build();

		AssistantMessage assistantMessage = AssistantMessage.builder()
			.content(choiceMessage != null ? choiceMessage.content() : "")
			.toolCalls(toolCalls)
			.build();

		return new Generation(assistantMessage, generationMetadata);
	}

	private ChatCompletion chunkToChatCompletion(ChatCompletionChunk chunk) {
		if (Objects.isNull(chunk.output())) {
			throw new DashScopeException("LLM response chunk is null.");
		}
		return new ChatCompletion(chunk.requestId(), chunk.output(), chunk.usage());
	}

	private ChatResponseMetadata from(ChatCompletion result, Usage usage) {
		Assert.notNull(result, "DashScopeAi ChatCompletionResult must not be null");
		return ChatResponseMetadata.builder()
			.id(Objects.requireNonNullElse(result.requestId(), ""))
			.usage(usage)
			.model("")
			.build();
	}

	/**
	 * Accessible for testing.
	 */
	ChatCompletionRequest createRequest(Prompt prompt) {
		DashScopeChatOptions requestOptions = (DashScopeChatOptions) prompt.getOptions();
		Assert.state(requestOptions != null, "requestOptions must not be null");

		List<ToolDefinition> toolDefinitions = this.toolCallingManager.resolveToolDefinitions(requestOptions);
		boolean multiModel = isMultiModel(requestOptions);

		return ChatCompletionRequest.builder()
			.model(requestOptions.getModel())
			.input(new Input(prompt.getInstructions()
				.stream()
				.map(message -> toDashScopeMessage(message, multiModel))
				.flatMap(List::stream)
				.toList()))
			.parameters(toDashScopeChatRequestParameter(requestOptions, toolDefinitions))
			.build();
	}

	private List<Input.ChatCompletionMessage> toDashScopeMessage(Message message, boolean multiModel) {
		if (message.getMessageType() == MessageType.USER || message.getMessageType() == MessageType.SYSTEM) {
			Object content = message.getText();
			Map<String, String> cacheControl = extractCacheControl(message);

			if (message instanceof UserMessage userMessage) {
				if (!ObjectUtils.isEmpty(userMessage.getMedia())) {
					content = convertMediaContent(userMessage, cacheControl);
				}
				else if (multiModel || cacheControl != null) {
					Assert.notNull(message.getText(), "Text must not be null");
					content = List.of(textMediaContent(message.getText(), cacheControl));
				}
			}
			else if (message instanceof SystemMessage && cacheControl != null) {
				Assert.notNull(message.getText(), "Text must not be null");
				content = List.of(textMediaContent(message.getText(), cacheControl));
			}

			return List.of(Input.ChatCompletionMessage.builder()
				.role(Role.valueOf(message.getMessageType().name()))
				.content(content)
				.build());
		}
		if (message.getMessageType() == MessageType.TOOL) {
			ToolResponseMessage toolMessage = (ToolResponseMessage) message;
			toolMessage.getResponses().forEach(response -> {
				Assert.isTrue(response.id() != null, "ToolResponseMessage must have an id");
				Assert.isTrue(response.name() != null, "ToolResponseMessage must have a name");
			});
			return toolMessage.getResponses()
				.stream()
				.map(tr -> Input.ChatCompletionMessage.builder()
					.content(tr.responseData())
					.role(Role.TOOL)
					.toolCallId(tr.id())
					.build())
				.toList();
		}
		if (message.getMessageType() == MessageType.ASSISTANT) {
			AssistantMessage assistantMessage = (AssistantMessage) message;
			List<ToolCall> toolCalls = null;
			if (!CollectionUtils.isEmpty(assistantMessage.getToolCalls())) {
				toolCalls = assistantMessage.getToolCalls().stream().map(toolCall -> {
					ChatCompletionFunction function = new ChatCompletionFunction(toolCall.name(), toolCall.arguments());
					return new ToolCall(toolCall.id(), toolCall.type(), function, null);
				}).toList();
			}
			Boolean partial = parsePartial(assistantMessage.getMetadata().get("partial"));

			return List.of(Input.ChatCompletionMessage.builder()
				.content(assistantMessage.getText())
				.role(Role.ASSISTANT)
				.toolCalls(toolCalls)
				.partial(partial)
				.build());
		}
		throw new IllegalArgumentException("Unsupported message type: " + message.getMessageType());
	}

	private static @Nullable Boolean parsePartial(@Nullable Object partial) {
		if (partial == null) {
			return null;
		}
		if (partial instanceof Boolean value) {
			return value;
		}
		if (partial instanceof String value) {
			return Boolean.valueOf(value);
		}
		throw new IllegalArgumentException("Unsupported partial metadata type: " + partial.getClass().getName());
	}

	private HttpHeaders getAdditionalHttpHeaders(Prompt prompt) {
        Map<String, String> headers = new HashMap<>();
		if (prompt.getOptions() instanceof DashScopeChatOptions chatOptions) {
            if (!CollectionUtils.isEmpty(chatOptions.getHttpHeaders())) {
                headers.putAll(chatOptions.getHttpHeaders());
            }
			if (StringUtils.hasText(chatOptions.getDataInspection())) {
				headers.put(DashScopeApiConstants.HEADER_DATAINSPECTION, chatOptions.getDataInspection());
			}
		}
		HttpHeaders httpHeaders = new HttpHeaders();
		headers.forEach(httpHeaders::add);
		return httpHeaders;
	}

	@SuppressWarnings("unchecked")
	private @Nullable Map<String, String> extractCacheControl(Message message) {
		if (message.getMetadata() == null) {
			return null;
		}
		Object cacheControlObj = message.getMetadata().get(DashScopeApiConstants.CACHE_CONTROL);
		if (cacheControlObj instanceof Map<?, ?> rawMap) {
			try {
				Map<String, String> cacheControl = new HashMap<>();
				for (Map.Entry<?, ?> entry : rawMap.entrySet()) {
					if (entry.getKey() instanceof String key && entry.getValue() instanceof String value) {
						cacheControl.put(key, value);
					}
				}
				if (!cacheControl.isEmpty()) {
					return cacheControl;
				}
			}
			catch (ClassCastException ex) {
				logger.warn("DashScopeChatModel extractCacheControl Invalid cache_control format in message metadata");
			}
		}
		return null;
	}

	private List<MediaContent> convertMediaContent(UserMessage message, @Nullable Map<String, String> cacheControl) {
		Assert.hasText(message.getText(), "User message text must not be empty");
		MessageFormat format = MessageFormat.IMAGE;
		if (message.getMetadata().get(DashScopeApiConstants.MESSAGE_FORMAT) instanceof MessageFormat messageFormat) {
			format = messageFormat;
		}

		List<MediaContent> contentList = new ArrayList<>();
		if (format == MessageFormat.VIDEO) {
			List<String> mediaList = message.getMedia()
				.stream()
				.map(media -> this.fromMediaData(media.getMimeType(), media.getData()))
				.toList();
			contentList.add(MediaContent.builder().video(mediaList).build());
			contentList.add(textMediaContent(message.getText(), cacheControl));
		}
		else if (format == MessageFormat.AUDIO) {
			contentList.addAll(message.getMedia()
				.stream()
				.map(media -> MediaContent.builder()
					.audio(this.fromMediaData(media.getMimeType(), media.getData()))
					.build())
				.toList());
			contentList.add(textMediaContent(message.getText(), cacheControl));
		}
		else {
			contentList.addAll(message.getMedia()
				.stream()
				.map(media -> MediaContent.builder()
					.image(this.fromMediaData(media.getMimeType(), media.getData()))
					.build())
				.toList());
			contentList.add(textMediaContent(message.getText(), cacheControl));
		}
		return contentList;
	}

	private MediaContent textMediaContent(String text, @Nullable Map<String, String> cacheControl) {
		return MediaContent.builder().text(text).cacheControl(cacheControl).build();
	}

	private String fromMediaData(MimeType mimeType, Object mediaContentData) {
		if (mediaContentData instanceof byte[] bytes) {
			return String.format("data:%s;base64,%s", mimeType, Base64.getEncoder().encodeToString(bytes));
		}
		if (mediaContentData instanceof String text) {
			return text;
		}
		throw new IllegalArgumentException(
				"Unsupported media data type: " + mediaContentData.getClass().getSimpleName());
	}

	private Parameters toDashScopeChatRequestParameter(DashScopeChatOptions options,
			List<ToolDefinition> toolDefinitions) {
		String resultFormat = options.getResultFormat();
		if (resultFormat == null && !Boolean.TRUE.equals(options.getMultiModel())
				&& CollectionUtils.isEmpty(options.getSkill())) {
			resultFormat = DEFAULT_RESULT_FORMAT;
		}

		return Parameters.builder()
			.temperature(toFloat(options.getTemperature()))
			.topP(toFloat(options.getTopP()))
			.topK(options.getTopK())
			.enableThinking(options.getEnableThinking())
			.preserveThinking(options.getPreserveThinking())
			.thinkingBudget(options.getThinkingBudget())
			.reasoningEffort(options.getReasoningEffort())
			.toolStream(options.getToolStream())
			.enableCodeInterpreter(options.getEnableCodeInterpreter())
			.repetitionPenalty(toFloat(options.getRepetitionPenalty()))
			.presencePenalty(toFloat(options.getPresencePenalty()))
			.vlHighResolutionImages(options.getVlHighResolutionImages())
			.vlEnableImageHwOutput(options.getVlEnableImageHwOutput())
			.maxCompletionTokens(options.getMaxCompletionTokens())
			.seed(options.getSeed())
			.incrementalOutput(options.getIncrementalOutput())
			.responseFormat(options.getResponseFormat())
			.resultFormat(resultFormat)
			.logprobs(options.getLogprobs())
			.topLogprobs(options.getTopLogprobs())
			.n(options.getN())
			.stop(options.getStop())
			.tools(resolveTools(options, toolDefinitions))
			.toolChoice(options.getToolChoice())
			.parallelToolCalls(options.getParallelToolCalls())
			.enableSearch(options.getEnableSearch())
			.searchOptions(options.getSearchOptions())
			.skill(options.getSkill())
			.extraBody(options.getExtraBody())
			.build();
	}

	private @Nullable Float toFloat(@Nullable Double value) {
		return value != null ? value.floatValue() : null;
	}

	private @Nullable List<Tool> resolveTools(DashScopeChatOptions options, List<ToolDefinition> toolDefinitions) {
		if (!CollectionUtils.isEmpty(toolDefinitions)) {
			return toolDefinitions.stream().map(this::toTool).toList();
		}
		return options.getTools();
	}

	private Tool toTool(ToolDefinition toolDefinition) {
		return new Tool("function", new Function(toolDefinition.name(), toolDefinition.description(),
				toParameterMap(toolDefinition.inputSchema())));
	}

	private Map<String, Object> toParameterMap(@Nullable String inputSchema) {
		if (!StringUtils.hasText(inputSchema)) {
			return Map.of();
		}
		try {
			return new JsonHelper().fromJsonToMap(inputSchema);
		}
		catch (RuntimeException ex) {
			logger.warn("Invalid tool input schema for DashScope chat request. Use empty schema instead.", ex);
			return Map.of();
		}
	}

    public void setObservationConvention(ChatModelObservationConvention observationConvention) {
        Assert.notNull(observationConvention, "observationConvention cannot be null");
        this.observationConvention = observationConvention;
    }

    @Override
    public String toString() {
        return "DashScopeChatModel [defaultOptions=" + this.defaultOptions + "]";
    }

	public Builder mutate() {
		return new Builder(this);
	}

	private Prompt buildRequestPrompt(Prompt prompt) {
		if (prompt.getOptions() == null) {
			return prompt.mutate().chatOptions(this.getOptions()).build();
		}
		else {
			return prompt;
		}
	}

	@Override
	public DashScopeChatModel clone() {
		return this.mutate().build();
	}

	public static Builder builder() {
		return new Builder();
	}

	public static final class Builder {

		private @Nullable DashScopeApi dashScopeApi;

		private DashScopeChatOptions defaultOptions = DashScopeChatOptions.builder().model(DEFAULT_MODEL_NAME).build();

		private RetryTemplate retryTemplate = RetryUtils.DEFAULT_RETRY_TEMPLATE;

		private ToolCallingManager toolCallingManager = ToolCallingManager.builder().build();

		private ToolCallValidator toolCallValidator = new DefaultToolCallValidator();

		private ObservationRegistry observationRegistry = ObservationRegistry.NOOP;

		private Builder() {
		}

		private Builder(DashScopeChatModel dashScopeChatModel) {
			this.dashScopeApi = dashScopeChatModel.dashscopeApi;
			this.defaultOptions = dashScopeChatModel.defaultOptions;
			this.toolCallingManager = dashScopeChatModel.toolCallingManager;
			this.retryTemplate = dashScopeChatModel.retryTemplate;
			this.observationRegistry = dashScopeChatModel.observationRegistry;
			this.toolCallValidator = dashScopeChatModel.toolCallingValidator;
		}

		public Builder dashScopeApi(@Nullable DashScopeApi dashScopeApi) {
			this.dashScopeApi = dashScopeApi;
			return this;
		}

		public Builder defaultOptions(DashScopeChatOptions defaultOptions) {
			this.defaultOptions = defaultOptions;
			return this;
		}

        public Builder toolCallingManager(ToolCallingManager toolCallingManager) {
            this.toolCallingManager = toolCallingManager;
            return this;
        }


        public Builder retryTemplate(RetryTemplate retryTemplate) {
			this.retryTemplate = retryTemplate;
			return this;
		}

		public Builder observationRegistry(ObservationRegistry observationRegistry) {
			this.observationRegistry = observationRegistry;
			return this;
		}

		public DashScopeChatModel build() {
			DashScopeApi dashScopeApi = this.dashScopeApi;
			Assert.notNull(dashScopeApi, "dashScopeApi cannot be null");

            return new DashScopeChatModel(dashScopeApi, this.defaultOptions, this.toolCallingManager,
                    this.retryTemplate, this.observationRegistry, this.toolCallValidator);

        }

	}

}
