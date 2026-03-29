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
import org.jspecify.annotations.Nullable;
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
        private @Nullable String model;

        @JsonProperty("messages")
        private @Nullable List<TranscriptionUserMessage> messages;

        @JsonProperty("modalities")
        private @Nullable List<String> modalities;

        @JsonProperty("audio")
        private @Nullable Audio audio;

        @JsonProperty("stream")
        private @Nullable Boolean stream;

        @JsonProperty("stream_options")
        private @Nullable StreamOptions streamOptions;

        @JsonProperty("max_tokens")
        private @Nullable Integer maxTokens;

        @JsonProperty("seed")
        private @Nullable Integer seed;

        @JsonProperty("temperature")
        private @Nullable Float temperature;

        @JsonProperty("top_p")
        private @Nullable Float topP;

        @JsonProperty("presence_penalty")
        private @Nullable Float presencePenalty;

        @JsonProperty("top_k")
        private @Nullable Integer topK;

        @JsonProperty("repetition_penalty")
        private @Nullable Float repetitionPenalty;

        @JsonProperty("translation_options")
        private @Nullable TranslationOptions translationOptions;

        @JsonProperty("asr_options")
        private @Nullable AsrOptions asrOptions;

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private DashScopeAudioTranscriptionRequest request;

            public Builder() {
                this.request = new DashScopeAudioTranscriptionRequest();
            }

            public Builder model(@Nullable String model) {
                this.request.model = model;
                return this;
            }

            public Builder messages(@Nullable List<TranscriptionUserMessage> messages) {
                this.request.messages = messages;
                return this;
            }

            public Builder modalities(@Nullable List<String> modalities) {
                this.request.modalities = modalities;
                return this;
            }

            public Builder audio(@Nullable Audio audio) {
                this.request.audio = audio;
                return this;
            }

            public Builder stream(Boolean stream) {
                this.request.stream = stream;
                return this;
            }

            public Builder streamOptions(@Nullable StreamOptions streamOptions) {
                this.request.streamOptions = streamOptions;
                return this;
            }

            public Builder maxTokens(@Nullable Integer maxTokens) {
                this.request.maxTokens = maxTokens;
                return this;
            }

            public Builder seed(@Nullable Integer seed) {
                this.request.seed = seed;
                return this;
            }

            public Builder temperature(@Nullable Float temperature) {
                this.request.temperature = temperature;
                return this;
            }

            public Builder topP(@Nullable Float topP) {
                this.request.topP = topP;
                return this;
            }

            public Builder presencePenalty(@Nullable Float presencePenalty) {
                this.request.presencePenalty = presencePenalty;
                return this;
            }

            public Builder topK(@Nullable Integer topK) {
                this.request.topK = topK;
                return this;
            }

            public Builder repetitionPenalty(@Nullable Float repetitionPenalty) {
                this.request.repetitionPenalty = repetitionPenalty;
                return this;
            }

            public Builder translationOptions(@Nullable TranslationOptions translationOptions) {
                this.request.translationOptions = translationOptions;
                return this;
            }

            public Builder asrOptions(@Nullable AsrOptions asrOptions) {
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
        private @Nullable String id;

        @JsonProperty("created")
        private @Nullable Integer created;

        @JsonProperty("model")
        private @Nullable String model;

        @JsonProperty("object")
        private @Nullable String object;

        @JsonProperty("usage")
        private @Nullable Usage usage;

        @JsonProperty("choices")
        private @Nullable List<Choice> choices;

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

        public @Nullable String getId() {
            return id;
        }

        public @Nullable Integer getCreated() {
            return created;
        }

        public @Nullable String getModel() {
            return model;
        }

        public @Nullable String getObject() {
            return object;
        }

        public @Nullable Usage getUsage() {
            return usage;
        }

        public @Nullable List<Choice> getChoices() {
            return choices;
        }

        @JsonInclude(JsonInclude.Include.NON_NULL)
        public record Choice(
                @JsonProperty("delta") @Nullable Delta delta,
                @JsonProperty("message") @Nullable Message message,
                @JsonProperty("finish_reason") @Nullable String finishReason,
                @JsonProperty("index") @Nullable Integer index
        ) {}

        @JsonInclude(JsonInclude.Include.NON_NULL)
        public record Delta(
                @JsonProperty("content") @Nullable String content,
                @JsonProperty("role") @Nullable String role,
                @JsonProperty("audio") @Nullable Audio audio
        ) {};

        @JsonInclude(JsonInclude.Include.NON_NULL)
        public record Message(
                @JsonProperty("content") @Nullable String content,
                @JsonProperty("role") @Nullable String role,
                @JsonProperty("annotations") @Nullable List<Annotation> annotations
        ) {};

        @JsonInclude(JsonInclude.Include.NON_NULL)
        public record Audio(
                @JsonProperty("data") @Nullable String data,
                @JsonProperty("expires_at") @Nullable Integer expiresAt,
                @JsonProperty("id") @Nullable String id
        ) {};

        @JsonInclude(JsonInclude.Include.NON_NULL)
        public record Usage(
                @JsonProperty("prompt_tokens") @Nullable Integer promptTokens,
                @JsonProperty("completion_tokens") @Nullable Integer completionTokens,
                @JsonProperty("total_tokens") @Nullable Integer totalTokens,
                @JsonProperty("completion_tokens_details") @Nullable CompletionTokensDetails completionTokensDetails,
                @JsonProperty("prompt_tokens_details") @Nullable PromptTokensDetails promptTokensDetails,
                @JsonProperty("seconds") @Nullable Integer seconds
                ) {}

        @JsonInclude(JsonInclude.Include.NON_NULL)
        public record CompletionTokensDetails(
                @JsonProperty("audio_tokens") @Nullable Integer audioTokens,
                @JsonProperty("text_tokens") @Nullable Integer textTokens
        ) {}

        @JsonInclude(JsonInclude.Include.NON_NULL)
        public record PromptTokensDetails(
                @JsonProperty("audio_tokens") @Nullable Integer audioTokens,
                @JsonProperty("video_tokens") @Nullable Integer videoTokens
        ) {}

        @JsonInclude(JsonInclude.Include.NON_NULL)
        public record Annotation(
                @JsonProperty("emotion") @Nullable String emotion,
                @JsonProperty("language") @Nullable String language,
                @JsonProperty("type") @Nullable String type
        ) {}
    }


}
