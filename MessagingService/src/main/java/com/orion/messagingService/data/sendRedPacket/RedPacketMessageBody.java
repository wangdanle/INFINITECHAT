package com.orion.messagingService.data.sendRedPacket;


import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author: Orion
 * @CreateTime: 2025/6/6
 * @Description:
 */
@Data
@Builder
@Accessors(chain = true)
public class RedPacketMessageBody {
    private String content;

    private String redPacketWrapperText;
}
