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

package com.alibaba.cloud.ai.dashscope.sdk.image;

import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesisOutput;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesisParam;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesisResult;
import org.junit.jupiter.api.Test;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DashScopeSdkImageModelTests {

	@Test
	void testImageCall() {
		FakeImageClient client = new FakeImageClient();
		DashScopeSdkImageModel model = DashScopeSdkImageModel.builder()
			.imageClient(client)
			.defaultOptions(DashScopeSdkImageOptions.builder().model("wanx-v1").n(1).build())
			.apiKey("test-key")
			.build();

		ImageResponse response = model.call(new ImagePrompt("draw a cat"));

		assertThat(response.getResults()).hasSize(1);
		assertThat(response.getResult().getOutput().getUrl()).isEqualTo("https://example.com/image.png");
		assertThat((String) response.getMetadata().get("taskStatus")).isEqualTo("SUCCEEDED");
		assertThat(client.asyncCallCount).isEqualTo(1);
		assertThat(client.waitCount).isEqualTo(1);
	}

	@Test
	void testSyncModeUsesCallPathWithoutWait() {
		FakeImageClient client = new FakeImageClient();
		DashScopeSdkImageModel model = DashScopeSdkImageModel.builder()
			.imageClient(client)
			.defaultOptions(DashScopeSdkImageOptions.builder().model("wanx-v1").n(1).async(true).build())
			.build();

		ImageResponse response = model
			.call(new ImagePrompt("draw a cat", DashScopeSdkImageOptions.builder().async(false).build()));

		assertThat(response.getResults()).hasSize(1);
		assertThat(client.callCount).isEqualTo(1);
		assertThat(client.asyncCallCount).isZero();
		assertThat(client.waitCount).isZero();
	}

	@Test
	void testAsyncTerminalStatusSkipsWait() {
		FakeImageClient client = new FakeImageClient();
		client.asyncResult = resultWithStatus("SUCCEEDED");
		DashScopeSdkImageModel model = DashScopeSdkImageModel.builder()
			.imageClient(client)
			.defaultOptions(DashScopeSdkImageOptions.builder().model("wanx-v1").build())
			.build();

		model.call(new ImagePrompt("draw a cat"));

		assertThat(client.asyncCallCount).isEqualTo(1);
		assertThat(client.waitCount).isZero();
	}

	@Test
	void testWaitFailureIsWrapped() {
		FakeImageClient client = new FakeImageClient();
		client.asyncResult = resultWithStatus("RUNNING");
		client.throwOnWait = new RuntimeException("wait failed");
		DashScopeSdkImageModel model = DashScopeSdkImageModel.builder()
			.imageClient(client)
			.defaultOptions(DashScopeSdkImageOptions.builder().model("wanx-v1").pollIntervalMs(1500).build())
			.build();

		assertThatThrownBy(
				() -> model.call(new ImagePrompt("draw a cat", DashScopeSdkImageOptions.builder().pollIntervalMs(1500).build())))
			.isInstanceOf(com.alibaba.cloud.ai.dashscope.sdk.common.DashScopeSdkException.class)
			.hasMessageContaining("Failed to fetch DashScope SDK image task result")
			.hasCause(client.throwOnWait);
		assertThat(client.lastWaitInterval).isEqualTo("1500");
	}

	@Test
	void testSubmitFailureIsWrapped() {
		FakeImageClient client = new FakeImageClient();
		client.throwOnAsyncCall = new RuntimeException("submit failed");
		DashScopeSdkImageModel model = DashScopeSdkImageModel.builder()
			.imageClient(client)
			.defaultOptions(DashScopeSdkImageOptions.builder().model("wanx-v1").build())
			.build();

		assertThatThrownBy(() -> model.call(new ImagePrompt("draw a cat")))
			.isInstanceOf(com.alibaba.cloud.ai.dashscope.sdk.common.DashScopeSdkException.class)
			.hasMessageContaining("Failed to submit DashScope SDK image request")
			.hasCause(client.throwOnAsyncCall);
	}

	@Test
	void testRequestMergesHeadersAndParameters() {
		FakeImageClient client = new FakeImageClient();
		DashScopeSdkImageModel model = DashScopeSdkImageModel.builder()
			.imageClient(client)
			.defaultOptions(DashScopeSdkImageOptions.builder()
				.model("wanx-v1")
				.httpHeaders(Map.of("x-default", "d", "x-override", "default"))
				.responseFormat("url")
				.extraBody(Map.of("watermark", false))
				.build())
			.apiKey("api-key")
			.workspaceId("workspace-id")
			.connectionHeaders(Map.of("x-conn", "c", "x-override", "conn"))
			.build();

		model.call(new ImagePrompt("draw a cat", DashScopeSdkImageOptions.builder()
			.httpHeaders(Map.of("x-runtime", "r", "x-override", "runtime"))
			.responseFormat("b64_json")
			.extraBody(Map.of("seed", 7))
			.build()));

		assertThat(client.lastRequest.getHeaders()).containsEntry("x-conn", "c")
			.containsEntry("x-runtime", "r")
			.containsEntry("x-override", "runtime");
		assertThat(client.lastRequest.getParameters()).containsEntry("response_format", "b64_json")
			.containsEntry("seed", 7);
	}

	@Test
	void testMapsImageUrlAndBase64AndSkipsInvalidItems() {
		FakeImageClient client = new FakeImageClient();
		client.callResult = imageResult(List.of(
				Map.of("image_url", "https://example.com/from-image-url.png"),
				Map.of("image", "base64-data"),
				Map.of()));
		DashScopeSdkImageModel model = DashScopeSdkImageModel.builder()
			.imageClient(client)
			.defaultOptions(DashScopeSdkImageOptions.builder().model("wanx-v1").async(false).build())
			.build();

		ImageResponse response = model.call(new ImagePrompt("draw a cat", DashScopeSdkImageOptions.builder().async(false).build()));

		assertThat(response.getResults()).hasSize(2);
		assertThat(response.getResults().get(0).getOutput().getUrl()).isEqualTo("https://example.com/from-image-url.png");
		assertThat(response.getResults().get(1).getOutput().getB64Json()).isEqualTo("base64-data");
	}

	@Test
	void testNullResultReturnsEmptyResponse() {
		FakeImageClient client = new FakeImageClient();
		client.returnNullCallResult = true;
		DashScopeSdkImageModel model = DashScopeSdkImageModel.builder()
			.imageClient(client)
			.defaultOptions(DashScopeSdkImageOptions.builder().model("wanx-v1").async(false).build())
			.build();

		ImageResponse response = model.call(new ImagePrompt("draw a cat", DashScopeSdkImageOptions.builder().async(false).build()));
		assertThat(response.getResults()).isEmpty();
	}

	private static final class FakeImageClient implements DashScopeSdkImageSynthesisClient {

		private ImageSynthesisResult callResult;

		private boolean returnNullCallResult;

		private ImageSynthesisResult asyncResult;

		private ImageSynthesisResult waitResult;

		private RuntimeException throwOnCall;

		private RuntimeException throwOnAsyncCall;

		private RuntimeException throwOnWait;

		private int callCount;

		private int asyncCallCount;

		private int waitCount;

		private ImageSynthesisParam lastRequest;

		private String lastWaitInterval;

		@Override
		public ImageSynthesisResult call(ImageSynthesisParam request) {
			this.callCount++;
			this.lastRequest = request;
			if (this.throwOnCall != null) {
				throw this.throwOnCall;
			}
			if (this.returnNullCallResult) {
				return null;
			}
			return this.callResult == null ? successResult() : this.callResult;
		}

		@Override
		public ImageSynthesisResult asyncCall(ImageSynthesisParam request) {
			this.asyncCallCount++;
			this.lastRequest = request;
			if (this.throwOnAsyncCall != null) {
				throw this.throwOnAsyncCall;
			}
			if (this.asyncResult != null) {
				return this.asyncResult;
			}
			return resultWithStatus("RUNNING");
		}

		@Override
		public ImageSynthesisResult wait(ImageSynthesisResult request, String intervalMs) {
			this.waitCount++;
			this.lastWaitInterval = intervalMs;
			if (this.throwOnWait != null) {
				throw this.throwOnWait;
			}
			return this.waitResult == null ? successResult() : this.waitResult;
		}

		private ImageSynthesisResult successResult() {
			return imageResult(List.of(Map.of("url", "https://example.com/image.png")));
		}

	}

	private static ImageSynthesisResult resultWithStatus(String status) {
		ImageSynthesisResult result = instantiateImageSynthesisResult();
		ImageSynthesisOutput output = new ImageSynthesisOutput();
		output.setTaskId("task-1");
		output.setTaskStatus(status);
		result.setOutput(output);
		return result;
	}

	private static ImageSynthesisResult imageResult(List<Map<String, String>> images) {
		ImageSynthesisResult result = instantiateImageSynthesisResult();
		result.setRequestId("req-1");
		ImageSynthesisOutput output = new ImageSynthesisOutput();
		output.setTaskId("task-1");
		output.setTaskStatus("SUCCEEDED");
		output.setResults(images);
		result.setOutput(output);
		return result;
	}

	private static ImageSynthesisResult instantiateImageSynthesisResult() {
		try {
			var constructor = ImageSynthesisResult.class.getDeclaredConstructor();
			constructor.setAccessible(true);
			return constructor.newInstance();
		}
		catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

}
