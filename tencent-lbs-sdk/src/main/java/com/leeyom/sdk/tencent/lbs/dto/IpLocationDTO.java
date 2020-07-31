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
    private LocationDTO location;
    private AdInfoDTO ad_info;

}
