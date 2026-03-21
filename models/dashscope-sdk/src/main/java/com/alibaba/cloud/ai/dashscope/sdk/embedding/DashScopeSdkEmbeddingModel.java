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

package com.alibaba.cloud.ai.dashscope.sdk.embedding;

import com.alibaba.cloud.ai.dashscope.sdk.common.DashScopeSdkException;
import com.alibaba.dashscope.embeddings.TextEmbeddingParam;
import com.alibaba.dashscope.embeddings.TextEmbeddingResult;
import com.alibaba.dashscope.embeddings.TextEmbeddingResultItem;
import com.alibaba.dashscope.embeddings.TextEmbeddingUsage;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.ai.chat.metadata.DefaultUsage;
import org.springframework.ai.chat.metadata.EmptyUsage;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.embedding.AbstractEmbeddingModel;
import org.springframework.ai.embedding.BatchingStrategy;
import org.springframework.ai.embedding.Embedding;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingOptions;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.embedding.EmbeddingResponseMetadata;
import org.springframework.ai.embedding.observation.DefaultEmbeddingModelObservationConvention;
import org.springframework.ai.embedding.observation.EmbeddingModelObservationContext;
import org.springframework.ai.embedding.observation.EmbeddingModelObservationConvention;
import org.springframework.ai.embedding.observation.EmbeddingModelObservationDocumentation;
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
 * {@link EmbeddingModel} implementation backed by DashScope Java SDK.
 */
public class DashScopeSdkEmbeddingModel extends AbstractEmbeddingModel {

	private static final EmbeddingModelObservationConvention DEFAULT_OBSERVATION_CONVENTION =
			new DefaultEmbeddingModelObservationConvention();

	public static final String PROVIDER_NAME = "dashscope-sdk";

	public static final String DEFAULT_MODEL_NAME = "text-embedding-v2";

	private static final Map<String, Integer> KNOWN_DIMENSIONS = Map.of("text-embedding-v1", 1536,
			"text-embedding-v2", 1536);

	private final DashScopeSdkTextEmbeddingClient embeddingClient;

	private final DashScopeSdkEmbeddingOptions defaultOptions;

	private final MetadataMode metadataMode;

	private final RetryTemplate retryTemplate;

	private final ObservationRegistry observationRegistry;

	private final String apiKey;

	private final String workspaceId;

	private final Map<String, String> connectionHeaders;

	private EmbeddingModelObservationConvention observationConvention = DEFAULT_OBSERVATION_CONVENTION;

	public DashScopeSdkEmbeddingModel(DashScopeSdkTextEmbeddingClient embeddingClient,
			DashScopeSdkEmbeddingOptions defaultOptions, MetadataMode metadataMode, RetryTemplate retryTemplate,
			ObservationRegistry observationRegistry, String apiKey, String workspaceId,
			Map<String, String> connectionHeaders) {

		Assert.notNull(embeddingClient, "embeddingClient cannot be null");
		Assert.notNull(defaultOptions, "defaultOptions cannot be null");
		Assert.notNull(metadataMode, "metadataMode cannot be null");
		Assert.notNull(retryTemplate, "retryTemplate cannot be null");
		Assert.notNull(observationRegistry, "observationRegistry cannot be null");
		Assert.notNull(connectionHeaders, "connectionHeaders cannot be null");

		this.embeddingClient = embeddingClient;
		this.defaultOptions = defaultOptions;
		this.metadataMode = metadataMode;
		this.retryTemplate = retryTemplate;
		this.observationRegistry = observationRegistry;
		this.apiKey = apiKey;
		this.workspaceId = workspaceId;
		this.connectionHeaders = connectionHeaders;
	}

	@Override
	public float[] embed(Document document) {
		Assert.notNull(document, "Document must not be null");
		return this.embed(document.getFormattedContent(this.metadataMode));
	}

