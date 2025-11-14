/*
 * Copyright 2024-2025 the original author or authors.
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
package com.alibaba.cloud.ai.dashscope.audio;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.springframework.ai.audio.transcription.AudioTranscriptionOptions;

/**
 * @author xYLiu
 * @author yuluo
 * @author kevinlin09
 * @author xuguan
 */
public class DashScopeAudioTranscriptionOptions implements AudioTranscriptionOptions {

	// @formatter:off
    /**
     * Audio Transcription models.
     */
    @JsonProperty("model")
    private String model;

    @JsonProperty("vocabulary_id")
    private String vocabularyId;

    @JsonProperty("resource_id")
    private String resourceId;

	@JsonProperty("sample_rate")
	private Integer sampleRate;

	@JsonProperty("format")
	private AudioFormat format;

    @JsonProperty("channel_id")
    private List<Integer> channelId = List.of(0);

    @JsonProperty("disfluency_removal_enabled")
    private Boolean disfluencyRemovalEnabled = false;

	@JsonProperty("timestamp_alignment_enabled")
	private Boolean timestampAlignmentEnabled = false;

	@JsonProperty("special_word_filter")
	private String specialWordFilter;

    @JsonProperty("language_hints")
    private List<String> languageHints = List.of("zh", "en");

	@JsonProperty("diarization_enabled")
	private Boolean diarizationEnabled = false;

	@JsonProperty("speaker_count")
	private Integer speakerCount;

    // @formatter:on
	@Override
	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getVocabularyId() {
		return vocabularyId;
	}

	public void setVocabularyId(String vocabularyId) {
		this.vocabularyId = vocabularyId;
	}

	public String getResourceId() {
		return resourceId;
	}

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}

	public Integer getSampleRate() {
		return sampleRate;
	}

	public void setSampleRate(Integer sampleRate) {
		this.sampleRate = sampleRate;
	}

	public AudioFormat getFormat() {
		return format;
	}

	public void setFormat(AudioFormat format) {
		this.format = format;
	}

	public List<Integer> getChannelId() {
		return channelId;
	}

	public void setChannelId(List<Integer> channelId) {
		this.channelId = channelId;
	}

	public Boolean getDisfluencyRemovalEnabled() {
		return disfluencyRemovalEnabled;
	}

	public void setDisfluencyRemovalEnabled(Boolean disfluencyRemovalEnabled) {
		this.disfluencyRemovalEnabled = disfluencyRemovalEnabled;
	}

	public Boolean getTimestampAlignmentEnabled() {
		return timestampAlignmentEnabled;
	}

	public void setTimestampAlignmentEnabled(Boolean timestampAlignmentEnabled) {
		this.timestampAlignmentEnabled = timestampAlignmentEnabled;
	}

	public String getSpecialWordFilter() {
		return specialWordFilter;
	}

	public void setSpecialWordFilter(String specialWordFilter) {
		this.specialWordFilter = specialWordFilter;
	}

	public List<String> getLanguageHints() {
		return languageHints;
	}

	public void setLanguageHints(List<String> languageHints) {
		this.languageHints = languageHints;
	}

	public Boolean getDiarizationEnabled() {
		return diarizationEnabled;
	}

	public void setDiarizationEnabled(Boolean diarizationEnabled) {
		this.diarizationEnabled = diarizationEnabled;
	}

	public Integer getSpeakerCount() {
		return speakerCount;
	}

	public void setSpeakerCount(Integer speakerCount) {
		this.speakerCount = speakerCount;
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {

		private final DashScopeAudioTranscriptionOptions options = new DashScopeAudioTranscriptionOptions();

		public Builder withModel(String model) {
			options.setModel(model);
			return this;
		}

		public Builder withVocabularyId(String vocabularyId) {
			options.setVocabularyId(vocabularyId);
			return this;
		}

		public Builder withResourceId(String resourceId) {
			options.setResourceId(resourceId);
			return this;
		}

		public Builder withSampleRate(Integer sampleRate) {
			options.setSampleRate(sampleRate);
			return this;
		}

		public Builder withFormat(AudioFormat format) {
			options.setFormat(format);
			return this;
		}

		public Builder withChannelId(List<Integer> channelId) {
			options.setChannelId(channelId);
			return this;
		}

		public Builder withDisfluencyRemovalEnabled(Boolean disfluencyRemovalEnabled) {
			options.setDisfluencyRemovalEnabled(disfluencyRemovalEnabled);
			return this;
		}

		public Builder withTimestampAlignmentEnabled(Boolean timestampAlignmentEnabled) {
			options.setTimestampAlignmentEnabled(timestampAlignmentEnabled);
			return this;
		}

		public Builder withSpecialWordFilter(String specialWordFilter) {
			options.setSpecialWordFilter(specialWordFilter);
			return this;
		}

		public Builder withLanguageHints(List<String> languageHints) {
			options.setLanguageHints(languageHints);
			return this;
		}

		public Builder withDiarizationEnabled(Boolean diarizationEnabled) {
			options.setDiarizationEnabled(diarizationEnabled);
			return this;
		}

		public Builder withSpeakerCount(Integer speakerCount) {
			options.setSpeakerCount(speakerCount);
			return this;
		}
		public DashScopeAudioTranscriptionOptions build() {
			return options;
		}

	}

	public enum AudioFormat {

		// @formatter:off
		@JsonProperty("pcm") PCM("pcm"),
		@JsonProperty("wav") WAV("wav"),
		@JsonProperty("mp3") MP3("mp3"),
		@JsonProperty("opus") OPUS("opus"),
		@JsonProperty("speex") SPEEX("speex"),
		@JsonProperty("aac") AAC("aac"),
		@JsonProperty("amr") AMR("amr");

		// @formatter:on

		public final String value;

		AudioFormat(String value) {
			this.value = value;
		}

		public String getValue() {
			return this.value;
		}

	}

}
