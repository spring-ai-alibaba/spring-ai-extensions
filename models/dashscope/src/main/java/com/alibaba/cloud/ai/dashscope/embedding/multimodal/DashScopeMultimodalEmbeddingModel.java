/*
 * Copyright 2026-2027 the original author or authors.
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
package com.alibaba.cloud.ai.dashscope.embedding.multimodal;

import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.alibaba.cloud.ai.dashscope.api.DashScopeMultimodalEmbeddingApi;
import com.alibaba.cloud.ai.dashscope.common.DashScopeApiConstants;
import com.alibaba.cloud.ai.dashscope.spec.DashScopeApiSpec;
import io.micrometer.observation.ObservationRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.metadata.DefaultUsage;
import org.springframework.ai.content.Media;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.DocumentEmbeddingModel;
import org.springframework.ai.embedding.DocumentEmbeddingRequest;
import org.springframework.ai.embedding.Embedding;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.embedding.EmbeddingResponseMetadata;
import org.springframework.ai.embedding.EmbeddingResultMetadata;
import org.springframework.ai.embedding.observation.DefaultEmbeddingModelObservationConvention;
import org.springframework.ai.embedding.observation.EmbeddingModelObservationContext;
import org.springframework.ai.embedding.observation.EmbeddingModelObservationConvention;
import org.springframework.ai.embedding.observation.EmbeddingModelObservationDocumentation;
import org.springframework.ai.model.ModelOptionsUtils;
import org.springframework.ai.retry.RetryUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.util.Assert;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.util.StringUtils;

/**
 * Implementation of the DashScope Multimodal Embedding Model.
 * Provides capabilities to convert Text, Image, and Video into embeddings.
 *
 * @author buvidk
 */
public class DashScopeMultimodalEmbeddingModel implements DocumentEmbeddingModel {

	private static final Logger logger = LoggerFactory.getLogger(DashScopeMultimodalEmbeddingModel.class);

	private static final EmbeddingModelObservationConvention DEFAULT_OBSERVATION_CONVENTION = new DefaultEmbeddingModelObservationConvention();

	private static final MimeType TEXT_MIME_TYPE = MimeTypeUtils.parseMimeType("text/*");
	private static final MimeType IMAGE_MIME_TYPE = MimeTypeUtils.parseMimeType("image/*");
	private static final MimeType VIDEO_MIME_TYPE = MimeTypeUtils.parseMimeType("video/*");

	private static final List<MimeType> SUPPORTED_IMAGE_MIME_SUB_TYPES = List.of(
			MimeTypeUtils.IMAGE_JPEG, MimeTypeUtils.IMAGE_GIF, MimeTypeUtils.IMAGE_PNG,
			MimeTypeUtils.parseMimeType("image/bmp"), MimeTypeUtils.parseMimeType("image/webp"),
			MimeTypeUtils.parseMimeType("image/tiff")
	);

	private final DashScopeMultimodalEmbeddingApi dashscopeApi;

	private final DashScopeMultimodalEmbeddingOptions defaultOptions;

	private final RetryTemplate retryTemplate;

	/**
	 * Observation registry used for instrumentation.
	 */
	private final ObservationRegistry observationRegistry;

	/**
	 * Conventions to use for generating observations.
	 */
	private EmbeddingModelObservationConvention observationConvention = DEFAULT_OBSERVATION_CONVENTION;

	public DashScopeMultimodalEmbeddingModel(DashScopeMultimodalEmbeddingApi dashscopeApi,
											 DashScopeMultimodalEmbeddingOptions defaultOptions) {
		this(dashscopeApi, defaultOptions, RetryUtils.DEFAULT_RETRY_TEMPLATE);
	}

	public DashScopeMultimodalEmbeddingModel(DashScopeMultimodalEmbeddingApi dashscopeApi,
											 DashScopeMultimodalEmbeddingOptions defaultOptions,
											 RetryTemplate retryTemplate) {
		this(dashscopeApi, defaultOptions, retryTemplate, ObservationRegistry.NOOP);
	}

	public DashScopeMultimodalEmbeddingModel(DashScopeMultimodalEmbeddingApi dashscopeApi,
											 DashScopeMultimodalEmbeddingOptions defaultOptions,
											 RetryTemplate retryTemplate,
											 ObservationRegistry observationRegistry) {
		Assert.notNull(dashscopeApi, "dashscopeApi must not be null");
		Assert.notNull(defaultOptions, "defaultOptions must not be null");
		Assert.notNull(retryTemplate, "retryTemplate must not be null");
		Assert.notNull(observationRegistry, "observationRegistry must not be null");

		this.dashscopeApi = dashscopeApi;
		this.defaultOptions = defaultOptions;
		this.retryTemplate = retryTemplate;
		this.observationRegistry = observationRegistry;
	}

