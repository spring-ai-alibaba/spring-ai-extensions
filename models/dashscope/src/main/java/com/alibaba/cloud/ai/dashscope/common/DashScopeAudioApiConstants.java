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

import com.alibaba.cloud.ai.dashscope.spec.DashScopeModel.AudioModel;

import java.util.List;

/**
 * @author yingzi
 * @since 2026/1/25
 */

public class DashScopeAudioApiConstants {

    public static final String DEFAULT_WEBSOCKET_URL = "wss://dashscope.aliyuncs.com/api-ws/v1/inference";

    public static final String MULTIMODAL_GENERATION = "api/v1/services/aigc/multimodal-generation/generation";

    public static final String CHAT_COMPLETIONS = "/compatible-mode/v1/chat/completions";

    public static final String ASR_TRANSCRIPTION = "api/v1/services/audio/asr/transcription";

    public static final String QWEN_ASR = "compatible-mode/v1/chat/completions";

    public static final String ASR_CUSTOMIZATION = "/api/v1/services/audio/asr/customization";

    // 实时语音合成 - CosyVocie
    public static List<String> COSY_VOICE_MODEL_LIST = List.of(AudioModel.COSYVOICE_V1.getValue(), AudioModel.COSYVOICE_V2.getValue(), AudioModel.COSYVOICE_V3_FLASH.getValue(), AudioModel.COSYVOICE_V3_PLUS.getValue());

    // 实时语音合成 - Sambert
    public static List<String> SAMBERT_MODEL_LIST = List.of(AudioModel.SAMBERT_ZHINAN_V1.getValue(), AudioModel.SAMBERT_ZHIQI_V1.getValue(), AudioModel.SAMBERT_ZHICHU_V1.getValue(), AudioModel.SAMBERT_ZHIDE_V1.getValue(), AudioModel.SAMBERT_ZHIJIA_V1.getValue(), AudioModel.SAMBERT_ZHIRU_V1.getValue(), AudioModel.SAMBERT_ZHIQIAN_V1.getValue(), AudioModel.SAMBERT_ZHIXIANG_V1.getValue(), AudioModel.SAMBERT_ZHIWEI_V1.getValue(), AudioModel.SAMBERT_ZHIHAO_V1.getValue(), AudioModel.SAMBERT_ZHIJING_V1.getValue(), AudioModel.SAMBERT_ZHIMING_V1.getValue(), AudioModel.SAMBERT_ZHIMO_V1.getValue(), AudioModel.SAMBERT_ZHINA_V1.getValue(), AudioModel.SAMBERT_ZHISHU_V1.getValue(), AudioModel.SAMBERT_ZHISTELLA_V1.getValue(), AudioModel.SAMBERT_ZHITING_V1.getValue(), AudioModel.SAMBERT_ZHIXIAO_V1.getValue(), AudioModel.SAMBERT_ZHIYA_V1.getValue(), AudioModel.SAMBERT_ZHIYE_V1.getValue(), AudioModel.SAMBERT_ZHIYING_V1.getValue(), AudioModel.SAMBERT_ZHIYUAN_V1.getValue(), AudioModel.SAMBERT_ZHIYUE_V1.getValue(), AudioModel.SAMBERT_ZHIGUI_V1.getValue(), AudioModel.SAMBERT_ZHISHUO_V1.getValue(), AudioModel.SAMBERT_ZHIMIAO_EMO_V1.getValue(), AudioModel.SAMBERT_ZHIMAO_V1.getValue(), AudioModel.SAMBERT_ZHILUN_V1.getValue(), AudioModel.SAMBERT_ZHIFEI_V1.getValue(), AudioModel.SAMBERT_ZHIDA_V1.getValue(), AudioModel.SAMBERT_CAMILA_V1.getValue(), AudioModel.SAMBERT_PERLA_V1.getValue(), AudioModel.SAMBERT_INDAH_V1.getValue(), AudioModel.SAMBERT_CLARA_V1.getValue(), AudioModel.SAMBERT_HANNA_V1.getValue(), AudioModel.SAMBERT_BETH_V1.getValue(), AudioModel.SAMBERT_BETTY_V1.getValue(), AudioModel.SAMBERT_CALLY_V1.getValue(), AudioModel.SAMBERT_CINDY_V1.getValue(), AudioModel.SAMBERT_EVA_V1.getValue(), AudioModel.SAMBERT_DONNA_V1.getValue(), AudioModel.SAMBERT_BRIAN_V1.getValue(), AudioModel.SAMBERT_WAAN_V1.getValue());

