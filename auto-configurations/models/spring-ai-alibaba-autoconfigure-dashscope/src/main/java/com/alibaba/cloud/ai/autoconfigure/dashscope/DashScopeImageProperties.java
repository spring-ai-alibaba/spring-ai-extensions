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

package com.alibaba.cloud.ai.autoconfigure.dashscope;

import java.util.List;

import com.alibaba.cloud.ai.dashscope.common.DashScopeApiConstants;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageApiSpec.ImageRequest.ColorPaletteItem;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageApiSpec.ImageRequest.Element;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageApiSpec.ImageRequest.ReferenceEdge;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageApiSpec.ImageRequest.Resource;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageApiSpec.InvokeMode;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageApiSpec.RequestType;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageOptions;
import com.alibaba.cloud.ai.dashscope.spec.DashScopeModel;
import org.jspecify.annotations.Nullable;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.DeprecatedConfigurationProperty;

/**
 * DashScope image auto-configuration properties.
 *
 * @author yuluo
 * @author <a href="mailto:yuluo08290126@gmail.com">yuluo</a>
 * @author xuguan
 * @since 2023.0.1.0
 */
@ConfigurationProperties(DashScopeImageProperties.CONFIG_PREFIX)
public class DashScopeImageProperties extends DashScopeParentProperties {

    /**
     * Spring AI Alibaba configuration prefix.
     */
    public static final String CONFIG_PREFIX = "spring.ai.dashscope.image";

    /**
     * Enable DashScope ai images client.
     */
    private boolean enabled = true;

    /**
     * DashScope ai images restful url path.
     */
    private @Nullable String imagesPath;

    /**
     * DashScope ai images query task result restful url path.
     */
    private @Nullable String queryTaskPath;

    private long pollIntervalMs = DashScopeApiConstants.DEFAULT_POLL_INTERVAL_MS;

    private long pollTimeoutMs = DashScopeApiConstants.DEFAULT_POLL_TIMEOUT_MS;

    private @Nullable String model = DashScopeModel.ImageModel.WAN_2_2_T2I_FLASH.getValue();

    private @Nullable Integer n = 1;

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

    private @Nullable String promptExtendMode;

    private @Nullable Boolean enableThinking;

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

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public @Nullable String getImagesPath() {
        return this.imagesPath;
    }

    public void setImagesPath(@Nullable String imagesPath) {
        this.imagesPath = imagesPath;
    }

    public @Nullable String getQueryTaskPath() {
        return this.queryTaskPath;
    }

    public void setQueryTaskPath(@Nullable String queryTaskPath) {
        this.queryTaskPath = queryTaskPath;
    }

    public long getPollIntervalMs() {
        return this.pollIntervalMs;
    }

    public void setPollIntervalMs(long pollIntervalMs) {
        this.pollIntervalMs = pollIntervalMs;
    }

    public long getPollTimeoutMs() {
        return this.pollTimeoutMs;
    }

    public void setPollTimeoutMs(long pollTimeoutMs) {
        this.pollTimeoutMs = pollTimeoutMs;
    }

    public @Nullable String getModel() {
        return this.model;
    }

    public void setModel(@Nullable String model) {
        this.model = model;
    }

    public @Nullable Integer getN() {
        return this.n;
    }

    public void setN(@Nullable Integer n) {
        this.n = n;
    }

    public @Nullable Integer getWidth() {
        return this.width;
    }

    public void setWidth(@Nullable Integer width) {
        this.width = width;
    }

    public @Nullable Integer getHeight() {
        return this.height;
    }

    public void setHeight(@Nullable Integer height) {
        this.height = height;
    }

    public @Nullable String getSize() {
        if (this.size != null) {
            return this.size;
        }
        return (this.width != null && this.height != null) ? this.width + "*" + this.height : null;
    }

    public void setSize(@Nullable String size) {
        this.size = size;
    }

    public @Nullable String getStyle() {
        return this.style;
    }

    public void setStyle(@Nullable String style) {
        this.style = style;
    }

    public @Nullable Integer getStyleIndex() {
        return this.styleIndex;
    }

    public void setStyleIndex(@Nullable Integer styleIndex) {
        this.styleIndex = styleIndex;
    }

    public @Nullable String getStyleRefUrl() {
        return this.styleRefUrl;
    }

    public void setStyleRefUrl(@Nullable String styleRefUrl) {
        this.styleRefUrl = styleRefUrl;
    }

    public @Nullable String getBaseImageUrl() {
        return this.baseImageUrl;
    }

    public void setBaseImageUrl(@Nullable String baseImageUrl) {
        this.baseImageUrl = baseImageUrl;
    }

    public @Nullable List<String> getImages() {
        return this.images;
    }

    public void setImages(@Nullable List<String> images) {
        this.images = images;
    }

    public @Nullable String getMaskImageUrl() {
        return this.maskImageUrl;
    }

    public void setMaskImageUrl(@Nullable String maskImageUrl) {
        this.maskImageUrl = maskImageUrl;
    }

    public @Nullable String getSketchImageUrl() {
        return this.sketchImageUrl;
    }

    public void setSketchImageUrl(@Nullable String sketchImageUrl) {
        this.sketchImageUrl = sketchImageUrl;
    }

    public @Nullable String getTemplateImageUrl() {
        return this.templateImageUrl;
    }

    public void setTemplateImageUrl(@Nullable String templateImageUrl) {
        this.templateImageUrl = templateImageUrl;
    }

    public @Nullable List<String> getShoeImageUrl() {
        return this.shoeImageUrl;
    }

    public void setShoeImageUrl(@Nullable List<String> shoeImageUrl) {
        this.shoeImageUrl = shoeImageUrl;
    }

    public @Nullable String getFaceImageUrl() {
        return this.faceImageUrl;
    }

