package com.leeyom.sdk.demo.api;

import com.leeyom.sdk.aliyun.oss.dto.AliyunOssPreSignedUrlDTO;
import com.leeyom.sdk.aliyun.oss.util.AliOssUtil;
import com.leeyom.sdk.base.ApiResponse;
import com.leeyom.sdk.demo.vo.UploadImageUrlVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 阿里云对象存储示例
 *
 * @author leeyom
 */
@RestController
@RequestMapping("aliyunOss")
public class AliyunOssTestController {

    /**
     * 上传图片(文件)
     *
     * @param file 文件域
     * @return 图片url信息
     */
    @PostMapping("uploadFile")
    public ApiResponse<UploadImageUrlVO> uploadFile(MultipartFile file) {
        String url = AliOssUtil.upload2Oss(file);
        return ApiResponse.ofSuccess(UploadImageUrlVO.builder().imageUrl(url).build());
    }

    /**
     * 获取前端直传策略数据
     *
     * @param fileName 文件名
     * @return 预签名url和文件访问url
     */
    @GetMapping("getOssPolicy")
    public ApiResponse<AliyunOssPreSignedUrlDTO> getOssPolicy(String fileName) {
        return ApiResponse.ofSuccess(AliOssUtil.getOssPolicy(fileName));
    }

    /**
     * 断点续传上传
     *
     * @param uploadFile 文件路径，例如：/Users/leeyom/Downloads/myheader.jpg
     * @return 文件上传后的访问地址
     */
    @GetMapping("breakpointUpload")
    public ApiResponse<UploadImageUrlVO> breakpointUpload(String uploadFile) {
        String url = AliOssUtil.breakpointUpload(uploadFile);
        return ApiResponse.ofSuccess(UploadImageUrlVO.builder().imageUrl(url).build());
    }
}
