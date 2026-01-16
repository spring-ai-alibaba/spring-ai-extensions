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
package com.alibaba.cloud.ai.dashscope.video;

import com.alibaba.cloud.ai.dashscope.api.DashScopeVideoApi;
import com.alibaba.cloud.ai.dashscope.spec.DashScopeApiSpec;
import com.alibaba.cloud.ai.dashscope.spec.DashScopeModel;
import com.alibaba.cloud.ai.dashscope.video.DashScopeVideoModel.Builder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.retry.RetryUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * DashScope Video Model Test.
 *
 * @author dashscope
 */
class DashScopeVideoModelTest {

    // Test constants
    private static final String TEST_MODEL = DashScopeModel.VideoModel.WANX2_1_T2V_TURBO.getValue();

    private static final String TEST_PROMPT = "A little smile dog.";

    private static final String TEST_IMG_URL = "https://help-static-aliyun-doc.aliyuncs.com/file-manage-files/zh-CN/20250925/wpimhv/rap.png";

    private static final String  TEST_FIRST_FRAME_URL = "data:image/png;base64,GDU7MtCZzEbTbmRZ......";

    private static final String  TEST_LAST_FRAME_URL = "data:image/png;base64,VBORw0KGgoAAAANSUh......";

    private DashScopeVideoApi videoApi;

    private DashScopeVideoModel videoModel;

    private DashScopeVideoOptions defaultOptions;

    @BeforeEach
    void setUp() {
        videoApi = mock(DashScopeVideoApi.class);
        defaultOptions = DashScopeVideoOptions.builder().model(TEST_MODEL).build();
        videoModel = new DashScopeVideoModel(videoApi, defaultOptions, RetryUtils.DEFAULT_RETRY_TEMPLATE);
    }

    @Test
    void testDashScopeVideoOptionsBuilder() {
        DashScopeVideoOptions options = DashScopeVideoOptions.builder()
                .model(TEST_MODEL)
                .prompt(TEST_PROMPT)
                .imageUrl(TEST_IMG_URL)
                .firstFrameUrl(TEST_FIRST_FRAME_URL)
                .lastFrameUrl(TEST_LAST_FRAME_URL)
                .resolution("720P")
                .size("640x480")
                .duration(10)
                .promptExtend(false)
                .seed(123456789L)
                .negativePrompt("negative prompt")
                .template(DashScopeApiSpec.VideoTemplate.SINGLEHEART)
                .build();

        assertThat(options.getModel()).isNotNull();
        assertThat(options.getModel()).isEqualTo(TEST_MODEL);
        assertThat(options.getPrompt()).isEqualTo(TEST_PROMPT);
        assertThat(options.getImageUrl()).isEqualTo(TEST_IMG_URL);
        assertThat(options.getFirstFrameUrl()).isEqualTo(TEST_FIRST_FRAME_URL);
        assertThat(options.getLastFrameUrl()).isEqualTo(TEST_LAST_FRAME_URL);
        assertThat(options.getResolution()).isEqualTo("720P");
        assertThat(options.getSize()).isEqualTo("640x480");
        assertThat(options.getDuration()).isEqualTo(10);
        assertThat(options.getPromptExtend()).isFalse();
        assertThat(options.getSeed()).isEqualTo(123456789L);
        assertThat(options.getNegativePrompt()).isEqualTo("negative prompt");
        assertThat(options.getTemplate()).isEqualTo(DashScopeApiSpec.VideoTemplate.SINGLEHEART);
    }

    @Test
    void testBuilder() {
        DashScopeVideoModel model1 = DashScopeVideoModel.builder()
                .videoApi(videoApi)
                .build();

        DashScopeVideoModel model2 = DashScopeVideoModel.builder()
                .videoApi(videoApi)
                .defaultOptions(defaultOptions)
                .retryTemplate(RetryUtils.DEFAULT_RETRY_TEMPLATE)
                .build();

        DashScopeVideoModel clone1 = model1.clone();
        DashScopeVideoModel clone2 = model2.clone();

        Builder mutate1 = model1.mutate();
        Builder mutate2 = model2.mutate();

        assertThat(model1).isNotNull();
        assertThat(model2).isNotNull();
        assertThat(clone1).isNotNull();
        assertThat(clone2).isNotNull();
        assertThat(mutate1).isNotNull();
        assertThat(mutate2).isNotNull();
    }

}
