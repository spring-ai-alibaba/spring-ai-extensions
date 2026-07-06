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

import java.util.List;

import com.alibaba.cloud.ai.dashscope.agent.DashScopeAgentFlowStreamMode;
import com.alibaba.cloud.ai.dashscope.agent.DashScopeAgentOptions;
import com.alibaba.cloud.ai.dashscope.agent.DashScopeAgentRagOptions;
import tools.jackson.databind.JsonNode;

import org.jspecify.annotations.Nullable;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.DeprecatedConfigurationProperty;

import static com.alibaba.cloud.ai.dashscope.common.DashScopeApiConstants.APPS_COMPLETION_RESTFUL_URL;

/**
 * @author yuluo
 * @author <a href="mailto:yuluo08290126@gmail.com">yuluo</a>
 */

@ConfigurationProperties(DashScopeAgentProperties.CONFIG_PREFIX)
public class DashScopeAgentProperties extends DashScopeParentProperties {

    /**
     * Spring AI Alibaba configuration prefix.
     */
    public static final String CONFIG_PREFIX = "spring.ai.dashscope.agent";

    /**
     * Enable DashScope ai agent client.
     */
    private boolean enabled = true;

    /**
     * DashScope ai agent path.
     */
    private String agentPath = APPS_COMPLETION_RESTFUL_URL;
    private DashScopeAgentOptions options = DashScopeAgentOptions.builder().build();
	private final Options legacyOptions = new Options();

    public DashScopeAgentOptions toOptions() {
		if (this.options == null) {
			this.options = DashScopeAgentOptions.builder().build();
		}
		return this.options;
	}

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getAgentPath() {
        return this.agentPath;
    }

    public void setAgentPath(String agentPath) {
        this.agentPath = agentPath;
    }

