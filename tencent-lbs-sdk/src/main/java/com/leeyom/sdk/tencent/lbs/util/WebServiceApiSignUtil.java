package com.leeyom.sdk.tencent.lbs.util;


import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 腾讯地图接口签名工具类
 *
 * @author leeyom
 */
public class WebServiceApiSignUtil {

    /**
     * WebServiceAPI GET 方法签名计算
     *
     * @param uri       请求的uri
     * @param appSecret 签名密钥
     * @param appKey    腾讯地图开发密钥
     * @param param     参数信息
     * @return 签名
     */
    public static String signGetRequest(String uri, String appSecret, String appKey, Map<String, Object> param) {
        return SecureUtil.md5(buildQueryStr(uri, appSecret, appKey, param));
    }

    private static String buildQueryStr(String uri, String appSecret, String appKey, Map<String, Object> param) {
        StringBuilder originData = new StringBuilder();
        originData.append(uri).append("?");
        param.put("key", appKey);
        // 按参数名升序
        List<String> keyList = new ArrayList<>(param.keySet());
        Collections.sort(keyList);
        for (String key : keyList) {
            originData.append(key).append("=").append(param.get(key)).append("&");
        }
        return StrUtil.removeSuffix(originData, "&") + appSecret;
    }

}
