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
package com.alibaba.cloud.ai.reader.gitlab;

import org.jspecify.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Configuration class for GitLab issue reader. Contains all parameters for filtering and
 * retrieving issues.
 *
 * @author brianxiadong
 */
public class GitLabIssueConfig {

	// Assignee username to filter issues
	private @Nullable String assignee;

	// Author username to filter issues
	private @Nullable String author;

	// Whether to return only confidential issues
	private @Nullable Boolean confidential;

	// Return issues created after this date
	private @Nullable LocalDateTime createdAfter;

	// Return issues created before this date
	private @Nullable LocalDateTime createdBefore;

	// List of issue IIDs to filter
	private @Nullable List<Integer> iids;

	// Type of issues to return (issue, incident, test_case)
	private @Nullable GitLabIssueType issueType;

	// Labels to filter issues
	private @Nullable List<String> labels;

	// Milestone title to filter issues
	private @Nullable String milestone;

	// Whether to return only non-archived issues
	private @Nullable Boolean nonArchived;

	// Scope of issues to return (created_by_me, assigned_to_me, all)
	private @Nullable GitLabScope scope;

	// Search query to filter issues
	private @Nullable String search;

	// State of issues to return (opened, closed, all)
	private @Nullable GitLabIssueState state;

	// Return issues updated after this date
	private @Nullable LocalDateTime updatedAfter;

	// Return issues updated before this date
	private @Nullable LocalDateTime updatedBefore;

	private GitLabIssueConfig() {
		// Use builder pattern to create instances
	}

	public @Nullable String getAssignee() {
		return assignee;
	}

	public @Nullable String getAuthor() {
		return author;
	}

	public @Nullable Boolean getConfidential() {
		return confidential;
	}

	public @Nullable LocalDateTime getCreatedAfter() {
		return createdAfter;
	}

	public @Nullable LocalDateTime getCreatedBefore() {
		return createdBefore;
	}

	public @Nullable List<Integer> getIids() {
		return iids;
	}

	public @Nullable GitLabIssueType getIssueType() {
		return issueType;
	}

	public @Nullable List<String> getLabels() {
		return labels;
	}

	public @Nullable String getMilestone() {
		return milestone;
	}

	public @Nullable Boolean getNonArchived() {
		return nonArchived;
	}

	public @Nullable GitLabScope getScope() {
		return scope;
	}

	public @Nullable String getSearch() {
		return search;
	}

	public @Nullable GitLabIssueState getState() {
		return state;
	}

	public @Nullable LocalDateTime getUpdatedAfter() {
		return updatedAfter;
	}

	public @Nullable LocalDateTime getUpdatedBefore() {
		return updatedBefore;
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {

		private final GitLabIssueConfig config;

		private Builder() {
			config = new GitLabIssueConfig();
		}

		public Builder assignee(@Nullable String assignee) {
			config.assignee = assignee;
			return this;
		}

		public Builder author(@Nullable String author) {
			config.author = author;
			return this;
		}

		public Builder confidential(@Nullable Boolean confidential) {
			config.confidential = confidential;
			return this;
		}

		public Builder createdAfter(@Nullable LocalDateTime createdAfter) {
			config.createdAfter = createdAfter;
			return this;
		}

		public Builder createdBefore(@Nullable LocalDateTime createdBefore) {
			config.createdBefore = createdBefore;
			return this;
		}

		public Builder iids(@Nullable List<Integer> iids) {
			config.iids = iids;
			return this;
		}

		public Builder issueType(@Nullable GitLabIssueType issueType) {
			config.issueType = issueType;
			return this;
		}

		public Builder labels(@Nullable List<String> labels) {
			config.labels = labels;
			return this;
		}

		public Builder milestone(@Nullable String milestone) {
			config.milestone = milestone;
			return this;
		}

		public Builder nonArchived(@Nullable Boolean nonArchived) {
			config.nonArchived = nonArchived;
			return this;
		}

		public Builder scope(@Nullable GitLabScope scope) {
			config.scope = scope;
			return this;
		}

		public Builder search(@Nullable String search) {
			config.search = search;
			return this;
		}

		public Builder state(@Nullable GitLabIssueState state) {
			config.state = state != null ? state : GitLabIssueState.OPEN;
			return this;
		}

		public Builder updatedAfter(@Nullable LocalDateTime updatedAfter) {
			config.updatedAfter = updatedAfter;
			return this;
		}

		public Builder updatedBefore(@Nullable LocalDateTime updatedBefore) {
			config.updatedBefore = updatedBefore;
			return this;
		}

		public GitLabIssueConfig build() {
			return config;
		}

	}

}
