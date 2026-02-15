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

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.ai.audio.transcription.AudioTranscriptionMetadata;

import java.util.List;

/**
 * @author yingzi
 * @since 2026/2/7
 */

public record DashScopeAudioTranscriptionMetadata(
        @JsonProperty("sentence_id") Integer sentenceId,
        @JsonProperty("begin_time") Integer beginTime,
        @JsonProperty("end_time") Integer endTime,
        @JsonProperty("words") List<DashScopeAudioTranscriptionResponseMetadata.Translation.Word> words,
        @JsonProperty("sentence_end") Boolean sentenceEnd,
        @JsonProperty("channel_id") Integer channelId,
        @JsonProperty("content_duration_in_milliseconds") Integer contentDurationInMilliseconds,
        @JsonProperty("sentences") List<DashScopeAudioTranscriptionResponseMetadata.Sentence> sentences
) implements AudioTranscriptionMetadata {}
