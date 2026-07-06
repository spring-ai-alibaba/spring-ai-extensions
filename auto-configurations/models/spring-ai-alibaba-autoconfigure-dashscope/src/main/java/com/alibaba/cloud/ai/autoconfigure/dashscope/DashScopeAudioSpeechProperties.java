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
import org.jspecify.annotations.Nullable;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.DeprecatedConfigurationProperty;

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
	private DashScopeAudioSpeechOptions options = DashScopeAudioSpeechOptions.builder().build();
	private final Options legacyOptions = new Options();

	public DashScopeAudioSpeechOptions toOptions() {
		if (this.options == null) {
			this.options = DashScopeAudioSpeechOptions.builder().build();
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

	private void updateOptions(java.util.function.Consumer<DashScopeAudioSpeechOptions.Builder> customizer) {
		DashScopeAudioSpeechOptions.Builder builder = DashScopeAudioSpeechOptions.builder().from(toOptions());
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

	public @Nullable String getTextType() {
		return toOptions().getTextType();
	}

	public void setTextType(String textType) {
		updateOptions(builder -> builder.textType(textType));
	}

	public @Nullable Boolean getEnableAigcTag() {
		return toOptions().getEnableAigcTag();
	}

	public void setEnableAigcTag(Boolean enableAigcTag) {
		updateOptions(builder -> builder.enableAigcTag(enableAigcTag));
	}

	public @Nullable String getAigcPropagator() {
		return toOptions().getAigcPropagator();
	}

	public void setAigcPropagator(String aigcPropagator) {
		updateOptions(builder -> builder.aigcPropagator(aigcPropagator));
	}

	public @Nullable String getAigcPropagateId() {
		return toOptions().getAigcPropagateId();
	}

	public void setAigcPropagateId(String aigcPropagateId) {
		updateOptions(builder -> builder.aigcPropagateId(aigcPropagateId));
	}

	public @Nullable Integer getSampleRate() {
		return toOptions().getSampleRate();
	}

	public void setSampleRate(Integer sampleRate) {
		updateOptions(builder -> builder.sampleRate(sampleRate));
	}

	public @Nullable String getFormat() {
		return toOptions().getFormat();
	}

	public void setFormat(String format) {
		updateOptions(builder -> builder.format(format));
	}

	public void setResponseFormat(String format) {
		updateOptions(builder -> builder.format(format));
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

	public @Nullable Integer getVolume() {
		return toOptions().getVolume();
	}

	public void setVolume(Integer volume) {
		updateOptions(builder -> builder.volume(volume));
	}

	public @Nullable Double getSpeed() {
		return toOptions().getSpeed();
	}

	public void setSpeed(Double speed) {
		updateOptions(builder -> builder.speed(speed));
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

	public @Nullable Boolean getEnableSsml() {
		return toOptions().getEnableSsml();
	}

	public void setEnableSsml(Boolean enableSsml) {
		updateOptions(builder -> builder.enableSsml(enableSsml));
	}

	public @Nullable Integer getBitRate() {
		return toOptions().getBitRate();
	}

	public void setBitRate(Integer bitRate) {
		updateOptions(builder -> builder.bitRate(bitRate));
	}

	public @Nullable Integer getSeed() {
		return toOptions().getSeed();
	}

	public void setSeed(Integer seed) {
		updateOptions(builder -> builder.seed(seed));
	}

	public @Nullable List<String> getLanguageHints() {
		return toOptions().getLanguageHints();
	}

	public void setLanguageHints(List<String> languageHints) {
		updateOptions(builder -> builder.languageHints(languageHints));
	}

	public @Nullable String getInstruction() {
		return toOptions().getInstruction();
	}

	public void setInstruction(String instruction) {
		updateOptions(builder -> builder.instruction(instruction));
	}

	public @Nullable Boolean getOptimizeInstructions() {
		return toOptions().getOptimizeInstructions();
	}

	public void setOptimizeInstructions(Boolean optimizeInstructions) {
		updateOptions(builder -> builder.optimizeInstructions(optimizeInstructions));
	}

	public @Nullable String getLanguageType() {
		return toOptions().getLanguageType();
	}

	public void setLanguageType(String languageType) {
		updateOptions(builder -> builder.languageType(languageType));
	}

    public String getWebsocketUrl() {
        return websocketUrl;
    }

    public void setWebsocketUrl(String websocketUrl) {
        this.websocketUrl = websocketUrl;
    }
	public class Options {

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".model")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable String getModel() {
			return DashScopeAudioSpeechProperties.this.getModel();
		}

		public void setModel(String model) {
			DashScopeAudioSpeechProperties.this.setModel(model);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".voice")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable String getVoice() {
			return DashScopeAudioSpeechProperties.this.getVoice();
		}

		public void setVoice(String voice) {
			DashScopeAudioSpeechProperties.this.setVoice(voice);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".text-type")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable String getTextType() {
			return DashScopeAudioSpeechProperties.this.getTextType();
		}

		public void setTextType(String textType) {
			DashScopeAudioSpeechProperties.this.setTextType(textType);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".enable-aigc-tag")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Boolean getEnableAigcTag() {
			return DashScopeAudioSpeechProperties.this.getEnableAigcTag();
		}

		public void setEnableAigcTag(Boolean enableAigcTag) {
			DashScopeAudioSpeechProperties.this.setEnableAigcTag(enableAigcTag);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".aigc-propagator")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable String getAigcPropagator() {
			return DashScopeAudioSpeechProperties.this.getAigcPropagator();
		}

		public void setAigcPropagator(String aigcPropagator) {
			DashScopeAudioSpeechProperties.this.setAigcPropagator(aigcPropagator);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".aigc-propagate-id")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable String getAigcPropagateId() {
			return DashScopeAudioSpeechProperties.this.getAigcPropagateId();
		}

		public void setAigcPropagateId(String aigcPropagateId) {
			DashScopeAudioSpeechProperties.this.setAigcPropagateId(aigcPropagateId);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".sample-rate")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Integer getSampleRate() {
			return DashScopeAudioSpeechProperties.this.getSampleRate();
		}

		public void setSampleRate(Integer sampleRate) {
			DashScopeAudioSpeechProperties.this.setSampleRate(sampleRate);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".format")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable String getFormat() {
			return DashScopeAudioSpeechProperties.this.getFormat();
		}

		public void setFormat(String format) {
			DashScopeAudioSpeechProperties.this.setFormat(format);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".word-timestamp-enabled")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Boolean getWordTimestampEnabled() {
			return DashScopeAudioSpeechProperties.this.getWordTimestampEnabled();
		}

		public void setWordTimestampEnabled(Boolean wordTimestampEnabled) {
			DashScopeAudioSpeechProperties.this.setWordTimestampEnabled(wordTimestampEnabled);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".phoneme-timestamp-enabled")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Boolean getPhonemeTimestampEnabled() {
			return DashScopeAudioSpeechProperties.this.getPhonemeTimestampEnabled();
		}

		public void setPhonemeTimestampEnabled(Boolean phonemeTimestampEnabled) {
			DashScopeAudioSpeechProperties.this.setPhonemeTimestampEnabled(phonemeTimestampEnabled);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".volume")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Integer getVolume() {
			return DashScopeAudioSpeechProperties.this.getVolume();
		}

		public void setVolume(Integer volume) {
			DashScopeAudioSpeechProperties.this.setVolume(volume);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".speed")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Double getSpeed() {
			return DashScopeAudioSpeechProperties.this.getSpeed();
		}

		public void setSpeed(Double speed) {
			DashScopeAudioSpeechProperties.this.setSpeed(speed);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".rate")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Float getRate() {
			return DashScopeAudioSpeechProperties.this.getRate();
		}

		public void setRate(Float rate) {
			DashScopeAudioSpeechProperties.this.setRate(rate);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".pitch")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Float getPitch() {
			return DashScopeAudioSpeechProperties.this.getPitch();
		}

		public void setPitch(Float pitch) {
			DashScopeAudioSpeechProperties.this.setPitch(pitch);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".enable-ssml")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Boolean getEnableSsml() {
			return DashScopeAudioSpeechProperties.this.getEnableSsml();
		}

		public void setEnableSsml(Boolean enableSsml) {
			DashScopeAudioSpeechProperties.this.setEnableSsml(enableSsml);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".bit-rate")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Integer getBitRate() {
			return DashScopeAudioSpeechProperties.this.getBitRate();
		}

		public void setBitRate(Integer bitRate) {
			DashScopeAudioSpeechProperties.this.setBitRate(bitRate);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".seed")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Integer getSeed() {
			return DashScopeAudioSpeechProperties.this.getSeed();
		}

		public void setSeed(Integer seed) {
			DashScopeAudioSpeechProperties.this.setSeed(seed);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".language-hints")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable List<String> getLanguageHints() {
			return DashScopeAudioSpeechProperties.this.getLanguageHints();
		}

		public void setLanguageHints(List<String> languageHints) {
			DashScopeAudioSpeechProperties.this.setLanguageHints(languageHints);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".instruction")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable String getInstruction() {
			return DashScopeAudioSpeechProperties.this.getInstruction();
		}

		public void setInstruction(String instruction) {
			DashScopeAudioSpeechProperties.this.setInstruction(instruction);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".optimize-instructions")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable Boolean getOptimizeInstructions() {
			return DashScopeAudioSpeechProperties.this.getOptimizeInstructions();
		}

		public void setOptimizeInstructions(Boolean optimizeInstructions) {
			DashScopeAudioSpeechProperties.this.setOptimizeInstructions(optimizeInstructions);
		}

		@DeprecatedConfigurationProperty(replacement = CONFIG_PREFIX + ".language-type")
		@Deprecated(since = "2.0.0", forRemoval = true)
		public @Nullable String getLanguageType() {
			return DashScopeAudioSpeechProperties.this.getLanguageType();
		}

		public void setLanguageType(String languageType) {
			DashScopeAudioSpeechProperties.this.setLanguageType(languageType);
		}

	}


}