    public void setFaceImageUrl(@Nullable String faceImageUrl) {
        this.faceImageUrl = faceImageUrl;
    }

    public @Nullable String getBackgroundImageUrl() {
        return this.backgroundImageUrl;
    }

    public void setBackgroundImageUrl(@Nullable String backgroundImageUrl) {
        this.backgroundImageUrl = backgroundImageUrl;
    }

    public @Nullable String getForegroundUrl() {
        return this.foregroundUrl;
    }

    public void setForegroundUrl(@Nullable String foregroundUrl) {
        this.foregroundUrl = foregroundUrl;
    }

    public @Nullable String getPersonImageUrl() {
        return this.personImageUrl;
    }

    public void setPersonImageUrl(@Nullable String personImageUrl) {
        this.personImageUrl = personImageUrl;
    }

    public @Nullable String getTopGarmentUrl() {
        return this.topGarmentUrl;
    }

    public void setTopGarmentUrl(@Nullable String topGarmentUrl) {
        this.topGarmentUrl = topGarmentUrl;
    }

    public @Nullable String getBottomGarmentUrl() {
        return this.bottomGarmentUrl;
    }

    public void setBottomGarmentUrl(@Nullable String bottomGarmentUrl) {
        this.bottomGarmentUrl = bottomGarmentUrl;
    }

    public @Nullable String getCoarseImageUrl() {
        return this.coarseImageUrl;
    }

    public void setCoarseImageUrl(@Nullable String coarseImageUrl) {
        this.coarseImageUrl = coarseImageUrl;
    }

    public @Nullable List<String> getUserUrls() {
        return this.userUrls;
    }

    public void setUserUrls(@Nullable List<String> userUrls) {
        this.userUrls = userUrls;
    }

    public @Nullable String getRefImg() {
        return this.refImg;
    }

    public void setRefImg(@Nullable String refImg) {
        this.refImg = refImg;
    }

    public @Nullable String getPredefinedFaceId() {
        return this.predefinedFaceId;
    }

    public void setPredefinedFaceId(@Nullable String predefinedFaceId) {
        this.predefinedFaceId = predefinedFaceId;
    }

    public @Nullable String getFacePrompt() {
        return this.facePrompt;
    }

    public void setFacePrompt(@Nullable String facePrompt) {
        this.facePrompt = facePrompt;
    }

    public @Nullable Float getBgstyleScale() {
        return this.bgstyleScale;
    }

    public void setBgstyleScale(@Nullable Float bgstyleScale) {
        this.bgstyleScale = bgstyleScale;
    }

    public @Nullable Boolean getRealPerson() {
        return this.realPerson;
    }

    public void setRealPerson(@Nullable Boolean realPerson) {
        this.realPerson = realPerson;
    }

    public @Nullable Integer getSeed() {
        return this.seed;
    }

    public void setSeed(@Nullable Integer seed) {
        this.seed = seed;
    }

    public @Nullable Float getRefStrength() {
        return this.refStrength;
    }

    public void setRefStrength(@Nullable Float refStrength) {
        this.refStrength = refStrength;
    }

    public @Nullable String getResponseFormat() {
        return this.responseFormat;
    }

    public void setResponseFormat(@Nullable String responseFormat) {
        this.responseFormat = responseFormat;
    }

    public @Nullable String getRefMode() {
        return this.refMode;
    }

    public void setRefMode(@Nullable String refMode) {
        this.refMode = refMode;
    }

    public @Nullable String getNegativePrompt() {
        return this.negativePrompt;
    }

    public void setNegativePrompt(@Nullable String negativePrompt) {
        this.negativePrompt = negativePrompt;
    }

    public @Nullable String getText() {
        return this.text;
    }

    public void setText(@Nullable String text) {
        this.text = text;
    }

    public @Nullable String getPromptExtendMode() {
        return this.promptExtendMode;
    }

    public void setPromptExtendMode(@Nullable String promptExtendMode) {
        this.promptExtendMode = promptExtendMode;
    }

    public @Nullable Boolean getEnableThinking() {
        return this.enableThinking;
    }

    public void setEnableThinking(@Nullable Boolean enableThinking) {
        this.enableThinking = enableThinking;
    }

    public @Nullable Boolean getPromptExtend() {
        return this.promptExtend;
    }

    public void setPromptExtend(@Nullable Boolean promptExtend) {
        this.promptExtend = promptExtend;
    }

    public @Nullable Boolean getWatermark() {
        return this.watermark;
    }

    public void setWatermark(@Nullable Boolean watermark) {
        this.watermark = watermark;
    }

    public @Nullable String getFunction() {
        return this.function;
    }

    public void setFunction(@Nullable String function) {
        this.function = function;
    }

    public @Nullable Integer getSketchWeight() {
        return this.sketchWeight;
    }

    public void setSketchWeight(@Nullable Integer sketchWeight) {
        this.sketchWeight = sketchWeight;
    }

    public @Nullable Boolean getSketchExtraction() {
        return this.sketchExtraction;
    }

    public void setSketchExtraction(@Nullable Boolean sketchExtraction) {
        this.sketchExtraction = sketchExtraction;
    }

    public @Nullable Integer @Nullable [][] getSketchColor() {
        return this.sketchColor;
    }

    public void setSketchColor(@Nullable Integer @Nullable [][] sketchColor) {
        this.sketchColor = sketchColor;
    }

    public @Nullable Integer @Nullable [][] getMaskColor() {
        return this.maskColor;
    }

    public void setMaskColor(@Nullable Integer @Nullable [][] maskColor) {
        this.maskColor = maskColor;
    }

    public @Nullable Integer @Nullable [][][] getBboxList() {
        return this.bboxList;
    }

    public void setBboxList(@Nullable Integer @Nullable [][][] bboxList) {
        this.bboxList = bboxList;
    }

