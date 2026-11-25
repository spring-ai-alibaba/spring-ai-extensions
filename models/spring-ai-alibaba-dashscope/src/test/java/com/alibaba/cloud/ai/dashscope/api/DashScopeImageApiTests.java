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
package com.alibaba.cloud.ai.dashscope.api;

import java.util.Collections;
import java.util.List;

import com.alibaba.cloud.ai.dashscope.image.DashScopeImageApiSpec;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageApiSpec.ImageRequest;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageApiSpec.ImageRequest.BaseInput;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageApiSpec.ImageRequest.ColorPaletteItem;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageApiSpec.ImageRequest.Element;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageApiSpec.ImageRequest.GenerationInput;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageApiSpec.ImageRequest.Image;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageApiSpec.ImageRequest.Input;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageApiSpec.ImageRequest.Parameters;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageApiSpec.ImageRequest.ReferenceEdge;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageApiSpec.ImageRequest.Resource;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageApiSpec.ImageRequest.Text;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageApiSpec.ImageResponse;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageApiSpec.ImageResponse.Choice;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageApiSpec.ImageResponse.Output;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageApiSpec.ImageResponse.Result;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageApiSpec.ImageResponse.TaskMetrics;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageApiSpec.ImageResponse.Usage;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageApiSpec.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.ai.retry.RetryUtils;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

/**
 * Tests for DashScopeImageApi class functionality
 *
 * @author yuluo
 * @author <a href="mailto:yuluo08290126@gmail.com">yuluo</a>
 * @author brianxiadong
 * @since 1.0.0-M2
 */
class DashScopeImageApiTests {

	private DashScopeImageApi imageApi;

	private RestClient mockRestClient;

	@BeforeEach
	void setUp() {
		// Setup mock RestClient
		mockRestClient = mock(RestClient.class);

		// Initialize DashScopeImageApi with test API key
		imageApi = DashScopeImageApi.builder()
			.apiKey("test-api-key")
			.workSpaceId(null)
			.restClientBuilder(RestClient.builder())
			.responseErrorHandler(RetryUtils.DEFAULT_RESPONSE_ERROR_HANDLER)
			.build();

	}

	@Test
	void testConstructorWithApiKey() {

		// Test constructor with only API key
		DashScopeImageApi api = DashScopeImageApi.builder()
			.apiKey("test-api-key")
			.workSpaceId(null)
			.responseErrorHandler(RetryUtils.DEFAULT_RESPONSE_ERROR_HANDLER)
			.restClientBuilder(RestClient.builder())
			.build();
		assertNotNull(api, "DashScopeImageApi should be created with API key");
	}

	@Test
	void testConstructorWithApiKeyAndWorkspaceId() {

		// Test constructor with API key and workspace ID
		DashScopeImageApi api = DashScopeImageApi.builder()
			.apiKey("test-api-key")
			.workSpaceId("test-workspace-id")
			.responseErrorHandler(RetryUtils.DEFAULT_RESPONSE_ERROR_HANDLER)
			.restClientBuilder(RestClient.builder())
			.build();

		assertNotNull(api, "DashScopeImageApi should be created with API key and workspace ID");
	}

	@Test
	void testConstructorWithApiKeyWorkspaceIdAndBaseUrl() {

		// Test constructor with API key, workspace ID, and base URL
		DashScopeImageApi api = DashScopeImageApi.builder()
			.apiKey("test-api-key")
			.workSpaceId("test-workspace-id")
			.baseUrl("/api/v1/services/aigc/")
			.responseErrorHandler(RetryUtils.DEFAULT_RESPONSE_ERROR_HANDLER)
			.restClientBuilder(RestClient.builder())
			.build();

		assertNotNull(api, "DashScopeImageApi should be created with API key, workspace ID, and base URL");
	}

	@Test
	void testDefaultImageModel() {
		// Test the default image model constant
		assertEquals("qwen-image", DashScopeImageApi.DEFAULT_IMAGE_MODEL, "Default image model should be 'qwen-image'");
	}

	@Nested
	class ImageRequestTests {

		@Test
		void testImageRequestConstructor() {
			Input input = Input.builder().prompt("a cat").build();
			Parameters params = Parameters.builder().size("1024*1024").build();

			ImageRequest request = new ImageRequest("stable-diffusion-xl", input, params, null);

			assertEquals("stable-diffusion-xl", request.model());
			assertNotNull(request.input());
			assertNotNull(request.parameters());
			assertNull(request.trainingFileIds());
		}

