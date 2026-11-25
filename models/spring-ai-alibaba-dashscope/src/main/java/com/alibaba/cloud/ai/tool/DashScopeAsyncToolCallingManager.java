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

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.contextpropagation.ObservationThreadLocalAccessor;
import org.jspecify.annotations.Nullable;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.ToolCallingChatOptions;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.model.tool.ToolCallDecisions;
import org.springframework.ai.model.tool.ToolExecutionResult;
import org.springframework.ai.model.tool.internal.ToolCallReactiveContextHolder;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.ai.tool.execution.DefaultToolExecutionExceptionProcessor;
import org.springframework.ai.tool.execution.ToolExecutionException;
import org.springframework.ai.tool.execution.ToolExecutionExceptionProcessor;
import org.springframework.ai.tool.observation.DefaultToolCallingObservationConvention;
import org.springframework.ai.tool.observation.ToolCallingObservationContext;
import org.springframework.ai.tool.observation.ToolCallingObservationConvention;
import org.springframework.ai.tool.observation.ToolCallingObservationDocumentation;
import org.springframework.ai.tool.resolution.DelegatingToolCallbackResolver;
import org.springframework.ai.tool.resolution.ToolCallbackResolver;
import org.springframework.util.Assert;

public class DashScopeAsyncToolCallingManager implements ToolCallingManager {

	private static final ToolCallingObservationConvention DEFAULT_OBSERVATION_CONVENTION =
			new DefaultToolCallingObservationConvention();

	private final ObservationRegistry observationRegistry;

	private final ToolExecutionExceptionProcessor exceptionProcessor;

	private final ThreadPoolExecutor taskExecutor;

	private final ToolCallBatchExecutor batchExecutor;

	private ToolCallingObservationConvention observationConvention = DEFAULT_OBSERVATION_CONVENTION;

	public DashScopeAsyncToolCallingManager(ObservationRegistry observationRegistry, ToolCallbackResolver resolver,
			ToolExecutionExceptionProcessor exceptionProcessor, ThreadPoolExecutor taskExecutor) {
		this(observationRegistry, resolver, exceptionProcessor, taskExecutor, ToolCallExecutionOptions.defaults());
	}

	private DashScopeAsyncToolCallingManager(ObservationRegistry observationRegistry, ToolCallbackResolver resolver,
			ToolExecutionExceptionProcessor exceptionProcessor, ThreadPoolExecutor taskExecutor,
			ToolCallExecutionOptions executionOptions) {
		this.observationRegistry = Objects.requireNonNull(observationRegistry);
		this.exceptionProcessor = Objects.requireNonNull(exceptionProcessor);
		this.taskExecutor = Objects.requireNonNull(taskExecutor);
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
		Observation parent = ToolCallReactiveContextHolder.getContext()
			.getOrDefault(ObservationThreadLocalAccessor.KEY, this.observationRegistry.getCurrentObservation());
		return this.batchExecutor.execute(prompt, response, decisions, (invocation, context) ->
			CompletableFuture.supplyAsync(() -> invoke(invocation, context, parent), this.taskExecutor));
	}

	private String invoke(ToolCallBatchExecutor.Invocation invocation, ToolContext context, @Nullable Observation parent) {
		ToolCallback callback = invocation.callback();
		String arguments = invocation.arguments();
		var observationContext = ToolCallingObservationContext.builder()
			.toolDefinition(callback.getToolDefinition())
			.toolMetadata(callback.getToolMetadata())
			.toolCallId(invocation.call().id())
			.toolType(invocation.call().type())
			.toolCallArguments(arguments)
			.build();
		String result = ToolCallingObservationDocumentation.TOOL_CALL
			.observation(this.observationConvention, DEFAULT_OBSERVATION_CONVENTION, () -> observationContext,
					this.observationRegistry)
			.parentObservation(parent)
			.observe(() -> {
				String output;
				try {
					output = callback.call(arguments, context);
				}
				catch (ToolExecutionException ex) {
					output = this.exceptionProcessor.process(ex);
				}
				observationContext.setToolCallResult(output);
				return output;
			});
		return Objects.requireNonNullElse(result, "");
	}

	public void setObservationConvention(ToolCallingObservationConvention observationConvention) {
		this.observationConvention = observationConvention;
	}

	public static Builder builder() {
		return new Builder();
	}

	public static final class Builder {

		private ObservationRegistry observationRegistry = ObservationRegistry.NOOP;

		private ToolCallbackResolver toolCallbackResolver = new DelegatingToolCallbackResolver(List.of());

		private ToolExecutionExceptionProcessor toolExecutionExceptionProcessor =
				DefaultToolExecutionExceptionProcessor.builder().build();

		private ToolCallExecutionOptions executionOptions = ToolCallExecutionOptions.defaults();

		private @Nullable ThreadPoolExecutor taskExecutor;

		private Builder() {
		}

		public Builder observationRegistry(ObservationRegistry observationRegistry) {
			this.observationRegistry = observationRegistry;
			return this;
		}

		public Builder toolCallbackResolver(ToolCallbackResolver toolCallbackResolver) {
			this.toolCallbackResolver = toolCallbackResolver;
			return this;
		}

		public Builder toolExecutionExceptionProcessor(ToolExecutionExceptionProcessor toolExecutionExceptionProcessor) {
			this.toolExecutionExceptionProcessor = toolExecutionExceptionProcessor;
			return this;
		}

		public Builder executionOptions(ToolCallExecutionOptions executionOptions) {
			this.executionOptions = executionOptions;
			return this;
		}

		public Builder taskExecutor(ThreadPoolExecutor taskExecutor) {
			this.taskExecutor = taskExecutor;
			return this;
		}

		public DashScopeAsyncToolCallingManager build() {
			Assert.notNull(this.taskExecutor, "taskExecutor cannot be null");
			return new DashScopeAsyncToolCallingManager(this.observationRegistry, this.toolCallbackResolver,
					this.toolExecutionExceptionProcessor, this.taskExecutor, this.executionOptions);
		}

	}

}
