/*
 * Copyright 2024-2025 the original author or authors.
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

import org.springframework.ai.model.ChatModelDescription;
import org.springframework.ai.model.ModelDescription;

/**
 * @author yuluo
 * @author <a href="mailto:yuluo08290126@gmail.com">yuluo</a>
 */

public class DashScopeModel {

    /**
     * Spring AI Alibaba Dashscope implements all models that support the dashscope
     * platform, and only the Qwen series models are listed here. For more model options,
     * refer to: <a href="https://help.aliyun.com/zh/model-studio/models">Model List</a>
     */
    public enum ChatModel implements ChatModelDescription {

        /**
         * The capabilities are balanced, with the reasoning effect, cost and speed falling
         * between that of Qwen Max and Qwen Flash. It is suitable for medium-complex tasks.
         */
        QWEN_PLUS("qwen-plus"),

        /**
         * The model supports a context of 32k tokens. To ensure normal use and output,
         * the API limits user input to 30k tokens.
         */
        QWEN_TURBO("qwen-turbo"),

        /**
         * The model supports an 8k tokens context, and to ensure normal use and output,
         * the API limits user input to 6k tokens.
         */
        QWEN_MAX("qwen-max"),

        /**
         * The best-performing model in the Qwen series, suitable for complex and multi-step tasks.
         */
        QWEN3_MAX("qwen3-max"),

        /**
         * The Tongyi Qianwen series is a model with the longest context window,
         * balanced capabilities and low cost. It is suitable for tasks such as long text analysis,
         * information extraction, summary and abstract generation, and classification tagging.
         */
        QWEN_LONG("qwen-long"),

        /**
         * Tongyi Qianwen MT Plus is a multilingual language model that supports.
         * Belongs to Qwen3-MT.
         */
        QWN_MT_PLUS("qwen-mt-plus"),

        /**
         * The Tongyi Qianwen mathematical model is a language model specifically designed for solving mathematical problems.
         */
        QWEN_MATH_PLUS("qwen-math-plus"),

        /**
         * Tongyi Qianwen Code Model. The latest Qwen3-Coder-Plus series models are code generation models based on Qwen3, featuring powerful Coding Agent capabilities. They excel at tool invocation and environment interaction, enabling autonomous programming. Their code capabilities are outstanding while also possessing general capabilities.
         */
        QWEN_CODER_PLUS("qwen3-coder-plus"),

        /**
         * The model supports a context of 30k tokens. To ensure normal use and output,
         * the API limits user input to 28k tokens.
         */
        QWEN_MAX_LONGCONTEXT("qwen-max-longcontext"),

        /**
         * The QwQ inference model trained based on the Qwen2.5 model has significantly enhanced
         * the model's inference capabilities through reinforcement learning.
         * The core indicators of the model's mathematical code (AIME 24/25, LiveCodeBench) as well as
         * some general indicators (IFEval, LiveBench, etc.) have reached the full health level of DeepSeek-R1.
         * <a href="https://help.aliyun.com/zh/model-studio/deep-thinking">qwen3</a>
         */
        QWQ_PLUS("qwq-plus"),

        /**
         * The QwQ inference model trained based on the Qwen2.5-32B model greatly improves
         * the model inference ability through reinforcement learning. The core indicators
         * such as the mathematical code of the model (AIME 24/25, LiveCodeBench) and some
         * general indicators (IFEval, LiveBench, etc.) have reached the level of
         * DeepSeek-R1 full blood version, and all indicators significantly exceed the
         * DeepSeek-R1-Distill-Qwen-32B, which is also based on Qwen2.5-32B.
         * <a href="https://help.aliyun.com/zh/model-studio/deep-thinking">qwen3</a>
         */
        QWEN_3_32B("qwq-32b"),

        /**
         * The QWEN-OMNI series models support the input of multiple modalities of data,
         * including video, audio, image, text, and output audio and text
         * <a href="https://help.aliyun.com/zh/model-studio/qwen-omni">qwen-omni</a>
         */
        QWEN_OMNI_TURBO("qwen-omni-turbo"),

