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

package com.alibaba.cloud.ai.autoconfigure.dashscope;

import com.alibaba.cloud.ai.dashscope.common.DashScopeApiConstants;
import com.alibaba.cloud.ai.dashscope.embedding.text.DashScopeEmbeddingOptions;
import org.jspecify.annotations.Nullable;
import org.springframework.ai.document.MetadataMode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.DeprecatedConfigurationProperty;

/**
 * DashScope embedding auto-configuration properties.
 *
 * @author nuocheng.lxm
 * @author yuluo
 * @author xuguan
 * @since 2024/8/1 11:14
 */
@ConfigurationProperties(DashScopeEmbeddingProperties.CONFIG_PREFIX)
public class DashScopeEmbeddingProperties extends DashScopeParentProperties {

	public static final String CONFIG_PREFIX = "spring.ai.dashscope.embedding";

	public static final String DEFAULT_EMBEDDING_MODEL = "text-embedding-v3";

	/**
	 * Enable DashScope embedding client.
	 */
	private boolean enabled = true;

	/**
	 * DashScope embedding path.
	 */
	private String embeddingsPath = DashScopeApiConstants.TEXT_EMBEDDING_RESTFUL_URL;

	private MetadataMode metadataMode = MetadataMode.EMBED;

	private @Nullable String model = DEFAULT_EMBEDDING_MODEL;

	private @Nullable String textType;

	private @Nullable Integer dimensions;

	private @Nullable String outputType;

	public boolean isEnabled() {
		return this.enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getEmbeddingsPath() {
		return this.embeddingsPath;
	}

	public void setEmbeddingsPath(String embeddingsPath) {
		this.embeddingsPath = embeddingsPath;
	}

	public MetadataMode getMetadataMode() {
		return this.metadataMode;
	}

	public void setMetadataMode(MetadataMode metadataMode) {
		this.metadataMode = metadataMode;
	}

	public @Nullable String getModel() {
		return this.model;
	}

	public void setModel(@Nullable String model) {
		this.model = model;
	}

	public @Nullable String getTextType() {
		return this.textType;
	}

	public void setTextType(@Nullable String textType) {
		this.textType = textType;
	}

	public @Nullable Integer getDimensions() {
		return this.dimensions;
	}

	public void setDimensions(@Nullable Integer dimensions) {
		this.dimensions = dimensions;
	}

	public @Nullable String getOutputType() {
		return this.outputType;
	}

	public void setOutputType(@Nullable String outputType) {
		this.outputType = outputType;
	}

	public DashScopeEmbeddingOptions toOptions() {
		return DashScopeEmbeddingOptions.builder()
				.model(this.model)
				.textType(this.textType)
				.dimensions(this.dimensions)
				.outputType(this.outputType)
				.embeddingsPath(this.embeddingsPath)
				.build();
	}

	private Options options = new Options();

	@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX)
	@Deprecated(since = "2.0.0", forRemoval = true)
	public Options getOptions() {
		return this.options;
	}

	public void setOptions(Options options) {
		this.options = options;
	}

	public class Options {

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".model")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable String getModel() {
			return DashScopeEmbeddingProperties.this.getModel();
		}

		public void setModel(String model) {
			DashScopeEmbeddingProperties.this.setModel(model);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".dimensions")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Integer getDimensions() {
			return DashScopeEmbeddingProperties.this.getDimensions();
		}

		public void setDimensions(Integer dimensions) {
			DashScopeEmbeddingProperties.this.setDimensions(dimensions);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".text-type")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable String getTextType() {
			return DashScopeEmbeddingProperties.this.getTextType();
		}

		public void setTextType(String textType) {
			DashScopeEmbeddingProperties.this.setTextType(textType);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".output-type")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable String getOutputType() {
			return DashScopeEmbeddingProperties.this.getOutputType();
		}

		public void setOutputType(String outputType) {
			DashScopeEmbeddingProperties.this.setOutputType(outputType);
		}

	}

}
