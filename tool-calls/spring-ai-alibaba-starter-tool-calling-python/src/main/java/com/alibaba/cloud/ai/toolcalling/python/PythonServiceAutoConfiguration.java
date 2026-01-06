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
package com.alibaba.cloud.ai.toolcalling.python;

import org.graalvm.polyglot.Engine;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * Auto-configuration for Python Tool.
 *
 * This configuration automatically registers the PythonTool as a ToolCallback
 * when GraalVM polyglot is available on the classpath.
 *
 * @author Spring AI Alibaba
 */
@AutoConfiguration
@ConditionalOnClass({ Engine.class })
@ConditionalOnProperty(prefix = PythonConstants.CONFIG_PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(PythonProperties.class)
public class PythonServiceAutoConfiguration {

    @Bean(name = PythonConstants.TOOL_NAME)
	@ConditionalOnMissingBean
    public PythonService pythonService(PythonProperties properties) {
        return new PythonService(properties);
	}

    @Bean
    @ConditionalOnMissingBean(name = "pythonToolCallback")
    public ToolCallback pythonToolCallback() {
        return PythonService.createPythonToolCallback(PythonConstants.DESCRIPTION);
    }

}

