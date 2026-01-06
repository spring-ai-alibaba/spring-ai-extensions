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

package com.alibaba.cloud.ai.mcp.discovery.client.transport.sse;

import com.alibaba.cloud.ai.mcp.common.transport.builder.WebFluxSseClientTransportBuilder;
import com.alibaba.cloud.ai.mcp.discovery.client.transport.DistributedAsyncMcpClient;
import com.alibaba.cloud.ai.mcp.utils.CommonUtil;
import com.alibaba.cloud.ai.mcp.utils.NacosMcpClientUtil;
import com.alibaba.cloud.ai.mcp.nacos.service.NacosMcpOperationService;
import com.alibaba.cloud.ai.mcp.nacos.service.model.NacosMcpServerEndpoint;
import com.alibaba.nacos.api.ai.constant.AiConstants;
import com.alibaba.nacos.api.ai.model.mcp.McpEndpointInfo;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.utils.StringUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.modelcontextprotocol.client.McpAsyncClient;
import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.transport.WebFluxSseClientTransport;
import io.modelcontextprotocol.json.McpJsonMapper;
import io.modelcontextprotocol.json.jackson.JacksonMcpJsonMapper;
import io.modelcontextprotocol.spec.McpSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.mcp.client.common.autoconfigure.NamedClientMcpTransport;
import org.springframework.ai.mcp.client.common.autoconfigure.configurer.McpAsyncClientConfigurer;
import org.springframework.ai.mcp.client.common.autoconfigure.properties.McpClientCommonProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author yingzi
 * @since 2025/10/25
 */

public class SseWebFluxDistributedAsyncMcpClient implements DistributedAsyncMcpClient {

    private static final Logger logger = LoggerFactory.getLogger(SseWebFluxDistributedAsyncMcpClient.class);

    private final String serverName;

    private final String version;

    private final NacosMcpOperationService nacosMcpOperationService;

    private final McpClientCommonProperties commonProperties;

    private final McpAsyncClientConfigurer mcpAsyncClientConfigurer;

    private final WebClient.Builder webClientBuilderTemplate;

    private final McpJsonMapper mcpJsonMapper;

    private final boolean lazyInit;

    private final AtomicInteger index = new AtomicInteger(0);

    private Map<String, McpAsyncClient> keyToClientMap;

    private NacosMcpServerEndpoint serverEndpoint;

    // Link Tracking Filters
    private final ExchangeFilterFunction traceFilter;

    public SseWebFluxDistributedAsyncMcpClient(String serverName, String version,
                                               NacosMcpOperationService nacosMcpOperationService,
                                               ApplicationContext applicationContext, boolean lazyInit) {
        Assert.notNull(serverName, "serviceName cannot be null");
        Assert.notNull(version, "version cannot be null");
        Assert.notNull(nacosMcpOperationService, "nacosMcpOperationService cannot be null");
        Assert.notNull(applicationContext, "applicationContext cannot be null");

        this.serverName = serverName;
        this.version = version;
        this.nacosMcpOperationService = nacosMcpOperationService;
        this.lazyInit = lazyInit;

        commonProperties = applicationContext.getBean(McpClientCommonProperties.class);
        mcpAsyncClientConfigurer = applicationContext.getBean(McpAsyncClientConfigurer.class);
        webClientBuilderTemplate = applicationContext.getBean(WebClient.Builder.class);
        mcpJsonMapper = new JacksonMcpJsonMapper(applicationContext.getBean(ObjectMapper.class));
        // Try to get the link tracking filter
        ExchangeFilterFunction tempTraceFilter = null;
        try {
            tempTraceFilter = applicationContext.getBean("mcpTraceExchangeFilterFunction",
                    ExchangeFilterFunction.class);
        }
        catch (Exception e) {
            // The link tracking filter does not exist, continue normal operation
            logger.debug("MCP trace filter not found, continuing without tracing: {}", e.getMessage());
        }
        this.traceFilter = tempTraceFilter;
    }

