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
package com.alibaba.cloud.ai.dashscope.api;

import java.net.URI;
import java.time.Duration;
import java.util.List;

import com.alibaba.cloud.ai.dashscope.audio.transcription.DashScopeAudioTranscriptionModel;
import com.alibaba.cloud.ai.dashscope.audio.transcription.DashScopeAudioTranscriptionOptions;
import com.alibaba.cloud.ai.dashscope.audio.transcription.DashScopeAudioTranscriptionPrompt;
import com.alibaba.cloud.ai.dashscope.audio.transcription.DashScopeAudioTranscriptionPrompt.TranscriptionUserMessage;
import com.alibaba.cloud.ai.dashscope.audio.transcription.DashScopeAudioTranscriptionPrompt.TranscriptionUserMessage.Content;
import com.alibaba.cloud.ai.dashscope.audio.transcription.DashScopeAudioTranscriptionPrompt.TranscriptionUserMessage.InputAudio;
import com.alibaba.cloud.ai.dashscope.audio.tts.DashScopeAudioSpeechOptions;
import com.alibaba.cloud.ai.dashscope.audio.tts.DashScopeAudioSpeechModel;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageApiSpec.InvokeMode;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageModel;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageOptions;
import com.alibaba.cloud.ai.dashscope.rerank.DashScopeRerankModel;
import com.alibaba.cloud.ai.dashscope.rerank.DashScopeRerankOptions;
import com.alibaba.cloud.ai.dashscope.video.DashScopeVideoModel;
import com.alibaba.cloud.ai.model.RerankRequest;
import com.alibaba.cloud.ai.dashscope.video.DashScopeVideoOptions;
import com.alibaba.cloud.ai.dashscope.video.DashScopeVideoOptions.ParametersOptions;
import com.alibaba.cloud.ai.dashscope.video.VideoMessage;
import com.alibaba.cloud.ai.dashscope.video.VideoPrompt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.content.Media;
import org.springframework.ai.document.Document;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.audio.tts.TextToSpeechPrompt;
import org.springframework.ai.audio.tts.TextToSpeechOptions;
import org.springframework.ai.model.SimpleApiKey;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.headerDoesNotExist;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class DashScopeCurrentMediaApiTests {

	private static final String BASE = "https://media.example.test";

	private static final String MULTIMODAL = BASE + "/api/v1/services/aigc/multimodal-generation/generation";

	private RestClient.Builder rest;

	private MockRestServiceServer server;

	@BeforeEach
	void setUp() {
		this.rest = RestClient.builder();
		this.server = MockRestServiceServer.bindTo(this.rest).build();
	}

	@Test
	void qwenImageThreeUsesMultimodalSchemaAndDecodesDataUrl() {
		this.server.expect(requestTo(MULTIMODAL)).andExpect(headerDoesNotExist("X-DashScope-Async"))
			.andExpect(jsonPath("$.input.messages[0].content[0].text").value("Draw a circle"))
			.andExpect(jsonPath("$.input.messages[0].content[1].image").value("https://example.test/reference.png"))
			.andExpect(jsonPath("$.parameters.response_format").value("b64_json"))
			.andExpect(jsonPath("$.parameters.prompt_extend_mode").value("direct"))
			.andExpect(jsonPath("$.parameters.enable_thinking").value(false))
			.andRespond(withSuccess("""
					{"output":{"choices":[{"message":{"role":"assistant","content":[{"image":"data:image/png;base64,YWJj"}]}}]}}
					""", MediaType.APPLICATION_JSON));
		var api = DashScopeImageApi.builder().baseUrl(BASE).apiKey("test-key").restClientBuilder(this.rest).build();
		var options = DashScopeImageOptions.builder().model("qwen-image-3.0").responseFormat("b64_json")
			.baseImageUrl("https://example.test/reference.png").promptExtendMode("direct").enableThinking(false).build();
		var model = DashScopeImageModel.builder().dashScopeApi(api).defaultOptions(options).build();
		assertThat(model.call(new ImagePrompt("Draw a circle")).getResult().getOutput().getB64Json()).isEqualTo("YWJj");
		this.server.verify();
	}

	@Test
	void qwenImageTwoOneUsesAsyncSchemaAndPollsTask() {
		this.server.expect(requestTo(BASE + "/api/v1/services/aigc/image-generation/generation"))
			.andExpect(header("X-DashScope-Async", "enable"))
			.andExpect(jsonPath("$.input.messages[0].content[0].text").value("Draw a circle"))
			.andRespond(withSuccess("""
					{"output":{"task_id":"image-1","task_status":"PENDING"}}
					""", MediaType.APPLICATION_JSON));
		this.server.expect(requestTo(BASE + "/api/v1/tasks/image-1")).andExpect(method(HttpMethod.GET))
			.andRespond(withSuccess("""
					{"output":{"task_id":"image-1","task_status":"SUCCEEDED","results":[{"url":"https://example.test/image.png"}]}}
					""", MediaType.APPLICATION_JSON));
		var api = DashScopeImageApi.builder().baseUrl(BASE).apiKey("test-key").restClientBuilder(this.rest).build();
		var options = DashScopeImageOptions.builder().model("qwen-image-2.1-turbo").invokeMode(InvokeMode.ASYNC).build();
		var model = DashScopeImageModel.builder().dashScopeApi(api).defaultOptions(options).pollIntervalMs(1).build();
		assertThat(model.call(new ImagePrompt("Draw a circle")).getResult().getOutput().getUrl())
			.isEqualTo("https://example.test/image.png");
		this.server.verify();
	}

	@Test
	void wanThreeSubmitsPromptAndPollsUntilSuccess() {
		this.server.expect(requestTo(BASE + "/api/v1/services/aigc/video-generation/video-synthesis"))
			.andExpect(header("X-DashScope-Async", "enable"))
			.andExpect(jsonPath("$.model").value("wan3.0-video"))
			.andExpect(jsonPath("$.input.prompt").value("A sunrise"))
			.andExpect(jsonPath("$.parameters.ratio").value("16:9"))
			.andExpect(jsonPath("$.parameters.size").doesNotExist())
			.andRespond(withSuccess("""
					{"output":{"task_id":"video-1","task_status":"PENDING"}}
					""", MediaType.APPLICATION_JSON));
		this.server.expect(requestTo(BASE + "/api/v1/tasks/video-1")).andExpect(method(HttpMethod.GET))
			.andRespond(withSuccess("""
					{"output":{"task_id":"video-1","task_status":"RUNNING"}}
					""", MediaType.APPLICATION_JSON));
		this.server.expect(requestTo(BASE + "/api/v1/tasks/video-1")).andExpect(method(HttpMethod.GET))
			.andRespond(withSuccess("""
					{"output":{"task_id":"video-1","task_status":"SUCCEEDED","video_url":"https://example.test/video.mp4"}}
					""", MediaType.APPLICATION_JSON));
		var api = DashScopeVideoApi.builder().baseUrl(BASE).apiKey("test-key")
			.restClientBuilder(this.rest).build();
		var options = DashScopeVideoOptions.builder().model("wan3.0-video")
			.parameters(ParametersOptions.builder().ratio("16:9").build()).build();
		var model = DashScopeVideoModel.builder().videoApi(api).defaultOptions(options)
			.pollInterval(Duration.ofMillis(1)).build();
		var response = model.call(new VideoPrompt(List.of(new VideoMessage("A sunrise")), options));
		assertThat(response.getResult().getOutput().videoUrl()).isEqualTo("https://example.test/video.mp4");
		this.server.verify();
	}

	@Test
	void qwenAudioTtsUsesDedicatedEndpointAndSpeechBytes() {
		this.server.expect(requestTo(BASE + "/api/v1/services/audio/tts/SpeechSynthesizer"))
			.andExpect(jsonPath("$.input.text").value("Hello")).andExpect(jsonPath("$.input.sample_rate").value(24000))
			.andExpect(jsonPath("$.input.voice").value("longanhuan_v3.1"))
			.andRespond(withSuccess("""
					{"output":{"finish_reason":"stop","audio":{"data":"YWJj"}},"request_id":"tts-1"}
					""", MediaType.APPLICATION_JSON));
		var api = DashScopeAudioSpeechApi.builder().baseUrl(BASE).apiKey(new SimpleApiKey("test-key"))
			.restClientBuilder(this.rest).build();
		var options = DashScopeAudioSpeechOptions.builder().sampleRate(24000).build();
		var model = new DashScopeAudioSpeechModel(api, options);
		var prompt = new TextToSpeechPrompt("Hello",
				TextToSpeechOptions.builder().model("qwen-audio-3.1-tts-flash").build());
		assertThat(model.call(prompt).getResult().getOutput()).containsExactly((byte) 97, (byte) 98, (byte) 99);
		this.server.verify();
	}

	@Test
	void cosyVoiceThreeFiveRequiresAModelSpecificVoice() {
		var api = DashScopeAudioSpeechApi.builder().baseUrl(BASE).apiKey(new SimpleApiKey("test-key"))
			.restClientBuilder(this.rest).build();
		var options = DashScopeAudioSpeechOptions.builder().model("cosyvoice-v3.5-flash").build();
		var model = new DashScopeAudioSpeechModel(api, options);
		assertThatThrownBy(() -> model.stream(new TextToSpeechPrompt("Hello")))
			.isInstanceOf(IllegalArgumentException.class).hasMessageContaining("requires a voice");
		this.server.verify();
	}

	@Test
	void shortAsrSendsContextAndReadsNativeTranscript() {
		this.server.expect(requestTo(MULTIMODAL))
			.andExpect(jsonPath("$.input.messages[0].content[0].text").value("Prior turn"))
			.andExpect(jsonPath("$.input.messages[1].content[0].input_audio.data").value("https://example.test/audio.wav"))
			.andExpect(jsonPath("$.input.messages[1].content[0].input_audio.format").doesNotExist())
			.andExpect(jsonPath("$.parameters.sample_rate").value("16000"))
			.andRespond(withSuccess("""
					{"request_id":"asr-1","output":{"text":"Hello","sentence":{"sentence_id":1,"sentence_end":true}}}
					""", MediaType.APPLICATION_JSON));
		var api = DashScopeAudioTranscriptionApi.builder().baseUrl(BASE).apiKey(new SimpleApiKey("test-key"))
			.restClientBuilder(this.rest).build();
		var options = DashScopeAudioTranscriptionOptions.builder().model("qwen-audio-3.1-asr-flash")
			.format("wav").sampleRate(16000).build();
		var prompt = DashScopeAudioTranscriptionPrompt.withMessages(options,
				List.of(new TranscriptionUserMessage(List.of(new Content("input_text", "Prior turn"))),
						new TranscriptionUserMessage(List.of(new Content("input_audio",
								new InputAudio("https://example.test/audio.wav", "wav"))))));
		assertThat(api.callQwenAudioAsr(prompt, options).getResult().getOutput()).isEqualTo("Hello");
		this.server.verify();
	}

	@Test
	void fileTranscriptionUsesTaskProtocolForQwenThree() {
		this.server.expect(requestTo(BASE + "/api/v1/services/audio/asr/transcription"))
			.andExpect(header("X-DashScope-Async", "enable"))
			.andExpect(jsonPath("$.input.file_url").value("https://example.test/audio.wav"))
			.andExpect(jsonPath("$.input.file_urls").doesNotExist())
			.andRespond(withSuccess("""
					{"output":{"task_id":"asr-task","task_status":"PENDING"}}
					""", MediaType.APPLICATION_JSON));
		this.server.expect(requestTo(BASE + "/api/v1/tasks/asr-task")).andExpect(method(HttpMethod.GET))
			.andRespond(withSuccess("""
					{"output":{"task_id":"asr-task","task_status":"FAILED"}}
					""", MediaType.APPLICATION_JSON));
		var api = DashScopeAudioTranscriptionApi.builder().baseUrl(BASE).apiKey(new SimpleApiKey("test-key"))
			.restClientBuilder(this.rest).build();
		var options = DashScopeAudioTranscriptionOptions.builder().model("qwen3-asr-flash-filetrans").build();
		var model = DashScopeAudioTranscriptionModel.builder().audioTranscriptionApi(api).defaultOptions(options).build();
		var prompt = new DashScopeAudioTranscriptionPrompt(options, List.of("https://example.test/audio.wav"));
		assertThatThrownBy(() -> model.call(prompt)).isInstanceOf(IllegalStateException.class).hasMessageContaining("asr-task");
		this.server.verify();
	}

	@Test
	void visionRerankPreservesMediaDocumentsAndInstruction() {
		this.server.expect(requestTo(BASE + "/api/v1/services/rerank/text-rerank/text-rerank"))
			.andExpect(jsonPath("$.model").value("qwen3-vl-rerank"))
			.andExpect(jsonPath("$.input.documents[1].image").value("https://example.test/reference.png"))
			.andExpect(jsonPath("$.parameters.instruct").value("Find related images"))
			.andRespond(withSuccess("""
					{"request_id":"rank-1","output":{"results":[{"index":1,"relevance_score":0.92}]}}
					""", MediaType.APPLICATION_JSON));
		var api = DashScopeApi.builder().baseUrl(BASE).apiKey("test-key").restClientBuilder(this.rest).build();
		var options = DashScopeRerankOptions.builder().model("qwen3-vl-rerank").instruct("Find related images").build();
		var model = new DashScopeRerankModel(api, options);
		var image = Document.builder().media(new Media(MimeTypeUtils.IMAGE_PNG,
				URI.create("https://example.test/reference.png"))).build();
		var response = model.call(new RerankRequest("red circle", List.of(new Document("A red square"), image)));
		assertThat(response.getResults()).singleElement().satisfies(result -> {
			assertThat(result.getOutput()).isSameAs(image);
			assertThat(result.getScore()).isEqualTo(0.92);
		});
		this.server.verify();
	}

}