		@Test
		void testImageRequestBuilder() {
			Input input = Input.builder().prompt("a dog").build();
			Parameters params = Parameters.builder().n(2).build();
			List<String> trainingFiles = List.of("file-1", "file-2");

			ImageRequest request = ImageRequest.builder()
				.model("wanx-v1")
				.input(input)
				.parameters(params)
				.trainingFileIds(trainingFiles)
				.build();

			assertEquals("wanx-v1", request.model());
			assertSame(input, request.input());
			assertSame(params, request.parameters());
			assertEquals(2, request.trainingFileIds().size());
		}

		@Test
		void testImageRequestWithGenerationInput() {
			Message msg = new Message("user", List.of(new Message.Content("generate image", null)));
			GenerationInput genInput = new GenerationInput(List.of(msg), null);

			ImageRequest request = ImageRequest.builder()
				.model("qwen-image")
				.input(genInput)
				.build();

			assertNotNull(request.input());
			assertInstanceOf(GenerationInput.class, request.input());
		}
	}

	@Nested
	class InputTests {

		@Test
		void testInputBuilderBasicFields() {
			Input input = Input.builder()
				.prompt("a beautiful sunset")
				.negativePrompt("blurry, low quality")
				.function("stylization_all")
				.build();

			assertEquals("a beautiful sunset", input.prompt());
			assertEquals("blurry, low quality", input.negativePrompt());
			assertEquals("stylization_all", input.function());
			assertNull(input.text());
			assertNull(input.baseImageUrl());
		}

		@Test
		void testInputBuilderImageFields() {
			Input input = Input.builder()
				.prompt("edit this image")
				.baseImageUrl("https://example.com/base.png")
				.imageUrl("https://example.com/img.png")
				.maskImageUrl("https://example.com/mask.png")
				.sketchImageUrl("https://example.com/sketch.png")
				.refImageUrl("https://example.com/ref.png")
				.build();

			assertEquals("https://example.com/base.png", input.baseImageUrl());
			assertEquals("https://example.com/img.png", input.imageUrl());
			assertEquals("https://example.com/mask.png", input.maskImageUrl());
			assertEquals("https://example.com/sketch.png", input.sketchImageUrl());
			assertEquals("https://example.com/ref.png", input.refImageUrl());
		}

		@Test
		void testInputBuilderGarmentFields() {
			Input input = Input.builder()
				.topGarmentUrl("https://example.com/top.png")
				.bottomGarmentUrl("https://example.com/bottom.png")
				.personImageUrl("https://example.com/person.png")
				.build();

			assertEquals("https://example.com/top.png", input.topGarmentUrl());
			assertEquals("https://example.com/bottom.png", input.bottomGarmentUrl());
			assertEquals("https://example.com/person.png", input.personImageUrl());
		}

		@Test
		void testInputBuilderWithImageObject() {
			Image image = new Image("https://example.com/photo.png");
			Input input = Input.builder()
				.prompt("describe this")
				.image(image)
				.build();

			assertNotNull(input.image());
			assertEquals("https://example.com/photo.png", input.image().imageUrl());
		}

		@Test
		void testInputBuilderWithTextObject() {
			Text text = Text.builder()
				.textContent("Hello World")
				.fontName("Arial")
				.ttfUrl("https://example.com/font.ttf")
				.outputImageRatio("1:1")
				.build();

			Input input = Input.builder()
				.prompt("render text")
				.text(text)
				.build();

			assertNotNull(input.text());
			assertInstanceOf(Text.class, input.text());
		}

		@Test
		void testInputBuilderWithTextString() {
			Input input = Input.builder()
				.prompt("render text")
				.text("plain text content")
				.build();

			assertEquals("plain text content", input.text());
		}

		@Test
		void testInputBuilderListFields() {
			List<String> images = List.of("img1.png", "img2.png");
			List<String> shoeUrls = List.of("shoe1.png");
			List<String> userUrls = List.of("user1.png", "user2.png", "user3.png");

			Input input = Input.builder()
				.images(images)
				.shoeImageUrl(shoeUrls)
				.userUrls(userUrls)
				.build();

			assertEquals(2, input.images().size());
			assertEquals(1, input.shoeImageUrl().size());
			assertEquals(3, input.userUrls().size());
		}

