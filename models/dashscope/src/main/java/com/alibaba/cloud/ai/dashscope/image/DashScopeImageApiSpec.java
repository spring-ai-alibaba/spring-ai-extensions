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

import java.util.List;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Spec entity for DashScope Image API
 *
 * @author xuguan
 */
public class DashScopeImageApiSpec {

    // @formatter:off
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record ImageRequest(@JsonProperty("model") String model,
                               @JsonProperty("input") BaseInput input,
                               @JsonProperty("parameters") Parameters parameters,
                               @JsonProperty("training_file_ids") List<String> trainingFileIds) {

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private String model;
            private BaseInput input;
            private Parameters parameters;
            private List<String> trainingFileIds;

            public Builder model(String model) {
                this.model = model;
                return this;
            }

            public Builder input(BaseInput input) {
                this.input = input;
                return this;
            }

            public Builder parameters(Parameters parameters) {
                this.parameters = parameters;
                return this;
            }

            public Builder trainingFileIds(List<String> trainingFileIds) {
                this.trainingFileIds = trainingFileIds;
                return this;
            }

            public ImageRequest build() {
                return new ImageRequest(model, input, parameters, trainingFileIds);
            }
        }

        public interface BaseInput {}

        @JsonInclude(JsonInclude.Include.NON_NULL)
        public record Input(@JsonProperty("prompt") String prompt,
                            @JsonProperty("text") Object text,
                            @JsonProperty("negative_prompt") String negativePrompt,
                            @JsonProperty("function") String function,
                            @JsonProperty("base_image_url") String baseImageUrl,
                            @JsonProperty("image_url") String imageUrl,
                            @JsonProperty("image") Image image,
                            @JsonProperty("images") List<String> images,
                            @JsonProperty("mask_image_url") String maskImageUrl,
                            @JsonProperty("sketch_image_url") String sketchImageUrl,
                            @JsonProperty("style_ref_url") String styleRefUrl,
                            @JsonProperty("face_image_url") String faceImageUrl,
                            @JsonProperty("background_image_url") String backgroundImageUrl,
                            @JsonProperty("template_image_url") String templateImageUrl,
                            @JsonProperty("shoe_image_url") List<String> shoeImageUrl,
                            @JsonProperty("ref_image_url") String refImageUrl,
                            @JsonProperty("foreground_url") String foregroundUrl,
                            @JsonProperty("person_image_url") String personImageUrl,
                            @JsonProperty("top_garment_url") String topGarmentUrl,
                            @JsonProperty("bottom_garment_url") String bottomGarmentUrl,
                            @JsonProperty("coarse_image_url") String coarseImageUrl,
                            @JsonProperty("template_url") String templateUrl,
                            @JsonProperty("user_urls") List<String> userUrls,
                            @JsonProperty("ref_img") String refImg,
                            @JsonProperty("source_lang") String sourceLang,
                            @JsonProperty("target_lang") String targetLang,
                            @JsonProperty("ext") Object ext,
                            @JsonProperty("style_index") Integer styleIndex,
                            @JsonProperty("predefined_face_id") String predefinedFaceId,
                            @JsonProperty("face_prompt") String facePrompt,
                            @JsonProperty("bgstyle_scale") Float bgstyleScale,
                            @JsonProperty("realPerson") Boolean realPerson,
                            @JsonProperty("generate_mode") String generateMode,
                            @JsonProperty("generate_num") Integer generateNum,
                            @JsonProperty("auxiliary_parameters") String auxiliaryParameters,
                            @JsonProperty("title") String title,
                            @JsonProperty("sub_title") String subTitle,
                            @JsonProperty("body_text") String bodyText,
                            @JsonProperty("prompt_text_zh") String promptTextZh,
                            @JsonProperty("prompt_text_en") String promptTextEn,
                            @JsonProperty("wh_ratios") String whRatios,
                            @JsonProperty("lora_name") String loraName,
                            @JsonProperty("lora_weight") Float loraWeight,
                            @JsonProperty("ctrl_ratio") Float ctrlRatio,
                            @JsonProperty("ctrl_step") Float ctrlStep,
                            @JsonProperty("creative_title_layout") Boolean creativeTitleLayout,
                            @JsonProperty("ref_prompt") String refPrompt,
                            @JsonProperty("neg_ref_prompt") String negRefPrompt,
                            @JsonProperty("reference_edge") ReferenceEdge referenceEdge,
                            @JsonProperty("texture_style") String textureStyle) implements BaseInput {

            public static Builder builder() {
                return new Builder();
            }

            public static class Builder {

                private String prompt;
                private Object text;
                private String negativePrompt;
                private String function;
                private String baseImageUrl;
                private String imageUrl;
                private Image image;
                private List<String> images;
                private String maskImageUrl;
                private String sketchImageUrl;
                private String styleRefUrl;
                private String faceImageUrl;
                private String backgroundImageUrl;
                private String templateImageUrl;
                private List<String> shoeImageUrl;
                private String refImageUrl;
                private String foregroundUrl;
                private String personImageUrl;
                private String topGarmentUrl;
                private String bottomGarmentUrl;
                private String coarseImageUrl;
                private String templateUrl;
                private List<String> userUrls;
                private String refImg;
                private String sourceLang;
                private String targetLang;
                private Object ext;
                private Integer styleIndex;
                private String predefinedFaceId;
                private String facePrompt;
                private Float bgstyleScale;
                private Boolean realPerson;
                private String generateMode;
                private Integer generateNum;
                private String auxiliaryParameters;
                private String title;
                private String subTitle;
                private String bodyText;
                private String promptTextZh;
                private String promptTextEn;
                private String whRatios;
                private String loraName;
                private Float loraWeight;
                private Float ctrlRatio;
                private Float ctrlStep;
                private Boolean creativeTitleLayout;
                private String refPrompt;
                private String negRefPrompt;
                private ReferenceEdge referenceEdge;
                private String textureStyle;

                public Builder prompt(String prompt) {
                    this.prompt = prompt;
                    return this;
                }

                public Builder text(String text) {
                    this.text = text;
                    return this;
                }

                public Builder text(Text text) {
                    this.text = text;
                    return this;
                }

                public Builder negativePrompt(String negativePrompt) {
                    this.negativePrompt = negativePrompt;
                    return this;
                }

                public Builder function(String function) {
                    this.function = function;
                    return this;
                }

                public Builder baseImageUrl(String baseImageUrl) {
                    this.baseImageUrl = baseImageUrl;
                    return this;
                }

                public Builder imageUrl(String imageUrl) {
                    this.imageUrl = imageUrl;
                    return this;
                }

                public Builder image(Image image) {
                    this.image = image;
                    return this;
                }

                public Builder images(List<String> images) {
                    this.images = images;
                    return this;
                }

                public Builder maskImageUrl(String maskImageUrl) {
                    this.maskImageUrl = maskImageUrl;
                    return this;
                }

                public Builder sketchImageUrl(String sketchImageUrl) {
                    this.sketchImageUrl = sketchImageUrl;
                    return this;
                }

                public Builder styleRefUrl(String styleRefUrl) {
                    this.styleRefUrl = styleRefUrl;
                    return this;
                }

                public Builder faceImageUrl(String faceImageUrl) {
                    this.faceImageUrl = faceImageUrl;
                    return this;
                }

                public Builder backgroundImageUrl(String backgroundImageUrl) {
                    this.backgroundImageUrl = backgroundImageUrl;
                    return this;
                }

                public Builder templateImageUrl(String templateImageUrl) {
                    this.templateImageUrl = templateImageUrl;
                    return this;
                }

                public Builder shoeImageUrl(List<String> shoeImageUrl) {
                    this.shoeImageUrl = shoeImageUrl;
                    return this;
                }

                public Builder refImageUrl(String refImageUrl) {
                    this.refImageUrl = refImageUrl;
                    return this;
                }

                public Builder foregroundUrl(String foregroundUrl) {
                    this.foregroundUrl = foregroundUrl;
                    return this;
                }

                public Builder personImageUrl(String personImageUrl) {
                    this.personImageUrl = personImageUrl;
                    return this;
                }

                public Builder topGarmentUrl(String topGarmentUrl) {
                    this.topGarmentUrl = topGarmentUrl;
                    return this;
                }

                public Builder bottomGarmentUrl(String bottomGarmentUrl) {
                    this.bottomGarmentUrl = bottomGarmentUrl;
                    return this;
                }

                public Builder coarseImageUrl(String coarseImageUrl) {
                    this.coarseImageUrl = coarseImageUrl;
                    return this;
                }

                public Builder templateUrl(String templateUrl) {
                    this.templateUrl = templateUrl;
                    return this;
                }

                public Builder userUrls(List<String> userUrls) {
                    this.userUrls = userUrls;
                    return this;
                }

                public Builder refImg(String refImg) {
                    this.refImg = refImg;
                    return this;
                }

                public Builder sourceLang(String sourceLang) {
                    this.sourceLang = sourceLang;
                    return this;
                }

                public Builder targetLang(String targetLang) {
                    this.targetLang = targetLang;
                    return this;
                }

                public Builder ext(Object ext) {
                    this.ext = ext;
                    return this;
                }

                public Builder styleIndex(Integer styleIndex) {
                    this.styleIndex = styleIndex;
                    return this;
                }

                public Builder predefinedFaceId(String predefinedFaceId) {
                    this.predefinedFaceId = predefinedFaceId;
                    return this;
                }

                public Builder facePrompt(String facePrompt) {
                    this.facePrompt = facePrompt;
                    return this;
                }

                public Builder bgstyleScale(Float bgstyleScale) {
                    this.bgstyleScale = bgstyleScale;
                    return this;
                }

                public Builder realPerson(Boolean realPerson) {
                    this.realPerson = realPerson;
                    return this;
                }

                public Builder generateMode(String generateMode) {
                    this.generateMode = generateMode;
                    return this;
                }

                public Builder generateNum(Integer generateNum) {
                    this.generateNum = generateNum;
                    return this;
                }

                public Builder auxiliaryParameters(String auxiliaryParameters) {
                    this.auxiliaryParameters = auxiliaryParameters;
                    return this;
                }

                public Builder title(String title) {
                    this.title = title;
                    return this;
                }

                public Builder subTitle(String subTitle) {
                    this.subTitle = subTitle;
                    return this;
                }

                public Builder bodyText(String bodyText) {
                    this.bodyText = bodyText;
                    return this;
                }

                public Builder promptTextZh(String promptTextZh) {
                    this.promptTextZh = promptTextZh;
                    return this;
                }

                public Builder promptTextEn(String promptTextEn) {
                    this.promptTextEn = promptTextEn;
                    return this;
                }

                public Builder whRatios(String whRatios) {
                    this.whRatios = whRatios;
                    return this;
                }

                public Builder loraName(String loraName) {
                    this.loraName = loraName;
                    return this;
                }

                public Builder loraWeight(Float loraWeight) {
                    this.loraWeight = loraWeight;
                    return this;
                }

                public Builder ctrlRatio(Float ctrlRatio) {
                    this.ctrlRatio = ctrlRatio;
                    return this;
                }

                public Builder ctrlStep(Float ctrlStep) {
                    this.ctrlStep = ctrlStep;
                    return this;
                }

                public Builder creativeTitleLayout(Boolean creativeTitleLayout) {
                    this.creativeTitleLayout = creativeTitleLayout;
                    return this;
                }

                public Builder refPrompt(String refPrompt) {
                    this.refPrompt = refPrompt;
                    return this;
                }

                public Builder negRefPrompt(String negRefPrompt) {
                    this.negRefPrompt = negRefPrompt;
                    return this;
                }

                public Builder referenceEdge(ReferenceEdge referenceEdge) {
                    this.referenceEdge = referenceEdge;
                    return this;
                }

                public Builder textureStyle(String textureStyle) {
                    this.textureStyle = textureStyle;
                    return this;
                }

                public Input build() {
                    return new Input(prompt, text, negativePrompt, function, baseImageUrl, imageUrl, image, images,
                            maskImageUrl, sketchImageUrl, styleRefUrl, faceImageUrl, backgroundImageUrl, templateImageUrl,
                            shoeImageUrl, refImageUrl, foregroundUrl, personImageUrl, topGarmentUrl, bottomGarmentUrl,
                            coarseImageUrl, templateUrl, userUrls, refImg, sourceLang, targetLang, ext, styleIndex,
                            predefinedFaceId, facePrompt, bgstyleScale, realPerson, generateMode, generateNum,
                            auxiliaryParameters, title, subTitle, bodyText, promptTextZh, promptTextEn, whRatios, loraName,
                            loraWeight, ctrlRatio, ctrlStep, creativeTitleLayout, refPrompt, negRefPrompt, referenceEdge,
                            textureStyle);
                }
            }
        }

