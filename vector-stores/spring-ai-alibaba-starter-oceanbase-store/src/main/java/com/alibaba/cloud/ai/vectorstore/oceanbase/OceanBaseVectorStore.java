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
package com.alibaba.cloud.ai.vectorstore.oceanbase;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingOptions;
import org.springframework.ai.util.JacksonUtils;
import org.springframework.ai.vectorstore.AbstractVectorStoreBuilder;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionConverter;
import org.springframework.ai.vectorstore.observation.AbstractObservationVectorStore;
import org.springframework.ai.vectorstore.observation.VectorStoreObservationContext;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.stream.IntStream;

import static org.springframework.ai.vectorstore.SearchRequest.DEFAULT_TOP_K;

/**
 * OceanBase Vector Store implementation with hybrid search support.
 * Supports vector similarity search, vector+fulltext hybrid search, and vector+filter search.
 */
public class OceanBaseVectorStore extends AbstractObservationVectorStore implements InitializingBean {

	private static final Logger logger = LoggerFactory.getLogger(OceanBaseVectorStore.class);

	private static final String DATA_BASE_SYSTEM = "oceanbase";

	private static final String ID_FIELD = "id";
	private static final String EMBEDDING_FIELD = "embedding";
	private static final String DOCUMENT_FIELD = "document";
	private static final String METADATA_FIELD = "metadata";
	private static final String TIMESTAMP_FIELD = "timestamp";

	private static final Double DEFAULT_SIMILARITY_THRESHOLD = 0.0;
	private static final int DEFAULT_DIMENSION = 384;

	public static final String HYBRID_SEARCH_TYPE_FULLTEXT = "fulltext";

	public static final String INDEX_TYPE_HNSW = "HNSW";
	public static final String INDEX_TYPE_IVF = "IVF";
	public static final String INDEX_TYPE_FLAT = "FLAT";

	public static final String METRIC_TYPE_L2 = "l2";
	public static final String METRIC_TYPE_COSINE = "cosine";
	public static final String METRIC_TYPE_INNER_PRODUCT = "inner_product";

	private static final String DISTANCE_FUNCTION_L2 = "l2_distance";
	private static final String DISTANCE_FUNCTION_COSINE = "cosine_distance";
	private static final String DISTANCE_FUNCTION_INNER_PRODUCT = "inner_product";

	public final FilterExpressionConverter filterExpressionConverter = new OceanBaseVectorFilterExpressionConverter();

	private final String tableName;
	private final Integer defaultTopK;
	private final Double defaultSimilarityThreshold;
	private final DataSource dataSource;
	private final ObjectMapper objectMapper;
	private final Integer dimension;
	private final String hybridSearchType;
	private final String indexType;
	private final String indexMetricType;
	private final String indexName;
	private final String fulltextIndexName;
	private final boolean enableFulltext;

	protected OceanBaseVectorStore(Builder builder) {
		super(builder);
		this.tableName = builder.tableName;
		this.dataSource = builder.dataSource;
		this.objectMapper = JsonMapper.builder()
			.addModules(JacksonUtils.instantiateAvailableModules())
			.build();
		this.defaultSimilarityThreshold = builder.defaultSimilarityThreshold;
		this.defaultTopK = builder.defaultTopK;
		this.dimension = builder.dimension;
		this.hybridSearchType = builder.hybridSearchType;
		this.indexType = builder.indexType;
		this.indexMetricType = builder.indexMetricType;
		this.indexName = generateVectorIndexName(builder.tableName);
		this.fulltextIndexName = generateFulltextIndexName(builder.tableName);
		this.enableFulltext = HYBRID_SEARCH_TYPE_FULLTEXT.equalsIgnoreCase(hybridSearchType);
	}

	public static Builder builder(String tableName, DataSource dataSource, EmbeddingModel embeddingModel) {
		return new Builder(tableName, dataSource, embeddingModel);
	}