    public @Nullable Integer getMaxImages() {
        return this.maxImages;
    }

    public void setMaxImages(@Nullable Integer maxImages) {
        this.maxImages = maxImages;
    }

    public @Nullable Boolean getEnableInterleave() {
        return this.enableInterleave;
    }

    public void setEnableInterleave(@Nullable Boolean enableInterleave) {
        this.enableInterleave = enableInterleave;
    }

    public @Nullable Boolean getEnableSequential() {
        return this.enableSequential;
    }

    public void setEnableSequential(@Nullable Boolean enableSequential) {
        this.enableSequential = enableSequential;
    }

    public @Nullable List<ColorPaletteItem> getColorPalette() {
        return this.colorPalette;
    }

    public void setColorPalette(@Nullable List<ColorPaletteItem> colorPalette) {
        this.colorPalette = colorPalette;
    }

    public @Nullable Boolean getThinkingMode() {
        return this.thinkingMode;
    }

    public void setThinkingMode(@Nullable Boolean thinkingMode) {
        this.thinkingMode = thinkingMode;
    }

    public @Nullable String getOutputRatio() {
        return this.outputRatio;
    }

    public void setOutputRatio(@Nullable String outputRatio) {
        this.outputRatio = outputRatio;
    }

    public @Nullable Float getXScale() {
        return this.xScale;
    }

    public void setXScale(@Nullable Float xScale) {
        this.xScale = xScale;
    }

    public @Nullable Float getYScale() {
        return this.yScale;
    }

    public void setYScale(@Nullable Float yScale) {
        this.yScale = yScale;
    }

    public @Nullable Integer getAngle() {
        return this.angle;
    }

    public void setAngle(@Nullable Integer angle) {
        this.angle = angle;
    }

    public @Nullable Integer getLeftOffset() {
        return this.leftOffset;
    }

    public void setLeftOffset(@Nullable Integer leftOffset) {
        this.leftOffset = leftOffset;
    }

    public @Nullable Integer getRightOffset() {
        return this.rightOffset;
    }

    public void setRightOffset(@Nullable Integer rightOffset) {
        this.rightOffset = rightOffset;
    }

    public @Nullable Integer getTopOffset() {
        return this.topOffset;
    }

    public void setTopOffset(@Nullable Integer topOffset) {
        this.topOffset = topOffset;
    }

    public @Nullable Integer getBottomOffset() {
        return this.bottomOffset;
    }

    public void setBottomOffset(@Nullable Integer bottomOffset) {
        this.bottomOffset = bottomOffset;
    }

    public @Nullable Boolean getBestQuality() {
        return this.bestQuality;
    }

    public void setBestQuality(@Nullable Boolean bestQuality) {
        this.bestQuality = bestQuality;
    }

    public @Nullable Boolean getLimitImageSize() {
        return this.limitImageSize;
    }

    public void setLimitImageSize(@Nullable Boolean limitImageSize) {
        this.limitImageSize = limitImageSize;
    }

    public @Nullable String getSourceLang() {
        return this.sourceLang;
    }

    public void setSourceLang(@Nullable String sourceLang) {
        this.sourceLang = sourceLang;
    }

    public @Nullable String getTargetLang() {
        return this.targetLang;
    }

    public void setTargetLang(@Nullable String targetLang) {
        this.targetLang = targetLang;
    }

    public @Nullable Object getExt() {
        return this.ext;
    }

    public void setExt(@Nullable Object ext) {
        this.ext = ext;
    }

    public @Nullable List<Element> getElementList() {
        return this.elementList;
    }

    public void setElementList(@Nullable List<Element> elementList) {
        this.elementList = elementList;
    }

    public @Nullable String getResultType() {
        return this.resultType;
    }

    public void setResultType(@Nullable String resultType) {
        this.resultType = resultType;
    }

    public @Nullable Integer getSeriesAmount() {
        return this.seriesAmount;
    }

    public void setSeriesAmount(@Nullable Integer seriesAmount) {
        this.seriesAmount = seriesAmount;
    }

    public @Nullable String getAspectRatio() {
        return this.aspectRatio;
    }

    public void setAspectRatio(@Nullable String aspectRatio) {
        this.aspectRatio = aspectRatio;
    }

    public @Nullable String getResolution() {
        return this.resolution;
    }

    public void setResolution(@Nullable String resolution) {
        this.resolution = resolution;
    }

    public @Nullable String getShortSideSize() {
        return this.shortSideSize;
    }

    public void setShortSideSize(@Nullable String shortSideSize) {
        this.shortSideSize = shortSideSize;
    }

    public @Nullable Float getScale() {
        return this.scale;
    }

    public void setScale(@Nullable Float scale) {
        this.scale = scale;
    }

    public @Nullable String getModelVersion() {
        return this.modelVersion;
    }

    public void setModelVersion(@Nullable String modelVersion) {
        this.modelVersion = modelVersion;
    }

    public @Nullable Integer getNoiseLevel() {
        return this.noiseLevel;
    }

    public void setNoiseLevel(@Nullable Integer noiseLevel) {
        this.noiseLevel = noiseLevel;
    }

    public @Nullable Float getRefPromptWeight() {
        return this.refPromptWeight;
    }

    public void setRefPromptWeight(@Nullable Float refPromptWeight) {
        this.refPromptWeight = refPromptWeight;
    }

    public @Nullable ReferenceEdge getReferenceEdge() {
        return this.referenceEdge;
    }

    public void setReferenceEdge(@Nullable ReferenceEdge referenceEdge) {
        this.referenceEdge = referenceEdge;
    }

    public @Nullable String getGenerateMode() {
        return this.generateMode;
    }

    public void setGenerateMode(@Nullable String generateMode) {
        this.generateMode = generateMode;
    }

    public @Nullable String getAuxiliaryParameters() {
        return this.auxiliaryParameters;
    }

