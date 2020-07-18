package com.leeyom.sdk.demo.api;

import cn.hutool.core.lang.Validator;
import com.leeyom.sdk.aliyun.sms.util.AliSmsUtil;
import com.leeyom.sdk.base.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("aliyunSms")
public class AliyunSmsTestController {

    @Autowired
    private AliSmsUtil aliSmsUtil;

    /**
     * 发送验证码
     *
     * @param phoneNum 手机号
     * @return 发送结果
     */
    @GetMapping("sendSms")
    public ApiResponse sendSms(String phoneNum) {
        if (!Validator.isMobile(phoneNum)) {
            return ApiResponse.ofFail("手机号格式不准确");
        }
        return ApiResponse.ofSuccess(aliSmsUtil.sendSms(phoneNum, "123456"));
    }
}
