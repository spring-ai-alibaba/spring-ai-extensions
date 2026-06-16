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
package com.alibaba.cloud.ai.dashscope.image;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

import com.alibaba.cloud.ai.dashscope.api.DashScopeImageApi;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageApiSpec.ImageRequest.ReferenceEdge;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageApiSpec.InvokeMode;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageApiSpec.RequestType;
import com.alibaba.cloud.ai.dashscope.spec.DashScopeModel.ImageModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;

/**
 * Integration tests for DashScope Image Model.
 * These tests make real API calls to DashScope image generation service.
 * Only runs when AI_DASHSCOPE_API_KEY environment variable is set.
 *
 * @author xuguan
 */
@Tag("integration")
@EnabledIfEnvironmentVariable(named = DashScopeImageModelIT.API_KEY_ENV, matches = ".+")
class DashScopeImageModelIT {

	static final String API_KEY_ENV = "AI_DASHSCOPE_API_KEY";

    private DashScopeImageModel imageModel;

	@BeforeEach
	void setUp() {
        String apiKey = System.getenv(API_KEY_ENV);
        DashScopeImageApi imageApi = DashScopeImageApi.builder().apiKey(apiKey).build();

		imageModel = DashScopeImageModel.builder()
				.dashScopeApi(imageApi)
                .build();
	}

	@Test
	void defaultQwenImageGeneration() {
		ImagePrompt prompt = new ImagePrompt("A light cream colored mini golden doodle puppy sitting on a grass field");
		ImageResponse response = imageModel.call(prompt);

		assertThat(response).isNotNull();
		assertThat(response.getResults()).hasSize(1);
		assertThat(response.getResult().getOutput().getUrl()).isNotEmpty();
		assertThat((String) response.getMetadata().get("taskStatus")).isEqualTo("SUCCEEDED");
	}

	@Test
	void qwenImageGenerationModelAutoModeTextToImage() {
        DashScopeImageOptions options = DashScopeImageOptions.builder()
                .model(ImageModel.QWEN_IMAGE_2_0_PRO.getValue())
                .n(1)
                .build();

        ImagePrompt prompt = new ImagePrompt("A beautiful sunset over mountains", options);
        ImageResponse response = imageModel.call(prompt);
        assertThat(response).isNotNull();
        assertThat(response.getResults()).hasSize(1);
        assertThat(response.getResult().getOutput().getUrl()).isNotEmpty();
        assertThat((String) response.getMetadata().get("taskStatus")).isEqualTo("SUCCEEDED");
	}

	@Test
	void qwenImageGenerationModelUseAsyncModeTextToImage() {
        DashScopeImageOptions options = DashScopeImageOptions.builder()
                .model(ImageModel.QWEN_IMAGE_PLUS.getValue())
                .invokeMode(InvokeMode.ASYNC)
                .n(1)
                .build();

        ImagePrompt prompt = new ImagePrompt("A beautiful sunset over mountains", options);
        ImageResponse response = imageModel.call(prompt);
        assertThat(response).isNotNull();
        assertThat(response.getResults()).hasSize(1);
        assertThat(response.getResult().getOutput().getUrl()).isNotEmpty();
        assertThat((String) response.getMetadata().get("taskStatus")).isEqualTo("SUCCEEDED");
	}

	@Test
	void qwenImageGenerationModelUseAsyncModeAndStandardRequestTypeTextToImage() {
        DashScopeImageOptions options = DashScopeImageOptions.builder()
                .model(ImageModel.QWEN_IMAGE_PLUS.getValue())
                .invokeMode(InvokeMode.ASYNC)
                .requestType(RequestType.STANDARD)
                .n(1)
                .build();

        ImagePrompt prompt = new ImagePrompt("A beautiful sunset over mountains", options);
        ImageResponse response = imageModel.call(prompt);
        assertThat(response).isNotNull();
        assertThat(response.getResults()).hasSize(1);
        assertThat(response.getResult().getOutput().getUrl()).isNotEmpty();
        assertThat((String) response.getMetadata().get("taskStatus")).isEqualTo("SUCCEEDED");
	}

