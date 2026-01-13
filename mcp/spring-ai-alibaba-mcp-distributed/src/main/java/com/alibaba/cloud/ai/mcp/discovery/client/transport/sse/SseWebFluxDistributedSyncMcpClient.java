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
import com.alibaba.cloud.ai.mcp.discovery.client.transport.DistributedSyncMcpClient;
import com.alibaba.cloud.ai.mcp.utils.CommonUtil;
import com.alibaba.cloud.ai.mcp.utils.NacosMcpClientUtil;
import com.alibaba.cloud.ai.mcp.nacos.service.NacosMcpOperationService;
import com.alibaba.cloud.ai.mcp.nacos.service.model.NacosMcpServerEndpoint;
import com.alibaba.nacos.api.ai.constant.AiConstants;
import com.alibaba.nacos.api.ai.model.mcp.McpEndpointInfo;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.utils.StringUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.WebFluxSseClientTransport;
import io.modelcontextprotocol.json.McpJsonMapper;
import io.modelcontextprotocol.json.jackson.JacksonMcpJsonMapper;
import io.modelcontextprotocol.spec.McpSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.mcp.client.common.autoconfigure.NamedClientMcpTransport;
import org.springframework.ai.mcp.client.common.autoconfigure.configurer.McpSyncClientConfigurer;
import org.springframework.ai.mcp.client.common.autoconfigure.properties.McpClientCommonProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author yingzi
 * @since 2025/10/25
 */
public class SseWebFluxDistributedSyncMcpClient implements DistributedSyncMcpClient {

    private static final Logger logger = LoggerFactory.getLogger(SseWebFluxDistributedSyncMcpClient.class);

    private final String serverName;

    private final String version;

    private final NacosMcpOperationService nacosMcpOperationService;

    private final McpClientCommonProperties commonProperties;

    private final McpSyncClientConfigurer mcpSyncClientConfigurer;

    private final WebClient.Builder webClientBuilderTemplate;

    private final McpJsonMapper mcpJsonMapper;

    private final boolean lazyInit;

    private final AtomicInteger index = new AtomicInteger(0);

    private Map<String, McpSyncClient> keyToClientMap;

    private NacosMcpServerEndpoint serverEndpoint;

    // Link Tracking Filters
    private final ExchangeFilterFunction traceFilter;

    public SseWebFluxDistributedSyncMcpClient(String serverName, String version,
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
        mcpSyncClientConfigurer = applicationContext.getBean(McpSyncClientConfigurer.class);
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

    public Map<String, McpSyncClient> init() {
        keyToClientMap = new ConcurrentHashMap<>();
        boolean initialized = initServerEndpoint(serverName, version);
        if (!initialized) {
            logger.info("[Nacos Mcp Sync Client] No MCP server endpoint found during init. serverName: {}, version: {}",
                    serverName, version);
            return keyToClientMap;
        }
        for (McpEndpointInfo mcpEndpointInfo : serverEndpoint.getMcpEndpointInfoList()) {
            updateByAddEndpoint(mcpEndpointInfo, serverEndpoint.getExportPath());
        }
        logger.info("[Nacos Mcp Sync Client] McpSyncClient init, serverName: {}, version: {}, endpoint: {}", serverName,
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
            updateClientList(nacosMcpServerEndpoint);
        });
        logger.info("[Nacos Mcp Sync Client] Subscribe Mcp Server from nacos, serverName: {}, version: {}", serverName,
                version);
    }

    public McpSyncClient getMcpSyncClient() {
        List<McpSyncClient> syncClients = getMcpSyncClientList();
        if (syncClients.isEmpty()) {
            throw new IllegalStateException("[Nacos Mcp Sync Client] No McpSyncClient available, name :" + serverName);
        }
        int currentIndex = index.getAndUpdate(index -> (index + 1) % syncClients.size());

        return syncClients.get(currentIndex);
    }

    public List<McpSyncClient> getMcpSyncClientList() {
        return keyToClientMap.values().stream().toList();
    }

    public String getServerName() {
        return serverName;
    }

    public NacosMcpServerEndpoint getNacosMcpServerEndpoint() {
        return this.serverEndpoint;
    }

    private void updateByAddEndpoint(McpEndpointInfo mcpEndpointInfo, String exportPath) {
        McpSyncClient mcpSyncClient = clientByEndpoint(mcpEndpointInfo, exportPath);
        String key = NacosMcpClientUtil.getMcpEndpointInfoId(mcpEndpointInfo, exportPath);
        keyToClientMap.putIfAbsent(key, mcpSyncClient);
    }

