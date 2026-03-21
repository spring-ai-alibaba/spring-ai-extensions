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

package com.alibaba.cloud.ai.dashscope.sdk.audio.tts;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.ai.audio.tts.TextToSpeechOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Options for DashScope SDK audio speech model.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DashScopeSdkAudioSpeechOptions implements TextToSpeechOptions {

	@JsonProperty("model")
	private String model;

	@JsonProperty("voice")
	private String voice;

	@JsonProperty("format")
	private String format;

	@JsonProperty("speed")
	private Double speed;

	@JsonProperty("text_type")
	private String textType;

	@JsonProperty("sample_rate")
	private Integer sampleRate;

	@JsonProperty("volume")
	private Integer volume;

	@JsonProperty("rate")
	private Float rate;

	@JsonProperty("pitch")
	private Float pitch;

	@JsonProperty("word_timestamp_enabled")
	private Boolean wordTimestampEnabled;

	@JsonProperty("phoneme_timestamp_enabled")
	private Boolean phonemeTimestampEnabled;

	@JsonIgnore
	private Map<String, String> httpHeaders = new HashMap<>();

	public static DashScopeSdkAudioSpeechOptionsBuilder builder() {
		return new DashScopeSdkAudioSpeechOptionsBuilder();
	}

	public static DashScopeSdkAudioSpeechOptions fromOptions(DashScopeSdkAudioSpeechOptions options) {
		if (options == null) {
			return null;
		}
		DashScopeSdkAudioSpeechOptions copy = new DashScopeSdkAudioSpeechOptions();
		copy.setModel(options.getModel());
		copy.setVoice(options.getVoice());
		copy.setFormat(options.getFormat());
		copy.setSpeed(options.getSpeed());
		copy.setTextType(options.getTextType());
		copy.setSampleRate(options.getSampleRate());
		copy.setVolume(options.getVolume());
		copy.setRate(options.getRate());
		copy.setPitch(options.getPitch());
		copy.setWordTimestampEnabled(options.getWordTimestampEnabled());
		copy.setPhonemeTimestampEnabled(options.getPhonemeTimestampEnabled());
		copy.setHttpHeaders(options.getHttpHeaders() == null ? new HashMap<>() : new HashMap<>(options.getHttpHeaders()));
		return copy;
	}

	@Override
	public String getModel() {
		return this.model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	@Override
	public String getVoice() {
		return this.voice;
	}

	public void setVoice(String voice) {
		this.voice = voice;
	}

	@Override
	public String getFormat() {
		return this.format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	@Override
	public Double getSpeed() {
		return this.speed;
	}

	public void setSpeed(Double speed) {
		this.speed = speed;
	}

	public String getTextType() {
		return this.textType;
	}

	public void setTextType(String textType) {
		this.textType = textType;
	}

	public Integer getSampleRate() {
		return this.sampleRate;
	}

	public void setSampleRate(Integer sampleRate) {
		this.sampleRate = sampleRate;
	}

	public Integer getVolume() {
		return this.volume;
	}

	public void setVolume(Integer volume) {
		this.volume = volume;
	}

	public Float getRate() {
		return this.rate;
	}

	public void setRate(Float rate) {
		this.rate = rate;
	}

	public Float getPitch() {
		return this.pitch;
	}

	public void setPitch(Float pitch) {
		this.pitch = pitch;
	}

	public Boolean getWordTimestampEnabled() {
		return this.wordTimestampEnabled;
	}

	public void setWordTimestampEnabled(Boolean wordTimestampEnabled) {
		this.wordTimestampEnabled = wordTimestampEnabled;
	}

	public Boolean getPhonemeTimestampEnabled() {
		return this.phonemeTimestampEnabled;
	}

	public void setPhonemeTimestampEnabled(Boolean phonemeTimestampEnabled) {
		this.phonemeTimestampEnabled = phonemeTimestampEnabled;
	}

	public Map<String, String> getHttpHeaders() {
		return this.httpHeaders;
	}

	public void setHttpHeaders(Map<String, String> httpHeaders) {
		this.httpHeaders = httpHeaders;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends TextToSpeechOptions> T copy() {
		return (T) DashScopeSdkAudioSpeechOptions.fromOptions(this);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		DashScopeSdkAudioSpeechOptions that = (DashScopeSdkAudioSpeechOptions) o;
		return Objects.equals(this.model, that.model) && Objects.equals(this.voice, that.voice)
				&& Objects.equals(this.format, that.format) && Objects.equals(this.speed, that.speed)
				&& Objects.equals(this.textType, that.textType) && Objects.equals(this.sampleRate, that.sampleRate)
				&& Objects.equals(this.volume, that.volume) && Objects.equals(this.rate, that.rate)
				&& Objects.equals(this.pitch, that.pitch)
				&& Objects.equals(this.wordTimestampEnabled, that.wordTimestampEnabled)
				&& Objects.equals(this.phonemeTimestampEnabled, that.phonemeTimestampEnabled)
				&& Objects.equals(this.httpHeaders, that.httpHeaders);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.model, this.voice, this.format, this.speed, this.textType, this.sampleRate,
				this.volume, this.rate, this.pitch, this.wordTimestampEnabled, this.phonemeTimestampEnabled,
				this.httpHeaders);
	}

	public static class DashScopeSdkAudioSpeechOptionsBuilder {

		private final DashScopeSdkAudioSpeechOptions options;

		public DashScopeSdkAudioSpeechOptionsBuilder() {
			this.options = new DashScopeSdkAudioSpeechOptions();
		}

		public DashScopeSdkAudioSpeechOptionsBuilder model(String model) {
			this.options.model = model;
			return this;
		}

		public DashScopeSdkAudioSpeechOptionsBuilder voice(String voice) {
			this.options.voice = voice;
			return this;
		}

		public DashScopeSdkAudioSpeechOptionsBuilder format(String format) {
			this.options.format = format;
			return this;
		}

		public DashScopeSdkAudioSpeechOptionsBuilder speed(Double speed) {
			this.options.speed = speed;
			return this;
		}

		public DashScopeSdkAudioSpeechOptionsBuilder textType(String textType) {
			this.options.textType = textType;
			return this;
		}

		public DashScopeSdkAudioSpeechOptionsBuilder sampleRate(Integer sampleRate) {
			this.options.sampleRate = sampleRate;
			return this;
		}

		public DashScopeSdkAudioSpeechOptionsBuilder volume(Integer volume) {
			this.options.volume = volume;
			return this;
		}

		public DashScopeSdkAudioSpeechOptionsBuilder rate(Float rate) {
			this.options.rate = rate;
			return this;
		}

		public DashScopeSdkAudioSpeechOptionsBuilder pitch(Float pitch) {
			this.options.pitch = pitch;
			return this;
		}

		public DashScopeSdkAudioSpeechOptionsBuilder wordTimestampEnabled(Boolean wordTimestampEnabled) {
			this.options.wordTimestampEnabled = wordTimestampEnabled;
			return this;
		}

		public DashScopeSdkAudioSpeechOptionsBuilder phonemeTimestampEnabled(Boolean phonemeTimestampEnabled) {
			this.options.phonemeTimestampEnabled = phonemeTimestampEnabled;
			return this;
		}

		public DashScopeSdkAudioSpeechOptionsBuilder httpHeaders(Map<String, String> httpHeaders) {
			this.options.httpHeaders = httpHeaders;
			return this;
		}

		public DashScopeSdkAudioSpeechOptions build() {
			return this.options;
		}

	}

}