        @JsonInclude(JsonInclude.Include.NON_NULL)
        public record GenerationInput(@JsonProperty("messages") List<Message> messages,
                                      @JsonProperty("element_list") List<Element> elementList) implements BaseInput {

        }

        @JsonInclude(JsonInclude.Include.NON_NULL)
        public record Parameters(@JsonProperty("style") String style,
                                 @JsonProperty("size") String size,
                                 @JsonProperty("n") Integer n,
                                 @JsonProperty("seed") Integer seed,
                                 @JsonProperty("ref_strength") Float refStrength,
                                 @JsonProperty("ref_mode") String refMode,
                                 @JsonProperty("prompt_extend") Boolean promptExtend,
                                 @JsonProperty("watermark") Boolean watermark,
                                 @JsonProperty("add_watermark") Boolean addWatermark,
                                 @JsonProperty("sketch_weight") Integer sketchWeight,
                                 @JsonProperty("sketch_extraction") Boolean sketchExtraction,
                                 @JsonProperty("sketch_color") Integer[][] sketchColor,
                                 @JsonProperty("mask_color") Integer[][] maskColor,
                                 @JsonProperty("negative_prompt") String negativePrompt,
                                 @JsonProperty("max_images") Integer maxImages,
                                 @JsonProperty("enable_interleave") Boolean enableInterleave,
                                 @JsonProperty("output_ratio") String outputRatio,
                                 @JsonProperty("x_scale") Float xScale,
                                 @JsonProperty("y_scale") Float yScale,
                                 @JsonProperty("angle") Integer angle,
                                 @JsonProperty("left_offset") Integer leftOffset,
                                 @JsonProperty("right_offset") Integer rightOffset,
                                 @JsonProperty("top_offset") Integer topOffset,
                                 @JsonProperty("bottom_offset") Integer bottomOffset,
                                 @JsonProperty("best_quality") Boolean bestQuality,
                                 @JsonProperty("limit_image_size") Boolean limitImageSize,
                                 @JsonProperty("enable_sequential") Boolean enableSequential,
                                 @JsonProperty("color_palette") List<ColorPaletteItem> colorPalette,
                                 @JsonProperty("thinking_mode") Boolean thinkingMode,
                                 @JsonProperty("bbox_list") Integer[][][] bboxList,
                                 @JsonProperty("result_type") String resultType,
                                 @JsonProperty("series_amount") Integer seriesAmount,
                                 @JsonProperty("aspect_ratio") String aspectRatio,
                                 @JsonProperty("resolution") String resolution,
                                 @JsonProperty("short_side_size") String shortSideSize,
                                 @JsonProperty("scale") Float scale,
                                 @JsonProperty("model_version") String modelVersion,
                                 @JsonProperty("noise_level") Integer noiseLevel,
                                 @JsonProperty("ref_prompt_weight") Float refPromptWeight,
                                 @JsonProperty("fast_mode") Boolean fastMode,
                                 @JsonProperty("dilate_flag") Boolean dilateFlag,
                                 @JsonProperty("restore_face") Boolean restoreFace,
                                 @JsonProperty("gender") String gender,
                                 @JsonProperty("clothes_type") List<String> clothesType,
                                 @JsonProperty("resources") List<Resource> resources,
                                 @JsonProperty("skin_retouch") Boolean skinRetouch,
                                 @JsonProperty("steps") Integer steps,
                                 @JsonProperty("font_name") String fontName,
                                 @JsonProperty("ttf_url") String ttfUrl,
                                 @JsonProperty("output_image_ratio") String outputImageRatio,
                                 @JsonProperty("image_short_size") Integer imageShortSize,
                                 @JsonProperty("alpha_channel") Boolean alphaChannel) {

            public static Builder builder() {
                return new Builder();
            }

            public static class Builder {

                private String style;
                private String size;
                private Integer n;
                private Integer seed;
                private Float refStrength;
                private String refMode;
                private Boolean promptExtend;
                private Boolean watermark;
                private Boolean addWatermark;
                private Integer sketchWeight;
                private Boolean sketchExtraction;
                private Integer[][] sketchColor;
                private Integer[][] maskColor;
                private String negativePrompt;
                private Integer maxImages;
                private Boolean enableInterleave;
                private String outputRatio;
                private Float xScale;
                private Float yScale;
                private Integer angle;
                private Integer leftOffset;
                private Integer rightOffset;
                private Integer topOffset;
                private Integer bottomOffset;
                private Boolean bestQuality;
                private Boolean limitImageSize;
                private Boolean enableSequential;
                private List<ColorPaletteItem> colorPalette;
                private Boolean thinkingMode;
                private Integer[][][] bboxList;
                private String resultType;
                private Integer seriesAmount;
                private String aspectRatio;
                private String resolution;
                private String shortSideSize;
                private Float scale;
                private String modelVersion;
                private Integer noiseLevel;
                private Float refPromptWeight;
                private Boolean fastMode;
                private Boolean dilateFlag;
                private Boolean restoreFace;
                private String gender;
                private List<String> clothesType;
                private List<Resource> resources;
                private Boolean skinRetouch;
                private Integer steps;
                private String fontName;
                private String ttfUrl;
                private String outputImageRatio;
                private Integer imageShortSize;
                private Boolean alphaChannel;

                public Builder style(String style) {
                    this.style = style;
                    return this;
                }

                public Builder size(String size) {
                    this.size = size;
                    return this;
                }

                public Builder n(Integer n) {
                    this.n = n;
                    return this;
                }

                public Builder seed(Integer seed) {
                    this.seed = seed;
                    return this;
                }

                public Builder refStrength(Float refStrength) {
                    this.refStrength = refStrength;
                    return this;
                }

                public Builder refMode(String refMode) {
                    this.refMode = refMode;
                    return this;
                }

                public Builder promptExtend(Boolean promptExtend) {
                    this.promptExtend = promptExtend;
                    return this;
                }

                public Builder watermark(Boolean watermark) {
                    this.watermark = watermark;
                    return this;
                }

                public Builder addWatermark(Boolean addWatermark) {
                    this.addWatermark = addWatermark;
                    return this;
                }

                public Builder sketchWeight(Integer sketchWeight) {
                    this.sketchWeight = sketchWeight;
                    return this;
                }

                public Builder sketchExtraction(Boolean sketchExtraction) {
                    this.sketchExtraction = sketchExtraction;
                    return this;
                }

                public Builder sketchColor(Integer[][] sketchColor) {
                    this.sketchColor = sketchColor;
                    return this;
                }

                public Builder maskColor(Integer[][] maskColor) {
                    this.maskColor = maskColor;
                    return this;
                }

                public Builder negativePrompt(String negativePrompt) {
                    this.negativePrompt = negativePrompt;
                    return this;
                }

                public Builder maxImages(Integer maxImages) {
                    this.maxImages = maxImages;
                    return this;
                }

                public Builder enableInterleave(Boolean enableInterleave) {
                    this.enableInterleave = enableInterleave;
                    return this;
                }

                public Builder outputRatio(String outputRatio) {
                    this.outputRatio = outputRatio;
                    return this;
                }

                public Builder xScale(Float xScale) {
                    this.xScale = xScale;
                    return this;
                }

                public Builder yScale(Float yScale) {
                    this.yScale = yScale;
                    return this;
                }

                public Builder angle(Integer angle) {
                    this.angle = angle;
                    return this;
                }

                public Builder leftOffset(Integer leftOffset) {
                    this.leftOffset = leftOffset;
                    return this;
                }

                public Builder rightOffset(Integer rightOffset) {
                    this.rightOffset = rightOffset;
                    return this;
                }

                public Builder topOffset(Integer topOffset) {
                    this.topOffset = topOffset;
                    return this;
                }

                public Builder bottomOffset(Integer bottomOffset) {
                    this.bottomOffset = bottomOffset;
                    return this;
                }

                public Builder bestQuality(Boolean bestQuality) {
                    this.bestQuality = bestQuality;
                    return this;
                }

                public Builder limitImageSize(Boolean limitImageSize) {
                    this.limitImageSize = limitImageSize;
                    return this;
                }

                public Builder enableSequential(Boolean enableSequential) {
                    this.enableSequential = enableSequential;
                    return this;
                }

                public Builder colorPalette(List<ColorPaletteItem> colorPalette) {
                    this.colorPalette = colorPalette;
                    return this;
                }

                public Builder thinkingMode(Boolean thinkingMode) {
                    this.thinkingMode = thinkingMode;
                    return this;
                }

                public Builder bboxList(Integer[][][] bboxList) {
                    this.bboxList = bboxList;
                    return this;
                }

                public Builder resultType(String resultType) {
                    this.resultType = resultType;
                    return this;
                }

                public Builder seriesAmount(Integer seriesAmount) {
                    this.seriesAmount = seriesAmount;
                    return this;
                }

                public Builder aspectRatio(String aspectRatio) {
                    this.aspectRatio = aspectRatio;
                    return this;
                }

                public Builder resolution(String resolution) {
                    this.resolution = resolution;
                    return this;
                }

                public Builder shortSideSize(String shortSideSize) {
                    this.shortSideSize = shortSideSize;
                    return this;
                }

                public Builder scale(Float scale) {
                    this.scale = scale;
                    return this;
                }

                public Builder modelVersion(String modelVersion) {
                    this.modelVersion = modelVersion;
                    return this;
                }

                public Builder noiseLevel(Integer noiseLevel) {
                    this.noiseLevel = noiseLevel;
                    return this;
                }

                public Builder refPromptWeight(Float refPromptWeight) {
                    this.refPromptWeight = refPromptWeight;
                    return this;
                }

                public Builder fastMode(Boolean fastMode) {
                    this.fastMode = fastMode;
                    return this;
                }

                public Builder dilateFlag(Boolean dilateFlag) {
                    this.dilateFlag = dilateFlag;
                    return this;
                }

                public Builder restoreFace(Boolean restoreFace) {
                    this.restoreFace = restoreFace;
                    return this;
                }

                public Builder gender(String gender) {
                    this.gender = gender;
                    return this;
                }

                public Builder clothesType(List<String> clothesType) {
                    this.clothesType = clothesType;
                    return this;
                }

                public Builder resources(List<Resource> resources) {
                    this.resources = resources;
                    return this;
                }

                public Builder skinRetouch(Boolean skinRetouch) {
                    this.skinRetouch = skinRetouch;
                    return this;
                }

                public Builder steps(Integer steps) {
                    this.steps = steps;
                    return this;
                }

                public Builder fontName(String fontName) {
                    this.fontName = fontName;
                    return this;
                }

                public Builder ttfUrl(String ttfUrl) {
                    this.ttfUrl = ttfUrl;
                    return this;
                }

                public Builder outputImageRatio(String outputImageRatio) {
                    this.outputImageRatio = outputImageRatio;
                    return this;
                }

                public Builder imageShortSize(Integer imageShortSize) {
                    this.imageShortSize = imageShortSize;
                    return this;
                }

                public Builder alphaChannel(Boolean alphaChannel) {
                    this.alphaChannel = alphaChannel;
                    return this;
                }

                public Parameters build() {
                    return new Parameters(style, size, n, seed, refStrength, refMode, promptExtend, watermark,
                            addWatermark, sketchWeight, sketchExtraction, sketchColor, maskColor, negativePrompt, maxImages,
                            enableInterleave, outputRatio, xScale, yScale, angle, leftOffset, rightOffset, topOffset,
                            bottomOffset, bestQuality, limitImageSize, enableSequential, colorPalette, thinkingMode,
                            bboxList, resultType, seriesAmount, aspectRatio, resolution, shortSideSize, scale,
                            modelVersion, noiseLevel, refPromptWeight, fastMode, dilateFlag, restoreFace, gender,
                            clothesType, resources, skinRetouch, steps, fontName, ttfUrl, outputImageRatio,
                            imageShortSize, alphaChannel);
                }
            }
        }

