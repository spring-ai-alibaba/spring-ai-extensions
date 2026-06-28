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
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageApiSpec.ImageRequest.GenerationInput;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageApiSpec.ImageRequest.Image;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageApiSpec.ImageRequest.Input;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageApiSpec.ImageRequest.Parameters;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageApiSpec.ImageRequest.ReferenceEdge;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageApiSpec.ImageRequest.Resource;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageApiSpec.ImageRequest.Text;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageApiSpec.InvokeMode;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageApiSpec.Message;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageApiSpec.Message.Content;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageApiSpec.RequestType;
import com.alibaba.cloud.ai.dashscope.spec.DashScopeModel.ImageModel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jspecify.annotations.Nullable;
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
@JsonInclude(Include.NON_NULL)
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
     * The index of the style to use for generation.
     */
    @JsonProperty("style_index")
    private final @Nullable Integer styleIndex;

    /**
     * The URL of the style reference image.
     */
    @JsonProperty("style_ref_url")
    private final @Nullable String styleRefUrl;

    @JsonProperty("base_image_url")
    private final @Nullable String baseImageUrl;

    /**
     * The list of image URLs to edit.
     */
    @JsonProperty("images")
    private final @Nullable List<String> images;

    @JsonProperty("mask_image_url")
    private final @Nullable String maskImageUrl;

    @JsonProperty("sketch_image_url")
    private final @Nullable String sketchImageUrl;

    /**
     * The URL of the template model image.
     */
    @JsonProperty("template_image_url")
    private final @Nullable String templateImageUrl;

    /**
     * The list of shoe image URLs from multiple angles.
     */
    @JsonProperty("shoe_image_url")
    private final @Nullable List<String> shoeImageUrl;

    /**
     * The URL of the face image to replace in the generated result.
     */
    @JsonProperty("face_image_url")
    private final @Nullable String faceImageUrl;

    /**
     * The URL of the background reference image.
     */
    @JsonProperty("background_image_url")
    private final @Nullable String backgroundImageUrl;

    /**
     * Enter the URL address of the retained area mask image URL or image base64 data.
     */
    @JsonProperty("foreground_url")
    private final @Nullable String foregroundUrl;

    /**
     * The URL of the person image.
     */
    @JsonProperty("person_image_url")
    private final @Nullable String personImageUrl;

    /**
     * The URL of the top garment image.
     */
    @JsonProperty("top_garment_url")
    private final @Nullable String topGarmentUrl;

    /**
     * The URL of the bottom garment image.
     */
    @JsonProperty("bottom_garment_url")
    private final @Nullable String bottomGarmentUrl;

    /**
     * The URL of the coarse image.
     */
    @JsonProperty("coarse_image_url")
    private final @Nullable String coarseImageUrl;

    /**
     * The list of user-provided URLs.
     */
    @JsonProperty("user_urls")
    private final @Nullable List<String> userUrls;

    /**
     * refer image,Support jpg, png, tiff, webp
     */
    @JsonProperty("ref_img")
    private final @Nullable String refImg;

    /**
     * The predefined face ID to use for face generation.
     */
    @JsonProperty("predefined_face_id")
    private final @Nullable String predefinedFaceId;

    /**
     * The prompt describing the face appearance.
     */
    @JsonProperty("face_prompt")
    private final @Nullable String facePrompt;

    /**
     * The weight scale for the background reference image style.
     */
    @JsonProperty("bgstyle_scale")
    private final @Nullable Float bgstyleScale;

    /**
     * Whether the input image is a real person photo.
     */
    @JsonProperty("realPerson")
    private final @Nullable Boolean realPerson;

    /**
     * Sets the random number seed to use for generation. Must be between 0 and 4294967290.
     */
    @JsonProperty("seed")
    private final @Nullable Integer seed;

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

    /**
     * The text input for text-based image generation.
     */
    @JsonProperty("text")
    private final @Nullable String text;

    @JsonProperty("prompt_extend")
    private final @Nullable Boolean promptExtend;

    @JsonProperty("watermark")
    private final @Nullable Boolean watermark;

    @JsonProperty("function")
    private final @Nullable String function;

    @JsonProperty("sketch_weight")
    private final @Nullable Integer sketchWeight;

    @JsonProperty("sketch_extraction")
    private final @Nullable Boolean sketchExtraction;

    @JsonProperty("sketch_color")
    private final @Nullable Integer @Nullable [][] sketchColor;

    @JsonProperty("mask_color")
    private final @Nullable Integer @Nullable [][] maskColor;

    @JsonProperty("bbox_list")
    private final @Nullable Integer @Nullable [][][] bboxList;

    @JsonProperty("max_images")
    private final @Nullable Integer maxImages;

    @JsonProperty("enable_interleave")
    private final @Nullable Boolean enableInterleave;

    @JsonProperty("enable_sequential")
    private final @Nullable Boolean enableSequential;

    @JsonProperty("color_palette")
    private final @Nullable List<ColorPaletteItem> colorPalette;

    @JsonProperty("thinking_mode")
    private final @Nullable Boolean thinkingMode;

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

    /**
     * The source language for translation.
     */
    @JsonProperty("source_lang")
    private final @Nullable String sourceLang;

    /**
     * The target language for translation.
     */
    @JsonProperty("target_lang")
    private final @Nullable String targetLang;

    /**
     * Extensional parameters for the API call.
     */
    @JsonProperty("ext")
    private final @Nullable Object ext;

    /**
     * The list of element items specifying subjects to preserve during generation.
     * The total count of reference images and element_list entries must not exceed 10.
     */
    @JsonProperty("element_list")
    private final @Nullable List<Element> elementList;

    /**
     * The result type for image generation, e.g. "single" or "series".
     */
    @JsonProperty("result_type")
    private final @Nullable String resultType;

    /**
     * The number of series (frames) to generate for series output.
     */
    @JsonProperty("series_amount")
    private final @Nullable Integer seriesAmount;

    /**
     * The aspect ratio for the generated images, e.g. "1:1", "16:9".
     */
    @JsonProperty("aspect_ratio")
    private final @Nullable String aspectRatio;

    /**
     * The resolution for the generated images, e.g. "1K", "2K".
     */
    @JsonProperty("resolution")
    private final @Nullable String resolution;

    /**
     * The short side size for the generated images, e.g. "512", "768".
     */
    @JsonProperty("short_side_size")
    private final @Nullable String shortSideSize;

    /**
     * The generation strength scale control.
     */
    @JsonProperty("scale")
    private final @Nullable Float scale;

    /**
     * The model version.
     */
    @JsonProperty("model_version")
    private final @Nullable String modelVersion;

    /**
     * This parameter introduces random variations during the image-guided process.
     */
    @JsonProperty("noise_level")
    private final @Nullable Integer noiseLevel;

    /**
     * The weight of the refer prompt.
     */
    @JsonProperty("ref_prompt_weight")
    private final @Nullable Float refPromptWeight;

    /**
     * Reference edge configuration for poster generation.
     */
    @JsonProperty("reference_edge")
    private final @Nullable ReferenceEdge referenceEdge;

    /**
     * The poster generation mode.
     */
    @JsonProperty("generate_mode")
    private final @Nullable String generateMode;

    /**
     * Auxiliary parameters for resolution enhancement or HD repair of the poster image. Limited to 1 entry.
     */
    @JsonProperty("auxiliary_parameters")
    private final @Nullable String auxiliaryParameters;

    /**
     * The main title of the poster.
     */
    @JsonProperty("title")
    private final @Nullable String title;

    /**
     * The subtitle of the poster.
     */
    @JsonProperty("sub_title")
    private final @Nullable String subTitle;

    /**
     * The body text of the poster.
     */
    @JsonProperty("body_text")
    private final @Nullable String bodyText;

    /**
     * The Chinese prompt text for the poster.
     */
    @JsonProperty("prompt_text_zh")
    private final @Nullable String promptTextZh;

    /**
     * The English prompt text for the poster.
     */
    @JsonProperty("prompt_text_en")
    private final @Nullable String promptTextEn;

    /**
     * The layout aspect ratio for the generated poster.
     */
    @JsonProperty("wh_ratios")
    private final @Nullable String whRatios;

    /**
     * The poster style name (LoRA name). Must be used together with {@code loraWeight}.
     */
    @JsonProperty("lora_name")
    private final @Nullable String loraName;

    /**
     * The poster style weight (LoRA weight). Must be used together with {@code loraName}.
     */
    @JsonProperty("lora_weight")
    private final @Nullable Float loraWeight;

    /**
     * The whitespace effect weight, used to control the poster whitespace effect.
     */
    @JsonProperty("ctrl_ratio")
    private final @Nullable Float ctrlRatio;

    /**
     * The whitespace step ratio, used to control the poster whitespace effect.
     */
    @JsonProperty("ctrl_step")
    private final @Nullable Float ctrlStep;

    /**
     * Whether to enable creative title layout for the poster.
     */
    @JsonProperty("creative_title_layout")
    private final @Nullable Boolean creativeTitleLayout;

    /**
     * Whether to enable fast mode for image erase completion.
     */
    @JsonProperty("fast_mode")
    private final @Nullable Boolean fastMode;

    /**
     * Whether to enable dilate flag for image erase completion.
     * If the erasure mask is the result of algorithmic segmentation, set it to true;
     * if the erasure mask is the result of painting, set it to false.
     */
    @JsonProperty("dilate_flag")
    private final @Nullable Boolean dilateFlag;

    /**
     * Whether to restore the faces in the model image.
     * True: Default value, retains the original face image.
     * False: Randomly generate a new face.
     */
    @JsonProperty("restore_face")
    private final @Nullable Boolean restoreFace;

    /**
     * The gender for AI try-on, e.g. "male" or "female".
     */
    @JsonProperty("gender")
    private final @Nullable String gender;

    /**
     * The list of clothes types for AI try-on.
     */
    @JsonProperty("clothes_type")
    private final @Nullable List<String> clothesType;

    /**
     * The list of resources for image generation.
     */
    @JsonProperty("resources")
    private final @Nullable List<Resource> resources;

    /**
     * Whether to enable skin retouch for image generation.
     */
    @JsonProperty("skin_retouch")
    private final @Nullable Boolean skinRetouch;

    /**
     * The number of inference steps for image generation.
     */
    @JsonProperty("steps")
    private final @Nullable Integer steps;

    /**
     * The font name for text rendering in image generation.
     */
    @JsonProperty("font_name")
    private final @Nullable String fontName;

    /**
     * The URL of the TTF font file for text rendering in image generation.
     */
    @JsonProperty("ttf_url")
    private final @Nullable String ttfUrl;

    /**
     * The short side size of the output image in pixels.
     */
    @JsonProperty("image_short_size")
    private final @Nullable Integer imageShortSize;

    /**
     * Whether to include alpha channel in the output image.
     */
    @JsonProperty("alpha_channel")
    private final @Nullable Boolean alphaChannel;

    /**
     * The list of training file IDs for fine-tuned models.
     */
    @JsonProperty("training_file_ids")
    private final @Nullable List<String> trainingFileIds;

    /**
     * Invocation mode for the API call.
     * - AUTO: automatically detect based on model
     * - SYNC: synchronous call (no async header)
     * - ASYNC: asynchronous call (with async header, returns task_id for polling)
     */
    @JsonIgnore
    private final InvokeMode invokeMode;

    /**
     * Request type for the API call.
     * - AUTO: automatically detect based on model
     * - GENERATION: generation request
     * - STANDARD: standard request
     */
    @JsonIgnore
    private final RequestType requestType;

    protected DashScopeImageOptions(@Nullable String model, @Nullable Integer n, @Nullable Integer width,
            @Nullable Integer height, @Nullable String size, @Nullable String style, @Nullable Integer styleIndex,
            @Nullable String styleRefUrl, @Nullable String baseImageUrl, @Nullable List<String> images,
            @Nullable String maskImageUrl, @Nullable String sketchImageUrl, @Nullable String templateImageUrl,
            @Nullable List<String> shoeImageUrl, @Nullable String faceImageUrl,
            @Nullable String backgroundImageUrl, @Nullable String foregroundUrl, @Nullable String personImageUrl,
            @Nullable String topGarmentUrl, @Nullable String bottomGarmentUrl, @Nullable String coarseImageUrl,
            @Nullable List<String> userUrls, @Nullable String refImg, @Nullable String predefinedFaceId,
            @Nullable String facePrompt, @Nullable Float bgstyleScale, @Nullable Boolean realPerson,
            @Nullable Integer seed, @Nullable Float refStrength, @Nullable String responseFormat,
            @Nullable String refMode, @Nullable String negativePrompt, @Nullable String text,
            @Nullable Boolean promptExtend, @Nullable Boolean watermark, @Nullable String function,
            @Nullable Integer sketchWeight, @Nullable Boolean sketchExtraction, @Nullable Integer @Nullable [][] sketchColor,
            @Nullable Integer @Nullable [][] maskColor, @Nullable Integer @Nullable [][][] bboxList, @Nullable Integer maxImages,
            @Nullable Boolean enableInterleave, @Nullable Boolean enableSequential,
            @Nullable List<ColorPaletteItem> colorPalette, @Nullable Boolean thinkingMode,
            @Nullable String outputRatio, @Nullable Float xScale, @Nullable Float yScale, @Nullable Integer angle,
            @Nullable Integer leftOffset, @Nullable Integer rightOffset, @Nullable Integer topOffset,
            @Nullable Integer bottomOffset, @Nullable Boolean bestQuality, @Nullable Boolean limitImageSize,
            @Nullable String sourceLang, @Nullable String targetLang, @Nullable Object ext,
            @Nullable List<Element> elementList, @Nullable String resultType, @Nullable Integer seriesAmount,
            @Nullable String aspectRatio, @Nullable String resolution, @Nullable String shortSideSize,
            @Nullable Float scale, @Nullable String modelVersion, @Nullable Integer noiseLevel,
            @Nullable Float refPromptWeight, @Nullable ReferenceEdge referenceEdge, @Nullable String generateMode,
            @Nullable String auxiliaryParameters, @Nullable String title, @Nullable String subTitle,
            @Nullable String bodyText, @Nullable String promptTextZh, @Nullable String promptTextEn,
            @Nullable String whRatios, @Nullable String loraName, @Nullable Float loraWeight,
            @Nullable Float ctrlRatio, @Nullable Float ctrlStep, @Nullable Boolean creativeTitleLayout,
            @Nullable Boolean fastMode, @Nullable Boolean dilateFlag, @Nullable Boolean restoreFace,
            @Nullable String gender, @Nullable List<String> clothesType, @Nullable List<Resource> resources,
            @Nullable Boolean skinRetouch, @Nullable Integer steps, @Nullable String fontName,
            @Nullable String ttfUrl, @Nullable Integer imageShortSize, @Nullable Boolean alphaChannel,
            @Nullable List<String> trainingFileIds, @Nullable InvokeMode invokeMode,
            @Nullable RequestType requestType) {

        this.model = model;
        this.n = n;
        this.width = width;
        this.height = height;
        this.size = size;
        this.style = style;
        this.styleIndex = styleIndex;
        this.styleRefUrl = styleRefUrl;
        this.baseImageUrl = baseImageUrl;
        this.images = images != null ? List.copyOf(images) : null;
        this.maskImageUrl = maskImageUrl;
        this.sketchImageUrl = sketchImageUrl;
        this.templateImageUrl = templateImageUrl;
        this.shoeImageUrl = shoeImageUrl != null ? List.copyOf(shoeImageUrl) : null;
        this.faceImageUrl = faceImageUrl;
        this.backgroundImageUrl = backgroundImageUrl;
        this.foregroundUrl = foregroundUrl;
        this.personImageUrl = personImageUrl;
        this.topGarmentUrl = topGarmentUrl;
        this.bottomGarmentUrl = bottomGarmentUrl;
        this.coarseImageUrl = coarseImageUrl;
        this.userUrls = userUrls != null ? List.copyOf(userUrls) : null;
        this.refImg = refImg;
        this.predefinedFaceId = predefinedFaceId;
        this.facePrompt = facePrompt;
        this.bgstyleScale = bgstyleScale;
        this.realPerson = realPerson;
        this.seed = seed;
        this.refStrength = refStrength;
        this.responseFormat = responseFormat;
        this.refMode = refMode;
        this.negativePrompt = negativePrompt;
        this.text = text;
        this.promptExtend = promptExtend;
        this.watermark = watermark;
        this.function = function;
        this.sketchWeight = sketchWeight;
        this.sketchExtraction = sketchExtraction;
        this.sketchColor = sketchColor;
        this.maskColor = maskColor;
        this.bboxList = bboxList;
        this.maxImages = maxImages;
        this.enableInterleave = enableInterleave;
        this.enableSequential = enableSequential;
        this.colorPalette = colorPalette != null ? List.copyOf(colorPalette) : null;
        this.thinkingMode = thinkingMode;
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
        this.sourceLang = sourceLang;
        this.targetLang = targetLang;
        this.ext = ext;
        this.elementList = elementList != null ? List.copyOf(elementList) : null;
        this.resultType = resultType;
        this.seriesAmount = seriesAmount;
        this.aspectRatio = aspectRatio;
        this.resolution = resolution;
        this.shortSideSize = shortSideSize;
        this.scale = scale;
        this.modelVersion = modelVersion;
        this.noiseLevel = noiseLevel;
        this.refPromptWeight = refPromptWeight;
        this.referenceEdge = referenceEdge;
        this.generateMode = generateMode;
        this.auxiliaryParameters = auxiliaryParameters;
        this.title = title;
        this.subTitle = subTitle;
        this.bodyText = bodyText;
        this.promptTextZh = promptTextZh;
        this.promptTextEn = promptTextEn;
        this.whRatios = whRatios;
        this.loraName = loraName;
        this.loraWeight = loraWeight;
        this.ctrlRatio = ctrlRatio;
        this.ctrlStep = ctrlStep;
        this.creativeTitleLayout = creativeTitleLayout;
        this.fastMode = fastMode;
        this.dilateFlag = dilateFlag;
        this.restoreFace = restoreFace;
        this.gender = gender;
        this.clothesType = clothesType != null ? List.copyOf(clothesType) : null;
        this.resources = resources != null ? List.copyOf(resources) : null;
        this.skinRetouch = skinRetouch;
        this.steps = steps;
        this.fontName = fontName;
        this.ttfUrl = ttfUrl;
        this.imageShortSize = imageShortSize;
        this.alphaChannel = alphaChannel;
        this.trainingFileIds = trainingFileIds != null ? List.copyOf(trainingFileIds) : null;
        this.invokeMode = invokeMode != null ? invokeMode : InvokeMode.AUTO;
        this.requestType = requestType != null ? requestType : RequestType.AUTO;
    }

    @Override
    public @Nullable String getModel() {
        return this.model;
    }

    @Override
    public @Nullable Integer getN() {
        return this.n;
    }

    @Override
    public @Nullable Integer getWidth() {
        return this.width;
    }

    @Override
    public @Nullable Integer getHeight() {
        return this.height;
    }

    public @Nullable String getSize() {
        if (this.size != null) {
            return this.size;
        }
        return (this.width != null && this.height != null) ? this.width + "*" + this.height : null;
    }

    @Override
    public @Nullable String getStyle() {
        return this.style;
    }

    public @Nullable Integer getStyleIndex() {
        return this.styleIndex;
    }

    public @Nullable String getStyleRefUrl() {
        return this.styleRefUrl;
    }

    public @Nullable String getBaseImageUrl() {
        return this.baseImageUrl;
    }

    public @Nullable List<String> getImages() {
        return this.images;
    }

    public @Nullable String getMaskImageUrl() {
        return this.maskImageUrl;
    }

    public @Nullable String getSketchImageUrl() {
        return this.sketchImageUrl;
    }

    public @Nullable String getTemplateImageUrl() {
        return this.templateImageUrl;
    }

    public @Nullable List<String> getShoeImageUrl() {
        return this.shoeImageUrl;
    }

    public @Nullable String getFaceImageUrl() {
        return this.faceImageUrl;
    }

    public @Nullable String getBackgroundImageUrl() {
        return this.backgroundImageUrl;
    }

    public @Nullable String getForegroundUrl() {
        return this.foregroundUrl;
    }

    public @Nullable String getPersonImageUrl() {
        return this.personImageUrl;
    }

    public @Nullable String getTopGarmentUrl() {
        return this.topGarmentUrl;
    }

    public @Nullable String getBottomGarmentUrl() {
        return this.bottomGarmentUrl;
    }

    public @Nullable String getCoarseImageUrl() {
        return this.coarseImageUrl;
    }

    public @Nullable List<String> getUserUrls() {
        return this.userUrls;
    }

    public @Nullable String getRefImg() {
        return this.refImg;
    }

    public @Nullable String getPredefinedFaceId() {
        return this.predefinedFaceId;
    }

    public @Nullable String getFacePrompt() {
        return this.facePrompt;
    }

    public @Nullable Float getBgstyleScale() {
        return this.bgstyleScale;
    }

    public @Nullable Boolean getRealPerson() {
        return this.realPerson;
    }

    public @Nullable Integer getSeed() {
        return this.seed;
    }

    public @Nullable Float getRefStrength() {
        return this.refStrength;
    }

    @Override
    public @Nullable String getResponseFormat() {
        return this.responseFormat;
    }

    public @Nullable String getRefMode() {
        return this.refMode;
    }

    public @Nullable String getNegativePrompt() {
        return this.negativePrompt;
    }

    public @Nullable String getText() {
        return this.text;
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

    public @Nullable Integer getSketchWeight() {
        return this.sketchWeight;
    }

    public @Nullable Boolean getSketchExtraction() {
        return this.sketchExtraction;
    }

    public @Nullable Integer @Nullable [][] getSketchColor() {
        return this.sketchColor;
    }

    public @Nullable Integer @Nullable [][] getMaskColor() {
        return this.maskColor;
    }

    public @Nullable Integer @Nullable [][][] getBboxList() {
        return this.bboxList;
    }

    public @Nullable Integer getMaxImages() {
        return this.maxImages;
    }

    public @Nullable Boolean getEnableInterleave() {
        return this.enableInterleave;
    }

    public @Nullable Boolean getEnableSequential() {
        return this.enableSequential;
    }

    public @Nullable List<ColorPaletteItem> getColorPalette() {
        return this.colorPalette;
    }

    public @Nullable Boolean getThinkingMode() {
        return this.thinkingMode;
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

    public @Nullable String getSourceLang() {
        return this.sourceLang;
    }

    public @Nullable String getTargetLang() {
        return this.targetLang;
    }

    public @Nullable Object getExt() {
        return this.ext;
    }

    public @Nullable List<Element> getElementList() {
        return this.elementList;
    }

    public @Nullable String getResultType() {
        return this.resultType;
    }

    public @Nullable Integer getSeriesAmount() {
        return this.seriesAmount;
    }

    public @Nullable String getAspectRatio() {
        return this.aspectRatio;
    }

    public @Nullable String getResolution() {
        return this.resolution;
    }

    public @Nullable String getShortSideSize() {
        return this.shortSideSize;
    }

    public @Nullable Float getScale() {
        return this.scale;
    }

    public @Nullable String getModelVersion() {
        return this.modelVersion;
    }

    public @Nullable Integer getNoiseLevel() {
        return this.noiseLevel;
    }

    public @Nullable Float getRefPromptWeight() {
        return this.refPromptWeight;
    }

    public @Nullable ReferenceEdge getReferenceEdge() {
        return this.referenceEdge;
    }

    public @Nullable String getGenerateMode() {
        return this.generateMode;
    }

    public @Nullable String getAuxiliaryParameters() {
        return this.auxiliaryParameters;
    }

    public @Nullable String getTitle() {
        return this.title;
    }

    public @Nullable String getSubTitle() {
        return this.subTitle;
    }

    public @Nullable String getBodyText() {
        return this.bodyText;
    }

    public @Nullable String getPromptTextZh() {
        return this.promptTextZh;
    }

    public @Nullable String getPromptTextEn() {
        return this.promptTextEn;
    }

    public @Nullable String getWhRatios() {
        return this.whRatios;
    }

    public @Nullable String getLoraName() {
        return this.loraName;
    }

    public @Nullable Float getLoraWeight() {
        return this.loraWeight;
    }

    public @Nullable Float getCtrlRatio() {
        return this.ctrlRatio;
    }

    public @Nullable Float getCtrlStep() {
        return this.ctrlStep;
    }

    public @Nullable Boolean getCreativeTitleLayout() {
        return this.creativeTitleLayout;
    }

    public @Nullable Boolean getFastMode() {
        return this.fastMode;
    }

    public @Nullable Boolean getDilateFlag() {
        return this.dilateFlag;
    }

    public @Nullable Boolean getRestoreFace() {
        return this.restoreFace;
    }

    public @Nullable String getGender() {
        return this.gender;
    }

    public @Nullable List<String> getClothesType() {
        return this.clothesType;
    }

    public @Nullable List<Resource> getResources() {
        return this.resources;
    }

    public @Nullable Boolean getSkinRetouch() {
        return this.skinRetouch;
    }

    public @Nullable Integer getSteps() {
        return this.steps;
    }

    public @Nullable String getFontName() {
        return this.fontName;
    }

    public @Nullable String getTtfUrl() {
        return this.ttfUrl;
    }

    public @Nullable Integer getImageShortSize() {
        return this.imageShortSize;
    }

    public @Nullable Boolean getAlphaChannel() {
        return this.alphaChannel;
    }

    public @Nullable List<String> getTrainingFileIds() {
        return this.trainingFileIds;
    }

    public InvokeMode getInvokeMode() {
        return this.invokeMode;
    }

    public RequestType getRequestType() {
        return this.requestType;
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
        Input.Builder inputBuilder = Input.builder();

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
                inputBuilder.image(new Image(this.baseImageUrl));
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
                inputBuilder.text(Text.builder()
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
        List<Content> content = new ArrayList<>();
        content.add(new Content(prompt, null));

        if (this.baseImageUrl != null) {
            content.add(new Content(null, this.baseImageUrl));
        }

        if (!CollectionUtils.isEmpty(this.images)) {
            this.images.forEach(image -> content.add(new Content(null, image)));
        }

        List<Message> messages = List.of(new Message("user", content));

        GenerationInput input = new GenerationInput(messages, this.elementList);

        return ImageRequest.builder()
                .model(this.model)
                .input(input)
                .parameters(toImageRequestParameter())
                .trainingFileIds(this.trainingFileIds)
                .build();
    }

    protected Parameters toImageRequestParameter() {
        Parameters.Builder paramBuilder = Parameters.builder();

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

    public Builder mutate() {
        return builder()
                .model(this.model)
                .n(this.n)
                .width(this.width)
                .height(this.height)
                .size(this.size)
                .style(this.style)
                .styleIndex(this.styleIndex)
                .styleRefUrl(this.styleRefUrl)
                .baseImageUrl(this.baseImageUrl)
                .images(this.images)
                .maskImageUrl(this.maskImageUrl)
                .sketchImageUrl(this.sketchImageUrl)
                .templateImageUrl(this.templateImageUrl)
                .shoeImageUrl(this.shoeImageUrl)
                .faceImageUrl(this.faceImageUrl)
                .backgroundImageUrl(this.backgroundImageUrl)
                .foregroundUrl(this.foregroundUrl)
                .personImageUrl(this.personImageUrl)
                .topGarmentUrl(this.topGarmentUrl)
                .bottomGarmentUrl(this.bottomGarmentUrl)
                .coarseImageUrl(this.coarseImageUrl)
                .userUrls(this.userUrls)
                .refImg(this.refImg)
                .predefinedFaceId(this.predefinedFaceId)
                .facePrompt(this.facePrompt)
                .bgstyleScale(this.bgstyleScale)
                .realPerson(this.realPerson)
                .seed(this.seed)
                .refStrength(this.refStrength)
                .responseFormat(this.responseFormat)
                .refMode(this.refMode)
                .negativePrompt(this.negativePrompt)
                .text(this.text)
                .promptExtend(this.promptExtend)
                .watermark(this.watermark)
                .function(this.function)
                .sketchWeight(this.sketchWeight)
                .sketchExtraction(this.sketchExtraction)
                .sketchColor(this.sketchColor)
                .maskColor(this.maskColor)
                .bboxList(this.bboxList)
                .maxImages(this.maxImages)
                .enableInterleave(this.enableInterleave)
                .enableSequential(this.enableSequential)
                .colorPalette(this.colorPalette)
                .thinkingMode(this.thinkingMode)
                .outputRatio(this.outputRatio)
                .xScale(this.xScale)
                .yScale(this.yScale)
                .angle(this.angle)
                .leftOffset(this.leftOffset)
                .rightOffset(this.rightOffset)
                .topOffset(this.topOffset)
                .bottomOffset(this.bottomOffset)
                .bestQuality(this.bestQuality)
                .limitImageSize(this.limitImageSize)
                .sourceLang(this.sourceLang)
                .targetLang(this.targetLang)
                .ext(this.ext)
                .elementList(this.elementList)
                .resultType(this.resultType)
                .seriesAmount(this.seriesAmount)
                .aspectRatio(this.aspectRatio)
                .resolution(this.resolution)
                .shortSideSize(this.shortSideSize)
                .scale(this.scale)
                .modelVersion(this.modelVersion)
                .noiseLevel(this.noiseLevel)
                .refPromptWeight(this.refPromptWeight)
                .referenceEdge(this.referenceEdge)
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
                .trainingFileIds(this.trainingFileIds)
                .invokeMode(this.invokeMode)
                .requestType(this.requestType);
    }

    public static class Builder {

        private @Nullable String model;

        private @Nullable Integer n;

        private @Nullable Integer width;

        private @Nullable Integer height;

        private @Nullable String size;

        private @Nullable String style;

        private @Nullable Integer styleIndex;

        private @Nullable String styleRefUrl;

        private @Nullable String baseImageUrl;

        private @Nullable List<String> images;

        private @Nullable String maskImageUrl;

        private @Nullable String sketchImageUrl;

        private @Nullable String templateImageUrl;

        private @Nullable List<String> shoeImageUrl;

        private @Nullable String faceImageUrl;

        private @Nullable String backgroundImageUrl;

        private @Nullable String foregroundUrl;

        private @Nullable String personImageUrl;

        private @Nullable String topGarmentUrl;

        private @Nullable String bottomGarmentUrl;

        private @Nullable String coarseImageUrl;

        private @Nullable List<String> userUrls;

        private @Nullable String refImg;

        private @Nullable String predefinedFaceId;

        private @Nullable String facePrompt;

        private @Nullable Float bgstyleScale;

        private @Nullable Boolean realPerson;

        private @Nullable Integer seed;

        private @Nullable Float refStrength;

        private @Nullable String responseFormat;

        private @Nullable String refMode;

        private @Nullable String negativePrompt;

        private @Nullable String text;

        private @Nullable Boolean promptExtend;

        private @Nullable Boolean watermark;

        private @Nullable String function;

        private @Nullable Integer sketchWeight;

        private @Nullable Boolean sketchExtraction;

        private @Nullable Integer @Nullable [][] sketchColor;

        private @Nullable Integer @Nullable [][] maskColor;

        private @Nullable Integer @Nullable [][][] bboxList;

        private @Nullable Integer maxImages;

        private @Nullable Boolean enableInterleave;

        private @Nullable Boolean enableSequential;

        private @Nullable List<ColorPaletteItem> colorPalette;

        private @Nullable Boolean thinkingMode;

        private @Nullable String outputRatio;

        private @Nullable Float xScale;

        private @Nullable Float yScale;

        private @Nullable Integer angle;

        private @Nullable Integer leftOffset;

        private @Nullable Integer rightOffset;

        private @Nullable Integer topOffset;

        private @Nullable Integer bottomOffset;

        private @Nullable Boolean bestQuality;

        private @Nullable Boolean limitImageSize;

        private @Nullable String sourceLang;

        private @Nullable String targetLang;

        private @Nullable Object ext;

        private @Nullable List<Element> elementList;

        private @Nullable String resultType;

        private @Nullable Integer seriesAmount;

        private @Nullable String aspectRatio;

        private @Nullable String resolution;

        private @Nullable String shortSideSize;

        private @Nullable Float scale;

        private @Nullable String modelVersion;

        private @Nullable Integer noiseLevel;

        private @Nullable Float refPromptWeight;

        private @Nullable ReferenceEdge referenceEdge;

        private @Nullable String generateMode;

        private @Nullable String auxiliaryParameters;

        private @Nullable String title;

        private @Nullable String subTitle;

        private @Nullable String bodyText;

        private @Nullable String promptTextZh;

        private @Nullable String promptTextEn;

        private @Nullable String whRatios;

        private @Nullable String loraName;

        private @Nullable Float loraWeight;

        private @Nullable Float ctrlRatio;

        private @Nullable Float ctrlStep;

        private @Nullable Boolean creativeTitleLayout;

        private @Nullable Boolean fastMode;

        private @Nullable Boolean dilateFlag;

        private @Nullable Boolean restoreFace;

        private @Nullable String gender;

        private @Nullable List<String> clothesType;

        private @Nullable List<Resource> resources;

        private @Nullable Boolean skinRetouch;

        private @Nullable Integer steps;

        private @Nullable String fontName;

        private @Nullable String ttfUrl;

        private @Nullable Integer imageShortSize;

        private @Nullable Boolean alphaChannel;

        private @Nullable List<String> trainingFileIds;

        private InvokeMode invokeMode = InvokeMode.AUTO;

        private RequestType requestType = RequestType.AUTO;

        private Builder() {
        }

        public Builder from(DashScopeImageOptions fromOptions) {
            this.model = fromOptions.getModel();
            this.n = fromOptions.getN();
            this.width = fromOptions.getWidth();
            this.height = fromOptions.getHeight();
            this.size = fromOptions.size;
            this.style = fromOptions.getStyle();
            this.styleIndex = fromOptions.getStyleIndex();
            this.styleRefUrl = fromOptions.getStyleRefUrl();
            this.baseImageUrl = fromOptions.getBaseImageUrl();
            this.images = fromOptions.getImages();
            this.maskImageUrl = fromOptions.getMaskImageUrl();
            this.sketchImageUrl = fromOptions.getSketchImageUrl();
            this.templateImageUrl = fromOptions.getTemplateImageUrl();
            this.shoeImageUrl = fromOptions.getShoeImageUrl();
            this.faceImageUrl = fromOptions.getFaceImageUrl();
            this.backgroundImageUrl = fromOptions.getBackgroundImageUrl();
            this.foregroundUrl = fromOptions.getForegroundUrl();
            this.personImageUrl = fromOptions.getPersonImageUrl();
            this.topGarmentUrl = fromOptions.getTopGarmentUrl();
            this.bottomGarmentUrl = fromOptions.getBottomGarmentUrl();
            this.coarseImageUrl = fromOptions.getCoarseImageUrl();
            this.userUrls = fromOptions.getUserUrls();
            this.refImg = fromOptions.getRefImg();
            this.predefinedFaceId = fromOptions.getPredefinedFaceId();
            this.facePrompt = fromOptions.getFacePrompt();
            this.bgstyleScale = fromOptions.getBgstyleScale();
            this.realPerson = fromOptions.getRealPerson();
            this.seed = fromOptions.getSeed();
            this.refStrength = fromOptions.getRefStrength();
            this.responseFormat = fromOptions.getResponseFormat();
            this.refMode = fromOptions.getRefMode();
            this.negativePrompt = fromOptions.getNegativePrompt();
            this.text = fromOptions.getText();
            this.promptExtend = fromOptions.getPromptExtend();
            this.watermark = fromOptions.getWatermark();
            this.function = fromOptions.getFunction();
            this.sketchWeight = fromOptions.getSketchWeight();
            this.sketchExtraction = fromOptions.getSketchExtraction();
            this.sketchColor = fromOptions.getSketchColor();
            this.maskColor = fromOptions.getMaskColor();
            this.bboxList = fromOptions.getBboxList();
            this.maxImages = fromOptions.getMaxImages();
            this.enableInterleave = fromOptions.getEnableInterleave();
            this.enableSequential = fromOptions.getEnableSequential();
            this.colorPalette = fromOptions.getColorPalette();
            this.thinkingMode = fromOptions.getThinkingMode();
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
            this.sourceLang = fromOptions.getSourceLang();
            this.targetLang = fromOptions.getTargetLang();
            this.ext = fromOptions.getExt();
            this.elementList = fromOptions.getElementList();
            this.resultType = fromOptions.getResultType();
            this.seriesAmount = fromOptions.getSeriesAmount();
            this.aspectRatio = fromOptions.getAspectRatio();
            this.resolution = fromOptions.getResolution();
            this.shortSideSize = fromOptions.getShortSideSize();
            this.scale = fromOptions.getScale();
            this.modelVersion = fromOptions.getModelVersion();
            this.noiseLevel = fromOptions.getNoiseLevel();
            this.refPromptWeight = fromOptions.getRefPromptWeight();
            this.referenceEdge = fromOptions.getReferenceEdge();
            this.generateMode = fromOptions.getGenerateMode();
            this.auxiliaryParameters = fromOptions.getAuxiliaryParameters();
            this.title = fromOptions.getTitle();
            this.subTitle = fromOptions.getSubTitle();
            this.bodyText = fromOptions.getBodyText();
            this.promptTextZh = fromOptions.getPromptTextZh();
            this.promptTextEn = fromOptions.getPromptTextEn();
            this.whRatios = fromOptions.getWhRatios();
            this.loraName = fromOptions.getLoraName();
            this.loraWeight = fromOptions.getLoraWeight();
            this.ctrlRatio = fromOptions.getCtrlRatio();
            this.ctrlStep = fromOptions.getCtrlStep();
            this.creativeTitleLayout = fromOptions.getCreativeTitleLayout();
            this.fastMode = fromOptions.getFastMode();
            this.dilateFlag = fromOptions.getDilateFlag();
            this.restoreFace = fromOptions.getRestoreFace();
            this.gender = fromOptions.getGender();
            this.clothesType = fromOptions.getClothesType();
            this.resources = fromOptions.getResources();
            this.skinRetouch = fromOptions.getSkinRetouch();
            this.steps = fromOptions.getSteps();
            this.fontName = fromOptions.getFontName();
            this.ttfUrl = fromOptions.getTtfUrl();
            this.imageShortSize = fromOptions.getImageShortSize();
            this.alphaChannel = fromOptions.getAlphaChannel();
            this.trainingFileIds = fromOptions.getTrainingFileIds();
            this.invokeMode = fromOptions.getInvokeMode();
            this.requestType = fromOptions.getRequestType();
            return this;
        }

        public Builder merge(@Nullable ImageOptions from) {
            if (from == null) {
                return this;
            }
            // ImageOptions interface fields
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
            // DashScopeImageOptions-specific fields
            if (from instanceof DashScopeImageOptions castFrom) {
                if (castFrom.size != null) {
                    this.size = castFrom.size;
                }
                if (castFrom.getStyleIndex() != null) {
                    this.styleIndex = castFrom.getStyleIndex();
                }
                if (castFrom.getStyleRefUrl() != null) {
                    this.styleRefUrl = castFrom.getStyleRefUrl();
                }
                if (castFrom.getBaseImageUrl() != null) {
                    this.baseImageUrl = castFrom.getBaseImageUrl();
                }
                if (castFrom.getImages() != null) {
                    this.images = castFrom.getImages();
                }
                if (castFrom.getMaskImageUrl() != null) {
                    this.maskImageUrl = castFrom.getMaskImageUrl();
                }
                if (castFrom.getSketchImageUrl() != null) {
                    this.sketchImageUrl = castFrom.getSketchImageUrl();
                }
                if (castFrom.getTemplateImageUrl() != null) {
                    this.templateImageUrl = castFrom.getTemplateImageUrl();
                }
                if (castFrom.getShoeImageUrl() != null) {
                    this.shoeImageUrl = castFrom.getShoeImageUrl();
                }
                if (castFrom.getFaceImageUrl() != null) {
                    this.faceImageUrl = castFrom.getFaceImageUrl();
                }
                if (castFrom.getBackgroundImageUrl() != null) {
                    this.backgroundImageUrl = castFrom.getBackgroundImageUrl();
                }
                if (castFrom.getForegroundUrl() != null) {
                    this.foregroundUrl = castFrom.getForegroundUrl();
                }
                if (castFrom.getPersonImageUrl() != null) {
                    this.personImageUrl = castFrom.getPersonImageUrl();
                }
                if (castFrom.getTopGarmentUrl() != null) {
                    this.topGarmentUrl = castFrom.getTopGarmentUrl();
                }
                if (castFrom.getBottomGarmentUrl() != null) {
                    this.bottomGarmentUrl = castFrom.getBottomGarmentUrl();
                }
                if (castFrom.getCoarseImageUrl() != null) {
                    this.coarseImageUrl = castFrom.getCoarseImageUrl();
                }
                if (castFrom.getUserUrls() != null) {
                    this.userUrls = castFrom.getUserUrls();
                }
                if (castFrom.getRefImg() != null) {
                    this.refImg = castFrom.getRefImg();
                }
                if (castFrom.getPredefinedFaceId() != null) {
                    this.predefinedFaceId = castFrom.getPredefinedFaceId();
                }
                if (castFrom.getFacePrompt() != null) {
                    this.facePrompt = castFrom.getFacePrompt();
                }
                if (castFrom.getBgstyleScale() != null) {
                    this.bgstyleScale = castFrom.getBgstyleScale();
                }
                if (castFrom.getRealPerson() != null) {
                    this.realPerson = castFrom.getRealPerson();
                }
                if (castFrom.getSeed() != null) {
                    this.seed = castFrom.getSeed();
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
                if (castFrom.getText() != null) {
                    this.text = castFrom.getText();
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
                if (castFrom.getBboxList() != null) {
                    this.bboxList = castFrom.getBboxList();
                }
                if (castFrom.getMaxImages() != null) {
                    this.maxImages = castFrom.getMaxImages();
                }
                if (castFrom.getEnableInterleave() != null) {
                    this.enableInterleave = castFrom.getEnableInterleave();
                }
                if (castFrom.getEnableSequential() != null) {
                    this.enableSequential = castFrom.getEnableSequential();
                }
                if (castFrom.getColorPalette() != null) {
                    this.colorPalette = castFrom.getColorPalette();
                }
                if (castFrom.getThinkingMode() != null) {
                    this.thinkingMode = castFrom.getThinkingMode();
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
                if (castFrom.getSourceLang() != null) {
                    this.sourceLang = castFrom.getSourceLang();
                }
                if (castFrom.getTargetLang() != null) {
                    this.targetLang = castFrom.getTargetLang();
                }
                if (castFrom.getExt() != null) {
                    this.ext = castFrom.getExt();
                }
                if (castFrom.getElementList() != null) {
                    this.elementList = castFrom.getElementList();
                }
                if (castFrom.getResultType() != null) {
                    this.resultType = castFrom.getResultType();
                }
                if (castFrom.getSeriesAmount() != null) {
                    this.seriesAmount = castFrom.getSeriesAmount();
                }
                if (castFrom.getAspectRatio() != null) {
                    this.aspectRatio = castFrom.getAspectRatio();
                }
                if (castFrom.getResolution() != null) {
                    this.resolution = castFrom.getResolution();
                }
                if (castFrom.getShortSideSize() != null) {
                    this.shortSideSize = castFrom.getShortSideSize();
                }
                if (castFrom.getScale() != null) {
                    this.scale = castFrom.getScale();
                }
                if (castFrom.getModelVersion() != null) {
                    this.modelVersion = castFrom.getModelVersion();
                }
                if (castFrom.getNoiseLevel() != null) {
                    this.noiseLevel = castFrom.getNoiseLevel();
                }
                if (castFrom.getRefPromptWeight() != null) {
                    this.refPromptWeight = castFrom.getRefPromptWeight();
                }
                if (castFrom.getReferenceEdge() != null) {
                    this.referenceEdge = castFrom.getReferenceEdge();
                }
                if (castFrom.getGenerateMode() != null) {
                    this.generateMode = castFrom.getGenerateMode();
                }
                if (castFrom.getAuxiliaryParameters() != null) {
                    this.auxiliaryParameters = castFrom.getAuxiliaryParameters();
                }
                if (castFrom.getTitle() != null) {
                    this.title = castFrom.getTitle();
                }
                if (castFrom.getSubTitle() != null) {
                    this.subTitle = castFrom.getSubTitle();
                }
                if (castFrom.getBodyText() != null) {
                    this.bodyText = castFrom.getBodyText();
                }
                if (castFrom.getPromptTextZh() != null) {
                    this.promptTextZh = castFrom.getPromptTextZh();
                }
                if (castFrom.getPromptTextEn() != null) {
                    this.promptTextEn = castFrom.getPromptTextEn();
                }
                if (castFrom.getWhRatios() != null) {
                    this.whRatios = castFrom.getWhRatios();
                }
                if (castFrom.getLoraName() != null) {
                    this.loraName = castFrom.getLoraName();
                }
                if (castFrom.getLoraWeight() != null) {
                    this.loraWeight = castFrom.getLoraWeight();
                }
                if (castFrom.getCtrlRatio() != null) {
                    this.ctrlRatio = castFrom.getCtrlRatio();
                }
                if (castFrom.getCtrlStep() != null) {
                    this.ctrlStep = castFrom.getCtrlStep();
                }
                if (castFrom.getCreativeTitleLayout() != null) {
                    this.creativeTitleLayout = castFrom.getCreativeTitleLayout();
                }
                if (castFrom.getFastMode() != null) {
                    this.fastMode = castFrom.getFastMode();
                }
                if (castFrom.getDilateFlag() != null) {
                    this.dilateFlag = castFrom.getDilateFlag();
                }
                if (castFrom.getRestoreFace() != null) {
                    this.restoreFace = castFrom.getRestoreFace();
                }
                if (castFrom.getGender() != null) {
                    this.gender = castFrom.getGender();
                }
                if (castFrom.getClothesType() != null) {
                    this.clothesType = castFrom.getClothesType();
                }
                if (castFrom.getResources() != null) {
                    this.resources = castFrom.getResources();
                }
                if (castFrom.getSkinRetouch() != null) {
                    this.skinRetouch = castFrom.getSkinRetouch();
                }
                if (castFrom.getSteps() != null) {
                    this.steps = castFrom.getSteps();
                }
                if (castFrom.getFontName() != null) {
                    this.fontName = castFrom.getFontName();
                }
                if (castFrom.getTtfUrl() != null) {
                    this.ttfUrl = castFrom.getTtfUrl();
                }
                if (castFrom.getImageShortSize() != null) {
                    this.imageShortSize = castFrom.getImageShortSize();
                }
                if (castFrom.getAlphaChannel() != null) {
                    this.alphaChannel = castFrom.getAlphaChannel();
                }
                if (castFrom.getTrainingFileIds() != null) {
                    this.trainingFileIds = castFrom.getTrainingFileIds();
                }
                // @JsonIgnore fields: only override if not AUTO
                if (castFrom.getInvokeMode() != InvokeMode.AUTO) {
                    this.invokeMode = castFrom.getInvokeMode();
                }
                if (castFrom.getRequestType() != RequestType.AUTO) {
                    this.requestType = castFrom.getRequestType();
                }
            }
            return this;
        }

        public Builder model(@Nullable String model) {
            this.model = model;
            return this;
        }

        public Builder n(@Nullable Integer n) {
            this.n = n;
            return this;
        }

        public Builder width(@Nullable Integer width) {
            this.width = width;
            return this;
        }

        public Builder height(@Nullable Integer height) {
            this.height = height;
            return this;
        }

        public Builder size(@Nullable String size) {
            this.size = size;
            return this;
        }

        public Builder style(@Nullable String style) {
            this.style = style;
            return this;
        }

        public Builder styleIndex(@Nullable Integer styleIndex) {
            this.styleIndex = styleIndex;
            return this;
        }

        public Builder styleRefUrl(@Nullable String styleRefUrl) {
            this.styleRefUrl = styleRefUrl;
            return this;
        }

        public Builder baseImageUrl(@Nullable String baseImageUrl) {
            this.baseImageUrl = baseImageUrl;
            return this;
        }

        public Builder images(@Nullable List<String> images) {
            this.images = images;
            return this;
        }

        public Builder maskImageUrl(@Nullable String maskImageUrl) {
            this.maskImageUrl = maskImageUrl;
            return this;
        }

        public Builder sketchImageUrl(@Nullable String sketchImageUrl) {
            this.sketchImageUrl = sketchImageUrl;
            return this;
        }

        public Builder templateImageUrl(@Nullable String templateImageUrl) {
            this.templateImageUrl = templateImageUrl;
            return this;
        }

        public Builder shoeImageUrl(@Nullable List<String> shoeImageUrl) {
            this.shoeImageUrl = shoeImageUrl;
            return this;
        }

        public Builder faceImageUrl(@Nullable String faceImageUrl) {
            this.faceImageUrl = faceImageUrl;
            return this;
        }

        public Builder backgroundImageUrl(@Nullable String backgroundImageUrl) {
            this.backgroundImageUrl = backgroundImageUrl;
            return this;
        }

        public Builder foregroundUrl(@Nullable String foregroundUrl) {
            this.foregroundUrl = foregroundUrl;
            return this;
        }

        public Builder personImageUrl(@Nullable String personImageUrl) {
            this.personImageUrl = personImageUrl;
            return this;
        }

        public Builder topGarmentUrl(@Nullable String topGarmentUrl) {
            this.topGarmentUrl = topGarmentUrl;
            return this;
        }

        public Builder bottomGarmentUrl(@Nullable String bottomGarmentUrl) {
            this.bottomGarmentUrl = bottomGarmentUrl;
            return this;
        }

        public Builder coarseImageUrl(@Nullable String coarseImageUrl) {
            this.coarseImageUrl = coarseImageUrl;
            return this;
        }

        public Builder userUrls(@Nullable List<String> userUrls) {
            this.userUrls = userUrls;
            return this;
        }

        public Builder refImg(@Nullable String refImg) {
            this.refImg = refImg;
            return this;
        }

        public Builder predefinedFaceId(@Nullable String predefinedFaceId) {
            this.predefinedFaceId = predefinedFaceId;
            return this;
        }

        public Builder facePrompt(@Nullable String facePrompt) {
            this.facePrompt = facePrompt;
            return this;
        }

        public Builder bgstyleScale(@Nullable Float bgstyleScale) {
            this.bgstyleScale = bgstyleScale;
            return this;
        }

        public Builder realPerson(@Nullable Boolean realPerson) {
            this.realPerson = realPerson;
            return this;
        }

        public Builder seed(@Nullable Integer seed) {
            this.seed = seed;
            return this;
        }

        public Builder refStrength(@Nullable Float refStrength) {
            this.refStrength = refStrength;
            return this;
        }

        public Builder responseFormat(@Nullable String responseFormat) {
            this.responseFormat = responseFormat;
            return this;
        }

        public Builder refMode(@Nullable String refMode) {
            this.refMode = refMode;
            return this;
        }

        public Builder negativePrompt(@Nullable String negativePrompt) {
            this.negativePrompt = negativePrompt;
            return this;
        }

        public Builder text(@Nullable String text) {
            this.text = text;
            return this;
        }

        public Builder promptExtend(@Nullable Boolean promptExtend) {
            this.promptExtend = promptExtend;
            return this;
        }

        public Builder watermark(@Nullable Boolean watermark) {
            this.watermark = watermark;
            return this;
        }

        public Builder function(@Nullable String function) {
            this.function = function;
            return this;
        }

        public Builder sketchWeight(@Nullable Integer sketchWeight) {
            this.sketchWeight = sketchWeight;
            return this;
        }

        public Builder sketchExtraction(@Nullable Boolean sketchExtraction) {
            this.sketchExtraction = sketchExtraction;
            return this;
        }

        public Builder sketchColor(@Nullable Integer @Nullable [][] sketchColor) {
            this.sketchColor = sketchColor;
            return this;
        }

        public Builder maskColor(@Nullable Integer @Nullable [][] maskColor) {
            this.maskColor = maskColor;
            return this;
        }

        public Builder bboxList(@Nullable Integer @Nullable [][][] bboxList) {
            this.bboxList = bboxList;
            return this;
        }

        public Builder maxImages(@Nullable Integer maxImages) {
            this.maxImages = maxImages;
            return this;
        }

        public Builder enableInterleave(@Nullable Boolean enableInterleave) {
            this.enableInterleave = enableInterleave;
            return this;
        }

        public Builder enableSequential(@Nullable Boolean enableSequential) {
            this.enableSequential = enableSequential;
            return this;
        }

        public Builder colorPalette(@Nullable List<ColorPaletteItem> colorPalette) {
            this.colorPalette = colorPalette;
            return this;
        }

        public Builder thinkingMode(@Nullable Boolean thinkingMode) {
            this.thinkingMode = thinkingMode;
            return this;
        }

        public Builder outputRatio(@Nullable String outputRatio) {
            this.outputRatio = outputRatio;
            return this;
        }

        public Builder xScale(@Nullable Float xScale) {
            this.xScale = xScale;
            return this;
        }

        public Builder yScale(@Nullable Float yScale) {
            this.yScale = yScale;
            return this;
        }

        public Builder angle(@Nullable Integer angle) {
            this.angle = angle;
            return this;
        }

        public Builder leftOffset(@Nullable Integer leftOffset) {
            this.leftOffset = leftOffset;
            return this;
        }

        public Builder rightOffset(@Nullable Integer rightOffset) {
            this.rightOffset = rightOffset;
            return this;
        }

        public Builder topOffset(@Nullable Integer topOffset) {
            this.topOffset = topOffset;
            return this;
        }

        public Builder bottomOffset(@Nullable Integer bottomOffset) {
            this.bottomOffset = bottomOffset;
            return this;
        }

        public Builder bestQuality(@Nullable Boolean bestQuality) {
            this.bestQuality = bestQuality;
            return this;
        }

        public Builder limitImageSize(@Nullable Boolean limitImageSize) {
            this.limitImageSize = limitImageSize;
            return this;
        }

        public Builder sourceLang(@Nullable String sourceLang) {
            this.sourceLang = sourceLang;
            return this;
        }

        public Builder targetLang(@Nullable String targetLang) {
            this.targetLang = targetLang;
            return this;
        }

        public Builder ext(@Nullable Object ext) {
            this.ext = ext;
            return this;
        }

        public Builder elementList(@Nullable List<Element> elementList) {
            this.elementList = elementList;
            return this;
        }

        public Builder resultType(@Nullable String resultType) {
            this.resultType = resultType;
            return this;
        }

        public Builder seriesAmount(@Nullable Integer seriesAmount) {
            this.seriesAmount = seriesAmount;
            return this;
        }

        public Builder aspectRatio(@Nullable String aspectRatio) {
            this.aspectRatio = aspectRatio;
            return this;
        }

        public Builder resolution(@Nullable String resolution) {
            this.resolution = resolution;
            return this;
        }

        public Builder shortSideSize(@Nullable String shortSideSize) {
            this.shortSideSize = shortSideSize;
            return this;
        }

        public Builder scale(@Nullable Float scale) {
            this.scale = scale;
            return this;
        }

        public Builder modelVersion(@Nullable String modelVersion) {
            this.modelVersion = modelVersion;
            return this;
        }

        public Builder noiseLevel(@Nullable Integer noiseLevel) {
            this.noiseLevel = noiseLevel;
            return this;
        }

        public Builder refPromptWeight(@Nullable Float refPromptWeight) {
            this.refPromptWeight = refPromptWeight;
            return this;
        }

        public Builder referenceEdge(@Nullable ReferenceEdge referenceEdge) {
            this.referenceEdge = referenceEdge;
            return this;
        }

        public Builder generateMode(@Nullable String generateMode) {
            this.generateMode = generateMode;
            return this;
        }

        public Builder auxiliaryParameters(@Nullable String auxiliaryParameters) {
            this.auxiliaryParameters = auxiliaryParameters;
            return this;
        }

        public Builder title(@Nullable String title) {
            this.title = title;
            return this;
        }

        public Builder subTitle(@Nullable String subTitle) {
            this.subTitle = subTitle;
            return this;
        }

        public Builder bodyText(@Nullable String bodyText) {
            this.bodyText = bodyText;
            return this;
        }

        public Builder promptTextZh(@Nullable String promptTextZh) {
            this.promptTextZh = promptTextZh;
            return this;
        }

        public Builder promptTextEn(@Nullable String promptTextEn) {
            this.promptTextEn = promptTextEn;
            return this;
        }

        public Builder whRatios(@Nullable String whRatios) {
            this.whRatios = whRatios;
            return this;
        }

        public Builder loraName(@Nullable String loraName) {
            this.loraName = loraName;
            return this;
        }

        public Builder loraWeight(@Nullable Float loraWeight) {
            this.loraWeight = loraWeight;
            return this;
        }

        public Builder ctrlRatio(@Nullable Float ctrlRatio) {
            this.ctrlRatio = ctrlRatio;
            return this;
        }

        public Builder ctrlStep(@Nullable Float ctrlStep) {
            this.ctrlStep = ctrlStep;
            return this;
        }

        public Builder creativeTitleLayout(@Nullable Boolean creativeTitleLayout) {
            this.creativeTitleLayout = creativeTitleLayout;
            return this;
        }

        public Builder fastMode(@Nullable Boolean fastMode) {
            this.fastMode = fastMode;
            return this;
        }

        public Builder dilateFlag(@Nullable Boolean dilateFlag) {
            this.dilateFlag = dilateFlag;
            return this;
        }

        public Builder restoreFace(@Nullable Boolean restoreFace) {
            this.restoreFace = restoreFace;
            return this;
        }

        public Builder gender(@Nullable String gender) {
            this.gender = gender;
            return this;
        }

        public Builder clothesType(@Nullable List<String> clothesType) {
            this.clothesType = clothesType;
            return this;
        }

        public Builder resources(@Nullable List<Resource> resources) {
            this.resources = resources;
            return this;
        }

        public Builder skinRetouch(@Nullable Boolean skinRetouch) {
            this.skinRetouch = skinRetouch;
            return this;
        }

        public Builder steps(@Nullable Integer steps) {
            this.steps = steps;
            return this;
        }

        public Builder fontName(@Nullable String fontName) {
            this.fontName = fontName;
            return this;
        }

        public Builder ttfUrl(@Nullable String ttfUrl) {
            this.ttfUrl = ttfUrl;
            return this;
        }

        public Builder imageShortSize(@Nullable Integer imageShortSize) {
            this.imageShortSize = imageShortSize;
            return this;
        }

        public Builder alphaChannel(@Nullable Boolean alphaChannel) {
            this.alphaChannel = alphaChannel;
            return this;
        }

        public Builder trainingFileIds(@Nullable List<String> trainingFileIds) {
            this.trainingFileIds = trainingFileIds;
            return this;
        }

        public Builder invokeMode(InvokeMode invokeMode) {
            Assert.notNull(invokeMode, "InvokeMode must not be null");
            this.invokeMode = invokeMode;
            return this;
        }

        public Builder requestType(RequestType requestType) {
            Assert.notNull(requestType, "RequestType must not be null");
            this.requestType = requestType;
            return this;
        }

        public DashScopeImageOptions build() {
            Assert.notNull(this.invokeMode, "InvokeMode must not be null");
            Assert.notNull(this.requestType, "RequestType must not be null");
            return new DashScopeImageOptions(this.model, this.n, this.width, this.height, this.size, this.style,
                    this.styleIndex, this.styleRefUrl, this.baseImageUrl, this.images, this.maskImageUrl,
                    this.sketchImageUrl, this.templateImageUrl, this.shoeImageUrl, this.faceImageUrl,
                    this.backgroundImageUrl, this.foregroundUrl, this.personImageUrl, this.topGarmentUrl,
                    this.bottomGarmentUrl, this.coarseImageUrl, this.userUrls, this.refImg, this.predefinedFaceId,
                    this.facePrompt, this.bgstyleScale, this.realPerson, this.seed, this.refStrength,
                    this.responseFormat, this.refMode, this.negativePrompt, this.text, this.promptExtend,
                    this.watermark, this.function, this.sketchWeight, this.sketchExtraction, this.sketchColor,
                    this.maskColor, this.bboxList, this.maxImages, this.enableInterleave, this.enableSequential,
                    this.colorPalette, this.thinkingMode, this.outputRatio, this.xScale, this.yScale, this.angle,
                    this.leftOffset, this.rightOffset, this.topOffset, this.bottomOffset, this.bestQuality,
                    this.limitImageSize, this.sourceLang, this.targetLang, this.ext, this.elementList,
                    this.resultType, this.seriesAmount, this.aspectRatio, this.resolution, this.shortSideSize,
                    this.scale, this.modelVersion, this.noiseLevel, this.refPromptWeight, this.referenceEdge,
                    this.generateMode, this.auxiliaryParameters, this.title, this.subTitle, this.bodyText,
                    this.promptTextZh, this.promptTextEn, this.whRatios, this.loraName, this.loraWeight,
                    this.ctrlRatio, this.ctrlStep, this.creativeTitleLayout, this.fastMode, this.dilateFlag,
                    this.restoreFace, this.gender, this.clothesType, this.resources, this.skinRetouch, this.steps,
                    this.fontName, this.ttfUrl, this.imageShortSize, this.alphaChannel, this.trainingFileIds,
                    this.invokeMode, this.requestType);
        }

    }

}
