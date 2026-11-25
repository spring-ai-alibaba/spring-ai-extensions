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
package com.alibaba.cloud.ai.dashscope.agent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.alibaba.cloud.ai.agent.Agent;
import com.alibaba.cloud.ai.dashscope.api.DashScopeAgentApi;
import com.alibaba.cloud.ai.dashscope.api.DashScopeAgentApi.DashScopeAgentRequest;
import com.alibaba.cloud.ai.dashscope.api.DashScopeAgentApi.DashScopeAgentRequest.DashScopeAgentRequestInput;
import com.alibaba.cloud.ai.dashscope.api.DashScopeAgentApi.DashScopeAgentRequest.DashScopeAgentRequestInput.DashScopeAgentRequestMessage;
import com.alibaba.cloud.ai.dashscope.api.DashScopeAgentApi.DashScopeAgentRequest.DashScopeAgentRequestParameters;
import com.alibaba.cloud.ai.dashscope.api.DashScopeAgentApi.DashScopeAgentRequest.DashScopeAgentRequestParameters.DashScopeAgentRequestRagOptions;
import com.alibaba.cloud.ai.dashscope.api.DashScopeAgentApi.DashScopeAgentResponse;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeMessageParts;
import com.alibaba.cloud.ai.dashscope.common.DashScopeApiConstants;
import com.alibaba.cloud.ai.dashscope.common.DashScopeException;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.messages.part.MediaPart;
import org.springframework.ai.chat.messages.part.ReasoningPart;
import org.springframework.ai.chat.messages.part.StreamingParts;
import org.springframework.ai.chat.messages.part.TextPart;
import org.springframework.ai.chat.messages.part.UnknownPart;
import org.springframework.ai.chat.metadata.ChatGenerationMetadata;
import org.springframework.ai.chat.metadata.ChatResponseMetadata;
import org.springframework.ai.chat.metadata.DefaultUsage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import tools.jackson.databind.json.JsonMapper;
import reactor.core.publisher.Flux;

/**
 * Title DashScope low level agent.<br>
 * Description DashScope low level agent.<br>
 *
 * @author yuanci.ytb
 * @since 1.0.0-M2
 */
public final class DashScopeAgent extends Agent implements ChatModel {

    private static final Logger logger = LoggerFactory.getLogger(DashScopeAgent.class);

    private final DashScopeAgentOptions defaultOptions;

    private final DashScopeAgentApi dashScopeAgentApi;

    public DashScopeAgent(DashScopeAgentApi dashScopeAgentApi, DashScopeAgentOptions defaultOptions) {
        this.dashScopeAgentApi = dashScopeAgentApi;
        this.defaultOptions = defaultOptions;
    }

    @Override
    public ChatResponse call(Prompt prompt) {
        Prompt requestPrompt = buildRequestPrompt(prompt);
        DashScopeAgentRequest request = toRequest(requestPrompt, false);

        ResponseEntity<DashScopeAgentResponse> response = this.dashScopeAgentApi.call(request);

        if (response == null || response.getBody() == null) {
            logger.warn("No chat completion returned for prompt: {}", prompt);
            return new ChatResponse(List.of());
        }

        return toChatResponse(response.getBody(), false, false);
    }

    @Override
    public Flux<ChatResponse> stream(Prompt prompt) {
        Prompt requestPrompt = buildRequestPrompt(prompt);
        DashScopeAgentRequest request = toRequest(requestPrompt, true);

        Flux<DashScopeAgentResponse> response = this.dashScopeAgentApi.stream(request);

        return response.map(chunk -> toChatResponse(chunk, true,
                Boolean.TRUE.equals(request.parameters().incrementalOutput())));
    }

    @Override
    public ChatOptions getOptions() {
        return DashScopeAgentOptions.fromOptions(this.defaultOptions);
    }

    private DashScopeAgentRequest toRequest(Prompt prompt, boolean stream) {
        Assert.notNull(prompt, "Prompt must not be null");
        Assert.notNull(prompt.getOptions(), "DashScopeAgentOptions must not be null");
        Assert.isInstanceOf(DashScopeAgentOptions.class, prompt.getOptions());

        DashScopeAgentOptions runtimeOptions = (DashScopeAgentOptions)prompt.getOptions();
        String appId = runtimeOptions.getAppId();

        if (appId == null || appId.isEmpty()) {
            throw new IllegalArgumentException("appId must be set");
        }

        @Nullable String requestPrompt = null;
        List<DashScopeAgentRequestMessage> requestMessages = List.of();

        List<Message> messages = prompt.getInstructions();
        List<String> images = new ArrayList<>(Objects.requireNonNullElse(runtimeOptions.getImages(), List.of()));
        for (int i = 0; i < messages.size(); i++) {
            Message message = messages.get(i);
            if (message instanceof UserMessage user) {
                for (var part : user.getParts()) {
                    if (part instanceof MediaPart media) {
                        Assert.isTrue(i == messages.size() - 1 && "image".equals(media.media().getMimeType().getType()),
                                "DashScope applications accept images only on the current user message");
                        images.add(DashScopeMessageParts.mediaData(media.media()));
                    }
                    else if (!(part instanceof TextPart)) {
                        throw new IllegalArgumentException("Unsupported part in a DashScope application user message");
                    }
                }
            }
            else if (message instanceof AssistantMessage assistant) {
                Assert.isTrue(assistant.getToolCalls().isEmpty() && !DashScopeMessageParts.requiresBlocks(assistant.getParts()),
                        "DashScope application history requires text assistant messages");
            }
            else if (message.getMessageType() == MessageType.TOOL) {
                throw new IllegalArgumentException("DashScope applications execute tools on the server");
            }
        }
        boolean onlyOneUserMessage = messages.size() == 1 && messages.get(0).getMessageType() == MessageType.USER;
        if (onlyOneUserMessage) {
            requestPrompt = messages.get(0).getText();
        } else {
            requestMessages = messages.stream()
                    .map(msg -> new DashScopeAgentRequestMessage(msg.getMessageType().getValue(), Objects.requireNonNullElse(msg.getText(), "")))
                    .toList();
        }

        // @formatter:off
        DashScopeAgentRagOptions ragOptions = runtimeOptions.getRagOptions();
        DashScopeAgentRequestInput input = new DashScopeAgentRequestInput(requestPrompt, requestMessages,
                runtimeOptions.getSessionId(), runtimeOptions.getMemoryId(),
                images.isEmpty() ? runtimeOptions.getImages() : images,
                runtimeOptions.getFiles(), runtimeOptions.getBizParams());
        DashScopeAgentRequestParameters parameters = new DashScopeAgentRequestParameters(
                runtimeOptions.getFlowStreamMode(), runtimeOptions.getHasThoughts(),
                Boolean.TRUE.equals(runtimeOptions.getHasThoughts()) && Boolean.TRUE.equals(runtimeOptions.getEnableThinking()),
                stream && Boolean.TRUE.equals(runtimeOptions.getIncrementalOutput()), runtimeOptions.getModelId(),
                ragOptions == null ? null : new DashScopeAgentRequestRagOptions(
                        ragOptions.getPipelineIds(), ragOptions.getFileIds(), ragOptions.getMetadataFilter(),
                        ragOptions.getTags(), ragOptions.getStructuredFilter(), ragOptions.getSessionFileIds()));
        // @formatter:on

        return new DashScopeAgentRequest(appId, input, parameters);
    }

