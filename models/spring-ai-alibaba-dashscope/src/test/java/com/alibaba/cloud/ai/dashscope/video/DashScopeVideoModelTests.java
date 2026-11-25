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
import com.alibaba.cloud.ai.dashscope.common.DashScopeVideoApiConstants;
import com.alibaba.cloud.ai.dashscope.spec.DashScopeModel;
import com.alibaba.cloud.ai.dashscope.video.DashScopeVideoOptions.InputOptions;
import com.alibaba.cloud.ai.dashscope.video.DashScopeVideoOptions.ParametersOptions;
import com.alibaba.cloud.ai.dashscope.video.model.DashScopeVideoRequest;
import com.alibaba.cloud.ai.dashscope.video.model.DashScopeVideoResponse;
import com.alibaba.cloud.ai.dashscope.video.model.DashScopeVideoResponse.VideoOutput;
import com.alibaba.cloud.ai.dashscope.video.model.DashScopeVideoResponse.VideoUsage;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.ai.retry.RetryUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import tools.jackson.databind.json.JsonMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
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

    private static final JsonMapper JSON_MAPPER = JsonMapper.builder().build();

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

        videoModel = new DashScopeVideoModel(dashScopeVideoApi, defaultOptions, RetryUtils.DEFAULT_RETRY_TEMPLATE);
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
                .retryTemplate(RetryUtils.DEFAULT_RETRY_TEMPLATE)
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
    void testVideoGenerationWithWatermarkParameter() {
        mockSuccessfulVideoGeneration();

        DashScopeVideoOptions optionsWithWatermark = DashScopeVideoOptions.builder()
                .model(TEST_MODEL)
                .input(InputOptions.builder().prompt(TEST_PROMPT).build())
                .parameters(ParametersOptions.builder().size("832*480").watermark(true).build())
                .build();

        videoModel.call(VideoPrompt.builder().options(optionsWithWatermark).build());

        ArgumentCaptor<DashScopeVideoRequest> requestCaptor = ArgumentCaptor.forClass(DashScopeVideoRequest.class);
        verify(dashScopeVideoApi).submitVideoGenTask(requestCaptor.capture());
        DashScopeVideoRequest.VideoParameters parameters = requestCaptor.getValue().getParameters();

        assertThat(parameters).isNotNull();
        assertJsonProperty(DashScopeVideoRequest.VideoParameters.class, "watermark", "watermark");
        assertThat(ReflectionTestUtils.getField(parameters, "watermark")).isEqualTo(true);
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
    void newVideoGenerationSynthesisModelsResolveToVideoSynthesisPath() {
        List<DashScopeModel.VideoModel> videoGenerationModels = List.of(
                DashScopeModel.VideoModel.HAPPYHORSE_1_0_T2V,
                DashScopeModel.VideoModel.HAPPYHORSE_1_0_I2V,
                DashScopeModel.VideoModel.HAPPYHORSE_1_0_R2V,
                DashScopeModel.VideoModel.HAPPYHORSE_1_0_VIDEO_EDIT,
                DashScopeModel.VideoModel.PIXVERSE_PIXVERSE_C1_T2V,
                DashScopeModel.VideoModel.PIXVERSE_PIXVERSE_V6_T2V,
                DashScopeModel.VideoModel.PIXVERSE_PIXVERSE_C1_IT2V,
                DashScopeModel.VideoModel.PIXVERSE_PIXVERSE_V6_IT2V,
                DashScopeModel.VideoModel.PIXVERSE_PIXVERSE_C1_KF2V,
                DashScopeModel.VideoModel.PIXVERSE_PIXVERSE_C1_R2V,
                DashScopeModel.VideoModel.KLING_V3_VIDEO_GENERATION,
                DashScopeModel.VideoModel.KLING_V3_OMNI_VIDEO_GENERATION,
                DashScopeModel.VideoModel.VIDUG3_TURBO_TEXT2VIDEO,
                DashScopeModel.VideoModel.VIDUG3_PRO_IMG2VIDEO,
                DashScopeModel.VideoModel.VIDUG3_TURBO_START_END2VIDEO,
                DashScopeModel.VideoModel.VIDUG3_MIX_REFERENCE2VIDEO,
                DashScopeModel.VideoModel.VIDUQ2_PRO_REFERENCE2VIDEO);

        assertThat(videoGenerationModels)
                .allSatisfy(model -> assertThat(DashScopeVideoApiConstants.getPathByModelName(model.getName()))
                        .isEqualTo(DashScopeVideoApiConstants.VIDEO_GENERATION_SYNTHESIS));
    }

    @Test
    void happyHorseTextToVideoUsesOfficialRequestShape() throws Exception {
        assertSubmittedRequestJson(DashScopeVideoOptions.builder()
                .model(DashScopeModel.VideoModel.HAPPYHORSE_1_0_T2V.getName())
                .input(InputOptions.builder()
                        .prompt("一座由硬纸板和瓶盖搭建的微型城市，在夜晚焕发出生机。一列硬纸板火车缓缓驶过，小灯点缀其间，照亮前路。")
                        .build())
                .parameters(ParametersOptions.builder().resolution("720P").ratio("16:9").duration(5).build())
                .build(), """
                {
                  "model": "happyhorse-1.0-t2v",
                  "input": {
                    "prompt": "一座由硬纸板和瓶盖搭建的微型城市，在夜晚焕发出生机。一列硬纸板火车缓缓驶过，小灯点缀其间，照亮前路。"
                  },
                  "parameters": {
                    "resolution": "720P",
                    "ratio": "16:9",
                    "duration": 5
                  }
                }
                """);
    }

    @Test
    void happyHorseImageToVideoUsesFirstFrameMediaRequestShape() throws Exception {
        assertSubmittedRequestJson(DashScopeVideoOptions.builder()
                .model(DashScopeModel.VideoModel.HAPPYHORSE_1_0_I2V.getName())
                .input(InputOptions.builder()
                        .prompt("一只猫在草地上奔跑")
                        .media(List.of(media("first_frame", "https://cdn.translate.alibaba.com/r/wanx-demo-1.png")))
                        .build())
                .parameters(ParametersOptions.builder().resolution("720P").duration(5).build())
                .build(), """
                {
                  "model": "happyhorse-1.0-i2v",
                  "input": {
                    "prompt": "一只猫在草地上奔跑",
                    "media": [
                      {
                        "type": "first_frame",
                        "url": "https://cdn.translate.alibaba.com/r/wanx-demo-1.png"
                      }
                    ]
                  },
                  "parameters": {
                    "resolution": "720P",
                    "duration": 5
                  }
                }
                """);
    }

    @Test
    void happyHorseReferenceToVideoUsesOfficialRequestShape() throws Exception {
        assertSubmittedRequestJson(DashScopeVideoOptions.builder()
                .model(DashScopeModel.VideoModel.HAPPYHORSE_1_0_R2V.getName())
                .input(InputOptions.builder()
                        .prompt("[Image 1]中身着红色旗袍的女性，镜头先以侧面中景勾勒旗袍修身剪裁与S型曲线，随即切换至低角度仰拍，捕捉她轻抬玉手展开[Image 2]中的折扇的同时，[Image 3]中的流苏耳坠随头部转动轻盈摆动的细节，最后推近至面部特写，定格在她指尖轻点扇骨、眼波流转间的含蓄风情，多视角全方位展现东方韵味。")
                        .media(List.of(
                                media("reference_image",
                                        "https://help-static-aliyun-doc.aliyuncs.com/file-manage-files/zh-CN/20260424/mvzfud/hh-v2v-girl.jpg"),
                                media("reference_image",
                                        "https://help-static-aliyun-doc.aliyuncs.com/file-manage-files/zh-CN/20260424/fvuihk/hh-v2v2-folding-fan.jpg"),
                                media("reference_image",
                                        "https://help-static-aliyun-doc.aliyuncs.com/file-manage-files/zh-CN/20260424/imerii/hh-v2v-earrings.jpg")))
                        .build())
                .parameters(ParametersOptions.builder().resolution("720P").ratio("16:9").duration(5).build())
                .build(), """
                {
                  "model": "happyhorse-1.0-r2v",
                  "input": {
                    "prompt": "[Image 1]中身着红色旗袍的女性，镜头先以侧面中景勾勒旗袍修身剪裁与S型曲线，随即切换至低角度仰拍，捕捉她轻抬玉手展开[Image 2]中的折扇的同时，[Image 3]中的流苏耳坠随头部转动轻盈摆动的细节，最后推近至面部特写，定格在她指尖轻点扇骨、眼波流转间的含蓄风情，多视角全方位展现东方韵味。",
                    "media": [
                      {
                        "type": "reference_image",
                        "url": "https://help-static-aliyun-doc.aliyuncs.com/file-manage-files/zh-CN/20260424/mvzfud/hh-v2v-girl.jpg"
                      },
                      {
                        "type": "reference_image",
                        "url": "https://help-static-aliyun-doc.aliyuncs.com/file-manage-files/zh-CN/20260424/fvuihk/hh-v2v2-folding-fan.jpg"
                      },
                      {
                        "type": "reference_image",
                        "url": "https://help-static-aliyun-doc.aliyuncs.com/file-manage-files/zh-CN/20260424/imerii/hh-v2v-earrings.jpg"
                      }
                    ]
                  },
                  "parameters": {
                    "resolution": "720P",
                    "ratio": "16:9",
                    "duration": 5
                  }
                }
                """);
    }

    @Test
    void happyHorseVideoEditUsesOfficialRequestShape() throws Exception {
        assertSubmittedRequestJson(DashScopeVideoOptions.builder()
                .model(DashScopeModel.VideoModel.HAPPYHORSE_1_0_VIDEO_EDIT.getName())
                .input(InputOptions.builder()
                        .prompt("让视频中的马头人身角色穿上图片中的条纹毛衣")
                        .media(List.of(
                                media("video",
                                        "https://help-static-aliyun-doc.aliyuncs.com/file-manage-files/zh-CN/20260409/dozxak/Wan_Video_Edit_33_1.mp4"),
                                media("reference_image",
                                        "https://help-static-aliyun-doc.aliyuncs.com/file-manage-files/zh-CN/20260415/hynnff/wan-video-edit-clothes.webp")))
                        .build())
                .parameters(ParametersOptions.builder().resolution("720P").audioSetting("origin").build())
                .build(), """
                {
                  "model": "happyhorse-1.0-video-edit",
                  "input": {
                    "prompt": "让视频中的马头人身角色穿上图片中的条纹毛衣",
                    "media": [
                      {
                        "type": "video",
                        "url": "https://help-static-aliyun-doc.aliyuncs.com/file-manage-files/zh-CN/20260409/dozxak/Wan_Video_Edit_33_1.mp4"
                      },
                      {
                        "type": "reference_image",
                        "url": "https://help-static-aliyun-doc.aliyuncs.com/file-manage-files/zh-CN/20260415/hynnff/wan-video-edit-clothes.webp"
                      }
                    ]
                  },
                  "parameters": {
                    "resolution": "720P",
                    "audio_setting": "origin"
                  }
                }
                """);
    }

    @Test
    void pixverseTextToVideoUsesOfficialRequestShape() throws Exception {
        assertSubmittedRequestJson(DashScopeVideoOptions.builder()
                .model(DashScopeModel.VideoModel.PIXVERSE_PIXVERSE_C1_T2V.getName())
                .input(InputOptions.builder()
                        .prompt("下着雨，赛博城市里，一只浣熊在栏杆上行走。突然他眼睛发出蓝光，变身成一架高科技无人机，快速飞离画面。")
                        .build())
                .parameters(ParametersOptions.builder().size("1280*720").duration(5).watermark(true).build())
                .build(), """
                {
                  "model": "pixverse/pixverse-c1-t2v",
                  "input": {
                    "prompt": "下着雨，赛博城市里，一只浣熊在栏杆上行走。突然他眼睛发出蓝光，变身成一架高科技无人机，快速飞离画面。"
                  },
                  "parameters": {
                    "size": "1280*720",
                    "duration": 5,
                    "watermark": true
                  }
                }
                """);
    }

    @Test
    void pixverseImageToVideoUsesImageUrlMediaRequestShape() throws Exception {
        assertSubmittedRequestJson(DashScopeVideoOptions.builder()
                .model(DashScopeModel.VideoModel.PIXVERSE_PIXVERSE_C1_IT2V.getName())
                .input(InputOptions.builder()
                        .media(List.of(media("image_url",
                                "https://help-static-aliyun-doc.aliyuncs.com/file-manage-files/zh-CN/20260121/zlpocv/wan-i2v-haigui.webp")))
                        .prompt("镜头从海龟下方缓缓上移，海龟悠然游动，腹部细节清晰可见。")
                        .build())
                .parameters(ParametersOptions.builder()
                        .resolution("720P")
                        .duration(5)
                        .audio(false)
                        .watermark(true)
                        .build())
                .build(), """
                {
                  "model": "pixverse/pixverse-c1-it2v",
                  "input": {
                    "media": [
                      {
                        "type": "image_url",
                        "url": "https://help-static-aliyun-doc.aliyuncs.com/file-manage-files/zh-CN/20260121/zlpocv/wan-i2v-haigui.webp"
                      }
                    ],
                    "prompt": "镜头从海龟下方缓缓上移，海龟悠然游动，腹部细节清晰可见。"
                  },
                  "parameters": {
                    "resolution": "720P",
                    "duration": 5,
                    "audio": false,
                    "watermark": true
                  }
                }
                """);
    }

    @Test
    void pixverseKeyframeToVideoUsesFirstAndLastFrameMediaRequestShape() throws Exception {
        assertSubmittedRequestJson(DashScopeVideoOptions.builder()
                .model(DashScopeModel.VideoModel.PIXVERSE_PIXVERSE_C1_KF2V.getName())
                .input(InputOptions.builder()
                        .media(List.of(media("first_frame", "https://wanx.alicdn.com/material/20250318/first_frame.png"),
                                media("last_frame", "https://wanx.alicdn.com/material/20250318/last_frame.png")))
                        .prompt("一只小猫从窗台向下跳跃，轻盈地落在沙发上，然后好奇地环顾四周。")
                        .build())
                .parameters(ParametersOptions.builder().resolution("720P").duration(5).watermark(true).build())
                .build(), """
                {
                  "model": "pixverse/pixverse-c1-kf2v",
                  "input": {
                    "media": [
                      {
                        "type": "first_frame",
                        "url": "https://wanx.alicdn.com/material/20250318/first_frame.png"
                      },
                      {
                        "type": "last_frame",
                        "url": "https://wanx.alicdn.com/material/20250318/last_frame.png"
                      }
                    ],
                    "prompt": "一只小猫从窗台向下跳跃，轻盈地落在沙发上，然后好奇地环顾四周。"
                  },
                  "parameters": {
                    "resolution": "720P",
                    "duration": 5,
                    "watermark": true
                  }
                }
                """);
    }

    @Test
    void pixverseReferenceToVideoUsesOfficialRequestShape() throws Exception {
        assertSubmittedRequestJson(DashScopeVideoOptions.builder()
                .model(DashScopeModel.VideoModel.PIXVERSE_PIXVERSE_C1_R2V.getName())
                .input(InputOptions.builder()
                        .media(List.of(
                                media("image_url",
                                        "https://help-static-aliyun-doc.aliyuncs.com/file-manage-files/zh-CN/20260320/knsple/wan-r2v-role-frame.jpg",
                                        "character"),
                                media("image_url",
                                        "https://help-static-aliyun-doc.aliyuncs.com/file-manage-files/zh-CN/20260129/qpzxps/wan-r2v-object4.png",
                                        "prop"),
                                media("image_url",
                                        "https://help-static-aliyun-doc.aliyuncs.com/file-manage-files/zh-CN/20260129/wfjikw/wan-r2v-backgroud5.png",
                                        "background")))
                        .prompt("男人坐在靠窗的椅子上，手持吉他，在咖啡厅旁演奏一首舒缓的美国乡村民谣")
                        .build())
                .parameters(ParametersOptions.builder()
                        .size("1280*720")
                        .duration(5)
                        .audio(false)
                        .watermark(true)
                        .build())
                .build(), """
                {
                  "model": "pixverse/pixverse-c1-r2v",
                  "input": {
                    "media": [
                      {
                        "type": "image_url",
                        "url": "https://help-static-aliyun-doc.aliyuncs.com/file-manage-files/zh-CN/20260320/knsple/wan-r2v-role-frame.jpg",
                        "ref_name": "character"
                      },
                      {
                        "type": "image_url",
                        "url": "https://help-static-aliyun-doc.aliyuncs.com/file-manage-files/zh-CN/20260129/qpzxps/wan-r2v-object4.png",
                        "ref_name": "prop"
                      },
                      {
                        "type": "image_url",
                        "url": "https://help-static-aliyun-doc.aliyuncs.com/file-manage-files/zh-CN/20260129/wfjikw/wan-r2v-backgroud5.png",
                        "ref_name": "background"
                      }
                    ],
                    "prompt": "男人坐在靠窗的椅子上，手持吉他，在咖啡厅旁演奏一首舒缓的美国乡村民谣"
                  },
                  "parameters": {
                    "size": "1280*720",
                    "duration": 5,
                    "audio": false,
                    "watermark": true
                  }
                }
                """);
    }

    @Test
    void klingVideoGenerationUsesOfficialRequestShape() throws Exception {
        assertSubmittedRequestJson(DashScopeVideoOptions.builder()
                .model(DashScopeModel.VideoModel.KLING_V3_VIDEO_GENERATION.getName())
                .input(InputOptions.builder().prompt("一只小猫在月光下奔跑").build())
                .parameters(ParametersOptions.builder()
                        .mode("std")
                        .aspectRatio("16:9")
                        .duration(5)
                        .audio(false)
                        .watermark(true)
                        .build())
                .build(), """
                {
                  "model": "kling/kling-v3-video-generation",
                  "input": {
                    "prompt": "一只小猫在月光下奔跑"
                  },
                  "parameters": {
                    "mode": "std",
                    "aspect_ratio": "16:9",
                    "duration": 5,
                    "audio": false,
                    "watermark": true
                  }
                }
                """);
    }

    @Test
    void viduTextToVideoUsesOfficialRequestShape() throws Exception {
        assertSubmittedRequestJson(DashScopeVideoOptions.builder()
                .model(DashScopeModel.VideoModel.VIDUG3_TURBO_TEXT2VIDEO.getName())
                .input(InputOptions.builder().prompt("一只小猫在月光下奔跑").build())
                .parameters(ParametersOptions.builder()
                        .size("1024*576")
                        .resolution("540P")
                        .duration(5)
                        .watermark(true)
                        .build())
                .build(), """
                {
                  "model": "vidu/viduq3-turbo_text2video",
                  "input": {
                    "prompt": "一只小猫在月光下奔跑"
                  },
                  "parameters": {
                    "size": "1024*576",
                    "resolution": "540P",
                    "duration": 5,
                    "watermark": true
                  }
                }
                """);
    }

    @Test
    void viduImageToVideoUsesImageMediaRequestShape() throws Exception {
        assertSubmittedRequestJson(DashScopeVideoOptions.builder()
                .model(DashScopeModel.VideoModel.VIDUG3_PRO_IMG2VIDEO.getName())
                .input(InputOptions.builder()
                        .media(List.of(media("image",
                                "https://help-static-aliyun-doc.aliyuncs.com/file-manage-files/zh-CN/20260121/zlpocv/wan-i2v-haigui.webp")))
                        .prompt("镜头从海龟下方缓缓上移，海龟悠然游动，腹部细节清晰可见。")
                        .build())
                .parameters(ParametersOptions.builder().duration(5).resolution("720P").watermark(true).build())
                .build(), """
                {
                  "model": "vidu/viduq3-pro_img2video",
                  "input": {
                    "media": [
                      {
                        "type": "image",
                        "url": "https://help-static-aliyun-doc.aliyuncs.com/file-manage-files/zh-CN/20260121/zlpocv/wan-i2v-haigui.webp"
                      }
                    ],
                    "prompt": "镜头从海龟下方缓缓上移，海龟悠然游动，腹部细节清晰可见。"
                  },
                  "parameters": {
                    "duration": 5,
                    "resolution": "720P",
                    "watermark": true
                  }
                }
                """);
    }

    @Test
    void viduStartEndToVideoUsesOrderedImageMediaRequestShape() throws Exception {
        assertSubmittedRequestJson(DashScopeVideoOptions.builder()
                .model(DashScopeModel.VideoModel.VIDUG3_TURBO_START_END2VIDEO.getName())
                .input(InputOptions.builder()
                        .media(List.of(media("image", "https://wanx.alicdn.com/material/20250318/first_frame.png"),
                                media("image", "https://wanx.alicdn.com/material/20250318/last_frame.png")))
                        .prompt("一只小猫从窗台向下跳跃，轻盈地落在沙发上，然后好奇地环顾四周。")
                        .build())
                .parameters(ParametersOptions.builder().resolution("540P").duration(5).watermark(true).build())
                .build(), """
                {
                  "model": "vidu/viduq3-turbo_start-end2video",
                  "input": {
                    "media": [
                      {
                        "type": "image",
                        "url": "https://wanx.alicdn.com/material/20250318/first_frame.png"
                      },
                      {
                        "type": "image",
                        "url": "https://wanx.alicdn.com/material/20250318/last_frame.png"
                      }
                    ],
                    "prompt": "一只小猫从窗台向下跳跃，轻盈地落在沙发上，然后好奇地环顾四周。"
                  },
                  "parameters": {
                    "resolution": "540P",
                    "duration": 5,
                    "watermark": true
                  }
                }
                """);
    }

    @Test
    void viduReferenceToVideoUsesOfficialRequestShape() throws Exception {
        assertSubmittedRequestJson(DashScopeVideoOptions.builder()
                .model(DashScopeModel.VideoModel.VIDUG3_MIX_REFERENCE2VIDEO.getName())
                .input(InputOptions.builder()
                        .media(List.of(
                                media("image",
                                        "https://help-static-aliyun-doc.aliyuncs.com/file-manage-files/zh-CN/20260320/knsple/wan-r2v-role-frame.jpg"),
                                media("image",
                                        "https://help-static-aliyun-doc.aliyuncs.com/file-manage-files/zh-CN/20260129/qpzxps/wan-r2v-object4.png"),
                                media("image",
                                        "https://help-static-aliyun-doc.aliyuncs.com/file-manage-files/zh-CN/20260129/wfjikw/wan-r2v-backgroud5.png")))
                        .prompt("男人坐在靠窗的椅子上，手持吉他，在咖啡厅旁演奏一首舒缓的美国乡村民谣")
                        .build())
                .parameters(ParametersOptions.builder()
                        .duration(5)
                        .size("1280*720")
                        .resolution("720P")
                        .watermark(true)
                        .build())
                .build(), """
                {
                  "model": "vidu/viduq3-mix_reference2video",
                  "input": {
                    "media": [
                      {
                        "type": "image",
                        "url": "https://help-static-aliyun-doc.aliyuncs.com/file-manage-files/zh-CN/20260320/knsple/wan-r2v-role-frame.jpg"
                      },
                      {
                        "type": "image",
                        "url": "https://help-static-aliyun-doc.aliyuncs.com/file-manage-files/zh-CN/20260129/qpzxps/wan-r2v-object4.png"
                      },
                      {
                        "type": "image",
                        "url": "https://help-static-aliyun-doc.aliyuncs.com/file-manage-files/zh-CN/20260129/wfjikw/wan-r2v-backgroud5.png"
                      }
                    ],
                    "prompt": "男人坐在靠窗的椅子上，手持吉他，在咖啡厅旁演奏一首舒缓的美国乡村民谣"
                  },
                  "parameters": {
                    "duration": 5,
                    "size": "1280*720",
                    "resolution": "720P",
                    "watermark": true
                  }
                }
                """);
    }

    @Test
    void testVideoGenerationWithInputMediaAndMultiShotFields() {
        mockSuccessfulVideoGeneration();

        DashScopeVideoOptions multiShotOptions = DashScopeVideoOptions.builder()
                .model(TEST_MODEL)
                .input(InputOptions.builder()
                        .prompt(TEST_PROMPT)
                        .media(List.of(InputOptions.Media.builder()
                                .type("base")
                                .url("https://example.com/source.mp4")
                                .keepOriginalSound("yes")
                                .build()))
                        .multiShot(true)
                        .shotType("customize")
                        .multiPrompt(List.of(InputOptions.MultiPrompt.builder()
                                .index(1)
                                .prompt("第一个镜头")
                                .duration(5)
                                .build()))
                        .elementList(List.of(InputOptions.Element.builder().elementId(171).build()))
                        .build())
                .parameters(ParametersOptions.builder().duration(5).build())
                .build();

        videoModel.call(VideoPrompt.builder().options(multiShotOptions).build());

        ArgumentCaptor<DashScopeVideoRequest> requestCaptor = ArgumentCaptor.forClass(DashScopeVideoRequest.class);
        verify(dashScopeVideoApi).submitVideoGenTask(requestCaptor.capture());

        DashScopeVideoRequest.VideoInput input = requestCaptor.getValue().getInput();
        assertThat(input).isNotNull();
        assertJsonProperty(DashScopeVideoRequest.VideoInput.class, "media", "media");
        assertJsonProperty(DashScopeVideoRequest.VideoInput.class, "multiShot", "multi_shot");
        assertJsonProperty(DashScopeVideoRequest.VideoInput.class, "shotType", "shot_type");
        assertJsonProperty(DashScopeVideoRequest.VideoInput.class, "multiPrompt", "multi_prompt");
        assertJsonProperty(DashScopeVideoRequest.VideoInput.class, "elementList", "element_list");

        @SuppressWarnings("unchecked")
        List<Object> media = (List<Object>) ReflectionTestUtils.getField(input, "media");
        assertThat(media).hasSize(1);
        Object videoMedia = media.get(0);
        assertJsonProperty(videoMedia.getClass(), "keepOriginalSound", "keep_original_sound");
        assertThat(ReflectionTestUtils.getField(videoMedia, "type")).isEqualTo("base");
        assertThat(ReflectionTestUtils.getField(videoMedia, "url")).isEqualTo("https://example.com/source.mp4");
        assertThat(ReflectionTestUtils.getField(videoMedia, "keepOriginalSound")).isEqualTo("yes");

        assertThat(ReflectionTestUtils.getField(input, "multiShot")).isEqualTo(true);
        assertThat(ReflectionTestUtils.getField(input, "shotType")).isEqualTo("customize");

        @SuppressWarnings("unchecked")
        List<Object> multiPrompt = (List<Object>) ReflectionTestUtils.getField(input, "multiPrompt");
        assertThat(multiPrompt).hasSize(1);
        Object videoMultiPrompt = multiPrompt.get(0);
        assertThat(ReflectionTestUtils.getField(videoMultiPrompt, "index")).isEqualTo(1);
        assertThat(ReflectionTestUtils.getField(videoMultiPrompt, "prompt")).isEqualTo("第一个镜头");
        assertThat(ReflectionTestUtils.getField(videoMultiPrompt, "duration")).isEqualTo(5);

        @SuppressWarnings("unchecked")
        List<Object> elementList = (List<Object>) ReflectionTestUtils.getField(input, "elementList");
        assertThat(elementList).hasSize(1);
        Object videoElement = elementList.get(0);
        assertJsonProperty(videoElement.getClass(), "elementId", "element_id");
        assertThat(ReflectionTestUtils.getField(videoElement, "elementId")).isEqualTo(171);

        DashScopeVideoRequest.VideoParameters parameters = requestCaptor.getValue().getParameters();
        assertThat(parameters).isNotNull();
        assertThat(org.springframework.util.ReflectionUtils.findField(DashScopeVideoRequest.VideoParameters.class,
                "media"))
                .isNull();
        assertThat(org.springframework.util.ReflectionUtils.findField(DashScopeVideoRequest.VideoParameters.class,
                "multiShot")).isNull();
        assertThat(org.springframework.util.ReflectionUtils.findField(DashScopeVideoRequest.VideoParameters.class,
                "multiPrompt")).isNull();
        assertThat(org.springframework.util.ReflectionUtils.findField(DashScopeVideoRequest.VideoParameters.class,
                "elementList")).isNull();
    }

    @Test
    void testMediaKeepOriginalSoundRejectsInvalidValue() {
        assertThatThrownBy(() -> InputOptions.Media.builder().keepOriginalSound("maybe"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("keep_original_sound must be \"yes\" or \"no\"");
    }

    @Test
    void happyHorseVideoEditUsageParsesFractionalDurations() throws Exception {
        DashScopeVideoResponse response = JSON_MAPPER.readValue("""
                {
                  "request_id": "test-request-id-789",
                  "output": {
                    "task_id": "test-task-id-123456",
                    "task_status": "SUCCEEDED",
                    "output_video_url": "https://example.com/generated-video.mp4"
                  },
                  "usage": {
                    "duration": 13.24,
                    "input_video_duration": 6.62,
                    "output_video_duration": 13.24,
                    "video_count": 1
                  }
                }
                """, DashScopeVideoResponse.class);

        assertThat(response.usage()).isNotNull();
        assertThat(response.usage().duration()).isEqualTo(13.24);
        assertThat(response.usage().inputVideoDuration()).isEqualTo(6.62);
        assertThat(response.usage().outputVideoDuration()).isEqualTo(13.24);
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
        VideoUsage detectionUsage = new VideoUsage(0.0, 0.0, 0.0, 0, 0, null, null, null, 1);
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
        VideoUsage usage = new VideoUsage(5.0, 0.0, 5.0, 1, 0, "832*480", "16:9", "5s", 0);
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

    private static void assertJsonProperty(Class<?> type, String fieldName, String expectedPropertyName) {
        var field = org.springframework.util.ReflectionUtils.findField(type, fieldName);
        assertThat(field).isNotNull();
        JsonProperty jsonProperty = field.getAnnotation(JsonProperty.class);
        assertThat(jsonProperty).isNotNull();
        assertThat(jsonProperty.value()).isEqualTo(expectedPropertyName);
    }

    private void assertSubmittedRequestJson(DashScopeVideoOptions options, String expectedJson) throws Exception {
        mockSuccessfulVideoGeneration();
        DashScopeVideoModel cleanVideoModel = new DashScopeVideoModel(dashScopeVideoApi,
                DashScopeVideoOptions.builder().build(), RetryUtils.DEFAULT_RETRY_TEMPLATE);

        cleanVideoModel.call(VideoPrompt.builder().options(options).build());

        ArgumentCaptor<DashScopeVideoRequest> requestCaptor = ArgumentCaptor.forClass(DashScopeVideoRequest.class);
        verify(dashScopeVideoApi).submitVideoGenTask(requestCaptor.capture());
        String jsonRequest = JSON_MAPPER.writeValueAsString(requestCaptor.getValue());

        assertThat(JSON_MAPPER.readTree(jsonRequest)).isEqualTo(JSON_MAPPER.readTree(expectedJson));
        assertThat(jsonRequest).doesNotContainPattern("\"parameters\"\\s*:\\s*\\{[^}]*\"media\"");
    }

    private static InputOptions.Media media(String type, String url) {
        return InputOptions.Media.builder().type(type).url(url).build();
    }

    private static InputOptions.Media media(String type, String url, String refName) {
        return InputOptions.Media.builder().type(type).url(url).refName(refName).build();
    }

}
