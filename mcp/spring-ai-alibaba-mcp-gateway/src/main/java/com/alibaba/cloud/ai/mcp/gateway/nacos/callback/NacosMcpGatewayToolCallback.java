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

package com.alibaba.cloud.ai.mcp.gateway.nacos.callback;

import com.alibaba.cloud.ai.mcp.gateway.core.McpGatewayToolDefinition;
import com.alibaba.cloud.ai.mcp.gateway.core.jsontemplate.RequestTemplateInfo;
import com.alibaba.cloud.ai.mcp.gateway.core.jsontemplate.RequestTemplateParser;
import com.alibaba.cloud.ai.mcp.gateway.core.utils.SpringBeanUtils;
import com.alibaba.cloud.ai.mcp.gateway.core.security.McpGatewayOAuthInterceptor;
import com.alibaba.cloud.ai.mcp.gateway.core.security.McpGatewayOAuthTokenManager;
import com.alibaba.cloud.ai.mcp.gateway.core.security.McpGatewayOAuthProperties;
import com.alibaba.cloud.ai.mcp.gateway.nacos.definition.NacosMcpGatewayToolDefinition;
import com.alibaba.cloud.ai.mcp.nacos.service.NacosMcpOperationService;
import com.alibaba.nacos.api.ai.model.mcp.McpEndpointInfo;
import com.alibaba.nacos.api.ai.model.mcp.McpServerRemoteServiceConfig;
import com.alibaba.nacos.api.ai.model.mcp.McpServiceRef;
import com.alibaba.nacos.api.ai.model.mcp.McpToolMeta;
import com.alibaba.nacos.api.config.listener.AbstractListener;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.common.utils.JacksonUtils;
import com.alibaba.nacos.shaded.com.google.common.collect.Maps;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.HttpClientSseClientTransport;
import io.modelcontextprotocol.client.transport.WebClientStreamableHttpTransport;
import io.modelcontextprotocol.spec.McpSchema;
import io.modelcontextprotocol.spec.McpSchema.CallToolResult;
import io.modelcontextprotocol.spec.McpSchema.InitializeResult;
import io.modelcontextprotocol.spec.McpClientTransport;
import io.modelcontextprotocol.spec.McpSchema.TextContent;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.lang.NonNull;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The type Nacos mcp gateway tool callback.
 */
public class NacosMcpGatewayToolCallback implements ToolCallback {

    private static final Logger logger = LoggerFactory.getLogger(NacosMcpGatewayToolCallback.class);

    private static final Pattern TEMPLATE_PATTERN = Pattern.compile("\\{\\{\\s*(\\.(?:[\\w]+(?:\\.[\\w]+)*)?)\\s*\\}\\}");

    // Match {{ ${nacos.dataId/group} }} or {{ ${nacos.dataId/group}.key1.key2 }}
    private static final Pattern NACOS_TEMPLATE_PATTERN = Pattern
            .compile("\\{\\{\\s*\\$\\{nacos\\.([^}]+)\\}(\\.[\\w]+(?:\\.[\\w]+)*)?\\s*}}");

