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
package com.alibaba.cloud.ai.toolcalling.agentbay;

import com.aliyun.agentbay.AgentBay;
import com.aliyun.agentbay.model.CommandResult;
import com.aliyun.agentbay.model.DeleteResult;
import com.aliyun.agentbay.model.SessionResult;
import com.aliyun.agentbay.model.code.EnhancedCodeExecutionResult;
import com.aliyun.agentbay.session.CreateSessionParams;
import com.aliyun.agentbay.session.Session;
import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

/**
 * AgentBay Tool Service - Unified AgentBay tool service
 *
 * Provides core tools: create session, delete session, execute shell commands
 *
 * @author Spring AI Alibaba
 */
public class AgentBayToolService {

	private static final Logger log = LoggerFactory.getLogger(AgentBayToolService.class);

	private final AgentBay agentBay;

	private final AgentBayProperties properties;

	public AgentBayToolService(AgentBay agentBay, AgentBayProperties properties) {
		this.agentBay = agentBay;
		this.properties = properties;
	}

	private Session getSession(String sessionId) throws com.aliyun.agentbay.exception.AgentBayException {
		SessionResult result = agentBay.get(sessionId);
		if (result.isSuccess()) {
			return result.getSession();
		}
		return null;
	}

	// ==================== Create Session Tool ====================

	public Function<CreateSessionRequest, CreateSessionResponse> createSessionTool() {
		return request -> {
			String imageId = request.imageId != null ? request.imageId : properties.getDefaultImageId();
			log.info("Creating AgentBay session with imageId: {}", imageId);

			try {
				CreateSessionParams params = new CreateSessionParams();
				params.setImageId(imageId);

				SessionResult result = agentBay.create(params);

				if (result.isSuccess()) {
					Session session = result.getSession();
					String sessionId = session.getSessionId();

					log.info("AgentBay session created successfully: {}", sessionId);
					return new CreateSessionResponse(sessionId, true, "Session created successfully");
				}
				else {
					log.error("Failed to create AgentBay session: {}", result.getErrorMessage());
					return new CreateSessionResponse(null, false,
							"Failed to create session: " + result.getErrorMessage());
				}
			}
			catch (Exception e) {
				log.error("Error creating AgentBay session", e);
				return new CreateSessionResponse(null, false, "Error creating session: " + e.getMessage());
			}
		};
	}

	@JsonClassDescription("Create a new AgentBay cloud sandbox session")
	public record CreateSessionRequest(
			@JsonProperty(value = "imageId") @JsonPropertyDescription("Runtime image ID, such as 'code_latest', 'browser_latest', 'linux_latest'. Optional, defaults to 'code_latest'") String imageId) {
	}

	public record CreateSessionResponse(
			@JsonProperty("sessionId") @JsonPropertyDescription("Unique session identifier") String sessionId,
			@JsonProperty("success") @JsonPropertyDescription("Whether the operation was successful") boolean success,
			@JsonProperty("message") @JsonPropertyDescription("Additional information or error message") String message) {
	}

	// ==================== Delete Session Tool ====================

	public Function<DeleteSessionRequest, DeleteSessionResponse> deleteSessionTool() {
		return request -> {
			if (request.sessionId == null || request.sessionId.trim().isEmpty()) {
				return new DeleteSessionResponse(false, "Session ID is required");
			}

			log.info("Deleting AgentBay session: {}", request.sessionId);

			try {
				Session session = getSession(request.sessionId);

				if (session != null) {
					DeleteResult result = agentBay.delete(session, false);

					if (result.isSuccess()) {
						log.info("AgentBay session deleted successfully: {}", request.sessionId);
						return new DeleteSessionResponse(true, "Session deleted successfully");
					}
					else {
						log.error("Failed to delete AgentBay session: {}", result.getErrorMessage());
						return new DeleteSessionResponse(false,
								"Failed to delete session: " + result.getErrorMessage());
					}
				}
				else {
					log.warn("Session not found: {}", request.sessionId);
					return new DeleteSessionResponse(false,
							"Session not found or already deleted.");
				}
			}
			catch (Exception e) {
				log.error("Error deleting AgentBay session", e);
				return new DeleteSessionResponse(false, "Error deleting session: " + e.getMessage());
			}
		};
	}

