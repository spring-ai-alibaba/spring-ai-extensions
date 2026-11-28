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
package com.alibaba.cloud.ai.dashscope.api;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.HttpHeaders;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.reactivestreams.Publisher;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.DisposableServer;
import reactor.netty.Connection;
import reactor.netty.http.server.HttpServer;
import reactor.netty.http.server.HttpServerResponse;
import reactor.test.StepVerifier;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DashScopeChatHttpTransportTests {

	private static final Duration WAIT = Duration.ofSeconds(5);

	private static final String NATIVE_DELTA = """
			{"request_id":"local","output":{"choices":[{"finish_reason":"null","message":{"role":"assistant","content":"partial"}}]}}
			""".strip();

	private static final String NATIVE_COMPLETE = """
			{"request_id":"local","output":{"choices":[{"finish_reason":"stop","message":{"role":"assistant","content":"ok"}}]}}
			""".strip();

	private static final String COMPAT_DELTA = """
			{"id":"local","object":"chat.completion.chunk","created":1,"model":"qwen-plus","choices":[{"index":0,"delta":{"role":"assistant","content":"partial"},"finish_reason":null}]}
			""".strip();

	private static final String COMPAT_COMPLETE = """
			{"id":"local","object":"chat.completion.chunk","created":1,"model":"qwen-plus","choices":[{"index":0,"delta":{},"finish_reason":"stop"}]}
			""".strip();

	private static final String COMPAT_SYNC = """
			{"id":"local","object":"chat.completion","created":1,"model":"qwen-plus","choices":[{"index":0,"message":{"role":"assistant","content":"ok"},"finish_reason":"stop"}]}
			""".strip();

	@ParameterizedTest
	@ValueSource(booleans = { false, true })
	void validatesTerminalFramesWithoutReplayingTheRequest(boolean compatible) {
		var requests = new AtomicInteger();
		var bodies = new AtomicReference<String>();
		var headers = new AtomicReference<String>();
		var server = server((request, response) -> {
			bodies.set(request.body());
			headers.set(request.headers().get("X-DashScope-Workspace"));
			int count = requests.incrementAndGet();
			String body = compatible
					? event(COMPAT_DELTA) + event(COMPAT_COMPLETE) + (count > 1 ? event("[DONE]") : "")
					: event(count == 1 ? NATIVE_DELTA : NATIVE_COMPLETE);
			return response.sse().sendString(Mono.just(body));
		});
		try (var transport = new DashScopeHttpTransport(DashScopeHttpTransport.Settings.defaults())) {
			var model = model(compatible, server, transport);
			assertThatThrownBy(() -> model.stream(new Prompt("hello")).collectList().block(WAIT))
					.isInstanceOf(DashScopeStreamTruncatedException.class);
			assertThat(requests).hasValue(1);
			var successful = model.stream(new Prompt("hello")).collectList().block(WAIT);
			assertThat(successful).isNotEmpty();
			assertThat(requests).hasValue(2);
			if (compatible) {
				assertThat(bodies.get()).contains("\"stream\":true", "hello");
				assertThat(headers).hasValue("model-workspace");
			}
		}
		finally {
			server.disposeNow();
		}
	}

	@ParameterizedTest
	@ValueSource(booleans = { false, true })
	void compatibleCancellationReleasesTheConnectionBeforeOrAfterHeaders(boolean afterHeaders) throws Exception {
		var accepted = new CountDownLatch(1);
		var released = new CountDownLatch(1);
		var first = new CountDownLatch(1);
		var requests = new AtomicInteger();
		var server = server((request, response) -> {
			if (requests.incrementAndGet() > 1) {
				return response.sse().sendString(Mono.just(event(COMPAT_DELTA) + event(COMPAT_COMPLETE) + event("[DONE]")));
			}
			response.withConnection(channel -> channel.onDispose(released::countDown));
			accepted.countDown();
			if (afterHeaders) {
				return response.sse().send(Flux.concat(
						Mono.fromSupplier(() -> Unpooled.wrappedBuffer(event(COMPAT_DELTA).getBytes(UTF_8))),
						Mono.never()), ignored -> true);
			}
			return Mono.never();
		});
		try (var transport = new DashScopeHttpTransport(new DashScopeHttpTransport.Settings(1, 1,
				Duration.ofSeconds(1), Duration.ofSeconds(10), Duration.ofMinutes(1), Duration.ofSeconds(2),
				Duration.ofSeconds(2), Duration.ofSeconds(4), Duration.ofSeconds(4), WAIT))) {
			var model = model(true, server, transport);
			var running = model.stream(new Prompt("wait")).subscribe(value -> first.countDown(), error -> { });
			try {
				assertThat(accepted.await(3, TimeUnit.SECONDS)).isTrue();
				if (afterHeaders) {
					assertThat(first.await(3, TimeUnit.SECONDS)).isTrue();
				}
				running.dispose();
				assertThat(released.await(2, TimeUnit.SECONDS)).isTrue();
				assertThat(model.stream(new Prompt("next")).collectList().block(WAIT)).isNotEmpty();
				assertThat(requests).hasValue(2);
			}
			finally {
				running.dispose();
			}
		}
		finally {
			server.disposeNow();
		}
	}

	@Test
	void compatibleBlockingCallsUseTheSameTransportAndKeepSdkMapping() {
		var received = new AtomicReference<String>();
		var server = server((request, response) -> {
			received.set(request.body());
			return response.header("Content-Type", "application/json").sendString(Mono.just(COMPAT_SYNC));
		});
		try (var transport = new DashScopeHttpTransport(DashScopeHttpTransport.Settings.defaults())) {
			var result = model(true, server, transport).call(new Prompt("hello"));
			assertThat(result.getResult().getOutput().getText()).isEqualTo("ok");
			assertThat(received.get()).contains("hello");
		}
		finally {
			server.disposeNow();
		}
	}

	@ParameterizedTest
	@ValueSource(booleans = { false, true })
	void opensAFreshConnectionAfterTheServerClosesAnIdleOne(boolean compatible) {
		var lastConnection = new AtomicReference<Connection>();
		var requests = new AtomicInteger();
		var server = server((request, response) -> {
			response.withConnection(lastConnection::set);
			requests.incrementAndGet();
			return response.header("Content-Type", "application/json")
					.sendString(Mono.just(compatible ? COMPAT_SYNC : NATIVE_COMPLETE));
		});
		try (var transport = new DashScopeHttpTransport(DashScopeHttpTransport.Settings.defaults())) {
			var model = model(compatible, server, transport);
			assertThat(model.call(new Prompt("first")).getResult().getOutput().getText()).isEqualTo("ok");
			var closed = lastConnection.get();
			closed.disposeNow(WAIT);
			assertThat(model.call(new Prompt("second")).getResult().getOutput().getText()).isEqualTo("ok");
			assertThat(lastConnection.get().channel()).isNotSameAs(closed.channel());
			assertThat(requests).hasValue(2);
		}
		finally {
			server.disposeNow();
		}
	}

	@ParameterizedTest
	@ValueSource(booleans = { false, true })
	void aFailedPostIsNotRetriedByTheDefaultBlockingClient(boolean compatible) {
		var requests = new AtomicInteger();
		var server = server((request, response) -> {
			requests.incrementAndGet();
			return response.status(503).header("Content-Type", "application/json")
					.sendString(Mono.just("{\"error\":{\"message\":\"unavailable\"}}"));
		});
		try (var transport = new DashScopeHttpTransport(DashScopeHttpTransport.Settings.defaults())) {
			assertThatThrownBy(() -> model(compatible, server, transport).call(new Prompt("hello")))
					.isInstanceOf(RuntimeException.class);
			assertThat(requests).hasValue(1);
		}
		finally {
			server.disposeNow();
		}
	}

	@ParameterizedTest
	@ValueSource(booleans = { false, true })
	void aDisconnectAfterTheServerReceivesThePostDoesNotReplayIt(boolean compatible) {
		var requests = new AtomicInteger();
		var server = server((request, response) -> {
			requests.incrementAndGet();
			response.withConnection(connection -> connection.dispose());
			return Mono.never();
		});
		try (var transport = new DashScopeHttpTransport(DashScopeHttpTransport.Settings.defaults())) {
			assertThatThrownBy(() -> model(compatible, server, transport).stream(new Prompt("hello")).blockLast(WAIT))
					.isInstanceOf(WebClientRequestException.class);
			assertThat(requests).hasValue(1);
		}
		finally {
			server.disposeNow();
		}
	}

	@Test
	void streamDeadlineExpiresWhileFramesContinueToArrive() {
		var server = server((request, response) -> response.sse().sendString(
				Flux.interval(Duration.ZERO, Duration.ofMillis(40)).map(tick -> event(COMPAT_DELTA))));
		try (var transport = new DashScopeHttpTransport(new DashScopeHttpTransport.Settings(1, 1,
				Duration.ofSeconds(2), Duration.ofSeconds(2), Duration.ofMinutes(1), Duration.ofSeconds(2),
				Duration.ofSeconds(2), Duration.ofSeconds(1), Duration.ofSeconds(3), Duration.ofMillis(1200)))) {
			StepVerifier.create(model(true, server, transport).stream(new Prompt("keep streaming")))
					.expectNextCount(2).thenConsumeWhile(frame -> true).expectError(TimeoutException.class).verify(WAIT);
		}
		finally {
			server.disposeNow();
		}
	}

	@Test
	void cancellingAPendingAcquireFreesItsQueueSlotAndDoesNotSendItsPost() throws Exception {
		var accepted = new CountDownLatch(1);
		var requests = new AtomicInteger();
		var cancelledPosts = new AtomicInteger();
		var server = server((request, response) -> {
			requests.incrementAndGet();
			if (request.body().contains("do not send")) {
				cancelledPosts.incrementAndGet();
			}
			if (request.body().contains("hold the connection")) {
				accepted.countDown();
				return Mono.never();
			}
			return response.sse().sendString(Mono.just(event(COMPAT_DELTA) + event("[DONE]")));
		});
		try (var transport = new DashScopeHttpTransport(new DashScopeHttpTransport.Settings(1, 1,
				Duration.ofSeconds(4), Duration.ofSeconds(10), Duration.ofMinutes(1), Duration.ofSeconds(2),
				Duration.ofSeconds(2), WAIT, WAIT, WAIT))) {
			var model = model(true, server, transport);
			var occupying = model.stream(new Prompt("hold the connection")).subscribe(value -> { }, error -> { });
			try {
				assertThat(accepted.await(3, TimeUnit.SECONDS)).isTrue();
				var pending = model.stream(new Prompt("do not send")).collectList().toFuture();
				assertThatThrownBy(() -> model.stream(new Prompt("reject")).blockLast(WAIT))
						.hasMessageContaining("Pending acquire queue");
				assertThat(requests).hasValue(1);
				assertThat(pending.cancel(true)).isTrue();
				var admitted = model.stream(new Prompt("admit")).collectList().toFuture();
				assertThatThrownBy(() -> admitted.get(100, TimeUnit.MILLISECONDS))
						.isInstanceOf(TimeoutException.class);
				occupying.dispose();
				assertThat(admitted.get(5, TimeUnit.SECONDS)).isNotEmpty();
				assertThat(requests).hasValue(2);
				assertThat(cancelledPosts).hasValue(0);
			}
			finally {
				occupying.dispose();
			}
		}
		finally {
			server.disposeNow();
		}
	}

	private static ChatModel model(boolean compatible, DisposableServer server, DashScopeHttpTransport transport) {
		String url = "http://127.0.0.1:" + server.port();
		if (compatible) {
			return DashScopeCompatibleChatModel.builder(OpenAiChatOptions.builder().apiKey("test")
					.baseUrl(url + "/compatible-mode/v1").model("qwen-plus")
					.customHeaders(Map.of("X-DashScope-Workspace", "model-workspace")).build(), transport)
					.build();
		}
		return DashScopeChatModel.builder().dashScopeApi(DashScopeApi.builder().apiKey("test").baseUrl(url)
				.webClientBuilder(transport.webClient(WebClient.builder()))
				.restClientBuilder(transport.restClient(RestClient.builder(), WAIT)).build()).build();
	}

	private static String event(String data) {
		return "data: " + data + "\n\n";
	}

	private static DisposableServer server(BiFunction<Received, HttpServerResponse, Publisher<Void>> handler) {
		return HttpServer.create().host("127.0.0.1").port(0).handle((request, response) ->
			request.receive().aggregate().asString()
					.flatMap(body -> Mono.from(handler.apply(new Received(body, request.requestHeaders()), response))))
			.bindNow(WAIT);
	}

	private record Received(String body, HttpHeaders headers) {
	}
}
