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

import com.alibaba.cloud.ai.dashscope.sdk.audio.transcription.DashScopeSdkAudioTranscriptionOptions;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * DashScope SDK audio transcription model properties.
 */
@ConfigurationProperties(DashScopeSdkAudioTranscriptionProperties.CONFIG_PREFIX)
public class DashScopeSdkAudioTranscriptionProperties extends DashScopeSdkParentProperties {

	public static final String CONFIG_PREFIX = "spring.ai.dashscope.sdk.audio.transcription";

	private boolean enabled = true;

	@NestedConfigurationProperty
	private DashScopeSdkAudioTranscriptionOptions options = DashScopeSdkAudioTranscriptionOptions.builder()
		.model("paraformer-v2")
		.build();

	public boolean isEnabled() {
		return this.enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public DashScopeSdkAudioTranscriptionOptions getOptions() {
		return this.options;
	}

	public void setOptions(DashScopeSdkAudioTranscriptionOptions options) {
		this.options = options;
	}

}
