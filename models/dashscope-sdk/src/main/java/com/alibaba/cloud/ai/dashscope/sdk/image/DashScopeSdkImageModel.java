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

package com.alibaba.cloud.ai.dashscope.sdk.image;

import com.alibaba.cloud.ai.dashscope.sdk.common.DashScopeSdkException;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesisParam;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesisResult;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesisUsage;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.ai.image.Image;
import org.springframework.ai.image.ImageGeneration;
import org.springframework.ai.image.ImageModel;
import org.springframework.ai.image.ImageOptions;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.image.ImageResponseMetadata;
import org.springframework.ai.image.observation.DefaultImageModelObservationConvention;
import org.springframework.ai.image.observation.ImageModelObservationContext;
import org.springframework.ai.image.observation.ImageModelObservationConvention;
import org.springframework.ai.image.observation.ImageModelObservationDocumentation;
import org.springframework.ai.model.ModelOptionsUtils;
import org.springframework.ai.retry.RetryUtils;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@link ImageModel} implementation backed by DashScope Java SDK.
 */
public class DashScopeSdkImageModel implements ImageModel {

	private static final ImageModelObservationConvention DEFAULT_OBSERVATION_CONVENTION =
			new DefaultImageModelObservationConvention();

	public static final String PROVIDER_NAME = "dashscope-sdk";

	public static final String DEFAULT_MODEL_NAME = "wanx-v1";

	private final DashScopeSdkImageSynthesisClient imageClient;

	private final DashScopeSdkImageOptions defaultOptions;

	private final RetryTemplate retryTemplate;

	private final ObservationRegistry observationRegistry;

	private final String apiKey;

	private final String workspaceId;

	private final Map<String, String> connectionHeaders;

	private ImageModelObservationConvention observationConvention = DEFAULT_OBSERVATION_CONVENTION;

	public DashScopeSdkImageModel(DashScopeSdkImageSynthesisClient imageClient, DashScopeSdkImageOptions defaultOptions,
			RetryTemplate retryTemplate, ObservationRegistry observationRegistry, String apiKey, String workspaceId,
			Map<String, String> connectionHeaders) {

		Assert.notNull(imageClient, "imageClient cannot be null");
		Assert.notNull(defaultOptions, "defaultOptions cannot be null");
		Assert.notNull(retryTemplate, "retryTemplate cannot be null");
		Assert.notNull(observationRegistry, "observationRegistry cannot be null");
		Assert.notNull(connectionHeaders, "connectionHeaders cannot be null");

		this.imageClient = imageClient;
		this.defaultOptions = defaultOptions;
		this.retryTemplate = retryTemplate;
		this.observationRegistry = observationRegistry;
		this.apiKey = apiKey;
		this.workspaceId = workspaceId;
		this.connectionHeaders = connectionHeaders;
	}

	@Override
	public ImageResponse call(ImagePrompt request) {
		Assert.notNull(request, "Prompt must not be null");
		Assert.isTrue(!CollectionUtils.isEmpty(request.getInstructions()), "Prompt messages must not be empty");

		DashScopeSdkImageOptions options = toImageOptions(request.getOptions());
		ImageSynthesisParam sdkRequest = createRequest(request, options);

		ImageModelObservationContext observationContext = ImageModelObservationContext.builder()
			.imagePrompt(request)
			.provider(PROVIDER_NAME)
			.build();

		return ImageModelObservationDocumentation.IMAGE_MODEL_OPERATION
			.observation(this.observationConvention, DEFAULT_OBSERVATION_CONVENTION, () -> observationContext,
					this.observationRegistry)
			.observe(() -> {
				ImageSynthesisResult submitResult = this.retryTemplate.execute(ctx -> executeSubmit(sdkRequest, options));
				ImageSynthesisResult finalResult = resolveFinalResult(submitResult, options);
				ImageResponse response = toImageResponse(finalResult);
				observationContext.setResponse(response);
				return response;
			});
	}

