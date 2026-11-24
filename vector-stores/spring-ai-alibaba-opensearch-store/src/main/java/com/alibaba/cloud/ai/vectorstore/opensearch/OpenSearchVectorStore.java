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
package com.alibaba.cloud.ai.vectorstore.opensearch;

import com.alibaba.cloud.ai.vectorstore.opensearch.OpenSearchApi.SimilarityResult;
import com.aliyun.ha3engine.vector.models.QueryRequest;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.BatchingStrategy;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.TokenCountBatchingStrategy;
import org.springframework.ai.observation.conventions.VectorStoreProvider;
import org.springframework.ai.observation.conventions.VectorStoreSimilarityMetric;
import org.springframework.ai.vectorstore.AbstractVectorStoreBuilder;
import org.springframework.ai.vectorstore.EmbeddedDocument;
import org.springframework.ai.vectorstore.SearchRequest;
import org.jspecify.annotations.Nullable;
import org.springframework.ai.vectorstore.observation.AbstractObservationVectorStore;
import org.springframework.ai.vectorstore.observation.VectorStoreObservationContext;
import org.springframework.ai.vectorstore.observation.VectorStoreObservationConvention;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.util.Assert;
import tools.jackson.databind.json.JsonMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Implementation of the VectorStore interface for Alibaba OpenSearch. This class provides
 * methods to add, delete, and perform similarity searches on documents in an OpenSearch
 * index.
 *
 * @author fuyou.lxm
 * @since 1.0.0-M3
 */
public class OpenSearchVectorStore extends AbstractObservationVectorStore implements InitializingBean {

	/**
	 * The field name for the document ID.
	 */
	private static final String ID_FIELD_NAME = "id";

	/**
	 * The field name for the document content.
	 */
	private static final String CONTENT_FIELD_NAME = "content";

	/**
	 * The field name for the document metadata.
	 */
	private static final String METADATA_FIELD_NAME = "metadata";

	/**
	 * The API client used to interact with OpenSearch.
	 */
	private final OpenSearchApi openSearchApi;

	/**
	 * Configuration options for the OpenSearch vector store.
	 */
	private final OpenSearchVectorStoreOptions options;

	/**
	 * Whether to initialize the schema for the vector store.
	 */
	private final boolean initializeSchema;

	/**
	 * The batching strategy used for processing documents.
	 */
	private final BatchingStrategy batchingStrategy;

	/**
	 * Converter that transforms a JSON object representing a document into a
	 * {@link OpenSearchApi.SimilarityResult} object.
	 */
	private final Converter<JsonNode, SimilarityResult> itemConverter = new SimilarityResultConverter();

	private final JsonMapper jsonMapper = JsonMapper.shared();

	/**
	 * Constructs a new instance of OpenSearchVectorStore with the specified parameters.
	 * @param openSearchApi The API client used to interact with OpenSearch.
	 * @param embeddingModel The embedding model used for vector operations.
	 * @param observationRegistry The observation registry for metrics.
	 * @param customObservationConvention Custom observation convention for metrics.
	 * @param batchingStrategy The batching strategy used for processing documents.
	 */
	public OpenSearchVectorStore(OpenSearchApi openSearchApi, EmbeddingModel embeddingModel,
			ObservationRegistry observationRegistry, VectorStoreObservationConvention customObservationConvention,
			BatchingStrategy batchingStrategy) {
		this(builder(openSearchApi, embeddingModel).observationRegistry(observationRegistry)
			.customObservationConvention(customObservationConvention)
			.batchingStrategy(batchingStrategy));
	}

	/**
	 * Constructs a new instance of OpenSearchVectorStore with the specified parameters.
	 * @param openSearchApi The API client used to interact with OpenSearch.
	 * @param embeddingModel The embedding model used for vector operations.
	 */
	public OpenSearchVectorStore(OpenSearchApi openSearchApi,
			EmbeddingModel embeddingModel) {
		this(builder(openSearchApi, embeddingModel));
	}

