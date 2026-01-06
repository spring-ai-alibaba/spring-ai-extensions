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

import com.alibaba.cloud.ai.dashscope.rag.util.FileSizeFormatter;

/**
 * Exception thrown when file size exceeds the maximum allowed limit
 *
 * @author kevin
 * @since 2025/11/27
 */
public class FileSizeExceededException extends IllegalArgumentException {

    private final long fileSize;
    private final long maxFileSize;

    /**
     * Constructor
     *
     * @param fileSize    actual file size
     * @param maxFileSize maximum allowed file size
     */
    public FileSizeExceededException(long fileSize, long maxFileSize) {
        super(String.format("File size (%s) exceeds maximum allowed size (%s)",
                            FileSizeFormatter.format(fileSize), FileSizeFormatter.format(maxFileSize)));
        this.fileSize = fileSize;
        this.maxFileSize = maxFileSize;
    }

    public long getFileSize() {
        return fileSize;
    }

    public long getMaxFileSize() {
        return maxFileSize;
    }

}