    private McpSyncClient clientByEndpoint(McpEndpointInfo mcpEndpointInfo, String exportPath) {
        McpSyncClient syncClient;

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

        McpClient.SyncSpec spec = McpClient.sync(namedClientMcpTransport.transport())
                .clientInfo(clientInfo)
                ;
        spec = mcpSyncClientConfigurer.configure(namedClientMcpTransport.name(), spec);
        syncClient = spec.build();
        if (commonProperties.isInitialized()) {
            syncClient.initialize();
        }

        logger.info("Added McpSyncClient: {}", clientInfo.name());
        return syncClient;
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
                    "[Nacos Mcp Sync Client] Mcp server {} exportPath or protocol changed, need to update all endpoints: {}",
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
                logger.info("[Nacos Mcp Sync Client] Mcp server {} endpoints changed, endpoints need to add {}",
                        serverName, addEndpointInfoList);
            }
            for (McpEndpointInfo addEndpointInfo : addEndpointInfoList) {
                updateByAddEndpoint(addEndpointInfo, newServerEndpoint.getExportPath());
            }
            if (!removeEndpointInfoList.isEmpty()) {
                logger.info("[Nacos Mcp Sync Client] Mcp server {} endpoints changed, endpoints need to remove {}",
                        serverName, removeEndpointInfoList);
            }
            for (McpEndpointInfo removeEndpointInfo : removeEndpointInfoList) {
                updateByRemoveEndpoint(removeEndpointInfo, newServerEndpoint.getExportPath());
            }
        }
        this.serverEndpoint = newServerEndpoint;
    }

    protected boolean initServerEndpoint(String serverName, String version) {
        try {
            this.serverEndpoint = this.nacosMcpOperationService.getServerEndpoint(serverName, version);
            if (this.serverEndpoint == null) {
                throw new NacosException(NacosException.NOT_FOUND,
                        String.format("[Nacos Mcp Sync Client] Can not find mcp server from nacos: %s, version:%s",
                                serverName, version));
            }
            if (!StringUtils.equals(serverEndpoint.getProtocol(), AiConstants.Mcp.MCP_PROTOCOL_SSE)) {
                throw new RuntimeException(
                        String.format("[Nacos Mcp Sync Client] Protocol of mcp server:%s, version :%s must be sse",
                                serverName, version));
            }
            return true;
        } catch (NacosException e) {
            if (lazyInit) {
                logger.warn("[Nacos Mcp Sync Client] Failed to get endpoints for Mcp Server from nacos: {}, " +
                        "version:{}", serverName, version, e);
                this.serverEndpoint = null;
                return false;
            }
            throw new RuntimeException(String.format(
                    "[Nacos Mcp Sync Client] Failed to get endpoints for Mcp Server from nacos: %s, version:%s",
                    serverName, version), e);
        }
    }

    private void updateAll(NacosMcpServerEndpoint newServerEndpoint) {
        Map<String, McpSyncClient> newKeyToClientMap = new ConcurrentHashMap<>();
        Map<String, McpSyncClient> oldKeyToClientMap = this.keyToClientMap;
        Map<String, Integer> newKeyToCountMap = new ConcurrentHashMap<>();
        for (McpEndpointInfo mcpEndpointInfo : newServerEndpoint.getMcpEndpointInfoList()) {
            McpSyncClient syncClient = clientByEndpoint(mcpEndpointInfo, newServerEndpoint.getExportPath());
            String key = NacosMcpClientUtil.getMcpEndpointInfoId(mcpEndpointInfo, newServerEndpoint.getExportPath());
            newKeyToClientMap.putIfAbsent(key, syncClient);
            newKeyToCountMap.putIfAbsent(key, 0);
        }
        this.keyToClientMap = newKeyToClientMap;
        for (Map.Entry<String, McpSyncClient> entry : oldKeyToClientMap.entrySet()) {
            McpSyncClient syncClient = entry.getValue();
            logger.info("Removing McpSyncClient: {}", syncClient.getClientInfo().name());
            syncClient.closeGracefully();
            logger.info("Removed McpSyncClient: {} Success", syncClient.getClientInfo().name());
        }
    }

    private void updateByRemoveEndpoint(McpEndpointInfo serverEndpoint, String exportPath) {
        String key = NacosMcpClientUtil.getMcpEndpointInfoId(serverEndpoint, exportPath);
        if (keyToClientMap.containsKey(key)) {
            McpSyncClient syncClient = keyToClientMap.remove(key);
            logger.info("Removing McpSyncClient: {}", syncClient.getClientInfo().name());
            syncClient.closeGracefully();
            logger.info("Removed McpSyncClient: {} Success", syncClient.getClientInfo().name());
        }
    }

    // ---------------------------原始调用方法------------------------------//
    public McpSchema.ServerCapabilities getServerCapabilities() {
        return getMcpSyncClient().getServerCapabilities();
    }

    public String getServerInstructions() {
        return getMcpSyncClient().getServerInstructions();
    }

    public McpSchema.Implementation getServerInfo() {
        return getMcpSyncClient().getServerInfo();
    }

    public McpSchema.ClientCapabilities getClientCapabilities() {
        return getMcpSyncClient().getClientCapabilities();
    }

    public McpSchema.Implementation getClientInfo() {
        return getMcpSyncClient().getClientInfo();
    }

    public void close() {
        Iterator<McpSyncClient> iterator = getMcpSyncClientList().iterator();
        while (iterator.hasNext()) {
            McpSyncClient mcpSyncClient = iterator.next();
            mcpSyncClient.close();
            iterator.remove();
            logger.info("[Nacos Mcp Sync Client] Closed and removed McpSyncClient: {}",
                    mcpSyncClient.getClientInfo().name());
        }
    }

    public boolean closeGracefully() {
        List<Boolean> flagList = new ArrayList<>();
        Iterator<McpSyncClient> iterator = getMcpSyncClientList().iterator();
        while (iterator.hasNext()) {
            McpSyncClient mcpSyncClient = iterator.next();
            boolean flag = mcpSyncClient.closeGracefully();
            flagList.add(flag);
            if (flag) {
                iterator.remove();
                logger.info("[Nacos Mcp Sync Client] Closed and removed McpSyncClient: {}",
                        mcpSyncClient.getClientInfo().name());
            }
        }
        return !flagList.stream().allMatch(flag -> flag);
    }

    public void rootsListChangedNotification() {
        getMcpSyncClient().rootsListChangedNotification();
    }

    public void addRoot(McpSchema.Root root)     {
        for (McpSyncClient syncClient : getMcpSyncClientList()) {
            syncClient.addRoot(root);
        }
    }

    public void removeRoot(String rootUri) {
        for (McpSyncClient syncClient : getMcpSyncClientList()) {
            syncClient.removeRoot(rootUri);
        }
    }

    public Object ping() {
        return getMcpSyncClient().ping();
    }

    public McpSchema.CallToolResult callTool(McpSchema.CallToolRequest callToolRequest) {
        return getMcpSyncClient().callTool(callToolRequest);
    }

    public McpSchema.ListToolsResult listTools() {
        return getMcpSyncClient().listTools();
    }

    public McpSchema.ListToolsResult listTools(String cursor) {
        return getMcpSyncClient().listTools(cursor);
    }

    public McpSchema.ListResourcesResult listResources(String cursor) {
        return getMcpSyncClient().listResources(cursor);
    }

    public McpSchema.ListResourcesResult listResources() {
        return getMcpSyncClient().listResources();
    }

    public McpSchema.ReadResourceResult readResource(McpSchema.Resource resource) {
        return getMcpSyncClient().readResource(resource);
    }

    public McpSchema.ReadResourceResult readResource(McpSchema.ReadResourceRequest readResourceRequest) {
        return getMcpSyncClient().readResource(readResourceRequest);
    }

    public McpSchema.ListResourceTemplatesResult listResourceTemplates(String cursor) {
        return getMcpSyncClient().listResourceTemplates(cursor);
    }

    public McpSchema.ListResourceTemplatesResult listResourceTemplates() {
        return getMcpSyncClient().listResourceTemplates();
    }

    public void subscribeResource (McpSchema.SubscribeRequest subscribeRequest) {
        for (McpSyncClient syncClient : getMcpSyncClientList()) {
            syncClient.subscribeResource(subscribeRequest);
        }
    }

    public void unsubscribeResource(McpSchema.UnsubscribeRequest unsubscribeRequest) {
        for (McpSyncClient syncClient : getMcpSyncClientList()) {
            syncClient.unsubscribeResource(unsubscribeRequest);
        }
    }

    public McpSchema.ListPromptsResult listPrompts(String cursor) {
        return getMcpSyncClient().listPrompts(cursor);
    }

    public McpSchema.ListPromptsResult listPrompts() {
        return getMcpSyncClient().listPrompts();
    }

    public McpSchema.GetPromptResult getPrompt(McpSchema.GetPromptRequest getPromptRequest) {
        return getMcpSyncClient().getPrompt(getPromptRequest);
    }

    public void setLoggingLevel(McpSchema.LoggingLevel loggingLevel) {
        for (McpSyncClient syncClient : getMcpSyncClientList()) {
            syncClient.setLoggingLevel(loggingLevel);
        }
    }

    public McpSchema.CompleteResult completeCompletion(McpSchema.CompleteRequest completeRequest) {
        return getMcpSyncClient().completeCompletion(completeRequest);
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

        public SseWebFluxDistributedSyncMcpClient build() {
            return new SseWebFluxDistributedSyncMcpClient(this.serverName, this.version,
                this.nacosMcpOperationService, this.applicationContext, this.lazyInit);
        }

    }
}
