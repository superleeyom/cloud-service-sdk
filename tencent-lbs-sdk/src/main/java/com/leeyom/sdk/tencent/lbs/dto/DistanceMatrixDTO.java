package com.leeyom.sdk.tencent.lbs.dto;

import lombok.Data;

import java.util.List;

/**
 * 矩阵距离
 */
@Data
public class DistanceMatrixDTO {

    private List<TencentMapDistanceDTO> rows;


}
