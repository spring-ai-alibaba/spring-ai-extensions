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

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jspecify.annotations.Nullable;
import reactor.core.publisher.Flux;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import org.springframework.ai.document.Document;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.util.Assert;
import org.springframework.util.DigestUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Client for the workspace-scoped Bailian RAG gateway.
 */
public final class DashScopeRagApi {

	private static final String INDEX = "/api/v1/indices/rag/index";

	private static final String CONNECTOR = "/api/v1/connector/dash/";

	private final RestClient restClient;

	private final RestClient uploadClient;

	private final WebClient webClient;

	public DashScopeRagApi(String workspaceId, String apiKey) {
		this(workspaceUrl(workspaceId), apiKey, RestClient.builder(), WebClient.builder(), RestClient.create());
	}

	public DashScopeRagApi(URI baseUrl, String apiKey, RestClient.Builder restClient, WebClient.Builder webClient,
			RestClient uploadClient) {
		Assert.hasText(apiKey, "A Bailian API key is required");
		this.restClient = restClient.clone().baseUrl(baseUrl.toString())
			.defaultHeaders(headers -> headers.setBearerAuth(apiKey)).build();
		this.webClient = webClient.clone().baseUrl(baseUrl.toString())
			.defaultHeaders(headers -> headers.setBearerAuth(apiKey)).build();
		this.uploadClient = uploadClient;
	}

	public static URI workspaceUrl(String workspaceId) {
		Assert.isTrue(workspaceId != null && workspaceId.matches("[a-zA-Z0-9-]+"), "Invalid Bailian workspace ID");
		return URI.create("https://" + workspaceId + ".cn-beijing.maas.aliyuncs.com");
	}

	public List<Document> retrieve(String indexId, String query, int topK, List<Map<String, Object>> filters) {
		Assert.hasText(indexId, "A knowledge base ID is required");
		Assert.hasText(query, "A text query is required");
		Assert.isTrue(topK > 0, "topK must be positive");
		var body = Map.of("index_id", indexId, "query", query, "top_k", topK, "search_filters", filters);
		RetrievalData data = post(INDEX + "/retrieve", body, new ParameterizedTypeReference<>() { });
		return documents(data, indexId).stream().limit(topK).toList();
	}

	public List<Document> search(String agentId, String query, List<String> images, List<KnowledgeBaseFilters> filters) {
		Assert.hasText(agentId, "A published retrieval service ID is required");
		Assert.isTrue((query != null && !query.isBlank()) || !images.isEmpty(), "A query or images are required");
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("agent_id", agentId);
		if (query != null && !query.isBlank()) {
			body.put("query", query);
		}
		if (!images.isEmpty()) {
			body.put("images", images);
		}
		if (!filters.isEmpty()) {
			body.put("kb_search_configs", filters);
		}
		RetrievalData data = post("/api/v1/indices/knowledge/search", body, new ParameterizedTypeReference<>() { });
		return documents(data, null);
	}

	public Flux<ChatEvent> chat(String agentId, List<ChatMessage> messages, List<KnowledgeBaseFilters> filters) {
		Assert.hasText(agentId, "A published knowledge chat service ID is required");
		Assert.notEmpty(messages, "Chat messages are required");
		Map<String, Object> agentOptions = new LinkedHashMap<>();
		agentOptions.put("agent_id", agentId);
		if (!filters.isEmpty()) {
			agentOptions.put("kb_search_configs", filters);
		}
		var body = Map.of("input", Map.of("messages", messages),
				"parameters", Map.of("agent_options", agentOptions), "stream", true);
		return this.webClient.post().uri("/api/v2/apps/knowledge/chat")
			.contentType(MediaType.APPLICATION_JSON).accept(MediaType.TEXT_EVENT_STREAM).bodyValue(body).retrieve()
			.bodyToFlux(new ParameterizedTypeReference<ServerSentEvent<String>>() { })
			.takeWhile(event -> !"[DONE]".equals(event.data()))
			.<ChatEvent>handle((event, sink) -> {
				String data = event.data();
				if (data == null || data.isBlank() || "[DONE]".equals(data)) {
					return;
				}
				JsonNode payload = JsonMapper.shared().readTree(data);
				if ("error".equals(event.event()) || payload.path("status_code").asInt(200) >= 400
						|| payload.path("success").isBoolean() && !payload.path("success").asBoolean()) {
					sink.error(new RagApiException(payload.path("code").asString("KnowledgeChat.Error"),
							payload.path("request_id").asString(""), payload.path("message").asString("Knowledge chat failed"),
							null));
					return;
				}
				sink.next(new ChatEvent(event.event(), payload));
			});
	}

