package com.orion.contactservice.exception;

import com.orion.contactservice.constants.ErrorEnum;
import lombok.Getter;

@Getter
public class MessageSendFailureException extends RuntimeException {
    private final int code;

    public MessageSendFailureException(String msg){
        super(msg);
        this.code = ErrorEnum.DATABASE_ERR.getCode();
    }

    public MessageSendFailureException(ErrorEnum errorEnum){
        super(errorEnum.getMessage());
        this.code = errorEnum.getCode();
    }

    public MessageSendFailureException(ErrorEnum errorEnum, String msg){
        super(msg);
        this.code = errorEnum.getCode();
    }

    public MessageSendFailureException(ErrorEnum errorEnum, String requestBody, Throwable cause){
        super(requestBody, cause);
        this.code = errorEnum.getCode();
    }

}
