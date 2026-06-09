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

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel.Builder;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatApiSpec.ChatCompletion;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatApiSpec.ChatCompletionChunk;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatApiSpec.ChatCompletionFinishReason;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatApiSpec.ChatCompletionMessage;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatApiSpec.ChatCompletionFunction;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatApiSpec.ToolCall;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatApiSpec.ChatCompletionOutput;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatApiSpec.ChatCompletionOutput.Choice;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatApiSpec.ChatCompletionRequest;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatApiSpec.ChatCompletionRequest.Parameters.SearchOptions;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatApiSpec.SearchInfo;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatApiSpec.SearchResult;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatApiSpec.TokenUsage;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatApiSpec.CacheCreation;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatApiSpec.MediaContent;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatApiSpec.PromptTokenDetailed;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatApiSpec.Role;
import com.alibaba.cloud.ai.dashscope.common.DashScopeApiConstants;
import tools.jackson.databind.json.JsonMapper;
import io.micrometer.observation.ObservationRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.ToolResponseMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.ToolCallingChatOptions;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.retry.RetryUtils;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.definition.DefaultToolDefinition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.InstanceOfAssertFactories.type;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test cases for DashScopeChatModel. Tests cover basic chat completion, streaming, tool
 * calls, error handling, and various edge cases.
 *
 * @author yuluo
 * @author <a href="mailto:yuluo08290126@gmail.com">yuluo</a>
 * @author brianxiadong
 * @since 1.0.0-M5.1
 */
class DashScopeChatModelTests {

    // Test constants
    private static final String TEST_MODEL = "qwen-turbo";

    private static final String TEST_REQUEST_ID = "test-request-id";

    private static final String TEST_PROMPT = "Hello, how are you?";

    private static final String TEST_RESPONSE = "I'm doing well, thank you for asking!";

    private static final String EMPTY_INPUT_SCHEMA = "{\"$schema\": \"https://json-schema.org/draft/2020-12/schema\",\"type\": \"object\",\"properties\": {}}";

    private DashScopeApi dashScopeApi;

    private DashScopeChatModel chatModel;

    private DashScopeChatOptions defaultOptions;

    @BeforeEach
    void setUp() throws Exception {
        // Initialize mock objects and test instances
        dashScopeApi = Mockito.mock(DashScopeApi.class);

        defaultOptions = DashScopeChatOptions.builder()
                .model(TEST_MODEL)
                .temperature(0.7)
                .topP(0.8)
                .topK(50)
                .seed(1234)
                .build();
        chatModel = DashScopeChatModel.builder().dashScopeApi(dashScopeApi).defaultOptions(defaultOptions).build();
    }

    @Test
    void defaultOptionsUseMessageResultFormat() {
        Prompt requestPrompt = chatModel.buildRequestPrompt(new Prompt(List.of(new UserMessage(TEST_PROMPT))));
        ChatCompletionRequest request = chatModel.createRequest(requestPrompt);

        assertThat(request.parameters().resultFormat()).isEqualTo("message");
    }

    @Test
    void callSendsDataInspectionAsHeaderOnly() {
        DashScopeChatOptions options = DashScopeChatOptions.builder()
                .model("qwen-plus")
                .dataInspection(DashScopeApiConstants.ENABLED)
                .build();
        Prompt prompt = new Prompt(List.of(new UserMessage(TEST_PROMPT)), options);

        ChatCompletionMessage responseMessage = new ChatCompletionMessage(TEST_RESPONSE, Role.ASSISTANT);
        Choice choice = new Choice(ChatCompletionFinishReason.STOP, responseMessage, null, 0);
        ChatCompletionOutput output = new ChatCompletionOutput(TEST_RESPONSE, List.of(choice), null);
        ChatCompletion chatCompletion = new ChatCompletion(TEST_REQUEST_ID, output, null);

        when(dashScopeApi.chatCompletionEntity(any(ChatCompletionRequest.class), any(), eq(false)))
                .thenReturn(ResponseEntity.ok(chatCompletion));

        chatModel.call(prompt);

        ArgumentCaptor<ChatCompletionRequest> requestCaptor = ArgumentCaptor.forClass(ChatCompletionRequest.class);
        ArgumentCaptor<HttpHeaders> headersCaptor = ArgumentCaptor.forClass(HttpHeaders.class);
        verify(dashScopeApi).chatCompletionEntity(requestCaptor.capture(), headersCaptor.capture(), eq(false));

        assertThat(headersCaptor.getValue().getFirst(DashScopeApiConstants.HEADER_DATAINSPECTION))
                .isEqualTo(DashScopeApiConstants.ENABLED);
        String jsonRequest = JsonMapper.builder().build().writeValueAsString(requestCaptor.getValue());
        assertThat(jsonRequest).doesNotContain(DashScopeApiConstants.HEADER_DATAINSPECTION);
        assertThat(jsonRequest).doesNotContain("dataInspection");
    }

    @Test
    void streamSendsDataInspectionAsHeaderOnly() {
        DashScopeChatOptions options = DashScopeChatOptions.builder()
                .model("qwen-plus")
                .dataInspection(DashScopeApiConstants.ENABLED)
                .build();
        Prompt prompt = new Prompt(List.of(new UserMessage(TEST_PROMPT)), options);

        ChatCompletionMessage chunkMessage = new ChatCompletionMessage(TEST_RESPONSE, Role.ASSISTANT);
        Choice choice = new Choice(ChatCompletionFinishReason.STOP, chunkMessage, null, 0);
        ChatCompletionOutput output = new ChatCompletionOutput(TEST_RESPONSE, List.of(choice), null);
        ChatCompletionChunk chunk = new ChatCompletionChunk(TEST_REQUEST_ID, output, null, null);

        when(dashScopeApi.chatCompletionStream(any(ChatCompletionRequest.class), any(), eq(false)))
                .thenReturn(Flux.just(chunk));

        StepVerifier.create(chatModel.stream(prompt))
                .assertNext(response -> assertThat(response.getResult().getOutput().getText()).isEqualTo(TEST_RESPONSE))
                .verifyComplete();

        ArgumentCaptor<ChatCompletionRequest> requestCaptor = ArgumentCaptor.forClass(ChatCompletionRequest.class);
        ArgumentCaptor<HttpHeaders> headersCaptor = ArgumentCaptor.forClass(HttpHeaders.class);
        verify(dashScopeApi).chatCompletionStream(requestCaptor.capture(), headersCaptor.capture(), eq(false));

        assertThat(headersCaptor.getValue().getFirst(DashScopeApiConstants.HEADER_DATAINSPECTION))
                .isEqualTo(DashScopeApiConstants.ENABLED);
        String jsonRequest = JsonMapper.builder().build().writeValueAsString(requestCaptor.getValue());
        assertThat(jsonRequest).doesNotContain(DashScopeApiConstants.HEADER_DATAINSPECTION);
        assertThat(jsonRequest).doesNotContain("dataInspection");
    }

    @Test
    void callSendsDashScopeMessageRequestToGenerationEndpoint() {
        DashScopeChatOptions options = DashScopeChatOptions.builder()
                .model("qwen-plus")
                .resultFormat("message")
                .build();
        Prompt prompt = new Prompt(List.of(
                new SystemMessage("You are a helpful assistant."),
                new UserMessage("你是谁？")
        ), options);

        ChatCompletionMessage responseMessage = new ChatCompletionMessage("我是通义千问。", Role.ASSISTANT);
        Choice choice = new Choice(ChatCompletionFinishReason.STOP, responseMessage, null, 0);
        ChatCompletionOutput output = new ChatCompletionOutput("我是通义千问。", List.of(choice), null);
        TokenUsage usage = new TokenUsage(10, 5, 15, null, null, null, null, null, null, null);
        ChatCompletion chatCompletion = new ChatCompletion(TEST_REQUEST_ID, output, usage);

        when(dashScopeApi.chatCompletionEntity(any(ChatCompletionRequest.class), any(), eq(false)))
                .thenReturn(ResponseEntity.ok(chatCompletion));

        ChatResponse response = chatModel.call(prompt);

        assertThat(response.getResult().getOutput().getText()).isEqualTo("我是通义千问。");

        ArgumentCaptor<ChatCompletionRequest> requestCaptor = ArgumentCaptor.forClass(ChatCompletionRequest.class);
        verify(dashScopeApi).chatCompletionEntity(requestCaptor.capture(), any(), eq(false));
        verify(dashScopeApi, never()).chatCompletionStream(any(ChatCompletionRequest.class), any(), eq(false));

        ChatCompletionRequest request = requestCaptor.getValue();
        assertThat(request.model()).isEqualTo("qwen-plus");
        assertThat(request.input().messages()).hasSize(2);
        assertThat(request.input().messages().get(0).role()).isEqualTo(Role.SYSTEM);
        assertThat(request.input().messages().get(0).rawContent()).isEqualTo("You are a helpful assistant.");
        assertThat(request.input().messages().get(1).role()).isEqualTo(Role.USER);
        assertThat(request.input().messages().get(1).rawContent()).isEqualTo("你是谁？");
        assertThat(request.parameters().resultFormat()).isEqualTo("message");

        String jsonRequest = JsonMapper.builder().build().writeValueAsString(request);
        assertThat(jsonRequest).doesNotContain("\"stream\"");
        assertThat(jsonRequest).doesNotContain("\"multi_model\"");
        assertThat(jsonRequest).doesNotContain("\"forced_search\"");
    }

