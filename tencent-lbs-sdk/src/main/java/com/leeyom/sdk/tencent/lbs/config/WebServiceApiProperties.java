package com.leeyom.sdk.tencent.lbs.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Data
@Component
@PropertySource("classpath:tencent-lbs.properties")
@ConfigurationProperties(prefix = "tencent.map")
public class WebServiceApiProperties {

    /**
     * 腾讯地图开发key
     */
    private String appKey;

    /**
     * 腾讯地图密钥
     */
    private String appSecret;

    /**
     * 腾讯地图api接口域名
     */
    private String domain;

}
