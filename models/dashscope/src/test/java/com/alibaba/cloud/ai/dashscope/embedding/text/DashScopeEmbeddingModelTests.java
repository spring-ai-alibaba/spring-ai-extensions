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

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.spec.DashScopeApiSpec;
import com.alibaba.cloud.ai.dashscope.embedding.text.DashScopeEmbeddingModel.Builder;
import com.alibaba.cloud.ai.dashscope.spec.DashScopeApiSpec.Embedding;
import com.alibaba.cloud.ai.dashscope.spec.DashScopeApiSpec.EmbeddingList;
import com.alibaba.cloud.ai.dashscope.spec.DashScopeApiSpec.EmbeddingUsage;
import com.alibaba.cloud.ai.dashscope.spec.DashScopeApiSpec.Embeddings;
import com.alibaba.cloud.ai.dashscope.spec.DashScopeModel.EmbeddingModel;
import io.micrometer.observation.ObservationRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.embedding.TokenCountBatchingStrategy;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.retry.RetryUtils;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test cases for DashScopeEmbeddingModel. Tests cover basic embedding operations, error
 * handling, and various edge cases.
 *
 * @author yuluo
 * @author <a href="mailto:yuluo08290126@gmail.com">yuluo</a>
 * @author brianxiadong
 * @since 1.0.0-M5.1
 */
class DashScopeEmbeddingModelTests {

	// Test constants
	private static final String TEST_MODEL = "text-embedding-v3";

	private static final String TEST_TEXT_TYPE = "document";

	private static final String TEST_REQUEST_ID = "test-request-id";

	private static final String TEST_TEXT = "Hello, world!";

	private static final Integer TEST_DIMENSION = 512;

	private DashScopeApi dashScopeApi;

	private DashScopeEmbeddingModel embeddingModel;

	private DashScopeEmbeddingOptions defaultOptions;

	@BeforeEach
	void setUp() {
		// Initialize mock objects and test instances
		dashScopeApi = Mockito.mock(DashScopeApi.class);
		defaultOptions = DashScopeEmbeddingOptions.builder()
			.model(TEST_MODEL)
			.textType(TEST_TEXT_TYPE)
			.dimensions(TEST_DIMENSION)
			.build();
		embeddingModel = new DashScopeEmbeddingModel(dashScopeApi, MetadataMode.EMBED, defaultOptions);
	}

	@Test
	void testBasicEmbedding() {
		// Test basic embedding with a single text input
		float[] embeddingVector = { 0.1f, 0.2f, 0.3f };
		Embedding embedding = new Embedding(0, embeddingVector);
		Embeddings embeddings = new Embeddings(List.of(embedding));
		EmbeddingUsage usage = new EmbeddingUsage(10L);
		EmbeddingList embeddingList = new EmbeddingList(TEST_REQUEST_ID, null, null, embeddings, usage);
		ResponseEntity<EmbeddingList> responseEntity = ResponseEntity.ok(embeddingList);

		when(dashScopeApi.embeddings(any())).thenReturn(responseEntity);

		// Test embedding a single text
		EmbeddingResponse response = embeddingModel.embedForResponse(List.of(TEST_TEXT));

		assertThat(response.getResults()).hasSize(1);
		assertThat(response.getResults().get(0).getOutput()).containsExactly(embeddingVector);
		assertThat(response.getResults().get(0).getIndex()).isEqualTo(0);
	}

	@Test
	void testMultipleEmbeddings() {
		// Test embedding multiple texts
		float[] vector1 = { 0.1f, 0.2f, 0.3f };
		float[] vector2 = { 0.4f, 0.5f, 0.6f };
		List<Embedding> embeddingList = Arrays.asList(new Embedding(0, vector1), new Embedding(1, vector2));
		Embeddings embeddings = new Embeddings(embeddingList);
		EmbeddingUsage usage = new EmbeddingUsage(20L);
		EmbeddingList response = new EmbeddingList(TEST_REQUEST_ID, null, null, embeddings, usage);
		ResponseEntity<EmbeddingList> responseEntity = ResponseEntity.ok(response);

		when(dashScopeApi.embeddings(any())).thenReturn(responseEntity);

		List<String> texts = Arrays.asList("First text", "Second text");
		EmbeddingResponse embeddingResponse = embeddingModel.embedForResponse(texts);

		assertThat(embeddingResponse.getResults()).hasSize(2);
		assertThat(embeddingResponse.getResults().get(0).getOutput()).containsExactly(vector1);
		assertThat(embeddingResponse.getResults().get(1).getOutput()).containsExactly(vector2);
	}

