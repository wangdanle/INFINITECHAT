package com.orion.messagingService.data.sendRedPacket;


import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * @Author: Orion
 * @CreateTime: 2025/6/5
 * @Description:
 */
@Data
@Accessors(chain = true)
public class SendRedPacketRequest {
    private Long sessionId;

    private Long receiverUserId;

    private Long sendUserId;

    private Integer type;

    private Integer sessionType;

    private Body body;

    @Data
    @Accessors(chain = true)
    public static class Body {
        private Integer redPacketType;

        private BigDecimal totalAmount;

        private Integer totalCount;

        private String redPacketWrapperText;
    }
}
