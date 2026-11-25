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

import java.util.List;
import java.util.Map;

import com.alibaba.cloud.ai.dashscope.sdk.audio.transcription.DashScopeSdkAudioTranscriptionOptions;
import org.jspecify.annotations.Nullable;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.DeprecatedConfigurationProperty;

/**
 * DashScope SDK audio transcription model properties.
 */
@ConfigurationProperties(DashScopeSdkAudioTranscriptionProperties.CONFIG_PREFIX)
public class DashScopeSdkAudioTranscriptionProperties extends DashScopeSdkParentProperties {

    public static final String CONFIG_PREFIX = "spring.ai.dashscope.sdk.audio.transcription";

    private boolean enabled = true;

    private @Nullable String model = "paraformer-v2";

    private @Nullable List<String> fileUrls;

    private @Nullable String phraseId;

    private @Nullable List<Integer> channelId;

    private @Nullable Boolean diarizationEnabled;

    private @Nullable Integer speakerCount;

    private @Nullable Boolean disfluencyRemovalEnabled;

    private @Nullable Boolean timestampAlignmentEnabled;

    private @Nullable String specialWordFilter;

    private @Nullable Boolean audioEventDetectionEnabled;

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

    public @Nullable List<String> getFileUrls() {
        return this.fileUrls;
    }

    public void setFileUrls(@Nullable List<String> fileUrls) {
        this.fileUrls = fileUrls;
    }

    public @Nullable String getPhraseId() {
        return this.phraseId;
    }

    public void setPhraseId(@Nullable String phraseId) {
        this.phraseId = phraseId;
    }

    public @Nullable List<Integer> getChannelId() {
        return this.channelId;
    }

