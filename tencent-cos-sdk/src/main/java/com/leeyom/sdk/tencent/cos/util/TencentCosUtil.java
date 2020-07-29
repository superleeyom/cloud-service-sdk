package com.leeyom.sdk.tencent.cos.util;

import cn.hutool.core.net.url.UrlBuilder;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.leeyom.sdk.base.BizException;
import com.leeyom.sdk.base.Status;
import com.leeyom.sdk.tencent.cos.config.TencentCosProperties;
import com.leeyom.sdk.tencent.cos.dto.TencentCosPreSignPostDTO;
import com.leeyom.sdk.tencent.cos.dto.TencentCosPreSignPutDTO;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.auth.COSSigner;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.exception.CosServiceException;
import com.qcloud.cos.http.HttpMethodName;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.region.Region;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
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
@Slf4j
@Component
public class TencentCosUtil {

    private static TencentCosProperties tencentCosProperties;
    private static COSClient client;

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
        client = new COSClient(cred, clientConfig);
    }

    /**
     * PUT方式获取预签名
     *
     * @param fileName 文件名，比如：header.jpg
     * @return 前端上传文件的预签名url和实际文件访问地址
     */
    public static TencentCosPreSignPutDTO getCosPreSignForPut(String fileName) {
        String fileSuffix = validateFileName(fileName);
        Date expiredTime = new Date(System.currentTimeMillis() + tencentCosProperties.getExpiredTime());
        // 为防止文件重复，重写文件名称
        String key = tencentCosProperties.getDir() + IdUtil.simpleUUID() + fileSuffix;
        URL sign = client.generatePresignedUrl(tencentCosProperties.getBucketName(), key, expiredTime, HttpMethodName.PUT);
        UrlBuilder builder = UrlBuilder.ofHttp(sign.toString(), CharsetUtil.CHARSET_UTF_8);

        // 如果有自定义域名，比如 domain：https://leeyom-image.cn/
        if (StrUtil.isNotBlank(tencentCosProperties.getDomain())) {
            String fileUrl = tencentCosProperties.getDomain() + key;
            String requestUrl = StrUtil.replace(sign.toString(), builder.getScheme() + "://" + builder.getHost(),
                    StrUtil.removeSuffix(tencentCosProperties.getDomain(), "/"));
            return TencentCosPreSignPutDTO.builder().preSignUrl(requestUrl).fileUrl(fileUrl).build();
        }

        String fileUrl = "https://" + builder.getHost() + "/" + key;
        return TencentCosPreSignPutDTO.builder().preSignUrl(sign.toString()).fileUrl(fileUrl).build();
    }

    private static String validateFileName(String fileName) {
        if (StrUtil.isBlank(fileName)) {
            throw new BizException("文件名不能为空");
        }
        String fileSuffix = fileName.substring(fileName.lastIndexOf("."));
        if (StrUtil.isBlank(fileSuffix)) {
            throw new BizException("请指定文件名后缀");
        }
        return fileSuffix;
    }

    /**
     * POST方式获取预签名
     *
     * @param fileName 文件名，比如：header.jpg
     * @return 前端上传文件的预签名url和实际文件访问地址
     */
    public static TencentCosPreSignPostDTO getCosPreSignForPost(String fileName) {
        String fileSuffix = validateFileName(fileName);
        String bucketName = tencentCosProperties.getBucketName();
        String key = tencentCosProperties.getDir() + IdUtil.simpleUUID() + fileSuffix;
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
        String preSignUrl = "https://" + bucketName + "." + endpoint;
        return TencentCosPreSignPostDTO.builder()
                .formFields(formFields)
                .preSignUrl(preSignUrl)
                .fileUrl(preSignUrl + "/" + key).build();
    }

    /**
     * 流式上传
     *
     * @param file 文件域
     * @return 上传成功后的访问url
     */
    public static String upload2Cos(MultipartFile file) {
        if (file == null) {
            throw new BizException(Status.ERROR, "文件不能为空");
        }
        String originalFilename = file.getOriginalFilename();
        String fileSuffix = validateFileName(originalFilename);
        String key = tencentCosProperties.getDir() + IdUtil.simpleUUID() + fileSuffix;
        try {
            InputStream inputStream = file.getInputStream();
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(inputStream.available());
            metadata.setContentType(getContentType(fileSuffix));
            PutObjectRequest putObjectRequest = new PutObjectRequest(tencentCosProperties.getBucketName(), key, inputStream, metadata);
            client.putObject(putObjectRequest);
        } catch (IOException e) {
            log.error("文件流读取失败：", e);
        } catch (CosServiceException e) {
            log.error("文件上传失败，服务端异常：", e);
        } catch (CosClientException e) {
            log.error("文件上传失败，客户端异常：", e);
        }
        String endpoint = "cos." + tencentCosProperties.getRegion() + ".myqcloud.com";
        return "https://" + tencentCosProperties.getBucketName() + "." + endpoint + "/" + key;
    }

    /**
     * 判断OSS服务文件上传时文件的contentType
     *
     * @param fileSuffix 文件后缀
     * @return ContentType
     */
    private static String getContentType(String fileSuffix) {
        if (fileSuffix.equalsIgnoreCase(".bmp")) {
            return "image/bmp";
        }
        if (fileSuffix.equalsIgnoreCase(".gif")) {
            return "image/gif";
        }
        if (fileSuffix.equalsIgnoreCase(".jpeg") || fileSuffix.equalsIgnoreCase(".jpg")
                || fileSuffix.equalsIgnoreCase(".png")) {
            return "image/jpeg";
        }
        if (fileSuffix.equalsIgnoreCase(".html")) {
            return "text/html";
        }
        if (fileSuffix.equalsIgnoreCase(".txt")) {
            return "text/plain";
        }
        if (fileSuffix.equalsIgnoreCase(".vsd")) {
            return "application/vnd.visio";
        }
        if (fileSuffix.equalsIgnoreCase(".pptx") || fileSuffix.equalsIgnoreCase(".ppt")) {
            return "application/vnd.ms-powerpoint";
        }
        if (fileSuffix.equalsIgnoreCase(".docx") || fileSuffix.equalsIgnoreCase(".doc")) {
            return "application/msword";
        }
        if (fileSuffix.equalsIgnoreCase(".xml")) {
            return "text/xml";
        }
        return "image/jpeg";
    }

}