    private ChatResponse toChatResponse(DashScopeAgentResponse response, boolean streaming, boolean incremental) {
        DashScopeAgentResponse.DashScopeAgentResponseOutput output = response.output();
        DashScopeAgentResponse.DashScopeAgentResponseUsage usage = response.usage();
        if (output == null) {
            throw new DashScopeException("output is null");
        }

        String text = output.text();

        if (text == null) {
            text = "";
        }

        // @formatter:off
        Map<String, Object> metadata = new HashMap<>();
        metadata.put(DashScopeApiConstants.REQUEST_ID, response.requestId());
        metadata.put(DashScopeApiConstants.USAGE, usage);
        metadata.put(DashScopeApiConstants.THOUGHTS, output.thoughts());
        metadata.put(DashScopeApiConstants.OUTPUT, output);

        var assistant = AssistantMessage.builder().properties(metadata);
        var thoughts = output.thoughts();
        if (thoughts != null) {
            for (int i = 0; i < thoughts.size(); i++) {
                var thought = thoughts.get(i);
                String reasoning = thought.reasoningContent();
                if (StringUtils.hasText(reasoning)) {
                    var part = ReasoningPart.of(reasoning);
                    assistant.part(!streaming ? part : (incremental
                            ? StreamingParts.partial(part, 1 + i * 2) : StreamingParts.complete(part, 1 + i * 2)));
                }
                var raw = new UnknownPart("dashscope-agent", "thought", JsonMapper.shared().writeValueAsString(thought),
                        null, Map.of());
                assistant.part(streaming ? StreamingParts.complete(raw, 2 + i * 2) : raw);
            }
        }
        var textPart = TextPart.of(text);
        assistant.part(!streaming ? textPart : (incremental
                ? StreamingParts.partial(textPart, 0) : StreamingParts.complete(textPart, 0)));
        var assistantMessage = assistant.build();
        var generationMetadata = ChatGenerationMetadata.builder()
                .finishReason(output.finishReason()).build();
        Generation generation = new Generation(assistantMessage, generationMetadata);
        // @formatter:on

        var responseMetadata = ChatResponseMetadata.builder().id(Objects.requireNonNullElse(response.requestId(), ""));
        if (usage != null && usage.models() != null) {
            int inputTokens = usage.models().stream().mapToInt(model -> Objects.requireNonNullElse(model.inputTokens(), 0)).sum();
            int outputTokens = usage.models().stream().mapToInt(model -> Objects.requireNonNullElse(model.outputTokens(), 0)).sum();
            responseMetadata.usage(new DefaultUsage(inputTokens, outputTokens, inputTokens + outputTokens));
        }
        return new ChatResponse(List.of(generation), responseMetadata.build());
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
    public DashScopeAgent clone() {
        return mutate().build();
    }

    public Builder mutate() {
        return new Builder(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private @Nullable DashScopeAgentApi dashScopeAgentApi;

        private DashScopeAgentOptions defaultOptions = DashScopeAgentOptions.builder().build();

        public Builder() {
        }

        public Builder(DashScopeAgent agent) {
            this.dashScopeAgentApi = agent.dashScopeAgentApi;
            this.defaultOptions = agent.defaultOptions;
        }

        public Builder dashScopeAgentApi(@Nullable DashScopeAgentApi dashScopeAgentApi) {
            this.dashScopeAgentApi = dashScopeAgentApi;
            return this;
        }

        public Builder defaultOptions(@Nullable DashScopeAgentOptions defaultOptions) {
            this.defaultOptions = defaultOptions != null ? defaultOptions : DashScopeAgentOptions.builder().build();
            return this;
        }

        public DashScopeAgent build() {
            DashScopeAgentApi dashScopeAgentApi = Objects.requireNonNull(this.dashScopeAgentApi,
                    "dashScopeAgentApi cannot be null");
            return new DashScopeAgent(dashScopeAgentApi, this.defaultOptions);
        }

    }

}
