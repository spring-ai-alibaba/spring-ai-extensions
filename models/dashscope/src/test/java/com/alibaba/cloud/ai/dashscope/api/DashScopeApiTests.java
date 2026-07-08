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
package com.alibaba.cloud.ai.dashscope.api;

import com.alibaba.cloud.ai.dashscope.rag.DashScopeStoreOptions;
import com.alibaba.cloud.ai.dashscope.spec.DashScopeModel.ChatModel;
import com.alibaba.cloud.ai.dashscope.spec.DashScopeModel.EmbeddingModel;
import com.alibaba.cloud.ai.dashscope.spec.DashScopeApiSpec.EmbeddingRequest;
import com.alibaba.cloud.ai.dashscope.spec.DashScopeApiSpec.EmbeddingRequestInput;
import com.alibaba.cloud.ai.dashscope.spec.DashScopeApiSpec.EmbeddingRequestInputParameters;
import com.alibaba.cloud.ai.dashscope.spec.DashScopeModel.EmbeddingTextType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.ai.document.Document;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * Tests for DashScopeApi class functionality
 *
 * @author yuluo
 * @author <a href="mailto:yuluo08290126@gmail.com">yuluo</a>
 * @author brianxiadong
 * @since 1.0.0-M2
 */
class DashScopeApiTests {

	private DashScopeApi dashScopeApi;

	private RestClient mockRestClient;

	@BeforeEach
	void setUp() {
		// Setup mock RestClient
		mockRestClient = mock(RestClient.class);

		// Initialize DashScopeApi with test API key
		dashScopeApi = DashScopeApi.builder().apiKey("test-api-key").build();
	}

	@Test
	void testChatModelEnum() {
		System.out.println("output: " + dashScopeApi.getApiKey());
		// Test ChatModel enum values
		assertEquals("qwen-max", ChatModel.QWEN_MAX.getValue(), "ChatModel.QWEN_MAX should have value 'qwen-max'");
		assertEquals("qwen-max-longcontext", ChatModel.QWEN_MAX_LONGCONTEXT.getValue(),
				"ChatModel.QWEN_MAX_LONGCONTEXT should have value 'qwen-max-longcontext'");
		assertEquals("qwen-plus", ChatModel.QWEN_PLUS.getValue(), "ChatModel.QWEN_PLUS should have value 'qwen-plus'");
		assertEquals("qwen-turbo", ChatModel.QWEN_TURBO.getValue(),
				"ChatModel.QWEN_TURBO should have value 'qwen-turbo'");
	}

	@Test
	void upsertPipelineShouldAddDocumentsWithoutCreatingPipelineWhenPipelineAlreadyExists() {
		RestClient.Builder restClientBuilder = RestClient.builder();
		MockRestServiceServer server = MockRestServiceServer.bindTo(restClientBuilder).build();
		DashScopeApi api = DashScopeApi.builder()
			.apiKey("test-api-key")
			.restClientBuilder(restClientBuilder)
			.build();
		DashScopeStoreOptions options = new DashScopeStoreOptions("existing-index");

		server.expect(once(),
				requestTo("https://dashscope.aliyuncs.com/api/v1/indices/pipeline_simple?pipeline_name=existing-index"))
			.andExpect(method(HttpMethod.GET))
			.andRespond(withSuccess("""
					{"id":"pipeline-1","status":"SUCCESS"}
					""", MediaType.APPLICATION_JSON));
		server.expect(once(), requestTo("https://dashscope.aliyuncs.com/api/v1/indices/pipeline/pipeline-1/documents"))
			.andExpect(method(HttpMethod.PUT))
			.andRespond(withSuccess("""
					{"ingestionId":"ingestion-1","code":"SUCCESS"}
					""", MediaType.APPLICATION_JSON));

		assertThatCode(() -> api.upsertPipeline(List.of(new Document("file-1", "content", Map.of())), options))
			.doesNotThrowAnyException();

		server.verify();
	}

	@Test
	void upsertPipelineShouldFallbackToExistingPipelineWhenCreateFails() {
		RestClient.Builder restClientBuilder = RestClient.builder();
		MockRestServiceServer server = MockRestServiceServer.bindTo(restClientBuilder).build();
		DashScopeApi api = DashScopeApi.builder()
			.apiKey("test-api-key")
			.restClientBuilder(restClientBuilder)
			.build();
		DashScopeStoreOptions options = new DashScopeStoreOptions("race-index");

		server.expect(once(),
				requestTo("https://dashscope.aliyuncs.com/api/v1/indices/pipeline_simple?pipeline_name=race-index"))
			.andExpect(method(HttpMethod.GET))
			.andRespond(withSuccess("""
					{"status":"SUCCESS"}
					""", MediaType.APPLICATION_JSON));
		server.expect(once(), requestTo("https://dashscope.aliyuncs.com/api/v1/indices/pipeline"))
			.andExpect(method(HttpMethod.PUT))
			.andRespond(withSuccess("""
					{"status":"FAILED","message":"duplicate pipeline"}
					""", MediaType.APPLICATION_JSON));
		server.expect(once(),
				requestTo("https://dashscope.aliyuncs.com/api/v1/indices/pipeline_simple?pipeline_name=race-index"))
			.andExpect(method(HttpMethod.GET))
			.andRespond(withSuccess("""
					{"id":"pipeline-1","status":"SUCCESS"}
					""", MediaType.APPLICATION_JSON));
		server.expect(once(), requestTo("https://dashscope.aliyuncs.com/api/v1/indices/pipeline/pipeline-1/documents"))
			.andExpect(method(HttpMethod.PUT))
			.andRespond(withSuccess("""
					{"ingestionId":"ingestion-2","code":"SUCCESS"}
					""", MediaType.APPLICATION_JSON));

		assertThatCode(() -> api.upsertPipeline(List.of(new Document("file-1", "content", Map.of())), options))
			.doesNotThrowAnyException();

		server.verify();
	}

