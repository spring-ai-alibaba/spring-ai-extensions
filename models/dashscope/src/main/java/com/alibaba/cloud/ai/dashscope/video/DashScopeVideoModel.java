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

import com.alibaba.cloud.ai.dashscope.api.DashScopeVideoApi;
import com.alibaba.cloud.ai.dashscope.common.DashScopeVideoApiConstants;
import com.alibaba.cloud.ai.dashscope.video.model.DashScopeVideoRequest;
import com.alibaba.cloud.ai.dashscope.video.model.DashScopeVideoResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.model.ModelOptionsUtils;
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

    public DashScopeVideoModel(
            DashScopeVideoApi dashScopeVideoApi,
            DashScopeVideoOptions defaultOptions,
            RetryTemplate retryTemplate) {

        Assert.notNull(dashScopeVideoApi, "DashScopeVideoApi must not be null");
        Assert.notNull(defaultOptions, "DashScopeVideoOptions must not be null");
        Assert.notNull(retryTemplate, "RetryTemplate must not be null");

        this.dashScopeVideoApi = dashScopeVideoApi;
        this.defaultOptions = defaultOptions;
        this.retryTemplate = retryTemplate;
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
        // 图像检测直接返回
        if (DashScopeVideoApiConstants.isDetect(request.getModel())) {
            logger.info("Video detect task completed successfully:");
            return new VideoResponse(responseEntity.getBody());
        }

        if (Objects.isNull(responseEntity) || Objects.isNull(responseEntity.getBody())) {
            logger.error("Failed to submit video generation task: null response");
            throw new IllegalStateException("Failed to submit video generation task: null response");
        }
        DashScopeVideoResponse response = responseEntity.getBody();
        if (Objects.isNull(response.getOutput()) || Objects.isNull(response.getOutput().taskId())) {
            logger.error("Failed to submit video generation task: {}", response);
            throw new IllegalStateException("Failed to submit video generation task: invalid output");
        }

        String taskId = response.getOutput().taskId();

		// todo: add observation
		logger.warn("Video generation task submitted with taskId: {}", taskId);
		return RetryUtils.execute(this.retryTemplate, () -> {
            var resp = getVideoTask(taskId);
            if (Objects.nonNull(resp)) {
                logger.debug(String.valueOf(resp));
                String status = resp.getOutput().taskStatus();
                switch (status) {
                    // status enum SUCCEEDED, FAILED, PENDING, RUNNING
                    case "SUCCEEDED" -> {
                        logger.info("Video generation task completed successfully: {}", taskId);
                        return toVideoResponse(resp);
                    }
                    case "FAILED" -> {
                        logger.error("Video generation task failed: {}", resp.getOutput());
                        throw new IllegalStateException("Video generation task failed: " + resp.getOutput());
                    }
                }
            }
            throw new TransientAiException("Video generation still pending, retry ...");
        });
	}

    private DashScopeVideoResponse getVideoTask(String taskId) {

        ResponseEntity<DashScopeVideoResponse> videoGenerationResponseResponseEntity = this.dashScopeVideoApi.queryVideoGenTask(taskId);
        if (videoGenerationResponseResponseEntity.getStatusCode().is2xxSuccessful()) {
            return videoGenerationResponseResponseEntity.getBody();
        } else {
            logger.warn("Failed to query video task: {}", videoGenerationResponseResponseEntity.getStatusCode());
            return null;
        }
    }

    private VideoResponse toVideoResponse(DashScopeVideoResponse asyncResp) {
        // todo: add metadata
        return new VideoResponse(asyncResp);
    }

    private DashScopeVideoRequest buildDashScopeVideoRequest(VideoPrompt prompt) {

        DashScopeVideoOptions options = toVideoOptions(prompt.getOptions());
        logger.debug("Submitting video generation task with options: {}", options);

        return DashScopeVideoRequest.builder()
                .model(options.getModel())
                .input(DashScopeVideoRequest.VideoInput.optionsConvertReq(options.getInput()))
                .parameters(DashScopeVideoRequest.VideoParameters.optionsConvertReq(options.getParameters()))
                .build();
    }

    /**
     * Merge Video options. Notice: Programmatically(runtime) set options parameters take
     * precedence.
     */
    private DashScopeVideoOptions toVideoOptions(VideoOptions runtimeOptions) {
        // set default image model
        var currentOptions = DashScopeVideoOptions.builder().build();

        if (Objects.nonNull(runtimeOptions)) {
            currentOptions = ModelOptionsUtils.copyToTarget(runtimeOptions, VideoOptions.class, DashScopeVideoOptions.class);
        }

        currentOptions = ModelOptionsUtils.merge(currentOptions, this.defaultOptions, DashScopeVideoOptions.class);

        return currentOptions;
    }

    public static final class Builder {

        private DashScopeVideoApi videoApi;

        private DashScopeVideoOptions defaultOptions = DashScopeVideoOptions.builder().build();

        private RetryTemplate retryTemplate = RetryUtils.DEFAULT_RETRY_TEMPLATE;

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

        public DashScopeVideoModel build() {
            return new DashScopeVideoModel(this.videoApi, this.defaultOptions, this.retryTemplate);
		}

	}

}
