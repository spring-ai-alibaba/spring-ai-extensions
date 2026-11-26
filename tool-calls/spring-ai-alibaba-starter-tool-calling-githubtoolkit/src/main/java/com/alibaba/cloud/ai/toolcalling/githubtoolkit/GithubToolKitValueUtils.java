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
package com.alibaba.cloud.ai.toolcalling.githubtoolkit;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jspecify.annotations.Nullable;
import org.springframework.util.StringUtils;

final class GithubToolKitValueUtils {

	private GithubToolKitValueUtils() {
	}

	static String requireResponseBody(@Nullable String responseBody, String operationName) {
		if (responseBody != null) {
			return responseBody;
		}
		throw new IllegalStateException(operationName + " response body must not be null");
	}

	static String requireConfiguredText(@Nullable String value, String propertyName) {
		if (StringUtils.hasText(value)) {
			return value;
		}
		throw new IllegalStateException(propertyName + " must not be empty");
	}

	static Map<String, Object> requireObject(Map<String, Object> source, String fieldName) {
		@Nullable Object value = source.get(fieldName);
		return asObjectMap(value, fieldName);
	}

	static long requireLong(Map<String, Object> source, String fieldName) {
		@Nullable Object value = source.get(fieldName);
		if (value instanceof Number number) {
			return number.longValue();
		}
		throw new IllegalArgumentException("GitHub response field '" + fieldName + "' must be a number");
	}

	static int requireInt(Map<String, Object> source, String fieldName) {
		@Nullable Object value = source.get(fieldName);
		if (value instanceof Number number) {
			return number.intValue();
		}
		throw new IllegalArgumentException("GitHub response field '" + fieldName + "' must be a number");
	}

	static String requireString(Map<String, Object> source, String fieldName) {
		@Nullable Object value = source.get(fieldName);
		if (value instanceof String text) {
			return text;
		}
		throw new IllegalArgumentException("GitHub response field '" + fieldName + "' must be a string");
	}

	static @Nullable String nullableString(Map<String, Object> source, String fieldName) {
		@Nullable Object value = source.get(fieldName);
		if (value == null) {
			return null;
		}
		if (value instanceof String text) {
			return text;
		}
		throw new IllegalArgumentException("GitHub response field '" + fieldName + "' must be a string");
	}

	static List<Map<String, Object>> optionalObjectList(Map<String, Object> source, String fieldName) {
		@Nullable Object value = source.get(fieldName);
		if (value == null) {
			return List.of();
		}
		if (!(value instanceof List<?> values)) {
			throw new IllegalArgumentException("GitHub response field '" + fieldName + "' must be a list");
		}
		return values.stream().map(item -> asObjectMap(item, fieldName)).toList();
	}

	private static Map<String, Object> asObjectMap(@Nullable Object value, String fieldName) {
		if (!(value instanceof Map<?, ?> rawMap)) {
			throw new IllegalArgumentException("GitHub response field '" + fieldName + "' must be an object");
		}
		Map<String, Object> map = new LinkedHashMap<>();
		rawMap.forEach((key, entryValue) -> map.put(String.valueOf(key), entryValue));
		return map;
	}

}
