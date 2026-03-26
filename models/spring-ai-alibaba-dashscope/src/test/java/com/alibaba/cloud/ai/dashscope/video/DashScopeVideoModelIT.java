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

import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import com.alibaba.cloud.ai.dashscope.api.DashScopeVideoApi;
import com.alibaba.cloud.ai.dashscope.spec.DashScopeModel;
import com.alibaba.cloud.ai.dashscope.video.DashScopeVideoOptions.InputOptions;
import com.alibaba.cloud.ai.dashscope.video.DashScopeVideoOptions.ParametersOptions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.ai.retry.RetryUtils;
import org.springframework.core.retry.RetryTemplate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for DashScope Video Model functionality. These tests will only run
 * if AI_DASHSCOPE_API_KEY environment variable is set.
 *
 * <p>
 * These tests make real API calls to DashScope video generation service and may take
 * several minutes to complete as video generation is an asynchronous process.
 * </p>
 *
 * @author yingzi
 * @since 1.1.0.0
 */
@Tag("integration")
@EnabledIfEnvironmentVariable(named = "AI_DASHSCOPE_API_KEY", matches = ".+")
class DashScopeVideoModelIT {

    // Test constants
    private static final String API_KEY_ENV = "AI_DASHSCOPE_API_KEY";

    private static final String VIDEO_OUTPUT_DIR = "src/test/resources/video";

    private String apiKey;

    private DashScopeVideoApi videoApi;

    private RetryTemplate retryTemplate;

    @BeforeEach
    void setUp() {
        // Get API key from environment variable
        apiKey = System.getenv(API_KEY_ENV);

        // Skip tests if API key is not set
        Assumptions.assumeTrue(
                apiKey != null && !apiKey.trim().isEmpty(),
                "Skipping tests because " + API_KEY_ENV + " environment variable is not set");

        // Create real API client (shared by all tests)
        videoApi = DashScopeVideoApi.builder().apiKey(apiKey).build();

        // Create retry template (shared by all tests)
        // Video generation may take longer, so increase retry attempts
        retryTemplate = RetryUtils.DEFAULT_RETRY_TEMPLATE;
    }

    /**
     * Test Scenario 1: Basic text-to-video generation (文生视频). This matches the curl
     * command: wan2.2-t2v-plus model with prompt-based video generation.
     */
    @Test
    void testBasicTextToVideoGeneration() throws Exception {
        System.out.println("=".repeat(80));
        System.out.println("Test Scenario 1: Text-to-Video Generation (文生视频)");
        System.out.println("=".repeat(80));

        String prompt = "低对比度，在一个复古的70年代风格地铁站里，街头音乐家在昏暗的色彩和粗糙的质感中演奏。"
                + "他穿着旧式夹克，手持吉他，专注地弹奏。通勤者匆匆走过，一小群人渐渐聚拢聆听。"
                + "镜头慢慢向右移动，捕捉到乐器声与城市喧嚣交织的场景，背景中有老式的地铁标志和斑驳的墙面。";

        // Build options matching curl command
        DashScopeVideoOptions options = DashScopeVideoOptions.builder()
                .model(DashScopeModel.VideoModel.WAN22_T2V_PLUS.getName())
                .input(InputOptions.builder().prompt(prompt).build())
                .parameters(ParametersOptions.builder().size("832*480").promptExtend(true).build())
                .build();

        // Create video model with options
        DashScopeVideoModel videoModel = DashScopeVideoModel.builder()
                .videoApi(videoApi)
                .defaultOptions(options)
                .retryTemplate(retryTemplate)
                .build();

        // Create prompt using builder pattern
        VideoPrompt videoPrompt = VideoPrompt.builder().options(options).build();

        System.out.println("Model: " + DashScopeModel.VideoModel.WAN22_T2V_PLUS.getName());
        System.out.println("Video Size: 832*480");
        System.out.println("Prompt Extend: true");
        System.out.println("\nSubmitting video generation task...");

        // Call API
        VideoResponse response = videoModel.call(videoPrompt);

        // Verify response
        assertThat(response).isNotNull();
        assertThat(response.getResult()).isNotNull();
        assertThat(response.getResult().getOutput().taskStatus()).isEqualTo("SUCCEEDED");
        assertThat(response.getResult().getOutput().videoUrl()).isNotEmpty();

        String videoUrl = response.getResult().getOutput().videoUrl();
        System.out.println("✓ Video generated successfully!");
        System.out.println("Video URL: " + videoUrl);

        // Download and save video
        downloadAndSaveVideo(videoUrl, "文生视频.mp4");

        System.out.println("=".repeat(80));
    }

