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
package com.alibaba.cloud.ai.toolcalling.baidusearch;

import com.alibaba.cloud.ai.toolcalling.common.JsonParseTool;
import com.alibaba.cloud.ai.toolcalling.common.WebClientTool;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.function.FunctionToolCallback;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

import org.springframework.http.HttpHeaders;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import static com.alibaba.cloud.ai.toolcalling.common.CommonToolCallConstants.DEFAULT_USER_AGENTS;

/**
 * @author KrakenZJC
 **/
@Configuration
@EnableConfigurationProperties({ BaiduSearchProperties.class, BaiduAiSearchProperties.class })
@ConditionalOnProperty(prefix = BaiduSearchConstants.CONFIG_PREFIX, name = "enabled", havingValue = "true",
		matchIfMissing = true)
public class BaiduSearchAutoConfiguration {

	@Bean(name = BaiduSearchConstants.TOOL_NAME)
	@ConditionalOnMissingBean
	@Description("Use baidu search engine to query for the latest news.")
	public BaiduSearchService baiduSearch(JsonParseTool jsonParseTool, BaiduSearchProperties properties) {
		Consumer<HttpHeaders> consumer = headers -> {
			headers.add(HttpHeaders.USER_AGENT,
					DEFAULT_USER_AGENTS[ThreadLocalRandom.current().nextInt(DEFAULT_USER_AGENTS.length)]);
			headers.add(HttpHeaders.REFERER, "https://www.baidu.com/");
			headers.add(HttpHeaders.CONNECTION, "keep-alive");
			headers.add(HttpHeaders.ACCEPT_LANGUAGE, "zh-CN,zh;q=0.9");
			headers.add(HttpHeaders.ACCEPT, "text/html,application/xhtml+xml,application/xml");
		};
		return new BaiduSearchService(jsonParseTool, properties,
				WebClientTool.builder(jsonParseTool, properties).httpHeadersConsumer(consumer).build());
	}

	@Bean(name = BaiduSearchConstants.TOOL_NAME_AI)
	@ConditionalOnMissingBean
	@Description("Use baidu ai search engine to query information.")
	@ConditionalOnProperty(prefix = BaiduSearchConstants.CONFIG_PREFIX_AI, name = "enabled", havingValue = "true")
	public BaiduAiSearchService baiduAiSearch(JsonParseTool jsonParseTool, BaiduAiSearchProperties properties) {
		Consumer<HttpHeaders> consumer = headers -> {
			headers.add("Content-Type", "application/json");
			headers.add("Authorization", "Bearer " + properties.getApiKey());
		};
		return new BaiduAiSearchService(
				WebClientTool.builder(jsonParseTool, properties).httpHeadersConsumer(consumer).build(), jsonParseTool,
				properties);
	}

	@Bean(name = "baiduSearchToolCallback")
	@ConditionalOnMissingBean(name = "baiduSearchToolCallback")
	public ToolCallback baiduSearchToolCallback(BaiduSearchService baiduSearch) {
		return FunctionToolCallback.builder(BaiduSearchConstants.TOOL_NAME, baiduSearch)
			.description("Use baidu search engine to query for the latest news.")
			.inputType(BaiduSearchService.Request.class)
			.build();
	}

	@Bean(name = "baiduAiSearchToolCallback")
	@ConditionalOnMissingBean(name = "baiduAiSearchToolCallback")
	@ConditionalOnProperty(prefix = BaiduSearchConstants.CONFIG_PREFIX_AI, name = "enabled", havingValue = "true")
	public ToolCallback baiduAiSearchToolCallback(BaiduAiSearchService baiduAiSearch) {
		return FunctionToolCallback.builder(BaiduSearchConstants.TOOL_NAME_AI, baiduAiSearch)
			.description("Use baidu ai search engine to query information.")
			.inputType(BaiduAiSearchService.Request.class)
			.build();
	}

}