	@Override
	public EmbeddingResponse call(DocumentEmbeddingRequest request) {
		Assert.notNull(request, "request must not be null");
		Assert.notEmpty(request.getInstructions(), "request.getInstructions() must not be empty");

		DashScopeMultimodalEmbeddingOptions mergedOptions = this.defaultOptions;
		if (request.getOptions() != null) {
			DashScopeMultimodalEmbeddingOptions defaultOptionsCopy = DashScopeMultimodalEmbeddingOptions.builder()
					.model(this.defaultOptions.getModel())
					.dimensions(this.defaultOptions.getDimensions())
					.outputType(this.defaultOptions.getOutputType())
					.fps(this.defaultOptions.getFps())
					.instruct(this.defaultOptions.getInstruct())
					.build();
			mergedOptions = ModelOptionsUtils.merge(request.getOptions(), defaultOptionsCopy, DashScopeMultimodalEmbeddingOptions.class);
		}

		List<Map<String, Object>> inputContents = new ArrayList<>();
		// Keep track of metadata mapping
		Map<Integer, DocumentMetadata> documentMetadataMap = new HashMap<>();

		int index = 0;
		for (Document document : request.getInstructions()) {
			if (StringUtils.hasText(document.getText())) {
				inputContents.add(Map.of("text", document.getText()));
				documentMetadataMap.put(index++, new DocumentMetadata(document.getId(), MimeTypeUtils.TEXT_PLAIN, document.getText()));
			}

			Media media = document.getMedia();
			if (media != null) {
				if (media.getMimeType().isCompatibleWith(TEXT_MIME_TYPE)) {
					inputContents.add(Map.of("text", media.getData().toString()));
					documentMetadataMap.put(index++, new DocumentMetadata(document.getId(), MimeTypeUtils.TEXT_PLAIN, media.getData().toString()));
				} else if (media.getMimeType().isCompatibleWith(IMAGE_MIME_TYPE)) {
					if (SUPPORTED_IMAGE_MIME_SUB_TYPES.contains(media.getMimeType())) {
						inputContents.add(Map.of("image", resolveMediaData(media)));
						documentMetadataMap.put(index++, new DocumentMetadata(document.getId(), media.getMimeType(), media.getData()));
					} else {
						logger.warn("Unsupported image mime type: {}", media.getMimeType());
						throw new IllegalArgumentException("Unsupported image mime type: " + media.getMimeType());
						}
				} else if (media.getMimeType().isCompatibleWith(VIDEO_MIME_TYPE)) {
					String videoUrl = resolveMediaData(media);
					inputContents.add(Map.of("video", videoUrl));
					documentMetadataMap.put(index++, new DocumentMetadata(document.getId(), media.getMimeType(), media.getData()));
				} else {
					logger.warn("Unsupported media type: {}", media.getMimeType());
					throw new IllegalArgumentException("Unsupported media type: " + media.getMimeType());
				}
			}
		}

		DashScopeApiSpec.Input input = new DashScopeApiSpec.Input(inputContents);
		DashScopeApiSpec.Parameters parameters = new DashScopeApiSpec.Parameters(
				mergedOptions.getOutputType(), mergedOptions.getDimensions(), mergedOptions.getFps(), mergedOptions.getInstruct()
		);

		DashScopeApiSpec.MultimodalEmbeddingRequest apiRequest = new DashScopeApiSpec.MultimodalEmbeddingRequest(
				mergedOptions.getModel(), input, parameters);

		var observationContext = EmbeddingModelObservationContext.builder()
				.embeddingRequest(new EmbeddingRequest(List.of(), mergedOptions))
				.provider(DashScopeApiConstants.PROVIDER_NAME)
				.build();

		DashScopeMultimodalEmbeddingOptions finalMergedOptions = mergedOptions;

		return Objects.requireNonNull(EmbeddingModelObservationDocumentation.EMBEDDING_MODEL_OPERATION
				.observation(this.observationConvention, DEFAULT_OBSERVATION_CONVENTION, () -> observationContext,
						this.observationRegistry)
				.observe(() -> {
					ResponseEntity<DashScopeApiSpec.MultimodalEmbeddingResponse> responseEntity =
							this.retryTemplate.execute(ctx -> this.dashscopeApi.embedding(apiRequest));
					DashScopeApiSpec.MultimodalEmbeddingResponse body = responseEntity.getBody();

					if (body == null) {
						logger.warn("No response body returned from DashScope API");
						return new EmbeddingResponse(List.of());
					}

					List<Embedding> embeddingList = new ArrayList<>();
					if (body.output() != null && body.output().embeddings() != null) {
						for (DashScopeApiSpec.EmbeddingResult embeddingResult : body.output().embeddings()) {
							int resultIndex = embeddingResult.index();
							DocumentMetadata docMetadata = documentMetadataMap.get(resultIndex);

							EmbeddingResultMetadata resultMetadata = getEmbeddingResultMetadata(embeddingResult, docMetadata);
							embeddingList.add(new Embedding(embeddingResult.embedding(), resultIndex, resultMetadata));
						}
					}

					EmbeddingResponse embeddingResponse = getEmbeddingResponse(body, finalMergedOptions, embeddingList);
					observationContext.setResponse(embeddingResponse);
					return embeddingResponse;
				}));
	}

