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
import com.alibaba.cloud.ai.dashscope.sdk.metadata.DashScopeSdkUsage;
import com.alibaba.dashscope.aigc.generation.GenerationOutput;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.aigc.generation.GenerationUsage;
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
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.MessageType;
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
import org.springframework.ai.chat.observation.DefaultChatModelObservationConvention;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.ModelOptionsUtils;
import org.springframework.ai.model.tool.DefaultToolExecutionEligibilityPredicate;
import org.springframework.ai.model.tool.ToolCallingChatOptions;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.model.tool.ToolExecutionEligibilityPredicate;
import org.springframework.ai.model.tool.ToolExecutionResult;
import org.springframework.ai.retry.RetryUtils;
import org.springframework.ai.support.UsageCalculator;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * {@link ChatModel} implementation backed by DashScope Java SDK.
 */
public class DashScopeSdkChatModel implements ChatModel {

	private static final ChatModelObservationConvention DEFAULT_OBSERVATION_CONVENTION =
			new DefaultChatModelObservationConvention();

	public static final String PROVIDER_NAME = "dashscope-sdk";

	public static final String DEFAULT_MODEL_NAME = "qwen-plus";

	private DashScopeSdkChatOptions defaultOptions;

	private final DashScopeSdkGenerationClient generationClient;

	private final RetryTemplate retryTemplate;

	private final ObservationRegistry observationRegistry;

	private final ToolCallingManager toolCallingManager;

	private final ToolExecutionEligibilityPredicate toolExecutionEligibilityPredicate;

	private final String apiKey;

	private final String workspaceId;

	private final Map<String, String> connectionHeaders;

	private ChatModelObservationConvention observationConvention = DEFAULT_OBSERVATION_CONVENTION;

