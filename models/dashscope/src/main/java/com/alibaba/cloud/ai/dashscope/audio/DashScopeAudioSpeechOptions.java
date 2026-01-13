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
package com.alibaba.cloud.ai.dashscope.audio;

import com.alibaba.cloud.ai.dashscope.api.DashScopeAudioSpeechApi;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.ai.audio.tts.TextToSpeechOptions;

import java.util.List;

/**
 * @author kevinlin09
 * @author xuguan
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DashScopeAudioSpeechOptions implements TextToSpeechOptions {

	// @formatter:off
    /**
     * Audio Speech models.
     */
    @JsonProperty("model")
    private String model;

    /**
     * Text content.
     */
    @JsonProperty("text")
    private String text;

	/**
	 * Voice, only for tts v2.
	 */
	@JsonProperty("voice")
	private String voice;

	/**
	 * Input Text type.
	 */
	@JsonProperty("request_text_type")
	private DashScopeAudioSpeechApi.RequestTextType requestTextType = DashScopeAudioSpeechApi.RequestTextType.PLAIN_TEXT;

    /**
     * synthesis audio sample rate.
     */
    @JsonProperty("sample_rate")
    private Integer sampleRate = 48000;

    /**
     * synthesis audio volume.
     */
    @JsonProperty("volume")
    private Integer volume = 50;

    /**
     * synthesis audio speed.
     */
    @JsonProperty("speed")
    private Double speed = 1.0;

    /**
     * synthesis audio pitch.
     */
    @JsonProperty("pitch")
    private Double pitch = 1.0;

    /**
     * enable word level timestamp.
     */
    @JsonProperty("enable_word_timestamp")
    private Boolean enableWordTimestamp = false;

    /**
     * enable phoneme level timestamp.
     */
    @JsonProperty("enable_phoneme_timestamp")
    private Boolean enablePhonemeTimestamp = false;

	/**
	 * Whether SSML is enabled. When this parameter is set to true,
	 * text is only allowed to be sent once,
	 * and plain text or text containing SSML is supported.
	 */
	@JsonProperty("enable_ssml")
	private Boolean enableSsml;

	/**
	 * Audio bit rate.
	 */
	@JsonProperty("bit_rate")
	private Integer bitRate;

	/**
	 * The random number seed used at the time of generation.
	 */
	@JsonProperty("seed")
	private Integer seed;

	/**
	 * Synthetic Text Language.
	 */
	@JsonProperty("language_hints")
	private List<String> languageHints;

	/**
	 * 	Set prompt words.
	 * 	Only cosyvoice-v3 and cosyvoice-v3-plus support this feature.
	 * 	Currently only emotions are supported.
	 */
	@JsonProperty("instruction")
	private String instruction;

    /**
     * The format of the audio output. Supported formats are mp3, wav, and pcm. Defaults
     * to mp3.
     */
    @JsonProperty("response_format")
    private DashScopeAudioSpeechApi.ResponseFormat responseFormat = DashScopeAudioSpeechApi.ResponseFormat.MP3;

    // @formatter:on

	public static Builder builder() {
		return new Builder();
	}

	@Override
	public String getModel() {
		return this.model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getText() {
		return this.text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@Override
	public String getVoice() {
		return this.voice;
	}

	public void setVoice(String voice) {
		this.voice = voice;
	}

	public DashScopeAudioSpeechApi.RequestTextType getRequestTextType() {
		return this.requestTextType;
	}

	public void setRequestTextType(DashScopeAudioSpeechApi.RequestTextType requestTextType) {
		this.requestTextType = requestTextType;
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

	@Override
	public Double getSpeed() {
		return this.speed;
	}

	public void setSpeed(Double speed) {
		this.speed = speed;
	}

	public Double getPitch() {
		return this.pitch;
	}

	public void setPitch(Double pitch) {
		this.pitch = pitch;
	}

	public Boolean getEnableWordTimestamp() {
		return this.enableWordTimestamp;
	}

	public void setEnableWordTimestamp(Boolean enableWordTimestamp) {
		this.enableWordTimestamp = enableWordTimestamp;
	}

	public Boolean getEnablePhonemeTimestamp() {
		return this.enablePhonemeTimestamp;
	}

	public void setEnablePhonemeTimestamp(Boolean enablePhonemeTimestamp) {
		this.enablePhonemeTimestamp = enablePhonemeTimestamp;
	}

	public Boolean getEnableSsml() {
		return this.enableSsml;
	}

	public void setEnableSsml(Boolean enableSsml) {
		this.enableSsml = enableSsml;
	}

	public Integer getBitRate() {
		return bitRate;
	}

	public void setBitRate(Integer bitRate) {
		this.bitRate = bitRate;
	}

	public Integer getSeed() {
		return this.seed;
	}

	public void setSeed(Integer seed) {
		this.seed = seed;
	}

	public List<String> getLanguageHints() {
		return this.languageHints;
	}

	public void setLanguageHints(List<String> languageHints) {
		this.languageHints = languageHints;
	}

	public String getInstruction() {
		return this.instruction;
	}

	public void setInstruction(String instruction) {
		this.instruction = instruction;
	}

	public DashScopeAudioSpeechApi.ResponseFormat getResponseFormat() {
		return this.responseFormat;
	}

	public void setResponseFormat(DashScopeAudioSpeechApi.ResponseFormat responseFormat) {
		this.responseFormat = responseFormat;
	}

	@Override
	public String getFormat() {
		return this.responseFormat == null ? null : this.responseFormat.getValue();
	}

	@Override
	@SuppressWarnings("unchecked")
	public DashScopeAudioSpeechOptions copy() {
		return DashScopeAudioSpeechOptions.builder()
			.model(this.model)
			.text(this.text)
			.voice(this.voice)
			.requestTextType(this.requestTextType)
			.sampleRate(this.sampleRate)
			.volume(this.volume)
			.speed(this.speed)
			.pitch(this.pitch)
			.enableWordTimestamp(this.enableWordTimestamp)
			.enablePhonemeTimestamp(this.enablePhonemeTimestamp)
			.enableSsml(this.enableSsml)
			.bitRate(this.bitRate)
			.seed(this.seed)
			.languageHints(this.languageHints)
			.instruction(this.instruction)
			.responseFormat(this.responseFormat)
			.build();
	}

	/**
	 * Build an options instances.
	 */
	public static class Builder {

		private final DashScopeAudioSpeechOptions options = new DashScopeAudioSpeechOptions();

		public DashScopeAudioSpeechOptions.Builder model(String model) {
			options.model = model;
			return this;
		}

		public DashScopeAudioSpeechOptions.Builder text(String text) {
			options.text = text;
			return this;
		}

		public DashScopeAudioSpeechOptions.Builder voice(String voice) {
			options.voice = voice;
			return this;
		}

		public DashScopeAudioSpeechOptions.Builder requestTextType(
				DashScopeAudioSpeechApi.RequestTextType requestTextType) {
			options.requestTextType = requestTextType;
			return this;
		}

		public DashScopeAudioSpeechOptions.Builder sampleRate(Integer sampleRate) {
			options.sampleRate = sampleRate;
			return this;
		}

		public DashScopeAudioSpeechOptions.Builder volume(Integer volume) {
			options.volume = volume;
			return this;
		}

		public DashScopeAudioSpeechOptions.Builder speed(Double speed) {
			options.speed = speed;
			return this;
		}

		public DashScopeAudioSpeechOptions.Builder responseFormat(DashScopeAudioSpeechApi.ResponseFormat format) {
			options.responseFormat = format;
			return this;
		}

		public DashScopeAudioSpeechOptions.Builder pitch(Double pitch) {
			options.pitch = pitch;
			return this;
		}

		public DashScopeAudioSpeechOptions.Builder enableWordTimestamp(Boolean enableWordTimestamp) {
			options.enableWordTimestamp = enableWordTimestamp;
			return this;
		}

		public DashScopeAudioSpeechOptions.Builder enablePhonemeTimestamp(Boolean enablePhonemeTimestamp) {
			options.enablePhonemeTimestamp = enablePhonemeTimestamp;
			return this;
		}

		public DashScopeAudioSpeechOptions.Builder enableSsml(Boolean enableSsml) {
			options.enableSsml = enableSsml;
			return this;
		}

		public DashScopeAudioSpeechOptions.Builder bitRate(Integer bitRate) {
			options.bitRate = bitRate;
			return this;
		}

		public DashScopeAudioSpeechOptions.Builder seed(Integer seed) {
			options.seed = seed;
			return this;
		}

		public DashScopeAudioSpeechOptions.Builder languageHints(List<String> languageHints) {
			options.languageHints = languageHints;
			return this;
		}

		public DashScopeAudioSpeechOptions.Builder instruction(String instruction) {
			options.instruction = instruction;
			return this;
		}

		public DashScopeAudioSpeechOptions build() {
			return options;
		}

	}

}
