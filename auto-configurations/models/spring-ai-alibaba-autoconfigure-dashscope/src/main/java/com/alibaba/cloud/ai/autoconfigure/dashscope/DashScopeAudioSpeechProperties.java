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

import com.alibaba.cloud.ai.dashscope.audio.tts.DashScopeAudioSpeechOptions;
import com.alibaba.cloud.ai.dashscope.common.DashScopeAudioApiConstants;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.DeprecatedConfigurationProperty;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * @author kevinlin09、yingzi
 */

@ConfigurationProperties(DashScopeAudioSpeechProperties.CONFIG_PREFIX)
public class DashScopeAudioSpeechProperties extends DashScopeParentProperties {

	/**
	 * Spring AI Alibaba configuration prefix.
	 */
	public static final String CONFIG_PREFIX = "spring.ai.dashscope.audio.speech";

    private String websocketUrl = DashScopeAudioApiConstants.DEFAULT_WEBSOCKET_URL;

    @NestedConfigurationProperty
	private DashScopeAudioSpeechOptions options = DashScopeAudioSpeechOptions.builder().build();

	public DashScopeAudioSpeechOptions toOptions() {
		return this.options;
	}

	@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX)
	@Deprecated(since = "2.0.0", forRemoval = true)
	public DashScopeAudioSpeechOptions getOptions() {
		return options;
	}

	public void setOptions(DashScopeAudioSpeechOptions options) {
		this.options = options;
	}

	public String getModel() {
		return this.options.getModel();
	}

	public void setModel(String model) {
		this.options.setModel(model);
	}

	public String getVoice() {
		return this.options.getVoice();
	}

	public void setVoice(String voice) {
		this.options.setVoice(voice);
	}

	public String getTextType() {
		return this.options.getTextType();
	}

	public void setTextType(String textType) {
		this.options.setTextType(textType);
	}

	public Boolean getEnableAigcTag() {
		return this.options.getEnableAigcTag();
	}

	public void setEnableAigcTag(Boolean enableAigcTag) {
		this.options.setEnableAigcTag(enableAigcTag);
	}

	public String getAigcPropagator() {
		return this.options.getAigcPropagator();
	}

	public void setAigcPropagator(String aigcPropagator) {
		this.options.setAigcPropagator(aigcPropagator);
	}

	public String getAigcPropagateId() {
		return this.options.getAigcPropagateId();
	}

	public void setAigcPropagateId(String aigcPropagateId) {
		this.options.setAigcPropagateId(aigcPropagateId);
	}

	public Integer getSampleRate() {
		return this.options.getSampleRate();
	}

	public void setSampleRate(Integer sampleRate) {
		this.options.setSampleRate(sampleRate);
	}

	public String getFormat() {
		return this.options.getFormat();
	}

	public void setFormat(String format) {
		this.options.setFormat(format);
	}

	public void setResponseFormat(String format) {
		this.options.setResponseFormat(format);
	}

	public Boolean getWordTimestampEnabled() {
		return this.options.getWordTimestampEnabled();
	}

	public void setWordTimestampEnabled(Boolean wordTimestampEnabled) {
		this.options.setWordTimestampEnabled(wordTimestampEnabled);
	}

	public Boolean getPhonemeTimestampEnabled() {
		return this.options.getPhonemeTimestampEnabled();
	}

	public void setPhonemeTimestampEnabled(Boolean phonemeTimestampEnabled) {
		this.options.setPhonemeTimestampEnabled(phonemeTimestampEnabled);
	}

	public Integer getVolume() {
		return this.options.getVolume();
	}

	public void setVolume(Integer volume) {
		this.options.setVolume(volume);
	}

	public Double getSpeed() {
		return this.options.getSpeed();
	}

	public void setSpeed(Double speed) {
		this.options.setSpeed(speed);
	}

	public Float getRate() {
		return this.options.getRate();
	}

	public void setRate(Float rate) {
		this.options.setRate(rate);
	}

	public Float getPitch() {
		return this.options.getPitch();
	}

	public void setPitch(Float pitch) {
		this.options.setPitch(pitch);
	}

	public Boolean getEnableSsml() {
		return this.options.getEnableSsml();
	}

	public void setEnableSsml(Boolean enableSsml) {
		this.options.setEnableSsml(enableSsml);
	}

	public Integer getBitRate() {
		return this.options.getBitRate();
	}

	public void setBitRate(Integer bitRate) {
		this.options.setBitRate(bitRate);
	}

	public Integer getSeed() {
		return this.options.getSeed();
	}

	public void setSeed(Integer seed) {
		this.options.setSeed(seed);
	}

	public List<String> getLanguageHints() {
		return this.options.getLanguageHints();
	}

	public void setLanguageHints(List<String> languageHints) {
		this.options.setLanguageHints(languageHints);
	}

	public String getInstruction() {
		return this.options.getInstruction();
	}

	public void setInstruction(String instruction) {
		this.options.setInstruction(instruction);
	}

	public Boolean getOptimizeInstructions() {
		return this.options.getOptimizeInstructions();
	}

	public void setOptimizeInstructions(Boolean optimizeInstructions) {
		this.options.setOptimizeInstructions(optimizeInstructions);
	}

	public String getLanguageType() {
		return this.options.getLanguageType();
	}

	public void setLanguageType(String languageType) {
		this.options.setLanguageType(languageType);
	}

    public String getWebsocketUrl() {
        return websocketUrl;
    }

    public void setWebsocketUrl(String websocketUrl) {
        this.websocketUrl = websocketUrl;
    }

}
