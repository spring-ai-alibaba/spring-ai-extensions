/*
 * Copyright 2026-2027 the original author or authors.
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
package com.alibaba.cloud.ai.reader.csdn;

import com.alibaba.cloud.ai.document.DocumentParser;
import com.alibaba.cloud.ai.parser.bshtml.BsHtmlDocumentParser;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentReader;
import org.springframework.http.HttpHeaders;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * A lightweight reader for public CSDN article pages.
 *
 * <p>It extracts the article block from HTML first, then delegates text extraction to
 * {@link BsHtmlDocumentParser}.
 *
 * @author zssh000
 */
public class CsdnDocumentReader implements DocumentReader {

	public static final String SDK_FLAG = "SpringAIAlibaba";

	private static final Logger logger = LoggerFactory.getLogger(CsdnDocumentReader.class);

	private static final Pattern ARTICLE_PATH_PATTERN = Pattern.compile("^/.+/article/details/\\d+/?$");

	private static final Pattern ARTICLE_ID_PATTERN = Pattern.compile("/article/details/(\\d+)");

	private final String articleUrl;

	private final WebClient webClient;

	private final DocumentParser htmlParser;

	public CsdnDocumentReader(String articleUrl) {
		this(articleUrl, defaultWebClient(), new BsHtmlDocumentParser());
	}

	public CsdnDocumentReader(String articleUrl, WebClient webClient) {
		this(articleUrl, webClient, new BsHtmlDocumentParser());
	}

	public CsdnDocumentReader(String articleUrl, WebClient webClient, DocumentParser htmlParser) {
		Assert.hasText(articleUrl, "CSDN article URL must not be empty");
		Assert.notNull(webClient, "WebClient must not be null");
		Assert.notNull(htmlParser, "DocumentParser must not be null");

		String trimmed = articleUrl.trim();
		if (!isValidCsdnArticleUrl(trimmed)) {
			throw new IllegalArgumentException("Invalid CSDN article URL: " + articleUrl);
		}

		this.articleUrl = trimmed;
		this.webClient = webClient;
		this.htmlParser = htmlParser;
	}

