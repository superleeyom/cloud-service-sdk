package com.leeyom.sdk.aliyun.oss.dto;

import lombok.Data;

/**
 * OSS上传策略
 * <p>
 * 这里需要注意下，实际前端直传的时候，请求的content-type为form-data，请求方式为post
 * 请求的参数如下body格式如下：
 * <p>
 * {
 * "OSSAccessKeyId": "xxx",
 * "policy":"xxx",
 * "Signature":"xxx",
 * "key":"xxx",
 * "file":(binary)
 * }
 * <p>
 * 注意，其中file必须是表单中的最后一个域，否则是会上传失败
 * 具体参数，可以参考：https://help.aliyun.com/document_detail/31988.html?spm=a2c4g.11186623.6.1497.59c06611TqgjXe
 * <p>
 *
 * @author leeyom
 */
@Data
public class AliyunOssPolicy {

    /**
     * 访问身份验证中用到用户标识
     */
    private String accessKeyId;
    /**
     * 用户表单上传的策略,经过base64编码过的字符串
     */
    private String policy;
    /**
     * 对policy签名后的字符串
     */
    private String signature;
    /**
     * oss对外服务的访问域名，前端用此host进行上传
     */
    private String host;
    /**
     * 上传的objectName
     */
    private String key;

}