    public Map<String, McpAsyncClient> init() {
        keyToClientMap = new ConcurrentHashMap<>();
        boolean initialized = initServerEndpoint(serverName, version);
        if (!initialized) {
            logger.info("[Nacos Mcp Async Client] No MCP server endpoint found during init. serverName: {}, version: {}",
                serverName, version);
            return keyToClientMap;
        }
        for (McpEndpointInfo mcpEndpointInfo : serverEndpoint.getMcpEndpointInfoList()) {
            updateByAddEndpoint(mcpEndpointInfo, serverEndpoint.getExportPath());
        }
        logger.info("[Nacos Mcp Async Client] McpAsyncClient init, serverName: {}, version: {}, endpoint: {}", serverName,
                version, serverEndpoint);
        return keyToClientMap;
    }

    public void subscribe() {
        String serverNameAndVersion = this.serverName + "::" + this.version;
        this.nacosMcpOperationService.subscribeNacosMcpServer(serverNameAndVersion, mcpServerDetailInfo -> {
            List<McpEndpointInfo> mcpEndpointInfoList = mcpServerDetailInfo.getBackendEndpoints() == null
                    ? new ArrayList<>() : mcpServerDetailInfo.getBackendEndpoints();
            String exportPath = mcpServerDetailInfo.getRemoteServerConfig().getExportPath();
            String protocol = mcpServerDetailInfo.getProtocol();
            String realVersion = mcpServerDetailInfo.getVersionDetail().getVersion();
            NacosMcpServerEndpoint nacosMcpServerEndpoint = new NacosMcpServerEndpoint(mcpEndpointInfoList, exportPath,
                    protocol, realVersion);
            if (!StringUtils.equals(protocol, AiConstants.Mcp.MCP_PROTOCOL_SSE)) {
                return;
            }
            updateClientList(nacosMcpServerEndpoint);
        });
        logger.info("[Nacos Mcp Async Client] Subscribe Mcp Server from nacos, serverName: {}, version: {}", serverName,
                version);
    }

    public McpAsyncClient getMcpAsyncClient() {
        List<McpAsyncClient> asynClients = getMcpAsyncClientList();
        if (asynClients.isEmpty()) {
            throw new IllegalStateException("[Nacos Mcp Async Client] No McpAsyncClient available, name:" + serverName);
        }

        int currentIndex = index.getAndUpdate(index -> (index + 1) % asynClients.size());
        return asynClients.get(currentIndex);
    }

    public List<McpAsyncClient> getMcpAsyncClientList() {
        return keyToClientMap.values().stream().toList();
    }

    public String getServerName() {
        return serverName;
    }

    public NacosMcpServerEndpoint getNacosMcpServerEndpoint() {
        return this.serverEndpoint;
    }

    private void updateByAddEndpoint(McpEndpointInfo mcpEndpointInfo, String exportPath) {
        McpAsyncClient mcpAsyncClient = clientByEndpoint(mcpEndpointInfo, exportPath);
        String key = NacosMcpClientUtil.getMcpEndpointInfoId(mcpEndpointInfo, exportPath);
        keyToClientMap.putIfAbsent(key, mcpAsyncClient);
    }

    private McpAsyncClient clientByEndpoint(McpEndpointInfo mcpEndpointInfo, String exportPath) {
        McpAsyncClient mcpAsyncClient;

        String protocol = NacosMcpClientUtil.checkProtocol(mcpEndpointInfo);
        String baseUrl = protocol + "://" + mcpEndpointInfo.getAddress() + ":" + mcpEndpointInfo.getPort();
        WebClient.Builder webClientBuilder = webClientBuilderTemplate.clone().baseUrl(baseUrl);

        WebFluxSseClientTransport transport;
        if (traceFilter != null) {
            transport = WebFluxSseClientTransportBuilder.build(webClientBuilder, mcpJsonMapper, exportPath);
        } else {
            transport = WebFluxSseClientTransportBuilder.build(webClientBuilder, mcpJsonMapper, exportPath, traceFilter);
        }

        NamedClientMcpTransport namedClientMcpTransport = new NamedClientMcpTransport(
                serverName + "-" + NacosMcpClientUtil.getMcpEndpointInfoId(mcpEndpointInfo, exportPath),
                transport);
        McpSchema.Implementation clientInfo = new McpSchema.Implementation(
                CommonUtil.connectedClientName(commonProperties.getName(), namedClientMcpTransport.name()),
                commonProperties.getVersion()
        );

        McpClient.AsyncSpec spec = McpClient.async(namedClientMcpTransport.transport())
                .clientInfo(clientInfo)
                ;
        spec = mcpAsyncClientConfigurer.configure(namedClientMcpTransport.name(), spec);
        mcpAsyncClient = spec.build();
        if (commonProperties.isInitialized()) {
            mcpAsyncClient.initialize().block();
        }
        logger.info("Added McpAsyncClient: {}", clientInfo.name());
        return mcpAsyncClient;
    }

