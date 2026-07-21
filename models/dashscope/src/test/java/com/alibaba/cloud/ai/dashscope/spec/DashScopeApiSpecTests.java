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
package com.alibaba.cloud.ai.dashscope.spec;

import java.util.List;
import java.util.Map;

import com.alibaba.cloud.ai.dashscope.spec.DashScopeApiSpec.ChatCompletionMessage.MediaContent;
import com.alibaba.cloud.ai.dashscope.spec.DashScopeApiSpec.ChatCompletionRequestParameter.ToolChoiceBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test cases for {@link DashScopeApiSpec} types.
 */
class DashScopeApiSpecTests {

    @Test
    void autoToolChoice() {
        assertThat(ToolChoiceBuilder.AUTO).isEqualTo("auto");
    }

    @Test
    void noneToolChoice() {
        assertThat(ToolChoiceBuilder.NONE).isEqualTo("none");
    }

    @Test
    void requiredToolChoice() {
        assertThat(ToolChoiceBuilder.REQUIRED).isEqualTo("required");
    }

    @Test
    void functionToolChoice() {
        assertThat(ToolChoiceBuilder.function("getCurrentWeather"))
            .isEqualTo(Map.of("type", "function", "function", Map.of("name", "getCurrentWeather")));
    }

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void mediaContentSerializesFpsForSingleVideo() throws Exception {
        MediaContent content = new MediaContent("video", null, null, "https://example.com/1.mp4", null, 2.0);
        String json = objectMapper.writeValueAsString(content);
        assertThat(json).contains("\"video\":\"https://example.com/1.mp4\"");
        assertThat(json).contains("\"fps\":2.0");
    }

    @Test
    void mediaContentSerializesFpsForFrameList() throws Exception {
        MediaContent content = new MediaContent("video", null, null,
                List.of("https://example.com/f1.jpg", "https://example.com/f2.jpg"), null, 4.0);
        String json = objectMapper.writeValueAsString(content);
        assertThat(json).contains("\"fps\":4");
    }

    @Test
    void mediaContentOmitsFpsWhenNull() throws Exception {
        MediaContent content = new MediaContent("video", null, null, "https://example.com/1.mp4", null, (Double) null);
        String json = objectMapper.writeValueAsString(content);
        assertThat(json).doesNotContain("fps");
    }

}
