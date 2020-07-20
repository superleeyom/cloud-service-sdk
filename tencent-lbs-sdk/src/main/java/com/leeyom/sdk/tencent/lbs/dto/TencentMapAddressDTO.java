package com.leeyom.sdk.tencent.lbs.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class TencentMapAddressDTO {


    /**
     * location : {"lat":39.911185,"lng":116.372938}
     * address : 北京市西城区西单北大街130号
     * formatted_addresses : {"recommend":"西城区西单大悦城写字楼(大木仓胡同南)","rough":"西城区西单大悦城写字楼(大木仓胡同南)"}
     * address_component : {"nation":"中国","province":"北京市","city":"北京市","district":"西城区","street":"西单北大街","street_number":"西单北大街130号"}
     * ad_info : {"nation_code":"156","adcode":"110102","city_code":"156110000","name":"中国,北京市,北京市,西城区","location":{"lat":39.870731,"lng":116.375},"nation":"中国","province":"北京市","city":"北京市","district":"西城区"}
     * address_reference : {"business_area":{"id":"14312873533493635930","title":"西单","location":{"lat":39.9095,"lng":116.373001},"_distance":0,"_dir_desc":"内"},"famous_area":{"id":"14312873533493635930","title":"西单","location":{"lat":39.9095,"lng":116.373001},"_distance":0,"_dir_desc":"内"},"crossroad":{"id":"545908","title":"西单北大街/大木仓胡同(路口)","location":{"lat":39.911652,"lng":116.373871},"_distance":89.4,"_dir_desc":"西南"},"town":{"id":"110102011","title":"金融街街道","location":{"lat":39.923622,"lng":116.356262},"_distance":0,"_dir_desc":"内"},"street_number":{"id":"17414105854100477457","title":"西单北大街130号","location":{"lat":39.911301,"lng":116.374519},"_distance":3.3,"_dir_desc":""},"street":{"id":"12667939243515887255","title":"大木仓胡同","location":{"lat":39.911655,"lng":116.370163},"_distance":42.6,"_dir_desc":"南"},"landmark_l2":{"id":"15380476883213903976","title":"大悦城写字楼","location":{"lat":39.910885,"lng":116.372932},"_distance":0,"_dir_desc":"内"}}
     */

    private LocationDTO location;
    private String address;
    private FormattedAddressDTO formatted_addresses;
    private AddressComponentDTO address_component;
    private AdInfoDTO ad_info;
    private AddressReferenceDTO address_reference;
}
