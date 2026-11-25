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
package com.alibaba.cloud.ai.model.chat.memory.autoconfigure;

import org.springframework.ai.model.session.autoconfigure.SessionServiceAutoConfiguration;
import org.springframework.ai.session.InMemorySessionRepository;
import org.springframework.ai.session.SessionRepository;
import org.springframework.ai.session.SessionService;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

@AutoConfiguration(before = SessionServiceAutoConfiguration.class,
		afterName = "org.springframework.ai.model.session.jdbc.autoconfigure.JdbcSessionRepositoryAutoConfiguration")
@ConditionalOnProperty(prefix = "spring.ai.alibaba.session", name = "enabled", havingValue = "true")
public class AlibabaSessionMemoryAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean({ SessionRepository.class, SessionService.class })
	SessionRepository alibabaSessionRepository() {
		return InMemorySessionRepository.builder().build();
	}

	@Bean
	@ConditionalOnMissingBean
	LegacyChatMemorySessionImporter legacyChatMemorySessionImporter(SessionService sessionService) {
		return new LegacyChatMemorySessionImporter(sessionService);
	}

}
