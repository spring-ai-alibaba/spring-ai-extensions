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
package com.alibaba.cloud.ai.dashscope.video;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.cloud.ai.dashscope.api.DashScopeVideoApi;
import com.alibaba.cloud.ai.dashscope.video.DashScopeVideoOptions.InputOptions;
import com.alibaba.cloud.ai.dashscope.video.DashScopeVideoOptions.ParametersOptions;
import com.alibaba.cloud.ai.dashscope.video.model.DashScopeVideoRequest;
import com.alibaba.cloud.ai.dashscope.video.model.DashScopeVideoResponse;
import com.alibaba.cloud.ai.dashscope.video.model.DashScopeVideoResponse.VideoOutput;
import com.alibaba.cloud.ai.dashscope.video.model.DashScopeVideoResponse.VideoUsage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.support.RetryTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Test cases for DashScopeVideoModel. Tests cover basic video generation, custom options,
 * async task handling, error handling, and edge cases.
 *
 * @author yingzi
 * @since 1.1.0.0
 */
class DashScopeVideoModelTests {

    // Test constants
    private static final String TEST_MODEL = "wan2.2-t2v-plus";

    private static final String TEST_TASK_ID = "test-task-id-123456";

    private static final String TEST_REQUEST_ID = "test-request-id-789";

    private static final String TEST_VIDEO_URL = "https://example.com/generated-video.mp4";

    private static final String TEST_PROMPT = "低对比度，在一个复古的70年代风格地铁站里，街头音乐家在昏暗的色彩和粗糙的质感中演奏";

    private DashScopeVideoApi dashScopeVideoApi;

    private DashScopeVideoModel videoModel;

    private DashScopeVideoOptions defaultOptions;

    @BeforeEach
    void setUp() {
        // Initialize mock objects and test instances
        dashScopeVideoApi = Mockito.mock(DashScopeVideoApi.class);

        // Create default options with basic configuration
        defaultOptions = DashScopeVideoOptions.builder()
                .model(TEST_MODEL)
                .input(InputOptions.builder().prompt(TEST_PROMPT).build())
                .parameters(ParametersOptions.builder().size("832*480").promptExtend(true).build())
                .build();

        videoModel = new DashScopeVideoModel(dashScopeVideoApi, defaultOptions, RetryTemplate.builder().build());
    }

    @Test
    void testBasicVideoGeneration() {
        // Test basic video generation with successful response
        mockSuccessfulVideoGeneration();

        VideoPrompt prompt = VideoPrompt.builder().content(TEST_PROMPT).build();
        VideoResponse response = videoModel.call(prompt);

        assertThat(response).isNotNull();
        assertThat(response.getResult()).isNotNull();
        assertThat(response.getResult().getOutput().videoUrl()).isEqualTo(TEST_VIDEO_URL);
        assertThat(response.getResult().getOutput().taskStatus()).isEqualTo("SUCCEEDED");
    }

    @Test
    void testVideoGenerationWithCustomOptions() {
        // Test video generation with custom options
        mockSuccessfulVideoGeneration();

        DashScopeVideoOptions customOptions = DashScopeVideoOptions.builder()
                .model(TEST_MODEL)
                .input(InputOptions.builder().prompt(TEST_PROMPT).negativePrompt("低质量，模糊").build())
                .parameters(ParametersOptions.builder().size("1280*720").promptExtend(false).duration(5).build())
                .build();

        VideoPrompt prompt = VideoPrompt.builder().options(customOptions).build();
        VideoResponse response = videoModel.call(prompt);

        assertThat(response).isNotNull();
        assertThat(response.getResult()).isNotNull();
        assertThat(response.getResult().getOutput().videoUrl()).isEqualTo(TEST_VIDEO_URL);
    }

    @Test
    void testNullResponseThrowsException() {
        // Test handling of null API response - should throw exception
        when(dashScopeVideoApi.submitVideoGenTask(any(DashScopeVideoRequest.class))).thenReturn(null);

        VideoPrompt prompt = VideoPrompt.builder().content(TEST_PROMPT).build();

        assertThatThrownBy(() -> videoModel.call(prompt)).isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Failed to submit video generation task: null response");
    }