		@Test
		void testInputBuilderLoraFields() {
			Input input = Input.builder()
				.loraName("style-lora")
				.loraWeight(0.8f)
				.ctrlRatio(0.5f)
				.ctrlStep(0.1f)
				.build();

			assertEquals("style-lora", input.loraName());
			assertEquals(0.8f, input.loraWeight());
			assertEquals(0.5f, input.ctrlRatio());
			assertEquals(0.1f, input.ctrlStep());
		}

		@Test
		void testInputBuilderGenerationFields() {
			Input input = Input.builder()
				.generateMode("fast")
				.generateNum(4)
				.auxiliaryParameters("extra")
				.realPerson(true)
				.build();

			assertEquals("fast", input.generateMode());
			assertEquals(4, input.generateNum());
			assertEquals("extra", input.auxiliaryParameters());
			assertTrue(input.realPerson());
		}

		@Test
		void testInputBuilderPosterFields() {
			Input input = Input.builder()
				.title("Main Title")
				.subTitle("Subtitle")
				.bodyText("Body content here")
				.promptTextZh("中文提示")
				.promptTextEn("English prompt")
				.whRatios("16:9")
				.creativeTitleLayout(true)
				.build();

			assertEquals("Main Title", input.title());
			assertEquals("Subtitle", input.subTitle());
			assertEquals("Body content here", input.bodyText());
			assertEquals("中文提示", input.promptTextZh());
			assertEquals("English prompt", input.promptTextEn());
			assertEquals("16:9", input.whRatios());
			assertTrue(input.creativeTitleLayout());
		}

		@Test
		void testInputBuilderReferenceEdge() {
			ReferenceEdge edge = ReferenceEdge.builder()
				.foregroundEdge(List.of("edge1"))
				.backgroundEdge(List.of("edge2"))
				.build();

			Input input = Input.builder()
				.referenceEdge(edge)
				.textureStyle("wood")
				.build();

			assertNotNull(input.referenceEdge());
			assertEquals("wood", input.textureStyle());
		}

		@Test
		void testInputBuilderFaceFields() {
			Input input = Input.builder()
				.faceImageUrl("https://example.com/face.png")
				.predefinedFaceId("face-123")
				.facePrompt("smiling face")
				.bgstyleScale(1.5f)
				.build();

			assertEquals("https://example.com/face.png", input.faceImageUrl());
			assertEquals("face-123", input.predefinedFaceId());
			assertEquals("smiling face", input.facePrompt());
			assertEquals(1.5f, input.bgstyleScale());
		}

		@Test
		void testInputBuilderAllRemainingFields() {
			Input input = Input.builder()
				.styleRefUrl("https://example.com/style.png")
				.backgroundImageUrl("https://example.com/bg.png")
				.templateImageUrl("https://example.com/template.png")
				.foregroundUrl("https://example.com/fg.png")
				.coarseImageUrl("https://example.com/coarse.png")
				.templateUrl("https://example.com/tmpl.png")
				.refImg("ref-img-data")
				.sourceLang("en")
				.targetLang("zh")
				.ext(Collections.singletonMap("key", "value"))
				.styleIndex(3)
				.build();

			assertEquals("https://example.com/style.png", input.styleRefUrl());
			assertEquals("https://example.com/bg.png", input.backgroundImageUrl());
			assertEquals("https://example.com/template.png", input.templateImageUrl());
			assertEquals("https://example.com/fg.png", input.foregroundUrl());
			assertEquals("https://example.com/coarse.png", input.coarseImageUrl());
			assertEquals("https://example.com/tmpl.png", input.templateUrl());
			assertEquals("ref-img-data", input.refImg());
			assertEquals("en", input.sourceLang());
			assertEquals("zh", input.targetLang());
			assertNotNull(input.ext());
			assertEquals(3, input.styleIndex());
		}

		@Test
		void testInputImplementsBaseInput() {
			Input input = Input.builder().prompt("test").build();
			assertInstanceOf(BaseInput.class, input);
		}
	}

	@Nested
	class ParametersTests {

		@Test
		void testParametersBuilderBasicFields() {
			Parameters params = Parameters.builder()
				.style("anime")
				.size("1024*1024")
				.n(4)
				.seed(42)
				.build();

			assertEquals("anime", params.style());
			assertEquals("1024*1024", params.size());
			assertEquals(4, params.n());
			assertEquals(42, params.seed());
		}