	/**
	 * Protected constructor for building instances of OpenSearchVectorStore using the
	 * Builder pattern.
	 * @param builder The builder instance containing configuration options.
	 */
	protected OpenSearchVectorStore(Builder builder) {
		super(builder);

		Assert.notNull(builder.openSearchApi, "The openSearchApi cannot be null");
		Assert.notNull(builder.options.getPrimaryKeyField(), "The primaryKeyField cannot be null");
		Assert.notNull(builder.options.getTableName(), "The tableName cannot be null");

		this.options = builder.options;
		this.initializeSchema = builder.initializeSchema;
		this.openSearchApi = builder.openSearchApi;
		this.batchingStrategy = builder.batchingStrategy;
	}

	/**
	 * Creates a new Builder instance for constructing OpenSearchVectorStore objects.
	 * @param openSearchApi The API client used to interact with OpenSearch.
	 * @param embeddingModel The embedding model used for vector operations.
	 * @return A new Builder instance.
	 */
	public static Builder builder(OpenSearchApi openSearchApi, EmbeddingModel embeddingModel) {
		return new Builder(openSearchApi, embeddingModel);
	}

	@Override
	public void doAdd(List<Document> documents) {
		for (Document document : documents) {
			this.openSearchApi.uploadDocument(this.options.getTableName(), this.options.getPrimaryKeyField(),
					List.of(toAddOperation(document, null)));
		}
	}

	@Override
	protected boolean supportsUpsert() {
		return true;
	}

	@Override
	protected void doUpsert(List<EmbeddedDocument> documents) {
		Assert.hasText(this.options.getVectorField(), "OpenSearch vector field must be configured");
		List<Map<String, ?>> rows = documents.stream().<Map<String, ?>>map(embedded -> {
			Document document = embedded.document();
			float[] embedding = embedded.embedding();
			checkDimensions(document.getId(), embedding.length, this.options.getDimensions());
			return toAddOperation(document, toVector(embedding));
		}).toList();
		this.openSearchApi.uploadDocument(this.options.getTableName(), this.options.getPrimaryKeyField(), rows);
	}

	private Map<String, ?> toAddOperation(Document document, @Nullable List<Float> embedding) {
		Map<String, Object> fields = new HashMap<>();
		fields.put(this.options.getPrimaryKeyField(), document.getId());
		fields.put(CONTENT_FIELD_NAME, document.getText());
		fields.put(METADATA_FIELD_NAME, this.jsonMapper.writeValueAsString(document.getMetadata()));
		if (embedding != null) {
			fields.put(this.options.getVectorField(), embedding);
		}
		return Map.of("cmd", "add", "fields", fields);
	}

	private static List<Float> toVector(float[] embedding) {
		var vector = new ArrayList<Float>(embedding.length);
		for (float value : embedding) {
			vector.add(value);
		}
		return vector;
	}

	/**
	 * Delete documents from the vector store.
	 */
	@Override
	public void doDelete(List<String> idList) {
		for (String id : idList) {
			List<Map<String, ?>> documentToDelete = new ArrayList<>();
			Map<String, Object> documentMap = new HashMap<>();
			Map<String, Object> documentFields = new HashMap<>();

			documentFields.put(this.options.getPrimaryKeyField(), id);
			documentMap.put("fields", documentFields);
			documentMap.put("cmd", "delete");
			documentToDelete.add(documentMap);

			openSearchApi.deleteDocument(this.options.getTableName(), this.options.getPrimaryKeyField(),
					documentToDelete);
		}
	}

	/**
	 * Perform a similarity search in the vector store.
	 * @param request The search request containing the query and parameters.
	 * @return list of documents
	 */
	@Override
	public List<Document> doSimilaritySearch(SearchRequest request) {
		Assert.notNull(request, "The search request must not be null.");
		QueryRequest query = queryRequest(request).setContent(request.getQuery()).setModal("text");
		return search(query, request, false);
	}

	@Override
	protected boolean supportsQueryEmbeddingSearch() {
		return true;
	}

