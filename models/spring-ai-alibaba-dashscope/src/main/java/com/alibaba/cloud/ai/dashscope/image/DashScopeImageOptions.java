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
import java.util.Arrays;
import java.util.List;

import com.alibaba.cloud.ai.dashscope.image.DashScopeImageApiSpec.ImageRequest;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageApiSpec.ImageRequest.ColorPaletteItem;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageApiSpec.ImageRequest.Element;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageApiSpec.ImageRequest.ReferenceEdge;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageApiSpec.ImageRequest.Resource;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageApiSpec.InvokeMode;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageApiSpec.Message;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageApiSpec.RequestType;
import com.alibaba.cloud.ai.dashscope.spec.DashScopeModel.ImageModel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.ai.image.ImageOptions;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

/**
 * @author nuocheng.lxm
 * @author yuluo
 * @author Polaris
 * @author xuguan
 * @since 2024/8/16 11:29
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DashScopeImageOptions implements ImageOptions {

    /**
     * The model to use for image generation.
     */
    @JsonProperty("model")
    private String model;

    /**
     * The number of images to generate. Must be between 1 and 4.
     */
    @JsonProperty("n")
    private Integer n;

    /**
     * The width of the generated images. Must be one of 720, 1024, 1280
     */
    @JsonProperty("width")
    private Integer width;

    /**
     * The height of the generated images. Must be one of 720, 1024, 1280
     */
    @JsonProperty("height")
    private Integer height;

    /**
     * The size of the generated images. Must be one of 1024*1024, 720*1280, 1280*720
     */
    @JsonProperty("size")
    private String size;

    /**
     * The style of the generated images.Must be one of <photography>,<portrait>,<3d cartoon>,<anime>,
     * <oil painting>,<watercolor>,<sketch>,<chinese painting> <flat illustration>,<auto>
     */
    @JsonProperty("style")
    private String style;

    /**
     * The index of the style to use for generation.
     */
    @JsonProperty("style_index")
    private Integer styleIndex;

    /**
     * The URL of the style reference image.
     */
    @JsonProperty("style_ref_url")
    private String styleRefUrl;

    @JsonProperty("base_image_url")
    private String baseImageUrl;

    /**
     * The list of image URLs to edit.
     */
    @JsonProperty("images")
    private List<String> images;

    @JsonProperty("mask_image_url")
    private String maskImageUrl;

    @JsonProperty("sketch_image_url")
    private String sketchImageUrl;

    /**
     * The URL of the template model image.
     */
    @JsonProperty("template_image_url")
    private String templateImageUrl;

    /**
     * The list of shoe image URLs from multiple angles.
     */
    @JsonProperty("shoe_image_url")
    private List<String> shoeImageUrl;

    /**
     * The URL of the face image to replace in the generated result.
     */
    @JsonProperty("face_image_url")
    private String faceImageUrl;

    /**
     * The URL of the background reference image.
     */
    @JsonProperty("background_image_url")
    private String backgroundImageUrl;

    /**
     * Enter the URL address of the retained area mask image URL or image base64 data.
     */
    @JsonProperty("foreground_url")
    private String foregroundUrl;

    /**
     * The URL of the person image.
     */
    @JsonProperty("person_image_url")
    private String personImageUrl;

    /**
     * The URL of the top garment image.
     */
    @JsonProperty("top_garment_url")
    private String topGarmentUrl;

    /**
     * The URL of the bottom garment image.
     */
    @JsonProperty("bottom_garment_url")
    private String bottomGarmentUrl;

    /**
     * The URL of the coarse image.
     */
    @JsonProperty("coarse_image_url")
    private String coarseImageUrl;

    /**
     * The list of user-provided URLs.
     */
    @JsonProperty("user_urls")
    private List<String> userUrls;

    /**
     * refer image,Support jpg, png, tiff, webp
     */
    @JsonProperty("ref_img")
    private String refImg;

    /**
     * The predefined face ID to use for face generation.
     */
    @JsonProperty("predefined_face_id")
    private String predefinedFaceId;

    /**
     * The prompt describing the face appearance.
     */
    @JsonProperty("face_prompt")
    private String facePrompt;

    /**
     * The weight scale for the background reference image style.
     */
    @JsonProperty("bgstyle_scale")
    private Float bgstyleScale;

    /**
     * Whether the input image is a real person photo.
     */
    @JsonProperty("realPerson")
    private Boolean realPerson;

    /**
     * Sets the random number seed to use for generation. Must be between 0 and 4294967290.
     */
    @JsonProperty("seed")
    private Integer seed;

    /**
     * refer strength,Must be between 0.0 and 1.0
     */
    @JsonProperty("ref_strength")
    private Float refStrength;

    /**
     * The format in which the generated images are returned. Must be one of url or b64_json.
     */
    @JsonProperty("response_format")
    private String responseFormat;

    /**
     * refer mode,Must be one of repaint,refonly
     */
    @JsonProperty("ref_mode")
    private String refMode;

    @JsonProperty("negative_prompt")
    private String negativePrompt;

    /**
     * The text input for text-based image generation.
     */
    @JsonProperty("text")
    private String text;

    @JsonProperty("prompt_extend")
    private Boolean promptExtend;

    @JsonProperty("watermark")
    private Boolean watermark;

    @JsonProperty("function")
    private String function;

    @JsonProperty("sketch_weight")
    private Integer sketchWeight;

    @JsonProperty("sketch_extraction")
    private Boolean sketchExtraction;

    @JsonProperty("sketch_color")
    private Integer[][] sketchColor;

    @JsonProperty("mask_color")
    private Integer[][] maskColor;

    @JsonProperty("bbox_list")
    private Integer[][][] bboxList;

    @JsonProperty("max_images")
    private Integer maxImages;

    @JsonProperty("enable_interleave")
    private Boolean enableInterleave;

    @JsonProperty("enable_sequential")
    private Boolean enableSequential;

    @JsonProperty("color_palette")
    private List<ColorPaletteItem> colorPalette;

    @JsonProperty("thinking_mode")
    private Boolean thinkingMode;

    /**
     * Output aspect ratio for out-painting, e.g. "4:3".
     */
    @JsonProperty("output_ratio")
    private String outputRatio;

    /**
     * Horizontal expansion scale for out-painting.
     */
    @JsonProperty("x_scale")
    private Float xScale;

    /**
     * Vertical expansion scale for out-painting.
     */
    @JsonProperty("y_scale")
    private Float yScale;

    /**
     * Rotation angle in degrees for out-painting.
     */
    @JsonProperty("angle")
    private Integer angle;

    /**
     * Left expansion in pixels for out-painting.
     */
    @JsonProperty("left_offset")
    private Integer leftOffset;

    /**
     * Right expansion in pixels for out-painting.
     */
    @JsonProperty("right_offset")
    private Integer rightOffset;

    /**
     * Top expansion in pixels for out-painting.
     */
    @JsonProperty("top_offset")
    private Integer topOffset;

    /**
     * Bottom expansion in pixels for out-painting.
     */
    @JsonProperty("bottom_offset")
    private Integer bottomOffset;

    /**
     * Whether to use best quality mode for out-painting.
     */
    @JsonProperty("best_quality")
    private Boolean bestQuality;

    /**
     * Whether to limit output image size for out-painting.
     */
    @JsonProperty("limit_image_size")
    private Boolean limitImageSize;

    /**
     * The source language for translation.
     */
    @JsonProperty("source_lang")
    private String sourceLang;

    /**
     * The target language for translation.
     */
    @JsonProperty("target_lang")
    private String targetLang;

    /**
     * Extensional parameters for the API call.
     */
    @JsonProperty("ext")
    private Object ext;

    /**
     * The list of element items specifying subjects to preserve during generation.
     * The total count of reference images and element_list entries must not exceed 10.
     */
    @JsonProperty("element_list")
    private List<Element> elementList;

    /**
     * The result type for image generation, e.g. "single" or "series".
     */
    @JsonProperty("result_type")
    private String resultType;

    /**
     * The number of series (frames) to generate for series output.
     */
    @JsonProperty("series_amount")
    private Integer seriesAmount;

    /**
     * The aspect ratio for the generated images, e.g. "1:1", "16:9".
     */
    @JsonProperty("aspect_ratio")
    private String aspectRatio;

    /**
     * The resolution for the generated images, e.g. "1K", "2K".
     */
    @JsonProperty("resolution")
    private String resolution;

    /**
     * The short side size for the generated images, e.g. "512", "768".
     */
    @JsonProperty("short_side_size")
    private String shortSideSize;

    /**
     * The generation strength scale control.
     */
    @JsonProperty("scale")
    private Float scale;

    /**
     * The model version.
     */
    @JsonProperty("model_version")
    private String modelVersion;

    /**
     * This parameter introduces random variations during the image-guided process.
     */
    @JsonProperty("noise_level")
    private Integer noiseLevel;

    /**
     * The weight of the refer prompt.
     */
    @JsonProperty("ref_prompt_weight")
    private Float refPromptWeight;

    /**
     * Reference edge configuration for poster generation.
     */
    @JsonProperty("reference_edge")
    private ReferenceEdge referenceEdge;

    /**
     * The poster generation mode.
     */
    @JsonProperty("generate_mode")
    private String generateMode;

    /**
     * Auxiliary parameters for resolution enhancement or HD repair of the poster image. Limited to 1 entry.
     */
    @JsonProperty("auxiliary_parameters")
    private String auxiliaryParameters;

    /**
     * The main title of the poster.
     */
    @JsonProperty("title")
    private String title;

    /**
     * The subtitle of the poster.
     */
    @JsonProperty("sub_title")
    private String subTitle;

    /**
     * The body text of the poster.
     */
    @JsonProperty("body_text")
    private String bodyText;

    /**
     * The Chinese prompt text for the poster.
     */
    @JsonProperty("prompt_text_zh")
    private String promptTextZh;

    /**
     * The English prompt text for the poster.
     */
    @JsonProperty("prompt_text_en")
    private String promptTextEn;

    /**
     * The layout aspect ratio for the generated poster.
     */
    @JsonProperty("wh_ratios")
    private String whRatios;

    /**
     * The poster style name (LoRA name). Must be used together with {@code loraWeight}.
     */
    @JsonProperty("lora_name")
    private String loraName;

    /**
     * The poster style weight (LoRA weight). Must be used together with {@code loraName}.
     */
    @JsonProperty("lora_weight")
    private Float loraWeight;

    /**
     * The whitespace effect weight, used to control the poster whitespace effect.
     */
    @JsonProperty("ctrl_ratio")
    private Float ctrlRatio;

    /**
     * The whitespace step ratio, used to control the poster whitespace effect.
     */
    @JsonProperty("ctrl_step")
    private Float ctrlStep;

    /**
     * Whether to enable creative title layout for the poster.
     */
    @JsonProperty("creative_title_layout")
    private Boolean creativeTitleLayout;

    /**
     * Whether to enable fast mode for image erase completion.
     */
    @JsonProperty("fast_mode")
    private Boolean fastMode;

    /**
     * Whether to enable dilate flag for image erase completion.
     * If the erasure mask is the result of algorithmic segmentation, set it to true;
     * if the erasure mask is the result of painting, set it to false.
     */
    @JsonProperty("dilate_flag")
    private Boolean dilateFlag;

    /**
     * Whether to restore the faces in the model image.
     * True: Default value, retains the original face image.
     * False: Randomly generate a new face.
     */
    @JsonProperty("restore_face")
    private Boolean restoreFace;

    /**
     * The gender for AI try-on, e.g. "male" or "female".
     */
    @JsonProperty("gender")
    private String gender;

    /**
     * The list of clothes types for AI try-on.
     */
    @JsonProperty("clothes_type")
    private List<String> clothesType;

    /**
     * The list of resources for image generation.
     */
    @JsonProperty("resources")
    private List<Resource> resources;

    /**
     * Whether to enable skin retouch for image generation.
     */
    @JsonProperty("skin_retouch")
    private Boolean skinRetouch;

    /**
     * The number of inference steps for image generation.
     */
    @JsonProperty("steps")
    private Integer steps;

    /**
     * The font name for text rendering in image generation.
     */
    @JsonProperty("font_name")
    private String fontName;

    /**
     * The URL of the TTF font file for text rendering in image generation.
     */
    @JsonProperty("ttf_url")
    private String ttfUrl;

    /**
     * The short side size of the output image in pixels.
     */
    @JsonProperty("image_short_size")
    private Integer imageShortSize;

    /**
     * Whether to include alpha channel in the output image.
     */
    @JsonProperty("alpha_channel")
    private Boolean alphaChannel;

    /**
     * The list of training file IDs for fine-tuned models.
     */
    @JsonProperty("training_file_ids")
    private List<String> trainingFileIds;

    /**
     * Invocation mode for the API call.
     * - AUTO: automatically detect based on model
     * - SYNC: synchronous call (no async header)
     * - ASYNC: asynchronous call (with async header, returns task_id for polling)
     */
    @JsonIgnore
    private InvokeMode invokeMode = InvokeMode.AUTO;

    /**
     * Request type for the API call.
     * - AUTO: automatically detect based on model
     * - GENERATION: generation request
     * - STANDARD: standard request
     */
    @JsonIgnore
    private RequestType requestType = RequestType.AUTO;

    @Override
    public String getModel() {
        return this.model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    @Override
    public Integer getN() {
        return this.n;
    }

    public void setN(Integer n) {
        this.n = n;
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

    public String getSize() {
        if (this.size != null) {
            return this.size;
        }
        return (this.width != null && this.height != null) ? this.width + "*" + this.height : null;
    }

    public void setSize(String size) {
        this.size = size;
    }

    @Override
    public String getStyle() {
        return this.style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public Integer getStyleIndex() {
        return this.styleIndex;
    }

    public void setStyleIndex(Integer styleIndex) {
        this.styleIndex = styleIndex;
    }

    public String getStyleRefUrl() {
        return this.styleRefUrl;
    }

    public void setStyleRefUrl(String styleRefUrl) {
        this.styleRefUrl = styleRefUrl;
    }

    public String getBaseImageUrl() {
        return this.baseImageUrl;
    }

    public void setBaseImageUrl(String baseImageUrl) {
        this.baseImageUrl = baseImageUrl;
    }

    public List<String> getImages() {
        return this.images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public String getMaskImageUrl() {
        return this.maskImageUrl;
    }

    public void setMaskImageUrl(String maskImageUrl) {
        this.maskImageUrl = maskImageUrl;
    }

    public String getSketchImageUrl() {
        return this.sketchImageUrl;
    }

    public void setSketchImageUrl(String sketchImageUrl) {
        this.sketchImageUrl = sketchImageUrl;
    }

    public String getTemplateImageUrl() {
        return this.templateImageUrl;
    }

    public void setTemplateImageUrl(String templateImageUrl) {
        this.templateImageUrl = templateImageUrl;
    }

    public List<String> getShoeImageUrl() {
        return this.shoeImageUrl;
    }

    public void setShoeImageUrl(List<String> shoeImageUrl) {
        this.shoeImageUrl = shoeImageUrl;
    }

    public String getFaceImageUrl() {
        return this.faceImageUrl;
    }

    public void setFaceImageUrl(String faceImageUrl) {
        this.faceImageUrl = faceImageUrl;
    }

    public String getBackgroundImageUrl() {
        return this.backgroundImageUrl;
    }

    public void setBackgroundImageUrl(String backgroundImageUrl) {
        this.backgroundImageUrl = backgroundImageUrl;
    }

    public String getForegroundUrl() {
        return this.foregroundUrl;
    }

    public void setForegroundUrl(String foregroundUrl) {
        this.foregroundUrl = foregroundUrl;
    }

    public String getPersonImageUrl() {
        return this.personImageUrl;
    }

    public void setPersonImageUrl(String personImageUrl) {
        this.personImageUrl = personImageUrl;
    }

    public String getTopGarmentUrl() {
        return this.topGarmentUrl;
    }

    public void setTopGarmentUrl(String topGarmentUrl) {
        this.topGarmentUrl = topGarmentUrl;
    }

    public String getBottomGarmentUrl() {
        return this.bottomGarmentUrl;
    }

    public void setBottomGarmentUrl(String bottomGarmentUrl) {
        this.bottomGarmentUrl = bottomGarmentUrl;
    }

    public String getCoarseImageUrl() {
        return this.coarseImageUrl;
    }

    public void setCoarseImageUrl(String coarseImageUrl) {
        this.coarseImageUrl = coarseImageUrl;
    }

    public List<String> getUserUrls() {
        return this.userUrls;
    }

    public void setUserUrls(List<String> userUrls) {
        this.userUrls = userUrls;
    }

    public String getRefImg() {
        return this.refImg;
    }

    public void setRefImg(String refImg) {
        this.refImg = refImg;
    }

    public String getPredefinedFaceId() {
        return this.predefinedFaceId;
    }

    public void setPredefinedFaceId(String predefinedFaceId) {
        this.predefinedFaceId = predefinedFaceId;
    }

    public String getFacePrompt() {
        return this.facePrompt;
    }

    public void setFacePrompt(String facePrompt) {
        this.facePrompt = facePrompt;
    }

    public Float getBgstyleScale() {
        return this.bgstyleScale;
    }

    public void setBgstyleScale(Float bgstyleScale) {
        this.bgstyleScale = bgstyleScale;
    }

    public Boolean getRealPerson() {
        return this.realPerson;
    }

    public void setRealPerson(Boolean realPerson) {
        this.realPerson = realPerson;
    }

    public Integer getSeed() {
        return this.seed;
    }

    public void setSeed(Integer seed) {
        this.seed = seed;
    }

    public Float getRefStrength() {
        return this.refStrength;
    }

    public void setRefStrength(Float refStrength) {
        this.refStrength = refStrength;
    }

    @Override
    public String getResponseFormat() {
        return this.responseFormat;
    }

    public void setResponseFormat(String responseFormat) {
        this.responseFormat = responseFormat;
    }

    public String getRefMode() {
        return this.refMode;
    }

    public void setRefMode(String refMode) {
        this.refMode = refMode;
    }

    public String getNegativePrompt() {
        return this.negativePrompt;
    }

    public void setNegativePrompt(String negativePrompt) {
        this.negativePrompt = negativePrompt;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Boolean getPromptExtend() {
        return this.promptExtend;
    }

    public void setPromptExtend(Boolean promptExtend) {
        this.promptExtend = promptExtend;
    }

    public Boolean getWatermark() {
        return this.watermark;
    }

    public void setWatermark(Boolean watermark) {
        this.watermark = watermark;
    }

    public String getFunction() {
        return this.function;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    public Integer getSketchWeight() {
        return this.sketchWeight;
    }

    public void setSketchWeight(Integer sketchWeight) {
        this.sketchWeight = sketchWeight;
    }

    public Boolean getSketchExtraction() {
        return this.sketchExtraction;
    }

    public void setSketchExtraction(Boolean sketchExtraction) {
        this.sketchExtraction = sketchExtraction;
    }

    public Integer[][] getSketchColor() {
        return this.sketchColor;
    }

    public void setSketchColor(Integer[][] sketchColor) {
        this.sketchColor = sketchColor;
    }

    public Integer[][] getMaskColor() {
        return this.maskColor;
    }

    public void setMaskColor(Integer[][] maskColor) {
        this.maskColor = maskColor;
    }

    public Integer[][][] getBboxList() {
        return this.bboxList;
    }

    public void setBboxList(Integer[][][] bboxList) {
        this.bboxList = bboxList;
    }

    public Integer getMaxImages() {
        return this.maxImages;
    }

    public void setMaxImages(Integer maxImages) {
        this.maxImages = maxImages;
    }

    public Boolean getEnableInterleave() {
        return this.enableInterleave;
    }

    public void setEnableInterleave(Boolean enableInterleave) {
        this.enableInterleave = enableInterleave;
    }

    public Boolean getEnableSequential() {
        return this.enableSequential;
    }

    public void setEnableSequential(Boolean enableSequential) {
        this.enableSequential = enableSequential;
    }

    public List<ColorPaletteItem> getColorPalette() {
        return this.colorPalette;
    }

    public void setColorPalette(List<ColorPaletteItem> colorPalette) {
        this.colorPalette = colorPalette;
    }

    public Boolean getThinkingMode() {
        return this.thinkingMode;
    }

    public void setThinkingMode(Boolean thinkingMode) {
        this.thinkingMode = thinkingMode;
    }

    public String getOutputRatio() {
        return this.outputRatio;
    }

    public void setOutputRatio(String outputRatio) {
        this.outputRatio = outputRatio;
    }

    public Float getXScale() {
        return this.xScale;
    }

    public void setXScale(Float xScale) {
        this.xScale = xScale;
    }

    public Float getYScale() {
        return this.yScale;
    }

    public void setYScale(Float yScale) {
        this.yScale = yScale;
    }

    public Integer getAngle() {
        return this.angle;
    }

    public void setAngle(Integer angle) {
        this.angle = angle;
    }

    public Integer getLeftOffset() {
        return this.leftOffset;
    }

    public void setLeftOffset(Integer leftOffset) {
        this.leftOffset = leftOffset;
    }

    public Integer getRightOffset() {
        return this.rightOffset;
    }

    public void setRightOffset(Integer rightOffset) {
        this.rightOffset = rightOffset;
    }

    public Integer getTopOffset() {
        return this.topOffset;
    }

    public void setTopOffset(Integer topOffset) {
        this.topOffset = topOffset;
    }

    public Integer getBottomOffset() {
        return this.bottomOffset;
    }

    public void setBottomOffset(Integer bottomOffset) {
        this.bottomOffset = bottomOffset;
    }

    public Boolean getBestQuality() {
        return this.bestQuality;
    }

    public void setBestQuality(Boolean bestQuality) {
        this.bestQuality = bestQuality;
    }

    public Boolean getLimitImageSize() {
        return this.limitImageSize;
    }

    public void setLimitImageSize(Boolean limitImageSize) {
        this.limitImageSize = limitImageSize;
    }

    public String getSourceLang() {
        return this.sourceLang;
    }

    public void setSourceLang(String sourceLang) {
        this.sourceLang = sourceLang;
    }

    public String getTargetLang() {
        return this.targetLang;
    }

    public void setTargetLang(String targetLang) {
        this.targetLang = targetLang;
    }

    public Object getExt() {
        return this.ext;
    }

    public void setExt(Object ext) {
        this.ext = ext;
    }

    public List<Element> getElementList() {
        return this.elementList;
    }

    public void setElementList(List<Element> elementList) {
        this.elementList = elementList;
    }

    public String getResultType() {
        return this.resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    public Integer getSeriesAmount() {
        return this.seriesAmount;
    }

    public void setSeriesAmount(Integer seriesAmount) {
        this.seriesAmount = seriesAmount;
    }

    public String getAspectRatio() {
        return this.aspectRatio;
    }

    public void setAspectRatio(String aspectRatio) {
        this.aspectRatio = aspectRatio;
    }

    public String getResolution() {
        return this.resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public String getShortSideSize() {
        return this.shortSideSize;
    }

    public void setShortSideSize(String shortSideSize) {
        this.shortSideSize = shortSideSize;
    }

    public Float getScale() {
        return this.scale;
    }

    public void setScale(Float scale) {
        this.scale = scale;
    }

    public String getModelVersion() {
        return this.modelVersion;
    }

    public void setModelVersion(String modelVersion) {
        this.modelVersion = modelVersion;
    }

    public Integer getNoiseLevel() {
        return this.noiseLevel;
    }

    public void setNoiseLevel(Integer noiseLevel) {
        this.noiseLevel = noiseLevel;
    }

    public Float getRefPromptWeight() {
        return this.refPromptWeight;
    }

    public void setRefPromptWeight(Float refPromptWeight) {
        this.refPromptWeight = refPromptWeight;
    }

    public ReferenceEdge getReferenceEdge() {
        return this.referenceEdge;
    }

    public void setReferenceEdge(ReferenceEdge referenceEdge) {
        this.referenceEdge = referenceEdge;
    }

    public String getGenerateMode() {
        return this.generateMode;
    }

    public void setGenerateMode(String generateMode) {
        this.generateMode = generateMode;
    }

    public String getAuxiliaryParameters() {
        return this.auxiliaryParameters;
    }

    public void setAuxiliaryParameters(String auxiliaryParameters) {
        this.auxiliaryParameters = auxiliaryParameters;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return this.subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getBodyText() {
        return this.bodyText;
    }

    public void setBodyText(String bodyText) {
        this.bodyText = bodyText;
    }

    public String getPromptTextZh() {
        return this.promptTextZh;
    }

    public void setPromptTextZh(String promptTextZh) {
        this.promptTextZh = promptTextZh;
    }

    public String getPromptTextEn() {
        return this.promptTextEn;
    }

    public void setPromptTextEn(String promptTextEn) {
        this.promptTextEn = promptTextEn;
    }

    public String getWhRatios() {
        return this.whRatios;
    }

    public void setWhRatios(String whRatios) {
        this.whRatios = whRatios;
    }

    public String getLoraName() {
        return this.loraName;
    }

    public void setLoraName(String loraName) {
        this.loraName = loraName;
    }

    public Float getLoraWeight() {
        return this.loraWeight;
    }

    public void setLoraWeight(Float loraWeight) {
        this.loraWeight = loraWeight;
    }

    public Float getCtrlRatio() {
        return this.ctrlRatio;
    }

    public void setCtrlRatio(Float ctrlRatio) {
        this.ctrlRatio = ctrlRatio;
    }

    public Float getCtrlStep() {
        return this.ctrlStep;
    }

    public void setCtrlStep(Float ctrlStep) {
        this.ctrlStep = ctrlStep;
    }

    public Boolean getCreativeTitleLayout() {
        return this.creativeTitleLayout;
    }

    public void setCreativeTitleLayout(Boolean creativeTitleLayout) {
        this.creativeTitleLayout = creativeTitleLayout;
    }

    public Boolean getFastMode() {
        return this.fastMode;
    }

    public void setFastMode(Boolean fastMode) {
        this.fastMode = fastMode;
    }

    public Boolean getDilateFlag() {
        return this.dilateFlag;
    }

    public void setDilateFlag(Boolean dilateFlag) {
        this.dilateFlag = dilateFlag;
    }

    public Boolean getRestoreFace() {
        return this.restoreFace;
    }

    public void setRestoreFace(Boolean restoreFace) {
        this.restoreFace = restoreFace;
    }

    public String getGender() {
        return this.gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public List<String> getClothesType() {
        return this.clothesType;
    }

    public void setClothesType(List<String> clothesType) {
        this.clothesType = clothesType;
    }

    public List<Resource> getResources() {
        return this.resources;
    }

    public void setResources(List<Resource> resources) {
        this.resources = resources;
    }

    public Boolean getSkinRetouch() {
        return this.skinRetouch;
    }

    public void setSkinRetouch(Boolean skinRetouch) {
        this.skinRetouch = skinRetouch;
    }

    public Integer getSteps() {
        return this.steps;
    }

    public void setSteps(Integer steps) {
        this.steps = steps;
    }

    public String getFontName() {
        return this.fontName;
    }

    public void setFontName(String fontName) {
        this.fontName = fontName;
    }

    public String getTtfUrl() {
        return this.ttfUrl;
    }

    public void setTtfUrl(String ttfUrl) {
        this.ttfUrl = ttfUrl;
    }

    public Integer getImageShortSize() {
        return this.imageShortSize;
    }

    public void setImageShortSize(Integer imageShortSize) {
        this.imageShortSize = imageShortSize;
    }

    public Boolean getAlphaChannel() {
        return this.alphaChannel;
    }

    public void setAlphaChannel(Boolean alphaChannel) {
        this.alphaChannel = alphaChannel;
    }

    public List<String> getTrainingFileIds() {
        return this.trainingFileIds;
    }

    public void setTrainingFileIds(List<String> trainingFileIds) {
        this.trainingFileIds = trainingFileIds;
    }

    public InvokeMode getInvokeMode() {
        return this.invokeMode;
    }

    public void setInvokeMode(InvokeMode invokeMode) {
        this.invokeMode = invokeMode;
    }

    public RequestType getRequestType() {
        return this.requestType;
    }

    public void setRequestType(RequestType requestType) {
        this.requestType = requestType;
    }

    @Override
    public String toString() {
        return "DashScopeImageOptions{" + "model='" + this.model + '\'' + ", n=" + this.n + ", width=" + this.width
                + ", height=" + this.height + ", size='" + this.size + '\'' + ", style='" + this.style + '\''
                + ", styleIndex=" + this.styleIndex + ", styleRefUrl='" + this.styleRefUrl + '\''
                + ", predefinedFaceId='" + this.predefinedFaceId + '\'' + ", faceImageUrl='" + this.faceImageUrl + '\''
                + ", facePrompt='" + this.facePrompt + '\'' + ", backgroundImageUrl='" + this.backgroundImageUrl + '\''
                + ", bgstyleScale=" + this.bgstyleScale + ", realPerson=" + this.realPerson + ", seed=" + this.seed
                + ", refImg='" + this.refImg + '\'' + ", refStrength=" + this.refStrength + ", responseFormat='"
                + this.responseFormat + '\'' + ", refMode='" + this.refMode + '\'' + ", negativePrompt='"
                + this.negativePrompt + '\'' + ", text='" + this.text + '\'' + ", promptExtend=" + this.promptExtend
                + ", watermark=" + this.watermark + ", function='" + this.function + '\'' + ", baseImageUrl='"
                + this.baseImageUrl + '\'' + ", maskImageUrl='" + this.maskImageUrl + '\'' + ", sketchImageUrl='"
                + this.sketchImageUrl + '\'' + ", templateImageUrl='" + this.templateImageUrl + '\'' + ", shoeImageUrl="
                + this.shoeImageUrl + ", userUrls=" + this.userUrls + ", sketchWeight=" + this.sketchWeight
                + ", sketchExtraction=" + this.sketchExtraction + ", sketchColor=" + Arrays.toString(this.sketchColor)
                + ", maskColor=" + Arrays.toString(this.maskColor) + ", maxImages=" + this.maxImages
                + ", enableInterleave=" + this.enableInterleave + ", invokeMode=" + this.invokeMode + ", outputRatio='"
                + this.outputRatio + '\'' + ", xScale=" + this.xScale + ", yScale=" + this.yScale + ", angle="
                + this.angle + ", leftOffset=" + this.leftOffset + ", rightOffset=" + this.rightOffset + ", topOffset="
                + this.topOffset + ", bottomOffset=" + this.bottomOffset + ", bestQuality=" + this.bestQuality
                + ", limitImageSize=" + this.limitImageSize + ", requestType=" + this.requestType + ", images="
                + this.images + ", enableSequential=" + this.enableSequential + ", colorPalette=" + this.colorPalette
                + ", thinkingMode=" + this.thinkingMode + ", bboxList=" + Arrays.toString(this.bboxList)
                + ", sourceLang='" + this.sourceLang + '\'' + ", targetLang='" + this.targetLang + '\'' + ", ext="
                + this.ext + ", elementList=" + this.elementList + ", resultType='" + this.resultType + '\''
                + ", seriesAmount=" + this.seriesAmount + ", aspectRatio='" + this.aspectRatio + '\'' + ", resolution='"
                + this.resolution + '\'' + ", shortSideSize='" + this.shortSideSize + '\'' + ", scale=" + this.scale
                + ", generateMode='" + this.generateMode + '\'' + ", auxiliaryParameters='" + this.auxiliaryParameters
                + '\'' + ", title='" + this.title + '\'' + ", subTitle='" + this.subTitle + '\'' + ", bodyText='"
                + this.bodyText + '\'' + ", promptTextZh='" + this.promptTextZh + '\'' + ", promptTextEn='"
                + this.promptTextEn + '\'' + ", whRatios='" + this.whRatios + '\'' + ", loraName='" + this.loraName
                + '\'' + ", loraWeight=" + this.loraWeight + ", ctrlRatio=" + this.ctrlRatio + ", ctrlStep="
                + this.ctrlStep + ", creativeTitleLayout=" + this.creativeTitleLayout + ", fastMode=" + this.fastMode
                + ", dilateFlag=" + this.dilateFlag + ", restoreFace=" + this.restoreFace + ", gender='" + this.gender
                + '\'' + ", clothesType=" + this.clothesType + ", resources=" + this.resources + ", skinRetouch="
                + this.skinRetouch + ", steps=" + this.steps + ", fontName='" + this.fontName + '\'' + ", ttfUrl='"
                + this.ttfUrl + '\'' + ", imageShortSize=" + this.imageShortSize + ", alphaChannel=" + this.alphaChannel
                + ", trainingFileIds=" + this.trainingFileIds + '}';
    }

    protected ImageRequest toImageRequest(ImagePrompt imagePrompt) {
        String prompt = imagePrompt.getInstructions().get(0).getText();
        Assert.hasText(prompt, "Image prompt text cannot be empty");

        // Build RequestInput
        ImageRequest.Input.Builder inputBuilder = ImageRequest.Input.builder();

        if (ImageModel.WANX_BACKGROUND_GENERATION_V2.getValue().equals(this.model)) {
            inputBuilder.refPrompt(prompt);
        } else {
            inputBuilder.prompt(prompt);
        }

        if (ImageModel.WANX_BACKGROUND_GENERATION_V2.getValue().equals(this.model)) {
            inputBuilder.negRefPrompt(this.negativePrompt);
        } else {
            inputBuilder.negativePrompt(this.negativePrompt);
        }

        if (this.baseImageUrl != null) {
            if (ImageModel.QWEN_MT_IMAGE.getValue().equals(this.model) || ImageModel.WANX_STYLE_REPAINT_V1.getValue()
                    .equals(this.model) || ImageModel.IMAGE_INSTANCE_SEGMENTATION.getValue().equals(this.model)
                    || ImageModel.AITRYON_PARSING_V1.getValue().equals(this.model)) {
                inputBuilder.imageUrl(this.baseImageUrl);
            } else if (ImageModel.WORDART_TEXTURE.getValue().equals(this.model)) {
                inputBuilder.image(new ImageRequest.Image(this.baseImageUrl));
            } else {
                inputBuilder.baseImageUrl(this.baseImageUrl);
            }
        }

        if (ImageModel.WANX_BACKGROUND_GENERATION_V2.getValue().equals(this.model)
                || ImageModel.WORDART_TEXTURE.getValue().equals(this.model)) {
            inputBuilder.refImageUrl(this.refImg);
        } else {
            inputBuilder.refImg(this.refImg);
        }

        if (ImageModel.FACECHAIN_GENERATION.getValue().equals(this.model)) {
            inputBuilder.templateUrl(this.templateImageUrl);
        } else {
            inputBuilder.templateImageUrl(this.templateImageUrl);
        }

        if (ImageModel.WORDART_TEXTURE.getValue().equals(this.model)) {
            inputBuilder.textureStyle(this.style);
        }

        if (ImageModel.WANX_POSTER_GENERATION_V1.getValue().equals(this.model)) {
            inputBuilder.generateNum(this.n);
        }

        if (ImageModel.WORDART_TEXTURE.getValue().equals(this.model)) {
            if (this.text != null
                    || this.fontName != null
                    || this.ttfUrl != null
                    || this.outputRatio != null) {
                inputBuilder.text(ImageRequest.Text.builder()
                        .textContent(this.text)
                        .fontName(this.fontName)
                        .ttfUrl(this.ttfUrl)
                        .outputImageRatio(this.outputRatio)
                        .build());
            }
        } else {
            inputBuilder.text(this.text);
        }

        inputBuilder.function(this.function)
                .images(this.images)
                .maskImageUrl(this.maskImageUrl)
                .sketchImageUrl(this.sketchImageUrl)
                .sourceLang(this.sourceLang)
                .targetLang(this.targetLang)
                .ext(this.ext)
                .styleIndex(this.styleIndex)
                .styleRefUrl(this.styleRefUrl)
                .predefinedFaceId(this.predefinedFaceId)
                .faceImageUrl(this.faceImageUrl)
                .facePrompt(this.facePrompt)
                .backgroundImageUrl(this.backgroundImageUrl)
                .bgstyleScale(this.bgstyleScale)
                .realPerson(this.realPerson)
                .shoeImageUrl(this.shoeImageUrl)
                .generateMode(this.generateMode)
                .auxiliaryParameters(this.auxiliaryParameters)
                .title(this.title)
                .subTitle(this.subTitle)
                .bodyText(this.bodyText)
                .promptTextZh(this.promptTextZh)
                .promptTextEn(this.promptTextEn)
                .whRatios(this.whRatios)
                .loraName(this.loraName)
                .loraWeight(this.loraWeight)
                .ctrlRatio(this.ctrlRatio)
                .ctrlStep(this.ctrlStep)
                .creativeTitleLayout(this.creativeTitleLayout)
                .referenceEdge(this.referenceEdge)
                .foregroundUrl(this.foregroundUrl)
                .personImageUrl(this.personImageUrl)
                .topGarmentUrl(this.topGarmentUrl)
                .bottomGarmentUrl(this.bottomGarmentUrl)
                .coarseImageUrl(this.coarseImageUrl)
                .userUrls(this.userUrls);

        return ImageRequest.builder()
                .model(this.model)
                .input(inputBuilder.build())
                .parameters(toImageRequestParameter())
                .trainingFileIds(this.trainingFileIds)
                .build();
    }

    protected ImageRequest toImageGenerationRequest(ImagePrompt request) {
        String prompt = request.getInstructions().get(0).getText();
        Assert.hasText(prompt, "Image prompt text cannot be empty");

        // Build message content
        List<Message.Content> content = new ArrayList<>();
        content.add(new Message.Content(prompt, null));

        if (this.baseImageUrl != null) {
            content.add(new Message.Content(null, this.baseImageUrl));
        }

        if (!CollectionUtils.isEmpty(this.images)) {
            this.images.forEach(image -> content.add(new Message.Content(null, image)));
        }

        List<Message> messages = List.of(new Message("user", content));

        ImageRequest.GenerationInput input = new ImageRequest.GenerationInput(messages, this.elementList);

        return ImageRequest.builder()
                .model(this.model)
                .input(input)
                .parameters(toImageRequestParameter())
                .trainingFileIds(this.trainingFileIds)
                .build();
    }

    protected ImageRequest.Parameters toImageRequestParameter() {
        ImageRequest.Parameters.Builder paramBuilder = ImageRequest.Parameters.builder();

        if (ImageModel.IMAGE_OUT_PAINTING.getValue().equals(this.model) || ImageModel.IMAGE_ERASE_COMPLETION.getValue()
                .equals(this.model)) {
            paramBuilder.addWatermark(this.watermark);
        } else {
            paramBuilder.watermark(this.watermark);
        }

        if (!ImageModel.WORDART_TEXTURE.getValue().equals(this.model)) {
            paramBuilder.style(this.style);
        }

        if (!ImageModel.WANX_POSTER_GENERATION_V1.getValue().equals(this.model)) {
            paramBuilder.n(this.n).outputRatio(this.outputRatio);
        }

        return paramBuilder.size(this.getSize())
                .seed(this.seed)
                .refStrength(this.refStrength)
                .refMode(this.refMode)
                .promptExtend(this.promptExtend)
                .sketchWeight(this.sketchWeight)
                .sketchExtraction(this.sketchExtraction)
                .sketchColor(this.sketchColor)
                .maskColor(this.maskColor)
                .negativePrompt(this.negativePrompt)
                .maxImages(this.maxImages)
                .enableInterleave(this.enableInterleave)
                .xScale(this.xScale)
                .yScale(this.yScale)
                .angle(this.angle)
                .leftOffset(this.leftOffset)
                .rightOffset(this.rightOffset)
                .topOffset(this.topOffset)
                .bottomOffset(this.bottomOffset)
                .bestQuality(this.bestQuality)
                .limitImageSize(this.limitImageSize)
                .enableSequential(this.enableSequential)
                .colorPalette(this.colorPalette)
                .thinkingMode(this.thinkingMode)
                .bboxList(this.bboxList)
                .resultType(this.resultType)
                .seriesAmount(this.seriesAmount)
                .aspectRatio(this.aspectRatio)
                .resolution(this.resolution)
                .shortSideSize(this.shortSideSize)
                .scale(this.scale)
                .modelVersion(this.modelVersion)
                .noiseLevel(this.noiseLevel)
                .refPromptWeight(this.refPromptWeight)
                .fastMode(this.fastMode)
                .dilateFlag(this.dilateFlag)
                .restoreFace(this.restoreFace)
                .gender(this.gender)
                .clothesType(this.clothesType)
                .resources(this.resources)
                .skinRetouch(this.skinRetouch)
                .steps(this.steps)
                .fontName(this.fontName)
                .ttfUrl(this.ttfUrl)
                .imageShortSize(this.imageShortSize)
                .alphaChannel(this.alphaChannel)
                .build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private final DashScopeImageOptions options;

        private Builder() {
            this.options = new DashScopeImageOptions();
        }

        public Builder model(String model) {
            this.options.setModel(model);
            return this;
        }

        public Builder n(Integer n) {
            this.options.setN(n);
            return this;
        }

        public Builder width(Integer width) {
            this.options.setWidth(width);
            return this;
        }

        public Builder height(Integer height) {
            this.options.setHeight(height);
            return this;
        }

        public Builder size(String size) {
            this.options.setSize(size);
            return this;
        }

        public Builder style(String style) {
            this.options.setStyle(style);
            return this;
        }

        public Builder styleIndex(Integer styleIndex) {
            this.options.setStyleIndex(styleIndex);
            return this;
        }

        public Builder styleRefUrl(String styleRefUrl) {
            this.options.setStyleRefUrl(styleRefUrl);
            return this;
        }

        public Builder baseImageUrl(String baseImageUrl) {
            this.options.setBaseImageUrl(baseImageUrl);
            return this;
        }

        public Builder images(List<String> images) {
            this.options.setImages(images);
            return this;
        }

        public Builder maskImageUrl(String maskImageUrl) {
            this.options.setMaskImageUrl(maskImageUrl);
            return this;
        }

        public Builder sketchImageUrl(String sketchImageUrl) {
            this.options.setSketchImageUrl(sketchImageUrl);
            return this;
        }

        public Builder templateImageUrl(String templateImageUrl) {
            this.options.setTemplateImageUrl(templateImageUrl);
            return this;
        }

        public Builder shoeImageUrl(List<String> shoeImageUrl) {
            this.options.setShoeImageUrl(shoeImageUrl);
            return this;
        }

        public Builder faceImageUrl(String faceImageUrl) {
            this.options.setFaceImageUrl(faceImageUrl);
            return this;
        }

        public Builder backgroundImageUrl(String backgroundImageUrl) {
            this.options.setBackgroundImageUrl(backgroundImageUrl);
            return this;
        }

        public Builder foregroundUrl(String foregroundUrl) {
            this.options.setForegroundUrl(foregroundUrl);
            return this;
        }

        public Builder personImageUrl(String personImageUrl) {
            this.options.setPersonImageUrl(personImageUrl);
            return this;
        }

        public Builder topGarmentUrl(String topGarmentUrl) {
            this.options.setTopGarmentUrl(topGarmentUrl);
            return this;
        }

        public Builder bottomGarmentUrl(String bottomGarmentUrl) {
            this.options.setBottomGarmentUrl(bottomGarmentUrl);
            return this;
        }

        public Builder coarseImageUrl(String coarseImageUrl) {
            this.options.setCoarseImageUrl(coarseImageUrl);
            return this;
        }

        public Builder userUrls(List<String> userUrls) {
            this.options.setUserUrls(userUrls);
            return this;
        }

        public Builder refImg(String refImg) {
            this.options.setRefImg(refImg);
            return this;
        }

        public Builder predefinedFaceId(String predefinedFaceId) {
            this.options.setPredefinedFaceId(predefinedFaceId);
            return this;
        }

        public Builder facePrompt(String facePrompt) {
            this.options.setFacePrompt(facePrompt);
            return this;
        }

        public Builder bgstyleScale(Float bgstyleScale) {
            this.options.setBgstyleScale(bgstyleScale);
            return this;
        }

        public Builder realPerson(Boolean realPerson) {
            this.options.setRealPerson(realPerson);
            return this;
        }

        public Builder seed(Integer seed) {
            this.options.setSeed(seed);
            return this;
        }

        public Builder refStrength(Float refStrength) {
            this.options.setRefStrength(refStrength);
            return this;
        }

        public Builder responseFormat(String responseFormat) {
            this.options.setResponseFormat(responseFormat);
            return this;
        }

        public Builder refMode(String refMode) {
            this.options.setRefMode(refMode);
            return this;
        }

        public Builder negativePrompt(String negativePrompt) {
            this.options.setNegativePrompt(negativePrompt);
            return this;
        }

        public Builder text(String text) {
            this.options.setText(text);
            return this;
        }

        public Builder promptExtend(Boolean promptExtend) {
            this.options.setPromptExtend(promptExtend);
            return this;
        }

        public Builder watermark(Boolean watermark) {
            this.options.setWatermark(watermark);
            return this;
        }

        public Builder function(String function) {
            this.options.setFunction(function);
            return this;
        }

        public Builder sketchWeight(Integer sketchWeight) {
            this.options.setSketchWeight(sketchWeight);
            return this;
        }

        public Builder sketchExtraction(Boolean sketchExtraction) {
            this.options.setSketchExtraction(sketchExtraction);
            return this;
        }

        public Builder sketchColor(Integer[][] sketchColor) {
            this.options.setSketchColor(sketchColor);
            return this;
        }

        public Builder maskColor(Integer[][] maskColor) {
            this.options.setMaskColor(maskColor);
            return this;
        }

        public Builder bboxList(Integer[][][] bboxList) {
            this.options.setBboxList(bboxList);
            return this;
        }

        public Builder maxImages(Integer maxImages) {
            this.options.setMaxImages(maxImages);
            return this;
        }

        public Builder enableInterleave(Boolean enableInterleave) {
            this.options.setEnableInterleave(enableInterleave);
            return this;
        }

        public Builder enableSequential(Boolean enableSequential) {
            this.options.setEnableSequential(enableSequential);
            return this;
        }

        public Builder colorPalette(List<ColorPaletteItem> colorPalette) {
            this.options.setColorPalette(colorPalette);
            return this;
        }

        public Builder thinkingMode(Boolean thinkingMode) {
            this.options.setThinkingMode(thinkingMode);
            return this;
        }

        public Builder outputRatio(String outputRatio) {
            this.options.setOutputRatio(outputRatio);
            return this;
        }

        public Builder xScale(Float xScale) {
            this.options.setXScale(xScale);
            return this;
        }

        public Builder yScale(Float yScale) {
            this.options.setYScale(yScale);
            return this;
        }

        public Builder angle(Integer angle) {
            this.options.setAngle(angle);
            return this;
        }

        public Builder leftOffset(Integer leftOffset) {
            this.options.setLeftOffset(leftOffset);
            return this;
        }

        public Builder rightOffset(Integer rightOffset) {
            this.options.setRightOffset(rightOffset);
            return this;
        }

        public Builder topOffset(Integer topOffset) {
            this.options.setTopOffset(topOffset);
            return this;
        }

        public Builder bottomOffset(Integer bottomOffset) {
            this.options.setBottomOffset(bottomOffset);
            return this;
        }

        public Builder bestQuality(Boolean bestQuality) {
            this.options.setBestQuality(bestQuality);
            return this;
        }

        public Builder limitImageSize(Boolean limitImageSize) {
            this.options.setLimitImageSize(limitImageSize);
            return this;
        }

        public Builder sourceLang(String sourceLang) {
            this.options.setSourceLang(sourceLang);
            return this;
        }

        public Builder targetLang(String targetLang) {
            this.options.setTargetLang(targetLang);
            return this;
        }

        public Builder ext(Object ext) {
            this.options.setExt(ext);
            return this;
        }

        public Builder elementList(List<Element> elementList) {
            this.options.setElementList(elementList);
            return this;
        }

        public Builder resultType(String resultType) {
            this.options.setResultType(resultType);
            return this;
        }

        public Builder seriesAmount(Integer seriesAmount) {
            this.options.setSeriesAmount(seriesAmount);
            return this;
        }

        public Builder aspectRatio(String aspectRatio) {
            this.options.setAspectRatio(aspectRatio);
            return this;
        }

        public Builder resolution(String resolution) {
            this.options.setResolution(resolution);
            return this;
        }

        public Builder shortSideSize(String shortSideSize) {
            this.options.setShortSideSize(shortSideSize);
            return this;
        }

        public Builder scale(Float scale) {
            this.options.setScale(scale);
            return this;
        }

        public Builder modelVersion(String modelVersion) {
            this.options.setModelVersion(modelVersion);
            return this;
        }

        public Builder noiseLevel(Integer noiseLevel) {
            this.options.setNoiseLevel(noiseLevel);
            return this;
        }

        public Builder refPromptWeight(Float refPromptWeight) {
            this.options.setRefPromptWeight(refPromptWeight);
            return this;
        }

        public Builder referenceEdge(ReferenceEdge referenceEdge) {
            this.options.setReferenceEdge(referenceEdge);
            return this;
        }

        public Builder generateMode(String generateMode) {
            this.options.setGenerateMode(generateMode);
            return this;
        }

        public Builder auxiliaryParameters(String auxiliaryParameters) {
            this.options.setAuxiliaryParameters(auxiliaryParameters);
            return this;
        }

        public Builder title(String title) {
            this.options.setTitle(title);
            return this;
        }

        public Builder subTitle(String subTitle) {
            this.options.setSubTitle(subTitle);
            return this;
        }

        public Builder bodyText(String bodyText) {
            this.options.setBodyText(bodyText);
            return this;
        }

        public Builder promptTextZh(String promptTextZh) {
            this.options.setPromptTextZh(promptTextZh);
            return this;
        }

        public Builder promptTextEn(String promptTextEn) {
            this.options.setPromptTextEn(promptTextEn);
            return this;
        }

        public Builder whRatios(String whRatios) {
            this.options.setWhRatios(whRatios);
            return this;
        }

        public Builder loraName(String loraName) {
            this.options.setLoraName(loraName);
            return this;
        }

        public Builder loraWeight(Float loraWeight) {
            this.options.setLoraWeight(loraWeight);
            return this;
        }

        public Builder ctrlRatio(Float ctrlRatio) {
            this.options.setCtrlRatio(ctrlRatio);
            return this;
        }

        public Builder ctrlStep(Float ctrlStep) {
            this.options.setCtrlStep(ctrlStep);
            return this;
        }

        public Builder creativeTitleLayout(Boolean creativeTitleLayout) {
            this.options.setCreativeTitleLayout(creativeTitleLayout);
            return this;
        }

        public Builder fastMode(Boolean fastMode) {
            this.options.setFastMode(fastMode);
            return this;
        }

        public Builder dilateFlag(Boolean dilateFlag) {
            this.options.setDilateFlag(dilateFlag);
            return this;
        }

        public Builder restoreFace(Boolean restoreFace) {
            this.options.setRestoreFace(restoreFace);
            return this;
        }

        public Builder gender(String gender) {
            this.options.setGender(gender);
            return this;
        }

        public Builder clothesType(List<String> clothesType) {
            this.options.setClothesType(clothesType);
            return this;
        }

        public Builder resources(List<Resource> resources) {
            this.options.setResources(resources);
            return this;
        }

        public Builder skinRetouch(Boolean skinRetouch) {
            this.options.setSkinRetouch(skinRetouch);
            return this;
        }

        public Builder steps(Integer steps) {
            this.options.setSteps(steps);
            return this;
        }

        public Builder fontName(String fontName) {
            this.options.setFontName(fontName);
            return this;
        }

        public Builder ttfUrl(String ttfUrl) {
            this.options.setTtfUrl(ttfUrl);
            return this;
        }

        public Builder imageShortSize(Integer imageShortSize) {
            this.options.setImageShortSize(imageShortSize);
            return this;
        }

        public Builder alphaChannel(Boolean alphaChannel) {
            this.options.setAlphaChannel(alphaChannel);
            return this;
        }

        public Builder trainingFileIds(List<String> trainingFileIds) {
            this.options.setTrainingFileIds(trainingFileIds);
            return this;
        }

        public Builder invokeMode(InvokeMode invokeMode) {
            this.options.setInvokeMode(invokeMode);
            return this;
        }

        public Builder requestType(RequestType requestType) {
            this.options.setRequestType(requestType);
            return this;
        }

        public DashScopeImageOptions build() {
            return this.options;
        }
    }
}
