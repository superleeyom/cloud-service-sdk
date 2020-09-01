package com.leeyom.sdk.tencent.lbs.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DistanceModeEnum {

    /**
     * 驾车
     */
    DRIVING("driving", "驾车"),

    /**
     * 步行
     */
    WALKING("walking", "步行");

    /**
     * 计算方式
     */
    private String mode;

    /**
     * 描述
     */
    private String desc;

}
