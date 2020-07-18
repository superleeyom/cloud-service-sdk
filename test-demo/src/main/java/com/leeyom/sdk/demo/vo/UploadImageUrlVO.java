package com.leeyom.sdk.demo.vo;

import lombok.Builder;
import lombok.Data;

/**
 * 上传图片的url
 *
 * @author leeyom
 */
@Data
@Builder
public class UploadImageUrlVO {

    /**
     * 图片url
     */
    private String imageUrl;

}