	/**
	 * Generate vector index name based on table name.
	 * Format: {tableName}_vidx
	 */
	private static String generateVectorIndexName(String tableName) {
		return tableName.toLowerCase() + "_vidx";
	}

	/**
	 * Generate fulltext index name based on table name.
	 * Format: {tableName}_fts_idx
	 */
	private static String generateFulltextIndexName(String tableName) {
		return tableName.toLowerCase() + "_fts_idx";
	}

	@Override
	public void afterPropertiesSet() {
		initializeDatabase();
	}

	private void initializeDatabase() {
		int vectorDimension = dimension != null ? dimension :
			(this.embeddingModel != null ? this.embeddingModel.dimensions() : DEFAULT_DIMENSION);

		String createTableSql = buildCreateTableSql(vectorDimension);
		executeUpdate(createTableSql);
		createVectorIndex(vectorDimension);

		if (enableFulltext) {
			createFulltextIndex();
		}

		logger.debug("Successfully created or verified table: {}", tableName);
	}

	private String buildCreateTableSql(int vectorDimension) {
		StringBuilder sql = new StringBuilder();
		sql.append("CREATE TABLE IF NOT EXISTS ").append(tableName).append(" (");
		sql.append(ID_FIELD).append(" BIGINT AUTO_INCREMENT PRIMARY KEY, ");
		sql.append(EMBEDDING_FIELD).append(" VECTOR(").append(vectorDimension).append(") NOT NULL, ");
		sql.append(DOCUMENT_FIELD).append(" LONGTEXT, ");
		sql.append(METADATA_FIELD).append(" JSON, ");
		sql.append(TIMESTAMP_FIELD).append(" TIMESTAMP DEFAULT CURRENT_TIMESTAMP");
		sql.append(")");

		return sql.toString();
	}

	private void createVectorIndex(int vectorDimension) {
		if (checkIndexExists(indexName)) {
			logger.debug("Vector index {} already exists, skipping creation", indexName);
			return;
		}

		if (hasVectorIndexOnColumn()) {
			logger.debug("Column {} already has a vector index, skipping creation", EMBEDDING_FIELD);
			return;
		}

		try {
			String distanceFunc = getDistanceFunction(indexMetricType);
			String indexSql = String.format(
				"CREATE VECTOR INDEX %s ON %s (%s) WITH (distance=%s, type=%s)",
				indexName, tableName, EMBEDDING_FIELD, distanceFunc, indexType.toLowerCase()
			);
			executeUpdate(indexSql);
			logger.debug("Created vector index: {}", indexName);
		}
		catch (Exception e) {
			String errorMessage = getErrorMessage(e);
			String lowerErrorMessage = errorMessage != null ? errorMessage.toLowerCase() : "";

			if (lowerErrorMessage.contains("has vector index")
					|| lowerErrorMessage.contains("duplicate")
					|| lowerErrorMessage.contains("already exists")
					|| lowerErrorMessage.contains("add index failed")) {
				if (checkIndexExists(indexName) || hasVectorIndexOnColumn()) {
					logger.debug("Vector index already exists on column {}, skipping creation", EMBEDDING_FIELD);
					return;
				}
				logger.debug("Vector index may already exist (error: {}), continuing without creating", errorMessage);
				return;
			}

			if (checkIndexExists(indexName) || hasVectorIndexOnColumn()) {
				logger.debug("Vector index exists after failed creation attempt, skipping creation", EMBEDDING_FIELD);
				return;
			}
			logger.warn("Failed to create vector index: {}", errorMessage != null ? errorMessage : e.getMessage());
		}
	}

