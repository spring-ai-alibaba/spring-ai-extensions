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

package com.alibaba.cloud.ai.dashscope.sdk.embedding;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jspecify.annotations.Nullable;
import org.springframework.ai.embedding.EmbeddingOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Options for DashScope SDK embedding model.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DashScopeSdkEmbeddingOptions implements EmbeddingOptions {

	@JsonProperty("model")
	private final @Nullable String model;

	@JsonProperty("text_type")
	private final @Nullable String textType;

	@JsonProperty("dimensions")
	private final @Nullable Integer dimensions;

	@JsonIgnore
	private final @Nullable Map<String, String> httpHeaders;

	protected DashScopeSdkEmbeddingOptions(@Nullable String model, @Nullable String textType,
			@Nullable Integer dimensions, @Nullable Map<String, String> httpHeaders) {
		this.model = model;
		this.textType = textType;
		this.dimensions = dimensions;
		this.httpHeaders = httpHeaders != null ? Map.copyOf(httpHeaders) : null;
	}

	public static Builder builder() {
		return new Builder();
	}

	@Override
	public @Nullable String getModel() {
		return this.model;
	}

	public @Nullable String getTextType() {
		return this.textType;
	}

	@Override
	public @Nullable Integer getDimensions() {
		return this.dimensions;
	}

	public @Nullable Map<String, String> getHttpHeaders() {
		return this.httpHeaders;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		DashScopeSdkEmbeddingOptions that = (DashScopeSdkEmbeddingOptions) o;
		return Objects.equals(this.model, that.model) && Objects.equals(this.textType, that.textType)
				&& Objects.equals(this.dimensions, that.dimensions) && Objects.equals(this.httpHeaders, that.httpHeaders);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.model, this.textType, this.dimensions, this.httpHeaders);
	}

	@Override
	public String toString() {
		return "DashScopeSdkEmbeddingOptions{" + "model='" + this.model + '\'' + ", textType='" + this.textType + '\''
				+ ", dimensions=" + this.dimensions + ", httpHeaders=" + this.httpHeaders + '}';
	}

	public static final class Builder extends AbstractBuilder<DashScopeSdkEmbeddingOptions, Builder> {

		private Builder() {
		}

		public Builder from(DashScopeSdkEmbeddingOptions fromOptions) {
			this.model = fromOptions.getModel();
			this.textType = fromOptions.getTextType();
			this.dimensions = fromOptions.getDimensions();
			this.httpHeaders = fromOptions.getHttpHeaders();
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
			if (from instanceof DashScopeSdkEmbeddingOptions castFrom) {
				if (castFrom.getTextType() != null) {
					this.textType = castFrom.getTextType();
				}
			if (castFrom.getHttpHeaders() != null) {
                    if (this.httpHeaders == null) {
                        this.httpHeaders = new HashMap<>(castFrom.getHttpHeaders());
                    }
                    else {
                        Map<String, String> merged = new HashMap<>(this.httpHeaders);
                        merged.putAll(castFrom.getHttpHeaders());
                        this.httpHeaders = merged;
                    }
				}
			}
			return this;
		}

		public DashScopeSdkEmbeddingOptions build() {
			return new DashScopeSdkEmbeddingOptions(this.model, this.textType, this.dimensions, this.httpHeaders);
		}

	}

	protected abstract static class AbstractBuilder<O extends DashScopeSdkEmbeddingOptions, B extends AbstractBuilder<O, B>> {

		protected @Nullable String model;

		protected @Nullable String textType;

		protected @Nullable Integer dimensions;

		protected @Nullable Map<String, String> httpHeaders;

		@SuppressWarnings("unchecked")
		protected B self() {
			return (B) this;
		}

		public B model(@Nullable String model) {
			this.model = model;
			return self();
		}

		public B textType(@Nullable String textType) {
			this.textType = textType;
			return self();
		}

		public B dimensions(@Nullable Integer dimensions) {
			this.dimensions = dimensions;
			return self();
		}

		public B httpHeaders(@Nullable Map<String, String> httpHeaders) {
			this.httpHeaders = httpHeaders;
			return self();
		}

		public abstract O build();

	}

}