	private DashScopeSdkImageOptions toImageOptions(ImageOptions runtimeOptions) {
		DashScopeSdkImageOptions options = DashScopeSdkImageOptions.fromOptions(this.defaultOptions);
		if (runtimeOptions == null) {
			return options;
		}

		DashScopeSdkImageOptions runtime = ModelOptionsUtils.copyToTarget(runtimeOptions, ImageOptions.class,
				DashScopeSdkImageOptions.class);

		DashScopeSdkImageOptions merged = ModelOptionsUtils.merge(runtime, options, DashScopeSdkImageOptions.class);
		if (runtime != null && !CollectionUtils.isEmpty(runtime.getHttpHeaders())) {
			merged.setHttpHeaders(runtime.getHttpHeaders());
		}
		else {
			merged.setHttpHeaders(this.defaultOptions.getHttpHeaders());
		}
		if (runtime != null && runtime.getExtraBody() != null) {
			merged.setExtraBody(runtime.getExtraBody());
		}
		else {
			merged.setExtraBody(this.defaultOptions.getExtraBody());
		}
		return merged;
	}

	private ImageSynthesisParam createRequest(ImagePrompt request, DashScopeSdkImageOptions options) {
		ImageSynthesisParam.ImageSynthesisParamBuilder<?, ?> builder = ImageSynthesisParam.builder()
			.model(options.getModel())
			.prompt(request.getInstructions().get(0).getText())
			.n(options.getN())
			.size(options.getSize())
			.style(options.getStyle())
			.seed(options.getSeed())
			.negativePrompt(options.getNegativePrompt())
			.refImage(options.getRefImage());

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

		if (StringUtils.hasText(options.getResponseFormat())) {
			builder.parameter("response_format", options.getResponseFormat());
		}
		if (!CollectionUtils.isEmpty(options.getExtraBody())) {
			builder.parameters(options.getExtraBody());
		}

		return builder.build();
	}

	private ImageSynthesisResult executeSubmit(ImageSynthesisParam request, DashScopeSdkImageOptions options) {
		try {
			if (Boolean.FALSE.equals(options.getAsync())) {
				return this.imageClient.call(request);
			}
			return this.imageClient.asyncCall(request);
		}
		catch (Exception ex) {
			throw new DashScopeSdkException("Failed to submit DashScope SDK image request", ex);
		}
	}

	private ImageSynthesisResult resolveFinalResult(ImageSynthesisResult submitResult, DashScopeSdkImageOptions options) {
		if (submitResult == null || submitResult.getOutput() == null || Boolean.FALSE.equals(options.getAsync())) {
			return submitResult;
		}

		String status = submitResult.getOutput().getTaskStatus();
		if ("SUCCEEDED".equalsIgnoreCase(status) || "FAILED".equalsIgnoreCase(status)
				|| "CANCELED".equalsIgnoreCase(status)) {
			return submitResult;
		}

		try {
			return this.retryTemplate.execute(ctx -> this.imageClient.wait(submitResult,
					String.valueOf(options.getPollIntervalMs() == null ? 1000 : options.getPollIntervalMs())));
		}
		catch (Exception ex) {
			throw new DashScopeSdkException("Failed to fetch DashScope SDK image task result", ex);
		}
	}

