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
package com.alibaba.cloud.ai.mcp.nacos;

import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.utils.StringUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertyResolver;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.PropertySources;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Sunrisea
 */
@ConfigurationProperties(prefix = NacosMcpProperties.CONFIG_PREFIX)
public class NacosMcpProperties {

	private static final Logger log = LoggerFactory.getLogger(NacosMcpProperties.class);

	public static final String CONFIG_PREFIX = "spring.ai.alibaba.mcp.nacos";

	public static final String DEFAULT_ADDRESS = "127.0.0.1:8848";

	private static final Pattern PATTERN = Pattern.compile("-(\\w)");

	String namespace = "public";

	@Nullable
	String serverAddr;

	@Nullable
	String username;

	@Nullable
	String password;

	@Nullable
	String accessKey;

	@Nullable
	String secretKey;

	@Nullable
	String endpoint;

	@Nullable
	String ip;

	@Autowired
	@JsonIgnore
	private Environment environment;

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public @Nullable String getUsername() {
		return username;
	}

	public void setUsername(@Nullable String username) {
		this.username = username;
	}

	public @Nullable String getPassword() {
		return password;
	}

	public void setPassword(@Nullable String password) {
		this.password = password;
	}

	public @Nullable String getAccessKey() {
		return accessKey;
	}

	public void setAccessKey(@Nullable String accessKey) {
		this.accessKey = accessKey;
	}

	public @Nullable String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(@Nullable String secretKey) {
		this.secretKey = secretKey;
	}

	public @Nullable String getIp() {
		return ip;
	}

	public void setIp(@Nullable String ip) {
		this.ip = ip;
	}

	public @Nullable String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(@Nullable String endpoint) {
		this.endpoint = endpoint;
	}

	public @Nullable String getServerAddr() {
		return serverAddr;
	}

	void setServerAddr(@Nullable String serverAddr) {
		this.serverAddr = serverAddr;
	}

	public Properties getNacosProperties() {
		Properties properties = new Properties();
		properties.put(PropertyKeyConst.NAMESPACE, Objects.toString(this.namespace, ""));
		properties.put(PropertyKeyConst.SERVER_ADDR, Objects.toString(this.serverAddr, ""));
		properties.put(PropertyKeyConst.USERNAME, Objects.toString(this.username, ""));
		properties.put(PropertyKeyConst.PASSWORD, Objects.toString(this.password, ""));
		properties.put(PropertyKeyConst.ACCESS_KEY, Objects.toString(this.accessKey, ""));
		properties.put(PropertyKeyConst.SECRET_KEY, Objects.toString(this.secretKey, ""));
		String endpoint = Objects.toString(this.endpoint, "");
		if (endpoint.contains(":")) {
			int index = endpoint.indexOf(":");
			properties.put(PropertyKeyConst.ENDPOINT, endpoint.substring(0, index));
			properties.put(PropertyKeyConst.ENDPOINT_PORT, endpoint.substring(index + 1));
		}
		else {
			properties.put(PropertyKeyConst.ENDPOINT, endpoint);
		}

		enrichNacosConfigProperties(properties);

		if (StringUtils.isEmpty(this.serverAddr) && StringUtils.isEmpty(this.endpoint)) {
			properties.put(PropertyKeyConst.SERVER_ADDR, DEFAULT_ADDRESS);
		}

		return properties;
	}

	protected void enrichNacosConfigProperties(Properties nacosConfigProperties) {
		if (environment == null) {
			return;
		}
		ConfigurableEnvironment env = (ConfigurableEnvironment) environment;
		Map<String, Object> properties = getSubProperties(env.getPropertySources(), env, CONFIG_PREFIX);
		properties.forEach((k, v) -> nacosConfigProperties.putIfAbsent(resolveKey(k), String.valueOf(v)));
	}

	protected String resolveKey(String key) {
		Matcher matcher = PATTERN.matcher(key);
		StringBuilder sb = new StringBuilder();
		while (matcher.find()) {
			matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
		}
		matcher.appendTail(sb);
		return sb.toString();
	}

	private Map<String, Object> getSubProperties(PropertySources propertySources, PropertyResolver propertyResolver,
			String prefix) {

		Map<String, Object> subProperties = new LinkedHashMap<String, Object>();

		for (PropertySource<?> source : propertySources) {
			for (String name : getPropertyNames(source)) {
				if (!subProperties.containsKey(name) && name.startsWith(prefix)) {
					String subName = name.substring(prefix.length() + 1);
					if (!subProperties.containsKey(subName)) { // take first one
						Object value = source.getProperty(name);
						if (value instanceof String) {
							value = propertyResolver.resolvePlaceholders((String) value);
						}
						subProperties.put(subName, value);
					}
				}
			}
		}
		return Collections.unmodifiableMap(subProperties);
	}

	private String[] getPropertyNames(PropertySource propertySource) {

		String[] propertyNames = propertySource instanceof EnumerablePropertySource
				? ((EnumerablePropertySource<?>) propertySource).getPropertyNames() : null;

		if (propertyNames == null) {
			return new String[0];
		}
		return propertyNames;
	}

}
