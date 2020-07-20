package com.leeyom.sdk.tencent.lbs.exception;

import com.leeyom.sdk.base.BaseException;
import com.leeyom.sdk.base.Status;

public class LbsException extends BaseException {

    public LbsException(Status status) {
        super(status);
    }

    public LbsException(Status status, Object data) {
        super(status, data);
    }

    public LbsException(Status status,  String message) {
        super(status, message);
    }

    public LbsException(String message) {
        super(message);
    }

    public LbsException(Integer code, String message) {
        super(code, message);
    }

    public LbsException(Integer code, String message, Object data) {
        super(code, message, data);
    }
    
}
