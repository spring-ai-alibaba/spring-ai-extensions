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
package com.alibaba.cloud.ai.vectorstore.tair;

import com.aliyun.tair.tairvector.factory.VectorBuilderFactory;
import com.aliyun.tair.tairvector.factory.VectorBuilderFactory.KnnItem;
import tools.jackson.core.type.TypeReference;
import io.micrometer.observation.ObservationRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.BatchingStrategy;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.TokenCountBatchingStrategy;
import org.springframework.ai.vectorstore.AbstractVectorStoreBuilder;
import org.springframework.ai.vectorstore.EmbeddedDocument;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.observation.AbstractObservationVectorStore;
import org.springframework.ai.vectorstore.observation.VectorStoreObservationContext;
import org.springframework.ai.vectorstore.observation.VectorStoreObservationConvention;
import org.jspecify.annotations.Nullable;
import org.springframework.util.Assert;
import org.springframework.beans.factory.InitializingBean;
import tools.jackson.databind.json.JsonMapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Provides an API for interacting with Tair Vector, extending the functionality of the
 * {@link AbstractObservationVectorStore} class. This class manages vector operations
 * using a TairVectorApi and an EmbeddingModel.
 *
 * @author fuyou.lxm
 * @since 1.0.0-M3
 */
public class TairVectorStore extends AbstractObservationVectorStore implements InitializingBean {

	private static final Logger logger = LoggerFactory.getLogger(TairVectorStore.class);

	/**
	 * The field name for the document ID.
	 */
	protected static final String ID_FIELD_NAME = "id";

	/**
	 * The field name for the document content.
	 */
	protected static final String CONTENT_FIELD_NAME = "content";

	/**
	 * The field name for the document metadata.
	 */
	protected static final String METADATA_FIELD_NAME = "metadata";

	/**
	 * The API client used to interact with Tair.
	 */
	protected final TairVectorApi tairVectorApi;

	/**
	 * The embedding model used for vector operations.
	 */
	protected final EmbeddingModel embeddingModel;

	/**
	 * Configuration options for the Tair vector store.
	 */
	protected TairVectorStoreOptions options;

	private volatile @Nullable Integer indexDimensions;

	protected final BatchingStrategy batchingStrategy;

	private final JsonMapper jsonMapper = JsonMapper.shared();

	private final boolean initializeSchema;

	/**
	 * Constructs a new instance of TairVectorStore with the specified parameters.
	 * @param tairVectorApi The API client used to interact with Tair.
	 * @param embeddingModel The embedding model used for vector operations.
	 * @param observationRegistry The observation registry for metrics.
	 * @param customObservationConvention Custom observation convention for metrics.
	 * @param batchingStrategy The batching strategy used for processing documents.
	 */
	public TairVectorStore(TairVectorApi tairVectorApi, EmbeddingModel embeddingModel,
			ObservationRegistry observationRegistry, VectorStoreObservationConvention customObservationConvention,
			BatchingStrategy batchingStrategy) {
		this(builder(tairVectorApi, embeddingModel).observationRegistry(observationRegistry)
			.customObservationConvention(customObservationConvention)
			.batchingStrategy(batchingStrategy));
	}

	/**
	 * Constructs a new instance of TairVectorStore with the specified parameters.
	 * @param tairVectorApi The API client used to interact with Tair.
	 * @param embeddingModel The embedding model used for vector operations.
	 */
	public TairVectorStore(TairVectorApi tairVectorApi, EmbeddingModel embeddingModel) {
		this(builder(tairVectorApi, embeddingModel));
	}

	/**
	 * Initializes a new instance of the {@link TairVectorStore} class.
	 * @param builder The builder containing the necessary configuration for the
	 * TairVectorStore.
	 */
	protected TairVectorStore(Builder builder) {
		super(builder);

		Assert.notNull(builder.tairVectorApi, "The tairVectorClient cannot be null");

		this.options = builder.options;
		this.tairVectorApi = builder.tairVectorApi;
		this.embeddingModel = builder.getEmbeddingModel();
		this.batchingStrategy = builder.batchingStrategy;
		this.initializeSchema = builder.initializeSchema;
	}

	/**
	 * Creates a new builder for the {@link TairVectorStore} class.
	 * @param tairVectorApi The TairVectorApi instance to be used.
	 * @param embeddingModel The EmbeddingModel instance to be used.
	 * @return A new builder instance.
	 */
	public static Builder builder(TairVectorApi tairVectorApi, EmbeddingModel embeddingModel) {
		return new Builder(tairVectorApi, embeddingModel);
	}

