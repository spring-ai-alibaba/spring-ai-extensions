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
	private final Options legacyOptions = new Options();

    public DashScopeImageOptions toOptions() {
		if (this.options == null) {
			this.options = DashScopeImageOptions.builder().model(DashScopeModel.ImageModel.WAN_2_2_T2I_FLASH.getValue()).n(1).build();
		}
		return this.options;
	}

    @DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX)
    @Deprecated(since = "2.0.0", forRemoval = true)
    public Options getOptions() {
		return this.legacyOptions;
	}

	public void setOptions(Options options) {
		// Deprecated options are applied by the nested Options setters.
	}

	private void updateOptions(java.util.function.Consumer<DashScopeImageOptions.Builder> customizer) {
		DashScopeImageOptions.Builder builder = DashScopeImageOptions.builder().from(toOptions());
		customizer.accept(builder);
		this.options = builder.build();
	}

    public @Nullable String getModel() {
        return toOptions().getModel();
    }

    public void setModel(String model) {
        updateOptions(builder -> builder.model(model));
    }

    public @Nullable Integer getN() {
        return toOptions().getN();
    }

    public void setN(Integer n) {
        updateOptions(builder -> builder.n(n));
    }

    public @Nullable Integer getWidth() {
        return toOptions().getWidth();
    }

    public void setWidth(Integer width) {
        updateOptions(builder -> builder.width(width));
    }

    public @Nullable Integer getHeight() {
        return toOptions().getHeight();
    }

    public void setHeight(Integer height) {
        updateOptions(builder -> builder.height(height));
    }

    public @Nullable String getSize() {
        return toOptions().getSize();
    }

    public void setSize(String size) {
        updateOptions(builder -> builder.size(size));
    }

    public @Nullable String getStyle() {
        return toOptions().getStyle();
    }

    public void setStyle(String style) {
        updateOptions(builder -> builder.style(style));
    }

    public @Nullable Integer getStyleIndex() {
        return toOptions().getStyleIndex();
    }

    public void setStyleIndex(Integer styleIndex) {
        updateOptions(builder -> builder.styleIndex(styleIndex));
    }

    public @Nullable String getStyleRefUrl() {
        return toOptions().getStyleRefUrl();
    }

    public void setStyleRefUrl(String styleRefUrl) {
        updateOptions(builder -> builder.styleRefUrl(styleRefUrl));
    }

    public @Nullable String getBaseImageUrl() {
        return toOptions().getBaseImageUrl();
    }

    public void setBaseImageUrl(String baseImageUrl) {
        updateOptions(builder -> builder.baseImageUrl(baseImageUrl));
    }

    public @Nullable List<String> getImages() {
        return toOptions().getImages();
    }

    public void setImages(List<String> images) {
        updateOptions(builder -> builder.images(images));
    }

    public @Nullable String getMaskImageUrl() {
        return toOptions().getMaskImageUrl();
    }

    public void setMaskImageUrl(String maskImageUrl) {
        updateOptions(builder -> builder.maskImageUrl(maskImageUrl));
    }

    public @Nullable String getSketchImageUrl() {
        return toOptions().getSketchImageUrl();
    }

    public void setSketchImageUrl(String sketchImageUrl) {
        updateOptions(builder -> builder.sketchImageUrl(sketchImageUrl));
    }

    public @Nullable String getTemplateImageUrl() {
        return toOptions().getTemplateImageUrl();
    }

    public void setTemplateImageUrl(String templateImageUrl) {
        updateOptions(builder -> builder.templateImageUrl(templateImageUrl));
    }

    public @Nullable List<String> getShoeImageUrl() {
        return toOptions().getShoeImageUrl();
    }

    public void setShoeImageUrl(List<String> shoeImageUrl) {
        updateOptions(builder -> builder.shoeImageUrl(shoeImageUrl));
    }

    public @Nullable String getFaceImageUrl() {
        return toOptions().getFaceImageUrl();
    }

    public void setFaceImageUrl(String faceImageUrl) {
        updateOptions(builder -> builder.faceImageUrl(faceImageUrl));
    }

    public @Nullable String getBackgroundImageUrl() {
        return toOptions().getBackgroundImageUrl();
    }

    public void setBackgroundImageUrl(String backgroundImageUrl) {
        updateOptions(builder -> builder.backgroundImageUrl(backgroundImageUrl));
    }

    public @Nullable String getForegroundUrl() {
        return toOptions().getForegroundUrl();
    }

    public void setForegroundUrl(String foregroundUrl) {
        updateOptions(builder -> builder.foregroundUrl(foregroundUrl));
    }

    public @Nullable String getPersonImageUrl() {
        return toOptions().getPersonImageUrl();
    }

    public void setPersonImageUrl(String personImageUrl) {
        updateOptions(builder -> builder.personImageUrl(personImageUrl));
    }

    public @Nullable String getTopGarmentUrl() {
        return toOptions().getTopGarmentUrl();
    }

    public void setTopGarmentUrl(String topGarmentUrl) {
        updateOptions(builder -> builder.topGarmentUrl(topGarmentUrl));
    }

    public @Nullable String getBottomGarmentUrl() {
        return toOptions().getBottomGarmentUrl();
    }

    public void setBottomGarmentUrl(String bottomGarmentUrl) {
        updateOptions(builder -> builder.bottomGarmentUrl(bottomGarmentUrl));
    }

    public @Nullable String getCoarseImageUrl() {
        return toOptions().getCoarseImageUrl();
    }

    public void setCoarseImageUrl(String coarseImageUrl) {
        updateOptions(builder -> builder.coarseImageUrl(coarseImageUrl));
    }

    public @Nullable List<String> getUserUrls() {
        return toOptions().getUserUrls();
    }

    public void setUserUrls(List<String> userUrls) {
        updateOptions(builder -> builder.userUrls(userUrls));
    }

    public @Nullable String getRefImg() {
        return toOptions().getRefImg();
    }

    public void setRefImg(String refImg) {
        updateOptions(builder -> builder.refImg(refImg));
    }

    public @Nullable String getPredefinedFaceId() {
        return toOptions().getPredefinedFaceId();
    }

    public void setPredefinedFaceId(String predefinedFaceId) {
        updateOptions(builder -> builder.predefinedFaceId(predefinedFaceId));
    }

    public @Nullable String getFacePrompt() {
        return toOptions().getFacePrompt();
    }

    public void setFacePrompt(String facePrompt) {
        updateOptions(builder -> builder.facePrompt(facePrompt));
    }

    public @Nullable Float getBgstyleScale() {
        return toOptions().getBgstyleScale();
    }

    public void setBgstyleScale(Float bgstyleScale) {
        updateOptions(builder -> builder.bgstyleScale(bgstyleScale));
    }

    public @Nullable Boolean getRealPerson() {
        return toOptions().getRealPerson();
    }

    public void setRealPerson(Boolean realPerson) {
        updateOptions(builder -> builder.realPerson(realPerson));
    }

    public @Nullable Integer getSeed() {
        return toOptions().getSeed();
    }

    public void setSeed(Integer seed) {
        updateOptions(builder -> builder.seed(seed));
    }

    public @Nullable Float getRefStrength() {
        return toOptions().getRefStrength();
    }

    public void setRefStrength(Float refStrength) {
        updateOptions(builder -> builder.refStrength(refStrength));
    }

    public @Nullable String getResponseFormat() {
        return toOptions().getResponseFormat();
    }

    public void setResponseFormat(String responseFormat) {
        updateOptions(builder -> builder.responseFormat(responseFormat));
    }

    public @Nullable String getRefMode() {
        return toOptions().getRefMode();
    }

    public void setRefMode(String refMode) {
        updateOptions(builder -> builder.refMode(refMode));
    }

    public @Nullable String getNegativePrompt() {
        return toOptions().getNegativePrompt();
    }

    public void setNegativePrompt(String negativePrompt) {
        updateOptions(builder -> builder.negativePrompt(negativePrompt));
    }

    public @Nullable String getText() {
        return toOptions().getText();
    }

    public void setText(String text) {
        updateOptions(builder -> builder.text(text));
    }

    public @Nullable Boolean getPromptExtend() {
        return toOptions().getPromptExtend();
    }

    public void setPromptExtend(Boolean promptExtend) {
        updateOptions(builder -> builder.promptExtend(promptExtend));
    }

    public @Nullable Boolean getWatermark() {
        return toOptions().getWatermark();
    }

    public void setWatermark(Boolean watermark) {
        updateOptions(builder -> builder.watermark(watermark));
    }

    public @Nullable String getFunction() {
        return toOptions().getFunction();
    }

    public void setFunction(String function) {
        updateOptions(builder -> builder.function(function));
    }

    public @Nullable Integer getSketchWeight() {
        return toOptions().getSketchWeight();
    }

    public void setSketchWeight(Integer sketchWeight) {
        updateOptions(builder -> builder.sketchWeight(sketchWeight));
    }

    public @Nullable Boolean getSketchExtraction() {
        return toOptions().getSketchExtraction();
    }

    public void setSketchExtraction(Boolean sketchExtraction) {
        updateOptions(builder -> builder.sketchExtraction(sketchExtraction));
    }

    public @Nullable Integer @Nullable [][] getSketchColor() {
        return toOptions().getSketchColor();
    }

    public void setSketchColor(@Nullable Integer @Nullable [][] sketchColor) {
        updateOptions(builder -> builder.sketchColor(sketchColor));
    }

    public @Nullable Integer @Nullable [][] getMaskColor() {
        return toOptions().getMaskColor();
    }

    public void setMaskColor(@Nullable Integer @Nullable [][] maskColor) {
        updateOptions(builder -> builder.maskColor(maskColor));
    }

    public @Nullable Integer @Nullable [][][] getBboxList() {
        return toOptions().getBboxList();
    }

    public void setBboxList(@Nullable Integer @Nullable [][][] bboxList) {
        updateOptions(builder -> builder.bboxList(bboxList));
    }

    public @Nullable Integer getMaxImages() {
        return toOptions().getMaxImages();
    }

    public void setMaxImages(Integer maxImages) {
        updateOptions(builder -> builder.maxImages(maxImages));
    }

    public @Nullable Boolean getEnableInterleave() {
        return toOptions().getEnableInterleave();
    }

    public void setEnableInterleave(Boolean enableInterleave) {
        updateOptions(builder -> builder.enableInterleave(enableInterleave));
    }

    public @Nullable Boolean getEnableSequential() {
        return toOptions().getEnableSequential();
    }

    public void setEnableSequential(Boolean enableSequential) {
        updateOptions(builder -> builder.enableSequential(enableSequential));
    }

    public @Nullable List<ColorPaletteItem> getColorPalette() {
        return toOptions().getColorPalette();
    }

    public void setColorPalette(List<ColorPaletteItem> colorPalette) {
        updateOptions(builder -> builder.colorPalette(colorPalette));
    }

    public @Nullable Boolean getThinkingMode() {
        return toOptions().getThinkingMode();
    }

    public void setThinkingMode(Boolean thinkingMode) {
        updateOptions(builder -> builder.thinkingMode(thinkingMode));
    }

    public @Nullable String getOutputRatio() {
        return toOptions().getOutputRatio();
    }

    public void setOutputRatio(String outputRatio) {
        updateOptions(builder -> builder.outputRatio(outputRatio));
    }

    public @Nullable Float getXScale() {
        return toOptions().getXScale();
    }

    public void setXScale(Float xScale) {
        updateOptions(builder -> builder.xScale(xScale));
    }

    public @Nullable Float getYScale() {
        return toOptions().getYScale();
    }

    public void setYScale(Float yScale) {
        updateOptions(builder -> builder.yScale(yScale));
    }

    public @Nullable Integer getAngle() {
        return toOptions().getAngle();
    }

    public void setAngle(Integer angle) {
        updateOptions(builder -> builder.angle(angle));
    }

    public @Nullable Integer getLeftOffset() {
        return toOptions().getLeftOffset();
    }

    public void setLeftOffset(Integer leftOffset) {
        updateOptions(builder -> builder.leftOffset(leftOffset));
    }

    public @Nullable Integer getRightOffset() {
        return toOptions().getRightOffset();
    }

    public void setRightOffset(Integer rightOffset) {
        updateOptions(builder -> builder.rightOffset(rightOffset));
    }

    public @Nullable Integer getTopOffset() {
        return toOptions().getTopOffset();
    }

    public void setTopOffset(Integer topOffset) {
        updateOptions(builder -> builder.topOffset(topOffset));
    }

    public @Nullable Integer getBottomOffset() {
        return toOptions().getBottomOffset();
    }

    public void setBottomOffset(Integer bottomOffset) {
        updateOptions(builder -> builder.bottomOffset(bottomOffset));
    }

    public @Nullable Boolean getBestQuality() {
        return toOptions().getBestQuality();
    }

    public void setBestQuality(Boolean bestQuality) {
        updateOptions(builder -> builder.bestQuality(bestQuality));
    }

    public @Nullable Boolean getLimitImageSize() {
        return toOptions().getLimitImageSize();
    }

    public void setLimitImageSize(Boolean limitImageSize) {
        updateOptions(builder -> builder.limitImageSize(limitImageSize));
    }

    public @Nullable String getSourceLang() {
        return toOptions().getSourceLang();
    }

    public void setSourceLang(String sourceLang) {
        updateOptions(builder -> builder.sourceLang(sourceLang));
    }

    public @Nullable String getTargetLang() {
        return toOptions().getTargetLang();
    }

    public void setTargetLang(String targetLang) {
        updateOptions(builder -> builder.targetLang(targetLang));
    }

    public @Nullable Object getExt() {
        return toOptions().getExt();
    }

    public void setExt(Object ext) {
        updateOptions(builder -> builder.ext(ext));
    }

    public @Nullable List<Element> getElementList() {
        return toOptions().getElementList();
    }

    public void setElementList(List<Element> elementList) {
        updateOptions(builder -> builder.elementList(elementList));
    }

    public @Nullable String getResultType() {
        return toOptions().getResultType();
    }

    public void setResultType(String resultType) {
        updateOptions(builder -> builder.resultType(resultType));
    }

    public @Nullable Integer getSeriesAmount() {
        return toOptions().getSeriesAmount();
    }

    public void setSeriesAmount(Integer seriesAmount) {
        updateOptions(builder -> builder.seriesAmount(seriesAmount));
    }

    public @Nullable String getAspectRatio() {
        return toOptions().getAspectRatio();
    }

    public void setAspectRatio(String aspectRatio) {
        updateOptions(builder -> builder.aspectRatio(aspectRatio));
    }

    public @Nullable String getResolution() {
        return toOptions().getResolution();
    }

    public void setResolution(String resolution) {
        updateOptions(builder -> builder.resolution(resolution));
    }

    public @Nullable String getShortSideSize() {
        return toOptions().getShortSideSize();
    }

    public void setShortSideSize(String shortSideSize) {
        updateOptions(builder -> builder.shortSideSize(shortSideSize));
    }

    public @Nullable Float getScale() {
        return toOptions().getScale();
    }

    public void setScale(Float scale) {
        updateOptions(builder -> builder.scale(scale));
    }

    public @Nullable String getModelVersion() {
        return toOptions().getModelVersion();
    }

    public void setModelVersion(String modelVersion) {
        updateOptions(builder -> builder.modelVersion(modelVersion));
    }

    public @Nullable Integer getNoiseLevel() {
        return toOptions().getNoiseLevel();
    }

    public void setNoiseLevel(Integer noiseLevel) {
        updateOptions(builder -> builder.noiseLevel(noiseLevel));
    }

    public @Nullable Float getRefPromptWeight() {
        return toOptions().getRefPromptWeight();
    }

    public void setRefPromptWeight(Float refPromptWeight) {
        updateOptions(builder -> builder.refPromptWeight(refPromptWeight));
    }

    public @Nullable ReferenceEdge getReferenceEdge() {
        return toOptions().getReferenceEdge();
    }

    public void setReferenceEdge(ReferenceEdge referenceEdge) {
        updateOptions(builder -> builder.referenceEdge(referenceEdge));
    }

    public @Nullable String getGenerateMode() {
        return toOptions().getGenerateMode();
    }

    public void setGenerateMode(String generateMode) {
        updateOptions(builder -> builder.generateMode(generateMode));
    }

    public @Nullable String getAuxiliaryParameters() {
        return toOptions().getAuxiliaryParameters();
    }

    public void setAuxiliaryParameters(String auxiliaryParameters) {
        updateOptions(builder -> builder.auxiliaryParameters(auxiliaryParameters));
    }

    public @Nullable String getTitle() {
        return toOptions().getTitle();
    }

    public void setTitle(String title) {
        updateOptions(builder -> builder.title(title));
    }

    public @Nullable String getSubTitle() {
        return toOptions().getSubTitle();
    }

    public void setSubTitle(String subTitle) {
        updateOptions(builder -> builder.subTitle(subTitle));
    }

    public @Nullable String getBodyText() {
        return toOptions().getBodyText();
    }

    public void setBodyText(String bodyText) {
        updateOptions(builder -> builder.bodyText(bodyText));
    }

    public @Nullable String getPromptTextZh() {
        return toOptions().getPromptTextZh();
    }

    public void setPromptTextZh(String promptTextZh) {
        updateOptions(builder -> builder.promptTextZh(promptTextZh));
    }

    public @Nullable String getPromptTextEn() {
        return toOptions().getPromptTextEn();
    }

    public void setPromptTextEn(String promptTextEn) {
        updateOptions(builder -> builder.promptTextEn(promptTextEn));
    }

    public @Nullable String getWhRatios() {
        return toOptions().getWhRatios();
    }

    public void setWhRatios(String whRatios) {
        updateOptions(builder -> builder.whRatios(whRatios));
    }

    public @Nullable String getLoraName() {
        return toOptions().getLoraName();
    }

    public void setLoraName(String loraName) {
        updateOptions(builder -> builder.loraName(loraName));
    }

    public @Nullable Float getLoraWeight() {
        return toOptions().getLoraWeight();
    }

    public void setLoraWeight(Float loraWeight) {
        updateOptions(builder -> builder.loraWeight(loraWeight));
    }

    public @Nullable Float getCtrlRatio() {
        return toOptions().getCtrlRatio();
    }

    public void setCtrlRatio(Float ctrlRatio) {
        updateOptions(builder -> builder.ctrlRatio(ctrlRatio));
    }

    public @Nullable Float getCtrlStep() {
        return toOptions().getCtrlStep();
    }

    public void setCtrlStep(Float ctrlStep) {
        updateOptions(builder -> builder.ctrlStep(ctrlStep));
    }

    public @Nullable Boolean getCreativeTitleLayout() {
        return toOptions().getCreativeTitleLayout();
    }

    public void setCreativeTitleLayout(Boolean creativeTitleLayout) {
        updateOptions(builder -> builder.creativeTitleLayout(creativeTitleLayout));
    }

    public @Nullable Boolean getFastMode() {
        return toOptions().getFastMode();
    }

    public void setFastMode(Boolean fastMode) {
        updateOptions(builder -> builder.fastMode(fastMode));
    }

    public @Nullable Boolean getDilateFlag() {
        return toOptions().getDilateFlag();
    }

    public void setDilateFlag(Boolean dilateFlag) {
        updateOptions(builder -> builder.dilateFlag(dilateFlag));
    }

    public @Nullable Boolean getRestoreFace() {
        return toOptions().getRestoreFace();
    }

    public void setRestoreFace(Boolean restoreFace) {
        updateOptions(builder -> builder.restoreFace(restoreFace));
    }

    public @Nullable String getGender() {
        return toOptions().getGender();
    }

    public void setGender(String gender) {
        updateOptions(builder -> builder.gender(gender));
    }

    public @Nullable List<String> getClothesType() {
        return toOptions().getClothesType();
    }

    public void setClothesType(List<String> clothesType) {
        updateOptions(builder -> builder.clothesType(clothesType));
    }

    public @Nullable List<Resource> getResources() {
        return toOptions().getResources();
    }

    public void setResources(List<Resource> resources) {
        updateOptions(builder -> builder.resources(resources));
    }

    public @Nullable Boolean getSkinRetouch() {
        return toOptions().getSkinRetouch();
    }

    public void setSkinRetouch(Boolean skinRetouch) {
        updateOptions(builder -> builder.skinRetouch(skinRetouch));
    }

    public @Nullable Integer getSteps() {
        return toOptions().getSteps();
    }

    public void setSteps(Integer steps) {
        updateOptions(builder -> builder.steps(steps));
    }

    public @Nullable String getFontName() {
        return toOptions().getFontName();
    }

    public void setFontName(String fontName) {
        updateOptions(builder -> builder.fontName(fontName));
    }

    public @Nullable String getTtfUrl() {
        return toOptions().getTtfUrl();
    }

    public void setTtfUrl(String ttfUrl) {
        updateOptions(builder -> builder.ttfUrl(ttfUrl));
    }

    public @Nullable Integer getImageShortSize() {
        return toOptions().getImageShortSize();
    }

    public void setImageShortSize(Integer imageShortSize) {
        updateOptions(builder -> builder.imageShortSize(imageShortSize));
    }

    public @Nullable Boolean getAlphaChannel() {
        return toOptions().getAlphaChannel();
    }

    public void setAlphaChannel(Boolean alphaChannel) {
        updateOptions(builder -> builder.alphaChannel(alphaChannel));
    }

    public @Nullable List<String> getTrainingFileIds() {
        return toOptions().getTrainingFileIds();
    }

    public void setTrainingFileIds(List<String> trainingFileIds) {
        updateOptions(builder -> builder.trainingFileIds(trainingFileIds));
    }

    public @Nullable InvokeMode getInvokeMode() {
        return toOptions().getInvokeMode();
    }

    public void setInvokeMode(InvokeMode invokeMode) {
        updateOptions(builder -> builder.invokeMode(invokeMode));
    }

    public @Nullable RequestType getRequestType() {
        return toOptions().getRequestType();
    }

    public void setRequestType(RequestType requestType) {
        updateOptions(builder -> builder.requestType(requestType));
    }
	public class Options {

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".model")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable String getModel() {
			return DashScopeImageProperties.this.getModel();
		}

		public void setModel(String model) {
			DashScopeImageProperties.this.setModel(model);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".n")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Integer getN() {
			return DashScopeImageProperties.this.getN();
		}

		public void setN(Integer n) {
			DashScopeImageProperties.this.setN(n);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".width")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Integer getWidth() {
			return DashScopeImageProperties.this.getWidth();
		}

		public void setWidth(Integer width) {
			DashScopeImageProperties.this.setWidth(width);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".height")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Integer getHeight() {
			return DashScopeImageProperties.this.getHeight();
		}

		public void setHeight(Integer height) {
			DashScopeImageProperties.this.setHeight(height);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".size")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable String getSize() {
			return DashScopeImageProperties.this.getSize();
		}

		public void setSize(String size) {
			DashScopeImageProperties.this.setSize(size);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".style")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable String getStyle() {
			return DashScopeImageProperties.this.getStyle();
		}

		public void setStyle(String style) {
			DashScopeImageProperties.this.setStyle(style);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".style-index")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Integer getStyleIndex() {
			return DashScopeImageProperties.this.getStyleIndex();
		}

		public void setStyleIndex(Integer styleIndex) {
			DashScopeImageProperties.this.setStyleIndex(styleIndex);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".style-ref-url")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable String getStyleRefUrl() {
			return DashScopeImageProperties.this.getStyleRefUrl();
		}

		public void setStyleRefUrl(String styleRefUrl) {
			DashScopeImageProperties.this.setStyleRefUrl(styleRefUrl);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".base-image-url")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable String getBaseImageUrl() {
			return DashScopeImageProperties.this.getBaseImageUrl();
		}

		public void setBaseImageUrl(String baseImageUrl) {
			DashScopeImageProperties.this.setBaseImageUrl(baseImageUrl);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".images")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable List<String> getImages() {
			return DashScopeImageProperties.this.getImages();
		}

		public void setImages(List<String> images) {
			DashScopeImageProperties.this.setImages(images);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".mask-image-url")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable String getMaskImageUrl() {
			return DashScopeImageProperties.this.getMaskImageUrl();
		}

		public void setMaskImageUrl(String maskImageUrl) {
			DashScopeImageProperties.this.setMaskImageUrl(maskImageUrl);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".sketch-image-url")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable String getSketchImageUrl() {
			return DashScopeImageProperties.this.getSketchImageUrl();
		}

		public void setSketchImageUrl(String sketchImageUrl) {
			DashScopeImageProperties.this.setSketchImageUrl(sketchImageUrl);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".template-image-url")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable String getTemplateImageUrl() {
			return DashScopeImageProperties.this.getTemplateImageUrl();
		}

		public void setTemplateImageUrl(String templateImageUrl) {
			DashScopeImageProperties.this.setTemplateImageUrl(templateImageUrl);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".shoe-image-url")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable List<String> getShoeImageUrl() {
			return DashScopeImageProperties.this.getShoeImageUrl();
		}

		public void setShoeImageUrl(List<String> shoeImageUrl) {
			DashScopeImageProperties.this.setShoeImageUrl(shoeImageUrl);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".face-image-url")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable String getFaceImageUrl() {
			return DashScopeImageProperties.this.getFaceImageUrl();
		}

		public void setFaceImageUrl(String faceImageUrl) {
			DashScopeImageProperties.this.setFaceImageUrl(faceImageUrl);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".background-image-url")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable String getBackgroundImageUrl() {
			return DashScopeImageProperties.this.getBackgroundImageUrl();
		}

		public void setBackgroundImageUrl(String backgroundImageUrl) {
			DashScopeImageProperties.this.setBackgroundImageUrl(backgroundImageUrl);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".foreground-url")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable String getForegroundUrl() {
			return DashScopeImageProperties.this.getForegroundUrl();
		}

		public void setForegroundUrl(String foregroundUrl) {
			DashScopeImageProperties.this.setForegroundUrl(foregroundUrl);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".person-image-url")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable String getPersonImageUrl() {
			return DashScopeImageProperties.this.getPersonImageUrl();
		}

		public void setPersonImageUrl(String personImageUrl) {
			DashScopeImageProperties.this.setPersonImageUrl(personImageUrl);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".top-garment-url")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable String getTopGarmentUrl() {
			return DashScopeImageProperties.this.getTopGarmentUrl();
		}

		public void setTopGarmentUrl(String topGarmentUrl) {
			DashScopeImageProperties.this.setTopGarmentUrl(topGarmentUrl);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".bottom-garment-url")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable String getBottomGarmentUrl() {
			return DashScopeImageProperties.this.getBottomGarmentUrl();
		}

		public void setBottomGarmentUrl(String bottomGarmentUrl) {
			DashScopeImageProperties.this.setBottomGarmentUrl(bottomGarmentUrl);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".coarse-image-url")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable String getCoarseImageUrl() {
			return DashScopeImageProperties.this.getCoarseImageUrl();
		}

		public void setCoarseImageUrl(String coarseImageUrl) {
			DashScopeImageProperties.this.setCoarseImageUrl(coarseImageUrl);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".user-urls")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable List<String> getUserUrls() {
			return DashScopeImageProperties.this.getUserUrls();
		}

		public void setUserUrls(List<String> userUrls) {
			DashScopeImageProperties.this.setUserUrls(userUrls);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".ref-img")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable String getRefImg() {
			return DashScopeImageProperties.this.getRefImg();
		}

		public void setRefImg(String refImg) {
			DashScopeImageProperties.this.setRefImg(refImg);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".predefined-face-id")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable String getPredefinedFaceId() {
			return DashScopeImageProperties.this.getPredefinedFaceId();
		}

		public void setPredefinedFaceId(String predefinedFaceId) {
			DashScopeImageProperties.this.setPredefinedFaceId(predefinedFaceId);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".face-prompt")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable String getFacePrompt() {
			return DashScopeImageProperties.this.getFacePrompt();
		}

		public void setFacePrompt(String facePrompt) {
			DashScopeImageProperties.this.setFacePrompt(facePrompt);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".bgstyle-scale")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Float getBgstyleScale() {
			return DashScopeImageProperties.this.getBgstyleScale();
		}

		public void setBgstyleScale(Float bgstyleScale) {
			DashScopeImageProperties.this.setBgstyleScale(bgstyleScale);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".real-person")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Boolean getRealPerson() {
			return DashScopeImageProperties.this.getRealPerson();
		}

		public void setRealPerson(Boolean realPerson) {
			DashScopeImageProperties.this.setRealPerson(realPerson);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".seed")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Integer getSeed() {
			return DashScopeImageProperties.this.getSeed();
		}

		public void setSeed(Integer seed) {
			DashScopeImageProperties.this.setSeed(seed);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".ref-strength")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Float getRefStrength() {
			return DashScopeImageProperties.this.getRefStrength();
		}

		public void setRefStrength(Float refStrength) {
			DashScopeImageProperties.this.setRefStrength(refStrength);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".response-format")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable String getResponseFormat() {
			return DashScopeImageProperties.this.getResponseFormat();
		}

		public void setResponseFormat(String responseFormat) {
			DashScopeImageProperties.this.setResponseFormat(responseFormat);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".ref-mode")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable String getRefMode() {
			return DashScopeImageProperties.this.getRefMode();
		}

		public void setRefMode(String refMode) {
			DashScopeImageProperties.this.setRefMode(refMode);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".negative-prompt")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable String getNegativePrompt() {
			return DashScopeImageProperties.this.getNegativePrompt();
		}

		public void setNegativePrompt(String negativePrompt) {
			DashScopeImageProperties.this.setNegativePrompt(negativePrompt);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".text")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable String getText() {
			return DashScopeImageProperties.this.getText();
		}

		public void setText(String text) {
			DashScopeImageProperties.this.setText(text);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".prompt-extend")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Boolean getPromptExtend() {
			return DashScopeImageProperties.this.getPromptExtend();
		}

		public void setPromptExtend(Boolean promptExtend) {
			DashScopeImageProperties.this.setPromptExtend(promptExtend);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".watermark")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Boolean getWatermark() {
			return DashScopeImageProperties.this.getWatermark();
		}

		public void setWatermark(Boolean watermark) {
			DashScopeImageProperties.this.setWatermark(watermark);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".function")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable String getFunction() {
			return DashScopeImageProperties.this.getFunction();
		}

		public void setFunction(String function) {
			DashScopeImageProperties.this.setFunction(function);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".sketch-weight")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Integer getSketchWeight() {
			return DashScopeImageProperties.this.getSketchWeight();
		}

		public void setSketchWeight(Integer sketchWeight) {
			DashScopeImageProperties.this.setSketchWeight(sketchWeight);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".sketch-extraction")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Boolean getSketchExtraction() {
			return DashScopeImageProperties.this.getSketchExtraction();
		}

		public void setSketchExtraction(Boolean sketchExtraction) {
			DashScopeImageProperties.this.setSketchExtraction(sketchExtraction);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".sketch-color")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Integer @Nullable [][] getSketchColor() {
			return DashScopeImageProperties.this.getSketchColor();
		}

		public void setSketchColor(@Nullable Integer @Nullable [][] sketchColor) {
			DashScopeImageProperties.this.setSketchColor(sketchColor);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".mask-color")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Integer @Nullable [][] getMaskColor() {
			return DashScopeImageProperties.this.getMaskColor();
		}

		public void setMaskColor(@Nullable Integer @Nullable [][] maskColor) {
			DashScopeImageProperties.this.setMaskColor(maskColor);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".bbox-list")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Integer @Nullable [][][] getBboxList() {
			return DashScopeImageProperties.this.getBboxList();
		}

		public void setBboxList(@Nullable Integer @Nullable [][][] bboxList) {
			DashScopeImageProperties.this.setBboxList(bboxList);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".max-images")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Integer getMaxImages() {
			return DashScopeImageProperties.this.getMaxImages();
		}

		public void setMaxImages(Integer maxImages) {
			DashScopeImageProperties.this.setMaxImages(maxImages);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".enable-interleave")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Boolean getEnableInterleave() {
			return DashScopeImageProperties.this.getEnableInterleave();
		}

		public void setEnableInterleave(Boolean enableInterleave) {
			DashScopeImageProperties.this.setEnableInterleave(enableInterleave);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".enable-sequential")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Boolean getEnableSequential() {
			return DashScopeImageProperties.this.getEnableSequential();
		}

		public void setEnableSequential(Boolean enableSequential) {
			DashScopeImageProperties.this.setEnableSequential(enableSequential);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".color-palette")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable List<ColorPaletteItem> getColorPalette() {
			return DashScopeImageProperties.this.getColorPalette();
		}

		public void setColorPalette(List<ColorPaletteItem> colorPalette) {
			DashScopeImageProperties.this.setColorPalette(colorPalette);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".thinking-mode")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Boolean getThinkingMode() {
			return DashScopeImageProperties.this.getThinkingMode();
		}

		public void setThinkingMode(Boolean thinkingMode) {
			DashScopeImageProperties.this.setThinkingMode(thinkingMode);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".output-ratio")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable String getOutputRatio() {
			return DashScopeImageProperties.this.getOutputRatio();
		}

		public void setOutputRatio(String outputRatio) {
			DashScopeImageProperties.this.setOutputRatio(outputRatio);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".x-scale")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Float getXScale() {
			return DashScopeImageProperties.this.getXScale();
		}

		public void setXScale(Float xScale) {
			DashScopeImageProperties.this.setXScale(xScale);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".y-scale")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Float getYScale() {
			return DashScopeImageProperties.this.getYScale();
		}

		public void setYScale(Float yScale) {
			DashScopeImageProperties.this.setYScale(yScale);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".angle")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Integer getAngle() {
			return DashScopeImageProperties.this.getAngle();
		}

		public void setAngle(Integer angle) {
			DashScopeImageProperties.this.setAngle(angle);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".left-offset")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Integer getLeftOffset() {
			return DashScopeImageProperties.this.getLeftOffset();
		}

		public void setLeftOffset(Integer leftOffset) {
			DashScopeImageProperties.this.setLeftOffset(leftOffset);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".right-offset")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Integer getRightOffset() {
			return DashScopeImageProperties.this.getRightOffset();
		}

		public void setRightOffset(Integer rightOffset) {
			DashScopeImageProperties.this.setRightOffset(rightOffset);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".top-offset")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Integer getTopOffset() {
			return DashScopeImageProperties.this.getTopOffset();
		}

		public void setTopOffset(Integer topOffset) {
			DashScopeImageProperties.this.setTopOffset(topOffset);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".bottom-offset")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Integer getBottomOffset() {
			return DashScopeImageProperties.this.getBottomOffset();
		}

		public void setBottomOffset(Integer bottomOffset) {
			DashScopeImageProperties.this.setBottomOffset(bottomOffset);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".best-quality")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Boolean getBestQuality() {
			return DashScopeImageProperties.this.getBestQuality();
		}

		public void setBestQuality(Boolean bestQuality) {
			DashScopeImageProperties.this.setBestQuality(bestQuality);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".limit-image-size")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Boolean getLimitImageSize() {
			return DashScopeImageProperties.this.getLimitImageSize();
		}

		public void setLimitImageSize(Boolean limitImageSize) {
			DashScopeImageProperties.this.setLimitImageSize(limitImageSize);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".source-lang")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable String getSourceLang() {
			return DashScopeImageProperties.this.getSourceLang();
		}

		public void setSourceLang(String sourceLang) {
			DashScopeImageProperties.this.setSourceLang(sourceLang);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".target-lang")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable String getTargetLang() {
			return DashScopeImageProperties.this.getTargetLang();
		}

		public void setTargetLang(String targetLang) {
			DashScopeImageProperties.this.setTargetLang(targetLang);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".ext")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Object getExt() {
			return DashScopeImageProperties.this.getExt();
		}

		public void setExt(Object ext) {
			DashScopeImageProperties.this.setExt(ext);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".element-list")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable List<Element> getElementList() {
			return DashScopeImageProperties.this.getElementList();
		}

		public void setElementList(List<Element> elementList) {
			DashScopeImageProperties.this.setElementList(elementList);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".result-type")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable String getResultType() {
			return DashScopeImageProperties.this.getResultType();
		}

		public void setResultType(String resultType) {
			DashScopeImageProperties.this.setResultType(resultType);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".series-amount")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Integer getSeriesAmount() {
			return DashScopeImageProperties.this.getSeriesAmount();
		}

		public void setSeriesAmount(Integer seriesAmount) {
			DashScopeImageProperties.this.setSeriesAmount(seriesAmount);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".aspect-ratio")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable String getAspectRatio() {
			return DashScopeImageProperties.this.getAspectRatio();
		}

		public void setAspectRatio(String aspectRatio) {
			DashScopeImageProperties.this.setAspectRatio(aspectRatio);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".resolution")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable String getResolution() {
			return DashScopeImageProperties.this.getResolution();
		}

		public void setResolution(String resolution) {
			DashScopeImageProperties.this.setResolution(resolution);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".short-side-size")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable String getShortSideSize() {
			return DashScopeImageProperties.this.getShortSideSize();
		}

		public void setShortSideSize(String shortSideSize) {
			DashScopeImageProperties.this.setShortSideSize(shortSideSize);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".scale")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Float getScale() {
			return DashScopeImageProperties.this.getScale();
		}

		public void setScale(Float scale) {
			DashScopeImageProperties.this.setScale(scale);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".model-version")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable String getModelVersion() {
			return DashScopeImageProperties.this.getModelVersion();
		}

		public void setModelVersion(String modelVersion) {
			DashScopeImageProperties.this.setModelVersion(modelVersion);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".noise-level")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Integer getNoiseLevel() {
			return DashScopeImageProperties.this.getNoiseLevel();
		}

		public void setNoiseLevel(Integer noiseLevel) {
			DashScopeImageProperties.this.setNoiseLevel(noiseLevel);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".ref-prompt-weight")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Float getRefPromptWeight() {
			return DashScopeImageProperties.this.getRefPromptWeight();
		}

		public void setRefPromptWeight(Float refPromptWeight) {
			DashScopeImageProperties.this.setRefPromptWeight(refPromptWeight);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".reference-edge")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable ReferenceEdge getReferenceEdge() {
			return DashScopeImageProperties.this.getReferenceEdge();
		}

		public void setReferenceEdge(ReferenceEdge referenceEdge) {
			DashScopeImageProperties.this.setReferenceEdge(referenceEdge);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".generate-mode")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable String getGenerateMode() {
			return DashScopeImageProperties.this.getGenerateMode();
		}

		public void setGenerateMode(String generateMode) {
			DashScopeImageProperties.this.setGenerateMode(generateMode);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".auxiliary-parameters")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable String getAuxiliaryParameters() {
			return DashScopeImageProperties.this.getAuxiliaryParameters();
		}

		public void setAuxiliaryParameters(String auxiliaryParameters) {
			DashScopeImageProperties.this.setAuxiliaryParameters(auxiliaryParameters);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".title")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable String getTitle() {
			return DashScopeImageProperties.this.getTitle();
		}

		public void setTitle(String title) {
			DashScopeImageProperties.this.setTitle(title);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".sub-title")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable String getSubTitle() {
			return DashScopeImageProperties.this.getSubTitle();
		}

		public void setSubTitle(String subTitle) {
			DashScopeImageProperties.this.setSubTitle(subTitle);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".body-text")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable String getBodyText() {
			return DashScopeImageProperties.this.getBodyText();
		}

		public void setBodyText(String bodyText) {
			DashScopeImageProperties.this.setBodyText(bodyText);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".prompt-text-zh")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable String getPromptTextZh() {
			return DashScopeImageProperties.this.getPromptTextZh();
		}

		public void setPromptTextZh(String promptTextZh) {
			DashScopeImageProperties.this.setPromptTextZh(promptTextZh);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".prompt-text-en")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable String getPromptTextEn() {
			return DashScopeImageProperties.this.getPromptTextEn();
		}

		public void setPromptTextEn(String promptTextEn) {
			DashScopeImageProperties.this.setPromptTextEn(promptTextEn);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".wh-ratios")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable String getWhRatios() {
			return DashScopeImageProperties.this.getWhRatios();
		}

		public void setWhRatios(String whRatios) {
			DashScopeImageProperties.this.setWhRatios(whRatios);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".lora-name")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable String getLoraName() {
			return DashScopeImageProperties.this.getLoraName();
		}

		public void setLoraName(String loraName) {
			DashScopeImageProperties.this.setLoraName(loraName);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".lora-weight")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Float getLoraWeight() {
			return DashScopeImageProperties.this.getLoraWeight();
		}

		public void setLoraWeight(Float loraWeight) {
			DashScopeImageProperties.this.setLoraWeight(loraWeight);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".ctrl-ratio")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Float getCtrlRatio() {
			return DashScopeImageProperties.this.getCtrlRatio();
		}

		public void setCtrlRatio(Float ctrlRatio) {
			DashScopeImageProperties.this.setCtrlRatio(ctrlRatio);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".ctrl-step")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Float getCtrlStep() {
			return DashScopeImageProperties.this.getCtrlStep();
		}

		public void setCtrlStep(Float ctrlStep) {
			DashScopeImageProperties.this.setCtrlStep(ctrlStep);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".creative-title-layout")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Boolean getCreativeTitleLayout() {
			return DashScopeImageProperties.this.getCreativeTitleLayout();
		}

		public void setCreativeTitleLayout(Boolean creativeTitleLayout) {
			DashScopeImageProperties.this.setCreativeTitleLayout(creativeTitleLayout);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".fast-mode")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Boolean getFastMode() {
			return DashScopeImageProperties.this.getFastMode();
		}

		public void setFastMode(Boolean fastMode) {
			DashScopeImageProperties.this.setFastMode(fastMode);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".dilate-flag")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Boolean getDilateFlag() {
			return DashScopeImageProperties.this.getDilateFlag();
		}

		public void setDilateFlag(Boolean dilateFlag) {
			DashScopeImageProperties.this.setDilateFlag(dilateFlag);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".restore-face")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Boolean getRestoreFace() {
			return DashScopeImageProperties.this.getRestoreFace();
		}

		public void setRestoreFace(Boolean restoreFace) {
			DashScopeImageProperties.this.setRestoreFace(restoreFace);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".gender")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable String getGender() {
			return DashScopeImageProperties.this.getGender();
		}

		public void setGender(String gender) {
			DashScopeImageProperties.this.setGender(gender);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".clothes-type")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable List<String> getClothesType() {
			return DashScopeImageProperties.this.getClothesType();
		}

		public void setClothesType(List<String> clothesType) {
			DashScopeImageProperties.this.setClothesType(clothesType);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".resources")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable List<Resource> getResources() {
			return DashScopeImageProperties.this.getResources();
		}

		public void setResources(List<Resource> resources) {
			DashScopeImageProperties.this.setResources(resources);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".skin-retouch")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Boolean getSkinRetouch() {
			return DashScopeImageProperties.this.getSkinRetouch();
		}

		public void setSkinRetouch(Boolean skinRetouch) {
			DashScopeImageProperties.this.setSkinRetouch(skinRetouch);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".steps")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Integer getSteps() {
			return DashScopeImageProperties.this.getSteps();
		}

		public void setSteps(Integer steps) {
			DashScopeImageProperties.this.setSteps(steps);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".font-name")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable String getFontName() {
			return DashScopeImageProperties.this.getFontName();
		}

		public void setFontName(String fontName) {
			DashScopeImageProperties.this.setFontName(fontName);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".ttf-url")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable String getTtfUrl() {
			return DashScopeImageProperties.this.getTtfUrl();
		}

		public void setTtfUrl(String ttfUrl) {
			DashScopeImageProperties.this.setTtfUrl(ttfUrl);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".image-short-size")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Integer getImageShortSize() {
			return DashScopeImageProperties.this.getImageShortSize();
		}

		public void setImageShortSize(Integer imageShortSize) {
			DashScopeImageProperties.this.setImageShortSize(imageShortSize);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".alpha-channel")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Boolean getAlphaChannel() {
			return DashScopeImageProperties.this.getAlphaChannel();
		}

		public void setAlphaChannel(Boolean alphaChannel) {
			DashScopeImageProperties.this.setAlphaChannel(alphaChannel);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".training-file-ids")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable List<String> getTrainingFileIds() {
			return DashScopeImageProperties.this.getTrainingFileIds();
		}

		public void setTrainingFileIds(List<String> trainingFileIds) {
			DashScopeImageProperties.this.setTrainingFileIds(trainingFileIds);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".invoke-mode")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable InvokeMode getInvokeMode() {
			return DashScopeImageProperties.this.getInvokeMode();
		}

		public void setInvokeMode(InvokeMode invokeMode) {
			DashScopeImageProperties.this.setInvokeMode(invokeMode);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".request-type")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable RequestType getRequestType() {
			return DashScopeImageProperties.this.getRequestType();
		}

		public void setRequestType(RequestType requestType) {
			DashScopeImageProperties.this.setRequestType(requestType);
		}

	}


}
