/*
 * Copyright 2025-2026 the original author or authors.
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
 *
 */

package com.alibaba.cloud.ai.mcp.router.model;

import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class McpServerInfo {

	private @Nullable String name;

	private @Nullable String description;

	private @Nullable String protocol;

	private @Nullable String version;

	private @Nullable String endpoint;

	private @Nullable Boolean enabled;

	private @Nullable List<String> tags;

	// 临时分数字段，用于向量搜索结果排序，不参与 equals 和 hashCode
	private transient double score;

	public McpServerInfo() {
	}

	public McpServerInfo(@Nullable String name, @Nullable String description, @Nullable String protocol,
			@Nullable String version, @Nullable String endpoint, @Nullable Boolean enabled,
			@Nullable List<String> tags) {
		this.name = name;
		this.description = description;
		this.protocol = protocol;
		this.version = version;
		this.endpoint = endpoint;
		this.enabled = enabled;
		this.tags = tags;
	}

	public @Nullable String getName() {
		return name;
	}

	public void setName(@Nullable String name) {
		this.name = name;
	}

	public @Nullable String getDescription() {
		return description;
	}

	public void setDescription(@Nullable String description) {
		this.description = description;
	}

	public @Nullable String getProtocol() {
		return protocol;
	}

	public void setProtocol(@Nullable String protocol) {
		this.protocol = protocol;
	}

	public @Nullable String getVersion() {
		return version;
	}

	public void setVersion(@Nullable String version) {
		this.version = version;
	}

	public @Nullable String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(@Nullable String endpoint) {
		this.endpoint = endpoint;
	}

	public @Nullable Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(@Nullable Boolean enabled) {
		this.enabled = enabled;
	}

	public @Nullable List<String> getTags() {
		return tags;
	}

	public void setTags(@Nullable List<String> tags) {
		this.tags = tags;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}

	@Override
	public String toString() {
		return "McpServerInfo{" + "name='" + name + '\'' + ", description='" + description + '\'' + ", protocol='"
				+ protocol + '\'' + ", version='" + version + '\'' + ", endpoint='" + endpoint + '\'' + ", enabled="
				+ enabled + ", tags=" + tags + ", score=" + score + '}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		McpServerInfo that = (McpServerInfo) o;
		return Objects.equals(name, that.name) && Objects.equals(description, that.description)
				&& Objects.equals(protocol, that.protocol) && Objects.equals(version, that.version)
				&& Objects.equals(endpoint, that.endpoint) && Objects.equals(enabled, that.enabled)
				&& Objects.equals(tags, that.tags);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, description, protocol, version, endpoint, enabled, tags);
	}

}
