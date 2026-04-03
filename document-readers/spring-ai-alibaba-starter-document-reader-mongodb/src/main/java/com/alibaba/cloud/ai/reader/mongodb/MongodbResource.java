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
package com.alibaba.cloud.ai.reader.mongodb;

import org.jspecify.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

import org.springframework.core.io.Resource;

/**
 * MongoDB Document Reader Configuration Properties Class Used to configure MongoDB
 * connection, document processing and performance related parameters
 *
 * @author Yongtao Tan
 * @version 1.0.0
 */
public class MongodbResource implements Resource {

	/**
	 * MongoDB Connection URI Format:
	 * mongodb://[username:password@]host1[:port1][,host2[:port2],...][/database][?options]
	 * Default: mongodb://localhost:27017
	 */
	private @Nullable String uri;

	/**
	 * MongoDB Username Optional, used for authentication
	 */
	private @Nullable String username;

	/**
	 * MongoDB Password Optional, used for authentication
	 */
	private @Nullable String password;

	/**
	 * MongoDB Database Name Required, specifies the database to connect to
	 */
	private @Nullable String database;

	/**
	 * MongoDB Collection Name Required, specifies the collection to read from
	 */
	private @Nullable String collection;

	/**
	 * MongoDB Query Condition (JSON format) Optional, used to filter documents to be read
	 * Example: {"status": "active", "type": "article"}
	 */
	private @Nullable String query;

	/**
	 * Document Chunk Size (in characters) Used to split large documents into smaller
	 * chunks for processing Default: 1000 characters
	 */
	private int chunkSize = 1000;

	/**
	 * Chunk Overlap Size (in characters) Number of overlapping characters between
	 * adjacent chunks, used to maintain context continuity Default: 200 characters
	 */
	private int overlap = 200;

	/**
	 * Whether to Enable Vectorization If true, will process vector fields in documents
	 * Default: false
	 */
	private boolean enableVectorization = false;

	/**
	 * Vector Dimensions Dimension size for document vectorization Default: 1536 (suitable
	 * for OpenAI's text-embedding-ada-002 model)
	 */
	private int vectorDimensions = 1536;

	/**
	 * Vector Field Name Field name storing vectors in MongoDB documents Default: "vector"
	 */
	private String vectorField = "vector";

	/**
	 * Batch Size Number of documents to read from MongoDB in each batch Default: 100
	 */
	private int batchSize = 100;

	/**
	 * MongoDB Connection Pool Size Default: 10
	 */
	private int poolSize = 5;

	/**
	 * MongoDB Connection Timeout (milliseconds) Default: 3000ms (3 seconds)
	 */
	private int connectTimeout = 3000;

	// Getters and Setters

	public @Nullable String getUri() {
		return uri;
	}

	public void setUri(@Nullable String uri) {
		this.uri = uri;
	}

	public @Nullable String getDatabase() {
		return database;
	}

	public void setDatabase(@Nullable String database) {
		this.database = database;
	}

	public @Nullable String getCollection() {
		return collection;
	}

	public void setCollection(@Nullable String collection) {
		this.collection = collection;
	}

	public @Nullable String getQuery() {
		return query;
	}

	public void setQuery(@Nullable String query) {
		this.query = query;
	}

	public int getChunkSize() {
		return chunkSize;
	}

	public void setChunkSize(int chunkSize) {
		this.chunkSize = chunkSize;
	}

	public int getOverlap() {
		return overlap;
	}

	public void setOverlap(int overlap) {
		this.overlap = overlap;
	}

	public boolean isEnableVectorization() {
		return enableVectorization;
	}

	public void setEnableVectorization(boolean enableVectorization) {
		this.enableVectorization = enableVectorization;
	}

	public int getVectorDimensions() {
		return vectorDimensions;
	}

	public void setVectorDimensions(int vectorDimensions) {
		this.vectorDimensions = vectorDimensions;
	}

	public String getVectorField() {
		return vectorField;
	}

	public void setVectorField(String vectorField) {
		this.vectorField = vectorField;
	}

	public int getBatchSize() {
		return batchSize;
	}

	public void setBatchSize(int batchSize) {
		this.batchSize = batchSize;
	}

	public int getPoolSize() {
		return poolSize;
	}

	public void setPoolSize(int poolSize) {
		this.poolSize = poolSize;
	}

	public int getConnectTimeout() {
		return connectTimeout;
	}

	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	@Override
	public boolean exists() {
		return false;
	}

	@Override
	public URL getURL() throws IOException {
		throw new UnsupportedOperationException("MongodbResource does not expose a URL");
	}

	@Override
	public URI getURI() throws IOException {
		throw new UnsupportedOperationException("MongodbResource does not expose a URI");
	}

	@Override
	public File getFile() throws IOException {
		throw new UnsupportedOperationException("MongodbResource is not backed by a local file");
	}

	@Override
	public long contentLength() throws IOException {
		return 0;
	}

	@Override
	public long lastModified() throws IOException {
		return 0;
	}

	@Override
	public Resource createRelative(String relativePath) throws IOException {
		throw new UnsupportedOperationException("MongodbResource does not support relative resources");
	}

	@Override
	public String getFilename() {
		return collection != null ? collection : "mongodb";
	}

	@Override
	public String getDescription() {
		return "MongoDB resource";
	}

	@Override
	public InputStream getInputStream() {
		throw new UnsupportedOperationException("MongodbResource does not expose a raw input stream");
	}

	public void setUsername(@Nullable String username) {
		this.username = username;
	}

	public void setPassword(@Nullable String password) {
		this.password = password;
	}

	public @Nullable String getUsername() {
		return username;
	}

	public @Nullable String getPassword() {
		return password;
	}

}