	@Override
	public void afterPropertiesSet() {
		if (!this.initializeSchema) {
			return;
		}

		Map<String, String> indexInfo = this.tairVectorApi.tvsgetindex(this.options.getIndexName());
		if (indexInfo != null && !indexInfo.isEmpty()) {
			if (this.options.getConfiguredDimensions() == null) {
				this.indexDimensions = readDimensions(indexInfo);
			}
			return;
		}

		try {
			tairVectorApi.tvscreateindex(
				options.getIndexName(),
				options.getDimensions(),
				options.getIndexAlgorithm(),
				options.getDistanceMethod(),
				options.getIndexParams().toArray(new String[0])
			);
			this.indexDimensions = this.options.getDimensions();
			logger.debug("Created Tair vector index: {}", options.getIndexName());
		}
		catch (Exception e) {
			throw new RuntimeException("Failed to create Tair vector index", e);
		}
	}

	private synchronized int getVectorDimensions() {
		Integer configured = this.options.getConfiguredDimensions();
		if (configured != null) {
			return configured;
		}
		if (this.indexDimensions == null) {
			this.indexDimensions = readDimensions(this.tairVectorApi.tvsgetindex(this.options.getIndexName()));
		}
		return this.indexDimensions;
	}

	private int readDimensions(@Nullable Map<String, String> indexInfo) {
		String dimension = indexInfo != null ? indexInfo.get("dimension") : null;
		Assert.state(dimension != null, "Cannot determine vector dimensions for Tair index " + this.options.getIndexName());
		int dimensions = Integer.parseInt(dimension);
		Assert.state(dimensions > 0 && dimensions <= 32768,
				"Invalid vector dimension for Tair index " + this.options.getIndexName());
		return dimensions;
	}

	@Override
	public void doAdd(List<Document> documents) {
		Objects.requireNonNull(documents, "Documents list cannot be null");
		if (documents.isEmpty()) {
			throw new IllegalArgumentException("Documents list cannot be empty");
		}

		for (Document document : documents) {
			logger.info("Calling EmbeddingModel for document id = {}", document.getId());
			writeDocument(document, this.embeddingModel.embed(document));
		}
	}

	@Override
	protected boolean supportsUpsert() {
		return true;
	}

	@Override
	protected void doUpsert(List<EmbeddedDocument> documents) {
		int dimensions = getVectorDimensions();
		for (EmbeddedDocument embedded : documents) {
			checkDimensions(embedded.document().getId(), embedded.embedding().length, dimensions);
		}
		for (EmbeddedDocument embedded : documents) {
			writeDocument(embedded.document(), embedded.embedding());
		}
	}

	private void writeDocument(Document document, float[] embedding) {
		String embeddingString = this.jsonMapper.writeValueAsString(embedding);
		String metadata = this.jsonMapper.writeValueAsString(document.getMetadata());
		this.tairVectorApi.tvshset(this.options.getIndexName(), document.getId(), embeddingString,
				ID_FIELD_NAME, document.getId(), CONTENT_FIELD_NAME, document.getText(), METADATA_FIELD_NAME, metadata);
	}

	@Override
	public void doDelete(List<String> idList) {
		throw new UnsupportedOperationException("delete is not supported");
	}

	@Override
	public List<Document> doSimilaritySearch(SearchRequest request) {
		return search(getUserQueryEmbedding(request.getQuery()), request.getTopK()).stream()
			.filter(item -> item.getScore() >= request.getSimilarityThreshold())
			.map(this::mapToDocument)
			.limit(request.getTopK())
			.toList();
	}

	@Override
	protected boolean supportsQueryEmbeddingSearch() {
		return true;
	}

	@Override
	protected List<Document> doSimilaritySearch(float[] queryEmbedding, SearchRequest request) {
		checkDimensions("query", queryEmbedding.length, getVectorDimensions());
		if (request.hasFilterExpression()) {
			throw new UnsupportedOperationException("Tair vector search does not support metadata filters");
		}
		var documents = new ArrayList<Document>();
		for (KnnItem<String> item : search(queryEmbedding, request.getTopK())) {
			double distance = item.getScore();
			double similarity = switch (this.options.getDistanceMethod()) {
				case L2 -> 1.0 / (1.0 + distance);
				case IP, COSINE, JACCARD -> 1.0 - distance;
			};
			if (similarity >= request.getSimilarityThreshold()) {
				documents.add(mapToDocument(item).mutate().score(similarity).build());
			}
			if (documents.size() >= request.getTopK()) {
				break;
			}
		}
		return documents;
	}

