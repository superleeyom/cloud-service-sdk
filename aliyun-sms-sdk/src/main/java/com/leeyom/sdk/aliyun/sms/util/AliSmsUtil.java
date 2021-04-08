package com.leeyom.sdk.aliyun.sms.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.leeyom.sdk.aliyun.sms.config.AliSmsConst;
import com.leeyom.sdk.aliyun.sms.config.AliSmsProperties;
import com.leeyom.sdk.base.BizException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

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
    private static final String SUCCESS = "OK";

    @Autowired
    public void setAliSmsProperties(AliSmsProperties aliSmsProperties) {
        AliSmsUtil.aliSmsProperties = aliSmsProperties;
    }

    @Autowired
    public void setClient(IAcsClient client) {
        AliSmsUtil.client = client;
    }

    /**
     * 单个发送验证码
     *
     * @param phoneNum      接收手机号，标准11位
     * @param signName      短信签名，比如：【xx科技】
     * @param templateCode  短信模板id，比如：SMS_183835228
     * @param templateParam 模板参数，比如：["code":"233423"]
     * @return 发送结果，true或false
     */
    public static boolean sendSms(String phoneNum, String signName, String templateCode, Map<String, Object> templateParam) {
        validateParam(phoneNum, signName, templateCode, templateParam);
        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain(AliSmsConst.DOMAIN);
        request.setSysVersion(AliSmsConst.SYS_VERSION);
        request.setSysAction(AliSmsConst.SysAction.SEND_SMS);
        request.putQueryParameter("RegionId", aliSmsProperties.getRegionId());
        request.putQueryParameter("PhoneNumbers", phoneNum);
        request.putQueryParameter("SignName", signName);
        request.putQueryParameter("TemplateCode", templateCode);
        request.putQueryParameter("TemplateParam", JSONUtil.toJsonStr(templateParam));
        try {
            CommonResponse response = client.getCommonResponse(request);
            log.info("发送短信结果：{}", JSONUtil.toJsonStr(response.getData()));
            JSONObject responseData = JSONUtil.parseObj(response.getData());
            return SUCCESS.equals(responseData.get("Code"));
        } catch (ClientException e) {
            log.error("短信验证码发送异常：{}", e.getMessage());
        }
        return false;
    }

    /**
     * 批量发送短信
     *
     * @param phoneNumber   接收的手机号，比如：[“15000000000”,”15000000001”]
     * @param signName      短信签名，比如：[“云通信”,”云通信”]
     * @param templateCode  短信模板id，比如：SMS_183835228
     * @param templateParam 短信模板参数，比如：[{“code”:”1234”,”product”:”ytx1”},{“code”:”5678”,”product”:”ytx2”}]
     * @return 发送结果，true或false
     */
    public static boolean sendBatchSms(List<String> phoneNumber, List<String> signName, String templateCode, List<Map<String, Object>> templateParam) {
        validateParam(phoneNumber, signName, templateCode, templateParam);
        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain(AliSmsConst.DOMAIN);
        request.setSysVersion(AliSmsConst.SYS_VERSION);
        request.setSysAction(AliSmsConst.SysAction.SEND_BATCH_SMS);
        request.putQueryParameter("RegionId", aliSmsProperties.getRegionId());
        request.putQueryParameter("PhoneNumberJson", JSONUtil.toJsonStr(phoneNumber));
        request.putQueryParameter("SignNameJson", JSONUtil.toJsonStr(signName));
        request.putQueryParameter("TemplateCode", templateCode);
        request.putQueryParameter("TemplateParamJson", JSONUtil.toJsonStr(templateParam));
        try {
            CommonResponse response = client.getCommonResponse(request);
            log.info("批量发送短信结果：{}", JSONUtil.toJsonStr(response.getData()));
            JSONObject responseData = JSONUtil.parseObj(response.getData());
            return SUCCESS.equals(responseData.get("Code"));
        } catch (ClientException e) {
            log.error("批量发送短信验证码异常：{}", e.getMessage());
        }
        return false;
    }

    private static void validateParam(List<String> phoneNumber, List<String> signName, String templateCode, List<Map<String, Object>> templateParam) {
        if (CollUtil.isEmpty(phoneNumber)) {
            throw new BizException("手机号不能为空");
        } else {
            boolean match = phoneNumber.stream().anyMatch(phone -> !Validator.isMobile(phone));
            if (match) {
                throw new BizException("存在手机号不符合格式");
            }
        }
        if (CollUtil.isEmpty(signName)) {
            throw new BizException("短信签名不能为空");
        }
        if (StrUtil.isBlank(templateCode)) {
            throw new BizException("短信模板不能为空");
        }
        if (CollUtil.isEmpty(templateParam)) {
            throw new BizException("模板参数不能为空");
        } else {
            boolean match = templateParam.stream().anyMatch(CollUtil::isEmpty);
            if (match) {
                throw new BizException("存在模板参数为空");
            }
        }
        if (!(phoneNumber.size() == templateParam.size() && templateParam.size() == signName.size())) {
            throw new BizException("当前要发送的模板参数，与接收的手机号数量不匹配");
        }
    }

    private static void validateParam(String phoneNum, String signName, String templateCode, Map<String, Object> templateParam) {
        if (!Validator.isMobile(phoneNum)) {
            throw new BizException("手机格式不正确");
        }
        if (StrUtil.isBlank(signName)) {
            throw new BizException("短信签名不能为空");
        }
        if (StrUtil.isBlank(templateCode)) {
            throw new BizException("短信模板不能为空");
        }
        if (CollUtil.isEmpty(templateParam)) {
            throw new BizException("模板参数不能为空");
        }
    }
}
