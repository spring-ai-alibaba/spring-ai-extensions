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

import java.util.List;
import java.util.Map;

import com.alibaba.cloud.ai.dashscope.rag.DashScopeRagApi.KnowledgeBaseFilters;
import org.springframework.ai.document.Document;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;

public final class DashScopeRagDocumentRetriever implements DocumentRetriever {

	private final DocumentRetriever delegate;

	private DashScopeRagDocumentRetriever(DocumentRetriever delegate) {
		this.delegate = delegate;
	}

	public static DashScopeRagDocumentRetriever forIndex(DashScopeRagApi api, String indexId, int topK,
			List<Map<String, Object>> filters) {
		var searchFilters = List.copyOf(filters);
		return new DashScopeRagDocumentRetriever(query -> api.retrieve(indexId, query.text(), topK, searchFilters));
	}

	public static DashScopeRagDocumentRetriever forService(DashScopeRagApi api, String agentId,
			List<KnowledgeBaseFilters> filters) {
		var knowledgeBaseFilters = List.copyOf(filters);
		return new DashScopeRagDocumentRetriever(query -> api.search(agentId, query.text(), List.of(), knowledgeBaseFilters));
	}

	@Override
	public List<Document> retrieve(Query query) {
		return this.delegate.retrieve(query);
	}

}
