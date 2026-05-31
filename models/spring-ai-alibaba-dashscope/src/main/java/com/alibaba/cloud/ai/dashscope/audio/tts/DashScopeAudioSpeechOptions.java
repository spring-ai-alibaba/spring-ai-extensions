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
package com.alibaba.cloud.ai.dashscope.audio.tts;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.alibaba.cloud.ai.dashscope.audio.AudioCommonType.TextType;
import com.alibaba.cloud.ai.dashscope.spec.DashScopeModel.AudioModel;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jspecify.annotations.Nullable;
import org.springframework.ai.audio.tts.TextToSpeechOptions;

/**
 * DashScope Audio Speech Options.
 *
 * @author kevinlin09
 * @author xuguan
 * @author yingzi
 */
public class DashScopeAudioSpeechOptions implements TextToSpeechOptions {

	public static final String DEFAULT_MODEL = AudioModel.SAMBERT_ZHICHU_V1.getValue();

	@JsonProperty("model")
	private final @Nullable String model;

	@JsonProperty("text_type")
	private final String textType;

	@JsonProperty("voice")
	private final String voice;

	@JsonProperty("format")
	private final @Nullable String format;

	@JsonProperty("sample_rate")
	private final @Nullable Integer sampleRate;

	@JsonProperty("volume")
	private final @Nullable Integer volume;

	@JsonProperty("rate")
	private final @Nullable Float rate;

	@JsonProperty("pitch")
	private final @Nullable Float pitch;

	@JsonProperty("enable_ssml")
	private final @Nullable Boolean enableSsml;

	@JsonProperty("bit_rate")
	private final @Nullable Integer bitRate;

	@JsonProperty("speed")
	private final @Nullable Double speed;

	@JsonProperty("seed")
	private final @Nullable Integer seed;

	@JsonProperty("word_timestamp_enabled")
	private final @Nullable Boolean wordTimestampEnabled;

	@JsonProperty("phoneme_timestamp_enabled")
	private final @Nullable Boolean phonemeTimestampEnabled;

	@JsonProperty("language_hints")
	private final @Nullable List<String> languageHints;

	@JsonProperty("instruction")
	private final @Nullable String instruction;

	@JsonProperty("optimize_instructions")
	private final @Nullable Boolean optimizeInstructions;

	@JsonProperty("enable_aigc_tag")
	private final @Nullable Boolean enableAigcTag;

	@JsonProperty("aigc_propagator")
	private final @Nullable String aigcPropagator;

	@JsonProperty("aigc_propagate_id")
	private final @Nullable String aigcPropagateId;

	@JsonProperty("language_type")
	private final @Nullable String languageType;

	protected DashScopeAudioSpeechOptions(@Nullable String model, @Nullable String textType, @Nullable String voice,
			@Nullable String format, @Nullable Integer sampleRate, @Nullable Integer volume, @Nullable Float rate,
			@Nullable Float pitch, @Nullable Boolean enableSsml, @Nullable Integer bitRate, @Nullable Double speed,
			@Nullable Integer seed, @Nullable Boolean wordTimestampEnabled, @Nullable Boolean phonemeTimestampEnabled,
			@Nullable List<String> languageHints, @Nullable String instruction,
			@Nullable Boolean optimizeInstructions, @Nullable Boolean enableAigcTag,
			@Nullable String aigcPropagator, @Nullable String aigcPropagateId, @Nullable String languageType) {
		this.model = model;
		this.textType = textType != null ? textType : TextType.PLAIN_TEXT.getValue();
		this.voice = voice != null ? voice : "longanyang";
		this.format = format;
		this.sampleRate = sampleRate;
		this.volume = volume;
		this.rate = rate;
		this.pitch = pitch;
		this.enableSsml = enableSsml;
		this.bitRate = bitRate;
		this.speed = speed;
		this.seed = seed;
		this.wordTimestampEnabled = wordTimestampEnabled;
		this.phonemeTimestampEnabled = phonemeTimestampEnabled;
		this.languageHints = languageHints != null ? new ArrayList<>(languageHints) : null;
		this.instruction = instruction;
		this.optimizeInstructions = optimizeInstructions;
		this.enableAigcTag = enableAigcTag;
		this.aigcPropagator = aigcPropagator;
		this.aigcPropagateId = aigcPropagateId;
		this.languageType = languageType;
	}