	private void createFulltextIndex() {
		if (checkIndexExists(fulltextIndexName)) {
			logger.debug("Fulltext index {} already exists, skipping creation", fulltextIndexName);
			return;
		}

		if (hasFulltextIndexOnColumn()) {
			logger.debug("Column {} already has a fulltext index, skipping creation", DOCUMENT_FIELD);
			return;
		}

		try {
			String indexSql = String.format(
				"CREATE FULLTEXT INDEX %s ON %s (%s) WITH PARSER ngram",
				fulltextIndexName, tableName, DOCUMENT_FIELD
			);
			executeUpdate(indexSql);
			logger.debug("Created fulltext index: {}", fulltextIndexName);
		}
		catch (Exception e) {
			String errorMessage = getErrorMessage(e);
			if (errorMessage != null && errorMessage.toLowerCase().contains("duplicate key name")) {
				logger.debug("Fulltext index {} already exists, skipping creation", fulltextIndexName);
				return;
			}
			logger.warn("Failed to create fulltext index: {}", errorMessage != null ? errorMessage : e.getMessage());
		}
	}

	private boolean checkIndexExists(String indexName) {
		try (Connection connection = dataSource.getConnection();
				PreparedStatement pstmt = connection.prepareStatement(
					"SHOW INDEX FROM `" + tableName + "` WHERE Key_name = ?")) {
			pstmt.setString(1, indexName);
			ResultSet rs = pstmt.executeQuery();
			boolean exists = rs.next();
			if (exists) {
				logger.debug("Index {} already exists on table {}", indexName, tableName);
			}
			return exists;
		}
		catch (SQLException e) {
			logger.debug("Failed to check index existence for {}: {}, will attempt to create", indexName, e.getMessage());
			return hasVectorIndexOnColumn();
		}
	}

