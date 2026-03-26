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
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DashScopeSdkEmbeddingModelTests {

	@Test
	void testEmbeddingCall() {
		FakeEmbeddingClient client = new FakeEmbeddingClient();
		DashScopeSdkEmbeddingModel model = DashScopeSdkEmbeddingModel.builder()
			.embeddingClient(client)
			.defaultOptions(DashScopeSdkEmbeddingOptions.builder().model("text-embedding-v2").build())
			.apiKey("test-key")
			.build();

		EmbeddingResponse response = model.call(new EmbeddingRequest(List.of("hello"),
				DashScopeSdkEmbeddingOptions.builder().textType("query").build()));

		assertThat(response.getResults()).hasSize(1);
		assertThat(response.getResult().getOutput()).containsExactly(0.1f, 0.2f, 0.3f);
		assertThat(response.getMetadata().getModel()).isEqualTo("text-embedding-v2");
		assertThat(client.lastRequest).isNotNull();
		assertThat(client.lastRequest.getModel()).isEqualTo("text-embedding-v2");
		assertThat(client.lastRequest.getParameters()).containsEntry("text_type", "query");
	}

	@Test
	void testHeadersAreMergedWithRuntimeOverride() {
		FakeEmbeddingClient client = new FakeEmbeddingClient();
		DashScopeSdkEmbeddingModel model = DashScopeSdkEmbeddingModel.builder()
			.embeddingClient(client)
			.defaultOptions(DashScopeSdkEmbeddingOptions.builder()
				.model("text-embedding-v2")
				.httpHeaders(Map.of("x-default", "d", "x-override", "default"))
				.build())
			.connectionHeaders(Map.of("x-conn", "c", "x-override", "conn"))
			.build();

		model.call(new EmbeddingRequest(List.of("hello"),
				DashScopeSdkEmbeddingOptions.builder()
					.httpHeaders(Map.of("x-runtime", "r", "x-override", "runtime"))
					.build()));

		assertThat(client.lastRequest.getHeaders()).containsEntry("x-conn", "c")
			.containsEntry("x-runtime", "r")
			.containsEntry("x-override", "runtime");
	}

	@Test
	void testReturnsEmptyWhenEmbeddingsMissing() {
		FakeEmbeddingClient client = new FakeEmbeddingClient();
		client.callResult = instantiateTextEmbeddingResult();
		DashScopeSdkEmbeddingModel model = DashScopeSdkEmbeddingModel.builder()
			.embeddingClient(client)
			.defaultOptions(DashScopeSdkEmbeddingOptions.builder().model("text-embedding-v2").build())
			.build();

		EmbeddingResponse response = model.call(new EmbeddingRequest(List.of("hello"), null));

		assertThat(response.getResults()).isEmpty();
	}

	@Test
	void testSkipsNullEmbeddingItems() {
		FakeEmbeddingClient client = new FakeEmbeddingClient();
		client.callResult = embeddingResultWithNullAndValidItem();
		DashScopeSdkEmbeddingModel model = DashScopeSdkEmbeddingModel.builder()
			.embeddingClient(client)
			.defaultOptions(DashScopeSdkEmbeddingOptions.builder().model("text-embedding-v2").build())
			.build();

		EmbeddingResponse response = model.call(new EmbeddingRequest(List.of("hello"), null));

		assertThat(response.getResults()).hasSize(1);
		assertThat(response.getResult().getOutput()).containsExactly(1.0f, 2.0f);
	}

	@Test
	void testWrapsClientException() {
		FakeEmbeddingClient client = new FakeEmbeddingClient();
		client.throwOnCall = new RuntimeException("boom");
		DashScopeSdkEmbeddingModel model = DashScopeSdkEmbeddingModel.builder()
			.embeddingClient(client)
			.defaultOptions(DashScopeSdkEmbeddingOptions.builder().model("text-embedding-v2").build())
			.build();

		assertThatThrownBy(() -> model.call(new EmbeddingRequest(List.of("hello"), null)))
			.isInstanceOf(com.alibaba.cloud.ai.dashscope.sdk.common.DashScopeSdkException.class)
			.hasMessageContaining("Failed to call DashScope SDK embedding API")
			.hasCause(client.throwOnCall);
	}

	@Test
	void testDimensionsUsesKnownModelValue() {
		DashScopeSdkEmbeddingModel model = DashScopeSdkEmbeddingModel.builder()
			.embeddingClient(new FakeEmbeddingClient())
			.defaultOptions(DashScopeSdkEmbeddingOptions.builder().model("text-embedding-v2").build())
			.build();

		assertThat(model.dimensions()).isEqualTo(1536);
	}

	@Test
	void testGetDefaultOptionsReturnsCopy() {
		DashScopeSdkEmbeddingModel model = DashScopeSdkEmbeddingModel.builder()
			.embeddingClient(new FakeEmbeddingClient())
			.defaultOptions(DashScopeSdkEmbeddingOptions.builder()
				.model("text-embedding-v2")
				.httpHeaders(Map.of("x-default", "d"))
				.build())
			.build();

		DashScopeSdkEmbeddingOptions copied = model.getDefaultOptions();
		copied.getHttpHeaders().put("x-new", "n");

		assertThat(model.getDefaultOptions().getHttpHeaders()).containsOnlyKeys("x-default");
	}

	private static TextEmbeddingResult embeddingResultWithNullAndValidItem() {
		TextEmbeddingResult result = instantiateTextEmbeddingResult();
		result.setRequestId("req-2");

		TextEmbeddingResultItem nullItem = new TextEmbeddingResultItem();
		nullItem.setTextIndex(0);
		nullItem.setEmbedding(null);

		TextEmbeddingResultItem validItem = new TextEmbeddingResultItem();
		validItem.setTextIndex(1);
		validItem.setEmbedding(List.of(1.0, 2.0));

		TextEmbeddingOutput output = new TextEmbeddingOutput();
		output.setEmbeddings(List.of(nullItem, validItem));
		result.setOutput(output);
		return result;
	}

	private static TextEmbeddingResult instantiateTextEmbeddingResult() {
		try {
			var constructor = TextEmbeddingResult.class.getDeclaredConstructor();
			constructor.setAccessible(true);
			return constructor.newInstance();
		}
		catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	private static final class FakeEmbeddingClient implements DashScopeSdkTextEmbeddingClient {

		private TextEmbeddingResult callResult;

		private RuntimeException throwOnCall;

		private TextEmbeddingParam lastRequest;

		@Override
		public TextEmbeddingResult call(TextEmbeddingParam embeddingParam) {
			this.lastRequest = embeddingParam;
			if (this.throwOnCall != null) {
				throw this.throwOnCall;
			}
			if (this.callResult != null) {
				return this.callResult;
			}

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

	}

}
