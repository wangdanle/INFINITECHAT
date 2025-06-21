package com.orion.messagingService.constants;

import lombok.Getter;

@Getter
public enum RedPacketStatus {
    UNCLAIMED(1, "未领取完"),

    CLAIMED(2, "已领取完"),

    EXPIRED(3, "已过期");

    private Integer status;

    private String description;

    RedPacketStatus(Integer status, String description) {
        this.status = status;
        this.description = description;
    }
}