    @Test
    void qwenImageGenerationModelUseCustomModelTextToImage() {
        DashScopeImageOptions options = DashScopeImageOptions.builder()
                .model(ImageModel.QWEN_IMAGE_PLUS.getValue())
                .invokeMode(InvokeMode.ASYNC)
                .requestType(RequestType.STANDARD)
                .n(1)
                .build();

        DashScopeImageApi api = DashScopeImageApi.builder()
                .apiKey(System.getenv(API_KEY_ENV))
                .baseUrl("https://dashscope.aliyuncs.com")
                .imagesPath("/api/v1/services/aigc/text2image/image-synthesis")
                .queryTaskPath("/api/v1/tasks/{task_id}")
                .build();

        DashScopeImageModel model = DashScopeImageModel.builder()
                .dashScopeApi(api)
                .build();

        ImagePrompt prompt = new ImagePrompt("A beautiful sunset over mountains", options);

        ImageResponse response = model.call(prompt);
        assertThat(response).isNotNull();
        assertThat(response.getResults()).hasSize(1);
        assertThat(response.getResult().getOutput().getUrl()).isNotEmpty();
        assertThat((String) response.getMetadata().get("taskStatus")).isEqualTo("SUCCEEDED");
    }

    @Test
    void qwenImageGenerationModelEditImage() {
        DashScopeImageOptions options = DashScopeImageOptions.builder()
                .model(ImageModel.QWEN_IMAGE_EDIT_MAX.getValue())
                .images(List.of("https://help-static-aliyun-doc.aliyuncs.com/file-manage-files/zh-CN/20260310/jiydyi/image+%2818%29-2026-03-10-16-39-59.webp"))
                .n(1)
                .build();

        ImagePrompt prompt = new ImagePrompt("Change flowers to red roses.", options);
        ImageResponse response = imageModel.call(prompt);
        assertThat(response).isNotNull();
        assertThat(response.getResults()).hasSize(1);
        assertThat(response.getResult().getOutput().getUrl()).isNotEmpty();
    }

    @Test
    void qwenMtImageModelTranslateImage() {
        DashScopeImageOptions options = DashScopeImageOptions.builder()
                .model(ImageModel.QWEN_MT_IMAGE.getValue())
                .baseImageUrl("https://help-static-aliyun-doc.aliyuncs.com/file-manage-files/zh-CN/20250916/ordhsk/1.webp")
                .sourceLang("zh")
                .targetLang("en")
                .ext(Map.of("config", Map.of("imageSegment", false)))
                .build();

        ImagePrompt prompt = new ImagePrompt("Translate image", options);
        ImageResponse response = imageModel.call(prompt);
        assertThat(response).isNotNull();
        assertThat(response.getResults()).hasSize(1);
        assertThat(response.getResult().getOutput().getUrl()).isNotEmpty();
        assertThat((String) response.getMetadata().get("taskStatus")).isEqualTo("SUCCEEDED");
    }

    @Test
    void wan2_6_T2IGenerationImageModelSyncModeTextToImage() {
        DashScopeImageOptions options = DashScopeImageOptions.builder()
                .model(ImageModel.WAN_2_6_T2I.getValue())
                .n(1)
                .build();

        ImagePrompt prompt = new ImagePrompt("A flower shop with delicate windows, beautiful wooden doors, flowers.", options);
        ImageResponse response = imageModel.call(prompt);
        assertThat(response).isNotNull();
        assertThat(response.getResults()).hasSize(1);
        assertThat(response.getResult().getOutput().getUrl()).isNotEmpty();
        assertThat((String) response.getMetadata().get("taskStatus")).isEqualTo("SUCCEEDED");
    }

    @Test
    void wan2_6_T2IGenerationImageModelAsyncModeTextToImage() {
        DashScopeImageOptions options = DashScopeImageOptions.builder()
                .model(ImageModel.WAN_2_6_T2I.getValue())
                .invokeMode(InvokeMode.ASYNC)
                .n(1)
                .build();

        ImagePrompt prompt = new ImagePrompt("A flower shop with delicate windows, beautiful wooden doors, flowers.", options);
        ImageResponse response = imageModel.call(prompt);
        assertThat(response).isNotNull();
        assertThat(response.getResults()).hasSize(1);
        assertThat(response.getResult().getOutput().getUrl()).isNotEmpty();
        assertThat((String) response.getMetadata().get("taskStatus")).isEqualTo("SUCCEEDED");
    }

