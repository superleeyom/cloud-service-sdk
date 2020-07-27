package com.leeyom.sdk.aliyun.oss.util;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.ObjectMetadata;
import com.leeyom.sdk.aliyun.oss.config.AliOssProperties;
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
    public static String uploadImg2Oss(MultipartFile file) {
        String suffix = validateParam(file);
        String name = IdUtil.simpleUUID() + suffix;
        InputStream inputStream;
        try {
            inputStream = file.getInputStream();
        } catch (IOException e) {
            throw new BizException("文件流读取出错");
        }
        // 上传文件
        uploadFile2OSS(inputStream, name);
        // 返回完整的访问url
        String imgUrl = getImgUrl(name);
        if (StrUtil.isBlank(imgUrl)) {
            throw new BizException(Status.ERROR, "文件上传失败，文件url为空");
        }

        // 去掉url后面的敏感数据，例如 AccessKeyId 等
        String[] split = imgUrl.split("\\?");
        if (ArrayUtil.isEmpty(split)) {
            throw new BizException(Status.ERROR, "文件上传失败，文件url为空");
        }
        return split[0];
    }

    /**
     * 参数校验
     *
     * @param file 文件域
     * @return 文件后缀
     */
    private static String validateParam(MultipartFile file) {
        long MAX_SIZE = 30 * 1024 * 1024;
        if (file == null) {
            throw new BizException(Status.ERROR, "文件不能为空");
        }
        if (file.getSize() > MAX_SIZE) {
            throw new BizException(Status.ERROR, "图片上传大小不能超过30兆");
        }
        String originalFilename = file.getOriginalFilename();
        if (StrUtil.isBlank(originalFilename)) {
            throw new BizException(Status.ERROR, "图片名称不能为空");
        }

        String suffix = StrUtil.subSuf(originalFilename, StrUtil.lastIndexOfIgnoreCase(originalFilename, ".")).toLowerCase();
        if (StrUtil.isBlank(suffix)) {
            throw new BizException(Status.ERROR, "文件后缀为空");
        }
        return suffix;
    }

    /**
     * 获得图片路径
     *
     * @param fileName 文件名，比如：123.jpg
     * @return 图片的访问路径
     */
    private static String getImgUrl(String fileName) {
        if (StrUtil.isNotBlank(fileName)) {
            return getUrl(aliOssProperties.getFileDir() + fileName);
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
        if (fileSuffix.equalsIgnoreCase("bmp")) {
            return "image/bmp";
        }
        if (fileSuffix.equalsIgnoreCase("gif")) {
            return "image/gif";
        }
        if (fileSuffix.equalsIgnoreCase("jpeg") || fileSuffix.equalsIgnoreCase("jpg")
                || fileSuffix.equalsIgnoreCase("png")) {
            return "image/jpeg";
        }
        if (fileSuffix.equalsIgnoreCase("html")) {
            return "text/html";
        }
        if (fileSuffix.equalsIgnoreCase("txt")) {
            return "text/plain";
        }
        if (fileSuffix.equalsIgnoreCase("vsd")) {
            return "application/vnd.visio";
        }
        if (fileSuffix.equalsIgnoreCase("pptx") || fileSuffix.equalsIgnoreCase("ppt")) {
            return "application/vnd.ms-powerpoint";
        }
        if (fileSuffix.equalsIgnoreCase("docx") || fileSuffix.equalsIgnoreCase("doc")) {
            return "application/msword";
        }
        if (fileSuffix.equalsIgnoreCase("xml")) {
            return "text/xml";
        }
        return "image/jpeg";
    }

    /**
     * 使用签名URL临时授权
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
