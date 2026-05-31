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
import org.jspecify.annotations.Nullable;
import org.springframework.ai.embedding.EmbeddingOptions;

import java.util.Objects;

/**
 * @author nuocheng.lxm
 * @author why_ohh
 * @author yuluo
 * @author <a href="mailto:550588941@qq.com">why_ohh</a>
 * @author guanxu
 * @since 2024/8/1 11:14
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DashScopeEmbeddingOptions implements EmbeddingOptions {

	public static final String OUTPUT_TYPE_DENSE = "dense";

	public static final String OUTPUT_TYPE_SPARSE = "sparse";

	public static final String OUTPUT_TYPE_DENSE_AND_SPARSE = "dense&sparse";

	@JsonProperty("model")
	private final @Nullable String model;

	@JsonProperty("text_type")
	private final @Nullable String textType;

	@JsonProperty("dimensions")
	private final @Nullable Integer dimensions;

	@JsonProperty("output_type")
	private final @Nullable String outputType;

	@JsonIgnore
	private final @Nullable String embeddingsPath;

	protected DashScopeEmbeddingOptions(@Nullable String model, @Nullable String textType, @Nullable Integer dimensions,
			@Nullable String outputType, @Nullable String embeddingsPath) {
		validateOutputType(outputType);
		this.model = model;
		this.textType = textType;
		this.dimensions = dimensions;
		this.outputType = outputType;
		this.embeddingsPath = embeddingsPath;
	}

	private static void validateOutputType(@Nullable String outputType) {
		if (outputType != null && !OUTPUT_TYPE_DENSE.equals(outputType) && !OUTPUT_TYPE_SPARSE.equals(outputType)
				&& !OUTPUT_TYPE_DENSE_AND_SPARSE.equals(outputType)) {
			throw new IllegalArgumentException(
					"outputType only supports " + OUTPUT_TYPE_DENSE + ", " + OUTPUT_TYPE_SPARSE + ", " + OUTPUT_TYPE_DENSE_AND_SPARSE);
		}
	}

    public static DashScopeEmbeddingOptions fromOptions(DashScopeEmbeddingOptions fromOptions) {
        return builder()
                .model(fromOptions.getModel())
                .textType(fromOptions.getTextType())
                .dimensions(fromOptions.getDimensions())
                .outputType(fromOptions.getOutputType())
                .embeddingsPath(fromOptions.getEmbeddingsPath())
                .build();
    }

	public static Builder builder() {
		return new Builder();
	}

	@Override
	public @Nullable String getModel() {
		return this.model;
	}

	@Override
	public @Nullable Integer getDimensions() {
		return this.dimensions;
	}

	public @Nullable String getTextType() {
		return this.textType;
	}

	public @Nullable String getOutputType() {
		return this.outputType;
	}

	public @Nullable String getEmbeddingsPath() {
		return this.embeddingsPath;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		DashScopeEmbeddingOptions that = (DashScopeEmbeddingOptions) o;
		return Objects.equals(this.model, that.model) && Objects.equals(this.textType, that.textType)
				&& Objects.equals(this.dimensions, that.dimensions) && Objects.equals(this.outputType, that.outputType)
				&& Objects.equals(this.embeddingsPath, that.embeddingsPath);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.model, this.textType, this.dimensions, this.outputType, this.embeddingsPath);
	}

	@Override
	public String toString() {
		return "DashScopeEmbeddingOptions{" + "model='" + this.model + '\'' + ", textType='" + this.textType + '\''
				+ ", dimensions=" + this.dimensions + ", outputType='" + this.outputType + '\'' + ", embeddingsPath='"
				+ this.embeddingsPath + '\'' + '}';
	}

	public static class Builder {

		protected @Nullable String model;

		protected @Nullable String textType;

		protected @Nullable Integer dimensions;

		protected @Nullable String outputType;

		protected @Nullable String embeddingsPath;

		public Builder model(@Nullable String model) {
			this.model = model;
			return this;
		}

		public Builder dimensions(@Nullable Integer dimensions) {
			this.dimensions = dimensions;
			return this;
		}

		public Builder textType(@Nullable String textType) {
			this.textType = textType;
			return this;
		}

		public Builder outputType(@Nullable String outputType) {
			validateOutputType(outputType);
			this.outputType = outputType;
			return this;
		}

		public Builder embeddingsPath(@Nullable String embeddingsPath) {
			this.embeddingsPath = embeddingsPath;
			return this;
		}

		public Builder from(DashScopeEmbeddingOptions fromOptions) {
			this.model = fromOptions.getModel();
			this.textType = fromOptions.getTextType();
			this.dimensions = fromOptions.getDimensions();
			this.outputType = fromOptions.getOutputType();
			this.embeddingsPath = fromOptions.getEmbeddingsPath();
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
			if (from instanceof DashScopeEmbeddingOptions castFrom) {
				if (castFrom.getTextType() != null) {
					this.textType = castFrom.getTextType();
				}
				if (castFrom.getOutputType() != null) {
					this.outputType = castFrom.getOutputType();
				}
				if (castFrom.getEmbeddingsPath() != null) {
					this.embeddingsPath = castFrom.getEmbeddingsPath();
				}
			}
			return this;
		}

		public DashScopeEmbeddingOptions build() {
			return new DashScopeEmbeddingOptions(this.model, this.textType, this.dimensions, this.outputType,
					this.embeddingsPath);
		}

	}

}
