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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.alibaba.cloud.ai.dashscope.api.DashScopeImageApi;
import com.alibaba.cloud.ai.dashscope.common.DashScopeApiConstants;
import com.alibaba.cloud.ai.dashscope.common.DashScopeImageApiConstants;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageApiSpec.ImageResponse.Choice;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageApiSpec.ImageResponse.TaskMetrics;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageApiSpec.ImageResponse.Usage;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageApiSpec.InvokeMode;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageApiSpec.Message;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageApiSpec.RequestType;
import com.alibaba.cloud.ai.dashscope.image.observation.DashScopeImageModelObservationConvention;
import com.alibaba.cloud.ai.dashscope.image.observation.DashScopeImagePromptContentObservationHandler;
import com.alibaba.cloud.ai.dashscope.spec.DashScopeModel;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationHandler;
import io.micrometer.observation.ObservationRegistry;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.image.Image;
import org.springframework.ai.image.ImageGeneration;
import org.springframework.ai.image.ImageModel;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.image.ImageResponseMetadata;
import org.springframework.ai.image.observation.DefaultImageModelObservationConvention;
import org.springframework.ai.image.observation.ImageModelObservationContext;
import org.springframework.ai.image.observation.ImageModelObservationConvention;
import org.springframework.ai.image.observation.ImageModelObservationDocumentation;
import org.springframework.ai.retry.RetryUtils;
import org.springframework.ai.retry.TransientAiException;
import org.springframework.core.retry.RetryTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * DashScope Image Model implementation.
 *
 * @author nuocheng.lxm
 * @author yuluo
 * @author polaris
 * @author xuguan
 * @since 2024/8/16 11:29
 */
public class DashScopeImageModel implements ImageModel {

    private static final Logger logger = LoggerFactory.getLogger(DashScopeImageModel.class);

    /** Default observation convention. */
    private static final ImageModelObservationConvention DEFAULT_OBSERVATION_CONVENTION = new DefaultImageModelObservationConvention();

    /**
     * Low-level access to the DashScope Image API.
     */
    private final DashScopeImageApi dashScopeImageApi;

    /**
     * The default options used for the image completion requests.
     */
    private final DashScopeImageOptions defaultOptions;

    /**
     * The retry template used to retry the DashScope Image API calls (e.g. transient fetch failures).
     */
    private final RetryTemplate retryTemplate;

    /**
     * Observation registry used for instrumentation.
     */
    private final ObservationRegistry observationRegistry;

    /**
     * Interval between task result polls, in milliseconds. Aliyun recommends a reasonable interval (e.g. 10 seconds).
     */
    private final long pollIntervalMs;

    /**
     * Maximum time to wait for task completion, in milliseconds. Task completion time is unpredictable (depends on queue and service load).
     */
    private final long pollTimeoutMs;

    /**
     * Conventions to use for generating observations.
     */
    private ImageModelObservationConvention observationConvention;

    /**
     * Full constructor with poll settings (used by builder).
     */
    private DashScopeImageModel(
            DashScopeImageApi dashScopeImageApi,
            DashScopeImageOptions options,
            RetryTemplate retryTemplate,
            ObservationRegistry observationRegistry,
            long pollIntervalMs,
            long pollTimeoutMs) {

        Assert.notNull(dashScopeImageApi, "DashScopeImageApi must not be null");
        Assert.notNull(options, "options must not be null");
        Assert.notNull(retryTemplate, "retryTemplate must not be null");
        Assert.notNull(observationRegistry, "observationRegistry must not be null");
        Assert.isTrue(pollIntervalMs > 0, "pollIntervalMs must be positive");
        Assert.isTrue(pollTimeoutMs >= pollIntervalMs, "pollTimeoutMs must be >= pollIntervalMs");

        this.dashScopeImageApi = dashScopeImageApi;
        this.defaultOptions = options;
        this.retryTemplate = retryTemplate;
        this.observationRegistry = observationRegistry;
        this.pollIntervalMs = pollIntervalMs;
        this.pollTimeoutMs = pollTimeoutMs;

        this.observationRegistry.observationConfig()
                .observationHandler(new DashScopeImagePromptContentObservationHandler());

        this.observationConvention = new DashScopeImageModelObservationConvention();
    }

