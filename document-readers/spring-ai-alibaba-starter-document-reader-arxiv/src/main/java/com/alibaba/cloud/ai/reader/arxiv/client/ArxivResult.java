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
package com.alibaba.cloud.ai.reader.arxiv.client;

import org.jspecify.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * arXiv查询结果中的一个条目
 *
 * @see <a href=
 * "https://arxiv.org/help/api/user-manual#_details_of_atom_results_returned">arXiv API
 * User's Manual: Details of Atom Results Returned</a>
 * @author brianxiadong
 */
public class ArxivResult {

	private @Nullable String entryId; // URL in the format https://arxiv.org/abs/{id}

	private @Nullable LocalDateTime updated; // Last update time

	private @Nullable LocalDateTime published; // Initial publication time

	private @Nullable String title; // Title

	private @Nullable List<ArxivAuthor> authors; // List of authors

	private @Nullable String summary; // Abstract

	private @Nullable String comment; // Author comments (optional)

	private @Nullable String journalRef; // Journal reference (optional)

	private @Nullable String doi; // DOI link (optional)

	private @Nullable String primaryCategory; // Primary category

	private @Nullable List<String> categories; // All categories

	private @Nullable List<ArxivLink> links; // Related links (up to 3)

	private @Nullable String pdfUrl; // PDF link (if available)

	// Getters and Setters
	public String getEntryId() {
		return Objects.requireNonNull(entryId, "entryId must not be null");
	}

	public void setEntryId(String entryId) {
		this.entryId = entryId;
	}

	public LocalDateTime getUpdated() {
		return Objects.requireNonNull(updated, "updated must not be null");
	}

	public void setUpdated(LocalDateTime updated) {
		this.updated = updated;
	}

	public LocalDateTime getPublished() {
		return Objects.requireNonNull(published, "published must not be null");
	}

	public void setPublished(LocalDateTime published) {
		this.published = published;
	}

	public String getTitle() {
		return Objects.requireNonNull(title, "title must not be null");
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<ArxivAuthor> getAuthors() {
		return Objects.requireNonNull(authors, "authors must not be null");
	}

	public void setAuthors(List<ArxivAuthor> authors) {
		this.authors = authors;
	}

	public String getSummary() {
		return Objects.requireNonNull(summary, "summary must not be null");
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public @Nullable String getComment() {
		return comment;
	}

	public void setComment(@Nullable String comment) {
		this.comment = comment;
	}

	public @Nullable String getJournalRef() {
		return journalRef;
	}

	public void setJournalRef(@Nullable String journalRef) {
		this.journalRef = journalRef;
	}

	public @Nullable String getDoi() {
		return doi;
	}

	public void setDoi(@Nullable String doi) {
		this.doi = doi;
	}

	public @Nullable String getPrimaryCategory() {
		return primaryCategory;
	}

	public void setPrimaryCategory(@Nullable String primaryCategory) {
		this.primaryCategory = primaryCategory;
	}

	public List<String> getCategories() {
		return Objects.requireNonNull(categories, "categories must not be null");
	}

	public void setCategories(List<String> categories) {
		this.categories = categories;
	}

	public List<ArxivLink> getLinks() {
		return Objects.requireNonNull(links, "links must not be null");
	}

	public void setLinks(List<ArxivLink> links) {
		this.links = links;
		// Set PDF URL
		this.pdfUrl = links.stream()
			.filter(link -> "pdf".equals(link.getTitle()))
			.findFirst()
			.map(ArxivLink::getHref)
			.orElse(null);
	}

	public @Nullable String getPdfUrl() {
		return pdfUrl;
	}

	/**
	 * Get article's short ID Examples: - For URL "https://arxiv.org/abs/2107.05580v1"
	 * returns "2107.05580v1" - For URL "https://arxiv.org/abs/quant-ph/0201082v1" returns
	 * "quant-ph/0201082v1"
	 */
	public String getShortId() {
		return getEntryId().split("arxiv.org/abs/")[1];
	}

	/**
	 * Generate default filename
	 */
	public String getDefaultFilename(String extension) {
		String nonEmptyTitle = title != null && !title.isEmpty() ? title : "UNTITLED";
		return String.format("%s.%s.%s", getShortId().replace("/", "_"),
				Pattern.compile("[^\\w]").matcher(nonEmptyTitle).replaceAll("_"), extension);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		ArxivResult result = (ArxivResult) o;
		return Objects.equals(entryId, result.entryId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(entryId);
	}

	/**
	 * Inner class representing article author
	 */
	public static class ArxivAuthor {

		private String name;

		public ArxivAuthor(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;
			if (o == null || getClass() != o.getClass())
				return false;
			ArxivAuthor author = (ArxivAuthor) o;
			return Objects.equals(name, author.name);
		}

		@Override
		public int hashCode() {
			return Objects.hash(name);
		}

		@Override
		public String toString() {
			return name;
		}

	}

	/**
	 * Inner class representing related links
	 */
	public static class ArxivLink {

		private String href; // Link URL

		private String title; // Link title

		private String rel; // Relationship between link and Result

		private String contentType; // HTTP content type

		public ArxivLink(String href, String title, String rel, String contentType) {
			this.href = href;
			this.title = title;
			this.rel = rel;
			this.contentType = contentType;
		}

		public String getHref() {
			return href;
		}

		public void setHref(String href) {
			this.href = href;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getRel() {
			return rel;
		}

		public void setRel(String rel) {
			this.rel = rel;
		}

		public String getTextType() {
			return contentType;
		}

		public void setContentType(String contentType) {
			this.contentType = contentType;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;
			if (o == null || getClass() != o.getClass())
				return false;
			ArxivLink link = (ArxivLink) o;
			return Objects.equals(href, link.href);
		}

		@Override
		public int hashCode() {
			return Objects.hash(href);
		}

		@Override
		public String toString() {
			return href;
		}

	}

}
