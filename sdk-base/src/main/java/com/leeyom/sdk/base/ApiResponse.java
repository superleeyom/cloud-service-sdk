package com.leeyom.sdk.base;

import lombok.Data;

import java.io.Serializable;

/**
 * 通用的 API 接口封装
 *
 * @author leeyom
 */
@Data
public class ApiResponse<T> implements Serializable {
    private static final long serialVersionUID = 8993485788201922830L;

    /**
     * 状态码
     */
    private Integer code;

    /**
     * 返回内容
     */
    private String message;

    /**
     * 返回数据
     */
    private T data;

    /**
     * 无参构造函数
     */
    private ApiResponse() {

    }

    /**
     * 全参构造函数
     *
     * @param code    状态码
     * @param message 返回内容
     * @param data    返回数据
     */
    private ApiResponse(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 构造一个自定义的API返回
     *
     * @param code    状态码
     * @param message 返回内容
     * @param data    返回数据
     * @return ApiResponse
     */
    public static <T> ApiResponse<T> of(Integer code, String message, T data) {
        return new ApiResponse<>(code, message, data);
    }

    /**
     * 构造一个成功且不带数据的API返回
     *
     * @return ApiResponse
     */
    public static <T> ApiResponse<T> ofSuccess() {
        return ofSuccess(null);
    }

    /**
     * 构造一个成功且带数据的API返回
     *
     * @param data 返回数据
     * @return ApiResponse
     */
    public static <T> ApiResponse<T> ofSuccess(T data) {
        return ofStatus(Status.SUCCESS, data);
    }

    /**
     * 构造一个成功且带数据的并自定义消息的API返回
     *
     * @param data    返回数据
     * @param message 自定义消息
     * @return ApiResponse
     */
    public static <T> ApiResponse<T> ofSuccess(T data, String message) {
        return ofStatus(Status.SUCCESS, data, message);
    }

    /**
     * 构造一个失败且带自定义消息的API返回
     *
     * @param message 自定义消息
     * @return ApiResponse
     */
    public static <T> ApiResponse<T> ofFail(String message) {
        return ofStatus(Status.ERROR, message);
    }

    /**
     * 构造一个成功且自定义消息的API返回
     *
     * @param message 返回内容
     * @return ApiResponse
     */
    public static <T> ApiResponse<T> ofMessage(String message) {
        return of(Status.SUCCESS.getCode(), message, null);
    }

    /**
     * 构造一个有状态的API返回
     *
     * @param status 状态 {@link Status}
     * @return ApiResponse
     */
    public static <T> ApiResponse<T> ofStatus(Status status) {
        return ofStatus(status, null);
    }

    /**
     * 构造一个有状态且带数据的API返回
     *
     * @param status 状态 {@link IStatus}
     * @param data   返回数据
     * @return ApiResponse
     */
    public static <T> ApiResponse<T> ofStatus(IStatus status, T data) {
        return of(status.getCode(), status.getMessage(), data);
    }

    /**
     * 构造一个有状态且带数据并自定义消息的API返回
     *
     * @param status  状态 {@link IStatus}
     * @param data    返回数据
     * @param message 自定义消息
     * @return ApiResponse
     */
    public static <T> ApiResponse<T> ofStatus(IStatus status, T data, String message) {
        return of(status.getCode(), message, data);
    }

    /**
     * 构造一个有状态且带自定义消息的API返回
     *
     * @param status  状态 {@link IStatus}
     * @param message 自定义消息
     * @return ApiResponse
     */
    public static <T> ApiResponse<T> ofStatus(IStatus status, String message) {
        return of(status.getCode(), message, null);
    }

    /**
     * 构造一个异常的API返回
     *
     * @param e   异常
     * @param <E> {@link BaseException} 的子类
     * @return ApiResponse
     */
    public static <E extends BaseException> ApiResponse ofException(E e) {
        return of(e.getCode(), e.getMessage(), e.getData());
    }
}
