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

package com.alibaba.cloud.ai.dashscope.sdk.audio.transcription;

import com.alibaba.cloud.ai.dashscope.sdk.common.DashScopeSdkException;
import com.alibaba.dashscope.audio.asr.transcription.TranscriptionParam;
import com.alibaba.dashscope.audio.asr.transcription.TranscriptionQueryParam;
import com.alibaba.dashscope.audio.asr.transcription.TranscriptionResult;
import com.alibaba.dashscope.audio.asr.transcription.TranscriptionTaskResult;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.ai.audio.transcription.AudioTranscription;
import org.springframework.ai.audio.transcription.AudioTranscriptionOptions;
import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.audio.transcription.AudioTranscriptionResponse;
import org.springframework.ai.audio.transcription.AudioTranscriptionResponseMetadata;
import org.springframework.ai.audio.transcription.TranscriptionModel;
import org.springframework.ai.model.ModelOptionsUtils;
import org.springframework.ai.retry.RetryUtils;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * {@link TranscriptionModel} implementation backed by DashScope Java SDK.
 */
public class DashScopeSdkAudioTranscriptionModel implements TranscriptionModel {

	public static final String DEFAULT_MODEL_NAME = "paraformer-v2";

	private final DashScopeSdkTranscriptionClient transcriptionClient;

	private final DashScopeSdkAudioTranscriptionOptions defaultOptions;

	private final RetryTemplate retryTemplate;

	private final String apiKey;

	private final String workspaceId;

	private final Map<String, String> connectionHeaders;

	private final HttpClient httpClient;

	public DashScopeSdkAudioTranscriptionModel(DashScopeSdkTranscriptionClient transcriptionClient,
			DashScopeSdkAudioTranscriptionOptions defaultOptions, RetryTemplate retryTemplate, String apiKey,
			String workspaceId, Map<String, String> connectionHeaders) {
		this.transcriptionClient = transcriptionClient;
		this.defaultOptions = defaultOptions;
		this.retryTemplate = retryTemplate;
		this.apiKey = apiKey;
		this.workspaceId = workspaceId;
		this.connectionHeaders = connectionHeaders;
		this.httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
	}

	@Override
	public AudioTranscriptionResponse call(AudioTranscriptionPrompt prompt) {
		DashScopeSdkAudioTranscriptionOptions options = mergeOptions(prompt.getOptions());
		List<String> fileUrls = resolveFileUrls(prompt, options);
		if (CollectionUtils.isEmpty(fileUrls)) {
			throw new IllegalArgumentException(
					"No valid file URLs found. Configure spring.ai.dashscope.sdk.audio.transcription.options.file-urls or use URL resource.");
		}

		TranscriptionParam request = createRequest(fileUrls, options);

		TranscriptionResult submitResult = this.retryTemplate.execute(ctx -> executeSubmit(request));
		if (submitResult == null || !StringUtils.hasText(submitResult.getTaskId())) {
			return toAudioResponse(submitResult);
		}

		TranscriptionQueryParam query = TranscriptionQueryParam.FromTranscriptionParam(request, submitResult.getTaskId());
		TranscriptionResult finalResult = this.retryTemplate.execute(ctx -> executeWait(query));
		return toAudioResponse(finalResult == null ? submitResult : finalResult);
	}

	private DashScopeSdkAudioTranscriptionOptions mergeOptions(AudioTranscriptionOptions runtimeOptions) {
		DashScopeSdkAudioTranscriptionOptions options = DashScopeSdkAudioTranscriptionOptions.fromOptions(this.defaultOptions);
		if (runtimeOptions == null) {
			return options;
		}

		DashScopeSdkAudioTranscriptionOptions runtime = ModelOptionsUtils.copyToTarget(runtimeOptions,
				AudioTranscriptionOptions.class, DashScopeSdkAudioTranscriptionOptions.class);
		DashScopeSdkAudioTranscriptionOptions merged = ModelOptionsUtils.merge(runtime, options,
				DashScopeSdkAudioTranscriptionOptions.class);
		if (runtime != null && !CollectionUtils.isEmpty(runtime.getHttpHeaders())) {
			merged.setHttpHeaders(runtime.getHttpHeaders());
		}
		else {
			merged.setHttpHeaders(this.defaultOptions.getHttpHeaders());
		}
		return merged;
	}

