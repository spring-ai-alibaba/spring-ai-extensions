/*
 * Copyright 2026-2027 the original author or authors.
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

package com.alibaba.cloud.ai.dashscope.embedding.multimodal;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jspecify.annotations.Nullable;
import org.springframework.ai.embedding.EmbeddingOptions;

import java.util.Objects;

/**
 * @author buvidk
 * @author guanxu
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DashScopeMultimodalEmbeddingOptions implements EmbeddingOptions {

    /**
     * ID of the model to use.
     */
    @JsonProperty("model")
    private final @Nullable String model;

    /**
     * The number of dimensions the resulting output embeddings should have.
     */
    @JsonProperty("dimension")
    private final @Nullable Integer dimensions;

    /**
     * User-specified output vector format, currently only 'dense' is supported.
     */
    @JsonProperty("output_type")
    private final @Nullable String outputType;

    /**
     * Controls the frame rate of the video. The smaller the ratio, the fewer frames are actually extracted. Range [0,1]. Default is 1.0.
     */
    @JsonProperty("fps")
    private final @Nullable Float fps;

    /**
     * Add custom task instructions to guide the model's intent. Recommended in English.
     */
    @JsonProperty("instruct")
    private final @Nullable String instruct;

    protected DashScopeMultimodalEmbeddingOptions(
            @Nullable String model,
            @Nullable Integer dimensions,
            @Nullable String outputType,
            @Nullable Float fps,
            @Nullable String instruct) {
        this.model = model;
        this.dimensions = dimensions;
        this.outputType = outputType;
        this.fps = fps;
        this.instruct = instruct;
    }

    @Override
    public @Nullable String getModel() {
        return this.model;
    }

    @Override
    public @Nullable Integer getDimensions() {
        return this.dimensions;
    }

    public @Nullable String getOutputType() {
        return this.outputType;
    }

    public @Nullable Float getFps() {
        return this.fps;
    }

    public @Nullable String getInstruct() {
        return this.instruct;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DashScopeMultimodalEmbeddingOptions that = (DashScopeMultimodalEmbeddingOptions) o;
        return Objects.equals(this.model, that.model) && Objects.equals(this.dimensions, that.dimensions)
                && Objects.equals(this.outputType, that.outputType) && Objects.equals(this.fps, that.fps)
                && Objects.equals(this.instruct, that.instruct);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.model, this.dimensions, this.outputType, this.fps, this.instruct);
    }

    @Override
    public String toString() {
        return "DashScopeMultimodalEmbeddingOptions{" + "model='" + this.model + '\'' + ", dimensions="
                + this.dimensions + ", outputType='" + this.outputType + '\'' + ", fps=" + this.fps + ", instruct='"
                + this.instruct + '\'' + '}';
    }

    public static class Builder {

        protected @Nullable String model;

        protected @Nullable Integer dimensions;

        protected @Nullable String outputType;

        protected @Nullable Float fps;

        protected @Nullable String instruct;

        public Builder model(@Nullable String model) {
            this.model = model;
            return this;
        }

        public Builder dimensions(@Nullable Integer dimensions) {
            this.dimensions = dimensions;
            return this;
        }

        public Builder outputType(@Nullable String outputType) {
            this.outputType = outputType;
            return this;
        }

        public Builder fps(@Nullable Float fps) {
            this.fps = fps;
            return this;
        }

        public Builder instruct(@Nullable String instruct) {
            this.instruct = instruct;
            return this;
        }

        public Builder from(DashScopeMultimodalEmbeddingOptions fromOptions) {
            this.model = fromOptions.getModel();
            this.dimensions = fromOptions.getDimensions();
            this.outputType = fromOptions.getOutputType();
            this.fps = fromOptions.getFps();
            this.instruct = fromOptions.getInstruct();
            return this;
        }

        public Builder merge(@Nullable EmbeddingOptions from) {
            if (from == null) {
                return this;
            }
            if (from.getModel() != null) {
                this.model = from.getModel();
            }
            if (from.getDimensions() != null) {
                this.dimensions = from.getDimensions();
            }
            if (from instanceof DashScopeMultimodalEmbeddingOptions castFrom) {
                if (castFrom.getOutputType() != null) {
                    this.outputType = castFrom.getOutputType();
                }
                if (castFrom.getFps() != null) {
                    this.fps = castFrom.getFps();
                }
                if (castFrom.getInstruct() != null) {
                    this.instruct = castFrom.getInstruct();
                }
            }
            return this;
        }

        public DashScopeMultimodalEmbeddingOptions build() {
            return new DashScopeMultimodalEmbeddingOptions(this.model, this.dimensions, this.outputType, this.fps, this.instruct);
        }

    }

}
