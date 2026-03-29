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

import com.alibaba.cloud.ai.dashscope.audio.transcription.DashScopeAudioTranscriptionOptions.Resource;
import com.alibaba.cloud.ai.dashscope.audio.transcription.DashScopeTranscriptionResponse.DashScopeAudioTranscription;
import com.alibaba.cloud.ai.dashscope.metadata.audio.DashScopeAudioTranscriptionResponseMetadata.Usage;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jspecify.annotations.Nullable;
import org.springframework.ai.audio.transcription.AudioTranscriptionResponse;

import java.util.List;

/**
 * @author yingzi
 * @since 2026/2/4
 */

public class DashScopeAsrTranscriptionApiSpec {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class AsrTranscriptionRequest {
        @JsonProperty("model")
        private @Nullable String model;

        @JsonProperty("input")
        private @Nullable Input input;

        @JsonProperty("parameters")
        private @Nullable Parameters parameters;

        @JsonProperty("resources")
        private @Nullable List<Resource> resources;

        public @Nullable String getModel() {
            return model;
        }

        public void setModel(@Nullable String model) {
            this.model = model;
        }

        public @Nullable Input getInput() {
            return input;
        }

        public void setInput(@Nullable Input input) {
            this.input = input;
        }

        public @Nullable Parameters getParameters() {
            return parameters;
        }

        public void setParameters(@Nullable Parameters parameters) {
            this.parameters = parameters;
        }

        public @Nullable List<Resource> getResources() {
            return resources;
        }

        public void setResources(@Nullable List<Resource> resources) {
            this.resources = resources;
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private final AsrTranscriptionRequest request = new AsrTranscriptionRequest();

            public Builder model(@Nullable String model) {
                request.model = model;
                return this;
            }

            public Builder input(@Nullable Input input) {
                request.input = input;
                return this;
            }

            public Builder parameters(@Nullable Parameters parameters) {
                request.parameters = parameters;
                return this;
            }

            public Builder resources(@Nullable List<Resource> resources) {
                request.resources = resources;
                return this;
            }

            public AsrTranscriptionRequest build() {
                return request;
            }
        }

        public static class Input {
            @JsonProperty("file_urls")
            private @Nullable List<String> fileUrls;

            public @Nullable List<String> getFileUrls() {
                return fileUrls;
            }

            public void setFileUrls(@Nullable List<String> fileUrls) {
                this.fileUrls = fileUrls;
            }

            public static Builder builder() {
                return new Builder();
            }

            public static class Builder {
                private final Input input = new Input();

                public Builder fileUrls(@Nullable List<String> fileUrls) {
                    input.fileUrls = fileUrls;
                    return this;
                }

                public Input build() {
                    return input;
                }
            }
        }

        public static class Parameters {
            @JsonProperty("vocabulary_id")
            private @Nullable String vocabularyId;

            @JsonProperty("channel_id")
            private @Nullable List<Integer> channelId;

            @JsonProperty("special_word_filter")
            private @Nullable String specialWordFilter;

            @JsonProperty("diarization_enabled")
            private @Nullable Boolean diarizationEnabled;

            @JsonProperty("disfluency_removal_enabled")
            private @Nullable Boolean disfluencyRemovalEnabled;

            @JsonProperty("timestamp_alignment_enabled")
            private @Nullable Boolean timestampAlignmentEnabled;

            @JsonProperty("speaker_count")
            private @Nullable Integer speakerCount;

            @JsonProperty("language_hints")
            private @Nullable List<String> languageHints;

            public @Nullable String getVocabularyId() {
                return vocabularyId;
            }

            public void setVocabularyId(@Nullable String vocabularyId) {
                this.vocabularyId = vocabularyId;
            }

            public @Nullable List<Integer> getChannelId() {
                return channelId;
            }

