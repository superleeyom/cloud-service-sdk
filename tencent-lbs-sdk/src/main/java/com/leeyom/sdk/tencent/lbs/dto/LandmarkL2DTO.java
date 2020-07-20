package com.leeyom.sdk.tencent.lbs.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class LandmarkL2DTO {
    /**
     * id : 15380476883213903976
     * title : 大悦城写字楼
     * location : {"lat":39.910885,"lng":116.372932}
     * _distance : 0
     * _dir_desc : 内
     */

    private String id;
    private String title;
    private LocationDTO location;
    private int _distance;
    private String _dir_desc;
}