    @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX)
    @Deprecated(since = "2.0.0", forRemoval = true)
    public Options getOptions() {
		return this.legacyOptions;
	}

	public void setOptions(Options options) {
		// Deprecated options are applied by the nested Options setters.
	}

	private void updateOptions(java.util.function.Consumer<DashScopeAgentOptions.Builder> customizer) {
		DashScopeAgentOptions.Builder builder = toOptions().mutate();
		customizer.accept(builder);
		this.options = builder.build();
	}

    public @Nullable String getAppId() {
        return toOptions().getAppId();
    }

    public void setAppId(String appId) {
        updateOptions(builder -> builder.appId(appId));
    }

    public @Nullable String getSessionId() {
        return toOptions().getSessionId();
    }

    public void setSessionId(String sessionId) {
        updateOptions(builder -> builder.sessionId(sessionId));
    }

    public @Nullable String getMemoryId() {
        return toOptions().getMemoryId();
    }

    public void setMemoryId(String memoryId) {
        updateOptions(builder -> builder.memoryId(memoryId));
    }

    public @Nullable String getModelId() {
        return toOptions().getModelId();
    }

    public void setModelId(String modelId) {
        updateOptions(builder -> builder.modelId(modelId));
    }

    public @Nullable Boolean getIncrementalOutput() {
        return toOptions().getIncrementalOutput();
    }

    public void setIncrementalOutput(Boolean incrementalOutput) {
        updateOptions(builder -> builder.incrementalOutput(incrementalOutput));
    }

    public @Nullable Boolean getHasThoughts() {
        return toOptions().getHasThoughts();
    }

    public void setHasThoughts(Boolean hasThoughts) {
        updateOptions(builder -> builder.hasThoughts(hasThoughts));
    }

    public @Nullable Boolean getEnableThinking() {
        return toOptions().getEnableThinking();
    }

    public void setEnableThinking(Boolean enableThinking) {
        updateOptions(builder -> builder.enableThinking(enableThinking));
    }

    public @Nullable List<String> getImages() {
        return toOptions().getImages();
    }

    public void setImages(List<String> images) {
        updateOptions(builder -> builder.images(images));
    }

    public @Nullable List<String> getFiles() {
        return toOptions().getFiles();
    }

    public void setFiles(List<String> files) {
        updateOptions(builder -> builder.files(files));
    }

    public @Nullable JsonNode getBizParams() {
        return toOptions().getBizParams();
    }

    public void setBizParams(JsonNode bizParams) {
        updateOptions(builder -> builder.bizParams(bizParams));
    }

    public @Nullable DashScopeAgentRagOptions getRagOptions() {
        return toOptions().getRagOptions();
    }

    public void setRagOptions(DashScopeAgentRagOptions ragOptions) {
        updateOptions(builder -> builder.ragOptions(ragOptions));
    }

    public @Nullable DashScopeAgentFlowStreamMode getFlowStreamMode() {
        return toOptions().getFlowStreamMode();
    }

    public void setFlowStreamMode(DashScopeAgentFlowStreamMode flowStreamMode) {
        updateOptions(builder -> builder.flowStreamMode(flowStreamMode));
    }
	public class Options {

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".app-id")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable String getAppId() {
			return DashScopeAgentProperties.this.getAppId();
		}

		public void setAppId(String appId) {
			DashScopeAgentProperties.this.setAppId(appId);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".session-id")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable String getSessionId() {
			return DashScopeAgentProperties.this.getSessionId();
		}

		public void setSessionId(String sessionId) {
			DashScopeAgentProperties.this.setSessionId(sessionId);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".memory-id")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable String getMemoryId() {
			return DashScopeAgentProperties.this.getMemoryId();
		}

		public void setMemoryId(String memoryId) {
			DashScopeAgentProperties.this.setMemoryId(memoryId);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".model-id")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable String getModelId() {
			return DashScopeAgentProperties.this.getModelId();
		}

		public void setModelId(String modelId) {
			DashScopeAgentProperties.this.setModelId(modelId);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".incremental-output")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Boolean getIncrementalOutput() {
			return DashScopeAgentProperties.this.getIncrementalOutput();
		}

		public void setIncrementalOutput(Boolean incrementalOutput) {
			DashScopeAgentProperties.this.setIncrementalOutput(incrementalOutput);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".has-thoughts")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Boolean getHasThoughts() {
			return DashScopeAgentProperties.this.getHasThoughts();
		}

		public void setHasThoughts(Boolean hasThoughts) {
			DashScopeAgentProperties.this.setHasThoughts(hasThoughts);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".enable-thinking")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Boolean getEnableThinking() {
			return DashScopeAgentProperties.this.getEnableThinking();
		}

		public void setEnableThinking(Boolean enableThinking) {
			DashScopeAgentProperties.this.setEnableThinking(enableThinking);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".images")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable List<String> getImages() {
			return DashScopeAgentProperties.this.getImages();
		}

		public void setImages(List<String> images) {
			DashScopeAgentProperties.this.setImages(images);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".files")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable List<String> getFiles() {
			return DashScopeAgentProperties.this.getFiles();
		}

		public void setFiles(List<String> files) {
			DashScopeAgentProperties.this.setFiles(files);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".biz-params")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable JsonNode getBizParams() {
			return DashScopeAgentProperties.this.getBizParams();
		}

		public void setBizParams(JsonNode bizParams) {
			DashScopeAgentProperties.this.setBizParams(bizParams);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".rag-options")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable DashScopeAgentRagOptions getRagOptions() {
			return DashScopeAgentProperties.this.getRagOptions();
		}

		public void setRagOptions(DashScopeAgentRagOptions ragOptions) {
			DashScopeAgentProperties.this.setRagOptions(ragOptions);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".flow-stream-mode")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable DashScopeAgentFlowStreamMode getFlowStreamMode() {
			return DashScopeAgentProperties.this.getFlowStreamMode();
		}

		public void setFlowStreamMode(DashScopeAgentFlowStreamMode flowStreamMode) {
			DashScopeAgentProperties.this.setFlowStreamMode(flowStreamMode);
		}

	}


}