        @JsonInclude(JsonInclude.Include.NON_NULL)
        public record Element(@JsonProperty("element_id") Integer elementId) {}

        @JsonInclude(JsonInclude.Include.NON_NULL)
        public record ReferenceEdge(@JsonProperty("foreground_edge") List<String> foregroundEdge,
                                    @JsonProperty("background_edge") List<String> backgroundEdge,
                                    @JsonProperty("foreground_edge_prompt") List<String> foregroundEdgePrompt,
                                    @JsonProperty("background_edge_prompt") List<String> backgroundEdgePrompt) {

            public static Builder builder() {
                return new Builder();
            }

            public static class Builder {

                private List<String> foregroundEdge;
                private List<String> backgroundEdge;
                private List<String> foregroundEdgePrompt;
                private List<String> backgroundEdgePrompt;

                public Builder foregroundEdge(List<String> foregroundEdge) {
                    this.foregroundEdge = foregroundEdge;
                    return this;
                }

                public Builder backgroundEdge(List<String> backgroundEdge) {
                    this.backgroundEdge = backgroundEdge;
                    return this;
                }

                public Builder foregroundEdgePrompt(List<String> foregroundEdgePrompt) {
                    this.foregroundEdgePrompt = foregroundEdgePrompt;
                    return this;
                }

                public Builder backgroundEdgePrompt(List<String> backgroundEdgePrompt) {
                    this.backgroundEdgePrompt = backgroundEdgePrompt;
                    return this;
                }

                public ReferenceEdge build() {
                    return new ReferenceEdge(foregroundEdge, backgroundEdge, foregroundEdgePrompt, backgroundEdgePrompt);
                }
            }
        }

