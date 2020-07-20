package com.leeyom.sdk.tencent.lbs.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class TencentMapResultDTO {


    /**
     * 状态码，0为正常,
     * 310请求参数信息有误，
     * 311Key格式错误,
     * 306请求有护持信息请检查字符串,
     * 110请求来源未被授权
     */
    private int status;

    /**
     * 状态说明
     */
    private String message;

    /**
     * 本次请求的唯一标识
     */
    private String request_id;

    /**
     * 结果
     */
    private Object result;
}
