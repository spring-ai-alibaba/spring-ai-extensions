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

import org.springframework.ai.model.ChatModelDescription;
import org.springframework.ai.model.ModelDescription;

/**
 * @author yuluo
 * @author <a href="mailto:yuluo08290126@gmail.com">yuluo</a>
 */

public class DashScopeModel {

    /**
     * Spring AI Alibaba DashScope implements all models that support the dashscope
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
		 * Tongyi Qianwen Code Model. The latest Qwen3-Coder-Plus series models are code generation models based on Qwen3,
		 * featuring powerful Coding Agent capabilities. They excel at tool invocation and environment interaction,
		 * enabling autonomous programming. Their code capabilities are outstanding while also possessing general capabilities.
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
		 * Tongyi Qianwen 3-Omni-Flash multimodal large model, based on Thinker-Talker Mixture of Experts (MoE) architecture,
		 * supports efficient understanding of text, images, audio, video and speech generation capability,
		 * and can perform text interaction in 119 languages and speech interaction in 20 languages to generate
		 * human-like speech for accurate cross-language communication. The model has powerful command following
		 * and system prompt customization functions, flexibly adapts dialogue style and role setting,
		 * and is widely used in text creation, voice assistant, multimedia analysis and other scenes to provide a
		 * natural and smooth multi-modal interaction experience.
		 */
		QWEN3_OMNI_FLASH("qwen3-omni-flash"),

		/**
		 * The Qwen-Omni model can receive combined inputs of various modalities such as text,
		 * images, audio, and video, and generate responses in text or voice forms.
		 * It offers multiple anthropomorphic voices and supports voice output in multiple languages and dialects.
		 * It can be applied in scenarios such as text creation, visual recognition, and voice assistants.
		 */
		QWEN_OMNI_FLASH("qwen-omni-flash"),

		/**
		 * Tongyi Qianwen new multi-modal understanding generation model, supports text, image, voice, video input
		 * understanding and mixed input understanding, with text and voice simultaneous streaming generation ability,
		 * multi-modal content understanding speed significantly improved, provides 4 natural dialogue tones,
		 * this version is a dynamic update version.
		 */
		QWEN_OMNI_TURBO_LATEST("qwen-omni-turbo-latest"),

		/**
		 * New multi-modal understanding generation large model trained based on Qwen2.5, supports text, image,
		 * voice, and video input understanding as well as mixed input understanding, with text and voice simultaneous streaming
		 * generation capabilities. Multi-modal content understanding speed is significantly improved,
		 * providing 4 natural dialogue tones.
		 */
		QWEN2_5_OMNI_7B("qwen2.5-omni-7b"),

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
		 * The Tongyi Qianwen OCR model is a model specifically designed for text extraction. Compared to the
		 * Tongyi Qianwen VL model, it focuses more on the text extraction capabilities for types of images such as
		 * documents, tables, test questions, and handwritten text. It can recognize multiple languages, including
		 * English, French, Japanese, Korean, German, Russian, and Italian, etc.
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

	/**
	 * <a href="https://help.aliyun.com/zh/model-studio/cosyvoice-java-sdk#95303fd00f0ge">Audio Models</a>
	 */
	public enum AudioModel {
		// =============== TTS Model ===============
		COSYVOICE_V1("cosyvoice-v1"),

		COSYVOICE_V2("cosyvoice-v2"),

		COSYVOICE_V3("cosyvoice-v3"),

		COSYVOICE_V3_PLUS("cosyvoice-v3-plus"),

		SAMBERT_ZHINAN_V1("sambert-zhinan-v1"),

		SAMBERT_ZHIQI_V1("sambert-zhiqi-v1"),

		SAMBERT_ZHICHU_V1("sambert-zhichu-v1"),

		SAMBERT_ZHIDE_V1("sambert-zhide-v1"),

		SAMBERT_ZHIJIA_V1("sambert-zhijia-v1"),

		SAMBERT_ZHIRU_V1("sambert-zhiru-v1"),

		SAMBERT_ZHIQIAN_V1("sambert-zhiqian-v1"),