    public void setAuxiliaryParameters(@Nullable String auxiliaryParameters) {
        this.auxiliaryParameters = auxiliaryParameters;
    }

    public @Nullable String getTitle() {
        return this.title;
    }

    public void setTitle(@Nullable String title) {
        this.title = title;
    }

    public @Nullable String getSubTitle() {
        return this.subTitle;
    }

    public void setSubTitle(@Nullable String subTitle) {
        this.subTitle = subTitle;
    }

    public @Nullable String getBodyText() {
        return this.bodyText;
    }

    public void setBodyText(@Nullable String bodyText) {
        this.bodyText = bodyText;
    }

    public @Nullable String getPromptTextZh() {
        return this.promptTextZh;
    }

    public void setPromptTextZh(@Nullable String promptTextZh) {
        this.promptTextZh = promptTextZh;
    }

    public @Nullable String getPromptTextEn() {
        return this.promptTextEn;
    }

    public void setPromptTextEn(@Nullable String promptTextEn) {
        this.promptTextEn = promptTextEn;
    }

    public @Nullable String getWhRatios() {
        return this.whRatios;
    }

    public void setWhRatios(@Nullable String whRatios) {
        this.whRatios = whRatios;
    }

    public @Nullable String getLoraName() {
        return this.loraName;
    }

    public void setLoraName(@Nullable String loraName) {
        this.loraName = loraName;
    }

    public @Nullable Float getLoraWeight() {
        return this.loraWeight;
    }

    public void setLoraWeight(@Nullable Float loraWeight) {
        this.loraWeight = loraWeight;
    }

    public @Nullable Float getCtrlRatio() {
        return this.ctrlRatio;
    }

    public void setCtrlRatio(@Nullable Float ctrlRatio) {
        this.ctrlRatio = ctrlRatio;
    }

    public @Nullable Float getCtrlStep() {
        return this.ctrlStep;
    }

    public void setCtrlStep(@Nullable Float ctrlStep) {
        this.ctrlStep = ctrlStep;
    }

    public @Nullable Boolean getCreativeTitleLayout() {
        return this.creativeTitleLayout;
    }

    public void setCreativeTitleLayout(@Nullable Boolean creativeTitleLayout) {
        this.creativeTitleLayout = creativeTitleLayout;
    }

    public @Nullable Boolean getFastMode() {
        return this.fastMode;
    }

    public void setFastMode(@Nullable Boolean fastMode) {
        this.fastMode = fastMode;
    }

    public @Nullable Boolean getDilateFlag() {
        return this.dilateFlag;
    }

    public void setDilateFlag(@Nullable Boolean dilateFlag) {
        this.dilateFlag = dilateFlag;
    }

    public @Nullable Boolean getRestoreFace() {
        return this.restoreFace;
    }

    public void setRestoreFace(@Nullable Boolean restoreFace) {
        this.restoreFace = restoreFace;
    }

    public @Nullable String getGender() {
        return this.gender;
    }

    public void setGender(@Nullable String gender) {
        this.gender = gender;
    }

    public @Nullable List<String> getClothesType() {
        return this.clothesType;
    }

    public void setClothesType(@Nullable List<String> clothesType) {
        this.clothesType = clothesType;
    }

    public @Nullable List<Resource> getResources() {
        return this.resources;
    }

    public void setResources(@Nullable List<Resource> resources) {
        this.resources = resources;
    }

    public @Nullable Boolean getSkinRetouch() {
        return this.skinRetouch;
    }

    public void setSkinRetouch(@Nullable Boolean skinRetouch) {
        this.skinRetouch = skinRetouch;
    }

    public @Nullable Integer getSteps() {
        return this.steps;
    }

    public void setSteps(@Nullable Integer steps) {
        this.steps = steps;
    }

    public @Nullable String getFontName() {
        return this.fontName;
    }

    public void setFontName(@Nullable String fontName) {
        this.fontName = fontName;
    }

    public @Nullable String getTtfUrl() {
        return this.ttfUrl;
    }

    public void setTtfUrl(@Nullable String ttfUrl) {
        this.ttfUrl = ttfUrl;
    }

    public @Nullable Integer getImageShortSize() {
        return this.imageShortSize;
    }

    public void setImageShortSize(@Nullable Integer imageShortSize) {
        this.imageShortSize = imageShortSize;
    }

    public @Nullable Boolean getAlphaChannel() {
        return this.alphaChannel;
    }

    public void setAlphaChannel(@Nullable Boolean alphaChannel) {
        this.alphaChannel = alphaChannel;
    }

    public @Nullable List<String> getTrainingFileIds() {
        return this.trainingFileIds;
    }

    public void setTrainingFileIds(@Nullable List<String> trainingFileIds) {
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

    // NullAway loses the nullable array dimensions of the builder parameters across modules.
    @SuppressWarnings("NullAway")
    public DashScopeImageOptions toOptions() {
        return DashScopeImageOptions.builder()
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
                .promptExtendMode(this.promptExtendMode)
                .enableThinking(this.enableThinking)
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
                .requestType(this.requestType)
                .build();
    }

    private Options options = new Options();

    @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX)
    @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
    public Options getOptions() {
        return this.options;
    }

    public void setOptions(Options options) {
        this.options = options;
    }

    public class Options {

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".model")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable String getModel() {
            return DashScopeImageProperties.this.getModel();
        }

        public void setModel(String model) {
            DashScopeImageProperties.this.setModel(model);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".n")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable Integer getN() {
            return DashScopeImageProperties.this.getN();
        }

        public void setN(Integer n) {
            DashScopeImageProperties.this.setN(n);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".width")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable Integer getWidth() {
            return DashScopeImageProperties.this.getWidth();
        }

        public void setWidth(Integer width) {
            DashScopeImageProperties.this.setWidth(width);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".height")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable Integer getHeight() {
            return DashScopeImageProperties.this.getHeight();
        }

