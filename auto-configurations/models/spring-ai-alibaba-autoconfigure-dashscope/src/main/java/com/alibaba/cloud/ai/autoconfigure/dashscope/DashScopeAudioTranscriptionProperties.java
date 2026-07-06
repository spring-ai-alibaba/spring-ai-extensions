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
package com.alibaba.cloud.ai.autoconfigure.dashscope;

import java.util.List;

import com.alibaba.cloud.ai.dashscope.audio.transcription.DashScopeAudioTranscriptionOptions;
import com.alibaba.cloud.ai.dashscope.common.DashScopeAudioApiConstants;
import org.jspecify.annotations.Nullable;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.DeprecatedConfigurationProperty;

/**
 * @author xYLiu、yuluo、kevinlin09、yingzi
 */

@ConfigurationProperties(DashScopeAudioTranscriptionProperties.CONFIG_PREFIX)
public class DashScopeAudioTranscriptionProperties extends DashScopeParentProperties {

	/**
	 * Spring AI Alibaba configuration prefix.
	 */
	public static final String CONFIG_PREFIX = "spring.ai.dashscope.audio.transcription";

    private String websocketUrl = DashScopeAudioApiConstants.DEFAULT_WEBSOCKET_URL;
	private DashScopeAudioTranscriptionOptions options = DashScopeAudioTranscriptionOptions.builder().build();
	private final Options legacyOptions = new Options();

	public DashScopeAudioTranscriptionOptions toOptions() {
		if (this.options == null) {
			this.options = DashScopeAudioTranscriptionOptions.builder().build();
		}
		return this.options;
	}

