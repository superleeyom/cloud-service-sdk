package com.leeyom.sdk.tencent.lbs.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class StreetNumberDTO {
    /**
     * id : 17414105854100477457
     * title : 西单北大街130号
     * location : {"lat":39.911301,"lng":116.374519}
     * _distance : 3.3
     * _dir_desc :
     */

    private String id;
    private String title;
    private LocationDTO location;
    private double _distance;
    private String _dir_desc;
}
