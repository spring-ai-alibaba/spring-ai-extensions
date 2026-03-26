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

package com.alibaba.cloud.ai.dashscope.sdk.audio.tts;

import com.alibaba.cloud.ai.dashscope.sdk.common.DashScopeSdkException;
import com.alibaba.dashscope.audio.tts.SpeechSynthesisAudioFormat;
import com.alibaba.dashscope.audio.tts.SpeechSynthesisParam;
import com.alibaba.dashscope.audio.tts.SpeechSynthesisResult;
import com.alibaba.dashscope.audio.tts.SpeechSynthesisTextType;
import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;
import org.springframework.ai.audio.tts.Speech;
import org.springframework.ai.audio.tts.TextToSpeechModel;
import org.springframework.ai.audio.tts.TextToSpeechOptions;
import org.springframework.ai.audio.tts.TextToSpeechPrompt;
import org.springframework.ai.audio.tts.TextToSpeechResponse;
import org.springframework.ai.audio.tts.TextToSpeechResponseMetadata;
import org.springframework.ai.model.ModelOptionsUtils;
import org.springframework.ai.retry.RetryUtils;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@link TextToSpeechModel} implementation backed by DashScope Java SDK.
 */
public class DashScopeSdkAudioSpeechModel implements TextToSpeechModel {

	public static final String DEFAULT_MODEL_NAME = "sambert-zhichu-v1";

	private final DashScopeSdkSpeechSynthesisClient speechClient;

	private final DashScopeSdkAudioSpeechOptions defaultOptions;

	private final RetryTemplate retryTemplate;

	private final String apiKey;

	private final String workspaceId;

	private final Map<String, String> connectionHeaders;

	public DashScopeSdkAudioSpeechModel(DashScopeSdkSpeechSynthesisClient speechClient,
			DashScopeSdkAudioSpeechOptions defaultOptions, RetryTemplate retryTemplate, String apiKey,
			String workspaceId, Map<String, String> connectionHeaders) {
		this.speechClient = speechClient;
		this.defaultOptions = defaultOptions;
		this.retryTemplate = retryTemplate;
		this.apiKey = apiKey;
		this.workspaceId = workspaceId;
		this.connectionHeaders = connectionHeaders;
	}

	@Override
	public TextToSpeechResponse call(TextToSpeechPrompt prompt) {
		SpeechSynthesisParam request = createRequest(prompt, false);
		ByteBuffer data = this.retryTemplate.execute(ctx -> executeCall(request));
		byte[] bytes = toBytes(data);
		TextToSpeechResponseMetadata metadata = new TextToSpeechResponseMetadata();
		metadata.put("model", request.getModel());
		return new TextToSpeechResponse(List.of(new Speech(bytes)), metadata);
	}

	@Override
	public Flux<TextToSpeechResponse> stream(TextToSpeechPrompt prompt) {
		SpeechSynthesisParam request = createRequest(prompt, true);
		Flowable<SpeechSynthesisResult> streamResult = this.retryTemplate.execute(ctx -> executeStream(request));
		return flowableToFlux(streamResult)
			.map(this::toTextToSpeechResponse)
			.filter(response -> !CollectionUtils.isEmpty(response.getResults())
					&& response.getResult().getOutput().length > 0);
	}

	private SpeechSynthesisParam createRequest(TextToSpeechPrompt prompt, boolean stream) {
		DashScopeSdkAudioSpeechOptions options = mergeOptions(prompt.getOptions());

		SpeechSynthesisParam.SpeechSynthesisParamBuilder<?, ?> builder = SpeechSynthesisParam.builder()
			.model(options.getModel())
			.text(prompt.getInstructions().getText());

		if (StringUtils.hasText(options.getTextType())) {
			builder.textType(toTextType(options.getTextType()));
		}
		if (StringUtils.hasText(options.getFormat())) {
			builder.format(toAudioFormat(options.getFormat()));
		}
		if (options.getSampleRate() != null) {
			builder.sampleRate(options.getSampleRate());
		}
		if (options.getVolume() != null) {
			builder.volume(options.getVolume());
		}
		if (options.getRate() != null) {
			builder.rate(options.getRate());
		}
		if (options.getPitch() != null) {
			builder.pitch(options.getPitch());
		}
		if (options.getWordTimestampEnabled() != null) {
			builder.enableWordTimestamp(options.getWordTimestampEnabled());
		}
		if (options.getPhonemeTimestampEnabled() != null) {
			builder.enablePhonemeTimestamp(options.getPhonemeTimestampEnabled());
		}

		if (StringUtils.hasText(options.getVoice())) {
			builder.parameter("voice", options.getVoice());
		}
		if (options.getSpeed() != null) {
			builder.parameter("speed", options.getSpeed());
		}
		if (stream) {
			builder.parameter("stream", true);
		}

		if (StringUtils.hasText(this.apiKey)) {
			builder.apiKey(this.apiKey);
		}
		if (StringUtils.hasText(this.workspaceId)) {
			builder.workspace(this.workspaceId);
		}

		Map<String, Object> headers = mergeHeaders(options.getHttpHeaders());
		if (!CollectionUtils.isEmpty(headers)) {
			builder.headers(headers);
		}

		return builder.build();
	}

