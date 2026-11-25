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
package com.alibaba.cloud.ai.toolcalling.yuque;

import com.alibaba.cloud.ai.toolcalling.common.JsonParseTool;
import com.alibaba.cloud.ai.toolcalling.common.WebClientTool;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.function.FunctionToolCallback;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

/**
 * @author 北极星
 */
@Configuration
@ConditionalOnProperty(prefix = YuqueConstants.CONFIG_PREFIX, name = "enabled", havingValue = "true",
		matchIfMissing = true)
@ConditionalOnClass
@EnableConfigurationProperties(YuqueProperties.class)
public class YuqueAutoConfiguration {

	@Bean(name = YuqueConstants.CREATE_BOOK_TOOL_NAME)
	@ConditionalOnMissingBean
	@Description("Use yuque api to invoke a http request to create a book.")
	public YuqueCreateBookService createYuqueBook(YuqueProperties yuqueProperties, JsonParseTool jsonParseTool) {
		return new YuqueCreateBookService(WebClientTool.builder(jsonParseTool, yuqueProperties)
			.httpHeadersConsumer(headers -> headers.set("X-Auth-Token", yuqueProperties.getToken()))
			.build(), jsonParseTool);
	}

	@Bean(name = YuqueConstants.QUERY_BOOK_TOOL_NAME)
	@ConditionalOnMissingBean
	@Description("Use yuque api to invoke a http request to query a book.")
	public YuqueQueryBookService queryYuqueBook(YuqueProperties yuqueProperties, JsonParseTool jsonParseTool) {
		return new YuqueQueryBookService(WebClientTool.builder(jsonParseTool, yuqueProperties)
			.httpHeadersConsumer(headers -> headers.set("X-Auth-Token", yuqueProperties.getToken()))
			.build(), jsonParseTool);
	}

	@Bean(name = YuqueConstants.UPDATE_BOOK_TOOL_NAME)
	@ConditionalOnMissingBean
	@Description("Use yuque api to invoke a http request to update a book.")
	public YuqueUpdateBookService updateYuqueBook(YuqueProperties yuqueProperties, JsonParseTool jsonParseTool) {
		return new YuqueUpdateBookService(WebClientTool.builder(jsonParseTool, yuqueProperties)
			.httpHeadersConsumer(headers -> headers.set("X-Auth-Token", yuqueProperties.getToken()))
			.build(), jsonParseTool);
	}

	@Bean(name = YuqueConstants.DELETE_BOOK_TOOL_NAME)
	@ConditionalOnMissingBean
	@Description("Use yuque api to invoke a http request to delete a book.")
	public YuqueDeleteBookService deleteYuqueBook(YuqueProperties yuqueProperties, JsonParseTool jsonParseTool) {
		return new YuqueDeleteBookService(WebClientTool.builder(jsonParseTool, yuqueProperties)
			.httpHeadersConsumer(headers -> headers.set("X-Auth-Token", yuqueProperties.getToken()))
			.build(), jsonParseTool);
	}

	@Bean(name = YuqueConstants.CREATE_DOC_TOOL_NAME)
	@ConditionalOnMissingBean
	@Description("Use yuque api to invoke a http request to create a doc.")
	public YuqueCreateDocService createYuqueDoc(YuqueProperties yuqueProperties, JsonParseTool jsonParseTool) {
		return new YuqueCreateDocService(WebClientTool.builder(jsonParseTool, yuqueProperties)
			.httpHeadersConsumer(headers -> headers.set("X-Auth-Token", yuqueProperties.getToken()))
			.build(), jsonParseTool);
	}

	@Bean(name = YuqueConstants.QUERY_DOC_TOOL_NAME)
	@ConditionalOnMissingBean
	@Description("Use yuque api to invoke a http request to query a doc.")
	public YuqueQueryDocService queryYuqueDoc(YuqueProperties yuqueProperties, JsonParseTool jsonParseTool) {
		return new YuqueQueryDocService(WebClientTool.builder(jsonParseTool, yuqueProperties)
			.httpHeadersConsumer(headers -> headers.set("X-Auth-Token", yuqueProperties.getToken()))
			.build(), jsonParseTool);
	}

	@Bean(name = YuqueConstants.UPDATE_DOC_TOOL_NAME)
	@ConditionalOnMissingBean
	@Description("Use yuque api to invoke a http request to update your doc.")
	public YuqueUpdateDocService updateDocService(YuqueProperties yuqueProperties, JsonParseTool jsonParseTool) {
		return new YuqueUpdateDocService(WebClientTool.builder(jsonParseTool, yuqueProperties)
			.httpHeadersConsumer(headers -> headers.set("X-Auth-Token", yuqueProperties.getToken()))
			.build(), jsonParseTool);
	}

