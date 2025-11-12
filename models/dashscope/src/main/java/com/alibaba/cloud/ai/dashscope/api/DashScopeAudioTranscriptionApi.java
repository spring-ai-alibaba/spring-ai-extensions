/*
 * Copyright 2024-2025 the original author or authors.
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

import com.alibaba.cloud.ai.dashscope.audio.DashScopeAudioTranscriptionOptions;
import com.alibaba.cloud.ai.dashscope.common.DashScopeApiConstants;
import com.alibaba.cloud.ai.dashscope.common.DashScopeException;
import com.alibaba.cloud.ai.dashscope.protocol.DashScopeWebSocketClient;
import com.alibaba.cloud.ai.dashscope.protocol.DashScopeWebSocketClientOptions;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.model.ApiKey;
import org.springframework.ai.model.NoopApiKey;
import org.springframework.ai.model.SimpleApiKey;
import org.springframework.ai.retry.RetryUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClient;
import reactor.core.publisher.Flux;

import java.io.InputStream;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.function.Consumer;

import static com.alibaba.cloud.ai.dashscope.common.DashScopeApiConstants.AUDIO_TRANSCRIPTION_RESTFUL_URL;

/**
 * Turn audio into text or text into audio. Based on <a href=
 * "https://help.aliyun.com/zh/model-studio/user-guide/speech-recognition-and-synthesis">DashScope
 * Audio Model</a>
 *
 * @author Kevin Lin
 * @author yuluo-yx
 * @author xuguan
 */

public class DashScopeAudioTranscriptionApi {

	private final String baseUrl;

	private final String model;

	private final ApiKey apiKey;

	private final String workSpaceId;

	private final String webSocketUrl;

	private final MultiValueMap<String, String> headers;

	private final DashScopeWebSocketClient webSocketClient;

	private final RestClient restClient;

	private final ResponseErrorHandler responseErrorHandler;

	// @formatter:off
	public DashScopeAudioTranscriptionApi(
			String baseUrl,
			ApiKey apiKey,
			String model,
			String workSpaceId,
			MultiValueMap<String, String> headers,
			String webSocketUrl,
			RestClient.Builder restClientBuilder,
			ResponseErrorHandler responseErrorHandler
	) {

		this.baseUrl = baseUrl;
		this.model = model;
		this.apiKey = apiKey;
		this.workSpaceId = workSpaceId;
		this.webSocketUrl = webSocketUrl;
		this.headers = headers;
		this.responseErrorHandler = responseErrorHandler;

		Consumer<HttpHeaders> authHeaders = h -> {
			h.addAll(headers);
			if (!(apiKey instanceof NoopApiKey)) {
				h.setBearerAuth(apiKey.getValue());
				h.set(DashScopeApiConstants.HEADER_ASYNC, "enable");
			}
		};

		this.restClient = restClientBuilder.clone()
				.baseUrl(baseUrl)
				.defaultHeaders(authHeaders)
				.defaultStatusHandler(responseErrorHandler)
				.build();

		this.webSocketClient = new DashScopeWebSocketClient(DashScopeWebSocketClientOptions.builder()
			.withApiKey(apiKey.getValue())
			.withWorkSpaceId(workSpaceId)
			.withUrl(webSocketUrl)
			.build());
	}
	// @formatter:on

	/**
	 * Returns a builder pre-populated with the current configuration for mutation.
	 */
	public Builder mutate() {
		return new Builder(this);
	}

	public static Builder builder() {

		return new Builder();
	}

	public ResponseEntity<Response> submitTask(DashScopeAudioTranscriptionApi.Request request) {

		return restClient.post().uri(AUDIO_TRANSCRIPTION_RESTFUL_URL).body(request).retrieve().toEntity(Response.class);
	}

	public ResponseEntity<Response> queryTaskResult(String taskId) {

		String uri = "/api/v1/tasks/" + taskId;
		return restClient.post().uri(uri).retrieve().toEntity(Response.class);
	}

