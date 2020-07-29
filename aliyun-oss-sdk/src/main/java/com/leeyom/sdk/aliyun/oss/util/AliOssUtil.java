package com.leeyom.sdk.aliyun.oss.util;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PolicyConditions;
import com.leeyom.sdk.aliyun.oss.config.AliOssProperties;
import com.leeyom.sdk.aliyun.oss.dto.AliyunOssPolicy;
import com.leeyom.sdk.aliyun.oss.dto.AliyunOssPreSignedUrlDTO;
import com.leeyom.sdk.base.BizException;
import com.leeyom.sdk.base.Status;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * 阿里OSS对象存储工具类
 *
 * @author leeyom
 */
@Component
@Slf4j
public class AliOssUtil {

    private static AliOssProperties aliOssProperties;
    private static OSS ossClient;

    @Autowired
    public void setAliOssProperties(AliOssProperties aliOssProperties) {
        AliOssUtil.aliOssProperties = aliOssProperties;
    }

    @PostConstruct
    private void init() {
        try {
            ossClient = new OSSClientBuilder().build(aliOssProperties.getEndpoint(),
                    aliOssProperties.getAccessKeyId(), aliOssProperties.getAccessKeySecret());
        } catch (Exception e) {
            log.error("阿里云oss客户端初始化失败，请确认您填写的阿里云oss配置是否正确！！！");
        }
    }

    /**
     * 流式上传
     *
     * @param file 文件域
     * @return 上传后的url
     */
    public static String upload2Oss(MultipartFile file) {
        String suffix = validateParam(file);
        String fileName = IdUtil.simpleUUID() + suffix;
        InputStream inputStream;
        try {
            inputStream = file.getInputStream();
        } catch (IOException e) {
            throw new BizException("文件流读取出错");
        }
        // 上传文件
        uploadFile2OSS(inputStream, fileName);
        // 返回完整的访问url
        return getFileUrl(fileName);
    }

    /**
     * 获取前端直传策略数据
     *
     * @param fileName 文件名
     * @return 预签名url和文件访问url
     */
    public static AliyunOssPreSignedUrlDTO getOssPolicy(String fileName) {

        String suffix = validateFileName(fileName);
        fileName = IdUtil.simpleUUID() + suffix;

        // PostObject请求最大可支持的文件大小为5GB，即CONTENT_LENGTH_RANGE为5*1024*1024*1024
        PolicyConditions policyConds = new PolicyConditions();
        policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, 1048576000);
        policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, aliOssProperties.getFileDir());

        // 设置预签名的上传url的过期时间为1小时
        Date expiration = new Date(new Date().getTime() + 3600 * 1000);
        String postPolicy = ossClient.generatePostPolicy(expiration, policyConds);
        byte[] binaryData = postPolicy.getBytes(StandardCharsets.UTF_8);
        String encodedPolicy = BinaryUtil.toBase64String(binaryData);
        String postSignature = ossClient.calculatePostSignature(postPolicy);

        // 构建直传策略
        AliyunOssPolicy policy = new AliyunOssPolicy();
        policy.setAccessKeyId(aliOssProperties.getAccessKeyId());
        policy.setPolicy(encodedPolicy);
        policy.setSignature(postSignature);
        policy.setHost("https://" + aliOssProperties.getBucketName() + "." + aliOssProperties.getEndpoint());
        policy.setKey(aliOssProperties.getFileDir() + fileName);

        return AliyunOssPreSignedUrlDTO.builder().fileUrl(getFileUrl(fileName)).aliyunOssPolicy(policy).build();
    }

    private static String validateFileName(String fileName) {
        if (StrUtil.isBlank(fileName)) {
            throw new BizException(Status.ERROR, "文件名称不能为空");
        }
        String suffix = StrUtil.subSuf(fileName, StrUtil.lastIndexOfIgnoreCase(fileName, ".")).toLowerCase();
        if (StrUtil.isBlank(suffix)) {
            throw new BizException(Status.ERROR, "文件后缀为空");
        }
        return suffix;
    }

    /**
     * 参数校验
     *
     * @param file 文件域
     * @return 文件后缀
     */
    private static String validateParam(MultipartFile file) {
        if (file == null) {
            throw new BizException(Status.ERROR, "文件不能为空");
        }
        if (file.getSize() / (1024 * 1024) > aliOssProperties.getMaxFileSize()) {
            throw new BizException(Status.ERROR, "文件上传大小不能超过" + aliOssProperties.getMaxFileSize() + "MB");
        }
        String originalFilename = file.getOriginalFilename();
        return validateFileName(originalFilename);
    }

    /**
     * 获得上传文件路径
     *
     * @param fileName 文件名，比如：123.jpg
     * @return 文件的访问路径
     */
    private static String getFileUrl(String fileName) {
        if (StrUtil.isNotBlank(fileName)) {
            String fileUrl = getUrl(aliOssProperties.getFileDir() + fileName);
            if (StrUtil.isBlank(fileUrl)) {
                throw new BizException(Status.ERROR, "文件上传失败，文件url为空");
            }
            // 去掉url后面的敏感数据，例如 AccessKeyId 等
            String[] split = fileUrl.split("\\?");
            if (ArrayUtil.isEmpty(split)) {
                throw new BizException(Status.ERROR, "文件上传失败，文件url为空");
            }
            return split[0];
        }
        return StrUtil.EMPTY;
    }

    /**
     * 上传到OSS服务器 如果同名文件会覆盖服务器上的
     *
     * @param inputStream 文件流
     * @param fileName    文件名称 包括后缀名
     */
    private static void uploadFile2OSS(InputStream inputStream, String fileName) {
        try {
            // 创建上传Object的Metadata
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(inputStream.available());
            objectMetadata.setCacheControl("no-cache");
            objectMetadata.setHeader("Pragma", "no-cache");
            objectMetadata.setContentType(getContentType(fileName.substring(fileName.lastIndexOf("."))));
            objectMetadata.setContentDisposition("inline;filename=" + fileName);
            // 上传文件
            ossClient.putObject(aliOssProperties.getBucketName(), aliOssProperties.getFileDir() + fileName, inputStream, objectMetadata);
        } catch (IOException e) {
            log.error("阿里云oss文件上传失败：", e);
        } finally {
            IoUtil.close(inputStream);
        }
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

    /**
     * 使用签名URL获取文件
     *
     * @param key objectName
     * @return 文件访问url
     */
    private static String getUrl(String key) {
        // 设置URL过期时间为10年 3600l* 1000*24*365*10
        Date expiration = new Date(System.currentTimeMillis() + 3600L * 1000 * 24 * 365 * 10);
        // 生成URL
        URL url = ossClient.generatePresignedUrl(aliOssProperties.getBucketName(), key, expiration);
        if (ObjectUtil.isNotNull(url)) {
            return url.toString();
        }
        return StrUtil.EMPTY;
    }

}