    /**
     * Test Scenario 2: Keyframe-to-video generation (基于首尾帧). This matches the curl
     * command: wan2.2-kf2v-flash model with first/last frame URLs.
     */
    @Test
    void testKeyframeToVideoGeneration() throws Exception {
        System.out.println("=".repeat(80));
        System.out.println("Test Scenario 2: Keyframe-to-Video Generation (基于首尾帧)");
        System.out.println("=".repeat(80));

        String prompt = "写实风格，一只黑色小猫好奇地看向天空，镜头从平视逐渐上升，最后俯拍它的好奇的眼神。";
        String firstFrameUrl = "https://wanx.alicdn.com/material/20250318/first_frame.png";
        String lastFrameUrl = "https://wanx.alicdn.com/material/20250318/last_frame.png";

        // Build options matching curl command
        DashScopeVideoOptions options = DashScopeVideoOptions.builder()
                .model(DashScopeModel.VideoModel.WAN22_KF2V_FLASH.getName())
                .input(InputOptions.builder()
                        .firstFrameUrl(firstFrameUrl)
                        .lastFrameUrl(lastFrameUrl)
                        .prompt(prompt)
                        .build())
                .parameters(ParametersOptions.builder().resolution("480P").promptExtend(true).build())
                .build();

        // Create video model
        DashScopeVideoModel videoModel = DashScopeVideoModel.builder()
                .videoApi(videoApi)
                .defaultOptions(options)
                .retryTemplate(retryTemplate)
                .build();

        // Create prompt
        VideoPrompt videoPrompt = VideoPrompt.builder().options(options).build();

        System.out.println("Model: " + DashScopeModel.VideoModel.WAN22_KF2V_FLASH.getName());
        System.out.println("Resolution: 480P");
        System.out.println("First Frame: " + firstFrameUrl);
        System.out.println("Last Frame: " + lastFrameUrl);
        System.out.println("\nSubmitting keyframe-to-video generation task...");

        // Call API
        VideoResponse response = videoModel.call(videoPrompt);

        // Verify response
        assertThat(response).isNotNull();
        assertThat(response.getResult()).isNotNull();
        assertThat(response.getResult().getOutput().taskStatus()).isEqualTo("SUCCEEDED");
        assertThat(response.getResult().getOutput().videoUrl()).isNotEmpty();

        String videoUrl = response.getResult().getOutput().videoUrl();
        System.out.println("✓ Keyframe video generated successfully!");
        System.out.println("Video URL: " + videoUrl);

        // Download and save video
        downloadAndSaveVideo(videoUrl, "基于首尾帧.mp4");

        System.out.println("=".repeat(80));
    }

