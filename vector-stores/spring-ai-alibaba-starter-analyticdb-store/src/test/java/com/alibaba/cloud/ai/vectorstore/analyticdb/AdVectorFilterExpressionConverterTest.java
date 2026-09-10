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

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.ai.vectorstore.filter.Filter.Expression;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link AdVectorFilterExpressionConverter}. The converter output
 * is spliced into the WHERE clause of AnalyticDB for PostgreSQL queries, so it
 * must be valid PostgreSQL against a {@code jsonb} metadata column.
 */
class AdVectorFilterExpressionConverterTest {

	private final AdVectorFilterExpressionConverter converter = new AdVectorFilterExpressionConverter();

	@Test
	void equalityAgainstStringUsesNativeJsonbExtraction() {
		Expression expression = new FilterExpressionBuilder().eq("country", "CN").build();

		assertThat(this.converter.convertExpression(expression)).isEqualTo("metadata->>'country' = 'CN'");
	}

	@Test
	void comparisonAgainstNumberCastsExtractedTextToNumeric() {
		FilterExpressionBuilder builder = new FilterExpressionBuilder();

		assertThat(this.converter.convertExpression(builder.gte("score", 80).build()))
			.isEqualTo("(metadata->>'score')::numeric >= 80");
		assertThat(this.converter.convertExpression(builder.lt("year", 2024).build()))
			.isEqualTo("(metadata->>'year')::numeric < 2024");
	}

	@Test
	void conjunctionsUseSqlKeywords() {
		FilterExpressionBuilder builder = new FilterExpressionBuilder();
		Expression expression = builder.and(builder.eq("country", "CN"), builder.gte("score", 80)).build();

		assertThat(this.converter.convertExpression(expression))
			.isEqualTo("metadata->>'country' = 'CN' AND (metadata->>'score')::numeric >= 80");
	}

	@Test
	void disjunctionsUseSqlKeywords() {
		FilterExpressionBuilder builder = new FilterExpressionBuilder();
		Expression expression = builder.or(builder.eq("country", "CN"), builder.eq("country", "US")).build();

		assertThat(this.converter.convertExpression(expression))
			.isEqualTo("metadata->>'country' = 'CN' OR metadata->>'country' = 'US'");
	}

	@Test
	void groupsStayParenthesized() {
		FilterExpressionBuilder builder = new FilterExpressionBuilder();
		Expression expression = builder
			.and(builder.eq("type", "pdf"), builder.group(builder.or(builder.eq("lang", "zh"), builder.eq("lang", "en"))))
			.build();

		assertThat(this.converter.convertExpression(expression))
			.isEqualTo("metadata->>'type' = 'pdf' AND (metadata->>'lang' = 'zh' OR metadata->>'lang' = 'en')");
	}

	@Test
	void inExpandsToEqualityConditions() {
		Expression expression = new FilterExpressionBuilder().in("type", List.of("pdf", "doc")).build();

		assertThat(this.converter.convertExpression(expression))
			.isEqualTo("(metadata->>'type' = 'pdf' OR metadata->>'type' = 'doc')");
	}

	@Test
	void inOverNumbersCastsEveryCondition() {
		Expression expression = new FilterExpressionBuilder().in("year", List.of(2023, 2024)).build();

		assertThat(this.converter.convertExpression(expression))
			.isEqualTo("((metadata->>'year')::numeric = 2023 OR (metadata->>'year')::numeric = 2024)");
	}

	@Test
	void notInNegatesTheWholeConditionList() {
		Expression expression = new FilterExpressionBuilder().nin("type", List.of("pdf", "doc")).build();

		assertThat(this.converter.convertExpression(expression))
			.isEqualTo("NOT (metadata->>'type' = 'pdf' OR metadata->>'type' = 'doc')");
	}

	@Test
	void booleanValuesCompareAsTextLiterals() {
		Expression expression = new FilterExpressionBuilder().eq("active", true).build();

		assertThat(this.converter.convertExpression(expression)).isEqualTo("metadata->>'active' = 'true'");
	}

	@Test
	void stringLiteralsEscapeSingleQuotes() {
		Expression expression = new FilterExpressionBuilder().eq("note", "it's fine").build();

		assertThat(this.converter.convertExpression(expression)).isEqualTo("metadata->>'note' = 'it''s fine'");
	}

	@Test
	void stringKeysAreQuotedToo() {
		Expression expression = new FilterExpressionBuilder().eq("weird'key", "v").build();

		assertThat(this.converter.convertExpression(expression)).isEqualTo("metadata->>'weird''key' = 'v'");
	}

	@Test
	void negationIsPushedDownByTheBaseClass() {
		FilterExpressionBuilder builder = new FilterExpressionBuilder();
		Expression expression = builder.not(builder.eq("country", "CN")).build();

		assertThat(this.converter.convertExpression(expression)).isEqualTo("metadata->>'country' != 'CN'");
	}

}