    public void setChannelId(@Nullable List<Integer> channelId) {
        this.channelId = channelId;
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

    public @Nullable Boolean getDisfluencyRemovalEnabled() {
        return this.disfluencyRemovalEnabled;
    }

    public void setDisfluencyRemovalEnabled(@Nullable Boolean disfluencyRemovalEnabled) {
        this.disfluencyRemovalEnabled = disfluencyRemovalEnabled;
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

    public @Nullable Boolean getAudioEventDetectionEnabled() {
        return this.audioEventDetectionEnabled;
    }

    public void setAudioEventDetectionEnabled(@Nullable Boolean audioEventDetectionEnabled) {
        this.audioEventDetectionEnabled = audioEventDetectionEnabled;
    }

    public @Nullable Map<String, String> getHttpHeaders() {
        return this.httpHeaders;
    }

    public void setHttpHeaders(@Nullable Map<String, String> httpHeaders) {
        this.httpHeaders = httpHeaders;
    }

    public DashScopeSdkAudioTranscriptionOptions toOptions() {
        return DashScopeSdkAudioTranscriptionOptions.builder()
                .model(this.model)
                .fileUrls(this.fileUrls)
                .phraseId(this.phraseId)
                .channelId(this.channelId)
                .diarizationEnabled(this.diarizationEnabled)
                .speakerCount(this.speakerCount)
                .disfluencyRemovalEnabled(this.disfluencyRemovalEnabled)
                .timestampAlignmentEnabled(this.timestampAlignmentEnabled)
                .specialWordFilter(this.specialWordFilter)
                .audioEventDetectionEnabled(this.audioEventDetectionEnabled)
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
            return DashScopeSdkAudioTranscriptionProperties.this.getModel();
        }

        public void setModel(String model) {
            DashScopeSdkAudioTranscriptionProperties.this.setModel(model);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".file-urls")
        @Deprecated(since = "2.0.0", forRemoval = true)
        public @Nullable List<String> getFileUrls() {
            return DashScopeSdkAudioTranscriptionProperties.this.getFileUrls();
        }

        public void setFileUrls(List<String> fileUrls) {
            DashScopeSdkAudioTranscriptionProperties.this.setFileUrls(fileUrls);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".phrase-id")
        @Deprecated(since = "2.0.0", forRemoval = true)
        public @Nullable String getPhraseId() {
            return DashScopeSdkAudioTranscriptionProperties.this.getPhraseId();
        }

        public void setPhraseId(String phraseId) {
            DashScopeSdkAudioTranscriptionProperties.this.setPhraseId(phraseId);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".channel-id")
        @Deprecated(since = "2.0.0", forRemoval = true)
        public @Nullable List<Integer> getChannelId() {
            return DashScopeSdkAudioTranscriptionProperties.this.getChannelId();
        }

        public void setChannelId(List<Integer> channelId) {
            DashScopeSdkAudioTranscriptionProperties.this.setChannelId(channelId);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".diarization-enabled")
        @Deprecated(since = "2.0.0", forRemoval = true)
        public @Nullable Boolean getDiarizationEnabled() {
            return DashScopeSdkAudioTranscriptionProperties.this.getDiarizationEnabled();
        }

        public void setDiarizationEnabled(Boolean diarizationEnabled) {
            DashScopeSdkAudioTranscriptionProperties.this.setDiarizationEnabled(diarizationEnabled);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".speaker-count")
        @Deprecated(since = "2.0.0", forRemoval = true)
        public @Nullable Integer getSpeakerCount() {
            return DashScopeSdkAudioTranscriptionProperties.this.getSpeakerCount();
        }

        public void setSpeakerCount(Integer speakerCount) {
            DashScopeSdkAudioTranscriptionProperties.this.setSpeakerCount(speakerCount);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".disfluency-removal-enabled")
        @Deprecated(since = "2.0.0", forRemoval = true)
        public @Nullable Boolean getDisfluencyRemovalEnabled() {
            return DashScopeSdkAudioTranscriptionProperties.this.getDisfluencyRemovalEnabled();
        }

        public void setDisfluencyRemovalEnabled(Boolean disfluencyRemovalEnabled) {
            DashScopeSdkAudioTranscriptionProperties.this.setDisfluencyRemovalEnabled(disfluencyRemovalEnabled);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".timestamp-alignment-enabled")
        @Deprecated(since = "2.0.0", forRemoval = true)
        public @Nullable Boolean getTimestampAlignmentEnabled() {
            return DashScopeSdkAudioTranscriptionProperties.this.getTimestampAlignmentEnabled();
        }

        public void setTimestampAlignmentEnabled(Boolean timestampAlignmentEnabled) {
            DashScopeSdkAudioTranscriptionProperties.this.setTimestampAlignmentEnabled(timestampAlignmentEnabled);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".special-word-filter")
        @Deprecated(since = "2.0.0", forRemoval = true)
        public @Nullable String getSpecialWordFilter() {
            return DashScopeSdkAudioTranscriptionProperties.this.getSpecialWordFilter();
        }

        public void setSpecialWordFilter(String specialWordFilter) {
            DashScopeSdkAudioTranscriptionProperties.this.setSpecialWordFilter(specialWordFilter);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".audio-event-detection-enabled")
        @Deprecated(since = "2.0.0", forRemoval = true)
        public @Nullable Boolean getAudioEventDetectionEnabled() {
            return DashScopeSdkAudioTranscriptionProperties.this.getAudioEventDetectionEnabled();
        }

        public void setAudioEventDetectionEnabled(Boolean audioEventDetectionEnabled) {
            DashScopeSdkAudioTranscriptionProperties.this.setAudioEventDetectionEnabled(audioEventDetectionEnabled);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".http-headers")
        @Deprecated(since = "2.0.0", forRemoval = true)
        public @Nullable Map<String, String> getHttpHeaders() {
            return DashScopeSdkAudioTranscriptionProperties.this.getHttpHeaders();
        }

        public void setHttpHeaders(Map<String, String> httpHeaders) {
            DashScopeSdkAudioTranscriptionProperties.this.setHttpHeaders(httpHeaders);
        }

    }

}
