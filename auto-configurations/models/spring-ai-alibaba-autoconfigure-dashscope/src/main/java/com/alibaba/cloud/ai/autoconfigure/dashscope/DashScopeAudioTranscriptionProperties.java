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
 * DashScope audio transcription auto-configuration properties.
 *
 * @author xYLiu
 * @author yuluo
 * @author kevinlin09
 * @author xuguan
 * @author yingzi
 */
@ConfigurationProperties(DashScopeAudioTranscriptionProperties.CONFIG_PREFIX)
public class DashScopeAudioTranscriptionProperties extends DashScopeParentProperties {

    /**
     * Spring AI Alibaba configuration prefix.
     */
    public static final String CONFIG_PREFIX = "spring.ai.dashscope.audio.transcription";

    private String websocketUrl = DashScopeAudioApiConstants.DEFAULT_WEBSOCKET_URL;

    private @Nullable String model;

    private @Nullable String vocabularyId;

    private @Nullable Integer sampleRate;

    private @Nullable String format;

    private @Nullable String sourceLanguage;

    private @Nullable Boolean transcriptionEnabled;

    private @Nullable Boolean translationEnabled;

    private @Nullable List<String> translationTargetLanguages;

    private @Nullable Integer maxEndSilence;

    private @Nullable List<String> modalities;

    private DashScopeAudioTranscriptionOptions.@Nullable Audio audio;

    private @Nullable Boolean stream;

    private DashScopeAudioTranscriptionOptions.@Nullable StreamOptions streamOptions;

    private @Nullable Integer maxTokens;

    private @Nullable Integer seed;

    private @Nullable Float temperature;

    private @Nullable Float topP;

    private @Nullable Float presencePenalty;

    private @Nullable Integer topK;

    private @Nullable Float repetitionPenalty;

    private DashScopeAudioTranscriptionOptions.@Nullable TranslationOptions translationOptions;

    private @Nullable Boolean disfluencyRemovalEnabled;

    private @Nullable List<String> languageHints;

    private @Nullable Boolean semanticPunctuationEnabled;

    private @Nullable Integer maxSentenceSilence;

    private @Nullable Boolean multiThresholdModeEnabled;

    private @Nullable Boolean punctuationPredictionEnabled;

    private @Nullable Boolean heartbeat;

    private @Nullable Boolean inverseTextNormalizationEnabled;

    private @Nullable List<DashScopeAudioTranscriptionOptions.Resource> resources;

    private @Nullable Boolean timestampAlignmentEnabled;

    private @Nullable String specialWordFilter;

    private @Nullable Boolean diarizationEnabled;

    private @Nullable Integer speakerCount;

    private @Nullable List<Integer> channelId;

    private DashScopeAudioTranscriptionOptions.@Nullable AsrOptions asrOptions;

    public @Nullable String getModel() {
        return this.model;
    }

    public void setModel(@Nullable String model) {
        this.model = model;
    }

    public @Nullable String getVocabularyId() {
        return this.vocabularyId;
    }

    public void setVocabularyId(@Nullable String vocabularyId) {
        this.vocabularyId = vocabularyId;
    }

    public @Nullable Integer getSampleRate() {
        return this.sampleRate;
    }

    public void setSampleRate(@Nullable Integer sampleRate) {
        this.sampleRate = sampleRate;
    }

    public @Nullable String getFormat() {
        return this.format;
    }

    public void setFormat(@Nullable String format) {
        this.format = format;
    }

    public @Nullable String getSourceLanguage() {
        return this.sourceLanguage;
    }

    public void setSourceLanguage(@Nullable String sourceLanguage) {
        this.sourceLanguage = sourceLanguage;
    }

    public @Nullable Boolean getTranscriptionEnabled() {
        return this.transcriptionEnabled;
    }

    public void setTranscriptionEnabled(@Nullable Boolean transcriptionEnabled) {
        this.transcriptionEnabled = transcriptionEnabled;
    }

    public @Nullable Boolean getTranslationEnabled() {
        return this.translationEnabled;
    }

    public void setTranslationEnabled(@Nullable Boolean translationEnabled) {
        this.translationEnabled = translationEnabled;
    }

    public @Nullable List<String> getTranslationTargetLanguages() {
        return this.translationTargetLanguages;
    }

    public void setTranslationTargetLanguages(@Nullable List<String> translationTargetLanguages) {
        this.translationTargetLanguages = translationTargetLanguages;
    }

    public @Nullable Integer getMaxEndSilence() {
        return this.maxEndSilence;
    }

