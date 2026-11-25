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

package com.alibaba.cloud.ai.dashscope.spec;

import java.util.List;

import org.jspecify.annotations.Nullable;

/** Selects the native DashScope generation endpoint for a chat request. */
public final class DashScopeChatEndpoints {

	private static final List<String> MULTIMODAL_MODELS = List.of(
			"qwen4-max", "qwen3.8-max", "qwen3.8-flash", "qwen3.8-omni-flash", "qwen3.8-27b",
			"qwen3.7-plus", "qwen3.7-flash", "qwen3.7-max-2026-06-08",
			"qwen3.6-plus", "qwen3.6-flash", "qwen3.6-27b", "qwen3.6-35b-a3b",
			"qwen3.5-plus", "qwen3.5-flash", "qwen3.5-omni-plus", "qwen3.5-omni-flash",
			"qwen3.5-122b-a10b", "qwen3.5-27b", "qwen3.5-35b-a3b", "qwen3.5-397b-a17b",
			"qwen-vl", "qwen2.5-vl", "qwen3-vl", "qwen-omni", "qwen3-omni", "qwen-audio",
			"kimi-k3.1-pro", "kimi-k3", "kimi-k2.7-code", "kimi-k2.6", "kimi-k2.5");

	private DashScopeChatEndpoints() {
	}

	public static boolean supportsEncryptedThinking(@Nullable String model) {
		return model != null && (model.equals("qwen4-max") || model.startsWith("qwen4-max-"));
	}

	public static boolean isMultimodal(@Nullable String model, @Nullable Boolean override) {
		if (override != null) {
			return override;
		}
		return model != null && MULTIMODAL_MODELS.stream()
			.anyMatch(family -> model.equals(family) || model.startsWith(family + "-"));
	}

}