	@Override
	public @Nullable String getModel() {
		return this.model;
	}

	@Override
	public String getVoice() {
		return this.voice;
	}

	public String getTextType() {
		return this.textType;
	}

	@Override
	public @Nullable String getFormat() {
		return this.format;
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

	public @Nullable Boolean getEnableSsml() {
		return this.enableSsml;
	}

	public @Nullable Integer getBitRate() {
		return this.bitRate;
	}

	@Override
	public @Nullable Double getSpeed() {
		return this.speed;
	}

	public @Nullable Integer getSeed() {
		return this.seed;
	}

	public @Nullable Boolean getWordTimestampEnabled() {
		return this.wordTimestampEnabled;
	}

	public @Nullable Boolean getPhonemeTimestampEnabled() {
		return this.phonemeTimestampEnabled;
	}

	public @Nullable List<String> getLanguageHints() {
		return this.languageHints;
	}

	public @Nullable String getInstruction() {
		return this.instruction;
	}

	public @Nullable Boolean getOptimizeInstructions() {
		return this.optimizeInstructions;
	}

	public @Nullable Boolean getEnableAigcTag() {
		return this.enableAigcTag;
	}

	public @Nullable String getAigcPropagator() {
		return this.aigcPropagator;
	}

	public @Nullable String getAigcPropagateId() {
		return this.aigcPropagateId;
	}

	public @Nullable String getLanguageType() {
		return this.languageType;
	}

    @Override
    public DashScopeAudioSpeechOptions copy() {
        return mutate().build();
    }

    public Builder mutate() {
        return builder()
                .model(this.model)
                .textType(this.textType)
                .voice(this.voice)
                .format(this.format)
                .sampleRate(this.sampleRate)
                .volume(this.volume)
                .rate(this.rate)
                .pitch(this.pitch)
                .enableSsml(this.enableSsml)
                .bitRate(this.bitRate)
                .speed(this.speed)
                .seed(this.seed)
                .wordTimestampEnabled(this.wordTimestampEnabled)
                .phonemeTimestampEnabled(this.phonemeTimestampEnabled)
                .languageHints(this.languageHints)
                .instruction(this.instruction)
                .optimizeInstructions(this.optimizeInstructions)
                .enableAigcTag(this.enableAigcTag)
                .aigcPropagator(this.aigcPropagator)
                .aigcPropagateId(this.aigcPropagateId)
                .languageType(this.languageType);
    }

    public static DashScopeAudioSpeechOptions fromOptions(DashScopeAudioSpeechOptions options) {
        return options.mutate().build();
    }

    public static Builder builder() {
        return new Builder();
    }

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		DashScopeAudioSpeechOptions that = (DashScopeAudioSpeechOptions) o;
		return Objects.equals(this.model, that.model) && Objects.equals(this.textType, that.textType)
				&& Objects.equals(this.voice, that.voice) && Objects.equals(this.format, that.format)
				&& Objects.equals(this.sampleRate, that.sampleRate) && Objects.equals(this.volume, that.volume)
				&& Objects.equals(this.rate, that.rate) && Objects.equals(this.pitch, that.pitch)
				&& Objects.equals(this.enableSsml, that.enableSsml) && Objects.equals(this.bitRate, that.bitRate)
				&& Objects.equals(this.speed, that.speed) && Objects.equals(this.seed, that.seed)
				&& Objects.equals(this.wordTimestampEnabled, that.wordTimestampEnabled)
				&& Objects.equals(this.phonemeTimestampEnabled, that.phonemeTimestampEnabled)
				&& Objects.equals(this.languageHints, that.languageHints)
				&& Objects.equals(this.instruction, that.instruction)
				&& Objects.equals(this.optimizeInstructions, that.optimizeInstructions)
				&& Objects.equals(this.enableAigcTag, that.enableAigcTag)
				&& Objects.equals(this.aigcPropagator, that.aigcPropagator)
				&& Objects.equals(this.aigcPropagateId, that.aigcPropagateId)
				&& Objects.equals(this.languageType, that.languageType);
	}

