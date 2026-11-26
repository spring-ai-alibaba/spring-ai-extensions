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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import io.micrometer.observation.tck.TestObservationRegistry;
import io.micrometer.observation.tck.TestObservationRegistryAssert;
import org.awaitility.Awaitility;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.images.PullPolicy;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.oceanbase.OceanBaseCEContainer;
import org.testcontainers.utility.DockerLoggerFactory;
import org.springframework.boot.SpringBootConfiguration;

import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.test.vectorstore.BaseVectorStoreTests;
import org.springframework.ai.transformers.TransformersEmbeddingModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.observation.DefaultVectorStoreObservationConvention;
import org.springframework.ai.vectorstore.observation.VectorStoreObservationContext;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.DefaultResourceLoader;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;

/**
 * OceanBase vector store test. This class tests adding, searching, and deleting documents
 * in OceanBase.
 */
@Testcontainers
class OceanBaseVectorStoreIT extends BaseVectorStoreTests {

	private static final String IMAGE = "oceanbase/oceanbase-ce:latest";

	private static final String HOSTNAME = "oceanbase_test";

	private static final int PORT = 2881;

	public static final Network NETWORK = Network.newNetwork();

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
			.withUserConfiguration(TestApplication.class);

	List<Document> documents = List.of(
			new Document("1", getText("classpath:spring.ai.txt"), Map.of("docId", "1", "spring", "great")),
			new Document("2", getText("classpath:time.shelter.txt"), Map.of("docId", "1")),
			new Document("3", getText("classpath:great.depression.txt"), Map.of("docId", "1", "depression", "bad")));

	@BeforeAll
	public static void beforeAll() {
		Awaitility.setDefaultPollInterval(2, TimeUnit.SECONDS);
		Awaitility.setDefaultPollDelay(Duration.ZERO);
		Awaitility.setDefaultTimeout(Duration.ofMinutes(1));
	}

	@BeforeEach
	public void cleanDatabase() {
		this.contextRunner.run(context -> {
			JdbcTemplate jdbcTemplate = context.getBean(JdbcTemplate.class);
			jdbcTemplate.execute("delete from spring_ai_test_table where 1=1");
		});
	}

