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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.alibaba.cloud.ai.dashscope.image.DashScopeImageApiSpec.InvokeMode;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageApiSpec.RequestType;
import com.alibaba.cloud.ai.dashscope.spec.DashScopeModel.ImageModel;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * The constants for DashScope Image API.
 *
 * @author xuguan
 * @since 2026/6/17
 */
public class DashScopeImageApiConstants {

    // Sync endpoint
    public static final String MULTIMODAL_GENERATION_URL = "/api/v1/services/aigc/multimodal-generation/generation";

    public static final String VISION_IMAGE_PROCESS_URL = "/api/v1/services/vision/image-process/process";

    public static final String VISION_FACEDETECTION_URL = "/api/v1/services/vision/facedetection/detect";

    // Async endpoints
    public static final String IMAGE_GENERATION_URL = "/api/v1/services/aigc/image-generation/generation";

    public static final String TEXT2IMAGE_SYNTHESIS_URL = "/api/v1/services/aigc/text2image/image-synthesis";

    public static final String IMAGE2IMAGE_SYNTHESIS_URL = "/api/v1/services/aigc/image2image/image-synthesis";

    public static final String OUT_PAINTING_URL = "/api/v1/services/aigc/image2image/out-painting";

    public static final String VIRTUALMODEL_GENERATION_URL = "/api/v1/services/aigc/virtualmodel/generation";

    public static final String BACKGROUND_GENERATION_URL = "/api/v1/services/aigc/background-generation/generation";

    public static final String WORDART_SEMANTIC_URL = "/api/v1/services/aigc/wordart/semantic";

    public static final String WORDART_TEXTURE_URL = "/api/v1/services/aigc/wordart/texture";

    public static final String FINE_TUNES_URL = "/api/v1/fine-tunes";

    public static final String ALBUM_GEN_PORTRAIT_URL = "/api/v1/services/aigc/album/gen_portrait";

    // Task query endpoints
    public static final String TASK_QUERY_URL = "/api/v1/tasks/{task_id}";

    public static final String FINE_TUNES_TASK_QUERY_URL = "/api/v1/fine-tunes/{task_id}";

    // @formatter:off
    static final List<String> qwenFamilyModels = List.of(
            ImageModel.QWEN_IMAGE_2_0_PRO.getValue(), ImageModel.QWEN_IMAGE_2_0.getValue(),
            ImageModel.QWEN_IMAGE_MAX.getValue(), ImageModel.QWEN_IMAGE_PLUS.getValue(),
            ImageModel.QWEN_IMAGE.getValue(), ImageModel.QWEN_IMAGE_EDIT_MAX.getValue(),
            ImageModel.QWEN_IMAGE_EDIT_PLUS.getValue(), ImageModel.QWEN_IMAGE_EDIT.getValue(),
            ImageModel.QWEN_MT_IMAGE.getValue());

    static final List<String> generationModels = List.of(
            // Qwen Family
            ImageModel.QWEN_IMAGE_2_0_PRO.getValue(), ImageModel.QWEN_IMAGE_2_0.getValue(),
            ImageModel.QWEN_IMAGE_MAX.getValue(), ImageModel.QWEN_IMAGE_PLUS.getValue(),
            ImageModel.QWEN_IMAGE.getValue(), ImageModel.QWEN_IMAGE_EDIT_MAX.getValue(),
            ImageModel.QWEN_IMAGE_EDIT_PLUS.getValue(), ImageModel.QWEN_IMAGE_EDIT.getValue(),
            // Wan Family 2.6+
            ImageModel.WAN_2_6_T2I.getValue(), ImageModel.WAN_2_7_IMAGE_PRO.getValue(),
            ImageModel.WAN_2_7_IMAGE.getValue(), ImageModel.WAN_2_6_IMAGE.getValue(),
            // Z-Image Family
            ImageModel.Z_IMAGE_TURBO.getValue(),
            // KLing Family
            ImageModel.KLING_V3_IMAGE_GENERATION.getValue(), ImageModel.KLING_V3_OMNI_IMAGE_GENERATION.getValue());

    // Sync, model → multimodal-generation/generation
    static final Map<String, List<String>> multimodalGenerationModelMap = Map.of(MULTIMODAL_GENERATION_URL, List.of(
            // Qwen Family
            ImageModel.QWEN_IMAGE_2_0_PRO.getValue(), ImageModel.QWEN_IMAGE_2_0.getValue(),
            ImageModel.QWEN_IMAGE_MAX.getValue(), ImageModel.QWEN_IMAGE_PLUS.getValue(),
            ImageModel.QWEN_IMAGE.getValue(), ImageModel.QWEN_IMAGE_EDIT_MAX.getValue(),
            ImageModel.QWEN_IMAGE_EDIT_PLUS.getValue(), ImageModel.QWEN_IMAGE_EDIT.getValue(),
            // Wan Family 2.6+
            ImageModel.WAN_2_6_T2I.getValue(), ImageModel.WAN_2_7_IMAGE_PRO.getValue(),
            ImageModel.WAN_2_7_IMAGE.getValue(), ImageModel.WAN_2_6_IMAGE.getValue(),
            // Z-Image Family
            ImageModel.Z_IMAGE_TURBO.getValue()));