    @Test
    void wan2_2_T2IImageModelAsyncModeTextToImage() {
        DashScopeImageOptions options = DashScopeImageOptions.builder()
                .model(ImageModel.WAN_2_2_T2I_FLASH.getValue())
                .invokeMode(InvokeMode.ASYNC)
                .n(1)
                .build();

        ImagePrompt prompt = new ImagePrompt("A flower shop with delicate windows, beautiful wooden doors, flowers.", options);
        ImageResponse response = imageModel.call(prompt);
        assertThat(response).isNotNull();
        assertThat(response.getResults()).hasSize(1);
        assertThat(response.getResult().getOutput().getUrl()).isNotEmpty();
        assertThat((String) response.getMetadata().get("taskStatus")).isEqualTo("SUCCEEDED");
    }

    @Test
    void wanx_v1ImageModelAsyncModeTextToImage() {
        DashScopeImageOptions options = DashScopeImageOptions.builder()
                .model(ImageModel.WANX_V1.getValue())
                .n(1)
                .build();

        ImagePrompt prompt = new ImagePrompt("A flower shop with delicate windows, beautiful wooden doors, flowers.", options);
        ImageResponse response = imageModel.call(prompt);
        assertThat(response).isNotNull();
        assertThat(response.getResults()).hasSize(1);
        assertThat(response.getResult().getOutput().getUrl()).isNotEmpty();
        assertThat((String) response.getMetadata().get("taskStatus")).isEqualTo("SUCCEEDED");
    }

    @Test
    void wan_2_7GenerationImageModelSyncModeTextToImage() {
        DashScopeImageOptions options = DashScopeImageOptions.builder()
                .model(ImageModel.WAN_2_7_IMAGE.getValue())
                .n(1)
                .build();

        ImagePrompt prompt = new ImagePrompt("A flower shop with delicate windows, beautiful wooden doors, flowers.", options);
        ImageResponse response = imageModel.call(prompt);
        assertThat(response).isNotNull();
        assertThat(response.getResults()).hasSize(1);
        assertThat(response.getResult().getOutput().getUrl()).isNotEmpty();
        assertThat((String) response.getMetadata().get("taskStatus")).isEqualTo("SUCCEEDED");
    }

    @Test
    void wan_2_7GenerationImageModelAsyncModeTextToImage() {
        DashScopeImageOptions options = DashScopeImageOptions.builder()
                .model(ImageModel.WAN_2_7_IMAGE.getValue())
                .invokeMode(InvokeMode.ASYNC)
                .n(1)
                .build();

        ImagePrompt prompt = new ImagePrompt("A flower shop with delicate windows, beautiful wooden doors, flowers.", options);
        ImageResponse response = imageModel.call(prompt);
        assertThat(response).isNotNull();
        assertThat(response.getResults()).hasSize(1);
        assertThat(response.getResult().getOutput().getUrl()).isNotEmpty();
        assertThat((String) response.getMetadata().get("taskStatus")).isEqualTo("SUCCEEDED");
    }

    @Test
    void wan_2_7GenerationImageModelSyncModeEditImage() {
        DashScopeImageOptions options = DashScopeImageOptions.builder()
                .model(ImageModel.WAN_2_7_IMAGE.getValue())
                .images(List.of("https://img.alicdn.com/imgextra/i3/O1CN0157XGE51l6iL9441yX_!!6000000004770-49-tps-1104-1472.webp",
                        "https://img.alicdn.com/imgextra/i3/O1CN01SfG4J41UYn9WNt4X1_!!6000000002530-49-tps-1696-960.webp"))
                .bboxList(new Integer[][][]{{},{{989, 515, 1138, 681}}})
                .n(1)
                .build();

        ImagePrompt prompt = new ImagePrompt("Place the alarm clock of Figure 1 in the box of Figure 2 to keep the scene and light blending naturally.", options);
        ImageResponse response = imageModel.call(prompt);
        assertThat(response).isNotNull();
        assertThat(response.getResults()).hasSize(1);
        assertThat(response.getResult().getOutput().getUrl()).isNotEmpty();
        assertThat((String) response.getMetadata().get("taskStatus")).isEqualTo("SUCCEEDED");
    }

    @Test
    void wan_2_5I2IAsyncModeEditImage() {
        DashScopeImageOptions options = DashScopeImageOptions.builder()
                .model(ImageModel.WAN_2_5_I2I_PREVIEW.getValue())
                .images(List.of("https://img.alicdn.com/imgextra/i2/O1CN01vHOj4h28jOxUJPwY8_!!6000000007968-49-tps-1344-896.webp"))
                .n(1)
                .build();

        ImagePrompt prompt = new ImagePrompt("Replace the floral dress with a vintage-style lace dress with exquisite embroidery details on the neckline and cuffs.", options);
        ImageResponse response = imageModel.call(prompt);
        assertThat(response).isNotNull();
        assertThat(response.getResults()).hasSize(1);
        assertThat(response.getResult().getOutput().getUrl()).isNotEmpty();
        assertThat((String) response.getMetadata().get("taskStatus")).isEqualTo("SUCCEEDED");
    }

