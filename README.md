




## 简介

第三方云服务的sdk封装，包括有阿里云短信服务，阿里云oss对象存储，腾讯地图WebService API，腾讯云cos对象存储等服务，这些服务虽然都有专门提供sdk，但是现实接入的话，还是需要我们二次进行封装，比如签名、参数封装等，引入二次封装后的sdk，可以避免你再次踩坑，直接使用。

## 目前已支持功能

- 阿里云短信服务：
    - [x] 单条发送短信
    - [x] 批量发送短信
- 阿里云对象存储：
    - [x] 流式上传文件（后台上传）
    - [x] 获取前端直传策略数据（前端直传）
    - [x] 断点续传上传
    - [x] 断点续传下载
- 腾讯云对象存储：
    - [x] 预签名URL方式上传-PUT方式（前端直传）
    - [x] 预签名URL方式上传-POST方式（前端直传）
    - [x] 流式上传（后台上传）
- 腾讯地图：
    - [x] 地理坐标转具体的地址信息
    - [x] 具体的地址信息转经纬度坐标
    - [x] 用于单起点到多终点，或多起点到单终点的路线距离（非直线距离）计算
    - [x] 通过终端设备IP地址获取其当前所在地理位置
    - [x] 坐标转换
    
## 模块介绍

- `tencent-lbs-sdk`：腾讯地图 webServiceAPI 接口封装
- `tencent-cos-sdk`：腾讯云对象存储封装
- `aliyun-oss-sdk`：阿里云对象存储封装
- `aliyun-sms-sdk`：阿里云短信封装
- `sdk-base`：基础依赖包
- `test-demo`：示例 demo

## 核心类与配置

- 配置类：这些配置类里面的属性，在对应的云服务商控制台都能找到
    - `aliyun-oss.properties`
    - `aliyun-sms.properties`
    - `tencent-cos.properties`
    - `tencent-lbs.properties`
- 工具类：
    - `AliOssUtil`：阿里云对象存储相关
    - `AliSmsUtil`：阿里短信服务相关
    - `TencentCosUtil`：腾讯对象存储相关
    - `TencentLbsUtil`：腾讯地图 webServiceAPI 接口相关

## 使用环境

- JDK 1.8+
- Spring Boot 2.x
    
## 如何使用

修改各个sdk classpath 下的 `xxx.properties` 文件，填写对应的配置，然后进入本项目根目录，执行 `mvn clean install` 命令，安装完毕后，在你的项目中引入如下依赖即可，这里我以引入腾讯地图为例：

```xml
<!-- 腾讯地图webServiceAPI sdk -->
<dependency>
    <groupId>com.leeyom</groupId>
    <artifactId>tencent-lbs-sdk</artifactId>
    <version>${tencetn-lbs-sdk.version}</version>
</dependency>
```

然后直接使用 `TencentLbsUtil` 工具类，即可调用对应的接口，不需关心参数签名等细节：

```java
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
```
更多具体示例，可以参考示例项目：`test-demo`