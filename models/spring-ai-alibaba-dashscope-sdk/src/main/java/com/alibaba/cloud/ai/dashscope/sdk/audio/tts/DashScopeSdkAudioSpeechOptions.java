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
import org.jspecify.annotations.Nullable;
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
	private final @Nullable String model;

	@JsonProperty("voice")
	private final @Nullable String voice;

	@JsonProperty("format")
	private final @Nullable String format;

	@JsonProperty("speed")
	private final @Nullable Double speed;

	@JsonProperty("text_type")
	private final @Nullable String textType;

	@JsonProperty("sample_rate")
	private final @Nullable Integer sampleRate;

	@JsonProperty("volume")
	private final @Nullable Integer volume;

	@JsonProperty("rate")
	private final @Nullable Float rate;

	@JsonProperty("pitch")
	private final @Nullable Float pitch;

	@JsonProperty("word_timestamp_enabled")
	private final @Nullable Boolean wordTimestampEnabled;

	@JsonProperty("phoneme_timestamp_enabled")
	private final @Nullable Boolean phonemeTimestampEnabled;

	@JsonIgnore
	private final Map<String, String> httpHeaders;

	protected DashScopeSdkAudioSpeechOptions(@Nullable String model, @Nullable String voice, @Nullable String format,
			@Nullable Double speed, @Nullable String textType, @Nullable Integer sampleRate, @Nullable Integer volume,
			@Nullable Float rate, @Nullable Float pitch, @Nullable Boolean wordTimestampEnabled,
			@Nullable Boolean phonemeTimestampEnabled, @Nullable Map<String, String> httpHeaders) {
		this.model = model;
		this.voice = voice;
		this.format = format;
		this.speed = speed;
		this.textType = textType;
		this.sampleRate = sampleRate;
		this.volume = volume;
		this.rate = rate;
		this.pitch = pitch;
		this.wordTimestampEnabled = wordTimestampEnabled;
		this.phonemeTimestampEnabled = phonemeTimestampEnabled;
		this.httpHeaders = httpHeaders != null ? new HashMap<>(httpHeaders) : new HashMap<>();
	}

	public static Builder builder() {
		return new Builder();
	}

	public static DashScopeSdkAudioSpeechOptions fromOptions(DashScopeSdkAudioSpeechOptions options) {
		return options.mutate().build();
	}

	@Override
	public @Nullable String getModel() {
		return this.model;
	}

	@Override
	public @Nullable String getVoice() {
		return this.voice;
	}

	@Override
	public @Nullable String getFormat() {
		return this.format;
	}

	@Override
	public @Nullable Double getSpeed() {
		return this.speed;
	}

	public @Nullable String getTextType() {
		return this.textType;
	}

	public @Nullable Integer getSampleRate() {
		return this.sampleRate;
	}

	public @Nullable Integer getVolume() {
		return this.volume;
	}

	public @Nullable Float getRate() {
		return this.rate;
	}

	public @Nullable Float getPitch() {
		return this.pitch;
	}

	public @Nullable Boolean getWordTimestampEnabled() {
		return this.wordTimestampEnabled;
	}

	public @Nullable Boolean getPhonemeTimestampEnabled() {
		return this.phonemeTimestampEnabled;
	}

	public Map<String, String> getHttpHeaders() {
		return this.httpHeaders;
	}

	public Builder mutate() {
		return builder()
			.model(this.model)
			.voice(this.voice)
			.format(this.format)
			.speed(this.speed)
			.textType(this.textType)
			.sampleRate(this.sampleRate)
			.volume(this.volume)
			.rate(this.rate)
			.pitch(this.pitch)
			.wordTimestampEnabled(this.wordTimestampEnabled)
			.phonemeTimestampEnabled(this.phonemeTimestampEnabled)
			.httpHeaders(this.httpHeaders);
	}

	@Override
	public DashScopeSdkAudioSpeechOptions copy() {
		return mutate().build();
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

	@Override
	public String toString() {
		return "DashScopeSdkAudioSpeechOptions{" + "model='" + this.model + '\'' + ", voice='" + this.voice + '\''
				+ ", format='" + this.format + '\'' + ", speed=" + this.speed + ", textType='" + this.textType + '\''
				+ ", sampleRate=" + this.sampleRate + ", volume=" + this.volume + ", rate=" + this.rate
				+ ", pitch=" + this.pitch + ", wordTimestampEnabled=" + this.wordTimestampEnabled
				+ ", phonemeTimestampEnabled=" + this.phonemeTimestampEnabled + ", httpHeaders=" + this.httpHeaders
				+ '}';
	}

	public static class Builder {

		protected @Nullable String model;

		protected @Nullable String voice;

		protected @Nullable String format;

		protected @Nullable Double speed;

		protected @Nullable String textType;

		protected @Nullable Integer sampleRate;

		protected @Nullable Integer volume;

		protected @Nullable Float rate;

		protected @Nullable Float pitch;

		protected @Nullable Boolean wordTimestampEnabled;

		protected @Nullable Boolean phonemeTimestampEnabled;

		protected Map<String, String> httpHeaders = new HashMap<>();

		public Builder() {
		}

		public Builder model(@Nullable String model) {
			this.model = model;
			return this;
		}

		public Builder voice(@Nullable String voice) {
			this.voice = voice;
			return this;
		}

		public Builder format(@Nullable String format) {
			this.format = format;
			return this;
		}

		public Builder speed(@Nullable Double speed) {
			this.speed = speed;
			return this;
		}

		public Builder textType(@Nullable String textType) {
			this.textType = textType;
			return this;
		}

		public Builder sampleRate(@Nullable Integer sampleRate) {
			this.sampleRate = sampleRate;
			return this;
		}

		public Builder volume(@Nullable Integer volume) {
			this.volume = volume;
			return this;
		}

		public Builder rate(@Nullable Float rate) {
			this.rate = rate;
			return this;
		}

		public Builder pitch(@Nullable Float pitch) {
			this.pitch = pitch;
			return this;
		}

		public Builder wordTimestampEnabled(@Nullable Boolean wordTimestampEnabled) {
			this.wordTimestampEnabled = wordTimestampEnabled;
			return this;
		}

		public Builder phonemeTimestampEnabled(@Nullable Boolean phonemeTimestampEnabled) {
			this.phonemeTimestampEnabled = phonemeTimestampEnabled;
			return this;
		}

		public Builder httpHeaders(@Nullable Map<String, String> httpHeaders) {
			this.httpHeaders = httpHeaders != null ? new HashMap<>(httpHeaders) : new HashMap<>();
			return this;
		}

		public Builder from(DashScopeSdkAudioSpeechOptions fromOptions) {
			this.model = fromOptions.getModel();
			this.voice = fromOptions.getVoice();
			this.format = fromOptions.getFormat();
			this.speed = fromOptions.getSpeed();
			this.textType = fromOptions.getTextType();
			this.sampleRate = fromOptions.getSampleRate();
			this.volume = fromOptions.getVolume();
			this.rate = fromOptions.getRate();
			this.pitch = fromOptions.getPitch();
			this.wordTimestampEnabled = fromOptions.getWordTimestampEnabled();
			this.phonemeTimestampEnabled = fromOptions.getPhonemeTimestampEnabled();
			this.httpHeaders = fromOptions.getHttpHeaders();
			return this;
		}

		public Builder merge(@Nullable TextToSpeechOptions from) {
			if (from == null) {
				return this;
			}
			if (from.getModel() != null) {
				this.model = from.getModel();
			}
			if (from.getVoice() != null) {
				this.voice = from.getVoice();
			}
			if (from instanceof DashScopeSdkAudioSpeechOptions castFrom) {
				if (castFrom.getFormat() != null) {
					this.format = castFrom.getFormat();
				}
				if (castFrom.getSpeed() != null) {
					this.speed = castFrom.getSpeed();
				}
				if (castFrom.getTextType() != null) {
					this.textType = castFrom.getTextType();
				}
				if (castFrom.getSampleRate() != null) {
					this.sampleRate = castFrom.getSampleRate();
				}
				if (castFrom.getVolume() != null) {
					this.volume = castFrom.getVolume();
				}
				if (castFrom.getRate() != null) {
					this.rate = castFrom.getRate();
				}
				if (castFrom.getPitch() != null) {
					this.pitch = castFrom.getPitch();
				}
				if (castFrom.getWordTimestampEnabled() != null) {
					this.wordTimestampEnabled = castFrom.getWordTimestampEnabled();
				}
				if (castFrom.getPhonemeTimestampEnabled() != null) {
					this.phonemeTimestampEnabled = castFrom.getPhonemeTimestampEnabled();
				}
				if (!castFrom.getHttpHeaders().isEmpty()) {
					this.httpHeaders = castFrom.getHttpHeaders();
				}
			}
			return this;
		}

        // @formatter:off
		public DashScopeSdkAudioSpeechOptions build() {
			return new DashScopeSdkAudioSpeechOptions(this.model, this.voice, this.format, this.speed, this.textType,
					this.sampleRate, this.volume, this.rate, this.pitch, this.wordTimestampEnabled,
					this.phonemeTimestampEnabled, this.httpHeaders);
		}
        // @formatter:on

	}

}
