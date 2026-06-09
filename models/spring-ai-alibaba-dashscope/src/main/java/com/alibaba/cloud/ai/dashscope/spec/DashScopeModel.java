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

import org.jspecify.annotations.NonNull;
import org.springframework.ai.model.ChatModelDescription;

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

        QWEN_PLUS("qwen-plus"),
        QWEN_MAX("qwen-max"),
        QWEN37_MAX("qwen3.7-max"),
        QWEN36_PLUS("qwen3.6-plus"),
        QWEN36_FLASH("qwen3.6-flash"),
        DEEPSEEK_V4_PRO("deepseek-v4-pro"),
        DEEPSEEK_V4_FLASH("deepseek-v4-flash"),
        GLM_51("glm-5.1"),
        KIMI_K26("kimi-k2.6"),
        MINIMAX_M25("MinMax-M2.5"),
        MIMO_V25_PRO("mimo-v2.5-pro"),
        QWEN_DOC_TURBO("qwen-doc-turbo"),

        QWEN_VL_PLUS("qwen-vl-plus"),
        QWEN3_VL_PLUS("qwen3-vl-plus"),
        QWEN_VL_MAX("qwen-vl-max"),
        QWEN_AUDIO_TURBO("qwen-audio-turbo"),
;
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
        // =============== COSY VOICE Model ===============
		COSYVOICE_V1("cosyvoice-v1"),
        COSYVOICE_V2("cosyvoice-v2"),
		COSYVOICE_V3_FLASH("cosyvoice-v3-flash"),
		COSYVOICE_V3_PLUS("cosyvoice-v3-plus"),
        // =============== COSY VOICE Model ===============

        // =============== SAMBERT Model ===============
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
        // =============== SAMBERT Model ===============

        // =============== TTS Model ===============
        QWEN3_TTS_FLASH("qwen3-tts-flash"),
        QWEN3_TTS_FLASH_2025_11_27("qwen3-tts-flash-2025-11-27"),
        QWEN3_TTS_FLASH_2025_09_18("qwen3-tts-flash-2025-09-18"),
        QWEN_TTS("qwen-tts"),
        QWEN_TTS_LATEST("qwen-tts-latest"),
        QWEN_TTS_2025_05_22("qwen-tts-2025-05-22"),
        QWEN_TTS_2025_04_10("qwen-tts-2025-04-10"),
        QWEN3_TTS_FLASH_REALTIME("qwen3-tts-flash-realtime"),
        // =============== TTS Model ===============

        // =============== Transcription Model ===============
        FUN_ASR_REALTIME("fun-asr-realtime"),
        GUMMY_REALTIME_V1("gummy-realtime-v1"),
        GUMMY_CHAT_V1("gummy-chat-v1"),
        PARAFORMER_REALTIME_V2("paraformer-realtime-v2"),
        PARAFORMER_REALTIME_V1("paraformer-realtime-v1"),
        PARAFORMER_REALTIME_8K_V1("paraformer-realtime-8k-v1"),
        PARAFORMER_REALTIME_8K_V2("paraformer-realtime-8k-v2"),

        QWEN3_LIVETRANSLATE_FLASH("qwen3-livetranslate-flash"),
        QWEN3_LIVETRANSLATE_FLASH_2025_12_01("qwen3-livetranslate-flash-2025-12-01"),
        // =============== Transcription Model ===============

        // =============== 录音文件识别 Model ===============
        PARAFORMER_V2("paraformer-v2"),
        PARAFORMER_V1("paraformer-v1"),
        PARAFORMER_8K_V2("paraformer-8k-v2"),
        PARAFORMER_8K_V1("paraformer-8k-v1"),
        PARAFORMER_MTL_V1("paraformer-mtl-v1"),
        FUN_ASR("fun-asr"),
        FUN_ASR_2025_11_07("fun-asr-2025-11-07"),
        FUN_ASR_2025_08_25("fun-asr-2025-08-25"),
        FUN_ASR_MTL("fun-asr-mtl"),
        FUN_ASR_MTL_2025_08_25("fun-asr-mtl-2025-08-25"),
        SPEECH_BIASING("speech-biasing"),
        // =============== 录音文件识别 Model ===============

        // =============== 千问ASR Model ===============
        QWEN3_ASR_FLASH_FILETRANS("qwen3-asr-flash-filetrans"),
        QWEN3_ASR_FLASH("qwen3-asr-flash"),
        QWEN3_ASR_FLASH_US("qwen3-asr-flash-us"),
        QWEN3_ASR_FLASH_FILETANS("qwen3-asr-flash-filetrans"),
        // =============== 千问ASR Model ===============


        ;

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

	public enum MultimodalEmbeddingModel {

		QWEN3_VL_EMBEDDING("qwen3-vl-embedding"),

		QWEN2_5_VL_EMBEDDING("qwen2.5-vl-embedding"),

		TONGYI_EMBEDDING_VISION_PLUS("tongyi-embedding-vision-plus"),

		TONGYI_EMBEDDING_VISION_FLASH("tongyi-embedding-vision-flash"),

		MULTIMODAL_EMBEDDING_V1("multimodal-embedding-v1");

		public final String value;

		MultimodalEmbeddingModel(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}
	}

    public enum ImageModel {

        // =============== Qwen Image Model ===============
        QWEN_IMAGE_2_0_PRO("qwen-image-2.0-pro"),
        QWEN_IMAGE_2_0("qwen-image-2.0"),
        QWEN_IMAGE_MAX("qwen-image-max"),
        QWEN_IMAGE_PLUS("qwen-image-plus"),
        QWEN_IMAGE("qwen-image"),
        QWEN_IMAGE_EDIT_MAX("qwen-image-edit-max"),
        QWEN_IMAGE_EDIT_PLUS("qwen-image-edit-plus"),
        QWEN_IMAGE_EDIT("qwen-image-edit"),
        QWEN_MT_IMAGE("qwen-mt-image"),

        // =============== Wan Image Model ===============
        WAN_2_6_T2I("wan2.6-t2i"),
        WAN_2_5_T2I_PREVIEW("wan2.5-t2i-preview"),
        WAN_2_2_T2I_PLUS("wan2.2-t2i-plus"),
        WAN_2_2_T2I_FLASH("wan2.2-t2i-flash"),
        WANX_2_1_T2I_TURBO("wanx2.1-t2i-turbo"),
        WANX_2_1_T2I_PLUS("wanx2.1-t2i-plus"),
        WANX_2_0_T2I_TURBO("wanx2.0-t2i-turbo"),
        WAN_2_7_IMAGE_PRO("wan2.7-image-pro"),
        WAN_2_7_IMAGE("wan2.7-image"),
        WAN_2_6_IMAGE("wan2.6-image"),
        WAN_2_5_I2I_PREVIEW("wan2.5-i2i-preview"),
        WANX_2_1_IMAGEEDIT("wanx2.1-imageedit"),
        WANX_V1("wanx-v1"),
        WANX_SKETCH_TO_IMAGE_LITE("wanx-sketch-to-image-lite"),
        WANX_X_PAINTING("wanx-x-painting"),

        // =============== Z-Image Image Generation ===============
        Z_IMAGE_TURBO("z-image-turbo"),

        // =============== KLing Image Generation ===============
        KLING_V3_IMAGE_GENERATION("kling/kling-v3-image-generation"),
        KLING_V3_OMNI_IMAGE_GENERATION("kling/kling-v3-omni-image-generation"),

        // =============== Style Repaint ===============
        WANX_STYLE_REPAINT_V1("wanx-style-repaint-v1"),

        // =============== Image Expansion ===============
        IMAGE_OUT_PAINTING("image-out-painting"),

        // =============== Virtual Model ===============
        WANX_VIRTUALMODEL("wanx-virtualmodel"),
        VIRTUALMODEL_V2("virtualmodel-v2"),
        SHOEMODEL_V1("shoemodel-v1"),

        // =============== Poster Generation ===============
        WANX_POSTER_GENERATION_V1("wanx-poster-generation-v1"),

        // =============== Instance Segmentation ===============
        IMAGE_INSTANCE_SEGMENTATION("image-instance-segmentation"),

        // =============== Background Generation ===============
        WANX_BACKGROUND_GENERATION_V2("wanx-background-generation-v2"),

        // =============== Image Erase Completion ===============
        IMAGE_ERASE_COMPLETION("image-erase-completion"),

        // =============== AI TryOn ===============
        AITRYON("aitryon"),
        AITRYON_PLUS("aitryon-plus"),
        AITRYON_REFINER("aitryon-refiner"),
        AITRYON_PARSING_V1("aitryon-parsing-v1"),

        // =============== FaceChain ===============
        FACECHAIN_FACEDETECT("facechain-facedetect"),
        FACECHAIN_FINETUNE("facechain-finetune"),
        FACECHAIN_GENERATION("facechain-generation"),

        // =============== WordArt ===============
        WORDART_SEMANTIC("wordart-semantic"),
        WORDART_TEXTURE("wordart-texture");

        public final String value;

        ImageModel(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

    }

    public enum VideoModel {

        WANX21_I2V_TURBO("wanx2.1-i2v-turbo"),
        WANX21_I2V_PLUS("wanx2.1-i2v-plus"),
        WANX22_I2V_PLUS("wan2.2-i2v-plus"),
        WAN22_I2V_FLASH("wan2.2-i2v-flash"),
        WAN25_I2V_PREVIEW("wan2.5-i2v-preview"),
        WAN26_I2V_FLASH("wan2.6-i2v-flash"),
        WAN26_I2V("wan2.6-i2v"),
        WAN26_R2V("wan2.6-r2v"),
        WANX21_T2V_PLUS("wanx2.1-t2v-plus"),
        WANX21_T2V_TURBO("wanx2.1-t2v-turbo"),
        WAN22_T2V_PLUS("wan2.2-t2v-plus"),
        WAN25_T2V_PREVIEW("wan2.5-t2v-preview"),
        WAN26_T2V("wan2.6-t2v"),
        WANX21_VACE_PLUS("wanx2.1-vace-plus"),
        VIDEO_STYLE_TRANSFORM("video-style-transform"),
        HAPPYHORSE_1_0_T2V("happyhorse-1.0-t2v"),
        HAPPYHORSE_1_0_I2V("happyhorse-1.0-i2v"),
        HAPPYHORSE_1_0_R2V("happyhorse-1.0-r2v"),
        HAPPYHORSE_1_0_VIDEO_EDIT("happyhorse-1.0-video-edit"),
        PIXVERSE_PIXVERSE_C1_T2V("pixverse/pixverse-c1-t2v"),
        PIXVERSE_PIXVERSE_V6_T2V("pixverse/pixverse-v6-t2v"),
        PIXVERSE_PIXVERSE_C1_IT2V("pixverse/pixverse-c1-it2v"),
        PIXVERSE_PIXVERSE_V6_IT2V("pixverse/pixverse-v6-it2v"),
        PIXVERSE_PIXVERSE_C1_KF2V("pixverse/pixverse-c1-kf2v"),
        PIXVERSE_PIXVERSE_C1_R2V("pixverse/pixverse-c1-r2v"),
        KLING_V3_VIDEO_GENERATION("kling/kling-v3-video-generation"),
        KLING_V3_OMNI_VIDEO_GENERATION("kling/kling-v3-omni-video-generation"),
        VIDUG3_TURBO_TEXT2VIDEO("vidu/viduq3-turbo_text2video"),
        VIDUG3_PRO_IMG2VIDEO("vidu/viduq3-pro_img2video"),
        VIDUG3_TURBO_START_END2VIDEO("vidu/viduq3-turbo_start-end2video"),
        VIDUG3_MIX_REFERENCE2VIDEO("vidu/viduq3-mix_reference2video"),
        VIDUQ2_PRO_REFERENCE2VIDEO("vidu/viduq2-pro_reference2video"),

        WANX21_KF2V_PLUS("wanx2.1-kf2v-plus"),
        WAN22_KF2V_FLASH("wan2.2-kf2v-flash"),
        WAN22_ANIMATE_MOVE("wan2.2-animate-move"),
        WAN22_ANIMATE_MIX("wan2.2-animate-mix"),
        WAN22_S2V("wan2.2-s2v"),
        ANIMATE_ANYONE_GEN2("animate-anyone-gen2"),
        EMO_V1("emo-v1"),
        LIVEPORTRAIT("liveportrait"),
        VIDEORETALK("videoretalk"),
        EMOJI_V1("emoji-v1"),

        WAN22_S2V_DETECT("wan2.2-s2v-detect"),
        EMO_DETECT_V1("emo-detect-v1"),
        LIVEPORTRAIT_DETECT("liveportrait-detect"),
        EMOJI_DETECT_V1("emoji-detect-v1"),

        ANIMATE_ANYONE_DETECT_GEN2("animate-anyone-detect-gen2"),

        ANIMATE_ANYONE_TEMPLATE_GEN2("animate-anyone-template-gen2");

        public String value;

        VideoModel(String value) {
            this.value = value;
        }

        @NonNull
        public String getName() {
            return value;
        }
    }

}
