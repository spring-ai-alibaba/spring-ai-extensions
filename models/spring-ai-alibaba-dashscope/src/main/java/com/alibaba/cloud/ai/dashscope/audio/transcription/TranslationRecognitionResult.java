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
 * Rich result type for Gummy translation-recognition models (long-speech and chat) via WebSocket.
 * <p>
 * Maps to the {@code payload.output} section of the DashScope WebSocket response.
 * Corresponds to the SDK's {@code TranslationRecognizerResult} with full field coverage,
 * including both source-language transcription and multi-language translations.
 *
 * @author spring-ai-alibaba
 * @since 1.1.0.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public record TranslationRecognitionResult(
		@JsonProperty("transcription") Transcription transcription,
		@JsonProperty("translations") List<Translation> translations,
		@JsonProperty("usage") Usage usage) {

	/**
	 * Whether a sentence-end has been reached (in either transcription or translation).
	 */
	public boolean isSentenceEnd() {
		if (transcription != null && Boolean.TRUE.equals(transcription.sentenceEnd())) {
			return true;
		}
		if (translations != null) {
			for (Translation t : translations) {
				if (Boolean.TRUE.equals(t.sentenceEnd())) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Convenience: get the source-language transcription text.
	 */
	public String getTranscriptionText() {
		return transcription != null ? transcription.text() : null;
	}

	/**
	 * Convenience: get translation text for a specific target language.
	 */
	public String getTranslationText(String language) {
		if (translations == null) {
			return null;
		}
		for (Translation t : translations) {
			if (language.equals(t.lang())) {
				return t.text();
			}
		}
		return null;
	}

	/**
	 * Source-language transcription result.
	 */
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonIgnoreProperties(ignoreUnknown = true)
	public record Transcription(
			@JsonProperty("sentence_id") Long sentenceId,
			@JsonProperty("begin_time") Long beginTime,
			@JsonProperty("end_time") Long endTime,
			@JsonProperty("text") String text,
			@JsonProperty("words") List<Word> words,
			@JsonProperty("stash") Stash stash,
			@JsonProperty("sentence_end") Boolean sentenceEnd,
			@JsonProperty("vad_pre_end") Boolean vadPreEnd,
			@JsonProperty("pre_end_failed") Boolean preEndFailed,
			@JsonProperty("pre_end_timemillis") Long preEndTimemillis,
			@JsonProperty("pre_end_start_time") Long preEndStartTime,
			@JsonProperty("pre_end_end_time") Long preEndEndTime) {
	}

	/**
	 * A single target-language translation result.
	 */
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonIgnoreProperties(ignoreUnknown = true)
	public record Translation(
			@JsonProperty("lang") String lang,
			@JsonProperty("sentence_id") Long sentenceId,
			@JsonProperty("begin_time") Long beginTime,
			@JsonProperty("end_time") Long endTime,
			@JsonProperty("text") String text,
			@JsonProperty("words") List<Word> words,
			@JsonProperty("stash") Stash stash,
			@JsonProperty("sentence_end") Boolean sentenceEnd,
			@JsonProperty("vad_pre_end") Boolean vadPreEnd,
			@JsonProperty("pre_end_failed") Boolean preEndFailed,
			@JsonProperty("pre_end_timemillis") Long preEndTimemillis,
			@JsonProperty("pre_end_start_time") Long preEndStartTime,
			@JsonProperty("pre_end_end_time") Long preEndEndTime) {
	}

	/**
	 * Intermediate (not yet finalized) result, available before sentenceEnd.
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
