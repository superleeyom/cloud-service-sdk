package com.leeyom.sdk.tencent.lbs.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class TownDTO {
    /**
     * id : 110102011
     * title : 金融街街道
     * location : {"lat":39.923622,"lng":116.356262}
     * _distance : 0
     * _dir_desc : 内
     */

    private String id;
    private String title;
    private LocationDTO location;
    private int _distance;
    private String _dir_desc;
}
