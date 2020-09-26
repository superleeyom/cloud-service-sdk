package com.leeyom.sdk.base;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 通用业务异常
 *
 * @author leeyom
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BizException extends BaseException {

    public BizException(IStatus status) {
        super(status);
    }

    public BizException(IStatus status, Object data) {
        super(status, data);
    }

    public BizException(Integer code, String message) {
        super(code, message);
    }

    public BizException(String message) {
        super(message);
    }

    public BizException(IStatus status, String message) {
        super(status, message);
    }

    public BizException(Integer code, String message, Object data) {
        super(code, message, data);
    }

}
