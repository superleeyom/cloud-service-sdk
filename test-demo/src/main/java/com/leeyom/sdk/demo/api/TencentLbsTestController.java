package com.leeyom.sdk.demo.api;

import com.leeyom.sdk.base.ApiResponse;
import com.leeyom.sdk.tencent.lbs.dto.DistanceMatrixDTO;
import com.leeyom.sdk.tencent.lbs.dto.IpLocationDTO;
import com.leeyom.sdk.tencent.lbs.dto.LocationDTO;
import com.leeyom.sdk.tencent.lbs.dto.TranslateRequestDTO;
import com.leeyom.sdk.tencent.lbs.util.TencentLbsUtil;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 腾讯地图 webservice api 接口 demo
 *
 * @author leeyom
 */
@RestController
@RequestMapping("tencentLbs")
public class TencentLbsTestController {

    /**
     * 地理坐标转具体的地址信息
     *
     * @param location 地理坐标，比如：39.984154,116.307490
     * @return 具体的地址信息
     */
    @GetMapping("locationToAddress")
    public ApiResponse<String> locationToAddress(String location) {
        return ApiResponse.ofSuccess(TencentLbsUtil.locationToAddress(location));
    }

    /**
     * 具体的地址信息转经纬度坐标
     *
     * @param address 详细地址，比如：北京市海淀区彩和坊路海淀西大街74号
     * @return 经纬度
     */
    @GetMapping("addressToLocation")
    public ApiResponse<LocationDTO> addressToLocation(String address) {
        return ApiResponse.ofSuccess(TencentLbsUtil.addressToLocation(address));
    }

    /**
     * 用于单起点到多终点，或多起点到单终点的路线距离（非直线距离）计算；
     * 起点到终点最大限制直线距离10公里，一般用于O2O上门服务
     *
     * @param mode 计算方式：driving（驾车）、walking（步行）
     * @param from 起点坐标，例如：39.071510,117.190091
     * @param to   终点坐标，经度与纬度用英文逗号分隔，坐标间用英文分号分隔
     * @return 起点到终点的距离，单位：km
     */
    @GetMapping("distance")
    public ApiResponse<Double> distance(String mode, String from, String to) {
        return ApiResponse.ofSuccess(TencentLbsUtil.distance(mode, from, to));
    }

    /**
     * 距离矩阵，用于批量计算一组起终点的路面距离（或称导航距离），可应用于网约车派单、多目的地最优路径智能计算等场景中，
     * 支持驾车、步行、骑行多种交通方式，满足不同应用需要
     *
     * @param mode 计算方式，取值：driving：驾车、walking：步行、bicycling：自行车
     * @param from 起点坐标，lat,lng[,heading];lat,lng[,heading]，经度与纬度用英文逗号分隔，坐标间用英文分号分隔，heading为车头方向
     * @param to   终点坐标，lat,lng;lat,lng，经度与纬度用英文逗号分隔，坐标间用英文分号分隔
     * @return 多点到多点距离计算，结果为二维数组，rows为行，elements为列 结果数组（行）
     */
    @GetMapping("distanceMatrix")
    public ApiResponse<DistanceMatrixDTO> distanceMatrix(String mode, String from, String to) {
        return ApiResponse.ofSuccess(TencentLbsUtil.distanceMatrix(mode, from, to));
    }

    /**
     * 通过终端设备IP地址获取其当前所在地理位置，精确到市级，
     * 常用于显示当地城市天气预报、初始化用户城市等非精确定位场景。
     *
     * @param ip 比如：61.135.17.68
     * @return 当前ip的地理位置信息
     */
    @GetMapping("ipLocation")
    public ApiResponse<IpLocationDTO> ipLocation(String ip) {
        return ApiResponse.ofSuccess(TencentLbsUtil.ipLocation(ip));
    }


    /**
     * 实现从其它地图供应商坐标系或标准GPS坐标系，批量转换到腾讯地图坐标系
     *
     * @param translateRequestDTO 请求参数
     * @return 坐标转换结果，转换后的坐标顺序与输入顺序一致
     */
    @PostMapping("translate")
    public ApiResponse<List<LocationDTO>> translate(@RequestBody TranslateRequestDTO translateRequestDTO) {
        return ApiResponse.ofSuccess(TencentLbsUtil.translate(translateRequestDTO));
    }

}

