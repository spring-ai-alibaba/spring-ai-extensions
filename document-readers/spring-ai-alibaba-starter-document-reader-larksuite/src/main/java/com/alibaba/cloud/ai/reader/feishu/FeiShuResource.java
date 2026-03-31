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
package com.alibaba.cloud.ai.reader.feishu;

import com.lark.oapi.Client;
import com.lark.oapi.core.enums.BaseUrlEnum;
import org.jspecify.annotations.Nullable;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.Objects;

public class FeiShuResource implements Resource {

	public static final String SOURCE = "source";

	public static final String FEISHU_PROPERTIES_PREFIX = "spring.ai.alibaba.plugin.feishu";

	private final String appId;

	private final String appSecret;

	public String getAppId() {
		return appId;
	}

	public String getAppSecret() {
		return appSecret;
	}

	public FeiShuResource(String appId, String appSecret) {
		this.appId = appId;
		this.appSecret = appSecret;
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {

		private @Nullable String appId;

		private @Nullable String appSecret;

		public Builder appId(@Nullable String appId) {
			this.appId = appId;
			return this;
		}

		public Builder appSecret(@Nullable String appSecret) {
			this.appSecret = appSecret;
			return this;
		}

		public FeiShuResource build() {
			return new FeiShuResource(Objects.requireNonNull(appId, "FeiShu AppId must not be empty"),
					Objects.requireNonNull(appSecret, "FeiShu AppSecret must not be empty"));
		}

	}

	public Client buildDefaultFeiShuClient() {
		return Client.newBuilder(this.appId, this.appSecret)
			.openBaseUrl(BaseUrlEnum.FeiShu)
			.logReqAtDebug(true)
			.build();
	}

	@Override
	public boolean exists() {
		return false;
	}

	@Override
	public URL getURL() throws IOException {
		throw new UnsupportedOperationException("FeiShuResource does not expose a URL");
	}

	@Override
	public URI getURI() throws IOException {
		throw new UnsupportedOperationException("FeiShuResource does not expose a URI");
	}

	@Override
	public File getFile() throws IOException {
		throw new UnsupportedOperationException("FeiShuResource is not backed by a local file");
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
		throw new UnsupportedOperationException("FeiShuResource does not support relative resources");
	}

	@Override
	public String getFilename() {
		return appId;
	}

	@Override
	public String getDescription() {
		return "FeiShu resource [appId=" + appId + "]";
	}

	@Override
	public InputStream getInputStream() throws IOException {
		throw new UnsupportedOperationException("FeiShuResource does not expose a raw input stream");
	}

}
