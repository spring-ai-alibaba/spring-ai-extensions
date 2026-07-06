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
import com.alibaba.cloud.ai.dashscope.rerank.DashScopeRerankOptions;
import org.jspecify.annotations.Nullable;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.DeprecatedConfigurationProperty;

/**
 * Title DashScope rerank properties.<br>
 * Description DashScope rerank properties.<br>
 *
 * @author yuanci.ytb
 * @since 1.0.0-M2
 */
@ConfigurationProperties(DashScopeRerankProperties.CONFIG_PREFIX)
public class DashScopeRerankProperties extends DashScopeParentProperties {

    /**
     * Spring AI Alibaba configuration prefix.
     */
    public static final String CONFIG_PREFIX = "spring.ai.dashscope.rerank";

    /**
     * Default DashScope rerank model.
     */
    public static final String DEFAULT_RERANK_MODEL = "gte-rerank";

    /**
     * Default rerank path.
     */
    private String rerankPath = DashScopeApiConstants.TEXT_RERANK_RESTFUL_URL;
    private DashScopeRerankOptions options = DashScopeRerankOptions.builder()
            .model(DEFAULT_RERANK_MODEL)
            .topN(5)
            .returnDocuments(false)
            .build();
	private final Options legacyOptions = new Options();

    public DashScopeRerankOptions toOptions() {
		if (this.options == null) {
			this.options = DashScopeRerankOptions.builder().build();
		}
		return this.options;
	}

    public @Nullable Integer getTopN() {
        return toOptions().getTopN();
    }

    public void setTopN(Integer topN) {
        updateOptions(builder -> builder.topN(topN));
    }

    public @Nullable Boolean getReturnDocuments() {
        return toOptions().getReturnDocuments();
    }

    public void setReturnDocuments(Boolean returnDocuments) {
        updateOptions(builder -> builder.returnDocuments(returnDocuments));
    }

    public @Nullable String getModel() {
        return toOptions().getModel();
    }

    public void setModel(String model) {
        updateOptions(builder -> builder.model(model));
    }

    public String getRerankPath() {
        return rerankPath;
    }

    public void setRerankPath(String rerankPath) {
        this.rerankPath = rerankPath;
    }

    @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX)
    @Deprecated(since = "2.0.0", forRemoval = true)
    public Options getOptions() {
		return this.legacyOptions;
	}

	public void setOptions(Options options) {
		// Deprecated options are applied by the nested Options setters.
	}

	private void updateOptions(java.util.function.Consumer<DashScopeRerankOptions.Builder> customizer) {
		DashScopeRerankOptions.Builder builder = DashScopeRerankOptions.builder().from(toOptions());
		customizer.accept(builder);
		this.options = builder.build();
	}
	public class Options {

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".top-n")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Integer getTopN() {
			return DashScopeRerankProperties.this.getTopN();
		}

		public void setTopN(Integer topN) {
			DashScopeRerankProperties.this.setTopN(topN);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".return-documents")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Boolean getReturnDocuments() {
			return DashScopeRerankProperties.this.getReturnDocuments();
		}

		public void setReturnDocuments(Boolean returnDocuments) {
			DashScopeRerankProperties.this.setReturnDocuments(returnDocuments);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".model")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable String getModel() {
			return DashScopeRerankProperties.this.getModel();
		}

		public void setModel(String model) {
			DashScopeRerankProperties.this.setModel(model);
		}

	}


}
