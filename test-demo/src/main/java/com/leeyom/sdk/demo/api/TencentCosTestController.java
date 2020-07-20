package com.leeyom.sdk.demo.api;


import cn.hutool.core.util.StrUtil;
import com.leeyom.sdk.base.ApiResponse;
import com.leeyom.sdk.tencent.cos.dto.TencentCosPreSignPostDTO;
import com.leeyom.sdk.tencent.cos.dto.TencentCosPreSignPutDTO;
import com.leeyom.sdk.tencent.cos.util.TencentCosUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("tencentCos")
public class TencentCosTestController {

    @Autowired
    private TencentCosUtil tencentCosUtil;

    /**
     * 获取腾讯云cos预签名地址(put方式)
     *
     * @return 预签名地址
     */
    @GetMapping("getPreSignedUrlForPut")
    public ApiResponse<TencentCosPreSignPutDTO> getPreSignedUrlForPut(String fileName) {
        if (StrUtil.isBlank(fileName)) {
            return ApiResponse.ofFail("文件名不能为空");
        }
        return ApiResponse.ofSuccess(tencentCosUtil.getCosUploadTokenForPut(fileName));
    }

    /**
     * 获取腾讯云cos预签名地址(post方式)
     *
     * @return 预签名地址
     */
    @GetMapping("getPreSignedUrlForPost")
    public ApiResponse<TencentCosPreSignPostDTO> getPreSignedUrlForPost(String fileName) {
        if (StrUtil.isBlank(fileName)) {
            return ApiResponse.ofFail("文件名不能为空");
        }
        return ApiResponse.ofSuccess(tencentCosUtil.getCosUploadTokenForPost(fileName));
    }

}
