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

/**
 * Exception thrown when document parsing times out
 *
 * @author kevin
 * @since 2025/11/27
 */
public class DocumentParseTimeoutException extends DashScopeDocumentException {

    private final int attemptCount;
    private final long elapsedTimeMs;

    /**
     * Constructor
     *
     * @param message       the detail message
     * @param attemptCount  number of attempts made
     * @param elapsedTimeMs elapsed time in milliseconds
     */
    public DocumentParseTimeoutException(String message, int attemptCount, long elapsedTimeMs) {
        super(message);
        this.attemptCount = attemptCount;
        this.elapsedTimeMs = elapsedTimeMs;
    }

    public int getAttemptCount() {
        return attemptCount;
    }

    public long getElapsedTimeMs() {
        return elapsedTimeMs;
    }

    @Override
    public String getMessage() {
        return String.format("%s [attempts=%d, elapsedTimeMs=%d]",
                             super.getMessage(), attemptCount, elapsedTimeMs);
    }
}