	private List<String> resolveFileUrls(AudioTranscriptionPrompt prompt, DashScopeSdkAudioTranscriptionOptions options) {
		if (!CollectionUtils.isEmpty(options.getFileUrls())) {
			return options.getFileUrls();
		}
		if (prompt == null || prompt.getInstructions() == null) {
			return List.of();
		}
		try {
			URI uri = prompt.getInstructions().getURI();
			if (uri != null && StringUtils.hasText(uri.getScheme())
					&& ("http".equalsIgnoreCase(uri.getScheme()) || "https".equalsIgnoreCase(uri.getScheme()))) {
				return List.of(uri.toString());
			}
		}
		catch (Exception ignored) {
		}
		return List.of();
	}

	private TranscriptionParam createRequest(List<String> fileUrls, DashScopeSdkAudioTranscriptionOptions options) {
		TranscriptionParam.TranscriptionParamBuilder<?, ?> builder = TranscriptionParam.builder()
			.model(options.getModel())
			.fileUrls(fileUrls)
			.phraseId(options.getPhraseId())
			.channelId(options.getChannelId())
			.diarizationEnabled(options.getDiarizationEnabled())
			.speakerCount(options.getSpeakerCount())
			.disfluencyRemovalEnabled(options.getDisfluencyRemovalEnabled())
			.timestampAlignmentEnabled(options.getTimestampAlignmentEnabled())
			.specialWordFilter(options.getSpecialWordFilter())
			.audioEventDetectionEnabled(options.getAudioEventDetectionEnabled());

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

	private TranscriptionResult executeSubmit(TranscriptionParam request) {
		try {
			return this.transcriptionClient.asyncCall(request);
		}
		catch (Exception ex) {
			throw new DashScopeSdkException("Failed to submit DashScope SDK transcription request", ex);
		}
	}

	private TranscriptionResult executeWait(TranscriptionQueryParam query) {
		try {
			return this.transcriptionClient.wait(query);
		}
		catch (Exception ex) {
			throw new DashScopeSdkException("Failed to fetch DashScope SDK transcription result", ex);
		}
	}

	private AudioTranscriptionResponse toAudioResponse(TranscriptionResult result) {
		String text = extractBestText(result);
		AudioTranscriptionResponseMetadata metadata = new AudioTranscriptionResponseMetadata();
		if (result != null) {
			if (StringUtils.hasText(result.getRequestId())) {
				metadata.put("requestId", result.getRequestId());
			}
			if (StringUtils.hasText(result.getTaskId())) {
				metadata.put("taskId", result.getTaskId());
			}
			if (result.getTaskStatus() != null && StringUtils.hasText(result.getTaskStatus().getValue())) {
				metadata.put("taskStatus", result.getTaskStatus().getValue());
			}
			if (result.getUsage() != null) {
				metadata.put("usage", result.getUsage().toString());
			}
			if (!CollectionUtils.isEmpty(result.getResults())) {
				metadata.put("resultCount", result.getResults().size());
			}
		}
		return new AudioTranscriptionResponse(new AudioTranscription(text), metadata);
	}

	private String extractBestText(TranscriptionResult result) {
		if (result == null) {
			return "";
		}

		Set<String> snippets = new LinkedHashSet<>();
		if (!CollectionUtils.isEmpty(result.getResults())) {
			for (TranscriptionTaskResult taskResult : result.getResults()) {
				if (!StringUtils.hasText(taskResult.getTranscriptionUrl())) {
					continue;
				}
				String rawDocument = downloadDocument(taskResult.getTranscriptionUrl());
				String extracted = extractTextFromJson(rawDocument);
				if (StringUtils.hasText(extracted)) {
					snippets.add(extracted);
				}
				else if (StringUtils.hasText(rawDocument)) {
					snippets.add(rawDocument);
				}
				else {
					snippets.add(taskResult.getTranscriptionUrl());
				}
			}
		}

		if (snippets.isEmpty() && result.getOutput() != null) {
			String fromOutput = extractTextFromJson(result.getOutput().toString());
			if (StringUtils.hasText(fromOutput)) {
				snippets.add(fromOutput);
			}
		}

		if (snippets.isEmpty()) {
			return "";
		}
		return String.join("\n", snippets);
	}

	private String downloadDocument(String url) {
		try {
			HttpRequest request = HttpRequest.newBuilder(URI.create(url)).timeout(Duration.ofSeconds(10)).GET().build();
			HttpResponse<String> response = this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
			if (response.statusCode() >= 200 && response.statusCode() < 300) {
				return response.body();
			}
		}
		catch (Exception ignored) {
		}
		return "";
	}

	private String extractTextFromJson(String rawText) {
		if (!StringUtils.hasText(rawText)) {
			return "";
		}
		try {
			JsonElement root = JsonParser.parseString(rawText);
			List<String> collected = new ArrayList<>();
			collectText(root, collected);
			return String.join("\n", collected.stream().filter(StringUtils::hasText).distinct().toList());
		}
		catch (Exception ignored) {
		}
		return "";
	}

	private void collectText(JsonElement node, List<String> collected) {
		if (node == null || node.isJsonNull()) {
			return;
		}
		if (node.isJsonArray()) {
			node.getAsJsonArray().forEach(child -> collectText(child, collected));
			return;
		}
		if (!node.isJsonObject()) {
			return;
		}

		JsonObject object = node.getAsJsonObject();
		for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
			String key = entry.getKey() == null ? "" : entry.getKey().toLowerCase();
			JsonElement value = entry.getValue();
			if ((key.contains("text") || key.contains("transcript")) && value != null && value.isJsonPrimitive()) {
				collected.add(value.getAsString());
			}
			else {
				collectText(value, collected);
			}
		}
	}

