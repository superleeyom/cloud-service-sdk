package com.leeyom.sdk.tencent.lbs.dto;

import lombok.Data;

/**
 * 地址坐标转换请求参数
 *
 * @author leeyom
 */
@Data
public class TranslateRequestDTO {

    /**
     * 预转换的坐标，支持批量转换，
     * 格式：纬度前，经度后，纬度和经度之间用",“分隔，每组坐标之间使用”;"分隔；
     */
    private String locations;

    /**
     * 输入的locations的坐标类型
     * 可选值为[1,6]之间的整数，每个数字代表的类型说明：
     * 1 GPS坐标
     * 2 sogou经纬度
     * 3 baidu经纬度
     * 4 mapbar经纬度
     * 5 [默认]腾讯、google、高德坐标
     * 6 sogou墨卡托
     */
    private int type = 5;

}