	private Collection<KnnItem<String>> search(float[] queryEmbedding, int topK) {
		String embeddingString = this.jsonMapper.writeValueAsString(queryEmbedding);
		VectorBuilderFactory.Knn<String> result = this.tairVectorApi.tvsknnsearch(this.options.getIndexName(),
				(long) topK, embeddingString);
		return result.getKnnResults();
	}

	/**
	 * Retrieves a document from the vector store based on a KnnItem.
	 * @param item The KnnItem containing the document ID.
	 * @return The document corresponding to the KnnItem.
	 */
	protected Document mapToDocument(KnnItem<String> item) {
		List<String> detail = this.tairVectorApi.tvshmget(options.getIndexName(), item.getId(), ID_FIELD_NAME,
				CONTENT_FIELD_NAME, METADATA_FIELD_NAME);
		String id = detail.get(0);
		String content = detail.get(1);
		String metadataStr = detail.get(2);
		Map<String, Object> metaData = jsonMapper.readValue(metadataStr, new TypeReference<Map<String, Object>>() {
        });
		return new Document(id, content, metaData);
	}

	/**
	 * Generates an embedding for a user query.
	 * @param query The user query string.
	 * @return The embedding for the user query.
	 */
	protected float[] getUserQueryEmbedding(String query) {
		return this.embeddingModel.embed(query);
	}

	/**
	 * Creates a VectorStoreObservationContext.Builder for Tair vector operations.
	 * @param operationName The operation name (e.g., "add", "search", "delete")
	 * @return A configured VectorStoreObservationContext.Builder instance
	 */
	@Override
	public VectorStoreObservationContext.Builder createObservationContextBuilder(String operationName) {
		var builder = VectorStoreObservationContext.builder("tair", operationName)
			.collectionName(this.options.getIndexName())
			.similarityMetric(this.options.getDistanceMethod().name());
		Integer dimensions = this.options.getConfiguredDimensions();
		if (dimensions == null) {
			dimensions = this.indexDimensions;
		}
		if (dimensions != null) {
			builder.dimensions(dimensions);
		}
		return builder;
	}

	/**
	 * Builder class for constructing {@link TairVectorStore} instances.
	 */
	public static class Builder extends AbstractVectorStoreBuilder<Builder> {

		protected TairVectorStoreOptions options = new TairVectorStoreOptions();

		protected final TairVectorApi tairVectorApi;

		protected BatchingStrategy batchingStrategy = new TokenCountBatchingStrategy();

		protected boolean initializeSchema = false;

		/**
		 * Initializes a new instance of the {@link Builder} class.
		 * @param tairVectorApi The TairVectorApi instance to be used.
		 * @param embeddingModel The EmbeddingModel instance to be used.
		 */
		public Builder(TairVectorApi tairVectorApi, EmbeddingModel embeddingModel) {
			super(embeddingModel);
			this.tairVectorApi = tairVectorApi;
		}

		/**
		 * Sets the Tair vector store options.
		 * @param options The vector store options to use.
		 * @return The builder instance.
		 * @throws IllegalArgumentException if options is null.
		 */
		public Builder options(TairVectorStoreOptions options) {
			Assert.notNull(options, "options must not be null");
			this.options = options;
			return this;
		}

		/**
		 * Sets the batching strategy.
		 * @param batchingStrategy The strategy to use.
		 * @return The builder instance.
		 */
		public Builder batchingStrategy(BatchingStrategy batchingStrategy) {
			Assert.notNull(batchingStrategy, "BatchingStrategy must not be null");
			this.batchingStrategy = batchingStrategy;
			return this;
		}

		/**
		 * Sets whether to initialize the schema (create index) on startup.
		 * @param initializeSchema true to initialize schema, false otherwise.
		 * @return The builder instance.
		 */
		public Builder initializeSchema(boolean initializeSchema) {
			this.initializeSchema = initializeSchema;
			return this;
		}

		/**
		 * Builds the TairVectorStore instance.
		 * @return A new TairVectorStore instance.
		 * @throws IllegalStateException if the builder is in an invalid state.
		 */
		@Override
		public TairVectorStore build() {
			return new TairVectorStore(this);
		}

	}

}
