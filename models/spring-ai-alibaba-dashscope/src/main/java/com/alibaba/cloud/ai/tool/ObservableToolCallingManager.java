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
package com.alibaba.cloud.ai.tool;

import com.alibaba.cloud.ai.tool.observation.ArmsToolCallingObservationContext;
import com.alibaba.cloud.ai.tool.observation.ArmsToolCallingObservationConvention;
import com.alibaba.cloud.ai.tool.observation.ArmsToolCallingObservationDocumentation;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.contextpropagation.ObservationThreadLocalAccessor;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.AssistantMessage.ToolCall;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.chat.prompt.Prompt;
import org.jspecify.annotations.Nullable;
import org.springframework.ai.model.tool.ToolCallDecisions;
import org.springframework.ai.model.tool.ToolCallingChatOptions;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.model.tool.ToolExecutionResult;
import org.springframework.ai.model.tool.internal.ToolCallReactiveContextHolder;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.ai.tool.execution.DefaultToolExecutionExceptionProcessor;
import org.springframework.ai.tool.execution.ToolExecutionException;
import org.springframework.ai.tool.execution.ToolExecutionExceptionProcessor;
import org.springframework.ai.tool.resolution.DelegatingToolCallbackResolver;
import org.springframework.ai.tool.resolution.ToolCallbackResolver;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Inspired from org.springframework.ai.model.tool.DefaultToolCallingManager.
 *
 * @author Lumian
 */
public class ObservableToolCallingManager implements ToolCallingManager {

	// @formatter:off

  private static final ObservationRegistry DEFAULT_OBSERVATION_REGISTRY
      = ObservationRegistry.NOOP;

  private static final ToolCallbackResolver DEFAULT_TOOL_CALLBACK_RESOLVER
      = new DelegatingToolCallbackResolver(List.of());

  private static final ToolExecutionExceptionProcessor DEFAULT_TOOL_EXECUTION_EXCEPTION_PROCESSOR
      = DefaultToolExecutionExceptionProcessor.builder().build();

  private static final ArmsToolCallingObservationConvention DEFAULT_OBSERVATION_CONVENTION = new ArmsToolCallingObservationConvention();

  // @formatter:on

	private final ObservationRegistry observationRegistry;

	private final ToolCallBatchExecutor batchExecutor;

	private final ToolExecutionExceptionProcessor toolExecutionExceptionProcessor;

	// TODO Mandatory Convention as ARMS implementation until the Spring AI project
	// officially supports for observation
	private final ArmsToolCallingObservationConvention observationConvention = DEFAULT_OBSERVATION_CONVENTION;

	public ObservableToolCallingManager(ObservationRegistry observationRegistry, ToolCallbackResolver resolver,
			ToolExecutionExceptionProcessor exceptionProcessor) {
		this(observationRegistry, resolver, exceptionProcessor, ToolCallExecutionOptions.defaults());
	}

	private ObservableToolCallingManager(ObservationRegistry observationRegistry, ToolCallbackResolver resolver,
			ToolExecutionExceptionProcessor exceptionProcessor, ToolCallExecutionOptions executionOptions) {
		this.observationRegistry = Objects.requireNonNull(observationRegistry);
		this.toolExecutionExceptionProcessor = Objects.requireNonNull(exceptionProcessor);
		this.batchExecutor = new ToolCallBatchExecutor(Objects.requireNonNull(resolver),
				Objects.requireNonNull(executionOptions));
	}

	@Override
	public List<ToolDefinition> resolveToolDefinitions(ToolCallingChatOptions options) {
		return ToolCallBatchExecutor.resolveToolDefinitions(options);
	}

	@Override
	public ToolExecutionResult executeToolCalls(Prompt prompt, ChatResponse response) {
		return execute(prompt, response, null);
	}

	@Override
	public ToolExecutionResult executeToolCalls(Prompt prompt, ChatResponse response, ToolCallDecisions decisions) {
		Assert.notNull(decisions, "decisions cannot be null");
		return execute(prompt, response, decisions);
	}

	private ToolExecutionResult execute(Prompt prompt, ChatResponse response, @Nullable ToolCallDecisions decisions) {
		return this.batchExecutor.execute(prompt, response, decisions,
				(invocation, context) -> CompletableFuture.completedFuture(invoke(invocation, context)));
	}

