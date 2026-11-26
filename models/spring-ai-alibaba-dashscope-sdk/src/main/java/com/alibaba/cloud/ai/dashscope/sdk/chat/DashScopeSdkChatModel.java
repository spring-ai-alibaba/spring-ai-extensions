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

import com.alibaba.cloud.ai.dashscope.sdk.common.DashScopeSdkException;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatApiSpec.ChatCompletionOutput;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatApiSpec.TokenUsage;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatApiSpec.ToolCall;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatReasoning;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeMessageParts;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeToolCallStream;
import com.alibaba.cloud.ai.dashscope.metadata.DashScopeAiUsage;
import com.alibaba.cloud.ai.dashscope.spec.DashScopeChatEndpoints;
import com.alibaba.cloud.ai.dashscope.sdk.metadata.DashScopeSdkUsage;
import com.alibaba.dashscope.aigc.generation.GenerationOutput;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.aigc.generation.GenerationUsage;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationParam;
import com.alibaba.dashscope.common.DashScopeResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.tools.FunctionDefinition;
import com.alibaba.dashscope.tools.ToolBase;
import com.alibaba.dashscope.tools.ToolCallBase;
import com.alibaba.dashscope.tools.ToolCallFunction;
import com.alibaba.dashscope.tools.ToolFunction;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.contextpropagation.ObservationThreadLocalAccessor;
import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;
import org.jspecify.annotations.Nullable;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.messages.ToolResponseMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.messages.part.MediaPart;
import org.springframework.ai.chat.messages.part.MessagePart;
import org.springframework.ai.chat.messages.part.TextPart;
import org.springframework.ai.chat.metadata.ChatGenerationMetadata;
import org.springframework.ai.chat.metadata.ChatResponseMetadata;
import org.springframework.ai.chat.metadata.EmptyUsage;
import org.springframework.ai.chat.metadata.FinishReasonCategory;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.model.MessageAggregator;
import org.springframework.ai.chat.observation.ChatModelObservationContext;
import org.springframework.ai.chat.observation.ChatModelObservationConvention;
import org.springframework.ai.chat.observation.ChatModelObservationDocumentation;
import org.springframework.ai.chat.observation.DefaultChatModelObservationConvention;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.retry.RetryUtils;
import org.springframework.ai.support.UsageCalculator;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.core.retry.RetryTemplate;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;

import tools.jackson.databind.json.JsonMapper;

/**
 * {@link ChatModel} implementation backed by DashScope Java SDK.
 */
public final class DashScopeSdkChatModel implements ChatModel {

	private static final ChatModelObservationConvention DEFAULT_OBSERVATION_CONVENTION =
			new DefaultChatModelObservationConvention();

	public static final String PROVIDER_NAME = "dashscope-sdk";

	public static final String DEFAULT_MODEL_NAME = "qwen-plus";

	private DashScopeSdkChatOptions defaultOptions;

	private final DashScopeSdkGenerationClient generationClient;

	private final DashScopeSdkMultimodalClient multimodalClient;

	private final @Nullable String baseUrl;

	private final boolean customGenerationClient;

	private static final JsonMapper JSON = JsonMapper.shared();

	private final RetryTemplate retryTemplate;

	private final ObservationRegistry observationRegistry;

	private final ToolCallingManager toolCallingManager;

	private final @Nullable String apiKey;

	private final @Nullable String workspaceId;

	private final Map<String, String> connectionHeaders;

	private ChatModelObservationConvention observationConvention = DEFAULT_OBSERVATION_CONVENTION;

	public DashScopeSdkChatModel(DashScopeSdkGenerationClient generationClient, DashScopeSdkChatOptions defaultOptions,
                                 ToolCallingManager toolCallingManager, RetryTemplate retryTemplate, ObservationRegistry observationRegistry,
                                 @Nullable String apiKey, @Nullable String workspaceId,
                                 Map<String, String> connectionHeaders) {

		this(generationClient, defaultOptions, toolCallingManager, retryTemplate, observationRegistry, apiKey,
				workspaceId, connectionHeaders, new DashScopeSdkMultimodalClient(null), null, true);
	}

