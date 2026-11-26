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

import com.alibaba.cloud.ai.dashscope.audio.tts.DashScopeAudioSpeechOptions;
import com.alibaba.cloud.ai.dashscope.common.DashScopeAudioApiConstants;
import org.jspecify.annotations.Nullable;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.DeprecatedConfigurationProperty;

/**
 * DashScope audio speech auto-configuration properties.
 *
 * @author kevinlin09
 * @author yingzi
 * @author xuguan
 */
@ConfigurationProperties(DashScopeAudioSpeechProperties.CONFIG_PREFIX)
public class DashScopeAudioSpeechProperties extends DashScopeParentProperties {

    /**
     * Spring AI Alibaba configuration prefix.
     */
    public static final String CONFIG_PREFIX = "spring.ai.dashscope.audio.speech";

    private String websocketUrl = DashScopeAudioApiConstants.DEFAULT_WEBSOCKET_URL;

    private @Nullable String model;

    private @Nullable String textType;

    private @Nullable String voice;

    private @Nullable String format;

    private @Nullable Integer sampleRate;

    private @Nullable Integer volume;

    private @Nullable Float rate;

    private @Nullable Float pitch;

    private @Nullable Boolean enableSsml;

    private @Nullable Integer bitRate;

    private @Nullable Double speed;

    private @Nullable Integer seed;

    private @Nullable Boolean wordTimestampEnabled;

    private @Nullable Boolean phonemeTimestampEnabled;

    private @Nullable List<String> languageHints;

    private @Nullable String instruction;

    private @Nullable Boolean optimizeInstructions;

    private @Nullable Boolean enableAigcTag;

    private @Nullable String aigcPropagator;

    private @Nullable String aigcPropagateId;

    private @Nullable String languageType;

    public @Nullable String getModel() {
        return this.model;
    }

    public void setModel(@Nullable String model) {
        this.model = model;
    }

    public @Nullable String getTextType() {
        return this.textType;
    }

    public void setTextType(@Nullable String textType) {
        this.textType = textType;
    }

    public @Nullable String getVoice() {
        return this.voice;
    }

    public void setVoice(@Nullable String voice) {
        this.voice = voice;
    }

    public @Nullable String getFormat() {
        return this.format;
    }

    public void setFormat(@Nullable String format) {
        this.format = format;
    }

    public @Nullable Integer getSampleRate() {
        return this.sampleRate;
    }

    public void setSampleRate(@Nullable Integer sampleRate) {
        this.sampleRate = sampleRate;
    }

    public @Nullable Integer getVolume() {
        return this.volume;
    }

    public void setVolume(@Nullable Integer volume) {
        this.volume = volume;
    }

    public @Nullable Float getRate() {
        return this.rate;
    }

    public void setRate(@Nullable Float rate) {
        this.rate = rate;
    }

    public @Nullable Float getPitch() {
        return this.pitch;
    }

    public void setPitch(@Nullable Float pitch) {
        this.pitch = pitch;
    }

    public @Nullable Boolean getEnableSsml() {
        return this.enableSsml;
    }

    public void setEnableSsml(@Nullable Boolean enableSsml) {
        this.enableSsml = enableSsml;
    }

    public @Nullable Integer getBitRate() {
        return this.bitRate;
    }

    public void setBitRate(@Nullable Integer bitRate) {
        this.bitRate = bitRate;
    }

    public @Nullable Double getSpeed() {
        return this.speed;
    }

    public void setSpeed(@Nullable Double speed) {
        this.speed = speed;
    }

    public @Nullable Integer getSeed() {
        return this.seed;
    }

    public void setSeed(@Nullable Integer seed) {
        this.seed = seed;
    }

    public @Nullable Boolean getWordTimestampEnabled() {
        return this.wordTimestampEnabled;
    }

    public void setWordTimestampEnabled(@Nullable Boolean wordTimestampEnabled) {
        this.wordTimestampEnabled = wordTimestampEnabled;
    }

    public @Nullable Boolean getPhonemeTimestampEnabled() {
        return this.phonemeTimestampEnabled;
    }

