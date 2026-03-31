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

package com.alibaba.cloud.ai.dashscope.sdk.image;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jspecify.annotations.Nullable;
import org.springframework.ai.image.ImageOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Options for DashScope SDK image model.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DashScopeSdkImageOptions implements ImageOptions {

	@JsonProperty("model")
	private @Nullable String model;

	@JsonProperty("n")
	private @Nullable Integer n;

	@JsonProperty("width")
	private @Nullable Integer width;

	@JsonProperty("height")
	private @Nullable Integer height;

	@JsonProperty("size")
	private @Nullable String size;

	@JsonProperty("style")
	private @Nullable String style;

	@JsonProperty("response_format")
	private @Nullable String responseFormat;

	@JsonProperty("seed")
	private @Nullable Integer seed;

	@JsonProperty("negative_prompt")
	private @Nullable String negativePrompt;

	@JsonProperty("ref_image")
	private @Nullable String refImage;

	@JsonProperty("poll_interval_ms")
	private Integer pollIntervalMs = 1000;

	@JsonProperty("async")
	private Boolean async = true;

	@JsonIgnore
	private Map<String, String> httpHeaders = new HashMap<>();

	@JsonProperty("extra_body")
	private @Nullable Map<String, Object> extraBody;

	public static DashScopeSdkImageOptionsBuilder builder() {
		return new DashScopeSdkImageOptionsBuilder();
	}

	public static @Nullable DashScopeSdkImageOptions fromOptions(@Nullable DashScopeSdkImageOptions options) {
		if (options == null) {
			return null;
		}
		DashScopeSdkImageOptions copy = new DashScopeSdkImageOptions();
		copy.setModel(options.getModel());
		copy.setN(options.getN());
		copy.setWidth(options.getWidth());
		copy.setHeight(options.getHeight());
		copy.setSize(options.getSize());
		copy.setStyle(options.getStyle());
		copy.setResponseFormat(options.getResponseFormat());
		copy.setSeed(options.getSeed());
		copy.setNegativePrompt(options.getNegativePrompt());
		copy.setRefImage(options.getRefImage());
		copy.setPollIntervalMs(options.getPollIntervalMs());
		copy.setAsync(options.getAsync());
		copy.setHttpHeaders(options.getHttpHeaders() == null ? new HashMap<>() : new HashMap<>(options.getHttpHeaders()));
		copy.setExtraBody(options.getExtraBody() == null ? null : new HashMap<>(options.getExtraBody()));
		return copy;
	}

	@Override
	public @Nullable Integer getN() {
		return this.n;
	}

	public void setN(@Nullable Integer n) {
		this.n = n;
	}

	@Override
	public @Nullable String getModel() {
		return this.model;
	}

	public void setModel(@Nullable String model) {
		this.model = model;
	}

	@Override
	public @Nullable Integer getWidth() {
		return this.width;
	}

	public void setWidth(@Nullable Integer width) {
		this.width = width;
	}

	@Override
	public @Nullable Integer getHeight() {
		return this.height;
	}

	public void setHeight(@Nullable Integer height) {
		this.height = height;
	}

	public @Nullable String getSize() {
		if (this.size != null) {
			return this.size;
		}
		return (this.width != null && this.height != null) ? this.width + "*" + this.height : null;
	}

	public void setSize(@Nullable String size) {
		this.size = size;
	}

	@Override
	public @Nullable String getResponseFormat() {
		return this.responseFormat;
	}

	public void setResponseFormat(@Nullable String responseFormat) {
		this.responseFormat = responseFormat;
	}

	@Override
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

	public Integer getPollIntervalMs() {
		return this.pollIntervalMs;
	}

	public void setPollIntervalMs(Integer pollIntervalMs) {
		this.pollIntervalMs = pollIntervalMs;
	}

	public Boolean getAsync() {
		return this.async;
	}

	public void setAsync(Boolean async) {
		this.async = async;
	}

	public Map<String, String> getHttpHeaders() {
		return this.httpHeaders;
	}

	public void setHttpHeaders(Map<String, String> httpHeaders) {
		this.httpHeaders = httpHeaders;
	}

	public @Nullable Map<String, Object> getExtraBody() {
		return this.extraBody;
	}

	public void setExtraBody(@Nullable Map<String, Object> extraBody) {
		this.extraBody = extraBody;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		DashScopeSdkImageOptions that = (DashScopeSdkImageOptions) o;
		return Objects.equals(this.model, that.model) && Objects.equals(this.n, that.n)
				&& Objects.equals(this.width, that.width) && Objects.equals(this.height, that.height)
				&& Objects.equals(this.size, that.size) && Objects.equals(this.style, that.style)
				&& Objects.equals(this.responseFormat, that.responseFormat) && Objects.equals(this.seed, that.seed)
				&& Objects.equals(this.negativePrompt, that.negativePrompt)
				&& Objects.equals(this.refImage, that.refImage)
				&& Objects.equals(this.pollIntervalMs, that.pollIntervalMs) && Objects.equals(this.async, that.async)
				&& Objects.equals(this.httpHeaders, that.httpHeaders) && Objects.equals(this.extraBody, that.extraBody);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.model, this.n, this.width, this.height, this.size, this.style, this.responseFormat,
				this.seed, this.negativePrompt, this.refImage, this.pollIntervalMs, this.async, this.httpHeaders,
				this.extraBody);
	}

	public static class DashScopeSdkImageOptionsBuilder {

		private final DashScopeSdkImageOptions options;

		public DashScopeSdkImageOptionsBuilder() {
			this.options = new DashScopeSdkImageOptions();
		}

		public DashScopeSdkImageOptionsBuilder model(@Nullable String model) {
			this.options.model = model;
			return this;
		}

		public DashScopeSdkImageOptionsBuilder n(@Nullable Integer n) {
			this.options.n = n;
			return this;
		}

		public DashScopeSdkImageOptionsBuilder width(@Nullable Integer width) {
			this.options.width = width;
			return this;
		}

		public DashScopeSdkImageOptionsBuilder height(@Nullable Integer height) {
			this.options.height = height;
			return this;
		}

		public DashScopeSdkImageOptionsBuilder size(@Nullable String size) {
			this.options.size = size;
			return this;
		}

		public DashScopeSdkImageOptionsBuilder style(@Nullable String style) {
			this.options.style = style;
			return this;
		}

		public DashScopeSdkImageOptionsBuilder responseFormat(@Nullable String responseFormat) {
			this.options.responseFormat = responseFormat;
			return this;
		}

		public DashScopeSdkImageOptionsBuilder seed(@Nullable Integer seed) {
			this.options.seed = seed;
			return this;
		}

		public DashScopeSdkImageOptionsBuilder negativePrompt(@Nullable String negativePrompt) {
			this.options.negativePrompt = negativePrompt;
			return this;
		}

		public DashScopeSdkImageOptionsBuilder refImage(@Nullable String refImage) {
			this.options.refImage = refImage;
			return this;
		}

		public DashScopeSdkImageOptionsBuilder pollIntervalMs(Integer pollIntervalMs) {
			this.options.pollIntervalMs = pollIntervalMs;
			return this;
		}

		public DashScopeSdkImageOptionsBuilder async(Boolean async) {
			this.options.async = async;
			return this;
		}

		public DashScopeSdkImageOptionsBuilder httpHeaders(Map<String, String> httpHeaders) {
			this.options.httpHeaders = httpHeaders;
			return this;
		}

		public DashScopeSdkImageOptionsBuilder extraBody(@Nullable Map<String, Object> extraBody) {
			this.options.extraBody = extraBody;
			return this;
		}

		public DashScopeSdkImageOptions build() {
			return this.options;
		}

	}

}