    @Override
    public ImageResponse call(ImagePrompt request) {
        Assert.notNull(request, "Prompt must not be null");
        Assert.isTrue(!CollectionUtils.isEmpty(request.getInstructions()), "Prompt messages must not be empty");

        ImagePrompt requestImagePrompt = buildRequestImagePrompt(request);

        Assert.isInstanceOf(DashScopeImageOptions.class, requestImagePrompt.getOptions(), "Options must be DashScopeImageOptions");
        DashScopeImageOptions requestOptions = (DashScopeImageOptions) requestImagePrompt.getOptions();
        logger.debug("Image options: {}", requestOptions);
        String model = requestOptions.getModel();
        InvokeMode invokeMode = requestOptions.getInvokeMode();
        RequestType requestType = requestOptions.getRequestType();
        Assert.hasText(model, "Model must not be empty");
        Assert.notNull(invokeMode, "InvokeMode must not be null");
        Assert.notNull(requestType, "RequestType must not be null");

        DashScopeImageApiSpec.ImageRequest imageRequest = createRequest(requestImagePrompt);

        ImageModelObservationContext observationContext = ImageModelObservationContext.builder()
                .imagePrompt(requestImagePrompt)
                .provider(DashScopeApiConstants.PROVIDER_NAME)
                .build();

        return ImageModelObservationDocumentation.IMAGE_MODEL_OPERATION
                .observation(this.observationConvention, DEFAULT_OBSERVATION_CONVENTION,
                        () -> observationContext, this.observationRegistry)
                .observe(() -> {
                    DashScopeImageApiSpec.ImageResponse imageResponse = submitImageGenTask(imageRequest, invokeMode);
                    if (imageResponse == null || imageResponse.output() == null) {
                        ImageResponse response = new ImageResponse(List.of(), toMetadataEmpty());
                        observationContext.setResponse(response);
                        return response;
                    }

                    String taskId = imageResponse.output().taskId();
                    ImageResponse response;
                    if (DashScopeImageApiConstants.isAsync(model, invokeMode)) {
                        // Async invoke mode
                        if (!StringUtils.hasText(taskId)) {
                            response = new ImageResponse(List.of(), toMetadataEmpty());
                        }
                        else {
                            response = pollTaskResultUntilDone(model, taskId);
                        }
                    }
                    else {
                        // Sync invoke mode
                        response = toImageResponse(imageResponse);
                    }

                    observationContext.setResponse(response);
                    return response;
                });
    }

    /**
     * Polls task result with configurable interval and timeout. Per Aliyun doc: task return time is
     * unpredictable (PENDING → RUNNING → SUCCEEDED/FAILED); use a reasonable poll interval (e.g. 10s).
     * RetryTemplate is used only for transient fetch failures (null response).
     */
    private ImageResponse pollTaskResultUntilDone(String model, String taskId) {
        long deadlineMs = System.currentTimeMillis() + pollTimeoutMs;

        return RetryUtils.execute(this.retryTemplate, () -> {
            Observation observation = this.observationRegistry.getCurrentObservation();

            while (System.currentTimeMillis() < deadlineMs) {
                DashScopeImageApiSpec.ImageResponse resp = getImageGenTask(model, taskId);
                if (resp == null || resp.output() == null || resp.output().taskStatus() == null) {
                    logger.warn("No image response returned for taskId: {}, will retry", taskId);
                    throw new TransientAiException("Failed to fetch task result for " + taskId);
                }

                var output = resp.output();
                String status = output.taskStatus();
                Assert.hasText(status, "Task status must not be empty");

                if (observation != null) {
                    observation.lowCardinalityKeyValue("task.status", status);
                }

                switch (status) {
                    case "SUCCEEDED" -> {
                        return toImageResponse(resp);
                    }
                    case "FAILED", "CANCELED", "UNKNOWN" -> {
                        logger.warn("Image task {} ended with status {}: code={}, message={}",
                                taskId, status, output.code(), output.message());
                        return new ImageResponse(List.of(), toMetadata(resp));
                    }
                    case "PENDING", "RUNNING" -> { /* fall through to wait */ }
                    default -> logger.debug("Image task {} status {}, treating as in-progress", taskId, status);
                }

                long remaining = deadlineMs - System.currentTimeMillis();
                if (remaining < pollIntervalMs) {
                    if (observation != null) {
                        observation.lowCardinalityKeyValue("timeout", "true");
                    }
                    return new ImageResponse(List.of(), toMetadataTimeout(taskId));
                }
                try {
                    MILLISECONDS.sleep(pollIntervalMs);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    if (observation != null) {
                        observation.lowCardinalityKeyValue("timeout", "interrupted");
                    }
                    return new ImageResponse(List.of(), toMetadataTimeout(taskId));
                }
            }

            if (observation != null) {
                observation.lowCardinalityKeyValue("timeout", "true");
            }
            return new ImageResponse(List.of(), toMetadataTimeout(taskId));
        });
    }

