/*
 * Copyright 2024-2025 the original author or authors.
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

package com.alibaba.cloud.ai.autoconfigure.memory;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest5_client.Rest5ClientTransport;
import co.elastic.clients.transport.rest5_client.low_level.Rest5Client;
import com.alibaba.cloud.ai.memory.elasticsearch.ElasticsearchChatMemoryRepository;
import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.client5.http.ssl.DefaultClientTlsStrategy;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.net.ssl.SSLContext;

/**
 * Auto-configuration for ElasticSearch chat memory repository.
 */
@ConditionalOnClass({ ElasticsearchChatMemoryRepository.class, ElasticsearchClient.class })
@ConditionalOnProperty(prefix = ElasticsearchChatMemoryProperties.CONFIG_PREFIX, name = "enabled", havingValue = "true",
		matchIfMissing = false)
@EnableConfigurationProperties(ElasticsearchChatMemoryProperties.class)
public class ElasticsearchChatMemoryAutoConfiguration {

	private static final Logger logger = LoggerFactory.getLogger(ElasticsearchChatMemoryAutoConfiguration.class);

	public static final String ES_CHAT_MEMORY_REPOSITORY_BEAN_NAME = "elasticsearchChatMemoryRepository";

	@Bean(value = ES_CHAT_MEMORY_REPOSITORY_BEAN_NAME)
	@ConditionalOnMissingBean(name = ES_CHAT_MEMORY_REPOSITORY_BEAN_NAME)
	ElasticsearchChatMemoryRepository elasticsearchChatMemoryRepository(ElasticsearchChatMemoryProperties properties)
			throws Exception {
		logger.info("Configuring elasticsearch chat memory repository");
		// Create HttpHosts for all nodes
		HttpHost[] httpHosts;
		if (!CollectionUtils.isEmpty(properties.getNodes())) {
			httpHosts = properties.getNodes().stream().map(node -> {
				String[] parts = node.split(":");
				return new HttpHost(properties.getScheme(), parts[0], Integer.parseInt(parts[1]));
			}).toArray(HttpHost[]::new);
		}
		else {
			// Fallback to single node configuration
			httpHosts = new HttpHost[] {
					new HttpHost(properties.getScheme(), properties.getHost(), properties.getPort()) };
		}

		var restClientBuilder = Rest5Client.builder(httpHosts);

		// Add authentication if credentials are provided
		if (StringUtils.hasText(properties.getUsername()) && StringUtils.hasText(properties.getPassword())) {
            BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
			credentialsProvider.setCredentials(new AuthScope(null, -1),
					new UsernamePasswordCredentials(properties.getUsername(), properties.getPassword().toCharArray()));

			// Create SSL context if using HTTPS
			if ("https".equalsIgnoreCase(properties.getScheme())) {
				SSLContext sslContext = SSLContextBuilder.create()
					.loadTrustMaterial(null, (chains, authType) -> true)
					.build();

                restClientBuilder.setHttpClientConfigCallback(
                                httpClientBuilder -> httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider))
                        .setConnectionManagerCallback(
                                connectionManager -> connectionManager.setTlsStrategy(
                                        new DefaultClientTlsStrategy(sslContext, NoopHostnameVerifier.INSTANCE)));
			}
			else {
				restClientBuilder.setHttpClientConfigCallback(
						httpClientBuilder -> httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider));
			}
		}

		// Create the transport and client
		ElasticsearchTransport transport = new Rest5ClientTransport(restClientBuilder.build(), new JacksonJsonpMapper());
		ElasticsearchClient elasticsearchClient = new ElasticsearchClient(transport);
		return new ElasticsearchChatMemoryRepository(elasticsearchClient);
	}

}
