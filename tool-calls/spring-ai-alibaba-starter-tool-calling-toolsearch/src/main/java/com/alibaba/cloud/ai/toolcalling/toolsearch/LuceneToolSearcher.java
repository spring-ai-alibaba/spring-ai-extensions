/*
 * Copyright 2025-2026 the original author or authors.
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
package com.alibaba.cloud.ai.toolcalling.toolsearch;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.definition.ToolDefinition;

import java.io.Closeable;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Lucene-based tool searcher implementation.
 */
public class LuceneToolSearcher implements ToolSearcher, Closeable {

	private static final Logger log = LoggerFactory.getLogger(LuceneToolSearcher.class);

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	private final Directory indexDirectory;

	private final Analyzer analyzer;

	private final Map<String, Float> fieldBoosts;

	private final List<String> indexFields;

	private final Object indexLock = new Object();

	private volatile IndexSearcher indexSearcher;

	private volatile DirectoryReader indexReader;

	private final Map<String, ToolCallback> toolCallbackMap = new ConcurrentHashMap<>();

	private final Map<String, String> schemaCache = new ConcurrentHashMap<>();

	public LuceneToolSearcher() {
		this(builder());
	}

	private LuceneToolSearcher(Builder builder) {
		this.indexDirectory = builder.indexDirectory != null ? builder.indexDirectory : new ByteBuffersDirectory();
		this.analyzer = builder.analyzer != null ? builder.analyzer : new StandardAnalyzer();
		this.fieldBoosts = new HashMap<>(builder.fieldBoosts);
		this.indexFields = new ArrayList<>(builder.indexFields);
	}

	public static Builder builder() {
		return new Builder();
	}

	@Override
	public void indexTools(List<ToolCallback> tools) {
		if (tools == null || tools.isEmpty()) {
			log.warn("No tools to index");
			return;
		}

		synchronized (indexLock) {
			try {
				// Close old reader
				closeReader();

				// Configure to overwrite mode to avoid duplicate indexing
				IndexWriterConfig config = new IndexWriterConfig(analyzer);
				config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
				IndexWriter indexWriter = new IndexWriter(indexDirectory, config);

				for (ToolCallback tool : tools) {
					ToolDefinition definition = tool.getToolDefinition();
					Document doc = new Document();

					for (String fieldName : indexFields) {
						String fieldValue = getFieldValue(definition, fieldName);
						if (fieldValue != null && !fieldValue.isEmpty()) {
							doc.add(new TextField(fieldName, fieldValue, Field.Store.YES));
						}
					}

					// Generate and cache schema
					String schema = generateSchema(tool);
					schemaCache.put(definition.name(), schema);
					doc.add(new StoredField("schema", schema));

					indexWriter.addDocument(doc);

					// Cache ToolCallback
					toolCallbackMap.put(definition.name(), tool);
				}

				indexWriter.commit();
				indexWriter.close();

				// Create new searcher
				indexReader = DirectoryReader.open(indexDirectory);
				this.indexSearcher = new IndexSearcher(indexReader);

				log.info("Successfully indexed {} tools with fields: {}", tools.size(), indexFields);
			}
			catch (IOException e) {
				throw new RuntimeException("Failed to index tools", e);
			}
		}
	}

	/**
	 * Close old DirectoryReader.
	 */
	private void closeReader() {
		if (indexReader != null) {
			try {
				indexReader.close();
				indexReader = null;
			}
			catch (IOException e) {
				log.warn("Failed to close old index reader", e);
			}
		}
	}

	/**
	 * Get the value of the specified field from ToolDefinition.
	 */
	private String getFieldValue(ToolDefinition definition, String fieldName) {
		switch (fieldName) {
			case "name":
				return definition.name();
			case "description":
				return definition.description();
			case "parameters":
				return definition.inputSchema();
			default:
				return null;
		}
	}

