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
package com.alibaba.cloud.ai.vectorstore.opensearch;

import java.util.List;

import org.junit.jupiter.api.Test;

import org.springframework.ai.vectorstore.filter.Filter;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link OpenSearchVectorStoreFilterExpressionConverter}.
 *
 * @author zh-hanlabs
 */
class OpenSearchVectorStoreFilterExpressionConverterTest {

	private final OpenSearchVectorStoreFilterExpressionConverter converter = new OpenSearchVectorStoreFilterExpressionConverter();

	@Test
	void convertsEqualityToQueryStringSyntax() {
		Filter.Expression expression = new Filter.Expression(Filter.ExpressionType.EQ, new Filter.Key("country"),
				new Filter.Value("CN"));
		assertThat(this.converter.convertExpression(expression)).isEqualTo("metadata.country:CN");
	}

	@Test
	void convertsNumericRangeComparisons() {
		Filter.Expression gte = new Filter.Expression(Filter.ExpressionType.GTE, new Filter.Key("score"),
				new Filter.Value(80));
		Filter.Expression lt = new Filter.Expression(Filter.ExpressionType.LT, new Filter.Key("size"),
				new Filter.Value(5));
		assertThat(this.converter.convertExpression(gte)).isEqualTo("metadata.score:>=80");
		assertThat(this.converter.convertExpression(lt)).isEqualTo("metadata.size:<5");
	}

	@Test
	void convertsInAndNotIn() {
		Filter.Value values = new Filter.Value(List.of("a", "b"));
		Filter.Expression in = new Filter.Expression(Filter.ExpressionType.IN, new Filter.Key("tag"), values);
		Filter.Expression nin = new Filter.Expression(Filter.ExpressionType.NIN, new Filter.Key("tag"), values);
		assertThat(this.converter.convertExpression(in)).isEqualTo("(metadata.tag:a OR b)");
		assertThat(this.converter.convertExpression(nin)).isEqualTo("NOT (metadata.tag:a OR b)");
	}

	@Test
	void combinesWithAndOrAndGrouping() {
		Filter.Expression a = new Filter.Expression(Filter.ExpressionType.EQ, new Filter.Key("a"), new Filter.Value(1));
		Filter.Expression b = new Filter.Expression(Filter.ExpressionType.EQ, new Filter.Key("b"), new Filter.Value(2));
		Filter.Expression c = new Filter.Expression(Filter.ExpressionType.EQ, new Filter.Key("c"), new Filter.Value(3));
		Filter.Expression or = new Filter.Expression(Filter.ExpressionType.OR, a, b);
		Filter.Expression expression = new Filter.Expression(Filter.ExpressionType.AND, new Filter.Group(or), c);
		assertThat(this.converter.convertExpression(expression))
				.isEqualTo("(metadata.a:1 OR metadata.b:2) AND metadata.c:3");
	}

	@Test
	void convertsInequality() {
		Filter.Expression expression = new Filter.Expression(Filter.ExpressionType.NE, new Filter.Key("status"),
				new Filter.Value("active"));
		assertThat(this.converter.convertExpression(expression)).isEqualTo("metadata.status: NOT active");
	}

	@Test
	void stripsOuterQuotesFromKeys() {
		Filter.Expression expression = new Filter.Expression(Filter.ExpressionType.EQ, new Filter.Key("\"country\""),
				new Filter.Value("CN"));
		assertThat(this.converter.convertExpression(expression)).isEqualTo("metadata.country:CN");
	}

	@Test
	void prefixesMetadataToIdentifiers() {
		assertThat(this.converter.withMetaPrefix("country")).isEqualTo("metadata.country");
	}

}