	private String invoke(ToolCallBatchExecutor.Invocation invocation, ToolContext context) {
		ToolCallback callback = invocation.callback();
		var observationContext = ArmsToolCallingObservationContext.builder()
			.toolCall(invocation.call())
			.description(callback.getToolDefinition().description())
			.returnDirect(callback.getToolMetadata().returnDirect())
			.build();
		observationContext.setParentObservation(ToolCallReactiveContextHolder.getContext()
			.getOrDefault(ObservationThreadLocalAccessor.KEY, this.observationRegistry.getCurrentObservation()));

		String result = ArmsToolCallingObservationDocumentation.EXECUTE_TOOL_OPERATION
			.observation(this.observationConvention, DEFAULT_OBSERVATION_CONVENTION, () -> observationContext,
					this.observationRegistry)
			.observe(() -> {
				String output;
				try {
					output = callback.call(invocation.arguments(), context);
				}
				catch (ToolExecutionException ex) {
					observationContext.setError(ex);
					output = this.toolExecutionExceptionProcessor.process(ex);
				}
				observationContext.setToolResult(output);
				return output;
			});
		return Objects.requireNonNullElse(result, "");
	}

	/**
	 * We have to assume that tool calls is ordered in streaming mode.
	 */
	static AssistantMessage mergeToolCalls(AssistantMessage assistantMessage) {
		List<AssistantMessage.ToolCall> toolCalls = new ArrayList<>();
		Iterator<ToolCall> iterator = assistantMessage.getToolCalls().iterator();
		StringBuilder argumentsContent = new StringBuilder();
		String id = null;
		String type = null;
		String name = null;
		while (iterator.hasNext()) {
			ToolCall toolCallChunk = iterator.next();
			if (StringUtils.hasText(toolCallChunk.id()) && StringUtils.hasText(toolCallChunk.name())) {
				if (StringUtils.hasText(id) && StringUtils.hasText(name)) {
					// save previous one
					toolCalls.add(new AssistantMessage.ToolCall(id, Objects.requireNonNullElse(type, "function"), name, argumentsContent.toString()));
					argumentsContent.setLength(0);
				}
				id = toolCallChunk.id();
				type = toolCallChunk.type();
				name = toolCallChunk.name();
			}
			if (StringUtils.hasText(toolCallChunk.arguments())) {
				argumentsContent.append(toolCallChunk.arguments());
			}
		}

		if (StringUtils.hasText(id) && StringUtils.hasText(name)) {
			// save last one
			toolCalls.add(new AssistantMessage.ToolCall(id, Objects.requireNonNullElse(type, "function"), name, argumentsContent.toString()));
		}
		return assistantMessage.mutate()
			.toolCalls(toolCalls)
			.build();
	}

	public static ObservableToolCallingManager.Builder builder() {
		return new ObservableToolCallingManager.Builder();
	}

	public static class Builder {

		private ObservationRegistry observationRegistry = DEFAULT_OBSERVATION_REGISTRY;

		private ToolCallbackResolver toolCallbackResolver = DEFAULT_TOOL_CALLBACK_RESOLVER;

		private ToolExecutionExceptionProcessor toolExecutionExceptionProcessor = DEFAULT_TOOL_EXECUTION_EXCEPTION_PROCESSOR;

		private ToolCallExecutionOptions executionOptions = ToolCallExecutionOptions.defaults();

		private Builder() {
		}

		public ObservableToolCallingManager.Builder observationRegistry(ObservationRegistry observationRegistry) {
			this.observationRegistry = observationRegistry;
			return this;
		}

		public ObservableToolCallingManager.Builder toolCallbackResolver(ToolCallbackResolver toolCallbackResolver) {
			this.toolCallbackResolver = toolCallbackResolver;
			return this;
		}

		public ObservableToolCallingManager.Builder toolExecutionExceptionProcessor(
				ToolExecutionExceptionProcessor toolExecutionExceptionProcessor) {
			this.toolExecutionExceptionProcessor = toolExecutionExceptionProcessor;
			return this;
		}

		public Builder executionOptions(ToolCallExecutionOptions executionOptions) {
			this.executionOptions = executionOptions;
			return this;
		}

		public ObservableToolCallingManager build() {
			return new ObservableToolCallingManager(observationRegistry, toolCallbackResolver,
					toolExecutionExceptionProcessor, executionOptions);
		}

	}

}
