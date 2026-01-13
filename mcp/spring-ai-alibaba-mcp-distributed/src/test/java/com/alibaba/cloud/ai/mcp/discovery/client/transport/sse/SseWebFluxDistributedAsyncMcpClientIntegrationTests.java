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

import com.alibaba.cloud.ai.mcp.nacos.service.NacosMcpOperationService;
import com.alibaba.cloud.ai.mcp.nacos.service.NacosMcpSubscriber;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.ai.constant.AiConstants;
import com.alibaba.nacos.api.ai.model.mcp.McpEndpointInfo;
import com.alibaba.nacos.api.ai.model.mcp.McpEndpointSpec;
import com.alibaba.nacos.api.ai.model.mcp.McpServerBasicInfo;
import com.alibaba.nacos.api.ai.model.mcp.McpServerDetailInfo;
import com.alibaba.nacos.api.ai.model.mcp.McpServerRemoteServiceConfig;
import com.alibaba.nacos.api.ai.model.mcp.McpServiceRef;
import com.alibaba.nacos.api.ai.model.mcp.McpToolSpecification;
import com.alibaba.nacos.api.ai.model.mcp.registry.ServerVersionDetail;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.ai.mcp.client.common.autoconfigure.configurer.McpAsyncClientConfigurer;
import org.springframework.ai.mcp.client.common.autoconfigure.properties.McpClientCommonProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The current integration test requires dependency on Nacos server 3.1.
 * If you have a running Nacos Server locally, you can remove @Disabled and run it.
 * TODO: Set up the Nacos server for integration testing in GitHub Actions.
 */
@Disabled
class SseWebFluxDistributedAsyncMcpClientIntegrationTests {

    private static final String NACOS_HOST = "localhost";

    private static final int NACOS_PORT = 8848;

    private static final String NACOS_USERNAME = "nacos";

    private static final String NACOS_PASSWORD = "nacos";

    private static final String DEFAULT_NAMESPACE = "public";

    private static final String DEFAULT_GROUP = "DEFAULT_GROUP";

    private final ApplicationContextRunner springContext = new ApplicationContextRunner()
            .withUserConfiguration(TestClientConfiguration.class);

    private final Map<String, NacosMcpOperationService> operationServiceCache = new HashMap<>();

    @Test
    void lazyInit_shouldStartWhenServiceNeverRegistered() {
        assertThat(isNacosReachable()).isTrue().as("Nacos Server is required for this test");

        TestMcpServerSpec spec = TestMcpServerSpec.create("svc-a");
        boolean lazyInit = true;

        springContext.run(context -> {
            SseWebFluxDistributedAsyncMcpClient client = createClient(context, spec, lazyInit);
            assertThat(client.getMcpAsyncClientList()).isEmpty();
            assertThat(client.getNacosMcpServerEndpoint()).isNull();
        });
    }

    @Test
    void lazyInit_shouldStartWhenServiceRegisteredWithoutInstances() throws Exception {
        assertThat(isNacosReachable()).isTrue().as("Nacos Server is required for this test");

        TestMcpServerSpec spec = TestMcpServerSpec.create("svc-b");
        NacosMcpOperationService operationService = createOperationService();
        createMcpServer(operationService, spec);
        Instance instance = registerInstance(operationService, spec);
        waitAndAssertEndpointsSize(operationService, spec, 1);
        deregisterInstance(operationService, instance, spec);
        waitAndAssertEndpointsSize(operationService, spec, 0);

        boolean lazyInit = true;
        springContext.run(context -> {
            SseWebFluxDistributedAsyncMcpClient client = createClient(context, spec, lazyInit);
            assertThat(client.getMcpAsyncClientList()).isEmpty();
            assertThat(client.getNacosMcpServerEndpoint()).isNotNull();
        });
    }

    @Test
    void lazyInit_shouldDetectInstanceAfterRegistration() {
        assertThat(isNacosReachable()).isTrue().as("Nacos Server is required for this test");

        TestMcpServerSpec spec = TestMcpServerSpec.create("svc-c");
        boolean lazyInit = true;

        springContext.run(context -> {
            SseWebFluxDistributedAsyncMcpClient client = createClient(context, spec, lazyInit);
            assertThat(client.getMcpAsyncClientList()).isEmpty();

            NacosMcpOperationService operationService = getOperationService(spec.configName());
            createMcpServer(operationService, spec);
            Instance instance = registerInstance(operationService, spec);

            waitAndAssertEndpointsSize(operationService, spec, 1);
            forceTriggerSubscribe(operationService, spec);
            waitAndAssertClientSize(client, 1);

            deregisterInstance(operationService, instance, spec);
        });
    }

