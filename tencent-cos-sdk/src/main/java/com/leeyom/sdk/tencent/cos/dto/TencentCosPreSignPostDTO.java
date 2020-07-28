package com.leeyom.sdk.tencent.cos.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * 腾讯云cos post方式上传文件
 *
 * @author leeyom
 */
@Builder
@Data
public class TencentCosPreSignPostDTO {


    /**
     * 前端直传表单的body字段值
     */
    Map<String, String> formFields;

    /**
     * 前端直传的请求的host
     */
    private String preSignUrl;

    /**
     * 上传后的文件访问url
     */
    private String fileUrl;


}
