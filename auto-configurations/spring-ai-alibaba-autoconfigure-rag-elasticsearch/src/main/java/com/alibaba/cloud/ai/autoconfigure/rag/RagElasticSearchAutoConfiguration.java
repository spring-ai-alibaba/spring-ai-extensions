package com.alibaba.cloud.ai.autoconfigure.rag;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * Auto-configuration class for integrating ElasticSearch as a retrieval backend in
 *
 * @author benym
 */
@AutoConfiguration
@EnableConfigurationProperties({ RagElasticSearchProperties.class })
public class RagElasticSearchAutoConfiguration {

}
