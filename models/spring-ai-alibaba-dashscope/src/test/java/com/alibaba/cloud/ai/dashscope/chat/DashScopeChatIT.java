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
import java.net.URI;
import java.time.Duration;
import java.util.List;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatApiSpec.ChatCompletionRequest.Parameters.Skill;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.context.i18n.LocaleContextHolder;
import reactor.core.publisher.Flux;
import org.springframework.ai.content.Media;
import org.springframework.ai.retry.NonTransientAiException;
import org.springframework.util.MimeTypeUtils;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link DashScopeChatModel}.
 * <p>
 * Requires a valid DashScope API key in either {@code DASHSCOPE_API_KEY} or the legacy
 * {@code AI_DASHSCOPE_API_KEY} environment variable. Tests that require optional remote
 * fixtures are guarded by their own environment variables.
 */
@Tag("IT")
class DashScopeChatIT {

	private static final String TEST_MODEL = "qwen-plus";

	private static final String TEST_SYSTEM_PROMPT = "You are a helpful assistant.";

	private static final String TEST_USER_PROMPT = "你是谁？";

	private static final String API_KEY_ENV = "DASHSCOPE_API_KEY";

	private static final String LEGACY_API_KEY_ENV = "AI_DASHSCOPE_API_KEY";

	private String apiKey;

	@BeforeEach
	void setUp() {
		this.apiKey = System.getenv(API_KEY_ENV);
		if (!hasText(this.apiKey)) {
			this.apiKey = System.getenv(LEGACY_API_KEY_ENV);
		}
		Assumptions.assumeTrue(hasText(this.apiKey),
				"Skipping tests because neither " + API_KEY_ENV + " nor " + LEGACY_API_KEY_ENV + " is set");
	}

	private DashScopeChatModel chatModel(DashScopeChatOptions options) {
		return DashScopeChatModel.builder()
			.dashScopeApi(DashScopeApi.builder().apiKey(this.apiKey).build())
			.defaultOptions(options)
			.build();
	}

	private Prompt textPrompt(String userPrompt) {
		return new Prompt(List.of(new SystemMessage(TEST_SYSTEM_PROMPT), new UserMessage(userPrompt)));
	}

	private ChatResponse callOrSkipQuota(DashScopeChatModel model, Prompt prompt) {
		try {
			return model.call(prompt);
		}
		catch (NonTransientAiException ex) {
			Assumptions.abort("DashScope request failed, likely due to remote quota or service availability: "
					+ ex.getMessage());
			return null;
		}
	}

	@Test
	void testBasicChat() {
		DashScopeChatOptions options = DashScopeChatOptions.builder()
			.model(TEST_MODEL)
			.resultFormat("message")
			.build();
		ChatResponse chatResponse = callOrSkipQuota(chatModel(options), textPrompt(TEST_USER_PROMPT));

		assertThat(chatResponse).isNotNull();
		Generation response = chatResponse.getResult();
		assertThat(response.getOutput().getText()).isNotEmpty();
		String responseId = chatResponse.getMetadata().getId();
		if (responseId != null) {
			assertThat(responseId).isNotBlank();
		}
		System.out.println(format("\n>>> RESPONSE: %s", response.getOutput().getText()));
	}

	@Test
	void testStreamChat() {
		DashScopeChatOptions options = DashScopeChatOptions.builder()
			.model(TEST_MODEL)
			.resultFormat("message")
			.incrementalOutput(true)
			.build();

		StringBuilder finalResponse = new StringBuilder();
		List<String> chunks = chatModel(options).stream(textPrompt(TEST_USER_PROMPT))
			.map(response -> response.getResult().getOutput().getText())
			.filter(DashScopeChatIT::hasText)
			.doOnNext(content -> {
				System.out.println(format("\n>>> RESPONSE: %s", content));
				finalResponse.append(content);
			})
			.collectList()
			.block(Duration.ofSeconds(60));

		assertThat(chunks).isNotNull().isNotEmpty();
		assertThat(finalResponse).isNotBlank();
	}

	@Test
	void testImageUnderstanding() {
		DashScopeChatOptions options = DashScopeChatOptions.builder().model("qwen-vl-max").build();
		Media media = new Media(MimeTypeUtils.IMAGE_JPEG,
				URI.create("https://dashscope.oss-cn-beijing.aliyuncs.com/images/dog_and_girl.jpeg"));
		Prompt prompt = new Prompt(List.of(new SystemMessage(TEST_SYSTEM_PROMPT),
				UserMessage.builder().text("这是什么？").media(media).build()));

		ChatResponse chatResponse = callOrSkipQuota(chatModel(options), prompt);

		assertThat(chatResponse).isNotNull();
		assertThat(chatResponse.getResult().getOutput().getText()).isNotEmpty();
		System.out.println(format("\n>>> RESPONSE: %s", chatResponse.getResult().getOutput().getText()));
	}

