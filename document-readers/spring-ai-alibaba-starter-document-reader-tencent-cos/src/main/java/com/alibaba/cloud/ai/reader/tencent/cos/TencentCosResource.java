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
package com.alibaba.cloud.ai.reader.tencent.cos;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.COSCredentialsProvider;
import com.qcloud.cos.model.COSObject;
import com.qcloud.cos.model.COSObjectSummary;
import com.qcloud.cos.model.GetObjectRequest;
import com.qcloud.cos.model.ListObjectsRequest;
import com.qcloud.cos.model.ObjectListing;
import com.qcloud.cos.region.Region;
import org.jspecify.annotations.Nullable;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author HeYQ
 * @since 2024-11-27 21:44
 */
public class TencentCosResource implements Resource {

	public static final String SOURCE = "source";

	private final InputStream inputStream;

	private final String bucket;

	private final String key;

	private final COSClient cosClient;

	public TencentCosResource(COSClient cosClient, String bucket, String key) {
		this.cosClient = cosClient;
		this.bucket = bucket;
		this.key = key;
		GetObjectRequest getObjectRequest = new GetObjectRequest(bucket, key);
		COSObject cosObject = cosClient.getObject(getObjectRequest);
		this.inputStream = cosObject.getObjectContent();
	}

	@Override
	public boolean exists() {
		return false;
	}

	@Override
	public URL getURL() throws IOException {
		throw new UnsupportedOperationException("TencentCosResource does not expose a URL");
	}

	@Override
	public URI getURI() throws IOException {
		throw new UnsupportedOperationException("TencentCosResource does not expose a URI");
	}

	@Override
	public File getFile() throws IOException {
		throw new UnsupportedOperationException("TencentCosResource is not backed by a local file");
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
		throw new UnsupportedOperationException("TencentCosResource does not support relative resources");
	}

	@Override
	public String getFilename() {
		int lastSlash = key.lastIndexOf('/');
		return lastSlash >= 0 ? key.substring(lastSlash + 1) : key;
	}

	@Override
	public String getDescription() {
		return "Tencent COS resource [bucket=" + bucket + ", key=" + key + "]";
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return inputStream;
	}

	public String getBucket() {
		return bucket;
	}

	public String getKey() {
		return key;
	}

	public COSClient getCosClient() {
		return cosClient;
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {

		private @Nullable Region region;

		private @Nullable TencentCredentials tencentCredentials;

		private @Nullable String secretId;

		private @Nullable String secretKey;

		private @Nullable String sessionToken;

		private @Nullable String bucket;

		private @Nullable String key;

		private @Nullable String prefix;

		private @Nullable COSClient cosClient;

		/**
		 * Set the Tencent region.
		 * @param region The Tencent region.
		 * @return The builder instance.
		 */
		public Builder region(@Nullable String region) {
			this.region = region != null ? new Region(Region.formatRegion(region)) : null;
			return this;
		}

		/**
		 * Set the Tencent region.
		 * @param region The Tencent region.
		 * @return The builder instance.
		 */
		public Builder region(@Nullable Region region) {
			this.region = region;
			return this;
		}

		/**
		 * Set the Tencent credentials. If not set, it will use the default credentials.
		 * @param tencentCredentials The Tencent credentials.
		 * @return The builder instance.
		 */
		public Builder tencentCredentials(@Nullable TencentCredentials tencentCredentials) {
			this.tencentCredentials = tencentCredentials;
			return this;
		}

		public Builder secretId(@Nullable String secretId) {
			this.secretId = secretId;
			return this;
		}

		public Builder secretKey(@Nullable String secretKey) {
			this.secretKey = secretKey;
			return this;
		}

		public Builder sessionToken(@Nullable String sessionToken) {
			this.sessionToken = sessionToken;
			return this;
		}

		public Builder bucket(@Nullable String bucket) {
			this.bucket = bucket;
			return this;
		}

		public Builder key(@Nullable String key) {
			this.key = key;
			return this;
		}

		public Builder prefix(@Nullable String prefix) {
			this.prefix = prefix;
			return this;
		}

		public Builder cosClient(@Nullable COSClient cosClient) {
			this.cosClient = cosClient;
			return this;
		}

		public TencentCosResource build() {
			String resolvedBucket = Objects.requireNonNull(bucket, "Bucket must not be null");
			String resolvedKey = Objects.requireNonNull(key, "Key must not be null");
			judgeAndCreateCosClient();
			return new TencentCosResource(Objects.requireNonNull(this.cosClient), resolvedBucket, resolvedKey);
		}

		public List<TencentCosResource> buildBatch() {
			String resolvedBucket = Objects.requireNonNull(bucket, "Bucket must not be null");
			judgeAndCreateCosClient();
			ListObjectsRequest listObjectsRequest = new ListObjectsRequest()
				.withBucketName(resolvedBucket)
				.withPrefix(prefix);
			ObjectListing objectListing = Objects.requireNonNull(this.cosClient).listObjects(listObjectsRequest);
			List<COSObjectSummary> filteredObjects = objectListing.getObjectSummaries()
				.stream()
				.filter(object -> !object.getKey().endsWith("/") && object.getSize() > 0)
				.toList();
			List<TencentCosResource> tencentCosResourceList = new ArrayList<>();
			for (COSObjectSummary object : filteredObjects) {
				tencentCosResourceList
					.add(new TencentCosResource(Objects.requireNonNull(this.cosClient), resolvedBucket, object.getKey()));
			}
			return tencentCosResourceList;
		}

		private COSCredentialsProvider createCredentialsProvider() {
			if (tencentCredentials != null) {
				return tencentCredentials.toCredentialsProvider();
			}

			throw new IllegalArgumentException("Tencent credentials are required.");
		}

		private COSClient createCosClient(COSCredentialsProvider cosCredentialsProvider) {
			ClientConfig clientConfig = new ClientConfig(Objects.requireNonNull(region, "Region must not be null"));
			return new COSClient(cosCredentialsProvider, clientConfig);
		}

		private void judgeAndCreateCosClient() {
			if (Objects.isNull(this.cosClient)) {
				if (Objects.isNull(tencentCredentials)) {
					this.tencentCredentials = new TencentCredentials(Objects.requireNonNull(secretId, "SecretId is required"),
							Objects.requireNonNull(secretKey, "SecretKey is required"), sessionToken);
				}
				Assert.notNull(region, "Region must not be null");
				COSCredentialsProvider credentialsProvider = createCredentialsProvider();
				this.cosClient = createCosClient(credentialsProvider);
			}
		}

	}

}