    /**
     * Test Scenario 3: Video character replacement (视频换人). This matches the curl
     * command: wan2.2-animate-mix model with image and video URLs.
     */
    @Test
    void testVideoCharacterReplacement() throws Exception {
        System.out.println("=".repeat(80));
        System.out.println("Test Scenario 3: Video Character Replacement (视频换人)");
        System.out.println("=".repeat(80));

        String imageUrl = "https://help-static-aliyun-doc.aliyuncs.com/file-manage-files/zh-CN/20250919/bhkfor/mix_input_image.jpeg";
        String videoUrl = "https://help-static-aliyun-doc.aliyuncs.com/file-manage-files/zh-CN/20250919/wqefue/mix_input_video.mp4";

        // Build options matching curl command
        DashScopeVideoOptions options = DashScopeVideoOptions.builder()
                .model(DashScopeModel.VideoModel.WAN22_ANIMATE_MIX.getName())
                .input(InputOptions.builder().imageUrl(imageUrl).videoUrl(videoUrl).build())
                .parameters(ParametersOptions.builder().mode("wan-std").build())
                .build();

        // Create video model
        DashScopeVideoModel videoModel = DashScopeVideoModel.builder()
                .videoApi(videoApi)
                .defaultOptions(options)
                .retryTemplate(retryTemplate)
                .build();

        // Create prompt
        VideoPrompt videoPrompt = VideoPrompt.builder().options(options).build();

        System.out.println("Model: " + DashScopeModel.VideoModel.WAN22_ANIMATE_MIX.getName());
        System.out.println("Mode: wan-std");
        System.out.println("Image URL: " + imageUrl);
        System.out.println("Video URL: " + videoUrl);
        System.out.println("\nSubmitting video character replacement task...");

        // Call API
        VideoResponse response = videoModel.call(videoPrompt);

        // Verify response
        assertThat(response).isNotNull();
        assertThat(response.getResult()).isNotNull();
        assertThat(response.getResult().getOutput().taskStatus()).isEqualTo("SUCCEEDED");
        assertThat(response.getResult().getOutput().results().videoUrl()).isNotEmpty();

        String generatedVideoUrl = response.getResult().getOutput().results().videoUrl();
        System.out.println("✓ Video character replacement completed successfully!");
        System.out.println("Video URL: " + generatedVideoUrl);

        // Download and save video
        downloadAndSaveVideo(generatedVideoUrl, "视频换人.mp4");

        System.out.println("=".repeat(80));
    }

    /**
     * Test Scenario 4: Emoji video generation (Emoji视频生成). This matches the curl
     * command: emoji-v1 model with face/ext bounding boxes.
     */
    @Test
    void testEmojiVideoGeneration() throws Exception {
        System.out.println("=".repeat(80));
        System.out.println("Test Scenario 4: Emoji Video Generation (Emoji视频生成)");
        System.out.println("=".repeat(80));

        String imageUrl = "https://help-static-aliyun-doc.aliyuncs.com/file-manage-files/zh-CN/20250912/uopnly/emoji-%E5%9B%BE%E5%83%8F%E6%A3%80%E6%B5%8B.png";
        String drivenId = "mengwa_kaixin";
        List<Integer> faceBbox = List.of(212, 194, 460, 441);
        List<Integer> extBbox = List.of(63, 30, 609, 575);

        // Build options matching curl command
        DashScopeVideoOptions options = DashScopeVideoOptions.builder()
                .model("emoji-v1").input(InputOptions.builder()
                        .imageUrl(imageUrl)
                        .drivenId(drivenId)
                        .faceBbox(faceBbox)
                        .extBbox(extBbox)
                        .build())
                .build();

        // Create video model
        DashScopeVideoModel videoModel = DashScopeVideoModel.builder()
                .videoApi(videoApi)
                .defaultOptions(options)
                .retryTemplate(retryTemplate)
                .build();

        // Create prompt
        VideoPrompt videoPrompt = VideoPrompt.builder().options(options).build();

        System.out.println("Model: emoji-v1");
        System.out.println("Driven ID: " + drivenId);
        System.out.println("Face BBox: " + faceBbox);
        System.out.println("Ext BBox: " + extBbox);
        System.out.println("Image URL: " + imageUrl);
        System.out.println("\nSubmitting emoji video generation task...");

        // Call API
        VideoResponse response = videoModel.call(videoPrompt);

        // Verify response
        assertThat(response).isNotNull();
        assertThat(response.getResult()).isNotNull();
        assertThat(response.getResult().getOutput().taskStatus()).isEqualTo("SUCCEEDED");
        assertThat(response.getResult().getOutput().videoUrl()).isNotEmpty();

        String generatedVideoUrl = response.getResult().getOutput().videoUrl();
        System.out.println("✓ Emoji video generated successfully!");
        System.out.println("Video URL: " + generatedVideoUrl);

        // Download and save video
        downloadAndSaveVideo(generatedVideoUrl, "Emoji视频生成.mp4");

        System.out.println("=".repeat(80));
    }

