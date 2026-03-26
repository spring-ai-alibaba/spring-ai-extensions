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
package com.alibaba.cloud.ai.autoconfigure.dashscope;

import com.alibaba.cloud.ai.tool.DashScopeAsyncToolCallingManager;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.model.tool.autoconfigure.ToolCallingAutoConfiguration;
import org.springframework.ai.tool.execution.ToolExecutionExceptionProcessor;
import org.springframework.ai.tool.observation.ToolCallingObservationConvention;
import org.springframework.ai.tool.resolution.ToolCallbackResolver;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Spring AI Alibaba DashScope Async ToolCallingManager Auto Configuration.
 *
 * @since 1.1.2.1
 */
@AutoConfiguration(before = ToolCallingAutoConfiguration.class)
@ConditionalOnDashScopeEnabled
@ConditionalOnProperty(prefix = DashScopeAsyncToolCallingProperties.CONFIG_PREFIX, name = "enabled", havingValue = "true")
@ConditionalOnClass(DashScopeAsyncToolCallingManager.class)
@EnableConfigurationProperties(DashScopeAsyncToolCallingProperties.class)
public class DashScopeAsyncToolCallingManagerAutoConfiguration {

    // @formatter:off
    @Bean
    ToolCallingManager toolCallingManager(
            ToolCallbackResolver toolCallbackResolver,
            ToolExecutionExceptionProcessor toolExecutionExceptionProcessor,
            ObjectProvider<ObservationRegistry> observationRegistry,
            ObjectProvider<ToolCallingObservationConvention> observationConvention,
            DashScopeAsyncToolCallingProperties dashScopeAsyncToolCallingProperties) {

        // init toolCallingManager
        var toolCallingManager = DashScopeAsyncToolCallingManager.builder()
                .observationRegistry(observationRegistry.getIfUnique(() -> ObservationRegistry.NOOP))
                .toolCallbackResolver(toolCallbackResolver)
                .toolExecutionExceptionProcessor(toolExecutionExceptionProcessor)
                .taskExecutor(buildAsyncToolCallThreadPool(dashScopeAsyncToolCallingProperties))
                .build();
        observationConvention.ifAvailable(toolCallingManager::setObservationConvention);

        return toolCallingManager;
    }

    private ThreadPoolExecutor buildAsyncToolCallThreadPool(DashScopeAsyncToolCallingProperties asyncToolCallingProperties) {
        return new ThreadPoolExecutor(
                asyncToolCallingProperties.getCorePoolSize(),
                asyncToolCallingProperties.getMaximumPoolSize(),
                asyncToolCallingProperties.getKeepAliveTime(),
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(asyncToolCallingProperties.getQueueCapacity()),
                new ThreadPoolExecutor.CallerRunsPolicy());
    }
    // @formatter:on
}
