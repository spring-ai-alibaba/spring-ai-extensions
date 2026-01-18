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
    private String imagesPath = DashScopeApiConstants.TEXT2IMAGE_RESTFUL_URL;

    /**
     * DashScope ai images query task result restful url path.
     */
    private String queryTaskPath = DashScopeApiConstants.QUERY_TASK_RESTFUL_URL;

    @NestedConfigurationProperty
    private DashScopeImageOptions options = DashScopeImageOptions.builder()
            .model(DashScopeModel.ImageModel.WANX_V1.getValue())
            .n(1)
            .build();

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getImagesPath() {
        return this.imagesPath;
    }

    public void setImagesPath(String imagesPath) {
        this.imagesPath = imagesPath;
    }

    public String getQueryTaskPath() {
        return this.queryTaskPath;
    }

    public void setQueryTaskPath(String queryTaskPath) {
        this.queryTaskPath = queryTaskPath;
    }

    public DashScopeImageOptions getOptions() {
        return this.options;
    }

    public void setOptions(DashScopeImageOptions options) {
        this.options = options;
    }
}
