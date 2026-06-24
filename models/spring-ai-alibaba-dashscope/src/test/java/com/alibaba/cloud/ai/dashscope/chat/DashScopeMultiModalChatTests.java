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

import static com.alibaba.cloud.ai.dashscope.common.DashScopeApiConstants.MESSAGE_FORMAT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatApiSpec.ChatCompletion;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatApiSpec.ChatCompletionChunk;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatApiSpec.ChatCompletionFinishReason;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatApiSpec.ChatCompletionMessage;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatApiSpec.ChatCompletionOutput;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatApiSpec.ChatCompletionOutput.Choice;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatApiSpec.ChatCompletionRequest;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatApiSpec.MediaContent;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatApiSpec.Role;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatApiSpec.TokenUsage;
import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.content.Media;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MimeTypeUtils;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import tools.jackson.databind.json.JsonMapper;

/**
 * Tests for DashScope multi-modal chat functionality.
 *
 * @author brianxiadong
 * @since 1.0.0-M5.1
 */
public class DashScopeMultiModalChatTests {

  private static final String TEST_MODEL = "qwen-vl-max-latest";

  private static final String TEST_API_KEY = "test-api-key";

  private static final String TEST_REQUEST_ID = "test-request-id";

  private static final String TEST_PROMPT = "这些是什么？";

  private static final String TEST_RESPONSE = "图片中是一个小女孩和一只狗在户外。";

  private static final String TEST_VIDEO_PROMPT = "这是一组从视频中提取的图片帧，请描述此视频中的内容。";

  private static final String TEST_AUDIO_PROMPT = "这是一个音频文件，请描述此音频中的内容。";

  private static final String TEST_VIDEO_RESPONSE = "视频展示了一系列连续的画面，内容是...";

  private static final String TEST_AUDIO_RESPONSE = "音频中是一个男性的声音，说的是...";

  private static final String MULTIMODAL_IMAGE_MODEL = "qwen3-vl-plus";

  private static final String MULTIMODAL_VIDEO_MODEL = "qwen-vl-max";

  private static final String MULTIMODAL_IMAGE_URL =
      "https://help-static-aliyun-doc.aliyuncs.com/file-manage-files/zh-CN/20241022/emyrja/dog_and_girl.jpeg";

  private static final String MULTIMODAL_IMAGE_PROMPT = "图中描绘的是什么景象？";

  private static final String MULTIMODAL_VIDEO_PROMPT = "描述这个视频的具体过程";

  private static final List<String> MULTIMODAL_VIDEO_FRAME_URLS =
      List.of(
          "https://img.alicdn.com/imgextra/i3/O1CN01K3SgGo1eqmlUgeE9b_!!6000000003923-0-tps-3840-2160.jpg",
          "https://img.alicdn.com/imgextra/i4/O1CN01BjZvwg1Y23CF5qIRB_!!6000000003000-0-tps-3840-2160.jpg",
          "https://img.alicdn.com/imgextra/i4/O1CN01Ib0clU27vTgBdbVLQ_!!6000000007859-0-tps-3840-2160.jpg",
          "https://img.alicdn.com/imgextra/i1/O1CN01aygPLW1s3EXCdSN4X_!!6000000005710-0-tps-3840-2160.jpg");

  private static final String API_KEY_ENV = "DASHSCOPE_API_KEY";

  private static final String LEGACY_API_KEY_ENV = "AI_DASHSCOPE_API_KEY";

  private DashScopeApi dashScopeApi;

  private DashScopeChatModel chatModel;

  private DashScopeChatOptions defaultOptions;

  private ResourceLoader resourceLoader;

  @BeforeEach
  void setUp() {
    // Mock the DashScopeApi
    dashScopeApi = Mockito.mock(DashScopeApi.class);

    // Mock ResourceLoader
    resourceLoader = Mockito.mock(ResourceLoader.class);
    Resource mockResource = new ClassPathResource("multimodel/dog_and_girl.jpeg");
    when(resourceLoader.getResource("classpath:/multimodel/dog_and_girl.jpeg"))
        .thenReturn(mockResource);

    // Setup default options
    defaultOptions = DashScopeChatOptions.builder().model(TEST_MODEL).multiModel(true).build();

    // Create the chat model with mocked API
    chatModel =
        DashScopeChatModel.builder()
            .dashScopeApi(dashScopeApi)
            .defaultOptions(defaultOptions)
            .build();
  }