		SAMBERT_ZHIXIANG_V1("sambert-zhixiang-v1"),

		SAMBERT_ZHIWEI_V1("sambert-zhiwei-v1"),

		SAMBERT_ZHIHAO_V1("sambert-zhihao-v1"),

		SAMBERT_ZHIJING_V1("sambert-zhijing-v1"),

		SAMBERT_ZHIMING_V1("sambert-zhiming-v1"),

		SAMBERT_ZHIMO_V1("sambert-zhimo-v1"),

		SAMBERT_ZHINA_V1("sambert-zhina-v1"),

		SAMBERT_ZHISHU_V1("sambert-zhishu-v1"),

		SAMBERT_ZHISTELLA_V1("sambert-zhistella-v1"),

		SAMBERT_ZHITING_V1("sambert-zhiting-v1"),

		SAMBERT_ZHIXIAO_V1("sambert-zhixiao-v1"),

		SAMBERT_ZHIYA_V1("sambert-zhiya-v1"),

		SAMBERT_ZHIYE_V1("sambert-zhiye-v1"),

		SAMBERT_ZHIYING_V1("sambert-zhiying-v1"),

		SAMBERT_ZHIYUAN_V1("sambert-zhiyuan-v1"),

		SAMBERT_ZHIYUE_V1("sambert-zhiyue-v1"),

		SAMBERT_ZHIGUI_V1("sambert-zhigui-v1"),

		SAMBERT_ZHISHUO_V1("sambert-zhishuo-v1"),

		SAMBERT_ZHIMIAO_EMO_V1("sambert-zhimiao-emo-v1"),

		SAMBERT_ZHIMAO_V1("sambert-zhimao-v1"),

		SAMBERT_ZHILUN_V1("sambert-zhilun-v1"),

		SAMBERT_ZHIFEI_V1("sambert-zhifei-v1"),

		SAMBERT_ZHIDA_V1("sambert-zhida-v1"),

		SAMBERT_CAMILA_V1("sambert-camila-v1"),

		SAMBERT_PERLA_V1("sambert-perla-v1"),

		SAMBERT_INDAH_V1("sambert-indah-v1"),

		SAMBERT_CLARA_V1("sambert-clara-v1"),

		SAMBERT_HANNA_V1("sambert-hanna-v1"),

		SAMBERT_BETH_V1("sambert-beth-v1"),

		SAMBERT_BETTY_V1("sambert-betty-v1"),

		SAMBERT_CALLY_V1("sambert-cally-v1"),

		SAMBERT_CINDY_V1("sambert-cindy-v1"),

		SAMBERT_EVA_V1("sambert-eva-v1"),

		SAMBERT_DONNA_V1("sambert-donna-v1"),

		SAMBERT_BRIAN_V1("sambert-brian-v1"),

		SAMBERT_WAAN_V1("sambert-waan-v1"),

        // =============== Transcription Model ===============
		PARAFORMER_REALTIME_V1("paraformer-realtime-v1"),

		PARAFORMER_REALTIME_8K_V1("paraformer-realtime-8k-v1"),

		PARAFORMER_REALTIME_V2("paraformer-realtime-v2"),

		PARAFORMER_REALTIME_8K_V2("paraformer-realtime-8k-v2"),

        PARAFORMER_V1("paraformer-v1"),

        PARAFORMER_8K_V1("paraformer-8k-v1"),

		PARAFORMER_V2("paraformer-v2"),

		PARAFORMER_8K_V2("paraformer-8k-v2"),

        PARAFORMER_MTL_V1("paraformer-mtl-v1"),

        FUN_ASR_REALTIME("fun-asr-realtime"),

		FUN_ASR("fun-asr"),

		FUN_ASR_MTL("fun-asr-mtl"),

        GUMMY_REALTIME_V1("gummy-realtime-v1"),

        GUMMY_CHAT_V1("gummy-chat-v1");

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
     * <a href="https://help.aliyun.com/zh/model-studio/text-to-video-api-reference">Video Models</a>
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
