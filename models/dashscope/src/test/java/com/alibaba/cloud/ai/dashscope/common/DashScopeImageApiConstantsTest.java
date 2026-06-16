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

package com.alibaba.cloud.ai.dashscope.common;

import com.alibaba.cloud.ai.dashscope.image.DashScopeImageApiSpec.InvokeMode;
import com.alibaba.cloud.ai.dashscope.spec.DashScopeModel.ImageModel;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link DashScopeImageApiConstants}.
 *
 * @author xuguan
 */
class DashScopeImageApiConstantsTests {

    @Test
    void syncModelsMapToMultimodalGenerationUrl() {
        assertThat(DashScopeImageApiConstants.getImagePath(ImageModel.QWEN_IMAGE_2_0_PRO.getValue(), InvokeMode.SYNC))
                .isEqualTo(DashScopeImageApiConstants.MULTIMODAL_GENERATION_URL);
        assertThat(DashScopeImageApiConstants.getImagePath(ImageModel.QWEN_IMAGE_EDIT_MAX.getValue(), InvokeMode.SYNC))
                .isEqualTo(DashScopeImageApiConstants.MULTIMODAL_GENERATION_URL);
        assertThat(DashScopeImageApiConstants.getImagePath(ImageModel.WAN_2_7_IMAGE_PRO.getValue(), InvokeMode.SYNC))
                .isEqualTo(DashScopeImageApiConstants.MULTIMODAL_GENERATION_URL);
        assertThat(DashScopeImageApiConstants.getImagePath(ImageModel.Z_IMAGE_TURBO.getValue(), InvokeMode.SYNC))
                .isEqualTo(DashScopeImageApiConstants.MULTIMODAL_GENERATION_URL);
    }

    @Test
    void syncModelsMapToVisionImageProcessUrl() {
        assertThat(DashScopeImageApiConstants.getImagePath(ImageModel.AITRYON_PARSING_V1.getValue(), InvokeMode.SYNC))
                .isEqualTo(DashScopeImageApiConstants.VISION_IMAGE_PROCESS_URL);
    }

    @Test
    void syncModelsMapToVisionFacedetectionUrl() {
        assertThat(DashScopeImageApiConstants.getImagePath(ImageModel.FACECHAIN_FACEDETECT.getValue(), InvokeMode.SYNC))
                .isEqualTo(DashScopeImageApiConstants.VISION_FACEDETECTION_URL);
    }

    @Test
    void asyncModelsMapToImageGenerationUrl() {
        assertThat(DashScopeImageApiConstants.getImagePath(ImageModel.KLING_V3_IMAGE_GENERATION.getValue(), InvokeMode.ASYNC))
                .isEqualTo(DashScopeImageApiConstants.IMAGE_GENERATION_URL);
        assertThat(DashScopeImageApiConstants.getImagePath(ImageModel.KLING_V3_OMNI_IMAGE_GENERATION.getValue(), InvokeMode.ASYNC))
                .isEqualTo(DashScopeImageApiConstants.IMAGE_GENERATION_URL);
        assertThat(DashScopeImageApiConstants.getImagePath(ImageModel.WANX_STYLE_REPAINT_V1.getValue(), InvokeMode.ASYNC))
                .isEqualTo(DashScopeImageApiConstants.IMAGE_GENERATION_URL);
    }

    @Test
    void asyncModelsMapToText2ImageSynthesisUrl() {
        assertThat(DashScopeImageApiConstants.getImagePath(ImageModel.WANX_V1.getValue(), InvokeMode.ASYNC))
                .isEqualTo(DashScopeImageApiConstants.TEXT2IMAGE_SYNTHESIS_URL);
        assertThat(DashScopeImageApiConstants.getImagePath(ImageModel.WANX_POSTER_GENERATION_V1.getValue(), InvokeMode.ASYNC))
                .isEqualTo(DashScopeImageApiConstants.TEXT2IMAGE_SYNTHESIS_URL);
        assertThat(DashScopeImageApiConstants.getImagePath(ImageModel.WAN_2_5_T2I_PREVIEW.getValue(), InvokeMode.ASYNC))
                .isEqualTo(DashScopeImageApiConstants.TEXT2IMAGE_SYNTHESIS_URL);
    }

    @Test
    void asyncModelsMapToImage2ImageSynthesisUrl() {
        assertThat(DashScopeImageApiConstants.getImagePath(ImageModel.AITRYON.getValue(), InvokeMode.ASYNC))
                .isEqualTo(DashScopeImageApiConstants.IMAGE2IMAGE_SYNTHESIS_URL);
        assertThat(DashScopeImageApiConstants.getImagePath(ImageModel.AITRYON_PLUS.getValue(), InvokeMode.ASYNC))
                .isEqualTo(DashScopeImageApiConstants.IMAGE2IMAGE_SYNTHESIS_URL);
        assertThat(DashScopeImageApiConstants.getImagePath(ImageModel.AITRYON_REFINER.getValue(), InvokeMode.ASYNC))
                .isEqualTo(DashScopeImageApiConstants.IMAGE2IMAGE_SYNTHESIS_URL);
        assertThat(DashScopeImageApiConstants.getImagePath(ImageModel.IMAGE_INSTANCE_SEGMENTATION.getValue(), InvokeMode.ASYNC))
                .isEqualTo(DashScopeImageApiConstants.IMAGE2IMAGE_SYNTHESIS_URL);
    }

