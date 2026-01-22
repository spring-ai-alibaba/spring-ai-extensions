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

import com.alibaba.cloud.ai.dashscope.spec.DashScopeModel;

/**
 * @author yingzi
 * @since 2026/1/18
 */
public class DashScopeVideoApiConstants {

    static Map<String, String> model2Url = new HashMap<>();

    // 通义万相-图生视频-基于首帧；通义万相-参考生视频；通义万相-文生视频；通义万相-通用视频编辑；视频风格重绘
    public static final String VIDEO_GENERATION_SYNTHESIS = "/api/v1/services/aigc/video-generation/video-synthesis";

    static final Map<String, List<String>> videoGenerationSynthesis2Model = Map.of(VIDEO_GENERATION_SYNTHESIS, List.of(
            // 通义万相-图生视频-基于首帧
            DashScopeModel.VideoModel.WANX21_I2V_TURBO.getName(), DashScopeModel.VideoModel.WANX21_I2V_PLUS.getName(), DashScopeModel.VideoModel.WANX22_I2V_PLUS.getName(), DashScopeModel.VideoModel.WAN22_I2V_FLASH.getName(), DashScopeModel.VideoModel.WAN25_I2V_PREVIEW.getName(), DashScopeModel.VideoModel.WAN26_I2V_FLASH.getName(), DashScopeModel.VideoModel.WAN26_I2V.getName(),
            // 通义万相-参考生视频
            DashScopeModel.VideoModel.WAN26_R2V.getName(),
            // 通义万相-文生视频
            DashScopeModel.VideoModel.WANX21_T2V_PLUS.getName(), DashScopeModel.VideoModel.WANX21_T2V_TURBO.getName(), DashScopeModel.VideoModel.WANX22_T2V_PLUS.getName(), DashScopeModel.VideoModel.WANX25_T2V_PREVIEW.getName(), DashScopeModel.VideoModel.WANX26_T2V.getName(),
            // 通义万相-通用视频编辑
            DashScopeModel.VideoModel.WANX21_VACE_PLUS.getName(),
            // 视频风格重绘
            DashScopeModel.VideoModel.VIDEO_STYLE_TRANSFORM.getName()));

    // 通义万相-图生视频-基于首尾帧；通义万相-图生动作；通义万相数字人-图像检测-视频生成；图声舞蹈视频-舞动人像-AnimateAnyone-视频生成；图生唱视频-悦动人像EMO-视频生成；VideoRetalk-视频生成
    public static final String IMAGE_2_VIDEO_SYNTHESIS = "/api/v1/services/aigc/image2video/video-synthesis";

    static final Map<String, List<String>> image2VideoSynthesis2Model = Map.of(IMAGE_2_VIDEO_SYNTHESIS, List.of(
            // 通义万相-图生视频-基于首尾帧
            DashScopeModel.VideoModel.WANX21_KF2V_PLUS.getName(),
            // 通义万相-图生动作
            DashScopeModel.VideoModel.WAN22_KF2V_FLASH.getName(),
            // 通义万相-视频换人
            DashScopeModel.VideoModel.WAN22_ANIMATE_MIX.getName(),
            // 通义万相数字人-图像检测-视频生成
            DashScopeModel.VideoModel.WAN22_S2V.getName(),
            // 图声舞蹈视频-舞动人像-AnimateAnyone-动作模版生成
            DashScopeModel.VideoModel.ANIMATE_ANYONE_GEN2.getName(),
            // 图生唱视频-悦动人像EMO-视频生成
            DashScopeModel.VideoModel.EMO_V1.getName(),
            // 图生播报视频-灵动人像-视频生成
            DashScopeModel.VideoModel.LIVEPORTRAIT.getName(),
            // VideoRetalk-视频生成
            DashScopeModel.VideoModel.VIDEORETALK.getName(),
            // 图声表情包视频-表情包Emoji-视频生成
            DashScopeModel.VideoModel.EMOJI_V1.getName()));

    // 通义万相数字人-图像检测；图生唱视频-悦动人像EMO-图像检测；图生播报视频-灵动人像-图像检测；图声表情包视频-表情包Emoji-图像检测
    public static final String IMAGE_2_VIDEO_FACE_DETECT = "/api/v1/services/aigc/image2video/face-detect";

    static final Map<String, List<String>> image2VideoFaceDetect2Model = Map.of(IMAGE_2_VIDEO_FACE_DETECT, List.of(
            // 通义万相数字人
            DashScopeModel.VideoModel.WAN22_S2V_DETECT.getName(),
            // 图生唱视频-悦动人像EMO-图像检测
            DashScopeModel.VideoModel.EMO_DETECT_V1.getName(),
            // 图生播报视频-灵动人像-图像检测
            DashScopeModel.VideoModel.LIVEPORTRAIT_DETECT.getName(),
            // 图声表情包视频-表情包Emoji-图像检测
            DashScopeModel.VideoModel.EMOJI_DETECT_V1.getName()));

    // 图声舞蹈视频-舞动人像-AnimateAnyone-图像检测
    public static final String IMAGE_2_VIDEO_AA_DETECT = "/api/v1/services/aigc/image2video/aa-detect";

    static final Map<String, List<String>> image2VideoFaceAa2Model = Map.of(IMAGE_2_VIDEO_AA_DETECT, List.of(
            // AnimateAnyone人像检测
            DashScopeModel.VideoModel.ANIMATE_ANYONE_DETECT_GEN2.getName()));

    // 图声舞蹈视频-舞动人像-AnimateAnyone-动作模版生成
    public static final String IMAGE_2_VIDEO_AA_TEMPLATE_GENERATION = "/api/v1/services/aigc/image2video/aa-template-generation";

    static final Map<String, List<String>> image2VideoAaTemplateGeneration2Model = Map.of(IMAGE_2_VIDEO_AA_TEMPLATE_GENERATION, List.of(
            // AnimateAnyone动作模板生成
            DashScopeModel.VideoModel.ANIMATE_ANYONE_TEMPLATE_GEN2.getName()));

    static {
        // 从 url -> models 映射自动生成 model -> url 的反向映射
        registerModelsFromMap(videoGenerationSynthesis2Model);
        registerModelsFromMap(image2VideoSynthesis2Model);
        registerModelsFromMap(image2VideoFaceDetect2Model);
        registerModelsFromMap(image2VideoFaceAa2Model);
        registerModelsFromMap(image2VideoAaTemplateGeneration2Model);
    }

    /**
     * 从 URL -> 模型列表 的映射中注册模型到 URL 的反向映射
     *
     * @param urlToModelsMap URL 到模型列表的映射
     */
    private static void registerModelsFromMap(Map<String, List<String>> urlToModelsMap) {
        for (Map.Entry<String, List<String>> entry : urlToModelsMap.entrySet()) {
            String url = entry.getKey();
            List<String> modelNames = entry.getValue();
            for (String modelName : modelNames) {
                model2Url.put(modelName, url);
            }
        }
    }

    /**
     * 根据模型名称获取对应的 API 路径
     *
     * @param modelName 模型名称
     *
     * @return API 路径，如果模型不存在则返回 null
     */
    public static String getPathByModelName(String modelName) {
        return model2Url.get(modelName);
    }

    public static boolean isDetect(String modelName) {
        return image2VideoFaceAa2Model.values().stream().anyMatch(modelList -> modelList.contains(modelName))
                || image2VideoFaceDetect2Model.values().stream().anyMatch(modelList -> modelList.contains(modelName));
    }


}
