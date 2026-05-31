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

package com.alibaba.cloud.ai.dashscope.sdk.audio.transcription;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jspecify.annotations.Nullable;
import org.springframework.ai.audio.transcription.AudioTranscriptionOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Options for DashScope SDK audio transcription model.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DashScopeSdkAudioTranscriptionOptions implements AudioTranscriptionOptions {

    @JsonProperty("model")
    private final String model;

    @JsonProperty("file_urls")
    private final @Nullable List<String> fileUrls;

    @JsonProperty("phrase_id")
    private final @Nullable String phraseId;

    @JsonProperty("channel_id")
    private final @Nullable List<Integer> channelId;

    @JsonProperty("diarization_enabled")
    private final @Nullable Boolean diarizationEnabled;

    @JsonProperty("speaker_count")
    private final @Nullable Integer speakerCount;

    @JsonProperty("disfluency_removal_enabled")
    private final @Nullable Boolean disfluencyRemovalEnabled;

    @JsonProperty("timestamp_alignment_enabled")
    private final @Nullable Boolean timestampAlignmentEnabled;

    @JsonProperty("special_word_filter")
    private final @Nullable String specialWordFilter;

    @JsonProperty("audio_event_detection_enabled")
    private final @Nullable Boolean audioEventDetectionEnabled;

    @JsonIgnore
    private final Map<String, String> httpHeaders;

    protected DashScopeSdkAudioTranscriptionOptions(
            @Nullable String model,
            @Nullable List<String> fileUrls,
            @Nullable String phraseId,
            @Nullable List<Integer> channelId,
            @Nullable Boolean diarizationEnabled,
            @Nullable Integer speakerCount,
            @Nullable Boolean disfluencyRemovalEnabled,
            @Nullable Boolean timestampAlignmentEnabled,
            @Nullable String specialWordFilter,
            @Nullable Boolean audioEventDetectionEnabled,
            @Nullable Map<String, String> httpHeaders) {
        this.model = model != null ? model : "paraformer-v2";
        this.fileUrls = fileUrls != null ? new ArrayList<>(fileUrls) : null;
        this.phraseId = phraseId;
        this.channelId = channelId != null ? new ArrayList<>(channelId) : null;
        this.diarizationEnabled = diarizationEnabled;
        this.speakerCount = speakerCount;
        this.disfluencyRemovalEnabled = disfluencyRemovalEnabled;
        this.timestampAlignmentEnabled = timestampAlignmentEnabled;
        this.specialWordFilter = specialWordFilter;
        this.audioEventDetectionEnabled = audioEventDetectionEnabled;
        this.httpHeaders = httpHeaders != null ? new HashMap<>(httpHeaders) : new HashMap<>();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static DashScopeSdkAudioTranscriptionOptions fromOptions(DashScopeSdkAudioTranscriptionOptions options) {
        return options.mutate().build();
    }

    @Override
    public String getModel() {
        return this.model;
    }

    public @Nullable List<String> getFileUrls() {
        return this.fileUrls;
    }

    public @Nullable String getPhraseId() {
        return this.phraseId;
    }

    public @Nullable List<Integer> getChannelId() {
        return this.channelId;
    }

    public @Nullable Boolean getDiarizationEnabled() {
        return this.diarizationEnabled;
    }

    public @Nullable Integer getSpeakerCount() {
        return this.speakerCount;
    }

    public @Nullable Boolean getDisfluencyRemovalEnabled() {
        return this.disfluencyRemovalEnabled;
    }

    public @Nullable Boolean getTimestampAlignmentEnabled() {
        return this.timestampAlignmentEnabled;
    }

    public @Nullable String getSpecialWordFilter() {
        return this.specialWordFilter;
    }

    public @Nullable Boolean getAudioEventDetectionEnabled() {
        return this.audioEventDetectionEnabled;
    }

    public Map<String, String> getHttpHeaders() {
        return this.httpHeaders;
    }

    public Builder mutate() {
        return builder().model(this.model)
                .fileUrls(this.fileUrls)
                .phraseId(this.phraseId)
                .channelId(this.channelId)
                .diarizationEnabled(this.diarizationEnabled)
                .speakerCount(this.speakerCount)
                .disfluencyRemovalEnabled(this.disfluencyRemovalEnabled)
                .timestampAlignmentEnabled(this.timestampAlignmentEnabled)
                .specialWordFilter(this.specialWordFilter)
                .audioEventDetectionEnabled(this.audioEventDetectionEnabled)
                .httpHeaders(this.httpHeaders);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DashScopeSdkAudioTranscriptionOptions that = (DashScopeSdkAudioTranscriptionOptions) o;
        return Objects.equals(this.model, that.model) && Objects.equals(this.fileUrls, that.fileUrls)
                && Objects.equals(this.phraseId, that.phraseId) && Objects.equals(this.channelId, that.channelId)
                && Objects.equals(this.diarizationEnabled, that.diarizationEnabled)
                && Objects.equals(this.speakerCount, that.speakerCount)
                && Objects.equals(this.disfluencyRemovalEnabled, that.disfluencyRemovalEnabled)
                && Objects.equals(this.timestampAlignmentEnabled, that.timestampAlignmentEnabled)
                && Objects.equals(this.specialWordFilter, that.specialWordFilter)
                && Objects.equals(this.audioEventDetectionEnabled, that.audioEventDetectionEnabled)
                && Objects.equals(this.httpHeaders, that.httpHeaders);
    }

    // @formatter:off
	@Override
	public int hashCode() {
		return Objects.hash(this.model, this.fileUrls, this.phraseId, this.channelId, this.diarizationEnabled,
				this.speakerCount, this.disfluencyRemovalEnabled, this.timestampAlignmentEnabled,
				this.specialWordFilter, this.audioEventDetectionEnabled, this.httpHeaders);
	}
    // @formatter:on

    @Override
    public String toString() {
        return "DashScopeSdkAudioTranscriptionOptions{" + "model='" + this.model + '\'' + ", fileUrls=" + this.fileUrls
                + ", phraseId='" + this.phraseId + '\'' + ", channelId=" + this.channelId + ", diarizationEnabled="
                + this.diarizationEnabled + ", speakerCount=" + this.speakerCount + ", disfluencyRemovalEnabled="
                + this.disfluencyRemovalEnabled + ", timestampAlignmentEnabled=" + this.timestampAlignmentEnabled
                + ", specialWordFilter='" + this.specialWordFilter + '\'' + ", audioEventDetectionEnabled="
                + this.audioEventDetectionEnabled + ", httpHeaders=" + this.httpHeaders + '}';
    }

    public static class Builder {

        protected @Nullable String model;

        protected @Nullable List<String> fileUrls;

        protected @Nullable String phraseId;

        protected @Nullable List<Integer> channelId;

        protected @Nullable Boolean diarizationEnabled;

        protected @Nullable Integer speakerCount;

        protected @Nullable Boolean disfluencyRemovalEnabled;

        protected @Nullable Boolean timestampAlignmentEnabled;

        protected @Nullable String specialWordFilter;

        protected @Nullable Boolean audioEventDetectionEnabled;

        protected Map<String, String> httpHeaders = new HashMap<>();

        public Builder() {
        }

        public Builder model(@Nullable String model) {
            this.model = model;
            return this;
        }

        public Builder fileUrls(@Nullable List<String> fileUrls) {
            this.fileUrls = fileUrls;
            return this;
        }

        public Builder phraseId(@Nullable String phraseId) {
            this.phraseId = phraseId;
            return this;
        }

        public Builder channelId(@Nullable List<Integer> channelId) {
            this.channelId = channelId;
            return this;
        }

        public Builder diarizationEnabled(@Nullable Boolean diarizationEnabled) {
            this.diarizationEnabled = diarizationEnabled;
            return this;
        }

        public Builder speakerCount(@Nullable Integer speakerCount) {
            this.speakerCount = speakerCount;
            return this;
        }

        public Builder disfluencyRemovalEnabled(@Nullable Boolean disfluencyRemovalEnabled) {
            this.disfluencyRemovalEnabled = disfluencyRemovalEnabled;
            return this;
        }

        public Builder timestampAlignmentEnabled(@Nullable Boolean timestampAlignmentEnabled) {
            this.timestampAlignmentEnabled = timestampAlignmentEnabled;
            return this;
        }

        public Builder specialWordFilter(@Nullable String specialWordFilter) {
            this.specialWordFilter = specialWordFilter;
            return this;
        }

        public Builder audioEventDetectionEnabled(@Nullable Boolean audioEventDetectionEnabled) {
            this.audioEventDetectionEnabled = audioEventDetectionEnabled;
            return this;
        }

        public Builder httpHeaders(Map<String, String> httpHeaders) {
            this.httpHeaders = httpHeaders;
            return this;
        }

        public Builder from(DashScopeSdkAudioTranscriptionOptions fromOptions) {
            this.model = fromOptions.getModel();
            this.fileUrls = fromOptions.getFileUrls();
            this.phraseId = fromOptions.getPhraseId();
            this.channelId = fromOptions.getChannelId();
            this.diarizationEnabled = fromOptions.getDiarizationEnabled();
            this.speakerCount = fromOptions.getSpeakerCount();
            this.disfluencyRemovalEnabled = fromOptions.getDisfluencyRemovalEnabled();
            this.timestampAlignmentEnabled = fromOptions.getTimestampAlignmentEnabled();
            this.specialWordFilter = fromOptions.getSpecialWordFilter();
            this.audioEventDetectionEnabled = fromOptions.getAudioEventDetectionEnabled();
            this.httpHeaders = fromOptions.getHttpHeaders();
            return this;
        }

        public Builder merge(@Nullable AudioTranscriptionOptions from) {
            if (from == null) {
                return this;
            }
            if (from.getModel() != null) {
                this.model = from.getModel();
            }
            if (from instanceof DashScopeSdkAudioTranscriptionOptions castFrom) {
                if (castFrom.getFileUrls() != null) {
                    this.fileUrls = castFrom.getFileUrls();
                }
                if (castFrom.getPhraseId() != null) {
                    this.phraseId = castFrom.getPhraseId();
                }
                if (castFrom.getChannelId() != null) {
                    this.channelId = castFrom.getChannelId();
                }
                if (castFrom.getDiarizationEnabled() != null) {
                    this.diarizationEnabled = castFrom.getDiarizationEnabled();
                }
                if (castFrom.getSpeakerCount() != null) {
                    this.speakerCount = castFrom.getSpeakerCount();
                }
                if (castFrom.getDisfluencyRemovalEnabled() != null) {
                    this.disfluencyRemovalEnabled = castFrom.getDisfluencyRemovalEnabled();
                }
                if (castFrom.getTimestampAlignmentEnabled() != null) {
                    this.timestampAlignmentEnabled = castFrom.getTimestampAlignmentEnabled();
                }
                if (castFrom.getSpecialWordFilter() != null) {
                    this.specialWordFilter = castFrom.getSpecialWordFilter();
                }
                if (castFrom.getAudioEventDetectionEnabled() != null) {
                    this.audioEventDetectionEnabled = castFrom.getAudioEventDetectionEnabled();
                }
                if (castFrom.getHttpHeaders() != null && !castFrom.getHttpHeaders().isEmpty()) {
                    this.httpHeaders = castFrom.getHttpHeaders();
                }
            }
            return this;
        }

        // @formatter:off
		public DashScopeSdkAudioTranscriptionOptions build() {
			return new DashScopeSdkAudioTranscriptionOptions(this.model, this.fileUrls, this.phraseId, this.channelId,
					this.diarizationEnabled, this.speakerCount, this.disfluencyRemovalEnabled,
					this.timestampAlignmentEnabled, this.specialWordFilter, this.audioEventDetectionEnabled,
					this.httpHeaders);
		}
        // @formatter:on

    }

}