    private void updateClientList(NacosMcpServerEndpoint newServerEndpoint) {
        if (this.serverEndpoint == null) {
            for (McpEndpointInfo mcpEndpointInfo : newServerEndpoint.getMcpEndpointInfoList()) {
                updateByAddEndpoint(mcpEndpointInfo, newServerEndpoint.getExportPath());
            }
            this.serverEndpoint = newServerEndpoint;
            return;
        }
        if (!StringUtils.equals(this.serverEndpoint.getExportPath(), newServerEndpoint.getExportPath())
                || !StringUtils.equals(this.serverEndpoint.getVersion(), newServerEndpoint.getVersion())) {
            logger.info(
                    "[Nacos Mcp Async Client] Mcp server {} exportPath or protocol changed, need to update all endpoints: {}",
                    serverName, newServerEndpoint);
            updateAll(newServerEndpoint);
        }
        else {
            List<McpEndpointInfo> currentMcpEndpointInfoList = this.serverEndpoint.getMcpEndpointInfoList();
            List<McpEndpointInfo> newMcpEndpointInfoList = newServerEndpoint.getMcpEndpointInfoList();
            List<McpEndpointInfo> addEndpointInfoList = newMcpEndpointInfoList.stream()
                    .filter(newEndpoint -> currentMcpEndpointInfoList.stream()
                            .noneMatch(currentEndpoint -> currentEndpoint.getAddress().equals(newEndpoint.getAddress())
                                    && currentEndpoint.getPort() == newEndpoint.getPort()))
                    .toList();
            List<McpEndpointInfo> removeEndpointInfoList = currentMcpEndpointInfoList.stream()
                    .filter(currentEndpoint -> newMcpEndpointInfoList.stream()
                            .noneMatch(newEndpoint -> newEndpoint.getAddress().equals(currentEndpoint.getAddress())
                                    && newEndpoint.getPort() == currentEndpoint.getPort()))
                    .toList();
            if (!addEndpointInfoList.isEmpty()) {
                logger.info("[Nacos Mcp Async Client] Mcp server {} endpoints changed, endpoints need to add {}",
                        serverName, addEndpointInfoList);
            }
            for (McpEndpointInfo addEndpointInfo : addEndpointInfoList) {
                updateByAddEndpoint(addEndpointInfo, newServerEndpoint.getExportPath());
            }
            if (!removeEndpointInfoList.isEmpty()) {
                logger.info("[Nacos Mcp Async Client] Mcp server {} endpoints changed, endpoints need to remove {}",
                        serverName, removeEndpointInfoList);
            }
            for (McpEndpointInfo removeEndpointInfo : removeEndpointInfoList) {
                updateByRemoveEndpoint(removeEndpointInfo, newServerEndpoint.getExportPath());
            }
        }
        this.serverEndpoint = newServerEndpoint;
    }

    private boolean initServerEndpoint(String serverName, String version) {
        try {
            this.serverEndpoint = this.nacosMcpOperationService.getServerEndpoint(serverName, version);
            if (this.serverEndpoint == null) {
                throw new NacosException(NacosException.NOT_FOUND,
                    String.format("[Nacos Mcp Async Client] Can not find mcp server from nacos: %s, version:%s",
                        serverName, version));
            }
            if (!StringUtils.equals(serverEndpoint.getProtocol(), AiConstants.Mcp.MCP_PROTOCOL_SSE)) {
                throw new RuntimeException(
                    String.format("[Nacos Mcp Async Client] Protocol of mcp server:%s, version :%s must be sse",
                        serverName, version));
            }
            return true;
        } catch (NacosException e) {
            if (lazyInit) {
                logger.warn("[Nacos Mcp Async Client] Failed to get endpoints for Mcp Server from nacos: {}, " +
                    "version:{}", serverName, version, e);
                this.serverEndpoint = null;
                return false;
            }
            throw new RuntimeException(String.format(
                "[Nacos Mcp Async Client] Failed to get endpoints for Mcp Server from nacos: %s, version:%s",
                serverName, version), e);
        }
    }

