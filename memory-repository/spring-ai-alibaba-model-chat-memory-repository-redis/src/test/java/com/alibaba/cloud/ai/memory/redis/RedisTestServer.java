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

package com.alibaba.cloud.ai.memory.redis;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

/**
 * Runs Redis tests against a loopback port forward when configured.
 * The default test setup starts a disposable Redis container.
 */
public final class RedisTestServer implements ExecutionCondition, BeforeAllCallback, AfterAllCallback {

	private static final int REDIS_PORT = 6379;

	private final boolean tls;

	private final String forwardedPort;

	private GenericContainer<?> container;

	public RedisTestServer(boolean tls) {
		this.tls = tls;
		this.forwardedPort = System.getenv(tls ? "SAA_TEST_REDIS_TLS_PORT" : "SAA_TEST_REDIS_PORT");
	}

	@Override
	public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
		if (this.forwardedPort != null) {
			return ConditionEvaluationResult.enabled("Redis port forward configured");
		}
		return DockerClientFactory.instance().isDockerAvailable()
				? ConditionEvaluationResult.enabled("Docker available")
				: ConditionEvaluationResult.disabled("Redis port forward and Docker are unavailable");
	}

	@Override
	public void beforeAll(ExtensionContext context) {
		if (this.forwardedPort != null) {
			return;
		}
		this.container = new GenericContainer<>(DockerImageName.parse("redis:7.0")).withExposedPorts(REDIS_PORT);
		if (this.tls) {
			this.container.withCopyToContainer(MountableFile.forClasspathResource("ssl/cert.pem"), "/redis.crt")
				.withCopyToContainer(MountableFile.forClasspathResource("ssl/key.pem"), "/redis.key")
				.withCommand("redis-server", "--tls-port", "6379", "--port", "0", "--tls-cert-file", "/redis.crt",
						"--tls-key-file", "/redis.key", "--tls-ca-cert-file", "/redis.crt", "--tls-auth-clients", "no");
		}
		this.container.start();
	}

	@Override
	public void afterAll(ExtensionContext context) {
		if (this.container != null) {
			this.container.stop();
		}
	}

	public String getHost() {
		return this.forwardedPort == null ? this.container.getHost() : "127.0.0.1";
	}

	public int getPort() {
		return this.forwardedPort == null ? this.container.getMappedPort(REDIS_PORT)
				: Integer.parseInt(this.forwardedPort);
	}

}
