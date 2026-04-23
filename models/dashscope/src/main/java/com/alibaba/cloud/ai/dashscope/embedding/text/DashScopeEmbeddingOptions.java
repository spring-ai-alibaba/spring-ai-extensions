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
package com.alibaba.cloud.ai.dashscope.embedding.text;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.ai.embedding.EmbeddingOptions;

/**
 * @author nuocheng.lxm
 * @author why_ohh
 * @author yuluo
 * @author <a href="mailto:550588941@qq.com">why_ohh</a>
 * @since 2024/8/1 11:14
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DashScopeEmbeddingOptions implements EmbeddingOptions {

	public static final String OUTPUT_TYPE_DENSE = "dense";

	public static final String OUTPUT_TYPE_SPARSE = "sparse";

	public static final String OUTPUT_TYPE_DENSE_AND_SPARSE = "dense&sparse";

	private @JsonProperty("model") String model;

	private @JsonProperty("text_type") String textType;

	private @JsonProperty("output_type") String outputType;

	private @JsonProperty("dimensions") Integer dimensions;

	@JsonIgnore
	private String embeddingsPath;

	public static Builder builder() {
		return new Builder();
	}

	@Override
	public String getModel() {
		return this.model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	@Override
	public Integer getDimensions() {
		return this.dimensions;
	}

	public void setDimensions(Integer dimensions) {
		this.dimensions = dimensions;
	}

	public String getTextType() {
		return this.textType;
	}

	public void setTextType(String textType) {
		this.textType = textType;
	}

	public String getOutputType() {
		return this.outputType;
	}

	public void setOutputType(String outputType) {
		if (outputType != null && !OUTPUT_TYPE_DENSE.equals(outputType) && !OUTPUT_TYPE_SPARSE.equals(outputType)
				&& !OUTPUT_TYPE_DENSE_AND_SPARSE.equals(outputType)) {
			throw new IllegalArgumentException(
					"outputType only supports " + OUTPUT_TYPE_DENSE + ", " + OUTPUT_TYPE_SPARSE + ", " + OUTPUT_TYPE_DENSE_AND_SPARSE);
		}
		this.outputType = outputType;
	}

	public String getEmbeddingsPath() {
		return embeddingsPath;
	}

	public void setEmbeddingsPath(String embeddingsPath) {
		this.embeddingsPath = embeddingsPath;
	}

	public static class Builder {

		protected DashScopeEmbeddingOptions options;

		public Builder() {
			this.options = new DashScopeEmbeddingOptions();
		}

		public Builder model(String model) {
			this.options.setModel(model);
			return this;
		}

		@Deprecated
		public Builder withModel(String model) {
			return model(model);
		}

		public Builder dimensions(Integer dimensions) {
			this.options.setDimensions(dimensions);
			return this;
		}

		@Deprecated
		public Builder withDimensions(Integer dimensions) {
			return dimensions(dimensions);
		}

		public Builder textType(String textType) {
			this.options.setTextType(textType);
			return this;
		}

		public Builder outputType(String outputType) {
			this.options.setOutputType(outputType);
			return this;
		}

		@Deprecated
		public Builder withTextType(String textType) {
			return textType(textType);
		}


		public Builder embeddingsPath(String embeddingsPath) {
			this.options.setEmbeddingsPath(embeddingsPath);
			return this;
		}

		@Deprecated
		public Builder withEmbeddingsPath(String embeddingsPath) {
			return embeddingsPath(embeddingsPath);
		}

		public DashScopeEmbeddingOptions build() {
			return this.options;
		}

	}

}
