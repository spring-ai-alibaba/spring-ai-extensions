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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.alibaba.cloud.ai.dashscope.spec.DashScopeModel.AudioModel;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jspecify.annotations.Nullable;

import org.springframework.ai.audio.transcription.AudioTranscriptionOptions;

/**
 * @author xYLiu
 * @author yuluo
 * @author kevinlin09
 * @author xuguan
 * @author yingzi
 */
public class DashScopeAudioTranscriptionOptions implements AudioTranscriptionOptions {

	public static final String DEFAULT_MODEL = AudioModel.GUMMY_REALTIME_V1.getValue();

	@JsonProperty("model")
	private final String model;

	@JsonProperty("vocabulary_id")
	private final @Nullable String vocabularyId;

	@JsonProperty("sample_rate")
	private final Integer sampleRate;

	@JsonProperty("format")
	private final String format;

	@JsonProperty("source_language")
	private final @Nullable String sourceLanguage;

	@JsonProperty("transcription_enabled")
	private final @Nullable Boolean transcriptionEnabled;

	@JsonProperty("translation_enabled")
	private final @Nullable Boolean translationEnabled;

	@JsonProperty("translation_target_languages")
	private final @Nullable List<String> translationTargetLanguages;

	@JsonProperty("max_end_silence")
	private final @Nullable Integer maxEndSilence;

	@JsonProperty("modalities")
	private final @Nullable List<String> modalities;

	@JsonProperty("audio")
	private final @Nullable Audio audio;

	@JsonProperty("stream")
	private final @Nullable Boolean stream;

	@JsonProperty("stream_options")
	private final @Nullable StreamOptions streamOptions;

	@JsonProperty("max_tokens")
	private final @Nullable Integer maxTokens;

	@JsonProperty("seed")
	private final @Nullable Integer seed;

	@JsonProperty("temperature")
	private final @Nullable Float temperature;

	@JsonProperty("top_p")
	private final @Nullable Float topP;

	@JsonProperty("presence_penalty")
	private final @Nullable Float presencePenalty;

	@JsonProperty("top_k")
	private final @Nullable Integer topK;

	@JsonProperty("repetition_penalty")
	private final @Nullable Float repetitionPenalty;

	@JsonProperty("translation_options")
	private final @Nullable TranslationOptions translationOptions;

	@JsonProperty("disfluency_removal_enabled")
	private final @Nullable Boolean disfluencyRemovalEnabled;

	@JsonProperty("language_hints")
	private final @Nullable List<String> languageHints;

	@JsonProperty("semantic_punctuation_enabled")
	private final @Nullable Boolean semanticPunctuationEnabled;

	@JsonProperty("max_sentence_silence")
	private final @Nullable Integer maxSentenceSilence;

	@JsonProperty("multi_threshold_mode_enabled")
	private final @Nullable Boolean multiThresholdModeEnabled;

	@JsonProperty("punctuation_prediction_enabled")
	private final @Nullable Boolean punctuationPredictionEnabled;

	@JsonProperty("heartbeat")
	private final @Nullable Boolean heartbeat;

	@JsonProperty("inverse_text_normalization_enabled")
	private final @Nullable Boolean inverseTextNormalizationEnabled;

	@JsonProperty("resources")
	private final @Nullable List<Resource> resources;

	@JsonProperty("timestamp_alignment_enabled")
	private final @Nullable Boolean timestampAlignmentEnabled;

    @JsonProperty("special_word_filter")
    private @Nullable String specialWordFilter;

    @JsonProperty("diarization_enabled")
    private @Nullable Boolean diarizationEnabled;

	@JsonProperty("speaker_count")
	private final @Nullable Integer speakerCount;

	@JsonProperty("channel_id")
	private final @Nullable List<Integer> channelId;

	@JsonProperty("asr_options")
	private final @Nullable AsrOptions asrOptions;

