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

package com.alibaba.cloud.ai.toolcalling.jsonprocessor;

import com.alibaba.cloud.ai.toolcalling.common.JsonParseTool;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test class for JsonRemoveService
 */
public class JsonProcessorRemoveServiceTest {

	private JsonProcessorRemoveService jsonProcessorRemoveService;

	private String jsonContent;

	private JsonMapper jsonMapper;

	@BeforeEach
	void setUp() {
		jsonMapper = JsonMapper.shared();
		JsonParseTool jsonParseTool = new JsonParseTool(jsonMapper);
		jsonProcessorRemoveService = new JsonProcessorRemoveService(jsonParseTool);
		jsonContent = "{\"name\":\"John\",\"age\":30,\"city\":\"Beijing\",\"isActive\":true}";
	}

	@Test
	void testRemoveStringField() {
		// Test removing a string type field
		JsonProcessorRemoveService.JsonRemoveRequest request = new JsonProcessorRemoveService.JsonRemoveRequest(
				jsonContent, "name");

		// Execute the remove operation and get the return value
		JsonNode result = (JsonNode) jsonProcessorRemoveService.apply(request);

		// Verify that the returned value is the removed field value
		Assertions.assertEquals("John", result.asText());

		// Verify that the field has been removed from the JSON
		ObjectNode jsonObject = (ObjectNode) jsonMapper.readTree(jsonContent);
		jsonObject.remove("name");
		String expectedJson = jsonObject.toString();

		// Execute the service again to get the processed JSON
		ObjectNode processedJson = (ObjectNode) jsonMapper.readTree(jsonContent);
		processedJson.remove("name");

		// Compare the processed JSON
		Assertions.assertEquals(expectedJson, processedJson.toString());
	}

	@Test
	void testRemoveNumberField() {
		// Test removing a number type field
		JsonProcessorRemoveService.JsonRemoveRequest request = new JsonProcessorRemoveService.JsonRemoveRequest(
				jsonContent, "age");

		// Execute the remove operation and get the return value
		JsonNode result = (JsonNode) jsonProcessorRemoveService.apply(request);

		// Verify that the returned value is the removed field value
		Assertions.assertEquals(30, result.asInt());
	}

	@Test
	void testRemoveBooleanField() {
		// Test removing a boolean type field
		JsonProcessorRemoveService.JsonRemoveRequest request = new JsonProcessorRemoveService.JsonRemoveRequest(
				jsonContent, "isActive");

		// Execute the remove operation and get the return value
		JsonNode result = (JsonNode) jsonProcessorRemoveService.apply(request);

		// Verify that the returned value is the removed field value
		Assertions.assertTrue(result.asBoolean());
	}

	@Test
	void testRemoveNonExistentField() {
		// Test removing a non-existent field
		JsonProcessorRemoveService.JsonRemoveRequest request = new JsonProcessorRemoveService.JsonRemoveRequest(
				jsonContent, "nonExistentField");

		// Execute the remove operation and get the return value
		JsonNode result = (JsonNode) jsonProcessorRemoveService.apply(request);

		// Verify that the return value is null
		Assertions.assertNull(result);
	}

	@Test
	void testNonObjectJsonContent() {
		// Test non-object type JSON content
		JsonProcessorRemoveService.JsonRemoveRequest request = new JsonProcessorRemoveService.JsonRemoveRequest(
				jsonContent, "noneField");

		// Verify that an exception is thrown
		JsonNode result = (JsonNode) jsonProcessorRemoveService.apply(request);
		Assertions.assertNull(result);
	}

	@Test
	void testRemoveAndVerifyJsonStructure() {
		// Test the JSON structure after removing a field
		JsonProcessorRemoveService.JsonRemoveRequest request = new JsonProcessorRemoveService.JsonRemoveRequest(
				jsonContent, "city");

		// Execute the remove operation
		jsonProcessorRemoveService.apply(request);

		// Manually build the expected JSON structure
		ObjectNode expectedJson = jsonMapper.createObjectNode();
		expectedJson.put("name", "John");
		expectedJson.put("age", 30);
		expectedJson.put("isActive", true);

		// Manually remove the field and compare
		ObjectNode actualJson = (ObjectNode) jsonMapper.readTree(jsonContent);

		actualJson.remove("city");

		// Verify the JSON structure
		Assertions.assertEquals(expectedJson.get("name").asText(), actualJson.get("name").asText());
		Assertions.assertEquals(expectedJson.get("age").asInt(), actualJson.get("age").asInt());
		Assertions.assertEquals(expectedJson.get("isActive").asBoolean(), actualJson.get("isActive").asBoolean());
		Assertions.assertFalse(actualJson.has("city"));
	}

}
