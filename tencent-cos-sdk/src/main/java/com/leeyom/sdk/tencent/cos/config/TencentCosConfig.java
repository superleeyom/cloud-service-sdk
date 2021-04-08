package com.leeyom.sdk.tencent.cos.config;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.region.Region;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

/**
 * 腾讯云对象存储客户端配置
 *
 * @author Leeyom Wang
 * @date 2021/4/8 4:07 下午
 */
@Configuration
@DependsOn({"tencentCosProperties"})
public class TencentCosConfig {

    @Autowired
    TencentCosProperties tencentCosProperties;

    @Bean
    public COSClient cosClient() {
        // 1 初始化用户身份信息
        COSCredentials cred = new BasicCOSCredentials(tencentCosProperties.getSecretId(), tencentCosProperties.getSecretKey());
        // 2 设置bucket的区域
        ClientConfig clientConfig = new ClientConfig(new Region(tencentCosProperties.getRegion()));
        // 3 生成cos客户端
        return new COSClient(cred, clientConfig);
    }

}
