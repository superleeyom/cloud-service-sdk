package com.leeyom.sdk.demo.api;

import com.leeyom.sdk.aliyun.sms.util.AliSmsUtil;
import com.leeyom.sdk.base.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("aliyunSms")
public class AliyunSmsTestController {

    /**
     * 发送验证码
     *
     * @param phoneNum 手机号
     * @return 发送结果
     */
    @GetMapping("sendSms")
    public ApiResponse sendSms(String phoneNum) {
        return ApiResponse.ofSuccess(AliSmsUtil.sendSms(phoneNum, "123456"));
    }
}
