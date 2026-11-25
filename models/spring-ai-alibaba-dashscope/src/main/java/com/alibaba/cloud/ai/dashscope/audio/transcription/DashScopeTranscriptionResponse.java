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

import com.alibaba.cloud.ai.dashscope.metadata.audio.DashScopeAudioTranscriptionMetadata;
import com.alibaba.cloud.ai.dashscope.metadata.audio.DashScopeAudioTranscriptionResponseMetadata;
import com.alibaba.cloud.ai.dashscope.metadata.audio.DashScopeAudioTranscriptionResponseMetadata.Sentence;
import com.alibaba.cloud.ai.dashscope.metadata.audio.DashScopeAudioTranscriptionResponseMetadata.Translation;
import com.alibaba.cloud.ai.dashscope.metadata.audio.DashScopeAudioTranscriptionResponseMetadata.Usage;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jspecify.annotations.Nullable;
import org.springframework.ai.audio.transcription.AudioTranscription;
import org.springframework.ai.audio.transcription.AudioTranscriptionResponse;

import java.util.List;
import java.util.Objects;

/**
 * @author yingzi
 * @since 2026/2/1
 */

public class DashScopeTranscriptionResponse extends AudioTranscriptionResponse {

    private final @Nullable DashScopeAudioTranscription transcription;

    private final DashScopeAudioTranscriptionResponseMetadata metadata;

    public DashScopeTranscriptionResponse(List<Translation> transcript, DashScopeAudioTranscription transcription) {
        super(transcription);
        this.metadata = new DashScopeAudioTranscriptionResponseMetadata(transcript);
        this.transcription = transcription;
    }

    @SuppressWarnings("NullAway")
    public DashScopeTranscriptionResponse(Sentence sentence, Usage usage) {
        super(null);
        this.transcription = null;
        this.metadata = new DashScopeAudioTranscriptionResponseMetadata(sentence, usage);
    }

    public DashScopeAudioTranscription getResult() {
        return Objects.requireNonNull(this.transcription, "transcription must not be null");
    }

    public DashScopeAudioTranscriptionResponseMetadata getMetadata() {
        return this.metadata;
    }

    @SuppressWarnings("NullAway")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DashScopeAudioTranscription extends AudioTranscription {
        @JsonProperty("text")
        private String text;

        @JsonProperty("metadata")
        private @Nullable DashScopeAudioTranscriptionMetadata metadata;

        @JsonCreator
        public DashScopeAudioTranscription(@JsonProperty("text") @JsonAlias("transcript") String text) {
            super(text != null ? text : "");
            this.text = text;
        }

        public String getText() {
            return text;
        }

        public @Nullable DashScopeAudioTranscriptionMetadata getMetadata() {
            return metadata;
        }

        public void setMetadata(@Nullable DashScopeAudioTranscriptionMetadata metadata) {
            this.metadata = metadata;
        }

    }

}
