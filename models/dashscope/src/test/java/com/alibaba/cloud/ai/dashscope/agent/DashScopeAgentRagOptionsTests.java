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
package com.alibaba.cloud.ai.dashscope.agent;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DashScopeAgentRagOptionsTests {

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Test
	void testBuilderWithMethods() {
		ObjectNode metadataFilter = this.objectMapper.createObjectNode().put("author", "alice");
		ObjectNode structuredFilter = this.objectMapper.createObjectNode().put("category", "tech");

		DashScopeAgentRagOptions options = DashScopeAgentRagOptions.builder()
			.pipelineIds(List.of("p1", "p2"))
			.fileIds(List.of("f1"))
			.tags(List.of("tag1"))
			.metadataFilter(metadataFilter)
			.structuredFilter(structuredFilter)
			.sessionFileIds(List.of("sf1"))
			.build();

		assertThat(options.getPipelineIds()).containsExactly("p1", "p2");
		assertThat(options.getFileIds()).containsExactly("f1");
		assertThat(options.getTags()).containsExactly("tag1");
		assertThat(options.getMetadataFilter().get("author").asText()).isEqualTo("alice");
		assertThat(options.getStructuredFilter().get("category").asText()).isEqualTo("tech");
		assertThat(options.getSessionFileIds()).containsExactly("sf1");
	}

	@Test
	void testSettersAndGetters() {
		ObjectNode metadataFilter = this.objectMapper.createObjectNode().put("k", "v");
		ObjectNode structuredFilter = this.objectMapper.createObjectNode().put("s", "x");

		DashScopeAgentRagOptions options = new DashScopeAgentRagOptions();
		options.setPipelineIds(List.of("p1"));
		options.setFileIds(List.of("f1"));
		options.setTags(List.of("t1"));
		options.setMetadataFilter(metadataFilter);
		options.setStructuredFilter(structuredFilter);
		options.setSessionFileIds(List.of("sf1"));

		assertThat(options.getPipelineIds()).containsExactly("p1");
		assertThat(options.getFileIds()).containsExactly("f1");
		assertThat(options.getTags()).containsExactly("t1");
		assertThat(options.getMetadataFilter().get("k").asText()).isEqualTo("v");
		assertThat(options.getStructuredFilter().get("s").asText()).isEqualTo("x");
		assertThat(options.getSessionFileIds()).containsExactly("sf1");
	}

	@Test
	void testBuilderFromExistingOptions() {
		DashScopeAgentRagOptions original = new DashScopeAgentRagOptions();
		original.setPipelineIds(List.of("old"));

		DashScopeAgentRagOptions built = new DashScopeAgentRagOptions.Builder(original)
			.pipelineIds(List.of("new"))
			.build();

		assertThat(built).isSameAs(original);
		assertThat(built.getPipelineIds()).containsExactly("new");
	}

}
