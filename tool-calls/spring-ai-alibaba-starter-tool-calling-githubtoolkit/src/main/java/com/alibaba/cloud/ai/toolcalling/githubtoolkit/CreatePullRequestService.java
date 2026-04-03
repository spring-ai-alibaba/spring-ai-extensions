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
package com.alibaba.cloud.ai.toolcalling.githubtoolkit;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.alibaba.cloud.ai.toolcalling.common.JsonParseTool;
import com.alibaba.cloud.ai.toolcalling.common.WebClientTool;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreatePullRequestService implements Function<CreatePullRequestService.Request, Response> {

	private static final String REPO_ENDPOINT = "/repos/{owner}/{repo}";

	protected static final String PULL_REQUESTS_ENDPOINT = "/pulls";

	private static final Logger logger = LoggerFactory.getLogger(CreatePullRequestService.class);

	private final WebClientTool webClientTool;

	private final JsonParseTool jsonParseTool;

	private final GithubToolKitProperties properties;

	public CreatePullRequestService(GithubToolKitProperties properties, WebClientTool webClientTool,
			JsonParseTool jsonParseTool) {
		GithubToolKitValueUtils.requireConfiguredText(properties.getToken(), "GitHub token");
		GithubToolKitValueUtils.requireConfiguredText(properties.getOwner(), "GitHub owner");
		GithubToolKitValueUtils.requireConfiguredText(properties.getRepository(), "GitHub repository");
		this.properties = properties;
		this.webClientTool = webClientTool;
		this.jsonParseTool = jsonParseTool;
	}

	@Override
	public Response apply(Request request) {
		try {
			String endpoint = REPO_ENDPOINT + PULL_REQUESTS_ENDPOINT;
			Map<String, Object> body = new HashMap<>();
			addIfNotNull(body, "title", request.pullRequestTitle());
			addIfNotNull(body, "body", request.pullRequestBody());
			addIfNotNull(body, "head", request.pullRequestHead());
			addIfNotNull(body, "base", request.pullRequestBase());
			addIfNotNull(body, "head_repo", request.headRepo());
			addIfNotNull(body, "issue", request.issue());
			addIfNotNull(body, "draft", request.draft());

			String responseData = GithubToolKitValueUtils.requireResponseBody(
					webClientTool.post(endpoint, repositoryVariables(), body).block(), "Create pull request");
			logger.info("Pull request created successfully.");
			return new Response<>(parsePullRequest(responseData));
		}
		catch (IOException e) {
			logger.error("Error parsing pull request data: {}", e.getMessage());
			throw new RuntimeException("Error parsing response", e);
		}
		catch (Exception e) {
			logger.error("Unexpected error: {}", e.getMessage());
			throw new RuntimeException("Unexpected error", e);
		}
	}

	private void addIfNotNull(Map<String, Object> map, String key, @Nullable Object value) {
		if (value != null) {
			map.put(key, value);
		}
	}

	public PullRequest parsePullRequest(String json) throws JsonProcessingException {
		Map<String, Object> prMap = jsonParseTool.jsonToMap(json, Object.class);
		Map<String, Object> userMap = GithubToolKitValueUtils.requireObject(prMap, "user");
		Map<String, Object> headMap = GithubToolKitValueUtils.requireObject(prMap, "head");
		Map<String, Object> baseMap = GithubToolKitValueUtils.requireObject(prMap, "base");

		long id = GithubToolKitValueUtils.requireLong(prMap, "id");
		String title = GithubToolKitValueUtils.requireString(prMap, "title");
		String state = GithubToolKitValueUtils.requireString(prMap, "state");
		int prNumber = GithubToolKitValueUtils.requireInt(prMap, "number");
		@Nullable String body = GithubToolKitValueUtils.nullableString(prMap, "body");
		String htmlUrl = GithubToolKitValueUtils.requireString(prMap, "html_url");

		String userLogin = GithubToolKitValueUtils.requireString(userMap, "login");
		String headRef = GithubToolKitValueUtils.requireString(headMap, "ref");
		String baseRef = GithubToolKitValueUtils.requireString(baseMap, "ref");

		return new PullRequest(id, title, state, prNumber, userLogin, body, htmlUrl, headRef, baseRef);
	}

	private Map<String, Object> repositoryVariables() {
		return Map.of("owner", GithubToolKitValueUtils.requireConfiguredText(properties.getOwner(), "GitHub owner"),
				"repo",
				GithubToolKitValueUtils.requireConfiguredText(properties.getRepository(), "GitHub repository"));
	}

	public record PullRequest(long id, String title, String state, Integer prNumber, String userLogin,
			@Nullable String body, String htmlUrl, String headRef, String baseRef) {
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonClassDescription("GitHub Pull Request creation request")
	public record Request(@JsonProperty(required = true,
			value = "pullRequestTitle") @JsonPropertyDescription("the title of the Pull Request") String pullRequestTitle,

			@JsonProperty(required = true,
					value = "pullRequestBody") @JsonPropertyDescription("the description of the Pull Request") String pullRequestBody,

			@JsonProperty(required = true,
					value = "pullRequestHead") @JsonPropertyDescription("The name of the branch where your changes are implemented.") String pullRequestHead,

			@JsonProperty(required = true,
					value = "pullRequestBase") @JsonPropertyDescription("The name of the branch you want the changes pulled into.") String pullRequestBase,

			@JsonProperty(
					value = "headRepo") @JsonPropertyDescription("The name of the repository where the changes in the pull request were made. This field is required for cross-repository pull requests if both repositories are owned by the same organization.") @Nullable String headRepo,

			@JsonProperty(
					value = "issue") @JsonPropertyDescription("An issue in the repository to convert to a pull request. The issue title, body, and comments will become the title, body, and comments on the new pull request. Required unless title is specified.") @Nullable String issue,

			@JsonProperty(
					value = "draft") @JsonPropertyDescription("Indicates whether the pull request is a draft.") boolean draft) {
	}

}