    @Test
    void testNullBodyInResponseThrowsException() {
        // Test handling of null body in response - should throw exception
        when(dashScopeVideoApi.submitVideoGenTask(any(DashScopeVideoRequest.class))).thenReturn(ResponseEntity.ok(null));

        VideoPrompt prompt = VideoPrompt.builder().content(TEST_PROMPT).build();

        assertThatThrownBy(() -> videoModel.call(prompt)).isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Failed to submit video generation task: null response");
    }

    @Test
    void testNullTaskIdResponseThrowsException() {
        // Test handling of null task ID in submit response - should throw exception
        VideoOutput submitOutput = new VideoOutput(null, "PENDING", null, null, null, null, null, null, null, null, null, null, false, false, false, null, null);
        DashScopeVideoResponse submitResponse = new DashScopeVideoResponse(TEST_REQUEST_ID, submitOutput, null);
        when(dashScopeVideoApi.submitVideoGenTask(any(DashScopeVideoRequest.class))).thenReturn(ResponseEntity.ok(submitResponse));

        VideoPrompt prompt = VideoPrompt.builder().content(TEST_PROMPT).build();

        assertThatThrownBy(() -> videoModel.call(prompt)).isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Failed to submit video generation task: invalid output");
    }

    @Test
    void testNullPrompt() {
        // Test handling of null prompt
        assertThatThrownBy(() -> videoModel.call(null)).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Prompt");
    }

    @Test
    void testEmptyPrompt() {
        // Test handling of empty prompt
        assertThatThrownBy(() -> videoModel.call(VideoPrompt.builder()
                .messages(new ArrayList<>())
                .build())).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("Prompt instructions");
    }

    @Test
    void testBuilderPattern() {
        // Test using builder pattern to create model
        DashScopeVideoModel builtModel = DashScopeVideoModel.builder()
                .videoApi(dashScopeVideoApi)
                .defaultOptions(defaultOptions)
                .retryTemplate(RetryTemplate.builder().build())
                .build();

        assertThat(builtModel).isNotNull();
    }

    @Test
    void testVideoOptionsWithSeed() {
        // Test video generation with seed parameter for reproducibility
        mockSuccessfulVideoGeneration();

        DashScopeVideoOptions optionsWithSeed = DashScopeVideoOptions.builder()
                .model(TEST_MODEL)
                .input(InputOptions.builder().prompt(TEST_PROMPT).build())
                .parameters(ParametersOptions.builder().size("832*480").seed(42L).build())
                .build();

        VideoPrompt prompt = VideoPrompt.builder().options(optionsWithSeed).build();
        VideoResponse response = videoModel.call(prompt);

        assertThat(response).isNotNull();
        assertThat(response.getResult()).isNotNull();
    }

    @Test
    void testVideoGenerationFailureThrowsException() {
        // Test handling of failed video generation task - should throw exception
        mockFailedVideoGeneration();

        VideoPrompt prompt = VideoPrompt.builder().content(TEST_PROMPT).build();

        assertThatThrownBy(() -> videoModel.call(prompt)).isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Video generation task failed");
    }

    @Test
    void testVideoOptionsWithAllParameters() {
        // Test video generation with comprehensive parameter configuration
        mockSuccessfulVideoGeneration();

        DashScopeVideoOptions comprehensiveOptions = DashScopeVideoOptions.builder()
                .model(TEST_MODEL).input(InputOptions.builder().prompt(TEST_PROMPT)
                        .negativePrompt("低质量")
                        .firstFrameUrl("https://example.com/first-frame.jpg")
                        .build()).parameters(ParametersOptions.builder().size("1280*720")
                        .promptExtend(true)
                        .duration(5)
                        .seed(123L)
                        .resolution("1080p")
                        .build())
                .build();

        VideoPrompt prompt = VideoPrompt.builder().options(comprehensiveOptions).build();
        VideoResponse response = videoModel.call(prompt);

        assertThat(response).isNotNull();
        assertThat(response.getResult()).isNotNull();
        assertThat(response.getResult().getOutput().videoUrl()).isEqualTo(TEST_VIDEO_URL);
    }