    @Test
    void streamSendsIncrementalDashScopeMessageRequestToStreamingEndpoint() {
        DashScopeChatOptions options = DashScopeChatOptions.builder()
                .model("qwen-plus")
                .resultFormat("message")
                .incrementalOutput(true)
                .build();
        Prompt prompt = new Prompt(List.of(
                new SystemMessage("You are a helpful assistant."),
                new UserMessage("你是谁？")
        ), options);

        ChatCompletionMessage chunkMessage1 = new ChatCompletionMessage("我是", Role.ASSISTANT);
        ChatCompletionMessage chunkMessage2 = new ChatCompletionMessage("通义千问。", Role.ASSISTANT);
        Choice choice1 = new Choice(null, chunkMessage1, null, 0);
        Choice choice2 = new Choice(ChatCompletionFinishReason.STOP, chunkMessage2, null, 0);
        ChatCompletionOutput output1 = new ChatCompletionOutput("我是", List.of(choice1), null);
        ChatCompletionOutput output2 = new ChatCompletionOutput("通义千问。", List.of(choice2), null);
        ChatCompletionChunk chunk1 = new ChatCompletionChunk(TEST_REQUEST_ID, output1, null, null);
        ChatCompletionChunk chunk2 = new ChatCompletionChunk(TEST_REQUEST_ID, output2,
                new TokenUsage(10, 5, 15, null, null, null, null, null, null, null), null);

        when(dashScopeApi.chatCompletionStream(any(ChatCompletionRequest.class), any(), eq(false)))
                .thenReturn(Flux.just(chunk1, chunk2));

        StepVerifier.create(chatModel.stream(prompt))
                .assertNext(response -> assertThat(response.getResult().getOutput().getText()).isEqualTo("我是"))
                .assertNext(response -> assertThat(response.getResult().getOutput().getText()).isEqualTo("通义千问。"))
                .verifyComplete();

        ArgumentCaptor<ChatCompletionRequest> requestCaptor = ArgumentCaptor.forClass(ChatCompletionRequest.class);
        verify(dashScopeApi).chatCompletionStream(requestCaptor.capture(), any(), eq(false));
        verify(dashScopeApi, never()).chatCompletionEntity(any(ChatCompletionRequest.class), any(), eq(false));

        ChatCompletionRequest request = requestCaptor.getValue();
        assertThat(request.model()).isEqualTo("qwen-plus");
        assertThat(request.input().messages()).hasSize(2);
        assertThat(request.input().messages().get(0).role()).isEqualTo(Role.SYSTEM);
        assertThat(request.input().messages().get(0).rawContent()).isEqualTo("You are a helpful assistant.");
        assertThat(request.input().messages().get(1).role()).isEqualTo(Role.USER);
        assertThat(request.input().messages().get(1).rawContent()).isEqualTo("你是谁？");
        assertThat(request.parameters().resultFormat()).isEqualTo("message");
        assertThat(request.parameters().incrementalOutput()).isTrue();
    }

    @Test
    void testBasicChatCompletion() {
        // Test basic chat completion with a simple user message
        Message message = new UserMessage(TEST_PROMPT);
        Prompt prompt = new Prompt(List.of(message));

        // Mock API response
        ChatCompletionMessage responseMessage = new ChatCompletionMessage(TEST_RESPONSE, Role.ASSISTANT);
        Choice choice = new Choice(ChatCompletionFinishReason.STOP, responseMessage, null, 0);
        ChatCompletionOutput output = new ChatCompletionOutput(TEST_RESPONSE, List.of(choice), null);
        TokenUsage usage = new TokenUsage(10, 5, 15, null, null, null, null, null, null, null);
        ChatCompletion chatCompletion = new ChatCompletion(TEST_REQUEST_ID, output, usage);
        ResponseEntity<ChatCompletion> responseEntity = ResponseEntity.ok(chatCompletion);

        when(dashScopeApi.chatCompletionEntity(any(ChatCompletionRequest.class), any(), eq(false))).thenReturn(responseEntity);

        // Execute test
        ChatResponse response = chatModel.call(prompt);

        // Verify results
        assertThat(response).isNotNull();
        assertThat(response.getResult()).isNotNull();
        assertThat(response.getResult().getOutput()).isInstanceOf(AssistantMessage.class);
        assertThat(response.getResult().getOutput().getText()).isEqualTo(TEST_RESPONSE);
        assertThat(response.getMetadata().getId()).isEqualTo(TEST_REQUEST_ID);
    }

    @Test
    void testStreamChatCompletion() {
        // Test streaming chat completion with chunked responses
        Message message = new UserMessage(TEST_PROMPT);
        Prompt prompt = new Prompt(List.of(message));

        // Mock streaming responses
        ChatCompletionMessage chunkMessage1 = new ChatCompletionMessage("I'm ", Role.ASSISTANT);
        ChatCompletionMessage chunkMessage2 = new ChatCompletionMessage("doing ", Role.ASSISTANT);
        ChatCompletionMessage chunkMessage3 = new ChatCompletionMessage("well!", Role.ASSISTANT);

        Choice choice1 = new Choice(null, chunkMessage1, null, 0);
        Choice choice2 = new Choice(null, chunkMessage2, null, 0);
        Choice choice3 = new Choice(ChatCompletionFinishReason.STOP, chunkMessage3, null, 0);

        ChatCompletionOutput output1 = new ChatCompletionOutput("I'm ", List.of(choice1), null);
        ChatCompletionOutput output2 = new ChatCompletionOutput("doing ", List.of(choice2), null);
        ChatCompletionOutput output3 = new ChatCompletionOutput("well!", List.of(choice3), null);

        ChatCompletionChunk chunk1 = new ChatCompletionChunk(TEST_REQUEST_ID, output1, null, null);
        ChatCompletionChunk chunk2 = new ChatCompletionChunk(TEST_REQUEST_ID, output2, null, null);
        ChatCompletionChunk chunk3 = new ChatCompletionChunk(TEST_REQUEST_ID, output3, new TokenUsage(10, 5, 15, null, null, null, null, null, null, null), null);

        when(dashScopeApi.chatCompletionStream(any(ChatCompletionRequest.class), any(), eq(false)))
                .thenReturn(Flux.just(chunk1, chunk2, chunk3));

        // Execute test
        Flux<ChatResponse> responseFlux = chatModel.stream(prompt);

        // Verify results
        StepVerifier.create(responseFlux)
                .assertNext(response -> assertThat(response.getResult().getOutput().getText()).isEqualTo("I'm "))
                .assertNext(response -> assertThat(response.getResult().getOutput().getText()).isEqualTo("doing "))
                .assertNext(response -> {
                    assertThat(response.getResult().getOutput().getText()).isEqualTo("well!");
                    assertThat(response.getMetadata().getUsage()).isNotNull();
                })
                .verifyComplete();
    }

    @Test
    void testSystemMessage() {
        // Test chat completion with system message
        SystemMessage systemMessage = new SystemMessage("You are a helpful assistant.");
        UserMessage userMessage = new UserMessage("Hello!");

        // Mock API response
        String response = "Hello! How can I help you today?";
        ChatCompletionMessage responseMessage = new ChatCompletionMessage(response, Role.ASSISTANT);
        Choice choice = new Choice(ChatCompletionFinishReason.STOP, responseMessage, null, 0);
        ChatCompletionOutput output = new ChatCompletionOutput(response, List.of(choice), null);

        // Add non-null TokenUsage with zero values
        TokenUsage usage = new TokenUsage(10, 5, 15, null, null, null, null, null, null, null);

        ChatCompletion completion = new ChatCompletion("test-id", output, usage);

        when(dashScopeApi.chatCompletionEntity(any(), any(), eq(false))).thenReturn(ResponseEntity.ok(completion));

        // Test with system message
        Prompt prompt = new Prompt(List.of(systemMessage, userMessage));
        ChatResponse chatResponse = chatModel.call(prompt);

        assertThat(chatResponse).isNotNull();
        assertThat(chatResponse.getResults().get(0).getOutput().getText()).isEqualTo(response);
    }