	@Override
	protected List<Document> doSimilaritySearch(float[] queryEmbedding, SearchRequest request) {
		checkDimensions("query", queryEmbedding.length, this.options.getDimensions());
		if (request.hasFilterExpression()) {
			throw new UnsupportedOperationException("OpenSearch vector search does not support packed metadata filters");
		}
		boolean euclidean = "l2".equalsIgnoreCase(this.options.getSimilarityFunction())
				|| "SquaredEuclidean".equalsIgnoreCase(this.options.getSimilarityFunction());
		QueryRequest query = queryRequest(request).setVector(toVector(queryEmbedding))
			.setIndexName(this.options.getIndex()).setOrder(euclidean ? "ASC" : "DESC").setIncludeVector(false);
		return search(query, request, euclidean);
	}

	private QueryRequest queryRequest(SearchRequest request) {
		return new QueryRequest().setTableName(this.options.getTableName()).setTopK(request.getTopK())
			.setOutputFields(this.options.getOutputFields());
	}

	private List<Document> search(QueryRequest query, SearchRequest request, boolean euclidean) {
		var documents = new ArrayList<Document>();
		for (SimilarityResult result : this.openSearchApi.search(query, this.itemConverter)) {
			double score = euclidean ? 1.0 / (1.0 + result.score()) : result.score();
			if (score >= request.getSimilarityThreshold()) {
				documents.add(Document.builder().id(result.id()).text(result.content()).metadata(result.metadata())
					.score(score).build());
			}
		}
		return documents;
	}

	/**
	 * Create an ObservationContextBuilder for OpenSearchVectorStore.
	 * @return VectorStoreObservationContext.Builder
	 */
	@Override
	public VectorStoreObservationContext.Builder createObservationContextBuilder(String operationName) {
		return VectorStoreObservationContext.builder(VectorStoreProvider.OPENSEARCH.value(), operationName)
			.collectionName(this.options.getIndex())
			.dimensions(this.options.getDimensions())
			.similarityMetric(getSimilarityFunction());
	}

	private String getSimilarityFunction() {
		if ("cosinesimil".equalsIgnoreCase(this.options.getSimilarityFunction())) {
			return VectorStoreSimilarityMetric.COSINE.value();
		}
		else if ("l2".equalsIgnoreCase(this.options.getSimilarityFunction())) {
			return VectorStoreSimilarityMetric.EUCLIDEAN.value();
		}

		return this.options.getSimilarityFunction();
	}

	public boolean exists(String tableName) throws Exception {
		List<Object> indexList = openSearchApi.getIndexList(tableName);
		return indexList.contains("saa_default_index");
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (this.initializeSchema && !exists(this.options.getIndex())) {
			openSearchApi.createCollectionAndIndex(this.options.getMappingJson().formatted(this.options.getDimensions()));
		}
	}

	/**
	 * Get the native client.
	 * @return Client
	 */
	@Override
	public <T> Optional<T> getNativeClient() {
		@SuppressWarnings("unchecked")
		T client = (T) this.openSearchApi;
		return Optional.of(client);
	}

	/**
	 * A builder class for constructing instances of {@link OpenSearchVectorStore}. This
	 * class extends {@link AbstractVectorStoreBuilder} and provides methods to configure
	 * the options and batching strategy for the OpenSearch vector store.
	 */
	public static class Builder extends AbstractVectorStoreBuilder<Builder> {

		private final OpenSearchApi openSearchApi;

		private OpenSearchVectorStoreOptions options = new OpenSearchVectorStoreOptions();

		private boolean initializeSchema = false;

		private BatchingStrategy batchingStrategy = new TokenCountBatchingStrategy();

		/**
		 * Constructs a new instance of the Builder.
		 * @param openSearchApi The API client used to interact with OpenSearch.
		 * @param embeddingModel The embedding model used for vector operations.
		 */
		public Builder(OpenSearchApi openSearchApi,
				EmbeddingModel embeddingModel) {
			super(embeddingModel);
			this.openSearchApi = openSearchApi;
		}

		/**
		 * Sets the configuration options for the OpenSearch vector store.
		 * @param options The configuration options to set.
		 * @return The current Builder instance.
		 * @throws IllegalArgumentException if options is null.
		 */
		public Builder options(OpenSearchVectorStoreOptions options) {
			Assert.notNull(options, "options must not be null");
			this.options = options;
			return this;
		}

