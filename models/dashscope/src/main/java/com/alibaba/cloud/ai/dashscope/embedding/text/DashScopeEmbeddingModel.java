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
package com.alibaba.cloud.ai.dashscope.embedding.text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.common.DashScopeApiConstants;
import com.alibaba.cloud.ai.dashscope.spec.DashScopeApiSpec;
import com.alibaba.cloud.ai.dashscope.spec.DashScopeModel.EmbeddingModel;
import io.micrometer.observation.ObservationRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.metadata.DefaultUsage;
import org.springframework.ai.chat.metadata.EmptyUsage;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.embedding.AbstractEmbeddingModel;
import org.springframework.ai.embedding.BatchingStrategy;
import org.springframework.ai.embedding.Embedding;
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

/**
 * DashScope Embedding Model implementation.
 *
 * @author nuocheng.lxm
 * @author why_ohh
 * @author yuluo
 * @author <a href="mailto:550588941@qq.com">why_ohh</a>
 * @author yyyhhx
 * @author guanxu
 * @since 2024/7/31 10:57
 */
public class DashScopeEmbeddingModel extends AbstractEmbeddingModel {

	private static final Logger logger = LoggerFactory.getLogger(DashScopeEmbeddingModel.class);

	/**
	 * Metadata key for sparse embeddings stored in {@link EmbeddingResponseMetadata}. The
	 * associated value is a {@code Map<Integer,
	 * List<DashScopeApiSpec.SparseEmbeddingItem>>} keyed by DashScope {@code text_index}.
	 */
	public static final String SPARSE_EMBEDDINGS_METADATA = "sparse-embeddings";

	private static final EmbeddingModelObservationConvention DEFAULT_OBSERVATION_CONVENTION = new DefaultEmbeddingModelObservationConvention();

    /**
     * Known embedding dimensions for DashScope models.
     */
    private static final Map<String, Integer> KNOWN_EMBEDDING_DIMENSIONS = Map.of(
            EmbeddingModel.EMBEDDING_V1.getValue(), 1536, EmbeddingModel.EMBEDDING_V2.getValue(), 1536,
            EmbeddingModel.EMBEDDING_V3.getValue(), 1024, EmbeddingModel.EMBEDDING_V4.getValue(), 1024);

	private final DashScopeEmbeddingOptions defaultOptions;

	private final RetryTemplate retryTemplate;

	private final DashScopeApi dashScopeApi;

	private final MetadataMode metadataMode;

	/**
	 * Observation registry used for instrumentation.
	 */
	private final ObservationRegistry observationRegistry;

	/**
	 * Conventions to use for generating observations.
	 */
	private EmbeddingModelObservationConvention observationConvention = DEFAULT_OBSERVATION_CONVENTION;

	public DashScopeEmbeddingModel(DashScopeApi dashScopeApi) {
		this(dashScopeApi, MetadataMode.EMBED);
	}

	public DashScopeEmbeddingModel(DashScopeApi dashScopeApi, MetadataMode metadataMode) {
		this(dashScopeApi, metadataMode,
				DashScopeEmbeddingOptions.builder()
					.model(DashScopeApi.DEFAULT_EMBEDDING_MODEL)
					.textType(DashScopeApi.DEFAULT_EMBEDDING_TEXT_TYPE)
					.build());
	}

	public DashScopeEmbeddingModel(DashScopeApi dashScopeApi, MetadataMode metadataMode,
			DashScopeEmbeddingOptions dashScopeEmbeddingOptions) {
		this(dashScopeApi, metadataMode, dashScopeEmbeddingOptions, RetryUtils.DEFAULT_RETRY_TEMPLATE);
	}

	public DashScopeEmbeddingModel(DashScopeApi dashScopeApi, MetadataMode metadataMode,
			DashScopeEmbeddingOptions dashScopeEmbeddingOptions, RetryTemplate retryTemplate) {
		this(dashScopeApi, metadataMode, dashScopeEmbeddingOptions, retryTemplate, ObservationRegistry.NOOP);
	}

	public DashScopeEmbeddingModel(DashScopeApi dashScopeApi, MetadataMode metadataMode,
			DashScopeEmbeddingOptions options, RetryTemplate retryTemplate, ObservationRegistry observationRegistry) {
		Assert.notNull(dashScopeApi, "DashScopeApi must not be null");
		Assert.notNull(metadataMode, "metadataMode must not be null");
		Assert.notNull(options, "options must not be null");
		Assert.notNull(retryTemplate, "retryTemplate must not be null");
		Assert.notNull(observationRegistry, "observationRegistry must not be null");

		this.dashScopeApi = dashScopeApi;
		this.metadataMode = metadataMode;
		this.defaultOptions = options;
		this.retryTemplate = retryTemplate;
		this.observationRegistry = observationRegistry;
	}

	@Override
	public float[] embed(Document document) {
		Assert.notNull(document, "Document must not be null");
		return this.embed(document.getFormattedContent(this.metadataMode));
	}

