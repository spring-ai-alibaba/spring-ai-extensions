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
package com.alibaba.cloud.ai.dashscope.image;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.alibaba.cloud.ai.dashscope.api.DashScopeImageApi;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageApiSpec.ImageResponse.Output;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageApiSpec.ImageResponse.Result;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageApiSpec.ImageResponse.Usage;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageModel.Builder;
import io.micrometer.observation.ObservationRegistry;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.retry.RetryUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.support.RetryTemplate;

/**
 * Test cases for DashScopeImageModel. Tests cover basic image generation, custom options,
 * async task handling, error handling, and edge cases.
 *
 * @author yuluo
 * @author polaris
 * @author brianxiadong
 * @since 1.0.0-M5.1
 */
class DashScopeImageModelTests {

	// Test constants
	private static final String TEST_MODEL = "wanx-v1";

	private static final String TEST_TASK_ID = "test-task-id";

	private static final String TEST_REQUEST_ID = "test-request-id";

	private static final String TEST_IMAGE_URL = "https://example.com/image.jpg";

	private static final String TEST_PROMPT = "A beautiful sunset over mountains";

	private DashScopeImageApi dashScopeImageApi;

	private DashScopeImageModel imageModel;

	private DashScopeImageOptions defaultOptions;

	@BeforeEach
	void setUp() {
		// Initialize mock objects and test instances
		dashScopeImageApi = Mockito.mock(DashScopeImageApi.class);
		defaultOptions = DashScopeImageOptions.builder().model(TEST_MODEL).n(1).build();
		imageModel = new DashScopeImageModel(dashScopeImageApi, defaultOptions, RetryTemplate.builder().build(),
				ObservationRegistry.NOOP);
	}

	@Test
	void testBasicImageGeneration() {
		// Test basic image generation with successful response
		mockSuccessfulImageGeneration();

		ImagePrompt prompt = new ImagePrompt(TEST_PROMPT);
		ImageResponse response = imageModel.call(prompt);

		assertThat(response.getResults()).hasSize(1);
		assertThat(response.getResult().getOutput().getUrl()).isEqualTo(TEST_IMAGE_URL);
	}

	@Test
	void testCustomOptions() {
		// Test image generation with custom options
		mockSuccessfulImageGeneration();

		DashScopeImageOptions customOptions = DashScopeImageOptions.builder()
			.model(TEST_MODEL)
			.n(2)
			.width(1024)
			.height(1024)
			.style("photography")
			.seed(42)
			.build();

		ImagePrompt prompt = new ImagePrompt(TEST_PROMPT, customOptions);
		ImageResponse response = imageModel.call(prompt);

		assertThat(response.getResults()).hasSize(1);
		assertThat(response.getResult().getOutput().getUrl()).isEqualTo(TEST_IMAGE_URL);
	}

	@Test
	void testFailedImageGeneration() {
		// Test handling of failed image generation
		mockFailedImageGeneration();

		ImagePrompt prompt = new ImagePrompt(TEST_PROMPT);
		ImageResponse response = imageModel.call(prompt);

		assertThat(response.getResults()).isEmpty();
	}

	@Test
	void testNullResponse() {
		// Test handling of null API response
		when(dashScopeImageApi.submitImageGenTask(any(), any())).thenReturn(null);

		ImagePrompt prompt = new ImagePrompt(TEST_PROMPT);
		ImageResponse response = imageModel.call(prompt);

		assertThat(response.getResults()).isEmpty();
	}

	@Test
	void testNullPrompt() {
		// Test handling of null prompt
		assertThatThrownBy(() -> imageModel.call(null)).isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("Prompt");
	}

	@Test
	void testEmptyPrompt() {
		// Test handling of empty prompt
		assertThatThrownBy(() -> imageModel.call(new ImagePrompt(new ArrayList<>())))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("Prompt");
	}

	private void mockSuccessfulImageGeneration() {
		// Mock successful task submission (PENDING)
		Output pendingOutput = Output.builder()
				.taskId(TEST_TASK_ID).taskStatus("PENDING").build();
		DashScopeImageApiSpec.ImageResponse submitResponse =
				new DashScopeImageApiSpec.ImageResponse(TEST_REQUEST_ID, pendingOutput, new Usage(1));
		when(dashScopeImageApi.submitImageGenTask(any(), any())).thenReturn(ResponseEntity.ok(submitResponse));

		// Mock successful task completion (SUCCEEDED)
		Output completedOutput = Output.builder()
				.taskId(TEST_TASK_ID)
				.taskStatus("SUCCEEDED")
				.results(List.of(new Result(TEST_IMAGE_URL, null, null)))
				.build();
		DashScopeImageApiSpec.ImageResponse completedResponse =
				new DashScopeImageApiSpec.ImageResponse(TEST_REQUEST_ID, completedOutput, new Usage(1));
		when(dashScopeImageApi.getImageGenTaskResult(any(), any()))
				.thenReturn(ResponseEntity.ok(completedResponse));
	}

	private void mockFailedImageGeneration() {
		// Mock successful task submission but failed completion
		Output pendingOutput = Output.builder()
				.taskId(TEST_TASK_ID).taskStatus("PENDING").build();
		DashScopeImageApiSpec.ImageResponse submitResponse =
				new DashScopeImageApiSpec.ImageResponse(TEST_REQUEST_ID, pendingOutput, new Usage(1));
		when(dashScopeImageApi.submitImageGenTask(any(), any())).thenReturn(ResponseEntity.ok(submitResponse));

		// Mock failed task completion
		Output failedOutput = Output.builder()
				.taskId(TEST_TASK_ID)
				.taskStatus("FAILED")
				.code("ERROR_CODE")
				.message("Error message")
				.build();
		DashScopeImageApiSpec.ImageResponse failedResponse =
				new DashScopeImageApiSpec.ImageResponse(TEST_REQUEST_ID, failedOutput, new Usage(1));
		when(dashScopeImageApi.getImageGenTaskResult(any(), any()))
				.thenReturn(ResponseEntity.ok(failedResponse));
	}