	protected DashScopeAudioTranscriptionOptions(@Nullable String model, @Nullable String vocabularyId,
			@Nullable Integer sampleRate, @Nullable String format, @Nullable String sourceLanguage,
			@Nullable Boolean transcriptionEnabled, @Nullable Boolean translationEnabled,
			@Nullable List<String> translationTargetLanguages, @Nullable Integer maxEndSilence,
			@Nullable List<String> modalities, @Nullable Audio audio, @Nullable Boolean stream,
			@Nullable StreamOptions streamOptions, @Nullable Integer maxTokens, @Nullable Integer seed,
			@Nullable Float temperature, @Nullable Float topP, @Nullable Float presencePenalty,
			@Nullable Integer topK, @Nullable Float repetitionPenalty, @Nullable TranslationOptions translationOptions,
			@Nullable Boolean disfluencyRemovalEnabled, @Nullable List<String> languageHints,
			@Nullable Boolean semanticPunctuationEnabled, @Nullable Integer maxSentenceSilence,
			@Nullable Boolean multiThresholdModeEnabled, @Nullable Boolean punctuationPredictionEnabled,
			@Nullable Boolean heartbeat, @Nullable Boolean inverseTextNormalizationEnabled,
			@Nullable List<Resource> resources, @Nullable Boolean timestampAlignmentEnabled,
			@Nullable String specialWordFilter, @Nullable Boolean diarizationEnabled, @Nullable Integer speakerCount,
			@Nullable List<Integer> channelId, @Nullable AsrOptions asrOptions) {
		this.model = model != null ? model : DEFAULT_MODEL;
		this.vocabularyId = vocabularyId;
		this.sampleRate = sampleRate != null ? sampleRate : 16000;
		this.format = format != null ? format : "pcm";
		this.sourceLanguage = sourceLanguage;
		this.transcriptionEnabled = transcriptionEnabled;
		this.translationEnabled = translationEnabled;
		this.translationTargetLanguages = translationTargetLanguages != null ? List.copyOf(translationTargetLanguages) : null;
		this.maxEndSilence = maxEndSilence;
		this.modalities = modalities != null ? List.copyOf(modalities) : null;
		this.audio = audio;
		this.stream = stream;
		this.streamOptions = streamOptions;
		this.maxTokens = maxTokens;
		this.seed = seed;
		this.temperature = temperature;
		this.topP = topP;
		this.presencePenalty = presencePenalty;
		this.topK = topK;
		this.repetitionPenalty = repetitionPenalty;
		this.translationOptions = translationOptions;
		this.disfluencyRemovalEnabled = disfluencyRemovalEnabled;
		this.languageHints = languageHints != null ? List.copyOf(languageHints) : null;
		this.semanticPunctuationEnabled = semanticPunctuationEnabled;
		this.maxSentenceSilence = maxSentenceSilence;
		this.multiThresholdModeEnabled = multiThresholdModeEnabled;
		this.punctuationPredictionEnabled = punctuationPredictionEnabled;
		this.heartbeat = heartbeat;
		this.inverseTextNormalizationEnabled = inverseTextNormalizationEnabled;
		this.resources = resources != null ? List.copyOf(resources) : null;
		this.timestampAlignmentEnabled = timestampAlignmentEnabled;
		this.specialWordFilter = specialWordFilter;
		this.diarizationEnabled = diarizationEnabled;
		this.speakerCount = speakerCount;
		this.channelId = channelId != null ? List.copyOf(channelId) : null;
		this.asrOptions = asrOptions;
	}

	@Override
	public String getModel() {
		return this.model;
	}

	public @Nullable String getVocabularyId() {
		return this.vocabularyId;
	}

	public String getFormat() {
		return this.format;
	}

	public Integer getSampleRate() {
		return this.sampleRate;
	}

	public @Nullable String getSourceLanguage() {
		return this.sourceLanguage;
	}

	public @Nullable Boolean getTranscriptionEnabled() {
		return this.transcriptionEnabled;
	}

	public @Nullable Boolean getTranslationEnabled() {
		return this.translationEnabled;
	}

	public @Nullable List<String> getTranslationTargetLanguages() {
		return this.translationTargetLanguages;
	}

	public @Nullable AsrOptions getAsrOptions() {
		return this.asrOptions;
	}

	public @Nullable Integer getMaxEndSilence() {
		return this.maxEndSilence;
	}

	public @Nullable List<String> getModalities() {
		return this.modalities;
	}

	public @Nullable Audio getAudio() {
		return this.audio;
	}

	public @Nullable Boolean getStream() {
		return this.stream;
	}

	public @Nullable StreamOptions getStreamOptions() {
		return this.streamOptions;
	}

	public @Nullable Integer getMaxTokens() {
		return this.maxTokens;
	}

	public @Nullable Integer getSeed() {
		return this.seed;
	}

	public @Nullable Float getTemperature() {
		return this.temperature;
	}

	public @Nullable Float getTopP() {
		return this.topP;
	}

	public @Nullable Float getPresencePenalty() {
		return this.presencePenalty;
	}

	public @Nullable Integer getTopK() {
		return this.topK;
	}

	public @Nullable Float getRepetitionPenalty() {
		return this.repetitionPenalty;
	}

	public @Nullable TranslationOptions getTranslationOptions() {
		return this.translationOptions;
	}

	public @Nullable Boolean getDisfluencyRemovalEnabled() {
		return this.disfluencyRemovalEnabled;
	}

	public @Nullable List<String> getLanguageHints() {
		return this.languageHints;
	}

	public @Nullable Boolean getSemanticPunctuationEnabled() {
		return this.semanticPunctuationEnabled;
	}

	public @Nullable Integer getMaxSentenceSilence() {
		return this.maxSentenceSilence;
	}