    // 语音合成 Qwen-TTS
    public static List<String> QWEN_TTS_MODEL_LIST = List.of(AudioModel.QWEN3_TTS_FLASH.getValue(), AudioModel.QWEN3_TTS_FLASH_2025_11_27.getValue(), AudioModel.QWEN3_TTS_FLASH_2025_09_18.getValue(), AudioModel.QWEN_TTS.getValue(), AudioModel.QWEN_TTS_LATEST.getValue(), AudioModel.QWEN_TTS_2025_05_22.getValue(), AudioModel.QWEN_TTS_2025_04_10.getValue());

    // 语音翻译 - 音视频翻译 - 通义千问
    public static List<String> QWEN3_LIVE_TRANSLATE_LIST = List.of(AudioModel.QWEN3_LIVETRANSLATE_FLASH.getValue(), AudioModel.QWEN3_LIVETRANSLATE_FLASH_2025_12_01.getValue());

    // 语音翻译 - 实时长（短）语音翻译
    public static List<String> QWEN3_LONG_SHORT_TRANSLATE_LIST = List.of(AudioModel.GUMMY_REALTIME_V1.getValue(), AudioModel.GUMMY_CHAT_V1.getValue());

    // 实时语音识别
    public static List<String> PARAFORMER_FUNAS_LIST = List.of(AudioModel.PARAFORMER_REALTIME_V2.getValue(), AudioModel.PARAFORMER_REALTIME_V1.getValue(), AudioModel.PARAFORMER_REALTIME_8K_V2.getValue(), AudioModel.PARAFORMER_REALTIME_8K_V1.getValue(),
            AudioModel.FUN_ASR_REALTIME.getValue(), AudioModel.GUMMY_REALTIME_V1.getValue(), AudioModel.GUMMY_CHAT_V1.getValue());

    // 录音文件识别
    public static List<String> ASR_TRANSCRIPTION_LIST = List.of(AudioModel.PARAFORMER_V2.getValue(), AudioModel.PARAFORMER_V1.getValue(), AudioModel.PARAFORMER_8K_V2.getValue(), AudioModel.PARAFORMER_8K_V1.getValue(), AudioModel.PARAFORMER_MTL_V1.getValue(),
            AudioModel.FUN_ASR.getValue(), AudioModel.FUN_ASR_2025_11_07.getValue(), AudioModel.FUN_ASR_2025_08_25.getValue(), AudioModel.FUN_ASR_MTL.getValue(), AudioModel.FUN_ASR_MTL_2025_08_25.getValue());

    // 录音文件识别（Qwen-ASR）
    public static List<String> QWEN_ASR_MODEL_LIST = List.of(
            AudioModel.QWEN3_ASR_FLASH_FILETRANS.getValue(),AudioModel.QWEN3_ASR_FLASH.getValue(),AudioModel.QWEN3_ASR_FLASH_US.getValue(),AudioModel.QWEN3_ASR_FLASH_FILETANS.getValue()
    );

    public static boolean isWebsocketByTTSModelName(String modelName) {
        if (COSY_VOICE_MODEL_LIST.contains(modelName) || SAMBERT_MODEL_LIST.contains(modelName)) {
            return true;
        }
        return false;
    }

    public static boolean isQwenTTSModel(String modelName) {
        return QWEN_TTS_MODEL_LIST.contains(modelName);
    }

    public static boolean isLiveTranslate(String modelName) {
        return QWEN3_LIVE_TRANSLATE_LIST.contains(modelName);
    }

    public static boolean isAsr(String modelName) {
        return ASR_TRANSCRIPTION_LIST.contains(modelName);
    }

    public static boolean isQwenAsr(String modelName) {
        return QWEN_ASR_MODEL_LIST.contains(modelName);
    }
}
