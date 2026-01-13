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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.common.DashScopeApiConstants;
import com.alibaba.cloud.ai.dashscope.common.DashScopeException;
import com.alibaba.cloud.ai.dashscope.common.ErrorCodeEnum;
import com.alibaba.cloud.ai.dashscope.rag.context.DocumentProcessContext;
import com.alibaba.cloud.ai.dashscope.rag.exception.DashScopeDocumentException;
import com.alibaba.cloud.ai.dashscope.rag.exception.DocumentParseTimeoutException;
import com.alibaba.cloud.ai.dashscope.rag.handler.DefaultFileStatusHandler;
import com.alibaba.cloud.ai.dashscope.rag.handler.FileStatusHandler;
import com.alibaba.cloud.ai.dashscope.rag.handler.FileStatusResult;
import com.alibaba.cloud.ai.dashscope.rag.validation.FileValidator;
import com.alibaba.cloud.ai.dashscope.spec.DashScopeApiSpec;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentReader;
import org.springframework.http.ResponseEntity;

/**
 * @author nuocheng.lxm
 * @since 2024/7/22 14:40 百炼云端文档解析，主要是走当前数据中心逻辑
 */
public class DashScopeDocumentCloudReader implements DocumentReader {

    private static final Logger logger = LoggerFactory.getLogger(DashScopeDocumentCloudReader.class);

    private final DashScopeApi dashScopeApi;
    private final DashScopeDocumentCloudReaderOptions readerConfig;
    private final DashScopeDocumentCloudReaderConfig clientConfig;
    private final File file;
    private final FileStatusHandler fileHandler;
    private final FileValidator fileValidator;

    /**
     * Constructor with default config
     *
     * @param filePath     file path to read
     * @param dashScopeApi DashScope API client
     * @param options      API options
     */
    public DashScopeDocumentCloudReader(String filePath,
                                        DashScopeApi dashScopeApi,
                                        DashScopeDocumentCloudReaderOptions options) {
        this(filePath, dashScopeApi, options, new DashScopeDocumentCloudReaderConfig());
    }

    /**
     * Constructor
     *
     * @param filePath     file path
     * @param dashScopeApi DashScope API client
     * @param readerConfig reader configuration (can be null, default config will be used)
     * @param clientConfig client configuration (can be null, default config will be used)
     * @throws IllegalArgumentException when file does not exist or is not readable
     */
    public DashScopeDocumentCloudReader(String filePath, DashScopeApi dashScopeApi,
                                        DashScopeDocumentCloudReaderOptions readerConfig,
                                        DashScopeDocumentCloudReaderConfig clientConfig) {
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("File path must not be null or empty");
        }
        if (dashScopeApi == null) {
            throw new IllegalArgumentException("DashScopeApi must not be null");
        }

        this.file = new File(filePath);
        this.dashScopeApi = dashScopeApi;
        this.readerConfig = readerConfig != null ? readerConfig : new DashScopeDocumentCloudReaderOptions();
        this.clientConfig = clientConfig != null ? clientConfig : new DashScopeDocumentCloudReaderConfig();
        this.fileHandler = new DefaultFileStatusHandler();
        this.fileValidator = new FileValidator(this.clientConfig); // Initialize validator

