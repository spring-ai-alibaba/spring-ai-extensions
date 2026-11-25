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

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Map;

import com.alibaba.cloud.ai.dashscope.rag.DashScopeRagApi.ChatMessage;
import com.alibaba.cloud.ai.dashscope.rag.DashScopeRagApi.KnowledgeBaseFilters;
import com.alibaba.cloud.ai.dashscope.rag.DashScopeRagApi.RagApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.headerDoesNotExist;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class DashScopeRagApiTests {

	private static final String BASE = "https://rag.example.test";

	private MockRestServiceServer server;

	private MockRestServiceServer oss;

	private DashScopeRagApi api;

	@BeforeEach
	void setUp() {
		RestClient.Builder rest = RestClient.builder();
		RestClient.Builder upload = RestClient.builder();
		this.server = MockRestServiceServer.bindTo(rest).build();
		this.oss = MockRestServiceServer.bindTo(upload).build();
		this.api = new DashScopeRagApi(URI.create(BASE), "test-key", rest, WebClient.builder(), upload.build());
	}

	@Test
	void retrievesWithNestedFiltersAndPreservesScoresAndIds() {
		var filter = Map.<String, Object>of("_operator", "OR", "_conditions",
				List.of(Map.of("department", "Research"), Map.of("department", "Engineering")));
		this.server.expect(requestTo(BASE + "/api/v1/indices/rag/index/retrieve"))
			.andExpect(method(HttpMethod.POST)).andExpect(header("Authorization", "Bearer test-key"))
			.andExpect(jsonPath("$.index_id").value("kb-1")).andExpect(jsonPath("$.top_k").value(1))
			.andExpect(jsonPath("$.search_filters[0]._conditions[1].department").value("Engineering"))
			.andRespond(withSuccess("""
					{"success":true,"data":{"nodes":[{"text":"result","score":0.75,
					"metadata":{"_id":"chunk-1","doc_id":"doc-1","image_url":"https://media.example.test/image.png"}},{"text":"extra","score":0.3}]}}
					""", MediaType.APPLICATION_JSON));
		var hits = this.api.retrieve("kb-1", "query", 1, List.of(filter));
		assertThat(hits).singleElement().satisfies(hit -> {
			assertThat(hit.getId()).isEqualTo("kb-1:chunk-1");
			assertThat(hit.getScore()).isEqualTo(0.75);
			assertThat(hit.getMetadata()).containsEntry("index_id", "kb-1").containsEntry("doc_id", "doc-1")
				.containsKey("image_url");
		});
		this.server.verify();
	}

	@Test
	void hostedSearchSendsImageAndPerIndexFilters() {
		this.server.expect(requestTo(BASE + "/api/v1/indices/knowledge/search"))
			.andExpect(jsonPath("$.agent_id").value("aid-test"))
			.andExpect(jsonPath("$.images[0]").value("https://media.example.test/image.png"))
			.andExpect(jsonPath("$.kb_search_configs[0].id").value("kb-1"))
			.andExpect(jsonPath("$.kb_search_configs[0].search_filters[0].tags[0]").value("approved"))
			.andRespond(withSuccess("""
					{"success":true,"data":{"nodes":[]}}
					""", MediaType.APPLICATION_JSON));
		assertThat(this.api.search("aid-test", "", List.of("https://media.example.test/image.png"),
				List.of(new KnowledgeBaseFilters("kb-1", List.of(Map.of("tags", List.of("approved"))))))).isEmpty();
		this.server.verify();
	}

	@Test
	void distinguishesBusinessFailureFromNoHits() {
		this.server.expect(requestTo(BASE + "/api/v1/indices/rag/index/retrieve"))
			.andRespond(withSuccess("""
					{"success":false,"status_code":401,"code":"InvalidApiKey","request_id":"request-1"}
					""", MediaType.APPLICATION_JSON));
		assertThatThrownBy(() -> this.api.retrieve("kb-1", "query", 3, List.of()))
			.isInstanceOfSatisfying(RagApiException.class, exception -> {
				assertThat(exception.getCode()).isEqualTo("InvalidApiKey");
				assertThat(exception.getRequestId()).isEqualTo("request-1");
			});
		this.server.verify();
	}

	@Test
	void registersImportsAndDeletesUsingSeparateIdentifiers() throws Exception {
		this.server.expect(requestTo(BASE + "/api/v1/connector/dash/applyFileUploadLease"))
			.andExpect(jsonPath("$.sizeBytes").value("3"))
			.andExpect(jsonPath("$.contentMd5").value("kAFQmDzST7DWlj99KOF/cg=="))
			.andRespond(withSuccess("""
					{"code":"Success","data":{"leaseId":"lease-1","param":{"url":"https://oss.example.test/file?sig=abc",
					"method":"PUT","headers":{"Content-Type":"text/plain","x-bailian-extra":"lease-token"}}}}
					""", MediaType.APPLICATION_JSON));
		this.oss.expect(requestTo("https://oss.example.test/file?sig=abc")).andExpect(method(HttpMethod.PUT))
			.andExpect(headerDoesNotExist("Authorization")).andExpect(header("x-bailian-extra", "lease-token"))
			.andExpect(content().bytes("abc".getBytes(StandardCharsets.UTF_8))).andRespond(withSuccess());
		this.server.expect(requestTo(BASE + "/api/v1/connector/dash/addFile"))
			.andExpect(jsonPath("$.leaseId").value("lease-1"))
			.andRespond(withSuccess("""
					{"code":"Success","status_code":200,"data":{"fileId":"file-1"}}
					""", MediaType.APPLICATION_JSON));
		this.server.expect(requestTo(BASE + "/api/v1/indices/rag/index/job/create"))
			.andExpect(jsonPath("$.sourceType").value("DATA_CENTER_FILE")).andExpect(jsonPath("$.docIds[0]").value("file-1"))
			.andRespond(withSuccess("""
					{"success":true,"data":{"ingestionId":"job-1","pipelineId":"kb-1","status":"PENDING"}}
					""", MediaType.APPLICATION_JSON));
		this.server.expect(requestTo(BASE + "/api/v1/indices/rag/index_job/status?index_id=kb-1&job_id=job-1&page_number=1&page_size=10"))
			.andExpect(method(HttpMethod.GET))
			.andRespond(withSuccess("""
					{"success":true,"data":{"ingestion_status":"COMPLETED","total_count":1,
					"rows":[{"doc_id":"doc-1","code":"FINISH","status":"FINISH"}]}}
					""", MediaType.APPLICATION_JSON));
		this.server.expect(requestTo(BASE + "/api/v1/indices/rag/index/delete_file"))
			.andExpect(jsonPath("$.doc_ids[0]").value("doc-1"))
			.andRespond(withSuccess("""
					{"success":true}
					""", MediaType.APPLICATION_JSON));
		this.server.expect(requestTo(BASE + "/api/v1/connector/dash/deleteFile"))
			.andExpect(jsonPath("$.fileId").value("file-1"))
			.andRespond(withSuccess("""
					{"code":"Success","status_code":200}
					""", MediaType.APPLICATION_JSON));
		var file = new InputStreamResource(new ByteArrayInputStream("abc".getBytes(StandardCharsets.UTF_8))) {

			@Override
			public String getFilename() {
				return "sample.txt";
			}
		};
		var job = this.api.submitImport("kb-1", List.of(this.api.upload(file, "default", List.of())));
		var documents = this.api.awaitImport("kb-1", job.ingestionId(), Duration.ofSeconds(2), Duration.ofMillis(20));
		this.api.deleteDocuments("kb-1", documents.stream().map(DashScopeRagApi.ImportedDocument::id).toList());
		this.api.deleteFile("file-1");
		this.server.verify();
		this.oss.verify();
	}

	@Test
	void hostedChatPreservesEventsAndReportsAnErrorFrame() {
		var web = WebClient.builder().exchangeFunction(request -> {
			assertThat(request.url().getPath()).isEqualTo("/api/v2/apps/knowledge/chat");
			assertThat(request.headers().getFirst("Authorization")).isEqualTo("Bearer test-key");
			return Mono.just(ClientResponse.create(HttpStatus.OK)
				.header("Content-Type", MediaType.TEXT_EVENT_STREAM_VALUE)
				.body("""
						data: {"output":{"step":"planning","message":{"content":"plan"}}}

						data: {"output":{"step":"generating","message":{"content":"answer"}}}

						event: error
						data: {"code":"AgentApp.NotFound","request_id":"request-2"}

						""").build());
		});
		var api = new DashScopeRagApi(URI.create(BASE), "test-key", RestClient.builder(), web, RestClient.create());
		StepVerifier.create(api.chat("aid-test", List.of(new ChatMessage("user", "question")), List.of()))
			.assertNext(event -> assertThat(event.payload().path("output").path("step").asString()).isEqualTo("planning"))
			.assertNext(event -> assertThat(event.payload().path("output").path("step").asString()).isEqualTo("generating"))
			.expectErrorSatisfies(error -> assertThat(error).isInstanceOfSatisfying(RagApiException.class,
					exception -> assertThat(exception.getRequestId()).isEqualTo("request-2")))
			.verify(Duration.ofSeconds(5));
	}

}
