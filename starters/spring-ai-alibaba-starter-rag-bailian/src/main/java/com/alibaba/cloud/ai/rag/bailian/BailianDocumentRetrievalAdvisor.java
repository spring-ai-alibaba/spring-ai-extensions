/*
 * Copyright 2024-2026 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.cloud.ai.rag.bailian;

import com.alibaba.cloud.ai.dashscope.rag.DashScopeDocumentRetrievalAdvisor;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;

/**
 * Advisor for using a Bailian knowledge base with an application-owned chat model.
 */
public class BailianDocumentRetrievalAdvisor extends DashScopeDocumentRetrievalAdvisor {

	public BailianDocumentRetrievalAdvisor(DocumentRetriever retriever, boolean enableReference) {
		super(retriever, enableReference);
	}

	public BailianDocumentRetrievalAdvisor(DocumentRetriever retriever, PromptTemplate userTextAdvise,
			boolean enableReference) {
		super(retriever, userTextAdvise, enableReference);
	}

	public BailianDocumentRetrievalAdvisor(DocumentRetriever retriever, PromptTemplate userTextAdvise,
			boolean enableReference, int order) {
		super(retriever, userTextAdvise, enableReference, order);
	}

}
