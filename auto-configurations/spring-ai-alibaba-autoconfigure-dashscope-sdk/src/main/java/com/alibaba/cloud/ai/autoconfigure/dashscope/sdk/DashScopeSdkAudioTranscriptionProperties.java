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

import java.util.List;
import java.util.Map;

import com.alibaba.cloud.ai.dashscope.sdk.audio.transcription.DashScopeSdkAudioTranscriptionOptions;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.DeprecatedConfigurationProperty;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * DashScope SDK audio transcription model properties.
 */
@ConfigurationProperties(DashScopeSdkAudioTranscriptionProperties.CONFIG_PREFIX)
public class DashScopeSdkAudioTranscriptionProperties extends DashScopeSdkParentProperties {

	public static final String CONFIG_PREFIX = "spring.ai.dashscope.sdk.audio.transcription";

	private boolean enabled = true;

	@NestedConfigurationProperty
	private DashScopeSdkAudioTranscriptionOptions options = DashScopeSdkAudioTranscriptionOptions.builder()
		.model("paraformer-v2")
		.build();

	public DashScopeSdkAudioTranscriptionOptions toOptions() {
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
	public DashScopeSdkAudioTranscriptionOptions getOptions() {
		return this.options;
	}

	public void setOptions(DashScopeSdkAudioTranscriptionOptions options) {
		this.options = options;
	}

	public String getModel() {
		return this.options.getModel();
	}

	public void setModel(String model) {
		this.options.setModel(model);
	}

	public List<String> getFileUrls() {
		return this.options.getFileUrls();
	}

	public void setFileUrls(List<String> fileUrls) {
		this.options.setFileUrls(fileUrls);
	}

	public String getPhraseId() {
		return this.options.getPhraseId();
	}

	public void setPhraseId(String phraseId) {
		this.options.setPhraseId(phraseId);
	}

	public List<Integer> getChannelId() {
		return this.options.getChannelId();
	}

	public void setChannelId(List<Integer> channelId) {
		this.options.setChannelId(channelId);
	}

	public Boolean getDiarizationEnabled() {
		return this.options.getDiarizationEnabled();
	}

	public void setDiarizationEnabled(Boolean diarizationEnabled) {
		this.options.setDiarizationEnabled(diarizationEnabled);
	}

	public Integer getSpeakerCount() {
		return this.options.getSpeakerCount();
	}

	public void setSpeakerCount(Integer speakerCount) {
		this.options.setSpeakerCount(speakerCount);
	}

	public Boolean getDisfluencyRemovalEnabled() {
		return this.options.getDisfluencyRemovalEnabled();
	}

	public void setDisfluencyRemovalEnabled(Boolean disfluencyRemovalEnabled) {
		this.options.setDisfluencyRemovalEnabled(disfluencyRemovalEnabled);
	}

	public Boolean getTimestampAlignmentEnabled() {
		return this.options.getTimestampAlignmentEnabled();
	}

	public void setTimestampAlignmentEnabled(Boolean timestampAlignmentEnabled) {
		this.options.setTimestampAlignmentEnabled(timestampAlignmentEnabled);
	}

	public String getSpecialWordFilter() {
		return this.options.getSpecialWordFilter();
	}

	public void setSpecialWordFilter(String specialWordFilter) {
		this.options.setSpecialWordFilter(specialWordFilter);
	}

	public Boolean getAudioEventDetectionEnabled() {
		return this.options.getAudioEventDetectionEnabled();
	}

	public void setAudioEventDetectionEnabled(Boolean audioEventDetectionEnabled) {
		this.options.setAudioEventDetectionEnabled(audioEventDetectionEnabled);
	}

	public Map<String, String> getHttpHeaders() {
		return this.options.getHttpHeaders();
	}

	public void setHttpHeaders(Map<String, String> httpHeaders) {
		this.options.setHttpHeaders(httpHeaders);
	}

}
