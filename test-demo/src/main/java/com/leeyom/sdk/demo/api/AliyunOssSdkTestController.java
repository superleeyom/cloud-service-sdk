package com.leeyom.sdk.demo.api;

import cn.hutool.core.util.ObjectUtil;
import com.leeyom.sdk.aliyun.oss.util.AliOssUtil;
import com.leeyom.sdk.base.ApiResponse;
import com.leeyom.sdk.demo.vo.UploadImageUrlVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("aliyunOss")
public class AliyunOssSdkTestController {

    @Autowired
    private AliOssUtil aliOssUtil;

    /**
     * 上传图片(文件)
     *
     * @param file 文件域
     * @return 图片url信息
     */
    @PostMapping("uploadImage")
    public ApiResponse<UploadImageUrlVO> uploadImage(MultipartFile file) {
        if (ObjectUtil.isNull(file)) {
            return ApiResponse.ofFail("请上传文件");
        }
        String url = aliOssUtil.uploadImg2Oss(file);
        return ApiResponse.ofSuccess(UploadImageUrlVO.builder().imageUrl(url).build());
    }


}