    public void setMaxEndSilence(@Nullable Integer maxEndSilence) {
        this.maxEndSilence = maxEndSilence;
    }

    public @Nullable List<String> getModalities() {
        return this.modalities;
    }

    public void setModalities(@Nullable List<String> modalities) {
        this.modalities = modalities;
    }

    public DashScopeAudioTranscriptionOptions.@Nullable Audio getAudio() {
        return this.audio;
    }

    public void setAudio(DashScopeAudioTranscriptionOptions.@Nullable Audio audio) {
        this.audio = audio;
    }

    public @Nullable Boolean getStream() {
        return this.stream;
    }

    public void setStream(@Nullable Boolean stream) {
        this.stream = stream;
    }

    public DashScopeAudioTranscriptionOptions.@Nullable StreamOptions getStreamOptions() {
        return this.streamOptions;
    }

    public void setStreamOptions(DashScopeAudioTranscriptionOptions.@Nullable StreamOptions streamOptions) {
        this.streamOptions = streamOptions;
    }

    public @Nullable Integer getMaxTokens() {
        return this.maxTokens;
    }

    public void setMaxTokens(@Nullable Integer maxTokens) {
        this.maxTokens = maxTokens;
    }

    public @Nullable Integer getSeed() {
        return this.seed;
    }

    public void setSeed(@Nullable Integer seed) {
        this.seed = seed;
    }

    public @Nullable Float getTemperature() {
        return this.temperature;
    }

    public void setTemperature(@Nullable Float temperature) {
        this.temperature = temperature;
    }

    public @Nullable Float getTopP() {
        return this.topP;
    }

    public void setTopP(@Nullable Float topP) {
        this.topP = topP;
    }

    public @Nullable Float getPresencePenalty() {
        return this.presencePenalty;
    }

    public void setPresencePenalty(@Nullable Float presencePenalty) {
        this.presencePenalty = presencePenalty;
    }

    public @Nullable Integer getTopK() {
        return this.topK;
    }

    public void setTopK(@Nullable Integer topK) {
        this.topK = topK;
    }

    public @Nullable Float getRepetitionPenalty() {
        return this.repetitionPenalty;
    }

    public void setRepetitionPenalty(@Nullable Float repetitionPenalty) {
        this.repetitionPenalty = repetitionPenalty;
    }

    public DashScopeAudioTranscriptionOptions.@Nullable TranslationOptions getTranslationOptions() {
        return this.translationOptions;
    }

    public void setTranslationOptions(DashScopeAudioTranscriptionOptions.@Nullable TranslationOptions translationOptions) {
        this.translationOptions = translationOptions;
    }

    public @Nullable Boolean getDisfluencyRemovalEnabled() {
        return this.disfluencyRemovalEnabled;
    }

    public void setDisfluencyRemovalEnabled(@Nullable Boolean disfluencyRemovalEnabled) {
        this.disfluencyRemovalEnabled = disfluencyRemovalEnabled;
    }

    public @Nullable List<String> getLanguageHints() {
        return this.languageHints;
    }

    public void setLanguageHints(@Nullable List<String> languageHints) {
        this.languageHints = languageHints;
    }

    public @Nullable Boolean getSemanticPunctuationEnabled() {
        return this.semanticPunctuationEnabled;
    }

    public void setSemanticPunctuationEnabled(@Nullable Boolean semanticPunctuationEnabled) {
        this.semanticPunctuationEnabled = semanticPunctuationEnabled;
    }

    public @Nullable Integer getMaxSentenceSilence() {
        return this.maxSentenceSilence;
    }

    public void setMaxSentenceSilence(@Nullable Integer maxSentenceSilence) {
        this.maxSentenceSilence = maxSentenceSilence;
    }

    public @Nullable Boolean getMultiThresholdModeEnabled() {
        return this.multiThresholdModeEnabled;
    }

    public void setMultiThresholdModeEnabled(@Nullable Boolean multiThresholdModeEnabled) {
        this.multiThresholdModeEnabled = multiThresholdModeEnabled;
    }

    public @Nullable Boolean getPunctuationPredictionEnabled() {
        return this.punctuationPredictionEnabled;
    }

    public void setPunctuationPredictionEnabled(@Nullable Boolean punctuationPredictionEnabled) {
        this.punctuationPredictionEnabled = punctuationPredictionEnabled;
    }

    public @Nullable Boolean getHeartbeat() {
        return this.heartbeat;
    }

    public void setHeartbeat(@Nullable Boolean heartbeat) {
        this.heartbeat = heartbeat;
    }

