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
package com.alibaba.cloud.ai.dashscope.audio.tts;

import com.alibaba.cloud.ai.dashscope.audio.AudioCommonType.TextType;
import com.alibaba.cloud.ai.dashscope.spec.DashScopeModel.AudioModel;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.ai.audio.tts.TextToSpeechOptions;

import java.util.List;

/**
 * DashScope Audio Speech Options.
 *
 * @author kevinlin09
 * @author xuguan
 * @author yingzi
 */
public class DashScopeAudioSpeechOptions implements TextToSpeechOptions {

    public static final String DEFAULT_MODEL = AudioModel.SAMBERT_ZHICHU_V1.getValue();

    @JsonProperty("model")
    private String model;

	@JsonProperty("text_type")
	private String textType = TextType.PLAIN_TEXT.getValue();

    @JsonProperty("voice")
    private String voice = "longanyang";

    @JsonProperty("format")
    private String format;

    @JsonProperty("sample_rate")
    private Integer sampleRate;

    @JsonProperty("volume")
    private Integer volume;

    @JsonProperty("rate")
    private Float rate;

    @JsonProperty("pitch")
    private Float pitch;

    @JsonProperty("enable_ssml")
    private Boolean enableSsml;

    @JsonProperty("bit_rate")
    private Integer bitRate;

    @JsonProperty("speed")
    private Double speed;

    @JsonProperty("seed")
    private Integer seed;

    @JsonProperty("word_timestamp_enabled")
    private Boolean wordTimestampEnabled;

    @JsonProperty("phoneme_timestamp_enabled")
    private Boolean phonemeTimestampEnabled;

	@JsonProperty("language_hints")
	private List<String> languageHints;

    @JsonProperty("instruction")
    private String instruction;

    @JsonProperty("enable_aigc_tag")
    private Boolean enableAigcTag;

    @JsonProperty("aigc_propagator")
    private String aigcPropagator;

    @JsonProperty("aigc_propagate_id")
    private String aigcPropagateId;

    @JsonProperty("language_type")
    private String languageType;

    @Override
    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    @Override
    public String getVoice() {
        return voice;
    }

    public void setVoice(String voice) {
        this.voice = voice;
    }

    public String getTextType() {
        return textType;
    }

    public void setTextType(String textType) {
        this.textType = textType;
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

    public Integer getSampleRate() {
        return sampleRate;
    }

    public void setSampleRate(Integer sampleRate) {
        this.sampleRate = sampleRate;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public Boolean getWordTimestampEnabled() {
        return wordTimestampEnabled;
    }

    public void setWordTimestampEnabled(Boolean wordTimestampEnabled) {
        this.wordTimestampEnabled = wordTimestampEnabled;
    }

    public Boolean getPhonemeTimestampEnabled() {
        return phonemeTimestampEnabled;
    }

    public void setPhonemeTimestampEnabled(Boolean phonemeTimestampEnabled) {
        this.phonemeTimestampEnabled = phonemeTimestampEnabled;
    }

    public Integer getVolume() {
        return volume;
    }

    public void setVolume(Integer volume) {
        this.volume = volume;
    }

    @Override
    public Double getSpeed() {
        return speed;
    }

    @Override
    public <T extends TextToSpeechOptions> T copy() {
        return null;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }

    public Float getRate() {
        return rate;
    }

    public void setRate(Float rate) {
        this.rate = rate;
    }

    public void setResponseFormat(String format) {
        this.format = format;
    }

    @Override
    public String getFormat() {
        return format;
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

    public String getLanguageType() {
        return languageType;
    }

    public void setLanguageType(String languageType) {
        this.languageType = languageType;
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder for DashScopeAudioSpeechOptions.
     */
    public static class Builder {

        private final DashScopeAudioSpeechOptions options;

        public Builder() {
            this.options = new DashScopeAudioSpeechOptions();
        }

        public Builder model(String model) {
            this.options.model = model;
            return this;
        }

        public Builder textType(String textType) {
            this.options.textType = textType;
            return this;
        }

        public Builder voice(String voice) {
            this.options.voice = voice;
            return this;
        }

        public Builder format(String format) {
            this.options.format = format;
            return this;
        }

        public Builder sampleRate(Integer sampleRate) {
            this.options.sampleRate = sampleRate;
            return this;
        }

        public Builder volume(Integer volume) {
            this.options.volume = volume;
            return this;
        }

        public Builder rate(Float rate) {
            this.options.rate = rate;
            return this;
        }

        public Builder pitch(Float pitch) {
            this.options.pitch = pitch;
            return this;
        }

        public Builder enableSsml(Boolean enableSsml) {
            this.options.enableSsml = enableSsml;
            return this;
        }

        public Builder bitRate(Integer bitRate) {
            this.options.bitRate = bitRate;
            return this;
        }

        public Builder speed(Double speed) {
            this.options.speed = speed;
            return this;
        }

        public Builder seed(Integer seed) {
            this.options.seed = seed;
            return this;
        }

        public Builder wordTimestampEnabled(Boolean wordTimestampEnabled) {
            this.options.wordTimestampEnabled = wordTimestampEnabled;
            return this;
        }

        public Builder phonemeTimestampEnabled(Boolean phonemeTimestampEnabled) {
            this.options.phonemeTimestampEnabled = phonemeTimestampEnabled;
            return this;
        }

        public Builder languageHints(List<String> languageHints) {
            this.options.languageHints = languageHints;
            return this;
        }

        public Builder instruction(String instruction) {
            this.options.instruction = instruction;
            return this;
        }

        public Builder enableAigcTag(Boolean enableAigcTag) {
            this.options.enableAigcTag = enableAigcTag;
            return this;
        }

        public Builder aigcPropagator(String aigcPropagator) {
            this.options.aigcPropagator = aigcPropagator;
            return this;
        }

        public Builder aigcPropagateId(String aigcPropagateId) {
            this.options.aigcPropagateId = aigcPropagateId;
            return this;
        }

        public Builder languageType(String languageType) {
            this.options.languageType = languageType;
            return this;
        }

        public DashScopeAudioSpeechOptions build() {
            return this.options;
        }

    }

}
