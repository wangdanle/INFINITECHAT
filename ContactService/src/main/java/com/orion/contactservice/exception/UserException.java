package com.orion.contactservice.exception;


import com.orion.contactservice.constants.ErrorEnum;

/**
 * @Author: Orion
 * @CreateTime: 2025/5/21
 * @Description:
 */
public class UserException extends RuntimeException{
    private final int code;

    public UserException(String msg){
        super(msg);
        this.code = ErrorEnum.SYSTEM_ERROR.getCode();
    }

    public UserException(ErrorEnum errorEnum){
        super(errorEnum.getMessage());
        this.code = errorEnum.getCode();
    }

    public UserException(ErrorEnum errorEnum, String msg){
        super(msg);
        this.code = errorEnum.getCode();
    }

    public int getCode() {
        return this.code;
    }
}
