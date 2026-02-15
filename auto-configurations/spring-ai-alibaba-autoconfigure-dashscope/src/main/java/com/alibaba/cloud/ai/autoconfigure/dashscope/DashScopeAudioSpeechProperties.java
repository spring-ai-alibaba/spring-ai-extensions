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

import com.alibaba.cloud.ai.dashscope.audio.tts.DashScopeAudioSpeechOptions;
import com.alibaba.cloud.ai.dashscope.common.DashScopeAudioApiConstants;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * @author kevinlin09、yingzi
 */

@ConfigurationProperties(DashScopeAudioSpeechProperties.CONFIG_PREFIX)
public class DashScopeAudioSpeechProperties extends DashScopeParentProperties {

	/**
	 * Spring AI Alibaba configuration prefix.
	 */
	public static final String CONFIG_PREFIX = "spring.ai.dashscope.audio.speech";

    private String websocketUrl = DashScopeAudioApiConstants.DEFAULT_WEBSOCKET_URL;

    @NestedConfigurationProperty
	private DashScopeAudioSpeechOptions options = DashScopeAudioSpeechOptions.builder().build();

	public DashScopeAudioSpeechOptions getOptions() {
		return options;
	}

	public void setOptions(DashScopeAudioSpeechOptions options) {
		this.options = options;
	}

    public String getWebsocketUrl() {
        return websocketUrl;
    }

    public void setWebsocketUrl(String websocketUrl) {
        this.websocketUrl = websocketUrl;
    }

}
