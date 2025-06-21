package com.orion.messagingService.constants;


import lombok.Getter;

/**
 * @Author: Orion
 * @CreateTime: 2025/6/5
 * @Description:
 */
@Getter
public enum BalanceLogType {
    SEND_RED_PACKET(1, "发送红包"),

    RECEIVE_RED_PACKET(2, "接受红包"),

    REFUND_RED_PACKET(3, "红包退回")
    ;

    private final int type;

    private final String description;

    BalanceLogType(int type, String description) {
        this.type = type;
        this.description = description;
    }
}