    @Test
    void testToolCalls() {
        // Test tool calls functionality
        ToolCallback weatherCallback = mock(ToolCallback.class);
        when(weatherCallback.getToolDefinition()).thenReturn(DefaultToolDefinition.builder()
                .name("get_weather")
                .description("Get weather information")
                .inputSchema(EMPTY_INPUT_SCHEMA)
                .build());

        // Create options with tool
        DashScopeChatOptions options = DashScopeChatOptions.builder()
                .model("qwen-turbo")
                .toolCallbacks(List.of(weatherCallback))
                .build();

        DashScopeChatModel toolChatModel = DashScopeChatModel.builder()
                .dashScopeApi(dashScopeApi)
                .defaultOptions(options)
                .build();

        // Mock API responses for tool call
        String toolCallResponse = "{\"name\": \"get_weather\", \"arguments\": \"{\\\"location\\\": \\\"Beijing\\\"}\"}";
        ChatCompletionMessage toolMessage = new ChatCompletionMessage(toolCallResponse, Role.ASSISTANT);
        Choice toolChoice = new Choice(ChatCompletionFinishReason.TOOL_CALLS, toolMessage, null, 0);

        // Add non-null TokenUsage with zero values
        TokenUsage usage = new TokenUsage(10, 5, 15, null, null, null, null, null, null, null);

        ChatCompletionOutput toolOutput = new ChatCompletionOutput(toolCallResponse, List.of(toolChoice), null);
        ChatCompletion toolCompletion = new ChatCompletion("test-id", toolOutput, usage);

        when(dashScopeApi.chatCompletionEntity(any(), any(), eq(false))).thenReturn(ResponseEntity.ok(toolCompletion));

        // Test tool call
        Message message = new UserMessage("What's the weather like?");
        Prompt prompt = new Prompt(List.of(message), options);
        ChatResponse response = toolChatModel.call(prompt);

        assertThat(response).isNotNull();
        assertThat(response.getResults().get(0).getOutput().getText()).contains("get_weather");
    }

    @Test
    void callMapsToolCallResponseWithoutExecutingToolCallback() {
        ToolCallback weatherCallback = mock(ToolCallback.class);
        when(weatherCallback.getToolDefinition()).thenReturn(DefaultToolDefinition.builder()
                .name("get_weather")
                .description("Get weather information")
                .inputSchema(EMPTY_INPUT_SCHEMA)
                .build());

        DashScopeChatOptions options = DashScopeChatOptions.builder()
                .model("qwen-turbo")
                .toolCallbacks(List.of(weatherCallback))
                .build();

        DashScopeChatModel toolChatModel = DashScopeChatModel.builder()
                .dashScopeApi(dashScopeApi)
                .defaultOptions(options)
                .build();

        ToolCall toolCall = new ToolCall("tool-call-id-1", "function",
                new ChatCompletionFunction("get_weather", "{\"location\":\"Beijing\"}"), null);
        ChatCompletionMessage toolMessage = new ChatCompletionMessage("", Role.ASSISTANT, null, null,
                List.of(toolCall), null, null, null, null, null);
        Choice toolChoice = new Choice(ChatCompletionFinishReason.TOOL_CALLS, toolMessage, null, 0);
        ChatCompletionOutput toolOutput = new ChatCompletionOutput("", List.of(toolChoice), null);
        TokenUsage usage = new TokenUsage(10, 5, 15, null, null, null, null, null, null, null);
        ChatCompletion toolCompletion = new ChatCompletion("test-id", toolOutput, usage);

        when(dashScopeApi.chatCompletionEntity(any(), any(), eq(false))).thenReturn(ResponseEntity.ok(toolCompletion));

        ChatResponse response = toolChatModel.call(new Prompt(List.of(new UserMessage("What's the weather?")), options));

        assertThat(response.hasToolCalls()).isTrue();
        assertThat(response.getResult().getOutput().getToolCalls()).hasSize(1);
        assertThat(response.getResult().getOutput().getToolCalls().get(0).name()).isEqualTo("get_weather");

        ArgumentCaptor<ChatCompletionRequest> requestCaptor = ArgumentCaptor.forClass(ChatCompletionRequest.class);
        verify(dashScopeApi).chatCompletionEntity(requestCaptor.capture(), any(), eq(false));
        assertThat(requestCaptor.getValue().parameters().tools()).hasSize(1);
        verify(weatherCallback, never()).call(any(String.class));
    }

    @Test
    void testStreamToolCalls() {
        // Test streaming tool calls
        ToolCallback weatherCallback = mock(ToolCallback.class);
        when(weatherCallback.getToolDefinition()).thenReturn(DefaultToolDefinition.builder()
                .name("get_weather")
                .description("Get weather information")
                .inputSchema(EMPTY_INPUT_SCHEMA)
                .build());

        DashScopeChatOptions options = DashScopeChatOptions.builder()
                .model("qwen-turbo")
                .toolCallbacks(List.of(weatherCallback))
                .stream(true)
                .build();

        DashScopeChatModel toolChatModel = DashScopeChatModel.builder()
                .dashScopeApi(dashScopeApi)
                .defaultOptions(options)
                .build();

        // Mock streaming tool call responses
        String chunk1 = "{\"name\": \"get_";
        String chunk2 = "weather\", \"arguments\": \"{\\\"location\\\"";
        String chunk3 = ": \\\"Beijing\\\"}\"}";

        ChatCompletionMessage message1 = new ChatCompletionMessage(chunk1, Role.ASSISTANT);
        ChatCompletionMessage message2 = new ChatCompletionMessage(chunk2, Role.ASSISTANT);
        ChatCompletionMessage message3 = new ChatCompletionMessage(chunk3, Role.ASSISTANT);

        Choice choice1 = new Choice(null, message1, null, 0);
        Choice choice2 = new Choice(null, message2, null, 0);
        Choice choice3 = new Choice(ChatCompletionFinishReason.TOOL_CALLS, message3, null, 0);

        ChatCompletionChunk chunk1Response = new ChatCompletionChunk("test-id", new ChatCompletionOutput(chunk1, List.of(choice1), null), null, null);
        ChatCompletionChunk chunk2Response = new ChatCompletionChunk("test-id", new ChatCompletionOutput(chunk2, List.of(choice2), null), null, null);
        ChatCompletionChunk chunk3Response = new ChatCompletionChunk("test-id", new ChatCompletionOutput(chunk3, List.of(choice3), null), new TokenUsage(10, 5, 15, null, null, null, null, null, null, null), null);

        when(dashScopeApi.chatCompletionStream(any(), any(), eq(false)))
                .thenReturn(Flux.just(chunk1Response, chunk2Response, chunk3Response));

        Message message = new UserMessage("What's the weather like?");
        Prompt prompt = new Prompt(List.of(message), options);
        List<ChatResponse> responses = toolChatModel.stream(prompt).collectList().block();

        assertThat(responses).isNotNull();
        assertThat(responses).hasSize(3);
        assertThat(responses.get(0).getResults().get(0).getOutput().getText()).isEqualTo(chunk1);
        assertThat(responses.get(1).getResults().get(0).getOutput().getText()).isEqualTo(chunk2);
        assertThat(responses.get(2).getResults().get(0).getOutput().getText()).isEqualTo(chunk3);
    }

    @Test
    void testErrorHandling() {
        // Test error handling
        when(dashScopeApi.chatCompletionEntity(any(), any(), eq(false))).thenThrow(new RuntimeException("API Error"));

        Message message = new UserMessage("Test message");
        Prompt prompt = new Prompt(List.of(message));

        assertThatThrownBy(() -> chatModel.call(prompt)).isInstanceOf(RuntimeException.class).hasMessage("API Error");
    }

    @Test
    void testEmptyResponse() {
        // Test handling of empty response
        ChatCompletionOutput output = new ChatCompletionOutput("", Collections.emptyList(), null);
        // Add non-null TokenUsage with zero values
        TokenUsage usage = new TokenUsage(0, 0, 0, null, null, null, null, null, null, null);
        ChatCompletion completion = new ChatCompletion("test-id", output, usage);

        when(dashScopeApi.chatCompletionEntity(any(), any(), eq(false))).thenReturn(ResponseEntity.ok(completion));

        Message message = new UserMessage("Test message");
        Prompt prompt = new Prompt(List.of(message));
        ChatResponse response = chatModel.call(prompt);

        assertThat(response).isNotNull();
        assertThat(response.getResults()).isEmpty();
        // Verify usage metadata
        assertThat(response.getMetadata().getUsage()).isNotNull();
        var aiUsage = response.getMetadata().getUsage();
        assertThat(aiUsage.getPromptTokens()).isZero();
        assertThat(aiUsage.getCompletionTokens()).isZero();
        assertThat(aiUsage.getTotalTokens()).isZero();
    }

    @Test
    void testEmptyPrompt() {
        // Test handling of empty prompt
        Prompt emptyPrompt = new Prompt(Collections.emptyList());
        assertThatThrownBy(() -> chatModel.call(emptyPrompt)).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Prompt");
    }

    @Test
    void testNullPrompt() {
        // Test handling of null prompt
        Prompt prompt = null;
        assertThatThrownBy(() -> chatModel.call(prompt)).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Prompt");
    }