	private DashScopeSdkChatModel(DashScopeSdkGenerationClient generationClient, DashScopeSdkChatOptions defaultOptions,
			ToolCallingManager toolCallingManager, RetryTemplate retryTemplate, ObservationRegistry observationRegistry,
			@Nullable String apiKey, @Nullable String workspaceId, Map<String, String> connectionHeaders,
			DashScopeSdkMultimodalClient multimodalClient, @Nullable String baseUrl, boolean customGenerationClient) {
		Assert.notNull(generationClient, "generationClient cannot be null");
		Assert.notNull(defaultOptions, "defaultOptions cannot be null");
		Assert.notNull(toolCallingManager, "toolCallingManager cannot be null");
		Assert.notNull(retryTemplate, "retryTemplate cannot be null");
		Assert.notNull(observationRegistry, "observationRegistry cannot be null");
		Assert.notNull(connectionHeaders, "connectionHeaders cannot be null");

		this.generationClient = generationClient;
		this.multimodalClient = multimodalClient;
		this.baseUrl = baseUrl;
		this.customGenerationClient = customGenerationClient;
		this.defaultOptions = defaultOptions;
		this.toolCallingManager = toolCallingManager;
		this.retryTemplate = retryTemplate;
		this.observationRegistry = observationRegistry;
		this.apiKey = apiKey;
		this.workspaceId = workspaceId;
		this.connectionHeaders = connectionHeaders;
	}

	@Override
	public ChatResponse call(Prompt prompt) {
		Assert.notNull(prompt, "Prompt must not be null");
		Assert.isTrue(!CollectionUtils.isEmpty(prompt.getInstructions()), "Prompt messages must not be empty");
		Prompt requestPrompt = buildRequestPrompt(prompt);
		return internalCall(requestPrompt, null);
	}

	@Override
	public Flux<ChatResponse> stream(Prompt prompt) {
		Assert.notNull(prompt, "Prompt must not be null");
		Assert.isTrue(!CollectionUtils.isEmpty(prompt.getInstructions()), "Prompt messages must not be empty");
		Prompt requestPrompt = buildRequestPrompt(prompt);
		return internalStream(requestPrompt, null);
	}

	@Override
	public ChatOptions getOptions() {
		return this.defaultOptions;
	}

	public ChatResponse internalCall(Prompt prompt, @Nullable ChatResponse previousChatResponse) {
		DashScopeSdkChatOptions options = requestOptions(prompt);

		ChatModelObservationContext observationContext = ChatModelObservationContext.builder()
			.prompt(prompt)
			.provider(PROVIDER_NAME)
			.build();

		ChatResponse response = ChatModelObservationDocumentation.CHAT_MODEL_OPERATION
			.observation(this.observationConvention, DEFAULT_OBSERVATION_CONVENTION, () -> observationContext,
					this.observationRegistry)
			.observe(() -> {
				ChatResponse chatResponse;
				if (isMultimodal(options)) {
					var request = createMultimodalRequest(prompt, options, false);
					var result = RetryUtils.execute(this.retryTemplate, () -> executeMultimodalCall(request));
					chatResponse = toChatResponse(result, previousChatResponse, request, null);
				}
				else {
					GenerationParam request = createRequest(prompt, false);
					var result = RetryUtils.execute(this.retryTemplate, () -> executeCall(request));
					chatResponse = toChatResponse(result, previousChatResponse, request.getModel(), null, false);
				}
				observationContext.setResponse(chatResponse);
				return chatResponse;
			});

		return response;
	}

	public Flux<ChatResponse> internalStream(Prompt prompt, @Nullable ChatResponse previousChatResponse) {
		return Flux.deferContextual(contextView -> {
			DashScopeSdkChatOptions options = requestOptions(prompt);

			Map<Integer, StreamChoice> choices = new HashMap<>();
			Flux<ChatResponse> chatResponse;
			if (isMultimodal(options)) {
				var request = createMultimodalRequest(prompt, options, true);
				var results = RetryUtils.execute(this.retryTemplate, () -> executeMultimodalStream(request));
				chatResponse = flowableToFlux(results)
					.map(result -> toChatResponse(result, previousChatResponse, request, choices));
			}
			else {
				GenerationParam request = createRequest(prompt, true);
				var results = RetryUtils.execute(this.retryTemplate, () -> executeStream(request));
				boolean incremental = Boolean.TRUE.equals(request.getIncrementalOutput());
				chatResponse = flowableToFlux(results)
					.map(result -> toChatResponse(result, previousChatResponse, request.getModel(), choices, incremental));
			}

			ChatModelObservationContext observationContext = ChatModelObservationContext.builder()
				.prompt(prompt)
				.provider(PROVIDER_NAME)
				.build();

			Observation observation = ChatModelObservationDocumentation.CHAT_MODEL_OPERATION.observation(
					this.observationConvention,
					DEFAULT_OBSERVATION_CONVENTION,
					() -> observationContext,
					this.observationRegistry);

			observation.parentObservation(contextView.getOrDefault(ObservationThreadLocalAccessor.KEY, null)).start();

			Flux<ChatResponse> flux = chatResponse.doOnError(observation::error)
				.doFinally(s -> observation.stop())
				.contextWrite(ctx -> ctx.put(ObservationThreadLocalAccessor.KEY, observation));

			return new MessageAggregator().aggregate(flux, observationContext::setResponse);
		});
	}

