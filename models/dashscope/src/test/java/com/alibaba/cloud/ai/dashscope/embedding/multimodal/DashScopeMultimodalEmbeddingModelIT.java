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

import com.alibaba.cloud.ai.dashscope.api.DashScopeMultimodalEmbeddingApi;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.ai.content.Media;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.DocumentEmbeddingRequest;
import org.springframework.ai.embedding.Embedding;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.embedding.EmbeddingResultMetadata;
import org.springframework.ai.model.SimpleApiKey;
import org.springframework.util.MimeTypeUtils;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@EnabledIfEnvironmentVariable(named = "AI_DASHSCOPE_API_KEY", matches = ".+")
public class DashScopeMultimodalEmbeddingModelIT {

	private final DashScopeMultimodalEmbeddingModel embeddingModel;

	public DashScopeMultimodalEmbeddingModelIT() {
		DashScopeMultimodalEmbeddingApi api = DashScopeMultimodalEmbeddingApi.builder()
				.apiKey(new SimpleApiKey(System.getenv("AI_DASHSCOPE_API_KEY")))
				.build();
		DashScopeMultimodalEmbeddingOptions options = DashScopeMultimodalEmbeddingOptions.builder()
				.model(DashScopeMultimodalEmbeddingApi.DEFAULT_MULTIMODAL_EMBEDDING_MODEL)
				.build();
		this.embeddingModel = new DashScopeMultimodalEmbeddingModel(api, options);
	}

	@Test
	public void testTextEmbedding() {
		Document document = new Document("hello world");
		EmbeddingResponse response = this.embeddingModel.call(new DocumentEmbeddingRequest(List.of(document), null));
		verifyEmbeddingResponse(response, 1);
		assertThat(response.getResults().get(0).getMetadata().getModalityType()).isEqualTo(EmbeddingResultMetadata.ModalityType.TEXT);
	}

	@Test
	public void testImageEmbeddingBase64() {
		// A 1x1 transparent png in base64
		byte[] imageData = java.util.Base64.getDecoder().decode("iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mNkYAAAAAYAAjCB0C8AAAAASUVORK5CYII=");
		Media media = Media.builder().mimeType(MimeTypeUtils.IMAGE_PNG).data(imageData).build();
		Document document = new Document(media, Map.of());

		EmbeddingResponse response = this.embeddingModel.call(new DocumentEmbeddingRequest(List.of(document), null));
		verifyEmbeddingResponse(response, 1);
		assertThat(response.getResults().get(0).getMetadata().getModalityType()).isEqualTo(EmbeddingResultMetadata.ModalityType.IMAGE);
	}

	@Test
	public void testImageEmbeddingUrl() throws Exception {
		Media media = Media.builder().mimeType(MimeTypeUtils.IMAGE_JPEG).data("https://img.alicdn.com/imgextra/i3/O1CN01rdstgY1uiZWt8gqSL_!!6000000006071-0-tps-1970-356.jpg").build();
		Document document = new Document(media, Map.of());

		EmbeddingResponse response = this.embeddingModel.call(new DocumentEmbeddingRequest(List.of(document), null));
		verifyEmbeddingResponse(response, 1);
		assertThat(response.getResults().get(0).getMetadata().getModalityType()).isEqualTo(EmbeddingResultMetadata.ModalityType.IMAGE);
	}

	@Test
	public void testBatchMultimodalEmbedding() {
		Document textDoc = new Document("multimodal vector model");
		
		Media imageMedia = Media.builder().mimeType(MimeTypeUtils.IMAGE_JPEG).data("https://img.alicdn.com/imgextra/i3/O1CN01rdstgY1uiZWt8gqSL_!!6000000006071-0-tps-1970-356.jpg").build();
		Document imageDoc = new Document(imageMedia, Map.of());
		
		EmbeddingResponse response = this.embeddingModel.call(new DocumentEmbeddingRequest(List.of(textDoc, imageDoc), null));

		// Expecting 2 results in a single call.
		verifyEmbeddingResponse(response, 2);
		
		int textCount = 0;
		int imageCount = 0;
		for(Embedding e : response.getResults()) {
			if (e.getMetadata().getModalityType() == EmbeddingResultMetadata.ModalityType.TEXT) textCount++;
			if (e.getMetadata().getModalityType() == EmbeddingResultMetadata.ModalityType.IMAGE) imageCount++;
		}
		assertThat(textCount).isEqualTo(1);
		assertThat(imageCount).isEqualTo(1);
	}

	@Test
	public void testVideoEmbedding() {
		Media videoMedia = Media.builder().mimeType(MimeTypeUtils.parseMimeType("video/mp4")).data("https://help-static-aliyun-doc.aliyuncs.com/file-manage-files/zh-CN/20250107/lbcemt/new+video.mp4").build();
		Document videoDoc = new Document(videoMedia, Map.of());

		EmbeddingResponse response = this.embeddingModel.call(new DocumentEmbeddingRequest(List.of(videoDoc), null));
		verifyEmbeddingResponse(response, 1);
		assertThat(response.getResults().get(0).getMetadata().getModalityType()).isEqualTo(EmbeddingResultMetadata.ModalityType.VIDEO);
	}

	private void verifyEmbeddingResponse(EmbeddingResponse response, int expectedSize) {
		assertThat(response).isNotNull();
		assertThat(response.getResults()).hasSize(expectedSize);
		response.getResults().forEach(result -> {
			assertThat(result.getOutput()).isNotEmpty();
			// Default tongyi-embedding-vision-plus dimension is 1024 or higher
			assertThat(result.getOutput().length).isGreaterThan(0);
		});
		// test usage
		assertThat(response.getMetadata()).isNotNull();
		assertThat(response.getMetadata().getUsage()).isNotNull();
		assertThat(response.getMetadata().getUsage().getPromptTokens()).isGreaterThanOrEqualTo(0);
	}

}
