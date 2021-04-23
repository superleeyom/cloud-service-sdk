




## 简介

第三方云服务的sdk封装，包括有阿里云短信服务，阿里云oss对象存储，腾讯地图WebService API，腾讯云cos对象存储等服务，这些服务虽然都有专门提供sdk，但是现实接入的话，还是需要我们二次进行封装，比如签名、参数封装等，引入二次封装后的sdk，可以避免你再次踩坑，直接使用。

## 目前已支持功能

- 阿里云短信服务：
    - [x] [单条发送短信](https://help.aliyun.com/document_detail/55284.html?spm=a2c4g.11174283.6.667.8d482c42J8LHtu)
    - [x] [批量发送短信](https://help.aliyun.com/document_detail/66041.html?spm=a2c4g.11186623.6.670.63d25777Qq5QSs)
- 阿里云对象存储：
    - [x] [流式上传文件（后台上传）](https://help.aliyun.com/document_detail/84781.html?spm=a2c4g.11174283.6.823.3f117da2HMePRX)
    - [x] [获取前端直传策略数据（前端直传）](https://help.aliyun.com/document_detail/91868.html?spm=a2c4g.11186623.2.15.16076e28vYpsMF#concept-ahk-rfz-2fb)
    - [x] [断点续传上传](https://help.aliyun.com/document_detail/84785.html?spm=a2c4g.11186623.6.826.14b41df2yWKbLx)
    - [x] [断点续传下载](https://help.aliyun.com/document_detail/84827.html?spm=a2c4g.11186623.6.835.aa1d50a6nWqdGx)
- 腾讯云对象存储：
    - [x] [预签名URL方式上传-PUT方式（前端直传）](https://cloud.tencent.com/document/product/436/35217)
    - [x] [预签名URL方式上传-POST方式（前端直传）](https://cloud.tencent.com/document/product/436/14690)
    - [x] [流式上传（后台上传）](https://cloud.tencent.com/document/product/436/35215#.E7.AE.80.E5.8D.95.E4.B8.8A.E4.BC.A0.E5.AF.B9.E8.B1.A1)
- 腾讯地图：
    - [x] [地理坐标转具体的地址信息](https://lbs.qq.com/service/webService/webServiceGuide/webServiceGcoder)
    - [x] [具体的地址信息转经纬度坐标](https://lbs.qq.com/service/webService/webServiceGuide/webServiceGeocoder)
    - [x] [~~用于单起点到多终点，或多起点到单终点的路线距离（非直线距离）计算~~](https://lbs.qq.com/service/webService/webServiceGuide/webServiceDistance)
    - [x] [通过终端设备IP地址获取其当前所在地理位置](https://lbs.qq.com/service/webService/webServiceGuide/webServiceIp)
    - [x] [坐标转换](https://lbs.qq.com/service/webService/webServiceGuide/webServiceTranslate)
    - [x] [距离矩阵计算](https://lbs.qq.com/service/webService/webServiceGuide/webServiceMatrix)
    
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

## 注意事项

### 阿里云OSS前端直传

这里需要注意下，实际前端直传的时候，请求的 content-type 为 form-data，请求方式为 post，前端先调用后台接口：`http://localhost:8080/demo/aliyunOss/getOssPolicy` （先启动示例项目：`test-demo`），获取直传策略参数，返回的示例：
```json5
{
  "code": 200,
  "message": "操作成功！",
  "data": {
    "aliyunOssPolicy": {
      // Bucket拥有者的AccessKey Id     
      "ossAccessKeyId": "xxx",
      // Policy规定了请求表单域的合法性
      "policy": "xxx",
      // 根据AccessKeySecret和Policy计算的签名信息      
      "signature": "xxx",
      // 前端直传的请求链接
      "host": "https://xxx.oss-cn-beijing.aliyuncs.com",
      // 上传Object的名称。如果名称包含路径，例如destfolder/example.jpg
      "key": "xxx/xxx.jpg"
    },
    // 前端上传完后，文件实际的访问地址，把这个地址告诉给后台
    "fileUrl": "http://xxx.oss-cn-beijing.aliyuncs.com/xxx/xxx.jpg"
  }
}
```
拿到直传参数后，前端正式向阿里云发起上传请求（host字段指定的域名），这里 postman 示例：
![](http://image.leeyom.top/blog/20210423170409.png)
注意：**file必须是表单中的最后一个域！**，具体的可以[参考官方的文档](https://help.aliyun.com/document_detail/31988.html?spm=a2c4g.11186623.6.1497.59c06611TqgjXe)

### 腾讯云COS前端直传

#### put方式

请求后台接口：`http://localhost:8080/demo/tencentCos/getPreSignedUrlForPut` （先启动示例项目：`test-demo`）拿到预签名地址 `preSignUrl`：
```json5
{
  "code": 200,
  "message": "操作成功！",
  "data": {
    // 前端直传的请求链接
    "preSignUrl": "https://retail-cos.aqara.cn/retail_image_fat/e5809dfe63b94ebba89fb20749d74f62.jpg?xxx",
    // 前端上传完后，文件实际的访问地址，把这个地址告诉给后台
    "fileUrl": "https://xxx/retail_image_fat/e5809dfe63b94ebba89fb20749d74f62.jpg"
  }
}
```
前端拿到预签名地址后，发起 put 上传请求，将文件域放到`binary`里面，postman示例如下：
![](http://image.leeyom.top/blog/20210423174115.png)
然后将 `fileUrl` 回传给后台即可！

#### post方式

post方式其实跟阿里云OSS处理方式一样，后台返回直传策略参数，然后前端 post直接发起上传请求，请求后台接口：`http://localhost:8080/demo/tencentCos/getPreSignedUrlForPost `，返回策略参数：
```json5
{
  "code": 200,
  "message": "操作成功！",
  "data": {
    "formFields": {
      "q-sign-algorithm": "sha1",
      "q-ak": "xx",
      "q-signature": "xx",
      "q-key-time": "xx",
      "key": "xxx",
      "policy": "xxx"
    },
    "preSignUrl": "https://xxx.cn",
    "fileUrl": "https:/xxx.cn/retail_image_fat/7dca24d58ac24e8894a6b65c91df8042.jpg"
  }
}
```
`formFields`里面的数据就是直传策略参数，拿到直传策略参数后，前端再次发起post请求，请求的 content-type 为 form-data，postman 示例如下：
![](http://image.leeyom.top/blog/20210423175344.png)