            public void setChannelId(@Nullable List<Integer> channelId) {
                this.channelId = channelId;
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

            public @Nullable Boolean getDisfluencyRemovalEnabled() {
                return disfluencyRemovalEnabled;
            }

            public void setDisfluencyRemovalEnabled(@Nullable Boolean disfluencyRemovalEnabled) {
                this.disfluencyRemovalEnabled = disfluencyRemovalEnabled;
            }

            public @Nullable Boolean getTimestampAlignmentEnabled() {
                return timestampAlignmentEnabled;
            }

            public void setTimestampAlignmentEnabled(@Nullable Boolean timestampAlignmentEnabled) {
                this.timestampAlignmentEnabled = timestampAlignmentEnabled;
            }

            public @Nullable Integer getSpeakerCount() {
                return speakerCount;
            }

            public void setSpeakerCount(@Nullable Integer speakerCount) {
                this.speakerCount = speakerCount;
            }

            public @Nullable List<String> getLanguageHints() {
                return languageHints;
            }

            public void setLanguageHints(@Nullable List<String> languageHints) {
                this.languageHints = languageHints;
            }

            public static Builder builder() {
                return new Builder();
            }

            public static class Builder {
                private final Parameters parameters = new Parameters();

                public Builder vocabularyId(@Nullable String vocabularyId) {
                    parameters.vocabularyId = vocabularyId;
                    return this;
                }

                public Builder channelId(@Nullable List<Integer> channelId) {
                    parameters.channelId = channelId;
                    return this;
                }

                public Builder specialWordFilter(@Nullable String specialWordFilter) {
                    parameters.specialWordFilter = specialWordFilter;
                    return this;
                }

                public Builder diarizationEnabled(@Nullable Boolean diarizationEnabled) {
                    parameters.diarizationEnabled = diarizationEnabled;
                    return this;
                }

                public Builder disfluencyRemovalEnabled(@Nullable Boolean disfluencyRemovalEnabled) {
                    parameters.disfluencyRemovalEnabled = disfluencyRemovalEnabled;
                    return this;
                }

                public Builder timestampAlignmentEnabled(@Nullable Boolean timestampAlignmentEnabled) {
                    parameters.timestampAlignmentEnabled = timestampAlignmentEnabled;
                    return this;
                }

                public Builder speakerCount(@Nullable Integer speakerCount) {
                    parameters.speakerCount = speakerCount;
                    return this;
                }

                public Builder languageHints(@Nullable List<String> languageHints) {
                    parameters.languageHints = languageHints;
                    return this;
                }

                public Parameters build() {
                    return parameters;
                }
            }
        }

    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record AsrOutPut(
            @JsonProperty("request_id") @Nullable String requestId,
            @JsonProperty("output") @Nullable Output output
    ) {
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public record Output(
                @JsonProperty("task_status") @Nullable String taskStatus,
                @JsonProperty("task_id") @Nullable String taskId) {
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record AsrResponse(
            @JsonProperty("request_id") @Nullable String requestId,
            @JsonProperty("output") @Nullable Output output,
            @JsonProperty("usage") @Nullable Usage usage
    ) {
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public record Output(
                @JsonProperty("task_status") @Nullable String taskStatus,
                @JsonProperty("task_id") @Nullable String taskId,
                @JsonProperty("submit_time") @Nullable String submitTime,
                @JsonProperty("scheduled_time") @Nullable String scheduledTime,
                @JsonProperty("end_time") @Nullable String endTime,
                @JsonProperty("results") @Nullable List<Result> results,
                @JsonProperty("task_metrics") @Nullable TaskMetrics taskMetrics
        ) {
            public record Result(
                    @JsonProperty("file_url") @Nullable String fileUrl,
                    @JsonProperty("transcription_url") @Nullable String transcriptionUrl,
                    @JsonProperty("subtask_status") @Nullable String subtaskStatus
            ) {}

            public record TaskMetrics(
                    @JsonProperty("TOTAL") @Nullable Integer total,
                    @JsonProperty("SUCCEEDED") @Nullable Integer succeeded,
                    @JsonProperty("FAILED") @Nullable Integer failed
            ) {}
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class DashScopeAudioAsrTranscriptionResponse extends AudioTranscriptionResponse {

        private final List<TranscriptionResult> transcriptionResults;

        public DashScopeAudioAsrTranscriptionResponse() {
            super(null);
            this.transcriptionResults = List.of();
        }

        public DashScopeAudioAsrTranscriptionResponse(List<TranscriptionResult> transcriptionResults) {
            super(null);
            this.transcriptionResults = transcriptionResults != null ? transcriptionResults : List.of();
        }

        public DashScopeAudioAsrTranscriptionResponse(TranscriptionResult transcriptionResult) {
            super(null);
            this.transcriptionResults = transcriptionResult != null ? List.of(transcriptionResult) : List.of();
        }

        public List<TranscriptionResult> getTranscriptionResults() {
            return transcriptionResults;
        }

        @JsonInclude(JsonInclude.Include.NON_NULL)
        public record TranscriptionResult(
                @JsonProperty("file_url") @Nullable String fileUrl,
                @JsonProperty("properties") @Nullable Properties properties,
                @JsonProperty("transcripts") @Nullable List<DashScopeAudioTranscription> transcripts
        ) {
            @JsonInclude(JsonInclude.Include.NON_NULL)
            public record Properties(
                    @JsonProperty("audio_format") @Nullable String audioFormat,
                    @JsonProperty("channels") @Nullable List<Integer> channels,
                    @JsonProperty("original_sampling_rate") @Nullable Integer originalSamplingRate,
                    @JsonProperty("original_duration_in_milliseconds") @Nullable Integer originalDurationInMilliseconds
            ) {}
        }

    }

}
