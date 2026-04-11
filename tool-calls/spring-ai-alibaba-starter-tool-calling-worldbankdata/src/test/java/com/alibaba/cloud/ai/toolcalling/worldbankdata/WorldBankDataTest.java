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
package com.alibaba.cloud.ai.toolcalling.worldbankdata;

import com.alibaba.cloud.ai.toolcalling.common.CommonToolCallAutoConfiguration;
import com.alibaba.cloud.ai.toolcalling.common.interfaces.SearchService;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.IOException;
import java.util.logging.Logger;

@SpringBootTest(classes = { CommonToolCallAutoConfiguration.class, WorldBankDataAutoConfiguration.class })
@DisplayName("World Bank Data Test")
public class WorldBankDataTest {

	private static MockWebServer mockWebServer;

	@BeforeAll
	static void setUp() throws IOException {
		mockWebServer = new MockWebServer();
		mockWebServer.start();
	}

	@AfterAll
	static void tearDown() throws IOException {
		mockWebServer.shutdown();
	}

	@DynamicPropertySource
	static void registerBaseUrl(DynamicPropertyRegistry registry) {
		registry.add("spring.ai.alibaba.toolcalling.worldbankdata.base-url", () -> mockWebServer.url("/").toString());
	}

	@Autowired
	private WorldBankDataService worldBankDataService;

	private static final Logger log = Logger.getLogger(WorldBankDataTest.class.getName());

	@Test
	@DisplayName("Tool-Calling Test - Specific Implementation")
	public void testWorldBankDataService() {
		String responseJson = "[{\"page\":1,\"pages\":1},[{\"indicator\":{\"id\":\"NY.GDP.MKTP.CD\",\"value\":\"GDP\"},\"country\":{\"id\":\"CHN\",\"value\":\"China\"},\"value\":10000,\"date\":\"2022\"}]]";
		mockWebServer.enqueue(new MockResponse().setBody(responseJson).addHeader("Content-Type", "application/json"));

		// Test with a popular indicator - GDP Current US$
		var resp = worldBankDataService.apply(WorldBankDataService.Request.simpleQuery("NY.GDP.MKTP.CD"));
		Assertions.assertNotNull(resp);
		Assertions.assertNotNull(resp.results());
		log.info("GDP Data results: " + resp.results());

		String popResponseJson = "[{\"page\":1},[{\"indicator\":{\"id\":\"SP.POP.TOTL\",\"value\":\"Population\"},\"country\":{\"id\":\"CHN\",\"value\":\"China\"},\"value\":1400000000,\"date\":\"2022\"}]]";
		mockWebServer.enqueue(new MockResponse().setBody(popResponseJson).addHeader("Content-Type", "application/json"));

		// Test with country specific query
		var chinaPopResp = worldBankDataService
			.apply(new WorldBankDataService.Request("中国人口", "CHN", "SP.POP.TOTL", "2020:2023", "data", 1, 5, null));
		Assertions.assertNotNull(chinaPopResp);
		Assertions.assertNotNull(chinaPopResp.results());
		log.info("China Population results: " + chinaPopResp.results());
	}

	@Autowired
	private SearchService searchService;

	@Test
	@DisplayName("Abstract Search Service Test")
	public void testAbstractSearch() {
		String responseJson = "[{\"page\":1},[{\"indicator\":{\"id\":\"SP.POP.TOTL\"},\"country\":{\"id\":\"WLD\"},\"value\":8000000000,\"date\":\"2023\"}]]";
		mockWebServer.enqueue(new MockResponse().setBody(responseJson).addHeader("Content-Type", "application/json"));

		// Test using abstract SearchService interface
		var resp = searchService.query("SP.POP.TOTL");
		Assertions.assertNotNull(resp);
		Assertions.assertNotNull(resp.getSearchResult());
		Assertions.assertNotNull(resp.getSearchResult().results());
		Assertions.assertFalse(resp.getSearchResult().results().isEmpty());
		log.info("Abstract search results: " + resp.getSearchResult());

		String lifeResponseJson = "[{\"page\":1},[{\"indicator\":{\"id\":\"SP.DYN.LE00.IN\"},\"country\":{\"id\":\"WLD\"},\"value\":73,\"date\":\"2021\"}]]";
		mockWebServer.enqueue(new MockResponse().setBody(lifeResponseJson).addHeader("Content-Type", "application/json"));

		// Test with another common indicator - Life Expectancy
		var lifeExpResp = searchService.query("SP.DYN.LE00.IN");
		Assertions.assertNotNull(lifeExpResp);
		Assertions.assertNotNull(lifeExpResp.getSearchResult());
		log.info("Life Expectancy search results: " + lifeExpResp.getSearchResult());
	}

	@Test
	@DisplayName("Country Code Detection Test")
	public void testCountryCodeDetection() {
		String responseJson = "[{\"page\":1},[{\"id\":\"USA\",\"name\":\"United States\",\"iso2Code\":\"US\"}]]";
		mockWebServer.enqueue(new MockResponse().setBody(responseJson).addHeader("Content-Type", "application/json"));

		// Test automatic country code detection
		var usaResp = worldBankDataService.apply(WorldBankDataService.Request.simpleQuery("USA"));
		Assertions.assertNotNull(usaResp);
		Assertions.assertNotNull(usaResp.results());
		log.info("USA country info: " + usaResp.results());

		String chnResponseJson = "[{\"page\":1},[{\"id\":\"CHN\",\"name\":\"China\",\"iso2Code\":\"CN\"}]]";
		mockWebServer.enqueue(new MockResponse().setBody(chnResponseJson).addHeader("Content-Type", "application/json"));

		// Test with China
		var chnResp = worldBankDataService.apply(WorldBankDataService.Request.simpleQuery("CHN"));
		Assertions.assertNotNull(chnResp);
		Assertions.assertNotNull(chnResp.results());
		log.info("China country info: " + chnResp.results());
	}

	@Test
	@DisplayName("Indicator Code Detection Test")
	public void testIndicatorCodeDetection() {
		String responseJson = "[{\"page\":1},[{\"indicator\":{\"id\":\"NY.GDP.MKTP.CD\"},\"country\":{\"id\":\"WLD\"},\"value\":100000000000000}]]";
		mockWebServer.enqueue(new MockResponse().setBody(responseJson).addHeader("Content-Type", "application/json"));

		// Test automatic indicator code detection
		var gdpResp = worldBankDataService.apply(WorldBankDataService.Request.simpleQuery("NY.GDP.MKTP.CD"));
		Assertions.assertNotNull(gdpResp);
		Assertions.assertNotNull(gdpResp.results());
		log.info("GDP indicator results: " + gdpResp.results());

		String popResponseJson = "[{\"page\":1},[{\"indicator\":{\"id\":\"SP.POP.TOTL\"},\"country\":{\"id\":\"WLD\"},\"value\":8000000000}]]";
		mockWebServer.enqueue(new MockResponse().setBody(popResponseJson).addHeader("Content-Type", "application/json"));

		// Test with another indicator
		var popResp = worldBankDataService.apply(WorldBankDataService.Request.simpleQuery("SP.POP.TOTL"));
		Assertions.assertNotNull(popResp);
		Assertions.assertNotNull(popResp.results());
		log.info("Population indicator results: " + popResp.results());
	}

}