    @Test
    void wanx_sketch_to_image_liteModelAsyncModeEditImage() {
        DashScopeImageOptions options = DashScopeImageOptions.builder()
                .model(ImageModel.WANX_SKETCH_TO_IMAGE_LITE.getValue())
                .sketchImageUrl("https://help-static-aliyun-doc.aliyuncs.com/assets/img/zh-CN/6609471071/p743851.jpg")
                .n(1)
                .build();

        ImagePrompt prompt = new ImagePrompt("A towering tree.", options);
        ImageResponse response = imageModel.call(prompt);
        assertThat(response).isNotNull();
        assertThat(response.getResults()).hasSize(1);
        assertThat(response.getResult().getOutput().getUrl()).isNotEmpty();
        assertThat((String) response.getMetadata().get("taskStatus")).isEqualTo("SUCCEEDED");
    }

    @Test
    void wanx_x_paintingModelAsyncModeEditImage() {
        DashScopeImageOptions options = DashScopeImageOptions.builder()
                .model(ImageModel.WANX_X_PAINTING.getValue())
                .baseImageUrl("http://synthesis-source.oss-accelerate.aliyuncs.com/lingji/validation/mask2img/demo/source3.jpg")
                .maskImageUrl("http://synthesis-source.oss-accelerate.aliyuncs.com/lingji/validation/mask2img/demo/glasses.png")
                .n(1)
                .build();

        ImagePrompt prompt = new ImagePrompt("A dog wearing red glasses.", options);
        ImageResponse response = imageModel.call(prompt);
        assertThat(response).isNotNull();
        assertThat(response.getResults()).hasSize(1);
        assertThat(response.getResult().getOutput().getUrl()).isNotEmpty();
        assertThat((String) response.getMetadata().get("taskStatus")).isEqualTo("SUCCEEDED");
    }

    @Test
    void Z_ImageGenerationModelSyncModeTextToImage() {
        DashScopeImageOptions options = DashScopeImageOptions.builder()
                .model(ImageModel.Z_IMAGE_TURBO.getValue())
                .build();

        ImagePrompt prompt = new ImagePrompt("A dog wearing red glasses.", options);
        ImageResponse response = imageModel.call(prompt);
        assertThat(response).isNotNull();
        assertThat(response.getResults()).hasSize(1);
        assertThat(response.getResult().getOutput().getUrl()).isNotEmpty();
    }

    @Test
    void KLingImageGenerationModelSyncModeTextToImage() {
        DashScopeImageOptions options = DashScopeImageOptions.builder()
                .model(ImageModel.KLING_V3_IMAGE_GENERATION.getValue())
                .n(1)
                .build();

        ImagePrompt prompt = new ImagePrompt("A flower shop with delicate windows, beautiful wooden doors, flowers.", options);
        ImageResponse response = imageModel.call(prompt);
        assertThat(response).isNotNull();
        assertThat(response.getResults()).hasSize(1);
        assertThat(response.getResult().getOutput().getUrl()).isNotEmpty();
        assertThat((String) response.getMetadata().get("taskStatus")).isEqualTo("SUCCEEDED");
    }

    @Test
    void KLingImageGenerationModelSyncModeImageToImage() {
        DashScopeImageOptions options = DashScopeImageOptions.builder()
                .model(ImageModel.KLING_V3_OMNI_IMAGE_GENERATION.getValue())
                .images(List.of("https://cdn.wanx.aliyuncs.com/tmp/pressure/umbrella1.png",
                        "https://img.alicdn.com/imgextra/i3/O1CN01SfG4J41UYn9WNt4X1_!!6000000002530-49-tps-1696-960.webp"))
                .n(1)
                .build();

        ImagePrompt prompt = new ImagePrompt("Refer to the style in Figure 1 and the background in Figure 2 to generate tomato scrambled eggs.", options);
        ImageResponse response = imageModel.call(prompt);
        assertThat(response).isNotNull();
        assertThat(response.getResults()).hasSize(1);
        assertThat(response.getResult().getOutput().getUrl()).isNotEmpty();
        assertThat((String) response.getMetadata().get("taskStatus")).isEqualTo("SUCCEEDED");
    }

