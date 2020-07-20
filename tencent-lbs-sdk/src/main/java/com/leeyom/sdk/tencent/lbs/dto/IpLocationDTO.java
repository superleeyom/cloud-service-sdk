package com.leeyom.sdk.tencent.lbs.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IpLocationDTO {
    /**
     * ip : 202.106.0.30
     * location : {"lng":116.407526,"lat":39.90403}
     * ad_info : {"nation":"中国","province":"","city":"","adcode":110000}
     */

    private String ip;
    private LocationBean location;
    private AdInfoBean ad_info;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class LocationBean {
        /**
         * lng : 116.407526
         * lat : 39.90403
         */

        private double lng;
        private double lat;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class AdInfoBean {
        /**
         * nation : 中国
         * province :
         * city :
         * adcode : 110000
         */

        private String nation;
        private String province;
        private String city;
        private String district;
        private int adcode;
    }

}
