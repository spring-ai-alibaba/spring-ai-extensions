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

import java.util.List;
import java.util.Map;

import com.alibaba.cloud.ai.dashscope.sdk.chat.DashScopeSdkChatOptions;
import org.jspecify.annotations.Nullable;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.DeprecatedConfigurationProperty;

/**
 * DashScope SDK chat model properties.
 */
@ConfigurationProperties(DashScopeSdkChatProperties.CONFIG_PREFIX)
public class DashScopeSdkChatProperties extends DashScopeSdkParentProperties {

	public static final String CONFIG_PREFIX = "spring.ai.dashscope.sdk.chat";

	public static final String DEFAULT_DEPLOYMENT_NAME = "qwen-plus";

	private boolean enabled = true;
	private DashScopeSdkChatOptions options = DashScopeSdkChatOptions.builder()
		.model(DEFAULT_DEPLOYMENT_NAME)
		.build();
	private final Options legacyOptions = new Options();

	public DashScopeSdkChatOptions toOptions() {
		if (this.options == null) {
			this.options = DashScopeSdkChatOptions.builder().model(DEFAULT_DEPLOYMENT_NAME).build();
		}
		return this.options;
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX)
	@Deprecated(since = "2.0.0", forRemoval = true)
	public Options getOptions() {
		return this.legacyOptions;
	}

	public void setOptions(Options options) {
		// Deprecated options are applied by the nested Options setters.
	}

	private void updateOptions(java.util.function.Consumer<DashScopeSdkChatOptions.Builder> customizer) {
		DashScopeSdkChatOptions.Builder builder = toOptions().mutate();
		customizer.accept(builder);
		this.options = builder.build();
	}

	public @Nullable String getModel() {
		return toOptions().getModel();
	}

	public void setModel(String model) {
		updateOptions(builder -> builder.model(model));
	}

	public @Nullable Boolean getStream() {
		return toOptions().getStream();
	}

	public void setStream(Boolean stream) {
		updateOptions(builder -> builder.stream(stream));
	}

	public @Nullable Double getTemperature() {
		return toOptions().getTemperature();
	}

	public void setTemperature(Double temperature) {
		updateOptions(builder -> builder.temperature(temperature));
	}

	public @Nullable Integer getSeed() {
		return toOptions().getSeed();
	}

	public void setSeed(Integer seed) {
		updateOptions(builder -> builder.seed(seed));
	}

	public @Nullable Double getTopP() {
		return toOptions().getTopP();
	}

	public void setTopP(Double topP) {
		updateOptions(builder -> builder.topP(topP));
	}

	public @Nullable Integer getTopK() {
		return toOptions().getTopK();
	}

	public void setTopK(Integer topK) {
		updateOptions(builder -> builder.topK(topK));
	}

	public @Nullable Object getStop() {
		return toOptions().getStop();
	}

	public void setStop(Object stop) {
		updateOptions(builder -> builder.stop(stop));
	}

	public @Nullable Boolean getEnableSearch() {
		return toOptions().getEnableSearch();
	}

	public void setEnableSearch(Boolean enableSearch) {
		updateOptions(builder -> builder.enableSearch(enableSearch));
	}

	public @Nullable Integer getMaxTokens() {
		return toOptions().getMaxTokens();
	}

	public void setMaxTokens(Integer maxTokens) {
		updateOptions(builder -> builder.maxTokens(maxTokens));
	}

	public @Nullable Boolean getIncrementalOutput() {
		return toOptions().getIncrementalOutput();
	}

	public void setIncrementalOutput(Boolean incrementalOutput) {
		updateOptions(builder -> builder.incrementalOutput(incrementalOutput));
	}

	public @Nullable Double getRepetitionPenalty() {
		return toOptions().getRepetitionPenalty();
	}

	public void setRepetitionPenalty(Double repetitionPenalty) {
		updateOptions(builder -> builder.repetitionPenalty(repetitionPenalty));
	}

	public @Nullable Object getToolChoice() {
		return toOptions().getToolChoice();
	}

	public void setToolChoice(Object toolChoice) {
		updateOptions(builder -> builder.toolChoice(toolChoice));
	}

	public @Nullable Map<String, String> getHttpHeaders() {
		return toOptions().getHttpHeaders();
	}

	public void setHttpHeaders(Map<String, String> httpHeaders) {
		updateOptions(builder -> builder.httpHeaders(httpHeaders));
	}

	public @Nullable Map<String, Object> getExtraBody() {
		return toOptions().getExtraBody();
	}

	public void setExtraBody(Map<String, Object> extraBody) {
		updateOptions(builder -> builder.extraBody(extraBody));
	}