	public void realtimeControl(DashScopeAudioTranscriptionApi.RealtimeRequest request) {
		try {
			String message = (new ObjectMapper()).writeValueAsString(request);
			this.webSocketClient.sendText(message);
		}
		catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	public Flux<RealtimeResponse> realtimeStream(Flux<ByteBuffer> audio) {
		return this.webSocketClient.streamTextOut(audio).handle((msg, sink) -> {
			try {
				sink.next((new ObjectMapper()).readValue(msg, RealtimeResponse.class));
			}
			catch (JsonProcessingException e) {
				sink.error(new DashScopeException(String.valueOf(e)));
			}
		});
	}

	public Outcome getOutcome(String transcriptionUrl) {
		ObjectMapper objectMapper = new ObjectMapper();

		try {
			InputStream inputStream = URI.create(transcriptionUrl).toURL().openStream();
			Outcome outcome = objectMapper.readValue(inputStream, Outcome.class);
			inputStream.close();
			return outcome;
		}
		catch (Exception e) {
			throw new DashScopeException("get transcription outcome failed", e);
		}
	}


	// @formatter:off
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Request(
        @JsonProperty("model") String model,
        @JsonProperty("input") Input input,
        @JsonProperty("resources") List<Resource> resources,
        @JsonProperty("parameters") Parameters parameters
    ) {
        public record Input(
            @JsonProperty("file_urls") List<String> fileUrls
        ) {}

        public record Resource(
			@JsonProperty("resource_id") String resourceId,
			@JsonProperty("resource_type") String resourceType
        ) {}

        public record Parameters(
            @JsonProperty("vocabulary_id") String vocabularyId,
            @JsonProperty("channel_id") List<Integer> channelId,
            @JsonProperty("disfluency_removal_enabled") Boolean disfluencyRemovalEnabled,
			@JsonProperty("timestamp_alignment_enabled") Boolean timestampAlignmentEnabled,
			@JsonProperty("special_word_filter") String specialWordFilter,
            @JsonProperty("language_hints") List<String> languageHints,
			@JsonProperty("diarization_enabled") Boolean diarizationEnabled,
			@JsonProperty("speaker_count") Integer speakerCount
        ) {}
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Response(
        @JsonProperty("request_id") String requestId,
        @JsonProperty("usage") Usage usage,
        @JsonProperty("output") Output output
    ) {
        public record Usage(
            @JsonProperty("duration") Integer duration
        ) {}

        public record Output(
            @JsonProperty("task_id") String taskId,
            @JsonProperty("task_status") TaskStatus taskStatus,
            @JsonProperty("submit_time") String submitTime,
            @JsonProperty("scheduled_time") String scheduledTime,
            @JsonProperty("end_time") String endTime,
            @JsonProperty("results") List<Result> results,
            @JsonProperty("task_metrics") TaskMetrics taskMetrics
        ) {
            public record Result(
                @JsonProperty("file_url") String fileUrl,
                @JsonProperty("transcription_url") String transcriptionUrl,
                @JsonProperty("subtask_status") String subtaskStatus
            ) {}

            public record TaskMetrics(
                @JsonProperty("TOTAL") Integer total,
                @JsonProperty("SUCCEEDED") Integer succeeded,
                @JsonProperty("FAILED") Integer failed
            ) {}
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Outcome(
        @JsonProperty("file_url") String fileUrl,
        @JsonProperty("properties") Properties properties,
        @JsonProperty("transcripts") List<Transcript> transcripts
    ) {
        public record Properties(
            @JsonProperty("audio_format") String audioFormat,
            @JsonProperty("channels") List<Integer> channels,
            @JsonProperty("original_sampling_rate") Integer originalSamplingRate,
            @JsonProperty("original_duration_in_milliseconds") Integer originalDurationInMilliseconds
        ) {}

        public record Transcript(
            @JsonProperty("channel_id") Integer channelId,
            @JsonProperty("content_duration_in_milliseconds") Integer contentDurationInMilliseconds,
            @JsonProperty("text") String text,
            @JsonProperty("sentences") List<Sentence> sentences
        ) {
            public record Sentence(
                @JsonProperty("begin_time") Integer beginTime,
                @JsonProperty("end_time") Integer endTime,
                @JsonProperty("text") String text,
                @JsonProperty("sentence_id") String sentenceId,
                @JsonProperty("speaker_id") String speakerId,
                @JsonProperty("words") List<Word> words
            ) {
                public record Word(
                    @JsonProperty("begin_time") Integer beginTime,
                    @JsonProperty("end_time") Integer endTime,
                    @JsonProperty("text") String text,
                    @JsonProperty("punctuation") String punctuation
                ) {}
            }
        }
    }

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public record RealtimeRequest(
		@JsonProperty("header") Header header,
		@JsonProperty("payload") Payload payload) {
		public record Header(
			@JsonProperty("action") DashScopeWebSocketClient.EventType action,
			@JsonProperty("task_id") String taskId,
			@JsonProperty("streaming") String streaming
		) {}
		public record Payload(
			@JsonProperty("model") String model,
			@JsonProperty("task_group") String taskGroup,
			@JsonProperty("task") String task,
			@JsonProperty("function") String function,
			@JsonProperty("input") Input input,
			@JsonProperty("parameters") Parameters parameters) {
			public record Input(
			) {}
			public record Parameters(
				@JsonProperty("sample_rate") Integer sampleRate,
				@JsonProperty("format") DashScopeAudioTranscriptionOptions.AudioFormat format,
				@JsonProperty("disfluency_removal_enabled") Boolean disfluencyRemovalEnabled
			) {}
		}
	}

	public record RealtimeResponse(
		@JsonProperty("header") Header header,
		@JsonProperty("payload") Payload payload
	) {
		public record Header(
			@JsonProperty("task_id") String taskId,
			@JsonProperty("event") DashScopeWebSocketClient.EventType event,
			@JsonProperty("attributes") Attributes attributes
		) {
			public record Attributes(
			) {}
		}

		public record Payload(
			@JsonProperty("output") Output output,
			@JsonProperty("usage") Usage usage
		) {
			public record Output(
				@JsonProperty("sentence") Sentence sentence
			) {
				public record Sentence(
					@JsonProperty("sentence_id") String sentenceId,
					@JsonProperty("begin_time") Integer beginTime,
					@JsonProperty("end_time") Integer endTime,
					@JsonProperty("text") String text,
					@JsonProperty("channel_id") Integer channelId,
					@JsonProperty("speaker_id") String speakerId,
					@JsonProperty("sentence_end") Boolean sentenceEnd,
					@JsonProperty("words") List<Word> words,
					@JsonProperty("stash") Stash stash
				) {
					public record Word(
						@JsonProperty("begin_time") Integer beginTime,
						@JsonProperty("end_time") Integer endTime,
						@JsonProperty("text") String text,
						@JsonProperty("punctuation") String punctuation,
						@JsonProperty("fixed") Boolean fixed,
						@JsonProperty("speaker_id") String speakerId
					){}
					public record Stash(
						@JsonProperty("sentence_id") String sentenceId,
						@JsonProperty("text") String text,
						@JsonProperty("begin_time") Integer beginTime,
						@JsonProperty("current_time") Integer currentTime,
						@JsonProperty("words") List<Word> words
					) {}
				}
			}

			public record Usage(
				@JsonProperty("duration") Integer duration
			) {}
		}
	}
    // @formatter:on
	public enum TaskStatus {

		PENDING("PENDING"),

		SUSPENDED("SUSPENDED"),

		SUCCEEDED("SUCCEEDED"),

		CANCELED("CANCELED"),

		RUNNING("RUNNING"),

		FAILED("FAILED"),

		UNKNOWN("UNKNOWN"),;

		private final String status;

		TaskStatus(String status) {
			this.status = status;
		}

		public String getValue() {
			return status;
		}

	}

	public String getBaseUrl() {
		return this.baseUrl;
	}

	public String getModel() {
		return this.model;
	}

	public ApiKey getApiKey() {
		return this.apiKey;
	}

	public String getWorkSpaceId() {
		return this.workSpaceId;
	}

	public String getWebSocketUrl() {
		return this.webSocketUrl;
	}

	public MultiValueMap<String, String> getHeaders() {
		return headers;
	}

	ResponseErrorHandler getResponseErrorHandler() {
		return this.responseErrorHandler;
	}

	public static class Builder {

		private String baseUrl = DashScopeApiConstants.DEFAULT_BASE_URL;

		private String workSpaceId;

		private String model;

		private ApiKey apiKey;

		private MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();

		private String webSocketUrl = DashScopeApiConstants.DEFAULT_WEBSOCKET_URL;

		private RestClient.Builder restClientBuilder = RestClient.builder();

		private ResponseErrorHandler responseErrorHandler = RetryUtils.DEFAULT_RESPONSE_ERROR_HANDLER;

		public Builder() {
		}

		public Builder(DashScopeAudioTranscriptionApi api) {
			this.baseUrl = api.getBaseUrl();
			this.apiKey = api.getApiKey();
			this.model = api.getModel();
			this.headers = new LinkedMultiValueMap<>(api.getHeaders());
			this.webSocketUrl = api.webSocketUrl;
			this.restClientBuilder = api.restClient != null ? api.restClient.mutate() : RestClient.builder();
			this.responseErrorHandler = api.getResponseErrorHandler();
		}

		public Builder baseUrl(String baseUrl) {
			this.baseUrl = baseUrl;
			return this;
		}

		public Builder workSpaceId(String workSpaceId) {
			this.workSpaceId = workSpaceId;
			return this;
		}

		public Builder apiKey(ApiKey apiKey) {
			this.apiKey = apiKey;
			return this;
		}

		public Builder apiKey(String simpleApiKey) {
			this.apiKey = new SimpleApiKey(simpleApiKey);
			return this;
		}

		public Builder model(String model) {
			this.model = model;
			return this;
		}

		public Builder webSocketUrl(String webSocketUrl) {
			this.webSocketUrl = webSocketUrl;
			return this;
		}

		public Builder headers(MultiValueMap<String, String> headers) {
			this.headers = headers;
			return this;
		}

		public Builder restClientBuilder(RestClient.Builder restClientBuilder) {
			this.restClientBuilder = restClientBuilder;
			return this;
		}

		public Builder responseErrorHandler(ResponseErrorHandler responseErrorHandler) {
			this.responseErrorHandler = responseErrorHandler;
			return this;
		}

		public DashScopeAudioTranscriptionApi build() {

			Assert.hasText(this.baseUrl, "baseUrl cannot be null or empty");
			Assert.notNull(this.apiKey, "apiKey must be set");
			Assert.notNull(this.model, "model must be set");
			Assert.notNull(this.headers, "headers cannot be null");
			Assert.notNull(this.restClientBuilder, "restClientBuilder cannot be null");
			Assert.notNull(this.responseErrorHandler, "responseErrorHandler cannot be null");

			return new DashScopeAudioTranscriptionApi(this.baseUrl, this.apiKey, this.model, this.workSpaceId,
					this.headers, this.webSocketUrl, this.restClientBuilder, this.responseErrorHandler);
		}

	}

}