    @Test
    void wanx_style_repaint_v1ImageModelAsyncMode() {
        DashScopeImageOptions options = DashScopeImageOptions.builder()
                .model(ImageModel.WANX_STYLE_REPAINT_V1.getValue())
                .baseImageUrl("https://vigen-video.oss-cn-shanghai.aliyuncs.com/demo_image/image_demo_input.png")
                .styleIndex(3)
                .build();

        ImagePrompt prompt = new ImagePrompt("A towering tree.", options);
        ImageResponse response = imageModel.call(prompt);
        assertThat(response).isNotNull();
        assertThat(response.getResults()).hasSize(1);
        assertThat(response.getResult().getOutput().getUrl()).isNotEmpty();
        assertThat((String) response.getMetadata().get("taskStatus")).isEqualTo("SUCCEEDED");
    }

    @Test
    void image_out_paintingImageModelAsyncMode() {
        DashScopeImageOptions options = DashScopeImageOptions.builder()
                .model(ImageModel.IMAGE_OUT_PAINTING.getValue())
                .baseImageUrl("https://vigen-video.oss-cn-shanghai.aliyuncs.com/demo_image/image_demo_input.png")
                .angle(45)
                .xScale(1.5F)
                .yScale(1.5F)
                .watermark(true)
                .build();

        ImagePrompt prompt = new ImagePrompt("A towering tree.", options);
        ImageResponse response = imageModel.call(prompt);
        assertThat(response).isNotNull();
        assertThat(response.getResults()).hasSize(1);
        assertThat(response.getResult().getOutput().getUrl()).isNotEmpty();
        assertThat((String) response.getMetadata().get("taskStatus")).isEqualTo("SUCCEEDED");
    }

    @Test
    void wanxVirtualModelAsyncMode() {
        DashScopeImageOptions options = DashScopeImageOptions.builder()
                .model(ImageModel.WANX_VIRTUALMODEL.getValue())
                .baseImageUrl("https://huarong123.oss-cn-hangzhou.aliyuncs.com/image/%E7%9C%9F%E4%BA%BA%E6%A8%A1%E7%89%B9%E5%AE%9E%E6%8B%8D-%E5%A5%B3%20%281%29.jpeg")
                .maskImageUrl("https://huarong123.oss-cn-hangzhou.aliyuncs.com/image/image.jpg")
                .facePrompt("Young woman, with a beautiful face and the highest quality.")
                .shortSideSize("512")
                .n(1)
                .build();

        ImagePrompt prompt = new ImagePrompt("A young woman wearing white shorts, a minimalist palette, long takes, and two-tone effects (dark silver and light pink).", options);
        ImageResponse response = imageModel.call(prompt);
        assertThat(response).isNotNull();
        assertThat(response.getResults()).hasSize(1);
        assertThat(response.getResult().getOutput().getUrl()).isNotEmpty();
        assertThat((String) response.getMetadata().get("taskStatus")).isEqualTo("SUCCEEDED");
    }

    @Test
    void shoemodel_v1VirtualModelAsyncMode() {
        DashScopeImageOptions options = DashScopeImageOptions.builder()
                .model(ImageModel.SHOEMODEL_V1.getValue())
                .templateImageUrl("https://help-static-aliyun-doc.aliyuncs.com/assets/img/zh-CN/8268778171/p809310.webp")
                .shoeImageUrl(List.of("https://help-static-aliyun-doc.aliyuncs.com/assets/img/zh-CN/8268778171/p809301.webp"))
                .n(1)
                .build();

        ImagePrompt prompt = new ImagePrompt("Change the shoes on the model.", options);
        ImageResponse response = imageModel.call(prompt);
        assertThat(response).isNotNull();
        assertThat(response.getResults()).hasSize(1);
        assertThat(response.getResult().getOutput().getUrl()).isNotEmpty();
        assertThat((String) response.getMetadata().get("taskStatus")).isEqualTo("SUCCEEDED");
    }

