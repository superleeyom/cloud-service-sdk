package com.leeyom.sdk.aliyun.oss.config;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

/**
 * 阿里云oss客户端配置
 *
 * @author Leeyom Wang
 * @date 2021/4/8 4:16 下午
 */
@Configuration
@DependsOn({"aliOssProperties"})
public class AliOssConfig {

    @Autowired
    AliOssProperties aliOssProperties;

    @Bean
    public OSS ossClient(){
        return new OSSClientBuilder().build(aliOssProperties.getEndpoint(),
                aliOssProperties.getAccessKeyId(), aliOssProperties.getAccessKeySecret());
    }
}