	private DashScopeSdkChatOptions requestOptions(Prompt prompt) {
		DashScopeSdkChatOptions options = (DashScopeSdkChatOptions) prompt.getOptions();
		Assert.notNull(options, "DashScopeSdkChatOptions cannot be null");
		return options;
	}

	private boolean isMultimodal(DashScopeSdkChatOptions options) {
		return DashScopeChatEndpoints.isMultimodal(options.getModel(), options.getMultiModel());
	}

	GenerationParam createRequest(Prompt prompt, boolean stream) {
		DashScopeSdkChatOptions options = requestOptions(prompt);
		return createGenerationParameters(options, toSdkMessages(prompt, options), stream);
	}

	private GenerationParam createGenerationParameters(DashScopeSdkChatOptions requestOptions, List<Message> sdkMessages,
			boolean stream) {
		String model = Objects.requireNonNull(requestOptions.getModel(), "DashScopeSdkChatOptions model cannot be null");

		GenerationParam.GenerationParamBuilder<?, ?> requestBuilder = GenerationParam.builder()
			.model(model)
			.messages(sdkMessages)
			.resultFormat("message");

		if (requestOptions.getEnableSearch() != null) {
			requestBuilder.enableSearch(requestOptions.getEnableSearch());
		}

		if (requestOptions.getMaxTokens() != null) {
			requestBuilder.maxTokens(requestOptions.getMaxTokens());
		}
		if (requestOptions.getTopP() != null) {
			requestBuilder.topP(requestOptions.getTopP());
		}
		if (requestOptions.getTopK() != null) {
			requestBuilder.topK(requestOptions.getTopK());
		}
		if (requestOptions.getSeed() != null) {
			requestBuilder.seed(requestOptions.getSeed());
		}

		if (requestOptions.getTemperature() != null) {
			requestBuilder.temperature(requestOptions.getTemperature().floatValue());
		}
		if (requestOptions.getRepetitionPenalty() != null) {
			requestBuilder.repetitionPenalty(requestOptions.getRepetitionPenalty().floatValue());
		}
		if (stream) {
			requestBuilder.incrementalOutput(Objects.requireNonNullElse(requestOptions.getIncrementalOutput(), true));
		}
		applyStop(requestBuilder, requestOptions.getStop());

		List<ToolDefinition> toolDefinitions = this.toolCallingManager.resolveToolDefinitions(requestOptions);
		if (!CollectionUtils.isEmpty(toolDefinitions)) {
			requestBuilder.tools(toDashScopeTools(toolDefinitions));
		}
		if (requestOptions.getToolChoice() != null) {
			requestBuilder.toolChoice(requestOptions.getToolChoice());
		}

		if (StringUtils.hasText(this.apiKey)) {
			requestBuilder.apiKey(this.apiKey);
		}
		if (StringUtils.hasText(this.workspaceId)) {
			requestBuilder.workspace(this.workspaceId);
		}

		Map<String, Object> mergedHeaders = mergeHeaders(requestOptions.getHttpHeaders());
		if (!CollectionUtils.isEmpty(mergedHeaders)) {
			requestBuilder.headers(mergedHeaders);
		}

		if (requestOptions.getExtraBody() != null) {
			requestOptions.getExtraBody().forEach((name, value) -> {
				if (!"multi_model".equals(name)) {
					requestBuilder.parameter(name, value);
				}
			});
		}

		return requestBuilder.build();
	}

