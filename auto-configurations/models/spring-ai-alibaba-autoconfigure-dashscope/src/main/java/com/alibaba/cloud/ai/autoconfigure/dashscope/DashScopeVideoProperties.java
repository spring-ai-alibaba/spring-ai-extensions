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
import com.alibaba.cloud.ai.dashscope.common.DashScopeVideoApiConstants;
import com.alibaba.cloud.ai.dashscope.video.DashScopeVideoOptions;
import org.jspecify.annotations.Nullable;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.DeprecatedConfigurationProperty;

/**
 * DashScope Video Generation Properties.
 *
 * @author dashscope
 * @author yuluo
 * @since 1.0.0.3
 */

@ConfigurationProperties(prefix = DashScopeVideoProperties.CONFIG_PREFIX)
public class DashScopeVideoProperties extends DashScopeParentProperties {

	public static final String CONFIG_PREFIX = "spring.ai.dashscope.video";

    private String videoPath = DashScopeVideoApiConstants.VIDEO_GENERATION_SYNTHESIS;

    private String queryTaskPath = DashScopeApiConstants.QUERY_TASK_RESTFUL_URL;
    private DashScopeVideoOptions options = DashScopeVideoOptions.builder().build();
	private final Options legacyOptions = new Options();

    public DashScopeVideoOptions toOptions() {
		if (this.options == null) {
			this.options = DashScopeVideoOptions.builder().build();
		}
		return this.options;
	}

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public String getQueryTaskPath() {
        return queryTaskPath;
    }

    public void setQueryTaskPath(String queryTaskPath) {
        this.queryTaskPath = queryTaskPath;
    }

    @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX)
    @Deprecated(since = "2.0.0", forRemoval = true)
	public Options getOptions() {
		return this.legacyOptions;
	}

	public void setOptions(Options options) {
		// Deprecated options are applied by the nested Options setters.
	}

	private void updateOptions(java.util.function.Consumer<DashScopeVideoOptions.Builder> customizer) {
		DashScopeVideoOptions.Builder builder = DashScopeVideoOptions.builder().from(toOptions());
		customizer.accept(builder);
		this.options = builder.build();
	}

    public @Nullable String getModel() {
        return toOptions().getModel();
    }

    public void setModel(String model) {
        updateOptions(builder -> builder.model(model));
    }

    public DashScopeVideoOptions.@Nullable InputOptions getInput() {
        return toOptions().getInput();
    }

    public void setInput(DashScopeVideoOptions.InputOptions input) {
        updateOptions(builder -> builder.input(input));
    }

    public DashScopeVideoOptions.@Nullable ParametersOptions getParameters() {
        return toOptions().getParameters();
    }

    public void setParameters(DashScopeVideoOptions.ParametersOptions parameters) {
        updateOptions(builder -> builder.parameters(parameters));
    }
	public class Options {

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".model")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable String getModel() {
			return DashScopeVideoProperties.this.getModel();
		}

		public void setModel(String model) {
			DashScopeVideoProperties.this.setModel(model);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".input")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public DashScopeVideoOptions.@Nullable InputOptions getInput() {
			return DashScopeVideoProperties.this.getInput();
		}

		public void setInput(DashScopeVideoOptions.InputOptions input) {
			DashScopeVideoProperties.this.setInput(input);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".parameters")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public DashScopeVideoOptions.@Nullable ParametersOptions getParameters() {
			return DashScopeVideoProperties.this.getParameters();
		}

		public void setParameters(DashScopeVideoOptions.ParametersOptions parameters) {
			DashScopeVideoProperties.this.setParameters(parameters);
		}

	}


}
