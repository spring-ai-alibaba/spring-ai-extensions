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
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.DeprecatedConfigurationProperty;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

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

	@NestedConfigurationProperty
	private DashScopeAudioTranscriptionOptions options = DashScopeAudioTranscriptionOptions.builder().build();

	public DashScopeAudioTranscriptionOptions toOptions() {
		return this.options;
	}

	@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX)
	@Deprecated(since = "2.0.0", forRemoval = true)
	public DashScopeAudioTranscriptionOptions getOptions() {
		return this.options;
	}

	public void setOptions(DashScopeAudioTranscriptionOptions options) {
		this.options = options;
	}

	public String getModel() {
		return this.options.getModel();
	}

	public void setModel(String model) {
		this.options.setModel(model);
	}

	public String getVocabularyId() {
		return this.options.getVocabularyId();
	}

	public void setVocabularyId(String vocabularyId) {
		this.options.setVocabularyId(vocabularyId);
	}

	public String getFormat() {
		return this.options.getFormat();
	}

	public void setFormat(String format) {
		this.options.setFormat(format);
	}

	public Integer getSampleRate() {
		return this.options.getSampleRate();
	}

	public void setSampleRate(Integer sampleRate) {
		this.options.setSampleRate(sampleRate);
	}

	public String getSourceLanguage() {
		return this.options.getSourceLanguage();
	}

	public void setSourceLanguage(String sourceLanguage) {
		this.options.setSourceLanguage(sourceLanguage);
	}

	public Boolean getTranscriptionEnabled() {
		return this.options.getTranscriptionEnabled();
	}

	public void setTranscriptionEnabled(Boolean transcriptionEnabled) {
		this.options.setTranscriptionEnabled(transcriptionEnabled);
	}

	public Boolean getTranslationEnabled() {
		return this.options.getTranslationEnabled();
	}

	public void setTranslationEnabled(Boolean translationEnabled) {
		this.options.setTranslationEnabled(translationEnabled);
	}

	public List<String> getTranslationTargetLanguages() {
		return this.options.getTranslationTargetLanguages();
	}

	public void setTranslationTargetLanguages(List<String> translationTargetLanguages) {
		this.options.setTranslationTargetLanguages(translationTargetLanguages);
	}

	public DashScopeAudioTranscriptionOptions.AsrOptions getAsrOptions() {
		return this.options.getAsrOptions();
	}

	public void setAsrOptions(DashScopeAudioTranscriptionOptions.AsrOptions asrOptions) {
		this.options.setAsrOptions(asrOptions);
	}

	public Integer getMaxEndSilence() {
		return this.options.getMaxEndSilence();
	}

	public void setMaxEndSilence(Integer maxEndSilence) {
		this.options.setMaxEndSilence(maxEndSilence);
	}

	public List<String> getModalities() {
		return this.options.getModalities();
	}

	public void setModalities(List<String> modalities) {
		this.options.setModalities(modalities);
	}

	public DashScopeAudioTranscriptionOptions.Audio getAudio() {
		return this.options.getAudio();
	}

	public void setAudio(DashScopeAudioTranscriptionOptions.Audio audio) {
		this.options.setAudio(audio);
	}

	public Boolean getStream() {
		return this.options.getStream();
	}

	public void setStream(Boolean stream) {
		this.options.setStream(stream);
	}

	public DashScopeAudioTranscriptionOptions.StreamOptions getStreamOptions() {
		return this.options.getStreamOptions();
	}

	public void setStreamOptions(DashScopeAudioTranscriptionOptions.StreamOptions streamOptions) {
		this.options.setStreamOptions(streamOptions);
	}

	public Integer getMaxTokens() {
		return this.options.getMaxTokens();
	}

	public void setMaxTokens(Integer maxTokens) {
		this.options.setMaxTokens(maxTokens);
	}

	public Integer getSeed() {
		return this.options.getSeed();
	}

	public void setSeed(Integer seed) {
		this.options.setSeed(seed);
	}

	public Float getTemperature() {
		return this.options.getTemperature();
	}

	public void setTemperature(Float temperature) {
		this.options.setTemperature(temperature);
	}

	public Float getTopP() {
		return this.options.getTopP();
	}

	public void setTopP(Float topP) {
		this.options.setTopP(topP);
	}

	public Float getPresencePenalty() {
		return this.options.getPresencePenalty();
	}

	public void setPresencePenalty(Float presencePenalty) {
		this.options.setPresencePenalty(presencePenalty);
	}

	public Integer getTopK() {
		return this.options.getTopK();
	}

	public void setTopK(Integer topK) {
		this.options.setTopK(topK);
	}

	public Float getRepetitionPenalty() {
		return this.options.getRepetitionPenalty();
	}

	public void setRepetitionPenalty(Float repetitionPenalty) {
		this.options.setRepetitionPenalty(repetitionPenalty);
	}

	public DashScopeAudioTranscriptionOptions.TranslationOptions getTranslationOptions() {
		return this.options.getTranslationOptions();
	}

	public void setTranslationOptions(DashScopeAudioTranscriptionOptions.TranslationOptions translationOptions) {
		this.options.setTranslationOptions(translationOptions);
	}

	public Boolean getDisfluencyRemovalEnabled() {
		return this.options.getDisfluencyRemovalEnabled();
	}

	public void setDisfluencyRemovalEnabled(Boolean disfluencyRemovalEnabled) {
		this.options.setDisfluencyRemovalEnabled(disfluencyRemovalEnabled);
	}

	public List<String> getLanguageHints() {
		return this.options.getLanguageHints();
	}

	public void setLanguageHints(List<String> languageHints) {
		this.options.setLanguageHints(languageHints);
	}

	public Boolean getSemanticPunctuationEnabled() {
		return this.options.getSemanticPunctuationEnabled();
	}

	public void setSemanticPunctuationEnabled(Boolean semanticPunctuationEnabled) {
		this.options.setSemanticPunctuationEnabled(semanticPunctuationEnabled);
	}

	public Integer getMaxSentenceSilence() {
		return this.options.getMaxSentenceSilence();
	}

	public void setMaxSentenceSilence(Integer maxSentenceSilence) {
		this.options.setMaxSentenceSilence(maxSentenceSilence);
	}

	public Boolean getMultiThresholdModeEnabled() {
		return this.options.getMultiThresholdModeEnabled();
	}

	public void setMultiThresholdModeEnabled(Boolean multiThresholdModeEnabled) {
		this.options.setMultiThresholdModeEnabled(multiThresholdModeEnabled);
	}

	public Boolean getPunctuationPredictionEnabled() {
		return this.options.getPunctuationPredictionEnabled();
	}

	public void setPunctuationPredictionEnabled(Boolean punctuationPredictionEnabled) {
		this.options.setPunctuationPredictionEnabled(punctuationPredictionEnabled);
	}

	public Boolean getHeartbeat() {
		return this.options.getHeartbeat();
	}

	public void setHeartbeat(Boolean heartbeat) {
		this.options.setHeartbeat(heartbeat);
	}

	public Boolean getInverseTextNormalizationEnabled() {
		return this.options.getInverseTextNormalizationEnabled();
	}

	public void setInverseTextNormalizationEnabled(Boolean inverseTextNormalizationEnabled) {
		this.options.setInverseTextNormalizationEnabled(inverseTextNormalizationEnabled);
	}

	public List<DashScopeAudioTranscriptionOptions.Resource> getResources() {
		return this.options.getResources();
	}

	public void setResources(List<DashScopeAudioTranscriptionOptions.Resource> resources) {
		this.options.setResources(resources);
	}

	public Boolean getTimestampAlignmentEnabled() {
		return this.options.getTimestampAlignmentEnabled();
	}

	public void setTimestampAlignmentEnabled(Boolean timestampAlignmentEnabled) {
		this.options.setTimestampAlignmentEnabled(timestampAlignmentEnabled);
	}

	public String getSpecialWordFilter() {
		return this.options.getSpecialWordFilter();
	}

	public void setSpecialWordFilter(String specialWordFilter) {
		this.options.setSpecialWordFilter(specialWordFilter);
	}

	public Boolean getDiarizationEnabled() {
		return this.options.getDiarizationEnabled();
	}

	public void setDiarizationEnabled(Boolean diarizationEnabled) {
		this.options.setDiarizationEnabled(diarizationEnabled);
	}

	public Integer getSpeakerCount() {
		return this.options.getSpeakerCount();
	}

	public void setSpeakerCount(Integer speakerCount) {
		this.options.setSpeakerCount(speakerCount);
	}

	public List<Integer> getChannelId() {
		return this.options.getChannelId();
	}

	public void setChannelId(List<Integer> channelId) {
		this.options.setChannelId(channelId);
	}

    public String getWebsocketUrl() {
        return websocketUrl;
    }

    public void setWebsocketUrl(String websocketUrl) {
        this.websocketUrl = websocketUrl;
    }

}