	@JsonClassDescription("Delete an existing AgentBay session and clean up resources")
	public record DeleteSessionRequest(
			@JsonProperty(required = true, value = "sessionId") @JsonPropertyDescription("Session ID to delete") String sessionId) {
	}

	public record DeleteSessionResponse(
			@JsonProperty("success") @JsonPropertyDescription("Whether the operation was successful") boolean success,
			@JsonProperty("message") @JsonPropertyDescription("Additional information or error message") String message) {
	}

	// ==================== Execute Shell Command Tool ====================

	public Function<ExecuteShellRequest, ExecuteShellResponse> executeShellTool() {
		return request -> {
			if (request.command == null || request.command.trim().isEmpty()) {
				return new ExecuteShellResponse(null, -1, false, null, "Command cannot be empty");
			}

			if (request.sessionId == null || request.sessionId.trim().isEmpty()) {
				return new ExecuteShellResponse(null, -1, false, null,
						"Session ID is required. Please create a session first using createSessionTool.");
			}

			String sessionId = request.sessionId;
			Session session;
			try {
				session = getSession(sessionId);
			}
			catch (Exception e) {
				return new ExecuteShellResponse(null, -1, false, sessionId,
						"Error getting session: " + e.getMessage());
			}

			if (session == null) {
				return new ExecuteShellResponse(null, -1, false, sessionId,
						"Session not found: " + sessionId + ". Please create a session first.");
			}

			boolean shouldCleanup = request.autoCleanup != null ? request.autoCleanup : false;

			try {
				String command = request.command;

				log.info("Executing command in session {}: {}", sessionId, command);

				CommandResult cmdResult = session.getCommand().executeCommand(command, 30000);

				if (cmdResult.isSuccess()) {
					log.info("Command executed successfully in session {}", sessionId);
					return new ExecuteShellResponse(cmdResult.getOutput(), cmdResult.getExitCode(), true, sessionId,
							"Command executed successfully");
				}
				else {
					log.error("Command execution failed in session {}: {}", sessionId, cmdResult.getErrorMessage());
					return new ExecuteShellResponse(cmdResult.getOutput(), cmdResult.getExitCode(), false, sessionId,
							"Command execution failed: " + cmdResult.getErrorMessage());
				}
			}
			catch (Exception e) {
				log.error("Error executing command in AgentBay session", e);
				return new ExecuteShellResponse(null, -1, false, sessionId,
						"Error executing command: " + e.getMessage());
			}
			finally {
				if (shouldCleanup) {
					try {
						log.info("Cleaning up session: {}", sessionId);
						agentBay.delete(session, false);
					}
					catch (Exception e) {
						log.error("Error cleaning up session", e);
					}
				}
			}
		};
	}

	@JsonClassDescription("Execute a shell command in an AgentBay session")
	public record ExecuteShellRequest(
			@JsonProperty(required = true, value = "command") @JsonPropertyDescription("Shell command to execute. Single-line command. Use && or ; to connect multiple commands") String command,
			@JsonProperty(required = true, value = "sessionId") @JsonPropertyDescription("Session ID to use (required). Must create a session first using createSessionTool") String sessionId,
			@JsonProperty(value = "autoCleanup") @JsonPropertyDescription("Whether to automatically delete the session after execution (optional). Defaults to false") Boolean autoCleanup) {
	}

	public record ExecuteShellResponse(@JsonProperty("output") @JsonPropertyDescription("Command output") String output,
			@JsonProperty("exitCode") @JsonPropertyDescription("Command exit code") int exitCode,
			@JsonProperty("success") @JsonPropertyDescription("Whether the operation was successful") boolean success,
			@JsonProperty("sessionId") @JsonPropertyDescription("Session ID used") String sessionId,
			@JsonProperty("message") @JsonPropertyDescription("Additional information or error message") String message) {
	}

