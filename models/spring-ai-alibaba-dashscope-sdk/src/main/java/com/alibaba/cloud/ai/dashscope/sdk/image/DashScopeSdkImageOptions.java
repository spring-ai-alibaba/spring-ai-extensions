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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Options for DashScope SDK image model.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DashScopeSdkImageOptions implements ImageOptions {

	@JsonProperty("model")
	private final @Nullable String model;

	@JsonProperty("n")
	private final @Nullable Integer n;

	@JsonProperty("width")
	private final @Nullable Integer width;

	@JsonProperty("height")
	private final @Nullable Integer height;

	@JsonProperty("size")
	private final @Nullable String size;

	@JsonProperty("style")
	private final @Nullable String style;

	@JsonProperty("response_format")
	private final @Nullable String responseFormat;

	@JsonProperty("seed")
	private final @Nullable Integer seed;

	@JsonProperty("negative_prompt")
	private final @Nullable String negativePrompt;

	@JsonProperty("ref_image")
	private final @Nullable String refImage;

	@JsonProperty("poll_interval_ms")
	private final Integer pollIntervalMs;

	@JsonProperty("async")
	private final Boolean async;

	@JsonIgnore
	private final Map<String, String> httpHeaders;

	@JsonProperty("extra_body")
	private final @Nullable Map<String, Object> extraBody;

	protected DashScopeSdkImageOptions(@Nullable String model, @Nullable Integer n, @Nullable Integer width,
			@Nullable Integer height, @Nullable String size, @Nullable String style,
			@Nullable String responseFormat, @Nullable Integer seed, @Nullable String negativePrompt,
			@Nullable String refImage, @Nullable Integer pollIntervalMs, @Nullable Boolean async,
			@Nullable Map<String, String> httpHeaders, @Nullable Map<String, Object> extraBody) {
		this.model = model;
		this.n = n;
		this.width = width;
		this.height = height;
		this.size = size;
		this.style = style;
		this.responseFormat = responseFormat;
		this.seed = seed;
		this.negativePrompt = negativePrompt;
		this.refImage = refImage;
		this.pollIntervalMs = pollIntervalMs != null ? pollIntervalMs : 1000;
		this.async = async != null ? async : true;
		this.httpHeaders = httpHeaders != null ? new HashMap<>(httpHeaders) : new HashMap<>();
		this.extraBody = extraBody != null ? new HashMap<>(extraBody) : null;
	}

	public static Builder builder() {
		return new Builder();
	}

	public static DashScopeSdkImageOptions fromOptions(DashScopeSdkImageOptions options) {
		return options.mutate().build();
	}

	@Override
	public @Nullable Integer getN() {
		return this.n;
	}

	@Override
	public @Nullable String getModel() {
		return this.model;
	}

	@Override
	public @Nullable Integer getWidth() {
		return this.width;
	}

	@Override
	public @Nullable Integer getHeight() {
		return this.height;
	}

	public @Nullable String getSize() {
		if (this.size != null) {
			return this.size;
		}
		return (this.width != null && this.height != null) ? this.width + "*" + this.height : null;
	}

	@Override
	public @Nullable String getResponseFormat() {
		return this.responseFormat;
	}

	@Override
	public @Nullable String getStyle() {
		return this.style;
	}

	public @Nullable Integer getSeed() {
		return this.seed;
	}

	public @Nullable String getNegativePrompt() {
		return this.negativePrompt;
	}

	public @Nullable String getRefImage() {
		return this.refImage;
	}

	public Integer getPollIntervalMs() {
		return this.pollIntervalMs;
	}

	public Boolean getAsync() {
		return this.async;
	}

	public Map<String, String> getHttpHeaders() {
		return Collections.unmodifiableMap(this.httpHeaders);
	}

	public @Nullable Map<String, Object> getExtraBody() {
		return this.extraBody;
	}

	public Builder mutate() {
		return builder()
			.model(this.model)
			.n(this.n)
			.width(this.width)
			.height(this.height)
			.size(this.size)
			.style(this.style)
			.responseFormat(this.responseFormat)
			.seed(this.seed)
			.negativePrompt(this.negativePrompt)
			.refImage(this.refImage)
			.pollIntervalMs(this.pollIntervalMs)
			.async(this.async)
			.httpHeaders(new HashMap<>(this.httpHeaders))
			.extraBody(this.extraBody);
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

    // @formatter:off
	@Override
	public int hashCode() {
		return Objects.hash(this.model, this.n, this.width, this.height, this.size, this.style, this.responseFormat,
				this.seed, this.negativePrompt, this.refImage, this.pollIntervalMs, this.async, this.httpHeaders,
				this.extraBody);
	}
    // @formatter:on

	@Override
	public String toString() {
		return "DashScopeSdkImageOptions{" + "model='" + this.model + '\'' + ", n=" + this.n + ", width=" + this.width
				+ ", height=" + this.height + ", size='" + this.size + '\'' + ", style='" + this.style + '\''
				+ ", responseFormat='" + this.responseFormat + '\'' + ", seed=" + this.seed
				+ ", negativePrompt='" + this.negativePrompt + '\'' + ", refImage='" + this.refImage + '\''
				+ ", pollIntervalMs=" + this.pollIntervalMs + ", async=" + this.async + ", httpHeaders="
				+ this.httpHeaders + ", extraBody=" + this.extraBody + '}';
	}

	public static class Builder {

		protected @Nullable String model;

		protected @Nullable Integer n;

		protected @Nullable Integer width;

		protected @Nullable Integer height;

		protected @Nullable String size;

		protected @Nullable String style;

		protected @Nullable String responseFormat;

		protected @Nullable Integer seed;

		protected @Nullable String negativePrompt;

		protected @Nullable String refImage;

		protected @Nullable Integer pollIntervalMs;

		protected @Nullable Boolean async;

		protected Map<String, String> httpHeaders = new HashMap<>();

		protected @Nullable Map<String, Object> extraBody;

		public Builder() {
		}

		public Builder model(@Nullable String model) {
			this.model = model;
			return this;
		}

		public Builder n(@Nullable Integer n) {
			this.n = n;
			return this;
		}

		public Builder width(@Nullable Integer width) {
			this.width = width;
			return this;
		}

		public Builder height(@Nullable Integer height) {
			this.height = height;
			return this;
		}

		public Builder size(@Nullable String size) {
			this.size = size;
			return this;
		}

		public Builder style(@Nullable String style) {
			this.style = style;
			return this;
		}

		public Builder responseFormat(@Nullable String responseFormat) {
			this.responseFormat = responseFormat;
			return this;
		}

		public Builder seed(@Nullable Integer seed) {
			this.seed = seed;
			return this;
		}

		public Builder negativePrompt(@Nullable String negativePrompt) {
			this.negativePrompt = negativePrompt;
			return this;
		}

		public Builder refImage(@Nullable String refImage) {
			this.refImage = refImage;
			return this;
		}

		public Builder pollIntervalMs(@Nullable Integer pollIntervalMs) {
			this.pollIntervalMs = pollIntervalMs;
			return this;
		}

		public Builder async(@Nullable Boolean async) {
			this.async = async;
			return this;
		}

		public Builder httpHeaders(@Nullable Map<String, String> httpHeaders) {
			this.httpHeaders = httpHeaders != null ? new HashMap<>(httpHeaders) : new HashMap<>();
			return this;
		}

		public Builder extraBody(@Nullable Map<String, Object> extraBody) {
			this.extraBody = extraBody;
			return this;
		}

		public Builder from(DashScopeSdkImageOptions fromOptions) {
			this.model = fromOptions.getModel();
			this.n = fromOptions.getN();
			this.width = fromOptions.getWidth();
			this.height = fromOptions.getHeight();
			this.size = fromOptions.getSize();
			this.style = fromOptions.getStyle();
			this.responseFormat = fromOptions.getResponseFormat();
			this.seed = fromOptions.getSeed();
			this.negativePrompt = fromOptions.getNegativePrompt();
			this.refImage = fromOptions.getRefImage();
			this.pollIntervalMs = fromOptions.getPollIntervalMs();
			this.async = fromOptions.getAsync();
			this.httpHeaders = new HashMap<>(fromOptions.getHttpHeaders());
			this.extraBody = fromOptions.getExtraBody();
			return this;
		}

		public Builder merge(@Nullable ImageOptions from) {
			if (from == null) {
				return this;
			}
			if (from.getModel() != null) {
				this.model = from.getModel();
			}
			if (from.getN() != null) {
				this.n = from.getN();
			}
			if (from.getWidth() != null) {
				this.width = from.getWidth();
			}
			if (from.getHeight() != null) {
				this.height = from.getHeight();
			}
			if (from.getStyle() != null) {
				this.style = from.getStyle();
			}
			if (from.getResponseFormat() != null) {
				this.responseFormat = from.getResponseFormat();
			}
			if (from instanceof DashScopeSdkImageOptions castFrom) {
				if (castFrom.getSize() != null) {
					this.size = castFrom.getSize();
				}
				if (castFrom.getSeed() != null) {
					this.seed = castFrom.getSeed();
				}
				if (castFrom.getNegativePrompt() != null) {
					this.negativePrompt = castFrom.getNegativePrompt();
				}
				if (castFrom.getRefImage() != null) {
					this.refImage = castFrom.getRefImage();
				}
				if (castFrom.getPollIntervalMs() != null) {
					this.pollIntervalMs = castFrom.getPollIntervalMs();
				}
				if (castFrom.getAsync() != null) {
					this.async = castFrom.getAsync();
				}
				if (castFrom.getHttpHeaders() != null && !castFrom.getHttpHeaders().isEmpty()) {
					this.httpHeaders = new HashMap<>(castFrom.getHttpHeaders());
				}
				if (castFrom.getExtraBody() != null) {
					this.extraBody = castFrom.getExtraBody();
				}
			}
			return this;
		}

        // @formatter:off
		public DashScopeSdkImageOptions build() {
			return new DashScopeSdkImageOptions(this.model, this.n, this.width, this.height, this.size, this.style,
					this.responseFormat, this.seed, this.negativePrompt, this.refImage, this.pollIntervalMs,
					this.async, this.httpHeaders, this.extraBody);
		}
        // @formatter:on

	}

}
