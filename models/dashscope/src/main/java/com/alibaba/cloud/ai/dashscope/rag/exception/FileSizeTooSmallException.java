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
 * Exception thrown when file size is below the minimum allowed limit
 *
 * @author kevin
 * @since 2025/11/27
 */
public class FileSizeTooSmallException extends IllegalArgumentException {

    private final long fileSize;
    private final long minFileSize;

    /**
     * Constructor
     *
     * @param fileSize    actual file size
     * @param minFileSize minimum allowed file size
     */
    public FileSizeTooSmallException(long fileSize, long minFileSize) {
        super(String.format("File size (%s) is below minimum allowed size (%s)",
                            FileSizeFormatter.format(fileSize), FileSizeFormatter.format(minFileSize)));
        this.fileSize = fileSize;
        this.minFileSize = minFileSize;
    }

    public long getFileSize() {
        return fileSize;
    }

    public long getMinFileSize() {
        return minFileSize;
    }

}
