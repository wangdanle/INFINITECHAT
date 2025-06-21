package com.orion.authenticationservice.constant.config;

import lombok.Getter;

@Getter
public enum ConfigEnum {
    TOKEN_SECRET_KEY("tokenSecretKey", "orion");

    private final String value;
    private final String text;

    ConfigEnum(String value, String text) {
        this.value = value;
        this.text = text;
    }

}