	public @Nullable Boolean getMultiThresholdModeEnabled() {
		return this.multiThresholdModeEnabled;
	}

	public @Nullable Boolean getPunctuationPredictionEnabled() {
		return this.punctuationPredictionEnabled;
	}

	public @Nullable Boolean getHeartbeat() {
		return this.heartbeat;
	}

	public @Nullable Boolean getInverseTextNormalizationEnabled() {
		return this.inverseTextNormalizationEnabled;
	}

	public @Nullable List<Resource> getResources() {
		return this.resources;
	}

	public @Nullable Boolean getTimestampAlignmentEnabled() {
		return this.timestampAlignmentEnabled;
	}

	public @Nullable String getSpecialWordFilter() {
		return this.specialWordFilter;
	}

	public @Nullable Boolean getDiarizationEnabled() {
		return this.diarizationEnabled;
	}

	public @Nullable Integer getSpeakerCount() {
		return this.speakerCount;
	}

	public @Nullable List<Integer> getChannelId() {
		return this.channelId;
	}

	public static Builder builder() {
		return new Builder();
	}

	public Builder mutate() {
		return builder()
			.model(this.model)
			.vocabularyId(this.vocabularyId)
			.sampleRate(this.sampleRate)
			.format(this.format)
			.sourceLanguage(this.sourceLanguage)
			.transcriptionEnabled(this.transcriptionEnabled)
			.translationEnabled(this.translationEnabled)
			.translationTargetLanguages(this.translationTargetLanguages)
			.maxEndSilence(this.maxEndSilence)
			.modalities(this.modalities)
			.audio(this.audio)
			.stream(this.stream)
			.streamOptions(this.streamOptions)
			.maxTokens(this.maxTokens)
			.seed(this.seed)
			.temperature(this.temperature)
			.topP(this.topP)
			.presencePenalty(this.presencePenalty)
			.topK(this.topK)
			.repetitionPenalty(this.repetitionPenalty)
			.translationOptions(this.translationOptions)
			.disfluencyRemovalEnabled(this.disfluencyRemovalEnabled)
			.languageHints(this.languageHints)
			.semanticPunctuationEnabled(this.semanticPunctuationEnabled)
			.maxSentenceSilence(this.maxSentenceSilence)
			.multiThresholdModeEnabled(this.multiThresholdModeEnabled)
			.punctuationPredictionEnabled(this.punctuationPredictionEnabled)
			.heartbeat(this.heartbeat)
			.inverseTextNormalizationEnabled(this.inverseTextNormalizationEnabled)
			.resources(this.resources)
			.timestampAlignmentEnabled(this.timestampAlignmentEnabled)
			.specialWordFilter(this.specialWordFilter)
			.diarizationEnabled(this.diarizationEnabled)
			.speakerCount(this.speakerCount)
			.channelId(this.channelId)
			.asrOptions(this.asrOptions);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		DashScopeAudioTranscriptionOptions that = (DashScopeAudioTranscriptionOptions) o;
		return Objects.equals(this.model, that.model) && Objects.equals(this.vocabularyId, that.vocabularyId)
				&& Objects.equals(this.sampleRate, that.sampleRate) && Objects.equals(this.format, that.format)
				&& Objects.equals(this.sourceLanguage, that.sourceLanguage)
				&& Objects.equals(this.transcriptionEnabled, that.transcriptionEnabled)
				&& Objects.equals(this.translationEnabled, that.translationEnabled)
				&& Objects.equals(this.translationTargetLanguages, that.translationTargetLanguages)
				&& Objects.equals(this.maxEndSilence, that.maxEndSilence) && Objects.equals(this.modalities, that.modalities)
				&& Objects.equals(this.audio, that.audio) && Objects.equals(this.stream, that.stream)
				&& Objects.equals(this.streamOptions, that.streamOptions) && Objects.equals(this.maxTokens, that.maxTokens)
				&& Objects.equals(this.seed, that.seed) && Objects.equals(this.temperature, that.temperature)
				&& Objects.equals(this.topP, that.topP) && Objects.equals(this.presencePenalty, that.presencePenalty)
				&& Objects.equals(this.topK, that.topK) && Objects.equals(this.repetitionPenalty, that.repetitionPenalty)
				&& Objects.equals(this.translationOptions, that.translationOptions)
				&& Objects.equals(this.disfluencyRemovalEnabled, that.disfluencyRemovalEnabled)
				&& Objects.equals(this.languageHints, that.languageHints)
				&& Objects.equals(this.semanticPunctuationEnabled, that.semanticPunctuationEnabled)
				&& Objects.equals(this.maxSentenceSilence, that.maxSentenceSilence)
				&& Objects.equals(this.multiThresholdModeEnabled, that.multiThresholdModeEnabled)
				&& Objects.equals(this.punctuationPredictionEnabled, that.punctuationPredictionEnabled)
				&& Objects.equals(this.heartbeat, that.heartbeat)
				&& Objects.equals(this.inverseTextNormalizationEnabled, that.inverseTextNormalizationEnabled)
				&& Objects.equals(this.resources, that.resources)
				&& Objects.equals(this.timestampAlignmentEnabled, that.timestampAlignmentEnabled)
				&& Objects.equals(this.specialWordFilter, that.specialWordFilter)
				&& Objects.equals(this.diarizationEnabled, that.diarizationEnabled)
				&& Objects.equals(this.speakerCount, that.speakerCount)
				&& Objects.equals(this.channelId, that.channelId) && Objects.equals(this.asrOptions, that.asrOptions);
	}

