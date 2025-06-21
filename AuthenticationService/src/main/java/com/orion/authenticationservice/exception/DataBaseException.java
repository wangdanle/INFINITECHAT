package com.orion.authenticationservice.exception;


import com.orion.authenticationservice.constant.user.UserErrorEnum;

/**
 * @Author: Orion
 * @CreateTime: 2025/5/21
 * @Description:
 */
public class DataBaseException extends RuntimeException{
    private final int code;

    public DataBaseException(String msg){
        super(msg);
        this.code = UserErrorEnum.DATABASE_ERR.getCode();
    }

    public DataBaseException(UserErrorEnum errorEnum){
        super(errorEnum.getMessage());
        this.code = errorEnum.getCode();
    }

    public DataBaseException(UserErrorEnum errorEnum, String msg){
        super(msg);
        this.code = errorEnum.getCode();
    }

    public int getCode() {
        return this.code;
    }
}