    public void setPhonemeTimestampEnabled(@Nullable Boolean phonemeTimestampEnabled) {
        this.phonemeTimestampEnabled = phonemeTimestampEnabled;
    }

    public @Nullable List<String> getLanguageHints() {
        return this.languageHints;
    }

    public void setLanguageHints(@Nullable List<String> languageHints) {
        this.languageHints = languageHints;
    }

    public @Nullable String getInstruction() {
        return this.instruction;
    }

    public void setInstruction(@Nullable String instruction) {
        this.instruction = instruction;
    }

    public @Nullable Boolean getOptimizeInstructions() {
        return this.optimizeInstructions;
    }

    public void setOptimizeInstructions(@Nullable Boolean optimizeInstructions) {
        this.optimizeInstructions = optimizeInstructions;
    }

    public @Nullable Boolean getEnableAigcTag() {
        return this.enableAigcTag;
    }

    public void setEnableAigcTag(@Nullable Boolean enableAigcTag) {
        this.enableAigcTag = enableAigcTag;
    }

    public @Nullable String getAigcPropagator() {
        return this.aigcPropagator;
    }

    public void setAigcPropagator(@Nullable String aigcPropagator) {
        this.aigcPropagator = aigcPropagator;
    }

    public @Nullable String getAigcPropagateId() {
        return this.aigcPropagateId;
    }

    public void setAigcPropagateId(@Nullable String aigcPropagateId) {
        this.aigcPropagateId = aigcPropagateId;
    }

    public @Nullable String getLanguageType() {
        return this.languageType;
    }

    public void setLanguageType(@Nullable String languageType) {
        this.languageType = languageType;
    }

    public String getWebsocketUrl() {
        return this.websocketUrl;
    }

    public void setWebsocketUrl(String websocketUrl) {
        this.websocketUrl = websocketUrl;
    }

    public DashScopeAudioSpeechOptions toOptions() {
        return DashScopeAudioSpeechOptions.builder()
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
                .languageHints(this.languageHints)
                .instruction(this.instruction)
                .optimizeInstructions(this.optimizeInstructions)
                .enableAigcTag(this.enableAigcTag)
                .aigcPropagator(this.aigcPropagator)
                .aigcPropagateId(this.aigcPropagateId)
                .languageType(this.languageType)
                .build();
    }

    private Options options = new Options();

