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
	private final Map<String, String> httpHeaders;

	protected DashScopeSdkEmbeddingOptions(@Nullable String model, @Nullable String textType,
			@Nullable Integer dimensions, @Nullable Map<String, String> httpHeaders) {
		this.model = model;
		this.textType = textType;
		this.dimensions = dimensions;
		this.httpHeaders = httpHeaders != null ? new HashMap<>(httpHeaders) : new HashMap<>();
	}

    public static DashScopeSdkEmbeddingOptions fromOptions(DashScopeSdkEmbeddingOptions options) {
        return builder()
                .model(options.getModel())
                .textType(options.getTextType())
                .dimensions(options.getDimensions())
                .httpHeaders(options.getHttpHeaders())
                .build();
    }

	public static DashScopeSdkEmbeddingOptionsBuilder builder() {
		return new DashScopeSdkEmbeddingOptionsBuilder();
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

	public Map<String, String> getHttpHeaders() {
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

	public static class DashScopeSdkEmbeddingOptionsBuilder {

		protected @Nullable String model;

		protected @Nullable String textType;

		protected @Nullable Integer dimensions;

		protected Map<String, String> httpHeaders = new HashMap<>();

		public DashScopeSdkEmbeddingOptionsBuilder model(@Nullable String model) {
			this.model = model;
			return this;
		}

		public DashScopeSdkEmbeddingOptionsBuilder textType(@Nullable String textType) {
			this.textType = textType;
			return this;
		}

		public DashScopeSdkEmbeddingOptionsBuilder dimensions(@Nullable Integer dimensions) {
			this.dimensions = dimensions;
			return this;
		}

		public DashScopeSdkEmbeddingOptionsBuilder httpHeaders(@Nullable Map<String, String> httpHeaders) {
			this.httpHeaders = httpHeaders != null ? new HashMap<>(httpHeaders) : new HashMap<>();
			return this;
		}

		public DashScopeSdkEmbeddingOptionsBuilder from(DashScopeSdkEmbeddingOptions fromOptions) {
			this.model = fromOptions.getModel();
			this.textType = fromOptions.getTextType();
			this.dimensions = fromOptions.getDimensions();
			this.httpHeaders = fromOptions.getHttpHeaders();
			return this;
		}

		public DashScopeSdkEmbeddingOptionsBuilder merge(@Nullable EmbeddingOptions from) {
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
				if (castFrom.getHttpHeaders() != null && !castFrom.getHttpHeaders().isEmpty()) {
					this.httpHeaders = castFrom.getHttpHeaders();
				}
			}
			return this;
		}

		public DashScopeSdkEmbeddingOptions build() {
			return new DashScopeSdkEmbeddingOptions(this.model, this.textType, this.dimensions, this.httpHeaders);
		}

	}

}
