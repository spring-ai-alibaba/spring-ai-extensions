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

package com.alibaba.cloud.ai.autoconfigure.dashscope.sdk;

import java.util.Map;

import com.alibaba.cloud.ai.dashscope.sdk.audio.tts.DashScopeSdkAudioSpeechOptions;
import org.jspecify.annotations.Nullable;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.DeprecatedConfigurationProperty;

/**
 * DashScope SDK audio speech model properties.
 */
@ConfigurationProperties(DashScopeSdkAudioSpeechProperties.CONFIG_PREFIX)
public class DashScopeSdkAudioSpeechProperties extends DashScopeSdkParentProperties {

    public static final String CONFIG_PREFIX = "spring.ai.dashscope.sdk.audio.speech";

    private boolean enabled = true;

    private @Nullable String model = "sambert-zhichu-v1";

    private @Nullable String voice;

    private @Nullable String format;

    private @Nullable Double speed;

    private @Nullable String textType;

    private @Nullable Integer sampleRate;

    private @Nullable Integer volume;

    private @Nullable Float rate;

    private @Nullable Float pitch;

    private @Nullable Boolean wordTimestampEnabled;

    private @Nullable Boolean phonemeTimestampEnabled;

    private @Nullable Map<String, String> httpHeaders;

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public @Nullable String getModel() {
        return this.model;
    }

    public void setModel(@Nullable String model) {
        this.model = model;
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

    public @Nullable Double getSpeed() {
        return this.speed;
    }

    public void setSpeed(@Nullable Double speed) {
        this.speed = speed;
    }

    public @Nullable String getTextType() {
        return this.textType;
    }

    public void setTextType(@Nullable String textType) {
        this.textType = textType;
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

    public @Nullable Map<String, String> getHttpHeaders() {
        return this.httpHeaders;
    }

    public void setHttpHeaders(@Nullable Map<String, String> httpHeaders) {
        this.httpHeaders = httpHeaders;
    }

    public DashScopeSdkAudioSpeechOptions toOptions() {
        return DashScopeSdkAudioSpeechOptions.builder()
                .model(this.model)
                .voice(this.voice)
                .format(this.format)
                .speed(this.speed)
                .textType(this.textType)
                .sampleRate(this.sampleRate)
                .volume(this.volume)
                .rate(this.rate)
                .pitch(this.pitch)
                .wordTimestampEnabled(this.wordTimestampEnabled)
                .phonemeTimestampEnabled(this.phonemeTimestampEnabled)
                .httpHeaders(this.httpHeaders)
                .build();
    }

    private Options options = new Options();

    @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX)
    @Deprecated(since = "2.0.0", forRemoval = true)
    public Options getOptions() {
        return this.options;
    }

    public void setOptions(Options options) {
        this.options = options;
    }

    public class Options {

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".model")
        @Deprecated(since = "2.0.0", forRemoval = true)
        public @Nullable String getModel() {
            return DashScopeSdkAudioSpeechProperties.this.getModel();
        }

        public void setModel(String model) {
            DashScopeSdkAudioSpeechProperties.this.setModel(model);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".voice")
        @Deprecated(since = "2.0.0", forRemoval = true)
        public @Nullable String getVoice() {
            return DashScopeSdkAudioSpeechProperties.this.getVoice();
        }

        public void setVoice(String voice) {
            DashScopeSdkAudioSpeechProperties.this.setVoice(voice);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".format")
        @Deprecated(since = "2.0.0", forRemoval = true)
        public @Nullable String getFormat() {
            return DashScopeSdkAudioSpeechProperties.this.getFormat();
        }

        public void setFormat(String format) {
            DashScopeSdkAudioSpeechProperties.this.setFormat(format);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".speed")
        @Deprecated(since = "2.0.0", forRemoval = true)
        public @Nullable Double getSpeed() {
            return DashScopeSdkAudioSpeechProperties.this.getSpeed();
        }

        public void setSpeed(Double speed) {
            DashScopeSdkAudioSpeechProperties.this.setSpeed(speed);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".text-type")
        @Deprecated(since = "2.0.0", forRemoval = true)
        public @Nullable String getTextType() {
            return DashScopeSdkAudioSpeechProperties.this.getTextType();
        }

        public void setTextType(String textType) {
            DashScopeSdkAudioSpeechProperties.this.setTextType(textType);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".sample-rate")
        @Deprecated(since = "2.0.0", forRemoval = true)
        public @Nullable Integer getSampleRate() {
            return DashScopeSdkAudioSpeechProperties.this.getSampleRate();
        }

        public void setSampleRate(Integer sampleRate) {
            DashScopeSdkAudioSpeechProperties.this.setSampleRate(sampleRate);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".volume")
        @Deprecated(since = "2.0.0", forRemoval = true)
        public @Nullable Integer getVolume() {
            return DashScopeSdkAudioSpeechProperties.this.getVolume();
        }

        public void setVolume(Integer volume) {
            DashScopeSdkAudioSpeechProperties.this.setVolume(volume);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".rate")
        @Deprecated(since = "2.0.0", forRemoval = true)
        public @Nullable Float getRate() {
            return DashScopeSdkAudioSpeechProperties.this.getRate();
        }

        public void setRate(Float rate) {
            DashScopeSdkAudioSpeechProperties.this.setRate(rate);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".pitch")
        @Deprecated(since = "2.0.0", forRemoval = true)
        public @Nullable Float getPitch() {
            return DashScopeSdkAudioSpeechProperties.this.getPitch();
        }

        public void setPitch(Float pitch) {
            DashScopeSdkAudioSpeechProperties.this.setPitch(pitch);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".word-timestamp-enabled")
        @Deprecated(since = "2.0.0", forRemoval = true)
        public @Nullable Boolean getWordTimestampEnabled() {
            return DashScopeSdkAudioSpeechProperties.this.getWordTimestampEnabled();
        }

        public void setWordTimestampEnabled(Boolean wordTimestampEnabled) {
            DashScopeSdkAudioSpeechProperties.this.setWordTimestampEnabled(wordTimestampEnabled);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".phoneme-timestamp-enabled")
        @Deprecated(since = "2.0.0", forRemoval = true)
        public @Nullable Boolean getPhonemeTimestampEnabled() {
            return DashScopeSdkAudioSpeechProperties.this.getPhonemeTimestampEnabled();
        }

        public void setPhonemeTimestampEnabled(Boolean phonemeTimestampEnabled) {
            DashScopeSdkAudioSpeechProperties.this.setPhonemeTimestampEnabled(phonemeTimestampEnabled);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".http-headers")
        @Deprecated(since = "2.0.0", forRemoval = true)
        public @Nullable Map<String, String> getHttpHeaders() {
            return DashScopeSdkAudioSpeechProperties.this.getHttpHeaders();
        }

        public void setHttpHeaders(Map<String, String> httpHeaders) {
            DashScopeSdkAudioSpeechProperties.this.setHttpHeaders(httpHeaders);
        }

    }

}
