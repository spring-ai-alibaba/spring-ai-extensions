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

import com.alibaba.cloud.ai.dashscope.api.DashScopeAudioSpeechApi;
import com.alibaba.cloud.ai.dashscope.audio.tts.DashScopeAudioSpeechModel;
import com.alibaba.cloud.ai.model.SpringAIAlibabaModels;
import org.springframework.ai.model.SimpleApiKey;
import org.springframework.ai.model.SpringAIModelProperties;
import org.springframework.ai.retry.RetryUtils;
import org.springframework.ai.retry.autoconfigure.SpringAiRetryAutoConfiguration;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.client.RestClientAutoConfiguration;
import org.springframework.boot.autoconfigure.web.reactive.function.client.WebClientAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;

import static com.alibaba.cloud.ai.autoconfigure.dashscope.DashScopeConnectionUtils.resolveConnectionProperties;

/**
 * Spring AI Alibaba DashScope Audio Speech Auto Configuration.
 * @author yuluo, yingzi
 */

@AutoConfiguration(after = { RestClientAutoConfiguration.class, WebClientAutoConfiguration.class,
		SpringAiRetryAutoConfiguration.class })
@ConditionalOnDashScopeEnabled
@ConditionalOnClass(DashScopeAudioSpeechApi.class)
@ConditionalOnProperty(name = SpringAIModelProperties.AUDIO_SPEECH_MODEL, havingValue = SpringAIAlibabaModels.DASHSCOPE,
		matchIfMissing = true)
@EnableConfigurationProperties({ DashScopeConnectionProperties.class, DashScopeAudioSpeechProperties.class })
public class DashScopeAudioSpeechAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public DashScopeAudioSpeechModel dashScopeSpeechSynthesisModel(DashScopeConnectionProperties commonProperties,
                                                                   DashScopeAudioSpeechProperties audioSpeechProperties,
                                                                   ObjectProvider<WebClient.Builder> webClientBuilderProvider,
                                                                   ObjectProvider<RestClient.Builder> restClientBuilderProvider,
                                                                   ObjectProvider<RetryTemplate> retryTemplate,
                                                                   ObjectProvider<ResponseErrorHandler> responseErrorHandler) {

        ResolvedConnectionProperties resolved = resolveConnectionProperties(commonProperties, audioSpeechProperties,
                "audio.speech");

        var dashScopeAudioSpeechApi = DashScopeAudioSpeechApi.builder()
                .baseUrl(resolved.baseUrl())
                .websocketUrl(audioSpeechProperties.getWebsocketUrl())
                .apiKey(new SimpleApiKey(resolved.apiKey()))
                .workSpaceId(resolved.workspaceId())
                .restClientBuilder(restClientBuilderProvider.getIfAvailable(RestClient::builder))
                .webClientBuilder(webClientBuilderProvider.getIfAvailable(WebClient::builder))
                .headers(resolved.headers())
                .responseErrorHandler(responseErrorHandler.getIfAvailable(() -> RetryUtils.DEFAULT_RESPONSE_ERROR_HANDLER))
                .build();

		return DashScopeAudioSpeechModel.builder()
                .audioSpeechApi(dashScopeAudioSpeechApi)
                .defaultOptions(audioSpeechProperties.getOptions())
                .retryTemplate(retryTemplate.getIfUnique(() -> RetryUtils.DEFAULT_RETRY_TEMPLATE))
                .build();
	}

}
