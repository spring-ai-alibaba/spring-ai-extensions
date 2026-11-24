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
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.contextpropagation.ObservationThreadLocalAccessor;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.AssistantMessage.ToolCall;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.ToolResponseMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.DefaultToolCallingManager;
import org.springframework.ai.model.tool.ToolCallLimitBehavior;
import org.springframework.ai.model.tool.ToolCallingChatOptions;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.model.tool.ToolExecutionResult;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.ai.tool.execution.DefaultToolExecutionExceptionProcessor;
import org.springframework.ai.tool.execution.ToolExecutionException;
import org.springframework.ai.tool.execution.ToolExecutionExceptionProcessor;
import org.springframework.ai.tool.observation.DefaultToolCallingObservationConvention;
import org.springframework.ai.tool.observation.ToolCallingObservationContext;
import org.springframework.ai.tool.observation.ToolCallingObservationConvention;
import org.springframework.ai.tool.observation.ToolCallingObservationDocumentation;
import org.springframework.ai.model.tool.internal.ToolCallReactiveContextHolder;
import org.springframework.ai.tool.resolution.DelegatingToolCallbackResolver;
import org.springframework.ai.tool.resolution.ToolCallbackResolver;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ThreadPoolExecutor;


public class DashScopeAsyncToolCallingManager implements ToolCallingManager {

    private static final Logger logger = LoggerFactory.getLogger(DefaultToolCallingManager.class);

    private static final ObservationRegistry DEFAULT_OBSERVATION_REGISTRY
            = ObservationRegistry.NOOP;

    private static final ToolCallingObservationConvention DEFAULT_OBSERVATION_CONVENTION
            = new DefaultToolCallingObservationConvention();

    private static final ToolCallbackResolver DEFAULT_TOOL_CALLBACK_RESOLVER
            = new DelegatingToolCallbackResolver(List.of());

    private static final ToolExecutionExceptionProcessor DEFAULT_TOOL_EXECUTION_EXCEPTION_PROCESSOR
            = DefaultToolExecutionExceptionProcessor.builder().build();

    private static final String POSSIBLE_LLM_TOOL_NAME_CHANGE_WARNING
            = "LLM may have adapted the tool name '{}', especially if the name was truncated due to length limits. If this is the case, you can customize the prefixing and processing logic using McpToolNamePrefixGenerator";


    // @formatter:on

    private ObservationRegistry observationRegistry = DEFAULT_OBSERVATION_REGISTRY;

    private ToolCallbackResolver toolCallbackResolver = DEFAULT_TOOL_CALLBACK_RESOLVER;

    private final boolean resolutionFallbackEnabled;

    private final ToolCallLimitPolicy limitPolicy;

    private ToolExecutionExceptionProcessor toolExecutionExceptionProcessor = DEFAULT_TOOL_EXECUTION_EXCEPTION_PROCESSOR;

    private ToolCallingObservationConvention observationConvention = DEFAULT_OBSERVATION_CONVENTION;

    private @Nullable ArmsToolCallingObservationConvention armsObservationConvention;

    private ThreadPoolExecutor asyncToolCallingTaskExecutor;

    public DashScopeAsyncToolCallingManager(ObservationRegistry observationRegistry, ToolCallbackResolver toolCallbackResolver,
                                   ToolExecutionExceptionProcessor toolExecutionExceptionProcessor, ThreadPoolExecutor taskExecutor) {
        this(observationRegistry, toolCallbackResolver, toolExecutionExceptionProcessor, taskExecutor, false,
                ToolCallLimitPolicy.DEFAULT);
    }