	private EmbeddingResponse getEmbeddingResponse(DashScopeApiSpec.MultimodalEmbeddingResponse body, DashScopeMultimodalEmbeddingOptions finalMergedOptions, List<Embedding> embeddingList) {
		DashScopeApiSpec.MultimodalEmbeddingUsage usage = body.usage();
		Integer inputTokens = (usage != null && usage.inputTokens() != null) ? usage.inputTokens() : 0;
		DefaultUsage defaultUsage = new DefaultUsage(inputTokens, 0, inputTokens);
		EmbeddingResponseMetadata responseMetadata = new EmbeddingResponseMetadata(finalMergedOptions.getModel(), defaultUsage, Map.of(
				"request-id", body.requestId() != null ? body.requestId() : ""
		));

		return new EmbeddingResponse(embeddingList, responseMetadata);
	}

	private EmbeddingResultMetadata getEmbeddingResultMetadata(DashScopeApiSpec.EmbeddingResult embeddingResult, DocumentMetadata docMetadata) {
		EmbeddingResultMetadata.ModalityType modalityType = EmbeddingResultMetadata.ModalityType.TEXT;
		if ("image".equalsIgnoreCase(embeddingResult.type())) {
			modalityType = EmbeddingResultMetadata.ModalityType.IMAGE;
		} else if ("video".equalsIgnoreCase(embeddingResult.type())) {
			modalityType = EmbeddingResultMetadata.ModalityType.VIDEO;
		}

		return new EmbeddingResultMetadata(
				docMetadata.documentId(), modalityType, docMetadata.mimeType(), docMetadata.data());
	}

	@Override
	public int dimensions() {
		// If custom dimensions were provided, we could use them, else fallback to unknown or a known map
		if (this.defaultOptions != null && this.defaultOptions.getDimensions() != null) {
			return this.defaultOptions.getDimensions();
		}
		return 1024; // typical for qwen multimodal
	}

	/**
	 * Use the provided convention for reporting observation data.
	 *
	 * @param observationConvention The provided convention
	 */
	public void setObservationConvention(EmbeddingModelObservationConvention observationConvention) {
		Assert.notNull(observationConvention, "observationConvention cannot be null");
		this.observationConvention = observationConvention;
	}

	private String resolveMediaData(Media media) {
		Object data = media.getData();
		boolean isVideo = media.getMimeType().isCompatibleWith(VIDEO_MIME_TYPE);

		// byte[] → base64 data URI (image only, video does not support inline data)
		if (!isVideo && data instanceof byte[] bytes) {
			return toBase64DataUri(media.getMimeType(), bytes);
		}
		// String / URL → return directly
		if (data instanceof String str) {
			return str;
		}
		if (data instanceof URL url) {
			return url.toString();
		}
		throw new IllegalArgumentException("Unsupported media data type: " + data.getClass().getName());
	}

	private String toBase64DataUri(MimeType mimeType, byte[] bytes) {
		return "data:" + mimeType + ";base64," + Base64.getEncoder().encodeToString(bytes);
	}

	private record DocumentMetadata(String documentId, MimeType mimeType, Object data) {
	}

	/**
	 * Returns a builder pre-populated with the current configuration for mutation.
	 */
	public Builder mutate() {
		return new Builder(this);
	}

	@Override
	public DashScopeMultimodalEmbeddingModel clone() {
		return this.mutate().build();
	}

	public static Builder builder() {
		return new Builder();
	}

	public static final class Builder {

		private DashScopeMultimodalEmbeddingApi dashScopeMultimodalEmbeddingApi;

		private DashScopeMultimodalEmbeddingOptions defaultOptions = DashScopeMultimodalEmbeddingOptions.builder()
				.model(DashScopeMultimodalEmbeddingApi.DEFAULT_MULTIMODAL_EMBEDDING_MODEL)
				.build();

		private RetryTemplate retryTemplate = RetryUtils.DEFAULT_RETRY_TEMPLATE;

		private ObservationRegistry observationRegistry = ObservationRegistry.NOOP;

		private Builder() {
		}

		private Builder(DashScopeMultimodalEmbeddingModel model) {
			this.dashScopeMultimodalEmbeddingApi = model.dashscopeApi;
			this.defaultOptions = model.defaultOptions;
			this.retryTemplate = model.retryTemplate;
			this.observationRegistry = model.observationRegistry;
		}

		public Builder dashScopeMultimodalEmbeddingApi(DashScopeMultimodalEmbeddingApi dashScopeMultimodalEmbeddingApi) {
			this.dashScopeMultimodalEmbeddingApi = dashScopeMultimodalEmbeddingApi;
			return this;
		}

		public Builder defaultOptions(DashScopeMultimodalEmbeddingOptions defaultOptions) {
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

		public DashScopeMultimodalEmbeddingModel build() {
			return new DashScopeMultimodalEmbeddingModel(
					this.dashScopeMultimodalEmbeddingApi, this.defaultOptions,
					this.retryTemplate, this.observationRegistry);
		}
	}

}
