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
package com.alibaba.cloud.ai.prompt;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import com.alibaba.cloud.nacos.annotation.NacosConfigListener;
import com.alibaba.nacos.api.ai.AiService;
import com.alibaba.nacos.api.ai.listener.AbstractNacosPromptListener;
import com.alibaba.nacos.api.ai.listener.NacosPromptEvent;
import com.alibaba.nacos.api.ai.model.prompt.Prompt;
import com.alibaba.nacos.api.ai.model.prompt.PromptVariable;
import com.alibaba.nacos.api.exception.NacosException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.core.io.Resource;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

public class ConfigurablePromptTemplateFactory {

	private static final Logger logger = LoggerFactory.getLogger(ConfigurablePromptTemplateFactory.class);

	private final Map<String, ConfigurablePromptTemplate> templates = new ConcurrentHashMap<>();

	private final Map<String, ConfigurablePromptTemplate> fallbackTemplates = new ConcurrentHashMap<>();

	private final Set<String> subscribedPrompts = ConcurrentHashMap.newKeySet();

	private final PromptTemplateBuilderConfigure promptTemplateBuilderConfigure;

	private final AiService aiService;

	public ConfigurablePromptTemplateFactory(PromptTemplateBuilderConfigure promptTemplateBuilderConfigure) {
		this(promptTemplateBuilderConfigure, null);
	}

	public ConfigurablePromptTemplateFactory(PromptTemplateBuilderConfigure promptTemplateBuilderConfigure,
			AiService aiService) {
		this.promptTemplateBuilderConfigure = promptTemplateBuilderConfigure;
		this.aiService = aiService;
	}

	public ConfigurablePromptTemplate create(String name, Resource resource) {
		return createAndSubscribe(name, () -> new ConfigurablePromptTemplate(name, resource));
	}

	public ConfigurablePromptTemplate create(String name, String template) {
		return createAndSubscribe(name, () -> new ConfigurablePromptTemplate(name, template));
	}

	public ConfigurablePromptTemplate create(String name, String template, Map<String, Object> model) {
		return createAndSubscribe(name, () -> new ConfigurablePromptTemplate(name, template, model));
	}

	public ConfigurablePromptTemplate create(String name, Resource resource, Map<String, Object> model) {
		return createAndSubscribe(name, () -> new ConfigurablePromptTemplate(name, resource, model));
	}

	public ConfigurablePromptTemplate create(String name, PromptTemplate promptTemplate) {
		return createAndSubscribe(name, () -> new ConfigurablePromptTemplate(name, promptTemplate));
	}

	@NacosConfigListener(dataId = "spring.ai.alibaba.configurable.prompt", group = "DEFAULT_GROUP", initNotify = true)
	protected void onConfigChange(List<ConfigurablePromptTemplateModel> configList) {
		if (CollectionUtils.isEmpty(configList)) {
			return;
		}
		for (ConfigurablePromptTemplateModel configuration : configList) {
			if (!StringUtils.hasText(configuration.name()) || !StringUtils.hasText(configuration.template())) {
				continue;
			}
			updateTemplate(configuration.name(), configuration.template(), configuration.model());
		}
	}

	public ConfigurablePromptTemplate getTemplate(String name) {
		return templates.get(name);
	}

	private ConfigurablePromptTemplate createAndSubscribe(String name, Supplier<ConfigurablePromptTemplate> supplier) {
		ConfigurablePromptTemplate fallbackTemplate = fallbackTemplates.computeIfAbsent(name, key -> supplier.get());
		ConfigurablePromptTemplate template = templates.computeIfAbsent(name, key -> fallbackTemplate);
		subscribe(name);
		return templates.getOrDefault(name, template);
	}

	private void subscribe(String name) {
		if (aiService == null || !subscribedPrompts.add(name)) {
			return;
		}
		try {
			Prompt prompt = aiService.subscribePrompt(name, null, null, new AbstractNacosPromptListener() {
				@Override
				public void onEvent(NacosPromptEvent event) {
					updateTemplate(event.getPromptKey(), event.getPrompt());
				}
			});
			updateTemplate(name, prompt);
		}
		catch (NacosException ex) {
			subscribedPrompts.remove(name);
			logger.warn("Failed to subscribe Nacos prompt: {}", name, ex);
		}
	}

	private void updateTemplate(String name, Prompt prompt) {
		if (prompt == null) {
			ConfigurablePromptTemplate fallbackTemplate = fallbackTemplates.get(name);
			if (fallbackTemplate == null) {
				templates.remove(name);
			}
			else {
				templates.put(name, fallbackTemplate);
			}
			return;
		}
		Map<String, Object> model = new HashMap<>();
		String template = prompt.getTemplate();
		if (!CollectionUtils.isEmpty(prompt.getVariables())) {
			for (PromptVariable variable : prompt.getVariables()) {
				if (StringUtils.hasText(variable.getName())) {
					template = normalizeVariablePlaceholder(template, variable.getName());
					if (variable.getDefaultValue() != null) {
						model.put(variable.getName(), variable.getDefaultValue());
					}
				}
			}
		}
		updateTemplate(name, template, model);
	}

	private String normalizeVariablePlaceholder(String template, String variableName) {
		if (template == null) {
			return null;
		}
		return template.replace("{{" + variableName + "}}", "{" + variableName + "}");
	}

	private void updateTemplate(String name, String template, Map<String, Object> model) {
		if (!StringUtils.hasText(name) || !StringUtils.hasText(template)) {
			return;
		}
		PromptTemplate.Builder promptTemplateBuilder = promptTemplateBuilderConfigure
			.configure(PromptTemplate.builder()
				.template(template)
				.variables(model == null ? new HashMap<>() : model));

		templates.put(name, new ConfigurablePromptTemplate(name, promptTemplateBuilder.build()));

		logger.info("OnPromptTemplateConfigChange,templateName:{},template:{},model:{}", name, template, model);
	}

	public record ConfigurablePromptTemplateModel(String name, String template, Map<String, Object> model) {

	}

}
