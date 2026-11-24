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
package com.alibaba.cloud.ai.parser.tika;

import java.io.Serial;

/** Indicates that the extracted text would exceed the Tika content handler's write limit. */
public class TikaTextExtractionLimitException extends IllegalStateException {

	@Serial
	private static final long serialVersionUID = 1L;

	TikaTextExtractionLimitException(Throwable cause) {
		super("Extracted text exceeds the content handler write limit", cause);
	}

}
