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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link RedisChatMemoryProperties}.
 *
 * @author Antigravity
 */
class RedisChatMemoryPropertiesTests {

	@Test
	void defaultValues() {
		var props = new RedisChatMemoryProperties();
		assertThat(props.getHost()).isEqualTo("127.0.0.1");
		assertThat(props.getPort()).isEqualTo(6379);
		assertThat(props.getDatabase()).isEqualTo(0);
		assertThat(props.getTimeout()).isEqualTo(2000);
		assertThat(props.getUsername()).isNull();
		assertThat(props.getPassword()).isNull();
		assertThat(props.getKeyPrefix()).isNull();
		assertThat(props.getClientType()).isNull();
		assertThat(props.getCluster()).isNull();
		assertThat(props.getMode()).isNull();
		assertThat(props.getSsl().isEnabled()).isFalse();
		assertThat(props.getSsl().getBundle()).isNull();
	}

	@Test
	void customValues() {
		var props = new RedisChatMemoryProperties();
		props.setHost("redis.example.com");
		props.setPort(6380);
		props.setDatabase(2);
		props.setTimeout(5000);
		props.setUsername("admin");
		props.setPassword("secret");
		props.setKeyPrefix("chat:");
		props.setClientType(RedisChatMemoryProperties.ClientType.LETTUCE);
		props.setMode(RedisChatMemoryProperties.Mode.STANDALONE);

		assertThat(props.getHost()).isEqualTo("redis.example.com");
		assertThat(props.getPort()).isEqualTo(6380);
		assertThat(props.getDatabase()).isEqualTo(2);
		assertThat(props.getTimeout()).isEqualTo(5000);
		assertThat(props.getUsername()).isEqualTo("admin");
		assertThat(props.getPassword()).isEqualTo("secret");
		assertThat(props.getKeyPrefix()).isEqualTo("chat:");
		assertThat(props.getClientType()).isEqualTo(RedisChatMemoryProperties.ClientType.LETTUCE);
		assertThat(props.getMode()).isEqualTo(RedisChatMemoryProperties.Mode.STANDALONE);
	}

	@Test
	void clusterProperties() {
		var props = new RedisChatMemoryProperties();
		var cluster = new RedisChatMemoryProperties.Cluster();
		cluster.setNodes(List.of("node1:7000", "node2:7001", "node3:7002"));
		cluster.setMaxRedirects(3);
		props.setCluster(cluster);
		props.setMode(RedisChatMemoryProperties.Mode.CLUSTER);

		assertThat(props.getCluster().getNodes()).hasSize(3);
		assertThat(props.getCluster().getNodes()).containsExactly("node1:7000", "node2:7001", "node3:7002");
		assertThat(props.getCluster().getMaxRedirects()).isEqualTo(3);
		assertThat(props.getMode()).isEqualTo(RedisChatMemoryProperties.Mode.CLUSTER);
	}

	@Test
	void sslProperties() {
		var props = new RedisChatMemoryProperties();

		// SSL enabled via bundle name
		props.getSsl().setBundle("my-ssl-bundle");
		assertThat(props.getSsl().isEnabled()).isTrue();
		assertThat(props.getSsl().getBundle()).isEqualTo("my-ssl-bundle");

		// SSL explicitly disabled even with bundle
		props.getSsl().setEnabled(false);
		assertThat(props.getSsl().isEnabled()).isFalse();
	}

	@Test
	void clientTypeEnumValues() {
		assertThat(RedisChatMemoryProperties.ClientType.values()).containsExactly(
				RedisChatMemoryProperties.ClientType.LETTUCE,
				RedisChatMemoryProperties.ClientType.JEDIS,
				RedisChatMemoryProperties.ClientType.REDISSON);
	}

	@Test
	void modeEnumValues() {
		assertThat(RedisChatMemoryProperties.Mode.values()).containsExactly(
				RedisChatMemoryProperties.Mode.STANDALONE,
				RedisChatMemoryProperties.Mode.CLUSTER);
	}

}