	private MultiModalConversationParam createMultimodalRequest(Prompt prompt, DashScopeSdkChatOptions options,
			boolean stream) {
		GenerationParam shared = createGenerationParameters(options, List.of(), stream);
		Map<String, Object> parameters = shared.getParameters();
		// The same incremental_output setting also controls this adapter's accumulator.
		if (stream) {
			parameters.put("incremental_output", shared.getIncrementalOutput());
		}
		var request = MultiModalConversationParam.builder()
			.model(shared.getModel())
			.messages(toSdkMultimodalMessages(prompt, options))
			.parameters(parameters)
			.incrementalOutput(shared.getIncrementalOutput());
		if (StringUtils.hasText(this.apiKey)) {
			request.apiKey(this.apiKey);
		}
		if (StringUtils.hasText(this.workspaceId)) {
			request.workspace(this.workspaceId);
		}
		return request.headers(mergeHeaders(options.getHttpHeaders())).build();
	}

	private DashScopeResult executeMultimodalCall(MultiModalConversationParam request) {
		try {
			return this.multimodalClient.call(request);
		}
		catch (Exception ex) {
			throw new DashScopeSdkException("Failed to call DashScope SDK multimodal generation API", ex);
		}
	}

	private Flowable<DashScopeResult> executeMultimodalStream(MultiModalConversationParam request) {
		try {
			return this.multimodalClient.stream(request);
		}
		catch (Exception ex) {
			throw new DashScopeSdkException("Failed to stream DashScope SDK multimodal generation API", ex);
		}
	}

	private GenerationResult executeCall(GenerationParam request) {
		try {
			return this.generationClient.call(request);
		}
		catch (Exception ex) {
			throw new DashScopeSdkException("Failed to call DashScope SDK generation API", ex);
		}
	}

	private Flowable<GenerationResult> executeStream(GenerationParam request) {
		try {
			return this.generationClient.stream(request);
		}
		catch (Exception ex) {
			throw new DashScopeSdkException("Failed to stream DashScope SDK generation API", ex);
		}
	}

	private <T> Flux<T> flowableToFlux(Flowable<T> flowable) {
		return Flux.create(sink -> {
			Disposable disposable = flowable.subscribe(sink::next, sink::error, sink::complete);
			sink.onDispose(disposable::dispose);
		});
	}

	private ChatResponse toChatResponse(GenerationResult generationResult, @Nullable ChatResponse previousChatResponse,
			String requestModel, @Nullable Map<Integer, StreamChoice> toolStreams, boolean incremental) {
		if (generationResult == null || generationResult.getOutput() == null) {
			return new ChatResponse(List.of());
		}

		GenerationOutput output = generationResult.getOutput();
		List<GenerationOutput.Choice> choices = output.getChoices();
		List<Generation> generations = new ArrayList<>();

		if (!CollectionUtils.isEmpty(choices)) {
			for (int position = 0; position < choices.size(); position++) {
				GenerationOutput.Choice choice = choices.get(position);
				int index = Objects.requireNonNullElse(choice.getIndex(), position);
				var toolStream = toolStreams != null
						? toolStreams.computeIfAbsent(index, key -> new StreamChoice()) : null;
				Message message = choice.getMessage();
				String finish = choice.getFinishReason() != null ? choice.getFinishReason() : output.getFinishReason();
				generations.add(buildGeneration(generationResult.getRequestId(), message != null ? message.getRole() : "assistant",
						message != null ? message.getContent() : output.getText(), finish,
						message != null ? message.getReasoningContent() : null, null, sdkToolDeltas(message), toolStream,
						incremental, false));
			}
		}
		else if (StringUtils.hasText(output.getText())) {
			generations.add(buildTextOnlyGeneration(generationResult.getRequestId(), output.getText(),
					output.getFinishReason()));
		}

		GenerationUsage usage = generationResult.getUsage();
		Usage currentUsage = usage != null ? DashScopeSdkUsage.from(usage) : new EmptyUsage();
		currentUsage = UsageCalculator.getCumulativeUsage(currentUsage, previousChatResponse);

		return new ChatResponse(generations,
				ChatResponseMetadata.builder().id(generationResult.getRequestId()).usage(currentUsage).model(requestModel)
					.build());
	}