	public static String getText(String uri) {
		var resource = new DefaultResourceLoader().getResource(uri);
		try {
			return resource.getContentAsString(StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected void executeTest(Consumer<VectorStore> testFunction) {
		this.contextRunner.run(context -> {
			VectorStore vectorStore = context.getBean(VectorStore.class);
			testFunction.accept(vectorStore);
		});
	}

	@Test
	public void addAndSearchTest() {
		this.contextRunner
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
				.withPropertyValues("spring.ai.vectorstore.oceanbase.hybridSearchType=fulltext")
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

	@Test
	public void documentUpdateTest() {
		this.contextRunner
				.run(context -> {
					VectorStore vectorStore = context.getBean(VectorStore.class);

					Document document = new Document(java.util.UUID.randomUUID().toString(), "Spring AI rocks!!",
							java.util.Collections.singletonMap("meta1", "meta1"));

					vectorStore.add(List.of(document));

					Awaitility.await()
							.until(() -> vectorStore.similaritySearch(SearchRequest.builder().query("Spring").topK(5).build()),
									hasSize(1));

					List<Document> results = vectorStore
							.similaritySearch(SearchRequest.builder().query("Spring").topK(5).build());

					assertThat(results).hasSize(1);
					Document resultDoc = results.get(0);
					assertThat(resultDoc.getId()).isEqualTo(document.getId());
					assertThat(resultDoc.getText()).isEqualTo("Spring AI rocks!!");
					assertThat(resultDoc.getMetadata()).containsKey("meta1");

					Document sameIdDocument = new Document(document.getId(),
							"The World is Big and Salvation Lurks Around the Corner",
							java.util.Collections.singletonMap("meta2", "meta2"));

					vectorStore.add(List.of(sameIdDocument));

					Awaitility.await()
							.until(() -> vectorStore.similaritySearch(SearchRequest.builder().query("FooBar").topK(5).build()),
									hasSize(1));

					results = vectorStore.similaritySearch(SearchRequest.builder().query("FooBar").topK(5).build());

					assertThat(results).hasSize(1);
					resultDoc = results.get(0);
					assertThat(resultDoc.getId()).isEqualTo(document.getId());
					assertThat(resultDoc.getText()).isEqualTo("The World is Big and Salvation Lurks Around the Corner");
					assertThat(resultDoc.getMetadata()).containsKey("meta2");

					vectorStore.delete(List.of(document.getId()));
				});
	}

	@Test
	public void searchThresholdTest() {
		this.contextRunner
				.withPropertyValues("spring.ai.vectorstore.oceanbase.hybridSearchType=")
				.run(context -> {
					VectorStore vectorStore = context.getBean(VectorStore.class);

					vectorStore.add(this.documents);

					Awaitility.await()
							.until(() -> vectorStore.similaritySearch(SearchRequest.builder().query("Spring").topK(1).build()),
									hasSize(1));

					List<Document> fullResult = vectorStore
							.similaritySearch(SearchRequest.builder().query("Depression").topK(5).similarityThresholdAll().build());

					List<Double> scores = fullResult.stream().map(Document::getScore).toList();
					assertThat(scores).hasSize(3);

					double similarityThreshold = (scores.get(0) + scores.get(1)) / 2;

					List<Document> results = vectorStore.similaritySearch(
							SearchRequest.builder().query("Depression").topK(5).similarityThreshold(similarityThreshold).build());

					assertThat(results).hasSize(1);
					Document resultDoc = results.get(0);
					assertThat(resultDoc.getId()).isEqualTo(this.documents.get(2).getId()); // "great.depression.txt"
					assertThat(resultDoc.getScore()).isGreaterThanOrEqualTo(similarityThreshold);

					vectorStore.delete(this.documents.stream().map(Document::getId).toList());
				});
	}

	@Test
	public void deleteWithComplexFilterExpressionTest() {
		this.contextRunner
				.run(context -> {
					VectorStore vectorStore = context.getBean(VectorStore.class);

					var doc1 = new Document("Content 1", Map.of("type", "A", "priority", 1));
					var doc2 = new Document("Content 2", Map.of("type", "A", "priority", 2));
					var doc3 = new Document("Content 3", Map.of("type", "B", "priority", 1));

					vectorStore.add(List.of(doc1, doc2, doc3));

					Awaitility.await()
							.until(() -> vectorStore.similaritySearch(SearchRequest.builder().query("Content").topK(5).build()),
									hasSize(3));

					// Complex filter expression: (type == 'A' AND priority > 1)
					Filter.Expression priorityFilter = new Filter.Expression(Filter.ExpressionType.GT,
							new Filter.Key("priority"), new Filter.Value(1));
					Filter.Expression typeFilter = new Filter.Expression(Filter.ExpressionType.EQ, new Filter.Key("type"),
							new Filter.Value("A"));
					Filter.Expression complexFilter = new Filter.Expression(Filter.ExpressionType.AND, typeFilter,
							priorityFilter);

					vectorStore.delete(complexFilter);

					// Wait for deletion (OceanBase might have delay, though usually delete is transactional)
					// Re-query to verify
					Awaitility.await()
							.until(() -> vectorStore.similaritySearch(SearchRequest.builder().query("Content").topK(5).similarityThresholdAll().build()),
									hasSize(2));

					var results = vectorStore
							.similaritySearch(SearchRequest.builder().query("Content").topK(5).similarityThresholdAll().build());

					assertThat(results).hasSize(2);
					assertThat(results.stream().map(doc -> doc.getMetadata().get("type")).toList())
							.containsExactlyInAnyOrder("A", "B");
					assertThat(results.stream().map(doc -> doc.getMetadata().get("priority")).toList())
							.containsExactlyInAnyOrder(1, 1);

					vectorStore.delete(List.of(doc1.getId(), doc3.getId()));
				});
	}

	@SpringBootConfiguration
	public static class TestApplication {

		@Bean
		public TestObservationRegistry observationRegistry() {
			return TestObservationRegistry.create();
		}

		@Bean
		public EmbeddingModel embeddingModel() {
			return new TransformersEmbeddingModel();
		}

		@Bean
		public DataSource dataSource() {
			DriverManagerDataSource dataSource = new DriverManagerDataSource();
			dataSource.setUrl(oceanBaseContainer.getJdbcUrl());
			dataSource.setUsername(oceanBaseContainer.getUsername());
			dataSource.setPassword(oceanBaseContainer.getPassword());
			return dataSource;
		}

		@Bean
		public JdbcTemplate jdbcTemplate(DataSource dataSource) {
			return new JdbcTemplate(dataSource);
		}

		@Bean
		public OceanBaseVectorStore vectorStore(EmbeddingModel embeddingModel, DataSource dataSource, TestObservationRegistry observationRegistry, Environment environment) {
			return OceanBaseVectorStore.builder("spring_ai_test_table", dataSource, embeddingModel)
					.initializeSchema(true)
					.hybridSearchType(environment.getProperty("spring.ai.vectorstore.oceanbase.hybridSearchType", ""))
					.observationRegistry(observationRegistry)
					.build();
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
}