	// ==================== Get Public Link Tool ====================

	public Function<GetLinkRequest, GetLinkResponse> getLinkTool() {
		return request -> {
			if (request.sessionId == null || request.sessionId.trim().isEmpty()) {
				return new GetLinkResponse(null, false, "Session ID is required");
			}

			Integer port = request.port;
			if (port == null) {
				return new GetLinkResponse(null, false, "Port is required");
			}

			if (port < 30100 || port > 30199) {
				return new GetLinkResponse(null, false, "Port must be between 30100 and 30199");
			}

			String sessionId = request.sessionId;
			Session session;
			try {
				session = getSession(sessionId);
			}
			catch (Exception e) {
				return new GetLinkResponse(null, false,
						"Error getting session: " + e.getMessage());
			}

			if (session == null) {
				return new GetLinkResponse(null, false,
						"Session not found: " + sessionId + ". Please create a session first.");
			}

			try {
				log.info("Getting link for session {} on port {}", sessionId, port);

				com.aliyun.agentbay.model.OperationResult result = session.getLink("https", port);

				if (result.isSuccess()) {
					String url = (String) result.getData();
					log.info("Link retrieved successfully: {}", url);
					return new GetLinkResponse(url, true, "Link retrieved successfully");
				}
				else {
					log.error("Failed to get link: {}", result.getErrorMessage());
					return new GetLinkResponse(null, false, "Failed to get link: " + result.getErrorMessage());
				}
			}
			catch (Exception e) {
				log.error("Error getting link for session", e);
				return new GetLinkResponse(null, false, "Error getting link: " + e.getMessage());
			}
		};
	}

	@JsonClassDescription("Get public HTTP link for sandbox port")
	public record GetLinkRequest(
			@JsonProperty(required = true, value = "sessionId") @JsonPropertyDescription("Session ID (required)") String sessionId,
			@JsonProperty(required = true, value = "port") @JsonPropertyDescription("Port number (required), range 30100-30199") Integer port) {
	}

	public record GetLinkResponse(@JsonProperty("url") @JsonPropertyDescription("Public access URL") String url,
			@JsonProperty("success") @JsonPropertyDescription("Whether the operation was successful") boolean success,
			@JsonProperty("message") @JsonPropertyDescription("Additional information or error message") String message) {
	}

	// ==================== Read File Tool ====================

	public Function<ReadFileRequest, ReadFileResponse> readFileTool() {
		return request -> {
			if (request.sessionId == null || request.sessionId.trim().isEmpty()) {
				return new ReadFileResponse(null, false, "Session ID is required");
			}

			if (request.path == null || request.path.trim().isEmpty()) {
				return new ReadFileResponse(null, false, "File path is required");
			}

			String sessionId = request.sessionId;
			Session session;
			try {
				session = getSession(sessionId);
			}
			catch (Exception e) {
				return new ReadFileResponse(null, false,
						"Error getting session: " + e.getMessage());
			}

			if (session == null) {
				return new ReadFileResponse(null, false,
						"Session not found: " + sessionId + ". Please create a session first.");
			}

			try {
				log.info("Reading file from session {}: {}", sessionId, request.path);

				com.aliyun.agentbay.model.FileContentResult result = session.getFileSystem().readFile(request.path);

				if (result.isSuccess()) {
					String content = result.getContent();
					log.info("File read successfully from session {}, size: {} bytes", sessionId,
							content != null ? content.length() : 0);
					return new ReadFileResponse(content, true, "File read successfully");
				}
				else {
					log.error("Failed to read file: {}", result.getErrorMessage());
					return new ReadFileResponse(null, false, "Failed to read file: " + result.getErrorMessage());
				}
			}
			catch (Exception e) {
				log.error("Error reading file from session", e);
				return new ReadFileResponse(null, false, "Error reading file: " + e.getMessage());
			}
		};
	}