    @Test
    void lazyInit_shouldHandleInstanceRemoval() {
        assertThat(isNacosReachable()).isTrue().as("Nacos Server is required for this test");

        TestMcpServerSpec spec = TestMcpServerSpec.create("svc-d");
        boolean lazyInit = true;

        springContext.run(context -> {
            SseWebFluxDistributedAsyncMcpClient client = createClient(context, spec, lazyInit);
            NacosMcpOperationService operationService = getOperationService(spec.configName());

            createMcpServer(operationService, spec);
            Instance instance = registerInstance(operationService, spec);
            waitAndAssertEndpointsSize(operationService, spec, 1);
            forceTriggerSubscribe(operationService, spec);
            waitAndAssertClientSize(client, 1);

            deregisterInstance(operationService, instance, spec);
            waitAndAssertEndpointsSize(operationService, spec, 0);
            forceTriggerSubscribe(operationService, spec);
            waitAndAssertClientSize(client, 0);
        });
    }

    @Test
    void nonLazy_shouldFailWhenServiceMissing() {
        assertThat(isNacosReachable()).isTrue().as("Nacos Server is required for this test");

        TestMcpServerSpec spec = TestMcpServerSpec.create("svc-e-new");
        boolean lazyInit = false;

        springContext.run(context -> {
            try {
                createClient(context, spec, lazyInit);
                throw new AssertionError("Should have thrown exception");
            } catch (RuntimeException e) {
                assertThat(e).hasMessageContaining("Failed to get endpoints");
            }
        });
    }

    @Test
    void nonLazy_shouldStartWhenServiceRegisteredButNoInstances() throws Exception {
        assertThat(isNacosReachable()).isTrue().as("Nacos Server is required for this test");

        TestMcpServerSpec spec = TestMcpServerSpec.create("svc-e-known");
        NacosMcpOperationService operationService = createOperationService();
        createMcpServer(operationService, spec);
        boolean lazyInit = false;

        springContext.run(context -> {
            SseWebFluxDistributedAsyncMcpClient client = createClient(context, spec, lazyInit);
            assertThat(client).isNotNull();
            assertThat(client.getMcpAsyncClientList()).isEmpty();
        });
    }

    private SseWebFluxDistributedAsyncMcpClient createClient(ConfigurableApplicationContext context,
            TestMcpServerSpec spec, boolean lazyInit) {
        NacosMcpOperationService operationService = getOperationService(spec.configName());
        SseWebFluxDistributedAsyncMcpClient client = SseWebFluxDistributedAsyncMcpClient.builder()
                .serverName(spec.serverName())
                .version(spec.version())
                .nacosMcpOperationService(operationService)
                .applicationContext(context)
                .lazyInit(lazyInit)
                .build();
        client.init();
        client.subscribe();
        return client;
    }

