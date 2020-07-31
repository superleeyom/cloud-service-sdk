package com.leeyom.sdk.tencent.lbs.dto;

import lombok.Data;

import java.util.List;

/**
 * 坐标转换返回结果
 *
 * @author leeyom
 */
@Data
public class TencentLbsTranslateResponseDTO extends TencentLbsResponseDTO {

    /**
     * 坐标转换结果，转换后的坐标顺序与输入顺序一致
     */
    private List<LocationDTO> locations;

}
