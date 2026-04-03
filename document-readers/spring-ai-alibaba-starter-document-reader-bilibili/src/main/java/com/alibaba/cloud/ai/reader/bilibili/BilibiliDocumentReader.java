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
package com.alibaba.cloud.ai.reader.bilibili;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentReader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * A class for reading and parsing video information and subtitles from Bilibili.
 * Implements the DocumentReader interface to provide methods for obtaining document
 * content.
 *
 * @author zshs000
 */
public class BilibiliDocumentReader implements DocumentReader {

	private static final Logger logger = LoggerFactory.getLogger(BilibiliDocumentReader.class);

	public static final String SDK_FLAG = "SpringAIAlibaba";

	private static final String API_VIDEO_INFO = "https://api.bilibili.com/x/web-interface/view";

	private static final String API_PAGE_LIST = "https://api.bilibili.com/x/player/pagelist";

	private static final String API_PLAYER_WBI = "https://api.bilibili.com/x/player/wbi/v2";

	private static final String API_NAV = "https://api.bilibili.com/x/web-interface/nav";

	private static final int[] MIXIN_KEY_ENC_TAB = { 46, 47, 18, 2, 53, 8, 23, 32, 15, 50, 10, 31, 58, 3, 45, 35, 27,
			43, 5, 49, 33, 9, 42, 19, 29, 28, 14, 39, 12, 38, 41, 13, 37, 48, 7, 16, 24, 55, 40, 61, 26, 17, 0, 1, 60,
			51, 30, 4, 22, 25, 54, 21, 56, 59, 6, 63, 57, 62, 11, 36, 20, 34, 44, 52 };

	private final @Nullable BilibiliResource bilibiliResource;

	private final @Nullable List<BilibiliResource> bilibiliResourceList;

	private final ObjectMapper objectMapper;

	private final WebClient webClient;

	private static final int MEMORY_SIZE = 5;

	private static final int BYTE_SIZE = 1024;

	private static final int MAX_MEMORY_SIZE = MEMORY_SIZE * BYTE_SIZE * BYTE_SIZE;

	public BilibiliDocumentReader(BilibiliResource bilibiliResource) {
		this.bilibiliResource = bilibiliResource;
		this.bilibiliResourceList = null;
		this.objectMapper = new ObjectMapper();
		this.webClient = createWebClient(bilibiliResource.getCredentials());
	}

	public BilibiliDocumentReader(List<BilibiliResource> bilibiliResourceList) {
		this.bilibiliResourceList = bilibiliResourceList;
		this.bilibiliResource = null;
		this.objectMapper = new ObjectMapper();
		// Use credentials from first resource for WebClient
		this.webClient = createWebClient(
				bilibiliResourceList.isEmpty() ? null : bilibiliResourceList.get(0).getCredentials());
	}

