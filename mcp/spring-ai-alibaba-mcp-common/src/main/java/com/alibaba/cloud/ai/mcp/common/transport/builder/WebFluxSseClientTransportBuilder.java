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

package com.alibaba.cloud.ai.mcp.common.transport.builder;

import io.modelcontextprotocol.client.transport.WebFluxSseClientTransport;
import io.modelcontextprotocol.json.McpJsonMapper;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * @author yingzi
 * @since 2025/10/25
 */

public class WebFluxSseClientTransportBuilder {

    public static WebFluxSseClientTransport build(WebClient.Builder webClientBuilder, McpJsonMapper jsonMapper,
                                           String sseEndpoint) {
        return WebFluxSseClientTransport.builder(webClientBuilder)
                .sseEndpoint(sseEndpoint)
                .jsonMapper(jsonMapper)
                .build();
    }

    public static WebFluxSseClientTransport build(WebClient.Builder webClientBuilder, McpJsonMapper jsonMapper,
                                           String sseEndpoint, ExchangeFilterFunction traceFilter) {
        if (traceFilter != null) {
            webClientBuilder.filter(traceFilter);
        }
        return WebFluxSseClientTransport.builder(webClientBuilder)
                .sseEndpoint(sseEndpoint)
                .jsonMapper(jsonMapper)
                .build();
    }
}