    // @formatter:off
	@Override
	public int hashCode() {
		return Objects.hash(this.model, this.vocabularyId, this.sampleRate, this.format, this.sourceLanguage,
				this.transcriptionEnabled, this.translationEnabled, this.translationTargetLanguages,
				this.maxEndSilence, this.modalities, this.audio, this.stream, this.streamOptions, this.maxTokens,
				this.seed, this.temperature, this.topP, this.presencePenalty, this.topK, this.repetitionPenalty,
				this.translationOptions, this.disfluencyRemovalEnabled, this.languageHints,
				this.semanticPunctuationEnabled, this.maxSentenceSilence, this.multiThresholdModeEnabled,
				this.punctuationPredictionEnabled, this.heartbeat, this.inverseTextNormalizationEnabled,
				this.resources, this.timestampAlignmentEnabled, this.specialWordFilter, this.diarizationEnabled,
				this.speakerCount, this.channelId, this.asrOptions);
	}
    // @formatter:on

	@Override
	public String toString() {
		return "DashScopeAudioTranscriptionOptions{" + "model='" + this.model + '\'' + ", vocabularyId='"
				+ this.vocabularyId + '\'' + ", sampleRate=" + this.sampleRate + ", format='" + this.format + '\''
				+ ", sourceLanguage='" + this.sourceLanguage + '\'' + ", transcriptionEnabled="
				+ this.transcriptionEnabled + ", translationEnabled=" + this.translationEnabled
				+ ", translationTargetLanguages=" + this.translationTargetLanguages + ", maxEndSilence="
				+ this.maxEndSilence + ", modalities=" + this.modalities + ", audio=" + this.audio + ", stream="
				+ this.stream + ", streamOptions=" + this.streamOptions + ", maxTokens=" + this.maxTokens
				+ ", seed=" + this.seed + ", temperature=" + this.temperature + ", topP=" + this.topP
				+ ", presencePenalty=" + this.presencePenalty + ", topK=" + this.topK + ", repetitionPenalty="
				+ this.repetitionPenalty + ", translationOptions=" + this.translationOptions
				+ ", disfluencyRemovalEnabled=" + this.disfluencyRemovalEnabled + ", languageHints="
				+ this.languageHints + ", semanticPunctuationEnabled=" + this.semanticPunctuationEnabled
				+ ", maxSentenceSilence=" + this.maxSentenceSilence + ", multiThresholdModeEnabled="
				+ this.multiThresholdModeEnabled + ", punctuationPredictionEnabled="
				+ this.punctuationPredictionEnabled + ", heartbeat=" + this.heartbeat
				+ ", inverseTextNormalizationEnabled=" + this.inverseTextNormalizationEnabled + ", resources="
				+ this.resources + ", timestampAlignmentEnabled=" + this.timestampAlignmentEnabled
				+ ", specialWordFilter='" + this.specialWordFilter + '\'' + ", diarizationEnabled="
				+ this.diarizationEnabled + ", speakerCount=" + this.speakerCount + ", channelId=" + this.channelId
				+ ", asrOptions=" + this.asrOptions + '}';
	}

	public static final class Builder extends AbstractBuilder<DashScopeAudioTranscriptionOptions, Builder> {

		@Override
		protected Builder self() {
			return this;
		}

        // @formatter:off
		public DashScopeAudioTranscriptionOptions build() {
			return new DashScopeAudioTranscriptionOptions(this.model, this.vocabularyId, this.sampleRate, this.format,
					this.sourceLanguage, this.transcriptionEnabled, this.translationEnabled,
					this.translationTargetLanguages, this.maxEndSilence, this.modalities, this.audio, this.stream,
					this.streamOptions, this.maxTokens, this.seed, this.temperature, this.topP,
					this.presencePenalty, this.topK, this.repetitionPenalty, this.translationOptions,
					this.disfluencyRemovalEnabled, this.languageHints, this.semanticPunctuationEnabled,
					this.maxSentenceSilence, this.multiThresholdModeEnabled, this.punctuationPredictionEnabled,
					this.heartbeat, this.inverseTextNormalizationEnabled, this.resources,
					this.timestampAlignmentEnabled, this.specialWordFilter, this.diarizationEnabled,
					this.speakerCount, this.channelId, this.asrOptions);
		}
        // @formatter:on

	}

