package com.orion.momentservice.exception;


import com.orion.momentservice.constants.ErrorEnum;

/**
 * @Author: Orion
 * @CreateTime: 2025/5/21
 * @Description:
 */
public class DataBaseException extends RuntimeException{
    private final int code;

    public DataBaseException(String msg){
        super(msg);
        this.code = ErrorEnum.DATABASE_ERR.getCode();
    }

    public DataBaseException(ErrorEnum errorEnum){
        super(errorEnum.getMessage());
        this.code = errorEnum.getCode();
    }

    public DataBaseException(ErrorEnum errorEnum, String msg){
        super(msg);
        this.code = errorEnum.getCode();
    }

    public int getCode() {
        return this.code;
    }
}