	// The DATA_CENTER_FILE data source expects `component` to be a single object (as the
	// add/upsert path already sends). Wrapping it in an array made the delete request body
	// malformed and DashScope answered 500 SystemError (gh#288). Assert `component` is an
	// object whose `doc_ids` are directly addressable; the old array shape fails this.
	@Test
	void deletePipelineDocumentShouldSendComponentAsObjectNotArray() {
		RestClient.Builder restClientBuilder = RestClient.builder();
		MockRestServiceServer server = MockRestServiceServer.bindTo(restClientBuilder).build();
		DashScopeApi api = DashScopeApi.builder()
			.apiKey("test-api-key")
			.restClientBuilder(restClientBuilder)
			.build();

		server.expect(once(), requestTo("https://dashscope.aliyuncs.com/api/v1/indices/pipeline/pipeline-1/delete"))
			.andExpect(method(HttpMethod.POST))
			.andExpect(jsonPath("$.data_sources[0].source_type").value("DATA_CENTER_FILE"))
			.andExpect(jsonPath("$.data_sources[0].component.doc_ids[0]").value("doc-1"))
			.andExpect(jsonPath("$.data_sources[0].component.doc_ids[1]").value("doc-2"))
			.andRespond(withSuccess("""
					{"status":"200","code":"SUCCESS"}
					""", MediaType.APPLICATION_JSON));

		boolean deleted = api.deletePipelineDocument("pipeline-1", List.of("doc-1", "doc-2"));

		assertEquals(true, deleted);
		server.verify();
	}

	@Test
	void testEmbeddingModelEnum() {
		// Test EmbeddingModel enum values
		assertEquals("text-embedding-v2", EmbeddingModel.EMBEDDING_V2.getValue(),
				"EmbeddingModel.EMBEDDING_V2 should have value 'text-embedding-v2'");
		assertEquals("text-embedding-v1", EmbeddingModel.EMBEDDING_V1.getValue(),
				"EmbeddingModel.EMBEDDING_V1 should have value 'text-embedding-v1'");
	}

	@Test
	void testEmbeddingTextTypeEnum() {
		// Test EmbeddingTextType enum values
		assertEquals("document", EmbeddingTextType.DOCUMENT.getValue(),
				"EmbeddingTextType.DOCUMENT should have value 'document'");
		assertEquals("query", EmbeddingTextType.QUERY.getValue(), "EmbeddingTextType.QUERY should have value 'query'");
	}

	@Test
	void testEmbeddingRequestWithNullTextType() {
		// Test null textType handling in EmbeddingRequestInputParameters.Builder
		EmbeddingRequestInputParameters params = EmbeddingRequestInputParameters.builder()
				.textType(null)
				.build();

		// Should default to "document" when textType is null
		assertEquals("document", params.textType(), "Null textType should default to 'document'");
	}

	@Test
	void testEmbeddingRequestWithEmptyTextType() {
		// Test empty string textType handling
		EmbeddingRequestInputParameters params = EmbeddingRequestInputParameters.builder()
				.textType("")
				.build();

		// Empty string should be preserved (not converted to null)
		assertEquals("", params.textType(), "Empty textType should be preserved");
	}

	@Test
	void testEmbeddingRequestWithValidTextType() {
		// Test valid textType values
		EmbeddingRequestInputParameters queryParams = EmbeddingRequestInputParameters.builder()
				.textType("query")
				.build();
		assertEquals("query", queryParams.textType(), "Valid textType 'query' should be preserved");

		EmbeddingRequestInputParameters docParams = EmbeddingRequestInputParameters.builder()
				.textType("document")
				.build();
		assertEquals("document", docParams.textType(), "Valid textType 'document' should be preserved");
	}

	@Test
	void testEmbeddingRequestBuilderWithNullTextType() {
		// Test EmbeddingRequest creation with null textType through constructor
		EmbeddingRequestInput input = new EmbeddingRequestInput(List.of("text1", "text2"));
		EmbeddingRequestInputParameters params = EmbeddingRequestInputParameters.builder()
				.textType(null)
				.build();
		EmbeddingRequest request = new EmbeddingRequest("test-model", input, params);

		// Verify the request was created successfully with default textType
		assertNotNull(request, "EmbeddingRequest should be created successfully");
		assertEquals("document", request.parameters().textType(),
				"Request should have default textType 'document' when null is provided");
	}

}
