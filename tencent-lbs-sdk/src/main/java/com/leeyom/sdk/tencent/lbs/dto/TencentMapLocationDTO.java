package com.leeyom.sdk.tencent.lbs.dto;

import lombok.Data;

@Data
public class TencentMapLocationDTO {

    /**
     * 具体参数含义见腾讯地图api文档：https://lbs.qq.com/webservice_v1/guide-geocoder.html
     */

    private String title;
    private LocationDTO location;
    private AdInfoDTO ad_info;
    private AddressComponentDTO address_components;
    private double similarity;
    private int deviation;
    private int reliability;
    private int level;

}
