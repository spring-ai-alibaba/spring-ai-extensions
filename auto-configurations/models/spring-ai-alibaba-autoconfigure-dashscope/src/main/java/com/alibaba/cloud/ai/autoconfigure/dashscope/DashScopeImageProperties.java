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

package com.alibaba.cloud.ai.autoconfigure.dashscope;

import com.alibaba.cloud.ai.dashscope.common.DashScopeApiConstants;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageOptions;
import com.alibaba.cloud.ai.dashscope.spec.DashScopeModel;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.lang.Nullable;

/**
 * TongYi Image API properties.
 *
 * @author yuluo
 * @author <a href="mailto:yuluo08290126@gmail.com">yuluo</a>
 * @since 2023.0.1.0
 */
@ConfigurationProperties(DashScopeImageProperties.CONFIG_PREFIX)
public class DashScopeImageProperties extends DashScopeParentProperties {

    /**
     * Spring AI Alibaba configuration prefix.
     */
    public static final String CONFIG_PREFIX = "spring.ai.dashscope.image";

    /**
     * Enable DashScope ai images client.
     */
    private boolean enabled = true;

    /**
     * DashScope ai images restful url path.
     */
    private @Nullable String imagesPath;

    /**
     * DashScope ai images query task result restful url path.
     */
    private @Nullable String queryTaskPath;

    private long pollIntervalMs = DashScopeApiConstants.DEFAULT_POLL_INTERVAL_MS;

    private long pollTimeoutMs = DashScopeApiConstants.DEFAULT_POLL_TIMEOUT_MS;

    @NestedConfigurationProperty
    private DashScopeImageOptions options = DashScopeImageOptions.builder()
            .model(DashScopeModel.ImageModel.WAN_2_2_T2I_FLASH.getValue())
            .n(1)
            .build();

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public @Nullable String getImagesPath() {
        return this.imagesPath;
    }

    public void setImagesPath(@Nullable String imagesPath) {
        this.imagesPath = imagesPath;
    }

    public @Nullable String getQueryTaskPath() {
        return this.queryTaskPath;
    }

    public void setQueryTaskPath(@Nullable String queryTaskPath) {
        this.queryTaskPath = queryTaskPath;
    }

    public long getPollIntervalMs() {
        return this.pollIntervalMs;
    }

    public void setPollIntervalMs(long pollIntervalMs) {
        this.pollIntervalMs = pollIntervalMs;
    }

    public long getPollTimeoutMs() {
        return this.pollTimeoutMs;
    }

    public void setPollTimeoutMs(long pollTimeoutMs) {
        this.pollTimeoutMs = pollTimeoutMs;
    }

    public DashScopeImageOptions getOptions() {
        return this.options;
    }

    public void setOptions(DashScopeImageOptions options) {
        this.options = options;
    }
}
