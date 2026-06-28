/*
 * Copyright 2026-2027 the original author or authors.
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
import org.jspecify.annotations.Nullable;

/**
 * Spec entity for DashScope Image API
 *
 * @author xuguan
 */
public class DashScopeImageApiSpec {

    // @formatter:off
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record ImageRequest(@JsonProperty("model") @Nullable String model,
                               @JsonProperty("input") @Nullable BaseInput input,
                               @JsonProperty("parameters") @Nullable Parameters parameters,
                               @JsonProperty("training_file_ids") @Nullable List<String> trainingFileIds) {

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private @Nullable String model;
            private @Nullable BaseInput input;
            private @Nullable Parameters parameters;
            private @Nullable List<String> trainingFileIds;

            public Builder model(@Nullable String model) {
                this.model = model;
                return this;
            }

            public Builder input(@Nullable BaseInput input) {
                this.input = input;
                return this;
            }

            public Builder parameters(@Nullable Parameters parameters) {
                this.parameters = parameters;
                return this;
            }

            public Builder trainingFileIds(@Nullable List<String> trainingFileIds) {
                this.trainingFileIds = trainingFileIds;
                return this;
            }

            public ImageRequest build() {
                return new ImageRequest(model, input, parameters, trainingFileIds);
            }
        }

        public interface BaseInput {}