	@Override
	public EmbeddingResponse call(EmbeddingRequest request) {
		Assert.notNull(request, "EmbeddingRequest must not be null");

		EmbeddingRequest embeddingRequest = buildEmbeddingRequest(request);
		TextEmbeddingParam sdkRequest = createRequest(embeddingRequest);

		EmbeddingModelObservationContext observationContext = EmbeddingModelObservationContext.builder()
			.embeddingRequest(embeddingRequest)
			.provider(PROVIDER_NAME)
			.build();

		return EmbeddingModelObservationDocumentation.EMBEDDING_MODEL_OPERATION
			.observation(this.observationConvention, DEFAULT_OBSERVATION_CONVENTION, () -> observationContext,
					this.observationRegistry)
			.observe(() -> {
				TextEmbeddingResult result = this.retryTemplate.execute(ctx -> executeCall(sdkRequest));
				EmbeddingResponse response = toEmbeddingResponse(result, sdkRequest.getModel());
				observationContext.setResponse(response);
				return response;
			});
	}

	private EmbeddingRequest buildEmbeddingRequest(EmbeddingRequest embeddingRequest) {
		DashScopeSdkEmbeddingOptions runtimeOptions = null;
		if (embeddingRequest.getOptions() != null) {
			runtimeOptions = ModelOptionsUtils.copyToTarget(embeddingRequest.getOptions(), EmbeddingOptions.class,
					DashScopeSdkEmbeddingOptions.class);
		}

		DashScopeSdkEmbeddingOptions requestOptions = runtimeOptions == null ? DashScopeSdkEmbeddingOptions
			.fromOptions(this.defaultOptions)
			: DashScopeSdkEmbeddingOptions.builder()
				.model(ModelOptionsUtils.mergeOption(runtimeOptions.getModel(), this.defaultOptions.getModel()))
				.dimensions(ModelOptionsUtils.mergeOption(runtimeOptions.getDimensions(), this.defaultOptions.getDimensions()))
				.textType(ModelOptionsUtils.mergeOption(runtimeOptions.getTextType(), this.defaultOptions.getTextType()))
				.build();

		if (runtimeOptions != null && !CollectionUtils.isEmpty(runtimeOptions.getHttpHeaders())) {
			requestOptions.setHttpHeaders(runtimeOptions.getHttpHeaders());
		}
		else {
			requestOptions.setHttpHeaders(this.defaultOptions.getHttpHeaders());
		}

		return new EmbeddingRequest(embeddingRequest.getInstructions(), requestOptions);
	}

	private TextEmbeddingParam createRequest(EmbeddingRequest request) {
		DashScopeSdkEmbeddingOptions requestOptions = (DashScopeSdkEmbeddingOptions) request.getOptions();

		TextEmbeddingParam.TextEmbeddingParamBuilder<?, ?> builder = TextEmbeddingParam.builder()
			.model(requestOptions.getModel())
			.texts(request.getInstructions());

		if (StringUtils.hasText(requestOptions.getTextType())) {
			builder.textType(toTextType(requestOptions.getTextType()));
		}

		if (requestOptions.getDimensions() != null) {
			builder.parameter("dimension", requestOptions.getDimensions());
		}

		if (StringUtils.hasText(this.apiKey)) {
			builder.apiKey(this.apiKey);
		}
		if (StringUtils.hasText(this.workspaceId)) {
			builder.workspace(this.workspaceId);
		}

		Map<String, Object> headers = mergeHeaders(requestOptions.getHttpHeaders());
		if (!CollectionUtils.isEmpty(headers)) {
			builder.headers(headers);
		}

		return builder.build();
	}

	private TextEmbeddingResult executeCall(TextEmbeddingParam request) {
		try {
			return this.embeddingClient.call(request);
		}
		catch (Exception ex) {
			throw new DashScopeSdkException("Failed to call DashScope SDK embedding API", ex);
		}
	}

	private EmbeddingResponse toEmbeddingResponse(TextEmbeddingResult result, String requestModel) {
		if (result == null || result.getOutput() == null || CollectionUtils.isEmpty(result.getOutput().getEmbeddings())) {
			return new EmbeddingResponse(List.of());
		}

		List<Embedding> embeddings = new ArrayList<>();
		for (TextEmbeddingResultItem item : result.getOutput().getEmbeddings()) {
			if (item.getEmbedding() == null) {
				continue;
			}
			float[] vector = new float[item.getEmbedding().size()];
			for (int i = 0; i < item.getEmbedding().size(); i++) {
				vector[i] = item.getEmbedding().get(i).floatValue();
			}
			embeddings.add(new Embedding(vector, item.getTextIndex()));
		}

		TextEmbeddingUsage usage = result.getUsage();
		Usage embeddingUsage = usage == null ? new EmptyUsage()
				: new DefaultUsage(null, null, usage.getTotalTokens(), usage);

		EmbeddingResponseMetadata metadata = new EmbeddingResponseMetadata(requestModel, embeddingUsage,
				new HashMap<>(Map.of("requestId", result.getRequestId() == null ? "" : result.getRequestId())));
		return new EmbeddingResponse(embeddings, metadata);
	}

