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
import org.jspecify.annotations.Nullable;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.DeprecatedConfigurationProperty;

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
	private DashScopeMultimodalEmbeddingOptions options = DashScopeMultimodalEmbeddingOptions.builder()
			.model(DEFAULT_MULTIMODAL_EMBEDDING_MODEL)
			.build();
	private final Options legacyOptions = new Options();

	public DashScopeMultimodalEmbeddingOptions toOptions() {
		if (this.options == null) {
			this.options = DashScopeMultimodalEmbeddingOptions.builder().model(DEFAULT_MULTIMODAL_EMBEDDING_MODEL).build();
		}
		return this.options;
	}

	@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX)
	@Deprecated(since = "2.0.0", forRemoval = true)
	public Options getOptions() {
		return this.legacyOptions;
	}

	public void setOptions(Options options) {
		// Deprecated options are applied by the nested Options setters.
	}

	private void updateOptions(java.util.function.Consumer<DashScopeMultimodalEmbeddingOptions.Builder> customizer) {
		DashScopeMultimodalEmbeddingOptions.Builder builder = DashScopeMultimodalEmbeddingOptions.builder().from(toOptions());
		customizer.accept(builder);
		this.options = builder.build();
	}

	public @Nullable String getModel() {
		return toOptions().getModel();
	}

	public void setModel(String model) {
		updateOptions(builder -> builder.model(model));
	}

	public @Nullable Integer getDimensions() {
		return toOptions().getDimensions();
	}

	public void setDimensions(Integer dimensions) {
		updateOptions(builder -> builder.dimensions(dimensions));
	}

	public @Nullable String getOutputType() {
		return toOptions().getOutputType();
	}

	public void setOutputType(String outputType) {
		updateOptions(builder -> builder.outputType(outputType));
	}

	public @Nullable Float getFps() {
		return toOptions().getFps();
	}

	public void setFps(Float fps) {
		updateOptions(builder -> builder.fps(fps));
	}

	public @Nullable String getInstruct() {
		return toOptions().getInstruct();
	}

	public void setInstruct(String instruct) {
		updateOptions(builder -> builder.instruct(instruct));
	}

	public String getMultimodalPath() {
		return multimodalPath;
	}

	public void setMultimodalPath(String multimodalPath) {
		this.multimodalPath = multimodalPath;
	}
	public class Options {

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".model")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable String getModel() {
			return DashScopeMultimodalEmbeddingProperties.this.getModel();
		}

		public void setModel(String model) {
			DashScopeMultimodalEmbeddingProperties.this.setModel(model);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".dimensions")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Integer getDimensions() {
			return DashScopeMultimodalEmbeddingProperties.this.getDimensions();
		}

		public void setDimensions(Integer dimensions) {
			DashScopeMultimodalEmbeddingProperties.this.setDimensions(dimensions);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".output-type")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable String getOutputType() {
			return DashScopeMultimodalEmbeddingProperties.this.getOutputType();
		}

		public void setOutputType(String outputType) {
			DashScopeMultimodalEmbeddingProperties.this.setOutputType(outputType);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".fps")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Float getFps() {
			return DashScopeMultimodalEmbeddingProperties.this.getFps();
		}

		public void setFps(Float fps) {
			DashScopeMultimodalEmbeddingProperties.this.setFps(fps);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".instruct")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable String getInstruct() {
			return DashScopeMultimodalEmbeddingProperties.this.getInstruct();
		}

		public void setInstruct(String instruct) {
			DashScopeMultimodalEmbeddingProperties.this.setInstruct(instruct);
		}

	}


}
