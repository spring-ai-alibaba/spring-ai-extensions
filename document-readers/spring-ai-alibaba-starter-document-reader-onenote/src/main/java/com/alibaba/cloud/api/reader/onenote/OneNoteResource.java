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
package com.alibaba.cloud.api.reader.onenote;

import org.jspecify.annotations.Nullable;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.Objects;

/**
 * @author sparkle6979l
 */
public class OneNoteResource implements Resource {

	public static final String SOURCE = "source";

	public enum ResourceType {

		NOTEBOOK, SECTION, PAGE

	}

	private final ResourceType resourceType;

	private final String resourceId;

	private final URI uri;

	public ResourceType getResourceType() {
		return resourceType;
	}

	public String getResourceId() {
		return resourceId;
	}

	public OneNoteResource(String resourceId, ResourceType resourceType) {
		Assert.hasText(resourceId, "ResourceId must not be empty");
		Assert.notNull(resourceType, "ResourceType must not be null");

		this.resourceId = resourceId;
		this.resourceType = resourceType;
		this.uri = URI.create(String.format("onenote://%s/%s", resourceType.name().toLowerCase(), resourceId));
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {

		private @Nullable ResourceType resourceType;

		private @Nullable String resourceId;

		public Builder resourceType(@Nullable ResourceType resourceType) {
			this.resourceType = resourceType;
			return this;
		}

		public Builder resourceId(@Nullable String resourceId) {
			this.resourceId = resourceId;
			return this;
		}

		public OneNoteResource build() {
			String resolvedResourceId = Objects.requireNonNull(resourceId, "ResourceId must not be empty");
			Assert.hasText(resolvedResourceId, "ResourceId must not be empty");
			return new OneNoteResource(resolvedResourceId,
					Objects.requireNonNull(resourceType, "ResourceType must not be null"));
		}

	}

	@Override
	public boolean exists() {
		return false;
	}

	@Override
	public URL getURL() throws IOException {
		throw new UnsupportedOperationException("OneNoteResource does not expose a URL");
	}

	@Override
	public URI getURI() throws IOException {
		return uri;
	}

	@Override
	public File getFile() throws IOException {
		throw new UnsupportedOperationException("OneNoteResource is not backed by a local file");
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
		throw new UnsupportedOperationException("OneNoteResource does not support relative resources");
	}

	@Override
	public String getFilename() {
		return resourceId;
	}

	@Override
	public String getDescription() {
		return "OneNote resource [type=" + resourceType + ", id=" + resourceId + "]";
	}

	@Override
	public InputStream getInputStream() throws IOException {
		throw new UnsupportedOperationException("OneNoteResource does not expose a raw input stream");
	}

}