	@Override
	public EmbeddingResponse call(EmbeddingRequest request) {
		// Before moving any further, build the final request EmbeddingRequest,
		// merging runtime and default options.
		EmbeddingRequest embeddingRequest = buildEmbeddingRequest(request);

        DashScopeApiSpec.EmbeddingRequest apiRequest = createRequest(embeddingRequest);

		var observationContext = EmbeddingModelObservationContext.builder()
			.embeddingRequest(embeddingRequest)
			.provider(DashScopeApiConstants.PROVIDER_NAME)
			.build();

		return Objects.requireNonNull(EmbeddingModelObservationDocumentation.EMBEDDING_MODEL_OPERATION
                .observation(this.observationConvention, DEFAULT_OBSERVATION_CONVENTION, () -> observationContext,
                        this.observationRegistry)
                .observe(() -> {
                    DashScopeApiSpec.EmbeddingList apiEmbeddingResponse = this.retryTemplate.execute(ctx -> {
                        try {
                            return this.dashScopeApi.embeddings(apiRequest).getBody();
                        } catch (Exception e) {
                            logger.error("Error embedding request: {}", request.getInstructions(), e);
                            throw e;
                        }
                    });

                    if (apiEmbeddingResponse == null) {
                        logger.warn("No embeddings returned for request: {}", request);
                        return new EmbeddingResponse(List.of());
                    }

                    if (apiEmbeddingResponse.message() != null) {
                        logger.error("Error message returned for request: {}", apiEmbeddingResponse.message());
                        throw new RuntimeException("Embedding failed: error code:" + apiEmbeddingResponse.code()
                                + ", message:" + apiEmbeddingResponse.message());
                    }

                    DashScopeApiSpec.EmbeddingUsage usage = apiEmbeddingResponse.usage();

                    Usage embeddingUsage = usage != null ? this.getDefaultUsage(usage) : new EmptyUsage();

					Map<Integer, List<DashScopeApiSpec.SparseEmbeddingItem>> sparseEmbeddings = new HashMap<>();
					List<Embedding> embeddings = new ArrayList<>();
					if (apiEmbeddingResponse.output() != null && apiEmbeddingResponse.output().embeddings() != null) {
						for (DashScopeApiSpec.Embedding embedding : apiEmbeddingResponse.output().embeddings()) {
							int textIndex = embedding.textIndex() != null ? embedding.textIndex() : embeddings.size();
							float[] denseEmbedding = embedding.embedding() != null ? embedding.embedding() : new float[0];
							embeddings.add(new Embedding(denseEmbedding, textIndex));
							if (embedding.sparseEmbedding() != null && !embedding.sparseEmbedding().isEmpty()) {
								sparseEmbeddings.put(textIndex, embedding.sparseEmbedding());
							}
						}
					}
					var metadata = generateResponseMetadata(apiRequest.model(), embeddingUsage, sparseEmbeddings);

					EmbeddingResponse embeddingResponse = new EmbeddingResponse(embeddings, metadata);

                    observationContext.setResponse(embeddingResponse);

                    return embeddingResponse;
                }));
	}

	private DefaultUsage getDefaultUsage(DashScopeApiSpec.EmbeddingUsage usage) {
		return new DefaultUsage(usage.getPromptTokens(), usage.getCompletionTokens(), usage.getTotalTokens(), usage);
	}

	private EmbeddingRequest buildEmbeddingRequest(EmbeddingRequest embeddingRequest) {
		DashScopeEmbeddingOptions requestOptions = mergeOptions(embeddingRequest.getOptions());
		return new EmbeddingRequest(embeddingRequest.getInstructions(), requestOptions);
	}

	private DashScopeEmbeddingOptions mergeOptions(EmbeddingOptions options) {
		DashScopeEmbeddingOptions runtimeOptions = null;
		if (options != null) {
			runtimeOptions = ModelOptionsUtils.copyToTarget(options, EmbeddingOptions.class,
					DashScopeEmbeddingOptions.class);
		}

		return runtimeOptions == null ? this.defaultOptions
				: DashScopeEmbeddingOptions.builder()
					.model(ModelOptionsUtils.mergeOption(runtimeOptions.getModel(), this.defaultOptions.getModel()))
					.dimensions(ModelOptionsUtils.mergeOption(runtimeOptions.getDimensions(),
							this.defaultOptions.getDimensions()))
					.textType(ModelOptionsUtils.mergeOption(runtimeOptions.getTextType(),
							this.defaultOptions.getTextType()))
					.outputType(ModelOptionsUtils.mergeOption(runtimeOptions.getOutputType(),
							this.defaultOptions.getOutputType()))
					.build();
	}

	private DashScopeApiSpec.EmbeddingRequest createRequest(EmbeddingRequest request) {
		DashScopeEmbeddingOptions requestOptions = (DashScopeEmbeddingOptions) request.getOptions();
		return DashScopeApiSpec.EmbeddingRequest.builder()
			.model(requestOptions.getModel())
			.texts(request.getInstructions())
			.textType(requestOptions.getTextType())
			.dimension(requestOptions.getDimensions())
			.outputType(requestOptions.getOutputType())
			.build();
	}

