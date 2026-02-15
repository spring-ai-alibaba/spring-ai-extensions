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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.ai.audio.transcription.AudioTranscriptionOptions;
import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;

import java.util.List;

/**
 * @author yingzi
 * @since 2026/2/1
 */

public class DashScopeAudioTranscriptionPrompt extends AudioTranscriptionPrompt {

    private final List<TranscriptionUserMessage> messages;

    private final List<String> fileUrls;

    public DashScopeAudioTranscriptionPrompt(AudioTranscriptionOptions options, TranscriptionUserMessage  message) {
        super(null, options);
        this.messages = List.of(message);
        this.fileUrls = null;
    }

    public DashScopeAudioTranscriptionPrompt(AudioTranscriptionOptions options, List<String> fileUrls) {
        super(null, options);
        this.messages = null;
        this.fileUrls = fileUrls;
    }

    public List<TranscriptionUserMessage> getMessages() {
        return messages;
    }

    public List<String> getFileUrls() {
        return fileUrls;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class TranscriptionUserMessage {
        @JsonProperty("role")
        private String role = "user";

        @JsonProperty("content")
        private List<Content> content;

        public TranscriptionUserMessage(List<Content> content) {
            this.content = content;
        }

        public String getRole() {
            return role;
        }

        public List<Content> getContent() {
            return content;
        }

        @JsonInclude(JsonInclude.Include.NON_NULL)
        public static class Content {
            @JsonProperty("type")
            private String type;

            @JsonProperty("input_audio")
            private InputAudio inputAudio;

            @JsonProperty("video_url")
            private VideoUrl videoUrl;

            public Content(String type, InputAudio inputAudio) {
                this.type = type;
                this.inputAudio = inputAudio;
            }

            public Content(String type, VideoUrl videoUrl) {
                this.type = type;
                this.videoUrl = videoUrl;
            }

            public String getType() {
                return type;
            }

            public InputAudio getInputAudio() {
                return inputAudio;
            }

            public VideoUrl getVideoUrl() {
                return videoUrl;
            }
        }

        public static class InputAudio {
            @JsonProperty("data")
            private String data;

            @JsonProperty("format")
            private String format;

            public InputAudio(String data, String format) {
                this.data = data;
                this.format = format;
            }

            public String getData() {
                return data;
            }

            public String getFormat() {
                return format;
            }
        }

        public static class VideoUrl {
            @JsonProperty("url")
            private String url;

            public VideoUrl(String url) {
                this.url = url;
            }

            public String getUrl() {
                return url;
            }
        }
    }
}