		public Builder initializeSchema(boolean initializeSchema) {
			this.initializeSchema = initializeSchema;
			return this;
		}

		/**
		 * Sets the batching strategy for the OpenSearch vector store.
		 * @param batchingStrategy The batching strategy to set.
		 * @return The current Builder instance.
		 * @throws IllegalArgumentException if batchingStrategy is null.
		 */
		public Builder batchingStrategy(BatchingStrategy batchingStrategy) {
			Assert.notNull(batchingStrategy, "BatchingStrategy must not be null");
			this.batchingStrategy = batchingStrategy;
			return this;
		}

		/**
		 * Builds and returns a new instance of {@link OpenSearchVectorStore} configured
		 * with the current settings.
		 * @return A new instance of OpenSearchVectorStore.
		 */
		@Override
		public OpenSearchVectorStore build() {
			return new OpenSearchVectorStore(this);
		}

	}

	/**
	 * A converter that transforms a JSON object representing a document into a
	 * {@link OpenSearchApi.SimilarityResult} object. This class is used to parse the
	 * response from an OpenSearch similarity search query and convert it into a more
	 * usable format.
	 */
	public static class SimilarityResultConverter
			implements Converter<JsonNode, SimilarityResult> {

		private static final String EMPTY_TEXT = "";

		private static final String FIELDS_KEY = "fields";

		private static final String SCORE_KEY = "score";

		/**
		 * Extracts the content from the JSON document.
		 * @param jsonDocument The JSON object containing the document details.
		 * @return The content of the document, or empty string if not found or empty.
		 */
		private static String extractContent(JsonNode jsonDocument) {
			if (jsonDocument.has(FIELDS_KEY)) {
				JsonNode fields = jsonDocument.get(FIELDS_KEY);
				String content = fields.path(CONTENT_FIELD_NAME).asString();
				if (content == null || content.isEmpty()) {
					return EMPTY_TEXT;
				}
				return content;
			}

			return EMPTY_TEXT;
		}

		/**
		 * Extracts the ID from the JSON document.
		 * @param jsonDocument The JSON object containing the document details.
		 * @return The ID of the document, or empty string if not found or empty.
		 */
		private static String extractId(JsonNode jsonDocument) {
			String id = jsonDocument.path(ID_FIELD_NAME).asString();
			if (id == null || id.isEmpty()) {
				return EMPTY_TEXT;
			}
			return id;
		}

		/**
		 * Extracts the score from the JSON document.
		 * @param jsonDocument The JSON object containing the document details.
		 * @return The score of the document.
		 */
		private static double extractScore(JsonNode jsonDocument) {
			return jsonDocument.path(SCORE_KEY).asDouble();
		}

		/**
		 * Extracts the metadata from the JSON document.
		 * @param jsonDocument The JSON object containing the document details.
		 * @return A map of metadata extracted from the document, or an empty map if not
		 * found.
		 */
		@SuppressWarnings("unchecked")
		private static Map<String, Object> extractMetadata(JsonNode jsonDocument) {
			if (jsonDocument.has(FIELDS_KEY)) {
				JsonNode fields = jsonDocument.get(FIELDS_KEY);
				String metadataStr = fields.path(METADATA_FIELD_NAME).asString();
				try {
					return JsonMapper.shared().readValue(metadataStr, HashMap.class);
				}
				catch (JacksonException e) {
					return new HashMap<>();
				}
			}
			else {
				return new HashMap<>();
			}
		}

		/**
		 * Parses a single JSON object representing a document into a
		 * {@link OpenSearchApi.SimilarityResult} object.
		 * @param jsonDocument The JSON object containing the document details.
		 * @return A {@link OpenSearchApi.SimilarityResult} object extracted from the JSON
		 * document.
		 */
		@Override
		public SimilarityResult convert(
				JsonNode jsonDocument) {
			String id = extractId(jsonDocument);
			String content = extractContent(jsonDocument);
			double score = extractScore(jsonDocument);
			Map<String, Object> metadata = extractMetadata(jsonDocument);

			return new SimilarityResult(id, score, content,
					metadata);
		}

	}

}
