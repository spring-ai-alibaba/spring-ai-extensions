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

package com.alibaba.cloud.ai.autoconfigure.dashscope.sdk;

import java.util.Map;

import com.alibaba.cloud.ai.dashscope.sdk.image.DashScopeSdkImageOptions;
import org.jspecify.annotations.Nullable;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.DeprecatedConfigurationProperty;

/**
 * DashScope SDK image model properties.
 */
@ConfigurationProperties(DashScopeSdkImageProperties.CONFIG_PREFIX)
public class DashScopeSdkImageProperties extends DashScopeSdkParentProperties {

	public static final String CONFIG_PREFIX = "spring.ai.dashscope.sdk.image";

	private boolean enabled = true;
	private DashScopeSdkImageOptions options = DashScopeSdkImageOptions.builder()
		.model("wanx-v1")
		.n(1)
		.build();
	private final Options legacyOptions = new Options();

	public DashScopeSdkImageOptions toOptions() {
		if (this.options == null) {
			this.options = DashScopeSdkImageOptions.builder().model("wanx-v1").n(1).build();
		}
		return this.options;
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX)
	@Deprecated(since = "2.0.0", forRemoval = true)
	public Options getOptions() {
		return this.legacyOptions;
	}

	public void setOptions(Options options) {
		// Deprecated options are applied by the nested Options setters.
	}

	private void updateOptions(java.util.function.Consumer<DashScopeSdkImageOptions.Builder> customizer) {
		DashScopeSdkImageOptions.Builder builder = DashScopeSdkImageOptions.builder().from(toOptions());
		customizer.accept(builder);
		this.options = builder.build();
	}

	public @Nullable Integer getN() {
		return toOptions().getN();
	}

	public void setN(Integer n) {
		updateOptions(builder -> builder.n(n));
	}

	public @Nullable String getModel() {
		return toOptions().getModel();
	}

	public void setModel(String model) {
		updateOptions(builder -> builder.model(model));
	}

	public @Nullable Integer getWidth() {
		return toOptions().getWidth();
	}

	public void setWidth(Integer width) {
		updateOptions(builder -> builder.width(width));
	}

	public @Nullable Integer getHeight() {
		return toOptions().getHeight();
	}

	public void setHeight(Integer height) {
		updateOptions(builder -> builder.height(height));
	}

	public @Nullable String getSize() {
		return toOptions().getSize();
	}

	public void setSize(String size) {
		updateOptions(builder -> builder.size(size));
	}

	public @Nullable String getResponseFormat() {
		return toOptions().getResponseFormat();
	}

	public void setResponseFormat(String responseFormat) {
		updateOptions(builder -> builder.responseFormat(responseFormat));
	}

	public @Nullable String getStyle() {
		return toOptions().getStyle();
	}

	public void setStyle(String style) {
		updateOptions(builder -> builder.style(style));
	}

	public @Nullable Integer getSeed() {
		return toOptions().getSeed();
	}

	public void setSeed(Integer seed) {
		updateOptions(builder -> builder.seed(seed));
	}

	public @Nullable String getNegativePrompt() {
		return toOptions().getNegativePrompt();
	}

	public void setNegativePrompt(String negativePrompt) {
		updateOptions(builder -> builder.negativePrompt(negativePrompt));
	}

	public @Nullable String getRefImage() {
		return toOptions().getRefImage();
	}

	public void setRefImage(String refImage) {
		updateOptions(builder -> builder.refImage(refImage));
	}

	public @Nullable Integer getPollIntervalMs() {
		return toOptions().getPollIntervalMs();
	}

	public void setPollIntervalMs(Integer pollIntervalMs) {
		updateOptions(builder -> builder.pollIntervalMs(pollIntervalMs));
	}

	public @Nullable Boolean getAsync() {
		return toOptions().getAsync();
	}

	public void setAsync(Boolean async) {
		updateOptions(builder -> builder.async(async));
	}

	public @Nullable Map<String, String> getHttpHeaders() {
		return toOptions().getHttpHeaders();
	}

	public void setHttpHeaders(Map<String, String> httpHeaders) {
		updateOptions(builder -> builder.httpHeaders(httpHeaders));
	}

	public @Nullable Map<String, Object> getExtraBody() {
		return toOptions().getExtraBody();
	}

	public void setExtraBody(Map<String, Object> extraBody) {
		updateOptions(builder -> builder.extraBody(extraBody));
	}
	public class Options {

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".n")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Integer getN() {
			return DashScopeSdkImageProperties.this.getN();
		}

		public void setN(Integer n) {
			DashScopeSdkImageProperties.this.setN(n);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".model")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable String getModel() {
			return DashScopeSdkImageProperties.this.getModel();
		}

		public void setModel(String model) {
			DashScopeSdkImageProperties.this.setModel(model);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".width")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Integer getWidth() {
			return DashScopeSdkImageProperties.this.getWidth();
		}

		public void setWidth(Integer width) {
			DashScopeSdkImageProperties.this.setWidth(width);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".height")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Integer getHeight() {
			return DashScopeSdkImageProperties.this.getHeight();
		}

		public void setHeight(Integer height) {
			DashScopeSdkImageProperties.this.setHeight(height);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".size")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable String getSize() {
			return DashScopeSdkImageProperties.this.getSize();
		}

		public void setSize(String size) {
			DashScopeSdkImageProperties.this.setSize(size);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".response-format")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable String getResponseFormat() {
			return DashScopeSdkImageProperties.this.getResponseFormat();
		}

		public void setResponseFormat(String responseFormat) {
			DashScopeSdkImageProperties.this.setResponseFormat(responseFormat);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".style")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable String getStyle() {
			return DashScopeSdkImageProperties.this.getStyle();
		}

		public void setStyle(String style) {
			DashScopeSdkImageProperties.this.setStyle(style);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".seed")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Integer getSeed() {
			return DashScopeSdkImageProperties.this.getSeed();
		}

		public void setSeed(Integer seed) {
			DashScopeSdkImageProperties.this.setSeed(seed);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".negative-prompt")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable String getNegativePrompt() {
			return DashScopeSdkImageProperties.this.getNegativePrompt();
		}

		public void setNegativePrompt(String negativePrompt) {
			DashScopeSdkImageProperties.this.setNegativePrompt(negativePrompt);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".ref-image")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable String getRefImage() {
			return DashScopeSdkImageProperties.this.getRefImage();
		}

		public void setRefImage(String refImage) {
			DashScopeSdkImageProperties.this.setRefImage(refImage);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".poll-interval-ms")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Integer getPollIntervalMs() {
			return DashScopeSdkImageProperties.this.getPollIntervalMs();
		}

		public void setPollIntervalMs(Integer pollIntervalMs) {
			DashScopeSdkImageProperties.this.setPollIntervalMs(pollIntervalMs);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".async")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Boolean getAsync() {
			return DashScopeSdkImageProperties.this.getAsync();
		}

		public void setAsync(Boolean async) {
			DashScopeSdkImageProperties.this.setAsync(async);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".http-headers")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Map<String, String> getHttpHeaders() {
			return DashScopeSdkImageProperties.this.getHttpHeaders();
		}

		public void setHttpHeaders(Map<String, String> httpHeaders) {
			DashScopeSdkImageProperties.this.setHttpHeaders(httpHeaders);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".extra-body")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Map<String, Object> getExtraBody() {
			return DashScopeSdkImageProperties.this.getExtraBody();
		}

		public void setExtraBody(Map<String, Object> extraBody) {
			DashScopeSdkImageProperties.this.setExtraBody(extraBody);
		}

	}


}
