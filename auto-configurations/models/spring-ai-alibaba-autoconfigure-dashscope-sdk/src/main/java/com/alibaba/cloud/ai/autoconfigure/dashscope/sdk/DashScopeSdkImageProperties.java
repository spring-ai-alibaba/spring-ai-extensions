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

import com.alibaba.cloud.ai.dashscope.sdk.image.DashScopeSdkImageOptions;
import org.jspecify.annotations.Nullable;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.DeprecatedConfigurationProperty;

/**
 * DashScope SDK image model properties.
 */
@ConfigurationProperties(DashScopeSdkImageProperties.CONFIG_PREFIX)
public class DashScopeSdkImageProperties extends DashScopeSdkParentProperties {

    public static final String CONFIG_PREFIX = "spring.ai.dashscope.sdk.image";

    private boolean enabled = true;

    private @Nullable String model = "wanx-v1";

    private @Nullable Integer n = 1;

    private @Nullable Integer width;

    private @Nullable Integer height;

    private @Nullable String size;

    private @Nullable String responseFormat;

    private @Nullable String style;

    private @Nullable Integer seed;

    private @Nullable String negativePrompt;

    private @Nullable String refImage;

    private @Nullable Integer pollIntervalMs;

    private @Nullable Boolean async;

    private @Nullable Map<String, String> httpHeaders;

    private @Nullable Map<String, Object> extraBody;

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

    public @Nullable Integer getN() {
        return this.n;
    }

    public void setN(@Nullable Integer n) {
        this.n = n;
    }

    public @Nullable Integer getWidth() {
        return this.width;
    }

    public void setWidth(@Nullable Integer width) {
        this.width = width;
    }

    public @Nullable Integer getHeight() {
        return this.height;
    }

    public void setHeight(@Nullable Integer height) {
        this.height = height;
    }

    public @Nullable String getSize() {
        return this.size;
    }

    public void setSize(@Nullable String size) {
        this.size = size;
    }

    public @Nullable String getResponseFormat() {
        return this.responseFormat;
    }

    public void setResponseFormat(@Nullable String responseFormat) {
        this.responseFormat = responseFormat;
    }

    public @Nullable String getStyle() {
        return this.style;
    }

    public void setStyle(@Nullable String style) {
        this.style = style;
    }

    public @Nullable Integer getSeed() {
        return this.seed;
    }

    public void setSeed(@Nullable Integer seed) {
        this.seed = seed;
    }

    public @Nullable String getNegativePrompt() {
        return this.negativePrompt;
    }

    public void setNegativePrompt(@Nullable String negativePrompt) {
        this.negativePrompt = negativePrompt;
    }

    public @Nullable String getRefImage() {
        return this.refImage;
    }

    public void setRefImage(@Nullable String refImage) {
        this.refImage = refImage;
    }

    public @Nullable Integer getPollIntervalMs() {
        return this.pollIntervalMs;
    }

    public void setPollIntervalMs(@Nullable Integer pollIntervalMs) {
        this.pollIntervalMs = pollIntervalMs;
    }

    public @Nullable Boolean getAsync() {
        return this.async;
    }

    public void setAsync(@Nullable Boolean async) {
        this.async = async;
    }

    public @Nullable Map<String, String> getHttpHeaders() {
        return this.httpHeaders;
    }

    public void setHttpHeaders(@Nullable Map<String, String> httpHeaders) {
        this.httpHeaders = httpHeaders;
    }

    public @Nullable Map<String, Object> getExtraBody() {
        return this.extraBody;
    }

    public void setExtraBody(@Nullable Map<String, Object> extraBody) {
        this.extraBody = extraBody;
    }

    public DashScopeSdkImageOptions toOptions() {
        return DashScopeSdkImageOptions.builder()
                .model(this.model)
                .n(this.n)
                .width(this.width)
                .height(this.height)
                .size(this.size)
                .responseFormat(this.responseFormat)
                .style(this.style)
                .seed(this.seed)
                .negativePrompt(this.negativePrompt)
                .refImage(this.refImage)
                .pollIntervalMs(this.pollIntervalMs)
                .async(this.async)
                .httpHeaders(this.httpHeaders)
                .extraBody(this.extraBody)
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

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".n")
        @Deprecated(since = "2.0.0", forRemoval = true)
        public @Nullable Integer getN() {
            return DashScopeSdkImageProperties.this.getN();
        }