	@Bean(name = YuqueConstants.DELETE_DOC_TOOL_NAME)
	@ConditionalOnMissingBean
	@Description("Use yuque api to invoke a http request to delete your doc.")
	public YuqueDeleteDocService deleteDocService(YuqueProperties yuqueProperties, JsonParseTool jsonParseTool) {
		return new YuqueDeleteDocService(WebClientTool.builder(jsonParseTool, yuqueProperties)
			.httpHeadersConsumer(headers -> headers.set("X-Auth-Token", yuqueProperties.getToken()))
			.build(), jsonParseTool);
	}

	@Bean(name = "createYuqueBookToolCallback")
	@ConditionalOnMissingBean(name = "createYuqueBookToolCallback")
	public ToolCallback createYuqueBookToolCallback(YuqueCreateBookService createYuqueBook) {
		return FunctionToolCallback.builder(YuqueConstants.CREATE_BOOK_TOOL_NAME, createYuqueBook)
			.description("Use yuque api to invoke a http request to create a book.")
			.inputType(YuqueCreateBookService.CreateBookRequest.class)
			.build();
	}

	@Bean(name = "queryYuqueBookToolCallback")
	@ConditionalOnMissingBean(name = "queryYuqueBookToolCallback")
	public ToolCallback queryYuqueBookToolCallback(YuqueQueryBookService queryYuqueBook) {
		return FunctionToolCallback.builder(YuqueConstants.QUERY_BOOK_TOOL_NAME, queryYuqueBook)
			.description("Use yuque api to invoke a http request to query a book.")
			.inputType(YuqueQueryBookService.QueryBookRequest.class)
			.build();
	}

	@Bean(name = "updateYuqueBookToolCallback")
	@ConditionalOnMissingBean(name = "updateYuqueBookToolCallback")
	public ToolCallback updateYuqueBookToolCallback(YuqueUpdateBookService updateYuqueBook) {
		return FunctionToolCallback.builder(YuqueConstants.UPDATE_BOOK_TOOL_NAME, updateYuqueBook)
			.description("Use yuque api to invoke a http request to update a book.")
			.inputType(YuqueUpdateBookService.updateBookRequest.class)
			.build();
	}

	@Bean(name = "deleteYuqueBookToolCallback")
	@ConditionalOnMissingBean(name = "deleteYuqueBookToolCallback")
	public ToolCallback deleteYuqueBookToolCallback(YuqueDeleteBookService deleteYuqueBook) {
		return FunctionToolCallback.builder(YuqueConstants.DELETE_BOOK_TOOL_NAME, deleteYuqueBook)
			.description("Use yuque api to invoke a http request to delete a book.")
			.inputType(YuqueDeleteBookService.DeleteBookRequest.class)
			.build();
	}

	@Bean(name = "createYuqueDocToolCallback")
	@ConditionalOnMissingBean(name = "createYuqueDocToolCallback")
	public ToolCallback createYuqueDocToolCallback(YuqueCreateDocService createYuqueDoc) {
		return FunctionToolCallback.builder(YuqueConstants.CREATE_DOC_TOOL_NAME, createYuqueDoc)
			.description("Use yuque api to invoke a http request to create a doc.")
			.inputType(YuqueCreateDocService.CreateDocRequest.class)
			.build();
	}

	@Bean(name = "queryYuqueDocToolCallback")
	@ConditionalOnMissingBean(name = "queryYuqueDocToolCallback")
	public ToolCallback queryYuqueDocToolCallback(YuqueQueryDocService queryYuqueDoc) {
		return FunctionToolCallback.builder(YuqueConstants.QUERY_DOC_TOOL_NAME, queryYuqueDoc)
			.description("Use yuque api to invoke a http request to query a doc.")
			.inputType(YuqueQueryDocService.queryDocRequest.class)
			.build();
	}

	@Bean(name = "updateDocServiceToolCallback")
	@ConditionalOnMissingBean(name = "updateDocServiceToolCallback")
	public ToolCallback updateDocServiceToolCallback(YuqueUpdateDocService updateDocService) {
		return FunctionToolCallback.builder(YuqueConstants.UPDATE_DOC_TOOL_NAME, updateDocService)
			.description("Use yuque api to invoke a http request to update your doc.")
			.inputType(YuqueUpdateDocService.updateDocRequest.class)
			.build();
	}

	@Bean(name = "deleteDocServiceToolCallback")
	@ConditionalOnMissingBean(name = "deleteDocServiceToolCallback")
	public ToolCallback deleteDocServiceToolCallback(YuqueDeleteDocService deleteDocService) {
		return FunctionToolCallback.builder(YuqueConstants.DELETE_DOC_TOOL_NAME, deleteDocService)
			.description("Use yuque api to invoke a http request to delete your doc.")
			.inputType(YuqueDeleteDocService.DeleteDocRequest.class)
			.build();
	}

}
