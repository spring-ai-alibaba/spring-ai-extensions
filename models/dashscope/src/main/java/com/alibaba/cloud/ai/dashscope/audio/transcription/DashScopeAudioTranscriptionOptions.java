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
    private String model;

    @JsonProperty("vocabulary_id")
    private String vocabularyId;

	@JsonProperty("sample_rate")
	private Integer sampleRate = 16000;

	@JsonProperty("format")
	private String format = "pcm";

    @JsonProperty("source_language")
    private String sourceLanguage;

    @JsonProperty("transcription_enabled")
    private Boolean transcriptionEnabled;

    @JsonProperty("translation_enabled")
    private Boolean translationEnabled;

    @JsonProperty("translation_target_languages")
    private List<String> translationTargetLanguages;

    @JsonProperty("max_end_silence")
    private Integer maxEndSilence;

    @JsonProperty("modalities")
    private List<String> modalities;

    @JsonProperty("audio")
    private Audio audio;

    @JsonProperty("stream")
    private Boolean stream;

    @JsonProperty("stream_options")
    private StreamOptions streamOptions;

    @JsonProperty("max_tokens")
    private Integer maxTokens;

    @JsonProperty("seed")
    private Integer seed;

    @JsonProperty("temperature")
    private Float temperature;

    @JsonProperty("top_p")
    private Float topP;

    @JsonProperty("presence_penalty")
    private Float presencePenalty;

    @JsonProperty("top_k")
    private Integer topK;

    @JsonProperty("repetition_penalty")
    private Float repetitionPenalty;

    @JsonProperty("translation_options")
    private TranslationOptions translationOptions;

    @JsonProperty("disfluency_removal_enabled")
    private Boolean disfluencyRemovalEnabled;

    @JsonProperty("language_hints")
    private List<String> languageHints;

    @JsonProperty("semantic_punctuation_enabled")
    private Boolean semanticPunctuationEnabled;

    @JsonProperty("max_sentence_silence")
    private Integer maxSentenceSilence;

    @JsonProperty("multi_threshold_mode_enabled")
    private Boolean multiThresholdModeEnabled;

    @JsonProperty("punctuation_prediction_enabled")
    private Boolean punctuationPredictionEnabled;

    @JsonProperty("heartbeat")
    private Boolean heartbeat;

    @JsonProperty("inverse_text_normalization_enabled")
    private Boolean inverseTextNormalizationEnabled;

    @JsonProperty("resources")
    private List<Resource> resources;

    @JsonProperty("timestamp_alignment_enabled")
    private Boolean timestampAlignmentEnabled;

    @JsonProperty("specialWordFilter")
    private String specialWordFilter;

    @JsonProperty("diarizationEnabled")
    private Boolean diarizationEnabled;

    @JsonProperty("speaker_count")
    private Integer speakerCount;

    @JsonProperty("channel_id")
    private List<Integer> channelId;

    @JsonProperty("asr_options")
    private AsrOptions asrOptions;

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

    public AsrOptions getAsrOptions() {
        return asrOptions;
    }

    public void setAsrOptions(AsrOptions asrOptions) {
        this.asrOptions = asrOptions;
    }

    public Integer getMaxEndSilence() {
        return maxEndSilence;
    }

    public void setMaxEndSilence(Integer maxEndSilence) {
        this.maxEndSilence = maxEndSilence;
    }

    public List<String> getModalities() {
        return modalities;
    }

    public void setModalities(List<String> modalities) {
        this.modalities = modalities;
    }

    public Audio getAudio() {
        return audio;
    }

    public void setAudio(Audio audio) {
        this.audio = audio;
    }

    public Boolean getStream() {
        return stream;
    }

    public void setStream(Boolean stream) {
        this.stream = stream;
    }

    public StreamOptions getStreamOptions() {
        return streamOptions;
    }

    public void setStreamOptions(StreamOptions streamOptions) {
        this.streamOptions = streamOptions;
    }

    public Integer getMaxTokens() {
        return maxTokens;
    }

    public void setMaxTokens(Integer maxTokens) {
        this.maxTokens = maxTokens;
    }

    public Integer getSeed() {
        return seed;
    }

    public void setSeed(Integer seed) {
        this.seed = seed;
    }

    public Float getTemperature() {
        return temperature;
    }

    public void setTemperature(Float temperature) {
        this.temperature = temperature;
    }

    public Float getTopP() {
        return topP;
    }

    public void setTopP(Float topP) {
        this.topP = topP;
    }

    public Float getPresencePenalty() {
        return presencePenalty;
    }

    public void setPresencePenalty(Float presencePenalty) {
        this.presencePenalty = presencePenalty;
    }

    public Integer getTopK() {
        return topK;
    }

    public void setTopK(Integer topK) {
        this.topK = topK;
    }

    public Float getRepetitionPenalty() {
        return repetitionPenalty;
    }

    public void setRepetitionPenalty(Float repetitionPenalty) {
        this.repetitionPenalty = repetitionPenalty;
    }

    public TranslationOptions getTranslationOptions() {
        return translationOptions;
    }

    public void setTranslationOptions(TranslationOptions translationOptions) {
        this.translationOptions = translationOptions;
    }

    public Boolean getDisfluencyRemovalEnabled() {
        return disfluencyRemovalEnabled;
    }

    public void setDisfluencyRemovalEnabled(Boolean disfluencyRemovalEnabled) {
        this.disfluencyRemovalEnabled = disfluencyRemovalEnabled;
    }

    public List<String> getLanguageHints() {
        return languageHints;
    }

    public void setLanguageHints(List<String> languageHints) {
        this.languageHints = languageHints;
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

    public List<Resource> getResources() {
        return resources;
    }

    public void setResources(List<Resource> resources) {
        this.resources = resources;
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

    public List<Integer> getChannelId() {
        return channelId;
    }

    public void setChannelId(List<Integer> channelId) {
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

        public Builder model(String model) {
            options.setModel(model);
            return this;
        }

        public Builder vocabularyId(String vocabularyId) {
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

        public Builder sourceLanguage(String sourceLanguage) {
            options.setSourceLanguage(sourceLanguage);
            return this;
        }

        public Builder transcriptionEnabled(Boolean transcriptionEnabled) {
            options.setTranscriptionEnabled(transcriptionEnabled);
            return this;
        }

        public Builder translationEnabled(Boolean translationEnabled) {
            options.setTranslationEnabled(translationEnabled);
            return this;
        }

        public Builder translationTargetLanguages(List<String> translationTargetLanguages) {
            options.setTranslationTargetLanguages(translationTargetLanguages);
            return this;
        }

        public Builder maxEndSilence(Integer maxEndSilence) {
            options.setMaxEndSilence(maxEndSilence);
            return this;
        }

        public Builder modalities(List<String> modalities) {
            options.setModalities(modalities);
            return this;
        }

        public Builder audio(Audio audio) {
            options.setAudio(audio);
            return this;
        }

        public Builder stream(Boolean stream) {
            options.setStream(stream);
            return this;
        }

        public Builder streamOptions(StreamOptions streamOptions) {
            options.setStreamOptions(streamOptions);
            return this;
        }

        public Builder maxTokens(Integer maxTokens) {
            options.setMaxTokens(maxTokens);
            return this;
        }

        public Builder seed(Integer seed) {
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
        private String voice;

        @JsonProperty("format")
        private String format;

        public Audio() {}

        public Audio(String voice, String format) {
            this.voice = voice;
            this.format = format;
        }

        public String getVoice() {
            return voice;
        }

        public void setVoice(String voice) {
            this.voice = voice;
        }

        public String getFormat() {
            return format;
        }

        public void setFormat(String format) {
            this.format = format;
        }
    }

    public static class StreamOptions {
        @JsonProperty("include_usage")
        private Boolean includeUsage;

        public StreamOptions() {}

        public StreamOptions(Boolean includeUsage) {
            this.includeUsage = includeUsage;
        }

        public Boolean getIncludeUsage() {
            return includeUsage;
        }

        public void setIncludeUsage(Boolean includeUsage) {
            this.includeUsage = includeUsage;
        }
    }

    public static class TranslationOptions {
        @JsonProperty("source_lang")
        private String sourceLang;

        @JsonProperty("target_lang")
        private String targetLang;

        public TranslationOptions() {}

        public TranslationOptions(String sourceLang, String targetLang) {
            this.sourceLang = sourceLang;
            this.targetLang = targetLang;
        }

        public String getSourceLang() {
            return sourceLang;
        }

        public void setSourceLang(String sourceLang) {
            this.sourceLang = sourceLang;
        }

        public String getTargetLang() {
            return targetLang;
        }

        public void setTargetLang(String targetLang) {
            this.targetLang = targetLang;
        }
    }

    public static class Resource {
        @JsonProperty("resource_id")
        private String resourceId;

        @JsonProperty("resource_type")
        private String resourceType;

        public String getResourceId() {
            return resourceId;
        }

        public void setResourceId(String resourceId) {
            this.resourceId = resourceId;
        }

        public String getResourceType() {
            return resourceType;
        }

        public void setResourceType(String resourceType) {
            this.resourceType = resourceType;
        }
    }

    public static class AsrOptions {
        @JsonProperty("language")
        private String language;

        public String getLanguage() {
            return language;
        }

        public void setLanguage(String language) {
            this.language = language;
        }

        public Boolean getEnableItn() {
            return enableItn;
        }

        public void setEnableItn(Boolean enableItn) {
            this.enableItn = enableItn;
        }

        @JsonProperty("enable_itn")
        private Boolean enableItn;
    }

}