        public void setHeight(Integer height) {
            DashScopeImageProperties.this.setHeight(height);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".size")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable String getSize() {
            return DashScopeImageProperties.this.getSize();
        }

        public void setSize(String size) {
            DashScopeImageProperties.this.setSize(size);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".style")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable String getStyle() {
            return DashScopeImageProperties.this.getStyle();
        }

        public void setStyle(String style) {
            DashScopeImageProperties.this.setStyle(style);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".style-index")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable Integer getStyleIndex() {
            return DashScopeImageProperties.this.getStyleIndex();
        }

        public void setStyleIndex(Integer styleIndex) {
            DashScopeImageProperties.this.setStyleIndex(styleIndex);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".style-ref-url")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable String getStyleRefUrl() {
            return DashScopeImageProperties.this.getStyleRefUrl();
        }

        public void setStyleRefUrl(String styleRefUrl) {
            DashScopeImageProperties.this.setStyleRefUrl(styleRefUrl);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".base-image-url")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable String getBaseImageUrl() {
            return DashScopeImageProperties.this.getBaseImageUrl();
        }

        public void setBaseImageUrl(String baseImageUrl) {
            DashScopeImageProperties.this.setBaseImageUrl(baseImageUrl);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".images")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable List<String> getImages() {
            return DashScopeImageProperties.this.getImages();
        }

        public void setImages(List<String> images) {
            DashScopeImageProperties.this.setImages(images);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".mask-image-url")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable String getMaskImageUrl() {
            return DashScopeImageProperties.this.getMaskImageUrl();
        }

        public void setMaskImageUrl(String maskImageUrl) {
            DashScopeImageProperties.this.setMaskImageUrl(maskImageUrl);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".sketch-image-url")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable String getSketchImageUrl() {
            return DashScopeImageProperties.this.getSketchImageUrl();
        }

        public void setSketchImageUrl(String sketchImageUrl) {
            DashScopeImageProperties.this.setSketchImageUrl(sketchImageUrl);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".template-image-url")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable String getTemplateImageUrl() {
            return DashScopeImageProperties.this.getTemplateImageUrl();
        }

        public void setTemplateImageUrl(String templateImageUrl) {
            DashScopeImageProperties.this.setTemplateImageUrl(templateImageUrl);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".shoe-image-url")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable List<String> getShoeImageUrl() {
            return DashScopeImageProperties.this.getShoeImageUrl();
        }

        public void setShoeImageUrl(List<String> shoeImageUrl) {
            DashScopeImageProperties.this.setShoeImageUrl(shoeImageUrl);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".face-image-url")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable String getFaceImageUrl() {
            return DashScopeImageProperties.this.getFaceImageUrl();
        }

        public void setFaceImageUrl(String faceImageUrl) {
            DashScopeImageProperties.this.setFaceImageUrl(faceImageUrl);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".background-image-url")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable String getBackgroundImageUrl() {
            return DashScopeImageProperties.this.getBackgroundImageUrl();
        }

        public void setBackgroundImageUrl(String backgroundImageUrl) {
            DashScopeImageProperties.this.setBackgroundImageUrl(backgroundImageUrl);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".foreground-url")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable String getForegroundUrl() {
            return DashScopeImageProperties.this.getForegroundUrl();
        }

        public void setForegroundUrl(String foregroundUrl) {
            DashScopeImageProperties.this.setForegroundUrl(foregroundUrl);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".person-image-url")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable String getPersonImageUrl() {
            return DashScopeImageProperties.this.getPersonImageUrl();
        }

        public void setPersonImageUrl(String personImageUrl) {
            DashScopeImageProperties.this.setPersonImageUrl(personImageUrl);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".top-garment-url")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable String getTopGarmentUrl() {
            return DashScopeImageProperties.this.getTopGarmentUrl();
        }

        public void setTopGarmentUrl(String topGarmentUrl) {
            DashScopeImageProperties.this.setTopGarmentUrl(topGarmentUrl);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".bottom-garment-url")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable String getBottomGarmentUrl() {
            return DashScopeImageProperties.this.getBottomGarmentUrl();
        }

        public void setBottomGarmentUrl(String bottomGarmentUrl) {
            DashScopeImageProperties.this.setBottomGarmentUrl(bottomGarmentUrl);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".coarse-image-url")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable String getCoarseImageUrl() {
            return DashScopeImageProperties.this.getCoarseImageUrl();
        }

        public void setCoarseImageUrl(String coarseImageUrl) {
            DashScopeImageProperties.this.setCoarseImageUrl(coarseImageUrl);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".user-urls")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable List<String> getUserUrls() {
            return DashScopeImageProperties.this.getUserUrls();
        }

        public void setUserUrls(List<String> userUrls) {
            DashScopeImageProperties.this.setUserUrls(userUrls);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".ref-img")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable String getRefImg() {
            return DashScopeImageProperties.this.getRefImg();
        }

        public void setRefImg(String refImg) {
            DashScopeImageProperties.this.setRefImg(refImg);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".predefined-face-id")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable String getPredefinedFaceId() {
            return DashScopeImageProperties.this.getPredefinedFaceId();
        }

        public void setPredefinedFaceId(String predefinedFaceId) {
            DashScopeImageProperties.this.setPredefinedFaceId(predefinedFaceId);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".face-prompt")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable String getFacePrompt() {
            return DashScopeImageProperties.this.getFacePrompt();
        }

        public void setFacePrompt(String facePrompt) {
            DashScopeImageProperties.this.setFacePrompt(facePrompt);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".bgstyle-scale")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable Float getBgstyleScale() {
            return DashScopeImageProperties.this.getBgstyleScale();
        }