	@Test
	void testEmbedDocument() {
		// Test embedding a Document object
		float[] embeddingVector = { 0.1f, 0.2f, 0.3f };
		Embedding embedding = new Embedding(0, embeddingVector);
		Embeddings embeddings = new Embeddings(List.of(embedding));
		EmbeddingUsage usage = new EmbeddingUsage(10L);
		EmbeddingList embeddingList = new EmbeddingList(TEST_REQUEST_ID, null, null, embeddings, usage);
		ResponseEntity<EmbeddingList> responseEntity = ResponseEntity.ok(embeddingList);

		when(dashScopeApi.embeddings(any())).thenReturn(responseEntity);

		Document document = new Document(TEST_TEXT, Map.of("key", "value"));
		float[] result = embeddingModel.embed(document);

		assertThat(result).containsExactly(embeddingVector);
	}

	@Test
	void testErrorHandling() {
		// Test error handling with error response
		EmbeddingList errorResponse = new EmbeddingList(TEST_REQUEST_ID, "ERROR_CODE", "Error message", null,
				new EmbeddingUsage(0L));
		ResponseEntity<EmbeddingList> responseEntity = ResponseEntity.ok(errorResponse);

		when(dashScopeApi.embeddings(any())).thenReturn(responseEntity);

		assertThatThrownBy(() -> embeddingModel.embedForResponse(List.of(TEST_TEXT)))
			.isInstanceOf(RuntimeException.class)
			.hasMessageContaining("Embedding failed");
	}

	@Test
	void testNullInputHandling() {
		// Test handling of null inputs
		assertThatThrownBy(() -> embeddingModel.embed((Document) null)).isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("Document must not be null");

		assertThatThrownBy(() -> embeddingModel.embedForResponse((List<String>) null))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("Texts must not be null");
	}

	@Test
	void testCustomOptions() {
		// Test embedding with custom options
		float[] embeddingVector = { 0.1f, 0.2f, 0.3f };
		Embedding embedding = new Embedding(0, embeddingVector);
		Embeddings embeddings = new Embeddings(List.of(embedding));
		EmbeddingUsage usage = new EmbeddingUsage(10L);
		EmbeddingList embeddingList = new EmbeddingList(TEST_REQUEST_ID, null, null, embeddings, usage);
		ResponseEntity<EmbeddingList> responseEntity = ResponseEntity.ok(embeddingList);

		when(dashScopeApi.embeddings(any())).thenReturn(responseEntity);

		DashScopeEmbeddingOptions customOptions = DashScopeEmbeddingOptions.builder()
			.model("custom-model")
			.textType("query")
			.dimensions(512)
			.build();

		EmbeddingRequest request = new EmbeddingRequest(List.of(TEST_TEXT), customOptions);
		EmbeddingResponse response = embeddingModel.call(request);

		assertThat(response.getResults()).hasSize(1);
		assertThat(response.getResults().get(0).getOutput()).containsExactly(embeddingVector);
	}

	@Test
	void testEmptyResponse() {
		// Test handling of empty response with non-null usage
		EmbeddingUsage usage = new EmbeddingUsage(0L);
		EmbeddingList emptyResponse = new EmbeddingList(TEST_REQUEST_ID, null, null, new Embeddings(List.of()), usage);
		ResponseEntity<EmbeddingList> responseEntity = ResponseEntity.ok(emptyResponse);

		when(dashScopeApi.embeddings(any())).thenReturn(responseEntity);

		EmbeddingResponse response = embeddingModel.embedForResponse(List.of(TEST_TEXT));

		assertThat(response.getResults()).isEmpty();
		assertThat(response.getMetadata().getUsage().getTotalTokens()).isZero();
	}

	@Test
	void testNullTextTypeHandling() {
		// Test that null textType is properly handled at API level to prevent FastJson WriteNullStringAsEmpty issues
		float[] embeddingVector = { 0.1f, 0.2f, 0.3f };
		Embedding embedding = new Embedding(0, embeddingVector);
		Embeddings embeddings = new Embeddings(List.of(embedding));
		EmbeddingUsage usage = new EmbeddingUsage(10L);
		EmbeddingList embeddingList = new EmbeddingList(TEST_REQUEST_ID, null, null, embeddings, usage);
		ResponseEntity<EmbeddingList> responseEntity = ResponseEntity.ok(embeddingList);

		when(dashScopeApi.embeddings(any())).thenReturn(responseEntity);

		// Create options with null textType (simulating the issue scenario)
		DashScopeEmbeddingOptions optionsWithNullTextType = DashScopeEmbeddingOptions.builder()
			.model(TEST_MODEL)
			.textType(null) // null textType
			.dimensions(TEST_DIMENSION)
			.build();

		EmbeddingRequest request = new EmbeddingRequest(List.of(TEST_TEXT), optionsWithNullTextType);
		EmbeddingResponse response = embeddingModel.call(request);

		assertThat(response.getResults()).hasSize(1);
		assertThat(response.getResults().get(0).getOutput()).containsExactly(embeddingVector);
		assertThat(response.getResults().get(0).getIndex()).isEqualTo(0);
	}