	@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX)
	@Deprecated(since = "2.0.0", forRemoval = true)
	public Options getOptions() {
		return this.legacyOptions;
	}

	public void setOptions(Options options) {
		// Deprecated options are applied by the nested Options setters.
	}

	private void updateOptions(java.util.function.Consumer<DashScopeAudioTranscriptionOptions.Builder> customizer) {
		DashScopeAudioTranscriptionOptions.Builder builder = DashScopeAudioTranscriptionOptions.builder().from(toOptions());
		customizer.accept(builder);
		this.options = builder.build();
	}

	public @Nullable String getModel() {
		return toOptions().getModel();
	}

	public void setModel(String model) {
		updateOptions(builder -> builder.model(model));
	}

	public @Nullable String getVocabularyId() {
		return toOptions().getVocabularyId();
	}

	public void setVocabularyId(String vocabularyId) {
		updateOptions(builder -> builder.vocabularyId(vocabularyId));
	}

	public @Nullable String getFormat() {
		return toOptions().getFormat();
	}

	public void setFormat(String format) {
		updateOptions(builder -> builder.format(format));
	}

	public @Nullable Integer getSampleRate() {
		return toOptions().getSampleRate();
	}

	public void setSampleRate(Integer sampleRate) {
		updateOptions(builder -> builder.sampleRate(sampleRate));
	}

	public @Nullable String getSourceLanguage() {
		return toOptions().getSourceLanguage();
	}

	public void setSourceLanguage(String sourceLanguage) {
		updateOptions(builder -> builder.sourceLanguage(sourceLanguage));
	}

	public @Nullable Boolean getTranscriptionEnabled() {
		return toOptions().getTranscriptionEnabled();
	}

	public void setTranscriptionEnabled(Boolean transcriptionEnabled) {
		updateOptions(builder -> builder.transcriptionEnabled(transcriptionEnabled));
	}

	public @Nullable Boolean getTranslationEnabled() {
		return toOptions().getTranslationEnabled();
	}

	public void setTranslationEnabled(Boolean translationEnabled) {
		updateOptions(builder -> builder.translationEnabled(translationEnabled));
	}

	public @Nullable List<String> getTranslationTargetLanguages() {
		return toOptions().getTranslationTargetLanguages();
	}

	public void setTranslationTargetLanguages(List<String> translationTargetLanguages) {
		updateOptions(builder -> builder.translationTargetLanguages(translationTargetLanguages));
	}

	public DashScopeAudioTranscriptionOptions.@Nullable AsrOptions getAsrOptions() {
		return toOptions().getAsrOptions();
	}

	public void setAsrOptions(DashScopeAudioTranscriptionOptions.AsrOptions asrOptions) {
		updateOptions(builder -> builder.asrOptions(asrOptions));
	}

	public @Nullable Integer getMaxEndSilence() {
		return toOptions().getMaxEndSilence();
	}

	public void setMaxEndSilence(Integer maxEndSilence) {
		updateOptions(builder -> builder.maxEndSilence(maxEndSilence));
	}

	public @Nullable List<String> getModalities() {
		return toOptions().getModalities();
	}

	public void setModalities(List<String> modalities) {
		updateOptions(builder -> builder.modalities(modalities));
	}

	public DashScopeAudioTranscriptionOptions.@Nullable Audio getAudio() {
		return toOptions().getAudio();
	}

	public void setAudio(DashScopeAudioTranscriptionOptions.Audio audio) {
		updateOptions(builder -> builder.audio(audio));
	}

	public @Nullable Boolean getStream() {
		return toOptions().getStream();
	}

	public void setStream(Boolean stream) {
		updateOptions(builder -> builder.stream(stream));
	}

	public DashScopeAudioTranscriptionOptions.@Nullable StreamOptions getStreamOptions() {
		return toOptions().getStreamOptions();
	}

	public void setStreamOptions(DashScopeAudioTranscriptionOptions.StreamOptions streamOptions) {
		updateOptions(builder -> builder.streamOptions(streamOptions));
	}

	public @Nullable Integer getMaxTokens() {
		return toOptions().getMaxTokens();
	}

	public void setMaxTokens(Integer maxTokens) {
		updateOptions(builder -> builder.maxTokens(maxTokens));
	}

	public @Nullable Integer getSeed() {
		return toOptions().getSeed();
	}

	public void setSeed(Integer seed) {
		updateOptions(builder -> builder.seed(seed));
	}

	public @Nullable Float getTemperature() {
		return toOptions().getTemperature();
	}

	public void setTemperature(Float temperature) {
		updateOptions(builder -> builder.temperature(temperature));
	}

	public @Nullable Float getTopP() {
		return toOptions().getTopP();
	}

	public void setTopP(Float topP) {
		updateOptions(builder -> builder.topP(topP));
	}

	public @Nullable Float getPresencePenalty() {
		return toOptions().getPresencePenalty();
	}

	public void setPresencePenalty(Float presencePenalty) {
		updateOptions(builder -> builder.presencePenalty(presencePenalty));
	}

	public @Nullable Integer getTopK() {
		return toOptions().getTopK();
	}

	public void setTopK(Integer topK) {
		updateOptions(builder -> builder.topK(topK));
	}

	public @Nullable Float getRepetitionPenalty() {
		return toOptions().getRepetitionPenalty();
	}

	public void setRepetitionPenalty(Float repetitionPenalty) {
		updateOptions(builder -> builder.repetitionPenalty(repetitionPenalty));
	}

	public DashScopeAudioTranscriptionOptions.@Nullable TranslationOptions getTranslationOptions() {
		return toOptions().getTranslationOptions();
	}

	public void setTranslationOptions(DashScopeAudioTranscriptionOptions.TranslationOptions translationOptions) {
		updateOptions(builder -> builder.translationOptions(translationOptions));
	}

	public @Nullable Boolean getDisfluencyRemovalEnabled() {
		return toOptions().getDisfluencyRemovalEnabled();
	}

	public void setDisfluencyRemovalEnabled(Boolean disfluencyRemovalEnabled) {
		updateOptions(builder -> builder.disfluencyRemovalEnabled(disfluencyRemovalEnabled));
	}

	public @Nullable List<String> getLanguageHints() {
		return toOptions().getLanguageHints();
	}

	public void setLanguageHints(List<String> languageHints) {
		updateOptions(builder -> builder.languageHints(languageHints));
	}

	public @Nullable Boolean getSemanticPunctuationEnabled() {
		return toOptions().getSemanticPunctuationEnabled();
	}

	public void setSemanticPunctuationEnabled(Boolean semanticPunctuationEnabled) {
		updateOptions(builder -> builder.semanticPunctuationEnabled(semanticPunctuationEnabled));
	}

	public @Nullable Integer getMaxSentenceSilence() {
		return toOptions().getMaxSentenceSilence();
	}

	public void setMaxSentenceSilence(Integer maxSentenceSilence) {
		updateOptions(builder -> builder.maxSentenceSilence(maxSentenceSilence));
	}

	public @Nullable Boolean getMultiThresholdModeEnabled() {
		return toOptions().getMultiThresholdModeEnabled();
	}

	public void setMultiThresholdModeEnabled(Boolean multiThresholdModeEnabled) {
		updateOptions(builder -> builder.multiThresholdModeEnabled(multiThresholdModeEnabled));
	}

	public @Nullable Boolean getPunctuationPredictionEnabled() {
		return toOptions().getPunctuationPredictionEnabled();
	}

	public void setPunctuationPredictionEnabled(Boolean punctuationPredictionEnabled) {
		updateOptions(builder -> builder.punctuationPredictionEnabled(punctuationPredictionEnabled));
	}

	public @Nullable Boolean getHeartbeat() {
		return toOptions().getHeartbeat();
	}

	public void setHeartbeat(Boolean heartbeat) {
		updateOptions(builder -> builder.heartbeat(heartbeat));
	}

	public @Nullable Boolean getInverseTextNormalizationEnabled() {
		return toOptions().getInverseTextNormalizationEnabled();
	}

	public void setInverseTextNormalizationEnabled(Boolean inverseTextNormalizationEnabled) {
		updateOptions(builder -> builder.inverseTextNormalizationEnabled(inverseTextNormalizationEnabled));
	}

	public @Nullable List<DashScopeAudioTranscriptionOptions.Resource> getResources() {
		return toOptions().getResources();
	}

	public void setResources(List<DashScopeAudioTranscriptionOptions.Resource> resources) {
		updateOptions(builder -> builder.resources(resources));
	}

	public @Nullable Boolean getTimestampAlignmentEnabled() {
		return toOptions().getTimestampAlignmentEnabled();
	}

	public void setTimestampAlignmentEnabled(Boolean timestampAlignmentEnabled) {
		updateOptions(builder -> builder.timestampAlignmentEnabled(timestampAlignmentEnabled));
	}

	public @Nullable String getSpecialWordFilter() {
		return toOptions().getSpecialWordFilter();
	}

	public void setSpecialWordFilter(String specialWordFilter) {
		updateOptions(builder -> builder.specialWordFilter(specialWordFilter));
	}

	public @Nullable Boolean getDiarizationEnabled() {
		return toOptions().getDiarizationEnabled();
	}

	public void setDiarizationEnabled(Boolean diarizationEnabled) {
		updateOptions(builder -> builder.diarizationEnabled(diarizationEnabled));
	}

	public @Nullable Integer getSpeakerCount() {
		return toOptions().getSpeakerCount();
	}

	public void setSpeakerCount(Integer speakerCount) {
		updateOptions(builder -> builder.speakerCount(speakerCount));
	}

	public @Nullable List<Integer> getChannelId() {
		return toOptions().getChannelId();
	}

	public void setChannelId(List<Integer> channelId) {
		updateOptions(builder -> builder.channelId(channelId));
	}

    public String getWebsocketUrl() {
        return websocketUrl;
    }

    public void setWebsocketUrl(String websocketUrl) {
        this.websocketUrl = websocketUrl;
    }
	public class Options {

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".model")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable String getModel() {
			return DashScopeAudioTranscriptionProperties.this.getModel();
		}

		public void setModel(String model) {
			DashScopeAudioTranscriptionProperties.this.setModel(model);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".vocabulary-id")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable String getVocabularyId() {
			return DashScopeAudioTranscriptionProperties.this.getVocabularyId();
		}

		public void setVocabularyId(String vocabularyId) {
			DashScopeAudioTranscriptionProperties.this.setVocabularyId(vocabularyId);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".format")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable String getFormat() {
			return DashScopeAudioTranscriptionProperties.this.getFormat();
		}

		public void setFormat(String format) {
			DashScopeAudioTranscriptionProperties.this.setFormat(format);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".sample-rate")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Integer getSampleRate() {
			return DashScopeAudioTranscriptionProperties.this.getSampleRate();
		}

		public void setSampleRate(Integer sampleRate) {
			DashScopeAudioTranscriptionProperties.this.setSampleRate(sampleRate);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".source-language")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable String getSourceLanguage() {
			return DashScopeAudioTranscriptionProperties.this.getSourceLanguage();
		}

		public void setSourceLanguage(String sourceLanguage) {
			DashScopeAudioTranscriptionProperties.this.setSourceLanguage(sourceLanguage);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".transcription-enabled")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Boolean getTranscriptionEnabled() {
			return DashScopeAudioTranscriptionProperties.this.getTranscriptionEnabled();
		}

		public void setTranscriptionEnabled(Boolean transcriptionEnabled) {
			DashScopeAudioTranscriptionProperties.this.setTranscriptionEnabled(transcriptionEnabled);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".translation-enabled")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Boolean getTranslationEnabled() {
			return DashScopeAudioTranscriptionProperties.this.getTranslationEnabled();
		}

		public void setTranslationEnabled(Boolean translationEnabled) {
			DashScopeAudioTranscriptionProperties.this.setTranslationEnabled(translationEnabled);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".translation-target-languages")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable List<String> getTranslationTargetLanguages() {
			return DashScopeAudioTranscriptionProperties.this.getTranslationTargetLanguages();
		}

		public void setTranslationTargetLanguages(List<String> translationTargetLanguages) {
			DashScopeAudioTranscriptionProperties.this.setTranslationTargetLanguages(translationTargetLanguages);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".asr-options")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public DashScopeAudioTranscriptionOptions.@Nullable AsrOptions getAsrOptions() {
			return DashScopeAudioTranscriptionProperties.this.getAsrOptions();
		}

		public void setAsrOptions(DashScopeAudioTranscriptionOptions.AsrOptions asrOptions) {
			DashScopeAudioTranscriptionProperties.this.setAsrOptions(asrOptions);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".max-end-silence")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Integer getMaxEndSilence() {
			return DashScopeAudioTranscriptionProperties.this.getMaxEndSilence();
		}

		public void setMaxEndSilence(Integer maxEndSilence) {
			DashScopeAudioTranscriptionProperties.this.setMaxEndSilence(maxEndSilence);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".modalities")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable List<String> getModalities() {
			return DashScopeAudioTranscriptionProperties.this.getModalities();
		}

		public void setModalities(List<String> modalities) {
			DashScopeAudioTranscriptionProperties.this.setModalities(modalities);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".audio")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public DashScopeAudioTranscriptionOptions.@Nullable Audio getAudio() {
			return DashScopeAudioTranscriptionProperties.this.getAudio();
		}

		public void setAudio(DashScopeAudioTranscriptionOptions.Audio audio) {
			DashScopeAudioTranscriptionProperties.this.setAudio(audio);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".stream")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Boolean getStream() {
			return DashScopeAudioTranscriptionProperties.this.getStream();
		}

		public void setStream(Boolean stream) {
			DashScopeAudioTranscriptionProperties.this.setStream(stream);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".stream-options")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public DashScopeAudioTranscriptionOptions.@Nullable StreamOptions getStreamOptions() {
			return DashScopeAudioTranscriptionProperties.this.getStreamOptions();
		}

		public void setStreamOptions(DashScopeAudioTranscriptionOptions.StreamOptions streamOptions) {
			DashScopeAudioTranscriptionProperties.this.setStreamOptions(streamOptions);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".max-tokens")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Integer getMaxTokens() {
			return DashScopeAudioTranscriptionProperties.this.getMaxTokens();
		}

		public void setMaxTokens(Integer maxTokens) {
			DashScopeAudioTranscriptionProperties.this.setMaxTokens(maxTokens);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".seed")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Integer getSeed() {
			return DashScopeAudioTranscriptionProperties.this.getSeed();
		}

		public void setSeed(Integer seed) {
			DashScopeAudioTranscriptionProperties.this.setSeed(seed);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".temperature")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Float getTemperature() {
			return DashScopeAudioTranscriptionProperties.this.getTemperature();
		}

		public void setTemperature(Float temperature) {
			DashScopeAudioTranscriptionProperties.this.setTemperature(temperature);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".top-p")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Float getTopP() {
			return DashScopeAudioTranscriptionProperties.this.getTopP();
		}

		public void setTopP(Float topP) {
			DashScopeAudioTranscriptionProperties.this.setTopP(topP);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".presence-penalty")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Float getPresencePenalty() {
			return DashScopeAudioTranscriptionProperties.this.getPresencePenalty();
		}

		public void setPresencePenalty(Float presencePenalty) {
			DashScopeAudioTranscriptionProperties.this.setPresencePenalty(presencePenalty);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".top-k")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Integer getTopK() {
			return DashScopeAudioTranscriptionProperties.this.getTopK();
		}

		public void setTopK(Integer topK) {
			DashScopeAudioTranscriptionProperties.this.setTopK(topK);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".repetition-penalty")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Float getRepetitionPenalty() {
			return DashScopeAudioTranscriptionProperties.this.getRepetitionPenalty();
		}

		public void setRepetitionPenalty(Float repetitionPenalty) {
			DashScopeAudioTranscriptionProperties.this.setRepetitionPenalty(repetitionPenalty);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".translation-options")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public DashScopeAudioTranscriptionOptions.@Nullable TranslationOptions getTranslationOptions() {
			return DashScopeAudioTranscriptionProperties.this.getTranslationOptions();
		}

		public void setTranslationOptions(DashScopeAudioTranscriptionOptions.TranslationOptions translationOptions) {
			DashScopeAudioTranscriptionProperties.this.setTranslationOptions(translationOptions);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".disfluency-removal-enabled")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Boolean getDisfluencyRemovalEnabled() {
			return DashScopeAudioTranscriptionProperties.this.getDisfluencyRemovalEnabled();
		}

		public void setDisfluencyRemovalEnabled(Boolean disfluencyRemovalEnabled) {
			DashScopeAudioTranscriptionProperties.this.setDisfluencyRemovalEnabled(disfluencyRemovalEnabled);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".language-hints")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable List<String> getLanguageHints() {
			return DashScopeAudioTranscriptionProperties.this.getLanguageHints();
		}

		public void setLanguageHints(List<String> languageHints) {
			DashScopeAudioTranscriptionProperties.this.setLanguageHints(languageHints);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".semantic-punctuation-enabled")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Boolean getSemanticPunctuationEnabled() {
			return DashScopeAudioTranscriptionProperties.this.getSemanticPunctuationEnabled();
		}

		public void setSemanticPunctuationEnabled(Boolean semanticPunctuationEnabled) {
			DashScopeAudioTranscriptionProperties.this.setSemanticPunctuationEnabled(semanticPunctuationEnabled);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".max-sentence-silence")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Integer getMaxSentenceSilence() {
			return DashScopeAudioTranscriptionProperties.this.getMaxSentenceSilence();
		}

		public void setMaxSentenceSilence(Integer maxSentenceSilence) {
			DashScopeAudioTranscriptionProperties.this.setMaxSentenceSilence(maxSentenceSilence);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".multi-threshold-mode-enabled")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Boolean getMultiThresholdModeEnabled() {
			return DashScopeAudioTranscriptionProperties.this.getMultiThresholdModeEnabled();
		}

		public void setMultiThresholdModeEnabled(Boolean multiThresholdModeEnabled) {
			DashScopeAudioTranscriptionProperties.this.setMultiThresholdModeEnabled(multiThresholdModeEnabled);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".punctuation-prediction-enabled")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Boolean getPunctuationPredictionEnabled() {
			return DashScopeAudioTranscriptionProperties.this.getPunctuationPredictionEnabled();
		}

		public void setPunctuationPredictionEnabled(Boolean punctuationPredictionEnabled) {
			DashScopeAudioTranscriptionProperties.this.setPunctuationPredictionEnabled(punctuationPredictionEnabled);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".heartbeat")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Boolean getHeartbeat() {
			return DashScopeAudioTranscriptionProperties.this.getHeartbeat();
		}

		public void setHeartbeat(Boolean heartbeat) {
			DashScopeAudioTranscriptionProperties.this.setHeartbeat(heartbeat);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".inverse-text-normalization-enabled")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Boolean getInverseTextNormalizationEnabled() {
			return DashScopeAudioTranscriptionProperties.this.getInverseTextNormalizationEnabled();
		}

		public void setInverseTextNormalizationEnabled(Boolean inverseTextNormalizationEnabled) {
			DashScopeAudioTranscriptionProperties.this.setInverseTextNormalizationEnabled(inverseTextNormalizationEnabled);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".resources")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable List<DashScopeAudioTranscriptionOptions.Resource> getResources() {
			return DashScopeAudioTranscriptionProperties.this.getResources();
		}

		public void setResources(List<DashScopeAudioTranscriptionOptions.Resource> resources) {
			DashScopeAudioTranscriptionProperties.this.setResources(resources);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".timestamp-alignment-enabled")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Boolean getTimestampAlignmentEnabled() {
			return DashScopeAudioTranscriptionProperties.this.getTimestampAlignmentEnabled();
		}

		public void setTimestampAlignmentEnabled(Boolean timestampAlignmentEnabled) {
			DashScopeAudioTranscriptionProperties.this.setTimestampAlignmentEnabled(timestampAlignmentEnabled);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".special-word-filter")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable String getSpecialWordFilter() {
			return DashScopeAudioTranscriptionProperties.this.getSpecialWordFilter();
		}

		public void setSpecialWordFilter(String specialWordFilter) {
			DashScopeAudioTranscriptionProperties.this.setSpecialWordFilter(specialWordFilter);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".diarization-enabled")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Boolean getDiarizationEnabled() {
			return DashScopeAudioTranscriptionProperties.this.getDiarizationEnabled();
		}

		public void setDiarizationEnabled(Boolean diarizationEnabled) {
			DashScopeAudioTranscriptionProperties.this.setDiarizationEnabled(diarizationEnabled);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".speaker-count")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Integer getSpeakerCount() {
			return DashScopeAudioTranscriptionProperties.this.getSpeakerCount();
		}

		public void setSpeakerCount(Integer speakerCount) {
			DashScopeAudioTranscriptionProperties.this.setSpeakerCount(speakerCount);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".channel-id")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable List<Integer> getChannelId() {
			return DashScopeAudioTranscriptionProperties.this.getChannelId();
		}

		public void setChannelId(List<Integer> channelId) {
			DashScopeAudioTranscriptionProperties.this.setChannelId(channelId);
		}

	}


}