    @Test
    void testVideoGenerationWithImageToVideo() {
        // Test image-to-video generation with image URL input
        mockSuccessfulVideoGeneration();

        DashScopeVideoOptions imageToVideoOptions = DashScopeVideoOptions.builder()
                .model(TEST_MODEL)
                .input(InputOptions.builder().prompt(TEST_PROMPT).imageUrl("https://example.com/input.jpg").build())
                .parameters(ParametersOptions.builder().size("832*480").duration(5).build())
                .build();

        VideoPrompt prompt = VideoPrompt.builder().options(imageToVideoOptions).build();
        VideoResponse response = videoModel.call(prompt);

        assertThat(response).isNotNull();
        assertThat(response.getResult()).isNotNull();
    }

    @Test
    void testImageDetectionModel() {
        // Test image detection model which returns directly without polling
        // Image detection models return results synchronously (no task polling)
        DashScopeVideoOptions detectionOptions = DashScopeVideoOptions.builder()
                .model("emoji-detect-v1") // Detect model
                .input(InputOptions.builder().imageUrl("https://example.com/test.jpg").build())
                .parameters(ParametersOptions.builder().ratio("1:1").build())
                .build();

        // Mock detection response - no task polling needed
        VideoOutput detectionOutput = new VideoOutput(null, null, null, null, null, null, null, null, null, null, null, null, false, false, false, List.of(212, 194, 460, 441), List.of(63, 30, 609, 575));
        VideoUsage detectionUsage = new VideoUsage(0, 0, 0, 0, 0, null, null, null, 1);
        DashScopeVideoResponse detectionResponse = new DashScopeVideoResponse(TEST_REQUEST_ID, detectionOutput, detectionUsage);

        when(dashScopeVideoApi.submitVideoGenTask(any(DashScopeVideoRequest.class))).thenReturn(ResponseEntity.ok(detectionResponse));

        VideoPrompt prompt = VideoPrompt.builder().options(detectionOptions).build();
        VideoResponse response = videoModel.call(prompt);

        // Verify detection results
        assertThat(response).isNotNull();
        assertThat(response.getResult()).isNotNull();
        assertThat(response.getResult().getOutput().bboxFace()).isNotNull();
        assertThat(response.getResult().getOutput().extBboxFace()).isNotNull();
        assertThat(response.getResult().usage().imageCount()).isEqualTo(1);
    }

    private void mockSuccessfulVideoGeneration() {
        // Mock successful task submission
        VideoOutput submitOutput = new VideoOutput(TEST_TASK_ID, "PENDING", null, null, null, null, null, null, null, null, null, null, false, false, false, null, null);
        DashScopeVideoResponse submitResponse = new DashScopeVideoResponse(TEST_REQUEST_ID, submitOutput, null);
        when(dashScopeVideoApi.submitVideoGenTask(any(DashScopeVideoRequest.class))).thenReturn(ResponseEntity.ok(submitResponse));

        // Mock successful task completion
        VideoOutput completedOutput = new VideoOutput(TEST_TASK_ID, "SUCCEEDED", null, null, null, null, null, TEST_VIDEO_URL, null, null, null, null, false, false, false, null, null);
        VideoUsage usage = new VideoUsage(5, 0, 5, 1, 0, "832*480", "16:9", "5s", 0);
        DashScopeVideoResponse completedResponse = new DashScopeVideoResponse(TEST_REQUEST_ID, completedOutput, usage);
        when(dashScopeVideoApi.queryVideoGenTask(TEST_TASK_ID)).thenReturn(ResponseEntity.ok(completedResponse));
    }

    private void mockFailedVideoGeneration() {
        // Mock successful task submission but failed completion
        VideoOutput submitOutput = new VideoOutput(TEST_TASK_ID, "PENDING", null, null, null, null, null, null, null, null, null, null, false, false, false, null, null);
        DashScopeVideoResponse submitResponse = new DashScopeVideoResponse(TEST_REQUEST_ID, submitOutput, null);
        when(dashScopeVideoApi.submitVideoGenTask(any(DashScopeVideoRequest.class))).thenReturn(ResponseEntity.ok(submitResponse));

        // Mock failed task completion
        VideoOutput failedOutput = new VideoOutput(TEST_TASK_ID, "FAILED", null, null, null, null, null, null, null, "VIDEO_GEN_ERROR", "Video generation failed due to internal error", null, false, false, false, null, null);
        DashScopeVideoResponse failedResponse = new DashScopeVideoResponse(TEST_REQUEST_ID, failedOutput, null);
        when(dashScopeVideoApi.queryVideoGenTask(anyString())).thenReturn(ResponseEntity.ok(failedResponse));
    }

}
