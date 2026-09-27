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
package com.alibaba.cloud.ai.vectorstore.analyticdb;

import org.junit.jupiter.api.Test;

import org.springframework.ai.vectorstore.filter.Filter;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link AdVectorFilterExpressionConverter}.
 *
 * @author zh-hanlabs
 */
class AdVectorFilterExpressionConverterTest {

	private final AdVectorFilterExpressionConverter converter = new AdVectorFilterExpressionConverter();

	@Test
	void convertsKeyToJsonbExtraction() {
		Filter.Expression expression = new Filter.Expression(Filter.ExpressionType.EQ, new Filter.Key("tenantId"),
				new Filter.Value("t001"));
		assertThat(this.converter.convertExpression(expression)).isEqualTo("metadata->>'tenantId' = \"t001\"");
	}

	@Test
	void castsNumericComparisonToJsonbNumericCast() {
		Filter.Expression expression = new Filter.Expression(Filter.ExpressionType.GTE, new Filter.Key("score"),
				new Filter.Value(80));
		assertThat(this.converter.convertExpression(expression)).isEqualTo("(metadata->>'score')::numeric >= 80");
	}

	@Test
	void castsNumericEqualityAndInequality() {
		Filter.Expression eq = new Filter.Expression(Filter.ExpressionType.EQ, new Filter.Key("year"),
				new Filter.Value(2024));
		Filter.Expression ne = new Filter.Expression(Filter.ExpressionType.NE, new Filter.Key("count"),
				new Filter.Value(0));
		assertThat(this.converter.convertExpression(eq)).isEqualTo("(metadata->>'year')::numeric = 2024");
		assertThat(this.converter.convertExpression(ne)).isEqualTo("(metadata->>'count')::numeric != 0");
	}

	@Test
	void doesNotCastStringComparison() {
		Filter.Expression expression = new Filter.Expression(Filter.ExpressionType.LT, new Filter.Key("name"),
				new Filter.Value("a"));
		assertThat(this.converter.convertExpression(expression)).isEqualTo("metadata->>'name' < \"a\"");
	}

	@Test
	void keepsGroupingAndLogicalOperators() {
		Filter.Expression tenant = new Filter.Expression(Filter.ExpressionType.EQ, new Filter.Key("tenantId"),
				new Filter.Value("t001"));
		Filter.Expression score = new Filter.Expression(Filter.ExpressionType.GTE, new Filter.Key("score"),
				new Filter.Value(80));
		Filter.Expression and = new Filter.Expression(Filter.ExpressionType.AND, tenant, score);
		Filter.Expression expression = new Filter.Expression(Filter.ExpressionType.OR, new Filter.Group(and),
				new Filter.Expression(Filter.ExpressionType.EQ, new Filter.Key("region"), new Filter.Value("cn")));
		assertThat(this.converter.convertExpression(expression))
				.isEqualTo("(metadata->>'tenantId' = \"t001\" && (metadata->>'score')::numeric >= 80) "
						+ "|| metadata->>'region' = \"cn\"");
	}

}
