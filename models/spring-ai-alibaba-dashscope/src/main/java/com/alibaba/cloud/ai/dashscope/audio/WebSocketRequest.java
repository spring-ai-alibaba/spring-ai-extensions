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

package com.alibaba.cloud.ai.dashscope.audio;

import com.alibaba.cloud.ai.dashscope.audio.transcription.DashScopeAudioTranscriptionOptions.Resource;
import com.alibaba.cloud.ai.dashscope.audio.tts.DashScopeAudioSpeechOptions;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jspecify.annotations.Nullable;

import java.util.List;

/**
 * DashScope WebSocket Request.
 *
 * @author yingzi
 * @since 2026/1/25
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WebSocketRequest {

    @JsonProperty("header")
    private @Nullable RequestHeader header;

    @JsonProperty("payload")
    private @Nullable RequestPayload payload;

    public WebSocketRequest(@Nullable RequestHeader header, @Nullable RequestPayload payload) {
        this.header = header;
        this.payload = payload;
    }

    public @Nullable RequestHeader getHeader() {
        return header;
    }

    public void setHeader(@Nullable RequestHeader header) {
        this.header = header;
    }

    public @Nullable RequestPayload getPayload() {
        return payload;
    }

    public void setPayload(@Nullable RequestPayload payload) {
        this.payload = payload;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private @Nullable RequestHeader header;

        private @Nullable RequestPayload payload;

        public Builder header(@Nullable RequestHeader header) {
            this.header = header;
            return this;
        }

        public Builder payload(@Nullable RequestPayload payload) {
            this.payload = payload;
            return this;
        }

        public WebSocketRequest build() {
            return new WebSocketRequest(this.header, this.payload);
        }

    }

    /**
     * Request header.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class RequestHeader {

        @JsonProperty("action")
        private DashScopeWebSocketClient.@Nullable EventType action;

        @JsonProperty("task_id")
        private @Nullable String taskId;

        @JsonProperty("streaming")
        private @Nullable String streaming;

        public RequestHeader() {
        }

        public static Builder builder() {
            return new Builder();
        }

        public DashScopeWebSocketClient.@Nullable EventType getAction() {
            return action;
        }

        public void setAction(DashScopeWebSocketClient.@Nullable EventType action) {
            this.action = action;
        }

        public @Nullable String getTaskId() {
            return taskId;
        }

        public void setTaskId(@Nullable String taskId) {
            this.taskId = taskId;
        }

        public @Nullable String getStreaming() {
            return streaming;
        }

        public void setStreaming(@Nullable String streaming) {
            this.streaming = streaming;
        }

        public static class Builder {

            private final RequestHeader header;

            public Builder() {
                this.header = new RequestHeader();
            }

            public Builder action(DashScopeWebSocketClient.@Nullable EventType action) {
                this.header.action = action;
                return this;
            }

            public Builder taskId(@Nullable String taskId) {
                this.header.taskId = taskId;
                return this;
            }

            public Builder streaming(@Nullable String streaming) {
                this.header.streaming = streaming;
                return this;
            }

            public RequestHeader build() {
                return header;
            }

        }

    }

    /**
     * Request payload.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class RequestPayload {

        @JsonProperty("model")
        private @Nullable String model;

        @JsonProperty("task_group")
        private @Nullable String taskGroup;

        @JsonProperty("task")
        private @Nullable String task;

        @JsonProperty("function")
        private @Nullable String function;

        @JsonProperty("input")
        private @Nullable RequestPayloadInput input;

        @JsonProperty("parameters")
        private @Nullable RequestPayloadParameters parameters;

        @JsonProperty("output")
        private @Nullable Object output;

        @JsonProperty("resources")
        private @Nullable List<Resource> resources;

        public RequestPayload() {
        }

        public static Builder builder() {
            return new Builder();
        }

        public @Nullable String getModel() {
            return model;
        }

        public void setModel(@Nullable String model) {
            this.model = model;
        }

        public @Nullable String getTaskGroup() {
            return taskGroup;
        }

        public void setTaskGroup(@Nullable String taskGroup) {
            this.taskGroup = taskGroup;
        }

        public @Nullable String getTask() {
            return task;
        }

        public void setTask(@Nullable String task) {
            this.task = task;
        }

        public @Nullable String getFunction() {
            return function;
        }

        public void setFunction(@Nullable String function) {
            this.function = function;
        }

        public @Nullable RequestPayloadInput getInput() {
            return input;
        }

        public void setInput(@Nullable RequestPayloadInput input) {
            this.input = input;
        }

        public @Nullable RequestPayloadParameters getParameters() {
            return parameters;
        }

        public void setParameters(@Nullable RequestPayloadParameters parameters) {
            this.parameters = parameters;
        }

        public @Nullable Object getOutput() {
            return output;
        }

        public void setOutput(@Nullable Object output) {
            this.output = output;
        }

        public @Nullable List<Resource> getResources() {
            return resources;
        }

        public void setResources(@Nullable List<Resource> resources) {
            this.resources = resources;
        }

        public static class Builder {

            private final RequestPayload payload;

            public Builder() {
                this.payload = new RequestPayload();
            }

            public Builder model(@Nullable String model) {
                this.payload.model = model;
                return this;
            }

            public Builder taskGroup(@Nullable String taskGroup) {
                this.payload.taskGroup = taskGroup;
                return this;
            }

            public Builder task(@Nullable String task) {
                this.payload.task = task;
                return this;
            }

            public Builder function(@Nullable String function) {
                this.payload.function = function;
                return this;
            }

            public Builder input(@Nullable RequestPayloadInput input) {
                this.payload.input = input;
                return this;
            }

            public Builder parameters(@Nullable RequestPayloadParameters parameters) {
                this.payload.parameters = parameters;
                return this;
            }

            public Builder output(@Nullable Object output) {
                this.payload.output = output;
                return this;
            }

            public Builder resources(@Nullable List<Resource> resources) {
                this.payload.resources = resources;
                return this;
            }

            public RequestPayload build() {
                return payload;
            }

        }

    }

    /**
     * Request payload input.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class RequestPayloadInput {

        @JsonProperty("text")
        private @Nullable String text;

        public RequestPayloadInput() {
        }

        public static Builder builder() {
            return new Builder();
        }

        public @Nullable String getText() {
            return text;
        }

        public void setText(@Nullable String text) {
            this.text = text;
        }

        public static class Builder {

            private final RequestPayloadInput input;

            public Builder() {
                this.input = new RequestPayloadInput();
            }

            public Builder text(@Nullable String text) {
                this.input.text = text;
                return this;
            }

            public RequestPayloadInput build() {
                return input;
            }

        }

    }

    /**
     * Request payload parameters.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class RequestPayloadParameters {

        @JsonProperty("volume")
        private @Nullable Integer volume;

        @JsonProperty("text_type")
        private @Nullable String textType;

        @JsonProperty("voice")
        private @Nullable String voice;

        @JsonProperty("sample_rate")
        private @Nullable Integer sampleRate;

        @JsonProperty("rate")
        private @Nullable Float rate;

        @JsonProperty("format")
        private @Nullable String format;

        @JsonProperty("pitch")
        private @Nullable Float pitch;

        @JsonProperty("enable_ssml")
        private @Nullable Boolean enableSsml;

        @JsonProperty("bit_rate")
        private @Nullable Integer bitRate;

        @JsonProperty("seed")
        private @Nullable Integer seed;

        @JsonProperty("language_hints")
        private @Nullable List<String> languageHints;

        @JsonProperty("instruction")
        private @Nullable String instruction;

        @JsonProperty("phoneme_timestamp_enabled")
        private @Nullable Boolean phonemeTimestampEnabled;

        @JsonProperty("word_timestamp_enabled")
        private @Nullable Boolean wordTimestampEnabled;

        @JsonProperty("enable_aigc_tag")
        private @Nullable Boolean enableAigcTag;

        @JsonProperty("aigc_propagator")
        private @Nullable String aigcPropagator;

        @JsonProperty("aigc_propagate_id")
        private @Nullable String aigcPropagateId;

        @JsonProperty("vocabulary_id")
        private @Nullable String vocabularyId;

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

        @JsonProperty("disfluency_removal_enabled")
        private @Nullable Boolean disfluencyRemovalEnabled;

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

        public RequestPayloadParameters() {
        }

        public static Builder builder() {
            return new Builder();
        }

        public @Nullable Integer getVolume() {
            return volume;
        }

        public void setVolume(@Nullable Integer volume) {
            this.volume = volume;
        }

        public @Nullable String getTextType() {
            return textType;
        }

        public void setTextType(@Nullable String textType) {
            this.textType = textType;
        }

        public @Nullable String getVoice() {
            return voice;
        }

        public void setVoice(@Nullable String voice) {
            this.voice = voice;
        }

        public @Nullable Integer getSampleRate() {
            return sampleRate;
        }

        public void setSampleRate(@Nullable Integer sampleRate) {
            this.sampleRate = sampleRate;
        }

        public @Nullable Float getRate() {
            return rate;
        }

        public void setRate(@Nullable Float rate) {
            this.rate = rate;
        }

        public @Nullable String getFormat() {
            return format;
        }

        public void setFormat(@Nullable String format) {
            this.format = format;
        }

        public @Nullable Float getPitch() {
            return pitch;
        }

        public void setPitch(@Nullable Float pitch) {
            this.pitch = pitch;
        }

        public @Nullable Boolean getEnableSsml() {
            return enableSsml;
        }

        public void setEnableSsml(@Nullable Boolean enableSsml) {
            this.enableSsml = enableSsml;
        }

        public @Nullable Integer getBitRate() {
            return bitRate;
        }

        public void setBitRate(@Nullable Integer bitRate) {
            this.bitRate = bitRate;
        }

        public @Nullable Integer getSeed() {
            return seed;
        }

        public void setSeed(@Nullable Integer seed) {
            this.seed = seed;
        }

        public @Nullable List<String> getLanguageHints() {
            return languageHints;
        }

        public void setLanguageHints(@Nullable List<String> languageHints) {
            this.languageHints = languageHints;
        }

        public @Nullable String getInstruction() {
            return instruction;
        }

        public void setInstruction(@Nullable String instruction) {
            this.instruction = instruction;
        }

        public @Nullable Boolean getPhonemeTimestampEnabled() {
            return phonemeTimestampEnabled;
        }

        public void setPhonemeTimestampEnabled(@Nullable Boolean phonemeTimestampEnabled) {
            this.phonemeTimestampEnabled = phonemeTimestampEnabled;
        }

        public @Nullable Boolean getWordTimestampEnabled() {
            return wordTimestampEnabled;
        }

        public void setWordTimestampEnabled(@Nullable Boolean wordTimestampEnabled) {
            this.wordTimestampEnabled = wordTimestampEnabled;
        }

        public @Nullable Boolean getEnableAigcTag() {
            return enableAigcTag;
        }

        public void setEnableAigcTag(@Nullable Boolean enableAigcTag) {
            this.enableAigcTag = enableAigcTag;
        }

        public @Nullable String getAigcPropagator() {
            return aigcPropagator;
        }

        public void setAigcPropagator(@Nullable String aigcPropagator) {
            this.aigcPropagator = aigcPropagator;
        }

        public @Nullable String getAigcPropagateId() {
            return aigcPropagateId;
        }

        public void setAigcPropagateId(@Nullable String aigcPropagateId) {
            this.aigcPropagateId = aigcPropagateId;
        }

        public @Nullable String getVocabularyId() {
            return vocabularyId;
        }

        public void setVocabularyId(@Nullable String vocabularyId) {
            this.vocabularyId = vocabularyId;
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

        public @Nullable Integer getMaxEndSilence() {
            return maxEndSilence;
        }

        public void setMaxEndSilence(@Nullable Integer maxEndSilence) {
            this.maxEndSilence = maxEndSilence;
        }

        public @Nullable Boolean getDisfluencyRemovalEnabled() {
            return disfluencyRemovalEnabled;
        }

        public void setDisfluencyRemovalEnabled(@Nullable Boolean disfluencyRemovalEnabled) {
            this.disfluencyRemovalEnabled = disfluencyRemovalEnabled;
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

        public static class Builder {

            private final RequestPayloadParameters parameters;

            public Builder() {
                this.parameters = new RequestPayloadParameters();
            }

            public Builder volume(@Nullable Integer volume) {
                this.parameters.volume = volume;
                return this;
            }

            public Builder textType(@Nullable String textType) {
                this.parameters.textType = textType;
                return this;
            }

            public Builder voice(@Nullable String voice) {
                this.parameters.voice = voice;
                return this;
            }

            public Builder sampleRate(@Nullable Integer sampleRate) {
                this.parameters.sampleRate = sampleRate;
                return this;
            }

            public Builder rate(@Nullable Float rate) {
                this.parameters.rate = rate;
                return this;
            }

            public Builder format(@Nullable String format) {
                this.parameters.format = format;
                return this;
            }

            public Builder pitch(@Nullable Float pitch) {
                this.parameters.pitch = pitch;
                return this;
            }

            public Builder enableSsml(@Nullable Boolean enableSsml) {
                this.parameters.enableSsml = enableSsml;
                return this;
            }

            public Builder bitRate(@Nullable Integer bitRate) {
                this.parameters.bitRate = bitRate;
                return this;
            }

            public Builder seed(@Nullable Integer seed) {
                this.parameters.seed = seed;
                return this;
            }

            public Builder languageHints(@Nullable List<String> languageHints) {
                this.parameters.languageHints = languageHints;
                return this;
            }

            public Builder instruction(@Nullable String instruction) {
                this.parameters.instruction = instruction;
                return this;
            }

            public Builder phonemeTimestampEnabled(@Nullable Boolean phonemeTimestampEnabled) {
                this.parameters.phonemeTimestampEnabled = phonemeTimestampEnabled;
                return this;
            }

            public Builder wordTimestampEnabled(@Nullable Boolean wordTimestampEnabled) {
                this.parameters.wordTimestampEnabled = wordTimestampEnabled;
                return this;
            }

            public Builder enableAigcTag(@Nullable Boolean enableAigcTag) {
                this.parameters.enableAigcTag = enableAigcTag;
                return this;
            }

            public Builder aigcPropagator(@Nullable String aigcPropagator) {
                this.parameters.aigcPropagator = aigcPropagator;
                return this;
            }

            public Builder aigcPropagateId(@Nullable String aigcPropagateId) {
                this.parameters.aigcPropagateId = aigcPropagateId;
                return this;
            }

            public Builder vocabularyId(@Nullable String vocabularyId) {
                this.parameters.vocabularyId = vocabularyId;
                return this;
            }

            public Builder sourceLanguage(@Nullable String sourceLanguage) {
                this.parameters.sourceLanguage = sourceLanguage;
                return this;
            }

            public Builder transcriptionEnabled(@Nullable Boolean transcriptionEnabled) {
                this.parameters.transcriptionEnabled = transcriptionEnabled;
                return this;
            }

            public Builder translationEnabled(@Nullable Boolean translationEnabled) {
                this.parameters.translationEnabled = translationEnabled;
                return this;
            }

            public Builder translationTargetLanguages(@Nullable List<String> translationTargetLanguages) {
                this.parameters.translationTargetLanguages = translationTargetLanguages;
                return this;
            }

            public Builder maxEndSilence(@Nullable Integer maxEndSilence) {
                this.parameters.maxEndSilence = maxEndSilence;
                return this;
            }

            public Builder disfluencyRemovalEnabled(@Nullable Boolean disfluencyRemovalEnabled) {
                this.parameters.disfluencyRemovalEnabled = disfluencyRemovalEnabled;
                return this;
            }

            public Builder semanticPunctuationEnabled(@Nullable Boolean semanticPunctuationEnabled) {
                this.parameters.semanticPunctuationEnabled = semanticPunctuationEnabled;
                return this;
            }

            public Builder maxSentenceSilence(@Nullable Integer maxSentenceSilence) {
                this.parameters.maxSentenceSilence = maxSentenceSilence;
                return this;
            }

            public Builder multiThresholdModeEnabled(@Nullable Boolean multiThresholdModeEnabled) {
                this.parameters.multiThresholdModeEnabled = multiThresholdModeEnabled;
                return this;
            }

            public Builder punctuationPredictionEnabled(@Nullable Boolean punctuationPredictionEnabled) {
                this.parameters.punctuationPredictionEnabled = punctuationPredictionEnabled;
                return this;
            }

            public Builder heartbeat(@Nullable Boolean heartbeat) {
                this.parameters.heartbeat = heartbeat;
                return this;
            }

            public Builder inverseTextNormalizationEnabled(@Nullable Boolean inverseTextNormalizationEnabled) {
                this.parameters.inverseTextNormalizationEnabled = inverseTextNormalizationEnabled;
                return this;
            }

            public RequestPayloadParameters build() {
                return parameters;
            }

        }

        public static RequestPayloadParameters speechOptionsConvertReq(DashScopeAudioSpeechOptions options) {
            return RequestPayloadParameters.builder()
                    .volume(options.getVolume())
                    .textType(options.getTextType())
                    .voice(options.getVoice())
                    .sampleRate(options.getSampleRate())
                    .rate(options.getRate())
                    .format(options.getFormat())
                    .pitch(options.getPitch())
                    .volume(options.getVolume())
                    .enableSsml(options.getEnableSsml())
                    .bitRate(options.getBitRate())
                    .seed(options.getSeed())
                    .languageHints(options.getLanguageHints())
                    .instruction(options.getInstruction())
                    .phonemeTimestampEnabled(options.getPhonemeTimestampEnabled())
                    .wordTimestampEnabled(options.getWordTimestampEnabled())
                    .enableAigcTag(options.getEnableAigcTag())
                    .aigcPropagator(options.getAigcPropagator())
                    .aigcPropagateId(options.getAigcPropagateId())
                    .build();
        }

    }

}