    public @Nullable Boolean getInverseTextNormalizationEnabled() {
        return this.inverseTextNormalizationEnabled;
    }

    public void setInverseTextNormalizationEnabled(@Nullable Boolean inverseTextNormalizationEnabled) {
        this.inverseTextNormalizationEnabled = inverseTextNormalizationEnabled;
    }

    public @Nullable List<DashScopeAudioTranscriptionOptions.Resource> getResources() {
        return this.resources;
    }

    public void setResources(@Nullable List<DashScopeAudioTranscriptionOptions.Resource> resources) {
        this.resources = resources;
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

    public @Nullable List<Integer> getChannelId() {
        return this.channelId;
    }

    public void setChannelId(@Nullable List<Integer> channelId) {
        this.channelId = channelId;
    }

    public DashScopeAudioTranscriptionOptions.@Nullable AsrOptions getAsrOptions() {
        return this.asrOptions;
    }

    public void setAsrOptions(DashScopeAudioTranscriptionOptions.@Nullable AsrOptions asrOptions) {
        this.asrOptions = asrOptions;
    }

    public String getWebsocketUrl() {
        return this.websocketUrl;
    }

    public void setWebsocketUrl(String websocketUrl) {
        this.websocketUrl = websocketUrl;
    }

    public DashScopeAudioTranscriptionOptions toOptions() {
        return DashScopeAudioTranscriptionOptions.builder()
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
                .asrOptions(this.asrOptions)
                .build();
    }

    private Options options = new Options();

    @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX)
    @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
    public Options getOptions() {
        return this.options;
    }

    public void setOptions(Options options) {
        this.options = options;
    }

    public class Options {

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".model")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable String getModel() {
            return DashScopeAudioTranscriptionProperties.this.getModel();
        }

        public void setModel(@Nullable String model) {
            DashScopeAudioTranscriptionProperties.this.setModel(model);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".vocabulary-id")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable String getVocabularyId() {
            return DashScopeAudioTranscriptionProperties.this.getVocabularyId();
        }

        public void setVocabularyId(@Nullable String vocabularyId) {
            DashScopeAudioTranscriptionProperties.this.setVocabularyId(vocabularyId);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".format")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable String getFormat() {
            return DashScopeAudioTranscriptionProperties.this.getFormat();
        }

        public void setFormat(@Nullable String format) {
            DashScopeAudioTranscriptionProperties.this.setFormat(format);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".sample-rate")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable Integer getSampleRate() {
            return DashScopeAudioTranscriptionProperties.this.getSampleRate();
        }

        public void setSampleRate(@Nullable Integer sampleRate) {
            DashScopeAudioTranscriptionProperties.this.setSampleRate(sampleRate);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".source-language")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable String getSourceLanguage() {
            return DashScopeAudioTranscriptionProperties.this.getSourceLanguage();
        }

        public void setSourceLanguage(@Nullable String sourceLanguage) {
            DashScopeAudioTranscriptionProperties.this.setSourceLanguage(sourceLanguage);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".transcription-enabled")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable Boolean getTranscriptionEnabled() {
            return DashScopeAudioTranscriptionProperties.this.getTranscriptionEnabled();
        }

        public void setTranscriptionEnabled(@Nullable Boolean transcriptionEnabled) {
            DashScopeAudioTranscriptionProperties.this.setTranscriptionEnabled(transcriptionEnabled);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".translation-enabled")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable Boolean getTranslationEnabled() {
            return DashScopeAudioTranscriptionProperties.this.getTranslationEnabled();
        }

        public void setTranslationEnabled(@Nullable Boolean translationEnabled) {
            DashScopeAudioTranscriptionProperties.this.setTranslationEnabled(translationEnabled);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".translation-target-languages")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable List<String> getTranslationTargetLanguages() {
            return DashScopeAudioTranscriptionProperties.this.getTranslationTargetLanguages();
        }

        public void setTranslationTargetLanguages(@Nullable List<String> translationTargetLanguages) {
            DashScopeAudioTranscriptionProperties.this.setTranslationTargetLanguages(translationTargetLanguages);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".asr-options")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public DashScopeAudioTranscriptionOptions.@Nullable AsrOptions getAsrOptions() {
            return DashScopeAudioTranscriptionProperties.this.getAsrOptions();
        }

        public void setAsrOptions(DashScopeAudioTranscriptionOptions.@Nullable AsrOptions asrOptions) {
            DashScopeAudioTranscriptionProperties.this.setAsrOptions(asrOptions);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".max-end-silence")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable Integer getMaxEndSilence() {
            return DashScopeAudioTranscriptionProperties.this.getMaxEndSilence();
        }

