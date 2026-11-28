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

import java.time.Duration;

import com.alibaba.cloud.ai.dashscope.api.DashScopeHttpTransport;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties("spring.ai.dashscope.chat.http")
public record DashScopeChatHttpProperties(@DefaultValue("64") int maxConnections,
		@DefaultValue("64") int pendingAcquireMaxCount, @DefaultValue("2s") Duration acquireTimeout,
		@DefaultValue("30s") Duration maxIdleTime, @DefaultValue("10m") Duration maxLifeTime,
		@DefaultValue("15s") Duration evictionInterval, @DefaultValue("10s") Duration connectTimeout,
		@DefaultValue("60s") Duration firstChunkTimeout, @DefaultValue("60s") Duration streamReadTimeout,
		@DefaultValue("5m") Duration requestTimeout) {

	DashScopeHttpTransport.Settings settings() {
		return new DashScopeHttpTransport.Settings(maxConnections, pendingAcquireMaxCount, acquireTimeout, maxIdleTime,
				maxLifeTime, evictionInterval, connectTimeout, firstChunkTimeout, streamReadTimeout, requestTimeout);
	}
}
