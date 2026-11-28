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
package com.alibaba.cloud.ai.autoconfigure.dashscope;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DashScopeChatHttpAutoConfigurationTests {

	@Test
	void usesConfiguredFirstChunkBudgetAndPreservesWebClientFilters() {
		var requests = new AtomicInteger();
		var server = HttpServer.create().host("127.0.0.1").port(0)
				.handle((request, response) -> request.receive().aggregate().asString().flatMap(body -> {
					if ("preserved".equals(request.requestHeaders().get("X-Client-Filter"))) {
						requests.incrementAndGet();
					}
					return Mono.never();
				})).bindNow(Duration.ofSeconds(5));
		try {
			new ApplicationContextRunner()
					.withConfiguration(AutoConfigurations.of(DashScopeChatAutoConfiguration.class))
					.withBean(ToolCallingManager.class, () -> ToolCallingManager.builder().build())
					.withBean(WebClient.Builder.class, () -> WebClient.builder().filter((request, exchange) ->
							exchange.exchange(ClientRequest.from(request).header("X-Client-Filter", "preserved").build())))
					.withPropertyValues("spring.ai.dashscope.api-key=local-test",
							"spring.ai.dashscope.base-url=http://127.0.0.1:" + server.port(),
							"spring.ai.dashscope.chat.http.first-chunk-timeout=1s",
							"spring.ai.dashscope.chat.http.stream-read-timeout=10s",
							"spring.ai.dashscope.chat.http.request-timeout=10s")
					.run(context -> {
						assertThat(context).hasSingleBean(DashScopeChatModel.class);
						long started = System.nanoTime();
						assertThatThrownBy(() -> context.getBean(DashScopeChatModel.class)
								.stream(new Prompt("local test")).blockLast(Duration.ofSeconds(5)))
								.hasRootCauseInstanceOf(TimeoutException.class);
						assertThat(TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - started)).isLessThan(4000);
						assertThat(requests).hasValue(1);
					});
		}
		finally {
			server.disposeNow();
		}
	}
}