	private ChatResponse toChatResponse(DashScopeResult result, @Nullable ChatResponse previousChatResponse,
			MultiModalConversationParam request, @Nullable Map<Integer, StreamChoice> streamedChoices) {
		if (StringUtils.hasText(result.getCode())) {
			throw new DashScopeSdkException("DashScope SDK multimodal generation failed: " + result.getCode());
		}
		var output = result.getOutput() == null ? null
				: JSON.readValue(result.getOutput().toString(), ChatCompletionOutput.class);
		List<Generation> generations = new ArrayList<>();
		boolean incremental = Boolean.TRUE.equals(request.getIncrementalOutput());
		boolean summarized = "summarized".equals(request.getParameters().get("thinking_display"));
		if (output != null && output.choices() != null) {
			for (int position = 0; position < output.choices().size(); position++) {
				var choice = output.choices().get(position);
				var message = choice.message();
				var state = streamedChoices != null
						? streamedChoices.computeIfAbsent(Objects.requireNonNullElse(choice.index(), position),
								key -> new StreamChoice()) : null;
				var calls = message != null && message.toolCalls() != null ? message.toolCalls() : List.<ToolCall>of();
				var deltas = IntStream.range(0, calls.size()).filter(i -> calls.get(i) != null).mapToObj(i -> {
					var call = calls.get(i);
					var function = call.function();
					return new DashScopeToolCallStream.Delta(Objects.requireNonNullElse(call.index(), i), call.id(),
							function != null ? function.name() : null, function != null ? function.arguments() : null);
				}).toList();
				generations.add(buildGeneration(result.getRequestId(),
						message != null && message.role() != null ? message.role().name().toLowerCase(Locale.ROOT) : "assistant",
						message != null ? message.rawContent() : null, choice.finishReason() != null ? choice.finishReason().name() : null,
						message != null ? message.reasoningContent() : null, message != null ? message.encryptedContent() : null,
						deltas, state, incremental, summarized));
			}
		}
		Usage usage = result.getUsage() == null ? new EmptyUsage()
				: DashScopeAiUsage.from(JSON.readValue(result.getUsage().toString(), TokenUsage.class));
		return new ChatResponse(generations, ChatResponseMetadata.builder().id(result.getRequestId())
			.usage(UsageCalculator.getCumulativeUsage(usage, previousChatResponse)).model(request.getModel()).build());
	}

	private Generation buildGeneration(String requestId, @Nullable String role, @Nullable Object content,
			@Nullable String finishReason, @Nullable String reasoning, @Nullable String encrypted,
			List<DashScopeToolCallStream.Delta> calls, @Nullable StreamChoice state, boolean incremental,
			boolean summarized) {
		String finish = finishReason == null || "null".equalsIgnoreCase(finishReason) ? "" : finishReason;
		var metadata = Map.<String, Object>of("id", Objects.requireNonNullElse(requestId, ""), "role",
				Objects.requireNonNullElse(role, ""), "finishReason", finish);
		var message = AssistantMessage.builder().properties(metadata);
		var part = state == null ? DashScopeChatReasoning.fromResponse(reasoning, encrypted, summarized)
				: state.reasoning.accept(reasoning, encrypted, incremental, summarized);
		if (part != null) {
			message.part(part);
		}
		if (state == null) {
			message.parts(DashScopeMessageParts.decode(content)).toolCalls(calls.stream().filter(call -> StringUtils.hasText(call.name()))
				.map(call -> new AssistantMessage.ToolCall(Objects.requireNonNullElse(call.id(), ""), "function", call.name(),
						Objects.requireNonNullElse(call.arguments(), ""))).toList());
		}
		else {
			var contentParts = state.content.accept(content, incremental);
			if (contentParts.isEmpty()) {
				message.content("");
			}
			else {
				message.parts(contentParts);
			}
			message.parts(state.tools.accept(calls, "tool_calls".equalsIgnoreCase(finish), incremental));
		}

		var generationMetadata = ChatGenerationMetadata.builder().finishReason(finish);
		if ("sensitive".equalsIgnoreCase(finish)) {
			generationMetadata.finishReasonCategory(FinishReasonCategory.FILTERED);
		}
		return new Generation(message.build(), generationMetadata.build());
	}

	private static final class StreamChoice {

		private final DashScopeMessageParts.Stream content = new DashScopeMessageParts.Stream();

		private final DashScopeToolCallStream tools = new DashScopeToolCallStream(index -> this.content.nextIndex());

		private final DashScopeChatReasoning.Stream reasoning = new DashScopeChatReasoning.Stream();

	}

