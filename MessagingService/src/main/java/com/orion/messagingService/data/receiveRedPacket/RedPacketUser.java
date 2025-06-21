package com.orion.messagingService.data.receiveRedPacket;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@Accessors(chain = true)
public class RedPacketUser {

    private String userName;

    private String avatar;

    private String receivedAt;

    private BigDecimal amount;
}