        @JsonInclude(JsonInclude.Include.NON_NULL)
        public record Input(@JsonProperty("prompt") @Nullable String prompt,
                            @JsonProperty("text") @Nullable Object text,
                            @JsonProperty("negative_prompt") @Nullable String negativePrompt,
                            @JsonProperty("function") @Nullable String function,
                            @JsonProperty("base_image_url") @Nullable String baseImageUrl,
                            @JsonProperty("image_url") @Nullable String imageUrl,
                            @JsonProperty("image") @Nullable Image image,
                            @JsonProperty("images") @Nullable List<String> images,
                            @JsonProperty("mask_image_url") @Nullable String maskImageUrl,
                            @JsonProperty("sketch_image_url") @Nullable String sketchImageUrl,
                            @JsonProperty("style_ref_url") @Nullable String styleRefUrl,
                            @JsonProperty("face_image_url") @Nullable String faceImageUrl,
                            @JsonProperty("background_image_url") @Nullable String backgroundImageUrl,
                            @JsonProperty("template_image_url") @Nullable String templateImageUrl,
                            @JsonProperty("shoe_image_url") @Nullable List<String> shoeImageUrl,
                            @JsonProperty("ref_image_url") @Nullable String refImageUrl,
                            @JsonProperty("foreground_url") @Nullable String foregroundUrl,
                            @JsonProperty("person_image_url") @Nullable String personImageUrl,
                            @JsonProperty("top_garment_url") @Nullable String topGarmentUrl,
                            @JsonProperty("bottom_garment_url") @Nullable String bottomGarmentUrl,
                            @JsonProperty("coarse_image_url") @Nullable String coarseImageUrl,
                            @JsonProperty("template_url") @Nullable String templateUrl,
                            @JsonProperty("user_urls") @Nullable List<String> userUrls,
                            @JsonProperty("ref_img") @Nullable String refImg,
                            @JsonProperty("source_lang") @Nullable String sourceLang,
                            @JsonProperty("target_lang") @Nullable String targetLang,
                            @JsonProperty("ext") @Nullable Object ext,
                            @JsonProperty("style_index") @Nullable Integer styleIndex,
                            @JsonProperty("predefined_face_id") @Nullable String predefinedFaceId,
                            @JsonProperty("face_prompt") @Nullable String facePrompt,
                            @JsonProperty("bgstyle_scale") @Nullable Float bgstyleScale,
                            @JsonProperty("realPerson") @Nullable Boolean realPerson,
                            @JsonProperty("generate_mode") @Nullable String generateMode,
                            @JsonProperty("generate_num") @Nullable Integer generateNum,
                            @JsonProperty("auxiliary_parameters") @Nullable String auxiliaryParameters,
                            @JsonProperty("title") @Nullable String title,
                            @JsonProperty("sub_title") @Nullable String subTitle,
                            @JsonProperty("body_text") @Nullable String bodyText,
                            @JsonProperty("prompt_text_zh") @Nullable String promptTextZh,
                            @JsonProperty("prompt_text_en") @Nullable String promptTextEn,
                            @JsonProperty("wh_ratios") @Nullable String whRatios,
                            @JsonProperty("lora_name") @Nullable String loraName,
                            @JsonProperty("lora_weight") @Nullable Float loraWeight,
                            @JsonProperty("ctrl_ratio") @Nullable Float ctrlRatio,
                            @JsonProperty("ctrl_step") @Nullable Float ctrlStep,
                            @JsonProperty("creative_title_layout") @Nullable Boolean creativeTitleLayout,
                            @JsonProperty("ref_prompt") @Nullable String refPrompt,
                            @JsonProperty("neg_ref_prompt") @Nullable String negRefPrompt,
                            @JsonProperty("reference_edge") @Nullable ReferenceEdge referenceEdge,
                            @JsonProperty("texture_style") @Nullable String textureStyle) implements BaseInput {

            public static Builder builder() {
                return new Builder();
            }

            public static class Builder {

                private @Nullable String prompt;
                private @Nullable Object text;
                private @Nullable String negativePrompt;
                private @Nullable String function;
                private @Nullable String baseImageUrl;
                private @Nullable String imageUrl;
                private @Nullable Image image;
                private @Nullable List<String> images;
                private @Nullable String maskImageUrl;
                private @Nullable String sketchImageUrl;
                private @Nullable String styleRefUrl;
                private @Nullable String faceImageUrl;
                private @Nullable String backgroundImageUrl;
                private @Nullable String templateImageUrl;
                private @Nullable List<String> shoeImageUrl;
                private @Nullable String refImageUrl;
                private @Nullable String foregroundUrl;
                private @Nullable String personImageUrl;
                private @Nullable String topGarmentUrl;
                private @Nullable String bottomGarmentUrl;
                private @Nullable String coarseImageUrl;
                private @Nullable String templateUrl;
                private @Nullable List<String> userUrls;
                private @Nullable String refImg;
                private @Nullable String sourceLang;
                private @Nullable String targetLang;
                private @Nullable Object ext;
                private @Nullable Integer styleIndex;
                private @Nullable String predefinedFaceId;
                private @Nullable String facePrompt;
                private @Nullable Float bgstyleScale;
                private @Nullable Boolean realPerson;
                private @Nullable String generateMode;
                private @Nullable Integer generateNum;
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
                private @Nullable String refPrompt;
                private @Nullable String negRefPrompt;
                private @Nullable ReferenceEdge referenceEdge;
                private @Nullable String textureStyle;

                public Builder prompt(@Nullable String prompt) {
                    this.prompt = prompt;
                    return this;
                }

                public Builder text(@Nullable String text) {
                    this.text = text;
                    return this;
                }

                public Builder text(@Nullable Text text) {
                    this.text = text;
                    return this;
                }

                public Builder negativePrompt(@Nullable String negativePrompt) {
                    this.negativePrompt = negativePrompt;
                    return this;
                }

                public Builder function(@Nullable String function) {
                    this.function = function;
                    return this;
                }

                public Builder baseImageUrl(@Nullable String baseImageUrl) {
                    this.baseImageUrl = baseImageUrl;
                    return this;
                }

                public Builder imageUrl(@Nullable String imageUrl) {
                    this.imageUrl = imageUrl;
                    return this;
                }

                public Builder image(@Nullable Image image) {
                    this.image = image;
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

                public Builder styleRefUrl(@Nullable String styleRefUrl) {
                    this.styleRefUrl = styleRefUrl;
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

                public Builder templateImageUrl(@Nullable String templateImageUrl) {
                    this.templateImageUrl = templateImageUrl;
                    return this;
                }

                public Builder shoeImageUrl(@Nullable List<String> shoeImageUrl) {
                    this.shoeImageUrl = shoeImageUrl;
                    return this;
                }

                public Builder refImageUrl(@Nullable String refImageUrl) {
                    this.refImageUrl = refImageUrl;
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

                public Builder templateUrl(@Nullable String templateUrl) {
                    this.templateUrl = templateUrl;
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

                public Builder styleIndex(@Nullable Integer styleIndex) {
                    this.styleIndex = styleIndex;
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

                public Builder generateMode(@Nullable String generateMode) {
                    this.generateMode = generateMode;
                    return this;
                }

                public Builder generateNum(@Nullable Integer generateNum) {
                    this.generateNum = generateNum;
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

                public Builder refPrompt(@Nullable String refPrompt) {
                    this.refPrompt = refPrompt;
                    return this;
                }

                public Builder negRefPrompt(@Nullable String negRefPrompt) {
                    this.negRefPrompt = negRefPrompt;
                    return this;
                }

                public Builder referenceEdge(@Nullable ReferenceEdge referenceEdge) {
                    this.referenceEdge = referenceEdge;
                    return this;
                }

                public Builder textureStyle(@Nullable String textureStyle) {
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
                                      @JsonProperty("element_list") @Nullable List<Element> elementList) implements BaseInput {

        }

        @JsonInclude(JsonInclude.Include.NON_NULL)
        public record Parameters(@JsonProperty("style") @Nullable String style,
                                 @JsonProperty("size") @Nullable String size,
                                 @JsonProperty("n") @Nullable Integer n,
                                 @JsonProperty("seed") @Nullable Integer seed,
                                 @JsonProperty("ref_strength") @Nullable Float refStrength,
                                 @JsonProperty("ref_mode") @Nullable String refMode,
                                 @JsonProperty("prompt_extend") @Nullable Boolean promptExtend,
                                 @JsonProperty("watermark") @Nullable Boolean watermark,
                                 @JsonProperty("add_watermark") @Nullable Boolean addWatermark,
                                 @JsonProperty("sketch_weight") @Nullable Integer sketchWeight,
                                 @JsonProperty("sketch_extraction") @Nullable Boolean sketchExtraction,
                                 @JsonProperty("sketch_color") @Nullable Integer @Nullable [][] sketchColor,
                                 @JsonProperty("mask_color") @Nullable Integer @Nullable [][] maskColor,
                                 @JsonProperty("bbox_list") @Nullable Integer @Nullable [][][] bboxList,
                                 @JsonProperty("negative_prompt") @Nullable String negativePrompt,
                                 @JsonProperty("max_images") @Nullable Integer maxImages,
                                 @JsonProperty("enable_interleave") @Nullable Boolean enableInterleave,
                                 @JsonProperty("output_ratio") @Nullable String outputRatio,
                                 @JsonProperty("x_scale") @Nullable Float xScale,
                                 @JsonProperty("y_scale") @Nullable Float yScale,
                                 @JsonProperty("angle") @Nullable Integer angle,
                                 @JsonProperty("left_offset") @Nullable Integer leftOffset,
                                 @JsonProperty("right_offset") @Nullable Integer rightOffset,
                                 @JsonProperty("top_offset") @Nullable Integer topOffset,
                                 @JsonProperty("bottom_offset") @Nullable Integer bottomOffset,
                                 @JsonProperty("best_quality") @Nullable Boolean bestQuality,
                                 @JsonProperty("limit_image_size") @Nullable Boolean limitImageSize,
                                 @JsonProperty("enable_sequential") @Nullable Boolean enableSequential,
                                 @JsonProperty("color_palette") @Nullable List<ColorPaletteItem> colorPalette,
                                 @JsonProperty("thinking_mode") @Nullable Boolean thinkingMode,
                                 @JsonProperty("result_type") @Nullable String resultType,
                                 @JsonProperty("series_amount") @Nullable Integer seriesAmount,
                                 @JsonProperty("aspect_ratio") @Nullable String aspectRatio,
                                 @JsonProperty("resolution") @Nullable String resolution,
                                 @JsonProperty("short_side_size") @Nullable String shortSideSize,
                                 @JsonProperty("scale") @Nullable Float scale,
                                 @JsonProperty("model_version") @Nullable String modelVersion,
                                 @JsonProperty("noise_level") @Nullable Integer noiseLevel,
                                 @JsonProperty("ref_prompt_weight") @Nullable Float refPromptWeight,
                                 @JsonProperty("fast_mode") @Nullable Boolean fastMode,
                                 @JsonProperty("dilate_flag") @Nullable Boolean dilateFlag,
                                 @JsonProperty("restore_face") @Nullable Boolean restoreFace,
                                 @JsonProperty("gender") @Nullable String gender,
                                 @JsonProperty("clothes_type") @Nullable List<String> clothesType,
                                 @JsonProperty("resources") @Nullable List<Resource> resources,
                                 @JsonProperty("skin_retouch") @Nullable Boolean skinRetouch,
                                 @JsonProperty("steps") @Nullable Integer steps,
                                 @JsonProperty("font_name") @Nullable String fontName,
                                 @JsonProperty("ttf_url") @Nullable String ttfUrl,
                                 @JsonProperty("output_image_ratio") @Nullable String outputImageRatio,
                                 @JsonProperty("image_short_size") @Nullable Integer imageShortSize,
                                 @JsonProperty("alpha_channel") @Nullable Boolean alphaChannel) {

            public static Builder builder() {
                return new Builder();
            }

            public static class Builder {

                private @Nullable String style;
                private @Nullable String size;
                private @Nullable Integer n;
                private @Nullable Integer seed;
                private @Nullable Float refStrength;
                private @Nullable String refMode;
                private @Nullable Boolean promptExtend;
                private @Nullable Boolean watermark;
                private @Nullable Boolean addWatermark;
                private @Nullable Integer sketchWeight;
                private @Nullable Boolean sketchExtraction;
                private @Nullable Integer @Nullable [][] sketchColor;
                private @Nullable Integer @Nullable [][] maskColor;
                private @Nullable Integer @Nullable [][][] bboxList;
                private @Nullable String negativePrompt;
                private @Nullable Integer maxImages;
                private @Nullable Boolean enableInterleave;
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
                private @Nullable Boolean enableSequential;
                private @Nullable List<ColorPaletteItem> colorPalette;
                private @Nullable Boolean thinkingMode;
                private @Nullable String resultType;
                private @Nullable Integer seriesAmount;
                private @Nullable String aspectRatio;
                private @Nullable String resolution;
                private @Nullable String shortSideSize;
                private @Nullable Float scale;
                private @Nullable String modelVersion;
                private @Nullable Integer noiseLevel;
                private @Nullable Float refPromptWeight;
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
                private @Nullable String outputImageRatio;
                private @Nullable Integer imageShortSize;
                private @Nullable Boolean alphaChannel;

                public Builder style(@Nullable String style) {
                    this.style = style;
                    return this;
                }

                public Builder size(@Nullable String size) {
                    this.size = size;
                    return this;
                }

                public Builder n(@Nullable Integer n) {
                    this.n = n;
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

                public Builder refMode(@Nullable String refMode) {
                    this.refMode = refMode;
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

                public Builder addWatermark(@Nullable Boolean addWatermark) {
                    this.addWatermark = addWatermark;
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

                public Builder negativePrompt(@Nullable String negativePrompt) {
                    this.negativePrompt = negativePrompt;
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

                public Builder outputImageRatio(@Nullable String outputImageRatio) {
                    this.outputImageRatio = outputImageRatio;
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

                public Parameters build() {
                    return new Parameters(style, size, n, seed, refStrength, refMode, promptExtend, watermark,
                            addWatermark, sketchWeight, sketchExtraction, sketchColor, maskColor, bboxList,
                            negativePrompt, maxImages, enableInterleave, outputRatio, xScale, yScale, angle,
                            leftOffset, rightOffset, topOffset, bottomOffset, bestQuality, limitImageSize,
                            enableSequential, colorPalette, thinkingMode, resultType, seriesAmount, aspectRatio,
                            resolution, shortSideSize, scale, modelVersion, noiseLevel, refPromptWeight, fastMode,
                            dilateFlag, restoreFace, gender, clothesType, resources, skinRetouch, steps, fontName,
                            ttfUrl, outputImageRatio, imageShortSize, alphaChannel);
                }
            }
        }

        @JsonInclude(JsonInclude.Include.NON_NULL)
        public record Element(@JsonProperty("element_id") @Nullable Integer elementId) {}

        @JsonInclude(JsonInclude.Include.NON_NULL)
        public record ReferenceEdge(@JsonProperty("foreground_edge") @Nullable List<String> foregroundEdge,
                                    @JsonProperty("background_edge") @Nullable List<String> backgroundEdge,
                                    @JsonProperty("foreground_edge_prompt") @Nullable List<String> foregroundEdgePrompt,
                                    @JsonProperty("background_edge_prompt") @Nullable List<String> backgroundEdgePrompt) {

            public static Builder builder() {
                return new Builder();
            }

            public static class Builder {

                private @Nullable List<String> foregroundEdge;
                private @Nullable List<String> backgroundEdge;
                private @Nullable List<String> foregroundEdgePrompt;
                private @Nullable List<String> backgroundEdgePrompt;

                public Builder foregroundEdge(@Nullable List<String> foregroundEdge) {
                    this.foregroundEdge = foregroundEdge;
                    return this;
                }

                public Builder backgroundEdge(@Nullable List<String> backgroundEdge) {
                    this.backgroundEdge = backgroundEdge;
                    return this;
                }

                public Builder foregroundEdgePrompt(@Nullable List<String> foregroundEdgePrompt) {
                    this.foregroundEdgePrompt = foregroundEdgePrompt;
                    return this;
                }

                public Builder backgroundEdgePrompt(@Nullable List<String> backgroundEdgePrompt) {
                    this.backgroundEdgePrompt = backgroundEdgePrompt;
                    return this;
                }

                public ReferenceEdge build() {
                    return new ReferenceEdge(foregroundEdge, backgroundEdge, foregroundEdgePrompt, backgroundEdgePrompt);
                }
            }
        }

        @JsonInclude(JsonInclude.Include.NON_NULL)
        public record ColorPaletteItem(@JsonProperty("hex") @Nullable String hex,
                                       @JsonProperty("ratio") @Nullable String ratio) {}

        @JsonInclude(JsonInclude.Include.NON_NULL)
        public record Resource(@JsonProperty("resource_type") @Nullable String resourceType,
                               @JsonProperty("resource_id") @Nullable String resourceId) {}

        @JsonInclude(JsonInclude.Include.NON_NULL)
        public record Image(@JsonProperty("image_url") @Nullable String imageUrl) {}

        @JsonInclude(JsonInclude.Include.NON_NULL)
        public record Text(@JsonProperty("text_content") @Nullable String textContent,
                           @JsonProperty("ttf_url") @Nullable String ttfUrl,
                           @JsonProperty("font_name") @Nullable String fontName,
                           @JsonProperty("output_image_ratio") @Nullable String outputImageRatio) {

            public static Builder builder() {
                return new Builder();
            }

            public static class Builder {

                private @Nullable String textContent;
                private @Nullable String ttfUrl;
                private @Nullable String fontName;
                private @Nullable String outputImageRatio;

                public Builder textContent(@Nullable String textContent) {
                    this.textContent = textContent;
                    return this;
                }

                public Builder ttfUrl(@Nullable String ttfUrl) {
                    this.ttfUrl = ttfUrl;
                    return this;
                }

                public Builder fontName(@Nullable String fontName) {
                    this.fontName = fontName;
                    return this;
                }

                public Builder outputImageRatio(@Nullable String outputImageRatio) {
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
    public record ImageResponse(@JsonProperty("request_id") @Nullable String requestId,
                                @JsonProperty("output") @Nullable Output output,
                                @JsonProperty("usage") @Nullable Usage usage) {

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonIgnoreProperties(ignoreUnknown = true)
        public record Output(@JsonProperty("task_id") @JsonAlias("job_id") @Nullable String taskId,
                             @JsonProperty("task_status") @JsonAlias("status") @Nullable String taskStatus,
                             @JsonProperty("submit_time") @Nullable String submitTime,
                             @JsonProperty("scheduled_time") @Nullable String scheduledTime,
                             @JsonProperty("end_time") @Nullable String endTime,
                             @JsonProperty("code") @Nullable String code,
                             @JsonProperty("message") @Nullable String message,
                             @JsonProperty("results") @Nullable List<Result> results,
                             @JsonProperty("output_image_url") @JsonAlias("image_url") @Nullable String outputImageUrl,
                             @JsonProperty("render_urls") @Nullable List<String> renderUrls,
                             @JsonProperty("bg_urls") @Nullable List<String> bgUrls,
                             @JsonProperty("output_vis_image_url") @Nullable String outputVisImageUrl,
                             @JsonProperty("choices") @Nullable List<Choice> choices,
                             @JsonProperty("task_metrics") @Nullable TaskMetrics taskMetrics,
                             @JsonProperty("parsing_img_url") @Nullable List<String> parsingImgUrl,
                             @JsonProperty("crop_img_url") @Nullable List<String> cropImgUrl,
                             @JsonProperty("bbox") @Nullable List<Integer> bbox,
                             @JsonProperty("is_face") @Nullable List<Boolean> isFace,
                             @JsonProperty("failed_reason") @Nullable String failedReason,
                             @JsonProperty("finetuned_output") @Nullable String finetunedOutput) {

            public static Builder builder() {
                return new Builder();
            }

            public static class Builder {

                private @Nullable String taskId;
                private @Nullable String taskStatus;
                private @Nullable String submitTime;
                private @Nullable String scheduledTime;
                private @Nullable String endTime;
                private @Nullable String code;
                private @Nullable String message;
                private @Nullable List<Result> results;
                private @Nullable String outputImageUrl;
                private @Nullable List<String> renderUrls;
                private @Nullable List<String> bgUrls;
                private @Nullable String outputVisImageUrl;
                private @Nullable List<Choice> choices;
                private @Nullable TaskMetrics taskMetrics;
                private @Nullable List<String> parsingImgUrl;
                private @Nullable List<String> cropImgUrl;
                private @Nullable List<Integer> bbox;
                private @Nullable List<Boolean> isFace;
                private @Nullable String failedReason;
                private @Nullable String finetunedOutput;

                public Builder taskId(@Nullable String taskId) {
                    this.taskId = taskId;
                    return this;
                }

                public Builder taskStatus(@Nullable String taskStatus) {
                    this.taskStatus = taskStatus;
                    return this;
                }

                public Builder submitTime(@Nullable String submitTime) {
                    this.submitTime = submitTime;
                    return this;
                }

                public Builder scheduledTime(@Nullable String scheduledTime) {
                    this.scheduledTime = scheduledTime;
                    return this;
                }

                public Builder endTime(@Nullable String endTime) {
                    this.endTime = endTime;
                    return this;
                }

                public Builder code(@Nullable String code) {
                    this.code = code;
                    return this;
                }

                public Builder message(@Nullable String message) {
                    this.message = message;
                    return this;
                }

                public Builder results(@Nullable List<Result> results) {
                    this.results = results;
                    return this;
                }

                public Builder outputImageUrl(@Nullable String outputImageUrl) {
                    this.outputImageUrl = outputImageUrl;
                    return this;
                }

                public Builder renderUrls(@Nullable List<String> renderUrls) {
                    this.renderUrls = renderUrls;
                    return this;
                }

                public Builder bgUrls(@Nullable List<String> bgUrls) {
                    this.bgUrls = bgUrls;
                    return this;
                }

                public Builder outputVisImageUrl(@Nullable String outputVisImageUrl) {
                    this.outputVisImageUrl = outputVisImageUrl;
                    return this;
                }

                public Builder choices(@Nullable List<Choice> choices) {
                    this.choices = choices;
                    return this;
                }

                public Builder taskMetrics(@Nullable TaskMetrics taskMetrics) {
                    this.taskMetrics = taskMetrics;
                    return this;
                }

                public Builder parsingImgUrl(@Nullable List<String> parsingImgUrl) {
                    this.parsingImgUrl = parsingImgUrl;
                    return this;
                }

                public Builder cropImgUrl(@Nullable List<String> cropImgUrl) {
                    this.cropImgUrl = cropImgUrl;
                    return this;
                }

                public Builder bbox(@Nullable List<Integer> bbox) {
                    this.bbox = bbox;
                    return this;
                }

                public Builder isFace(@Nullable List<Boolean> isFace) {
                    this.isFace = isFace;
                    return this;
                }

                public Builder failedReason(@Nullable String failedReason) {
                    this.failedReason = failedReason;
                    return this;
                }

                public Builder finetunedOutput(@Nullable String finetunedOutput) {
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
        public record Usage(@JsonProperty("image_count") @Nullable Integer imageCount) {}

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonIgnoreProperties(ignoreUnknown = true)
        public record Choice(@JsonProperty("finish_reason") @Nullable String finishReason,
                             @JsonProperty("message") @Nullable Message message,
                             @JsonProperty("index") @Nullable Integer index) {}

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonIgnoreProperties(ignoreUnknown = true)
        public record Result(@JsonProperty("url") @Nullable String url,
                             @JsonProperty("png_url") @Nullable String pngUrl,
                             @JsonProperty("svg_url") @Nullable String svgUrl) {}


        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonIgnoreProperties(ignoreUnknown = true)
        public record TaskMetrics(@JsonProperty("TOTAL") @Nullable Integer total,
                                  @JsonProperty("SUCCEEDED") @Nullable Integer succeeded,
                                  @JsonProperty("FAILED") @Nullable Integer failed) {}
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Message(@JsonProperty("role") String role,
                          @JsonProperty("content") @Nullable List<Content> content) {

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonIgnoreProperties(ignoreUnknown = true)
        public record Content(@JsonProperty("text") @Nullable String text,
                              @JsonProperty("image") @Nullable String image) {}

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
