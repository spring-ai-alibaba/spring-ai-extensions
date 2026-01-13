# Spring AI Alibaba Python Tool Starter

This starter provides a Python Tool for Spring AI Alibaba agents that allows executing Python code using GraalVM polyglot.

## Features

- Execute Python code snippets in a sandboxed environment
- Automatic tool registration via Spring Boot auto-configuration
- Secure execution with restricted access (no file I/O, no process creation)
- Support for various Python data types (strings, numbers, booleans, arrays)

## Requirements

- Java 17+
- GraalVM polyglot dependencies (automatically included as optional dependencies)

## Usage

### 1. Add Dependency

Add the starter to your `pom.xml`:

```xml
<dependency>
    <groupId>com.alibaba.cloud.ai</groupId>
    <artifactId>spring-ai-alibaba-starter-python-tool</artifactId>
    <version>${spring-ai-alibaba.version}</version>
</dependency>
```

### 2. Enable Python Tool (Optional)

The Python Tool is enabled by default. To disable it, add the following configuration:

```yaml
  spring:
      ai:
          alibaba:
              tool-calling:
                  python:
                      enabled: true
                      engine:
                          warn-interpreter-only: false
                      context:
                          allow-all-access: false
                          allow-io: false
                          allow-native-access: false
                          allow-create-process: false
                          allow-host-access: true
```

### 3. Use in Agent

The Python Tool will be automatically registered as a `ToolCallback` and available to your agents. The tool can be used to execute Python code:

**Example Python code executions:**
- Simple calculation: `2 + 2` returns `"4"`
- String operations: `'Hello, ' + 'World'` returns `"Hello, World"`
- List operations: `[1, 2, 3][0]` returns `"1"`

## Security

The Python Tool runs in a sandboxed environment with the following restrictions:
- File I/O is disabled
- Native access is disabled
- Process creation is disabled
- All access is restricted by default

## Customization

You can provide your own `PythonService` bean to customize the behavior:

```java

@Bean(name = PythonConstants.TOOL_NAME)
@ConditionalOnMissingBean
public PythonService pythonService(PythonProperties properties) {
    return new PythonService(properties);
}
```

Or provide a custom `ToolCallback`:

```java
@Bean
@ConditionalOnMissingBean(name = "pythonToolCallback")
public ToolCallback pythonToolCallback() {
    return PythonService.createPythonToolCallback(PythonConstants.DESCRIPTION);
}
```

## License

Copyright 2024-2026 the original author or authors.

Licensed under the Apache License, Version 2.0.