	/**
	 * Dense embedding convenience APIs cannot return sparse-only outputs because their
	 * contract is fixed to {@code float[]} results. Callers that need sparse embeddings
	 * must use {@link #call(EmbeddingRequest)} instead.
	 */
	private void validateDenseEmbeddingApiOutputType(DashScopeEmbeddingOptions options) {
		if (DashScopeEmbeddingOptions.OUTPUT_TYPE_SPARSE.equalsIgnoreCase(options.getOutputType())) {
			throw new IllegalStateException(
					"DashScope sparse-only output cannot be returned from dense embedding APIs. Use call() to access sparse embeddings.");
		}
	}

	private EmbeddingResponseMetadata generateResponseMetadata(String model, Usage usage,
			Map<Integer, List<DashScopeApiSpec.SparseEmbeddingItem>> sparseEmbeddings) {
		Map<String, Object> map = new HashMap<>();
		map.put("model", model);
		map.put("total-tokens", usage.getTotalTokens());
		if (!sparseEmbeddings.isEmpty()) {
			map.put(SPARSE_EMBEDDINGS_METADATA, sparseEmbeddings);
		}

		return new EmbeddingResponseMetadata(model, usage, map);
	}

	/**
	 * Use the provided convention for reporting observation data
	 * @param observationConvention The provided convention
	 */
	public void setObservationConvention(EmbeddingModelObservationConvention observationConvention) {
		Assert.notNull(observationConvention, "observationConvention cannot be null");
		this.observationConvention = observationConvention;
	}

	/**
	 * Embed the provided texts and return the embeddings.
	 * @return The embeddings
	 */
	@Override
	public List<float[]> embed(List<String> texts) {
		Assert.notNull(texts, "Texts must not be null");
		validateDenseEmbeddingApiOutputType(this.defaultOptions);
		return this.call(new EmbeddingRequest(texts, defaultOptions))
			.getResults()
			.stream()
			.map(Embedding::getOutput)
			.toList();
	}

	/**
	 * Embed the provided documents and return the embeddings.
	 * @return The embeddings
	 */
	@Override
	public List<float[]> embed(List<Document> documents, EmbeddingOptions options, BatchingStrategy batchingStrategy) {
		DashScopeEmbeddingOptions requestOptions = mergeOptions(options);
		validateDenseEmbeddingApiOutputType(requestOptions);
		return super.embed(documents, requestOptions, batchingStrategy);
	}

	/**
	 * Embed the provided documents and return the response.
	 * @return The embedding response
	 */
	@Override
	public EmbeddingResponse embedForResponse(List<String> texts) {
		Assert.notNull(texts, "Texts must not be null");
		return this.call(new EmbeddingRequest(texts, defaultOptions));
	}

    @Override
    public int dimensions() {
        if (KNOWN_EMBEDDING_DIMENSIONS.containsKey(this.defaultOptions.getModel())) {
            return KNOWN_EMBEDDING_DIMENSIONS.get(this.defaultOptions.getModel());
        }
        return super.dimensions();
    }

    /**
     * Returns a builder pre-populated with the current configuration for mutation.
     */
    public Builder mutate() {
        return new Builder(this);
    }

    @Override
    public DashScopeEmbeddingModel clone() {
        return this.mutate().build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private DashScopeApi dashScopeApi;

        private MetadataMode metadataMode = MetadataMode.EMBED;

        private DashScopeEmbeddingOptions defaultOptions = DashScopeEmbeddingOptions.builder()
					.model(DashScopeApi.DEFAULT_EMBEDDING_MODEL)
					.textType(DashScopeApi.DEFAULT_EMBEDDING_TEXT_TYPE)
					.build();

        private RetryTemplate retryTemplate = RetryUtils.DEFAULT_RETRY_TEMPLATE;

        private ObservationRegistry observationRegistry = ObservationRegistry.NOOP;

        private Builder() {
        }

        private Builder(DashScopeEmbeddingModel embeddingModel) {
            this.dashScopeApi = embeddingModel.dashScopeApi;
            this.metadataMode = embeddingModel.metadataMode;
            this.defaultOptions = embeddingModel.defaultOptions;
            this.retryTemplate = embeddingModel.retryTemplate;
            this.observationRegistry = embeddingModel.observationRegistry;
        }

        public Builder dashScopeApi(DashScopeApi dashScopeApi) {
            this.dashScopeApi = dashScopeApi;
            return this;
        }

        public Builder metadataMode(MetadataMode metadataMode) {
            this.metadataMode = metadataMode;
            return this;
        }

        public Builder defaultOptions(DashScopeEmbeddingOptions defaultOptions) {
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

        public DashScopeEmbeddingModel build() {
            return new DashScopeEmbeddingModel(this.dashScopeApi, this.metadataMode, this.defaultOptions,
                    this.retryTemplate, this.observationRegistry);
        }
    }

}