		@Test
		void testParametersBuilderRefFields() {
			Parameters params = Parameters.builder()
				.refStrength(0.7f)
				.refMode("canny")
				.refPromptWeight(0.5f)
				.build();

			assertEquals(0.7f, params.refStrength());
			assertEquals("canny", params.refMode());
			assertEquals(0.5f, params.refPromptWeight());
		}

		@Test
		void testParametersBuilderWatermarkFields() {
			Parameters params = Parameters.builder()
				.watermark(false)
				.addWatermark(true)
				.build();

			assertFalse(params.watermark());
			assertTrue(params.addWatermark());
		}

		@Test
		void testParametersBuilderSketchFields() {
			Integer[][] sketchColor = { { 0, 0, 0 }, { 255, 255, 255 } };
			Integer[][] maskColor = { { 128, 128, 128 } };

			Parameters params = Parameters.builder()
				.sketchWeight(10)
				.sketchExtraction(true)
				.sketchColor(sketchColor)
				.maskColor(maskColor)
				.build();

			assertEquals(10, params.sketchWeight());
			assertTrue(params.sketchExtraction());
			assertEquals(2, params.sketchColor().length);
			assertEquals(1, params.maskColor().length);
		}

		@Test
		void testParametersBuilderScaleAndOffset() {
			Parameters params = Parameters.builder()
				.xScale(1.5f)
				.yScale(2.0f)
				.angle(90)
				.leftOffset(10)
				.rightOffset(20)
				.topOffset(5)
				.bottomOffset(15)
				.build();

			assertEquals(1.5f, params.xScale());
			assertEquals(2.0f, params.yScale());
			assertEquals(90, params.angle());
			assertEquals(10, params.leftOffset());
			assertEquals(20, params.rightOffset());
			assertEquals(5, params.topOffset());
			assertEquals(15, params.bottomOffset());
		}

		@Test
		void testParametersBuilderQualityFlags() {
			Parameters params = Parameters.builder()
				.bestQuality(true)
				.limitImageSize(true)
				.enableSequential(false)
				.promptExtend(true)
				.thinkingMode(true)
				.build();

			assertTrue(params.bestQuality());
			assertTrue(params.limitImageSize());
			assertFalse(params.enableSequential());
			assertTrue(params.promptExtend());
			assertTrue(params.thinkingMode());
		}

		@Test
		void testParametersBuilderAspectRatioAndResolution() {
			Parameters params = Parameters.builder()
				.aspectRatio("16:9")
				.resolution("1080p")
				.shortSideSize("512")
				.outputRatio("1:1")
				.outputImageRatio("4:3")
				.build();

			assertEquals("16:9", params.aspectRatio());
			assertEquals("1080p", params.resolution());
			assertEquals("512", params.shortSideSize());
			assertEquals("1:1", params.outputRatio());
			assertEquals("4:3", params.outputImageRatio());
		}

		@Test
		void testParametersBuilderAdvancedFields() {
			Parameters params = Parameters.builder()
				.scale(3.5f)
				.modelVersion("v2")
				.noiseLevel(50)
				.fastMode(true)
				.dilateFlag(false)
				.restoreFace(true)
				.steps(30)
				.seriesAmount(4)
				.resultType("url")
				.imageShortSize(256)
				.alphaChannel(true)
				.maxImages(10)
				.enableInterleave(true)
				.build();

			assertEquals(3.5f, params.scale());
			assertEquals("v2", params.modelVersion());
			assertEquals(50, params.noiseLevel());
			assertTrue(params.fastMode());
			assertFalse(params.dilateFlag());
			assertTrue(params.restoreFace());
			assertEquals(30, params.steps());
			assertEquals(4, params.seriesAmount());
			assertEquals("url", params.resultType());
			assertEquals(256, params.imageShortSize());
			assertTrue(params.alphaChannel());
			assertEquals(10, params.maxImages());
			assertTrue(params.enableInterleave());
		}