	@Test
	void testEmptyTextTypeHandling() {
		// Test empty textType preservation.
		float[] embeddingVector = { 0.1f, 0.2f, 0.3f };
		Embedding embedding = new Embedding(0, embeddingVector);
		Embeddings embeddings = new Embeddings(List.of(embedding));
		EmbeddingUsage usage = new EmbeddingUsage(10L);
		EmbeddingList embeddingList = new EmbeddingList(TEST_REQUEST_ID, null, null, embeddings, usage);
		ResponseEntity<EmbeddingList> responseEntity = ResponseEntity.ok(embeddingList);

		when(dashScopeApi.embeddings(any())).thenReturn(responseEntity);

		// Test empty textType handling.
		DashScopeEmbeddingOptions optionsWithEmptyTextType = DashScopeEmbeddingOptions.builder()
			.model(TEST_MODEL)
			.textType("") // Empty textType - user's explicit choice.
			.dimensions(TEST_DIMENSION)
			.build();

		EmbeddingRequest request = new EmbeddingRequest(List.of(TEST_TEXT), optionsWithEmptyTextType);
		EmbeddingResponse response = embeddingModel.call(request);

		// Note: Mocked response passes, real API may fail with empty textType.
		assertThat(response.getResults()).hasSize(1);
		assertThat(response.getResults().get(0).getOutput()).containsExactly(embeddingVector);
		assertThat(response.getResults().get(0).getIndex()).isEqualTo(0);
	}

    @Test
    void testBuilder() {
        DashScopeEmbeddingModel model1 = DashScopeEmbeddingModel.builder()
                .dashScopeApi(dashScopeApi)
                .build();
        DashScopeEmbeddingModel model2 = DashScopeEmbeddingModel.builder()
            .dashScopeApi(dashScopeApi)
            .metadataMode(MetadataMode.EMBED)
            .defaultOptions(defaultOptions)
            .retryTemplate(RetryUtils.DEFAULT_RETRY_TEMPLATE)
            .observationRegistry(ObservationRegistry.NOOP)
            .build();

        DashScopeEmbeddingModel clone1 = model1.clone();
        DashScopeEmbeddingModel clone2 = model2.clone();

        Builder mutate1 = model1.mutate();
        Builder mutate2 = model2.mutate();

        assertThat(model1).isNotNull();
        assertThat(model2).isNotNull();
        assertThat(clone1).isNotNull();
        assertThat(clone2).isNotNull();
        assertThat(mutate1).isNotNull();
        assertThat(mutate2).isNotNull();
    }

    @Test
    void testDimensions() {
        DashScopeApi api = DashScopeApi.builder().apiKey("sk-123").build();
        DashScopeEmbeddingModel model = DashScopeEmbeddingModel.builder()
                .dashScopeApi(api)
                .metadataMode(MetadataMode.EMBED)
                .defaultOptions(DashScopeEmbeddingOptions.builder().model(EmbeddingModel.EMBEDDING_V3.getValue()).build())
                .build();

        DashScopeEmbeddingModel spyModel = spy(model);
        int dimensions = spyModel.dimensions();

        assertThat(dimensions).isEqualTo(1024);
        verify(spyModel, never()).embed(anyString());
    }

    @Test
    void testTotalTokens() {
        float[] embeddingVector = { 0.1f, 0.2f, 0.3f };
        Embedding embedding = new Embedding(0, embeddingVector);
        Embeddings embeddings = new Embeddings(List.of(embedding));
        EmbeddingUsage usage = new EmbeddingUsage(10L);
        EmbeddingList embeddingList = new EmbeddingList(TEST_REQUEST_ID, null, null, embeddings, usage);
        ResponseEntity<EmbeddingList> responseEntity = ResponseEntity.ok(embeddingList);

        when(dashScopeApi.embeddings(any())).thenReturn(responseEntity);

        EmbeddingResponse response = embeddingModel.embedForResponse(List.of(TEST_TEXT));

        assertThat(response.getMetadata().getUsage().getTotalTokens()).isEqualTo(10L);
    }

