package com.leeyom.sdk.aliyun.sms.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * 阿里云短信服务配置
 *
 * @author leeyom
 */
@Component
@PropertySource(value = "classpath:aliyun-sms.properties", encoding = "UTF-8")
@ConfigurationProperties(prefix = "ali.sms")
@Data
public class AliSmsProperties {

    /**
     * accessKeyId
     */
    private String accessKeyId;

    /**
     * 秘钥
     */
    private String accessKeySecret;

    /**
     * 区域
     */
    private String regionId;

    /**
     * 短信模板
     */
    private SmsTemplateCode templateCode;

    /**
     * 短信签名
     */
    private SmsSign sign;

}