	protected abstract static class AbstractBuilder<O extends DashScopeAudioTranscriptionOptions, B extends AbstractBuilder<O, B>> {

		protected @Nullable String model;

		protected @Nullable String vocabularyId;

		protected @Nullable Integer sampleRate;

		protected @Nullable String format;

		protected @Nullable String sourceLanguage;

		protected @Nullable Boolean transcriptionEnabled;

		protected @Nullable Boolean translationEnabled;

		protected @Nullable List<String> translationTargetLanguages;

		protected @Nullable Integer maxEndSilence;

		protected @Nullable List<String> modalities;

		protected @Nullable Audio audio;

		protected @Nullable Boolean stream;

		protected @Nullable StreamOptions streamOptions;

		protected @Nullable Integer maxTokens;

		protected @Nullable Integer seed;

		protected @Nullable Float temperature;

		protected @Nullable Float topP;

		protected @Nullable Float presencePenalty;

		protected @Nullable Integer topK;

		protected @Nullable Float repetitionPenalty;

		protected @Nullable TranslationOptions translationOptions;

		protected @Nullable Boolean disfluencyRemovalEnabled;

		protected @Nullable List<String> languageHints;

		protected @Nullable Boolean semanticPunctuationEnabled;

		protected @Nullable Integer maxSentenceSilence;

		protected @Nullable Boolean multiThresholdModeEnabled;

		protected @Nullable Boolean punctuationPredictionEnabled;

		protected @Nullable Boolean heartbeat;

		protected @Nullable Boolean inverseTextNormalizationEnabled;

		protected @Nullable List<Resource> resources;

		protected @Nullable Boolean timestampAlignmentEnabled;

		protected @Nullable String specialWordFilter;

		protected @Nullable Boolean diarizationEnabled;

		protected @Nullable Integer speakerCount;

		protected @Nullable List<Integer> channelId;

		protected @Nullable AsrOptions asrOptions;

		protected AbstractBuilder() {
		}

		protected abstract B self();

		public B model(@Nullable String model) {
			this.model = model;
			return self();
		}

		public B vocabularyId(@Nullable String vocabularyId) {
			this.vocabularyId = vocabularyId;
			return self();
		}

		public B sampleRate(@Nullable Integer sampleRate) {
			this.sampleRate = sampleRate;
			return self();
		}

		public B format(@Nullable String format) {
			this.format = format;
			return self();
		}

		public B sourceLanguage(@Nullable String sourceLanguage) {
			this.sourceLanguage = sourceLanguage;
			return self();
		}

		public B transcriptionEnabled(@Nullable Boolean transcriptionEnabled) {
			this.transcriptionEnabled = transcriptionEnabled;
			return self();
		}

		public B translationEnabled(@Nullable Boolean translationEnabled) {
			this.translationEnabled = translationEnabled;
			return self();
		}

		public B translationTargetLanguages(@Nullable List<String> translationTargetLanguages) {
			this.translationTargetLanguages = translationTargetLanguages;
			return self();
		}

		public B maxEndSilence(@Nullable Integer maxEndSilence) {
			this.maxEndSilence = maxEndSilence;
			return self();
		}

		public B modalities(@Nullable List<String> modalities) {
			this.modalities = modalities;
			return self();
		}

		public B audio(@Nullable Audio audio) {
			this.audio = audio;
			return self();
		}

		public B stream(@Nullable Boolean stream) {
			this.stream = stream;
			return self();
		}

		public B streamOptions(@Nullable StreamOptions streamOptions) {
			this.streamOptions = streamOptions;
			return self();
		}

		public B maxTokens(@Nullable Integer maxTokens) {
			this.maxTokens = maxTokens;
			return self();
		}

		public B seed(@Nullable Integer seed) {
			this.seed = seed;
			return self();
		}

		public B temperature(@Nullable Float temperature) {
			this.temperature = temperature;
			return self();
		}

		public B topP(@Nullable Float topP) {
			this.topP = topP;
			return self();
		}

		public B presencePenalty(@Nullable Float presencePenalty) {
			this.presencePenalty = presencePenalty;
			return self();
		}

		public B topK(@Nullable Integer topK) {
			this.topK = topK;
			return self();
		}

		public B repetitionPenalty(@Nullable Float repetitionPenalty) {
			this.repetitionPenalty = repetitionPenalty;
			return self();
		}

		public B translationOptions(@Nullable TranslationOptions translationOptions) {
			this.translationOptions = translationOptions;
			return self();
		}

		public B disfluencyRemovalEnabled(@Nullable Boolean disfluencyRemovalEnabled) {
			this.disfluencyRemovalEnabled = disfluencyRemovalEnabled;
			return self();
		}