    private DashScopeAsyncToolCallingManager(ObservationRegistry observationRegistry, ToolCallbackResolver toolCallbackResolver,
            ToolExecutionExceptionProcessor toolExecutionExceptionProcessor, ThreadPoolExecutor taskExecutor,
            boolean resolutionFallbackEnabled, ToolCallLimitPolicy limitPolicy) {
        Assert.notNull(observationRegistry, "observationRegistry cannot be null");
        Assert.notNull(toolCallbackResolver, "toolCallbackResolver cannot be null");
        Assert.notNull(toolExecutionExceptionProcessor, "toolCallExceptionConverter cannot be null");
        Assert.notNull(taskExecutor, "taskExecutor cannot be null");

        this.observationRegistry = observationRegistry;
        this.toolCallbackResolver = toolCallbackResolver;
        this.resolutionFallbackEnabled = resolutionFallbackEnabled;
        this.limitPolicy = limitPolicy;
        this.toolExecutionExceptionProcessor = toolExecutionExceptionProcessor;
        this.asyncToolCallingTaskExecutor = taskExecutor;
    }

    @Override
    public List<ToolDefinition> resolveToolDefinitions(ToolCallingChatOptions chatOptions) {
        Assert.notNull(chatOptions, "chatOptions cannot be null");

        List<ToolCallback> toolCallbacks = new ArrayList<>(
                !CollectionUtils.isEmpty(chatOptions.getToolCallbacks()) ? chatOptions.getToolCallbacks() : List.of());
//        for (String toolName : chatOptions.getToolNames()) {
//            // Skip the tool if it is already present in the request toolCallbacks.
//            // That might happen if a tool is defined in the options
//            // both as a ToolCallback and as a tool name.
//            if (chatOptions.getToolCallbacks()
//                    .stream()
//                    .anyMatch(tool -> tool.getToolDefinition().name().equals(toolName))) {
//                continue;
//            }
//            ToolCallback toolCallback = this.toolCallbackResolver.resolve(toolName);
//            if (toolCallback == null) {
//                logger.warn(POSSIBLE_LLM_TOOL_NAME_CHANGE_WARNING, toolName);
//                throw new IllegalStateException("No ToolCallback found for tool name: " + toolName);
//            }
//            toolCallbacks.add(toolCallback);
//        }

        return toolCallbacks.stream().map(ToolCallback::getToolDefinition).toList();
    }

    @Override
    public ToolExecutionResult executeToolCalls(Prompt prompt, ChatResponse chatResponse) {
        Assert.notNull(prompt, "prompt cannot be null");
        Assert.notNull(chatResponse, "chatResponse cannot be null");

        Optional<Generation> toolCallGeneration = chatResponse.getResults()
                .stream()
                .filter(g -> !CollectionUtils.isEmpty(g.getOutput().getToolCalls()))
                .findFirst();

        if (toolCallGeneration.isEmpty()) {
            throw new IllegalStateException("No tool call requested by the chat model");
        }

        AssistantMessage assistantMessage = toolCallGeneration.get().getOutput();

        ToolContext toolContext = buildToolContext(prompt, assistantMessage);

        InternalToolExecutionResult internalToolExecutionResult = executeToolCall(prompt, assistantMessage,
                toolContext);

        List<Message> conversationHistory = buildConversationHistoryAfterToolExecution(prompt.getInstructions(),
                assistantMessage, internalToolExecutionResult.toolResponseMessage());

        return ToolExecutionResult.builder()
                .conversationHistory(conversationHistory)
                .returnDirect(internalToolExecutionResult.returnDirect())
                .build();
    }

    private static ToolContext buildToolContext(Prompt prompt, AssistantMessage assistantMessage) {
        Map<String, Object> toolContextMap = Map.of();

        if (prompt.getOptions() instanceof ToolCallingChatOptions toolCallingChatOptions
                && !CollectionUtils.isEmpty(toolCallingChatOptions.getToolContext())) {
            toolContextMap = new HashMap<>(toolCallingChatOptions.getToolContext());
        }

        return new ToolContext(toolContextMap);
    }

