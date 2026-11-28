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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.net.URI;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.alibaba.cloud.ai.dashscope.common.DashScopeException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.openai.client.OpenAIClientAsync;
import com.openai.client.OpenAIClientImpl;
import com.openai.core.ClientOptions;
import com.openai.core.ObjectMappers;
import com.openai.core.RequestOptions;
import com.openai.core.http.AsyncStreamResponse;
import com.openai.core.http.Headers;
import com.openai.core.http.HttpClient;
import com.openai.core.http.HttpRequest;
import com.openai.core.http.HttpResponse;
import com.openai.models.chat.completions.ChatCompletionChunk;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import com.openai.services.async.ChatServiceAsync;
import com.openai.services.async.chat.ChatCompletionServiceAsync;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.Disposable;
import reactor.core.Disposables;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * Opt-in transport for the DashScope OpenAI-compatible Chat Completions endpoint.
 * Spring AI still builds the requests and maps responses, including tool calls.
 */
public final class DashScopeCompatibleChatModel {

	private static final String DONE = "[DONE]";

	private static final String DEFAULT_URL = "https://dashscope.aliyuncs.com/compatible-mode/v1";

	private DashScopeCompatibleChatModel() {
	}

	/**
	 * Sets up an OpenAI model builder with a caller-owned, non-replaying transport.
	 * The application should close the transport when the model is no longer needed.
	 */
	public static OpenAiChatModel.Builder builder(OpenAiChatOptions options, DashScopeHttpTransport transport) {
		return builder(options, transport, WebClient.builder());
	}

	public static OpenAiChatModel.Builder builder(OpenAiChatOptions options, DashScopeHttpTransport transport,
			WebClient.Builder webClientBuilder) {
		String apiKey = Objects.requireNonNull(options.getApiKey(), "DashScope API key is required");
		String baseUrl = Objects.requireNonNullElse(options.getBaseUrl(), DEFAULT_URL).replaceFirst("/+$", "");
		var customHeaders = options.getCustomHeaders() == null ? Map.<String, String>of()
				: Map.copyOf(options.getCustomHeaders());
		var webClient = transport.webClient(webClientBuilder).build();
		var sdkBuilder = ClientOptions.builder().apiKey(apiKey).baseUrl(baseUrl).maxRetries(0)
				.timeout(transport.requestTimeout()).httpClient(new SyncClient(webClient, transport.requestTimeout()));
		customHeaders.forEach(sdkBuilder::putHeader);
		var client = new OpenAIClientImpl(sdkBuilder.build());
		URI completions = URI.create(baseUrl + "/chat/completions");
		var async = streamingClient(client.async(), (params, request) -> {
			var stream = webClient.post().uri(completions).accept(MediaType.TEXT_EVENT_STREAM)
					.contentType(MediaType.APPLICATION_JSON)
					.headers(headers -> {
						headers.setBearerAuth(apiKey);
						customHeaders.forEach(headers::set);
						params._headers().asMap().forEach(headers::put);
					})
					.bodyValue(streamBody(params))
					.retrieve().bodyToFlux(String.class);
			return new StreamingResponse(DashScopeHttpTransport.withDeadline(
					decode(stream, Boolean.TRUE.equals(request.getResponseValidation())),
					deadline(request, transport.requestTimeout()), transport.firstChunkTimeout()));
		});
		return OpenAiChatModel.builder().options(options).openAiClient(client).openAiClientAsync(async);
	}

	private static String streamBody(ChatCompletionCreateParams params) {
		var json = ObjectMappers.jsonMapper();
		ObjectNode body = json.valueToTree(params._body());
		body.put("stream", true);
		return body.toString();
	}

	private static Flux<ChatCompletionChunk> decode(Flux<String> frames, boolean validateResponse) {
		return Flux.defer(() -> {
			var completed = new AtomicBoolean();
			var received = new AtomicBoolean();
			return frames.takeUntil(DONE::equals)
					.doOnNext(frame -> {
						if (DONE.equals(frame)) {
							completed.set(true);
						}
					})
					.filter(frame -> !DONE.equals(frame))
					.map(frame -> {
						try {
							var json = ObjectMappers.jsonMapper();
							var data = json.readTree(frame);
							if (data.hasNonNull("error")) {
								throw new DashScopeException("DashScope compatible stream returned a provider error");
							}
							var chunk = json.treeToValue(data, ChatCompletionChunk.class);
							if (validateResponse) {
								chunk.validate();
							}
							received.set(true);
							return chunk;
						}
						catch (JsonProcessingException error) {
							throw new DashScopeException("Invalid DashScope compatible stream frame", error);
						}
					})
					.concatWith(Flux.defer(() -> completed.get() && received.get() ? Flux.empty()
							: Flux.error(new DashScopeStreamTruncatedException(
									"DashScope compatible chat stream ended without [DONE] and response data"))));
		});
	}

	private static Duration deadline(RequestOptions options, Duration budget) {
		var timeout = options.getTimeout();
		return timeout != null && !timeout.request().isZero() && timeout.request().compareTo(budget) < 0
				? timeout.request() : budget;
	}

