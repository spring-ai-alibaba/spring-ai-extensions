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

package com.alibaba.cloud.ai.model.chat.memory.redis.autoconfigure;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link RedisChatMemoryAutoConfiguration}.
 *
 * @author Antigravity
 */
class RedisChatMemoryAutoConfigurationTests {

	private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
		.withConfiguration(AutoConfigurations.of(RedisChatMemoryAutoConfiguration.class));

	@Test
	void connectionDetailsBean_isCreatedByDefault() {
		this.contextRunner.run(context -> {
			assertThat(context).hasSingleBean(RedisChatMemoryConnectionDetails.class);
			assertThat(context).hasSingleBean(RedisMemoryConnectionDetails.class);
		});
	}

	@Test
	void connectionDetailsBean_defaultPropertiesBound() {
		this.contextRunner.run(context -> {
			RedisMemoryConnectionDetails connectionDetails = context.getBean(RedisMemoryConnectionDetails.class);
			assertThat(connectionDetails.getUsername()).isNull();
			assertThat(connectionDetails.getPassword()).isNull();
			assertThat(connectionDetails.getStandalone()).isNotNull();
			assertThat(connectionDetails.getStandalone().getHost()).isEqualTo("127.0.0.1");
			assertThat(connectionDetails.getStandalone().getPort()).isEqualTo(6379);
			assertThat(connectionDetails.getStandalone().getDatabase()).isEqualTo(0);
		});
	}

	@Test
	void connectionDetailsBean_customPropertiesBound() {
		this.contextRunner
			.withPropertyValues("spring.ai.chat.memory.repository.redis.host=redis.example.com",
					"spring.ai.chat.memory.repository.redis.port=6380",
					"spring.ai.chat.memory.repository.redis.username=admin",
					"spring.ai.chat.memory.repository.redis.password=secret",
					"spring.ai.chat.memory.repository.redis.database=2")
			.run(context -> {
				RedisMemoryConnectionDetails connectionDetails = context.getBean(RedisMemoryConnectionDetails.class);
				assertThat(connectionDetails.getUsername()).isEqualTo("admin");
				assertThat(connectionDetails.getPassword()).isEqualTo("secret");
				assertThat(connectionDetails.getStandalone().getHost()).isEqualTo("redis.example.com");
				assertThat(connectionDetails.getStandalone().getPort()).isEqualTo(6380);
				assertThat(connectionDetails.getStandalone().getDatabase()).isEqualTo(2);
			});
	}

	@Test
	void connectionDetailsBean_backsOffWhenCustomProvided() {
		this.contextRunner.withUserConfiguration(CustomConnectionDetailsConfiguration.class).run(context -> {
			// The auto-configured bean should back off because a RedisMemoryConnectionDetails already exists
			assertThat(context).hasSingleBean(RedisMemoryConnectionDetails.class);
			RedisMemoryConnectionDetails connectionDetails = context.getBean(RedisMemoryConnectionDetails.class);
			assertThat(connectionDetails.getStandalone().getHost()).isEqualTo("custom-host");
			assertThat(connectionDetails.getStandalone().getPort()).isEqualTo(6399);
		});
	}

	@Test
	void connectionDetailsBean_clusterPropertiesBound() {
		this.contextRunner
			.withPropertyValues("spring.ai.chat.memory.repository.redis.cluster.nodes=node1:7000,node2:7001",
					"spring.ai.chat.memory.repository.redis.cluster.max-redirects=5")
			.run(context -> {
				RedisMemoryConnectionDetails connectionDetails = context.getBean(RedisMemoryConnectionDetails.class);
				assertThat(connectionDetails.getCluster()).isNotNull();
				assertThat(connectionDetails.getCluster().getNodes()).hasSize(2);
				assertThat(connectionDetails.getCluster().getNodes().get(0).host()).isEqualTo("node1");
				assertThat(connectionDetails.getCluster().getNodes().get(0).port()).isEqualTo(7000);
			});
	}

	@Test
	void propertiesBean_isBound() {
		this.contextRunner
			.withPropertyValues("spring.ai.chat.memory.repository.redis.timeout=5000",
					"spring.ai.chat.memory.repository.redis.key-prefix=chat:")
			.run(context -> {
				RedisChatMemoryProperties properties = context.getBean(RedisChatMemoryProperties.class);
				assertThat(properties.getTimeout()).isEqualTo(5000);
				assertThat(properties.getKeyPrefix()).isEqualTo("chat:");
			});
	}

	@Test
	void jedisConfiguration_activatedByDefault() {
		this.contextRunner.run(context -> {
			assertThat(context).hasSingleBean(JedisRedisChatMemoryConnectionAutoConfiguration.class);
			assertThat(context).doesNotHaveBean(LettuceRedisChatMemoryConnectionAutoConfiguration.class);
			assertThat(context).doesNotHaveBean(RedissonRedisChatMemoryConnectionAutoConfiguration.class);
		});
	}

	@Test
	void lettuceConfiguration_activatedByProperty() {
		this.contextRunner
			.withPropertyValues("spring.ai.chat.memory.repository.redis.client-type=lettuce")
			.run(context -> {
				assertThat(context).hasSingleBean(LettuceRedisChatMemoryConnectionAutoConfiguration.class);
				assertThat(context).doesNotHaveBean(JedisRedisChatMemoryConnectionAutoConfiguration.class);
				assertThat(context).doesNotHaveBean(RedissonRedisChatMemoryConnectionAutoConfiguration.class);
			});
	}

	@Test
	void redissonConfiguration_activatedByProperty() {
		this.contextRunner
			.withPropertyValues("spring.ai.chat.memory.repository.redis.client-type=redisson")
			.run(context -> {
				// Redisson.create() eagerly connects during bean creation, so without a real Redis
				// the context will fail. The failure message proves RedissonRedisChatMemoryConnectionAutoConfiguration
				// was activated (which is what we're testing).
				assertThat(context).hasFailed();
				assertThat(context.getStartupFailure())
					.hasMessageContaining("RedissonRedisChatMemoryConnectionAutoConfiguration");
			});
	}

	/**
	 * Custom configuration that provides a {@link RedisChatMemoryConnectionDetails} bean.
	 * <p>
	 * Note: The imported Jedis/Lettuce/Redisson configs require the concrete
	 * {@code RedisChatMemoryConnectionDetails} type (not just the interface), so the custom
	 * bean must be of the concrete type to satisfy that dependency.
	 */
	@Configuration(proxyBeanMethods = false)
	static class CustomConnectionDetailsConfiguration {

		@Bean
		RedisChatMemoryConnectionDetails customConnectionDetails() {
			RedisChatMemoryProperties props = new RedisChatMemoryProperties();
			props.setHost("custom-host");
			props.setPort(6399);
			return new RedisChatMemoryConnectionDetails(props);
		}

	}

}
