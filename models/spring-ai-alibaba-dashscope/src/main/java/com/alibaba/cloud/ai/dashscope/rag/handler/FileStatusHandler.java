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

import com.alibaba.cloud.ai.dashscope.rag.context.DocumentProcessContext;
import com.alibaba.cloud.ai.dashscope.spec.DashScopeApiSpec;

import org.springframework.http.ResponseEntity;

/**
 * File status handler interface - Strategy Pattern
 *
 * <p>Defines the contract for handling different file parsing statuses.
 * Each concrete implementation handles a specific status type (SUCCESS, FAILED, PARSING).
 *
 * @author kevin
 * @since 2025/11/27
 */
public interface FileStatusHandler {

    /**
     * Handles file status
     *
     * @param context  document processing context
     * @param response API response containing file status information
     * @return processing result indicating completion status and success/failure
     */
    FileStatusResult handle(DocumentProcessContext context,
                            ResponseEntity<DashScopeApiSpec.CommonResponse<
                                    DashScopeApiSpec.QueryFileResponseData>> response);
}
