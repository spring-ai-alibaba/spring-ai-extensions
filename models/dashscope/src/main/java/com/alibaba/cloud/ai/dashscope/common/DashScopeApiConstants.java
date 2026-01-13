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

import com.alibaba.cloud.ai.dashscope.observation.conventions.AiProvider;

/**
 * @author nuocheng.lxm
 * @author yuluo
 * @author xuguan
 * @since 1.0.0-M2
 */

public final class DashScopeApiConstants {

	public static final String HEADER_OPENAPI_SOURCE = "X-DashScope-OpenAPISource";

	public static final String HEADER_WORK_SPACE_ID = "X-DashScope-WorkSpace";

	public static final String HEADER_ASYNC = "X-DashScope-Async";

	public static final String HEADER_SSE = "X-DashScope-SSE";

	public static final String HEADER_DATAINSPECTION = "X-DashScope-DataInspection";

	public static final String HEADER_X_ACCEL_BUFFERING = "X-Accel-Buffering";

	public static final String ENABLED = "enable";

	public static final String SOURCE_FLAG = "CloudSDK";

	public static final String SDK_FLAG = "SpringAIAlibaba";

	public static final String DEFAULT_BASE_URL = "https://dashscope.aliyuncs.com";

	public static final String AI_DASHSCOPE_API_KEY = "AI_DASHSCOPE_API_KEY";

	public static final String TEXT_GENERATION_RESTFUL_URL = "/api/v1/services/aigc/text-generation/generation";

	public static final String TEXT_EMBEDDING_RESTFUL_URL = "/api/v1/services/embeddings/text-embedding/text-embedding";

	public static final String MULTIMODAL_GENERATION_RESTFUL_URL = "/api/v1/services/aigc/multimodal-generation/generation";

	public static final String TEXT2IMAGE_RESTFUL_URL = "/api/v1/services/aigc/text2image/image-synthesis";

	public static final String IMAGE2IMAGE_RESTFUL_URL = "/api/v1/services/aigc/image2image/image-synthesis";

	public static final String VIDEO_GENERATION_RESTFUL_URL = "/api/v1/services/aigc/video-generation/video-synthesis";

	public static final String IMAGE2VIDEO_RESTFUL_URL = "/api/v1/services/aigc/image2video/video-synthesis";

	public static final String AUDIO_TRANSCRIPTION_RESTFUL_URL = "/api/v1/services/audio/asr/transcription";

	public static final String TEXT_RERANK_RESTFUL_URL = "/api/v1/services/rerank/text-rerank/text-rerank";

	public static final String APPS_COMPLETION_RESTFUL_URL = "/api/v1/apps/{app_id}/completion";

	public static final String QUERY_TASK_RESTFUL_URL = "/api/v1/tasks/{task_id}";

	public static final String QUERY_CATEGORY_RESTFUL_URL = "/api/v1/datacenter/category/{category}/file/{file_id}/query";

	public static final String DOWNLOAD_LEASE_CATEGORY_RESTFUL_URL = "/api/v1/datacenter/category/{category_id}/file/{file_id}/download_lease";

	public static final String UPLOAD_LEASE_CATEGORY_RESTFUL_URL = "/api/v1/datacenter/category/{category_id}/upload_lease";

	public static final String ADD_FILE_CATEGORY_RESTFUL_URL = "/api/v1/datacenter/category/{category_id}/add_file";

	public static final String PIPELINE_RESTFUL_URL = "/api/v1/indices/pipeline";

	public static final String PIPELINE_SIMPLE_RESTFUL_URL = "/api/v1/indices/pipeline_simple";

	public static final String DOCUMENT_SPLITER_RESTFUL_URL = "/api/v1/indices/component/configed_transformations/spliter";

	public static final String MANAGED_INGEST_PIPELINE_RESTFUL_URL = "/api/v1/indices/pipeline/{pipeline_id}/managed_ingest";

	public static final String DELETE_PIPELINE_RESTFUL_URL = "/api/v1/indices/pipeline/{pipeline_id}/delete";

	public static final String RETRIEVE_PIPELINE_RESTFUL_URL = "/api/v1/indices/pipeline/{pipeline_id}/retrieve";

	public static final String DEFAULT_WEBSOCKET_URL = "wss://dashscope.aliyuncs.com/api-ws/v1/inference";

	public static final Integer DEFAULT_READ_TIMEOUT = 60;

	public static final String PROVIDER_NAME = AiProvider.DASHSCOPE.value();

	public static final String REQUEST_ID = "request_id";

	public static final String USAGE = "usage";

	public static final String OUTPUT = "output";

	public static final String DEFAULT_PARSER_NAME = "DASHSCOPE_DOCMIND";

	public static final String TASK_ID = "task_id";

	public static final String STATUS_CODE = "status_code";

	public static final String CODE = "code";

	public static final String MESSAGE = "message";

	public static final String MESSAGE_FORMAT = "messageFormat";

	public static final int MAX_TRY_COUNT = 10;

	public static String RETRIEVED_DOCUMENTS = "question_answer_context";

}
