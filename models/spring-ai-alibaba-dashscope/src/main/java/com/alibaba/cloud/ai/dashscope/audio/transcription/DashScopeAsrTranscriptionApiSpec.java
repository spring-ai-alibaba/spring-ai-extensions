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
        private String model;

        @JsonProperty("input")
        private Input input;

        @JsonProperty("parameters")
        private Parameters parameters;

        @JsonProperty("resources")
        private List<Resource> resources;

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }

        public Input getInput() {
            return input;
        }

        public void setInput(Input input) {
            this.input = input;
        }

        public Parameters getParameters() {
            return parameters;
        }

        public void setParameters(Parameters parameters) {
            this.parameters = parameters;
        }

        public List<Resource> getResources() {
            return resources;
        }

        public void setResources(List<Resource> resources) {
            this.resources = resources;
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private final AsrTranscriptionRequest request = new AsrTranscriptionRequest();

            public Builder model(String model) {
                request.model = model;
                return this;
            }

            public Builder input(Input input) {
                request.input = input;
                return this;
            }

            public Builder parameters(Parameters parameters) {
                request.parameters = parameters;
                return this;
            }

            public Builder resources(List<Resource> resources) {
                request.resources = resources;
                return this;
            }

            public AsrTranscriptionRequest build() {
                return request;
            }
        }

        public static class Input {
            @JsonProperty("file_urls")
            private List<String> fileUrls;

            public List<String> getFileUrls() {
                return fileUrls;
            }

            public void setFileUrls(List<String> fileUrls) {
                this.fileUrls = fileUrls;
            }

            public static Builder builder() {
                return new Builder();
            }

            public static class Builder {
                private final Input input = new Input();

                public Builder fileUrls(List<String> fileUrls) {
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
            private String vocabularyId;

            @JsonProperty("channel_id")
            private List<Integer> channelId;

            @JsonProperty("special_word_filter")
            private String specialWordFilter;

            @JsonProperty("diarization_enabled")
            private Boolean diarizationEnabled;

            @JsonProperty("disfluency_removal_enabled")
            private Boolean disfluencyRemovalEnabled;

            @JsonProperty("timestamp_alignment_enabled")
            private Boolean timestampAlignmentEnabled;

            @JsonProperty("speaker_count")
            private Integer speakerCount;

            @JsonProperty("language_hints")
            private List<String> languageHints;

            public String getVocabularyId() {
                return vocabularyId;
            }

            public void setVocabularyId(String vocabularyId) {
                this.vocabularyId = vocabularyId;
            }

            public List<Integer> getChannelId() {
                return channelId;
            }

            public void setChannelId(List<Integer> channelId) {
                this.channelId = channelId;
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

            public Boolean getDisfluencyRemovalEnabled() {
                return disfluencyRemovalEnabled;
            }

            public void setDisfluencyRemovalEnabled(Boolean disfluencyRemovalEnabled) {
                this.disfluencyRemovalEnabled = disfluencyRemovalEnabled;
            }

            public Boolean getTimestampAlignmentEnabled() {
                return timestampAlignmentEnabled;
            }

            public void setTimestampAlignmentEnabled(Boolean timestampAlignmentEnabled) {
                this.timestampAlignmentEnabled = timestampAlignmentEnabled;
            }

            public Integer getSpeakerCount() {
                return speakerCount;
            }

            public void setSpeakerCount(Integer speakerCount) {
                this.speakerCount = speakerCount;
            }

            public List<String> getLanguageHints() {
                return languageHints;
            }

            public void setLanguageHints(List<String> languageHints) {
                this.languageHints = languageHints;
            }

            public static Builder builder() {
                return new Builder();
            }

            public static class Builder {
                private final Parameters parameters = new Parameters();

                public Builder vocabularyId(String vocabularyId) {
                    parameters.vocabularyId = vocabularyId;
                    return this;
                }

                public Builder channelId(List<Integer> channelId) {
                    parameters.channelId = channelId;
                    return this;
                }

                public Builder specialWordFilter(String specialWordFilter) {
                    parameters.specialWordFilter = specialWordFilter;
                    return this;
                }

                public Builder diarizationEnabled(Boolean diarizationEnabled) {
                    parameters.diarizationEnabled = diarizationEnabled;
                    return this;
                }

                public Builder disfluencyRemovalEnabled(Boolean disfluencyRemovalEnabled) {
                    parameters.disfluencyRemovalEnabled = disfluencyRemovalEnabled;
                    return this;
                }

                public Builder timestampAlignmentEnabled(Boolean timestampAlignmentEnabled) {
                    parameters.timestampAlignmentEnabled = timestampAlignmentEnabled;
                    return this;
                }

                public Builder speakerCount(Integer speakerCount) {
                    parameters.speakerCount = speakerCount;
                    return this;
                }

                public Builder languageHints(List<String> languageHints) {
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
            @JsonProperty("request_id") String requestId,
            @JsonProperty("output") Output output
    ) {
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public record Output(
                @JsonProperty("task_status") String taskStatus,
                @JsonProperty("task_id") String taskId) {
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record AsrResponse(
            @JsonProperty("request_id") String requestId,
            @JsonProperty("output") Output output,
            @JsonProperty("usage") Usage usage
    ) {
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public record Output(
                @JsonProperty("task_status") String taskStatus,
                @JsonProperty("task_id") String taskId,
                @JsonProperty("submit_time") String submitTime,
                @JsonProperty("scheduled_time") String scheduledTime,
                @JsonProperty("end_time") String endTime,
                @JsonProperty("results") List<Result> results,
                @JsonProperty("task_metrics") TaskMetrics taskMetrics
        ) {
            public record Result(
                    @JsonProperty("file_url") String fileUrl,
                    @JsonProperty("transcription_url") String transcriptionUrl,
                    @JsonProperty("subtask_status") String subtaskStatus
            ) {}

            public record TaskMetrics(
                    @JsonProperty("TOTAL") Integer total,
                    @JsonProperty("SUCCEEDED") Integer succeeded,
                    @JsonProperty("FAILED") Integer failed
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
                @JsonProperty("file_url") String fileUrl,
                @JsonProperty("properties") Properties properties,
                @JsonProperty("transcripts") List<DashScopeAudioTranscription> transcripts
        ) {
            @JsonInclude(JsonInclude.Include.NON_NULL)
            public record Properties(
                    @JsonProperty("audio_format") String audioFormat,
                    @JsonProperty("channels") List<Integer> channels,
                    @JsonProperty("original_sampling_rate") Integer originalSamplingRate,
                    @JsonProperty("original_duration_in_milliseconds") Integer originalDurationInMilliseconds
            ) {}
        }

    }

}