	// Keep the SDK's blocking client and generated service APIs. Replace the single
	// asynchronous service method used by OpenAiChatModel for streaming.
	private static OpenAIClientAsync streamingClient(OpenAIClientAsync delegate,
			BiFunction<ChatCompletionCreateParams, RequestOptions, AsyncStreamResponse<ChatCompletionChunk>> stream) {
		var chat = delegate.chat();
		var completions = intercept(ChatCompletionServiceAsync.class, chat.completions(), "createStreaming",
				args -> args[0] instanceof ChatCompletionCreateParams params
						? stream.apply(params, args.length == 2 ? (RequestOptions) args[1] : RequestOptions.none())
						: null);
		var service = intercept(ChatServiceAsync.class, chat, "completions", args -> completions);
		return intercept(OpenAIClientAsync.class, delegate, "chat", args -> service);
	}

	private static <T> T intercept(Class<T> api, T delegate, String name,
			Function<Object[], @Nullable Object> replacement) {
		return api.cast(Proxy.newProxyInstance(api.getClassLoader(), new Class<?>[] { api }, (proxy, method, args) -> {
			if (method.getName().equals(name)) {
				var result = replacement.apply(args);
				if (result != null) {
					return result;
				}
			}
			try {
				return method.invoke(delegate, args);
			}
			catch (InvocationTargetException error) {
				throw error.getCause();
			}
		}));
	}

	private static final class StreamingResponse implements AsyncStreamResponse<ChatCompletionChunk> {

		private final Flux<ChatCompletionChunk> source;
		private final Disposable.Swap active = Disposables.swap();
		private final CompletableFuture<Void> completion = new CompletableFuture<>();
		private final AtomicBoolean subscribed = new AtomicBoolean();

		private StreamingResponse(Flux<ChatCompletionChunk> source) {
			this.source = source;
		}

		@Override
		public AsyncStreamResponse<ChatCompletionChunk> subscribe(Handler<? super ChatCompletionChunk> handler) {
			return subscribeTo(this.source, handler);
		}

		@Override
		public AsyncStreamResponse<ChatCompletionChunk> subscribe(Handler<? super ChatCompletionChunk> handler,
				Executor executor) {
			return subscribeTo(this.source.publishOn(Schedulers.fromExecutor(executor), 1), handler);
		}

		private AsyncStreamResponse<ChatCompletionChunk> subscribeTo(Flux<ChatCompletionChunk> flux,
				Handler<? super ChatCompletionChunk> handler) {
			if (!this.subscribed.compareAndSet(false, true)) {
				throw new IllegalStateException("DashScope streaming response can only be subscribed once");
			}
			var subscriber = new BaseSubscriber<ChatCompletionChunk>() {
				@Override
				protected void hookOnNext(ChatCompletionChunk value) {
					handler.onNext(value);
				}

				@Override
				protected void hookOnComplete() {
					try {
						handler.onComplete(Optional.empty());
						completion.complete(null);
					}
					catch (RuntimeException error) {
						completion.completeExceptionally(error);
					}
				}

				@Override
				protected void hookOnError(Throwable error) {
					try {
						handler.onComplete(Optional.of(error));
					}
					finally {
						completion.completeExceptionally(error);
					}
				}
			};
			if (this.active.update(subscriber)) {
				flux.subscribe(subscriber);
			}
			return this;
		}

		@Override
		public CompletableFuture<Void> onCompleteFuture() {
			return this.completion;
		}

		@Override
		public void close() {
			this.active.dispose();
			this.completion.complete(null);
		}
	}

	private record SyncClient(WebClient webClient, Duration budget) implements HttpClient {

		@Override
		public HttpResponse execute(HttpRequest request, RequestOptions options) {
			return Objects.requireNonNull(exchange(request, options).block());
		}

		@Override
		public CompletableFuture<HttpResponse> executeAsync(HttpRequest request, RequestOptions options) {
			return exchange(request, options).toFuture();
		}

		private Mono<HttpResponse> exchange(HttpRequest request, RequestOptions options) {
			return Mono.defer(() -> {
				var spec = this.webClient.method(HttpMethod.valueOf(request.method().name())).uri(URI.create(request.url()))
						.headers(headers -> request.headers().asMap().forEach(headers::put));
				var body = request.body();
				if (body != null) {
					var bytes = new ByteArrayOutputStream();
					try {
						body.writeTo(bytes);
					}
					finally {
						body.close();
					}
					if (body.contentType() != null) {
						spec.contentType(MediaType.parseMediaType(body.contentType()));
					}
					spec.bodyValue(bytes.toByteArray());
				}
				return spec.<HttpResponse>exchangeToMono(response -> {
					var headerBuilder = Headers.builder();
					response.headers().asHttpHeaders().forEach(headerBuilder::put);
					var headers = headerBuilder.build();
					return response.bodyToMono(byte[].class).defaultIfEmpty(new byte[0])
							.map(bytes -> new BufferedResponse(response.statusCode().value(), headers, bytes));
				});
			}).timeout(deadline(options, this.budget));
		}

		@Override
		public void close() {
			// The caller owns the shared DashScopeHttpTransport.
		}
	}

	private record BufferedResponse(int statusCode, Headers headers, byte[] bytes) implements HttpResponse {

		@Override
		public ByteArrayInputStream body() {
			return new ByteArrayInputStream(this.bytes);
		}

		@Override
		public void close() {
			// The connection was released while buffering the response.
		}
	}
}
