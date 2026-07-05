/*
 * Copyright 2026-2027 the original author or authors.
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
package com.alibaba.cloud.ai.autoconfigure.dashscope;

import com.alibaba.cloud.ai.dashscope.common.DashScopeApiConstants;
import com.alibaba.cloud.ai.dashscope.embedding.multimodal.DashScopeMultimodalEmbeddingOptions;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.DeprecatedConfigurationProperty;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * DashScope multimodal embedding properties.
 *
 * @author buvidk
 */
@ConfigurationProperties(DashScopeMultimodalEmbeddingProperties.CONFIG_PREFIX)
public class DashScopeMultimodalEmbeddingProperties extends DashScopeParentProperties {

	public static final String CONFIG_PREFIX = "spring.ai.dashscope.embedding.multimodal";

	public static final String DEFAULT_MULTIMODAL_EMBEDDING_MODEL = "tongyi-embedding-vision-plus";

	private String multimodalPath = DashScopeApiConstants.MULTIMODAL_EMBEDDING_RESTFUL_URL;

	@NestedConfigurationProperty
	private DashScopeMultimodalEmbeddingOptions options = DashScopeMultimodalEmbeddingOptions.builder()
			.model(DEFAULT_MULTIMODAL_EMBEDDING_MODEL)
			.build();

	public DashScopeMultimodalEmbeddingOptions toOptions() {
		return this.options;
	}

	@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX)
	@Deprecated(since = "2.0.0", forRemoval = true)
	public DashScopeMultimodalEmbeddingOptions getOptions() {
		return this.options;
	}

	public void setOptions(DashScopeMultimodalEmbeddingOptions options) {
		this.options = options;
	}

	public String getModel() {
		return this.options.getModel();
	}

	public void setModel(String model) {
		this.options.setModel(model);
	}

	public Integer getDimensions() {
		return this.options.getDimensions();
	}

	public void setDimensions(Integer dimensions) {
		this.options.setDimensions(dimensions);
	}

	public String getOutputType() {
		return this.options.getOutputType();
	}

	public void setOutputType(String outputType) {
		this.options.setOutputType(outputType);
	}

	public Float getFps() {
		return this.options.getFps();
	}

	public void setFps(Float fps) {
		this.options.setFps(fps);
	}

	public String getInstruct() {
		return this.options.getInstruct();
	}

	public void setInstruct(String instruct) {
		this.options.setInstruct(instruct);
	}

	public String getMultimodalPath() {
		return multimodalPath;
	}

	public void setMultimodalPath(String multimodalPath) {
		this.multimodalPath = multimodalPath;
	}
}
