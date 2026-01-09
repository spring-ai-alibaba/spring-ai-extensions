package com.alibaba.cloud.ai.autoconfigure.dashscope;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(DashScopeConnectionProperties.CONFIG_PREFIX)
public class DashScopeAsyncToolCallingProperties {

    /**
     * Spring AI Alibaba configuration prefix.
     */
    public static final String CONFIG_PREFIX = "spring.ai.alibaba.tool.async";

    /**
     * Enable DashScope chat client async tool call.
     */
    private boolean enabled;

    /**
     * DashScope chat client async tool calling thread pool config.
     */
    private int corePoolSize = Runtime.getRuntime().availableProcessors() * 4;
    private int maximumPoolSize = Runtime.getRuntime().availableProcessors() * 8;
    private int keepAliveTime = 60;
    private int queueCapacity = 1000;

    public boolean isEnable() {
        return enabled;
    }

    public void setEnable(boolean enable) {
        this.enabled = enable;
    }

    public int getCorePoolSize() {
        return corePoolSize;
    }

    public void setCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    public int getMaximumPoolSize() {
        return maximumPoolSize;
    }

    public void setMaximumPoolSize(int maximumPoolSize) {
        this.maximumPoolSize = maximumPoolSize;
    }

    public int getKeepAliveTime() {
        return keepAliveTime;
    }

    public void setKeepAliveTime(int keepAliveTime) {
        this.keepAliveTime = keepAliveTime;
    }

    public int getQueueCapacity() {
        return queueCapacity;
    }

    public void setQueueCapacity(int queueCapacity) {
        this.queueCapacity = queueCapacity;
    }
}
