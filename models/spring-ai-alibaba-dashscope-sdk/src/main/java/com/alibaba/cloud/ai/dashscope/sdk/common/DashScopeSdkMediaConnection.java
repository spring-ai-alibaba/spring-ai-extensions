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

package com.alibaba.cloud.ai.dashscope.sdk.common;

import java.util.Objects;

import com.alibaba.cloud.ai.dashscope.common.DashScopeApiConstants;
import org.jspecify.annotations.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Connection settings for model protocols shared with the DashScope HTTP adapter.
 */
public final class DashScopeSdkMediaConnection {

	private DashScopeSdkMediaConnection() {
	}

	public static String apiKey(@Nullable String configuredKey) {
		String key = StringUtils.hasText(configuredKey) ? configuredKey : System.getenv("DASHSCOPE_API_KEY");
		Assert.hasText(key, "A DashScope API key is required");
		return Objects.requireNonNull(key);
	}

	public static String baseUrl(@Nullable String workspaceId) {
		if (!StringUtils.hasText(workspaceId)) {
			return DashScopeApiConstants.DEFAULT_BASE_URL;
		}
		Assert.isTrue(workspaceId.matches("[a-zA-Z0-9-]+"), "Invalid DashScope workspace ID");
		return "https://" + workspaceId + ".cn-beijing.maas.aliyuncs.com";
	}

}