    @Test
    void wanxPosterGenerationV1ModelAsyncModeGeneratePoster() {
        DashScopeImageOptions options = DashScopeImageOptions.builder()
                .model(ImageModel.WANX_POSTER_GENERATION_V1.getValue())
                .title("春节快乐")
                .subTitle("家庭团聚，共享天伦之乐")
                .bodyText("春节是中国最重要的传统节日之一，它象征着新的开始和希望")
                .promptTextZh("灯笼，小猫，梅花")
                .whRatios("竖版")
                .loraName("童话油画")
                .loraWeight(0.8f)
                .ctrlRatio(0.7f)
                .ctrlStep(0.7f)
                .generateMode("generate")
                .n(1)
                .build();

        ImagePrompt prompt = new ImagePrompt("春节快乐海报", options);
        ImageResponse response = imageModel.call(prompt);
        assertThat(response).isNotNull();
        assertThat(response.getResults()).hasSize(1);
        assertThat(response.getResult().getOutput().getUrl()).isNotEmpty();
        assertThat((List<String>)response.getMetadata().get("bgUrls")).hasSize(1);
        assertThat((String) response.getMetadata().get("taskStatus")).isEqualTo("SUCCEEDED");
    }

    @Test
    void imageInstanceSegmentationModelAsyncMode() {
        DashScopeImageOptions options = DashScopeImageOptions.builder()
                .model(ImageModel.IMAGE_INSTANCE_SEGMENTATION.getValue())
                .baseImageUrl("https://huarong123.oss-cn-hangzhou.aliyuncs.com/image/%E4%BA%BA%E5%83%8F%E5%88%86%E5%89%B2.png")
                .n(1)
                .build();

        ImagePrompt prompt = new ImagePrompt("Segment image", options);
        ImageResponse response = imageModel.call(prompt);
        assertThat(response).isNotNull();
        assertThat((String) response.getMetadata().get("taskStatus")).isEqualTo("SUCCEEDED");
        assertThat((String) response.getMetadata().get("outputVisImageUrl")).isNotEmpty();
    }

    @Test
    void wanx_background_generation_v2ModelAsyncMode() {
        ReferenceEdge referenceEdge = ReferenceEdge.builder()
                .foregroundEdge(List.of(
                        "https://vision-poster.oss-cn-shanghai.aliyuncs.com/lllcho.lc/data/test_data/images/huaban_soft_edge/6cdd13941cef1b11d885aea1717b983ae566b8efc9094-vcsvxa_fw658webp.png",
                        "http://vision-poster.oss-cn-shanghai.aliyuncs.com/lllcho.lc/data/test_data/images/ref_edge/2c36cc4b7da027279e87311dac48fc2d5d784b1e72c0e-x4f1wC_fw658webp.png"))
                .backgroundEdge(List.of(
                        "http://vision-poster.oss-cn-shanghai.aliyuncs.com/lllcho.lc/data/test_data/images/ref_edge/0718a9741e07c52ca5506e75c4f2b99e22fff68a4c7d3-P9WGLr_fw658webp.png"))
                .foregroundEdgePrompt(List.of("粉色桃花", "可爱小狗"))
                .backgroundEdgePrompt(List.of("树叶"))
                .build();

        DashScopeImageOptions options = DashScopeImageOptions.builder()
                .model(ImageModel.WANX_BACKGROUND_GENERATION_V2.getValue())
                .baseImageUrl("https://vision-poster.oss-cn-shanghai.aliyuncs.com/lllcho.lc/data/test_data/images/main_images/new_main_img/a.png")
                .refImg("http://vision-poster.oss-cn-shanghai.aliyuncs.com/lllcho.lc/data/test_data/images/ref_images/c5e50d27be534709817b2ab080b0162f_0.jpg")
                .referenceEdge(referenceEdge)
                .modelVersion("v3")
                .n(1)
                .refPromptWeight(0.5f)
                .noiseLevel(300)
                .build();

        ImagePrompt prompt = new ImagePrompt("山脉和晚霞", options);
        ImageResponse response = imageModel.call(prompt);
        assertThat(response).isNotNull();
        assertThat(response.getResults()).hasSize(1);
        assertThat(response.getResult().getOutput().getUrl()).isNotEmpty();
        assertThat((String) response.getMetadata().get("taskStatus")).isEqualTo("SUCCEEDED");
    }