    private ImagePrompt buildRequestImagePrompt(ImagePrompt imagePrompt) {
        DashScopeImageOptions options = DashScopeImageOptions.builder()
                .from(this.defaultOptions)
                .merge(imagePrompt.getOptions())
                .build();
        return new ImagePrompt(imagePrompt.getInstructions(), options);
    }

    private DashScopeImageApiSpec.ImageRequest createRequest(ImagePrompt request) {
        DashScopeImageOptions imageOptions = (DashScopeImageOptions) request.getOptions();
        Assert.notNull(imageOptions, "Image options must not be null");
        String model = imageOptions.getModel();
        Assert.hasText(model, "Model must not be empty");
        InvokeMode invokeMode = imageOptions.getInvokeMode();
        RequestType requestType = imageOptions.getRequestType();

        DashScopeImageApiSpec.ImageRequest imageRequest;
        if (DashScopeImageApiConstants.isGenerationRequestType(model, invokeMode, requestType)) {
            imageRequest = imageOptions.toImageGenerationRequest(request);
        }
        else {
            imageRequest = imageOptions.toImageRequest(request);
        }

        return imageRequest;
    }

    private DashScopeImageApiSpec.@Nullable ImageResponse submitImageGenTask(DashScopeImageApiSpec.ImageRequest request, InvokeMode invokeMode) {
        ResponseEntity<DashScopeImageApiSpec.ImageResponse> submitResponse =
                dashScopeImageApi.submitImageGenTask(request, invokeMode);

        if (submitResponse == null || submitResponse.getBody() == null) {
            logger.warn("Submit imageGen error, request: {}", request);
            return null;
        }

        return submitResponse.getBody();
    }

    private DashScopeImageApiSpec.@Nullable ImageResponse getImageGenTask(String model, String taskId) {
        ResponseEntity<DashScopeImageApiSpec.ImageResponse> getImageGenResponse = dashScopeImageApi.getImageGenTaskResult(model, taskId);
        if (getImageGenResponse == null || getImageGenResponse.getBody() == null) {
            logger.warn("No image response returned for taskId: {}", taskId);
            return null;
        }
        return getImageGenResponse.getBody();
    }

    public DashScopeImageOptions getOptions() {
        return this.defaultOptions;
    }

