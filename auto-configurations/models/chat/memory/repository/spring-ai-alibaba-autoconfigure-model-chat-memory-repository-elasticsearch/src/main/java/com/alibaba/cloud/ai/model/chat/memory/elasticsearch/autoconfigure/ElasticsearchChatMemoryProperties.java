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

package com.alibaba.cloud.ai.model.chat.memory.elasticsearch.autoconfigure;

import org.jspecify.annotations.Nullable;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * Configuration properties for ElasticSearch chat memory.
 */
@ConfigurationProperties(prefix = ElasticsearchChatMemoryProperties.CONFIG_PREFIX)
public class ElasticsearchChatMemoryProperties {

	public static final String CONFIG_PREFIX = "spring.ai.chat.memory.repository.elasticsearch";
	/**
	 * Elasticsearch host URL
	 */
	private String host = "localhost";

	/**
	 * Elasticsearch port
	 */
	private int port = 9200;

	/**
	 * List of cluster nodes in format: hostname:port
	 */
	private List<String> nodes = new ArrayList<>();

	/**
	 * Index name to query
	 */
	private @Nullable String index;

	/**
	 * Query field to search in
	 */
	private String queryField = "content";

	/**
	 * Username for authentication (optional)
	 */
	private @Nullable String username;

	/**
	 * Password for authentication (optional)
	 */
	private @Nullable String password;

	/**
	 * Maximum number of documents to retrieve
	 */
	private int maxResults = 10;

	/**
	 * Connection scheme (http/https)
	 */
	private String scheme = "http";

	public String getHost() {
		return host;
	}

	public void setHost(final String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(final int port) {
		this.port = port;
	}

	public List<String> getNodes() {
		return nodes;
	}

	public void setNodes(final List<String> nodes) {
		this.nodes = nodes;
	}

	public @Nullable String getIndex() {
		return index;
	}

	public void setIndex(final @Nullable String index) {
		this.index = index;
	}

	public String getQueryField() {
		return queryField;
	}

	public void setQueryField(final String queryField) {
		this.queryField = queryField;
	}

	public @Nullable String getUsername() {
		return username;
	}

	public void setUsername(final @Nullable String username) {
		this.username = username;
	}

	public @Nullable String getPassword() {
		return password;
	}

	public void setPassword(final @Nullable String password) {
		this.password = password;
	}

	public int getMaxResults() {
		return maxResults;
	}

	public void setMaxResults(final int maxResults) {
		this.maxResults = maxResults;
	}

	public String getScheme() {
		return scheme;
	}

	public void setScheme(final String scheme) {
		this.scheme = scheme;
	}

}
