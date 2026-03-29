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
import org.jspecify.annotations.Nullable;
import org.springframework.ai.audio.tts.TextToSpeechOptions;

import java.util.ArrayList;
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
    private @Nullable String model;

	@JsonProperty("text_type")
	private String textType = TextType.PLAIN_TEXT.getValue();

    @JsonProperty("voice")
    private String voice = "longanyang";

    @JsonProperty("format")
    private @Nullable String format;

    @JsonProperty("sample_rate")
    private @Nullable Integer sampleRate;

    @JsonProperty("volume")
    private @Nullable Integer volume;

    @JsonProperty("rate")
    private @Nullable Float rate;

    @JsonProperty("pitch")
    private @Nullable Float pitch;

    @JsonProperty("enable_ssml")
    private @Nullable Boolean enableSsml;

    @JsonProperty("bit_rate")
    private @Nullable Integer bitRate;

    @JsonProperty("speed")
    private @Nullable Double speed;

    @JsonProperty("seed")
    private @Nullable Integer seed;

    @JsonProperty("word_timestamp_enabled")
    private @Nullable Boolean wordTimestampEnabled;

    @JsonProperty("phoneme_timestamp_enabled")
    private @Nullable Boolean phonemeTimestampEnabled;

	@JsonProperty("language_hints")
	private @Nullable List<String> languageHints;

    @JsonProperty("instruction")
    private @Nullable String instruction;

    @JsonProperty("optimize_instructions")
    private @Nullable Boolean optimizeInstructions;

    @JsonProperty("enable_aigc_tag")
    private @Nullable Boolean enableAigcTag;

    @JsonProperty("aigc_propagator")
    private @Nullable String aigcPropagator;

    @JsonProperty("aigc_propagate_id")
    private @Nullable String aigcPropagateId;

    @JsonProperty("language_type")
    private @Nullable String languageType;

    @Override
    public @Nullable String getModel() {
        return model;
    }

    public void setModel(@Nullable String model) {
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

    public @Nullable Boolean getEnableAigcTag() {
        return enableAigcTag;
    }

    public void setEnableAigcTag(Boolean enableAigcTag) {
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

    public @Nullable Integer getSampleRate() {
        return sampleRate;
    }

    public void setSampleRate(@Nullable Integer sampleRate) {
        this.sampleRate = sampleRate;
    }

    public void setFormat(@Nullable String format) {
        this.format = format;
    }

    public @Nullable Boolean getWordTimestampEnabled() {
        return wordTimestampEnabled;
    }

    public void setWordTimestampEnabled(@Nullable Boolean wordTimestampEnabled) {
        this.wordTimestampEnabled = wordTimestampEnabled;
    }

    public @Nullable Boolean getPhonemeTimestampEnabled() {
        return phonemeTimestampEnabled;
    }

    public void setPhonemeTimestampEnabled(@Nullable Boolean phonemeTimestampEnabled) {
        this.phonemeTimestampEnabled = phonemeTimestampEnabled;
    }

    public @Nullable Integer getVolume() {
        return volume;
    }

    public void setVolume(@Nullable Integer volume) {
        this.volume = volume;
    }

    @Override
    public @Nullable Double getSpeed() {
        return speed;
    }

    @Override
    public <T extends TextToSpeechOptions> T copy() {
        DashScopeAudioSpeechOptions copied = DashScopeAudioSpeechOptions.builder()
                .model(this.model)
                .textType(this.textType)
                .voice(this.voice)
                .format(this.format)
                .sampleRate(this.sampleRate)
                .volume(this.volume)
                .rate(this.rate)
                .pitch(this.pitch)
                .enableSsml(this.enableSsml)
                .bitRate(this.bitRate)
                .speed(this.speed)
                .seed(this.seed)
                .wordTimestampEnabled(this.wordTimestampEnabled)
                .phonemeTimestampEnabled(this.phonemeTimestampEnabled)
                .languageHints(this.languageHints == null ? null : new ArrayList<>(this.languageHints))
                .instruction(this.instruction)
                .optimizeInstructions(this.optimizeInstructions)
                .enableAigcTag(this.enableAigcTag)
                .aigcPropagator(this.aigcPropagator)
                .aigcPropagateId(this.aigcPropagateId)
                .languageType(this.languageType)
                .build();
        return (T) copied;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }

    public @Nullable Float getRate() {
        return rate;
    }

    public void setRate(@Nullable Float rate) {
        this.rate = rate;
    }

    public void setResponseFormat(@Nullable String format) {
        this.format = format;
    }

    @Override
    public @Nullable String getFormat() {
        return format;
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

    public @Nullable Boolean getOptimizeInstructions() {
        return optimizeInstructions;
    }

    public void setOptimizeInstructions(@Nullable Boolean optimizeInstructions) {
        this.optimizeInstructions = optimizeInstructions;
    }

    public @Nullable String getLanguageType() {
        return languageType;
    }

    public void setLanguageType(@Nullable String languageType) {
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

        public Builder model(@Nullable String model) {
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

        public Builder format(@Nullable String format) {
            this.options.format = format;
            return this;
        }

        public Builder sampleRate(@Nullable Integer sampleRate) {
            this.options.sampleRate = sampleRate;
            return this;
        }

        public Builder volume(@Nullable Integer volume) {
            this.options.volume = volume;
            return this;
        }

        public Builder rate(@Nullable Float rate) {
            this.options.rate = rate;
            return this;
        }

        public Builder pitch(@Nullable Float pitch) {
            this.options.pitch = pitch;
            return this;
        }

        public Builder enableSsml(@Nullable Boolean enableSsml) {
            this.options.enableSsml = enableSsml;
            return this;
        }

        public Builder bitRate(@Nullable Integer bitRate) {
            this.options.bitRate = bitRate;
            return this;
        }

        public Builder speed(@Nullable Double speed) {
            this.options.speed = speed;
            return this;
        }

        public Builder seed(@Nullable Integer seed) {
            this.options.seed = seed;
            return this;
        }

        public Builder wordTimestampEnabled(@Nullable Boolean wordTimestampEnabled) {
            this.options.wordTimestampEnabled = wordTimestampEnabled;
            return this;
        }

        public Builder phonemeTimestampEnabled(@Nullable Boolean phonemeTimestampEnabled) {
            this.options.phonemeTimestampEnabled = phonemeTimestampEnabled;
            return this;
        }

        public Builder languageHints(@Nullable List<String> languageHints) {
            this.options.languageHints = languageHints;
            return this;
        }

        public Builder instruction(@Nullable String instruction) {
            this.options.instruction = instruction;
            return this;
        }

        public Builder optimizeInstructions(@Nullable Boolean optimizeInstructions) {
            this.options.optimizeInstructions = optimizeInstructions;
            return this;
        }

        public Builder enableAigcTag(@Nullable Boolean enableAigcTag) {
            this.options.enableAigcTag = enableAigcTag;
            return this;
        }

        public Builder aigcPropagator(@Nullable String aigcPropagator) {
            this.options.aigcPropagator = aigcPropagator;
            return this;
        }

        public Builder aigcPropagateId(@Nullable String aigcPropagateId) {
            this.options.aigcPropagateId = aigcPropagateId;
            return this;
        }

        public Builder languageType(@Nullable String languageType) {
            this.options.languageType = languageType;
            return this;
        }

        public DashScopeAudioSpeechOptions build() {
            return this.options;
        }

    }

}
