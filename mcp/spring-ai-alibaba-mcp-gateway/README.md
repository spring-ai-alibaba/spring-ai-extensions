# Spring AI Alibaba MCP Gateway

基于 Spring AI 的 MCP (Model Context Protocol) Gateway 实现，提供统一的 MCP 服务网关功能，支持多种协议和认证方式。

## 功能特性

- **多协议支持**: 支持 HTTP/HTTPS、MCP-SSE、MCP-Streamable 协议
- **Nacos 服务发现**: 与 Nacos 注册中心集成，自动发现 MCP 服务
- **OAuth 认证**: 支持 OAuth 2.0 客户端凭证模式认证
- **动态配置**: 支持 Nacos 配置中心动态更新
- **工具回调**: 提供 Spring AI ToolCallback 实现

## MCP SDK 版本兼容性

### 支持版本

| 组件 | 版本 |
|------|------|
| MCP Java SDK | 0.14.x |
| 最低版本 | 0.14.0 |
| Spring AI | 兼容 |

### 版本升级说明

从旧版本升级到 MCP SDK 0.14.0：

#### 1. 更新父 POM 版本

```xml
<properties>
    <mcp.version>0.14.0</mcp.version>
</properties>
```

#### 2. 移除模块级版本覆盖

如果模块 pom.xml 中存在本地版本覆盖，请移除：

```xml
<!-- 移除此类配置 -->
<properties>
    <mcp-spring.version>0.14.0</mcp-spring.version>
</properties>
```

#### 3. 使用 BOM 管理版本

确保父 POM 导入了 MCP BOM：

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>io.modelcontextprotocol.sdk</groupId>
            <artifactId>mcp-bom</artifactId>
            <version>${mcp.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

### API 兼容性

MCP SDK 0.14.0 保持向后兼容，以下 API 无需修改：

| API | 状态 |
|-----|------|
| `McpClient.sync(transport).build()` | ✅ 兼容 |
| `HttpClientSseClientTransport.builder()` | ✅ 兼容 |
| `WebClientStreamableHttpTransport.builder()` | ✅ 兼容 |
| `CallToolResult.content()` | ✅ 兼容 |
| `TextContent.text()` | ✅ 兼容 |
| `McpSchema.CallToolRequest` | ✅ 兼容 |
| `McpSchema.InitializeResult` | ✅ 兼容 |

### 已知限制

1. **连接池**: 当前版本未实现连接池，每次调用创建新连接
2. **性能考虑**: 高频调用场景建议考虑连接复用策略

## 快速开始

### 1. 添加依赖

```xml
<dependency>
    <groupId>com.alibaba.cloud.ai</groupId>
    <artifactId>spring-ai-alibaba-mcp-gateway</artifactId>
    <version>${revision}</version>
</dependency>
```

### 2. 配置属性

```yaml
spring:
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848
        username: nacos
        password: nacos
```

## OAuth 配置

MCP Gateway 支持通过 OAuth 2.0 为外部服务调用提供透明的身份验证。

### 启用 OAuth 验证

```yaml
spring:
  ai:
    alibaba:
      mcp:
        gateway:
          oauth:
            enabled: true
            provider:
              client-id: your-client-id
              client-secret: your-client-secret
              token-uri: https://your-oauth-server.com/oauth/token
              grant-type: client_credentials
              scope: read,write
            token-cache:
              enabled: true
              max-size: 1000
              refresh-before-expiry: PT5M
            retry:
              max-attempts: 3
              backoff: PT1S
```

### 配置参数说明

| 参数 | 说明 |
|------|------|
| `enabled` | 是否启用 OAuth 认证 |
| `provider.client-id` | OAuth 客户端 ID |
| `provider.client-secret` | OAuth 客户端密钥 |
| `provider.token-uri` | 获取访问令牌的端点 URL |
| `provider.grant-type` | OAuth 授权类型（默认 `client_credentials`） |
| `provider.scope` | 请求的权限范围 |
| `token-cache.enabled` | 是否启用 Token 缓存 |
| `token-cache.max-size` | 缓存最大大小 |
| `token-cache.refresh-before-expiry` | Token 过期前刷新时间 |
| `retry.max-attempts` | 最大重试次数 |
| `retry.backoff` | 重试间隔 |

### 当前支持的授权类型

- `client_credentials`: 客户端凭证模式（推荐用于服务间通信）

## 核心组件

### NacosMcpGatewayToolCallback

实现 Spring AI `ToolCallback` 接口，提供：

- 自动协议检测（HTTP/HTTPS、SSE、Streamable）
- OAuth 透明认证
- 请求模板渲染
- 响应解析

### 协议处理

| 协议 | 传输实现 |
|------|----------|
| HTTP/HTTPS | WebClient |
| MCP-SSE | HttpClientSseClientTransport |
| MCP-Streamable | WebClientStreamableHttpTransport |

## 模块依赖

```
spring-ai-alibaba-mcp-gateway
├── spring-ai-alibaba-mcp-common
├── nacos-client (Nacos3)
├── spring-webflux
└── reactor-netty-http
```

## 许可证

Apache License 2.0
