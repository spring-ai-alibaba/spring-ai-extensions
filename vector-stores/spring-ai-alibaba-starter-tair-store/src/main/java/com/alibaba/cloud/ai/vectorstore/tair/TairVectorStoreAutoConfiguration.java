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
package com.alibaba.cloud.ai.vectorstore.tair;

import io.micrometer.observation.ObservationRegistry;
import org.springframework.ai.embedding.BatchingStrategy;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.TokenCountBatchingStrategy;
import org.springframework.ai.vectorstore.observation.VectorStoreObservationConvention;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Autoconfiguration for the Tair Vector Store.
 *
 * @author buvidk
 * @since 2026-2-14
 */
@AutoConfiguration
@ConditionalOnClass({TairVectorStore.class, EmbeddingModel.class})
@EnableConfigurationProperties(TairVectorStoreProperties.class)
@ConditionalOnProperty(prefix = "spring.ai.vectorstore.tair", name = "enabled", havingValue = "true", matchIfMissing = true)
public class TairVectorStoreAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public TairVectorApi tairVectorApi(TairVectorStoreProperties properties) {
        JedisPoolConfig config = new JedisPoolConfig();
        JedisPool jedisPool = new JedisPool(config, properties.getHost(), properties.getPort(), properties.getTimeout(), properties.getPassword());
        return new TairVectorApi(jedisPool);
    }

    @Bean
    @ConditionalOnMissingBean(BatchingStrategy.class)
    public BatchingStrategy tairBatchingStrategy() {
        return new TokenCountBatchingStrategy();
    }

    @Bean
    @ConditionalOnMissingBean
    public TairVectorStore tairVectorStore(
            TairVectorApi tairVectorApi,
            EmbeddingModel embeddingModel,
            TairVectorStoreProperties properties,
            BatchingStrategy batchingStrategy,
            ObjectProvider<ObservationRegistry> observationRegistry,
            ObjectProvider<VectorStoreObservationConvention> customObservationConvention) {
        return TairVectorStore.builder(tairVectorApi, embeddingModel)
                .options(properties.getOptions())
                .batchingStrategy(batchingStrategy)
                .initializeSchema(properties.isInitializeSchema())
                .observationRegistry(observationRegistry.getIfUnique(() -> ObservationRegistry.NOOP))
                .customObservationConvention(customObservationConvention.getIfAvailable())
                .build();
    }
}
