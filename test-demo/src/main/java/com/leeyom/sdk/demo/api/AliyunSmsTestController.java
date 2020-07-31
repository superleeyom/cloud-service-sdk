package com.leeyom.sdk.demo.api;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.RandomUtil;
import com.leeyom.sdk.aliyun.sms.config.AliSmsProperties;
import com.leeyom.sdk.aliyun.sms.util.AliSmsUtil;
import com.leeyom.sdk.base.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 阿里云短信示例
 *
 * @author leeyom
 */
@RestController
@RequestMapping("aliyunSms")
public class AliyunSmsTestController {

    @Autowired
    private AliSmsProperties properties;

    /**
     * 发送验证码
     *
     * @param phoneNum 手机号
     * @return 发送结果
     */
    @GetMapping("sendSms")
    public ApiResponse sendSms(String phoneNum) {
        HashMap<String, Object> templateParam = MapUtil.newHashMap(1);
        templateParam.put("code", RandomUtil.randomNumbers(6));
        boolean sendResult = AliSmsUtil.sendSms(phoneNum, properties.getSign().getTest(), properties.getTemplateCode().getAuthCode(), templateParam);
        if (sendResult) {
            return ApiResponse.ofSuccess("发送成功");
        }
        return ApiResponse.ofFail("发送失败");
    }

    /**
     * 批量发送短信
     *
     * @param phoneNum 手机号
     * @return 发送结果
     */
    @GetMapping("sendBatchSms")
    public ApiResponse sendBatchSms(@RequestParam("phoneNum") List<String> phoneNum) {

        List<String> signName = CollUtil.newArrayList();
        signName.add(properties.getSign().getTest());
        signName.add(properties.getSign().getTest());

        List<Map<String, Object>> templateParam = CollUtil.newArrayList();
        HashMap<String, Object> param1 = MapUtil.newHashMap(1);
        param1.put("code", RandomUtil.randomNumbers(6));
        templateParam.add(param1);

        HashMap<String, Object> param2 = MapUtil.newHashMap(1);
        param2.put("code", RandomUtil.randomNumbers(6));
        templateParam.add(param2);

        boolean sendResult = AliSmsUtil.sendBatchSms(phoneNum, signName, properties.getTemplateCode().getAuthCode(), templateParam);
        if (sendResult) {
            return ApiResponse.ofSuccess("发送成功");
        }
        return ApiResponse.ofFail("发送失败");
    }
}
