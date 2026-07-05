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
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.DeprecatedConfigurationProperty;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * DashScope SDK image model properties.
 */
@ConfigurationProperties(DashScopeSdkImageProperties.CONFIG_PREFIX)
public class DashScopeSdkImageProperties extends DashScopeSdkParentProperties {

	public static final String CONFIG_PREFIX = "spring.ai.dashscope.sdk.image";

	private boolean enabled = true;

	@NestedConfigurationProperty
	private DashScopeSdkImageOptions options = DashScopeSdkImageOptions.builder()
		.model("wanx-v1")
		.n(1)
		.build();

	public DashScopeSdkImageOptions toOptions() {
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
	public DashScopeSdkImageOptions getOptions() {
		return this.options;
	}

	public void setOptions(DashScopeSdkImageOptions options) {
		this.options = options;
	}

	public Integer getN() {
		return this.options.getN();
	}

	public void setN(Integer n) {
		this.options.setN(n);
	}

	public String getModel() {
		return this.options.getModel();
	}

	public void setModel(String model) {
		this.options.setModel(model);
	}

	public Integer getWidth() {
		return this.options.getWidth();
	}

	public void setWidth(Integer width) {
		this.options.setWidth(width);
	}

	public Integer getHeight() {
		return this.options.getHeight();
	}

	public void setHeight(Integer height) {
		this.options.setHeight(height);
	}

	public String getSize() {
		return this.options.getSize();
	}

	public void setSize(String size) {
		this.options.setSize(size);
	}

	public String getResponseFormat() {
		return this.options.getResponseFormat();
	}

	public void setResponseFormat(String responseFormat) {
		this.options.setResponseFormat(responseFormat);
	}

	public String getStyle() {
		return this.options.getStyle();
	}

	public void setStyle(String style) {
		this.options.setStyle(style);
	}

	public Integer getSeed() {
		return this.options.getSeed();
	}

	public void setSeed(Integer seed) {
		this.options.setSeed(seed);
	}

	public String getNegativePrompt() {
		return this.options.getNegativePrompt();
	}

	public void setNegativePrompt(String negativePrompt) {
		this.options.setNegativePrompt(negativePrompt);
	}

	public String getRefImage() {
		return this.options.getRefImage();
	}

	public void setRefImage(String refImage) {
		this.options.setRefImage(refImage);
	}

	public Integer getPollIntervalMs() {
		return this.options.getPollIntervalMs();
	}

	public void setPollIntervalMs(Integer pollIntervalMs) {
		this.options.setPollIntervalMs(pollIntervalMs);
	}

	public Boolean getAsync() {
		return this.options.getAsync();
	}

	public void setAsync(Boolean async) {
		this.options.setAsync(async);
	}

	public Map<String, String> getHttpHeaders() {
		return this.options.getHttpHeaders();
	}

	public void setHttpHeaders(Map<String, String> httpHeaders) {
		this.options.setHttpHeaders(httpHeaders);
	}

	public Map<String, Object> getExtraBody() {
		return this.options.getExtraBody();
	}

	public void setExtraBody(Map<String, Object> extraBody) {
		this.options.setExtraBody(extraBody);
	}

}
