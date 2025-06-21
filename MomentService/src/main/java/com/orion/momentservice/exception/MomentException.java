package com.orion.momentservice.exception;

import com.orion.momentservice.constants.ErrorEnum;
import lombok.Getter;

@Getter
public class MomentException extends RuntimeException {
    private final int code;

    public MomentException(String msg) {
        super(msg);
        this.code = ErrorEnum.DATABASE_ERR.getCode();
    }

    public MomentException(ErrorEnum errorEnum) {
        super(errorEnum.getMessage());
        this.code = errorEnum.getCode();
    }

    public MomentException(ErrorEnum errorEnum, String msg) {
        super(msg);
        this.code = errorEnum.getCode();
    }

    public MomentException(ErrorEnum errorEnum, String requestBody, Throwable cause) {
        super(requestBody, cause);
        this.code = errorEnum.getCode();
    }

}