		@Test
		void testParametersBuilderColorPalette() {
			List<ColorPaletteItem> palette = List.of(
				new ColorPaletteItem("#FF0000", "0.5"),
				new ColorPaletteItem("#00FF00", "0.3")
			);

			Parameters params = Parameters.builder()
				.colorPalette(palette)
				.build();

			assertEquals(2, params.colorPalette().size());
			assertEquals("#FF0000", params.colorPalette().get(0).hex());
			assertEquals("0.3", params.colorPalette().get(1).ratio());
		}

		@Test
		void testParametersBuilderBboxList() {
			Integer[][][] bboxList = {
				{ { 0, 0, 100, 100 } },
				{ { 50, 50, 200, 200 } }
			};

			Parameters params = Parameters.builder()
				.bboxList(bboxList)
				.build();

			assertEquals(2, params.bboxList().length);
		}

		@Test
		void testParametersBuilderResources() {
			List<Resource> resources = List.of(
				new Resource("lora", "res-001"),
				new Resource("style", "res-002")
			);

			Parameters params = Parameters.builder()
				.resources(resources)
				.build();

			assertEquals(2, params.resources().size());
			assertEquals("lora", params.resources().get(0).resourceType());
			assertEquals("res-002", params.resources().get(1).resourceId());
		}

		@Test
		void testParametersBuilderFontFields() {
			Parameters params = Parameters.builder()
				.fontName("Helvetica")
				.ttfUrl("https://example.com/font.ttf")
				.skinRetouch(true)
				.build();

			assertEquals("Helvetica", params.fontName());
			assertEquals("https://example.com/font.ttf", params.ttfUrl());
			assertTrue(params.skinRetouch());
		}

		@Test
		void testParametersBuilderGenderAndClothes() {
			Parameters params = Parameters.builder()
				.gender("female")
				.clothesType(List.of("dress", "casual"))
				.build();

			assertEquals("female", params.gender());
			assertEquals(2, params.clothesType().size());
		}

		@Test
		void testParametersConstructorAllNull() {
			Parameters params = Parameters.builder().build();

			assertNull(params.style());
			assertNull(params.size());
			assertNull(params.n());
			assertNull(params.seed());
			assertNull(params.refStrength());
			assertNull(params.promptExtend());
			assertNull(params.watermark());
			assertNull(params.colorPalette());
			assertNull(params.resources());
		}
	}

	@Nested
	class GenerationInputTests {

		@Test
		void testGenerationInputConstructor() {
			Message msg = new Message("user", List.of(new Message.Content("hello", null)));
			List<Element> elements = List.of(new Element(1), new Element(2));

			GenerationInput genInput = new GenerationInput(List.of(msg), elements);

			assertEquals(1, genInput.messages().size());
			assertEquals("user", genInput.messages().get(0).role());
			assertEquals(2, genInput.elementList().size());
		}

		@Test
		void testGenerationInputImplementsBaseInput() {
			GenerationInput genInput = new GenerationInput(null, null);
			assertInstanceOf(BaseInput.class, genInput);
		}

		@Test
		void testGenerationInputNullFields() {
			GenerationInput genInput = new GenerationInput(null, null);

			assertNull(genInput.messages());
			assertNull(genInput.elementList());
		}
	}

	@Nested
	class SupportingTypeTests {

		@Test
		void testElement() {
			Element element = new Element(42);
			assertEquals(42, element.elementId());
		}

		@Test
		void testColorPaletteItem() {
			ColorPaletteItem item = new ColorPaletteItem("#AABBCC", "0.75");
			assertEquals("#AABBCC", item.hex());
			assertEquals("0.75", item.ratio());
		}

		@Test
		void testResource() {
			Resource resource = new Resource("lora", "resource-123");
			assertEquals("lora", resource.resourceType());
			assertEquals("resource-123", resource.resourceId());
		}

		@Test
		void testImage() {
			Image image = new Image("https://example.com/photo.png");
			assertEquals("https://example.com/photo.png", image.imageUrl());
		}

		@Test
		void testTextBuilder() {
			Text text = Text.builder()
				.textContent("Hello")
				.ttfUrl("https://example.com/font.ttf")
				.fontName("Roboto")
				.outputImageRatio("16:9")
				.build();

			assertEquals("Hello", text.textContent());
			assertEquals("https://example.com/font.ttf", text.ttfUrl());
			assertEquals("Roboto", text.fontName());
			assertEquals("16:9", text.outputImageRatio());
		}