        @JsonInclude(JsonInclude.Include.NON_NULL)
        public record ColorPaletteItem(@JsonProperty("hex") String hex,
                                       @JsonProperty("ratio") String ratio) {}

        @JsonInclude(JsonInclude.Include.NON_NULL)
        public record Resource(@JsonProperty("resource_type") String resourceType,
                               @JsonProperty("resource_id") String resourceId) {}

        @JsonInclude(JsonInclude.Include.NON_NULL)
        public record Image(@JsonProperty("image_url") String imageUrl) {}

        @JsonInclude(JsonInclude.Include.NON_NULL)
        public record Text(@JsonProperty("text_content") String textContent,
                           @JsonProperty("ttf_url") String ttfUrl,
                           @JsonProperty("font_name") String fontName,
                           @JsonProperty("output_image_ratio") String outputImageRatio) {

            public static Builder builder() {
                return new Builder();
            }

            public static class Builder {

                private String textContent;
                private String ttfUrl;
                private String fontName;
                private String outputImageRatio;

                public Builder textContent(String textContent) {
                    this.textContent = textContent;
                    return this;
                }

                public Builder ttfUrl(String ttfUrl) {
                    this.ttfUrl = ttfUrl;
                    return this;
                }

                public Builder fontName(String fontName) {
                    this.fontName = fontName;
                    return this;
                }

                public Builder outputImageRatio(String outputImageRatio) {
                    this.outputImageRatio = outputImageRatio;
                    return this;
                }

                public Text build() {
                    return new Text(textContent, ttfUrl, fontName, outputImageRatio);
                }
            }
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ImageResponse(@JsonProperty("request_id") String requestId,
                                @JsonProperty("output") Output output,
                                @JsonProperty("usage") Usage usage) {

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonIgnoreProperties(ignoreUnknown = true)
        public record Output(@JsonProperty("task_id") @JsonAlias("job_id") String taskId,
                             @JsonProperty("task_status") @JsonAlias("status") String taskStatus,
                             @JsonProperty("submit_time") String submitTime,
                             @JsonProperty("scheduled_time") String scheduledTime,
                             @JsonProperty("end_time") String endTime,
                             @JsonProperty("code") String code,
                             @JsonProperty("message") String message,
                             @JsonProperty("results") List<Result> results,
                             @JsonProperty("output_image_url") @JsonAlias("image_url") String outputImageUrl,
                             @JsonProperty("render_urls") List<String> renderUrls,
                             @JsonProperty("bg_urls") List<String> bgUrls,
                             @JsonProperty("output_vis_image_url") String outputVisImageUrl,
                             @JsonProperty("choices") List<Choice> choices,
                             @JsonProperty("task_metrics") TaskMetrics taskMetrics,
                             @JsonProperty("parsing_img_url") List<String> parsingImgUrl,
                             @JsonProperty("crop_img_url") List<String> cropImgUrl,
                             @JsonProperty("bbox") List<Integer> bbox,
                             @JsonProperty("is_face") List<Boolean> isFace,
                             @JsonProperty("failed_reason") String failedReason,
                             @JsonProperty("finetuned_output") String finetunedOutput) {

            public static Builder builder() {
                return new Builder();
            }

            public static class Builder {

                private String taskId;
                private String taskStatus;
                private String submitTime;
                private String scheduledTime;
                private String endTime;
                private String code;
                private String message;
                private List<Result> results;
                private String outputImageUrl;
                private List<String> renderUrls;
                private List<String> bgUrls;
                private String outputVisImageUrl;
                private List<Choice> choices;
                private TaskMetrics taskMetrics;
                private List<String> parsingImgUrl;
                private List<String> cropImgUrl;
                private List<Integer> bbox;
                private List<Boolean> isFace;
                private String failedReason;
                private String finetunedOutput;

                public Builder taskId(String taskId) {
                    this.taskId = taskId;
                    return this;
                }

                public Builder taskStatus(String taskStatus) {
                    this.taskStatus = taskStatus;
                    return this;
                }

                public Builder submitTime(String submitTime) {
                    this.submitTime = submitTime;
                    return this;
                }

                public Builder scheduledTime(String scheduledTime) {
                    this.scheduledTime = scheduledTime;
                    return this;
                }

                public Builder endTime(String endTime) {
                    this.endTime = endTime;
                    return this;
                }

                public Builder code(String code) {
                    this.code = code;
                    return this;
                }

                public Builder message(String message) {
                    this.message = message;
                    return this;
                }

                public Builder results(List<Result> results) {
                    this.results = results;
                    return this;
                }

                public Builder outputImageUrl(String outputImageUrl) {
                    this.outputImageUrl = outputImageUrl;
                    return this;
                }

                public Builder renderUrls(List<String> renderUrls) {
                    this.renderUrls = renderUrls;
                    return this;
                }

                public Builder bgUrls(List<String> bgUrls) {
                    this.bgUrls = bgUrls;
                    return this;
                }

                public Builder outputVisImageUrl(String outputVisImageUrl) {
                    this.outputVisImageUrl = outputVisImageUrl;
                    return this;
                }

                public Builder choices(List<Choice> choices) {
                    this.choices = choices;
                    return this;
                }

                public Builder taskMetrics(TaskMetrics taskMetrics) {
                    this.taskMetrics = taskMetrics;
                    return this;
                }

                public Builder parsingImgUrl(List<String> parsingImgUrl) {
                    this.parsingImgUrl = parsingImgUrl;
                    return this;
                }

                public Builder cropImgUrl(List<String> cropImgUrl) {
                    this.cropImgUrl = cropImgUrl;
                    return this;
                }

                public Builder bbox(List<Integer> bbox) {
                    this.bbox = bbox;
                    return this;
                }

                public Builder isFace(List<Boolean> isFace) {
                    this.isFace = isFace;
                    return this;
                }

                public Builder failedReason(String failedReason) {
                    this.failedReason = failedReason;
                    return this;
                }

                public Builder finetunedOutput(String finetunedOutput) {
                    this.finetunedOutput = finetunedOutput;
                    return this;
                }

                public Output build() {
                    return new Output(taskId, taskStatus, submitTime, scheduledTime, endTime, code, message, results,
                            outputImageUrl, renderUrls, bgUrls, outputVisImageUrl, choices, taskMetrics, parsingImgUrl,
                            cropImgUrl, bbox, isFace, failedReason, finetunedOutput);
                }
            }
        }

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonIgnoreProperties(ignoreUnknown = true)
        public record Usage(@JsonProperty("image_count") Integer imageCount) {}

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonIgnoreProperties(ignoreUnknown = true)
        public record Choice(@JsonProperty("finish_reason") String finishReason,
                             @JsonProperty("message") Message message,
                             @JsonProperty("index") Integer index) {}

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonIgnoreProperties(ignoreUnknown = true)
        public record Result(@JsonProperty("url") String url,
                             @JsonProperty("png_url") String pngUrl,
                             @JsonProperty("svg_url") String svgUrl) {}


        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonIgnoreProperties(ignoreUnknown = true)
        public record TaskMetrics(@JsonProperty("TOTAL") Integer total,
                                  @JsonProperty("SUCCEEDED") Integer succeeded,
                                  @JsonProperty("FAILED") Integer failed) {}
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Message(@JsonProperty("role") String role,
                          @JsonProperty("content") List<Content> content) {

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonIgnoreProperties(ignoreUnknown = true)
        public record Content(@JsonProperty("text") String text,
                              @JsonProperty("image") String image) {}

    }

    /**
     * Invocation mode for DashScope Image API.
     */
    public enum InvokeMode {

        /**
         * Auto detect invocation mode.
         */
        AUTO,

        /**
         * Synchronous mode - call without async header, blocks until completion.
         */
        SYNC,

        /**
         * Asynchronous mode - call with async header, returns task_id for polling.
         */
        ASYNC

    }

    /**
     * Request type for DashScope API.
     */
    public enum RequestType {

        /**
         * Auto detect request type.
         */
        AUTO,

        /**
         * Standard request type.
         */
        STANDARD,

        /**
         * Generation request type.
         */
        GENERATION

    }
    // @formatter:on
}