	public @Nullable List<ToolCallback> getToolCallbacks() {
		return toOptions().getToolCallbacks();
	}

	public void setToolCallbacks(List<ToolCallback> toolCallbacks) {
		updateOptions(builder -> builder.toolCallbacks(toolCallbacks));
	}

	public @Nullable Map<String, Object> getToolContext() {
		return toOptions().getToolContext();
	}

	public void setToolContext(Map<String, Object> toolContext) {
		updateOptions(builder -> builder.toolContext(toolContext));
	}
	public class Options {

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".model")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable String getModel() {
			return DashScopeSdkChatProperties.this.getModel();
		}

		public void setModel(String model) {
			DashScopeSdkChatProperties.this.setModel(model);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".stream")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Boolean getStream() {
			return DashScopeSdkChatProperties.this.getStream();
		}

		public void setStream(Boolean stream) {
			DashScopeSdkChatProperties.this.setStream(stream);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".temperature")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Double getTemperature() {
			return DashScopeSdkChatProperties.this.getTemperature();
		}

		public void setTemperature(Double temperature) {
			DashScopeSdkChatProperties.this.setTemperature(temperature);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".seed")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Integer getSeed() {
			return DashScopeSdkChatProperties.this.getSeed();
		}

		public void setSeed(Integer seed) {
			DashScopeSdkChatProperties.this.setSeed(seed);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".top-p")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Double getTopP() {
			return DashScopeSdkChatProperties.this.getTopP();
		}

		public void setTopP(Double topP) {
			DashScopeSdkChatProperties.this.setTopP(topP);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".top-k")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Integer getTopK() {
			return DashScopeSdkChatProperties.this.getTopK();
		}

		public void setTopK(Integer topK) {
			DashScopeSdkChatProperties.this.setTopK(topK);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".stop")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Object getStop() {
			return DashScopeSdkChatProperties.this.getStop();
		}

		public void setStop(Object stop) {
			DashScopeSdkChatProperties.this.setStop(stop);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".enable-search")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Boolean getEnableSearch() {
			return DashScopeSdkChatProperties.this.getEnableSearch();
		}

		public void setEnableSearch(Boolean enableSearch) {
			DashScopeSdkChatProperties.this.setEnableSearch(enableSearch);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".max-tokens")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Integer getMaxTokens() {
			return DashScopeSdkChatProperties.this.getMaxTokens();
		}

		public void setMaxTokens(Integer maxTokens) {
			DashScopeSdkChatProperties.this.setMaxTokens(maxTokens);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".incremental-output")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Boolean getIncrementalOutput() {
			return DashScopeSdkChatProperties.this.getIncrementalOutput();
		}

		public void setIncrementalOutput(Boolean incrementalOutput) {
			DashScopeSdkChatProperties.this.setIncrementalOutput(incrementalOutput);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".repetition-penalty")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Double getRepetitionPenalty() {
			return DashScopeSdkChatProperties.this.getRepetitionPenalty();
		}

		public void setRepetitionPenalty(Double repetitionPenalty) {
			DashScopeSdkChatProperties.this.setRepetitionPenalty(repetitionPenalty);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".tool-choice")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Object getToolChoice() {
			return DashScopeSdkChatProperties.this.getToolChoice();
		}

		public void setToolChoice(Object toolChoice) {
			DashScopeSdkChatProperties.this.setToolChoice(toolChoice);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".http-headers")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Map<String, String> getHttpHeaders() {
			return DashScopeSdkChatProperties.this.getHttpHeaders();
		}

		public void setHttpHeaders(Map<String, String> httpHeaders) {
			DashScopeSdkChatProperties.this.setHttpHeaders(httpHeaders);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".extra-body")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Map<String, Object> getExtraBody() {
			return DashScopeSdkChatProperties.this.getExtraBody();
		}

		public void setExtraBody(Map<String, Object> extraBody) {
			DashScopeSdkChatProperties.this.setExtraBody(extraBody);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".tool-callbacks")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable List<ToolCallback> getToolCallbacks() {
			return DashScopeSdkChatProperties.this.getToolCallbacks();
		}

		public void setToolCallbacks(List<ToolCallback> toolCallbacks) {
			DashScopeSdkChatProperties.this.setToolCallbacks(toolCallbacks);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".tool-context")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Map<String, Object> getToolContext() {
			return DashScopeSdkChatProperties.this.getToolContext();
		}

		public void setToolContext(Map<String, Object> toolContext) {
			DashScopeSdkChatProperties.this.setToolContext(toolContext);
		}

	}


}
