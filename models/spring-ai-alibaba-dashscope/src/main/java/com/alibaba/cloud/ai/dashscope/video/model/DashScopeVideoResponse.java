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
package com.alibaba.cloud.ai.dashscope.video.model;

import java.util.List;

import com.alibaba.cloud.ai.dashscope.video.model.DashScopeVideoResponse.VideoOutput;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jspecify.annotations.Nullable;
import org.springframework.ai.model.ModelResult;
import org.springframework.ai.model.ResultMetadata;

/**
 * @author yingzi
 * @since 2026/1/18
 */
public record DashScopeVideoResponse(@JsonProperty("request_id") @Nullable String requestId,
                                     @JsonProperty("output") @Nullable VideoOutput output,
                                     @JsonProperty("usage") @Nullable VideoUsage usage) implements ModelResult<VideoOutput> {

    private static final ResultMetadata EMPTY_METADATA = new ResultMetadata() {
    };

    private static final VideoOutput EMPTY_OUTPUT = new VideoOutput(null, null, null, null, null, null, null, null,
            null, null, null, null, null, null, null, null, null);

    @Override
    public VideoOutput getOutput() {
        return output != null ? output : EMPTY_OUTPUT;
    }

    @Override
    public ResultMetadata getMetadata() {
        return EMPTY_METADATA;
    }

    public record VideoOutput(@JsonProperty("task_id") @Nullable String taskId,
                              @JsonProperty("task_status") @Nullable String taskStatus,
                              @JsonProperty("submit_time") @Nullable String submitTime,
                              @JsonProperty("scheduled_time") @Nullable String scheduledTime,
                              @JsonProperty("end_time") @Nullable String endTime,
                              @JsonProperty("orig_prompt") @Nullable String origPrompt,
                              @JsonProperty("actual_prompt") @Nullable String actualPrompt,
                              @JsonProperty("video_url") @Nullable String videoUrl,
                              @JsonProperty("output_video_url") @Nullable String outputVideoUrl,
                              @JsonProperty("code") @Nullable String code,
                              @JsonProperty("message") @Nullable String message,
                              @JsonProperty("results") @Nullable VideoResult results,
                              @JsonProperty("check_pass") @Nullable Boolean checkPass,
                              @JsonProperty("humanoid") @Nullable Boolean humanoid,
                              @JsonProperty("pass") @Nullable Boolean pass,
                              @JsonProperty("bbox_face") @Nullable List<Integer> bboxFace,
                              @JsonProperty("ext_bbox_face") @Nullable List<Integer> extBboxFace) {}

    public record VideoUsage(@JsonProperty("duration") @Nullable Integer duration,
                             @JsonProperty("input_video_duration") @Nullable Integer inputVideoDuration,
                             @JsonProperty("output_video_duration") @Nullable Integer outputVideoDuration,
                             @JsonProperty("video_count") @Nullable Integer videoCount,
                             @JsonProperty("SR") @Nullable Integer sr,
                             @JsonProperty("size") @Nullable String size,
                             @JsonProperty("video_ratio") @Nullable String videoRatio,
                             @JsonProperty("video_duration") @Nullable String videoDuration,
                             @JsonProperty("image_count") @Nullable Integer imageCount) {}

    public record VideoResult(@JsonProperty("video_url") @Nullable String videoUrl) {}
}
