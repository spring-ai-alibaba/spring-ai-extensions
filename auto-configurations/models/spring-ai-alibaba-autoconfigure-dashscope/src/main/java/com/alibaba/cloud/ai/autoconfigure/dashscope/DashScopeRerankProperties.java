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
 * DashScope rerank auto-configuration properties.
 *
 * @author yuanci.ytb
 * @author xuguan
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
    private @Nullable String instruct;

    public @Nullable String getInstruct() {
        return this.instruct;
    }

    public void setInstruct(@Nullable String instruct) {
        this.instruct = instruct;
    }

    public static final String DEFAULT_RERANK_MODEL = "qwen3-rerank";

    /**
     * Default rerank path.
     */
    private String rerankPath = DashScopeApiConstants.TEXT_RERANK_RESTFUL_URL;

    private @Nullable String model = DEFAULT_RERANK_MODEL;

    private @Nullable Integer topN = 3;

    private @Nullable Boolean returnDocuments = false;

    public String getRerankPath() {
        return this.rerankPath;
    }

    public void setRerankPath(String rerankPath) {
        this.rerankPath = rerankPath;
    }

    public @Nullable String getModel() {
        return this.model;
    }

    public void setModel(@Nullable String model) {
        this.model = model;
    }

    public @Nullable Integer getTopN() {
        return this.topN;
    }

    public void setTopN(@Nullable Integer topN) {
        this.topN = topN;
    }

    public @Nullable Boolean getReturnDocuments() {
        return this.returnDocuments;
    }

    public void setReturnDocuments(@Nullable Boolean returnDocuments) {
        this.returnDocuments = returnDocuments;
    }

    public DashScopeRerankOptions toOptions() {
        return DashScopeRerankOptions.builder()
                .model(this.model)
                .topN(this.topN)
                .returnDocuments(this.returnDocuments)
                .instruct(this.instruct)
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
            return DashScopeRerankProperties.this.getModel();
        }

        public void setModel(String model) {
            DashScopeRerankProperties.this.setModel(model);
        }

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

    }

}
