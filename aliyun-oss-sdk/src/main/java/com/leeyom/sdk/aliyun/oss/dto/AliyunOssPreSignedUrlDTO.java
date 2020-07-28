package com.leeyom.sdk.aliyun.oss.dto;

import lombok.Builder;
import lombok.Data;

/**
 * 阿里云oss预签名url
 *
 * @author leeyom
 */
@Builder
@Data
public class AliyunOssPreSignedUrlDTO {

    /**
     * 前端直传策略参数
     */
    private AliyunOssPolicy aliyunOssPolicy;

    /**
     * 文件访问url
     */
    private String fileUrl;

}
