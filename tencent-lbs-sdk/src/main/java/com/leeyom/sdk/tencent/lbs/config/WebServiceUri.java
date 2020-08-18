package com.leeyom.sdk.tencent.lbs.config;

public interface WebServiceUri {

    /**
     * 地址解析（正逆解析）
     */
    String GEOCODER_API = "/ws/geocoder/v1";

    /**
     * 计算距离（一对多）
     */
    String DISTANCE_API = "/ws/distance/v1";

    /**
     * IP定位
     */
    String IP_API = "/ws/location/v1/ip";

    /**
     * 坐标转换
     */
    String TRANSLATE = "/ws/coord/v1/translate";

    /**
     * 距离矩阵
     */
    String DISTANCE_MATRIX = "/ws/distance/v1/matrix";
}
