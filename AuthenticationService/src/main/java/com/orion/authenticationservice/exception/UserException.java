package com.orion.authenticationservice.exception;


import com.orion.authenticationservice.constant.user.UserErrorEnum;

/**
 * @Author: Orion
 * @CreateTime: 2025/5/21
 * @Description:
 */
public class UserException extends RuntimeException{
    private final int code;

    public UserException(String msg){
        super(msg);
        this.code = UserErrorEnum.SYSTEM_ERR.getCode();
    }

    public UserException(UserErrorEnum errorEnum){
        super(errorEnum.getMessage());
        this.code = errorEnum.getCode();
    }

    public UserException(UserErrorEnum errorEnum, String msg){
        super(msg);
        this.code = errorEnum.getCode();
    }

    public int getCode() {
        return this.code;
    }
}
