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
package com.alibaba.cloud.ai.dashscope.rag;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.common.DashScopeException;
import org.jspecify.annotations.Nullable;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Locale;
import java.util.Optional;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author nuocheng.lxm
 * @since 2024/8/6 15:42
 */
public class DashScopeCloudStore implements VectorStore {

	private final DashScopeStoreOptions options;

	private final @Nullable DashScopeRagApi ragApi;

	private final @Nullable DashScopeApi dashScopeApi;

	private final @Nullable String indexId;

	public DashScopeCloudStore(DashScopeApi dashScopeApi, DashScopeStoreOptions options) {
		Assert.notNull(options, "DashScopeStoreOptions must not be null");
		Assert.notNull(options.getIndexName(), "IndexName must not be null");
		this.options = options;
		this.dashScopeApi = dashScopeApi;
		this.ragApi = null;
		this.indexId = null;
	}


	/**
	 * Connects to an existing knowledge base through the workspace RAG gateway.
	 * Document IDs supplied to add must be data center file IDs.
	 */
	public DashScopeCloudStore(DashScopeRagApi ragApi, String indexId, DashScopeStoreOptions options) {
		Assert.hasText(indexId, "A knowledge base ID is required");
		this.options = options;
		this.ragApi = ragApi;
		this.indexId = indexId;
		this.dashScopeApi = null;
	}

	@Override
	public String getName() {
		return VectorStore.super.getName();
	}

	/**
	 * @param documents the list of documents to store. Current document must be
	 * DashScopeDocumentReader's Result
	 */
	@Override
	public void add(List<Document> documents) {
		if (CollectionUtils.isEmpty(documents)) {
			throw new DashScopeException("documents must not be null");
		}
		List<String> documentIdList = documents.stream()
			.filter(e -> e.getId() != null)
			.map(Document::getId)
			.collect(Collectors.toList());
		if (documentIdList == null || documentIdList.isEmpty()) {
			throw new DashScopeException("document's id must not be null");
		}
		if (this.ragApi != null) {
			var job = this.ragApi.submitImport(Objects.requireNonNull(this.indexId), documentIdList);
			this.ragApi.awaitImport(Objects.requireNonNull(this.indexId), job.ingestionId(), Duration.ofMinutes(5), Duration.ofSeconds(2));
			return;
		}
		legacyApi().upsertPipeline(documents, this.options);
	}

	@Override
	public void delete(List<String> idList) {
		if (this.ragApi != null) {
			this.ragApi.deleteDocuments(Objects.requireNonNull(this.indexId), idList);
			return;
		}
		String pipelineId = legacyApi().getPipelineIdByName(options.getIndexName());
		if (pipelineId == null) {
			throw new DashScopeException("Index:" + options.getIndexName() + " NotExist");
		}
		legacyApi().deletePipelineDocument(pipelineId, idList);
	}

	@Override
	public void delete(Filter.Expression filterExpression) {
		throw new UnsupportedOperationException("Bailian deletion requires explicit knowledge base document IDs");
	}

	@Override
	public List<Document> similaritySearch(String query) {

		return similaritySearch(SearchRequest.builder().query(query).build());

	}

	@Override
	public <T> Optional<T> getNativeClient() {
		return VectorStore.super.getNativeClient();
	}

	@Override
	public List<Document> similaritySearch(SearchRequest request) {
		if (this.ragApi != null) {
			var filters = new ArrayList<Map<String, Object>>();
			var configured = this.options.getRetrieverOptions();
			if (configured != null && configured.getSearchFilters() != null) {
				filters.addAll(configured.getSearchFilters());
			}
			if (request.getFilterExpression() != null) {
				filters.add(searchFilter(request.getFilterExpression()));
			}
			return this.ragApi.retrieve(Objects.requireNonNull(this.indexId), request.getQuery(), request.getTopK(), filters).stream()
				.filter(document -> request.getSimilarityThreshold() <= 0
						|| document.getScore() != null && document.getScore() >= request.getSimilarityThreshold())
				.limit(request.getTopK()).toList();
		}
		if (request.getFilterExpression() != null) {
			throw new UnsupportedOperationException("Use the workspace RAG gateway for SearchRequest filters");
		}
		String pipelineId = legacyApi().getPipelineIdByName(options.getIndexName());
		if (pipelineId == null) {
			throw new DashScopeException("Index:" + options.getIndexName() + " NotExist");
		}
		DashScopeDocumentRetrieverOptions searchOption = options.getRetrieverOptions();
		if (searchOption == null) {
			searchOption = new DashScopeDocumentRetrieverOptions();
		}
		searchOption.setRerankTopN(request.getTopK());
		return legacyApi().retriever(pipelineId, request.getQuery(), searchOption).stream()
			.filter(document -> request.getSimilarityThreshold() <= 0
					|| document.getScore() != null && document.getScore() >= request.getSimilarityThreshold())
			.limit(request.getTopK()).toList();
	}


	private DashScopeApi legacyApi() {
		return Objects.requireNonNull(this.dashScopeApi);
	}

	private static Map<String, Object> searchFilter(Filter.@Nullable Operand operand) {
		if (operand instanceof Filter.Group group) {
			return searchFilter(group.content());
		}
		if (operand instanceof Filter.Expression expr) {
			return switch (expr.type()) {
				case AND, OR -> Map.of("_operator", expr.type().name(), "_conditions",
						List.of(searchFilter(expr.left()), searchFilter(expr.right())));
				case EQ, GT, GTE, LT, LTE -> comparisonFilter(expr);
				default -> throw new IllegalArgumentException("Unsupported Bailian search filter " + expr.type());
			};
		}
		throw new IllegalArgumentException("Bailian search filters require an expression");
	}

	private static Map<String, Object> comparisonFilter(Filter.Expression expression) {
		if (!(expression.left() instanceof Filter.Key key) || !(expression.right() instanceof Filter.Value value)
				|| !(value.value() instanceof String || value.value() instanceof Number || value.value() instanceof Boolean)
				|| key.key().startsWith("_")
				|| expression.type() != Filter.ExpressionType.EQ && !(value.value() instanceof Number)) {
			throw new IllegalArgumentException("Invalid Bailian search filter operands");
		}
		Object condition = expression.type() == Filter.ExpressionType.EQ ? value.value()
				: Map.of(expression.type().name().toLowerCase(Locale.ROOT), value.value());
		return Map.of(key.key(), condition);
	}

}
