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
package com.alibaba.cloud.ai.vectorstore.oceanbase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.EmbeddedDocument;
import org.springframework.ai.vectorstore.SearchRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class OceanBasePrecomputedVectorTests {

	@Test
	void usesColumnDimensionsAndBindsThePrecomputedVectorInSql() throws Exception {
		var model = mock(EmbeddingModel.class);
		var source = mock(DataSource.class);
		var connection = mock(Connection.class);
		var upsert = mock(PreparedStatement.class);
		var query = mock(PreparedStatement.class);
		var empty = mock(ResultSet.class);
		var describe = mock(PreparedStatement.class);
		var columns = mock(ResultSet.class);
		when(source.getConnection()).thenReturn(connection);
		when(connection.prepareStatement(anyString())).thenReturn(query);
		when(connection.prepareStatement(contains("SHOW COLUMNS"))).thenReturn(describe);
		when(connection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS))).thenReturn(upsert);
		when(describe.executeQuery()).thenReturn(columns);
		when(columns.next()).thenReturn(true);
		when(columns.getString("Field")).thenReturn("embedding");
		when(columns.getString("Type")).thenReturn("vector(2)");
		when(query.executeQuery()).thenReturn(empty);
		var store = OceanBaseVectorStore.builder("documents", source, model).build();
		var document = new Document("test-id", "text", Map.of("kind", "test"));
		float[] vector = { 0.25f, 0.75f };
		var embedded = new EmbeddedDocument(document, vector);

		assertThatIllegalArgumentException().isThrownBy(
				() -> store.upsert(List.of(embedded, new EmbeddedDocument(new Document("other"), new float[] { 1 }))));
		store.upsert(List.of(embedded));
		var statement = ArgumentCaptor.forClass(String.class);
		verify(connection).prepareStatement(statement.capture(), eq(Statement.RETURN_GENERATED_KEYS));
		assertThat(statement.getValue()).contains("ON DUPLICATE KEY UPDATE");
		verify(upsert).setString(1, "test-id");
		verify(upsert).setString(2, "[0.25, 0.75]");
		verify(upsert).executeBatch();

		assertThat(store.similaritySearch(vector, SearchRequest.builder().query("ignored").topK(2).build())).isEmpty();
		verify(query).setString(1, "[0.25, 0.75]");
		verify(query).setString(2, "[0.25, 0.75]");
		verify(query).setInt(3, 2);
		verifyNoInteractions(model);
	}

}
