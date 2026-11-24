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

import org.junit.jupiter.api.Test;
import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.audio.transcription.AudioTranscriptionResponse;
import org.springframework.ai.audio.transcription.TranscriptionModel;
import org.springframework.core.io.ByteArrayResource;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.mock;

class AudioTranscriptionModelCompatibilityTests {

    @Test
    void upstreamTranscriptionInterfaceDispatchesToDashScopeStreamingImplementation() {
        AudioTranscriptionResponse response = mock(AudioTranscriptionResponse.class);
        TranscriptionModel model = new AudioTranscriptionModel() {
            @Override
            public AudioTranscriptionResponse call(AudioTranscriptionPrompt prompt) {
                return response;
            }

            @Override
            public Flux<AudioTranscriptionResponse> stream(AudioTranscriptionPrompt prompt) {
                return Flux.just(response);
            }
        };
        StepVerifier.create(model.stream(new AudioTranscriptionPrompt(new ByteArrayResource(new byte[] {1}))))
                .expectNext(response).verifyComplete();
    }
}