		public B languageHints(@Nullable List<String> languageHints) {
			this.languageHints = languageHints;
			return self();
		}

		public B semanticPunctuationEnabled(@Nullable Boolean semanticPunctuationEnabled) {
			this.semanticPunctuationEnabled = semanticPunctuationEnabled;
			return self();
		}

		public B maxSentenceSilence(@Nullable Integer maxSentenceSilence) {
			this.maxSentenceSilence = maxSentenceSilence;
			return self();
		}

		public B multiThresholdModeEnabled(@Nullable Boolean multiThresholdModeEnabled) {
			this.multiThresholdModeEnabled = multiThresholdModeEnabled;
			return self();
		}

		public B punctuationPredictionEnabled(@Nullable Boolean punctuationPredictionEnabled) {
			this.punctuationPredictionEnabled = punctuationPredictionEnabled;
			return self();
		}

		public B heartbeat(@Nullable Boolean heartbeat) {
			this.heartbeat = heartbeat;
			return self();
		}

		public B inverseTextNormalizationEnabled(@Nullable Boolean inverseTextNormalizationEnabled) {
			this.inverseTextNormalizationEnabled = inverseTextNormalizationEnabled;
			return self();
		}

		public B resources(@Nullable List<Resource> resources) {
			this.resources = resources;
			return self();
		}

		public B timestampAlignmentEnabled(@Nullable Boolean timestampAlignmentEnabled) {
			this.timestampAlignmentEnabled = timestampAlignmentEnabled;
			return self();
		}

		public B specialWordFilter(@Nullable String specialWordFilter) {
			this.specialWordFilter = specialWordFilter;
			return self();
		}

		public B diarizationEnabled(@Nullable Boolean diarizationEnabled) {
			this.diarizationEnabled = diarizationEnabled;
			return self();
		}

		public B speakerCount(@Nullable Integer speakerCount) {
			this.speakerCount = speakerCount;
			return self();
		}

		public B channelId(@Nullable List<Integer> channelId) {
			this.channelId = channelId;
			return self();
		}

		public B asrOptions(@Nullable AsrOptions asrOptions) {
			this.asrOptions = asrOptions;
			return self();
		}

		public B from(DashScopeAudioTranscriptionOptions fromOptions) {
			this.model = fromOptions.getModel();
			this.vocabularyId = fromOptions.getVocabularyId();
			this.sampleRate = fromOptions.getSampleRate();
			this.format = fromOptions.getFormat();
			this.sourceLanguage = fromOptions.getSourceLanguage();
			this.transcriptionEnabled = fromOptions.getTranscriptionEnabled();
			this.translationEnabled = fromOptions.getTranslationEnabled();
			this.translationTargetLanguages = fromOptions.getTranslationTargetLanguages();
			this.maxEndSilence = fromOptions.getMaxEndSilence();
			this.modalities = fromOptions.getModalities();
			this.audio = fromOptions.getAudio();
			this.stream = fromOptions.getStream();
			this.streamOptions = fromOptions.getStreamOptions();
			this.maxTokens = fromOptions.getMaxTokens();
			this.seed = fromOptions.getSeed();
			this.temperature = fromOptions.getTemperature();
			this.topP = fromOptions.getTopP();
			this.presencePenalty = fromOptions.getPresencePenalty();
			this.topK = fromOptions.getTopK();
			this.repetitionPenalty = fromOptions.getRepetitionPenalty();
			this.translationOptions = fromOptions.getTranslationOptions();
			this.disfluencyRemovalEnabled = fromOptions.getDisfluencyRemovalEnabled();
			this.languageHints = fromOptions.getLanguageHints();
			this.semanticPunctuationEnabled = fromOptions.getSemanticPunctuationEnabled();
			this.maxSentenceSilence = fromOptions.getMaxSentenceSilence();
			this.multiThresholdModeEnabled = fromOptions.getMultiThresholdModeEnabled();
			this.punctuationPredictionEnabled = fromOptions.getPunctuationPredictionEnabled();
			this.heartbeat = fromOptions.getHeartbeat();
			this.inverseTextNormalizationEnabled = fromOptions.getInverseTextNormalizationEnabled();
			this.resources = fromOptions.getResources();
			this.timestampAlignmentEnabled = fromOptions.getTimestampAlignmentEnabled();
			this.specialWordFilter = fromOptions.getSpecialWordFilter();
			this.diarizationEnabled = fromOptions.getDiarizationEnabled();
			this.speakerCount = fromOptions.getSpeakerCount();
			this.channelId = fromOptions.getChannelId();
			this.asrOptions = fromOptions.getAsrOptions();
			return self();
		}