    private ImageResponse toImageResponse(DashScopeImageApiSpec.ImageResponse response) {
        var output = response.output();
        Assert.notNull(output, "Output must not be null");
        var results = output.results();
        String outputImageUrl = output.outputImageUrl();
        List<String> renderUrls = output.renderUrls();
        List<String> parsingImgUrls = output.parsingImgUrl();
        List<Choice> choices = output.choices();
        List<ImageGeneration> gens = new ArrayList<>();
        ImageResponseMetadata md = toMetadata(response);
        if (results != null) {
            for (DashScopeImageApiSpec.ImageResponse.Result r : results) {
                if (StringUtils.hasText(r.url())) {
                    gens.add(new ImageGeneration(new Image(r.url(), null)));
                }
                if (StringUtils.hasText(r.pngUrl())) {
                    gens.add(new ImageGeneration(new Image(r.pngUrl(), null)));
                }
            }
        }
        if (StringUtils.hasText(outputImageUrl)) {
            gens.add(new ImageGeneration(new Image(outputImageUrl, null)));
        }
        if (choices != null) {
            for (Choice choice : choices) {
                Message message = choice.message();
                if (message == null) {
                    continue;
                }
                List<Message.Content> contents = message.content();
                if (contents == null) {
                    continue;
                }
                for (Message.Content content : contents) {
                    if(StringUtils.hasText(content.image())){
                        gens.add(new ImageGeneration(new Image(content.image(), null)));
                    }
                }
            }
        }
        if (renderUrls != null) {
            for (String renderUrl : renderUrls) {
                if(StringUtils.hasText(renderUrl)){
                    gens.add(new ImageGeneration(new Image(renderUrl, null)));
                }
            }
        }
        if (parsingImgUrls != null) {
            for (String parsingImgUrl : parsingImgUrls) {
                if(StringUtils.hasText(parsingImgUrl)){
                    gens.add(new ImageGeneration(new Image(parsingImgUrl, null)));
                }
            }
        }

        return new ImageResponse(gens, md);
    }

    private ImageResponseMetadata toMetadata(DashScopeImageApiSpec.ImageResponse resp) {
        var out = resp.output();
        Assert.notNull(out, "Output must not be null");
        var tm = out.taskMetrics();
        var usage = resp.usage();

        ImageResponseMetadata md = new ImageResponseMetadata();

        Optional.ofNullable(usage).map(Usage::imageCount).ifPresent(count -> md.put("imageCount", count));
        Optional.ofNullable(tm).map(TaskMetrics::total).ifPresent(taskTotal -> md.put("taskTotal", taskTotal));
        Optional.ofNullable(tm).map(TaskMetrics::succeeded).ifPresent(taskSucceeded -> md.put("taskSucceeded", taskSucceeded));
        Optional.ofNullable(tm).map(TaskMetrics::failed).ifPresent(taskFailed -> md.put("taskFailed", taskFailed));
        Optional.ofNullable(resp.requestId()).ifPresent(requestId ->  md.put("requestId", requestId));
        Optional.ofNullable(out.taskId()).ifPresent(taskId -> md.put("taskId", taskId));
        Optional.ofNullable(out.taskStatus()).ifPresent(taskStatus -> md.put("taskStatus", taskStatus));
        Optional.ofNullable(out.code()).ifPresent(code -> md.put("code", code));
        Optional.ofNullable(out.message()).ifPresent(msg -> md.put("message", msg));
        Optional.ofNullable(out.bgUrls()).ifPresent(bgUrls -> md.put("bgUrls", bgUrls));
        Optional.ofNullable(out.outputVisImageUrl()).ifPresent(url -> md.put("outputVisImageUrl", url));
        Optional.ofNullable(out.cropImgUrl()).ifPresent(urls -> md.put("cropImgUrl", urls));
        Optional.ofNullable(out.bbox()).ifPresent(bbox -> md.put("bbox", bbox));
        Optional.ofNullable(out.isFace()).ifPresent(isFace -> md.put("isFace", isFace));
        Optional.ofNullable(out.failedReason()).ifPresent(reason -> md.put("failedReason", reason));
        Optional.ofNullable(out.finetunedOutput()).ifPresent(url -> md.put("finetunedOutput", url));
        List<String> svgUrls = Optional.ofNullable(out.results())
                .map(results -> results.stream()
                        .map(DashScopeImageApiSpec.ImageResponse.Result::svgUrl)
                        .filter(StringUtils::hasText)
                        .collect(Collectors.toList()))
                .orElse(List.of());
        if (!svgUrls.isEmpty()) {
            md.put("svgUrls", svgUrls);
        }

        return md;
    }

    private ImageResponseMetadata toMetadataEmpty() {
        ImageResponseMetadata md = new ImageResponseMetadata();
        md.put("taskStatus", "NO_TASK_ID");
        return md;
    }

