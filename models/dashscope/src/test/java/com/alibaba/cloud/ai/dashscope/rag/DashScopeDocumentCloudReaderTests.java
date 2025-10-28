/*
 * Copyright 2024-2025 the original author or authors.
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
import com.alibaba.cloud.ai.dashscope.spec.DashScopeAPISpec;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ai.document.Document;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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

	private File testFile;

	@BeforeEach
	void setUp() throws IOException {
		// Initialize mocks and test objects
		MockitoAnnotations.openMocks(this);

		// Create test file
		testFile = tempDir.resolve(TEST_FILE_NAME).toFile();
		Files.writeString(testFile.toPath(), TEST_CONTENT);

		// Set up reader with options
		DashScopeDocumentCloudReaderOptions options = new DashScopeDocumentCloudReaderOptions(TEST_CATEGORY_ID);
		reader = new DashScopeDocumentCloudReader(testFile.getAbsolutePath(), dashScopeApi, options);

		// Mock successful file upload
		mockSuccessfulUpload();
	}

	@Test
	void testConstructorWithNonExistentFile() {
		// Test constructor with non-existent file
		String nonExistentPath = tempDir.resolve("nonexistent.txt").toString();
		assertThatThrownBy(() -> new DashScopeDocumentCloudReader(nonExistentPath, dashScopeApi,
				new DashScopeDocumentCloudReaderOptions()))
			.isInstanceOf(RuntimeException.class);
	}

	@Test
	void testSuccessfulDocumentParsing() throws IOException {
		// Test successful document parsing
		mockSuccessfulParsing();

		List<Document> documents = reader.get();

		assertThat(documents).hasSize(1);
		assertThat(documents.get(0).getText()).isEqualTo(TEST_CONTENT);
	}

	@Test
	void testParseFailure() throws IOException {
		// Test parse failure
		mockFailedParsing();

		assertThatThrownBy(() -> reader.get()).isInstanceOf(RuntimeException.class);
	}

	private void mockSuccessfulUpload() {
        DashScopeAPISpec.UploadRequest request = new DashScopeAPISpec.UploadRequest(TEST_CATEGORY_ID, TEST_FILE_NAME,
				TEST_FILE_SIZE, "md5");
		when(dashScopeApi.upload(any(File.class), any(DashScopeAPISpec.UploadRequest.class))).thenReturn(TEST_FILE_ID);
	}

	private void mockSuccessfulParsing() {
        DashScopeAPISpec.QueryFileResponseData successResponse = new DashScopeAPISpec.QueryFileResponseData(TEST_CATEGORY_ID,
				TEST_FILE_ID, TEST_FILE_NAME, TEST_FILE_TYPE, TEST_FILE_SIZE, "PARSE_SUCCESS", TEST_UPLOAD_TIME);
        DashScopeAPISpec.CommonResponse<DashScopeAPISpec.QueryFileResponseData> response = new DashScopeAPISpec.CommonResponse<>(
				"SUCCESS", "OK", successResponse);
		when(dashScopeApi.queryFileInfo(eq(TEST_CATEGORY_ID), any(DashScopeAPISpec.UploadRequest.QueryFileRequest.class)))
			.thenReturn(ResponseEntity.ok(response));
		when(dashScopeApi.getFileParseResult(eq(TEST_CATEGORY_ID),
				any(DashScopeAPISpec.UploadRequest.QueryFileRequest.class)))
			.thenReturn(TEST_CONTENT);
	}

	private void mockFailedParsing() {
        DashScopeAPISpec.QueryFileResponseData failedResponse = new DashScopeAPISpec.QueryFileResponseData(TEST_CATEGORY_ID,
				TEST_FILE_ID, TEST_FILE_NAME, TEST_FILE_TYPE, TEST_FILE_SIZE, "PARSE_FAILED", TEST_UPLOAD_TIME);
        DashScopeAPISpec.CommonResponse<DashScopeAPISpec.QueryFileResponseData> response = new DashScopeAPISpec.CommonResponse<>(
				"FAILED", "Parse failed", failedResponse);
		when(dashScopeApi.queryFileInfo(eq(TEST_CATEGORY_ID), any(DashScopeAPISpec.UploadRequest.QueryFileRequest.class)))
			.thenReturn(ResponseEntity.ok(response));
	}

	private void mockPollingTimeout() {
        DashScopeAPISpec.QueryFileResponseData processingResponse = new DashScopeAPISpec.QueryFileResponseData(TEST_CATEGORY_ID,
				TEST_FILE_ID, TEST_FILE_NAME, TEST_FILE_TYPE, TEST_FILE_SIZE, "PROCESSING", TEST_UPLOAD_TIME);
        DashScopeAPISpec.CommonResponse<DashScopeAPISpec.QueryFileResponseData> response = new DashScopeAPISpec.CommonResponse<>(
				"SUCCESS", "Processing", processingResponse);
		when(dashScopeApi.queryFileInfo(eq(TEST_CATEGORY_ID), any(DashScopeAPISpec.UploadRequest.QueryFileRequest.class)))
			.thenReturn(ResponseEntity.ok(response));
	}

}
