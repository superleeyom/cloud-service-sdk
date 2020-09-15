package com.leeyom.sdk.demo.api;


import com.leeyom.sdk.base.ApiResponse;
import com.leeyom.sdk.demo.vo.UploadImageUrlVO;
import com.leeyom.sdk.tencent.cos.dto.TencentCosPreSignPostDTO;
import com.leeyom.sdk.tencent.cos.dto.TencentCosPreSignPutDTO;
import com.leeyom.sdk.tencent.cos.util.TencentCosUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/**
 * 腾讯云对象存储示例
 *
 * @author leeyom
 */
@RestController
@RequestMapping("tencentCos")
public class TencentCosTestController {

    /**
     * 获取腾讯云cos预签名地址(put方式)
     *
     * @return 预签名地址
     */
    @GetMapping("getPreSignedUrlForPut")
    public ApiResponse<TencentCosPreSignPutDTO> getPreSignedUrlForPut(String fileName) {
        return ApiResponse.ofSuccess(TencentCosUtil.getCosPreSignForPut(fileName));
    }

    /**
     * 获取腾讯云cos预签名地址(post方式)
     *
     * @return 预签名地址
     */
    @GetMapping("getPreSignedUrlForPost")
    public ApiResponse<TencentCosPreSignPostDTO> getPreSignedUrlForPost(String fileName) {
        return ApiResponse.ofSuccess(TencentCosUtil.getCosPreSignForPost(fileName));
    }

    /**
     * 流式上传图片(文件)
     *
     * @param file 文件域
     * @return 图片url信息
     */
    @PostMapping("uploadFile")
    public ApiResponse<UploadImageUrlVO> uploadFile(MultipartFile file) {
        String url = TencentCosUtil.upload2Cos(file);
        return ApiResponse.ofSuccess(UploadImageUrlVO.builder().imageUrl(url).build());
    }

    /**
     * 流式上传
     *
     * @return 图片url信息
     */
    @GetMapping("uploadLocalFile")
    public ApiResponse<UploadImageUrlVO> uploadLocalFile() {
        File file = new File("/Users/leeyom/Pictures/头像/default.jpg");
        String url = TencentCosUtil.upload2Cos(file);
        return ApiResponse.ofSuccess(UploadImageUrlVO.builder().imageUrl(url).build());
    }

}
