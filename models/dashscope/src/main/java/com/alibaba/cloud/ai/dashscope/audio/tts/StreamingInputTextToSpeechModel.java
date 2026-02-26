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

import org.springframework.ai.audio.tts.TextToSpeechOptions;
import org.springframework.ai.audio.tts.TextToSpeechResponse;
import reactor.core.publisher.Flux;

/**
 * Interface for TTS models that support streaming text input.
 * <p>
 * Unlike {@link org.springframework.ai.audio.tts.TextToSpeechModel} which accepts complete text
 * upfront, this interface allows text to be sent incrementally (e.g., as it becomes available
 * from LLM streaming output), enabling lower latency for models like CosyVoice and Qwen TTS Realtime.
 *
 * @author spring-ai-alibaba
 */
public interface StreamingInputTextToSpeechModel {

	/**
	 * Stream text-to-speech with streaming text input.
	 * @param textStream the text stream to be synthesized (e.g., from LLM streaming output)
	 * @param options TTS options including model, voice, etc.
	 * @return flux of audio responses (each typically contains a chunk of audio data)
	 */
	Flux<TextToSpeechResponse> stream(Flux<String> textStream, TextToSpeechOptions options);

}