  @Test
  void createRequestKeepsSystemContentAsTextForMultimodalPrompt() throws Exception {
    UserMessage userMessage =
        multimodalMessage(
            TEST_PROMPT,
            MessageFormat.IMAGE,
            List.of(
                new Media(
                    MimeTypeUtils.IMAGE_PNG,
                    new URI("https://dashscope.oss-cn-beijing.aliyuncs.com/images/dog_and_girl.jpeg"))));
    Prompt prompt =
        new Prompt(
            List.of(new SystemMessage("You are a helpful assistant."), userMessage),
            DashScopeChatOptions.builder().model(TEST_MODEL).multiModel(true).build());

    ChatCompletionRequest request = chatModel.createRequest(prompt);

    assertThat(request.input().messages()).hasSize(2);
    assertThat(request.input().messages().get(0).role()).isEqualTo(Role.SYSTEM);
    assertThat(request.input().messages().get(0).rawContent())
        .isEqualTo("You are a helpful assistant.");
    assertThat(request.input().messages().get(1).role()).isEqualTo(Role.USER);
    assertMediaContentList(request.input().messages().get(1).rawContent());
  }

  /** Test image processing with URL-based media */
  @Test
  void testImageWithUrl() throws Exception {
    // Setup mock response
    ChatCompletionMessage responseMessage =
        new ChatCompletionMessage(TEST_RESPONSE, Role.ASSISTANT);
    Choice choice = new Choice(ChatCompletionFinishReason.STOP, responseMessage, null, 0);
    ChatCompletionOutput output = new ChatCompletionOutput(TEST_RESPONSE, List.of(choice), null);
    TokenUsage usage = new TokenUsage(10, 5, 15, null, null, null, null, null, null, null);
    ChatCompletion chatCompletion = new ChatCompletion(TEST_REQUEST_ID, output, usage);
    ResponseEntity<ChatCompletion> responseEntity = ResponseEntity.ok(chatCompletion);

    when(dashScopeApi.chatCompletionEntity(any(ChatCompletionRequest.class), any(), anyBoolean()))
        .thenReturn(responseEntity);

    // Create media list with URL
    List<Media> mediaList =
        List.of(
            new Media(
                MimeTypeUtils.IMAGE_PNG,
                new URI("https://dashscope.oss-cn-beijing.aliyuncs.com/images/dog_and_girl.jpeg")));

    // Create user message with media
    UserMessage message = UserMessage.builder().text(TEST_PROMPT).media(mediaList).build();
    message.getMetadata().put(MESSAGE_FORMAT, MessageFormat.IMAGE);

    // Create prompt with options
    Prompt prompt =
        new Prompt(
            message, DashScopeChatOptions.builder().model(TEST_MODEL).multiModel(true).build());

    // Call the chat model
    ChatResponse response = chatModel.call(prompt);

    // Verify response
    assertThat(response).isNotNull();
    assertThat(response.getResult().getOutput().getText()).isEqualTo(TEST_RESPONSE);
  }

  /** Test image processing with binary resource */
  @Test
  void testImageWithBinaryResource() {
    // Setup mock response
    ChatCompletionMessage responseMessage =
        new ChatCompletionMessage(TEST_RESPONSE, Role.ASSISTANT);
    Choice choice = new Choice(ChatCompletionFinishReason.STOP, responseMessage, null, 0);
    ChatCompletionOutput output = new ChatCompletionOutput(TEST_RESPONSE, List.of(choice), null);
    TokenUsage usage = new TokenUsage(10, 5, 15, null, null, null, null, null, null, null);
    ChatCompletion chatCompletion = new ChatCompletion(TEST_REQUEST_ID, output, usage);
    ResponseEntity<ChatCompletion> responseEntity = ResponseEntity.ok(chatCompletion);

    when(dashScopeApi.chatCompletionEntity(any(ChatCompletionRequest.class), any(), anyBoolean()))
        .thenReturn(responseEntity);

    // Create user message with resource media
    UserMessage message =
        UserMessage.builder()
            .text(TEST_PROMPT)
            .media(
                new Media(
                    MimeTypeUtils.IMAGE_JPEG,
                    new ClassPathResource("multimodel/dog_and_girl.jpeg")))
            .build();
    message.getMetadata().put(MESSAGE_FORMAT, MessageFormat.IMAGE);

    // Create prompt with options
    Prompt prompt =
        new Prompt(
            message, DashScopeChatOptions.builder().model(TEST_MODEL).multiModel(true).build());

    // Call the chat model
    ChatResponse response = chatModel.call(prompt);

    // Verify response
    assertThat(response).isNotNull();
    assertThat(response.getResult().getOutput().getText()).isEqualTo(TEST_RESPONSE);
  }

