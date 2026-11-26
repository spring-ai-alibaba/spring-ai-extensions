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

package com.alibaba.cloud.ai.model.chat.memory.tablestore.autoconfigure;

import org.jspecify.annotations.Nullable;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for Tablestore chat memory.
 */
@ConfigurationProperties(prefix = TablestoreChatMemoryProperties.CONFIG_PREFIX)
public class TablestoreChatMemoryProperties {

	public static final String CONFIG_PREFIX = "spring.ai.chat.memory.repository.tablestore";

	private @Nullable String endpoint;

	private @Nullable String instanceName;

	private @Nullable String accessKeyId;

	private @Nullable String accessKeySecret;

	private String sessionTableName = "session";

	private String sessionSecondaryIndexName = "session_secondary_index";

	private String messageTableName = "message";

	private String messageSecondaryIndexName = "message_secondary_index";

	public @Nullable String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(@Nullable String endpoint) {
		this.endpoint = endpoint;
	}

	public @Nullable String getInstanceName() {
		return instanceName;
	}

	public void setInstanceName(@Nullable String instanceName) {
		this.instanceName = instanceName;
	}

	public @Nullable String getAccessKeyId() {
		return accessKeyId;
	}

	public void setAccessKeyId(@Nullable String accessKeyId) {
		this.accessKeyId = accessKeyId;
	}

	public @Nullable String getAccessKeySecret() {
		return accessKeySecret;
	}

	public void setAccessKeySecret(@Nullable String accessKeySecret) {
		this.accessKeySecret = accessKeySecret;
	}

	public String getSessionTableName() {
		return sessionTableName;
	}

	public void setSessionTableName(String sessionTableName) {
		this.sessionTableName = sessionTableName;
	}

	public String getSessionSecondaryIndexName() {
		return sessionSecondaryIndexName;
	}

	public void setSessionSecondaryIndexName(String sessionSecondaryIndexName) {
		this.sessionSecondaryIndexName = sessionSecondaryIndexName;
	}

	public String getMessageTableName() {
		return messageTableName;
	}

	public void setMessageTableName(String messageTableName) {
		this.messageTableName = messageTableName;
	}

	public String getMessageSecondaryIndexName() {
		return messageSecondaryIndexName;
	}

	public void setMessageSecondaryIndexName(String messageSecondaryIndexName) {
		this.messageSecondaryIndexName = messageSecondaryIndexName;
	}

}