    @Test
    void asyncModelsMapToOutPaintingUrl() {
        assertThat(DashScopeImageApiConstants.getImagePath(ImageModel.IMAGE_OUT_PAINTING.getValue(), InvokeMode.ASYNC))
                .isEqualTo(DashScopeImageApiConstants.OUT_PAINTING_URL);
    }

    @Test
    void asyncModelsMapToVirtualModelUrl() {
        assertThat(DashScopeImageApiConstants.getImagePath(ImageModel.WANX_VIRTUALMODEL.getValue(), InvokeMode.ASYNC))
                .isEqualTo(DashScopeImageApiConstants.VIRTUALMODEL_GENERATION_URL);
        assertThat(DashScopeImageApiConstants.getImagePath(ImageModel.VIRTUALMODEL_V2.getValue(), InvokeMode.ASYNC))
                .isEqualTo(DashScopeImageApiConstants.VIRTUALMODEL_GENERATION_URL);
        assertThat(DashScopeImageApiConstants.getImagePath(ImageModel.SHOEMODEL_V1.getValue(), InvokeMode.ASYNC))
                .isEqualTo(DashScopeImageApiConstants.VIRTUALMODEL_GENERATION_URL);
    }

    @Test
    void asyncModelsMapToBackgroundGenerationUrl() {
        assertThat(DashScopeImageApiConstants.getImagePath(ImageModel.WANX_BACKGROUND_GENERATION_V2.getValue(), InvokeMode.ASYNC))
                .isEqualTo(DashScopeImageApiConstants.BACKGROUND_GENERATION_URL);
    }

    @Test
    void asyncModelsMapToFineTunesUrl() {
        assertThat(DashScopeImageApiConstants.getImagePath(ImageModel.FACECHAIN_FINETUNE.getValue(), InvokeMode.ASYNC))
                .isEqualTo(DashScopeImageApiConstants.FINE_TUNES_URL);
    }

    @Test
    void asyncModelsMapToAlbumGenPortraitUrl() {
        assertThat(DashScopeImageApiConstants.getImagePath(ImageModel.FACECHAIN_GENERATION.getValue(), InvokeMode.ASYNC))
                .isEqualTo(DashScopeImageApiConstants.ALBUM_GEN_PORTRAIT_URL);
    }

    @Test
    void asyncModelsMapToWordartSemanticUrl() {
        assertThat(DashScopeImageApiConstants.getImagePath(ImageModel.WORDART_SEMANTIC.getValue(), InvokeMode.ASYNC))
                .isEqualTo(DashScopeImageApiConstants.WORDART_SEMANTIC_URL);
    }

    @Test
    void asyncModelsMapToWordartTextureUrl() {
        assertThat(DashScopeImageApiConstants.getImagePath(ImageModel.WORDART_TEXTURE.getValue(), InvokeMode.ASYNC))
                .isEqualTo(DashScopeImageApiConstants.WORDART_TEXTURE_URL);
    }

    @Test
    void autoModeResolvesSyncForSyncModels() {
        assertThat(DashScopeImageApiConstants.getImagePath(ImageModel.QWEN_IMAGE_2_0_PRO.getValue(), InvokeMode.AUTO))
                .isEqualTo(DashScopeImageApiConstants.MULTIMODAL_GENERATION_URL);
        assertThat(DashScopeImageApiConstants.getImagePath(ImageModel.AITRYON_PARSING_V1.getValue(), InvokeMode.AUTO))
                .isEqualTo(DashScopeImageApiConstants.VISION_IMAGE_PROCESS_URL);
    }

    @Test
    void autoModeResolvesAsyncForAsyncOnlyModels() {
        assertThat(DashScopeImageApiConstants.getImagePath(ImageModel.KLING_V3_IMAGE_GENERATION.getValue(), InvokeMode.AUTO))
                .isEqualTo(DashScopeImageApiConstants.IMAGE_GENERATION_URL);
        assertThat(DashScopeImageApiConstants.getImagePath(ImageModel.AITRYON.getValue(), InvokeMode.AUTO))
                .isEqualTo(DashScopeImageApiConstants.IMAGE2IMAGE_SYNTHESIS_URL);
    }