	@Test
	void testMultiImageUnderstanding() throws Exception {
		DashScopeChatOptions options = DashScopeChatOptions.builder().model("qwen-vl-max").build();
		Media media1 = new Media(MimeTypeUtils.IMAGE_JPEG, new URI(
				"https://help-static-aliyun-doc.aliyuncs.com/file-manage-files/zh-CN/20241115/bxsngf/zhangsan.png"));
		Media media2 = new Media(MimeTypeUtils.IMAGE_JPEG, new URI(
				"https://help-static-aliyun-doc.aliyuncs.com/file-manage-files/zh-CN/20241115/ctdzex/tongyixing.png"));
		Media media3 = new Media(MimeTypeUtils.IMAGE_JPEG, new URI(
				"https://help-static-aliyun-doc.aliyuncs.com/file-manage-files/zh-CN/20241115/ukvbpq/lisi.png"));
		Prompt prompt = new Prompt(List.of(new SystemMessage(TEST_SYSTEM_PROMPT), UserMessage.builder()
			.text("告诉我三张图片有什么相同之处")
			.media(List.of(media1, media2, media3))
			.build()));

		ChatResponse chatResponse = callOrSkipQuota(chatModel(options), prompt);

		assertThat(chatResponse).isNotNull();
		assertThat(chatResponse.getResult().getOutput().getText()).isNotEmpty();
		System.out.println(format("\n>>> RESPONSE: %s", chatResponse.getResult().getOutput().getText()));
	}

	@Test
	void testVideoUnderstanding() throws Exception {
		DashScopeChatOptions options = DashScopeChatOptions.builder().model("qwen-vl-max").build();
		Media media = new Media(MimeTypeUtils.parseMimeType("video/mp4"),
				new URI("https://dashscope.oss-cn-beijing.aliyuncs.com/videos/dog_and_girl.mp4"));
		Prompt prompt = new Prompt(List.of(new SystemMessage(TEST_SYSTEM_PROMPT),
				UserMessage.builder().text("描述这个视频的具体过程").media(media).build()));

		ChatResponse chatResponse = callOrSkipQuota(chatModel(options), prompt);

		assertThat(chatResponse).isNotNull();
		assertThat(chatResponse.getResult().getOutput().getText()).isNotEmpty();
		System.out.println(format("\n>>> RESPONSE: %s", chatResponse.getResult().getOutput().getText()));
	}

	@Test
	void testAudioUnderstanding() throws Exception {
		DashScopeChatOptions options = DashScopeChatOptions.builder().model("qwen-audio-turbo-latest").build();
		Media media = new Media(MimeTypeUtils.parseMimeType("audio/mpeg"),
				new URI("https://dashscope.oss-cn-beijing.aliyuncs.com/audios/2channel_16K.mp3"));
		Prompt prompt = new Prompt(List.of(new SystemMessage(TEST_SYSTEM_PROMPT),
				UserMessage.builder().text("这段音频在说什么？").media(media).build()));

		ChatResponse chatResponse = callOrSkipQuota(chatModel(options), prompt);

		assertThat(chatResponse).isNotNull();
		assertThat(chatResponse.getResult().getOutput().getText()).isNotEmpty();
		System.out.println(format("\n>>> RESPONSE: %s", chatResponse.getResult().getOutput().getText()));
	}

	@Test
	void testSearchEnabled() {
		DashScopeChatOptions options = DashScopeChatOptions.builder()
			.model(TEST_MODEL)
			.enableSearch(true)
			.build();

		ChatResponse chatResponse = callOrSkipQuota(chatModel(options), textPrompt("今天杭州天气怎么样？"));

		assertThat(chatResponse).isNotNull();
		assertThat(chatResponse.getResult().getOutput().getText()).isNotEmpty();
		System.out.println(format("\n>>> RESPONSE: %s", chatResponse.getResult().getOutput().getText()));
	}

	@Test
	@EnabledIfEnvironmentVariable(named = "DASHSCOPE_TEST_FILE_ID", matches = ".+")
	void testDocUnderstanding() {
		DashScopeChatOptions options = DashScopeChatOptions.builder().model("qwen-long").build();
		String fileId = System.getenv("DASHSCOPE_TEST_FILE_ID");
		Media media = Media.builder().id(fileId).mimeType(MimeTypeUtils.parseMimeType("application/pdf")).build();
		Prompt prompt = new Prompt(List.of(new SystemMessage(TEST_SYSTEM_PROMPT),
				UserMessage.builder().text("总结一下文档内容").media(media).build()));

		ChatResponse chatResponse = callOrSkipQuota(chatModel(options), prompt);

		assertThat(chatResponse).isNotNull();
		assertThat(chatResponse.getResult().getOutput().getText()).isNotEmpty();
		System.out.println(format("\n>>> RESPONSE: %s", chatResponse.getResult().getOutput().getText()));
	}

	@Test
	@EnabledIfEnvironmentVariable(named = "DASHSCOPE_ENABLE_PPT_IT", matches = "true")
	void testPptGenerationStream() {
		DashScopeChatOptions options = DashScopeChatOptions.builder()
			.model("qwen-doc-turbo")
			.incrementalOutput(true)
			.skill(List.of(new Skill("ppt", "general", "news_01")))
			.build();

		StringBuilder finalResponse = new StringBuilder();
		chatModel(options).stream(textPrompt("请以最近的AI新闻为主题，帮我生成一个PPT大纲。"))
			.map(response -> response.getResult().getOutput().getText())
			.filter(DashScopeChatIT::hasText)
			.doOnNext(content -> {
				System.out.println(format("\n>>> PPT RESPONSE: %s", content));
				finalResponse.append(content);
			})
			.blockLast(Duration.ofSeconds(120));

		assertThat(finalResponse).isNotBlank();
	}

	private static boolean hasText(String value) {
		return value != null && !value.isBlank();
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
