package com.leeyom.sdk.tencent.lbs.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class CrossroadDTO {
    /**
     * id : 545908
     * title : 西单北大街/大木仓胡同(路口)
     * location : {"lat":39.911652,"lng":116.373871}
     * _distance : 89.4
     * _dir_desc : 西南
     */

    private String id;
    private String title;
    private LocationDTO location;
    private double _distance;
    private String _dir_desc;
}
