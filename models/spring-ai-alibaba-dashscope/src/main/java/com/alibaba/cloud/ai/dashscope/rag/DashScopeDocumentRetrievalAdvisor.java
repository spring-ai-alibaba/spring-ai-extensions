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
package com.alibaba.cloud.ai.dashscope.rag;

import com.alibaba.cloud.ai.dashscope.common.DashScopeApiConstants;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.AdvisorChain;
import org.springframework.ai.chat.client.advisor.api.BaseAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;
import org.springframework.ai.rag.generation.augmentation.QueryAugmenter;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Title Document retrieval advisor.<br>
 * Description Document retrieval advisor.<br>
 *
 * @author yuanci.ytb
 * @since 2024/8/16 11:29
 */

public class DashScopeDocumentRetrievalAdvisor implements BaseAdvisor {

	private static final Pattern RAG_REFERENCE_PATTERN = Pattern.compile("<ref>(.*?)</ref>", Pattern.DOTALL);

	private static final Pattern RAG_REFERENCE_INNER_PATTERN = Pattern.compile("[0-9]+");

	private static final PromptTemplate DEFAULT_USER_TEXT_ADVISE = new PromptTemplate(
			"""
					# 知识库
					请记住以下材料，他们可能对回答问题有帮助。
					指令：您需要仅使用提供的搜索文档为给定问题写出高质量的答案，并正确引用它们。 引用多个搜索结果时，请使用<ref>[编号]</ref>格式，注意确保这些引用直接有助于解答问题，编号需与材料原始编号一致且唯一。请注意，每个句子中必须至少引用一个文档。换句话说，你禁止在没有引用任何文献的情况下写句子。此外，您应该在每个句子中添加引用符号，注意在句号之前。

					对于每个问题按照下面的推理步骤得到带引用的答案：

					步骤1：我判断文档1和文档2与问题相关。

					步骤2：根据文档1，我写了一个回答陈述并引用了该文档。

					步骤3：根据文档2，我写一个答案声明并引用该文档。

					步骤4：我将以上两个答案语句进行合并、排序和连接，以获得流畅连贯的答案。

					$$材料：
					[1] 【文档名】植物中的光合作用.pdf
					【标题】光合作用位置
					【正文】光合作用主要在叶绿体中进行，涉及光能到化学能的转化。
					[2] 【文档名】光合作用.pdf
					【标题】光合作用转化
					【正文】光合作用是利用阳光将CO2和H2O转化为氧气和葡萄糖的过程。

					$$材料:
					{context}

					问题: {query}

					答案:
					""");

	private static final int DEFAULT_ORDER = 0;

	private final DocumentRetriever retriever;

	private final QueryAugmenter queryAugmenter;

	private final boolean enableReference;

	private final int order;

	public DashScopeDocumentRetrievalAdvisor(DocumentRetriever retriever, boolean enableReference) {
		this(retriever, DEFAULT_USER_TEXT_ADVISE, enableReference);
	}

	public DashScopeDocumentRetrievalAdvisor(DocumentRetriever retriever, PromptTemplate userTextAdvise,
			boolean enableReference) {
		this(retriever, userTextAdvise, enableReference, DEFAULT_ORDER);
	}

	public DashScopeDocumentRetrievalAdvisor(DocumentRetriever retriever, PromptTemplate userTextAdvise,
			boolean enableReference, int order) {
		this.retriever = retriever;
		this.queryAugmenter = ContextualQueryAugmenter.builder()
			.promptTemplate(userTextAdvise)
			.documentFormatter(documents -> documents.stream()
				.map(document -> """
						[%s] 【文档名】%s
						【标题】%s
						【正文】%s
						""".formatted(document.getMetadata().get("citation_index"), document.getMetadata().get("doc_name"),
						document.getMetadata().get("title"), document.getText()))
				.collect(Collectors.joining(System.lineSeparator())))
			.build();
		this.enableReference = enableReference;
		this.order = order;
	}

	@Override
	public int getOrder() {
		return this.order;
	}

	@Override
	public ChatClientRequest before(ChatClientRequest chatClientRequest, @Nullable AdvisorChain advisorChain) {
		Map<String, Object> context = new HashMap<>(chatClientRequest.context());

        String text = chatClientRequest.prompt().getUserMessage().getText();
        Query originalQuery = Query.builder().text(Objects.requireNonNullElse(text, ""))
			.history(chatClientRequest.prompt().getInstructions()).context(context).build();

		List<Document> documents = retriever.retrieve(originalQuery);

		Map<String, Document> documentMap = new LinkedHashMap<>();
		for (int i = 0; i < documents.size(); i++) {
			Document document = documents.get(i);
			int indexId = i + 1;
			document = document.mutate().metadata(new HashMap<>(document.getMetadata()))
				.metadata("citation_index", indexId).build();
			documentMap.put("[%d]".formatted(indexId), document);
		}

		context.put(DashScopeApiConstants.RETRIEVED_DOCUMENTS, documentMap);

		Query augmentedQuery = this.queryAugmenter.augment(originalQuery, new ArrayList<>(documentMap.values()));

		return chatClientRequest.mutate()
			.prompt(chatClientRequest.prompt().augmentUserMessage(augmentedQuery.text()))
			.context(context)
			.build();
	}

	@Override
	public ChatClientResponse after(ChatClientResponse response, @Nullable AdvisorChain advisorChain) {
		return withReferences(response, responseText(response));
	}

	@Override
	public Flux<ChatClientResponse> adviseStream(ChatClientRequest request, StreamAdvisorChain chain) {
		return Flux.defer(() -> {
			StringBuilder answer = new StringBuilder();
			return Mono.fromCallable(() -> before(request, chain)).subscribeOn(getScheduler())
				.flatMapMany(processed -> chain.nextStream(processed).map(response -> {
					if (this.enableReference) {
						answer.append(responseText(response));
					}
					var context = new HashMap<>(processed.context());
					context.putAll(response.context());
					var contextualResponse = ChatClientResponse.builder().chatResponse(response.chatResponse())
						.context(context).build();
					return withReferences(contextualResponse, answer);
				}));
		});
	}

	private static String responseText(ChatClientResponse response) {
		var chatResponse = response.chatResponse();
		if (chatResponse == null || chatResponse.getResult() == null) {
			return "";
		}
		return Objects.requireNonNullElse(chatResponse.getResult().getOutput().getText(), "");
	}

	private ChatClientResponse withReferences(ChatClientResponse response, CharSequence answer) {
		var chatResponse = response.chatResponse();
		var builder = chatResponse == null ? ChatResponse.builder() : ChatResponse.builder().from(chatResponse);
		Object retrieved = response.context().get(DashScopeApiConstants.RETRIEVED_DOCUMENTS);
		Map<?, ?> documents = retrieved instanceof Map<?, ?> map ? map : Map.of();
		builder.metadata(DashScopeApiConstants.RETRIEVED_DOCUMENTS, documents);
		if (this.enableReference) {
			var references = new LinkedHashSet<Document>();
			var matcher = RAG_REFERENCE_PATTERN.matcher(answer);
			while (matcher.find()) {
				var numbers = RAG_REFERENCE_INNER_PATTERN.matcher(matcher.group(1));
				while (numbers.find()) {
					if (documents.get("[" + numbers.group() + "]") instanceof Document document) {
						references.add(document);
					}
				}
			}
			builder.metadata(DashScopeApiConstants.REFERENCED_DOCUMENTS, List.copyOf(references));
		}
		return ChatClientResponse.builder().chatResponse(builder.build()).context(response.context()).build();
	}

}