        public void setN(Integer n) {
            DashScopeSdkImageProperties.this.setN(n);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".model")
        @Deprecated(since = "2.0.0", forRemoval = true)
        public @Nullable String getModel() {
            return DashScopeSdkImageProperties.this.getModel();
        }

        public void setModel(String model) {
            DashScopeSdkImageProperties.this.setModel(model);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".width")
        @Deprecated(since = "2.0.0", forRemoval = true)
        public @Nullable Integer getWidth() {
            return DashScopeSdkImageProperties.this.getWidth();
        }

        public void setWidth(Integer width) {
            DashScopeSdkImageProperties.this.setWidth(width);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".height")
        @Deprecated(since = "2.0.0", forRemoval = true)
        public @Nullable Integer getHeight() {
            return DashScopeSdkImageProperties.this.getHeight();
        }

        public void setHeight(Integer height) {
            DashScopeSdkImageProperties.this.setHeight(height);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".size")
        @Deprecated(since = "2.0.0", forRemoval = true)
        public @Nullable String getSize() {
            return DashScopeSdkImageProperties.this.getSize();
        }

        public void setSize(String size) {
            DashScopeSdkImageProperties.this.setSize(size);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".response-format")
        @Deprecated(since = "2.0.0", forRemoval = true)
        public @Nullable String getResponseFormat() {
            return DashScopeSdkImageProperties.this.getResponseFormat();
        }

        public void setResponseFormat(String responseFormat) {
            DashScopeSdkImageProperties.this.setResponseFormat(responseFormat);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".style")
        @Deprecated(since = "2.0.0", forRemoval = true)
        public @Nullable String getStyle() {
            return DashScopeSdkImageProperties.this.getStyle();
        }

        public void setStyle(String style) {
            DashScopeSdkImageProperties.this.setStyle(style);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".seed")
        @Deprecated(since = "2.0.0", forRemoval = true)
        public @Nullable Integer getSeed() {
            return DashScopeSdkImageProperties.this.getSeed();
        }

        public void setSeed(Integer seed) {
            DashScopeSdkImageProperties.this.setSeed(seed);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".negative-prompt")
        @Deprecated(since = "2.0.0", forRemoval = true)
        public @Nullable String getNegativePrompt() {
            return DashScopeSdkImageProperties.this.getNegativePrompt();
        }

        public void setNegativePrompt(String negativePrompt) {
            DashScopeSdkImageProperties.this.setNegativePrompt(negativePrompt);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".ref-image")
        @Deprecated(since = "2.0.0", forRemoval = true)
        public @Nullable String getRefImage() {
            return DashScopeSdkImageProperties.this.getRefImage();
        }

        public void setRefImage(String refImage) {
            DashScopeSdkImageProperties.this.setRefImage(refImage);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".poll-interval-ms")
        @Deprecated(since = "2.0.0", forRemoval = true)
        public @Nullable Integer getPollIntervalMs() {
            return DashScopeSdkImageProperties.this.getPollIntervalMs();
        }

        public void setPollIntervalMs(Integer pollIntervalMs) {
            DashScopeSdkImageProperties.this.setPollIntervalMs(pollIntervalMs);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".async")
        @Deprecated(since = "2.0.0", forRemoval = true)
        public @Nullable Boolean getAsync() {
            return DashScopeSdkImageProperties.this.getAsync();
        }

        public void setAsync(Boolean async) {
            DashScopeSdkImageProperties.this.setAsync(async);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".http-headers")
        @Deprecated(since = "2.0.0", forRemoval = true)
        public @Nullable Map<String, String> getHttpHeaders() {
            return DashScopeSdkImageProperties.this.getHttpHeaders();
        }

        public void setHttpHeaders(Map<String, String> httpHeaders) {
            DashScopeSdkImageProperties.this.setHttpHeaders(httpHeaders);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".extra-body")
        @Deprecated(since = "2.0.0", forRemoval = true)
        public @Nullable Map<String, Object> getExtraBody() {
            return DashScopeSdkImageProperties.this.getExtraBody();
        }

        public void setExtraBody(Map<String, Object> extraBody) {
            DashScopeSdkImageProperties.this.setExtraBody(extraBody);
        }

    }

}
