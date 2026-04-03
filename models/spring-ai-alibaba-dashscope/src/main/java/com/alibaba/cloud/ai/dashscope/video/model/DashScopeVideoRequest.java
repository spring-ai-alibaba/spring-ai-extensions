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

import com.alibaba.cloud.ai.dashscope.video.DashScopeVideoOptions.InputOptions;
import com.alibaba.cloud.ai.dashscope.video.DashScopeVideoOptions.ParametersOptions;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jspecify.annotations.Nullable;

/**
 * DashScope Video Generation Request.
 *
 * @author yingzi
 * @since 2026/1/18
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DashScopeVideoRequest {

    @JsonProperty("model")
    private @Nullable String model;

    @JsonProperty("input")
    private @Nullable VideoInput input;

    @JsonProperty("parameters")
    private @Nullable VideoParameters parameters;

    public DashScopeVideoRequest(@Nullable String model, @Nullable VideoInput input, @Nullable VideoParameters parameters) {
        this.model = model;
        this.input = input;
        this.parameters = parameters;
    }

    public @Nullable String getModel() {
        return model;
    }

    public void setModel(@Nullable String model) {
        this.model = model;
    }

    public @Nullable VideoInput getInput() {
        return input;
    }

    public void setInput(@Nullable VideoInput input) {
        this.input = input;
    }

    public @Nullable VideoParameters getParameters() {
        return parameters;
    }

    public void setParameters(@Nullable VideoParameters parameters) {
        this.parameters = parameters;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private @Nullable String model;

        private @Nullable VideoInput input;

        private @Nullable VideoParameters parameters;

        public Builder model(@Nullable String model) {
            this.model = model;
            return this;
        }

        public Builder input(@Nullable VideoInput input) {
            this.input = input;
            return this;
        }

        public Builder parameters(@Nullable VideoParameters parameters) {
            this.parameters = parameters;
            return this;
        }

        public DashScopeVideoRequest build() {
            return new DashScopeVideoRequest(this.model, this.input, this.parameters);
        }

    }

    /**
     * Video input parameters.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class VideoInput {

        @JsonProperty("prompt")
        private @Nullable String prompt;

        @JsonProperty("img_url")
        private @Nullable String imgUrl;

        @JsonProperty("image_url")
        private @Nullable String imageUrl;

        @JsonProperty("audio_url")
        private @Nullable String audioUrl;

        @JsonProperty("template")
        private @Nullable String template;

        @JsonProperty("negative_prompt")
        private @Nullable String negativePrompt;

        @JsonProperty("first_frame_url")
        private @Nullable String firstFrameUrl;

        @JsonProperty("last_frame_url")
        private @Nullable String lastFrameUrl;

        @JsonProperty("reference_video_urls")
        private @Nullable List<String> referenceVideoUrls;

        @JsonProperty("function")
        private @Nullable String function;

        @JsonProperty("ref_image_url")
        private @Nullable String refImageUrl;

        @JsonProperty("ref_images_url")
        private @Nullable List<String> refImagesUrl;

        @JsonProperty("mask_frame_id")
        private @Nullable Integer maskFrameId;

        @JsonProperty("first_clip_url")
        private @Nullable String firstClipUrl;

        @JsonProperty("video_url")
        private @Nullable String videoUrl;

        @JsonProperty("template_id")
        private @Nullable String templateId;

        @JsonProperty("face_bbox")
        private @Nullable List<Integer> faceBbox;

        @JsonProperty("ext_bbox")
        private @Nullable List<Integer> extBbox;

        @JsonProperty("driven_id")
        private @Nullable String drivenId;

        public VideoInput() {
        }

        public static VideoInput optionsConvertReq(@Nullable InputOptions options) {
            if (options == null) {
                return VideoInput.builder().build();
            }
            return VideoInput.builder()
                    .prompt(options.getPrompt())
                    .imageUrl(options.getImageUrl())
                    .imgUrl(options.getImgUrl())
                    .audioUrl(options.getAudioUrl())
                    .template(options.getTemplate())
                    .negativePrompt(options.getNegativePrompt())
                    .firstFrameUrl(options.getFirstFrameUrl())
                    .lastFrameUrl(options.getLastFrameUrl())
                    .referenceVideoUrls(options.getReferenceVideoUrls())
                    .function(options.getFunction())
                    .refImageUrl(options.getRefImageUrl())
                    .refImagesUrl(options.getRefImagesUrl())
                    .maskFrameId(options.getMaskFrameId())
                    .firstClipUrl(options.getFirstClipUrl())
                    .videoUrl(options.getVideoUrl())
                    .templateId(options.getTemplateId())
                    .faceBbox(options.getFaceBbox())
                    .extBbox(options.getExtBbox())
                    .drivenId(options.getDrivenId())
                    .build();
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {

            private final VideoInput videoInput;

            public Builder() {
                this.videoInput = new VideoInput();
            }

            public Builder prompt(@Nullable String prompt) {
                this.videoInput.prompt = prompt;
                return this;
            }

            public Builder imgUrl(@Nullable String imgUrl) {
                this.videoInput.imgUrl = imgUrl;
                return this;
            }

            public Builder imageUrl(@Nullable String imageUrl) {
                this.videoInput.imageUrl = imageUrl;
                return this;
            }

            public Builder audioUrl(@Nullable String audioUrl) {
                this.videoInput.audioUrl = audioUrl;
                return this;
            }

            public Builder template(@Nullable String template) {
                this.videoInput.template = template;
                return this;
            }

            public Builder negativePrompt(@Nullable String negativePrompt) {
                this.videoInput.negativePrompt = negativePrompt;
                return this;
            }

            public Builder firstFrameUrl(@Nullable String firstFrameUrl) {
                this.videoInput.firstFrameUrl = firstFrameUrl;
                return this;
            }

            public Builder lastFrameUrl(@Nullable String lastFrameUrl) {
                this.videoInput.lastFrameUrl = lastFrameUrl;
                return this;
            }

            public Builder referenceVideoUrls(@Nullable List<String> referenceVideoUrls) {
                this.videoInput.referenceVideoUrls = referenceVideoUrls;
                return this;
            }

            public Builder function(@Nullable String function) {
                this.videoInput.function = function;
                return this;
            }

            public Builder refImageUrl(@Nullable String refImageUrl) {
                this.videoInput.refImageUrl = refImageUrl;
                return this;
            }

            public Builder refImagesUrl(@Nullable List<String> refImagesUrl) {
                this.videoInput.refImagesUrl = refImagesUrl;
                return this;
            }

            public Builder maskFrameId(@Nullable Integer maskFrameId) {
                this.videoInput.maskFrameId = maskFrameId;
                return this;
            }

            public Builder firstClipUrl(@Nullable String firstClipUrl) {
                this.videoInput.firstClipUrl = firstClipUrl;
                return this;
            }

            public Builder videoUrl(@Nullable String videoUrl) {
                this.videoInput.videoUrl = videoUrl;
                return this;
            }

            public Builder templateId(@Nullable String templateId) {
                this.videoInput.templateId = templateId;
                return this;
            }

            public Builder faceBbox(@Nullable List<Integer> faceBbox) {
                this.videoInput.faceBbox = faceBbox;
                return this;
            }

            public Builder extBbox(@Nullable List<Integer> extBbox) {
                this.videoInput.extBbox = extBbox;
                return this;
            }

            public Builder drivenId(@Nullable String drivenId) {
                this.videoInput.drivenId = drivenId;
                return this;
            }

            public VideoInput build() {
                return videoInput;
            }

        }

    }

    /**
     * Video generation parameters.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class VideoParameters {

        @JsonProperty("resolution")
        private @Nullable String resolution;

        @JsonProperty("size")
        private @Nullable String size;

        @JsonProperty("prompt_extend")
        private @Nullable Boolean promptExtend;

        @JsonProperty("video_extension")
        private @Nullable Boolean videoExtension;

        @JsonProperty("duration")
        private @Nullable Integer duration;

        @JsonProperty("shot_type")
        private @Nullable String shotType;

        @JsonProperty("obj_or_bg")
        private @Nullable List<String> objOrBg;

        @JsonProperty("mask_type")
        private @Nullable String maskType;

        @JsonProperty("expand_ratio")
        private @Nullable Double expandRatio;

        @JsonProperty("top_scale")
        private @Nullable Double topScale;

        @JsonProperty("bottom_scale")
        private @Nullable Double bottomScale;

        @JsonProperty("left_scale")
        private @Nullable Double leftScale;

        @JsonProperty("right_scale")
        private @Nullable Double rightScale;

        @JsonProperty("mode")
        private @Nullable String mode;

        @JsonProperty("use_ref_img_bg")
        private @Nullable Boolean useRefImgBg;

        @JsonProperty("video_ratio")
        private @Nullable String videoRatio;

        @JsonProperty("ratio")
        private @Nullable String ratio;

        @JsonProperty("style_level")
        private @Nullable String styleLevel;

        @JsonProperty("template_id")
        private @Nullable String templateId;

        @JsonProperty("eye_move_freq")
        private @Nullable Double eyeMoveFreq;

        @JsonProperty("video_fps")
        private @Nullable Integer videoFps;

        @JsonProperty("mouth_move_strength")
        private @Nullable Integer mouthMoveStrength;

        @JsonProperty("paste_back")
        private @Nullable Boolean pasteBack;

        @JsonProperty("head_move_strength")
        private @Nullable Double headMoveStrength;

        @JsonProperty("style")
        private @Nullable Integer style;

        @JsonProperty("seed")
        private @Nullable Long seed;

        public VideoParameters() {
        }

        public static VideoParameters optionsConvertReq(@Nullable ParametersOptions parameters) {
            if (parameters == null)
                return VideoParameters.builder().build();

            return VideoParameters.builder()
                    .resolution(parameters.getResolution())
                    .size(parameters.getSize())
                    .promptExtend(parameters.getPromptExtend())
                    .duration(parameters.getDuration())
                    .shotType(parameters.getShotType())
                    .objOrBg(parameters.getObjOrBg())
                    .maskType(parameters.getMaskType())
                    .expandRatio(parameters.getExpandRatio())
                    .topScale(parameters.getTopScale())
                    .bottomScale(parameters.getBottomScale())
                    .leftScale(parameters.getLeftScale())
                    .rightScale(parameters.getRightScale())
                    .mode(parameters.getMode())
                    .useRefImgBg(parameters.getUseRefImgBg())
                    .videoRatio(parameters.getVideoRatio())
                    .ratio(parameters.getRatio())
                    .styleLevel(parameters.getStyleLevel())
                    .templateId(parameters.getTemplateId())
                    .eyeMoveFreq(parameters.getEyeMoveFreq())
                    .videoFps(parameters.getVideoFps())
                    .mouthMoveStrength(parameters.getMouthMoveStrength())
                    .pasteBack(parameters.getPasteBack())
                    .headMoveStrength(parameters.getHeadMoveStrength())
                    .style(parameters.getStyle())
                    .seed(parameters.getSeed())
                    .build();
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {

            private VideoParameters videoParameters;

            public Builder() {
                this.videoParameters = new VideoParameters();
            }

            public Builder resolution(@Nullable String resolution) {
                this.videoParameters.resolution = resolution;
                return this;
            }

            public Builder size(@Nullable String size) {
                this.videoParameters.size = size;
                return this;
            }

            public Builder promptExtend(@Nullable Boolean promptExtend) {
                this.videoParameters.promptExtend = promptExtend;
                return this;
            }

            public Builder videoExtension(@Nullable Boolean videoExtension) {
                this.videoParameters.videoExtension = videoExtension;
                return this;
            }

            public Builder duration(@Nullable Integer duration) {
                this.videoParameters.duration = duration;
                return this;
            }

            public Builder shotType(@Nullable String shotType) {
                this.videoParameters.shotType = shotType;
                return this;
            }

            public Builder objOrBg(@Nullable List<String> objOrBg) {
                this.videoParameters.objOrBg = objOrBg;
                return this;
            }

            public Builder maskType(@Nullable String maskType) {
                this.videoParameters.maskType = maskType;
                return this;
            }

            public Builder expandRatio(@Nullable Double expandRatio) {
                this.videoParameters.expandRatio = expandRatio;
                return this;
            }

            public Builder topScale(@Nullable Double topScale) {
                this.videoParameters.topScale = topScale;
                return this;
            }

            public Builder bottomScale(@Nullable Double bottomScale) {
                this.videoParameters.bottomScale = bottomScale;
                return this;
            }

            public Builder leftScale(@Nullable Double leftScale) {
                this.videoParameters.leftScale = leftScale;
                return this;
            }

            public Builder rightScale(@Nullable Double rightScale) {
                this.videoParameters.rightScale = rightScale;
                return this;
            }

            public Builder mode(@Nullable String mode) {
                this.videoParameters.mode = mode;
                return this;
            }

            public Builder useRefImgBg(@Nullable Boolean useRefImgBg) {
                this.videoParameters.useRefImgBg = useRefImgBg;
                return this;
            }

            public Builder videoRatio(@Nullable String videoRatio) {
                this.videoParameters.videoRatio = videoRatio;
                return this;
            }

            public Builder ratio(@Nullable String ratio) {
                this.videoParameters.ratio = ratio;
                return this;
            }

            public Builder styleLevel(@Nullable String styleLevel) {
                this.videoParameters.styleLevel = styleLevel;
                return this;
            }

            public Builder templateId(@Nullable String templateId) {
                this.videoParameters.templateId = templateId;
                return this;
            }

            public Builder eyeMoveFreq(@Nullable Double eyeMoveFreq) {
                this.videoParameters.eyeMoveFreq = eyeMoveFreq;
                return this;
            }

            public Builder videoFps(@Nullable Integer videoFps) {
                this.videoParameters.videoFps = videoFps;
                return this;
            }

            public Builder mouthMoveStrength(@Nullable Integer mouthMoveStrength) {
                this.videoParameters.mouthMoveStrength = mouthMoveStrength;
                return this;
            }

            public Builder pasteBack(@Nullable Boolean pasteBack) {
                this.videoParameters.pasteBack = pasteBack;
                return this;
            }

            public Builder headMoveStrength(@Nullable Double headMoveStrength) {
                this.videoParameters.headMoveStrength = headMoveStrength;
                return this;
            }

            public Builder style(@Nullable Integer style) {
                this.videoParameters.style = style;
                return this;
            }

            public Builder seed(@Nullable Long seed) {
                this.videoParameters.seed = seed;
                return this;
            }

            public VideoParameters build() {
                return this.videoParameters;
            }

        }

    }

}