  /** Test video processing with multiple frames */
  @Test
  void testVideoWithMultipleFrames() {
    // Setup mock response
    ChatCompletionMessage responseMessage =
        new ChatCompletionMessage(TEST_VIDEO_RESPONSE, Role.ASSISTANT);
    Choice choice = new Choice(ChatCompletionFinishReason.STOP, responseMessage, null, 0);
    ChatCompletionOutput output =
        new ChatCompletionOutput(TEST_VIDEO_RESPONSE, List.of(choice), null);
    TokenUsage usage = new TokenUsage(10, 5, 15, null, null, null, null, null, null, null);
    ChatCompletion chatCompletion = new ChatCompletion(TEST_REQUEST_ID, output, usage);
    ResponseEntity<ChatCompletion> responseEntity = ResponseEntity.ok(chatCompletion);

    when(dashScopeApi.chatCompletionEntity(any(ChatCompletionRequest.class), any(), anyBoolean()))
        .thenReturn(responseEntity);

    // Create media list with multiple frames (simulating video frames)
    List<Media> mediaList = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      mediaList.add(
          new Media(
              MimeTypeUtils.IMAGE_JPEG, new ClassPathResource("multimodel/dog_and_girl.jpeg")));
    }

    // Create user message with media
    UserMessage message = UserMessage.builder().text(TEST_VIDEO_PROMPT).media(mediaList).build();
    message.getMetadata().put(MESSAGE_FORMAT, MessageFormat.VIDEO);

    // Create prompt with options
    Prompt prompt =
        new Prompt(
            message, DashScopeChatOptions.builder().model(TEST_MODEL).multiModel(true).build());

    // Call the chat model
    ChatResponse response = chatModel.call(prompt);