        public void setBgstyleScale(Float bgstyleScale) {
            DashScopeImageProperties.this.setBgstyleScale(bgstyleScale);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".real-person")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable Boolean getRealPerson() {
            return DashScopeImageProperties.this.getRealPerson();
        }

        public void setRealPerson(Boolean realPerson) {
            DashScopeImageProperties.this.setRealPerson(realPerson);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".seed")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable Integer getSeed() {
            return DashScopeImageProperties.this.getSeed();
        }

        public void setSeed(Integer seed) {
            DashScopeImageProperties.this.setSeed(seed);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".ref-strength")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable Float getRefStrength() {
            return DashScopeImageProperties.this.getRefStrength();
        }

        public void setRefStrength(Float refStrength) {
            DashScopeImageProperties.this.setRefStrength(refStrength);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".response-format")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable String getResponseFormat() {
            return DashScopeImageProperties.this.getResponseFormat();
        }

        public void setResponseFormat(String responseFormat) {
            DashScopeImageProperties.this.setResponseFormat(responseFormat);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".ref-mode")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable String getRefMode() {
            return DashScopeImageProperties.this.getRefMode();
        }

        public void setRefMode(String refMode) {
            DashScopeImageProperties.this.setRefMode(refMode);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".negative-prompt")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable String getNegativePrompt() {
            return DashScopeImageProperties.this.getNegativePrompt();
        }

        public void setNegativePrompt(String negativePrompt) {
            DashScopeImageProperties.this.setNegativePrompt(negativePrompt);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".text")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable String getText() {
            return DashScopeImageProperties.this.getText();
        }

        public void setText(String text) {
            DashScopeImageProperties.this.setText(text);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".prompt-extend")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable Boolean getPromptExtend() {
            return DashScopeImageProperties.this.getPromptExtend();
        }

        public void setPromptExtend(Boolean promptExtend) {
            DashScopeImageProperties.this.setPromptExtend(promptExtend);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".watermark")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable Boolean getWatermark() {
            return DashScopeImageProperties.this.getWatermark();
        }

        public void setWatermark(Boolean watermark) {
            DashScopeImageProperties.this.setWatermark(watermark);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".function")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable String getFunction() {
            return DashScopeImageProperties.this.getFunction();
        }

        public void setFunction(String function) {
            DashScopeImageProperties.this.setFunction(function);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".sketch-weight")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable Integer getSketchWeight() {
            return DashScopeImageProperties.this.getSketchWeight();
        }

        public void setSketchWeight(Integer sketchWeight) {
            DashScopeImageProperties.this.setSketchWeight(sketchWeight);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".sketch-extraction")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable Boolean getSketchExtraction() {
            return DashScopeImageProperties.this.getSketchExtraction();
        }

        public void setSketchExtraction(Boolean sketchExtraction) {
            DashScopeImageProperties.this.setSketchExtraction(sketchExtraction);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".sketch-color")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable Integer @Nullable [][] getSketchColor() {
            return DashScopeImageProperties.this.getSketchColor();
        }

        public void setSketchColor(@Nullable Integer @Nullable [][] sketchColor) {
            DashScopeImageProperties.this.setSketchColor(sketchColor);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".mask-color")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable Integer @Nullable [][] getMaskColor() {
            return DashScopeImageProperties.this.getMaskColor();
        }

        public void setMaskColor(@Nullable Integer @Nullable [][] maskColor) {
            DashScopeImageProperties.this.setMaskColor(maskColor);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".bbox-list")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable Integer @Nullable [][][] getBboxList() {
            return DashScopeImageProperties.this.getBboxList();
        }

        public void setBboxList(@Nullable Integer @Nullable [][][] bboxList) {
            DashScopeImageProperties.this.setBboxList(bboxList);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".max-images")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable Integer getMaxImages() {
            return DashScopeImageProperties.this.getMaxImages();
        }

        public void setMaxImages(Integer maxImages) {
            DashScopeImageProperties.this.setMaxImages(maxImages);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".enable-interleave")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable Boolean getEnableInterleave() {
            return DashScopeImageProperties.this.getEnableInterleave();
        }

        public void setEnableInterleave(Boolean enableInterleave) {
            DashScopeImageProperties.this.setEnableInterleave(enableInterleave);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".enable-sequential")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable Boolean getEnableSequential() {
            return DashScopeImageProperties.this.getEnableSequential();
        }

        public void setEnableSequential(Boolean enableSequential) {
            DashScopeImageProperties.this.setEnableSequential(enableSequential);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".color-palette")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable List<ColorPaletteItem> getColorPalette() {
            return DashScopeImageProperties.this.getColorPalette();
        }

        public void setColorPalette(List<ColorPaletteItem> colorPalette) {
            DashScopeImageProperties.this.setColorPalette(colorPalette);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".thinking-mode")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable Boolean getThinkingMode() {
            return DashScopeImageProperties.this.getThinkingMode();
        }

        public void setThinkingMode(Boolean thinkingMode) {
            DashScopeImageProperties.this.setThinkingMode(thinkingMode);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".output-ratio")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable String getOutputRatio() {
            return DashScopeImageProperties.this.getOutputRatio();
        }

        public void setOutputRatio(String outputRatio) {
            DashScopeImageProperties.this.setOutputRatio(outputRatio);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".x-scale")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable Float getXScale() {
            return DashScopeImageProperties.this.getXScale();
        }

        public void setXScale(Float xScale) {
            DashScopeImageProperties.this.setXScale(xScale);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".y-scale")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable Float getYScale() {
            return DashScopeImageProperties.this.getYScale();
        }

        public void setYScale(Float yScale) {
            DashScopeImageProperties.this.setYScale(yScale);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".angle")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable Integer getAngle() {
            return DashScopeImageProperties.this.getAngle();
        }

        public void setAngle(Integer angle) {
            DashScopeImageProperties.this.setAngle(angle);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".left-offset")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable Integer getLeftOffset() {
            return DashScopeImageProperties.this.getLeftOffset();
        }

