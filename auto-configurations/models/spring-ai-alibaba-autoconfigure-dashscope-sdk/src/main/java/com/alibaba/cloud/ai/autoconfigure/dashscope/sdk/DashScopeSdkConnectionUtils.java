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

import org.springframework.lang.NonNull;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Connection property resolver for DashScope SDK module.
 */
public final class DashScopeSdkConnectionUtils {

	private static final String AI_DASHSCOPE_API_KEY = "AI_DASHSCOPE_API_KEY";

	private DashScopeSdkConnectionUtils() {
	}

	@NonNull
	public static ResolvedConnectionProperties resolveConnectionProperties(
			DashScopeSdkParentProperties commonProperties,
			DashScopeSdkParentProperties modelProperties) {

		String apiKey = StringUtils.hasText(modelProperties.getApiKey()) ? modelProperties.getApiKey()
				: commonProperties.getApiKey();

		String workspaceId = StringUtils.hasText(modelProperties.getWorkspaceId()) ? modelProperties.getWorkspaceId()
				: commonProperties.getWorkspaceId();

		if (Objects.isNull(apiKey) && Objects.nonNull(System.getenv(AI_DASHSCOPE_API_KEY))) {
			apiKey = System.getenv(AI_DASHSCOPE_API_KEY);
		}

		Assert.hasText(apiKey,
				"DashScope SDK API key must be set. Use spring.ai.dashscope.sdk.api-key or spring.ai.dashscope.sdk.chat.api-key");

		Map<String, String> headers = new HashMap<>();
		if (StringUtils.hasText(workspaceId)) {
			headers.put("DashScope-Workspace", workspaceId);
		}

		return new ResolvedConnectionProperties(apiKey, workspaceId, headers);
	}

}
