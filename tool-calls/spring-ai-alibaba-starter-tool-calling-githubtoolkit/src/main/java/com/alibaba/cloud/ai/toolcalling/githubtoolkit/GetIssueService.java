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

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.alibaba.cloud.ai.toolcalling.common.JsonParseTool;
import com.alibaba.cloud.ai.toolcalling.common.WebClientTool;
import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetIssueService implements Function<GetIssueService.Request, Response> {

	private static final String REPO_ENDPOINT = "/repos/{owner}/{repo}";

	private static final String ISSUES_ENDPOINT = "/issues";

	private static final Logger logger = LoggerFactory.getLogger(GetIssueService.class);

	private final WebClientTool webClientTool;

	private final JsonParseTool jsonParseTool;

	private final GithubToolKitProperties properties;

	public GetIssueService(GithubToolKitProperties properties, WebClientTool webClientTool,
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
        String endpoint = REPO_ENDPOINT + ISSUES_ENDPOINT + "/{issueNumber}";
        String responseData = GithubToolKitValueUtils.requireResponseBody(
                webClientTool.get(endpoint, repositoryVariables(request.issueNumber())).block(), "Get issue");
        logger.info("GetIssueOperation response: {}", responseData);
        return new Response<>(parseIssueDetails(responseData));
	}

	public Issue parseIssueDetails(String json) {
		Map<String, Object> issueMap = jsonParseTool.jsonToMap(json, Object.class);
		Map<String, Object> userMap = GithubToolKitValueUtils.requireObject(issueMap, "user");
		long id = GithubToolKitValueUtils.requireLong(issueMap, "id");
		String title = GithubToolKitValueUtils.requireString(issueMap, "title");
		String state = GithubToolKitValueUtils.requireString(issueMap, "state");
		String createdAt = GithubToolKitValueUtils.requireString(issueMap, "created_at");
		String updatedAt = GithubToolKitValueUtils.requireString(issueMap, "updated_at");
		int comments = GithubToolKitValueUtils.requireInt(issueMap, "comments");
		String htmlUrl = GithubToolKitValueUtils.requireString(issueMap, "html_url");
		@Nullable String body = GithubToolKitValueUtils.nullableString(issueMap, "body");
		@Nullable String closedAt = GithubToolKitValueUtils.nullableString(issueMap, "closed_at");

		String userLogin = GithubToolKitValueUtils.requireString(userMap, "login");
		@Nullable String closedBy = null;
		if (issueMap.get("closed_by") != null) {
			closedBy = GithubToolKitValueUtils
				.requireString(GithubToolKitValueUtils.requireObject(issueMap, "closed_by"), "login");
		}

		List<String> labels = GithubToolKitValueUtils.optionalObjectList(issueMap, "labels")
			.stream()
			.map(labelMap -> GithubToolKitValueUtils.requireString(labelMap, "name"))
			.toList();
		List<String> assignees = GithubToolKitValueUtils.optionalObjectList(issueMap, "assignees")
			.stream()
			.map(assigneeMap -> GithubToolKitValueUtils.requireString(assigneeMap, "login"))
			.toList();

		return new Issue(id, title, body, state, userLogin, labels, assignees, createdAt, updatedAt, closedAt, closedBy,
				comments, htmlUrl);
	}

	private Map<String, Object> repositoryVariables(Integer issueNumber) {
		return Map.of("owner", GithubToolKitValueUtils.requireConfiguredText(properties.getOwner(), "GitHub owner"),
				"repo",
				GithubToolKitValueUtils.requireConfiguredText(properties.getRepository(), "GitHub repository"),
				"issueNumber", issueNumber);
	}

	public record Issue(long id, String title, @Nullable String body, String state, String userLogin,
			List<String> labels, List<String> assignees, String createdAt, String updatedAt, @Nullable String closedAt,
			@Nullable String closedBy, int comments, String htmlUrl) {
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonClassDescription("GitHub Issue request")
	public record Request(@JsonProperty(required = true,
			value = "issueNumber") @JsonPropertyDescription("The number of the issue, which is used to get details about the issue or to leave a comment") Integer issueNumber) {
	}

}
