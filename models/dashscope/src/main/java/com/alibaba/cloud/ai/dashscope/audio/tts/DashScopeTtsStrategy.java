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

import org.springframework.ai.audio.tts.TextToSpeechResponse;
import reactor.core.publisher.Flux;

/**
 * Strategy interface for different TTS backends (Qwen-TTS, Sambert, CosyVoice, Qwen TTS Realtime).
 *
 * @author spring-ai-alibaba
 */
public interface DashScopeTtsStrategy {

	/**
	 * Whether this strategy supports the given model.
	 */
	boolean supports(String modelName);

	/**
	 * Synchronous call - returns full audio. Only supported by Qwen-TTS.
	 */
	default TextToSpeechResponse call(String text, DashScopeAudioSpeechOptions options) {
		throw new UnsupportedOperationException(
				"Model " + options.getModel() + " does not support synchronous call");
	}

	/**
	 * Stream audio output. All models support this.
	 */
	Flux<TextToSpeechResponse> stream(String text, DashScopeAudioSpeechOptions options);

}
