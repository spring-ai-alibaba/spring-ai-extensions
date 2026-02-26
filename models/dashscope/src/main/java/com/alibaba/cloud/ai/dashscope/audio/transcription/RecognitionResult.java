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

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Rich result type for Paraformer / Fun-ASR real-time recognition via WebSocket.
 * <p>
 * Maps to the {@code payload.output} section of the DashScope WebSocket response.
 * Corresponds to the SDK's {@code RecognitionResult} with full field coverage including
 * {@code stash} (intermediate results), {@code vadPreEnd}, emotion tags, etc.
 *
 * @author spring-ai-alibaba
 * @since 1.1.0.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public record RecognitionResult(
		@JsonProperty("sentence") Sentence sentence,
		@JsonProperty("usage") Usage usage) {

	/**
	 * Whether this is a sentence-end event (final result for this sentence).
	 */
	public boolean isSentenceEnd() {
		return sentence != null
				&& (Boolean.TRUE.equals(sentence.sentenceEnd()) || sentence.endTime() != null);
	}

	/**
	 * Whether this is a sentence-begin event (first partial result for a new sentence).
	 */
	public boolean isSentenceBegin() {
		return sentence != null && Boolean.TRUE.equals(sentence.sentenceBegin());
	}

	/**
	 * Convenience: get the recognized text (from sentence or stash).
	 */
	public String getText() {
		if (sentence == null) {
			return null;
		}
		return sentence.text();
	}

	/**
	 * A single recognition sentence, including both final and intermediate results.
	 */
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonIgnoreProperties(ignoreUnknown = true)
	public record Sentence(
			@JsonProperty("sentence_id") Long sentenceId,
			@JsonProperty("begin_time") Long beginTime,
			@JsonProperty("end_time") Long endTime,
			@JsonProperty("text") String text,
			@JsonProperty("words") List<Word> words,
			@JsonProperty("stash") Stash stash,
			@JsonProperty("emo_tag") String emoTag,
			@JsonProperty("emo_confidence") Double emoConfidence,
			@JsonProperty("heartbeat") Boolean heartbeat,
			@JsonProperty("vad_pre_end") Boolean vadPreEnd,
			@JsonProperty("pre_end_failed") Boolean preEndFailed,
			@JsonProperty("pre_end_timemillis") Long preEndTimemillis,
			@JsonProperty("pre_end_start_time") Long preEndStartTime,
			@JsonProperty("pre_end_end_time") Long preEndEndTime,
			@JsonProperty("sentence_begin") Boolean sentenceBegin,
			@JsonProperty("sentence_end") Boolean sentenceEnd,
			@JsonProperty("speaker_id") Integer speakerId) {
	}

	/**
	 * Intermediate (not yet finalized) recognition result.
	 * Available before {@code sentenceEnd} for low-latency display.
	 */
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonIgnoreProperties(ignoreUnknown = true)
	public record Stash(
			@JsonProperty("sentence_id") Long sentenceId,
			@JsonProperty("begin_time") Long beginTime,
			@JsonProperty("end_time") Long endTime,
			@JsonProperty("text") String text,
			@JsonProperty("words") List<Word> words) {
	}

	/**
	 * A single word with timestamps and punctuation.
	 */
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonIgnoreProperties(ignoreUnknown = true)
	public record Word(
			@JsonProperty("begin_time") Long beginTime,
			@JsonProperty("end_time") Long endTime,
			@JsonProperty("text") String text,
			@JsonProperty("punctuation") String punctuation,
			@JsonProperty("fixed") Boolean fixed) {
	}

	/**
	 * Usage information (e.g., audio duration in milliseconds).
	 */
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonIgnoreProperties(ignoreUnknown = true)
	public record Usage(
			@JsonProperty("duration") Integer duration) {
	}
}
