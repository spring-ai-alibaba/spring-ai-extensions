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

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * Test cases for DashScopeDocumentCloudReaderConfig
 *
 * @author kevin
 * @since 2025/12/01
 */
class DashScopeDocumentCloudReaderConfigTests {

    @Test
    void testDefaultConfiguration() {
        DashScopeDocumentCloudReaderConfig config = new DashScopeDocumentCloudReaderConfig();

        assertThat(config.getMaxRetryAttempts()).isEqualTo(10);
        assertThat(config.getInitialWaitMillis()).isEqualTo(3_000L);
        assertThat(config.getRetryIntervalMillis()).isEqualTo(30_000L);
        assertThat(config.getMaxRetryIntervalMillis()).isEqualTo(300_000L);
        assertThat(config.isUseExponentialBackoff()).isTrue();
        assertThat(config.getBackoffMultiplier()).isEqualTo(1.5);
        assertThat(config.getMaxFileSize()).isEqualTo(10 * 1024 * 1024L);
        assertThat(config.getMinFileSize()).isEqualTo(1L);
        assertThat(config.isEnableFileSizeValidation()).isTrue();
    }

    @Test
    void testBuilderChaining() {
        DashScopeDocumentCloudReaderConfig config = new DashScopeDocumentCloudReaderConfig()
                .withMaxRetryAttempts(15)
                .withRetryIntervalSeconds(5)
                .withInitialWaitMillis(2)
                .withMaxRetryIntervalSeconds(120)
                .withMaxFileSizeMB(100)
                .withMinFileSizeBytes(10)
                .withoutFileSizeValidation();

        assertThat(config.getMaxRetryAttempts()).isEqualTo(15);
        assertThat(config.getRetryIntervalMillis()).isEqualTo(5_000L);
        assertThat(config.getInitialWaitMillis()).isEqualTo(2_000L);
        assertThat(config.getMaxRetryIntervalMillis()).isEqualTo(120_000L);
        assertThat(config.getMaxFileSize()).isEqualTo(100 * 1024 * 1024L);
        assertThat(config.getMinFileSize()).isEqualTo(10L);
        assertThat(config.isEnableFileSizeValidation()).isFalse();
    }

    @Test
    void testSetters() {
        DashScopeDocumentCloudReaderConfig config = new DashScopeDocumentCloudReaderConfig();

        config.setMaxRetryAttempts(20);
        config.setRetryIntervalMillis(10_000L);
        config.setInitialWaitMillis(5_000L);
        config.setUseExponentialBackoff(false);
        config.setBackoffMultiplier(2.0);
        config.setMaxFileSize(50 * 1024 * 1024L);
        config.setEnableFileSizeValidation(false);

        assertThat(config.getMaxRetryAttempts()).isEqualTo(20);
        assertThat(config.getRetryIntervalMillis()).isEqualTo(10_000L);
        assertThat(config.getInitialWaitMillis()).isEqualTo(5_000L);
        assertThat(config.isUseExponentialBackoff()).isFalse();
        assertThat(config.getBackoffMultiplier()).isEqualTo(2.0);
        assertThat(config.getMaxFileSize()).isEqualTo(50 * 1024 * 1024L);
        assertThat(config.isEnableFileSizeValidation()).isFalse();
    }

    @Test
    void testTimeConversions() {
        DashScopeDocumentCloudReaderConfig config = new DashScopeDocumentCloudReaderConfig()
                .withRetryIntervalSeconds(10)
                .withInitialWaitMillis(5)
                .withMaxRetryIntervalSeconds(60);

        assertThat(config.getRetryIntervalMillis()).isEqualTo(10_000L);
        assertThat(config.getInitialWaitMillis()).isEqualTo(5_000L);
        assertThat(config.getMaxRetryIntervalMillis()).isEqualTo(60_000L);
    }

    @Test
    void testFileSizeConfiguration() {
        DashScopeDocumentCloudReaderConfig config = new DashScopeDocumentCloudReaderConfig()
                .withMaxFileSizeMB(50)
                .withMinFileSizeBytes(100);

        assertThat(config.getMaxFileSize()).isEqualTo(50 * 1024 * 1024L);
        assertThat(config.getMinFileSize()).isEqualTo(100L);
    }

    @Test
    void testDisableFileSizeValidation() {
        DashScopeDocumentCloudReaderConfig config = new DashScopeDocumentCloudReaderConfig()
                .withoutFileSizeValidation();

        assertThat(config.isEnableFileSizeValidation()).isFalse();
    }

    @Test
    void testToString() {
        DashScopeDocumentCloudReaderConfig config = new DashScopeDocumentCloudReaderConfig()
                .withMaxRetryAttempts(15);

        String toString = config.toString();

        assertThat(toString)
                .contains("maxRetryAttempts=15")
                .contains("useExponentialBackoff=true")
                .contains("enableFileSizeValidation=true");
    }
}
