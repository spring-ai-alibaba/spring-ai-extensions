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

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.rag.exception.DashScopeDocumentException;
import com.alibaba.cloud.ai.dashscope.rag.exception.DocumentParseTimeoutException;
import com.alibaba.cloud.ai.dashscope.rag.exception.FileSizeExceededException;
import com.alibaba.cloud.ai.dashscope.rag.exception.FileSizeTooSmallException;
import com.alibaba.cloud.ai.dashscope.spec.DashScopeApiSpec;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ai.document.Document;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test cases for DashScopeDocumentCloudReader. Tests cover file handling, document
 * parsing, and error scenarios.
 *
 * @author yuluo
 * @author <a href="mailto:yuluo08290126@gmail.com">yuluo</a>
 * @author brianxiadong
 * @since 1.0.0-M5.1
 */
class DashScopeDocumentCloudReaderTests {

    private static final String TEST_CATEGORY_ID = "test-category";
    private static final String TEST_FILE_ID = "test-file-id";
    private static final String TEST_CONTENT = "Test content";
    private static final String TEST_FILE_NAME = "test.txt";
    private static final String TEST_FILE_TYPE = "txt";
    private static final long TEST_FILE_SIZE = 1024L;
    private static final String TEST_UPLOAD_TIME = "2024-01-01 00:00:00";

    @Mock
    private DashScopeApi dashScopeApi;

    @TempDir
    Path tempDir;

