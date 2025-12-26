# Spring AI Alibaba AgentBay Tool Starter

这是一个Spring AI工具集成，提供了在阿里云AgentBay安全沙箱中执行命令和代码的能力。

## 功能

该Starter提供了7个主要工具：

1. **create_sandbox** - 创建沙箱环境并返回 sandbox_id
2. **kill_sandbox** - 销毁沙箱并释放资源
3. **shell** - 在沙箱中执行Shell命令
4. **get_link** - 获取沙箱端口的公网访问链接
5. **read_file** - 读取沙箱中的文件内容
6. **write_file** - 写入内容到沙箱文件
7. **list_files** - 列出沙箱目录中的文件和子目录

## 快速开始

### 1. 添加依赖

```xml
<dependency>
    <groupId>com.alibaba.cloud.ai</groupId>
    <artifactId>spring-ai-alibaba-starter-tool-calling-agentbay</artifactId>
    <version>${spring-ai-alibaba.version}</version>
</dependency>
```

### 2. 配置

在 `application.properties` 或 `application.yml` 中配置：

```properties
# AgentBay API Key (也可以通过环境变量 AGENTBAY_API_KEY 设置)
spring.ai.alibaba.agentbay.tool.api-key=your-api-key

# 可选配置
spring.ai.alibaba.agentbay.tool.enabled=true
spring.ai.alibaba.agentbay.tool.default-image-id=code_latest
spring.ai.alibaba.agentbay.tool.timeout-ms=300000
```

### 3. 使用示例

```java
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AgentService {

    @Autowired
    private ChatClient chatClient;

    public String executeCommand(String userPrompt) {
        return chatClient.prompt()
            .user(userPrompt)
            .call()
            .content();
    }
}
```

## 工具详解

### 1. create_sandbox

创建一个新的沙箱环境。

**参数：**
- `imageId` (可选): 运行时镜像ID，如 'code_latest', 'browser_latest', 'linux_latest'

**返回：**
- `sessionId`: 沙箱唯一标识符
- `success`: 是否成功
- `message`: 附加信息或错误消息

### 2. kill_sandbox

销毁沙箱并释放资源。

**参数：**
- `sessionId` (必需): 要销毁的沙箱ID

**返回：**
- `success`: 是否成功
- `message`: 附加信息或错误消息

### 3. shell

在沙箱中执行Shell命令。

**参数：**
- `command` (必需): 要执行的Shell命令
- `sessionId` (必需): 沙箱ID（必须先使用 create_sandbox 创建）
- `autoCleanup` (可选): 执行后是否自动删除会话，默认为 false

**返回：**
- `output`: 命令输出
- `exitCode`: 命令退出码
- `success`: 是否成功执行
- `sessionId`: 使用的沙箱ID
- `message`: 附加信息或错误消息

### 4. get_link

获取沙箱端口的公网HTTP链接。

**参数：**
- `sessionId` (必需): 沙箱ID
- `port` (必需): 端口号，范围 30100-30199

**返回：**
- `url`: 公网访问链接
- `success`: 是否成功
- `message`: 附加信息或错误消息

### 5. read_file

读取沙箱中的文件内容。

**参数：**
- `sessionId` (必需): 沙箱ID
- `path` (必需): 文件路径

**返回：**
- `content`: 文件内容
- `success`: 是否成功
- `message`: 附加信息或错误消息

### 6. write_file

写入内容到沙箱文件。

**参数：**
- `sessionId` (必需): 沙箱ID
- `path` (必需): 文件路径
- `content` (必需): 文件内容

**返回：**
- `success`: 是否成功
- `message`: 附加信息或错误消息

### 7. list_files

列出沙箱目录中的文件和子目录。

**参数：**
- `sessionId` (必需): 沙箱ID
- `path` (可选): 目录路径，默认为当前目录

**返回：**
- `listing`: 目录列表
- `success`: 是否成功
- `message`: 附加信息或错误消息

## 安全特性

- ✅ 完全隔离的云端沙箱环境
- ✅ 无法访问宿主系统
- ✅ 自动资源清理
- ✅ 安全的代码执行环境

## 使用场景

1. **安全代码执行** - 在隔离环境中运行用户提供的代码
2. **系统命令执行** - 安全地执行系统命令
3. **临时开发环境** - 快速创建临时的开发测试环境
4. **自动化任务** - 执行各类自动化脚本和任务

## 注意事项

1. 请妥善保管您的 AgentBay API Key
2. 建议在使用完会话后及时删除以节省资源

## 获取 API Key

访问 [AgentBay 控制台](https://agentbay.console.aliyun.com/service-management) 创建您的 API Key。

## 参考链接

- [AgentBay 官方文档](https://agentbay.console.aliyun.com/)