	private static WebClient defaultWebClient() {
		return WebClient.builder()
			.defaultHeader(HttpHeaders.ACCEPT, "text/html")
			.defaultHeader(HttpHeaders.USER_AGENT, userAgent())
			.codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024))
			.build();
	}

	private static String userAgent() {
		return String.format("%s/%s; java/%s; platform/%s; processor/%s", SDK_FLAG, "1.0.0",
				System.getProperty("java.version"), System.getProperty("os.name"), System.getProperty("os.arch"));
	}

	private static boolean isValidCsdnArticleUrl(String url) {
		try {
			URI uri = URI.create(url);
			String scheme = uri.getScheme();
			String host = uri.getHost();
			String path = uri.getPath();

			if (!"http".equalsIgnoreCase(scheme) && !"https".equalsIgnoreCase(scheme)) {
				return false;
			}
			if (!StringUtils.hasText(host) || !StringUtils.hasText(path)) {
				return false;
			}

			String normalizedHost = host.toLowerCase(Locale.ROOT);
			boolean hostAllowed = "csdn.net".equals(normalizedHost) || normalizedHost.endsWith(".csdn.net");
			return hostAllowed && ARTICLE_PATH_PATTERN.matcher(path).matches();
		}
		catch (Exception ignored) {
			return false;
		}
	}

	@Override
	public List<Document> get() {
		// Step 1: Fetch the raw HTML of the target CSDN article page.
		String html = webClient.get().uri(articleUrl).retrieve().bodyToMono(String.class).block();
		if (!StringUtils.hasText(html)) {
			throw new RuntimeException("Failed to fetch CSDN article content: " + articleUrl);
		}

		// Step 2: Parse the page and locate the main article container.
		org.jsoup.nodes.Document page = Jsoup.parse(html, articleUrl);
		Element articleElement = page.selectFirst("article.baidu_pl");
		if (articleElement == null) {
			articleElement = page.selectFirst("#article_content");
		}
		if (articleElement == null) {
			throw new RuntimeException("Failed to locate CSDN article block: " + articleUrl);
		}

		// Step 3: Remove obvious non-content nodes (ads/scripts/recommendations).
		Element cleanArticle = articleElement.clone();
		cleanArticle.select("script,style,noscript,iframe,ins.adsbygoogle,svg,link").remove();
		cleanArticle.select("div[id^=dmp_ad_],div[id^=kp_box_],.recommend-box,.second-recommend-box,.recommend-item-box")
			.remove();

		// Step 4: Convert cleaned article HTML into readable plain text.
		String text = parseArticleText(cleanArticle);
		if (!StringUtils.hasText(text)) {
			throw new RuntimeException("Failed to extract readable CSDN content: " + articleUrl);
		}

		// Step 5: Extract metadata and normalize the final text structure.
		Map<String, Object> metadata = extractMetadata(page, html);
		String finalText = buildDocumentText(metadata.get("title"), text);

		// Step 6: Return a single document containing content and metadata.
		return Collections.singletonList(new Document(finalText, metadata));
	}

	private String parseArticleText(Element cleanArticle) {
		try {
			try (ByteArrayInputStream inputStream = new ByteArrayInputStream(
					cleanArticle.outerHtml().getBytes(StandardCharsets.UTF_8))) {
				List<Document> parsedDocs = htmlParser.parse(inputStream);
				String parsedText = parsedDocs.stream()
					.map(Document::getText)
					.filter(StringUtils::hasText)
					.collect(Collectors.joining("\n"))
					.trim();
				if (StringUtils.hasText(parsedText)) {
					return parsedText;
				}
			}
		}
		catch (Exception ex) {
			logger.warn("BsHtmlDocumentParser failed for url: {}, message: {}, fallback to Jsoup text extraction",
					articleUrl, ex.getMessage());
			logger.debug("BsHtmlDocumentParser exception details for url: {}", articleUrl, ex);
		}
		return cleanArticle.text().replace('\u00A0', ' ').replaceAll("\\s+", " ").trim();
	}

	private Map<String, Object> extractMetadata(org.jsoup.nodes.Document page, String html) {
		Map<String, Object> metadata = new HashMap<>();

		metadata.put("source", articleUrl);

		String articleId = extractArticleId(articleUrl);
		if (StringUtils.hasText(articleId)) {
			metadata.put("article_id", articleId);
		}

		String title = normalizedText(page.selectFirst("h1.title-article"));
		if (!StringUtils.hasText(title)) {
			String pageTitle = page.title();
			if (StringUtils.hasText(pageTitle)) {
				title = pageTitle.replaceFirst("\\s*-\\s*CSDN博客\\s*$", "").trim();
			}
		}
		if (StringUtils.hasText(title)) {
			metadata.put("title", title);
		}

		// Resolve author with fallbacks: visible DOM first, then script variables.
		// This improves extraction robustness across different CSDN page variants.
		String author = normalizedText(page.selectFirst("a.follow-nickName"));
		if (!StringUtils.hasText(author)) {
			author = jsStringVar(html, "username");
		}
		if (!StringUtils.hasText(author)) {
			author = jsStringVar(html, "nickName");
		}
		if (StringUtils.hasText(author)) {
			metadata.put("author", author);
		}

		return metadata;
	}

	private String jsStringVar(String html, String varName) {
		String regex = "var\\s+" + Pattern.quote(varName) + "\\s*=\\s*(['\"])(.*?)\\1\\s*;";
		Matcher matcher = Pattern.compile(regex, Pattern.DOTALL).matcher(html);
		return matcher.find() ? matcher.group(2).trim() : "";
	}

	private String extractArticleId(String url) {
		Matcher matcher = ARTICLE_ID_PATTERN.matcher(url);
		return matcher.find() ? matcher.group(1) : "";
	}

	private String normalizedText(@Nullable Element element) {
		if (element == null) {
			return "";
		}
		return element.text().replace('\u00A0', ' ').replaceAll("\\s+", " ").trim();
	}

	/**
	 * Build the final document text using a stable two-part structure.
	 * Format:
	 * {@code Article Title: ...}
	 * {@code Content: ...}
	 * If title is missing, only {@code Content: ...} is returned.
	 * If body already starts with title, the duplicated prefix is removed.
	 */
	private String buildDocumentText(@Nullable Object titleObj, String bodyText) {
		String title = titleObj instanceof String ? ((String) titleObj).trim() : "";
		String body = bodyText == null ? "" : bodyText.trim();
		if (!StringUtils.hasText(title)) {
			return "Content: " + body;
		}
		// Some pages repeat the title as the first line of body; remove that prefix
		// to keep RAG input cleaner.
		if (body.startsWith(title)) {
			return "Article Title: " + title + "\nContent: " + body.substring(title.length()).trim();
		}
		return "Article Title: " + title + "\nContent: " + body;
	}

}
