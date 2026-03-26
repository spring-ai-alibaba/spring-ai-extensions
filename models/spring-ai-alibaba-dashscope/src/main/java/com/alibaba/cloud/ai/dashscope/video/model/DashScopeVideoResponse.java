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
import org.springframework.ai.model.ModelResult;
import org.springframework.ai.model.ResultMetadata;

/**
 * @author yingzi
 * @since 2026/1/18
 */
public record DashScopeVideoResponse(@JsonProperty("request_id") String requestId,
                                     @JsonProperty("output") VideoOutput output,
                                     @JsonProperty("usage") VideoUsage usage) implements ModelResult<VideoOutput> {

    @Override
    public VideoOutput getOutput() {
        return output;
    }

    @Override
    public ResultMetadata getMetadata() {
        return null;
    }

    public record VideoOutput(@JsonProperty("task_id") String taskId, @JsonProperty("task_status") String taskStatus,
                              @JsonProperty("submit_time") String submitTime,
                              @JsonProperty("scheduled_time") String scheduledTime,
                              @JsonProperty("end_time") String endTime, @JsonProperty("orig_prompt") String origPrompt,
                              @JsonProperty("actual_prompt") String actualPrompt,
                              @JsonProperty("video_url") String videoUrl,
                              @JsonProperty("output_video_url") String outputVideoUrl, @JsonProperty("code") String code,
                              @JsonProperty("message") String message, @JsonProperty("results") VideoResult results,
                              @JsonProperty("check_pass") boolean checkPass, @JsonProperty("humanoid") boolean humanoid,
                              @JsonProperty("pass") boolean pass, @JsonProperty("bbox_face") List<Integer> bboxFace,
                              @JsonProperty("ext_bbox_face") List<Integer> extBboxFace) {}

    public record VideoUsage(@JsonProperty("duration") int duration,
                             @JsonProperty("input_video_duration") int inputVideoDuration,
                             @JsonProperty("output_video_duration") int outputVideoDuration,
                             @JsonProperty("video_count") int video_count, @JsonProperty("SR") int sr,
                             @JsonProperty("size") String size, @JsonProperty("video_ratio") String videoRatio,
                             @JsonProperty("video_duration") String videoDuration,
                             @JsonProperty("image_count") int imageCount) {}

    public record VideoResult(@JsonProperty("video_url") String videoUrl) {}
}

