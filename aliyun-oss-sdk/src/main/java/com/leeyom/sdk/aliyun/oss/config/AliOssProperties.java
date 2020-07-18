package com.leeyom.sdk.aliyun.oss.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * 阿里云OSS配置
 *
 * @author leeyom
 */
@Data
@Component
@PropertySource("classpath:aliyun-oss.properties")
@ConfigurationProperties(prefix = "ali.oss")
public class AliOssProperties {

    /**
     * endpoint
     */
    private String endpoint;

    /**
     * accessKeyId
     */
    private String accessKeyId;

    /**
     * 秘钥
     */
    private String accessKeySecret;

    /**
     * bucketName
     */
    private String bucketName;

    /**
     * 上传文件目录
     */
    private String fileDir;

}
