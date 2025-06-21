package com.orion.momentservice.constants;

import lombok.Getter;

@Getter
public enum ErrorEnum {
    SUCCESS(200, "ok"),
    NO_USER_ERROR(40004, "用户不存在"),
    DELETE_MOMENT_FAILED_MSG(40005, "删除失败,朋友圈不存在"),
    SYSTEM_ERROR(50000, "系统内部异常"),
    SERVICE_UNAVAILABLE(50001, "实时通信服务不可用"),
    MESSAGE_SEND_FAILURE(50002, "消息发送失败"),
    MOMENT_DATABASE_ERROR(50003, "保存朋友圈失败"),
    DATABASE_ERR(50004, "数据库操作异常"),
    UPDATE_AVATAR_ERROR(50011, "更新头像失败"),
    ERROR_UPLOAD_FAILED(50012, "上传图片失失败");

    private int code;
    private String message;

    ErrorEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
