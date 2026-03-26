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

import java.util.List;

import com.alibaba.cloud.ai.dashscope.spec.DashScopeModel;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DashScope Video Generation Options.
 *
 * @author dashscope
 * @author yuluo，yingzi
 * @since 1.1.0.0
 */

public class DashScopeVideoOptions implements VideoOptions {

    /**
     * Default video model.
     */
    public static final String DEFAULT_MODEL = DashScopeModel.VideoModel.WANX21_T2V_TURBO.getName();

	@JsonProperty("model")
	private String model;

    @JsonProperty("input")
    private InputOptions input;

    @JsonProperty("parameters")
    private ParametersOptions parameters;

	@Override
	public String getModel() {
        return model;
	}

	public void setModel(String model) {
		this.model = model;
    }

    public InputOptions getInput() {
        return input;
    }

    public void setInput(InputOptions input) {
        this.input = input;
    }

    public ParametersOptions getParameters() {
        return parameters;
    }

    public void setParameters(ParametersOptions parameters) {
        this.parameters = parameters;
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {

        private String model;

        private InputOptions input;

        private ParametersOptions parameters;

        public Builder() {
        }

        public Builder model(String model) {
            this.model = model;
            return this;
        }

        public Builder input(InputOptions input) {
            this.input = input;
            return this;
        }

        public Builder parameters(ParametersOptions parameters) {
            this.parameters = parameters;
            return this;
        }

        public DashScopeVideoOptions build() {
            DashScopeVideoOptions options = new DashScopeVideoOptions();
            options.setModel(model == null ? DEFAULT_MODEL : model);
            options.setInput(input);
            options.setParameters(parameters);
            return options;
        }

    }

    public static class InputOptions {

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

        public InputOptions() {
        }

        public String getPrompt() {
            return prompt;
        }

        public void setPrompt(String prompt) {
            this.prompt = prompt;
        }

        public String getImgUrl() {
            return imgUrl;
        }

        public void setImgUrl(String imgUrl) {
            this.imgUrl = imgUrl;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public String getAudioUrl() {
            return audioUrl;
        }

        public void setAudioUrl(String audioUrl) {
            this.audioUrl = audioUrl;
        }

        public String getTemplate() {
            return template;
        }

        public void setTemplate(String template) {
            this.template = template;
        }

        public String getNegativePrompt() {
            return negativePrompt;
        }

        public void setNegativePrompt(String negativePrompt) {
            this.negativePrompt = negativePrompt;
        }

        public String getFirstFrameUrl() {
            return firstFrameUrl;
        }

        public void setFirstFrameUrl(String firstFrameUrl) {
            this.firstFrameUrl = firstFrameUrl;
        }

        public String getLastFrameUrl() {
            return lastFrameUrl;
        }

        public void setLastFrameUrl(String lastFrameUrl) {
            this.lastFrameUrl = lastFrameUrl;
        }

        public List<String> getReferenceVideoUrls() {
            return referenceVideoUrls;
        }

        public void setReferenceVideoUrls(List<String> referenceVideoUrls) {
            this.referenceVideoUrls = referenceVideoUrls;
        }

        public String getFunction() {
            return function;
        }

        public void setFunction(String function) {
            this.function = function;
        }

        public String getRefImageUrl() {
            return refImageUrl;
        }

        public void setRefImageUrl(String refImageUrl) {
            this.refImageUrl = refImageUrl;
        }

        public List<String> getRefImagesUrl() {
            return refImagesUrl;
        }

        public void setRefImagesUrl(List<String> refImagesUrl) {
            this.refImagesUrl = refImagesUrl;
        }

        public Integer getMaskFrameId() {
            return maskFrameId;
        }

        public void setMaskFrameId(Integer maskFrameId) {
            this.maskFrameId = maskFrameId;
        }

        public String getFirstClipUrl() {
            return firstClipUrl;
        }

        public void setFirstClipUrl(String firstClipUrl) {
            this.firstClipUrl = firstClipUrl;
        }

        public String getVideoUrl() {
            return videoUrl;
        }

        public void setVideoUrl(String videoUrl) {
            this.videoUrl = videoUrl;
        }

        public String getTemplateId() {
            return templateId;
        }

        public void setTemplateId(String templateId) {
            this.templateId = templateId;
        }

        public List<Integer> getFaceBbox() {
            return faceBbox;
        }

        public void setFaceBbox(List<Integer> faceBbox) {
            this.faceBbox = faceBbox;
        }

        public List<Integer> getExtBbox() {
            return extBbox;
        }

        public void setExtBbox(List<Integer> extBbox) {
            this.extBbox = extBbox;
        }

        public String getDrivenId() {
            return drivenId;
        }

        public void setDrivenId(String drivenId) {
            this.drivenId = drivenId;
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {

            private final InputOptions options;

            public Builder() {
                this.options = new InputOptions();
            }

            public Builder prompt(String prompt) {
                this.options.setPrompt(prompt);
                return this;
            }

            public Builder imgUrl(String imgUrl) {
                this.options.setImgUrl(imgUrl);
                return this;
            }

            public Builder imageUrl(String imageUrl) {
                this.options.setImageUrl(imageUrl);
                return this;
            }

            public Builder audioUrl(String audioUrl) {
                this.options.setAudioUrl(audioUrl);
                return this;
            }

            public Builder template(String template) {
                this.options.setTemplate(template);
                return this;
            }

            public Builder negativePrompt(String negativePrompt) {
                this.options.setNegativePrompt(negativePrompt);
                return this;
            }

            public Builder firstFrameUrl(String firstFrameUrl) {
                this.options.setFirstFrameUrl(firstFrameUrl);
                return this;
            }

            public Builder lastFrameUrl(String lastFrameUrl) {
                this.options.setLastFrameUrl(lastFrameUrl);
                return this;
            }

            public Builder referenceVideoUrls(List<String> referenceVideoUrls) {
                this.options.setReferenceVideoUrls(referenceVideoUrls);
                return this;
            }

            public Builder function(String function) {
                this.options.setFunction(function);
                return this;
            }

            public Builder refImageUrl(String refImageUrl) {
                this.options.setRefImageUrl(refImageUrl);
                return this;
            }

            public Builder refImagesUrl(List<String> refImagesUrl) {
                this.options.setRefImagesUrl(refImagesUrl);
                return this;
            }

            public Builder maskFrameId(Integer maskFrameId) {
                this.options.setMaskFrameId(maskFrameId);
                return this;
            }

            public Builder firstClipUrl(String firstClipUrl) {
                this.options.setFirstClipUrl(firstClipUrl);
                return this;
            }

            public Builder videoUrl(String videoUrl) {
                this.options.setVideoUrl(videoUrl);
                return this;
            }

            public Builder templateId(String templateId) {
                this.options.setTemplateId(templateId);
                return this;
            }

            public Builder faceBbox(List<Integer> faceBbox) {
                this.options.setFaceBbox(faceBbox);
                return this;
            }

            public Builder extBbox(List<Integer> extBbox) {
                this.options.setExtBbox(extBbox);
                return this;
            }

            public Builder drivenId(String drivenId) {
                this.options.setDrivenId(drivenId);
                return this;
            }

            public InputOptions build() {
                return this.options;
            }
        }
    }

    public static class ParametersOptions {

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

        public ParametersOptions() {
        }

        public String getResolution() {
            return resolution;
        }

        public void setResolution(String resolution) {
            this.resolution = resolution;
        }

        public String getSize() {
            return size;
        }

        public void setSize(String size) {
            this.size = size;
        }

        public Boolean getPromptExtend() {
            return promptExtend;
        }

        public void setPromptExtend(Boolean promptExtend) {
            this.promptExtend = promptExtend;
        }

        public Boolean getVideoExtension() {
            return videoExtension;
        }

        public void setVideoExtension(Boolean videoExtension) {
            this.videoExtension = videoExtension;
        }

        public Integer getDuration() {
            return duration;
        }

        public void setDuration(Integer duration) {
            this.duration = duration;
        }

        public String getShotType() {
            return shotType;
        }

        public void setShotType(String shotType) {
            this.shotType = shotType;
        }

        public List<String> getObjOrBg() {
            return objOrBg;
        }

        public void setObjOrBg(List<String> objOrBg) {
            this.objOrBg = objOrBg;
        }

        public String getMaskType() {
            return maskType;
        }

        public void setMaskType(String maskType) {
            this.maskType = maskType;
        }

        public Double getExpandRatio() {
            return expandRatio;
        }

        public void setExpandRatio(Double expandRatio) {
            this.expandRatio = expandRatio;
        }

        public Double getTopScale() {
            return topScale;
        }

        public void setTopScale(Double topScale) {
            this.topScale = topScale;
        }

        public Double getBottomScale() {
            return bottomScale;
        }

        public void setBottomScale(Double bottomScale) {
            this.bottomScale = bottomScale;
        }

        public Double getLeftScale() {
            return leftScale;
        }

        public void setLeftScale(Double leftScale) {
            this.leftScale = leftScale;
        }

        public Double getRightScale() {
            return rightScale;
        }

        public void setRightScale(Double rightScale) {
            this.rightScale = rightScale;
        }

        public String getMode() {
            return mode;
        }

        public void setMode(String mode) {
            this.mode = mode;
        }

        public Boolean getUseRefImgBg() {
            return useRefImgBg;
        }

        public void setUseRefImgBg(Boolean useRefImgBg) {
            this.useRefImgBg = useRefImgBg;
        }

        public String getVideoRatio() {
            return videoRatio;
        }

        public void setVideoRatio(String videoRatio) {
            this.videoRatio = videoRatio;
        }

        public String getRatio() {
            return ratio;
        }

        public void setRatio(String ratio) {
            this.ratio = ratio;
        }

        public String getStyleLevel() {
            return styleLevel;
        }

        public void setStyleLevel(String styleLevel) {
            this.styleLevel = styleLevel;
        }

        public String getTemplateId() {
            return templateId;
        }

        public void setTemplateId(String templateId) {
            this.templateId = templateId;
        }

        public Double getEyeMoveFreq() {
            return eyeMoveFreq;
        }

        public void setEyeMoveFreq(Double eyeMoveFreq) {
            this.eyeMoveFreq = eyeMoveFreq;
        }

        public Integer getVideoFps() {
            return videoFps;
        }

        public void setVideoFps(Integer videoFps) {
            this.videoFps = videoFps;
        }

        public Integer getMouthMoveStrength() {
            return mouthMoveStrength;
        }

        public void setMouthMoveStrength(Integer mouthMoveStrength) {
            this.mouthMoveStrength = mouthMoveStrength;
        }

        public Boolean getPasteBack() {
            return pasteBack;
        }

        public void setPasteBack(Boolean pasteBack) {
            this.pasteBack = pasteBack;
        }

        public Double getHeadMoveStrength() {
            return headMoveStrength;
        }

        public void setHeadMoveStrength(Double headMoveStrength) {
            this.headMoveStrength = headMoveStrength;
        }

        public Integer getStyle() {
            return style;
        }

        public void setStyle(Integer style) {
            this.style = style;
        }

        public Long getSeed() {
            return seed;
        }

        public void setSeed(Long seed) {
            this.seed = seed;
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {

            private final ParametersOptions options;

            public Builder() {
                this.options = new ParametersOptions();
            }

            public Builder resolution(String resolution) {
                this.options.setResolution(resolution);
                return this;
            }

            public Builder size(String size) {
                this.options.setSize(size);
                return this;
            }

            public Builder promptExtend(Boolean promptExtend) {
                this.options.setPromptExtend(promptExtend);
                return this;
            }

            public Builder videoExtension(Boolean videoExtension) {
                this.options.setVideoExtension(videoExtension);
                return this;
            }

            public Builder duration(Integer duration) {
                this.options.setDuration(duration);
                return this;
            }

            public Builder shotType(String shotType) {
                this.options.setShotType(shotType);
                return this;
            }

            public Builder objOrBg(List<String> objOrBg) {
                this.options.setObjOrBg(objOrBg);
                return this;
            }

            public Builder maskType(String maskType) {
                this.options.setMaskType(maskType);
                return this;
            }

            public Builder expandRatio(Double expandRatio) {
                this.options.setExpandRatio(expandRatio);
                return this;
            }

            public Builder topScale(Double topScale) {
                this.options.setTopScale(topScale);
                return this;
            }

            public Builder bottomScale(Double bottomScale) {
                this.options.setBottomScale(bottomScale);
                return this;
            }

            public Builder leftScale(Double leftScale) {
                this.options.setLeftScale(leftScale);
                return this;
            }

            public Builder rightScale(Double rightScale) {
                this.options.setRightScale(rightScale);
                return this;
            }

            public Builder mode(String mode) {
                this.options.setMode(mode);
                return this;
            }

            public Builder useRefImgBg(Boolean useRefImgBg) {
                this.options.setUseRefImgBg(useRefImgBg);
                return this;
            }

            public Builder videoRatio(String videoRatio) {
                this.options.setVideoRatio(videoRatio);
                return this;
            }

            public Builder ratio(String ratio) {
                this.options.setRatio(ratio);
                return this;
            }

            public Builder styleLevel(String styleLevel) {
                this.options.setStyleLevel(styleLevel);
                return this;
            }

            public Builder templateId(String templateId) {
                this.options.setTemplateId(templateId);
                return this;
            }

            public Builder eyeMoveFreq(Double eyeMoveFreq) {
                this.options.setEyeMoveFreq(eyeMoveFreq);
                return this;
            }

            public Builder videoFps(Integer videoFps) {
                this.options.setVideoFps(videoFps);
                return this;
            }

            public Builder mouthMoveStrength(Integer mouthMoveStrength) {
                this.options.setMouthMoveStrength(mouthMoveStrength);
                return this;
            }

            public Builder pasteBack(Boolean pasteBack) {
                this.options.setPasteBack(pasteBack);
                return this;
            }

            public Builder headMoveStrength(Double headMoveStrength) {
                this.options.setHeadMoveStrength(headMoveStrength);
                return this;
            }

            public Builder style(Integer style) {
                this.options.setStyle(style);
                return this;
            }

            public Builder seed(Long seed) {
                this.options.setSeed(seed);
                return this;
            }

            public ParametersOptions build() {
                return this.options;
            }
        }
    }

}