	private Generation buildTextOnlyGeneration(String requestId, String content, String finishReason) {
		Map<String, Object> metadata = Map.of("id", requestId, "role", "assistant", "finishReason",
				finishReason == null ? "" : finishReason);
		AssistantMessage assistantMessage = AssistantMessage.builder().content(content).properties(metadata).build();
		return new Generation(assistantMessage,
				ChatGenerationMetadata.builder().finishReason(finishReason == null ? "" : finishReason).build());
	}

	private List<DashScopeToolCallStream.Delta> sdkToolDeltas(@Nullable Message message) {
		if (message == null || CollectionUtils.isEmpty(message.getToolCalls())) {
			return List.of();
		}
		var calls = message.getToolCalls();
		return IntStream.range(0, calls.size()).filter(i -> calls.get(i) instanceof ToolCallFunction).mapToObj(i -> {
			var call = (ToolCallFunction) calls.get(i);
			var function = call.getFunction();
			return new DashScopeToolCallStream.Delta(Objects.requireNonNullElse(call.getIndex(), i), call.getId(),
					function != null ? function.getName() : null, function != null ? function.getArguments() : null);
		}).toList();
	}

	private List<Message> toSdkMessages(Prompt prompt, DashScopeSdkChatOptions options) {
		List<Message> sdkMessages = new ArrayList<>();
		prompt.getInstructions().forEach(message -> {
			if (message.getMessageType() == MessageType.USER || message.getMessageType() == MessageType.SYSTEM) {
				if (message instanceof UserMessage userMessage && DashScopeMessageParts.requiresBlocks(userMessage.getParts())) {
					throw new IllegalArgumentException(
							"DashScope SDK chat module currently supports text-only USER messages");
				}
				sdkMessages.add(Message.builder()
					.role(message.getMessageType().name().toLowerCase(Locale.ROOT))
					.content(message.getText())
					.build());
			}
			else if (message.getMessageType() == MessageType.ASSISTANT) {
				AssistantMessage assistantMessage = (AssistantMessage) message;
				Assert.isTrue(!DashScopeMessageParts.requiresBlocks(assistantMessage.getParts()),
						"DashScope SDK assistant media and provider blocks require a multimodal endpoint");
				Message sdkMessage = Message.builder()
					.role("assistant")
					.content(assistantMessage.getText())
					.build();
				if (!CollectionUtils.isEmpty(assistantMessage.getToolCalls())) {
					sdkMessage.setToolCalls(toDashScopeToolCalls(assistantMessage.getToolCalls()));
				}
				if (preserveThinking(options)) {
					String reasoning = DashScopeChatReasoning.reasoningContent(assistantMessage);
					if (StringUtils.hasText(reasoning)) {
						sdkMessage.setReasoningContent(reasoning);
					}
				}
				sdkMessages.add(sdkMessage);
			}
			else if (message.getMessageType() == MessageType.TOOL) {
				ToolResponseMessage toolMessage = (ToolResponseMessage) message;
				toolMessage.getResults().forEach(result -> sdkMessages.add(Message.builder().role("tool")
					.name(result.name()).toolCallId(DashScopeMessageParts.toolCallId(result))
					.content(DashScopeMessageParts.toolResultText(result)).build()));
			}
			else {
				throw new IllegalArgumentException("Unsupported message type: " + message.getMessageType());
			}
		});
		return sdkMessages;
	}

	private List<Object> toSdkMultimodalMessages(Prompt prompt, DashScopeSdkChatOptions options) {
		List<Object> messages = new ArrayList<>();
		for (var message : prompt.getInstructions()) {
			if (message instanceof ToolResponseMessage toolMessage) {
				for (var result : toolMessage.getResults()) {
					Map<String, Object> tool = new LinkedHashMap<>();
					tool.put("role", "tool");
					tool.put("tool_call_id", DashScopeMessageParts.toolCallId(result));
					tool.put("content", DashScopeMessageParts.toolResultText(result));
					if (result.name() != null) {
						tool.put("name", result.name());
					}
					messages.add(tool);
				}
				continue;
			}
			Map<String, Object> entry = new LinkedHashMap<>();
			entry.put("role", message.getMessageType().name().toLowerCase(Locale.ROOT));
			if (message instanceof UserMessage userMessage) {
				entry.put("content", sdkContent(userMessage.getParts()));
			}
			else if (message instanceof AssistantMessage assistant) {
				entry.put("content", sdkContent(assistant.getParts()));
			}
			else {
				String text = Objects.requireNonNullElse(message.getText(), "");
				entry.put("content", text.isEmpty() ? List.of() : List.of(Map.of("text", text)));
			}
			if (message instanceof AssistantMessage assistant) {
				if (!assistant.getToolCalls().isEmpty()) {
					entry.put("tool_calls", toDashScopeToolCalls(assistant.getToolCalls()));
				}
				if (preserveThinking(options)) {
					String reasoning = DashScopeChatReasoning.reasoningContent(assistant);
					if (StringUtils.hasText(reasoning)) {
						entry.put("reasoning_content", reasoning);
					}
					if (DashScopeChatEndpoints.supportsEncryptedThinking(options.getModel())) {
						String encrypted = DashScopeChatReasoning.encryptedContent(assistant);
						if (encrypted != null) {
							entry.put("encrypted_content", encrypted);
						}
					}
				}
			}
			messages.add(entry);
		}
		return messages;
	}

