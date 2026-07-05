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

package com.alibaba.cloud.ai.autoconfigure.dashscope.sdk;

import java.util.Map;

import com.alibaba.cloud.ai.dashscope.sdk.audio.tts.DashScopeSdkAudioSpeechOptions;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.DeprecatedConfigurationProperty;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * DashScope SDK audio speech model properties.
 */
@ConfigurationProperties(DashScopeSdkAudioSpeechProperties.CONFIG_PREFIX)
public class DashScopeSdkAudioSpeechProperties extends DashScopeSdkParentProperties {

	public static final String CONFIG_PREFIX = "spring.ai.dashscope.sdk.audio.speech";

	private boolean enabled = true;

	@NestedConfigurationProperty
	private DashScopeSdkAudioSpeechOptions options = DashScopeSdkAudioSpeechOptions.builder()
		.model("sambert-zhichu-v1")
		.build();

	public DashScopeSdkAudioSpeechOptions toOptions() {
		return this.options;
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX)
	@Deprecated(since = "2.0.0", forRemoval = true)
	public DashScopeSdkAudioSpeechOptions getOptions() {
		return this.options;
	}

	public void setOptions(DashScopeSdkAudioSpeechOptions options) {
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

	public String getFormat() {
		return this.options.getFormat();
	}

	public void setFormat(String format) {
		this.options.setFormat(format);
	}

	public Double getSpeed() {
		return this.options.getSpeed();
	}

	public void setSpeed(Double speed) {
		this.options.setSpeed(speed);
	}

	public String getTextType() {
		return this.options.getTextType();
	}

	public void setTextType(String textType) {
		this.options.setTextType(textType);
	}

	public Integer getSampleRate() {
		return this.options.getSampleRate();
	}

	public void setSampleRate(Integer sampleRate) {
		this.options.setSampleRate(sampleRate);
	}

	public Integer getVolume() {
		return this.options.getVolume();
	}

	public void setVolume(Integer volume) {
		this.options.setVolume(volume);
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

	public Map<String, String> getHttpHeaders() {
		return this.options.getHttpHeaders();
	}

	public void setHttpHeaders(Map<String, String> httpHeaders) {
		this.options.setHttpHeaders(httpHeaders);
	}

}