	@JsonClassDescription("Read file content from sandbox")
	public record ReadFileRequest(
			@JsonProperty(required = true, value = "sessionId") @JsonPropertyDescription("Session ID (required)") String sessionId,
			@JsonProperty(required = true, value = "path") @JsonPropertyDescription("File path (required)") String path) {
	}

	public record ReadFileResponse(@JsonProperty("content") @JsonPropertyDescription("File content") String content,
			@JsonProperty("success") @JsonPropertyDescription("Whether the operation was successful") boolean success,
			@JsonProperty("message") @JsonPropertyDescription("Additional information or error message") String message) {
	}

	// ==================== Write File Tool ====================

	public Function<WriteFileRequest, WriteFileResponse> writeFileTool() {
		return request -> {
			if (request.sessionId == null || request.sessionId.trim().isEmpty()) {
				return new WriteFileResponse(false, "Session ID is required");
			}

			if (request.path == null || request.path.trim().isEmpty()) {
				return new WriteFileResponse(false, "File path is required");
			}

			if (request.content == null) {
				return new WriteFileResponse(false, "File content cannot be null");
			}

			String sessionId = request.sessionId;
			Session session;
			try {
				session = getSession(sessionId);
			}
			catch (Exception e) {
				return new WriteFileResponse(false,
						"Error getting session: " + e.getMessage());
			}

			if (session == null) {
				return new WriteFileResponse(false,
						"Session not found: " + sessionId + ". Please create a session first.");
			}

			try {
				log.info("Writing file to session {}: {}", sessionId, request.path);

				com.aliyun.agentbay.model.BoolResult result = session.getFileSystem()
					.writeFile(request.path, request.content, "overwrite", true);

				if (result.isSuccess()) {
					log.info("File written successfully to session {}", sessionId);
					return new WriteFileResponse(true, "File written successfully");
				}
				else {
					log.error("Failed to write file: {}", result.getErrorMessage());
					return new WriteFileResponse(false, "Failed to write file: " + result.getErrorMessage());
				}
			}
			catch (Exception e) {
				log.error("Error writing file to session", e);
				return new WriteFileResponse(false, "Error writing file: " + e.getMessage());
			}
		};
	}

	@JsonClassDescription("Write content to a file in sandbox")
	public record WriteFileRequest(
			@JsonProperty(required = true, value = "sessionId") @JsonPropertyDescription("Session ID (required)") String sessionId,
			@JsonProperty(required = true, value = "path") @JsonPropertyDescription("File path (required)") String path,
			@JsonProperty(required = true, value = "content") @JsonPropertyDescription("File content (required)") String content) {
	}

	public record WriteFileResponse(@JsonProperty("success") @JsonPropertyDescription("Whether the operation was successful") boolean success,
			@JsonProperty("message") @JsonPropertyDescription("Additional information or error message") String message) {
	}

	// ==================== List Files Tool ====================

	public Function<ListFilesRequest, ListFilesResponse> listFilesTool() {
		return request -> {
			if (request.sessionId == null || request.sessionId.trim().isEmpty()) {
				return new ListFilesResponse(null, false, "Session ID is required");
			}

			String sessionId = request.sessionId;
			Session session;
			try {
				session = getSession(sessionId);
			}
			catch (Exception e) {
				return new ListFilesResponse(null, false,
						"Error getting session: " + e.getMessage());
			}

			if (session == null) {
				return new ListFilesResponse(null, false,
						"Session not found: " + sessionId + ". Please create a session first.");
			}

			String path = request.path != null && !request.path.trim().isEmpty() ? request.path : ".";

			try {
				log.info("Listing directory in session {}: {}", sessionId, path);

				com.aliyun.agentbay.model.DirectoryListResult result = session.getFileSystem().listDirectory(path);

				if (result.isSuccess()) {
					java.util.List<java.util.Map<String, Object>> entries = result.getEntries();
					StringBuilder listing = new StringBuilder();

					if (entries != null && !entries.isEmpty()) {
						for (java.util.Map<String, Object> entry : entries) {
							String name = (String) entry.get("name");
							String type = (String) entry.get("type");
							Object size = entry.get("size");

							listing.append(type != null ? type : "file")
								.append("\t")
								.append(size != null ? size : "")
								.append("\t")
								.append(name != null ? name : "")
								.append("\n");
						}
					} else {
						listing.append("(empty directory)");
					}

					log.info("Directory listed successfully in session {}", sessionId);
					return new ListFilesResponse(listing.toString(), true, "Directory listed successfully");
				}
				else {
					log.error("Failed to list directory: {}", result.getErrorMessage());
					return new ListFilesResponse(null, false, "Failed to list directory: " + result.getErrorMessage());
				}
			}
			catch (Exception e) {
				log.error("Error listing directory in session", e);
				return new ListFilesResponse(null, false, "Error listing directory: " + e.getMessage());
			}
		};
	}

