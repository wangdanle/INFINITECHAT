package com.orion.messagingService.data.receiveRedPacket;


import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author: Orion
 * @CreateTime: 2025/6/6
 * @Description:
 */
@Data
@Accessors(chain = true)
public class ReceiveRedPacketRequest {
    private Long userId;

    private Long redPacketId;
}
