package com.orion.messagingService.constants;


/**
 * @Author: Orion
 * @CreateTime: 2025/6/3
 * @Description:
 */
public enum SessionType {
    SINGLE(1),
    GROUP(2);

    private int value;

    SessionType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
