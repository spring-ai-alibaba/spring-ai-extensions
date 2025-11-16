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

import com.alibaba.cloud.ai.dashscope.api.DashScopeAudioTranscriptionApi;
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
	private DashScopeAudioTranscriptionApi.AudioFormat format = DashScopeAudioTranscriptionApi.AudioFormat.PCM;

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

	@JsonProperty("semantic_punctuation_enabled")
	private Boolean semanticPunctuationEnabled = false;

	@JsonProperty("max_sentence_silence")
	private Integer maxSentenceSilence = 800;

	@JsonProperty("multi_threshold_mode_enabled")
	private Boolean multiThresholdModeEnabled = false;

	@JsonProperty("punctuation_prediction_enabled")
	private Boolean punctuationPredictionEnabled = true;

	@JsonProperty("heartbeat")
	private Boolean heartbeat = false;

	@JsonProperty("inverse_text_normalization_enabled")
	private Boolean inverseTextNormalizationEnabled = true;

	@JsonProperty("source_language")
	private String sourceLanguage;

	@JsonProperty("transcription_enabled")
	private Boolean transcriptionEnabled = true;

	@JsonProperty("translation_enabled")
	private Boolean translationEnabled = false;

	@JsonProperty("translation_target_languages")
	private List<String> translationTargetLanguages;

	@JsonProperty("max_end_silence")
	private Integer maxEndSilence = 800;

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

	public DashScopeAudioTranscriptionApi.AudioFormat getFormat() {
		return format;
	}

	public void setFormat(DashScopeAudioTranscriptionApi.AudioFormat format) {
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

	public Boolean getSemanticPunctuationEnabled() {
		return semanticPunctuationEnabled;
	}

	public void setSemanticPunctuationEnabled(Boolean semanticPunctuationEnabled) {
		this.semanticPunctuationEnabled = semanticPunctuationEnabled;
	}

	public Integer getMaxSentenceSilence() {
		return maxSentenceSilence;
	}

	public void setMaxSentenceSilence(Integer maxSentenceSilence) {
		this.maxSentenceSilence = maxSentenceSilence;
	}

	public Boolean getMultiThresholdModeEnabled() {
		return multiThresholdModeEnabled;
	}

	public void setMultiThresholdModeEnabled(Boolean multiThresholdModeEnabled) {
		this.multiThresholdModeEnabled = multiThresholdModeEnabled;
	}

	public Boolean getPunctuationPredictionEnabled() {
		return punctuationPredictionEnabled;
	}

	public void setPunctuationPredictionEnabled(Boolean punctuationPredictionEnabled) {
		this.punctuationPredictionEnabled = punctuationPredictionEnabled;
	}

	public Boolean getHeartbeat() {
		return heartbeat;
	}

	public void setHeartbeat(Boolean heartbeat) {
		this.heartbeat = heartbeat;
	}

	public Boolean getInverseTextNormalizationEnabled() {
		return inverseTextNormalizationEnabled;
	}

	public void setInverseTextNormalizationEnabled(Boolean inverseTextNormalizationEnabled) {
		this.inverseTextNormalizationEnabled = inverseTextNormalizationEnabled;
	}

	public String getSourceLanguage() {
		return sourceLanguage;
	}

	public void setSourceLanguage(String sourceLanguage) {
		this.sourceLanguage = sourceLanguage;
	}

	public Boolean getTranscriptionEnabled() {
		return transcriptionEnabled;
	}

	public void setTranscriptionEnabled(Boolean transcriptionEnabled) {
		this.transcriptionEnabled = transcriptionEnabled;
	}

	public Boolean getTranslationEnabled() {
		return translationEnabled;
	}

	public void setTranslationEnabled(Boolean translationEnabled) {
		this.translationEnabled = translationEnabled;
	}

	public List<String> getTranslationTargetLanguages() {
		return translationTargetLanguages;
	}

	public void setTranslationTargetLanguages(List<String> translationTargetLanguages) {
		this.translationTargetLanguages = translationTargetLanguages;
	}

	public Integer getMaxEndSilence() {
		return maxEndSilence;
	}

	public void setMaxEndSilence(Integer maxEndSilence) {
		this.maxEndSilence = maxEndSilence;
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

		public Builder withFormat(DashScopeAudioTranscriptionApi.AudioFormat format) {
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


		public Builder withSemanticPunctuationEnabled(Boolean semanticPunctuationEnabled) {
			options.setSemanticPunctuationEnabled(semanticPunctuationEnabled);
			return this;
		}

		public Builder withMaxSentenceSilence(Integer maxSentenceSilence) {
			options.setMaxSentenceSilence(maxSentenceSilence);
			return this;
		}

		public Builder withMultiThresholdModeEnabled(Boolean multiThresholdModeEnabled) {
			options.setMultiThresholdModeEnabled(multiThresholdModeEnabled);
			return this;
		}

		public Builder withPunctuationPredictionEnabled(Boolean punctuationPredictionEnabled) {
			options.setPunctuationPredictionEnabled(punctuationPredictionEnabled);
			return this;
		}

		public Builder withHeartbeat(Boolean heartbeat) {
			options.setHeartbeat(heartbeat);
			return this;
		}

		public Builder withInverseTextNormalizationEnabled(Boolean inverseTextNormalizationEnabled) {
			options.setInverseTextNormalizationEnabled(inverseTextNormalizationEnabled);
			return this;
		}

		public Builder withSourceLanguage(String sourceLanguage) {
			options.setSourceLanguage(sourceLanguage);
			return this;
		}

		public Builder withTranscriptionEnabled(Boolean transcriptionEnabled) {
			options.setTranscriptionEnabled(transcriptionEnabled);
			return this;
		}

		public Builder withTranslationEnabled(Boolean translationEnabled) {
			options.setTranslationEnabled(translationEnabled);
			return this;
		}

		public Builder withTranslationTargetLanguages(List<String> translationTargetLanguages) {
			options.setTranslationTargetLanguages(translationTargetLanguages);
			return this;
		}

		public Builder withMaxEndSilence(Integer maxEndSilence) {
			options.setMaxEndSilence(maxEndSilence);
			return this;
		}

		public DashScopeAudioTranscriptionOptions build() {
			return options;
		}

	}

}