    /**
     * Execute the tool call and return the response message.
     */
    private InternalToolExecutionResult executeToolCall(Prompt prompt, AssistantMessage assistantMessage,
                                                        ToolContext toolContext) {

        List<ToolCallback> promptToolCallbacks = List.of();
        if (prompt.getOptions() instanceof ToolCallingChatOptions toolCallingChatOptions) {
            if (!CollectionUtils.isEmpty(toolCallingChatOptions.getToolCallbacks())) {
                promptToolCallbacks = toolCallingChatOptions.getToolCallbacks();
            }
        }
        final List<ToolCallback> toolCallbacks = promptToolCallbacks;

        final Queue<Boolean> toolsReturnDirect = new ConcurrentLinkedDeque<>();
        @Nullable Observation parentObservation = ToolCallReactiveContextHolder.getContext()
                .getOrDefault(ObservationThreadLocalAccessor.KEY, this.observationRegistry.getCurrentObservation());

        List<CompletableFuture<ToolResponseMessage.ToolResponse>> futuresToolResponses = new ArrayList<>();
        var turn = this.limitPolicy.startTurn(prompt.getInstructions());
        ToolCallLimitPolicy.@Nullable Breach terminalBreach = null;
        for (ToolCall toolCall : assistantMessage.getToolCalls()) {
            var breach = turn.check(toolCall);
            if (breach != null) {
                futuresToolResponses.add(CompletableFuture.completedFuture(breach.response()));
                if (this.limitPolicy.onLimitExceeded() == ToolCallLimitBehavior.THROW) {
                    terminalBreach = breach;
                    break;
                }
                continue;
            }
            futuresToolResponses.add(CompletableFuture.supplyAsync(() -> {
                logger.debug("Executing tool call: {}", toolCall.name());

                String toolName = toolCall.name();
                String arguments = toolCall.arguments();
                String toolInputArguments = StringUtils.hasText(arguments) ? arguments : "{}";

                ToolCallback toolCallback = toolCallbacks.stream()
                        .filter(tool -> toolName.equals(tool.getToolDefinition().name()))
                        .findFirst()
                        .orElseGet(() -> this.resolutionFallbackEnabled ? this.toolCallbackResolver.resolve(toolName) : null);

                if (toolCallback == null) {
                    throw new IllegalStateException("No ToolCallback found for tool name: " + toolName);
                }

                toolsReturnDirect.add(toolCallback.getToolMetadata().returnDirect());

                String toolCallResult = observeToolCall(toolCall, toolCallback, toolInputArguments, toolContext,
                        parentObservation);

                logger.debug("Async Tool call end! toolCall = {}, toolInputArguments = {}, result: {}",
                        toolCall.name(), toolInputArguments, toolCallResult);

                return new ToolResponseMessage.ToolResponse(toolCall.id(), toolName,
                        toolCallResult != null ? toolCallResult : "");
            }, this.asyncToolCallingTaskExecutor));
        }

        final List<ToolResponseMessage.ToolResponse> toolResponses = CompletableFuture
                .allOf(futuresToolResponses.toArray(new CompletableFuture[0]))
                .thenApply(result -> futuresToolResponses.stream().map(CompletableFuture::join).toList())
                .join();

        boolean returnDirect = !toolsReturnDirect.isEmpty() && toolsReturnDirect.stream().allMatch(Boolean::booleanValue);
        if (terminalBreach != null) {
            throw terminalBreach.exception(prompt, assistantMessage, toolResponses, returnDirect);
        }
        return new InternalToolExecutionResult(
                ToolResponseMessage.builder().responses(toolResponses).metadata(Map.of()).build(), returnDirect);
    }