    @Test
    void image_erase_completionModelAsyncMode() {
        DashScopeImageOptions options = DashScopeImageOptions.builder()
                .model(ImageModel.IMAGE_ERASE_COMPLETION.getValue())
                .baseImageUrl("https://huarong123.oss-cn-hangzhou.aliyuncs.com/image/%E5%9B%BE%E7%89%87%E6%93%A6%E9%99%A42-%E5%8E%9F%E5%9B%BE.png")
                .maskImageUrl("https://huarong123.oss-cn-hangzhou.aliyuncs.com/image/%E5%9B%BE%E7%89%87%E6%93%A6%E9%99%A42-%E6%93%A6%E9%99%A4.png")
                .foregroundUrl("https://huarong123.oss-cn-hangzhou.aliyuncs.com/image/%E5%9B%BE%E7%89%87%E6%93%A6%E9%99%A42-%E4%BF%9D%E7%95%99.png")
                .fastMode(false)
                .dilateFlag(true)
                .build();

        ImagePrompt prompt = new ImagePrompt("Erase and complete image", options);
        ImageResponse response = imageModel.call(prompt);
        assertThat(response).isNotNull();
        assertThat(response.getResults()).hasSize(1);
        assertThat(response.getResult().getOutput().getUrl()).isNotEmpty();
        assertThat((String) response.getMetadata().get("taskStatus")).isEqualTo("SUCCEEDED");
    }

    @Test
    void aitryonModelAsyncMode() {
        DashScopeImageOptions options = DashScopeImageOptions.builder()
                .model(ImageModel.AITRYON.getValue())
                .personImageUrl("https://help-static-aliyun-doc.aliyuncs.com/file-manage-files/zh-CN/20250626/ubznva/model_person.png")
                .topGarmentUrl("https://help-static-aliyun-doc.aliyuncs.com/file-manage-files/zh-CN/20250626/epousa/short_sleeve.jpeg")
                .resolution("-1")
                .restoreFace(true)
                .build();

        ImagePrompt prompt = new ImagePrompt("Try on garment", options);
        ImageResponse response = imageModel.call(prompt);
        assertThat(response).isNotNull();
        assertThat(response.getResults()).hasSize(1);
        assertThat(response.getResult().getOutput().getUrl()).isNotEmpty();
        assertThat((String) response.getMetadata().get("taskStatus")).isEqualTo("SUCCEEDED");
    }

    @Test
    void aitryon_refinerModelAsyncMode() {
        DashScopeImageOptions options = DashScopeImageOptions.builder()
                .model(ImageModel.AITRYON_REFINER.getValue())
                .topGarmentUrl("https://dashscope-swap.oss-cn-beijing.aliyuncs.com/aa-test/sample-top.jpg")
                .bottomGarmentUrl("https://dashscope-swap.oss-cn-beijing.aliyuncs.com/aa-test/sample-bottom.jpg")
                .personImageUrl("https://dashscope-swap.oss-cn-beijing.aliyuncs.com/aa-test/sample-person.png")
                .coarseImageUrl("https://dashscope-swap.oss-cn-beijing.aliyuncs.com/aa-test/result.png")
                .gender("woman")
                .build();

        ImagePrompt prompt = new ImagePrompt("Try on refiner", options);
        ImageResponse response = imageModel.call(prompt);
        assertThat(response).isNotNull();
        assertThat(response.getResults()).hasSize(1);
        assertThat(response.getResult().getOutput().getUrl()).isNotEmpty();
        assertThat((String) response.getMetadata().get("taskStatus")).isEqualTo("SUCCEEDED");
    }

    @Test
    void aitryon_parsing_v1ModelSyncMode() {
        DashScopeImageOptions options = DashScopeImageOptions.builder()
                .model(ImageModel.AITRYON_PARSING_V1.getValue())
                .baseImageUrl("https://help-static-aliyun-doc.aliyuncs.com/file-manage-files/zh-CN/20250630/bakbqz/aitryon_parse_model.png")
                .clothesType(List.of("upper"))
                .build();

        ImagePrompt prompt = new ImagePrompt("Try on parsing", options);
        ImageResponse response = imageModel.call(prompt);
        assertThat(response).isNotNull();
        assertThat(response.getResults()).hasSize(1);
        assertThat(response.getResult().getOutput().getUrl()).isNotEmpty();
        assertThat((String) response.getMetadata().get("taskStatus")).isEqualTo("SUCCEEDED");
        assertThat((List<String>) response.getMetadata().get("cropImgUrl")).isNotNull();
        assertThat((List<Integer>) response.getMetadata().get("bbox")).isNotNull();
    }