	/**
	 * Registers one reusable knowledge-base file. Reuse the returned file ID when
	 * submitting an import for an existing knowledge base.
	 */
	public String upload(Resource file, String category, List<String> tags) throws IOException {
		Assert.hasText(category, "A data center category is required");
		String name = file.getFilename();
		Assert.hasText(name, "The upload must have a filename");
		if (!file.isOpen()) {
			return registerFile(file, Objects.requireNonNull(name), category, tags);
		}
		var temporaryFile = Files.createTempFile("bailian-rag-", ".upload");
		try {
			try (InputStream input = file.getInputStream()) {
				Files.copy(input, temporaryFile, StandardCopyOption.REPLACE_EXISTING);
			}
			return registerFile(new FileSystemResource(temporaryFile), Objects.requireNonNull(name), category, tags);
		}
		finally {
			Files.deleteIfExists(temporaryFile);
		}
	}

	private String registerFile(Resource file, String name, String category, List<String> tags) throws IOException {
		String md5;
		try (InputStream input = file.getInputStream()) {
			md5 = Base64.getEncoder().encodeToString(DigestUtils.md5Digest(input));
		}
		UploadLease lease = post(CONNECTOR + "applyFileUploadLease",
				Map.of("category", category, "categoryType", "UNSTRUCTURED", "fileName", name,
						"sizeBytes", Long.toString(file.contentLength()), "contentMd5", md5),
				new ParameterizedTypeReference<>() { });
		URI uploadUrl = URI.create(lease.param().url());
		Assert.isTrue("https".equalsIgnoreCase(uploadUrl.getScheme()), "The upload lease must use HTTPS");
		Assert.isTrue("PUT".equals(lease.param().method()), "The upload lease must use PUT");
		this.uploadClient.put().uri(uploadUrl)
			.headers(headers -> lease.param().headers().forEach(headers::set))
			.body(file).retrieve().toBodilessEntity();
		RegisteredFile registered = post(CONNECTOR + "addFile",
				Map.of("leaseId", lease.leaseId(), "category", category, "categoryType", "UNSTRUCTURED",
						"parser", "AUTO_SELECT", "tags", tags),
				new ParameterizedTypeReference<>() { });
		Assert.hasText(registered.fileId(), "Bailian did not return a registered file ID");
		return registered.fileId();
	}

	public void deleteFile(String fileId) {
		Assert.hasText(fileId, "A data center file ID is required");
		exchange(this.restClient.post().uri(CONNECTOR + "deleteFile").contentType(MediaType.APPLICATION_JSON)
			.body(Map.of("fileId", fileId)), new ParameterizedTypeReference<Envelope<Object>>() { }, false);
	}

	public ImportJob createDocumentIndex(String name, String description, String embeddingModel, List<String> fileIds) {
		Assert.hasText(name, "A knowledge base name is required");
		Assert.hasText(embeddingModel, "An embedding model is required");
		Assert.notEmpty(fileIds, "At least one registered file ID is required");
		return post(INDEX + "/create_v2",
				Map.of("name", name, "description", description, "structureType", "unstructured",
						"sinkType", "BUILT_IN", "sourceType", "DATA_CENTER_FILE",
						"embeddingModelName", embeddingModel, "docIds", fileIds,
						"dataSources", List.of(Map.of("sourceType", "DATA_CENTER_FILE"))),
				new ParameterizedTypeReference<>() { });
	}

	public Page<KnowledgeBase> listIndices(@Nullable String name, int pageNumber, int pageSize) {
		return Objects.requireNonNull(exchange(this.restClient.get()
			.uri(uri -> uri.path(INDEX + "/list").queryParamIfPresent("pipeline_name", Optional.ofNullable(name))
				.queryParam("page_number", pageNumber).queryParam("page_size", pageSize).build()),
				new ParameterizedTypeReference<Envelope<Page<KnowledgeBase>>>() { }, true));
	}

	public Page<ImportedDocument> listDocuments(String indexId, int pageNumber, int pageSize) {
		Assert.hasText(indexId, "A knowledge base ID is required");
		return Objects.requireNonNull(exchange(this.restClient.get()
			.uri(uri -> uri.path(INDEX + "/files").queryParam("index_id", indexId)
				.queryParam("page_number", pageNumber).queryParam("page_size", pageSize).build()),
				new ParameterizedTypeReference<Envelope<Page<ImportedDocument>>>() { }, true));
	}

