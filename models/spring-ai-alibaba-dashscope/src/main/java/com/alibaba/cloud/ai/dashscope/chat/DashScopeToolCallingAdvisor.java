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
package com.alibaba.cloud.ai.dashscope.chat;

import java.util.HashMap;
import java.util.List;

import io.micrometer.observation.ObservationRegistry;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.ToolCallingAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
import org.springframework.ai.chat.metadata.DefaultUsage;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.support.UsageCalculator;
import reactor.core.publisher.Flux;

/**
 * Preserves cache usage for each streaming tool round. Spring AI 2.0.3's message
 * aggregator keeps the token totals but drops cache read and write counts.
 */
public final class DashScopeToolCallingAdvisor extends ToolCallingAdvisor {

    private static final String CACHE_READ = DashScopeToolCallingAdvisor.class.getName() + ".cacheRead";
    private static final String CACHE_WRITE = DashScopeToolCallingAdvisor.class.getName() + ".cacheWrite";

    private DashScopeToolCallingAdvisor(Builder builder) {
        super(builder.manager(), builder.getToolExecutionEligibilityChecker(), builder.getAdvisorOrder(),
                builder.conversationHistoryEnabled());
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public Flux<ChatClientResponse> adviseStream(ChatClientRequest request, StreamAdvisorChain chain) {
        return super.adviseStream(request, new CacheUsageStreamChain(chain)).map(response -> {
            if (!response.context().containsKey(CACHE_READ) && !response.context().containsKey(CACHE_WRITE)) {
                return response;
            }
            var context = new HashMap<>(response.context());
            context.remove(CACHE_READ);
            context.remove(CACHE_WRITE);
            return response.mutate().context(context).build();
        });
    }

    @Override
    protected ChatClientResponse doAfterStream(ChatClientResponse response, StreamAdvisorChain chain) {
        var chatResponse = response.chatResponse();
        if (chatResponse == null) {
            return response;
        }
        var usage = chatResponse.getMetadata().getUsage();
        var read = (Long) response.context().get(CACHE_READ);
        var write = (Long) response.context().get(CACHE_WRITE);
        if (read == null && write == null) {
            return response;
        }
        var roundUsage = new DefaultUsage(usage.getPromptTokens(), usage.getCompletionTokens(),
                usage.getTotalTokens(), usage.getNativeUsage(), read, write);
        return response.mutate().chatResponse(UsageCalculator.withUsage(chatResponse, roundUsage)).build();
    }

    private record CacheUsageStreamChain(StreamAdvisorChain delegate) implements StreamAdvisorChain {

        @Override
        public Flux<ChatClientResponse> nextStream(ChatClientRequest request) {
            return this.delegate.nextStream(request).map(response -> {
                var chatResponse = response.chatResponse();
                if (chatResponse == null) {
                    return response;
                }
                var usage = chatResponse.getMetadata().getUsage();
                Long read = usage.getCacheReadInputTokens();
                Long write = usage.getCacheWriteInputTokens();
                if (read == null && write == null) {
                    return response;
                }
                // ChatClientMessageAggregator carries context through to doAfterStream.
                var context = new HashMap<>(response.context());
                if (read != null) {
                    context.put(CACHE_READ, read);
                }
                if (write != null) {
                    context.put(CACHE_WRITE, write);
                }
                return response.mutate().context(context).build();
            });
        }

        @Override
        public List<StreamAdvisor> getStreamAdvisors() {
            return this.delegate.getStreamAdvisors();
        }

        @Override
        public StreamAdvisorChain copy(StreamAdvisor after) {
            return new CacheUsageStreamChain(this.delegate.copy(after));
        }

        @Override
        public ObservationRegistry getObservationRegistry() {
            return this.delegate.getObservationRegistry();
        }

    }

    public static final class Builder extends ToolCallingAdvisor.Builder<Builder> {

        private Builder() {
        }

        private ToolCallingManager manager() {
            return getToolCallingManager();
        }

        private boolean conversationHistoryEnabled() {
            return isConversationHistoryEnabled();
        }

        @Override
        protected Builder newCopy() {
            return new Builder();
        }

        @Override
        public DashScopeToolCallingAdvisor build() {
            return new DashScopeToolCallingAdvisor(this);
        }

    }

}
