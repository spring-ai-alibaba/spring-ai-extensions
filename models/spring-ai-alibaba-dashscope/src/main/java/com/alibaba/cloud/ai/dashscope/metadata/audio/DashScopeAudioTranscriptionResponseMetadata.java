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
package com.alibaba.cloud.ai.dashscope.metadata.audio;

import com.alibaba.cloud.ai.dashscope.metadata.audio.DashScopeAudioTranscriptionResponseMetadata.Translation.Word;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.ai.audio.transcription.AudioTranscriptionResponseMetadata;
import org.springframework.ai.chat.metadata.RateLimit;

import java.util.List;

/**
 * Audio transcription metadata implementation for {@literal DashScope}.
 *
 * @author yuluo
 * @see RateLimit
 */
public class DashScopeAudioTranscriptionResponseMetadata extends AudioTranscriptionResponseMetadata {

    private final List<Translation> translations;

    private final Sentence sentence;

    private final Usage usage;

	public static final DashScopeAudioTranscriptionResponseMetadata NULL = new DashScopeAudioTranscriptionResponseMetadata() {

	};

    protected DashScopeAudioTranscriptionResponseMetadata() {
        this.translations = null;
        this.sentence = null;
        this.usage = null;
    }

    public DashScopeAudioTranscriptionResponseMetadata(List<Translation> translations) {
        this.translations = translations;
        this.sentence = null;
        this.usage = null;
    }

    public DashScopeAudioTranscriptionResponseMetadata(Sentence sentence, Usage usage) {
        this.translations = null;
        this.sentence = sentence;
        this.usage = usage;
    }

    public List<Translation> getTranslations() {
        return translations;
    }

    public Sentence getSentence() {
        return sentence;
    }

    public Usage getUsage() {
        return usage;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Sentence(
            @JsonProperty("begin_time") Integer beginTime,
            @JsonProperty("end_time") Integer endTime,
            @JsonProperty("text") String text,
            @JsonProperty("heartbeat") Boolean heartbeat,
            @JsonProperty("sentence_end") Boolean sentenceEnd,
            @JsonProperty("emo_tag") String emoTag,
            @JsonProperty("emo_confidence") Double emoConfidence,
            @JsonProperty("words") List<Word> words,
            @JsonProperty("sentence_id") Integer sentenceId,
            @JsonProperty("speaker_id") Integer speakerId
    ) {}

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Usage(
            @JsonProperty("duration") Integer duration
    ) {}

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Translation(
            @JsonProperty("sentence_id") Integer sentenceId,
            @JsonProperty("begin_time") Integer beginTime,
            @JsonProperty("end_time") Integer endTime,
            @JsonProperty("text") String text,
            @JsonProperty("lang") String lang,
            @JsonProperty("words") List<Word> words,
            @JsonProperty("sentence_end") Boolean sentenceEnd,
            @JsonProperty("speaker_id") Integer speakerId
    ) {
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public record Word(
                @JsonProperty("begin_time") Integer beginTime,
                @JsonProperty("end_time") Integer endTime,
                @JsonProperty("text") String text,
                @JsonProperty("punctuation") String punctuation,
                @JsonProperty("fixed") Boolean fixed) {
        }
    }

}
