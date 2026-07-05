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

import com.alibaba.cloud.ai.dashscope.sdk.embedding.DashScopeSdkEmbeddingOptions;
import org.springframework.ai.document.MetadataMode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.DeprecatedConfigurationProperty;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * DashScope SDK embedding model properties.
 */
@ConfigurationProperties(DashScopeSdkEmbeddingProperties.CONFIG_PREFIX)
public class DashScopeSdkEmbeddingProperties extends DashScopeSdkParentProperties {

	public static final String CONFIG_PREFIX = "spring.ai.dashscope.sdk.embedding";

	private boolean enabled = true;

	private MetadataMode metadataMode = MetadataMode.EMBED;

	@NestedConfigurationProperty
	private DashScopeSdkEmbeddingOptions options = DashScopeSdkEmbeddingOptions.builder()
		.model("text-embedding-v2")
		.build();

	public DashScopeSdkEmbeddingOptions toOptions() {
		return this.options;
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public MetadataMode getMetadataMode() {
		return this.metadataMode;
	}

	public void setMetadataMode(MetadataMode metadataMode) {
		this.metadataMode = metadataMode;
	}

	@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX)
	@Deprecated(since = "2.0.0", forRemoval = true)
	public DashScopeSdkEmbeddingOptions getOptions() {
		return this.options;
	}

	public void setOptions(DashScopeSdkEmbeddingOptions options) {
		this.options = options;
	}

	public String getModel() {
		return this.options.getModel();
	}

	public void setModel(String model) {
		this.options.setModel(model);
	}

	public String getTextType() {
		return this.options.getTextType();
	}

	public void setTextType(String textType) {
		this.options.setTextType(textType);
	}

	public Integer getDimensions() {
		return this.options.getDimensions();
	}

	public void setDimensions(Integer dimensions) {
		this.options.setDimensions(dimensions);
	}

	public Map<String, String> getHttpHeaders() {
		return this.options.getHttpHeaders();
	}

	public void setHttpHeaders(Map<String, String> httpHeaders) {
		this.options.setHttpHeaders(httpHeaders);
	}

}
