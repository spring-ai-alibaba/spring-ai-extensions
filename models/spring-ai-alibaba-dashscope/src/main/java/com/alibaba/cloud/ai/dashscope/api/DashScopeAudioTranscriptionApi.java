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

import java.net.URI;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.jspecify.annotations.Nullable;
import com.alibaba.cloud.ai.dashscope.audio.transcription.DashScopeAsrTranscriptionApiSpec.AsrOutPut;
import com.alibaba.cloud.ai.dashscope.audio.transcription.DashScopeAsrTranscriptionApiSpec.AsrTranscriptionRequest;
import com.alibaba.cloud.ai.dashscope.audio.transcription.DashScopeAsrTranscriptionApiSpec.DashScopeAudioAsrTranscriptionResponse;
import com.alibaba.cloud.ai.dashscope.audio.transcription.DashScopeAudioTranscriptionOptions;
import com.alibaba.cloud.ai.dashscope.audio.transcription.DashScopeAudioTranscriptionPrompt;
import com.alibaba.cloud.ai.dashscope.audio.transcription.DashScopeTranscriptionApiSpec.DashScopeAudioTranscriptionRequest;
import com.alibaba.cloud.ai.dashscope.audio.transcription.DashScopeTranscriptionApiSpec.DashScopeAudioTranscriptionResponse;
import com.alibaba.cloud.ai.dashscope.audio.tts.DashScopeTTSApiSpec.DashScopeAudioTTSRequest;
import com.alibaba.cloud.ai.dashscope.common.DashScopeApiConstants;
import com.alibaba.cloud.ai.dashscope.common.DashScopeAudioApiConstants;
import com.alibaba.cloud.ai.dashscope.api.asr.DashScopeWebSocketAsrApi;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.json.JsonMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.audio.transcription.AudioTranscription;
import org.springframework.ai.audio.transcription.AudioTranscriptionResponse;
import org.springframework.ai.audio.transcription.AudioTranscriptionResponseMetadata;
import org.springframework.ai.model.ApiKey;
import org.springframework.ai.model.NoopApiKey;
import org.springframework.ai.retry.RetryUtils;
import org.springframework.ai.util.JacksonUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.http.MediaType;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.alibaba.cloud.ai.dashscope.common.DashScopeApiConstants.ENABLED;
import static com.alibaba.cloud.ai.dashscope.common.DashScopeApiConstants.HEADER_ASYNC;
import static com.alibaba.cloud.ai.dashscope.common.DashScopeApiConstants.HEADER_SSE;

/**
 * Turn audio into text or text into audio. Based on <a href=
 * "https://help.aliyun.com/zh/model-studio/user-guide/speech-recognition-and-synthesis">DashScope
 * Audio Model</a>
 *
 * @author Kevin Lin
 * @author yuluo-yx
 * @author xuguan
 * @author yingzi
 */
public class DashScopeAudioTranscriptionApi {

    private static final Logger log = LoggerFactory.getLogger(DashScopeAudioTranscriptionApi.class);

    private static final int MAX_POLL_ATTEMPTS = 30;

    private static final long POLL_INTERVAL_MS = 2000;

    private final String baseUrl;

    private final String websocketUrl;

    private final ApiKey apiKey;

    private final @Nullable String workSpaceId;

    private final HttpHeaders headers;

	private final DashScopeWebSocketAsrApi webSocketAsrApi;

    private final RestClient restClient;

    private final RestClient downloadClient = RestClient.create();

    private final WebClient webClient;

    private final JsonMapper jsonMapper;

