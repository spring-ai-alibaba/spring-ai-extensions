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

import org.springframework.ai.vectorstore.properties.CommonVectorStoreProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * Configuration properties for the Tair Vector Store.
 *
 * @author buvidk
 * @since 2026-2-14
 */
@ConfigurationProperties(TairVectorStoreProperties.CONFIG_PREFIX)
public class TairVectorStoreProperties extends CommonVectorStoreProperties {

    public static final String CONFIG_PREFIX = "spring.ai.vectorstore.tair";

    private String host = "localhost";

    private int port = 6379;

    private String password;

    private int timeout = 2000;

    @NestedConfigurationProperty
    private final TairVectorStoreOptions options = new TairVectorStoreOptions();

    public TairVectorStoreOptions getOptions() {
        return options;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

}
