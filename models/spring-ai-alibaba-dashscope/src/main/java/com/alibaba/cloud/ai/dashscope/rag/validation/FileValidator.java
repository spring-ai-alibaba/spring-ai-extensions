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
package com.alibaba.cloud.ai.dashscope.rag.validation;

import java.io.File;

import com.alibaba.cloud.ai.dashscope.rag.DashScopeDocumentCloudReaderConfig;
import com.alibaba.cloud.ai.dashscope.rag.exception.FileSizeExceededException;
import com.alibaba.cloud.ai.dashscope.rag.exception.FileSizeTooSmallException;
import com.alibaba.cloud.ai.dashscope.rag.util.FileSizeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * File validator for validating file properties before upload
 *
 * <p>Validates:
 * <ul>
 *   <li>File existence</li>
 *   <li>File type (not a directory)</li>
 *   <li>File readability</li>
 *   <li>File size constraints</li>
 * </ul>
 *
 * @author kevin
 * @since 2025/11/27
 */
public class FileValidator {

    private static final Logger logger = LoggerFactory.getLogger(FileValidator.class);

    private final DashScopeDocumentCloudReaderConfig clientConfig;

    /**
     * Constructor
     *
     * @param clientConfig reader configuration
     */
    public FileValidator(DashScopeDocumentCloudReaderConfig clientConfig) {
        this.clientConfig = clientConfig;
    }

    /**
     * Validates file before processing
     *
     * @param file file to validate
     * @throws IllegalArgumentException when validation fails
     */
    public void validate(File file) {
        validateFileExists(file);
        validateFileType(file);
        validateFileReadable(file);
        validateFileSize(file);
    }

    /**
     * Validates file existence
     */
    private void validateFileExists(File file) {
        if (!file.exists()) {
            throw new IllegalArgumentException(
                    "File does not exist: " + file.getAbsolutePath());
        }
    }

    /**
     * Validates file type (must be a regular file, not a directory)
     */
    private void validateFileType(File file) {
        if (!file.isFile()) {
            throw new IllegalArgumentException(
                    "Path is not a file: " + file.getAbsolutePath());
        }
    }

    /**
     * Validates file readability
     */
    private void validateFileReadable(File file) {
        if (!file.canRead()) {
            throw new IllegalArgumentException(
                    "File is not readable: " + file.getAbsolutePath());
        }
    }

    /**
     * Validates file size constraints
     *
     * @throws FileSizeTooSmallException when file size is below minimum
     * @throws FileSizeExceededException when file size exceeds maximum
     */
    private void validateFileSize(File file) {
        // Skip validation if disabled
        if (!clientConfig.isEnableFileSizeValidation()) {
            logger.debug("File size validation is disabled for file: {}", file.getName());
            return;
        }

        long fileSize = file.length();
        long minSize = clientConfig.getMinFileSize();
        long maxSize = clientConfig.getMaxFileSize();

        // Check minimum size
        if (fileSize < minSize) {
            logger.error("File size {} is below minimum {}: {}",
                         FileSizeFormatter.format(fileSize), FileSizeFormatter.format(minSize), file.getName());
            throw new FileSizeTooSmallException(fileSize, minSize);
        }

        // Check maximum size
        if (fileSize > maxSize) {
            logger.error("File size {} exceeds maximum {}: {}",
                         FileSizeFormatter.format(fileSize), FileSizeFormatter.format(maxSize), file.getName());
            throw new FileSizeExceededException(fileSize, maxSize);
        }

        logger.debug("File size validation passed: {} ({})",
                     file.getName(), FileSizeFormatter.format(fileSize));
    }

}