    @Test
    void testCustomMetadata() {
        // Test custom metadata handling
        Message message = new UserMessage(TEST_PROMPT);
        Prompt prompt = new Prompt(List.of(message));

        ChatCompletionMessage responseMessage = new ChatCompletionMessage(TEST_RESPONSE, Role.ASSISTANT);
        Choice choice = new Choice(ChatCompletionFinishReason.STOP, responseMessage, null, 0);
        ChatCompletionOutput output = new ChatCompletionOutput(TEST_RESPONSE, List.of(choice), null);
        TokenUsage usage = new TokenUsage(10, 5, 15, null, null, null, null, null, null, null);
        ChatCompletion chatCompletion = new ChatCompletion(TEST_REQUEST_ID, output, usage);
        ResponseEntity<ChatCompletion> responseEntity = ResponseEntity.ok(chatCompletion);

        when(dashScopeApi.chatCompletionEntity(any(), any(), eq(false))).thenReturn(responseEntity);

        ChatResponse response = chatModel.call(prompt);

        assertThat(response.getMetadata()).isNotNull();
        assertThat(response.getMetadata().getId()).isEqualTo(TEST_REQUEST_ID);
        var aiUsage = response.getMetadata().getUsage();
        assertThat(aiUsage.getPromptTokens()).isEqualTo(5);
        assertThat(aiUsage.getCompletionTokens()).isEqualTo(10);
        assertThat(aiUsage.getTotalTokens()).isEqualTo(15);
    }

    @Test
    void testInvalidModelName() {
        // Test handling of invalid model name
        DashScopeChatOptions invalidOptions = DashScopeChatOptions.builder().model("invalid-model").build();

        DashScopeChatModel invalidModel = DashScopeChatModel.builder()
                .dashScopeApi(dashScopeApi)
                .defaultOptions(invalidOptions)
                .build();
        Message message = new UserMessage(TEST_PROMPT);
        Prompt prompt = new Prompt(List.of(message));

        when(dashScopeApi.chatCompletionEntity(any(), any(), eq(false))).thenThrow(new RuntimeException("Invalid model name"));

        assertThatThrownBy(() -> invalidModel.call(prompt)).isInstanceOf(RuntimeException.class)
                .hasMessage("Invalid model name");
    }

    @Test
    void testMultipleMessagesInPrompt() {
        // Test handling of multiple messages in prompt
        SystemMessage systemMessage = new SystemMessage("You are a helpful assistant.");
        UserMessage userMessage1 = new UserMessage("Hello!");
        AssistantMessage assistantMessage = new AssistantMessage("Hi! How can I help you?");
        UserMessage userMessage2 = new UserMessage("What's the weather?");

        ChatCompletionMessage responseMessage = new ChatCompletionMessage("It's sunny today!", Role.ASSISTANT);
        Choice choice = new Choice(ChatCompletionFinishReason.STOP, responseMessage, null, 0);
        ChatCompletionOutput output = new ChatCompletionOutput("It's sunny today!", List.of(choice), null);
        // Add non-null TokenUsage with zero values
        TokenUsage usage = new TokenUsage(10, 5, 15, null, null, null, null, null, null, null);
        ChatCompletion completion = new ChatCompletion("test-id", output, usage);

        when(dashScopeApi.chatCompletionEntity(any(), any(), eq(false))).thenReturn(ResponseEntity.ok(completion));

        Prompt prompt = new Prompt(List.of(systemMessage, userMessage1, assistantMessage, userMessage2));
        ChatResponse response = chatModel.call(prompt);

        assertThat(response).isNotNull();
        assertThat(response.getResult().getOutput().getText()).isEqualTo("It's sunny today!");
    }


    @Test
    void testNullToolNameHandling() {
        // Test that null tool names are filtered out and don't cause NPE
        ToolCallback weatherCallback = mock(ToolCallback.class);
        when(weatherCallback.getToolDefinition()).thenReturn(DefaultToolDefinition.builder()
                .name("get_weather")
                .description("Get weather information")
                .inputSchema(EMPTY_INPUT_SCHEMA)
                .build());

        DashScopeChatOptions options = DashScopeChatOptions.builder()
                .model("qwen-turbo")
                .toolCallbacks(List.of(weatherCallback))
                .build();

        DashScopeChatModel toolChatModel = DashScopeChatModel.builder()
                .dashScopeApi(dashScopeApi)
                .defaultOptions(options)
                .build();

        // Create tool call with null function name
        ChatCompletionFunction nullNameFunction = new ChatCompletionFunction(null, "{\"location\": \"Beijing\"}");
        ToolCall nullNameToolCall = new ToolCall("tool-call-id", "function", nullNameFunction, null);

        ChatCompletionMessage nullNameToolMessage = new ChatCompletionMessage("", Role.ASSISTANT, null, null, List.of(nullNameToolCall), null, null, null, null, null);
        Choice nullNameChoice = new Choice(ChatCompletionFinishReason.TOOL_CALLS, nullNameToolMessage, null, 0);

        // Add non-null TokenUsage with correct parameters - 9 parameters total
        TokenUsage usage = new TokenUsage(10, 5, 15, null, null, null, null, null, null, null);

        ChatCompletionOutput nullNameOutput = new ChatCompletionOutput("", List.of(nullNameChoice), null);
        ChatCompletion nullNameCompletion = new ChatCompletion("test-id", nullNameOutput, usage);

        when(dashScopeApi.chatCompletionEntity(any(), any(), eq(false))).thenReturn(ResponseEntity.ok(nullNameCompletion));

        // Test tool call with null name - should not throw NPE
        Message message = new UserMessage("What's the weather like?");
        Prompt prompt = new Prompt(List.of(message), options);

        // This should not throw NPE anymore
        assertThatCode(() -> {
            ChatResponse response = toolChatModel.call(prompt);
            assertThat(response).isNotNull();
            // Tool calls with null names should be filtered out
            assertThat(response.getResults().get(0).getOutput().getToolCalls()).isEmpty();
        }).doesNotThrowAnyException();
    }

    @Test
    void testPartialModeForCodeCompletion() {
        // Test partial mode support for code completion scenarios (Issue #298)
        List<Message> messages = List.of(new UserMessage("Please complete this Fibonacci function."), AssistantMessage.builder()
                .content("""
                        def calculate_fibonacci(n):
                        	if n <= 1:
                        		return n
                        	else:
                        """)
                .properties(Map.of("partial", true))
                .build());

        Prompt prompt = new Prompt(messages, DashScopeChatOptions.builder().build());
        ChatCompletionRequest request = chatModel.createRequest(prompt);

        var requestMessages = request.input().messages();
        assertThat(requestMessages).isNotEmpty();
        assertThat(requestMessages.size()).isEqualTo(2);

        var lastMessage = requestMessages.get(1);
        assertThat(lastMessage.role()).isEqualTo(Role.ASSISTANT);
        assertThat(lastMessage.partial()).isNotNull();
        assertThat(lastMessage.partial()).isTrue();
        assertThat(lastMessage.content().toString()).contains("def calculate_fibonacci");
    }

    @Test
    void testPartialModeWithStringValue() {
        // Test partial mode when set as string "true" in metadata
        AssistantMessage assistantMessage = AssistantMessage.builder().content("""
                def calculate_fibonacci(n):
                	if n <= 1:
                		return n
                	else:
                """).properties(Map.of("partial", "true")).build();

        List<Message> messages = List.of(new UserMessage("Please complete this function."), assistantMessage);

        Prompt prompt = new Prompt(messages, DashScopeChatOptions.builder().build());
        ChatCompletionRequest request = chatModel.createRequest(prompt);

        var requestMessages = request.input().messages();
        var lastMessage = requestMessages.get(requestMessages.size() - 1);

        assertThat(lastMessage.partial()).isNotNull();
        assertThat(lastMessage.partial()).isTrue();
    }

    @Test
    void testPartialModeWithFalseStringValue() {
        AssistantMessage assistantMessage = AssistantMessage.builder()
                .content("The function is already complete.")
                .properties(Map.of("partial", "false"))
                .build();

        List<Message> messages = List.of(new UserMessage("Please complete this function."), assistantMessage);

        Prompt prompt = new Prompt(messages, DashScopeChatOptions.builder().build());
        ChatCompletionRequest request = chatModel.createRequest(prompt);

        var requestMessages = request.input().messages();
        var lastMessage = requestMessages.get(requestMessages.size() - 1);

        assertThat(lastMessage.partial()).isNotNull();
        assertThat(lastMessage.partial()).isFalse();
    }

    @Test
    void testWithoutPartialMode() {
        // Test normal assistant message without partial flag
        AssistantMessage assistantMessage = new AssistantMessage("This is a normal response");

        List<Message> messages = List.of(new UserMessage("Hello"), assistantMessage);

        Prompt prompt = new Prompt(messages, DashScopeChatOptions.builder().build());
        ChatCompletionRequest request = chatModel.createRequest(prompt);

        var requestMessages = request.input().messages();
        var lastMessage = requestMessages.get(requestMessages.size() - 1);

        assertThat(lastMessage.partial()).isNull();
    }

