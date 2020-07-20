package com.leeyom.sdk.tencent.lbs.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class AddressComponentDTO {
    /**
     * nation : 中国
     * province : 北京市
     * city : 北京市
     * district : 西城区
     * street : 西单北大街
     * street_number : 西单北大街130号
     */

    private String nation;
    private String province;
    private String city;
    private String district;
    private String street;
    private String street_number;
}
