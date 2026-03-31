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

import org.jspecify.annotations.Nullable;
import org.springframework.util.Assert;

import java.util.Objects;

/**
 * Bilibili authentication credentials for accessing video content.
 * These credentials can be reused across multiple video requests.
 *
 * @author zshs000
 */
public class BilibiliCredentials {

	private final String sessdata;

	private final String biliJct;

	private final @Nullable String buvid3;

	private BilibiliCredentials(Builder builder) {
		this.sessdata = Objects.requireNonNull(builder.sessdata, "SESSDATA must not be null");
		this.biliJct = Objects.requireNonNull(builder.biliJct, "bili_jct must not be null");
		this.buvid3 = builder.buvid3;
	}

	public String getSessdata() {
		return sessdata;
	}

	public String getBiliJct() {
		return biliJct;
	}

	public @Nullable String getBuvid3() {
		return buvid3;
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {

			private @Nullable String sessdata;

			private @Nullable String biliJct;

			private @Nullable String buvid3;

			public Builder sessdata(@Nullable String sessdata) {
				this.sessdata = sessdata;
				return this;
			}

			public Builder biliJct(@Nullable String biliJct) {
				this.biliJct = biliJct;
				return this;
			}

			public Builder buvid3(@Nullable String buvid3) {
				this.buvid3 = buvid3;
				return this;
			}

		public BilibiliCredentials build() {
			Assert.hasText(sessdata, "SESSDATA must not be empty");
			Assert.hasText(biliJct, "bili_jct must not be empty");
			return new BilibiliCredentials(this);
		}

	}

}