        validateFile();
    }

    /**
     * Validates file validity
     */
    private void validateFile() {
        fileValidator.validate(file);
    }

    /**
     * Reads and parses the document
     *
     * <p>Important: This method never returns null
     * <ul>
     *   <li>On success: Returns a list containing the parsed document</li>
     *   <li>On failure: Throws DashScopeException or its subclasses</li>
     * </ul>
     *
     * @return non-null list of documents
     * @throws DashScopeException when document processing fails
     */
    @Override
    public List<Document> get() {
        logger.info("Starting document processing for file: {}", file.getName());

        DocumentProcessContext context = new DocumentProcessContext();
        FileInputStream fileInputStream = null;

        try {
            // Step 1: Calculate file MD5
            fileInputStream = new FileInputStream(file);
            context.setFileMD5(DigestUtils.md5Hex(fileInputStream));
            logger.debug("File MD5 calculated: {} for file: {}", context.getFileMD5(), file.getName());

            // Step 2: Upload file
            context.setFileId(uploadFile(context.getFileMD5()));
            logger.info("File uploaded successfully. FileId: {}, FileName: {}",
                        context.getFileId(), file.getName());

            // Step 3: Poll for parsing status
            pollAndWaitForCompletion(context);

            // Step 4: Download parse result
            String parseResult = downloadParseResult(context.getFileId());

            // Step 5: Convert to Document
            Document document = toDocument(context.getFileId(), parseResult);

            logger.info("Document processing completed successfully. FileId: {}, FileName: {}",
                        context.getFileId(), file.getName());

            return Collections.singletonList(document);

        } catch (DashScopeDocumentException e) {
            // Document-specific exception, add context and rethrow
            e.withFilePath(file.getAbsolutePath());
            if (context.getFileId() != null) {
                e.withFileId(context.getFileId());
            }
            logger.error("Document processing failed: {}", e.getMessage());
            throw e;

        } catch (IOException e) {
            // IO exception
            logger.error("IO error while processing file: {}", file.getName(), e);
            throw new DashScopeDocumentException(ErrorCodeEnum.READER_PARSE_FILE_ERROR);

        } catch (InterruptedException e) {
            // Interrupted exception
            Thread.currentThread().interrupt();
            logger.error("Document processing interrupted for file: {}", file.getName(), e);
            throw new DashScopeDocumentException(ErrorCodeEnum.READER_PARSE_FILE_ERROR);

        } catch (Exception e) {
            // Unexpected exception
            logger.error("Unexpected error processing file: {}, FileId: {}",
                         file.getName(), context.getFileId(), e);
            throw new DashScopeDocumentException(ErrorCodeEnum.READER_PARSE_FILE_ERROR);

        } finally {
            closeQuietly(fileInputStream);
        }
    }

    /**
     * Uploads file to Bailian cloud
     */
    private String uploadFile(String fileMD5) {
        DashScopeApiSpec.UploadRequest uploadRequest = new DashScopeApiSpec.UploadRequest(
                readerConfig.getCategoryId(),
                file.getName(),
                file.length(),
                fileMD5
        );

        String fileId = dashScopeApi.upload(file, uploadRequest);

        if (fileId == null || fileId.trim().isEmpty()) {
            logger.error("Upload returned empty fileId for file: {}", file.getName());
            throw new DashScopeDocumentException(ErrorCodeEnum.READER_PARSE_FILE_ERROR);
        }

        return fileId;
    }

    /**
     * Polls and waits for file parsing completion
     *
     * <p>Uses Strategy Pattern to handle different file statuses:
     * <ul>
     *   <li>PARSE_SUCCESS: Parsing succeeded, return</li>
     *   <li>PARSE_FAILED: Parsing failed, throw exception</li>
     * </ul>
     *
     * @param context document processing context
     * @throws InterruptedException when thread is interrupted
     * @throws DashScopeException   when parsing fails or times out
     */
    private void pollAndWaitForCompletion(DocumentProcessContext context) throws InterruptedException {

        int tryCount = 0;
        long startTime = System.currentTimeMillis();
        int maxRetryCount = getMaxRetryCount();

        // Initial wait before first query to allow server initialization
        long initialWaitMillis = clientConfig.getInitialWaitMillis();
        if (initialWaitMillis > 0) {
            logger.debug("Initial wait {}ms before first status check", initialWaitMillis);
            Thread.sleep(initialWaitMillis);
        }

        while (tryCount < maxRetryCount) {

            // Query file status
            ResponseEntity<DashScopeApiSpec.CommonResponse<DashScopeApiSpec.QueryFileResponseData>>
                    response = queryFileStatus(context.getFileId());

            // Validate response
            if (response == null || response.getBody() == null) {
                logger.warn("Received null response for fileId: {}, attempt: {}/{}",
                            context.getFileId(), tryCount + 1, maxRetryCount);
                waitBeforeNextRetry(tryCount);
                tryCount++;
                continue;
            }

            DashScopeApiSpec.QueryFileResponseData data = response.getBody().data();
            if (data == null) {
                logger.warn("Received null data for fileId: {}, attempt: {}/{}",
                            context.getFileId(), tryCount + 1, maxRetryCount);
                waitBeforeNextRetry(tryCount);
                tryCount++;
                continue;
            }

            String fileStatus = data.status();
            logger.debug("File status check: FileId={}, Status={}, Attempt={}/{}",
                         context.getFileId(), fileStatus, tryCount + 1, maxRetryCount);

            // Use Strategy Pattern to handle different statuses
            FileStatusResult result = fileHandler.handle(context, response);

            // Process result
            if (result.isCompleted()) {
                if (result.isSuccess()) {
                    long elapsed = System.currentTimeMillis() - startTime;
                    logger.info("File parsing succeeded: FileId={}, Attempts={}, ElapsedTime={}ms",
                                context.getFileId(), tryCount + 1, elapsed);
                    return;
                } else {
                    // Parsing failed
                    logger.error("File parsing failed: FileId={}, ErrorMessage={}", context.getFileId(),
                                 result.getErrorMessage());
                    throw new DashScopeException(ErrorCodeEnum.READER_PARSE_FILE_ERROR);
                }
            }

            // Continue waiting
            waitBeforeNextRetry(tryCount);
            tryCount++;
        }

        // Timeout handling - never returns null, throws exception instead
        long totalElapsed = System.currentTimeMillis() - startTime;
        logger.error("File parsing timed out: FileId={}, TotalElapsedTime={}ms, ErrorMessage={}",
                     context.getFileId(), totalElapsed,
                     String.format("File parsing timeout after %d attempts (%d ms) for fileId: %s, fileName: %s",
                                   tryCount, totalElapsed, context.getFileId(), file.getName()));
        throw new DocumentParseTimeoutException(
                String.format("File parsing timeout for fileId: %s, fileName: %s",
                              context.getFileId(), file.getName()),
                tryCount, totalElapsed)
                .withFileId(context.getFileId())
                .withFilePath(context.getFileMD5());
    }

    /**
     * Queries file parsing status
     */
    private ResponseEntity<DashScopeApiSpec.CommonResponse<
            DashScopeApiSpec.QueryFileResponseData>> queryFileStatus(String fileId) {
        return dashScopeApi.queryFileInfo(
                readerConfig.getCategoryId(),
                new DashScopeApiSpec.UploadRequest.QueryFileRequest(fileId)
        );
    }

    /**
     * Downloads parse result
     */
    private String downloadParseResult(String fileId) {
        String parseResult = dashScopeApi.getFileParseResult(
                readerConfig.getCategoryId(),
                new DashScopeApiSpec.UploadRequest.QueryFileRequest(fileId)
        );

        if (parseResult == null || parseResult.trim().isEmpty()) {
            logger.warn("Downloaded empty parse result for fileId: {}", fileId);
            return "";
        }

        return parseResult;
    }

    /**
     * Waits before retry
     *
     * <p>Supports exponential backoff strategy to reduce API call frequency
     */
    private void waitBeforeNextRetry(int attemptCount) throws InterruptedException {
        long delay = calculateRetryDelay(attemptCount);
        logger.debug("Waiting {}ms before next attempt", delay);
        Thread.sleep(delay);
    }

    /**
     * Calculates retry delay
     *
     * <p>Can choose fixed delay or exponential backoff strategy based on configuration
     */
    private long calculateRetryDelay(int attemptCount) {
        // If exponential backoff is enabled in config
        if (clientConfig.isUseExponentialBackoff()) {
            long baseDelay = clientConfig.getRetryIntervalMillis();
            double multiplier = clientConfig.getBackoffMultiplier();
            long maxDelay = clientConfig.getMaxRetryIntervalMillis();

            long delay = (long) (baseDelay * Math.pow(multiplier, attemptCount));
            return Math.min(delay, maxDelay);
        }

        // Default fixed delay
        return clientConfig.getRetryIntervalMillis();
    }

    /**
     * Gets maximum retry count
     */
    private int getMaxRetryCount() {
        // Use configured value if set, otherwise use default
        return clientConfig.getMaxRetryAttempts() > 0
                ? clientConfig.getMaxRetryAttempts()
                : DashScopeApiConstants.MAX_TRY_COUNT;
    }

    /**
     * Converts to Document object
     */
    private Document toDocument(String fileId, String parseResultText) {
        Map<String, Object> metaData = new HashMap<>();
        metaData.put("parse_fmt_type", "DASHSCOPE_DOCMIND");
        metaData.put("file_id", fileId);
        metaData.put("file_name", file.getName());
        metaData.put("file_size", file.length());

        return new Document(fileId, parseResultText, metaData);
    }

    /**
     * Closes resource quietly
     */
    private void closeQuietly(AutoCloseable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception e) {
                logger.debug("Error closing resource: {}", e.getMessage());
            }
        }
    }

}
