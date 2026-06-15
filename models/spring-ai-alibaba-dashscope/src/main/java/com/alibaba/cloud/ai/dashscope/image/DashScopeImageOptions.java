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

import java.util.Arrays;
import java.util.Objects;

import com.alibaba.cloud.ai.dashscope.spec.DashScopeApiSpec.InvokeMode;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jspecify.annotations.Nullable;
import org.springframework.ai.image.ImageOptions;

/**
 * @author nuocheng.lxm
 * @author yuluo
 * @author Polaris
 * @author guanxu
 * @since 2024/8/16 11:29
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DashScopeImageOptions implements ImageOptions {

    /**
     * The model to use for image generation.
     */
    @JsonProperty("model")
    private final @Nullable String model;

    /**
     * The number of images to generate. Must be between 1 and 4.
     */
    @JsonProperty("n")
    private final @Nullable Integer n;

    /**
     * The width of the generated images. Must be one of 720, 1024, 1280
     */
    @JsonProperty("width")
    private final @Nullable Integer width;

    /**
     * The height of the generated images. Must be one of 720, 1024, 1280
     */
    @JsonProperty("height")
    private final @Nullable Integer height;

    /**
     * The size of the generated images. Must be one of 1024*1024, 720*1280, 1280*720
     */
    @JsonProperty("size")
    private final @Nullable String size;

    /**
     * The style of the generated images.Must be one of <photography>,<portrait>,<3d cartoon>,<anime>,
     * <oil painting>,<watercolor>,<sketch>,<chinese painting> <flat illustration>,<auto>
     */
    @JsonProperty("style")
    private final @Nullable String style;

    /**
     * Sets the random number seed to use for generation. Must be between 0 and 4294967290.
     */
    @JsonProperty("seed")
    private final @Nullable Integer seed;

    /**
     * refer image,Support jpg, png, tiff, webp
     */
    @JsonProperty("ref_img")
    private final @Nullable String refImg;

    /**
     * refer strength,Must be between 0.0 and 1.0
     */
    @JsonProperty("ref_strength")
    private final @Nullable Float refStrength;

    /**
     * The format in which the generated images are returned. Must be one of url or b64_json.
     */
    @JsonProperty("response_format")
    private final @Nullable String responseFormat;

    /**
     * refer mode,Must be one of repaint,refonly
     */
    @JsonProperty("ref_mode")
    private final @Nullable String refMode;

    @JsonProperty("negative_prompt")
    private final @Nullable String negativePrompt;

    @JsonProperty("prompt_extend")
    private final @Nullable Boolean promptExtend;

    @JsonProperty("watermark")
    private final @Nullable Boolean watermark;

    @JsonProperty("function")
    private final @Nullable String function;

    @JsonProperty("base_image_url")
    private final @Nullable String baseImageUrl;

    @JsonProperty("mask_image_url")
    private final @Nullable String maskImageUrl;

    @JsonProperty("sketch_image_url")
    private final @Nullable String sketchImageUrl;

    @JsonProperty("sketch_weight")
    private final @Nullable Integer sketchWeight;

    @JsonProperty("sketch_extraction")
    private final @Nullable Boolean sketchExtraction;

    @JsonProperty("sketch_color")
    private final Integer @Nullable [][] sketchColor;

    @JsonProperty("mask_color")
    private final Integer @Nullable [][] maskColor;

    @JsonProperty("max_images")
    private final @Nullable Integer maxImages;

    @JsonProperty("enable_interleave")
    private final @Nullable Boolean enableInterleave;

    /**
     * Invocation mode for the API call.
     * - AUTO: automatically choose based on model defaults (backward compatible)
     * - SYNC: synchronous call (no async header)
     * - ASYNC: asynchronous call (with async header, returns task_id for polling)
     * Note: If model doesn't support sync, will auto-downgrade to async with WARN log.
     */
    @JsonIgnore
    private final InvokeMode invokeMode;

    /**
     * Output aspect ratio for out-painting, e.g. "4:3".
     */
    @JsonProperty("output_ratio")
    private final @Nullable String outputRatio;

    /**
     * Horizontal expansion scale for out-painting.
     */
    @JsonProperty("x_scale")
    private final @Nullable Float xScale;

    /**
     * Vertical expansion scale for out-painting.
     */
    @JsonProperty("y_scale")
    private final @Nullable Float yScale;

    /**
     * Rotation angle in degrees for out-painting.
     */
    @JsonProperty("angle")
    private final @Nullable Integer angle;

    /**
     * Left expansion in pixels for out-painting.
     */
    @JsonProperty("left_offset")
    private final @Nullable Integer leftOffset;

    /**
     * Right expansion in pixels for out-painting.
     */
    @JsonProperty("right_offset")
    private final @Nullable Integer rightOffset;

    /**
     * Top expansion in pixels for out-painting.
     */
    @JsonProperty("top_offset")
    private final @Nullable Integer topOffset;

    /**
     * Bottom expansion in pixels for out-painting.
     */
    @JsonProperty("bottom_offset")
    private final @Nullable Integer bottomOffset;

    /**
     * Whether to use best quality mode for out-painting.
     */
    @JsonProperty("best_quality")
    private final @Nullable Boolean bestQuality;

    /**
     * Whether to limit output image size for out-painting.
     */
    @JsonProperty("limit_image_size")
    private final @Nullable Boolean limitImageSize;

    protected DashScopeImageOptions(
            @Nullable String model,
            @Nullable Integer n,
            @Nullable Integer width,
            @Nullable Integer height,
            @Nullable String size,
            @Nullable String style,
            @Nullable Integer seed,
            @Nullable String refImg,
            @Nullable Float refStrength,
            @Nullable String responseFormat,
            @Nullable String refMode,
            @Nullable String negativePrompt,
            @Nullable Boolean promptExtend,
            @Nullable Boolean watermark,
            @Nullable String function,
            @Nullable String baseImageUrl,
            @Nullable String maskImageUrl,
            @Nullable String sketchImageUrl,
            @Nullable Integer sketchWeight,
            @Nullable Boolean sketchExtraction,
            Integer @Nullable [][] sketchColor,
            Integer @Nullable [][] maskColor,
            @Nullable Integer maxImages,
            @Nullable Boolean enableInterleave,
            @Nullable InvokeMode invokeMode,
            @Nullable String outputRatio,
            @Nullable Float xScale,
            @Nullable Float yScale,
            @Nullable Integer angle,
            @Nullable Integer leftOffset,
            @Nullable Integer rightOffset,
            @Nullable Integer topOffset,
            @Nullable Integer bottomOffset,
            @Nullable Boolean bestQuality,
            @Nullable Boolean limitImageSize) {
        this.model = model;
        this.n = n;
        this.width = width;
        this.height = height;
        this.size = size;
        this.style = style;
        this.seed = seed;
        this.refImg = refImg;
        this.refStrength = refStrength;
        this.responseFormat = responseFormat;
        this.refMode = refMode;
        this.negativePrompt = negativePrompt;
        this.promptExtend = promptExtend;
        this.watermark = watermark;
        this.function = function;
        this.baseImageUrl = baseImageUrl;
        this.maskImageUrl = maskImageUrl;
        this.sketchImageUrl = sketchImageUrl;
        this.sketchWeight = sketchWeight;
        this.sketchExtraction = sketchExtraction;
        this.sketchColor = sketchColor;
        this.maskColor = maskColor;
        this.maxImages = maxImages;
        this.enableInterleave = enableInterleave;
        this.invokeMode = invokeMode != null ? invokeMode : InvokeMode.AUTO;
        this.outputRatio = outputRatio;
        this.xScale = xScale;
        this.yScale = yScale;
        this.angle = angle;
        this.leftOffset = leftOffset;
        this.rightOffset = rightOffset;
        this.topOffset = topOffset;
        this.bottomOffset = bottomOffset;
        this.bestQuality = bestQuality;
        this.limitImageSize = limitImageSize;
    }

    @Override
    public @Nullable Integer getN() {
        return this.n;
    }

    @Override
    public @Nullable String getModel() {
        return this.model;
    }

    @Override
    public @Nullable Integer getWidth() {
        return this.width;
    }

    @Override
    public @Nullable Integer getHeight() {
        return this.height;
    }

    @Override
    public @Nullable String getResponseFormat() {
        return this.responseFormat;
    }

    @Override
    public @Nullable String getStyle() {
        return this.style;
    }

    public @Nullable String getSize() {
        if (this.size != null) {
            return this.size;
        }
        return (this.width != null && this.height != null) ? this.width + "*" + this.height : null;
    }

    public @Nullable Integer getSeed() {
        return this.seed;
    }

    public @Nullable String getRefImg() {
        return this.refImg;
    }

    public @Nullable Float getRefStrength() {
        return this.refStrength;
    }

    public @Nullable String getRefMode() {
        return this.refMode;
    }

    public @Nullable String getNegativePrompt() {
        return this.negativePrompt;
    }

    public @Nullable Boolean getPromptExtend() {
        return this.promptExtend;
    }

    public @Nullable Boolean getWatermark() {
        return this.watermark;
    }

    public @Nullable String getFunction() {
        return this.function;
    }

    public @Nullable String getBaseImageUrl() {
        return this.baseImageUrl;
    }

    public @Nullable String getMaskImageUrl() {
        return this.maskImageUrl;
    }

    public @Nullable String getSketchImageUrl() {
        return this.sketchImageUrl;
    }

    public @Nullable Integer getSketchWeight() {
        return this.sketchWeight;
    }

    public @Nullable Boolean getSketchExtraction() {
        return this.sketchExtraction;
    }

    public Integer @Nullable [][] getSketchColor() {
        return this.sketchColor;
    }

    public Integer @Nullable [][] getMaskColor() {
        return this.maskColor;
    }

    public @Nullable Integer getMaxImages() {
        return this.maxImages;
    }

    public @Nullable Boolean getEnableInterleave() {
        return this.enableInterleave;
    }

    public InvokeMode getInvokeMode() {
        return this.invokeMode;
    }

    public @Nullable String getOutputRatio() {
        return this.outputRatio;
    }

    public @Nullable Float getXScale() {
        return this.xScale;
    }

    public @Nullable Float getYScale() {
        return this.yScale;
    }

    public @Nullable Integer getAngle() {
        return this.angle;
    }

    public @Nullable Integer getLeftOffset() {
        return this.leftOffset;
    }

    public @Nullable Integer getRightOffset() {
        return this.rightOffset;
    }

    public @Nullable Integer getTopOffset() {
        return this.topOffset;
    }

    public @Nullable Integer getBottomOffset() {
        return this.bottomOffset;
    }

    public @Nullable Boolean getBestQuality() {
        return this.bestQuality;
    }

    public @Nullable Boolean getLimitImageSize() {
        return this.limitImageSize;
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
        DashScopeImageOptions that = (DashScopeImageOptions) o;
        return Objects.equals(this.model, that.model) && Objects.equals(this.n, that.n)
                && Objects.equals(this.width, that.width) && Objects.equals(this.height, that.height)
                && Objects.equals(this.size, that.size) && Objects.equals(this.style, that.style)
                && Objects.equals(this.seed, that.seed) && Objects.equals(this.refImg, that.refImg)
                && Objects.equals(this.refStrength, that.refStrength)
                && Objects.equals(this.responseFormat, that.responseFormat)
                && Objects.equals(this.refMode, that.refMode)
                && Objects.equals(this.negativePrompt, that.negativePrompt)
                && Objects.equals(this.promptExtend, that.promptExtend)
                && Objects.equals(this.watermark, that.watermark) && Objects.equals(this.function, that.function)
                && Objects.equals(this.baseImageUrl, that.baseImageUrl)
                && Objects.equals(this.maskImageUrl, that.maskImageUrl)
                && Objects.equals(this.sketchImageUrl, that.sketchImageUrl)
                && Objects.equals(this.sketchWeight, that.sketchWeight)
                && Objects.equals(this.sketchExtraction, that.sketchExtraction)
                && Objects.deepEquals(this.sketchColor, that.sketchColor)
                && Objects.deepEquals(this.maskColor, that.maskColor) && Objects.equals(this.maxImages, that.maxImages)
                && Objects.equals(this.enableInterleave, that.enableInterleave)
                && Objects.equals(this.invokeMode, that.invokeMode)
                && Objects.equals(this.outputRatio, that.outputRatio) && Objects.equals(this.xScale, that.xScale)
                && Objects.equals(this.yScale, that.yScale) && Objects.equals(this.angle, that.angle)
                && Objects.equals(this.leftOffset, that.leftOffset)
                && Objects.equals(this.rightOffset, that.rightOffset) && Objects.equals(this.topOffset, that.topOffset)
                && Objects.equals(this.bottomOffset, that.bottomOffset)
                && Objects.equals(this.bestQuality, that.bestQuality)
                && Objects.equals(this.limitImageSize, that.limitImageSize);
    }

    // @formatter:off
    @Override
    public int hashCode() {
        return Objects.hash(this.model, this.n, this.width, this.height, this.size, this.style, this.seed, this.refImg,
                this.refStrength, this.responseFormat, this.refMode, this.negativePrompt, this.promptExtend,
                this.watermark, this.function, this.baseImageUrl, this.maskImageUrl, this.sketchImageUrl,
                this.sketchWeight, this.sketchExtraction, Arrays.deepHashCode(this.sketchColor),
                Arrays.deepHashCode(this.maskColor), this.maxImages, this.enableInterleave, this.invokeMode,
                this.outputRatio, this.xScale, this.yScale, this.angle, this.leftOffset, this.rightOffset,
                this.topOffset, this.bottomOffset, this.bestQuality, this.limitImageSize);
    }
    // @formatter:on

    @Override
    public String toString() {
        return "DashScopeImageOptions{" + "model='" + this.model + '\'' + ", n=" + this.n + ", width=" + this.width
                + ", height=" + this.height + ", size='" + this.size + '\'' + ", style='" + this.style + '\''
                + ", seed=" + this.seed + ", refImg='" + this.refImg + '\'' + ", refStrength=" + this.refStrength
                + ", responseFormat='" + this.responseFormat + '\'' + ", refMode='" + this.refMode + '\''
                + ", negativePrompt='" + this.negativePrompt + '\'' + ", promptExtend=" + this.promptExtend
                + ", watermark=" + this.watermark + ", function='" + this.function + '\'' + ", baseImageUrl='"
                + this.baseImageUrl + '\'' + ", maskImageUrl='" + this.maskImageUrl + '\'' + ", sketchImageUrl='"
                + this.sketchImageUrl + '\'' + ", sketchWeight=" + this.sketchWeight + ", sketchExtraction="
                + this.sketchExtraction + ", sketchColor=" + Arrays.toString(this.sketchColor) + ", maskColor="
                + Arrays.toString(this.maskColor) + ", maxImages=" + this.maxImages + ", enableInterleave="
                + this.enableInterleave + ", invokeMode=" + this.invokeMode + ", outputRatio='" + this.outputRatio
                + '\'' + ", xScale=" + this.xScale + ", yScale=" + this.yScale + ", angle=" + this.angle
                + ", leftOffset=" + this.leftOffset + ", rightOffset=" + this.rightOffset + ", topOffset="
                + this.topOffset + ", bottomOffset=" + this.bottomOffset + ", bestQuality=" + this.bestQuality
                + ", limitImageSize=" + this.limitImageSize + '}';
    }

    public static final class Builder extends AbstractBuilder<DashScopeImageOptions, Builder> {

        private Builder() {
        }

        public Builder from(DashScopeImageOptions fromOptions) {
            this.model = fromOptions.getModel();
            this.n = fromOptions.getN();
            this.width = fromOptions.getWidth();
            this.height = fromOptions.getHeight();
            this.size = fromOptions.getSize();
            this.style = fromOptions.getStyle();
            this.responseFormat = fromOptions.getResponseFormat();
            this.seed = fromOptions.getSeed();
            this.refImg = fromOptions.getRefImg();
            this.refStrength = fromOptions.getRefStrength();
            this.refMode = fromOptions.getRefMode();
            this.negativePrompt = fromOptions.getNegativePrompt();
            this.promptExtend = fromOptions.getPromptExtend();
            this.watermark = fromOptions.getWatermark();
            this.function = fromOptions.getFunction();
            this.baseImageUrl = fromOptions.getBaseImageUrl();
            this.maskImageUrl = fromOptions.getMaskImageUrl();
            this.sketchImageUrl = fromOptions.getSketchImageUrl();
            this.sketchWeight = fromOptions.getSketchWeight();
            this.sketchExtraction = fromOptions.getSketchExtraction();
            this.sketchColor = fromOptions.getSketchColor();
            this.maskColor = fromOptions.getMaskColor();
            this.maxImages = fromOptions.getMaxImages();
            this.enableInterleave = fromOptions.getEnableInterleave();
            this.invokeMode = fromOptions.getInvokeMode();
            this.outputRatio = fromOptions.getOutputRatio();
            this.xScale = fromOptions.getXScale();
            this.yScale = fromOptions.getYScale();
            this.angle = fromOptions.getAngle();
            this.leftOffset = fromOptions.getLeftOffset();
            this.rightOffset = fromOptions.getRightOffset();
            this.topOffset = fromOptions.getTopOffset();
            this.bottomOffset = fromOptions.getBottomOffset();
            this.bestQuality = fromOptions.getBestQuality();
            this.limitImageSize = fromOptions.getLimitImageSize();
            return this;
        }

        public Builder merge(@Nullable ImageOptions from) {
            if (from == null) {
                return this;
            }
            if (from.getModel() != null) {
                this.model = from.getModel();
            }
            if (from.getN() != null) {
                this.n = from.getN();
            }
            if (from.getWidth() != null) {
                this.width = from.getWidth();
            }
            if (from.getHeight() != null) {
                this.height = from.getHeight();
            }
            if (from.getResponseFormat() != null) {
                this.responseFormat = from.getResponseFormat();
            }
            if (from.getStyle() != null) {
                this.style = from.getStyle();
            }
            if (from instanceof DashScopeImageOptions castFrom) {
                if (castFrom.getSize() != null) {
                    this.size = castFrom.getSize();
                }
                if (castFrom.getSeed() != null) {
                    this.seed = castFrom.getSeed();
                }
                if (castFrom.getRefImg() != null) {
                    this.refImg = castFrom.getRefImg();
                }
                if (castFrom.getRefStrength() != null) {
                    this.refStrength = castFrom.getRefStrength();
                }
                if (castFrom.getRefMode() != null) {
                    this.refMode = castFrom.getRefMode();
                }
                if (castFrom.getNegativePrompt() != null) {
                    this.negativePrompt = castFrom.getNegativePrompt();
                }
                if (castFrom.getPromptExtend() != null) {
                    this.promptExtend = castFrom.getPromptExtend();
                }
                if (castFrom.getWatermark() != null) {
                    this.watermark = castFrom.getWatermark();
                }
                if (castFrom.getFunction() != null) {
                    this.function = castFrom.getFunction();
                }
                if (castFrom.getBaseImageUrl() != null) {
                    this.baseImageUrl = castFrom.getBaseImageUrl();
                }
                if (castFrom.getMaskImageUrl() != null) {
                    this.maskImageUrl = castFrom.getMaskImageUrl();
                }
                if (castFrom.getSketchImageUrl() != null) {
                    this.sketchImageUrl = castFrom.getSketchImageUrl();
                }
                if (castFrom.getSketchWeight() != null) {
                    this.sketchWeight = castFrom.getSketchWeight();
                }
                if (castFrom.getSketchExtraction() != null) {
                    this.sketchExtraction = castFrom.getSketchExtraction();
                }
                if (castFrom.getSketchColor() != null) {
                    this.sketchColor = castFrom.getSketchColor();
                }
                if (castFrom.getMaskColor() != null) {
                    this.maskColor = castFrom.getMaskColor();
                }
                if (castFrom.getMaxImages() != null) {
                    this.maxImages = castFrom.getMaxImages();
                }
                if (castFrom.getEnableInterleave() != null) {
                    this.enableInterleave = castFrom.getEnableInterleave();
                }
                if (castFrom.getInvokeMode() != null) {
                    this.invokeMode = castFrom.getInvokeMode();
                }
                if (castFrom.getOutputRatio() != null) {
                    this.outputRatio = castFrom.getOutputRatio();
                }
                if (castFrom.getXScale() != null) {
                    this.xScale = castFrom.getXScale();
                }
                if (castFrom.getYScale() != null) {
                    this.yScale = castFrom.getYScale();
                }
                if (castFrom.getAngle() != null) {
                    this.angle = castFrom.getAngle();
                }
                if (castFrom.getLeftOffset() != null) {
                    this.leftOffset = castFrom.getLeftOffset();
                }
                if (castFrom.getRightOffset() != null) {
                    this.rightOffset = castFrom.getRightOffset();
                }
                if (castFrom.getTopOffset() != null) {
                    this.topOffset = castFrom.getTopOffset();
                }
                if (castFrom.getBottomOffset() != null) {
                    this.bottomOffset = castFrom.getBottomOffset();
                }
                if (castFrom.getBestQuality() != null) {
                    this.bestQuality = castFrom.getBestQuality();
                }
                if (castFrom.getLimitImageSize() != null) {
                    this.limitImageSize = castFrom.getLimitImageSize();
                }
            }
            return this;
        }

        // @formatter:off
        public DashScopeImageOptions build() {
            return new DashScopeImageOptions(this.model, this.n, this.width, this.height, this.size, this.style,
                    this.seed, this.refImg, this.refStrength, this.responseFormat, this.refMode, this.negativePrompt,
                    this.promptExtend, this.watermark, this.function, this.baseImageUrl, this.maskImageUrl,
                    this.sketchImageUrl, this.sketchWeight, this.sketchExtraction, this.sketchColor, this.maskColor,
                    this.maxImages, this.enableInterleave, this.invokeMode, this.outputRatio, this.xScale, this.yScale,
                    this.angle, this.leftOffset, this.rightOffset, this.topOffset, this.bottomOffset, this.bestQuality,
                    this.limitImageSize);
        }
        // @formatter:on

    }

    protected abstract static class AbstractBuilder<O extends DashScopeImageOptions, B extends AbstractBuilder<O, B>> {

        protected @Nullable String model;

        protected @Nullable Integer n;

        protected @Nullable Integer width;

        protected @Nullable Integer height;

        protected @Nullable String size;

        protected @Nullable String style;

        protected @Nullable String responseFormat;

        protected @Nullable Integer seed;

        protected @Nullable String refImg;

        protected @Nullable Float refStrength;

        protected @Nullable String refMode;

        protected @Nullable String negativePrompt;

        protected @Nullable Boolean promptExtend;

        protected @Nullable Boolean watermark;

        protected @Nullable String function;

        protected @Nullable String baseImageUrl;

        protected @Nullable String maskImageUrl;

        protected @Nullable String sketchImageUrl;

        protected @Nullable Integer sketchWeight;

        protected @Nullable Boolean sketchExtraction;

        protected Integer @Nullable [][] sketchColor;

        protected Integer @Nullable [][] maskColor;

        protected @Nullable Integer maxImages;

        protected @Nullable Boolean enableInterleave;

        protected @Nullable InvokeMode invokeMode;

        protected @Nullable String outputRatio;

        protected @Nullable Float xScale;

        protected @Nullable Float yScale;

        protected @Nullable Integer angle;

        protected @Nullable Integer leftOffset;

        protected @Nullable Integer rightOffset;

        protected @Nullable Integer topOffset;

        protected @Nullable Integer bottomOffset;

        protected @Nullable Boolean bestQuality;

        protected @Nullable Boolean limitImageSize;

        @SuppressWarnings("unchecked")
        protected B self() {
            return (B) this;
        }

        public B model(@Nullable String model) {
            this.model = model;
            return self();
        }

        public B n(@Nullable Integer n) {
            this.n = n;
            return self();
        }

        public B width(@Nullable Integer width) {
            this.width = width;
            if (this.width != null && this.height != null) {
                this.size = this.width + "*" + this.height;
            }
            return self();
        }

        public B height(@Nullable Integer height) {
            this.height = height;
            if (this.width != null && this.height != null) {
                this.size = this.width + "*" + this.height;
            }
            return self();
        }

        public B size(@Nullable String size) {
            this.size = size;
            return self();
        }

        public B style(@Nullable String style) {
            this.style = style;
            return self();
        }

        public B responseFormat(@Nullable String responseFormat) {
            this.responseFormat = responseFormat;
            return self();
        }

        public B seed(@Nullable Integer seed) {
            this.seed = seed;
            return self();
        }

        public B refImg(@Nullable String refImg) {
            this.refImg = refImg;
            return self();
        }

        public B refStrength(@Nullable Float refStrength) {
            this.refStrength = refStrength;
            return self();
        }

        public B refMode(@Nullable String refMode) {
            this.refMode = refMode;
            return self();
        }

        public B negativePrompt(@Nullable String negativePrompt) {
            this.negativePrompt = negativePrompt;
            return self();
        }

        public B promptExtend(@Nullable Boolean promptExtend) {
            this.promptExtend = promptExtend;
            return self();
        }

        public B watermark(@Nullable Boolean watermark) {
            this.watermark = watermark;
            return self();
        }

        public B function(@Nullable String function) {
            this.function = function;
            return self();
        }

        public B baseImageUrl(@Nullable String baseImageUrl) {
            this.baseImageUrl = baseImageUrl;
            return self();
        }

        public B maskImageUrl(@Nullable String maskImageUrl) {
            this.maskImageUrl = maskImageUrl;
            return self();
        }

        public B sketchImageUrl(@Nullable String sketchImageUrl) {
            this.sketchImageUrl = sketchImageUrl;
            return self();
        }

        public B sketchWeight(@Nullable Integer sketchWeight) {
            this.sketchWeight = sketchWeight;
            return self();
        }

        public B sketchExtraction(@Nullable Boolean sketchExtraction) {
            this.sketchExtraction = sketchExtraction;
            return self();
        }

        public B sketchColor(Integer @Nullable [][] sketchColor) {
            this.sketchColor = sketchColor;
            return self();
        }

        public B maskColor(Integer @Nullable [][] maskColor) {
            this.maskColor = maskColor;
            return self();
        }

        public B maxImages(@Nullable Integer maxImages) {
            this.maxImages = maxImages;
            return self();
        }

        public B enableInterleave(@Nullable Boolean enableInterleave) {
            this.enableInterleave = enableInterleave;
            return self();
        }

        public B invokeMode(@Nullable InvokeMode invokeMode) {
            this.invokeMode = invokeMode;
            return self();
        }

        public B outputRatio(@Nullable String outputRatio) {
            this.outputRatio = outputRatio;
            return self();
        }

        public B xScale(@Nullable Float xScale) {
            this.xScale = xScale;
            return self();
        }

        public B yScale(@Nullable Float yScale) {
            this.yScale = yScale;
            return self();
        }

        public B angle(@Nullable Integer angle) {
            this.angle = angle;
            return self();
        }

        public B leftOffset(@Nullable Integer leftOffset) {
            this.leftOffset = leftOffset;
            return self();
        }

        public B rightOffset(@Nullable Integer rightOffset) {
            this.rightOffset = rightOffset;
            return self();
        }

        public B topOffset(@Nullable Integer topOffset) {
            this.topOffset = topOffset;
            return self();
        }

        public B bottomOffset(@Nullable Integer bottomOffset) {
            this.bottomOffset = bottomOffset;
            return self();
        }

        public B bestQuality(@Nullable Boolean bestQuality) {
            this.bestQuality = bestQuality;
            return self();
        }

        public B limitImageSize(@Nullable Boolean limitImageSize) {
            this.limitImageSize = limitImageSize;
            return self();
        }

        public abstract O build();

    }

}
