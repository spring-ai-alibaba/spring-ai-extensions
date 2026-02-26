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
package com.alibaba.cloud.ai.dashscope.audio.tts;

import com.alibaba.cloud.ai.dashscope.api.tts.DashScopeQwenTTSApi;
import com.alibaba.cloud.ai.dashscope.api.tts.DashScopeQwenTTSRealtimeApi;
import com.alibaba.cloud.ai.dashscope.api.tts.DashScopeWebSocketTTSApi;
import com.alibaba.cloud.ai.dashscope.common.DashScopeAudioApiConstants;
import org.springframework.ai.audio.tts.Speech;
import org.springframework.ai.audio.tts.TextToSpeechResponse;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * Registry of TTS strategies. Selects the appropriate strategy based on model name.
 *
 * @author spring-ai-alibaba
 */
public class DashScopeTtsStrategyRegistry {

	private final DashScopeQwenTTSApi qwenTTSApi;
	private final DashScopeWebSocketTTSApi webSocketTTSApi;
	private final DashScopeQwenTTSRealtimeApi qwenTTSRealtimeApi;

	public DashScopeTtsStrategyRegistry(DashScopeQwenTTSApi qwenTTSApi,
			DashScopeWebSocketTTSApi webSocketTTSApi,
			DashScopeQwenTTSRealtimeApi qwenTTSRealtimeApi) {
		this.qwenTTSApi = qwenTTSApi;
		this.webSocketTTSApi = webSocketTTSApi;
		this.qwenTTSRealtimeApi = qwenTTSRealtimeApi;
	}

	public DashScopeTtsStrategy getStrategy(String modelName) {
		if (DashScopeAudioApiConstants.isQwenTTSModel(modelName)) {
			return new QwenTTSStrategy(qwenTTSApi);
		}
		if (DashScopeAudioApiConstants.isWebsocketByTTSModelName(modelName)) {
			return new WebSocketTTSStrategy(webSocketTTSApi);
		}
		if (DashScopeAudioApiConstants.isQwenTTSRealtimeModel(modelName)) {
			return new QwenTTSRealtimeStrategy(qwenTTSRealtimeApi);
		}
		return null;
	}

	public boolean supports(String modelName) {
		return getStrategy(modelName) != null;
	}

	private static class QwenTTSStrategy implements DashScopeTtsStrategy {

		private final DashScopeQwenTTSApi api;

		QwenTTSStrategy(DashScopeQwenTTSApi api) {
			this.api = api;
		}

		@Override
		public boolean supports(String modelName) {
			return DashScopeAudioApiConstants.isQwenTTSModel(modelName);
		}

		@Override
		public TextToSpeechResponse call(String text, DashScopeAudioSpeechOptions options) {
			return api.call(text, options);
		}

		@Override
		public Flux<TextToSpeechResponse> stream(String text, DashScopeAudioSpeechOptions options) {
			return api.stream(text, options).map(r -> (TextToSpeechResponse) r);
		}
	}

	private static class WebSocketTTSStrategy implements DashScopeTtsStrategy {

		private final DashScopeWebSocketTTSApi api;

		WebSocketTTSStrategy(DashScopeWebSocketTTSApi api) {
			this.api = api;
		}

		@Override
		public boolean supports(String modelName) {
			return DashScopeAudioApiConstants.isWebsocketByTTSModelName(modelName);
		}

		@Override
		public Flux<TextToSpeechResponse> stream(String text, DashScopeAudioSpeechOptions options) {
			return api.stream(text, options).map(byteBuffer -> {
				byte[] data = new byte[byteBuffer.remaining()];
				byteBuffer.get(data);
				return new TextToSpeechResponse(List.of(new Speech(data)));
			});
		}
	}

	private static class QwenTTSRealtimeStrategy implements DashScopeTtsStrategy {

		private final DashScopeQwenTTSRealtimeApi api;

		QwenTTSRealtimeStrategy(DashScopeQwenTTSRealtimeApi api) {
			this.api = api;
		}

		@Override
		public boolean supports(String modelName) {
			return DashScopeAudioApiConstants.isQwenTTSRealtimeModel(modelName);
		}

		@Override
		public Flux<TextToSpeechResponse> stream(String text, DashScopeAudioSpeechOptions options) {
			return api.stream(text, options).map(byteBuffer -> {
				byte[] data = new byte[byteBuffer.remaining()];
				byteBuffer.get(data);
				return new TextToSpeechResponse(List.of(new Speech(data)));
			});
		}
	}
}
