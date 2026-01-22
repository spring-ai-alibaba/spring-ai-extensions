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
import com.alibaba.cloud.ai.dashscope.common.DashScopeVideoApiConstants;
import com.alibaba.cloud.ai.dashscope.video.DashScopeVideoOptions;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * DashScope Video Generation Properties.
 *
 * @author dashscope
 * @author yuluo
 * @since 1.0.0.3
 */

@ConfigurationProperties(prefix = DashScopeVideoProperties.CONFIG_PREFIX)
public class DashScopeVideoProperties extends DashScopeParentProperties {

	public static final String CONFIG_PREFIX = "spring.ai.dashscope.video";

    private String videoPath = DashScopeVideoApiConstants.VIDEO_GENERATION_SYNTHESIS;

    private String queryTaskPath = DashScopeApiConstants.QUERY_TASK_RESTFUL_URL;

	@NestedConfigurationProperty
    private DashScopeVideoOptions options = DashScopeVideoOptions.builder().build();

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public String getQueryTaskPath() {
        return queryTaskPath;
    }

    public void setQueryTaskPath(String queryTaskPath) {
        this.queryTaskPath = queryTaskPath;
    }

    public DashScopeVideoOptions getOptions() {
		return this.options;
	}

	public void setOptions(DashScopeVideoOptions options) {
		this.options = options;
	}

}