	@Test
	void testOutputTypePropagation() {
		float[] embeddingVector = { 0.1f, 0.2f, 0.3f };
		Embedding embedding = new Embedding(0, embeddingVector);
		Embeddings embeddings = new Embeddings(List.of(embedding));
		EmbeddingList embeddingList = new EmbeddingList(TEST_REQUEST_ID, null, null, embeddings, new EmbeddingUsage(10L));
		when(dashScopeApi.embeddings(any())).thenReturn(ResponseEntity.ok(embeddingList));

		DashScopeEmbeddingOptions options = DashScopeEmbeddingOptions.builder()
			.model(TEST_MODEL)
			.textType(TEST_TEXT_TYPE)
			.dimensions(TEST_DIMENSION)
			.outputType(DashScopeEmbeddingOptions.OUTPUT_TYPE_DENSE_AND_SPARSE)
			.build();

		embeddingModel.call(new EmbeddingRequest(List.of(TEST_TEXT), options));

		ArgumentCaptor<DashScopeApiSpec.EmbeddingRequest> captor = ArgumentCaptor.forClass(DashScopeApiSpec.EmbeddingRequest.class);
		verify(dashScopeApi).embeddings(captor.capture());
		assertThat(captor.getValue().parameters().outputType())
			.isEqualTo(DashScopeEmbeddingOptions.OUTPUT_TYPE_DENSE_AND_SPARSE);
	}

	@Test
	void testSparseEmbeddingStoredInMetadata() {
		DashScopeApiSpec.SparseEmbeddingItem sparseItem = new DashScopeApiSpec.SparseEmbeddingItem(108386, "你好", 2.3828f);
		Embedding embedding = new Embedding(0, null, List.of(sparseItem));
		Embeddings embeddings = new Embeddings(List.of(embedding));
		EmbeddingList embeddingList = new EmbeddingList(TEST_REQUEST_ID, null, null, embeddings, new EmbeddingUsage(2L));
		when(dashScopeApi.embeddings(any())).thenReturn(ResponseEntity.ok(embeddingList));

		DashScopeEmbeddingOptions options = DashScopeEmbeddingOptions.builder()
			.model(TEST_MODEL)
			.textType(TEST_TEXT_TYPE)
			.outputType(DashScopeEmbeddingOptions.OUTPUT_TYPE_SPARSE)
			.build();

		EmbeddingResponse response = embeddingModel.call(new EmbeddingRequest(List.of("你好"), options));

		assertThat(response.getResults()).hasSize(1);
		assertThat(response.getResults().get(0).getOutput()).isEmpty();
		@SuppressWarnings("unchecked")
		Map<Integer, List<DashScopeApiSpec.SparseEmbeddingItem>> sparseEmbeddings =
				(Map<Integer, List<DashScopeApiSpec.SparseEmbeddingItem>>) response.getMetadata()
					.get(DashScopeEmbeddingModel.SPARSE_EMBEDDINGS_METADATA);
		assertThat(sparseEmbeddings).containsKey(0);
		assertThat(sparseEmbeddings.get(0)).hasSize(1);
		assertThat(sparseEmbeddings.get(0).get(0).index()).isEqualTo(108386);
		assertThat(sparseEmbeddings.get(0).get(0).token()).isEqualTo("你好");
		assertThat(sparseEmbeddings.get(0).get(0).value()).isEqualTo(2.3828f);
	}