    public DashScopeAudioTranscriptionApi(
            String baseUrl,
            String  websocketUrl,
            ApiKey apiKey,
            @Nullable String workSpaceId,
            HttpHeaders headers,
            RestClient.Builder restClientBuilder,
            WebClient.Builder webClientBuilder,
            ResponseErrorHandler responseErrorHandler) {
        Assert.notNull(baseUrl, "baseUrl must not be null");
        Assert.notNull(websocketUrl, "websocketUrl must not be null");
        Assert.notNull(apiKey, "apiKey must not be null");
        Assert.notNull(headers, "headers must not be null");
        Assert.notNull(restClientBuilder, "restClientBuilder must not be null");
        Assert.notNull(webClientBuilder, "webClientBuilder must not be null");
        Assert.notNull(responseErrorHandler, "responseErrorHandler must not be null");
        this.baseUrl = baseUrl;
        this.websocketUrl = websocketUrl;
        this.apiKey = apiKey;
        this.workSpaceId = workSpaceId;
        this.headers = headers;

        Consumer<HttpHeaders> authHeaders = h -> {
            h.addAll(headers);
            if (!(apiKey instanceof NoopApiKey)) {
                h.setBearerAuth(apiKey.getValue());
            }
            h.add("Content-Type", "application/json");
        };

        this.restClient = restClientBuilder.clone()
                .baseUrl(baseUrl)
                .defaultHeaders(authHeaders)
                .defaultStatusHandler(responseErrorHandler)
                .build();

        this.webClient = webClientBuilder.clone()
                .baseUrl(baseUrl)
                .defaultHeaders(authHeaders)
                .build();

		this.webSocketAsrApi = DashScopeWebSocketAsrApi.builder()
				.options(com.alibaba.cloud.ai.dashscope.protocol.DashScopeWebSocketClientOptions.builder()
                        .apiKey(apiKey.getValue())
                        .workSpaceId(workSpaceId)
                        .url(websocketUrl)
                        .build())
				.build();

        this.jsonMapper = JsonMapper.builder()
                // Deserialization configuration
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                // Serialization configuration
                .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                .disable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS)
                .changeDefaultPropertyInclusion(incl -> incl.withValueInclusion(JsonInclude.Include.NON_NULL))
                .changeDefaultPropertyInclusion(incl -> incl.withContentInclusion(JsonInclude.Include.NON_NULL))
                // Register standard Jackson modules (Jdk8, JavaTime, ParameterNames, Kotlin)
                .addModules(JacksonUtils.instantiateAvailableModules())
                .build();
    }

    public AudioTranscriptionResponse callLiveTranslate(
            DashScopeAudioTranscriptionPrompt prompt,
            DashScopeAudioTranscriptionOptions options) {

        DashScopeAudioTranscriptionRequest request = DashScopeAudioTranscriptionRequest
                .builder()
                .model(options.getModel())
                .messages(prompt.getMessages())
                .modalities(options.getModalities())
                .stream(false)
                .audio(options.getAudio())
                .maxTokens(options.getMaxTokens())
                .seed(options.getSeed())
                .temperature(options.getTemperature())
                .topP(options.getTopP())
                .presencePenalty(options.getPresencePenalty())
                .topK(options.getTopK())
                .repetitionPenalty(options.getRepetitionPenalty())
                .translationOptions(options.getTranslationOptions())
                .build();

        ResponseEntity<DashScopeAudioTranscriptionResponse> response = restClient.post()
                .uri(DashScopeAudioApiConstants.CHAT_COMPLETIONS)
                .body(request)
                .retrieve()
                .toEntity(DashScopeAudioTranscriptionResponse.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            DashScopeAudioTranscriptionResponse body = response.getBody();
            Assert.notNull(body, "response body must not be null");
            return body;
        }

        log.error("Failed to call Live Translate API: " + response.getStatusCode());
        throw new RuntimeException("Failed to call Live Translate API: " + response.getStatusCode());
    }

    public Flux<AudioTranscriptionResponse> streamLiveTranslate(
            DashScopeAudioTranscriptionPrompt prompt,
            DashScopeAudioTranscriptionOptions options) {
        DashScopeAudioTranscriptionRequest request = DashScopeAudioTranscriptionRequest
                .builder()
                .model(options.getModel())
                .messages(prompt.getMessages())
                .modalities(options.getModalities())
                .stream(true)
                .streamOptions(options.getStreamOptions())
                .audio(options.getAudio())
                .maxTokens(options.getMaxTokens())
                .seed(options.getSeed())
                .temperature(options.getTemperature())
                .topP(options.getTopP())
                .presencePenalty(options.getPresencePenalty())
                .topK(options.getTopK())
                .repetitionPenalty(options.getRepetitionPenalty())
                .translationOptions(options.getTranslationOptions())
                .build();

        // SSE 流结束标志
        Predicate<String> SSE_DONE_PREDICATE = "[DONE]"::equals;

        return this.webClient.post()
                .uri(DashScopeAudioApiConstants.CHAT_COMPLETIONS)
                .body(Mono.just(request), DashScopeAudioTTSRequest.class)
                .retrieve()
                .bodyToFlux(String.class)  // 接收 SSE 流数据
                .takeUntil(SSE_DONE_PREDICATE)  // 遇到 [DONE] 停止
                .filter(SSE_DONE_PREDICATE.negate())  // 过滤掉 [DONE]
                .map(content -> this.jsonMapper.readValue(content, DashScopeAudioTranscriptionResponse.class));
    }

    public Flux<String> createWebSocketTask(ByteBuffer binaryData, DashScopeAudioTranscriptionOptions options) {
        return webSocketAsrApi.stream(binaryData, options);
    }

    /**
     * Create WebSocket ASR task with streaming audio input (bidirectional streaming).
     * Suitable for real-time microphone input.
     */
    public Flux<String> createWebSocketStreamingTask(Flux<ByteBuffer> audioStream,
            DashScopeAudioTranscriptionOptions options) {
        return webSocketAsrApi.stream(audioStream, options);
    }

    public AudioTranscriptionResponse callAsr(
            DashScopeAudioTranscriptionPrompt prompt,
            DashScopeAudioTranscriptionOptions options) {

        Assert.notEmpty(prompt.getFileUrls(), "At least one publicly accessible audio file URL is required");
        AsrTranscriptionRequest asrReq = AsrTranscriptionRequest.builder()
                .model(options.getModel())
                .input(AsrTranscriptionRequest.Input.builder()
                        .fileUrls(prompt.getFileUrls())
                        .build())
                .parameters(AsrTranscriptionRequest.Parameters.builder()
                        .vocabularyId(options.getVocabularyId())
                        .channelId(options.getChannelId())
                        .disfluencyRemovalEnabled(options.getDisfluencyRemovalEnabled())
                        .timestampAlignmentEnabled(options.getTimestampAlignmentEnabled())
                        .specialWordFilter(options.getSpecialWordFilter())
                        .diarizationEnabled(options.getDiarizationEnabled())
                        .languageHints(options.getLanguageHints())
                        .speakerCount(options.getSpeakerCount())
                        .build())
                .resources(options.getResources())
                .build();
        String model = Objects.requireNonNull(options.getModel(), "ASR model is required");
        Object requestBody = asrReq;
        if (DashScopeAudioApiConstants.isQwenFileTranscription(model)) {
            var urls = Objects.requireNonNull(prompt.getFileUrls());
            Assert.isTrue(urls.size() == 1, "Qwen file transcription requires exactly one URL");
            var input = model.startsWith("qwen-audio-") ? Map.of("file_urls", urls)
                    : Map.of("file_url", urls.get(0));
            Map<String, Object> parameters = new LinkedHashMap<>();
            parameters.put("channel_id", options.getChannelId());
            parameters.put("diarization_enabled", options.getDiarizationEnabled());
            parameters.put("speaker_count", options.getSpeakerCount());
            parameters.values().removeIf(Objects::isNull);
            requestBody = Map.of("model", model, "input", input, "parameters", parameters);
        }
        ResponseEntity<AsrOutPut> response = this.restClient.post()
                .uri(DashScopeAudioApiConstants.ASR_TRANSCRIPTION)
                .header(HEADER_ASYNC, ENABLED)
                .body(requestBody)
                .retrieve()
                .toEntity(AsrOutPut.class);
        String taskId;
        if (response.getStatusCode().is2xxSuccessful()) {
            AsrOutPut body = response.getBody();
            Assert.notNull(body, "ASR response body must not be null");
            Assert.notNull(body.output(), "ASR response output must not be null");
            taskId = body.output().taskId();
            log.info("ASR transcription taskId: {}", taskId);
        } else {
            throw new RuntimeException("Failed to call ASR transcription: " + response.getStatusCode());
        }

        Assert.hasText(taskId, "DashScope did not return a transcription task ID");
        for (int attempt = 0; attempt < MAX_POLL_ATTEMPTS; attempt++) {
            JsonNode responseBody = this.restClient.get().uri("/api/v1/tasks/{taskId}", taskId)
                    .retrieve().body(JsonNode.class);
            Assert.notNull(responseBody, "ASR polling response body must not be null");
            JsonNode output = responseBody.path("output");
            String status = output.path("task_status").asString();
            if ("SUCCEEDED".equals(status)) {
                return transcriptionResult(output, taskId);
            }
            if (!"PENDING".equals(status) && !"RUNNING".equals(status)) {
                throw new IllegalStateException("ASR task " + taskId + " failed: " + status);
            }
            try {
                Thread.sleep(POLL_INTERVAL_MS);
            }
            catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("ASR transcription polling interrupted. Task ID " + taskId, exception);
            }
        }
        throw new IllegalStateException("ASR transcription timed out. Task ID " + taskId);
    }

    private AudioTranscriptionResponse transcriptionResult(JsonNode output, String taskId) {
        var results = new ArrayList<DashScopeAudioAsrTranscriptionResponse.TranscriptionResult>();
        var result = output.path("result");
        if (result.hasNonNull("transcription_url")) {
            results.add(downloadTranscription(result.path("transcription_url").asString(), taskId));
        }
        for (JsonNode item : output.path("results")) {
            if (!"SUCCEEDED".equals(item.path("subtask_status").asString())
                    || !item.hasNonNull("transcription_url")) {
                throw new IllegalStateException("ASR subtask failed for task " + taskId);
            }
            results.add(downloadTranscription(item.path("transcription_url").asString(), taskId));
        }
        if (results.isEmpty()) {
            throw new IllegalStateException("No transcription result for task " + taskId);
        }
        return new DashScopeAudioAsrTranscriptionResponse(results);
    }

    private DashScopeAudioAsrTranscriptionResponse.TranscriptionResult downloadTranscription(String url, String taskId) {
        return Objects.requireNonNull(this.downloadClient.get().uri(URI.create(url)).retrieve()
                .body(DashScopeAudioAsrTranscriptionResponse.TranscriptionResult.class),
                "Empty ASR transcription result. Task ID " + taskId);
    }

    public AudioTranscriptionResponse callQwenAudioAsr(DashScopeAudioTranscriptionPrompt prompt,
            DashScopeAudioTranscriptionOptions options) {
        JsonNode body = this.restClient.post().uri(DashScopeAudioApiConstants.MULTIMODAL_GENERATION)
                .body(qwenAudioAsrRequest(prompt, options)).retrieve().body(JsonNode.class);
        return qwenAudioTranscription(Objects.requireNonNull(body));
    }

    public Flux<AudioTranscriptionResponse> streamQwenAudioAsr(DashScopeAudioTranscriptionPrompt prompt,
            DashScopeAudioTranscriptionOptions options) {
        return this.webClient.post().uri(DashScopeAudioApiConstants.MULTIMODAL_GENERATION)
                .accept(MediaType.TEXT_EVENT_STREAM).header(HEADER_SSE, ENABLED)
                .bodyValue(qwenAudioAsrRequest(prompt, options)).retrieve().bodyToFlux(String.class)
                .takeWhile(frame -> !"[DONE]".equals(frame))
                .map(frame -> qwenAudioTranscription(this.jsonMapper.readTree(frame)));
    }

    private static Map<String, Object> qwenAudioAsrRequest(DashScopeAudioTranscriptionPrompt prompt,
            DashScopeAudioTranscriptionOptions options) {
        var messages = prompt.getMessages();
        Assert.notEmpty(messages, "Qwen Audio ASR requires an input_audio message");
        Map<String, Object> parameters = new LinkedHashMap<>();
        parameters.put("format", options.getFormat());
        Integer sampleRate = options.getSampleRate();
        parameters.put("sample_rate", sampleRate == null ? null : sampleRate.toString());
        parameters.put("diarization_enabled", options.getDiarizationEnabled());
        parameters.values().removeIf(Objects::isNull);
        var nativeMessages = messages.stream()
                .map(message -> Map.of("role", message.getRole(), "content", message.getContent().stream()
                        .map(part -> {
                            var audio = part.getInputAudio();
                            if (audio != null) {
                                return Map.of("type", "input_audio", "input_audio", Map.of("data", audio.getData()));
                            }
                            Assert.hasText(part.getText(), "Qwen Audio ASR history must contain text");
                            return Map.of("type", part.getType(), "text", Objects.requireNonNull(part.getText()));
                        }).toList())).toList();
        return Map.of("model", Objects.requireNonNull(options.getModel()), "input", Map.of("messages", nativeMessages),
                "parameters", parameters);
    }

    private static AudioTranscriptionResponse qwenAudioTranscription(JsonNode payload) {
        JsonNode output = payload.path("output");
        if (!output.isObject() || payload.hasNonNull("code")) {
            throw new IllegalStateException("DashScope Audio ASR failed: " + payload.path("code").asString()
                    + " (request_id=" + payload.path("request_id").asString() + ")");
        }
        var metadata = new AudioTranscriptionResponseMetadata();
        metadata.put("request_id", payload.path("request_id").asString());
        if (output.hasNonNull("sentence")) {
            metadata.put("sentence", output.path("sentence"));
        }
        if (output.hasNonNull("sentences")) {
            metadata.put("sentences", output.path("sentences"));
        }
        return new AudioTranscriptionResponse(new AudioTranscription(output.path("text").asString("")), metadata);
    }

    public AudioTranscriptionResponse callQwenAsr(DashScopeAudioTranscriptionPrompt prompt, DashScopeAudioTranscriptionOptions options) {
        DashScopeAudioTranscriptionRequest request = DashScopeAudioTranscriptionRequest.builder()
                .model(options.getModel())
                .messages(prompt.getMessages())
                .asrOptions(options.getAsrOptions())
                .stream(false)
                .build();

        ResponseEntity<DashScopeAudioTranscriptionResponse> response = restClient.post()
                .uri(DashScopeAudioApiConstants.QWEN_ASR)
                .body(request)
                .retrieve()
                .toEntity(DashScopeAudioTranscriptionResponse.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            DashScopeAudioTranscriptionResponse body = response.getBody();
            Assert.notNull(body, "response body must not be null");
            return body;
        }

        log.error("Failed to call Qwen ASR API: " + response.getStatusCode());
        throw new RuntimeException("Failed to call Qwen ASR API: " + response.getStatusCode());
    }

    public Flux<AudioTranscriptionResponse> streamQwenAsr(
            DashScopeAudioTranscriptionPrompt prompt,
            DashScopeAudioTranscriptionOptions options) {
        DashScopeAudioTranscriptionRequest request = DashScopeAudioTranscriptionRequest.builder()
                .model(options.getModel())
                .messages(prompt.getMessages())
                .asrOptions(options.getAsrOptions())
                .stream(true)
                .streamOptions(options.getStreamOptions())
                .build();

        // SSE 流结束标志
        Predicate<String> SSE_DONE_PREDICATE = "[DONE]"::equals;

        return this.webClient.post()
                .uri(DashScopeAudioApiConstants.QWEN_ASR)
                .body(Mono.just(request), DashScopeAudioTranscriptionResponse.class)
                .retrieve()
                .bodyToFlux(String.class)  // 接收 SSE 流数据
                .takeUntil(SSE_DONE_PREDICATE)  // 遇到 [DONE] 停止
                .filter(SSE_DONE_PREDICATE.negate())  // 过滤掉 [DONE]
                .map(content -> this.jsonMapper.readValue(content, DashScopeAudioTranscriptionResponse.class));
    }

    /**
     * Returns a builder pre-populated with the current configuration for mutation.
     */
    public Builder mutate() {
        return new Builder(this);
    }

    public String getBaseUrl() {
        return this.baseUrl;
    }

    public String getWebsocketUrl() {
        return this.websocketUrl;
    }

    public ApiKey getApiKey() {
        return this.apiKey;
    }

    public @Nullable String getWorkSpaceId() {
        return this.workSpaceId;
    }

    public HttpHeaders getHeaders() {
        return this.headers;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String baseUrl = DashScopeApiConstants.DEFAULT_BASE_URL;

        private String websocketUrl = DashScopeAudioApiConstants.DEFAULT_WEBSOCKET_URL;

        private @Nullable ApiKey apiKey;

        private @Nullable String workSpaceId;

        private HttpHeaders headers = new HttpHeaders();

        private RestClient.Builder restClientBuilder = RestClient.builder();

        private WebClient.Builder webClientBuilder = WebClient.builder();

        private ResponseErrorHandler responseErrorHandler = RetryUtils.DEFAULT_RESPONSE_ERROR_HANDLER;

        public Builder() {
        }

        public Builder(DashScopeAudioTranscriptionApi api) {
            this.baseUrl = api.getBaseUrl();
            this.websocketUrl = api.getWebsocketUrl();
            this.apiKey = api.getApiKey();
            this.workSpaceId = api.getWorkSpaceId();
            this.headers = api.getHeaders();
        }

        public Builder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public Builder websocketUrl(String websocketUrl) {
            this.websocketUrl = websocketUrl;
            return this;
        }

        public Builder workSpaceId(@Nullable String workSpaceId) {
            this.workSpaceId = workSpaceId;
            return this;
        }

        public Builder apiKey(@Nullable ApiKey apiKey) {
            this.apiKey = apiKey;
            return this;
        }

        public Builder headers(HttpHeaders headers) {
            this.headers = headers;
            return this;
        }

        public Builder restClientBuilder(RestClient.Builder restClientBuilder) {
            this.restClientBuilder = restClientBuilder;
            return this;
        }

        public Builder webClientBuilder(WebClient.Builder webClientBuilder) {
            this.webClientBuilder = webClientBuilder;
            return this;
        }

        public Builder responseErrorHandler(ResponseErrorHandler responseErrorHandler) {
            this.responseErrorHandler = responseErrorHandler;
            return this;
        }

        public DashScopeAudioTranscriptionApi build() {
            Assert.hasText(this.baseUrl, "baseUrl cannot be null or empty");
            Assert.hasText(this.websocketUrl, "websocketUrl cannot be null or empty");
            Assert.notNull(this.apiKey, "apiKey must be set");
            Assert.notNull(this.headers, "headers cannot be null");
            Assert.notNull(this.restClientBuilder, "restClientBuilder cannot be null");
            Assert.notNull(this.webClientBuilder, "webClientBuilder cannot be null");
            Assert.notNull(this.responseErrorHandler, "responseErrorHandler cannot be null");

            return new DashScopeAudioTranscriptionApi(
                    this.baseUrl,
                    this.websocketUrl,
                    this.apiKey,
                    this.workSpaceId,
                    this.headers,
                    this.restClientBuilder,
                    this.webClientBuilder,
                    this.responseErrorHandler);
        }
    }
}
