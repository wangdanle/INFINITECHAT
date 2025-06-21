package com.orion.messagingService.data.sendRedPacket;


import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author: Orion
 * @CreateTime: 2025/6/5
 * @Description:
 */
@Data
@Accessors(chain = true)
public class SendRedPacketResponse {
    private String sessionId;

    private Integer sessionType;

    private Integer type;

    private Long messageId;

    private String createAt;

    private Object body;
}