    @Test
    void testCallWithIncrementalOutput() {
        var message = UserMessage.builder().text(TEST_PROMPT).build();
        var prompt = Prompt.builder().messages(message).chatOptions(defaultOptions).build();
        var responseMessage = new ChatCompletionMessage(TEST_RESPONSE, Role.ASSISTANT);
        var choice = new Choice(ChatCompletionFinishReason.STOP, responseMessage, null, 0);
        var output = new ChatCompletionOutput(TEST_RESPONSE, List.of(choice), null);
        var usage = new TokenUsage(10, 5, 15, null, null, null, null, null, null, null);
        var chatCompletion = new ChatCompletion(TEST_REQUEST_ID, output, usage);
        var responseEntity = ResponseEntity.ok(chatCompletion);

        when(dashScopeApi.chatCompletionEntity(any(), any(), eq(false))).thenReturn(responseEntity);

        var request = chatModel.createRequest(prompt);
        assertThat(request.parameters().incrementalOutput()).isTrue();
        assertThat(JsonMapper.builder().build().writeValueAsString(request)).doesNotContain("\"stream\"");

        var chatResponse = chatModel.call(prompt);
        assertThat(chatResponse).isNotNull();
    }

    @Test
    void testStreamWithIncrementalOutput() {
        var message = UserMessage.builder().text(TEST_PROMPT).build();
        var prompt = Prompt.builder().messages(message).chatOptions(defaultOptions).build();
        var responseMessage = new ChatCompletionMessage(TEST_RESPONSE, Role.ASSISTANT);
        var choice = new Choice(ChatCompletionFinishReason.STOP, responseMessage, null, 0);
        var output = new ChatCompletionOutput(TEST_RESPONSE, List.of(choice), null);
        var usage = new TokenUsage(10, 5, 15, null, null, null, null, null, null, null);
        var chunk = new ChatCompletionChunk(TEST_REQUEST_ID, output, usage, null);

        when(dashScopeApi.chatCompletionStream(any(), any(), eq(false))).thenReturn(Flux.just(chunk));

        var request = chatModel.createRequest(prompt);
        assertThat(request.parameters().incrementalOutput()).isTrue();
        assertThat(JsonMapper.builder().build().writeValueAsString(request)).doesNotContain("\"stream\"");

        StepVerifier.create(chatModel.stream(prompt))
                .assertNext(chatResponse -> assertThat(chatResponse.getResult().getOutput().getText()).isEqualTo(TEST_RESPONSE))
                .verifyComplete();
    }

    @Test
    void testStreamWithoutIncrementalOutput() {
        defaultOptions = defaultOptions.mutate().incrementalOutput(false).build();
        chatModel.setDashScopeChatOptions(defaultOptions);
        var message = UserMessage.builder().text(TEST_PROMPT).build();
        var prompt = Prompt.builder().messages(message).chatOptions(defaultOptions).build();
        var responseMessage = new ChatCompletionMessage(TEST_RESPONSE, Role.ASSISTANT);
        var choice = new Choice(ChatCompletionFinishReason.STOP, responseMessage, null, 0);
        var output = new ChatCompletionOutput(TEST_RESPONSE, List.of(choice), null);
        var usage = new TokenUsage(10, 5, 15, null, null, null, null, null, null, null);
        var chunk = new ChatCompletionChunk(TEST_REQUEST_ID, output, usage, null);

        when(dashScopeApi.chatCompletionStream(any(), any(), eq(false))).thenReturn(Flux.just(chunk));

        var request = chatModel.createRequest(prompt);
        assertThat(request.parameters().incrementalOutput()).isFalse();

        StepVerifier.create(chatModel.stream(prompt))
                .assertNext(chatResponse -> assertThat(chatResponse.getResult().getOutput().getText()).isEqualTo(TEST_RESPONSE))
                .verifyComplete();
    }

    @Test
    void testStreamErrorResponse() {
        Message message = new UserMessage("Test error handling");
        Prompt prompt = new Prompt(List.of(message));

        when(dashScopeApi.chatCompletionStream(any(), any(), eq(false))).thenReturn(Flux.error(new com.alibaba.cloud.ai.dashscope.common.DashScopeException("InvalidParameter  (requestId: error-request-123)")));

        Flux<ChatResponse> responseFlux = chatModel.stream(prompt);

        StepVerifier.create(responseFlux)
                .expectErrorMatches(throwable ->
                        throwable instanceof com.alibaba.cloud.ai.dashscope.common.DashScopeException
                                && throwable.getMessage().contains("InvalidParameter") && throwable.getMessage()
                                .contains("error-request-123"))
                .verify();
    }

    @Test
    void testStreamErrorResponseWithCode() {
        Message message = new UserMessage("Test error handling");
        Prompt prompt = new Prompt(List.of(message));

        when(dashScopeApi.chatCompletionStream(any(), any(), eq(false))).thenReturn(Flux.error(
                new com.alibaba.cloud.ai.dashscope.common.DashScopeException("InvalidParameter",
                        "[InvalidParameter] invalid input (requestId: error-request-123)")));

        Flux<ChatResponse> responseFlux = chatModel.stream(prompt);

        StepVerifier.create(responseFlux)
                .expectErrorMatches(throwable ->
                        throwable instanceof com.alibaba.cloud.ai.dashscope.common.DashScopeException
                                && ((com.alibaba.cloud.ai.dashscope.common.DashScopeException) throwable).getCode()
                                .equals("InvalidParameter")
                                && throwable.getMessage().contains("InvalidParameter")
                                && throwable.getMessage().contains("error-request-123"))
                .verify();
    }

    @Test
    void testBuildRequestPrompt() {
        DashScopeChatOptions runtimeOptions = DashScopeChatOptions.builder()
                .model("qwen-plus")
                .enableThinking(true)
                .thinkingBudget(50)
                .build();
        Prompt prompt = chatModel.buildRequestPrompt(Prompt.builder()
                .content(TEST_PROMPT)
                .chatOptions(runtimeOptions)
                .build());
        assertThat(prompt.getOptions().getModel()).isEqualTo("qwen-plus");
        assertThat(prompt.getOptions().getTemperature()).isEqualTo(0.7);
        assertThat(prompt.getOptions().getTopP()).isEqualTo(0.8);
        assertThat(prompt.getOptions().getTopK()).isEqualTo(50);
        assertThat(prompt.getOptions()).isInstanceOf(DashScopeChatOptions.class);
        assertThat(prompt.getOptions()).asInstanceOf(type(DashScopeChatOptions.class))
                .extracting(DashScopeChatOptions::getSeed)
                .isEqualTo(1234);
        assertThat(prompt.getOptions()).asInstanceOf(type(DashScopeChatOptions.class))
                .extracting(DashScopeChatOptions::getEnableThinking)
                .isEqualTo(true);
        assertThat(prompt.getOptions()).asInstanceOf(type(DashScopeChatOptions.class))
                .extracting(DashScopeChatOptions::getThinkingBudget)
                .isEqualTo(50);
    }

    @Test
    void testBuildRequestPromptKeepsDefaultMultiModelWhenRuntimeUsesToolCallingOptions() {
        DashScopeChatOptions multiModelOptions = DashScopeChatOptions.builder()
                .model("qwen3.6-plus")
                .multiModel(true)
                .build();
        DashScopeChatModel multiModelChatModel = DashScopeChatModel.builder()
                .dashScopeApi(dashScopeApi)
                .defaultOptions(multiModelOptions)
                .build();
        ToolCallingChatOptions runtimeToolOptions = ToolCallingChatOptions.builder()
                .toolContext(Map.of("key1", "value1"))
                .build();

        Prompt requestPrompt = multiModelChatModel.buildRequestPrompt(Prompt.builder()
                .content(TEST_PROMPT)
                .chatOptions(runtimeToolOptions)
                .build());
        ChatCompletionRequest request = multiModelChatModel.createRequest(requestPrompt);

        assertThat(requestPrompt.getOptions()).isInstanceOf(DashScopeChatOptions.class);
        assertThat(requestPrompt.getOptions()).asInstanceOf(type(DashScopeChatOptions.class))
                .extracting(DashScopeChatOptions::getMultiModel)
                .isEqualTo(true);
        assertThat(requestPrompt.getOptions()).asInstanceOf(type(DashScopeChatOptions.class))
                .extracting(DashScopeChatOptions::getToolContext)
                .isEqualTo(Map.of("key1", "value1"));
        assertThat(JsonMapper.builder().build().writeValueAsString(request)).doesNotContain("\"multi_model\"");
    }

