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
package com.alibaba.cloud.ai.dashscope.audio;

import java.nio.ByteBuffer;

import com.alibaba.cloud.ai.dashscope.api.DashScopeAudioSpeechApi;
import com.alibaba.cloud.ai.dashscope.audio.DashScopeAudioSpeechModel.Builder;
import com.alibaba.cloud.ai.dashscope.spec.DashScopeModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.ai.audio.tts.TextToSpeechPrompt;
import org.springframework.ai.audio.tts.TextToSpeechResponse;
import org.springframework.retry.support.RetryTemplate;
import reactor.core.publisher.Flux;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Test cases for DashScopeAudioSpeechModel. Tests cover basic speech synthesis, custom
 * options, streaming, error handling, and edge cases.
 *
 * @author yingzi
 * @since 1.1.0.0
 */
class DashScopeAudioSpeechModelTests {

    // Test constants
    private static final String TEST_MODEL = DashScopeModel.AudioModel.COSYVOICE_V1.getValue();

    private static final String TEST_VOICE = "longhua";

    private static final String TEST_TEXT = "Hello, this is a test";

    private static final byte[] TEST_AUDIO_DATA = new byte[] {0x1, 0x2, 0x3, 0x4};

    private DashScopeAudioSpeechApi audioSpeechApi;

    private DashScopeAudioSpeechModel speechModel;

    private DashScopeAudioSpeechOptions defaultOptions;

    @BeforeEach
    void setUp() {
        // Initialize mock objects and test instances
        audioSpeechApi = Mockito.mock(DashScopeAudioSpeechApi.class);
        defaultOptions = DashScopeAudioSpeechOptions.builder()
                .model(TEST_MODEL)
                .voice(TEST_VOICE)
                .speed(1.0)
                .responseFormat(DashScopeAudioSpeechApi.ResponseFormat.MP3)
                .build();
        speechModel = new DashScopeAudioSpeechModel(audioSpeechApi, defaultOptions, RetryTemplate.builder().build());
    }

    @Test
    void testBasicSpeechSynthesis() {
        // Test basic speech synthesis with successful response
        mockSuccessfulSpeechGeneration();

        TextToSpeechPrompt prompt = new TextToSpeechPrompt(TEST_TEXT);
        TextToSpeechResponse response = speechModel.call(prompt);

        assertThat(response.getResults()).isNotEmpty();
        assertThat(response.getResult().getOutput()).isNotNull();
    }

    @Test
    void testCustomOptions() {
        // Test speech synthesis with custom options
        mockSuccessfulSpeechGeneration();

        DashScopeAudioSpeechOptions customOptions = DashScopeAudioSpeechOptions.builder()
                .model(TEST_MODEL)
                .voice("Cherry")
                .speed(1.5)
                .pitch(1.2)
                .volume(80)
                .sampleRate(24000)
                .responseFormat(DashScopeAudioSpeechApi.ResponseFormat.WAV)
                .build();

        TextToSpeechPrompt prompt = new TextToSpeechPrompt(TEST_TEXT, customOptions);
        TextToSpeechResponse response = speechModel.call(prompt);

        assertThat(response.getResults()).isNotEmpty();
        byte[] output = response.getResult().getOutput();

        assertThat(response.getResult().getOutput()).isNotNull();
    }

    @Test
    void testStreamingSpeech() {
        // Test streaming speech synthesis
        mockSuccessfulSpeechGeneration();

        TextToSpeechPrompt prompt = new TextToSpeechPrompt(TEST_TEXT);
        Flux<TextToSpeechResponse> responseFlux = speechModel.stream(prompt);

        assertThat(responseFlux).isNotNull();
        TextToSpeechResponse response = responseFlux.blockFirst();
        assertThat(response).isNotNull();
        assertThat(response.getResults()).isNotEmpty();
        assertThat(response.getResult().getOutput()).isNotNull();
    }

    @Test
    void testDefaultConstructor() {
        // Test model creation with default options
        DashScopeAudioSpeechModel modelWithDefaults = new DashScopeAudioSpeechModel(audioSpeechApi);
        assertThat(modelWithDefaults).isNotNull();
    }

    @Test
    void testCreateRequestWithDefaultOptions() {
        // Test request creation with default options
        TextToSpeechPrompt prompt = new TextToSpeechPrompt(TEST_TEXT);
        String taskId = "test-task-id";

        DashScopeAudioSpeechApi.Request request = speechModel.createRequest(prompt, taskId, com.alibaba.cloud.ai.dashscope.protocol.DashScopeWebSocketClient.EventType.RUN_TASK);

        assertThat(request).isNotNull();
        assertThat(request.header()).isNotNull();
        assertThat(request.header().taskId()).isEqualTo(taskId);
        assertThat(request.payload()).isNotNull();
        assertThat(request.payload().input()).isNotNull();
        assertThat(request.payload().input().text()).isEqualTo(TEST_TEXT);
    }

    @Test
    void testCreateRequestWithCustomOptions() {
        // Test request creation with custom options
        DashScopeAudioSpeechOptions customOptions = DashScopeAudioSpeechOptions.builder()
                .model(TEST_MODEL)
                .voice("xiaoyun")
                .speed(1.5)
                .pitch(1.2)
                .volume(80)
                .sampleRate(24000)
                .responseFormat(DashScopeAudioSpeechApi.ResponseFormat.WAV)
                .enableSsml(true)
                .build();

        TextToSpeechPrompt prompt = new TextToSpeechPrompt(TEST_TEXT, customOptions);
        String taskId = "test-task-id";

        DashScopeAudioSpeechApi.Request request = speechModel.createRequest(prompt, taskId, com.alibaba.cloud.ai.dashscope.protocol.DashScopeWebSocketClient.EventType.RUN_TASK);

        assertThat(request).isNotNull();
        assertThat(request.payload()).isNotNull();
        assertThat(request.payload().parameters()).isNotNull();
    }

    @Test
    void testBuilder() {
        DashScopeAudioSpeechModel model1 = DashScopeAudioSpeechModel.builder()
                .audioSpeechApi(audioSpeechApi)
                .build();

        DashScopeAudioSpeechModel model2 = DashScopeAudioSpeechModel.builder()
                .audioSpeechApi(audioSpeechApi)
                .defaultOptions(defaultOptions)
                .retryTemplate(RetryTemplate.builder().build())
                .build();

        DashScopeAudioSpeechModel clone1 = model1.clone();
        DashScopeAudioSpeechModel clone2 = model2.clone();

        Builder mutate1 = model1.mutate();
        Builder mutate2 = model2.mutate();

        assertThat(model1).isNotNull();
        assertThat(model2).isNotNull();
        assertThat(clone1).isNotNull();
        assertThat(clone2).isNotNull();
        assertThat(mutate1).isNotNull();
        assertThat(mutate2).isNotNull();
    }

    private void mockSuccessfulSpeechGeneration() {
        // Mock successful speech synthesis
        ByteBuffer audioBuffer = ByteBuffer.wrap(TEST_AUDIO_DATA);
        when(audioSpeechApi.streamBinaryOut(any())).thenReturn(Flux.just(audioBuffer));
    }

}
