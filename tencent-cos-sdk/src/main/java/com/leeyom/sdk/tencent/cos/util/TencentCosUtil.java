package com.leeyom.sdk.tencent.cos.util;

import cn.hutool.core.net.url.UrlBuilder;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.leeyom.sdk.base.BizException;
import com.leeyom.sdk.tencent.cos.config.TencentCosProperties;
import com.leeyom.sdk.tencent.cos.dto.TencentCosPreSignPostDTO;
import com.leeyom.sdk.tencent.cos.dto.TencentCosPreSignPutDTO;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.auth.COSSigner;
import com.qcloud.cos.http.HttpMethodName;
import com.qcloud.cos.region.Region;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 腾讯云cos工具类
 *
 * @author leeyom
 */
@Component
public class TencentCosUtil {

    private static TencentCosProperties tencentCosProperties;
    private static COSClient cosclient;

    @Autowired
    public void setTencentCosProperties(TencentCosProperties tencentCosProperties) {
        TencentCosUtil.tencentCosProperties = tencentCosProperties;
    }

    @PostConstruct
    private void init() {
        // 1 初始化用户身份信息
        COSCredentials cred = new BasicCOSCredentials(tencentCosProperties.getSecretId(), tencentCosProperties.getSecretKey());
        // 2 设置bucket的区域
        ClientConfig clientConfig = new ClientConfig(new Region(tencentCosProperties.getRegion()));
        // 3 生成cos客户端
        cosclient = new COSClient(cred, clientConfig);
    }

    /**
     * PUT方式获取预签名
     *
     * @param fileName 文件名，比如：header.jpg
     * @return 前端上传文件的预签名url和实际文件访问地址
     */
    public static TencentCosPreSignPutDTO getCosUploadTokenForPut(String fileName) {
        validateParam(fileName);
        Date expiredTime = new Date(System.currentTimeMillis() + tencentCosProperties.getExpiredTime());
        // 为防止文件重复，重写文件名称
        String key = tencentCosProperties.getDir() + IdUtil.simpleUUID() + fileName.substring(fileName.lastIndexOf("."));
        URL sign = cosclient.generatePresignedUrl(tencentCosProperties.getBucketName(), key, expiredTime, HttpMethodName.PUT);
        UrlBuilder builder = UrlBuilder.ofHttp(sign.toString(), CharsetUtil.CHARSET_UTF_8);

        // 如果有自定义域名，比如 domain：https://leeyom-image.cn/
        if (StrUtil.isNotBlank(tencentCosProperties.getDomain())) {
            String fileUrl = tencentCosProperties.getDomain() + key;
            String requestUrl = StrUtil.replace(sign.toString(), builder.getScheme() + "://" + builder.getHost(),
                    StrUtil.removeSuffix(tencentCosProperties.getDomain(), "/"));
            return TencentCosPreSignPutDTO.builder().preSignUrl(requestUrl).fileUrl(fileUrl).build();
        }

        String fileUrl = builder.getScheme() + "://" + builder.getHost() + "/" + key;
        return TencentCosPreSignPutDTO.builder().preSignUrl(sign.toString()).fileUrl(fileUrl).build();
    }

    private static void validateParam(String fileName) {
        if (StrUtil.isBlank(fileName)) {
            throw new BizException("文件名不能为空");
        }
        String fileSuffix = fileName.substring(fileName.lastIndexOf("."));
        if (StrUtil.isBlank(fileSuffix)) {
            throw new BizException("请指定文件名后缀");
        }
    }

    /**
     * POST方式获取预签名
     *
     * @param fileName 文件名，比如：header.jpg
     * @return 前端上传文件的预签名url和实际文件访问地址
     */
    public static TencentCosPreSignPostDTO getCosUploadTokenForPost(String fileName) {
        validateParam(fileName);
        String bucketName = tencentCosProperties.getBucketName();
        String key = tencentCosProperties.getDir() + IdUtil.simpleUUID() + fileName.substring(fileName.lastIndexOf("."));
        long startTimestamp = System.currentTimeMillis() / 1000;
        long endTimestamp = startTimestamp + 30 * 60;
        String endTimestampStr = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(endTimestamp * 1000);
        String keyTime = startTimestamp + ";" + endTimestamp;

        // 设置表单的body字段值
        Map<String, String> formFields = new HashMap<>();
        formFields.put("q-sign-algorithm", "sha1");
        formFields.put("key", key);
        formFields.put("q-ak", tencentCosProperties.getSecretId());
        formFields.put("q-key-time", keyTime);

        // 构造policy，参考文档: https://cloud.tencent.com/document/product/436/14690
        String policy = "{\n" +
                "    \"expiration\": \"" + endTimestampStr + "\",\n" +
                "    \"conditions\": [\n" +
                "        { \"bucket\": \"" + bucketName + "\" },\n" +
                "        { \"q-sign-algorithm\": \"sha1\" },\n" +
                "        { \"q-ak\": \"" + tencentCosProperties.getSecretId() + "\" },\n" +
                "        { \"q-sign-time\":\"" + keyTime + "\" }\n" +
                "    ]\n" +
                "}";

        // policy需要base64后算放入表单中
        String encodedPolicy = new String(Base64.encodeBase64(policy.getBytes()));
        // 设置policy
        formFields.put("policy", encodedPolicy);
        // 根据编码后的policy和secretKey计算签名
        COSSigner cosSigner = new COSSigner();
        String signature = cosSigner.buildPostObjectSignature(tencentCosProperties.getSecretKey(), keyTime, policy);
        // 设置签名
        formFields.put("q-signature", signature);

        // 如果有自定义域名，比如 domain：https://leeyom-image.cn/
        if (StrUtil.isNotBlank(tencentCosProperties.getDomain())) {
            return TencentCosPreSignPostDTO.builder()
                    .formFields(formFields)
                    .preSignUrl(StrUtil.removeSuffix(tencentCosProperties.getDomain(), "/"))
                    .fileUrl(tencentCosProperties.getDomain() + key).build();
        }

        String endpoint = "cos." + tencentCosProperties.getRegion() + ".myqcloud.com";
        String preSignUrl = "http://" + bucketName + "." + endpoint;
        return TencentCosPreSignPostDTO.builder()
                .formFields(formFields)
                .preSignUrl(preSignUrl)
                .fileUrl(preSignUrl + "/" + key).build();

    }


}
