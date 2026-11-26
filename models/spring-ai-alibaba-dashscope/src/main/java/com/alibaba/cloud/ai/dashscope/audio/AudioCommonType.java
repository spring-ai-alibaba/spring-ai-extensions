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

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author yingzi
 * @since 2026/1/26
 */

public class AudioCommonType {

    public enum TextType {

        @JsonProperty("PlainText") PLAIN_TEXT("PlainText"),
        @JsonProperty("SSML") SSML("SSML");

        private final String value;

        TextType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

    }

    public enum Format {

        @JsonProperty("pcm") PCM("pcm"),
        @JsonProperty("wav") WAV("wav"),
        @JsonProperty("mp3") MP3("mp3"),
        @JsonProperty("opus") OPUS("opus"),
        @JsonProperty("speex") SPEEX("speex"),
        @JsonProperty("aac") AAC("aac"),
        @JsonProperty("amr") AMR("amr");

        public final String formatType;

        Format(String value) {
            this.formatType = value;
        }

        public String getValue() {
            return this.formatType;
        }

    }
}