	/**
	 * Check if the embedding column already has a vector index (regardless of name).
	 */
	private boolean hasVectorIndexOnColumn() {
		try (Connection connection = dataSource.getConnection();
				PreparedStatement pstmt = connection.prepareStatement(
					"SHOW CREATE TABLE `" + tableName + "`")) {
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				String createTableSql = rs.getString(2);
				if (createTableSql != null) {
					String lowerSql = createTableSql.toLowerCase();
					boolean hasVectorIndex = lowerSql.contains("vector index")
							&& lowerSql.contains(EMBEDDING_FIELD.toLowerCase());
					if (hasVectorIndex) {
						logger.debug("Found vector index on column {} in table {}", EMBEDDING_FIELD, tableName);
					}
					return hasVectorIndex;
				}
			}
		}
		catch (SQLException e) {
			logger.debug("Failed to check vector index existence on column: {}", e.getMessage());
		}
		return false;
	}

	/**
	 * Check if the document column already has a fulltext index (regardless of name).
	 */
	private boolean hasFulltextIndexOnColumn() {
		try (Connection connection = dataSource.getConnection();
				PreparedStatement pstmt = connection.prepareStatement(
					"SHOW INDEX FROM `" + tableName + "` WHERE Column_name = ? AND Index_type = 'FULLTEXT'")) {
			pstmt.setString(1, DOCUMENT_FIELD);
			ResultSet rs = pstmt.executeQuery();
			return rs.next();
		}
		catch (SQLException e) {
			logger.debug("Failed to check fulltext index existence on column: {}", e.getMessage());
		}
		return false;
	}

	private String getErrorMessage(Throwable e) {
		if (e == null) {
			return null;
		}
		Throwable cause = e;
		while (cause != null) {
			if (cause instanceof SQLException) {
				return cause.getMessage();
			}
			cause = cause.getCause();
		}
		return e.getMessage();
	}

	private String getDistanceFunction(String metricType) {
		if (metricType == null) {
			return METRIC_TYPE_L2;
		}
		switch (metricType.toLowerCase()) {
			case METRIC_TYPE_L2:
				return METRIC_TYPE_L2;
			case METRIC_TYPE_COSINE:
				return METRIC_TYPE_COSINE;
			case METRIC_TYPE_INNER_PRODUCT:
				return METRIC_TYPE_INNER_PRODUCT;
			default:
				return METRIC_TYPE_L2;
		}
	}

	@Override
	public void doAdd(List<Document> documents) {
		Assert.notNull(documents, "The document list should not be null.");
		if (CollectionUtils.isEmpty(documents)) {
			return;
		}

		List<float[]> embeddings = this.embeddingModel.embed(documents, EmbeddingOptions.builder().build(),
				this.batchingStrategy);

		String sql = buildInsertSql();
		try (Connection connection = dataSource.getConnection();
				PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			for (int i = 0; i < documents.size(); i++) {
				Document doc = documents.get(i);
				String vectorString = convertEmbeddingToString(embeddings.get(i));
				String metadataJson = serializeMetadata(doc.getMetadata());

				pstmt.setString(1, vectorString);
				pstmt.setString(2, doc.getText());
				pstmt.setString(3, metadataJson);
				pstmt.addBatch();
			}
			pstmt.executeBatch();
		}
		catch (Exception e) {
			logger.error("Failed to add documents", e);
			throw new RuntimeException("Failed to add documents to OceanBase", e);
		}
	}

	private String buildInsertSql() {
		return String.format("INSERT INTO %s (%s, %s, %s) VALUES (?, ?, ?)",
			tableName, EMBEDDING_FIELD, DOCUMENT_FIELD, METADATA_FIELD);
	}

	private String serializeMetadata(Map<String, Object> metadata) {
		if (metadata == null || metadata.isEmpty()) {
			return "{}";
		}
		try {
			return objectMapper.writeValueAsString(metadata);
		}
		catch (JsonProcessingException e) {
			logger.warn("Failed to serialize metadata, using empty object", e);
			return "{}";
		}
	}

	private String convertEmbeddingToString(float[] embedding) {
		return Arrays.toString(IntStream.range(0, embedding.length)
			.mapToObj(i -> embedding[i])
			.toArray());
	}

	@Override
	public void doDelete(List<String> ids) {
		if (CollectionUtils.isEmpty(ids)) {
			return;
		}
		String sql = String.format("DELETE FROM %s WHERE %s = ?", tableName, ID_FIELD);
		executeBatchUpdate(sql, ids);
	}

	@Override
	public void doDelete(Filter.Expression filterExpression) {
		String nativeFilterExpression = filterExpressionConverter.convertExpression(filterExpression);
		String sql = String.format("DELETE FROM %s WHERE %s", tableName, nativeFilterExpression);
		logger.info("Executing delete SQL: {}", sql);
		int deletedRows = executeUpdateWithResult(sql);
		logger.info("Deleted {} rows from table {}", deletedRows, tableName);
	}

	@Override
	public List<Document> similaritySearch(String query) {
		return this.similaritySearch(SearchRequest.builder()
			.query(query)
			.topK(this.defaultTopK)
			.similarityThreshold(this.defaultSimilarityThreshold)
			.build());
	}

	@Override
	public List<Document> doSimilaritySearch(SearchRequest searchRequest) {
		if (enableFulltext && searchRequest.getQuery() != null) {
			return doHybridSearchWithFulltext(searchRequest);
		}
		return doVectorSimilaritySearch(searchRequest);
	}

	private List<Document> doVectorSimilaritySearch(SearchRequest searchRequest) {
		boolean indexExists = checkIndexExists(indexName) || hasVectorIndexOnColumn();
		boolean useApproximateLimit = indexExists;

		try {
			return executeVectorSimilaritySearch(searchRequest, useApproximateLimit);
		}
		catch (Exception e) {
			if (useApproximateLimit) {
				logger.info("APPROXIMATE LIMIT failed (index may not exist), retrying with regular LIMIT: {}", e.getMessage());
				return executeVectorSimilaritySearch(searchRequest, false);
			}
			logger.error("Failed to perform similarity search", e);
			throw new RuntimeException("Failed to perform similarity search in OceanBase", e);
		}
	}


	private List<Document> executeVectorSimilaritySearch(SearchRequest searchRequest, boolean useApproximateLimit) {
		String distanceFunc = getDistanceFunctionName(indexMetricType);
		StringBuilder sql = buildVectorSimilaritySearchSql(searchRequest, distanceFunc, useApproximateLimit);

		List<Document> similarDocuments = new ArrayList<>();
		try (Connection connection = dataSource.getConnection();
				PreparedStatement pstmt = connection.prepareStatement(sql.toString())) {
			String vector = convertQueryToVectorBytes(searchRequest.getQuery());
			pstmt.setString(1, vector);
			pstmt.setString(2, vector);
			pstmt.setInt(3, searchRequest.getTopK());

			String limitType = useApproximateLimit ? "APPROXIMATE LIMIT" : "LIMIT";
			logger.info("Executing similarity search SQL with {}: {}", limitType, sql.toString());
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				Document doc = extractDocumentFromResultSet(rs);
				similarDocuments.add(doc);
			}
			logger.info("Found {} documents in similarity search", similarDocuments.size());
		}
		catch (Exception e) {
			logger.error("Failed to perform similarity search", e);
			throw new RuntimeException("Failed to perform similarity search in OceanBase", e);
		}
		return similarDocuments;
	}

	private StringBuilder buildVectorSimilaritySearchSql(SearchRequest searchRequest, String distanceFunc, boolean useApproximateLimit) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT ").append(ID_FIELD).append(", ").append(EMBEDDING_FIELD).append(", ");
		sql.append(DOCUMENT_FIELD).append(", ").append(METADATA_FIELD).append(", ").append(TIMESTAMP_FIELD).append(", ");
		sql.append(distanceFunc).append("(").append(EMBEDDING_FIELD).append(", ?) as distance ");
		sql.append("FROM ").append(tableName);

		if (searchRequest.getFilterExpression() != null) {
			String filterExpr = filterExpressionConverter.convertExpression(searchRequest.getFilterExpression());
			sql.append(" WHERE ").append(filterExpr);
		}

		sql.append(" ORDER BY ").append(distanceFunc).append("(").append(EMBEDDING_FIELD).append(", ?) ASC ");

		if (useApproximateLimit) {
			sql.append("APPROXIMATE LIMIT ?");
			logger.info("Using APPROXIMATE LIMIT for similarity search");
		}
		else {
			sql.append("LIMIT ?");
			logger.info("Using regular LIMIT for similarity search");
		}

		return sql;
	}

	private List<Document> doHybridSearchWithFulltext(SearchRequest searchRequest) {
		List<Document> vectorResults = doVectorSimilaritySearch(SearchRequest.builder()
			.query(searchRequest.getQuery())
			.topK(searchRequest.getTopK() * 2)
			.filterExpression(searchRequest.getFilterExpression())
			.build());

		List<Document> fulltextResults = doFulltextSearch(searchRequest);
		return combineHybridResults(vectorResults, fulltextResults, searchRequest.getTopK());
	}

	private List<Document> doFulltextSearch(SearchRequest searchRequest) {
		String distanceFunc = getDistanceFunctionName(indexMetricType);
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT ").append(ID_FIELD).append(", ").append(EMBEDDING_FIELD).append(", ");
		sql.append(DOCUMENT_FIELD).append(", ").append(METADATA_FIELD).append(", ").append(TIMESTAMP_FIELD).append(", ");
		sql.append(distanceFunc).append("(").append(EMBEDDING_FIELD).append(", ?) as distance ");
		sql.append("FROM ").append(tableName);
		sql.append(" WHERE MATCH(").append(DOCUMENT_FIELD).append(") AGAINST(? IN NATURAL LANGUAGE MODE)");

		if (searchRequest.getFilterExpression() != null) {
			String filterExpr = filterExpressionConverter.convertExpression(searchRequest.getFilterExpression());
			sql.append(" AND ").append(filterExpr);
		}

		sql.append(" ORDER BY ").append(distanceFunc).append("(").append(EMBEDDING_FIELD).append(", ?) ASC ");
		sql.append("LIMIT ?");

		List<Document> results = new ArrayList<>();
		try (Connection connection = dataSource.getConnection();
				PreparedStatement pstmt = connection.prepareStatement(sql.toString())) {
			String vector = convertQueryToVectorBytes(searchRequest.getQuery());
			pstmt.setString(1, vector);
			pstmt.setString(2, searchRequest.getQuery());
			pstmt.setString(3, vector);
			pstmt.setInt(4, searchRequest.getTopK() * 2);

			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				Document doc = extractDocumentFromResultSet(rs);
				results.add(doc);
			}
		}
		catch (Exception e) {
			logger.error("Failed to perform fulltext search", e);
		}
		return results;
	}

	private List<Document> combineHybridResults(List<Document> vectorResults, List<Document> fulltextResults, int topK) {
		Map<String, Document> docMap = new LinkedHashMap<>();
		Map<String, Double> scores = new HashMap<>();

		for (int i = 0; i < vectorResults.size(); i++) {
			Document doc = vectorResults.get(i);
			String id = doc.getId();
			docMap.put(id, doc);
			double score = 1.0 - (i / (double) Math.max(vectorResults.size(), 1));
			scores.put(id, scores.getOrDefault(id, 0.0) + score * 0.7);
		}

		for (int i = 0; i < fulltextResults.size(); i++) {
			Document doc = fulltextResults.get(i);
			String id = doc.getId();
			if (!docMap.containsKey(id)) {
				docMap.put(id, doc);
			}
			double score = 1.0 - (i / (double) Math.max(fulltextResults.size(), 1));
			scores.put(id, scores.getOrDefault(id, 0.0) + score * 0.3);
		}

		return docMap.values().stream()
			.sorted((d1, d2) -> Double.compare(
				scores.getOrDefault(d2.getId(), 0.0),
				scores.getOrDefault(d1.getId(), 0.0)
			))
			.limit(topK)
			.toList();
	}

	private String getDistanceFunctionName(String metricType) {
		if (metricType == null) {
			return DISTANCE_FUNCTION_L2;
		}
		switch (metricType.toLowerCase()) {
			case METRIC_TYPE_L2:
				return DISTANCE_FUNCTION_L2;
			case METRIC_TYPE_COSINE:
				return DISTANCE_FUNCTION_COSINE;
			case METRIC_TYPE_INNER_PRODUCT:
				return DISTANCE_FUNCTION_INNER_PRODUCT;
			default:
				return DISTANCE_FUNCTION_L2;
		}
	}

	private Document extractDocumentFromResultSet(ResultSet rs) throws SQLException, JsonProcessingException {
		String id = rs.getString(ID_FIELD);
		String metadataJson = rs.getString(METADATA_FIELD);
		String distanceStr = rs.getString("distance");
		String pageContent = rs.getString(DOCUMENT_FIELD);

		Map<String, Object> metadata = parseMetadata(metadataJson);

		Double score = null;
		if (distanceStr != null) {
			try {
				double distance = Double.parseDouble(distanceStr);
				metadata.put("distance", distanceStr);
				score = distance;
			}
			catch (NumberFormatException e) {
				logger.warn("Failed to parse distance as number: {}", distanceStr);
			}
		}

		return Document.builder()
			.id(id)
			.text(pageContent)
			.metadata(metadata)
			.score(score)
			.build();
	}

	private Map<String, Object> parseMetadata(String metadataJson) {
		if (!StringUtils.hasText(metadataJson)) {
			return new HashMap<>();
		}
		try {
			return objectMapper.readValue(metadataJson, new TypeReference<Map<String, Object>>() {});
		}
		catch (JsonProcessingException e) {
			logger.warn("Failed to parse metadata JSON: {}", metadataJson, e);
			return new HashMap<>();
		}
	}

	private String convertQueryToVectorBytes(String query) {
		return Arrays.toString(this.embeddingModel.embed(query));
	}

	private void executeUpdate(String sql) {
		executeUpdateWithResult(sql);
	}

	private int executeUpdateWithResult(String sql) {
		try (Connection connection = dataSource.getConnection();
				PreparedStatement pstmt = connection.prepareStatement(sql)) {
			boolean autoCommit = connection.getAutoCommit();
			try {
				if (!autoCommit) {
					connection.setAutoCommit(true);
				}
				int rowsAffected = pstmt.executeUpdate();
				return rowsAffected;
			}
			finally {
				if (!autoCommit) {
					connection.setAutoCommit(false);
				}
			}
		}
		catch (SQLException e) {
			logger.error("SQL execution failed: {}", sql, e);
			throw new RuntimeException("Failed to execute SQL", e);
		}
	}

	private void executeBatchUpdate(String sql, List<String> params) {
		try (Connection connection = dataSource.getConnection();
				PreparedStatement pstmt = connection.prepareStatement(sql)) {
			boolean autoCommit = connection.getAutoCommit();
			try {
				if (!autoCommit) {
					connection.setAutoCommit(true);
				}
				for (String param : params) {
					pstmt.setString(1, param);
					pstmt.addBatch();
				}
				pstmt.executeBatch();
			}
			finally {
				if (!autoCommit) {
					connection.setAutoCommit(false);
				}
			}
		}
		catch (SQLException e) {
			logger.error("Batch SQL execution failed", e);
			throw new RuntimeException("Failed to execute batch SQL", e);
		}
	}

	@Override
	public VectorStoreObservationContext.Builder createObservationContextBuilder(String operationName) {
		return VectorStoreObservationContext.builder(DATA_BASE_SYSTEM, operationName)
			.collectionName(this.tableName)
			.dimensions(this.embeddingModel.dimensions());
	}

	public static class Builder extends AbstractVectorStoreBuilder<Builder> {

		private final String tableName;
		private final DataSource dataSource;
		private int defaultTopK = DEFAULT_TOP_K;
		private Double defaultSimilarityThreshold = DEFAULT_SIMILARITY_THRESHOLD;
		private Integer dimension;
		private String hybridSearchType;
		private String indexType = INDEX_TYPE_HNSW;
		private String indexMetricType = METRIC_TYPE_L2;

		private Builder(String tableName, DataSource dataSource, EmbeddingModel embeddingModel) {
			super(embeddingModel);
			Assert.notNull(tableName, "Table name must not be null");
			Assert.notNull(dataSource, "Data source must not be null");
			this.tableName = tableName.toLowerCase();
			this.dataSource = dataSource;
		}

		public Builder defaultTopK(int defaultTopK) {
			Assert.isTrue(defaultTopK >= 0, "The topK should be positive value.");
			this.defaultTopK = defaultTopK;
			return this;
		}

		public Builder defaultSimilarityThreshold(Double defaultSimilarityThreshold) {
			Assert.isTrue(defaultSimilarityThreshold >= 0.0 && defaultSimilarityThreshold <= 1.0,
					"The similarity threshold must be in range [0.0:1.0].");
			this.defaultSimilarityThreshold = defaultSimilarityThreshold;
			return this;
		}

		public Builder dimension(Integer dimension) {
			this.dimension = dimension;
			return this;
		}

		public Builder hybridSearchType(String hybridSearchType) {
			this.hybridSearchType = hybridSearchType;
			return this;
		}

		@Override
		public OceanBaseVectorStore build() {
			try {
				return new OceanBaseVectorStore(this);
			}
			catch (Exception e) {
				throw new RuntimeException("Failed to build OceanBaseVectorStore: " + e.getMessage(), e);
			}
		}

	}

}