    // Verify response
    assertThat(response).isNotNull();
    assertThat(response.getResult().getOutput().getText()).isEqualTo(TEST_VIDEO_RESPONSE);
  }

  /** Test audio processing with multiple frames */
  @Test
  void testAudioWithMultipleFrames() {
    // Setup mock response
    ChatCompletionMessage responseMessage =
        new ChatCompletionMessage(TEST_AUDIO_RESPONSE, Role.ASSISTANT);
    Choice choice = new Choice(ChatCompletionFinishReason.STOP, responseMessage, null, 0);
    ChatCompletionOutput output =
        new ChatCompletionOutput(TEST_AUDIO_RESPONSE, List.of(choice), null);
    TokenUsage usage = new TokenUsage(10, 5, 15, null, null, null, null, null, null, null);
    ChatCompletion chatCompletion = new ChatCompletion(TEST_REQUEST_ID, output, usage);
    ResponseEntity<ChatCompletion> responseEntity = ResponseEntity.ok(chatCompletion);

    when(dashScopeApi.chatCompletionEntity(any(ChatCompletionRequest.class), any(), anyBoolean()))
        .thenReturn(responseEntity);

    // Create media list with multiple frames (simulating video frames)
    List<Media> mediaList = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      mediaList.add(
          new Media(
              MediaType.parseMediaType("audio/mpeg"),
              URI.create("https://dashscope.oss-cn-beijing.aliyuncs.com/audios/welcome.mp3")));
    }

    // Create user message with media
    UserMessage message = UserMessage.builder().text(TEST_AUDIO_PROMPT).media(mediaList).build();
    message.getMetadata().put(MESSAGE_FORMAT, MessageFormat.AUDIO);

    // Create prompt with options
    Prompt prompt =
        new Prompt(
            message, DashScopeChatOptions.builder().model(TEST_MODEL).multiModel(true).build());

    // Call the chat model
    ChatResponse response = chatModel.call(prompt);

    // Verify response
    assertThat(response).isNotNull();
    assertThat(response.getResult().getOutput().getText()).isEqualTo(TEST_AUDIO_RESPONSE);
  }

  /** Test streaming response with image input */
  @Test
  void testStreamImageResponse() {
    // Setup mock streaming response
    ChatCompletionMessage chunkMessage1 =
        new ChatCompletionMessage("图片中是一个", Role.ASSISTANT);
    ChatCompletionMessage chunkMessage2 =
        new ChatCompletionMessage("小女孩和一只狗在户外。", Role.ASSISTANT);

    Choice choice1 = new Choice(null, chunkMessage1, null, 0);
    Choice choice2 = new Choice(ChatCompletionFinishReason.STOP, chunkMessage2, null, 0);

    ChatCompletionOutput output1 = new ChatCompletionOutput("图片中是一个", List.of(choice1), null);
    ChatCompletionOutput output2 = new ChatCompletionOutput("小女孩和一只狗在户外。", List.of(choice2), null);

    ChatCompletionChunk chunk1 = new ChatCompletionChunk(TEST_REQUEST_ID, output1, null, null);
    ChatCompletionChunk chunk2 =
        new ChatCompletionChunk(
            TEST_REQUEST_ID,
            output2,
            new TokenUsage(10, 5, 15, null, null, null, null, null, null, null),
            null);

    when(dashScopeApi.chatCompletionStream(any(ChatCompletionRequest.class), any(), anyBoolean()))
        .thenReturn(Flux.just(chunk1, chunk2));

    // Create user message with resource media
    UserMessage message =
        UserMessage.builder()
            .text(TEST_PROMPT)
            .media(
                new Media(
                    MimeTypeUtils.IMAGE_JPEG,
                    new ClassPathResource("multimodel/dog_and_girl.jpeg")))
            .build();
    message.getMetadata().put(MESSAGE_FORMAT, MessageFormat.IMAGE);

    // Create prompt with options
    Prompt prompt =
        new Prompt(
            message, DashScopeChatOptions.builder().model(TEST_MODEL).multiModel(true).build());

    // Call the streaming API
    Flux<ChatResponse> responseFlux = chatModel.stream(prompt);

    // Verify streaming response
    StepVerifier.create(responseFlux)
        .assertNext(
            response -> {
              assertThat(response.getResult().getOutput().getText()).isEqualTo("图片中是一个");
            })
        .assertNext(
            response -> {
              assertThat(response.getResult().getOutput().getText()).isEqualTo("小女孩和一只狗在户外。");
            })
        .verifyComplete();
  }

  @Test
  void streamSendsDashScopeMultimodalImageRequestToStreamingEndpoint() {
    ChatCompletionMessage chunkMessage1 = new ChatCompletionMessage("图中是一个", Role.ASSISTANT);
    ChatCompletionMessage chunkMessage2 = new ChatCompletionMessage("女孩和一只狗。", Role.ASSISTANT);
    Choice choice1 = new Choice(null, chunkMessage1, null, 0);
    Choice choice2 = new Choice(ChatCompletionFinishReason.STOP, chunkMessage2, null, 0);
    ChatCompletionOutput output1 = new ChatCompletionOutput("图中是一个", List.of(choice1), null);
    ChatCompletionOutput output2 = new ChatCompletionOutput("女孩和一只狗。", List.of(choice2), null);
    ChatCompletionChunk chunk1 = new ChatCompletionChunk(TEST_REQUEST_ID, output1, null, null);
    ChatCompletionChunk chunk2 =
        new ChatCompletionChunk(
            TEST_REQUEST_ID,
            output2,
            new TokenUsage(10, 5, 15, null, null, null, null, null, null, null),
            null);

    when(dashScopeApi.chatCompletionStream(
            any(ChatCompletionRequest.class), any(), eq(true)))
        .thenReturn(Flux.just(chunk1, chunk2));

    UserMessage message =
        multimodalMessage(
            MULTIMODAL_IMAGE_PROMPT,
            MessageFormat.IMAGE,
            List.of(new Media(MimeTypeUtils.IMAGE_JPEG, URI.create(MULTIMODAL_IMAGE_URL))));
    DashScopeChatOptions options =
        DashScopeChatOptions.builder()
            .model(MULTIMODAL_IMAGE_MODEL)
            .multiModel(true)
            .incrementalOutput(true)
            .build();
    Prompt prompt = new Prompt(message, options);

    StepVerifier.create(chatModel.stream(prompt))
        .assertNext(
            response -> assertThat(response.getResult().getOutput().getText()).isEqualTo("图中是一个"))
        .assertNext(
            response -> assertThat(response.getResult().getOutput().getText()).isEqualTo("女孩和一只狗。"))
        .verifyComplete();

    ArgumentCaptor<ChatCompletionRequest> requestCaptor =
        ArgumentCaptor.forClass(ChatCompletionRequest.class);
    verify(dashScopeApi).chatCompletionStream(requestCaptor.capture(), any(), eq(true));
    verify(dashScopeApi, never())
        .chatCompletionEntity(any(ChatCompletionRequest.class), any(), anyBoolean());

    ChatCompletionRequest request = requestCaptor.getValue();
    assertThat(request.model()).isEqualTo(MULTIMODAL_IMAGE_MODEL);
    assertThat(request.parameters().incrementalOutput()).isTrue();
    assertThat(request.input().messages()).hasSize(1);
    assertThat(request.input().messages().get(0).role()).isEqualTo(Role.USER);

    List<?> content = assertMediaContentList(request.input().messages().get(0).rawContent());
    MediaContent imageContent = (MediaContent) content.get(0);
    MediaContent textContent = (MediaContent) content.get(1);
    assertThat(imageContent.type()).isEqualTo("image");
    assertThat(imageContent.image()).isEqualTo(MULTIMODAL_IMAGE_URL);
    assertThat(textContent.type()).isEqualTo("text");
    assertThat(textContent.text()).isEqualTo(MULTIMODAL_IMAGE_PROMPT);

    String jsonRequest = JsonMapper.builder().build().writeValueAsString(request);
    assertThat(jsonRequest).contains("\"image\":\"" + MULTIMODAL_IMAGE_URL + "\"");
    assertThat(jsonRequest).contains("\"text\":\"" + MULTIMODAL_IMAGE_PROMPT + "\"");
    assertThat(jsonRequest).doesNotContain("\"type\"");
    assertThat(jsonRequest).doesNotContain("\"multi_model\"");
  }

  @Test
  void callSendsDashScopeMultimodalVideoRequestToGenerationEndpoint() {
    ChatCompletionMessage responseMessage =
        new ChatCompletionMessage("视频展示了多个连续画面。", Role.ASSISTANT);
    Choice choice = new Choice(ChatCompletionFinishReason.STOP, responseMessage, null, 0);
    ChatCompletionOutput output = new ChatCompletionOutput("视频展示了多个连续画面。", List.of(choice), null);
    TokenUsage usage = new TokenUsage(10, 5, 15, null, null, null, null, null, null, null);
    ChatCompletion chatCompletion = new ChatCompletion(TEST_REQUEST_ID, output, usage);

    when(dashScopeApi.chatCompletionEntity(
            any(ChatCompletionRequest.class), any(), eq(true)))
        .thenReturn(ResponseEntity.ok(chatCompletion));

    UserMessage message =
        multimodalMessage(
            MULTIMODAL_VIDEO_PROMPT,
            MessageFormat.VIDEO,
            MULTIMODAL_VIDEO_FRAME_URLS.stream()
                .map(url -> new Media(MimeTypeUtils.IMAGE_JPEG, URI.create(url)))
                .toList());
    DashScopeChatOptions options =
        DashScopeChatOptions.builder()
            .model(MULTIMODAL_VIDEO_MODEL)
            .multiModel(true)
            .build();
    Prompt prompt = new Prompt(message, options);

    ChatResponse response = chatModel.call(prompt);

    assertThat(response.getResult().getOutput().getText()).isEqualTo("视频展示了多个连续画面。");

    ArgumentCaptor<ChatCompletionRequest> requestCaptor =
        ArgumentCaptor.forClass(ChatCompletionRequest.class);
    verify(dashScopeApi).chatCompletionEntity(requestCaptor.capture(), any(), eq(true));
    verify(dashScopeApi, never())
        .chatCompletionStream(any(ChatCompletionRequest.class), any(), anyBoolean());

    ChatCompletionRequest request = requestCaptor.getValue();
    assertThat(request.model()).isEqualTo(MULTIMODAL_VIDEO_MODEL);
    assertThat(request.input().messages()).hasSize(1);
    assertThat(request.input().messages().get(0).role()).isEqualTo(Role.USER);

    List<?> content = assertMediaContentList(request.input().messages().get(0).rawContent());
    MediaContent videoContent = (MediaContent) content.get(0);
    MediaContent textContent = (MediaContent) content.get(1);
    assertThat(videoContent.type()).isEqualTo("video");
    assertThat(videoContent.video()).containsExactlyElementsOf(MULTIMODAL_VIDEO_FRAME_URLS);
    assertThat(textContent.type()).isEqualTo("text");
    assertThat(textContent.text()).isEqualTo(MULTIMODAL_VIDEO_PROMPT);

    String jsonRequest = JsonMapper.builder().build().writeValueAsString(request);
    assertThat(jsonRequest).contains("\"video\":[");
    assertThat(jsonRequest).contains("\"text\":\"" + MULTIMODAL_VIDEO_PROMPT + "\"");
    assertThat(jsonRequest).doesNotContain("\"type\"");
    assertThat(jsonRequest).doesNotContain("\"multi_model\"");
    assertThat(jsonRequest).doesNotContain("\"incremental_output\"");
    assertThat(jsonRequest).doesNotContain("\"result_format\"");
  }

  // =============== Integration Test Cases ===============

  @Test
  @Tag("integration")
  void integrationTestStreamImageWithUrlQwen3VlPlus() {
    DashScopeChatModel realChatModel = realChatModel();

    UserMessage message =
        multimodalMessage(
            MULTIMODAL_IMAGE_PROMPT,
            MessageFormat.IMAGE,
            List.of(new Media(MimeTypeUtils.IMAGE_JPEG, URI.create(MULTIMODAL_IMAGE_URL))));
    DashScopeChatOptions options =
        DashScopeChatOptions.builder()
            .model(MULTIMODAL_IMAGE_MODEL)
            .multiModel(true)
            .incrementalOutput(true)
            .build();
    Prompt prompt = new Prompt(message, options);

    StringBuilder responseBuilder = new StringBuilder();
    List<String> chunks =
        realChatModel
            .stream(prompt)
            .map(response -> response.getResult().getOutput().getText())
            .filter(DashScopeMultiModalChatTests::hasText)
            .doOnNext(
                content -> {
                  System.out.println("Multimodal image streaming chunk: " + content);
                  responseBuilder.append(content);
                })
            .collectList()
            .block(Duration.ofSeconds(60));

    assertThat(chunks).isNotNull().isNotEmpty();
    assertThat(responseBuilder.toString()).isNotBlank();
    System.out.println("Final multimodal image streaming response: " + responseBuilder);
  }

  @Test
  @Tag("integration")
  void integrationTestVideoWithUrlFramesQwenVlMax() {
    DashScopeChatModel realChatModel = realChatModel();

    UserMessage message =
        multimodalMessage(
            MULTIMODAL_VIDEO_PROMPT,
            MessageFormat.VIDEO,
            MULTIMODAL_VIDEO_FRAME_URLS.stream()
                .map(url -> new Media(MimeTypeUtils.IMAGE_JPEG, URI.create(url)))
                .toList());
    DashScopeChatOptions options =
        DashScopeChatOptions.builder()
            .model(MULTIMODAL_VIDEO_MODEL)
            .multiModel(true)
            .build();
    Prompt prompt = new Prompt(message, options);

    ChatResponse chatResponse = realChatModel.call(prompt);

    assertThat(chatResponse).isNotNull();
    assertThat(chatResponse.getResult()).isNotNull();
    assertThat(chatResponse.getResult().getOutput().getText()).isNotBlank();
    String responseId = chatResponse.getMetadata().getId();
    if (responseId != null) {
      assertThat(responseId).isNotBlank();
    }
    System.out.println(
        "Multimodal video frames response: "
            + chatResponse.getResult().getOutput().getText());
  }

  /**
   * Integration test for image processing with URL This test will only run if AI_DASHSCOPE_API_KEY
   * environment variable is set
   */
  @Test
  @Tag("integration")
  @EnabledIfEnvironmentVariable(named = "AI_DASHSCOPE_API_KEY", matches = ".+")
  void integrationTestImageWithUrl() throws Exception {
    // Create real API client
    String apiKey = System.getenv("AI_DASHSCOPE_API_KEY");
    DashScopeApi realApi = DashScopeApi.builder().apiKey(apiKey).build();
    ;

    // Create real chat model
    DashScopeChatModel realChatModel = DashScopeChatModel.builder().dashScopeApi(realApi).build();
    ;

    // Create media list with URL
    List<Media> mediaList =
        List.of(
            new Media(
                MimeTypeUtils.IMAGE_PNG,
                new URI("https://dashscope.oss-cn-beijing.aliyuncs.com/images/dog_and_girl.jpeg")));

    // Create user message with media
    UserMessage message = UserMessage.builder().text(TEST_PROMPT).media(mediaList).build();
    message.getMetadata().put(MESSAGE_FORMAT, MessageFormat.IMAGE);

    // Create prompt
    Prompt prompt =
        new Prompt(
            message, DashScopeChatOptions.builder().model(TEST_MODEL).multiModel(true).build());

    // Call the chat model
    ChatResponse response = realChatModel.call(prompt);

    // Verify response
    assertThat(response).isNotNull();
    assertThat(response.getResult().getOutput().getText()).isNotEmpty();
    System.out.println("Image URL Response: " + response.getResult().getOutput().getText());
  }

  /**
   * Integration test for image processing with binary resource This test will only run if
   * DASHSCOPE_API_KEY environment variable is set
   */
  @Test
  @Tag("integration")
  @EnabledIfEnvironmentVariable(named = "AI_DASHSCOPE_API_KEY", matches = ".+")
  void integrationTestImageWithBinaryResource() throws IOException {
    // Create real API client
    String apiKey = System.getenv("AI_DASHSCOPE_API_KEY");
    DashScopeApi realApi = DashScopeApi.builder().apiKey(apiKey).build();
    ;

    // Create real chat model
    DashScopeChatModel realChatModel = DashScopeChatModel.builder().dashScopeApi(realApi).build();

    // Create user message with resource media
    UserMessage message =
        UserMessage.builder()
            .text(TEST_PROMPT)
            .media(
                new Media(
                    MimeTypeUtils.IMAGE_JPEG,
                    new ClassPathResource("multimodel/dog_and_girl.jpeg")))
            .build();
    message.getMetadata().put(MESSAGE_FORMAT, MessageFormat.IMAGE);

    // Create prompt
    Prompt prompt =
        new Prompt(
            message, DashScopeChatOptions.builder().model(TEST_MODEL).multiModel(true).build());

    // Call the chat model
    ChatResponse response = realChatModel.call(prompt);

    // Verify response
    assertThat(response).isNotNull();
    assertThat(response.getResult().getOutput().getText()).isNotEmpty();
    System.out.println("Binary Image Response: " + response.getResult().getOutput().getText());
  }

  /**
   * Integration test for video processing with multiple frames This test will only run if
   * DASHSCOPE_API_KEY environment variable is set
   */
  @Test
  @Tag("integration")
  @EnabledIfEnvironmentVariable(named = "AI_DASHSCOPE_API_KEY", matches = ".+")
  void integrationTestVideoWithMultipleFrames() throws IOException {
    // Create real API client
    String apiKey = System.getenv("AI_DASHSCOPE_API_KEY");
    DashScopeApi realApi = DashScopeApi.builder().apiKey(apiKey).build();
    ;

    // Create real chat model
    DashScopeChatModel realChatModel = DashScopeChatModel.builder().dashScopeApi(realApi).build();

    // Create media list with multiple frames (simulating video frames)
    List<Media> mediaList = new ArrayList<>();
    for (int i = 0; i < 5; i++) {
      mediaList.add(
          new Media(
              MimeTypeUtils.IMAGE_JPEG, new ClassPathResource("multimodel/dog_and_girl.jpeg")));
    }

    // Create user message with media
    UserMessage message = UserMessage.builder().text(TEST_VIDEO_PROMPT).media(mediaList).build();
    message.getMetadata().put(MESSAGE_FORMAT, MessageFormat.VIDEO);

    // Create prompt
    Prompt prompt =
        new Prompt(
            message, DashScopeChatOptions.builder().model(TEST_MODEL).multiModel(true).build());

    // Call the chat model
    ChatResponse response = realChatModel.call(prompt);

    // Verify response
    assertThat(response).isNotNull();
    assertThat(response.getResult().getOutput().getText()).isNotEmpty();
    System.out.println("Video Frames Response: " + response.getResult().getOutput().getText());
  }

  /**
   * Integration test for audio processing with multiple frames This test will only run if
   * DASHSCOPE_API_KEY environment variable is set
   */
  @Test
  @Tag("integration")
  @EnabledIfEnvironmentVariable(named = "AI_DASHSCOPE_API_KEY", matches = "sk.+")
  void integrationTestAudioWithMultipleFrames() throws IOException {
    // Create real API client
    String apiKey = System.getenv("AI_DASHSCOPE_API_KEY");
    DashScopeApi realApi = DashScopeApi.builder().apiKey(apiKey).build();

    // Create real chat model
    DashScopeChatModel realChatModel = DashScopeChatModel.builder().dashScopeApi(realApi).build();

    // Create media with multiple frames (simulating audio frames)
    Media media =
        new Media(
            MediaType.parseMediaType("audio/mpeg"),
            URI.create("https://dashscope.oss-cn-beijing.aliyuncs.com/audios/welcome.mp3"));

    // Create user message with media
    UserMessage message = UserMessage.builder().text(TEST_AUDIO_PROMPT).media(media).build();
    message.getMetadata().put(MESSAGE_FORMAT, MessageFormat.AUDIO);

    // Create prompt
    Prompt prompt =
        new Prompt(
            message,
            DashScopeChatOptions.builder()
                .model("qwen-audio-turbo-latest")
                .multiModel(true)
                .build());

    // Call the chat model
    ChatResponse response = realChatModel.call(prompt);

    // Verify response
    assertThat(response).isNotNull();
    assertThat(response.getResult().getOutput().getText()).isNotEmpty();
    System.out.println("Audio Frames Response: " + response.getResult().getOutput().getText());
  }

  /**
   * Integration test for streaming response with image input This test will only run if
   * DASHSCOPE_API_KEY environment variable is set
   */
  @Test
  @Tag("integration")
  @EnabledIfEnvironmentVariable(named = "AI_DASHSCOPE_API_KEY", matches = ".+")
  void integrationTestStreamImageResponse() throws IOException {
    // Create real API client
    String apiKey = System.getenv("AI_DASHSCOPE_API_KEY");
    DashScopeApi realApi = DashScopeApi.builder().apiKey(apiKey).build();
    ;

    // Create real chat model
    DashScopeChatModel realChatModel = DashScopeChatModel.builder().dashScopeApi(realApi).build();

    // Create user message with resource media
    UserMessage message =
        UserMessage.builder()
            .text(TEST_PROMPT)
            .media(
                new Media(
                    MimeTypeUtils.IMAGE_JPEG,
                    new ClassPathResource("multimodel/dog_and_girl.jpeg")))
            .build();
    message.getMetadata().put(MESSAGE_FORMAT, MessageFormat.IMAGE);

    // Create prompt
    Prompt prompt =
        new Prompt(
            message, DashScopeChatOptions.builder().model(TEST_MODEL).multiModel(true).build());

    // Call the streaming API
    Flux<ChatResponse> responseFlux = realChatModel.stream(prompt);

    // Collect all responses
    AtomicReference<StringBuilder> responseBuilder = new AtomicReference<>(new StringBuilder());

    // Verify streaming response
    responseFlux
        .doOnNext(
            response -> {
              String content = response.getResult().getOutput().getText();
              System.out.println("Streaming chunk: " + content);
              responseBuilder.get().append(content);
            })
        .blockLast(Duration.ofSeconds(30));

    // Verify final response
    String finalResponse = responseBuilder.get().toString();
    assertThat(finalResponse).isNotEmpty();
    System.out.println("Final streaming response: " + finalResponse);
  }

  /**
   * Integration test for image analysis with custom prompt This test will only run if
   * DASHSCOPE_API_KEY environment variable is set
   */
  @Test
  @Tag("integration")
  @EnabledIfEnvironmentVariable(named = "AI_DASHSCOPE_API_KEY", matches = ".+")
  void integrationTestImageAnalysisWithCustomPrompt() throws IOException {
    // Create real API client
    String apiKey = System.getenv("AI_DASHSCOPE_API_KEY");
    DashScopeApi realApi = DashScopeApi.builder().apiKey(apiKey).build();
    ;

    // Create real chat model
    DashScopeChatModel realChatModel = DashScopeChatModel.builder().dashScopeApi(realApi).build();

    // Create user message with resource media and custom prompt
    UserMessage message =
        UserMessage.builder()
            .text("请详细描述这张图片中的场景，包括人物、动物、环境等细节，并分析图片的情感基调。")
            .media(
                new Media(
                    MimeTypeUtils.IMAGE_JPEG,
                    new ClassPathResource("multimodel/dog_and_girl.jpeg")))
            .build();
    message.getMetadata().put(MESSAGE_FORMAT, MessageFormat.IMAGE);

    // Create prompt
    Prompt prompt =
        new Prompt(
            message, DashScopeChatOptions.builder().model(TEST_MODEL).multiModel(true).build());

    // Call the chat model
    ChatResponse response = realChatModel.call(prompt);

    // Verify response
    assertThat(response).isNotNull();
    assertThat(response.getResult().getOutput().getText()).isNotEmpty();
    System.out.println("Image Analysis Response: " + response.getResult().getOutput().getText());
  }

  private static UserMessage multimodalMessage(
      String text, MessageFormat messageFormat, List<Media> mediaList) {
    UserMessage message = UserMessage.builder().text(text).media(mediaList).build();
    message.getMetadata().put(MESSAGE_FORMAT, messageFormat);
    return message;
  }

  private static List<?> assertMediaContentList(Object content) {
    assertThat(content).isInstanceOf(List.class);
    List<?> contentList = (List<?>) content;
    assertThat(contentList).hasSize(2);
    assertThat(contentList).allSatisfy(item -> assertThat(item).isInstanceOf(MediaContent.class));
    return contentList;
  }

  private static DashScopeChatModel realChatModel() {
    DashScopeApi realApi = DashScopeApi.builder().apiKey(apiKey()).build();
    return DashScopeChatModel.builder().dashScopeApi(realApi).build();
  }

  private static String apiKey() {
    String apiKey = System.getenv(API_KEY_ENV);
    if (!hasText(apiKey)) {
      apiKey = System.getenv(LEGACY_API_KEY_ENV);
    }
    Assumptions.assumeTrue(
        hasText(apiKey),
        "Skipping tests because neither "
            + API_KEY_ENV
            + " nor "
            + LEGACY_API_KEY_ENV
            + " is set");
    return apiKey;
  }

  private static boolean hasText(String value) {
    return value != null && !value.trim().isEmpty();
  }
}