	public ImportJob submitImport(String indexId, List<String> fileIds) {
		Assert.hasText(indexId, "A knowledge base ID is required");
		Assert.notEmpty(fileIds, "At least one registered file ID is required");
		return post(INDEX + "/job/create",
				Map.of("indexId", indexId, "sourceType", "DATA_CENTER_FILE", "docIds", fileIds),
				new ParameterizedTypeReference<>() { });
	}

	public ImportStatus importStatus(String indexId, String jobId, int pageNumber, int pageSize) {
		return Objects.requireNonNull(exchange(this.restClient.get()
			.uri(uri -> uri.path("/api/v1/indices/rag/index_job/status")
				.queryParam("index_id", indexId).queryParam("job_id", jobId)
				.queryParam("page_number", pageNumber).queryParam("page_size", pageSize).build()),
				new ParameterizedTypeReference<Envelope<ImportStatus>>() { }, true));
	}

	public List<ImportedDocument> awaitImport(String indexId, String jobId, Duration timeout, Duration pollInterval) {
		Assert.isTrue(!timeout.isNegative() && !timeout.isZero(), "The timeout must be positive");
		Assert.isTrue(pollInterval.toMillis() > 0, "The poll interval must be at least one millisecond");
		long start = System.nanoTime();
		int pageSize = 10;
		while (true) {
			ImportStatus status = importStatus(indexId, jobId, 1, pageSize);
			switch (status.status()) {
				case "COMPLETED" -> {
					List<ImportedDocument> documents = new ArrayList<>(status.rows());
					for (int page = 2; (long) (page - 1) * pageSize < status.totalCount(); page++) {
						documents.addAll(importStatus(indexId, jobId, page, pageSize).rows());
					}
					if (documents.size() != status.totalCount() || documents.isEmpty()
							|| documents.stream().anyMatch(document -> !"FINISH".equals(document.code()))) {
						throw new IllegalStateException("Bailian import has incomplete or failed documents. Job ID " + jobId);
					}
					return List.copyOf(documents);
				}
				case "PENDING", "RUNNING" -> {
					long remaining = timeout.toNanos() - (System.nanoTime() - start);
					if (remaining <= 0) {
						throw new IllegalStateException("Timed out waiting for Bailian import. Job ID " + jobId);
					}
					try {
						TimeUnit.NANOSECONDS.sleep(Math.min(remaining, pollInterval.toNanos()));
					}
					catch (InterruptedException exception) {
						Thread.currentThread().interrupt();
						throw new IllegalStateException("Interrupted while waiting for Bailian import. Job ID " + jobId, exception);
					}
				}
				default -> throw new IllegalStateException("Bailian import status " + status.status() + ". Job ID " + jobId);
			}
		}
	}

	public void deleteDocuments(String indexId, List<String> documentIds) {
		Assert.hasText(indexId, "A knowledge base ID is required");
		Assert.notEmpty(documentIds, "At least one knowledge base document ID is required");
		exchange(this.restClient.post().uri(INDEX + "/delete_file").contentType(MediaType.APPLICATION_JSON)
			.body(Map.of("index_id", indexId, "doc_ids", documentIds)), new ParameterizedTypeReference<Envelope<Object>>() { },
			false);
	}

	private static List<Document> documents(RetrievalData data, @Nullable String indexId) {
		if (data.nodes() == null) {
			throw new IllegalStateException("Bailian retrieval did not return a nodes array");
		}
		return data.nodes().stream().map(node -> {
			Map<String, Object> metadata = new LinkedHashMap<>();
			if (node.metadata() != null) {
				node.metadata().forEach((key, value) -> {
					if (value != null) {
						metadata.put(key, value);
					}
				});
			}
			String knowledgeBase = indexId != null ? indexId : node.indexId();
			if (knowledgeBase != null) {
				metadata.put("index_id", knowledgeBase);
			}
			if (node.score() != null) {
				metadata.put("score", node.score());
			}
			Object chunkId = metadata.getOrDefault("chunk_id", metadata.get("_id"));
			var builder = Document.builder().text(node.text() == null ? "" : node.text())
				.metadata(metadata).score(node.score());
			String id = node.id() != null ? node.id() : (chunkId != null ? chunkId.toString() : null);
			if (id != null && !id.isBlank()) {
				builder.id(knowledgeBase == null ? id : knowledgeBase + ":" + id);
			}
			return builder.build();
		}).toList();
	}

	private <T> T post(String path, Object body, ParameterizedTypeReference<Envelope<T>> type) {
		return Objects.requireNonNull(exchange(
				this.restClient.post().uri(path).contentType(MediaType.APPLICATION_JSON).body(body), type, true));
	}

