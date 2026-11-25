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
package com.alibaba.cloud.ai.memory.mem0.model;

import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionTextParser;

import org.jspecify.annotations.Nullable;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Mem0ServerRequest {

	public static class Message {

		private String role = "";

		private String content = "";

		public Message() {
		}

		public Message(String role, String content) {
			this.role = Objects.requireNonNullElse(role, "");
			this.content = Objects.requireNonNullElse(content, "");
		}

		private Message(Builder builder) {
			setRole(builder.role);
			setContent(builder.content);
		}

		public String getRole() {
			return role;
		}

		public void setRole(String role) {
			this.role = role;
		}

		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}

		public static final class Builder {

			private String role = "";

			private String content = "";

			private Builder() {
			}

			public static Builder builder() {
				return new Builder();
			}

			public Builder role(String val) {
				role = val;
				return this;
			}

			public Builder content(String val) {
				content = val;
				return this;
			}

			public Message build() {
				return new Message(this);
			}

		}

	}

	public static class MemoryCreate implements Serializable {

		private List<Message> messages = List.of();

		@JsonProperty("user_id")
		private @Nullable String userId;

		@JsonProperty("agent_id")
		private @Nullable String agentId;

		@JsonProperty("run_id")
		private @Nullable String runId;

		private Map<String, Object> metadata = Map.of();

		public MemoryCreate() {
		}

		private MemoryCreate(Builder builder) {
			setMessages(Objects.requireNonNullElse(builder.messages, List.of()));
			setUserId(builder.userId);
			setAgentId(builder.agentId);
			setRunId(builder.runId);
			setMetadata(Objects.requireNonNullElse(builder.metadata, Map.of()));
		}

		public static Builder builder() {
			return new Builder();
		}

		public List<Message> getMessages() {
			return messages;
		}

		public void setMessages(List<Message> messages) {
			this.messages = messages;
		}

		public @Nullable String getUserId() {
			return userId;
		}

		public void setUserId(@Nullable String userId) {
			this.userId = userId;
		}

		public @Nullable String getAgentId() {
			return agentId;
		}

		public void setAgentId(@Nullable String agentId) {
			this.agentId = agentId;
		}

		public @Nullable String getRunId() {
			return runId;
		}

		public void setRunId(@Nullable String runId) {
			this.runId = runId;
		}

		public Map<String, Object> getMetadata() {
			return metadata;
		}

		public void setMetadata(Map<String, Object> metadata) {
			this.metadata = metadata;
		}

		public static final class Builder {

			private List<Message> messages = List.of();

			private @Nullable String userId;

			private @Nullable String agentId;

			private @Nullable String runId;

			private Map<String, Object> metadata = Map.of();

			private Builder() {
			}

			public static Builder builder() {
				return new Builder();
			}

			public Builder messages(List<Message> val) {
				messages = val;
				return this;
			}

			public Builder userId(@Nullable String val) {
				userId = val;
				return this;
			}

			public Builder agentId(@Nullable String val) {
				agentId = val;
				return this;
			}

			public Builder runId(@Nullable String val) {
				runId = val;
				return this;
			}

			public Builder metadata(Map<String, Object> val) {
				metadata = val;
				return this;
			}

			public MemoryCreate build() {
				return new MemoryCreate(this);
			}

		}

	}

	public static class SearchRequest extends org.springframework.ai.vectorstore.SearchRequest implements Serializable {

		private String query = "";

		@JsonProperty("user_id")
		private @Nullable String userId;

		@JsonProperty("run_id")
		private @Nullable String runId;

		@JsonProperty("agent_id")
		private @Nullable String agentId;

		private @Nullable Map<String, Object> filters;

		public SearchRequest() {
		}

		private SearchRequest(Builder builder, org.springframework.ai.vectorstore.SearchRequest.Builder springaiBuilder) {
			super(springaiBuilder.build());
			this.query = builder.query;
			this.agentId = builder.agentId;
			this.userId = builder.userId;
			this.runId = builder.runId;
			this.filters = builder.filters;
		}

		public static Builder mem0Builder() {
			return new Builder();
		}

		@Override
		public String getQuery() {
			return query;
		}

		public void setQuery(String query) {
			this.query = query;
		}

		public @Nullable String getUserId() {
			return userId;
		}

		public void setUserId(@Nullable String userId) {
			this.userId = userId;
		}

		public @Nullable String getRunId() {
			return runId;
		}

		public void setRunId(@Nullable String runId) {
			this.runId = runId;
		}

		public @Nullable String getAgentId() {
			return agentId;
		}

		public void setAgentId(@Nullable String agentId) {
			this.agentId = agentId;
		}

		public @Nullable Map<String, Object> getFilters() {
			return filters;
		}

		public void setFilters(@Nullable Map<String, Object> filters) {
			this.filters = filters;
		}

		public static class Builder {
			private String query = "";
			private int topK = 4;
			private double similarityThreshold = (double) 0.0F;

            private Filter.@Nullable Expression filterExpression;

			private @Nullable String userId;

			private @Nullable String runId;

			private @Nullable String agentId;

			private @Nullable Map<String, Object> filters;

			public Builder query(String val) {
				query = val;
				return this;
			}

			public Builder userId(@Nullable String val) {
				userId = val;
				return this;
			}

			public Builder runId(@Nullable String val) {
				runId = val;
				return this;
			}

			public Builder agentId(@Nullable String val) {
				agentId = val;
				return this;
			}

			public Builder filters(@Nullable Map<String, Object> val) {
				filters = val;
				return this;
			}

			public Builder topK(int topK) {
				Assert.isTrue(topK >= 0, "TopK should be positive.");
				this.topK = topK;
				return this;
			}

			public Builder similarityThreshold(double threshold) {
				Assert.isTrue(threshold >= (double) 0.0F && threshold <= (double) 1.0F, "Similarity threshold must be in [0,1] range.");
				this.similarityThreshold = threshold;
				return this;
			}

			public Builder similarityThresholdAll() {
				this.similarityThreshold = (double) 0.0F;
				return this;
			}

			public Builder filterExpression(Filter.@Nullable Expression expression) {
				this.filterExpression = expression;
				return this;
			}

			public Builder filterExpression(@Nullable String textExpression) {
				this.filterExpression = textExpression != null ? (new FilterExpressionTextParser()).parse(textExpression) : null;
				return this;
			}

			public SearchRequest build() {
				org.springframework.ai.vectorstore.SearchRequest.Builder springaiBuilder
						= org.springframework.ai.vectorstore.SearchRequest
						.builder()
						.filterExpression(this.filterExpression)
						.similarityThreshold(this.similarityThreshold)
						.topK(this.topK).query(this.query)
						.topK(this.topK);
				return new SearchRequest(this, springaiBuilder);
			}

		}

	}

}
