package com.leeyom.sdk.aliyun.oss.util;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectResult;
import com.leeyom.sdk.aliyun.oss.config.AliOssProperties;
import com.leeyom.sdk.base.BizException;
import com.leeyom.sdk.base.Status;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.*;
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

    @Autowired
    private AliOssProperties aliOssProperties;

    private OSS ossClient;

    /**
     * 允许上传文件的大小
     */
    private long MAX_SIZE = 30 * 1024 * 1024;

    @PostConstruct
    private void init() {
        try {
            ossClient = new OSSClientBuilder().build(aliOssProperties.getEndpoint(),
                    aliOssProperties.getAccessKeyId(), aliOssProperties.getAccessKeySecret());
        } catch (Exception e) {
            log.error("阿里云oss客户端初始化失败，请确认您填写的阿里云oss配置是否正确！！！");
        }
    }

    public void uploadImg2Oss(String url) {
        File fileOnServer = new File(url);
        FileInputStream fin;
        try {
            fin = new FileInputStream(fileOnServer);
            String[] split = url.split("/");
            this.uploadFile2OSS(fin, split[split.length - 1]);
        } catch (FileNotFoundException e) {
            throw new BizException(Status.ERROR, "图片上传失败");
        }
    }

    public String uploadImg2Oss(MultipartFile file) {
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
        String name = IdUtil.simpleUUID() + suffix;
        try {
            InputStream inputStream = file.getInputStream();
            this.uploadFile2OSS(inputStream, name);

            // 返回完整的访问url
            String imgUrl = getImgUrl(name);
            if (StrUtil.isBlank(imgUrl)) {
                throw new BizException(Status.ERROR, "文件上传失败，url为空");
            }
            String[] split = imgUrl.split("\\?");
            if (ArrayUtil.isEmpty(split)) {
                throw new BizException(Status.ERROR, "文件上传失败，url为空");
            }
            return split[0];
        } catch (Exception e) {
            throw new BizException(Status.ERROR, "图片上传失败");
        }
    }

    public String uploadImg2Oss(InputStream inputStream, String fileName) {
        try {
            this.uploadFile2OSS(inputStream, fileName);
            String imgUrl = getImgUrl(fileName);
            String[] split = imgUrl.split("\\?");
            if (ArrayUtil.isEmpty(split)) {
                throw new BizException(Status.ERROR, "文件上传失败，url为空");
            }
            return split[0];
        } catch (Exception e) {
            throw new BizException(Status.ERROR, "图片上传失败");
        }
    }

    /**
     * 获得图片路径
     *
     * @param fileUrl
     * @return
     */
    private String getImgUrl(String fileUrl) {
        System.out.println(fileUrl);
        if (StrUtil.isNotBlank(fileUrl)) {
            String[] split = fileUrl.split("/");
            return this.getUrl(aliOssProperties.getFileDir() + split[split.length - 1]);
        }
        return null;
    }

    /**
     * 上传到OSS服务器 如果同名文件会覆盖服务器上的
     *
     * @param instream 文件流
     * @param fileName 文件名称 包括后缀名
     * @return 出错返回"" ,唯一MD5数字签名
     */
    private String uploadFile2OSS(InputStream instream, String fileName) {
        String ret = "";
        try {
            // 创建上传Object的Metadata
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(instream.available());
            objectMetadata.setCacheControl("no-cache");
            objectMetadata.setHeader("Pragma", "no-cache");
            objectMetadata.setContentType(getcontentType(fileName.substring(fileName.lastIndexOf("."))));
            objectMetadata.setContentDisposition("inline;filename=" + fileName);
            // 上传文件
            PutObjectResult putResult = ossClient.putObject(aliOssProperties.getBucketName(),
                    aliOssProperties.getFileDir() + fileName, instream, objectMetadata);
            ret = putResult.getETag();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } finally {
            try {
                if (instream != null) {
                    instream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    /**
     * Description: 判断OSS服务文件上传时文件的contentType
     *
     * @param filenameExtension 文件后缀
     * @return String
     */
    private static String getcontentType(String filenameExtension) {
        if (filenameExtension.equalsIgnoreCase("bmp")) {
            return "image/bmp";
        }
        if (filenameExtension.equalsIgnoreCase("gif")) {
            return "image/gif";
        }
        if (filenameExtension.equalsIgnoreCase("jpeg") || filenameExtension.equalsIgnoreCase("jpg")
                || filenameExtension.equalsIgnoreCase("png")) {
            return "image/jpeg";
        }
        if (filenameExtension.equalsIgnoreCase("html")) {
            return "text/html";
        }
        if (filenameExtension.equalsIgnoreCase("txt")) {
            return "text/plain";
        }
        if (filenameExtension.equalsIgnoreCase("vsd")) {
            return "application/vnd.visio";
        }
        if (filenameExtension.equalsIgnoreCase("pptx") || filenameExtension.equalsIgnoreCase("ppt")) {
            return "application/vnd.ms-powerpoint";
        }
        if (filenameExtension.equalsIgnoreCase("docx") || filenameExtension.equalsIgnoreCase("doc")) {
            return "application/msword";
        }
        if (filenameExtension.equalsIgnoreCase("xml")) {
            return "text/xml";
        }
        return "image/jpeg";
    }

    /**
     * 获得url链接
     *
     * @param key
     * @return
     */
    private String getUrl(String key) {
        // 设置URL过期时间为10年 3600l* 1000*24*365*10
        Date expiration = new Date(System.currentTimeMillis() + 3600L * 1000 * 24 * 365 * 10);
        // 生成URL
        URL url = ossClient.generatePresignedUrl(aliOssProperties.getBucketName(), key, expiration);
        if (url != null) {
            return url.toString();
        }
        return null;
    }

}
