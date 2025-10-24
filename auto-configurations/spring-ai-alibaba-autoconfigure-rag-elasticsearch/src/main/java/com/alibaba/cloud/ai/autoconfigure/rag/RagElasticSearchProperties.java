package com.alibaba.cloud.ai.autoconfigure.rag;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for RAG ElasticSearch integration.
 *
 * @author benym
 */
@ConfigurationProperties(prefix = RagElasticSearchProperties.RAG_ES_PREFIX)
public class RagElasticSearchProperties {

    public static final String RAG_ES_PREFIX = "spring.ai.alibaba.rag.elasticsearch";
}
