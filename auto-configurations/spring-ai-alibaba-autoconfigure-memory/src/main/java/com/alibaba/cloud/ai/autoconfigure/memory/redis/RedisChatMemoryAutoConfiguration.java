/*
 * Copyright 2023-2026 the original author or authors.
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
package com.alibaba.cloud.ai.autoconfigure.memory.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

/**
 * Auto-configuration for Redis Memory support.
 *
 * @author benym
 * @since 2025/7/30 23:35
 */
@AutoConfiguration
@Import({ JedisRedisChatMemoryConnectionAutoConfiguration.class,
		LettuceRedisChatMemoryConnectionAutoConfiguration.class,
		RedissonRedisChatMemoryConnectionAutoConfiguration.class })
@ConditionalOnProperty(prefix = RedisChatMemoryProperties.CONFIG_PREFIX, name = "enabled", havingValue = "true",
		matchIfMissing = false)
@EnableConfigurationProperties(RedisChatMemoryProperties.class)
public class RedisChatMemoryAutoConfiguration {

	private static final Logger logger = LoggerFactory.getLogger(RedisChatMemoryAutoConfiguration.class);

	@Bean
	@ConditionalOnMissingBean(RedisMemoryConnectionDetails.class)
	RedisChatMemoryConnectionDetails redisChatMemoryConnectionDetails(RedisChatMemoryProperties properties) {
		logger.info("Configuring Redis chat memory connection details");
		return new RedisChatMemoryConnectionDetails(properties);
	}

}
