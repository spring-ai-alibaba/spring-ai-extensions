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
    private RequestHeader header;

    @JsonProperty("payload")
    private RequestPayload payload;

    public WebSocketRequest(RequestHeader header, RequestPayload payload) {
        this.header = header;
        this.payload = payload;
    }

    public RequestHeader getHeader() {
        return header;
    }

    public void setHeader(RequestHeader header) {
        this.header = header;
    }

    public RequestPayload getPayload() {
        return payload;
    }

    public void setPayload(RequestPayload payload) {
        this.payload = payload;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private RequestHeader header;

        private RequestPayload payload;

        public Builder header(RequestHeader header) {
            this.header = header;
            return this;
        }

        public Builder payload(RequestPayload payload) {
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
        private DashScopeWebSocketClient.EventType action;

        @JsonProperty("task_id")
        private String taskId;

        @JsonProperty("streaming")
        private String streaming;

        public RequestHeader() {
        }

        public static Builder builder() {
            return new Builder();
        }

        public DashScopeWebSocketClient.EventType getAction() {
            return action;
        }

        public void setAction(DashScopeWebSocketClient.EventType action) {
            this.action = action;
        }

        public String getTaskId() {
            return taskId;
        }

        public void setTaskId(String taskId) {
            this.taskId = taskId;
        }

        public String getStreaming() {
            return streaming;
        }

        public void setStreaming(String streaming) {
            this.streaming = streaming;
        }

        public static class Builder {

            private final RequestHeader header;

            public Builder() {
                this.header = new RequestHeader();
            }

            public Builder action(DashScopeWebSocketClient.EventType action) {
                this.header.action = action;
                return this;
            }

            public Builder taskId(String taskId) {
                this.header.taskId = taskId;
                return this;
            }

            public Builder streaming(String streaming) {
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
        private String model;

        @JsonProperty("task_group")
        private String taskGroup;

        @JsonProperty("task")
        private String task;

        @JsonProperty("function")
        private String function;

        @JsonProperty("input")
        private RequestPayloadInput input;

        @JsonProperty("parameters")
        private RequestPayloadParameters parameters;

        @JsonProperty("output")
        private Object output;

        @JsonProperty("resources")
        private List<Resource> resources;

        public RequestPayload() {
        }

        public static Builder builder() {
            return new Builder();
        }

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }

        public String getTaskGroup() {
            return taskGroup;
        }

        public void setTaskGroup(String taskGroup) {
            this.taskGroup = taskGroup;
        }

        public String getTask() {
            return task;
        }

        public void setTask(String task) {
            this.task = task;
        }

        public String getFunction() {
            return function;
        }

        public void setFunction(String function) {
            this.function = function;
        }

        public RequestPayloadInput getInput() {
            return input;
        }

        public void setInput(RequestPayloadInput input) {
            this.input = input;
        }

        public RequestPayloadParameters getParameters() {
            return parameters;
        }

        public void setParameters(RequestPayloadParameters parameters) {
            this.parameters = parameters;
        }

        public Object getOutput() {
            return output;
        }

        public void setOutput(Object output) {
            this.output = output;
        }

        public List<Resource> getResources() {
            return resources;
        }

        public void setResources(List<Resource> resources) {
            this.resources = resources;
        }

        public static class Builder {

            private final RequestPayload payload;

            public Builder() {
                this.payload = new RequestPayload();
            }

            public Builder model(String model) {
                this.payload.model = model;
                return this;
            }

            public Builder taskGroup(String taskGroup) {
                this.payload.taskGroup = taskGroup;
                return this;
            }

            public Builder task(String task) {
                this.payload.task = task;
                return this;
            }

            public Builder function(String function) {
                this.payload.function = function;
                return this;
            }

            public Builder input(RequestPayloadInput input) {
                this.payload.input = input;
                return this;
            }

            public Builder parameters(RequestPayloadParameters parameters) {
                this.payload.parameters = parameters;
                return this;
            }

            public Builder output(Object output) {
                this.payload.output = output;
                return this;
            }

            public Builder resources(List<Resource> resources) {
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
        private String text;

        public RequestPayloadInput() {
        }

        public static Builder builder() {
            return new Builder();
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public static class Builder {

            private final RequestPayloadInput input;

            public Builder() {
                this.input = new RequestPayloadInput();
            }

            public Builder text(String text) {
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
        private Integer volume;

        @JsonProperty("text_type")
        private String textType;

        @JsonProperty("voice")
        private String voice;

        @JsonProperty("sample_rate")
        private Integer sampleRate;

        @JsonProperty("rate")
        private Float rate;

        @JsonProperty("format")
        private String format;

        @JsonProperty("pitch")
        private Float pitch;

        @JsonProperty("enable_ssml")
        private Boolean enableSsml;

        @JsonProperty("bit_rate")
        private Integer bitRate;

        @JsonProperty("seed")
        private Integer seed;

        @JsonProperty("language_hints")
        private List<String> languageHints;

        @JsonProperty("instruction")
        private String instruction;

        @JsonProperty("phoneme_timestamp_enabled")
        private Boolean phonemeTimestampEnabled;

        @JsonProperty("word_timestamp_enabled")
        private Boolean wordTimestampEnabled;

        @JsonProperty("enable_aigc_tag")
        private Boolean enableAigcTag;

        @JsonProperty("aigc_propagator")
        private String aigcPropagator;

        @JsonProperty("aigc_propagate_id")
        private String aigcPropagateId;

        @JsonProperty("vocabulary_id")
        private String vocabularyId;

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

        @JsonProperty("disfluency_removal_enabled")
        private Boolean disfluencyRemovalEnabled;

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

        public RequestPayloadParameters() {
        }

        public static Builder builder() {
            return new Builder();
        }

        public Integer getVolume() {
            return volume;
        }

        public void setVolume(Integer volume) {
            this.volume = volume;
        }

        public String getTextType() {
            return textType;
        }

        public void setTextType(String textType) {
            this.textType = textType;
        }

        public String getVoice() {
            return voice;
        }

        public void setVoice(String voice) {
            this.voice = voice;
        }

        public Integer getSampleRate() {
            return sampleRate;
        }

        public void setSampleRate(Integer sampleRate) {
            this.sampleRate = sampleRate;
        }

        public Float getRate() {
            return rate;
        }

        public void setRate(Float rate) {
            this.rate = rate;
        }

        public String getFormat() {
            return format;
        }

        public void setFormat(String format) {
            this.format = format;
        }

        public Float getPitch() {
            return pitch;
        }

        public void setPitch(Float pitch) {
            this.pitch = pitch;
        }

        public Boolean getEnableSsml() {
            return enableSsml;
        }

        public void setEnableSsml(Boolean enableSsml) {
            this.enableSsml = enableSsml;
        }

        public Integer getBitRate() {
            return bitRate;
        }

        public void setBitRate(Integer bitRate) {
            this.bitRate = bitRate;
        }

        public Integer getSeed() {
            return seed;
        }

        public void setSeed(Integer seed) {
            this.seed = seed;
        }

        public List<String> getLanguageHints() {
            return languageHints;
        }

        public void setLanguageHints(List<String> languageHints) {
            this.languageHints = languageHints;
        }

        public String getInstruction() {
            return instruction;
        }

        public void setInstruction(String instruction) {
            this.instruction = instruction;
        }

        public Boolean getPhonemeTimestampEnabled() {
            return phonemeTimestampEnabled;
        }

        public void setPhonemeTimestampEnabled(Boolean phonemeTimestampEnabled) {
            this.phonemeTimestampEnabled = phonemeTimestampEnabled;
        }

        public Boolean getWordTimestampEnabled() {
            return wordTimestampEnabled;
        }

        public void setWordTimestampEnabled(Boolean wordTimestampEnabled) {
            this.wordTimestampEnabled = wordTimestampEnabled;
        }

        public Boolean getEnableAigcTag() {
            return enableAigcTag;
        }

        public void setEnableAigcTag(Boolean enableAigcTag) {
            this.enableAigcTag = enableAigcTag;
        }

        public String getAigcPropagator() {
            return aigcPropagator;
        }

        public void setAigcPropagator(String aigcPropagator) {
            this.aigcPropagator = aigcPropagator;
        }

        public String getAigcPropagateId() {
            return aigcPropagateId;
        }

        public void setAigcPropagateId(String aigcPropagateId) {
            this.aigcPropagateId = aigcPropagateId;
        }

        public String getVocabularyId() {
            return vocabularyId;
        }

        public void setVocabularyId(String vocabularyId) {
            this.vocabularyId = vocabularyId;
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

        public Boolean getDisfluencyRemovalEnabled() {
            return disfluencyRemovalEnabled;
        }

        public void setDisfluencyRemovalEnabled(Boolean disfluencyRemovalEnabled) {
            this.disfluencyRemovalEnabled = disfluencyRemovalEnabled;
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

        public static class Builder {

            private final RequestPayloadParameters parameters;

            public Builder() {
                this.parameters = new RequestPayloadParameters();
            }

            public Builder volume(Integer volume) {
                this.parameters.volume = volume;
                return this;
            }

            public Builder textType(String textType) {
                this.parameters.textType = textType;
                return this;
            }

            public Builder voice(String voice) {
                this.parameters.voice = voice;
                return this;
            }

            public Builder sampleRate(Integer sampleRate) {
                this.parameters.sampleRate = sampleRate;
                return this;
            }

            public Builder rate(Float rate) {
                this.parameters.rate = rate;
                return this;
            }

            public Builder format(String format) {
                this.parameters.format = format;
                return this;
            }

            public Builder pitch(Float pitch) {
                this.parameters.pitch = pitch;
                return this;
            }

            public Builder enableSsml(Boolean enableSsml) {
                this.parameters.enableSsml = enableSsml;
                return this;
            }

            public Builder bitRate(Integer bitRate) {
                this.parameters.bitRate = bitRate;
                return this;
            }

            public Builder seed(Integer seed) {
                this.parameters.seed = seed;
                return this;
            }

            public Builder languageHints(List<String> languageHints) {
                this.parameters.languageHints = languageHints;
                return this;
            }

            public Builder instruction(String instruction) {
                this.parameters.instruction = instruction;
                return this;
            }

            public Builder phonemeTimestampEnabled(Boolean phonemeTimestampEnabled) {
                this.parameters.phonemeTimestampEnabled = phonemeTimestampEnabled;
                return this;
            }

            public Builder wordTimestampEnabled(Boolean wordTimestampEnabled) {
                this.parameters.wordTimestampEnabled = wordTimestampEnabled;
                return this;
            }

            public Builder enableAigcTag(Boolean enableAigcTag) {
                this.parameters.enableAigcTag = enableAigcTag;
                return this;
            }

            public Builder aigcPropagator(String aigcPropagator) {
                this.parameters.aigcPropagator = aigcPropagator;
                return this;
            }

            public Builder aigcPropagateId(String aigcPropagateId) {
                this.parameters.aigcPropagateId = aigcPropagateId;
                return this;
            }

            public Builder vocabularyId(String vocabularyId) {
                this.parameters.vocabularyId = vocabularyId;
                return this;
            }

            public Builder sourceLanguage(String sourceLanguage) {
                this.parameters.sourceLanguage = sourceLanguage;
                return this;
            }

            public Builder transcriptionEnabled(Boolean transcriptionEnabled) {
                this.parameters.transcriptionEnabled = transcriptionEnabled;
                return this;
            }

            public Builder translationEnabled(Boolean translationEnabled) {
                this.parameters.translationEnabled = translationEnabled;
                return this;
            }

            public Builder translationTargetLanguages(List<String> translationTargetLanguages) {
                this.parameters.translationTargetLanguages = translationTargetLanguages;
                return this;
            }

            public Builder maxEndSilence(Integer maxEndSilence) {
                this.parameters.maxEndSilence = maxEndSilence;
                return this;
            }

            public Builder disfluencyRemovalEnabled(Boolean disfluencyRemovalEnabled) {
                this.parameters.disfluencyRemovalEnabled = disfluencyRemovalEnabled;
                return this;
            }

            public Builder semanticPunctuationEnabled(Boolean semanticPunctuationEnabled) {
                this.parameters.semanticPunctuationEnabled = semanticPunctuationEnabled;
                return this;
            }

            public Builder maxSentenceSilence(Integer maxSentenceSilence) {
                this.parameters.maxSentenceSilence = maxSentenceSilence;
                return this;
            }

            public Builder multiThresholdModeEnabled(Boolean multiThresholdModeEnabled) {
                this.parameters.multiThresholdModeEnabled = multiThresholdModeEnabled;
                return this;
            }

            public Builder punctuationPredictionEnabled(Boolean punctuationPredictionEnabled) {
                this.parameters.punctuationPredictionEnabled = punctuationPredictionEnabled;
                return this;
            }

            public Builder heartbeat(Boolean heartbeat) {
                this.parameters.heartbeat = heartbeat;
                return this;
            }

            public Builder inverseTextNormalizationEnabled(Boolean inverseTextNormalizationEnabled) {
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
