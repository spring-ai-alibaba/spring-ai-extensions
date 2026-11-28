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

import java.net.SocketAddress;
import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

import io.netty.channel.ChannelOption;
import io.netty.resolver.AddressResolverGroup;
import org.jspecify.annotations.Nullable;
import org.springframework.http.client.ReactorClientHttpRequestFactory;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.netty.Connection;
import reactor.netty.ConnectionObserver;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;
import reactor.netty.transport.TransportConfig;

/** Owns the connection pool shared by one model's synchronous and streaming HTTP clients. */
public final class DashScopeHttpTransport implements AutoCloseable {

	private static final Object ACQUIRE_CANCELLATION = new Object();

	private final ConnectionProvider pool;
	private final HttpClient httpClient;
	private final Settings settings;

	public DashScopeHttpTransport(Settings settings) {
		this.settings = Objects.requireNonNull(settings);
		this.pool = ConnectionProvider.builder("dashscope-model")
				.maxConnections(settings.maxConnections())
				.pendingAcquireMaxCount(settings.pendingAcquireMaxCount())
				.pendingAcquireTimeout(settings.acquireTimeout())
				.maxIdleTime(settings.maxIdleTime())
				.maxLifeTime(settings.maxLifeTime())
				.evictInBackground(settings.evictionInterval())
				.lifo()
				.build();
		this.httpClient = HttpClient.create(new CancellableAcquire(this.pool)).compress(true).disableRetry(true)
				.mapConnect(connection -> Mono.defer(() -> {
					var cancelled = Sinks.<Void>empty();
					return connection.contextWrite(context -> context.put(ACQUIRE_CANCELLATION, cancelled.asMono()))
							.doOnCancel(cancelled::tryEmitEmpty);
				}))
				.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, Math.toIntExact(settings.connectTimeout().toMillis()));
	}

	public RestClient.Builder restClient(RestClient.Builder builder, Duration readTimeout) {
		var factory = new ReactorClientHttpRequestFactory(this.httpClient.responseTimeout(readTimeout));
		factory.setConnectTimeout(this.settings.connectTimeout());
		factory.setReadTimeout(readTimeout);
		return builder.clone().requestFactory(factory);
	}

	public WebClient.Builder webClient(WebClient.Builder builder) {
		return builder.clone().clientConnector(new ReactorClientHttpConnector(
				this.httpClient.responseTimeout(this.settings.streamReadTimeout())));
	}

	public Duration requestTimeout() {
		return this.settings.requestTimeout();
	}

	public Duration firstChunkTimeout() {
		return this.settings.firstChunkTimeout();
	}

	/** A single attempt deadline, including pool admission and all response data. */
	static <T> Flux<T> withDeadline(Flux<T> source, Duration budget, Duration firstChunkTimeout) {
		return Flux.defer(() -> {
			long started = System.nanoTime();
			Supplier<Mono<Long>> remaining = () -> Mono.delay(Duration.ofNanos(
					Math.max(0, budget.toNanos() - (System.nanoTime() - started))));
			Duration first = firstChunkTimeout.compareTo(budget) < 0 ? firstChunkTimeout : budget;
			return source.timeout(Mono.delay(first), ignored -> remaining.get());
		});
	}

	@Override
	public void close() {
		this.pool.dispose();
	}

	// Netty's HTTP connection Mono can be cancelled before it attaches its
	// connection handle. Carry cancellation to the pending pool acquire itself.
	private record CancellableAcquire(ConnectionProvider pool) implements ConnectionProvider {

		@Override
		public Mono<? extends Connection> acquire(TransportConfig config, ConnectionObserver observer,
				@Nullable Supplier<? extends SocketAddress> address, @Nullable AddressResolverGroup<?> resolver) {
			return Mono.deferContextual(context -> {
				var acquire = this.pool.acquire(config, observer, address, resolver);
				var cancellation = context.<Mono<Void>>getOrEmpty(ACQUIRE_CANCELLATION);
				return cancellation.isPresent() ? acquire.takeUntilOther(cancellation.get()) : acquire;
			});
		}

		@Override
		public @Nullable String name() {
			return this.pool.name();
		}

		@Override
		public int maxConnections() {
			return this.pool.maxConnections();
		}

		@Override
		public @Nullable Map<SocketAddress, Integer> maxConnectionsPerHost() {
			return this.pool.maxConnectionsPerHost();
		}

		@Override
		public void disposeWhen(SocketAddress address) {
			this.pool.disposeWhen(address);
		}

		@Override
		public Mono<Void> disposeLater() {
			return this.pool.disposeLater();
		}

		@Override
		public boolean isDisposed() {
			return this.pool.isDisposed();
		}
	}

	public record Settings(int maxConnections, int pendingAcquireMaxCount, Duration acquireTimeout,
			Duration maxIdleTime, Duration maxLifeTime, Duration evictionInterval, Duration connectTimeout,
			Duration firstChunkTimeout, Duration streamReadTimeout, Duration requestTimeout) {

		public Settings {
			if (maxConnections < 1 || pendingAcquireMaxCount < 1) {
				throw new IllegalArgumentException("DashScope connection and queue limits must be positive");
			}
			for (Duration duration : new Duration[] { acquireTimeout, maxIdleTime, maxLifeTime,
					evictionInterval, connectTimeout, firstChunkTimeout, streamReadTimeout, requestTimeout }) {
				if (duration == null || duration.toMillis() < 1) {
					throw new IllegalArgumentException("DashScope HTTP durations must be at least one millisecond");
				}
			}
			Math.toIntExact(connectTimeout.toMillis());
			requestTimeout.toNanos();
		}

		public static Settings defaults() {
			return new Settings(64, 64, Duration.ofSeconds(2), Duration.ofSeconds(30), Duration.ofMinutes(10),
					Duration.ofSeconds(15), Duration.ofSeconds(10), Duration.ofSeconds(60), Duration.ofSeconds(60),
					Duration.ofMinutes(5));
		}
	}
}