	private Map<String, Object> mergeHeaders(Map<String, String> runtimeHeaders) {
		Map<String, Object> headers = new HashMap<>();
		headers.putAll(this.connectionHeaders);
		if (!CollectionUtils.isEmpty(runtimeHeaders)) {
			headers.putAll(runtimeHeaders);
		}
		return headers;
	}

	public DashScopeSdkAudioTranscriptionOptions getDefaultOptions() {
		return DashScopeSdkAudioTranscriptionOptions.fromOptions(this.defaultOptions);
	}

	public Builder mutate() {
		return new Builder(this);
	}

	@Override
	public DashScopeSdkAudioTranscriptionModel clone() {
		return this.mutate().build();
	}

	public static Builder builder() {
		return new Builder();
	}

	public static final class Builder {

		private DashScopeSdkTranscriptionClient transcriptionClient = new DefaultDashScopeSdkTranscriptionClient();

		private DashScopeSdkAudioTranscriptionOptions defaultOptions = DashScopeSdkAudioTranscriptionOptions.builder()
			.model(DEFAULT_MODEL_NAME)
			.build();

		private RetryTemplate retryTemplate = RetryUtils.DEFAULT_RETRY_TEMPLATE;

		private String apiKey;

		private String workspaceId;

		private Map<String, String> connectionHeaders = new HashMap<>();

		private Builder() {
		}

		private Builder(DashScopeSdkAudioTranscriptionModel model) {
			this.transcriptionClient = model.transcriptionClient;
			this.defaultOptions = model.defaultOptions;
			this.retryTemplate = model.retryTemplate;
			this.apiKey = model.apiKey;
			this.workspaceId = model.workspaceId;
			this.connectionHeaders = new HashMap<>(model.connectionHeaders);
		}

		public Builder transcriptionClient(DashScopeSdkTranscriptionClient transcriptionClient) {
			this.transcriptionClient = transcriptionClient;
			return this;
		}

		public Builder defaultOptions(DashScopeSdkAudioTranscriptionOptions defaultOptions) {
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

		public DashScopeSdkAudioTranscriptionModel build() {
			return new DashScopeSdkAudioTranscriptionModel(this.transcriptionClient, this.defaultOptions,
					this.retryTemplate, this.apiKey, this.workspaceId, this.connectionHeaders);
		}

	}

}