        public void setLeftOffset(Integer leftOffset) {
            DashScopeImageProperties.this.setLeftOffset(leftOffset);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".right-offset")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable Integer getRightOffset() {
            return DashScopeImageProperties.this.getRightOffset();
        }

        public void setRightOffset(Integer rightOffset) {
            DashScopeImageProperties.this.setRightOffset(rightOffset);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".top-offset")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable Integer getTopOffset() {
            return DashScopeImageProperties.this.getTopOffset();
        }

        public void setTopOffset(Integer topOffset) {
            DashScopeImageProperties.this.setTopOffset(topOffset);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".bottom-offset")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable Integer getBottomOffset() {
            return DashScopeImageProperties.this.getBottomOffset();
        }

        public void setBottomOffset(Integer bottomOffset) {
            DashScopeImageProperties.this.setBottomOffset(bottomOffset);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".best-quality")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable Boolean getBestQuality() {
            return DashScopeImageProperties.this.getBestQuality();
        }

        public void setBestQuality(Boolean bestQuality) {
            DashScopeImageProperties.this.setBestQuality(bestQuality);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".limit-image-size")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable Boolean getLimitImageSize() {
            return DashScopeImageProperties.this.getLimitImageSize();
        }

        public void setLimitImageSize(Boolean limitImageSize) {
            DashScopeImageProperties.this.setLimitImageSize(limitImageSize);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".source-lang")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable String getSourceLang() {
            return DashScopeImageProperties.this.getSourceLang();
        }

        public void setSourceLang(String sourceLang) {
            DashScopeImageProperties.this.setSourceLang(sourceLang);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".target-lang")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable String getTargetLang() {
            return DashScopeImageProperties.this.getTargetLang();
        }

        public void setTargetLang(String targetLang) {
            DashScopeImageProperties.this.setTargetLang(targetLang);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".ext")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable Object getExt() {
            return DashScopeImageProperties.this.getExt();
        }

        public void setExt(Object ext) {
            DashScopeImageProperties.this.setExt(ext);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".element-list")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable List<Element> getElementList() {
            return DashScopeImageProperties.this.getElementList();
        }

        public void setElementList(List<Element> elementList) {
            DashScopeImageProperties.this.setElementList(elementList);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".result-type")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable String getResultType() {
            return DashScopeImageProperties.this.getResultType();
        }

        public void setResultType(String resultType) {
            DashScopeImageProperties.this.setResultType(resultType);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".series-amount")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable Integer getSeriesAmount() {
            return DashScopeImageProperties.this.getSeriesAmount();
        }

        public void setSeriesAmount(Integer seriesAmount) {
            DashScopeImageProperties.this.setSeriesAmount(seriesAmount);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".aspect-ratio")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable String getAspectRatio() {
            return DashScopeImageProperties.this.getAspectRatio();
        }

        public void setAspectRatio(String aspectRatio) {
            DashScopeImageProperties.this.setAspectRatio(aspectRatio);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".resolution")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable String getResolution() {
            return DashScopeImageProperties.this.getResolution();
        }

        public void setResolution(String resolution) {
            DashScopeImageProperties.this.setResolution(resolution);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".short-side-size")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable String getShortSideSize() {
            return DashScopeImageProperties.this.getShortSideSize();
        }

        public void setShortSideSize(String shortSideSize) {
            DashScopeImageProperties.this.setShortSideSize(shortSideSize);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".scale")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable Float getScale() {
            return DashScopeImageProperties.this.getScale();
        }

        public void setScale(Float scale) {
            DashScopeImageProperties.this.setScale(scale);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".model-version")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable String getModelVersion() {
            return DashScopeImageProperties.this.getModelVersion();
        }

        public void setModelVersion(String modelVersion) {
            DashScopeImageProperties.this.setModelVersion(modelVersion);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".noise-level")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable Integer getNoiseLevel() {
            return DashScopeImageProperties.this.getNoiseLevel();
        }

        public void setNoiseLevel(Integer noiseLevel) {
            DashScopeImageProperties.this.setNoiseLevel(noiseLevel);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".ref-prompt-weight")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable Float getRefPromptWeight() {
            return DashScopeImageProperties.this.getRefPromptWeight();
        }

        public void setRefPromptWeight(Float refPromptWeight) {
            DashScopeImageProperties.this.setRefPromptWeight(refPromptWeight);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".reference-edge")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable ReferenceEdge getReferenceEdge() {
            return DashScopeImageProperties.this.getReferenceEdge();
        }

        public void setReferenceEdge(ReferenceEdge referenceEdge) {
            DashScopeImageProperties.this.setReferenceEdge(referenceEdge);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".generate-mode")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable String getGenerateMode() {
            return DashScopeImageProperties.this.getGenerateMode();
        }

        public void setGenerateMode(String generateMode) {
            DashScopeImageProperties.this.setGenerateMode(generateMode);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".auxiliary-parameters")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable String getAuxiliaryParameters() {
            return DashScopeImageProperties.this.getAuxiliaryParameters();
        }

        public void setAuxiliaryParameters(String auxiliaryParameters) {
            DashScopeImageProperties.this.setAuxiliaryParameters(auxiliaryParameters);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".title")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable String getTitle() {
            return DashScopeImageProperties.this.getTitle();
        }

        public void setTitle(String title) {
            DashScopeImageProperties.this.setTitle(title);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".sub-title")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable String getSubTitle() {
            return DashScopeImageProperties.this.getSubTitle();
        }

        public void setSubTitle(String subTitle) {
            DashScopeImageProperties.this.setSubTitle(subTitle);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".body-text")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable String getBodyText() {
            return DashScopeImageProperties.this.getBodyText();
        }

