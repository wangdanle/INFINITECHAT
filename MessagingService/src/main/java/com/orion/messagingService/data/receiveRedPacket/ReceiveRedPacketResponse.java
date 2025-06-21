package com.orion.messagingService.data.receiveRedPacket;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * @Author: Orion
 * @CreateTime: 2025/6/6
 * @Description:
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
public class ReceiveRedPacketResponse {
    private BigDecimal receivedAmount;

    private Integer status;
}
