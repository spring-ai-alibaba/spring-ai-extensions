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
import org.jspecify.annotations.Nullable;

import java.util.Objects;

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
	private final String model;

	@JsonProperty("input")
	private final @Nullable InputOptions input;

	@JsonProperty("parameters")
	private final @Nullable ParametersOptions parameters;

	protected DashScopeVideoOptions(@Nullable String model, @Nullable InputOptions input,
			@Nullable ParametersOptions parameters) {
		this.model = model != null ? model : DEFAULT_MODEL;
		this.input = input;
		this.parameters = parameters;
	}

	@Override
	public String getModel() {
		return model;
	}

	public @Nullable InputOptions getInput() {
		return input;
	}

	public @Nullable ParametersOptions getParameters() {
		return parameters;
	}

	public Builder mutate() {
		return builder()
				.model(this.model)
				.input(this.input)
				.parameters(this.parameters);
	}

    public static DashScopeVideoOptions fromOptions(DashScopeVideoOptions options) {
        return options.mutate().build();
    }

    public static Builder builder() {
        return new Builder();
    }

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		DashScopeVideoOptions that = (DashScopeVideoOptions) o;
		return Objects.equals(this.model, that.model) && Objects.equals(this.input, that.input)
				&& Objects.equals(this.parameters, that.parameters);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.model, this.input, this.parameters);
	}

	@Override
	public String toString() {
		return "DashScopeVideoOptions{" + "model='" + this.model + '\'' + ", input=" + this.input
				+ ", parameters=" + this.parameters + '}';
	}

	public static class Builder {

		protected @Nullable String model;

		protected @Nullable InputOptions input;

		protected @Nullable ParametersOptions parameters;

		public Builder() {
		}

		public Builder model(@Nullable String model) {
			this.model = model;
			return this;
		}

		public Builder input(@Nullable InputOptions input) {
			this.input = input;
			return this;
		}

		public Builder parameters(@Nullable ParametersOptions parameters) {
			this.parameters = parameters;
			return this;
		}

		public Builder from(DashScopeVideoOptions fromOptions) {
			this.model = fromOptions.getModel();
			this.input = fromOptions.getInput();
			this.parameters = fromOptions.getParameters();
			return this;
		}

		public Builder merge(@Nullable VideoOptions from) {
			if (from == null) {
				return this;
			}
			if (from.getModel() != null) {
				this.model = from.getModel();
			}
			if (from instanceof DashScopeVideoOptions castFrom) {
				if (castFrom.getInput() != null) {
					this.input = mergeInput(this.input, castFrom.getInput());
				}
				if (castFrom.getParameters() != null) {
					this.parameters = mergeParameters(this.parameters, castFrom.getParameters());
				}
			}
			return this;
		}

		private @Nullable InputOptions mergeInput(@Nullable InputOptions base, InputOptions override) {
			if (base == null) {
				return override;
			}
			return InputOptions.builder().from(base).merge(override).build();
		}

		private @Nullable ParametersOptions mergeParameters(@Nullable ParametersOptions base, ParametersOptions override) {
			if (base == null) {
				return override;
			}
			return ParametersOptions.builder().from(base).merge(override).build();
		}

		public DashScopeVideoOptions build() {
			return new DashScopeVideoOptions(this.model, this.input, this.parameters);
		}

	}

	public static class InputOptions {

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

		public InputOptions() {
		}

		public @Nullable String getPrompt() {
			return prompt;
		}

		public void setPrompt(@Nullable String prompt) {
			this.prompt = prompt;
		}

		public @Nullable String getImgUrl() {
			return imgUrl;
		}

		public void setImgUrl(@Nullable String imgUrl) {
			this.imgUrl = imgUrl;
		}

		public @Nullable String getImageUrl() {
			return imageUrl;
		}

		public void setImageUrl(@Nullable String imageUrl) {
			this.imageUrl = imageUrl;
		}

		public @Nullable String getAudioUrl() {
			return audioUrl;
		}

		public void setAudioUrl(@Nullable String audioUrl) {
			this.audioUrl = audioUrl;
		}

		public @Nullable String getTemplate() {
			return template;
		}

		public void setTemplate(@Nullable String template) {
			this.template = template;
		}

		public @Nullable String getNegativePrompt() {
			return negativePrompt;
		}

		public void setNegativePrompt(@Nullable String negativePrompt) {
			this.negativePrompt = negativePrompt;
		}

		public @Nullable String getFirstFrameUrl() {
			return firstFrameUrl;
		}

		public void setFirstFrameUrl(@Nullable String firstFrameUrl) {
			this.firstFrameUrl = firstFrameUrl;
		}

		public @Nullable String getLastFrameUrl() {
			return lastFrameUrl;
		}

		public void setLastFrameUrl(@Nullable String lastFrameUrl) {
			this.lastFrameUrl = lastFrameUrl;
		}

		public @Nullable List<String> getReferenceVideoUrls() {
			return referenceVideoUrls;
		}

		public void setReferenceVideoUrls(@Nullable List<String> referenceVideoUrls) {
			this.referenceVideoUrls = referenceVideoUrls;
		}

		public @Nullable String getFunction() {
			return function;
		}

		public void setFunction(@Nullable String function) {
			this.function = function;
		}

		public @Nullable String getRefImageUrl() {
			return refImageUrl;
		}

		public void setRefImageUrl(@Nullable String refImageUrl) {
			this.refImageUrl = refImageUrl;
		}

		public @Nullable List<String> getRefImagesUrl() {
			return refImagesUrl;
		}

		public void setRefImagesUrl(@Nullable List<String> refImagesUrl) {
			this.refImagesUrl = refImagesUrl;
		}

		public @Nullable Integer getMaskFrameId() {
			return maskFrameId;
		}

		public void setMaskFrameId(@Nullable Integer maskFrameId) {
			this.maskFrameId = maskFrameId;
		}

		public @Nullable String getFirstClipUrl() {
			return firstClipUrl;
		}

		public void setFirstClipUrl(@Nullable String firstClipUrl) {
			this.firstClipUrl = firstClipUrl;
		}

		public @Nullable String getVideoUrl() {
			return videoUrl;
		}

		public void setVideoUrl(@Nullable String videoUrl) {
			this.videoUrl = videoUrl;
		}

		public @Nullable String getTemplateId() {
			return templateId;
		}

		public void setTemplateId(@Nullable String templateId) {
			this.templateId = templateId;
		}

		public @Nullable List<Integer> getFaceBbox() {
			return faceBbox;
		}

		public void setFaceBbox(@Nullable List<Integer> faceBbox) {
			this.faceBbox = faceBbox;
		}

		public @Nullable List<Integer> getExtBbox() {
			return extBbox;
		}

		public void setExtBbox(@Nullable List<Integer> extBbox) {
			this.extBbox = extBbox;
		}

		public @Nullable String getDrivenId() {
			return drivenId;
		}

		public void setDrivenId(@Nullable String drivenId) {
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

			public Builder prompt(@Nullable String prompt) {
				this.options.setPrompt(prompt);
				return this;
			}

			public Builder imgUrl(@Nullable String imgUrl) {
				this.options.setImgUrl(imgUrl);
				return this;
			}

			public Builder imageUrl(@Nullable String imageUrl) {
				this.options.setImageUrl(imageUrl);
				return this;
			}

			public Builder audioUrl(@Nullable String audioUrl) {
				this.options.setAudioUrl(audioUrl);
				return this;
			}

			public Builder template(@Nullable String template) {
				this.options.setTemplate(template);
				return this;
			}

			public Builder negativePrompt(@Nullable String negativePrompt) {
				this.options.setNegativePrompt(negativePrompt);
				return this;
			}

			public Builder firstFrameUrl(@Nullable String firstFrameUrl) {
				this.options.setFirstFrameUrl(firstFrameUrl);
				return this;
			}

			public Builder lastFrameUrl(@Nullable String lastFrameUrl) {
				this.options.setLastFrameUrl(lastFrameUrl);
				return this;
			}

			public Builder referenceVideoUrls(@Nullable List<String> referenceVideoUrls) {
				this.options.setReferenceVideoUrls(referenceVideoUrls);
				return this;
			}

			public Builder function(@Nullable String function) {
				this.options.setFunction(function);
				return this;
			}

			public Builder refImageUrl(@Nullable String refImageUrl) {
				this.options.setRefImageUrl(refImageUrl);
				return this;
			}

			public Builder refImagesUrl(@Nullable List<String> refImagesUrl) {
				this.options.setRefImagesUrl(refImagesUrl);
				return this;
			}

			public Builder maskFrameId(@Nullable Integer maskFrameId) {
				this.options.setMaskFrameId(maskFrameId);
				return this;
			}

			public Builder firstClipUrl(@Nullable String firstClipUrl) {
				this.options.setFirstClipUrl(firstClipUrl);
				return this;
			}

			public Builder videoUrl(@Nullable String videoUrl) {
				this.options.setVideoUrl(videoUrl);
				return this;
			}

			public Builder templateId(@Nullable String templateId) {
				this.options.setTemplateId(templateId);
				return this;
			}

			public Builder faceBbox(@Nullable List<Integer> faceBbox) {
				this.options.setFaceBbox(faceBbox);
				return this;
			}

			public Builder extBbox(@Nullable List<Integer> extBbox) {
				this.options.setExtBbox(extBbox);
				return this;
			}

			public Builder drivenId(@Nullable String drivenId) {
				this.options.setDrivenId(drivenId);
				return this;
			}

			public Builder from(InputOptions fromOptions) {
				this.options.setPrompt(fromOptions.getPrompt());
				this.options.setImgUrl(fromOptions.getImgUrl());
				this.options.setImageUrl(fromOptions.getImageUrl());
				this.options.setAudioUrl(fromOptions.getAudioUrl());
				this.options.setTemplate(fromOptions.getTemplate());
				this.options.setNegativePrompt(fromOptions.getNegativePrompt());
				this.options.setFirstFrameUrl(fromOptions.getFirstFrameUrl());
				this.options.setLastFrameUrl(fromOptions.getLastFrameUrl());
				this.options.setReferenceVideoUrls(fromOptions.getReferenceVideoUrls());
				this.options.setFunction(fromOptions.getFunction());
				this.options.setRefImageUrl(fromOptions.getRefImageUrl());
				this.options.setRefImagesUrl(fromOptions.getRefImagesUrl());
				this.options.setMaskFrameId(fromOptions.getMaskFrameId());
				this.options.setFirstClipUrl(fromOptions.getFirstClipUrl());
				this.options.setVideoUrl(fromOptions.getVideoUrl());
				this.options.setTemplateId(fromOptions.getTemplateId());
				this.options.setFaceBbox(fromOptions.getFaceBbox());
				this.options.setExtBbox(fromOptions.getExtBbox());
				this.options.setDrivenId(fromOptions.getDrivenId());
				return this;
			}

			public Builder merge(@Nullable InputOptions from) {
				if (from == null) {
					return this;
				}
				if (from.getPrompt() != null) {
					this.options.setPrompt(from.getPrompt());
				}
				if (from.getImgUrl() != null) {
					this.options.setImgUrl(from.getImgUrl());
				}
				if (from.getImageUrl() != null) {
					this.options.setImageUrl(from.getImageUrl());
				}
				if (from.getAudioUrl() != null) {
					this.options.setAudioUrl(from.getAudioUrl());
				}
				if (from.getTemplate() != null) {
					this.options.setTemplate(from.getTemplate());
				}
				if (from.getNegativePrompt() != null) {
					this.options.setNegativePrompt(from.getNegativePrompt());
				}
				if (from.getFirstFrameUrl() != null) {
					this.options.setFirstFrameUrl(from.getFirstFrameUrl());
				}
				if (from.getLastFrameUrl() != null) {
					this.options.setLastFrameUrl(from.getLastFrameUrl());
				}
				if (from.getReferenceVideoUrls() != null) {
					this.options.setReferenceVideoUrls(from.getReferenceVideoUrls());
				}
				if (from.getFunction() != null) {
					this.options.setFunction(from.getFunction());
				}
				if (from.getRefImageUrl() != null) {
					this.options.setRefImageUrl(from.getRefImageUrl());
				}
				if (from.getRefImagesUrl() != null) {
					this.options.setRefImagesUrl(from.getRefImagesUrl());
				}
				if (from.getMaskFrameId() != null) {
					this.options.setMaskFrameId(from.getMaskFrameId());
				}
				if (from.getFirstClipUrl() != null) {
					this.options.setFirstClipUrl(from.getFirstClipUrl());
				}
				if (from.getVideoUrl() != null) {
					this.options.setVideoUrl(from.getVideoUrl());
				}
				if (from.getTemplateId() != null) {
					this.options.setTemplateId(from.getTemplateId());
				}
				if (from.getFaceBbox() != null) {
					this.options.setFaceBbox(from.getFaceBbox());
				}
				if (from.getExtBbox() != null) {
					this.options.setExtBbox(from.getExtBbox());
				}
				if (from.getDrivenId() != null) {
					this.options.setDrivenId(from.getDrivenId());
				}
				return this;
			}

			public InputOptions build() {
				return this.options;
			}
		}
	}

	public static class ParametersOptions {

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

		public ParametersOptions() {
		}

		public @Nullable String getResolution() {
			return resolution;
		}

		public void setResolution(@Nullable String resolution) {
			this.resolution = resolution;
		}

		public @Nullable String getSize() {
			return size;
		}

		public void setSize(@Nullable String size) {
			this.size = size;
		}

		public @Nullable Boolean getPromptExtend() {
			return promptExtend;
		}

		public void setPromptExtend(@Nullable Boolean promptExtend) {
			this.promptExtend = promptExtend;
		}

		public @Nullable Boolean getVideoExtension() {
			return videoExtension;
		}

		public void setVideoExtension(@Nullable Boolean videoExtension) {
			this.videoExtension = videoExtension;
		}

		public @Nullable Integer getDuration() {
			return duration;
		}

		public void setDuration(@Nullable Integer duration) {
			this.duration = duration;
		}

		public @Nullable String getShotType() {
			return shotType;
		}

		public void setShotType(@Nullable String shotType) {
			this.shotType = shotType;
		}

		public @Nullable List<String> getObjOrBg() {
			return objOrBg;
		}

		public void setObjOrBg(@Nullable List<String> objOrBg) {
			this.objOrBg = objOrBg;
		}

		public @Nullable String getMaskType() {
			return maskType;
		}

		public void setMaskType(@Nullable String maskType) {
			this.maskType = maskType;
		}

		public @Nullable Double getExpandRatio() {
			return expandRatio;
		}

		public void setExpandRatio(@Nullable Double expandRatio) {
			this.expandRatio = expandRatio;
		}

		public @Nullable Double getTopScale() {
			return topScale;
		}

		public void setTopScale(@Nullable Double topScale) {
			this.topScale = topScale;
		}

		public @Nullable Double getBottomScale() {
			return bottomScale;
		}

		public void setBottomScale(@Nullable Double bottomScale) {
			this.bottomScale = bottomScale;
		}

		public @Nullable Double getLeftScale() {
			return leftScale;
		}

		public void setLeftScale(@Nullable Double leftScale) {
			this.leftScale = leftScale;
		}

		public @Nullable Double getRightScale() {
			return rightScale;
		}

		public void setRightScale(@Nullable Double rightScale) {
			this.rightScale = rightScale;
		}

		public @Nullable String getMode() {
			return mode;
		}

		public void setMode(@Nullable String mode) {
			this.mode = mode;
		}

		public @Nullable Boolean getUseRefImgBg() {
			return useRefImgBg;
		}

		public void setUseRefImgBg(@Nullable Boolean useRefImgBg) {
			this.useRefImgBg = useRefImgBg;
		}

		public @Nullable String getVideoRatio() {
			return videoRatio;
		}

		public void setVideoRatio(@Nullable String videoRatio) {
			this.videoRatio = videoRatio;
		}

		public @Nullable String getRatio() {
			return ratio;
		}

		public void setRatio(@Nullable String ratio) {
			this.ratio = ratio;
		}

		public @Nullable String getStyleLevel() {
			return styleLevel;
		}

		public void setStyleLevel(@Nullable String styleLevel) {
			this.styleLevel = styleLevel;
		}

		public @Nullable String getTemplateId() {
			return templateId;
		}

		public void setTemplateId(@Nullable String templateId) {
			this.templateId = templateId;
		}

		public @Nullable Double getEyeMoveFreq() {
			return eyeMoveFreq;
		}

		public void setEyeMoveFreq(@Nullable Double eyeMoveFreq) {
			this.eyeMoveFreq = eyeMoveFreq;
		}

		public @Nullable Integer getVideoFps() {
			return videoFps;
		}

		public void setVideoFps(@Nullable Integer videoFps) {
			this.videoFps = videoFps;
		}

		public @Nullable Integer getMouthMoveStrength() {
			return mouthMoveStrength;
		}

		public void setMouthMoveStrength(@Nullable Integer mouthMoveStrength) {
			this.mouthMoveStrength = mouthMoveStrength;
		}

		public @Nullable Boolean getPasteBack() {
			return pasteBack;
		}

		public void setPasteBack(@Nullable Boolean pasteBack) {
			this.pasteBack = pasteBack;
		}

		public @Nullable Double getHeadMoveStrength() {
			return headMoveStrength;
		}

		public void setHeadMoveStrength(@Nullable Double headMoveStrength) {
			this.headMoveStrength = headMoveStrength;
		}

		public @Nullable Integer getStyle() {
			return style;
		}

		public void setStyle(@Nullable Integer style) {
			this.style = style;
		}

		public @Nullable Long getSeed() {
			return seed;
		}

		public void setSeed(@Nullable Long seed) {
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

			public Builder resolution(@Nullable String resolution) {
				this.options.setResolution(resolution);
				return this;
			}

			public Builder size(@Nullable String size) {
				this.options.setSize(size);
				return this;
			}

			public Builder promptExtend(@Nullable Boolean promptExtend) {
				this.options.setPromptExtend(promptExtend);
				return this;
			}

			public Builder videoExtension(@Nullable Boolean videoExtension) {
				this.options.setVideoExtension(videoExtension);
				return this;
			}

			public Builder duration(@Nullable Integer duration) {
				this.options.setDuration(duration);
				return this;
			}

			public Builder shotType(@Nullable String shotType) {
				this.options.setShotType(shotType);
				return this;
			}

			public Builder objOrBg(@Nullable List<String> objOrBg) {
				this.options.setObjOrBg(objOrBg);
				return this;
			}

			public Builder maskType(@Nullable String maskType) {
				this.options.setMaskType(maskType);
				return this;
			}

			public Builder expandRatio(@Nullable Double expandRatio) {
				this.options.setExpandRatio(expandRatio);
				return this;
			}

			public Builder topScale(@Nullable Double topScale) {
				this.options.setTopScale(topScale);
				return this;
			}

			public Builder bottomScale(@Nullable Double bottomScale) {
				this.options.setBottomScale(bottomScale);
				return this;
			}

			public Builder leftScale(@Nullable Double leftScale) {
				this.options.setLeftScale(leftScale);
				return this;
			}

			public Builder rightScale(@Nullable Double rightScale) {
				this.options.setRightScale(rightScale);
				return this;
			}

			public Builder mode(@Nullable String mode) {
				this.options.setMode(mode);
				return this;
			}

			public Builder useRefImgBg(@Nullable Boolean useRefImgBg) {
				this.options.setUseRefImgBg(useRefImgBg);
				return this;
			}

			public Builder videoRatio(@Nullable String videoRatio) {
				this.options.setVideoRatio(videoRatio);
				return this;
			}

			public Builder ratio(@Nullable String ratio) {
				this.options.setRatio(ratio);
				return this;
			}

			public Builder styleLevel(@Nullable String styleLevel) {
				this.options.setStyleLevel(styleLevel);
				return this;
			}

			public Builder templateId(@Nullable String templateId) {
				this.options.setTemplateId(templateId);
				return this;
			}

			public Builder eyeMoveFreq(@Nullable Double eyeMoveFreq) {
				this.options.setEyeMoveFreq(eyeMoveFreq);
				return this;
			}

			public Builder videoFps(@Nullable Integer videoFps) {
				this.options.setVideoFps(videoFps);
				return this;
			}

			public Builder mouthMoveStrength(@Nullable Integer mouthMoveStrength) {
				this.options.setMouthMoveStrength(mouthMoveStrength);
				return this;
			}

			public Builder pasteBack(@Nullable Boolean pasteBack) {
				this.options.setPasteBack(pasteBack);
				return this;
			}

			public Builder headMoveStrength(@Nullable Double headMoveStrength) {
				this.options.setHeadMoveStrength(headMoveStrength);
				return this;
			}

			public Builder style(@Nullable Integer style) {
				this.options.setStyle(style);
				return this;
			}

			public Builder seed(@Nullable Long seed) {
				this.options.setSeed(seed);
				return this;
			}

			public Builder from(ParametersOptions fromOptions) {
				this.options.setResolution(fromOptions.getResolution());
				this.options.setSize(fromOptions.getSize());
				this.options.setPromptExtend(fromOptions.getPromptExtend());
				this.options.setVideoExtension(fromOptions.getVideoExtension());
				this.options.setDuration(fromOptions.getDuration());
				this.options.setShotType(fromOptions.getShotType());
				this.options.setObjOrBg(fromOptions.getObjOrBg());
				this.options.setMaskType(fromOptions.getMaskType());
				this.options.setExpandRatio(fromOptions.getExpandRatio());
				this.options.setTopScale(fromOptions.getTopScale());
				this.options.setBottomScale(fromOptions.getBottomScale());
				this.options.setLeftScale(fromOptions.getLeftScale());
				this.options.setRightScale(fromOptions.getRightScale());
				this.options.setMode(fromOptions.getMode());
				this.options.setUseRefImgBg(fromOptions.getUseRefImgBg());
				this.options.setVideoRatio(fromOptions.getVideoRatio());
				this.options.setRatio(fromOptions.getRatio());
				this.options.setStyleLevel(fromOptions.getStyleLevel());
				this.options.setTemplateId(fromOptions.getTemplateId());
				this.options.setEyeMoveFreq(fromOptions.getEyeMoveFreq());
				this.options.setVideoFps(fromOptions.getVideoFps());
				this.options.setMouthMoveStrength(fromOptions.getMouthMoveStrength());
				this.options.setPasteBack(fromOptions.getPasteBack());
				this.options.setHeadMoveStrength(fromOptions.getHeadMoveStrength());
				this.options.setStyle(fromOptions.getStyle());
				this.options.setSeed(fromOptions.getSeed());
				return this;
			}

			public Builder merge(@Nullable ParametersOptions from) {
				if (from == null) {
					return this;
				}
				if (from.getResolution() != null) {
					this.options.setResolution(from.getResolution());
				}
				if (from.getSize() != null) {
					this.options.setSize(from.getSize());
				}
				if (from.getPromptExtend() != null) {
					this.options.setPromptExtend(from.getPromptExtend());
				}
				if (from.getVideoExtension() != null) {
					this.options.setVideoExtension(from.getVideoExtension());
				}
				if (from.getDuration() != null) {
					this.options.setDuration(from.getDuration());
				}
				if (from.getShotType() != null) {
					this.options.setShotType(from.getShotType());
				}
				if (from.getObjOrBg() != null) {
					this.options.setObjOrBg(from.getObjOrBg());
				}
				if (from.getMaskType() != null) {
					this.options.setMaskType(from.getMaskType());
				}
				if (from.getExpandRatio() != null) {
					this.options.setExpandRatio(from.getExpandRatio());
				}
				if (from.getTopScale() != null) {
					this.options.setTopScale(from.getTopScale());
				}
				if (from.getBottomScale() != null) {
					this.options.setBottomScale(from.getBottomScale());
				}
				if (from.getLeftScale() != null) {
					this.options.setLeftScale(from.getLeftScale());
				}
				if (from.getRightScale() != null) {
					this.options.setRightScale(from.getRightScale());
				}
				if (from.getMode() != null) {
					this.options.setMode(from.getMode());
				}
				if (from.getUseRefImgBg() != null) {
					this.options.setUseRefImgBg(from.getUseRefImgBg());
				}
				if (from.getVideoRatio() != null) {
					this.options.setVideoRatio(from.getVideoRatio());
				}
				if (from.getRatio() != null) {
					this.options.setRatio(from.getRatio());
				}
				if (from.getStyleLevel() != null) {
					this.options.setStyleLevel(from.getStyleLevel());
				}
				if (from.getTemplateId() != null) {
					this.options.setTemplateId(from.getTemplateId());
				}
				if (from.getEyeMoveFreq() != null) {
					this.options.setEyeMoveFreq(from.getEyeMoveFreq());
				}
				if (from.getVideoFps() != null) {
					this.options.setVideoFps(from.getVideoFps());
				}
				if (from.getMouthMoveStrength() != null) {
					this.options.setMouthMoveStrength(from.getMouthMoveStrength());
				}
				if (from.getPasteBack() != null) {
					this.options.setPasteBack(from.getPasteBack());
				}
				if (from.getHeadMoveStrength() != null) {
					this.options.setHeadMoveStrength(from.getHeadMoveStrength());
				}
				if (from.getStyle() != null) {
					this.options.setStyle(from.getStyle());
				}
				if (from.getSeed() != null) {
					this.options.setSeed(from.getSeed());
				}
				return this;
			}

			public ParametersOptions build() {
				return this.options;
			}
		}
	}

}
