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
package com.alibaba.cloud.ai.vectorstore.oceanbase;

import io.micrometer.observation.tck.TestObservationRegistry;
import io.micrometer.observation.tck.TestObservationRegistryAssert;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.transformers.TransformersEmbeddingModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.observation.DefaultVectorStoreObservationConvention;
import org.springframework.ai.vectorstore.observation.VectorStoreObservationContext;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.DefaultResourceLoader;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.images.PullPolicy;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.oceanbase.OceanBaseCEContainer;
import org.testcontainers.utility.DockerLoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;

/**
 * OceanBase vector store test. This class tests adding, searching, and deleting documents
 * in OceanBase.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@EnabledIfEnvironmentVariable(named = "OCEANBASE_URL", matches = ".+")
@EnabledIfEnvironmentVariable(named = "OCEANBASE_USERNAME", matches = ".+")
@EnabledIfEnvironmentVariable(named = "OCEANBASE_PASSWORD", matches = ".+")
class OceanBaseVectorTest {

	private static final String IMAGE = "oceanbase/oceanbase-ce:latest";

	private static final String HOSTNAME = "oceanbase_test";

	private static final int PORT = 2881;

	public static final Network NETWORK = Network.newNetwork();

	private static final String USERNAME = "root@test";

	private static final String PASSWORD = "";

	private static final String OCEANBASE_DATABASE = "test";

	private static final String OCEANBASE_DRIVER_CLASS = "com.oceanbase.jdbc.Driver";

	private Connection connection;

	private OceanBaseCEContainer oceanBaseContainer;

	private ApplicationContextRunner contextRunner;

	List<Document> documents = List.of(
			new Document("1", getText("classpath:spring.ai.txt"), Map.of("docId", "1", "spring", "great")),
			new Document("2", getText("classpath:time.shelter.txt"), Map.of("docId", "1")),
			new Document("3", getText("classpath:great.depression.txt"), Map.of("docId", "1", "depression", "bad")));

	@BeforeAll
	public void setUp() throws Exception {
		oceanBaseContainer = initOceanbaseContainer();
		Startables.deepStart(Stream.of(oceanBaseContainer)).join();
		initializeJdbcConnection(getJdbcUrl());
		createSchemaIfNeeded();
		contextRunner = initApplicationContextRunner();
		Awaitility.setDefaultPollInterval(2, TimeUnit.SECONDS);
		Awaitility.setDefaultPollDelay(Duration.ZERO);
		Awaitility.setDefaultTimeout(Duration.ofMinutes(1));
	}

	@AfterAll
	public void tearDown() {
		if (oceanBaseContainer != null) {
			oceanBaseContainer.stop();
		}
	}

	public static String getText(String uri) {
		var resource = new DefaultResourceLoader().getResource(uri);
		try {
			return resource.getContentAsString(StandardCharsets.UTF_8);
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public ApplicationContextRunner initApplicationContextRunner() {
		return new ApplicationContextRunner()
			.withConfiguration(AutoConfigurations.of(OceanBaseVectorStoreAutoConfiguration.class))
			.withUserConfiguration(Config.class)
			.withPropertyValues("spring.ai.vectorstore.oceanbase.url=" + getJdbcUrl(),
					"spring.ai.vectorstore.oceanbase.username=" + System.getenv("OCEANBASE_USERNAME"),
					"spring.ai.vectorstore.oceanbase.password=" + System.getenv("OCEANBASE_PASSWORD"),
					"spring.ai.vectorstore.oceanbase.tableName=" + System.getenv("OCEANBASE_TABLENAME"));
	}

	@Test
	public void addAndSearchTest() {
		this.contextRunner
			.withPropertyValues("spring.ai.vectorstore.oceanbase.url=" + getJdbcUrl(),
					"spring.ai.vectorstore.oceanbase.username=" + USERNAME,
					"spring.ai.vectorstore.oceanbase.password=" + PASSWORD,
					"spring.ai.vectorstore.oceanbase.tableName=" + OCEANBASE_DATABASE)
			.run(context -> {
				VectorStore vectorStore = context.getBean(VectorStore.class);
				TestObservationRegistry observationRegistry = context.getBean(TestObservationRegistry.class);

				assertThat(vectorStore).isInstanceOf(OceanBaseVectorStore.class);

				vectorStore.add(this.documents);

				Awaitility.await()
					.until(() -> vectorStore.similaritySearch(SearchRequest.builder().query("Spring").topK(1).build()),
							hasSize(1));

				ObservationTestUtil.assertObservationRegistry(observationRegistry, "oceanbase",
						VectorStoreObservationContext.Operation.ADD);
				observationRegistry.clear();

				List<Document> results = vectorStore
					.similaritySearch(SearchRequest.builder().query("Spring").topK(1).build());

				assertThat(results).hasSize(1);
				Document resultDoc = results.get(0);
				assertThat(resultDoc.getId()).isEqualTo(this.documents.get(0).getId());
				assertThat(resultDoc.getText()).contains(
						"Spring AI provides abstractions that serve as the foundation for developing AI applications.");
				assertThat(resultDoc.getMetadata()).hasSize(3);
				assertThat(resultDoc.getMetadata()).containsKeys("spring", "distance");

				ObservationTestUtil.assertObservationRegistry(observationRegistry, "oceanbase",
						VectorStoreObservationContext.Operation.QUERY);
				observationRegistry.clear();

				vectorStore.delete(this.documents.stream().map(Document::getId).toList());

				Awaitility.await()
					.until(() -> vectorStore.similaritySearch(SearchRequest.builder().query("Spring").topK(1).build()),
							hasSize(0));

				ObservationTestUtil.assertObservationRegistry(observationRegistry, "oceanbase",
						VectorStoreObservationContext.Operation.DELETE);
				observationRegistry.clear();
			});
	}

	@Test
	public void searchWithFilterTest() {
		this.contextRunner
			.withPropertyValues("spring.ai.vectorstore.oceanbase.url=" + getJdbcUrl(),
					"spring.ai.vectorstore.oceanbase.username=" + USERNAME,
					"spring.ai.vectorstore.oceanbase.password=" + PASSWORD,
					"spring.ai.vectorstore.oceanbase.tableName=" + OCEANBASE_DATABASE + "_filter")
			.run(context -> {
				VectorStore vectorStore = context.getBean(VectorStore.class);
				TestObservationRegistry observationRegistry = context.getBean(TestObservationRegistry.class);

				assertThat(vectorStore).isInstanceOf(OceanBaseVectorStore.class);

				List<Document> testDocuments = List.of(
						new Document("1", "Spring AI is a framework", Map.of("category", "framework", "author", "Spring")),
						new Document("2", "OceanBase is a database", Map.of("category", "database", "author", "OceanBase")),
						new Document("3", "Java is a programming language", Map.of("category", "language", "author", "Oracle")));

				vectorStore.add(testDocuments);

				Awaitility.await()
					.until(() -> vectorStore.similaritySearch(SearchRequest.builder().query("framework").topK(1).build()),
						hasSize(1));

				ObservationTestUtil.assertObservationRegistry(observationRegistry, "oceanbase",
						VectorStoreObservationContext.Operation.ADD);
				observationRegistry.clear();

				Filter.Expression filter = new Filter.Expression(Filter.ExpressionType.EQ, new Filter.Key("category"),
						new Filter.Value("framework"));

				List<Document> results = vectorStore.similaritySearch(SearchRequest.builder()
					.query("framework")
					.topK(10)
					.filterExpression(filter)
					.build());

				assertThat(results).hasSize(1);
				assertThat(results.get(0).getMetadata().get("category")).isEqualTo("framework");

				ObservationTestUtil.assertObservationRegistry(observationRegistry, "oceanbase",
						VectorStoreObservationContext.Operation.QUERY);
				observationRegistry.clear();

				Filter.Expression neFilter = new Filter.Expression(Filter.ExpressionType.NE, new Filter.Key("category"),
						new Filter.Value("framework"));

				List<Document> neResults = vectorStore.similaritySearch(SearchRequest.builder()
					.query("database")
					.topK(10)
					.filterExpression(neFilter)
					.build());

				assertThat(neResults).hasSize(2);
				assertThat(neResults.stream().noneMatch(doc -> "framework".equals(doc.getMetadata().get("category"))))
					.isTrue();

				vectorStore.delete(testDocuments.stream().map(Document::getId).toList());
			});
	}

	@Test
	public void searchWithComplexFilterTest() {
		this.contextRunner
			.withPropertyValues("spring.ai.vectorstore.oceanbase.url=" + getJdbcUrl(),
					"spring.ai.vectorstore.oceanbase.username=" + USERNAME,
					"spring.ai.vectorstore.oceanbase.password=" + PASSWORD,
					"spring.ai.vectorstore.oceanbase.tableName=" + OCEANBASE_DATABASE + "_complex_filter")
			.run(context -> {
				VectorStore vectorStore = context.getBean(VectorStore.class);

				assertThat(vectorStore).isInstanceOf(OceanBaseVectorStore.class);

				List<Document> testDocuments = List.of(
						new Document("1", "Spring AI framework", Map.of("category", "framework", "author", "Spring", "year", 2024)),
						new Document("2", "OceanBase database", Map.of("category", "database", "author", "OceanBase", "year", 2023)),
						new Document("3", "Java language", Map.of("category", "language", "author", "Oracle", "year", 2024)));

				vectorStore.add(testDocuments);

				Awaitility.await()
					.until(() -> vectorStore.similaritySearch(SearchRequest.builder().query("framework").topK(1).build()),
							hasSize(1));

				Filter.Expression categoryFilter = new Filter.Expression(Filter.ExpressionType.EQ, new Filter.Key("category"),
						new Filter.Value("framework"));
				Filter.Expression authorFilter = new Filter.Expression(Filter.ExpressionType.EQ, new Filter.Key("author"),
						new Filter.Value("Spring"));
				Filter.Expression andFilter = new Filter.Expression(Filter.ExpressionType.AND, categoryFilter, authorFilter);

				List<Document> andResults = vectorStore.similaritySearch(SearchRequest.builder()
					.query("framework")
					.topK(10)
					.filterExpression(andFilter)
					.build());

				assertThat(andResults).hasSize(1);
				assertThat(andResults.get(0).getMetadata().get("category")).isEqualTo("framework");
				assertThat(andResults.get(0).getMetadata().get("author")).isEqualTo("Spring");

				Filter.Expression categoryFilter2 = new Filter.Expression(Filter.ExpressionType.EQ, new Filter.Key("category"),
						new Filter.Value("database"));
				Filter.Expression orFilter = new Filter.Expression(Filter.ExpressionType.OR, categoryFilter, categoryFilter2);

				List<Document> orResults = vectorStore.similaritySearch(SearchRequest.builder()
					.query("technology")
					.topK(10)
					.filterExpression(orFilter)
					.build());

				assertThat(orResults).hasSize(2);
				assertThat(orResults.stream()
					.allMatch(doc -> "framework".equals(doc.getMetadata().get("category"))
							|| "database".equals(doc.getMetadata().get("category")))).isTrue();

				vectorStore.delete(testDocuments.stream().map(Document::getId).toList());
			});
	}

	@Test
	public void hybridSearchWithFulltextTest() {
		this.contextRunner
			.withPropertyValues("spring.ai.vectorstore.oceanbase.url=" + getJdbcUrl(),
					"spring.ai.vectorstore.oceanbase.username=" + USERNAME,
					"spring.ai.vectorstore.oceanbase.password=" + PASSWORD,
					"spring.ai.vectorstore.oceanbase.tableName=" + OCEANBASE_DATABASE + "_hybrid",
					"spring.ai.vectorstore.oceanbase.hybridSearchType=fulltext")
			.run(context -> {
				VectorStore vectorStore = context.getBean(VectorStore.class);

				assertThat(vectorStore).isInstanceOf(OceanBaseVectorStore.class);

				List<Document> testDocuments = List.of(
						new Document("1", "Spring AI provides abstractions for developing AI applications",
								Map.of("category", "framework")),
						new Document("2", "OceanBase is a distributed database system",
								Map.of("category", "database")),
						new Document("3", "Java programming language is widely used",
								Map.of("category", "language")));

				vectorStore.add(testDocuments);

				Awaitility.await()
					.until(() -> vectorStore.similaritySearch(SearchRequest.builder().query("Spring").topK(1).build()),
							hasSize(1));

				List<Document> results = vectorStore.similaritySearch(SearchRequest.builder()
					.query("Spring AI")
					.topK(5)
					.build());

				assertThat(results).isNotEmpty();
				assertThat(results.stream().anyMatch(doc -> doc.getText().contains("Spring"))).isTrue();

				vectorStore.delete(testDocuments.stream().map(Document::getId).toList());
			});
	}

	@Test
	public void deleteWithFilterExpressionTest() {
		this.contextRunner
			.withPropertyValues("spring.ai.vectorstore.oceanbase.url=" + getJdbcUrl(),
					"spring.ai.vectorstore.oceanbase.username=" + USERNAME,
					"spring.ai.vectorstore.oceanbase.password=" + PASSWORD,
					"spring.ai.vectorstore.oceanbase.tableName=" + OCEANBASE_DATABASE + "_delete_filter")
			.run(context -> {
				VectorStore vectorStore = context.getBean(VectorStore.class);
				TestObservationRegistry observationRegistry = context.getBean(TestObservationRegistry.class);

				assertThat(vectorStore).isInstanceOf(OceanBaseVectorStore.class);

				List<Document> testDocuments = List.of(
						new Document("1", "Spring AI framework", Map.of("category", "framework", "status", "active")),
						new Document("2", "OceanBase database", Map.of("category", "database", "status", "active")),
						new Document("3", "Java language", Map.of("category", "language", "status", "inactive")));

				vectorStore.add(testDocuments);

				Awaitility.await()
					.until(() -> vectorStore.similaritySearch(SearchRequest.builder().query("framework").topK(1).build()),
						hasSize(1));

				ObservationTestUtil.assertObservationRegistry(observationRegistry, "oceanbase",
						VectorStoreObservationContext.Operation.ADD);
				observationRegistry.clear();

				List<Document> allResults = vectorStore
					.similaritySearch(SearchRequest.builder().query("technology").topK(10).build());
				assertThat(allResults).hasSize(3);

				ObservationTestUtil.assertObservationRegistry(observationRegistry, "oceanbase",
						VectorStoreObservationContext.Operation.QUERY);
				observationRegistry.clear();

				List<Document> inactiveDocs = vectorStore.similaritySearch(
					SearchRequest.builder()
						.query("language")
						.filterExpression(new Filter.Expression(Filter.ExpressionType.EQ, new Filter.Key("status"),
							new Filter.Value("inactive")))
						.topK(10)
						.build());
				assertThat(inactiveDocs).hasSize(1);
				String inactiveDocId = inactiveDocs.get(0).getId();

				observationRegistry.clear();

				vectorStore.delete(List.of(inactiveDocId));

				ObservationTestUtil.assertObservationRegistry(observationRegistry, "oceanbase",
						VectorStoreObservationContext.Operation.DELETE);
				observationRegistry.clear();

				List<Document> remainingResults = vectorStore
					.similaritySearch(SearchRequest.builder().query("technology").topK(10).build());
				assertThat(remainingResults).hasSize(2);

				vectorStore.delete(testDocuments.stream().map(Document::getId).toList());
			});
	}

	@Test
	public void searchWithInFilterTest() {
		this.contextRunner
			.withPropertyValues("spring.ai.vectorstore.oceanbase.url=" + getJdbcUrl(),
					"spring.ai.vectorstore.oceanbase.username=" + USERNAME,
					"spring.ai.vectorstore.oceanbase.password=" + PASSWORD,
					"spring.ai.vectorstore.oceanbase.tableName=" + OCEANBASE_DATABASE + "_in_filter")
			.run(context -> {
				VectorStore vectorStore = context.getBean(VectorStore.class);

				assertThat(vectorStore).isInstanceOf(OceanBaseVectorStore.class);

				List<Document> testDocuments = List.of(
						new Document("1", "Spring AI framework", Map.of("category", "framework")),
						new Document("2", "OceanBase database", Map.of("category", "database")),
						new Document("3", "Java language", Map.of("category", "language")),
						new Document("4", "Python language", Map.of("category", "language")));

				vectorStore.add(testDocuments);

				Awaitility.await()
					.until(() -> vectorStore.similaritySearch(SearchRequest.builder().query("framework").topK(1).build()),
							hasSize(1));

				Filter.Expression inFilter = new Filter.Expression(Filter.ExpressionType.IN, new Filter.Key("category"),
						new Filter.Value(List.of("framework", "database")));

				List<Document> inResults = vectorStore.similaritySearch(SearchRequest.builder()
					.query("technology")
					.topK(10)
					.filterExpression(inFilter)
					.build());

				assertThat(inResults).hasSize(2);
				assertThat(inResults.stream()
					.allMatch(doc -> "framework".equals(doc.getMetadata().get("category"))
							|| "database".equals(doc.getMetadata().get("category")))).isTrue();

				Filter.Expression ninFilter = new Filter.Expression(Filter.ExpressionType.NIN, new Filter.Key("category"),
						new Filter.Value(List.of("framework", "database")));

				List<Document> ninResults = vectorStore.similaritySearch(SearchRequest.builder()
					.query("programming")
					.topK(10)
					.filterExpression(ninFilter)
					.build());

				assertThat(ninResults).hasSize(2);
				assertThat(ninResults.stream()
					.allMatch(doc -> "language".equals(doc.getMetadata().get("category")))).isTrue();

				vectorStore.delete(testDocuments.stream().map(Document::getId).toList());
			});
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

	static class ObservationTestUtil {

		private ObservationTestUtil() {
		}

		public static void assertObservationRegistry(TestObservationRegistry observationRegistry,
				String vectorStoreProvider, VectorStoreObservationContext.Operation operation) {
			TestObservationRegistryAssert.assertThat(observationRegistry)
				.doesNotHaveAnyRemainingCurrentObservation()
				.hasObservationWithNameEqualTo(DefaultVectorStoreObservationConvention.DEFAULT_NAME)
				.that()
				.hasContextualNameEqualTo(vectorStoreProvider + " " + operation.value())
				.hasBeenStarted()
				.hasBeenStopped();
		}

	}

	private String getJdbcUrl() {
		return "jdbc:oceanbase://" + oceanBaseContainer.getHost() + ":" + oceanBaseContainer.getMappedPort(PORT) + "/"
				+ OCEANBASE_DATABASE;
	}

	@SuppressWarnings("resource")
	private OceanBaseCEContainer initOceanbaseContainer() {
		return new OceanBaseCEContainer(IMAGE).withEnv("MODE", "slim")
			.withEnv("OB_DATAFILE_SIZE", "2G")
			.withNetwork(NETWORK)
			.withNetworkAliases(HOSTNAME)
			.withExposedPorts(PORT)
			.withImagePullPolicy(PullPolicy.defaultPolicy())
			.waitingFor(Wait.forLogMessage(".*boot success!.*", 1))
			.withStartupTimeout(Duration.ofMinutes(5))
			.withLogConsumer(new Slf4jLogConsumer(DockerLoggerFactory.getLogger(IMAGE)));
	}

	private void initializeJdbcConnection(String jdbcUrl)
			throws SQLException, InstantiationException, IllegalAccessException, java.lang.reflect.InvocationTargetException, NoSuchMethodException {
		Driver driver = (Driver) loadDriverClass().getDeclaredConstructor().newInstance();
		Properties props = new Properties();

		props.put("user", USERNAME);
		props.put("password", PASSWORD);

		if (oceanBaseContainer != null) {
			jdbcUrl = jdbcUrl.replace(HOSTNAME, oceanBaseContainer.getHost());
		}

		this.connection = driver.connect(jdbcUrl, props);
		connection.setAutoCommit(false);
	}

	private Class<?> loadDriverClass() {
		try {
			return Class.forName(OCEANBASE_DRIVER_CLASS);
		}
		catch (Exception e) {
			throw new RuntimeException("Failed to load driver class: " + OCEANBASE_DRIVER_CLASS, e);
		}
	}

	private void createSchemaIfNeeded() {
		String sql = "CREATE DATABASE IF NOT EXISTS " + OCEANBASE_DATABASE;
		executeSql(sql);
	}

	private void executeSql(String sql) {
		try {
			connection.prepareStatement(sql).executeUpdate();
		}
		catch (Exception e) {
			throw new RuntimeException("Fail to execute sql " + sql, e);
		}
	}

}