	@Test
	void testDenseAndSparseEmbeddingsAreBothReturned() {
		float[] vector1 = { 0.1f, 0.2f, 0.3f };
		float[] vector2 = { 0.4f, 0.5f, 0.6f };
		DashScopeApiSpec.SparseEmbeddingItem sparse1 = new DashScopeApiSpec.SparseEmbeddingItem(7149, "风", 0.829f);
		DashScopeApiSpec.SparseEmbeddingItem sparse2 = new DashScopeApiSpec.SparseEmbeddingItem(246351, "渚", 1.0483f);
		List<Embedding> apiEmbeddings = List.of(
				new Embedding(0, vector1, List.of(sparse1)),
				new Embedding(1, vector2, List.of(sparse2)));
		EmbeddingList embeddingList = new EmbeddingList(TEST_REQUEST_ID, null, null, new Embeddings(apiEmbeddings),
				new EmbeddingUsage(27L));
		when(dashScopeApi.embeddings(any())).thenReturn(ResponseEntity.ok(embeddingList));

		DashScopeEmbeddingOptions options = DashScopeEmbeddingOptions.builder()
			.model(TEST_MODEL)
			.textType(TEST_TEXT_TYPE)
			.outputType(DashScopeEmbeddingOptions.OUTPUT_TYPE_DENSE_AND_SPARSE)
			.build();

		EmbeddingResponse response = embeddingModel.call(new EmbeddingRequest(List.of("第一句", "第二句"), options));

		assertThat(response.getResults()).hasSize(2);
		assertThat(response.getResults().get(0).getOutput()).containsExactly(vector1);
		assertThat(response.getResults().get(1).getOutput()).containsExactly(vector2);
		assertThat(response.getResults().get(0).getIndex()).isEqualTo(0);
		assertThat(response.getResults().get(1).getIndex()).isEqualTo(1);
		@SuppressWarnings("unchecked")
		Map<Integer, List<DashScopeApiSpec.SparseEmbeddingItem>> sparseEmbeddings =
				(Map<Integer, List<DashScopeApiSpec.SparseEmbeddingItem>>) response.getMetadata()
					.get(DashScopeEmbeddingModel.SPARSE_EMBEDDINGS_METADATA);
		assertThat(sparseEmbeddings).hasSize(2);
		assertThat(sparseEmbeddings.get(0).get(0).token()).isEqualTo("风");
		assertThat(sparseEmbeddings.get(1).get(0).token()).isEqualTo("渚");
	}

	@Test
	void testDocumentEmbeddingPathShouldPreserveOutputType() {
		float[] embeddingVector = { 0.1f, 0.2f, 0.3f };
		Embedding embedding = new Embedding(0, embeddingVector,
				List.of(new DashScopeApiSpec.SparseEmbeddingItem(108386, "你好", 2.3828f)));
		Embeddings embeddings = new Embeddings(List.of(embedding));
		EmbeddingList embeddingList = new EmbeddingList(TEST_REQUEST_ID, null, null, embeddings, new EmbeddingUsage(10L));
		when(dashScopeApi.embeddings(any())).thenReturn(ResponseEntity.ok(embeddingList));

		DashScopeEmbeddingOptions options = DashScopeEmbeddingOptions.builder()
			.outputType(DashScopeEmbeddingOptions.OUTPUT_TYPE_DENSE_AND_SPARSE)
			.build();

		embeddingModel.embed(List.of(new Document("你好")), options, new TokenCountBatchingStrategy());

		ArgumentCaptor<DashScopeApiSpec.EmbeddingRequest> captor = ArgumentCaptor.forClass(DashScopeApiSpec.EmbeddingRequest.class);
		verify(dashScopeApi).embeddings(captor.capture());
		assertThat(captor.getValue().parameters().outputType())
			.isEqualTo(DashScopeEmbeddingOptions.OUTPUT_TYPE_DENSE_AND_SPARSE);
	}

	@Test
	void testDocumentEmbeddingPathShouldRejectSparseOnlyOutput() {
		Embedding embedding = new Embedding(0, null,
				List.of(new DashScopeApiSpec.SparseEmbeddingItem(108386, "你好", 2.3828f)));
		Embeddings embeddings = new Embeddings(List.of(embedding));
		EmbeddingList embeddingList = new EmbeddingList(TEST_REQUEST_ID, null, null, embeddings, new EmbeddingUsage(2L));
		when(dashScopeApi.embeddings(any())).thenReturn(ResponseEntity.ok(embeddingList));

		DashScopeEmbeddingOptions options = DashScopeEmbeddingOptions.builder()
			.outputType(DashScopeEmbeddingOptions.OUTPUT_TYPE_SPARSE)
			.build();

		assertThatThrownBy(() -> embeddingModel.embed(List.of(new Document("你好")), options, new TokenCountBatchingStrategy()))
			.isInstanceOf(IllegalStateException.class)
			.hasMessageContaining("sparse-only");
	}

	@Test
	void testTextEmbeddingPathShouldRejectSparseOnlyDefaultOutput() {
		DashScopeEmbeddingModel sparseDefaultModel = new DashScopeEmbeddingModel(dashScopeApi, MetadataMode.EMBED,
				DashScopeEmbeddingOptions.builder()
					.model(TEST_MODEL)
					.textType(TEST_TEXT_TYPE)
					.outputType(DashScopeEmbeddingOptions.OUTPUT_TYPE_SPARSE)
					.build());

		assertThatThrownBy(() -> sparseDefaultModel.embed(List.of("你好")))
			.isInstanceOf(IllegalStateException.class)
			.hasMessageContaining("sparse-only");
	}

}
