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
package com.alibaba.cloud.ai.model.chat.memory.autoconfigure;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import javax.sql.DataSource;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatApiSpec.ChatCompletion;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatApiSpec.ChatCompletionFinishReason;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatApiSpec.ChatCompletionFunction;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatApiSpec.ChatCompletionMessage;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatApiSpec.ChatCompletionOutput;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatApiSpec.ChatCompletionOutput.Choice;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatApiSpec.ChatCompletionRequest;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatApiSpec.Role;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatApiSpec.ToolCall;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.alibaba.cloud.ai.tool.DashScopeAsyncToolCallingManager;
import com.alibaba.cloud.ai.tool.ObservableToolCallingManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.ToolCallingAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.messages.part.ReasoningPart;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatReasoning;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.session.autoconfigure.SessionServiceAutoConfiguration;
import org.springframework.ai.model.session.jdbc.autoconfigure.JdbcSessionRepositoryAutoConfiguration;
import org.springframework.ai.model.tool.ToolCallDecision;
import org.springframework.ai.model.tool.ToolCallDecisions;
import org.springframework.ai.model.tool.ToolCallingChatOptions;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.session.CreateSessionRequest;
import org.springframework.ai.session.InMemorySessionRepository;
import org.springframework.ai.session.SessionRepository;
import org.springframework.ai.session.SessionService;
import org.springframework.ai.session.advisor.SessionAdvisors;
import org.springframework.ai.session.jdbc.JdbcSessionRepository;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.definition.DefaultToolDefinition;
import org.springframework.ai.tool.metadata.ToolMetadata;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SessionMemoryToolCallingTests {

	private static final String SESSION_ID = "test-session";

	private final ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 2, 0, TimeUnit.SECONDS,
			new LinkedBlockingQueue<>());

	private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
		.withConfiguration(AutoConfigurations.of(AlibabaSessionMemoryAutoConfiguration.class,
				SessionServiceAutoConfiguration.class))
		.withPropertyValues("spring.ai.alibaba.session.enabled=true");

	@AfterEach
	void shutDownExecutor() {
		this.executor.shutdownNow();
	}

	@Test
	void createsAnInMemorySessionOnlyWhenEnabled() {
		this.contextRunner.run(context -> {
			assertThat(context).hasSingleBean(SessionService.class);
			assertThat(context).hasSingleBean(LegacyChatMemorySessionImporter.class);
			assertThat(context.getBean(SessionRepository.class)).isInstanceOf(InMemorySessionRepository.class);
		});
		this.contextRunner.withPropertyValues("spring.ai.alibaba.session.enabled=false").run(context -> {
			assertThat(context).doesNotHaveBean(SessionRepository.class);
			assertThat(context).doesNotHaveBean(LegacyChatMemorySessionImporter.class);
		});
	}

	@Test
	void importsStoredMessagesInOrderAndSkipsAnAlreadyImportedPrefix() {
		withJdbcSession(service -> {
			var importer = new LegacyChatMemorySessionImporter(service);
			var legacy = new InMemoryChatMemoryRepository();
			List<Message> first = List.of(new UserMessage("first"), new AssistantMessage("old answer"));
			legacy.saveAll("legacy", first);
			var target = CreateSessionRequest.builder().id(SESSION_ID).build();

			assertThat(importer.importConversation(legacy, "legacy", target)).isEqualTo(2);
			service.appendMessage(SESSION_ID, new UserMessage("another turn"));
			assertThat(importer.importConversation(legacy, "legacy", target)).isZero();
			assertThat(service.getMessages(SESSION_ID)).startsWith(first.toArray(Message[]::new)).hasSize(3);
			assertThat(legacy.findByConversationId("legacy")).isEqualTo(first);

			legacy.saveAll("unrelated", List.of(new UserMessage("unrelated content")));
			assertThatIllegalStateException()
				.isThrownBy(() -> importer.importConversation(legacy, "unrelated", target));
		});
	}

	@ParameterizedTest
	@CsvSource({ "false, false", "true, false", "false, true", "true, true" })
	void resumesFromJdbcHistoryAfterAnEarlierToolRoundAndReplaysReasoning(boolean async, boolean encrypted) {
		withJdbcSession(service -> {
			ToolCallingManager manager = async
					? DashScopeAsyncToolCallingManager.builder().taskExecutor(this.executor).build()
					: ObservableToolCallingManager.builder().build();
			ToolCallback lookup = testTool("lookup", false);
			ToolCallback confirmed = testTool("confirmed", true);
			DashScopeApi api = mock(DashScopeApi.class);
			List<ChatCompletionRequest> requests = new ArrayList<>();
			when(api.chatCompletionEntity(any(), any(), eq(encrypted))).thenAnswer(invocation -> {
				requests.add(invocation.getArgument(0));
				return ResponseEntity.ok(switch (requests.size()) {
					case 1 -> toolReply("lookup-id", "lookup", "first reasoning", encrypted);
					case 2 -> toolReply("confirm-id", "confirmed", "second reasoning", encrypted);
					case 3 -> textReply("resumed");
					case 4 -> textReply("next turn");
					default -> throw new AssertionError("Unexpected model request");
				});
			});
			var chatOptions = DashScopeChatOptions.builder().model(encrypted ? "qwen4-max" : "qwen-plus")
				.preserveThinking(true);
			if (encrypted) {
				chatOptions.thinkingDisplay("summarized");
			}
			DashScopeChatModel model = DashScopeChatModel.builder().dashScopeApi(api)
				.defaultOptions(chatOptions.build())
				.toolCallingManager(manager).build();
			var advisors = new ArrayList<>(SessionAdvisors.builder(service).build());
			advisors.add(ToolCallingAdvisor.builder().toolCallingManager(manager)
				.disableInternalConversationHistory().build());
			ChatClient client = ChatClient.builder(model).defaultAdvisors(advisors)
				.defaultTools(lookup, confirmed).build();

			ChatResponse paused = client.prompt().user("start").advisors(SessionAdvisors.session(SESSION_ID))
				.call().chatResponse();

			assertThat(ToolCallDecisions.awaitingDecision(paused)).containsExactly("confirm-id");
			verify(lookup).call(eq("{}"), any());
			verify(confirmed, never()).call(any(), any());
			List<Message> history = service.getMessages(SESSION_ID);
			assertThat(history).extracting(Message::getMessageType)
				.containsExactly(MessageType.USER, MessageType.ASSISTANT, MessageType.TOOL, MessageType.ASSISTANT);
			var saved = (AssistantMessage) history.get(3);
			if (encrypted) {
				assertThat(saved.getReasoning()).singleElement().extracting(ReasoningPart::summary)
					.isEqualTo("second reasoning");
				assertThat(DashScopeChatReasoning.encryptedContent(saved)).isEqualTo("cipher-confirm-id");
			}
			else {
				assertThat(saved.getReasoning()).containsExactly(ReasoningPart.of("second reasoning"));
			}
			ChatResponse storedReply = new ChatResponse(List.of(new Generation(saved)));
			var options = ToolCallingChatOptions.builder().toolCallbacks(List.of(lookup, confirmed)).build();
			var resumed = manager.executeToolCalls(new Prompt(history, options), storedReply,
					ToolCallDecisions.of(ToolCallDecision.allow("confirm-id")));

			assertThat(resumed.conversationHistory()).hasSize(5);
			assertThat(client.prompt().messages(resumed.conversationHistory().get(4))
				.advisors(SessionAdvisors.session(SESSION_ID)).call().content()).isEqualTo("resumed");
			assertThat(client.prompt().user("follow up").advisors(SessionAdvisors.session(SESSION_ID))
				.call().content()).isEqualTo("next turn");

			assertThat(requests.get(2).input().messages())
				.extracting(ChatCompletionRequest.Input.ChatCompletionMessage::role)
				.containsExactly(Role.USER, Role.ASSISTANT, Role.TOOL, Role.ASSISTANT, Role.TOOL);
			if (encrypted) {
				assertThat(requests.get(2).input().messages().get(1).encryptedContent()).isEqualTo("cipher-lookup-id");
				assertThat(requests.get(2).input().messages().get(3).encryptedContent()).isEqualTo("cipher-confirm-id");
				assertThat(requests.get(2).input().messages().get(3).reasoningContent()).isNull();
				assertThat(requests.get(3).input().messages().get(3).encryptedContent()).isEqualTo("cipher-confirm-id");
			}
			else {
				assertThat(requests.get(2).input().messages().get(1).reasoningContent()).isEqualTo("first reasoning");
				assertThat(requests.get(2).input().messages().get(3).reasoningContent()).isEqualTo("second reasoning");
				assertThat(requests.get(3).input().messages().get(3).reasoningContent()).isEqualTo("second reasoning");
			}
			assertThat(requests.get(2).input().messages().get(4).toolCallId()).isEqualTo("confirm-id");
			verify(confirmed).call(eq("{}"), any());
		});
	}

	private void withJdbcSession(Consumer<SessionService> test) {
		EmbeddedDatabase database = new EmbeddedDatabaseBuilder().generateUniqueName(true)
			.setType(EmbeddedDatabaseType.H2).build();
		try {
			this.contextRunner.withConfiguration(AutoConfigurations.of(JdbcSessionRepositoryAutoConfiguration.class))
				.withBean(DataSource.class, () -> database)
				.withPropertyValues("spring.ai.session.repository.jdbc.initialize-schema=always").run(context -> {
				assertThat(context.getBean(SessionRepository.class)).isInstanceOf(JdbcSessionRepository.class);
				test.accept(context.getBean(SessionService.class));
			});
		}
		finally {
			database.shutdown();
		}
	}

	private static ChatCompletion toolReply(String id, String name, String reasoning, boolean encrypted) {
		var call = new ToolCall(id, "function", new ChatCompletionFunction(name, "{}"), 0);
		var message = new ChatCompletionMessage("", Role.ASSISTANT, null, null, List.of(call), reasoning,
				null, null, null, null, encrypted ? "cipher-" + id : null);
		var choice = new Choice(ChatCompletionFinishReason.TOOL_CALLS, message, null, 0);
		return new ChatCompletion(id, new ChatCompletionOutput("", List.of(choice), null), null);
	}

	private static ChatCompletion textReply(String text) {
		var choice = new Choice(ChatCompletionFinishReason.STOP, new ChatCompletionMessage(text, Role.ASSISTANT),
				null, 0);
		return new ChatCompletion(text, new ChatCompletionOutput(text, List.of(choice), null), null);
	}

	private static ToolCallback testTool(String name, boolean confirmation) {
		ToolCallback callback = mock(ToolCallback.class);
		when(callback.getToolDefinition()).thenReturn(DefaultToolDefinition.builder().name(name).description(name)
			.inputSchema("{\"type\":\"object\"}").build());
		when(callback.getToolMetadata()).thenReturn(ToolMetadata.builder().requiresConfirmation(confirmation).build());
		when(callback.call(any(), any())).thenReturn(name + " result");
		return callback;
	}

}
