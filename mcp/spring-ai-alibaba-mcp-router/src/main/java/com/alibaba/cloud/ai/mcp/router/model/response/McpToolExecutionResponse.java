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
 */

package com.alibaba.cloud.ai.mcp.router.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jspecify.annotations.Nullable;

import java.util.Map;

/**
 * MCP 工具执行响应结果 用于封装 MCP 工具执行的结果，包含执行状态、结果数据和执行元信息
 *
 * @author spring-ai-alibaba
 * @since 2025.0.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class McpToolExecutionResponse {

	/**
	 * 执行是否成功
	 */
	@JsonProperty("success")
	private boolean success;

	/**
	 * 错误消息（当 success 为 false 时）
	 */
	@JsonProperty("error_message")
	private @Nullable String errorMessage;

	/**
	 * 服务名称
	 */
	@JsonProperty("service_name")
	private @Nullable String serviceName;

	/**
	 * 工具名称
	 */
	@JsonProperty("tool_name")
	private @Nullable String toolName;

	/**
	 * 执行参数
	 */
	@JsonProperty("execution_parameters")
	private @Nullable Map<String, Object> executionParameters;

	/**
	 * 执行结果
	 */
	@JsonProperty("result")
	private @Nullable McpToolExecutionResult result;

	/**
	 * 执行元信息
	 */
	@JsonProperty("execution_meta")
	private @Nullable McpExecutionMeta executionMeta;

	public McpToolExecutionResponse() {
	}

	/**
	 * 创建成功响应
	 */
	public static McpToolExecutionResponse success(@Nullable String serviceName, @Nullable String toolName,
			@Nullable Map<String, Object> parameters, @Nullable McpToolExecutionResult result,
			@Nullable McpExecutionMeta meta) {
		McpToolExecutionResponse response = new McpToolExecutionResponse();
		response.success = true;
		response.serviceName = serviceName;
		response.toolName = toolName;
		response.executionParameters = parameters;
		response.result = result;
		response.executionMeta = meta;
		return response;
	}

	/**
	 * 创建失败响应
	 */
	public static McpToolExecutionResponse error(@Nullable String serviceName, @Nullable String toolName,
			@Nullable Map<String, Object> parameters, @Nullable String errorMessage) {
		McpToolExecutionResponse response = new McpToolExecutionResponse();
		response.success = false;
		response.serviceName = serviceName;
		response.toolName = toolName;
		response.executionParameters = parameters;
		response.errorMessage = errorMessage;
		return response;
	}

	/**
	 * MCP 工具执行结果
	 */
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class McpToolExecutionResult {

		/**
		 * 结果数据类型（text, json, binary 等）
		 */
		@JsonProperty("content_type")
		private @Nullable String contentType;

		/**
		 * 结果内容
		 */
		@JsonProperty("content")
		private @Nullable Object content;

		/**
		 * 原始响应（调试用）
		 */
		@JsonProperty("raw_response")
		private @Nullable String rawResponse;

		/**
		 * 结果大小（字节）
		 */
		@JsonProperty("content_size")
		private @Nullable Long contentSize;

		public McpToolExecutionResult() {
		}

		public McpToolExecutionResult(@Nullable String contentType, @Nullable Object content,
				@Nullable String rawResponse) {
			this.contentType = contentType;
			this.content = content;
			this.rawResponse = rawResponse;
			this.contentSize = rawResponse != null ? (long) rawResponse.length() : null;
		}

		/**
		 * 创建文本结果
		 */
		public static McpToolExecutionResult text(@Nullable String content) {
			return new McpToolExecutionResult("text", content, content);
		}

		/**
		 * 创建JSON结果
		 */
		public static McpToolExecutionResult json(@Nullable Object content, @Nullable String rawResponse) {
			return new McpToolExecutionResult("json", content, rawResponse);
		}

		// Getters and Setters
		public @Nullable String getContentType() {
			return contentType;
		}

		public void setContentType(@Nullable String contentType) {
			this.contentType = contentType;
		}

		public @Nullable Object getContent() {
			return content;
		}

		public void setContent(@Nullable Object content) {
			this.content = content;
		}

		public @Nullable String getRawResponse() {
			return rawResponse;
		}

		public void setRawResponse(@Nullable String rawResponse) {
			this.rawResponse = rawResponse;
			this.contentSize = rawResponse != null ? (long) rawResponse.length() : null;
		}

		public @Nullable Long getContentSize() {
			return contentSize;
		}

		public void setContentSize(@Nullable Long contentSize) {
			this.contentSize = contentSize;
		}

	}

	/**
	 * MCP 执行元信息
	 */
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class McpExecutionMeta {

		/**
		 * 执行开始时间戳
		 */
		@JsonProperty("execution_start")
		private @Nullable Long executionStart;

		/**
		 * 执行结束时间戳
		 */
		@JsonProperty("execution_end")
		private @Nullable Long executionEnd;

		/**
		 * 执行耗时（毫秒）
		 */
		@JsonProperty("execution_duration_ms")
		private @Nullable Long executionDurationMs;

		/**
		 * 使用的协议
		 */
		@JsonProperty("protocol_used")
		private @Nullable String protocolUsed;

		/**
		 * 连接URL
		 */
		@JsonProperty("connection_url")
		private @Nullable String connectionUrl;

		/**
		 * 重试次数
		 */
		@JsonProperty("retry_count")
		private @Nullable Integer retryCount;

		public McpExecutionMeta() {
		}

		public McpExecutionMeta(@Nullable Long executionStart, @Nullable Long executionEnd,
				@Nullable String protocolUsed, @Nullable String connectionUrl) {
			this.executionStart = executionStart;
			this.executionEnd = executionEnd;
			this.executionDurationMs = executionEnd != null && executionStart != null ? executionEnd - executionStart
					: null;
			this.protocolUsed = protocolUsed;
			this.connectionUrl = connectionUrl;
			this.retryCount = 0;
		}

		/**
		 * 创建执行开始的元信息
		 */
		public static McpExecutionMeta start(@Nullable String protocolUsed, @Nullable String connectionUrl) {
			return new McpExecutionMeta(System.currentTimeMillis(), null, protocolUsed, connectionUrl);
		}

		/**
		 * 完成执行
		 */
		public void complete() {
			this.executionEnd = System.currentTimeMillis();
			if (this.executionStart != null) {
				this.executionDurationMs = this.executionEnd - this.executionStart;
			}
		}

		// Getters and Setters
		public @Nullable Long getExecutionStart() {
			return executionStart;
		}

		public void setExecutionStart(@Nullable Long executionStart) {
			this.executionStart = executionStart;
		}

		public @Nullable Long getExecutionEnd() {
			return executionEnd;
		}

		public void setExecutionEnd(@Nullable Long executionEnd) {
			this.executionEnd = executionEnd;
		}

		public @Nullable Long getExecutionDurationMs() {
			return executionDurationMs;
		}

		public void setExecutionDurationMs(@Nullable Long executionDurationMs) {
			this.executionDurationMs = executionDurationMs;
		}

		public @Nullable String getProtocolUsed() {
			return protocolUsed;
		}

		public void setProtocolUsed(@Nullable String protocolUsed) {
			this.protocolUsed = protocolUsed;
		}

		public @Nullable String getConnectionUrl() {
			return connectionUrl;
		}

		public void setConnectionUrl(@Nullable String connectionUrl) {
			this.connectionUrl = connectionUrl;
		}

		public @Nullable Integer getRetryCount() {
			return retryCount;
		}

		public void setRetryCount(@Nullable Integer retryCount) {
			this.retryCount = retryCount;
		}

	}

	// Getters and Setters
	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public @Nullable String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(@Nullable String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public @Nullable String getServiceName() {
		return serviceName;
	}

	public void setServiceName(@Nullable String serviceName) {
		this.serviceName = serviceName;
	}

	public @Nullable String getToolName() {
		return toolName;
	}

	public void setToolName(@Nullable String toolName) {
		this.toolName = toolName;
	}

	public @Nullable Map<String, Object> getExecutionParameters() {
		return executionParameters;
	}

	public void setExecutionParameters(@Nullable Map<String, Object> executionParameters) {
		this.executionParameters = executionParameters;
	}

	public @Nullable McpToolExecutionResult getResult() {
		return result;
	}

	public void setResult(@Nullable McpToolExecutionResult result) {
		this.result = result;
	}

	public @Nullable McpExecutionMeta getExecutionMeta() {
		return executionMeta;
	}

	public void setExecutionMeta(@Nullable McpExecutionMeta executionMeta) {
		this.executionMeta = executionMeta;
	}

	@Override
	public String toString() {
		return "McpToolExecutionResponse{" + "success=" + success + ", errorMessage='" + errorMessage + '\''
				+ ", serviceName='" + serviceName + '\'' + ", toolName='" + toolName + '\'' + ", executionParameters="
				+ executionParameters + ", result=" + result + ", executionMeta=" + executionMeta + '}';
	}

}
