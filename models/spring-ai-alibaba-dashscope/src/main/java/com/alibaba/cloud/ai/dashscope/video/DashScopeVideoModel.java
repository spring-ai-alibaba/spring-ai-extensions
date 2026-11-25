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

package com.alibaba.cloud.ai.dashscope.video;

import java.util.Objects;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

import com.alibaba.cloud.ai.dashscope.api.DashScopeVideoApi;
import com.alibaba.cloud.ai.dashscope.common.DashScopeVideoApiConstants;
import com.alibaba.cloud.ai.dashscope.video.model.DashScopeVideoRequest;
import com.alibaba.cloud.ai.dashscope.video.model.DashScopeVideoResponse;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.retry.RetryUtils;
import org.springframework.ai.retry.TransientAiException;
import org.springframework.core.retry.RetryTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;

/**
 * DashScope Video Generation Model.
 *
 * @author dashscope
 * @author yuluo、yingzi
 * @since 1.1.0.0
 */

public class DashScopeVideoModel implements VideoModel {

    private final static Logger logger = LoggerFactory.getLogger(DashScopeVideoModel.class);

    private final DashScopeVideoApi dashScopeVideoApi;

    private final DashScopeVideoOptions defaultOptions;

    private final RetryTemplate retryTemplate;

    private final Duration pollInterval;

    private final Duration pollTimeout;

    public DashScopeVideoModel(
            DashScopeVideoApi dashScopeVideoApi,
            DashScopeVideoOptions defaultOptions,
            RetryTemplate retryTemplate) {

        this(dashScopeVideoApi, defaultOptions, retryTemplate, Duration.ofSeconds(5), Duration.ofMinutes(5));
    }

    private DashScopeVideoModel(DashScopeVideoApi dashScopeVideoApi, DashScopeVideoOptions defaultOptions,
            RetryTemplate retryTemplate, Duration pollInterval, Duration pollTimeout) {
        Assert.notNull(dashScopeVideoApi, "DashScopeVideoApi must not be null");
        Assert.notNull(defaultOptions, "DashScopeVideoOptions must not be null");
        Assert.notNull(retryTemplate, "RetryTemplate must not be null");

        this.dashScopeVideoApi = dashScopeVideoApi;
        this.defaultOptions = defaultOptions;
        this.retryTemplate = retryTemplate;
        Assert.isTrue(pollInterval.toMillis() > 0 && !pollTimeout.isNegative() && !pollTimeout.isZero(),
                "Polling requires positive interval and timeout");
        this.pollInterval = pollInterval;
        this.pollTimeout = pollTimeout;
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Generate video from text prompt.
     */
    @Override
    public VideoResponse call(VideoPrompt prompt) {
        // Video Prompt use template gen, can null.
        Assert.notNull(prompt, "Prompt must not be null");
        Assert.notEmpty(prompt.getInstructions(), "Prompt instructions must not be empty");

        DashScopeVideoRequest request = buildDashScopeVideoRequest(prompt);
        // send request to DashScope Video API
        ResponseEntity<DashScopeVideoResponse> responseEntity = this.dashScopeVideoApi.submitVideoGenTask(request);
        if (Objects.isNull(responseEntity) || Objects.isNull(responseEntity.getBody())) {
            logger.error("Failed to submit video generation task: null response");
            throw new IllegalStateException("Failed to submit video generation task: null response");
        }
        DashScopeVideoResponse responseBody = responseEntity.getBody();
        String requestModel = request.getModel();
        Assert.hasText(requestModel, "Video model must not be empty");
        // 图像检测直接返回
        if (DashScopeVideoApiConstants.isDetect(requestModel)) {
            logger.info("Video detect task completed successfully:");
            return new VideoResponse(responseBody);
        }
        DashScopeVideoResponse response = responseBody;
        if (Objects.isNull(response.getOutput()) || Objects.isNull(response.getOutput().taskId())) {
            logger.error("Failed to submit video generation task, requestId: {}", response.requestId());
            throw new IllegalStateException("Failed to submit video generation task: invalid output");
        }

        String taskId = response.getOutput().taskId();

        String submissionStatus = response.getOutput().taskStatus();
        if ("SUCCEEDED".equals(submissionStatus)) {
            return toVideoResponse(response);
        }
        if ("FAILED".equals(submissionStatus) || "CANCELED".equals(submissionStatus)
                || "UNKNOWN".equals(submissionStatus)) {
            throw new IllegalStateException("Video generation task failed: " + taskId + " (" + submissionStatus + ")");
        }

        long start = System.nanoTime();
        while (true) {
            var result = RetryUtils.execute(this.retryTemplate, () -> getVideoTask(taskId));
            if (result != null) {
                String status = result.getOutput().taskStatus();
                if ("SUCCEEDED".equals(status)) {
                    return toVideoResponse(result);
                }
                if (!"PENDING".equals(status) && !"RUNNING".equals(status)) {
                    throw new IllegalStateException("Video generation task failed: " + taskId + " (" + status + ")");
                }
            }
            long remaining = this.pollTimeout.toNanos() - (System.nanoTime() - start);
            if (remaining <= 0) {
                throw new IllegalStateException("Timed out waiting for video generation task " + taskId);
            }
            try {
                TimeUnit.NANOSECONDS.sleep(Math.min(remaining, this.pollInterval.toNanos()));
            }
            catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("Interrupted while waiting for video generation task " + taskId, exception);
            }
        }
    }

