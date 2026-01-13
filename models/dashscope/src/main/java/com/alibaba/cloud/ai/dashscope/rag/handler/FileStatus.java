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
 * File parsing status enumeration
 *
 * <p>Represents the various states of a file during the parsing process
 * in the DashScope document cloud service.
 *
 * @author kevin
 * @since 2025/12/01
 */
public enum FileStatus {

    /**
     * File parsing completed successfully
     * This is a terminal state indicating the file is ready for use
     */
    PARSE_SUCCESS("PARSE_SUCCESS"),

    /**
     * File parsing failed
     * This is a terminal state indicating an error occurred during parsing
     */
    PARSE_FAILED("PARSE_FAILED"),

    /**
     * File is being parsed
     * This is a non-terminal state indicating the file is currently being parsed
     */
    PARSING("PARSING"),

    /**
     * File uploaded successfully
     * This is a terminal state indicating the file has been uploaded successfully
     */
    UPLOADED("UPLOADED"),

    /**
     * Unknown status
     */
    UNK("UNK");

    private final String value;

    FileStatus(String value) {
        this.value = value;
    }

    /**
     * Gets the string value of this status
     *
     * @return the status string value
     */
    public String getValue() {
        return value;
    }


    /**
     * Checks if this status represents a successful completion
     *
     * @return true if parsing completed successfully
     */
    public boolean isSuccess() {
        return this == PARSE_SUCCESS;
    }

    /**
     * Checks if this status represents a failure
     *
     * @return true if parsing failed
     */
    public boolean isFailed() {
        return this == PARSE_FAILED;
    }

    /**
     * Parses a string value into a FileStatus enum
     *
     * @param value the status string value
     * @return the corresponding FileStatus enum, or null if not found
     */
    public static FileStatus fromValue(String value) {
        if (value == null) {
            return null;
        }

        for (FileStatus status : values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }

        return null;
    }

    /**
     * Parses a string value into a FileStatus enum with a default fallback
     *
     * @param value         the status string value
     * @param defaultStatus the default status to return if value is not found
     * @return the corresponding FileStatus enum, or defaultStatus if not found
     */
    public static FileStatus fromValueOrDefault(String value, FileStatus defaultStatus) {
        FileStatus status = fromValue(value);
        return status != null ? status : defaultStatus;
    }

    @Override
    public String toString() {
        return value;
    }
}
