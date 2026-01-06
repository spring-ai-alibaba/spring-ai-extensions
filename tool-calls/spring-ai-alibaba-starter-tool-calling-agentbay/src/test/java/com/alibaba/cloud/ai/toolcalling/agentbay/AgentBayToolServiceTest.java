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
 * Tests for AgentBayToolService.
 *
 * @author Spring AI Alibaba
 */
class AgentBayToolServiceTest {

	@Test
	void testCreateSessionRequest() {
		AgentBayToolService.CreateSessionRequest request = new AgentBayToolService.CreateSessionRequest("code_latest");
		assertEquals("code_latest", request.imageId());
	}

	@Test
	void testCreateSessionResponse() {
		AgentBayToolService.CreateSessionResponse response = new AgentBayToolService.CreateSessionResponse("sess-123",
				true, "Success");

		assertEquals("sess-123", response.sessionId());
		assertTrue(response.success());
		assertEquals("Success", response.message());
	}

	@Test
	void testDeleteSessionRequest() {
		AgentBayToolService.DeleteSessionRequest request = new AgentBayToolService.DeleteSessionRequest("sess-456");
		assertEquals("sess-456", request.sessionId());
	}

	@Test
	void testDeleteSessionResponse() {
		AgentBayToolService.DeleteSessionResponse response = new AgentBayToolService.DeleteSessionResponse(true,
				"Deleted");

		assertTrue(response.success());
		assertEquals("Deleted", response.message());
	}

	@Test
	void testExecuteShellRequest() {
		AgentBayToolService.ExecuteShellRequest request = new AgentBayToolService.ExecuteShellRequest("ls -la",
				"sess-789", true);

		assertEquals("ls -la", request.command());
		assertEquals("sess-789", request.sessionId());
		assertTrue(request.autoCleanup());
	}

	@Test
	void testExecuteShellResponse() {
		AgentBayToolService.ExecuteShellResponse response = new AgentBayToolService.ExecuteShellResponse("output", 0,
				true, "sess-999", "Success");

		assertEquals("output", response.output());
		assertEquals(0, response.exitCode());
		assertTrue(response.success());
		assertEquals("sess-999", response.sessionId());
		assertEquals("Success", response.message());
	}

}


