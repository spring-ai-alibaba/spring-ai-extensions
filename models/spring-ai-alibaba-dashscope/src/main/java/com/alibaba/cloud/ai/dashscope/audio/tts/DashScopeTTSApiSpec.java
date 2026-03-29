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
package com.alibaba.cloud.ai.dashscope.audio.tts;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jspecify.annotations.Nullable;
import org.springframework.ai.audio.tts.Speech;
import org.springframework.ai.audio.tts.TextToSpeechResponse;

import java.util.Base64;
import java.util.List;

/**
 * @author yingzi
 * @since 2026/1/29
 */

public class DashScopeTTSApiSpec {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class DashScopeAudioTTSRequest {
        @JsonProperty("model")
        private @Nullable String model;

        @JsonProperty("input")
        private @Nullable TTSInput input;

        @JsonProperty("stream")
        private @Nullable Boolean stream;

        @JsonProperty("instructions")
        private @Nullable String instructions;

        @JsonProperty("optimize_instructions")
        private @Nullable Boolean optimizeInstructions;

        public DashScopeAudioTTSRequest(@Nullable String model, @Nullable String text, @Nullable String voice,
                @Nullable String languageType, @Nullable Boolean stream, @Nullable String instructions,
                @Nullable Boolean optimizeInstructions) {
            this.model = model;
            this.input = new TTSInput();
            this.input.text = text;
            this.input.voice = voice;
            this.input.languageType = languageType;
            this.stream = stream;
            this.instructions = instructions;
            this.optimizeInstructions = optimizeInstructions;
        }

        @JsonInclude(JsonInclude.Include.NON_NULL)
        private class TTSInput {
            @JsonProperty("text")
            private @Nullable String text;

            @JsonProperty("voice")
            private @Nullable String voice;

            @JsonProperty("language_type")
            private @Nullable String languageType;
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private @Nullable String model;
            private @Nullable String text;
            private @Nullable String voice;
            private @Nullable String languageType;
            private @Nullable Boolean stream;
            private @Nullable String instructions;
            private @Nullable Boolean optimizeInstructions;

            public Builder model(@Nullable String model) {
                this.model = model;
                return this;
            }

            public Builder text(@Nullable String text) {
                this.text = text;
                return this;
            }

            public Builder voice(@Nullable String voice) {
                this.voice = voice;
                return this;
            }

            public Builder languageType(@Nullable String languageType) {
                this.languageType = languageType;
                return this;
            }

            public Builder stream(@Nullable Boolean stream) {
                this.stream = stream;
                return this;
            }

            public Builder instructions(@Nullable String instructions) {
                this.instructions = instructions;
                return this;
            }

            public Builder optimizeInstructions(@Nullable Boolean optimizeInstructions) {
                this.optimizeInstructions = optimizeInstructions;
                return this;
            }

            public DashScopeAudioTTSRequest build() {
                return new DashScopeAudioTTSRequest(model, text, voice, languageType, stream,
                        instructions, optimizeInstructions);
            }
        }

    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class DashScopeAudioTTSResponse extends TextToSpeechResponse {
        @JsonProperty("request_id")
        private @Nullable String requestId;
        @JsonProperty("output")
        private @Nullable TTSOutput output;
        @JsonProperty("usage")
        private @Nullable TTSUsage usage;

        @JsonCreator
        public DashScopeAudioTTSResponse(
                @JsonProperty("request_id") @Nullable String requestId,
                @JsonProperty("output") @Nullable TTSOutput output,
                @JsonProperty("usage") @Nullable TTSUsage usage) {
            super(createSpeechList(output), null);
            this.requestId = requestId;
            this.output = output;
            this.usage = usage;
        }

        public @Nullable String getRequestId() {
            return requestId;
        }

        public @Nullable TTSOutput getOutput() {
            return output;
        }

        public @Nullable TTSUsage getUsage() {
            return usage;
        }

        @JsonInclude(JsonInclude.Include.NON_NULL)
        public record TTSOutput(
                @JsonProperty("finish_reason") @Nullable String finishReason,
                @JsonProperty("audio") @Nullable TTSAudio audio) {
        }

        @JsonInclude(JsonInclude.Include.NON_NULL)
        public record TTSUsage(
                @JsonProperty("input_tokens") @Nullable Integer inputTokens,
                @JsonProperty("output_tokens") @Nullable Integer outputTokens,
                @JsonProperty("characters") @Nullable Integer characters,
                @JsonProperty("input_tokens_details") @Nullable InputTokensDetails inputTokensDetails,
                @JsonProperty("output_tokens_details") @Nullable OutputTokensDetails outputTokensDetails,
                @JsonProperty("total_tokens") @Nullable Integer totalTokens
        ){}

        @JsonInclude(JsonInclude.Include.NON_NULL)
        public record TTSAudio(
                @JsonProperty("data") @Nullable String data,
                @JsonProperty("url") @Nullable String url,
                @JsonProperty("id") @Nullable String id,
                @JsonProperty("expires_at") @Nullable Integer expiresAt
        ){}

        @JsonInclude(JsonInclude.Include.NON_NULL)
        public record InputTokensDetails(
                @JsonProperty("text_tokens") @Nullable Integer textTokens) {
        }

        @JsonInclude(JsonInclude.Include.NON_NULL)
        public record OutputTokensDetails(
                @JsonProperty("audio_tokens") @Nullable Integer audioTokens,
                @JsonProperty("text_tokens") @Nullable Integer textTokens) {
        }


        /**
         * Create Speech objects from the output.
         * If base64 audio data is available, decode it and create a Speech object.
         * Otherwise, create an empty Speech object (the URL can be accessed via getOutput()).
         */
        private static List<Speech> createSpeechList(@Nullable TTSOutput output) {
            if (output == null || output.audio() == null) {
                return List.of(new Speech(new byte[0]));
            }

            TTSAudio audio = output.audio();
            // Prefer base64 data over URL
            if (audio.data() != null && !audio.data().isEmpty()) {
                try {
                    byte[] audioData = Base64.getDecoder().decode(audio.data());
                    return List.of(new Speech(audioData));
                }
                catch (IllegalArgumentException e) {
                    // Invalid base64, return empty speech
                    return List.of(new Speech(new byte[0]));
                }
            }

            // If only URL is available, create empty speech (URL can be accessed separately)
            return List.of(new Speech(new byte[0]));
        }
    }

}
