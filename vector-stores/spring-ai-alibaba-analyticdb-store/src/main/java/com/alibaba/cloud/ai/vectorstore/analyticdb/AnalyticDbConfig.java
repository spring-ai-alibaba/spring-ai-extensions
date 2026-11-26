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
package com.alibaba.cloud.ai.vectorstore.analyticdb;

import org.jspecify.annotations.Nullable;

/**
 * @author HeYQ
 * @since 2024-10-23 20:22
 */
public class AnalyticDbConfig {

	private @Nullable String accessKeyId;

	private @Nullable String accessKeySecret;

	private @Nullable String regionId;

	private @Nullable String dbInstanceId;

	private @Nullable String managerAccount;

	private @Nullable String managerAccountPassword;

	private @Nullable String namespace;

	private @Nullable String namespacePassword;

	private String metrics = "cosine";

	private Integer readTimeout = 60000;

	private String userAgent = "index";

	public AnalyticDbConfig() {
	}

	public AnalyticDbConfig(@Nullable String accessKeyId, @Nullable String accessKeySecret, @Nullable String regionId,
			@Nullable String dbInstanceId, @Nullable String managerAccount, @Nullable String managerAccountPassword,
			@Nullable String namespace, @Nullable String namespacePassword,
			String metrics, Integer readTimeout, String userAgent) {
		this.accessKeyId = accessKeyId;
		this.accessKeySecret = accessKeySecret;
		this.regionId = regionId;
		this.dbInstanceId = dbInstanceId;
		this.managerAccount = managerAccount;
		this.managerAccountPassword = managerAccountPassword;
		this.namespace = namespace;
		this.namespacePassword = namespacePassword;
		this.metrics = metrics;
		this.readTimeout = readTimeout;
		this.userAgent = userAgent;
	}

	public @Nullable String getAccessKeyId() {
		return accessKeyId;
	}

	public AnalyticDbConfig setAccessKeyId(@Nullable String accessKeyId) {
		this.accessKeyId = accessKeyId;
		return this;
	}

	public @Nullable String getAccessKeySecret() {
		return accessKeySecret;
	}

	public AnalyticDbConfig setAccessKeySecret(@Nullable String accessKeySecret) {
		this.accessKeySecret = accessKeySecret;
		return this;
	}

	public @Nullable String getRegionId() {
		return regionId;
	}

	public AnalyticDbConfig setRegionId(@Nullable String regionId) {
		this.regionId = regionId;
		return this;
	}

	public @Nullable String getDbInstanceId() {
		return dbInstanceId;
	}

	public AnalyticDbConfig setDbInstanceId(@Nullable String dbInstanceId) {
		this.dbInstanceId = dbInstanceId;
		return this;
	}

	public @Nullable String getManagerAccount() {
		return managerAccount;
	}

	public AnalyticDbConfig setManagerAccount(@Nullable String managerAccount) {
		this.managerAccount = managerAccount;
		return this;
	}

	public @Nullable String getManagerAccountPassword() {
		return managerAccountPassword;
	}

	public AnalyticDbConfig setManagerAccountPassword(@Nullable String managerAccountPassword) {
		this.managerAccountPassword = managerAccountPassword;
		return this;
	}

	public @Nullable String getNamespace() {
		return namespace;
	}

	public AnalyticDbConfig setNamespace(@Nullable String namespace) {
		this.namespace = namespace;
		return this;
	}

	public @Nullable String getNamespacePassword() {
		return namespacePassword;
	}

	public AnalyticDbConfig setNamespacePassword(@Nullable String namespacePassword) {
		this.namespacePassword = namespacePassword;
		return this;
	}

	public String getMetrics() {
		return metrics;
	}

	public AnalyticDbConfig setMetrics(String metrics) {
		this.metrics = metrics;
		return this;
	}

	public Integer getReadTimeout() {
		return readTimeout;
	}

	public AnalyticDbConfig setReadTimeout(Integer readTimeout) {
		this.readTimeout = readTimeout;
		return this;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public AnalyticDbConfig setUserAgent(String userAgent) {
		this.userAgent = userAgent;
		return this;
	}

}