        public void setBodyText(String bodyText) {
            DashScopeImageProperties.this.setBodyText(bodyText);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".prompt-text-zh")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable String getPromptTextZh() {
            return DashScopeImageProperties.this.getPromptTextZh();
        }

        public void setPromptTextZh(String promptTextZh) {
            DashScopeImageProperties.this.setPromptTextZh(promptTextZh);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".prompt-text-en")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable String getPromptTextEn() {
            return DashScopeImageProperties.this.getPromptTextEn();
        }

        public void setPromptTextEn(String promptTextEn) {
            DashScopeImageProperties.this.setPromptTextEn(promptTextEn);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".wh-ratios")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable String getWhRatios() {
            return DashScopeImageProperties.this.getWhRatios();
        }

        public void setWhRatios(String whRatios) {
            DashScopeImageProperties.this.setWhRatios(whRatios);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".lora-name")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable String getLoraName() {
            return DashScopeImageProperties.this.getLoraName();
        }

        public void setLoraName(String loraName) {
            DashScopeImageProperties.this.setLoraName(loraName);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".lora-weight")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable Float getLoraWeight() {
            return DashScopeImageProperties.this.getLoraWeight();
        }

        public void setLoraWeight(Float loraWeight) {
            DashScopeImageProperties.this.setLoraWeight(loraWeight);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".ctrl-ratio")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable Float getCtrlRatio() {
            return DashScopeImageProperties.this.getCtrlRatio();
        }

        public void setCtrlRatio(Float ctrlRatio) {
            DashScopeImageProperties.this.setCtrlRatio(ctrlRatio);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".ctrl-step")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable Float getCtrlStep() {
            return DashScopeImageProperties.this.getCtrlStep();
        }

        public void setCtrlStep(Float ctrlStep) {
            DashScopeImageProperties.this.setCtrlStep(ctrlStep);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".creative-title-layout")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable Boolean getCreativeTitleLayout() {
            return DashScopeImageProperties.this.getCreativeTitleLayout();
        }

        public void setCreativeTitleLayout(Boolean creativeTitleLayout) {
            DashScopeImageProperties.this.setCreativeTitleLayout(creativeTitleLayout);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".fast-mode")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable Boolean getFastMode() {
            return DashScopeImageProperties.this.getFastMode();
        }

        public void setFastMode(Boolean fastMode) {
            DashScopeImageProperties.this.setFastMode(fastMode);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".dilate-flag")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable Boolean getDilateFlag() {
            return DashScopeImageProperties.this.getDilateFlag();
        }

        public void setDilateFlag(Boolean dilateFlag) {
            DashScopeImageProperties.this.setDilateFlag(dilateFlag);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".restore-face")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable Boolean getRestoreFace() {
            return DashScopeImageProperties.this.getRestoreFace();
        }

        public void setRestoreFace(Boolean restoreFace) {
            DashScopeImageProperties.this.setRestoreFace(restoreFace);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".gender")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable String getGender() {
            return DashScopeImageProperties.this.getGender();
        }

        public void setGender(String gender) {
            DashScopeImageProperties.this.setGender(gender);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".clothes-type")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable List<String> getClothesType() {
            return DashScopeImageProperties.this.getClothesType();
        }

        public void setClothesType(List<String> clothesType) {
            DashScopeImageProperties.this.setClothesType(clothesType);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".resources")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable List<Resource> getResources() {
            return DashScopeImageProperties.this.getResources();
        }

        public void setResources(List<Resource> resources) {
            DashScopeImageProperties.this.setResources(resources);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".skin-retouch")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable Boolean getSkinRetouch() {
            return DashScopeImageProperties.this.getSkinRetouch();
        }

        public void setSkinRetouch(Boolean skinRetouch) {
            DashScopeImageProperties.this.setSkinRetouch(skinRetouch);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".steps")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable Integer getSteps() {
            return DashScopeImageProperties.this.getSteps();
        }

        public void setSteps(Integer steps) {
            DashScopeImageProperties.this.setSteps(steps);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".font-name")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable String getFontName() {
            return DashScopeImageProperties.this.getFontName();
        }

        public void setFontName(String fontName) {
            DashScopeImageProperties.this.setFontName(fontName);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".ttf-url")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable String getTtfUrl() {
            return DashScopeImageProperties.this.getTtfUrl();
        }

        public void setTtfUrl(String ttfUrl) {
            DashScopeImageProperties.this.setTtfUrl(ttfUrl);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".image-short-size")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable Integer getImageShortSize() {
            return DashScopeImageProperties.this.getImageShortSize();
        }

        public void setImageShortSize(Integer imageShortSize) {
            DashScopeImageProperties.this.setImageShortSize(imageShortSize);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".alpha-channel")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable Boolean getAlphaChannel() {
            return DashScopeImageProperties.this.getAlphaChannel();
        }

        public void setAlphaChannel(Boolean alphaChannel) {
            DashScopeImageProperties.this.setAlphaChannel(alphaChannel);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".training-file-ids")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable List<String> getTrainingFileIds() {
            return DashScopeImageProperties.this.getTrainingFileIds();
        }

        public void setTrainingFileIds(List<String> trainingFileIds) {
            DashScopeImageProperties.this.setTrainingFileIds(trainingFileIds);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".invoke-mode")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable InvokeMode getInvokeMode() {
            return DashScopeImageProperties.this.getInvokeMode();
        }

        public void setInvokeMode(InvokeMode invokeMode) {
            DashScopeImageProperties.this.setInvokeMode(invokeMode);
        }

        @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".request-type")
        @Deprecated(since = "2.0.0.0-RC1", forRemoval = true)
        public @Nullable RequestType getRequestType() {
            return DashScopeImageProperties.this.getRequestType();
        }

        public void setRequestType(RequestType requestType) {
            DashScopeImageProperties.this.setRequestType(requestType);
        }

    }

}
