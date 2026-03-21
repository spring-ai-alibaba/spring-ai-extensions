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

import com.alibaba.dashscope.embeddings.TextEmbedding;
import com.alibaba.dashscope.embeddings.TextEmbeddingParam;
import com.alibaba.dashscope.embeddings.TextEmbeddingResult;

/**
 * Default {@link DashScopeSdkTextEmbeddingClient} backed by DashScope Java SDK.
 */
public class DefaultDashScopeSdkTextEmbeddingClient implements DashScopeSdkTextEmbeddingClient {

	private final TextEmbedding textEmbedding;

	public DefaultDashScopeSdkTextEmbeddingClient() {
		this(new TextEmbedding());
	}

	public DefaultDashScopeSdkTextEmbeddingClient(TextEmbedding textEmbedding) {
		this.textEmbedding = textEmbedding;
	}

	@Override
	public TextEmbeddingResult call(TextEmbeddingParam embeddingParam) throws Exception {
		return this.textEmbedding.call(embeddingParam);
	}

}
