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

package com.alibaba.cloud.ai.autoconfigure.memory;

import com.alibaba.cloud.ai.memory.jdbc.H2ChatMemoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Auto-configuration for h2 chat memory repository.
 */
@AutoConfiguration(after = JdbcTemplateAutoConfiguration.class)
@ConditionalOnClass({ H2ChatMemoryRepository.class, JdbcTemplate.class })
@ConditionalOnProperty(prefix = H2ChatMemoryProperties.CONFIG_PREFIX, name = "enabled", havingValue = "true", matchIfMissing = false)
@EnableConfigurationProperties(H2ChatMemoryProperties.class)
public class H2ChatMemoryAutoConfiguration {

	private static final Logger logger = LoggerFactory.getLogger(H2ChatMemoryAutoConfiguration.class);

	public static final String H2_CHAT_MEMORY_REPOSITORY_BEAN_NAME = "h2ChatMemoryRepository";

	@Bean(value = H2_CHAT_MEMORY_REPOSITORY_BEAN_NAME)
	@ConditionalOnMissingBean(name = H2_CHAT_MEMORY_REPOSITORY_BEAN_NAME)
	H2ChatMemoryRepository h2ChatMemoryRepository(JdbcTemplate jdbcTemplate) {
		logger.info("Configuring H2 chat memory repository");
		return H2ChatMemoryRepository.h2Builder().jdbcTemplate(jdbcTemplate).build();
	}

}
