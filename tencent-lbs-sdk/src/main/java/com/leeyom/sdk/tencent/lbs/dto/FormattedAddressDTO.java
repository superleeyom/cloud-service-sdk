package com.leeyom.sdk.tencent.lbs.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class FormattedAddressDTO {
    /**
     * recommend : 西城区西单大悦城写字楼(大木仓胡同南)
     * rough : 西城区西单大悦城写字楼(大木仓胡同南)
     */

    private String recommend;
    private String rough;
}