	private static boolean preserveThinking(DashScopeSdkChatOptions options) {
		return options.getExtraBody() == null || !Boolean.FALSE.equals(options.getExtraBody().get("preserve_thinking"));
	}

	private static List<Object> sdkContent(List<MessagePart> parts) {
		List<Object> content = new ArrayList<>();
		for (var part : parts) {
			var block = DashScopeMessageParts.replayBlock(part);
			if (block != null) {
				content.add(JSON.convertValue(block, Object.class));
			}
			else if (part instanceof TextPart text) {
				content.add(Map.of("text", text.text()));
			}
			else if (part instanceof MediaPart media) {
				String kind = media.media().getMimeType().getType();
				Assert.isTrue(List.of("image", "audio", "video").contains(kind),
						"Unsupported media type for DashScope SDK multimodal chat: " + media.media().getMimeType());
				content.add(Map.of(kind, DashScopeMessageParts.mediaData(media.media())));
			}
		}
		return content;
	}

	private List<ToolCallBase> toDashScopeToolCalls(List<AssistantMessage.ToolCall> toolCalls) {
		return toolCalls.stream().map(toolCall -> {
			ToolCallFunction sdkToolCall = new ToolCallFunction();
			sdkToolCall.setId(toolCall.id());
			sdkToolCall.setType(toolCall.type());
			ToolCallFunction.CallFunction function = sdkToolCall.new CallFunction();
			function.setName(toolCall.name());
			function.setArguments(toolCall.arguments());
			sdkToolCall.setFunction(function);
			return sdkToolCall;
		}).map(ToolCallBase.class::cast).toList();
	}

	private List<ToolBase> toDashScopeTools(List<ToolDefinition> toolDefinitions) {
		return toolDefinitions.stream().map(toolDefinition -> {
			JsonObject parameters;
			try {
				parameters = JsonParser.parseString(toolDefinition.inputSchema()).getAsJsonObject();
			}
			catch (Exception ex) {
				parameters = new JsonObject();
			}

			FunctionDefinition functionDefinition = FunctionDefinition.builder()
				.name(toolDefinition.name())
				.description(toolDefinition.description())
				.parameters(parameters)
				.build();

			return ToolFunction.builder().type("function").function(functionDefinition).build();
		}).map(ToolBase.class::cast).toList();
	}

	private void applyStop(GenerationParam.GenerationParamBuilder<?, ?> requestBuilder, @Nullable Object stop) {
		if (stop == null) {
			return;
		}

        if (stop instanceof String stopString) {
            requestBuilder.stopString(stopString);
            return;
        }

        if (stop instanceof List<?> stopList) {
            if (CollectionUtils.isEmpty(stopList)) {
                return;
            }

            if (stopList.stream().allMatch(String.class::isInstance)) {
                List<String> stopStrings = stopList.stream().map(String.class::cast).toList();
                requestBuilder.stopStrings(stopStrings);
                return;
            }

            if (stopList.stream().allMatch(Number.class::isInstance)) {
                List<Integer> stopTokens = stopList.stream().map(Number.class::cast).map(Number::intValue).toList();
                requestBuilder.stopToken(stopTokens);
                return;
            }

            throw new IllegalArgumentException("Invalid stop value");
        }

	}

	private Map<String, Object> mergeHeaders(@Nullable Map<String, String> runtimeHeaders) {
		Map<String, Object> headers = new HashMap<>();
        if (!CollectionUtils.isEmpty(this.connectionHeaders)) {
            headers.putAll(this.connectionHeaders);
        }
		if (!CollectionUtils.isEmpty(runtimeHeaders)) {
			headers.putAll(runtimeHeaders);
		}
		return headers;
	}

