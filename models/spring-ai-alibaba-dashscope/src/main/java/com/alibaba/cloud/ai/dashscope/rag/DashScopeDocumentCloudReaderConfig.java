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
package com.alibaba.cloud.ai.dashscope.rag;

import com.alibaba.cloud.ai.dashscope.rag.util.FileSizeFormatter;

/**
 * DashScope document cloud reader client configuration
 *
 * <p>Client-side configurations for the document reader, including:
 * <ul>
 *   <li>Retry strategy (max attempts, intervals, backoff)</li>
 *   <li>File size validation (min/max sizes)</li>
 * </ul>
 *
 * <p>These configurations control client behavior and are not sent to the API.
 *
 * @author kevin
 * @see DashScopeDocumentCloudReaderOptions for API options
 * @since 2025/12/01
 */
public class DashScopeDocumentCloudReaderConfig {

    /**
     * Default maximum file size: 10MB
     */
    public static final long DEFAULT_MAX_FILE_SIZE = 10 * 1024 * 1024L;

    /**
     * Default minimum file size: 1 byte
     */
    public static final long DEFAULT_MIN_FILE_SIZE = 1L;

    /**
     * Maximum retry attempts
     * Default: 10 (use API default)
     */
    private int maxRetryAttempts;

    /**
     * Retry interval in milliseconds
     * Default: 30 seconds
     */
    private long retryIntervalMillis;

    /**
     * Initial wait time in milliseconds
     * Default: 3 seconds
     */
    private long initialWaitMillis;

    /**
     * Maximum retry interval in milliseconds
     * Default: 5 minutes
     */
    private long maxRetryIntervalMillis;

    /**
     * Whether to use exponential backoff
     * Default: true
     */
    private boolean useExponentialBackoff;

    /**
     * Backoff multiplier
     * Default: 1.5
     */
    private double backoffMultiplier;

    /**
     * Maximum file size in bytes
     * Default: 100MB
     */
    private long maxFileSize;

    /**
     * Minimum file size in bytes
     * Default: 1 byte
     */
    private long minFileSize;

    /**
     * Whether to enable file size validation
     * Default: true
     */
    private boolean enableFileSizeValidation;

    /**
     * Default constructor
     */
    public DashScopeDocumentCloudReaderConfig() {
        this.maxRetryAttempts = 10;
        this.initialWaitMillis = 3_000L;
        this.retryIntervalMillis = 30_000L;
        this.maxRetryIntervalMillis = 300_000L;
        this.useExponentialBackoff = true;
        this.backoffMultiplier = 1.5;
        this.maxFileSize = DEFAULT_MAX_FILE_SIZE;
        this.minFileSize = DEFAULT_MIN_FILE_SIZE;
        this.enableFileSizeValidation = true;
    }

    // ==================== Getters and Setters ====================

    public int getMaxRetryAttempts() {
        return maxRetryAttempts;
    }

    public void setMaxRetryAttempts(int maxRetryAttempts) {
        this.maxRetryAttempts = maxRetryAttempts;
    }

    public long getInitialWaitMillis() {
        return initialWaitMillis;
    }

    public void setInitialWaitMillis(long initialWaitMillis) {
        this.initialWaitMillis = initialWaitMillis;
    }

    public long getRetryIntervalMillis() {
        return retryIntervalMillis;
    }

    public void setRetryIntervalMillis(long retryIntervalMillis) {
        this.retryIntervalMillis = retryIntervalMillis;
    }

    public long getMaxRetryIntervalMillis() {
        return maxRetryIntervalMillis;
    }

    public void setMaxRetryIntervalMillis(long maxRetryIntervalMillis) {
        this.maxRetryIntervalMillis = maxRetryIntervalMillis;
    }

    public boolean isUseExponentialBackoff() {
        return useExponentialBackoff;
    }

    public void setUseExponentialBackoff(boolean useExponentialBackoff) {
        this.useExponentialBackoff = useExponentialBackoff;
    }

    public double getBackoffMultiplier() {
        return backoffMultiplier;
    }

    public void setBackoffMultiplier(double backoffMultiplier) {
        this.backoffMultiplier = backoffMultiplier;
    }

    public long getMaxFileSize() {
        return maxFileSize;
    }

    public void setMaxFileSize(long maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

    public long getMinFileSize() {
        return minFileSize;
    }

    public void setMinFileSize(long minFileSize) {
        this.minFileSize = minFileSize;
    }

    public boolean isEnableFileSizeValidation() {
        return enableFileSizeValidation;
    }

    public void setEnableFileSizeValidation(boolean enableFileSizeValidation) {
        this.enableFileSizeValidation = enableFileSizeValidation;
    }

    // ==================== Builder Methods ====================

    public DashScopeDocumentCloudReaderConfig withMaxRetryAttempts(int maxRetryAttempts) {
        this.maxRetryAttempts = maxRetryAttempts;
        return this;
    }

    public DashScopeDocumentCloudReaderConfig withRetryIntervalSeconds(long seconds) {
        this.retryIntervalMillis = seconds * 1000L;
        return this;
    }


    public DashScopeDocumentCloudReaderConfig withInitialWaitMillis(long seconds) {
        this.initialWaitMillis = seconds * 1000L;
        return this;
    }

    public DashScopeDocumentCloudReaderConfig withMaxRetryIntervalSeconds(long seconds) {
        this.maxRetryIntervalMillis = seconds * 1000L;
        return this;
    }

    public DashScopeDocumentCloudReaderConfig withMaxFileSizeMB(int megabytes) {
        this.maxFileSize = megabytes * 1024L * 1024L;
        return this;
    }

    public DashScopeDocumentCloudReaderConfig withMinFileSizeBytes(long bytes) {
        this.minFileSize = bytes;
        return this;
    }

    public DashScopeDocumentCloudReaderConfig withoutFileSizeValidation() {
        this.enableFileSizeValidation = false;
        return this;
    }

    @Override
    public String toString() {
        return "DashScopeDocumentCloudReaderConfig{" +
               "maxRetryAttempts=" + maxRetryAttempts +
               ", retryIntervalMillis=" + retryIntervalMillis +
               ", initialWaitMillis=" + initialWaitMillis +
               ", maxRetryIntervalMillis=" + maxRetryIntervalMillis +
               ", useExponentialBackoff=" + useExponentialBackoff +
               ", backoffMultiplier=" + backoffMultiplier +
               ", maxFileSize=" + FileSizeFormatter.format(maxFileSize) +
               ", minFileSize=" + FileSizeFormatter.format(minFileSize) +
               ", enableFileSizeValidation=" + enableFileSizeValidation +
               '}';
    }

}
