package com.leeyom.sdk.base;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 异常基类
 *
 * @author leeyom
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BaseException extends RuntimeException {

    /**
     * 状态码
     */
    private Integer code;

    /**
     * 提示信息
     */
    private String message;

    /**
     * 返回数据
     */
    private Object data;

    public BaseException() {
    }

    public BaseException(IStatus status) {
        super(status.getMessage());
        this.code = status.getCode();
        this.message = status.getMessage();
    }

    public BaseException(IStatus status, Object data) {
        this(status);
        this.data = data;
    }

    public BaseException(IStatus status, String message) {
        this(status.getCode(), message);
    }

    public BaseException(String message) {
        super(message);
        this.code = Status.ERROR.getCode();
        this.message = message;
    }

    public BaseException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public BaseException(Integer code, String message, Object data) {
        this(code, message);
        this.data = data;
    }
}