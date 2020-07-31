package com.leeyom.sdk.tencent.lbs.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.leeyom.sdk.base.BizException;
import com.leeyom.sdk.tencent.lbs.config.WebServiceApiConst;
import com.leeyom.sdk.tencent.lbs.config.WebServiceApiProperties;
import com.leeyom.sdk.tencent.lbs.config.WebServiceUri;
import com.leeyom.sdk.tencent.lbs.dto.*;
import com.leeyom.sdk.tencent.lbs.exception.LbsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 腾讯地图api接口
 *
 * @author leeyom
 */
@Component
public class TencentLbsUtil {


    private static WebServiceApiProperties webServiceApiProperties;
    private static final int SUCCESS = 0;

    @Autowired
    public void setWebServiceApiProperties(WebServiceApiProperties webServiceApiProperties) {
        TencentLbsUtil.webServiceApiProperties = webServiceApiProperties;
    }

    /**
     * 地理坐标转具体的地址信息
     *
     * @param location 地理坐标，比如：39.984154,116.307490
     * @return 具体的地址信息
     */
    public static String locationToAddress(String location) {
        if (StrUtil.isBlank(location)) {
            throw new BizException("地理坐标不能为空");
        }
        Map<String, Object> param = CollUtil.newHashMap(3);
        param.put(WebServiceApiConst.LOCATION, location);
        TencentLbsResponseDTO responseDTO = requestToTencentLbs(param, WebServiceUri.GEOCODER_API);
        TencentMapAddressDTO tencentMapAddressDTO = BeanUtil.toBean(responseDTO.getResult(), TencentMapAddressDTO.class);
        if (tencentMapAddressDTO == null) {
            throw new LbsException(responseDTO.getMessage());
        }
        return tencentMapAddressDTO.getAddress();
    }

    /**
     * 具体的地址信息转经纬度坐标
     *
     * @param address 详细地址，比如：北京市海淀区彩和坊路海淀西大街74号
     * @return 经纬度
     */
    public static LocationDTO addressToLocation(String address) {
        if (StrUtil.isBlank(address)) {
            throw new BizException("详细地址不能为空");
        }
        Map<String, Object> param = CollUtil.newHashMap(3);
        param.put(WebServiceApiConst.ADDRESS, address);
        TencentLbsResponseDTO responseDTO = requestToTencentLbs(param, WebServiceUri.GEOCODER_API);
        TencentMapLocationDTO tencentMapLocationDTO = BeanUtil.toBean(responseDTO.getResult(), TencentMapLocationDTO.class);
        if (tencentMapLocationDTO == null) {
            throw new LbsException(responseDTO.getMessage());
        }
        return tencentMapLocationDTO.getLocation();
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
    public static double distance(String mode, String from, String to) {
        validateParam(mode, from, to);
        Map<String, Object> param = CollUtil.newHashMap(5);
        param.put(WebServiceApiConst.MODE, mode);
        param.put(WebServiceApiConst.FROM, from);
        param.put(WebServiceApiConst.TO, to);
        TencentLbsResponseDTO responseDTO = requestToTencentLbs(param, WebServiceUri.DISTANCE_API);
        if (responseDTO.getStatus() != SUCCESS) {
            throw new LbsException(responseDTO.getMessage());
        }
        TencentMapDistanceDTO tencentMapDistanceDTO = BeanUtil.toBean(responseDTO.getResult(), TencentMapDistanceDTO.class);
        List<DistanceDTO> elements = tencentMapDistanceDTO.getElements();
        if (CollUtil.isNotEmpty(elements)) {
            DistanceDTO distanceDTO = elements.get(0);
            // 转km，保留两位小数
            double distance = distanceDTO.getDistance() / 1000;
            return NumberUtil.round(distance, 2).doubleValue();
        }
        return 0.00d;
    }

    private static void validateParam(String mode, String from, String to) {
        if (StrUtil.isBlank(mode)) {
            throw new BizException("计算方式不能为空");
        }

        if (StrUtil.isBlank(from)) {
            throw new BizException("起点坐标不能为空");
        }

        if (StrUtil.isBlank(to)) {
            throw new BizException("终点坐标不能为空");
        }
    }

    /**
     * 通过终端设备IP地址获取其当前所在地理位置，精确到市级，
     * 常用于显示当地城市天气预报、初始化用户城市等非精确定位场景。
     *
     * @param ip 比如：61.135.17.68
     * @return 当前ip的地理位置信息
     */
    public static IpLocationDTO ipLocation(String ip) {
        if (StrUtil.isBlank(ip)) {
            throw new BizException("ip不能为空");
        }
        Map<String, Object> param = CollUtil.newHashMap(3);
        param.put(WebServiceApiConst.IP, ip);
        TencentLbsResponseDTO responseDTO = requestToTencentLbs(param, WebServiceUri.IP_API);
        if (responseDTO.getStatus() != SUCCESS) {
            throw new LbsException(responseDTO.getMessage());
        }
        return BeanUtil.toBean(responseDTO.getResult(), IpLocationDTO.class);
    }

    /**
     * 实现从其它地图供应商坐标系或标准GPS坐标系，批量转换到腾讯地图坐标系
     *
     * @param translateRequestDTO 请求参数
     * @return 坐标转换结果，转换后的坐标顺序与输入顺序一致
     */
    public static List<LocationDTO> translate(TranslateRequestDTO translateRequestDTO) {
        if (StrUtil.isBlank(translateRequestDTO.getLocations())) {
            throw new BizException("预转换的坐标不能为空");
        }
        Map<String, Object> param = BeanUtil.beanToMap(translateRequestDTO);
        String responseJson = getResponseJson(param, WebServiceUri.TRANSLATE);
        TencentLbsTranslateResponseDTO tencentLbsTranslateResponseDTO = JSONUtil.toBean(responseJson, TencentLbsTranslateResponseDTO.class);
        if (tencentLbsTranslateResponseDTO.getStatus() != SUCCESS) {
            throw new LbsException(tencentLbsTranslateResponseDTO.getMessage());
        }
        return CollUtil.emptyIfNull(tencentLbsTranslateResponseDTO.getLocations());
    }

    private static TencentLbsResponseDTO requestToTencentLbs(Map<String, Object> param, String uri) {
        String responseJson = getResponseJson(param, uri);
        return JSONUtil.toBean(responseJson, TencentLbsResponseDTO.class);
    }

    private static String getResponseJson(Map<String, Object> param, String uri) {
        // 签名
        String sign = WebServiceApiSignUtil.signGetRequest(uri, webServiceApiProperties.getAppSecret(),
                webServiceApiProperties.getAppKey(), param);
        param.put(WebServiceApiConst.SIG, sign);
        String url = webServiceApiProperties.getDomain() + uri;
        return HttpUtil.get(url, param);
    }


}
