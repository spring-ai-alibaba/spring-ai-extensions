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
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * TongYi Image API properties.
 *
 * @author yuluo
 * @author <a href="mailto:yuluo08290126@gmail.com">yuluo</a>
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

    @NestedConfigurationProperty
    private DashScopeImageOptions options = DashScopeImageOptions.builder()
            .model(DashScopeModel.ImageModel.WAN_2_2_T2I_FLASH.getValue())
            .n(1)
            .build();

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

    public DashScopeImageOptions toOptions() {
        return this.options;
    }

    @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX)
    @Deprecated(since = "2.0.0", forRemoval = true)
    public DashScopeImageOptions getOptions() {
        return this.options;
    }

    public void setOptions(DashScopeImageOptions options) {
        this.options = options;
    }

    public String getModel() {
        return this.options.getModel();
    }

    public void setModel(String model) {
        this.options.setModel(model);
    }

    public Integer getN() {
        return this.options.getN();
    }

    public void setN(Integer n) {
        this.options.setN(n);
    }

    public Integer getWidth() {
        return this.options.getWidth();
    }

    public void setWidth(Integer width) {
        this.options.setWidth(width);
    }

    public Integer getHeight() {
        return this.options.getHeight();
    }

    public void setHeight(Integer height) {
        this.options.setHeight(height);
    }

    public String getSize() {
        return this.options.getSize();
    }

    public void setSize(String size) {
        this.options.setSize(size);
    }

    public String getStyle() {
        return this.options.getStyle();
    }

    public void setStyle(String style) {
        this.options.setStyle(style);
    }

    public Integer getStyleIndex() {
        return this.options.getStyleIndex();
    }

    public void setStyleIndex(Integer styleIndex) {
        this.options.setStyleIndex(styleIndex);
    }

    public String getStyleRefUrl() {
        return this.options.getStyleRefUrl();
    }

    public void setStyleRefUrl(String styleRefUrl) {
        this.options.setStyleRefUrl(styleRefUrl);
    }

    public String getBaseImageUrl() {
        return this.options.getBaseImageUrl();
    }

    public void setBaseImageUrl(String baseImageUrl) {
        this.options.setBaseImageUrl(baseImageUrl);
    }

    public List<String> getImages() {
        return this.options.getImages();
    }

    public void setImages(List<String> images) {
        this.options.setImages(images);
    }

    public String getMaskImageUrl() {
        return this.options.getMaskImageUrl();
    }

    public void setMaskImageUrl(String maskImageUrl) {
        this.options.setMaskImageUrl(maskImageUrl);
    }

    public String getSketchImageUrl() {
        return this.options.getSketchImageUrl();
    }

    public void setSketchImageUrl(String sketchImageUrl) {
        this.options.setSketchImageUrl(sketchImageUrl);
    }

    public String getTemplateImageUrl() {
        return this.options.getTemplateImageUrl();
    }

    public void setTemplateImageUrl(String templateImageUrl) {
        this.options.setTemplateImageUrl(templateImageUrl);
    }

    public List<String> getShoeImageUrl() {
        return this.options.getShoeImageUrl();
    }

    public void setShoeImageUrl(List<String> shoeImageUrl) {
        this.options.setShoeImageUrl(shoeImageUrl);
    }

    public String getFaceImageUrl() {
        return this.options.getFaceImageUrl();
    }

    public void setFaceImageUrl(String faceImageUrl) {
        this.options.setFaceImageUrl(faceImageUrl);
    }

    public String getBackgroundImageUrl() {
        return this.options.getBackgroundImageUrl();
    }

    public void setBackgroundImageUrl(String backgroundImageUrl) {
        this.options.setBackgroundImageUrl(backgroundImageUrl);
    }

    public String getForegroundUrl() {
        return this.options.getForegroundUrl();
    }

    public void setForegroundUrl(String foregroundUrl) {
        this.options.setForegroundUrl(foregroundUrl);
    }

    public String getPersonImageUrl() {
        return this.options.getPersonImageUrl();
    }

    public void setPersonImageUrl(String personImageUrl) {
        this.options.setPersonImageUrl(personImageUrl);
    }

    public String getTopGarmentUrl() {
        return this.options.getTopGarmentUrl();
    }

    public void setTopGarmentUrl(String topGarmentUrl) {
        this.options.setTopGarmentUrl(topGarmentUrl);
    }

    public String getBottomGarmentUrl() {
        return this.options.getBottomGarmentUrl();
    }

    public void setBottomGarmentUrl(String bottomGarmentUrl) {
        this.options.setBottomGarmentUrl(bottomGarmentUrl);
    }

    public String getCoarseImageUrl() {
        return this.options.getCoarseImageUrl();
    }

    public void setCoarseImageUrl(String coarseImageUrl) {
        this.options.setCoarseImageUrl(coarseImageUrl);
    }

    public List<String> getUserUrls() {
        return this.options.getUserUrls();
    }

    public void setUserUrls(List<String> userUrls) {
        this.options.setUserUrls(userUrls);
    }

    public String getRefImg() {
        return this.options.getRefImg();
    }

    public void setRefImg(String refImg) {
        this.options.setRefImg(refImg);
    }

    public String getPredefinedFaceId() {
        return this.options.getPredefinedFaceId();
    }

    public void setPredefinedFaceId(String predefinedFaceId) {
        this.options.setPredefinedFaceId(predefinedFaceId);
    }

    public String getFacePrompt() {
        return this.options.getFacePrompt();
    }

    public void setFacePrompt(String facePrompt) {
        this.options.setFacePrompt(facePrompt);
    }

    public Float getBgstyleScale() {
        return this.options.getBgstyleScale();
    }

    public void setBgstyleScale(Float bgstyleScale) {
        this.options.setBgstyleScale(bgstyleScale);
    }

    public Boolean getRealPerson() {
        return this.options.getRealPerson();
    }

    public void setRealPerson(Boolean realPerson) {
        this.options.setRealPerson(realPerson);
    }

    public Integer getSeed() {
        return this.options.getSeed();
    }

    public void setSeed(Integer seed) {
        this.options.setSeed(seed);
    }

    public Float getRefStrength() {
        return this.options.getRefStrength();
    }

    public void setRefStrength(Float refStrength) {
        this.options.setRefStrength(refStrength);
    }

    public String getResponseFormat() {
        return this.options.getResponseFormat();
    }

    public void setResponseFormat(String responseFormat) {
        this.options.setResponseFormat(responseFormat);
    }

    public String getRefMode() {
        return this.options.getRefMode();
    }

    public void setRefMode(String refMode) {
        this.options.setRefMode(refMode);
    }

    public String getNegativePrompt() {
        return this.options.getNegativePrompt();
    }

    public void setNegativePrompt(String negativePrompt) {
        this.options.setNegativePrompt(negativePrompt);
    }

    public String getText() {
        return this.options.getText();
    }

    public void setText(String text) {
        this.options.setText(text);
    }

    public Boolean getPromptExtend() {
        return this.options.getPromptExtend();
    }

    public void setPromptExtend(Boolean promptExtend) {
        this.options.setPromptExtend(promptExtend);
    }

    public Boolean getWatermark() {
        return this.options.getWatermark();
    }

    public void setWatermark(Boolean watermark) {
        this.options.setWatermark(watermark);
    }

    public String getFunction() {
        return this.options.getFunction();
    }

    public void setFunction(String function) {
        this.options.setFunction(function);
    }

    public Integer getSketchWeight() {
        return this.options.getSketchWeight();
    }

    public void setSketchWeight(Integer sketchWeight) {
        this.options.setSketchWeight(sketchWeight);
    }

    public Boolean getSketchExtraction() {
        return this.options.getSketchExtraction();
    }

    public void setSketchExtraction(Boolean sketchExtraction) {
        this.options.setSketchExtraction(sketchExtraction);
    }

    public Integer[][] getSketchColor() {
        return this.options.getSketchColor();
    }

    public void setSketchColor(Integer[][] sketchColor) {
        this.options.setSketchColor(sketchColor);
    }

    public Integer[][] getMaskColor() {
        return this.options.getMaskColor();
    }

    public void setMaskColor(Integer[][] maskColor) {
        this.options.setMaskColor(maskColor);
    }

    public Integer[][][] getBboxList() {
        return this.options.getBboxList();
    }

    public void setBboxList(Integer[][][] bboxList) {
        this.options.setBboxList(bboxList);
    }

    public Integer getMaxImages() {
        return this.options.getMaxImages();
    }

    public void setMaxImages(Integer maxImages) {
        this.options.setMaxImages(maxImages);
    }

    public Boolean getEnableInterleave() {
        return this.options.getEnableInterleave();
    }

    public void setEnableInterleave(Boolean enableInterleave) {
        this.options.setEnableInterleave(enableInterleave);
    }

    public Boolean getEnableSequential() {
        return this.options.getEnableSequential();
    }

    public void setEnableSequential(Boolean enableSequential) {
        this.options.setEnableSequential(enableSequential);
    }

    public List<ColorPaletteItem> getColorPalette() {
        return this.options.getColorPalette();
    }

    public void setColorPalette(List<ColorPaletteItem> colorPalette) {
        this.options.setColorPalette(colorPalette);
    }

    public Boolean getThinkingMode() {
        return this.options.getThinkingMode();
    }

    public void setThinkingMode(Boolean thinkingMode) {
        this.options.setThinkingMode(thinkingMode);
    }

    public String getOutputRatio() {
        return this.options.getOutputRatio();
    }

    public void setOutputRatio(String outputRatio) {
        this.options.setOutputRatio(outputRatio);
    }

    public Float getXScale() {
        return this.options.getXScale();
    }

    public void setXScale(Float xScale) {
        this.options.setXScale(xScale);
    }

    public Float getYScale() {
        return this.options.getYScale();
    }

    public void setYScale(Float yScale) {
        this.options.setYScale(yScale);
    }

    public Integer getAngle() {
        return this.options.getAngle();
    }

    public void setAngle(Integer angle) {
        this.options.setAngle(angle);
    }

    public Integer getLeftOffset() {
        return this.options.getLeftOffset();
    }

    public void setLeftOffset(Integer leftOffset) {
        this.options.setLeftOffset(leftOffset);
    }

    public Integer getRightOffset() {
        return this.options.getRightOffset();
    }

    public void setRightOffset(Integer rightOffset) {
        this.options.setRightOffset(rightOffset);
    }

    public Integer getTopOffset() {
        return this.options.getTopOffset();
    }

    public void setTopOffset(Integer topOffset) {
        this.options.setTopOffset(topOffset);
    }

    public Integer getBottomOffset() {
        return this.options.getBottomOffset();
    }

    public void setBottomOffset(Integer bottomOffset) {
        this.options.setBottomOffset(bottomOffset);
    }

    public Boolean getBestQuality() {
        return this.options.getBestQuality();
    }

    public void setBestQuality(Boolean bestQuality) {
        this.options.setBestQuality(bestQuality);
    }

    public Boolean getLimitImageSize() {
        return this.options.getLimitImageSize();
    }

    public void setLimitImageSize(Boolean limitImageSize) {
        this.options.setLimitImageSize(limitImageSize);
    }

    public String getSourceLang() {
        return this.options.getSourceLang();
    }

    public void setSourceLang(String sourceLang) {
        this.options.setSourceLang(sourceLang);
    }

    public String getTargetLang() {
        return this.options.getTargetLang();
    }

    public void setTargetLang(String targetLang) {
        this.options.setTargetLang(targetLang);
    }

    public Object getExt() {
        return this.options.getExt();
    }

    public void setExt(Object ext) {
        this.options.setExt(ext);
    }

    public List<Element> getElementList() {
        return this.options.getElementList();
    }

    public void setElementList(List<Element> elementList) {
        this.options.setElementList(elementList);
    }

    public String getResultType() {
        return this.options.getResultType();
    }

    public void setResultType(String resultType) {
        this.options.setResultType(resultType);
    }

    public Integer getSeriesAmount() {
        return this.options.getSeriesAmount();
    }

    public void setSeriesAmount(Integer seriesAmount) {
        this.options.setSeriesAmount(seriesAmount);
    }

    public String getAspectRatio() {
        return this.options.getAspectRatio();
    }

    public void setAspectRatio(String aspectRatio) {
        this.options.setAspectRatio(aspectRatio);
    }

    public String getResolution() {
        return this.options.getResolution();
    }

    public void setResolution(String resolution) {
        this.options.setResolution(resolution);
    }

    public String getShortSideSize() {
        return this.options.getShortSideSize();
    }

    public void setShortSideSize(String shortSideSize) {
        this.options.setShortSideSize(shortSideSize);
    }

    public Float getScale() {
        return this.options.getScale();
    }

    public void setScale(Float scale) {
        this.options.setScale(scale);
    }

    public String getModelVersion() {
        return this.options.getModelVersion();
    }

    public void setModelVersion(String modelVersion) {
        this.options.setModelVersion(modelVersion);
    }

    public Integer getNoiseLevel() {
        return this.options.getNoiseLevel();
    }

    public void setNoiseLevel(Integer noiseLevel) {
        this.options.setNoiseLevel(noiseLevel);
    }

    public Float getRefPromptWeight() {
        return this.options.getRefPromptWeight();
    }

    public void setRefPromptWeight(Float refPromptWeight) {
        this.options.setRefPromptWeight(refPromptWeight);
    }

    public ReferenceEdge getReferenceEdge() {
        return this.options.getReferenceEdge();
    }

    public void setReferenceEdge(ReferenceEdge referenceEdge) {
        this.options.setReferenceEdge(referenceEdge);
    }

    public String getGenerateMode() {
        return this.options.getGenerateMode();
    }

    public void setGenerateMode(String generateMode) {
        this.options.setGenerateMode(generateMode);
    }

    public String getAuxiliaryParameters() {
        return this.options.getAuxiliaryParameters();
    }

    public void setAuxiliaryParameters(String auxiliaryParameters) {
        this.options.setAuxiliaryParameters(auxiliaryParameters);
    }

    public String getTitle() {
        return this.options.getTitle();
    }

    public void setTitle(String title) {
        this.options.setTitle(title);
    }

    public String getSubTitle() {
        return this.options.getSubTitle();
    }

    public void setSubTitle(String subTitle) {
        this.options.setSubTitle(subTitle);
    }

    public String getBodyText() {
        return this.options.getBodyText();
    }

    public void setBodyText(String bodyText) {
        this.options.setBodyText(bodyText);
    }

    public String getPromptTextZh() {
        return this.options.getPromptTextZh();
    }

    public void setPromptTextZh(String promptTextZh) {
        this.options.setPromptTextZh(promptTextZh);
    }

    public String getPromptTextEn() {
        return this.options.getPromptTextEn();
    }

    public void setPromptTextEn(String promptTextEn) {
        this.options.setPromptTextEn(promptTextEn);
    }

    public String getWhRatios() {
        return this.options.getWhRatios();
    }

    public void setWhRatios(String whRatios) {
        this.options.setWhRatios(whRatios);
    }

    public String getLoraName() {
        return this.options.getLoraName();
    }

    public void setLoraName(String loraName) {
        this.options.setLoraName(loraName);
    }

    public Float getLoraWeight() {
        return this.options.getLoraWeight();
    }

    public void setLoraWeight(Float loraWeight) {
        this.options.setLoraWeight(loraWeight);
    }

    public Float getCtrlRatio() {
        return this.options.getCtrlRatio();
    }

    public void setCtrlRatio(Float ctrlRatio) {
        this.options.setCtrlRatio(ctrlRatio);
    }

    public Float getCtrlStep() {
        return this.options.getCtrlStep();
    }

    public void setCtrlStep(Float ctrlStep) {
        this.options.setCtrlStep(ctrlStep);
    }

    public Boolean getCreativeTitleLayout() {
        return this.options.getCreativeTitleLayout();
    }

    public void setCreativeTitleLayout(Boolean creativeTitleLayout) {
        this.options.setCreativeTitleLayout(creativeTitleLayout);
    }

    public Boolean getFastMode() {
        return this.options.getFastMode();
    }

    public void setFastMode(Boolean fastMode) {
        this.options.setFastMode(fastMode);
    }

    public Boolean getDilateFlag() {
        return this.options.getDilateFlag();
    }

    public void setDilateFlag(Boolean dilateFlag) {
        this.options.setDilateFlag(dilateFlag);
    }

    public Boolean getRestoreFace() {
        return this.options.getRestoreFace();
    }

    public void setRestoreFace(Boolean restoreFace) {
        this.options.setRestoreFace(restoreFace);
    }

    public String getGender() {
        return this.options.getGender();
    }

    public void setGender(String gender) {
        this.options.setGender(gender);
    }

    public List<String> getClothesType() {
        return this.options.getClothesType();
    }

    public void setClothesType(List<String> clothesType) {
        this.options.setClothesType(clothesType);
    }

    public List<Resource> getResources() {
        return this.options.getResources();
    }

    public void setResources(List<Resource> resources) {
        this.options.setResources(resources);
    }

    public Boolean getSkinRetouch() {
        return this.options.getSkinRetouch();
    }

    public void setSkinRetouch(Boolean skinRetouch) {
        this.options.setSkinRetouch(skinRetouch);
    }

    public Integer getSteps() {
        return this.options.getSteps();
    }

    public void setSteps(Integer steps) {
        this.options.setSteps(steps);
    }

    public String getFontName() {
        return this.options.getFontName();
    }

    public void setFontName(String fontName) {
        this.options.setFontName(fontName);
    }

    public String getTtfUrl() {
        return this.options.getTtfUrl();
    }

    public void setTtfUrl(String ttfUrl) {
        this.options.setTtfUrl(ttfUrl);
    }

    public Integer getImageShortSize() {
        return this.options.getImageShortSize();
    }

    public void setImageShortSize(Integer imageShortSize) {
        this.options.setImageShortSize(imageShortSize);
    }

    public Boolean getAlphaChannel() {
        return this.options.getAlphaChannel();
    }

    public void setAlphaChannel(Boolean alphaChannel) {
        this.options.setAlphaChannel(alphaChannel);
    }

    public List<String> getTrainingFileIds() {
        return this.options.getTrainingFileIds();
    }

    public void setTrainingFileIds(List<String> trainingFileIds) {
        this.options.setTrainingFileIds(trainingFileIds);
    }

    public InvokeMode getInvokeMode() {
        return this.options.getInvokeMode();
    }

    public void setInvokeMode(InvokeMode invokeMode) {
        this.options.setInvokeMode(invokeMode);
    }

    public RequestType getRequestType() {
        return this.options.getRequestType();
    }

    public void setRequestType(RequestType requestType) {
        this.options.setRequestType(requestType);
    }
}