    @Test
    void dualRegisteredModelsHaveDifferentSyncAndAsyncUrls() {
        // Models registered in both maps should resolve to different URLs depending on mode
        assertThat(DashScopeImageApiConstants.getImagePath(ImageModel.WAN_2_6_T2I.getValue(), InvokeMode.SYNC))
                .isEqualTo(DashScopeImageApiConstants.MULTIMODAL_GENERATION_URL);
        assertThat(DashScopeImageApiConstants.getImagePath(ImageModel.WAN_2_6_T2I.getValue(), InvokeMode.ASYNC))
                .isEqualTo(DashScopeImageApiConstants.IMAGE_GENERATION_URL);
        assertThat(DashScopeImageApiConstants.getImagePath(ImageModel.QWEN_IMAGE_2_0_PRO.getValue(), InvokeMode.SYNC))
                .isEqualTo(DashScopeImageApiConstants.MULTIMODAL_GENERATION_URL);
        assertThat(DashScopeImageApiConstants.getImagePath(ImageModel.QWEN_IMAGE_2_0_PRO.getValue(), InvokeMode.ASYNC))
                .isEqualTo(DashScopeImageApiConstants.TEXT2IMAGE_SYNTHESIS_URL);
    }

    @Test
    void getImagePathReturnsNullForUnknownModel() {
        assertThat(DashScopeImageApiConstants.getImagePath("unknown-model", InvokeMode.SYNC)).isNull();
        assertThat(DashScopeImageApiConstants.getImagePath("unknown-model", InvokeMode.ASYNC)).isNull();
        assertThat(DashScopeImageApiConstants.getImagePath("unknown-model", InvokeMode.AUTO)).isNull();
    }

    @Test
    void getQueryTaskUrlReturnsDefaultForMostModels() {
        assertThat(DashScopeImageApiConstants.getQueryTaskUrl(ImageModel.QWEN_IMAGE_2_0_PRO.getValue()))
                .isEqualTo(DashScopeImageApiConstants.TASK_QUERY_URL);
        assertThat(DashScopeImageApiConstants.getQueryTaskUrl(ImageModel.KLING_V3_IMAGE_GENERATION.getValue()))
                .isEqualTo(DashScopeImageApiConstants.TASK_QUERY_URL);
        assertThat(DashScopeImageApiConstants.getQueryTaskUrl("any-model"))
                .isEqualTo(DashScopeImageApiConstants.TASK_QUERY_URL);
    }

    @Test
    void getQueryTaskUrlReturnsFineTunesUrlForFacechainFinetune() {
        assertThat(DashScopeImageApiConstants.getQueryTaskUrl(ImageModel.FACECHAIN_FINETUNE.getValue()))
                .isEqualTo(DashScopeImageApiConstants.FINE_TUNES_TASK_QUERY_URL);
    }

    @Test
    void urlConstantsHaveExpectedValues() {
        assertThat(DashScopeImageApiConstants.MULTIMODAL_GENERATION_URL)
                .isEqualTo("/api/v1/services/aigc/multimodal-generation/generation");
        assertThat(DashScopeImageApiConstants.VISION_IMAGE_PROCESS_URL)
                .isEqualTo("/api/v1/services/vision/image-process/process");
        assertThat(DashScopeImageApiConstants.VISION_FACEDETECTION_URL)
                .isEqualTo("/api/v1/services/vision/facedetection/detect");
        assertThat(DashScopeImageApiConstants.IMAGE_GENERATION_URL)
                .isEqualTo("/api/v1/services/aigc/image-generation/generation");
        assertThat(DashScopeImageApiConstants.TEXT2IMAGE_SYNTHESIS_URL)
                .isEqualTo("/api/v1/services/aigc/text2image/image-synthesis");
        assertThat(DashScopeImageApiConstants.IMAGE2IMAGE_SYNTHESIS_URL)
                .isEqualTo("/api/v1/services/aigc/image2image/image-synthesis");
        assertThat(DashScopeImageApiConstants.OUT_PAINTING_URL)
                .isEqualTo("/api/v1/services/aigc/image2image/out-painting");
        assertThat(DashScopeImageApiConstants.VIRTUALMODEL_GENERATION_URL)
                .isEqualTo("/api/v1/services/aigc/virtualmodel/generation");
        assertThat(DashScopeImageApiConstants.BACKGROUND_GENERATION_URL)
                .isEqualTo("/api/v1/services/aigc/background-generation/generation");
        assertThat(DashScopeImageApiConstants.WORDART_SEMANTIC_URL)
                .isEqualTo("/api/v1/services/aigc/wordart/semantic");
        assertThat(DashScopeImageApiConstants.WORDART_TEXTURE_URL)
                .isEqualTo("/api/v1/services/aigc/wordart/texture");
        assertThat(DashScopeImageApiConstants.FINE_TUNES_URL)
                .isEqualTo("/api/v1/fine-tunes");
        assertThat(DashScopeImageApiConstants.ALBUM_GEN_PORTRAIT_URL)
                .isEqualTo("/api/v1/services/aigc/album/gen_portrait");
        assertThat(DashScopeImageApiConstants.TASK_QUERY_URL)
                .isEqualTo("/api/v1/tasks/{task_id}");
        assertThat(DashScopeImageApiConstants.FINE_TUNES_TASK_QUERY_URL)
                .isEqualTo("/api/v1/fine-tunes/{task_id}");
    }

}