    private DashScopeVideoResponse getVideoTask(String taskId) {

        ResponseEntity<DashScopeVideoResponse> videoGenerationResponseResponseEntity = this.dashScopeVideoApi.queryVideoGenTask(taskId);
        DashScopeVideoResponse body = videoGenerationResponseResponseEntity.getBody();
        if (!videoGenerationResponseResponseEntity.getStatusCode().is2xxSuccessful() || body == null) {
            throw new TransientAiException("No response for DashScope video task " + taskId);
        }
        return body;
    }

    private VideoResponse toVideoResponse(DashScopeVideoResponse asyncResp) {
        // todo: add metadata
        return new VideoResponse(asyncResp);
    }

    private DashScopeVideoRequest buildDashScopeVideoRequest(VideoPrompt prompt) {

        // Merge request options with default options
        DashScopeVideoOptions options = DashScopeVideoOptions.builder()
                .from(this.defaultOptions)
                .merge(prompt.getOptions())
                .build();
        logger.debug("Submitting video generation task with options: {}", options);
        String model = options.getModel();
        Assert.hasText(model, "Video model must not be empty");

        var input = DashScopeVideoRequest.VideoInput.optionsConvertReq(options.getInput(),
                prompt.getInstructions().get(0).getText());
        var parameterOptions = options.getParameters();
        if ((model.startsWith("wan2.7-") || model.startsWith("wan3.0-video")) && parameterOptions != null) {
            Assert.isTrue(parameterOptions.getSize() == null, "Wan 2.7 and 3.0 use resolution and ratio in place of size");
        }
        var parameters = DashScopeVideoRequest.VideoParameters.optionsConvertReq(parameterOptions);
        return DashScopeVideoRequest.builder()
                .model(model)
                .input(input)
                .parameters(parameters)
                .build();
    }

    public static final class Builder {

        private @Nullable DashScopeVideoApi videoApi;

        private DashScopeVideoOptions defaultOptions = DashScopeVideoOptions.builder().build();

        private RetryTemplate retryTemplate = RetryUtils.DEFAULT_RETRY_TEMPLATE;

        private Duration pollInterval = Duration.ofSeconds(5);

        private Duration pollTimeout = Duration.ofMinutes(5);

        private Builder() {
        }

        public Builder videoApi(DashScopeVideoApi videoApi) {
            this.videoApi = videoApi;
            return this;
        }

        public Builder defaultOptions(DashScopeVideoOptions defaultOptions) {
            this.defaultOptions = defaultOptions;
            return this;
        }

        public Builder retryTemplate(RetryTemplate retryTemplate) {
            this.retryTemplate = retryTemplate;
            return this;
        }

        public Builder pollInterval(Duration pollInterval) {
            this.pollInterval = pollInterval;
            return this;
        }

        public Builder pollTimeout(Duration pollTimeout) {
            this.pollTimeout = pollTimeout;
            return this;
        }

        public DashScopeVideoModel build() {
            DashScopeVideoApi videoApi = this.videoApi;
            Assert.notNull(videoApi, "videoApi must not be null");
            return new DashScopeVideoModel(videoApi, this.defaultOptions, this.retryTemplate, this.pollInterval, this.pollTimeout);
		}

	}

}