    @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX)
    @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
    public Options getOptions() {
        return options;
    }

    public void setOptions(Options options) {
        this.options = options;
    }

    public class Options {

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".model")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable String getModel() {
            return DashScopeAudioSpeechProperties.this.getModel();
        }

        public void setModel(@Nullable String model) {
            DashScopeAudioSpeechProperties.this.setModel(model);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".voice")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable String getVoice() {
            return DashScopeAudioSpeechProperties.this.getVoice();
        }

        public void setVoice(@Nullable String voice) {
            DashScopeAudioSpeechProperties.this.setVoice(voice);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".text-type")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable String getTextType() {
            return DashScopeAudioSpeechProperties.this.getTextType();
        }

        public void setTextType(@Nullable String textType) {
            DashScopeAudioSpeechProperties.this.setTextType(textType);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".enable-aigc-tag")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable Boolean getEnableAigcTag() {
            return DashScopeAudioSpeechProperties.this.getEnableAigcTag();
        }

        public void setEnableAigcTag(@Nullable Boolean enableAigcTag) {
            DashScopeAudioSpeechProperties.this.setEnableAigcTag(enableAigcTag);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".aigc-propagator")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable String getAigcPropagator() {
            return DashScopeAudioSpeechProperties.this.getAigcPropagator();
        }

        public void setAigcPropagator(@Nullable String aigcPropagator) {
            DashScopeAudioSpeechProperties.this.setAigcPropagator(aigcPropagator);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".aigc-propagate-id")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable String getAigcPropagateId() {
            return DashScopeAudioSpeechProperties.this.getAigcPropagateId();
        }

        public void setAigcPropagateId(@Nullable String aigcPropagateId) {
            DashScopeAudioSpeechProperties.this.setAigcPropagateId(aigcPropagateId);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".sample-rate")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable Integer getSampleRate() {
            return DashScopeAudioSpeechProperties.this.getSampleRate();
        }

        public void setSampleRate(@Nullable Integer sampleRate) {
            DashScopeAudioSpeechProperties.this.setSampleRate(sampleRate);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".format")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable String getFormat() {
            return DashScopeAudioSpeechProperties.this.getFormat();
        }

        public void setFormat(@Nullable String format) {
            DashScopeAudioSpeechProperties.this.setFormat(format);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".word-timestamp-enabled")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable Boolean getWordTimestampEnabled() {
            return DashScopeAudioSpeechProperties.this.getWordTimestampEnabled();
        }

        public void setWordTimestampEnabled(@Nullable Boolean wordTimestampEnabled) {
            DashScopeAudioSpeechProperties.this.setWordTimestampEnabled(wordTimestampEnabled);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".phoneme-timestamp-enabled")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable Boolean getPhonemeTimestampEnabled() {
            return DashScopeAudioSpeechProperties.this.getPhonemeTimestampEnabled();
        }

        public void setPhonemeTimestampEnabled(@Nullable Boolean phonemeTimestampEnabled) {
            DashScopeAudioSpeechProperties.this.setPhonemeTimestampEnabled(phonemeTimestampEnabled);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".volume")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable Integer getVolume() {
            return DashScopeAudioSpeechProperties.this.getVolume();
        }

        public void setVolume(@Nullable Integer volume) {
            DashScopeAudioSpeechProperties.this.setVolume(volume);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".speed")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable Double getSpeed() {
            return DashScopeAudioSpeechProperties.this.getSpeed();
        }

        public void setSpeed(@Nullable Double speed) {
            DashScopeAudioSpeechProperties.this.setSpeed(speed);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".rate")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable Float getRate() {
            return DashScopeAudioSpeechProperties.this.getRate();
        }

        public void setRate(@Nullable Float rate) {
            DashScopeAudioSpeechProperties.this.setRate(rate);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".pitch")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable Float getPitch() {
            return DashScopeAudioSpeechProperties.this.getPitch();
        }

        public void setPitch(@Nullable Float pitch) {
            DashScopeAudioSpeechProperties.this.setPitch(pitch);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".enable-ssml")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable Boolean getEnableSsml() {
            return DashScopeAudioSpeechProperties.this.getEnableSsml();
        }

        public void setEnableSsml(@Nullable Boolean enableSsml) {
            DashScopeAudioSpeechProperties.this.setEnableSsml(enableSsml);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".bit-rate")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable Integer getBitRate() {
            return DashScopeAudioSpeechProperties.this.getBitRate();
        }

        public void setBitRate(@Nullable Integer bitRate) {
            DashScopeAudioSpeechProperties.this.setBitRate(bitRate);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".seed")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable Integer getSeed() {
            return DashScopeAudioSpeechProperties.this.getSeed();
        }

        public void setSeed(@Nullable Integer seed) {
            DashScopeAudioSpeechProperties.this.setSeed(seed);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".language-hints")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable List<String> getLanguageHints() {
            return DashScopeAudioSpeechProperties.this.getLanguageHints();
        }

        public void setLanguageHints(@Nullable List<String> languageHints) {
            DashScopeAudioSpeechProperties.this.setLanguageHints(languageHints);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".instruction")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable String getInstruction() {
            return DashScopeAudioSpeechProperties.this.getInstruction();
        }

        public void setInstruction(@Nullable String instruction) {
            DashScopeAudioSpeechProperties.this.setInstruction(instruction);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".optimize-instructions")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable Boolean getOptimizeInstructions() {
            return DashScopeAudioSpeechProperties.this.getOptimizeInstructions();
        }

        public void setOptimizeInstructions(@Nullable Boolean optimizeInstructions) {
            DashScopeAudioSpeechProperties.this.setOptimizeInstructions(optimizeInstructions);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".language-type")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable String getLanguageType() {
            return DashScopeAudioSpeechProperties.this.getLanguageType();
        }

        public void setLanguageType(@Nullable String languageType) {
            DashScopeAudioSpeechProperties.this.setLanguageType(languageType);
        }

    }

}
