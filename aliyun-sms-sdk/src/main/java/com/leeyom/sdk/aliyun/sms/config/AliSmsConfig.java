package com.leeyom.sdk.aliyun.sms.config;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.profile.DefaultProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

/**
 * 阿里短信客户端配置
 *
 * @author Leeyom Wang
 * @date 2021/4/8 4:21 下午
 */
@Configuration
@DependsOn({"aliSmsProperties"})
public class AliSmsConfig {

    @Autowired
    AliSmsProperties aliSmsProperties;

    @Bean
    public IAcsClient client(){
        DefaultProfile profile = DefaultProfile.getProfile(aliSmsProperties.getRegionId(),
                aliSmsProperties.getAccessKeyId(), aliSmsProperties.getAccessKeySecret());
        return new DefaultAcsClient(profile);
    }
}
