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
 * DashScope video auto-configuration properties.
 *
 * @author dashscope
 * @author yuluo
 * @author xuguan
 * @since 1.0.0.3
 */
@ConfigurationProperties(prefix = DashScopeVideoProperties.CONFIG_PREFIX)
public class DashScopeVideoProperties extends DashScopeParentProperties {

	public static final String CONFIG_PREFIX = "spring.ai.dashscope.video";

	private String videoPath = DashScopeVideoApiConstants.VIDEO_GENERATION_SYNTHESIS;

	private String queryTaskPath = DashScopeApiConstants.QUERY_TASK_RESTFUL_URL;

	private @Nullable String model = DashScopeVideoOptions.DEFAULT_MODEL;

	private DashScopeVideoOptions.@Nullable InputOptions input;

	private DashScopeVideoOptions.@Nullable ParametersOptions parameters;

	public String getVideoPath() {
		return this.videoPath;
	}

	public void setVideoPath(String videoPath) {
		this.videoPath = videoPath;
	}

	public String getQueryTaskPath() {
		return this.queryTaskPath;
	}

	public void setQueryTaskPath(String queryTaskPath) {
		this.queryTaskPath = queryTaskPath;
	}

	public @Nullable String getModel() {
		return this.model;
	}

	public void setModel(@Nullable String model) {
		this.model = model;
	}

	public DashScopeVideoOptions.@Nullable InputOptions getInput() {
		return this.input;
	}

	public void setInput(DashScopeVideoOptions.@Nullable InputOptions input) {
		this.input = input;
	}

	public DashScopeVideoOptions.@Nullable ParametersOptions getParameters() {
		return this.parameters;
	}

	public void setParameters(DashScopeVideoOptions.@Nullable ParametersOptions parameters) {
		this.parameters = parameters;
	}

	public DashScopeVideoOptions toOptions() {
		return DashScopeVideoOptions.builder()
				.model(this.model)
				.input(this.input)
				.parameters(this.parameters)
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