    // @formatter:off
	@Override
	public int hashCode() {
		return Objects.hash(this.model, this.textType, this.voice, this.format, this.sampleRate, this.volume, this.rate,
				this.pitch, this.enableSsml, this.bitRate, this.speed, this.seed, this.wordTimestampEnabled,
				this.phonemeTimestampEnabled, this.languageHints, this.instruction, this.optimizeInstructions,
				this.enableAigcTag, this.aigcPropagator, this.aigcPropagateId, this.languageType);
	}
    // @formatter:on

	@Override
	public String toString() {
		return "DashScopeAudioSpeechOptions{" + "model='" + this.model + '\'' + ", textType='" + this.textType + '\''
				+ ", voice='" + this.voice + '\'' + ", format='" + this.format + '\'' + ", sampleRate="
				+ this.sampleRate + ", volume=" + this.volume + ", rate=" + this.rate + ", pitch=" + this.pitch
				+ ", enableSsml=" + this.enableSsml + ", bitRate=" + this.bitRate + ", speed=" + this.speed
				+ ", seed=" + this.seed + ", wordTimestampEnabled=" + this.wordTimestampEnabled
				+ ", phonemeTimestampEnabled=" + this.phonemeTimestampEnabled + ", languageHints="
				+ this.languageHints + ", instruction='" + this.instruction + '\'' + ", optimizeInstructions="
				+ this.optimizeInstructions + ", enableAigcTag=" + this.enableAigcTag + ", aigcPropagator='"
				+ this.aigcPropagator + '\'' + ", aigcPropagateId='" + this.aigcPropagateId + '\''
				+ ", languageType='" + this.languageType + '\'' + '}';
	}

	/**
	 * Builder for DashScopeAudioSpeechOptions.
	 */
	public static class Builder {

		protected @Nullable String model;

		protected @Nullable String textType;

		protected @Nullable String voice;

		protected @Nullable String format;

		protected @Nullable Integer sampleRate;

		protected @Nullable Integer volume;

		protected @Nullable Float rate;

		protected @Nullable Float pitch;

		protected @Nullable Boolean enableSsml;

		protected @Nullable Integer bitRate;

		protected @Nullable Double speed;

		protected @Nullable Integer seed;

		protected @Nullable Boolean wordTimestampEnabled;

		protected @Nullable Boolean phonemeTimestampEnabled;

		protected @Nullable List<String> languageHints;

		protected @Nullable String instruction;

		protected @Nullable Boolean optimizeInstructions;

		protected @Nullable Boolean enableAigcTag;

		protected @Nullable String aigcPropagator;

		protected @Nullable String aigcPropagateId;

		protected @Nullable String languageType;

		public Builder() {
		}

		public Builder model(@Nullable String model) {
			this.model = model;
			return this;
		}

