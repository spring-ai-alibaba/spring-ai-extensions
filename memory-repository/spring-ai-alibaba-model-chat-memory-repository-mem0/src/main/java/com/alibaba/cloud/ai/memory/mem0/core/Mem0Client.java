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
package com.alibaba.cloud.ai.memory.mem0.core;

/**
 * @author yingzi
 * @since 2025/9/14
 */

public class Mem0Client {

	private String baseUrl = "http://localhost:8888";

	private boolean enableCache = true;

	private int timeoutSeconds = 30;

	private int maxRetryAttempts = 3;

	private AsyncConfig async = new AsyncConfig();

	// 私有构造函数，防止直接实例化
	private Mem0Client() {
	}

	// 私有构造函数，用于从 Builder 创建实例
	private Mem0Client(Builder builder) {
		this.baseUrl = builder.baseUrl;
		this.enableCache = builder.enableCache;
		this.timeoutSeconds = builder.timeoutSeconds;
		this.maxRetryAttempts = builder.maxRetryAttempts;
		this.async = builder.async;
	}

	public static class AsyncConfig {

		private boolean enabled = true;

		private int corePoolSize = 2;

		private int maxPoolSize = 4;

		private int queueCapacity = 100;

		private String threadNamePrefix = "mem0-async-";

		public boolean isEnabled() {
			return enabled;
		}

		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}

		public int getCorePoolSize() {
			return corePoolSize;
		}

		public void setCorePoolSize(int corePoolSize) {
			this.corePoolSize = corePoolSize;
		}

		public int getMaxPoolSize() {
			return maxPoolSize;
		}

		public void setMaxPoolSize(int maxPoolSize) {
			this.maxPoolSize = maxPoolSize;
		}

		public int getQueueCapacity() {
			return queueCapacity;
		}

		public void setQueueCapacity(int queueCapacity) {
			this.queueCapacity = queueCapacity;
		}

		public String getThreadNamePrefix() {
			return threadNamePrefix;
		}

		public void setThreadNamePrefix(String threadNamePrefix) {
			this.threadNamePrefix = threadNamePrefix;
		}

	}


	public AsyncConfig getAsync() {
		return async;
	}

	public void setAsync(AsyncConfig async) {
		this.async = async;
	}

	public static Builder builder() {
		return new Builder();
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public boolean isEnableCache() {
		return enableCache;
	}

	public void setEnableCache(boolean enableCache) {
		this.enableCache = enableCache;
	}

	public int getTimeoutSeconds() {
		return timeoutSeconds;
	}

	public void setTimeoutSeconds(int timeoutSeconds) {
		this.timeoutSeconds = timeoutSeconds;
	}

	public int getMaxRetryAttempts() {
		return maxRetryAttempts;
	}

	public void setMaxRetryAttempts(int maxRetryAttempts) {
		this.maxRetryAttempts = maxRetryAttempts;
	}

	public static class Builder {

		private String baseUrl = "http://localhost:8888";

		private boolean enableCache = true;

		private int timeoutSeconds = 30;

		private int maxRetryAttempts = 3;

		private AsyncConfig async = new AsyncConfig();

		private Builder() {
		}

		public Builder baseUrl(String baseUrl) {
			this.baseUrl = baseUrl;
			return this;
		}

		public Builder enableCache(boolean enableCache) {
			this.enableCache = enableCache;
			return this;
		}

		public Builder timeoutSeconds(int timeoutSeconds) {
			this.timeoutSeconds = timeoutSeconds;
			return this;
		}

		public Builder maxRetryAttempts(int maxRetryAttempts) {
			this.maxRetryAttempts = maxRetryAttempts;
			return this;
		}

		public Builder async(AsyncConfig async) {
			this.async = async;
			return this;
		}

		public Mem0Client build() {
			return new Mem0Client(this);
		}

	}

}
