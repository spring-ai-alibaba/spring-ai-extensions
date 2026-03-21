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
	private String model;

	@JsonProperty("text_type")
	private String textType;

	@JsonProperty("dimensions")
	private Integer dimensions;

	@JsonIgnore
	private Map<String, String> httpHeaders = new HashMap<>();

	public static DashScopeSdkEmbeddingOptionsBuilder builder() {
		return new DashScopeSdkEmbeddingOptionsBuilder();
	}

	public static DashScopeSdkEmbeddingOptions fromOptions(DashScopeSdkEmbeddingOptions options) {
		if (options == null) {
			return null;
		}
		DashScopeSdkEmbeddingOptions copy = new DashScopeSdkEmbeddingOptions();
		copy.setModel(options.getModel());
		copy.setTextType(options.getTextType());
		copy.setDimensions(options.getDimensions());
		copy.setHttpHeaders(options.getHttpHeaders() == null ? new HashMap<>() : new HashMap<>(options.getHttpHeaders()));
		return copy;
	}

	@Override
	public String getModel() {
		return this.model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getTextType() {
		return this.textType;
	}

	public void setTextType(String textType) {
		this.textType = textType;
	}

	@Override
	public Integer getDimensions() {
		return this.dimensions;
	}

	public void setDimensions(Integer dimensions) {
		this.dimensions = dimensions;
	}

	public Map<String, String> getHttpHeaders() {
		return this.httpHeaders;
	}

	public void setHttpHeaders(Map<String, String> httpHeaders) {
		this.httpHeaders = httpHeaders;
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

	public static class DashScopeSdkEmbeddingOptionsBuilder {

		private final DashScopeSdkEmbeddingOptions options;

		public DashScopeSdkEmbeddingOptionsBuilder() {
			this.options = new DashScopeSdkEmbeddingOptions();
		}

		public DashScopeSdkEmbeddingOptionsBuilder model(String model) {
			this.options.model = model;
			return this;
		}

		public DashScopeSdkEmbeddingOptionsBuilder textType(String textType) {
			this.options.textType = textType;
			return this;
		}

		public DashScopeSdkEmbeddingOptionsBuilder dimensions(Integer dimensions) {
			this.options.dimensions = dimensions;
			return this;
		}

		public DashScopeSdkEmbeddingOptionsBuilder httpHeaders(Map<String, String> httpHeaders) {
			this.options.httpHeaders = httpHeaders;
			return this;
		}

		public DashScopeSdkEmbeddingOptions build() {
			return this.options;
		}

	}

}
