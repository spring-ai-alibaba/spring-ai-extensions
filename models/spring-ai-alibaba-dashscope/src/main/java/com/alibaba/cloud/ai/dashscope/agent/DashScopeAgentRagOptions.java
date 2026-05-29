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
package com.alibaba.cloud.ai.dashscope.agent;

import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.databind.JsonNode;

import java.util.List;

import org.jspecify.annotations.Nullable;

/**
 * @author kevinlin09
 */
public class DashScopeAgentRagOptions {

	/** knowledge base ids */
	@JsonProperty("pipeline_ids")
	private @Nullable List<String> pipelineIds;

	/** file ids of knowledge base */
	@JsonProperty("file_ids")
	private @Nullable List<String> fileIds;

	/** tags of knowledge base */
	@JsonProperty("tags")
	private @Nullable List<String> tags;

	/** metadata filter of knowledge base query */
	@JsonProperty("metadata_filter")
	private @Nullable JsonNode metadataFilter;

	/** structured filter of knowledge base query */
	@JsonProperty("structured_filter")
	private @Nullable JsonNode structuredFilter;

	/** file ID is a temporary file associated with the current session */
	@JsonProperty("session_file_ids")
	private @Nullable List<String> sessionFileIds;

	public @Nullable List<String> getPipelineIds() {
		return pipelineIds;
	}

	public void setPipelineIds(@Nullable List<String> pipelineIds) {
		this.pipelineIds = pipelineIds;
	}

	public @Nullable List<String> getFileIds() {
		return fileIds;
	}

	public void setFileIds(@Nullable List<String> fileIds) {
		this.fileIds = fileIds;
	}

	public @Nullable List<String> getTags() {
		return tags;
	}

	public void setTags(@Nullable List<String> tags) {
		this.tags = tags;
	}

	public @Nullable JsonNode getMetadataFilter() {
		return metadataFilter;
	}

	public void setMetadataFilter(@Nullable JsonNode metadataFilter) {
		this.metadataFilter = metadataFilter;
	}

	public @Nullable JsonNode getStructuredFilter() {
		return structuredFilter;
	}

	public void setStructuredFilter(@Nullable JsonNode structuredFilter) {
		this.structuredFilter = structuredFilter;
	}

	public @Nullable List<String> getSessionFileIds() {
		return sessionFileIds;
	}

	public void setSessionFileIds(@Nullable List<String> sessionFileIds) {
		this.sessionFileIds = sessionFileIds;
	}

	public static DashScopeAgentRagOptions.Builder builder() {

		return new DashScopeAgentRagOptions.Builder();
	}

	public static class Builder {

		protected DashScopeAgentRagOptions options;

		public Builder() {
			this.options = new DashScopeAgentRagOptions();
		}

		public Builder(DashScopeAgentRagOptions options) {
			this.options = options;
		}

		public Builder pipelineIds(@Nullable List<String> pipelineIds) {
			this.options.pipelineIds = pipelineIds;
			return this;
		}

		public Builder fileIds(@Nullable List<String> fileIds) {
			this.options.fileIds = fileIds;
			return this;
		}

		public Builder tags(@Nullable List<String> tags) {
			this.options.tags = tags;
			return this;
		}

		public Builder metadataFilter(@Nullable JsonNode metadataFilter) {
			this.options.metadataFilter = metadataFilter;
			return this;
		}

		public Builder structuredFilter(@Nullable JsonNode structuredFilter) {
			this.options.structuredFilter = structuredFilter;
			return this;
		}

		public Builder sessionFileIds(@Nullable List<String> sessionFileIds) {
			this.options.sessionFileIds = sessionFileIds;
			return this;
		}

		public DashScopeAgentRagOptions build() {
			return this.options;
		}

	}

}
