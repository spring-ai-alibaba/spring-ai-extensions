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

@ConfigurationProperties(DashScopeEmbeddingProperties.CONFIG_PREFIX)
public class DashScopeEmbeddingProperties extends DashScopeParentProperties {

  public static final String CONFIG_PREFIX = "spring.ai.dashscope.embedding";

  public static final String DEFAULT_EMBEDDING_MODEL = "text-embedding-v3";

  /** Enable DashScope embedding client. */
  private boolean enabled = true;

  /**
   * DashScope embedding path.
   */
  private String embeddingsPath = DashScopeApiConstants.TEXT_EMBEDDING_RESTFUL_URL;

  private MetadataMode metadataMode = MetadataMode.EMBED;
  private DashScopeEmbeddingOptions options =
      DashScopeEmbeddingOptions.builder().model(DEFAULT_EMBEDDING_MODEL).build();
	private final Options legacyOptions = new Options();

  public DashScopeEmbeddingOptions toOptions() {
		if (this.options == null) {
			this.options = DashScopeEmbeddingOptions.builder().model(DEFAULT_EMBEDDING_MODEL).build();
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

	private void updateOptions(java.util.function.Consumer<DashScopeEmbeddingOptions.Builder> customizer) {
		DashScopeEmbeddingOptions.Builder builder = DashScopeEmbeddingOptions.builder().from(toOptions());
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

  public @Nullable String getTextType() {
    return toOptions().getTextType();
  }

  public void setTextType(String textType) {
    updateOptions(builder -> builder.textType(textType));
  }

  public @Nullable String getOutputType() {
    return toOptions().getOutputType();
  }

  public void setOutputType(String outputType) {
    updateOptions(builder -> builder.outputType(outputType));
  }

  public MetadataMode getMetadataMode() {
    return this.metadataMode;
  }

  public void setMetadataMode(MetadataMode metadataMode) {
    this.metadataMode = metadataMode;
  }

  public boolean isEnabled() {
    return this.enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public String getEmbeddingsPath() {
    return embeddingsPath;
  }

  public void setEmbeddingsPath(String embeddingsPath) {
    this.embeddingsPath = embeddingsPath;
    updateOptions(builder -> builder.embeddingsPath(embeddingsPath));
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
