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

import java.util.List;

import com.alibaba.cloud.ai.dashscope.api.DashScopeAudioTranscriptionApi;
import com.alibaba.cloud.ai.dashscope.audio.DashScopeAudioTranscriptionModel.Builder;
import com.alibaba.cloud.ai.dashscope.common.DashScopeApiConstants;
import com.alibaba.cloud.ai.dashscope.spec.DashScopeModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.audio.transcription.AudioTranscriptionResponse;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.support.RetryTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Test cases for DashScopeAudioTranscriptionModel. Tests cover basic audio transcription,
 * custom options, async task handling, error handling, and edge cases.
 *
 * @author yingzi
 * @since 1.1.0.0
 */
class DashScopeAudioTranscriptionModelTests {

    // Test constants
    private static final String TEST_MODEL = DashScopeModel.AudioModel.PARAFORMER_V1.getValue();

    private static final String TEST_TASK_ID = "test-task-id";

    private static final String TEST_REQUEST_ID = "test-request-id";

    private static final String TEST_TRANSCRIPTION_URL = "https://example.com/transcription.json";

    private static final String TEST_TRANSCRIBED_TEXT = "Hello, this is a test transcription";

    private static final String AUDIO_FILE_URL = "https://dashscope.oss-cn-beijing.aliyuncs.com/samples/audio/paraformer/hello_world_female2.wav";

    private DashScopeAudioTranscriptionApi audioTranscriptionApi;

    private DashScopeAudioTranscriptionModel transcriptionModel;

    private DashScopeAudioTranscriptionOptions defaultOptions;

    @BeforeEach
    void setUp() {
        // Initialize mock objects and test instances
        audioTranscriptionApi = Mockito.mock(DashScopeAudioTranscriptionApi.class);
        defaultOptions = DashScopeAudioTranscriptionOptions.builder().model(TEST_MODEL).build();
        transcriptionModel = new DashScopeAudioTranscriptionModel(audioTranscriptionApi, defaultOptions, RetryTemplate.builder()
                .maxAttempts(1)
                .build());
    }

    @Test
    void testBasicTranscription() throws Exception {
        // Test basic audio transcription with successful response
        mockSuccessfulTranscription();

        Resource audioResource = new UrlResource(AUDIO_FILE_URL);
        AudioTranscriptionPrompt prompt = new AudioTranscriptionPrompt(audioResource);
        AudioTranscriptionResponse response = transcriptionModel.call(prompt);

        assertThat(response).isNotNull();
        assertThat(response.getResult()).isNotNull();
        assertThat(response.getResult().getOutput()).isEqualTo(TEST_TRANSCRIBED_TEXT);
        assertThat(response.getMetadata()).isNotNull();
        assertThat((String) response.getMetadata().get(DashScopeApiConstants.REQUEST_ID)).isEqualTo(TEST_REQUEST_ID);
    }

    @Test
    void testCustomOptions() throws Exception {
        // Test transcription with custom options
        mockSuccessfulTranscription();

        DashScopeAudioTranscriptionOptions customOptions = DashScopeAudioTranscriptionOptions.builder()
                .model(TEST_MODEL)
                .disfluencyRemovalEnabled(true)
                .timestampAlignmentEnabled(true)
                .languageHints(List.of("zh", "en"))
                .build();

        Resource audioResource = new UrlResource(AUDIO_FILE_URL);
        AudioTranscriptionPrompt prompt = new AudioTranscriptionPrompt(audioResource, customOptions);
        AudioTranscriptionResponse response = transcriptionModel.call(prompt);

        assertThat(response).isNotNull();
        assertThat(response.getResult()).isNotNull();
        assertThat(response.getResult().getOutput()).isEqualTo(TEST_TRANSCRIBED_TEXT);
    }

    @Test
    void testFailedTranscription() throws Exception {
        // Test handling of failed transcription
        mockFailedTranscription();

        Resource audioResource = new UrlResource(AUDIO_FILE_URL);
        AudioTranscriptionPrompt prompt = new AudioTranscriptionPrompt(audioResource);
        AudioTranscriptionResponse response = transcriptionModel.call(prompt);

        assertThat(response).isNotNull();
        assertThat(response.getResult()).isNotNull();
        assertThat(response.getResult().getOutput()).isNull();
        assertThat(response.getMetadata()).isNotNull();
    }