    private void updateAll(NacosMcpServerEndpoint newServerEndpoint) {
        Map<String, McpAsyncClient> newKeyToClientMap = new ConcurrentHashMap<>();
        Map<String, McpAsyncClient> oldKeyToClientMap = this.keyToClientMap;
        Map<String, Integer> newKeyToCountMap = new ConcurrentHashMap<>();
        for (McpEndpointInfo mcpEndpointInfo : newServerEndpoint.getMcpEndpointInfoList()) {
            McpAsyncClient syncClient = clientByEndpoint(mcpEndpointInfo, newServerEndpoint.getExportPath());
            String key = NacosMcpClientUtil.getMcpEndpointInfoId(mcpEndpointInfo, newServerEndpoint.getExportPath());
            newKeyToClientMap.putIfAbsent(key, syncClient);
            newKeyToCountMap.putIfAbsent(key, 0);
        }
        this.keyToClientMap = newKeyToClientMap;
        for (Map.Entry<String, McpAsyncClient> entry : oldKeyToClientMap.entrySet()) {
            McpAsyncClient asyncClient = entry.getValue();
            logger.info("Removing McpAsyncClient: {}", asyncClient.getClientInfo().name());
            asyncClient.closeGracefully().block();
            logger.info("Removed McpAsyncClient: {} Success", asyncClient.getClientInfo().name());
        }
    }

    private void updateByRemoveEndpoint(McpEndpointInfo serverEndpoint, String exportPath) {
        String key = NacosMcpClientUtil.getMcpEndpointInfoId(serverEndpoint, exportPath);
        if (keyToClientMap.containsKey(key)) {
            McpAsyncClient asyncClient = keyToClientMap.remove(key);
            logger.info("Removing McpAsyncClient: {}", asyncClient.getClientInfo().name());
            asyncClient.closeGracefully().block();
            logger.info("Removed McpAsyncClient: {} Success", asyncClient.getClientInfo().name());
        }
    }

    // ---------------------------原始调用方法------------------------------//
    public McpSchema.ServerCapabilities getServerCapabilities() {
        return getMcpAsyncClient().getServerCapabilities();
    }

    public String getServerInstructions() {
        return getMcpAsyncClient().getServerInstructions();
    }

    public McpSchema.Implementation getServerInfo() {
        return getMcpAsyncClient().getServerInfo();
    }

    public McpSchema.ClientCapabilities getClientCapabilities() {
        return getMcpAsyncClient().getClientCapabilities();
    }

    public McpSchema.Implementation getClientInfo() {
        return getMcpAsyncClient().getClientInfo();
    }

    public void close() {
        Iterator<McpAsyncClient> iterator = getMcpAsyncClientList().iterator();
        while (iterator.hasNext()) {
            McpAsyncClient mcpAsyncClient = iterator.next();
            mcpAsyncClient.close();
            iterator.remove();
            logger.info("[Nacos Mcp Async Client] Closed and removed McpAsyncClient: {}",
                    mcpAsyncClient.getClientInfo().name());
        }
    }

    public Mono<Void> closeGracefully() {
        Iterator<McpAsyncClient> iterator = getMcpAsyncClientList().iterator();
        List<Mono<Void>> closeMonos = new ArrayList<>();
        while (iterator.hasNext()) {
            McpAsyncClient mcpAsyncClient = iterator.next();
            Mono<Void> voidMono = mcpAsyncClient.closeGracefully().doOnSuccess(v -> {
                iterator.remove();
                logger.info("[Nacos Mcp Async Client] Closed and removed McpAsyncClient: {}",
                        mcpAsyncClient.getClientInfo().name());
            });
            closeMonos.add(voidMono);
        }
        return Mono.when(closeMonos);
    }

    public Mono<Object> ping() {
        return getMcpAsyncClient().ping();
    }

    public Mono<Void> addRoot(McpSchema.Root root) {
        return Mono.when(getMcpAsyncClientList().stream()
                .map(mcpAsyncClient -> mcpAsyncClient.addRoot(root))
                .collect(Collectors.toList()));
    }

    public Mono<Void> removeRoot(String rootUri) {
        return Mono.when(getMcpAsyncClientList().stream()
                .map(mcpAsyncClient -> mcpAsyncClient.removeRoot(rootUri))
                .collect(Collectors.toList()));
    }