		public Builder textType(@Nullable String textType) {
			this.textType = textType;
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

		public Builder enableSsml(@Nullable Boolean enableSsml) {
			this.enableSsml = enableSsml;
			return this;
		}

		public Builder bitRate(@Nullable Integer bitRate) {
			this.bitRate = bitRate;
			return this;
		}

		public Builder speed(@Nullable Double speed) {
			this.speed = speed;
			return this;
		}

		public Builder seed(@Nullable Integer seed) {
			this.seed = seed;
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

		public Builder languageHints(@Nullable List<String> languageHints) {
			this.languageHints = languageHints;
			return this;
		}

		public Builder instruction(@Nullable String instruction) {
			this.instruction = instruction;
			return this;
		}

		public Builder optimizeInstructions(@Nullable Boolean optimizeInstructions) {
			this.optimizeInstructions = optimizeInstructions;
			return this;
		}

		public Builder enableAigcTag(@Nullable Boolean enableAigcTag) {
			this.enableAigcTag = enableAigcTag;
			return this;
		}

		public Builder aigcPropagator(@Nullable String aigcPropagator) {
			this.aigcPropagator = aigcPropagator;
			return this;
		}

		public Builder aigcPropagateId(@Nullable String aigcPropagateId) {
			this.aigcPropagateId = aigcPropagateId;
			return this;
		}

		public Builder languageType(@Nullable String languageType) {
			this.languageType = languageType;
			return this;
		}

        // @formatter:off
		public Builder from(DashScopeAudioSpeechOptions fromOptions) {
			this.model = fromOptions.getModel();
			this.textType = fromOptions.getTextType();
			this.voice = fromOptions.getVoice();
			this.format = fromOptions.getFormat();
			this.sampleRate = fromOptions.getSampleRate();
			this.volume = fromOptions.getVolume();
			this.rate = fromOptions.getRate();
			this.pitch = fromOptions.getPitch();
			this.enableSsml = fromOptions.getEnableSsml();
			this.bitRate = fromOptions.getBitRate();
			this.speed = fromOptions.getSpeed();
			this.seed = fromOptions.getSeed();
			this.wordTimestampEnabled = fromOptions.getWordTimestampEnabled();
			this.phonemeTimestampEnabled = fromOptions.getPhonemeTimestampEnabled();
			this.languageHints = fromOptions.getLanguageHints();
			this.instruction = fromOptions.getInstruction();
			this.optimizeInstructions = fromOptions.getOptimizeInstructions();
			this.enableAigcTag = fromOptions.getEnableAigcTag();
			this.aigcPropagator = fromOptions.getAigcPropagator();
			this.aigcPropagateId = fromOptions.getAigcPropagateId();
			this.languageType = fromOptions.getLanguageType();
			return this;
		}
        // @formatter:on

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
			if (from.getFormat() != null) {
				this.format = from.getFormat();
			}
			if (from.getSpeed() != null) {
				this.speed = from.getSpeed();
			}
			if (from instanceof DashScopeAudioSpeechOptions castFrom) {
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
				if (castFrom.getEnableSsml() != null) {
					this.enableSsml = castFrom.getEnableSsml();
				}
				if (castFrom.getBitRate() != null) {
					this.bitRate = castFrom.getBitRate();
				}
				if (castFrom.getSeed() != null) {
					this.seed = castFrom.getSeed();
				}
				if (castFrom.getWordTimestampEnabled() != null) {
					this.wordTimestampEnabled = castFrom.getWordTimestampEnabled();
				}
				if (castFrom.getPhonemeTimestampEnabled() != null) {
					this.phonemeTimestampEnabled = castFrom.getPhonemeTimestampEnabled();
				}
				if (castFrom.getLanguageHints() != null) {
					this.languageHints = castFrom.getLanguageHints();
				}
				if (castFrom.getInstruction() != null) {
					this.instruction = castFrom.getInstruction();
				}
				if (castFrom.getOptimizeInstructions() != null) {
					this.optimizeInstructions = castFrom.getOptimizeInstructions();
				}
				if (castFrom.getEnableAigcTag() != null) {
					this.enableAigcTag = castFrom.getEnableAigcTag();
				}
				if (castFrom.getAigcPropagator() != null) {
					this.aigcPropagator = castFrom.getAigcPropagator();
				}
				if (castFrom.getAigcPropagateId() != null) {
					this.aigcPropagateId = castFrom.getAigcPropagateId();
				}
				if (castFrom.getLanguageType() != null) {
					this.languageType = castFrom.getLanguageType();
				}
			}
			return this;
		}

        // @formatter:off
		public DashScopeAudioSpeechOptions build() {
			return new DashScopeAudioSpeechOptions(this.model, this.textType, this.voice, this.format, this.sampleRate,
					this.volume, this.rate, this.pitch, this.enableSsml, this.bitRate, this.speed, this.seed,
					this.wordTimestampEnabled, this.phonemeTimestampEnabled, this.languageHints, this.instruction,
					this.optimizeInstructions, this.enableAigcTag, this.aigcPropagator, this.aigcPropagateId,
					this.languageType);
		}
        // @formatter:on

	}

}
