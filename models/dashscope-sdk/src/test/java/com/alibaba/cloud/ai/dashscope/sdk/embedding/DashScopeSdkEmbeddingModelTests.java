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

import com.alibaba.dashscope.embeddings.TextEmbeddingOutput;
import com.alibaba.dashscope.embeddings.TextEmbeddingParam;
import com.alibaba.dashscope.embeddings.TextEmbeddingResult;
import com.alibaba.dashscope.embeddings.TextEmbeddingResultItem;
import com.alibaba.dashscope.embeddings.TextEmbeddingUsage;
import org.junit.jupiter.api.Test;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DashScopeSdkEmbeddingModelTests {

	@Test
	void testEmbeddingCall() {
		DashScopeSdkEmbeddingModel model = DashScopeSdkEmbeddingModel.builder()
			.embeddingClient(new FakeEmbeddingClient())
			.defaultOptions(DashScopeSdkEmbeddingOptions.builder().model("text-embedding-v2").build())
			.apiKey("test-key")
			.build();

		EmbeddingResponse response = model.call(new EmbeddingRequest(List.of("hello"),
				DashScopeSdkEmbeddingOptions.builder().textType("query").build()));

		assertThat(response.getResults()).hasSize(1);
		assertThat(response.getResult().getOutput()).containsExactly(0.1f, 0.2f, 0.3f);
		assertThat(response.getMetadata().getModel()).isEqualTo("text-embedding-v2");
	}

	private static final class FakeEmbeddingClient implements DashScopeSdkTextEmbeddingClient {

		@Override
		public TextEmbeddingResult call(TextEmbeddingParam embeddingParam) {
			TextEmbeddingResult result = instantiateTextEmbeddingResult();
			result.setRequestId("req-1");

			TextEmbeddingResultItem item = new TextEmbeddingResultItem();
			item.setTextIndex(0);
			item.setEmbedding(List.of(0.1, 0.2, 0.3));

			TextEmbeddingOutput output = new TextEmbeddingOutput();
			output.setEmbeddings(List.of(item));
			result.setOutput(output);

			TextEmbeddingUsage usage = new TextEmbeddingUsage();
			usage.setTotalTokens(3);
			result.setUsage(usage);
			return result;
		}

		private TextEmbeddingResult instantiateTextEmbeddingResult() {
			try {
				var constructor = TextEmbeddingResult.class.getDeclaredConstructor();
				constructor.setAccessible(true);
				return constructor.newInstance();
			}
			catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		}

	}

}