    @Test
    void testNullTaskId() throws Exception {
        // Test handling of null task ID from API
        DashScopeAudioTranscriptionApi.Response.Output output = new DashScopeAudioTranscriptionApi.Response.Output(null, null, null, null, null, null, null);
        DashScopeAudioTranscriptionApi.Response submitResponse = new DashScopeAudioTranscriptionApi.Response(TEST_REQUEST_ID, new DashScopeAudioTranscriptionApi.Response.Usage(0), output);

        when(audioTranscriptionApi.submitTask(any())).thenReturn(ResponseEntity.ok(submitResponse));

        Resource audioResource = new UrlResource(AUDIO_FILE_URL);
        AudioTranscriptionPrompt prompt = new AudioTranscriptionPrompt(audioResource);
        AudioTranscriptionResponse response = transcriptionModel.call(prompt);

        assertThat(response).isNotNull();
        assertThat(response.getResult()).isNotNull();
        assertThat(response.getResult().getOutput()).isNull();
        assertThat((String) response.getMetadata().get("taskStatus")).isEqualTo("NO_TASK_ID");
    }

    @Test
    void testNullResponse() throws Exception {
        // Test handling of null API response
        when(audioTranscriptionApi.submitTask(any())).thenReturn(null);

        Resource audioResource = new UrlResource(AUDIO_FILE_URL);
        AudioTranscriptionPrompt prompt = new AudioTranscriptionPrompt(audioResource);
        AudioTranscriptionResponse response = transcriptionModel.call(prompt);

        assertThat(response).isNotNull();
        assertThat(response.getResult()).isNotNull();
        assertThat(response.getResult().getOutput()).isNull();
        assertThat((String) response.getMetadata().get("taskStatus")).isEqualTo("NO_TASK_ID");
    }

    @Test
    void testNullPrompt() {
        // Test handling of null prompt - call with AudioTranscriptionPrompt explicitly
        assertThatThrownBy(() -> {
            AudioTranscriptionPrompt nullPrompt = null;
            transcriptionModel.call(nullPrompt);
        }).isInstanceOf(NullPointerException.class);
    }

    @Test
    void testConstructorValidation() {
        // Test constructor parameter validation
        assertThatThrownBy(() -> new DashScopeAudioTranscriptionModel(null, defaultOptions)).isInstanceOf(NullPointerException.class)
                .hasMessageContaining("api must not be null");

        assertThatThrownBy(() -> new DashScopeAudioTranscriptionModel(audioTranscriptionApi, null)).isInstanceOf(NullPointerException.class)
                .hasMessageContaining("options must not be null");

        assertThatThrownBy(() -> new DashScopeAudioTranscriptionModel(audioTranscriptionApi, defaultOptions, null)).isInstanceOf(NullPointerException.class)
                .hasMessageContaining("retryTemplate must not be null");
    }

    @Test
    void testParaformerOptions() throws Exception {
        // Test with Paraformer model specific options
        mockSuccessfulTranscription();

        DashScopeAudioTranscriptionOptions options = DashScopeAudioTranscriptionOptions.builder()
                .model(DashScopeModel.AudioModel.PARAFORMER_V1.getValue())
                .resourceId("test-resource-id")
                .channelId(List.of(0))
                .disfluencyRemovalEnabled(true)
                .build();

        Resource audioResource = new UrlResource(AUDIO_FILE_URL);
        AudioTranscriptionPrompt prompt = new AudioTranscriptionPrompt(audioResource, options);
        AudioTranscriptionResponse response = transcriptionModel.call(prompt);

        assertThat(response).isNotNull();
        assertThat(response.getResult()).isNotNull();
    }

