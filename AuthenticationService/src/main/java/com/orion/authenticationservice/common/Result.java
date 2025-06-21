package com.orion.authenticationservice.common;


import jodd.net.HttpStatus;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author: Orion
 * @CreateTime: 2025/5/20
 * @Description:
 */
@Data
@Accessors(chain = true)
public class Result<T> {
    private int code;
    private String message;
    private T data;

    public static <T> Result<T> OK(T data){
        Result<T> r = new Result<>();

        return r.setCode(HttpStatus.ok().status()).setData(data);
    }

    /**
     * 数据库异常
     * @param msg
     * @return
     * @param <T>
     */
    public static <T> Result databaseError(String msg){
        Result<T> r = new Result<>();

        return r.setCode(HttpStatus.error500().status()).setMessage(msg);
    }

    /**
     * 数据库异常
     * @param msg
     * @return
     * @param <T>
     */
    public static <T> Result databaseError(int code, String msg){
        Result<T> r = new Result<>();

        return r.setCode(code).setMessage(msg);
    }

    /**
     * 服务器异常
     * @param msg
     * @return
     * @param <T>
     */
    public static <T> Result serverError(String msg){
        Result<T> r = new Result<>();

        return r.setCode(HttpStatus.error500().status()).setMessage(msg);
    }

    /**
     * 用户信息异常
     * @param code
     * @param msg
     * @return
     * @param <T>
     */
    public static <T> Result userError(int code, String msg){
        Result<T> r = new Result<>();

        return r.setCode(code).setMessage(msg);
    }

    /**
     * 校验异常
     * @param msg
     * @return
     * @param <T>
     */
    public static <T> Result validError(String msg){
        Result<T> r = new Result<>();

        return r.setCode(HttpStatus.error400().status()).setMessage(msg);
    }
}