		@Test
		void testTextBuilderPartialFields() {
			Text text = Text.builder()
				.textContent("Only text")
				.build();

			assertEquals("Only text", text.textContent());
			assertNull(text.ttfUrl());
			assertNull(text.fontName());
			assertNull(text.outputImageRatio());
		}

		@Test
		void testReferenceEdgeBuilder() {
			ReferenceEdge edge = ReferenceEdge.builder()
				.foregroundEdge(List.of("fg1", "fg2"))
				.backgroundEdge(List.of("bg1"))
				.foregroundEdgePrompt(List.of("prompt1", "prompt2"))
				.backgroundEdgePrompt(List.of("bgprompt1"))
				.build();

			assertEquals(2, edge.foregroundEdge().size());
			assertEquals(1, edge.backgroundEdge().size());
			assertEquals(2, edge.foregroundEdgePrompt().size());
			assertEquals(1, edge.backgroundEdgePrompt().size());
		}

		@Test
		void testReferenceEdgeBuilderPartial() {
			ReferenceEdge edge = ReferenceEdge.builder()
				.foregroundEdge(List.of("edge"))
				.build();

			assertNotNull(edge.foregroundEdge());
			assertNull(edge.backgroundEdge());
			assertNull(edge.foregroundEdgePrompt());
			assertNull(edge.backgroundEdgePrompt());
		}
	}

	@Nested
	class MessageTests {

		@Test
		void testMessageConstructor() {
			Message.Content content = new Message.Content("describe image", "https://example.com/img.png");
			Message message = new Message("user", List.of(content));

			assertEquals("user", message.role());
			assertEquals(1, message.content().size());
			assertEquals("describe image", message.content().get(0).text());
			assertEquals("https://example.com/img.png", message.content().get(0).image());
		}

		@Test
		void testMessageContentTextOnly() {
			Message.Content content = new Message.Content("text only", null);

			assertEquals("text only", content.text());
			assertNull(content.image());
		}

		@Test
		void testMessageContentImageOnly() {
			Message.Content content = new Message.Content(null, "https://example.com/img.png");

			assertNull(content.text());
			assertEquals("https://example.com/img.png", content.image());
		}

		@Test
		void testMessageWithMultipleContents() {
			List<Message.Content> contents = List.of(
				new Message.Content("first", null),
				new Message.Content(null, "https://example.com/img.png"),
				new Message.Content("second", "https://example.com/img2.png")
			);

			Message message = new Message("assistant", contents);

			assertEquals("assistant", message.role());
			assertEquals(3, message.content().size());
		}
	}

	@Nested
	class ImageResponseTests {

		@Test
		void testImageResponseConstructor() {
			Output output = Output.builder()
				.taskId("task-001")
				.taskStatus("SUCCEEDED")
				.build();
			Usage usage = new Usage(1);

			ImageResponse response = new ImageResponse("req-001", output, usage);

			assertEquals("req-001", response.requestId());
			assertNotNull(response.output());
			assertNotNull(response.usage());
		}

		@Test
		void testImageResponseNullFields() {
			ImageResponse response = new ImageResponse("req-002", null, null);

			assertEquals("req-002", response.requestId());
			assertNull(response.output());
			assertNull(response.usage());
		}
	}

	@Nested
	class OutputTests {

		@Test
		void testOutputBuilderTaskFields() {
			Output output = Output.builder()
				.taskId("task-123")
				.taskStatus("RUNNING")
				.submitTime("2024-01-01T00:00:00Z")
				.scheduledTime("2024-01-01T00:00:01Z")
				.endTime("2024-01-01T00:00:10Z")
				.build();

			assertEquals("task-123", output.taskId());
			assertEquals("RUNNING", output.taskStatus());
			assertEquals("2024-01-01T00:00:00Z", output.submitTime());
			assertEquals("2024-01-01T00:00:01Z", output.scheduledTime());
			assertEquals("2024-01-01T00:00:10Z", output.endTime());
		}

		@Test
		void testOutputBuilderErrorFields() {
			Output output = Output.builder()
				.code("InternalError")
				.message("Something went wrong")
				.failedReason("timeout")
				.build();

			assertEquals("InternalError", output.code());
			assertEquals("Something went wrong", output.message());
			assertEquals("timeout", output.failedReason());
		}

