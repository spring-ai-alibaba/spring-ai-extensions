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
package com.alibaba.cloud.ai.reader.arxiv;

import com.alibaba.cloud.ai.reader.arxiv.client.*;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIf;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for arXiv client
 *
 * @author brianxiadong
 */

@DisabledIf("GithubCI")
public class ArxivClientTest {

	/**
	 * Check if the tests are running in Local. In GitHub CI environment, this test not
	 * running.
	 */
	static boolean GithubCI() {
		return "true".equals(System.getenv("ENABLE_TEST_CI"));
	}

	@Test
	public void testBasicSearch() throws IOException {
		// Create client
		ArxivClient client = new ArxivClient();

		// Create search
		ArxivSearch search = new ArxivSearch();
		search.setQuery("cat:cs.AI AND ti:\"artificial intelligence\"");
		search.setMaxResults(5);

		// Execute search
		Iterator<ArxivResult> results = client.results(search, 0);

		// Verify results
		List<ArxivResult> resultList = new ArrayList<>();
		results.forEachRemaining(resultList::add);

		assertEquals(5, resultList.size(), "Should return 5 results");

		// Verify basic information of the first result
		ArxivResult firstResult = resultList.get(0);
		assertNotNull(firstResult.getEntryId(), "Article ID should not be null");
		assertNotNull(firstResult.getTitle(), "Title should not be null");
		assertNotNull(firstResult.getAuthors(), "Author list should not be null");
		assertFalse(firstResult.getAuthors().isEmpty(), "Author list should not be empty");
		assertNotNull(firstResult.getSummary(), "Summary should not be null");
		assertNotNull(firstResult.getCategories(), "Category list should not be null");
		assertFalse(firstResult.getCategories().isEmpty(), "Category list should not be empty");
		assertTrue(firstResult.getCategories().contains("cs.AI"), "Should contain cs.AI category");
	}

	@Test
	public void testSearchWithIdList() throws IOException {
		ArxivClient client = new ArxivClient();

		ArxivSearch search = new ArxivSearch();
		List<String> idList = new ArrayList<>();
		idList.add("2501.01639v1"); // Replace with an actual existing article ID
		search.setIdList(idList);

		Iterator<ArxivResult> results = client.results(search, 0);

		List<ArxivResult> resultList = new ArrayList<>();
		results.forEachRemaining(resultList::add);

		assertFalse(resultList.isEmpty(), "Should return at least one result");
		assertEquals("2501.01639v1", resultList.get(0).getShortId(), "Should return the article with the specified ID");
	}

	@Test
	public void testSearchWithSorting() throws IOException {
		ArxivClient client = new ArxivClient();

		ArxivSearch search = new ArxivSearch();
		search.setQuery("cat:cs.AI AND ti:\"artificial intelligence\"");
		search.setMaxResults(10);
		search.setSortBy(ArxivSortCriterion.SUBMITTED_DATE);
		search.setSortOrder(ArxivSortOrder.DESCENDING);

		Iterator<ArxivResult> results = client.results(search, 0);

		List<ArxivResult> resultList = new ArrayList<>();
		results.forEachRemaining(resultList::add);

		assertEquals(10, resultList.size(), "Should return 10 results");

		// Verify results are sorted by submission date in descending order
		for (int i = 1; i < resultList.size(); i++) {
			assertTrue(
					resultList.get(i - 1).getPublished().isAfter(resultList.get(i).getPublished())
							|| resultList.get(i - 1).getPublished().equals(resultList.get(i).getPublished()),
					"Results should be sorted by submission date in descending order");
		}
	}