		public B merge(@Nullable AudioTranscriptionOptions from) {
			if (from == null) {
				return self();
			}
			this.model = from.getModel();
			if (from instanceof DashScopeAudioTranscriptionOptions castFrom) {
				if (castFrom.getVocabularyId() != null) {
					this.vocabularyId = castFrom.getVocabularyId();
				}
				if (castFrom.getSampleRate() != null) {
					this.sampleRate = castFrom.getSampleRate();
				}
				if (castFrom.getFormat() != null) {
					this.format = castFrom.getFormat();
				}
				if (castFrom.getSourceLanguage() != null) {
					this.sourceLanguage = castFrom.getSourceLanguage();
				}
				if (castFrom.getTranscriptionEnabled() != null) {
					this.transcriptionEnabled = castFrom.getTranscriptionEnabled();
				}
				if (castFrom.getTranslationEnabled() != null) {
					this.translationEnabled = castFrom.getTranslationEnabled();
				}
				if (castFrom.getTranslationTargetLanguages() != null) {
                    if (this.translationTargetLanguages == null) {
                        this.translationTargetLanguages = new ArrayList<>(castFrom.getTranslationTargetLanguages());
                    }
                    else {
                        List<String> merged = new ArrayList<>(this.translationTargetLanguages);
                        merged.addAll(castFrom.getTranslationTargetLanguages());
                        this.translationTargetLanguages = merged;
                    }
				}
				if (castFrom.getMaxEndSilence() != null) {
					this.maxEndSilence = castFrom.getMaxEndSilence();
				}
				if (castFrom.getModalities() != null) {
                    if (this.modalities == null) {
                        this.modalities = new ArrayList<>(castFrom.getModalities());
                    }
                    else {
                        List<String> merged = new ArrayList<>(this.modalities);
                        merged.addAll(castFrom.getModalities());
                        this.modalities = merged;
                    }
				}
				if (castFrom.getAudio() != null) {
					this.audio = castFrom.getAudio();
				}
				if (castFrom.getStream() != null) {
					this.stream = castFrom.getStream();
				}
				if (castFrom.getStreamOptions() != null) {
					this.streamOptions = castFrom.getStreamOptions();
				}
				if (castFrom.getMaxTokens() != null) {
					this.maxTokens = castFrom.getMaxTokens();
				}
				if (castFrom.getSeed() != null) {
					this.seed = castFrom.getSeed();
				}
				if (castFrom.getTemperature() != null) {
					this.temperature = castFrom.getTemperature();
				}
				if (castFrom.getTopP() != null) {
					this.topP = castFrom.getTopP();
				}
				if (castFrom.getPresencePenalty() != null) {
					this.presencePenalty = castFrom.getPresencePenalty();
				}
				if (castFrom.getTopK() != null) {
					this.topK = castFrom.getTopK();
				}
				if (castFrom.getRepetitionPenalty() != null) {
					this.repetitionPenalty = castFrom.getRepetitionPenalty();
				}
				if (castFrom.getTranslationOptions() != null) {
					this.translationOptions = castFrom.getTranslationOptions();
				}
				if (castFrom.getDisfluencyRemovalEnabled() != null) {
					this.disfluencyRemovalEnabled = castFrom.getDisfluencyRemovalEnabled();
				}
				if (castFrom.getLanguageHints() != null) {
                    if (this.languageHints == null) {
                        this.languageHints = new ArrayList<>(castFrom.getLanguageHints());
                    }
                    else {
                        List<String> merged = new ArrayList<>(this.languageHints);
                        merged.addAll(castFrom.getLanguageHints());
                        this.languageHints = merged;
                    }
				}
				if (castFrom.getSemanticPunctuationEnabled() != null) {
					this.semanticPunctuationEnabled = castFrom.getSemanticPunctuationEnabled();
				}
				if (castFrom.getMaxSentenceSilence() != null) {
					this.maxSentenceSilence = castFrom.getMaxSentenceSilence();
				}
				if (castFrom.getMultiThresholdModeEnabled() != null) {
					this.multiThresholdModeEnabled = castFrom.getMultiThresholdModeEnabled();
				}
				if (castFrom.getPunctuationPredictionEnabled() != null) {
					this.punctuationPredictionEnabled = castFrom.getPunctuationPredictionEnabled();
				}
				if (castFrom.getHeartbeat() != null) {
					this.heartbeat = castFrom.getHeartbeat();
				}
				if (castFrom.getInverseTextNormalizationEnabled() != null) {
					this.inverseTextNormalizationEnabled = castFrom.getInverseTextNormalizationEnabled();
				}
				if (castFrom.getResources() != null) {
                    if (this.resources == null) {
                        this.resources = new ArrayList<>(castFrom.getResources());
                    }
                    else {
                        List<Resource> merged = new ArrayList<>(this.resources);
                        merged.addAll(castFrom.getResources());
                        this.resources = merged;
                    }
				}
				if (castFrom.getTimestampAlignmentEnabled() != null) {
					this.timestampAlignmentEnabled = castFrom.getTimestampAlignmentEnabled();
				}
				if (castFrom.getSpecialWordFilter() != null) {
					this.specialWordFilter = castFrom.getSpecialWordFilter();
				}
				if (castFrom.getDiarizationEnabled() != null) {
					this.diarizationEnabled = castFrom.getDiarizationEnabled();
				}
				if (castFrom.getSpeakerCount() != null) {
					this.speakerCount = castFrom.getSpeakerCount();
				}
				if (castFrom.getChannelId() != null) {
                    if (this.channelId == null) {
                        this.channelId = new ArrayList<>(castFrom.getChannelId());
                    }
                    else {
                        List<Integer> merged = new ArrayList<>(this.channelId);
                        merged.addAll(castFrom.getChannelId());
                        this.channelId = merged;
                    }
				}
				if (castFrom.getAsrOptions() != null) {
					this.asrOptions = castFrom.getAsrOptions();
				}
			}
			return self();
		}

	}

