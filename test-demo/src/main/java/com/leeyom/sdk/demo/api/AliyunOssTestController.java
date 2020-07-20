package com.leeyom.sdk.demo.api;

import com.leeyom.sdk.aliyun.oss.util.AliOssUtil;
import com.leeyom.sdk.base.ApiResponse;
import com.leeyom.sdk.demo.vo.UploadImageUrlVO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("aliyunOss")
public class AliyunOssTestController {

    /**
     * 上传图片(文件)
     *
     * @param file 文件域
     * @return 图片url信息
     */
    @PostMapping("uploadImage")
    public ApiResponse<UploadImageUrlVO> uploadImage(MultipartFile file) {
        String url = AliOssUtil.uploadImg2Oss(file);
        return ApiResponse.ofSuccess(UploadImageUrlVO.builder().imageUrl(url).build());
    }


}
