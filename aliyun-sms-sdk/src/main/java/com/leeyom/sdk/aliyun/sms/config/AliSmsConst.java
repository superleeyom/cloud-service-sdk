package com.leeyom.sdk.aliyun.sms.config;

/**
 * 短信常量
 *
 * @author leeyom
 */
public interface AliSmsConst {

    /**
     * 阿里短信API产品域名
     */
    String DOMAIN = "dysmsapi.aliyuncs.com";

    /**
     * 阿里短信API接口
     */
    interface SysAction {

        /**
         * 单个发送短信
         */
        String SEND_SMS = "SendSms";

        /**
         * 批量发送短信
         */
        String SEND_BATCH_SMS = "SendBatchSms";

    }

    /**
     * 阿里短信API版本号
     */
    String SYS_VERSION = "2017-05-25";

}