	private ImageResponse toImageResponse(ImageSynthesisResult result) {
		if (result == null || result.getOutput() == null) {
			return new ImageResponse(List.of(), new ImageResponseMetadata());
		}

		List<ImageGeneration> generations = new ArrayList<>();
		if (!CollectionUtils.isEmpty(result.getOutput().getResults())) {
			for (Map<String, String> item : result.getOutput().getResults()) {
				String url = item.getOrDefault("url", item.get("image_url"));
				String b64 = item.getOrDefault("b64_json", item.get("image"));
				if (!StringUtils.hasText(url) && !StringUtils.hasText(b64)) {
					continue;
				}
				generations.add(new ImageGeneration(new Image(url, b64)));
			}
		}

		ImageResponseMetadata metadata = new ImageResponseMetadata();
		if (StringUtils.hasText(result.getRequestId())) {
			metadata.put("requestId", result.getRequestId());
		}
		if (StringUtils.hasText(result.getOutput().getTaskId())) {
			metadata.put("taskId", result.getOutput().getTaskId());
		}
		if (StringUtils.hasText(result.getOutput().getTaskStatus())) {
			metadata.put("taskStatus", result.getOutput().getTaskStatus());
		}
		if (StringUtils.hasText(result.getOutput().getCode())) {
			metadata.put("code", result.getOutput().getCode());
		}
		if (StringUtils.hasText(result.getOutput().getMessage())) {
			metadata.put("message", result.getOutput().getMessage());
		}
		ImageSynthesisUsage usage = result.getUsage();
		if (usage != null && usage.getImageCount() != null) {
			metadata.put("imageCount", usage.getImageCount());
		}

		return new ImageResponse(generations, metadata);
	}

	private Map<String, Object> mergeHeaders(Map<String, String> runtimeHeaders) {
		Map<String, Object> headers = new HashMap<>();
		headers.putAll(this.connectionHeaders);
		if (!CollectionUtils.isEmpty(runtimeHeaders)) {
			headers.putAll(runtimeHeaders);
		}
		return headers;
	}

	public DashScopeSdkImageOptions getDefaultOptions() {
		return DashScopeSdkImageOptions.fromOptions(this.defaultOptions);
	}

	public void setObservationConvention(ImageModelObservationConvention observationConvention) {
		Assert.notNull(observationConvention, "observationConvention cannot be null");
		this.observationConvention = observationConvention;
	}

	public Builder mutate() {
		return new Builder(this);
	}

	@Override
	public DashScopeSdkImageModel clone() {
		return this.mutate().build();
	}

	public static Builder builder() {
		return new Builder();
	}

	public static final class Builder {

		private DashScopeSdkImageSynthesisClient imageClient = new DefaultDashScopeSdkImageSynthesisClient();

		private DashScopeSdkImageOptions defaultOptions = DashScopeSdkImageOptions.builder()
			.model(DEFAULT_MODEL_NAME)
			.n(1)
			.build();

		private RetryTemplate retryTemplate = RetryUtils.DEFAULT_RETRY_TEMPLATE;

		private ObservationRegistry observationRegistry = ObservationRegistry.NOOP;

		private String apiKey;

		private String workspaceId;

		private Map<String, String> connectionHeaders = new HashMap<>();

		private Builder() {
		}

		private Builder(DashScopeSdkImageModel imageModel) {
			this.imageClient = imageModel.imageClient;
			this.defaultOptions = imageModel.defaultOptions;
			this.retryTemplate = imageModel.retryTemplate;
			this.observationRegistry = imageModel.observationRegistry;
			this.apiKey = imageModel.apiKey;
			this.workspaceId = imageModel.workspaceId;
			this.connectionHeaders = new HashMap<>(imageModel.connectionHeaders);
		}

		public Builder imageClient(DashScopeSdkImageSynthesisClient imageClient) {
			this.imageClient = imageClient;
			return this;
		}

		public Builder defaultOptions(DashScopeSdkImageOptions defaultOptions) {
			this.defaultOptions = defaultOptions;
			return this;
		}

		public Builder retryTemplate(RetryTemplate retryTemplate) {
			this.retryTemplate = retryTemplate;
			return this;
		}

		public Builder observationRegistry(ObservationRegistry observationRegistry) {
			this.observationRegistry = observationRegistry;
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

		public DashScopeSdkImageModel build() {
			return new DashScopeSdkImageModel(this.imageClient, this.defaultOptions, this.retryTemplate,
					this.observationRegistry, this.apiKey, this.workspaceId, this.connectionHeaders);
		}

	}

}
