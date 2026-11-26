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

package com.alibaba.cloud.ai.dashscope.audio.transcription;

import org.springframework.ai.audio.transcription.AudioTranscriptionOptions;
import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.audio.transcription.AudioTranscriptionResponse;
import org.springframework.ai.model.StreamingModel;
import org.springframework.core.io.Resource;
import reactor.core.publisher.Flux;

/**
 * Interface for the streaming audio transcription model.
 * <p>
 * Provides unidirectional streaming: {@link #stream(AudioTranscriptionPrompt)} - audio input is provided
 * as a complete resource (file/URL), output is streamed. Suitable for pre-recorded audio.
 * <p>
 * For bidirectional streaming (real-time microphone input), use the scenario-specific methods on
 * {@link DashScopeAudioTranscriptionModel}:
 * <ul>
 *   <li>{@code streamRecognition} - Paraformer / Fun-ASR real-time recognition</li>
 *   <li>{@code streamTranslation} - Gummy long-speech translation</li>
 *   <li>{@code streamTranslationChat} - Gummy short-speech (single sentence) translation</li>
 * </ul>
 *
 * @author xuguan
 * @since 1.1.0.0
 */
@FunctionalInterface
public interface StreamingTranscriptionModel extends StreamingModel<AudioTranscriptionPrompt, AudioTranscriptionResponse> {

	default Flux<String> stream(Resource audioResource) {
		AudioTranscriptionPrompt prompt = new AudioTranscriptionPrompt(audioResource);
		return stream(prompt).map(response -> (response.getResult() == null || response.getResult().getOutput() == null)
			? "" : response.getResult().getOutput());
	}

	default Flux<String> stream(Resource audioResource, AudioTranscriptionOptions options) {
		AudioTranscriptionPrompt prompt = new AudioTranscriptionPrompt(audioResource, options);
		return stream(prompt).map(response -> (response.getResult() == null || response.getResult().getOutput() == null)
			? "" : response.getResult().getOutput());
	}

	Flux<AudioTranscriptionResponse> stream(AudioTranscriptionPrompt prompt);

}
