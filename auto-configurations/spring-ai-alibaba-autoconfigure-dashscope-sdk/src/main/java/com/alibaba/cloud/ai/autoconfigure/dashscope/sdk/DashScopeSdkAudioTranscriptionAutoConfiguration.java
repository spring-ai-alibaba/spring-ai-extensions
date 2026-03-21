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

import com.alibaba.cloud.ai.dashscope.sdk.audio.transcription.DashScopeSdkAudioTranscriptionModel;
import com.alibaba.dashscope.audio.asr.transcription.Transcription;
import org.springframework.ai.model.SpringAIModelProperties;
import org.springframework.ai.retry.RetryUtils;
import org.springframework.ai.retry.autoconfigure.SpringAiRetryAutoConfiguration;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.retry.support.RetryTemplate;

import static com.alibaba.cloud.ai.autoconfigure.dashscope.sdk.DashScopeSdkConnectionUtils.resolveConnectionProperties;

/**
 * Auto configuration for DashScope SDK audio transcription model.
 */
@AutoConfiguration(after = SpringAiRetryAutoConfiguration.class)
@ConditionalOnDashScopeSdkEnabled
@ConditionalOnClass(Transcription.class)
@ConditionalOnProperty(name = SpringAIModelProperties.AUDIO_TRANSCRIPTION_MODEL, havingValue = "dashscope-sdk", matchIfMissing = true)
@EnableConfigurationProperties({ DashScopeSdkConnectionProperties.class, DashScopeSdkAudioTranscriptionProperties.class })
public class DashScopeSdkAudioTranscriptionAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	@ConditionalOnProperty(prefix = DashScopeSdkAudioTranscriptionProperties.CONFIG_PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
	public DashScopeSdkAudioTranscriptionModel dashScopeSdkAudioTranscriptionModel(
			ObjectProvider<RetryTemplate> retryTemplate,
			DashScopeSdkConnectionProperties commonProperties,
			DashScopeSdkAudioTranscriptionProperties audioTranscriptionProperties) {

		ResolvedConnectionProperties resolved = resolveConnectionProperties(commonProperties, audioTranscriptionProperties);

		return DashScopeSdkAudioTranscriptionModel.builder()
			.apiKey(resolved.apiKey())
			.workspaceId(resolved.workspaceId())
			.connectionHeaders(resolved.headers())
			.defaultOptions(audioTranscriptionProperties.getOptions())
			.retryTemplate(retryTemplate.getIfUnique(() -> RetryUtils.DEFAULT_RETRY_TEMPLATE))
			.build();
	}

}