	@Test
	public void testPagination() throws IOException {
		HttpServer server = createPaginationServer();
		server.start();
		try {
			ArxivClient client = new TestArxivClient(1, 0.0f, 0,
					"http://localhost:" + server.getAddress().getPort() + "/api/query?%s");

			ArxivSearch search = new ArxivSearch();
			search.setIdList(List.of("2501.01639v1", "2212.12633v2"));
			search.setMaxResults(2); // Request more than one page

			// Get first page
			Iterator<ArxivResult> firstPage = client.results(search, 0);
			List<ArxivResult> firstPageResults = new ArrayList<>();

			// Only take at most pageSize (1) item from the iterator
			int count = 0;
			while (count < 1 && firstPage.hasNext()) {
				firstPageResults.add(firstPage.next());
				count++;
			}

			// Print debug information
			System.out.println("First page results count: " + firstPageResults.size());

			// Verify we have results from first page
			assertTrue(firstPageResults.size() > 0, "First page should return results");
			assertTrue(firstPageResults.size() <= 1, "First page should not exceed page size");

			// Get second page
			Iterator<ArxivResult> secondPage = client.results(search, firstPageResults.size());
			List<ArxivResult> secondPageResults = new ArrayList<>();

			// Take only up to 1 result from second page
			count = 0;
			while (count < 1 && secondPage.hasNext()) {
				secondPageResults.add(secondPage.next());
				count++;
			}

			System.out.println("Second page results count: " + secondPageResults.size());

			// Verify we have results from second page
			assertTrue(secondPageResults.size() > 0, "Second page should return results");
			assertTrue(secondPageResults.size() <= 1, "Second page should not exceed page size");

			// Verify results are different
			Set<String> firstPageIds = firstPageResults.stream()
				.map(ArxivResult::getEntryId)
				.collect(Collectors.toSet());
			Set<String> secondPageIds = secondPageResults.stream()
				.map(ArxivResult::getEntryId)
				.collect(Collectors.toSet());

			// Check for any overlap between pages
			Set<String> intersection = new HashSet<>(firstPageIds);
			intersection.retainAll(secondPageIds);
			assertTrue(intersection.isEmpty(), "Pages should not have overlapping results");
		}
		finally {
			server.stop(0);
		}
	}

	@Test
	public void testDownloadPdf() throws IOException {
		// Create client
		ArxivClient client = new ArxivClient();

		// Search for a specific paper
		ArxivSearch search = new ArxivSearch();
		search.setQuery("cat:cs.AI AND ti:\"artificial intelligence\"");
		search.setMaxResults(1);

		// Get search results
		Iterator<ArxivResult> results = client.results(search, 0);
		assertTrue(results.hasNext(), "Should have at least one search result");

		ArxivResult result = results.next();
		assertNotNull(result.getPdfUrl(), "PDF URL should not be null");

		// Create temporary directory for testing
		Path tempDir = Files.createTempDirectory("arxiv-test");

		// Test download with default filename
		Path defaultPath = client.downloadPdf(result, tempDir.toString());
		assertTrue(Files.exists(defaultPath), "PDF file should be downloaded");
		assertTrue(Files.size(defaultPath) > 0, "PDF file should not be empty");

		// Test download with custom filename
		String customFilename = "test_download.pdf";
		Path customPath = client.downloadPdf(result, tempDir.toString(), customFilename);
		assertTrue(Files.exists(customPath), "PDF file with custom filename should be downloaded");
		assertTrue(Files.size(customPath) > 0, "PDF file with custom filename should not be empty");
		assertEquals(customFilename, customPath.getFileName().toString(), "Filename should match the custom name");

		// Verify both files have the same content
		assertArrayEquals(Files.readAllBytes(defaultPath), Files.readAllBytes(customPath),
				"Both downloaded files should have the same content");

		// Clean up temporary files
		Files.deleteIfExists(defaultPath);
		Files.deleteIfExists(customPath);
		Files.deleteIfExists(tempDir);
	}

	private static HttpServer createPaginationServer() throws IOException {
		HttpServer server = HttpServer.create(new InetSocketAddress(0), 0);
		server.createContext("/api/query", exchange -> {
			String query = exchange.getRequestURI().getRawQuery();
			String id = query != null && query.contains("start=1") ? "2212.12633v2" : "2501.01639v1";
			byte[] body = atomFeed(id).getBytes(StandardCharsets.UTF_8);
			exchange.getResponseHeaders().set("Content-Type", "application/atom+xml; charset=UTF-8");
			exchange.sendResponseHeaders(200, body.length);
			try (OutputStream outputStream = exchange.getResponseBody()) {
				outputStream.write(body);
			}
		});
		return server;
	}

	private static String atomFeed(String id) {
		return """
				<?xml version="1.0" encoding="UTF-8"?>
				<feed xmlns="http://www.w3.org/2005/Atom" xmlns:opensearch="http://a9.com/-/spec/opensearch/1.1/">
					<opensearch:totalResults>2</opensearch:totalResults>
					<entry>
						<id>https://arxiv.org/abs/%s</id>
						<title>Test paper %s</title>
						<summary>Deterministic pagination fixture.</summary>
						<updated>2025-01-01T00:00:00Z</updated>
						<published>2025-01-01T00:00:00Z</published>
						<author><name>Test Author</name></author>
						<category term="cs.AI" />
						<link href="https://arxiv.org/abs/%s" rel="alternate" type="text/html" />
					</entry>
				</feed>
				""".formatted(id, id, id);
	}

	private static final class TestArxivClient extends ArxivClient {

		private TestArxivClient(int pageSize, float delaySeconds, int numRetries, String queryUrlFormat) {
			super(pageSize, delaySeconds, numRetries, queryUrlFormat);
		}

	}

}
