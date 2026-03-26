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

/**
 * DashScope Video Generation Request.
 *
 * @author yingzi
 * @since 2026/1/18
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DashScopeVideoRequest {

    @JsonProperty("model")
    private String model;

    @JsonProperty("input")
    private VideoInput input;

    @JsonProperty("parameters")
    private VideoParameters parameters;

    public DashScopeVideoRequest(String model, VideoInput input, VideoParameters parameters) {
        this.model = model;
        this.input = input;
        this.parameters = parameters;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public VideoInput getInput() {
        return input;
    }

    public void setInput(VideoInput input) {
        this.input = input;
    }

    public VideoParameters getParameters() {
        return parameters;
    }

    public void setParameters(VideoParameters parameters) {
        this.parameters = parameters;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String model;

        private VideoInput input;

        private VideoParameters parameters;

        public Builder model(String model) {
            this.model = model;
            return this;
        }

        public Builder input(VideoInput input) {
            this.input = input;
            return this;
        }

        public Builder parameters(VideoParameters parameters) {
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
        private String prompt;

        @JsonProperty("img_url")
        private String imgUrl;

        @JsonProperty("image_url")
        private String imageUrl;

        @JsonProperty("audio_url")
        private String audioUrl;

        @JsonProperty("template")
        private String template;

        @JsonProperty("negative_prompt")
        private String negativePrompt;

        @JsonProperty("first_frame_url")
        private String firstFrameUrl;

        @JsonProperty("last_frame_url")
        private String lastFrameUrl;

        @JsonProperty("reference_video_urls")
        private List<String> referenceVideoUrls;

        @JsonProperty("function")
        private String function;

        @JsonProperty("ref_image_url")
        private String refImageUrl;

        @JsonProperty("ref_images_url")
        private List<String> refImagesUrl;

        @JsonProperty("mask_frame_id")
        private Integer maskFrameId;

        @JsonProperty("first_clip_url")
        private String firstClipUrl;

        @JsonProperty("video_url")
        private String videoUrl;

        @JsonProperty("template_id")
        private String templateId;

        @JsonProperty("face_bbox")
        private List<Integer> faceBbox;

        @JsonProperty("ext_bbox")
        private List<Integer> extBbox;

        @JsonProperty("driven_id")
        private String drivenId;

        public VideoInput() {
        }

        public static VideoInput optionsConvertReq(InputOptions options) {
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

            public Builder prompt(String prompt) {
                this.videoInput.prompt = prompt;
                return this;
            }

            public Builder imgUrl(String imgUrl) {
                this.videoInput.imgUrl = imgUrl;
                return this;
            }

            public Builder imageUrl(String imageUrl) {
                this.videoInput.imageUrl = imageUrl;
                return this;
            }

            public Builder audioUrl(String audioUrl) {
                this.videoInput.audioUrl = audioUrl;
                return this;
            }

            public Builder template(String template) {
                this.videoInput.template = template;
                return this;
            }

            public Builder negativePrompt(String negativePrompt) {
                this.videoInput.negativePrompt = negativePrompt;
                return this;
            }

            public Builder firstFrameUrl(String firstFrameUrl) {
                this.videoInput.firstFrameUrl = firstFrameUrl;
                return this;
            }

            public Builder lastFrameUrl(String lastFrameUrl) {
                this.videoInput.lastFrameUrl = lastFrameUrl;
                return this;
            }

            public Builder referenceVideoUrls(List<String> referenceVideoUrls) {
                this.videoInput.referenceVideoUrls = referenceVideoUrls;
                return this;
            }

            public Builder function(String function) {
                this.videoInput.function = function;
                return this;
            }

            public Builder refImageUrl(String refImageUrl) {
                this.videoInput.refImageUrl = refImageUrl;
                return this;
            }

            public Builder refImagesUrl(List<String> refImagesUrl) {
                this.videoInput.refImagesUrl = refImagesUrl;
                return this;
            }

            public Builder maskFrameId(Integer maskFrameId) {
                this.videoInput.maskFrameId = maskFrameId;
                return this;
            }

            public Builder firstClipUrl(String firstClipUrl) {
                this.videoInput.firstClipUrl = firstClipUrl;
                return this;
            }

            public Builder videoUrl(String videoUrl) {
                this.videoInput.videoUrl = videoUrl;
                return this;
            }

            public Builder templateId(String templateId) {
                this.videoInput.templateId = templateId;
                return this;
            }

            public Builder faceBbox(List<Integer> faceBbox) {
                this.videoInput.faceBbox = faceBbox;
                return this;
            }

            public Builder extBbox(List<Integer> extBbox) {
                this.videoInput.extBbox = extBbox;
                return this;
            }

            public Builder drivenId(String drivenId) {
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
        private String resolution;

        @JsonProperty("size")
        private String size;

        @JsonProperty("prompt_extend")
        private Boolean promptExtend;

        @JsonProperty("video_extension")
        private Boolean videoExtension;

        @JsonProperty("duration")
        private Integer duration;

        @JsonProperty("shot_type")
        private String shotType;

        @JsonProperty("obj_or_bg")
        private List<String> objOrBg;

        @JsonProperty("mask_type")
        private String maskType;

        @JsonProperty("expand_ratio")
        private Double expandRatio;

        @JsonProperty("top_scale")
        private Double topScale;

        @JsonProperty("bottom_scale")
        private Double bottomScale;

        @JsonProperty("left_scale")
        private Double leftScale;

        @JsonProperty("right_scale")
        private Double rightScale;

        @JsonProperty("mode")
        private String mode;

        @JsonProperty("use_ref_img_bg")
        private Boolean useRefImgBg;

        @JsonProperty("video_ratio")
        private String videoRatio;

        @JsonProperty("ratio")
        private String ratio;

        @JsonProperty("style_level")
        private String styleLevel;

        @JsonProperty("template_id")
        private String templateId;

        @JsonProperty("eye_move_freq")
        private Double eyeMoveFreq;

        @JsonProperty("video_fps")
        private Integer videoFps;

        @JsonProperty("mouth_move_strength")
        private Integer mouthMoveStrength;

        @JsonProperty("paste_back")
        private Boolean pasteBack;

        @JsonProperty("head_move_strength")
        private Double headMoveStrength;

        @JsonProperty("style")
        private Integer style;

        @JsonProperty("seed")
        private Long seed;

        public VideoParameters() {
        }

        public static VideoParameters optionsConvertReq(ParametersOptions parameters) {
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

            public Builder resolution(String resolution) {
                this.videoParameters.resolution = resolution;
                return this;
            }

            public Builder size(String size) {
                this.videoParameters.size = size;
                return this;
            }

            public Builder promptExtend(Boolean promptExtend) {
                this.videoParameters.promptExtend = promptExtend;
                return this;
            }

            public Builder videoExtension(Boolean videoExtension) {
                this.videoParameters.videoExtension = videoExtension;
                return this;
            }

            public Builder duration(Integer duration) {
                this.videoParameters.duration = duration;
                return this;
            }

            public Builder shotType(String shotType) {
                this.videoParameters.shotType = shotType;
                return this;
            }

            public Builder objOrBg(List<String> objOrBg) {
                this.videoParameters.objOrBg = objOrBg;
                return this;
            }

            public Builder maskType(String maskType) {
                this.videoParameters.maskType = maskType;
                return this;
            }

            public Builder expandRatio(Double expandRatio) {
                this.videoParameters.expandRatio = expandRatio;
                return this;
            }

            public Builder topScale(Double topScale) {
                this.videoParameters.topScale = topScale;
                return this;
            }

            public Builder bottomScale(Double bottomScale) {
                this.videoParameters.bottomScale = bottomScale;
                return this;
            }

            public Builder leftScale(Double leftScale) {
                this.videoParameters.leftScale = leftScale;
                return this;
            }

            public Builder rightScale(Double rightScale) {
                this.videoParameters.rightScale = rightScale;
                return this;
            }

            public Builder mode(String mode) {
                this.videoParameters.mode = mode;
                return this;
            }

            public Builder useRefImgBg(Boolean useRefImgBg) {
                this.videoParameters.useRefImgBg = useRefImgBg;
                return this;
            }

            public Builder videoRatio(String videoRatio) {
                this.videoParameters.videoRatio = videoRatio;
                return this;
            }

            public Builder ratio(String ratio) {
                this.videoParameters.ratio = ratio;
                return this;
            }

            public Builder styleLevel(String styleLevel) {
                this.videoParameters.styleLevel = styleLevel;
                return this;
            }

            public Builder templateId(String templateId) {
                this.videoParameters.templateId = templateId;
                return this;
            }

            public Builder eyeMoveFreq(Double eyeMoveFreq) {
                this.videoParameters.eyeMoveFreq = eyeMoveFreq;
                return this;
            }

            public Builder videoFps(Integer videoFps) {
                this.videoParameters.videoFps = videoFps;
                return this;
            }

            public Builder mouthMoveStrength(Integer mouthMoveStrength) {
                this.videoParameters.mouthMoveStrength = mouthMoveStrength;
                return this;
            }

            public Builder pasteBack(Boolean pasteBack) {
                this.videoParameters.pasteBack = pasteBack;
                return this;
            }

            public Builder headMoveStrength(Double headMoveStrength) {
                this.videoParameters.headMoveStrength = headMoveStrength;
                return this;
            }

            public Builder style(Integer style) {
                this.videoParameters.style = style;
                return this;
            }

            public Builder seed(Long seed) {
                this.videoParameters.seed = seed;
                return this;
            }

            public VideoParameters build() {
                return this.videoParameters;
            }

        }

    }

}
