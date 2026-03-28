/*
 * Copyright 2026-2027 the original author or authors.
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
package com.alibaba.cloud.ai.dashscope.embedding.multimodal;

import java.net.URL;
import java.util.List;
import java.util.Map;

import com.alibaba.cloud.ai.dashscope.api.DashScopeMultimodalEmbeddingApi;
import com.alibaba.cloud.ai.dashscope.spec.DashScopeApiSpec;
import io.micrometer.observation.ObservationRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.ai.content.Media;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.DocumentEmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.embedding.EmbeddingResultMetadata;
import org.springframework.ai.retry.RetryUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MimeTypeUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DashScopeMultimodalEmbeddingModelTests {

	private static final String TEST_MODEL = "multimodal-embedding-one-peace-v1";

	private static final String TEST_REQUEST_ID = "test-request-id";

	private DashScopeMultimodalEmbeddingApi api;

	private DashScopeMultimodalEmbeddingModel embeddingModel;

	private DashScopeMultimodalEmbeddingOptions defaultOptions;

	@BeforeEach
	void setUp() {
		api = Mockito.mock(DashScopeMultimodalEmbeddingApi.class);
		defaultOptions = DashScopeMultimodalEmbeddingOptions.builder()
				.model(TEST_MODEL)
				.build();
		embeddingModel = DashScopeMultimodalEmbeddingModel.builder()
				.dashScopeMultimodalEmbeddingApi(api)
				.defaultOptions(defaultOptions)
				.build();
	}

	@Test
	void testBasicTextEmbedding() {
		float[] embeddingVector = {0.1f, 0.2f, 0.3f};
		DashScopeApiSpec.EmbeddingResult result = new DashScopeApiSpec.EmbeddingResult(0, embeddingVector, "text");
		DashScopeApiSpec.Output output = new DashScopeApiSpec.Output(List.of(result));
		DashScopeApiSpec.MultimodalEmbeddingUsage usage = new DashScopeApiSpec.MultimodalEmbeddingUsage(10, null, null, null);
		DashScopeApiSpec.MultimodalEmbeddingResponse apiResponse = new DashScopeApiSpec.MultimodalEmbeddingResponse(
				output, usage, TEST_REQUEST_ID, null, null);
		when(api.embedding(any())).thenReturn(ResponseEntity.ok(apiResponse));

		Document document = new Document("hello world");
		EmbeddingResponse response = embeddingModel.call(new DocumentEmbeddingRequest(List.of(document), null));

		assertThat(response.getResults()).hasSize(1);
		assertThat(response.getResults().get(0).getOutput()).containsExactly(embeddingVector);
		assertThat(response.getResults().get(0).getMetadata().getModalityType())
				.isEqualTo(EmbeddingResultMetadata.ModalityType.TEXT);
	}

	@Test
	void testImageEmbedding() {
		float[] embeddingVector = {0.4f, 0.5f, 0.6f};
		DashScopeApiSpec.EmbeddingResult result = new DashScopeApiSpec.EmbeddingResult(0, embeddingVector, "image");
		DashScopeApiSpec.Output output = new DashScopeApiSpec.Output(List.of(result));
		DashScopeApiSpec.MultimodalEmbeddingUsage usage = new DashScopeApiSpec.MultimodalEmbeddingUsage(5, 5, 1, null);
		DashScopeApiSpec.MultimodalEmbeddingResponse apiResponse = new DashScopeApiSpec.MultimodalEmbeddingResponse(
				output, usage, TEST_REQUEST_ID, null, null);
		when(api.embedding(any())).thenReturn(ResponseEntity.ok(apiResponse));

		byte[] imageData = new byte[]{(byte) 0x89, 0x50};
		Media media = Media.builder().mimeType(MimeTypeUtils.IMAGE_PNG).data(imageData).build();
		Document document = new Document(media, Map.of());
		EmbeddingResponse response = embeddingModel.call(new DocumentEmbeddingRequest(List.of(document), null));

		assertThat(response.getResults()).hasSize(1);
		assertThat(response.getResults().get(0).getOutput()).containsExactly(embeddingVector);
		assertThat(response.getResults().get(0).getMetadata().getModalityType())
				.isEqualTo(EmbeddingResultMetadata.ModalityType.IMAGE);
	}

	@Test
	void testVideoEmbedding() {
		float[] embeddingVector = {0.7f, 0.8f, 0.9f};
		DashScopeApiSpec.EmbeddingResult result = new DashScopeApiSpec.EmbeddingResult(0, embeddingVector, "video");
		DashScopeApiSpec.Output output = new DashScopeApiSpec.Output(List.of(result));
		DashScopeApiSpec.MultimodalEmbeddingUsage usage = new DashScopeApiSpec.MultimodalEmbeddingUsage(0, null, null, 10);
		DashScopeApiSpec.MultimodalEmbeddingResponse apiResponse = new DashScopeApiSpec.MultimodalEmbeddingResponse(
				output, usage, TEST_REQUEST_ID, null, null);
		when(api.embedding(any())).thenReturn(ResponseEntity.ok(apiResponse));

		Media videoMedia = Media.builder().mimeType(MimeTypeUtils.parseMimeType("video/mp4"))
				.data("https://example.com/video.mp4").build();
		Document document = new Document(videoMedia, Map.of());
		EmbeddingResponse response = embeddingModel.call(new DocumentEmbeddingRequest(List.of(document), null));

		assertThat(response.getResults()).hasSize(1);
		assertThat(response.getResults().get(0).getMetadata().getModalityType())
				.isEqualTo(EmbeddingResultMetadata.ModalityType.VIDEO);
	}

	@Test
	void testNullResponseBody() {
		when(api.embedding(any())).thenReturn(ResponseEntity.ok(null));

		Document document = new Document("test");
		EmbeddingResponse response = embeddingModel.call(new DocumentEmbeddingRequest(List.of(document), null));

		assertThat(response.getResults()).isEmpty();
	}

	@Test
	void testNullRequestHandling() {
		assertThatThrownBy(() -> embeddingModel.call(null))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("request must not be null");
	}

	@Test
	void testEmptyInstructionsHandling() {
		assertThatThrownBy(() -> embeddingModel.call(new DocumentEmbeddingRequest(List.of(), null)))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("must not be empty");
	}

	@Test
	void testCustomOptionsOverride() {
		float[] embeddingVector = {0.1f, 0.2f, 0.3f};
		DashScopeApiSpec.EmbeddingResult result = new DashScopeApiSpec.EmbeddingResult(0, embeddingVector, "text");
		DashScopeApiSpec.Output output = new DashScopeApiSpec.Output(List.of(result));
		DashScopeApiSpec.MultimodalEmbeddingUsage usage = new DashScopeApiSpec.MultimodalEmbeddingUsage(10, null, null, null);
		DashScopeApiSpec.MultimodalEmbeddingResponse apiResponse = new DashScopeApiSpec.MultimodalEmbeddingResponse(
				output, usage, TEST_REQUEST_ID, null, null);
		when(api.embedding(any())).thenReturn(ResponseEntity.ok(apiResponse));

		DashScopeMultimodalEmbeddingOptions customOptions = DashScopeMultimodalEmbeddingOptions.builder()
				.model("custom-model")
				.dimensions(512)
				.build();

		Document document = new Document("test text");
		EmbeddingResponse response = embeddingModel.call(new DocumentEmbeddingRequest(List.of(document), customOptions));

		assertThat(response.getResults()).hasSize(1);
		assertThat(response.getResults().get(0).getOutput()).containsExactly(embeddingVector);
		verify(api, times(1)).embedding(any());
	}

	@Test
	void testUnsupportedMediaType() {
		Media media = Media.builder().mimeType(MimeTypeUtils.parseMimeType("audio/mp3"))
				.data("https://example.com/audio.mp3").build();
		Document document = new Document(media, Map.of());

		assertThatThrownBy(() -> embeddingModel.call(new DocumentEmbeddingRequest(List.of(document), null)))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("Unsupported media type");
	}

	@Test
	void testUnsupportedImageMimeSubType() {
		Media media = Media.builder().mimeType(MimeTypeUtils.parseMimeType("image/svg+xml"))
				.data("https://example.com/image.svg").build();
		Document document = new Document(media, Map.of());

		assertThatThrownBy(() -> embeddingModel.call(new DocumentEmbeddingRequest(List.of(document), null)))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("Unsupported image mime type");
	}

	@Test
	void testVideoWithNonUrlData() {
		// Video media with byte[] data should throw — byte[] is not supported for video
		byte[] videoBytes = new byte[]{0x00, 0x01};
		Media videoMedia = Media.builder().mimeType(MimeTypeUtils.parseMimeType("video/mp4")).data(videoBytes).build();
		Document document = new Document(videoMedia, Map.of());

		assertThatThrownBy(() -> embeddingModel.call(new DocumentEmbeddingRequest(List.of(document), null)))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("Unsupported media data type");
	}

	@Test
	void testBuilderAndMutateClone() {
		DashScopeMultimodalEmbeddingModel model = DashScopeMultimodalEmbeddingModel.builder()
				.dashScopeMultimodalEmbeddingApi(api)
				.defaultOptions(defaultOptions)
				.retryTemplate(RetryUtils.DEFAULT_RETRY_TEMPLATE)
				.observationRegistry(ObservationRegistry.NOOP)
				.build();

		assertThat(model).isNotNull();

		// Test mutate
		DashScopeMultimodalEmbeddingModel.Builder mutatedBuilder = model.mutate();
		assertThat(mutatedBuilder).isNotNull();

		DashScopeMultimodalEmbeddingModel mutatedModel = mutatedBuilder.build();
		assertThat(mutatedModel).isNotNull();

		// Test clone
		DashScopeMultimodalEmbeddingModel clonedModel = model.clone();
		assertThat(clonedModel).isNotNull();
	}

	@Test
	void testDimensions() {
		// With custom dimensions set
		DashScopeMultimodalEmbeddingOptions optionsWithDim = DashScopeMultimodalEmbeddingOptions.builder()
				.model(TEST_MODEL)
				.dimensions(512)
				.build();
		DashScopeMultimodalEmbeddingModel modelWithDim = DashScopeMultimodalEmbeddingModel.builder()
				.dashScopeMultimodalEmbeddingApi(api)
				.defaultOptions(optionsWithDim)
				.build();
		assertThat(modelWithDim.dimensions()).isEqualTo(512);

		// Without dimensions → default 1024
		DashScopeMultimodalEmbeddingModel modelNoDim = DashScopeMultimodalEmbeddingModel.builder()
				.dashScopeMultimodalEmbeddingApi(api)
				.defaultOptions(DashScopeMultimodalEmbeddingOptions.builder().model(TEST_MODEL).build())
				.build();
		assertThat(modelNoDim.dimensions()).isEqualTo(1024);
	}

	@Test
	void testRetryTemplateIsUsed() {
		float[] embeddingVector = {0.1f, 0.2f, 0.3f};
		DashScopeApiSpec.EmbeddingResult result = new DashScopeApiSpec.EmbeddingResult(0, embeddingVector, "text");
		DashScopeApiSpec.Output output = new DashScopeApiSpec.Output(List.of(result));
		DashScopeApiSpec.MultimodalEmbeddingUsage usage = new DashScopeApiSpec.MultimodalEmbeddingUsage(10, null, null, null);
		DashScopeApiSpec.MultimodalEmbeddingResponse apiResponse = new DashScopeApiSpec.MultimodalEmbeddingResponse(
				output, usage, TEST_REQUEST_ID, null, null);
		when(api.embedding(any())).thenReturn(ResponseEntity.ok(apiResponse));

		Document document = new Document("test");
		embeddingModel.call(new DocumentEmbeddingRequest(List.of(document), null));

		// Verify the API was actually called, proving retryTemplate.execute() invoked the callback
		verify(api, times(1)).embedding(any());
	}

	@Test
	void testUsageMetadata() {
		float[] embeddingVector = {0.1f, 0.2f, 0.3f};
		DashScopeApiSpec.EmbeddingResult result = new DashScopeApiSpec.EmbeddingResult(0, embeddingVector, "text");
		DashScopeApiSpec.Output output = new DashScopeApiSpec.Output(List.of(result));
		DashScopeApiSpec.MultimodalEmbeddingUsage usage = new DashScopeApiSpec.MultimodalEmbeddingUsage(42, null, null, null);
		DashScopeApiSpec.MultimodalEmbeddingResponse apiResponse = new DashScopeApiSpec.MultimodalEmbeddingResponse(
				output, usage, TEST_REQUEST_ID, null, null);
		when(api.embedding(any())).thenReturn(ResponseEntity.ok(apiResponse));

		Document document = new Document("test");
		EmbeddingResponse response = embeddingModel.call(new DocumentEmbeddingRequest(List.of(document), null));

		assertThat(response.getMetadata()).isNotNull();
		assertThat(response.getMetadata().getModel()).isEqualTo(TEST_MODEL);
		assertThat(response.getMetadata().getUsage()).isNotNull();
		assertThat(response.getMetadata().getUsage().getPromptTokens()).isEqualTo(42);
	}

	@Test
	void testNullUsageHandling() {
		float[] embeddingVector = {0.1f, 0.2f, 0.3f};
		DashScopeApiSpec.EmbeddingResult result = new DashScopeApiSpec.EmbeddingResult(0, embeddingVector, "text");
		DashScopeApiSpec.Output output = new DashScopeApiSpec.Output(List.of(result));
		// null usage
		DashScopeApiSpec.MultimodalEmbeddingResponse apiResponse = new DashScopeApiSpec.MultimodalEmbeddingResponse(
				output, null, TEST_REQUEST_ID, null, null);
		when(api.embedding(any())).thenReturn(ResponseEntity.ok(apiResponse));

		Document document = new Document("test");
		EmbeddingResponse response = embeddingModel.call(new DocumentEmbeddingRequest(List.of(document), null));

		assertThat(response.getMetadata().getUsage().getPromptTokens()).isZero();
		assertThat(response.getMetadata().getUsage().getTotalTokens()).isZero();
	}

	@Test
	void testBatchMultimodalEmbedding() {
		float[] textVector = {0.1f, 0.2f};
		float[] imageVector = {0.3f, 0.4f};
		DashScopeApiSpec.EmbeddingResult textResult = new DashScopeApiSpec.EmbeddingResult(0, textVector, "text");
		DashScopeApiSpec.EmbeddingResult imageResult = new DashScopeApiSpec.EmbeddingResult(1, imageVector, "image");
		DashScopeApiSpec.Output output = new DashScopeApiSpec.Output(List.of(textResult, imageResult));
		DashScopeApiSpec.MultimodalEmbeddingUsage usage = new DashScopeApiSpec.MultimodalEmbeddingUsage(15, 5, 1, null);
		DashScopeApiSpec.MultimodalEmbeddingResponse apiResponse = new DashScopeApiSpec.MultimodalEmbeddingResponse(
				output, usage, TEST_REQUEST_ID, null, null);
		when(api.embedding(any())).thenReturn(ResponseEntity.ok(apiResponse));

		Document textDoc = new Document("hello");
		Media imageMedia = Media.builder().mimeType(MimeTypeUtils.IMAGE_JPEG)
				.data("https://example.com/image.jpg").build();
		Document imageDoc = new Document(imageMedia, Map.of());

		EmbeddingResponse response = embeddingModel.call(new DocumentEmbeddingRequest(List.of(textDoc, imageDoc), null));

		assertThat(response.getResults()).hasSize(2);
		assertThat(response.getResults().get(0).getMetadata().getModalityType())
				.isEqualTo(EmbeddingResultMetadata.ModalityType.TEXT);
		assertThat(response.getResults().get(1).getMetadata().getModalityType())
				.isEqualTo(EmbeddingResultMetadata.ModalityType.IMAGE);
	}



	@Test
	void testVideoWithUrl() throws Exception {
		float[] embeddingVector = {0.5f, 0.6f};
		DashScopeApiSpec.EmbeddingResult result = new DashScopeApiSpec.EmbeddingResult(0, embeddingVector, "video");
		DashScopeApiSpec.Output output = new DashScopeApiSpec.Output(List.of(result));
		DashScopeApiSpec.MultimodalEmbeddingUsage usage = new DashScopeApiSpec.MultimodalEmbeddingUsage(0, null, null, 10);
		DashScopeApiSpec.MultimodalEmbeddingResponse apiResponse = new DashScopeApiSpec.MultimodalEmbeddingResponse(
				output, usage, TEST_REQUEST_ID, null, null);
		when(api.embedding(any())).thenReturn(ResponseEntity.ok(apiResponse));

		URL videoUrl = new URL("https://help-static-aliyun-doc.aliyuncs.com/file-manage-files/zh-CN/20250107/lbcemt/new+video.mp4");
		Media media = new Media(MimeTypeUtils.parseMimeType("video/mp4"), videoUrl.toURI());
		Document document = new Document(media, Map.of());
		EmbeddingResponse response = embeddingModel.call(new DocumentEmbeddingRequest(List.of(document), null));

		assertThat(response.getResults()).hasSize(1);
		assertThat(response.getResults().get(0).getMetadata().getModalityType())
				.isEqualTo(EmbeddingResultMetadata.ModalityType.VIDEO);
	}

	@Test
	void testImageWithJavaNetUrl() throws Exception {
		float[] embeddingVector = {0.7f, 0.8f};
		DashScopeApiSpec.EmbeddingResult result = new DashScopeApiSpec.EmbeddingResult(0, embeddingVector, "image");
		DashScopeApiSpec.Output output = new DashScopeApiSpec.Output(List.of(result));
		DashScopeApiSpec.MultimodalEmbeddingUsage usage = new DashScopeApiSpec.MultimodalEmbeddingUsage(5, null, null, null);
		DashScopeApiSpec.MultimodalEmbeddingResponse apiResponse = new DashScopeApiSpec.MultimodalEmbeddingResponse(
				output, usage, TEST_REQUEST_ID, null, null);
		when(api.embedding(any())).thenReturn(ResponseEntity.ok(apiResponse));

		URL imageUrl = new URL("https://img.alicdn.com/imgextra/i3/O1CN01rdstgY1uiZWt8gqSL_!!6000000006071-0-tps-1970-356.jpg");
		Media media = new Media(MimeTypeUtils.IMAGE_JPEG, imageUrl.toURI());
		Document document = new Document(media, Map.of());
		EmbeddingResponse response = embeddingModel.call(new DocumentEmbeddingRequest(List.of(document), null));

		assertThat(response.getResults()).hasSize(1);
		assertThat(response.getResults().get(0).getMetadata().getModalityType())
				.isEqualTo(EmbeddingResultMetadata.ModalityType.IMAGE);
	}

}
