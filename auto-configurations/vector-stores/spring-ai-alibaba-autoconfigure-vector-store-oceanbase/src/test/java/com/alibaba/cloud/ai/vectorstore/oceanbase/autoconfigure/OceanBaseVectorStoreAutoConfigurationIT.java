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
package com.alibaba.cloud.ai.vectorstore.oceanbase.autoconfigure;

import com.alibaba.cloud.ai.vectorstore.oceanbase.OceanBaseVectorStore;
import io.micrometer.observation.tck.TestObservationRegistry;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.images.PullPolicy;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.oceanbase.OceanBaseCEContainer;
import org.testcontainers.utility.DockerLoggerFactory;

import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.transformers.TransformersEmbeddingModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


@Testcontainers
public class OceanBaseVectorStoreAutoConfigurationIT {

	private static final String IMAGE = "oceanbase/oceanbase-ce:latest";

	private static final String HOSTNAME = "oceanbase_test_autoconfig";

	private static final int PORT = 2881;

	public static final Network NETWORK = Network.newNetwork();

	private static final String USERNAME = "root@test";

	private static final String PASSWORD = "";

	private static final String OCEANBASE_DATABASE = "test";

	@Container
	private static final OceanBaseCEContainer oceanBaseContainer = new OceanBaseCEContainer(IMAGE)
		.withEnv("MODE", "slim")
		.withEnv("OB_DATAFILE_SIZE", "2G")
		.withNetwork(NETWORK)
		.withNetworkAliases(HOSTNAME)
		.withExposedPorts(PORT)
		.withImagePullPolicy(PullPolicy.defaultPolicy())
		.waitingFor(Wait.forLogMessage(".*boot success!.*", 1))
		.withStartupTimeout(Duration.ofMinutes(5))
		.withLogConsumer(new Slf4jLogConsumer(DockerLoggerFactory.getLogger(IMAGE)));

	private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
		.withConfiguration(AutoConfigurations.of(OceanBaseVectorStoreAutoConfiguration.class))
		.withUserConfiguration(Config.class);

	@Test
	void addAndSearch() {
		this.contextRunner
			.withPropertyValues("spring.ai.vectorstore.oceanbase.url=" + getJdbcUrl(),
					"spring.ai.vectorstore.oceanbase.username=" + USERNAME,
					"spring.ai.vectorstore.oceanbase.password=" + PASSWORD,
					"spring.ai.vectorstore.oceanbase.tableName=" + OCEANBASE_DATABASE + "_autoconfig",
					"spring.ai.vectorstore.oceanbase.initialize-schema=true")
			.run(context -> {
				VectorStore vectorStore = context.getBean(VectorStore.class);
				assertThat(vectorStore).isInstanceOf(OceanBaseVectorStore.class);
				assertThat(context.getBean(OceanBaseVectorStoreProperties.class)).isNotNull();

				List<Document> documents = List.of(
						new Document("Spring AI rocks!!", Map.of("meta1", "meta1")));
				
				vectorStore.add(documents);

				List<Document> results = vectorStore
					.similaritySearch(SearchRequest.builder().query("Spring").topK(1).build());

				assertThat(results).hasSize(1);
				assertThat(results.get(0).getText()).isEqualTo("Spring AI rocks!!");
			});
	}

	@Test
	public void autoConfigurationDisabledWhenEnabledIsFalse() {
		this.contextRunner.withPropertyValues("spring.ai.vectorstore.oceanbase.enabled=false").run(context -> {
			assertThat(context.getBeansOfType(OceanBaseVectorStoreProperties.class)).isEmpty();
			assertThat(context.getBeansOfType(OceanBaseVectorStore.class)).isEmpty();
		});
	}

	private String getJdbcUrl() {
		return "jdbc:oceanbase://" + oceanBaseContainer.getHost() + ":" + oceanBaseContainer.getMappedPort(PORT) + "/"
				+ OCEANBASE_DATABASE;
	}

	@Configuration(proxyBeanMethods = false)
	static class Config {

		@Bean
		public TestObservationRegistry observationRegistry() {
			return TestObservationRegistry.create();
		}

		@Bean
		public EmbeddingModel embeddingModel() {
			return new TransformersEmbeddingModel();
		}

	}

}
