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

package com.alibaba.cloud.ai.dashscope.sdk.audio.transcription;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jspecify.annotations.Nullable;
import org.springframework.ai.audio.transcription.AudioTranscriptionOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Options for DashScope SDK audio transcription model.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DashScopeSdkAudioTranscriptionOptions implements AudioTranscriptionOptions {

	@JsonProperty("model")
	private @Nullable String model;

	@JsonProperty("file_urls")
	private @Nullable List<String> fileUrls;

	@JsonProperty("phrase_id")
	private @Nullable String phraseId;

	@JsonProperty("channel_id")
	private @Nullable List<Integer> channelId;

	@JsonProperty("diarization_enabled")
	private @Nullable Boolean diarizationEnabled;

	@JsonProperty("speaker_count")
	private @Nullable Integer speakerCount;

	@JsonProperty("disfluency_removal_enabled")
	private @Nullable Boolean disfluencyRemovalEnabled;

	@JsonProperty("timestamp_alignment_enabled")
	private @Nullable Boolean timestampAlignmentEnabled;

	@JsonProperty("special_word_filter")
	private @Nullable String specialWordFilter;

	@JsonProperty("audio_event_detection_enabled")
	private @Nullable Boolean audioEventDetectionEnabled;

	@JsonIgnore
	private Map<String, String> httpHeaders = new HashMap<>();

	public static DashScopeSdkAudioTranscriptionOptionsBuilder builder() {
		return new DashScopeSdkAudioTranscriptionOptionsBuilder();
	}

	public static @Nullable DashScopeSdkAudioTranscriptionOptions fromOptions(
			@Nullable DashScopeSdkAudioTranscriptionOptions options) {
		if (options == null) {
			return null;
		}
		DashScopeSdkAudioTranscriptionOptions copy = new DashScopeSdkAudioTranscriptionOptions();
		copy.setModel(options.getModel());
		copy.setFileUrls(options.getFileUrls() == null ? null : new ArrayList<>(options.getFileUrls()));
		copy.setPhraseId(options.getPhraseId());
		copy.setChannelId(options.getChannelId() == null ? null : new ArrayList<>(options.getChannelId()));
		copy.setDiarizationEnabled(options.getDiarizationEnabled());
		copy.setSpeakerCount(options.getSpeakerCount());
		copy.setDisfluencyRemovalEnabled(options.getDisfluencyRemovalEnabled());
		copy.setTimestampAlignmentEnabled(options.getTimestampAlignmentEnabled());
		copy.setSpecialWordFilter(options.getSpecialWordFilter());
		copy.setAudioEventDetectionEnabled(options.getAudioEventDetectionEnabled());
		copy.setHttpHeaders(options.getHttpHeaders() == null ? new HashMap<>() : new HashMap<>(options.getHttpHeaders()));
		return copy;
	}

	@Override
	public @Nullable String getModel() {
		return this.model;
	}

	public void setModel(@Nullable String model) {
		this.model = model;
	}

	public @Nullable List<String> getFileUrls() {
		return this.fileUrls;
	}

	public void setFileUrls(@Nullable List<String> fileUrls) {
		this.fileUrls = fileUrls;
	}

	public @Nullable String getPhraseId() {
		return this.phraseId;
	}

	public void setPhraseId(@Nullable String phraseId) {
		this.phraseId = phraseId;
	}

	public @Nullable List<Integer> getChannelId() {
		return this.channelId;
	}

	public void setChannelId(@Nullable List<Integer> channelId) {
		this.channelId = channelId;
	}

	public @Nullable Boolean getDiarizationEnabled() {
		return this.diarizationEnabled;
	}

	public void setDiarizationEnabled(@Nullable Boolean diarizationEnabled) {
		this.diarizationEnabled = diarizationEnabled;
	}

	public @Nullable Integer getSpeakerCount() {
		return this.speakerCount;
	}

	public void setSpeakerCount(@Nullable Integer speakerCount) {
		this.speakerCount = speakerCount;
	}

	public @Nullable Boolean getDisfluencyRemovalEnabled() {
		return this.disfluencyRemovalEnabled;
	}

	public void setDisfluencyRemovalEnabled(@Nullable Boolean disfluencyRemovalEnabled) {
		this.disfluencyRemovalEnabled = disfluencyRemovalEnabled;
	}

	public @Nullable Boolean getTimestampAlignmentEnabled() {
		return this.timestampAlignmentEnabled;
	}

	public void setTimestampAlignmentEnabled(@Nullable Boolean timestampAlignmentEnabled) {
		this.timestampAlignmentEnabled = timestampAlignmentEnabled;
	}

	public @Nullable String getSpecialWordFilter() {
		return this.specialWordFilter;
	}

	public void setSpecialWordFilter(@Nullable String specialWordFilter) {
		this.specialWordFilter = specialWordFilter;
	}

	public @Nullable Boolean getAudioEventDetectionEnabled() {
		return this.audioEventDetectionEnabled;
	}

	public void setAudioEventDetectionEnabled(@Nullable Boolean audioEventDetectionEnabled) {
		this.audioEventDetectionEnabled = audioEventDetectionEnabled;
	}

	public Map<String, String> getHttpHeaders() {
		return this.httpHeaders;
	}

	public void setHttpHeaders(Map<String, String> httpHeaders) {
		this.httpHeaders = httpHeaders;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		DashScopeSdkAudioTranscriptionOptions that = (DashScopeSdkAudioTranscriptionOptions) o;
		return Objects.equals(this.model, that.model) && Objects.equals(this.fileUrls, that.fileUrls)
				&& Objects.equals(this.phraseId, that.phraseId) && Objects.equals(this.channelId, that.channelId)
				&& Objects.equals(this.diarizationEnabled, that.diarizationEnabled)
				&& Objects.equals(this.speakerCount, that.speakerCount)
				&& Objects.equals(this.disfluencyRemovalEnabled, that.disfluencyRemovalEnabled)
				&& Objects.equals(this.timestampAlignmentEnabled, that.timestampAlignmentEnabled)
				&& Objects.equals(this.specialWordFilter, that.specialWordFilter)
				&& Objects.equals(this.audioEventDetectionEnabled, that.audioEventDetectionEnabled)
				&& Objects.equals(this.httpHeaders, that.httpHeaders);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.model, this.fileUrls, this.phraseId, this.channelId, this.diarizationEnabled,
				this.speakerCount, this.disfluencyRemovalEnabled, this.timestampAlignmentEnabled,
				this.specialWordFilter, this.audioEventDetectionEnabled, this.httpHeaders);
	}

	public static class DashScopeSdkAudioTranscriptionOptionsBuilder {

		private final DashScopeSdkAudioTranscriptionOptions options;

		public DashScopeSdkAudioTranscriptionOptionsBuilder() {
			this.options = new DashScopeSdkAudioTranscriptionOptions();
		}

		public DashScopeSdkAudioTranscriptionOptionsBuilder model(@Nullable String model) {
			this.options.model = model;
			return this;
		}

		public DashScopeSdkAudioTranscriptionOptionsBuilder fileUrls(@Nullable List<String> fileUrls) {
			this.options.fileUrls = fileUrls;
			return this;
		}

		public DashScopeSdkAudioTranscriptionOptionsBuilder phraseId(@Nullable String phraseId) {
			this.options.phraseId = phraseId;
			return this;
		}

		public DashScopeSdkAudioTranscriptionOptionsBuilder channelId(@Nullable List<Integer> channelId) {
			this.options.channelId = channelId;
			return this;
		}

		public DashScopeSdkAudioTranscriptionOptionsBuilder diarizationEnabled(
				@Nullable Boolean diarizationEnabled) {
			this.options.diarizationEnabled = diarizationEnabled;
			return this;
		}

		public DashScopeSdkAudioTranscriptionOptionsBuilder speakerCount(@Nullable Integer speakerCount) {
			this.options.speakerCount = speakerCount;
			return this;
		}

		public DashScopeSdkAudioTranscriptionOptionsBuilder disfluencyRemovalEnabled(
				@Nullable Boolean disfluencyRemovalEnabled) {
			this.options.disfluencyRemovalEnabled = disfluencyRemovalEnabled;
			return this;
		}

		public DashScopeSdkAudioTranscriptionOptionsBuilder timestampAlignmentEnabled(
				@Nullable Boolean timestampAlignmentEnabled) {
			this.options.timestampAlignmentEnabled = timestampAlignmentEnabled;
			return this;
		}

		public DashScopeSdkAudioTranscriptionOptionsBuilder specialWordFilter(@Nullable String specialWordFilter) {
			this.options.specialWordFilter = specialWordFilter;
			return this;
		}

		public DashScopeSdkAudioTranscriptionOptionsBuilder audioEventDetectionEnabled(@Nullable Boolean enabled) {
			this.options.audioEventDetectionEnabled = enabled;
			return this;
		}

		public DashScopeSdkAudioTranscriptionOptionsBuilder httpHeaders(Map<String, String> httpHeaders) {
			this.options.httpHeaders = httpHeaders;
			return this;
		}

		public DashScopeSdkAudioTranscriptionOptions build() {
			return this.options;
		}

	}

}
