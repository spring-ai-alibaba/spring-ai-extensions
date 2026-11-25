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

import com.alibaba.cloud.ai.dashscope.sdk.embedding.DashScopeSdkEmbeddingOptions;
import org.jspecify.annotations.Nullable;
import org.springframework.ai.document.MetadataMode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.DeprecatedConfigurationProperty;

/**
 * DashScope SDK embedding model properties.
 */
@ConfigurationProperties(DashScopeSdkEmbeddingProperties.CONFIG_PREFIX)
public class DashScopeSdkEmbeddingProperties extends DashScopeSdkParentProperties {

    public static final String CONFIG_PREFIX = "spring.ai.dashscope.sdk.embedding";

    private boolean enabled = true;

    private MetadataMode metadataMode = MetadataMode.EMBED;

    private @Nullable String model = "text-embedding-v2";

    private @Nullable String textType;

    private @Nullable Integer dimensions;

    private @Nullable Map<String, String> httpHeaders;

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public MetadataMode getMetadataMode() {
        return this.metadataMode;
    }

    public void setMetadataMode(MetadataMode metadataMode) {
        this.metadataMode = metadataMode;
    }

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

    public @Nullable Integer getDimensions() {
        return this.dimensions;
    }

    public void setDimensions(@Nullable Integer dimensions) {
        this.dimensions = dimensions;
    }

    public @Nullable Map<String, String> getHttpHeaders() {
        return this.httpHeaders;
    }

    public void setHttpHeaders(@Nullable Map<String, String> httpHeaders) {
        this.httpHeaders = httpHeaders;
    }

    public DashScopeSdkEmbeddingOptions toOptions() {
        return DashScopeSdkEmbeddingOptions.builder()
                .model(this.model)
                .textType(this.textType)
                .dimensions(this.dimensions)
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
            return DashScopeSdkEmbeddingProperties.this.getModel();
        }

        public void setModel(String model) {
            DashScopeSdkEmbeddingProperties.this.setModel(model);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".text-type")
        @Deprecated(since = "2.0.0", forRemoval = true)
        public @Nullable String getTextType() {
            return DashScopeSdkEmbeddingProperties.this.getTextType();
        }

        public void setTextType(String textType) {
            DashScopeSdkEmbeddingProperties.this.setTextType(textType);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".dimensions")
        @Deprecated(since = "2.0.0", forRemoval = true)
        public @Nullable Integer getDimensions() {
            return DashScopeSdkEmbeddingProperties.this.getDimensions();
        }

        public void setDimensions(Integer dimensions) {
            DashScopeSdkEmbeddingProperties.this.setDimensions(dimensions);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".http-headers")
        @Deprecated(since = "2.0.0", forRemoval = true)
        public @Nullable Map<String, String> getHttpHeaders() {
            return DashScopeSdkEmbeddingProperties.this.getHttpHeaders();
        }

        public void setHttpHeaders(Map<String, String> httpHeaders) {
            DashScopeSdkEmbeddingProperties.this.setHttpHeaders(httpHeaders);
        }

    }

}