		@Test
		void testOutputBuilderResults() {
			List<Result> results = List.of(
				new Result("https://example.com/1.png", null, null),
				new Result("https://example.com/2.png", "https://example.com/2-svg.png", null)
			);

			Output output = Output.builder()
				.results(results)
				.build();

			assertEquals(2, output.results().size());
			assertEquals("https://example.com/1.png", output.results().get(0).url());
		}

		@Test
		void testOutputBuilderUrlFields() {
			Output output = Output.builder()
				.outputImageUrl("https://example.com/output.png")
				.renderUrls(List.of("https://example.com/render1.png"))
				.bgUrls(List.of("https://example.com/bg1.png"))
				.outputVisImageUrl("https://example.com/vis.png")
				.parsingImgUrl(List.of("https://example.com/parse.png"))
				.cropImgUrl(List.of("https://example.com/crop.png"))
				.build();

			assertEquals("https://example.com/output.png", output.outputImageUrl());
			assertEquals(1, output.renderUrls().size());
			assertEquals(1, output.bgUrls().size());
			assertEquals("https://example.com/vis.png", output.outputVisImageUrl());
			assertEquals(1, output.parsingImgUrl().size());
			assertEquals(1, output.cropImgUrl().size());
		}

		@Test
		void testOutputBuilderChoices() {
			Message msg = new Message("assistant", List.of(new Message.Content("done", null)));
			List<Choice> choices = List.of(
				new Choice("stop", msg, 0),
				new Choice("length", msg, 1)
			);

			Output output = Output.builder()
				.choices(choices)
				.build();

			assertEquals(2, output.choices().size());
			assertEquals("stop", output.choices().get(0).finishReason());
			assertEquals(1, output.choices().get(1).index());
		}

		@Test
		void testOutputBuilderTaskMetrics() {
			TaskMetrics metrics = new TaskMetrics(10, 8, 2);

			Output output = Output.builder()
				.taskMetrics(metrics)
				.build();

			assertNotNull(output.taskMetrics());
			assertEquals(10, output.taskMetrics().total());
			assertEquals(8, output.taskMetrics().succeeded());
			assertEquals(2, output.taskMetrics().failed());
		}

		@Test
		void testOutputBuilderBboxAndFace() {
			Output output = Output.builder()
				.bbox(List.of(0, 0, 100, 100))
				.isFace(List.of(true, false))
				.finetunedOutput("finetuned-data")
				.build();

			assertEquals(4, output.bbox().size());
			assertEquals(2, output.isFace().size());
			assertTrue(output.isFace().get(0));
			assertFalse(output.isFace().get(1));
			assertEquals("finetuned-data", output.finetunedOutput());
		}

		@Test
		void testOutputBuilderAllNull() {
			Output output = Output.builder().build();

			assertNull(output.taskId());
			assertNull(output.taskStatus());
			assertNull(output.results());
			assertNull(output.choices());
			assertNull(output.taskMetrics());
			assertNull(output.outputImageUrl());
		}
	}

	@Nested
	class ResponseSupportingTypeTests {

		@Test
		void testUsage() {
			Usage usage = new Usage(5);
			assertEquals(5, usage.imageCount());
		}

		@Test
		void testChoice() {
			Message msg = new Message("assistant", List.of(new Message.Content("result", null)));
			Choice choice = new Choice("stop", msg, 0);

			assertEquals("stop", choice.finishReason());
			assertNotNull(choice.message());
			assertEquals(0, choice.index());
		}

		@Test
		void testResultAllFields() {
			Result result = new Result("https://example.com/img.png", "https://example.com/img-svg.png",
					"https://example.com/img-svg2.svg");

			assertEquals("https://example.com/img.png", result.url());
			assertEquals("https://example.com/img-svg.png", result.pngUrl());
			assertEquals("https://example.com/img-svg2.svg", result.svgUrl());
		}

		@Test
		void testResultUrlOnly() {
			Result result = new Result("https://example.com/img.png", null, null);

			assertEquals("https://example.com/img.png", result.url());
			assertNull(result.pngUrl());
			assertNull(result.svgUrl());
		}

		@Test
		void testTaskMetrics() {
			TaskMetrics metrics = new TaskMetrics(100, 95, 5);

			assertEquals(100, metrics.total());
			assertEquals(95, metrics.succeeded());
			assertEquals(5, metrics.failed());
		}
	}

	@Nested
	class EnumTests {