	@Override
	public List<ToolCallback> search(String query, int maxResults) {
		if (indexSearcher == null) {
			throw new IllegalStateException("Tools not indexed yet. Call indexTools() first.");
		}

		try {
			// Build multi-field query using configured fields and weights
			String[] fields = indexFields.toArray(new String[0]);
			MultiFieldQueryParser parser = new MultiFieldQueryParser(fields, analyzer, fieldBoosts);

			// Escape special characters
			String escapedQuery = QueryParser.escape(query);
			Query luceneQuery = parser.parse(escapedQuery);

			// Execute search
			TopDocs topDocs = indexSearcher.search(luceneQuery, maxResults);

			// Convert to ToolCallback
			List<ToolCallback> results = new ArrayList<>();
			for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
				Document doc = indexSearcher.doc(scoreDoc.doc);
				String toolName = doc.get("name");

				// Get ToolCallback from cache
				ToolCallback tool = toolCallbackMap.get(toolName);
				if (tool != null) {
					results.add(tool);
				}
			}

			log.debug("Search query '{}' found {} tools", query, results.size());
			return results;
		}
		catch (Exception e) {
			log.error("Failed to search tools for query: {}", query, e);
			return Collections.emptyList();
		}
	}

	@Override
	public String getToolSchema(ToolCallback tool) {
		String toolName = tool.getToolDefinition().name();
		return schemaCache.computeIfAbsent(toolName, k -> generateSchema(tool));
	}

	/**
	 * Generate JSON Schema for the tool.
	 */
	private String generateSchema(ToolCallback tool) {
		try {
			ToolDefinition definition = tool.getToolDefinition();

			// Build JSON Schema
			Map<String, Object> schema = new HashMap<>();
			schema.put("type", "function");

			Map<String, Object> function = new HashMap<>();
			function.put("name", definition.name());
			function.put("description", definition.description());

			// Try to parse parameter Schema
			String inputTypeSchema = definition.inputSchema();
			if (inputTypeSchema != null && !inputTypeSchema.isEmpty()) {
				try {
					Object parameters = OBJECT_MAPPER.readValue(inputTypeSchema, Object.class);
					function.put("parameters", parameters);
				}
				catch (Exception e) {
					log.warn("Failed to parse input schema for tool {}: {}", definition.name(), e.getMessage());
					function.put("parameters", Collections.emptyMap());
				}
			}
			else {
				function.put("parameters", Collections.emptyMap());
			}

			schema.put("function", function);

			return OBJECT_MAPPER.writeValueAsString(schema);
		}
		catch (Exception e) {
			log.error("Failed to generate schema for tool", e);
			return "{}";
		}
	}

	@Override
	public void close() throws IOException {
		synchronized (indexLock) {
			closeReader();
			if (indexDirectory != null) {
				indexDirectory.close();
			}
			toolCallbackMap.clear();
			schemaCache.clear();
		}
	}

	public static class Builder {

		private Directory indexDirectory;

		private Analyzer analyzer;

		private final Map<String, Float> fieldBoosts = new HashMap<>();

		private final List<String> indexFields = new ArrayList<>();

		public Builder() {
			indexFields.add("name");
			indexFields.add("description");
			indexFields.add("parameters");

			fieldBoosts.put("name", ToolSearchConstants.DEFAULT_NAME_BOOST);
			fieldBoosts.put("description", ToolSearchConstants.DEFAULT_DESCRIPTION_BOOST);
			fieldBoosts.put("parameters", ToolSearchConstants.DEFAULT_PARAMETERS_BOOST);
		}

		public Builder indexDirectory(Directory indexDirectory) {
			this.indexDirectory = indexDirectory;
			return this;
		}

		public Builder analyzer(Analyzer analyzer) {
			this.analyzer = analyzer;
			return this;
		}
		public Builder fieldBoost(String fieldName, float boost) {
			this.fieldBoosts.put(fieldName, boost);
			return this;
		}

		public Builder fieldBoosts(Map<String, Float> boosts) {
			this.fieldBoosts.putAll(boosts);
			return this;
		}

		public Builder addIndexField(String fieldName) {
			if (!this.indexFields.contains(fieldName)) {
				this.indexFields.add(fieldName);
			}
			return this;
		}

		public Builder addIndexField(String fieldName, float boost) {
			addIndexField(fieldName);
			fieldBoost(fieldName, boost);
			return this;
		}

		public Builder clearIndexFields() {
			this.indexFields.clear();
			this.fieldBoosts.clear();
			return this;
		}

		public LuceneToolSearcher build() {
			if (indexFields.isEmpty()) {
				throw new IllegalStateException("At least one index field must be configured");
			}
			return new LuceneToolSearcher(this);
		}

	}

}