	@JsonClassDescription("List files and subdirectories in sandbox directory")
	public record ListFilesRequest(
			@JsonProperty(required = true, value = "sessionId") @JsonPropertyDescription("Session ID (required)") String sessionId,
			@JsonProperty(value = "path") @JsonPropertyDescription("Directory path (optional, defaults to current directory)") String path) {
	}

	public record ListFilesResponse(@JsonProperty("listing") @JsonPropertyDescription("Directory listing") String listing,
			@JsonProperty("success") @JsonPropertyDescription("Whether the operation was successful") boolean success,
			@JsonProperty("message") @JsonPropertyDescription("Additional information or error message") String message) {
	}

	public Function<RunCodeRequest, RunCodeResponse> runCodeTool() {
		return request -> {
			if (request.sessionId == null || request.sessionId.trim().isEmpty()) {
				return new RunCodeResponse(null, null, false, "Session ID is required");
			}

			if (request.code == null || request.code.trim().isEmpty()) {
				return new RunCodeResponse(null, null, false, "Code cannot be empty");
			}

			if (request.language == null || request.language.trim().isEmpty()) {
				return new RunCodeResponse(null, null, false, "Language is required");
			}

			String sessionId = request.sessionId;
			Session session;
			try {
				session = getSession(sessionId);
			}
			catch (Exception e) {
				return new RunCodeResponse(null, null, false,
						"Error getting session: " + e.getMessage());
			}

			if (session == null) {
				return new RunCodeResponse(null, null, false,
						"Session not found: " + sessionId + ". Please create a session first.");
			}

			try {
				log.info("Executing {} code in session {}", request.language, sessionId);

                EnhancedCodeExecutionResult result = session.getCode()
						.runCode(request.code, request.language);

				if (result.isSuccess()) {
					log.info("Code executed successfully in session {}", sessionId);
					return new RunCodeResponse(result.getResult(), result.getRequestId(), true,
							"Code executed successfully");
				}
				else {
					log.error("Code execution failed in session {}: {}", sessionId, result.getErrorMessage());
					return new RunCodeResponse(null, result.getRequestId(), false,
							"Code execution failed: " + result.getErrorMessage());
				}
			}
			catch (Exception e) {
				log.error("Error executing code in session", e);
				return new RunCodeResponse(null, null, false, "Error executing code: " + e.getMessage());
			}
		};
	}

	@JsonClassDescription("Execute code in sandbox (supports Python, JavaScript.)")
	public record RunCodeRequest(
			@JsonProperty(required = true, value = "sessionId") @JsonPropertyDescription("Session ID (required)") String sessionId,
			@JsonProperty(required = true, value = "code") @JsonPropertyDescription("Code to execute (required)") String code,
			@JsonProperty(required = true, value = "language") @JsonPropertyDescription("Programming language (required, e.g., 'python', 'javascript')") String language) {
	}

	public record RunCodeResponse(@JsonProperty("output") @JsonPropertyDescription("Code execution output") String output,
			@JsonProperty("requestId") @JsonPropertyDescription("Request ID") String requestId,
			@JsonProperty("success") @JsonPropertyDescription("Whether the operation was successful") boolean success,
			@JsonProperty("message") @JsonPropertyDescription("Additional information or error message") String message) {
	}

}


