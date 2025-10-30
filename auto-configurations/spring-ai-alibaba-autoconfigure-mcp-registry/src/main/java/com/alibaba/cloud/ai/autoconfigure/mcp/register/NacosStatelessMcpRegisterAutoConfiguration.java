package com.alibaba.cloud.ai.autoconfigure.mcp.register;


import com.alibaba.cloud.ai.mcp.nacos.NacosMcpProperties;
import com.alibaba.cloud.ai.mcp.nacos.service.NacosMcpOperationService;
import com.alibaba.cloud.ai.mcp.register.NacosMcpRegisterProperties;
import com.alibaba.cloud.ai.mcp.register.NacosStatelessMcpRegister;
import com.alibaba.nacos.api.ai.constant.AiConstants;
import com.alibaba.nacos.api.exception.NacosException;
import io.modelcontextprotocol.server.McpStatelessAsyncServer;
import io.modelcontextprotocol.server.McpStatelessSyncServer;
import org.springframework.ai.mcp.server.common.autoconfigure.McpServerAutoConfiguration;
import org.springframework.ai.mcp.server.common.autoconfigure.McpServerStatelessAutoConfiguration;
import org.springframework.ai.mcp.server.common.autoconfigure.McpServerStdioDisabledCondition;
import org.springframework.ai.mcp.server.common.autoconfigure.properties.McpServerProperties;
import org.springframework.ai.mcp.server.common.autoconfigure.properties.McpServerSseProperties;
import org.springframework.ai.mcp.server.common.autoconfigure.properties.McpServerStreamableHttpProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;

import java.lang.reflect.Field;
import java.util.Properties;


@EnableConfigurationProperties({ NacosMcpRegisterProperties.class, NacosMcpProperties.class,
        McpServerProperties.class, McpServerSseProperties.class, McpServerStreamableHttpProperties.class})
@AutoConfiguration(after = McpServerAutoConfiguration.class)
@ConditionalOnProperty(prefix = NacosMcpRegisterProperties.CONFIG_PREFIX, name = "enabled", havingValue = "true",
        matchIfMissing = false)
@Conditional({ McpServerStdioDisabledCondition.class,
        McpServerStatelessAutoConfiguration.EnabledStatelessServerCondition.class })
public class NacosStatelessMcpRegisterAutoConfiguration {
    
    @Bean
    @ConditionalOnMissingBean(NacosMcpOperationService.class)
    public NacosMcpOperationService nacosMcpOperationService(NacosMcpProperties nacosMcpProperties) {
        Properties nacosProperties = nacosMcpProperties.getNacosProperties();
        try {
            return new NacosMcpOperationService(nacosProperties);
        }
        catch (NacosException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Bean
    @ConditionalOnProperty(prefix = McpServerProperties.CONFIG_PREFIX, name = "type", havingValue = "SYNC",
            matchIfMissing = true)
    public NacosStatelessMcpRegister nacosStatelessMcpRegisterSync(NacosMcpOperationService nacosMcpOperationService,
            McpStatelessSyncServer mcpStatelessSyncServer, NacosMcpProperties nacosMcpProperties,
            NacosMcpRegisterProperties nacosMcpRegistryProperties, McpServerProperties mcpServerProperties, McpServerSseProperties mcpServerSseProperties,
            ApplicationContext applicationContext)
            throws NoSuchFieldException, IllegalAccessException {
        Field asyncServerField = McpStatelessSyncServer.class.getDeclaredField("asyncServer");
        asyncServerField.setAccessible(true);
        McpStatelessAsyncServer mcpStatelessAsyncServer = (McpStatelessAsyncServer) asyncServerField.get(mcpStatelessSyncServer);
        return getNacosMcpRegister(nacosMcpOperationService, mcpStatelessAsyncServer, nacosMcpProperties,
                nacosMcpRegistryProperties, mcpServerProperties, mcpServerSseProperties, applicationContext);
    }
    
    @Bean
    //	@ConditionalOnBean(McpAsyncServer.class)
    @ConditionalOnProperty(prefix = McpServerProperties.CONFIG_PREFIX, name = "type", havingValue = "ASYNC")
    public NacosStatelessMcpRegister nacosStatelessMcpRegisterAsync(NacosMcpOperationService nacosMcpOperationService,
            McpStatelessAsyncServer mcpStatelessAsyncServer, NacosMcpProperties nacosMcpProperties,
            NacosMcpRegisterProperties nacosMcpRegistryProperties, McpServerProperties mcpServerProperties, McpServerSseProperties mcpServerSseProperties,
            ApplicationContext applicationContext) {
        return getNacosMcpRegister(nacosMcpOperationService, mcpStatelessAsyncServer, nacosMcpProperties,
                nacosMcpRegistryProperties, mcpServerProperties, mcpServerSseProperties, applicationContext);
    }
    
    
    private NacosStatelessMcpRegister getNacosMcpRegister(NacosMcpOperationService nacosMcpOperationService,
            McpStatelessAsyncServer mcpStatelessAsyncServer, NacosMcpProperties nacosMcpProperties,
            NacosMcpRegisterProperties nacosMcpRegistryProperties, McpServerProperties mcpServerProperties, McpServerSseProperties mcpServerSseProperties,
            ApplicationContext applicationContext) {
        return new NacosStatelessMcpRegister(nacosMcpOperationService, mcpStatelessAsyncServer, nacosMcpProperties, nacosMcpRegistryProperties,
                mcpServerProperties, mcpServerSseProperties, applicationContext ,AiConstants.Mcp.MCP_PROTOCOL_STREAMABLE);
    }
    
}