	private <T> @Nullable T exchange(RestClient.RequestHeadersSpec<?> request,
			ParameterizedTypeReference<Envelope<T>> type, boolean requireData) {
		try {
			Envelope<T> response = request.retrieve().body(type);
			if (response == null) {
				throw new RagApiException("InvalidResponse", "", "Bailian returned an empty response", null);
			}
			boolean success = !"ERROR".equalsIgnoreCase(response.status()) && !"FAILED".equalsIgnoreCase(response.status())
					&& (response.statusCode() == null || response.statusCode() >= 200 && response.statusCode() < 300)
					&& (response.success() != null ? response.success() : "Success".equalsIgnoreCase(response.code()));
			if (!success || requireData && response.data() == null) {
				throw new RagApiException(response.code(), response.requestId(), response.message(), null);
			}
			return response.data();
		}
		catch (RestClientResponseException exception) {
			Envelope<?> error = null;
			try {
				error = exception.getResponseBodyAs(Envelope.class);
			}
			catch (RuntimeException ignored) {
				// A proxy may return a plain text or HTML error.
			}
			throw new RagApiException(error != null ? error.code() : null, error != null ? error.requestId() : null,
					"Bailian RAG HTTP " + exception.getStatusCode().value(), exception);
		}
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	private record Envelope<T>(@Nullable Boolean success, @Nullable String status, @Nullable String code,
			@Nullable String message, @JsonProperty("status_code") @Nullable Integer statusCode,
			@JsonProperty("request_id") @JsonAlias("requestId") @Nullable String requestId, @Nullable T data) { }

	@JsonIgnoreProperties(ignoreUnknown = true)
	private record RetrievalData(@Nullable List<RetrievedNode> nodes) { }

	@JsonIgnoreProperties(ignoreUnknown = true)
	private record RetrievedNode(@JsonAlias({"id_", "chunk_id"}) @Nullable String id, @Nullable String text,
			@Nullable Double score, @Nullable Map<String, Object> metadata,
			@JsonProperty("index_id") @JsonAlias("knowledge_base_id") @Nullable String indexId) { }

	@JsonIgnoreProperties(ignoreUnknown = true)
	private record UploadLease(String leaseId, SignedUpload param) { }

	@JsonIgnoreProperties(ignoreUnknown = true)
	private record SignedUpload(String url, String method, Map<String, String> headers) { }

	@JsonIgnoreProperties(ignoreUnknown = true)
	private record RegisteredFile(String fileId) { }

	@JsonIgnoreProperties(ignoreUnknown = true)
	public record Page<T>(@JsonProperty("page_number") int pageNumber, @JsonProperty("page_size") int pageSize,
			@JsonProperty("total_count") int totalCount, List<T> rows) { }

	@JsonIgnoreProperties(ignoreUnknown = true)
	public record KnowledgeBase(String id, String name, @Nullable String description) { }

	@JsonIgnoreProperties(ignoreUnknown = true)
	public record ImportJob(String ingestionId, String pipelineId, String status) { }

	@JsonIgnoreProperties(ignoreUnknown = true)
	public record ImportStatus(@JsonProperty("ingestion_status") String status,
			@JsonProperty("total_count") int totalCount, List<ImportedDocument> rows) {

		public ImportStatus {
			rows = rows == null ? List.of() : List.copyOf(rows);
		}

	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public record ImportedDocument(@JsonProperty("doc_id") String id, String code, String status,
			@Nullable String message) { }

	public record KnowledgeBaseFilters(String id, @JsonProperty("search_filters") List<Map<String, Object>> filters) { }

	public record ChatMessage(String role, Object content) {

		public ChatMessage {
			Assert.isTrue("user".equals(role) || "assistant".equals(role), "Knowledge chat supports user and assistant roles");
			Assert.isTrue(content instanceof String || content instanceof List<?>, "Content must be text or a list of parts");
		}

	}

	public record ChatEvent(@Nullable String event, JsonNode payload) { }

	public static final class RagApiException extends RuntimeException {

		private final @Nullable String code;

		private final @Nullable String requestId;

		private RagApiException(@Nullable String code, @Nullable String requestId, @Nullable String message,
				@Nullable Throwable cause) {
			super(message + " (code=" + code + ", request_id=" + requestId + ")", cause);
			this.code = code;
			this.requestId = requestId;
		}

		public @Nullable String getCode() {
			return this.code;
		}

		public @Nullable String getRequestId() {
			return this.requestId;
		}

	}

}