    @Test
    void facechain_facedetectModelSyncMode() {
        DashScopeImageOptions options = DashScopeImageOptions.builder()
                .model(ImageModel.FACECHAIN_FACEDETECT.getValue())
                .images(List.of(
                        "http://finetune-swap-wulanchabu.oss-cn-wulanchabu.aliyuncs.com/zhicheng/tmp/1E1D5AFA-3C3A-4B6F-ABD6-8742CA983C42.png",
                        "http://finetune-swap-wulanchabu.oss-cn-wulanchabu.aliyuncs.com/zhicheng/tmp/3.JPG",
                        "http://finetune-swap-wulanchabu.oss-cn-wulanchabu.aliyuncs.com/zhicheng/tmp/F2EA3984-6EE2-44CD-928F-109B7276BCB6.png"))
                .build();

        ImagePrompt prompt = new ImagePrompt("Face detect", options);
        ImageResponse response = imageModel.call(prompt);
        assertThat(response).isNotNull();
        assertThat((List<String>)response.getMetadata().get("isFace")).hasSize(3);
    }

    @Test
    void facechain_finetuneModelSyncMode() {
        DashScopeImageOptions options = DashScopeImageOptions.builder()
                .model(ImageModel.FACECHAIN_FINETUNE.getValue())
                .trainingFileIds(List.of(
                        "https://dashscope.oss-cn-beijing.aliyuncs.com/samples/fine-tune/facechain/sample1.jpg"))
                .build();

        ImagePrompt prompt = new ImagePrompt("Face finetune", options);
        ImageResponse response = imageModel.call(prompt);
        assertThat(response).isNotNull();
        assertThat((String) response.getMetadata().get("taskStatus")).isEqualTo("SUCCEEDED");
        assertThat((String) response.getMetadata().get("finetunedOutput")).isNotNull();
    }

    @Test
    void facechain_generationModelAsyncMode() {
        DashScopeImageOptions options = DashScopeImageOptions.builder()
                .model(ImageModel.FACECHAIN_GENERATION.getValue())
                .style("train_free_portrait_url_template")
                .templateImageUrl("http://viapi-test.oss-cn-shanghai.aliyuncs.com/viapi-3.0domepic/facebody/CompareFace/CompareFace-left3.png")
                .userUrls(List.of("http://viapi-test.oss-cn-shanghai.aliyuncs.com/viapi-3.0domepic/facebody/CompareFace/CompareFace-right1.png"))
                .n(1)
                .build();

        ImagePrompt prompt = new ImagePrompt("Generate portrait", options);
        ImageResponse response = imageModel.call(prompt);
        assertThat(response).isNotNull();
        assertThat(response.getResults()).hasSize(1);
        assertThat(response.getResult().getOutput().getUrl()).isNotEmpty();
        assertThat((String) response.getMetadata().get("taskStatus")).isEqualTo("SUCCEEDED");
    }

    @Test
    void wordart_semanticModelAsyncMode() {
        DashScopeImageOptions options = DashScopeImageOptions.builder()
                .model(ImageModel.WORDART_SEMANTIC.getValue())
                .text("文字创意")
                .n(2)
                .steps(80)
                .outputRatio("1024x1024")
                .fontName("dongfangdakai")
                .build();

        ImagePrompt prompt = new ImagePrompt("水果，蔬菜，温暖的色彩空间", options);
        ImageResponse response = imageModel.call(prompt);
        assertThat(response).isNotNull();
        assertThat(response.getResults()).hasSize(2);
        assertThat(response.getResult().getOutput().getUrl()).isNotEmpty();
        assertThat((String) response.getMetadata().get("taskStatus")).isEqualTo("SUCCEEDED");
    }

    @Test
    void wordart_textureModelAsyncMode() {
        DashScopeImageOptions options = DashScopeImageOptions.builder()
                .model(ImageModel.WORDART_TEXTURE.getValue())
                .text("文字创意")
                .fontName("dongfangdakai")
                .outputRatio("16:9")
                .refImg("https://dmshared-new.oss-cn-hangzhou.aliyuncs.com/0ximian/transfer_20230801/new_design/tmp/wordposter/ref_images/flower2.png")
                .imageShortSize(704)
                .n(2)
                .alphaChannel(false)
                .build();

        ImagePrompt prompt = new ImagePrompt("鲜花", options);
        ImageResponse response = imageModel.call(prompt);
        assertThat(response).isNotNull();
        assertThat(response.getResults()).hasSize(2);
        assertThat(response.getResult().getOutput().getUrl()).isNotEmpty();
        assertThat((String) response.getMetadata().get("taskStatus")).isEqualTo("SUCCEEDED");
    }
}
