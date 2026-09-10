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

import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.Filter.Expression;
import org.springframework.ai.vectorstore.filter.Filter.Group;
import org.springframework.ai.vectorstore.filter.Filter.Key;
import org.springframework.ai.vectorstore.filter.Filter.Value;
import org.springframework.ai.vectorstore.filter.converter.AbstractFilterExpressionConverter;

/**
 * Converts filter expressions into the native PostgreSQL boolean syntax expected
 * by AnalyticDB for PostgreSQL. Collection metadata lives in a {@code jsonb}
 * column, so keys are emitted as {@code metadata->>'key'} text extractions.
 * Comparisons against numbers cast the extracted text with {@code ::numeric},
 * because PostgreSQL defines no comparison operators between text and numeric
 * types.
 *
 * @author HeYQ
 */
public class AdVectorFilterExpressionConverter extends AbstractFilterExpressionConverter {

	private static final String METADATA_COLUMN = "metadata";

	@Override
	protected void doExpression(Expression expression, StringBuilder context) {
		if (expression.type() == Filter.ExpressionType.IN) {
			handleIn(expression, context);
		}
		else if (expression.type() == Filter.ExpressionType.NIN) {
			handleNotIn(expression, context);
		}
		else {
			if (expression.left() instanceof Key key) {
				doKey(key, isNumericOperand(expression.right()), context);
			}
			else {
				this.convertOperand(expression.left(), context);
			}
			context.append(getOperationSymbol(expression));
			this.convertOperand(expression.right(), context);
		}
	}

	private void handleIn(Expression expression, StringBuilder context) {
		context.append("(");
		convertToConditions(expression, context);
		context.append(")");
	}

	private void convertToConditions(Expression expression, StringBuilder context) {
		if (!(expression.left() instanceof Key key)) {
			throw new IllegalArgumentException("IN/NIN left operand must be a Key");
		}
		Filter.Value right = (Filter.Value) expression.right();
		Object value = right.value();
		if (!(value instanceof List)) {
			throw new IllegalArgumentException("Expected a List, but got: " + value.getClass().getSimpleName());
		}
		List<Object> values = (List) value;
		for (int i = 0; i < values.size(); i++) {
			this.doKey(key, values.get(i) instanceof Number, context);
			context.append(" = ");
			this.doSingleValue(values.get(i), context);
			if (i < values.size() - 1) {
				context.append(" OR ");
			}
		}
	}

	private void handleNotIn(Expression expression, StringBuilder context) {
		context.append("NOT (");
		convertToConditions(expression, context);
		context.append(")");
	}

	private String getOperationSymbol(Expression exp) {
		switch (exp.type()) {
			case AND:
				return " AND ";
			case OR:
				return " OR ";
			case EQ:
				return " = ";
			case NE:
				return " != ";
			case LT:
				return " < ";
			case LTE:
				return " <= ";
			case GT:
				return " > ";
			case GTE:
				return " >= ";
			default:
				throw new RuntimeException("Not supported expression type: " + exp.type());
		}
	}

	@Override
	protected void doKey(Key key, StringBuilder context) {
		doKey(key, false, context);
	}

	private void doKey(Key key, boolean numericCast, StringBuilder context) {
		String path = METADATA_COLUMN + "->>" + sqlLiteral(key.key());
		context.append(numericCast ? "(" + path + ")::numeric" : path);
	}

	@Override
	protected void doSingleValue(Object value, StringBuilder context) {
		if (value instanceof String s) {
			context.append(sqlLiteral(s));
		}
		else if (value instanceof Boolean b) {
			context.append(sqlLiteral(String.valueOf(b)));
		}
		else {
			context.append(value);
		}
	}

	private boolean isNumericOperand(Filter.Operand operand) {
		return operand instanceof Value value && value.value() instanceof Number;
	}

	private String sqlLiteral(String raw) {
		return "'" + raw.replace("'", "''") + "'";
	}

	@Override
	protected void doStartGroup(Group group, StringBuilder context) {
		context.append("(");
	}

	@Override
	protected void doEndGroup(Group group, StringBuilder context) {
		context.append(")");
	}

}
