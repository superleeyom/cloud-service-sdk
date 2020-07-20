package com.leeyom.sdk.aliyun.sms.util;

import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.StrUtil;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.leeyom.sdk.aliyun.sms.config.AliSmsProperties;
import com.leeyom.sdk.base.BizException;
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

    private static AliSmsProperties aliSmsProperties;
    private static IAcsClient client;

    @Autowired
    public void setAliSmsProperties(AliSmsProperties aliSmsProperties) {
        AliSmsUtil.aliSmsProperties = aliSmsProperties;
    }

    @PostConstruct
    private void init() {
        try {
            DefaultProfile profile = DefaultProfile.getProfile(aliSmsProperties.getRegionId(),
                    aliSmsProperties.getAccessKeyId(), aliSmsProperties.getAccessKeySecret());
            client = new DefaultAcsClient(profile);
        } catch (Exception e) {
            log.error("阿里云短信客户端初始化失败，请确认相关配置文件是否正确");
        }
    }

    /**
     * 发送验证码
     *
     * @param phoneNum 手机号
     * @param code     验证码
     * @return 发送结果
     */
    public static boolean sendSms(String phoneNum, String code) {
        validateParam(phoneNum, code);
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

    private static void validateParam(String phoneNum, String code) {
        if (!Validator.isMobile(phoneNum)) {
            throw new BizException("手机格式不正确");
        }

        if (StrUtil.isBlank(code)) {
            throw new BizException("验证码不能为空");
        }
    }

}
