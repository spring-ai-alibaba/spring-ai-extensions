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

import java.util.List;

import org.junit.jupiter.api.Test;

import org.springframework.ai.vectorstore.filter.Filter;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link OceanBaseVectorFilterExpressionConverter}.
 *
 * @author zh-hanlabs
 */
class OceanBaseVectorFilterExpressionConverterTest {

	private final OceanBaseVectorFilterExpressionConverter converter = new OceanBaseVectorFilterExpressionConverter();

	private String meta(String key) {
		return "JSON_UNQUOTE(JSON_EXTRACT(metadata, '$." + key + "'))";
	}

	@Test
	void wrapsMetadataKeysInJsonExtract() {
		Filter.Expression expression = new Filter.Expression(Filter.ExpressionType.EQ, new Filter.Key("tenantId"),
				new Filter.Value("t001"));
		assertThat(this.converter.convertExpression(expression)).isEqualTo(this.meta("tenantId") + " = 't001'");
	}

	@Test
	void usesTableColumnsDirectly() {
		Filter.Expression expression = new Filter.Expression(Filter.ExpressionType.EQ, new Filter.Key("id"),
				new Filter.Value("doc-1"));
		assertThat(this.converter.convertExpression(expression)).isEqualTo("id = 'doc-1'");
	}

	@Test
	void matchesTableColumnsCaseInsensitively() {
		Filter.Expression expression = new Filter.Expression(Filter.ExpressionType.EQ, new Filter.Key("ID"),
				new Filter.Value("doc-1"));
		assertThat(this.converter.convertExpression(expression)).isEqualTo("ID = 'doc-1'");
	}

	@Test
	void escapesSingleQuotesInStringValues() {
		Filter.Expression expression = new Filter.Expression(Filter.ExpressionType.EQ, new Filter.Key("name"),
				new Filter.Value("O'Neill"));
		assertThat(this.converter.convertExpression(expression)).isEqualTo(this.meta("name") + " = 'O''Neill'");
	}

	@Test
	void rendersNumbersAndBooleansUnquoted() {
		Filter.Expression gte = new Filter.Expression(Filter.ExpressionType.GTE, new Filter.Key("score"),
				new Filter.Value(80));
		Filter.Expression eq = new Filter.Expression(Filter.ExpressionType.EQ, new Filter.Key("active"),
				new Filter.Value(true));
		assertThat(this.converter.convertExpression(gte)).isEqualTo(this.meta("score") + " >= 80");
		assertThat(this.converter.convertExpression(eq)).isEqualTo(this.meta("active") + " = true");
	}

	@Test
	void rendersNullValue() {
		Filter.Expression expression = new Filter.Expression(Filter.ExpressionType.EQ, new Filter.Key("flag"),
				new Filter.Value(null));
		assertThat(this.converter.convertExpression(expression)).isEqualTo(this.meta("flag") + " = NULL");
	}

	@Test
	void convertsInAndNotIn() {
		Filter.Value values = new Filter.Value(List.of("a", "b"));
		Filter.Expression in = new Filter.Expression(Filter.ExpressionType.IN, new Filter.Key("tag"), values);
		Filter.Expression nin = new Filter.Expression(Filter.ExpressionType.NIN, new Filter.Key("tag"), values);
		String condition = this.meta("tag") + " = 'a' OR " + this.meta("tag") + " = 'b'";
		assertThat(this.converter.convertExpression(in)).isEqualTo("(" + condition + ")");
		assertThat(this.converter.convertExpression(nin)).isEqualTo("!(" + condition + ")");
	}

	@Test
	void combinesWithAndOrAndGrouping() {
		Filter.Expression a = new Filter.Expression(Filter.ExpressionType.EQ, new Filter.Key("a"), new Filter.Value(1));
		Filter.Expression b = new Filter.Expression(Filter.ExpressionType.EQ, new Filter.Key("b"), new Filter.Value(2));
		Filter.Expression c = new Filter.Expression(Filter.ExpressionType.EQ, new Filter.Key("c"), new Filter.Value(3));
		Filter.Expression or = new Filter.Expression(Filter.ExpressionType.OR, a, b);
		Filter.Expression expression = new Filter.Expression(Filter.ExpressionType.AND, new Filter.Group(or), c);
		assertThat(this.converter.convertExpression(expression))
				.isEqualTo("(" + this.meta("a") + " = 1 OR " + this.meta("b") + " = 2) AND " + this.meta("c") + " = 3");
	}

}