    @Test
    void testCreateRequest() {
        DashScopeChatOptions runtimeOptions = DashScopeChatOptions.builder()
                .model(TEST_MODEL)
                .enableThinking(true)
                .thinkingBudget(50)
                .build();
        ChatCompletionRequest request = chatModel.createRequest(Prompt.builder()
                .content(TEST_PROMPT)
                .chatOptions(runtimeOptions)
                .build());

        String jsonRequest = JsonMapper.builder().build().writeValueAsString(request);

        assertThat(request.model()).isEqualTo(TEST_MODEL);
        assertThat(request.parameters().enableThinking()).isEqualTo(true);
        assertThat(jsonRequest).contains("\"model\":\"" + TEST_MODEL + "\"");
        assertThat(jsonRequest).contains("\"enable_thinking\":" + true);
        assertThat(jsonRequest).contains("\"thinking_budget\":" + 50);
        assertThat(jsonRequest).doesNotContain("\"stream\"");
        assertThat(jsonRequest).doesNotContain("\"multi_model\"");
        assertThat(jsonRequest).doesNotContain("\"forced_search\"");

    }

    // for mock
    private SearchInfo createMockSearchInfo() {
        SearchResult searchResult = new SearchResult(
                "Example Site",
                "https://example.com/favicon.ico",
                1,
                "Example Title",
                "https://example.com/page"
        );
        return new SearchInfo(List.of(searchResult), null);
    }

    @Test
    void testCallPreservesSearchInfo() {

        // Test that call() method preserves searchInfo in metadata
        SearchInfo searchInfo = createMockSearchInfo();

        ChatCompletionMessage responseMessage = new ChatCompletionMessage(
                TEST_RESPONSE,
                Role.ASSISTANT
        );
        Choice choice = new Choice(ChatCompletionFinishReason.STOP, responseMessage, null, 0);
        // Include searchInfo in the output
        ChatCompletionOutput output = new ChatCompletionOutput(TEST_RESPONSE, List.of(choice), searchInfo);
        TokenUsage usage = new TokenUsage(10, 5, 15, null, null, null, null, null, null, null);
        ChatCompletion chatCompletion = new ChatCompletion(TEST_REQUEST_ID, output, usage);
        ResponseEntity<ChatCompletion> responseEntity = ResponseEntity.ok(chatCompletion);

        when(dashScopeApi.chatCompletionEntity(any(ChatCompletionRequest.class), any(), eq(false)))
                .thenReturn(responseEntity);

        // Create prompt
        UserMessage message = UserMessage.builder().text(TEST_PROMPT).build();
        Prompt prompt = new Prompt(message);

        // Call the chat model
        ChatResponse response = chatModel.call(prompt);

        // Verify response
        assertThat(response).isNotNull();
        assertThat(response.getResult()).isNotNull();
        assertThat(response.getResult().getOutput()).isNotNull();

        // Verify searchInfo is preserved in metadata
        Object searchInfoFromMetadata = response.getResult().getOutput().getMetadata().get("search_info");
        assertThat(searchInfoFromMetadata)
                .as("searchInfo should be present in call() response metadata")
                .isNotNull()
                .isInstanceOf(SearchInfo.class);

        SearchInfo retrievedSearchInfo = (SearchInfo) searchInfoFromMetadata;
        assertThat(retrievedSearchInfo.searchResults())
                .as("searchInfo should contain search results")
                .isNotEmpty();
        assertThat(retrievedSearchInfo.searchResults().get(0).title())
                .isEqualTo("Example Title");
    }

    @Test
    void testStreamPreservesSearchInfo() {
        // Test that stream() method preserves searchInfo in metadata
        SearchInfo searchInfo = createMockSearchInfo();

        // First chunk - partial response without searchInfo
        ChatCompletionMessage chunkMessage1 = new ChatCompletionMessage(
                "Hello ",
                Role.ASSISTANT
        );
        Choice choice1 = new Choice(null, chunkMessage1, null, 0);
        ChatCompletionOutput output1 = new ChatCompletionOutput("Hello ", List.of(choice1), null);
        ChatCompletionChunk chunk1 = new ChatCompletionChunk(TEST_REQUEST_ID, output1, null, null);

        // Second chunk - final response WITH searchInfo
        ChatCompletionMessage chunkMessage2 = new ChatCompletionMessage(
                "World!",
                Role.ASSISTANT
        );
        Choice choice2 = new Choice(ChatCompletionFinishReason.STOP, chunkMessage2, null, 0);
        // Include searchInfo in the final chunk's output
        ChatCompletionOutput output2 = new ChatCompletionOutput("World!", List.of(choice2), searchInfo);
        TokenUsage usage = new TokenUsage(10, 5, 15, null, null, null, null, null, null, null);
        ChatCompletionChunk chunk2 = new ChatCompletionChunk(TEST_REQUEST_ID, output2, usage, null);

        when(dashScopeApi.chatCompletionStream(any(ChatCompletionRequest.class), any(), eq(false)))
                .thenReturn(Flux.just(chunk1, chunk2));

        // Create prompt
        UserMessage message = UserMessage.builder().text(TEST_PROMPT).build();
        Prompt prompt = new Prompt(message);

        // Call the streaming API and verify
        StepVerifier.create(chatModel.stream(prompt))
                .assertNext(response -> {
                    // First chunk - searchInfo is null, converted to empty string
                    assertThat(response.getResult().getOutput().getText()).isEqualTo("Hello ");
                    Object si = response.getResult().getOutput().getMetadata().get("search_info");
                    assertThat(si).isEqualTo("");
                })
                .assertNext(response -> {
                    // Second chunk should have searchInfo
                    assertThat(response.getResult().getOutput().getText()).isEqualTo("World!");
                    Object si = response.getResult().getOutput().getMetadata().get("search_info");
                    assertThat(si)
                            .as("searchInfo should be present in stream() response metadata")
                            .isNotNull()
                            .isNotEqualTo("")
                            .isInstanceOf(SearchInfo.class);

                    SearchInfo searchInfoResult = (SearchInfo) si;
                    assertThat(searchInfoResult.searchResults()).isNotEmpty();
                    assertThat(searchInfoResult.searchResults().get(0).title())
                            .isEqualTo("Example Title");
                })
                .verifyComplete();
    }

    @Test
    void testChunkToChatCompletionPreservesSearchInfo() {
        // Test that chunk to completion conversion preserves searchInfo
        SearchInfo searchInfo = createMockSearchInfo();

        ChatCompletionMessage message = new ChatCompletionMessage(
                TEST_RESPONSE,
                Role.ASSISTANT
        );
        Choice choice = new Choice(ChatCompletionFinishReason.STOP, message, null, 0);
        ChatCompletionOutput output = new ChatCompletionOutput(TEST_RESPONSE, List.of(choice), searchInfo);
        TokenUsage usage = new TokenUsage(10, 5, 15, null, null, null, null, null, null, null);
        ChatCompletionChunk chunk = new ChatCompletionChunk(TEST_REQUEST_ID, output, usage, null);

        when(dashScopeApi.chatCompletionStream(any(ChatCompletionRequest.class), any(), eq(false)))
                .thenReturn(Flux.just(chunk));

        // Create prompt
        UserMessage userMessage = UserMessage.builder().text(TEST_PROMPT).build();
        Prompt prompt = new Prompt(userMessage);

        // Call the streaming API and collect all responses
        List<ChatResponse> responses = chatModel.stream(prompt).collectList().block();

        assertThat(responses)
                .as("Should have received responses")
                .isNotNull()
                .isNotEmpty();

        // Check the response contains searchInfo
        ChatResponse lastResponse = responses.get(responses.size() - 1);
        Object searchInfoFromMetadata = lastResponse.getResult().getOutput().getMetadata().get("search_info");

        assertThat(searchInfoFromMetadata)
                .as("searchInfo should be preserved after chunk to completion conversion")
                .isNotNull()
                .isNotEqualTo("")
                .isInstanceOf(SearchInfo.class);

        SearchInfo retrievedSearchInfo = (SearchInfo) searchInfoFromMetadata;
        assertThat(retrievedSearchInfo.searchResults()).isNotEmpty();
        assertThat(retrievedSearchInfo.searchResults().get(0).url())
                .isEqualTo("https://example.com/page");
    }

    @Test
    void testCreateRequestWithStreamOptions() {
        SearchOptions searchOptions = new SearchOptions(false, false, "[<number>]", "turbo", true, true);
        DashScopeChatOptions runtimeOptions = DashScopeChatOptions.builder()
                .model(TEST_MODEL)
                .searchOptions(searchOptions)
                .build();
        ChatCompletionRequest request = chatModel.createRequest(Prompt.builder()
                .content(TEST_PROMPT)
                .chatOptions(runtimeOptions)
                .build());

        assertThat(request.model()).isEqualTo(TEST_MODEL);
        assertThat(request.parameters().searchOptions().enableSource()).isFalse();
        assertThat(request.parameters().searchOptions().enableCitation()).isFalse();
        assertThat(request.parameters().searchOptions().citationFormat()).isEqualTo("[<number>]");
        assertThat(request.parameters().searchOptions().searchStrategy()).isEqualTo("turbo");
        assertThat(request.parameters().searchOptions().enableSearchExtension()).isTrue();
        assertThat(request.parameters().searchOptions().prependSearchResult()).isTrue();
        assertThat(JsonMapper.builder().build().writeValueAsString(request)).doesNotContain("\"forced_search\"");
    }