        public void setMaxEndSilence(@Nullable Integer maxEndSilence) {
            DashScopeAudioTranscriptionProperties.this.setMaxEndSilence(maxEndSilence);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".modalities")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable List<String> getModalities() {
            return DashScopeAudioTranscriptionProperties.this.getModalities();
        }

        public void setModalities(@Nullable List<String> modalities) {
            DashScopeAudioTranscriptionProperties.this.setModalities(modalities);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".audio")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public DashScopeAudioTranscriptionOptions.@Nullable Audio getAudio() {
            return DashScopeAudioTranscriptionProperties.this.getAudio();
        }

        public void setAudio(DashScopeAudioTranscriptionOptions.@Nullable Audio audio) {
            DashScopeAudioTranscriptionProperties.this.setAudio(audio);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".stream")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable Boolean getStream() {
            return DashScopeAudioTranscriptionProperties.this.getStream();
        }

        public void setStream(@Nullable Boolean stream) {
            DashScopeAudioTranscriptionProperties.this.setStream(stream);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".stream-options")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public DashScopeAudioTranscriptionOptions.@Nullable StreamOptions getStreamOptions() {
            return DashScopeAudioTranscriptionProperties.this.getStreamOptions();
        }

        public void setStreamOptions(DashScopeAudioTranscriptionOptions.@Nullable StreamOptions streamOptions) {
            DashScopeAudioTranscriptionProperties.this.setStreamOptions(streamOptions);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".max-tokens")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable Integer getMaxTokens() {
            return DashScopeAudioTranscriptionProperties.this.getMaxTokens();
        }

        public void setMaxTokens(@Nullable Integer maxTokens) {
            DashScopeAudioTranscriptionProperties.this.setMaxTokens(maxTokens);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".seed")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable Integer getSeed() {
            return DashScopeAudioTranscriptionProperties.this.getSeed();
        }

        public void setSeed(@Nullable Integer seed) {
            DashScopeAudioTranscriptionProperties.this.setSeed(seed);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".temperature")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable Float getTemperature() {
            return DashScopeAudioTranscriptionProperties.this.getTemperature();
        }

        public void setTemperature(@Nullable Float temperature) {
            DashScopeAudioTranscriptionProperties.this.setTemperature(temperature);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".top-p")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable Float getTopP() {
            return DashScopeAudioTranscriptionProperties.this.getTopP();
        }

        public void setTopP(@Nullable Float topP) {
            DashScopeAudioTranscriptionProperties.this.setTopP(topP);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".presence-penalty")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable Float getPresencePenalty() {
            return DashScopeAudioTranscriptionProperties.this.getPresencePenalty();
        }

        public void setPresencePenalty(@Nullable Float presencePenalty) {
            DashScopeAudioTranscriptionProperties.this.setPresencePenalty(presencePenalty);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".top-k")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable Integer getTopK() {
            return DashScopeAudioTranscriptionProperties.this.getTopK();
        }

        public void setTopK(@Nullable Integer topK) {
            DashScopeAudioTranscriptionProperties.this.setTopK(topK);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".repetition-penalty")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable Float getRepetitionPenalty() {
            return DashScopeAudioTranscriptionProperties.this.getRepetitionPenalty();
        }

        public void setRepetitionPenalty(@Nullable Float repetitionPenalty) {
            DashScopeAudioTranscriptionProperties.this.setRepetitionPenalty(repetitionPenalty);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".translation-options")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public DashScopeAudioTranscriptionOptions.@Nullable TranslationOptions getTranslationOptions() {
            return DashScopeAudioTranscriptionProperties.this.getTranslationOptions();
        }

        public void setTranslationOptions(DashScopeAudioTranscriptionOptions.@Nullable TranslationOptions translationOptions) {
            DashScopeAudioTranscriptionProperties.this.setTranslationOptions(translationOptions);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".disfluency-removal-enabled")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable Boolean getDisfluencyRemovalEnabled() {
            return DashScopeAudioTranscriptionProperties.this.getDisfluencyRemovalEnabled();
        }

        public void setDisfluencyRemovalEnabled(@Nullable Boolean disfluencyRemovalEnabled) {
            DashScopeAudioTranscriptionProperties.this.setDisfluencyRemovalEnabled(disfluencyRemovalEnabled);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".language-hints")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable List<String> getLanguageHints() {
            return DashScopeAudioTranscriptionProperties.this.getLanguageHints();
        }

