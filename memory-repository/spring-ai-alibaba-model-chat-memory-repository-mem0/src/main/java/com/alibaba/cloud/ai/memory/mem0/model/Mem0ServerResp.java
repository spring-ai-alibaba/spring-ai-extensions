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

import org.jspecify.annotations.Nullable;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Mem0ServerResp {

	// Relationship data
	private List<Mem0Results> results;

	// Relationship data
	private List<Mem0Relation> relations;

	public Mem0ServerResp() {
		this.relations = new ArrayList<>();
		this.results = new ArrayList<>();
	}

	public Mem0ServerResp(List<Mem0Results> results, List<Mem0Relation> relations) {
		this.results = results;
		this.relations = relations;
	}

	public List<Mem0Results> getResults() {
		return results;
	}

	public void setResults(List<Mem0Results> results) {
		this.results = results;
	}

	public List<Mem0Relation> getRelations() {
		return relations;
	}

	public void setRelations(List<Mem0Relation> relations) {
		this.relations = relations;
	}

	/**
	 * Mem0 Relationship Data Model Corresponds to each relationship object in the
	 * relations array returned by the Mem0 service
	 */
	public static class Mem0Relation {

		private String source = ""; // Source node

		private String relationship = ""; // Relationship type

		private @Nullable String target; // Target path

		private @Nullable String destination; // Destination

		// Default constructor
		public Mem0Relation() {
		}

		// Full constructor
		public Mem0Relation(String source, String relationship, @Nullable String target, @Nullable String destination) {
			this.source = source;
			this.relationship = relationship;
			this.target = target;
			this.destination = destination;
		}

		// Getters and Setters
		public String getSource() {
			return source;
		}

		public void setSource(String source) {
			this.source = source;
		}

		public String getRelationship() {
			return relationship;
		}

		public void setRelationship(String relationship) {
			this.relationship = relationship;
		}

		public @Nullable String getTarget() {
			return target;
		}

		public void setTarget(@Nullable String target) {
			this.target = target;
		}

		public @Nullable String getDestination() {
			return destination;
		}

		public void setDestination(@Nullable String destination) {
			this.destination = destination;
		}

		@Override
		public String toString() {
			return "Mem0Relation{" + "source='" + source + '\'' + ", relationship='" + relationship + '\''
					+ ", target='" + target + '\'' + ", destination='" + destination + '\'' + '}';
		}

	}

	public static class Mem0Results {

		private String id = "";

		private String memory = ""; // Actual memory content

		private String hash = "";

		private Map<String, Object> metadata = Map.of();

		@JsonProperty("user_id")
		private @Nullable String userId;

		@JsonProperty("created_at")
		private @Nullable ZonedDateTime createdAt;

		@JsonProperty("updated_at")
		private @Nullable ZonedDateTime updatedAt;

		@JsonProperty("agent_id")
		private @Nullable String agentId;

		@JsonProperty("run_id")
		private @Nullable String runId;

		@JsonProperty("score")
		private @Nullable Double score;

		@JsonProperty("role")
		private String role = "";

		public Mem0Results() {
		}

		public Mem0Results(String id, String memory, String hash, Map<String, Object> metadata, @Nullable String userId,
				@Nullable ZonedDateTime createdAt, @Nullable ZonedDateTime updatedAt, @Nullable String agentId,
				@Nullable String runId, @Nullable Double score, String role) {
			this.id = id;
			this.memory = memory;
			this.hash = hash;
			this.metadata = metadata;
			this.userId = userId;
			this.createdAt = createdAt;
			this.updatedAt = updatedAt;
			this.agentId = agentId;
			this.runId = runId;
			this.score = score;
			this.role = role;
		}

		// Getters and Setters
		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getMemory() {
			return memory;
		}

		public void setMemory(String memory) {
			this.memory = memory;
		}

		public String getHash() {
			return hash;
		}

		public void setHash(String hash) {
			this.hash = hash;
		}

		public Map<String, Object> getMetadata() {
			return metadata;
		}

		public void setMetadata(Map<String, Object> metadata) {
			this.metadata = metadata;
		}

		public @Nullable String getUserId() {
			return userId;
		}

		public void setUserId(@Nullable String userId) {
			this.userId = userId;
		}

		public @Nullable ZonedDateTime getCreatedAt() {
			return createdAt;
		}

		public void setCreatedAt(@Nullable ZonedDateTime createdAt) {
			this.createdAt = createdAt;
		}

		public @Nullable ZonedDateTime getUpdatedAt() {
			return updatedAt;
		}

		public void setUpdatedAt(@Nullable ZonedDateTime updatedAt) {
			this.updatedAt = updatedAt;
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

		public @Nullable Double getScore() {
			return score;
		}

		public void setScore(@Nullable Double score) {
			this.score = score;
		}

		public String getRole() {
			return role;
		}

		public void setRole(String role) {
			this.role = role;
		}

		@Override
		public String toString() {
			return "Mem0Results{" + "id='" + id + '\'' + ", memory='" + memory + '\'' + ", hash='" + hash + '\''
					+ ", metadata=" + metadata + ", userId='" + userId + '\'' + ", createdAt=" + createdAt
					+ ", updatedAt=" + updatedAt + ", agentId='" + agentId + '\'' + ", runId='" + runId + '\''
					+ ", score=" + score + ", role='" + role + '\'' + '}';
		}

	}

}
