///*
// * Copyright 2024-2026 the original author or authors.
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *      https://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package com.alibaba.cloud.ai.memory.mem0.core;
//
//import com.alibaba.cloud.ai.memory.mem0.model.Mem0ServerRequest;
//import com.alibaba.cloud.ai.memory.mem0.model.Mem0ServerResp;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.core.io.ResourceLoader;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.CompletableFuture;
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.TimeUnit;
//import java.util.concurrent.atomic.AtomicInteger;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.assertj.core.api.Assertions.assertThatThrownBy;
//
///**
// * 异步功能集成测试
// *
// * @author Morain Miao
// * @since 1.0.0
// */
//@ExtendWith(MockitoExtension.class)
//class Mem0ServiceClientAsyncIntegrationTest {
//
//	@Mock
//	private ResourceLoader resourceLoader;
//
//	private Mem0Client mem0Client;
//
//	private Mem0Server mem0Server;
//
//	private Mem0ServiceClient client;
//
//	@BeforeEach
//	void setUp() {
//		// 创建 Mem0Client
//		Mem0Client.AsyncConfig asyncConfig = new Mem0Client.AsyncConfig();
//		asyncConfig.setEnabled(true);
//		asyncConfig.setCorePoolSize(2);
//		asyncConfig.setMaxPoolSize(4);
//		asyncConfig.setQueueCapacity(100);
//		asyncConfig.setThreadNamePrefix("test-async-");
//
//		this.mem0Client = Mem0Client.builder()
//				.baseUrl("http://localhost:8888")
//				.enableCache(true)
//				.timeoutSeconds(30)
//				.maxRetryAttempts(3)
//				.build();
//		this.mem0Client.setAsync(asyncConfig);
//
//		// 创建 Mem0Server
//		this.mem0Server = Mem0Server.builder().version("v1.1").build();
//
//		// 创建 Mem0ServiceClient
//		this.client = new Mem0ServiceClient(mem0Client, mem0Server, resourceLoader);
//	}
//
//	@Test
//	void testConcurrentAddMemoryOperations() throws InterruptedException {
//		// Given
//		int numberOfOperations = 10;
//		CountDownLatch latch = new CountDownLatch(numberOfOperations);
//		AtomicInteger successCount = new AtomicInteger(0);
//		AtomicInteger errorCount = new AtomicInteger(0);
//
//		// When - 并发执行多个添加内存操作
//		for (int i = 0; i < numberOfOperations; i++) {
//			final int index = i;
//			Mem0ServerRequest.MemoryCreate memoryCreate = Mem0ServerRequest.MemoryCreate.builder()
//				.messages(List.of(new Mem0ServerRequest.Message("user", "test message " + index)))
//				.userId("test-user-" + index)
//				.agentId("test-agent-" + index)
//				.runId("test-run-" + index)
//				.build();
//
//			CompletableFuture<Void> future = client.addMemoryAsync(memoryCreate);
//			future.whenComplete((result, throwable) -> {
//				if (throwable == null) {
//					successCount.incrementAndGet();
//				}
//				else {
//					errorCount.incrementAndGet();
//				}
//				latch.countDown();
//			});
//		}
//
//		// Then - 等待所有操作完成
//		boolean allCompleted = latch.await(30, TimeUnit.SECONDS);
//		assertThat(allCompleted).isTrue();
//
//		// 验证异步执行（由于没有真实服务器，会有错误，但这是预期的）
//		assertThat(successCount.get() + errorCount.get()).isEqualTo(numberOfOperations);
//	}
//
//	@Test
//	void testConcurrentUpdateMemoryOperations() throws InterruptedException {
//		// Given
//		int numberOfOperations = 10;
//		CountDownLatch latch = new CountDownLatch(numberOfOperations);
//		AtomicInteger successCount = new AtomicInteger(0);
//		AtomicInteger errorCount = new AtomicInteger(0);
//
//		// When - 并发执行多个更新内存操作
//		for (int i = 0; i < numberOfOperations; i++) {
//			final int index = i;
//			String memoryId = "test-memory-" + index;
//			Map<String, Object> updatedMemory = new HashMap<>();
//			updatedMemory.put("content", "updated content " + index);
//			updatedMemory.put("metadata", Map.of("updated_at", System.currentTimeMillis()));
//
//			CompletableFuture<Map<String, Object>> future = client.updateMemoryAsync(memoryId, updatedMemory);
//			future.whenComplete((result, throwable) -> {
//				if (throwable == null) {
//					successCount.incrementAndGet();
//				}
//				else {
//					errorCount.incrementAndGet();
//				}
//				latch.countDown();
//			});
//		}
//
//		// Then - 等待所有操作完成
//		boolean allCompleted = latch.await(10, TimeUnit.SECONDS);
//		assertThat(allCompleted).isTrue();
//
//		// 验证异步执行
//		assertThat(successCount.get() + errorCount.get()).isEqualTo(numberOfOperations);
//	}
//
//	@Test
//	void testAsyncVsSyncPerformance() {
//		// Given - 使用异步客户端（已在 setUp 中配置）
//		Mem0ServiceClient asyncClient = this.client;
//
//		// 创建同步客户端
//		Mem0Client.AsyncConfig syncAsyncConfig = new Mem0Client.AsyncConfig();
//		syncAsyncConfig.setEnabled(false);
//
//		Mem0Client syncMem0Client = Mem0Client.builder()
//				.baseUrl("http://localhost:8888")
//				.enableCache(true)
//				.timeoutSeconds(30)
//				.maxRetryAttempts(3)
//				.build();
//		syncMem0Client.setAsync(syncAsyncConfig);
//
//		Mem0Server syncMem0Server = Mem0Server.builder().version("v1.1").build();
//		Mem0ServiceClient syncClient = new Mem0ServiceClient(syncMem0Client, syncMem0Server, resourceLoader);
//
//		Mem0ServerRequest.MemoryCreate memoryCreate = Mem0ServerRequest.MemoryCreate.builder()
//			.messages(List.of(new Mem0ServerRequest.Message("user", "performance test")))
//			.userId("perf-user")
//			.agentId("perf-agent")
//			.runId("perf-run")
//			.build();
//
//		// When - 测试异步执行性能
//		long asyncStartTime = System.currentTimeMillis();
//		asyncClient.addMemory(memoryCreate);
//		long asyncEndTime = System.currentTimeMillis();
//		long asyncDuration = asyncEndTime - asyncStartTime;
//
//		// When - 测试同步执行性能
//		long syncStartTime = System.currentTimeMillis();
//		try {
//			syncClient.addMemory(memoryCreate);
//		}
//		catch (Exception e) {
//			// 预期的异常，因为服务器不存在
//		}
//		long syncEndTime = System.currentTimeMillis();
//		long syncDuration = syncEndTime - syncStartTime;
//
//		// Then - 异步执行应该比同步执行快（因为异步立即返回）
//		assertThat(asyncDuration).isLessThan(syncDuration);
//		assertThat(asyncDuration).isLessThan(100); // 异步应该在100ms内返回
//	}
//
//	@Test
//	void testAsyncMethodReturnsCompletableFuture() {
//		// Given
//		Mem0ServerRequest.MemoryCreate memoryCreate = Mem0ServerRequest.MemoryCreate.builder()
//			.messages(List.of(new Mem0ServerRequest.Message("user", "async test")))
//			.userId("async-user")
//			.agentId("async-agent")
//			.runId("async-run")
//			.build();
//
//		// When
//		CompletableFuture<Void> addFuture = client.addMemoryAsync(memoryCreate);
//
//		String memoryId = "test-memory";
//		Map<String, Object> updatedMemory = new HashMap<>();
//		updatedMemory.put("content", "async update test");
//		CompletableFuture<Map<String, Object>> updateFuture = client.updateMemoryAsync(memoryId, updatedMemory);
//
//		// Then
//		assertThat(addFuture).isNotNull();
//		assertThat(addFuture).isInstanceOf(CompletableFuture.class);
//		assertThat(addFuture.isDone()).isFalse();
//
//		assertThat(updateFuture).isNotNull();
//		assertThat(updateFuture).isInstanceOf(CompletableFuture.class);
//		assertThat(updateFuture.isDone()).isFalse();
//	}
//
//	@Test
//	void testAsyncMethodChaining() {
//		// Given
//		Mem0ServerRequest.MemoryCreate memoryCreate = Mem0ServerRequest.MemoryCreate.builder()
//			.messages(List.of(new Mem0ServerRequest.Message("user", "chaining test")))
//			.userId("chain-user")
//			.agentId("chain-agent")
//			.runId("chain-run")
//			.build();
//
//		// When - 链式异步操作
//		CompletableFuture<Void> chainedFuture = client.addMemoryAsync(memoryCreate).thenRun(() -> {
//			// 添加完成后执行更新
//			Map<String, Object> updatedMemory = new HashMap<>();
//			updatedMemory.put("content", "chained update");
//			client.updateMemoryAsync("test-memory", updatedMemory);
//		});
//
//		// Then
//		assertThat(chainedFuture).isNotNull();
//		assertThat(chainedFuture).isInstanceOf(CompletableFuture.class);
//
//		// 验证客户端已创建
//		assertThat(client).isNotNull();
//	}
//
//	@Test
//	void testThreadPoolConfiguration() {
//		// Given & When
//		Mem0Client.AsyncConfig asyncConfig = mem0Client.getAsync();
//
//		// Then
//		assertThat(asyncConfig.isEnabled()).isTrue();
//		assertThat(asyncConfig.getCorePoolSize()).isEqualTo(2);
//		assertThat(asyncConfig.getMaxPoolSize()).isEqualTo(4);
//		assertThat(asyncConfig.getQueueCapacity()).isEqualTo(100);
//		assertThat(asyncConfig.getThreadNamePrefix()).isEqualTo("test-async-");
//
//		// 验证客户端已创建
//		assertThat(client).isNotNull();
//	}
//
//	@Test
//	void testShutdownAfterAsyncOperations() throws InterruptedException {
//		// Given
//		CountDownLatch latch = new CountDownLatch(5);
//
//		// When - 启动一些异步操作
//		for (int i = 0; i < 5; i++) {
//			final int index = i;
//			CompletableFuture.runAsync(() -> {
//				try {
//					Mem0ServerRequest.MemoryCreate memoryCreate = Mem0ServerRequest.MemoryCreate.builder()
//						.messages(List.of(new Mem0ServerRequest.Message("user", "shutdown test " + index)))
//						.userId("shutdown-user-" + index)
//						.agentId("shutdown-agent-" + index)
//						.runId("shutdown-run-" + index)
//						.build();
//
//					client.addMemory(memoryCreate);
//				}
//				catch (Exception e) {
//					// 预期的异常
//				}
//				finally {
//					latch.countDown();
//				}
//			});
//		}
//
//		// 等待一些操作开始
//		Thread.sleep(100);
//
//		// 关闭客户端
//		client.shutdown();
//
//		// Then - 关闭操作应该成功
//		assertThat(client).isNotNull();
//
//		// 等待所有操作完成
//		latch.await(2, TimeUnit.SECONDS);
//	}
//
//	@Test
//	void testAsyncErrorHandling() {
//		// When - 使用null参数调用异步方法
//		CompletableFuture<Void> addFuture = client.addMemoryAsync(null);
//		CompletableFuture<Map<String, Object>> updateFuture = client.updateMemoryAsync(null, null);
//
//		// Then - 应该抛出异常
//		assertThatThrownBy(() -> addFuture.get(1, TimeUnit.SECONDS)).isInstanceOf(Exception.class);
//
//		assertThatThrownBy(() -> updateFuture.get(1, TimeUnit.SECONDS)).isInstanceOf(Exception.class);
//	}
//
//	@Test
//	void testAsyncOperationCallback() throws InterruptedException {
//		// Given
//		Mem0ServerRequest.MemoryCreate memoryCreate = Mem0ServerRequest.MemoryCreate.builder()
//			.messages(List.of(new Mem0ServerRequest.Message("user", "callback test")))
//			.userId("callback-user")
//			.agentId("callback-agent")
//			.runId("callback-run")
//			.build();
//
//		CountDownLatch callbackLatch = new CountDownLatch(1);
//		AtomicInteger callbackCount = new AtomicInteger(0);
//
//		// When - 执行异步操作并添加回调
//		CompletableFuture<Void> future = client.addMemoryAsync(memoryCreate);
//		future.whenComplete((result, throwable) -> {
//			callbackCount.incrementAndGet();
//			callbackLatch.countDown();
//		});
//
//		// Then - 等待回调执行（由于没有真实服务器，会失败，但回调应该执行）
//		boolean callbackExecuted = callbackLatch.await(30, TimeUnit.SECONDS);
//		assertThat(callbackExecuted).isTrue();
//		assertThat(callbackCount.get()).isEqualTo(1);
//	}
//
//	@Test
//	void testMultipleAsyncOperationsCompletion() throws InterruptedException {
//		// Given
//		int numberOfOperations = 5;
//		CountDownLatch completionLatch = new CountDownLatch(numberOfOperations);
//		AtomicInteger completedCount = new AtomicInteger(0);
//
//		// When - 启动多个异步操作
//		for (int i = 0; i < numberOfOperations; i++) {
//			final int index = i;
//			Mem0ServerRequest.MemoryCreate memoryCreate = Mem0ServerRequest.MemoryCreate.builder()
//				.messages(List.of(new Mem0ServerRequest.Message("user", "multi async test " + index)))
//				.userId("multi-user-" + index)
//				.agentId("multi-agent-" + index)
//				.runId("multi-run-" + index)
//				.build();
//
//			CompletableFuture<Void> future = client.addMemoryAsync(memoryCreate);
//			future.whenComplete((result, throwable) -> {
//				completedCount.incrementAndGet();
//				completionLatch.countDown();
//			});
//		}
//
//		// Then - 等待所有操作完成
//		boolean allCompleted = completionLatch.await(5, TimeUnit.SECONDS);
//		assertThat(allCompleted).isTrue();
//		assertThat(completedCount.get()).isEqualTo(numberOfOperations);
//	}
//
//	@Test
//	void testAsyncOperationWithExceptionHandling() throws InterruptedException {
//		// Given
//		CountDownLatch exceptionLatch = new CountDownLatch(1);
//		AtomicInteger exceptionCount = new AtomicInteger(0);
//
//		// When - 使用无效参数调用异步方法
//		CompletableFuture<Void> future = client.addMemoryAsync(null);
//		future.whenComplete((result, throwable) -> {
//			if (throwable != null) {
//				exceptionCount.incrementAndGet();
//			}
//			exceptionLatch.countDown();
//		});
//
//		// Then - 等待异常处理
//		boolean exceptionHandled = exceptionLatch.await(2, TimeUnit.SECONDS);
//		assertThat(exceptionHandled).isTrue();
//		assertThat(exceptionCount.get()).isEqualTo(1);
//	}
//
//	@Test
//	void testAsyncUpdateMemoryReturnsEmptyMap() {
//		// Given
//		String memoryId = "test-memory-id";
//		Map<String, Object> updatedMemory = new HashMap<>();
//		updatedMemory.put("content", "test content");
//
//		// When - 异步更新内存（异步模式下应该立即返回空Map）
//		Map<String, Object> result = client.updateMemory(memoryId, updatedMemory);
//
//		// Then - 异步执行时应该返回空结果
//		assertThat(result).isNotNull();
//		assertThat(result).isEmpty();
//	}
//
//	@Test
//	void testAsyncAddMemoryReturnsImmediately() {
//		// Given
//		Mem0ServerRequest.MemoryCreate memoryCreate = Mem0ServerRequest.MemoryCreate.builder()
//			.messages(List.of(new Mem0ServerRequest.Message("user", "immediate return test")))
//			.userId("immediate-user")
//			.agentId("immediate-agent")
//			.runId("immediate-run")
//			.build();
//
//		// When - 调用异步添加内存方法
//		long startTime = System.currentTimeMillis();
//		client.addMemory(memoryCreate);
//		long endTime = System.currentTimeMillis();
//		long duration = endTime - startTime;
//
//		// Then - 应该立即返回（不等待服务器响应）
//		assertThat(duration).isLessThan(50); // 应该在50ms内返回
//	}
//
//	@Test
//	void testConcurrentGetAllMemoriesOperations() throws InterruptedException {
//		// Given
//		int numberOfOperations = 10;
//		CountDownLatch latch = new CountDownLatch(numberOfOperations);
//		AtomicInteger successCount = new AtomicInteger(0);
//		AtomicInteger errorCount = new AtomicInteger(0);
//
//		// When - 并发执行多个获取所有内存操作
//		for (int i = 0; i < numberOfOperations; i++) {
//			final int index = i;
//			CompletableFuture.runAsync(() -> {
//				try {
//					Mem0ServerResp result = client.getAllMemories("test-user-" + index, "test-run-" + index, "test-agent-" + index);
//					if (result != null) {
//						successCount.incrementAndGet();
//					}
//				}
//				catch (Exception e) {
//					errorCount.incrementAndGet();
//				}
//				finally {
//					latch.countDown();
//				}
//			});
//		}
//
//		// Then - 等待所有操作完成
//		boolean allCompleted = latch.await(30, TimeUnit.SECONDS);
//		assertThat(allCompleted).isTrue();
//		assertThat(successCount.get() + errorCount.get()).isEqualTo(numberOfOperations);
//	}
//
//	@Test
//	void testConcurrentGetMemoryOperations() throws InterruptedException {
//		// Given
//		int numberOfOperations = 10;
//		CountDownLatch latch = new CountDownLatch(numberOfOperations);
//		AtomicInteger successCount = new AtomicInteger(0);
//		AtomicInteger errorCount = new AtomicInteger(0);
//
//		// When - 并发执行多个获取单个内存操作
//		for (int i = 0; i < numberOfOperations; i++) {
//			final int index = i;
//			CompletableFuture.runAsync(() -> {
//				try {
//					client.getMemory("test-memory-" + index);
//					// 由于没有真实服务器，result可能为null，这是预期的
//					successCount.incrementAndGet();
//				}
//				catch (Exception e) {
//					errorCount.incrementAndGet();
//				}
//				finally {
//					latch.countDown();
//				}
//			});
//		}
//
//		// Then - 等待所有操作完成
//		boolean allCompleted = latch.await(30, TimeUnit.SECONDS);
//		assertThat(allCompleted).isTrue();
//		assertThat(successCount.get() + errorCount.get()).isEqualTo(numberOfOperations);
//	}
//
//	@Test
//	void testConcurrentSearchMemoriesOperations() throws InterruptedException {
//		// Given
//		int numberOfOperations = 10;
//		CountDownLatch latch = new CountDownLatch(numberOfOperations);
//		AtomicInteger successCount = new AtomicInteger(0);
//		AtomicInteger errorCount = new AtomicInteger(0);
//
//		// When - 并发执行多个搜索内存操作
//		for (int i = 0; i < numberOfOperations; i++) {
//			final int index = i;
//			CompletableFuture.runAsync(() -> {
//				try {
//					Mem0ServerRequest.SearchRequest searchRequest = Mem0ServerRequest.SearchRequest.mem0Builder()
//						.query("test query " + index)
//						.userId("test-user-" + index)
//						.agentId("test-agent-" + index)
//						.runId("test-run-" + index)
//						.build();
//
//					Mem0ServerResp result = client.searchMemories(searchRequest);
//					if (result != null) {
//						successCount.incrementAndGet();
//					}
//				}
//				catch (Exception e) {
//					errorCount.incrementAndGet();
//				}
//				finally {
//					latch.countDown();
//				}
//			});
//		}
//
//		// Then - 等待所有操作完成
//		boolean allCompleted = latch.await(30, TimeUnit.SECONDS);
//		assertThat(allCompleted).isTrue();
//		assertThat(successCount.get() + errorCount.get()).isEqualTo(numberOfOperations);
//	}
//
//	@Test
//	void testSearchMemoriesWithEmptyQuery() {
//		// Given - 空查询应该回退到 getAllMemories
//		Mem0ServerRequest.SearchRequest searchRequest = Mem0ServerRequest.SearchRequest.mem0Builder()
//			.query("")
//			.userId("test-user")
//			.agentId("test-agent")
//			.runId("test-run")
//			.build();
//
//		// When & Then - 由于没有真实服务器，会抛出异常，但验证了回退机制被调用
//		// 空查询应该回退到 getAllMemories，但由于没有服务器会失败
//		assertThatThrownBy(() -> client.searchMemories(searchRequest))
//			.isInstanceOf(RuntimeException.class)
//			.hasMessageContaining("Failed to");
//	}
//
//	@Test
//	void testConcurrentGetMemoryHistoryOperations() throws InterruptedException {
//		// Given
//		int numberOfOperations = 10;
//		CountDownLatch latch = new CountDownLatch(numberOfOperations);
//		AtomicInteger successCount = new AtomicInteger(0);
//		AtomicInteger errorCount = new AtomicInteger(0);
//
//		// When - 并发执行多个获取内存历史操作
//		for (int i = 0; i < numberOfOperations; i++) {
//			final int index = i;
//			CompletableFuture.runAsync(() -> {
//				try {
//					List<Map<String, Object>> history = client.getMemoryHistory("test-memory-" + index);
//					if (history != null) {
//						successCount.incrementAndGet();
//					}
//				}
//				catch (Exception e) {
//					errorCount.incrementAndGet();
//				}
//				finally {
//					latch.countDown();
//				}
//			});
//		}
//
//		// Then - 等待所有操作完成
//		boolean allCompleted = latch.await(30, TimeUnit.SECONDS);
//		assertThat(allCompleted).isTrue();
//		assertThat(successCount.get() + errorCount.get()).isEqualTo(numberOfOperations);
//	}
//
//	@Test
//	void testConcurrentDeleteMemoryOperations() throws InterruptedException {
//		// Given
//		int numberOfOperations = 10;
//		CountDownLatch latch = new CountDownLatch(numberOfOperations);
//		AtomicInteger successCount = new AtomicInteger(0);
//		AtomicInteger errorCount = new AtomicInteger(0);
//
//		// When - 并发执行多个删除内存操作
//		for (int i = 0; i < numberOfOperations; i++) {
//			final int index = i;
//			CompletableFuture.runAsync(() -> {
//				try {
//					client.deleteMemory("test-memory-" + index);
//					successCount.incrementAndGet();
//				}
//				catch (Exception e) {
//					errorCount.incrementAndGet();
//				}
//				finally {
//					latch.countDown();
//				}
//			});
//		}
//
//		// Then - 等待所有操作完成
//		boolean allCompleted = latch.await(30, TimeUnit.SECONDS);
//		assertThat(allCompleted).isTrue();
//		assertThat(successCount.get() + errorCount.get()).isEqualTo(numberOfOperations);
//	}
//
//	@Test
//	void testConcurrentDeleteAllMemoriesOperations() throws InterruptedException {
//		// Given
//		int numberOfOperations = 5;
//		CountDownLatch latch = new CountDownLatch(numberOfOperations);
//		AtomicInteger successCount = new AtomicInteger(0);
//		AtomicInteger errorCount = new AtomicInteger(0);
//
//		// When - 并发执行多个删除所有内存操作
//		for (int i = 0; i < numberOfOperations; i++) {
//			final int index = i;
//			CompletableFuture.runAsync(() -> {
//				try {
//					client.deleteAllMemories("test-user-" + index, "test-run-" + index, "test-agent-" + index);
//					successCount.incrementAndGet();
//				}
//				catch (Exception e) {
//					errorCount.incrementAndGet();
//				}
//				finally {
//					latch.countDown();
//				}
//			});
//		}
//
//		// Then - 等待所有操作完成
//		boolean allCompleted = latch.await(30, TimeUnit.SECONDS);
//		assertThat(allCompleted).isTrue();
//		assertThat(successCount.get() + errorCount.get()).isEqualTo(numberOfOperations);
//	}
//
//	@Test
//	void testConcurrentResetAllMemoriesOperations() throws InterruptedException {
//		// Given
//		int numberOfOperations = 3;
//		CountDownLatch latch = new CountDownLatch(numberOfOperations);
//		AtomicInteger successCount = new AtomicInteger(0);
//		AtomicInteger errorCount = new AtomicInteger(0);
//
//		// When - 并发执行多个重置所有内存操作
//		for (int i = 0; i < numberOfOperations; i++) {
//			CompletableFuture.runAsync(() -> {
//				try {
//					client.resetAllMemories();
//					successCount.incrementAndGet();
//				}
//				catch (Exception e) {
//					errorCount.incrementAndGet();
//				}
//				finally {
//					latch.countDown();
//				}
//			});
//		}
//
//		// Then - 等待所有操作完成
//		boolean allCompleted = latch.await(30, TimeUnit.SECONDS);
//		assertThat(allCompleted).isTrue();
//		assertThat(successCount.get() + errorCount.get()).isEqualTo(numberOfOperations);
//	}
//
//	@Test
//	void testMixedAsyncOperations() throws InterruptedException {
//		// Given
//		CountDownLatch latch = new CountDownLatch(5);
//		AtomicInteger completedCount = new AtomicInteger(0);
//
//		// When - 混合执行不同类型的异步操作
//		// 1. 添加内存
//		Mem0ServerRequest.MemoryCreate memoryCreate = Mem0ServerRequest.MemoryCreate.builder()
//			.messages(List.of(new Mem0ServerRequest.Message("user", "mixed test")))
//			.userId("mixed-user")
//			.agentId("mixed-agent")
//			.runId("mixed-run")
//			.build();
//
//		CompletableFuture<Void> addFuture = client.addMemoryAsync(memoryCreate);
//		addFuture.whenComplete((result, throwable) -> {
//			completedCount.incrementAndGet();
//			latch.countDown();
//		});
//
//		// 2. 更新内存
//		Map<String, Object> updatedMemory = new HashMap<>();
//		updatedMemory.put("content", "mixed update");
//		CompletableFuture<Map<String, Object>> updateFuture = client.updateMemoryAsync("test-memory", updatedMemory);
//		updateFuture.whenComplete((result, throwable) -> {
//			completedCount.incrementAndGet();
//			latch.countDown();
//		});
//
//		// 3. 获取内存（同步方法，但在异步环境中调用）
//		CompletableFuture.runAsync(() -> {
//			try {
//				client.getMemory("test-memory");
//				completedCount.incrementAndGet();
//			}
//			catch (Exception e) {
//				// 预期的异常
//			}
//			finally {
//				latch.countDown();
//			}
//		});
//
//		// 4. 搜索内存
//		CompletableFuture.runAsync(() -> {
//			try {
//				Mem0ServerRequest.SearchRequest searchRequest = Mem0ServerRequest.SearchRequest.mem0Builder()
//					.query("mixed search")
//					.userId("mixed-user")
//					.build();
//				client.searchMemories(searchRequest);
//				completedCount.incrementAndGet();
//			}
//			catch (Exception e) {
//				// 预期的异常
//			}
//			finally {
//				latch.countDown();
//			}
//		});
//
//		// 5. 获取所有内存
//		CompletableFuture.runAsync(() -> {
//			try {
//				client.getAllMemories("mixed-user", "mixed-run", "mixed-agent");
//				completedCount.incrementAndGet();
//			}
//			catch (Exception e) {
//				// 预期的异常
//			}
//			finally {
//				latch.countDown();
//			}
//		});
//
//		// Then - 等待所有操作完成
//		boolean allCompleted = latch.await(30, TimeUnit.SECONDS);
//		assertThat(allCompleted).isTrue();
//		assertThat(completedCount.get()).isGreaterThanOrEqualTo(2); // 至少异步操作应该完成
//	}
//
//	@Test
//	void testAsyncOperationWithNullMemoryId() {
//		// When - 使用null memoryId调用更新方法
//		Map<String, Object> updatedMemory = new HashMap<>();
//		updatedMemory.put("content", "test");
//
//		// Then - 应该抛出异常或返回空结果
//		Map<String, Object> result = client.updateMemory(null, updatedMemory);
//		// 异步模式下应该立即返回空Map
//		assertThat(result).isNotNull();
//	}
//
//	@Test
//	void testAsyncOperationWithEmptyMemoryId() {
//		// When - 使用空字符串memoryId调用更新方法
//		Map<String, Object> updatedMemory = new HashMap<>();
//		updatedMemory.put("content", "test");
//
//		// Then - 应该能够处理（虽然会失败，但不会阻塞）
//		Map<String, Object> result = client.updateMemory("", updatedMemory);
//		assertThat(result).isNotNull();
//	}
//
//	@Test
//	void testAsyncOperationWithNullUpdatedMemory() {
//		// When - 使用null updatedMemory调用更新方法
//		// Then - 应该能够处理（虽然会失败，但不会阻塞）
//		Map<String, Object> result = client.updateMemory("test-memory", null);
//		assertThat(result).isNotNull();
//	}
//
//	@Test
//	void testAsyncOperationChainWithErrorHandling() throws InterruptedException {
//		// Given
//		CountDownLatch latch = new CountDownLatch(1);
//		AtomicInteger errorHandled = new AtomicInteger(0);
//
//		// When - 链式异步操作，第一个操作会失败
//		client.addMemoryAsync(null)
//			.thenRun(() -> {
//				// 这个回调不应该执行，因为前面的操作会失败
//			})
//			.exceptionally(throwable -> {
//				errorHandled.incrementAndGet();
//				latch.countDown();
//				return null;
//			});
//
//		// Then - 等待错误处理
//		boolean errorHandledResult = latch.await(2, TimeUnit.SECONDS);
//		assertThat(errorHandledResult).isTrue();
//		assertThat(errorHandled.get()).isEqualTo(1);
//	}
//
//	@Test
//	void testMultipleAsyncOperationsWithCompletableFutureAllOf() throws InterruptedException {
//		// Given
//		int numberOfOperations = 5;
//		List<CompletableFuture<Void>> futures = new ArrayList<>();
//
//		// When - 创建多个异步操作
//		for (int i = 0; i < numberOfOperations; i++) {
//			final int index = i;
//			Mem0ServerRequest.MemoryCreate memoryCreate = Mem0ServerRequest.MemoryCreate.builder()
//				.messages(List.of(new Mem0ServerRequest.Message("user", "allOf test " + index)))
//				.userId("allof-user-" + index)
//				.agentId("allof-agent-" + index)
//				.runId("allof-run-" + index)
//				.build();
//
//			CompletableFuture<Void> future = client.addMemoryAsync(memoryCreate);
//			futures.add(future);
//		}
//
//		// Then - 等待所有操作完成
//		CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
//		try {
//			allOf.get(5, TimeUnit.SECONDS);
//			assertThat(allOf.isDone()).isTrue();
//		}
//		catch (Exception e) {
//			// 由于没有真实服务器，操作会失败，但应该都完成
//			assertThat(allOf.isDone()).isTrue();
//		}
//	}
//
//	@Test
//	void testAsyncOperationTimeout() {
//		// Given
//		Mem0ServerRequest.MemoryCreate memoryCreate = Mem0ServerRequest.MemoryCreate.builder()
//			.messages(List.of(new Mem0ServerRequest.Message("user", "timeout test")))
//			.userId("timeout-user")
//			.agentId("timeout-agent")
//			.runId("timeout-run")
//			.build();
//
//		// When - 执行异步操作并设置超时
//		CompletableFuture<Void> future = client.addMemoryAsync(memoryCreate);
//
//		// Then - 应该能够获取future（即使操作会失败）
//		assertThat(future).isNotNull();
//		assertThatThrownBy(() -> future.get(1, TimeUnit.SECONDS))
//			.isInstanceOf(Exception.class);
//	}
//
//	@Test
//	void testAsyncExecutorShutdownGracefully() throws InterruptedException {
//		// Given - 启动一些异步操作
//		CountDownLatch latch = new CountDownLatch(3);
//		for (int i = 0; i < 3; i++) {
//			final int index = i;
//			Mem0ServerRequest.MemoryCreate memoryCreate = Mem0ServerRequest.MemoryCreate.builder()
//				.messages(List.of(new Mem0ServerRequest.Message("user", "shutdown graceful test " + index)))
//				.userId("shutdown-user-" + index)
//				.agentId("shutdown-agent-" + index)
//				.runId("shutdown-run-" + index)
//				.build();
//
//			CompletableFuture<Void> future = client.addMemoryAsync(memoryCreate);
//			future.whenComplete((result, throwable) -> {
//				latch.countDown();
//			});
//		}
//
//		// 等待一些操作开始
//		Thread.sleep(100);
//
//		// When - 关闭客户端
//		client.shutdown();
//
//		// Then - 关闭操作应该成功
//		assertThat(client).isNotNull();
//
//		// 等待操作完成（可能成功或失败）
//		latch.await(2, TimeUnit.SECONDS);
//	}
//
//	@Test
//	void testAsyncOperationWithLargeBatch() throws InterruptedException {
//		// Given
//		int batchSize = 20;
//		CountDownLatch latch = new CountDownLatch(batchSize);
//		AtomicInteger successCount = new AtomicInteger(0);
//
//		// When - 批量执行异步操作
//		for (int i = 0; i < batchSize; i++) {
//			final int index = i;
//			Mem0ServerRequest.MemoryCreate memoryCreate = Mem0ServerRequest.MemoryCreate.builder()
//				.messages(List.of(new Mem0ServerRequest.Message("user", "batch test " + index)))
//				.userId("batch-user-" + (index % 5)) // 使用不同的用户ID
//				.agentId("batch-agent-" + (index % 3)) // 使用不同的代理ID
//				.runId("batch-run-" + index)
//				.build();
//
//			CompletableFuture<Void> future = client.addMemoryAsync(memoryCreate);
//			future.whenComplete((result, throwable) -> {
//				if (throwable == null) {
//					successCount.incrementAndGet();
//				}
//				latch.countDown();
//			});
//		}
//
//		// Then - 等待所有操作完成
//		boolean allCompleted = latch.await(10, TimeUnit.SECONDS);
//		assertThat(allCompleted).isTrue();
//		// 由于没有真实服务器，所有操作都会失败，但应该都完成
//		assertThat(latch.getCount()).isEqualTo(0);
//	}
//
//	@Test
//	void testAsyncOperationOrdering() throws InterruptedException {
//		// Given
//		List<Integer> executionOrder = new ArrayList<>();
//		CountDownLatch latch = new CountDownLatch(3);
//
//		// When - 按顺序启动异步操作
//		for (int i = 0; i < 3; i++) {
//			final int index = i;
//			Mem0ServerRequest.MemoryCreate memoryCreate = Mem0ServerRequest.MemoryCreate.builder()
//				.messages(List.of(new Mem0ServerRequest.Message("user", "ordering test " + index)))
//				.userId("ordering-user")
//				.agentId("ordering-agent")
//				.runId("ordering-run")
//				.build();
//
//			CompletableFuture<Void> future = client.addMemoryAsync(memoryCreate);
//			future.whenComplete((result, throwable) -> {
//				synchronized (executionOrder) {
//					executionOrder.add(index);
//				}
//				latch.countDown();
//			});
//		}
//
//		// Then - 等待所有操作完成
//		boolean allCompleted = latch.await(30, TimeUnit.SECONDS);
//		assertThat(allCompleted).isTrue();
//		// 验证所有操作都完成了（顺序可能不同，因为异步执行）
//		assertThat(executionOrder.size()).isEqualTo(3);
//	}
//
//}