    private NacosMcpOperationService getOperationService(String configName) {
        return operationServiceCache.computeIfAbsent(configName, key -> {
            try {
                Properties properties = new Properties();
                properties.put(PropertyKeyConst.SERVER_ADDR, NACOS_HOST + ":" + NACOS_PORT);
                properties.put(PropertyKeyConst.NAMESPACE, DEFAULT_NAMESPACE);
                properties.put(PropertyKeyConst.USERNAME, NACOS_USERNAME);
                properties.put(PropertyKeyConst.PASSWORD, NACOS_PASSWORD);
                return new NacosMcpOperationService(properties);
            } catch (NacosException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private boolean isNacosReachable() {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(NACOS_HOST, NACOS_PORT), 500);
            return true;
        } catch (IOException ignored) {
            return false;
        }
    }

    private NacosMcpOperationService createOperationService() throws NacosException {
        Properties properties = new Properties();
        properties.put(PropertyKeyConst.SERVER_ADDR, NACOS_HOST + ":" + NACOS_PORT);
        properties.put(PropertyKeyConst.NAMESPACE, DEFAULT_NAMESPACE);
        properties.put(PropertyKeyConst.USERNAME, NACOS_USERNAME);
        properties.put(PropertyKeyConst.PASSWORD, NACOS_PASSWORD);
        return new NacosMcpOperationService(properties);
    }

    private void createMcpServer(NacosMcpOperationService operationService, TestMcpServerSpec spec)
            throws NacosException {
        McpServerBasicInfo basicInfo = new McpServerBasicInfo();
        basicInfo.setName(spec.serverName());
        basicInfo.setDescription(spec.serverName() + " integration test");

        ServerVersionDetail versionDetail = new ServerVersionDetail();
        versionDetail.setVersion(spec.version());
        basicInfo.setVersionDetail(versionDetail);
        basicInfo.setProtocol(AiConstants.Mcp.MCP_PROTOCOL_SSE);
        basicInfo.setFrontProtocol(AiConstants.Mcp.MCP_PROTOCOL_SSE);

        McpServiceRef serviceRef = new McpServiceRef();
        serviceRef.setServiceName(spec.nacosServiceName());
        serviceRef.setGroupName(DEFAULT_GROUP);
        serviceRef.setNamespaceId(DEFAULT_NAMESPACE);

        McpServerRemoteServiceConfig remoteConfig = new McpServerRemoteServiceConfig();
        remoteConfig.setServiceRef(serviceRef);
        remoteConfig.setExportPath(spec.exportPath());
        basicInfo.setRemoteServerConfig(remoteConfig);

        McpEndpointSpec endpointSpec = new McpEndpointSpec();
        endpointSpec.setType(AiConstants.Mcp.MCP_ENDPOINT_TYPE_REF);
        Map<String, String> data = new HashMap<>();
        data.put("serviceName", spec.nacosServiceName());
        data.put("groupName", DEFAULT_GROUP);
        endpointSpec.setData(data);

        McpToolSpecification toolSpecification = new McpToolSpecification();
        toolSpecification.setTools(new ArrayList<>());

        operationService.createMcpServer(spec.serverName(), basicInfo, toolSpecification, endpointSpec);
    }

    private Instance registerInstance(NacosMcpOperationService op, TestMcpServerSpec spec) throws NacosException {
        Instance instance = new Instance();
        instance.setIp("127.0.0.1");
        instance.setPort(findAvailablePort());
        instance.setEphemeral(true);
        instance.setHealthy(true);
        instance.setWeight(1.0);
        op.registerService(spec.nacosServiceName(), DEFAULT_GROUP, instance);
        return instance;
    }

    private NamingService extractNamingService(NacosMcpOperationService op)
            throws NoSuchFieldException, IllegalAccessException {
        Field field = NacosMcpOperationService.class.getDeclaredField("namingService");
        field.setAccessible(true);
        return (NamingService) field.get(op);
    }

    private void deregisterInstance(NacosMcpOperationService op, Instance instance, TestMcpServerSpec spec) {
        try {
            NamingService namingService = extractNamingService(op);
            namingService.deregisterInstance(spec.nacosServiceName(), DEFAULT_GROUP, instance.getIp(),
                    instance.getPort());
        } catch (Exception ignored) {
        }
    }

    private void waitAndAssertEndpointsSize(NacosMcpOperationService op, TestMcpServerSpec spec, int expected)
            throws NacosException {
        long deadline = System.nanoTime() + Duration.ofSeconds(10).toNanos();
        while (System.nanoTime() < deadline) {
            McpServerDetailInfo detail = op.getServerDetail(spec.serverName(), spec.version());
            if (detail != null) {
                List<McpEndpointInfo> endpoints = detail.getBackendEndpoints();
                int size = endpoints == null ? 0 : endpoints.size();
                if (size == expected) {
                    return;
                }
            }
            sleepBriefly();
        }
        throw new AssertionError("Timed out waiting for " + expected + " endpoints for " + spec.serverName());
    }

    private void forceTriggerSubscribe(NacosMcpOperationService op, TestMcpServerSpec spec) throws Exception {
        Field field = NacosMcpOperationService.class.getDeclaredField("subscribers");
        field.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<String, List<NacosMcpSubscriber>> subscribers = (Map<String, List<NacosMcpSubscriber>>) field.get(op);
        if (subscribers == null) {
            return;
        }
        List<NacosMcpSubscriber> subscriberList = subscribers.get(spec.serverName() + "::" + spec.version());
        if (subscriberList == null || subscriberList.isEmpty()) {
            return;
        }
        McpServerDetailInfo detail = op.getServerDetail(spec.serverName(), spec.version());
        if (detail == null) {
            return;
        }
        subscriberList.forEach(subscriber -> subscriber.receive(detail));
    }

    private void waitAndAssertClientSize(SseWebFluxDistributedAsyncMcpClient client, int expectedClientSize) {
        long deadline = System.nanoTime() + Duration.ofSeconds(10).toNanos();
        while (System.nanoTime() < deadline) {
            int actualClientSize = client.getMcpAsyncClientList().size();
            if (actualClientSize == expectedClientSize) {
                return;
            }
            sleepBriefly();
        }
        throw new AssertionError("Client did not reach expected size within " + Duration.ofSeconds(10));
    }

    private void sleepBriefly() {
        try {
            Thread.sleep(200);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    private int findAvailablePort() {
        try (ServerSocket serverSocket = new ServerSocket(0)) {
            return serverSocket.getLocalPort();
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to allocate port", ex);
        }
    }

    private record TestMcpServerSpec(String configName, String serverName, String version, String nacosServiceName,
            String exportPath) {
        public static TestMcpServerSpec create(String prefix) {
            String id = prefix + "-" + UUID.randomUUID().toString().replace("-", "");
            return new TestMcpServerSpec("cfg-" + id, "mcp-" + id, "1.0.0", "svc-" + id, "/mcp-" + id);
        }
    }

    @Configuration(proxyBeanMethods = false)
    static class TestClientConfiguration {

        @Bean
        McpClientCommonProperties mcpClientCommonProperties() {
            McpClientCommonProperties properties = new McpClientCommonProperties();
            properties.setEnabled(true);
            properties.setName("integration-client");
            properties.setVersion("1.0.0");
            properties.setInitialized(false);
            return properties;
        }

        @Bean
        McpAsyncClientConfigurer mcpAsyncClientConfigurer() {
            return new McpAsyncClientConfigurer(List.of());
        }

        @Bean
        WebClient.Builder webClientBuilder() {
            return WebClient.builder();
        }

        @Bean
        ObjectMapper objectMapper() {
            return new ObjectMapper();
        }
    }
}
