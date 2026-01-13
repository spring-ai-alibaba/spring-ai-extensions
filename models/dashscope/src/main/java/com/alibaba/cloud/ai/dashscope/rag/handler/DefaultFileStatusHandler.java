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
package com.alibaba.cloud.ai.dashscope.rag.handler;

import com.alibaba.cloud.ai.dashscope.rag.context.DocumentProcessContext;
import com.alibaba.cloud.ai.dashscope.spec.DashScopeApiSpec.CommonResponse;
import com.alibaba.cloud.ai.dashscope.spec.DashScopeApiSpec.QueryFileResponseData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.ResponseEntity;

/**
 * Default file status handler implementation
 *
 * <p>Handles all file parsing statuses with appropriate logic for each status type.
 * Uses a simple switch-case approach to route status handling to dedicated methods.
 *
 * <p>Supported statuses:
 * <ul>
 *   <li>PARSE_SUCCESS: File parsing completed successfully</li>
 *   <li>PARSE_FAILED: File parsing failed with error details</li>
 *   <li>PARSING/UPLOADED: File parsing still in progress</li>
 * </ul>
 *
 * @author kevin
 * @since 2025/12/01
 */
public class DefaultFileStatusHandler implements FileStatusHandler {

    private static final Logger logger = LoggerFactory.getLogger(DefaultFileStatusHandler.class);

    @Override
    public FileStatusResult handle(DocumentProcessContext context,
                                   ResponseEntity<CommonResponse<QueryFileResponseData>> response) {

        QueryFileResponseData data = response.getBody().data();
        String statusValue = data != null ? data.status() : null;

        FileStatus fileStatus = FileStatus.fromValueOrDefault(statusValue, FileStatus.UNK);

        if (fileStatus == null) {
            logger.warn("Received null status for file: {}, treating as PARSING",
                        context.getFileId());
            return handleParsing(context);
        }

        return switch (fileStatus) {
            case PARSE_SUCCESS -> handleParseSuccess(context);
            case PARSE_FAILED -> handleParseFailed(context, response);
            case PARSING, UPLOADED -> FileStatusResult.inProgress();
            default -> {
                logger.warn("Unknown file status: {} for file: {}, treating as PARSING",
                            statusValue, context.getFileId());
                yield handleParsing(context);
            }
        };
    }

    /**
     * Handles parse success status
     *
     * @param context document processing context
     * @return success result
     */
    private FileStatusResult handleParseSuccess(DocumentProcessContext context) {
        logger.debug("File parsing succeeded for fileId: {}", context.getFileId());
        return FileStatusResult.success();
    }

    /**
     * Handles parse failed status with error details extraction
     *
     * <p>This is the only method with complex business logic - it extracts
     * error code and message from the API response and formats a detailed
     * error message.
     *
     * @param context  document processing context
     * @param response API response containing error details
     * @return failure result with detailed error message
     */
    private FileStatusResult handleParseFailed(DocumentProcessContext context,
                                               ResponseEntity<CommonResponse<QueryFileResponseData>> response) {
        CommonResponse<QueryFileResponseData> body = response.getBody();
        if (body == null) {
            logger.error("File parsing failed. FileId: {}. Response body is null.", context.getFileId());
            String message = String.format(
                    "File parsing failed - FileId: %s. Response body is null.",
                    context.getFileId());
            return FileStatusResult.failure(message);
        }
        String errorCode = body.code();
        String errorMessage = body.message();

        logger.error("File parsing failed. FileId: {}, ErrorCode: {}, ErrorMessage: {}",
                     context.getFileId(), errorCode, errorMessage);

        String message = String.format(
                "File parsing failed - FileId: %s, ErrorCode: %s, ErrorMessage: %s",
                context.getFileId(), errorCode, errorMessage);

        return FileStatusResult.failure(message);
    }

    /**
     * Handles parsing in progress status
     *
     * @param context document processing context
     * @return in-progress result
     */
    private FileStatusResult handleParsing(DocumentProcessContext context) {
        logger.debug("File is still parsing. FileId: {}", context.getFileId());
        return FileStatusResult.inProgress();
    }
}
