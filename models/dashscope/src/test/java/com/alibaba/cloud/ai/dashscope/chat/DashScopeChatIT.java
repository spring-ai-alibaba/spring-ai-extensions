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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.context.i18n.LocaleContextHolder;
import reactor.core.publisher.Flux;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for DashScope Chat functionality. These tests will only run if
 * AI_DASHSCOPE_API_KEY environment variable is set.
 *
 * @author brianxiadong
 * @author xuguan
 * @since 1.0.0-M5.1
 */
@Tag("integration")
@EnabledIfEnvironmentVariable(named = "AI_DASHSCOPE_API_KEY", matches = ".+")
class DashScopeChatIT {

    // Test constants
    private static final String TEST_MODEL = "qwen-turbo";

    private static final String TEST_PROMPT = "你好，请介绍一下你自己。";

    private static final String API_KEY_ENV = "AI_DASHSCOPE_API_KEY";

    private String apiKey;

    @BeforeEach
    void setUp() {
        // Get API key from environment variable
        apiKey = System.getenv(API_KEY_ENV);
        // Skip tests if API key is not set
        Assumptions.assumeTrue(
                apiKey != null && !apiKey.trim().isEmpty(),
                "Skipping tests because " + API_KEY_ENV + " environment variable is not set");
    }

    /**
     * Test basic chat functionality with simple text prompt.
     */
    @Test
    void testBasicChat() {
        // Create real API client with API key from environment
        DashScopeApi realApi = DashScopeApi.builder().apiKey(apiKey).build();

        // Create chat model with default options
        DashScopeChatOptions options = DashScopeChatOptions.builder().model(TEST_MODEL).build();
        DashScopeChatModel chatModel = DashScopeChatModel.builder()
                .dashScopeApi(realApi)
                .defaultOptions(options)
                .build();

        // Create prompt with user message
        UserMessage message = new UserMessage(TEST_PROMPT);
        Prompt prompt = new Prompt(message);

        // Call the chat model
        Generation response = chatModel.call(prompt).getResult();

        // Verify response
        assertThat(response).isNotNull();
        assertThat(response.getOutput().getText()).isNotEmpty();
        System.out.println("Chat Response: " + response.getOutput().getText());
    }

    /**
     * Test streaming chat functionality.
     */
    @Test
    void testStreamChat() {
        // Create real API client with API key from environment
        DashScopeApi realApi = DashScopeApi.builder().apiKey(apiKey).build();

        // Create chat model with default options
        DashScopeChatOptions options = DashScopeChatOptions.builder().model(TEST_MODEL).build();
        DashScopeChatModel chatModel = DashScopeChatModel.builder()
                .dashScopeApi(realApi)
                .defaultOptions(options)
                .build();

        // Create prompt with user message
        UserMessage message = new UserMessage(TEST_PROMPT);
        Prompt prompt = new Prompt(message);

        // Call the streaming API and collect responses
        StringBuilder responseBuilder = new StringBuilder();
        Flux<Generation> responseFlux = chatModel.stream(prompt).map(ChatResponse::getResult);

        responseFlux.doOnNext(generation -> {
            String content = generation.getOutput().getText();
            System.out.println("Streaming chunk: " + content);
            responseBuilder.append(content);
        }).blockLast();

        // Verify final response
        String finalResponse = responseBuilder.toString();
        assertThat(finalResponse).isNotEmpty();
        System.out.println("Final streaming response: " + finalResponse);
    }

    @Test
    void testStreamParallelToolCalls() {
        // Create real API client with API key from environment
        DashScopeApi realApi = DashScopeApi.builder().apiKey(apiKey).build();

        // Create chat model with default options
        DashScopeChatOptions options = DashScopeChatOptions.builder()
                .model(TEST_MODEL)
                .stream(true)
                .parallelToolCalls(true)
                .enableThinking(true)
                .toolCallbacks(List.of(ToolCallbacks.from(new DateTimeTools(), new WeatherTools())))
                .build();

        DashScopeChatModel chatModel = DashScopeChatModel.builder()
                .dashScopeApi(realApi)
                .defaultOptions(options)
                .build();

        // Call the streaming API and collect responses
        Flux<String> flux = chatModel.stream("Get tomorrow datetime, set a alarm and tell me the weather of Beijing for me in tomorrow 9:00 am.");

        String result = String.join("", flux.collectList().block());
        assertThat(result).isNotBlank();
        assertThat(result).containsIgnoringCase("time");
        assertThat(result).containsIgnoringCase("alarm");
        assertThat(result).containsIgnoringCase("weather");
    }

    static class DateTimeTools {

        @Tool(description = "Get the current date and time in the user's timezone")
        String getCurrentDateTime() {
            return LocalDateTime.now().atZone(LocaleContextHolder.getTimeZone().toZoneId()).toString();
        }

        @Tool(description = "Set a user alarm for the given time")
        void setAlarm(@ToolParam(description = "Time in ISO-8601 format") String time) {
            LocalDateTime alarmTime = LocalDateTime.parse(time, DateTimeFormatter.ISO_DATE_TIME);
            System.out.println("Alarm set for " + alarmTime);
        }

    }

    static class WeatherTools {

        @Tool(description = "Get the weather forecast for the given location")
        WeatherResponse getWeatherForecast(@ToolParam(description = "The given location") String location) {
            return switch (location) {
                case "Beijing" -> new WeatherResponse(10.0, Unit.C);
                case "Shanghai" -> new WeatherResponse(20.0, Unit.C);
                case "Hangzhou" -> new WeatherResponse(30.0, Unit.C);
                default -> throw new IllegalArgumentException("Unknow location: " + location);
            };
        }

        enum Unit {
            C,
            F
        }

        record WeatherResponse(double temp, Unit unit) {}

    }

}