    private DashScopeDocumentCloudReader reader;
    private DashScopeDocumentCloudReaderOptions options;
    private DashScopeDocumentCloudReaderConfig config;

    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);

        // Separate API options and client config
        options = new DashScopeDocumentCloudReaderOptions(TEST_CATEGORY_ID);
        config = new DashScopeDocumentCloudReaderConfig();
        config.setMaxRetryAttempts(3);
        config.setRetryIntervalMillis(10L); // Short interval for testing

        // Create test file
        File testFile = tempDir.resolve(TEST_FILE_NAME).toFile();
        Files.writeString(testFile.toPath(), TEST_CONTENT);

        // Set up reader with options and config
        reader = new DashScopeDocumentCloudReader(testFile.getAbsolutePath(), dashScopeApi, options, config);

        // Mock successful file upload
        mockSuccessfulUpload();
    }

    @Test
    void testSuccessfulDocumentProcessing() throws IOException {
        // Create test file
        File testFile = createTestFile("test.txt", "Test content");

        // Mock upload response
        when(dashScopeApi.upload(
                any(File.class),
                any(DashScopeApiSpec.UploadRequest.class))
        ).thenReturn("file-123");

        // Mock query response - success immediately
        DashScopeApiSpec.CommonResponse<DashScopeApiSpec.QueryFileResponseData> successResponse =
                createMockResponse("PARSE_SUCCESS", null, null);
        when(dashScopeApi.queryFileInfo(anyString(), any()))
                .thenReturn(new ResponseEntity<>(successResponse, HttpStatus.OK));

        // Mock parse result
        when(dashScopeApi.getFileParseResult(anyString(), any()))
                .thenReturn("Parsed content");

        // Execute
        DashScopeDocumentCloudReader reader = new DashScopeDocumentCloudReader(
                testFile.getAbsolutePath(), dashScopeApi, options, config);
        List<Document> documents = reader.get();

        // Verify
        assertNotNull(documents);
        assertEquals(1, documents.size());
        assertEquals("file-123", documents.get(0).getId());
        assertEquals("Parsed content", documents.get(0).getText());

        verify(dashScopeApi, times(1)).upload(any(), any());
        verify(dashScopeApi, times(1)).queryFileInfo(anyString(), any());
        verify(dashScopeApi, times(1)).getFileParseResult(anyString(), any());
    }

    @Test
    void testFileNotFound() {
        assertThrows(IllegalArgumentException.class, () -> {
            new DashScopeDocumentCloudReader(
                    "/non/existent/file.txt", dashScopeApi, options, config);
        });
    }

    @Test
    void testFileTooLarge() throws IOException {
        // Set small max file size in config
        config.setMaxFileSize(10L);

        // Create larger file
        File testFile = createTestFile("large.txt", "This content is larger than 10 bytes");

        assertThrows(FileSizeExceededException.class, () -> {
            new DashScopeDocumentCloudReader(
                    testFile.getAbsolutePath(), dashScopeApi, options, config);
        });
    }

    @Test
    void testUploadFailure() throws IOException {
        File testFile = createTestFile("test.txt", "Content");

        when(dashScopeApi.upload(any(), any()))
                .thenThrow(new RuntimeException("Network error"));

        DashScopeDocumentCloudReader reader = new DashScopeDocumentCloudReader(
                testFile.getAbsolutePath(), dashScopeApi, options, config);

        assertThrows(DashScopeDocumentException.class, () -> reader.get());
    }

    @Test
    void testParseTimeout() throws IOException {
        File testFile = createTestFile("test.txt", "Content");

        when(dashScopeApi.upload(any(), any())).thenReturn("file-123");

        // Always return PARSING status
        DashScopeApiSpec.CommonResponse<DashScopeApiSpec.QueryFileResponseData> parsingResponse =
                createMockResponse("PARSING", null, null);
        when(dashScopeApi.queryFileInfo(anyString(), any()))
                .thenReturn(new ResponseEntity<>(parsingResponse, HttpStatus.OK));

        DashScopeDocumentCloudReader reader = new DashScopeDocumentCloudReader(
                testFile.getAbsolutePath(), dashScopeApi, options, config);

        DocumentParseTimeoutException exception =
                assertThrows(DocumentParseTimeoutException.class, reader::get);

        assertEquals(3, exception.getAttemptCount());
        assertTrue(exception.getElapsedTimeMs() > 0);
    }

    @Test
    void testParsingWithRetries() throws IOException {
        File testFile = createTestFile("test.txt", "Content");

        when(dashScopeApi.upload(any(), any())).thenReturn("file-123");

        // Return PARSING twice, then SUCCESS
        DashScopeApiSpec.CommonResponse<DashScopeApiSpec.QueryFileResponseData> parsingResponse =
                createMockResponse("PARSING", null, null);
        DashScopeApiSpec.CommonResponse<DashScopeApiSpec.QueryFileResponseData> successResponse =
                createMockResponse("PARSE_SUCCESS", null, null);

        when(dashScopeApi.queryFileInfo(anyString(), any()))
                .thenReturn(new ResponseEntity<>(parsingResponse, HttpStatus.OK))
                .thenReturn(new ResponseEntity<>(parsingResponse, HttpStatus.OK))
                .thenReturn(new ResponseEntity<>(successResponse, HttpStatus.OK));

        when(dashScopeApi.getFileParseResult(anyString(), any()))
                .thenReturn("Parsed content");

        DashScopeDocumentCloudReader reader = new DashScopeDocumentCloudReader(
                testFile.getAbsolutePath(), dashScopeApi, options, config);
        List<Document> documents = reader.get();

        assertNotNull(documents);
        assertEquals(1, documents.size());

        // Verify queried 3 times
        verify(dashScopeApi, times(3)).queryFileInfo(anyString(), any());
    }

    @Test
    void testConstructorWithNonExistentFile() {
        // Test constructor with non-existent file
        String nonExistentPath = tempDir.resolve("nonexistent.txt").toString();
        assertThatThrownBy(() -> new DashScopeDocumentCloudReader(
                nonExistentPath, dashScopeApi, options, config))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void testSuccessfulDocumentParsing() {
        // Test successful document parsing
        mockSuccessfulParsing();

        List<Document> documents = reader.get();

        assertThat(documents).hasSize(1);
        assertThat(documents.get(0).getText()).isEqualTo(TEST_CONTENT);
    }

    @Test
    void testParseFailure() {
        // Test parse failure
        mockFailedParsing();

        assertThatThrownBy(() -> reader.get()).isInstanceOf(RuntimeException.class);
    }

    @Test
    void testConfigWithExponentialBackoff() throws IOException {
        // Test exponential backoff configuration
        DashScopeDocumentCloudReaderConfig customConfig = new DashScopeDocumentCloudReaderConfig()
                .withMaxRetryAttempts(5)
                .withRetryIntervalSeconds(2)
                .withMaxRetryIntervalSeconds(60);

        File testFile = createTestFile("test.txt", "Content");

        when(dashScopeApi.upload(any(), any())).thenReturn("file-123");

        DashScopeApiSpec.CommonResponse<DashScopeApiSpec.QueryFileResponseData> parsingResponse =
                createMockResponse("PARSING", null, null);
        when(dashScopeApi.queryFileInfo(anyString(), any()))
                .thenReturn(new ResponseEntity<>(parsingResponse, HttpStatus.OK));

        DashScopeDocumentCloudReader reader = new DashScopeDocumentCloudReader(
                testFile.getAbsolutePath(), dashScopeApi, options, customConfig);

        assertThrows(DocumentParseTimeoutException.class, reader::get);

        // Verify retry attempts
        verify(dashScopeApi, times(5)).queryFileInfo(anyString(), any());
    }

    @Test
    void testConfigWithCustomFileSize() throws IOException {
        // Test custom file size validation - file too small
        DashScopeDocumentCloudReaderConfig customConfig = new DashScopeDocumentCloudReaderConfig()
                .withMaxFileSizeMB(1)
                .withMinFileSizeBytes(10);

        File testFile = createTestFile("test.txt", "Small");

        assertThrows(FileSizeTooSmallException.class, () -> {
            new DashScopeDocumentCloudReader(
                    testFile.getAbsolutePath(), dashScopeApi, options, customConfig);
        });
    }

    @Test
    void testConfigWithFileTooLarge() throws IOException {
        // Test custom file size validation - file too large
        DashScopeDocumentCloudReaderConfig customConfig = new DashScopeDocumentCloudReaderConfig()
                .withMaxFileSizeMB(1);  // 1MB limit

        // Create a file larger than 1MB
        File testFile = createTestFile("large.txt", "x".repeat(2 * 1024 * 1024));

        assertThrows(FileSizeExceededException.class, () -> {
            new DashScopeDocumentCloudReader(
                    testFile.getAbsolutePath(), dashScopeApi, options, customConfig);
        });
    }

    @Test
    void testConfigWithDisabledFileSizeValidation() throws IOException {
        // Test disabled file size validation
        DashScopeDocumentCloudReaderConfig customConfig = new DashScopeDocumentCloudReaderConfig()
                .withoutFileSizeValidation();

        // Create a very small file that would normally fail validation
        File testFile = createTestFile("tiny.txt", "");

        // Should not throw exception with validation disabled
        when(dashScopeApi.upload(any(), any())).thenReturn("file-123");

        DashScopeApiSpec.CommonResponse<DashScopeApiSpec.QueryFileResponseData> successResponse =
                createMockResponse("PARSE_SUCCESS", null, null);
        when(dashScopeApi.queryFileInfo(anyString(), any()))
                .thenReturn(new ResponseEntity<>(successResponse, HttpStatus.OK));
        when(dashScopeApi.getFileParseResult(anyString(), any()))
                .thenReturn("Content");

        DashScopeDocumentCloudReader reader = new DashScopeDocumentCloudReader(
                testFile.getAbsolutePath(), dashScopeApi, options, customConfig);

        List<Document> documents = reader.get();
        assertNotNull(documents);
    }

    @Test
    void testOptionsWithCustomCategory() throws IOException {
        // Test custom category in options
        DashScopeDocumentCloudReaderOptions customOptions =
                new DashScopeDocumentCloudReaderOptions("custom-category");

        File testFile = createTestFile("test.txt", "Content");

        when(dashScopeApi.upload(any(), any())).thenReturn("file-123");

        DashScopeApiSpec.CommonResponse<DashScopeApiSpec.QueryFileResponseData> successResponse =
                createMockResponse("PARSE_SUCCESS", null, null);
        when(dashScopeApi.queryFileInfo(anyString(), any()))
                .thenReturn(new ResponseEntity<>(successResponse, HttpStatus.OK));
        when(dashScopeApi.getFileParseResult(anyString(), any()))
                .thenReturn("Content");

        DashScopeDocumentCloudReader reader = new DashScopeDocumentCloudReader(
                testFile.getAbsolutePath(), dashScopeApi, customOptions, config);

        List<Document> documents = reader.get();

        assertNotNull(documents);
        assertEquals("custom-category", customOptions.getCategoryId());
    }

    // Helper methods

    private File createTestFile(String filename, String content) throws IOException {
        File file = tempDir.resolve(filename).toFile();
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(content);
        }
        return file;
    }

    private DashScopeApiSpec.CommonResponse<DashScopeApiSpec.QueryFileResponseData> createMockResponse(
            String status, String errorCode, String errorMessage) {

        DashScopeApiSpec.CommonResponse<DashScopeApiSpec.QueryFileResponseData> response =
                mock(DashScopeApiSpec.CommonResponse.class);
        DashScopeApiSpec.QueryFileResponseData data =
                mock(DashScopeApiSpec.QueryFileResponseData.class);

        when(response.data()).thenReturn(data);
        when(data.status()).thenReturn(status);

        if (errorCode != null) {
            when(response.code()).thenReturn(errorCode);
        }
        if (errorMessage != null) {
            when(response.message()).thenReturn(errorMessage);
        }

        return response;
    }

    private void mockSuccessfulUpload() {
        DashScopeApiSpec.UploadRequest request = new DashScopeApiSpec.UploadRequest(
                TEST_CATEGORY_ID, TEST_FILE_NAME, TEST_FILE_SIZE, "md5");
        when(dashScopeApi.upload(any(File.class), any(DashScopeApiSpec.UploadRequest.class)))
                .thenReturn(TEST_FILE_ID);
    }

    private void mockSuccessfulParsing() {
        DashScopeApiSpec.QueryFileResponseData successResponse =
                new DashScopeApiSpec.QueryFileResponseData(
                        TEST_CATEGORY_ID,
                        TEST_FILE_ID,
                        TEST_FILE_NAME,
                        TEST_FILE_TYPE,
                        TEST_FILE_SIZE,
                        "PARSE_SUCCESS",
                        TEST_UPLOAD_TIME);
        DashScopeApiSpec.CommonResponse<DashScopeApiSpec.QueryFileResponseData> response =
                new DashScopeApiSpec.CommonResponse<>("SUCCESS", "OK", successResponse);

        when(dashScopeApi.queryFileInfo(
                eq(TEST_CATEGORY_ID),
                any(DashScopeApiSpec.UploadRequest.QueryFileRequest.class)))
                .thenReturn(ResponseEntity.ok(response));

        when(dashScopeApi.getFileParseResult(
                eq(TEST_CATEGORY_ID),
                any(DashScopeApiSpec.UploadRequest.QueryFileRequest.class)))
                .thenReturn(TEST_CONTENT);
    }

    private void mockFailedParsing() {
        DashScopeApiSpec.QueryFileResponseData failedResponse =
                new DashScopeApiSpec.QueryFileResponseData(
                        TEST_CATEGORY_ID,
                        TEST_FILE_ID,
                        TEST_FILE_NAME,
                        TEST_FILE_TYPE,
                        TEST_FILE_SIZE,
                        "PARSE_FAILED",
                        TEST_UPLOAD_TIME);
        DashScopeApiSpec.CommonResponse<DashScopeApiSpec.QueryFileResponseData> response =
                new DashScopeApiSpec.CommonResponse<>("FAILED", "Parse failed", failedResponse);

        when(dashScopeApi.queryFileInfo(
                eq(TEST_CATEGORY_ID),
                any(DashScopeApiSpec.UploadRequest.QueryFileRequest.class)))
                .thenReturn(ResponseEntity.ok(response));
    }
}
