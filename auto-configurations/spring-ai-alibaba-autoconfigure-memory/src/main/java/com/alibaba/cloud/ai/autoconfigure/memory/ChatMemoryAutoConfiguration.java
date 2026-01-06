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

package com.alibaba.cloud.ai.autoconfigure.memory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnClass({ ChatMemory.class, ChatMemoryRepository.class })
@ConditionalOnProperty(prefix = ChatMemoryProperties.CONFIG_PREFIX, name = "enabled", havingValue = "true",
		matchIfMissing = false)
@EnableConfigurationProperties(ChatMemoryProperties.class)
public class ChatMemoryAutoConfiguration {

	private static final Logger logger = LoggerFactory.getLogger(ChatMemoryAutoConfiguration.class);

	public static final String IN_CHAT_MEMORY_REPOSITORY_BEAN_NAME = "inChatMemoryRepository";

	@Bean(value = IN_CHAT_MEMORY_REPOSITORY_BEAN_NAME)
	@ConditionalOnMissingBean(name = IN_CHAT_MEMORY_REPOSITORY_BEAN_NAME)
	ChatMemoryRepository chatMemoryRepository() {
		logger.info("Using InMemoryChatMemoryRepository");
		return new InMemoryChatMemoryRepository();
	}

	@Bean
	@ConditionalOnMissingBean
	ChatMemory chatMemory(ChatMemoryRepository chatMemoryRepository, ChatMemoryProperties properties) {
		return MessageWindowChatMemory.builder().chatMemoryRepository(chatMemoryRepository)
				.maxMessages(properties.getMaxMessages())
				.build();
	}

}