    @Test
    void testBuilder() {
        DashScopeChatModel model1 = DashScopeChatModel.builder()
                .dashScopeApi(dashScopeApi)
                .build();

        DashScopeChatModel model2 = DashScopeChatModel.builder()
                .dashScopeApi(dashScopeApi)
                .defaultOptions(DashScopeChatOptions.builder()
                        .model(TEST_MODEL)
                        .build())
                .toolCallingManager(ToolCallingManager.builder().build())
                .retryTemplate(RetryUtils.DEFAULT_RETRY_TEMPLATE)
                .observationRegistry(ObservationRegistry.NOOP)
                .build();

        DashScopeChatModel clone1 = model1.clone();
        DashScopeChatModel clone2 = model2.clone();

        Builder mutate1 = model1.mutate();
        Builder mutate2 = model2.mutate();

        assertThat(model1).isNotNull();
        assertThat(model2).isNotNull();
        assertThat(clone1).isNotNull();
        assertThat(clone2).isNotNull();
        assertThat(mutate1).isNotNull();
        assertThat(mutate2).isNotNull();
    }

    @Test
    void testChatResponseUsageContainsDashScopeNativeUsage() {
        // Test that ChatResponse.metadata.usage is DashScopeAiUsage with native TokenUsage
        Message message = new UserMessage(TEST_PROMPT);
        Prompt prompt = new Prompt(List.of(message));

        // Build TokenUsage with prompt_tokens_details (explicit cache related fields)
        var cacheCreation = new CacheCreation(1024);
        var promptTokenDetailed = new PromptTokenDetailed(
                128,           // cachedTokens
                cacheCreation, // cacheCreation
                1024,          // cacheCreationInputTokens
                "ephemeral_5m" // cacheType
        );
        TokenUsage usage = new TokenUsage(10, 5, 15, null, null, null, null, null, null, promptTokenDetailed);

        ChatCompletionMessage responseMessage = new ChatCompletionMessage(TEST_RESPONSE, Role.ASSISTANT);
        Choice choice = new Choice(ChatCompletionFinishReason.STOP, responseMessage, null, 0);
        ChatCompletionOutput output = new ChatCompletionOutput(TEST_RESPONSE, List.of(choice), null);
        ChatCompletion chatCompletion = new ChatCompletion(TEST_REQUEST_ID, output, usage);

        when(dashScopeApi.chatCompletionEntity(any(ChatCompletionRequest.class), any(), eq(false))).thenReturn(ResponseEntity.ok(chatCompletion));

        ChatResponse response = chatModel.call(prompt);

        // Verify usage is DashScopeAiUsage instance
        assertThat(response.getMetadata().getUsage()).isInstanceOf(com.alibaba.cloud.ai.dashscope.metadata.DashScopeAiUsage.class);

        var dashScopeUsage = (com.alibaba.cloud.ai.dashscope.metadata.DashScopeAiUsage) response.getMetadata().getUsage();

        // Verify getNativeUsage returns the original TokenUsage
        assertThat(dashScopeUsage.getNativeUsage()).isSameAs(usage);

        // Verify cache-related fields are accessible via native usage
        TokenUsage nativeUsage = (TokenUsage) dashScopeUsage.getNativeUsage();
        assertThat(nativeUsage.promptTokenDetailed()).isNotNull();
        assertThat(nativeUsage.promptTokenDetailed().cachedTokens()).isEqualTo(128);
        assertThat(nativeUsage.promptTokenDetailed().cacheType()).isEqualTo("ephemeral_5m");
        assertThat(nativeUsage.promptTokenDetailed().cacheCreationInputTokens()).isEqualTo(1024);
        assertThat(nativeUsage.promptTokenDetailed().cacheCreation().ephemeral5mInputTokens()).isEqualTo(1024);
    }

    @Test
    void testUserMessageWithCacheControl() {
        // Test that cache_control in UserMessage metadata is properly processed
        Map<String, Object> cacheControl = Map.of("type", "ephemeral");
        Message message = UserMessage.builder()
                .text(TEST_PROMPT)
                .metadata(Map.of("cache_control", cacheControl))
                .build();
        Prompt prompt = new Prompt(List.of(message));

        // Mock API response
        ChatCompletionMessage responseMessage = new ChatCompletionMessage(TEST_RESPONSE, Role.ASSISTANT);
        Choice choice = new Choice(ChatCompletionFinishReason.STOP, responseMessage, null, 0);
        ChatCompletionOutput output = new ChatCompletionOutput(TEST_RESPONSE, List.of(choice), null);
        TokenUsage usage = new TokenUsage(10, 5, 15, null, null, null, null, null, null, null);
        ChatCompletion chatCompletion = new ChatCompletion(TEST_REQUEST_ID, output, usage);

        when(dashScopeApi.chatCompletionEntity(any(ChatCompletionRequest.class), any(), eq(false))).thenReturn(ResponseEntity.ok(chatCompletion));

        // Should not throw any exception
        ChatResponse response = chatModel.call(prompt);

        assertThat(response).isNotNull();
        assertThat(response.getResult().getOutput().getText()).isEqualTo(TEST_RESPONSE);
    }

    @Test
    void testSystemMessageWithCacheControl() {
        // Test that cache_control in SystemMessage metadata is properly processed
        Map<String, Object> cacheControl = Map.of("type", "ephemeral");
        Message systemMessage = SystemMessage.builder()
                .text("You are a helpful assistant.")
                .metadata(Map.of("cache_control", cacheControl))
                .build();
        Message userMessage = new UserMessage("Hello!");

        // Mock API response
        ChatCompletionMessage responseMessage = new ChatCompletionMessage(TEST_RESPONSE, Role.ASSISTANT);
        Choice choice = new Choice(ChatCompletionFinishReason.STOP, responseMessage, null, 0);
        ChatCompletionOutput output = new ChatCompletionOutput(TEST_RESPONSE, List.of(choice), null);
        TokenUsage usage = new TokenUsage(10, 5, 15, null, null, null, null, null, null, null);
        ChatCompletion chatCompletion = new ChatCompletion(TEST_REQUEST_ID, output, usage);

        when(dashScopeApi.chatCompletionEntity(any(ChatCompletionRequest.class), any(), eq(false))).thenReturn(ResponseEntity.ok(chatCompletion));

        Prompt prompt = new Prompt(List.of(systemMessage, userMessage));
        ChatResponse response = chatModel.call(prompt);

        assertThat(response).isNotNull();
        assertThat(response.getResult().getOutput().getText()).isEqualTo(TEST_RESPONSE);
    }

    @Test
    void testCreateRequestWithCacheControlInUserMessage() {
        // Test that createRequest properly converts cache_control to MediaContent
        Map<String, Object> cacheControl = Map.of("type", "ephemeral");
        Message message = UserMessage.builder()
                .text(TEST_PROMPT)
                .metadata(Map.of("cache_control", cacheControl))
                .build();
        Prompt prompt = new Prompt(List.of(message), defaultOptions);

        // Use createRequest directly for unit testing
        ChatCompletionRequest request = chatModel.createRequest(prompt);

        assertThat(request).isNotNull();
        assertThat(request.input()).isNotNull();
        assertThat(request.input().messages()).isNotEmpty();

        // The message content should be converted to List<MediaContent> with cache_control
        var firstMessage = request.input().messages().get(0);
        assertThat(firstMessage.rawContent()).isInstanceOf(List.class);

        @SuppressWarnings("unchecked")
        List<MediaContent> contentList = (List<MediaContent>) firstMessage.rawContent();

        assertThat(contentList).hasSize(1);
        assertThat(contentList.get(0).text()).isEqualTo(TEST_PROMPT);
        assertThat(contentList.get(0).cacheControl()).isNotNull();
        assertThat(contentList.get(0).cacheControl().get("type")).isEqualTo("ephemeral");
    }

    @Test
    void testCreateRequestWithCacheControlInSystemMessage() {
        // Test that createRequest properly converts cache_control in SystemMessage
        Map<String, Object> cacheControl = Map.of("type", "ephemeral");
        Message systemMessage = SystemMessage.builder()
                .text("You are a helpful assistant.")
                .metadata(Map.of("cache_control", cacheControl))
                .build();
        Message userMessage = new UserMessage("Hello!");
        Prompt prompt = new Prompt(List.of(systemMessage, userMessage), defaultOptions);

        ChatCompletionRequest request = chatModel.createRequest(prompt);

        assertThat(request).isNotNull();
        assertThat(request.input().messages()).hasSize(2);

        // First message (system) should have cache_control
        var firstMessage = request.input().messages().get(0);
        assertThat(firstMessage.rawContent()).isInstanceOf(List.class);

        @SuppressWarnings("unchecked")
        List<MediaContent> contentList = (List<MediaContent>) firstMessage.rawContent();

        assertThat(contentList).hasSize(1);
        assertThat(contentList.get(0).cacheControl()).isNotNull();
        assertThat(contentList.get(0).cacheControl().get("type")).isEqualTo("ephemeral");

        // Second message (user) should be plain text (no cache_control)
        var secondMessage = request.input().messages().get(1);
        assertThat(secondMessage.rawContent()).isInstanceOf(String.class);
    }

