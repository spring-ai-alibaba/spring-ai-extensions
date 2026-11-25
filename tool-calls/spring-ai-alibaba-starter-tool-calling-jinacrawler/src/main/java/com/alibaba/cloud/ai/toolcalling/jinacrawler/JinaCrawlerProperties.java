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
package com.alibaba.cloud.ai.toolcalling.jinacrawler;

import com.alibaba.cloud.ai.toolcalling.common.CommonToolCallProperties;
import org.jspecify.annotations.Nullable;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.MediaType;

@ConfigurationProperties(prefix = JinaCrawlerConstants.CONFIG_PREFIX)
public class JinaCrawlerProperties extends CommonToolCallProperties {

	public JinaCrawlerProperties() {
		super(JinaCrawlerConstants.BASE_URL);
		this.setPropertiesFromEnv(JinaCrawlerConstants.API_KEY_ENV, null, null, null);
	}

	private MediaType accept = MediaType.asMediaType(MediaType.APPLICATION_JSON);

	private @Nullable String targetSelector;

	private @Nullable String waitForSelector;

	private @Nullable String removeSelector;

	private @Nullable String retainImages;

	private @Nullable Boolean withLinksSummary;

	private @Nullable Boolean withImagesSummary;

	private @Nullable String setCookie;

	private @Nullable Boolean withGeneratedAlt;

	private @Nullable String proxyUrl;

	private @Nullable Boolean noCache;

	private @Nullable String locale;

	private @Nullable Boolean withIframe;

	private @Nullable Boolean withShadowDom;

	public MediaType getAccept() {
		return accept;
	}

	public void setAccept(MediaType accept) {
		this.accept = accept;
	}

	public @Nullable String getTargetSelector() {
		return targetSelector;
	}

	public void setTargetSelector(@Nullable String targetSelector) {
		this.targetSelector = targetSelector;
	}

	public @Nullable String getWaitForSelector() {
		return waitForSelector;
	}

	public void setWaitForSelector(@Nullable String waitForSelector) {
		this.waitForSelector = waitForSelector;
	}

	public @Nullable String getRemoveSelector() {
		return removeSelector;
	}

	public void setRemoveSelector(@Nullable String removeSelector) {
		this.removeSelector = removeSelector;
	}

	public @Nullable String getRetainImages() {
		return retainImages;
	}

	public void setRetainImages(@Nullable String retainImages) {
		this.retainImages = retainImages;
	}

	public @Nullable Boolean getWithLinksSummary() {
		return withLinksSummary;
	}

	public void setWithLinksSummary(@Nullable Boolean withLinksSummary) {
		this.withLinksSummary = withLinksSummary;
	}

	public @Nullable Boolean getWithImagesSummary() {
		return withImagesSummary;
	}

	public void setWithImagesSummary(@Nullable Boolean withImagesSummary) {
		this.withImagesSummary = withImagesSummary;
	}

	public @Nullable String getSetCookie() {
		return setCookie;
	}

	public void setSetCookie(@Nullable String setCookie) {
		this.setCookie = setCookie;
	}

	public @Nullable Boolean getWithGeneratedAlt() {
		return withGeneratedAlt;
	}

	public void setWithGeneratedAlt(@Nullable Boolean withGeneratedAlt) {
		this.withGeneratedAlt = withGeneratedAlt;
	}

	public @Nullable String getProxyUrl() {
		return proxyUrl;
	}

	public void setProxyUrl(@Nullable String proxyUrl) {
		this.proxyUrl = proxyUrl;
	}

	public @Nullable Boolean getNoCache() {
		return noCache;
	}

	public void setNoCache(@Nullable Boolean noCache) {
		this.noCache = noCache;
	}

	public @Nullable String getLocale() {
		return locale;
	}

	public void setLocale(@Nullable String locale) {
		this.locale = locale;
	}

	public @Nullable Boolean getWithIframe() {
		return withIframe;
	}

	public void setWithIframe(@Nullable Boolean withIframe) {
		this.withIframe = withIframe;
	}

	public @Nullable Boolean getWithShadowDom() {
		return withShadowDom;
	}

	public void setWithShadowDom(@Nullable Boolean withShadowDom) {
		this.withShadowDom = withShadowDom;
	}

	@Override
	public String toString() {
		return "JinaCrawlerProperties{" + "api-key='" + "**********" + '\'' + ", enabled=" + super.isEnabled()
				+ ", targetSelector='" + targetSelector + '\'' + ", waitForSelector='" + waitForSelector + '\''
				+ ", removeSelector='" + removeSelector + '\'' + ", retainImages='" + retainImages + '\''
				+ ", withLinksSummary=" + withLinksSummary + ", withImagesSummary=" + withImagesSummary
				+ ", setCookie='" + setCookie + '\'' + ", withGeneratedAlt=" + withGeneratedAlt + ", proxyUrl='"
				+ proxyUrl + '\'' + ", noCache=" + noCache + ", locale='" + locale + '\'' + ", withIframe=" + withIframe
				+ ", withShadowDom=" + withShadowDom + '}';
	}

}
