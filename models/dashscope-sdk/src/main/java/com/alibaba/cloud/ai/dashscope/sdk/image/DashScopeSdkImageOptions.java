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
	private String model;

	@JsonProperty("n")
	private Integer n;

	@JsonProperty("width")
	private Integer width;

	@JsonProperty("height")
	private Integer height;

	@JsonProperty("size")
	private String size;

	@JsonProperty("style")
	private String style;

	@JsonProperty("response_format")
	private String responseFormat;

	@JsonProperty("seed")
	private Integer seed;

	@JsonProperty("negative_prompt")
	private String negativePrompt;

	@JsonProperty("ref_image")
	private String refImage;

	@JsonProperty("poll_interval_ms")
	private Integer pollIntervalMs = 1000;

	@JsonProperty("async")
	private Boolean async = true;

	@JsonIgnore
	private Map<String, String> httpHeaders = new HashMap<>();

	@JsonProperty("extra_body")
	private Map<String, Object> extraBody;

	public static DashScopeSdkImageOptionsBuilder builder() {
		return new DashScopeSdkImageOptionsBuilder();
	}

	public static DashScopeSdkImageOptions fromOptions(DashScopeSdkImageOptions options) {
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
	public Integer getN() {
		return this.n;
	}

	public void setN(Integer n) {
		this.n = n;
	}

	@Override
	public String getModel() {
		return this.model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	@Override
	public Integer getWidth() {
		return this.width;
	}

	public void setWidth(Integer width) {
		this.width = width;
	}

	@Override
	public Integer getHeight() {
		return this.height;
	}

	public void setHeight(Integer height) {
		this.height = height;
	}

	public String getSize() {
		if (this.size != null) {
			return this.size;
		}
		return (this.width != null && this.height != null) ? this.width + "*" + this.height : null;
	}

	public void setSize(String size) {
		this.size = size;
	}

	@Override
	public String getResponseFormat() {
		return this.responseFormat;
	}

	public void setResponseFormat(String responseFormat) {
		this.responseFormat = responseFormat;
	}

	@Override
	public String getStyle() {
		return this.style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public Integer getSeed() {
		return this.seed;
	}

	public void setSeed(Integer seed) {
		this.seed = seed;
	}

	public String getNegativePrompt() {
		return this.negativePrompt;
	}

	public void setNegativePrompt(String negativePrompt) {
		this.negativePrompt = negativePrompt;
	}

	public String getRefImage() {
		return this.refImage;
	}

	public void setRefImage(String refImage) {
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

	public Map<String, Object> getExtraBody() {
		return this.extraBody;
	}

	public void setExtraBody(Map<String, Object> extraBody) {
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

		public DashScopeSdkImageOptionsBuilder model(String model) {
			this.options.model = model;
			return this;
		}

		public DashScopeSdkImageOptionsBuilder n(Integer n) {
			this.options.n = n;
			return this;
		}

		public DashScopeSdkImageOptionsBuilder width(Integer width) {
			this.options.width = width;
			return this;
		}

		public DashScopeSdkImageOptionsBuilder height(Integer height) {
			this.options.height = height;
			return this;
		}

		public DashScopeSdkImageOptionsBuilder size(String size) {
			this.options.size = size;
			return this;
		}

		public DashScopeSdkImageOptionsBuilder style(String style) {
			this.options.style = style;
			return this;
		}

		public DashScopeSdkImageOptionsBuilder responseFormat(String responseFormat) {
			this.options.responseFormat = responseFormat;
			return this;
		}

		public DashScopeSdkImageOptionsBuilder seed(Integer seed) {
			this.options.seed = seed;
			return this;
		}

		public DashScopeSdkImageOptionsBuilder negativePrompt(String negativePrompt) {
			this.options.negativePrompt = negativePrompt;
			return this;
		}

		public DashScopeSdkImageOptionsBuilder refImage(String refImage) {
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

		public DashScopeSdkImageOptionsBuilder extraBody(Map<String, Object> extraBody) {
			this.options.extraBody = extraBody;
			return this;
		}

		public DashScopeSdkImageOptions build() {
			return this.options;
		}

	}

}