    // Sync, model → vision/image-process/process
    static final Map<String, List<String>> visionImageProcessModelMap = Map.of(VISION_IMAGE_PROCESS_URL,
            List.of(ImageModel.AITRYON_PARSING_V1.getValue()));

    // Sync → vision/facedetection/detect
    static final Map<String, List<String>> visionFacedetectionModelMap = Map.of(VISION_FACEDETECTION_URL,
            List.of(ImageModel.FACECHAIN_FACEDETECT.getValue()));

    // Async, model → image-generation/generation
    static final Map<String, List<String>> imageGenerationModelMap = Map.of(IMAGE_GENERATION_URL, List.of(
            // Wan 2.6+
            ImageModel.WAN_2_6_T2I.getValue(), ImageModel.WAN_2_7_IMAGE_PRO.getValue(),
            ImageModel.WAN_2_7_IMAGE.getValue(), ImageModel.WAN_2_6_IMAGE.getValue(),
            // KLing Family
            ImageModel.KLING_V3_IMAGE_GENERATION.getValue(), ImageModel.KLING_V3_OMNI_IMAGE_GENERATION.getValue(),
            // Style Repaint
            ImageModel.WANX_STYLE_REPAINT_V1.getValue()));

    // Async, model → text2image/image-synthesis
    static final Map<String, List<String>> imageSynthesisModelMap = Map.of(TEXT2IMAGE_SYNTHESIS_URL, List.of(
            // Qwen Family
            ImageModel.QWEN_IMAGE_2_0_PRO.getValue(), ImageModel.QWEN_IMAGE_2_0.getValue(),
            ImageModel.QWEN_IMAGE_MAX.getValue(), ImageModel.QWEN_IMAGE_PLUS.getValue(),
            ImageModel.QWEN_IMAGE.getValue(),
            // Wan 2.5-
            ImageModel.WAN_2_5_T2I_PREVIEW.getValue(), ImageModel.WAN_2_2_T2I_PLUS.getValue(),
            ImageModel.WAN_2_2_T2I_FLASH.getValue(), ImageModel.WANX_2_1_T2I_TURBO.getValue(),
            ImageModel.WANX_2_1_T2I_PLUS.getValue(), ImageModel.WANX_2_0_T2I_TURBO.getValue(),
            // Wanx v1
            ImageModel.WANX_V1.getValue(),
            // Poster generation
            ImageModel.WANX_POSTER_GENERATION_V1.getValue()));

    // Async, model → image2image/image-synthesis
    static final Map<String, List<String>> image2ImageSynthesisModelMap = Map.of(IMAGE2IMAGE_SYNTHESIS_URL, List.of(
            // Qwen-MT-Image
            ImageModel.QWEN_MT_IMAGE.getValue(),
            // Wan 2.5- i2i
            ImageModel.WAN_2_5_I2I_PREVIEW.getValue(), ImageModel.WANX_2_1_IMAGEEDIT.getValue(),
            // Wanx Sketch to image
            ImageModel.WANX_SKETCH_TO_IMAGE_LITE.getValue(),
            // Wanx-X-painting
            ImageModel.WANX_X_PAINTING.getValue(),
            // Instance Segmentation
            ImageModel.IMAGE_INSTANCE_SEGMENTATION.getValue(),
            // Image Erase Completion
            ImageModel.IMAGE_ERASE_COMPLETION.getValue(),
            // AI Try on
            ImageModel.AITRYON.getValue(), ImageModel.AITRYON_PLUS.getValue(), ImageModel.AITRYON_REFINER.getValue()));

    // Async, model → image2image/out-painting
    static final Map<String, List<String>> outPaintingModelMap = Map.of(OUT_PAINTING_URL,
            List.of(ImageModel.IMAGE_OUT_PAINTING.getValue()));

    // Async, model → virtualmodel/generation
    static final Map<String, List<String>> virtualmodelMap = Map.of(VIRTUALMODEL_GENERATION_URL,
            List.of(ImageModel.WANX_VIRTUALMODEL.getValue(), ImageModel.VIRTUALMODEL_V2.getValue(), ImageModel.SHOEMODEL_V1.getValue()));

