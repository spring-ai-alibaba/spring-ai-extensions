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
package com.alibaba.cloud.ai.toolcalling.baidusearch;

import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link BaiduSearchService#parseHtml(String)}. These do not hit the
 * network, so they run without any external configuration.
 */
class BaiduSearchServiceParseHtmlTests {

	private final BaiduSearchService service = new BaiduSearchService(null, null, null);

	@Test
	void parseHtmlReturnsEmptyListWhenContentLeftMissing() {
		// Baidu can return an anti-bot/blank page without div#content_left; parsing must
		// return an empty list instead of throwing a NullPointerException. See #4701.
		List<BaiduSearchService.SearchResult> results = service
			.parseHtml("<html><body><div>blocked</div></body></html>");

		assertTrue(results.isEmpty());
	}

	@Test
	void parseHtmlExtractsResultContainers() {
		String html = "<html><body><div id=\"content_left\">"
				+ "<div class=\"c-container result-op\" mu=\"https://example.com\">" + "<h3>Example Title</h3>"
				+ "<div class=\"c-abstract\">Example abstract</div>" + "</div></div></body></html>";

		List<BaiduSearchService.SearchResult> results = service.parseHtml(html);

		assertEquals(1, results.size());
		assertEquals("Example Title", results.get(0).title());
		assertEquals("Example abstract", results.get(0).abstractText());
		assertEquals("https://example.com", results.get(0).sourceUrl());
	}

}