	public static class Audio {

		@JsonProperty("voice")
		private final @Nullable String voice;

		@JsonProperty("format")
		private final @Nullable String format;

		public Audio(@Nullable String voice, @Nullable String format) {
			this.voice = voice;
			this.format = format;
		}

		public @Nullable String getVoice() {
			return this.voice;
		}

		public @Nullable String getFormat() {
			return this.format;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (o == null || getClass() != o.getClass()) {
				return false;
			}
			Audio that = (Audio) o;
			return Objects.equals(this.voice, that.voice) && Objects.equals(this.format, that.format);
		}

		@Override
		public int hashCode() {
			return Objects.hash(this.voice, this.format);
		}
	}

	public static class StreamOptions {

		@JsonProperty("include_usage")
		private final @Nullable Boolean includeUsage;

		public StreamOptions(@Nullable Boolean includeUsage) {
			this.includeUsage = includeUsage;
		}

		public @Nullable Boolean getIncludeUsage() {
			return this.includeUsage;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (o == null || getClass() != o.getClass()) {
				return false;
			}
			StreamOptions that = (StreamOptions) o;
			return Objects.equals(this.includeUsage, that.includeUsage);
		}

		@Override
		public int hashCode() {
			return Objects.hash(this.includeUsage);
		}
	}

	public static class TranslationOptions {

		@JsonProperty("source_lang")
		private final @Nullable String sourceLang;

		@JsonProperty("target_lang")
		private final @Nullable String targetLang;

		public TranslationOptions(@Nullable String sourceLang, @Nullable String targetLang) {
			this.sourceLang = sourceLang;
			this.targetLang = targetLang;
		}

		public @Nullable String getSourceLang() {
			return this.sourceLang;
		}

		public @Nullable String getTargetLang() {
			return this.targetLang;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (o == null || getClass() != o.getClass()) {
				return false;
			}
			TranslationOptions that = (TranslationOptions) o;
			return Objects.equals(this.sourceLang, that.sourceLang) && Objects.equals(this.targetLang, that.targetLang);
		}

		@Override
		public int hashCode() {
			return Objects.hash(this.sourceLang, this.targetLang);
		}
	}

	public static class Resource {

		@JsonProperty("resource_id")
		private final @Nullable String resourceId;

		@JsonProperty("resource_type")
		private final @Nullable String resourceType;

		public Resource(@Nullable String resourceId, @Nullable String resourceType) {
			this.resourceId = resourceId;
			this.resourceType = resourceType;
		}

		public @Nullable String getResourceId() {
			return this.resourceId;
		}

		public @Nullable String getResourceType() {
			return this.resourceType;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (o == null || getClass() != o.getClass()) {
				return false;
			}
			Resource that = (Resource) o;
			return Objects.equals(this.resourceId, that.resourceId) && Objects.equals(this.resourceType, that.resourceType);
		}

		@Override
		public int hashCode() {
			return Objects.hash(this.resourceId, this.resourceType);
		}
	}

	public static class AsrOptions {

		@JsonProperty("language")
		private final @Nullable String language;

		@JsonProperty("enable_itn")
		private final @Nullable Boolean enableItn;

		public AsrOptions(@Nullable String language, @Nullable Boolean enableItn) {
			this.language = language;
			this.enableItn = enableItn;
		}

		public @Nullable String getLanguage() {
			return this.language;
		}

		public @Nullable Boolean getEnableItn() {
			return this.enableItn;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (o == null || getClass() != o.getClass()) {
				return false;
			}
			AsrOptions that = (AsrOptions) o;
			return Objects.equals(this.language, that.language) && Objects.equals(this.enableItn, that.enableItn);
		}

		@Override
		public int hashCode() {
			return Objects.hash(this.language, this.enableItn);
		}
	}

}