	private DashScopeSdkAudioSpeechOptions mergeOptions(TextToSpeechOptions runtimeOptions) {
		DashScopeSdkAudioSpeechOptions options = DashScopeSdkAudioSpeechOptions.fromOptions(this.defaultOptions);
		if (runtimeOptions == null) {
			return options;
		}
		DashScopeSdkAudioSpeechOptions runtime = ModelOptionsUtils.copyToTarget(runtimeOptions, TextToSpeechOptions.class,
				DashScopeSdkAudioSpeechOptions.class);
		DashScopeSdkAudioSpeechOptions merged = ModelOptionsUtils.merge(runtime, options,
				DashScopeSdkAudioSpeechOptions.class);
		if (runtime != null && !CollectionUtils.isEmpty(runtime.getHttpHeaders())) {
			merged.setHttpHeaders(runtime.getHttpHeaders());
		}
		else {
			merged.setHttpHeaders(this.defaultOptions.getHttpHeaders());
		}
		return merged;
	}

	private ByteBuffer executeCall(SpeechSynthesisParam request) {
		try {
			return this.speechClient.call(request);
		}
		catch (Exception ex) {
			throw new DashScopeSdkException("Failed to call DashScope SDK speech API", ex);
		}
	}

	private Flowable<SpeechSynthesisResult> executeStream(SpeechSynthesisParam request) {
		try {
			return this.speechClient.streamCall(request);
		}
		catch (Exception ex) {
			throw new DashScopeSdkException("Failed to stream DashScope SDK speech API", ex);
		}
	}

	private Flux<SpeechSynthesisResult> flowableToFlux(Flowable<SpeechSynthesisResult> flowable) {
		return Flux.create(sink -> {
			Disposable disposable = flowable.subscribe(sink::next, sink::error, sink::complete);
			sink.onDispose(disposable::dispose);
		});
	}

	private TextToSpeechResponse toTextToSpeechResponse(SpeechSynthesisResult result) {
		byte[] bytes = result == null ? new byte[0] : toBytes(result.getAudioFrame());
		TextToSpeechResponseMetadata metadata = new TextToSpeechResponseMetadata();
		if (result != null) {
			if (StringUtils.hasText(result.getRequestId())) {
				metadata.put("requestId", result.getRequestId());
			}
			if (result.getUsage() != null && result.getUsage().getCharacters() != null) {
				metadata.put("characters", result.getUsage().getCharacters());
			}
		}
		return new TextToSpeechResponse(List.of(new Speech(bytes)), metadata);
	}

	private byte[] toBytes(ByteBuffer byteBuffer) {
		if (byteBuffer == null) {
			return new byte[0];
		}
		ByteBuffer duplicate = byteBuffer.asReadOnlyBuffer();
		byte[] data = new byte[duplicate.remaining()];
		duplicate.get(data);
		return data;
	}

	private SpeechSynthesisTextType toTextType(String textType) {
		String normalized = textType.trim().toUpperCase().replace('-', '_');
		return SpeechSynthesisTextType.valueOf(normalized);
	}

	private SpeechSynthesisAudioFormat toAudioFormat(String format) {
		String normalized = format.trim().toUpperCase().replace('-', '_');
		return SpeechSynthesisAudioFormat.valueOf(normalized);
	}

	private Map<String, Object> mergeHeaders(Map<String, String> runtimeHeaders) {
		Map<String, Object> headers = new HashMap<>();
		headers.putAll(this.connectionHeaders);
		if (!CollectionUtils.isEmpty(runtimeHeaders)) {
			headers.putAll(runtimeHeaders);
		}
		return headers;
	}

	@Override
	public DashScopeSdkAudioSpeechOptions getDefaultOptions() {
		return DashScopeSdkAudioSpeechOptions.fromOptions(this.defaultOptions);
	}

	public Builder mutate() {
		return new Builder(this);
	}

	@Override
	public DashScopeSdkAudioSpeechModel clone() {
		return this.mutate().build();
	}

	public static Builder builder() {
		return new Builder();
	}

	public static final class Builder {

		private DashScopeSdkSpeechSynthesisClient speechClient = new DefaultDashScopeSdkSpeechSynthesisClient();

		private DashScopeSdkAudioSpeechOptions defaultOptions = DashScopeSdkAudioSpeechOptions.builder()
			.model(DEFAULT_MODEL_NAME)
			.build();

		private RetryTemplate retryTemplate = RetryUtils.DEFAULT_RETRY_TEMPLATE;

		private String apiKey;

		private String workspaceId;

		private Map<String, String> connectionHeaders = new HashMap<>();

		private Builder() {
		}

		private Builder(DashScopeSdkAudioSpeechModel model) {
			this.speechClient = model.speechClient;
			this.defaultOptions = model.defaultOptions;
			this.retryTemplate = model.retryTemplate;
			this.apiKey = model.apiKey;
			this.workspaceId = model.workspaceId;
			this.connectionHeaders = new HashMap<>(model.connectionHeaders);
		}

		public Builder speechClient(DashScopeSdkSpeechSynthesisClient speechClient) {
			this.speechClient = speechClient;
			return this;
		}

		public Builder defaultOptions(DashScopeSdkAudioSpeechOptions defaultOptions) {
			this.defaultOptions = defaultOptions;
			return this;
		}

		public Builder retryTemplate(RetryTemplate retryTemplate) {
			this.retryTemplate = retryTemplate;
			return this;
		}

		public Builder apiKey(String apiKey) {
			this.apiKey = apiKey;
			return this;
		}

		public Builder workspaceId(String workspaceId) {
			this.workspaceId = workspaceId;
			return this;
		}

		public Builder connectionHeaders(Map<String, String> connectionHeaders) {
			this.connectionHeaders = connectionHeaders == null ? new HashMap<>() : new HashMap<>(connectionHeaders);
			return this;
		}

		public DashScopeSdkAudioSpeechModel build() {
			return new DashScopeSdkAudioSpeechModel(this.speechClient, this.defaultOptions, this.retryTemplate,
					this.apiKey, this.workspaceId, this.connectionHeaders);
		}

	}

}
