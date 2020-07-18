package com.leeyom.sdk.aliyun.sms.util;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.leeyom.sdk.aliyun.sms.config.AliSmsProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 阿里短信工具类
 *
 * @author leeyom
 */
@Component
@Slf4j
public class AliSmsUtil {

    @Autowired
    private AliSmsProperties aliSmsProperties;
    private IAcsClient client;

    @PostConstruct
    public void init() {
        try {
            DefaultProfile profile = DefaultProfile.getProfile(aliSmsProperties.getRegionId(),
                    aliSmsProperties.getAccessKeyId(), aliSmsProperties.getAccessKeySecret());
            client = new DefaultAcsClient(profile);
        } catch (Exception e) {
            log.error("阿里云短信客户端初始化失败，请确认相关配置文件是否正确");
        }
    }

    public boolean sendSms(String phoneNum, String code) {
        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain("dysmsapi.aliyuncs.com");
        request.setSysVersion("2017-05-25");
        request.setSysAction("SendSms");
        request.putQueryParameter("RegionId", aliSmsProperties.getRegionId());
        request.putQueryParameter("PhoneNumbers", phoneNum);
        request.putQueryParameter("SignName", aliSmsProperties.getSign().getTest());
        request.putQueryParameter("TemplateCode", aliSmsProperties.getTemplateCode().getAuthCode());
        request.putQueryParameter("TemplateParam", "{\"code\":\"" + code + "\"}");
        try {
            CommonResponse response = client.getCommonResponse(request);
            int httpStatus = response.getHttpStatus();
            return httpStatus == 200;
        } catch (ClientException e) {
            log.error("短信验证码发送异常：{}", e.getMessage());
        }
        return false;
    }

}