        /**
         * Compared to the Omniverse model, it supports audio-based streaming input and has a
         * built-in VAD (Voice Activity Detection) function, which can automatically detect
         * the start and end of the user's voice.
         */
        QWEN_OMNI_FLASH_REALTIME("qwen3-omni-flash-realtime"),

        /**
         * The Qwen-Omni model can receive combined inputs of various modalities such as text,
         * images, audio, and video, and generate responses in text or voice forms.
         * It offers multiple anthropomorphic voices and supports voice output in multiple languages and dialects.
         * It can be applied in scenarios such as text creation, visual recognition, and voice assistants.
         */
        QWEN_OMNI_FLASH("qwen-omni-flash"),

        /**
         * The qwen-vl model can answer based on the pictures you pass in.
         * <a href="https://help.aliyun.com/zh/model-studio/vision">qwen-vl</a>
         */
        QWEN_VL_MAX("qwen-vl-max"),

        /**
         * Tongyi Qianwen VL is a text generation model with visual (image) comprehension capabilities.
         * It not only can perform OCR (image text recognition), but also can further summarize
         * and reason, such as extracting attributes from product photos and solving problems based on exercise diagrams, etc.
         */
        QWEN3_VL_PLUS("qwen3-vl-plus"),

        // The Qwen Flash model in the Qwen series is the fastest and most cost-effective,
        // suitable for simple tasks. Qwen Flash adopts a flexible tiered pricing system,
        // which is more reasonable than the Qwen Turbo billing model.
        // Belongs to the Qwen3 series.
        QWEN_FLASH("qwen-flash"),

        /**
         * The Tongyi Qianwen OCR model is a model specifically designed for text extraction. Compared to the Tongyi Qianwen VL model, it focuses more on the text extraction capabilities for types of images such as documents, tables, test questions, and handwritten text. It can recognize multiple languages, including English, French, Japanese, Korean, German, Russian, and Italian, etc.
         */
        QWEN_VL_OCR("qwen-vl-ocr"),

        /**
         * QVQ is a visual reasoning model that supports visual input and generates thought chains.
         * It demonstrates stronger capabilities in mathematics, programming,
         * visual analysis, creation, and general tasks.
         */
        QVQ_MAX("qvq-max"),

        // =================== DeepSeek Model =====================
        // The third-party models of the Dashscope platform are currently only listed on
        // Deepseek, refer: https://help.aliyun.com/zh/model-studio/models for
        // more models

        DEEPSEEK_R1("deepseek-r1"),

        DEEPSEEK_V3("deepseek-v3"),

        DEEPSEEK_V3_1("deepseek-v3.1"),

        KIMI_K2("Moonshot-Kimi-K2-Instruct"),

        GLM_4_6("glm-4.6");

        public final String value;

        ChatModel(String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }

        @Override
        public String getName() {
            return this.value;
        }

    }

    public enum AudioModel {

        /**
         * Qwen-TTS is a speech synthesis model of the Tongyi Qianwen series. It supports input of Chinese, English, and mixed Chinese-English texts, and outputs audio in a streaming manner.
         */
        QWEN3_TTS_FLASH("qwen3-tts-flash"),

        /**
         * Based on Qwen-TTS, it supports streaming input of text and has the ability to adaptively adjust the speech speed according to the text content and punctuation. It supports input of Chinese, English, and mixed Chinese-English texts, and outputs audio in a streaming manner.
         */
        QWEN3_TTS_FLASH_REALTIME("qwen3-tts-flash-realtime"),

        /**
         * Qwen3-ASR is based on the multi-modal foundation of Bingwen Qianwen and supports functions such as multilingual recognition, singing recognition, and noise rejection. It is recommended for use in production environments.
         */
        QWEN_ASR_FLASH("qwen3-asr-flash"),

