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
package com.alibaba.cloud.ai.dashscope.chat;

import java.util.Map;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatApiSpec.ChatCompletionRequest.Parameters.ResponseFormat;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatApiSpec.ChatCompletionRequest.Parameters.ResponseFormat.JsonSchemaConfig;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatApiSpec.ChatCompletionRequest.Parameters.ResponseFormat.Type;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import org.junit.jupiter.api.Test;

import org.springframework.ai.converter.BeanOutputConverter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for {@link ResponseFormat}.
 *
 * @author yuluo
 * @author brianxiadong
 * @author guanxu
 */
class DashScopeChatResponseFormatTests {

	@Test
	void testTextType() {
		ResponseFormat format = ResponseFormat.builder().type(Type.TEXT).build();

		assertNotNull(format, "Response format should be created");
		assertEquals(Type.TEXT, format.getType(), "Type should be TEXT");
		assertEquals("{\"type\":\"text\"}", format.toString(), "String representation should match");
	}

	@Test
	void testJsonObjectType() {
		ResponseFormat format = ResponseFormat.builder().type(Type.JSON_OBJECT).build();

		assertNotNull(format, "Response format should be created");
		assertEquals(Type.JSON_OBJECT, format.getType(), "Type should be JSON_OBJECT");
		assertEquals("{\"type\":\"json_object\"}", format.toString(), "String representation should match");
	}

	@Test
	void testJsonSchemeType() {
		BeanOutputConverter<User> converter = new BeanOutputConverter<>(User.class);
		Map<String, Object> schema = converter.getJsonSchemaMap();

		JsonSchemaConfig jsonSchemaConfig = JsonSchemaConfig.builder()
			.name("user_info")
			.description("The user information")
			.strict(true)
			.schema(schema)
			.build();
		ResponseFormat format = ResponseFormat.builder().type(Type.JSON_SCHEMA).jsonScheme(jsonSchemaConfig).build();

		assertNotNull(format, "Response format should be created");
		assertEquals(Type.JSON_SCHEMA, format.getType(), "Type should be JSON_SCHEMA");
		assertEquals(jsonSchemaConfig, format.getJsonScheme(), "JSON schema config should match");
		assertTrue(format.toString().contains("\"type\":\"json_schema\""), "String representation should match");
	}

	@Test
	void testEqualsAndHashCode() {
		ResponseFormat format1 = ResponseFormat.builder()
			.type(Type.TEXT)
			.jsonScheme(JsonSchemaConfig.builder().build())
			.build();
		ResponseFormat format2 = ResponseFormat.builder()
			.type(Type.TEXT)
			.jsonScheme(JsonSchemaConfig.builder().build())
			.build();
		ResponseFormat format3 = ResponseFormat.builder()
			.type(Type.JSON_OBJECT)
			.jsonScheme(JsonSchemaConfig.builder().build())
			.build();

		assertTrue(format1.equals(format2), "Equal formats should be equal");
		assertFalse(format1.equals(format3), "Different formats should not be equal");
		assertFalse(format1.equals(null), "Format should not equal null");
		assertFalse(format1.equals("string"), "Format should not equal different type");
		assertEquals(format1.hashCode(), format2.hashCode(), "Equal formats should have same hash code");
		assertNotEquals(format1.hashCode(), format3.hashCode(),
				"Different formats should have different hash codes");
	}

	@Test
	void testSetType() {
		ResponseFormat format = ResponseFormat.builder().type(Type.TEXT).build();
		format.setType(Type.JSON_OBJECT);

		assertEquals(Type.JSON_OBJECT, format.getType(), "Type should be changed to JSON_OBJECT");
	}

	private record User(
			@JsonPropertyDescription("The user name") @JsonProperty(value = "name", required = true) String name,
			@JsonPropertyDescription("The user age") @JsonProperty(value = "age", required = true) int age,
			@JsonPropertyDescription("The user email address") @JsonProperty("email") String email) {
	}

}
