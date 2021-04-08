package com.leeyom.sdk.aliyun.oss.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.*;
import com.aliyuncs.exceptions.ClientException;
import com.leeyom.sdk.aliyun.oss.config.AliOssProperties;
import com.leeyom.sdk.aliyun.oss.dto.AliyunOssPolicy;
import com.leeyom.sdk.aliyun.oss.dto.AliyunOssPreSignedUrlDTO;
import com.leeyom.sdk.base.BizException;
import com.leeyom.sdk.base.Status;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
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

    @Autowired
    public void setOssClient(OSS ossClient) {
        AliOssUtil.ossClient = ossClient;
    }

    /**
     * 流式上传
     *
     * @param file 文件域
     * @return 上传后的url
     */
    public static String upload2Oss(MultipartFile file) {

        // 防止文件重复，重命名文件名称
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

        // 防止文件重复，重命名文件名称
        String suffix = validateFileName(fileName);
        fileName = IdUtil.simpleUUID() + suffix;

        // PostObject请求最大可支持的文件大小为5GB，即CONTENT_LENGTH_RANGE为5*1024*1024*1024
        PolicyConditions policyConds = new PolicyConditions();
        policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, 1048576000);
        policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, aliOssProperties.getFileDir());

        // 设置预签名的上传url的过期时间为1小时
        Date expiration = new Date(System.currentTimeMillis() + 3600 * 1000);
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

    /**
     * 断点续传上传
     * 上传过程中的进度信息会保存在 ${uploadFile}.ucp 文件中，如果某一分片上传失败，
     * 再次上传时会根据文件中记录的点继续上传。上传完成后，该文件会被删除。
     *
     * @param uploadFile 文件路径，例如：/Users/leeyom/Downloads/myheader.jpg
     * @return 文件上传后的访问地址
     */
    public static String breakpointUpload(String uploadFile) {
        if (StrUtil.isBlank(uploadFile)) {
            throw new BizException("文件路径为空");
        }
        if (!FileUtil.exist(uploadFile)) {
            throw new BizException("当前文件不存在");
        }
        try {
            File file = FileUtil.newFile(uploadFile);
            // 存储空间名称和上传到OSS的文件名称
            UploadFileRequest uploadFileRequest = new UploadFileRequest(aliOssProperties.getBucketName(),
                    aliOssProperties.getFileDir() + file.getName());
            // 待上传的本地文件路径
            uploadFileRequest.setUploadFile(uploadFile);
            // 上传并发线程数，默认值为1
            uploadFileRequest.setTaskNum(5);
            // 上传的分片大小，取值范围为100 KB~5 GB，默认值是100KB
            uploadFileRequest.setPartSize(1024 * 1024);
            // 是否开启断点续传功能，默认关闭
            uploadFileRequest.setEnableCheckpoint(true);
            UploadFileResult uploadResult = ossClient.uploadFile(uploadFileRequest);
            uploadResult.getMultipartUploadResult();
            // 返回完整的访问url
            return getFileUrl(file.getName());
        } catch (OSSException oe) {
            log.error("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            log.error("Error Message: {}", oe.getErrorMessage());
            log.error("Error Code: {} ", oe.getErrorCode());
            log.error("Request ID: {}", oe.getRequestId());
            log.error("Host ID: {}", oe.getHostId());
        } catch (ClientException ce) {
            log.error("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            log.error("Error Message: {}", ce.getMessage());
        } catch (Throwable e) {
            log.error("Error Message: {}", e.getMessage());
        }
        return "";
    }

    /**
     * 断点续传下载
     * downloadFile.ucp，记录本地分片下载结果的文件。开启断点续传功能，下载过程中的进度信息会保存在该文件中，
     * 如果某一分片下载失败，再次下载时会根据文件中记录的点继续下载。下载完成后，该文件会被删除，该文件与DownloadFile同目录
     *
     * @param fileName 文件名称，比如：myheader.jpg
     * @return 文件存储在本地的路径
     */
    public static String downloadFile(String fileName) {
        if (StrUtil.isBlank(fileName)) {
            throw new BizException("文件名称不能为空");
        }

        // 重命名文件，防止文件覆盖
        String downloadFilePath = renameFile(fileName);

        // 下载请求，10个任务并发下载，启动断点续传
        DownloadFileRequest downloadFileRequest = new DownloadFileRequest(aliOssProperties.getBucketName(),
                aliOssProperties.getFileDir() + fileName);
        downloadFileRequest.setDownloadFile(downloadFilePath);
        downloadFileRequest.setPartSize(1024 * 1024);
        downloadFileRequest.setTaskNum(10);
        downloadFileRequest.setEnableCheckpoint(true);
        try {
            // 下载文件
            ossClient.downloadFile(downloadFileRequest);
            return downloadFilePath;
        } catch (Throwable throwable) {
            log.error("文件下载失败：{}", throwable.getMessage());
            return throwable.getMessage();
        }
    }

    /**
     * 如果出现相同名称的文件，文件名称将重命名为xxx(1).jpg，
     * 比如文件名为：myheader.jpg，重命名后为：myheader(1).jpg
     *
     * @param fileName 文件名称
     * @return 文件存储在本地的路径
     */
    private static String renameFile(String fileName) {
        String downloadFileDir;
        String downloadFilePath;
        if (FileUtil.isWindows()) {
            downloadFileDir = FileUtil.getUserHomePath() + "\\" + aliOssProperties.getBucketName() + "\\";
        } else {
            downloadFileDir = FileUtil.getUserHomePath() + "/" + aliOssProperties.getBucketName() + "/";
        }
        downloadFilePath = downloadFileDir + fileName;
        File dir = new File(downloadFileDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File downloadFile = FileUtil.newFile(downloadFilePath);
        if (FileUtil.exist(downloadFile)) {
            String suffix = FileUtil.getSuffix(downloadFile);
            String prefix = FileUtil.getPrefix(FileUtil.getName(downloadFilePath));
            for (int i = 1; downloadFile.exists() && i < Integer.MAX_VALUE; i++) {
                downloadFile = new File(downloadFileDir + prefix + '(' + i + ')' + "." + suffix);
            }
            downloadFilePath = downloadFile.getPath();
        }
        return downloadFilePath;
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
        if ("bmp".equalsIgnoreCase(fileSuffix)) {
            return "image/bmp";
        }
        if ("gif".equalsIgnoreCase(fileSuffix)) {
            return "image/gif";
        }
        if ("jpeg".equalsIgnoreCase(fileSuffix) || "jpg".equalsIgnoreCase(fileSuffix)
                || "png".equalsIgnoreCase(fileSuffix)) {
            return "image/jpeg";
        }
        if ("html".equalsIgnoreCase(fileSuffix)) {
            return "text/html";
        }
        if ("txt".equalsIgnoreCase(fileSuffix)) {
            return "text/plain";
        }
        if ("vsd".equalsIgnoreCase(fileSuffix)) {
            return "application/vnd.visio";
        }
        if ("pptx".equalsIgnoreCase(fileSuffix) || "ppt".equalsIgnoreCase(fileSuffix)) {
            return "application/vnd.ms-powerpoint";
        }
        if ("docx".equalsIgnoreCase(fileSuffix) || "doc".equalsIgnoreCase(fileSuffix)) {
            return "application/msword";
        }
        if ("xml".equalsIgnoreCase(fileSuffix)) {
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
