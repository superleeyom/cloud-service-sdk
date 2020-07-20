package com.leeyom.sdk.tencent.lbs.dto;

import lombok.Data;

@Data
public class DistanceDTO {

    private LocationDTO from;

    private LocationDTO to;

    /**
     * 起点到终点的距离，单位：米，
     * 如果radius半径过小或者无法搜索到，则返回-1
     */
    private double distance;

    /**
     * 表示从起点到终点的结合路况的时间，秒为单位
     * 注：步行方式不计算耗时，该值始终为0
     */
    private int duration;

}
