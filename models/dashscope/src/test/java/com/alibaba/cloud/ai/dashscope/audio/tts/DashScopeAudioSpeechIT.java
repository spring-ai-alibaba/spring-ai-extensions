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
package com.alibaba.cloud.ai.dashscope.audio.tts;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import com.alibaba.cloud.ai.dashscope.api.DashScopeAudioSpeechApi;
import com.alibaba.cloud.ai.dashscope.spec.DashScopeModel.AudioModel;
import com.alibaba.cloud.ai.util.AudioUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.condition.EnabledIf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.audio.tts.Speech;
import org.springframework.ai.audio.tts.TextToSpeechPrompt;
import org.springframework.ai.audio.tts.TextToSpeechResponse;
import org.springframework.ai.model.SimpleApiKey;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for DashScopeAudioSpeechModel using real API calls.
 * These tests require a valid DASHSCOPE_API_KEY environment variable.
 *
 * <p>Test data is based on official DashScope documentation examples.</p>
 *
 * @author yingzi
 * @since 1.1
 */
@EnabledIf("isApiKeySet")
class DashScopeAudioSpeechIT {

	private static final Logger logger = LoggerFactory.getLogger(DashScopeAudioSpeechIT.class);

	private static final String API_KEY_ENV = "DASHSCOPE_API_KEY";

	private static final String BASE_URL = "https://dashscope.aliyuncs.com";

	private static final String TEST_TEXT = "那我来给大家推荐一款T恤，这款呢真的是超级好看";

	private String apiKey;

	private DashScopeAudioSpeechModel speechModel;

	static boolean isApiKeySet() {
		return System.getenv(API_KEY_ENV) != null && !System.getenv(API_KEY_ENV).isEmpty();
	}

	@BeforeEach
	void setUp() {
		apiKey = System.getenv(API_KEY_ENV);
		if (apiKey == null || apiKey.isEmpty()) {
			logger.warn("{} is not set, skipping integration tests", API_KEY_ENV);
			return;
		}

		DashScopeAudioSpeechApi speechApi =
				DashScopeAudioSpeechApi.builder()
						.apiKey(new SimpleApiKey(apiKey))
						.build();
		// Build model with default options
		DashScopeAudioSpeechOptions defaultOptions = DashScopeAudioSpeechOptions.builder().build();

		speechModel = DashScopeAudioSpeechModel.builder()
			.audioSpeechApi(speechApi)
			.defaultOptions(defaultOptions)
			.build();
	}

	// ==================== CosyVoice Model Tests ====================
	// 备注：改功能暂未通过，报错：Invalid payload data，待和百炼官方文档确认后再启用
//	@org.junit.jupiter.api.Test
//	void testCosyVoice_Stream_RealApi() {
//		// Arrange
//		DashScopeAudioSpeechOptions options = DashScopeAudioSpeechOptions.builder()
//			.model(AudioModel.COSYVOICE_V3_FLASH.getValue())
//				.textType("PlainText")
//				.voice("longanyang")
//				.format("mp3")
//				.sampleRate(22050)
//				.volume(50)
//				.rate(1f)
//				.pitch(1f)
//			.build();
//
//		// Act
//		TextToSpeechPrompt prompt = new TextToSpeechPrompt(TEST_TEXT, options);
//		Flux<TextToDashScopeAudioTTSResponse> result = speechModel.stream(prompt);
//
//		List<byte[]> speechChunks = new ArrayList<>();
//
//		// Assert - 使用 consumeWhileWith 来处理流中的所有元素
//		StepVerifier.create(result)
//				.thenConsumeWhile(response -> {
//					assertThat(response).isNotNull();
//					Speech speech = response.getResult();
//					speechChunks.add(speech.getOutput());
//					return true; // 继续消费更多元素
//				})
//				.verifyComplete();
//
//		// 保存合并后的音频文件
//		if (!speechChunks.isEmpty()) {
//			String outputPath = "src/test/resources/audio/websocket/cosyvoice-stream-test.mp3";
//			try {
//				AudioUtils.saveAudioFromByteChunks(speechChunks, outputPath);
//				logger.info("CosyVoice stream test passed successfully, audio saved to: {}",
//						Paths.get(outputPath).toAbsolutePath());
//			}
//			catch (IOException e) {
//				logger.error("Failed to save audio: {}", e.getMessage());
//			}
//		}
//		else {
//			logger.warn("No audio chunks received");
//		}
//	}

	@org.junit.jupiter.api.Test
	void testSambert_Stream_RealApi() {
		// Arrange
		DashScopeAudioSpeechOptions options = DashScopeAudioSpeechOptions.builder()
			.model(AudioModel.SAMBERT_ZHICHU_V1.getValue())
				.textType("PlainText")
				.format("mp3")
				.sampleRate(16000)
				.volume(50)
				.rate(1f)
				.pitch(1f)
				.wordTimestampEnabled(true)
				.phonemeTimestampEnabled(true)
			.build();

		// Act
		TextToSpeechPrompt prompt = new TextToSpeechPrompt(TEST_TEXT, options);
		Flux<TextToSpeechResponse> result = speechModel.stream(prompt);
		List<byte[]> speechChunks = new ArrayList<>();

		// Assert - 使用 consumeWhileWith 来处理流中的所有元素
		StepVerifier.create(result)
				.thenConsumeWhile(response -> {
					assertThat(response).isNotNull();
					Speech speech = response.getResult();
					speechChunks.add(speech.getOutput());
					return true; // 继续消费更多元素
				})
				.verifyComplete();

		// 保存合并后的音频文件
		if (!speechChunks.isEmpty()) {
			String outputPath = "src/test/resources/audio/websocket/sambert-stream-test.mp3";
			try {
				AudioUtils.saveAudioFromByteChunks(speechChunks, outputPath);
				logger.info("sambert stream test passed successfully, audio saved to: {}",
						Paths.get(outputPath).toAbsolutePath());
			}
			catch (IOException e) {
				logger.error("Failed to save audio: {}", e.getMessage());
			}
		}
		else {
			logger.warn("No audio chunks received");
		}
	}

