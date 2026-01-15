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

import com.alibaba.cloud.ai.dashscope.api.DashScopeAudioSpeechApi;
import com.alibaba.cloud.ai.dashscope.common.DashScopeException;
import com.alibaba.cloud.ai.dashscope.protocol.DashScopeWebSocketClient;
import com.alibaba.cloud.ai.dashscope.spec.DashScopeModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.audio.tts.Speech;
import org.springframework.ai.audio.tts.TextToSpeechModel;
import org.springframework.ai.audio.tts.TextToSpeechOptions;
import org.springframework.ai.audio.tts.TextToSpeechPrompt;
import org.springframework.ai.audio.tts.TextToSpeechResponse;
import org.springframework.ai.model.ModelOptionsUtils;
import org.springframework.ai.retry.RetryUtils;
import org.springframework.retry.support.RetryTemplate;
import reactor.core.publisher.Flux;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Audio Speech: input text, output audio.
 *
 * @author kevinlin09
 * @author xuguan
 */
public class DashScopeAudioSpeechModel implements TextToSpeechModel {

	private static final Logger logger = LoggerFactory.getLogger(DashScopeAudioSpeechModel.class);

	private final DashScopeAudioSpeechApi audioSpeechApi;

	private final DashScopeAudioSpeechOptions defaultOptions;

	private final RetryTemplate retryTemplate;

	public DashScopeAudioSpeechModel(DashScopeAudioSpeechApi audioSpeechApi) {
		this(audioSpeechApi, DashScopeAudioSpeechOptions.builder()
			.model(DashScopeModel.AudioModel.COSYVOICE_V1.getValue())
			.voice("longhua")
			.speed(1.0)
			.responseFormat(DashScopeAudioSpeechApi.ResponseFormat.MP3)
			.build());
	}

	public DashScopeAudioSpeechModel(DashScopeAudioSpeechApi audioSpeechApi, DashScopeAudioSpeechOptions defaultOptions) {
		this(audioSpeechApi, defaultOptions, RetryUtils.DEFAULT_RETRY_TEMPLATE);
	}

	public DashScopeAudioSpeechModel(DashScopeAudioSpeechApi audioSpeechApi, DashScopeAudioSpeechOptions defaultOptions,
		RetryTemplate retryTemplate) {
		this.audioSpeechApi = audioSpeechApi;
		this.defaultOptions = defaultOptions;
		this.retryTemplate = retryTemplate;
	}

	@Override
	public TextToSpeechResponse call(TextToSpeechPrompt prompt) {
        String taskId = UUID.randomUUID().toString();
        DashScopeAudioSpeechApi.Request runTaskRequest = this.createRequest(prompt, taskId,
                DashScopeWebSocketClient.EventType.RUN_TASK);

        logger.info("send run-task");
        return this.retryTemplate.execute(ctx -> this.audioSpeechApi.streamBinaryOut(runTaskRequest)
                .collectList()
                .map(byteBuffers -> {
                    // combine all byte buffers
                    ByteBuffer combined = ByteBuffer.allocate(byteBuffers.stream()
                            .mapToInt(ByteBuffer::remaining)
                            .sum());

                    for (ByteBuffer byteBuffer : byteBuffers) {
                        combined.put(byteBuffer);
                    }

                    combined.flip();

                    byte[] data = new byte[combined.remaining()];
                    combined.get(data);
                    return new TextToSpeechResponse(List.of(new Speech(data)));
                })
                .block());
	}

	@Override
	public Flux<TextToSpeechResponse> stream(TextToSpeechPrompt prompt) {
		String taskId = UUID.randomUUID().toString();

        // Ensure WebSocket connection is established before sending run-task
        logger.info("Ensuring WebSocket connection is ready, taskId={}", taskId);
        try {
            this.audioSpeechApi.ensureWebSocketConnectionReady(10, TimeUnit.SECONDS);
        } catch (DashScopeException e) {
            logger.error("Failed to establish WebSocket connection", e);
            return Flux.error(e);
        }

		DashScopeAudioSpeechApi.Request runTaskRequest = this.createRequest(prompt, taskId,
			DashScopeWebSocketClient.EventType.RUN_TASK);

		logger.info("send run-task");
		return this.retryTemplate.execute(ctx -> this.audioSpeechApi.streamBinaryOut(runTaskRequest)
			.map(byteBuffer -> {
				byte[] data = new byte[byteBuffer.remaining()];
				byteBuffer.get(data);
				return new TextToSpeechResponse(List.of(new Speech(data)));
			}));
	}

	public DashScopeAudioSpeechApi.Request createRequest(TextToSpeechPrompt prompt,
		String taskId, DashScopeWebSocketClient.EventType action) {
		DashScopeAudioSpeechOptions options = this.mergeOptions(prompt);

		return new DashScopeAudioSpeechApi.Request(
			new DashScopeAudioSpeechApi.Request.RequestHeader(action, taskId, "out"),
			new DashScopeAudioSpeechApi.Request.RequestPayload(options.getModel(),
				"audio", "tts", "SpeechSynthesizer",
				new DashScopeAudioSpeechApi.Request.RequestPayload.RequestPayloadInput(prompt.getInstructions().getText()),
				new DashScopeAudioSpeechApi.Request.RequestPayload.RequestPayloadParameters(
					options.getVolume(), options.getRequestTextType(), options.getVoice(), options.getSampleRate(),
					options.getSpeed(), options.getResponseFormat(), options.getPitch(), options.getEnableSsml(),
					options.getBitRate(), options.getSeed(), options.getLanguageHints(), options.getInstruction(),
					options.getEnablePhonemeTimestamp(), options.getEnableWordTimestamp())));
	}

	private DashScopeAudioSpeechOptions mergeOptions(TextToSpeechPrompt prompt) {
		DashScopeAudioSpeechOptions options = DashScopeAudioSpeechOptions.builder().build();
		if (prompt.getOptions() != null) {
			DashScopeAudioSpeechOptions runtimeOptions = ModelOptionsUtils.copyToTarget(prompt.getOptions(),
				TextToSpeechOptions.class, DashScopeAudioSpeechOptions.class);

			options = ModelOptionsUtils.merge(runtimeOptions, options, DashScopeAudioSpeechOptions.class);
		}

		return ModelOptionsUtils.merge(options, this.defaultOptions, DashScopeAudioSpeechOptions.class);
	}

}