    /**
     * Test Scenario 5: Image detection (图像检测). This tests the detect functionality
     * with emoji-detect-v1 model that returns face bounding boxes directly.
     */
    @Test
    void testImageDetection() throws Exception {
        System.out.println("=".repeat(80));
        System.out.println("Test Scenario 5: Image Detection (图像检测)");
        System.out.println("=".repeat(80));

        String imageUrl = "https://help-static-aliyun-doc.aliyuncs.com/file-manage-files/zh-CN/20250912/uopnly/emoji-%E5%9B%BE%E5%83%8F%E6%A3%80%E6%B5%8B.png";
        String ratio = "1:1";

        // Build options matching curl command
        DashScopeVideoOptions options = DashScopeVideoOptions.builder()
                .model(DashScopeModel.VideoModel.EMOJI_DETECT_V1.getName())
                .input(InputOptions.builder().imageUrl(imageUrl).build())
                .parameters(ParametersOptions.builder().ratio(ratio).build())
                .build();

        // Create video model
        DashScopeVideoModel videoModel = DashScopeVideoModel.builder()
                .videoApi(videoApi)
                .defaultOptions(options)
                .retryTemplate(retryTemplate)
                .build();

        // Create prompt
        VideoPrompt videoPrompt = VideoPrompt.builder().options(options).build();

        System.out.println("Model: emoji-detect-v1");
        System.out.println("Ratio: " + ratio);
        System.out.println("Image URL: " + imageUrl);
        System.out.println("\nSubmitting image detection task...");

        // Call API - detection is synchronous, returns immediately
        VideoResponse response = videoModel.call(videoPrompt);

        // Verify response
        assertThat(response).isNotNull();
        assertThat(response.getResult()).isNotNull();

        // Print the complete response
        System.out.println("\n========== Response Details ==========");
        System.out.println("Request ID: " + response.getResult().requestId());
        System.out.println("Output: " + response.getResult().getOutput());
        System.out.println("Usage: " + response.getResult().usage());
        System.out.println("======================================\n");

        // Verify detection results
        assertThat(response.getResult().getOutput().bboxFace()).isNotNull();
        assertThat(response.getResult().getOutput().extBboxFace()).isNotNull();
        assertThat(response.getResult().usage().imageCount()).isEqualTo(1);
        assertThat(response.getResult().requestId()).isNotEmpty();

        // Print bounding boxes
        System.out.println("✓ Image detection completed successfully!");
        System.out.println("Face BBox: " + response.getResult().getOutput().bboxFace());
        System.out.println("Ext BBox: " + response.getResult().getOutput().extBboxFace());
        System.out.println("Image Count: " + response.getResult().usage().imageCount());
        System.out.println("Request ID: " + response.getResult().requestId());

        System.out.println("=".repeat(80));
    }

    /**
     * Utility method to download and save video from URL.
     *
     * @param videoUrl the video URL
     * @param fileName the output file name
     *
     * @throws Exception if download fails
     */
    private void downloadAndSaveVideo(String videoUrl, String fileName) throws Exception {
        System.out.println("\nDownloading video from: " + videoUrl);

        URI uri = new URI(videoUrl);
        Path outputPath = Paths.get(VIDEO_OUTPUT_DIR, fileName);

        // Create directory if it doesn't exist
        Files.createDirectories(outputPath.getParent());

        // Download and save the video
        try (InputStream in = uri.toURL().openStream()) {
            Files.copy(in, outputPath, StandardCopyOption.REPLACE_EXISTING);
        }

        System.out.println("✓ Video saved to: " + outputPath);
        System.out.println("File size: " + Files.size(outputPath) + " bytes");

        // Verify file exists and has content
        assertThat(Files.exists(outputPath)).isTrue();
        assertThat(Files.size(outputPath)).isGreaterThan(0);
    }

}
