package com.orion.momentservice.constants;

import lombok.Getter;

@Getter
public enum MomentConfigEnum {
    TOKEN_SECRET_KEY("goat"),

    PASSWORD_SALT("goat"),

    WX_STATE("goat"),

    WORKED_ID("1"),

    DATACENTER_ID("1"),

    IMAGE_URI("http://118.25.77.201:9000/infinited-chat/"),

    IMAGE_PATH("/home/img/avatar"),

    NOTICE_URL("/api/v1/message/push/moment"),

    MEDIA_TYPE("application/json; charset=utf-8"),

    MINIO_SERVER_URL("http://118.25.77.201:9000"),

    MINIO_ACCESS_KEY("minioadmin"),

    MINIO_SECRET_KEY("minioadmin"),

    REQUEST_SUCCESSFUL("请求成功"),

    MINIO_BUCKET_NAME("infinitec-chat");

    private final String value;

    MomentConfigEnum(String value) {
        this.value = value;
    }

}