    @Test
    void testCreateRequestWithCacheControlInAssistantMessage() {
        Map<String, Object> cacheControl = Map.of("type", "ephemeral");
        AssistantMessage assistantMessage = AssistantMessage.builder()
                .content(TEST_RESPONSE)
                .properties(Map.of("cache_control", cacheControl))
                .build();
        Prompt prompt = new Prompt(List.of(new UserMessage(TEST_PROMPT), assistantMessage), defaultOptions);

        ChatCompletionRequest request = chatModel.createRequest(prompt, false);

        var assistantRequestMessage = request.input().messages().get(1);
        assertThat(assistantRequestMessage.role()).isEqualTo(ChatCompletionMessage.Role.ASSISTANT);
        assertThat(assistantRequestMessage.rawContent()).isInstanceOf(List.class);

        @SuppressWarnings("unchecked")
        List<MediaContent> contentList = (List<MediaContent>) assistantRequestMessage.rawContent();

        assertThat(contentList).hasSize(1);
        assertThat(contentList.get(0).text()).isEqualTo(TEST_RESPONSE);
        assertThat(contentList.get(0).cacheControl()).containsEntry("type", "ephemeral");
    }

    @Test
    void testCreateRequestWithCacheControlInAssistantMessageKeepsPartial() {
        Map<String, Object> cacheControl = Map.of("type", "ephemeral");
        AssistantMessage assistantMessage = AssistantMessage.builder()
                .content("def fibonacci(n):")
                .properties(Map.of("cache_control", cacheControl, "partial", true))
                .build();
        Prompt prompt = new Prompt(List.of(new UserMessage(TEST_PROMPT), assistantMessage), defaultOptions);

        ChatCompletionRequest request = chatModel.createRequest(prompt, false);

        var assistantRequestMessage = request.input().messages().get(1);
        assertThat(assistantRequestMessage.partial()).isTrue();
        assertThat(assistantRequestMessage.rawContent()).isInstanceOf(List.class);

        @SuppressWarnings("unchecked")
        List<MediaContent> contentList = (List<MediaContent>) assistantRequestMessage.rawContent();

        assertThat(contentList.get(0).text()).isEqualTo("def fibonacci(n):");
        assertThat(contentList.get(0).cacheControl()).containsEntry("type", "ephemeral");
    }

    @Test
    void testCreateRequestWithCacheControlInAssistantMessageKeepsToolCalls() {
        Map<String, Object> cacheControl = Map.of("type", "ephemeral");
        AssistantMessage assistantMessage = AssistantMessage.builder()
                .content("Calling weather tool.")
                .properties(Map.of("cache_control", cacheControl))
                .toolCalls(List.of(new AssistantMessage.ToolCall("call-1", "function", "get_weather", "{\"city\":\"HZ\"}")))
                .build();
        Prompt prompt = new Prompt(List.of(new UserMessage(TEST_PROMPT), assistantMessage), defaultOptions);

        ChatCompletionRequest request = chatModel.createRequest(prompt, false);

        var assistantRequestMessage = request.input().messages().get(1);
        assertThat(assistantRequestMessage.toolCalls()).hasSize(1);
        assertThat(assistantRequestMessage.toolCalls().get(0).id()).isEqualTo("call-1");
        assertThat(assistantRequestMessage.toolCalls().get(0).function().name()).isEqualTo("get_weather");
        assertThat(assistantRequestMessage.rawContent()).isInstanceOf(List.class);

        @SuppressWarnings("unchecked")
        List<MediaContent> contentList = (List<MediaContent>) assistantRequestMessage.rawContent();

        assertThat(contentList.get(0).cacheControl()).containsEntry("type", "ephemeral");
    }

    @Test
    void testCreateRequestWithCacheControlInToolResponseMessage() {
        Map<String, Object> cacheControl = Map.of("type", "ephemeral");
        ToolResponseMessage toolResponseMessage = ToolResponseMessage.builder()
                .responses(List.of(
                        new ToolResponseMessage.ToolResponse("call-1", "get_weather", "{\"city\":\"HZ\"}"),
                        new ToolResponseMessage.ToolResponse("call-2", "get_time", "{\"timezone\":\"Asia/Shanghai\"}")))
                .metadata(Map.of("cache_control", cacheControl))
                .build();
        Prompt prompt = new Prompt(List.of(toolResponseMessage), defaultOptions);

        ChatCompletionRequest request = chatModel.createRequest(prompt, false);

        assertThat(request.input().messages()).hasSize(2);
        var firstToolMessage = request.input().messages().get(0);
        assertThat(firstToolMessage.role()).isEqualTo(ChatCompletionMessage.Role.TOOL);
        assertThat(firstToolMessage.name()).isEqualTo("get_weather");
        assertThat(firstToolMessage.toolCallId()).isEqualTo("call-1");
        assertThat(firstToolMessage.rawContent()).isInstanceOf(String.class);
        assertThat(firstToolMessage.rawContent()).isEqualTo("{\"city\":\"HZ\"}");

        var secondToolMessage = request.input().messages().get(1);
        assertThat(secondToolMessage.name()).isEqualTo("get_time");
        assertThat(secondToolMessage.toolCallId()).isEqualTo("call-2");
        assertThat(secondToolMessage.rawContent()).isInstanceOf(List.class);

        @SuppressWarnings("unchecked")
        List<MediaContent> secondContentList = (List<MediaContent>) secondToolMessage.rawContent();

        assertThat(secondContentList.get(0).text()).isEqualTo("{\"timezone\":\"Asia/Shanghai\"}");
        assertThat(secondContentList.get(0).cacheControl()).containsEntry("type", "ephemeral");
    }

    @Test
    void testCreateRequestWithoutCacheControl() {
        // Test that message without cache_control stays as plain text
        Message message = new UserMessage(TEST_PROMPT);
        Prompt prompt = new Prompt(List.of(message), defaultOptions);

        ChatCompletionRequest request = chatModel.createRequest(prompt);

        assertThat(request).isNotNull();
        var firstMessage = request.input().messages().get(0);
        // Without cache_control, content should remain as plain String
        assertThat(firstMessage.rawContent()).isInstanceOf(String.class);
        assertThat(firstMessage.rawContent()).isEqualTo(TEST_PROMPT);
    }

    @Test
    void testCreateRequestWithoutCacheControlInAssistantAndToolMessages() {
        AssistantMessage assistantMessage = new AssistantMessage(TEST_RESPONSE);
        ToolResponseMessage toolResponseMessage = ToolResponseMessage.builder()
                .responses(List.of(new ToolResponseMessage.ToolResponse("call-1", "get_weather", "{\"city\":\"HZ\"}")))
                .metadata(Map.of())
                .build();
        Prompt prompt = new Prompt(List.of(assistantMessage, toolResponseMessage), defaultOptions);

        ChatCompletionRequest request = chatModel.createRequest(prompt, false);

        var assistantRequestMessage = request.input().messages().get(0);
        assertThat(assistantRequestMessage.rawContent()).isInstanceOf(String.class);
        assertThat(assistantRequestMessage.rawContent()).isEqualTo(TEST_RESPONSE);

        var toolRequestMessage = request.input().messages().get(1);
        assertThat(toolRequestMessage.rawContent()).isInstanceOf(String.class);
        assertThat(toolRequestMessage.rawContent()).isEqualTo("{\"city\":\"HZ\"}");
    }

    @Test
    void testCreateRequestWithEnableCodeInterpreter() {
        Message message = new UserMessage(TEST_PROMPT);
        DashScopeChatOptions options = DashScopeChatOptions.builder()
                .enableCodeInterpreter(true)
                .build();
        Prompt prompt = new Prompt(List.of(message), options);

        ChatCompletionRequest request = chatModel.createRequest(prompt);

        assertThat(request).isNotNull();
        assertThat(request.parameters().enableCodeInterpreter()).isTrue();
    }

    @Test
    void testCreateRequestWithoutEnableCodeInterpreter() {
        Message message = new UserMessage(TEST_PROMPT);
        Prompt prompt = new Prompt(List.of(message), defaultOptions);

        ChatCompletionRequest request = chatModel.createRequest(prompt);

        assertThat(request).isNotNull();
        assertThat(request.parameters().enableCodeInterpreter()).isNull();
    }
}
