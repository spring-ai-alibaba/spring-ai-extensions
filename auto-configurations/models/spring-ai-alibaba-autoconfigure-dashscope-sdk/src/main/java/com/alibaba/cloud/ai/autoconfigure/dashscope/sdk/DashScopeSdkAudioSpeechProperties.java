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
import org.jspecify.annotations.Nullable;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.DeprecatedConfigurationProperty;

/**
 * DashScope SDK audio speech model properties.
 */
@ConfigurationProperties(DashScopeSdkAudioSpeechProperties.CONFIG_PREFIX)
public class DashScopeSdkAudioSpeechProperties extends DashScopeSdkParentProperties {

	public static final String CONFIG_PREFIX = "spring.ai.dashscope.sdk.audio.speech";

	private boolean enabled = true;
	private DashScopeSdkAudioSpeechOptions options = DashScopeSdkAudioSpeechOptions.builder()
		.model("sambert-zhichu-v1")
		.build();
	private final Options legacyOptions = new Options();

	public DashScopeSdkAudioSpeechOptions toOptions() {
		if (this.options == null) {
			this.options = DashScopeSdkAudioSpeechOptions.builder().model("sambert-zhichu-v1").build();
		}
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
	public Options getOptions() {
		return this.legacyOptions;
	}

	public void setOptions(Options options) {
		// Deprecated options are applied by the nested Options setters.
	}

	private void updateOptions(java.util.function.Consumer<DashScopeSdkAudioSpeechOptions.Builder> customizer) {
		DashScopeSdkAudioSpeechOptions.Builder builder = DashScopeSdkAudioSpeechOptions.builder().from(toOptions());
		customizer.accept(builder);
		this.options = builder.build();
	}

	public @Nullable String getModel() {
		return toOptions().getModel();
	}

	public void setModel(String model) {
		updateOptions(builder -> builder.model(model));
	}

	public @Nullable String getVoice() {
		return toOptions().getVoice();
	}

	public void setVoice(String voice) {
		updateOptions(builder -> builder.voice(voice));
	}

	public @Nullable String getFormat() {
		return toOptions().getFormat();
	}

	public void setFormat(String format) {
		updateOptions(builder -> builder.format(format));
	}

	public @Nullable Double getSpeed() {
		return toOptions().getSpeed();
	}

	public void setSpeed(Double speed) {
		updateOptions(builder -> builder.speed(speed));
	}

	public @Nullable String getTextType() {
		return toOptions().getTextType();
	}

	public void setTextType(String textType) {
		updateOptions(builder -> builder.textType(textType));
	}

	public @Nullable Integer getSampleRate() {
		return toOptions().getSampleRate();
	}

	public void setSampleRate(Integer sampleRate) {
		updateOptions(builder -> builder.sampleRate(sampleRate));
	}

	public @Nullable Integer getVolume() {
		return toOptions().getVolume();
	}

	public void setVolume(Integer volume) {
		updateOptions(builder -> builder.volume(volume));
	}

	public @Nullable Float getRate() {
		return toOptions().getRate();
	}

	public void setRate(Float rate) {
		updateOptions(builder -> builder.rate(rate));
	}

	public @Nullable Float getPitch() {
		return toOptions().getPitch();
	}

	public void setPitch(Float pitch) {
		updateOptions(builder -> builder.pitch(pitch));
	}

	public @Nullable Boolean getWordTimestampEnabled() {
		return toOptions().getWordTimestampEnabled();
	}

	public void setWordTimestampEnabled(Boolean wordTimestampEnabled) {
		updateOptions(builder -> builder.wordTimestampEnabled(wordTimestampEnabled));
	}

	public @Nullable Boolean getPhonemeTimestampEnabled() {
		return toOptions().getPhonemeTimestampEnabled();
	}

	public void setPhonemeTimestampEnabled(Boolean phonemeTimestampEnabled) {
		updateOptions(builder -> builder.phonemeTimestampEnabled(phonemeTimestampEnabled));
	}

	public @Nullable Map<String, String> getHttpHeaders() {
		return toOptions().getHttpHeaders();
	}

	public void setHttpHeaders(Map<String, String> httpHeaders) {
		updateOptions(builder -> builder.httpHeaders(httpHeaders));
	}
	public class Options {

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".model")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable String getModel() {
			return DashScopeSdkAudioSpeechProperties.this.getModel();
		}

		public void setModel(String model) {
			DashScopeSdkAudioSpeechProperties.this.setModel(model);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".voice")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable String getVoice() {
			return DashScopeSdkAudioSpeechProperties.this.getVoice();
		}

		public void setVoice(String voice) {
			DashScopeSdkAudioSpeechProperties.this.setVoice(voice);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".format")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable String getFormat() {
			return DashScopeSdkAudioSpeechProperties.this.getFormat();
		}

		public void setFormat(String format) {
			DashScopeSdkAudioSpeechProperties.this.setFormat(format);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".speed")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Double getSpeed() {
			return DashScopeSdkAudioSpeechProperties.this.getSpeed();
		}

		public void setSpeed(Double speed) {
			DashScopeSdkAudioSpeechProperties.this.setSpeed(speed);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".text-type")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable String getTextType() {
			return DashScopeSdkAudioSpeechProperties.this.getTextType();
		}

		public void setTextType(String textType) {
			DashScopeSdkAudioSpeechProperties.this.setTextType(textType);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".sample-rate")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Integer getSampleRate() {
			return DashScopeSdkAudioSpeechProperties.this.getSampleRate();
		}

		public void setSampleRate(Integer sampleRate) {
			DashScopeSdkAudioSpeechProperties.this.setSampleRate(sampleRate);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".volume")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Integer getVolume() {
			return DashScopeSdkAudioSpeechProperties.this.getVolume();
		}

		public void setVolume(Integer volume) {
			DashScopeSdkAudioSpeechProperties.this.setVolume(volume);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".rate")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Float getRate() {
			return DashScopeSdkAudioSpeechProperties.this.getRate();
		}

		public void setRate(Float rate) {
			DashScopeSdkAudioSpeechProperties.this.setRate(rate);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".pitch")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Float getPitch() {
			return DashScopeSdkAudioSpeechProperties.this.getPitch();
		}

		public void setPitch(Float pitch) {
			DashScopeSdkAudioSpeechProperties.this.setPitch(pitch);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".word-timestamp-enabled")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Boolean getWordTimestampEnabled() {
			return DashScopeSdkAudioSpeechProperties.this.getWordTimestampEnabled();
		}

		public void setWordTimestampEnabled(Boolean wordTimestampEnabled) {
			DashScopeSdkAudioSpeechProperties.this.setWordTimestampEnabled(wordTimestampEnabled);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".phoneme-timestamp-enabled")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Boolean getPhonemeTimestampEnabled() {
			return DashScopeSdkAudioSpeechProperties.this.getPhonemeTimestampEnabled();
		}

		public void setPhonemeTimestampEnabled(Boolean phonemeTimestampEnabled) {
			DashScopeSdkAudioSpeechProperties.this.setPhonemeTimestampEnabled(phonemeTimestampEnabled);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".http-headers")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Map<String, String> getHttpHeaders() {
			return DashScopeSdkAudioSpeechProperties.this.getHttpHeaders();
		}

		public void setHttpHeaders(Map<String, String> httpHeaders) {
			DashScopeSdkAudioSpeechProperties.this.setHttpHeaders(httpHeaders);
		}

	}


}
