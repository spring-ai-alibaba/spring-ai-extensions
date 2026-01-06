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
package com.alibaba.cloud.ai.dashscope.rag.context;

/**
 * Document processing context
 *
 * <p>Holds the state information during document processing workflow.
 * This context is passed between different processing stages to maintain
 * state consistency.
 *
 * @author kevin
 * @since 2025/11/27
 */
public class DocumentProcessContext {

    /**
     * File ID returned by upload API
     */
    private String fileId;

    /**
     * MD5 checksum of the file
     */
    private String fileMD5;

    /**
     * Default constructor
     */
    public DocumentProcessContext() {
    }

    /**
     * Constructor with parameters
     *
     * @param fileId  file ID
     * @param fileMD5 file MD5 checksum
     */
    public DocumentProcessContext(String fileId, String fileMD5) {
        this.fileId = fileId;
        this.fileMD5 = fileMD5;
    }

    // ==================== Getters and Setters ====================

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getFileMD5() {
        return fileMD5;
    }

    public void setFileMD5(String fileMD5) {
        this.fileMD5 = fileMD5;
    }

    @Override
    public String toString() {
        return "DocumentProcessContext{" +
               "fileId='" + fileId + '\'' +
               ", fileMD5='" + fileMD5 + '\'' +
               '}';
    }
}