    // Async, model → background-generation/generation
    static final Map<String, List<String>> backgroundGeneration2Model = Map.of(BACKGROUND_GENERATION_URL,
            List.of(ImageModel.WANX_BACKGROUND_GENERATION_V2.getValue()));

    // Async, model → fine-tunes
    static final Map<String, List<String>> finetunes2Model = Map.of(FINE_TUNES_URL,
            List.of(ImageModel.FACECHAIN_FINETUNE.getValue()));

    // Async, model → album/gen_portrait
    static final Map<String, List<String>> albumGenPortrait2Model = Map.of(ALBUM_GEN_PORTRAIT_URL,
            List.of(ImageModel.FACECHAIN_GENERATION.getValue()));

    // Async, model → wordart/semantic
    static final Map<String, List<String>> wordartSemantic2Model = Map.of(WORDART_SEMANTIC_URL,
            List.of(ImageModel.WORDART_SEMANTIC.getValue()));

    // Async, model → wordart/texture
    static final Map<String, List<String>> wordartTexture2Model = Map.of(WORDART_TEXTURE_URL,
            List.of(ImageModel.WORDART_TEXTURE.getValue()));
    // @formatter:on

    /**
     * Model name to sync URL Map.
     */
    static final Map<String, String> model2SyncUrl = new HashMap<>();
    /**
     * Model name to async URL Map.
     */
    static final Map<String, String> model2AsyncUrl = new HashMap<>();

    static {
        registerModelsFromSyncUrlMap(multimodalGenerationModelMap);
        registerModelsFromSyncUrlMap(visionImageProcessModelMap);
        registerModelsFromSyncUrlMap(visionFacedetectionModelMap);

        registerModelsFromAsyncUrlMap(imageGenerationModelMap);
        registerModelsFromAsyncUrlMap(imageSynthesisModelMap);
        registerModelsFromAsyncUrlMap(image2ImageSynthesisModelMap);
        registerModelsFromAsyncUrlMap(outPaintingModelMap);
        registerModelsFromAsyncUrlMap(virtualmodelMap);
        registerModelsFromAsyncUrlMap(backgroundGeneration2Model);
        registerModelsFromAsyncUrlMap(finetunes2Model);
        registerModelsFromAsyncUrlMap(albumGenPortrait2Model);
        registerModelsFromAsyncUrlMap(wordartSemantic2Model);
        registerModelsFromAsyncUrlMap(wordartTexture2Model);
    }

    private static void registerModelsFromSyncUrlMap(Map<String, List<String>> syncUrlToModelsMap) {
        model2SyncUrl.putAll(toSingleValueMap(syncUrlToModelsMap));
    }

    private static void registerModelsFromAsyncUrlMap(Map<String, List<String>> asyncUrlToModelsMap) {
        model2AsyncUrl.putAll(toSingleValueMap(asyncUrlToModelsMap));
    }

    private static Map<String, String> toSingleValueMap(Map<String, List<String>> listValueMap) {
        Map<String, String> singleValueMap = new HashMap<>();
        for (Entry<String, List<String>> listValueEntry : listValueMap.entrySet()) {
            for (String value : listValueEntry.getValue()) {
                String prev = singleValueMap.putIfAbsent(value, listValueEntry.getKey());
                Assert.isNull(prev, "Duplicate model mapping: " + value);
            }
        }
        return singleValueMap;
    }

    public static @Nullable String getImagePath(String model, InvokeMode mode) {
        return switch (mode) {
            case SYNC -> model2SyncUrl.get(model);
            case ASYNC -> model2AsyncUrl.get(model);
            case AUTO -> {
                // If model both support sync and async, use sync
                String path = model2SyncUrl.get(model);
                if (path == null) {
                    path = model2AsyncUrl.get(model);
                }
                yield path;
            }
        };
    }

    public static String getQueryTaskUrl(String model) {
        if (ImageModel.FACECHAIN_FINETUNE.getValue().equals(model)) {
            return FINE_TUNES_TASK_QUERY_URL;
        }
        return TASK_QUERY_URL;
    }

    public static boolean isGenerationRequestType(String model, InvokeMode mode, RequestType requestType) {
        return switch (requestType) {
            case STANDARD -> false;
            case GENERATION -> true;
            case AUTO -> {
                if (!generationModels.contains(model)) {
                    yield false;
                }
                if (qwenFamilyModels.contains(model)) {
                    yield !isAsync(model, mode);
                }
                yield true;
            }
        };
    }

    public static boolean isAsync(String model, InvokeMode mode) {
        return switch (mode) {
            case SYNC -> false;
            case ASYNC -> true;
            case AUTO -> !model2SyncUrl.containsKey(model) && model2AsyncUrl.containsKey(model);
        };
    }

}
