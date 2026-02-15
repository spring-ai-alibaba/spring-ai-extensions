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
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jspecify.annotations.NonNull;
import org.springframework.ai.audio.transcription.AudioTranscription;
import org.springframework.ai.audio.transcription.AudioTranscriptionResponse;

import java.util.List;

/**
 * @author yingzi
 * @since 2026/2/1
 */

public class DashScopeTranscriptionResponse extends AudioTranscriptionResponse {

    private final DashScopeAudioTranscription transcription;

    private final DashScopeAudioTranscriptionResponseMetadata metadata;

    public DashScopeTranscriptionResponse(List<Translation> transcript, DashScopeAudioTranscription transcription) {
        super(transcription);
        this.metadata = new DashScopeAudioTranscriptionResponseMetadata(transcript);
        this.transcription = transcription;
    }

    public DashScopeTranscriptionResponse(Sentence sentence, Usage usage) {
        super(null);
        this.transcription = null;
        this.metadata = new DashScopeAudioTranscriptionResponseMetadata(sentence, usage);
    }

    @NonNull
    public DashScopeAudioTranscription getResult() {
        assert this.transcription != null;
        return this.transcription;
    }

    public DashScopeAudioTranscriptionResponseMetadata getMetadata() {
        return this.metadata;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class DashScopeAudioTranscription extends AudioTranscription {
        @JsonProperty("text")
        private String text;

        private DashScopeAudioTranscriptionMetadata metadata;

        public DashScopeAudioTranscription(String text) {
            super(text);
        }

        public DashScopeAudioTranscriptionMetadata withTranscriptionMetadata(DashScopeAudioTranscriptionMetadata metadata) {
            return this.metadata = metadata;
        }

        public String getText() {
            return text;
        }

        public DashScopeAudioTranscriptionMetadata getMetadata() {
            return metadata;
        }

    }

}

