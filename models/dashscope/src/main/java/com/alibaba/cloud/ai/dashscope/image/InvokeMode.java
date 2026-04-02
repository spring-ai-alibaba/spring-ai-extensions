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
package com.alibaba.cloud.ai.dashscope.image;

/**
 * Invocation mode for DashScope Image API.
 *
 * @author yuluo
 * @since 2024/8/16
 */
public enum InvokeMode {

    /**
     * Auto mode - automatically choose based on model defaults for backward compatibility.
     */
    AUTO,

    /**
     * Synchronous mode - call without async header, blocks until completion.
     */
    SYNC,

    /**
     * Asynchronous mode - call with async header, returns task_id for polling.
     */
    ASYNC

}