	// ==================== Qwen-TTS Model Tests ====================
	@org.junit.jupiter.api.Test
	void testQwen3TtsFlash_Call_RealApi() {
		// Arrange - Using complete text from official documentation
		DashScopeAudioSpeechOptions options = DashScopeAudioSpeechOptions.builder()
			.model(AudioModel.QWEN3_TTS_FLASH.getValue())
			.voice("Cherry")
			.languageType("Chinese")
			.build();

		// Act
		TextToSpeechPrompt prompt = new TextToSpeechPrompt(TEST_TEXT, options);
		TextToSpeechResponse response = speechModel.call(prompt);

		// Assert
		assertThat(response).isNotNull();
		assertThat(response).isInstanceOf(DashScopeTTSApiSpec.DashScopeAudioTTSResponse.class);

		DashScopeTTSApiSpec.DashScopeAudioTTSResponse dashScopeResponse = (DashScopeTTSApiSpec.DashScopeAudioTTSResponse) response;
		assertThat(dashScopeResponse.getResult()).isNotNull();
		assertThat(dashScopeResponse.getRequestId()).isNotEmpty();
		assertThat(dashScopeResponse.getOutput().audio()).isNotNull();
		assertThat(dashScopeResponse.getOutput().audio().url()).isNotEmpty();

		logger.info("Qwen3-tts-flash call test passed");
		logger.info("Audio URL: {}", dashScopeResponse.getOutput().audio().url());

		// 保存url音频文件
		String audioUrl = dashScopeResponse.getOutput().audio().url();
		String outputPath = "src/test/resources/audio/qwen-tts/call-url-test.wav";
		try {
			AudioUtils.saveAudioFromUrl(audioUrl, outputPath);
			logger.info("Audio saved from URL to: {}", Paths.get(outputPath).toAbsolutePath());
		}
		catch (IOException e) {
			logger.error("Failed to save audio from URL: {}", e.getMessage());
		}
	}

	@org.junit.jupiter.api.Test
	void testQwen3TtsFlash_Stream_RealApi() {
		// Arrange - 使用官方文档中的 SSE curl 示例数据
		DashScopeAudioSpeechOptions options = DashScopeAudioSpeechOptions.builder()
				.model(AudioModel.QWEN3_TTS_FLASH.getValue())
				.voice("Cherry")
				.languageType("Chinese")
				.build();

		// 用于收集 Base64 音频数据块
		List<String> base64Chunks = new ArrayList<>();
		AtomicReference<String> audioUrlAtomic = new AtomicReference<>("");

		// Act
		TextToSpeechPrompt prompt = new TextToSpeechPrompt(TEST_TEXT, options);
		Flux<TextToSpeechResponse> result = speechModel.stream(prompt);

		// Assert - 使用 consumeWhileWith 来处理流中的所有元素
		StepVerifier.create(result)
				.thenConsumeWhile(response -> {
					assertThat(response).isNotNull();
					assertThat(response).isInstanceOf(DashScopeTTSApiSpec.DashScopeAudioTTSResponse.class);

					DashScopeTTSApiSpec.DashScopeAudioTTSResponse r = (DashScopeTTSApiSpec.DashScopeAudioTTSResponse) response;
					assertThat(r.getOutput()).isNotNull();
					assertThat(r.getRequestId()).isNotEmpty();

					// 记录音频数据信息并收集 Base64 数据
					DashScopeTTSApiSpec.DashScopeAudioTTSResponse.TTSAudio audio = r.getOutput().audio();
					if (audio != null) {
						logger.info("Audio data received:");
						if (audio.data() != null && !audio.data().isEmpty()) {
							base64Chunks.add(audio.data());
							logger.info("  - Base64 data length: {} chars", audio.data().length());
						}
						if (audio.url() != null && !audio.url().isEmpty()) {
							logger.info("  - Audio URL: {}", audio.url());
							audioUrlAtomic.set(audio.url());
						}
					}

					return true; // 继续消费更多元素
				})
				.verifyComplete();

		// 保存base64音频文件
		if (!base64Chunks.isEmpty()) {
			String combinedBase64 = String.join("", base64Chunks);
			String outputPath = "src/test/resources/audio/qwen-tts/stream-binary-test.wav";
			try {
				AudioUtils.saveAudioFromBase64(combinedBase64, outputPath);
				logger.info("Audio saved to: {}", Paths.get(outputPath).toAbsolutePath());
				logger.info("Total Base64 chunks: {}", base64Chunks.size());
			}
			catch (IOException e) {
				logger.error("Failed to save audio: {}", e.getMessage());
			}
		}

		// 保存url音频文件
		String audioUrl = audioUrlAtomic.get();
		if (audioUrl != null && !audioUrl.isEmpty()) {
			String outputPath = "src/test/resources/audio/qwen-tts/stream-url-test.wav";
			try {
				AudioUtils.saveAudioFromUrl(audioUrl, outputPath);
				logger.info("Audio saved from URL to: {}", Paths.get(outputPath).toAbsolutePath());
			}
			catch (IOException e) {
				logger.error("Failed to save audio from URL: {}", e.getMessage());
			}
		}
		logger.info("Qwen3-tts-flash stream test passed");
	}

}