	public DashScopeSdkChatModel(DashScopeSdkGenerationClient generationClient, DashScopeSdkChatOptions defaultOptions,
			ToolCallingManager toolCallingManager, RetryTemplate retryTemplate, ObservationRegistry observationRegistry,
			ToolExecutionEligibilityPredicate toolExecutionEligibilityPredicate, String apiKey, String workspaceId,
			Map<String, String> connectionHeaders) {

		Assert.notNull(generationClient, "generationClient cannot be null");
		Assert.notNull(defaultOptions, "defaultOptions cannot be null");
		Assert.notNull(toolCallingManager, "toolCallingManager cannot be null");
		Assert.notNull(retryTemplate, "retryTemplate cannot be null");
		Assert.notNull(observationRegistry, "observationRegistry cannot be null");
		Assert.notNull(toolExecutionEligibilityPredicate, "toolExecutionEligibilityPredicate cannot be null");
		Assert.notNull(connectionHeaders, "connectionHeaders cannot be null");

		this.generationClient = generationClient;
		this.defaultOptions = defaultOptions;
		this.toolCallingManager = toolCallingManager;
		this.retryTemplate = retryTemplate;
		this.observationRegistry = observationRegistry;
		this.toolExecutionEligibilityPredicate = toolExecutionEligibilityPredicate;
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
	public ChatOptions getDefaultOptions() {
		return DashScopeSdkChatOptions.fromOptions(this.defaultOptions);
	}

	public ChatResponse internalCall(Prompt prompt, ChatResponse previousChatResponse) {
		GenerationParam request = createRequest(prompt, false);

		ChatModelObservationContext observationContext = ChatModelObservationContext.builder()
			.prompt(prompt)
			.provider(PROVIDER_NAME)
			.build();

		ChatResponse response = ChatModelObservationDocumentation.CHAT_MODEL_OPERATION
			.observation(this.observationConvention, DEFAULT_OBSERVATION_CONVENTION, () -> observationContext,
					this.observationRegistry)
			.observe(() -> {
				GenerationResult result = this.retryTemplate.execute(ctx -> executeCall(request));
				ChatResponse chatResponse = toChatResponse(result, previousChatResponse, request.getModel());
				observationContext.setResponse(chatResponse);
				return chatResponse;
			});

		if (this.toolExecutionEligibilityPredicate.isToolExecutionRequired(prompt.getOptions(), response)) {
			ToolExecutionResult toolExecutionResult = this.toolCallingManager.executeToolCalls(prompt, response);
			if (toolExecutionResult.returnDirect()) {
				return ChatResponse.builder()
					.from(response)
					.generations(ToolExecutionResult.buildGenerations(toolExecutionResult))
					.build();
			}
			return internalCall(new Prompt(toolExecutionResult.conversationHistory(), prompt.getOptions()), response);
		}

		return response;
	}

	public Flux<ChatResponse> internalStream(Prompt prompt, ChatResponse previousChatResponse) {
		return Flux.deferContextual(contextView -> {
			GenerationParam request = createRequest(prompt, true);

			Flowable<GenerationResult> generationResults = this.retryTemplate.execute(ctx -> executeStream(request));

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

			Flux<ChatResponse> chatResponse = flowableToFlux(generationResults)
				.map(result -> toChatResponse(result, previousChatResponse, request.getModel()));

			Flux<ChatResponse> flux = chatResponse.flatMap(response -> {
				if (this.toolExecutionEligibilityPredicate.isToolExecutionRequired(prompt.getOptions(), response)) {
					return Flux.defer(() -> {
						ToolExecutionResult toolExecutionResult = this.toolCallingManager.executeToolCalls(prompt, response);
						if (toolExecutionResult.returnDirect()) {
							return Flux.just(ChatResponse.builder()
								.from(response)
								.generations(ToolExecutionResult.buildGenerations(toolExecutionResult))
								.build());
						}
						return internalStream(new Prompt(toolExecutionResult.conversationHistory(), prompt.getOptions()),
								response);
					}).subscribeOn(Schedulers.boundedElastic());
				}
				return Flux.just(response);
			}).doOnError(observation::error)
				.doFinally(s -> observation.stop())
				.contextWrite(ctx -> ctx.put(ObservationThreadLocalAccessor.KEY, observation));

			return new MessageAggregator().aggregate(flux, observationContext::setResponse);
		});
	}

	Prompt buildRequestPrompt(Prompt prompt) {
		DashScopeSdkChatOptions runtimeOptions = null;
		if (prompt.getOptions() != null) {
			if (prompt.getOptions() instanceof ToolCallingChatOptions toolCallingChatOptions) {
				runtimeOptions = ModelOptionsUtils.copyToTarget(toolCallingChatOptions, ToolCallingChatOptions.class,
						DashScopeSdkChatOptions.class);
			}
			else {
				runtimeOptions = ModelOptionsUtils.copyToTarget(prompt.getOptions(), ChatOptions.class,
						DashScopeSdkChatOptions.class);
			}
		}

		DashScopeSdkChatOptions requestOptions = ModelOptionsUtils.merge(runtimeOptions, this.defaultOptions,
				DashScopeSdkChatOptions.class);

		if (runtimeOptions != null && !CollectionUtils.isEmpty(runtimeOptions.getHttpHeaders())) {
			requestOptions.setHttpHeaders(runtimeOptions.getHttpHeaders());
		}
		else {
			requestOptions.setHttpHeaders(this.defaultOptions.getHttpHeaders());
		}

		if (runtimeOptions != null) {
			requestOptions.setInternalToolExecutionEnabled(ModelOptionsUtils.mergeOption(
					runtimeOptions.getInternalToolExecutionEnabled(),
					this.defaultOptions.getInternalToolExecutionEnabled()));
			requestOptions.setToolNames(ToolCallingChatOptions.mergeToolNames(runtimeOptions.getToolNames(),
					this.defaultOptions.getToolNames()));
			requestOptions.setToolCallbacks(ToolCallingChatOptions.mergeToolCallbacks(runtimeOptions.getToolCallbacks(),
					this.defaultOptions.getToolCallbacks()));
			requestOptions.setToolContext(ToolCallingChatOptions.mergeToolContext(runtimeOptions.getToolContext(),
					this.defaultOptions.getToolContext()));
			requestOptions.setExtraBody(mergeExtraBody(runtimeOptions.getExtraBody(), this.defaultOptions.getExtraBody()));
		}
		else {
			requestOptions.setInternalToolExecutionEnabled(this.defaultOptions.getInternalToolExecutionEnabled());
			requestOptions.setToolNames(this.defaultOptions.getToolNames());
			requestOptions.setToolCallbacks(this.defaultOptions.getToolCallbacks());
			requestOptions.setToolContext(this.defaultOptions.getToolContext());
			requestOptions.setExtraBody(this.defaultOptions.getExtraBody());
		}

		ToolCallingChatOptions.validateToolCallbacks(requestOptions.getToolCallbacks());
		return new Prompt(prompt.getInstructions(), requestOptions);
	}

	GenerationParam createRequest(Prompt prompt, boolean stream) {
		DashScopeSdkChatOptions requestOptions = (DashScopeSdkChatOptions) prompt.getOptions();
		List<Message> sdkMessages = toSdkMessages(prompt);

		GenerationParam.GenerationParamBuilder<?, ?> requestBuilder = GenerationParam.builder()
			.model(requestOptions.getModel())
			.messages(sdkMessages)
			.resultFormat("message")
			.enableSearch(requestOptions.getEnableSearch())
			.maxTokens(requestOptions.getMaxTokens())
			.topP(requestOptions.getTopP())
			.topK(requestOptions.getTopK())
			.seed(requestOptions.getSeed());

		if (requestOptions.getTemperature() != null) {
			requestBuilder.temperature(requestOptions.getTemperature().floatValue());
		}
		if (requestOptions.getRepetitionPenalty() != null) {
			requestBuilder.repetitionPenalty(requestOptions.getRepetitionPenalty().floatValue());
		}
		if (stream) {
			requestBuilder.incrementalOutput(requestOptions.getIncrementalOutput());
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

		if (!CollectionUtils.isEmpty(requestOptions.getExtraBody())) {
			requestBuilder.parameters(requestOptions.getExtraBody());
		}

		return requestBuilder.build();
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

	private Flux<GenerationResult> flowableToFlux(Flowable<GenerationResult> flowable) {
		return Flux.create(sink -> {
			Disposable disposable = flowable.subscribe(sink::next, sink::error, sink::complete);
			sink.onDispose(disposable::dispose);
		});
	}

	private ChatResponse toChatResponse(GenerationResult generationResult, ChatResponse previousChatResponse,
			String requestModel) {
		if (generationResult == null || generationResult.getOutput() == null) {
			return new ChatResponse(List.of());
		}

		GenerationOutput output = generationResult.getOutput();
		List<GenerationOutput.Choice> choices = output.getChoices();
		List<Generation> generations = new ArrayList<>();

		if (!CollectionUtils.isEmpty(choices)) {
			for (GenerationOutput.Choice choice : choices) {
				generations.add(buildGeneration(generationResult.getRequestId(), choice, output));
			}
		}
		else if (StringUtils.hasText(output.getText())) {
			generations.add(buildTextOnlyGeneration(generationResult.getRequestId(), output.getText(),
					output.getFinishReason()));
		}

		GenerationUsage usage = generationResult.getUsage();
		Usage currentUsage = usage != null ? DashScopeSdkUsage.from(usage) : new EmptyUsage();
		UsageCalculator.getCumulativeUsage(currentUsage, previousChatResponse);

		return new ChatResponse(generations,
				ChatResponseMetadata.builder().id(generationResult.getRequestId()).usage(currentUsage).model(requestModel)
					.build());
	}

	private Generation buildGeneration(String requestId, GenerationOutput.Choice choice, GenerationOutput output) {
		Message sdkMessage = choice.getMessage();
		String content = sdkMessage != null ? sdkMessage.getContent() : output.getText();
		String role = sdkMessage != null ? sdkMessage.getRole() : "assistant";
		String finishReason = choice.getFinishReason() != null ? choice.getFinishReason() : output.getFinishReason();

		Map<String, Object> metadata = Map.of(
				"id", requestId,
				"role", role == null ? "" : role,
				"finishReason", finishReason == null ? "" : finishReason);

		List<AssistantMessage.ToolCall> toolCalls = toSpringToolCalls(sdkMessage);

		AssistantMessage assistantMessage = AssistantMessage.builder()
			.content(content)
			.properties(metadata)
			.toolCalls(toolCalls)
			.build();

		return new Generation(assistantMessage,
				ChatGenerationMetadata.builder().finishReason(finishReason == null ? "" : finishReason).build());
	}

	private Generation buildTextOnlyGeneration(String requestId, String content, String finishReason) {
		Map<String, Object> metadata = Map.of("id", requestId, "role", "assistant", "finishReason",
				finishReason == null ? "" : finishReason);
		AssistantMessage assistantMessage = AssistantMessage.builder().content(content).properties(metadata).build();
		return new Generation(assistantMessage,
				ChatGenerationMetadata.builder().finishReason(finishReason == null ? "" : finishReason).build());
	}

	private List<AssistantMessage.ToolCall> toSpringToolCalls(Message sdkMessage) {
		if (sdkMessage == null || CollectionUtils.isEmpty(sdkMessage.getToolCalls())) {
			return List.of();
		}
		return sdkMessage.getToolCalls()
			.stream()
			.filter(ToolCallFunction.class::isInstance)
			.map(ToolCallFunction.class::cast)
			.filter(toolCall -> toolCall.getFunction() != null)
			.map(toolCall -> new AssistantMessage.ToolCall(toolCall.getId(), "function",
					toolCall.getFunction().getName(), toolCall.getFunction().getArguments()))
			.toList();
	}

	private List<Message> toSdkMessages(Prompt prompt) {
		List<Message> sdkMessages = new ArrayList<>();
		prompt.getInstructions().forEach(message -> {
			if (message.getMessageType() == MessageType.USER || message.getMessageType() == MessageType.SYSTEM) {
				if (message instanceof UserMessage userMessage && !CollectionUtils.isEmpty(userMessage.getMedia())) {
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
				Message sdkMessage = Message.builder()
					.role("assistant")
					.content(assistantMessage.getText())
					.build();
				if (!CollectionUtils.isEmpty(assistantMessage.getToolCalls())) {
					sdkMessage.setToolCalls(toDashScopeToolCalls(assistantMessage.getToolCalls()));
				}
				sdkMessages.add(sdkMessage);
			}
			else if (message.getMessageType() == MessageType.TOOL) {
				ToolResponseMessage toolMessage = (ToolResponseMessage) message;
				toolMessage.getResponses().forEach(response -> {
					Assert.notNull(response.id(), "ToolResponseMessage must have an id");
					Assert.notNull(response.name(), "ToolResponseMessage must have a name");
					sdkMessages.add(Message.builder()
						.role("tool")
						.name(response.name())
						.toolCallId(response.id())
						.content(response.responseData())
						.build());
				});
			}
			else {
				throw new IllegalArgumentException("Unsupported message type: " + message.getMessageType());
			}
		});
		return sdkMessages;
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

	private void applyStop(GenerationParam.GenerationParamBuilder<?, ?> requestBuilder, List<Object> stop) {
		if (CollectionUtils.isEmpty(stop)) {
			return;
		}

		if (stop.stream().allMatch(String.class::isInstance)) {
			List<String> stopStrings = stop.stream().map(String.class::cast).toList();
			requestBuilder.stopStrings(stopStrings);
			return;
		}

		if (stop.stream().allMatch(Number.class::isInstance)) {
			List<Integer> stopTokens = stop.stream().map(Number.class::cast).map(Number::intValue).toList();
			requestBuilder.stopToken(stopTokens);
			return;
		}

		if (stop.stream().allMatch(List.class::isInstance)) {
			for (Object tokenListObj : stop) {
				@SuppressWarnings("unchecked")
				List<Object> tokenList = (List<Object>) tokenListObj;
				List<Integer> stopTokens = tokenList.stream()
					.filter(Number.class::isInstance)
					.map(Number.class::cast)
					.map(Number::intValue)
					.toList();
				if (!CollectionUtils.isEmpty(stopTokens)) {
					requestBuilder.stopToken(stopTokens);
				}
			}
		}
	}

	private Map<String, Object> mergeHeaders(Map<String, String> runtimeHeaders) {
		Map<String, Object> headers = new HashMap<>();
		headers.putAll(this.connectionHeaders);
		if (!CollectionUtils.isEmpty(runtimeHeaders)) {
			headers.putAll(runtimeHeaders);
		}
		return headers;
	}

	private Map<String, Object> mergeExtraBody(Map<String, Object> runtimeExtraBody,
			Map<String, Object> defaultExtraBody) {
		if (defaultExtraBody == null && runtimeExtraBody == null) {
			return null;
		}
		Map<String, Object> merged = new HashMap<>();
		if (defaultExtraBody != null) {
			merged.putAll(defaultExtraBody);
		}
		if (runtimeExtraBody != null) {
			merged.putAll(runtimeExtraBody);
		}
		return merged.isEmpty() ? null : merged;
	}

	public DashScopeSdkChatOptions getDashScopeSdkChatOptions() {
		return this.defaultOptions;
	}

	public void setDashScopeSdkChatOptions(DashScopeSdkChatOptions options) {
		this.defaultOptions = options;
	}

	public void setObservationConvention(ChatModelObservationConvention observationConvention) {
		Assert.notNull(observationConvention, "observationConvention cannot be null");
		this.observationConvention = observationConvention;
	}

	public Builder mutate() {
		return new Builder(this);
	}

	@Override
	public DashScopeSdkChatModel clone() {
		return this.mutate().build();
	}

	public static Builder builder() {
		return new Builder();
	}

	public static final class Builder {

		private DashScopeSdkGenerationClient generationClient = new DefaultDashScopeSdkGenerationClient();

		private DashScopeSdkChatOptions defaultOptions = DashScopeSdkChatOptions.builder().model(DEFAULT_MODEL_NAME).build();

		private RetryTemplate retryTemplate = RetryUtils.DEFAULT_RETRY_TEMPLATE;

		private ToolCallingManager toolCallingManager = ToolCallingManager.builder().build();

		private ToolExecutionEligibilityPredicate toolExecutionEligibilityPredicate =
				new DefaultToolExecutionEligibilityPredicate();

		private ObservationRegistry observationRegistry = ObservationRegistry.NOOP;

		private String apiKey;

		private String workspaceId;

		private Map<String, String> connectionHeaders = new HashMap<>();

		private Builder() {
		}

		private Builder(DashScopeSdkChatModel dashScopeSdkChatModel) {
			this.generationClient = dashScopeSdkChatModel.generationClient;
			this.defaultOptions = dashScopeSdkChatModel.defaultOptions;
			this.retryTemplate = dashScopeSdkChatModel.retryTemplate;
			this.toolCallingManager = dashScopeSdkChatModel.toolCallingManager;
			this.toolExecutionEligibilityPredicate = dashScopeSdkChatModel.toolExecutionEligibilityPredicate;
			this.observationRegistry = dashScopeSdkChatModel.observationRegistry;
			this.apiKey = dashScopeSdkChatModel.apiKey;
			this.workspaceId = dashScopeSdkChatModel.workspaceId;
			this.connectionHeaders = new HashMap<>(dashScopeSdkChatModel.connectionHeaders);
		}

		public Builder generationClient(DashScopeSdkGenerationClient generationClient) {
			this.generationClient = generationClient;
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

		public Builder toolExecutionEligibilityPredicate(
				ToolExecutionEligibilityPredicate toolExecutionEligibilityPredicate) {
			this.toolExecutionEligibilityPredicate = toolExecutionEligibilityPredicate;
			return this;
		}

		public Builder observationRegistry(ObservationRegistry observationRegistry) {
			this.observationRegistry = observationRegistry;
			return this;
		}

		public Builder apiKey(String apiKey) {
			this.apiKey = apiKey;
			return this;
		}

		public Builder workspaceId(String workspaceId) {
			this.workspaceId = workspaceId;
			return this;
		}

		public Builder connectionHeaders(Map<String, String> connectionHeaders) {
			this.connectionHeaders = connectionHeaders == null ? new HashMap<>() : new HashMap<>(connectionHeaders);
			return this;
		}

		public DashScopeSdkChatModel build() {
			return new DashScopeSdkChatModel(this.generationClient, this.defaultOptions, this.toolCallingManager,
					this.retryTemplate, this.observationRegistry, this.toolExecutionEligibilityPredicate, this.apiKey,
					this.workspaceId, this.connectionHeaders);
		}

	}

}
