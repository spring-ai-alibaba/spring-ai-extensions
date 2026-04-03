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

/**
 * @author buvidk
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DashScopeMultimodalEmbeddingOptions implements EmbeddingOptions {

	// ID of the model to use.
	private @Nullable @JsonProperty("model") String model;

	// The number of dimensions the resulting output embeddings should have.
	private @Nullable @JsonProperty("dimension") Integer dimensions;

	// User-specified output vector format, currently only 'dense' is supported.
	private @Nullable @JsonProperty("output_type") String outputType;

	// Controls the frame rate of the video. The smaller the ratio, the fewer frames are actually extracted.
	// Range [0,1]. Default is 1.0.
	private @Nullable @JsonProperty("fps") Float fps;

	// Add custom task instructions to guide the model's intent. Recommended in English.
	private @Nullable @JsonProperty("instruct") String instruct;

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {

		private final DashScopeMultimodalEmbeddingOptions options = new DashScopeMultimodalEmbeddingOptions();

		public Builder model(@Nullable String model) {
			this.options.setModel(model);
			return this;
		}

		public Builder dimensions(@Nullable Integer dimensions) {
			this.options.setDimensions(dimensions);
			return this;
		}

		public Builder outputType(@Nullable String outputType) {
			this.options.setOutputType(outputType);
			return this;
		}

		public Builder fps(@Nullable Float fps) {
			this.options.setFps(fps);
			return this;
		}

		public Builder instruct(@Nullable String instruct) {
			this.options.setInstruct(instruct);
			return this;
		}

		public DashScopeMultimodalEmbeddingOptions build() {
			return this.options;
		}

	}

	@Override
	public @Nullable String getModel() {
		return this.model;
	}

	public void setModel(@Nullable String model) {
		this.model = model;
	}

	@Override
	public @Nullable Integer getDimensions() {
		return this.dimensions;
	}

	public void setDimensions(@Nullable Integer dimensions) {
		this.dimensions = dimensions;
	}

	public @Nullable String getOutputType() {
		return outputType;
	}

	public void setOutputType(@Nullable String outputType) {
		this.outputType = outputType;
	}

	public @Nullable Float getFps() {
		return fps;
	}

	public void setFps(@Nullable Float fps) {
		this.fps = fps;
	}

	public @Nullable String getInstruct() {
		return instruct;
	}

	public void setInstruct(@Nullable String instruct) {
		this.instruct = instruct;
	}

}
