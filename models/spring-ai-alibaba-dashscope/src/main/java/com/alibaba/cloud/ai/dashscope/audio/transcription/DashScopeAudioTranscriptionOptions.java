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
    private @Nullable String model;

    @JsonProperty("vocabulary_id")
    private @Nullable String vocabularyId;

	@JsonProperty("sample_rate")
	private Integer sampleRate = 16000;

	@JsonProperty("format")
	private String format = "pcm";

    @JsonProperty("source_language")
    private @Nullable String sourceLanguage;

    @JsonProperty("transcription_enabled")
    private @Nullable Boolean transcriptionEnabled;

    @JsonProperty("translation_enabled")
    private @Nullable Boolean translationEnabled;

    @JsonProperty("translation_target_languages")
    private @Nullable List<String> translationTargetLanguages;

    @JsonProperty("max_end_silence")
    private @Nullable Integer maxEndSilence;

    @JsonProperty("modalities")
    private @Nullable List<String> modalities;

    @JsonProperty("audio")
    private @Nullable Audio audio;

    @JsonProperty("stream")
    private @Nullable Boolean stream;

    @JsonProperty("stream_options")
    private @Nullable StreamOptions streamOptions;

    @JsonProperty("max_tokens")
    private @Nullable Integer maxTokens;

    @JsonProperty("seed")
    private @Nullable Integer seed;

    @JsonProperty("temperature")
    private @Nullable Float temperature;

    @JsonProperty("top_p")
    private @Nullable Float topP;

    @JsonProperty("presence_penalty")
    private @Nullable Float presencePenalty;

    @JsonProperty("top_k")
    private @Nullable Integer topK;

    @JsonProperty("repetition_penalty")
    private @Nullable Float repetitionPenalty;

    @JsonProperty("translation_options")
    private @Nullable TranslationOptions translationOptions;

    @JsonProperty("disfluency_removal_enabled")
    private @Nullable Boolean disfluencyRemovalEnabled;

    @JsonProperty("language_hints")
    private @Nullable List<String> languageHints;

    @JsonProperty("semantic_punctuation_enabled")
    private @Nullable Boolean semanticPunctuationEnabled;

    @JsonProperty("max_sentence_silence")
    private @Nullable Integer maxSentenceSilence;

    @JsonProperty("multi_threshold_mode_enabled")
    private @Nullable Boolean multiThresholdModeEnabled;

    @JsonProperty("punctuation_prediction_enabled")
    private @Nullable Boolean punctuationPredictionEnabled;

    @JsonProperty("heartbeat")
    private @Nullable Boolean heartbeat;

    @JsonProperty("inverse_text_normalization_enabled")
    private @Nullable Boolean inverseTextNormalizationEnabled;

    @JsonProperty("resources")
    private @Nullable List<Resource> resources;

    @JsonProperty("timestamp_alignment_enabled")
    private @Nullable Boolean timestampAlignmentEnabled;

    @JsonProperty("specialWordFilter")
    private @Nullable String specialWordFilter;

    @JsonProperty("diarizationEnabled")
    private @Nullable Boolean diarizationEnabled;

    @JsonProperty("speaker_count")
    private @Nullable Integer speakerCount;

    @JsonProperty("channel_id")
    private @Nullable List<Integer> channelId;

    @JsonProperty("asr_options")
    private @Nullable AsrOptions asrOptions;

    @Override
	public @Nullable String getModel() {
		return model;
	}

    public void setModel(@Nullable String model) {
        this.model = model;
    }

    public @Nullable String getVocabularyId() {
        return vocabularyId;
    }

    public void setVocabularyId(@Nullable String vocabularyId) {
        this.vocabularyId = vocabularyId;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public Integer getSampleRate() {
        return sampleRate;
    }

    public void setSampleRate(Integer sampleRate) {
        this.sampleRate = sampleRate;
    }

    public @Nullable String getSourceLanguage() {
        return sourceLanguage;
    }

    public void setSourceLanguage(@Nullable String sourceLanguage) {
        this.sourceLanguage = sourceLanguage;
    }

    public @Nullable Boolean getTranscriptionEnabled() {
        return transcriptionEnabled;
    }

    public void setTranscriptionEnabled(@Nullable Boolean transcriptionEnabled) {
        this.transcriptionEnabled = transcriptionEnabled;
    }

    public @Nullable Boolean getTranslationEnabled() {
        return translationEnabled;
    }

    public void setTranslationEnabled(@Nullable Boolean translationEnabled) {
        this.translationEnabled = translationEnabled;
    }

    public @Nullable List<String> getTranslationTargetLanguages() {
        return translationTargetLanguages;
    }

    public void setTranslationTargetLanguages(@Nullable List<String> translationTargetLanguages) {
        this.translationTargetLanguages = translationTargetLanguages;
    }

    public @Nullable AsrOptions getAsrOptions() {
        return asrOptions;
    }

    public void setAsrOptions(@Nullable AsrOptions asrOptions) {
        this.asrOptions = asrOptions;
    }

    public @Nullable Integer getMaxEndSilence() {
        return maxEndSilence;
    }

    public void setMaxEndSilence(@Nullable Integer maxEndSilence) {
        this.maxEndSilence = maxEndSilence;
    }

    public @Nullable List<String> getModalities() {
        return modalities;
    }

    public void setModalities(@Nullable List<String> modalities) {
        this.modalities = modalities;
    }

    public @Nullable Audio getAudio() {
        return audio;
    }

    public void setAudio(@Nullable Audio audio) {
        this.audio = audio;
    }

    public @Nullable Boolean getStream() {
        return stream;
    }

    public void setStream(@Nullable Boolean stream) {
        this.stream = stream;
    }

    public @Nullable StreamOptions getStreamOptions() {
        return streamOptions;
    }

    public void setStreamOptions(@Nullable StreamOptions streamOptions) {
        this.streamOptions = streamOptions;
    }

    public @Nullable Integer getMaxTokens() {
        return maxTokens;
    }

    public void setMaxTokens(@Nullable Integer maxTokens) {
        this.maxTokens = maxTokens;
    }

    public @Nullable Integer getSeed() {
        return seed;
    }

    public void setSeed(@Nullable Integer seed) {
        this.seed = seed;
    }

    public @Nullable Float getTemperature() {
        return temperature;
    }

    public void setTemperature(@Nullable Float temperature) {
        this.temperature = temperature;
    }

    public @Nullable Float getTopP() {
        return topP;
    }

    public void setTopP(@Nullable Float topP) {
        this.topP = topP;
    }

    public @Nullable Float getPresencePenalty() {
        return presencePenalty;
    }

    public void setPresencePenalty(@Nullable Float presencePenalty) {
        this.presencePenalty = presencePenalty;
    }

    public @Nullable Integer getTopK() {
        return topK;
    }

    public void setTopK(@Nullable Integer topK) {
        this.topK = topK;
    }

    public @Nullable Float getRepetitionPenalty() {
        return repetitionPenalty;
    }

    public void setRepetitionPenalty(@Nullable Float repetitionPenalty) {
        this.repetitionPenalty = repetitionPenalty;
    }

    public @Nullable TranslationOptions getTranslationOptions() {
        return translationOptions;
    }

    public void setTranslationOptions(@Nullable TranslationOptions translationOptions) {
        this.translationOptions = translationOptions;
    }

    public @Nullable Boolean getDisfluencyRemovalEnabled() {
        return disfluencyRemovalEnabled;
    }

    public void setDisfluencyRemovalEnabled(@Nullable Boolean disfluencyRemovalEnabled) {
        this.disfluencyRemovalEnabled = disfluencyRemovalEnabled;
    }

    public @Nullable List<String> getLanguageHints() {
        return languageHints;
    }

    public void setLanguageHints(@Nullable List<String> languageHints) {
        this.languageHints = languageHints;
    }

    public @Nullable Boolean getSemanticPunctuationEnabled() {
        return semanticPunctuationEnabled;
    }

    public void setSemanticPunctuationEnabled(@Nullable Boolean semanticPunctuationEnabled) {
        this.semanticPunctuationEnabled = semanticPunctuationEnabled;
    }

    public @Nullable Integer getMaxSentenceSilence() {
        return maxSentenceSilence;
    }

    public void setMaxSentenceSilence(@Nullable Integer maxSentenceSilence) {
        this.maxSentenceSilence = maxSentenceSilence;
    }

    public @Nullable Boolean getMultiThresholdModeEnabled() {
        return multiThresholdModeEnabled;
    }

    public void setMultiThresholdModeEnabled(@Nullable Boolean multiThresholdModeEnabled) {
        this.multiThresholdModeEnabled = multiThresholdModeEnabled;
    }

    public @Nullable Boolean getPunctuationPredictionEnabled() {
        return punctuationPredictionEnabled;
    }

    public void setPunctuationPredictionEnabled(@Nullable Boolean punctuationPredictionEnabled) {
        this.punctuationPredictionEnabled = punctuationPredictionEnabled;
    }

    public @Nullable Boolean getHeartbeat() {
        return heartbeat;
    }

    public void setHeartbeat(@Nullable Boolean heartbeat) {
        this.heartbeat = heartbeat;
    }

    public @Nullable Boolean getInverseTextNormalizationEnabled() {
        return inverseTextNormalizationEnabled;
    }

    public void setInverseTextNormalizationEnabled(@Nullable Boolean inverseTextNormalizationEnabled) {
        this.inverseTextNormalizationEnabled = inverseTextNormalizationEnabled;
    }

    public @Nullable List<Resource> getResources() {
        return resources;
    }

    public void setResources(@Nullable List<Resource> resources) {
        this.resources = resources;
    }

    public @Nullable Boolean getTimestampAlignmentEnabled() {
        return timestampAlignmentEnabled;
    }

    public void setTimestampAlignmentEnabled(@Nullable Boolean timestampAlignmentEnabled) {
        this.timestampAlignmentEnabled = timestampAlignmentEnabled;
    }

    public @Nullable String getSpecialWordFilter() {
        return specialWordFilter;
    }

    public void setSpecialWordFilter(@Nullable String specialWordFilter) {
        this.specialWordFilter = specialWordFilter;
    }

    public @Nullable Boolean getDiarizationEnabled() {
        return diarizationEnabled;
    }

    public void setDiarizationEnabled(@Nullable Boolean diarizationEnabled) {
        this.diarizationEnabled = diarizationEnabled;
    }

    public @Nullable Integer getSpeakerCount() {
        return speakerCount;
    }

    public void setSpeakerCount(@Nullable Integer speakerCount) {
        this.speakerCount = speakerCount;
    }

    public @Nullable List<Integer> getChannelId() {
        return channelId;
    }

    public void setChannelId(@Nullable List<Integer> channelId) {
        this.channelId = channelId;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final DashScopeAudioTranscriptionOptions options;

        public Builder() {
            options = new DashScopeAudioTranscriptionOptions();
        }

        public Builder model(@Nullable String model) {
            options.setModel(model);
            return this;
        }

        public Builder vocabularyId(@Nullable String vocabularyId) {
            options.setVocabularyId(vocabularyId);
            return this;
        }

        public Builder sampleRate(Integer sampleRate) {
            options.setSampleRate(sampleRate);
            return this;
        }

        public Builder format(String format) {
            options.setFormat(format);
            return this;
        }

        public Builder sourceLanguage(@Nullable String sourceLanguage) {
            options.setSourceLanguage(sourceLanguage);
            return this;
        }

        public Builder transcriptionEnabled(@Nullable Boolean transcriptionEnabled) {
            options.setTranscriptionEnabled(transcriptionEnabled);
            return this;
        }

        public Builder translationEnabled(@Nullable Boolean translationEnabled) {
            options.setTranslationEnabled(translationEnabled);
            return this;
        }

        public Builder translationTargetLanguages(@Nullable List<String> translationTargetLanguages) {
            options.setTranslationTargetLanguages(translationTargetLanguages);
            return this;
        }

        public Builder maxEndSilence(@Nullable Integer maxEndSilence) {
            options.setMaxEndSilence(maxEndSilence);
            return this;
        }

        public Builder modalities(@Nullable List<String> modalities) {
            options.setModalities(modalities);
            return this;
        }

        public Builder audio(@Nullable Audio audio) {
            options.setAudio(audio);
            return this;
        }

        public Builder stream(@Nullable Boolean stream) {
            options.setStream(stream);
            return this;
        }

        public Builder streamOptions(@Nullable StreamOptions streamOptions) {
            options.setStreamOptions(streamOptions);
            return this;
        }

        public Builder maxTokens(@Nullable Integer maxTokens) {
            options.setMaxTokens(maxTokens);
            return this;
        }

        public Builder seed(@Nullable Integer seed) {
            options.setSeed(seed);
            return this;
        }

        public Builder temperature(Float temperature) {
            options.setTemperature(temperature);
            return this;
        }

        public Builder topP(Float topP) {
            options.setTopP(topP);
            return this;
        }

        public Builder presencePenalty(Float presencePenalty) {
            options.setPresencePenalty(presencePenalty);
            return this;
        }

        public Builder topK(Integer topK) {
            options.setTopK(topK);
            return this;
        }

        public Builder repetitionPenalty(Float repetitionPenalty) {
            options.setRepetitionPenalty(repetitionPenalty);
            return this;
        }

        public Builder translationOptions(TranslationOptions translationOptions) {
            options.setTranslationOptions(translationOptions);
            return this;
        }

        public Builder disfluencyRemovalEnabled(Boolean disfluencyRemovalEnabled) {
            options.setDisfluencyRemovalEnabled(disfluencyRemovalEnabled);
            return this;
        }

        public Builder languageHints(List<String> languageHints) {
            options.setLanguageHints(languageHints);
            return this;
        }

        public Builder semanticPunctuationEnabled(Boolean semanticPunctuationEnabled) {
            options.setSemanticPunctuationEnabled(semanticPunctuationEnabled);
            return this;
        }

        public Builder maxSentenceSilence(Integer maxSentenceSilence) {
            options.setMaxSentenceSilence(maxSentenceSilence);
            return this;
        }

        public Builder multiThresholdModeEnabled(Boolean multiThresholdModeEnabled) {
            options.setMultiThresholdModeEnabled(multiThresholdModeEnabled);
            return this;
        }

        public Builder punctuationPredictionEnabled(Boolean punctuationPredictionEnabled) {
            options.setPunctuationPredictionEnabled(punctuationPredictionEnabled);
            return this;
        }

        public Builder heartbeat(Boolean heartbeat) {
            options.setHeartbeat(heartbeat);
            return this;
        }

        public Builder inverseTextNormalizationEnabled(Boolean inverseTextNormalizationEnabled) {
            options.setInverseTextNormalizationEnabled(inverseTextNormalizationEnabled);
            return this;
        }

        public Builder resources(List<Resource> resources) {
            options.setResources(resources);
            return this;
        }

        public Builder timestampAlignmentEnabled(Boolean timestampAlignmentEnabled) {
            options.setTimestampAlignmentEnabled(timestampAlignmentEnabled);
            return this;
        }

        public Builder specialWordFilter(String specialWordFilter) {
            options.setSpecialWordFilter(specialWordFilter);
            return this;
        }

        public Builder diarizationEnabled(Boolean diarizationEnabled) {
            options.setDiarizationEnabled(diarizationEnabled);
            return this;
        }

        public Builder speakerCount(Integer speakerCount) {
            options.setSpeakerCount(speakerCount);
            return this;
        }

        public Builder channelId(List<Integer> channelId) {
            options.setChannelId(channelId);
            return this;
        }

        public Builder asrOptions(AsrOptions asrOptions) {
            options.setAsrOptions(asrOptions);
            return this;
        }

        public DashScopeAudioTranscriptionOptions build() {
            return this.options;
        }
    }

    public static class Audio {
        @JsonProperty("voice")
        private @Nullable String voice;

        @JsonProperty("format")
        private @Nullable String format;

        public Audio() {}

        public Audio(@Nullable String voice, @Nullable String format) {
            this.voice = voice;
            this.format = format;
        }

        public @Nullable String getVoice() {
            return voice;
        }

        public void setVoice(@Nullable String voice) {
            this.voice = voice;
        }

        public @Nullable String getFormat() {
            return format;
        }

        public void setFormat(@Nullable String format) {
            this.format = format;
        }
    }

    public static class StreamOptions {
        @JsonProperty("include_usage")
        private @Nullable Boolean includeUsage;

        public StreamOptions() {}

        public StreamOptions(@Nullable Boolean includeUsage) {
            this.includeUsage = includeUsage;
        }

        public @Nullable Boolean getIncludeUsage() {
            return includeUsage;
        }

        public void setIncludeUsage(@Nullable Boolean includeUsage) {
            this.includeUsage = includeUsage;
        }
    }

    public static class TranslationOptions {
        @JsonProperty("source_lang")
        private @Nullable String sourceLang;

        @JsonProperty("target_lang")
        private @Nullable String targetLang;

        public TranslationOptions() {}

        public TranslationOptions(@Nullable String sourceLang, @Nullable String targetLang) {
            this.sourceLang = sourceLang;
            this.targetLang = targetLang;
        }

        public @Nullable String getSourceLang() {
            return sourceLang;
        }

        public void setSourceLang(@Nullable String sourceLang) {
            this.sourceLang = sourceLang;
        }

        public @Nullable String getTargetLang() {
            return targetLang;
        }

        public void setTargetLang(@Nullable String targetLang) {
            this.targetLang = targetLang;
        }
    }

    public static class Resource {
        @JsonProperty("resource_id")
        private @Nullable String resourceId;

        @JsonProperty("resource_type")
        private @Nullable String resourceType;

        public @Nullable String getResourceId() {
            return resourceId;
        }

        public void setResourceId(@Nullable String resourceId) {
            this.resourceId = resourceId;
        }

        public @Nullable String getResourceType() {
            return resourceType;
        }

        public void setResourceType(@Nullable String resourceType) {
            this.resourceType = resourceType;
        }
    }

    public static class AsrOptions {
        @JsonProperty("language")
        private @Nullable String language;

        public @Nullable String getLanguage() {
            return language;
        }

        public void setLanguage(@Nullable String language) {
            this.language = language;
        }

        public @Nullable Boolean getEnableItn() {
            return enableItn;
        }

        public void setEnableItn(@Nullable Boolean enableItn) {
            this.enableItn = enableItn;
        }

        @JsonProperty("enable_itn")
        private @Nullable Boolean enableItn;
    }

}