    private ImageResponseMetadata toMetadataTimeout(String taskId) {
        ImageResponseMetadata md = new ImageResponseMetadata();
        md.put("taskId", taskId);
        md.put("taskStatus", "TIMED_OUT");
        return md;
    }

    /**
     * Use the provided convention for reporting observation data
     *
     * @param observationConvention The provided convention
     */
    public void setObservationConvention(ImageModelObservationConvention observationConvention) {
        Assert.notNull(observationConvention, "observationConvention cannot be null");
        this.observationConvention = observationConvention;
    }

    /**
     * Returns a builder pre-populated with the current configuration for mutation.
     */
    public Builder mutate() {
        return new Builder(this);
    }

    @Override
    public DashScopeImageModel clone() {
        return this.mutate().build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private @Nullable DashScopeImageApi dashScopeImageApi;

        private DashScopeImageOptions defaultOptions = DashScopeImageOptions.builder()
                .model(DashScopeModel.ImageModel.WAN_2_2_T2I_FLASH.getValue())
                .n(1)
                .build();

        private RetryTemplate retryTemplate = RetryUtils.DEFAULT_RETRY_TEMPLATE;

        private ObservationRegistry observationRegistry = ObservationRegistry.NOOP;

        private long pollIntervalMs = DashScopeApiConstants.DEFAULT_POLL_INTERVAL_MS;

        private long pollTimeoutMs = DashScopeApiConstants.DEFAULT_POLL_TIMEOUT_MS;

        private ImageModelObservationConvention observationConvention = new DashScopeImageModelObservationConvention();

        private ObservationHandler<ImageModelObservationContext> promptHandler = new DashScopeImagePromptContentObservationHandler();

        private Builder() {
        }

        private Builder(DashScopeImageModel imageModel) {
            this.dashScopeImageApi = imageModel.dashScopeImageApi;
            this.defaultOptions = imageModel.defaultOptions;
            this.retryTemplate = imageModel.retryTemplate;
            this.observationRegistry = imageModel.observationRegistry;
            this.pollIntervalMs = imageModel.pollIntervalMs;
            this.pollTimeoutMs = imageModel.pollTimeoutMs;
            this.observationConvention = imageModel.observationConvention;
        }

        public DashScopeImageModel.Builder dashScopeApi(DashScopeImageApi dashScopeImageApi) {
            this.dashScopeImageApi = dashScopeImageApi;
            return this;
        }

        public Builder defaultOptions(DashScopeImageOptions defaultOptions) {
            this.defaultOptions = defaultOptions;
            return this;
        }

        public Builder retryTemplate(RetryTemplate retryTemplate) {
            this.retryTemplate = retryTemplate;
            return this;
        }

        public Builder observationRegistry(ObservationRegistry observationRegistry) {
            this.observationRegistry = observationRegistry;
            return this;
        }

        /**
         * Interval between task result polls (ms). Doc recommends e.g. 10 seconds.
         */
        public Builder pollIntervalMs(long pollIntervalMs) {
            this.pollIntervalMs = pollIntervalMs;
            return this;
        }

        /**
         * Maximum time to wait for task completion (ms). Task completion time is unpredictable.
         */
        public Builder pollTimeoutMs(long pollTimeoutMs) {
            this.pollTimeoutMs = pollTimeoutMs;
            return this;
        }

        public Builder observationConvention(ImageModelObservationConvention observationConvention) {
            this.observationConvention = observationConvention;
            return this;
        }

        public Builder promptHandler(ObservationHandler<ImageModelObservationContext> promptHandler) {
            this.promptHandler = promptHandler;
            return this;
        }

        public DashScopeImageModel build() {
            Assert.notNull(dashScopeImageApi, "dashScopeImageApi cannot be null");
            DashScopeImageModel model = new DashScopeImageModel(dashScopeImageApi, defaultOptions, retryTemplate,
                    observationRegistry, pollIntervalMs, pollTimeoutMs);

            model.setObservationConvention(this.observationConvention);
            this.observationRegistry.observationConfig().observationHandler(this.promptHandler);
            return model;
        }
    }
}
