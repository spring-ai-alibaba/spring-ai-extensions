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
package com.alibaba.cloud.ai.toolcalling.agentbay;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for AgentBayProperties.
 *
 * @author Spring AI Alibaba
 */
class AgentBayPropertiesTest {

	@Test
	void testDefaultValues() {
		AgentBayProperties properties = new AgentBayProperties();

		assertTrue(properties.isEnabled());
		assertNull(properties.getApiKey());
		assertEquals("cn-shanghai", properties.getRegionId());
		assertEquals("wuyingai.cn-shanghai.aliyuncs.com", properties.getEndpoint());
		assertEquals("linux_latest", properties.getDefaultImageId());
		assertEquals(60000, properties.getTimeoutMs());
	}

	@Test
	void testSetters() {
		AgentBayProperties properties = new AgentBayProperties();

		properties.setEnabled(false);
		assertFalse(properties.isEnabled());

		properties.setApiKey("test-key");
		assertEquals("test-key", properties.getApiKey());

		properties.setRegionId("cn-beijing");
		assertEquals("cn-beijing", properties.getRegionId());

		properties.setEndpoint("test.endpoint.com");
		assertEquals("test.endpoint.com", properties.getEndpoint());

		properties.setDefaultImageId("browser_latest");
		assertEquals("browser_latest", properties.getDefaultImageId());

		properties.setTimeoutMs(60000);
		assertEquals(60000, properties.getTimeoutMs());
	}

}

