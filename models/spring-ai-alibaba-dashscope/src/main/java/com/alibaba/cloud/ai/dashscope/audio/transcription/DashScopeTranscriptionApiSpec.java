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

package com.alibaba.cloud.ai.dashscope.audio.transcription;

import com.alibaba.cloud.ai.dashscope.audio.transcription.DashScopeAudioTranscriptionOptions.AsrOptions;
import com.alibaba.cloud.ai.dashscope.audio.transcription.DashScopeAudioTranscriptionOptions.Audio;
import com.alibaba.cloud.ai.dashscope.audio.transcription.DashScopeAudioTranscriptionOptions.StreamOptions;
import com.alibaba.cloud.ai.dashscope.audio.transcription.DashScopeAudioTranscriptionOptions.TranslationOptions;
import com.alibaba.cloud.ai.dashscope.audio.transcription.DashScopeAudioTranscriptionPrompt.TranscriptionUserMessage;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.ai.audio.transcription.AudioTranscription;
import org.springframework.ai.audio.transcription.AudioTranscriptionResponse;
import org.springframework.ai.audio.transcription.AudioTranscriptionResponseMetadata;

import java.util.List;

/**
 * @author yingzi
 * @since 2026/2/1
 */

public class DashScopeTranscriptionApiSpec {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class DashScopeAudioTranscriptionRequest {
        @JsonProperty("model")
        private String model;

        @JsonProperty("messages")
        private List<TranscriptionUserMessage> messages;

        @JsonProperty("modalities")
        private List<String> modalities;

        @JsonProperty("audio")
        private Audio audio;

        @JsonProperty("stream")
        private Boolean stream;

        @JsonProperty("stream_options")
        private StreamOptions streamOptions;

        @JsonProperty("max_tokens")
        private Integer maxTokens;

        @JsonProperty("seed")
        private Integer seed;

        @JsonProperty("temperature")
        private Float temperature;

        @JsonProperty("top_p")
        private Float topP;

        @JsonProperty("presence_penalty")
        private Float presencePenalty;

        @JsonProperty("top_k")
        private Integer topK;

        @JsonProperty("repetition_penalty")
        private Float repetitionPenalty;

        @JsonProperty("translation_options")
        private TranslationOptions translationOptions;

        @JsonProperty("asr_options")
        private AsrOptions asrOptions;

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private DashScopeAudioTranscriptionRequest request;

            public Builder() {
                this.request = new DashScopeAudioTranscriptionRequest();
            }

            public Builder model(String model) {
                this.request.model = model;
                return this;
            }

            public Builder messages(List<TranscriptionUserMessage> messages) {
                this.request.messages = messages;
                return this;
            }

            public Builder modalities(List<String> modalities) {
                this.request.modalities = modalities;
                return this;
            }

            public Builder audio(Audio audio) {
                this.request.audio = audio;
                return this;
            }

            public Builder stream(Boolean stream) {
                this.request.stream = stream;
                return this;
            }

            public Builder streamOptions(StreamOptions streamOptions) {
                this.request.streamOptions = streamOptions;
                return this;
            }

            public Builder maxTokens(Integer maxTokens) {
                this.request.maxTokens = maxTokens;
                return this;
            }

            public Builder seed(Integer seed) {
                this.request.seed = seed;
                return this;
            }

            public Builder temperature(Float temperature) {
                this.request.temperature = temperature;
                return this;
            }

            public Builder topP(Float topP) {
                this.request.topP = topP;
                return this;
            }

            public Builder presencePenalty(Float presencePenalty) {
                this.request.presencePenalty = presencePenalty;
                return this;
            }

            public Builder topK(Integer topK) {
                this.request.topK = topK;
                return this;
            }

            public Builder repetitionPenalty(Float repetitionPenalty) {
                this.request.repetitionPenalty = repetitionPenalty;
                return this;
            }

            public Builder translationOptions(TranslationOptions translationOptions) {
                this.request.translationOptions = translationOptions;
                return this;
            }

            public Builder asrOptions(AsrOptions asrOptions) {
                this.request.asrOptions = asrOptions;
                return this;
            }

            public DashScopeAudioTranscriptionRequest build() {
                return this.request;
            }
        }

    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class DashScopeAudioTranscriptionResponse extends AudioTranscriptionResponse {

        @JsonProperty("id")
        private String id;

        @JsonProperty("created")
        private Integer created;

        @JsonProperty("model")
        private String model;

        @JsonProperty("object")
        private String object;

        @JsonProperty("usage")
        private Usage usage;

        @JsonProperty("choices")
        private List<Choice> choices;

        // Default constructor for Jackson deserialization
        public DashScopeAudioTranscriptionResponse() {
            super(null);
        }

        public DashScopeAudioTranscriptionResponse(AudioTranscription transcript) {
            super(transcript);
        }

        public DashScopeAudioTranscriptionResponse(
                AudioTranscription transcript,
                AudioTranscriptionResponseMetadata transcriptionResponseMetadata) {
            super(transcript, transcriptionResponseMetadata);
        }

        public String getId() {
            return id;
        }

        public Integer getCreated() {
            return created;
        }

        public String getModel() {
            return model;
        }

        public String getObject() {
            return object;
        }

        public Usage getUsage() {
            return usage;
        }

        public List<Choice> getChoices() {
            return choices;
        }

        @JsonInclude(JsonInclude.Include.NON_NULL)
        public record Choice(
                @JsonProperty("delta") Delta delta,
                @JsonProperty("message") Message message,
                @JsonProperty("finish_reason") String finishReason,
                @JsonProperty("index") Integer index
        ) {}

        @JsonInclude(JsonInclude.Include.NON_NULL)
        public record Delta(
                @JsonProperty("content") String content,
                @JsonProperty("role") String role,
                @JsonProperty("audio") Audio audio
        ) {};

        @JsonInclude(JsonInclude.Include.NON_NULL)
        public record Message(
                @JsonProperty("content") String content,
                @JsonProperty("role") String role,
                @JsonProperty("annotations") List<Annotation> annotations
        ) {};

        @JsonInclude(JsonInclude.Include.NON_NULL)
        public record Audio(
                @JsonProperty("data") String data,
                @JsonProperty("expires_at") Integer expiresAt,
                @JsonProperty("id") String id
        ) {};

        @JsonInclude(JsonInclude.Include.NON_NULL)
        public record Usage(
                @JsonProperty("prompt_tokens") Integer promptTokens,
                @JsonProperty("completion_tokens") Integer completionTokens,
                @JsonProperty("total_tokens") Integer totalTokens,
                @JsonProperty("completion_tokens_details") CompletionTokensDetails completionTokensDetails,
                @JsonProperty("prompt_tokens_details") PromptTokensDetails promptTokensDetails,
                @JsonProperty("seconds") Integer seconds
                ) {}

        @JsonInclude(JsonInclude.Include.NON_NULL)
        public record CompletionTokensDetails(
                @JsonProperty("audio_tokens") Integer audioTokens,
                @JsonProperty("text_tokens") Integer textTokens
        ) {}

        @JsonInclude(JsonInclude.Include.NON_NULL)
        public record PromptTokensDetails(
                @JsonProperty("audio_tokens") Integer audioTokens,
                @JsonProperty("video_tokens") Integer videoTokens
        ) {}

        @JsonInclude(JsonInclude.Include.NON_NULL)
        public record Annotation(
                @JsonProperty("emotion") String emotion,
                @JsonProperty("language") String language,
                @JsonProperty("type") String type
        ) {}
    }


}
