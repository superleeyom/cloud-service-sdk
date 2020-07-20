package com.leeyom.sdk.tencent.cos.dto;

import lombok.Builder;
import lombok.Data;

/**
 * 腾讯云cos预签名地址
 *
 * @author leeyom
 */
@Data
@Builder
public class TencentCosPreSignPutDTO {

    /**
     * 腾讯云cos预签名地址
     */
    private String preSignUrl;

    /**
     * 上传后的文件访问url
     */
    private String fileUrl;

}