    public Mono<Void> rootsListChangedNotification() {
        return Mono.when(getMcpAsyncClientList().stream()
                .map(McpAsyncClient::rootsListChangedNotification)
                .collect(Collectors.toList()));
    }

    public Mono<McpSchema.CallToolResult> callTool(McpSchema.CallToolRequest callToolRequest) {
        return getMcpAsyncClient().callTool(callToolRequest);
    }

    public Mono<McpSchema.ListToolsResult> listTools() {
        return listToolsInternal(null);
    }

    public Mono<McpSchema.ListToolsResult> listTools(String cursor) {
        return listToolsInternal(cursor);
    }

    private Mono<McpSchema.ListToolsResult> listToolsInternal(String cursor) {
        return getMcpAsyncClient().listTools(cursor);
    }

    public Mono<McpSchema.ListResourcesResult> listResources() {
        return getMcpAsyncClient().listResources();
    }

    public Mono<McpSchema.ListResourcesResult> listResources(String cursor) {
        return getMcpAsyncClient().listResources(cursor);
    }

    public Mono<McpSchema.ReadResourceResult> readResource(McpSchema.Resource resource) {
        return getMcpAsyncClient().readResource(resource);
    }

    public Mono<McpSchema.ReadResourceResult> readResource(McpSchema.ReadResourceRequest readResourceRequest) {
        return getMcpAsyncClient().readResource(readResourceRequest);
    }

    public Mono<McpSchema.ListResourceTemplatesResult> listResourceTemplates() {
        return getMcpAsyncClient().listResourceTemplates();
    }
    public Mono<McpSchema.ListResourceTemplatesResult> listResourceTemplates(String cursor) {
        return getMcpAsyncClient().listResourceTemplates(cursor);
    }

    public Mono<Void> subscribeResource(McpSchema.SubscribeRequest subscribeRequest) {
        return Mono.when(getMcpAsyncClientList().stream()
                .map(mcpAsyncClient -> mcpAsyncClient.subscribeResource(subscribeRequest))
                .collect(Collectors.toList()));
    }

    public Mono<Void> unsubscribeResource(McpSchema.UnsubscribeRequest unsubscribeRequest) {
        return Mono.when(getMcpAsyncClientList().stream()
                .map(mcpAsyncClient -> mcpAsyncClient.unsubscribeResource(unsubscribeRequest))
                .collect(Collectors.toList()));
    }

    public Mono<McpSchema.ListPromptsResult> listPrompts() {
        return getMcpAsyncClient().listPrompts();
    }

    public Mono<McpSchema.ListPromptsResult> listPrompts(String cursor) {
        return getMcpAsyncClient().listPrompts(cursor);
    }

    public Mono<McpSchema.GetPromptResult> getPrompt(McpSchema.GetPromptRequest getPromptRequest) {
        return getMcpAsyncClient().getPrompt(getPromptRequest);
    }

    public Mono<Void> setLoggingLevel(McpSchema.LoggingLevel loggingLevel) {
        return Mono.when(getMcpAsyncClientList().stream()
                .map(mcpAsyncClient -> mcpAsyncClient.setLoggingLevel(loggingLevel))
                .collect(Collectors.toList()));
    }

    public Mono<McpSchema.CompleteResult> completeCompletion(McpSchema.CompleteRequest completeRequest) {
        return getMcpAsyncClient().completeCompletion(completeRequest);
    }
    // ---------------------------原始调用方法------------------------------//

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String serverName;

        private String version;

        private NacosMcpOperationService nacosMcpOperationService;

        private ApplicationContext applicationContext;

        private boolean lazyInit;

        public Builder serverName(String serverName) {
            this.serverName = serverName;
            return this;
        }

        public Builder version(String version) {
            this.version = version;
            return this;
        }

        public Builder nacosMcpOperationService(NacosMcpOperationService nacosMcpOperationService) {
            this.nacosMcpOperationService = nacosMcpOperationService;
            return this;
        }

        public Builder applicationContext(ApplicationContext applicationContext) {
            this.applicationContext = applicationContext;
            return this;
        }

        public Builder lazyInit(boolean lazyInit) {
            this.lazyInit = lazyInit;
            return this;
        }

        public SseWebFluxDistributedAsyncMcpClient build() {
            return new SseWebFluxDistributedAsyncMcpClient(this.serverName, this.version,
                this.nacosMcpOperationService, this.applicationContext, this.lazyInit);
        }

    }

}