	private TextEmbeddingParam.TextType toTextType(String textType) {
		if (!StringUtils.hasText(textType)) {
			return null;
		}
		String normalized = textType.trim().toUpperCase().replace('-', '_');
		return TextEmbeddingParam.TextType.valueOf(normalized);
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
	public List<float[]> embed(List<String> texts) {
		Assert.notNull(texts, "Texts must not be null");
		return this.call(new EmbeddingRequest(texts, this.defaultOptions))
			.getResults()
			.stream()
			.map(Embedding::getOutput)
			.toList();
	}

	@Override
	public EmbeddingResponse embedForResponse(List<String> texts) {
		Assert.notNull(texts, "Texts must not be null");
		return this.call(new EmbeddingRequest(texts, this.defaultOptions));
	}

	@Override
	public List<float[]> embed(List<Document> documents, EmbeddingOptions options, BatchingStrategy batchingStrategy) {
		if (options.getModel() == null && options.getDimensions() == null && this.defaultOptions != null) {
			options = this.defaultOptions;
		}
		return super.embed(documents, options, batchingStrategy);
	}

	@Override
	public int dimensions() {
		if (KNOWN_DIMENSIONS.containsKey(this.defaultOptions.getModel())) {
			return KNOWN_DIMENSIONS.get(this.defaultOptions.getModel());
		}
		return super.dimensions();
	}

	public DashScopeSdkEmbeddingOptions getDefaultOptions() {
		return DashScopeSdkEmbeddingOptions.fromOptions(this.defaultOptions);
	}

	public void setObservationConvention(EmbeddingModelObservationConvention observationConvention) {
		Assert.notNull(observationConvention, "observationConvention cannot be null");
		this.observationConvention = observationConvention;
	}

	public Builder mutate() {
		return new Builder(this);
	}

	@Override
	public DashScopeSdkEmbeddingModel clone() {
		return this.mutate().build();
	}

	public static Builder builder() {
		return new Builder();
	}

	public static final class Builder {

		private DashScopeSdkTextEmbeddingClient embeddingClient = new DefaultDashScopeSdkTextEmbeddingClient();

		private DashScopeSdkEmbeddingOptions defaultOptions = DashScopeSdkEmbeddingOptions.builder()
			.model(DEFAULT_MODEL_NAME)
			.build();

		private MetadataMode metadataMode = MetadataMode.EMBED;

		private RetryTemplate retryTemplate = RetryUtils.DEFAULT_RETRY_TEMPLATE;

		private ObservationRegistry observationRegistry = ObservationRegistry.NOOP;

		private String apiKey;

		private String workspaceId;

		private Map<String, String> connectionHeaders = new HashMap<>();

		private Builder() {
		}

		private Builder(DashScopeSdkEmbeddingModel embeddingModel) {
			this.embeddingClient = embeddingModel.embeddingClient;
			this.defaultOptions = embeddingModel.defaultOptions;
			this.metadataMode = embeddingModel.metadataMode;
			this.retryTemplate = embeddingModel.retryTemplate;
			this.observationRegistry = embeddingModel.observationRegistry;
			this.apiKey = embeddingModel.apiKey;
			this.workspaceId = embeddingModel.workspaceId;
			this.connectionHeaders = new HashMap<>(embeddingModel.connectionHeaders);
		}

		public Builder embeddingClient(DashScopeSdkTextEmbeddingClient embeddingClient) {
			this.embeddingClient = embeddingClient;
			return this;
		}

		public Builder defaultOptions(DashScopeSdkEmbeddingOptions defaultOptions) {
			this.defaultOptions = defaultOptions;
			return this;
		}

		public Builder metadataMode(MetadataMode metadataMode) {
			this.metadataMode = metadataMode;
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

		public DashScopeSdkEmbeddingModel build() {
			return new DashScopeSdkEmbeddingModel(this.embeddingClient, this.defaultOptions, this.metadataMode,
					this.retryTemplate, this.observationRegistry, this.apiKey, this.workspaceId, this.connectionHeaders);
		}

	}

}
