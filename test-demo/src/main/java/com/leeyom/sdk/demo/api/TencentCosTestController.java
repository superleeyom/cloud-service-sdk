package com.leeyom.sdk.demo.api;


import com.leeyom.sdk.base.ApiResponse;
import com.leeyom.sdk.tencent.cos.dto.TencentCosPreSignPostDTO;
import com.leeyom.sdk.tencent.cos.dto.TencentCosPreSignPutDTO;
import com.leeyom.sdk.tencent.cos.util.TencentCosUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        return ApiResponse.ofSuccess(TencentCosUtil.getCosUploadTokenForPut(fileName));
    }

    /**
     * 获取腾讯云cos预签名地址(post方式)
     *
     * @return 预签名地址
     */
    @GetMapping("getPreSignedUrlForPost")
    public ApiResponse<TencentCosPreSignPostDTO> getPreSignedUrlForPost(String fileName) {
        return ApiResponse.ofSuccess(TencentCosUtil.getCosUploadTokenForPost(fileName));
    }

}
