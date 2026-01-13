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
package com.alibaba.cloud.ai.dashscope.rag.exception;

import com.alibaba.cloud.ai.dashscope.common.DashScopeException;
import com.alibaba.cloud.ai.dashscope.common.ErrorCodeEnum;

/**
 * Base exception for DashScope document processing operations
 *
 * <p>This exception serves as the parent class for all document-related
 * exceptions in the DashScope document reader module. It extends
 * {@link DashScopeException} to integrate with the existing exception hierarchy.
 *
 * <p>Exception hierarchy:
 * <pre>
 * RuntimeException
 *   └── DashScopeException
 *         └── DashScopeDocumentException
 *               ├── FileSizeTooSmallException
 *               ├── FileSizeExceededException
 *               └── DocumentParseTimeoutException
 * </pre>
 *
 * @author kevin
 * @since 2025/11/27
 */
public class DashScopeDocumentException extends DashScopeException {

    private String filePath;
    private String fileId;

    public DashScopeDocumentException(String message) {
        super(message);
    }

    public DashScopeDocumentException(ErrorCodeEnum errorCode) {
        super(errorCode);
    }

    public DashScopeDocumentException(String message, Throwable cause) {
        super(message, cause);
    }

    // ==================== Getters and Setters ====================

    public String getFilePath() {
        return filePath;
    }

    public DashScopeDocumentException withFilePath(String filePath) {
        this.filePath = filePath;
        return this;
    }

    public String getFileId() {
        return fileId;
    }

    public DashScopeDocumentException withFileId(String fileId) {
        this.fileId = fileId;
        return this;
    }

    @Override
    public String getMessage() {
        StringBuilder sb = new StringBuilder(super.getMessage());

        if (filePath != null) {
            sb.append(" [filePath=").append(filePath).append("]");
        }

        if (fileId != null) {
            sb.append(" [fileId=").append(fileId).append("]");
        }

        return sb.toString();
    }
}
