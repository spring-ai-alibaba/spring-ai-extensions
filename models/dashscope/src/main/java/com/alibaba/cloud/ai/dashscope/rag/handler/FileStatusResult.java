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

/**
 * File status processing result
 *
 * <p>Encapsulates the result of file status handling, indicating whether
 * the processing is completed, successful, and any error messages.
 *
 * @author kevin
 * @since 2025/11/27
 */
public class FileStatusResult {

    /**
     * Whether processing is completed (terminal state)
     */
    private final boolean completed;

    /**
     * Whether processing succeeded
     */
    private final boolean success;

    /**
     * Error message if processing failed
     */
    private final String errorMessage;

    /**
     * Private constructor
     *
     * @param completed    whether processing is completed
     * @param success      whether processing succeeded
     * @param errorMessage error message
     */
    private FileStatusResult(boolean completed, boolean success, String errorMessage) {
        this.completed = completed;
        this.success = success;
        this.errorMessage = errorMessage;
    }

    /**
     * Creates a success result
     *
     * @return success result
     */
    public static FileStatusResult success() {
        return new FileStatusResult(true, true, null);
    }

    /**
     * Creates a failure result
     *
     * @param errorMessage error message
     * @return failure result
     */
    public static FileStatusResult failure(String errorMessage) {
        return new FileStatusResult(true, false, errorMessage);
    }

    /**
     * Creates an in-progress result
     *
     * @return in-progress result
     */
    public static FileStatusResult inProgress() {
        return new FileStatusResult(false, false, null);
    }

    // ==================== Getters ====================

    public boolean isCompleted() {
        return completed;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public String toString() {
        return "FileStatusResult{" +
               "completed=" + completed +
               ", success=" + success +
               ", errorMessage='" + errorMessage + '\'' +
               '}';
    }
}