	public void setObservationConvention(ChatModelObservationConvention observationConvention) {
		Assert.notNull(observationConvention, "observationConvention cannot be null");
		this.observationConvention = observationConvention;
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
	public DashScopeSdkChatModel clone() {
		return this.mutate().build();
	}

    public ChatOptions getChatOptions() {
		return this.defaultOptions;
	}

	public static Builder builder() {
		return new Builder();
	}

	public static final class Builder {

		private @Nullable DashScopeSdkGenerationClient generationClient;

		private boolean customGenerationClient;

		private @Nullable DashScopeSdkMultimodalClient multimodalClient;

		private @Nullable String baseUrl;

		private DashScopeSdkChatOptions defaultOptions = DashScopeSdkChatOptions.builder().model(DEFAULT_MODEL_NAME).build();

		private RetryTemplate retryTemplate = RetryUtils.DEFAULT_RETRY_TEMPLATE;

		private ToolCallingManager toolCallingManager = ToolCallingManager.builder().build();

		private ObservationRegistry observationRegistry = ObservationRegistry.NOOP;

		private @Nullable String apiKey;

		private @Nullable String workspaceId;

		private Map<String, String> connectionHeaders = new HashMap<>();

		private Builder() {
		}

		private Builder(DashScopeSdkChatModel dashScopeSdkChatModel) {
			this.generationClient = dashScopeSdkChatModel.generationClient;
			this.customGenerationClient = dashScopeSdkChatModel.customGenerationClient;
			this.baseUrl = dashScopeSdkChatModel.baseUrl;
			this.multimodalClient = dashScopeSdkChatModel.multimodalClient;
			this.defaultOptions = dashScopeSdkChatModel.defaultOptions;
			this.retryTemplate = dashScopeSdkChatModel.retryTemplate;
			this.toolCallingManager = dashScopeSdkChatModel.toolCallingManager;
			this.observationRegistry = dashScopeSdkChatModel.observationRegistry;
			this.apiKey = dashScopeSdkChatModel.apiKey;
			this.workspaceId = dashScopeSdkChatModel.workspaceId;
			this.connectionHeaders = new HashMap<>(dashScopeSdkChatModel.connectionHeaders);
		}

		public Builder generationClient(DashScopeSdkGenerationClient generationClient) {
			this.generationClient = generationClient;
			this.customGenerationClient = true;
			return this;
		}

		public Builder baseUrl(@Nullable String baseUrl) {
			this.baseUrl = StringUtils.hasText(baseUrl) ? baseUrl : null;
			this.multimodalClient = null;
			if (!this.customGenerationClient) {
				this.generationClient = null;
			}
			return this;
		}

		public Builder defaultOptions(DashScopeSdkChatOptions defaultOptions) {
			this.defaultOptions = defaultOptions;
			return this;
		}

		public Builder retryTemplate(RetryTemplate retryTemplate) {
			this.retryTemplate = retryTemplate;
			return this;
		}

		public Builder toolCallingManager(ToolCallingManager toolCallingManager) {
			this.toolCallingManager = toolCallingManager;
			return this;
		}

		public Builder observationRegistry(ObservationRegistry observationRegistry) {
			this.observationRegistry = observationRegistry;
			return this;
		}

		public Builder apiKey(@Nullable String apiKey) {
			this.apiKey = apiKey;
			return this;
		}

		public Builder workspaceId(@Nullable String workspaceId) {
			this.workspaceId = workspaceId;
			return this;
		}

		public Builder connectionHeaders(Map<String, String> connectionHeaders) {
			this.connectionHeaders = connectionHeaders == null ? new HashMap<>() : new HashMap<>(connectionHeaders);
			return this;
		}

		public DashScopeSdkChatModel build() {
			var textClient = this.generationClient != null ? this.generationClient
					: new DefaultDashScopeSdkGenerationClient(this.baseUrl);
			var mediaClient = this.multimodalClient != null ? this.multimodalClient : new DashScopeSdkMultimodalClient(this.baseUrl);
			return new DashScopeSdkChatModel(textClient, this.defaultOptions, this.toolCallingManager,
					this.retryTemplate, this.observationRegistry, this.apiKey, this.workspaceId,
					this.connectionHeaders, mediaClient, this.baseUrl, this.customGenerationClient);
		}

	}

}
