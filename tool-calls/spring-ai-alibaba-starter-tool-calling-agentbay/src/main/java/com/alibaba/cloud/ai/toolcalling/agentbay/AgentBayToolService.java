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
import com.aliyun.agentbay.session.CreateSessionParams;
import com.aliyun.agentbay.session.Session;
import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

/**
 * AgentBay Tool Service - 统一的 AgentBay 工具服务
 *
 * 提供三个核心工具：创建会话、删除会话、执行Shell命令
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

	// ==================== 创建会话工具 ====================

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

	@JsonClassDescription("创建一个新的 AgentBay 云端沙箱会话")
	public record CreateSessionRequest(
			@JsonProperty(value = "imageId") @JsonPropertyDescription("运行时镜像ID，如 'code_latest', 'browser_latest', 'linux_latest'。可选，默认为 'code_latest'") String imageId) {
	}

	public record CreateSessionResponse(
			@JsonProperty("sessionId") @JsonPropertyDescription("会话唯一标识符") String sessionId,
			@JsonProperty("success") @JsonPropertyDescription("是否成功") boolean success,
			@JsonProperty("message") @JsonPropertyDescription("附加信息或错误消息") String message) {
	}

	// ==================== 删除会话工具 ====================

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

	@JsonClassDescription("删除一个现有的 AgentBay 会话并清理资源")
	public record DeleteSessionRequest(
			@JsonProperty(required = true, value = "sessionId") @JsonPropertyDescription("要删除的会话ID") String sessionId) {
	}

	public record DeleteSessionResponse(
			@JsonProperty("success") @JsonPropertyDescription("是否成功") boolean success,
			@JsonProperty("message") @JsonPropertyDescription("附加信息或错误消息") String message) {
	}

	// ==================== 执行Shell命令工具 ====================

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

				// 执行命令
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

	@JsonClassDescription("在 AgentBay 会话中执行 Shell 命令")
	public record ExecuteShellRequest(
			@JsonProperty(required = true, value = "command") @JsonPropertyDescription("要执行的Shell命令。单行命令，如需多条命令请用 && 或 ; 连接") String command,
			@JsonProperty(required = true, value = "sessionId") @JsonPropertyDescription("使用的会话ID（必填）。必须先使用 createSessionTool 创建会话") String sessionId,
			@JsonProperty(value = "autoCleanup") @JsonPropertyDescription("执行后是否自动删除会话（可选）。默认为 false") Boolean autoCleanup) {
	}

	public record ExecuteShellResponse(@JsonProperty("output") @JsonPropertyDescription("命令输出") String output,
			@JsonProperty("exitCode") @JsonPropertyDescription("命令退出码") int exitCode,
			@JsonProperty("success") @JsonPropertyDescription("是否成功") boolean success,
			@JsonProperty("sessionId") @JsonPropertyDescription("使用的会话ID") String sessionId,
			@JsonProperty("message") @JsonPropertyDescription("附加信息或错误消息") String message) {
	}

	// ==================== 获取公网链接工具 ====================

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

	@JsonClassDescription("获取沙箱端口的公网 HTTP 链接")
	public record GetLinkRequest(
			@JsonProperty(required = true, value = "sessionId") @JsonPropertyDescription("会话ID（必填）") String sessionId,
			@JsonProperty(required = true, value = "port") @JsonPropertyDescription("端口号（必填），范围 30100-30199") Integer port) {
	}

	public record GetLinkResponse(@JsonProperty("url") @JsonPropertyDescription("公网访问链接") String url,
			@JsonProperty("success") @JsonPropertyDescription("是否成功") boolean success,
			@JsonProperty("message") @JsonPropertyDescription("附加信息或错误消息") String message) {
	}

	// ==================== 读取文件工具 ====================

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

	@JsonClassDescription("读取沙箱中的文件内容")
	public record ReadFileRequest(
			@JsonProperty(required = true, value = "sessionId") @JsonPropertyDescription("会话ID（必填）") String sessionId,
			@JsonProperty(required = true, value = "path") @JsonPropertyDescription("文件路径（必填）") String path) {
	}

	public record ReadFileResponse(@JsonProperty("content") @JsonPropertyDescription("文件内容") String content,
			@JsonProperty("success") @JsonPropertyDescription("是否成功") boolean success,
			@JsonProperty("message") @JsonPropertyDescription("附加信息或错误消息") String message) {
	}

	// ==================== 写入文件工具 ====================

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

	@JsonClassDescription("写入内容到沙箱中的文件")
	public record WriteFileRequest(
			@JsonProperty(required = true, value = "sessionId") @JsonPropertyDescription("会话ID（必填）") String sessionId,
			@JsonProperty(required = true, value = "path") @JsonPropertyDescription("文件路径（必填）") String path,
			@JsonProperty(required = true, value = "content") @JsonPropertyDescription("文件内容（必填）") String content) {
	}

	public record WriteFileResponse(@JsonProperty("success") @JsonPropertyDescription("是否成功") boolean success,
			@JsonProperty("message") @JsonPropertyDescription("附加信息或错误消息") String message) {
	}

	// ==================== 列出文件工具 ====================

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

	@JsonClassDescription("列出沙箱目录中的文件和子目录")
	public record ListFilesRequest(
			@JsonProperty(required = true, value = "sessionId") @JsonPropertyDescription("会话ID（必填）") String sessionId,
			@JsonProperty(value = "path") @JsonPropertyDescription("目录路径（可选，默认为当前目录）") String path) {
	}

	public record ListFilesResponse(@JsonProperty("listing") @JsonPropertyDescription("目录列表") String listing,
			@JsonProperty("success") @JsonPropertyDescription("是否成功") boolean success,
			@JsonProperty("message") @JsonPropertyDescription("附加信息或错误消息") String message) {
	}

}


