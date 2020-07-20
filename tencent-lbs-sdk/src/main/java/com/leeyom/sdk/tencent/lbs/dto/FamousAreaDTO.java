package com.leeyom.sdk.tencent.lbs.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class FamousAreaDTO {
    /**
     * id : 14312873533493635930
     * title : 西单
     * location : {"lat":39.9095,"lng":116.373001}
     * _distance : 0
     * _dir_desc : 内
     */

    private String id;
    private String title;
    private LocationDTO location;
    private int _distance;
    private String _dir_desc;
}