    /**
     * The Object mapper.
     */
    static ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.setSerializationInclusion(Include.NON_NULL);
    }

    private final NacosMcpGatewayToolDefinition toolDefinition;

    private final NacosMcpOperationService nacosMcpOperationService;

    private final HashMap<String, AbstractListener> nacosConfigListeners = new HashMap<>();

    private final HashMap<String, String> nacosConfigContent = new HashMap<>();

    private final WebClient.Builder webClientBuilder;

    /**
     * Instantiates a new Nacos mcp gateway tool callback.
     *
     * @param toolDefinition the tool definition
     */
    public NacosMcpGatewayToolCallback(final McpGatewayToolDefinition toolDefinition) {
        this.toolDefinition = (NacosMcpGatewayToolDefinition) toolDefinition;
        this.nacosMcpOperationService = SpringBeanUtils.getInstance().getBean(NacosMcpOperationService.class);
        this.webClientBuilder = initializeWebClientBuilder(toolDefinition.name());
    }

    private WebClient.Builder initializeWebClientBuilder(String toolName) {
        WebClient.Builder baseBuilder = SpringBeanUtils.getInstance().getBean(WebClient.Builder.class);

        try {
            McpGatewayOAuthProperties oauthProperties = SpringBeanUtils.getInstance()
                    .getBean(McpGatewayOAuthProperties.class);
            McpGatewayOAuthTokenManager tokenManager = SpringBeanUtils.getInstance()
                    .getBean(McpGatewayOAuthTokenManager.class);

            if (oauthProperties.isEnabled()) {
                McpGatewayOAuthInterceptor oauthInterceptor = new McpGatewayOAuthInterceptor(tokenManager,
                        oauthProperties);
                logger.info("MCP Gateway has enabled OAuth authentication tool: {}", toolName);
                return baseBuilder.filter(oauthInterceptor);
            } else {
                logger.debug("OAuth authentication is not enabled for tool: {}", toolName);
                return baseBuilder;
            }
        } catch (Exception e) {
            logger.debug("OAuth is not effective, using default WebClient tool: {}", toolName);
            return baseBuilder;
        }

    }

    /**
     * Process tool request
     */
    private Mono<String> processToolRequest(String configJson, Map<String, Object> args, String baseUrl) {
        try {
            JsonNode toolConfig = objectMapper.readTree(configJson);
            logger.info("[processToolRequest] toolConfig: {} args: {} baseUrl: {}", toolConfig, args, baseUrl);

            // Validate configuration integrity
            if (toolConfig == null || toolConfig.isEmpty()) {
                return Mono.error(new IllegalArgumentException("Tool configuration is empty or invalid"));
            }

            JsonNode requestTemplate = toolConfig.path("requestTemplate");
            JsonNode argsPosition = toolConfig.path("argsPosition");
            String url = requestTemplate.path("url").asText();
            String method = requestTemplate.path("method").asText();
            logger.info("[processToolRequest] requestTemplate: {} url: {} method: {}", requestTemplate, url, method);

            // Check URL and method
            if (url.isEmpty() || method.isEmpty()) {
                return Mono.error(new IllegalArgumentException("URL and method are required in requestTemplate"));
            }

            // Validate HTTP method
            try {
                HttpMethod.valueOf(method.toUpperCase());
            } catch (IllegalArgumentException e) {
                return Mono.error(new IllegalArgumentException("Invalid HTTP method: " + method));
            }

            // Create WebClient
            baseUrl = baseUrl != null ? baseUrl : "http://localhost";
            WebClient client = webClientBuilder.baseUrl(baseUrl).build();

            // Build and execute request
            return buildAndExecuteRequest(client, requestTemplate, argsPosition, toolConfig.path("responseTemplate"),
                    args, baseUrl)
                    .onErrorResume(e -> {
                        logger.error("Failed to execute tool request:", e);
                        return Mono.error(new RuntimeException("Tool execution failed: " + e.getMessage(), e));
                    });
        } catch (Exception e) {
            logger.error("Failed to process tool request", e);
            return Mono.error(new RuntimeException("Failed to process tool request: " + e.getMessage(), e));
        }
    }

    /**
     * Build and execute WebClient request
     */
    private Mono<String> buildAndExecuteRequest(WebClient client, JsonNode requestTemplate, JsonNode argsPosition,
                                                JsonNode responseTemplate, Map<String, Object> args, String baseUrl) {

        RequestTemplateInfo info = RequestTemplateParser.parseRequestTemplate(requestTemplate, argsPosition);
        String url = info.url;
        String method = info.method;
        HttpMethod httpMethod = HttpMethod.valueOf(method.toUpperCase());

        // Process path parameters in URL
        String processingUrl = RequestTemplateParser.addPathVariables(url, info, args);
        Map<String, Object> params = new HashMap<>();
        params.put("args", args);
        params.put("extendedData", "");
        String processedUrl = processTemplateString(processingUrl, params);
        logger.info("[buildAndExecuteRequest] original url template: {} processed url: {}", url, processedUrl);

        String hostFromUrl = extractHostFromUrl(processedUrl);
        String pathOnlyUrl = extractPathFromUrl(processedUrl);
        // Build request
        WebClient.RequestBodySpec requestBodySpec = client.method(httpMethod)
                .uri(builder -> RequestTemplateParser.buildUri(builder, pathOnlyUrl, info, args));

        // Add request headers
        MultiValueMap<String, String> headers = RequestTemplateParser.addHeaders(requestBodySpec, info, args,
                this::processTemplateString);

        if (hostFromUrl != null && !hostFromUrl.isEmpty()) {
            requestBodySpec.header("Host", hostFromUrl);
            headers.add("Host", hostFromUrl);
        }
        // Process request body
        WebClient.RequestHeadersSpec<?> headersSpec = RequestTemplateParser.addRequestBody(requestBodySpec, headers,
                info, args, this::processTemplateString, objectMapper, logger);

        // Output final request information
        String fullUrl = baseUrl.endsWith("/") && pathOnlyUrl.startsWith("/") ? baseUrl + pathOnlyUrl.substring(1)
                : baseUrl + pathOnlyUrl;
        logger.info("[buildAndExecuteRequest] final request: method={} url={} args={}", method, fullUrl, args);

        return headersSpec.retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        response -> Mono.error(new RuntimeException("Client error: " + response.statusCode())))
                .onStatus(HttpStatusCode::is5xxServerError,
                        response -> Mono.error(new RuntimeException("Server error: " + response.statusCode())))
                .bodyToMono(String.class)
                .timeout(getTimeoutDuration()) // Use configured timeout
                .doOnNext(responseBody -> logger.info("[buildAndExecuteRequest] received responseBody: {}", responseBody))
                .map(responseBody -> processResponse(responseBody, responseTemplate, args))
                .onErrorResume(e -> {
                    logger.error("[buildAndExecuteRequest] Request failed: {}", e.getMessage(), e);
                    return Mono.error(new RuntimeException("HTTP request failed: " + e.getMessage(), e));
                });
    }

    /**
     * Extract path part from full URL
     *
     * @param url Full URL
     * @return Path part, or original URL if parsing fails
     */
    private String extractPathFromUrl(String url) {
        if (url == null || url.isEmpty()) {
            return url;
        }

        try {
            java.net.URI uri = java.net.URI.create(url);
            String path = uri.getPath();
            String query = uri.getQuery();

            if (path == null) {
                path = "";
            }

            if (query != null && !query.isEmpty()) {
                return path + "?" + query;
            }

            return path;
        } catch (Exception e) {
            logger.warn("[extractPathFromUrl] Failed to parse URL: {}", e.getMessage());
            return url; // Return original URL if parsing fails
        }
    }

    /**
     * Extract host information from URL
     *
     * @param url Full URL
     * @return Host information (host:port format), or null if not present
     */
    private String extractHostFromUrl(String url) {
        if (url == null || url.isEmpty()) {
            return null;
        }

        try {
            // Parse URL using URI class
            java.net.URI uri = java.net.URI.create(url);
            String host = uri.getHost();
            int port = uri.getPort();

            if (host != null && !host.isEmpty()) {
                if (port != -1) {
                    return host + ":" + port;
                }
                return host;
            }
        } catch (Exception e) {
            logger.warn("[extractHostFromUrl] Failed to parse URL: {}", e.getMessage());
        }

        return null;
    }

    /**
     * Process response
     */
    private String processResponse(String responseBody, JsonNode responseTemplate, Map<String, Object> args) {
        logger.info("[processResponse] received responseBody: {}", responseBody);
        String result = null;
        Map<String, Object> params = new HashMap<>();
        params.put("args", args);
        params.put("extendedData", responseBody);
        if (!responseTemplate.isEmpty()) {
            if (responseTemplate.has("body") && !responseTemplate.path("body").asText().isEmpty()) {
                String bodyTemplate = responseTemplate.path("body").asText();
                // Hand over to ResponseTemplateParser for unified processing
                result = processTemplateString(bodyTemplate, params);
                logger.info("[processResponse] ResponseTemplateParser result: {}", result);
                return result;
            } else if (responseTemplate.has("prependBody") || responseTemplate.has("appendBody")) {
                String prependText = responseTemplate.path("prependBody").asText("");
                String appendText = responseTemplate.path("appendBody").asText("");
                result = processTemplateString(prependText, params) + responseBody
                        + processTemplateString(appendText, params);
                logger.info("[processResponse] prepend/append result: {}", result);
                return result;
            }
        }
        result = responseBody;
        logger.info("[processResponse] default result: {}", result);
        return result;
    }

    /**
     * Process nacos config ref template string.
     *
     * @param template the template
     * @return the string
     */
    public String processNacosConfigRefTemplate(String template) {
        if (StringUtils.isBlank(template)) {
            return template;
        }

        StringBuffer result = new StringBuffer();
        Matcher matcher = NACOS_TEMPLATE_PATTERN.matcher(template);

        while (matcher.find()) {
            String nacosRef = matcher.group(1);
            String dotNotation = matcher.group(2);
            String replacement = resolveNacosReference(nacosRef, dotNotation);
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement != null ? replacement : ""));
        }
        matcher.appendTail(result);

        return result.toString();
    }

    /**
     * Resolve Nacos reference
     *
     * @param nacosRef Reference string in format dataId/group
     * @param dotNotation Dot notation part in format .key1.key2 (may be null)
     * @return Resolved value
     */
    private String resolveNacosReference(String nacosRef, String dotNotation) {
        if (StringUtils.isBlank(nacosRef)) {
            return null;
        }

        try {
            // Parse dataId and group
            String[] configParts = nacosRef.split("/");
            if (configParts.length != 2) {
                throw new IllegalArgumentException(
                        "Invalid Nacos config reference format: " + nacosRef + ". Expected format: dataId/group");
            }

            String dataId = configParts[0];
            String group = configParts[1];

            // Get config content
            String configContent = getConfigContent(dataId, group);
            if (StringUtils.isBlank(configContent)) {
                logger.warn("[resolveNacosReference] No content found for dataId: {}, group: {}", dataId, group);
                return null;
            }

            // If no dot notation, return config content directly
            if (StringUtils.isBlank(dotNotation)) {
                return configContent;
            }

            // If dot notation exists, remove leading dot and parse JSON to extract specified field
            String jsonPath = dotNotation.startsWith(".") ? dotNotation.substring(1) : dotNotation;
            return extractJsonValueFromNacos(configContent, jsonPath);

        } catch (Exception e) {
            // Log error but don't interrupt processing
            logger.error("[resolveNacosReference] Failed to resolve Nacos reference: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to resolve Nacos reference: " + e.getMessage(), e);
        }
    }

    /**
     * Get Nacos config content
     *
     * @param dataId Config ID
     * @param group Group
     * @return Config content
     * @throws NacosException Nacos exception
     */
    private String getConfigContent(String dataId, String group) throws NacosException {
        String cacheKey = dataId + "@@" + group;
        if (nacosConfigContent.containsKey(cacheKey)) {
            return nacosConfigContent.get(cacheKey);
        } else {
            AbstractListener listener = new AbstractListener() {
                @Override
                public void receiveConfigInfo(String configInfo) {
                    nacosConfigContent.put(cacheKey, configInfo);
                }
            };
            AbstractListener oldListener = nacosConfigListeners.putIfAbsent(cacheKey, listener);
            if (oldListener == null) {
                try {
                    nacosMcpOperationService.getConfigService().addListener(dataId, group, listener);
                } catch (Exception e) {
                    nacosConfigListeners.remove(cacheKey);
                    logger.error("Failed to add listener for Nacos config: {}", e.getMessage(), e);
                }
            }
            return nacosMcpOperationService.getConfigService().getConfig(dataId, group, 3000);
        }
    }

    /**
     * Extract value at specified path from JSON string
     *
     * @param jsonString JSON string
     * @param jsonPath JSON path, e.g. key1.key2
     * @return Extracted value
     */
    private String extractJsonValueFromNacos(String jsonString, String jsonPath) {

        try {
            JsonNode rootNode = objectMapper.readTree(jsonString);
            String[] pathParts = jsonPath.split("\\.");

            JsonNode currentNode = rootNode;
            for (String part : pathParts) {
                if (currentNode == null || currentNode.isMissingNode()) {
                    logger.warn("[extractJsonValueFromNacos] Path '{}' not found in JSON", jsonPath);
                    return null;
                }
                currentNode = currentNode.get(part);
            }

            if (currentNode == null || currentNode.isMissingNode()) {
                logger.warn("[extractJsonValueFromNacos] Final path '{}' not found in JSON", jsonPath);
                return null;
            }

            // Return appropriate value based on node type
            if (currentNode.isTextual()) {
                return currentNode.asText();
            } else if (currentNode.isNumber()) {
                return currentNode.asText();
            } else if (currentNode.isBoolean()) {
                return String.valueOf(currentNode.asBoolean());
            } else {
                // For complex objects, return JSON string
                return currentNode.toString();
            }
        } catch (JsonProcessingException e) {
            logger.error("[extractJsonValueFromNacos] Failed to parse JSON from Nacos config. Content: {}, Error: {}",
                    jsonString, e.getMessage());
            throw new RuntimeException(
                    "Nacos config content is not valid JSON, but dot notation was used. Please ensure the config is in JSON format or remove the dot notation. Content: "
                            + jsonString,
                    e);
        } catch (Exception e) {
            logger.error("[extractJsonValueFromNacos] Failed to extract JSON value from Nacos config: {}",
                    e.getMessage(), e);
            throw e;
        }
    }

    private String processTemplateString(String template, Map<String, Object> params) {
        Map<String, Object> args = (Map<String, Object>) params.get("args");
        String extendedData = (String) params.get("extendedData");
        logger.debug("[processTemplateString] template: {} args: {} extendedData: {}", template, args, extendedData);
        if (template == null || template.isEmpty()) {
            return "";
        }
        Matcher matcher = TEMPLATE_PATTERN.matcher(template);
        StringBuilder result = new StringBuilder();
        while (matcher.find()) {
            // Get full path, e.g. .args.name or .data.key1.key2
            String fullPath = matcher.group(1);
            String replacement = resolvePathValue(fullPath, args, extendedData);
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(result);
        String finalResult = result.toString();
        finalResult = processNacosConfigRefTemplate(finalResult);
        logger.debug("[processTemplateString] final result: {}", finalResult);

        return finalResult;
    }

    /**
     * Resolve value by path
     *
     * @param fullPath Full path, e.g. .args.name or .data.key1.key2
     * @param args Parameter data mapping
     * @param extendedData Extended data (JSON string)
     * @return Resolved value
     */
    private String resolvePathValue(String fullPath, Map<String, Object> args, String extendedData) {
        if (fullPath == null) {
            return extendedData != null ? extendedData : "";
        }
        // Remove leading dot
        if (fullPath.startsWith(".")) {
            fullPath = fullPath.substring(1);
        }
        if (StringUtils.isBlank(fullPath)) {
            return extendedData != null ? extendedData : "";
        }

        String[] pathParts = fullPath.split("\\.");
        if (pathParts.length == 0) {
            return "";
        }

        // Determine data source
        Object dataSource;
        if (pathParts[0].equals("args")) {
            // Get value from args
            dataSource = args;
            // If only args without specific field name
            if (pathParts.length == 1) {
                if (args != null && args.size() == 1) {
                    return String.valueOf(args.values().iterator().next());
                } else if (args != null && !args.isEmpty()) {
                    return args.toString();
                } else {
                    return "";
                }
            }
        } else {
            // Get value from extendedData
            // First parse extendedData string as JSON object
            try {
                if (StringUtils.isNoneBlank(extendedData)) {
                    dataSource = objectMapper.readValue(extendedData, Map.class);
                } else {
                    dataSource = null;
                }
            } catch (Exception e) {
                logger.warn("[resolvePathValue] Failed to parse extendedData as JSON: {}", e.getMessage());
                // If parsing fails, treat extendedData as plain string
                if (pathParts.length == 1 && fullPath.equals("extendedData")) {
                    return extendedData != null ? extendedData : "";
                }
                return "";
            }

            // Special handling for direct access to extendedData
            if (pathParts.length == 1 && fullPath.equals("extendedData")) {
                return extendedData != null ? extendedData : "";
            }
        }

        // If data source is empty
        if (dataSource == null) {
            return "";
        }
        // Process nested path
        Object currentValue = dataSource;
        int startIndex = pathParts[0].equals("args") ? 1 : 0;
        // If args, start from index 1; otherwise start from index 0

        for (int i = startIndex; i < pathParts.length; i++) {
            String key = pathParts[i];
            if (currentValue instanceof Map) {
                Map<String, Object> currentMap = (Map<String, Object>) currentValue;
                currentValue = currentMap.get(key);
            } else {
                logger.warn("[resolvePathValue] Cannot access key '{}' from non-map value", key);
                return "";
            }

            if (currentValue == null) {
                logger.warn("[resolvePathValue] Key '{}' not found in nested path", key);
                return "";
            }
        }
        return currentValue.toString();
    }

    @Override
    public ToolDefinition getToolDefinition() {
        return this.toolDefinition;
    }

    @Override
    public String call(@NonNull final String input) {
        return call(input, new ToolContext(Maps.newHashMap()));
    }

    @Override
    @SuppressWarnings("unchecked")
    public String call(@NonNull final String input, final ToolContext toolContext) {
        try {
            logger.info("[call] input: {} toolContext: {}", input, JacksonUtils.toJson(toolContext));

            // Parameter validation
            if (this.toolDefinition == null) {
                throw new IllegalStateException("Tool definition is null");
            }

            // input parsing
            logger.info("[call] input string: {}", input);
            Map<String, Object> args = new HashMap<>();
            if (!input.isEmpty()) {
                try {
                    args = objectMapper.readValue(input, Map.class);
                    logger.info("[call] parsed args: {}", args);
                } catch (Exception e) {
                    logger.error("[call] Failed to parse input to args", e);
                    // If parsing fails, try to handle as single parameter
                    args.put("input", input);
                }
            }

            String protocol = this.toolDefinition.getProtocol();
            if (protocol == null) {
                throw new IllegalStateException("Protocol is null");
            }

            McpServerRemoteServiceConfig remoteServerConfig = this.toolDefinition.getRemoteServerConfig();
            if (remoteServerConfig == null) {
                throw new IllegalStateException("Remote server config is null");
            }
            // Dispatch to different handling methods based on protocol type
            if ("http".equalsIgnoreCase(protocol) || "https".equalsIgnoreCase(protocol)) {
                return handleHttpHttpsProtocol(args, remoteServerConfig, protocol);
            } else if ("mcp-sse".equalsIgnoreCase(protocol) || "mcp-streamable".equalsIgnoreCase(protocol)) {
                return handleMcpStreamProtocol(args, remoteServerConfig, protocol);
            } else {
                logger.error("[call] Unsupported protocol: {}", protocol);
                return "Error: Unsupported protocol " + protocol;
            }
        } catch (Exception e) {
            logger.error("[call] Unexpected error occurred", e);
            return "Error: " + e.getMessage();
        }
    }

    /**
     * Handle tool call for HTTP/HTTPS protocol
     */
    private String handleHttpHttpsProtocol(Map<String, Object> args, McpServerRemoteServiceConfig remoteServerConfig,
                                           String protocol) throws NacosException {
        McpServiceRef serviceRef = remoteServerConfig.getServiceRef();
        if (serviceRef == null) {
            logger.error("[handleHttpHttpsProtocol] serviceRef is null");
            return "Error: service reference is null";
        }
        McpEndpointInfo mcpEndpointInfo = nacosMcpOperationService.selectEndpoint(serviceRef);
        if (mcpEndpointInfo == null) {
            throw new RuntimeException("No available endpoint found for service: " + serviceRef.getServiceName());
        }

        logger.info("Tool callback instance: {}", JacksonUtils.toJson(mcpEndpointInfo));
        McpToolMeta toolMeta = this.toolDefinition.getToolMeta();
        String baseUrl = protocol + "://" + mcpEndpointInfo.getAddress() + ":" + mcpEndpointInfo.getPort();

        if (toolMeta == null || toolMeta.getTemplates() == null) {
            logger.warn("[handleHttpHttpsProtocol] templates not found in toolsMeta");
            return "Error: templates not found in tool metadata";
        }

        Map<String, Object> templates = toolMeta.getTemplates();
        if (templates != null && templates.containsKey("json-go-template")) {
            Object jsonGoTemplate = templates.get("json-go-template");
            try {
                logger.info("[handleHttpHttpsProtocol] json-go-template: {}",
                        objectMapper.writeValueAsString(jsonGoTemplate));
            } catch (JsonProcessingException e) {
                logger.error("[handleHttpHttpsProtocol] Failed to serialize json-go-template", e);
            }
            try {
                // Call executeToolRequest
                String configJson = objectMapper.writeValueAsString(jsonGoTemplate);
                logger.info("[handleHttpHttpsProtocol] configJson: {} args: {} baseUrl: {}", configJson, args,
                        baseUrl);
                return processToolRequest(configJson, args, baseUrl).block();
            } catch (Exception e) {
                logger.error("Failed to execute tool request", e);
                return "Error: " + e.getMessage();
            }
        } else {
            logger.warn("[handleHttpHttpsProtocol] json-go-template not found in templates");
            return "Error: json-go-template not found in tool configuration";
        }

    }

    /**
     * Handle tool call for MCP streaming protocol (mcp-sse, mcp-streamable)
     */
    private String handleMcpStreamProtocol(Map<String, Object> args, McpServerRemoteServiceConfig remoteServerConfig,
                                           String protocol) throws NacosException {
        McpServiceRef serviceRef = remoteServerConfig.getServiceRef();
		if (serviceRef == null) {
			logger.error("[handleMcpStreamProtocol] serviceRef is null");
			return "Error: service reference is null";
		}
        McpEndpointInfo mcpEndpointInfo = nacosMcpOperationService.selectEndpoint(serviceRef);
        if (mcpEndpointInfo == null) {
            throw new RuntimeException("No available endpoint found for service: " + serviceRef.getServiceName());
        }

        logger.info("[handleMcpStreamProtocol] Tool callback instance: {}", JacksonUtils.toJson(mcpEndpointInfo));
        String exportPath = remoteServerConfig.getExportPath();

        // Build base URL
        String baseUrl = "http://" + mcpEndpointInfo.getAddress() + ":" + mcpEndpointInfo.getPort();

        logger.info("[handleMcpStreamProtocol] Processing {} protocol with args: {} and baseUrl: {}", protocol,
                args, baseUrl);

        try {
            // Get tool name - extract actual tool name from tool definition name
            String toolDefinitionName = this.toolDefinition.name();
            if (toolDefinitionName.isEmpty()) {
                throw new RuntimeException("Tool definition name is not available");
            }

            // Tool definition name format: serverName_tools_toolName
            // Need to extract the last toolName part
            String toolName;
            if (toolDefinitionName.contains("_tools_")) {
                toolName = toolDefinitionName.substring(toolDefinitionName.lastIndexOf("_tools_") + 7);
            } else {
                // If no _tools_ separator, use the whole name
                toolName = toolDefinitionName;
            }

            if (toolName.isEmpty()) {
                throw new RuntimeException("Extracted tool name is empty");
            }

            // Build transport layer
            String sseEndpoint = "/sse";
            if (exportPath != null && !exportPath.isEmpty()) {
                sseEndpoint = exportPath;
            }

            McpClientTransport transport;
            if ("mcp-streamable".equalsIgnoreCase(protocol)) {
                // Use WebClientStreamableHttpTransport for streamable protocol
                WebClient.Builder webClientBuilder = this.webClientBuilder.clone().baseUrl(baseUrl);
                transport = WebClientStreamableHttpTransport.builder(webClientBuilder)
                        .endpoint(sseEndpoint)
                        .build();
                logger.info("[handleMcpStreamProtocol] Using WebClientStreamableHttpTransport for mcp-streamable");
            } else {
                // Use HttpClientSseClientTransport for SSE protocol
                transport = HttpClientSseClientTransport.builder(baseUrl)
                        .sseEndpoint(sseEndpoint)
                        .build();
                logger.info("[handleMcpStreamProtocol] Using HttpClientSseClientTransport for mcp-sse");
            }

            // Create MCP sync client
            McpSyncClient client = McpClient.sync(transport).build();

            try {
                // Initialize client
                InitializeResult initializeResult = client.initialize();
                logger.info("[handleMcpStreamProtocol] MCP Client initialized: {}", initializeResult);

                // Call tool
                McpSchema.CallToolRequest request = new McpSchema.CallToolRequest(toolName, args);
                logger.info("[handleMcpStreamProtocol] CallToolRequest: {}", request);

                CallToolResult result = client.callTool(request);
                logger.info("[handleMcpStreamProtocol] tool call result: {}", result);

                // Process result
                Object content = result.content();
                if (content instanceof List<?> list && !CollectionUtils.isEmpty(list)) {
                    Object first = list.get(0);
                    // Compatible with TextContent's text field
                    if (first instanceof TextContent textContent) {
                        return textContent.text();
                    } else if (first instanceof Map<?, ?> map && map.containsKey("text")) {
                        return map.get("text").toString();
                    } else {
                        return first.toString();
                    }
                } else {
                    return content != null ? content.toString() : "No content returned";
                }
            } finally {
                // Clean up resources
                try {
                    if (client != null) {
                        client.close();
                    }
                } catch (Exception e) {
                    logger.warn("[handleMcpStreamProtocol] Failed to close MCP client", e);
                }
            }
        } catch (Exception e) {
            logger.error("[handleMcpStreamProtocol] MCP call failed:", e);
            return "Error: MCP call failed - " + e.getMessage();
        }
    }

    private Duration getTimeoutDuration() {

        return Duration.ofSeconds(30); // Default timeout
    }

    /**
     * Close.
     */
    public void close() {

        for (Map.Entry<String, AbstractListener> entry : nacosConfigListeners.entrySet()) {
            String cacheKey = entry.getKey();
            String dataId = cacheKey.split("@@")[0];
            String group = cacheKey.split("@@")[1];
            nacosMcpOperationService.getConfigService().removeListener(dataId, group, entry.getValue());
        }
    }

}
