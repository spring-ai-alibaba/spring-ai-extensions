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

class DashScopeSdkImageModelTests {

	@Test
	void testImageCall() {
		DashScopeSdkImageModel model = DashScopeSdkImageModel.builder()
			.imageClient(new FakeImageClient())
			.defaultOptions(DashScopeSdkImageOptions.builder().model("wanx-v1").n(1).build())
			.apiKey("test-key")
			.build();

		ImageResponse response = model.call(new ImagePrompt("draw a cat"));

		assertThat(response.getResults()).hasSize(1);
		assertThat(response.getResult().getOutput().getUrl()).isEqualTo("https://example.com/image.png");
		assertThat((String) response.getMetadata().get("taskStatus")).isEqualTo("SUCCEEDED");
	}

	private static final class FakeImageClient implements DashScopeSdkImageSynthesisClient {

		@Override
		public ImageSynthesisResult call(ImageSynthesisParam request) {
			return successResult();
		}

		@Override
		public ImageSynthesisResult asyncCall(ImageSynthesisParam request) {
			ImageSynthesisResult result = instantiateImageSynthesisResult();
			ImageSynthesisOutput output = new ImageSynthesisOutput();
			output.setTaskId("task-1");
			output.setTaskStatus("RUNNING");
			result.setOutput(output);
			return result;
		}

		@Override
		public ImageSynthesisResult wait(ImageSynthesisResult request, String intervalMs) {
			return successResult();
		}

		private ImageSynthesisResult successResult() {
			ImageSynthesisResult result = instantiateImageSynthesisResult();
			result.setRequestId("req-1");
			ImageSynthesisOutput output = new ImageSynthesisOutput();
			output.setTaskId("task-1");
			output.setTaskStatus("SUCCEEDED");
			output.setResults(List.of(Map.of("url", "https://example.com/image.png")));
			result.setOutput(output);
			return result;
		}

		private ImageSynthesisResult instantiateImageSynthesisResult() {
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

}
