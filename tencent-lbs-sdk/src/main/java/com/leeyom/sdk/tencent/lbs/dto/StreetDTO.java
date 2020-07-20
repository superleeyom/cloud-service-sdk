package com.leeyom.sdk.tencent.lbs.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class StreetDTO {
    /**
     * id : 12667939243515887255
     * title : 大木仓胡同
     * location : {"lat":39.911655,"lng":116.370163}
     * _distance : 42.6
     * _dir_desc : 南
     */

    private String id;
    private String title;
    private LocationDTO location;
    private double _distance;
    private String _dir_desc;
}
