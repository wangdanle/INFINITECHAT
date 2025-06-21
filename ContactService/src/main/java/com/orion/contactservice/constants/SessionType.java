package com.orion.contactservice.constants;

public enum SessionType {
    // 单聊
    SINGLE(1),

    // 群聊
    GROUP(2),

    // 群聊正常状态
    STATUS_NORMAL(1),
    // 群聊删除状态
    STATUS_FIALED(2);

    private int value;

    SessionType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