        public void setLanguageHints(@Nullable List<String> languageHints) {
            DashScopeAudioTranscriptionProperties.this.setLanguageHints(languageHints);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".semantic-punctuation-enabled")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable Boolean getSemanticPunctuationEnabled() {
            return DashScopeAudioTranscriptionProperties.this.getSemanticPunctuationEnabled();
        }

        public void setSemanticPunctuationEnabled(@Nullable Boolean semanticPunctuationEnabled) {
            DashScopeAudioTranscriptionProperties.this.setSemanticPunctuationEnabled(semanticPunctuationEnabled);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".max-sentence-silence")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable Integer getMaxSentenceSilence() {
            return DashScopeAudioTranscriptionProperties.this.getMaxSentenceSilence();
        }

        public void setMaxSentenceSilence(@Nullable Integer maxSentenceSilence) {
            DashScopeAudioTranscriptionProperties.this.setMaxSentenceSilence(maxSentenceSilence);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".multi-threshold-mode-enabled")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable Boolean getMultiThresholdModeEnabled() {
            return DashScopeAudioTranscriptionProperties.this.getMultiThresholdModeEnabled();
        }

        public void setMultiThresholdModeEnabled(@Nullable Boolean multiThresholdModeEnabled) {
            DashScopeAudioTranscriptionProperties.this.setMultiThresholdModeEnabled(multiThresholdModeEnabled);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".punctuation-prediction-enabled")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable Boolean getPunctuationPredictionEnabled() {
            return DashScopeAudioTranscriptionProperties.this.getPunctuationPredictionEnabled();
        }

        public void setPunctuationPredictionEnabled(@Nullable Boolean punctuationPredictionEnabled) {
            DashScopeAudioTranscriptionProperties.this.setPunctuationPredictionEnabled(punctuationPredictionEnabled);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".heartbeat")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable Boolean getHeartbeat() {
            return DashScopeAudioTranscriptionProperties.this.getHeartbeat();
        }

        public void setHeartbeat(@Nullable Boolean heartbeat) {
            DashScopeAudioTranscriptionProperties.this.setHeartbeat(heartbeat);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".inverse-text-normalization-enabled")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable Boolean getInverseTextNormalizationEnabled() {
            return DashScopeAudioTranscriptionProperties.this.getInverseTextNormalizationEnabled();
        }

        public void setInverseTextNormalizationEnabled(@Nullable Boolean inverseTextNormalizationEnabled) {
            DashScopeAudioTranscriptionProperties.this.setInverseTextNormalizationEnabled(inverseTextNormalizationEnabled);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".resources")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable List<DashScopeAudioTranscriptionOptions.Resource> getResources() {
            return DashScopeAudioTranscriptionProperties.this.getResources();
        }

        public void setResources(@Nullable List<DashScopeAudioTranscriptionOptions.Resource> resources) {
            DashScopeAudioTranscriptionProperties.this.setResources(resources);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".timestamp-alignment-enabled")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable Boolean getTimestampAlignmentEnabled() {
            return DashScopeAudioTranscriptionProperties.this.getTimestampAlignmentEnabled();
        }

        public void setTimestampAlignmentEnabled(@Nullable Boolean timestampAlignmentEnabled) {
            DashScopeAudioTranscriptionProperties.this.setTimestampAlignmentEnabled(timestampAlignmentEnabled);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".special-word-filter")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable String getSpecialWordFilter() {
            return DashScopeAudioTranscriptionProperties.this.getSpecialWordFilter();
        }

        public void setSpecialWordFilter(@Nullable String specialWordFilter) {
            DashScopeAudioTranscriptionProperties.this.setSpecialWordFilter(specialWordFilter);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".diarization-enabled")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable Boolean getDiarizationEnabled() {
            return DashScopeAudioTranscriptionProperties.this.getDiarizationEnabled();
        }

        public void setDiarizationEnabled(@Nullable Boolean diarizationEnabled) {
            DashScopeAudioTranscriptionProperties.this.setDiarizationEnabled(diarizationEnabled);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".speaker-count")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable Integer getSpeakerCount() {
            return DashScopeAudioTranscriptionProperties.this.getSpeakerCount();
        }

        public void setSpeakerCount(@Nullable Integer speakerCount) {
            DashScopeAudioTranscriptionProperties.this.setSpeakerCount(speakerCount);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".channel-id")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable List<Integer> getChannelId() {
            return DashScopeAudioTranscriptionProperties.this.getChannelId();
        }

        public void setChannelId(@Nullable List<Integer> channelId) {
            DashScopeAudioTranscriptionProperties.this.setChannelId(channelId);
        }

    }

}