    @Test
    void testBuilder() {
        DashScopeAudioTranscriptionModel model1 = DashScopeAudioTranscriptionModel.builder()
                .audioTranscriptionApi(audioTranscriptionApi)
                .build();

        DashScopeAudioTranscriptionModel model2 = DashScopeAudioTranscriptionModel.builder()
                .audioTranscriptionApi(audioTranscriptionApi)
                .defaultOptions(defaultOptions)
                .retryTemplate(RetryTemplate.builder().build())
                .build();

        DashScopeAudioTranscriptionModel clone1 = model1.clone();
        DashScopeAudioTranscriptionModel clone2 = model2.clone();

        Builder mutate1 = model1.mutate();
        Builder mutate2 = model2.mutate();

        assertThat(model1).isNotNull();
        assertThat(model2).isNotNull();
        assertThat(clone1).isNotNull();
        assertThat(clone2).isNotNull();
        assertThat(mutate1).isNotNull();
        assertThat(mutate2).isNotNull();
    }

    private void mockSuccessfulTranscription() {
        // Mock successful task submission
        DashScopeAudioTranscriptionApi.Response.Output submitOutput = new DashScopeAudioTranscriptionApi.Response.Output(TEST_TASK_ID, DashScopeAudioTranscriptionApi.TaskStatus.PENDING, null, null, null, null, null);
        DashScopeAudioTranscriptionApi.Response submitResponse = new DashScopeAudioTranscriptionApi.Response(TEST_REQUEST_ID, new DashScopeAudioTranscriptionApi.Response.Usage(0), submitOutput);
        when(audioTranscriptionApi.submitTask(any())).thenReturn(ResponseEntity.ok(submitResponse));

        // Mock successful task completion
        DashScopeAudioTranscriptionApi.Response.Output.Result result = new DashScopeAudioTranscriptionApi.Response.Output.Result(null, TEST_TRANSCRIPTION_URL, null);
        DashScopeAudioTranscriptionApi.Response.Output completedOutput = new DashScopeAudioTranscriptionApi.Response.Output(TEST_TASK_ID, DashScopeAudioTranscriptionApi.TaskStatus.SUCCEEDED, null, null, null, List.of(result), null);
        DashScopeAudioTranscriptionApi.Response completedResponse = new DashScopeAudioTranscriptionApi.Response(TEST_REQUEST_ID, new DashScopeAudioTranscriptionApi.Response.Usage(100), completedOutput);
        when(audioTranscriptionApi.queryTaskResult(TEST_TASK_ID)).thenReturn(ResponseEntity.ok(completedResponse));

        // Mock outcome
        DashScopeAudioTranscriptionApi.Outcome.Transcript transcript = new DashScopeAudioTranscriptionApi.Outcome.Transcript(0, 5000, TEST_TRANSCRIBED_TEXT, null);
        DashScopeAudioTranscriptionApi.Outcome outcome = new DashScopeAudioTranscriptionApi.Outcome(null, null, List.of(transcript));
        when(audioTranscriptionApi.getOutcome(TEST_TRANSCRIPTION_URL)).thenReturn(outcome);
    }

    private void mockFailedTranscription() {
        // Mock successful task submission but failed completion
        DashScopeAudioTranscriptionApi.Response.Output submitOutput = new DashScopeAudioTranscriptionApi.Response.Output(TEST_TASK_ID, DashScopeAudioTranscriptionApi.TaskStatus.PENDING, null, null, null, null, null);
        DashScopeAudioTranscriptionApi.Response submitResponse = new DashScopeAudioTranscriptionApi.Response(TEST_REQUEST_ID, new DashScopeAudioTranscriptionApi.Response.Usage(0), submitOutput);
        when(audioTranscriptionApi.submitTask(any())).thenReturn(ResponseEntity.ok(submitResponse));

        // Mock failed task completion
        DashScopeAudioTranscriptionApi.Response.Output failedOutput = new DashScopeAudioTranscriptionApi.Response.Output(TEST_TASK_ID, DashScopeAudioTranscriptionApi.TaskStatus.FAILED, null, null, null, null, null);
        DashScopeAudioTranscriptionApi.Response failedResponse = new DashScopeAudioTranscriptionApi.Response(TEST_REQUEST_ID, new DashScopeAudioTranscriptionApi.Response.Usage(0), failedOutput);
        when(audioTranscriptionApi.queryTaskResult(TEST_TASK_ID)).thenReturn(ResponseEntity.ok(failedResponse));
    }

}
