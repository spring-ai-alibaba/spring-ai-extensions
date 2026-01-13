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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Arrays;
import org.springframework.ai.image.ImageOptions;

/**
 * @author nuocheng.lxm
 * @author yuluo
 * @author Polaris
 * @since 2024/8/16 11:29
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DashScopeImageOptions implements ImageOptions {

  /** The model to use for image generation. */
  @JsonProperty("model")
  private String model;

  /** The number of images to generate. Must be between 1 and 4. */
  @JsonProperty("n")
  private Integer n;

  /** The width of the generated images. Must be one of 720, 1024, 1280 */
  @JsonProperty("width")
  private Integer width;

  /** The height of the generated images. Must be one of 720, 1024, 1280 */
  @JsonProperty("height")
  private Integer height;

  /** The size of the generated images. Must be one of 1024*1024, 720*1280, 1280*720 */
  @JsonProperty("size")
  private String size;

  /**
   * The style of the generated images.Must be one of <photography>,<portrait>,<3d cartoon>,<anime>,
   * <oil painting>,<watercolor>,<sketch>,<chinese painting> <flat illustration>,<auto>
   */
  @JsonProperty("style")
  private String style;

  /** Sets the random number seed to use for generation. Must be between 0 and 4294967290. */
  @JsonProperty("seed")
  private Integer seed;

  /** refer image,Support jpg, png, tiff, webp */
  @JsonProperty("ref_img")
  private String refImg;

  /** refer strength,Must be between 0.0 and 1.0 */
  @JsonProperty("ref_strength")
  private Float refStrength;

  /** The format in which the generated images are returned. Must be one of url or b64_json. */
  @JsonProperty("response_format")
  private String responseFormat;

  /** refer mode,Must be one of repaint,refonly */
  @JsonProperty("ref_mode")
  private String refMode;

  @JsonProperty("negative_prompt")
  private String negativePrompt;

  @JsonProperty("prompt_extend")
  private Boolean promptExtend;

  @JsonProperty("watermark")
  private Boolean watermark;

  @JsonProperty("function")
  private String function;

  @JsonProperty("base_image_url")
  private String baseImageUrl;

  @JsonProperty("mask_image_url")
  private String maskImageUrl;

  @JsonProperty("sketch_image_url")
  private String sketchImageUrl;

  @JsonProperty("sketch_weight")
  private Integer sketchWeight;

  @JsonProperty("sketch_extraction")
  private Boolean sketchExtraction;

  @JsonProperty("sketch_color")
  private Integer[][] sketchColor;

  @JsonProperty("mask_color")
  private Integer[][] maskColor;

  public Boolean getPromptExtend() {
    return promptExtend;
  }

  public void setPromptExtend(Boolean promptExtend) {
    this.promptExtend = promptExtend;
  }

  public Boolean getWatermark() {
    return watermark;
  }

  public void setWatermark(Boolean watermark) {
    this.watermark = watermark;
  }

  public String getFunction() {
    return function;
  }

  public void setFunction(String function) {
    this.function = function;
  }

  public String getBaseImageUrl() {
    return baseImageUrl;
  }

  public void setBaseImageUrl(String baseImageUrl) {
    this.baseImageUrl = baseImageUrl;
  }

  public String getMaskImageUrl() {
    return maskImageUrl;
  }

  public void setMaskImageUrl(String maskImageUrl) {
    this.maskImageUrl = maskImageUrl;
  }

  public String getSketchImageUrl() {
    return sketchImageUrl;
  }

  public void setSketchImageUrl(String sketchImageUrl) {
    this.sketchImageUrl = sketchImageUrl;
  }

  public Integer getSketchWeight() {
    return sketchWeight;
  }

  public void setSketchWeight(Integer sketchWeight) {
    this.sketchWeight = sketchWeight;
  }

  public Boolean getSketchExtraction() {
    return sketchExtraction;
  }

  public void setSketchExtraction(Boolean sketchExtraction) {
    this.sketchExtraction = sketchExtraction;
  }

  public Integer[][] getSketchColor() {
    return sketchColor;
  }

  public void setSketchColor(Integer[][] sketchColor) {
    this.sketchColor = sketchColor;
  }

  public Integer[][] getMaskColor() {
    return maskColor;
  }

  public void setMaskColor(Integer[][] maskColor) {
    this.maskColor = maskColor;
  }

  public static Builder builder() {
    return new Builder();
  }

  @Override
  public Integer getN() {
    return this.n;
  }

  public void setN(Integer n) {
    this.n = n;
  }

  @Override
  public String getModel() {
    return this.model;
  }

  public void setModel(String model) {
    this.model = model;
  }

  @Override
  public Integer getWidth() {
    return this.width;
  }

  public void setWidth(Integer width) {
    this.width = width;
    this.size = this.width + "*" + this.height;
  }

  @Override
  public Integer getHeight() {
    return this.height;
  }

  public void setHeight(Integer height) {
    this.height = height;
    this.size = this.width + "*" + this.height;
  }

  @Override
  public String getResponseFormat() {
    return this.responseFormat;
  }

  @Override
  public String getStyle() {
    return this.style;
  }

  public void setStyle(String style) {
    this.style = style;
  }

  public String getSize() {

    if (this.size != null) {
      return this.size;
    }
    return (this.width != null && this.height != null) ? this.width + "*" + this.height : null;
  }

  @Deprecated
  public void setSize(String size) {
    this.size = size;
  }

  public Integer getSeed() {
    return seed;
  }

  public void setSeed(Integer seed) {
    this.seed = seed;
  }

  public String getRefImg() {
    return refImg;
  }

  public void setRefImg(String refImg) {
    this.refImg = refImg;
  }

  public Float getRefStrength() {
    return refStrength;
  }

  public void setRefStrength(Float refStrength) {
    this.refStrength = refStrength;
  }

  public String getRefMode() {
    return refMode;
  }

  public void setRefMode(String refMode) {
    this.refMode = refMode;
  }

  public String getNegativePrompt() {
    return negativePrompt;
  }

  public void setNegativePrompt(String negativePrompt) {
    this.negativePrompt = negativePrompt;
  }

  @Override
  public String toString() {
    return "DashScopeImageOptions{" + "model='" + this.model + '\'' + ", n=" + this.n + ", width=" + this.width
        + ", height=" + this.height + ", size='" + this.size + '\'' + ", style='" + this.style + '\'' + ", seed="
        + this.seed + ", refImg='" + this.refImg + '\'' + ", refStrength=" + this.refStrength + ", responseFormat='"
        + this.responseFormat + '\'' + ", refMode='" + this.refMode + '\'' + ", negativePrompt='"
        + this.negativePrompt + '\'' + ", promptExtend=" + this.promptExtend + ", watermark=" + this.watermark
        + ", function='" + this.function + '\'' + ", baseImageUrl='" + this.baseImageUrl + '\'' + ", maskImageUrl='"
        + this.maskImageUrl + '\'' + ", sketchImageUrl='" + this.sketchImageUrl + '\'' + ", sketchWeight="
        + this.sketchWeight + ", sketchExtraction=" + this.sketchExtraction + ", sketchColor="
        + Arrays.toString(this.sketchColor) + ", maskColor=" + Arrays.toString(this.maskColor) + '}';
  }

  public static class Builder {

    private final DashScopeImageOptions options;

    private Builder() {
      this.options = new DashScopeImageOptions();
    }

    public Builder n(Integer n) {
      options.setN(n);
      return this;
    }

    @Deprecated
    public Builder withN(Integer n) {
      return n(n);
    }

    public Builder model(String model) {
      options.setModel(model);
      return this;
    }

    @Deprecated
    public Builder withModel(String model) {
      return model(model);
    }

    public Builder width(Integer width) {
      options.setWidth(width);
      return this;
    }

    @Deprecated
    public Builder withWidth(Integer width) {
      return width(width);
    }

    public Builder height(Integer height) {
      options.setHeight(height);
      return this;
    }

    @Deprecated
    public Builder withHeight(Integer height) {
      return height(height);
    }

    public Builder style(String style) {
      options.setStyle(style);
      return this;
    }

    @Deprecated
    public Builder withStyle(String style) {
      return style(style);
    }

    public Builder seed(Integer seed) {
      options.setSeed(seed);
      return this;
    }

    @Deprecated
    public Builder withSeed(Integer seed) {
      options.setSeed(seed);
      return this;
    }

    public Builder refImg(String refImg) {
      options.setRefImg(refImg);
      return this;
    }

    @Deprecated
    public Builder withRefImg(String refImg) {
      return refImg(refImg);
    }

    public Builder refStrength(Float refStrength) {
      options.setRefStrength(refStrength);
      return this;
    }

    @Deprecated
    public Builder withRefStrength(Float refStrength) {
      return refStrength(refStrength);
    }

    public Builder refMode(String refMode) {
      options.setRefMode(refMode);
      return this;
    }

    @Deprecated
    public Builder withRefMode(String refMode) {
      return refMode(refMode);
    }

    @Deprecated
    public Builder withSize(String size) {
      options.setSize(size);
      return this;
    }

    public Builder negativePrompt(String negativePrompt) {
      options.setNegativePrompt(negativePrompt);
      return this;
    }

    @Deprecated
    public Builder withNegativePrompt(String negativePrompt) {
      return negativePrompt(negativePrompt);
    }

    public Builder promptExtend(Boolean promptExtend) {
      this.options.promptExtend = promptExtend;
      return this;
    }

    @Deprecated
    public Builder withPromptExtend(Boolean promptExtend) {
      return promptExtend(promptExtend);
    }

    public Builder watermark(Boolean watermark) {
      this.options.watermark = watermark;
      return this;
    }

    @Deprecated
    public Builder withWatermark(Boolean watermark) {
      return watermark(watermark);
    }

    public Builder function(String function) {
      this.options.function = function;
      return this;
    }

    @Deprecated
    public Builder withFunction(String function) {
      return function(function);
    }

    public Builder baseImageUrl(String baseImageUrl) {
      this.options.baseImageUrl = baseImageUrl;
      return this;
    }

    @Deprecated
    public Builder withBaseImageUrl(String baseImageUrl) {
      return baseImageUrl(baseImageUrl);
    }

    public Builder maskImageUrl(String maskImageUrl) {
      this.options.maskImageUrl = maskImageUrl;
      return this;
    }

    @Deprecated
    public Builder withMaskImageUrl(String maskImageUrl) {
      return maskImageUrl(maskImageUrl);
    }

    public Builder sketchImageUrl(String sketchImageUrl) {
      this.options.sketchImageUrl = sketchImageUrl;
      return this;
    }

    @Deprecated
    public Builder withSketchImageUrl(String sketchImageUrl) {
      return sketchImageUrl(sketchImageUrl);
    }

    public Builder sketchWeight(Integer sketchWeight) {
      this.options.sketchWeight = sketchWeight;
      return this;
    }

    @Deprecated
    public Builder withSketchWeight(Integer sketchWeight) {
      return sketchWeight(sketchWeight);
    }

    public Builder sketchExtraction(Boolean sketchExtraction) {
      this.options.sketchExtraction = sketchExtraction;
      return this;
    }

    @Deprecated
    public Builder withSketchExtraction(Boolean sketchExtraction) {
      return sketchExtraction(sketchExtraction);
    }

    public Builder sketchColor(Integer[][] sketchColor) {
      this.options.sketchColor = sketchColor;
      return this;
    }

    @Deprecated
    public Builder withSketchColor(Integer[][] sketchColor) {
      return sketchColor(sketchColor);
    }

    public Builder maskColor(Integer[][] maskColor) {
      this.options.maskColor = maskColor;
      return this;
    }

    @Deprecated
    public Builder withMaskColor(Integer[][] maskColor) {
      return maskColor(maskColor);
    }

    public Builder responseFormat(String responseFormat) {
      this.options.responseFormat = responseFormat;
      return this;
    }

    @Deprecated
    public Builder withResponseFormat(String responseFormat) {
      return responseFormat(responseFormat);
    }

    public DashScopeImageOptions build() {
      return options;
    }
  }
}
