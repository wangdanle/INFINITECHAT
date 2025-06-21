package com.orion.authenticationservice.constant.user;

import lombok.Getter;

@Getter
public enum UserErrorEnum {
    SUCCESS(200, "OK"),
    REGISTER_ERR(40001, "注册失败,用户已存在"),
    CODE_ERR(40002, "验证码错误"),
    USER_ERR(40003, "账户不存在"),
    ILLEGAL_REQUEST_ERR(40031, "非法的请求来源"),
    SYSTEM_ERR(50001, "服务器出现异常"),
    DATABASE_ERR(50002, "数据库存储异常"),
    UPTADE_AVATAR_ERR(50003, "更新头像失败");

    private final int code;
    private final String message;

    UserErrorEnum(int code,  String message){
        this.code = code;
        this.message = message;
    }
}