	private WebClient createWebClient(@Nullable BilibiliCredentials credentials) {
		return WebClient.builder()
			.defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
			.defaultHeader(HttpHeaders.USER_AGENT, userAgent())
			.defaultHeader(HttpHeaders.COOKIE, buildCookieHeader(credentials))
			.codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(MAX_MEMORY_SIZE))
			.build();
	}

	private static String userAgent() {
		return String.format("%s/%s; java/%s; platform/%s; processor/%s", SDK_FLAG, "1.0.0",
				System.getProperty("java.version"), System.getProperty("os.name"), System.getProperty("os.arch"));
	}

	private String buildCookieHeader(@Nullable BilibiliCredentials credentials) {
		if (credentials == null) {
			return "";
		}
		List<String> cookies = new ArrayList<>();
		if (credentials.getSessdata() != null && !credentials.getSessdata().isEmpty()) {
			cookies.add("SESSDATA=" + credentials.getSessdata());
		}
		if (credentials.getBiliJct() != null && !credentials.getBiliJct().isEmpty()) {
			cookies.add("bili_jct=" + credentials.getBiliJct());
		}
		if (credentials.getBuvid3() != null && !credentials.getBuvid3().isEmpty()) {
			cookies.add("buvid3=" + credentials.getBuvid3());
		}
		return String.join("; ", cookies);
	}

	@Override
	public List<Document> get() {
		List<Document> documents = new ArrayList<>();
		if (!Objects.isNull(bilibiliResourceList) && !bilibiliResourceList.isEmpty()) {
			for (BilibiliResource resource : bilibiliResourceList) {
				documents.addAll(processResource(resource));
			}
		}
		else if (bilibiliResource != null) {
			documents.addAll(processResource(bilibiliResource));
		}
		return documents;
	}

	private List<Document> processResource(BilibiliResource resource) {
		List<Document> documents = new ArrayList<>();
		try {
			String bvid = resource.getBvid();

			// Step 1: Get video basic info
				String videoInfoResponse = webClient.get()
					.uri(API_VIDEO_INFO + "?bvid=" + bvid)
					.retrieve()
					.bodyToMono(String.class)
					.block();

				JsonNode videoData = parseJson(Objects.requireNonNull(videoInfoResponse, "video info response must not be null"))
					.path("data");
			String title = videoData.path("title").asText();
			String description = videoData.path("desc").asText();

			// Add video info document
			Map<String, Object> infoMetadata = new java.util.HashMap<>();
			infoMetadata.put("bvid", bvid);
			infoMetadata.put("document_type", "metadata");
			infoMetadata.put("title", title);
			infoMetadata.put("description", description);
			Document infoDoc = new Document("Video information", infoMetadata);
			documents.add(infoDoc);

			// Step 2: Get page list to obtain cid
				String pageListResponse = webClient.get()
					.uri(API_PAGE_LIST + "?bvid=" + bvid)
					.retrieve()
					.bodyToMono(String.class)
					.block();

				JsonNode pageData = parseJson(Objects.requireNonNull(pageListResponse, "page list response must not be null"))
					.path("data");
			if (!pageData.isArray() || pageData.size() == 0) {
				logger.error("No page data found for video: {}", bvid);
				documents.add(new Document("Error: No page data found"));
				return documents;
			}

			// Step 3: Get subtitles for all pages/parts
			StringBuilder allTranscripts = new StringBuilder();
			for (int i = 0; i < pageData.size(); i++) {
				JsonNode page = pageData.get(i);
				long cid = page.path("cid").asLong();

				String transcript = fetchSubtitleTranscript(bvid, cid);
				if (!transcript.isEmpty()) {
					allTranscripts.append(transcript).append(" ");
				}
			}

			String finalContent = allTranscripts.length() > 0
					? String.format("Video Title: %s, Description: %s\nTranscript: %s", title, description,
							allTranscripts.toString().trim())
					: String.format("No subtitles found for video: %s", bvid);

			Map<String, Object> contentMetadata = new java.util.HashMap<>();
			contentMetadata.put("bvid", bvid);
			contentMetadata.put("document_type", "content");
			contentMetadata.put("title", title);
			documents.add(new Document(finalContent, contentMetadata));

		}
		catch (Exception e) {
			logger.error("Error processing Bilibili video: {}", resource.getBvid(), e);
			documents.add(new Document("Error: " + e.getMessage()));
		}
		return documents;
	}

	private String fetchSubtitleTranscript(String bvid, long cid) throws IOException {
		// Get mixin key for WBI signature
		String mixinKey = getMixinKey();

		// Generate WBI parameters
		Map<String, Object> params = new TreeMap<>();
		params.put("bvid", bvid);
		params.put("cid", cid);
		params.put("wts", System.currentTimeMillis() / 1000);
		params.put("web_location", 1315873);

		// Build query string
		String queryString = params.entrySet()
			.stream()
			.map(e -> e.getKey() + "=" + URLEncoder.encode(String.valueOf(e.getValue()), StandardCharsets.UTF_8))
			.collect(Collectors.joining("&"));

		// Calculate w_rid
		String signString = queryString + mixinKey;
		String wRid = md5(signString);

		// Fetch player info with WBI signature
		String playerUrl = API_PLAYER_WBI + "?" + queryString + "&w_rid=" + wRid;
		String playerResponse = webClient.get().uri(playerUrl).retrieve().bodyToMono(String.class).block();

		JsonNode playerData = parseJson(Objects.requireNonNull(playerResponse, "player response must not be null"))
			.path("data");
		JsonNode subtitleList = playerData.path("subtitle").path("subtitles");

		if (subtitleList.isArray() && subtitleList.size() > 0) {
			String subtitleUrl = subtitleList.get(0).path("subtitle_url").asText();

			// Add https: prefix if missing
			if (subtitleUrl.startsWith("//")) {
				subtitleUrl = "https:" + subtitleUrl;
			}

			// Download subtitle content
			String subtitleResponse = webClient.get().uri(subtitleUrl).retrieve().bodyToMono(String.class).block();

			JsonNode subtitleJson = parseJson(
					Objects.requireNonNull(subtitleResponse, "subtitle response must not be null"));
			StringBuilder rawTranscript = new StringBuilder();
			subtitleJson.path("body").forEach(node -> rawTranscript.append(node.path("content").asText()).append(" "));

			return rawTranscript.toString().trim();
		}
		else {
			return "";
		}
	}

	private String getMixinKey() throws IOException {
		// Fetch navigation API to get img_url and sub_url
		String navResponse = webClient.get().uri(API_NAV).retrieve().bodyToMono(String.class).block();

		JsonNode navData = parseJson(Objects.requireNonNull(navResponse, "nav response must not be null"))
			.path("data")
			.path("wbi_img");
		String imgUrl = navData.path("img_url").asText();
		String subUrl = navData.path("sub_url").asText();

		// Extract keys from URLs
		String imgKey = imgUrl.substring(imgUrl.lastIndexOf('/') + 1).replace(".png", "");
		String subKey = subUrl.substring(subUrl.lastIndexOf('/') + 1).replace(".png", "");
		String rawKey = imgKey + subKey;

		// Shuffle according to MIXIN_KEY_ENC_TAB
		StringBuilder mixinKey = new StringBuilder();
		for (int index : MIXIN_KEY_ENC_TAB) {
			if (index < rawKey.length()) {
				mixinKey.append(rawKey.charAt(index));
			}
		}

		// Return first 32 characters
		return mixinKey.substring(0, Math.min(32, mixinKey.length()));
	}

	private String md5(String input) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
			StringBuilder hex = new StringBuilder();
			for (byte b : digest) {
				String tmp = Integer.toHexString(b & 0xFF);
				if (tmp.length() == 1) {
					hex.append("0");
				}
				hex.append(tmp);
			}
			return hex.toString();
		}
		catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("MD5 algorithm not found", e);
		}
	}

	private JsonNode parseJson(String jsonResponse) throws IOException {
		return objectMapper.readTree(jsonResponse);
	}

}
