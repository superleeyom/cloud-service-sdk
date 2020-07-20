package com.leeyom.sdk.tencent.cos.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Data
@Component
@PropertySource("classpath:tencent-cos.properties")
@ConfigurationProperties(prefix = "tencent.cos")
public class TencentCosProperties {

    /**
     * 腾讯cos key
     */
    private String secretId;

    /**
     * 腾讯cos秘钥
     */
    private String secretKey;

    /**
     * 腾讯cos桶
     */
    private String bucketName;

    /**
     * 腾讯cos区域
     */
    private String region;

    /**
     * 腾讯cos预签名有效时间
     */
    private long expiredTime;

    /**
     * 文件上传目录
     */
    private String dir;

    /**
     * 自定义域名
     */
    private String domain;

}