		@Test
		void testInvokeModeValues() {
			assertEquals(3, DashScopeImageApiSpec.InvokeMode.values().length);
			assertNotNull(DashScopeImageApiSpec.InvokeMode.AUTO);
			assertNotNull(DashScopeImageApiSpec.InvokeMode.SYNC);
			assertNotNull(DashScopeImageApiSpec.InvokeMode.ASYNC);
		}

		@Test
		void testInvokeModeValueOf() {
			assertEquals(DashScopeImageApiSpec.InvokeMode.AUTO, DashScopeImageApiSpec.InvokeMode.valueOf("AUTO"));
			assertEquals(DashScopeImageApiSpec.InvokeMode.SYNC, DashScopeImageApiSpec.InvokeMode.valueOf("SYNC"));
			assertEquals(DashScopeImageApiSpec.InvokeMode.ASYNC, DashScopeImageApiSpec.InvokeMode.valueOf("ASYNC"));
		}

		@Test
		void testRequestTypeValues() {
			assertEquals(3, DashScopeImageApiSpec.RequestType.values().length);
			assertNotNull(DashScopeImageApiSpec.RequestType.AUTO);
			assertNotNull(DashScopeImageApiSpec.RequestType.STANDARD);
			assertNotNull(DashScopeImageApiSpec.RequestType.GENERATION);
		}

		@Test
		void testRequestTypeValueOf() {
			assertEquals(DashScopeImageApiSpec.RequestType.AUTO, DashScopeImageApiSpec.RequestType.valueOf("AUTO"));
			assertEquals(DashScopeImageApiSpec.RequestType.STANDARD,
					DashScopeImageApiSpec.RequestType.valueOf("STANDARD"));
			assertEquals(DashScopeImageApiSpec.RequestType.GENERATION,
					DashScopeImageApiSpec.RequestType.valueOf("GENERATION"));
		}
	}

	@Nested
	class IntegrationTests {

		@Test
		void testFullRequestResponseCycle() {
			// Build a complete request
			Input input = Input.builder()
				.prompt("a mountain landscape at sunset")
				.negativePrompt("blurry, low quality")
				.build();

			Parameters params = Parameters.builder()
				.style("photography")
				.size("1024*1024")
				.n(2)
				.seed(12345)
				.promptExtend(true)
				.watermark(false)
				.build();

			ImageRequest request = ImageRequest.builder()
				.model("wanx-v1")
				.input(input)
				.parameters(params)
				.build();

			// Verify request is properly formed
			assertEquals("wanx-v1", request.model());
			assertEquals("a mountain landscape at sunset", ((Input) request.input()).prompt());
			assertEquals("photography", request.parameters().style());
			assertEquals(2, request.parameters().n());

			// Build a complete response
			Result result = new Result("https://example.com/generated.png", null, null);
			TaskMetrics metrics = new TaskMetrics(2, 2, 0);

			Output output = Output.builder()
				.taskId("task-abc")
				.taskStatus("SUCCEEDED")
				.results(List.of(result))
				.taskMetrics(metrics)
				.outputImageUrl("https://example.com/generated.png")
				.build();

			ImageResponse response = new ImageResponse("req-xyz", output, new Usage(2));

			// Verify response is properly formed
			assertEquals("req-xyz", response.requestId());
			assertEquals("SUCCEEDED", response.output().taskStatus());
			assertEquals("task-abc", response.output().taskId());
			assertEquals(1, response.output().results().size());
			assertEquals("https://example.com/generated.png", response.output().results().get(0).url());
			assertEquals(2, response.usage().imageCount());
			assertEquals(0, response.output().taskMetrics().failed());
		}

		@Test
		void testGenerationRequestWithMessages() {
			Message.Content textContent = new Message.Content("Generate an image of a cat", null);
			Message.Content imageContent = new Message.Content(null, "https://example.com/ref.png");
			Message message = new Message("user", List.of(textContent, imageContent));

			GenerationInput genInput = new GenerationInput(List.of(message), List.of(new Element(1)));

			ImageRequest request = ImageRequest.builder()
				.model("qwen-vl-max")
				.input(genInput)
				.parameters(Parameters.builder().n(1).build())
				.build();

			assertInstanceOf(GenerationInput.class, request.input());
			GenerationInput actualInput = (GenerationInput) request.input();
			assertEquals(1, actualInput.messages().size());
			assertEquals(2, actualInput.messages().get(0).content().size());
		}
	}

}
