package com.leeyom.sdk.tencent.lbs.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class AdInfoDTO {
    /**
     * nation_code : 156
     * adcode : 110102
     * city_code : 156110000
     * name : 中国,北京市,北京市,西城区
     * location : {"lat":39.870731,"lng":116.375}
     * nation : 中国
     * province : 北京市
     * city : 北京市
     * district : 西城区
     */

    private String nation_code;
    private String adcode;
    private String city_code;
    private String name;
    private LocationDTO location;
    private String nation;
    private String province;
    private String city;
    private String district;
}
