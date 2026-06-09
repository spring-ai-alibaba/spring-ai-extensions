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

import com.alibaba.cloud.ai.dashscope.spec.DashScopeModel.ChatModel;

import java.util.List;

/**
 * @author yingzi
 * @since 2026/6/1
 */
public class DashScopeChatApiConstants {

    public static final String TEXT_GENERATION = "/api/v1/services/aigc/text-generation/generation";

    public static final String MULTIMODAL_GENERATION = "/api/v1/services/aigc/multimodal-generation/generation";

    // Chat部分模型
    public static List<String> CHAT_MODEL_LIST = List.of(
            ChatModel.QWEN_PLUS.getValue(),
            ChatModel.QWEN_MAX.getValue(),
            ChatModel.QWEN37_MAX.getValue(),
            ChatModel.QWEN36_PLUS.getValue(),
            ChatModel.QWEN36_FLASH.getValue(),
            ChatModel.DEEPSEEK_V4_PRO.getValue(),
            ChatModel.DEEPSEEK_V4_FLASH.getValue(),
            ChatModel.GLM_51.getValue(),
            ChatModel.KIMI_K26.getValue(),
            ChatModel.MINIMAX_M25.getValue(),
            ChatModel.MIMO_V25_PRO.getValue(),
            ChatModel.QWEN_DOC_TURBO.getValue()
    );

    // 多模态部分模型
    public static List<String> MULTIMODAL_MODEL_LIST = List.of(
            ChatModel.QWEN_VL_PLUS.getValue(),
            ChatModel.QWEN3_VL_PLUS.getValue(),
            ChatModel.QWEN_VL_MAX.getValue(),
            ChatModel.QWEN_AUDIO_TURBO.getValue()
    );

}