	@Test
	void testImageGenerationTimeout() {
		mockTimeoutImageGeneration();
		DashScopeImageModel shortPollModel = DashScopeImageModel.builder()
				.dashScopeApi(dashScopeImageApi)
				.defaultOptions(defaultOptions)
				.retryTemplate(RetryTemplate.builder().build())
				.observationRegistry(ObservationRegistry.NOOP)
				.pollIntervalMs(20)
				.pollTimeoutMs(80)
				.build();

		ImagePrompt prompt = new ImagePrompt(TEST_PROMPT);
		ImageResponse response = shortPollModel.call(prompt);

		assertThat(response.getResults()).isEmpty();
		Object status = response.getMetadata().get("taskStatus");
		Object taskIdMeta = response.getMetadata().get("taskId");
		assertThat(status).isEqualTo("TIMED_OUT");
		assertThat(taskIdMeta).isEqualTo(TEST_TASK_ID);
	}

	private void mockTimeoutImageGeneration() {
		// Mock successful task submission but pending status until timeout
		Output pendingOutput = Output.builder()
				.taskId(TEST_TASK_ID).taskStatus("PENDING").build();
		DashScopeImageApiSpec.ImageResponse submitResponse =
				new DashScopeImageApiSpec.ImageResponse(TEST_REQUEST_ID, pendingOutput, new Usage(1));
		when(dashScopeImageApi.submitImageGenTask(any(), any())).thenReturn(ResponseEntity.ok(submitResponse));

		// Mock pending status for all status checks
		DashScopeImageApiSpec.ImageResponse pendingResponse =
				new DashScopeImageApiSpec.ImageResponse(TEST_REQUEST_ID, pendingOutput, new Usage(1));
		when(dashScopeImageApi.getImageGenTaskResult(any(), any()))
				.thenReturn(ResponseEntity.ok(pendingResponse));
	}

	@Test
	void testBuilder() {
		DashScopeImageModel model1 = DashScopeImageModel.builder()
				.dashScopeApi(dashScopeImageApi)
				.build();
		DashScopeImageModel model2 = DashScopeImageModel.builder()
			.dashScopeApi(dashScopeImageApi)
			.defaultOptions(defaultOptions)
			.retryTemplate(RetryUtils.DEFAULT_RETRY_TEMPLATE)
			.observationRegistry(ObservationRegistry.NOOP)
			.build();

		DashScopeImageModel clone1 = model1.clone();
		DashScopeImageModel clone2 = model2.clone();

		Builder mutate1 = model1.mutate();
		Builder mutate2 = model2.mutate();

		assertThat(model1).isNotNull();
		assertThat(model2).isNotNull();
		assertThat(clone1).isNotNull();
		assertThat(clone2).isNotNull();
		assertThat(mutate1).isNotNull();
		assertThat(mutate2).isNotNull();
	}

	@Test
	void testOutPaintingImageGeneration() {
		// Test out-painting with ratio-based expansion
		DashScopeImageOptions outPaintingOptions = DashScopeImageOptions.builder()
				.model("image-out-painting")
				.baseImageUrl("https://example.com/original.jpg")
				.outputRatio("4:3")
				.bestQuality(false)
				.limitImageSize(true)
				.build();

		mockSuccessfulImageGeneration();

		ImagePrompt prompt = new ImagePrompt("expand this image", outPaintingOptions);
		ImageResponse response = imageModel.call(prompt);

		assertThat(response.getResults()).hasSize(1);
		assertThat(response.getResult().getOutput().getUrl()).isEqualTo(TEST_IMAGE_URL);
	}

	@Test
	void testOutPaintingWithScaleParameters() {
		// Test out-painting with scale-based expansion
		DashScopeImageOptions outPaintingOptions = DashScopeImageOptions.builder()
				.model("image-out-painting")
				.baseImageUrl("https://example.com/original.jpg")
				.xScale(1.5f)
				.yScale(1.5f)
				.angle(90)
				.build();

		mockSuccessfulImageGeneration();

		ImagePrompt prompt = new ImagePrompt("expand this image", outPaintingOptions);
		ImageResponse response = imageModel.call(prompt);

		assertThat(response.getResults()).hasSize(1);
		assertThat(response.getResult().getOutput().getUrl()).isEqualTo(TEST_IMAGE_URL);
	}

	@Test
	void testOutPaintingWithOffsetParameters() {
		// Test out-painting with pixel-based expansion
		DashScopeImageOptions outPaintingOptions = DashScopeImageOptions.builder()
				.model("image-out-painting")
				.baseImageUrl("https://example.com/original.jpg")
				.leftOffset(546)
				.rightOffset(960)
				.topOffset(158)
				.bottomOffset(939)
				.build();

		mockSuccessfulImageGeneration();

		ImagePrompt prompt = new ImagePrompt("expand this image", outPaintingOptions);
		ImageResponse response = imageModel.call(prompt);

		assertThat(response.getResults()).hasSize(1);
		assertThat(response.getResult().getOutput().getUrl()).isEqualTo(TEST_IMAGE_URL);
	}

}