    private String observeToolCall(ToolCall toolCall, ToolCallback toolCallback, String arguments,
                                   ToolContext toolContext, @Nullable Observation parent) {
        ArmsToolCallingObservationConvention armsConvention = this.armsObservationConvention;
        if (armsConvention != null) {
            var context = ArmsToolCallingObservationContext.builder()
                    .toolCall(toolCall)
                    .description(toolCallback.getToolDefinition().description())
                    .returnDirect(toolCallback.getToolMetadata().returnDirect())
                    .build();
            return ArmsToolCallingObservationDocumentation.EXECUTE_TOOL_OPERATION
                    .observation(armsConvention, armsConvention, () -> context, this.observationRegistry)
                    .parentObservation(parent)
                    .observe(() -> {
                        String result = executeCallback(toolCallback, arguments, toolContext, context);
                        context.setToolResult(result);
                        return result;
                    });
        }

        var context = ToolCallingObservationContext.builder()
                .toolDefinition(toolCallback.getToolDefinition())
                .toolMetadata(toolCallback.getToolMetadata())
                .toolCallArguments(arguments)
                .build();
        return ToolCallingObservationDocumentation.TOOL_CALL
                .observation(this.observationConvention, DEFAULT_OBSERVATION_CONVENTION, () -> context,
                        this.observationRegistry)
                .parentObservation(parent)
                .observe(() -> {
                    String result = executeCallback(toolCallback, arguments, toolContext, context);
                    context.setToolCallResult(result);
                    return result;
                });
    }

    private String executeCallback(ToolCallback callback, String arguments, ToolContext toolContext,
                                   Observation.Context observationContext) {
        try {
            return callback.call(arguments, toolContext);
        }
        catch (ToolExecutionException ex) {
            observationContext.setError(ex);
            return this.toolExecutionExceptionProcessor.process(ex);
        }
    }

    public void setArmsObservationConvention(ArmsToolCallingObservationConvention armsObservationConvention) {
        this.armsObservationConvention = armsObservationConvention;
    }

    private List<Message> buildConversationHistoryAfterToolExecution(List<Message> previousMessages,
                                                                     AssistantMessage assistantMessage, ToolResponseMessage toolResponseMessage) {
        List<Message> messages = new ArrayList<>(previousMessages);
        messages.add(assistantMessage);
        messages.add(toolResponseMessage);
        return messages;
    }

    public void setObservationConvention(ToolCallingObservationConvention observationConvention) {
        this.observationConvention = observationConvention;
    }

    public static Builder builder() {
        return new Builder();
    }

    private record InternalToolExecutionResult(ToolResponseMessage toolResponseMessage, boolean returnDirect) {
    }

    public final static class Builder {

        private ObservationRegistry observationRegistry = DEFAULT_OBSERVATION_REGISTRY;

        private ToolCallbackResolver toolCallbackResolver = DEFAULT_TOOL_CALLBACK_RESOLVER;

        private ToolExecutionExceptionProcessor toolExecutionExceptionProcessor = DEFAULT_TOOL_EXECUTION_EXCEPTION_PROCESSOR;

        private @Nullable ThreadPoolExecutor taskExecutor;

        private boolean resolutionFallbackEnabled;

        private ToolCallLimitPolicy limitPolicy = ToolCallLimitPolicy.DEFAULT;

        public Builder limitPolicy(ToolCallLimitPolicy limitPolicy) {
            Assert.notNull(limitPolicy, "limitPolicy must not be null");
            this.limitPolicy = limitPolicy;
            return this;
        }

        /**
         * Allow resolving tools that are not attached to the request. Disabled by default.
         */
        public Builder resolutionFallbackEnabled(boolean enabled) {
            this.resolutionFallbackEnabled = enabled;
            return this;
        }

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

        public Builder toolExecutionExceptionProcessor(
                ToolExecutionExceptionProcessor toolExecutionExceptionProcessor) {
            this.toolExecutionExceptionProcessor = toolExecutionExceptionProcessor;
            return this;
        }

        public Builder taskExecutor(ThreadPoolExecutor taskExecutor) {
            this.taskExecutor = taskExecutor;
            return this;
        }

        public DashScopeAsyncToolCallingManager build() {
            Assert.notNull(this.taskExecutor, "taskExecutor cannot be null");
            return new DashScopeAsyncToolCallingManager(this.observationRegistry, this.toolCallbackResolver,
                    this.toolExecutionExceptionProcessor, this.taskExecutor, this.resolutionFallbackEnabled, this.limitPolicy);
        }

    }
}
