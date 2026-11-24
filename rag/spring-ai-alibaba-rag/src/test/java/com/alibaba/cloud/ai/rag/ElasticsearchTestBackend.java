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

package com.alibaba.cloud.ai.rag;

import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * Uses a dedicated test Elasticsearch instance when {@code -Dtest.elasticsearch.uri}
 * is supplied. Otherwise, each suite starts its own Elasticsearch container.
 */
public final class ElasticsearchTestBackend implements AutoCloseable {

    private final String externalUri = System.getProperty("test.elasticsearch.uri");

    private ElasticsearchContainer container;

    public String uri() {
        if (this.externalUri != null && !this.externalUri.isBlank()) {
            return this.externalUri;
        }
        if (this.container == null) {
            this.container = new ElasticsearchContainer(
                    DockerImageName.parse("docker.elastic.co/elasticsearch/elasticsearch:9.4.9"))
                .withEnv("discovery.type", "single-node")
                .withEnv("xpack.security.enabled", "false")
                .withEnv("ES_JAVA_OPTS", "-Xms512m -Xmx512m")
                .withStartupAttempts(3);
            this.container.start();
        }
        return "http://" + this.container.getHttpHostAddress();
    }

    @Override
    public void close() {
        if (this.container != null) {
            this.container.stop();
        }
    }

}