        /**
         * CosyVoice is a new generation of generative speech synthesis model developed by Tongyi Laboratory based on large-scale pre-trained language models. It deeply integrates text understanding and speech generation, and supports real-time streaming synthesis from text to speech.
         */
        COSYVOICE_V3_PLUS("cosyvoice-v3-plus"),

        /**
         * qwen3-livetranslate-flash-realtime is a multi-language audio-video real-time translation model that can recognize 18 languages and translate audio in real time into 10 languages.
         */
        QWEN3_LIVETRANSLATE_FLASH_REALTIME("qwen3-livetranslate-flash-realtime"),

        /**
         * Tongyi Qianwen Audio is an audio understanding model that can process various types of audio (human speech, natural sounds, music, singing) and text as input, and output text. This model not only transcribes the input audio but also possesses deeper semantic understanding, sentiment analysis, audio event detection, voice chat, and other capabilities.
         */
        QWEN_AUDIO_TURBO("qwen-audio-turbo");

        public final String value;

        AudioModel(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    /**
     * <a href="https://help.aliyun.com/zh/model-studio/embedding">Embedding Models</a>
     */
    public enum EmbeddingModel {

        QWEN_2_5_VL_EMBEDDING("qwen2.5-vl-embedding"),

        TONGYI_EMBEDDING_VISION_PLUS("tongyi-embedding-vision-plus"),

        /**
         * DIMENSION: 1536
         */
        EMBEDDING_V1("text-embedding-v1"),

        /**
         * DIMENSION: 1536
         */
        EMBEDDING_V2("text-embedding-v2"),

        /**
         * 1,024(Default)、768、512、256、128 or 64
         */
        EMBEDDING_V3("text-embedding-v3"),

        /**
         * 2,048、1,536、1,024(Default)、768、512、256、128 or 64
         */
        EMBEDDING_V4("text-embedding-v4");

        public final String value;

        EmbeddingModel(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

    }

    public enum EmbeddingTextType {

        QUERY("query"),

        DOCUMENT("document");

        public final String value;

        EmbeddingTextType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

    }

    public enum ImageModel {

        QWEN_IMAGE_PLUS("qwen-image-plus"),

        QWEN_IMAGE("qwen-image"),

        QWEN_IMAGE_EDIT("qwen-image-edit"),

        QWEN_MT_IMAGE("qwen-mt-image"),

        WAN_2_2_T_2_I_PLUS("wan2.2-t2i-plus"),

        WAN_2_2_T_2_I_FLASH("wan2.2-t2i-flash"),

        WANX_2_1_IMAGEEDIT("wanx2.1-imageedit"),

        WAN_2_5_I_2_I_PREVIEW("wan2.2-t2i-preview");

        public final String value;

        ImageModel(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

    }

    /**
     * https://help.aliyun.com/zh/model-studio/text-to-video-api-reference
     */
    public enum VideoModel implements ModelDescription {

        /**
         * Text to Video, faster generation speed and balanced performance.
         */
        WANX2_1_T2V_TURBO("wanx2.1-t2v-turbo"),

        WANX2_5_T2V_PREVIEW("wan2.5-t2v-preview"),

        /**
         * Text to Video, The generated details are richer and the picture is more
         * textured.
         */
        WANX2_1_T2V_PLUS("wanx2.1-t2v-plus"),

        /**
         * Picture-generated video, based on the first frame. The generation speed is
         * faster, taking only one-third of the plus model, and it has a higher
         * cost-effectiveness.
         */
        WANX2_1_I2V_TURBO("wanx2.1-i2v-turbo"),

        /**
         * Picture-generated video, The generated details are richer and the picture is
         * more textured.
         */
        WANX2_1_I2V_PLUS("wanx2.1-i2v-plus"),

        /**
         * Generate video based on the beginning and end frames
         */
        WANX2_1_KF2V_PLUS("wanx2.1-kf2v-plus");

        public final String value;

        VideoModel(String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }

        @Override
        public String getName() {
            return this.value;
        }

    }

}
