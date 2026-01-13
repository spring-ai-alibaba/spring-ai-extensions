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

package com.alibaba.cloud.ai.dashscope.spec;

import com.alibaba.cloud.ai.dashscope.api.DashScopeResponseFormat;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.model.ModelOptionsUtils;
import org.springframework.ai.model.ModelResult;
import org.springframework.ai.model.ResultMetadata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author yuluo
 * @author <a href="mailto:yuluo08290126@gmail.com">yuluo</a>
 */

public class DashScopeApiSpec {

    public static final String DEFAULT_EMBEDDING_MODEL = DashScopeModel.EmbeddingModel.EMBEDDING_V2.getValue();

    public static final String DEFAULT_EMBEDDING_TEXT_TYPE = DashScopeModel.EmbeddingTextType.DOCUMENT.getValue();

    public interface ApiResponse {
        String code();
        String message();
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record CommonResponse<T>(@JsonProperty("code") String code, @JsonProperty("message") String message,
                                    @JsonProperty("data") T data) implements ApiResponse {
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record EmbeddingUsage(@JsonProperty("total_tokens") Long totalTokens) implements Usage {
        @Override
        public Integer getPromptTokens() {
            return null;
        }

        @Override
        public Integer getCompletionTokens() {
            return null;
        }

        @Override
        public Object getNativeUsage() {
            return null;
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Embedding(@JsonProperty("text_index") Integer textIndex,
                            @JsonProperty("embedding") float[] embedding) {
    }

    // @formatter:off
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record EmbeddingList(
            @JsonProperty("request_id") String requestId,
            @JsonProperty("code") String code,
            @JsonProperty("message") String message,
            @JsonProperty("output") Embeddings output,
            @JsonProperty("usage") EmbeddingUsage usage) {
    }
    // @formatter:on

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Embeddings(@JsonProperty("embeddings") List<Embedding> embeddings) {
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record EmbeddingRequestInput(@JsonProperty("texts") List<String> texts) {
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record EmbeddingRequestInputParameters(@JsonProperty("text_type") String textType,
                                                  @JsonProperty("dimension") Integer dimension) {

        @Deprecated
        public EmbeddingRequestInputParameters(String textType) {
            this(textType == null ? DEFAULT_EMBEDDING_TEXT_TYPE : textType, null);
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {

            private String textType;

            private Integer dimension;

            private Builder() {

            }

            public Builder textType(String textType) {
                this.textType = textType;
                return this;
            }

            public Builder dimension(Integer dimension) {
                this.dimension = dimension;
                return this;
            }

            public EmbeddingRequestInputParameters build() {
                // Handle null textType for FastJson compatibility.
                String finalTextType = textType == null ? DEFAULT_EMBEDDING_TEXT_TYPE : textType;
                return new EmbeddingRequestInputParameters(finalTextType, dimension);
            }

        }
    }

    /**
     * Creates an embedding vector representing the input text.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record EmbeddingRequest(@JsonProperty("model") String model,
                                   @JsonProperty("input") EmbeddingRequestInput input,
                                   @JsonProperty("parameters") EmbeddingRequestInputParameters parameters) {

        @Deprecated
        public EmbeddingRequest(String text) {
            this(DEFAULT_EMBEDDING_MODEL, new EmbeddingRequestInput(List.of(text)),
                    new EmbeddingRequestInputParameters(DEFAULT_EMBEDDING_TEXT_TYPE));
        }

        @Deprecated
        public EmbeddingRequest(String text, String model) {
            this(model, new EmbeddingRequestInput(List.of(text)),
                    new EmbeddingRequestInputParameters(DEFAULT_EMBEDDING_TEXT_TYPE));
        }

        @Deprecated
        public EmbeddingRequest(String text, String model, String textType) {
            this(model, new EmbeddingRequestInput(List.of(text)), new EmbeddingRequestInputParameters(
                    textType == null ? DEFAULT_EMBEDDING_TEXT_TYPE : textType));
        }

        @Deprecated
        public EmbeddingRequest(List<String> texts) {
            this(DEFAULT_EMBEDDING_MODEL, new EmbeddingRequestInput(texts),
                    new EmbeddingRequestInputParameters(DEFAULT_EMBEDDING_TEXT_TYPE));
        }

        @Deprecated
        public EmbeddingRequest(List<String> texts, String model) {
            this(model, new EmbeddingRequestInput(texts),
                    new EmbeddingRequestInputParameters(DEFAULT_EMBEDDING_TEXT_TYPE));
        }

        @Deprecated
        public EmbeddingRequest(List<String> texts, String model, String textType) {
            this(model, new EmbeddingRequestInput(texts), new EmbeddingRequestInputParameters(
                    textType == null ? DEFAULT_EMBEDDING_TEXT_TYPE : textType));
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {

            private final List<String> texts = new ArrayList<>();

            private String model = DEFAULT_EMBEDDING_MODEL;

            private String textType;

            private Integer dimension;

            private Builder() {
            }

            public Builder model(String model) {
                this.model = model;
                return this;
            }

            public Builder texts(String... texts) {
                this.texts.addAll(List.of(texts));
                return this;
            }

            public Builder texts(List<String> texts) {
                this.texts.addAll(texts);
                return this;
            }

            public Builder textType(String textType) {
                this.textType = textType;
                return this;
            }

            public Builder dimension(Integer dimension) {
                this.dimension = dimension;
                return this;
            }

            public EmbeddingRequest build() {
                return new EmbeddingRequest(model, new EmbeddingRequestInput(texts),
                        EmbeddingRequestInputParameters.builder().textType(textType).dimension(dimension).build());
            }

        }
    }

    /*******************************************
     * Data center.
     **********************************************/
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record UploadRequest(@JsonProperty("category_id") String categoryId,
                                @JsonProperty("file_name") String fileName, @JsonProperty("size_bytes") long fileLength,
                                @JsonProperty("content_md5") String fileMD5) {

        @JsonInclude(JsonInclude.Include.NON_NULL)
        public record AddFileRequest(@JsonProperty("lease_id") String leaseId, @JsonProperty("parser") String parser) {
        }

        @JsonInclude(JsonInclude.Include.NON_NULL)
        public record QueryFileRequest(@JsonProperty("file_id") String fileId) {
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record UploadLeaseResponse(@JsonProperty("code") String code, @JsonProperty("message") String message,
                                      @JsonProperty("data") UploadLeaseResponseData data
    ) { }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record UploadLeaseResponseData(@JsonProperty("lease_id") String leaseId,
                                          @JsonProperty("type") String type, @JsonProperty("param") UploadLeaseParamData param) {
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record UploadLeaseParamData(@JsonProperty("url") String url, @JsonProperty("method") String method,
                                       @JsonProperty("headers") Map<String, String> header) {
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record AddFileResponseData(@JsonProperty("file_id") String fileId, @JsonProperty("parser") String method) {
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record QueryFileResponseData(@JsonProperty("category") String category,
                                        @JsonProperty("file_id") String fileId, @JsonProperty("file_name") String fileName,
                                        @JsonProperty("file_type") String fileType, @JsonProperty("size_bytes") Long sizeBytes,
                                        @JsonProperty("status") String status, @JsonProperty("upload_time") String uploadtime) {
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record QueryFileParseResultData(@JsonProperty("file_id") String fileId,
                                           @JsonProperty("file_name") String fileName, @JsonProperty("lease_id") String leaseId,
                                           @JsonProperty("type") String type, @JsonProperty("param") DownloadFileParam param) {
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record DownloadFileParam(@JsonProperty("method") String method, @JsonProperty("url") String url,
                                    @JsonProperty("headers") Map<String, String> headers) {
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record DocumentSplitRequest(@JsonProperty("text") String text, @JsonProperty("chunk_size") Integer chunkSize,
                                       @JsonProperty("overlap_size") Integer overlapSize, @JsonProperty("file_type") String fileType,
                                       @JsonProperty("language") String language, @JsonProperty("separator") String separator) {

    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record DocumentSplitResponse(@JsonProperty("chunkService") DocumentSplitResponseData chunkService) {
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record DocumentSplitResponseData(@JsonProperty("chunkResult") List<DocumentChunk> chunkResult) { }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record DocumentChunk(@JsonProperty("chunk_id") int chunkId, @JsonProperty("content") String content,
                                @JsonProperty("title") String title, @JsonProperty("hier_title") String hierTitle,
                                @JsonProperty("nid") String nid, @JsonProperty("parent") String parent) {
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record UpsertPipelineRequest(@JsonProperty("name") String name,
                                        @JsonProperty("pipeline_type") String pipelineType,
                                        @JsonProperty("pipeline_description") String pipelineDescription,
                                        @JsonProperty("data_type") String dataType, @JsonProperty("config_model") String configModel,
                                        @JsonProperty("configured_transformations") List transformations,
                                        @JsonProperty("data_sources") List<DataSourcesConfig> dataSources,
                                        @JsonProperty("data_sinks") List<DataSinksConfig> dataSinks) {
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record DataSinksConfig(@JsonProperty("sink_type") String sinkType,
                                  @JsonProperty("component") DataSinksComponent component) {

        @JsonInclude(JsonInclude.Include.NON_NULL)
        public record DataSinksComponent() {
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record DataSourcesConfig(@JsonProperty("source_type") String sourceType,
                                    @JsonProperty("component") DataSourcesComponent component) {

        @JsonInclude(JsonInclude.Include.NON_NULL)
        public record DataSourcesComponent(@JsonProperty("doc_ids") List<String> docIds) {

        }

    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record ParserConfiguredTransformations(
            @JsonProperty("configurable_transformation_type") String transformationType,
            @JsonProperty("component") ParserComponent component) {
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public record ParserComponent(@JsonProperty("chunk_size") Integer chunkSize,
                                      @JsonProperty("overlap_size") Integer overlapSize, @JsonProperty("input_type") String inputType,
                                      @JsonProperty("separator") String separator, @JsonProperty("language") String language) {

        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record EmbeddingConfiguredTransformations(
            @JsonProperty("configurable_transformation_type") String transformationType,
            @JsonProperty("component") EmbeddingComponent component) {

        @JsonInclude(JsonInclude.Include.NON_NULL)
        public record EmbeddingComponent(@JsonProperty("model_name") String modelName) {

        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record RetrieverConfiguredTransformations(
            @JsonProperty("configurable_transformation_type") String transformationType,
            @JsonProperty("component") RetrieverComponent component) {
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public record RetrieverComponent(@JsonProperty("enable_rewrite") boolean enableRewrite,
                                         @JsonProperty("rewrite") List<CommonModelComponent> rewriteComponents,
                                         @JsonProperty("sparse_similarity_top_k") int sparseSimilarityTopK,
                                         @JsonProperty("dense_similarity_top_k") int denseSimilarityTopK,
                                         @JsonProperty("enable_reranking") boolean enableRerank,
                                         @JsonProperty("rerank") List<CommonModelComponent> rerankComponents,
                                         @JsonProperty("rerank_min_score") float rerankMinScore,
                                         @JsonProperty("rerank_top_n") int rerankTopN,
                                         @JsonProperty("search_filters") List<Map<String, Object>> searchFilters) {

        }

        @JsonInclude(JsonInclude.Include.NON_NULL)
        public record CommonModelComponent(@JsonProperty("model_name") String modelName) {
        }
    }


    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record UpsertPipelineResponse(@JsonProperty("id") String id,
                                         @JsonProperty("pipline_name") String pipline_name, @JsonProperty("status") String status,
                                         @JsonProperty("message") String message, @JsonProperty("code") String code) {
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record StartPipelineResponse(@JsonProperty("ingestionId") String ingestionId,
                                        @JsonProperty("status") String status, @JsonProperty("message") String message,
                                        @JsonProperty("code") String code, @JsonProperty("request_id") String requestId) {
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record QueryPipelineResponse(@JsonProperty("status") String status, @JsonProperty("message") String message,
                                        @JsonProperty("code") String code, @JsonProperty("id") String pipelineId) {
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record DelePipelineDocumentRequest(
            @JsonProperty("data_sources") List<DelePipelineDocumentDataSource> dataSources) {
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public record DelePipelineDocumentDataSource(@JsonProperty("source_type") String sourceType,
                                                     @JsonProperty("component") List<DelePipelineDocumentDataSourceComponent> component) {
        }

        @JsonInclude(JsonInclude.Include.NON_NULL)
        public record DelePipelineDocumentDataSourceComponent(@JsonProperty("doc_ids") List<String> docIds) {
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record DelePipelineDocumentResponse(@JsonProperty("status") String status,
                                               @JsonProperty("message") String message, @JsonProperty("code") String code) {
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record DocumentRetrieveRequest(@JsonProperty("query") String query,
                                          @JsonProperty("dense_similarity_top_k") int denseSimilarityTopK,
                                          @JsonProperty("sparse_similarity_top_k") int sparseSimilarityTopK,
                                          @JsonProperty("enable_rewrite") boolean enableRewrite,
                                          @JsonProperty("rewrite") List<DocumentRetrieveModelConfig> rewrite,
                                          @JsonProperty("enable_reranking") boolean enableReranking,
                                          @JsonProperty("rerank") List<DocumentRetrieveModelConfig> rerank,
                                          @JsonProperty("rerank_min_score") float rerankMinScore, @JsonProperty("rerank_top_n") int rerankTopN,
                                          @JsonProperty("search_filters") List<Map<String, Object>> searchFilters) {
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public record DocumentRetrieveModelConfig(@JsonProperty("model_name") String modelName,
                                                  @JsonProperty("class_name") String className) {
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record DocumentRetrieveResponse(@JsonProperty("status") String status,
                                           @JsonProperty("message") String message, @JsonProperty("code") String code,
                                           @JsonProperty("request_id") String requestId, @JsonProperty("total") int total,
                                           @JsonProperty("nodes") List<DocumentRetrieveResponseNode> nodes

    ) {
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public record DocumentRetrieveResponseNode(@JsonProperty("score") double score,
                                                   @JsonProperty("node") DocumentRetrieveResponseNodeData node) {
        }

        @JsonInclude(JsonInclude.Include.NON_NULL)
        public record DocumentRetrieveResponseNodeData(@JsonProperty("id_") String id,
                                                       @JsonProperty("text") String text, @JsonProperty("metadata") Map<String, Object> metadata) {
        }

        @JsonInclude(JsonInclude.Include.NON_NULL)
        public record DocumentRetrieveResponseNodeMetaData(@JsonProperty("parent") String text,
                                                           @JsonProperty("image_url") List<String> images, @JsonProperty("title") String title,
                                                           @JsonProperty("doc_id") String documentId, @JsonProperty("doc_name") String docName,
                                                           @JsonProperty("hier_title") String hierTitle) {
        }
    }

    /**
     * Represents a tool the model may call. Currently, only functions are supported as a
     * tool.
     *
     * @param type The type of the tool. Currently, only 'function' is supported.
     * @param function The function definition.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record FunctionTool(@JsonProperty("type") Type type, @JsonProperty("function") Function function) {

        /**
         * Create a tool of type 'function' and the given function definition.
         * @param function function definition.
         */
        public FunctionTool(Function function) {
            this(Type.FUNCTION, function);
        }

        /**
         * Create a tool of type 'function' and the given function definition.
         */
        public enum Type {

            /**
             * Function tool type.
             */
            @JsonProperty("function")
            FUNCTION

        }

        /**
         * Function definition.
         *
         * @param description A description of what the function does, used by the model
         * to choose when and how to call the function.
         * @param name The name of the function to be called. Must be a-z, A-Z, 0-9, or
         * contain underscores and dashes, with a maximum length of 64.
         * @param parameters The parameters the functions accepts, described as a JSON
         * Schema object. To describe a function that accepts no parameters, provide the
         * value {"type": "object", "properties": {}}.
         */
        public record Function(@JsonProperty("description") String description, @JsonProperty("name") String name,
                               @JsonProperty("parameters") Map<String, Object> parameters) {

            /**
             * Create tool function definition.
             * @param description tool function description.
             * @param name tool function name.
             * @param jsonSchema tool function schema as json.
             */
            public Function(String description, String name, String jsonSchema) {
                this(description, name, ModelOptionsUtils.jsonToMap(jsonSchema));
            }
        }
    }

    /**
     * Creates a model response for the given chat conversation.
     *
     * @param model ID of the model to use.
     * @param input request input of chat.
     */
    // @formatter:off
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record ChatCompletionRequest(
            @JsonProperty("model") String model,
            @JsonProperty("input") ChatCompletionRequestInput input,
            @JsonProperty("parameters") ChatCompletionRequestParameter parameters,
            @JsonProperty("stream") Boolean stream,
            @JsonIgnore Boolean multiModel
    ) {

        /**
         * Shortcut constructor for a chat completion request with the given messages and
         * model.
         * @param model ID of the model to use.
         * @param input request input of chat.
         */
        public ChatCompletionRequest(
                String model,
                ChatCompletionRequestInput input,
                Boolean stream
        ) {
            this(model, input, null, stream, false);
        }
    }
    // @formatter:on

    /**
     * Creates a model response for the given chat conversation.
     *
     * @param maxTokens The maximum number of tokens to generate in the chat completion.
     * The total length of input tokens and generated tokens is limited by the model's
     * context length.
     * @param stop Up to 4 sequences where the API will stop generating further tokens.
     * @param temperature What sampling temperature to use, between 0 and 1. Higher values
     * like 0.8 will make the output more random, while lower values like 0.2 will make it
     * more focused and deterministic. We generally recommend altering this or top_p but
     * not both.
     * @param topP An alternative to sampling with temperature, called nucleus sampling,
     * where the model considers the results of the tokens with top_p probability mass. So
     * 0.1 means only the tokens comprising the top 10% probability mass are considered.
     * We generally recommend altering this or temperature but not both.
     * @param tools A list of tools the model may call. Currently, only functions are
     * supported as a tool. Use this to provide a list of functions the model may generate
     * JSON inputs for.
     * @param toolChoice Controls which (if any) function is called by the model. none
     * means the model will not call a function and instead generates a message. auto
     * means the model can pick between generating a message or calling a function.
     * Specifying a particular function via {"type: "function", "function": {"name":
     * "my_function"}} forces the model to call that function. none is the default when no
     * functions are present. auto is the default if functions are present. Use the
     * {@link ToolChoiceBuilder} to create the tool choice value.
     * @param stream Whether to stream back partial progress. If set, tokens will be sent
     * as data-only server-sent events as they become available, with the stream
     * terminated by a data: [DONE] message.
     * @param vlHighResolutionImages Whether to generate high-resolution images for
     * visualization.
     * @param enableThinking Whether to enable the model to think before generating
     * responses. This is useful for complex tasks where the model needs to reason through
     * the problem before providing an answer.
     * @param thinkingBudget The maximum length of the thinking process takes effect when
     * enable_thinking is true, and is suitable for Qwen3 full system model.
     * @param vlEnableImageHwOutput Whether to return the size after image scaling. The
     * model will scale the input image. When configured as True, it will return the
     * height and width of the image after being scaled. When the streaming output is
     * turned on, this information will be returned in the last data block (chunk)
     * @param logprobs Whether to return the logarithmic probability of the output token.
     * @param topLogprobs Specifies the number of candidate tokens that return the maximum
     * probability of the model when generated at each step. Value range: [0,5] Effective
     * only if logprobs is true.
     */
    // @formatter:off
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record ChatCompletionRequestParameter(
            @JsonProperty("result_format") String resultFormat,
            @JsonProperty("seed") Integer seed,

            @JsonProperty("top_p") Double topP,
            @JsonProperty("top_k") Integer topK,
            @JsonProperty("repetition_penalty") Double repetitionPenalty,
            @JsonProperty("presence_penalty") Double presencePenalty,
            @JsonProperty("temperature") Double temperature,
            @JsonProperty("stop") List<Object> stop,

            @JsonProperty("enable_search") Boolean enableSearch,
            @JsonProperty("search_options") SearchOptions searchOptions,

            @JsonProperty("response_format") DashScopeResponseFormat responseFormat,
            @JsonProperty("incremental_output") Boolean incrementalOutput,

            @JsonProperty("tools") List<FunctionTool> tools,
            @JsonProperty("tool_choice") Object toolChoice,
            @JsonProperty("parallel_tool_calls") Boolean parallelToolCalls,

            @JsonProperty("enable_thinking") Boolean enableThinking,
            @JsonProperty("thinking_budget") Integer thinkingBudget,

            @JsonProperty("vl_high_resolution_images") Boolean vlHighResolutionImages,
            @JsonProperty("vl_enable_image_hw_output") Boolean vlEnableImageHwOutput,
            @JsonProperty("ocr_options")  OCROption ocrOptions,

            @JsonProperty("logprobs") Boolean logprobs,
            @JsonProperty("top_logprobs") Integer topLogprobs,

            @JsonProperty("translation_options") TranslationOptions translationOptions,

            @JsonProperty("stream") Boolean stream,
            @JsonProperty("stream_options") Object streamOptions,

            // modalities supported input modalities for the model, such as ["text", "audio"] and ["text"]
            // For Qwen-Omni model.
            @JsonProperty("modalities") List<String> modalities,
            // When modalities has, audio is required.
            @JsonProperty("audio") Object audio,

            @JsonProperty("max_tokens") Integer maxTokens,
            @JsonProperty("max_input_tokens") Integer maxInputTokens,

            // This parameter is only available when calling the recording file recognition-Tongyi Qianwen function, and is only effective for the Tongyi Qianwen 3 ASR model.
            // It is used to specify whether certain functions are enabled.
            @JsonProperty("asr_options") Object asrOptions,

            // The default value is "model_detailed_report"
            // Only when the Qwen-Deep-Research model for in-depth research is invoked, is the format for specifying the output content determined.
            // Optional values:
            //   "model_detailed_report": Detailed Report, approximately 6,000 words
            //   "model_summary_report": Summary Report, approximately 1500 - 2000 words
            @JsonProperty("output_format") String outputFormat,
            @JsonProperty("extra_body") Map<String, Object> extraBody
    ) {

        /**
         * Compact constructor that ensures extraBody is initialized as a mutable HashMap
         * when null, enabling @JsonAnySetter to populate it during deserialization.
         */
        public ChatCompletionRequestParameter{
            if (extraBody == null) {
                extraBody = new HashMap<>();
            }
        }

        /**
         * shortcut constructor for chat request parameter
         */
        public ChatCompletionRequestParameter() {

            this(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
                    null, null, null, null, null, null, null, null, null, null, null, null, null, null);
         }

        /**
         * Overrides the default accessor to add @JsonAnyGetter annotation.
         * This causes Jackson to flatten the extraBody map contents to the top level of the JSON.
         * @return The extraBody map, or null if not set.
         */
        @JsonAnyGetter
        public Map<String, Object> extraBody() {
            return this.extraBody;
        }

        /**
         * Handles deserialization of unknown properties into the extraBody map.
         * This enables JSON with extra fields to be deserialized into ChatCompletionRequest,
         * which is useful for implementing OpenAI API proxy servers with @RestController.
         * @param key The property name
         * @param value The property value
         */
        @JsonAnySetter
        private void setExtraBodyProperty(String key, Object value) {
            if (this.extraBody != null) {
                this.extraBody.put(key, value);
            }
        }

        /**
         * Helper factory that creates a tool_choice of type 'none', 'auto' or selected
         * function by name.
         */
        public static class ToolChoiceBuilder {

            /**
             * Model can pick between generating a message or calling a function.
             */
            public static final String AUTO = "auto";

            /**
             * Model will not call a function and instead generates a message
             */
            public static final String NONE = "none";

            /**
             * Specifying a particular function forces the model to call that function.
             */
            public static Object function(String functionName) {
                return Map.of("type", "function", "function", Map.of("name", functionName));
            }

        }
    }
    // @formatter:on

    /**
     * Translation parameters that need to be configured when you use the translation
     * model TranslationMemory A pair of statements representing the source and target
     * languages in translation memory。 Term Terminology pairs representing source and
     * target languages
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record TranslationOptions(@JsonProperty("source_lang") String sourceLang,
                                     @JsonProperty("target_lang") String targetLang, @JsonProperty("terms") List<LanguagePair> terms,
                                     @JsonProperty("tm_list") List<LanguagePair> tmList, @JsonProperty("domains") String domains) {
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record LanguagePair(@JsonProperty("source") String source, @JsonProperty("target") String target) {
    }

    /**
     * OCR recognizes parameter configuration, where task is the built-in task name, and
     * the supported options are: "text_recognition": general text recognition
     * "key_information_extraction": information extraction "document_parsing": document
     * parsing "table_parsing": table parsing "formula_recognition": formula recognition
     * "multi_lan": multilingual recognition taskConfig is (optional) Used when the
     * built-in task task is "key_information_extraction". where result_schema object
     * (required) represents the field that needs to be extracted by the model. It can be
     * any form of JSON structure and can be nested up to 3 layers of JSON objects. You
     * just need to fill in the key of the JSON object and keep the value empty.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record OCROption(@JsonProperty("task") String task,
                            @JsonProperty("task_config") List<TaskConfig> taskConfig) {
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record TaskConfig(@JsonProperty("result_schema") Object resultSchema) {
    }

    /**
     * Request input of chat
     *
     * @param messages chat messages
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record ChatCompletionRequestInput(@JsonProperty("messages") List<ChatCompletionMessage> messages) {
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record ChatCompletionAnnotations(
            // Fixed to audio_info, indicating audio information。
            @JsonProperty("type") String type,
            // Detected language
            @JsonProperty("language") String language,
            // Recognized audio emotions
            @JsonProperty("emotion") String emotion
    ){ }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record ChatCompletionExtra(
            @JsonProperty("deep_research") DeepResearch deepResearch
    ) { }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record DeepResearch(
            // task infos
            @JsonProperty("research") Research research,
            @JsonProperty("reference") Reference reference
    ) { }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Reference(
            @JsonProperty("icon") String icon,
            @JsonProperty("url") String url,
            @JsonProperty("title") String title,
            @JsonProperty("index_number") String indexNumber,
            @JsonProperty("description") String description
    ) { }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Research(
            // task infos
            @JsonProperty("id") Integer id,
            @JsonProperty("webSites") List<Websites> webSites,
            @JsonProperty("learningMap") Object learningMap
    ) { }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Websites(
            @JsonProperty("title") String title,
            @JsonProperty("description") String description,
            @JsonProperty("url") String url,
            @JsonProperty("favicon") String favicon
    ) { }

    /**
     * Message comprising the conversation.
     *
     * @param rawContent The contents of the message. Can be either a {@link MediaContent}
     * or a {@link String}. The response message content is always a {@link String}.
     * @param role The role of the messages author. Could be one of the {@link Role}
     * types.
     * @param name An optional name for the participant. Provides the model information to
     * differentiate between participants of the same role. In case of Function calling,
     * the name is the function name that the message is responding to.
     * @param toolCallId Tool call that this message is responding to. Only applicable for
     * the {@link Role#TOOL} role and null otherwise.
     * @param toolCalls The tool calls generated by the model, such as function calls.
     * @param reasoningContent The reasoning content of the message. <a href=
     * "https://help.aliyun.com/zh/model-studio/developer-reference/deepseek">DeepSeek
     * ReasoningContent</a> Applicable only for {@link Role#ASSISTANT} role and null
     * otherwise.
     * @param partial Indicates whether this message is a partial prefix for code completion.
     * When set to true, the model will continue generating from the provided content.
     * Applicable only for {@link Role#ASSISTANT} role and null otherwise.
     */
    // format: off
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ChatCompletionMessage(
            @JsonProperty("content") Object rawContent,
            @JsonProperty("role") Role role,
            @JsonProperty("name") String name,
            @JsonProperty("tool_call_id") String toolCallId,
            @JsonProperty("tool_calls") List<ToolCall> toolCalls,
            @JsonProperty("reasoning_content") String reasoningContent,
            // refer: http://help.aliyun.com/zh/model-studio/partial-mode
            @JsonProperty("partial") Boolean partial,
            // Only when the Tongyi Qianwen in-depth research model qwen-deep-research is called,
            // it indicates the stage of the research task.
            // ResearchPlanning WebResearch WebResearch and answer.
            @JsonProperty("phase") String phase,
            // When using recording file recognition-Tongyi Qianwen,
            // output annotation information (such as language)
            @JsonProperty("annotations") List<ChatCompletionAnnotations> annotations,
            @JsonProperty("status") String status
    ) {

        /**
         * Get message content as String.
         * If content contains both audio and text, audio takes precedence.
         * Audio content is wrapped in <audio></audio> tags.
         */
        public String content() {
            if (this.rawContent == null) {
                return "";
            }

            if (this.rawContent instanceof String text) {
                return text;
            }

            if (this.rawContent instanceof List list) {
                if (list.isEmpty()) {
                    return "";
                }

                Object object = list.get(0);
                if (object instanceof Map map) {
                    if (map.isEmpty()) {
                        return "";
                    }
                    if (map.get("audio") != null && map.get("audio") instanceof Map audioMap && audioMap.get("data") != null) {
                        return String.format("<audio>%s</audio>", audioMap.get("data").toString());
                    }
                    if (map.get("text") == null) {
                        return "";
                    }
                    return map.get("text").toString();
                }
            }
            throw new IllegalStateException("The content is not valid!");
        }

        /**
         * Create a chat completion message with the given content and role. All other
         * fields are null.
         * @param content The contents of the message.
         * @param role The role of the author of this message.
         */
        public ChatCompletionMessage(Object content, Role role) {

            this(content, role, null, null, null, null, null, null, null, null);
        }
        // format: on

        /**
         * The role of the author of this message.
         */
        public enum Role {

            /**
             * System message.
             */
            @JsonProperty("system")
            SYSTEM,
            /**
             * User message.
             */
            @JsonProperty("user")
            USER,
            /**
             * Assistant message.
             */
            @JsonProperty("assistant")
            ASSISTANT,
            /**
             * Tool message.
             */
            @JsonProperty("tool")
            TOOL

        }

        /**
         * An array of content parts with a defined type. Each MediaContent can be of
         * either "text" or "image_url" type. Not both.
         *
         * @param text The text content of the message.
         * @param image The image content of the message. You can pass multiple images
         * @param video The image list of video. by adding multiple image_url content
         * parts. Image input is only supported when using the glm-4v model.
         * @param audio The audio content of the message.
         */
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public record MediaContent(@JsonProperty("type") String type, @JsonProperty("text") String text,
                                   @JsonProperty("image") String image, @JsonProperty("video") List<String> video,
                                   @JsonProperty("audio") String audio) {
            /**
             * Shortcut constructor for a text content.
             * @param text The text content of the message.
             */
            public MediaContent(String text) {
                this("text", text, null, null);
            }

            public MediaContent(String type, String text, String image, List<String> video) {
                this(type, text, image, video, null);
            }
        }

        /**
         * The relevant tool call.
         *
         * @param id The ID of the tool call. This ID must be referenced when you submit
         * the tool outputs in using the Submit tool outputs to run endpoint.
         * @param type The type of tool call the output is required for. For now, this is
         * always function.
         * @param function The function definition.
         */
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public record ToolCall(
                @JsonProperty("id") String id,
                @JsonProperty("type") String type,
                @JsonProperty("function") ChatCompletionFunction function,
                @JsonProperty("index") Integer index
        ) { }

        /**
         * The function definition.
         *
         * @param name The name of the function.
         * @param arguments The arguments that the model expects you to pass to the
         * function.
         */
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public record ChatCompletionFunction(@JsonProperty("name") String name,
                                             @JsonProperty("arguments") String arguments) {
        }
    }

    /**
     * The reason the model stopped generating tokens.
     */
    public enum ChatCompletionFinishReason {

        /**
         * normal chunk message
         */
        @JsonProperty("null")
        NULL,

        /**
         * The model hit a natural stop point or a provided stop sequence.
         */
        @JsonProperty("stop")
        STOP,
        /**
         * The maximum number of tokens specified in the request was reached.
         */
        @JsonProperty("length")
        LENGTH,
        /**
         * The content was omitted due to a flag from our content filters.
         */
        @JsonProperty("content_filter")
        CONTENT_FILTER,
        /**
         * The model called a tool.
         */
        @JsonProperty("tool_calls")
        TOOL_CALLS,
        /**
         * Only for compatibility with Mistral AI API.
         */
        @JsonProperty("tool_call")
        TOOL_CALL

    }

    /**
     * Represents a chat completion response returned by model, based on the provided
     * input.
     *
     * @param requestId A unique identifier for the chat completion.
     * @param output chat completion output.
     * @param usage Usage statistics for the completion request.
     */
    // format: off
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record ChatCompletion(
            @JsonProperty("request_id") String requestId,
            @JsonProperty("output") ChatCompletionOutput output,
            @JsonProperty("usage") TokenUsage usage
    ) { }
    // format: on

    /**
     * Represents a chat completion response returned by model, based on the provided
     * input.
     *
     * @param text chat completion text if result format is text.
     * @param choices A list of chat completion choices. Can be more than one if n is
     * greater than 1. used in conjunction with the seed request parameter to understand
     * when backend changes have been made that might impact determinism.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record ChatCompletionOutput(
            @JsonProperty("text") String text,
            @JsonProperty("choices") List<Choice> choices,
            @JsonProperty("search_info") SearchInfo searchInfo
    ) {

        /**
         * Chat completion choice.
         *
         * @param finishReason The reason the model stopped generating tokens.
         * @param message A chat completion message generated by the model.
         */
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public record Choice(@JsonProperty("finish_reason") ChatCompletionFinishReason finishReason,
                             @JsonProperty("message") ChatCompletionMessage message,
                             @JsonProperty("logprobs") ChatCompletionLogprobs logprobs,
                             @JsonProperty("index") Integer index) {
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record SearchInfo(
            @JsonProperty("search_results") List<SearchResult> searchResults,
            @JsonProperty("extra_tool_info") List<ExtraToolInfo> extraToolInfo
    ) { }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record ExtraToolInfo(
            @JsonProperty("result") String result,
            @JsonProperty("tools") String tool
    ) { }


    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record SearchResult(@JsonProperty("site_name") String siteName, @JsonProperty("icon") String icon,
                               @JsonProperty("index") Integer index, @JsonProperty("title") String title,
                               @JsonProperty("url") String url) {
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record ChatCompletionLogprobs(@JsonProperty("content") List<TokenInfo> content) {
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record TokenInfo(@JsonProperty("token") String token, @JsonProperty("bytes") byte[] bytes,
                            @JsonProperty("logprob") Float logprob, @JsonProperty("top_logprobs") List<TopLogprobs> topLogprobs) {
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record TopLogprobs(@JsonProperty("token") String token, @JsonProperty("bytes") byte[] bytes,
                              @JsonProperty("logprob") Float logprob) {
    }

    /**
     * Usage statistics for the completion request.
     *
     * @param outputTokens Number of tokens in the generated completion. Only applicable
     * for completion requests.
     * @param inputTokens Number of tokens in the prompt.
     * @param totalTokens Total number of tokens used in the request (prompt +
     * completion).
     */
    // format: off
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record TokenUsage(@JsonProperty("output_tokens") Integer outputTokens,
                             @JsonProperty("input_tokens") Integer inputTokens,
                             @JsonProperty("total_tokens") Integer totalTokens,
                             @JsonProperty("image_tokens") Integer imageTokens,
                             @JsonProperty("video_tokens") Integer videoTokens,
                             @JsonProperty("audio_tokens") Integer audioTokens,

                             // Use recording file recognition - Tongyi Qianwen,
                             // which is the audio duration (unit: seconds)。
                             @JsonProperty("seconds") Integer seconds,

                             @JsonProperty("input_tokens_details") InputTokenDetailed inputTokensDetails,
                             @JsonProperty("output_tokens_details") OutputTokenDetailed outputTokensDetails,
                             @JsonProperty("prompt_tokens_details") PromptTokenDetailed promptTokenDetailed
    ) { }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record InputTokenDetailed(@JsonProperty("text_tokens") Integer text,
                                     @JsonProperty("image_tokens") Integer image, @JsonProperty("audio_tokens") Integer audio) {
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record OutputTokenDetailed(@JsonProperty("text_tokens") Integer text,
                                      // Only qwen3 models.
                                      @JsonProperty("reasoning_tokens") Integer image) {
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record PromptTokenDetailed(
            @JsonProperty("cached_tokens") Integer cachedTokens,
            @JsonProperty("cache_creation") CacheCreation cacheCreation,
            @JsonProperty("cache_creation_input_tokens") Integer cacheCreationInputTokens,
            @JsonProperty("cache_type") String cacheType

    ) { }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record CacheCreation(
            @JsonProperty("ephemeral_5m_input_tokens") Integer ephemeral_5m_input_tokens
    ) { }

    // format: on

    /**
     * Represents a chat completion response returned by model, based on the provided
     * input.
     *
     * @param requestId A unique identifier for the chat completion.
     * @param output    chat completion output.
     * @param usage     Usage statistics for the completion request.
     * @param o
     */

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record ChatCompletionChunk(@JsonProperty("request_id") String requestId,
                                      @JsonProperty("output") ChatCompletionOutput output, @JsonProperty("usage") TokenUsage usage,
                                      Object o) {
    }

    /**
     * Error response class in streaming responses.
     * @param code error code
     * @param message error message
     * @param requestId request ID
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record DashScopeErrorResponse(@JsonProperty("code") String code,
                                       @JsonProperty("message") String message,
                                       @JsonProperty("request_id") String requestId) implements ApiResponse {
    }

    /**
     * Represents dashscope rerank request input
     *
     * @param query query string for rerank.
     * @param documents list of documents for rerank.
     */
    public record RerankRequestInput(@JsonProperty("query") String query,
                                     @JsonProperty("documents") List<String> documents) {
    }

    /**
     * Represents rerank request parameters.
     *
     * @param topN return top n documents, it will return all the documents if top n not
     * pass.
     * @param returnDocuments if need to return original documents
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record RerankRequestParameter(@JsonProperty("top_n") Integer topN,
                                         @JsonProperty("return_documents") Boolean returnDocuments) {
    }

    /**
     * Represents rerank request information.
     *
     * @param model ID of the model to use.
     * @param input dashscope rerank input.
     * @param parameters rerank parameters.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record RerankRequest(@JsonProperty("model") String model, @JsonProperty("input") RerankRequestInput input,
                                @JsonProperty("parameters") RerankRequestParameter parameters) {
    }

    /**
     * Represents rerank output result
     *
     * @param index index of input document list
     * @param relevanceScore relevance score between query and document
     * @param document original document
     */
    public record RerankResponseOutputResult(@JsonProperty("index") Integer index,
                                             @JsonProperty("relevance_score") Double relevanceScore,
                                             @JsonProperty("document") Map<String, Object> document) {
    }

    /**
     * Represents rerank response output
     *
     * @param results rerank output results
     */
    public record RerankResponseOutput(@JsonProperty("results") List<RerankResponseOutputResult> results) {
    }

    /**
     * Represents rerank response
     *
     * @param output rerank response output
     * @param usage rerank token usage
     * @param requestId rerank request id
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record RerankResponse(@JsonProperty("output") RerankResponseOutput output,
                                 @JsonProperty("usage") TokenUsage usage, @JsonProperty("request_id") String requestId) {

    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record SearchOptions(@JsonProperty("enable_source") Boolean enableSource,
                                @JsonProperty("enable_citation") Boolean enableCitation,
                                @JsonProperty("citation_format") String citationFormat, @JsonProperty("forced_search") Boolean forcedSearch,
                                @JsonProperty("search_strategy") String searchStrategy) {

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {

            private Boolean enableSource;

            private Boolean enableCitation;

            private String citationFormat;

            private Boolean forcedSearch;

            private String searchStrategy;

            public Builder enableSource(Boolean enableSource) {
                this.enableSource = enableSource;
                return this;
            }

            public Builder enableCitation(Boolean enableCitation) {
                this.enableCitation = enableCitation;
                return this;
            }

            public Builder citationFormat(String citationFormat) {
                this.citationFormat = citationFormat;
                return this;
            }

            public Builder forcedSearch(Boolean forcedSearch) {
                this.forcedSearch = forcedSearch;
                return this;
            }

            public Builder searchStrategy(String searchStrategy) {
                this.searchStrategy = searchStrategy;
                return this;
            }

            public SearchOptions build() {
                return new SearchOptions(enableSource, enableCitation, citationFormat, forcedSearch, searchStrategy);
            }

        }
    }

    /**
     * Video generation request.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class VideoGenerationRequest {

        @JsonProperty("model")
        private String model;

        @JsonProperty("input")
        private VideoInput input;

        @JsonProperty("parameters")
        private VideoParameters parameters;

        public VideoGenerationRequest(String model, VideoInput input, VideoParameters parameters) {
            this.model = model;
            this.input = input;
            this.parameters = parameters;
        }

        public String getModel() {
            return this.model;
        }

        public void setModel(String model) {
            this.model = model;
        }

        public VideoInput getInput() {
            return this.input;
        }

        public void setInput(VideoInput input) {
            this.input = input;
        }

        public VideoParameters getParameters() {
            return this.parameters;
        }

        public void setParameters(VideoParameters parameters) {
            this.parameters = parameters;
        }

        /**
         * Video input parameters.
         */
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public static class VideoInput {

            @JsonProperty("prompt")
            private String prompt;

            /**
             * Reverse prompt words are used to describe content that you do not want to
             * see in the video screen, and can limit the video screen.
             */
            @JsonProperty("negative_prompt")
            private String negativePrompt;

            @JsonProperty("img_url")
            private String imageUrl;

            @JsonProperty("template")
            private String template;

            @JsonProperty("first_frame_url")
            private String firstFrameUrl;

            @JsonProperty("last_frame_url")
            private String lastFrameUrl;

            public VideoInput(String prompt) {
                this.prompt = prompt;
            }

            public VideoInput(String prompt, String negativePrompt) {
                this.prompt = prompt;
                this.negativePrompt = negativePrompt;
            }

            public VideoInput(String prompt, String negativePrompt, String imageUrl, String template) {
                this.prompt = prompt;
                this.negativePrompt = negativePrompt;
                this.imageUrl = imageUrl;
                this.template = template;
            }

            public VideoInput(String prompt, String negativePrompt, String imageUrl, String template,
                              String firstFrameUrl, String lastFrameUrl) {
                this.prompt = prompt;
                this.negativePrompt = negativePrompt;
                this.imageUrl = imageUrl;
                this.template = template;
                this.firstFrameUrl = firstFrameUrl;
                this.lastFrameUrl = lastFrameUrl;
            }

            public String getFirstFrameUrl() {
                return firstFrameUrl;
            }

            public void setFirstFrameUrl(String firstFrameUrl) {
                this.firstFrameUrl = firstFrameUrl;
            }

            public String getLastFrameUrl() {
                return lastFrameUrl;
            }

            public void setLastFrameUrl(String lastFrameUrl) {
                this.lastFrameUrl = lastFrameUrl;
            }

            public String getNegativePrompt() {
                return this.negativePrompt;
            }

            public void setNegativePrompt(String negativePrompt) {
                this.negativePrompt = negativePrompt;
            }

            public String getImageUrl() {
                return imageUrl;
            }

            public void setImageUrl(String imageUrl) {
                this.imageUrl = imageUrl;
            }

            public String getTemplate() {
                return template;
            }

            public void setTemplate(String template) {
                this.template = template;
            }

            public String getPrompt() {
                return this.prompt;
            }

            public void setPrompt(String prompt) {
                this.prompt = prompt;
            }

            public static Builder builder() {
                return new Builder();
            }

            public static class Builder {

                private String prompt;

                private String negativePrompt;

                private String imageUrl;

                private String template;

                private String firstFrameUrl;

                private String lastFrameUrl;

                public Builder() {
                }

                public Builder prompt(String prompt) {
                    this.prompt = prompt;
                    return this;
                }

                public Builder negativePrompt(String negativePrompt) {
                    this.negativePrompt = negativePrompt;
                    return this;
                }

                public Builder imageUrl(String imageUrl) {
                    this.imageUrl = imageUrl;
                    return this;
                }

                public Builder template(VideoTemplate template) {
                    this.template = Objects.nonNull(template) ? template.getValue() : "";
                    return this;
                }

                public Builder firstFrameUrl(String firstFrameUrl) {
                    this.firstFrameUrl = firstFrameUrl;
                    return this;
                }

                public Builder lastFrameUrl(String lastFrameUrl) {
                    this.lastFrameUrl = lastFrameUrl;
                    return this;
                }

                public VideoInput build() {
                    return new VideoInput(prompt, negativePrompt, imageUrl, template, firstFrameUrl, lastFrameUrl);
                }

            }

        }

        /**
         * Video generation parameters.
         */
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public static class VideoParameters {

            @JsonProperty("resolution")
            private String resolution;

            @JsonProperty("size")
            private String size;

            @JsonProperty("duration")
            private Integer duration;

            /**
             * Random number seeds are used to control the randomness of the content
             * generated by the model. The value range is [0, 2147483647].
             */
            @JsonProperty("seed")
            private Long seed;

            /**
             * Whether to enable prompt intelligent rewriting. After turning on, use the
             * big model to intelligently rewrite the input prompt. The generation effect
             * of shorter prompts is significantly improved, but it will increase
             * time-consuming.
             */
            @JsonProperty("prompt_extend")
            private Boolean promptExtend;

            public VideoParameters(String size, Integer duration, Long seed, String resolution, Boolean promptExtend) {
                this.promptExtend = promptExtend;
                this.resolution = resolution;
                this.size = size;
                this.seed = seed;
                this.duration = duration;
            }

            public String getResolution() {
                return resolution;
            }

            public void setResolution(String resolution) {
                this.resolution = resolution;
            }

            public Integer getDuration() {
                return this.duration;
            }

            public void setDuration(Integer duration) {
                this.duration = duration;
            }

            public Long getSeed() {
                return this.seed;
            }

            public void setSeed(Long seed) {
                this.seed = seed;
            }

            public Boolean getPromptExtend() {
                return this.promptExtend;
            }

            public void setPromptExtend(Boolean promptExtend) {
                this.promptExtend = promptExtend;
            }

            public String getSize() {
                return this.size;
            }

            public void setSize(String size) {
                this.size = size;
            }

            public static Builder builder() {
                return new Builder();
            }

            public static class Builder {

                private String size;

                private Integer duration;

                private Long seed;

                private Boolean promptExtend;

                private String resolution;

                public Builder() {
                }

                public Builder size(String size) {
                    this.size = size;
                    return this;
                }

                public Builder duration(Integer duration) {
                    this.duration = duration;
                    return this;
                }

                public Builder seed(Long seed) {
                    this.seed = seed;
                    return this;
                }

                public Builder promptExtend(Boolean promptExtend) {
                    this.promptExtend = promptExtend;
                    return this;
                }

                public Builder resolution(String resolution) {
                    this.resolution = resolution;
                    return this;
                }

                public VideoParameters build() {
                    return new VideoParameters(this.size, this.duration, this.seed, this.resolution, this.promptExtend);
                }

            }

        }

        public static Builder builder() {
            return new Builder();
        }

        public static final class Builder {

            private String model;

            private VideoInput input;

            private VideoParameters parameters;

            public Builder() {
            }

            public Builder model(String model) {
                this.model = model;
                return this;
            }

            public Builder input(VideoInput input) {
                this.input = input;
                return this;
            }

            public Builder parameters(VideoParameters parameters) {
                this.parameters = parameters;
                return this;
            }

            public VideoGenerationRequest build() {
                return new VideoGenerationRequest(this.model, this.input, this.parameters);
            }

        }

    }

    /**
     * Video generation response.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class VideoGenerationResponse implements ModelResult<VideoGenerationResponse.VideoOutput> {

        @JsonProperty("request_id")
        private String requestId;

        @JsonProperty("output")
        private VideoOutput output;

        @JsonProperty("usage")
        private VideoUsage usage;

        public VideoGenerationResponse() {
        }

        public VideoUsage getUsage() {
            return usage;
        }

        public void setUsage(VideoUsage usage) {
            this.usage = usage;
        }

        public String getRequestId() {
            return this.requestId;
        }

        public void setRequestId(String requestId) {
            this.requestId = requestId;
        }

        public VideoOutput getOutput() {
            return this.output;
        }

        @Override
        public ResultMetadata getMetadata() {

            // todo: add metadata
            return null;
        }

        public void setOutput(VideoOutput output) {
            this.output = output;
        }

        @Override
        public String toString() {
            return "VideoGenerationResponse{" + "requestId='" + requestId + '\'' + ", output=" + output + ", usage="
                    + usage + '}';
        }

        @JsonInclude(JsonInclude.Include.NON_NULL)
        public static class VideoUsage {

            @JsonProperty("video_duration")
            private Integer videoDuration;

            @JsonProperty("video_ratio")
            private String videoRatio;

            @JsonProperty("video_count")
            private Integer videoCount;

            public VideoUsage() {
            }

            public Integer getVideoDuration() {
                return this.videoDuration;
            }

            public void setVideoDuration(Integer videoDuration) {
                this.videoDuration = videoDuration;
            }

            public String getVideoRatio() {
                return this.videoRatio;
            }

            public void setVideoRatio(String videoRatio) {
                this.videoRatio = videoRatio;
            }

            public Integer getVideoCount() {
                return this.videoCount;
            }

            public void setVideoCount(Integer videoCount) {
                this.videoCount = videoCount;
            }

            @Override
            public String toString() {
                return "VideoUsage{" + "videoDuration=" + videoDuration + ", videoRatio='" + videoRatio + '\''
                        + ", videoCount=" + videoCount + '}';
            }

        }

        /**
         * Video output.
         */
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public static class VideoOutput {

            @JsonProperty("task_id")
            private String taskId;

            @JsonProperty("task_status")
            private String taskStatus;

            @JsonProperty("submit_time")
            private String submitTimes;

            @JsonProperty("end_time")
            private String endTime;

            @JsonProperty("scheduled_time")
            private String scheduledTime;

            @JsonProperty("video_url")
            private String videoUrl;

            @JsonProperty("orig_prompt")
            private String origPrompt;

            @JsonProperty("actual_prompt")
            private String actualPrompt;

            @JsonProperty("code")
            private String code;

            @JsonProperty("message")
            private String message;

            public VideoOutput() {
            }

            public String getTaskId() {
                return this.taskId;
            }

            public void setTaskId(String taskId) {
                this.taskId = taskId;
            }

            public String getTaskStatus() {
                return this.taskStatus;
            }

            public void setTaskStatus(String taskStatus) {
                this.taskStatus = taskStatus;
            }

            public String getSubmitTimes() {
                return this.submitTimes;
            }

            public void setSubmitTimes(String submitTimes) {
                this.submitTimes = submitTimes;
            }

            public String getEndTime() {
                return this.endTime;
            }

            public void setEndTime(String endTime) {
                this.endTime = endTime;
            }

            public String getScheduledTime() {
                return this.scheduledTime;
            }

            public void setScheduledTime(String scheduledTime) {
                this.scheduledTime = scheduledTime;
            }

            public String getVideoUrl() {
                return this.videoUrl;
            }

            public void setVideoUrl(String videoUrl) {
                this.videoUrl = videoUrl;
            }

            public String getOrigPrompt() {
                return this.origPrompt;
            }

            public void setOrigPrompt(String origPrompt) {
                this.origPrompt = origPrompt;
            }

            public String getActualPrompt() {
                return this.actualPrompt;
            }

            public void setActualPrompt(String actualPrompt) {
                this.actualPrompt = actualPrompt;
            }

            public String getCode() {
                return this.code;
            }

            public void setCode(String code) {
                this.code = code;
            }

            public String getMessage() {
                return this.message;
            }

            public void setMessage(String message) {
                this.message = message;
            }

            @Override
            public String toString() {
                return "VideoOutput{" + "taskId='" + taskId + '\'' + ", taskStatus='" + taskStatus + '\''
                        + ", submitTimes='" + submitTimes + '\'' + ", endTime='" + endTime + '\'' + ", scheduledTime='"
                        + scheduledTime + '\'' + ", videoUrl='" + videoUrl + '\'' + ", origPrompt='" + origPrompt + '\''
                        + ", actualPrompt='" + actualPrompt + '\'' + ", code='" + code + '\'' + ", message='" + message
                        + '\'' + '}';
            }

        }

    }

    public enum VideoTemplate {

        // General effects

        /**
         * 解压捏捏
         */
        SQUISH("squish"),

        /**
         * 戳戳乐
         */
        POKE("poke"),

        /**
         * 转圈圈
         */
        ROTATION("rotation"),

        /**
         * 气球膨胀
         */
        INFLATE("inflate"),

        /**
         * 分子扩散
         */
        DISSOLVE("dissolve"),

        // Single person effects

        /**
         * 时光木马
         */
        CAROUSEL("carousel"),

        /**
         * 爱你哟
         */
        SINGLEHEART("singleheart"),

        /**
         * 摇摆时刻
         */
        DANCE1("dance1"),

        /**
         * 头号甩舞
         */
        DANCE2("dance2"),

        /**
         * 星摇时刻
         */
        DANCE3("dance3"),

        /**
         * 人鱼觉醒
         */
        MERMAID("mermaid"),

        /**
         * 学术加冕
         */
        GRADUATION("graduation"),

        /**
         * 巨兽追袭
         */
        DEAGON("dragon"),

        /**
         * 财从天降
         */
        MONEY("money"),

        // Single person or animal effects

        /**
         * 魔法悬浮
         */
        FLYING("flying"),

        /**
         * 赠人玫瑰
         */
        ROSE("rose"),

        /**
         * 闪亮玫瑰
         */
        CRYSTALROSE("crystalrose"),

        // Two person effects

        /**
         * 爱的抱抱
         */
        HUG("hug"),

        /**
         * 唇齿相依
         */
        FRENCHKISS("frenchkiss"),

        /**
         * 双倍心动
         */
        COUPLEHEART("coupleheart");

        private final String value;

        VideoTemplate(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

    }


    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record DashScopeImageRequest(@JsonProperty("model") String model,
                                        @JsonProperty("input") DashScopeImageRequestInput input,
                                        @JsonProperty("parameters") DashScopeImageRequestParameter parameters

    ) {
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public record DashScopeImageRequestInput(@JsonProperty("prompt") String prompt,
                                                 @JsonProperty("negative_prompt") String negativePrompt, @JsonProperty("ref_img") String refImg,
                                                 @JsonProperty("function") String function, @JsonProperty("base_image_url") String baseImageUrl,
                                                 @JsonProperty("mask_image_url") String maskImageUrl,
                                                 @JsonProperty("sketch_image_url") String sketchImageUrl) {
        }

        @JsonInclude(JsonInclude.Include.NON_NULL)
        public record DashScopeImageRequestParameter(@JsonProperty("style") String style,
                                                     @JsonProperty("size") String size, @JsonProperty("n") Integer n, @JsonProperty("seed") Integer seed,
                                                     @JsonProperty("ref_strength") Float refStrength, @JsonProperty("ref_mode") String refMode,
                                                     @JsonProperty("prompt_extend") Boolean promptExtend, @JsonProperty("watermark") Boolean watermark,

                                                     @JsonProperty("sketch_weight") Integer sketchWeight,
                                                     @JsonProperty("sketch_extraction") Boolean sketchExtraction,
                                                     @JsonProperty("sketch_color") Integer[][] sketchColor,
                                                     @JsonProperty("mask_color") Integer[][] maskColor) {
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record DashScopeImageAsyncResponse(@JsonProperty("request_id") String requestId,
                                              @JsonProperty("output") DashScopeImageAsyncResponseOutput output,
                                              @JsonProperty("usage") DashScopeImageAsyncResponseUsage usage) {

        @JsonInclude(JsonInclude.Include.NON_NULL)
        public record DashScopeImageAsyncResponseOutput(@JsonProperty("task_id") String taskId,
                                                        @JsonProperty("task_status") String taskStatus,
                                                        @JsonProperty("results") List<DashScopeImageAsyncResponseResult> results,
                                                        @JsonProperty("task_metrics") DashScopeImageAsyncResponseTaskMetrics taskMetrics,
                                                        @JsonProperty("code") String code, @JsonProperty("message") String message) {
        }

        @JsonInclude(JsonInclude.Include.NON_NULL)
        public record DashScopeImageAsyncResponseTaskMetrics(@JsonProperty("TOTAL") Integer total,
                                                             @JsonProperty("SUCCEEDED") Integer SUCCEEDED, @JsonProperty("FAILED") Integer FAILED) {
        }

        @JsonInclude(JsonInclude.Include.NON_NULL)
        public record DashScopeImageAsyncResponseUsage(@JsonProperty("image_count") Integer imageCount) {
        }

        @JsonInclude(JsonInclude.Include.NON_NULL)
        public record DashScopeImageAsyncResponseResult(@JsonProperty("url") String url) {
        }
    }
    // format: on

}

