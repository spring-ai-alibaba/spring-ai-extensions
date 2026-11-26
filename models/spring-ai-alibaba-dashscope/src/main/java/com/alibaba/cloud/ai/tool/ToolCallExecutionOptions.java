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
package com.alibaba.cloud.ai.tool;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.jspecify.annotations.Nullable;
import org.springframework.ai.model.tool.DefaultToolCallingManager;
import org.springframework.ai.model.tool.ToolCallConfirmationPolicy;
import org.springframework.ai.model.tool.ToolCallLimitBehavior;
import org.springframework.util.Assert;

/**
 * Confirmation, resolution and per-turn limits shared by the Alibaba tool managers.
 */
public record ToolCallExecutionOptions(ToolCallConfirmationPolicy confirmationPolicy,
		boolean resolutionFallbackEnabled, int maxCallsPerTool, Map<String, Integer> perToolLimits,
		Set<String> excludedTools, int maxTotalCalls, ToolCallLimitBehavior onLimitExceeded) {

	public ToolCallExecutionOptions {
		Assert.notNull(confirmationPolicy, "confirmationPolicy cannot be null");
		Assert.notNull(onLimitExceeded, "onLimitExceeded cannot be null");
		Assert.isTrue(maxCallsPerTool >= -1 && maxTotalCalls >= -1, "Tool call limits must be at least -1");
		perToolLimits = Map.copyOf(perToolLimits);
		Assert.isTrue(perToolLimits.values().stream().allMatch(limit -> limit >= -1),
				"Per-tool call limits must be at least -1");
		excludedTools = Set.copyOf(excludedTools);
	}

	public static ToolCallExecutionOptions defaults() {
		return new ToolCallExecutionOptions(ToolCallConfirmationPolicy.fromToolMetadata(), false,
				DefaultToolCallingManager.DEFAULT_MAX_CALLS_PER_TOOL, Map.of(), Set.of(),
				DefaultToolCallingManager.DEFAULT_MAX_TOTAL_TOOL_CALLS, ToolCallLimitBehavior.THROW);
	}

	public static ToolCallExecutionOptions configured(boolean confirmationEnabled, List<String> confirmationTools,
			List<ToolCallConfirmationPolicy> policies, boolean fallbackEnabled, @Nullable Integer maxCallsPerTool,
			Map<String, Integer> perToolLimits, List<String> excludedTools, @Nullable Integer maxTotalCalls,
			ToolCallLimitBehavior onLimitExceeded) {
		ToolCallConfirmationPolicy policy = confirmationEnabled
				? ToolCallConfirmationPolicy.fromToolMetadata() : ToolCallConfirmationPolicy.none();
		if (!confirmationTools.isEmpty()) {
			policy = policy.or(ToolCallConfirmationPolicy.forTools(confirmationTools.toArray(String[]::new)));
		}
		for (ToolCallConfirmationPolicy configuredPolicy : policies) {
			policy = policy.or(configuredPolicy);
		}
		return new ToolCallExecutionOptions(policy, fallbackEnabled,
				Objects.requireNonNullElse(maxCallsPerTool, DefaultToolCallingManager.DEFAULT_MAX_CALLS_PER_TOOL),
				perToolLimits, Set.copyOf(excludedTools),
				Objects.requireNonNullElse(maxTotalCalls, DefaultToolCallingManager.DEFAULT_MAX_TOTAL_TOOL_CALLS),
				onLimitExceeded);
	}

}
