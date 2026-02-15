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
        private String model;

        @JsonProperty("input")
        private TTSInput input;

        public DashScopeAudioTTSRequest(String model, String text, String voice, String languageType) {
            this.model = model;
            this.input = new TTSInput();
            this.input.text = text;
            this.input.voice = voice;
            this.input.languageType = languageType;
        }

        @JsonInclude(JsonInclude.Include.NON_NULL)
        private class TTSInput {
            @JsonProperty("text")
            private String text;

            @JsonProperty("voice")
            private String voice;

            @JsonProperty("language_type")
            private String languageType;
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private String model;
            private String text;
            private String voice;
            private String languageType;

            public Builder model(String model) {
                this.model = model;
                return this;
            }

            public Builder text(String text) {
                this.text = text;
                return this;
            }

            public Builder voice(String voice) {
                this.voice = voice;
                return this;
            }

            public Builder languageType(String languageType) {
                this.languageType = languageType;
                return this;
            }

            public DashScopeAudioTTSRequest build() {
                return new DashScopeAudioTTSRequest(model, text, voice, languageType);
            }
        }

    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class DashScopeAudioTTSResponse extends TextToSpeechResponse {
        @JsonProperty("request_id")
        private String requestId;
        @JsonProperty("output")
        private TTSOutput output;
        @JsonProperty("usage")
        private TTSUsage usage;

        @JsonCreator
        public DashScopeAudioTTSResponse(
                @JsonProperty("request_id") String requestId,
                @JsonProperty("output") TTSOutput output,
                @JsonProperty("usage") TTSUsage usage) {
            super(createSpeechList(output), null);
            this.requestId = requestId;
            this.output = output;
            this.usage = usage;
        }

        public String getRequestId() {
            return requestId;
        }

        public TTSOutput getOutput() {
            return output;
        }

        public TTSUsage getUsage() {
            return usage;
        }

        @JsonInclude(JsonInclude.Include.NON_NULL)
        public record TTSOutput(
                @JsonProperty("finish_reason") String finishReason,
                @JsonProperty("audio") TTSAudio audio) {
        }

        @JsonInclude(JsonInclude.Include.NON_NULL)
        public record TTSUsage(
                @JsonProperty("input_tokens") Integer inputTokens,
                @JsonProperty("output_tokens") Integer outputTokens,
                @JsonProperty("characters") Integer characters,
                @JsonProperty("input_tokens_details") InputTokensDetails inputTokensDetails,
                @JsonProperty("output_tokens_details") OutputTokensDetails outputTokensDetails,
                @JsonProperty("total_tokens") Integer totalTokens
        ){}

        @JsonInclude(JsonInclude.Include.NON_NULL)
        public record TTSAudio(
                @JsonProperty("data") String data,
                @JsonProperty("url") String url,
                @JsonProperty("id") String id,
                @JsonProperty("expires_at") Integer expiresAt
        ){}

        @JsonInclude(JsonInclude.Include.NON_NULL)
        public record InputTokensDetails(
                @JsonProperty("text_tokens") Integer textTokens) {
        }

        @JsonInclude(JsonInclude.Include.NON_NULL)
        public record OutputTokensDetails(
                @JsonProperty("audio_tokens") Integer audioTokens,
                @JsonProperty("text_tokens") Integer textTokens) {
        }


        /**
         * Create Speech objects from the output.
         * If base64 audio data is available, decode it and create a Speech object.
         * Otherwise, create an empty Speech object (the URL can be accessed via getOutput()).
         */
        private static List<Speech> createSpeechList(TTSOutput output) {
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
